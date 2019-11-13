
/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.nqadmin.swingset.SSComboBox;
import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSJdbcRowSetImpl;
import com.nqadmin.swingset.utils.SSSyncManager;

/**
 * This example demonstrates the use of SSDBComboBox for record navigation.
 * Navigation can be accomplished using either the part combobox or the
 * navigation bar. Since the part name is used for navigation it can't be
 * updated.
 *
 * Since the navigation can take place by multiple methods, the navigation
 * controls have to be synchronized. This is done using a hidden SSTextField
 * containing the part id and a SSSyncManager.
 *
 * This example also demonstrates the use of other components to display
 * information in SSComboBox (Color) and SSTextField (Weight and City).
 */

public class Example4 extends JFrame {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -6594890166578252237L;
	
	/**
	 * screen label declarations
	 */
	JLabel lblPartID = new JLabel("Part ID");
	JLabel lblPartName = new JLabel("Part Name");
	JLabel lblSelectPart = new JLabel("Parts");
	JLabel lblPartColor = new JLabel("Color");
	JLabel lblPartWeight = new JLabel("Weight");
	JLabel lblPartCity = new JLabel("City");

	/**
	 * bound component declarations
	 */
	SSTextField txtPartID = new SSTextField();
	SSTextField txtPartName = new SSTextField();
	SSDBComboBox cmbSelectPart = null;
	SSComboBox cmbPartColor = new SSComboBox();
	SSTextField txtPartWeight = new SSTextField();
	SSTextField txtPartCity = new SSTextField();

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
	public Example4(String url) {

		// SET SCREEN TITLE
			super("Example4");
			
		// SET SCREEN DIMENSIONS
			setSize(MainClass.childScreenWidth, MainClass.childScreenHeight);

		// INITIALIZE DATABASE CONNECTION AND COMPONENTS
			try {
				System.out.println("url from ex 4: " + url);
				this.ssConnection = new SSConnection("jdbc:h2:mem:suppliers_and_parts;INIT=runscript from '" + url + "'",
						"sa", "");
				this.ssConnection.setDriverName("org.h2.Driver");
				this.ssConnection.createConnection();
				this.rowset = new SSJdbcRowSetImpl(this.ssConnection.getConnection());
				this.rowset.setCommand("SELECT * FROM part_data;");
				this.navigator = new SSDataNavigator(this.rowset);
			} catch (SQLException se) {
				se.printStackTrace();
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
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
				private static final long serialVersionUID = 9018468389405536891L;

				/**
				 * Obtain and set the PK value for the new record & perform any other actions needed before an insert.
				 */
				@Override
				public void performPreInsertOps() {
					
					super.performPreInsertOps();
					
					try {

					// GET THE NEW RECORD ID.	
						ResultSet rs = ssConnection.getConnection()
								.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
								.executeQuery("SELECT nextval('part_data_seq') as nextVal;");
						rs.next();
						int partID = rs.getInt("nextVal");
						txtPartID.setText(String.valueOf(partID));
						rs.close();
						
					// DISABLE PART SELECTOR
						Example4.this.cmbSelectPart.setEnabled(false);
					
					// SET OTHER DEFAULTS
						Example4.this.txtPartName.setText(null);
						Example4.this.cmbPartColor.setSelectedValue(0);
						Example4.this.txtPartWeight.setText("0");
						Example4.this.txtPartCity.setText(null);
						
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
				@Override
				public void performPostInsertOps() {
					super.performPostInsertOps();
					Example4.this.cmbSelectPart.setEnabled(true);
					try {
						Example4.this.rowset.execute();
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
						Example4.this.rowset.execute();
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
					Example4.this.syncManager.async();
					try {
						Example4.this.cmbSelectPart.execute();
					} catch (SQLException se) {
						se.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					Example4.this.syncManager.sync();
				}

				/**
				 * Re-enable DB Navigator following insertion Cancel
				 */
				@Override
				public void performCancelOps() {
					super.performCancelOps();
					Example4.this.cmbSelectPart.setEnabled(true);
				}

			});
			
			// SETUP NAVIGATOR QUERY
				String query = "SELECT * FROM part_data;";
				this.cmbSelectPart = new SSDBComboBox(this.ssConnection, query, "part_id", "part_name");
	
				try {
					this.cmbSelectPart.execute();
				} catch (SQLException se) {
					se.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			// SETUP THE COMBO BOX OPTIONS TO BE DISPLAYED AND THEIR CORRESPONDING VALUES
				this.cmbPartColor.setOptions(new String[] { "Red", "Green", "Blue" });
				
			// BIND THE COMPONENTS TO THE DATABASE COLUMNS
				this.txtPartID.bind(this.rowset, "part_id");
				this.txtPartName.bind(this.rowset, "part_name");
				this.cmbPartColor.bind(this.rowset, "color_code");
				this.txtPartWeight.bind(this.rowset, "weight");
				this.txtPartCity.bind(this.rowset, "city");
				
			// SETUP SYNCMANAGER, WHICH WILL TAKE CARE OF KEEPING THE COMBO NAVIGATOR AND
			// DATA NAVIGATOR IN SYNC.
			//
			// BEFORE CHANGING THE QUERY OR RE-EXECUTING THE QUERY FOR THE COMBO BOX,
			// YOU HAVE TO CALL THE .async() METHOD
			// 
			// AFTER CALLING .execute() ON THE COMBO NAVIGATOR, CALL THE .sync() METHOD
				this.syncManager = new SSSyncManager(this.cmbSelectPart, this.navigator);
				this.syncManager.setColumnName("part_id");
				this.syncManager.sync();

			// SET LABEL DIMENSIONS
				this.lblSelectPart.setPreferredSize(MainClass.labelDim);
				this.lblPartID.setPreferredSize(MainClass.labelDim);
				this.lblPartName.setPreferredSize(MainClass.labelDim);
				this.lblPartColor.setPreferredSize(MainClass.labelDim);
				this.lblPartWeight.setPreferredSize(MainClass.labelDim);
				this.lblPartCity.setPreferredSize(MainClass.labelDim);
				
			// SET BOUND COMPONENT DIMENSIONS
				this.cmbSelectPart.setPreferredSize(MainClass.ssDim);
				this.txtPartID.setPreferredSize(MainClass.ssDim);
				this.txtPartName.setPreferredSize(MainClass.ssDim);
				this.cmbPartColor.setPreferredSize(MainClass.ssDim);
				this.txtPartWeight.setPreferredSize(MainClass.ssDim);
				this.txtPartCity.setPreferredSize(MainClass.ssDim);
				
			// SETUP THE CONTAINER AND LAYOUT THE COMPONENTS
				Container contentPane = getContentPane();
				contentPane.setLayout(new GridBagLayout());
				GridBagConstraints constraints = new GridBagConstraints();

				constraints.gridx = 0;
				constraints.gridy = 0;
				contentPane.add(this.lblSelectPart, constraints);
				constraints.gridy = 1;
				contentPane.add(this.lblPartID, constraints);
				constraints.gridy = 2;
				contentPane.add(this.lblPartName, constraints);
				constraints.gridy = 3;
				contentPane.add(this.lblPartColor, constraints);
				constraints.gridy = 4;
				contentPane.add(this.lblPartWeight, constraints);
				constraints.gridy = 5;
				contentPane.add(this.lblPartCity, constraints);
		
				constraints.gridx = 1;
				constraints.gridy = 0;
				contentPane.add(this.cmbSelectPart, constraints);
				constraints.gridy = 1;
				contentPane.add(this.txtPartID, constraints);
				constraints.gridy = 2;
				contentPane.add(this.txtPartName, constraints);
				constraints.gridy = 3;
				contentPane.add(this.cmbPartColor, constraints);
				constraints.gridy = 4;
				contentPane.add(this.txtPartWeight, constraints);
				constraints.gridy = 5;
				contentPane.add(this.txtPartCity, constraints);
		
				constraints.gridx = 0;
				constraints.gridy = 6;
				constraints.gridwidth = 2;
				contentPane.add(this.navigator, constraints);

		// DISABLE THE PRIMARY KEY
			txtPartID.setEnabled(false);
	
		// MAKE THE JFRAME VISIBLE
			setVisible(true);	}

}

/*
 * $Log$ Revision 1.10 2012/06/07 15:54:38 beevo Modified example for
 * compatibilty with H2 database.
 *
 * Revision 1.9 2005/02/14 18:50:25 prasanth Updated to remove calls to
 * deprecated methods.
 *
 * Revision 1.8 2005/02/04 22:40:12 yoda2 Updated Copyright info.
 *
 * Revision 1.7 2004/11/11 15:04:38 yoda2 Using TextPad, converted all tabs to
 * "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.6 2004/11/01 19:18:51 yoda2 Fixed 0.9.X compatibility issues.
 *
 * Revision 1.5 2004/10/25 22:01:16 yoda2 Updated JavaDoc for new datasource
 * abstraction layer in 0.9.0 release.
 *
 */
