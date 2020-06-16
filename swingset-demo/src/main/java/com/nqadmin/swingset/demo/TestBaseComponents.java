
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

import com.nqadmin.swingset.SSCheckBox;
import com.nqadmin.swingset.SSComboBox;
import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSImage;
import com.nqadmin.swingset.SSLabel;
import com.nqadmin.swingset.SSList;
import com.nqadmin.swingset.SSSlider;
import com.nqadmin.swingset.SSTextArea;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSJdbcRowSetImpl;
import com.nqadmin.swingset.utils.SSSyncManager;

/**
 * This example demonstrates all of the Base SwingSet Components
 * except for the SSDataGrid.
 * 
 * Separate classes exist for the SSDataGrid and for the SwingSet
 * components based on SSFormattedTextField.
 */

public class TestBaseComponents extends JFrame {
//public class TestGridComponents extends JFrame {
//public class TestFormattedComponents extends JFrame {


	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 7155378273131680653L;

	/**
	 * screen label declarations
	 */
	JLabel lblSwingSetBaseTestPK = new JLabel("SwingSet Base Test PK");
	
	JLabel lblSSDBComboNav = new JLabel("SSDBComboNav"); // SSDBComboBox used just for navigation

	JLabel lblSSCheckBox = new JLabel("SSCheckBox");
	JLabel lblSSComboBox = new JLabel("SSComboBox");
	JLabel lblSSDBComboBox = new JLabel("SSDBComboBox");
	JLabel lblSSImage = new JLabel("SSImage");
	JLabel lblSSLabel = new JLabel("SSLabel");	
	JLabel lblSSList = new JLabel("SSList");
	JLabel lblSSSlider = new JLabel("SSSlider");
	JLabel lblSSTextArea = new JLabel("SSTextArea");
	JLabel lblSSTextField = new JLabel("SSTextField");


	/**
	 * bound component declarations
	 */
	SSTextField txtSwingSetBaseTestPK = new SSTextField();
	
	SSDBComboBox cmbSSDBComboNav = new SSDBComboBox(); // SSDBComboBox used just for navigation

	SSCheckBox chkSSCheckBox = new SSCheckBox();
	SSComboBox cmbSSComboBox = new SSComboBox();
	SSDBComboBox cmbSSDBComboBox = new SSDBComboBox();
	SSImage imgSSImage = new SSImage();
	SSLabel lblSSLabel2 = new SSLabel();	
	SSList lisSSList = new SSList();
	SSSlider sliSSSlider = new SSSlider();
	SSTextArea txtSSTextArea = new SSTextArea();
	SSTextField txtSSTextField = new SSTextField();

	/**
	 * database component declarations
	 */
	SSConnection ssConnection = null;
	SSJdbcRowSetImpl rowset = null;
	SSDataNavigator navigator = null;
	
	/**
	 * sync manger
	 */
	SSSyncManager syncManager;

