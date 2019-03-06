
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.nqadmin.swingSet.SSDBComboBox;
import com.nqadmin.swingSet.SSDBNavImp;
import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.SSTextField;
import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;

/**
 * This example demonstrates the use of SSTextDocument to display information in
 * SSDBComboBox (supplier and part) and SSTextField (quantity). The navigation
 * is done with SSDataNavigator.
 */
public class Example3 extends JFrame {

	private static final long serialVersionUID = 4859550616628544511L;
	JLabel lblSupplierName = new JLabel("Supplier");
	JLabel lblPartName = new JLabel("Part");
	JLabel lblQuantity = new JLabel("Quantity");

	SSDBComboBox cmbSupplierName = null;
	SSDBComboBox cmbPartName = null;
	SSTextField txtQuantity = new SSTextField();

	SSConnection ssConnection = null;
	SSJdbcRowSetImpl rowset = null;
	SSDataNavigator navigator = null;

	/**
	 * Constructor for Example3
	 * 
	 * @param url - path to SQL to create suppliers & parts database
	 */
	public Example3(String url) {

		super("Example3");
		setSize(600, 200);

		try {
			System.out.println("url from ex 3: " + url);
			this.ssConnection = new SSConnection("jdbc:h2:mem:suppliers_and_parts;INIT=runscript from '" + url + "'",
					"sa", "");
			this.ssConnection.setDriverName("org.h2.Driver");
			this.ssConnection.createConnection();

			this.rowset = new SSJdbcRowSetImpl(this.ssConnection.getConnection());
			this.rowset.setCommand("SELECT * FROM supplier_part_data");
			this.navigator = new SSDataNavigator(this.rowset);
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		// THE FOLLOWING CODE IS USED BECAUSE OF AN H2 LIMITATION. UPDATABLE ROWSET IS
		// NOT
		// FULLY IMPLEMENTED AND AN EXECUTE COMMAND IS REQUIRED WHEN INSERTING A NEW
		// ROW AND KEEPING THE CURSOR AT THE NEWLY INSERTED ROW.
		// IF USING ANOTHER DATABASE, THE FOLLOWING IS NOT REQURIED:
		this.navigator.setDBNav(new SSDBNavImp(this) {
			/**
			 * unique serial id
			 */
			private static final long serialVersionUID = 4343059684161003109L;

			/*
			 * @Override public void performPreInsertOps() { super.performPreInsertOps();
			 * Example3.this.cmbSupplierName.setSelectedItem(null);
			 * Example3.this.cmbPartName.setSelectedItem(null);
			 * Example3.this.txtQuantity.setText(null); }
			 */

			@Override
			public void performPostInsertOps() {
				super.performPostInsertOps();
				try {
					Example3.this.rowset.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		});

		String query = "SELECT * FROM supplier_data;";
		this.cmbSupplierName = new SSDBComboBox(this.ssConnection, query, "supplier_id", "supplier_name");
		this.cmbSupplierName.bind(this.rowset, "supplier_id");

		query = "SELECT * FROM part_data;";
		this.cmbPartName = new SSDBComboBox(this.ssConnection, query, "part_id", "part_name");
		this.cmbPartName.bind(this.rowset, "part_id");

		this.txtQuantity.bind(this.rowset, "quantity");

		try {
			this.cmbPartName.execute();
			this.cmbSupplierName.execute();

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.lblSupplierName.setPreferredSize(new Dimension(75, 20));
		this.lblPartName.setPreferredSize(new Dimension(75, 20));
		this.lblQuantity.setPreferredSize(new Dimension(75, 20));

		this.cmbSupplierName.setPreferredSize(new Dimension(150, 20));
		this.cmbPartName.setPreferredSize(new Dimension(150, 20));
		this.txtQuantity.setPreferredSize(new Dimension(150, 20));

		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx = 0;
		constraints.gridy = 0;
		contentPane.add(this.lblSupplierName, constraints);
		constraints.gridy = 1;
		contentPane.add(this.lblPartName, constraints);
		constraints.gridy = 2;
		contentPane.add(this.lblQuantity, constraints);

		constraints.gridx = 1;
		constraints.gridy = 0;
		contentPane.add(this.cmbSupplierName, constraints);
		constraints.gridy = 1;
		contentPane.add(this.cmbPartName, constraints);
		constraints.gridy = 2;
		contentPane.add(this.txtQuantity, constraints);

		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		contentPane.add(this.navigator, constraints);

		setVisible(true);
	}

}

/*
 * $Log$ Revision 1.10 2012/06/07 15:54:38 beevo Modified example for
 * compatibilty with H2 database.
 *
 * Revision 1.9 2005/02/14 18:50:25 prasanth Updated to remove calls to
 * deprecated methods.
 *
 * Revision 1.8 2005/02/04 22:40:12 yoda2 Updated Copyright info.
 *
 * Revision 1.7 2004/11/11 15:04:38 yoda2 Using TextPad, converted all tabs to
 * "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.6 2004/11/01 19:18:51 yoda2 Fixed 0.9.X compatibility issues.
 *
 * Revision 1.5 2004/10/25 22:01:16 yoda2 Updated JavaDoc for new datasource
 * abstraction layer in 0.9.0 release.
 *
 */
