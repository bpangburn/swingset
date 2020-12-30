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
import java.io.Serializable;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.models.SSListItem;

// SSSyncManager.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to synchronize a data navigator and a navigation combobox.
 * <p>
 * IMPORTANT: The SSDBComboBox and the RowSet queries should select the same
 * records and in the same order. Otherwise the SSSyncManager will spend a lot of
 * time looping through records to match.
 */
public class SSSyncManager {

	/**
	 * Listener for combo box to update data navigator when combo box-based
	 * navigation occurs.
	 */
	protected class SyncComboListener implements ActionListener {
		
		private int actionPerformedCount = 0;
		
		// **** GL STRICT/CONTAINS ****
		//
		// Hopefully lastValidItem can be eliminated once GlazedLists fully supports STRICT/CONTAINS
		private SSListItem lastValidItem = null;

		private Long comboPK;

		// WHEN THERE IS A CHANGE IN THIS VALUE MOVE THE ROWSET SO THAT
		// ITS POSITIONED AT THE RIGHT RECORD.
		@Override
		public void actionPerformed(final ActionEvent ae) {

			// ADD/REMOVE METHODS HAVE A CHECK TO ADD/REMOVE ONLY ONCE
			removeRowsetListener();

			try {
				// NOTHING TO DO FOR AN EMPTY/NULL ROWSET
				if ((rowset == null) || (rowset.getRow() < 1)) {
					return;
				}

				comboPK = comboBox.getSelectedMapping();
				logger.debug("COMBO NAVIGATOR: getSelectedMapping() returned: {}.", () -> comboPK);
				
				// getSelectedMapping() could return null during initialization.
				// We check for null/empty rowset in the prior block.
				if (comboPK==null) {
					// **** GL STRICT/CONTAINS ****
					//
					// Hopefully lastValidItem can be eliminated once GlazedLists fully supports STRICT/CONTAINS
					if (lastValidItem!=null) {
					// WE GET A NULL PK WHEN THE USER CLEARS THE COMBO EDITOR. NORMALLY lastValidItem WILL BE THE VERY FIRST RECORD IF THIS HAPPENS.
						comboBox.setSelectedItem(lastValidItem); 
					}
					return;
				}
				
				// **** GL STRICT/CONTAINS ****
				//
				// Hopefully selectedItem and lastValidItem can be eliminated once GlazedLists fully supports STRICT/CONTAINS
					// EXTRACT AND STORE SELECTED ITEM
					Object selectedItem = comboBox.getSelectedItem();
						
					// THIS SHOULD ALWAYS BE A SSLISTITEM, BUT COULD BE SOME EDGE CASES?
					if (selectedItem instanceof SSListItem) {
						lastValidItem = (SSListItem)selectedItem;
					} else {
						logger.warn(" -- Selected Item is not a SSListItem.");
					}

				// UPDATE THE PRESENT ROW BEFORE MOVING TO ANOTHER ROW.
				// This code was removed to improve performance.
				//
				// 2020-12-02_BP: adding back
				dataNavigator.updatePresentRow();

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
								"SSSyncManager RowSet and SSDBComboBox values do not match for the same index. This can be caused by SSDBComboBox and RowSet "
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
				addRowsetListener();
			}
		}
	} // protected class SyncComboListener implements ActionListener {

	/**
	 * Listener for rowset.
	 */
	protected class SyncRowSetListener implements RowSetListener, Serializable {

		/**
		 * unique serial ID 
		 */
		private static final long serialVersionUID = -7584919356924575482L;
		
		/**
		 * variables needed to consolidate multiple calls
		 */
		private int lastChange = 0;
		private int lastNotifiedChange = 0;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void cursorMoved(final RowSetEvent event) {
			logger.trace("Rowset cursor moved.");
			performUpdates();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void rowChanged(final RowSetEvent rse) {
			logger.trace("Rowset row changed.");
			// Do nothing as there is no navigation involved
			//performUpdates();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void rowSetChanged(final RowSetEvent rse) {
			logger.trace("Rowset changed.");
			performUpdates();
		}
		
		private void performUpdates() {
			lastChange++;
			logger.trace("performUpdates(): lastChange=" + lastChange
					+ ", lastNotifiedChange=" + lastNotifiedChange);
			
			// Delay execution of logic until all listener methods are called for current event
			// Based on: https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
			SwingUtilities.invokeLater(() -> {
				if (lastNotifiedChange != lastChange) {
					lastNotifiedChange = lastChange;

					adjustValue();
				}
			});
		}

	}

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * # of records to step back if doing a sequential search because SSDBComboBox and RowSet results don't match.
	 */
	private static final int offsetToCheck = 7;

	/**
	 * # of records of overlap to check if SSDBComboBox and RowSet results don't match due to record additions/deletions.
	 */
	private static final int overlapToCheck = 7;

