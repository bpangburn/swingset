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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

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
	 * Arbitrary negative value that can be used as the primary key
	 * value in a WHERE clause if there is no mapping/null mapping
	 * for a SSDBComboBox. Null could cause an SQL Exception, but
	 * an arbitrary should just return with 0 records.
	 * <p><pre>
	 * For example:
	 *     "SELECT * FROM part_data WHERE part_id = {null};"
	 * may throw an SQL Exception whereas:
	 *     "SELECT * FROM part_data WHERE part_id = -998877;"
	 * should execute properly with 0 records returned.
	 * </pre><p>
	 * ASSUMES that -998877 would never be a primary/foreign key value.
	 */
	public final static long hopefullyNoPKValue = -998877;

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = SSUtils.getLogger();

	/**
	 * unique serial ID
	 */
	private static final long serialVersionUID = 9108320379306383142L;
	
	/**
	 * SwingSet properties
	 */
	protected static final Properties ssProps = SSProperties.getProperties();

	private Connection connection; // Database connection.
	private RowSet rowset; // Rowset to be used for screen/form.
	private String pkColumn; // Primary key column name for rowset.
	
	private Long parentID = null; // Primary key value of parent record (FK for current rowset).
	
	private int defaultX = 0; // Default top left horizontal offset for screen/form.
	private int defaultY = 0; // Default top left vertical offset for screen/form.
	
	private Container parentContainer = null; // container/window from which current screen was opened
	
