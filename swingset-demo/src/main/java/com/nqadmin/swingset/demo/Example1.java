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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.rowset.JdbcRowSetImpl;
import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.decorators.TextComponentValidator;

/**
 * This example displays data from the supplier_data table.
 * SSTextFields are used to display supplier id, name, city,
 * and status.
 * <p>
 * Record navigation is handled with a SSDataNavigator.
 */

public class Example1 extends JFrame {

	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = LogManager.getLogger(Example1.class);

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
	Connection connection = null;
	RowSet rowset = null;
	SSDataNavigator navigator = null;

	/**
	 * Constructor for Example1
	 * <p>
	 * @param _dbConn - database connection
	 */
	public Example1(final Connection _dbConn) {

		// SET SCREEN TITLE
			super("Example1");

		// SET CONNECTION
			connection = _dbConn;

		// SET SCREEN DIMENSIONS
			setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);
			
		// SET SCREEN POSITION
			setLocation(DemoUtil.getChildScreenLocation(this.getName()));
		
		// SET A VALIDATOR (may be a no-op is disabled in SwingSet library)
			txtSupplierName.getSSCommon().setValidator(new TextComponentValidator() {
				@Override
				public boolean validate() {
					return !jc().getText().equalsIgnoreCase("oops");
				}
			});

		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
			try {
		        rowset = new JdbcRowSetImpl(connection);
				rowset.setCommand("SELECT * FROM supplier_data");
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
			private static final long serialVersionUID = -7698780157683623074L;

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
			 * Obtain and set the PK value for the new record & perform any other actions needed before an insert.
			 */
			@Override
			public void performPreInsertOps() {

				super.performPreInsertOps();

				try {

				// GET THE NEW RECORD ID.
					final ResultSet rs = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
							.executeQuery("SELECT nextval('supplier_data_seq') as nextVal;");
					rs.next();
					final int supplierID = rs.getInt("nextVal");
					txtSupplierID.setText(String.valueOf(supplierID));
					rs.close();

				// SET OTHER DEFAULTS
//					 txtSupplierName.setText(null);
//					 txtSupplierCity.setText(null);
//					 txtSupplierStatus.setText("0");

				} catch(final SQLException se) {
					logger.error("SQL Exception occured initializing new record.", se);
				} catch(final Exception e) {
					logger.error("Exception occured initializing new record.", e);
				}

			}

		});

		// BIND THE COMPONENTS TO THE DATABASE COLUMNS
			txtSupplierID.bind(rowset, "supplier_id");
			txtSupplierName.bind(rowset, "supplier_name");
			txtSupplierCity.bind(rowset, "city");
			txtSupplierStatus.bind(rowset, "status");

		// SET LABEL DIMENSIONS
			lblSupplierID.setPreferredSize(MainClass.labelDim);
			lblSupplierName.setPreferredSize(MainClass.labelDim);
			lblSupplierCity.setPreferredSize(MainClass.labelDim);
			lblSupplierStatus.setPreferredSize(MainClass.labelDim);

		// SET BOUND COMPONENT DIMENSIONS
			txtSupplierID.setPreferredSize(MainClass.ssDim);
			txtSupplierName.setPreferredSize(MainClass.ssDim);
			txtSupplierCity.setPreferredSize(MainClass.ssDim);
			txtSupplierStatus.setPreferredSize(MainClass.ssDim);

		// SETUP THE CONTAINER AND LAYOUT THE COMPONENTS
			final Container contentPane = getContentPane();
			contentPane.setLayout(new GridBagLayout());
			final GridBagConstraints constraints = new GridBagConstraints();

			constraints.gridx = 0;
			constraints.gridy = 0;
			contentPane.add(lblSupplierID, constraints);
			constraints.gridy = 1;
			contentPane.add(lblSupplierName, constraints);
			constraints.gridy = 2;
			contentPane.add(lblSupplierCity, constraints);
			constraints.gridy = 3;
			contentPane.add(lblSupplierStatus, constraints);

			constraints.gridx = 1;
			constraints.gridy = 0;
			contentPane.add(txtSupplierID, constraints);
			constraints.gridy = 1;
			contentPane.add(txtSupplierName, constraints);
			constraints.gridy = 2;
			contentPane.add(txtSupplierCity, constraints);
			constraints.gridy = 3;
			contentPane.add(txtSupplierStatus, constraints);

			constraints.gridx = 0;
			constraints.gridy = 4;
			constraints.gridwidth = 2;
			contentPane.add(navigator, constraints);

		// DISABLE THE PRIMARY KEY
			txtSupplierID.setEnabled(false);

		// MAKE THE JFRAME VISIBLE
			setVisible(true);
			pack();
	}

}
