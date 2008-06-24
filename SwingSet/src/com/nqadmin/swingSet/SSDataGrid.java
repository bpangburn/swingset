/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2006, The Pangburn Company and Prasanth R. Pasala
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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.nqadmin.swingSet.datasources.SSRowSet;

/**
 * SSDataGrid.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * SSDataGrid provides a way to display information from a database in a table
 * format (aka "spreadsheet" or "datasheet" view). The SSDataGrid takes a SSRowSet
 * as a source of data. It also provides different cell renderers including a
 * comboboxes renderer and a date renderer.
 *
 * SSDataGrid internally uses the SSTableModel to display the information in a
 * table format. SSDataGrid also provides an easy means for displaying headers.
 * Columns can be hidden or made uneditable. In addition, it provides much finer
 * control over which cells can be edited and which cells can't be edited.  It
 * uses the SSCellEditing interface for achieving this. The implementation of
 * this interface also provides a way to specify what kind of information is valid
 * for each cell.
 *
 * SSDataGrid uses the isCellEditable() method in SSCellEditing to determine if a
 * cell is editable or not.  The cellUpdateRequested() method of SSCellEditing is
 * used to notify a user program when an update is requested. While doing so it
 * provides the present value in the cell and also the new value. Based on this
 * information the new value can be rejected or accepted by the program.
 *
 * SSDataGrid also provides an "extra" row to facilitate the addition of rows to
 * the table.  Default values for various columns can be set programmatically.  A
 * programmer can also specify which column is the primary key column for the
 * underlying SSRowSet and supply a primary key for that column when a new row is
 * being added.
 *
 * While using the headers always set them before you set the SSRowSet.
 * Otherwise the headers will not appear.
 *
 * Also if you are using column names rather than column numbers for different function
 * you have to call them only after setting the SSRowSet. Because SSDataGrid uses the
 * SSRowSet to convert the column names to column numbers. If you specify the column
 * numbers you can do before or after setting the SSRowSet, it does not matter.
 *
 * You can simply remember this order
 *  1.Set the headers
 *  2.Set the SSRowSet
 *  3.Any other function calls.
 *
 * Simple Example:
 *
 *      //  SET THE HEADER BEFORE SETTING THE SSROWSET
 *          dataGrid.setHeaders(new String[]{"Part Name", "Color Code", " Weight", "City"});
 *          dataGrid.setSSRowSet(ssRowSet);
 *          // HIDE THE PART ID COLUMN
 *          // THIS SETS THE WIDTH OF THE COLUMN TO 0
 *          //dataGrid.setHiddenColumns(new String[]{"part_id"});
 *          dataGrid.setHiddenColumns(new String[]{"part_id"});
 *
 *          dataGrid.setMessageWindow(this);
 *          dataGrid.setUneditableColumns(new String[]{"part_id"});
 *
 *          dataGrid.setComboRenderer("color_code",new String[]{"Red","Green","Blue"},
 *                  new Integer[]{new Integer(0),new Integer(1),new Integer(2)});
 *          dataGrid.setDefaultValues(new int[]{1,2,3},new Object[]{new Integer(0),
 *                  new Integer(20),new String("New Orleans")});
 *
 *          dataGrid.setPrimaryColumn("part_id");
 *          dataGrid.setSSDataValue(new SSDataValue(){
 *              public Object getPrimaryColumnValue(){
 *                  // YOUR PRIMARY KEY VALUE GENERATION GOES HERE
 *                  // IF ITS SOME THING USER ENTERS THEN NO PROBLEM
 *                  // IF ITS AN AUTO INCREMENT FIELD THEN IT DEPENDS ON
 *                  // THE DATABASE DRIVER YOU ARE USING.
 *                  // IF THE UPDATEROW CAN RETRIEVE THE VALUES FOR THE ROW
 *                  // WITH OUT KNOWING THE PRIMARY  KEY VALUE ITS FINE
 *                  // BUT POSTGRES CAN'T UPDATE ROW WITH OUT THE PRIMARY
 *                  // COLUMN.
 *
 *                  // YOUR PRIMARY KEY VALUE GENERATION GOES HERE.
 *                  ........
 *                  ........
 *                  ........
 *              }
 *          });
 *
 * Also See Examples 5, 6, 7 in the samples.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */

public class SSDataGrid extends JTable {

    /**
     * Component where messages should be popped up.
     */
    protected Component messageWindow = null;

    /**
     * SSRowSet from which component will get/set values.
     */
    protected SSRowSet sSRowSet = null;

    /**
     * Number of columns in the SSRowSet.
     */
    protected int columnCount = -1;

    /**
     * Number of records retrieved from the SSRowSet.
     */
    protected int rowCount = -1;

    /**
     * Minimum width of the columns in the data grid.
     */
    protected int columnWidth = 100;

    /**
     * Table model to construct the JTable
     */
    protected SSTableModel tableModel = new SSTableModel();

    /**
     * Scrollpane used to scroll datagrid.
     */
    protected JScrollPane scrollPane = null; //new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    /**
     * Array used to store the column numbers that have to be hidden.
     */
    protected int[] hiddenColumns = null;

    /**
     * Array used to store the column names that have to hidden.
     */
    protected String[] hiddenColumnNames = null;

    /**
     * Variable to indicate if execute() should be called on the SSRowSet.
     */
    protected boolean callExecute = true;

    /**
     * Variable to indicate if the data grid will display an additional row for
     * inserting new rows.
     */
    protected boolean insertion = true;
    
    protected boolean allowDeletion = true;

    /**
	 * @return the allowDeletion
	 */
	public boolean isAllowDeletion() {
		return allowDeletion;
	}

	/**
	 * @param allowDeletion the allowDeletion to set
	 */
	public void setAllowDeletion(boolean allowDeletion) {
		this.allowDeletion = allowDeletion;
	}

	/**
     * Constructs a data grid with the data source set to the given SSRowSet.
     *
     * @param _sSRowSet    SSRowSet from which values have to be retrieved.
     */
    public SSDataGrid(SSRowSet _sSRowSet) {
        sSRowSet = _sSRowSet;
        init();
        bind();
    }

