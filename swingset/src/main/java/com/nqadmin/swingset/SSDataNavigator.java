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
import java.util.List;
import java.util.Objects;

import javax.sql.RowSet;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

import com.nqadmin.swingset.navigate.RowNumberSpinner;
import com.nqadmin.swingset.navigate.RowsModel;

import static com.nqadmin.swingset.navigate.RowsAction.*;
import static com.nqadmin.swingset.utils.SSUtils.findRowsModel;

/**
 * Component that can be used for data navigation. It provides buttons for
 * navigation, insertion, and deletion of records in a RowSet. The
 * modification of a RowSet can be prevented using the setModificaton()
 * method. Any changes made to the columns of a record are updated by
 * the commit button; there's row undo and re-read table (re-execute query).
 * <p>
 * For example if you are displaying three columns using the JTextField and the
 * user changes the text in the text fields then the columns will be updated to
 * the new values when the user presses commit. If the user wants to
 * revert the changes he made he can press the Undo button.
 * <p>
 * Normally the navigation buttons are disabled if the row is modified.
 * When auto commit mode is enabled, the navigation buttons remain enabled;
 * a navigation automatically commits any changes. Once navigation takes place
 * changes can't be reverted using Undo button (has to be done manually by the user).
 */
@SuppressWarnings("serial")
public class SSDataNavigator extends JPanel
{
	/** The RowSet's actions/models for the buttons are in here. */
	private RowsModel rowsModel;

	/** This panel's original action map is the parent of any navActionMap. */
	private final ActionMap parentActionMap;
	private final ActionMap navActionMap;

	/**
	 * Constructs a SSDataNavigator for the given RowSet
	 *
	 * @param rowSet
	 * @deprecated use RowsModel
	 */
	@Deprecated
	public SSDataNavigator(final RowSet rowSet) {
		this(rowSet, null);
	}

	/**
	 * @param rowSet
	 * @param buttonSize
	 * @deprecated use RowsModel
	 */
	@Deprecated
	public SSDataNavigator(final RowSet rowSet, final Dimension buttonSize)
	{
		this(findRowsModel(rowSet), buttonSize);
	}

	/**
	 * Constructs the SSDataNavigator with the given RowsModel.
	 *
	 * @param rowsModel   the RowsModel to which the navigator is bound to
	 */
	public SSDataNavigator(RowsModel rowsModel)
	{
		this(rowsModel, null);
	}

	/**
	 * Constructs the SSDataNavigator with the given RowsModel and sets the size of
 the buttons on the navigator to the given size
	 *
	 * @param rowsModel   the RowsModel to which the navigator is bound to
	 * @param _buttonSize the size to which the button on navigator have to be set
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public SSDataNavigator(RowsModel rowsModel, Dimension _buttonSize)
	{
		Objects.requireNonNull(rowsModel);
		rowNumberSpinner = new RowNumberSpinner(rowsModel);
		uiComponents = uiComponents();
		uiButtons = uiButtons();

		parentActionMap = getActionMap();
		navActionMap = new ActionMap();
		// Insert the navigate actions in front of the original actions.
		navActionMap.setParent(parentActionMap);
		setActionMap(navActionMap);

		rowSpinnerSize = new Dimension(65, 20);
		buttonSize = _buttonSize == null ? new Dimension(40, 20) : _buttonSize;

		rowNumberSpinner.removeTinyArrows(rowSpinnerSize);
		rowNumberSpinner.setWindowUpDownKeysEnable(true);
		rowNumberSpinner.addChangeListener((ChangeEvent e) -> {
			updateLblRowCount();
		});

		setRowsModel(rowsModel);
		hideActionText(); // suppress the Action name from appearing next to the button icon.
		createPanel();
	}

	/**
	 * 
	 * @param rowSet 
	 * @deprecated use RowsModel
	 */
	@Deprecated
	public final void setRowSet(final RowSet rowSet)
	{
		setRowsModel(findRowsModel(rowSet));
	}


	/**
	 * Set the navigator to use a different RowsModel ;
	 * swap in the new navigate ActionMap.
	 *
	 * @param rowsModel data for navigator
	 * @deprecated maybe temporarily, use RowsModel.setRowSet
	 */
	// TODO: setModel(RowsModel)
	@Deprecated
	public final void setRowsModel(RowsModel rowsModel)
	{
		Objects.requireNonNull(rowsModel);
		if (this.rowsModel != null)
			throw new IllegalStateException("RowsModel already set");
		this.rowsModel = rowsModel;

		installRowsModel(rowsModel);
	}

