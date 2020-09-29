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

package com.nqadmin.swingset;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.datasources.SSRowSet;
import com.nqadmin.swingset.utils.SSEnums.Navigation;

// SSDataNavigator.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Component that can be used for data navigation. It provides buttons for
 * navigation, insertion, and deletion of records in a SSRowSet. The
 * modification of a SSRowSet can be prevented using the setModificaton()
 * method. Any changes made to the columns of a record will be updated whenever
 * there is a navigation.
 * <p>
 * For example if you are displaying three columns using the JTextField and the
 * user changes the text in the text fields then the columns will be updated to
 * the new values when the user navigates the SSRowSet. If the user wants to
 * revert the changes he made he can press the Undo button, however this must be
 * done before any navigation. Once navigation takes place changes can't be
 * reverted using Undo button (has to be done manually by the user).
 */
public class SSDataNavigator extends JPanel {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 3129669039062103212L;

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
	 * Indicator to cause the navigator to skip the execute() function call on the
	 * specified SSRowSet. Must be false for MySQL (see FAQ).
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
	 * Number of rows in SSRowSet. Set to zero if next() method returns false.
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
	 * SSDBComboBox used for navigation if applicable.
	 * <p>
	 * Allows Navigator to disable it when a row is inserted and enable it when that row is saved.
	 * <p>
	 * TODO Consider writing a PropertyChangeListener for onInsertRow instead. 
	 */
	protected SSDBComboBox navCombo= null;
	
	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * @return the navCombo
	 */
	public SSDBComboBox getNavCombo() {
		return navCombo;
	}

	/**
	 * @param _navCombo the navCombo to set
	 */
	public void setNavCombo(SSDBComboBox _navCombo) {
		navCombo = _navCombo;
	}

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
	protected final SSDBNavRowSetListener sSRowSetListener = new SSDBNavRowSetListener();

	/**
	 * Creates a object of SSDataNavigator. Note: you have to set the SSRowSet
	 * before you can start using it.
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
	 * Constructs the SSDataNavigator with the given SSRowSet and sets the size of
	 * the buttons on the navigator to the given size
	 *
	 * @param _sSRowSet   the SSRowSet to which the navigator is bound to
	 * @param _buttonSize the size to which the button on navigator have to be set
	 */
	public SSDataNavigator(SSRowSet _sSRowSet, Dimension _buttonSize) {
		this.buttonSize = _buttonSize;
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

		if (this.rowCount == 0) {
			return false;
		}

		return true;
	}

	/**
	 * Method to cause the navigator to skip the execute() function call on the
	 * underlying SSRowSet. This is necessary for MySQL (see FAQ).
	 *
	 * @param _callExecute false if using MySQL database - otherwise true
	 */
	public void setCallExecute(boolean _callExecute) {
		boolean oldValue = this.callExecute;
		this.callExecute = _callExecute;
		firePropertyChange("callExecute", oldValue, this.callExecute);
	}

	/**
	 * Indicates if the navigator will skip the execute function call on the
	 * underlying SSRowSet (needed for MySQL - see FAQ).
	 *
	 * @return value of execute() indicator
	 */
	public boolean getCallExecute() {
		return this.callExecute;
	}

	/**
	 * Sets the preferredSize and the MinimumSize of the buttons to the specified
	 * size
	 *
	 * @param _buttonSize the required dimension of the buttons
	 */
	public void setButtonSize(Dimension _buttonSize) {
		Dimension oldValue = this.buttonSize;
		this.buttonSize = _buttonSize;
		firePropertyChange("buttonSize", oldValue, this.buttonSize);
		setButtonSizes();
	}

	/**
	 * Returns the size of buttons on the data navigator.
	 *
	 * @return returns a Dimension object representing the size of each button on
	 *         the data navigator.
	 */
	public Dimension getButtonSize() {
		return this.buttonSize;
	}

	/**
	 * Function that passes the implementation of the SSDBNav interface. This
	 * interface can be implemented by the developer to perform custom actions when
	 * the insert button is pressed
	 *
	 * @param _dBNav implementation of the SSDBNav interface
	 */
	public void setDBNav(SSDBNav _dBNav) {
		SSDBNav oldValue = this.dBNav;
		this.dBNav = _dBNav;
		firePropertyChange("dBNav", oldValue, this.dBNav);
	}

	/**
	 * Returns any custom implementation of the SSDBNav interface, which is used
	 * when the insert button is pressed to perform custom actions.
	 *
	 * @return any custom implementation of the SSDBNav interface
	 */
	public SSDBNav getDBNav() {
		return this.dBNav;
	}

	/**
	 * Enables or disables the modification-related buttons on the SSDataNavigator.
	 * If the user can only navigate through the records with out making any changes
	 * set this to false. By default, the modification-related buttons are enabled.
	 *
	 * @param _modification indicates whether or not the modification-related
	 *                      buttons are enabled.
	 */
	public void setModification(boolean _modification) {
		boolean oldValue = this.modification;
		this.modification = _modification;
		firePropertyChange("modification", oldValue, this.modification);

		if (!this.modification) {
			this.commitButton.setEnabled(false);
			this.undoButton.setEnabled(false);
			this.addButton.setEnabled(false);
			this.deleteButton.setEnabled(false);
		} else {
			this.commitButton.setEnabled(true);
			this.undoButton.setEnabled(true);
			this.addButton.setEnabled(true);
			this.deleteButton.setEnabled(true);
		}
	}

