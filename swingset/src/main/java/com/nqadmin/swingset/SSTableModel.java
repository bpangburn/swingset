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

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.nqadmin.swingset.datasources.SSRowSet;

/**
 * SSTableModel.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * SSTableModel provides an implementation of the TableModel interface. The
 * SSDataGrid uses this class for providing a grid view for a SSRowSet.
 * SSTableModel can be used without the SSDataGrid (e.g. in conjunction with a
 * JTable), but the cell renderers and hidden columns features of the SSDataGrid
 * will not be available.
 *
 * SSTableModel can be used with a JTable to get a Grid view of the data.
 */
public class SSTableModel extends AbstractTableModel {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -7650858998003486418L;

	protected SSRowSet rowset = null;

	/**
	 * Number of rows in the SSRowSet.
	 */
	protected transient int rowCount = 0;

	/**
	 * Number of columns in the SSRowSet.
	 */
	protected transient int columnCount = 0;

	/**
	 * Map to store the default values of different columns.
	 */
	protected HashMap<Integer, Object> defaultValuesMap = null;

	/**
	 * Indicator to determine if the SSRowSet is on the insertion row.
	 */
	protected boolean inInsertRow = false;

	/**
	 * Window where messages should be displayed.
	 */
	protected transient Component component = null;

	/**
	 * JTable being modeled.
	 */
	protected transient JTable table = null;

	/**
	 * JTable headers.
	 */
	protected transient String[] headers = null;

	/**
	 * Column containing primary key.
	 */
	int primaryColumn = -1;

	/**
	 * Implementation of SSDataValue interface used to determine PK value for new
	 * rows.
	 */
	protected SSDataValue dataValue = null;

	/**
	 * Implementation of SSCellEditing interface used to determine dynamically if a
	 * given cell can be edited and to determine if a given value is valid.
	 */
	protected SSCellEditing cellEditing = null;

	/**
	 * Implementation of DataGridHandler interface used to determine dynamically if
	 * a given row can be deleted, and what to do before and after a row is added or
	 * removed.
	 */
	protected SSDataGridHandler dataGridHandler = null;

	/**
	 * List of uneditable columns.
	 */
	protected int[] uneditableColumns = null;

	/**
	 * List of hidden columns.
	 */
	protected int[] hiddenColumns = null;

	/**
	 * Indicator to determine if insertions are allowed.
	 */
	protected boolean allowInsertion = true;

	/**
	 * Constructs a SSTableModel object. If this contructor is used the
	 * setSSRowSet() method has to be used to set the SSRowSet before constructing
	 * the JTable.
	 */
	public SSTableModel() {
		super();
	}

	/**
	 * Constructs a SSTableModel object with the given SSRowSet. This will call the
	 * execute method on the given SSRowSet.
	 *
	 * @param _rowset SSRowSet object whose records has to be displayed in JTable.
	 */
	public SSTableModel(SSRowSet _rowset) {
		super();
		this.rowset = _rowset;
		init();
	}

	/**
	 * Sets the SSRowSet for SSTableModel to the given SSRowSet. This SSRowSet will
	 * be used to get the data for JTable.
	 *
	 * @param _rowset SSRowSet object whose records has to be displayed in JTable.
	 */
	public void setSSRowSet(SSRowSet _rowset) {
		this.rowset = _rowset;
		init();
	}

	/**
	 * Used to set an implementation of SSCellEditing interface which can be used to
	 * determine dynamically if a given cell can be edited and to determine if a
	 * given value is valid.
	 *
	 * @param _cellEditing implementation of SSCellEditing interface.
	 */
	public void setSSCellEditing(SSCellEditing _cellEditing) {
		this.cellEditing = _cellEditing;
	}

	/**
	 * Used to set an implementation of SSDataGridHandler interface which can be
	 * used to determine dynamically if a given row can be deleted, and what should
	 * be done after row insertion, and deletion.
	 *
	 * @param _dataGridHandler implementation of SSDataGridHandler interface.
	 */
	public void setSSDataGridHandler(SSDataGridHandler _dataGridHandler) {
		this.dataGridHandler = _dataGridHandler;
	}

