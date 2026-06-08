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
package com.nqadmin.swingset.utils;

import java.awt.Container;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.datasources.DbOpsCustomizerImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSEnums.Navigation;

import static java.lang.System.Logger.Level.*;

/**
 * Helper class for designing SwingSet Form View screens.
 */
//public abstract class SSFormViewScreenHelper extends JInternalFrame {
@SuppressWarnings("serial")
public abstract class SSFormViewScreenHelper extends SSScreenHelperCommon {	

	/**
	 * Implementation of SSDBNav interface.
	 */
	private class FormHelperSSDBNavImpl extends DbOpsCustomizerImpl {
		
		/**
		 * Primary key of record being deleted.
		 */
		private Long pkOfDeletedRecord = null; 

		/**
		 * Constructor for implementation of SSDBNav interface.
		 *
		 * @param _container container in which to display messages for the data
		 *                   navigator
		 */
		public FormHelperSSDBNavImpl(final Container _container) {
			super(_container);
		}

		/**
		 * Perform checks to determine if the row can be deleted or not.
		 *
		 */
		@Override
		public boolean allowDeletion() {
			boolean allow;
			allow = ssDBNavAllowDeletion();
			return allow;
		}

		/**
		 * Perform checks to determine if all the data is valid and the row can be
		 * inserted.
		 */
		@Override
		public boolean allowInsertion() {
			boolean allow;
			allow = ssDBNavAllowInsertion();
			return allow;
		}

		/**
		 * Perform checks to determine if all the data is valid and the row can be
		 * updated.
		 */
		@Override
		public boolean allowUpdate() {
			boolean allow;
			allow = ssDBNavAllowUpdate();
			return allow;
		}

		/**
		 * Enable the record selector when cancel is pressed and perform any other actions
		 * specified.
		 *
		 * This is needed when cancel is pressed after add button is pressed.
		 */
		@Override
		public void performCancelOps() {
			logger.log(DEBUG, "");
			getComboNav().setEnabled(true);
			ssDBNavPerformCancelOps();
		}

		/**
		 * Close any child screens and performs any other developer
		 * specified actions following the specified record
		 * navigation.
		 *
		 * @param _navigationType Enum indicating type of navigation
		 */
		@Override
		public void performNavigationOps(final Navigation _navigationType) {
			logger.log(DEBUG, "");
			closeChildScreens();
			ssDBNavPerformNavigationOps(_navigationType);
			SwingUtilities.invokeLater(()->{
				try {
					activateDeactivateComponents();
				} catch (Exception e) {
					logger.log(Level.ERROR, e.getMessage(), e);
				}
			});
		}

		/**
		 * Deletes the appropriate entry from the combo navigator and performs
		 * any other developer specified actions following a record deletion.
		 */
		@Override
		public void performPostDeletionOps() {
			logger.log(DEBUG, "");
			if (requeryAfterInsertOrDelete) {
			// FOR SOME DATABASES LIKE H2, WE HAVE TO REQUERY THE ROWSET
				updateScreen();
			} else {
				getComboNav().removeKey(pkOfDeletedRecord);
			}
			ssDBNavPerformPostDeletionOps();
			pkOfDeletedRecord=null;
		}

		/**
		 * Adds the new record to the combo navigator, enables the combo navigator,
		 * and performs any other developer specified actions following a record insertion.
		 */
		@Override
		public void performPostInsertOps() {
			logger.log(DEBUG, "");
			if (requeryAfterInsertOrDelete) {
			// FOR SOME DATABASES LIKE H2, WE HAVE TO REQUERY THE ROWSET
				updateScreen();
			} else {
				addNewRecordToComboNav();
			}
			// 2020-11-25: DATA NAVIGATOR SHOULD TAKE CARE OF ENABLING COMBO NAVIGATOR
			ssDBNavPerformPostInsertOps();
		}

		/**
		 * Gets the primary key value for the record to be deleted
		 * and performs any other developer specified actions prior to a record deletion.
		 */
		@Override
		public void performPreDeletionOps() {
			logger.log(DEBUG, "");
			try {
				pkOfDeletedRecord = getRowsModel().getRowSet().getLong(getPkColumn());
			} catch (final SQLException se) {
				logger.log(Level.ERROR, "SQL Exception.", se);
				JOptionPane.showMessageDialog(container, "Database error while attempting to get ID of record to be deleted.");
			}
			ssDBNavPerformPreDeletionOps();
		}

