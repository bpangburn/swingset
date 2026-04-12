/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
 * All rights reserved.
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
 * 
 * Contributors:
 *   Prasanth R. Pasala
 *   Brian E. Pangburn
 *   Diego Gil
 *   Man "Bee" Vo
 *   Ernie R. Rael
 ******************************************************************************/
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024-2025, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.navigate;

import java.lang.System.Logger;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.RowSet;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.SpinnerNumberModel;

import com.google.common.collect.MapMaker;
import com.nqadmin.swingset.*;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.navigate.RowsEvent.OperatorKind;
import com.nqadmin.swingset.navigate.RowsEvent.RowSetEventType;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSSyncManager;
import com.nqadmin.swingset.utils.SSUtils;
import com.raelity.lib.eventbus.WeakEventBus;
import com.raelity.lib.eventbus.WeakSubscribe;

import static com.nqadmin.swingset.navigate.RowsAction.*;
import static com.nqadmin.swingset.navigate.UndoRedo.isUndoRedoEnabled;
import static com.nqadmin.swingset.navigate.Utils.getGlobalEventBus;
import static com.nqadmin.swingset.utils.SSUtils.objectID;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

//TODO: Handle CachedRowSet Paging

/*
 * External controls
 *     - confirmDeletes
 *     - DBNav
 *     - deletion (enableDeletion, deleteOK)
 *     - insertion (enableInsertion, insertOK)
 *     - writeable (writeOK)
 *     - NavCombo
 */

/** * {@link NavigateState} contains RowSet state which get reflected in RowsActions.
 * There are {@linkplain Action}s for navigation, insertion, and deletion of
 * records in a RowSet.
 * There are {@linkplain ButtonModel}s for state, such as row dirty
 * that could be connected to a commit UI component. Readonly versions of the
 * state buttons are available.
 * <p>
 * This class listens for events about RowSet modifications and sends navigation
 * events (IN THE FUTURE). When navigating to a row, this class stashes the
 * current values (the values fetched from the datase); these are used for undo.
 * TODO: undo/redo history.
 * NOTE: undo and refresh are not the same thing.
 * NOTE: FetchSize
 * 
 * They are used by {@linkplain SSDataNavigator}.
 
 There are various navigation management parameters that may be set.
 - auto commit
 
 
 
 
 
 
 
 
 
 Component that can be used for data navigation. It provides buttons for
 navigation, insertion, and deletion of records in a RowSet. The
 writable of a RowSet can be prevented using the setModificaton()
 method. Any changes made to the columns of a record will be updated whenever
 there is a navigation.
 <p>
 * For example if you are displaying three columns using the JTextField and the
 * user changes the text in the text fields then the columns will be updated to
 * the new values when the user navigates the RowSet. If the user wants to
 * revert the changes he made he can press the Undo button, however this must be
 * done before any navigation. Once navigation takes place changes can't be
 * reverted using Undo button (has to be done manually by the user).
 */
//
// TODO: package access
//
final class NavigateState
{
	/**
	 * Which key does increment/decrement.
	 */
	public enum UpDownKeysAction {
		/** This is like data in a grid. */
		UP_DECREMENT,
		/** Up key increments. */
		UP_INCREMENT,
	}

	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	//
	// TODO:
	//     For now, have the defaults here. In the future,
	//     probably want to set the defaults from some
	//     configurable spot, via CentralLookup?
	//     Maybe: interface SwingSetConfiguration {}
	//
	private static final boolean V3_BUTTONS_DEFAULT = false;
	private static final boolean AUTO_COMMIT_DEFAULT = false;