	/**
	 * Set the navigator to use a different row set;
	 * swap in the new navigate ActionMap.
	 *
	 * @param RowsModel for navigator
	 */
	private void installRowsModel(RowsModel rowsModel)
	{
		// Fill Actions for the navigator with actions from the new rowsModel/RowSet.
		rowsModel.fillNavActionMap(navActionMap);

		// set the actions to the new navAction
		firstButton.setAction(navActionMap.get(ACT_FIRST));
		previousButton.setAction(navActionMap.get(ACT_PREVIOUS));
		nextButton.setAction(navActionMap.get(ACT_NEXT));
		lastButton.setAction(navActionMap.get(ACT_LAST));
		commitButton.setAction(navActionMap.get(ACT_COMMIT));
		undoButton.setAction(navActionMap.get(ACT_REVERT));
		refreshButton.setAction(navActionMap.get(ACT_REFRESH));
		addButton.setAction(navActionMap.get(ACT_ADD));
		deleteButton.setAction(navActionMap.get(ACT_DELETE));

		updateLblRowCount();
	}

	private void updateLblRowCount()
	{
		Comparable<?> max = rowNumberSpinner.getModel().getMaximum();
		lblRowCount.setText(max != null ? "of " + max : "");
	}

	/**
	 * Adds the navigator components to the navigator panel.
	 */
	protected final void createPanel() {

		setButtonSizes();
		// SET THE BOX LAYOUT
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		// ADD BUTTONS TO THE PANEL
		uiComponents.forEach(uiItem -> add(uiItem));
	}

	/**
	 * Use the up/down arrow keys while this spinner's window is focused
	 * to adjust row number.
	 * 
	 * @param enable true enables up/down keys when window has focus
	 */
	public void setWindowUpDownKeysEnable(boolean enable) {
		rowNumberSpinner.setWindowUpDownKeysEnable(enable);
	}

	// There used to be a bunch of "do*ButtonClick()" methods; not used anywhere.
	// Could replace with "doButtonClick(RowsAction)" if needed for whatever.

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
	 * Return the RowsModel for this navigator.
	 * @return 
	 */
	public RowsModel getRowsModel()
	{
		return rowsModel;
	}
	
	/**
	 * Prevent the navigator buttons from displaying the Action name with the icon.
	 */
	private void hideActionText() {
		uiButtons.forEach(uiItem -> uiItem.setHideActionText(true));
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
		uiButtons.forEach(uiItem -> uiItem.setFocusable(focusable));
		rowNumberSpinner.setFocusable(focusable);
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
		uiButtons.forEach(uiItem -> uiItem.setPreferredSize(buttonSize));
		rowNumberSpinner.setPreferredSize(rowSpinnerSize);
		lblRowCount.setPreferredSize(rowSpinnerSize);

		lblRowCount.setHorizontalAlignment(SwingConstants.CENTER);

		uiButtons.forEach(uiItem -> uiItem.setMinimumSize(buttonSize));
		rowNumberSpinner.setMinimumSize(rowSpinnerSize);
		lblRowCount.setMinimumSize(rowSpinnerSize);
	}

	/**
	 * Indicates if the navigator will skip the execute function call on the
	 * underlying RowSet (needed for MySQL - see FAQ).
	 *
	 * @return value of execute() indicator
	 * @deprecated use {@linkplain RowsModel#getCallExecute()  }
	 */
	@Deprecated
	public boolean getCallExecute() {
		return rowsModel.getCallExecute();
	}

	/**
	 * Returns true if deletions must be confirmed by user, else false.
	 *
	 * @return returns true if a confirmation dialog is displayed when the user
	 *         deletes a record, else false.
	 * @deprecated use {@linkplain RowsModel#getConfirmDeletes() }
	 */
	@Deprecated
	public boolean getConfirmDeletes() {
		return rowsModel.getConfirmDeletes();
	}

	/**
	 * Returns any custom implementation of the SSDBNav interface, which is used
	 * when the insert button is pressed to perform custom actions.
	 *
	 * @return any custom implementation of the SSDBNav interface
	 * @deprecated use {@linkplain RowsModel#getDBNav() }
	 */
	@Deprecated
	public SSDBNav getDBNav() {
		return rowsModel.getDBNav();
	}

	/**
	 * Returns true if deletions are allowed, else false.
	 *
	 * @return returns true if deletions are allowed, else false.
	 * @deprecated use {@linkplain RowsModel#getDeletion()   }
	 */
	@Deprecated
	public boolean getDeletion() {
		return rowsModel.getDeletion();
	}

	/**
	 * Returns true if insertions are allowed, else false.
	 *
	 * @return returns true if insertions are allowed, else false.
	 * @deprecated use {@linkplain RowsModel#getInsertion()   }
	 */
	@Deprecated
	public boolean getInsertion() {
		return rowsModel.getInsertion();
	}

