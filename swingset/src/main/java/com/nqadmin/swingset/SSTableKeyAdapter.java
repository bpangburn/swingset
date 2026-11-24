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
package com.nqadmin.swingset;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.Date;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.utils.SSUtils;

// SSTableKeyAdapter.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Key adapter for JTable and SSDataGrid that manages cut and paste functionality
 * between a table and either another table or a spreadsheet.
 */
public class SSTableKeyAdapter extends KeyAdapter implements Serializable {

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = SSUtils.getLogger();

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -2748762202415891694L;

	/**
	 * Takes the column number and string value to be set for that column and
	 * converts the string in to appropriate class. The class is found by calling
	 * the getColumnClass() method of the JTable.
	 *
	 * @param _jTable JTable containing target object
	 * @param _column the column number for which new value has to be set.
	 * @param _value  string representation of the new value.
	 *
	 * @return returns the value as a column class object.
	 * @throws Exception catch all exception
	 */
	protected static Object getObjectToSet(final JTable _jTable, final int _column, final String _value) throws Exception {
		// GET THE COLUMN CLASS
		final Class<?> objectClass = _jTable.getColumnClass(_column);
		Object newValue = null;
		try {
			// CONSTRUCT THE OBJECT ONLY IF THE STRING IS NOT NULL
			if (_value != null) {
				// DATE CLASS DOESN'T HAVE A CONSTRUCTOR THAT TAKES A STRING
				if (objectClass.equals(java.sql.Date.class)) {
					newValue = SSTableModel.getSQLDate(_value);
				} else {
					// GET THE CONSTRUCTOR FOR THE CLASS WHICH TAKES A STRING
					final Constructor<?> constructor = objectClass.getConstructor(new Class<?>[] { String.class });

					// CREATE AN INSTANCE OF THE OBJECT
					newValue = constructor.newInstance(new Object[] { _value });
				}
			}		
		} catch (final NoSuchMethodException nsme) {
			logger.warn("No Such Method Exception. Failed to copy data.",  nsme);
			newValue = _value;
		}


		// RETURN THE NEWLY CREATED OBJECT.
		return newValue;

	}

	/**
	 * Formats a grid selection as TSV, with null cells as empty fields rather
	 * than the literal text "null".
	 */
	static String buildClipboardText(final JTable table, final int[] rows, final int[] columns) {
		final StringBuilder text = new StringBuilder();
		for (final int row : rows) {
			for (int j = 0; j < columns.length; j++) {
				if (j > 0) {
					text.append("\t");
				}
				final Object value = table.getValueAt(row, columns[j]);
				if (value != null) {
					text.append(value);
				}
			}
			text.append("\n");
		}
		return text.toString();
	}

	/**
	 * Parses spreadsheet TSV, preserving blank cells and interior blank rows.
	 * A final line separator is not an additional row.
	 */
	static String[][] parseClipboardCells(final String clipboardText) {
		if (clipboardText == null || clipboardText.isEmpty()) {
			return new String[0][];
		}
		final String[] lines = clipboardText.split("\\r\\n|\\n|\\r", -1);
		int rowCount = lines.length;
		if (lines[rowCount - 1].isEmpty()) {
			rowCount--;
		}
		final String[][] rows = new String[rowCount][];
		for (int i = 0; i < rowCount; i++) {
			rows[i] = lines[i].split("\t", -1);
		}
		return rows;
	}

	/** Returns the widest row, including empty trailing cells. */
	static int maxClipboardColumns(final String[][] rows) {
		int columns = 0;
		for (final String[] row : rows) {
			columns = Math.max(columns, row.length);
		}
		return columns;
	}

