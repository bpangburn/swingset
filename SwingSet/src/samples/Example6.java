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
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;
import com.nqadmin.swingSet.datasources.SSConnection;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;

public class Example6 extends JFrame {

    SSConnection ssConnection = null;
    SSJdbcRowSetImpl rowset   = null;
    SSDataGrid dataGrid = new SSDataGrid();

    public Example6(){
        super("Example 6");
        setSize(300,350);
        init();
    }

    private void init(){


        try{
            ssConnection = new SSConnection("jdbc:postgresql://pgserver.greatmindsworking.com/suppliers_and_parts",
                "swingset", "test");
            ssConnection.setDriverName("org.postgresql.Driver");
            ssConnection.createConnection();
            rowset = new SSJdbcRowSetImpl(ssConnection);
            rowset.setCommand("SELECT part_name,color_code, weight, city,part_id FROM part_data ORDER BY part_name;");
            //  SET THE HEADER BEFORE SETTING THE ROWSET
            dataGrid.setHeaders(new String[]{"Part Name", "Color Code", " Weight", "City"});
            dataGrid.setRowSet(rowset);
            // HIDE THE PART ID COLUMN
            // THIS SETS THE WIDTH OF THE COLUMN TO 0
            //dataGrid.setHiddenColumns(new String[]{"part_id"});
            dataGrid.setHiddenColumns(new String[]{"part_id"});

            dataGrid.setMessageWindow(this);
            dataGrid.setUneditableColumns(new String[]{"part_id"});

            dataGrid.setComboRenderer("color_code",new String[]{"Red","Green","Blue"},
                    new Integer[]{new Integer(0),new Integer(1),new Integer(2)});
            dataGrid.setDefaultValues(new int[]{1,2,3},new Object[]{new Integer(0),
                    new Integer(20),new String("New Orleans")});

            dataGrid.setPrimaryColumn("part_id");
            dataGrid.setSSDataValue(new SSDataValue(){
                public Object getPrimaryColumnValue(){
                    // YOUR PRIMARY KEY VALUE GENERATION GOES HERE
                    // IF ITS SOME THING USER ENTERS THEN NO PROBLEM
                    // IF ITS AN AUTO INCREMENT FIELD THEN IT DEPENDS ON
                    // THE DATABASE DRIVER YOU ARE USING.
                    // IF THE UPDATEROW CAN RETRIEVE THE VALUES FOR THE ROW
                    // WITH OUT KNOWING THE PRIMARY  KEY VALUE ITS FINE
                    // BUT POSTGRES CAN'T UPDATE ROW WITH OUT THE PRIMARY
                    // COLUMN.

                    // YOUR PRIMARY KEY VALUE GENERATION GOES HERE.
                    // the database does not allow updates so just returning
                    // a fixed value. in your code you have to generate unique value.
                    return new Integer(4);
                }
            });


        }catch(SQLException se){
            se.printStackTrace();
        }catch(ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }

        getContentPane().add(dataGrid.getComponent());

        setVisible(true);

    } // END OF INIT FUNCTION

 }// END OF EXAMPLE 6

/*
 * $Log$
 * Revision 1.3  2004/10/25 22:01:15  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.2  2004/10/25 19:52:12  prasanth
 * Modified to work with new SwingSet (SSConnection & SSRowSet)
 *
 * Revision 1.1  2003/12/18 20:14:43  prasanth
 * Initial commit.
 *
 */
