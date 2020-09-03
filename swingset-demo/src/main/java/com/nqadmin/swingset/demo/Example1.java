
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

import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSJdbcRowSetImpl;

/**
 * This example displays data from the supplier_data table. 
 * SSTextFields are used to display supplier id, name, city,
 * and status.
 * <p>
 * Record navigation is handled with a SSDataNavigator.
 */

public class Example1 extends JFrame {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 1613223721461838426L;
	
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
	SSTextField txtSupplierStatus = new SSTextField();

	/**
	 * database component declarations
	 */
	SSConnection ssConnection = null;
	SSJdbcRowSetImpl rowset = null;
	SSDataNavigator navigator = null;
	
	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = LogManager.getLogger(Example1.class);

	/**
	 * Constructor for Example1
	 * <p>
	 * @param _dbConn - database connection
	 */
	public Example1(Connection _dbConn) {
		
		// SET SCREEN TITLE
			super("Example1");
			
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
			private static final long serialVersionUID = -7698780157683623074L;

			/**
			 * Obtain and set the PK value for the new record & perform any other actions needed before an insert.
			 */
			@Override
			public void performPreInsertOps() {
				
				super.performPreInsertOps();
				
				try {

				// GET THE NEW RECORD ID.	
					ResultSet rs = ssConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
							.executeQuery("SELECT nextval('supplier_data_seq') as nextVal;");
					rs.next();
					int supplierID = rs.getInt("nextVal");
					txtSupplierID.setText(String.valueOf(supplierID));
					rs.close();
				
				// SET OTHER DEFAULTS
					 Example1.this.txtSupplierName.setText(null);
					 Example1.this.txtSupplierCity.setText(null);
					 Example1.this.txtSupplierStatus.setText("0");			
					
				} catch(SQLException se) {
					logger.error("SQL Exception occured initializing new record.", se);								
				} catch(Exception e) {
					logger.error("Exception occured initializing new record.", e);
				}		
				
			}

			/**
			 * Requery the rowset following an insertion. This is needed for H2.
			 */
			@Override
			public void performPostInsertOps() {
				super.performPostInsertOps();
				try {
					Example1.this.rowset.execute();
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
					Example1.this.rowset.execute();
				} catch (SQLException se) {
					logger.error("SQL Exception.", se);
				}
			}

		});

		// BIND THE COMPONENTS TO THE DATABASE COLUMNS
			this.txtSupplierID.bind(this.rowset, "supplier_id");
			this.txtSupplierName.bind(this.rowset, "supplier_name");
			this.txtSupplierCity.bind(this.rowset, "city");
			this.txtSupplierStatus.bind(this.rowset, "status");

		// SET LABEL DIMENSIONS
			this.lblSupplierID.setPreferredSize(MainClass.labelDim);
			this.lblSupplierName.setPreferredSize(MainClass.labelDim);
			this.lblSupplierCity.setPreferredSize(MainClass.labelDim);
			this.lblSupplierStatus.setPreferredSize(MainClass.labelDim);

		// SET BOUND COMPONENT DIMENSIONS
			this.txtSupplierID.setPreferredSize(MainClass.ssDim);
			this.txtSupplierName.setPreferredSize(MainClass.ssDim);
			this.txtSupplierCity.setPreferredSize(MainClass.ssDim);
			this.txtSupplierStatus.setPreferredSize(MainClass.ssDim);

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
			contentPane.add(this.txtSupplierStatus, constraints);
	
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