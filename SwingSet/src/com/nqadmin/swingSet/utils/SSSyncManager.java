/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2005, The Pangburn Company and Prasanth R. Pasala.
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

 package com.nqadmin.swingSet.utils;

 import com.nqadmin.swingSet.datasources.*;
 import java.sql.SQLException;
 import com.nqadmin.swingSet.SSDBComboBox;
 import com.nqadmin.swingSet.SSDataNavigator;
 import javax.sql.RowSetListener;
 import java.awt.event.ActionListener;
 import java.awt.event.ActionEvent;
 import javax.sql.RowSetEvent;

/**
 * SSSyncManager.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Used to synchronize the data navigator and combo box.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */

 	public class SSSyncManager{

 		protected SSDBComboBox comboBox;
 		protected SSDataNavigator dataNavigator;
 		protected SSRowSet rowset;
 		protected String columnName;

 		protected MyComboListener comboListener = new MyComboListener();
 		protected MyRowSetListener rowsetListener = new MyRowSetListener();

 		public SSSyncManager(SSDBComboBox comboBox, SSDataNavigator dataNavigator){
 			this.comboBox = comboBox;
 			this.dataNavigator = dataNavigator;
 			rowset = dataNavigator.getRowSet();
 		}

 		public void setColumnName(String columnName){
 			this.columnName = columnName;
 		}

 		public void setDataNavigator(SSDataNavigator dataNavigator){
 			this.dataNavigator = dataNavigator;
 			rowset = dataNavigator.getRowSet();
 		}

 		public void setComboBox(SSDBComboBox comboBox){
 			this.comboBox = comboBox;
 		}

 		public void sync(){
 			addListeners();
 			adjustValue();
 		}

 		public void async(){
 			removeListeners();
 		}

 		private void addListeners(){
 			comboBox.getComboBox().addActionListener(comboListener);
 			dataNavigator.getRowSet().addRowSetListener(rowsetListener);
 		}

 		private void removeListeners(){
 			comboBox.getComboBox().removeActionListener(comboListener);
 			dataNavigator.getRowSet().removeRowSetListener(rowsetListener);
 		}


	 	/**
		 *	Listener for column name.
		 */
		public class MyRowSetListener implements RowSetListener{

			public void cursorMoved(RowSetEvent rse) {
				adjustValue();
			}

			public void rowChanged(RowSetEvent rse) {
				adjustValue();
			}

			public void rowSetChanged(RowSetEvent rse) {
				adjustValue();
			}

		}

		protected void adjustValue(){
			comboBox.getComboBox().removeActionListener(comboListener);
			try{
				if(rowset != null & rowset.getRow() > 0){
					comboBox.setSelectedValue(rowset.getLong(columnName));
				}
				else{
					comboBox.getComboBox().setSelectedIndex(-1);
				}
			}catch(SQLException se){
				se.printStackTrace();
			}
			comboBox.getComboBox().addActionListener(comboListener);
		}


		/**
		 *	Listener for ComboBox
		 */
		private class MyComboListener implements ActionListener {
			protected long id = -1;

		// WHEN THERE IS A CHANGE IN THIS VALUE MOVE THE ROWSET SO THAT
		// ITS POSITIONED AT THE RIGHT RECORD.
			public void actionPerformed(ActionEvent ae) {
				try {

					if(rowset == null || rowset.getRow() < 1 || comboBox.getComboBox().getSelectedIndex() == -1){
						return;
					}


					id = comboBox.getSelectedValue();
					rowset.removeRowSetListener(rowsetListener);

				// UPDATE THE PRESENT ROW BEFORE MOVING TO ANOTHER ROW.
					dataNavigator.updatePresentRow();

					if(id != rowset.getLong(columnName) ){

						int index = comboBox.getComboBox().getSelectedIndex() + 1;
						rowset.absolute(index);
						int numRecords = comboBox.getComboBox().getItemCount();
						int count = 0;
						while(id != rowset.getLong(columnName) ) {
							if( !rowset.next()) {
								rowset.beforeFirst();
								rowset.next();
							}
								count++;
							//number of items in combo is the number of records in resultset.
							//so if for some reason item is in combo but deleted in rowset
							//To avoid infinite loop in such scenario
							if(count > numRecords + 5){
								//JOptionPane.showInternalMessageDialog(this,"Record deleted. Info the admin about this","Row not found",JOptionPane.OK_OPTION);
								break;
							}
						}
					}
					rowset.addRowSetListener(rowsetListener);

				}catch(SQLException se){
					se.printStackTrace();
				}
			}
		} // END OF COMBO LISTENER

 	} // END OF SYNC MANAGER

/*
 * $Log$
 * Revision 1.1  2005/01/03 19:53:43  prasanth
 * Initial Commit.
 *
 */