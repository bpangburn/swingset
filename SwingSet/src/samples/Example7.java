/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003, The Pangburn Company, Inc. and Prasanth R. Pasala
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
import com.sun.rowset.JdbcRowSetImpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;


public class Example7 extends JFrame {
	
	Connection conn = null;
	JdbcRowSetImpl rowset = null;
	SSDataGrid dataGrid = new SSDataGrid();
	
	public Example7(){
		super("Example 7");
		setSize(650,350);
		init();
	}
	
	private void init(){
		
		
		try{
			Class.forName("org.postgresql.Driver");	
 			conn = DriverManager.getConnection("jdbc:postgresql://pgserver.greatmindsworking.com/suppliers_and_parts","swingset","test");
 			rowset = new JdbcRowSetImpl(conn); 
 			rowset.setCommand("SELECT supplier_id, part_id,quantity, ship_date, supplier_part_id FROM supplier_part_data ORDER BY supplier_id, part_id;");
 			//  SET THE HEADER BEFORE SETTING THE ROWSET
 			
			dataGrid.setHeaders(new String[]{"Supplier Name", "Part Name", "Quantity", " Ship Date"});
			dataGrid.setRowSet(rowset);
			
			dataGrid.updateUI();
			// HIDE THE PART ID COLUMN
 			// THIS SETS THE WIDTH OF THE COLUMN TO 0
 			//dataGrid.setHiddenColumns(new String[]{"part_id"});
 			dataGrid.setHiddenColumns(new String[]{"supplier_part_id"});
 			dataGrid.setDateRenderer("ship_date");
 			if(this == null)
 				System.out.println("This frame is null");
 			dataGrid.setMessageWindow(this);
 			//dataGrid.setUneditableColumns(new String[]{"supplier_part_id"});
 			dataGrid.setUneditableColumns(new int[]{4});
 			ResultSet rs = conn.createStatement().executeQuery("SELECT supplier_name, supplier_id FROM supplier_data ORDER BY supplier_name;");
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
 			
 			rs = conn.createStatement().executeQuery("SELECT part_name, part_id FROM part_data ORDER BY part_name;");
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
 	
 }// END OF EXAMPLE 5.
/*
 * $Log$
 */
