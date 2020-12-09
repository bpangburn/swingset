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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

import javax.sql.RowSet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.rowset.JdbcRowSetImpl;
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
import com.nqadmin.swingset.models.SSCollectionModel;
import com.nqadmin.swingset.models.SSDbArrayModel;
import com.nqadmin.swingset.utils.SSSyncManager;

/**
 * This example demonstrates all of the Base SwingSet Components
 * except for the SSDataGrid.
 * <p>
 * There is a separate example screen to demonstrate the
 * Formatted SwingSet Components.
 * <p>
 * IMPORTANT: The SSDBComboBox and the SSRowSet queries should select the same
 * records and in the same order. Otherwise the SSSyncManager will spend a lot of
 * time looping through records to match.
 */

public class TestBaseComponents extends JFrame {

	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = LogManager.getLogger(TestBaseComponents.class);
	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 7155378273131680653L;
	
	/**
	 * map of 'hints' contianing info on which collection model to use
	 */
	private final Map<String,Object> hints;

	/**
	 * combo and list items
	 */
	enum ComboEnum {
		A("Combo Enum 0"),
		B("Combo Enum 1"),
		C("Combo Enum 2"),
		D("Combo Enum 3");
		private final String displayVal;
		private ComboEnum(String _displayVal) {
			displayVal = _displayVal;
		}

		@Override
		public String toString() {
			return displayVal;
		}
	}
	/** only used manually for testing */
	enum ListEnum {
		A("List Enum 1"),
		B("List Enum 2"),
		C("List Enum 3"),
		D("List Enum 4"),
		E("List Enum 5"),
		F("List Enum 6"),
		G("List Enum 7");
		private final String displayVal;
		private ListEnum(String _displayVal) {
			displayVal = _displayVal;
		}
		
		@Override
		public String toString() {
			return displayVal;
		}
	}
	private static final String[] comboItems = {"Combo Item 0","Combo Item 1", "Combo Item 2", "Combo Item 3"};
	//private static final int[] comboCodes = {0,1,2,3};
	private static final Integer[] comboCodesIntegers = new Integer[] {0,1,2,3};
	private static final Object[] listCodes = {1,2,3,4,5,6,7};
	private static final String[] listItems = {"List Item 1","List Item 2", "List Item 3", "List Item 4", "List Item 5", "List Item 6", "List Item 7"};
	
	/**
	 * screen label declarations
	 */
	JLabel lblSSDBComboNav = new JLabel("SSDBComboNav"); // SSDBComboBox used just for navigation
	JLabel lblSwingSetBaseTestPK = new JLabel("Record ID");
	JLabel lblSSCheckBox = new JLabel("SSCheckBox");
	JLabel lblSSComboBox = new JLabel("SSComboBox");
	JLabel lblEnumSSComboBox = new JLabel("enumSSComboBox");
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
	SSCheckBox chkSSCheckBox = new SSCheckBox();
	SSComboBox cmbSSComboBox = new SSComboBox();
	SSComboBox cmbEnumSSComboBox = new SSComboBox();
	SSDBComboBox cmbSSDBComboBox = new SSDBComboBox();
	SSImage imgSSImage = new SSImage();
	SSLabel lblSSLabel2 = new SSLabel();
	final SSList lstSSList;
	SSSlider sliSSSlider = new SSSlider();
	SSTextArea txtSSTextArea = new SSTextArea();
	SSTextField txtSSTextField = new SSTextField();

	/**
	 * database component declarations
	 */
	Connection connection = null;
	RowSet rowset = null;
	SSDataNavigator navigator = null;

	/**
	 * combo navigator and sync manger
	 */
	// TODO: this gets set again, why set it here? listeners?
	//SSDBComboBox cmbSSDBComboNav = new SSDBComboBox(); // SSDBComboBox used just for navigation
	final SSDBComboBox cmbSSDBComboNav; // SSDBComboBox used just for navigation
	SSSyncManager syncManager;

	/**
	 * Method to obtain proper data structure/model for SSList based on database used
	 */
	private SSCollectionModel getCollectionModel() {
		@SuppressWarnings("unchecked")
		Supplier<SSCollectionModel> supl
				= (Supplier<SSCollectionModel>) hints.get("collectionModel");
		return supl == null ? new SSDbArrayModel(JDBCType.INTEGER) : supl.get();
	}

