/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.rowset.JdbcRowSetImpl;
import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSTextField;
import java.sql.Connection;
import com.nqadmin.swingset.utils.SSEnums.Navigation;

//SSFormViewScreenHelper.java
//
//SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Helper class for designing SwingSet Form View screens.
 */
//public abstract class SSFormViewScreenHelper extends JInternalFrame {
public abstract class SSFormViewScreenHelper extends SSScreenHelperCommon {	

	/**
	 * Implementation of SSDBNav interface.
	 */
	private class FormHelperSSDBNavImpl extends SSDBNavImpl {

		/**
		 * unique serial ID
		 */
		private static final long serialVersionUID = 3470473198734864411L;
		
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
			logger.debug("");
			// super.performCancelOps();
			getCmbNavigator().setEnabled(true);
			ssDBNavPerformCancelOps();
			//getSyncManager().sync();
		}

		/**
		 * Close any child screens and performs any other developer
		 * specified actions following the specified record
		 * navigation.
		 *
		 * @param _navigationType Enum indicating type of navigation
		 */
		@Override
		//public void performNavigationOps(final int _navigationType) {
		public void performNavigationOps(final Navigation _navigationType) {
			logger.debug("");
			//super.performNavigationOps(_navigationType);
			closeChildScreens();
			ssDBNavPerformNavigationOps(_navigationType);
		}

		/**
		 * Deletes the appropriate entry from the combo navigator and performs
		 * any other developer specified actions following a record deletion.
		 */
		@Override
		public void performPostDeletionOps() {
			logger.debug("");
			//super.performPostDeletionOps();
			if (requeryAfterInsertOrDelete) {
			// FOR SOME DATABASES LIKE H2, WE HAVE TO REQUERY THE ROWSET
// TODO Check this....				
				updateScreen(getParentID());
			} else {
				getCmbNavigator().deleteItem(pkOfDeletedRecord);
			}
			ssDBNavPerformPostDeletionOps();
			pkOfDeletedRecord=null; 
			//getSyncManager().sync();
		}

		/**
		 * Adds the new record to the combo navigator, enables the combo navigator,
		 * and performs any other developer specified actions following a record insertion.
		 */
		@Override
		public void performPostInsertOps() {
			logger.debug("");
			//super.performPostInsertOps();
			if (requeryAfterInsertOrDelete) {
			// FOR SOME DATABASES LIKE H2, WE HAVE TO REQUERY THE ROWSET
// TODO Check this....				
				updateScreen(getParentID());
			} else {
				addNewRecordToCmbNavigator();
			}
			// 2020-11-25: DATA NAVIGATOR SHOULD TAKE CARE OF ENABLING COMBO NAVIGATOR
			//getCmbNavigator().setEnabled(true);
			ssDBNavPerformPostInsertOps();
			//getSyncManager().sync();
		}

		/**
		 * Gets the primary key value for the record to be deleted
		 * and performs any other developer specified actions prior to a record deletion.
		 */
		@Override
		public void performPreDeletionOps() {
			logger.debug("");
			//getSyncManager().async();
			//super.performPreDeletionOps();
			try {
				pkOfDeletedRecord = getRowset().getLong(getPkColumn());
			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
				JOptionPane.showMessageDialog(container, "Database error while attempting to get ID of record to be deleted.");
			}
			ssDBNavPerformPreDeletionOps();
		}

		/**
		 * Disables the combo navigator, queries and sets the primary key value
		 * for the new record, and performs any other developer specified actions
		 * prior to a record insertion.
		 */
		@Override
		public void performPreInsertOps() {
			logger.debug("");
			//getSyncManager().async();
			super.performPreInsertOps(); // THIS CALL RECURSIVELY CLEARS ALL OF THE COMPONENT VALUES
			// DATA NAVIGATOR SHOULD DISABLE COMBO NAVIGATOR
			//getCmbNavigator().setEnabled(false);
			retrieveAndSetNewPrimaryKey();
			try {
				setDefaultValues();
			} catch (final Exception e) {
				logger.error("Exception.", e);
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
			logger.debug("");
			//super.performRefreshOps();
			getSyncManager().async();
			try {
				updateCmbNavigatorData();
			} catch (final Exception e) {
				logger.error("Exception.", e);
				JOptionPane.showMessageDialog(container, "Database error while refreshing record display.");
			}
			updateSSDBComboBoxes();
			// TODO: call activateDeactivateComponents() ??
			ssDBNavPerformRefreshOps();
			getSyncManager().sync();
		}

	}
	
