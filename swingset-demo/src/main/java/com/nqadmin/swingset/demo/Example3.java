
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
 * This example displays data from the supplier_part_data table. 
 * SSTextFields are used to display supplier-part id and quantity.  
 * SSDBComboBoxes are used to display supplier name and part name
 * based on queries against the supplier_data and part_data tables.
 * <p>
 * Record navigation is handled with a SSDataNavigator.
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
	 * <p>
	 * @param _dbConn - database connection
	 */
	public Example3(final Connection _dbConn) {
		
		// SET SCREEN TITLE
			super("Example3");
			
		// SET CONNECTION
			ssConnection = new SSConnection(_dbConn);
		
		// SET SCREEN DIMENSIONS
			setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);

		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
			try {
				rowset = new SSJdbcRowSetImpl(ssConnection.getConnection());
				rowset.setCommand("SELECT * FROM supplier_part_data");
				navigator = new SSDataNavigator(rowset);
			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
			}

		/**
		 * Various navigator overrides needed to support H2
		 * H2 does not fully support updatable rowset so it must be
		 * re-queried following insert and delete with rowset.execute()
		 */
		navigator.setDBNav(new SSDBNavImpl(this) {
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
					final ResultSet rs = ssConnection.getConnection()
							.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
							.executeQuery("SELECT nextval('supplier_part_data_seq') as nextVal;");
					rs.next();
					final int supplierPartID = rs.getInt("nextVal");
					txtSupplierPartID.setText(String.valueOf(supplierPartID));
					rs.close();
				
				// SET OTHER DEFAULTS
					 cmbSupplierName.setSelectedValue(0);
					 cmbPartName.setSelectedValue(0);
					 txtQuantity.setText("0");
					
				} catch(final SQLException se) {
					logger.error("SQL Exception occured initializing new record.",se);								
				} catch(final Exception e) {
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
					rowset.execute();
				} catch (final SQLException se) {
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
					rowset.execute();
				} catch (final SQLException se) {
					logger.error("SQL Exception.", se);
				}
			}

		});

		// SETUP DB COMBO QUERIES
			String query = "SELECT * FROM supplier_data;";
			cmbSupplierName = new SSDBComboBox(ssConnection, query, "supplier_id", "supplier_name");
			
			query = "SELECT * FROM part_data;";
			cmbPartName = new SSDBComboBox(ssConnection, query, "part_id", "part_name");
		
		// BIND THE COMPONENTS TO THE DATABASE COLUMNS
			txtSupplierPartID.bind(rowset, "supplier_part_id");
			cmbSupplierName.bind(rowset, "supplier_id");
			cmbPartName.bind(rowset, "part_id");
			txtQuantity.bind(rowset, "quantity");
			
		// RUN DB COMBO QUERIES
			try {
				cmbPartName.execute();
				cmbSupplierName.execute();
	
			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
			} catch (final Exception e) {
				logger.error("Exception.", e);
			}
			
		// SET LABEL DIMENSIONS	
			lblSupplierPartID.setPreferredSize(MainClass.labelDim);
			lblSupplierName.setPreferredSize(MainClass.labelDim);
			lblPartName.setPreferredSize(MainClass.labelDim);
			lblQuantity.setPreferredSize(MainClass.labelDim);

		// SET BOUND COMPONENT DIMENSIONS
			txtSupplierPartID.setPreferredSize(MainClass.ssDim);
			cmbSupplierName.setPreferredSize(MainClass.ssDim);
			cmbPartName.setPreferredSize(MainClass.ssDim);
			txtQuantity.setPreferredSize(MainClass.ssDim);

		// SETUP THE CONTAINER AND LAYOUT THE COMPONENTS
			final Container contentPane = getContentPane();
			contentPane.setLayout(new GridBagLayout());
			final GridBagConstraints constraints = new GridBagConstraints();
	
			constraints.gridx = 0;
			constraints.gridy = 0;
			contentPane.add(lblSupplierPartID, constraints);
			constraints.gridy = 1;
			contentPane.add(lblSupplierName, constraints);
			constraints.gridy = 2;
			contentPane.add(lblPartName, constraints);
			constraints.gridy = 3;
			contentPane.add(lblQuantity, constraints);
	
			constraints.gridx = 1;
			constraints.gridy = 0;
			contentPane.add(txtSupplierPartID, constraints);
			constraints.gridy = 1;
			contentPane.add(cmbSupplierName, constraints);
			constraints.gridy = 2;
			contentPane.add(cmbPartName, constraints);
			constraints.gridy = 3;
			contentPane.add(txtQuantity, constraints);
	
			constraints.gridx = 0;
			constraints.gridy = 4;
			constraints.gridwidth = 2;
			contentPane.add(navigator, constraints);

		// DISABLE THE PRIMARY KEY
			txtSupplierPartID.setEnabled(false);

		// MAKE THE JFRAME VISIBLE
			setVisible(true);
	}

}