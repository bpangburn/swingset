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
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.formatting.SSCurrencyField;
import com.nqadmin.swingset.formatting.SSDateField;
import com.nqadmin.swingset.formatting.SSFormat;
import com.nqadmin.swingset.formatting.SSFormattedTextField;
import com.nqadmin.swingset.formatting.SSIntegerField;
import com.nqadmin.swingset.formatting.SSNumericField;
import com.nqadmin.swingset.formatting.SSPercentField;
import com.nqadmin.swingset.formatting.SSSSNField;
import com.nqadmin.swingset.formatting.SSTimeField;
import com.nqadmin.swingset.formatting.SSTimestampField;
import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSSyncManager;
import com.nqadmin.swingset.utils.SSUtils;

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
@SuppressWarnings("serial")
public class TestFormattedComponents extends JFrame {

	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = SSUtils.getLogger();
	
	/**
	 * screen label declarations
	 */
	JLabel lblSSDBComboNav = new JLabel("SSDBComboNav"); // SSDBComboBox used just for navigation
	JLabel lblSwingSetFormattedTestPK = new JLabel("Record ID");
	//JLabel lblSSCuitField = new JLabel("SSCuitField");
	JLabel lblSSCurrencyField = new JLabel("SSCurrencyField");
	JLabel lblSSCurrencyFieldNull = new JLabel("SSCurrencyFieldNull");
	JLabel lblSSDateField = new JLabel("SSDateField");
	JLabel lblSSDateFieldNull = new JLabel("SSDateFieldNull");
	JLabel lblSSFormattedTextField = new JLabel("SSFormattedTextField");
	JLabel lblSSIntegerField = new JLabel("SSIntegerField");
	JLabel lblSSIntegerFieldNull = new JLabel("SSIntegerFieldNull");
	JLabel lblSSNumericField = new JLabel("SSNumericField");
	JLabel lblSSPercentField = new JLabel("SSPercentField");
	JLabel lblSSSSNField = new JLabel("SSSSNField");
	JLabel lblSSTimeField = new JLabel("SSTimeField");
	JLabel lblSSTimestampField = new JLabel("SSTimestampField");
	JLabel lblDebugField = new JLabel("DebugField");
	JLabel lblDebugFieldNull = new JLabel("DebugFieldNull");

	/**
	 * bound component declarations
	 */
	SSTextField txtSwingSetFormattedTestPK = new SSTextField();
	//SSCuitField fmtSSCuitField = new SSCuitField();
	SSCurrencyField fmtSSCurrencyField = new SSCurrencyField();
	SSCurrencyField fmtSSCurrencyFieldNull = new SSCurrencyField();
	SSDateField fmtSSDateField = new SSDateField(SSFormat.DATE_MMDDYYYY_SLASH);
	SSDateField fmtSSDateFieldNull = new SSDateField(SSFormat.DATE_YYYYMMDD_STROKE);
	SSFormattedTextField fmtSSFormattedTextField = new SSFormattedTextField();
	SSIntegerField fmtSSIntegerField = new SSIntegerField();
	SSIntegerField fmtSSIntegerFieldNull = new SSIntegerField();
	SSNumericField fmtSSNumericField = new SSNumericField();
	SSPercentField fmtSSPercentField = new SSPercentField();
	SSSSNField fmtSSSSNField = new SSSSNField();
	SSTimeField fmtSSTimeField = new SSTimeField();
	SSTimestampField fmtSSTimestampField = new SSTimestampField();
	DebugField fmtDebugField = new DebugField();
	DebugField fmtDebugFieldNull = new DebugField();