		/**
		 * Disables the combo navigator, queries and sets the primary key value
		 * for the new record, and performs any other developer specified actions
		 * prior to a record insertion.
		 * 
		 * This is the only SSDBNav method where we want to call super.
		 */
		@Override
		public void performPreInsertOps() {
			logger.log(DEBUG, "");
			super.performPreInsertOps(); // THIS CALL RECURSIVELY CLEARS ALL OF THE COMPONENT VALUES
			// DATA NAVIGATOR SHOULD DISABLE COMBO NAVIGATOR
			//retrieveAndSetNewPrimaryKey();
			txtPrimaryKey.setText(retrieveNewPrimaryKey());
			try {
				setDefaultValues();
			} catch (final Exception e) {
				logger.log(Level.ERROR, "Exception.", e);
				JOptionPane.showMessageDialog(container, "Database error while setting default values for new record.");
			}
			ssDBNavPerformPreInsertOps();
		}

		/**
		 * Turns off the sync manager, updates any SSDBComboBoxes, and perform any other
		 * developer specified actions during a record refresh.
		 */
		@Override
		public void performRefreshOps() {
			logger.log(DEBUG, "");
			
			try {
				deactivateSyncManager();
				updateComboNav();
				updateSSDBComboBoxes();
				SwingUtilities.invokeLater(()->{
					try {
						activateDeactivateComponents();
					} catch (Exception e) {
						logger.log(Level.ERROR, e.getMessage(), e);
					}
				});
				ssDBNavPerformRefreshOps();
			} catch (final Exception e) {
				logger.log(Level.ERROR, "Exception.", e);
				JOptionPane.showMessageDialog(container, "Database error while refreshing record display.");
			} finally {
				activateSyncManager();
			}

		}
	}
	
	private static final Logger logger = SSUtils.getLogger();

	private SSDBComboBox comboNav; // Combo navigator.
	private String comboNavDisplayColumn1 = null; // name of the 1st database column to display in the combo navigator
	private String comboNavDisplayColumn2 = null; // name of the 2nd database column to display in the combo navigator
	private String comboNavSeparator = null; // character(s) used to separate the display of the 1st and 2nd columns  of the combo navigator
	
	private SSSyncManager syncManager; // SYNC DB NAV COMBO and NAVIGATION
	private SSDataNavigator dataNavigator; // Data navigator.
	
	private boolean requeryAfterInsertOrDelete = false; // for some databases like H2, you have to call .execute() on the rowset following insertion or deletion

	//private SSTextField txtParentID = new SSTextField(); // Always keep a SSTextField with the parent ID.
	private SSTextField txtPrimaryKey = new SSTextField(); // Always keep a SSTextField with the primary key.
	
	/**
	 * Constructs a Form View screen with the specified title and attaches it to the
	 * specified window.
	 *
	/**
	 * Constructs a Form View screen with the specified title and attaches it to the
	 * specified window.
	 *
	 * @param _title             title of window
	 * @param _parentContainer   parent window/container
	 * @param _connection        database connection
	 * @param _pkColumn          name of primary key column
	 * @param _parentID          primary key value of parent record (FK for current
	 *                           rowset), if applicable (can be null)
	 * @param _comboNavDisplayColumn1 name of the 1st database column to display in the
	 *                           combo navigator
	 */
	public SSFormViewScreenHelper(final String _title, final Container _parentContainer, final Connection _connection, final String _pkColumn,
			final Long _parentID, final String _comboNavDisplayColumn1) {
		
		this(_title, _parentContainer, _connection, _pkColumn, _parentID, _comboNavDisplayColumn1, null, null);
	}
	