	/**
	 * Sets row insertion indicator.
	 *
	 * @param _insert true if user can insert new rows, else false.
	 */
	public void setInsertion(boolean _insert) {
		this.allowInsertion = _insert;
	}

	/**
	 * Initializes the SSTableModel. (Gets the column count and row count for the
	 * given SSRowSet.)
	 */
	protected void init() {
		try {

			this.columnCount = this.rowset.getColumnCount();
			this.rowset.last();
			// ROWS IN THE SSROWSET ARE NUMBERED FROM 1, SO LAST ROW NUMBER GIVES THE
			// ROW COUNT
			this.rowCount = this.rowset.getRow();
			this.rowset.first();

// following code added 11-01-2004 per forum suggestion from Diego Gil (dags)
			// IF DATA CHANGES, ALERT LISTENERS
			this.fireTableDataChanged();
// end additions

		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * Returns the number of columns in the model. A JTable uses this method to
	 * determine how many columns it should create and display by default.
	 *
	 * @return the number of columns in the SSTableModel
	 */
	@Override
	public int getColumnCount() {
		return this.columnCount;
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
		if (this.allowInsertion)
			return this.rowCount + 1;
		// IF INSERTION IS NOT ALLOWED THEN RETURN THE ACTUAL ROW COUNT
		return this.rowCount;
	}

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
	public boolean isCellEditable(int _row, int _column) {

//      if(rowset.isReadOnly()){
//          System.out.println("Is Cell Editable : false");
//          return false;
//      }
//      System.out.println("Is Cell Editable : true");
		if (this.uneditableColumns != null) {
			for (int i = 0; i < this.uneditableColumns.length; i++) {
				if (_column == this.uneditableColumns[i]) {
					return false;
				}
			}
		}
		if (this.cellEditing != null) {
			return this.cellEditing.isCellEditable(_row, _column);
		}

		return true;

	} // end public boolean isCellEditable(int _row, int _column) {

	/**
	 * Returns the value for the cell at the specified row and column.
	 *
	 * @param _row    the row whose value to be queried.
	 * @param _column the column whose value to be queried.
	 *
	 * @return value at the requested cell.
	 */
	@Override
	public Object getValueAt(int _row, int _column) {

		Object value = null;
		if (_row == this.rowCount) {
			value = getDefaultValue(_column);
			return value;
		}

		try {
			// ROW NUMBERS IN SSROWSET START FROM 1 WHERE AS ROW NUMBERING FOR JTABLE START
			// FROM 0
			this.rowset.absolute(_row + 1);

			// IF IT IS NULL RETURN NULL
			if (this.rowset.getObject(_column + 1) == null)
				return null;

			// COLUMN NUMBERS IN SSROWSET START FROM 1 WHERE AS COLUMN NUMBERING FOR JTABLE
			// START FROM 0
			int type = this.rowset.getColumnType(_column + 1);
			switch (type) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				value = new Integer(this.rowset.getInt(_column + 1));
				break;
			case Types.BIGINT:
				value = new Long(this.rowset.getLong(_column + 1));
				break;
			case Types.FLOAT:
				value = new Float(this.rowset.getFloat(_column + 1));
				break;
			case Types.DOUBLE:
			case Types.NUMERIC:
				value = new Double(this.rowset.getDouble(_column + 1));
				break;
			case Types.BOOLEAN:
			case Types.BIT:
				value = new Boolean(this.rowset.getBoolean(_column + 1));
				break;
			case Types.DATE:
			case Types.TIMESTAMP:
				value = this.rowset.getDate(_column + 1);
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				value = this.rowset.getString(_column + 1);
				break;
			default:
				System.out.println("SSTableModel.getValueAt(): Unknown data type");
			}
		} catch (SQLException se) {
			se.printStackTrace();
			if (this.component != null) {
				JOptionPane.showMessageDialog(this.component, "Error while retrieving value.\n" + se.getMessage());
			}

		}

		return value;

	} // end public Object getValueAt(int _row, int _column) {

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
			type = this.rowset.getColumnType(_column + 1);
		} catch (SQLException se) {
			se.printStackTrace();
			if (this.component != null) {
				JOptionPane.showMessageDialog(this.component, "Error while updating the value.\n" + se.getMessage());
			}
			return;
		}

