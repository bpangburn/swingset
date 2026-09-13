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
 * copyright (C) 2024-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset;

import static com.nqadmin.swingset.utils.SSUtils.findRowsModel;

import java.awt.Dimension;

import javax.sql.RowSet;

import dev.visdb.seesaw.datasources.DbOps;
import dev.visdb.seesaw.navigate.RowSetState;
import dev.visdb.seesaw.navigate.RowsModel;
import dev.visdb.seesaw.utils.SsDataNavigator;

/**
 * See {@link SsDataNavigator}.
 */
// NOTE: NavigateState.setAutoCommit() is not public and never referenced.
// When auto commit mode is enabled, the navigation buttons remain enabled;
// a navigation automatically commits any changes. Once navigation takes place
// changes can't be reverted using Undo button (has to be done manually by the user).
@SuppressWarnings("serial")
public class SSDataNavigator extends SsDataNavigator {
  /**
   * Constructs the SSDataNavigator with the given RowsModel.
   *
   * @param rowsModel   the RowsModel to which the navigator is bound to
   */
  public SSDataNavigator(RowsModel rowsModel) {
    super(rowsModel, Lines.ONE, null);
  }

  /**
   * Constructs the SSDataNavigator with the given RowsModel.
   *
   * @param rowsModel   the RowsModel to which the navigator is bound to
   * @param nLines      number of display lines for the navigator
   */
  public SSDataNavigator(RowsModel rowsModel, Lines nLines) {
    super(rowsModel, nLines, null);
  }

  /**
   * Constructs the SSDataNavigator with the given RowsModel
   * and sets the size of the buttons on the navigator to the given size
   *
   * @param rowsModel   the RowsModel to which the navigator is bound to
   * @param _buttonSize the size to which the button on navigator have to be set
   */
  public SSDataNavigator(RowsModel rowsModel, Dimension _buttonSize) {
    super(rowsModel, Lines.ONE, _buttonSize);
  }

  /**
   * Constructs the SSDataNavigator with the given RowsModel
   * and sets the size of the buttons on the navigator to the given size
   *
   * @param rowsModel   the RowsModel to which the navigator is bound to
   * @param _buttonSize the size to which the button on navigator have to be set
   * @param nLines      number of display lines for the navigator
   */
  @SuppressWarnings({"LeakingThisInConstructor", "OverridableMethodCallInConstructor"})
  public SSDataNavigator(RowsModel rowsModel, Lines nLines, Dimension _buttonSize) {
    super(rowsModel, nLines, _buttonSize);
  }

