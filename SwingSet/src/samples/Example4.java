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
import javax.swing.event.*;
import javax.swing.text.*;
import javax.sql.*;
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

    JLabel lblSelectPart    = new JLabel("Part");
    JLabel lblPartColor     = new JLabel("Color");
    JLabel lblPartWeight    = new JLabel("Weight");
    JLabel lblPartCity      = new JLabel("City");

    SSDBComboBox cmbSelectPart  = null;
    SSComboBox cmbPartColor     = null;
    JTextField txtPartWeight    = new JTextField();
    JTextField txtPartCity      = new JTextField();

    SSConnection ssConnection = null;
    SSJdbcRowSetImpl rowset   = null;
    SSDataNavigator navigator = null;
    SSSyncManager syncManager;

    JTextField txtPartID = new JTextField();

   public Example4(){

        super("Example4");
        setSize(600,200);

        try{
            ssConnection = new SSConnection("jdbc:postgresql://pgserver.greatmindsworking.com/suppliers_and_parts",
                "swingset", "test");
            ssConnection.setDriverName("org.postgresql.Driver");
            ssConnection.createConnection();
            rowset = new SSJdbcRowSetImpl(ssConnection);

            // POSTGRES RAISES AN EXCEPTION WHEN YOU TRY TO USE THE UPDATEROW() METHOD
            // IF THERE IS A SEMICOLON AT THE END OF THE QUERY WITH OUT ANY CLAUSES
            // OR WHERE CONDITIONS AT THE END.
            // IF YOU REMOVE THE SEMICOLON IT WILL NOT RAISE THE EXCEPTION BUT
            // NO UPDATES ARE MADE.
            rowset.setCommand("SELECT * FROM part_data");
            navigator = new SSDataNavigator(rowset);
            // THIS DISABLES MODIFICATIONS TO THE DATA
            // ADDITION AND DELETION BUTTONS ARE DISABLED
            // ANY CHANGES MADE TO PRESENT RECORD WILL BE NEGLECTED.
            navigator.setModification(false);
            navigator.setDBNav( new SSDBNavImp(getContentPane()));
        }catch(SQLException se){
            se.printStackTrace();
        }catch(ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }

        txtPartID.setDocument(new SSTextDocument(rowset,"part_id"));

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
       // YOU HAVE TO CALL ASYNC ON THE SYNC MANAGER AND AFTER CALLING EXECUTE ON COMBO BOX
       // CALL SYNC ON SYNC MANAGER. 
       // THESE THREE LINE OF CODE IS USED A REPLACEMENT FOR THE TWO LISTENER CLASS WE HAD.
        syncManager = new SSSyncManager(cmbSelectPart, navigator);
        syncManager.setColumnName("part_id");
        syncManager.sync();
        
        cmbPartColor = new SSComboBox();
        cmbPartColor.setOption(new String[]{"Red","Green","Blue"});
        cmbPartColor.bind(rowset,"color_code");

        txtPartWeight.setDocument(new SSTextDocument(rowset,"weight"));
        txtPartCity.setDocument(new SSTextDocument(rowset,"city"));

        lblSelectPart.setPreferredSize(new Dimension(75,20));
        lblPartColor.setPreferredSize(new Dimension(75,20));
        lblPartWeight.setPreferredSize(new Dimension(75,20));
        lblPartCity.setPreferredSize(new Dimension(75,20));

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
        contentPane.add(lblPartColor, constraints);
        constraints.gridy = 2;
        contentPane.add(lblPartWeight, constraints);
        constraints.gridy = 3;
        contentPane.add(lblPartCity, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        contentPane.add(cmbSelectPart, constraints);
        constraints.gridy = 1;
        contentPane.add(cmbPartColor, constraints);
        constraints.gridy = 2;
        contentPane.add(txtPartWeight, constraints);
        constraints.gridy = 3;
        contentPane.add(txtPartCity, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
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