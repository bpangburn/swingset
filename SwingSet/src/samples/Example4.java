/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2004, The Pangburn Company, Inc. and Prasanth R. Pasala
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

    JTextField txtPartID = new JTextField();
    JTextField txtPartIDLinkedToCombo = new JTextField();
    // LISTENER OBJECTS FOR TEXTFIELD LINKED TO COMBO AND THE TEXT FIELD BOUND TO
    // PART_ID COLUMN
    MyPartIDDocumentListener partIDListener = new MyPartIDDocumentListener();
    MyPartIDDocumentLinkedToComboListener partIDLinkedToComboListener
        = new MyPartIDDocumentLinkedToComboListener();


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
        txtPartIDLinkedToCombo.setText(txtPartID.getText());

        String query = "SELECT * FROM part_data;";
        cmbSelectPart = new SSDBComboBox(ssConnection, query,
            "part_id", "part_name", txtPartIDLinkedToCombo);
        try{
            cmbSelectPart.execute();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        cmbPartColor = new SSComboBox();
        cmbPartColor.setOption(new String[]{"Red","Green","Blue"});
        cmbPartColor.bind(rowset,"color_code");

        txtPartWeight.setDocument(new SSTextDocument(rowset,"weight"));
        txtPartCity.setDocument(new SSTextDocument(rowset,"city"));

        lblSelectPart.setPreferredSize(new Dimension(75,20));
        lblPartColor.setPreferredSize(new Dimension(75,20));
        lblPartWeight.setPreferredSize(new Dimension(75,20));
        lblPartCity.setPreferredSize(new Dimension(75,20));

        cmbSelectPart.getComboBox().setPreferredSize(new Dimension(150,20));
        cmbPartColor.getComboBox().setPreferredSize(new Dimension(150,20));
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
        contentPane.add(cmbSelectPart.getComboBox(), constraints);
        constraints.gridy = 1;
        contentPane.add(cmbPartColor.getComboBox(), constraints);
        constraints.gridy = 2;
        contentPane.add(txtPartWeight, constraints);
        constraints.gridy = 3;
        contentPane.add(txtPartCity, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        contentPane.add(navigator,constraints);

        txtPartID.getDocument().addDocumentListener(partIDListener);
        txtPartIDLinkedToCombo.getDocument().addDocumentListener(partIDLinkedToComboListener);

        setVisible(true);

    }

        // LISTENER FOR THE TEXT FIELD BOUND TO THE Part_ID COLUMN
    private class MyPartIDDocumentListener implements DocumentListener{

        public void changedUpdate(DocumentEvent de) {

        }
        // WHEN EVER THE PART ID CHANGES, CHANGE THE PART ID IN TEXT FIELD
        // LINKED TO COMBO SO THAT COMBO ALSO SHOW THE RIGHT PART NAME
        public void insertUpdate(DocumentEvent de) {
            txtPartIDLinkedToCombo.getDocument().removeDocumentListener(partIDLinkedToComboListener);
            if(txtPartIDLinkedToCombo.getText() != txtPartID.getText()){
                txtPartIDLinkedToCombo.setText(txtPartID.getText());
            }
            txtPartIDLinkedToCombo.getDocument().addDocumentListener(partIDLinkedToComboListener);
        }
        public void removeUpdate(DocumentEvent de) {

        }
    }

    // LISTENER FOR THE TEXT FIELD LINKED TO THE PART SELECTION COMBO
    private class MyPartIDDocumentLinkedToComboListener implements DocumentListener {
        long partID = -1;
        public void changedUpdate(DocumentEvent de) {

        }
        // WHEN THERE IS A CHANGE IN THIS TEXT FIELD MOVE THE ROWSET TO THE APPROPRIATE
        // RECORD
        public void insertUpdate(DocumentEvent de) {
            Document doc = txtPartIDLinkedToCombo.getDocument();
            try {
                // GET THE NEW PART ID IN TEXT FIELD LINKED TO COMBO
                String strPartID = doc.getText(0,doc.getLength());
                partID = -1;
                if( !strPartID.equals("") ) {
                    partID = Integer.parseInt(strPartID);
                }
                // REMOVE LISTENER FOR THE PART_ID TEXT FIELD BOUND TO DB
                txtPartID.getDocument().removeDocumentListener(partIDListener);
                // CHECK IF THE CURRENT RECORD CORRESPONDS TO THAT OF THE DESIRED ONE.
                if(partID != rowset.getLong("part_id") ){
                    // MOVE THE RECORD SET TO THE RECORD NUMBER EQUAL TO THE SELECTED
                    // ITEM NUMBER IN COMBO
                    // COMBO BOX ITEM NUMBERING STARTS FROM ZERO
                    // FOR ROWSET IT STARTS FROM ONE.
                    int index = cmbSelectPart.getComboBox().getSelectedIndex() + 1;
                    rowset.absolute(index);
                    int numRecords = cmbSelectPart.getComboBox().getItemCount();
                    int count = 0;
                    // CHECK IF THE CURRENT RECORD CORRESPONDS TO THAT OF THE DESIRED ONE.
                    while(partID != rowset.getLong("part_id") ) {
                        // ABOVE CONDITION SHOULD ALWAYS FAIL EXCEPT IF SOME ONE DELETED
                        // A ROW OUT SIDE OF THIS APPLICATION
                        // IN SUCH CASE LOOK FOR THE CORRECT RECORD
                        if( !rowset.next()) {
                            rowset.beforeFirst();
                            rowset.next();
                        }
                            count++;
                        //number of items in combo is the number of records in resultset.
                        //so if for some reason item is in combo but deleted in rowset
                        //To avoid infinite loop in such scenario
                        if(count > numRecords + 5){
                            //JOptionPane.showInternalMessageDialog(this,"Record deleted. Info the admin about this","Row not found",JOptionPane.OK_OPTION);
                            break;
                        }
                    }
                }
                // ADD BACK THE LISTENER
                txtPartID.getDocument().addDocumentListener(partIDListener);

            }catch(SQLException se){
                se.printStackTrace();
            }catch(NumberFormatException nfe){
                nfe.printStackTrace();
            }catch(BadLocationException ble){
                ble.printStackTrace();
            }
        }
        public void removeUpdate(DocumentEvent de) {

        }
    }



    public static void main(String[] args){
        new Example4();
    }

 }

/*
 * $Log$
 * Revision 1.6  2004/11/01 19:18:51  yoda2
 * Fixed 0.9.X compatibility issues.
 *
 * Revision 1.5  2004/10/25 22:01:16  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 */