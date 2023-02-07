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

import java.awt.Component;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.sql.RowSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.utils.SSUtils;

// SSTableModel.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * SSTableModel provides an implementation of the TableModel interface. The
 * SSDataGrid uses this class for providing a grid view for a RowSet.
 * SSTableModel can be used without the SSDataGrid (e.g. in conjunction with a
 * JTable), but the cell renderers and hidden columns features of the SSDataGrid
 * will not be available.
 * <p>
 * SSTableModel can be used with a JTable to get a Grid view of the data.
 */
public class SSTableModel extends AbstractTableModel {

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = SSUtils.getLogger();

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -7650858998003486418L;

	/**
	 * Returns an SQL date for a string date formatted as "MM/dd/yyyy".
	 *
	 * @param _strDate String containing a date in "MM/dd/yyyy" format.
	 *
	 * @return String date reformatted as an SQL date
	 */
	protected static Date getSQLDate(final String _strDate) {

		// remove any leading/trailing spaces (e.g., could be introduced from
		// copy/paste)
		String newStrDate = _strDate.trim();

		// check for empty string
		if (newStrDate.equals("")) {
			return null;
		}

		// REMOVE ANY SPACES IF ANY (This could happen if copying from another
		// application)
		// _strDate = _strDate.trim();
		// String newStrDate = _strDate;
		if (newStrDate.indexOf("/") != -1) {
			final StringTokenizer strtok = new StringTokenizer(newStrDate, "/", false);
			final String month = strtok.nextToken();
			final String day = strtok.nextToken();
			newStrDate = strtok.nextToken() + "-" + month + "-" + day;
		}
		return Date.valueOf(newStrDate);
	}

	/**
	 * Indicator to determine if insertions are allowed.
	 */
	protected boolean allowInsertion = true;

	/**
	 * Implementation of SSCellEditing interface used to determine dynamically if a
	 * given cell can be edited and to determine if a given value is valid.
	 */
	protected SSCellEditing cellEditing = null;

	/**
	 * Number of columns in the RowSet.
	 */
	protected transient int columnCount = 0;

	/**
	 * Window where messages should be displayed.
	 */
	protected transient Component component = null;

	/**
	 * Implementation of DataGridHandler interface used to determine dynamically if
	 * a given row can be deleted, and what to do before and after a row is added or
	 * removed.
	 */
	protected SSDataGridHandler dataGridHandler = null;

	/**
	 * Implementation of SSDataValue interface used to determine PK value for new
	 * rows.
	 */
	protected SSDataValue dataValue = null;

	/**
	 * Map to store the default values of different columns.
	 */
	protected HashMap<Integer, Object> defaultValuesMap = null;

	/**
	 * JTable headers.
	 */
	protected transient String[] headers = null;

	/**
	 * List of hidden columns.
	 */
	protected int[] hiddenColumns = null;

	/**
	 * Indicator to determine if the RowSet is on the insertion row.
	 */
	protected boolean inInsertRow = false;

	/**
	 * Column containing primary key.
	 */
	private int primaryColumn = -1;

	/**
	 * Number of rows in the RowSet.
	 */
	protected transient int rowCount = 0;

	protected RowSet rowset = null;

	/**
	 * JTable being modeled.
	 */
	protected transient JTable table = null;

	/**
	 * List of uneditable columns.
	 */
	protected int[] uneditableColumns = null;

	/**
	 * Constructs a SSTableModel object. If this contructor is used the
	 * setRowSet() method has to be used to set the RowSet before constructing
	 * the JTable.
	 */
	public SSTableModel() {
		// Note that call to parent default constructor is implicit.
		//super();
	}

	/**
	 * Constructs a SSTableModel object with the given RowSet. This will call the
	 * execute method on the given RowSet.
	 *
	 * @param _rowset RowSet object whose records has to be displayed in JTable.
	 */
	public SSTableModel(final RowSet _rowset) {
		this();
		rowset = _rowset;
		init();
	}

