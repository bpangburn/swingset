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
import com.nqadmin.swingset.datasources.RowSetOps;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.logging.log4j.Logger;

/**
 * Add some buttons at the bottom of a DataGrid example
 * to invoke certain features.
 * uiContainer must have a BorderLayout.
 * There's code to handle insert/delete with H2.
 */
class DataGridExampleSupport {
	private final Container uiContainer;
	private final RowSet rowset;
	private final SSDataGrid dataGrid;
	private final Logger logger;

	/** assumes rowset has been set */
	static void setup(Logger logger, Container uiContainer,
			RowSet rowset, SSDataGrid dataGrid,
			int primaryColumn, SSDataValue dataValue, String[] columnNames, Object[] defaultValues)
			throws SQLException {
		DataGridExampleSupport dges = new DataGridExampleSupport(
				logger, uiContainer, rowset, dataGrid);
		dges.init(primaryColumn, dataValue, columnNames, defaultValues);
	}


	private DataGridExampleSupport(Logger _logger, Container _uiContainer,
			RowSet _rowset, SSDataGrid _dataGrid)
			throws SQLException {

		if(!(_uiContainer.getLayout() instanceof BorderLayout)) {
			throw new IllegalArgumentException("uiContainer without BorderLayout");
		}

		this.uiContainer = _uiContainer;
		this.rowset = _rowset;
		this.dataGrid = _dataGrid;
		this.logger = _logger;

	}
	
	private void init(int primaryColumn, SSDataValue dataValue,
			String[] columnNames, Object[] defaultValues)
			throws SQLException {
		// stuff needed if there's going to be an insertion
		dataGrid.setSSDataGridHandler(new DataGridHandler());
		dataGrid.setPrimaryColumn(primaryColumn);
		dataGrid.setSSDataValue(dataValue);
		dataGrid.setDefaultValues(columnNames, defaultValues);

		TableColumnModel cm = dataGrid.getColumnModel();
		for (int i = 1; i <= cm.getColumnCount(); i++) {
			TableColumn c = cm.getColumn(i-1);
			c.setIdentifier(RowSetOps.getColumnName(rowset, i));
			System.err.println("id: " + c.getIdentifier());
		}

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

	private void setShowHideSorting(AbstractButton b) {
		dataGrid.setSorting(b.isSelected());
		b.setText("<html><center>"
				+ "sorting"
				+ "<br>"
				+ "[" + (b.isSelected() ? "disable" : "enable") + "]"
				+ "</center></html>"
		);
	}

	private void setShowHideInsertion(AbstractButton b) {
		dataGrid.setInsertion(b.isSelected());
		b.setText("<html><center>"
				+ "insert row"
				+ "<br>"
				+ "[" + (b.isSelected() ? "hide" : "show") + "]"
				+ "</center></html>");
	}

	private void setAllowDelete(AbstractButton b) {
		dataGrid.setAllowDeletion(b.isSelected());
		b.setText("<html><center>"
				+ "deletion"
				+ "<br>"
				+ "[" + (b.isSelected() ? "disable" : "enable") + "]"
				+ "</center></html>");
	}

	@SuppressWarnings("deprecation")
	private void setupDebugButtons() {
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(2, 3));
		uiContainer.add(buttons, BorderLayout.SOUTH);
		AbstractButton button;

		// Toggle sorting
		button = new JToggleButton();
		button.setSelected(dataGrid.getSorting());
		setShowHideSorting(button);
		buttons.add(button);
		button.addActionListener((e) -> {
			setShowHideSorting((AbstractButton) e.getSource());
		});

		// Toggle insert row
		button = new JToggleButton();
		button.setSelected(dataGrid.getInsertion());
		setShowHideInsertion(button);
		buttons.add(button);
		button.addActionListener((e) -> {
			setShowHideInsertion((AbstractButton) e.getSource());
		});

		// Toggle allow deletion
		button = new JToggleButton();
		button.setSelected(dataGrid.isAllowDeletion());
		setAllowDelete(button);
		buttons.add(button);
		button.addActionListener((e) -> {
			setAllowDelete((AbstractButton) e.getSource());
		});

		if(Boolean.FALSE) {
			// trigger for random debug stuff
			button = new JButton("trigger");
			buttons.add(button);
			button.addActionListener((ActionEvent e) -> {
				System.err.println("BANG");
				List<Integer> cols = new ArrayList<>();
				for(int col : dataGrid.getSelectedColumns())
					cols.add(col);
				
				System.err.println("selCols: " + cols);
				//outputColInfo();
			});
		}

		// delete selected row
		button = new JButton("delete");
		buttons.add(button);
		button.addActionListener((e) -> {
			int rowSel = dataGrid.getSelectedRow();
			int row = rowSel != -1 ? dataGrid.convertRowIndexToModel(rowSel) : -1;
			if(row == -1 || !dataGrid.isAllowDeletion()) {
				String msg = row == -1
						? "No row is selected."
						: String.format("Deletion (%d --> %d) no allowed", rowSel, row);
				JOptionPane.showMessageDialog((Component) e.getSource(),
						msg, "Can not delete", JOptionPane.ERROR_MESSAGE);
			} else {
				SSTableModel model = (SSTableModel) dataGrid.getModel();
				model.deleteRow(row);
			}
		});

		// Toggle hide columns
		button = new JButton("<html><center>hide<br>cols</center></html>");
		buttons.add(button);
		button.addActionListener((e) -> {
			System.err.println("Changing hidden column info");
			//System.err.println("BEFORE:");
			//outputColInfo();
			// Alternate between column index and column name
			// and check out using 'null' for an array.
			int[] i_hide_cols = new int[0]; // don't hide anything
			String[] s_hide_cols = new String[0];
			if(!hidden[0]) {
				indexOrName = (indexOrName + 1) % 4;
				i_hide_cols = new int[] { 1, 3 };
				s_hide_cols = new String[i_hide_cols.length];
				// convert int column to name column
				int i = 0;
				for(int colIdx : i_hide_cols) {
					try {
						s_hide_cols[i++] = RowSetOps.getColumnName(rowset, colIdx+1);
					} catch (SQLException ex) {
						throw new IllegalStateException("SQL: " + ex.getMessage());
					}
				}
			}
			switch(indexOrName) {
				case 0:
				case 2:
					if(indexOrName == 2 && i_hide_cols.length == 0)
						i_hide_cols = null;
					System.err.println("setHiddenColumns(int)"
						+ (i_hide_cols == null ? " null" : ""));
					dataGrid.setHiddenColumns(i_hide_cols);
					break;
				default:
				case 1:
				case 3:
					if(indexOrName == 3 && s_hide_cols.length == 0)
						s_hide_cols = null;
					System.err.println("setHiddenColumns(string)"
						+ (s_hide_cols == null ? " null" : ""));
					try {
						dataGrid.setHiddenColumns(s_hide_cols);
					} catch (SQLException ex) {
						throw new IllegalStateException("SQL: " + ex.getMessage());
					}
					break;
			}
			hidden[0] = ! hidden[0];
			//System.err.println("AFTER:");
			//outputColInfo();
			System.err.println("Hidden: " + hidden[0]);
		});
		if(Boolean.FALSE) outputColInfo();
	}
	boolean[] hidden = new boolean[1];
	int indexOrName = 0;

	void outputColInfo() {
		for (TableColumn col : dataGrid.getColumnsList()) {
			System.err.printf("id: %s, widths: max %d, min %d, pref %d, set %d,\n",
					col.getIdentifier(), col.getMaxWidth(), col.getMinWidth(),
					col.getPreferredWidth(), col.getWidth());
		}
	}
}
