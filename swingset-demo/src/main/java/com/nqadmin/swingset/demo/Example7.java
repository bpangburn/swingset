
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
 ******************************************************************************/

package com.nqadmin.swingset.demo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSDataGrid;
import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSJdbcRowSetImpl;

/**
 * This example demonstrates the use of an SSDataGrid to display a tabular view
 * of the supplier_part_data table.
 * <p>
 * It adds a ComboRenderer with a lookup to the supplier_data table for the supplier name,
 * and adds a DateRenderer for the ship date column.
 */
public class Example7 extends JFrame {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 5925004336834854311L;

	/**
	 * declarations
	 */
	SSConnection ssConnection = null;
	SSJdbcRowSetImpl rowset = null;
	SSDataGrid dataGrid = null;
	String url;

	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = LogManager.getLogger(Example7.class);

	/**
	 * Constructor for Example7
	 * <p>
	 * @param _dbConn - database connection
	 */
	public Example7(final Connection _dbConn) {

		// SET SCREEN TITLE
			super("Example7");

		// SET CONNECTION
			ssConnection = new SSConnection(_dbConn);

		// SET SCREEN DIMENSIONS
			setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);

		// INITIALIZE SCREEN & DATAGRID
			init();
	}

	/**
	 * Initialize the screen & datagrid
	 */
	private void init() {

		// INTERACT WITH DATABASE IN TRY/CATCH BLOCK
			try {
			// INITIALIZE DATABASE CONNECTION AND COMPONENTS
				rowset = new SSJdbcRowSetImpl(ssConnection.getConnection());
				rowset.setCommand("SELECT supplier_part_id, supplier_id, part_id, quantity, ship_date FROM supplier_part_data ORDER BY supplier_id, part_id;");

			// SETUP THE DATA GRID - SET THE HEADER BEFORE SETTING THE ROWSET
				dataGrid = new SSDataGrid();
				dataGrid.setHeaders(new String[] { "Supplier-Part ID", "Supplier Name", "Part Name", "Quantity", "Ship Date" });
				dataGrid.setSSRowSet(rowset);
				dataGrid.setMessageWindow(this);

			// DISABLES NEW INSERTIONS TO THE DATABASE. - NOT CURRENTLY WORKING FOR H2
				dataGrid.setInsertion(false);

			//	this.dataGrid.updateUI();

			// MAKE THE SUPPLIER-PART ID UNEDITABLE
				dataGrid.setUneditableColumns(new String[] { "supplier_part_id" });

			// SET A DATE RENDERER FOR ship_date
				dataGrid.setDateRenderer("ship_date");

			// BUILD COMBO RENDERERS FOR SUPPLIER AND PART
			// ADDED STATEMENT "SCROLL INSENSITIVITY" FOR EXAMPLE TO BE COMPATIBLE WITH H2 DATABASE DEFAULT SETTINGS.
				try (Statement stmt = ssConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE)) {

					String[] displayItems = null;
					Integer[] underlyingNumbers = null;

					try (ResultSet rs = stmt
							.executeQuery("SELECT supplier_name, supplier_id FROM supplier_data ORDER BY supplier_name;")) {

						rs.last();
						displayItems = new String[rs.getRow()];
						underlyingNumbers = new Integer[rs.getRow()];
						rs.beforeFirst();

						for (int i = 0; i < displayItems.length; i++) {
							rs.next();
							displayItems[i] = rs.getString("supplier_name");
							underlyingNumbers[i] = new Integer(rs.getInt("supplier_id"));
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
							underlyingNumbers[i] = new Integer(rs.getInt("part_id"));
						}

						dataGrid.setComboRenderer("part_id", displayItems, underlyingNumbers, MainClass.gridColumnWidth);
					}
				}

			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
			}

		// SETUP THE CONTAINER AND ADD THE DATAGRID
			getContentPane().add(dataGrid.getComponent());

		// MAKE THE JFRAME VISIBLE
			setVisible(true);
	}

}