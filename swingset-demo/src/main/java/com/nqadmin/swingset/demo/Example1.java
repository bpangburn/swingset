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
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2025, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.demo;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

import javax.sql.RowSet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationItem;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;

import com.nqadmin.swingset.SSDBNav;
import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.decorators.TextComponentValidator;
import com.nqadmin.swingset.demo.simpval.SVUtils;
import com.nqadmin.swingset.demo.simpval.StringValidator;
import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSUtils;


/**
 * This example displays data from the supplier_data table.
 * SSTextFields are used to display supplier id, name, city,
 * and status.
 * <p>
 * Record navigation is handled with a SSDataNavigator.
 */

@SuppressWarnings("serial")
public class Example1 extends JFrame {
    private static final Logger logger = SSUtils.getLogger();

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
	Connection connection;
	SSDataNavigator navigator;
	RowsModel rowsModel;

	private void cleanup()
	{
		//connection = null;
		//rowset = null;
		//navigator.cleanup();
		//navigator = null;
	}

	RowSet getRowSet() {
		return rowsModel.getRowSet();
	}

	/**
	 * Constructor for Example1
	 * <p>
	 * @param _dbConn - database connection
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public Example1(final Connection _dbConn)
	{
		// Set screen title.
		super("Example1");
		DemoUtil.initExampleFrame(this, this::cleanup);
		
		JFrame frame = this;
		
		// Set connection.
		connection = _dbConn;
		
		// Set screen dimensions.
		setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);
		
		// Set screen position.
		setLocation(DemoUtil.getChildScreenLocation(this.getName()));
		
		// Set a validator.
		final boolean USE_SIMPLE_VALIDATION = false;
		//SSTextComponentValidationItem valSupplierName = null;
		ValidationItem decoSupplierName = null;
		Function<String, Boolean> validateSupplierName = (str) -> {
			boolean valid = str == null || !str.matches("(?i).*oops.{0,2}$");
			//logger.log(Level.TRACE, ()->sf("validateSupplierName %s", valid));
			return valid;
		};
		if (!USE_SIMPLE_VALIDATION) {
			txtSupplierName.setValidator(TextComponentValidator.create(
					validateSupplierName));
			txtSupplierCity.setValidator(TextComponentValidator.create(
					(str) -> !str.matches(".*X")));
		} else {
			SwingValidationGroup.setComponentName(txtSupplierName, "Supplier Name");
			StringValidator validator = SVUtils.getStringValidator(
					validateSupplierName, () -> "Supplier can not end with 'oops..'");
			decoSupplierName = SVUtils.decorator(txtSupplierName, validator);
		}

		RowSetButtons rsButtons = new RowSetButtons()
		{
			@Override
			RowSetButtons.ScreenInfo getScreenInfo()
			{
				return new ScreenInfo(logger, rowsModel);
			}
		};
		
		/**
		 * Various navigator overrides needed to support H2
		 * H2 does not fully support updatable rowset so it must be
		 * re-queried following insert and delete with rowset.execute()
		 */
		//rowsModel.setDBNav(new SSDBNavImpl(this)
		SSDBNav dbNav = new SSDBNavImpl(this)
		{
			/**
			 * Re-query the RowSet following a deletion. This is needed for H2.
			 */
			@Override
			public void performPostDeletionOps()
			{
				super.performPostDeletionOps();
				try {
					getRowSet().execute();
				} catch (final SQLException se) {
					logger.log(Level.ERROR, "SQL Exception.", se);
				}
			}

			/**
			 * Requery the rowset following an insertion. This is needed for H2.
			 */
			@Override
			public void performPostInsertOps()
			{
				super.performPostInsertOps();
				try {
					getRowSet().execute();
				} catch (final SQLException se) {
					logger.log(Level.ERROR, "SQL Exception.", se);
				}
			}

			/**
			 * Obtain and set the PK value for the new record & perform any other
			 * actions needed before an insert.
			 */
			@Override
			public void performPreInsertOps()
			{
				// SSDBNavImpl will clear the component values
				super.performPreInsertOps();

				try (final ResultSet rs = connection.createStatement(
						ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
						.executeQuery("SELECT nextval('supplier_data_seq') as nextVal;");
						) {
					// Get the new record id.
					rs.next();
					final int supplierID = rs.getInt("nextVal");
					txtSupplierID.setText(String.valueOf(supplierID));
					
					// // SET OTHER DEFAULTS
					//  txtSupplierName.setText(null);
					//  txtSupplierCity.setText(null);
					//  txtSupplierStatus.setText("0");

				} catch(final SQLException se) {
					logger.log(Level.ERROR, "SQL Exception occured initializing new record.", se);
				} catch(final Exception e) {
					logger.log(Level.ERROR, "Exception occured initializing new record.", e);
				}
			}
		};
		
		// Initialize database connection and components.
		try {
			if (Boolean.FALSE) {
				rsButtons.tableLoopIncr(); // at beginning of cycling
				RowSet tRowSet = rsButtons.getTableLoopRowSet();
				if (tRowSet != null) {
					rowsModel = RowsModel.create(tRowSet, dbNav);
				}
			}
			if (Boolean.FALSE) {
				// start out null
				rowsModel = RowsModel.create(null, dbNav);
			}
			if (rowsModel == null) {
				RowSet rs = DemoUtil.getNewRowSet(connection);
				rs.setCommand("SELECT * FROM supplier_data");
				rs.execute();
				rowsModel = RowsModel.create(rs, dbNav);
			}

			navigator = new SSDataNavigator(rowsModel);
		} catch (SQLException | ClassNotFoundException se) {
			logger.log(Level.ERROR, "SQL Exception.", se);
		}

		// Bind the components to the RowsModel and the database columns.
		rowsModel.bind(txtSupplierID, "supplier_id");
		rowsModel.bind(txtSupplierName, "supplier_name");
		rowsModel.bind(txtSupplierCity, "city");
		rowsModel.bind(txtSupplierStatus, "status");
		
		// Set label dimensions.
		lblSupplierID.setPreferredSize(MainClass.labelDim);
		lblSupplierName.setPreferredSize(MainClass.labelDim);
		lblSupplierCity.setPreferredSize(MainClass.labelDim);
		lblSupplierStatus.setPreferredSize(MainClass.labelDim);
		
		// Set bound component dimensions.
		txtSupplierID.setPreferredSize(MainClass.ssDim);
		txtSupplierName.setPreferredSize(MainClass.ssDim);
		txtSupplierCity.setPreferredSize(MainClass.ssDim);
		txtSupplierStatus.setPreferredSize(MainClass.ssDim);
		
		// Setup the container and layout the components.
		final Container contentPane = new JPanel();
		contentPane.setLayout(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = .40;
		constraints.anchor = GridBagConstraints.WEST;
		contentPane.add(lblSupplierID, constraints);
		constraints.gridy = 1;
		contentPane.add(lblSupplierName, constraints);
		constraints.gridy = 2;
		contentPane.add(lblSupplierCity, constraints);
		constraints.gridy = 3;
		contentPane.add(lblSupplierStatus, constraints);
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = .60;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
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
		
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 2;
		contentPane.add(rsButtons, constraints);
		
		// Disable the primary key.
		txtSupplierID.setEnabled(false);
		
		// Set up the simple validation panel.
		JPanel uiPanel;
		if (USE_SIMPLE_VALIDATION) {
			ValidationPanel valiPanel = new ValidationPanel();
			valiPanel.setInnerComponent(contentPane);
			ValidationGroup group = valiPanel.getValidationGroup();
			group.addItem(decoSupplierName, false);
			uiPanel =  valiPanel;
		} else {
			uiPanel = (JPanel) contentPane;
		}
		// Make the jframe visible.
		frame.add(uiPanel);
		frame.pack();
		frame.setVisible(true);
	}
	
}
