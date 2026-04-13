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
 * copyright (C) 2025-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.utils;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.System.Logger;
import java.sql.SQLException;
import java.util.Objects;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.SwingUtilities;

import com.nqadmin.swingset.SSDBComboBox;
import com.nqadmin.swingset.navigate.RowsModel;

import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * Used to synchronize a data navigator and a navigation ComboBox.
 * <p>
 * IMPORTANT: The SSDBComboBox and the RowSet queries should select the same
 * records and in the same order. Otherwise the SSSyncManager will spend a lot of
 * time looping through records to match.
 */
// TODO: SSSyncManager combo key is hardcoded to Long.
//       NOTE: note combo listener uses int
public class SSSyncManager
{
	/**
	 * Listener for combo box to update data navigator when combo box-based
	 * navigation occurs.
	 */
	protected class SyncComboListener implements ActionListener
	{

		// WHEN THERE IS A CHANGE IN THIS VALUE MOVE THE ROWSET SO THAT
		// ITS POSITIONED AT THE RIGHT RECORD.
		/** {@inheritDoc } */
		@Override
		public void actionPerformed(ActionEvent ae)
		{
			// Let the combo box close to avoid NetBeans breakpoint hang.
			EventQueue.invokeLater(() -> handleComboEvent(ae));
		}
	} // protected class SyncComboListener implements ActionListener {

	private int actionPerformedCount = 0;
	private void handleComboEvent(@SuppressWarnings("unused") ActionEvent ae)
	{
		// ADD/REMOVE METHODS HAVE A CHECK TO ADD/REMOVE ONLY ONCE
		removeRowsetListener();
		
		try {
			if (Boolean.FALSE) { handleComboEvent01(); handleComboEvent02(); }
			
			//handleComboEvent01();
			
			// This seems the most efficient.
			rowsModel.rsOp(comboBox, () -> handleComboEvent01());
			
			// Individually wrapped.
			//handleComboEvent02();
			
		} catch (final SQLException se) {
			logger.log(ERROR, "SQL Exception.", se);
		} finally {
			logger.log(DEBUG, ()->sf("SyncComboListener actionPerformedCount=%s",
					actionPerformedCount++));
			addRowsetListener();
		}
		
	}

	// direct use of getRowSet()
	private void handleComboEvent01() throws SQLException
	{
		// Nothing to do for an empty/null rowset.
		if ((getRowSet() == null) || (getRowSet().getRow() < 1)) {
			return;
		}
		
		Long comboPK = comboBox.getChosenKey();
		logger.log(DEBUG, ()->sf("COMBO NAVIGATOR: getChosenKey() returned: %s.", comboPK));
		
		// getChosenKey() could return null during initialization.
		// We check for null/empty rowset in the prior block.
		if (comboPK==null) {
			logger.log(WARNING, "Null selected in Combo Navigator.");
			return;
		}
		
		// Note that the rowset count starts at 1 whereas combobox index starts at 0.
		
		final long rowsetPK = getRowSet().getLong(syncColumnName);
		
		if (comboPK != rowsetPK) {
			// Update the present row before moving to another row.
			// This code was removed to improve performance.
			//
			// 2020-12-02_BP: adding back
			// 2021-02-26_BP: moving inside 'if (comboPK != rowsetPK) {' block
			// TODO: does autocommit/canModify/dirty need to be checked?
			rowsModel.updatePresentRow();
			
			final int indexOfPK = comboBox.getKeys().indexOf(comboPK) + 1;
			logger.log(DEBUG, ()->sf("Rowset PK=%s, Combo PK=%s, Target rowset record # should be %s.",
					rowsetPK, comboPK, indexOfPK));
			
			// BUG_Absolute
			getRowSet().absolute(indexOfPK);
			
			final int numRecords = comboBox.getItemCount();
			int count = 0;
			
			// If after positioning the rowset index at the combo index, the values
			// don't match, perform a manual loop to try to find a match presuming
			// records could be added/deleted by other connections, don't loop through
			// all of the records more than once plus a cushion of "OFFSET_TO_CHECK"
			while (comboPK != getRowSet().getLong(syncColumnName)) {
				// BUG_Absolute
				if (!getRowSet().next()) {
					getRowSet().beforeFirst();
					getRowSet().next();
				}
				
				count++;
				
				final int tcount = count;
				logger.log(WARNING, () -> "SSSyncManager RowSet and SSDBComboBox values "
						+ "do not match for the same index. This can be caused by "
						+ "SSDBComboBox and RowSet queries not selecting the same "
						+ "records in the same order. Looping through each record "
						+ "for a match. Check # " + tcount + ".");
				
				// Often records are just slightly out of order so a better
				// strategy would be to move backwards by some small offset
				// and then search forward rather than potentially searching
				// through all records to make a full loop.
				if (count==1) {
					// If there are only a few records, just start at first record
					int rowsetSearchFrom = 1;
					
					if (numRecords>OFFSET_TO_CHECK) {
						rowsetSearchFrom = indexOfPK - OFFSET_TO_CHECK;
					}
					if (rowsetSearchFrom<1) {
						rowsetSearchFrom += numRecords;
					}
					if (rowsetSearchFrom>numRecords) {
						rowsetSearchFrom -= numRecords;
					}
					// BUG_Absolute
					getRowSet().absolute(rowsetSearchFrom);
				}
				
				// number of items in combo is the number of records in resultset.
				// so if for some reason item is in combo but deleted in rowset
				// To avoid infinite loop in such scenario
				if (count > (numRecords + OVERLAP_TO_CHECK)) {
					// TODO: is this needed?
					comboBox.repaint();
					logger.log(WARNING, () -> "SSSyncManager unable to find a record matching the selection in the dropdown list: " + comboBox.getSelectedStringValue() + ".");
					// JOptionPane.showInternalMessageDialog(this,"Record deleted. Info the admin
					// about this","Row not found",JOptionPane.OK_OPTION);
					break;
				}
			}
		}
	}

