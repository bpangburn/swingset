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

public class Example5 extends JFrame {
	
	SSConnection ssConnection = null;
 	SSJdbcRowSetImpl rowset   = null;
	SSDataGrid dataGrid = new SSDataGrid();
	
	public Example5(){
		super("Example 5");
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
 			dataGrid.setMessageWindow(this);
 			// HIDE THE PART ID COLUMN
 			// THIS SETS THE WIDTH OF THE COLUMN TO 0
 			dataGrid.setHiddenColumns(new String[]{"part_id"});
 			dataGrid.setUneditableColumns(new String[]{"part_id"});
 			
 			
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
 * Revision 1.2  2004/10/25 19:52:12  prasanth
 * Modified to work with new SwingSet (SSConnection & SSRowSet)
 *
 * Revision 1.1  2003/12/18 20:14:43  prasanth
 * Initial commit.
 *
 */