	/**
	 * Constructor for Example4
	 * 
	 * @param url - path to SQL to create suppliers & parts database
	 */
	public TestBaseComponents(Connection _dbConn) {
		
		// SET SCREEN TITLE
			super("SwingSet Base Component Test");
			
		// SET CONNECTION
			ssConnection = new SSConnection(_dbConn);
		
		// SET SCREEN DIMENSIONS
			setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);

		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
			try {
				this.rowset = new SSJdbcRowSetImpl(ssConnection.getConnection());
				this.rowset.setCommand("SELECT * FROM swingset_base_test_data;");
				this.navigator = new SSDataNavigator(this.rowset);
			} catch (SQLException se) {
				se.printStackTrace();
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
				private static final long serialVersionUID = 4264119495814589191L;

				/**
				 * Obtain and set the PK value for the new record & perform any other actions needed before an insert.
				 */
				@Override
				public void performPreInsertOps() {
					
					super.performPreInsertOps();
					
					try {

					// GET THE NEW RECORD ID.	
						ResultSet rs = ssConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
								.executeQuery("SELECT nextval('swingset_base_test_seq') as nextVal;");
						rs.next();
						int recordPK = rs.getInt("nextVal");
						lblSwingSetBaseTestPK.setText(String.valueOf(recordPK));
						rs.close();

// TODO See if pre-insert reset of components is needed.					
					// SET OTHER DEFAULTS
//						TestBaseComponents.this.txtBaseName.setText(null);
//						TestBaseComponents.this.cmbBaseColor.setSelectedValue(0);
//						TestBaseComponents.this.txtBaseWeight.setText("0");
//						TestBaseComponents.this.txtBaseCity.setText(null);
						
					} catch(SQLException se) {
						se.printStackTrace();
						System.out.println("Error occured during pre insert operation.\n" + se.getMessage());								
					} catch(Exception e) {
						e.printStackTrace();
						System.out.println("Error occured during pre insert operation.\n" + e.getMessage());
					}		
					
				}

				/**
				 * Requery the rowset following an insertion. This is needed for H2.
				 */
				@SuppressWarnings("restriction")
				@Override
				public void performPostInsertOps() {
					super.performPostInsertOps();
					TestBaseComponents.this.cmbSSDBComboNav.setEnabled(true);
					try {
						TestBaseComponents.this.rowset.execute();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					performRefreshOps();
				}
				
				/**
				 * Requery the rowset following a deletion. This is needed for H2.
				 */
				@SuppressWarnings("restriction")
				@Override
				public void performPostDeletionOps() {
					super.performPostDeletionOps();
					try {
						TestBaseComponents.this.rowset.execute();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					performRefreshOps();
				}
				
				/**
				 * Manage sync manager during a Refresh
				 */
				@Override
				public void performRefreshOps() {
					super.performRefreshOps();
					TestBaseComponents.this.syncManager.async();
					try {
						TestBaseComponents.this.cmbSSDBComboNav.execute();
					} catch (SQLException se) {
						se.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					TestBaseComponents.this.syncManager.sync();
				}

				/**
				 * Re-enable DB Navigator following insertion Cancel
				 */
				@Override
				public void performCancelOps() {
					super.performCancelOps();
					TestBaseComponents.this.cmbSSDBComboNav.setEnabled(true);
				}

			});
			
			// SETUP NAVIGATOR QUERY
				String query = "SELECT * FROM part_data;";
				this.cmbSSDBComboNav = new SSDBComboBox(ssConnection, query, "part_id", "part_name");
	
				try {
					this.cmbSSDBComboNav.execute();
				} catch (SQLException se) {
					se.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			// SETUP THE COMBO BOX OPTIONS TO BE DISPLAYED AND THEIR CORRESPONDING VALUES
//				this.cmbBaseColor.setOptions(new String[] { "Red", "Green", "Blue" });
				
				
//				
//			// BIND THE COMPONENTS TO THE DATABASE COLUMNS
//				this.lblSwingSetBaseTestPK.bind(this.rowset, "part_id");
//				this.txtBaseName.bind(this.rowset, "part_name");
//				this.cmbBaseColor.bind(this.rowset, "color_code");
//				this.txtBaseWeight.bind(this.rowset, "weight");
//				this.txtBaseCity.bind(this.rowset, "city");
				
			// SETUP SYNCMANAGER, WHICH WILL TAKE CARE OF KEEPING THE COMBO NAVIGATOR AND
			// DATA NAVIGATOR IN SYNC.
			//
			// BEFORE CHANGING THE QUERY OR RE-EXECUTING THE QUERY FOR THE COMBO BOX,
			// YOU HAVE TO CALL THE .async() METHOD
			// 
			// AFTER CALLING .execute() ON THE COMBO NAVIGATOR, CALL THE .sync() METHOD
				this.syncManager = new SSSyncManager(this.cmbSSDBComboNav, this.navigator);
				this.syncManager.setColumnName("part_id");
				this.syncManager.sync();

			// SET LABEL DIMENSIONS
				this.lblSwingSetBaseTestPK.setPreferredSize(MainClass.labelDim);
				
				this.lblSSDBComboNav.setPreferredSize(MainClass.labelDim);
				
				this.lblSSCheckBox.setPreferredSize(MainClass.labelDim);
				this.lblSSComboBox.setPreferredSize(MainClass.labelDim);
				this.lblSSDBComboBox.setPreferredSize(MainClass.labelDim);
				this.lblSSImage.setPreferredSize(MainClass.labelDim);
				this.lblSSLabel.setPreferredSize(MainClass.labelDim);
				this.lblSSList.setPreferredSize(MainClass.labelDim);
				this.lblSSSlider.setPreferredSize(MainClass.labelDim);
				this.lblSSTextArea.setPreferredSize(MainClass.labelDim);
				this.lblSSTextField.setPreferredSize(MainClass.labelDim);
				
			// SET BOUND COMPONENT DIMENSIONS
//				this.cmbSSDBComboNav.setPreferredSize(MainClass.ssDim);
//				this.lblSwingSetBaseTestPK.setPreferredSize(MainClass.ssDim);
//				this.txtBaseName.setPreferredSize(MainClass.ssDim);
//				this.cmbBaseColor.setPreferredSize(MainClass.ssDim);
//				this.txtBaseWeight.setPreferredSize(MainClass.ssDim);
//				this.txtBaseCity.setPreferredSize(MainClass.ssDim);
				
			// SETUP THE CONTAINER AND LAYOUT THE COMPONENTS
				Container contentPane = getContentPane();
				contentPane.setLayout(new GridBagLayout());
				GridBagConstraints constraints = new GridBagConstraints();

				constraints.gridx = 0;
				constraints.gridy = 0;
//				contentPane.add(this.lblSelectBase, constraints);
//				constraints.gridy = 1;
//				contentPane.add(this.lblBaseID, constraints);
//				constraints.gridy = 2;
//				contentPane.add(this.lblBaseName, constraints);
//				constraints.gridy = 3;
//				contentPane.add(this.lblBaseColor, constraints);
//				constraints.gridy = 4;
//				contentPane.add(this.lblBaseWeight, constraints);
//				constraints.gridy = 5;
//				contentPane.add(this.lblBaseCity, constraints);
		
				constraints.gridx = 1;
				constraints.gridy = 0;
				contentPane.add(this.cmbSSDBComboNav, constraints);
//				constraints.gridy = 1;
//				contentPane.add(this.lblSwingSetBaseTestPK, constraints);
//				constraints.gridy = 2;
//				contentPane.add(this.txtBaseName, constraints);
//				constraints.gridy = 3;
//				contentPane.add(this.cmbBaseColor, constraints);
//				constraints.gridy = 4;
//				contentPane.add(this.txtBaseWeight, constraints);
//				constraints.gridy = 5;
//				contentPane.add(this.txtBaseCity, constraints);
		
				constraints.gridx = 0;
				constraints.gridy = 6;
				constraints.gridwidth = 2;
				contentPane.add(this.navigator, constraints);

		// DISABLE THE PRIMARY KEY
			lblSwingSetBaseTestPK.setEnabled(false);
	
		// MAKE THE JFRAME VISIBLE
			setVisible(true);	}

}