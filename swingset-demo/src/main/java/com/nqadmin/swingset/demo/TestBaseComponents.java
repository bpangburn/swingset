
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

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
	SSList lstSSList = new SSList();
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
	 * combo and list items
	 */
	private static final String[] comboItems = {"Combo Item 0","Combo Item 1", "Combo Item 2", "Combo Item 3"};
	private static final int[] comboCodes = {0,1,2,3};
	private static final String[] listItems = {"List Item 1","List Item 2", "List Item 3", "List Item 4", "List Item 5", "List Item 6", "List Item 7"};
	private static final Object[] listCodes = {1,2,3,4,5,6,7};
	
	
	/**
	 * Method to set default values following an insert
	 */
	public void setDefaultValues() {
		
		try {

		// GET THE NEW RECORD ID.	
			ResultSet rs = ssConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
					.executeQuery("SELECT nextval('swingset_base_test_seq') as nextVal;");
			rs.next();
			int recordPK = rs.getInt("nextVal");
			txtSwingSetBaseTestPK.setText(String.valueOf(recordPK));
			rs.close();

		// SET OTHER DEFAULTS
			chkSSCheckBox.setSelected(false);
			cmbSSComboBox.setSelectedIndex(-1);
			cmbSSDBComboBox.setSelectedIndex(-1);
			imgSSImage.clearImage();
			lblSSLabel2.setText(null);	
			lstSSList.clearSelection();
// TODO determine range for slider, 0 was not accepted			
			sliSSSlider.setValue(1);
			txtSSTextArea.setText(null);
			txtSSTextField.setText(null);
			
		} catch(SQLException se) {
			se.printStackTrace();
			System.out.println("Error occured during pre insert operation.\n" + se.getMessage());								
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error occured during pre insert operation.\n" + e.getMessage());
		}	
		

	}
	

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
			setSize(MainClass.childScreenWidth, MainClass.childScreenHeightTall);

		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
			try {
				rowset = new SSJdbcRowSetImpl(ssConnection.getConnection());
				rowset.setCommand("SELECT * FROM swingset_base_test_data;");
				navigator = new SSDataNavigator(rowset);
			} catch (SQLException se) {
				se.printStackTrace();
			}
			

			
			/**
			 * Various navigator overrides needed to support H2
			 * 
			 * H2 does not fully support updatable rowset so it must be
			 * re-queried following insert and delete with rowset.execute()
			 */
			navigator.setDBNav(new SSDBNavImpl(this) {
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
					
					setDefaultValues();
					
				}

				/**
				 * Requery the rowset following an insertion. This is needed for H2.
				 */
				@Override
				public void performPostInsertOps() {
					super.performPostInsertOps();
					//TestBaseComponents.this.cmbSSDBComboNav.setEnabled(true);
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
				String query = "SELECT * FROM swingset_base_test_data;";
				cmbSSDBComboNav = new SSDBComboBox(ssConnection, query, "swingset_base_test_pk", "swingset_base_test_pk");
	
				try {
					cmbSSDBComboNav.execute();
				} catch (SQLException se) {
					se.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			// SETUP SYNCMANAGER, WHICH WILL TAKE CARE OF KEEPING THE COMBO NAVIGATOR AND
			// DATA NAVIGATOR IN SYNC.
			//
			// BEFORE CHANGING THE QUERY OR RE-EXECUTING THE QUERY FOR THE COMBO BOX,
			// YOU HAVE TO CALL THE .async() METHOD
			// 
			// AFTER CALLING .execute() ON THE COMBO NAVIGATOR, CALL THE .sync() METHOD
				syncManager = new SSSyncManager(cmbSSDBComboNav, navigator);
				syncManager.setColumnName("swingset_base_test_pk");
				syncManager.sync();				
				
			// SETUP COMBO AND LIST OPTIONS
				// TODO if getAllowNull() is true then add blank item to SSComboBox
				cmbSSComboBox.setAllowNull(true);
				cmbSSComboBox.setOptions(comboItems, comboCodes);
				lstSSList.setOptions(listItems, listCodes);
				
				String dbComboQuery = "SELECT * FROM part_data;";
				cmbSSDBComboBox = new SSDBComboBox(this.ssConnection, dbComboQuery, "part_id", "part_name");
				cmbSSDBComboBox.setAllowNull(false);
				// TODO if getAllowNull() is false, user can still blank out the combo - we may want to prevent this
				
			// SET SLIDER RANGE
			// TODO Set slider range
				
			// SETUP BOUND COMPONENTS
				txtSwingSetBaseTestPK.bind(rowset, "swingset_base_test_pk");
				
				chkSSCheckBox.bind(rowset, "ss_check_box");
				chkSSCheckBox.bind(rowset, "ss_check_box");
				cmbSSComboBox.bind(rowset, "ss_combo_box");
				cmbSSDBComboBox.bind(rowset, "ss_db_combo_box");
				//cmbSSDBComboBox.setEditable(false);
				imgSSImage.bind(rowset, "ss_image");
				lblSSLabel2.bind(rowset, "ss_label");
				lstSSList.bind(rowset, "ss_list");
				sliSSSlider.bind(rowset, "ss_slider");
				txtSSTextArea.bind(rowset, "ss_text_area");
				txtSSTextField.bind(rowset, "ss_text_field");
				
			// RUN DB COMBO QUERIES
				try {
					this.cmbSSDBComboBox.execute();
				} catch (SQLException se) {
					se.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			// SET LABEL DIMENSIONS
				lblSSDBComboNav.setPreferredSize(MainClass.labelDim);
				
				lblSwingSetBaseTestPK.setPreferredSize(MainClass.labelDim);
				
				lblSSCheckBox.setPreferredSize(MainClass.labelDim);
				lblSSComboBox.setPreferredSize(MainClass.labelDim);
				lblSSDBComboBox.setPreferredSize(MainClass.labelDim);
				lblSSImage.setPreferredSize(MainClass.labelDimVeryTall);
				lblSSLabel.setPreferredSize(MainClass.labelDim);
				lblSSList.setPreferredSize(MainClass.labelDimTall);
				lblSSSlider.setPreferredSize(MainClass.labelDim);
				lblSSTextArea.setPreferredSize(MainClass.labelDimTall);
				lblSSTextField.setPreferredSize(MainClass.labelDim);
				
			// SET BOUND COMPONENT DIMENSIONS
				txtSwingSetBaseTestPK.setPreferredSize(MainClass.ssDim);
				
				chkSSCheckBox.setPreferredSize(MainClass.ssDim);
				cmbSSComboBox.setPreferredSize(MainClass.ssDim);
				cmbSSDBComboBox.setPreferredSize(MainClass.ssDim);
				imgSSImage.setPreferredSize(MainClass.ssDimVeryTall);
				lblSSLabel2.setPreferredSize(MainClass.ssDim);
				
				// NEED TO MAKE SURE LIST IS TALLER THAN THE SCROLLPANE TO SEE THE SCROLLBAR
				lstSSList.setPreferredSize(new Dimension(MainClass.ssDimTall.width-20, MainClass.ssDimVeryTall.height));
				JScrollPane lstScrollPane = new JScrollPane(lstSSList);
				lstScrollPane.setPreferredSize(MainClass.ssDimTall);
				
				sliSSSlider.setPreferredSize(MainClass.ssDim);
				txtSSTextArea.setPreferredSize(MainClass.ssDimTall);
				txtSSTextField.setPreferredSize(MainClass.ssDim);
				
			// SETUP THE CONTAINER AND LAYOUT THE COMPONENTS
				Container contentPane = getContentPane();
				contentPane.setLayout(new GridBagLayout());
				GridBagConstraints constraints = new GridBagConstraints();

				constraints.gridx = 0;
				constraints.gridy = 0;

				contentPane.add(lblSSDBComboNav, constraints);
				constraints.gridy++;
				contentPane.add(lblSwingSetBaseTestPK, constraints);
				constraints.gridy++;
				contentPane.add(lblSSCheckBox, constraints);
				constraints.gridy++;
				contentPane.add(lblSSComboBox, constraints);
				constraints.gridy++;
				contentPane.add(lblSSDBComboBox, constraints);
				constraints.gridy++;
				//constraints.gridheight=4;
				contentPane.add(lblSSImage, constraints);
				constraints.gridy++;
				//constraints.gridheight=1;
				contentPane.add(lblSSLabel, constraints);
				constraints.gridy++;
				//constraints.gridheight=4;
				contentPane.add(lblSSList, constraints);
				constraints.gridy++;
				//constraints.gridheight=1;
				contentPane.add(lblSSSlider, constraints);
				constraints.gridy++;
				//constraints.gridheight=4;
				contentPane.add(lblSSTextArea, constraints);
				constraints.gridy++;
				//constraints.gridheight=1;
				contentPane.add(lblSSTextField, constraints);

				constraints.gridx = 1;
				constraints.gridy = 0;
				//constraints.gridwidth = 2;

				contentPane.add(cmbSSDBComboNav, constraints);
				constraints.gridy++;
				contentPane.add(txtSwingSetBaseTestPK, constraints);
				constraints.gridy++;
				contentPane.add(chkSSCheckBox, constraints);
				constraints.gridy++;
				contentPane.add(cmbSSComboBox, constraints);
				constraints.gridy++;
				contentPane.add(cmbSSDBComboBox, constraints);
				constraints.gridy++;
				//constraints.gridheight=4;
				contentPane.add(imgSSImage, constraints);
				constraints.gridy++;
				//constraints.gridheight=1;
				contentPane.add(lblSSLabel2, constraints);
				constraints.gridy++;
				//constraints.gridheight=4;
				//contentPane.add(lstSSList, constraints);
				contentPane.add(lstScrollPane, constraints);
				constraints.gridy++;
				//constraints.gridheight=1;
				contentPane.add(sliSSSlider, constraints);
				constraints.gridy++;
				//constraints.gridheight=4;
				contentPane.add(txtSSTextArea, constraints);
				constraints.gridy++;
				//constraints.gridheight=1;
				contentPane.add(txtSSTextField, constraints);
				
				constraints.gridx = 0;
				constraints.gridy++;
				constraints.gridwidth = 2;
				contentPane.add(this.navigator, constraints);
				
		// DISABLE THE PRIMARY KEY
			txtSwingSetBaseTestPK.setEnabled(false);
	
		// MAKE THE JFRAME VISIBLE
			setVisible(true);
			lstScrollPane.setPreferredSize(MainClass.ssDimTall);
			
	}

}