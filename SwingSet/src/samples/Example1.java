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
import javax.sql.*;
import java.sql.*;
import java.awt.*;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;
import com.nqadmin.swingSet.datasources.SSConnection;

 /**
  * This example demonstrates the use of SSTextDocument to display information in
  * JTextField (Name, City, and Status). The navigation is done with
  * SSDataNavigator.
  */
 public class Example1 extends JFrame{

    JLabel lblSupplierName   = new JLabel("Name");
    JLabel lblSupplierCity   = new JLabel("City");
    JLabel lblSupplierStatus = new JLabel("Status");

    JTextField txtSupplierName   = new JTextField();
    JTextField txtSupplierCity   = new JTextField();
    JTextField txtSupplierStatus = new JTextField();
    JTextField txtSupplierID   = new JTextField();

    SSConnection ssConnection;
    SSJdbcRowSetImpl rowset   = null;
    SSDataNavigator navigator = null;

    public Example1() {

        super("Example1");

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

        txtSupplierID.setDocument(new SSTextDocument(rowset,"supplier_id"));
        txtSupplierName.setDocument(new SSTextDocument(rowset,"supplier_name"));
        txtSupplierCity.setDocument(new SSTextDocument(rowset,"city"));
        txtSupplierStatus.setDocument(new SSTextDocument(rowset,"status"));

        lblSupplierName.setPreferredSize(new Dimension(75,20));
        lblSupplierCity.setPreferredSize(new Dimension(75,20));
        lblSupplierStatus.setPreferredSize(new Dimension(75,20));

        txtSupplierName.setPreferredSize(new Dimension(150,20));
        txtSupplierCity.setPreferredSize(new Dimension(150,20));
        txtSupplierStatus.setPreferredSize(new Dimension(150,20));

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
        contentPane.add(txtSupplierStatus, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        contentPane.add(navigator,constraints);

        setVisible(true);

    }

    public static void main(String[] args){
        new Example1();
    }

 }

/*
 * $Log$
 * Revision 1.5  2004/10/25 22:01:16  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 */