	/**
	 * Deletes the specified row from the database. The rows are numbered as: 0, 1,
	 * ..., n-1
	 *
	 * @param _row the row number that has to be deleted.
	 *
	 * @return returns true on succesful deletion else false.
	 */
	public boolean deleteRow(final int _row) {
		if (dataGridHandler != null) {
			dataGridHandler.performPreDeletionOps(_row);
		}
		if (_row < rowCount) {
			try {
				if ((dataGridHandler != null) && !dataGridHandler.allowDeletion(_row)) {
					return false;
				}
				rowset.absolute(_row + 1);
				rowset.deleteRow();
				rowCount--;
				if (dataGridHandler != null) {
					dataGridHandler.performPostDeletionOps(_row);
				}
				fireTableRowsDeleted(_row, _row);
				return true;
			} catch (final SQLException se) {
				logger.error("SQL Exception while deleting row.",  se);
				if (component != null) {
					JOptionPane.showMessageDialog(component, "Error while deleting row.\n" + se.getMessage());
				}

			}
		}

		return false;

	} // end public boolean deleteRow(int _row) {

	/**
	 * Returns the type for the column specified for the current view.
	 *
	 * @param _column the column in the view being queried
	 *
	 * @return type for the specified column (first column is 0)
	 */
	@Override
	public Class<?> getColumnClass(final int _column) {
		
		// TODO May be able to utilize JDBCType Enum here.
		// TODO This may be better as a static method in RowSetOps
		
		int type;
		try {
			//type = rowset.getColumnType(_column + 1);
			type = RowSetOps.getColumnType(rowset, _column + 1);
		} catch (final SQLException se) {
			logger.debug("SQL Exception.",  se);
			return super.getColumnClass(_column);
		}

		switch (type) {
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			return Integer.class;

		case Types.BIGINT:
			return Long.class;

		case Types.FLOAT:
			return Float.class;

		case Types.DOUBLE:
		case Types.NUMERIC:
			return Double.class;

		case Types.BOOLEAN:
		case Types.BIT:
			return Boolean.class;

		case Types.DATE:
			return java.sql.Date.class;

		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return String.class;

		default:
			return Object.class;
		}

	} // end public Class getColumnClass(int _column) {

	/**
	 * Returns the number of columns in the model. A JTable uses this method to
	 * determine how many columns it should create and display by default.
	 *
	 * @return the number of columns in the SSTableModel
	 */
	@Override
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * Returns the name of the column appearing in the view at column position
	 * column.
	 *
	 * @param _columnNumber the column in the view being queried
	 *
	 * @return the name of the column at the position specified for the current vew
	 *         where column numbering begins at 0
	 */
	@Override
	public String getColumnName(final int _columnNumber) {
		if (headers != null) {
			if (_columnNumber < headers.length) {
				logger.debug("Sending header " + headers[_columnNumber]);
				return headers[_columnNumber];
			}
		}
		logger.warn("Not able to supply header name.");
		return "";
	}

	/**
	 * Returns the default value inforce for the requested column.
	 * <p>
	 * The type of object is same as returned by getColumnClass in JTable.
	 *
	 * @param _columnNumber the column number for which default value is needed.
	 *
	 * @return returns a object representing the default value.
	 */
	public Object getDefaultValue(final int _columnNumber) {
		Object value = null;
		if (defaultValuesMap != null) {
			value = defaultValuesMap.get(new Integer(_columnNumber));
		}
		return value;
	}

	/**
	 * Returns the number of rows in the model. A JTable uses this method to
	 * determine how many rows it should display.
	 *
	 * @return the number of rows in the SSTableModel
	 */
	@Override
	public int getRowCount() {
		// RETURN THE NUMBER OF ROWS AS ONE GREATER THAN THOSE IN DATABASE
		// ITS USED FOR INSERTING NEW ROWS
		if (allowInsertion) {
			return rowCount + 1;
		}
		// IF INSERTION IS NOT ALLOWED THEN RETURN THE ACTUAL ROW COUNT
		return rowCount;
	}

