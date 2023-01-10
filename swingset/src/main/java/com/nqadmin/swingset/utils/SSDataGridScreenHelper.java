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
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.TableCellEditor;

import org.apache.logging.log4j.Logger;

import com.nqadmin.rowset.JdbcRowSetImpl;
import com.nqadmin.swingset.SSDataGrid;

//SSDataGridScreenHelper.java
//
//SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Helper class for designing SSDataGrid screens.
 */
//public abstract class SSDataGridScreenHelper extends JInternalFrame {
public abstract class SSDataGridScreenHelper extends SSScreenHelperCommon {
	
	private static Logger logger = SSUtils.getLogger(); // Log4j Logger for component
	
	private static final long serialVersionUID = 3558830097072342112L; // unique serial ID

	protected SSDataGrid dataGrid = new SSDataGrid(); // SSDataGrid used for this screen


	/**
	 * Constructs a Data Grid screen with the specified title and attaches it
	 * to the specified window.
	 *
	 * @param _title                  title of window
	 * @param _parentContainer        parent window/container
	 * @param _connection             database connection
	 * @param _pkColumn               name of primary key column
	 * @param _parentID               primary key value of parent record (FK for
	 *                                current rowset), if applicable
	 */
	public SSDataGridScreenHelper(final String _title, final Container _parentContainer, final Connection _connection,
			final String _pkColumn, final Long _parentID) {

		// CALL TO PARENT CONSTRUCTOR
			super(_title,true,true,false,true);
			
			try {
				// SET PARAMETERS
				setConnection(_connection);
				setParentContainer(_parentContainer);
				setPkColumn(_pkColumn);
				setParentID(_parentID);

				// SET SCREEN SIZE AND LOCATION
				setScreenSize();
				setDefaultScreenLocation();

			} catch (final Exception e) {
				logger.error("Exception.", e);
				JOptionPane.showMessageDialog(this,
						"Error while constructing screen, parent ID: " + getParentID() + ".\n" + e.getMessage());
			}

		}
	
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
			/**
			 *	Stop editing of any cell when the screen is closing & close any child screens.
			 */
			@Override
			public void internalFrameClosing(final InternalFrameEvent ife) {
				stopEditing();
				closeChildScreens();
			}

			/**
			 *	Stop editing of any cell when the screen is losing focus.
			 */
			@Override
			public void internalFrameDeactivated(final InternalFrameEvent ife) {
				stopEditing();
			}
			