    /**
     *  Constructs an empty data grid.
     */
    public SSDataGrid() {
        init();
    }

    /**
     * Sets the minimum column width for the data grid.
     *
     * @param _columnWidth   minimum column width of the each column
     */
    public void setColumnWidth(int _columnWidth) {
        int oldValue = columnWidth;
        columnWidth = _columnWidth;
        firePropertyChange("columnWidth", oldValue, columnWidth);
    }

    /**
     * Returns the minimum column width for the data grid.
     *
     * @return minimum column width of the each column
     */
    public int getColumnWidth() {
        return columnWidth;
    }

    /**
     * Sets the component on which error messages will be popped up.
     * The error dialog will use this component as its parent component.
     *
     * @param _messageWindow    the component that should be used when displaying error messages
     */
    public void setMessageWindow(Component _messageWindow) {
        Component oldValue = messageWindow;
        messageWindow = _messageWindow;
        firePropertyChange("messageWindow", oldValue, messageWindow);
        tableModel.setMessageWindow(messageWindow);
    }

    /**
     * Returns the component on which error messages will be popped up.
     * The error dialog will use this component as its parent component.
     *
     * @return the component that should be used when displaying error messages
     */
    public Component getMessageWindow() {
        return messageWindow;
    }

    /**
     * Sets the callExecute property.
     * If set to true causes the navigator to skip the execute function call on the specified SSRowSet.
     * (See FAQ for further details)
     *
     * @param _callExecute  true if execute function call has to be skipped else false
     */
    public void setCallExecute(boolean _callExecute) {
        boolean oldValue = callExecute;
        callExecute = _callExecute;
        firePropertyChange("callExecute", oldValue, callExecute);
    }

    /**
     * Returns the callExecute property.
     * If set to true causes the navigator to skip the execute function call on the specified SSRowSet.
     * (See FAQ for further details).
     *
     * @return true if execute function call has to be skipped else false
     */
    public boolean getCallExecute() {
        return callExecute;
    }

    /**
     * Sets the allowInsertion property of the table.
     * If set to true an additional row for inserting new rows will be displayed
     *
     * @param _insertion true if new rows can be added else false.
     */
    public void setInsertion(boolean _insertion) {
        boolean oldValue = insertion;
        insertion = _insertion;
        firePropertyChange("insertion", oldValue, insertion);
        tableModel.setInsertion(_insertion);
        updateUI();
    }

    /**
     * Returns the allowInsertion property of the table.
     * If set to true an additional row for inserting new rows will be displayed
     *
     * @return true if new rows can be added else false.
     */
    public boolean getInsertion() {
        return insertion;
    }

    /**
     * Returns the list of selected columns.
     * This function gets the list of selected columns from parent class
     * and removes any columns which are present in hidden columns.
     *
     * Currently not a bean property since there is no associated variable
     *
     * @return array of selected columns
     */
    // THIS IS A STRANGE BEHAVIOUR. FOR SOME REASON SOMETIMES THE
    // LIST OF SELECTED COLUMNS INCLUDED HIDDEN COLUMNS THIS CAUSES
    // A PROBLEM WITH COPY AND PASTE OPERATIONS. SO MAKE SURE THAT THIS
    // LIST DOES NOT CONTAIN HIDDEN COLUMNS
    public int[] getSelectedColumns() {
        // IF THERE ARE NO HIDDEN COLUMNS THEN RETURN THE SAME LIST
            if (hiddenColumns == null) {
                return super.getSelectedColumns();
            }

        // GET THE LIST OF SELECTED COLUMNS FROM SUPER CLASS.
            int[] selectedColumns = super.getSelectedColumns();
            Vector filteredColumns = new Vector();

        // FILTER OUT THE HIDDEN COLUMNS FROM THIS LIST.
            for (int i=0; i<selectedColumns.length; i++) {
                boolean found = false;
                // CHECK THIS COLUMN NUMBER WITH HIDDEN COLUMNS
                    for (int j=0; j<hiddenColumns.length; j++) {
                    // IF ITS THERES INDICATE THE SAME AND BREAK OUT.
                        if (selectedColumns[i] == hiddenColumns[j]) {
                            found = true;
                            break;
                        }
                    }
                // IF THIS COLUMN IS NOT IN HIDDEN COLUMNS ADD IT TO FILTERED LIST
                    if (!found) {
                        filteredColumns.add(new Integer(selectedColumns[i]));
                    }
            }

        // CREATE AN INT ARRAY CONTAINING THE FILETED LIST OF COLUMNS
            int[] result = new int[filteredColumns.size()];
            for (int i=0; i<filteredColumns.size(); i++) {
                result[i] = ((Integer)filteredColumns.elementAt(i)).intValue();
            }

            return result;
    }

    /**
     * Returns number of selected columns.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @return number of selected columns
     */
    public int getSelectedColumnCount() {
        int[] selectedColumns = this.getSelectedColumns();
        if (selectedColumns == null) {
            return 0;
        }

        return selectedColumns.length;
    }

    /**
     * Binds the SSRowSet to the grid.
     * Data is taken from the new SSRowSet.
     *
     * @param _sSRowSet    the SSRowSet which acts as the data source.
     */
     public void setSSRowSet(SSRowSet _sSRowSet) {
        SSRowSet oldValue = sSRowSet;
        sSRowSet = _sSRowSet;
        firePropertyChange("sSRowSet", oldValue, sSRowSet);
        bind();
     } // end public void setSSRowSet(SSRowSet _sSRowSet) {

    /**
     * Returns the SSRowSet being used to get the values.
     *
     * @return returns the SSRowSet being used.
     */
    public SSRowSet getSSRowSet() {
        return sSRowSet;
    }

    /**
     * Returns scroll pane with the JTable embedded in it.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @return scroll pane with embedded JTable
     */
     public Component getComponent(){
        return scrollPane;
     }

