/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 *   Ernie R. Rael
 ******************************************************************************/
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset;

import java.awt.Dimension;
import java.util.Objects;

import javax.sql.RowSet;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

import com.nqadmin.swingset.navigate.NavigateActions;
import com.nqadmin.swingset.navigate.RowNumberSpinner;
import com.nqadmin.swingset.navigate.RowSetState;

import static com.nqadmin.swingset.navigate.NavAction.*;

// SSDataNavigator.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Component that can be used for data navigation. It provides buttons for
 * navigation, insertion, and deletion of records in a RowSet. The
 * modification of a RowSet can be prevented using the setModificaton()
 * method. Any changes made to the columns of a record will be updated whenever
 * there is a navigation.
 * <p>
 * For example if you are displaying three columns using the JTextField and the
 * user changes the text in the text fields then the columns will be updated to
 * the new values when the user navigates the RowSet. If the user wants to
 * revert the changes he made he can press the Undo button, however this must be
 * done before any navigation. Once navigation takes place changes can't be
 * reverted using Undo button (has to be done manually by the user).
 */
@SuppressWarnings("serial")
public class SSDataNavigator extends JPanel
{
	/** Button to add a record to the RowSet. */
	protected final JButton addButton;

	/** Button to commit screen changes to the RowSet. */
	protected final JButton commitButton;

	/** Button to delete the current record in the RowSet. */
	protected final JButton deleteButton;

	/** Button to navigate to the first record in the RowSet. */
	protected final JButton firstButton;

	/** Button to navigate to the last record in the RowSet. */
	protected final JButton lastButton;

	/** Button to navigate to the next record in the RowSet. */
	protected final JButton nextButton;

	/** Button to navigate to the previous record in the RowSet. */
	protected final JButton previousButton;
	
	/** Button to refresh the screen based on any changes to the RowSet. */
	protected final JButton refreshButton;

	/** Button to revert screen changes based on the RowSet. */
	protected final JButton undoButton;

	/** Navigator button dimensions. */
	private Dimension buttonSize = new Dimension(40, 20);

	/** Label to display the total number of records in the RowSet. */
	private final JLabel lblRowCount = new JLabel();

	/** Component for viewing/changing the current record number. */
	protected final RowNumberSpinner rowNumberSpinner;

	/** Current record spinner dimensions. */
	private final Dimension rowSpinnerSize = new Dimension(65, 20);


	/** Get the actions for the buttons from here */
	private NavigateActions navActs;

	/** This panel's original action map is the parent of any navActionMap. */
	private final ActionMap parentActionMap = getActionMap();

	/**
	 * Creates a object of SSDataNavigator. Note: you have to set the RowSet
	 * before you can start using it.
	 */
	public SSDataNavigator() {
		this(null);
	}

	/**
	 * Constructs a SSDataNavigator for the given RowSet
	 *
	 * @param _rowSet The RowSet to which the SSDataNavigator has to be bound
	 */
	public SSDataNavigator(final RowSet _rowSet) {
		this(_rowSet, null);
	}

	/**
	 * Constructs the SSDataNavigator with the given RowSet and sets the size of
	 * the buttons on the navigator to the given size
	 *
	 * @param _rowSet   the RowSet to which the navigator is bound to
	 * @param _buttonSize the size to which the button on navigator have to be set
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public SSDataNavigator(final RowSet _rowSet, final Dimension _buttonSize)
	{
		navActs = NavigateActions.get(_rowSet);
		ActionMap navActionMap = navActs.createActionMap();
		// insert the Actions in front of the original actions
		navActionMap.setParent(parentActionMap);
		setActionMap(navActionMap);

		firstButton =    new JButton(navActionMap.get(NAV_FIRST));
		previousButton = new JButton(navActionMap.get(NAV_PREVIOUS));
		nextButton =     new JButton(navActionMap.get(NAV_NEXT));
		lastButton =     new JButton(navActionMap.get(NAV_LAST));
		commitButton =   new JButton(navActionMap.get(NAV_COMMIT));
		undoButton =     new JButton(navActionMap.get(NAV_UNDO));
		refreshButton =  new JButton(navActionMap.get(NAV_REFRESH));
		addButton =      new JButton(navActionMap.get(NAV_ADD));
		deleteButton =   new JButton(navActionMap.get(NAV_DELETE));

		rowNumberSpinner = new RowNumberSpinner(navActs.getRowNumberModel());
		rowNumberSpinner.setAction(navActionMap.get(NAV_GOTOROW));
		RowNumberSpinner.removeTinyArrows(rowNumberSpinner, rowSpinnerSize);
		//RowNumberSpinner.disableUpDownKeys(rowNumberSpinner);
		RowNumberSpinner.inWindowUpDownKeys(rowNumberSpinner);
		updateLblRowCount();
		rowNumberSpinner.addChangeListener((ChangeEvent e) -> {
			updateLblRowCount();
		});

		if (_buttonSize!=null) {
			buttonSize = _buttonSize;
		}
		
		hideActionText(); // For each nav button, suppress the Action name from appearing next to the icon.
		//addToolTips(); // Integrated into button Action code.
		createPanel();
	}

	/**
	 * Set the navigator to use a different row set;
	 * the navigators ActionMap is changed.
	 *
	 * @param rowSet data for navigator
	 */
	public final void setRowSet(final RowSet rowSet)
	{
		Objects.requireNonNull(rowSet);
		RowSet oldValue = navActs.getRowSet();

		// setup new Actions
		navActs = NavigateActions.get(rowSet);
		ActionMap navActionMap = navActs.createActionMap();

		// make the navigation actions available to this component
		navActionMap.setParent(parentActionMap);
		setActionMap(navActionMap);

		// change the actions to the new rowSet
		firstButton.setAction(navActionMap.get(NAV_FIRST));
		previousButton.setAction(navActionMap.get(NAV_PREVIOUS));
		nextButton.setAction(navActionMap.get(NAV_NEXT));
		lastButton.setAction(navActionMap.get(NAV_LAST));
		commitButton.setAction(navActionMap.get(NAV_COMMIT));
		undoButton.setAction(navActionMap.get(NAV_UNDO));
		refreshButton.setAction(navActionMap.get(NAV_REFRESH));
		addButton.setAction(navActionMap.get(NAV_ADD));
		deleteButton.setAction(navActionMap.get(NAV_DELETE));

		rowNumberSpinner.setModel(navActs.getRowNumberModel());
		rowNumberSpinner.setAction(navActionMap.get(NAV_GOTOROW));
		updateLblRowCount();

		firePropertyChange("rowSet", oldValue, rowSet);
	}