		// IF COPYING VALUES THE DATE WILL COME AS STRING SO CONVERT IT TO DATE OBJECT.
		if (type == Types.DATE) {
			if (valueCopy instanceof String)
				valueCopy = getSQLDate((String) valueCopy);
		} else if (type == Types.TIMESTAMP) {
			if (valueCopy instanceof String)
				valueCopy = new Timestamp(getSQLDate((String) valueCopy).getTime());
		}

		// IF CELL EDITING INTERFACE IMPLEMENTATION IS PROVIDED INFO THE USER
		// THAT AN UPDATE FOR CELL HAS BEEN REQUESTED.
		if (this.cellEditing != null) {
			// THE ROW AND COLUMN NUMBERING STARTS FROM 0 FOR JTABLE BUT THE COLUMNS AND
			// ROWS ARE NUMBERED FROM 1 FOR SSROWSET.
			boolean allowEdit;

			// IF ITS NEW ROW SEND A NULL FOR THE OLD VALUE
			if (_row == this.rowCount) {
				allowEdit = this.cellEditing.cellUpdateRequested(_row, _column, null, valueCopy);
			} else {
				allowEdit = this.cellEditing.cellUpdateRequested(_row, _column, getValueAt(_row, _column), valueCopy);
			}

			// IF THE USER DOES NOT PERMIT THE UPDATE RETURN ELSE GO AHEAD AND UPDATE THE
			// DATABASE.
			if (!allowEdit) {
				return;
			}
		}