	/**
	 * Constructs a Form View screen with the specified title and attaches it to the
	 * specified window.
	 *
	 * @param _title                  title of window
	 * @param _parentContainer        parent window/container
	 * @param _connection             database connection
	 * @param _pkColumn               name of primary key column
	 * @param _parentID               primary key value of parent record (FK for
	 *                                current rowset), if applicable
	 * @param _comboNavDisplayColumn1 name of the 1st database column to display in
	 *                                the combo navigator
	 * @param _comboNavDisplayColumn2 name of the 2nd database column to display in
	 *                                the combo navigator, if applicable
	 * @param _comboNavSeparator      character(s) used to separate the display of
	 *                                the 1st and 2nd columns of the combo
	 *                                navigator, if null, the default separator will
	 *                                be used
	 */
	@SuppressWarnings({"LeakingThisInConstructor", "OverridableMethodCallInConstructor"})
	public SSFormViewScreenHelper(final String _title, final Container _parentContainer, final Connection _connection, final String _pkColumn,
			final Long _parentID, final String _comboNavDisplayColumn1, final String _comboNavDisplayColumn2,
			final String _comboNavSeparator) {

		// CALL TO PARENT CONSTRUCTOR
		super(_title, true, true, false, true);

		try {
			// SET PARAMETERS
			setConnection(_connection);
			setParentContainer(_parentContainer);
			setPkColumn(_pkColumn);
			setParentID(_parentID);
			setComboNavDisplayColumn1(_comboNavDisplayColumn1);
			setComboNavDisplayColumn2(_comboNavDisplayColumn2);
			setComboNavSeparator(_comboNavSeparator);

			// SET SCREEN SIZE AND LOCATION
			setScreenSize();
			setDefaultScreenLocation();

		} catch (final Exception e) {
			logger.log(Level.ERROR, "Exception.", e);
			JOptionPane.showMessageDialog(this,
					"Error while constructing screen. Parent ID is: " + getParentID() + ".\n" + e.getMessage());
		}

	}
	
	/**
	 * Enables/disables screen components based on current values.
	 * 
	 * @throws Exception  exception thrown while activating/deactivating screen components
	 */
	protected abstract void activateDeactivateComponents() throws Exception;

	/**
	 * Create syncManager if it doesn't exist and sync cmbNavigator and
	 * navigate actions.
	 */
	private void activateSyncManager() {
		if (getSyncManager() == null) {
			setSyncManager(new SSSyncManager(getComboNav(), getRowsModel()));
			getSyncManager().setSyncColumnName(getPkColumn());
		}

		getSyncManager().sync();
	}

	/**
	 * Add components to screen.
	 * 
	 * @throws Exception exception thrown while adding screen components
	 */
	protected abstract void addComponents() throws Exception;

	/**
	 * Adds screen listeners.
	 * 
	 * @throws Exception exception thrown while adding core screen listeners
	 */
	@Override
	protected final void addCoreListeners() throws Exception {
		// ADD LISTENERS TO SAVE RECORD WHEN FORM LOSES FOCUS and TO CLOSE ANY CHILD
		// SCREENS IF FORM IS CLOSED
		addInternalFrameListener(new InternalFrameAdapter() {
			// CLOSE CHILD SCREENS WHEN THE SCREEN IS CLOSING
			@Override
			public void internalFrameClosing(final InternalFrameEvent ife) {
				closeChildScreens();
			}

			// UPDATE PRESENT ROW WHEN SCREEN LOOSES FOCUS
			@Override
			public void internalFrameDeactivated(final InternalFrameEvent ife) {
				getRowsModel().updatePresentRow();
			}
		});

		// ADD OTHER LISTENERS IN IMPLEMENTATION
		addCustomListeners();

	}

	/**
	 * Adds a newly inserted record to the combo navigator.
	 * <p>
	 * This can be empty if a call is made to {@code #setRequeryAfterInsertOrDelete(true);}
	 * because in that case the combo navigator will be re-queried.
	 */
	protected abstract void addNewRecordToComboNav();

	/**
	 * Initialize and bind screen components.
	 * 
	 * @throws Exception exception thrown while binding components
	 */
	protected abstract void bindComponents() throws Exception;

	/**
	 * Deactivate sync between cmbNavigator and navigate actions.
	 */
	private void deactivateSyncManager() {
		if (getSyncManager() != null) {
			getSyncManager().async();
		}
	}
	
	/**
	 * @return name of the 1st database column to display in the combo navigator
	 */
	protected String getCmbDisplayColumn1() {
		return comboNavDisplayColumn1;
	}

	/**
	 * @return name of the 2nd database column to display in the combo navigator
	 */
	protected String getCmbDisplayColumn2() {
		return comboNavDisplayColumn2;
	}
	
	/**
	 * @return character(s) used to separate the display of the 1st and 2nd columns  of the combo navigator
	 */
	protected String getCmbSeparator() {
		return comboNavSeparator;
	}

	/**
	 * @return the combo navigator
	 */
	protected SSDBComboBox getComboNav() {
		return comboNav;
	}