    /**
     * Sets the default values for different columns.
     * When a new row is added these default values will be added to the columns.
     * Please make sure that the object specified for each column is of the same type
     * as that of the column in the database.
     * Use the getColumnClass function in JTable to determine the exact data type.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _columnNumbers    array containing the column numbers for which the
     *    defaults apply.
     * @param _values the values for the column numbers specified in _columnNumbers.
     */
     public void setDefaultValues(int[] _columnNumbers, Object[] _values) {
         //if (tableModel == null) {
         //   tableModel = new SSTableModel();
         //}
         tableModel.setDefaultValues(_columnNumbers,_values);
     }

    /**
     * Sets the default values for different columns.
     * When a new row is added these default values will be added to the columns.
     * Please make sure that the object specified for each column is of the same type
     * as that of the column in the database.
     * Use the getColumnClass function in JTable to determine the exact data type.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _columnNames    array containing the column names for which the
     *    defaults apply.
     * @param _values    the values for the column names specified in _columnNames.
     *
     * @throws SQLException is the specified column name is not present in the SSRowSet
     */
     public void setDefaultValues(String[] _columnNames, Object[] _values) throws SQLException {

        int[] columnNumbers = null;

        //if (tableModel == null) {
        //    tableModel = new SSTableModel();
        //}

        if ( _columnNames != null) {
            columnNumbers = new int[_columnNames.length];

            for (int i=0; i< _columnNames.length;i++) {
                columnNumbers[i] = sSRowSet.getColumnIndex(_columnNames[i]) -1 ;
            }
        }

        tableModel.setDefaultValues(columnNumbers, _values);
     }

    /**
     * Returns the default value being used for the specified column.
     * Returns null if a default is not in use.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _columnNumber    the column number for which default value is to be returned.
     *
     * @return returns an object containing the default value for the requested column.
     */
     public Object getDefaultValue(int _columnNumber) {
        return tableModel.getDefaultValue(_columnNumber);
     }

    /**
     * Returns the default value being used for the specified column.
     * Returns null if a default is not in use.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _columnName    the column name for which default value is to be returned.
     *
     * @return returns an object containing the default value for the requested column.
     *
     * @throws SQLException is the specified column name is not present in the SSRowSet
     */
     public Object getDefaultValue(String _columnName) throws SQLException {
        int columnNumber = sSRowSet.getColumnIndex(_columnName);
        return tableModel.getDefaultValue(columnNumber -1);
     }

    /**
     * Sets the column number which is the primary column for the table.
     * This is required if new rows have to be added to the JTable.
     * For this to properly work the SSDataValue object should also be provided
     * SSDataValue is used to get the value for the primary column.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _columnNumber    the column which is the primary column.
     */
    public void setPrimaryColumn(int _columnNumber) {
        tableModel.setPrimaryColumn(_columnNumber);
    }

    /**
     * Sets the column number which is the primary column for the table.
     * This is required if new rows have to be added to the JTable.
     * For this to properly work the SSDataValue object should also be provided
     * SSDataValue is used to get the value for the primary column.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _columnName    the column which is the primary column.
     */
    public void setPrimaryColumn(String _columnName) throws SQLException {
        int columnNumber = sSRowSet.getColumnIndex(_columnName) -1;
        tableModel.setPrimaryColumn(columnNumber);
    }

    /**
     * Sets the SSDataValue interface implemention. This interface specifies
     * function to retrieve primary column values for a new row to be added.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _dataValue   implementation of SSDataValue
     */
    public void setSSDataValue(SSDataValue _dataValue) {
        tableModel.setSSDataValue(_dataValue);
    }

    /**
     * Sets a date renderer for the specified column.
     * The date will be displayed in mm/dd/yyyy format. If a date renderer
     * is not requested then the date will be displayed in a standard format(yyyy-mm-dd).
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _column   column number for which a date renderer is needed.
     */
    public void setDateRenderer(int _column) {
        TableColumnModel columnModel = getColumnModel();
        TableColumn tableColumn = columnModel.getColumn(_column);
        tableColumn.setCellRenderer(new DateRenderer());
        tableColumn.setCellEditor(new DateEditor());
    }

    /**
     * Sets a date renderer for the specified column.
     * The date will be displayed in mm/dd/yyyy format. If a date renderer
     * is not requested then the date will be displayed in a standard format(yyyy-mm-dd).
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _column  column name for which a date renderer is needed.
     */
    public void setDateRenderer(String _column) throws SQLException {
        int column = sSRowSet.getColumnIndex(_column) -1;
        TableColumnModel columnModel = getColumnModel();
        TableColumn tableColumn = columnModel.getColumn(column);
        tableColumn.setCellRenderer(new DateRenderer());
        tableColumn.setCellEditor(new DateEditor());
    }

    /**
     * Sets a combo box renderer for the specified column.
     * This is use full to limit the values that go with a column or if an underlying code
     * is do be displayed in a more meaningfull manner.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _column  column number for which combo renderer is to be provided.
     * @param _displayItems    the actual Objects to be displayed in the combo box.
     * @param _underlyingValues    the values that have to be written to the database when an
     *  item in the combo box is selected.
     */
    public void setComboRenderer(int _column, Object[] _displayItems, Object[] _underlyingValues) {
        setComboRenderer(_column, _displayItems, _underlyingValues, 250);
    }

    /**
     * Sets a combo box renderer for the specified column.
     * This is use full to limit the values that go with a column or if an underlying code
     * is do be displayed in a more meaningfull manner.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _column  column number for which combo renderer is to be provided.
     * @param _displayItems    the actual Objects to be displayed in the combo box.
     * @param _underlyingValues    the values that have to be written to the database when an
     *  item in the combo box is selected.
     */
    public void setComboRenderer(int _column, Object[] _displayItems, Object[] _underlyingValues, int _columnWidth) {
        setRowHeight(20);
        TableColumnModel columnModel = getColumnModel();
        TableColumn tableColumn = columnModel.getColumn(_column);
        tableColumn.setCellRenderer(new ComboRenderer(_displayItems, _underlyingValues));
        tableColumn.setCellEditor(new ComboEditor(_displayItems, _underlyingValues));
        tableColumn.setMinWidth(_columnWidth);
    }

