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
import javax.sql.*;
import java.sql.*;
import java.awt.*;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;
import com.nqadmin.swingSet.datasources.SSConnection;

 /**
  * This example demonstrates the use of SSTextDocument to display information in
  * JTextField (Name and City) and SSComboBox (Status). The navigation is done with
  * SSDataNavigator.
  */
 public class Example2 extends JFrame{

    JLabel lblSupplierName   = new JLabel("Name");
    JLabel lblSupplierCity   = new JLabel("City");
    JLabel lblSupplierStatus = new JLabel("Status");

    JTextField txtSupplierName   = new JTextField();
    JTextField txtSupplierCity   = new JTextField();
    SSComboBox cmbSupplierStatus = new SSComboBox();

    SSConnection ssConnection = null;
    SSJdbcRowSetImpl rowset   = null;
    SSDataNavigator navigator = null;

    public Example2(){

        super("Example2");
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
            rowset.setCommand("SELECT * FROM supplier_data");
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

        txtSupplierName.setDocument(new SSTextDocument(rowset,"supplier_name"));
        txtSupplierCity.setDocument(new SSTextDocument(rowset,"city"));
        // LETS ASSUME THE STATUS CODE TO TEXT MAPPINGS
        // 10 -> BAD
        // 20 -> BETTER
        // 30 -> GOOD
        int[] codes = {10,20,30};
        String[] options = {"Bad","Better","Good"};
        // SET THE OPTIONS TO BE DISPLAYED AND THEIR CORRESPONDING VALUES
        cmbSupplierStatus.setOption(options,codes);
        // BIND THE COMBO TO THE STATUS COLUMN OF THE ROWSET
        cmbSupplierStatus.bind(rowset,"status");

        lblSupplierName.setPreferredSize(new Dimension(75,20));
        lblSupplierCity.setPreferredSize(new Dimension(75,20));
        lblSupplierStatus.setPreferredSize(new Dimension(75,20));

        txtSupplierName.setPreferredSize(new Dimension(150,20));
        txtSupplierCity.setPreferredSize(new Dimension(150,20));
        cmbSupplierStatus.setPreferredSize(new Dimension(150,20));

        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        contentPane.add(lblSupplierName, constraints);
        constraints.gridy = 1;
        contentPane.add(lblSupplierCity, constraints);
        constraints.gridy = 2;
        contentPane.add(lblSupplierStatus, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        contentPane.add(txtSupplierName, constraints);
        constraints.gridy = 1;
        contentPane.add(txtSupplierCity, constraints);
        constraints.gridy = 2;
        contentPane.add(cmbSupplierStatus, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        contentPane.add(navigator,constraints);

        setVisible(true);

    }

    public static void main(String[] args){
        new Example2();
    }

 }

/*
 * $Log$
 * Revision 1.7  2005/02/04 22:40:12  yoda2
 * Updated Copyright info.
 *
 * Revision 1.6  2004/11/11 15:04:38  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.5  2004/10/25 22:01:16  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 */