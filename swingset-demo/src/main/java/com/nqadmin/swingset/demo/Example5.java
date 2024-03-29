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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.RowSet;
import java.sql.ResultSet;
import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.rowset.JdbcRowSetImpl;
import com.nqadmin.swingset.SSDataGrid;
import java.awt.BorderLayout;

/**
 * This example demonstrates the use of an SSDataGrid to display a tabular view
 * of the part_data table.
 * <p>
 * For an editable table, users can delete rows by selecting the row to be deleted
 * and pressing Ctrl-X. By default, a confirmation message is displayed before deletion.
 */
public class Example5 extends JFrame {

	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = LogManager.getLogger(Example5.class);

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -5126011569315467420L;
	
	/**
	 * data grid
	 */
	SSDataGrid dataGrid = null;
	
	/**
	 * database component declarations
	 */
	Connection connection = null;
	RowSet rowset = null;

	/**
	 * Constructor for Example5
	 * <p>
	 * @param _dbConn - database connection
	 */
	public Example5(final Connection _dbConn) {

		// SET SCREEN TITLE
			super("Example5");

		// SET CONNECTION
			connection = _dbConn;

		// SET SCREEN DIMENSIONS
			setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);
			
		// SET SCREEN POSITION
			setLocation(DemoUtil.getChildScreenLocation(this.getName()));

		// INITIALIZE SCREEN & DATAGRID
			init();
	}

	private Object getNewPrimaryKey() {
		try (final ResultSet rs = connection.createStatement(
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
				.executeQuery("SELECT nextval('part_data_seq') as nextVal;")) {
			// GET THE NEW RECORD ID.
			rs.next();
			return rs.getInt("nextVal");
		} catch(final SQLException se) {
			logger.error("SQL Exception occured initializing new record.",se);
		} catch(final Exception e) {
			logger.error("Exception occured initializing new record.",e);
		}
		return null;
	}

	/**
	 * Initialize the screen & datagrid
	 */
	private void init() {

		// INTERACT WITH DATABASE IN TRY/CATCH BLOCK
			try {
			// INITIALIZE DATABASE CONNECTION AND COMPONENTS
				rowset = new JdbcRowSetImpl(connection);
				rowset.setCommand("SELECT * FROM part_data ORDER BY part_name;");

			// SETUP THE DATA GRID - SET THE HEADER BEFORE SETTING THE ROWSET
				dataGrid = new SSDataGrid();
				dataGrid.setHeaders(new String[] { "Part ID", "Part Name", "Color Code", "Weight", "City" });
				dataGrid.setRowSet(rowset);
				dataGrid.setMessageWindow(this);

			// DISABLES NEW INSERTIONS TO THE DATABASE.
				dataGrid.setInsertion(false);


			// MAKE THE PART ID UNEDITABLE
				dataGrid.setUneditableColumns(new String[] { "part_id" });

			// SETUP THE CONTAINER AND ADD THE DATAGRID
				getContentPane().setLayout(new BorderLayout());
				getContentPane().add(dataGrid.getComponent(), BorderLayout.CENTER);

				DataGridExampleSupport.setup(logger, getContentPane(),
						rowset, dataGrid,
						0, () -> getNewPrimaryKey(),
						new String[]{ "part_name", "color_code", "weight", "city", },
						new Object[]{ null, 0, 1, "New Roads" }
				);

			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
			}

		// MAKE THE JFRAME VISIBLE
			setVisible(true);
	}

}
