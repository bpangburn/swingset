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

import com.nqadmin.swingSet.*;
import javax.swing.*;

import java.sql.*;
import java.awt.*;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;
import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.utils.SSSyncManager;

 /**
  * This example demonstrates the use of SSDBComboBox for record navigation.
  * Navigation can be accomplished using either the part combobox or the
  * navigation bar. Since the part name is used for navigation it can't be
  * updated (note that none of the fields in these examples can actually be
  * updated since the database is read only).
  *
  * Since the navigation can take place by multiple methods, the navigation
  * controls have to be synchronized.  This is done using a hidden SSTextField
  * containing the part id and a SSSyncManager.
  *
  * This example also demonstrates the use of other components to display
  * information in SSComboBox (Color) and SSTextField (Weight and City).
  */

 public class Example4 extends JFrame{

	private static final long serialVersionUID = -6594890166578252237L;
	JLabel lblPartName      = new JLabel("Part Name");
    JLabel lblSelectPart    = new JLabel("Parts");
    JLabel lblPartColor     = new JLabel("Color");
    JLabel lblPartWeight    = new JLabel("Weight");
    JLabel lblPartCity      = new JLabel("City");

    SSTextField txtPartName      = new SSTextField();
    SSDBComboBox cmbSelectPart   = null;
    SSComboBox cmbPartColor      = null;
    SSTextField txtPartWeight    = new SSTextField();
    SSTextField txtPartCity      = new SSTextField();

    SSConnection ssConnection = null;
    SSJdbcRowSetImpl rowset   = null;
    SSDataNavigator navigator = null;
    SSSyncManager syncManager;

    SSTextField txtPartID = new SSTextField();

    /**
     * Constructor for Example4
     * 
     * @param url - path to SQL to create suppliers & parts database
     */
    public Example4(String url){

        super("Example4");
        setSize(600,200);
        
        try{
        	System.out.println("url from ex 4: "+url);
        	this.ssConnection = new SSConnection("jdbc:h2:mem:suppliers_and_parts;INIT=runscript from '"+url+"'", "sa", "");
        	this.ssConnection.setDriverName("org.h2.Driver");
            this.ssConnection.createConnection();
            this.rowset = new SSJdbcRowSetImpl(this.ssConnection);
            this.rowset.setCommand("SELECT * FROM part_data;");
            this.navigator = new SSDataNavigator(this.rowset);
        }catch(SQLException se){
            se.printStackTrace();
        }catch(ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }

        this.txtPartID.bind(this.rowset,"part_id");
        this.txtPartName.bind(this.rowset,"part_name");
        
        String query = "SELECT * FROM part_data;";
        this.cmbSelectPart = new SSDBComboBox(this.ssConnection, query, "part_id", "part_name");

        try{
            this.cmbSelectPart.execute();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
      //  cmbSelectPart.updateItem(1, "new nut");
       // SYNC MANAGER WILL TAKE CARE OF KEEPING THE COMBO BOX AND DATANAVIGATOR IN SYNC.
       // WHILE CHANGEING THE QUERY OR REEXECUTING THE QUERY FOR COMBO BOX
       // YOU HAVE TO CALL A SYNC ON THE SYNC MANAGER AND AFTER CALLING EXECUTE ON COMBO BOX
       // CALL SYNC ON SYNC MANAGER. 
       // THESE THREE LINE OF CODE IS USED AS A REPLACEMENT FOR THE TWO LISTENER CLASS WE HAD.
      
        this.syncManager = new SSSyncManager(this.cmbSelectPart, this.navigator);
        this.syncManager.setColumnName("part_id");
        this.syncManager.sync();
        
        // THE FOLLOWING CODE IS USED BECAUSE OF AN H2 LIMITATION. UPDATABLE ROWSET IS NOT
        // FULLY IMPLEMENTED AND AN EXECUTE COMMAND IS REQUIRED WHEN INSERTING A NEW
        // ROW AND KEEPING THE CURSOR AT THE NEWLY INSERTED ROW.
        // IF USING ANOTHER DATABASE, THE FOLLOWING IS NOT REQURIED:   
        this.navigator.setDBNav(new SSDBNavAdapter(){
 			/**
			 * unique serial id
			 */
			private static final long serialVersionUID = 9018468389405536891L;

			@Override
			public void performRefreshOps() {
				super.performRefreshOps();
				Example4.this.syncManager.async();
				try{
		            Example4.this.cmbSelectPart.execute();
		        }catch(SQLException se){
		            se.printStackTrace();
		        }catch(Exception e){
		            e.printStackTrace();
		        }
				Example4.this.syncManager.sync();
			}

			@Override
 			public void performCancelOps() {
 				super.performCancelOps();
 				Example4.this.cmbSelectPart.setEnabled(true);
 			}

 			@Override
 			public void performPreInsertOps() {
 				super.performPreInsertOps();
 				Example4.this.txtPartName.setText(null);
 				Example4.this.cmbSelectPart.setEnabled(false);
 				Example4.this.cmbPartColor.setSelectedItem(null);
 				Example4.this.txtPartWeight.setText(null);
 				Example4.this.txtPartCity.setText(null);
 			}

 			@Override
 			public void performPostInsertOps() {
 				super.performPostInsertOps();
 				Example4.this.cmbSelectPart.setEnabled(true);
 				try {
					Example4.this.rowset.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
 			}
 			
         });
        this.cmbPartColor = new SSComboBox();
        this.cmbPartColor.setOptions(new String[]{"Red","Green","Blue"});
        this.cmbPartColor.bind(this.rowset,"color_code");

        this.txtPartWeight.bind(this.rowset,"weight");
        this.txtPartCity.bind(this.rowset,"city");

        this.lblPartName.setPreferredSize(new Dimension(75,20));
        this.lblSelectPart.setPreferredSize(new Dimension(75,20));
        this.lblPartColor.setPreferredSize(new Dimension(75,20));
        this.lblPartWeight.setPreferredSize(new Dimension(75,20));
        this.lblPartCity.setPreferredSize(new Dimension(75,20));

        this.txtPartName.setPreferredSize(new Dimension(150,20));
        this.cmbSelectPart.setPreferredSize(new Dimension(150,20));
        this.cmbPartColor.setPreferredSize(new Dimension(150,20));
        this.txtPartWeight.setPreferredSize(new Dimension(150,20));
        this.txtPartCity.setPreferredSize(new Dimension(150,20));

        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        contentPane.add(this.lblSelectPart, constraints);
        constraints.gridy = 1;
        contentPane.add(this.lblPartName, constraints);
        constraints.gridy = 2;
        contentPane.add(this.lblPartColor, constraints);
        constraints.gridy = 3;
        contentPane.add(this.lblPartWeight, constraints);
        constraints.gridy = 4;
        contentPane.add(this.lblPartCity, constraints);

        constraints.gridx = 1;        
        constraints.gridy = 0;
        contentPane.add(this.cmbSelectPart, constraints);
        constraints.gridy = 1;
        contentPane.add(this.txtPartName, constraints);
        constraints.gridy = 2;
        contentPane.add(this.cmbPartColor, constraints);
        constraints.gridy = 3;
        contentPane.add(this.txtPartWeight, constraints);
        constraints.gridy = 4;
        contentPane.add(this.txtPartCity, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        contentPane.add(this.navigator,constraints);

        setVisible(true);
    }


 }

/*
 * $Log$
 * Revision 1.10  2012/06/07 15:54:38  beevo
 * Modified example for compatibilty with H2 database.
 *
 * Revision 1.9  2005/02/14 18:50:25  prasanth
 * Updated to remove calls to deprecated methods.
 *
 * Revision 1.8  2005/02/04 22:40:12  yoda2
 * Updated Copyright info.
 *
 * Revision 1.7  2004/11/11 15:04:38  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.6  2004/11/01 19:18:51  yoda2
 * Fixed 0.9.X compatibility issues.
 *
 * Revision 1.5  2004/10/25 22:01:16  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 */
