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

public class Example7 extends JFrame {

    SSConnection ssConnection = null;
    SSJdbcRowSetImpl rowset   = null;
    SSDataGrid dataGrid = new SSDataGrid();
    

    public Example7(){
        super("Example 7");
        setSize(730,290);
        init();
    }

    private void init(){

        try{
        	String url = "http://192.168.0.234/populate.sql";
        	ssConnection = new SSConnection("jdbc:h2:mem:suppliers_and_parts;INIT=runscript from '"+url+"'", "sa", "");
            ssConnection.setDriverName("org.h2.Driver");
            ssConnection.createConnection();
            
            rowset = new SSJdbcRowSetImpl(ssConnection);
            rowset.setCommand("SELECT supplier_id, part_id,quantity, ship_date, supplier_part_id FROM supplier_part_data ORDER BY supplier_id, part_id;");

            //  SET THE HEADER BEFORE SETTING THE ROWSET
            dataGrid.setHeaders(new String[]{"Supplier Name", "Part Name", "Quantity", " Ship Date"});
            dataGrid.setSSRowSet(rowset);

            dataGrid.updateUI();

            // HIDE THE PART ID COLUMN
            // THIS SETS THE WIDTH OF THE COLUMN TO 0
            dataGrid.setHiddenColumns(new String[]{"supplier_part_id"});

            dataGrid.setDateRenderer("ship_date");
            if (this == null) {
                System.out.println("This frame is null");
            }
            dataGrid.setMessageWindow(this);
            dataGrid.setUneditableColumns(new int[]{4});
            
            // DISABLES NEW INSERTIONS TO THE DATA BASE.
            // DUE TO H2 DATABASE PROPERTIES, INSERTION OF NEW DATA CAUSES ERRORS.
            // ANY CHANGES MADE TO THE PRESENT RECORD WILL BE SAVED BUT INSERTIONS ARE NOT ALLOWED
            // IN H2.
            dataGrid.setInsertion(false);
           
             // ADDED STATEMENT "SCROLL INSENSITIVITY" FOR EXAMPLE TO BE
             // COMPATIBLE WITH H2 DATABASE DEFAULT SETTINGS.
            Statement stmt = ssConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery("SELECT supplier_name, supplier_id FROM supplier_data ORDER BY supplier_name;");
       
            rs.last();
            String[] displayItems  = new String[rs.getRow()];
            Integer[] underlyingNumbers = new Integer[rs.getRow()];
            rs.beforeFirst();

            for( int i=0; i<displayItems.length; i++)
            {
                rs.next();
                displayItems[i] = rs.getString("supplier_name");
                underlyingNumbers[i] = new Integer(rs.getInt("supplier_id"));

            }

            dataGrid.setComboRenderer("supplier_id",displayItems,underlyingNumbers);
            rs = stmt.executeQuery("SELECT part_name, part_id FROM part_data ORDER BY part_name;");
            rs.last();
            displayItems  = new String[rs.getRow()];
            underlyingNumbers = new Integer[rs.getRow()];
            rs.beforeFirst();

            for( int i=0; i<displayItems.length; i++)
            {
                rs.next();
                displayItems[i] = rs.getString("part_name");
                underlyingNumbers[i] = new Integer(rs.getInt("part_id"));

            }

            dataGrid.setComboRenderer("part_id",displayItems,underlyingNumbers);

        }catch(SQLException se){
            se.printStackTrace();
        }catch(ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }

        getContentPane().add(dataGrid.getComponent());

        setVisible(true);

    } // END OF INIT FUNCTION

 }// END OF EXAMPLE 7

/*
 * $Log$
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