	private static Logger logger = LogManager.getLogger(); // Log4j Logger for component

	private static final long serialVersionUID = 266766406708536384L; // unique serial ID
	
	private SSDBComboBox cmbNavigator; // Combo navigator.
	private SSDataNavigator dataNavigator; // Data navigator.
	private SSSyncManager syncManager; // SYNC DB NAV COMBO and NAVIGATOR
	
	private String cmbNavigatorFullSQL = null; // Full SQL for the combo navigator
	private String cmbNavigatorSelectSQL = null; // SELECT CLAUSE FOR RECORDSET SUCH THAT selectSQL + parentID + " " + orderBySQL IS A VALID QUERY
	
	private String cmbDisplayColumn1 = null; // name of the 1st database column to display in the combo navigator
	private String cmbDisplayColumn2 = null; // name of the 2nd database column to display in the combo navigator
	private String cmbSeparator = null; // character(s) used to separate the display of the 1st and 2nd columns  of the combo navigator
	
	private boolean requeryAfterInsertOrDelete = false; // for some databases like H2, you have to call .execute() on the rowset following insertion or deletion

	//private SSTextField txtParentID = new SSTextField(); // Always keep a SSTextField with the parent ID.
	private SSTextField txtPrimaryKey = new SSTextField(); // Always keep a SSTextField with the primary key.

	/**
	 * Constructs a Form View screen with the specified title and attaches it to the
	 * specified window.
	 *
	 * @param _title     	title of window
	 * @param _connection database connection
	 * 
	 * @param _pkColumn  	name of primary key column
	 * @param _parentID 	primary key value of parent record (FK for current rowset)
	 * 
	 * @param _fullSQL 		the full SQL to set for the rowset
	 *  
 	 * @param _cmbDisplayColumn1 name of the 1st database column to display in the combo navigator
	 * @param _cmbDisplayColumn2 name of the 2nd database column to display in the combo navigator
	 * @param _cmbSeparator character(s) used to separate the display of the 1st and 2nd columns  of the combo navigator
	 * 
	 * @param _cmbNavigatorFullSQL the full SQL query to set for the combo navigator
	 */
	public SSFormViewScreenHelper(final String _title, final Connection _connection, final String _pkColumn,
			final Long _parentID, final String _fullSQL, final String _cmbDisplayColumn1, final String _cmbDisplayColumn2,
			final String _cmbSeparator, final String _cmbNavigatorFullSQL) {
		
		this(_title, _connection, _pkColumn, _parentID, _fullSQL, null, null, _cmbDisplayColumn1, _cmbDisplayColumn2,
				_cmbSeparator, _cmbNavigatorFullSQL, null);
	}
	
	/**
	 * Constructs a Form View screen with the specified title and attaches it to the
	 * specified window.
	 *
	 * @param _title     	title of window
	 * @param _connection database connection
	 * 
	 * @param _pkColumn  	name of primary key column
	 * @param _parentID 	primary key value of parent record (FK for current rowset)
	 * 
	 * @param _selectSQL 	the SELECT clause of the SQL to set  for the rowset
	 * @param _orderBySQL 	the ORDER BY clause of the SQL to set for for the rowset
	 * 
 	 * @param _cmbDisplayColumn1 name of the 1st database column to display in the combo navigator
	 * @param _cmbDisplayColumn2 name of the 2nd database column to display in the combo navigator
	 * @param _cmbSeparator character(s) used to separate the display of the 1st and 2nd columns  of the combo navigator
	 * 
	 * @param _cmbNavigatorSelectSQL the SELECT clause to set for the combo navigator
	 */
	public SSFormViewScreenHelper(final String _title, final Connection _connection, final String _pkColumn,
			final Long _parentID, final String _selectSQL, final String _orderBySQL, final String _cmbDisplayColumn1, final String _cmbDisplayColumn2,
			final String _cmbSeparator, final String _cmbNavigatorSelectSQL) {
		
		this(_title, _connection, _pkColumn, _parentID, null, _selectSQL, _orderBySQL, _cmbDisplayColumn1, _cmbDisplayColumn2,
				_cmbSeparator, null, _cmbNavigatorSelectSQL);
	}
	
