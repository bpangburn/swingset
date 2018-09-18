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

import java.sql.*;
import java.awt.*;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;
import com.nqadmin.swingSet.datasources.SSConnection;

 /**
  * This example demonstrates the use of SSTextDocument to display information in
  * SSDBComboBox (Supplier and Part) and JTextField (ty). The navigation
  * is done with SSDataNavigator.
  */
 public class Example3 extends JFrame{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4859550616628544511L;
	JLabel lblSupplierName   = new JLabel("Supplier");
    JLabel lblPartName   = new JLabel("Part");
    JLabel lblQuantity = new JLabel("Quantity");

    SSDBComboBox cmbSupplierName = null;
    SSDBComboBox cmbPartName = null;
    SSTextField txtQuantity   = new SSTextField();

    SSConnection ssConnection = null;
    SSJdbcRowSetImpl rowset   = null;
    SSDataNavigator navigator = null;

    public Example3(String url){

        super("Example3");
        setSize(600,200);

        try{
        	System.out.println("url from ex 3: "+url);
        	ssConnection = new SSConnection("jdbc:h2:mem:suppliers_and_parts;INIT=runscript from '"+url+"'", "sa", "");
        	ssConnection.setDriverName("org.h2.Driver");
            ssConnection.createConnection();
            
            rowset = new SSJdbcRowSetImpl(ssConnection);
            rowset.setCommand("SELECT * FROM supplier_part_data");
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
			private static final long serialVersionUID = 4343059684161003109L;
			@Override
        	public void performPreInsertOps() {
 			
 				super.performPreInsertOps();
 				cmbSupplierName.setSelectedItem(null);
 				cmbPartName.setSelectedItem(null);
 				txtQuantity.setText(null);
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
        
        String query = "SELECT * FROM supplier_data;";
        cmbSupplierName = new SSDBComboBox(ssConnection, query, "supplier_id", "supplier_name");  
        cmbSupplierName.bind(rowset,"supplier_id");
        
        query = "SELECT * FROM part_data;";
        cmbPartName = new SSDBComboBox(ssConnection, query, "part_id", "part_name");
        cmbPartName.bind(rowset,"part_id");
        
        txtQuantity.bind(rowset,"quantity");
        
        try{
            cmbPartName.execute();
            cmbSupplierName.execute();
         
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
       
        
        lblSupplierName.setPreferredSize(new Dimension(75,20));
        lblPartName.setPreferredSize(new Dimension(75,20));
        lblQuantity.setPreferredSize(new Dimension(75,20));

        cmbSupplierName.setPreferredSize(new Dimension(150,20));
        cmbPartName.setPreferredSize(new Dimension(150,20));
        txtQuantity.setPreferredSize(new Dimension(150,20));
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        contentPane.add(lblSupplierName, constraints);
        constraints.gridy = 1;
        contentPane.add(lblPartName, constraints);
        constraints.gridy = 2;
        contentPane.add(lblQuantity, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        contentPane.add(cmbSupplierName, constraints);
        constraints.gridy = 1;
        contentPane.add(cmbPartName, constraints);
        constraints.gridy = 2;
        contentPane.add(txtQuantity, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        contentPane.add(navigator,constraints);

        setVisible(true);
    }
  
 }

/*
 * $Log$
 * Revision 1.10  2012/06/07 15:54:38  beevo
 * Modified example for compatibilty with H2 database.
 *
 * Revision 1.9  2005/02/14 18:50:25  prasanth
 * Updated to remove calls to deprecated methods.
 *
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