
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSJdbcRowSetImpl;

/**
 * This example demonstrates the use of SSTextDocument to display information in
 * SSDBComboBox (supplier and part) and SSTextField (quantity). The navigation
 * is done with SSDataNavigator.
 */
public class Example3 extends JFrame {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 4859550616628544511L;
	
	/**
	 * screen label declarations
	 */
	JLabel lblSupplierPartID = new JLabel("Supplier-Part ID");
	JLabel lblSupplierName = new JLabel("Supplier");
	JLabel lblPartName = new JLabel("Part");
	JLabel lblQuantity = new JLabel("Quantity");

	/**
	 * bound component declarations
	 */
	SSTextField txtSupplierPartID = new SSTextField();
	SSDBComboBox cmbSupplierName = null;
	SSDBComboBox cmbPartName = null;
	SSTextField txtQuantity = new SSTextField();

	/**
	 * database component declarations
	 */
	SSConnection ssConnection = null;
	SSJdbcRowSetImpl rowset = null;
	SSDataNavigator navigator = null;
	
	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = LogManager.getLogger(Example3.class);

	/**
	 * Constructor for Example3
	 * 
	 * @param url - path to SQL to create suppliers & parts database
	 */
	public Example3(Connection _dbConn) {
		
		// SET SCREEN TITLE
			super("Example3");
			
		// SET CONNECTION
			ssConnection = new SSConnection(_dbConn);
		
		// SET SCREEN DIMENSIONS
			setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);

		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
			try {
				this.rowset = new SSJdbcRowSetImpl(ssConnection.getConnection());
				this.rowset.setCommand("SELECT * FROM supplier_part_data");
				this.navigator = new SSDataNavigator(this.rowset);
			} catch (SQLException se) {
				logger.error("SQL Exception.", se);
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
			private static final long serialVersionUID = 4343059684161003109L;

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
							.executeQuery("SELECT nextval('supplier_part_data_seq') as nextVal;");
					rs.next();
					int supplierPartID = rs.getInt("nextVal");
					txtSupplierPartID.setText(String.valueOf(supplierPartID));
					rs.close();
				
				// SET OTHER DEFAULTS
					 Example3.this.cmbSupplierName.setSelectedValue(0);
					 Example3.this.cmbPartName.setSelectedValue(0);
					 Example3.this.txtQuantity.setText("0");
					
				} catch(SQLException se) {
					logger.error("SQL Exception occured initializing new record.",se);								
				} catch(Exception e) {
					logger.error("Exception occured initializing new record.",e);
				}		
				
			}

			/**
			 * Requery the rowset following an insertion. This is needed for H2.
			 */
			@Override
			public void performPostInsertOps() {
				super.performPostInsertOps();
				try {
					Example3.this.rowset.execute();
				} catch (SQLException se) {
					logger.error("SQL Exception.", se);
				}
			}
			
			/**
			 * Requery the rowset following a deletion. This is needed for H2.
			 */
			@Override
			public void performPostDeletionOps() {
				super.performPostDeletionOps();
				try {
					Example3.this.rowset.execute();
				} catch (SQLException se) {
					logger.error("SQL Exception.", se);
				}
			}

		});

		// SETUP DB COMBO QUERIES
			String query = "SELECT * FROM supplier_data;";
			this.cmbSupplierName = new SSDBComboBox(this.ssConnection, query, "supplier_id", "supplier_name");
			
			query = "SELECT * FROM part_data;";
			this.cmbPartName = new SSDBComboBox(this.ssConnection, query, "part_id", "part_name");
		
		// BIND THE COMPONENTS TO THE DATABASE COLUMNS
			this.txtSupplierPartID.bind(this.rowset, "supplier_part_id");
			this.cmbSupplierName.bind(this.rowset, "supplier_id");
			this.cmbPartName.bind(this.rowset, "part_id");
			this.txtQuantity.bind(this.rowset, "quantity");
			
		// RUN DB COMBO QUERIES
			try {
				this.cmbPartName.execute();
				this.cmbSupplierName.execute();
	
			} catch (SQLException se) {
				logger.error("SQL Exception.", se);
			} catch (Exception e) {
				logger.error("Exception.", e);
			}
			
		// SET LABEL DIMENSIONS	
			this.lblSupplierPartID.setPreferredSize(MainClass.labelDim);
			this.lblSupplierName.setPreferredSize(MainClass.labelDim);
			this.lblPartName.setPreferredSize(MainClass.labelDim);
			this.lblQuantity.setPreferredSize(MainClass.labelDim);

		// SET BOUND COMPONENT DIMENSIONS
			this.txtSupplierPartID.setPreferredSize(MainClass.ssDim);
			this.cmbSupplierName.setPreferredSize(MainClass.ssDim);
			this.cmbPartName.setPreferredSize(MainClass.ssDim);
			this.txtQuantity.setPreferredSize(MainClass.ssDim);

		// SETUP THE CONTAINER AND LAYOUT THE COMPONENTS
			Container contentPane = getContentPane();
			contentPane.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
	
			constraints.gridx = 0;
			constraints.gridy = 0;
			contentPane.add(this.lblSupplierPartID, constraints);
			constraints.gridy = 1;
			contentPane.add(this.lblSupplierName, constraints);
			constraints.gridy = 2;
			contentPane.add(this.lblPartName, constraints);
			constraints.gridy = 3;
			contentPane.add(this.lblQuantity, constraints);
	
			constraints.gridx = 1;
			constraints.gridy = 0;
			contentPane.add(this.txtSupplierPartID, constraints);
			constraints.gridy = 1;
			contentPane.add(this.cmbSupplierName, constraints);
			constraints.gridy = 2;
			contentPane.add(this.cmbPartName, constraints);
			constraints.gridy = 3;
			contentPane.add(this.txtQuantity, constraints);
	
			constraints.gridx = 0;
			constraints.gridy = 4;
			constraints.gridwidth = 2;
			contentPane.add(this.navigator, constraints);

		// DISABLE THE PRIMARY KEY
			txtSupplierPartID.setEnabled(false);

		// MAKE THE JFRAME VISIBLE
			setVisible(true);
	}

}

/*
 * $Log$ Revision 1.10 2012/06/07 15:54:38 beevo Modified example for
 * compatibility with H2 database.
 *
 * Revision 1.9 2005/02/14 18:50:25 prasanth Updated to remove calls to
 * deprecated methods.
 *
 * Revision 1.8 2005/02/04 22:40:12 yoda2 Updated Copyright info.
 *
 * Revision 1.7 2004/11/11 15:04:38 yoda2 Using TextPad, converted all tabs to
 * "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.6 2004/11/01 19:18:51 yoda2 Fixed 0.9.X compatibility issues.
 *
 * Revision 1.5 2004/10/25 22:01:16 yoda2 Updated JavaDoc for new datasource
 * abstraction layer in 0.9.0 release.
 *
 */