    /**
     * Sets a combo box renderer for the specified column.
     * This is use full to limit the values that go with a column or if an underlying code
     * is do be displayed in a more meaningfull manner.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _column  column name for which combo renderer is to be provided.
     * @param _displayItems    the actual Objects to be displayed in the combo box.
     * @param _underlyingValues    the values that have to be written to the database when an
     *  item in the combo box is selected.
     */
    public void setComboRenderer(String _column, Object[] _displayItems, Object[] _underlyingValues) throws SQLException {
        setComboRenderer(_column, _displayItems, _underlyingValues, 250);
    }

    /**
     * Sets a combo box renderer for the specified column.
     * This is use full to limit the values that go with a column or if an underlying code
     * is do be displayed in a more meaningfull manner.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _column  column name for which combo renderer is to be provided.
     * @param _displayItems    the actual Objects to be displayed in the combo box.
     * @param _underlyingValues    the values that have to be written to the database when an
     *  item in the combo box is selected.
     * @param _columnWidth required minimum width for this column
     */
    public void setComboRenderer(String _column, Object[] _displayItems, Object[] _underlyingValues, int _columnWidth) throws SQLException {
        int column = sSRowSet.getColumnIndex(_column)-1;
        setComboRenderer(column, _displayItems, _underlyingValues, _columnWidth);
    }

    /**
     * Sets a check box renderer for the specified column.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _column - name ofthe column for which check box rendering is needed.
     */
    public void setCheckBoxRenderer(String _column) throws SQLException{
    	int column = sSRowSet.getColumnIndex(_column) - 1;
    	setCheckBoxRenderer(column);
    }

    /**
     * Sets a check box renderer for the specified column.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _column - column number for which check box rendering is needed.
     */
    public void setCheckBoxRenderer(int _column) throws SQLException{
    	TableColumnModel columnModel = getColumnModel();
    	TableColumn tableColumn = columnModel.getColumn(_column);
    	tableColumn.setCellRenderer(new CheckBoxRenderer());
    	tableColumn.setCellEditor(new CheckBoxEditor());
    }

    /**
     * Sets the header for the JTable.
     * This function has to be called before setting the SSRowSet for SSDataGrid.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _headers array of string objects representing the header of each column.
     */
    public void setHeaders(String[] _headers) {
        tableModel.setHeaders(_headers);
    }

    /**
     * Sets the uneditable columns.
     * The columns specified as uneditable will not be available for user to edit.
     * This overrides the isCellEditable function in SSCellEditing.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _columnNumbers  array specifying the column numbers which should be
     *  uneditable.
     */
    public void setUneditableColumns(int[] _columnNumbers) {
        tableModel.setUneditableColumns(_columnNumbers);
    }

