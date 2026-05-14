/* *****************************************************************************
 * Copyright (C) 2025-2026, Ernie R Rael. All rights reserved.
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
import java.lang.System.Logger.Level;
import java.lang.ref.WeakReference;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.SpinnerNumberModel;

import org.openide.util.WeakListeners;

import com.google.common.collect.MapMaker;
import com.google.common.eventbus.EventBus;
import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDBNav;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.navigate.RowsEvent.OperatorKind;
import com.nqadmin.swingset.navigate.RowsEvent.RowSetEventType;
import com.nqadmin.swingset.navigate.RowsModelEventHandling.RowsEventSource;
import com.nqadmin.swingset.navigate.RowsModelEventHandling.SimpleEvents;
import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSSyncManager;
import com.nqadmin.swingset.utils.SSUtils;
import com.raelity.lib.eventbus.WeakEventBus;

import static com.nqadmin.swingset.navigate.RowsAction.*;
import static com.nqadmin.swingset.navigate.RowsModelEventHandling.postAsync;
import static com.nqadmin.swingset.navigate.Utils.getGlobalEventBus;
import static com.nqadmin.swingset.utils.SSUtils.JDBCTypeMismatch;
import static com.nqadmin.swingset.utils.SSUtils.NullabilityMismatch;
import static com.nqadmin.swingset.utils.SSUtils.objectID;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * The RowsModel is associated with a {@link javax.sql.RowSet}.
 * Using {@link NavigateState} and {@link RowsAction} it encapsulates RowSet
 * traversal and their associated {@link javax.swing.Action}s which can be
 * plugged into UI components; it broadcasts RowSet events.<p>
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
//       Execution happens in NavigateState::setupRowSet() via RowsModel.
//       Should RowsModel try to execute if no command?
//       Handle an "empty"/"null"/non-executable RowSet?
//       Is there a way to tell if current command has been executed?
//
public class RowsModel
{
	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	// Used like a WeakHashSet.
	private static final Map<RowsModel,Boolean> activeRowModels
			= new MapMaker().weakKeys().makeMap();

	// Track the component bound column names.
	private final Map<SSComponent,String> bindings
			= new MapMaker().weakKeys().makeMap();
	
	private NavigateState navState;
	private final RowsActions rowsActions;

	/**
	 * Create and return a new RowsModel for the specified RowSet.
	 * <p>
	 * NOTE: the RowSet must have a query to execute. (IS THIS OK?)
	 *       Previously, the query was executed by SSDataNavigator.
	 * @param rs
	 * @return 
	 * @deprecated 
	 */
	@Deprecated
	public static RowsModel create(RowSet rs) {
		RowsModel rowsModel = create(rs, null);
		return rowsModel;
	}

	/**
	 * Create and return a new RowsModel for the specified RowSet.
	 * <p>
	 * NOTE: the RowSet must have a query to execute. (IS THIS OK?)
	 *       Previously, the query was executed by SSDataNavigator.
	 * @param rs
	 * @param dbNav
	 * @return 
	 */
	// TODO: dbNav should be picked up by DB/TBL
	public static RowsModel create(RowSet rs, SSDBNav dbNav) {
		RowsModel rowsModel = new RowsModel(rs, dbNav);
		return rowsModel;
	}

	/**
	 * @return
	 */
	public static int count() {
		// Can't depend on size() method when weakKeys.
		return SSUtils.size(activeRowModels);
	}

	/**
	 * @return
	 */
	public static int navCount() {
		return NavigateState.count();
	}

	/**
	 * Find the RowsModels that currently hold the specified RowSet.
	 * @param rs
	 * @return 
	 */
	// TODO: should this really be public?
	static List<RowsModel> getActiveRowModels(RowSet rs)
	{
		return activeRowModels.keySet().stream()
			.filter(rowsModel -> rowsModel.getRowSet() == rs)
			.toList();
	}

	/**
	 * TEMPORARY; typically for debug/transition; find any RowModel for the RowSet.
	 * @param rs
	 * @return 
	 */
	public static RowsModel getActiveRowModel(RowSet rs)
	{
		return activeRowModels.keySet().stream()
			.filter(rowsModel -> rowsModel.getRowSet() == rs)
			.findAny().orElse(null);
	}

	/**
	 * Create one associated with the given RowSet.
	 * @param rs
	 */
	// TODO: handle null RowSet; important, consider empty DataNavigator, build UI first.
	private RowsModel(RowSet rs, SSDBNav dbNav)
	{
		// TODO: get rid of junit test after tests are fully RowsModel ported.
		if (!SSUtils.isJunit() && rs != null && !verifyExecuted(rs))
			logger.log(Level.ERROR, "RowSet not executed", new Exception());
		logger.log(Level.INFO, () -> sf("new RowsModel %s for %s", objectID(this), objectID(rs)));

		activeRowModels.putIfAbsent(this, true);

		this.rowsActions = new RowsActions(this);

		setNavState(rs, dbNav);

		rowSetListener = new SimpleRowSetListener();
		rowSetListener.registerTo(rs);
	}

	/**
	 *
	 * @param rowsModel
	 */
	public static void issueRowChanged(RowsModel rowsModel)
	{
		startRowsEvent(rowsModel, ACT_ROW_CHANGED);
		addRowSetEvent(RowSetEventType.ROW_CHANGED, rowsModel.getRowSet());
		finishRowsEvent(rowsModel);
	}

	/**
	 * Get and set NavigationState. Two step process because when navState hooks into
	 * the RowSet, it uses the RowsModel.
	 * 
	 * <br>TODO: clean up the RowsModel/NavState initialization.
	 * 
	 * @param rs 
	 */
	private void setNavState(RowSet rs, SSDBNav dbNav)
	{
		if (rs == null) {
			navState = null;
			rowsActions.disableAllActions();
			return;
		}
		navState = NavigateState.getOrCreate(rs);
		if (navState != null)
			navState.setDBNav(dbNav);

		if (navState.getRowSet() == null)
			navState.setupRowSet(rs);
	}

	// TODO: verifyExecuted HACK, remove this and require executed row set 
	/**
	 * Check if RowSet is executed. If not executed, then execute it.
	 * @param rs
	 * @return true if was executed
	 * @throws SQLException 
	 */
	static boolean verifyExecuted(RowSet rs) {
		String msg;
		try {
			boolean ok = rs.getMetaData() != null
					? rs.getMetaData().getColumnCount() > 0 : false; // CachedRowSet
			if (ok)
				return true;
			msg = "no exception";
		} catch (SQLException ex) {
			msg = ex.getMessage();
		}
		String fMsg = msg;
		try {
			logger.log(Level.ERROR, () -> sf("%s. Will execute query", fMsg));
			// TODO: take out the callExecute error recovery, propogate the exception
			rs.execute();
		} catch (SQLException ex1) {
			logger.log(Level.ERROR, "execute() SQL Exception", ex1);
		}
		return false;
	}

	/**
	 * Change the RowSet associated with this model.
	 * @param rs new RowSet for this model
	 * @param dbNav
	 */
	public void setRowSet(RowSet rs, SSDBNav dbNav) {
		logger.log(Level.INFO, () -> sf("RowsModel %s change rowSet from %s to %s",
				objectID(this), objectID(getRowSet()), objectID(rs)));

		if (isDirty())
			//throw new IllegalStateException("oldRS dirty"); ???
			logger.log(INFO, "oldRS dirty");

		if (rs != null) {
			verifyExecuted(rs);

			// TODO: Probably need a setRowSet variant that allows replacing dirty rowSet
			NavigateState newNS = NavigateState.get(rs);
			if (newNS != null && newNS.undoRow.isDirty())
				//throw new IllegalStateException("newRS dirty"); ???
				logger.log(INFO, "newRS dirty");

			// Check for component binding compatibility here
			// to avoid exception buried in event handler.
			for (Map.Entry<SSComponent, String> entry : bindings.entrySet()) {
				SSComponent comp = entry.getKey();
				if (!comp.isFullyBound())
					continue;

				// verify same column type and nullable.
				String colName = entry.getValue();
				JDBCType oldType = comp.getBoundColumnJDBCType();
				JDBCType newType = JDBCType.NULL;
				try {
					newType = RowSetOps.getJDBCColumnType(rs, colName);
				} catch (SQLException ex) {
					logger.log(Level.ERROR, (String) null, ex);
				}
				if (newType != oldType)
					throw new IllegalArgumentException(JDBCTypeMismatch(oldType, newType));
				boolean oldVal = comp.getAllowNull();
				boolean newVal = RowSetOps.isNullable(rs, colName).get();
				if (oldVal != newVal)
					throw new IllegalArgumentException(NullabilityMismatch(oldVal, newVal));
			}
		}

		RowSet oldRowSet = getRowSet();
		rowSetListener.unregisterFrom(oldRowSet);
		setNavState(rs, dbNav);

		rowSetListener.registerTo(rs);
		enq.postNewRowSetEvent(this, oldRowSet);
	}

	// TODO: is this path OK?
	void syncSyncManager() {
		getNavState().syncSyncManager();
	}

	/**
	 * Change the RowSet associated with this model using current SSDBNav.
	 * @param rs new RowSet for this model
	 */
	// TODO: handle setRowSet only if there's already a dbNav.
	public void setRowSet(RowSet rs) {
		setRowSet(rs, getDBNav());
	}

	/**
	 * 
	 * @param comp
	 * @param columnName
	 */
	@SuppressWarnings("deprecation")
	public void bind(SSComponent comp, String columnName) {
		if (bindings.containsKey(comp))
			throw new IllegalArgumentException("Component already bound to this model");
		bindings.put(comp, columnName);
		try {
			comp.bind(this, columnName); // Only allowed from RowsModel
		} catch (Exception ex) {
			bindings.remove(comp);
			throw ex;
		}
	}

	/**
	 * Return the associated RowSet.
	 * @return row set
	 */
	public RowSet getRowSet() {
		return navState != null ? navState.getRowSet() : null;
	}

	NavigateState getNavState() {
		return navState;
	}

	/**
	 * Return an action, associated with this model, that can be plugged
	 * into a JComponent's {@link javax.swing.Action}.
	 * @param navAction
	 * @return 
	 */
	// TODO: javadoc says "action this model", but action is RowSet assoc.
	//       Wrap the action and go indirect to the navState.
	//       Cache the wrapped actions.
	public Action getAction(RowsAction navAction) {
		return rowsActions.get(navAction);
	}

	/**
	 * Put all the navigation actions into the action map.
	 * If the param is null, a new actionMap is constructed and filled
	 * @param actionMap the actionMap to fill; may be null
	 * @return the filled actionMap
	 */
	public ActionMap fillNavActionMap(ActionMap actionMap) {
		ActionMap am = actionMap != null ? actionMap : new ActionMap();
		Arrays.stream(RowsAction.values()).forEach(key -> {
			if (!key.isVirtual())
				am.put(key, getAction(key));
		});
		return am;
	}

	/**
	 * Return the associated RowSet's current row number.
	 * @return row number
	 */
	public int getRow() {
		int spin_row = getSpinnerModel().getNumber().intValue();
		if (Boolean.TRUE) {
			int rs_row = -1;
			try {
				rs_row = getRowSet().getRow();
			} catch (SQLException ex) {
			}
			if (spin_row != rs_row) {
				logger.log(ERROR, sf("spinner model out of sync with row set"));
			}
		}
		return spin_row;
	}

	/** Move the ResultSet cursor to the first row. */
	public void first() { rowsActions.run(ACT_FIRST); }
	/** Move the ResultSet cursor to the last row. */
	public void last() { rowsActions.run(ACT_LAST); }
	/** Move the ResultSet cursor to the next row. */
	public void next() { rowsActions.run(ACT_NEXT); }
	/** Move the ResultSet cursor to the previous row. */
	public void previous() { rowsActions.run(ACT_PREVIOUS); }
	/** Update the database with the new contents of the current row. */
	public void commit() { rowsActions.run(ACT_COMMIT); }

	/**
	 * Move the RowSet cursor to the specified row.
	 * @param row target cursor row
	 */
	public void setRow(int row) {
		SpinnerNumberModel spinnerModel = getSpinnerModel();
		if (spinnerModel != null) {
			spinnerModel.setValue(row);
		}
	}

	/**
	 * Return the count of rows in the ResultSet 
	 * @return count of rows
	 */
	public int getRowCount() {
		SpinnerNumberModel spinnerModel = getSpinnerModel();
		if (spinnerModel != null)
			return (Integer)spinnerModel.getMaximum();
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

	/**
	 * Is the current row of this dirty?
	 * @return is dirty
	 */
	public boolean isDirty() {
		//return getNavState().undoRow.isDirty();
		return getNavState() != null && getNavState().undoRow.isDirty();
	}

	// NOTE: SpinnerModel locked to RowSet
	SpinnerNumberModel getSpinnerModel() {
		return navState.rowNumberModel;
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
			if (rs != null)
				rs.addRowSetListener(wrsl);
		}

		protected void unregisterFrom(RowSet rs) {
			if (refWeakRowSetListener != null) {
				if (rs != null) {
					RowSetListener wrsl = refWeakRowSetListener.get();
					rs.removeRowSetListener(wrsl);
				}
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
	 * @param operatorKind
	 * @param model
	 * @param compOrNav
	 */
	public static void startRowsEvent(OperatorKind operatorKind, RowsModel model,
			Object compOrNav)
	{
		enq.startRowsEvent(operatorKind, model, compOrNav);
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

	private static final SimpleEvents enq = new SimpleEvents();

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

	interface EnqueueRowsModelEvent
	{
		void startRowsEvent(RowsModel model, Object compOrNav);
		void startRowsEvent(OperatorKind _operatorKind, RowsModel model, Object compOrNav);
		void addRowSetEvent(RowSetEventType rsEventType, RowSet rs);
		RowsEventSource finishRowsEvent(RowsModel model);
		void postNewRowSetEvent(RowsModel model, RowSet oldRowSet);
	}

	//////////////////////////////////////////////////////////////////////
	//
	// Config behavioral methods - taken from SSDataNavigation
	//
	
	/**
	 * This is necessary for ancient MySQL jdbc driver (see FAQ).
	 * @param callExecute
	 * @deprecated need to define a new strategy
	 */
	@Deprecated
	public void setCallExecute(final boolean callExecute) {
	}

	/**
	 * This is necessary for ancient MySQL jdbc driver (see FAQ).
	 * @return 
	 * @deprecated need to define a new strategy
	 */
	@Deprecated
	public boolean getCallExecute() {
		return false;
	}

	/**
	 * Sets the confirm deletion indicator. If set to true, every time delete action
	 * is pressed, the navigator pops up a confirmation dialog to the user. Default
	 * value is true.
	 *
	 * @param confirmDeletes indicates whether or not to confirm deletions
	 */
	public void setConfirmDeletes(boolean confirmDeletes) {
		navState.setConfirmDeletes(confirmDeletes);
	}

	/**
	 * Returns true if deletions must be confirmed by user, else false.
	 *
	 * @return returns true if a confirmation dialog is displayed when the user
	 *         deletes a record, else false.
	 */
	public boolean getConfirmDeletes() {
		return navState.getConfirmDeletes();
	}

	/**
	 * Function that passes the implementation of the SSDBNav interface. This
	 * interface can be implemented by the developer to perform custom actions when
	 * the insert action is pressed
	 *
	 * @param rs
	 * @param dBNav implementation of the SSDBNav interface
	 */
	// TODO: how to get the DBNav? Lookup and needs to be split.
	// TODO: should dBNav be local to this class? Hm, does seem like a rowset thing.
	public void setDBNav(RowSet rs, SSDBNav dBNav) {
		NavigateState nState = NavigateState.get(rs);
		nState.setDBNav(dBNav);
	}

	/**
	 * Function that passes the implementation of the SSDBNav interface. This
	 * interface can be implemented by the developer to perform custom actions when
	 * the insert action is pressed
	 *
	 * @param dBNav implementation of the SSDBNav interface
	 * @deprecated use setDBNav(Rowset, SSDBNav)
	 */
	@Deprecated
	public void setDBNav(SSDBNav dBNav) {
		navState.setDBNav(dBNav);
	}

	/**
	 * Returns any custom implementation of the SSDBNav interface, which is used
	 * when the insert action is pressed, to perform custom actions.
	 *
	 * @return any custom implementation of the SSDBNav interface
	 */
	public SSDBNav getDBNav() {
		return navState != null ? navState.getDBNav() : null;
	}

	/**
	 * Enables or disables the row deletion action. This method should be used if
	 * row deletions are not allowed. True by default.
	 *
	 * @param deletion indicates whether or not to allow deletions
	 */
	public void setDeletion(boolean deletion) {
		navState.setDeletion(deletion);
	}

	/**
	 * Returns true if deletions are allowed, else false.
	 *
	 * @return returns true if deletions are allowed, else false.
	 */
	public boolean getDeletion() {
		return navState.getDeletion();
	}

	/**
	 * Returns true if insertions are allowed, else false.
	 *
	 * @return returns true if insertions are allowed, else false.
	 */
	public boolean getInsertion() {
		return navState.getInsertion();
	}

	/**
	 * Enables or disables the row insertion action. This method should be used if
	 * row insertions are not allowed. True by default.
	 *
	 * @param insertion indicates whether or not to allow insertions
	 */
	public void setInsertion(boolean insertion) {
		navState.setInsertion(insertion);
	}

	/**
	 * Enables or disables the modification-related action on the SSDataNavigator.
	 * If the user can only navigate through the records with out making any changes set
	 * this to false. By default, the modification-related action are enabled.
	 *
	 * @param writable true to enable writable-related actions; false to disable
	 */
	public void setWritable(boolean writable) {
		navState.setWritable(writable);
	}

	/**
	 * Returns true if the user can modify the data in the RowSet, else false.
	 *
	 * @return returns true if the user modifications are written back to the
	 *         database, else false.
	 */
	public boolean getWritable() {
		return navState.getWritable();
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
		navState.setWritable(modification);
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
		return navState.getWritable();
	}

	/**
	 * @param navCombo the navCombo to set
	 * @deprecated use {@linkplain RowsModel#setNavCombo(com.nqadmin.swingset.SSDBComboBox, com.nqadmin.swingset.utils.SSSyncManager) }
	 */
	@Deprecated
	public void setNavCombo(SSDBComboBox navCombo) {
		setNavCombo(navCombo, null);
	}

	/**
	 * @param navCombo the navCombo used with this RowsModel
	 * @param syncer
	 */
	public void setNavCombo(SSDBComboBox navCombo, SSSyncManager syncer) {
		navState.setNavCombo(navCombo, syncer);
	}

	/**
	 * @return the navCombo
	 */
	// TODO: what's this about? Remove it.
	public SSDBComboBox getNavCombo() {
		return navState.getNavCombo();
	}

	/**
	 * Returns true if the RowSet contains one or more rows, else false.
	 *
	 * @return return true if RowSet contains data else false.
	 */
	public boolean containsRows()
	{
		return navState.containsRows();
	}

	/**
	 * @return boolean indicating if the navigator is on an insert row
	 */
	public boolean isOnInsertRow() {
		return navState.isOnInsertRow();
	}

	/**
	 * @param comp
	 * @return true if the component is in an error state
	 */
	public boolean hasError(SSComponent comp) {
		// TODO: check if row set has component's column name?
		// if (!bindings.containsKey(comp))
		// 	throw new IllegalArgumentException("Component not bound in RowsModel");
		return navState.hasError(comp);
	}

	/**
	 * Writes the present row back to the RowSet.
	 * 
	 * This is typically done when commit it pressed,
	 * but it may be done programmatically.
	 * 
	 * //		This is done automatically when
	 * //		any navigation takes place, but can also be called manually.
	 *
	 * @return returns true if update succeeds else false.
	 */
	public boolean updatePresentRow() {
		return navState.updatePresentRow();
	}
}
