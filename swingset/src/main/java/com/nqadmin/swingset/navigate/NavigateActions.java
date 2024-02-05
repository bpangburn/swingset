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
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.navigate;

import com.nqadmin.swingset.*;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.sql.rowset.CachedRowSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSEnums.Navigation;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.navigate.NavAction.*;
import static com.nqadmin.swingset.navigate.RowSetState.setInserting;
import static com.nqadmin.swingset.navigate.Utils.getLocalEventBus;
import static com.nqadmin.swingset.navigate.RowSetState.setNavigateActions;

//TODO: Handle CachedRowSet Paging

// SSDataNavigator.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * {@linkplain NavigateActions} contains Actions and models use for RowSet
 * navigation and state; there is at most one set of Actions and models for a RowSet.
 * There are {@linkplain Action}s for navigation, insertion, and deletion of records
 * in a RowSet. There are {@linkplain ButtonModel}s for state, such as row dirty
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
 * 
 * There are various navigation management parameters that may be set.
 * - auto commit
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * Component that can be used for data navigation. It provides buttons for
 * navigation, insertion, and deletion of records in a RowSet. The
 * modification of a RowSet can be prevented using the setModificaton()
 * method. Any changes made to the columns of a record will be updated whenever
 * there is a navigation.
 * <p>
 * For example if you are displaying three columns using the JTextField and the
 * user changes the text in the text fields then the columns will be updated to
 * the new values when the user navigates the RowSet. If the user wants to
 * revert the changes he made he can press the Undo button, however this must be
 * done before any navigation. Once navigation takes place changes can't be
 * reverted using Undo button (has to be done manually by the user).
 */
public class NavigateActions
{
	// TODO: should this go into RowSetState?
	private static final Map<RowSet, NavigateActions> rowSetActions = new WeakHashMap<>();

	private final Map<NavAction, Action> actions;

