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
package com.nqadmin.swingset.demo;

import java.awt.Container;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JMenuBar;
import javax.swing.plaf.InternalFrameUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.utils.SSDataGridScreenHelper;

/**
 * This example demonstrates the use of an SSDataGrid to display a tabular view
 * of the supplier_part_data table.
 * <p>
 * It adds a ComboRenderer with a lookup to the supplier_data table for the supplier name,
 * and adds a DateRenderer for the ship date column.
 * <p>
 * This example is identical to Example7, but utilizes
 * com.nqadmin.swingset.utils.SSDataGridScreenHelper to minimize
 * coding.
 */
public class Example7UsingHelper extends SSDataGridScreenHelper {

	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = LogManager.getLogger(Example7UsingHelper.class);

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * rowset query
	 */
	private static final String rowsetQuery = "SELECT supplier_part_id, supplier_id, part_id, quantity, ship_date FROM supplier_part_data ORDER BY supplier_id, part_id;";
	
	/**
	 * table/grid column headings
	 */
	private static final String[] tableHeaders = { "Supplier-Part ID", "Supplier Name", "Part Name", "Quantity", "Ship Date" };

	/**
	 * Constructor for Example7UsingHelper
	 * <p>
	 * @param _dbConn - database connection
	 * @param _container parent window/container
	 */
	public Example7UsingHelper(final Connection _dbConn, final Container _container) {

		// Parent Constructor
//		public SSDataGridScreenHelper(final String _title, final Container _parentContainer, final Connection _connection,
//				final String _pkColumn, final Long _parentID) {
		super("Example 7 Using Helper", _container, _dbConn, "supplier_part_id", null);

		// Hide the frame since we're putting the JInternalFrame in its own JFrame for the demo
		setBorder(null);
		
		// Finish Initialization
		initScreen();
		// updateScreen(); // Force a grid.setRowSet() to a new rowset for testing.
	}

	@Override
	protected void configureDataGrid() {
		// INTERACT WITH DATABASE IN TRY/CATCH BLOCK
		try {
//		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
//			rowset = new JdbcRowSetImpl(connection);
//			rowset.setCommand("SELECT supplier_part_id, supplier_id, part_id, quantity, ship_date FROM supplier_part_data ORDER BY supplier_id, part_id;");

		// SETUP THE DATA GRID - SET THE HEADER BEFORE SETTING THE ROWSET
//			dataGrid = new SSDataGrid();
//			dataGrid.setHeaders(new String[] { "Supplier-Part ID", "Supplier Name", "Part Name", "Quantity", "Ship Date" });
//			dataGrid.setRowSet(rowset);
			dataGrid.setMessageWindow(this);

		// DISABLES NEW INSERTIONS TO THE DATABASE. - NOT CURRENTLY WORKING FOR H2
			dataGrid.setInsertion(false);

		// MAKE THE SUPPLIER-PART ID UNEDITABLE
			dataGrid.setUneditableColumns(new String[] { "supplier_part_id" });

		// SET A DATE RENDERER FOR ship_date
			dataGrid.setDateRenderer("ship_date");

		// BUILD COMBO RENDERERS FOR SUPPLIER AND PART
		// ADDED STATEMENT "SCROLL INSENSITIVITY" FOR EXAMPLE TO BE COMPATIBLE WITH H2 DATABASE DEFAULT SETTINGS.
			try (Statement stmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE)) {

				String[] displayItems;
				Integer[] underlyingNumbers;

				try (ResultSet rs = stmt
						.executeQuery("SELECT supplier_name, supplier_id FROM supplier_data ORDER BY supplier_name;")) {

					rs.last();
					displayItems = new String[rs.getRow()];
					underlyingNumbers = new Integer[rs.getRow()];
					rs.beforeFirst();

					for (int i = 0; i < displayItems.length; i++) {
						rs.next();
						displayItems[i] = rs.getString("supplier_name");
						underlyingNumbers[i] = rs.getInt("supplier_id");
					}

					dataGrid.setComboRenderer("supplier_id", displayItems, underlyingNumbers, MainClass.gridColumnWidth);
				}

				try (ResultSet rs = stmt.executeQuery("SELECT part_name, part_id FROM part_data ORDER BY part_name;")) {
					rs.last();
					displayItems = new String[rs.getRow()];
					underlyingNumbers = new Integer[rs.getRow()];
					rs.beforeFirst();

					for (int i = 0; i < displayItems.length; i++) {
						rs.next();
						displayItems[i] = rs.getString("part_name");
						underlyingNumbers[i] = rs.getInt("part_id");
					}

					dataGrid.setComboRenderer("part_id", displayItems, underlyingNumbers, MainClass.gridColumnWidth);
				}
			}

		} catch (final SQLException se) {
			logger.error("SQL Exception.", se);
		}

//	// SETUP THE CONTAINER AND ADD THE DATAGRID
//		getContentPane().add(dataGrid.getComponent());
//
//	// MAKE THE JFRAME VISIBLE
//		setVisible(true);
	}

	@Override
	protected void addCustomListeners() throws Exception {
		// NOTHING TO DO...
	}

	@Override
	public void closeChildScreens() {
		// NOTHING TO DO...
	}

	@Override
	protected void configureToolBars() {
		// NOTHING TO DO...
	}
	
	@Override
	protected void configureSSCellEditing() {
		// NOTHING TO DO...
	}
	
	@Override
	protected String[] getDefaultColumnNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object[] getDefaultColumnValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected String[] getHeaders() {
		return tableHeaders;
	}
	
	@Override
	protected JMenuBar getCustomMenu() {
		// NOTHING TO DO...
		return null;
	}

	@Override
	protected String getRowsetQuery() {
		return rowsetQuery;
	}
	
	// THIS IS A HACK TO HIDE THE TITLE BAR SINCE WE'RE PUTTING THE JINTERNALFRAME IN ITS OWN JFRAME FOR THE SWINGSET DEMO
	// https://stackoverflow.com/a/51254020
	@Override
	public void setUI(InternalFrameUI _ui) {
		super.setUI(_ui); // this gets called internally when updating the ui and makes the northPane reappear
		BasicInternalFrameUI frameUI = (BasicInternalFrameUI) getUI(); // so...
		if (frameUI != null) frameUI.setNorthPane(null); // lets get rid of it
	}
	
	@Override
	protected void updateSSDBComboBoxes() {

		// IF NEEDED, ADD CODE/METHODS TO RE-QUERY/RE-POPULATE THE COMBO RENDERER.
		// THIS WOULD BE NEEDED IF THE SSDBComboBox LOOKUP NEEDED TO CHANGE BASED
		// ON A VALUE IN THE CURRENT RECORD.

	}

}