    /**
     * Sets the uneditable columns.
     * The columns specified as uneditable will not be available for user to edit.
     * This overrides the isCellEditable function in SSCellEditing.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _columnNames  array specifying the column names which should be
     *  uneditable.
     */
    public void setUneditableColumns(String[] _columnNames) throws SQLException {
        int[] columnNumbers = null;
        if (_columnNames != null) {
            columnNumbers = new int[_columnNames.length];

            for (int i=0;i<_columnNames.length;i++) {
                columnNumbers[i] = sSRowSet.getColumnIndex(_columnNames[i]) -1;
            }
        }

        tableModel.setUneditableColumns(columnNumbers);
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
     * Currently not a bean property since there is no associated variable.
     *
     * @param _columnNumbers   array specifying the column numbers which should be
     *  hidden
     */
    public void setHiddenColumns(int[] _columnNumbers) {
        hiddenColumns = _columnNumbers;
        tableModel.setHiddenColumns(_columnNumbers);
        hideColumns();
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
     * Currently not a bean property since there is no associated variable.
     *
     * @param _columnNames    array specifying the column names which should be
     *  hidden
     */
    public void setHiddenColumns(String[] _columnNames) throws SQLException {
        hiddenColumns = null;
        tableModel.setHiddenColumns(hiddenColumns);
        if (_columnNames != null) {
            hiddenColumns = new int[_columnNames.length];
            for(int i=0; i<_columnNames.length; i++) {
                hiddenColumns[i] = sSRowSet.getColumnIndex(_columnNames[i]) -1;
            }
        }
        hideColumns();
    }

    /**
     * If the user has to decide on which cell has to be editable and which is not
     * then SSCellEditable interface has to be implemented and set it for the SSTableModel.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _cellEditing    implementation of SSCellEditable interface.
     */
    public void setSSCellEditing(SSCellEditing _cellEditing) {
        tableModel.setSSCellEditing( _cellEditing );
    }
    
    /**
     *    This is the default editor for Numeric, String & Object column types.
     */
    class DefaultEditor extends DefaultCellEditor{
        /**
         *  Value of the editor.
         */
        Object value;
        
        /**
         *  Constructor to instanciate an object of column type from a string.
         */
        java.lang.reflect.Constructor constructor;
        
              
        /**
         *  Constructs Default Editor.
         */
        public DefaultEditor() {
            super(new SSTextField());
            MyListener listener = new MyListener();
            getComponent().addFocusListener(listener);
            getComponent().addKeyListener(listener);
        }
        
        /**
         *  Implementation of KeyListener & FocusListener for the editor component.
         */
        private class MyListener implements KeyListener, FocusListener{
            
            int keyPressed = 0;
            boolean hasFocus = false;
            
        // ASSUMPTION HERE IS THAT THE EDITOR WILL NOT GET THE KEY PRESSED EVENT
        // FOR THE FIRST KEY (WHICH TRIGGERS THE EDITOR, EVENT IS CONSUMED BY JTABLE)
            /**
             *  Increment the key pressed variable when ever there is a key pressed event.
             *only exception is tab key.
             */
            public void keyPressed(KeyEvent ke){
                if(ke.getKeyCode() != KeyEvent.VK_TAB)
                    keyPressed++;
            }
            
            /**
             *  Based on if this is first key release event the contents will be cleared
             */
            public void keyReleased(KeyEvent ke){
                JComponent editor = (JComponent)DefaultEditor.this.getComponent();
                if(editor instanceof JTextField){
                    if(keyPressed == 0 && Character.isLetterOrDigit(ke.getKeyChar())){
                        ((JTextField)editor).setText(String.valueOf(ke.getKeyChar()));
                    }
                }
                keyPressed--;
                if(keyPressed < 0)
                    keyPressed = 0;
                
            }   
            
            public void keyTyped(KeyEvent ke){
            } 
            
            /**
             * 
             */
            public void focusGained(FocusEvent fe){
                ((SSTextField)getComponent()).selectAll();
                hasFocus = true;
            }
            
            /**
             *  sets the keyPressed variable to zero.
             */
            public void focusLost(FocusEvent fe){
            // SET THE KEYPRESSED TO ZERO AS THE EDITOR HAS LOST THE FOCUS.    
                hasFocus = false;
                keyPressed = 0;
            }
        }

        public boolean stopCellEditing() {
            String s = (String)super.getCellEditorValue();
            
            if (s.trim().equals("")){
                if (constructor.getDeclaringClass() == String.class) {
                    value = s;
                }
                super.stopCellEditing();
            }
    
            try {
                value = constructor.newInstance(new Object[]{s});
            }catch (Exception e) {
            // DRAW A RED BORDER IF THE VALUE OBJECT CAN'T BE CREATED.
            // PROBABLY THE DATA ENTERED IS NOT RIGHT (STRING IN NUMBER FIELD OR VICE-VERSA)    
                ((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
                return false;
            }
            
            return super.stopCellEditing();
        }
    
        public Component getTableCellEditorComponent(JTable table, Object value,
                             boolean isSelected, int row, int column) {
        // SET INITIAL VALUE TO NULL.    
            this.value = null;
            
            ((JComponent)getComponent()).setBorder(new LineBorder(Color.black));
            
        // GET A CONSTRUCTOR FOR AN OBJECT OF THE CURRENT COLUMN TYPE.
        // THIS IS NEEDED FOR RETURNING THE VALUE IN COLUMN CLASS OBJECT 
            try {
                Class type = table.getColumnClass(column);
                if (type == Object.class) {
                    type = String.class;
                }
                constructor = type.getConstructor(new Class[]{String.class});
            }catch (Exception e) {
                return null;
            }
            
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
    
        /**
         *  Returns the cell value.
         */
        public Object getCellEditorValue() {
            return value;
        }
    
    }

    
    /**
     * Initialization code.
     */
    protected void init() {
        
        // FORCE JTABLE TO SURRENDER TO THE EDITOR WHEN KEYSTROKES CAUSE THE EDITOR TO BE ACTIVATED
            setSurrendersFocusOnKeystroke(true);
            setDefaultEditor(Number.class, new DefaultEditor());
            setDefaultEditor(String.class, new DefaultEditor());
            setDefaultEditor(Object.class, new DefaultEditor());
            
        // ADD KEY LISTENER TO JTABLE.
        // THIS IS USED FOR DELETING THE ROWS
        // ALLOWS MULTIPLE ROW DELETION.
        // KEY SEQUENCE FOR DELETING ROWS IS CTRL-X.
            this.addKeyListener(new KeyAdapter() {
                private boolean controlPressed = false;

            // IF THE KEY PRESSED IS CONTROL STORE THAT INFO.
                public void keyPressed(KeyEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
                        controlPressed = true;
                    }
                }

            // HANDLE KEY RELEASES
                public void keyReleased(KeyEvent ke) {
                // IF CONTROL KEY IS RELEASED SET THAT CONTROL IS NOT PRESSED.
                    if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
                        controlPressed = false;
                    }
                // IF X IS PRESSED WHILE THE CONTROL KEY IS STILL PRESSED
                // DELETE THE SELECTED ROWS.
                    if (ke.getKeyCode() == KeyEvent.VK_X) {
                    	if(!allowDeletion) {
                    		return;
                    	}
                    	
                        if (! controlPressed) {
                            return;
                        }
                    // GET THE NUMBER OF ROWS SELECTED
                        int numRows = getSelectedRowCount();
                        if (numRows == 0) {
                            return;
                        }
                    // GET LIST OF ROWS SELECTED
                        int[] rows = getSelectedRows();
                    // IF USER HAS PROVIDED A PARENT COMPONENT FOR ERROR MESSAGES
                    // CONFIRM THE DELETION
                        if (messageWindow != null) {
                            int returnValue = JOptionPane.showConfirmDialog(messageWindow,"You are about to delete " + rows.length + " rows. " +
                                "\nAre you sure you want to delete the rows?");
                            if (returnValue != JOptionPane.YES_OPTION) {
                                return;
                            }
                        }
                    // START DELETING THE ROWS IN BOTTON UP FASHION
                    // IN DOING SO YOU RETAIN THE ROW NUMBERS THAT HAVE TO BE DELETED
                    // IF YOU DO IT TOP DOWN THE ROW NUMBERING CHANGES AS SOON AS A
                    // ROW IS DELETED AS A RESULT LOT OF CARE HAS TO BE TAKEN
                    // TO IDENTIFY THE NEW ROW NUMBERS AND THEN DELETE THE ROWS
                    // INSTEAD OF THAT ITS MUCH EASIER IF YOU DO IT BOTTOM UP.
                        for (int i=rows.length -1;i>=0;i--) {
                            tableModel.deleteRow(rows[i]);
                        }
                        updateUI();
                    }
                }
            });

        // CREATE AN INSTANCE OF KEY ADAPTER ADD PROVIDE THE PRESET GRID TO THE ADAPTER.
        // THIS IS FOR COPY AND PASTE SUPPORT
            SSTableKeyAdapter keyAdapter = new SSTableKeyAdapter(this);
            keyAdapter.setAllowInsertion(true);

        // SET THE TABLE MODEL FOR JTABLE
        //    this.setModel(tableModel);

        // SPECIFY THE MESSAGE WINDOW TO WHICH THE TABLE MODEL HAS TO POP UP
        // ERROR MESSAGES.
            tableModel.setMessageWindow(messageWindow);
            tableModel.setJTable(this);

        // THIS CAUSES THE JTABLE TO DISPLAY THE HORIZONTAL SCROLL BAR AS NEEDED.
        // CODE IN HIDECOLUMNS FUNCTION DEPENDS ON THIS VARIABLE.
            this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // ADD THE JTABLE TO A SCROLL BAR
            scrollPane = new JScrollPane(this,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );

    } // end protected void init() {

    /**
     * Initializes the data grid control. Collects metadata information about the
     * given SSRowSet.
     */
    protected void bind() {

        try {
        // EXECUTE THE QUERY
            if (callExecute) {
                sSRowSet.execute();
            }

        // SPECIFY THE SSROWSET TO THE TABLE MODEL.
        //    if (tableModel == null) {
        //        tableModel = new SSTableModel(sSRowSet);
        //    } else {
                tableModel.setSSRowSet(sSRowSet);
        //    }

        // SET THE TABLE MODEL FOR JTABLE
            this.setModel(tableModel);

        // GET THE ROW COUNT
            rowCount = tableModel.getRowCount();

        // GET THE COLUMN COUNT
            columnCount = tableModel.getColumnCount();

        } catch(SQLException se) {
            se.printStackTrace();
        }

        // THIS IS NEEDED IF THE NUMBER OF COLUMNS IN THE NEW SSROWSET
        // DOES NOT MATCH WITH THE OLD COLUMNS.
            createDefaultColumnModel();

        // HIDE COLUMNS AS NEEDED - ALSO CALLS updateUI()
            hideColumns();

        // UPDATE DISPLAY
        //    updateUI();

    } // end protected void bind() {

    /**
     * Hides the columns specified in the hidden columns list.
     */
    protected void hideColumns(){

    // SET THE MINIMUM WIDTH OF COLUMNS
        TableColumnModel columnModel = this.getColumnModel();
        TableColumn column;
        for (int i=columnModel.getColumnCount()-1;i>=0;i--) {
            column = columnModel.getColumn(i);
            int j = -1;
            
            if (hiddenColumns != null) {
            // SET THE WIDTH OF HIDDEN COLUMNS AS 0
                for (j=0; j<hiddenColumns.length;j++) {
                    if (hiddenColumns[j] == i) {
                        column.setMaxWidth(0);
                        column.setMinWidth(0);
                        column.setPreferredWidth(0);
                        break;
                    }
                }
            // AUTO RESIZE IS SET TO OFF IN THE INIT FUNCTION.
            // SO IF IT IS NOT IN AUTO RESIZE MODE THEN USER HAS REQUESTED 
            // AUTO RESIZING. SO DON'T SET ANY SPECIFIC SIZE TO THE COLUMNS.    
                if (j == hiddenColumns.length) {
                    if(getAutoResizeMode() == AUTO_RESIZE_OFF){
                        column.setPreferredWidth(columnWidth);
                    }
                        
                }
            } else {
            // SET OTHER COLUMNS MIN WIDTH TO 100
                if(getAutoResizeMode() == AUTO_RESIZE_OFF){
                    column.setPreferredWidth(columnWidth);
                }
                    
            }
        }
        updateUI();
    }

    /**
     * Editor for date fields.  Used the SSTextField as the editor, but changes
     * the format to mm/dd/yyy from yyyy-mm-dd.
     */
    protected class DateEditor extends DefaultCellEditor {

        // CONSTRUCTOR FOR THE EDITOR CLASS
        public DateEditor(){
            super(new SSTextField(SSTextField.MMDDYYYY));
            getComponent().addKeyListener(new KeyAdapter(){
                int keyPressed = 0;
                public void keyPressed(KeyEvent ke){
                    if(ke.getKeyCode() != KeyEvent.VK_TAB)
                        keyPressed++;
                }
                public void keyReleased(KeyEvent ke){
                    JComponent editor = (JComponent)DateEditor.this.getComponent();
                    if(editor instanceof JTextField){
                        if(keyPressed == 0){
                            ((JTextField)editor).setText(String.valueOf(ke.getKeyChar()));
                        }
                    }
                    keyPressed--;
                    if(keyPressed < 0)
                        keyPressed = 0;
                }
            });
        }

        // RETURNS THE TEXTFIELD WITH THE GIVEN DATE IN THE TEXTFIELD
        // (AFTER THE FORMAT IS CHANGED TO MM/DD/YYYY
        public synchronized Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

            if (value instanceof Date) {
                Date date = (Date)value;
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                String strDate = "" + (calendar.get(Calendar.MONTH) + 1) + "/" +
                     calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
                    return super.getTableCellEditorComponent(table, strDate, isSelected, row, column);
            }

            return super.getTableCellEditorComponent(table, value, isSelected, row, column);

        }

        // RETURNS A DATE OBJECT REPRESENTING THE VALUE IN THE CELL.
        public Object getCellEditorValue(){
            String strDate = ((JTextField)(DateEditor.this.getComponent())).getText();
            StringTokenizer strtok = new StringTokenizer(strDate, "/", false);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, Integer.parseInt(strtok.nextToken())-1);
            calendar.set(Calendar.DATE, Integer.parseInt(strtok.nextToken()));
            calendar.set(Calendar.YEAR, Integer.parseInt(strtok.nextToken()));
            return new Date(calendar.getTimeInMillis());
        }

        public boolean isCellEditable(EventObject event){
            // IF NUMBER OF CLICKS IS LESS THAN THE CLICKCOUNTTOSTART RETURN FALSE
            // FOR CELL EDITING.
            if (event instanceof MouseEvent) {
                return ((MouseEvent)event).getClickCount() >= getClickCountToStart();
            }

            return true;
        }
    }

	/**
     * Renderer for check box fields.
     */
	protected class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

		public CheckBoxRenderer(){
			super();
		}

		public Component getTableCellRendererComponent(JTable _table, Object _value,
            boolean _selected, boolean _hasFocus, int _row, int _column){

            if(_value instanceof Boolean){
            	if(((Boolean)_value).booleanValue()){
            		this.setSelected(true);
            	}
            	else{
            		this.setSelected(false);
            	}
            }
            else if(_value instanceof Integer){
            	if( ((Integer)_value).intValue() != 0){
            		this.setSelected(true);
            	}
            	else{
            		this.setSelected(false);
            	}
            }
            else{
            	System.out.println("Can't set check box value. Unknown data type.");
            	System.out.println("Column type should be Boolean or Integer for check box columns.");
            }

            return this;
        }

    }

