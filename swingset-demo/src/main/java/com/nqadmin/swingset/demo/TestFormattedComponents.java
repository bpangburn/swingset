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
import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.formatting.SSCuitField;
import com.nqadmin.swingset.formatting.SSCurrencyField;
import com.nqadmin.swingset.formatting.SSDateField;
import com.nqadmin.swingset.formatting.SSFormattedTextField;
import com.nqadmin.swingset.formatting.SSIntegerField;
import com.nqadmin.swingset.formatting.SSNumericField;
import com.nqadmin.swingset.formatting.SSPercentField;
import com.nqadmin.swingset.formatting.SSSSNField;
import com.nqadmin.swingset.formatting.SSTimeField;
import com.nqadmin.swingset.formatting.SSTimestampField;
import com.nqadmin.swingset.utils.SSSyncManager;

/**
 * This example demonstrates all of the Formatted SwingSet Components.
 * <p>
 * There is a separate example screen to demonstrate the
 * Base SwingSet Components.
 * <p>
 * IMPORTANT: The SSDBComboBox and the SSRowSet queries should select the same
 * records and in the same order. Otherwise the SSSyncManager will spend a lot of
 * time looping through records to match.
 */
public class TestFormattedComponents extends JFrame {

	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = LogManager.getLogger(TestFormattedComponents.class);

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -1831202547517957436L;
	
	/**
	 * screen label declarations
	 */
	JLabel lblSSDBComboNav = new JLabel("SSDBComboNav"); // SSDBComboBox used just for navigation
	JLabel lblSwingSetFormattedTestPK = new JLabel("Record ID");
	JLabel lblSSCuitField = new JLabel("SSCuitField");
	JLabel lblSSCurrencyField = new JLabel("SSCurrencyField");
	JLabel lblSSDateField = new JLabel("SSDateField");
	JLabel lblSSFormattedTextField = new JLabel("SSFormattedTextField");
	JLabel lblSSIntegerField = new JLabel("SSIntegerField");
	JLabel lblSSNumericField = new JLabel("SSNumericField");
	JLabel lblSSPercentField = new JLabel("SSPercentField");
	JLabel lblSSSSNField = new JLabel("SSSSNField");
	JLabel lblSSTimeField = new JLabel("SSTimeField");
	JLabel lblSSTimestampField = new JLabel("SSTimestampField");

	/**
	 * bound component declarations
	 */
	SSTextField txtSwingSetFormattedTestPK = new SSTextField();
	SSCuitField fmtSSCuitField = new SSCuitField();
	SSCurrencyField fmtSSCurrencyField = new SSCurrencyField();
	SSDateField fmtSSDateField = new SSDateField(SSDateField.MMDDYYYY);
	SSFormattedTextField fmtSSFormattedTextField = new SSFormattedTextField();
	SSIntegerField fmtSSIntegerField = new SSIntegerField();
	SSNumericField fmtSSNumericField = new SSNumericField();
	SSPercentField fmtSSPercentField = new SSPercentField();
	SSSSNField fmtSSSSNField = new SSSSNField();
	SSTimeField fmtSSTimeField = new SSTimeField();
	SSTimestampField fmtSSTimestampField = new SSTimestampField();

	/**
	 * database component declarations
	 */
	SSConnection ssConnection = null;
	RowSet rowset = null;
	SSDataNavigator navigator = null;

	/**
	 * combo navigator and sync manger
	 */
	SSDBComboBox cmbSSDBComboNav = new SSDBComboBox(); // SSDBComboBox used just for navigation
	SSSyncManager syncManager;

