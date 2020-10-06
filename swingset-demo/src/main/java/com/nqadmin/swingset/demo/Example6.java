
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

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSDataGrid;
import com.nqadmin.swingset.SSDataValue;
import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSJdbcRowSetImpl;

/**
 * This example is similar to Example5, demonstrating the use of an SSDataGrid
 * to display a tabular view of the part_data table. It adds a
 * ComboRenderer for the color column.
 */
public class Example6 extends JFrame {

	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = LogManager.getLogger(Example6.class);

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 1727893372201402700L;
	SSDataGrid dataGrid = null;
	SSJdbcRowSetImpl rowset = null;
	/**
	 * declarations
	 */
	SSConnection ssConnection = null;

	String url;

	/**
	 * Constructor for Example6
	 * <p>
	 * @param _dbConn - database connection
	 */
	public Example6(final Connection _dbConn) {

		// SET SCREEN TITLE
			super("Example6");

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
				rowset.setCommand("SELECT * FROM part_data ORDER BY part_name;");

			// SETUP THE DATA GRID - SET THE HEADER BEFORE SETTING THE ROWSET
				dataGrid = new SSDataGrid();
				dataGrid.setHeaders(new String[] { "Part ID", "Part Name", "Color Code", "Weight", "City" });
				dataGrid.setSSRowSet(rowset);
				dataGrid.setMessageWindow(this);

			// DISABLES NEW INSERTIONS TO THE DATABASE. - NOT CURRENTLY WORKING FOR H2
				dataGrid.setInsertion(false);

			// MAKE THE PART ID UNEDITABLE
				dataGrid.setUneditableColumns(new String[] { "part_id" });

			// SETUP COMBO RENDER FOR COLOR COLUMN
				dataGrid.setComboRenderer("color_code", new String[] { "Red", "Green", "Blue" },
						new Integer[] { 0,1,2 }, MainClass.gridColumnWidth);

			// SET DEFAULTS FOR NEW RECORDS
			// THIS CODE IS NOT CURRENTLY USED AS THERE IS AN ISSUE ADDING RECORDS IN H2
				dataGrid.setDefaultValues(new String[] { "part_name", "color_code", "weight", "city" },
						new Object[] { "", 1, 20, "Default City" });

				dataGrid.setPrimaryColumn("part_id");
				dataGrid.setSSDataValue(new SSDataValue() {
					@Override
					public Object getPrimaryColumnValue() {

						int partID = 0;

						try {
						// GET THE NEW RECORD ID.
							final ResultSet rs = ssConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
									.executeQuery("SELECT nextval('part_data_seq') as nextVal;");
							rs.next();
							partID = rs.getInt("nextVal");
							rs.close();

						} catch(final SQLException se) {
							logger.error("SQL Exception occured obtaining primary key value for new record.",se);
						} catch(final Exception e) {
							logger.error("Exception occured obtaining primary key value for new record.",e);
						}

						return partID;
					}
				});

			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
			}

		// SETUP THE CONTAINER AND ADD THE DATAGRID
			getContentPane().add(dataGrid.getComponent());

		// MAKE THE JFRAME VISIBLE
			setVisible(true);

	}

}