	/**
     * Editor for check box fields.
     */
    protected class CheckBoxEditor extends DefaultCellEditor {
    // VARIABLE TO STORE THE COLUMN CLASS.
	   	protected int columnClass = 0;

    	public CheckBoxEditor(){
    		super(new JCheckBox());
    	}

    	public Component getTableCellEditorComponent(JTable _table, Object _value,
            boolean _selected, int _row, int _column) {

		// GET THE COMPONENT RENDERING THE VALUE.
			JCheckBox checkBox = (JCheckBox)getComponent();

		// CHECK THE TYPE OF COLUMN, IT SHOULD BE THE SAME AS THE TYPE OF _VALUE.
            if(_value instanceof Boolean){
            // STORE THE TYPE OF COLUMN WE NEED THIS WHEN EDITOR HAS TO RETURN
            // VALUE BACK.
            	columnClass = java.sql.Types.BOOLEAN;
            // BASED ON THE VALUE CHECK THE BOX OR UNCHECK IT.
            	if(((Boolean)_value).booleanValue()){
            		checkBox.setSelected(true);
            	}
            	else{
            		checkBox.setSelected(false);
            	}
            }
        // IF THE COLUMN CLASS IS INTEGER
            else if(_value instanceof Integer){
            // STORE THE COLUMN CLASS.
            	columnClass = java.sql.Types.INTEGER;
            // BASED ON THE INTEGER VALUE CHECK OR UNCHECK THE CHECK BOX.
            // A VALUE OF 0 IS CONSIDERED TRUE - CHECK BOX IS CHECKED.
            // ANY OTHER VALUE IS CONSIDERED FALSE - UNCHECK THE CHECK BOX.
            	if( ((Integer)_value).intValue() != 0){
            		checkBox.setSelected(true);
            	}
            	else{
            		checkBox.setSelected(false);
            	}
            }
        // IF THE COLUMN CLASS IS NOT BOOLEAN OR INTEGER
        // PRINT OUT ERROR MESSAGE.
            else{
            	System.out.println("Can't set check box value. Unknown data type.");
            	System.out.println("Column type should be Boolean or Integer for check box columns.");
            }
		// RETURN THE EDITOR COMPONENT
            return checkBox;
        }

