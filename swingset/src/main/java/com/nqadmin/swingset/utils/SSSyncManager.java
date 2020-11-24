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
 *   Ernie R. Rael
 ******************************************************************************/
package com.nqadmin.swingset.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.datasources.SSRowSet;

// SSSyncManager.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to synchronize a data navigator and a navigation combobox.
 * <p>
 * IMPORTANT: The SSDBComboBox and the SSRowSet queries should select the same
 * records and in the same order. Otherwise the SSSyncManager will spend a lot of
 * time looping through records to match.
 */
public class SSSyncManager {

	/**
	 * Listener for combo box to update data navigator when combo box-based
	 * navigation occurs.
	 */
	protected class SyncComboListener implements ActionListener {
		
		int actionPerformedCount = 0;

		protected long comboPK = -1;

		// WHEN THERE IS A CHANGE IN THIS VALUE MOVE THE ROWSET SO THAT
		// ITS POSITIONED AT THE RIGHT RECORD.
		@Override
		public void actionPerformed(final ActionEvent ae) {

			rowset.removeRowSetListener(rowsetListener);

			try {
				// IF THIS IS NOT CAUSED BY THE USER ACTION (IN WHICH THE FOCUS WILL BE ON THE COMBO) THERE IS NOTHING TO DO
				if ((rowset == null) || (rowset.getRow() < 1) || (comboBox.getSelectedIndex() == -1)
						/* || comboBox.textField == null */
						/* || comboBox.isBoundTextFieldNull() */
						/* || !comboBox.hasFocus() */ // with rewrite, the editor and not the combo likely has the focus
						) {

					return;
				}

				comboPK = comboBox.getSelectedValue();


				// UPDATE THE PRESENT ROW BEFORE MOVING TO ANOTHER ROW.
				// This code was removed to improve performance.
				// dataNavigator.updatePresentRow();


				// Note that the rowset count starts at 1 whereas combobox index starts at 0.

				final long rowsetPK = rowset.getLong(columnName);

				if (comboPK != rowsetPK) {
					// long indexOfId = SSSyncManager.this.comboBox.itemMap.get(this.id) + 1;
					final int indexOfPK = comboBox.getMappings().indexOf(comboPK) + 1;
					//int index = (int) indexOfPK;
					logger.debug("Rowset PK=" + rowsetPK + ", Combo PK=" + comboPK + ", Target rowset record # should be " + indexOfPK + ".");
					rowset.absolute(indexOfPK);
					final int numRecords = comboBox.getItemCount();
					int count = 0;

					// IF AFTER POSITIONING THE ROWSET INDEX AT THE COMBO INDEX, THE VALUES DON'T MATCH,
					// PERFORM A MANUAL LOOP TO TRY TO FIND A MATCH
					// PRESUMING RECORDS COULD BE ADDED/DELETED BY OTHER CONNECTIONS, DON'T LOOP
					// THROUGH ALL OF THE RECORDS MORE THAN ONCE PLUS A CUSHION OF overlapToCheck
					while (comboPK != rowset.getLong(columnName)) {
						if (!rowset.next()) {
							rowset.beforeFirst();
							rowset.next();
						}

						count++;

						logger.warn(
								"SSSyncManager SSRowSet and SSDBComboBox values do not match for the same index. This can be caused by SSDBComboBox and SSRowSet "
								+ " queries not selecting the same records in the same order. Looping through each record for a match. Pass # "
										+ count + ".");

						// Often records are just slightly out of order so a better strategy would be to move backwards by some small offset
						// and then search forward rather than potentially searching through all records to make a full loop.
						if (count==1) {
							// If there are only a few records, just start at first record
							int rowsetSearchFrom = 1;

							if (numRecords>offsetToCheck) {
								rowsetSearchFrom = indexOfPK - offsetToCheck;
							}
							if (rowsetSearchFrom<1) {
								rowsetSearchFrom += numRecords;
							}
							if (rowsetSearchFrom>numRecords) {
								rowsetSearchFrom -= numRecords;
							}
							rowset.absolute(rowsetSearchFrom);
						}


						// number of items in combo is the number of records in resultset.
						// so if for some reason item is in combo but deleted in rowset
						// To avoid infinite loop in such scenario
						if (count > (numRecords + overlapToCheck)) {
							comboBox.repaint();
							logger.warn("SSSyncManager unable to find a record matching the selection in the dropdown list: " + comboBox.getSelectedStringValue() + ".");
							// JOptionPane.showInternalMessageDialog(this,"Record deleted. Info the admin
							// about this","Row not found",JOptionPane.OK_OPTION);
							break;
						}
					}
				}


			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
			} finally {
				logger.debug("SyncComboListener actionPerformedCount=" + actionPerformedCount++);
				rowset.addRowSetListener(rowsetListener);
			}
		}
	} // protected class MyComboListener implements ActionListener {