	private void updateLblRowCount()
	{
		lblRowCount.setText("of "
				+ ((SpinnerNumberModel)rowNumberSpinner.getModel()).getMaximum());
	}


	/**
	 * Adds the navigator components to the navigator panel.
	 */
	protected final void createPanel() {

		setButtonSizes();
		// SET THE BOX LAYOUT
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		// ADD BUTTONS TO THE PANEL
		add(firstButton);
		add(previousButton);
		add(rowNumberSpinner);
		add(nextButton);
		add(lastButton);
		add(commitButton);
		add(undoButton);
		add(refreshButton);
		add(addButton);
		add(deleteButton);
		add(lblRowCount);
		// pack();
	}

	/**
	 * Calls the doClick on Add Button.
	 */
	public void doAddButtonClick() {
		addButton.doClick();
	}

	/**
	 * Calls the doClick on Commit Button.
	 */
	public void doCommitButtonClick() {
		commitButton.doClick();
	}

	/**
	 * Calls the doClick on Delete Button.
	 */
	public void doDeleteButtonClick() {
		deleteButton.doClick();
	}

	/**
	 * Calls the doClick on First Button.
	 */
	public void doFirstButtonClick() {
		firstButton.doClick();
	}

	/**
	 * Calls the doClick on Last Button.
	 */
	public void doLastButtonClick() {
		lastButton.doClick();
	}

	/**
	 * Calls the doClick on Next Button.
	 */
	public void doNextButtonClick() {
		nextButton.doClick();
	}

	/**
	 * Calls the doClick on Previous Button.
	 */
	public void doPreviousButtonClick() {
		previousButton.doClick();
	}

	/**
	 * Calls the doClick on Refresh Button.
	 */
	public void doRefreshButtonClick() {
		refreshButton.doClick();
	}

	/**
	 * Calls the doClick on Undo Button.
	 */
	public void doUndoButtonClick() {
		undoButton.doClick();
	}

	/**
	 * Returns the size of buttons on the data navigator.
	 *
	 * @return returns a Dimension object representing the size of each button on
	 *         the data navigator.
	 */
	// TODO: Dimension copy?
	public Dimension getButtonSize() {
		return buttonSize;
	}

	/**
	 * Indicates if the navigator will skip the execute function call on the
	 * underlying RowSet (needed for MySQL - see FAQ).
	 *
	 * @return value of execute() indicator
	 */
	public boolean getCallExecute() {
		return navActs.getCallExecute();
	}

	/**
	 * Returns true if deletions must be confirmed by user, else false.
	 *
	 * @return returns true if a confirmation dialog is displayed when the user
	 *         deletes a record, else false.
	 */
	public boolean getConfirmDeletes() {
		return navActs.getConfirmDeletes();
	}

	/**
	 * Returns any custom implementation of the SSDBNav interface, which is used
	 * when the insert button is pressed to perform custom actions.
	 *
	 * @return any custom implementation of the SSDBNav interface
	 */
	public SSDBNav getDBNav() {
		return navActs.getDBNav();
	}

	/**
	 * Returns true if deletions are allowed, else false.
	 *
	 * @return returns true if deletions are allowed, else false.
	 */
	public boolean getDeletion() {
		return navActs.getDeletion();
	}

