
/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;

import com.nqadmin.swingset.SSDataGrid;
import com.nqadmin.swingset.SSDataValue;
import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSJdbcRowSetImpl;

/**
 * This example is similar to Example5, demonstrating the use of an SSDataGrid
 * to display a tabular view of the suppliers & parts data. It adds a
 * ComboRenderer for the color.
 */
public class Example6 extends JFrame {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 1727893372201402700L;
	
	/**
	 * declarations
	 */
	SSConnection ssConnection = null;
	SSJdbcRowSetImpl rowset = null;
	SSDataGrid dataGrid = null;
	String url;

	/**
	 * Constructor for Example6
	 * 
	 * @param _url - path to SQL to create suppliers & parts database
	 */
	public Example6(String _url) {
		super("Example 6");
		this.url = _url;
		setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);
		init();
	}

	/**
	 * Initialize the screen & datagrid
	 */
	private void init() {

		// INTERACT WITH DATABASE IN TRY/CATCH BLOCK
			try {
			// INITIALIZE DATABASE CONNECTION AND COMPONENTS
				System.out.println("url from ex 6: " + this.url);
				this.ssConnection = new SSConnection(
						"jdbc:h2:mem:suppliers_and_parts;INIT=runscript from '" + this.url + "'", "sa", "");
				this.ssConnection.setDriverName("org.h2.Driver");
				this.ssConnection.createConnection();
	
				this.rowset = new SSJdbcRowSetImpl(this.ssConnection.getConnection());
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
	
			// SETUP COMBO RENDER FOR COLOR COLUMN
				this.dataGrid.setComboRenderer("color_code", new String[] { "Red", "Green", "Blue" },
						new Integer[] { 0,1,2 }, MainClass.gridColumnWidth);
				
			// SET DEFAULTS FOR NEW RECORDS
			// THIS CODE IS NOT CURRENTLY USED AS THERE IS AN ISSUE ADDING RECORDS IN H2
				this.dataGrid.setDefaultValues(new String[] { "part_name", "color_code", "weight", "city" },
						new Object[] { "", 1, 20, "Default City" });
	
				this.dataGrid.setPrimaryColumn("part_id");
				this.dataGrid.setSSDataValue(new SSDataValue() {
					@Override
					public Object getPrimaryColumnValue() {
						
						int partID = 0;
						
						try {
						// GET THE NEW RECORD ID.	
							ResultSet rs = ssConnection.getConnection()
									.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
									.executeQuery("SELECT nextval('part_data_seq') as nextVal;");
							rs.next();
							partID = rs.getInt("nextVal");
							rs.close();
	
						} catch(SQLException se) {
							se.printStackTrace();
							System.out.println("Error occured obtaining new Primary Key.\n" + se.getMessage());								
						} catch(Exception e) {
							e.printStackTrace();
							System.out.println("Error occured obtaining new Primary Key.\n" + e.getMessage());
						}	
						
						//System.out.println(partID);
						
						return partID;
					}
				});
	
			} catch (SQLException se) {
				se.printStackTrace();
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
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