	/**
	 * Returns true if the user can modify the data in the SSRowSet, else false.
	 *
	 * @return returns true if the user modifications are written back to the
	 *         database, else false.
	 */
	public boolean getModification() {
		return this.modification;
	}

	/**
	 * Enables or disables the row deletion button. This method should be used if
	 * row deletions are not allowed. True by default.
	 *
	 * @param _deletion indicates whether or not to allow deletions
	 */
	public void setDeletion(boolean _deletion) {
		boolean oldValue = this.deletion;
		this.deletion = _deletion;
		firePropertyChange("deletion", oldValue, this.deletion);

		if (!this.deletion) {
			this.deleteButton.setEnabled(false);
		} else {
			this.deleteButton.setEnabled(true);
		}
	}

	/**
	 * Returns true if deletions are allowed, else false.
	 *
	 * @return returns true if deletions are allowed, else false.
	 */
	public boolean getDeletion() {
		return this.deletion;
	}

	/**
	 * Enables or disables the row insertion button. This method should be used if
	 * row insertions are not allowed. True by default.
	 *
	 * @param _insertion indicates whether or not to allow insertions
	 */
	public void setInsertion(boolean _insertion) {
		boolean oldValue = this.insertion;
		this.insertion = _insertion;
		firePropertyChange("insertion", oldValue, this.insertion);

		if (!this.insertion) {
			this.addButton.setEnabled(false);
		} else {
			this.addButton.setEnabled(true);
		}
	}

	/**
	 * Returns true if insertions are allowed, else false.
	 *
	 * @return returns true if insertions are allowed, else false.
	 */
	public boolean getInsertion() {
		return this.insertion;
	}

	/**
	 * Sets the confirm deletion indicator. If set to true, every time delete button
	 * is pressed, the navigator pops up a confirmation dialog to the user. Default
	 * value is true.
	 *
	 * @param _confirmDeletes indicates whether or not to confirm deletions
	 */
	public void setConfirmDeletes(boolean _confirmDeletes) {
		boolean oldValue = this.confirmDeletes;
		this.confirmDeletes = _confirmDeletes;
		firePropertyChange("confirmDeletes", oldValue, this.confirmDeletes);
	}

	/**
	 * Returns true if deletions must be confirmed by user, else false.
	 *
	 * @return returns true if a confirmation dialog is displayed when the user
	 *         deletes a record, else false.
	 */
	public boolean getConfirmDeletes() {
		return this.confirmDeletes;
	}

	/**
	 * This method changes the SSRowSet to which the navigator is bound. The
	 * execute() and next() methods MUST be called on the SSRowSet before you set
	 * the SSRowSet for the SSDataNavigator.
	 *
	 * @param _sSRowSet a SSRowSet object to which the navigator will be bound
	 */
	public void setSSRowSet(SSRowSet _sSRowSet) {
		// RESET INSERT FLAG THIS IS NEED IF USERS LEFT THE LAST ROWSET IN INSERTION
		// MODE
		// WITH OUT SAVING THE RECORD OR UNDOING THE INSERTION
		this.onInsertRow = false;

		if (this.sSRowSet != null) {
			this.sSRowSet.removeRowSetListener(this.sSRowSetListener);
		}

		SSRowSet oldValue = this.sSRowSet;
		this.sSRowSet = _sSRowSet;
		firePropertyChange("sSRowSet", oldValue, this.sSRowSet);

		// SEE IF THERE ARE ANY ROWS IN THE GIVEN SSROWSET
		try {
			if (this.callExecute) {
				this.sSRowSet.execute();
			}

			if (!this.sSRowSet.next()) {
				this.rowCount = 0;
				this.currentRow = 0;
			} else {
				// IF THERE ARE ROWS GET THE ROW COUNT
				this.sSRowSet.last();
				this.rowCount = this.sSRowSet.getRow();
				this.sSRowSet.first();
				this.currentRow = this.sSRowSet.getRow();
			}
			// SET THE ROW COUNT AS LABEL
			this.lblRowCount.setText("of " + this.rowCount);
			this.txtCurrentRow.setText(String.valueOf(this.currentRow));

			this.sSRowSet.addRowSetListener(this.sSRowSetListener);
		} catch (SQLException se) {
			logger.error("SQL Exception.", se);
		}

		// IF NO ROWS ARE PRESENT DISABLE NAVIGATION
		// ELSE ENABLE THEN ELSE IS USEFUL WHEN THE SSROWSET IS CHNAGED
		// IF THE INITIAL SSROWSET HAS ZERO ROWS NEXT IF THE USER SETS A NEW SSROWSET
		// THEN THE BUTTONS HAVE TO BE ENABLED
		if (this.rowCount == 0) {
			this.firstButton.setEnabled(false);
			this.previousButton.setEnabled(false);
			this.nextButton.setEnabled(false);
			this.lastButton.setEnabled(false);
		} else {
			this.firstButton.setEnabled(true);
			this.previousButton.setEnabled(true);
			this.nextButton.setEnabled(true);
			this.lastButton.setEnabled(true);
		}

		try {
			if (this.sSRowSet.isLast()) {
				this.nextButton.setEnabled(false);
				this.lastButton.setEnabled(false);
			}
			if (this.sSRowSet.isFirst()) {
				this.firstButton.setEnabled(false);
				this.previousButton.setEnabled(false);
			}

		} catch (SQLException se) {
			logger.error("SQL Exception.", se);
		}

		// ENABLE OTHER BUTTONS IF NEED BE.

		// THIS IS NEEDED TO HANDLE USER LEAVING THE SCREEN IN AN INCONSISTENT
		// STATE EXAMPLE: USER CLICKS ADD BUTTON, THIS DISABLES ALL THE BUTTONS
		// EXCEPT COMMIT & UNDO. WITH OUT COMMITING OR UNDOING THE ADD USER
		// CLOSES THE SCREEN. NOW IF THE SCREEN IS OPENED WITH A NEW SSROWSET.
		// THE REFRESH, ADD & DELETE WILL BE DISABLED.
		// 2019-11-11: only enabling add/delete if this.modification==true
		this.refreshButton.setEnabled(true);
		if (this.insertion && this.modification) {
			this.addButton.setEnabled(true);
		}
		if (this.deletion && this.modification) {
			this.deleteButton.setEnabled(true);
		}
	}

