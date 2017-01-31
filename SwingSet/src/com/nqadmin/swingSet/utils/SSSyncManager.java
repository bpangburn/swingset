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
 * Used to synchronize a data navigator and a navigation combo box.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
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
    private final MyRowSetListener rowsetListener = new MyRowSetListener();

    /**

     * Creates a SSSyncManager with the specified combo box and data navigator.
     *
     * @param _comboBox SSDBComboBox used for record navigation
     * @param _dataNavigator    SSDataNavigator to be synchronized
     * with navigation combo box
     */
    public SSSyncManager(SSDBComboBox _comboBox, SSDataNavigator _dataNavigator) {
        comboBox = _comboBox;
        dataNavigator = _dataNavigator;
        rowset = dataNavigator.getSSRowSet();
    }

    /**
     * Sets column to be used as basis for synchronization.
     *
     * @param _columnName   SSRowSet column used as basis for synchronization.
     */
    public void setColumnName(String _columnName) {
        columnName = _columnName;
    }

    /**
     * Sets data navigator to be synchronized.
     *
     * @param _dataNavigator    data navigator to be synchronized
     */
    public void setDataNavigator(SSDataNavigator _dataNavigator) {
        dataNavigator = _dataNavigator;
        rowset = dataNavigator.getSSRowSet();
    }

    /**
     * Sets combo box to be synchronized.
     *
     * @param _comboBox combo box to be synchronized
     */

    public void setComboBox(SSDBComboBox _comboBox) {
        comboBox = _comboBox;
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
        comboBox.addActionListener(comboListener);
        dataNavigator.getSSRowSet().addRowSetListener(rowsetListener);
    }

    /**
     * Removes listeners from combo box & rowset.
     */
    private void removeListeners() {
        comboBox.removeActionListener(comboListener);
        dataNavigator.getSSRowSet().removeRowSetListener(rowsetListener);
    }

    /**
     *  Listener for rowset.
     */
    private class MyRowSetListener implements RowSetListener {

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

    /**

     * Method to update combo box based on rowset.
     */
    protected void adjustValue() {
        comboBox.removeActionListener(comboListener);
        try{
            if(rowset != null & rowset.getRow() > 0){
            // GET THE CURRENT VALUE FROM THE ROWSET.    
                String currentRowValue = rowset.getString(columnName);
            // CHECK IF THE COMBO BOX IS DISPLAYING THE SAME ONE.    
                if(comboBox.getSelectedStringValue() == null || !comboBox.getSelectedStringValue().equals(currentRowValue)){
                // IF NOT CHANGE THE SELECTION OF THE COMBO BOX.    
                    comboBox.setSelectedStringValue(rowset.getString(columnName));
                }
            } else {
                comboBox.setSelectedIndex(-1);
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        comboBox.addActionListener(comboListener);
    }


    /**
     *Listener for combo box to update data navigator when
     * combo box-based navigation occurs.
     */
    private class MyComboListener implements ActionListener {
        //protected long id = -1;
        protected String id = "";

    // WHEN THERE IS A CHANGE IN THIS VALUE MOVE THE ROWSET SO THAT
    // ITS POSITIONED AT THE RIGHT RECORD.
        public void actionPerformed(ActionEvent ae) {
        	try {
        		// IF THIS IS NOT CAUSED BY THE USER ACTION (IN WHICH THE FOCUS WILL BE ON THE COMBO) NOTHING TO DO
                if(rowset == null || rowset.getRow() < 1 || comboBox.getSelectedIndex() == -1 || comboBox.textField == null || !comboBox.hasFocus()){
                     return;
                }
                id = ""+comboBox.getSelectedFilteredValue();
                rowset.removeRowSetListener(rowsetListener);
                
            // UPDATE THE PRESENT ROW BEFORE MOVING TO ANOTHER ROW. *take out to make it faster
               // dataNavigator.updatePresentRow();
                //if(id != rowset.getLong(columnName)) {
                if(!id.equals(rowset.getString(columnName))) {
                	long indexOfId = comboBox.itemMap.get(id) + 1;
                  	int index = (int)indexOfId;
                    rowset.absolute(index);
                    int numRecords = comboBox.getItemCount();
                    int count = 0;
                    //while (id != rowset.getLong(columnName)) {
                    while (!id.equals(rowset.getString(columnName))) {
                        if (!rowset.next()) {
                            rowset.beforeFirst();
                            rowset.next();
                        }

                        count++;

                        //number of items in combo is the number of records in resultset.
                        //so if for some reason item is in combo but deleted in rowset
                        //To avoid infinite loop in such scenario
                        if (count > numRecords + 5) {
                        	comboBox.repaint();
                            //JOptionPane.showInternalMessageDialog(this,"Record deleted. Info the admin about this","Row not found",JOptionPane.OK_OPTION);
                            break;
                        }
                    }
                }
                rowset.addRowSetListener(rowsetListener);

            } catch(SQLException se) {
                se.printStackTrace();
            }
        }
    } // private class MyComboListener implements ActionListener {

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