  /**
   *
   * @param rowSet
   * @deprecated use RowsModel
   */
  @Deprecated
  public final void setRowSet(final RowSet rowSet) {
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
  @Override
  public final void setRowsModel(RowsModel rowsModel) {
    super.setRowsModel(rowsModel);
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
    return getRowsModel().getCallExecute();
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
    return getRowsModel().getConfirmDeletes();
  }

  /**
   * Returns any custom implementation of the SSDBNav interface, which is used
   * when the insert button is pressed to perform custom actions.
   *
   * @return any custom implementation of the SSDBNav interface
   * @deprecated use {@linkplain RowsModel#getDbOps()}
   */
  @Deprecated
  public DbOps getDBNav() {
    return getRowsModel().getDbOps();
  }

  /**
   * Returns true if deletions are allowed, else false.
   *
   * @return returns true if deletions are allowed, else false.
   * @deprecated use {@linkplain RowsModel#getAllowDelete() }
   */
  @Deprecated
  public boolean getDeletion() {
    return getRowsModel().getAllowDelete();
  }

  /**
   * Returns true if insertions are allowed, else false.
   *
   * @return returns true if insertions are allowed, else false.
   * @deprecated use {@linkplain RowsModel#getAllowInsert()   }
   */
  @Deprecated
  public boolean getInsertion() {
    return getRowsModel().getAllowInsert();
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
    return getRowsModel().getModification();
  }

  /**
   * @return the navCombo
   * @deprecated use {@linkplain RowsModel#getNavCombo() }
   */
  @Deprecated
  public SSDBComboBox getNavCombo() {
    return (SSDBComboBox) getRowsModel().getNavCombo();
  }

  /**
   * Returns true if the RowSet contains one or more rows, else false.
   *
   * @return return true if RowSet contains data else false.
   * @deprecated use {@linkplain RowsModel#containsRows() }
   */
  @Deprecated
  public boolean containsRows() {
    return getRowsModel().containsRows();
  }

  /**
   * @return boolean indicating if the navigator is on an insert row
   * @deprecated use {@linkplain RowsModel#isOnInsertRow()   }
   */
  @Deprecated
  public boolean isOnInsertRow() {
    return getRowsModel().isOnInsertRow();
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
    getRowsModel().setCallExecute(_callExecute);
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
    getRowsModel().setConfirmDeletes(_confirmDeletes);
  }

  /**
   * Function that passes the implementation of the SSDBNav interface. This
   * interface can be implemented by the developer to perform custom actions when
   * the insert button is pressed
   *
   * @param _dBNav implementation of the SSDBNav interface
   * @deprecated use {@linkplain RowsModel#setRowSet(javax.sql.RowSet, com.nqadmin.swingset.SSDBNav) }
   */
  @Deprecated
  public void setDBNav(final DbOps _dBNav) {
    getRowsModel().setDBNav(_dBNav);
  }

  /**
   * Enables or disables the row deletion button. This method should be used if
   * row deletions are not allowed. True by default.
   *
   * @param _deletion indicates whether or not to allow deletions
   * @deprecated use {@linkplain RowsModel#setAllowDelete(boolean)}
   */
  @Deprecated
  public void setDeletion(final boolean _deletion) {
    getRowsModel().setAllowDelete(_deletion);
  }

  /**
   * Enables or disables the row insertion button. This method should be used if
   * row insertions are not allowed. True by default.
   *
   * @param _insertion indicates whether or not to allow insertions
   * @deprecated use {@linkplain RowsModel#setAllowInsert(boolean) }
   */
  @Deprecated
  public void setInsertion(final boolean _insertion) {
    getRowsModel().setAllowInsert(_insertion);
  }

  /**
   * Enables or disables the modification-related buttons on the SSDataNavigator.
   * If the user can only navigate through the records with out making any changes
   * set this to false. By default, the modification-related buttons are enabled.
   *
   * @param _modification indicates whether or not the modification-related
   *                      buttons are enabled.
   * @deprecated use {@linkplain RowsModel#setAllowWrite(boolean) }
   */
  @Deprecated
  public void setModification(final boolean _modification) {
    getRowsModel().setModification(_modification);
  }

  /**
   * @param _navCombo the navCombo to set
   * @deprecated use {@linkplain RowsModel#setNavCombo(com.nqadmin.swingset.SSDBComboBox) }
   */
  @Deprecated
  public void setNavCombo(final SSDBComboBox _navCombo) {
    getRowsModel().setNavCombo(_navCombo);
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
   * @deprecated use {@linkplain RowsModel#commit() }
   */
  @Deprecated
  public boolean updatePresentRow() {
    return getRowsModel().updatePresentRow();
  }

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
  public SSDataNavigator(final RowSet rowSet, final Dimension buttonSize) {
    this(findRowsModel(rowSet), buttonSize);
  }
  
//=====================================================================================
//2026-01-12_BP: The code BELOW is needed for SwingSet 4.0.x compatibility
//===================================================================================== 

  /**
   * Returns the RowSet being used.
   *
   * @return returns the RowSet being used.
   */
  @Deprecated
  public RowSet getRowSet() {
    return getRowsModel() != null ? getRowsModel().getRowSet() : null;
  }

  /**
   * Find out if the specified RowSet is on the insert row.
   * 
   * @param rs get state for this RowSet
   * @return true if on the insert row
   * @deprecated use {@link #isOnInsertRow()}
   */
  @Deprecated
  public static boolean isInserting(RowSet rs) {
    return rs == null ? false : RowSetState.isInserting(rs);
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
//2026-01-12_BP: The code ABOVE is needed for SwingSet 4.0.x compatibility
//=====================================================================================
  
} // end public class SSDataNavigator extends JPanel {