//	private String fullSQL = null; // Full SQL for rowset
//	private String selectSQL = null; // SELECT CLAUSE FOR RECORDSET SUCH THAT selectSQL + parentID + " " + orderBySQL IS A VALID QUERY
//	private String orderBySQL = null; // ORDER BY CLAUSE FOR RECORDSET INCLUDING AT LEAST A SEMICOLON

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
	 * Helper method to add a screen closer to a JMenu. Usually this is 
	 * the "File" JMenu.
	 * 
	 * @param _jInternalFrame screen that the menu item should close
	 * @param _menu menu to which "Close" should be added
	 */
	public static void addInternalFrameCloserToMenu(JInternalFrame _jInternalFrame, JMenu _menu) {
		
		// CREATE MENU ITEMS FOR EACH TOP-LEVEL MENU
		JMenuItem menuFileClose = _menu.add("Close");
		
		// ADD LISTENERS FOR EACH MENU ITEM
		menuFileClose.addActionListener((event) -> {
			try {
				_jInternalFrame.setClosed(true);
			} catch (PropertyVetoException _pve) {
				logger.warn("Unable to close {}.",()-> {return _jInternalFrame.getTitle();});
			}
		});
		
	}

	/**
	 * Closes any child screens that are open.
	 */
	protected abstract void closeChildScreens();

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
	 * Adds and configures any required buttons to the main application toolbar.
	 *
	 * <pre>
	 * Separators added with:
	 * {@code
	 * 	getRootFrame().getToolBar().addSeparator();
	 * }
	 *
	 * Buttons added with:
	 * {@code
	 *  btnMyButtonName = getRootFrame().addToolBarButton(new AbstractAction() {
	 *  	public void actionPerformed(ActionEvent ae) {
	 *          myButtonNameActionMethod();
	 *      }
	 *  }, "My Button Description", "images/myButtonGraphic.gif");
	 * }
	 * </pre>
	 */
	protected abstract void configureToolBars();

	/**
	 * @return a database Connection
	 */
	protected Connection getConnection() {
		return connection;
	}
	
	/**
	 * Builds and returns custom menu bar and with applicable listeners.
	 * Cleanest to implement listeners with Lambda expressions.
	 *
	 * @return JMenuBar complete with any menu items and listeners (ideally using Lambdas)
	 */
	protected abstract JMenuBar getCustomMenu();
	
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
	 * @return Parent window/container.
	 *
	 * @deprecated Starting in 4.0.0+ use {@link #getRootFrame()} instead.
	 */
	@Deprecated
	protected Frame getMainFrame() {
		return getRootFrame();
	}
	
	/**
	 * Helper method to provide a JMenuBar with a single File Menu
	 * with a Menu Item to close the screen.
	 * 
	 * @param _jInternalFrame screen that the menu item should close
	 * 
	 * @return a JMenuBar with a single File menu containing a single Close menu item
	 */
	public static JMenuBar getMenuBarWithInternalFrameCloser(JInternalFrame _jInternalFrame) {
		
		// CREATE MENU BAR
		JMenuBar thisMenu = new JMenuBar();
		
		// CREATE TOP-LEVEL MENUS FOR THE MENU BAR.
		JMenu menuFile = new JMenu("File");
		
		addInternalFrameCloserToMenu(_jInternalFrame, menuFile);
		
		// ADD TOP-LEVEL MENUS TO THE MENU BAR.
		thisMenu.add(menuFile);

		// RETURN
		return thisMenu;
		
	}

	/**
	 * @return the parent container/window for this screen
	 */
	protected Container getParentContainer() {
		return parentContainer;
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
	 * Convenience method when developer wants to pass the result of
	 * getSelectedMapping() for a SSDBComboBox directly into the WHERE clause of
	 * a SQL query. Will either return not null Long from getSelectedMapping
	 * or hopefullyNoPKValue (-998877), but should never return null.
	 * <p>
	 * Presumes that there are no records matching hopefullyNoPKValue (-998877)
	 * so if this value is returned, the query will not return any records, but
	 * should not throw a SQL Exception.
	 * 
	 * @param _combo combobox for which to retrieve selected mapping
	 * @return Long containing selectedMapping if not null, otherwise hopefullyNoPKValue
	 */
	public static Long getPKForQuery(com.nqadmin.swingset.SSDBComboBox _combo) {
		Long result = _combo.getSelectedMapping();
		if (result==null) {
			result = hopefullyNoPKValue;
		}
		
		return result;
	}
	
	/**
	 * @return the root component for the current component tree
	 */
	public JFrame getRootFrame() {
		return (JFrame) SwingUtilities.getRoot(this);
	}

	/**
	 * @return the rowset
	 */
	protected RowSet getRowset() {
		return rowset;
	}
	
	/**
	 * Method implemented by screen developer to provide the entire SQL query for the rowset
	 * on demand. It may or may not utilize getParentID() for filtering the results.
	 * 
	 * @return the full SQL Query for the rowset
	 */
	protected abstract String getRowsetQuery();
	
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
	 * @param _connection the database connection
	 */
	protected void setConnection(final Connection _connection) {
		connection = _connection;
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
		setDefaultX(Integer.valueOf(_defaultX));
		setDefaultY(Integer.valueOf(_defaultY));
	}
	
	/**
	 * Sets any default values for the screen's components.
	 * <p>
	 * For a Form View screen, if there is a call to SSDBNavImpl.performPreInsertOps(),
	 * it should take care of clearing all of the component values before defaults
	 * are set.
	 * <p>
	 * For a Data Grid screen, this will be a one-time call to
	 * {@link com.nqadmin.swingset.SSDataGrid#setDefaultValues(String[],Object[])};
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
	 * Sets the parent container/window for this screen if applicable.
	 * 
	 * @param _parentContainer parent screen/window to which the form/grid should be attached
	 */
	protected void setParentContainer(final Container _parentContainer) {
		parentContainer = _parentContainer;
	}

	/**
	 * Sets the parent ID used to query records for the current screen.
	 * 
	 * Null if the there is not a foreign key to include in rowset query.
	 * 
	 * Normally used in conjunction with updateScreen() to refresh a child
	 * screen if there is a navigation in a parent screen.
	 * 
	 * @param _parentID the parentID to set
	 */
	public void setParentID(final Long _parentID) {
		parentID = _parentID;
	}

	/**
	 * @param _pkColumn the pkColumn to set
	 */
	protected void setPkColumn(final String _pkColumn) {
		pkColumn = _pkColumn;
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
				return getRowsetQuery();
//			} catch (SQLException se) {
//				return "*** getSelectionQuery() threw an SQL Exception ***";
			} catch (Exception e) {
				return "*** getSelectionQuery() threw an Exception ***";
			}
		});
		getRowset().setCommand(getRowsetQuery());
		getRowset().execute();
	}
	
	/**
	 * Updates the rowset and any components dependent upon the rowset.
	 * 
	 * Implementation should presume that parentID has already been updated
	 * if applicable.
	 */
	public abstract void updateScreen();
	
	/**
	 * Convenience method to combine setParentID() and updateScreen().
	 * 
	 * @param _parentID the parentID to set
	 */
	public void updateScreen(final Long _parentID) {
		setParentID(_parentID);
		updateScreen();
	}
	
	/**
	 * Used to update any SSDBComboBox that change when parent ID changes (i.e.,
	 * whenever the rowset for the screen changes). This is called from initScreen()
	 * after bindComponents() and addComponents(). It is also called from
	 * updateScreen() after the rowset has been requeried.
	 * <p>
	 * Generally there will be calls to cmbMySSDBCombo.setQuery(myNewCmbSQL); and
	 * cmbMySSDBCombo.execute();
	 * <p>
	 * If the SSDBComboBox query is unchanged regardless of the rowset, then it is
	 * sufficient to just call cmbMySSDBCombo.execute();
	 * <p>
	 * It is REDUNDANT/UNNECESSARY to call cmbMySSDBCombo.execute() here and in
	 * bindComponents().
	 */
	protected abstract void updateSSDBComboBoxes();


}