	// WORKS: rsOp() wrapped use of getRowSet().
	private void handleComboEvent02() throws SQLException
	{
		// Nothing to do for an empty/null rowset.
		if ((getRowSet() == null) || (getRowSet().getRow() < 1)) {
			return;
		}
		
		Long comboPK = comboBox.getChosenKey();
		logger.log(DEBUG, ()->sf("COMBO NAVIGATOR: getChosenKey() returned: %s.", comboPK));
		
		// getChosenKey() could return null during initialization.
		// We check for null/empty rowset in the prior block.
		if (comboPK==null) {
			logger.log(WARNING, "Null selected in Combo Navigator.");
			return;
		}
		
		// Note that the rowset count starts at 1 whereas combobox index starts at 0.
		
		final long rowsetPK = getRowSet().getLong(syncColumnName);
		
		if (comboPK != rowsetPK) {
			// Update the present row before moving to another row.
			// This code was removed to improve performance.
			//
			// 2020-12-02_BP: adding back
			// 2021-02-26_BP: moving inside 'if (comboPK != rowsetPK) {' block
			// TODO: does autocommit/canModify/dirty need to be checked?
			rowsModel.updatePresentRow();
			
			final int indexOfPK = comboBox.getKeys().indexOf(comboPK) + 1;
			logger.log(DEBUG, ()->sf("Rowset PK=%s, Combo PK=%s, Target rowset record # should be %s.",
					rowsetPK, comboPK, indexOfPK));
			
			// BUG_Absolute
			rowsModel.rsOp(SSSyncManager.this, () -> getRowSet().absolute(indexOfPK));
			
			final int numRecords = comboBox.getItemCount();
			int count = 0;
			
			// If after positioning the rowset index at the combo index, the values
			// don't match, perform a manual loop to try to find a match presuming
			// records could be added/deleted by other connections, don't loop through
			// all of the records more than once plus a cushion of "OFFSET_TO_CHECK"
			while (comboPK != getRowSet().getLong(syncColumnName)) {
				// BUG_Absolute
				rowsModel.rsOp(SSSyncManager.this, () -> {
					if (!getRowSet().next()) {
						getRowSet().beforeFirst();
						getRowSet().next();
					}
				});
				
				count++;
				
				final int tcount = count;
				logger.log(WARNING, () -> "SSSyncManager RowSet and SSDBComboBox values "
						+ "do not match for the same index. This can be caused by "
						+ "SSDBComboBox and RowSet queries not selecting the same "
						+ "records in the same order. Looping through each record "
						+ "for a match. Check # " + tcount + ".");
				
				// Often records are just slightly out of order so a better
				// strategy would be to move backwards by some small offset
				// and then search forward rather than potentially searching
				// through all records to make a full loop.
				if (count==1) {
					// If there are only a few records, just start at first record
					int rowsetSearchFrom = 1;
					
					if (numRecords>OFFSET_TO_CHECK) {
						rowsetSearchFrom = indexOfPK - OFFSET_TO_CHECK;
					}
					if (rowsetSearchFrom<1) {
						rowsetSearchFrom += numRecords;
					}
					if (rowsetSearchFrom>numRecords) {
						rowsetSearchFrom -= numRecords;
					}
					// BUG_Absolute
					int from = rowsetSearchFrom;
					rowsModel.rsOp(SSSyncManager.this, () -> getRowSet().absolute(from));
				}
				
				// number of items in combo is the number of records in resultset.
				// so if for some reason item is in combo but deleted in rowset
				// To avoid infinite loop in such scenario
				if (count > (numRecords + OVERLAP_TO_CHECK)) {
					// TODO: is this needed?
					comboBox.repaint();
					logger.log(WARNING, () -> "SSSyncManager unable to find a record matching the selection in the dropdown list: " + comboBox.getSelectedStringValue() + ".");
					// JOptionPane.showInternalMessageDialog(this,"Record deleted. Info the admin
					// about this","Row not found",JOptionPane.OK_OPTION);
					break;
				}
			}
		}
	}