	/**
	 * Constructs a Form View screen with the specified title and attaches it to the
	 * specified window.
	 *
	 * @param _title     	title of window
	 * @param _connection database connection
	 * 
	 * @param _pkColumn  	name of primary key column
	 * @param _parentID 	primary key value of parent record (FK for current rowset)
	 * 
	 * @param _fullSQL 		the full SQL to set for the rowset
	 * @param _selectSQL 	the SELECT clause of the SQL to set  for the rowset
	 * @param _orderBySQL 	the ORDER BY clause of the SQL to set for for the rowset
	 * 
 	 * @param _cmbDisplayColumn1 name of the 1st database column to display in the combo navigator
	 * @param _cmbDisplayColumn2 name of the 2nd database column to display in the combo navigator
	 * @param _cmbSeparator character(s) used to separate the display of the 1st and 2nd columns  of the combo navigator
	 * 
	 * @param _cmbNavigatorFullSQL the full SQL query to set for the combo navigator
	 * @param _cmbNavigatorSelectSQL the SELECT clause to set for the combo navigator
	 */
	private SSFormViewScreenHelper(final String _title, final Connection _connection, final String _pkColumn,
			final Long _parentID, final String _fullSQL, final String _selectSQL, final String _orderBySQL, final String _cmbDisplayColumn1, final String _cmbDisplayColumn2,
			final String _cmbSeparator, final String _cmbNavigatorFullSQL, final String _cmbNavigatorSelectSQL) {

		// CALL TO PARENT CONSTRUCTOR
		super(_title, true, true, false, true);

		try {
			// SET PARAMETERS
			setConnection(_connection);
			setPkColumn(_pkColumn);
			setParentID(_parentID);
			setFullSQL(_fullSQL);
			setSelectSQL(_selectSQL);
			setOrderBySQL(_orderBySQL);
			setCmbDisplayColumn1(_cmbDisplayColumn1);
			setCmbDisplayColumn2(_cmbDisplayColumn2);
			setCmbSeparator(_cmbSeparator);
			setCmbNavigatorFullSQL(_cmbNavigatorFullSQL);
			setCmbNavigatorSelectSQL(_cmbNavigatorSelectSQL);

			// SET SCREEN DEFAULTS
			setScreenSize();
			setDefaultScreenLocation();

		} catch (final Exception e) {
			logger.error("Exception.", e);
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
	 * dataNavigator
	 */
	private void activateSyncManager() {
		if (getSyncManager() == null) {
			setSyncManager(new SSSyncManager(getCmbNavigator(), getDataNavigator()));
			getSyncManager().setColumnName(getPkColumn());
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
				getDataNavigator().updatePresentRow();
			}
		});

		// ADD OTHER LISTENERS IN IMPLEMENTATION
		addImplListeners();
		addCustomListeners();

	}

	/**
	 * Adds any implementation-specific listeners.
	 * 
	 * @throws Exception exception thrown while adding any implementation-specific screen listeners
	 * 
	 * @deprecated Starting in 4.0.0+ use {@link #addCustomListeners()} instead.
	 */
	@Deprecated
	protected void addImplListeners() throws Exception {
		// Do nothing by default...
	}

	/**
	 * Adds a newly inserted record to the combo navigator.
	 * <p>
	 * This can be empty if a call is made to {@code #setRequeryAfterInsertOrDelete(true);}
	 * because in that case the combo navigator will be requeried.
	 */
	protected abstract void addNewRecordToCmbNavigator();

	/**
	 * Initialize and bind screen components.
	 * 
	 * @throws Exception exception thrown while binding components
	 */
	protected abstract void bindComponents() throws Exception;

	/**
	 * Deactivate sync between cmbNavigator and dataNavigator.
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
		return cmbDisplayColumn1;
	}

	/**
	 * @return name of the 2nd database column to display in the combo navigator
	 */
	protected String getCmbDisplayColumn2() {
		return cmbDisplayColumn2;
	}
	
	/**
	 * @return the cmbNavigator
	 */
	protected SSDBComboBox getCmbNavigator() {
		return cmbNavigator;
	}

