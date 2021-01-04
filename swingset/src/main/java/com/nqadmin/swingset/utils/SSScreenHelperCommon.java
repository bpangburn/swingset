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

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.RowSet;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;

//SSScreenHelperCommon.java
//
//SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Helper class for designing SwingSet Form View screens.
 */
public abstract class SSScreenHelperCommon extends JInternalFrame {

	/**
	 * unique serial ID
	 */
	private static final long serialVersionUID = 9108320379306383142L;

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * SwingSet properties
	 */
	protected static final Properties ssProps = SSProperties.getProperties();
	
	private Connection connection; // Database connection.
	private RowSet rowset; // Rowset to be used for screen/form.

	private String pkColumn; // Primary key column name for rowset.
	
	private Long parentID = null; // Primary key value of parent record (FK for current rowset).
	
	private String fullSQL = null; // Full SQL for rowset
	private String selectSQL = null; // SELECT CLAUSE FOR RECORDSET SUCH THAT selectSQL + parentID + " " + orderBySQL IS A VALID QUERY
	private String orderBySQL = null; // ORDER BY CLAUSE FOR RECORDSET INCLUDING AT LEAST A SEMICOLON

	private int defaultX = 0; // Default top left horizontal offset for screen/form.
	private int defaultY = 0; // Default top left vertical offset for screen/form.
	
	/**
	 * Creates a SSScreenHelperCommon based on an JInternalFrame with the specified title,
	 * resizability, closability, maximizability, and iconifiability.
	 * 
	 * @param _title - the String to display in the title bar
	 * @param _resizable - if true, the internal frame can be resized
	 * @param _closable - if true, the internal frame can be closed
	 * @param _maximizable - if true, the internal frame can be maximized
	 * @param _iconifiable - if true, the internal frame can be iconified
	 */
	public SSScreenHelperCommon(final String _title, final boolean _resizable, final boolean _closable,
			final boolean _maximizable, final boolean _iconifiable) {
		super(_title, _resizable, _closable, _maximizable, _iconifiable);
	}

	/**
	 * Adds screen listeners.
	 * 
	 * @throws Exception exception thrown while adding core screen listeners
	 */
	protected abstract void addCoreListeners() throws Exception;
	
	/**
	 * Method to allow Developer to add custom listeners to a SwingSet screen.
	 * <p>
	 * It is called from addCoreListeners() which is called from initScreen()
	 * <p>
	 * This method can be empty.
	 *  
	 * @throws Exception exception thrown while adding any custom screen listeners
	 */
	protected abstract void addCustomListeners() throws Exception;

	/**
	 * Closes any child screens that are open.
	 */
	public abstract void closeChildScreens();

	/**
	 * Closes the current screen.
	 */
	public void closeScreen() {
		try {
			setClosed(true);
			closeChildScreens();
		} catch (final PropertyVetoException pve) {
			logger.error("Property Veto Exception.", pve);
		}
	}

	/**
	 * Adds and configures any required buttons to the mainFrame toolbar.
	 *
	 * <pre>
	 * Separators added with:
	 * {@code
	 * 	mainFrame.getToolBar().addSeparator();
	 * }
	 *
	 * Buttons added with:
	 * {@code
	 *  btnMyButtonName = mainFrame.addToolBarButton(new AbstractAction() {
	 *  	public void actionPerformed(ActionEvent ae) {
	 *          myButtonNameActionMethod();
	 *      }
	 *  }, "My Button Description", "images/myButtonGraphic.gif");
	 *  }
	 * </pre>
	 */
	protected abstract void configureToolBars();

	/**
	 * @return the top left horizontal screen offset
	 */
	public int getDefaultX() {
		return defaultX;
	}

	/**
	 * @return the top left vertical screen offset
	 */
	public int getDefaultY() {
		return defaultY;
	}
	
	/**
	 * @return the full SQL for the rowset
	 */
	protected String getFullSQL() {
		return fullSQL;
	}

	/**
	 * Builds menu bar and adds applicable listeners.
	 *
	 * @return JMenuBar complete with any menu items and listeners
	 */
	@Override
	public abstract JMenuBar getJMenuBar();

