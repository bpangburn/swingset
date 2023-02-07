/* *****************************************************************************
 * Copyright (C) 2022, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 * ****************************************************************************/
package com.nqadmin.swingset.demo;

import com.nqadmin.swingset.SSDataGrid;
import com.nqadmin.swingset.SSDataGridHandler;
import com.nqadmin.swingset.SSDataValue;
import com.nqadmin.swingset.SSTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.sql.RowSet;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import org.apache.logging.log4j.Logger;

/**
 * Code to handle insert/delete in GridExamples,
 * uiContainer must have a BorderLayout.
 * 
 * <pre>
 * Some notes for updating DataGrid
 * 
 * TODO:	reconcile SSDBNav and SSDataGridHandler
 *			should be a single interface that can be used for
 *			both navigator/grid
 *
 * Want things like default values, primary key, ... 
 * to be looked up acc'd to database values.
 *
 * There's SSDataGrid.allowDeletion() and SSDataGridHandler.allowDeletion(row)
 * that maybe should be more cooperative?
 *
 * dataGrid.setAllowDeletion is weirdly named. The flag controls
 * AllowCtrl_XDeletion. Maybe the keyboard shortcuts should be "add on"
 * through a swing Action. Instead of messageWindow, how about
 *
 * Keep table.convertRowIndexToModel in mind
 * since glazed doesn't use TableRowSorter may not be an issue.
 * 
 * table.updateUI() all over SSDataGrid stuff, get rid of it,
 * use table model events or whatever. Get rid of table param to SSTableModel.
 * </pre>
 *
 * @author err
 */
class DataGridExampleSupport {
	private final Container uiContainer;
	private final RowSet rowset;
	private final SSDataGrid dataGrid;
	private final Logger logger;

	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	static void setup(Logger logger, Container uiContainer,
			RowSet rowset, SSDataGrid dataGrid,
			int primaryColumn, SSDataValue dataValue,
			String[] columnNames, Object[] defaultValues)
			throws SQLException {
		new DataGridExampleSupport(logger, uiContainer, rowset, dataGrid,
				primaryColumn, dataValue, columnNames, defaultValues);
	}


	DataGridExampleSupport(Logger logger, Container uiContainer,
			RowSet rowset, SSDataGrid dataGrid,
			int primaryColumn, SSDataValue dataValue,
			String[] columnNames, Object[] defaultValues)
			throws SQLException {

		if(!(uiContainer.getLayout() instanceof BorderLayout)) {
			throw new IllegalArgumentException("uiContainer without BorderLayout");
		}

		this.uiContainer = uiContainer;
		this.rowset = rowset;
		this.dataGrid = dataGrid;
		this.logger = logger;

		
		// stuff needed if there's going to be an insertion
		dataGrid.setSSDataGridHandler(new DataGridHandler());
		dataGrid.setPrimaryColumn(primaryColumn);
		dataGrid.setSSDataValue(dataValue);
		dataGrid.setDefaultValues(columnNames, defaultValues);

		setupDebugButtons();
	}


	/**
	 * Based on example4's SSDBNavImpl
	 */
	private class DataGridHandler implements SSDataGridHandler {
		
		@Override
		public boolean allowDeletion(int _row) {
			return true;
		}

		@Override
		public void performPreInsertOps(int _row) {
		}

		@Override
		public void performPostInsertOps(int _row) {
			try {
				rowset.execute();
			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
			}
		}

		@Override
		public void performPreDeletionOps(int _row) {
		}

		@Override
		public void performPostDeletionOps(int _row) {
			try {
				rowset.execute();
			} catch (final SQLException se) {
				logger.error("SQL Exception.", se);
			}
		}
	}

	private void setupDebugButtons() {
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 2));
		uiContainer.add(buttons, BorderLayout.SOUTH);
		AbstractButton button;

		button = new JToggleButton("<html><center>show<br>insert row</center></html>", dataGrid.getInsertion());
		buttons.add(button);
		button.addActionListener((e) -> {
			AbstractButton b = (AbstractButton) e.getSource();
			dataGrid.setInsertion(b.isSelected());
			//b.setText(b.isSelected() ? "showing insert" : "show insert");
		});

		button = new JToggleButton("<html><center>allow<br>deletion</center></html>", dataGrid.isAllowDeletion());
		buttons.add(button);
		button.addActionListener((e) -> {
			AbstractButton b = (AbstractButton) e.getSource();
			dataGrid.setAllowDeletion(b.isSelected());
		});

		button = new JButton("delete");
		buttons.add(button);
		button.addActionListener((e) -> {
			int row = dataGrid.getSelectedRow();
			if(row == -1 || !dataGrid.isAllowDeletion()) {
				JOptionPane.showMessageDialog((Component) e.getSource(),
						row == -1 ? "No row is selected." : "Deletion not allowed",
						"Can not delete", JOptionPane.ERROR_MESSAGE);
			} else {
				SSTableModel model = (SSTableModel) dataGrid.getModel();
				model.deleteRow(row);
			}
		});
	}
}