	/**
	 * Returns the value for the cell at the specified row and column.
	 *
	 * @param _row    the row whose value to be queried.
	 * @param _column the column whose value to be queried.
	 *
	 * @return value at the requested cell.
	 */
	@Override
	public Object getValueAt(final int _row, final int _column) {

		Object value = null;
		if (_row == rowCount) {
			value = getDefaultValue(_column);
			return value;
		}

		try {
			// ROW NUMBERS IN SSROWSET START FROM 1 WHERE AS ROW NUMBERING FOR JTABLE START
			// FROM 0
			rowset.absolute(_row + 1);

			// IF IT IS NULL RETURN NULL
			if (rowset.getObject(_column + 1) == null) {
				return null;
			}
			
			// TODO May be able to utilize JDBCType Enum here.
			// TODO This may be better as a static method in RowSetOps. Could use getObject() and instanceof.

			// COLUMN NUMBERS IN SSROWSET START FROM 1 WHERE AS COLUMN NUMBERING FOR JTABLE
			// START FROM 0
			//final int type = rowset.getColumnType(_column + 1);
			final int type = RowSetOps.getColumnType(rowset, _column + 1);
			switch (type) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				value = new Integer(rowset.getInt(_column + 1));
				break;
			case Types.BIGINT:
				value = new Long(rowset.getLong(_column + 1));
				break;
			case Types.FLOAT:
				value = new Float(rowset.getFloat(_column + 1));
				break;
			case Types.DOUBLE:
			case Types.NUMERIC:
				value = new Double(rowset.getDouble(_column + 1));
				break;
			case Types.BOOLEAN:
			case Types.BIT:
				value = new Boolean(rowset.getBoolean(_column + 1));
				break;
			case Types.DATE:
			case Types.TIMESTAMP:
				value = rowset.getDate(_column + 1);
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				value = rowset.getString(_column + 1);
				break;
			default:
				logger.warn("Unknown data type of " + type);
			}
		} catch (final SQLException se) {
			logger.error("SQL Exception while retrieving value.",  se);
			if (component != null) {
				JOptionPane.showMessageDialog(component, "Error while retrieving value.\n" + se.getMessage());
			}

		}