	/**
	 * Provides the full SQL query for the combo navigator.
	 * <p>
	 * Often the implementation may include a call to {@link #getParentID()} for filtering.
	 * <p>
	 * IMPORTANT: The query must return the same number of results and in the same order
	 * as {@link #getRowsetQuery()}
	 * 
	 * @return String with the full SQL query for the combo navigator
	 */
	protected abstract String getComboNavQuery();

	/**
	 * @return the dataNavigator
	 */
	protected SSDataNavigator getDataNavigator() {
		if (dataNavigator == null)
			dataNavigator = new SSDataNavigator(getRowsModel());
		return dataNavigator;
	}
	
	/**
	 * Indicates if .execute() has to be called on rowset following insertion or deletion. FALSE by default.
	 * 
	 * If TRUE, the appropriate method will be called to add the new record to the combo navigator following
	 * the commit of an inserted record and to remove the appropriate record from the combo navigator
	 * following a record deletion.
	 * 
	 * @return boolean indicating if .execute() has to be called on rowset following insertion or deletion
	 */
	protected boolean getRequeryAfterInsertOrDelete() {
		return requeryAfterInsertOrDelete;
	}

	/**
	 * @return the sync manager keeping the combo navigator and data navigator in sync
	 */
	protected SSSyncManager getSyncManager() {
		return syncManager;
	}
	
	/**
	 * @return the text field bound to the primary key column
	 */
	protected SSTextField getTxtPrimaryKey() {
		return txtPrimaryKey;
	}

	/**
	 * Initialize combo navigator and populate with database values.
	 *
	 * @throws SQLException SQL exception thrown while initializing the combo navigator
	 * @throws Exception exception thrown while initializing the combo navigator
	 */
	private void initComboNav() throws SQLException, Exception {

		setComboNav(new SSDBComboBox(getConnection(), getComboNavQuery(),
				getPkColumn(), getCmbDisplayColumn1()));

		if (getCmbDisplayColumn2() != null) {
			getComboNav().setSecondDisplayColumnName(getCmbDisplayColumn2());
		}

		if (getCmbSeparator()!= null) {
			getComboNav().setSeparator(getCmbSeparator());
		}
		
		getComboNav().execute();
	}

	/**
	 * Initialize rowset and loads sql query results
	 * 
	 * @throws SQLException exception thrown while initializing rowset
	 * @throws Exception exception thrown while initializing rowset
	 */
	private void initRowsModel() throws SQLException, Exception {
		RowSet rs = getNewRowSet(getConnection());
		rs.setCommand(getRowsetQuery());
		rs.execute();
		setRowsModel(RowsModel.create(rs, new FormHelperSSDBNavImpl(this)));
	}
	
	/**
	 * Performs post construction screen initialization.
	 */
	@Override
	protected void initScreen() {
		try {
			// INITIALIZE CENTRALLOOKUP (IF NEEDED)
			initializeCentralLookup(getConnection());

			// SETUP QUERY, DEFAULTS, and BUILD SCREEN
			// SET ROWSET QUERY
			// INITIALIZE NAVIGATION ACTIONS
			initRowsModel();
			
			// METADATA DEBUGGING
			logger.log(DEBUG, "rowset class = " + getRowsModel().getRowSet().getClass().getName());
			logger.log(DEBUG, "dataSourceName = " + getRowsModel().getRowSet().getDataSourceName());
			logger.log(DEBUG, "url = " + getRowsModel().getRowSet().getUrl());
			logger.log(DEBUG, "tableName = " + getRowsModel().getRowSet().getMetaData().getTableName(1));
			logger.log(DEBUG, "catalogName = " + getRowsModel().getRowSet().getMetaData().getCatalogName(1));
			logger.log(DEBUG, "conn catalog = " + getConnection().getCatalog());
			logger.log(DEBUG, "conn schema = " + getConnection().getSchema());
			
			// INITIALIZE COMBO NAVIGATOR
			initComboNav();
			
			// BIND PRIMARY KEY
			txtPrimaryKey.bind(getRowsModel(), getPkColumn());
			
			// ADD OPTIONS FOR ANY SSComboBoxes()
			populateSSComboBoxes();
			
			// INITIALIZE and BIND SCREEN COMPONENTS
			bindComponents();

			// ADD COMPONENTS TO SCREEN
			addComponents();
			
			// DISABLE & RIGHT JUSTIFY PRIMARY KEY
			getTxtPrimaryKey().setEnabled(false);
			getTxtPrimaryKey().setHorizontalAlignment(SwingConstants.RIGHT);

			// ADD MENU BAR TO THE SCREEN.
			setJMenuBar(getCustomMenu());

			// ADD/CONFIGURE TOOLBARS
			configureToolBars();

			// POPULATE/UPDATE DATA IN COMBO BOXES
			updateSSDBComboBoxes();

			// INITIALIZE AND TURN ON SYNC MANAGER
			activateSyncManager();

			// ACTIVATE/DEACTIVATE SCREEN COMPONENTS
			activateDeactivateComponents();
			
			// DISABLE PRIMARY KEY
			txtPrimaryKey.setEnabled(false);

			// ADD SCREEN LISTENERS
			addCoreListeners();
			
			// MAKE SCREEN VISIBLE
			// THIS SHOULD HAPPEN SEPARATELY FROM INITIALIZING OF SCREEN
//			showUp(getParentContainer());
				
		} catch (final SQLException se) {
			logger.log(Level.ERROR, "SQL Exception.", se);
			JOptionPane.showMessageDialog(this,
					"Database error while initializing screen. Parent ID is: " + getParentID() + ".\n" + se.getMessage());
		} catch (final Exception e) {
			logger.log(Level.ERROR, "Exception.", e);
			JOptionPane.showMessageDialog(this,
					"Error while initializing screen. Parent ID is: " + getParentID() + ".\n" + e.getMessage());
		}
	}
	
