/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2005, The Pangburn Company and Prasanth R. Pasala
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  The names of its contributors may not be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.nqadmin.swingSet;

import javax.swing.table.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.awt.Component;
import java.sql.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.StringTokenizer;
import com.nqadmin.swingSet.datasources.SSRowSet;

/**
 * SSTableModel.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * SSTableModel provides an implementation of the TableModel interface.
 * The SSDataGrid uses this class for providing a grid view for a SSRowSet.
 * SSTableModel can be used without the SSDataGrid (e.g. in conjunction with a
 * JTable), but the cell renderers and hidden columns features of the SSDataGrid
 * will not be available.
 *
 * SSTableModel can be used with  a JTable to get a Grid view of the data.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
public class SSTableModel extends AbstractTableModel {

    protected SSRowSet rowset = null;

    protected transient int rowCount            = 0;
    protected transient int columnCount         = 0;

    // MAP TO STORE THE DEFAULT VALUES OF DIFFERENT COLUMNS
    protected HashMap defaultValuesMap = null;

    // BOOLEAN TO INDICATE IF THE SSROWSET IS ON INSERT ROW.
    protected boolean inInsertRow = false;

    // MESSAGE WINDOW
    protected transient Component component = null;

    // JTABLE FOR WHICH PRESENT CLASS IS A TABLE MODEL
    protected transient JTable table = null;

    // HEADERS FOR THE JTABLE.
    protected transient String[] headers = null;

    // PRIMARY COLUMN NUMBER
    int primaryColumn = -1;

    protected SSDataValue    dataValue   = null;
    protected SSCellEditing  cellEditing = null;

    // LIST OF UNEDITABLE COLUMNS
    protected int[] uneditableColumns  = null;

    // LIST OF HIDDEN COLUMNS
    protected int[] hiddenColumns      = null;

    // BOOLEAN INDICATING IF INSERTIONS SHOULD BE ALLOWED
    protected boolean allowInsertion = true;

    /**
     * Constructs a SSTableModel object.
     * If this contructor is used the setSSRowSet() method has to be used to set the SSRowSet
     * before constructing the JTable.
     */
    public SSTableModel() {
        super();
    }

    /**
     * Constructs a SSTableModel object with the given SSRowSet.
     * This will call the execute method on the given SSRowSet.
     *
     * @param _rowset    SSRowSet object whose records has to be displayed in JTable.
     */
    public SSTableModel(SSRowSet _rowset) throws SQLException {
        super();
        rowset = _rowset;
        init();
    }

    /**
     * Sets the SSRowSet for SSTableModel to the given SSRowSet.
     * This SSRowSet will be used to get the data for JTable.
     *
     * @param _rowset    SSRowSet object whose records has to be displayed in JTable.
     */
    public void setSSRowSet(SSRowSet _rowset) throws SQLException {
        rowset = _rowset;
        init();
    }

    /**
     * If the user has to decide on which cell has to be editable and which is not
     * then SSCellEditing interface has to be implemented and set it for the SSTableModel.
     *
     * @param _cellEditing    implementation of SSCellEditing interface.
     */
     public void setSSCellEditing(SSCellEditing _cellEditing) {
        cellEditing = _cellEditing;
     }

     /**
      * Sets whether insertions should be allowed or not.
      *@param _insert - true if user can insert new rows else false.
      */
     public void setInsertion(boolean _insert){
        allowInsertion = _insert;
     }

    /**
     * Initializes the SSTableModel. (Gets  the column count and rowcount for the
     * given SSRowSet.)
     */
    protected void init() {
        try {

            columnCount = rowset.getColumnCount();
            rowset.last();
            // ROWS IN THE SSROWSET ARE NUMBERED FROM 1, SO LAST ROW NUMBER GIVES THE
            // ROW COUNT
            rowCount = rowset.getRow();
            rowset.first();

// following code added 11-01-2004 per forum suggestion from Diego Gil (dags)
            // IF DATA CHANGES, ALERT LISTENERS
            this.fireTableDataChanged();
// end additions

        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Returns the number of columns in the model. A JTable uses this method to
     * determine how many columns it should create and display by default.
     *
     * @return the number of columns in the SSTableModel
     */
    public int getColumnCount() {
        return columnCount ;
    }

    /**
     * Returns the number of rows in the model. A JTable uses this method to determine
     * how many rows it should display.
     *
     * @return the number of rows in the SSTableModel
     */
    public int getRowCount() {
        // RETURN THE NUMBER OF ROWS AS ONE GREATER THAN THOSE IN DATABASE
        // ITS USED FOR INSERTING NEW ROWS
        if(allowInsertion)
            return rowCount +1;
        // IF INSERTION IS NOT ALLOWED THEN RETURN THE ACTUAL ROW COUNT
        return rowCount;
    }

    /**
     * Returns true if the cell at rowIndex and columnIndex is editable. Otherwise,
     * setValueAt on the cell will not change the value of that cell.
     *
     * @param _row    the row whose value to be queried
     * @param _column    the column whose value to be queried
     *
     * @return editable indicator for cell at row and column specified
     */
    public boolean isCellEditable(int _row, int _column) {

//      if(rowset.isReadOnly()){
//          System.out.println("Is Cell Editable : false");
//          return false;
//      }
//      System.out.println("Is Cell Editable : true");
        if (uneditableColumns != null) {
            for (int i=0; i<uneditableColumns.length;i++) {
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
     * Returns the value for the cell at  _row and _column.
     *
     * @param _row    the row whose value to be queried.
     * @param _column    the column whose value to be queried.
     *
     * @return value at the requested cell.
     */
    public Object getValueAt(int _row, int _column) {

        Object value = null;
        if (_row == rowCount) {
            value = getDefaultValue(_column);
            return value;
        }

        try {
            // ROW NUMBERS IN SSROWSET START FROM 1 WHERE AS ROW NUMBERING FOR JTABLE START FROM 0
            rowset.absolute(_row + 1);
            // COLUMN NUMBERS IN SSROWSET START FROM 1 WHERE AS COLUMN NUMBERING FOR JTABLE START FROM 0
            int type = rowset.getColumnType(_column + 1);
            switch(type) {
                case Types.INTEGER:
                case Types.SMALLINT:
                case Types.TINYINT:
                    value = new Integer(rowset.getInt(_column+1));
                    break;
                case Types.BIGINT:
                    value = new Long(rowset.getLong(_column+1));
                    break;
                case Types.FLOAT:
                    value = new Float(rowset.getFloat(_column+1));
                    break;
                case Types.DOUBLE:
                case Types.NUMERIC:
                    value = new Double(rowset.getDouble(_column+1));
                    break;
                case Types.BOOLEAN:
                case Types.BIT:
                    value = new Boolean(rowset.getBoolean(_column+1));
                    break;
                case Types.DATE:
                    value = rowset.getDate(_column+1);
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    value = rowset.getString(_column+1);
                    break;
                default:
                    System.out.println("SSTableModel.getValueAt(): Unknown data type");
            }
         } catch(SQLException se) {
            se.printStackTrace();
            if (component != null) {
                JOptionPane.showMessageDialog(component,"Error while retrieving value.\n" + se.getMessage());
            }

         }

         return value;

    } // end public Object getValueAt(int _row, int _column) {

    /**
     * Sets the value in the cell at _row and _column to _value.
     *
     * @param _value    the new value
     * @param _row    the row whose value is to be changed
     * @param _column    the column whose value is to be changed
     */
    public void setValueAt(Object _value, int _row, int _column) {

        // IF CELL EDITING INTERFACE IMPLEMENTATION IS PROVIDED INFO THE USER
        // THAT AN UPDATE FOR CELL HAS BEEN REQUESTED.
        if (cellEditing != null) {
            // THE ROW AND COLUMN NUMBERING STARTS FROM 0 FOR JTABLE BUT THE COLUMNS AND
            // ROWS ARE NUMBERED FROM 1 FOR SSROWSET.
            boolean allowEdit;

            // IF ITS NEW ROW SEND A NULL FOR THE OLD VALUE
            if (_row == rowCount) {
                allowEdit = cellEditing.cellUpdateRequested(_row, _column, null, _value);
            } else {
                allowEdit = cellEditing.cellUpdateRequested(_row, _column, getValueAt(_row,_column), _value);
            }

            // IF THE USER DOES NOT PERMIT THE UPDATE RETURN ELSE GO AHEAD AND UPDATE THE
            // DATABASE.
            if (!allowEdit) {
                return;
            }
        }

        //  IF CHANGE IS MADE IN INSERT ROW ADD ROW TO THE DATABASE
        //  INSERTROW FUNCTION ALSO INCREMENTS THE ROW COUNT
        if ( _row == rowCount ) {
            insertRow(_value,_column);
            return;
        }

//      System.out.println("Set value at "+ _row + "  " + _column + " with "+ _value);
        try{
            // YOU SHOULD BE ON THE RIGHT ROW IN THE SSROWSET
            if (rowset.getRow() != _row +1) {
                rowset.absolute(_row +1);
            }
            if ( _value == null) {
                rowset.updateNull(_column+1);
                return;
            }

            int type = rowset.getColumnType(_column + 1);
            switch(type) {
                case Types.INTEGER:
                case Types.SMALLINT:
                case Types.TINYINT:
                    rowset.updateInt(_column+1, ((Integer)_value).intValue());
                    break;
                case Types.BIGINT:
// adding update long support 11-01-2004
                    rowset.updateLong(_column+1,((Long)_value).longValue());
                    break;
                case Types.FLOAT:
                    rowset.updateFloat(_column+1, ((Float)_value).floatValue());
                    break;
                case Types.DOUBLE:
                case Types.NUMERIC:
                    rowset.updateDouble(_column+1, ((Double)_value).doubleValue());
                    break;
                case Types.BOOLEAN:
                case Types.BIT:
                    rowset.updateBoolean(_column+1, ((Boolean)_value).booleanValue());
                    break;
                case Types.DATE:
                //  IF A DATE RENDERER AND EDITOR IS USED THE DATE IS DISPLAYED AS STRING.
                // SO A STRING WILL BE SENT FOR UPDATE
                // IN SUCH A CASE CHECK IS THE STRING IS EMPTY IF SO UPDATE NULL FOR THE FEILD
                    if (_value instanceof String) {
                        if (getSQLDate((String)_value) == null) {
                            rowset.updateNull(_column+1);
                        } else {
                            rowset.updateDate(_column+1,getSQLDate((String)_value));
                        }
                    } else {
                        rowset.updateDate(_column+1,(Date)_value);
                    }
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    rowset.updateString(_column+1, (String)_value);
                    break;
                default:
                    System.out.println("SSTableModel.setValueAt(): Unknown data type");
            }
            rowset.updateRow();
//          System.out.println("Updated value: " + getValueAt(_row,_column));
         } catch(SQLException se) {
            se.printStackTrace();
            if (component != null) {
                JOptionPane.showMessageDialog(component,"Error while updating the value.\n" + se.getMessage());
            }
         }

    } // end public void setValueAt(Object _value, int _row, int _column) {

    /**
     * Inserts a new row into the database.
     * While doing so it inserts all the defaults provided by user and also
     * if the primary column is specified along with SSDataValue implementation
     * then the primary column value will be inserted.
     *
     * @param _value   value entererd of a column
     * @param _column  the column number for which the value is entered.
     */
    protected void insertRow(Object _value, int _column) {
        if (_value == null) {
            return;
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

            int type = rowset.getColumnType(_column + 1);

            switch(type) {
                case Types.INTEGER:
                case Types.SMALLINT:
                case Types.TINYINT:
                    rowset.updateInt(_column+1, ((Integer)_value).intValue());
                    break;
                case Types.BIGINT:
// adding update long support 11-01-2004
                    rowset.updateLong(_column+1,((Long)_value).longValue());
                    break;
                case Types.FLOAT:
                    rowset.updateFloat(_column+1, ((Float)_value).floatValue());
                    break;
                case Types.DOUBLE:
                case Types.NUMERIC:
                    rowset.updateDouble(_column+1, ((Double)_value).doubleValue());
                    break;
                case Types.BOOLEAN:
                case Types.BIT:
                    rowset.updateBoolean(_column+1, ((Boolean)_value).booleanValue());
                    break;
                case Types.DATE:
                    if (_value instanceof String) {
                        rowset.updateDate(_column+1,getSQLDate((String)_value));
                    } else {
                        rowset.updateDate(_column+1,(Date)_value);
                    }
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    rowset.updateString(_column+1, (String)_value);
                    break;
                default:
                    System.out.println("SSTableModel.setValueAt(): Unknown data type");
            }

            rowset.insertRow();
            if (rowCount != 0) {
                rowset.moveToCurrentRow();
            } else {
                rowset.first();
            }
            rowset.refreshRow();
//          System.out.println("Row number of inserted row : "+ rowset.getRow());
            if (table != null) {
                table.updateUI();
            }
            inInsertRow = false;
            rowCount++;

       } catch(SQLException se) {
            se.printStackTrace();
            if (component != null) {
                JOptionPane.showMessageDialog(component,"Error while trying to insert row.\n" + se.getMessage());
            }
       }
//     System.out.println("Successfully added row");

    } // end protected void insertRow(Object _value, int _column) {

    /**
     * This functions sets the default values provided by the user
     * to the present row.
     */
    protected void setDefaults() {
        if (defaultValuesMap == null) {
            return;
        }

        Set keySet = defaultValuesMap.keySet();
        Iterator iterator = keySet.iterator();
        try {
            while (iterator.hasNext()) {
                Integer column = (Integer)iterator.next();
//              System.out.println("Column number is:" + column);
                // COLUMNS SPECIFIED START FROM 0 BUT FOR SSROWSET THEY START FROM 1
                int type = rowset.getColumnType(column.intValue() +1 );
                switch(type) {
                    case Types.INTEGER:
                    case Types.SMALLINT:
                    case Types.TINYINT:
                        rowset.updateInt(column.intValue()+1, ((Integer)defaultValuesMap.get(column)).intValue());
                        break;
                    case Types.BIGINT:
                        rowset.updateLong(column.intValue()+1, ((Long)defaultValuesMap.get(column)).longValue());
                        break;
                    case Types.FLOAT:
                        rowset.updateFloat(column.intValue()+1, ((Float)defaultValuesMap.get(column)).floatValue());
                        break;
                    case Types.DOUBLE:
                    case Types.NUMERIC:
                        rowset.updateDouble(column.intValue()+1, ((Double)defaultValuesMap.get(column)).doubleValue());
                        break;
                    case Types.BOOLEAN:
                    case Types.BIT:
                        rowset.updateBoolean(column.intValue()+1, ((Boolean)defaultValuesMap.get(column)).booleanValue());
                        break;
                    case Types.DATE:
                        rowset.updateDate(column.intValue()+1, (Date)defaultValuesMap.get(column));
                        break;
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.LONGVARCHAR:
                        rowset.updateString(column.intValue()+1, (String)defaultValuesMap.get(column));
                        break;
                    default:
                        System.out.println("SSTableModel.setValueAt(): Unknown data type");
                } // END OF SWITCH

            } //END OF WHILE

        } catch(SQLException se) {
            se.printStackTrace();
            if (component != null) {
                JOptionPane.showMessageDialog(component,"Error while inserting row.\n" + se.getMessage());
            }
        }
    } // end protected void setDefaults() {

    /**
     * Returns the type of the column appearing in the view at column position column.
     *
     * @param _column    the column in the view being queried
     *
     * @return the type of the column at position _column in the view where the first
     *  column is column 0
     */
    public Class getColumnClass(int _column) {
        int type;
        try {
            type = rowset.getColumnType(_column+1);
        } catch (SQLException e) {
            return super.getColumnClass(_column);
        }

        switch(type) {
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
     * Deletes the specified row from the database.
     * The rows  are numbered as 0,1,......
     *
     * @param _row the row number that has to be deleted.
     *
     * @return returns true on succesful deletion else false.
     */
    public boolean deleteRow(int _row) {

        if (_row < rowCount) {
            try {
                rowset.absolute(_row +1);
                rowset.deleteRow();
                rowCount--;
                return true;
            } catch(SQLException se) {
                se.printStackTrace();
                if (component != null) {
                    JOptionPane.showMessageDialog(component,"Error while deleting row.\n" + se.getMessage());
                }

            }
        }

        return false;

    } // end public boolean deleteRow(int _row) {

    /**
     * Sets the default values for different columns.
     * These values will be used while inserting a new row.
     *
     * @param _columnNumbers    the column numbers for which defaults are required
     * @param _values    the values for all the columns specified in argument 1.
     */
    public void setDefaultValues(int[] _columnNumbers, Object[] _values) {
        if (_columnNumbers == null || _values == null) {
            defaultValuesMap = null;
        }

        if (defaultValuesMap == null) {
            defaultValuesMap = new HashMap();
        } else {
            defaultValuesMap.clear();
        }

        for (int i=0;i<_columnNumbers.length;i++) {
            defaultValuesMap.put(new Integer(_columnNumbers[i]), _values[i]);
        }
    }

    /**
     * Returns the default value inforce for the requested column.
     *
     * The type of object is same as returned by getColumnClass in JTable.
     *
     * @param _columnNumber    the column number for which default value is needed.
     *
     * @return returns a object representing the default value.
     */
    public Object getDefaultValue(int _columnNumber) {
        Object value = null;
        if (defaultValuesMap != null) {
            value = defaultValuesMap.get(new Integer(_columnNumber));
        }
        return value;
    }

    /**
     * Sets the message window.
     * This is used as parent component for pop up message dialogs.
     *
     *@param _component    the component that should be used for message dialogs.
     */
    public void setMessageWindow(Component _component) {
        component = _component;
    }

    /**
     * This sets the JTable to which the table model is bound to.
     * When an insert row has taken place TableModel tries to update the UI.
     *
     * @param _table   JTable to which SSTableModel is bound to.
     */
    public void setJTable(JTable _table) {
        table = _table;
    }

    /**
     * Sets the column number which is the primary column for the table.
     * This is required if new rows have to be added to the JTable.
     * For this to properly work the SSDataValue object should also be provided
     * SSDataValue is used to get the value for the primary column.
     *
     * @param _columnNumber the column which is the primary column.
     */
    public void setPrimaryColumn(int _columnNumber) {
        primaryColumn = _columnNumber;
    }

     /**
     * Sets the SSDataValue interface implemention. This interface specifies
     * function to retrieve primary column values for a new row to be added.
     *
     * @param _dataValue   implementation of
     */
    public void setSSDataValue(SSDataValue _dataValue) {
        dataValue = _dataValue;
    }

    protected void setPrimaryColumn() {
        try {

            int type = rowset.getColumnType(primaryColumn +1);

            switch(type) {
                case Types.INTEGER:
                case Types.SMALLINT:
                case Types.TINYINT:
                    rowset.updateInt(primaryColumn+1,((Integer)dataValue.getPrimaryColumnValue()).intValue());
                    break;
                case Types.BIGINT:
                    rowset.updateLong(primaryColumn+1,((Long)dataValue.getPrimaryColumnValue()).longValue());
                    break;
                case Types.FLOAT:
                    rowset.updateFloat(primaryColumn+1,((Float)dataValue.getPrimaryColumnValue()).floatValue());
                    break;
                case Types.DOUBLE:
                case Types.NUMERIC:
                    rowset.updateDouble(primaryColumn+1,((Double)dataValue.getPrimaryColumnValue()).doubleValue());
                    break;
                case Types.BOOLEAN:
                case Types.BIT:
                    rowset.updateBoolean(primaryColumn+1,((Boolean)dataValue.getPrimaryColumnValue()).booleanValue());
                    break;
                case Types.DATE:
                    rowset.updateDate(primaryColumn+1,(Date)dataValue.getPrimaryColumnValue());
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    rowset.updateString(primaryColumn+1,(String)dataValue.getPrimaryColumnValue());
                    break;
                default:
                    System.out.println("SSTableModel.setPrimaryColumn(): Unknown data type");
            }
        } catch(SQLException se) {
            se.printStackTrace();
            if (component != null) {
                JOptionPane.showMessageDialog(component,"Error while inserting Primary Key value.\n" + se.getMessage());
            }
        }
    } // end protected void setPrimaryColumn() {


    protected Date getSQLDate(String _strDate) {
        if (_strDate.trim().equals("")) {
            return null;
        }
        String newStrDate = _strDate;
        if(_strDate.indexOf("/") != -1){
            StringTokenizer strtok = new StringTokenizer(_strDate,"/",false);
            String month = strtok.nextToken();
            String day = strtok.nextToken();
            newStrDate = strtok.nextToken() + "-" + month + "-" + day;
        }
        return Date.valueOf(newStrDate);
    }

    /**
     * Sets the header for the JTable.
     * This function has to be called before setting the SSRowSet for SSDataGrid.
     *
     * @param _headers array of string objects representing the header of each column.
     */
    public void setHeaders(String[] _headers) {
        headers = _headers;
    }

    /**
     * Returns the name of the column appearing in the view at column position column.
     *
     * @param _columnNumber  the column in the view being querie
     *
     * @return the name of the column at position column in the view where the first
     *  column is column 0
     */
    public String getColumnName(int _columnNumber) {
        if (headers != null) {
            if (_columnNumber < headers.length) {
//              System.out.println("sending header " + headers[_columnNumber]);
                return headers[_columnNumber];
            }
        }
//      System.out.println(" Not able to supply header name");
        return "";
    }

    /**
     * Sets the uneditable columns.
     * The columns specified as uneditable will not be available for user to edit.
     * This overrides the isCellEditable function in SSCellEditing.
     *
     * @param _columnNumbers    array specifying the column numbers which should be
     *  uneditable.
     */
    public void setUneditableColumns(int[] _columnNumbers) {
        uneditableColumns = _columnNumbers;
    }

    /**
     * Sets the column numbers that should be hidden.
     * The SSDataGrid sets the column width of these columns to 0.
     * The columns are set to zero width rather than removing the column from the table.
     * Thus preserving the column numbering.If a column is removed then the column numbers
     * for columns after the removed column will change.
     * Even if the column is specified as hidden user will be seeing a tiny strip.
     * Make sure that you specify the hidden column numbers in the uneditable column
     * list.
     *
     * @param _columnNumbers    array specifying the column numbers which should be
     *  hidden.
     */
    public void setHiddenColumns(int[] _columnNumbers) {
        hiddenColumns = _columnNumbers;
    }
    
    
    
// DEPRECATED STUFF....................

    /**
     * Sets the SSRowSet for SSTableModel to the given SSRowSet.
     * This SSRowSet will be used to get the data for JTable.
     *
     * @param _rowset    SSRowSet object whose records has to be displayed in JTable.
     *
     * @deprecated
     * @see #setSSRowSet        
     */
    public void setRowSet(SSRowSet _rowset) throws SQLException {
        rowset = _rowset;
        init();
    }
    
} // end public class SSTableModel extends AbstractTableModel {



/*
 * $Log$
 * Revision 1.16  2005/02/07 22:46:29  yoda2
 * Deprecated setRowSet().
 *
 * Revision 1.15  2005/02/04 23:05:10  yoda2
 * no message
 *
 * Revision 1.14  2005/02/04 22:48:54  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.13  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.12  2004/11/01 15:48:39  yoda2
 * Added support for NUMERIC.  Made type support consistent across SwingSet: INTEGER, SMALLINT, TINYINT (Integer); BIGINT (Long); FLOAT (Float); DOUBLE, NUMERIC (Double); BOOLEAN, BIT (Boolean); DATE (Date); CHAR, VARCHAR, LONGVARCHAR (String).
 *
 * Revision 1.11  2004/10/25 22:13:43  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.10  2004/10/25 19:51:03  prasanth
 * Modified to use the new SSRowSet instead of  RowSet.
 *
 * Revision 1.9  2004/10/19 21:13:07  prasanth
 * In getSQLDate function. checking if a / occurs in the string.
 * If not assuming that its in standard format yyyy-mm-dd and trying to convert
 * in to a date.
 *
 * Revision 1.8  2004/09/27 15:48:17  prasanth
 * Added function to disable insertions.
 *
 * Revision 1.7  2004/08/10 22:06:58  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.6  2004/08/02 15:27:15  prasanth
 * Made all variabled protected.
 *
 * Revision 1.5  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.4  2003/12/18 20:12:20  prasanth
 * Update class description.
 *
 * Revision 1.3  2003/12/16 18:01:40  prasanth
 * Documented versions for release 0.6.0
 *
 */