	/**
	 * Returns true if the user can modify the data in the RowSet, else false.
	 *
	 * @return returns true if the user modifications are written back to the
	 *         database, else false.
	 * @deprecated use {@linkplain RowsModel#getModification()   }
	 */
	@Deprecated
	public boolean getModification() {
		return rowsModel.getModification();
	}

	/**
	 * @return the navCombo
	 * @deprecated use {@linkplain RowsModel#getNavCombo() }
	 */
	@Deprecated
	public SSDBComboBox getNavCombo() {
		return rowsModel.getNavCombo();
	}

	/**
	 * Returns true if the RowSet contains one or more rows, else false.
	 *
	 * @return return true if RowSet contains data else false.
	 * @deprecated use {@linkplain RowsModel#containsRows() }
	 */
	@Deprecated
	public boolean containsRows()
	{
		return rowsModel.containsRows();
	}

	/**
	 * @return boolean indicating if the navigator is on an insert row
	 * @deprecated use {@linkplain RowsModel#isOnInsertRow()   }
	 */
	@Deprecated
	public boolean isOnInsertRow() {
		return rowsModel.isOnInsertRow();
	}
	
	/**
	 * Method to cause the navigator to skip the execute() function call on the
	 * underlying RowSet. This is necessary for MySQL (see FAQ).
	 *
	 * @param _callExecute false if using MySQL database - otherwise true
	 * @deprecated use {@linkplain RowsModel#setCallExecute(boolean)  }
	 */
	@Deprecated
	public void setCallExecute(final boolean _callExecute) {
		rowsModel.setCallExecute(_callExecute);
	}

	/**
	 * Sets the confirm deletion indicator. If set to true, every time delete button
	 * is pressed, the navigator pops up a confirmation dialog to the user. Default
	 * value is true.
	 *
	 * @param _confirmDeletes indicates whether or not to confirm deletions
	 * @deprecated use {@linkplain RowsModel#setConfirmDeletes(boolean)   }
	 */
	@Deprecated
	public void setConfirmDeletes(final boolean _confirmDeletes) {
		rowsModel.setConfirmDeletes(_confirmDeletes);
	}

	/**
	 * Function that passes the implementation of the SSDBNav interface. This
	 * interface can be implemented by the developer to perform custom actions when
	 * the insert button is pressed
	 *
	 * @param _dBNav implementation of the SSDBNav interface
	 * @deprecated use {@linkplain RowsModel#setDBNav(com.nqadmin.swingset.SSDBNav)}
	 */
	@Deprecated
	public void setDBNav(final SSDBNav _dBNav) {
		rowsModel.setDBNav(_dBNav);
	}

	/**
	 * Enables or disables the row deletion button. This method should be used if
	 * row deletions are not allowed. True by default.
	 *
	 * @param _deletion indicates whether or not to allow deletions
	 * @deprecated use {@linkplain RowsModel#setDeletion(boolean)}
	 */
	@Deprecated
	public void setDeletion(final boolean _deletion) {
		rowsModel.setDeletion(_deletion);
	}

	/**
	 * Enables or disables the row insertion button. This method should be used if
	 * row insertions are not allowed. True by default.
	 *
	 * @param _insertion indicates whether or not to allow insertions
	 * @deprecated use {@linkplain RowsModel#setInsertion(boolean) }
	 */
	@Deprecated
	public void setInsertion(final boolean _insertion) {
		rowsModel.setInsertion(_insertion);
	}

	/**
	 * Enables or disables the modification-related buttons on the SSDataNavigator.
	 * If the user can only navigate through the records with out making any changes
	 * set this to false. By default, the modification-related buttons are enabled.
	 *
	 * @param _modification indicates whether or not the modification-related
	 *                      buttons are enabled.
	 * @deprecated use {@linkplain RowsModel#setModification(boolean) }
	 */
	@Deprecated
	public void setModification(final boolean _modification) {
		rowsModel.setModification(_modification);
	}

	/**
	 * @param _navCombo the navCombo to set
	 * @deprecated use {@linkplain RowsModel#setNavCombo(com.nqadmin.swingset.SSDBComboBox) }
	 */
	@Deprecated
	public void setNavCombo(final SSDBComboBox _navCombo) {
		rowsModel.setNavCombo(_navCombo);
	}

	/**
	 * Writes the present row back to the RowSet.
	 * 
	 * This is typically done when commit it pressed,
	 * but it may be done programmaticaly.
	 * 
	 * //		This is done automatically when
	 * //		any navigation takes place, but can also be called manually.
	 *
	 * @return returns true if update succeeds else false.
	 * @deprecated use {@linkplain RowsModel#updatePresentRow() }
	 */
	@Deprecated
	public boolean updatePresentRow() {
		return rowsModel.updatePresentRow();
	}