	/**
	 * Listener for rowset.
	 */
	protected class SyncRowSetListener implements RowSetListener {
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
			logger.log(TRACE, "Rowset cursor moved.");
			performUpdates();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void rowChanged(final RowSetEvent rse) {
			logger.log(TRACE, "Rowset row changed.");
			// Do nothing as there is no navigation involved
			//performUpdates();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void rowSetChanged(final RowSetEvent rse) {
			logger.log(TRACE, "Rowset changed.");
			performUpdates();
		}
		
		private void performUpdates() {
			lastChange++;
			logger.log(TRACE, ()->sf("performUpdates(): lastChange=%s, lastNotifiedChange=%s",
					lastChange, lastNotifiedChange));
			
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

	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * # of records to step back if doing a sequential search because
	 * SSDBComboBox and RowSet results don't match.
	 */
	private static final int OFFSET_TO_CHECK = 7;

	/**
	 * # of records of overlap to check if SSDBComboBox and RowSet results
	 * don't match due to record additions/deletions.
	 */
	private static final int OVERLAP_TO_CHECK = 7;

	/** SSDBComboBox used for record navigation. */
	private SSDBComboBox comboBox;

	/** Listener on combo box to detect combo-based navigations. */
	private final SyncComboListener comboListener = new SyncComboListener();
	
	/** Indicates if combo navigator listener is added (or removed) */
	private boolean comboListenerAdded = false;

	private RowsModel rowsModel;

	/** Listener on RowSet to detect data navigator-based navigations. */
	private final SyncRowSetListener rowsetListener = new SyncRowSetListener();
	
	/** Indicates if rowset listener is added (or removed) */
	private boolean rowsetListenerAdded = false;
	

	/** RowSet column used as basis for synchronization. */
	private String syncColumnName;


	/**
	 * Creates a SSSyncManager with the specified combo box and data navigator.
	 *
	 * @param comboBox   SSDBComboBox used for record navigation
	 * @param rowsModel  RowsModel to be synchronized with navigation combo box
	 */
	public SSSyncManager(SSDBComboBox comboBox, RowsModel rowsModel) {
		this.comboBox = comboBox;
		this.rowsModel = rowsModel;
		rowsModel.setNavCombo(comboBox, this);
		if (comboBox.getLogColumnName() == null) {
			comboBox.setLogColumnName(sf("**ComboBoxNavigator@%x**",
					System.identityHashCode(comboBox)));
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
			getRowSet().addRowSetListener(rowsetListener);
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
			if ((getRowSet() != null) && (getRowSet().getRow() > 0)) {
				// GET THE PRIMARY KEY FOR THE CURRENT RECORD IN THE ROWSET
				final Long currentRowPK = getRowSet().getLong(syncColumnName);

				logger.log(DEBUG, ()->sf("SSSyncManager().adjustValue() - RowSet value: %s", currentRowPK));

				// CHECK IF THE COMBO BOX IS DISPLAYING THE SAME ONE.
				if ((comboBox.getSelectedStringValue() == null)
						|| !Objects.equals(comboBox.getChosenKey(), currentRowPK)) {
					// IF NOT CHANGE THE SELECTION OF THE COMBO BOX.
					// this.comboBox.setSelectedStringValue(this.rowset.getString(this.columnName));
					comboBox.setChosenKey(currentRowPK);
				}
			} else {
				//comboBox.setSelectedIndex(-1);
				comboBox.setChosenKey(null);
			}
		} catch (final SQLException se) {
			logger.log(ERROR, "SQL Exception.", se);
		}
		
		comboBox.setEnabled(true);
		addComboListener();
	}

	/**
	 * Stop synchronization between navigation components.
	 */
	public void async() {
		logger.log(DEBUG, "");
		removeListeners();
	}

	private RowSet getRowSet() {
		return rowsModel.getRowSet();
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
			getRowSet().removeRowSetListener(rowsetListener);
			rowsetListenerAdded = false;
		}
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
	 * Sets RowsModel actions to synchronize.
	 *
	 * @param rowsModel rowsModel to be synchronized
	 */
	public void setRowsModel(RowsModel rowsModel) {
		this.rowsModel = rowsModel;
	}
	
	/**
	 * Sets column to be used as basis for synchronization.
	 *
	 * @param _syncColumnName RowSet column used as basis for synchronization.
	 */
	public void setSyncColumnName(final String _syncColumnName) {
		syncColumnName = _syncColumnName;
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
		logger.log(DEBUG, "");
		adjustValue();
		addListeners();
	}

} // end public class SSSyncManager {

