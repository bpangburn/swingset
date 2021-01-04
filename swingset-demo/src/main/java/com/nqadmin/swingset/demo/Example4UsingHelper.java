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
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.plaf.InternalFrameUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSComboBox;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.utils.SSEnums.Navigation;
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
	private static final String cmbNavDisplayColumn = "part_name";
	
	// Screen Labels
	JLabel lblSelectPart = new JLabel("Parts");
	JLabel lblPartID = new JLabel("Part ID");
	JLabel lblPartName = new JLabel("Part Name");
	JLabel lblPartColor = new JLabel("Color");
	JLabel lblPartWeight = new JLabel("Weight");
	JLabel lblPartCity = new JLabel("City");
	
	// SwingSet Components
	//SSTextField txtPartID = new SSTextField();
	SSTextField txtPartName = new SSTextField();
	SSComboBox cmbPartColor = new SSComboBox();
	SSTextField txtPartWeight = new SSTextField();
	SSTextField txtPartCity = new SSTextField();

	/**
	 * Constructor for Example4
	 * <p>
	 * @param _dbConn - database connection
	 * @param _container parent container
	 */
	public Example4UsingHelper(final Connection _dbConn, final Container _container) {

		// Instantiate Screen
		super("Example 4 Using Helper", _dbConn, pkColumn, null, screenQuery, cmbNavDisplayColumn, null, null, cmbNavQuery);
		
		// For H2, the rowset has to be re-queried following a record insertion or deletion
		// TODO: Investigate how MySQL handles insert/delete and implement addNewRecordToCmbNavigator if needed.
		setRequeryAfterInsertOrDelete(true);
		
		// Finish Initialization
		initScreen();
		
		// Hide the frame since we're putting the JInternalFrame in its own JFrame for the demo
		setBorder(null);
		
		// Display Screen
		showUp(_container);
	}

	@Override
	protected void activateDeactivateComponents() throws Exception {
		// nothing to do...
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
		getTxtPrimaryKey().setPreferredSize(MainClass.ssDim);
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
		contentPane.add(getTxtPrimaryKey(), constraints);
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
	protected void addCustomListeners() throws Exception {
		// nothing to do...
	}
	
	@Override
	protected void addNewRecordToCmbNavigator() {
		// nothing to do because H2 requires that the rowset be 
		// requeried following a record insertion or deletion
		
	}

	@Override
	protected void bindComponents() throws Exception {
		// SET COMBO OPTIONS
		//cmbPartColor.setOptions(new String[] { "Red", "Green", "Blue" });
		cmbPartColor.setOptions(Arrays.asList(new String[] { "Red", "Green", "Blue" }));
		
		//txtPartID.bind(getRowset(), "part_id");
		txtPartName.bind(getRowset(), "part_name");
		cmbPartColor.bind(getRowset(), "color_code");
		txtPartWeight.bind(getRowset(), "weight");
		txtPartCity.bind(getRowset(), "city");
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

	/**
	 * Obtain and set the PK value for the new record.
	 */
	@Override
	protected void retrieveAndSetNewPrimaryKey() {

		try {

		// GET THE NEW RECORD ID.
			final ResultSet rs = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
					.executeQuery("SELECT nextval('part_data_seq') as nextVal;");
			rs.next();
			final int partID = rs.getInt("nextVal");
			//txtPartID.setText(String.valueOf(partID));
			getTxtPrimaryKey().setText(String.valueOf(partID));
			rs.close();
			
		} catch(final SQLException se) {
			logger.error("SQL Exception occured initializing new record.",se);
		} catch(final Exception e) {
			logger.error("Exception occured initializing new record.",e);
		}

	}

	@Override
	protected void setDefaultValues() throws Exception {
		//txtPartName.setText(null);
		//cmbPartColor.setSelectedValue(0);
		//txtPartWeight.setText("0");
		//txtPartCity.setText(null);
	}

	@Override
	protected void ssDBNavPerformCancelOps() {
		// nothing to do...

	}

	@Override
	protected void ssDBNavPerformNavigationOps(Navigation _navigationType) {
		// nothing to do...

	}

	@Override
	protected void ssDBNavPerformPostDeletionOps() {
		// nothing to do...
		
	}

	@Override
	protected void ssDBNavPerformPostInsertOps() {
		// nothing to do...
		
	}

	@Override
	protected void ssDBNavPerformPreDeletionOps() {
		// nothing to do...

	}

	@Override
	protected void ssDBNavPerformPreInsertOps() {
		// nothing to do...

	}

	@Override
	protected void ssDBNavPerformRefreshOps() {
		// nothing to do...

	}
	
	// THIS IS A HACK TO HIDE THE TITLE BAR SINCE WE'RE PUTTING THE JINTERNALFRAME IN ITS OWN JFRAME FOR THE SWINGSET DEMO
	// https://stackoverflow.com/a/51254020
    @Override
    public void setUI(InternalFrameUI _ui) {
        super.setUI(_ui); // this gets called internally when updating the ui and makes the northPane reappear
        BasicInternalFrameUI frameUI = (BasicInternalFrameUI) getUI(); // so...
        if (frameUI != null) frameUI.setNorthPane(null); // lets get rid of it
    }

	@Override
	protected void updateSSDBComboBoxes() {
		// nothing to do...

	}

}