		return value;

	} // end public Object getValueAt(int _row, int _column) {

	/**
	 * Initializes the SSTableModel. (Gets the column count and row count for the
	 * given RowSet.)
	 */
	protected void init() {
		try {

			//columnCount = rowset.getColumnCount();
			columnCount = RowSetOps.getColumnCount(rowset);
			rowset.last();
			// ROWS IN THE SSROWSET ARE NUMBERED FROM 1, SO LAST ROW NUMBER GIVES THE
			// ROW COUNT
			rowCount = rowset.getRow();
			rowset.first();

			// *** Following code added 11-01-2004 per forum suggestion from Diego Gil (dags).
			// IF DATA CHANGES, ALERT LISTENERS
			fireTableDataChanged();
			// *** End addition

		} catch (final SQLException se) {
			logger.error("SQL Exception.",  se);
		}
	}

	/**
	 * Inserts a new row into the database. While doing so it inserts all the
	 * defaults provided by user and if the primary column is specified along with
	 * an SSDataValue implementation then the primary column value will be inserted.
	 *
	 * @param _value  value entererd of a column
	 * @param _column the column number for which the value is entered.
	 */
	protected void insertRow(final Object _value, final int _column) {
		if (_value == null) {
			return;
		}
		if (dataGridHandler != null) {
			dataGridHandler.performPreInsertOps(rowCount);
		}

		try {
			// IF NOT ON INSERT ROW MOVE TO INSERT ROW.
			if (!inInsertRow) {
				rowset.moveToInsertRow();
				// SET THE DEFAULTS
				setDefaults();
				inInsertRow = true;
				// IS SSDATAVALUE IS PROVIDED SET PRIMARY KEY VALUE
				if (dataValue != null) {
					setPrimaryColumn();
				}

			}
			
			// TODO May be able to utilize JDBCType Enum here.
			// TODO This may be better as a static method in RowSetOps

			//final int type = rowset.getColumnType(_column + 1);
			final int type = RowSetOps.getColumnType(rowset, _column + 1);

			switch (type) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				rowset.updateInt(_column + 1, ((Integer) _value));
				break;
			case Types.BIGINT:
// adding update long support 11-01-2004
				rowset.updateLong(_column + 1, ((Long) _value));
				break;
			case Types.FLOAT:
				rowset.updateFloat(_column + 1, ((Float) _value));
				break;
			case Types.DOUBLE:
			case Types.NUMERIC:
				rowset.updateDouble(_column + 1, ((Double) _value));
				break;
			case Types.BOOLEAN:
			case Types.BIT:
				rowset.updateBoolean(_column + 1, ((Boolean) _value));
				break;
			case Types.DATE:
				if (_value instanceof String) {
					rowset.updateDate(_column + 1, getSQLDate((String) _value));
				} else {
					rowset.updateDate(_column + 1, (Date) _value);
				}
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				rowset.updateString(_column + 1, (String) _value);
				break;
			default:
				logger.warn("SSTableModel.setValueAt(): Unknown data type.");
			}

			rowset.insertRow();
			if (rowCount != 0) {
				rowset.moveToCurrentRow();
			} else {
				rowset.first();
			}
			rowset.refreshRow();

			logger.debug("Row number of inserted row : {}", () -> {
				try {
					return rowset.getRow();
				} catch (SQLException e) {
					return "*** getRow() threw an SQLException ***";
				}
			});

			if (table != null) {
				table.updateUI();
			}
			inInsertRow = false;
			rowCount++;

			if (dataGridHandler != null) {
				dataGridHandler.performPostInsertOps(rowCount - 1);
			}

		} catch (final SQLException se) {
			logger.error("SQL Exception while inserting row.",  se);
			inInsertRow = false;
			if (component != null) {
				JOptionPane.showMessageDialog(component, "Error while inserting row.\n" + se.getMessage());
			}
		}

		logger.debug("Successfully added row.");

	} // end protected void insertRow(Object _value, int _column) {

	/**
	 * Returns true if the cell at rowIndex and columnIndex is editable. Otherwise,
	 * a call to setValueAt() on the cell will not change the value of that cell.
	 *
	 * @param _row    the row whose value to be queried
	 * @param _column the column whose value to be queried
	 *
	 * @return editable indicator for cell at row and column specified
	 */
	@Override
	public boolean isCellEditable(final int _row, final int _column) {

		if (uneditableColumns != null) {
			for (int i = 0; i < uneditableColumns.length; i++) {
				if (_column == uneditableColumns[i]) {
					return false;
				}
			}
		}

		if (cellEditing != null) {
			return cellEditing.isCellEditable(_row, _column);
		}

		return true;

	} // end public boolean isCellEditable(int _row, int _column) {

	/**
	 * This function sets the default values for the present row.
	 */
	protected void setDefaults() {
		if (defaultValuesMap == null) {
			return;
		}

		final Set<Integer> keySet = defaultValuesMap.keySet();
		final Iterator<?> iterator = keySet.iterator();
		try {
			while (iterator.hasNext()) {
				final Integer column = (Integer) iterator.next();

				logger.debug("Column number is:" + column);
				
				// TODO May be able to utilize JDBCType Enum here.
				// TODO This may be better as a static method in RowSetOps

				// COLUMNS SPECIFIED START FROM 0 BUT FOR SSROWSET THEY START FROM 1
				//final int type = rowset.getColumnType(column.intValue() + 1);
				final int type = RowSetOps.getColumnType(rowset, column.intValue() + 1);
				switch (type) {
				case Types.INTEGER:
				case Types.SMALLINT:
				case Types.TINYINT:
					rowset.updateInt(column.intValue() + 1,
							((Integer) defaultValuesMap.get(column)));
					break;
				case Types.BIGINT:
					rowset.updateLong(column.intValue() + 1,
							((Long) defaultValuesMap.get(column)));
					break;
				case Types.FLOAT:
					rowset.updateFloat(column.intValue() + 1,
							((Float) defaultValuesMap.get(column)));
					break;
				case Types.DOUBLE:
				case Types.NUMERIC:
					rowset.updateDouble(column.intValue() + 1,
							((Double) defaultValuesMap.get(column)));
					break;
				case Types.BOOLEAN:
				case Types.BIT:
					rowset.updateBoolean(column.intValue() + 1,
							((Boolean) defaultValuesMap.get(column)));
					break;
				case Types.DATE:
					rowset.updateDate(column.intValue() + 1, (Date) defaultValuesMap.get(column));
					break;
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					rowset.updateString(column.intValue() + 1, (String) defaultValuesMap.get(column));
					break;
				default:
					logger.warn("Unknown data type of " + type);
				} // END OF SWITCH

			} // END OF WHILE

		} catch (final SQLException se) {
			logger.error("SQL Exception while setting defaults for row.",  se);
			if (component != null) {
				JOptionPane.showMessageDialog(component, "Error while setting defaults for row.\n" + se.getMessage());
			}
		}
	} // end protected void setDefaults() {

	/**
	 * Sets the default values for different columns. These values will be used
	 * while inserting a new row.
	 *
	 * @param _columnNumbers the column numbers for which defaults are required
	 * @param _values        the values for all the columns specified in first
	 *                       argument
	 */
	public void setDefaultValues(final int[] _columnNumbers, final Object[] _values) {
		if ((_columnNumbers == null) || (_values == null)) {
			defaultValuesMap = null;
		}

		if (defaultValuesMap == null) {
			defaultValuesMap = new HashMap<>();
		} else {
			defaultValuesMap.clear();
		}
		if ((_columnNumbers != null) && (_values != null)) {
			for (int i = 0; i < _columnNumbers.length; i++) {
				defaultValuesMap.put(new Integer(_columnNumbers[i]), _values[i]);
			}
		}
	}

	/**
	 * Sets the headers for the JTable. This function has to be called before
	 * setting the RowSet for SSDataGrid.
	 *
	 * @param _headers array of string objects representing the header for each
	 *                 column.
	 */
	public void setHeaders(final String[] _headers) {
		headers = _headers;
	}

	/**
	 * Sets the column numbers that should be hidden. The SSDataGrid sets the column
	 * width of these columns to 0. The columns are set to zero width rather than
	 * removing the column from the table. Thus preserving the column numbering. If
	 * a column is removed then the column numbers for columns after the removed
	 * column will change. Even if the column is specified as hidden user will be
	 * seeing a tiny strip. Make sure that you specify the hidden column numbers in
	 * the uneditable column list.
	 *
	 * @param _columnNumbers array specifying the column numbers which should be
	 *                       hidden.
	 */
	public void setHiddenColumns(final int[] _columnNumbers) {
		hiddenColumns = _columnNumbers;
	}

	/**
	 * Sets row insertion indicator.
	 *
	 * @param _insert true if user can insert new rows, else false.
	 */
	public void setInsertion(final boolean _insert) {
		allowInsertion = _insert;
	}

	/**
	 * This sets the JTable to which the table model is bound to. When an insert row
	 * has taken place TableModel tries to update the UI.
	 *
	 * @param _table JTable to which SSTableModel is bound to.
	 */
	public void setJTable(final JTable _table) {
		table = _table;
	}

	/**
	 * Sets the message window. This is used as parent component for pop up message
	 * dialogs.
	 *
	 * @param _component the component that should be used for message dialogs.
	 */
	public void setMessageWindow(final Component _component) {
		component = _component;
	}

	/**
	 * Updates the primary key column based on the SSDataValue implementation
	 * specified for the SSTableModel and the underlying SQL data type.
	 */
	protected void setPrimaryColumn() {
		try {
			
			// TODO May be able to utilize JDBCType Enum here.
			// TODO This may be better as a static method in RowSetOps

			//final int type = rowset.getColumnType(primaryColumn + 1);
			final int type = RowSetOps.getColumnType(rowset, primaryColumn + 1);

			switch (type) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				rowset.updateInt(primaryColumn + 1,
						((Integer) dataValue.getPrimaryColumnValue()));
				break;
			case Types.BIGINT:
				rowset.updateLong(primaryColumn + 1,
						((Long) dataValue.getPrimaryColumnValue()));
				break;
			case Types.FLOAT:
				rowset.updateFloat(primaryColumn + 1,
						((Float) dataValue.getPrimaryColumnValue()));
				break;
			case Types.DOUBLE:
			case Types.NUMERIC:
				rowset.updateDouble(primaryColumn + 1,
						((Double) dataValue.getPrimaryColumnValue()));
				break;
			case Types.BOOLEAN:
			case Types.BIT:
				rowset.updateBoolean(primaryColumn + 1,
						((Boolean) dataValue.getPrimaryColumnValue()));
				break;
			case Types.DATE:
				rowset.updateDate(primaryColumn + 1, (Date) dataValue.getPrimaryColumnValue());
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				rowset.updateString(primaryColumn + 1, (String) dataValue.getPrimaryColumnValue());
				break;
			default:
				logger.warn("Unknown data type of " + type);
			}
		} catch (final SQLException se) {
			logger.error("SQL Exception while insering Primary Key value.",  se);
			if (component != null) {
				JOptionPane.showMessageDialog(component,
						"Error while inserting Primary Key value.\n" + se.getMessage());
			}
		}
	} // end protected void setPrimaryColumn() {

	/**
	 * Sets the column number which is the primary column for the table. This is
	 * required if new rows have to be added to the JTable. For this to properly
	 * work the SSDataValue object should also be provided SSDataValue is used to
	 * get the value for the primary column.
	 *
	 * @param _columnNumber the column which is the primary column.
	 */
	public void setPrimaryColumn(final int _columnNumber) {
		primaryColumn = _columnNumber;
	}

	/**
	 * Sets the RowSet for SSTableModel to the given RowSet. This RowSet will
	 * be used to get the data for JTable.
	 *
	 * @param _rowset RowSet object whose records has to be displayed in JTable.
	 */
	public void setRowSet(final RowSet _rowset) {
		rowset = _rowset;
		init();
	}
	
	/**
	 * Used to set an implementation of SSCellEditing interface which can be used to
	 * determine dynamically if a given cell can be edited and to determine if a
	 * given value is valid.
	 *
	 * @param _cellEditing implementation of SSCellEditing interface.
	 */
	public void setSSCellEditing(final SSCellEditing _cellEditing) {
		cellEditing = _cellEditing;
	}

	/**
	 * Used to set an implementation of SSDataGridHandler interface which can be
	 * used to determine dynamically if a given row can be deleted, and what should
	 * be done after row insertion, and deletion.
	 *
	 * @param _dataGridHandler implementation of SSDataGridHandler interface.
	 */
	public void setSSDataGridHandler(final SSDataGridHandler _dataGridHandler) {
		dataGridHandler = _dataGridHandler;
	}

	/**
	 * Sets the SSDataValue interface implemention. This interface specifies
	 * function to retrieve primary column values for a new row to be added.
	 *
	 * @param _dataValue implementation of SSDataValue for determining PK
	 */
	public void setSSDataValue(final SSDataValue _dataValue) {
		dataValue = _dataValue;
	}

	/**
	 * Sets the uneditable columns. The columns specified as uneditable will not be
	 * available for user to edit. This overrides the isCellEditable function in
	 * SSCellEditing.
	 *
	 * @param _columnNumbers array specifying the column numbers which should be
	 *                       uneditable.
	 */
	public void setUneditableColumns(final int[] _columnNumbers) {
		uneditableColumns = _columnNumbers;
	}

	/**
	 * Sets the value in the cell at _row and _column to _value.
	 *
	 * @param _value  the new value
	 * @param _row    the row whose value is to be changed
	 * @param _column the column whose value is to be changed
	 */
	@Override
	public void setValueAt(final Object _value, final int _row, final int _column) {

		// MAKE LOCAL COPY OF OBJECT FOR DATE MANIPULATIONS
		Object valueCopy = _value;

		// GET THE TYPE OF THE COLUMN
		int type = -1;
		try {
			//type = rowset.getColumnType(_column + 1);
			type = RowSetOps.getColumnType(rowset, _column + 1);
		} catch (final SQLException se) {
			logger.error("SQL Exception while updating value.",  se);
			if (component != null) {
				JOptionPane.showMessageDialog(component, "Error while updating value.\n" + se.getMessage());
			}
			return;
		}
		
		// TODO Clean this up. Utilize java.util.Time.

		// IF COPYING VALUES THE DATE WILL COME AS STRING SO CONVERT IT TO DATE OBJECT.
		if (type == Types.DATE) {
			if (valueCopy instanceof String) {
				valueCopy = getSQLDate((String) valueCopy);
			}
		} else if (type == Types.TIMESTAMP) {
			if (valueCopy instanceof String) {
				valueCopy = new Timestamp(getSQLDate((String) valueCopy).getTime());
			}
		}

		// IF CELL EDITING INTERFACE IMPLEMENTATION IS PROVIDED INFO THE USER
		// THAT AN UPDATE FOR CELL HAS BEEN REQUESTED.
		if (cellEditing != null) {
			// THE ROW AND COLUMN NUMBERING STARTS FROM 0 FOR JTABLE BUT THE COLUMNS AND
			// ROWS ARE NUMBERED FROM 1 FOR SSROWSET.
			boolean allowEdit;

			// IF ITS NEW ROW SEND A NULL FOR THE OLD VALUE
			if (_row == rowCount) {
				allowEdit = cellEditing.cellUpdateRequested(_row, _column, null, valueCopy);
			} else {
				allowEdit = cellEditing.cellUpdateRequested(_row, _column, getValueAt(_row, _column), valueCopy);
			}

			// IF THE USER DOES NOT PERMIT THE UPDATE RETURN ELSE GO AHEAD AND UPDATE THE
			// DATABASE.
			if (!allowEdit) {
				return;
			}
		}

		// IF CHANGE IS MADE IN INSERT ROW ADD ROW TO THE DATABASE
		// INSERTROW FUNCTION ALSO INCREMENTS THE ROW COUNT
		if (_row == rowCount) {
			insertRow(valueCopy, _column);
			return;
		}

		logger.debug("Set value at "+ _row + "  " + _column + " with "+ valueCopy);

		try {
			// YOU SHOULD BE ON THE RIGHT ROW IN THE SSROWSET
			if (rowset.getRow() != (_row + 1)) {
				rowset.absolute(_row + 1);
			}
			if (valueCopy == null) {
				rowset.updateNull(_column + 1);
				return;
			}
			
			// TODO May be able to utilize JDBCType Enum here.
			// TODO This may be better as a static method in RowSetOps

			switch (type) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				rowset.updateInt(_column + 1, ((Integer) valueCopy));
				break;
			case Types.BIGINT:
// adding update long support 11-01-2004
				rowset.updateLong(_column + 1, ((Long) valueCopy));
				break;
			case Types.FLOAT:
				rowset.updateFloat(_column + 1, ((Float) valueCopy));
				break;
			case Types.DOUBLE:
			case Types.NUMERIC:
				rowset.updateDouble(_column + 1, ((Double) valueCopy));
				break;
			case Types.BOOLEAN:
			case Types.BIT:
				rowset.updateBoolean(_column + 1, ((Boolean) valueCopy));
				break;
			case Types.DATE:
				rowset.updateDate(_column + 1, (Date) valueCopy);
				break;
			case Types.TIMESTAMP:
				rowset.updateTimestamp(_column + 1, (Timestamp) valueCopy);
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				rowset.updateString(_column + 1, (String) valueCopy);
				break;
			default:
				logger.warn("Unknown data type of " + type);
			}
			rowset.updateRow();

			logger.debug("Updated value: {}.", () -> getValueAt(_row,_column));
		} catch (final SQLException se) {
			logger.error("SQL Exception while updating value.",  se);
			if (component != null) {
				JOptionPane.showMessageDialog(component, "Error while updating value.\n" + se.getMessage());
			}
		}

	} // end public void setValueAt(Object _value, int _row, int _column) {

}
