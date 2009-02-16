/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2006, The Pangburn Company and Prasanth R. Pasala.
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

package com.nqadmin.swingSet;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.nqadmin.swingSet.datasources.SSRowSet;

/**
 * SSDataNavigator.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Component that can be used for data navigation. It provides buttons for
 * navigation, insertion, and deletion of records in a SSRowSet. The modification
 * of a SSRowSet can be prevented using the setModificaton() method.  Any changes
 * made to the columns of a record will be updated whenever there is a
 * navigation.
 *
 * For example if you are displaying three columns using the JTextField and the
 * user changes the text in the text fields then the columns will be updated to
 * the new values when the user navigates the SSRowSet. If the user wants to revert
 * the changes he made he can press the Undo button, however this must be done
 * before any navigation.  Once navigation takes place changes can't be reverted
 * using Undo button (has to be done manually by the user).
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
public class SSDataNavigator extends JPanel {

    /**
     * Button to navigate to the first record in the SSRowSet.
     */
    protected JButton firstButton = new JButton();

    /**
     * Button to navigate to the previous record in the SSRowSet.
     */
    protected JButton previousButton = new JButton();

    /**
     * Text field for viewing/changing the current record number.
     */
    protected JTextField txtCurrentRow = new JTextField();

    /**
     * Button to navigate to the next record in the SSRowSet.
     */
    protected JButton nextButton = new JButton();

    /**
     * Button to navigate to the last record in the SSRowSet.
     */
    protected JButton lastButton = new JButton();

    /**
     * Button to commit screen changes to the SSRowSet.
     */
    protected JButton commitButton = new JButton(); // Commit button

    /**
     * Button to revert screen changes based on the SSRowSet.
     */
    protected JButton undoButton = new JButton();

    /**
     * Button to refresh the screen based on any changes to the SSRowSet.
     */
    protected JButton refreshButton = new JButton(); // REFRESH BUTTON

    /**
     * Button to add a record to the SSRowSet.
     */
    protected JButton addButton = new JButton();

    /**
     * Button to delete the current record in the SSRowSet.
     */
    protected JButton deleteButton = new JButton();

    /**
     * Label to display the total number of records in the SSRowSet.
     */
    protected JLabel lblRowCount = new JLabel();

    /**
     * Indicator to allow/disallow changes to the SSRowSet.
     */
    protected boolean modification = true;

    /**
     * Indicator to allow/disallow deletions from the SSRowSet.
     */
    protected boolean deletion = true;

    /**
     * Indicator to allow/disallow insertions to the SSRowSet.
     */
    protected boolean insertion = true;

    /**
     * Indicator to force confirmation of SSRowSet deletions.
     */
    protected boolean confirmDeletes = true;

    /**
     * Indicator to cause the navigator to skip the execute() function call on
     * the specified SSRowSet.  Must be false for MySQL (see FAQ).
     */
    protected boolean callExecute = true;

    /**
     * SSRowSet from which component will get/set values.
     */
    protected SSRowSet sSRowSet = null;

    /**
     * Container (frame or internal frame) which contains the navigator.
     */
    protected SSDBNav dBNav = null;

    /**
     * Number of rows in SSRowSet.  Set to zero if next() method returns false.
     */
    protected int rowCount = 0;

    /**
     * Row number for current record in SSRowSet.
     */
    protected int currentRow = 0;

    /**
     * Indicator used to determine if a row is being inserted into the SSRowSet.
     */
    protected boolean onInsertRow = false;

    /**
     * Navigator button dimensions.
     */
    protected Dimension buttonSize = new Dimension(40, 20);

    /**
     * Current record text field dimensions.
     */
    protected Dimension txtFieldSize = new Dimension(65, 20);

    /**
     * Listener on the SSRowSet used by data navigator.
     */
    private final SSDBNavRowSetListener sSRowSetListener = new SSDBNavRowSetListener();

    /**
     * Creates a object of SSDataNavigator.
     * Note: you have to set the SSRowSet before you can start using it.
     */
    public SSDataNavigator() {
        addToolTips();
        createPanel();
        addListeners();
    }

    /**
     * Constructs a SSDataNavigator for the given SSRowSet
     *
     * @param _sSRowSet The SSRowSet to which the SSDataNavigator has to be bound
     */
    public SSDataNavigator(SSRowSet _sSRowSet) {
        setSSRowSet(_sSRowSet);
        addToolTips();
        createPanel();
        addListeners();
    }

    /**
     * Constructs the SSDataNavigator with the given SSRowSet and sets the size of the buttons
     * on the navigator to the given size
     *
     * @param _sSRowSet    the SSRowSet to which the navigator is bound to
     * @param _buttonSize    the size to which the button on navigator have to be set
     */
    public SSDataNavigator(SSRowSet _sSRowSet, Dimension _buttonSize) {
        buttonSize = _buttonSize;
        setSSRowSet(_sSRowSet);
        addToolTips();
        createPanel();
        addListeners();
    }

    /**
     * Returns true if the SSRowSet contains one or more rows, else false.
     *
     * @return return true if SSRowSet contains data else false.
     */
     public boolean containsRows() {

         if ( rowCount == 0 ) {
            return false;
         }

         return true;
     }

    /**
     * Method to cause the navigator to skip the execute() function call on the
     * underlying SSRowSet. This is necessary for MySQL (see FAQ).
     *
     * @param _callExecute    false if using MySQL database - otherwise true
     */
    public void setCallExecute(boolean _callExecute) {
        boolean oldValue = callExecute;
        callExecute = _callExecute;
        firePropertyChange("callExecute", oldValue, callExecute);
    }

    /**
     * Indicates if the navigator will skip the execute function call on the
     * underlying SSRowSet (needed for MySQL - see FAQ).
     *
     * @return value of execute() indicator
     */
    public boolean getCallExecute() {
        return callExecute;
    }

    /**
     * Sets the preferredSize and the MinimumSize of the buttons to the specified size
     *
     * @param _buttonSize    the required dimension of the buttons
     */
    public void setButtonSize(Dimension _buttonSize) {
        Dimension oldValue = buttonSize;
        buttonSize = _buttonSize;
        firePropertyChange("buttonSize", oldValue, buttonSize);
        setButtonSizes();
    }

    /**
     * Returns the size of buttons on the data navigator.
     *
     * @return returns a Dimension object representing the size of each button
     *    on the data navigator.
     */
    public Dimension getButtonSize() {
        return buttonSize;
    }

    /**
     * Function that passes the implementation of the SSDBNav interface.  This
     * interface can be implemented by the developer to perform custom actions
     * when the insert button is pressed
     *
     * @param _dBNav    implementation of the SSDBNav interface
     */
    public void setDBNav(SSDBNav _dBNav) {
        SSDBNav oldValue = dBNav;
        dBNav = _dBNav;
        firePropertyChange("dBNav", oldValue, dBNav);
    }

    /**
     * Returns any custom implementation of the SSDBNav interface, which is used
     * when the insert button is pressed to perform custom actions.
     *
     * @return any custom implementation of the SSDBNav interface
     */
    public SSDBNav getDBNav() {
        return dBNav;
    }

    /**
     * Enables or disables the modification-related buttons on the SSDataNavigator.
     * If the user can only navigate through the records with out making any changes
     * set this to false.  By default, the modification-related buttons are enabled.
     *
     * @param _modification    indicates whether or not the modification-related
     *  buttons are enabled.
     */
    public void setModification(boolean _modification) {
        boolean oldValue = modification;
        modification = _modification;
        firePropertyChange("modification", oldValue, modification);

        if (!modification) {
            commitButton.setEnabled(false);
            undoButton.setEnabled(false);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
        } else {
            commitButton.setEnabled(true);
            undoButton.setEnabled(true);
            addButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }
    }

    /**
     * Returns true if the user can modify the data in the SSRowSet, else false.
     *
     * @return returns true if the user modifications are written back to the
     *  database, else false.
     */
    public  boolean getModification() {
        return modification;
    }

    /**
     * Enables or disables the row deletion button. This method should be used
     * if row deletions are not allowed.  True by default.
     *
     * @param _deletion    indicates whether or not to allow deletions
     */
    public void setDeletion(boolean _deletion) {
        boolean oldValue = deletion;
        deletion = _deletion;
        firePropertyChange("deletion", oldValue, deletion);

        if (!deletion) {
            deleteButton.setEnabled(false);
        } else {
            deleteButton.setEnabled(true);
        }
    }

    /**
     * Returns true if deletions are allowed, else false.
     *
     * @return returns true if deletions are allowed, else false.
     */
    public boolean getDeletion(){
        return deletion;
    }

    /**
     * Enables or disables the row insertion button.  This method should be used
     * if row insertions are not allowed.  True by default.
     *
     * @param _insertion    indicates whether or not to allow insertions
     */
    public void setInsertion(boolean _insertion) {
        boolean oldValue = insertion;
        insertion = _insertion;
        firePropertyChange("insertion", oldValue, insertion);

        if (!insertion) {
            addButton.setEnabled(false);
        } else{
            addButton.setEnabled(true);
        }
    }

    /**
     * Returns true if insertions are allowed, else false.
     *
     * @return returns true if insertions are allowed, else false.
     */
    public boolean getInsertion() {
        return insertion;
    }

    /**
     * Sets the confirm deletion indicator. If set to true, every time delete
     * button is pressed, the navigator pops up a confirmation dialog to the
     * user. Default value is true.
     *
     * @param _confirmDeletes    indicates whether or not to confirm deletions
     */
    public void setConfirmDeletes(boolean _confirmDeletes) {
        boolean oldValue = confirmDeletes;
        confirmDeletes = _confirmDeletes;
        firePropertyChange("confirmDeletes", oldValue, confirmDeletes);
    }

    /**
     * Returns true if deletions must be confirmed by user, else false.
     *
     * @return returns true if a confirmation dialog is displayed when the user
     *    deletes a record, else false.
     */
    public boolean getConfirmDeletes() {
        return confirmDeletes;
    }

    /**
     * This method changes the SSRowSet to which the navigator is bound.
     * The execute() and next() methods MUST be called on the SSRowSet
     * before you set the SSRowSet for the SSDataNavigator.
     *
     * @param _sSRowSet    a SSRowSet object to which the navigator will be bound
     */
    public void setSSRowSet(SSRowSet _sSRowSet) {
        if(sSRowSet != null){
            sSRowSet.removeRowSetListener(sSRowSetListener);
        }

        SSRowSet oldValue = sSRowSet;
        sSRowSet = _sSRowSet;
        firePropertyChange("sSRowSet", oldValue, sSRowSet);

        // SEE IF THERE ARE ANY ROWS IN THE GIVEN SSROWSET
        try {
            if (callExecute) {
                sSRowSet.execute();
            }

            if (!sSRowSet.next()) {
                rowCount = 0;
                currentRow = 0;
            } else {
            // IF THERE ARE ROWS GET THE ROW COUNT
                sSRowSet.last();
                rowCount = sSRowSet.getRow();
                sSRowSet.first();
                currentRow = sSRowSet.getRow();
            }
        // SET THE ROW COUNT AS LABEL
            lblRowCount.setText("of " + rowCount);
            txtCurrentRow.setText(String.valueOf(currentRow));

            sSRowSet.addRowSetListener(sSRowSetListener);
        } catch(SQLException se) {
            se.printStackTrace();
        }

        //IF NO ROWS ARE PRESENT DISABLE NAVIGATION
        //ELSE ENABLE THEN ELSE IS USEFUL WHEN THE SSROWSET IS CHNAGED
        // IF THE INITIAL SSROWSET HAS ZERO ROWS NEXT IF THE USER SETS A NEW SSROWSET THEN THE
        // BUTTONS HAVE TO BE ENABLED
        if (rowCount == 0) {
            firstButton.setEnabled(false);
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
            lastButton.setEnabled(false);
        } else{
            firstButton.setEnabled(true);
            previousButton.setEnabled(true);
            nextButton.setEnabled(true);
            lastButton.setEnabled(true);
        }

        try {
            if (sSRowSet.isLast()) {
                nextButton.setEnabled(false);
                lastButton.setEnabled(false);
            }
            if (sSRowSet.isFirst()) {
                firstButton.setEnabled(false);
                previousButton.setEnabled(false);
            }

        } catch(SQLException se) {
            se.printStackTrace();
        }

    // ENABLE OTHER BUTTONS IF NEED BE.

        // THIS IS NEEDED TO HANDLE USER LEAVING THE SCREEN IN AN INCONSISTENT
        // STATE EXAMPLE: USER CLICKS ADD BUTTON, THIS DISABLES ALL THE BUTTONS
        // EXCEPT COMMIT & UNDO. WITH OUT COMMITING OR UNDOING THE ADD USER
        // CLOSES THE SCREEN. NOW IF THE SCREEN IS OPENED WITH A NEW SSROWSET.
        // THE REFRESH, ADD & DELETE WILL BE DISABLED.
        refreshButton.setEnabled(true);
        if (insertion) {
            addButton.setEnabled(true);
        }
        if (deletion) {
            deleteButton.setEnabled(true);
        }
    }

    /**
     * Returns the SSRowSet being used.
     *
     * @return returns the SSRowSet being used.
     */
    public SSRowSet getSSRowSet() {
        return sSRowSet;
    }

    /**
     * Writes the present row back to the SSRowSet. This is done automatically
     * when any navigation takes place, but can also be called manually.
     *
     * @return returns true if update succeeds else false.
     */
    public boolean updatePresentRow() {
    	if(onInsertRow || currentRow > 0)
    		doCommitButtonClick();
    	
        return true;
    }

    /**
     *	Calls the doClick on First Button.
     */
    public void doFirstButtonClick(){
    	firstButton.doClick();
    }

    /**
     *	Calls the doClick on Previous Button.
     */
    public void doPreviousButtonClick(){
    	previousButton.doClick();
    }

    /**
     *	Calls the doClick on Next Button.
     */
    public void doNextButtonClick(){
    	nextButton.doClick();
    }

    /**
     *	Calls the doClick on Last Button.
     */
    public void doLastButtonClick(){
    	lastButton.doClick();
    }

    /**
     *	Calls the doClick on Refresh Button.
     */
    public void doRefreshButtonClick(){
    	refreshButton.doClick();
    }

    /**
     *	Calls the doClick on Commit Button.
     */
    public void doCommitButtonClick(){
    	commitButton.doClick();
    }

    /**
     *	Calls the doClick on Undo Button.
     */
    public void doUndoButtonClick(){
    	undoButton.doClick();
    }

    /**
     *	Calls the doClick on Add Button.
     */
    public void doAddButtonClick(){
    	addButton.doClick();
    }

    /**
     *	Calls the doClick on Delete Button.
     */
    public void doDeleteButtonClick(){
    	deleteButton.doClick();
    }

    /**
     * Method to add tooltips and button graphics (or text) to navigator components.
     */
    protected void addToolTips() {

        try {
            ClassLoader cl = this.getClass().getClassLoader();
            firstButton.setIcon(new ImageIcon(cl.getResource("images/first.gif")));
            previousButton.setIcon(new ImageIcon(cl.getResource("images/prev.gif")));
            nextButton.setIcon(new ImageIcon(cl.getResource("images/next.gif")));
            lastButton.setIcon(new ImageIcon(cl.getResource("images/last.gif")));
            commitButton.setIcon(new ImageIcon(cl.getResource("images/commit.gif")));
            undoButton.setIcon(new ImageIcon(cl.getResource("images/undo.gif")));
            refreshButton.setIcon(new ImageIcon(cl.getResource("images/refresh.gif")));
            addButton.setIcon(new ImageIcon(cl.getResource("images/add.gif")));
            deleteButton.setIcon(new ImageIcon(cl.getResource("images/delete.gif")));
        } catch(Exception e) {
            firstButton.setText("<<");
            previousButton.setText("<");
            nextButton.setText(">");
            lastButton.setText(">>");
            commitButton.setText("Commit");
            undoButton.setText("Undo");
            refreshButton.setText("Refresh");
            addButton.setText("Add");
            deleteButton.setText("Delete");
            System.out.println("Unable to load images for navigator buttons");
        }

    // SET TOOL TIPS FOR THE BUTTONS   
        firstButton.setToolTipText("First");
        previousButton.setToolTipText("Previous");
        nextButton.setToolTipText("Next");
        lastButton.setToolTipText("Last");
        commitButton.setToolTipText("Commit");
        undoButton.setToolTipText("Undo");
        refreshButton.setToolTipText("Refresh");
        addButton.setToolTipText("Add Record");
        deleteButton.setToolTipText("Delete Record");
       
    
    } // end protected void addToolTips() {
        
    /**
     *    This will make all the components in the navigator to either focusable
     *components or non focusable components.
     *Set to false if you don't want any of the buttons or text fields in the navigator to
     *receive the focus else true. The default value is true.
     *@param focusable - false if you don't want the navigator to receive focus else false.
     */    
    public void setFocusable(boolean focusable){
    // MAKE THE BUTTONS NON FOCUSABLE IF REQUESTED
        firstButton.setFocusable(focusable);
        previousButton.setFocusable(focusable);
        nextButton.setFocusable(focusable);
        lastButton.setFocusable(focusable);
        commitButton.setFocusable(focusable);
        undoButton.setFocusable(focusable);
        refreshButton.setFocusable(focusable);
        addButton.setFocusable(focusable);
        deleteButton.setFocusable(focusable);
        txtCurrentRow.setFocusable(focusable);
    }    

    /**
     * Sets the dimensions for the navigator components.
     */
    protected void setButtonSizes() {

        // SET THE PREFERRED SIZES
            firstButton.setPreferredSize(buttonSize);
            previousButton.setPreferredSize(buttonSize);
            nextButton.setPreferredSize(buttonSize);
            lastButton.setPreferredSize(buttonSize);
            commitButton.setPreferredSize(buttonSize);
            undoButton.setPreferredSize(buttonSize);
            refreshButton.setPreferredSize(buttonSize);
            addButton.setPreferredSize(buttonSize);
            deleteButton.setPreferredSize(buttonSize);
            txtCurrentRow.setPreferredSize(txtFieldSize);
            lblRowCount.setPreferredSize(txtFieldSize);
            lblRowCount.setHorizontalAlignment(SwingConstants.CENTER);

        // SET MINIMUM BUTTON SIZES
            firstButton.setMinimumSize(buttonSize);
            previousButton.setMinimumSize(buttonSize);
            nextButton.setMinimumSize(buttonSize);
            lastButton.setMinimumSize(buttonSize);
            commitButton.setMinimumSize(buttonSize);
            undoButton.setMinimumSize(buttonSize);
            refreshButton.setMinimumSize(buttonSize);
            addButton.setMinimumSize(buttonSize);
            deleteButton.setMinimumSize(buttonSize);
            txtCurrentRow.setMinimumSize(txtFieldSize);
            lblRowCount.setMinimumSize(txtFieldSize);
    }

    /**
     * Adds the navigator components to the navigator panel.
     */
    protected void createPanel() {

        setButtonSizes();
        //SET THE BOX LAYOUT
        setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS) );

        // ADD BUTTONS TO THE PANEL
        add(firstButton);
        add(previousButton);
        add(txtCurrentRow);
        add(nextButton);
        add(lastButton);
        add(commitButton);
        add(undoButton);
        add(refreshButton);
        add(addButton);
        add(deleteButton);
        add(lblRowCount);
        //pack();
    }

    /**
     * Adds the listeners for the navigator components.
     */
    private void addListeners() {

        // WHEN THIS BUTTON IS PRESSED THE RECORD ON WHICH USER WAS WORKING IS SAVED
        // AND MOVES THE SSROWSET TO THE FIRST ROW
        // SINCE ROW SET IS IN FIRST ROW DISABLE PREVIOUS BUTTON AND ENABLE NEXT BUTTON
            firstButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        if ( modification ) {
                        // CHECK IF THE DBNAV IS NULL IF SO UPDATE ROW	
                        	if(dBNav == null ){
                        		sSRowSet.updateRow();
                        	}
                        	else {
                        	// IF DBNAV IS NOT NULL CALL ALLOW UPDATE	
                        		if(dBNav.allowUpdate()){
                        		// UPDATE ALLOWED GO AHEAD AND DO THE UPDATE	
                        			sSRowSet.updateRow();
                        			dBNav.performPostUpdateOps();
                        		}
                        		else{
                        		// UPDATE NOT ALLOWED SO DO NOTHING.
                        		// WE SHOULD NOT MOVE TO THE ROW AS THE USER HAS MADE CHANGES TO
                        	    // TO THE ROW THAT SHOULD BE UNDONE.
                        			return;
                        		}
                        	}
                        }
                        sSRowSet.first();

                        firstButton.setEnabled(false);
                        previousButton.setEnabled(false);
                        if (!sSRowSet.isLast()) {
                            nextButton.setEnabled(true);
                            lastButton.setEnabled(true);
                        } else {
                            nextButton.setEnabled(false);
                            lastButton.setEnabled(false);
                        }
                        if ( dBNav != null ) {
                            dBNav.performNavigationOps(SSDBNav.NAVIGATION_FIRST);
                        }
                    // GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD
                        currentRow = 1;
                        txtCurrentRow.setText(String.valueOf(currentRow));
                    } catch(SQLException se) {
                        se.printStackTrace();
                        JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while updating row or moving the cursor.\n"+se.getMessage());
                    }
                }
            });

        // WHEN BUTTON 2 IS PRESSED THE CURRENT RECORD IS SAVED AND SSROWSET IS
        // MOVED TO PREVIOUS RECORD
        // CALLING PREVIOUS ON ENPTY SSROWSET IS ILLEGAL SO A CHECK IS MADE FOR THAT
        // IF NUMBER OF ROWS == 0 THEN SSROWSET IS EMPTY
            previousButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        //if( sSRowSet.rowUpdated() )
                        if ( modification ) {
                        // CHECK IF THE DBNAV IS NULL IF SO UPDATE ROW	
                        	if(dBNav == null ){
                        		sSRowSet.updateRow();
                        	}
                        	else {
                        	// IF DBNAV IS NOT NULL CALL ALLOW UPDATE	
                        		if(dBNav.allowUpdate()){
                        		// UPDATE ALLOWED GO AHEAD AND DO THE UPDATE	
                        			sSRowSet.updateRow();
                        			dBNav.performPostUpdateOps();
                        		}
                        		else{
                        		// UPDATE NOT ALLOWED SO DO NOTHING.
                        		// WE SHOULD NOT MOVE TO THE ROW AS THE USER HAS MADE CHANGES TO
                        	    // TO THE ROW THAT SHOULD BE UNDONE.
                        			return;
                        		}
                        	}
                        }
                        if ( sSRowSet.getRow() != 0 && !sSRowSet.previous() ) {
                            sSRowSet.first();
                        }
                        // IF IN THE FIRST RECORD DISABLE PREVIOUS BUTTON
                        if (sSRowSet.isFirst() || sSRowSet.getRow() == 0){
                            firstButton.setEnabled(false);
                            previousButton.setEnabled(false);
                        }

                        // IF NEXT BUTTON IS DISABLED ENABLE IT.
                        if ( !sSRowSet.isLast() ) {
                            nextButton.setEnabled(true);
                            lastButton.setEnabled(true);
                        }

                        if ( dBNav != null ) {
                            dBNav.performNavigationOps(SSDBNav.NAVIGATION_PREVIOUS);
                        }
                    // GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD
                        currentRow = sSRowSet.getRow();
                        txtCurrentRow.setText(String.valueOf(currentRow));
                    } catch(SQLException se) {
                        se.printStackTrace();
                        JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while updating row or moving the cursor.\n"+se.getMessage());
                    }
                }
            });

        // WHEN BUTTON 3 PRESSED THE CURRENT RECORD IS SAVED AND THE SSROWSET IS
        // MOVED TO NEXT RECORD. IF THIS IS THE LAST RECORD THEN BUTTON 3 IS DISABLED
        // ALSO IF THE PREVIOUS BUTTON IS NOT ENABLED THEN IT IS ENABLED
            nextButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        //if( sSRowSet.rowUpdated() )
                        if ( modification ) {
                        // CHECK IF THE DBNAV IS NULL IF SO UPDATE ROW	
                        	if(dBNav == null ){
                        		sSRowSet.updateRow();
                        	}
                        	else {
                        	// IF DBNAV IS NOT NULL CALL ALLOW UPDATE	
                        		if(dBNav.allowUpdate()){
                        		// UPDATE ALLOWED GO AHEAD AND DO THE UPDATE	
                        			sSRowSet.updateRow();
                        			dBNav.performPostUpdateOps();
                        		}
                        		else{
                        		// UPDATE NOT ALLOWED SO DO NOTHING.
                        		// WE SHOULD NOT MOVE TO THE ROW AS THE USER HAS MADE CHANGES TO
                        	    // TO THE ROW THAT SHOULD BE UNDONE.
                        			return;
                        		}
                        	}
                        }
                        if ( !sSRowSet.next() ) {
                            nextButton.setEnabled(false);
                            lastButton.setEnabled(false);
                            sSRowSet.last();
                        }
                        // IF LAST RECORD THEN DISABLE NEXT BUTTON
                        if ( sSRowSet.isLast() ) {
                            nextButton.setEnabled(false);
                            lastButton.setEnabled(false);
                        }

                        // IF THIS IS NOT FIRST ROW ENABLE FIRST AND PREVIOUS BUTTONS
                        if ( !sSRowSet.isFirst() ) {
                            previousButton.setEnabled(true);
                            firstButton.setEnabled(true);
                        }

                        if ( dBNav != null ) {
                            dBNav.performNavigationOps(SSDBNav.NAVIGATION_NEXT);
                        }
                    // GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD
                        currentRow = sSRowSet.getRow();
                        txtCurrentRow.setText(String.valueOf(currentRow));
                    } catch(SQLException se) {
                        se.printStackTrace();
                        JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while updating row or moving the cursor.\n"+se.getMessage());
                    }
                }
            });


        // BUTTON 4 ( "LAST" BUTTON )  CAUSED THE SSROWSET TO MOVE TO LAST RECORD.
        // BEFORE MOVING CURRENT RECORD IS SAVED
        // AFTER MOVING TO LAST RECORD THE NEXT BUTTON IS DIAABLED AND PREVIOUS BUTTON
        // ENABLED
            lastButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        //if( sSRowSet.rowUpdated() )
                        if ( modification ) {
                        // CHECK IF THE DBNAV IS NULL IF SO UPDATE ROW	
                        	if(dBNav == null ){
                        		sSRowSet.updateRow();
                        	}
                        	else {
                        	// IF DBNAV IS NOT NULL CALL ALLOW UPDATE	
                        		if(dBNav.allowUpdate()){
                        		// UPDATE ALLOWED GO AHEAD AND DO THE UPDATE	
                        			sSRowSet.updateRow();
                        			dBNav.performPostUpdateOps();
                        		}
                        		else{
                        		// UPDATE NOT ALLOWED SO DO NOTHING.
                        		// WE SHOULD NOT MOVE TO THE ROW AS THE USER HAS MADE CHANGES TO
                        	    // TO THE ROW THAT SHOULD BE UNDONE.
                        			return;
                        		}
                        	}
                        }
                        sSRowSet.last();

                        nextButton.setEnabled(false);
                        lastButton.setEnabled(false);
                        if (!sSRowSet.isFirst()) {
                            firstButton.setEnabled(true);
                            previousButton.setEnabled(true);
                        } else {
                            firstButton.setEnabled(false);
                            previousButton.setEnabled(false);
                        }
                        if ( dBNav != null ) {
                            dBNav.performNavigationOps(SSDBNav.NAVIGATION_LAST);
                        }
                    // GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD
                        currentRow = sSRowSet.getRow();
                        txtCurrentRow.setText(String.valueOf(currentRow));
                    } catch(SQLException se) {
                        se.printStackTrace();
                        JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while updating row or moving the cursor.\n"+se.getMessage());
                    }
                }
            });

        // THIS BUTTON INSERTS THE ROW AND MOVES TO THE NEWLY INSERTED ROW.
        // WHEN INSERT BUTTON IS PRESSED NAVIGATION WILL BE DISABLED SO THOSE HAVE TO BE
        // ENABLED HERE
            commitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        if (onInsertRow) {
                        // IF ON INSERT ROW ADD THE ROW.
                        // CHECK IF THE ROW CAN BE INSERTED.        
                            if(dBNav==null || (dBNav != null && dBNav.allowInsertion()) ){
                                sSRowSet.insertRow();
                                dBNav.performPostInsertOps();
                            // INCREMENT THE ROW COUNT
                                rowCount++;
 
                                sSRowSet.moveToCurrentRow();
                            // MOVE TO CURRENT ROW MOVES SSROWSET TO RECORD AT WHICH ADD WAS PRESSED.
                            // BUT IT NICE TO BE ON THE ADDED ROW WHICH IS THE LAST ONE IN THE SSROWSET.
                            // ALSO MOVE TO CURRENT ROW MOVES THE SSROWSET POSITION BUT DOES NOT TRIGGER
                            // ANY EVENT FOR THE LISTENERS AS A RESULT VALUES ON THE SCREEN WILL NOT
                            // DISPLAY THE CURRENT RECORD VALUES.
                                sSRowSet.last();
                            
                            // SET THE ROW COUNT AS LABEL
                                lblRowCount.setText("of " + rowCount);
                            // GET CURRENT ROW NUMBER
                                currentRow = sSRowSet.getRow();
                            // UPDATE THE TEXT FEILD
                                txtCurrentRow.setText(String.valueOf(currentRow));
                            
	                            onInsertRow = false;
	
	                            if (!sSRowSet.isFirst()) {
	                                firstButton.setEnabled(true);
	                                previousButton.setEnabled(true);
	                            }
	                            if (!sSRowSet.isLast()) {
	                                nextButton.setEnabled(true);
	                                lastButton.setEnabled(true);
	                            }
	                            refreshButton.setEnabled(true);
	
	                            if (insertion) {
	                                addButton.setEnabled(true);
	                            }
	                            if (deletion) {
	                                deleteButton.setEnabled(true);
	                            }
                            }
                            else {
                            	// WE DO NOTHING. THE ROWSET STAYS IN INSERT ROW. EITHER USER HAS TO FIX THE DATA AND SAVE THE ROW
                            	// OR CANCEL THE INSERTION. 
                            }
                            
                        } else {
                        // ELSE UPDATE THE PRESENT ROW VALUES.
                        // CHECK IF THE DBNAV IS NULL IF SO UPDATE ROW	
                        	if(dBNav == null ){
                        		sSRowSet.updateRow();
                        	}
                        	else {
                        	// IF DBNAV IS NOT NULL CALL ALLOW UPDATE	
                        		if(dBNav.allowUpdate()){
                        		// UPDATE ALLOWED GO AHEAD AND DO THE UPDATE	
                        			sSRowSet.updateRow();
                        			dBNav.performPostUpdateOps();
                        		}
                        		else{
                        		// UPDATE NOT ALLOWED SO DO NOTHING.
                        		// WE SHOULD NOT MOVE TO THE ROW AS THE USER HAS MADE CHANGES TO
                        	    // TO THE ROW THAT SHOULD BE UNDONE.
                        			return;
                        		}
                        	}
                        }

                    } catch(SQLException se) {
                        JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while inserting row.\n"+se.getMessage());
                        se.printStackTrace();
                    }
                }
            });

        // THIS BUTTON IS USED TO CANCEL THE CHANGES MADE TO THE RECORD.
        // IT CAN ALSO BE USED TO CANCEL INSERT ROW.
        // SO THE BUTTONS DISABLED AT THE INSERT BUTTON EVENT HAVE TO BE ENABLED
            undoButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                    // CALL MOVE TO CURRENT ROW IF ON INSERT ROW.    
                        if(onInsertRow){
                            sSRowSet.moveToCurrentRow();
                        }
                    // THIS FUNCTION IS NOT NEED IF ON INSERT ROW
                    // BUT MOVETOINSERTROW WILL NOT TRIGGER ANY EVENT SO FOR THE SCREEN
                    // TO UPDATE WE NEED TO TRIGGER SOME THING.
                    // SINCE USER IS MOVED TO CURRENT ROW PRIOR TO INSERT IT IS SAFE TO
                    // CALL CANCELROWUPDATE TO GET A TRIGGER    
                        sSRowSet.cancelRowUpdates();
                        onInsertRow = false;
                        if (dBNav != null) {
                            dBNav.performCancelOps();
                        }
                        //sSRowSet.deleteRow();
                        //sSRowSet.moveToCurrentRow();
                        firstButton.setEnabled(true);
                        previousButton.setEnabled(true);
                        nextButton.setEnabled(true);
                        lastButton.setEnabled(true);
                        refreshButton.setEnabled(true);
                        if (insertion) {
                            addButton.setEnabled(true);
                        }
                        if (deletion) {
                            deleteButton.setEnabled(true);
                        }
                        
                    // IF MOVED FROM INSERT ROW NEED TO UPDATE THE CURRENT ROW NUMBER.
                        int row = sSRowSet.getRow();
                        txtCurrentRow.setText(String.valueOf(currentRow));
                            
                    } catch(SQLException se) {
                        JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while undoing changes.\n"+se.getMessage());
                        se.printStackTrace();
                    }
                }
            });

        // REFETCH REFETCHES THE ROWS FROMS THE DATABASE AND MOVES THE CURSOR TO THE FIRST
        // RECORD IF THERE ARE NO RECORDS NAVIGATION BUTTONS ARE DIABLED
        // EVEN IS THERE ARE RECORDS PREVIOUS BUTTON IS DISABLED BECAUSE THE SSROWSET IS ON
        // THE FIRST ROW
            refreshButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    sSRowSet.removeRowSetListener(sSRowSetListener);
                    try {
                        if (callExecute) {
                            sSRowSet.execute();
                            if (!sSRowSet.next()) {
                                rowCount = 0;
                                currentRow = 0;
                                firstButton.setEnabled(false);
                                previousButton.setEnabled(false);
                                nextButton.setEnabled(false);
                                lastButton.setEnabled(false);

                            } else {
                            // IF THERE ARE ROWS GET THE ROW COUNT
                                sSRowSet.last();
                                rowCount = sSRowSet.getRow();
                                sSRowSet.first();
                                currentRow = sSRowSet.getRow();
                                firstButton.setEnabled(false);
                                previousButton.setEnabled(false);
                                nextButton.setEnabled(true);
                                lastButton.setEnabled(true);
                            }
                        // SET THE ROW COUNT AS LABEL
                            lblRowCount.setText("of " + rowCount);
                            txtCurrentRow.setText(String.valueOf(currentRow));
                        }

                        if ( dBNav != null ) {
                            dBNav.performRefreshOps();
                        }

                    } catch(SQLException se) {
                        se.printStackTrace();
                        JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured refreshing the data.\n"+se.getMessage());
                    }
                    sSRowSet.addRowSetListener(sSRowSetListener);
                }
            });

        // INSERT ROW BUTTON MOVES THE SSROWSET TO THE INSERT ROW POSITION
        // AT THIS TIME NAVIGATION HAS TO BE DISABLED
        // ONLY COMMIT AND CANCEL ARE ENABLED
            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        sSRowSet.moveToInsertRow();
                        onInsertRow = true;

                        if ( dBNav != null ) {
                            dBNav.performPreInsertOps();
                        }
                        //sSRowSet.updateString("client_name", "prasanh reddy");
                        //sSRowSet.insertRow();
                        //sSRowSet.moveToCurrentRow();
                        firstButton.setEnabled(false);
                        previousButton.setEnabled(false);
                        nextButton.setEnabled(false);
                        lastButton.setEnabled(false);
                        commitButton.setEnabled(true);
                        undoButton.setEnabled(true);
                        refreshButton.setEnabled(false);
                        addButton.setEnabled(false);
                        deleteButton.setEnabled(false);

                    } catch(SQLException se) {
                        JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while moving to insert row.\n"+se.getMessage());
                        se.printStackTrace();
                    }
                }
            });

        // DELETES THE CURRENT ROW AND MOVES TO NEXT ROW
        // IF THE DELETED ROW IS THE LAST ROW THEN MOVES TO LAST ROW IN SSROWSET
        // AFTER THE DELETION IS MADE (THATS THE PREVIOUS ROW TO THE DELETED ROW)
            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                    	if(confirmDeletes) {
                    		int answer = JOptionPane.showConfirmDialog(SSDataNavigator.this,"Are you sure you want to delete this record?","Delete Present Record", JOptionPane.YES_NO_OPTION);
                    		if ( answer != JOptionPane.YES_OPTION ) {
                    			return;
                    		}
                    	}

                        if ( dBNav != null ) {
                            dBNav.performPreDeletionOps();
                        }
                        
                        if ( dBNav ==null || (dBNav != null && dBNav.allowDeletion()) ) {
                        // DELETE ROW IS ALLOW DELETION RETURN TRUE.    
                            sSRowSet.deleteRow();
                            dBNav.performPostDeletionOps();
                         // SEEMS DELETION WAS SUCCESSFULL DECREMENT ROWCOUNT
                            rowCount--;    
                            if (! sSRowSet.next() ) {
                               sSRowSet.last();
                            }
                        // SET THE ROW COUNT AS LABEL
                            lblRowCount.setText("of " + rowCount);
                        // GET CURRENT ROW NUMBER
                            currentRow = sSRowSet.getRow();
                        // UPDATE THE TEXT FEILD
                            txtCurrentRow.setText(String.valueOf(currentRow));
                        }
                    
                    } catch(SQLException se) {
                        JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while deleting row.\n"+se.getMessage());
                        se.printStackTrace();
                    }
                }
            });

        // LISTENER FOR THE TEXT FIELD. USER CAN ENTER A ROW NUMBER IN THE TEXT
        // FIELD TO MOVE THE THE SPEICIFIED ROW.
        // IF ITS NOT A NUMBER OR IF ITS NOT VALID FOR THE CURRENT SSROWSET
        // NOTHING HAPPENS.
            txtCurrentRow.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                        try {
                            int row = Integer.parseInt(txtCurrentRow.getText().trim());
                            if (row <= rowCount && row >0) {
                                sSRowSet.absolute(row);
                            }
                        } catch(Exception e) {
                            // do nothing
                        }
                    }
                }
            });

    }

    /**
     * Adds the listeners on the SSRowSet used by data navigator.
     */
    private class SSDBNavRowSetListener implements RowSetListener {

        public void cursorMoved(RowSetEvent rse){
        // IF THERE ARE ROWS GET THE ROW COUNT
            try{
                currentRow = sSRowSet.getRow();
                updateInfo();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }

        public void rowChanged(RowSetEvent rse){
        // DO NOTHING
        }

        public void rowSetChanged(RowSetEvent rse){
        // IF THERE ARE ROWS GET THE ROW COUNT
            try{
                sSRowSet.last();
                rowCount = sSRowSet.getRow();
                sSRowSet.first();
                currentRow = sSRowSet.getRow();
                updateInfo();
            }catch(SQLException se){
                se.printStackTrace();
            }
            updateInfo();
        }

        protected void updateInfo(){
        // SET THE ROW COUNT AS LABEL
            lblRowCount.setText("of " + rowCount);
            txtCurrentRow.setText(String.valueOf(currentRow));
        // ENABLE OR DISABLE BUTTONS
            if (rowCount == 0) {
                firstButton.setEnabled(false);
                previousButton.setEnabled(false);
                nextButton.setEnabled(false);
                lastButton.setEnabled(false);
            } else{
                firstButton.setEnabled(true);
                previousButton.setEnabled(true);
                nextButton.setEnabled(true);
                lastButton.setEnabled(true);
            }

            try {
                if (sSRowSet.isLast()) {
                    nextButton.setEnabled(false);
                    lastButton.setEnabled(false);
                }
                if (sSRowSet.isFirst()) {
                    firstButton.setEnabled(false);
                    previousButton.setEnabled(false);
                }

            } catch(SQLException se) {
                se.printStackTrace();
            }
        }
    }


// DEPRECATED STUFF....................

    /**
     * Sets the new SSRowSet for the combo box.
     *
     * @param _sSRowSet  SSRowSet to which the combo has to update values.
     *
     * @deprecated
     * @see #setSSRowSet
     */
    public void setRowSet(SSRowSet _sSRowSet) {
        setSSRowSet(_sSRowSet);
    }

    /**
     * Returns the SSRowSet being used.
     *
     * @return returns the SSRowSet being used.
     *
     * @deprecated
     * @see #getSSRowSet
     */
     public SSRowSet getRowSet() {
        return sSRowSet;
     }

} // end public class SSDataNavigator extends JPanel {



