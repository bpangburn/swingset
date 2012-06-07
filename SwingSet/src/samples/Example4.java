/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2005, The Pangburn Company and Prasanth R. Pasala
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  The names of its contributors may not be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import com.nqadmin.swingSet.*;
import javax.swing.*;

import java.sql.*;
import java.awt.*;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;
import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.utils.SSSyncManager;

 /**
  * This example demonstrates the use of SSDBComboBox for record navigation.
  * Navigation can be accomplished using either the Part combobox or the
  * navigation bar. Since the part name is used for navigation it can't be
  * updated (note that none of the fields in these examples can actually be
  * updated since the database is read only).
  *
  * Since the navigation can take place by multiple methods, the navigation
  * controls have to be synchronized.  This is done using a hidden JTextField
  * containing the part_id and an event listener.
  *
  * This example also demonstrates the use of SSTextDocument to display
  * information in SSComboBox (Color) and JTextField (Weight and City).
  */

 public class Example4 extends JFrame{

	JLabel lblPartName    = new JLabel("Part Name");
    JLabel lblSelectPart    = new JLabel("Parts");
    JLabel lblPartColor     = new JLabel("Color");
    JLabel lblPartWeight    = new JLabel("Weight");
    JLabel lblPartCity      = new JLabel("City");

    SSTextField txtPartName      = new SSTextField();
    SSDBComboBox cmbSelectPart  = null;
    SSComboBox cmbPartColor     = null;
    SSTextField txtPartWeight    = new SSTextField();
    SSTextField txtPartCity      = new SSTextField();

    SSConnection ssConnection = null;
    SSJdbcRowSetImpl rowset   = null;
    SSDataNavigator navigator = null;
    SSSyncManager syncManager;

    SSTextField txtPartID = new SSTextField();

   public Example4(){

        super("Example4");
        setSize(600,200);

        try{
        	String url = "http://192.168.0.234/populate.sql";
        	ssConnection = new SSConnection("jdbc:h2:mem:suppliers_and_parts;INIT=runscript from '"+url+"'", "sa", "");
            ssConnection.setDriverName("org.h2.Driver");
            ssConnection.createConnection();
            ssConnection.createConnection();
            
            rowset = new SSJdbcRowSetImpl(ssConnection);
            rowset.setCommand("SELECT * FROM part_data");
            navigator = new SSDataNavigator(rowset);
        }catch(SQLException se){
            se.printStackTrace();
        }catch(ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }

        txtPartID.bind(rowset,"part_id");
        txtPartName.bind(rowset,"part_name");
        
        String query = "SELECT * FROM part_data;";
        cmbSelectPart = new SSDBComboBox(ssConnection, query, "part_id", "part_name");
        
        try{
            cmbSelectPart.execute();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

       // SYNC MANAGER WILL TAKE CARE OF KEEPING THE COMBO BOX AND DATANAVIGATOR IN SYNC.
       // WHILE CHANGEING THE QUERY OR REEXECUTING THE QUERY FOR COMBO BOX
       // YOU HAVE TO CALL A SYNC ON THE SYNC MANAGER AND AFTER CALLING EXECUTE ON COMBO BOX
       // CALL SYNC ON SYNC MANAGER. 
       // THESE THREE LINE OF CODE IS USED AS A REPLACEMENT FOR THE TWO LISTENER CLASS WE HAD.
        syncManager = new SSSyncManager(cmbSelectPart, navigator);
        syncManager.setColumnName("part_id");
        syncManager.sync();
        
        // THE FOLLOWING CODE IS USED BECAUSE OF AN H2 LIMITATION. UPDATABLE ROWSET IS NOT
        // FULLY IMPLEMENTED AND AN EXECUTE COMMAND IS REQUIRED WHEN INSERTING A NEW
        // ROW AND KEEPING THE CURSOR AT THE NEWLY INSERTED ROW.
        // IF USING ANOTHER DATABASE, THE FOLLOWING IS NOT REQURIED:   
        navigator.setDBNav(new SSDBNavAdapter(){
 			@Override
			public void performRefreshOps() {
				// TODO Auto-generated method stub
				super.performRefreshOps();
				syncManager.async();
				try{
		            cmbSelectPart.execute();
		        }catch(SQLException se){
		            se.printStackTrace();
		        }catch(Exception e){
		            e.printStackTrace();
		        }
				syncManager.sync();
			}

			@Override
 			public void performCancelOps() {
 				// TODO Auto-generated method stub
 				super.performCancelOps();
 				cmbSelectPart.setEnabled(true);
 			}

 			@Override
 			public void performPreInsertOps() {
 				// TODO Auto-generated method stub
 				super.performPreInsertOps();
 				txtPartName.setText(null);
 				cmbSelectPart.setEnabled(false);
 				cmbPartColor.setSelectedItem(null);
 				txtPartWeight.setText(null);
 				txtPartCity.setText(null);
 			}

 			@Override
 			public void performPostInsertOps() {
 				// TODO Auto-generated method stub
 				super.performPostInsertOps();
 				cmbSelectPart.setEnabled(true);
 				try {
					rowset.execute();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 			}
 			
         });

        cmbPartColor = new SSComboBox();
        cmbPartColor.setOptions(new String[]{"Red","Green","Blue"});
        cmbPartColor.bind(rowset,"color_code");

        txtPartWeight.bind(rowset,"weight");
        txtPartCity.bind(rowset,"city");

        lblPartName.setPreferredSize(new Dimension(75,20));
        lblSelectPart.setPreferredSize(new Dimension(75,20));
        lblPartColor.setPreferredSize(new Dimension(75,20));
        lblPartWeight.setPreferredSize(new Dimension(75,20));
        lblPartCity.setPreferredSize(new Dimension(75,20));

        txtPartName.setPreferredSize(new Dimension(150,20));
        cmbSelectPart.setPreferredSize(new Dimension(150,20));
        cmbPartColor.setPreferredSize(new Dimension(150,20));
        txtPartWeight.setPreferredSize(new Dimension(150,20));
        txtPartCity.setPreferredSize(new Dimension(150,20));

        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        contentPane.add(lblSelectPart, constraints);
        constraints.gridy = 1;
        contentPane.add(lblPartName, constraints);
        constraints.gridy = 2;
        contentPane.add(lblPartColor, constraints);
        constraints.gridy = 3;
        contentPane.add(lblPartWeight, constraints);
        constraints.gridy = 4;
        contentPane.add(lblPartCity, constraints);

        constraints.gridx = 1;        
        constraints.gridy = 0;
        contentPane.add(cmbSelectPart, constraints);
        constraints.gridy = 1;
        contentPane.add(txtPartName, constraints);
        constraints.gridy = 2;
        contentPane.add(cmbPartColor, constraints);
        constraints.gridy = 3;
        contentPane.add(txtPartWeight, constraints);
        constraints.gridy = 4;
        contentPane.add(txtPartCity, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        contentPane.add(navigator,constraints);

        setVisible(true);

    }

    public static void main(String[] args){
        new Example4();
    }

 }

/*
 * $Log$
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