	/**
	 * Returns the SSRowSet being used.
	 *
	 * @return returns the SSRowSet being used.
	 */
	public SSRowSet getSSRowSet() {
		return this.sSRowSet;
	}

	/**
	 * Writes the present row back to the SSRowSet. This is done automatically when
	 * any navigation takes place, but can also be called manually.
	 *
	 * @return returns true if update succeeds else false.
	 */
	public boolean updatePresentRow() {
		if (this.onInsertRow || this.currentRow > 0)
			doCommitButtonClick();

		return true;
	}

	/**
	 * Calls the doClick on First Button.
	 */
	public void doFirstButtonClick() {
		this.firstButton.doClick();
	}

	/**
	 * Calls the doClick on Previous Button.
	 */
	public void doPreviousButtonClick() {
		this.previousButton.doClick();
	}

	/**
	 * Calls the doClick on Next Button.
	 */
	public void doNextButtonClick() {
		this.nextButton.doClick();
	}

	/**
	 * Calls the doClick on Last Button.
	 */
	public void doLastButtonClick() {
		this.lastButton.doClick();
	}

	/**
	 * Calls the doClick on Refresh Button.
	 */
	public void doRefreshButtonClick() {
		this.refreshButton.doClick();
	}

	/**
	 * Calls the doClick on Commit Button.
	 */
	public void doCommitButtonClick() {
		this.commitButton.doClick();
	}

	/**
	 * Calls the doClick on Undo Button.
	 */
	public void doUndoButtonClick() {
		this.undoButton.doClick();
	}

	/**
	 * Calls the doClick on Add Button.
	 */
	public void doAddButtonClick() {
		this.addButton.doClick();
	}

	/**
	 * Calls the doClick on Delete Button.
	 */
	public void doDeleteButtonClick() {
		this.deleteButton.doClick();
	}

	/**
	 * Method to add tooltips and button graphics (or text) to navigator components.
	 */
	protected void addToolTips() {

		try {
			ClassLoader cl = this.getClass().getClassLoader();
			this.firstButton.setIcon(new ImageIcon(cl.getResource("images/first.gif")));
			this.previousButton.setIcon(new ImageIcon(cl.getResource("images/prev.gif")));
			this.nextButton.setIcon(new ImageIcon(cl.getResource("images/next.gif")));
			this.lastButton.setIcon(new ImageIcon(cl.getResource("images/last.gif")));
			this.commitButton.setIcon(new ImageIcon(cl.getResource("images/commit.gif")));
			this.undoButton.setIcon(new ImageIcon(cl.getResource("images/undo.gif")));
			this.refreshButton.setIcon(new ImageIcon(cl.getResource("images/refresh.gif")));
			this.addButton.setIcon(new ImageIcon(cl.getResource("images/add.gif")));
			this.deleteButton.setIcon(new ImageIcon(cl.getResource("images/delete.gif")));
		} catch (Exception e) {
			this.firstButton.setText("<<");
			this.previousButton.setText("<");
			this.nextButton.setText(">");
			this.lastButton.setText(">>");
			this.commitButton.setText("Commit");
			this.undoButton.setText("Undo");
			this.refreshButton.setText("Refresh");
			this.addButton.setText("Add");
			this.deleteButton.setText("Delete");
			logger.warn("Unable to load images for navigator buttons.", e);
		}

		// SET TOOL TIPS FOR THE BUTTONS
		this.firstButton.setToolTipText("First");
		this.previousButton.setToolTipText("Previous");
		this.nextButton.setToolTipText("Next");
		this.lastButton.setToolTipText("Last");
		this.commitButton.setToolTipText("Commit");
		this.undoButton.setToolTipText("Undo");
		this.refreshButton.setToolTipText("Refresh");
		this.addButton.setToolTipText("Add Record");
		this.deleteButton.setToolTipText("Delete Record");

	} // end protected void addToolTips() {

	/**
	 * This will make all the components in the navigator to either focusable
	 * components or non focusable components. Set to false if you don't want any of
	 * the buttons or text fields in the navigator to receive the focus else true.
	 * The default value is true.
	 * 
	 * @param focusable - false if you don't want the navigator to receive focus
	 *                  else false.
	 */
	@Override
	public void setFocusable(boolean focusable) {
		// MAKE THE BUTTONS NON FOCUSABLE IF REQUESTED
		this.firstButton.setFocusable(focusable);
		this.previousButton.setFocusable(focusable);
		this.nextButton.setFocusable(focusable);
		this.lastButton.setFocusable(focusable);
		this.commitButton.setFocusable(focusable);
		this.undoButton.setFocusable(focusable);
		this.refreshButton.setFocusable(focusable);
		this.addButton.setFocusable(focusable);
		this.deleteButton.setFocusable(focusable);
		this.txtCurrentRow.setFocusable(focusable);
	}

