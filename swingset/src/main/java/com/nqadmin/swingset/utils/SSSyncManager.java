/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

package com.nqadmin.swingset.utils;

import com.nqadmin.swingset.datasources.*;
import java.sql.SQLException;
import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDataNavigator;
import javax.sql.RowSetListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.sql.RowSetEvent;

/**
 * SSSyncManager.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Used to synchronize a data navigator and a navigation combo box.
 */
public class SSSyncManager {

	/**
	 * Listener for combo box to update data navigator when combo box-based
	 * navigation occurs.
	 */
	protected class SyncComboListener implements ActionListener {
		int actionPerformedCount = 0;

		protected long comboPK = -1;
		// protected String id = "";

		// WHEN THERE IS A CHANGE IN THIS VALUE MOVE THE ROWSET SO THAT
		// ITS POSITIONED AT THE RIGHT RECORD.
		@Override
		public void actionPerformed(ActionEvent ae) {
	
			SSSyncManager.this.rowset.removeRowSetListener(SSSyncManager.this.rowsetListener);
			
			try {
				// IF THIS IS NOT CAUSED BY THE USER ACTION (IN WHICH THE FOCUS WILL BE ON THE
				// COMBO) NOTHING TO DO
				if (rowset == null || rowset.getRow() < 1 || comboBox.getSelectedIndex() == -1
						/* || comboBox.textField == null */
						/* || comboBox.isBoundTextFieldNull() */
						/* || !comboBox.hasFocus() */ // with rewrite, the editor and not the combo likely has the focus
						) {
				
					return;
				}

				// this.id = ""+SSSyncManager.this.comboBox.getSelectedFilteredValue();
				comboPK = SSSyncManager.this.comboBox.getSelectedValue();
				

				// UPDATE THE PRESENT ROW BEFORE MOVING TO ANOTHER ROW. *take out to make it
				// faster
				// dataNavigator.updatePresentRow();
				// if(id != rowset.getLong(columnName)) {
				// TODO Consider getColumnText() over rowset.getString()

				if (comboPK != SSSyncManager.this.rowset.getLong(SSSyncManager.this.columnName)) {
					// long indexOfId = SSSyncManager.this.comboBox.itemMap.get(this.id) + 1;
					int indexOfPK = comboBox.getMappings().indexOf((Long) comboPK) + 1;
					//int index = (int) indexOfPK;
					SSSyncManager.this.rowset.absolute(indexOfPK);
					int numRecords = SSSyncManager.this.comboBox.getItemCount();
					int count = 0;
					// while (id != rowset.getLong(columnName)) {
					// IF AFTER POSITIONING THE ROWSET INDEX AT THE COMBO INDEX, THE VALUES DON'T
					// MATCH
					// PERFORM A MANUAL LOOP TO TRY TO FIND A MATCH
					// PRESUMING RECORDS COULD BE ADDED/DELETED BY OTHER CONNECTIONS, DON'T LOOP
					// THROUGH ALL OF THE RECORDS
					// MORE THAN ONCE PLUS A CUSHION OF 5
					while (comboPK != rowset.getLong(columnName)) {
						if (!SSSyncManager.this.rowset.next()) {
							SSSyncManager.this.rowset.beforeFirst();
							SSSyncManager.this.rowset.next();
						}

						count++;

						System.out.println(
								"SSSyncManager SSRowSet and SSDBComboBox values do not match for the same index. Looping through each record for a match. Pass # "
										+ count + ".");

						// number of items in combo is the number of records in resultset.
						// so if for some reason item is in combo but deleted in rowset
						// To avoid infinite loop in such scenario
						if (count > numRecords + 5) {
							comboBox.repaint();
							System.out.println("SSSyncManager unable to find a record matching the selection in the dropdown list: " + comboBox.getSelectedStringValue() + ".");
							// JOptionPane.showInternalMessageDialog(this,"Record deleted. Info the admin
							// about this","Row not found",JOptionPane.OK_OPTION);
							break;
						}
					}
				}
				

			} catch (SQLException se) {
				se.printStackTrace();
			} finally {
				System.out.println("SSSyncManager.MyComboListener.actionPerformed(): " + actionPerformedCount++);
				SSSyncManager.this.rowset.addRowSetListener(SSSyncManager.this.rowsetListener);

			}
		}
	} // protected class MyComboListener implements ActionListener {

	/**
	 * Listener for rowset.
	 */
	protected class SyncRowSetListener implements RowSetListener {

		@Override
		public void cursorMoved(RowSetEvent rse) {
			adjustValue();
		}

		@Override
		public void rowChanged(RowSetEvent rse) {
			adjustValue();
		}

		@Override
		public void rowSetChanged(RowSetEvent rse) {
			adjustValue();
		}

	}

	/**
	 * SSRowSet column used as basis for synchronization.
	 */
	protected String columnName;

	/**
	 * SSDBComboBox used for record navigation.
	 */
	protected SSDBComboBox comboBox;

	/**
	 * Listener on combo box to detect combo-based navigations.
	 */
	private final SyncComboListener comboListener = new SyncComboListener();