		// IF CHANGE IS MADE IN INSERT ROW ADD ROW TO THE DATABASE
		// INSERTROW FUNCTION ALSO INCREMENTS THE ROW COUNT
		if (_row == this.rowCount) {
			insertRow(valueCopy, _column);
			return;
		}

//      System.out.println("Set value at "+ _row + "  " + _column + " with "+ valueCopy);
		try {
			// YOU SHOULD BE ON THE RIGHT ROW IN THE SSROWSET
			if (this.rowset.getRow() != _row + 1) {
				this.rowset.absolute(_row + 1);
			}
			if (valueCopy == null) {
				this.rowset.updateNull(_column + 1);
				return;
			}

			switch (type) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				this.rowset.updateInt(_column + 1, ((Integer) valueCopy).intValue());
				break;
			case Types.BIGINT:
// adding update long support 11-01-2004
				this.rowset.updateLong(_column + 1, ((Long) valueCopy).longValue());
				break;
			case Types.FLOAT:
				this.rowset.updateFloat(_column + 1, ((Float) valueCopy).floatValue());
				break;
			case Types.DOUBLE:
			case Types.NUMERIC:
				this.rowset.updateDouble(_column + 1, ((Double) valueCopy).doubleValue());
				break;
			case Types.BOOLEAN:
			case Types.BIT:
				this.rowset.updateBoolean(_column + 1, ((Boolean) valueCopy).booleanValue());
				break;
			case Types.DATE:
				this.rowset.updateDate(_column + 1, (Date) valueCopy);
				break;
			case Types.TIMESTAMP:
				this.rowset.updateTimestamp(_column + 1, (Timestamp) valueCopy);
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				this.rowset.updateString(_column + 1, (String) valueCopy);
				break;
			default:
				System.out.println("SSTableModel.setValueAt(): Unknown data type");
			}
			this.rowset.updateRow();
//          System.out.println("Updated value: " + getValueAt(_row,_column));
		} catch (SQLException se) {
			se.printStackTrace();
			if (this.component != null) {
				JOptionPane.showMessageDialog(this.component, "Error while updating the value.\n" + se.getMessage());
			}
		}

	} // end public void setValueAt(Object _value, int _row, int _column) {

	/**
	 * Inserts a new row into the database. While doing so it inserts all the
	 * defaults provided by user and if the primary column is specified along with
	 * an SSDataValue implementation then the primary column value will be inserted.
	 *
	 * @param _value  value entererd of a column
	 * @param _column the column number for which the value is entered.
	 */
	protected void insertRow(Object _value, int _column) {
		if (_value == null) {
			return;
		}
		if (this.dataGridHandler != null) {
			this.dataGridHandler.performPreInsertOps(this.rowCount);
		}

		try {
			// IF NOT ON INSERT ROW MOVE TO INSERT ROW.
			if (!this.inInsertRow) {
				this.rowset.moveToInsertRow();
				// SET THE DEFAULTS
				setDefaults();
				this.inInsertRow = true;
				// IS SSDATAVALUE IS PROVIDED SET PRIMARY KEY VALUE
				if (this.dataValue != null) {
					setPrimaryColumn();
				}

			}

			int type = this.rowset.getColumnType(_column + 1);

			switch (type) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				this.rowset.updateInt(_column + 1, ((Integer) _value).intValue());
				break;
			case Types.BIGINT:
// adding update long support 11-01-2004
				this.rowset.updateLong(_column + 1, ((Long) _value).longValue());
				break;
			case Types.FLOAT:
				this.rowset.updateFloat(_column + 1, ((Float) _value).floatValue());
				break;
			case Types.DOUBLE:
			case Types.NUMERIC:
				this.rowset.updateDouble(_column + 1, ((Double) _value).doubleValue());
				break;
			case Types.BOOLEAN:
			case Types.BIT:
				this.rowset.updateBoolean(_column + 1, ((Boolean) _value).booleanValue());
				break;
			case Types.DATE:
				if (_value instanceof String) {
					this.rowset.updateDate(_column + 1, getSQLDate((String) _value));
				} else {
					this.rowset.updateDate(_column + 1, (Date) _value);
				}
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				this.rowset.updateString(_column + 1, (String) _value);
				break;
			default:
				System.out.println("SSTableModel.setValueAt(): Unknown data type");
			}

			this.rowset.insertRow();
			if (this.rowCount != 0) {
				this.rowset.moveToCurrentRow();
			} else {
				this.rowset.first();
			}
			this.rowset.refreshRow();
//          System.out.println("Row number of inserted row : "+ rowset.getRow());
			if (this.table != null) {
				this.table.updateUI();
			}
			this.inInsertRow = false;
			this.rowCount++;

			if (this.dataGridHandler != null) {
				this.dataGridHandler.performPostInsertOps(this.rowCount - 1);
			}

		} catch (SQLException se) {
			se.printStackTrace();
			this.inInsertRow = false;
			if (this.component != null) {
				JOptionPane.showMessageDialog(this.component, "Error while trying to insert row.\n" + se.getMessage());
			}
		}