        public Object getCellEditorValue() {
        // GET THE COMPONENT AND CHECK IF IT IS CHECKED OR NOT.
            if(((JCheckBox)getComponent()).isSelected()){
            // CHECK THE COLUMN TYPE AND RETURN CORRESPONDING OBJECT.
            // IF IT IS INTEGER THEN 1 IS CONSIDERED TRUE & 0 FALSE.
            	if(columnClass == java.sql.Types.BOOLEAN){
            		return new Boolean(true);
            	}
            	else{
            		return new Integer(1);
            	}
            }
            else{
            	if(columnClass == java.sql.Types.BOOLEAN){
            		return new Boolean(false);
            	}
            	else{
            		return new Integer(0);
            	}
            }
        }
    }

	/**
     * Renderer for date fields.  Displays dates using mm/dd/yyyy format.
     */
    protected class DateRenderer extends DefaultTableCellRenderer {

        public  void setValue(Object value) {
            if (value instanceof java.sql.Date) {
                Date date = (Date)value;
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                String strDate = "" + (calendar.get(Calendar.MONTH)+1) + "/" +
                    calendar.get(Calendar.DAY_OF_MONTH)  + "/" + calendar.get(Calendar.YEAR);
                setHorizontalAlignment(SwingConstants.CENTER);
                setText(strDate);
            } else {
                super.setValue(value);
            }
        }
    }

	/**
     * Renderer for combo box fields.
     */
//    protected class ComboRenderer extends JComboBox implements TableCellRenderer {
    protected class ComboRenderer extends DefaultTableCellRenderer.UIResource {
        Object[] underlyingValues = null;
//        JLabel label = new JLabel();
        Object[] displayValues = null;

        public ComboRenderer(Object[] _items, Object[] _underlyingValues) {
//            super(_items);
            underlyingValues = _underlyingValues;
            displayValues = _items;
        }

        public Component getTableCellRendererComponent(JTable _table, Object _value,
            boolean _selected, boolean _hasFocus, int _row, int _column){
                
            JLabel label = (JLabel)super.getTableCellRendererComponent(_table, _value, _selected, _hasFocus, _row, _column);
            
            int index = -1;
            if (displayValues.length > 0) {
//              setSelectedIndex(getIndexOf(_value));
                index = getIndexOf(_value);
            } else {
                System.out.println("Combo Renderer: No item in combo that corresponds to " + _value );
            }
//          return this;

            if (index == -1) {
                label.setText("");
            } else {
                label.setText(displayValues[index].toString());
            }
            return label;
        }

        protected int getIndexOf(Object _value) {
            if (_value == null) {
                return -1;
            }
            if (underlyingValues == null) {
                return ((Integer)_value).intValue();
            }
            for (int i=0;i<underlyingValues.length;i++) {
                if (underlyingValues[i].equals(_value)) {
                    return i;
                }
            }
            return 0;
        }
    }

	/**
     * Editor for combo box fields.
     */
    protected class ComboEditor extends DefaultCellEditor {
        Object[] underlyingValues = null;
        // SET THE CLICK COUNT TO EDIT THE COMBO AS 2
        int clickCountToStart = 2;
//      JComboBox comboBox = null;

        public ComboEditor(Object[] _items, Object[] _underlyingValues) {
            super(new JComboBox(_items));
            underlyingValues = _underlyingValues;
        }

        public boolean isCellEditable(EventObject event) {
            if (event instanceof MouseEvent) {
                return ((MouseEvent)event).getClickCount() >= clickCountToStart;
            }
            return true;
        }