	/**
	 * Sets the dimensions for the navigator components.
	 */
	protected void setButtonSizes() {

		// SET THE PREFERRED SIZES
		this.firstButton.setPreferredSize(this.buttonSize);
		this.previousButton.setPreferredSize(this.buttonSize);
		this.nextButton.setPreferredSize(this.buttonSize);
		this.lastButton.setPreferredSize(this.buttonSize);
		this.commitButton.setPreferredSize(this.buttonSize);
		this.undoButton.setPreferredSize(this.buttonSize);
		this.refreshButton.setPreferredSize(this.buttonSize);
		this.addButton.setPreferredSize(this.buttonSize);
		this.deleteButton.setPreferredSize(this.buttonSize);
		this.txtCurrentRow.setPreferredSize(this.txtFieldSize);
		this.lblRowCount.setPreferredSize(this.txtFieldSize);
		this.lblRowCount.setHorizontalAlignment(SwingConstants.CENTER);

		// SET MINIMUM BUTTON SIZES
		this.firstButton.setMinimumSize(this.buttonSize);
		this.previousButton.setMinimumSize(this.buttonSize);
		this.nextButton.setMinimumSize(this.buttonSize);
		this.lastButton.setMinimumSize(this.buttonSize);
		this.commitButton.setMinimumSize(this.buttonSize);
		this.undoButton.setMinimumSize(this.buttonSize);
		this.refreshButton.setMinimumSize(this.buttonSize);
		this.addButton.setMinimumSize(this.buttonSize);
		this.deleteButton.setMinimumSize(this.buttonSize);
		this.txtCurrentRow.setMinimumSize(this.txtFieldSize);
		this.lblRowCount.setMinimumSize(this.txtFieldSize);
	}

	/**
	 * Adds the navigator components to the navigator panel.
	 */
	protected void createPanel() {

		setButtonSizes();
		// SET THE BOX LAYOUT
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		// ADD BUTTONS TO THE PANEL
		add(this.firstButton);
		add(this.previousButton);
		add(this.txtCurrentRow);
		add(this.nextButton);
		add(this.lastButton);
		add(this.commitButton);
		add(this.undoButton);
		add(this.refreshButton);
		add(this.addButton);
		add(this.deleteButton);
		add(this.lblRowCount);
		// pack();
	}

