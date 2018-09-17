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
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;
import com.nqadmin.swingSet.datasources.SSConnection;

import javax.swing.*;
import java.sql.*;

public class Example6 extends JFrame {

    SSConnection ssConnection = null;
    SSJdbcRowSetImpl rowset   = null;
    SSDataGrid dataGrid = new SSDataGrid();
    String url;
    public Example6(String url){
        super("Example 6");
        this.url = url;
        setSize(580,170);
        init();
    }

    private void init(){

        try{
        	System.out.println("url from ex 6: "+url);
        	ssConnection = new SSConnection("jdbc:h2:mem:suppliers_and_parts;INIT=runscript from '"+url+"'", "sa", "");
            ssConnection.setDriverName("org.h2.Driver");
            ssConnection.createConnection();
            
            rowset = new SSJdbcRowSetImpl(ssConnection);
            rowset.setCommand("SELECT part_name,color_code, weight, city,part_id FROM part_data ORDER BY part_name;");
            
            //  SET THE HEADER BEFORE SETTING THE ROWSET
            dataGrid.setHeaders(new String[]{"Part Name", "Color Code", " Weight", "City"});
            dataGrid.setSSRowSet(rowset);
            // HIDE THE PART ID COLUMN
            // THIS SETS THE WIDTH OF THE COLUMN TO 0
            dataGrid.setHiddenColumns(new String[]{"part_id"});
            dataGrid.setMessageWindow(this);
            dataGrid.setUneditableColumns(new String[]{"part_id"});
            
            // THIS DISABLES NEW INSERTIONS TO THE DATA BASE.
            // DUE TO H2 DATABASE PROPERTIES, INSERTION OF NEW DATA CAUSES PROBLEMS.
            // ANY CHANGES MADE TO PRESENT RECORD WILL BE SAVED.
            dataGrid.setInsertion(false);

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
 * Revision 1.7  2012/06/07 15:54:38  beevo
 * Modified example for compatibilty with H2 database.
 *
 * Revision 1.6  2005/02/14 18:50:25  prasanth
 * Updated to remove calls to deprecated methods.
 *
 * Revision 1.5  2005/02/04 22:40:12  yoda2
 * Updated Copyright info.
 *
 * Revision 1.4  2004/11/11 15:04:38  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
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