//     System.out.println("Successfully added row");

	} // end protected void insertRow(Object _value, int _column) {

	/**
	 * This function sets the default values for the present row.
	 */
	protected void setDefaults() {
		if (this.defaultValuesMap == null) {
			return;
		}

		Set<Integer> keySet = this.defaultValuesMap.keySet();
		Iterator<?> iterator = keySet.iterator();
		try {
			while (iterator.hasNext()) {
				Integer column = (Integer) iterator.next();
//              System.out.println("Column number is:" + column);
				// COLUMNS SPECIFIED START FROM 0 BUT FOR SSROWSET THEY START FROM 1
				int type = this.rowset.getColumnType(column.intValue() + 1);
				switch (type) {
				case Types.INTEGER:
				case Types.SMALLINT:
				case Types.TINYINT:
					this.rowset.updateInt(column.intValue() + 1,
							((Integer) this.defaultValuesMap.get(column)).intValue());
					break;
				case Types.BIGINT:
					this.rowset.updateLong(column.intValue() + 1,
							((Long) this.defaultValuesMap.get(column)).longValue());
					break;
				case Types.FLOAT:
					this.rowset.updateFloat(column.intValue() + 1,
							((Float) this.defaultValuesMap.get(column)).floatValue());
					break;
				case Types.DOUBLE:
				case Types.NUMERIC:
					this.rowset.updateDouble(column.intValue() + 1,
							((Double) this.defaultValuesMap.get(column)).doubleValue());
					break;
				case Types.BOOLEAN:
				case Types.BIT:
					this.rowset.updateBoolean(column.intValue() + 1,
							((Boolean) this.defaultValuesMap.get(column)).booleanValue());
					break;
				case Types.DATE:
					this.rowset.updateDate(column.intValue() + 1, (Date) this.defaultValuesMap.get(column));
					break;
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					this.rowset.updateString(column.intValue() + 1, (String) this.defaultValuesMap.get(column));
					break;
				default:
					System.out.println("SSTableModel.setValueAt(): Unknown data type");
				} // END OF SWITCH

			} // END OF WHILE

		} catch (SQLException se) {
			se.printStackTrace();
			if (this.component != null) {
				JOptionPane.showMessageDialog(this.component, "Error while inserting row.\n" + se.getMessage());
			}
		}
	} // end protected void setDefaults() {

	/**
	 * Returns the type for the column specified for the current view.
	 *
	 * @param _column the column in the view being queried
	 *
	 * @return type for the specified column (first column is 0)
	 */
	@Override
	public Class<?> getColumnClass(int _column) {
		int type;
		try {
			type = this.rowset.getColumnType(_column + 1);
		} catch (SQLException e) {
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
	 * Deletes the specified row from the database. The rows are numbered as: 0, 1,
	 * ..., n-1
	 *
	 * @param _row the row number that has to be deleted.
	 *
	 * @return returns true on succesful deletion else false.
	 */
	public boolean deleteRow(int _row) {
		if (this.dataGridHandler != null) {
			this.dataGridHandler.performPreDeletionOps(_row);
		}
		if (_row < this.rowCount) {
			try {
				if (this.dataGridHandler != null && !this.dataGridHandler.allowDeletion(_row)) {
					return false;
				}
				this.rowset.absolute(_row + 1);
				this.rowset.deleteRow();
				this.rowCount--;
				if (this.dataGridHandler != null) {
					this.dataGridHandler.performPostDeletionOps(_row);
				}
				return true;
			} catch (SQLException se) {
				se.printStackTrace();
				if (this.component != null) {
					JOptionPane.showMessageDialog(this.component, "Error while deleting row.\n" + se.getMessage());
				}

			}
		}

		return false;

	} // end public boolean deleteRow(int _row) {

	/**
	 * Sets the default values for different columns. These values will be used
	 * while inserting a new row.
	 *
	 * @param _columnNumbers the column numbers for which defaults are required
	 * @param _values        the values for all the columns specified in first
	 *                       argument
	 */
	public void setDefaultValues(int[] _columnNumbers, Object[] _values) {
		if (_columnNumbers == null || _values == null) {
			this.defaultValuesMap = null;
		}

		if (this.defaultValuesMap == null) {
			this.defaultValuesMap = new HashMap<>();
		} else {
			this.defaultValuesMap.clear();
		}
		if (_columnNumbers != null && _values != null) {
			for (int i = 0; i < _columnNumbers.length; i++) {
				this.defaultValuesMap.put(new Integer(_columnNumbers[i]), _values[i]);
			}
		}
	}

	/**
	 * Returns the default value inforce for the requested column.
	 *
	 * The type of object is same as returned by getColumnClass in JTable.
	 *
	 * @param _columnNumber the column number for which default value is needed.
	 *
	 * @return returns a object representing the default value.
	 */
	public Object getDefaultValue(int _columnNumber) {
		Object value = null;
		if (this.defaultValuesMap != null) {
			value = this.defaultValuesMap.get(new Integer(_columnNumber));
		}
		return value;
	}

	/**
	 * Sets the message window. This is used as parent component for pop up message
	 * dialogs.
	 *
	 * @param _component the component that should be used for message dialogs.
	 */
	public void setMessageWindow(Component _component) {
		this.component = _component;
	}

	/**
	 * This sets the JTable to which the table model is bound to. When an insert row
	 * has taken place TableModel tries to update the UI.
	 *
	 * @param _table JTable to which SSTableModel is bound to.
	 */
	public void setJTable(JTable _table) {
		this.table = _table;
	}

	/**
	 * Sets the column number which is the primary column for the table. This is
	 * required if new rows have to be added to the JTable. For this to properly
	 * work the SSDataValue object should also be provided SSDataValue is used to
	 * get the value for the primary column.
	 *
	 * @param _columnNumber the column which is the primary column.
	 */
	public void setPrimaryColumn(int _columnNumber) {
		this.primaryColumn = _columnNumber;
	}

	/**
	 * Sets the SSDataValue interface implemention. This interface specifies
	 * function to retrieve primary column values for a new row to be added.
	 *
	 * @param _dataValue implementation of SSDataValue for determining PK
	 */
	public void setSSDataValue(SSDataValue _dataValue) {
		this.dataValue = _dataValue;
	}

	/**
	 * Updates the primary key column based on the SSDataValue implementation
	 * specified for the SSTableModel and the underlying SQL data type.
	 */
	protected void setPrimaryColumn() {
		try {

			int type = this.rowset.getColumnType(this.primaryColumn + 1);

			switch (type) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				this.rowset.updateInt(this.primaryColumn + 1,
						((Integer) this.dataValue.getPrimaryColumnValue()).intValue());
				break;
			case Types.BIGINT:
				this.rowset.updateLong(this.primaryColumn + 1,
						((Long) this.dataValue.getPrimaryColumnValue()).longValue());
				break;
			case Types.FLOAT:
				this.rowset.updateFloat(this.primaryColumn + 1,
						((Float) this.dataValue.getPrimaryColumnValue()).floatValue());
				break;
			case Types.DOUBLE:
			case Types.NUMERIC:
				this.rowset.updateDouble(this.primaryColumn + 1,
						((Double) this.dataValue.getPrimaryColumnValue()).doubleValue());
				break;
			case Types.BOOLEAN:
			case Types.BIT:
				this.rowset.updateBoolean(this.primaryColumn + 1,
						((Boolean) this.dataValue.getPrimaryColumnValue()).booleanValue());
				break;
			case Types.DATE:
				this.rowset.updateDate(this.primaryColumn + 1, (Date) this.dataValue.getPrimaryColumnValue());
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				this.rowset.updateString(this.primaryColumn + 1, (String) this.dataValue.getPrimaryColumnValue());
				break;
			default:
				System.out.println("SSTableModel.setPrimaryColumn(): Unknown data type");
			}
		} catch (SQLException se) {
			se.printStackTrace();
			if (this.component != null) {
				JOptionPane.showMessageDialog(this.component,
						"Error while inserting Primary Key value.\n" + se.getMessage());
			}
		}
	} // end protected void setPrimaryColumn() {

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
			StringTokenizer strtok = new StringTokenizer(newStrDate, "/", false);
			String month = strtok.nextToken();
			String day = strtok.nextToken();
			newStrDate = strtok.nextToken() + "-" + month + "-" + day;
		}
		return Date.valueOf(newStrDate);
	}

	/**
	 * Sets the headers for the JTable. This function has to be called before
	 * setting the SSRowSet for SSDataGrid.
	 *
	 * @param _headers array of string objects representing the header for each
	 *                 column.
	 */
	public void setHeaders(String[] _headers) {
		this.headers = _headers;
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
	public String getColumnName(int _columnNumber) {
		if (this.headers != null) {
			if (_columnNumber < this.headers.length) {
//              System.out.println("sending header " + headers[_columnNumber]);
				return this.headers[_columnNumber];
			}
		}
//      System.out.println(" Not able to supply header name");
		return "";
	}

	/**
	 * Sets the uneditable columns. The columns specified as uneditable will not be
	 * available for user to edit. This overrides the isCellEditable function in
	 * SSCellEditing.
	 *
	 * @param _columnNumbers array specifying the column numbers which should be
	 *                       uneditable.
	 */
	public void setUneditableColumns(int[] _columnNumbers) {
		this.uneditableColumns = _columnNumbers;
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
	public void setHiddenColumns(int[] _columnNumbers) {
		this.hiddenColumns = _columnNumbers;
	}

// DEPRECATED STUFF....................

	/**
	 * Sets the SSRowSet for SSTableModel to the given SSRowSet. This SSRowSet will
	 * be used to get the data for JTable.
	 *
	 * @param _rowset SSRowSet object whose records has to be displayed in JTable.
	 *
	 * @deprecated Use {@link #setSSRowSet(SSRowSet _rowset)} instead.
	 */
	@Deprecated
	public void setRowSet(SSRowSet _rowset) {
		this.rowset = _rowset;
		init();
	}

} // end public class SSTableModel extends AbstractTableModel {

/*
 * $Log$ Revision 1.24 2007/11/12 22:44:45 prasanth When the underlying column
 * is timestamp using updateTimestamp rather than updateDate.
 *
 * Revision 1.23 2007/10/26 20:35:26 prasanth getValueAt now returns null if a
 * given column has null. It used to return 0 for numeric fields (getInt,
 * getDouble return 0 for null columns)
 *
 * Revision 1.22 2007/01/08 22:03:20 prasanth Trimming any spaces on either side
 * to avoid NumberFormatException. This could happen when copying data to the
 * spreadsheet.
 *
 * Revision 1.21 2006/05/15 16:10:38 prasanth Updated copy right
 *
 * Revision 1.20 2005/03/16 21:12:33 prasanth In setValueAt checking for the
 * type of column and converting the value from String to Date if the column
 * type is Date or TimeStamp, so that all the functions in different interfaces
 * get the Date object rather than String as the new value.
 *
 * Revision 1.19 2005/03/09 21:45:26 prasanth Added TIMESTAMP column type in
 * setValueAt & getValueAt functions.
 *
 * Revision 1.18 2005/02/09 22:20:05 yoda2 JavaDoc cleanup.
 *
 * Revision 1.17 2005/02/07 22:55:34 yoda2 Fixed accidental renaming in
 * deprecated method.
 *
 * Revision 1.16 2005/02/07 22:46:29 yoda2 Deprecated setRowSet().
 *
 * Revision 1.15 2005/02/04 23:05:10 yoda2 no message
 *
 * Revision 1.14 2005/02/04 22:48:54 yoda2 API cleanup & updated Copyright info.
 *
 * Revision 1.13 2004/11/11 14:45:48 yoda2 Using TextPad, converted all tabs to
 * "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.12 2004/11/01 15:48:39 yoda2 Added support for NUMERIC. Made type
 * support consistent across SwingSet: INTEGER, SMALLINT, TINYINT (Integer);
 * BIGINT (Long); FLOAT (Float); DOUBLE, NUMERIC (Double); BOOLEAN, BIT
 * (Boolean); DATE (Date); CHAR, VARCHAR, LONGVARCHAR (String).
 *
 * Revision 1.11 2004/10/25 22:13:43 yoda2 Updated JavaDoc for new datasource
 * abstraction layer in 0.9.0 release.
 *
 * Revision 1.10 2004/10/25 19:51:03 prasanth Modified to use the new SSRowSet
 * instead of RowSet.
 *
 * Revision 1.9 2004/10/19 21:13:07 prasanth In getSQLDate function. checking if
 * a / occurs in the string. If not assuming that its in standard format
 * yyyy-mm-dd and trying to convert in to a date.
 *
 * Revision 1.8 2004/09/27 15:48:17 prasanth Added function to disable
 * insertions.
 *
 * Revision 1.7 2004/08/10 22:06:58 yoda2 Added/edited JavaDoc, made code layout
 * more uniform across classes, made various small coding improvements suggested
 * by PMD.
 *
 * Revision 1.6 2004/08/02 15:27:15 prasanth Made all variabled protected.
 *
 * Revision 1.5 2004/03/08 16:43:37 prasanth Updated copy right year.
 *
 * Revision 1.4 2003/12/18 20:12:20 prasanth Update class description.
 *
 * Revision 1.3 2003/12/16 18:01:40 prasanth Documented versions for release
 * 0.6.0
 *
 */
