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
import com.nqadmin.swingset.SSComboBox;
import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.utils.SSSyncManager;

/**
 * This example displays data from the part_data table.
 * SSTextFields are used to display part id, name, weight,
 * and city. SSComboBox is used to display color.
 * <p>
 * Record navigation can be handled with a SSDataNavigator or
 * with a SSDBComboBox.
 * <p>
 * Since the navigation can take place by multiple methods, the navigation
 * controls have to be synchronized. This is accomplished with the
 * SSSyncManager.
 * <p>
 * IMPORTANT: The SSDBComboBox and the SSRowSet queries should select the same
 * records and in the same order. Otherwise the SSSyncManager will spend a lot of
 * time looping through records to match.
 */
public class Example4 extends JFrame {

	/**
	 * Log4j2 Logger
	 */
    static final Logger logger = LogManager.getLogger(Example4.class);

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -6594890166578252237L;
	
	/**
	 * screen label declarations
	 */
	JLabel lblSelectPart = new JLabel("Part");
	JLabel lblPartID = new JLabel("Part ID");
	JLabel lblPartName = new JLabel("Part Name");
	JLabel lblPartColor = new JLabel("Color");
	JLabel lblPartWeight = new JLabel("Weight");
	JLabel lblPartCity = new JLabel("City");

	/**
	 * bound component declarations
	 */
	SSTextField txtPartID = new SSTextField();
	SSTextField txtPartName = new SSTextField();
	SSComboBox cmbPartColor = new SSComboBox();
	SSTextField txtPartWeight = new SSTextField();
	SSTextField txtPartCity = new SSTextField();

	/**
	 * database component declarations
	 */
	Connection connection = null;
	RowSet rowset = null;
	SSDataNavigator navigator = null;

	/**
	 * combo navigator and sync manger
	 */
	SSDBComboBox cmbSelectPart = null;
	SSSyncManager syncManager;

	/**
	 * Constructor for Example4
	 * <p>
	 * @param _dbConn - database connection
	 */
	public Example4(final Connection _dbConn) {

		// SET SCREEN TITLE
			super("Example4");

		// SET CONNECTION
			connection = _dbConn;

		// SET SCREEN DIMENSIONS
			setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);
			