	/**
	 * Provides the full SQL query for the combo navigator.
	 * <p>
	 * Often the implementation may include a call to {@link #getParentID()} for filtering.
	 * <p>
	 * IMPORTANT: The query must return the same number of results and in the same order
	 * as {@link #getSelectionQuery()}
	 * 
	 * @return String with the full SQL query for the combo navigator
	 * 
	 * @throws Exception exception thrown while retrieving the combo navigator query
	 */
	protected String getCmbNavigatorQuery() throws Exception {
		String result = null;
		
		if (getCmbNavigatorFullSQL()==null) {
			if (getParentID()==null) {
				result = getCmbNavigatorSelectSQL() + " " + getOrderBySQL();
			} else {
				result = getCmbNavigatorSelectSQL() + " " + getParentID() + " " + getOrderBySQL();
			}
		} else {
			result = getCmbNavigatorFullSQL();
		}
		return result;
		
	}
	
	/**
	 * @return the full SQL query for the combo navigator
	 */
	protected String getCmbNavigatorFullSQL() {
		return cmbNavigatorFullSQL;
	}

	/**
	 * @return the SELECT clause for the combo navigator
	 */
	protected String getCmbNavigatorSelectSQL() {
		return cmbNavigatorSelectSQL;
	}

	/**
	 * @return character(s) used to separate the display of the 1st and 2nd columns  of the combo navigator
	 */
	protected String getCmbSeparator() {
		return cmbSeparator;
	}

	/**
	 * @return the dataNavigator
	 */
	protected SSDataNavigator getDataNavigator() {
		return dataNavigator;
	}
	
	/**
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

//	/**
//	 * @return the txtParentID
//	 */
//	@Deprecated
//	protected SSTextField getTxtParentID() {
//		return txtParentID;
//	}
	
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
	private void initCmbNavigator() throws SQLException, Exception {

		setCmbNavigator(new SSDBComboBox(getConnection(), getCmbNavigatorQuery(),
				getPkColumn(), getCmbDisplayColumn1()));

		if (getCmbDisplayColumn2() != null) {
			getCmbNavigator().setSecondDisplayColumnName(getCmbDisplayColumn2());
		}

		if (getCmbSeparator()!= null) {
			getCmbNavigator().setSeparator(getCmbSeparator());
		}
		
		getCmbNavigator().execute();
	}

	/**
	 * Initialize dataNavigator
	 */
	private void initDataNavigator() {
		setDataNavigator(new SSDataNavigator(getRowset()));
		getDataNavigator().setDBNav(new FormHelperSSDBNavImpl(this));
	}

	/**
	 * Initialize rowset and loads sql query results
	 * 
	 * @throws SQLException exception thrown while initializing rowset
	 * @throws Exception exception thrown while initializing rowset
	 */
	private void initRowset() throws SQLException, Exception {
		setRowset(new JdbcRowSetImpl(getConnection()));
		updateRowset();
	}
	
	/**
	 * Performs post construction initialization.
	 *
	 * @param _parentID primary key value of parent record (FK for current rowset)
 	 * @param _cmbDisplayColumn1 name of the 1st database column to display in the combo navigator
 	 * 
	 * @deprecated Starting in 4.0.0+ these parameters are passed to constructor and initialization
	 *  	is performed in handled {@link #initScreen()}.
	 */
	@Deprecated
	protected void initScreen(final long _parentID, final String _cmbDisplayColumn1) {
		
		logger.error("initScreen() Method no longer supported. These parameters should be passed to the appropriate constructor.");
		
//		initScreen(_parentID, _cmbDisplayColumn1, null, null);
		
	}
	
	/**
	 * Performs post construction screen initialization.
	 */
	@Override
	protected void initScreen() {
		try {

			// SETUP QUERY, DEFAULTS, and BUILD SCREEN
			// SET ROWSET QUERY
			initRowset();

			// INITIALIZE DATA NAVIGATOR
			initDataNavigator();
			
			// INITIALIZE COMBO NAVIGATOR
			initCmbNavigator();
			
			// BIND PRIMARY KEY
			txtPrimaryKey.bind(getRowset(), getPkColumn());
			
			// INITIALIZE and BIND SCREEN COMPONENTS
			bindComponents();

			// ADD COMPONENTS TO SCREEN
			addComponents();

			// ADD MENU BAR TO THE SCREEN.
			setJMenuBar(getJMenuBar());

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
			//setVisible(true);
			// Parent to call screenClass.showUp(this);
				
		} catch (final SQLException se) {
			logger.error("SQL Exception.", se);
			JOptionPane.showMessageDialog(this,
					"Database error while initializing screen. Parent ID is: " + getParentID() + ".\n" + se.getMessage());
		} catch (final Exception e) {
			logger.error("Exception.", e);
			JOptionPane.showMessageDialog(this,
					"Error while initializing screen. Parent ID is: " + getParentID() + ".\n" + e.getMessage());
		}
	}

