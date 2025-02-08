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
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.SpinnerNumberModel;

import org.openide.util.WeakListeners;

import com.google.common.eventbus.EventBus;
import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDBNav;
import com.nqadmin.swingset.navigate.RowsEvent.OperatorKind;
import com.nqadmin.swingset.navigate.RowsEvent.RowSetEventType;
import com.nqadmin.swingset.navigate.RowsModelEventHandling.RowsEventSource;
import com.nqadmin.swingset.navigate.RowsModelEventHandling.SimpleEvents;
import com.nqadmin.swingset.utils.SSUtils;
import com.raelity.lib.eventbus.WeakEventBus;

import static com.nqadmin.swingset.navigate.RowsAction.*;
import static com.nqadmin.swingset.navigate.RowsModelEventHandling.postAsync;
import static com.nqadmin.swingset.navigate.Utils.getGlobalEventBus;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * The RowsModel is associated with a {@link javax.sql.RowSet}.
 * Using {@link NavigateActions} and {@link RowsAction} it encapsulates RowSet
 * traversal and their associated {@link javax.swing.Action}s which can be
 * plugged into UI components; it broadcasts RowSet events.
 * <p>
 * {@link RowsModelEvent}s are broadcast when the model's RowSet is changed
 * and when the current RowSet notifies of eventsNextQ. RowSet eventsNextQ are coalesced
 * when they occur in operations which are delineated by
 <ul>
 * <li> {@link #startRowsEvent }
 * <li> {@link #finishRowsEvent }
 * </ul>
 Other/stray RowSet eventsNextQ are sent individually as soon as they are received.
 */
//
// NOTE: The RowSet must be "executable".
//       Execution happens in NavigateActions::setupRowSet() via RowsModel.
//       Should RowsModel try to execute if no command?
//       Handle an "empty"/"null"/non-executable RowSet?
//       Is there a way to tell if current command has been executed?
//
// TODO: wrap the action and go indirect to the navActs.
//
// TODO: see test's NavigateHook
//
public class RowsModel
{
	static final String KEY_SPINNER_MODEL = "SPINNER_MODEL";

	/** Logger for component */
	static final Logger logger = SSUtils.getLogger();

	private NavigateActions navActs;

	// // For use ONLY with dummy
	// private RowsModel() {
	// 	rowSetListener = null;
	// }

	/** simplify switches back/forth */
	public static boolean ENABLED = true;

	/**
	 * Create and return a new RowsModel for the specified RowSet.
	 * <p>
	 * NOTE: the RowSet must have a query to execute. (IS THIS OK?)
	 *       Previously, the query was executed by SSDataNavigator.
	 * @param rs
	 * @return 
	 */
	public static RowsModel create(RowSet rs) {
		// For Now
		RowsModel rowsModel = new RowsModel(rs);
		//return SSUtils.findRowsModel(rs);
		return rowsModel;
	}

	/**
	 * Create one associated with the given RowSet.
	 * @param rs
	 */
	private RowsModel(RowSet rs) {
		// TODO: Could allow null RowSet as empty model.
		Objects.requireNonNull(rs);
		SSUtils.registerNewRowsModel(rs, this);
		rowSetListener = new SimpleRowSetListener();
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
		// TODO: Could allow null RowSet as empty model.
		Objects.requireNonNull(rs);
		rowSetListener.unregisterFrom(rs);
		this.navActs = NavigateActions.get(rs);
		rowSetListener.registerTo(rs);
		post(new RowsNewRowSetEvent(this));
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
	// TODO: javadoc says "action this model", but action is RowSet assoc.
	//       Wrap the action and go indirect to the navActs.
	//       Cache the wrapped actions.
	public Action getAction(RowsAction navAction) {
		check();
		return navActs.get(navAction);
	}

	/**
	 * Put all the navigation actions into the action map.
	 * If the param is null, a new actionMap is constructed and filled
	 * @param actionMap the actionMap to fill; may be null
	 * @return the filled actionMap
	 */
	public ActionMap fillNavActionMap(ActionMap actionMap) {
		ActionMap am = actionMap != null ? actionMap : new ActionMap();
		Arrays.stream(RowsAction.values()).forEach(key -> am.put(key, getAction(key)));
		return am;
	}

	// /**
	//  * 
	//  * @return 
	//  * @deprecated 
	//  */
	// @Deprecated
	// public NavigateActions getNavActs() {
	// 	check();
	// 	return navActs;
	// }

	/**
	 * Return the associated RowSet's current row number.
	 * @return row number
	 */
	public int getRow() {
		check();
		int spin_row = getSpinModel_Action().model.getNumber().intValue();
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
	public void first() { check(); navActs.run(ACT_FIRST); }
	/** Move the ResultSet cursor to the last row. */
	public void last() { check(); navActs.run(ACT_LAST); }
	/** Move the ResultSet cursor to the next row. */
	public void next() { check(); navActs.run(ACT_NEXT); }
	/** Move the ResultSet cursor to the previous row. */
	public void previous() { check(); navActs.run(ACT_PREVIOUS); }
	/** Update the database with the new contents of the current row. */
	public void commit() { check(); navActs.run(ACT_COMMIT); }

	/**
	 * Move the RowSet cursor to the specified row.
	 * @param row target cursor row
	 */
	public void setRow(int row) {
		check();
		Model_Action spinModel_Action = getSpinModel_Action();
		if (spinModel_Action.model != null) {
			spinModel_Action.model.setValue(row);
			//spinModel_Action.action.actionPerformed(null); // Just a repeat
		}
	}

	/**
	 * Return the count of rows in the ResultSet 
	 * @return count of rows
	 */
	public int getRowCount() {
		check();
		Model_Action spinModel_Action = getSpinModel_Action();
		if (spinModel_Action.model != null)
			return (Integer)spinModel_Action.model.getMaximum();
		return -1;
	}

	/**
	 * Check if there are rows in the RowSet.
	 * @return true if there are no rows
	 */
	public boolean isEmpty() {
		//return !rs.isBeforeFirst() && rs.getRow() == 0;
		return getRowCount() == 0;
	}

	private record Model_Action(SpinnerNumberModel model, Action action){}
	private Model_Action getSpinModel_Action() {
		Action action = navActs.get(ACT_GOTOROW);
		Object value = action.getValue(KEY_SPINNER_MODEL);
		return new Model_Action((SpinnerNumberModel) value, action);
	}

	/** Like Runnable, but may throw SQLException */
	public interface DBRunnable
	{
		/**
		 * @throws SQLException 
		 */
		public void run() throws SQLException ;
	}

	/**
	 * Use rsOp to capture multiple RowSet eventsNextQ into a single event.
	 * 
	 * @param operator
	 * @param r code that operates on a RowSet
	 * @throws java.sql.SQLException
	 */
	public void rsOp(Object operator, DBRunnable r) throws SQLException
	{
			RowsModel.startRowsEvent(OperatorKind.OTHER, this, operator);
			try {
				r.run();
			} finally {
				RowsModel.finishRowsEvent(this);
			}
	}

	/**
	 * Register the busReceiver to create RowsModel RowSet eventsNextQ;
	 * only methods annotated with {@code @WeakSubscribe} are registered.
	 * 
	 * @param busReceiver
	 */
	public void registerBusReceiver(Object busReceiver)
	{
		WeakEventBus.register(busReceiver, getEventBus());
	}

	/**
	 * Unregister the busReceiver to create RowsModel RowSet eventsNextQ.
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
	private abstract class RowSetListenerBase implements RowSetListener {
		private WeakReference<RowSetListener> refWeakRowSetListener;
		protected void registerTo(RowSet rs) {
			if(refWeakRowSetListener != null)
				throw new IllegalStateException("Already using listener");
			RowSetListener wrsl = WeakListeners.create(RowSetListener.class, this, rs);
			refWeakRowSetListener = new WeakReference<>(wrsl);
			rs.addRowSetListener(wrsl);
		}

		protected void unregisterFrom(RowSet rs) {
			if (refWeakRowSetListener != null) {
				RowSetListener wrsl = refWeakRowSetListener.get();
				rs.removeRowSetListener(wrsl);
				refWeakRowSetListener.clear();
				refWeakRowSetListener = null;
			}
		}
	}

	private final RowSetListenerBase rowSetListener;

	private class SimpleRowSetListener extends RowSetListenerBase
	{
		@Override
		public void rowSetChanged(RowSetEvent event)
		{
			addRowSetEvent(RowSetEventType.ROW_SET_CHANGED, (RowSet) event.getSource());
		}

		@Override
		public void rowChanged(RowSetEvent event)
		{
			addRowSetEvent(RowSetEventType.ROW_CHANGED, (RowSet) event.getSource());
		}

		@Override
		public void cursorMoved(RowSetEvent event)
		{
			addRowSetEvent(RowSetEventType.CURSOR_MOVED, (RowSet) event.getSource());
		}
	};

	/**
	 * Invoke this when starting an Operation that manipulates a RowSet.
	 * @param model
	 * @param compOrNav
	 */
	public static void startRowsEvent(RowsModel model, Object compOrNav)
	{
		enq.startRowsEvent(model, compOrNav);
	}

	/**
	 * Invoke this when starting an Operation that manipulates a RowSet.
	 * @param _operatorKind
	 * @param model
	 * @param compOrNav
	 */
	public static void startRowsEvent(OperatorKind _operatorKind, RowsModel model,
			Object compOrNav)
	{
		enq.startRowsEvent(_operatorKind, model, compOrNav);
	}

	/** from the RowSet event */
	private static void addRowSetEvent(RowSetEventType rsEventType, RowSet rs) {
		enq.addRowSetEvent(rsEventType, rs);
	}

	/**
	 * Invoke this when finishing an Operation that manipulates a RowSet.
	 * All the RowSet events that occured during the operation are
	 * coalesced into a single event.
	 * @param model must match the model associated with startRowsEvent
	 */
	public static void finishRowsEvent(RowsModel model) {
		enq.finishRowsEvent(model);
	}

	static void post(RowsModelEvent event) {
		postAsync(event);
	}

	// /** from the RowSet event */
	// private static void addRowSetEvents(Set<RowSetEventType> rsEventTypes, RowSet rs) {
	// 	enq.addRowSetEvents(rsEventTypes, rs);
	// }

	private static final EnqueueRowsEvent enq = new SimpleEvents();

	//
	// TODO: How to find the right event bus for Navigation Model?
	//       Use the global event bus, at least for now
	//

	/**
	 * The event bus used by this RowsModel.
	 * @return the event bus
	 */
	// TODO: per model event bus ???
	static EventBus getEventBus() {
		return getGlobalEventBus();
	}

	interface EnqueueRowsEvent
	{
		default void startRowsEvent(RowsModel model, Object compOrNav) {
			startRowsEvent(null, model, compOrNav);
		}
		void startRowsEvent(OperatorKind _operatorKind, RowsModel model, Object compOrNav);
		void addRowSetEvent(RowSetEventType rsEventType, RowSet rs);
		void addRowSetEvents(Set<RowSetEventType> rsEventTypes, RowSet rs);
		RowsEventSource finishRowsEvent(RowsModel model);
	}

	/**
	 * 
	 * @param tag 
	 */
	public static void dumpAllEvents(String tag) {
		RowsModelEventHandling.dumpAllEvents(tag);
	}

	//////////////////////////////////////////////////////////////////////
	//
	// Behavioral/State method - taken from SSDataNavigation
	//

	// /**
	//  * Get the NavigateActions used by this GUI element.
	//  * @return associated NavigateActions
	//  */
	// public NavigateActions getNavigateActions() {
	// 	return navActs;
	// }
	
	/**
	 * Method to cause the navigator to skip the execute() function call on the
	 * underlying RowSet. This is necessary for MySQL (see FAQ).
	 *
	 * @param callExecute false if using MySQL database - otherwise true
	 */
	public void setCallExecute(final boolean callExecute) {
		navActs.setCallExecute(callExecute);
	}

	/**
	 * Indicates if the navigator will skip the execute function call on the
	 * underlying RowSet (needed for MySQL - see FAQ).
	 *
	 * @return value of execute() indicator
	 */
	public boolean getCallExecute() {
		return navActs.getCallExecute();
	}

	/**
	 * Sets the confirm deletion indicator. If set to true, every time delete action
	 * is pressed, the navigator pops up a confirmation dialog to the user. Default
	 * value is true.
	 *
	 * @param confirmDeletes indicates whether or not to confirm deletions
	 */
	public void setConfirmDeletes(boolean confirmDeletes) {
		navActs.setConfirmDeletes(confirmDeletes);
	}

	/**
	 * Returns true if deletions must be confirmed by user, else false.
	 *
	 * @return returns true if a confirmation dialog is displayed when the user
	 *         deletes a record, else false.
	 */
	public boolean getConfirmDeletes() {
		return navActs.getConfirmDeletes();
	}

	/**
	 * Function that passes the implementation of the SSDBNav interface. This
	 * interface can be implemented by the developer to perform custom actions when
	 * the insert action is pressed
	 *
	 * @param dBNav implementation of the SSDBNav interface
	 */
	public void setDBNav(SSDBNav dBNav) {
		navActs.setDBNav(dBNav);
	}

	/**
	 * Returns any custom implementation of the SSDBNav interface, which is used
	 * when the insert action is pressed, to perform custom actions.
	 *
	 * @return any custom implementation of the SSDBNav interface
	 */
	public SSDBNav getDBNav() {
		return navActs.getDBNav();
	}

	/**
	 * Enables or disables the row deletion action. This method should be used if
	 * row deletions are not allowed. True by default.
	 *
	 * @param deletion indicates whether or not to allow deletions
	 */
	public void setDeletion(boolean deletion) {
		navActs.setDeletion(deletion);
	}

	/**
	 * Returns true if deletions are allowed, else false.
	 *
	 * @return returns true if deletions are allowed, else false.
	 */
	public boolean getDeletion() {
		return navActs.getDeletion();
	}

	/**
	 * Returns true if insertions are allowed, else false.
	 *
	 * @return returns true if insertions are allowed, else false.
	 */
	public boolean getInsertion() {
		return navActs.getInsertion();
	}

	/**
	 * Enables or disables the row insertion action. This method should be used if
	 * row insertions are not allowed. True by default.
	 *
	 * @param insertion indicates whether or not to allow insertions
	 */
	public void setInsertion(boolean insertion) {
		navActs.setInsertion(insertion);
	}

	/**
	 * Enables or disables the modification-related action on the SSDataNavigator.
	 * If the user can only navigate through the records with out making any changes set
	 * this to false. By default, the modification-related action are enabled.
	 *
	 * @param writable true to enable writable-related actions; false to disable
	 */
	public void setWritable(boolean writable) {
		navActs.setWritable(writable);
	}

	/**
	 * Returns true if the user can modify the data in the RowSet, else false.
	 *
	 * @return returns true if the user modifications are written back to the
	 *         database, else false.
	 */
	public boolean getWritable() {
		return navActs.getWritable();
	}

	/**
	 * Enables or disables the modification-related action on the SSDataNavigator.
	 * If the user can only navigate through the records with out making any changes
	 * set this to false. By default, the modification-related action are enabled.
	 *
	 * @param modification true to enable modification-related actions; false to disable
	 * @deprecated use setWritable
	 */
	@Deprecated
	public void setModification(boolean modification) {
		navActs.setWritable(modification);
	}

	/**
	 * Returns true if the user can modify the data in the RowSet, else false.
	 *
	 * @return returns true if the user modifications are written back to the
	 *         database, else false.
	 * @deprecated use getWritable
	 */
	@Deprecated
	public boolean getModification() {
		return navActs.getWritable();
	}

	/**
	 * @param navCombo the navCombo to set
	 */
	public void setNavCombo(SSDBComboBox navCombo) {
		navActs.setNavCombo(navCombo);
	}

	/**
	 * @return the navCombo
	 */
	// TODO: what's this about
	public SSDBComboBox getNavCombo() {
		return navActs.getNavCombo();
	}

	/**
	 * Returns true if the RowSet contains one or more rows, else false.
	 *
	 * @return return true if RowSet contains data else false.
	 */
	public boolean containsRows()
	{
		return navActs.containsRows();
	}

	/**
	 * @return boolean indicating if the navigator is on an insert row
	 */
	public boolean isOnInsertRow() {
		return navActs.isOnInsertRow();
	}

	/**
	 * Writes the present row back to the RowSet.
	 * 
	 * This is typically done when commit it pressed,
	 * but it may be done programmaticaly.
	 * 
	 * //		This is done automatically when
	 * //		any navigation takes place, but can also be called manually.
	 *
	 * @return returns true if update succeeds else false.
	 */
	public boolean updatePresentRow() {
		return navActs.updatePresentRow();
	}

	// //////////////////////////////////////////////////////////////////////
	// //
	// // Dummy model to avoid NPE
	// //
	// /**
	//  * TEMP: Can use this when there is no RowSet.
	//  * JUST BUILD IT WITH A ROW SET.
	//  * @return 
	//  */
	// public static RowsModel getDummy() {
	// 	return new DummyNavigationModel(null);
	// }
	// /**
	//  * TEMP: Can use this when there is no RowSet.
	//  * @param rs
	//  * @return 
	//  */
	// // TODO: try to find an existing model for the param RowSet ???
	// public static RowsModel getDummy(RowSet rs) {
	// 	return new DummyNavigationModel(rs);
	// }

	// //public class DummyNavigationModel {
	// // TODO: method isDummy() ???
	// private static class DummyNavigationModel extends RowsModel {
	// 	public DummyNavigationModel(RowSet rs)
	// 	{
	// 		this.rs = rs;
	// 	}

	// 	RowSet rs;
	// 	@Override public void setRowSet(RowSet rs) { this.rs = rs; }
	// 	@Override public RowSet getRowSet() { return rs; }

	// 	@Override public Action getAction(RowsAction navAction) { return null; }
	// 	@Override public int getRow() { return -1; } 
	// 	@Override public void first() { } 
	// 	@Override public void last() { } 
	// 	@Override public void next() { } 
	// 	@Override public void previous() { } 
	// 	@Override public void commit() { } 
	// 	@Override public void setRow(int row) { } 
	// 	@Override public int getRowCount() { return -1; }
	// }
}