	/** logger for package use. */
	static Logger getLogger()
	{
		return logger;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// TODO: Consider
	// The usage of these static access functions should probably
	// be reduced, if not eliminated, in favor of including a navAction
	// reference in the SSComponent.
	//

	// TODO: Should the static methods have instance counterparts,
	//		 e.g. hasActiveRow. Then could make direct queries when
	//		 a Navigation is available.

	/**
	 * Return NavState for the RowSet.
	 * If existing one, for given rowSet, not found;
	 * the returned navState.getRowSet is null;
	 * setupRowSet must be invoked before other usage. 
	 * @param rowSet
	 * @return 
	 */
	synchronized static NavigateState getOrCreate(RowSet rowSet)
	{
		Objects.requireNonNull(rowSet);
		//NavigateState navState = RowSetState.getNavigateState(rowSet);
		NavigateState navState = get(rowSet);
		if (navState == null) {
			// RowSetState.setNavigateState(rowSet, navState = new NavigateState(null));
			navState = new NavigateState(null); // TODO: why null?
			if (navState.rowSetState != null)
				throw new IllegalStateException("navState.rowSetState not null");

			// At least for now, some of the RowSetState can be there without NavState.
			// if (RowSetState.getExistingRowSetState(rowSet) != null)
			// 	throw new IllegalStateException("RowSetState.get... not null");

			navState.rowSetState = RowSetState.getRowSetState(rowSet);
		}
		return navState;
	}

	/**
	 * Return NavState for the RowSet.
	 * If existing one, for given rowSet, not found;
	 * the returned navState.getRowSet is null;
	 * setupRowSet must be invoked before other usage. 
	 * @param rowSet
	 * @return 
	 */
	synchronized static NavigateState get(RowSet rowSet)
	{
		Objects.requireNonNull(rowSet);
		//return RowSetState.getNavigateState(rowSet);
		return navigateState.get(rowSet);
	}

	private static final Map<RowSet,NavigateState> navigateState
			= new MapMaker().weakKeys().weakValues().makeMap();

	/**
	 * @return
	 */
	static int count() {
		// Can't depend on size() method when weakKeys.
		return SSUtils.size(navigateState);
	}


	//////////////////////////////////////////////////////////////////////
	//
	// INSTANCE starts here
	//

	//Weak Subscriber notes: {@link com.nqadmin.swingset.navigate.Utils}.

	/**
	 * Listener(s) for the underlying RowSet used to update the bound SwingSet
	 * component. When working with a {@linkplain javax.sql.rowset.CachedRowSet} there are
	 * extra steps involved which require the listener to ignore some events, see
	 * {@link RowSetState#acceptChanges(javax.sql.rowset.CachedRowSet, java.lang.Runnable) }.
	 */

	class BusReceiver {
		/**
		 * Ignore events generated by this class: OperatorKind.ACTION,
		 * and only process events for this RowSet.
		 */
		@WeakSubscribe
		public void handleRowSetEvent(RowsEvent ev)
		{
			logger.log(DEBUG, () -> sf("%s %s", objectID(getRowSet()), ev.toString()));
			
			// TODO: ev.getModel != getModel /// not RowSet
			if (ev.getKindOperator() == OperatorKind.ACTION
					|| ev.getRowSet() != getRowSet()) {
				// System.err.println("     ***** SKIP *****");
				return;
			}
			 if (!RowsModel.ENABLED)
			 	return;
			if (ev.getEventTypes().contains(RowSetEventType.ROW_SET_CHANGED)) {
				// Update the record count. Leave positioned at first row.
				try {
					logger.log(DEBUG, "Updating row count.");
					getRowSet().last();
					rowCount = getRowSet().getRow();
					getRowSet().first();
				} catch (final SQLException se) {
					logger.log(ERROR, "SQL Exception.", se);
				}
			}
			try {
				logger.log(DEBUG, "Calling updateNavigator().");
				freshRow();
				updateNavigator();
			} catch (final SQLException se) {
				logger.log(ERROR, "SQL Exception.", se);
			}
		}

		@WeakSubscribe
		public void handleNewRowSetEvent(RowsModelNewRowSetEvent ev)
		{
			logger.log(DEBUG, () -> sf("%s %s", objectID(getRowSet()), ev.toString()));

			// Return if not our row set.
			if (ev.getRowsModel().getRowSet() != getRowSet())
				return;

			try {
				updateNavigator();
			} catch (SQLException ex) {
				logger.log(ERROR, (String) null, ex);
			}
		}


		// Following are typically from RowSetOps.

		@WeakSubscribe
		public void handleRowDataChanged(RowSetModificationEvent ev)
		{
			if (ev.matches(getRowSet())) {
				// Our RowSet's row has changed
				
				// TODO what about ev.getSource == null ?

				try {
					ev.getSource().addUndoableChange(ev);
				} catch (SQLException ex) {
					logger.log(ERROR, "Undo/redo exception", ex);
				}
				if(ev.isError()) {
					errorComponents.add(ev.getSource());
				} else {
					errorComponents.remove(ev.getSource());
				}

				logger.log(TRACE, () -> ev.toString());
				setRowModified();
				updateActionState();
			}
		}

		@WeakSubscribe
		public void handleRowUndoRedo(RowSetUndoRedoEvent ev)
		{
			if (ev.matches(getRowSet())) {
				// Our RowSet's row had an undo/redo.
				if(ev.isError()) {
					errorComponents.add(ev.getSource());
				} else {
					errorComponents.remove(ev.getSource());
				}
				logger.log(TRACE, () -> ev.toString());
				updateActionState();
			}
		}

		@WeakSubscribe
		public void handleFocusChangeEvent(FocusChangeEvent ev)
		{
			undoRow.focusChange(ev);
		}
	}

	BusReceiver busReceiver; // Must have a strong reference.
	private void setupEventBus() {
		busReceiver = new BusReceiver();
		WeakEventBus.register(busReceiver, getGlobalEventBus());
	}

	
	// TODO: Also have Set<SSComponentInterface> modifiedComponents.
	//		 Paint modified/OK fields with identifying color, e.g. yellow.
	private final Set<SSComponentInterface> errorComponents;

	/** Undo/redo this this rowset */
	final UndoRow undoRow;

	
	/** Row number for current record in RowSet. */  // TODO: is this needed? Save much?
	/*private*/ int currentRow = 0;

	/** Container (frame or internal frame) which contains the navigator. */
	/*private*/ SSDBNav dBNav = new SSDBNav() {};

	/**
	 * SSDBComboBox used for navigation if applicable.
	 * <p>
	 * Allows Navigator to disable it when a row is inserted and enable it when that row is saved.
	 * <p>
	 * TODO Consider writing a PropertyChangeListener for onInsertRow instead.
	 */
	private SSDBComboBox navCombo = null;

	private SSSyncManager syncer = null;

	/** Number of rows in RowSet. Set to zero if next() method returns false. */
	/*private*/ int rowCount = 0;

	/** RowSet from which component will create/set values. */
	private /*final*/ RowSet rowSet;
	
	/** Indicates if rowset listener is added (or removed) */
	// XXX
	/*private*/ boolean rowsetListenerAdded = false;

	private RowSetState rowSetState;

	/**
	 * Create actions and models for the RowSet.Note that _rowSet may be null for a Dummy.
	 *
	 * @param rowSet   the RowSet to which the navigator is bound to
	 */
	//
	// TODO: RowsModel
	// TODO: create actions on demand; could have a supplier in RowsAction enum
	//
	@SuppressWarnings("LeakingThisInConstructor")
	private NavigateState(final RowSet rowSet)
	{
		v3Buttons = V3_BUTTONS_DEFAULT;
		autoCommit = AUTO_COMMIT_DEFAULT;

		// TODO: should we be listening to SpinnerNumberModel
		//       and not using an Action?

		rowNumberModel = new SpinnerNumberModel(1, 1, 1, 1);
		setUpDownKeysAction(UpDownKeysAction.UP_DECREMENT);

		undoRow = new UndoRow();
		errorComponents = new HashSet<>();

		setupEventBus();

		if (rowSet == null)
			return;
		setupRowSet(rowSet);
	}

	void setupRowSet(RowSet rowSet)
	{
		if (this.rowSet != null)
			throw new IllegalStateException("NavState already has a RowSet");
		this.rowSet = rowSet;
		navigateState.put(rowSet, this);
		setupRowSet();
	}

	/**
	 * Sets the RowSet for the navigator; the RowSet's current row is preserved.
	 * The RowSet's select query should already be executed.
	 *
	 * @param _rowSet data source for navigator
	 */
	private void setupRowSet()
	{
		try {
			RowsModel.verifyExecuted(getRowSet());
			int initial_row = getRowSet().getRow();

			if (initial_row != 0) {
				getRowSet().last();
				rowCount = getRowSet().getRow();
				getRowSet().absolute(initial_row);
				currentRow = initial_row;
			} else {
				// See if there are any rows in the given ssrowset.
				if (!getRowSet().next()) {
					rowCount = 0;
					currentRow = 0;
				} else {
					// If there are rows get the row count.
					getRowSet().last();
					rowCount = getRowSet().getRow();
					getRowSet().first();
					currentRow = getRowSet().getRow();
				}
			}
		} catch (final SQLException se) {
			logger.log(ERROR, "SQL Exception.", se);
		}
		
		// Add rowset listener.
		enableRowsetListeningFlag("setupRowSet");

		try {
			// freshRow();	// ************************** remove, setupRow only happens once.
			updateNavigator();
		} catch (final SQLException se) {
			logger.log(ERROR, "SQL Exception.", se);
		}

		// TODO: This is new since first time NavGroupState was implemented.
		//       I think that doing setRowModified(false) a few lines up
		//       before the updateNavigator() should take care of button state
		//       that the following is supposed to handle.
		//
		// // ENABLE OTHER BUTTONS IF NEED BE.

		// // THIS IS NEEDED TO HANDLE USER LEAVING THE SCREEN IN AN INCONSISTENT
		// // STATE EXAMPLE: USER CLICKS ADD BUTTON, THIS DISABLES ALL THE BUTTONS
		// // EXCEPT COMMIT & UNDO. WITH OUT COMMITING OR UNDOING THE ADD USER
		// // CLOSES THE SCREEN. NOW IF THE SCREEN IS OPENED WITH A NEW SSROWSET.
		// // THE REFRESH, ADD & DELETE WILL BE DISABLED.
		// // 2019-11-11: only enabling add/delete if this.writable==true
		// refreshButton.setEnabled(true);
		// if (insertion && writable) {
		// 	addButton.setEnabled(true);
		// }
		// if (deletion && writable) {
		// 	deleteButton.setEnabled(true);
		// }

	}

	/**
	 * Returns the RowSet being used.
	 *
	 * @return returns the RowSet being used.
	 */
	final RowSet getRowSet() {
		return rowSet;
	}

	/**
	 * Specifies how the up/down arrows increment/decrement.
	 * However the up key is specified, the down key does the opposite.
	 * @param act the up key behavior
	 */
	public final void setUpDownKeysAction(UpDownKeysAction act) {
		int stepsize = act == UpDownKeysAction.UP_DECREMENT ? -1
				: act == UpDownKeysAction.UP_INCREMENT ? 1 : 0;
		if (stepsize != 0)
			rowNumberModel.setStepSize(stepsize);
	}

	/**
	 * Perform the specified undo/redo cmd on the specified component.
	 * @param comp ssComponent
	 * @param cmd undo or redo
	 * @return new value, only for logging
	 * @throws java.sql.SQLException
	 */
	// TODO: Should this be public? NO, go through the static method in this class
	Object doUndoRedo(SSComponentInterface comp, UndoRedo cmd) throws SQLException
	{
		if (!isUndoRedoEnabled(comp))
			throw new IllegalStateException("UNDO/REDO disabled");
		Object value = undoRow.doUndoRedo(comp, cmd);
		updateActionState();
		return value;
	}

	/*private*/ final SpinnerNumberModel rowNumberModel;

	/**
	 * Common code to commit changes to the database from the rowset if
	 * modifications are allowed; called before every action.
	 * After committing, it performs any post-update operations.
	 * <p>
	 * If writable==false, then skip the update and return as
	 * successful, unless we have an empty rowset. 
	 * 
	 * @param performPostUpdateOps true if performPostUpdateOps() should
	 * 	be called after successful update, otherwise false
	 * 
	 * @return true unless there are no records OR dBNav.allowUpdate() returns false
	 * @throws SQLException SQL Exception if rowset call to updateRow() fails
	 */
	/*private*/ boolean commitChangesToDatabase(final boolean performPostUpdateOps)
			throws SQLException
	{
		boolean result = true;
		
		// check for an empty rowset 
		if (getRowSet().getRow() == 0) {
			result = false;
		}

		//boolean isDirty = undoRow.isDirty();
		cleanupUpdateRow();	// CURRENTLY A NO-OP
		
		// if we have at least one row and the user has not set modifications to false (read-only)
		// then continue to attempt to update database based on current rowset values
		if (result && writable) {
			if (!dBNav.allowUpdate()) {
				result = false;
			} else {
				RowSetOps.updateRow(getRowSet());
			}
		}
		
		// if update was successful or modifications are set to false (read-only) then
		// attempt post-update operations based on method parameter
		//
		// note that where modifications are set to false, we're pretending to have
		// successfully updated the database

		// TODO: If updateRow above didn't happen, when writable == false, then
		//       seems there is no reason to do dBNav.performPostUpdateOps()
		//       under any circumstance.
		//       undoRow.isDirty() might also be useful...

		if (result && performPostUpdateOps) {
			dBNav.performPostUpdateOps();
		}
		
		// return
		return result;
	}

	/**
	 * Make sure updateRow only has values that are user changes.
	 * After making a change, undo can back off to the original (base value
	 * of undo/redo stack); if that happened, cancelRowUpdates, and
	 * re-update that actual changes.
	 */
	private void cleanupUpdateRow()
	{
		// TODO: is this actually needed/desirable?
		// It seems nice to not "change" things in the database that don't change.
	}

	// //
	// // NOTE: looking at h2, doing deleteRow also clears updateRow
	// //
	// /**
	//  * Before something like refresh/delete Actions should clear the updateRow.
	//  */
	// private void clearUpdateRow()
	// {
	// }

	/**
	 * Returns true if the RowSet contains one or more rows, else false.
	 *
	 * @return return true if RowSet contains data else false.
	 */
	public boolean containsRows()
	{
		return rowCount != 0;
	}


	/**
	 * @return boolean indicating if the navigator is on an insert row
	 */
	public boolean isOnInsertRow() {
		return RowSetState.isInserting(getRowSet());
	}
	
	/**
	 * Adds listener to the rowset
	 */
	/*private*/ void enableRowsetListeningFlag(String tag) {
		// XXX
		if (!rowsetListenerAdded) {
			rowsetListenerAdded = true;
			logger.log(DEBUG, () -> sf("RowsetListener: %s: %s: is ON.", objectID(getRowSet()), tag));
		}
	}
	
	/**
	 * Removes listener from the rowset
	 */
	/*private*/ void disableRowsetListeningFlag(String tag) {
		// XXX
		if (rowsetListenerAdded) {
			rowsetListenerAdded = false;
			logger.log(DEBUG, () -> sf("RowsetListener: %s: %s: is OFF.", objectID(getRowSet()), tag));
		}
	}

	//////////////////////////////////////////////////////////////////////
	//
	// External control.
	//

	/**
	 * Indicator to cause the navigator to skip the execute() function call on the
	 * specified RowSet. Must be false for MySQL (see FAQ).
	 */

	/** Indicator to force confirmation of RowSet deletions. */
	/*private*/ boolean confirmDeletes = true;

	/** Indicator to allow/disallow deletions from the RowSet. */
	private boolean deletion = true;

	/** Indicator to allow/disallow insertions to the RowSet. */
	private boolean insertion = true;

	/** Indicator to allow/disallow changes to the RowSet. */
	private boolean writable = true;

	/**
	 * Sets the confirm deletion indicator. If set to true, every time delete button
	 * is pressed, the navigator pops up a confirmation dialog to the user. Default
	 * value is true.
	 *
	 * @param confirmDeletes indicates whether or not to confirm deletions
	 */
	// TODO: WHAT?
	public void setConfirmDeletes(boolean confirmDeletes) {
		//final boolean oldValue = confirmDeletes;
		this.confirmDeletes = confirmDeletes;
		// TODO: what is this about?
		//firePropertyChange("confirmDeletes", oldValue, confirmDeletes);
	}

	/**
	 * Returns true if deletions must be confirmed by user, else false.
	 *
	 * @return returns true if a confirmation dialog is displayed when the user
	 *         deletes a record, else false.
	 */
	public boolean getConfirmDeletes() {
		return confirmDeletes;
	}

	/**
	 * Enables or disables the row deletion button. This method should be used if
	 * row deletions are not allowed. True by default.
	 *
	 * @param deletion indicates whether or not to allow deletions
	 */
	public void setDeletion(boolean deletion) {
		//final boolean oldValue = deletion;
		this.deletion = deletion;
		// TODO: what is this about?
		//firePropertyChange("deletion", oldValue, deletion);

		updateActionState();
	}

	/**
	 * Returns true if deletions are allowed, else false.
	 *
	 * @return returns true if deletions are allowed, else false.
	 */
	public boolean getDeletion() {
		return deletion;
	}

	/**
	 * Enables or disables the row insertion button. This method should be used if
	 * row insertions are not allowed. True by default.
	 *
	 * @param insertion indicates whether or not to allow insertions
	 */
	public void setInsertion(boolean insertion) {
		//final boolean oldValue = insertion;
		this.insertion = insertion;
		// TODO: what is this about?
		//firePropertyChange("insertion", oldValue, insertion);

		updateActionState();
	}

	/**
	 * Returns true if insertions are allowed, else false.
	 *
	 * @return returns true if insertions are allowed, else false.
	 */
	public boolean getInsertion() {
		return insertion;
	}

	/**
	 * Enables or disables the modification-related buttons on the SSDataNavigator.
	 * If the user can only navigate through the records with out making any changes
	 * set this to false. By default, the modification-related buttons are enabled.
	 *
	 * @param writable indicates whether or not the writable-related
                      buttons are enabled.
	 */
	public void setWritable(boolean writable) {
		//final boolean oldValue = writable;
		this.writable = writable;
		// TODO: what is this about?
		//firePropertyChange("writable", oldValue, writable);

		updateActionState();
	}

	/**
	 * Returns true if the user can modify the data in the RowSet, else false.
	 *
	 * @return returns true if the user modifications are written back to the
	 *         database, else false.
	 */
	public boolean getWritable() {
		return writable;
	}

	/**
	 * Function that passes the implementation of the SSDBNav interface. This
	 * interface can be implemented by the developer to perform custom actions when
	 * the insert button is pressed
	 *
	 * @param dBNav implementation of the SSDBNav interface
	 */
	//TODO: does this belong here
	public void setDBNav(SSDBNav dBNav) {
		//final SSDBNav oldValue = dBNav;
		this.dBNav = dBNav != null ? dBNav : new SSDBNav() {};
		// TODO: what is this about?
		//firePropertyChange("dBNav", oldValue, dBNav);
	}

	/**
	 * Returns any custom implementation of the SSDBNav interface, which is used
	 * when the insert button is pressed to perform custom actions.
	 *
	 * @return any custom implementation of the SSDBNav interface
	 */
	// TODO: what's this about
	public SSDBNav getDBNav() {
		return dBNav;
	}

	// TODO: handle multipble navCombo?
	void setNavCombo(SSDBComboBox navCombo, SSSyncManager syncer) {
		Objects.requireNonNull(navCombo);
		// TODO: Objects.requireNonNull(syncer);
		if (this.navCombo != null)
			throw new IllegalStateException("navCombo already set");
		this.navCombo = navCombo;
		this.syncer = syncer;
	}

	/**
	 * @return the navCombo
	 */
	// TODO: what's this about
	/*public*/ SSDBComboBox getNavCombo() {
		return navCombo;
	}

	void syncSyncManager() {
		if (syncer != null)
			syncer.sync();
	}

	//////////////////////////////////////////////////////////////////////
	//
	// State maintenance - set enable/disable on various actions
	//

	/** Indicator that current row is dirty. */
	//private boolean isRowModified = false;

	void setNavComboEnabled(boolean b)
	{
		if (navCombo != null)
			navCombo.setEnabled(b);
	}

	/** Going to a new row, or undo updates, or refresh row. */
	/*private*/ void freshRow()
	{
		logger.log(TRACE, "freshRow");
		undoRow.clear();
		errorComponents.clear();
		//isRowModified = false; // TODO: get rid of this
	}

	/** Moving to insertRow. */
	/*private*/ void freshInsertRow()
	{
		logger.log(TRACE, "freshInsertRow");
		undoRow.clearInsertRow(getRowSet());
		errorComponents.clear();
	}


	// TODO: Use undoRow.isDirty() to check for writable.

	// TODO: get rid of the following
	private void setRowModified() {
		//isRowModified = true; // TODO: get rid of this
		//if (!isDirty) {
		//	undoRow.clear();
		//	errorComponents.clear();
		//}
	}
	
	private void updateEnable(RowsAction navAction, boolean flag) {
		List<RowsModel> rowsModels = RowsModel.getActiveRowModels(getRowSet());
		if (rowsModels.isEmpty())
			return;
		for (RowsModel rowsModel : rowsModels) {
			Action act = rowsModel.getAction(navAction);
			if(act.isEnabled() != flag)
				act.setEnabled(flag);
		}
	}

	/**
	 * Set to true for original behavior
	 */
	private boolean v3Buttons;
	
	/**
	 * when false, navigation disabled when row is dirty
	 */
	private boolean autoCommit;

	/**
	 * Return if Pre v4 button behavior.
	 * @return true if pre v4
	 */
	public boolean isV3Buttons() {
		return v3Buttons;
	}

	/**
	 * Set whether or not the commit and cancel buttons are
	 * always enabled, independent of whether or not there is a writable.
	 * @param _v3Buttons true for pre v4 behavior
	 */
	public void setV3Buttons(boolean _v3Buttons) {
		v3Buttons = _v3Buttons;
	}

	/**
	 * Return autoCommit mode. In autoCommit mode, the buttons that move
	 * the row, like first or prev, are enabled when row has a modification.
	 * Navigating away from a modified row,
	 * commits the changes to the rowSet.
	 * 
	 * @return true if autoCommit mode
	 */
	public boolean isAutoCommit() {
		return autoCommit;
	}

	/**
	 * Set whether or not to enable autoCommit mode.
	 * In autoCommit mode, the buttons that move
	 * the row, like first or prev, are enabled when row has a modification.
	 * Navigating away from a modified row,
	 * commits the changes to the rowSet.
	 * 
	 * @param _autoCommit inidcates whether or not to enable autoCommit mode
	 */
	public void setAutoCommit(boolean _autoCommit) {
		autoCommit = _autoCommit;
	}

	/**
	 * Set the enable/disable state of each button according to
	 * the Navigator state variables.
	 * @see #updateActionStateWithDatabaseCheck() 
	 */
	/*private*/ void updateActionState() {
		logger.log(TRACE, () -> sf("rowCount=%d, currentRow=%d", rowCount, currentRow));
		List<RowsModel> rowsModels = RowsModel.getActiveRowModels(getRowSet());
		if (rowsModels.isEmpty())
			logger.log(DEBUG, () -> sf("No RowsModel for rowSet %s", objectID(getRowSet())));

		boolean isRowModified = undoRow.isDirty();

		boolean onInsertRow = RowSetState.isInserting(getRowSet());
		boolean hasError = !errorComponents.isEmpty();
		boolean isAutoCommit = isAutoCommit();
		boolean commitUndoAlwaysEnabled = false;

		if (isV3Buttons()) {
			// force some things for old style
			isAutoCommit = true;
			hasError = false;
			commitUndoAlwaysEnabled = true;
		}

		// True if row is modified and don't want implicit commit.
		// Disables first, prev, next, last, add, refresh.
		boolean disablingAutoCommit = isRowModified && !isAutoCommit;

		// Handle first, prev, next, last (but there's that option for later)
		boolean canNavigate = rowCount != 0 && !onInsertRow && !disablingAutoCommit;
		boolean atFirst = currentRow == 1;
		boolean atLast = currentRow == rowCount;

		updateEnable(ACT_FIRST, canNavigate && !atFirst);
		updateEnable(ACT_PREVIOUS, canNavigate && !atFirst);
		updateEnable(ACT_NEXT, canNavigate && !atLast);
		updateEnable(ACT_LAST, canNavigate && !atLast);
		updateEnable(ACT_GOTOROW, canNavigate);
		setNavComboEnabled(canNavigate);

		// Handle commit, undo
		boolean commitUndoOk = writable
				&& (onInsertRow || isRowModified || commitUndoAlwaysEnabled);
		updateEnable(ACT_COMMIT, commitUndoOk  && !hasError);
		// TODO: Should undo/revert ever be disabled if isRowModified is true?
		updateEnable(ACT_REVERT, commitUndoOk);

		// TODO: Consider if row is dirty, delete button makes sense,
		//			but, does the add button make sense?
		// Handle add, delete
		if (onInsertRow) {
			updateEnable(ACT_ADD, false);
			updateEnable(ACT_DELETE, false);
		} else {
			// Perhaps the following should only be "!isRowModified"
			updateEnable(ACT_ADD, insertion && !disablingAutoCommit && writable);
			updateEnable(ACT_DELETE, deletion && writable && rowCount != 0);
		}

		// refresh
		// TODO: Should refresh/reload ever be disabled?
		updateEnable(ACT_REFRESH, !onInsertRow && !disablingAutoCommit);
	}

	/**
	 * Set the enable/disable state of each button according to
	 * the Navigator state variables; additionally set the state
	 * of the first, prev, next, and last buttons from the database.
	 * @see #updateActionState() 
	 */
	// TODO: understand why this is needed?
	private void updateActionStateWithDatabaseCheck() {
		updateActionState();
		try {
			if (getRowSet().isLast()) {
				updateEnable(ACT_NEXT, false);
				updateEnable(ACT_LAST, false);
			}
			if (getRowSet().isFirst()) {
				updateEnable(ACT_FIRST, false);
				updateEnable(ACT_PREVIOUS, false);
			}
		} catch (SQLException ex) {
			logger.log(ERROR, "SQL Exception.", ex);
		}
	}

	/**
	 * Enables/disables navigation buttons as needed
	 * and updates the current row and row count numbers.
	 * @throws SQLException 	SQLException
	 */
	void updateNavigator() throws SQLException {

		currentRow = getRowSet().getRow();

		rowNumberModel.setMaximum(rowCount);
		rowNumberModel.setValue(currentRow);

		logger.log(DEBUG, () -> "Current Row: " + currentRow + ". Row Count: " + rowCount);
		//logger.debug("Stack trace:", new Throwable());

		updateActionStateWithDatabaseCheck();
	}

	/**
	 * Writes the present row back to the RowSet. This is done automatically when
	 * any navigation takes place, but can also be called manually.
	 *
	 * @return returns true if update succeeds else false.
	 */
	public boolean updatePresentRow() {
		if (RowSetState.isInserting(getRowSet()) || (currentRow > 0)) {
			logger.log(DEBUG, "Doing NAV_COMMIT.");
			// TODO: minor optim getAnyRowModel(getRowSet())
			List<RowsModel> rowsModels = RowsModel.getActiveRowModels(getRowSet());
			if (rowsModels.isEmpty())
				throw new IllegalStateException("No RowsModel for rowSet");
			// Do the commit through any action.
			rowsModels.get(0).getAction(ACT_COMMIT).actionPerformed(null);
		}
		return true;
	}

} // end public class SSDataNavigator extends JPanel {

