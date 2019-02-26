/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Contributors:
 *   Prasanth R. Pasala
 *   Brian E. Pangburn
 *   Diego Gil
 *   Man "Bee" Vo
 ******************************************************************************/

import com.nqadmin.swingSet.*;

import javax.swing.*;

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
	
	private static final long serialVersionUID = 9205688923559422257L;
	JLabel lblSupplierName   = new JLabel("Name");
    JLabel lblSupplierCity   = new JLabel("City");
    JLabel lblSupplierStatus = new JLabel("Status");

    SSTextField txtSupplierName   = new SSTextField();
    SSTextField txtSupplierCity   = new SSTextField();
    SSComboBox cmbSupplierStatus = new SSComboBox();

    SSConnection ssConnection = null;
    SSJdbcRowSetImpl rowset   = null;
    SSDataNavigator navigator = null;

    public Example2(String url){

        super("Example2");
        setSize(600,200);

        try{
        	System.out.println("url from ex 2: "+url);
        	ssConnection = new SSConnection("jdbc:h2:mem:suppliers_and_parts;INIT=runscript from '"+url+"'", "sa", "");
            ssConnection.setDriverName("org.h2.Driver");
            ssConnection.createConnection();
            
            rowset = new SSJdbcRowSetImpl(ssConnection);
            rowset.setCommand("SELECT * FROM supplier_data");
            navigator = new SSDataNavigator(rowset);
        }catch(SQLException se){
            se.printStackTrace();
        }catch(ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }
        
        // THE FOLLOWING CODE IS USED BECAUSE OF AN H2 LIMITATION. UPDATABLE ROWSET IS NOT
        // FULLY IMPLEMENTED AND AN EXECUTE COMMAND IS REQUIRED WHEN INSERTING A NEW
        // ROW AND KEEPING THE CURSOR AT THE NEWLY INSERTED ROW.
        // IF USING ANOTHER DATABASE, THE FOLLOWING IS NOT REQURIED:   
        navigator.setDBNav(new SSDBNavAdapter(){
           	/**
			 * 
			 */
			private static final long serialVersionUID = 6964661066285402119L;
			@Override
        	public void performPreInsertOps() {
 		
 				super.performPreInsertOps();
 				txtSupplierName.setText(null);
 				txtSupplierCity.setText(null);
 				cmbSupplierStatus.setSelectedItem(null);
 			}
        	@Override
 			public void performPostInsertOps() {
 
 				super.performPostInsertOps();
 				try {
					rowset.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
 			}  
 			
         });
        
        // .BIND(SSRowSet _sSRowSet, String _columnName) REPLACES 
        // .SETDOCUMENT(Document document) AND BINDS THE ROWSET AND COLUMN NAME
        txtSupplierName.bind(rowset,"supplier_name");
        txtSupplierCity.bind(rowset,"city");
        
        // LETS ASSUME THE STATUS CODE TO TEXT MAPPINGS
        // 10 -> BAD
        // 20 -> BETTER
        // 30 -> GOOD
        int[] codes = {10,20,30};
        String[] options = {"Bad","Better","Good"};
        
        // SET THE OPTIONS TO BE DISPLAYED AND THEIR CORRESPONDING VALUES
        cmbSupplierStatus.setOptions(options,codes);
        
        // BIND THE COMBO TO THE STATUS COLUMN OF THE ROWSET
        cmbSupplierStatus.bind(rowset,"status");
        cmbSupplierStatus.setSelectedIndex(1);
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

 }

/*
 * $Log$
 * Revision 1.9  2012/06/07 15:54:38  beevo
 * Modified example for compatibilty with H2 database.
 *
 * Revision 1.8  2005/02/14 18:50:25  prasanth
 * Updated to remove calls to deprecated methods.
 *
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
