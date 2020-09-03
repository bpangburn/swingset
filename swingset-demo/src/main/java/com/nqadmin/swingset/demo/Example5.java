
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
import java.sql.SQLException;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSDataGrid;
import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSJdbcRowSetImpl;

/**
 * This example demonstrates the use of an SSDataGrid to display a tabular view
 * of the part_data table.
 * <p>
 * For an editable table, users can delete rows by selecting the row to be deleted
 * and pressing Ctrl-X. By default, a confirmation message is displayed before deletion.
 */
public class Example5 extends JFrame {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -5126011569315467420L;
	
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
    private static final Logger logger = LogManager.getLogger(Example5.class);

	/**
	 * Constructor for Example5
	 * <p>
	 * @param _dbConn - database connection
	 */
	public Example5(Connection _dbConn) {
		
		// SET SCREEN TITLE
			super("Example5");
			
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
				this.rowset = new SSJdbcRowSetImpl(ssConnection.getConnection());
				this.rowset.setCommand("SELECT * FROM part_data ORDER BY part_name;");
			
			// SETUP THE DATA GRID - SET THE HEADER BEFORE SETTING THE ROWSET
				this.dataGrid = new SSDataGrid();
				this.dataGrid.setHeaders(new String[] { "Part ID", "Part Name", "Color Code", "Weight", "City" });
				this.dataGrid.setSSRowSet(this.rowset);
				this.dataGrid.setMessageWindow(this);
	
			// DISABLES NEW INSERTIONS TO THE DATABASE. - NOT CURRENTLY WORKING FOR H2
				this.dataGrid.setInsertion(false);
	
			// MAKE THE PART ID UNEDITABLE
				this.dataGrid.setUneditableColumns(new String[] { "part_id" });
	
			} catch (SQLException se) {
				logger.error("SQL Exception.", se);
			}
			
		// SETUP THE CONTAINER AND ADD THE DATAGRID
			getContentPane().add(this.dataGrid.getComponent());

		// MAKE THE JFRAME VISIBLE
			setVisible(true);
	}

}