	/**
	 * Returns true if insertions are allowed, else false.
	 *
	 * @return returns true if insertions are allowed, else false.
	 */
	public boolean getInsertion() {
		return navActs.getInsertion();
	}

	/**
	 * Returns true if the user can modify the data in the RowSet, else false.
	 *
	 * @return returns true if the user modifications are written back to the
	 *         database, else false.
	 */
	public boolean getModification() {
		return navActs.getModification();
	}

	/**
	 * @return the navCombo
	 */
	public SSDBComboBox getNavCombo() {
		return navActs.getNavCombo();
	}

	/**
	 * Returns the RowSet being used.
	 *
	 * @return returns the RowSet being used.
	 */
	public RowSet getRowSet() {
		return navActs.getRowSet();
	}
	
	/**
	 * Prevent the navigator buttons from displaying the Action name with the icon.
	 */
	private void hideActionText() {
		// HIDE ACTION TEXT FOR BUTTONS
		firstButton.setHideActionText(true);
		previousButton.setHideActionText(true);
		nextButton.setHideActionText(true);
		lastButton.setHideActionText(true);
		commitButton.setHideActionText(true);
		undoButton.setHideActionText(true);
		refreshButton.setHideActionText(true);
		addButton.setHideActionText(true);
		deleteButton.setHideActionText(true);
	}

	/**
	 * @return boolean indicating if the navigator is on an insert row
	 */
	public boolean isOnInsertRow() {
		return RowSetState.isInserting(navActs.getRowSet());
	}

	/**
	 * Sets the preferredSize and the MinimumSize of the buttons to the specified
	 * size
	 *
	 * @param _buttonSize the required dimension of the buttons
	 */
	public void setButtonSize(final Dimension _buttonSize) {
		final Dimension oldValue = buttonSize;
		buttonSize = _buttonSize;
		firePropertyChange("buttonSize", oldValue, buttonSize);
		setButtonSizes();
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
		rowNumberSpinner.setPreferredSize(rowSpinnerSize);
		lblRowCount.setPreferredSize(rowSpinnerSize);
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
		rowNumberSpinner.setMinimumSize(rowSpinnerSize);
		lblRowCount.setMinimumSize(rowSpinnerSize);
	}
	
	/**
	 * Method to cause the navigator to skip the execute() function call on the
	 * underlying RowSet. This is necessary for MySQL (see FAQ).
	 *
	 * @param _callExecute false if using MySQL database - otherwise true
	 */
	public void setCallExecute(final boolean _callExecute) {
		navActs.setCallExecute(_callExecute);
	}

	/**
	 * Sets the confirm deletion indicator. If set to true, every time delete button
	 * is pressed, the navigator pops up a confirmation dialog to the user. Default
	 * value is true.
	 *
	 * @param _confirmDeletes indicates whether or not to confirm deletions
	 */
	public void setConfirmDeletes(final boolean _confirmDeletes) {
		navActs.setConfirmDeletes(_confirmDeletes);
	}

	/**
	 * Function that passes the implementation of the SSDBNav interface. This
	 * interface can be implemented by the developer to perform custom actions when
	 * the insert button is pressed
	 *
	 * @param _dBNav implementation of the SSDBNav interface
	 */
	public void setDBNav(final SSDBNav _dBNav) {
		navActs.setDBNav(_dBNav);
	}

	/**
	 * Enables or disables the row deletion button. This method should be used if
	 * row deletions are not allowed. True by default.
	 *
	 * @param _deletion indicates whether or not to allow deletions
	 */
	public void setDeletion(final boolean _deletion) {
		navActs.setDeletion(_deletion);
	}

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
	public void setFocusable(final boolean focusable) {
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
		rowNumberSpinner.setFocusable(focusable);
	}

	/**
	 * Enables or disables the row insertion button. This method should be used if
	 * row insertions are not allowed. True by default.
	 *
	 * @param _insertion indicates whether or not to allow insertions
	 */
	public void setInsertion(final boolean _insertion) {
		navActs.setInsertion(_insertion);
	}

	/**
	 * Enables or disables the modification-related buttons on the SSDataNavigator.
	 * If the user can only navigate through the records with out making any changes
	 * set this to false. By default, the modification-related buttons are enabled.
	 *
	 * @param _modification indicates whether or not the modification-related
	 *                      buttons are enabled.
	 */
	public void setModification(final boolean _modification) {
		navActs.setModification(_modification);
	}

	/**
	 * @param _navCombo the navCombo to set
	 */
	public void setNavCombo(final SSDBComboBox _navCombo) {
		navActs.setNavCombo(_navCombo);
	}

	/**
	 * Writes the present row back to the RowSet. This is done automatically when
	 * any navigation takes place, but can also be called manually.
	 *
	 * @return returns true if update succeeds else false.
	 */
	public boolean updatePresentRow() {
		return navActs.updatePresentRow();
	}

} // end public class SSDataNavigator extends JPanel {

