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
     * SSDBComboBox used for record navigation.
     */
    protected SSDBComboBox comboBox;

    /**
     * SSDataNavigator to be synchronized with navigation combo box.
     */
    protected SSDataNavigator dataNavigator;

    /**
     * SSRowSet navigated with data navigator and combo box.
     */
    protected SSRowSet rowset;

    /**
     * SSRowSet column used as basis for synchronization.
     */
    protected String columnName;

    /**
     * Listener on combo box to detect combo-based navigations.
     */
    private final MyComboListener comboListener = new MyComboListener();

    /**
     * Listener on SSRowSet to detect data navigator-based navigations.
     */
    protected final MyRowSetListener rowsetListener = new MyRowSetListener();

    /**

     * Creates a SSSyncManager with the specified combo box and data navigator.
     *
     * @param _comboBox SSDBComboBox used for record navigation
     * @param _dataNavigator    SSDataNavigator to be synchronized
     * with navigation combo box
     */
    public SSSyncManager(SSDBComboBox _comboBox, SSDataNavigator _dataNavigator) {
        this.comboBox = _comboBox;
        this.dataNavigator = _dataNavigator;
        this.rowset = this.dataNavigator.getSSRowSet();
    }

    /**
     * Sets column to be used as basis for synchronization.
     *
     * @param _columnName   SSRowSet column used as basis for synchronization.
     */
    public void setColumnName(String _columnName) {
        this.columnName = _columnName;
    }

    /**
     * Sets data navigator to be synchronized.
     *
     * @param _dataNavigator    data navigator to be synchronized
     */
    public void setDataNavigator(SSDataNavigator _dataNavigator) {
        this.dataNavigator = _dataNavigator;
        this.rowset = this.dataNavigator.getSSRowSet();
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
     * Start synchronization between navigation components.
     */
    public void sync() {
        addListeners();
        adjustValue();
    }

    /**
     * Stop synchronization between navigation components.
     */
    public void async() {
        removeListeners();
    }

    /**
     * Adds listeners to combo box & rowset.
     */
    private void addListeners() {
        this.comboBox.addActionListener(this.comboListener);
        this.dataNavigator.getSSRowSet().addRowSetListener(this.rowsetListener);
    }

    /**
     * Removes listeners from combo box & rowset.
     */
    private void removeListeners() {
        this.comboBox.removeActionListener(this.comboListener);
        this.dataNavigator.getSSRowSet().removeRowSetListener(this.rowsetListener);
    }

    /**
     *  Listener for rowset.
     */
    protected class MyRowSetListener implements RowSetListener {

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

     * Method to update combo box based on rowset.
     */
    protected void adjustValue() {
        this.comboBox.removeActionListener(this.comboListener);
        try{
            if(this.rowset != null && this.rowset.getRow() > 0){
            // GET THE CURRENT VALUE FROM THE ROWSET.    
                String currentRowValue = this.rowset.getString(this.columnName);
            // CHECK IF THE COMBO BOX IS DISPLAYING THE SAME ONE.    
                if(this.comboBox.getSelectedStringValue() == null || !this.comboBox.getSelectedStringValue().equals(currentRowValue)){
                // IF NOT CHANGE THE SELECTION OF THE COMBO BOX.    
                    this.comboBox.setSelectedStringValue(this.rowset.getString(this.columnName));
                }
            } else {
                this.comboBox.setSelectedIndex(-1);
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        this.comboBox.addActionListener(this.comboListener);
    }


    /**
     *Listener for combo box to update data navigator when
     * combo box-based navigation occurs.
     */
    protected class MyComboListener implements ActionListener {
        //protected long id = -1;
        protected String id = "";

    // WHEN THERE IS A CHANGE IN THIS VALUE MOVE THE ROWSET SO THAT
    // ITS POSITIONED AT THE RIGHT RECORD.
        @Override
		public void actionPerformed(ActionEvent ae) {
        	try {
        		// IF THIS IS NOT CAUSED BY THE USER ACTION (IN WHICH THE FOCUS WILL BE ON THE COMBO) NOTHING TO DO
                if(SSSyncManager.this.rowset == null || SSSyncManager.this.rowset.getRow() < 1 || SSSyncManager.this.comboBox.getSelectedIndex() == -1 || SSSyncManager.this.comboBox.textField == null || !SSSyncManager.this.comboBox.hasFocus()){
                     return;
                }
                this.id = ""+SSSyncManager.this.comboBox.getSelectedFilteredValue();
                SSSyncManager.this.rowset.removeRowSetListener(SSSyncManager.this.rowsetListener);
                
            // UPDATE THE PRESENT ROW BEFORE MOVING TO ANOTHER ROW. *take out to make it faster
               // dataNavigator.updatePresentRow();
                //if(id != rowset.getLong(columnName)) {
                if(!this.id.equals(SSSyncManager.this.rowset.getString(SSSyncManager.this.columnName))) {
					long indexOfId = SSSyncManager.this.comboBox.itemMap.get(this.id) + 1;
                  	int index = (int)indexOfId;
                    SSSyncManager.this.rowset.absolute(index);
                    int numRecords = SSSyncManager.this.comboBox.getItemCount();
                    int count = 0;
                    //while (id != rowset.getLong(columnName)) {
                    while (!this.id.equals(SSSyncManager.this.rowset.getString(SSSyncManager.this.columnName))) {
                        if (!SSSyncManager.this.rowset.next()) {
                            SSSyncManager.this.rowset.beforeFirst();
                            SSSyncManager.this.rowset.next();
                        }

                        count++;

                        //number of items in combo is the number of records in resultset.
                        //so if for some reason item is in combo but deleted in rowset
                        //To avoid infinite loop in such scenario
                        if (count > numRecords + 5) {
                        	SSSyncManager.this.comboBox.repaint();
                            //JOptionPane.showInternalMessageDialog(this,"Record deleted. Info the admin about this","Row not found",JOptionPane.OK_OPTION);
                            break;
                        }
                    }
                }
                SSSyncManager.this.rowset.addRowSetListener(SSSyncManager.this.rowsetListener);

            } catch(SQLException se) {
                se.printStackTrace();
            }
        }
    } // protected class MyComboListener implements ActionListener {

} // end public class SSSyncManager {

/*
 * $Log$
 * Revision 1.7  2012/08/10 14:41:01  prasanth
 * Modified for compatibility with filterable db combobox.
 *
 * Revision 1.6  2005/02/22 16:09:46  prasanth
 * In adjustValue while checking combo selection make sure the underlying value returned
 * is not null. If the selected item is -1 the getSelectedStringValue will return null.
 *
 * Revision 1.5  2005/02/10 16:51:23  prasanth
 * On rowset events checking if the combo box is displaying the right value
 * or not. Changing the selection only if it is not displaying the right one.
 *
 * Revision 1.4  2005/02/10 03:36:08  yoda2
 * JavaDoc cleanup and updated to support string columns used for synchronization (to match recent changes in SSDBComboBox).
 *
 * Revision 1.3  2005/02/05 15:06:56  yoda2
 * Got rid of depreciated calls.
 *
 * Revision 1.2  2005/02/04 22:49:15  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.1  2005/01/03 19:53:43  prasanth
 * Initial Commit.
 *
 */