	/**
	 * @return the ORDER BY clause of the SQL for the rowset
	 */
	protected String getOrderBySQL() {
		return orderBySQL;
	}
	
	/**
	 * @return Parent window/container.
	 *
	 * @deprecated Starting in 4.0.0+ use {@link #getRootFrame()} instead.
	 */
	@Deprecated
	public Frame getMainFrame() {
		return getRootFrame();
	}

	/**
	 * Returns the parent ID used for record retrieval (FK for current rowset))
	 *
	 * @return primary key value of parent record (FK for current rowset) used for record retrieval
	 */
	public Long getParentID() {
		return parentID;
	}

	/**
	 * @return Primary key column name for rowset.
	 */
	public String getPkColumn() {
		return pkColumn;
	}
	
	/**
	 * @return the root component for the current component tree
	 */
	public JFrame getRootFrame() {
		return (JFrame) SwingUtilities.getRoot(this);
	}

	/**
	 * @return the SELECT clause of SQL for the rowset
	 */
	protected String getSelectSQL() {
		return selectSQL;
	}

	/**
	 * @return the rowset
	 */
	protected RowSet getRowset() {
		return rowset;
	}

	/**
	 * Provides the full SQL query for the rowset.
	 * <p>
	 * Often the implementation may include a call to {@link #getParentID()} for filtering.
	 *
	 * @return String with the full SQL query for the rowset
	 * 
	 * @throws Exception exception thrown while retrieving the selection query
	 */
	protected String getSelectionQuery() throws Exception {
		
		String result = null;
		
		if (getFullSQL()==null) {
			if (getParentID()==null) {
				result = getSelectSQL() + " " + getOrderBySQL();
			} else {
				result = getSelectSQL() + " " + getParentID() + " " + getOrderBySQL();
			}
		} else {
			result = getFullSQL();
		}
		
		return result;
		
	}

	/**
	 * @return a SwingSet Connection based on the DBConnector
	 */
	protected Connection getConnection() {
		return connection;
	}
	
	/**
	 * Performs post construction screen initialization.
	 * <p>
	 * Generally called at end of implementing screen constructor or shortly thereafter.
	 * This allows screen implementation to handle any steps between construction and screen
	 * initialization.
	 */
	protected abstract void initScreen();

	/**
	 * Moves the screen and all its children recursively to their respective default
	 * positions.
	 */
	public void moveAllToDefaultLocation() {
		moveToDefaultLocation();
	}

	/**
	 * Moves the screen to its default position.
	 *
	 * Can be used when you want to position the screens in their default positions.
	 */
	public void moveToDefaultLocation() {
		setLocation(getDefaultX(), getDefaultY());
	}

	/**
	 * Sets default X and Y coordinates for the screen (top left offset)
	 */
	public void setDefaultScreenLocation() {
		setDefaultScreenLocation(Integer.valueOf(ssProps.getProperty("Level_2_X_Position")), Integer.valueOf(ssProps.getProperty("Level_2_Y_Position")));
	}

	/**
	 * Sets default X and Y coordinates for the screen (top left offset)
	 * 
	 * @param _defaultX the default X coordinate for the screen
	 * @param _defaultY the default Y coordinate for the screen
	 */
	public void setDefaultScreenLocation(final int _defaultX, final int _defaultY) {
		setDefaultX(Integer.valueOf(ssProps.getProperty("Level_2_X_Position")));
		setDefaultY(Integer.valueOf(ssProps.getProperty("Level_2_Y_Position")));
	}
	
	/**
	 * Sets any default values for the screen's components.
	 * <p>
	 * If there is a call to SSDBNavImpl.performPreInsertOps(), it should
	 * take care of clearing all of the component values before defaults are
	 * set.
	 * 
	 * @throws Exception exception thrown while setting default values for screen components
	 */
	protected abstract void setDefaultValues() throws Exception;
	
	/**
	 * Sets the default X coordinate for the screen (top left horizontal offset)
	 * 
	 * @param _defaultX the default X coordinate for the screen
	 */
	public void setDefaultX(final int _defaultX) {
		defaultX = _defaultX;
	}

