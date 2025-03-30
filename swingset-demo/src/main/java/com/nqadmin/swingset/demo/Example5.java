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

import java.awt.BorderLayout;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.swing.JFrame;

import com.nqadmin.swingset.SSDataGrid;
import com.nqadmin.swingset.utils.SSUtils;

/**
 * This example demonstrates the use of an SSDataGrid to display a tabular view
 * of the part_data table.
 * <p>
 * For an editable table, users can delete rows by selecting the row to be deleted
 * and pressing Ctrl-X. By default, a confirmation message is displayed before deletion.
 */
@SuppressWarnings("serial")
public class Example5 extends JFrame {

    private static final Logger logger = SSUtils.getLogger();
	
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
			DemoUtil.initExampleFrame(this, null);

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
			logger.log(Level.ERROR, "SQL Exception occured initializing new record.",se);
		} catch(final Exception e) {
			logger.log(Level.ERROR, "Exception occured initializing new record.",e);
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
				rowset = DemoUtil.getNewRowSet(connection);
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
				logger.log(Level.ERROR, "SQL Exception.", se);
			}

		// MAKE THE JFRAME VISIBLE
			setVisible(true);
	}

}