	/**
	 * Set the Options for any SSComboBoxes (not SSDBComboBoxes).
	 * <p>
	 * E.g.,
	 * {@code #mySSCombobox.setDisplayValues(Arrays.asList(new String[] { "Red", "Green", "Blue" }));}
	 */
	protected abstract void populateSSComboBoxes();
	
	/**
	 * Retrieve the primary key value for a new record.
	 * 
	 * @return String value of new primary key
	 */
	protected abstract String retrieveNewPrimaryKey();
	
	/**
	 * @param _comboNav the combo navigator to use for this screen/form
	 */
	private void setComboNav(final SSDBComboBox _comboNav) {
		comboNav = _comboNav;
	}

	/**
	 * @param _comboNavDisplayColumn1 name of the 1st database column to display in the combo navigator
	 */
	protected void setComboNavDisplayColumn1(final String _comboNavDisplayColumn1) {
		comboNavDisplayColumn1 = _comboNavDisplayColumn1;
	}

	/**
	 * @param _comboNavDisplayColumn2 name of the 2nd database column to display in the combo navigator
	 */
	protected void setComboNavDisplayColumn2(final String _comboNavDisplayColumn2) {
		comboNavDisplayColumn2 = _comboNavDisplayColumn2;
	}

	/**
	 * Updates the character(s) used to separate the first and second display columns for the
	 * combo navigator. If null, the SwingSet default is used.
	 * 
	 * @param _comboNavSeparator character(s) used to separate the display of the 1st and 2nd columns  of the combo navigator.
	 */
	protected void setComboNavSeparator(final String _comboNavSeparator) {
		if (_comboNavSeparator!=null) {
			comboNavSeparator = _comboNavSeparator;
		}
	}
	
	/**
	 * Sets boolean indicating if .execute() has to be called on rowset following insertion or deletion.
	 * 
	 * @param _requeryAfterInsertOrDelete boolean indicating if .execute() has to be called on rowset following insertion or deletion
	 */
	protected void setRequeryAfterInsertOrDelete(boolean _requeryAfterInsertOrDelete) {
		requeryAfterInsertOrDelete = _requeryAfterInsertOrDelete;
	}

	/**
	 * @param _syncManager the sync manager used to keep the selection combo and data navigator in sync 
	 */
	private void setSyncManager(final SSSyncManager _syncManager) {
		syncManager = _syncManager;
	}

	/**
	 * Perform any checks to determine if data can be deleted. If return value is
	 * true, then proceed with deletion. Otherwise cancel deletion. This method is
	 * called when the user clicks the 'delete' button or on a navigation event, or
	 * close event?
	 * 
	 * @return Boolean indicating if record deletions are allowed. True by default. Can be overridden.
	 */
	protected boolean ssDBNavAllowDeletion() {
		return true;
	}

