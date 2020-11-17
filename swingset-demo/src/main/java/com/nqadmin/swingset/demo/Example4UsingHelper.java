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

import javax.swing.JLabel;
import javax.swing.JMenuBar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSComboBox;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.utils.SSFormViewScreenHelper;

/**
 * This example displays data from the part_data table.
 * SSTextFields are used to display part id, name, weight,
 * and city. SSComboBox is used to display color.
 * <p>
 * This example is identical to Example4, but utilizes
 * com.nqadmin.swingset.utils.SSFormViewScreenHelper to minimize
 * coding.
 */
public class Example4UsingHelper extends SSFormViewScreenHelper {
	
	// Unique serial ID
	private static final long serialVersionUID = -5528806265008339747L;

	// Log4j2 Logger
    private static final Logger logger = LogManager.getLogger(Example4UsingHelper.class);
    
    // String Constants
	private static final String screenQuery = "SELECT * FROM part_data ORDER BY part_id;";
	private static final String cmbNavQuery = "SELECT part_id, part_name FROM part_data ORDER BY part_id;";
	private static final String pkColumn = "part_id";
	private static final String cmbNavDisplayColumn = "part_id";
	
	// Screen Labels
	JLabel lblSelectPart = new JLabel("Parts");
	JLabel lblPartID = new JLabel("Part ID");
	JLabel lblPartName = new JLabel("Part Name");
	JLabel lblPartColor = new JLabel("Color");
	JLabel lblPartWeight = new JLabel("Weight");
	JLabel lblPartCity = new JLabel("City");
	
	// SwingSet Components
	SSTextField txtPartID = new SSTextField();
	SSTextField txtPartName = new SSTextField();
	SSComboBox cmbPartColor = new SSComboBox();
	SSTextField txtPartWeight = new SSTextField();
	SSTextField txtPartCity = new SSTextField();

	/**
	 * Constructor for Example4
	 * <p>
	 * @param _dbConn - database connection
	 */
	public Example4UsingHelper(final Connection _dbConn, final Container _container) {

		// Instantiate Screen
		super("Example 4 Using Helper", new SSConnection(_dbConn), pkColumn, null, screenQuery, cmbNavDisplayColumn, null, null, cmbNavQuery);
		
		// Finish Initialization
		initScreen();
		
		// Display Screen
		showUp(_container);

	}



	@Override
	protected void activateDeactivateComponents() throws Exception {
		// DISABLE THE PRIMARY KEY
		txtPartID.setEnabled(false);
	}

	@Override
	protected void addComponents() throws Exception {
		// SET LABEL DIMENSIONS
		lblSelectPart.setPreferredSize(MainClass.labelDim);
		lblPartID.setPreferredSize(MainClass.labelDim);
		lblPartName.setPreferredSize(MainClass.labelDim);
		lblPartColor.setPreferredSize(MainClass.labelDim);
		lblPartWeight.setPreferredSize(MainClass.labelDim);
		lblPartCity.setPreferredSize(MainClass.labelDim);

	// SET BOUND COMPONENT DIMENSIONS
		getCmbNavigator().setPreferredSize(MainClass.ssDim);
		txtPartID.setPreferredSize(MainClass.ssDim);
		txtPartName.setPreferredSize(MainClass.ssDim);
		cmbPartColor.setPreferredSize(MainClass.ssDim);
		txtPartWeight.setPreferredSize(MainClass.ssDim);
		txtPartCity.setPreferredSize(MainClass.ssDim);

	// SETUP THE CONTAINER AND LAYOUT THE COMPONENTS
		final Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();

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
		contentPane.add(getCmbNavigator(), constraints);
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
		contentPane.add(getDataNavigator(), constraints);
	}

	@Override
	protected void bindComponents() throws Exception {
		txtPartID.bind(getRowset(), "part_id");
		txtPartName.bind(getRowset(), "part_name");
		cmbPartColor.bind(getRowset(), "color_code");
		txtPartWeight.bind(getRowset(), "weight");
		txtPartCity.bind(getRowset(), "city");
	}

	@Override
	protected void ssDBNavPerformCancelOps() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void ssDBNavPerformNavigationOps(int _navigationType) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void ssDBNavPerformPostDeletionOps() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void ssDBNavPerformPostInsertOps() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void ssDBNavPerformPreDeletionOps() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void ssDBNavPerformPreInsertOps() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void ssDBNavPerformRefreshOps() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateSSDBComboBoxes() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addCustomListeners() throws Exception {
		// nothing to do...
	}

	@Override
	public void closeChildScreens() {
		// nothing to do...

	}

	@Override
	protected void configureToolBars() {
		// nothing to do...

	}

	@Override
	public JMenuBar getJMenuBar() {
		// nothing to do...
		return null;
	}

	@Override
	protected void setDefaultValues() throws Exception {
		txtPartName.setText(null);
		cmbPartColor.setSelectedValue(0);
		txtPartWeight.setText("0");
		txtPartCity.setText(null);
	}

}
