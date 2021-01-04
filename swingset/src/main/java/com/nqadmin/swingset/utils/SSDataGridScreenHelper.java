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
import java.sql.SQLException;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.TableCellEditor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.rowset.JdbcRowSetImpl;
import com.nqadmin.swingset.SSDataGrid;
import java.sql.Connection;

//SSDataGridScreenHelper.java
//
//SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Helper class for designing SSDataGrid screens.
 */
//public abstract class SSDataGridScreenHelper extends JInternalFrame {
public abstract class SSDataGridScreenHelper extends SSScreenHelperCommon {
	
	private static Logger logger = LogManager.getLogger(); // Log4j Logger for component
	
	private static final long serialVersionUID = 3558830097072342112L; // unique serial ID

	protected Container contentPane; // Container for the JInternalFrame containing the SSDataGrid
	protected SSDataGrid dataGrid = new SSDataGrid(); // SSDataGrid used for this screen


	/**
	 * Constructs a DataGrid with the specified title and parent ID, and attaches it to the specified window.
	 * Sets up query based on specified SELECT and ORDER BY strings.
	 *
	 * @param _title		title of window
	 * @param _parentID		parent ID used for record retrieval
	 * @param _connection 	database connection
	 * @param _contentPane	Container to which JInternalFrame containing SSDataGrid should be attached
	 * @param _fullSQL		full SQL query for rowset
	 * @param _selectSQL	SELECT clause for rowset such that selectSQL + parentID + " " + orderBySQL is a valid query
	 * @param _orderBySQL	ORDER BY clause for rowset including at least a semicolon
	 */
	private SSDataGridScreenHelper(final String _title, final Long _parentID, final Connection _connection, final Container _contentPane, final String _fullSQL,
			final String _selectSQL, final String _orderBySQL) {

		// CALL TO PARENT CONSTRUCTOR
			super(_title,true,true,false,true);
			
			try {
				// SET PARAMETERS
				super.setParentID(_parentID);
				setConnection(_connection);
				contentPane = _contentPane;
				setFullSQL(_fullSQL);
				setSelectSQL(_selectSQL);
				setOrderBySQL(_orderBySQL);

				// SET SCREEN DEFAULTS
				setScreenSize();
				setDefaultScreenLocation();

			} catch (final Exception e) {
				logger.error("Exception.", e);
				JOptionPane.showMessageDialog(this,
						"Error while constructing screen, parent ID: " + getParentID() + ".\n" + e.getMessage());
			}

    }
	
	/**
	 * Constructs a DataGrid with the specified title and parent ID, and attaches it to the specified window.
	 * Sets up query based on specified SELECT and ORDER BY strings.
	 *
	 * @param _title		title of window
	 * @param _parentID		parent ID used for record retrieval
	 * @param _connection 	database connection
	 * @param _contentPane	Container to which JInternalFrame containing SSDataGrid should be attached
	 * @param _fullSQL		full SQL query for rowset
	 */
	public SSDataGridScreenHelper(final String _title, final Long _parentID, final Connection _connection,
			final Container _contentPane, final String _fullSQL) {

		this(_title, _parentID, _connection, _contentPane, _fullSQL, null, null);
    }
	
	/**
	 * Constructs a DataGrid with the specified title and parent ID, and attaches it to the specified window.
	 * Sets up query based on specified SELECT and ORDER BY strings.
	 *
	 * @param _title		title of window
	 * @param _parentID		parent ID used for record retrieval
	 * @param _connection	database connection
	 * @param _contentPane	Container to which JInternalFrame containing SSDataGrid should be attached
	 * @param _selectSQL	SELECT clause for rowset such that selectSQL + parentID + " " + orderBySQL is a valid query
	 * @param _orderBySQL	ORDER BY clause for rowset including at least a semicolon
	 */
	public SSDataGridScreenHelper(final String _title, final Long _parentID, final Connection _connection, final Container _contentPane,
			final String _selectSQL, final String _orderBySQL) {
		
		this(_title, _parentID, _connection, _contentPane, null, _selectSQL, _orderBySQL);
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
	 * This method signature should be used if there is a URL for all columns.
	 *
	 * @param _key keystroke name (e.g., "F1")
	 * @param _keyLabel label for keystroke (e.g. "Help")
	 * @param _urlPrefixesForColumns prefix of URL without ID
	 * @param _appendColumnValue Boolean indicating if column value should be appended to URL
	 */
	public void addKeyBasedURLLinking(final String _key, final String _keyLabel,
			final String[] _urlPrefixesForColumns, final boolean _appendColumnValue) {
		addKeyBasedURLLinking(null, _key, _keyLabel, _urlPrefixesForColumns, _appendColumnValue);
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
	public void addKeyBasedURLLinking(final int[] _columns, final String _key, final String _keyLabel,
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
			public void stopEditing(){
			// CHECK IF ANY CELL IS IN EDITING MODE.
				if(dataGrid.isEditing()){
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
				    }
				    catch (final Exception e) {
				    	logger.error("Exception.", e);
				    }
				}
			}
		});
		