	/**
	 * Sets the default Y coordinate for the screen (top left vertical offset)
	 * 
	 * @param _defaultY the default Y coordinate for the screen
	 */
	public void setDefaultY(final int _defaultY) {
		defaultY = _defaultY;
	}
	
	/**
	 * @param _fullSQL the full SQL to set for the rowset
	 */
	protected void setFullSQL(final String _fullSQL) {
		fullSQL = _fullSQL;
	}
	
	/**
	 * @param _parentID the parentID to set
	 */
	protected void setParentID(final Long _parentID) {
		parentID = _parentID;
	}

	/**
	 * @param _pkColumn the pkColumn to set
	 */
	protected void setPkColumn(final String _pkColumn) {
		pkColumn = _pkColumn;
	}

	/**
	 * @param _orderBySQL the ORDER BY clause of the SQL to set for for the rowset
	 */
	protected void setOrderBySQL(final String _orderBySQL) {
		orderBySQL = _orderBySQL;
	}

	/**
	 * @param _rowset rowset to be used by the screen/form
	 */
	protected void setRowset(final RowSet _rowset) {
		rowset = _rowset;
	}

	/**
	 * Sets the screen size
	 */
	public void setScreenSize() {
		setSize(Integer.valueOf(ssProps.getProperty("Frame_Width")), Integer.valueOf(ssProps.getProperty("Frame_Height")));
	}
	
	/**
	 * @param _selectSQL the SELECT clause of the SQL to set  for the rowset
	 */
	protected void setSelectSQL(final String _selectSQL) {
		selectSQL = _selectSQL;
	}
	
	/**
	 * @param _connection the connection to set
	 */
	protected void setConnection(final Connection _connection) {
		connection = _connection;
	}

	/**
	 * Shows the screen at the default location on the specified container.
	 *
	 * @param _container the container in which the screen has to appear
	 */
	public void showUp(final Container _container) {
		showUp(_container, getDefaultX(), getDefaultY());
	}

	/**
	 * Adds the screen to the specified container at the specified position.
	 *
	 * @param _container the container in which the screen has to appear
	 * @param _positionX the x coordinate of the position where the screen has to
	 *                   appear
	 * @param _positionY the y coordinate of the position where the screen has to
	 *                   appear
	 */
	public void showUp(final Container _container, final double _positionX, final double _positionY) {

		// SET THE POSITION OF THE SCREEN.
		setLocation((int) _positionX, (int) _positionY);
		setVisible(true);

		// SEE IF THE SCREEN IS IN THE CONTAINER
		final Component[] components = _container.getComponents();
		int i = 0;
		for (i = 0; i < components.length; i++) {
			// if (_callingClass.isInstance(components[i])) {
			if (getClass().isInstance(components[i])) {
				break;
			}
		}

		// IF IT IS NOT THERE ADD THE SCREEN TO THE CONTAINER
		if (i == components.length) {
			_container.add(this);
		}

		// MOVE THE SCREEN TO THE FRONT
		moveToFront();

		// REQUEST FOCUS FOR THE PLAN SCREEN
		requestFocus();

		// MAKE THE SCREEN SELECTED SCREEN
		try {
			setSelected(true);
			setClosed(false);
		} catch (final PropertyVetoException pve) {
			logger.error("Property Veto Exception.", pve);
		}

	}

	/**
	 * Updates rowset with sql query results
	 *
	 * @throws SQLException SQL exception thrown while updating the rowset
	 * @throws Exception exception thrown while updating the rowset
	 */
	protected void updateRowset() throws SQLException, Exception {
		logger.debug("Rowset query: [{}].", () -> {
			try {
				return getSelectionQuery();
			} catch (SQLException se) {
				return "*** getSelectionQuery() threw an SQL Exception ***";
			} catch (Exception e) {
				return "*** getSelectionQuery() threw an Exception ***";
			}
		});
		getRowset().setCommand(getSelectionQuery());
		getRowset().execute();
	}

	/**
	 * Updates the rowset and any components dependent upon the rowset
	 *
	 * @param _parentID primary key value of parent record (FK for current rowset)
	 */
	public abstract void updateScreen(final Long _parentID);

}