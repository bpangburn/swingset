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
import java.util.StringTokenizer;

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
					newValue = Date.valueOf(_value);
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
		final StringBuilder strBuf = new StringBuilder();

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

			// COPY THE DATA IN THE SELECTED ROWS AND COLUMNS
			// APPEND A TAB AFTER EACH CELL AND A NEW LINE CHAR AT END OF EACH ROW.
			for (int i = 0; i < selectedRows.length; i++) {
				for (int j = 0; j < selectedColumns.length; j++) {
					strBuf.append(jTable.getValueAt(selectedRows[i], selectedColumns[j]));
					if (j < (numColumns - 1)) {
						strBuf.append("\t");
					}
				}
				strBuf.append("\n");
			}

			// GET THE SYSTEM CLIPBOARD
			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

			// CREATE A TRANSFERABLE OBJECT
			final StringSelection stringSelection = new StringSelection(strBuf.toString());

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

				// CREATE A STRING TOKENIZER TO BREAK THE DATA INTO ROWS.
				StringTokenizer rowTokens = new StringTokenizer(strData, "\n", false);

				// INITIALIZE THE NUMBER OF ROWS AND COLUMNS
				int numRows = 0;
				int numColumns = 0;

				StringTokenizer columnTokens;

				// SEE IF THE DATA HAS ATLEAST ONE ROW
				// IF IT DOES GET THE NUMBER OF COLUMNS PRESENT.
				if (rowTokens.hasMoreTokens()) {
					numRows++;
					columnTokens = new StringTokenizer(rowTokens.nextToken(), "\t", false);
					// WHILE THERE ARE TOKENS INCREMENT THE COLUMN COUNT
					for (; columnTokens.hasMoreTokens(); numColumns++) {
						columnTokens.nextToken();
					}
				}

				// GET THE NUMBER OF ROWS PRESENT
				for (; rowTokens.hasMoreTokens(); numRows++) {
					rowTokens.nextToken();
				}

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
					// TOKENIZE DATA IN TO ROWS
					rowTokens = new StringTokenizer(strData, "\n", false);

					// WHILE THERE ARE ROWS IN THE CLIP BOARD DATA AND NUMBER OF ROWS
					// COPIED IS LESS THEN WE SHOULD COPY DATA TO JTABLE.
					for (int i = 0; rowTokens.hasMoreTokens() && (i < numRowsToCopy); i++) {
						// TOKENIZE THE ROW INFORMATION IN TO COLUMNS
						columnTokens = new StringTokenizer(rowTokens.nextToken(), "\t", false);
						// PASTE THE DATA IN TO JTABLE ROW.
						for (int j = 0; columnTokens.hasMoreTokens(); j++) {
							// GET THE VALUE FOR THIS CELL IN THE FORM OF THIS COLUMN CLASS OBJECT
							final Object newValue = getObjectToSet(jTable, selectedColumns[0] + j, columnTokens.nextToken());
							// SET THE VALUE FOR THE COLUMN
							jTable.setValueAt(newValue, selectedRows[0] + i, selectedColumns[0] + j);
						}
					}

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