	/**
	 * Rowset Listener on the RowSet used by data navigator.
	 */
	@SuppressWarnings("serial")
	class NavRowSetListener implements RowSetListener, Serializable
	{
		/**
		 * variables needed to consolidate multiple calls
		 */
		private int lastChange = 0;
		private int lastNotifiedChange = 0;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void cursorMoved(final RowSetEvent rse) {
			logger.trace("Rowset cursor moved.");
			performUpdates();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void rowChanged(final RowSetEvent rse) {
			logger.trace("Row changed.");
			performUpdates();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void rowSetChanged(final RowSetEvent rse) {
			logger.trace("Rowset changed.");
			// Update the record counts and navigator display following a navigation.
			try {
				logger.debug("Updating row count with last(), getRow(), and first().");
				rowSet.last();
				rowCount = rowSet.getRow();
				rowSet.first();
			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
			}
			performUpdates();

		}
		
		private void performUpdates() {
			lastChange++;
			logger.trace("performUpdates(): lastChange=" + lastChange
					+ ", lastNotifiedChange=" + lastNotifiedChange);
			
			// Delay execution of logic until all listener methods are called for current event
			// Based on: https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
			SwingUtilities.invokeLater(() -> {
				if (lastNotifiedChange != lastChange) {
					lastNotifiedChange = lastChange;

					try {
						logger.debug("Calling updateNavigator().");
						setRowModified(false);
						updateNavigator();
					} catch (final SQLException se) {
						logger.error("SQL Exception.", se);
					}				
				}
			});
		}

	}

	/**
	 * Log4j Logger for component
	 */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Indicator to cause the navigator to skip the execute() function call on the
	 * specified RowSet. Must be false for MySQL (see FAQ).
	 */
	private boolean callExecute = true;

	/**
	 * Indicator to force confirmation of RowSet deletions.
	 */
	private boolean confirmDeletes = true;
	
	/**
	 * Row number for current record in RowSet.
	 */
	private int currentRow = 0;

	/**
	 * Container (frame or internal frame) which contains the navigator.
	 */
	private SSDBNav dBNav = new SSDBNav() {};

	/**
	 * Indicator to allow/disallow deletions from the RowSet.
	 */
	private boolean deletion = true;

	/**
	 * NavGroup event bus.
	 */
	private EventBus eventBus;

	/**
	 * Indicator to allow/disallow insertions to the RowSet.
	 */
	private boolean insertion = true;

	/**
	 * Indicator that current row is dirty.
	 */
	private boolean isRowModified = false;

	/**
	 * Indicator to allow/disallow changes to the RowSet.
	 */
	private boolean modification = true;

	/**
	 * SSDBComboBox used for navigation if applicable.
	 * <p>
	 * Allows Navigator to disable it when a row is inserted and enable it when that row is saved.
	 * <p>
	 * TODO Consider writing a PropertyChangeListener for onInsertRow instead.
	 */
	private SSDBComboBox navCombo = null;

	/**
	 * Number of rows in RowSet. Set to zero if next() method returns false.
	 */
	private int rowCount = 0;

	/**
	 * RowSet from which component will get/set values.
	 */
	private RowSet rowSet = null;

	/**
	 * Listener on the RowSet used by data navigator.
	 */
	private final NavRowSetListener rowsetListener = new NavRowSetListener();
	
	/** Indicates if rowset listener is added (or removed) */
	private boolean rowsetListenerAdded = false;

	/** Listener on the rownumber spinner. */
	private final ChangeListener rownumberListener;
	
	/** Indicates if rownumber listener is added (or removed) */
	private boolean rownumberListenerAdded = false;

	//
	// TODO:
	//     For now, have the defaults here. In the future,
	//     probably want to set the defaults from some
	//     configurable spot, via CentralLookup?
	//     Maybe: interface SwingSetConfiguration {}
	//
	private static final boolean V3_BUTTONS_DEFAULT = false;
	private static final boolean AUTO_COMMIT_DEFAULT = false;

	private static NavigateActions dummy;
	private static NavigateActions dummy() {
		if (dummy == null)
			dummy = new NavigateActions(null);
		return dummy;
	}

	/**
	 * Return the navigate actions for the given {@linkplain RowSet}.
	 * @param rowSet rowSet
	 * @return actions
	 */
	public static NavigateActions get(RowSet rowSet)
	{
		if (rowSet == null)
			return dummy();
		// TODO: There's also RowSetState.[sg]etNavigateActions(). DON'T NEED BOTH
		return rowSetNavActions.computeIfAbsent(rowSet, (rs) -> new NavigateActions(rs));
	}

	/**
	 * Perform the specified undo/redo cmd on the specified component.
	 * @param comp
	 * @param cmd undo or redo
	 */
	public static void undoRedo(SSComponentInterface comp, UndoRedo cmd)
	{
		NavigateActions navActs = get(comp.getRowSet());
		navActs.undoRow.doUndoRedo(comp, cmd);
	}

	/**
	 * Constructs the SSDataNavigator with the given RowSet and sets the size of
	 * the buttons on the navigator to the given size
	 *
	 * @param _rowSet   the RowSet to which the navigator is bound to
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	NavigateActions(final RowSet _rowSet)
	{
		v3Buttons = V3_BUTTONS_DEFAULT;
		autoCommit = AUTO_COMMIT_DEFAULT;

		actions = new HashMap<>();
		actions.put(NAV_FIRST,		new NavFirstAction());
		actions.put(NAV_PREVIOUS,	new NavPreviousAction());
		actions.put(NAV_NEXT,		new NavNextAction());
		actions.put(NAV_LAST,		new NavLastAction());
		actions.put(NAV_COMMIT,		new NavCommitAction());
		actions.put(NAV_UNDO,		new NavUndoAction());
		actions.put(NAV_REFRESH,	new NavRefreshAction());
		actions.put(NAV_ADD,		new NavAddAction());
		actions.put(NAV_DELETE,		new NavDeleteAction());
		actions.put(NAV_GOTOROW,	new NavGotoRowAction());

		rowNumberModel = new SpinnerNumberModel(1, 1, 1, 1);

		// setSSRowSet will typically set the eventBus
		setupEventBus();

		if (_rowSet != null)
			setRowSet(_rowSet);

		rownumberListener = (ChangeEvent e)
				-> gotoRow((int) rowNumberModel.getNumber());
		addRownumberListener();
	}

	// TODO: Is it necessary to replace event bus when something changes?
	//       getLocalEventBus doesn't look at rowSet (or this for that matter)
	private void setupEventBus() {
		if (eventBus == null) {
			eventBus = getLocalEventBus(this, rowSet);
		}
		eventBus.register(new BusReceiver());
	}

	// TODO: also have Set<SSComponentInterface> modifiedComponents
	transient private final Set<SSComponentInterface> errorComponents = new HashSet<>();
	class BusReceiver {
		@Subscribe
		public void handleRowDataChanged(RowSetModificationEvent ev) {
			if (ev.matches(rowSet)) {
				// Our RowSet's row has changed

				// TODO what about ev.getSource == null ?
				if(ev.isError()) {
					errorComponents.add(ev.getSource());
				} else {
					errorComponents.remove(ev.getSource());
				}

				logger.trace(() -> ev.toString());
				setRowModified(true);
				updateActionState();
			}
		}
	}

	/**
	 * Builds a navigation ActionMap for this.
	 * 
	 * @return the action map with actions for the RowSet
	 */
	public final ActionMap createActionMap() {
		ActionMap actionMap = new ActionMap();
		actions.forEach((key, act) -> actionMap.put(key, act));
		return actionMap;
	}

	private final SpinnerNumberModel rowNumberModel;

	/**
	 * SpinnerModel for current row number.
	 * @return 
	 */
	public SpinnerNumberModel getRowNumberModel()
	{
		return rowNumberModel;
	}
	
	/**
	 * Action for the "First" button on the navigator.
	 */
	@SuppressWarnings("serial")
	private final class NavFirstAction extends AbstractAction
	{
		/**
		 * Constructor for the "First" button Action.
		 */
		public NavFirstAction() {
			super("First");
			putValue(LARGE_ICON_KEY, new ImageIcon(this.getClass().getClassLoader().getResource("images/first.gif")));
			putValue(SHORT_DESCRIPTION, "Navigate to First Record");
//	        putValue(MNEMONIC_KEY, mnemonic);
		}

		/**
		 * When the "first" button is pressed, the current record is saved and the user
		 * is taken to the first row in the rowset.
		 * 
		 * Since the rowset is on the first row, disable the "previous" button and
		 * enable the "next" button.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			logger.debug("FIRST button clicked.");
			removeRowsetListener();
			try {
				if (!commitChangesToDatabase(true))
					return;

				rowSet.first();

				setRowModified(false);
				updateNavigator();

				dBNav.performNavigationOps(Navigation.First);

			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
				JOptionPane.showMessageDialog(null, //NavigateActions.this,
						"Exception occured while updating row or moving the cursor.\n" + se.getMessage());
			} finally {
				addRowsetListener();
			}
		}
	} // end NavFirstAction

	/**
	 * Action for the "Previous" button on the navigator.
	 */
	@SuppressWarnings("serial")
	private final class NavPreviousAction extends AbstractAction
	{
		/**
		 * Constructor for the "Previous" button Action.
		 */
		public NavPreviousAction() {
			super("Previous");
			putValue(LARGE_ICON_KEY, new ImageIcon(this.getClass().getClassLoader().getResource("images/prev.gif")));
			putValue(SHORT_DESCRIPTION, "Navigate to Previous Record");
//	        putValue(MNEMONIC_KEY, mnemonic);
		}

		/**
		 * When the "previous" button is pressed, the current record is saved and the
		 * user is taken to the previous row in the rowset.
		 * 
		 * If there are records in the rowset and the move to the previous record fails,
		 * then the user is taken to the first row.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			logger.debug("PREVIOUS button clicked.");
			removeRowsetListener();
			try {
				if (!commitChangesToDatabase(true))
					return;

				if ((rowSet.getRow() != 0) && !rowSet.previous()) {
					rowSet.first();
				}

				setRowModified(false);
				updateNavigator();

				dBNav.performNavigationOps(Navigation.Previous);

			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
				JOptionPane.showMessageDialog(null, //NavigateActions.this,
						"Exception occured while updating row or moving the cursor.\n" + se.getMessage());
			} finally {
				addRowsetListener();
			}
		}
	} // end NavPreviousAction

	/**
	 * Action for the "Next" button on the navigator.
	 */
	private final class NavNextAction extends AbstractAction {

		private static final long serialVersionUID = 1L; // Unique ID

		/**
		 * Constructor for the "Next" button Action.
		 */
		public NavNextAction() {
			super("Next");
			putValue(LARGE_ICON_KEY, new ImageIcon(this.getClass().getClassLoader().getResource("images/next.gif")));
			putValue(SHORT_DESCRIPTION, "Navigate to Next Record");
//	        putValue(MNEMONIC_KEY, mnemonic);
		}

		/**
		 * When the "next" button is pressed, the current record is saved and the user
		 * is taken to the next row in the rowset.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			logger.debug("NEXT button clicked.");
			removeRowsetListener();
			try {
				if (!commitChangesToDatabase(true))
					return;

				rowSet.next();

				setRowModified(false);
				updateNavigator();

				dBNav.performNavigationOps(Navigation.Next);

			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
				JOptionPane.showMessageDialog(null, //NavigateActions.this,
						"Exception occured while updating row or moving the cursor.\n" + se.getMessage());
			} finally {
				addRowsetListener();
			}
		}
	} // end NavNextAction

	/**
	 * Action for the "Last" button on the navigator.
	 */
	private final class NavLastAction extends AbstractAction {

		private static final long serialVersionUID = 1L; // Unique ID

		/**
		 * Constructor for the "Last" button Action.
		 */
		public NavLastAction() {
			super("Last");
			putValue(LARGE_ICON_KEY, new ImageIcon(this.getClass().getClassLoader().getResource("images/last.gif")));
			putValue(SHORT_DESCRIPTION, "Navigate to Last Record");
//	        putValue(MNEMONIC_KEY, mnemonic);
		}

		/**
		 * When the "last" button is pressed, the current record is saved and the user
		 * is taken to the last row in the rowset.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			logger.debug("LAST button clicked.");
			removeRowsetListener();
			try {
				if (!commitChangesToDatabase(true))
					return;
				
				rowSet.last();
				
				setRowModified(false);
				updateNavigator();
				
				dBNav.performNavigationOps(Navigation.Last);
				
			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
				JOptionPane.showMessageDialog(null, //NavigateActions.this,
						"Exception occured while updating row or moving the cursor.\n" + se.getMessage());
			} finally {
				addRowsetListener();
			}
		}
	} // end NavLastAction

	/**
	 * Action for the "Commit" button on the navigator.
	 */
	private final class NavCommitAction extends AbstractAction {

		private static final long serialVersionUID = 1L; // Unique ID

		/**
		 * Constructor for the "Commit" button Action.
		 */
		public NavCommitAction() {
			super("Commit");
			putValue(LARGE_ICON_KEY, new ImageIcon(this.getClass().getClassLoader().getResource("images/commit.gif")));
			putValue(SHORT_DESCRIPTION, "Commit/Save Current Record");
//	        putValue(MNEMONIC_KEY, mnemonic);
		}

		/**
		 * When the "commit" button is pressed, any changes are committed to the database.
		 * 
		 * If the user is on the 'insert' row (new record) at the time of a commit,
		 * the record is inserted and the rowset is moved to the newly added/inserted row,
		 * and the other navigation buttons are re-enabled.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			logger.debug("COMMIT button clicked.");
			removeRowsetListener();
			try {
				if (RowSetState.isInserting(rowSet)) {
					// IF ON INSERT ROW ADD THE ROW.
					// CHECK IF THE ROW CAN BE INSERTED.
					if (!dBNav.allowInsertion()) {
						// WE DO NOTHING. THE ROWSET STAYS IN INSERT ROW. EITHER USER
						// HAS TO FIX THE DATA AND SAVE THE ROW OR CANCEL THE INSERTION.
						return;
					}
					
					RowSetOps.insertRow(rowSet);
					setInserting(rowSet, false);
					dBNav.performPostInsertOps();

					rowSet.last();

					rowCount = rowSet.getRow();
					
					setRowModified(false);
					updateNavigator();
				} else {
					// ELSE UPDATE THE DATABASE BASED ON THE PRESENT ROW VALUES.
					// IN THIS CASE WE WILL WAIT TO PERFORM POST-UPDATE OPS BELOW
					if (!commitChangesToDatabase(false))
						return;
				
					setRowModified(false);
					// TODO: why not updateNavigator?
					updateActionState();
					
					// 2020-11-24: Generally redundant, but force a refresh the screen with the
					// values from the rowset. This will be most noticeable if you have
					// two fields bound to the same column.
					//
					// rowSet.refreshRow() did not accomplish the intended result, but
					// navigating to the same row using absolute and the current record
					// number did.
					//
					// 2020-12-24
					// TODO: might get rid of this if broadcasting the right info,
					//       like picking up on the "other" component broadcast
					//

					rowSet.absolute(rowSet.getRow());
					
					//
					// TODO: if above cleaned up can remove following in favor
					//       of simpler commitChangesToDatabase() further above
					//
					dBNav.performPostUpdateOps();
				}

			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
				JOptionPane.showMessageDialog(null, //NavigateActions.this,
						"Exception occured while saving row.\n" + se.getMessage());
			} finally {
				addRowsetListener();
			}
		}
	} // end NavCommitAction

	/**
	 * Action for the "Undo" button on the navigator.
	 */
	private final class NavUndoAction extends AbstractAction {

		private static final long serialVersionUID = 1L; // Unique ID

		/**
		 * Constructor for the "Undo" button Action.
		 */
		public NavUndoAction() {
			super("Undo");
			putValue(LARGE_ICON_KEY, new ImageIcon(this.getClass().getClassLoader().getResource("images/undo.gif")));
			putValue(SHORT_DESCRIPTION, "Undo/Revert Changes to Current Record");
//	        putValue(MNEMONIC_KEY, mnemonic);
		}

		/**
		 * When the "undo" button is pressed, revert any changes to the current record.
		 * 
		 * This button can also be used to cancel an insertion so if on the insert row,
		 * re-enable the other navigation buttons.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			logger.debug("UNDO button clicked.");
			removeRowsetListener();
			try {
				// CALL MOVE TO CURRENT ROW IF ON INSERT ROW.
				if (RowSetState.isInserting(rowSet)) {
					rowSet.moveToCurrentRow();
				}

				// THIS FUNCTION IS NOT NEED IF ON INSERT ROW
				// BUT MOVETOINSERTROW WILL NOT TRIGGER ANY EVENT SO FOR THE SCREEN
				// TO UPDATE WE NEED TO TRIGGER SOME THING.
				// SINCE USER IS MOVED TO CURRENT ROW PRIOR TO INSERT IT IS SAFE TO
				// CALL CANCELROWUPDATE TO GET A TRIGGER

				// TODO: could cleanup/remove above comment and use issueRowChanged.

				rowSet.cancelRowUpdates();
				if (rowSet instanceof CachedRowSet) {
					// TODO: if there are RowSet listeners outside of the nav
					//		 container, they won't be signaled. Could set something
					//		 up, but wait for the event bus...
					dBNav.findSSComponents().forEach(
							(ssc) -> ssc.getSSCommon().issueRowChanged());
				}
				setInserting(rowSet, false);
				dBNav.performCancelOps();
				
				// Only attempt to refresh row if we have at least one record
				if (rowSet.getRow() > 0) {
					rowSet.refreshRow();
				}
				
				setRowModified(false);
				updateNavigator();
				
			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
				JOptionPane.showMessageDialog(null, //NavigateActions.this,
						"Exception occured while undoing changes.\n" + se.getMessage());
			} finally {
				addRowsetListener();
			}
		}
	} // end NavUndoAction

	/**
	 * Action for the "Refresh" button on the navigator.
	 */
	private final class NavRefreshAction extends AbstractAction {

		private static final long serialVersionUID = 1L; // Unique ID

		/**
		 * Constructor for the "Refresh" button Action.
		 */
		public NavRefreshAction() {
			super("Refresh");
			putValue(LARGE_ICON_KEY, new ImageIcon(this.getClass().getClassLoader().getResource("images/refresh.gif")));
			putValue(SHORT_DESCRIPTION, "Refresh/Reload Current Record");
//	        putValue(MNEMONIC_KEY, mnemonic);
		}

		/**
		 * When the "refresh" button is pressed, re-fetch all rows from the database
		 * and move the cursor to the first record. If there are no records, disable
		 * the other navigation buttons. Disable the first and previous buttons
		 * regardless because the rowset will be on the first record.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			logger.debug("REFRESH button clicked.");
			removeRowsetListener();
			try {
				if (callExecute) {
					rowSet.execute();
					
					if (!rowSet.next()) {
						// THERE ARE NO RECORDS IN THE ROWSET
						rowCount = 0;
					} else {
						// WE HAVE ROWS GET THE ROW COUNT AND MOVE BACK TO FIRST ROW
						rowSet.last();
						rowCount = rowSet.getRow();
						rowSet.first();
					}
					
					setRowModified(false);
					updateNavigator();
				}

				dBNav.performRefreshOps();
				
			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
				JOptionPane.showMessageDialog(null, //NavigateActions.this,
						"Exception occured refreshing the data.\n" + se.getMessage());
			} finally {
				addRowsetListener();
			}
		}
	} // end NavRefreshAction

	/**
	 * Action for the "Add" button on the navigator.
	 */
	private final class NavAddAction extends AbstractAction {

		private static final long serialVersionUID = 1L; // Unique ID

		/**
		 * Constructor for the "Add" button Action.
		 */
		public NavAddAction() {
			super("Add");
			putValue(LARGE_ICON_KEY, new ImageIcon(this.getClass().getClassLoader().getResource("images/add.gif")));
			putValue(SHORT_DESCRIPTION, "Add a New Record");
//	        putValue(MNEMONIC_KEY, mnemonic);
		}

		/**
		 * When the "add" button is pressed, move to the insert row and
		 * disable other navigation buttons except for Commit and Undo (cancel).
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			logger.debug("ADD button clicked.");
			removeRowsetListener();
			try {
				// Commit changes for current row to database
				commitChangesToDatabase(true);

				// Move to insert row, update status, and update combo navigator (if applicable)
				rowSet.moveToInsertRow();
				setInserting(rowSet, true);
				if (navCombo!=null) {
					navCombo.setEnabled(false);
				}

				// If we don't use invokeLater() here,
				// the values from the just-committed prior record
				// are displayed for the insert row.
				SwingUtilities.invokeLater(() -> dBNav.performPreInsertOps());
				
				updateActionState();
				
			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
				JOptionPane.showMessageDialog(null, //NavigateActions.this,
						"Exception occured while moving to insert row.\n" + se.getMessage());
			} finally {
				addRowsetListener();
			}
		}
	} // end NavAddAction

	/**
	 * Action for the "Delete" button on the navigator.
	 */
	private final class NavDeleteAction extends AbstractAction {

		private static final long serialVersionUID = 1L; // Unique ID

		/**
		 * Action for the "Delete" button Action.
		 */
		public NavDeleteAction() {
			super("Delete");
			putValue(LARGE_ICON_KEY, new ImageIcon(this.getClass().getClassLoader().getResource("images/delete.gif")));
			putValue(SHORT_DESCRIPTION, "Delete the Current Record");
//	        putValue(MNEMONIC_KEY, mnemonic);
		}

		/**
		 * When the "delete" button is pressed, delete the current row and move
		 * to the next row. If the deleted row is the last row then move the the
		 * record that was previous to the deleted row/record.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			logger.debug("DELETE button clicked.");
			removeRowsetListener();
			try {
				if (confirmDeletes) {
					final int answer = JOptionPane.showConfirmDialog(null, //NavigateActions.this,
							"Are you sure you want to delete this record?", "Delete Present Record",
							JOptionPane.YES_NO_OPTION);
					if (answer != JOptionPane.YES_OPTION) {
						return;
					}
				}

				if (!dBNav.allowDeletion()) {
					return;
				}
				
				// CAPTURE CURRENT ROW PRE-DELETION
				final int tmpPosition = currentRow;
				
				// SET ANTICIPATED ROW COUNT POST-DELETION
				final int tmpSize = rowCount-1;
				
				// PERFORM ANY PRE DELETION OPS
				dBNav.performPreDeletionOps();
				
				// DELETE ROW FROM ROWSET
				RowSetOps.deleteRow(rowSet);
				
				// PERFORM ANY POST DELETION OPS (WHICH MAY INVOLVE REQUERYING WHICH IS NEEDED FOR H2)
				dBNav.performPostDeletionOps();
				
				// UPDATE TOTAL ROW COUNT
				rowCount=tmpSize;
				
				// TRY TO NAVIGATE TO THE RECORD AFTER THE DELETED RECORD, OTHERWISE GO TO
				// WHATEVER IS THE LAST RECORD
				if ((tmpPosition <= rowCount) && (tmpPosition > 0)) {
					rowSet.absolute(tmpPosition);
				} else {
					rowSet.last();
				}
				
				setRowModified(false);
				// UPDATE THE STATUS OF THE NAVIGATOR
				updateNavigator();
				
			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
				JOptionPane.showMessageDialog(null, //NavigateActions.this,
						"Exception occured while deleting row.\n" + se.getMessage());
			} finally {
				addRowsetListener();
			}
		}
	} // end NavDeleteAction

	/** Fake action used with gotoRow used only for enable/disable. */
	@SuppressWarnings("serial")
	private final class NavGotoRowAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new IllegalStateException("NavGotoRowAction called");
		}
	}

	/**
	 * Make the specified row the current row.
	 * @param row goto this row
	 */
	public void gotoRow(int row)
	{
		Action gotoRowAction = actions.get(NAV_GOTOROW);
		if (!gotoRowAction.isEnabled())
			return;
		
		removeRowsetListener();
		try {
			
			if (!commitChangesToDatabase(true))
				return;
			
			logger.debug("Record number manually updated to " + row + ".");
			if ((row <= rowCount) && (row > 0)) {
				rowSet.absolute(row);
			}
			
			updateNavigator();
		} catch (final SQLException se) {
			logger.error("SQL Exception.", se);
			JOptionPane.showMessageDialog(null, //NavigateActions.this,
					"Exception occured while going to row.\n" + se.getMessage());
		} finally {
			addRowsetListener();
		}
	}
	
	/**
	 * Common code to commit changes to the database from the rowset if
	 * modifications are allowed. After committing, it performs any
	 * post-update operations.
	 * <p>
	 * If modification==false, then skip the update and return as
	 * successful, unless we have an empty rowset. 
	 * 
	 * @param _performPostUpdateOps true if performPostUpdateOps() should
	 * 	be called after successful update, otherwise false
	 * 
	 * @return true unless there are no records OR dBNav.allowUpdate() returns false
	 * @throws SQLException SQL Exception if rowset call to updateRow() fails
	 */
	private boolean commitChangesToDatabase(final boolean _performPostUpdateOps) throws SQLException {
		
		boolean result = true;
		
		// check for an empty rowset 
		if (rowSet.getRow() == 0) {
			result = false;
		}
		
		// if we have at least one row and the user has not set modifications to false (read-only)
		// then continue to attempt to update database based on current rowset values
		if (result && modification) {
			if (!dBNav.allowUpdate()) {
				result = false;
			} else {
				RowSetOps.updateRow(rowSet);
			}
		}
		
		// if update was successful or modifications are set to false (read-only) then
		// attempt post-update operations based on method parameter
		//
		// note that where modifications are set to false, we're pretending to have
		// successfully updated the database
		if (result && _performPostUpdateOps) {
			dBNav.performPostUpdateOps();
		}
		
		// return
		return result;
	}

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
		return RowSetState.isInserting(rowSet);
	}
	
	/**
	 * Adds listener to the rownumber spinner
	 */
	private void addRownumberListener() {
		if (!rownumberListenerAdded) {
			rowNumberModel.addChangeListener(rownumberListener);
			rownumberListenerAdded = true;
			logger.debug("RownumberListener is ON.");
		}
	}
	
	/**
	 * Removes listener from the rownumber spinner
	 */
	private boolean removeRownumberListener() {
		boolean isRemoved = false;
		if (rownumberListenerAdded) {
			rowNumberModel.removeChangeListener(rownumberListener);
			isRemoved = true;
			rownumberListenerAdded = false;
			logger.debug("RownumberListener is OFF.");
		}
		return isRemoved;
	}
	
	/**
	 * Adds listener to the rowset
	 */
	private void addRowsetListener() {
		if (!rowsetListenerAdded) {
			rowSet.addRowSetListener(rowsetListener);
			rowsetListenerAdded = true;
			logger.debug("RowsetListener is ON.");
		}
	}
	
	/**
	 * Removes listener from the rowset
	 */
	private void removeRowsetListener() {
		if (rowsetListenerAdded) {
			rowSet.removeRowSetListener(rowsetListener);
			rowsetListenerAdded = false;
			logger.debug("RowsetListener is OFF.");
		}
	}

	/**
	 * Method to cause the navigator to skip the execute() function call on the
	 * underlying RowSet. This is necessary for MySQL (see FAQ).
	 *
	 * @param _callExecute false if using MySQL database - otherwise true
	 */
	public void setCallExecute(final boolean _callExecute) {
		//final boolean oldValue = callExecute;
		callExecute = _callExecute;

		// TODO: what is this about?
		// firePropertyChange("callExecute", oldValue, callExecute);
	}

	/**
	 * Indicates if the navigator will skip the execute function call on the
	 * underlying RowSet (needed for MySQL - see FAQ).
	 *
	 * @return value of execute() indicator
	 */
	public boolean getCallExecute() {
		return callExecute;
	}

	/**
	 * Sets the confirm deletion indicator. If set to true, every time delete button
	 * is pressed, the navigator pops up a confirmation dialog to the user. Default
	 * value is true.
	 *
	 * @param _confirmDeletes indicates whether or not to confirm deletions
	 */
	public void setConfirmDeletes(final boolean _confirmDeletes) {
		//final boolean oldValue = confirmDeletes;
		confirmDeletes = _confirmDeletes;
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
	 * Function that passes the implementation of the SSDBNav interface. This
	 * interface can be implemented by the developer to perform custom actions when
	 * the insert button is pressed
	 *
	 * @param _dBNav implementation of the SSDBNav interface
	 */
	//TODO: does this belong here
	public void setDBNav(final SSDBNav _dBNav) {
		//final SSDBNav oldValue = dBNav;
		dBNav = _dBNav != null ? _dBNav : new SSDBNav() {};
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

	/**
	 * Enables or disables the row deletion button. This method should be used if
	 * row deletions are not allowed. True by default.
	 *
	 * @param _deletion indicates whether or not to allow deletions
	 */
	public void setDeletion(final boolean _deletion) {
		//final boolean oldValue = deletion;
		deletion = _deletion;
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
	 * @param _insertion indicates whether or not to allow insertions
	 */
	public void setInsertion(final boolean _insertion) {
		//final boolean oldValue = insertion;
		insertion = _insertion;
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
	 * @param _modification indicates whether or not the modification-related
	 *                      buttons are enabled.
	 */
	public void setModification(final boolean _modification) {
		//final boolean oldValue = modification;
		modification = _modification;
		// TODO: what is this about?
		//firePropertyChange("modification", oldValue, modification);

		updateActionState();
	}

	/**
	 * Returns true if the user can modify the data in the RowSet, else false.
	 *
	 * @return returns true if the user modifications are written back to the
	 *         database, else false.
	 */
	public boolean getModification() {
		return modification;
	}

	/**
	 * @param _navCombo the navCombo to set
	 */
	public void setNavCombo(final SSDBComboBox _navCombo) {
		navCombo = _navCombo;
	}

	/**
	 * @return the navCombo
	 */
	// TODO: what's this about
	public SSDBComboBox getNavCombo() {
		return navCombo;
	}

	/**
	 * Sets the RowSet for the navigator.
	 *
	 * @param _rowSet data source for navigator
	 */
	public final void setRowSet(final RowSet _rowSet)
	{
		Objects.requireNonNull(_rowSet);

		rowSetActions.put(rowSet, this);

		// RESET INSERT FLAG THIS IS NEED IF USERS LEFT THE LAST ROWSET
		// IN INSERTION MODE WITH OUT SAVING THE RECORD OR UNDOING THE INSERTION
		setInserting(rowSet, false);

		// REMOVE ROWSET LISTENER
		if (rowSet != null) {
			removeRowsetListener();
		}

		//final RowSet oldValue = rowSet;
		rowSet = _rowSet;
		// TODO: what is this about?
		//firePropertyChange("rowSet", oldValue, rowSet);

		setupEventBus();

		// SEE IF THERE ARE ANY ROWS IN THE GIVEN SSROWSET
		try {
			// TODO: this is dbms/app specific
			if (callExecute) {
				rowSet.execute();
			}

			if (!rowSet.next()) {
				rowCount = 0;
				currentRow = 0;
			} else {
				// IF THERE ARE ROWS GET THE ROW COUNT
				rowSet.last();
				rowCount = rowSet.getRow();
				rowSet.first();
				currentRow = rowSet.getRow();
			}
		} catch (final SQLException se) {
			logger.error("SQL Exception.", se);
		}
		
		// ADD ROWSET LISTENER
		addRowsetListener();

		try {
			setRowModified(false);
			setNavigateActions(rowSet, this);
			updateNavigator();
		} catch (final SQLException se) {
			logger.error("SQL Exception.", se);
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
		// // 2019-11-11: only enabling add/delete if this.modification==true
		// refreshButton.setEnabled(true);
		// if (insertion && modification) {
		// 	addButton.setEnabled(true);
		// }
		// if (deletion && modification) {
		// 	deleteButton.setEnabled(true);
		// }

	}

	/**
	 * Returns the RowSet being used.
	 *
	 * @return returns the RowSet being used.
	 */
	public RowSet getRowSet() {
		return rowSet;
	}

	private void setRowModified(boolean isDirty) {
		isRowModified = isDirty;
		if (!isDirty) {
			errorComponents.clear();
		}
	}
	
	private void updateEnable(NavAction navAction, boolean _flag) {
		Action act = actions.get(navAction);
		if(act.isEnabled() != _flag) {
			act.setEnabled(_flag);
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
	 * always enabled, independent of whether or not there is a modification.
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
	private void updateActionState() {
		logger.trace(() -> String.format("rowCount=%d, currentRow=%d", rowCount, currentRow));

		boolean onInsertRow = RowSetState.isInserting(rowSet);
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

		updateEnable(NAV_FIRST, canNavigate && !atFirst);
		updateEnable(NAV_PREVIOUS, canNavigate && !atFirst);
		updateEnable(NAV_NEXT, canNavigate && !atLast);
		updateEnable(NAV_LAST, canNavigate && !atLast);
		updateEnable(NAV_GOTOROW, canNavigate);

		// Handle commit, undo
		boolean commitUndoOk = (onInsertRow || isRowModified || commitUndoAlwaysEnabled) && modification;
		updateEnable(NAV_COMMIT, commitUndoOk  && !hasError);
		updateEnable(NAV_UNDO, commitUndoOk);

		// TODO: Consider if row is dirty, delete button makes sense,
		//			but, does the add button make sense?
		// Handle add, delete
		if (onInsertRow) {
			updateEnable(NAV_ADD, false);
			updateEnable(NAV_DELETE, false);
		} else {
			// Perhaps the following should only be "!isRowModified"
			updateEnable(NAV_ADD, insertion && !disablingAutoCommit && modification);
			updateEnable(NAV_DELETE, deletion && modification && rowCount != 0);
		}

		// refresh
		updateEnable(NAV_REFRESH, !onInsertRow && !disablingAutoCommit);
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
			if (rowSet.isLast()) {
				updateEnable(NAV_NEXT, false);
				updateEnable(NAV_LAST, false);
			}
			if (rowSet.isFirst()) {
				updateEnable(NAV_FIRST, false);
				updateEnable(NAV_PREVIOUS, false);
			}
		} catch (SQLException ex) {
			logger.error("SQL Exception.", ex);
		}
	}

	/**
	 * Enables/disables navigation buttons as needed
	 * and updates the current row and row count numbers.
	 * @throws SQLException 	SQLException
	 */
	void updateNavigator() throws SQLException {

		currentRow = rowSet.getRow();

		boolean isRemoved = false;
		rowNumberModel.setMaximum(rowCount);
		try {
			isRemoved = removeRownumberListener();
			rowNumberModel.setValue(currentRow);
		} finally {
			if (isRemoved)
				addRownumberListener();
		}

		logger.debug("Current Row: " + currentRow + ". Row Count: " + rowCount);
		//logger.debug("Stack trace:", new Throwable());

		updateActionStateWithDatabaseCheck();
	}

	/**
	 * Writes the present row back to the RowSet. This is done automatically when
	 * any navigation takes place, but can also be called manually.
	 *
	 * @return returns true if update succeeds else false.
	 */
	// TODO: better name
	public boolean updatePresentRow() {
		if (RowSetState.isInserting(rowSet) || (currentRow > 0)) {
			logger.debug("Doing NAV_COMMIT.");
			actions.get(NAV_COMMIT).actionPerformed(null);
		}

		return true;
	}

} // end public class SSDataNavigator extends JPanel {