	/**
	 * RowSet column used as basis for synchronization.
	 */
	private String columnName;

	/**
	 * SSDBComboBox used for record navigation.
	 */
	private SSDBComboBox comboBox;

	/**
	 * Listener on combo box to detect combo-based navigations.
	 */
	private final SyncComboListener comboListener = new SyncComboListener();
	
	/**
	 * Indicates if combo navigator listener is added (or removed)
	 */
	private boolean comboListenerAdded = false;

	/**
	 * SSDataNavigator to be synchronized with navigation combo box.
	 */
	private SSDataNavigator dataNavigator;

	/**
	 * RowSet navigated with data navigator and combo box.
	 */
	private RowSet rowset;

	/**
	 * Listener on RowSet to detect data navigator-based navigations.
	 */
	private final SyncRowSetListener rowsetListener = new SyncRowSetListener();
	
	/**
	 * Indicates if rowset listener is added (or removed)
	 */
	private boolean rowsetListenerAdded = false;

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
		rowset = dataNavigator.getRowSet();
		dataNavigator.setNavCombo(comboBox);
		if (_comboBox.getLogColumnName() == null) {
			_comboBox.setLogColumnName(String.format("**ComboBoxNavigator@%x**",
					System.identityHashCode(_comboBox)));
		}
	}
	
	/**
	 * Adds listener to the combo navigator
	 */
	private void addComboListener() {
		if (!comboListenerAdded) {
			comboBox.addActionListener(comboListener);
			comboListenerAdded = true;
		}
	}

	/**
	 * Adds listeners to combobox & rowset.
	 */
	private void addListeners() {
		addComboListener();
		addRowsetListener();
	}
	
	/**
	 * Adds listener to the rowset
	 */
	private void addRowsetListener() {
		if (!rowsetListenerAdded) {
			dataNavigator.getRowSet().addRowSetListener(rowsetListener);
			rowsetListenerAdded = true;
		}
	}

	/**
	 * Method to update combo box based on rowset.
	 */
	protected void adjustValue() {
		
//		// Ignore rowset listener calls triggered during row insertion or while navigator is calling updateRow() on rowset.
//		if (dataNavigator.isOnInsertRow()) {
//			return;
//		}

		removeComboListener();

		try {
			if ((rowset != null) && (rowset.getRow() > 0)) {
				// GET THE PRIMARY KEY FOR THE CURRENT RECORD IN THE ROWSET
				final Long currentRowPK = rowset.getLong(columnName);

				logger.debug("SSSyncManager().adjustValue() - RowSet value: " + currentRowPK);

				// CHECK IF THE COMBO BOX IS DISPLAYING THE SAME ONE.
				if ((comboBox.getSelectedStringValue() == null)
						|| (comboBox.getSelectedMapping()!=currentRowPK)) {
					// IF NOT CHANGE THE SELECTION OF THE COMBO BOX.
					// this.comboBox.setSelectedStringValue(this.rowset.getString(this.columnName));
					comboBox.setSelectedMapping(currentRowPK);
				}
			} else {
				comboBox.setSelectedIndex(-1);
			}
		} catch (final SQLException se) {
			logger.error("SQL Exception.", se);
		}
		
		comboBox.setEnabled(true);
		addComboListener();
	}

	/**
	 * Stop synchronization between navigation components.
	 */
	public void async() {
		logger.debug("");
		removeListeners();
	}

	/**
	 * Removes listener from the combo navigator
	 */
	private void removeComboListener() {
		if (comboListenerAdded) {
			comboBox.removeActionListener(comboListener);
			comboListenerAdded = false;
		}
	}

	/**
	 * Removes listeners from combobox & rowset.
	 */
	private void removeListeners() {
		removeComboListener();
		removeRowsetListener();
	}
	
	/**
	 * Removes listener from the rowset
	 */
	private void removeRowsetListener() {
		if (rowsetListenerAdded) {
			dataNavigator.getRowSet().removeRowSetListener(rowsetListener);
			rowsetListenerAdded = false;
		}
	}

	/**
	 * Sets column to be used as basis for synchronization.
	 *
	 * @param _columnName RowSet column used as basis for synchronization.
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
		rowset = dataNavigator.getRowSet();
	}

	/**
	 * Start synchronization between navigation components.
	 * 
	 * This aligns the combo navigator with the data navigator and turns on the listeners.
	 */
	public void sync() {
		// 2020-12-02_BP:
		//   Seems we should adjustValue() and then call addListeners() because adjustValue() starts by removing the combo listener.
		//   However since adjustValue() was removing/adding the combo listener, it was getting called twice.
		//   Added methods for adding/removing combo and rowset listeners along with booleans to indicate state.
		logger.debug("");
		adjustValue();
		addListeners();
	}

} // end public class SSSyncManager {