	/**
	 * Perform any checks to determine if data can be inserted. If return value is
	 * true, then proceed with insertion. Otherwise cancel insertion. This method is
	 * called when the user clicks the 'save' button or on a navigation event, or
	 * close event?
	 * 
	 * @return Boolean indicating if record insertions are allowed. True by default. Can be overridden.	 
	 */
	protected boolean ssDBNavAllowInsertion() {
		return true;
	}

	/**
	 * Perform any checks to determine if the present row can be updated. If return
	 * value is true, then proceed with update. Otherwise cancel update. This method
	 * is called when the user clicks the 'save' button or on a navigation event, or
	 * close event?
	 * 
	 * @return Boolean indicating if record updates are allowed. True by default. Can be overridden.	 
	 */
	protected boolean ssDBNavAllowUpdate() {
		return true;
	}

	/**
	 * Perform any actions needed after the cancel/undo button is pressed.
	 *
	 * Helper enables record selector. NO ACTION REQUIRED.
	 */
	protected void ssDBNavPerformCancelOps() {}

	/**
	 * Perform any actions needed after the specified Navigation
	 * takes place.
	 * <p>
	 * This method can be overridden to handle re-querying of a sub-screen
	 * or SSDBComboBox upon each record navigation.
	 * <p>
	 * The helper manages closing of any child screens.
	 *
	 * @param _navigationType Enum indicating type of navigation
	 */
	protected void ssDBNavPerformNavigationOps(Navigation _navigationType) {}

	/**
	 * Perform any actions needed after a deletion.
	 *
	 * Helper deletes record.
	 */
	protected void ssDBNavPerformPostDeletionOps() {}

	/**
	 * Add the new record and perform any other actions needed after an insert.
	 *
	 * Helper manages enabling of record selector, but addition of record is
	 * required.
	 */
	protected void ssDBNavPerformPostInsertOps() {}

	/**
	 * Perform any actions needed before a deletion.
	 *
	 * Helper gets ID of record to be deleted.
	 */
	protected void ssDBNavPerformPreDeletionOps() {}

	/**
	 * Any actions required prior to an insert.
	 * <p>
	 * Helper takes care of disabling combo navigator, retrieving the primary key for
	 * the new records, and setting default values.
	 */
	protected void ssDBNavPerformPreInsertOps() {}

	/**
	 * Perform any actions needed after the refresh button is pressed.
	 *
	 * Helper manages synchronization of record selector and navigator, and updating
	 * of any SSDBComboBoxes.
	 */
	protected void ssDBNavPerformRefreshOps() {}

	/**
	 * Update/requery the combo navigator
	 *
	 * @throws Exception exception thrown while updating the combo navigator query
	 */
	private void updateComboNav() throws Exception {
		getComboNav().setQuery(getComboNavQuery());
		getComboNav().execute();
	}

	/**
	 * Updates the rowset, data navigator, combo navigator, and any DB comboboxes.
	 * 
	 * Generally the developer should call this when the entire screen should be refreshed due to
	 * a change in for foreign key used for the rowset (e.g., a navigation is performed on a parent
	 * screen of the current screen).
	 */
	@Override
	public void updateScreen() {

		try {
			// TURN OFF THE SYNC MANAGER
			deactivateSyncManager();

			// SET THE NEW QUERY FOR ROWSET
			updateRowset();

			// UPDATE THE COMBO NAVIGATOR
			updateComboNav();

			// UPDATE SELECTION CRITERIA FOR ANY OTHER SSDBCombos
			updateSSDBComboBoxes();

			// TURN ON SYNC MANAGER
			activateSyncManager();

			// ACTIVATE/DEACTIVATE SCREEN COMPONENTS
			SwingUtilities.invokeLater(()->{
				try {
					activateDeactivateComponents();
				} catch (Exception e) {
					logger.log(Level.ERROR, e.getMessage(), e);
				}
			});

		} catch (final SQLException se) {
			logger.log(Level.ERROR, "SQL Exception.", se);
			JOptionPane.showMessageDialog(this,
					"Database error while updating screen for parent ID: " + getParentID() + ".\n" + se.getMessage());
		} catch (final Exception e) {
			logger.log(Level.ERROR, "Exception.", e);
			JOptionPane.showMessageDialog(this,
					"Error while updating screen for parent ID: " + getParentID() + ".\n" + e.getMessage());
		}

	} 

}