	/**
	 * Constructor for Base Component Test
	 * <p>
	 * @param _dbConn - database connection
	 */
	public TestBaseComponents(final Connection _dbConn, Map<String,Object> _hints) {

		// SET SCREEN TITLE
			super("SwingSet Base Component Test");

		// INITIALIZE SOME DYNAMIC INFORMATION
			hints =  _hints;
			lstSSList = new SSList(getCollectionModel());

		// SET CONNECTION
			connection = _dbConn;

		// SET SCREEN DIMENSIONS
			setSize(MainClass.childScreenWidth, MainClass.childScreenHeightTall);
			
		// SET SCREEN POSITION
			setLocation(DemoUtil.getChildScreenLocation(this.getName()));

		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
			try {
				rowset = new JdbcRowSetImpl(connection);
				rowset.setCommand("SELECT * FROM swingset_base_test_data;");
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
					//TestBaseComponents.this.cmbSSDBComboNav.setEnabled(true);
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
				final String query = "SELECT * FROM swingset_base_test_data;";
				cmbSSDBComboNav = new SSDBComboBox(connection, query, "swingset_base_test_pk", "swingset_base_test_pk");
				cmbSSDBComboNav.setLogColumnName("combo_nav");

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
				syncManager.setColumnName("swingset_base_test_pk");
				syncManager.sync();

			// SETUP COMBO AND LIST OPTIONS
				// TODO if getAllowNull() is true then add blank item to SSComboBox
				cmbSSComboBox.setAllowNull(true);
				cmbSSComboBox.setOptions(Arrays.asList(comboItems), Arrays.asList(comboCodesIntegers));
				cmbEnumSSComboBox.setAllowNull(true);
				cmbEnumSSComboBox.setOptions(ComboEnum.class);

				// NOTE following enum has [0,N) mapping, but DB is [1,N]
				//      Fortunately test DB doesn't have a "7" in ss_list array
				//lstSSList.setOptions(ListEnum.class);
				lstSSList.setOptions(Arrays.asList(listItems), Arrays.asList(listCodes));

				final String dbComboQuery = "SELECT * FROM part_data;";
				cmbSSDBComboBox = new SSDBComboBox(connection, dbComboQuery, "part_id", "part_name");
				cmbSSDBComboBox.setAllowNull(false);
				// TODO if getAllowNull() is false, user can still blank out the combo - we may want to prevent this

			// SET SLIDER RANGE
			// TODO Set slider range

			// SETUP BOUND COMPONENTS
				txtSwingSetBaseTestPK.bind(rowset, "swingset_base_test_pk");

				chkSSCheckBox.bind(rowset, "ss_check_box");
				cmbSSComboBox.bind(rowset, "ss_combo_box");
				cmbEnumSSComboBox.bind(rowset, "ss_combo_box");
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
					cmbSSDBComboBox.execute();
				} catch (final SQLException se) {
					logger.error("SQL Exception.", se);
				} catch (final Exception e) {
					logger.error("Exception.", e);
				}

			// SET LABEL DIMENSIONS
				lblSSDBComboNav.setPreferredSize(MainClass.labelDim);

				lblSwingSetBaseTestPK.setPreferredSize(MainClass.labelDim);

				lblSSCheckBox.setPreferredSize(MainClass.labelDim);
				lblSSComboBox.setPreferredSize(MainClass.labelDim);
				lblEnumSSComboBox.setPreferredSize(MainClass.labelDim);
				lblSSDBComboBox.setPreferredSize(MainClass.labelDim);
				lblSSImage.setPreferredSize(MainClass.labelDimVeryTall);
				lblSSLabel.setPreferredSize(MainClass.labelDim);
				lblSSList.setPreferredSize(MainClass.labelDimTall);
				lblSSSlider.setPreferredSize(MainClass.labelDim);
				lblSSTextArea.setPreferredSize(MainClass.labelDimTall);
				lblSSTextField.setPreferredSize(MainClass.labelDim);

			// SET BOUND COMPONENT DIMENSIONS
				cmbSSDBComboNav.setPreferredSize(MainClass.ssDim);

				txtSwingSetBaseTestPK.setPreferredSize(MainClass.ssDim);

				chkSSCheckBox.setPreferredSize(MainClass.ssDim);
				cmbSSComboBox.setPreferredSize(MainClass.ssDim);
				cmbEnumSSComboBox.setPreferredSize(MainClass.ssDim);
				cmbSSDBComboBox.setPreferredSize(MainClass.ssDim);
				imgSSImage.setPreferredSize(MainClass.ssDimVeryTall);
				lblSSLabel2.setPreferredSize(MainClass.ssDim);

				// NEED TO MAKE SURE LIST IS TALLER THAN THE SCROLLPANE TO SEE THE SCROLLBAR
				lstSSList.setPreferredSize(new Dimension(MainClass.ssDimTall.width-20, MainClass.ssDimVeryTall.height));
				final JScrollPane lstScrollPane = new JScrollPane(lstSSList);
				lstScrollPane.setPreferredSize(MainClass.ssDimTall);

				sliSSSlider.setPreferredSize(MainClass.ssDim);
				txtSSTextArea.setPreferredSize(MainClass.ssDimTall);
				txtSSTextField.setPreferredSize(MainClass.ssDim);

			// SETUP THE CONTAINER AND LAYOUT THE COMPONENTS
				final Container contentPane = getContentPane();
				contentPane.setLayout(new GridBagLayout());
				final GridBagConstraints constraints = new GridBagConstraints();

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
				contentPane.add(lblEnumSSComboBox, constraints);
				constraints.gridy++;
				contentPane.add(lblSSDBComboBox, constraints);
				constraints.gridy++;
				contentPane.add(lblSSImage, constraints);
				constraints.gridy++;
				contentPane.add(lblSSLabel, constraints);
				constraints.gridy++;
				contentPane.add(lblSSList, constraints);
				constraints.gridy++;
				contentPane.add(lblSSSlider, constraints);
				constraints.gridy++;
				contentPane.add(lblSSTextArea, constraints);
				constraints.gridy++;
				contentPane.add(lblSSTextField, constraints);

				constraints.gridx = 1;
				constraints.gridy = 0;

				contentPane.add(cmbSSDBComboNav, constraints);
				constraints.gridy++;
				contentPane.add(txtSwingSetBaseTestPK, constraints);
				constraints.gridy++;
				contentPane.add(chkSSCheckBox, constraints);
				constraints.gridy++;
				contentPane.add(cmbSSComboBox, constraints);
				constraints.gridy++;
				contentPane.add(cmbEnumSSComboBox, constraints);
				constraints.gridy++;
				contentPane.add(cmbSSDBComboBox, constraints);
				constraints.gridy++;
				contentPane.add(imgSSImage, constraints);
				constraints.gridy++;
				contentPane.add(lblSSLabel2, constraints);
				constraints.gridy++;
				//contentPane.add(lstSSList, constraints);
				contentPane.add(lstScrollPane, constraints);
				constraints.gridy++;
				contentPane.add(sliSSSlider, constraints);
				constraints.gridy++;
				contentPane.add(txtSSTextArea, constraints);
				constraints.gridy++;
				contentPane.add(txtSSTextField, constraints);

				constraints.gridx = 0;
				constraints.gridy++;
				constraints.gridwidth = 2;
				contentPane.add(navigator, constraints);

		// DISABLE THE PRIMARY KEY
			txtSwingSetBaseTestPK.setEnabled(false);

		// MAKE THE JFRAME VISIBLE
			setVisible(true);
			lstScrollPane.setPreferredSize(MainClass.ssDimTall);
			pack();

	}

	/**
	 * Method to set default values following an insert
	 */
	public void setDefaultValues() {

		try {

		// GET THE NEW RECORD ID.
			final ResultSet rs = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
					.executeQuery("SELECT nextval('swingset_base_test_seq') as nextVal;");
			rs.next();
			final int recordPK = rs.getInt("nextVal");
			txtSwingSetBaseTestPK.setText(String.valueOf(recordPK));
			rs.close();

		// SET OTHER DEFAULTS
//			chkSSCheckBox.setSelected(false);
//			cmbSSComboBox.setSelectedIndex(-1);
//			cmbEnumSSComboBox.setSelectedIndex(-1);
//			cmbSSDBComboBox.setSelectedIndex(-1);
//			imgSSImage.clearImage();
//			lblSSLabel2.setText(null);
//			lstSSList.clearSelection();
// TODO determine range for slider, 0 was not accepted
//			sliSSSlider.setValue(1);
//			txtSSTextArea.setText(null);
//			txtSSTextField.setText(null);

		} catch(final SQLException se) {
			logger.error("SQL Exception occured during setting default values.",se);
		} catch(final Exception e) {
			logger.error("Exception occured during setting default values.",e);
		}


	}

}