	/**
	 * Listener for rowset.
	 */
	protected class SyncRowSetListener implements RowSetListener {

		@Override
		public void cursorMoved(final RowSetEvent rse) {
			adjustValue();
		}

		/**
		 * When the database row changes we want to trigger a change to the bound
		 * combo navigator.
		 * <p>
		 * In SSDataNavigator, when a navigation is performed (first, previous,
		 * next, last) a call is made to updateRow() to flush the rowset to the 
		 * underlying database prior to a call to first(), previous(), next(),
		 * or last(). updateRow() triggers rowChanged, but we don't want to update
		 * the combo navigator for the database flush.
		 * <p>
		 * Calls to first(), previous(), next(), and last() trigger cursorMoved.
		 * For a navigation we will updated the combo navigator following cursorMoved.
		 * <p>
		 * In JdbcRowSetImpl, notifyRowChanged() is called for insertRow(),
		 * updateRow(), deleteRow(), & cancelRowUpdates(). We only want to block
		 * combo navigator updates for calls resulting from updateRow().
		 */
		@Override
		public void rowChanged(final RowSetEvent rse) {
			if (!rowset.isUpdatingRow()) {
				adjustValue();
			}
		}

		@Override
		public void rowSetChanged(final RowSetEvent rse) {
			adjustValue();
		}

	}

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * # of records to step back if doing a sequential search because SSDBComboBox and SSRowSet results don't match.
	 */
	protected static final int offsetToCheck = 7;

	/**
	 * # of records of overlap to check if SSDBComboBox and SSRowSet results don't match due to record additions/deletions.
	 */
	protected static final int overlapToCheck = 7;

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
	 * <p>
	 * Creates a SSSyncManager with the specified combo box and data navigator.
	 *
	 * @param _comboBox      SSDBComboBox used for record navigation
	 * @param _dataNavigator SSDataNavigator to be synchronized with navigation
	 *                       combo box
	 */
	public SSSyncManager(final SSDBComboBox _comboBox, final SSDataNavigator _dataNavigator) {
		comboBox = _comboBox;
		dataNavigator = _dataNavigator;
		rowset = dataNavigator.getSSRowSet();
		dataNavigator.setNavCombo(comboBox);
	}

	/**
	 * Adds listeners to combobox & rowset.
	 */
	private void addListeners() {
		comboBox.addActionListener(comboListener);
		dataNavigator.getSSRowSet().addRowSetListener(rowsetListener);
	}

	/**
	 * <p>
	 * Method to update combo box based on rowset.
	 */
	protected void adjustValue() {

		comboBox.removeActionListener(comboListener);

		try {
			if ((rowset != null) && (rowset.getRow() > 0)) {
				// GET THE PRIMARY KEY FOR THE CURRENT RECORD IN THE ROWSET
				final long currentRowPK = rowset.getLong(columnName);

				logger.debug("SSSyncManager().adjustValue() - RowSet value: " + currentRowPK);

				// CHECK IF THE COMBO BOX IS DISPLAYING THE SAME ONE.
				if ((comboBox.getSelectedStringValue() == null)
						|| (comboBox.getSelectedValue()!=currentRowPK)) {
					// IF NOT CHANGE THE SELECTION OF THE COMBO BOX.
					// this.comboBox.setSelectedStringValue(this.rowset.getString(this.columnName));
					comboBox.setSelectedValue(currentRowPK);
				}
			} else {
				comboBox.setSelectedIndex(-1);
			}
		} catch (final SQLException se) {
			logger.error("SQL Exception.", se);
		}
		comboBox.setEnabled(true);
		comboBox.addActionListener(comboListener);
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
		comboBox.removeActionListener(comboListener);
		dataNavigator.getSSRowSet().removeRowSetListener(rowsetListener);
	}

	/**
	 * Sets column to be used as basis for synchronization.
	 *
	 * @param _columnName SSRowSet column used as basis for synchronization.
	 */
	public void setColumnName(final String _columnName) {
		columnName = _columnName;
	}

	/**
	 * Sets combo box to be synchronized.
	 *
	 * @param _comboBox combo box to be synchronized
	 */

	public void setComboBox(final SSDBComboBox _comboBox) {
		comboBox = _comboBox;
	}

	/**
	 * Sets data navigator to be synchronized.
	 *
	 * @param _dataNavigator data navigator to be synchronized
	 */
	public void setDataNavigator(final SSDataNavigator _dataNavigator) {
		dataNavigator = _dataNavigator;
		rowset = dataNavigator.getSSRowSet();
	}

	/**
	 * Start synchronization between navigation components.
	 */
	public void sync() {
		addListeners();
		adjustValue();
	}

} // end public class SSSyncManager {