	/**
	 * Adds the listeners for the navigator components.
	 */
	protected void addListeners() {

		// WHEN THIS BUTTON IS PRESSED THE RECORD ON WHICH USER WAS WORKING IS SAVED
		// AND MOVES THE SSROWSET TO THE FIRST ROW
		// SINCE ROW SET IS IN FIRST ROW DISABLE PREVIOUS BUTTON AND ENABLE NEXT BUTTON
		this.firstButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					if (SSDataNavigator.this.modification) {
						// CHECK IF THE DBNAV IS NULL IF SO UPDATE ROW
						if (SSDataNavigator.this.dBNav == null) {
							SSDataNavigator.this.sSRowSet.updateRow();
						} else {
							// IF DBNAV IS NOT NULL CALL ALLOW UPDATE
							if (SSDataNavigator.this.dBNav.allowUpdate()) {
								// UPDATE ALLOWED GO AHEAD AND DO THE UPDATE
								SSDataNavigator.this.sSRowSet.updateRow();
								SSDataNavigator.this.dBNav.performPostUpdateOps();
							} else {
								// UPDATE NOT ALLOWED SO DO NOTHING.
								// WE SHOULD NOT MOVE TO THE ROW AS THE USER HAS MADE CHANGES TO
								// TO THE ROW THAT SHOULD BE UNDONE.
								return;
							}
						}
					}
					SSDataNavigator.this.sSRowSet.first();

					updateNavigator();

					if (SSDataNavigator.this.dBNav != null) {
						SSDataNavigator.this.dBNav.performNavigationOps(Navigation.First);
						// ALSO CALL DEPRECATED performNavigationOps TO ALLOW FOR LEGACY CODE
						SSDataNavigator.this.dBNav.performNavigationOps(SSDBNav.NAVIGATION_FIRST);
					}
					// GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD
					// 2019-10-14: updateNavigator should take care of this
					//SSDataNavigator.this.currentRow = 1;
					//SSDataNavigator.this.txtCurrentRow.setText(String.valueOf(SSDataNavigator.this.currentRow));
				} catch (SQLException se) {
					logger.error("SQL Exception.", se);
					JOptionPane.showMessageDialog(SSDataNavigator.this,
							"Exception occured while updating row or moving the cursor.\n" + se.getMessage());
				}
			}
		});

		// WHEN BUTTON 2 IS PRESSED THE CURRENT RECORD IS SAVED AND SSROWSET IS
		// MOVED TO PREVIOUS RECORD
		// CALLING PREVIOUS ON ENPTY SSROWSET IS ILLEGAL SO A CHECK IS MADE FOR THAT
		// IF NUMBER OF ROWS == 0 THEN SSROWSET IS EMPTY
		this.previousButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					// if( sSRowSet.rowUpdated() )
					if (SSDataNavigator.this.modification) {
						// CHECK IF THE DBNAV IS NULL IF SO UPDATE ROW
						if (SSDataNavigator.this.dBNav == null) {
							SSDataNavigator.this.sSRowSet.updateRow();
						} else {
							// IF DBNAV IS NOT NULL CALL ALLOW UPDATE
							if (SSDataNavigator.this.dBNav.allowUpdate()) {
								// UPDATE ALLOWED GO AHEAD AND DO THE UPDATE
								SSDataNavigator.this.sSRowSet.updateRow();
								SSDataNavigator.this.dBNav.performPostUpdateOps();
							} else {
								// UPDATE NOT ALLOWED SO DO NOTHING.
								// WE SHOULD NOT MOVE TO THE ROW AS THE USER HAS MADE CHANGES TO
								// TO THE ROW THAT SHOULD BE UNDONE.
								return;
							}
						}
					}
					if (SSDataNavigator.this.sSRowSet.getRow() != 0 && !SSDataNavigator.this.sSRowSet.previous()) {
						SSDataNavigator.this.sSRowSet.first();
					}

					updateNavigator();

					if (SSDataNavigator.this.dBNav != null) {
						SSDataNavigator.this.dBNav.performNavigationOps(Navigation.Previous);
						// ALSO CALL DEPRECATED performNavigationOps TO ALLOW FOR LEGACY CODE
						SSDataNavigator.this.dBNav.performNavigationOps(SSDBNav.NAVIGATION_PREVIOUS);
					}
					// GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD
					// 2019-10-14: updateNavigator should take care of this
					//SSDataNavigator.this.currentRow = SSDataNavigator.this.sSRowSet.getRow();
					//SSDataNavigator.this.txtCurrentRow.setText(String.valueOf(SSDataNavigator.this.currentRow));
				} catch (SQLException se) {
					logger.error("SQL Exception.", se);
					JOptionPane.showMessageDialog(SSDataNavigator.this,
							"Exception occured while updating row or moving the cursor.\n" + se.getMessage());
				}
			}
		});

		// WHEN BUTTON 3 PRESSED THE CURRENT RECORD IS SAVED AND THE SSROWSET IS
		// MOVED TO NEXT RECORD. IF THIS IS THE LAST RECORD THEN BUTTON 3 IS DISABLED
		// ALSO IF THE PREVIOUS BUTTON IS NOT ENABLED THEN IT IS ENABLED
		this.nextButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					// if( sSRowSet.rowUpdated() )
					if (SSDataNavigator.this.modification) {
						// CHECK IF THE DBNAV IS NULL IF SO UPDATE ROW
						if (SSDataNavigator.this.dBNav == null) {
							SSDataNavigator.this.sSRowSet.updateRow();
						} else {
							// IF DBNAV IS NOT NULL CALL ALLOW UPDATE
							if (SSDataNavigator.this.dBNav.allowUpdate()) {
								// UPDATE ALLOWED GO AHEAD AND DO THE UPDATE
								SSDataNavigator.this.sSRowSet.updateRow();
								SSDataNavigator.this.dBNav.performPostUpdateOps();
							} else {
								// UPDATE NOT ALLOWED SO DO NOTHING.
								// WE SHOULD NOT MOVE TO THE ROW AS THE USER HAS MADE CHANGES TO
								// TO THE ROW THAT SHOULD BE UNDONE.
								return;
							}
						}
					}

					SSDataNavigator.this.sSRowSet.next();

					updateNavigator();

					if (SSDataNavigator.this.dBNav != null) {
						SSDataNavigator.this.dBNav.performNavigationOps(Navigation.Next);
						// ALSO CALL DEPRECATED performNavigationOps TO ALLOW FOR LEGACY CODE
						SSDataNavigator.this.dBNav.performNavigationOps(SSDBNav.NAVIGATION_NEXT);
					}
					// GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD
					// 2019-10-14: updateNavigator should take care of this
					//SSDataNavigator.this.currentRow = SSDataNavigator.this.sSRowSet.getRow();
					//SSDataNavigator.this.txtCurrentRow.setText(String.valueOf(SSDataNavigator.this.currentRow));
				} catch (SQLException se) {
					logger.error("SQL Exception.", se);
					JOptionPane.showMessageDialog(SSDataNavigator.this,
							"Exception occured while updating row or moving the cursor.\n" + se.getMessage());
				}
			}
		});

		// BUTTON 4 ( "LAST" BUTTON ) CAUSED THE SSROWSET TO MOVE TO LAST RECORD.
		// BEFORE MOVING CURRENT RECORD IS SAVED
		// AFTER MOVING TO LAST RECORD THE NEXT BUTTON IS DIAABLED AND PREVIOUS BUTTON
		// ENABLED
		this.lastButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					// if( sSRowSet.rowUpdated() )
					if (SSDataNavigator.this.modification) {
						// CHECK IF THE DBNAV IS NULL IF SO UPDATE ROW
						if (SSDataNavigator.this.dBNav == null) {
							SSDataNavigator.this.sSRowSet.updateRow();
						} else {
							// IF DBNAV IS NOT NULL CALL ALLOW UPDATE
							if (SSDataNavigator.this.dBNav.allowUpdate()) {
								// UPDATE ALLOWED GO AHEAD AND DO THE UPDATE
								SSDataNavigator.this.sSRowSet.updateRow();
								SSDataNavigator.this.dBNav.performPostUpdateOps();
							} else {
								// UPDATE NOT ALLOWED SO DO NOTHING.
								// WE SHOULD NOT MOVE TO THE ROW AS THE USER HAS MADE CHANGES TO
								// TO THE ROW THAT SHOULD BE UNDONE.
								return;
							}
						}
					}
					SSDataNavigator.this.sSRowSet.last();

					updateNavigator();

					if (SSDataNavigator.this.dBNav != null) {
						SSDataNavigator.this.dBNav.performNavigationOps(Navigation.Last);
						// ALSO CALL DEPRECATED performNavigationOps TO ALLOW FOR LEGACY CODE
						SSDataNavigator.this.dBNav.performNavigationOps(SSDBNav.NAVIGATION_LAST);
					}

				} catch (SQLException se) {
					logger.error("SQL Exception.", se);
					JOptionPane.showMessageDialog(SSDataNavigator.this,
							"Exception occured while updating row or moving the cursor.\n" + se.getMessage());
				}
			}
		});

		// THIS BUTTON INSERTS THE ROW AND MOVES TO THE NEWLY INSERTED ROW.
		// WHEN INSERT BUTTON IS PRESSED NAVIGATION WILL BE DISABLED SO THOSE HAVE TO BE
		// ENABLED HERE
		this.commitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					if (SSDataNavigator.this.onInsertRow) {
						// IF ON INSERT ROW ADD THE ROW.
						// CHECK IF THE ROW CAN BE INSERTED.
						if (SSDataNavigator.this.dBNav == null || (SSDataNavigator.this.dBNav != null
								&& SSDataNavigator.this.dBNav.allowInsertion())) {
							SSDataNavigator.this.sSRowSet.insertRow();
							SSDataNavigator.this.onInsertRow = false;
							SSDataNavigator.this.dBNav.performPostInsertOps();
							
							// 2019-10-14: next bit of code seems odd. not sure why we're calling moveToCurrentRow() and last().
							//  In H2, this is leading to an incorrect total # of rows (e.g., have 5, insert a row, and it shows 6 of 7 until refreshed)
							
							/*
							// INCREMENT THE ROW COUNT
							SSDataNavigator.this.rowCount++;

							SSDataNavigator.this.sSRowSet.moveToCurrentRow();
							// MOVE TO CURRENT ROW MOVES SSROWSET TO RECORD AT WHICH ADD WAS PRESSED.
							// BUT IT NICE TO BE ON THE ADDED ROW WHICH IS THE LAST ONE IN THE SSROWSET.
							// ALSO MOVE TO CURRENT ROW MOVES THE SSROWSET POSITION BUT DOES NOT TRIGGER
							// ANY EVENT FOR THE LISTENERS AS A RESULT VALUES ON THE SCREEN WILL NOT
							// DISPLAY THE CURRENT RECORD VALUES.
							 */
							SSDataNavigator.this.sSRowSet.last();
							// 2019-10-14: adding this to replace SSDataNavigator.this.rowCount++;
							SSDataNavigator.this.rowCount = SSDataNavigator.this.sSRowSet.getRow();

							updateNavigator();

							SSDataNavigator.this.refreshButton.setEnabled(true);

							if (SSDataNavigator.this.insertion) {
								SSDataNavigator.this.addButton.setEnabled(true);
							}
							if (SSDataNavigator.this.deletion) {
								SSDataNavigator.this.deleteButton.setEnabled(true);
							}
						} else {
							// WE DO NOTHING. THE ROWSET STAYS IN INSERT ROW. EITHER USER HAS TO FIX THE
							// DATA AND SAVE THE ROW
							// OR CANCEL THE INSERTION.
						}

					} else {
						// ELSE UPDATE THE PRESENT ROW VALUES.
						// CHECK IF THE DBNAV IS NULL IF SO UPDATE ROW
						if (SSDataNavigator.this.dBNav == null) {
							SSDataNavigator.this.sSRowSet.updateRow();
						} else {
							// IF DBNAV IS NOT NULL CALL ALLOW UPDATE
							if (SSDataNavigator.this.dBNav.allowUpdate()) {
								// UPDATE ALLOWED GO AHEAD AND DO THE UPDATE
								SSDataNavigator.this.sSRowSet.updateRow();
								SSDataNavigator.this.dBNav.performPostUpdateOps();
							} else {
								// UPDATE NOT ALLOWED SO DO NOTHING.
								// WE SHOULD NOT MOVE TO THE ROW AS THE USER HAS MADE CHANGES TO
								// TO THE ROW THAT SHOULD BE UNDONE.
								return;
							}
						}
					}

				} catch (SQLException se) {
					logger.error("SQL Exception.", se);
					JOptionPane.showMessageDialog(SSDataNavigator.this,
							"Exception occured while saving row.\n" + se.getMessage());
				}
			}
		});

		// THIS BUTTON IS USED TO CANCEL THE CHANGES MADE TO THE RECORD.
		// IT CAN ALSO BE USED TO CANCEL INSERT ROW.
		// SO THE BUTTONS DISABLED AT THE INSERT BUTTON EVENT HAVE TO BE ENABLED
		this.undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					// CALL MOVE TO CURRENT ROW IF ON INSERT ROW.
					if (SSDataNavigator.this.onInsertRow) {
						SSDataNavigator.this.sSRowSet.moveToCurrentRow();
					}
					// THIS FUNCTION IS NOT NEED IF ON INSERT ROW
					// BUT MOVETOINSERTROW WILL NOT TRIGGER ANY EVENT SO FOR THE SCREEN
					// TO UPDATE WE NEED TO TRIGGER SOME THING.
					// SINCE USER IS MOVED TO CURRENT ROW PRIOR TO INSERT IT IS SAFE TO
					// CALL CANCELROWUPDATE TO GET A TRIGGER
					SSDataNavigator.this.sSRowSet.cancelRowUpdates();
					SSDataNavigator.this.onInsertRow = false;
					if (SSDataNavigator.this.dBNav != null) {
						SSDataNavigator.this.dBNav.performCancelOps();
					}
					SSDataNavigator.this.sSRowSet.refreshRow();

					updateNavigator();

					SSDataNavigator.this.refreshButton.setEnabled(true);
					if (SSDataNavigator.this.insertion) {
						SSDataNavigator.this.addButton.setEnabled(true);
					}
					if (SSDataNavigator.this.deletion) {
						SSDataNavigator.this.deleteButton.setEnabled(true);
					}

				} catch (SQLException se) {
					logger.error("SQL Exception.", se);
					JOptionPane.showMessageDialog(SSDataNavigator.this,
							"Exception occured while undoing changes.\n" + se.getMessage());
				}
			}
		});

		// REFETCH REFETCHES THE ROWS FROMS THE DATABASE AND MOVES THE CURSOR TO THE
		// FIRST
		// RECORD IF THERE ARE NO RECORDS NAVIGATION BUTTONS ARE DIABLED
		// EVEN IS THERE ARE RECORDS PREVIOUS BUTTON IS DISABLED BECAUSE THE SSROWSET IS
		// ON
		// THE FIRST ROW
		this.refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				SSDataNavigator.this.sSRowSet.removeRowSetListener(SSDataNavigator.this.sSRowSetListener);
				try {
					if (SSDataNavigator.this.callExecute) {
						SSDataNavigator.this.sSRowSet.execute();

						if (!SSDataNavigator.this.sSRowSet.next()) {
							// THERE ARE NO RECORDS IN THE ROWSET
							SSDataNavigator.this.rowCount = 0;
						} else {
							// WE HAVE ROWS GET THE ROW COUNT AND MOVE BACK TO FIRST ROW
							SSDataNavigator.this.sSRowSet.last();
							SSDataNavigator.this.rowCount = SSDataNavigator.this.sSRowSet.getRow();
							SSDataNavigator.this.sSRowSet.first();
						}

						updateNavigator();
					}

					if (SSDataNavigator.this.dBNav != null) {
						SSDataNavigator.this.dBNav.performRefreshOps();
					}

				} catch (SQLException se) {
					logger.error("SQL Exception.", se);
					JOptionPane.showMessageDialog(SSDataNavigator.this,
							"Exception occured refreshing the data.\n" + se.getMessage());
				}
				SSDataNavigator.this.sSRowSet.addRowSetListener(SSDataNavigator.this.sSRowSetListener);
			}
		});

		// INSERT ROW BUTTON MOVES THE SSROWSET TO THE INSERT ROW POSITION
		// AT THIS TIME NAVIGATION HAS TO BE DISABLED
		// ONLY COMMIT AND CANCEL ARE ENABLED
		this.addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {

					SSDataNavigator.this.sSRowSet.moveToInsertRow();
					SSDataNavigator.this.onInsertRow = true;
					if (navCombo!=null) {
						navCombo.setEnabled(false);
					}

					if (SSDataNavigator.this.dBNav != null) {
						SSDataNavigator.this.dBNav.performPreInsertOps();
					}

					SSDataNavigator.this.firstButton.setEnabled(false);
					SSDataNavigator.this.previousButton.setEnabled(false);
					SSDataNavigator.this.nextButton.setEnabled(false);
					SSDataNavigator.this.lastButton.setEnabled(false);
					SSDataNavigator.this.commitButton.setEnabled(true);
					SSDataNavigator.this.undoButton.setEnabled(true);
					SSDataNavigator.this.refreshButton.setEnabled(false);
					SSDataNavigator.this.addButton.setEnabled(false);
					SSDataNavigator.this.deleteButton.setEnabled(false);

				} catch (SQLException se) {
					logger.error("SQL Exception.", se);
					JOptionPane.showMessageDialog(SSDataNavigator.this,
							"Exception occured while moving to insert row.\n" + se.getMessage());
				}
			}
		});

		// DELETES THE CURRENT ROW AND MOVES TO NEXT ROW
		// IF THE DELETED ROW IS THE LAST ROW THEN MOVES TO LAST ROW IN SSROWSET
		// AFTER THE DELETION IS MADE (THATS THE PREVIOUS ROW TO THE DELETED ROW)
		this.deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					if (SSDataNavigator.this.confirmDeletes) {
						int answer = JOptionPane.showConfirmDialog(SSDataNavigator.this,
								"Are you sure you want to delete this record?", "Delete Present Record",
								JOptionPane.YES_NO_OPTION);
						if (answer != JOptionPane.YES_OPTION) {
							return;
						}
					}

					if (SSDataNavigator.this.dBNav != null) {
						SSDataNavigator.this.dBNav.performPreDeletionOps();
					}

					if (SSDataNavigator.this.dBNav == null
							|| (SSDataNavigator.this.dBNav != null && SSDataNavigator.this.dBNav.allowDeletion())) {
						
						// CAPTURE CURRENT ROW PRE-DELETION
						int tmpPosition = SSDataNavigator.this.currentRow;
						
						// SET ANTICIPATED ROW COUNT POST-DELETION
						int tmpSize = SSDataNavigator.this.rowCount-1;
						
						// DELETE ROW FROM ROWSET
						SSDataNavigator.this.sSRowSet.deleteRow();
						
						// PERFORM ANY POST DELETION OPS (WHICH MAY INVOLVE REQUERYING WHICH IS NEEDED FOR H2)
						SSDataNavigator.this.dBNav.performPostDeletionOps();
						
						// UPDATE TOTAL ROW COUNT
						SSDataNavigator.this.rowCount=tmpSize;
						
						// TRY TO NAVIGATE TO THE RECORD AFTER THE DELETED RECORD, OTHERWISE GO TO
						// WHATEVER IS THE LAST RECORD
						if (tmpPosition <= SSDataNavigator.this.rowCount && tmpPosition > 0) {
							SSDataNavigator.this.sSRowSet.absolute(tmpPosition);
						} else {
							SSDataNavigator.this.sSRowSet.last();
						}
						
						
						// SEEMS DELETION WAS SUCCESSFULL DECREMENT ROWCOUNT
						/*
						SSDataNavigator.this.rowCount--;
						SSDataNavigator.this.sSRowSet.
						if (!SSDataNavigator.this.sSRowSet.next()) {
							SSDataNavigator.this.sSRowSet.last();
						}
						*/
						
						// UPDATE THE STATUS OF THE NAVIGATOR
						updateNavigator();
					}

				} catch (SQLException se) {
					logger.error("SQL Exception.", se);
					JOptionPane.showMessageDialog(SSDataNavigator.this,
							"Exception occured while deleting row.\n" + se.getMessage());
				}
			}
		});

		// LISTENER FOR THE TEXT FIELD. USER CAN ENTER A ROW NUMBER IN THE TEXT
		// FIELD TO MOVE THE THE SPECIFIED ROW.
		// IF ITS NOT A NUMBER OR IF ITS NOT VALID FOR THE CURRENT SSROWSET
		// NOTHING HAPPENS.
		this.txtCurrentRow.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						int row = Integer.parseInt(SSDataNavigator.this.txtCurrentRow.getText().trim());
						if (row <= SSDataNavigator.this.rowCount && row > 0) {
							SSDataNavigator.this.sSRowSet.absolute(row);
						}
					} catch (Exception e) {
						// do nothing
					}
				}
			}
		});

	}

	/**
	 * Adds the listeners on the SSRowSet used by data navigator.
	 */
	protected class SSDBNavRowSetListener implements RowSetListener {

		@Override
		public void cursorMoved(RowSetEvent rse) {
			// IF THERE ARE ROWS GET THE ROW COUNT
			try {
				updateNavigator();
			} catch (SQLException se) {
				logger.error("SQL Exception.", se);
			}
		}

		@Override
		public void rowChanged(RowSetEvent rse) {
			// DO NOTHING
		}

		@Override
		public void rowSetChanged(RowSetEvent rse) {
			// IF THERE ARE ROWS GET THE ROW COUNT
			try {
				SSDataNavigator.this.sSRowSet.last();
				SSDataNavigator.this.rowCount = SSDataNavigator.this.sSRowSet.getRow();
				SSDataNavigator.this.sSRowSet.first();
				updateNavigator();
			} catch (SQLException se) {
				logger.error("SQL Exception.", se);
			}

		}

	}

	/**
	 * Enables/disables navigation buttons as needed and updates the current row and row count
	 * numbers
	 * @throws SQLException 	SQLException
	 */
	protected void updateNavigator() throws SQLException {
		
		this.currentRow = this.sSRowSet.getRow();
		// SET THE ROW COUNT AS LABEL
		this.lblRowCount.setText("of " + this.rowCount);
		this.txtCurrentRow.setText(String.valueOf(this.currentRow));
		// ENABLE OR DISABLE BUTTONS
		if (this.rowCount == 0) {
			this.firstButton.setEnabled(false);
			this.previousButton.setEnabled(false);
			this.nextButton.setEnabled(false);
			this.lastButton.setEnabled(false);
		} else {
			this.firstButton.setEnabled(true);
			this.previousButton.setEnabled(true);
			this.nextButton.setEnabled(true);
			this.lastButton.setEnabled(true);
		}

		try {
			if (this.sSRowSet.isLast()) {
				this.nextButton.setEnabled(false);
				this.lastButton.setEnabled(false);
			}
			if (this.sSRowSet.isFirst()) {
				this.firstButton.setEnabled(false);
				this.previousButton.setEnabled(false);
			}

		} catch (SQLException se) {
			logger.error("SQL Exception.", se);
		}
	}

// DEPRECATED STUFF....................

	/**
	 * Sets the new SSRowSet for the combo box.
	 *
	 * @param _sSRowSet SSRowSet to which the combo has to update values.
	 *
	 * @deprecated Use {@link #setSSRowSet(SSRowSet _rowset)} instead.
	 */
	@Deprecated
	public void setRowSet(SSRowSet _sSRowSet) {
		setSSRowSet(_sSRowSet);
	}

	/**
	 * Returns the SSRowSet being used.
	 *
	 * @return returns the SSRowSet being used.
	 *
	 * @deprecated Use {@link #getSSRowSet()} instead.
	 */
	@Deprecated
	public SSRowSet getRowSet() {
		return this.sSRowSet;
	}

} // end public class SSDataNavigator extends JPanel {

