/* *****************************************************************************
 * Copyright (C) 2025, Ernie R Rael. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * ****************************************************************************/
package com.nqadmin.swingset.navigate;


import java.lang.System.Logger;
import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.EventObject;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.Action;
import javax.swing.SpinnerNumberModel;

import org.openide.util.WeakListeners;

import com.google.common.eventbus.EventBus;
import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.navigate.NavigationRowSetEvent.OperatorKind;
import com.nqadmin.swingset.navigate.NavigationRowSetEvent.RowSetEventType;
import com.nqadmin.swingset.utils.SSUtils;
import com.raelity.lib.eventbus.WeakEventBus;

import static com.nqadmin.swingset.navigate.NavAction.*;
import static com.nqadmin.swingset.navigate.RowSetState.isAcceptingChanges;
import static com.nqadmin.swingset.navigate.Utils.getGlobalEventBus;
import static com.nqadmin.swingset.utils.SSUtils.isJunit;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * The NavigationModel is associated with a {@link javax.sql.RowSet}.
 * Using {@link NavigateActions} and {@link NavAction} it encapsulates RowSet
 * traversal and their associated {@link javax.swing.Action}s which can be
 * plugged into UI components; it broadcasts RowSet events.
 * <p>
 * {@link NavigationEvent}s are broadcast when the model's RowSet is changed
 * and when the current RowSet notifies of events. RowSet events are coalesced
 * when they occur in operations which are delineated by
 * <ul>
 * <li> {@link #startNavigationEvent }
 * <li> {@link #finishNavigationEvent }
 * </ul>
 * Other/stray RowSet events are sent individually as soon as they are received.
 */
// TODO: see test's NavigateHook
public class NavigationModel
{
	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	private NavigateActions navActs;

	// For use ONLY with dummy
	private NavigationModel() {
		rowSetListener = null;
	}

	/** simplify switches back/forth */
	public static boolean ENABLED = false;

	/**
	 * Create one associated with the given RowSet.
	 * @param rs
	 */
	public NavigationModel(RowSet rs) {
		rowSetListener = new MyRowSetListener();
		this.navActs = NavigateActions.get(rs);
		rowSetListener.registerTo(rs);
	}

	private void check() {
		if (navActs == null)
			throw new IllegalStateException("No RowSet");
	}

	/**
	 * Change the RowSet associated with this model.
	 * @param rs new RowSet for this model
	 */
	public void setRowSet(RowSet rs) {
		rowSetListener.unregisterFrom(rs);
		this.navActs = NavigateActions.get(rs);
		rowSetListener.registerTo(rs);
		post(new NavigationModelNewRowSetEvent(this));
	}

	/**
	 * Return the associated RowSet.
	 * @return row set
	 */
	public RowSet getRowSet() {
		check();
		return navActs.getRowSet();
	}

	/**
	 * Return an action, associated with this model, that can be plugged
	 * into a JComponent's {@link javax.swing.Action}.
	 * @param navAction
	 * @return 
	 */
	public Action getAction(NavAction navAction) {
		check();
		return navActs.get(navAction);
	}

	// public NavigateActions getNavActs() {
	// 	return navActs;
	// }

	/**
	 * Return the associated RowSet's current row number.
	 * @return row number
	 */
	public int getRow() {
		check();
		int spin_row = getSpinModelAct().model.getNumber().intValue();
		if (Boolean.TRUE) {
			int rs_row = -1;
			try {
				rs_row = navActs.getRowSet().getRow();
			} catch (SQLException ex) {
			}
			if (spin_row != rs_row) {
				logger.log(ERROR, sf("spinner model out of sync with row set"));
			}
		}
		return spin_row;
	}
	
	/** Move the ResultSet cursor to the first row. */
	public void first() { check(); navActs.run(NAV_FIRST); }
	/** Move the ResultSet cursor to the last row. */
	public void last() { check(); navActs.run(NAV_LAST); }
	/** Move the ResultSet cursor to the next row. */
	public void next() { check(); navActs.run(NAV_NEXT); }
	/** Move the ResultSet cursor to the previous row. */
	public void previous() { check(); navActs.run(NAV_PREVIOUS); }
	/** Update the database with the new contents of the current row. */
	public void commit() { check(); navActs.run(NAV_COMMIT); }

	/**
	 * Move the RowSet cursor to the specified row.
	 * @param row target cursor row
	 */
	public void setRow(int row) {
		check();
		ModelAct spinModel = getSpinModelAct();
		if (spinModel.model != null) {
			spinModel.model.setValue(row);
			spinModel.action.actionPerformed(null);
		}
	}

	/**
	 * Return the count of rows in the ResultSet 
	 * @return count of rows
	 */
	public int getRowCount() {
		check();
		ModelAct spinModel = getSpinModelAct();
		if (spinModel.model != null)
			return (Integer)spinModel.model.getMaximum();
		return -1;
	}

	private record ModelAct(SpinnerNumberModel model, Action action){}
	private ModelAct getSpinModelAct() {
		Action act = navActs.get(NAV_GOTOROW);
		Object value = act.getValue(NavigateActions.KEY_SPINNER_MODEL);
		return new ModelAct((SpinnerNumberModel) value, act);
	}

	// ////////////////////////////////////////////////////////////////////
	//
	// Events
	//

	/**
	 * Register the busReceiver to get NavigationModel RowSet events;
	 * only methods annotated with {@code @WeakSubscribe} are registered.
	 * 
	 * @param busReceiver
	 */
	public void registerBusReceiver(Object busReceiver)
	{
		WeakEventBus.register(busReceiver, getEventBus());
	}

	/**
	 * Unregister the busReceiver to get NavigationModel RowSet events.
	 * @param busReceiver
	 */
	public void unregisterBusReceiver(Object busReceiver)
	{
		WeakEventBus.unregister(busReceiver, getEventBus());
	}

	/**
	 * Use a Weak listener. Otherwise remove from rowset easily doesn't happen.
	 * If not weak, this model will not be collected while the
	 * RowSet exists; the rowset's NavigationAction would not be collected.
	 */
	private final MyRowSetListener rowSetListener;
	private class MyRowSetListener implements RowSetListener
	{
		private WeakReference<RowSetListener> refWeakRowSetListener;
		private void registerTo(RowSet rs) {
			if(refWeakRowSetListener != null)
				throw new IllegalStateException("Already using listener");
			RowSetListener wrsl = WeakListeners.create(RowSetListener.class, this, rs);
			refWeakRowSetListener = new WeakReference<>(wrsl);
			rs.addRowSetListener(wrsl);
		}

		private void unregisterFrom(RowSet rs) {
			if (refWeakRowSetListener != null) {
				RowSetListener wrsl = refWeakRowSetListener.get();
				rs.removeRowSetListener(wrsl);
				refWeakRowSetListener.clear();
				refWeakRowSetListener = null;
			}
		}

		@Override
		public void rowSetChanged(RowSetEvent event)
		{
			addRowSetEvent(RowSetEventType.ROW_SET_CHANGED, event);
		}

		@Override
		public void rowChanged(RowSetEvent event)
		{
			addRowSetEvent(RowSetEventType.ROW_CHANGED, event);
		}

		@Override
		public void cursorMoved(RowSetEvent event)
		{
			addRowSetEvent(RowSetEventType.CURSOR_MOVED, event);
		}
	};

	//
	// NOTE: Only one "operation" can be in progress at a time.
	//       I think this is consistent with how SS works.
	//
	// TODO: Don't use static; make these instance.

	private static NavigationModel originatingModel;
	private static RowSet originatingRowSet;

	private static OperatorKind operatorKind = OperatorKind.UNKNOWN;
	private static List<RowSetEventType> eventTypes = new ArrayList<>(6);

	private static Object originatingObject;

	/** If there's confusion, events go out singularly until re-sync. */
	private static boolean flushIndividualEvents;

	/**
	 * Invoke this when starting an Operation that manipulates a RowSet.
	 * @param model
	 * @param compOrNav 
	 */
	// TODO: synchronized? Possible that two thread modify at same time?
	public static void startNavigationEvent(NavigationModel model, Object compOrNav)
	{
		Objects.requireNonNull(model.getRowSet());
		if (operatorKind != OperatorKind.UNKNOWN)
			throw new IllegalStateException("Event already in progress");

		operatorKind = switch (compOrNav) {
		case RSC comp -> { Objects.isNull(comp);
			yield OperatorKind.COMPONENT;
		}
		case NavAction navAction -> { Objects.isNull(navAction);
			yield OperatorKind.NAV;
		}
		case null -> OperatorKind.UNKNOWN;
		default -> throw new IllegalArgumentException("Must be RSC or NavAtion");
		};

		originatingModel = model;
		originatingRowSet = model.getRowSet();
		originatingObject = compOrNav;
	}

	/**
	 * NOTE: Events that occur while processing CachedRowSet changes back to
	 * database are discarded.
	 * 
	 * @param rsEventType
	 * @param rse 
	 */
	private static void addRowSetEvent(RowSetEventType rsEventType, RowSetEvent rse) {
		RowSet rs = (RowSet) rse.getSource();
		if (isAcceptingChanges(rs)) // only possible if CachedRowSet
			return;

		Objects.requireNonNull(operatorKind);
		if (operatorKind == OperatorKind.UNKNOWN)
			logger.log(WARNING, "Anonymous RowSet event");

		if (originatingRowSet != rs) {
			logger.log(WARNING, () -> sf(
					"Different RowSet orig %s, new %s", originatingRowSet, rs));
			if (originatingRowSet == null)
				originatingRowSet = rs;
			flushIndividualEvents = true;
		}

		boolean hasDup = false;
		if (operatorKind != OperatorKind.UNKNOWN) {
			//
			// TODO: ??? if wrong rowset, flush current and clear operatorKind
			//
			if (eventTypes.contains(rsEventType)) {
				//System.err.println("MULTIPLE EVENTS OF SAME TYPE");
				hasDup = true;
			}
		}

		// TODO: SYNCHRO?
		eventTypes.add(rsEventType);

		// Send the event is not associated with a known operation or RowSet.
		if (flushIndividualEvents || operatorKind == OperatorKind.UNKNOWN)
			finishNavigationEventInternal(null, rs);

		if (hasDup) {
			// if (isJunit()) System.err.println("Multiple: " + eventTypes);
			logger.log(DEBUG, "Multiple: " + eventTypes);
		}
	}

	/**
	 * Invoke this when finishing an Operation that manipulates a RowSet.
	 * All the RowSet events that occured during the operation are
	 * coalesced into a single event.
	 * @param _model must match the model associated with startNavigationEvent
	 */
	public static void finishNavigationEvent(NavigationModel _model) {
		finishNavigationEventInternal(_model, null);
		flushIndividualEvents = false;
	}

	@SuppressWarnings("CallToPrintStackTrace")
	private static void finishNavigationEventInternal(NavigationModel _model, RowSet _rs)
	{
		if (_model != null && _model != originatingModel)
			throw new IllegalStateException("Different model");

		RowSet rs = _rs != null ? _rs : originatingRowSet;
		NavigationModel model = _model != null ? _model : getDummy(rs);

		// TODO: add the RowSet, consider case where model == null ... UNKNOWN
		NavigationRowSetEvent ev;

		// Only create an event if there were RowSetEvents
		// TODO: if needed, have a different event type for start/finish without event.
		if (eventTypes.isEmpty()) {
			ev = null;
			Supplier<String> msg = () -> sf("NavigationEvent %s: No RowSet event", operatorKind);
			if(isJunit())System.out.println(msg.get());
			logger.log(TRACE, msg);
		} else {
			ev = new NavigationRowSetEvent(model, rs,
					operatorKind, originatingObject,
					eventTypes.isEmpty()
							? EnumSet.noneOf(RowSetEventType.class)
							: EnumSet.copyOf(eventTypes));
			if(isJunit())System.out.println(ev.toString());
			logger.log(TRACE, () -> sf(""+ev));
		}

		if (operatorKind == OperatorKind.COMPONENT && ev != null) {
			System.err.printf("\n\n***** COMPONENT EVENTS %s *****\n\n", ev);
			new Exception().printStackTrace(System.out);
		}

		originatingModel = null;
		originatingRowSet = null;
		operatorKind = OperatorKind.UNKNOWN;
		eventTypes.clear();
		originatingObject = null;

		if (ev != null)
			post(ev);
	}
	
	private static void post(EventObject ev) {
		getEventBus().post(ev);
	}

	//
	// TODO: How to find the right event bus for Navigation Model?
	//       Use the global event bus, at least for now
	//

	/**
	 * The event bus used by this NavigationModel.
	 * @return the event bus
	 */
	// TODO: per model event bus
	private static EventBus getEventBus() {
		return getGlobalEventBus();
	}

	/**
	 * TEMP: Can use this when there is no RowSet.
	 * JUST BUILD IT WITH A ROW SET.
	 * @return 
	 */
	public static NavigationModel getDummy() {
		return new DummyNavigationModel(null);
	}
	/**
	 * TEMP: Can use this when there is no RowSet.
	 * @param rs
	 * @return 
	 */
	public static NavigationModel getDummy(RowSet rs) {
		return new DummyNavigationModel(rs);
	}

	//public class DummyNavigationModel {
	private static class DummyNavigationModel extends NavigationModel {
		public DummyNavigationModel(RowSet rs)
		{
			this.rs = rs;
		}

		RowSet rs;
		@Override public void setRowSet(RowSet rs) { this.rs = rs; }
		@Override public RowSet getRowSet() { return rs; }

		@Override public Action getAction(NavAction navAction) { return null; }
		@Override public int getRow() { return -1; } 
		@Override public void first() { } 
		@Override public void last() { } 
		@Override public void next() { } 
		@Override public void previous() { } 
		@Override public void commit() { } 
		@Override public void setRow(int row) { } 
		@Override public int getRowCount() { return -1; }
	}
}