	/**
	 * Pastes nonempty cells at their clipboard offsets. An empty source cell
	 * leaves its destination unchanged instead of shifting later fields.
	 */
	static void pasteClipboardCells(final JTable table, final String[][] cells,
			final int startRow, final int startColumn, final int numRowsToCopy) throws Exception {
		for (int i = 0; i < numRowsToCopy; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				final String cellText = cells[i][j];
				if (cellText.isEmpty()) {
					continue;
				}
				final Object value = getObjectToSet(table, startColumn + j, cellText);
				table.setValueAt(value, startRow + i, startColumn + j);
			}
		}
	}


	/**
	 * Indicates whether or not row insertions are allowed via cut/copy and paste.
	 */
	protected boolean allowInsertion = false;

	/**
	 * Indicates row used for insertion in SSDataGrid.
	 */
	protected boolean forSSDataGrid = false;

	/**
	 * Off state for copying or pasting.
	 */
	protected int offMask = InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;

	/**
	 * On state for copying or pasting.
	 */
	protected int onMask = InputEvent.CTRL_DOWN_MASK;

	/**
	 * Constructs a KeyAdapter for the JTable.
	 *
	 * @param _jTable JTable for which copy and paste support should be added.
	 */
	public SSTableKeyAdapter(final JTable _jTable) {
		init(_jTable);
	}

	/**
	 * Adds the key listener for the specified JTable.
	 * @param _jTable table for which listener is to be added
	 */
	protected void init(final JTable _jTable) {
		_jTable.addKeyListener(this);
	}

	/**
	 * Invoked when a key is released.
	 */
	@Override
	public void keyReleased(final KeyEvent ke) {
		final JTable jTable = (JTable) ke.getSource();

		logger.debug("Key Released on SSDataGrid. Key Released: {} {}.", () -> ke.getKeyCode(),
				() -> ((ke.getModifiersEx() & (onMask | offMask)) == onMask));

		if (((ke.getModifiersEx() & (onMask | offMask)) == onMask) && (ke.getKeyCode() == KeyEvent.VK_C)) {
			// CHECK IF CONTROL-C IS PRESSED
			// SHIFT OR ALT SHOULD NOT BE DOWN

			// ALERT USER
			logger.debug("Going to handle copy");

			// GET COLUMNS INVOLVED
			final int numRows = jTable.getSelectedRowCount();
			int numColumns = 0;
			if (jTable instanceof SSDataGrid) {
				numColumns = ((SSDataGrid) jTable).getSelectedColumnCount();
			} else {
				numColumns = jTable.getSelectedColumnCount();
			}

			// CHECK IF THERE IS ATLEAST ONE SELECTED CELL.
			// IF NOT NOTHING TO COPY JUST RETURN.
			if ((numRows < 1) || (numColumns < 1)) {
				return;
			}

			// GET THE ROWS AND COLUMNS SELECTED.
			final int[] selectedRows = jTable.getSelectedRows();
			int[] selectedColumns = null;
			if (jTable instanceof SSDataGrid) {
				selectedColumns = ((SSDataGrid) jTable).getSelectedColumns();
			} else {
				selectedColumns = jTable.getSelectedColumns();
			}

			// PRESERVE EMPTY FIELDS WHEN COPYING A GRID SELECTION.
			final String clipboardText = buildClipboardText(jTable, selectedRows, selectedColumns);

			// GET THE SYSTEM CLIPBOARD
			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

			// CREATE A TRANSFERABLE OBJECT
			final StringSelection stringSelection = new StringSelection(clipboardText);

			// COPY THE DATA TO CLIP BOARD
			clipboard.setContents(stringSelection, stringSelection);

		} else if (((ke.getModifiersEx() & (onMask | offMask)) == onMask)
				&& (ke.getKeyCode() == KeyEvent.VK_V)) {
			// CHECK IF CONTROL-V IS PRESSED
			// SHIFT OR ALT SHOULD NOT BE DOWN

			// GET CLIPBOARD
			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

			// GET THE CONTENTS OF THE CLIPBOARD
			final Transferable transferable = clipboard.getContents(this);

			// IF THE CONTENT TYPE SUPPORTS STRING TYPE GET THE DATA.
			if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				// GET DATA FROM CLIPBOARD
				String strData = "";
				try {
					strData = (String) transferable.getTransferData(DataFlavor.stringFlavor);
				} catch (final UnsupportedFlavorException ufe) {
					logger.error("Unsupported Flavor Exception.",  ufe);
					return;
				} catch (final IOException ioe) {
					logger.error("IO Exception.",  ioe);
					return;
				}

				// IF USER HAS NOT SELECTED ANY CELL THEN DO NOT COPY.
				if ((jTable.getSelectedRowCount() < 1) || (jTable.getSelectedColumnCount() < 1)) {
					return;
				}

				// GET THE SELECTED ROWS AND COLUMNS
				final int[] selectedRows = jTable.getSelectedRows();
				final int[] selectedColumns = jTable.getSelectedColumns();

				// PRESERVE BLANK CELLS/ROWS; COUNT THE WIDEST ROW FOR FIT CHECKS.
				final String[][] clipboardRows = parseClipboardCells(strData);
				final int numRows = clipboardRows.length;
				if (numRows == 0) {
					return;
				}
				final int numColumns = maxClipboardColumns(clipboardRows);

				// GET THE NUMBER OF COLUMNS AND ROWS IN THE JTABLE.
				int rowCount = jTable.getRowCount();
				final int columnCount = jTable.getColumnCount();

				if (forSSDataGrid || (jTable instanceof SSDataGrid)) {
					rowCount--;
				}

				// IF THE NUMBER OF COLUMNS NEEDED TO COPY FROM CLIPBOARD
				// IS MORE THAN THAT IN JTABLE CANCEL COPY
				// YOU HAVE TO CHECK THIS FROM THE STARTING SELECTED CELL
				// NO NEED TO ADD ONE FOR SELECTEDCOLUMNS[0] AS THE PASTING STARTS FROM THAT
				// ITSELF
				if (columnCount < (numColumns + selectedColumns[0])) {
					JOptionPane.showMessageDialog(jTable, "There are not enough columns in the table to copy into.\n");
					return;
				}

				int numRowsToCopy = numRows;

				// SEE IF SUFFICENT ROWS ARE PRESENT.
				if (rowCount < (numRows + selectedRows[0])) {
					JOptionPane.showMessageDialog(jTable, "There are not enough rows in the table to copy into.\n");
					if (allowInsertion) {
						final int option = JOptionPane.showConfirmDialog(jTable, "Do you want to insert new rows?",
								"Add Rows", JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							// SET THE NUMBER OF ROWS TO COPY EQUAL TO
							// NUMBER OF ROWS IN DATA.
							numRowsToCopy = numRows;
						} else if (JOptionPane.showConfirmDialog(jTable,
								"Do you want to copy data into just the rows present?", "Limited Copy",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							// GET THE NUMBER OF ROWS FROM SELECTED ROW TO LAST ROW
							// NO NEED TO ADD A ONE
							numRowsToCopy = rowCount - selectedRows[0];
						}
					} else if (JOptionPane.showConfirmDialog(jTable,
							"Do you want to copy data into just the rows present?", "Limited Copy",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						// GET THE NUMBER OF ROWS FROM SELECTED ROW TO LAST ROW
						// NO NEED TO ADD A ONE
						numRowsToCopy = rowCount - selectedRows[0];
					}

				}

				// COPY THE DATA FROM CLIP BOARD TO JTABLE
				try {
					pasteClipboardCells(jTable, clipboardRows, selectedRows[0], selectedColumns[0], numRowsToCopy);

					// UPDATE THE UI AS WE HAVE UPDATED UNDERLIYING DATA
					// THIS HAS TO PROPOGATE TO THE SCREEN.
					jTable.updateUI();

				} catch (final NoSuchMethodException nsme) {
					logger.error("No Such Method Exception. One of the column classes does not provide a constructor that takes a single String argument.",  nsme);
					JOptionPane.showMessageDialog(jTable, "One of the column classes does not provide a constructor that takes a single String argument.");
				} catch (final SecurityException se) {
					logger.error("Security Exception. One of the column class does not provide a constructor that takes a single String argument.",  se);
					JOptionPane.showMessageDialog(jTable, "One of the column class does not provide a constructor that takes a single String argument.");
				} catch (final InstantiationException ie) {
					logger.error("(Instantiation Exception. Failed to copy data. Error occured while instantiating a single String argument constructor for a column.",  ie);
					JOptionPane.showMessageDialog(jTable, "Failed to copy data. Error occured while instantiating a single String argument constructor for a column.");
				} catch (final Exception e) {
					logger.error("Exception. Failed to copy data.",  e);
					JOptionPane.showMessageDialog(jTable, "Failed to copy data.");
				}

			} // end if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {

		} // end if (((ke.getModifiersEx() & (onMask | offMask)) == onMask) &&
			// ke.getKeyCode() == KeyEvent.VK_C) {

	} // end public void keyReleased(KeyEvent ke) {

	/**
	 * Sets allowInsertion indicator. Set true if new rows can be added to JTable
	 * via cut/copy and paste - otherwise false. False by default.
	 *
	 * @param _allowInsertion true if new rows can be added when pasting data from
	 *                        clipboard, else false.
	 */
	public void setAllowInsertion(final boolean _allowInsertion) {
		allowInsertion = _allowInsertion;
	}

	/**
	 * Sets forSSDataGrid indicator. True if the key adapter is used for SSDataGrid
	 * -- otherwise false. False by default.
	 *
	 * @param _forSSDataGrid - true if this key adapter is used for SSDataGrid, else
	 *                       false.
	 */
	public void setForSSDataGrid(final boolean _forSSDataGrid) {
		forSSDataGrid = _forSSDataGrid;
	}

}