	/**
	 * database component declarations
	 */
	Connection connection = null;
	RowSet rowset = null;
	SSDataNavigator navigator = null;
	RowsModel rowsModel;

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
	@SuppressWarnings("LeakingThisInConstructor")
	public TestFormattedComponents(final Connection _dbConn) {
		
		// SET SCREEN TITLE
		super("SwingSet Formatted Component Test");
		DemoUtil.initExampleFrame(this, null);
		
		// SET CONNECTION
		connection = _dbConn;
		
		// SET SCREEN DIMENSIONS
		setSize(MainClass.childScreenWidth, MainClass.childScreenHeightTall);
		
		// SET SCREEN POSITION
		setLocation(DemoUtil.getChildScreenLocation(this.getName()));
		
		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
		try {
			rowset = DemoUtil.getNewRowSet(connection);
			rowset.setCommand("SELECT * FROM swingset_formatted_test_data;");
			rowsModel = RowsModel.create(rowset);
			navigator = new SSDataNavigator(rowsModel);
		} catch (final SQLException se) {
			logger.log(Level.ERROR, "SQL Exception.", se);
		}
		
		/**
		 * Various navigator overrides needed to support H2
		 * <p>
		 * H2 does not fully support updatable rowset so it must be
		 * re-queried following insert and delete with rowset.execute()
		 */
		rowsModel.setDBNav(new SSDBNavImpl(this) {
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
					logger.log(Level.ERROR, "SQL Exception.", se);
				}
				performRefreshOps();
			}
			
			/**
			 * Re-query the rowset following an insertion. This is needed for H2.
			 */
			@Override
			public void performPostInsertOps() {
				super.performPostInsertOps();
				//TestFormattedComponents.this.cmbSSDBComboNav.setEnabled(true);
				try {
					rowset.execute();
				} catch (final SQLException se) {
					logger.log(Level.ERROR, "SQL Exception.", se);
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
					logger.log(Level.ERROR, "SQL Exception.", se);
				} catch (final Exception e) {
					logger.log(Level.ERROR, "Exception.", e);
				}
				syncManager.sync();
			}
			
		});
		
		// SETUP NAVIGATOR QUERY
		final String query = "SELECT * FROM swingset_formatted_test_data;";
		cmbSSDBComboNav = new SSDBComboBox(connection, query, "swingset_formatted_test_pk", "swingset_formatted_test_pk");
		
		try {
			cmbSSDBComboNav.execute();
		} catch (final SQLException se) {
			logger.log(Level.ERROR, "SQL Exception.", se);
		} catch (final Exception e) {
			logger.log(Level.ERROR, "Exception.", e);
		}
		
		// SETUP SYNCMANAGER, WHICH WILL TAKE CARE OF KEEPING THE COMBO NAVIGATOR AND
		// DATA NAVIGATOR IN SYNC.
		//
		// BEFORE CHANGING THE QUERY OR RE-EXECUTING THE QUERY FOR THE COMBO BOX,
		// YOU HAVE TO CALL THE .async() METHOD
		//
		// AFTER CALLING .execute() ON THE COMBO NAVIGATOR, CALL THE .sync() METHOD
		syncManager = new SSSyncManager(cmbSSDBComboNav, rowsModel);
		syncManager.setSyncColumnName("swingset_formatted_test_pk");
		syncManager.sync();
		
		// SETUP BOUND COMPONENTS
		txtSwingSetFormattedTestPK.bind(rowsModel, "swingset_formatted_test_pk");
		
		//fmtSSCuitField.bind(rowsModel, "ss_cuit_field");
		fmtSSCurrencyField.bind(rowsModel, "ss_currency_field");
		fmtSSCurrencyFieldNull.bind(rowsModel, "ss_currency_field_null");
		fmtSSDateField.bind(rowsModel, "ss_date_field");
		fmtSSDateFieldNull.bind(rowsModel, "ss_date_field_null");
		fmtSSFormattedTextField.bind(rowsModel, "ss_formatted_text_field");
		fmtSSIntegerField.bind(rowsModel, "ss_integer_field");
		fmtSSIntegerFieldNull.bind(rowsModel, "ss_integer_field_null");
		fmtSSNumericField.bind(rowsModel, "ss_numeric_field");
		fmtSSPercentField.bind(rowsModel, "ss_percent_field");
		fmtSSSSNField.bind(rowsModel, "ss_ssn_field");
		fmtSSTimeField.bind(rowsModel, "ss_time_field");
		fmtSSTimestampField.bind(rowsModel, "ss_timestamp_field");
		
		//fmtDebugField.bind(rowsModel, "");
		//fmtDebugFieldNull.bind(rowsModel, "");
		fmtDebugField.setAllowNull(false);
		fmtDebugField.setText("333");
		
		// SET LABEL DIMENSIONS
		lblSSDBComboNav.setPreferredSize(MainClass.labelDim);
		
		lblSwingSetFormattedTestPK.setPreferredSize(MainClass.labelDim);
		
		//lblSSCuitField.setPreferredSize(MainClass.labelDim);
		lblSSCurrencyField.setPreferredSize(MainClass.labelDim);
		lblSSCurrencyFieldNull.setPreferredSize(MainClass.labelDim);
		lblSSDateField.setPreferredSize(MainClass.labelDim);
		lblSSDateFieldNull.setPreferredSize(MainClass.labelDim);
		lblSSFormattedTextField.setPreferredSize(MainClass.labelDim);
		lblSSIntegerField.setPreferredSize(MainClass.labelDim);
		lblSSIntegerFieldNull.setPreferredSize(MainClass.labelDim);
		lblSSNumericField.setPreferredSize(MainClass.labelDim);
		lblSSPercentField.setPreferredSize(MainClass.labelDim);
		lblSSSSNField.setPreferredSize(MainClass.labelDim);
		lblSSTimeField.setPreferredSize(MainClass.labelDim);
		lblSSTimestampField.setPreferredSize(MainClass.labelDim);
		lblDebugField.setPreferredSize(MainClass.labelDim);
		lblDebugFieldNull.setPreferredSize(MainClass.labelDim);
		
		// SET BOUND COMPONENT DIMENSIONS
		cmbSSDBComboNav.setPreferredSize(MainClass.ssDim);
		
		txtSwingSetFormattedTestPK.setPreferredSize(MainClass.ssDim);
		
		//fmtSSCuitField.setPreferredSize(MainClass.ssDim);
		fmtSSCurrencyField.setPreferredSize(MainClass.ssDim);
		fmtSSCurrencyFieldNull.setPreferredSize(MainClass.ssDim);
		fmtSSDateField.setPreferredSize(MainClass.ssDim);
		fmtSSDateFieldNull.setPreferredSize(MainClass.ssDim);
		fmtSSFormattedTextField.setPreferredSize(MainClass.ssDim);
		fmtSSIntegerField.setPreferredSize(MainClass.ssDim);
		fmtSSIntegerFieldNull.setPreferredSize(MainClass.ssDim);
		fmtSSNumericField.setPreferredSize(MainClass.ssDim);
		fmtSSPercentField.setPreferredSize(MainClass.ssDim);
		fmtSSSSNField.setPreferredSize(MainClass.ssDim);
		fmtSSTimeField.setPreferredSize(MainClass.ssDim);
		fmtSSTimestampField.setPreferredSize(MainClass.ssDim);
		fmtDebugField.setPreferredSize(MainClass.ssDim);
		fmtDebugFieldNull.setPreferredSize(MainClass.ssDim);
		
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
		//contentPane.add(lblSSCuitField, constraints);
		//constraints.gridy++;
		contentPane.add(lblSSCurrencyField, constraints);
		constraints.gridy++;
		contentPane.add(lblSSCurrencyFieldNull, constraints);
		constraints.gridy++;
		contentPane.add(lblSSDateField, constraints);
		constraints.gridy++;
		contentPane.add(lblSSDateFieldNull, constraints);
		constraints.gridy++;
		contentPane.add(lblSSFormattedTextField, constraints);
		constraints.gridy++;
		contentPane.add(lblSSIntegerField, constraints);
		constraints.gridy++;
		contentPane.add(lblSSIntegerFieldNull, constraints);
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
		constraints.gridy++;
		contentPane.add(lblDebugField, constraints);
		constraints.gridy++;
		contentPane.add(lblDebugFieldNull, constraints);
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		
		contentPane.add(cmbSSDBComboNav, constraints);
		constraints.gridy++;
		contentPane.add(txtSwingSetFormattedTestPK, constraints);
		constraints.gridy++;
		//contentPane.add(fmtSSCuitField, constraints);
		//constraints.gridy++;
		contentPane.add(fmtSSCurrencyField, constraints);
		constraints.gridy++;
		contentPane.add(fmtSSCurrencyFieldNull, constraints);
		constraints.gridy++;
		contentPane.add(fmtSSDateField, constraints);
		constraints.gridy++;
		contentPane.add(fmtSSDateFieldNull, constraints);
		constraints.gridy++;
		contentPane.add(fmtSSFormattedTextField, constraints);
		constraints.gridy++;
		contentPane.add(fmtSSIntegerField, constraints);
		constraints.gridy++;
		contentPane.add(fmtSSIntegerFieldNull, constraints);
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
		constraints.gridy++;
		contentPane.add(fmtDebugField, constraints);
		constraints.gridy++;
		contentPane.add(fmtDebugFieldNull, constraints);
		
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

		try (ResultSet rs = connection.createStatement()
				.executeQuery("SELECT nextval('swingset_formatted_test_seq') as nextVal;"))
		{
			
			// GET THE NEW RECORD ID.
			rs.next();
			final int recordPK = rs.getInt("nextVal");
			txtSwingSetFormattedTestPK.setText(String.valueOf(recordPK));
		} catch(final SQLException se) {
			logger.log(Level.ERROR, "SQL Exception occured during setting default values.",se);
		} catch(final Exception e) {
			logger.log(Level.ERROR, "Exception occured during setting default values.",e);
		}
	}
}