	/**
	 * Performs post construction initialization.
	 *
	 * @param _parentID primary key value of parent record (FK for current rowset)
 	 * @param _cmbDisplayColumn1 name of the 1st database column to display in the combo navigator
	 * @param _cmbDisplayColumn2 name of the 2nd database column to display in the combo navigator
	 * @param _cmbSeparator character(s) used to separate the display of the 1st and 2nd columns  of the combo navigator
	 * 
 	 * @deprecated Starting in 4.0.0+ this is handled in constructor.
	 */
	@Deprecated
	protected void initScreen(final Long _parentID,
			final String _cmbDisplayColumn1, final String _cmbDisplayColumn2, final String _cmbSeparator) {
		
		logger.error("initScreen() Method no longer supported. These parameters should be passed to the appropriate constructor.");
		
	}
	
	/**
	 * Retrieve and set the primary key value for a new record.
	 */
	protected abstract void retrieveAndSetNewPrimaryKey();
	
	/**
	 * @param _cmbDisplayColumn1 name of the 1st database column to display in the combo navigator
	 */
	protected void setCmbDisplayColumn1(final String _cmbDisplayColumn1) {
		cmbDisplayColumn1 = _cmbDisplayColumn1;
	}

	/**
	 * @param _cmbDisplayColumn2 name of the 2nd database column to display in the combo navigator
	 */
	protected void setCmbDisplayColumn2(final String _cmbDisplayColumn2) {
		cmbDisplayColumn2 = _cmbDisplayColumn2;
	}

	/**
	 * @param _cmbNavigator the combo navigator to use for this screen/form
	 */
	private void setCmbNavigator(final SSDBComboBox _cmbNavigator) {
		cmbNavigator = _cmbNavigator;
	}
	
	/**
	 * @param _cmbNavigatorFullSQL the full SQL query to set for the combo navigator
	 */
	protected void setCmbNavigatorFullSQL(String _cmbNavigatorFullSQL) {
		cmbNavigatorFullSQL = _cmbNavigatorFullSQL;
	}

	/**
	 * @param _cmbNavigatorSelectSQL the SELECT clause to set for the combo navigator
	 */
	protected void setCmbNavigatorSelectSQL(String _cmbNavigatorSelectSQL) {
		cmbNavigatorSelectSQL = _cmbNavigatorSelectSQL;
	}

	/**
	 * @param _cmbSeparator character(s) used to separate the display of the 1st and 2nd columns  of the combo navigator
	 */
	protected void setCmbSeparator(final String _cmbSeparator) {
		cmbSeparator = _cmbSeparator;
	}

	/**
	 * @param _dataNavigator the data navigator to use for this screen/form
	 */
	private void setDataNavigator(final SSDataNavigator _dataNavigator) {
		dataNavigator = _dataNavigator;
	}
	
	/**
	 * @param _requeryAfterInsertOrDelete sets boolean indicating if .execute() has to be called on rowset following insertion or deletion
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

//	/**
//	 * @param _txtParentID the ID of the parent record's primary key (screen rowset foreign key)
//	 */
//	@Deprecated
//	// TODO Determine if this is needed.
//	public void setTxtParentID(final SSTextField _txtParentID) {
//		txtParentID = _txtParentID;
//	}

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
	protected abstract void ssDBNavPerformCancelOps();

	/**
	 * Perform any actions needed after the specified Navigation
	 * takes place.
	 * <p>
	 * The helper manages closing of any child screens.
	 *
	 * @param _navigationType Enum indicating type of navigation
	 */
	protected abstract void ssDBNavPerformNavigationOps(Navigation _navigationType);

	/**
	 * Perform any actions needed after a deletion.
	 *
	 * Helper deletes record. NO ACTION REQUIRED.
	 */
	protected abstract void ssDBNavPerformPostDeletionOps();

	/**
	 * Add the new record and perform any other actions needed after an insert.
	 *
	 * Helper manages enabling of record selector, but addition of record is
	 * required.
	 */
	protected abstract void ssDBNavPerformPostInsertOps();

	/**
	 * Perform any actions needed before a deletion.
	 *
	 * Helper gets ID of record to be deleted. NO ACTION REQUIRED.
	 */
	protected abstract void ssDBNavPerformPreDeletionOps();