	//////////////////////////////////////////////////////////////////////
	//
	// Components/dimensions
	//

	/** Button to add a record to the RowSet. */
	protected final JButton addButton = new JButton();

	/** Button to commit screen changes to the RowSet. */
	protected final JButton commitButton = new JButton();

	/** Button to delete the current record in the RowSet. */
	protected final JButton deleteButton = new JButton();

	/** Button to navigate to the first record in the RowSet. */
	protected final JButton firstButton = new JButton();

	/** Button to navigate to the last record in the RowSet. */
	protected final JButton lastButton = new JButton();

	/** Button to navigate to the next record in the RowSet. */
	protected final JButton nextButton = new JButton();

	/** Button to navigate to the previous record in the RowSet. */
	protected final JButton previousButton = new JButton();
	
	/** Button to refresh the screen based on any changes to the RowSet. */
	protected final JButton refreshButton = new JButton();

	/** Button to revert screen changes based on the RowSet. */
	protected final JButton undoButton = new JButton();

	/** Navigator button dimensions. */
	private Dimension buttonSize;

	/** Label to display the total number of records in the RowSet. */
	private final JLabel lblRowCount = new JLabel();

	/** Component for viewing/changing the current record number. */
	protected final RowNumberSpinner rowNumberSpinner;

	/** Current record spinner dimensions. */
	private final Dimension rowSpinnerSize;

	private final List<JComponent> uiComponents;
	private final List<AbstractButton> uiButtons;

	/** These are added in order to this JPanel */
	private final List<JComponent> uiComponents()
	{
		return List.of(
				firstButton,
				previousButton,
				rowNumberSpinner,
				nextButton,
				lastButton,
				commitButton,
				undoButton,
				refreshButton,
				addButton,
				deleteButton,
				lblRowCount
		);
	}

	/** The buttons can often be handled en masse */
	private final List<AbstractButton> uiButtons()
	{
		return List.of(
				firstButton,
				previousButton,
				nextButton,
				lastButton,
				commitButton,
				undoButton,
				refreshButton,
				addButton,
				deleteButton
		);
	}
	
	
//=====================================================================================
// 2026-01-12_BP: The code BELOW is needed for SwingSet 4.0.x compatibility
//=====================================================================================	
	
	/**
	 * Returns the RowSet being used.
	 *
	 * @return returns the RowSet being used.
	 */
	@Deprecated
	public RowSet getRowSet() {
		return rowsModel != null ? rowsModel.getRowSet() : null;
	}
	
	/**
	 * Find out if the specified RowSet is on the insert row.
	 * @param rs get state for this RowSet
	 * @return true if on the insert row
	 * @deprecated use {@link #isOnInsertRow()}
	 */
	@Deprecated
	public static boolean isInserting(RowSet rs) {
		return rs == null ? false : com.nqadmin.swingset.navigate.RowSetState.isInserting(rs);
	}
	
	/**
	 * Calls the doClick on Add Button.
	 */
	@Deprecated
	public void doAddButtonClick() {
		addButton.doClick();
	}

	/**
	 * Calls the doClick on Commit Button.
	 */
	@Deprecated
	public void doCommitButtonClick() {
		commitButton.doClick();
	}

	/**
	 * Calls the doClick on Delete Button.
	 */
	@Deprecated
	public void doDeleteButtonClick() {
		deleteButton.doClick();
	}

	/**
	 * Calls the doClick on First Button.
	 */
	@Deprecated
	public void doFirstButtonClick() {
		firstButton.doClick();
	}

	/**
	 * Calls the doClick on Last Button.
	 */
	@Deprecated
	public void doLastButtonClick() {
		lastButton.doClick();
	}

	/**
	 * Calls the doClick on Next Button.
	 */
	@Deprecated
	public void doNextButtonClick() {
		nextButton.doClick();
	}

	/**
	 * Calls the doClick on Previous Button.
	 */
	@Deprecated
	public void doPreviousButtonClick() {
		previousButton.doClick();
	}

	/**
	 * Calls the doClick on Refresh Button.
	 */
	@Deprecated
	public void doRefreshButtonClick() {
		refreshButton.doClick();
	}

	/**
	 * Calls the doClick on Undo Button.
	 */
	@Deprecated
	public void doUndoButtonClick() {
		undoButton.doClick();
	}
	
//=====================================================================================
// 2026-01-12_BP: The code ABOVE is needed for SwingSet 4.0.x compatibility
//=====================================================================================			


} // end public class SSDataNavigator extends JPanel {