/*
 * $Log$
 * Revision 1.46  2009/02/16 22:21:29  prasanth
 * Added try/catch in updatePresentRow.
 *
 * Revision 1.45  2009/02/16 18:27:35  prasanth
 * 1. In updatePresentRow calling doCommitButtonClick.
 * 2. When on insert row and allowInsertion returns false staying in insertion row rather than making a call to moveToCurrentRow().
 *
 * Revision 1.44  2008/05/12 14:27:42  prasanth
 * In updatePresentRow allowUpdate was not called before calling updateRow().
 * Modified the code to call allowUpdate & performPostUpateOps in updatePresentRow.
 *
 * Revision 1.43  2006/05/23 05:48:47  prasanth
 * While deleting the row checking for confirmDeletes variable to display the confirmation dialog.
 *
 * Revision 1.42  2006/05/15 16:10:38  prasanth
 * Updated copy right
 *
 * Revision 1.41  2006/02/03 23:23:54  prasanth
 * In updatePresentRow function checking for modification variable before updating the current row.
 *
 * Revision 1.40  2005/11/02 17:18:23  prasanth
 * Calling the allowUpdate & performPostUpdateOps functions on SSDBNav.
 *
 * Revision 1.39  2005/06/10 20:36:31  prasanth
 * Added function setFocusable. This will call setFocusable on all buttons and the
 * text field.
 *
 * Revision 1.38  2005/05/24 16:35:45  prasanth
 * Made the current row number text field non focusable.
 *
 * Revision 1.37  2005/05/24 14:33:35  prasanth
 * Made the buttons non focusable.
 *
 * Revision 1.36  2005/05/03 15:23:43  prasanth
 * Updated the listeners for commit button and delete button to call allowInsertion
 * and allowDeletion functions.
 *
 * Revision 1.35  2005/03/08 16:13:50  prasanth
 * In undoButton listener based on insertRow flag changing the function call.
 * cancelRowUpdates throws exception if current row is on insertRow.
 * Used to work in 1.4.2 (though java docs say it would not).
 * Also updating the current row number when undo is pressed.
 *
 * Revision 1.34  2005/02/13 15:38:20  yoda2
 * Removed redundant PropertyChangeListener and VetoableChangeListener class variables and methods from components with JComponent as an ancestor.
 *
 * Revision 1.33  2005/02/12 03:29:26  yoda2
 * Added bound properties (for beans).
 *
 * Revision 1.32  2005/02/11 22:59:28  yoda2
 * Imported PropertyVetoException and added some bound properties.
 *
 * Revision 1.31  2005/02/11 20:16:04  yoda2
 * Added infrastructure to support property & vetoable change listeners (for beans).
 *
 * Revision 1.30  2005/02/10 20:13:00  yoda2
 * Setter/getter cleanup & method reordering for consistency.
 *
 * Revision 1.29  2005/02/07 22:47:15  yoda2
 * Replaced internal calls to setRowSet() with calls to setSSRowSet().
 *
 * Revision 1.28  2005/02/07 22:19:33  yoda2
 * Fixed infinite loop in deprecated setRowSet() which was calling setRowSet() rather than setSSRowSet()
 *
 * Revision 1.27  2005/02/07 20:27:26  yoda2
 * JavaDoc cleanup & made private listener data members final.
 *
 * Revision 1.26  2005/02/04 22:48:54  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.25  2005/01/09 03:56:14  prasanth
 * Added public methods to programmatically perform different button clicks.
 *
 * Revision 1.24  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.23  2004/11/01 15:53:30  yoda2
 * Fixed various JavaDoc errors.
 *
 * Revision 1.22  2004/10/25 22:03:17  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.21  2004/10/25 19:51:02  prasanth
 * Modified to use the new SSRowSet instead of  RowSet.
 *
 * Revision 1.20  2004/09/21 18:58:28  prasanth
 * removing the sSRowSet listener while doing the refresh ops.
 *
 * Revision 1.19  2004/09/21 14:15:33  prasanth
 * Displaying error messages when an exception occurs, when the user presses
 * any button.
 *
 * Revision 1.18  2004/09/08 18:41:54  prasanth
 * Added a sSRowSet listener.
 *
 * Revision 1.17  2004/09/02 16:37:05  prasanth
 * Moving to the last record if your has added a record & pressed commit button.
 * This would keep the user in the added record.
 *
 * Revision 1.16  2004/09/01 18:42:08  prasanth
 * Was calling next in the refresh listener. This would move to second record
 * if refresh is pressed. If there is only one record then user will not be able to
 * see as the navigation buttons are also disabled.
 *
 * Revision 1.15  2004/08/26 22:00:00  prasanth
 * Setting all the buttons when a new sSRowSet is set.
 *
 * Revision 1.14  2004/08/24 22:21:03  prasanth
 * Changed the way images are loaded.
 *
 * Revision 1.13  2004/08/16 20:51:16  yoda2
 * Gave button names in code more meaningful names (e.g. button1 -> firstButton).
 *
 * Revision 1.12  2004/08/13 14:55:28  prasanth
 * Changed the default size of buttons (decreased).
 * Also that of text field  & label (increased).
 * Displaying "of" before rowcount.
 *
 * Revision 1.11  2004/08/12 23:50:24  prasanth
 * Changing the row count when a new row is added or a row is deleted.
 *
 * Revision 1.10  2004/08/11 20:29:01  prasanth
 * When sSRowSet has no rows the values in text field and row count label were
 * not updated so corrected this.
 *
 * Revision 1.9  2004/08/10 22:06:59  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.8  2004/08/02 15:04:06  prasanth
 * 1. Added a text field to display current row number.
 * 2. Added a label to display the total number of rows in the sSRowSet.
 * 3. Added listener to text field so that user can enter a row number
 *     to navigate to that row.
 *
 * Revision 1.7  2004/03/08 16:40:00  prasanth
 * Added the if condition to check for callExecute in the listener for refresh button.
 *
 * Revision 1.6  2004/02/23 16:36:00  prasanth
 * Added  setMySQLDB function.
 * Skipping execute call on the sSRowSet if mySQL is true.
 *
 * Revision 1.5  2004/01/27 17:13:03  prasanth
 * Changed the behaviour of commit button. When not on insert row will update
 * present row.
 *
 * Also modified enabling and disabling of navigation buttons.
 *
 * Revision 1.4  2003/11/26 21:24:49  prasanth
 * Calling performCancelOps().
 *
 * Revision 1.3  2003/10/31 16:02:52  prasanth
 * Added login to disable the navigation buttons when only one record is present
 * in the sSRowSet.
 *
 * Revision 1.2  2003/09/25 14:27:45  yoda2
 * Removed unused Import statements and added preformatting tags to JavaDoc descriptions.
 *
 * Revision 1.1.1.1  2003/09/25 13:56:43  yoda2
 * Initial CVS import for SwingSet.
 *
 */