	/**
	 * SSDataNavigator to be synchronized with navigation combo box.
	 */
	protected SSDataNavigator dataNavigator;

	/**
	 * SSRowSet navigated with data navigator and combo box.
	 */
	protected SSRowSet rowset;

	/**
	 * Listener on SSRowSet to detect data navigator-based navigations.
	 */
	protected final SyncRowSetListener rowsetListener = new SyncRowSetListener();

	/**
	 * 
	 * Creates a SSSyncManager with the specified combo box and data navigator.
	 *
	 * @param _comboBox      SSDBComboBox used for record navigation
	 * @param _dataNavigator SSDataNavigator to be synchronized with navigation
	 *                       combo box
	 */
	public SSSyncManager(SSDBComboBox _comboBox, SSDataNavigator _dataNavigator) {
		this.comboBox = _comboBox;
		this.dataNavigator = _dataNavigator;
		this.rowset = this.dataNavigator.getSSRowSet();
	}

	/**
	 * Adds listeners to combobox & rowset.
	 */
	private void addListeners() {
		this.comboBox.addActionListener(this.comboListener);
		this.dataNavigator.getSSRowSet().addRowSetListener(this.rowsetListener);
	}

	/**
	 * 
	 * Method to update combo box based on rowset.
	 */
	protected void adjustValue() {
		comboBox.removeActionListener(comboListener);
		try {
			if (rowset != null && rowset.getRow() > 0) {
				// GET THE PRIMARY KEY FOR THE CURRENT RECORD IN THE ROWSET
				long currentRowPK = this.rowset.getLong(this.columnName);
				
System.out.println("RowSet value: " + currentRowPK);				

				// CHECK IF THE COMBO BOX IS DISPLAYING THE SAME ONE.
				if (comboBox.getSelectedStringValue() == null
						|| comboBox.getSelectedValue()!=currentRowPK) {
					// IF NOT CHANGE THE SELECTION OF THE COMBO BOX.
					// this.comboBox.setSelectedStringValue(this.rowset.getString(this.columnName));
					this.comboBox.setSelectedValue(currentRowPK);
				}
			} else {
				this.comboBox.setSelectedIndex(-1);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		this.comboBox.addActionListener(this.comboListener);

// THIS CODE IS FROM SSCommon.getBoundColumnText()
//		String value = "";
//
//		try {
//			if (getSSRowSet().getRow() != 0) {
//				value = getSSRowSet().getColumnText(getBoundColumnName());
//				if (!getAllowNull() && value == null) {
//					value = "";
//				}
//			}
//		} catch (SQLException se) {
//			se.printStackTrace();
//		}
//
//		return value;

	}

	/**
	 * Stop synchronization between navigation components.
	 */
	public void async() {
		removeListeners();
	}

	/**
	 * Removes listeners from combo box & rowset.
	 */
	private void removeListeners() {
		this.comboBox.removeActionListener(this.comboListener);
		this.dataNavigator.getSSRowSet().removeRowSetListener(this.rowsetListener);
	}

	/**
	 * Sets column to be used as basis for synchronization.
	 *
	 * @param _columnName SSRowSet column used as basis for synchronization.
	 */
	public void setColumnName(String _columnName) {
		this.columnName = _columnName;
	}

	/**
	 * Sets combo box to be synchronized.
	 *
	 * @param _comboBox combo box to be synchronized
	 */

	public void setComboBox(SSDBComboBox _comboBox) {
		this.comboBox = _comboBox;
	}

	/**
	 * Sets data navigator to be synchronized.
	 *
	 * @param _dataNavigator data navigator to be synchronized
	 */
	public void setDataNavigator(SSDataNavigator _dataNavigator) {
		this.dataNavigator = _dataNavigator;
		this.rowset = this.dataNavigator.getSSRowSet();
	}

	/**
	 * Start synchronization between navigation components.
	 */
	public void sync() {
		addListeners();
		adjustValue();
	}

} // end public class SSSyncManager {

/*
 * $Log$ Revision 1.7 2012/08/10 14:41:01 prasanth Modified for compatibility
 * with filterable db combobox.
 *
 * Revision 1.6 2005/02/22 16:09:46 prasanth In adjustValue while checking combo
 * selection make sure the underlying value returned is not null. If the
 * selected item is -1 the getSelectedStringValue will return null.
 *
 * Revision 1.5 2005/02/10 16:51:23 prasanth On rowset events checking if the
 * combo box is displaying the right value or not. Changing the selection only
 * if it is not displaying the right one.
 *
 * Revision 1.4 2005/02/10 03:36:08 yoda2 JavaDoc cleanup and updated to support
 * string columns used for synchronization (to match recent changes in
 * SSDBComboBox).
 *
 * Revision 1.3 2005/02/05 15:06:56 yoda2 Got rid of depreciated calls.
 *
 * Revision 1.2 2005/02/04 22:49:15 yoda2 API cleanup & updated Copyright info.
 *
 * Revision 1.1 2005/01/03 19:53:43 prasanth Initial Commit.
 *
 */