			/**
			 * Stops the editing of cell, if any.
			 */
			public void stopEditing() {
				// CHECK IF ANY CELL IS IN EDITING MODE.
				if (dataGrid.isEditing()) {
					try {
						// GET THE COLUMN IN WHICH EDITING IS TAKING PLACE
						final int column = dataGrid.getEditingColumn();
						if (column > -1) {
							// GET THE EDITOR FOR THAT CELL.
							TableCellEditor cellEditor = dataGrid.getColumnModel().getColumn(column).getCellEditor();
							// IF NO SPECIFIC EDITOR IS PRESENT THEN GET THE DEFAULT CELL EDITOR.
							if (cellEditor == null) {
								cellEditor = dataGrid.getDefaultEditor(dataGrid.getColumnClass(column));
							}
							// IF THERE IS ANY EDITOR THEN STOP THE EDITING.
							if (cellEditor != null) {
								cellEditor.stopCellEditing();
								cellEditor.cancelCellEditing();
							}
						}
					} catch (final Exception e) {
						logger.error("Exception.", e);
					}
				}
			}
		});
		
		// ADD OTHER LISTENERS IN IMPLEMENTATION
		addCustomListeners();

	}

	/**
	 * Method to add F2 linking based on an ID for specified column (e.g. wiki links, contact links)
	 *
	 * @param _columns array of column of data containing ID to link (count starts from zero)
	 * @param _key name for current action map
	 * @param _urlPrefix prefix of URL without ID
	 * 
	 * @deprecated Starting in 4.0.0+ use {@link #addKeyBasedURLLinking(int[], String, String, String[], boolean)} instead.
	 */
	@Deprecated
	public void addF2Linking(final int[] _columns, final String _key, final String[] _urlPrefix) {
		addKeyBasedURLLinking(_columns, "F2", _key, _urlPrefix, true);
	}
	
	/**
	 * Adds F1/Help key support where F1 will bring up the URL specified for each column in a browser
	 * 
	 * @param _columnLinks String array of URLs with one URL per column
	 * 
	 * @deprecated Starting in 4.0.0+ use {@link #addKeyBasedURLLinking(String, String, String[], boolean)} instead.
	 */
	@Deprecated
	public void addHelpSystemLinks(final String[] _columnLinks) {
		addKeyBasedURLLinking("F1", "Help", _columnLinks, false);
	}
	
	/**
	 * Method to bring up a URL based on a keystroke (e.g., F1, F2) linking based on an ID for specified column.
	 * The column value can be appended to the URL for a cell-specific lookup.
	 * <p>
	 * Column-based look ups are helpful for an F1 based help system. Cell-based look ups are helpful for looking up
	 * values in another system (e.g., a primary key based Wiki entry).
	 * <p>
	 * This method signature should be used if only some columns have a URL.
	 * 
	 * @param _columns Array of columns indices to link.
	 * @param _key keystroke name (e.g., "F1")
	 * @param _keyLabel label for keystroke (e.g. "Help")
	 * @param _urlPrefixesForColumns prefix of URL without ID
	 * @param _appendColumnValue Boolean indicating if column value should be appended to URL
	 */
	protected void addKeyBasedURLLinking(final int[] _columns, final String _key, final String _keyLabel,
			final String[] _urlPrefixesForColumns, final boolean _appendColumnValue) {
		
		dataGrid.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(_key), _keyLabel);
		
		dataGrid.getActionMap().put(_keyLabel, new Action() {
			boolean enabled = true;
			long lastTimestamp = -1;

			@Override
			public void actionPerformed(final ActionEvent ae) {
				if (_columns != null) {
					for(int i =0; i< _columns.length; i++) {
						if (dataGrid.getSelectedColumn()==_columns[i]) {
							if ((lastTimestamp == -1) || ((lastTimestamp + 2000 ) < ae.getWhen())) {
								String columnValue = "";
								if (_appendColumnValue) {
									columnValue = (String)dataGrid.getValueAt(dataGrid.getSelectedRow(),_columns[i]);
								}
								//final Integer linkID = (Integer)dataGrid.getValueAt(dataGrid.getSelectedRow(),_columns[i]);
								try {
									Runtime.getRuntime().exec(new String[] {ssProps.getProperty("Browser"), _urlPrefixesForColumns[i] + columnValue});
								} catch (final IOException ioe) {
									logger.error("IO Exception.", ioe);
								}
								lastTimestamp = ae.getWhen();
							}
							break;
						}
					}
				} else {
					final int column = dataGrid.getSelectedColumn();
					String columnValue = "";
					if (_appendColumnValue) {
						columnValue = (String)dataGrid.getValueAt(dataGrid.getSelectedRow(),column);
					}
					if((column >= 0) && (_urlPrefixesForColumns.length > column)) {
						final String url = _urlPrefixesForColumns[column] + columnValue;
						try {
							Runtime.getRuntime().exec(new String[] {ssProps.getProperty("Browser"), url});
						} catch (final IOException ioe) {
							logger.error("IO Exception.", ioe);
						}
					}
					lastTimestamp = ae.getWhen();
				}
			}

			@Override
			public void addPropertyChangeListener(
					final PropertyChangeListener listener) {
			}

			@Override
			public Object getValue(final String key) {
				return null;
			}

			@Override
			public boolean isEnabled() {
				return enabled;
			}

			@Override
			public void putValue(final String key, final Object value) {
			}

			@Override
			public void removePropertyChangeListener(
					final PropertyChangeListener listener) {
			}

			@Override
			public void setEnabled(final boolean _enabled) {
				enabled = _enabled;
			}
		});
	}
	
	/**
	 * Method to bring up a URL based on a keystroke (e.g., F1, F2) linking based on an ID for specified column.
	 * The column value can be appended to the URL for a cell-specific lookup.
	 * <p>
	 * Column-based look ups are helpful for an F1 based help system. Cell-based look ups are helpful for looking up
	 * values in another system (e.g., a primary key based Wiki entry).
	 * <p>
	 * This method signature should be used if there is a URL for all columns.
	 *
	 * @param _key keystroke name (e.g., "F1")
	 * @param _keyLabel label for keystroke (e.g. "Help")
	 * @param _urlPrefixesForColumns prefix of URL without ID
	 * @param _appendColumnValue Boolean indicating if column value should be appended to URL
	 */
	protected void addKeyBasedURLLinking(final String _key, final String _keyLabel,
			final String[] _urlPrefixesForColumns, final boolean _appendColumnValue) {
		addKeyBasedURLLinking(null, _key, _keyLabel, _urlPrefixesForColumns, _appendColumnValue);
	}

	/**
	 * Adds and configures the DataGrid components.
	 * <p>
	 * Calls for dataGrid.setRowSet() and dataGrid.setPrimaryColumn() are handled automatically.
	 */
	protected abstract void configureDataGrid();
	
	/**
	 * Method implemented by screen developer to return a String array with the table/grid
	 * column names for which to set default values.
	 * 
	 * @return a String array with the table/grid column names
	 */
	protected abstract String[] getDefaultColumnNames();
	
	/**
	 * Method implemented by screen developer to return a String array with the table/grid
	 * column default values.
	 * 
	 * @return an Object array with the table/grid column default values
	 */
	protected abstract Object[] getDefaultColumnValues();
	
	/**
	 * Method implemented by screen developer to return a String array with the table/grid
	 * column headings.
	 * 
	 * @return a String array with the table/grid column headings
	 */
	protected abstract String[] getHeaders();

	/**
	 * Performs post construction initialization.  Needs to be called from constructor in implementation.
	 *
	 * @param _parentID		parent ID used for record retrieval
	 * @param _fullSQL		full SQL query for rowset
	 * 
	 * @deprecated Starting in 4.0.0+ these parameters are passed to constructor and initialization
	 *  	is performed in handled {@link #initScreen()}.
	 */
	@Deprecated
	public void init(final long _parentID, final String _fullSQL) {
		
		logger.error("initScreen() Method no longer supported. These parameters should be passed to the appropriate constructor.");

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
	 * Performs post construction screen initialization.
	 */
	@Override
	protected void initScreen() {
		
		try {
			// SETUP QUERY, DEFAULTS, and BUILD SCREEN
			// SET ROWSET QUERY
			initRowset();
			
			// SET TABLE/GRID HEADERS
			dataGrid.setHeaders(getHeaders());

			// SET ROWSET FOR DATAGRID
			dataGrid.setRowSet(getRowset());
			
			// SET PRIMARY COLUMN
			dataGrid.setPrimaryColumn(getPkColumn());

			// CONFIGURE DATAGRID
			configureDataGrid();

			// ADD MENU BAR TO THE SCREEN.
			setJMenuBar(getCustomMenu());
	
			// ADD/CONFIGURE TOOLBARS
			configureToolBars();
			
			// UPDATE SELECTION CRITERIA FOR ANY OTHER SSDBCombos
			updateSSDBComboBoxes();
			
			// ADD DATAGRID TO CONTAINER
			//  - PUT INSIDE OF A JSCROLLPANE SO WE HAVE SCROLL BARS WHEN NEEDED
			//  - WITHOUT THE JSCROLLPANE, SOMETIMES THE COLUMN HEADERS DON'T RENDER
	 		//getContentPane().add(dataGrid);
			if(getParentContainer() == null) {
				getContentPane().add(dataGrid.getComponent());
			}
			else {
				getParentContainer().add(dataGrid.getComponent());
			}
			
			// SET CELL ENABLING/DISABLING
//			setActivateDeactivate();
			configureSSCellEditing();
	
			// ADD SCREEN LISTENERS
			addCoreListeners();

			// SET DEFAULT VALUES
			setDefaultValues();
			
			// MAKE SCREEN VISIBLE 
			// USER HAS TO DECIDE WHEN TO SHOW THE SCREEN
//			showUp(getParentContainer());
			
		} catch (final SQLException se) {
			logger.error("SQL Exception.", se);
			JOptionPane.showMessageDialog(this,
					"Database error while initializing screen. Parent ID is: " + getParentID() + ".\n" + se.getMessage());
		} catch (final Exception e) {
			logger.error("Exception.", e);
			JOptionPane.showMessageDialog(this,
					"Error while initializing screen, parent ID: " + getParentID() + ".\n" + e.getMessage());
		}
	}
	
	/**
	 * Performs post construction initialization.  Generally be called from constructor in implementation.
	 *
	 * @param _parentID		parent ID used for record retrieval
	 * @param _fullSQL		full SQL query for rowset
	 * 
	 * @deprecated Starting in 4.0.0+ these parameters are passed to constructor and initialization
	 *  	is performed in handled {@link #initScreen()}.
	 */
	@Deprecated	
	public void initScreen(final Long _parentID, final String _fullSQL) {
		
		logger.error("initScreen() Method no longer supported. These parameters should be passed to the appropriate constructor.");

	}
	
	/**
	 * Used to enable/disable cells based on adjacent cell value or other criteria.
	 * 
	 * JTables/SSDataGrids count rows and columns from zero.
	 * 
	 * Can simply override and return true if not enabling/disabling is needed.
	 * 
	 * @param _row JTable/SSDatagrid row to evaluate
	 * @param _column JTable/SSDatagrid column to evaluate
	 * @return true if cell should be editable/enabled, otherwise false
	 * 
	 * @deprecated - use {@link SSDataGrid#setSSCellEditing(com.nqadmin.swingset.SSCellEditing)} instead
	 */
	@Deprecated
	protected boolean isGridCellEditable(int _row, int _column) {
		return true;
	}
	
//	/**
//	 * Used to enable/disable cells based on adjacent cell value or other criteria.
//	 * 
//	 * @throws Exception thrown if an exception is encountered enabling/disabling cells
//	 */
//	@Deprecated
//	private void setActivateDeactivate() throws Exception {
//		dataGrid.setSSCellEditing(new SSCellEditing(){
//
//			private static final long serialVersionUID = 1L; // UNIQUE SERIAL ID
//
//			@Override
//			public boolean isCellEditable(int _row, int _column) {
//				return isGridCellEditable(_row, _column);
//			}
//		});
//
//	}
	
	/**
	 * Used to set the SSCellEditing for the SSDataGrid to activate/deactivate cells or validate cell values.
	 * <pre>
	 * dataGrid.setSSCellEditing(new SSCellEditing() {
	 *  // implement the methods in SSCellEditing
	 * });
	 * </pre>
	 */
	protected abstract void configureSSCellEditing(); 
	
	/**
	 * Sets any default values for the data grid columns.
	 * 
	 * @throws Exception exception thrown while setting default values for the data grid
	 */
	@Override
	protected void setDefaultValues() throws Exception {
		dataGrid.setDefaultValues(getDefaultColumnNames(),getDefaultColumnValues());
	}

	/**
	 * Set windows position on screen.
	 *
	 * @param _defaultX the default X coordinate for the screen
	 * @param _defaultY the default Y coordinate for the screen
	 * 
	 * @deprecated Starting in 4.0.0+ use {@link #setDefaultScreenLocation()} instead.
	 */
	@Deprecated
	public void setPosition(final int _defaultX, final int _defaultY) {
		setDefaultScreenLocation(_defaultX, _defaultY);
	}

	/**
	 * Updates the rowset. Developer can call setParentID() prior to calling updateScreen()
	 * if necessary for getRowsetQuery();
	 */
	@Override
	public void updateScreen() {

		try {

			// UPDATE/REQUERY ROWSET
			updateRowset();
			
			// UPDATE DATAGRID ROWSET
			dataGrid.setRowSet(getRowset());
			
			// UPDATE SELECTION CRITERIA FOR ANY OTHER SSDBCombos
			updateSSDBComboBoxes();
			
			// THIS IS NEEDED AS PARENT ID IS ALSO SET AS DEFAULT
			setDefaultValues();

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

} // end SSDataGridScreenHelper class