        public Component getTableCellEditorComponent(JTable _table, Object _value,
            boolean _selected, int _row, int _column) {

            JComboBox comboBox = (JComboBox)getComponent();
            comboBox.setSelectedIndex(getIndexOf(_value));
            return comboBox;
        }

        public Object getCellEditorValue() {
            if (underlyingValues == null) {
                return new Integer( ((JComboBox)getComponent()).getSelectedIndex());
            }

            int index = ((JComboBox)getComponent()).getSelectedIndex();
//          System.out.println("Index is "+ index);
            if (index == -1) {
                return underlyingValues[0];
            }

            return underlyingValues[index];
        }

        protected int getIndexOf(Object _value) {
            if (underlyingValues == null) {
            	// IF THE VALUE IS NULL THEN SET THE DISPLAY ON THE COMBO TO BLANK (INDEX -1)
            	if(_value == null)
            		return -1;
                return ((Integer)_value).intValue();
            }
            for (int i=0;i<underlyingValues.length;i++) {
                if (underlyingValues[i].equals(_value)) {
                    return i;
                }
            }

            return -1;
        }
    }



// DEPRECATED STUFF....................

    /**
     * Sets the new SSRowSet for the combo box.
     *
     * @param _sSRowSet  SSRowSet to which the combo has to update values.
     *
     * @deprecated
     * @see #setSSRowSet
     */
    public void setRowSet(SSRowSet _sSRowSet) {
        setSSRowSet(_sSRowSet);
    }

} // end public class SSDataGrid extends JTable {



/*
 * $Log$
 * Revision 1.36  2007/10/26 20:32:46  prasanth
 * Added ability to specify if deletions should be allowed.
 *
 * Revision 1.35  2006/05/15 16:10:38  prasanth
 * Updated copy right
 *
 * Revision 1.34  2005/07/26 21:02:27  prasanth
 * Setting the column width only if autoResizeMode = AUTO_RESIZE_OFF.
 * This is set in init.
 *
 * Revision 1.33  2005/03/09 21:59:41  prasanth
 * 1. Using DefaultTableCellRenderer.UIResource as parent class for ComboRenderer.
 * 2. Added custom editor for Numeric, String, & Object class types.
 *
 * Revision 1.32  2005/03/03 15:04:44  yoda2
 * Added setSurrendersFocusOnKeystroke(true); to init() to force the JTable to surrender the focus to the editor following keystroke-based navigation.  This seems to fix the problem with editors not working in the DataGrid following tab-based cell navigation.
 *
 * Revision 1.31  2005/02/22 15:16:09  yoda2
 * Removed preferredSize datamember along with setter & getter.  These all exist in the parent class.
 *
 * Revision 1.30  2005/02/13 15:38:20  yoda2
 * Removed redundant PropertyChangeListener and VetoableChangeListener class variables and methods from components with JComponent as an ancestor.
 *
 * Revision 1.29  2005/02/12 03:29:26  yoda2
 * Added bound properties (for beans).
 *
 * Revision 1.28  2005/02/11 22:59:28  yoda2
 * Imported PropertyVetoException and added some bound properties.
 *
 * Revision 1.27  2005/02/11 20:16:02  yoda2
 * Added infrastructure to support property & vetoable change listeners (for beans).
 *
 * Revision 1.26  2005/02/10 20:13:00  yoda2
 * Setter/getter cleanup & method reordering for consistency.
 *
 * Revision 1.25  2005/02/07 22:47:14  yoda2
 * Replaced internal calls to setRowSet() with calls to setSSRowSet().
 *
 * Revision 1.24  2005/02/07 22:34:10  yoda2
 * Fixed infinite loop in deprecated setRowSet() which was calling setRowSet() rather than setSSRowSet()
 *
 * Revision 1.23  2005/02/07 22:20:32  yoda2
 * JavaDoc cleanup.
 *
 * Revision 1.22  2005/02/04 22:48:53  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.21  2005/02/01 17:32:37  yoda2
 * API cleanup.
 *
 * Revision 1.20  2004/12/10 18:59:47  prasanth
 * Modified the getCellEditorValue function of CheckBoxEditor inner class.
 *
 * Revision 1.19  2004/12/09 18:36:04  prasanth
 * Added CheckBox rendering support.
 *
 * Revision 1.18  2004/11/11 14:45:33  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.17  2004/11/01 15:53:30  yoda2
 * Fixed various JavaDoc errors.
 *
 * Revision 1.16  2004/10/25 22:03:17  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.15  2004/10/25 19:51:02  prasanth
 * Modified to use the new SSRowSet instead of  RowSet.
 *
 * Revision 1.14  2004/10/22 17:38:56  prasanth
 * Using SSTextField for date mask.
 *
 * Revision 1.13  2004/10/19 21:14:36  prasanth
 * Added getCellEditorValue function for  date cell editor class.
 * This way the editor will return a Date object rather than a string as value
 * of the cell.
 *
 * Revision 1.12  2004/10/06 23:14:12  prasanth
 * Added function to set minimum column widths.
 * Added function to set combo box column widths.
 *
 * Revision 1.11  2004/09/27 15:47:19  prasanth
 * Added hideColumns function.
 * Calling createDefaultColumnModel function in setRowSet if the sSRowSet is not null.
 *
 * Revision 1.10  2004/08/10 22:06:59  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.9  2004/08/09 21:29:44  prasanth
 * The default selection of first item in combo box renderer is removed.
 * If a default value is specified it will be selected else no selected item.
 *
 * Revision 1.8  2004/08/02 14:48:39  prasanth
 * 1. Added getSelectedColumnCount and getSelectedColumns functions.
 * 2. Added the SSTableKeyAdapter instance for copy and paste support.
 *
 * Revision 1.7  2004/03/08 16:59:32  prasanth
 * Added callExecute function to let users decide whether execute should
 * be called or not.
 *
 * Revision 1.6  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.5  2004/02/23 16:47:41  prasanth
 * Println statements are commented out.
 *
 * Revision 1.4  2003/12/18 20:12:01  prasanth
 * Update class description.
 *
 * Revision 1.3  2003/12/16 18:01:40  prasanth
 * Documented versions for release 0.6.0
 *
 */