		// SET SCREEN POSITION
			setLocation(DemoUtil.getChildScreenLocation(this.getName()));

		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
			try {
				rowset = new JdbcRowSetImpl(connection);
				rowset.setCommand("SELECT * FROM part_data;");
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
				private static final long serialVersionUID = 9018468389405536891L;

				/**
				 * Re-enable DB Navigator following insertion Cancel
				 */
				@Override
				public void performCancelOps() {
					super.performCancelOps();
					cmbSelectPart.setEnabled(true);
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
					performRefreshOps();
				}

				/**
				 * Requery the rowset following an insertion. This is needed for H2.
				 */
				@Override
				public void performPostInsertOps() {
					super.performPostInsertOps();
					cmbSelectPart.setEnabled(true);
					try {
						rowset.execute();
					} catch (final SQLException se) {
						logger.error("SQL Exception.", se);
					}
					performRefreshOps();
				}

				/**
				 * Obtain and set the PK value for the new record & perform any other actions needed before an insert.
				 */
				@Override
				public void performPreInsertOps() {

					// SSDBNavImpl will clear the component values
					super.performPreInsertOps();

					try {

					// GET THE NEW RECORD ID.
						final ResultSet rs = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
								.executeQuery("SELECT nextval('part_data_seq') as nextVal;");
						rs.next();
						final int partID = rs.getInt("nextVal");
						txtPartID.setText(String.valueOf(partID));
						rs.close();

					// DISABLE PART SELECTOR
						cmbSelectPart.setEnabled(false);

					// SET OTHER DEFAULTS
//						txtPartName.setText(null);
//						cmbPartColor.setSelectedValue(0);
//						txtPartWeight.setText("0");
//						txtPartCity.setText(null);

					} catch(final SQLException se) {
						logger.error("SQL Exception occured initializing new record.",se);
					} catch(final Exception e) {
						logger.error("Exception occured initializing new record.",e);
					}

				}

				/**
				 * Manage sync manager during a Refresh
				 */
				@Override
				public void performRefreshOps() {
					super.performRefreshOps();
					syncManager.async();
					try {
						cmbSelectPart.execute();
					} catch (final SQLException se) {
						logger.error("SQL Exception.", se);
					} catch (final Exception e) {
						logger.error("Exception.", e);
					}
					syncManager.sync();
				}

			});

			// SETUP NAVIGATOR QUERY
				final String query = "SELECT * FROM part_data;";
				cmbSelectPart = new SSDBComboBox(connection, query, "part_id", "part_name");

				try {
					cmbSelectPart.execute();
				} catch (final SQLException se) {
					logger.error("SQL Exception.", se);
				} catch (final Exception e) {
					logger.error("Exception.", e);
				}

			// SETUP THE COMBO BOX OPTIONS TO BE DISPLAYED AND THEIR CORRESPONDING VALUES

				// This is the normal case, specify an option for each mapping
				cmbPartColor.setOptions(new String[] { "Red", "Green", "Blue" });

				// This is used to initialize some stuff for Example4Advanced
				cmbPartColorChangeOptions();

			// BIND THE COMPONENTS TO THE DATABASE COLUMNS
				txtPartID.bind(rowset, "part_id");
				txtPartName.bind(rowset, "part_name");
				cmbPartColor.bind(rowset, "color_code");
				txtPartWeight.bind(rowset, "weight");
				txtPartCity.bind(rowset, "city");
				//SSComboBox.testComboAdjustForNull(cmbPartColor);

			// SETUP SYNCMANAGER, WHICH WILL TAKE CARE OF KEEPING THE COMBO NAVIGATOR AND
			// DATA NAVIGATOR IN SYNC.
			//
			// BEFORE CHANGING THE QUERY OR RE-EXECUTING THE QUERY FOR THE COMBO BOX,
			// YOU HAVE TO CALL THE .async() METHOD
			//
			// AFTER CALLING .execute() ON THE COMBO NAVIGATOR, CALL THE .sync() METHOD
				syncManager = new SSSyncManager(cmbSelectPart, navigator);
				syncManager.setSyncColumnName("part_id");
				syncManager.sync();

			// SET LABEL DIMENSIONS
				lblSelectPart.setPreferredSize(MainClass.labelDim);
				lblPartID.setPreferredSize(MainClass.labelDim);
				lblPartName.setPreferredSize(MainClass.labelDim);
				lblPartColor.setPreferredSize(MainClass.labelDim);
				lblPartWeight.setPreferredSize(MainClass.labelDim);
				lblPartCity.setPreferredSize(MainClass.labelDim);

			// SET BOUND COMPONENT DIMENSIONS
				cmbSelectPart.setPreferredSize(MainClass.ssDim);
				txtPartID.setPreferredSize(MainClass.ssDim);
				txtPartName.setPreferredSize(MainClass.ssDim);
				cmbPartColor.setPreferredSize(MainClass.ssDim);
				txtPartWeight.setPreferredSize(MainClass.ssDim);
				txtPartCity.setPreferredSize(MainClass.ssDim);

			// SETUP THE CONTAINER AND LAYOUT THE COMPONENTS
				final Container contentPane = getContentPane();
				final GridBagConstraints constraints = new GridBagConstraints();
				contentPane.setLayout(new GridBagLayout());

				constraints.gridx = 0;
				constraints.gridy = 0;
				contentPane.add(lblSelectPart, constraints);
				constraints.gridy = 1;
				contentPane.add(lblPartID, constraints);
				constraints.gridy = 2;
				contentPane.add(lblPartName, constraints);
				constraints.gridy = 3;
				contentPane.add(lblPartColor, constraints);
				constraints.gridy = 4;
				contentPane.add(lblPartWeight, constraints);
				constraints.gridy = 5;
				contentPane.add(lblPartCity, constraints);

				constraints.gridx = 1;
				constraints.gridy = 0;
				contentPane.add(cmbSelectPart, constraints);
				constraints.gridy = 1;
				contentPane.add(txtPartID, constraints);
				constraints.gridy = 2;
				contentPane.add(txtPartName, constraints);
				constraints.gridy = 3;
				contentPane.add(cmbPartColor, constraints);
				constraints.gridy = 4;
				contentPane.add(txtPartWeight, constraints);
				constraints.gridy = 5;
				contentPane.add(txtPartCity, constraints);

				constraints.gridx = 0;
				constraints.gridy = 6;
				constraints.gridwidth = 2;
				contentPane.add(navigator, constraints);

				constraints.gridy = 7;
				constraints.gridwidth = 1;
				
			// DISABLE THE PRIMARY KEY
				txtPartID.setEnabled(false);

			// MAKE THE JFRAME VISIBLE
				setVisible(true);
				pack();
			}

	void cmbPartColorChangeOptions() {}
}