	/**
	 * Any actions required prior to an insert.
	 * <p>
	 * Helper takes care of disabling combo navigator, retrieving the primary key for
	 * the new records, and setting default values.
	 */
	protected abstract void ssDBNavPerformPreInsertOps();

	/**
	 * Perform any actions needed after the refresh button is pressed.
	 *
	 * Helper manages synchronization of record selector and navigator, and updating
	 * of any SSDBComboBoxes. NO ACTION REQUIRED.
	 */
	protected abstract void ssDBNavPerformRefreshOps();

	/**
	 * Update combo navigator
	 *
	 * @throws Exception exception thrown while updating the combo navigator query
	 */
	private void updateCmbNavigatorData() throws Exception {
		getCmbNavigator().setQuery(getCmbNavigatorQuery());
		getCmbNavigator().execute();
	}

	/**
	 * Update data navigator with latest rowset
	 */
	private void updateDataNavigator() {
		getDataNavigator().setRowSet(getRowset());
	}

	/**
	 * Updates the rowset, data navigator, combo navigator, and any DB comboboxes
	 *
	 * @param _parentID primary key value of parent record (FK for current rowset)
	 */
	@Override
	public void updateScreen(final Long _parentID) {
		
		// Update parameters
			setParentID(_parentID);

		try {
			// TURN OFF THE SYNC MANAGER
			deactivateSyncManager();

			// SET THE NEW QUERY FOR ROWSET
			updateRowset();

			// SET NEW ROWSET FOR NAVIGATOR.
			updateDataNavigator();
			
			// UPDATE THE COMBO NAVIGATOR
			updateCmbNavigatorData();

			// UPDATE SELECTION CRITERIA FOR ANY OTHER SSDBCombos
			updateSSDBComboBoxes();

			// TURN ON SYNC MANAGER
			activateSyncManager();

			// ACTIVATE/DEACTIVATE SCREEN COMPONENTS
			activateDeactivateComponents();

		} catch (final SQLException se) {
			logger.error("SQL Exception.", se);
			JOptionPane.showMessageDialog(this,
					"Database error while updating screen for parent ID: " + getParentID() + ".\n" + se.getMessage());
		} catch (final Exception e) {
			logger.error("Exception.", e);
			JOptionPane.showMessageDialog(this,
					"Error while updating screen for parent ID: " + getParentID() + ".\n" + e.getMessage());
		}

	} 
	
	/**
	 * Updates the rowset, data navigator, combo navigator, and any DB comboboxes
	 *
	 * @param _parentID primary key value of parent record (FK for current rowset)
	 * @param _fullSQL 		the full SQL to set for the rowset
	 *
	 * @param _cmbNavigatorFullSQL the full SQL query to set for the combo navigator
	 */
	public void updateScreen(final Long _parentID, final String _fullSQL, final String _cmbNavigatorFullSQL) {
		setFullSQL(_fullSQL);
		setCmbNavigatorFullSQL(_cmbNavigatorFullSQL);
		
		setSelectSQL(null);
		setOrderBySQL(null);
		setCmbNavigatorSelectSQL(null);
		
		updateScreen(_parentID);
	}
	
	/**
	 * Updates the rowset, data navigator, combo navigator, and any DB comboboxes
	 *
	 * @param _parentID primary key value of parent record (FK for current rowset)
	 * 
	 * @param _selectSQL 	the SELECT clause of the SQL to set  for the rowset
	 * @param _orderBySQL 	the ORDER BY clause of the SQL to set for for the rowset
	 * 
	 * @param _cmbNavigatorSelectSQL the SELECT clause to set for the combo navigator
	 */
	public void updateScreen(final Long _parentID, final String _selectSQL, final String _orderBySQL, final String _cmbNavigatorSelectSQL) {
		setFullSQL(null);
		setCmbNavigatorFullSQL(null);
		
		setSelectSQL(_selectSQL);
		setOrderBySQL(_orderBySQL);
		setCmbNavigatorSelectSQL(_cmbNavigatorSelectSQL);
		
		updateScreen(_parentID);
	}

	/**
	 * Used to update any SSDBComboBox that change when parent ID changes (e.g. a
	 * record selector combo). Assumes that any SSDBComboBoxes are created in
	 * configureScreen().
	 */
	protected abstract void updateSSDBComboBoxes();

}