	/**
	 * Constructor for Formatted Component Test
	 *
	 * @param _dbConn - database connection
	 */
	public TestFormattedComponents(final Connection _dbConn) {

		// SET SCREEN TITLE
			super("SwingSet Formatted Component Test");

		// SET CONNECTION
			ssConnection = new SSConnection(_dbConn);

		// SET SCREEN DIMENSIONS
			setSize(MainClass.childScreenWidth, MainClass.childScreenHeightTall);
			
		// SET SCREEN POSITION
			setLocation(DemoUtil.getChildScreenLocation(this.getName()));

		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
			try {
				rowset = new JdbcRowSetImpl(ssConnection.getConnection());
				rowset.setCommand("SELECT * FROM swingset_formatted_test_data;");
				navigator = new SSDataNavigator(rowset);
			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
			}



			/**
			 * Various navigator overrides needed to support H2
			 * <p>
			 * H2 does not fully support updatable rowset so it must be
			 * re-queried following insert and delete with rowset.execute()
			 */
			navigator.setDBNav(new SSDBNavImpl(this) {
				/**
				 * unique serial id
				 */
				private static final long serialVersionUID = 4264119495814589191L;

				/**
				 * Re-enable DB Navigator following insertion Cancel
				 */
				@Override
				public void performCancelOps() {
					super.performCancelOps();
					cmbSSDBComboNav.setEnabled(true);
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
					//TestFormattedComponents.this.cmbSSDBComboNav.setEnabled(true);
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

					super.performPreInsertOps();

					setDefaultValues();
				}

				/**
				 * Manage sync manager during a Refresh
				 */
				@Override
				public void performRefreshOps() {
					super.performRefreshOps();
					syncManager.async();
					try {
						cmbSSDBComboNav.execute();
					} catch (final SQLException se) {
						logger.error("SQL Exception.", se);
					} catch (final Exception e) {
						logger.error("Exception.", e);
					}
					syncManager.sync();
				}

			});

			// SETUP NAVIGATOR QUERY
				final String query = "SELECT * FROM swingset_formatted_test_data;";
				cmbSSDBComboNav = new SSDBComboBox(ssConnection, query, "swingset_formatted_test_pk", "swingset_formatted_test_pk");

				try {
					cmbSSDBComboNav.execute();
				} catch (final SQLException se) {
					logger.error("SQL Exception.", se);
				} catch (final Exception e) {
					logger.error("Exception.", e);
				}

			// SETUP SYNCMANAGER, WHICH WILL TAKE CARE OF KEEPING THE COMBO NAVIGATOR AND
			// DATA NAVIGATOR IN SYNC.
			//
			// BEFORE CHANGING THE QUERY OR RE-EXECUTING THE QUERY FOR THE COMBO BOX,
			// YOU HAVE TO CALL THE .async() METHOD
			//
			// AFTER CALLING .execute() ON THE COMBO NAVIGATOR, CALL THE .sync() METHOD
				syncManager = new SSSyncManager(cmbSSDBComboNav, navigator);
				syncManager.setColumnName("swingset_formatted_test_pk");
				syncManager.sync();

			// SETUP BOUND COMPONENTS
				txtSwingSetFormattedTestPK.bind(rowset, "swingset_formatted_test_pk");

				fmtSSCuitField.bind(rowset, "ss_cuit_field");
				fmtSSCurrencyField.bind(rowset, "ss_currency_field");
				fmtSSDateField.bind(rowset, "ss_date_field");
				fmtSSFormattedTextField.bind(rowset, "ss_formatted_text_field");
				fmtSSIntegerField.bind(rowset, "ss_integer_field");
				fmtSSNumericField.bind(rowset, "ss_numeric_field");
				fmtSSPercentField.bind(rowset, "ss_percent_field");
				fmtSSSSNField.bind(rowset, "ss_ssn_field");
				fmtSSTimeField.bind(rowset, "ss_time_field");
				fmtSSTimestampField.bind(rowset, "ss_timestamp_field");

			// SET LABEL DIMENSIONS
				lblSSDBComboNav.setPreferredSize(MainClass.labelDim);

				lblSwingSetFormattedTestPK.setPreferredSize(MainClass.labelDim);

				lblSSCuitField.setPreferredSize(MainClass.labelDim);
				lblSSCurrencyField.setPreferredSize(MainClass.labelDim);
				lblSSDateField.setPreferredSize(MainClass.labelDim);
				lblSSFormattedTextField.setPreferredSize(MainClass.labelDim);
				lblSSIntegerField.setPreferredSize(MainClass.labelDim);
				lblSSNumericField.setPreferredSize(MainClass.labelDim);
				lblSSPercentField.setPreferredSize(MainClass.labelDim);
				lblSSSSNField.setPreferredSize(MainClass.labelDim);
				lblSSTimeField.setPreferredSize(MainClass.labelDim);
				lblSSTimestampField.setPreferredSize(MainClass.labelDim);

			// SET BOUND COMPONENT DIMENSIONS
				cmbSSDBComboNav.setPreferredSize(MainClass.ssDim);

				txtSwingSetFormattedTestPK.setPreferredSize(MainClass.ssDim);

				fmtSSCuitField.setPreferredSize(MainClass.ssDim);
				fmtSSCurrencyField.setPreferredSize(MainClass.ssDim);
				fmtSSDateField.setPreferredSize(MainClass.ssDim);
				fmtSSFormattedTextField.setPreferredSize(MainClass.ssDim);
				fmtSSIntegerField.setPreferredSize(MainClass.ssDim);
				fmtSSNumericField.setPreferredSize(MainClass.ssDim);
				fmtSSPercentField.setPreferredSize(MainClass.ssDim);
				fmtSSSSNField.setPreferredSize(MainClass.ssDim);
				fmtSSTimeField.setPreferredSize(MainClass.ssDim);
				fmtSSTimestampField.setPreferredSize(MainClass.ssDim);

			// SETUP THE CONTAINER AND LAYOUT THE COMPONENTS
				final Container contentPane = getContentPane();
				contentPane.setLayout(new GridBagLayout());
				final GridBagConstraints constraints = new GridBagConstraints();

				constraints.gridx = 0;
				constraints.gridy = 0;

				contentPane.add(lblSSDBComboNav, constraints);
				constraints.gridy++;
				contentPane.add(lblSwingSetFormattedTestPK, constraints);
				constraints.gridy++;
				contentPane.add(lblSSCuitField, constraints);
				constraints.gridy++;
				contentPane.add(lblSSCurrencyField, constraints);
				constraints.gridy++;
				contentPane.add(lblSSDateField, constraints);
				constraints.gridy++;
				contentPane.add(lblSSFormattedTextField, constraints);
				constraints.gridy++;
				contentPane.add(lblSSIntegerField, constraints);
				constraints.gridy++;
				contentPane.add(lblSSNumericField, constraints);
				constraints.gridy++;
				contentPane.add(lblSSPercentField, constraints);
				constraints.gridy++;
				contentPane.add(lblSSSSNField, constraints);
				constraints.gridy++;
				contentPane.add(lblSSTimeField, constraints);
				constraints.gridy++;
				contentPane.add(lblSSTimestampField, constraints);

				constraints.gridx = 1;
				constraints.gridy = 0;

				contentPane.add(cmbSSDBComboNav, constraints);
				constraints.gridy++;
				contentPane.add(txtSwingSetFormattedTestPK, constraints);
				constraints.gridy++;
				contentPane.add(fmtSSCuitField, constraints);
				constraints.gridy++;
				contentPane.add(fmtSSCurrencyField, constraints);
				constraints.gridy++;
				contentPane.add(fmtSSDateField, constraints);
				constraints.gridy++;
				contentPane.add(fmtSSFormattedTextField, constraints);
				constraints.gridy++;
				contentPane.add(fmtSSIntegerField, constraints);
				constraints.gridy++;
				contentPane.add(fmtSSNumericField, constraints);
				constraints.gridy++;
				contentPane.add(fmtSSPercentField, constraints);
				constraints.gridy++;
				contentPane.add(fmtSSSSNField, constraints);
				constraints.gridy++;
				contentPane.add(fmtSSTimeField, constraints);
				constraints.gridy++;
				contentPane.add(fmtSSTimestampField, constraints);

				constraints.gridx = 0;
				constraints.gridy++;
				constraints.gridwidth = 2;
				contentPane.add(navigator, constraints);

		// DISABLE THE PRIMARY KEY
			txtSwingSetFormattedTestPK.setEnabled(false);

		// MAKE THE JFRAME VISIBLE
			setVisible(true);
			pack();
	}

	/**
	 * Method to set default values following an insert
	 */
	public void setDefaultValues() {

		try {

		// GET THE NEW RECORD ID.
			final ResultSet rs = ssConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
					.executeQuery("SELECT nextval('swingset_formatted_test_seq') as nextVal;");
			rs.next();
			final int recordPK = rs.getInt("nextVal");
			txtSwingSetFormattedTestPK.setText(String.valueOf(recordPK));
			rs.close();

		// SET OTHER DEFAULTS
			fmtSSCuitField.setText(null);
			fmtSSCurrencyField.setText(null);
			fmtSSDateField.setText(null);
			fmtSSFormattedTextField.setText(null);
			fmtSSIntegerField.setText(null);
			fmtSSNumericField.setText(null);
			fmtSSPercentField.setText(null);
			fmtSSSSNField.setText(null);
			fmtSSTimeField.setText(null);
			fmtSSTimestampField.setText(null);

		} catch(final SQLException se) {
			logger.error("SQL Exception occured during setting default values.",se);
		} catch(final Exception e) {
			logger.error("Exception occured during setting default values.",e);
		}


	}

}