		// ADD OTHER LISTENERS IN IMPLEMENTATION
		addCustomListeners();

	}

	/**
	 * Adds and configures the DataGrid components.
	 */
	public abstract void configureDataGrid();

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
	 * Performs post construction screen initialization.
	 */
	@Override
	protected void initScreen() {
		
		try {
			// SETUP QUERY, DEFAULTS, and BUILD SCREEN
			// SET ROWSET QUERY
			initRowset();
	
			// CONFIGURE DATAGRID
			configureDataGrid();
			
			// ADD DATAGRID TO CONTENT PANE
	 		contentPane.add(dataGrid.getComponent());
	
			// ADD MENU BAR TO THE SCREEN.
			setJMenuBar(getJMenuBar());
	
			// ADD/CONFIGURE TOOLBARS
			configureToolBars();
	
			// ADD SCREEN LISTENERS
			addCoreListeners();
			
			// SET DEFAULT VALUES
			setDefaultValues();
			
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
					"Error while initializing screen, parent ID: " + getParentID() + ".\n" + e.getMessage());
		}
	}

	/**
	 * Updates the parent ID used for record retrieval, sets the rowset query, updates the DataGrid rowset,
	 * and updates any DataGrid default values.
	 *
	 * @param _parentID the parent ID linking the records to be displayed.
	 * 
	 * @deprecated Starting in 4.0.0+ use {@link #updateScreen(Long)} instead.
	 */
	@Override
	@Deprecated
	public void setParentID(final Long _parentID) {
		updateScreen(_parentID);
	}
	
	/**
	 * Updates the parent ID used for record retrieval, sets the rowset query, updates the DataGrid rowset,
	 * and updates any DataGrid default values.
	 *
	 * @param _parentID the parent ID linking the records to be displayed.
	 * @param _fullSQL	full query for rowset
	 * 
	 * @deprecated Starting in 4.0.0+ use {@link #updateScreen(Long, String)} instead.
	 */
	@Deprecated
	public void setParentID(final Long _parentID, final String _fullSQL) {
		updateScreen(_parentID, _fullSQL);
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
	 * Updates the parent ID used for record retrieval, sets the rowset query, updates the DataGrid rowset,
	 * and updates any DataGrid default values.
	 *
	 * @param _parentID the parent ID linking the records to be displayed.
	 * @param _fullSQL	full query for rowset
	 */
	public void updateScreen(final Long _parentID, final String _fullSQL) {
	    updateScreen(_parentID, _fullSQL, null, null);
	}
	
	/**
	 * Updates the parent ID used for record retrieval, sets the rowset query, updates the DataGrid rowset,
	 * and updates any DataGrid default values.
	 *
	 * @param _parentID the parent ID linking the records to be displayed.
	 * @param _selectSQL	SELECT clause for rowset such that selectSQL + parentID + " " + orderBySQL is a valid query
	 * @param _orderBySQL	ORDER BY clause for rowset including at least a semicolon
	 */
	public void updateScreen(final Long _parentID, final String _selectSQL, final String _orderBySQL) {
		updateScreen(_parentID, null, _selectSQL, _orderBySQL);
	}
	
	/**
	 * Updates the parent ID used for record retrieval, sets the rowset query, updates the DataGrid rowset,
	 * and updates any DataGrid default values.
	 *
	 * @param _parentID the parent ID linking the records to be displayed.
	 * @param _fullSQL	full query for rowset
	 * @param _selectSQL	SELECT clause for rowset such that selectSQL + parentID + " " + orderBySQL is a valid query
	 * @param _orderBySQL	ORDER BY clause for rowset including at least a semicolon
	 */
	private void updateScreen(final Long _parentID, final String _fullSQL, final String _selectSQL, final String _orderBySQL) {

	    setFullSQL(_fullSQL);
	    setSelectSQL(_selectSQL);
	    setOrderBySQL(_orderBySQL);
	    
	    updateScreen(_parentID);
	}

	/**
	 * Updates the rowset
	 *
	 * @param _parentID primary key value of parent record (FK for current rowset)
	 */
	@Override
	public void updateScreen(final Long _parentID) {
		
		// Update parameters
			super.setParentID(_parentID);

			try {

		    // UPDATE ROWSET
	    		updateRowset();
		        dataGrid.setRowSet(getRowset());

	        // SET ANY DEFAULT VALUES
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