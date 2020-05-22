
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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.nqadmin.swingset.SSComboBox;
import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSJdbcRowSetImpl;

/**
 * This example demonstrates the use of SSTextDocument to display information in
 * SSTextField (name and city) and SSComboBox (status). The navigation is done
 * with SSDataNavigator.
 */

public class Example2 extends JFrame {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 9205688923559422257L;
	
	/**
	 * screen label declarations
	 */
	JLabel lblSupplierID = new JLabel("Supplier ID");
	JLabel lblSupplierName = new JLabel("Name");
	JLabel lblSupplierCity = new JLabel("City");
	JLabel lblSupplierStatus = new JLabel("Status");
	
	/**
	 * bound component declarations
	 */
	SSTextField txtSupplierID = new SSTextField();
	SSTextField txtSupplierName = new SSTextField();
	SSTextField txtSupplierCity = new SSTextField();
	SSComboBox cmbSupplierStatus = new SSComboBox();

	/**
	 * database component declarations
	 */
	SSConnection ssConnection = null;
	SSJdbcRowSetImpl rowset = null;
	SSDataNavigator navigator = null;

	/**
	 * Constructor for Example2
	 * 
	 * @param url - path to SQL to create suppliers & parts database
	 */
	public Example2(Connection _dbConn) {
		
		// SET SCREEN TITLE
			super("Example2");
			
		// SET CONNECTION
			ssConnection = new SSConnection(_dbConn);
		
		// SET SCREEN DIMENSIONS
			setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);
			
		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
			try {
				this.rowset = new SSJdbcRowSetImpl(ssConnection.getConnection());
				this.rowset.setCommand("SELECT * FROM supplier_data");
				this.navigator = new SSDataNavigator(this.rowset);
			} catch (SQLException se) {
				se.printStackTrace();
			}

		/**
		 * Various navigator overrides needed to support H2
		 * H2 does not fully support updatable rowset so it must be
		 * re-queried following insert and delete with rowset.execute()
		 */
		this.navigator.setDBNav(new SSDBNavImpl(this) {
			
			/**
			 * unique serial id
			 */
			private static final long serialVersionUID = 6964661066285402119L;
			
			/**
			 * Obtain and set the PK value for the new record & perform any other actions needed before an insert.
			 */
			@Override
			public void performPreInsertOps() {
				
				super.performPreInsertOps();
				
				try {

				// GET THE NEW RECORD ID.	
					ResultSet rs = ssConnection.getConnection()
							.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
							.executeQuery("SELECT nextval('supplier_data_seq') as nextVal;");
					rs.next();
					int supplierID = rs.getInt("nextVal");
					txtSupplierID.setText(String.valueOf(supplierID));
					rs.close();
				
				// SET OTHER DEFAULTS
					 Example2.this.txtSupplierName.setText(null);
					 Example2.this.txtSupplierCity.setText(null);
					 Example2.this.cmbSupplierStatus.setSelectedValue(0);
					
				} catch(SQLException se) {
					se.printStackTrace();
					System.out.println("Error occured during pre insert operation.\n" + se.getMessage());								
				} catch(Exception e) {
					e.printStackTrace();
					System.out.println("Error occured during pre insert operation.\n" + e.getMessage());
				}		
				
			}

			/**
			 * Requery the rowset following an insertion. This is needed for H2.
			 */
			@SuppressWarnings("restriction")
			@Override
			public void performPostInsertOps() {
				super.performPostInsertOps();
				try {
					Example2.this.rowset.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			/**
			 * Requery the rowset following a deletion. This is needed for H2.
			 */
			@SuppressWarnings("restriction")
			@Override
			public void performPostDeletionOps() {
				super.performPostDeletionOps();
				try {
					Example2.this.rowset.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		});

		// SETUP THE COMBO BOX OPTIONS TO BE DISPLAYED AND THEIR CORRESPONDING VALUES
		//	 LETS ASSUME THE STATUS CODE TO TEXT MAPPINGS
		// 		10 -> BAD
		// 		20 -> BETTER
		// 		30 -> GOOD
			int[] codes = { 10, 20, 30 };
			String[] options = { "Bad", "Better", "Good" };
			this.cmbSupplierStatus.setOptions(options, codes);

		// BIND THE COMPONENTS TO THE DATABASE COLUMNS
			this.txtSupplierID.bind(this.rowset, "supplier_id");
			this.txtSupplierName.bind(this.rowset, "supplier_name");
			this.txtSupplierCity.bind(this.rowset, "city");
			this.cmbSupplierStatus.bind(this.rowset, "status");
			//this.cmbSupplierStatus.setSelectedIndex(1);
		
		// SET LABEL DIMENSIONS
			this.lblSupplierID.setPreferredSize(MainClass.labelDim);
			this.lblSupplierName.setPreferredSize(MainClass.labelDim);
			this.lblSupplierCity.setPreferredSize(MainClass.labelDim);
			this.lblSupplierStatus.setPreferredSize(MainClass.labelDim);

		// SET BOUND COMPONENT DIMENSIONS
			this.txtSupplierID.setPreferredSize(MainClass.ssDim);
			this.txtSupplierName.setPreferredSize(MainClass.ssDim);
			this.txtSupplierCity.setPreferredSize(MainClass.ssDim);
			this.cmbSupplierStatus.setPreferredSize(MainClass.ssDim);

		// SETUP THE CONTAINER AND LAYOUT THE COMPONENTS
			Container contentPane = getContentPane();
			contentPane.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
	
			constraints.gridx = 0;
			constraints.gridy = 0;
			contentPane.add(this.lblSupplierID, constraints);
			constraints.gridy = 1;
			contentPane.add(this.lblSupplierName, constraints);
			constraints.gridy = 2;
			contentPane.add(this.lblSupplierCity, constraints);
			constraints.gridy = 3;
			contentPane.add(this.lblSupplierStatus, constraints);
	
			constraints.gridx = 1;
			constraints.gridy = 0;
			contentPane.add(this.txtSupplierID, constraints);
			constraints.gridy = 1;
			contentPane.add(this.txtSupplierName, constraints);
			constraints.gridy = 2;
			contentPane.add(this.txtSupplierCity, constraints);
			constraints.gridy = 3;
			contentPane.add(this.cmbSupplierStatus, constraints);
	
			constraints.gridx = 0;
			constraints.gridy = 4;
			constraints.gridwidth = 2;
			contentPane.add(this.navigator, constraints);
		
		// DISABLE THE PRIMARY KEY
			txtSupplierID.setEnabled(false);

		// MAKE THE JFRAME VISIBLE
			setVisible(true);

	}

}

/*
 * $Log$ Revision 1.9 2012/06/07 15:54:38 beevo Modified example for
 * compatibility with H2 database.
 *
 * Revision 1.8 2005/02/14 18:50:25 prasanth Updated to remove calls to
 * deprecated methods.
 *
 * Revision 1.7 2005/02/04 22:40:12 yoda2 Updated Copyright info.
 *
 * Revision 1.6 2004/11/11 15:04:38 yoda2 Using TextPad, converted all tabs to
 * "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.5 2004/10/25 22:01:16 yoda2 Updated JavaDoc for new datasource
 * abstraction layer in 0.9.0 release.
 *
 */
