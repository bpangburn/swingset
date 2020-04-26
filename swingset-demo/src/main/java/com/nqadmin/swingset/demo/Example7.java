
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

import com.nqadmin.swingset.SSDataGrid;
import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSJdbcRowSetImpl;

/**
 * This example is similar to Example6, demonstrating the use of an SSDataGrid
 * to display a tabular view of the suppliers & parts data. It adds a
 * ComboRenderer with a lookup to another table for the supplier name and adds a
 * DateRenderer for the ship date.
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
	 * Constructor for Example7
	 * 
	 * @param _url - path to SQL to create suppliers & parts database
	 */
	public Example7(Connection _dbConn) {
		
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
				this.rowset = new SSJdbcRowSetImpl(ssConnection.getConnection());
				this.rowset.setCommand("SELECT supplier_part_id, supplier_id, part_id, quantity, ship_date FROM supplier_part_data ORDER BY supplier_id, part_id;");
	
			// SETUP THE DATA GRID - SET THE HEADER BEFORE SETTING THE ROWSET
				this.dataGrid = new SSDataGrid();
				this.dataGrid.setHeaders(new String[] { "Supplier-Part ID", "Supplier Name", "Part Name", "Quantity", "Ship Date" });
				this.dataGrid.setSSRowSet(this.rowset);
				this.dataGrid.setMessageWindow(this);
				
			// DISABLES NEW INSERTIONS TO THE DATABASE. - NOT CURRENTLY WORKING FOR H2
				this.dataGrid.setInsertion(false);
	
			//	this.dataGrid.updateUI();
	
			// MAKE THE SUPPLIER-PART ID UNEDITABLE
				this.dataGrid.setUneditableColumns(new String[] { "supplier_part_id" });
				
			// SET A DATE RENDERER FOR ship_date
				this.dataGrid.setDateRenderer("ship_date");

			// BUILD COMBO RENDERERS FOR SUPPLIER AND PART
			// ADDED STATEMENT "SCROLL INSENSITIVITY" FOR EXAMPLE TO BE COMPATIBLE WITH H2 DATABASE DEFAULT SETTINGS.
				try (Statement stmt = this.ssConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
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
	
						this.dataGrid.setComboRenderer("supplier_id", displayItems, underlyingNumbers, MainClass.gridColumnWidth);
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
	
						this.dataGrid.setComboRenderer("part_id", displayItems, underlyingNumbers, MainClass.gridColumnWidth);
					}
				}
	
			} catch (SQLException se) {
				se.printStackTrace();
			}
			
		// SETUP THE CONTAINER AND ADD THE DATAGRID
			getContentPane().add(this.dataGrid.getComponent());

		// MAKE THE JFRAME VISIBLE
			setVisible(true);
	}

}

/*
 * $Log$ Revision 1.7 2012/06/07 15:54:38 beevo Modified example for
 * compatibilty with H2 database.
 *
 * Revision 1.6 2005/02/14 18:50:25 prasanth Updated to remove calls to
 * deprecated methods.
 *
 * Revision 1.5 2005/02/04 22:40:12 yoda2 Updated Copyright info.
 *
 * Revision 1.4 2004/11/11 15:04:38 yoda2 Using TextPad, converted all tabs to
 * "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.3 2004/10/25 22:01:15 yoda2 Updated JavaDoc for new datasource
 * abstraction layer in 0.9.0 release.
 *
 * Revision 1.2 2004/10/25 19:52:12 prasanth Modified to work with new SwingSet
 * (SSConnection & SSRowSet)
 *
 * Revision 1.1 2003/12/18 20:14:43 prasanth Initial commit.
 *
 */