/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2004, The Pangburn Company, Inc. and Prasanth R. Pasala
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
//import javax.sql.*;
import java.sql.SQLException;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.EventObject;
import java.util.Vector;
import com.nqadmin.swingSet.datasources.SSRowSet;
/**
 * SSDataGrid.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>	
 * SSDataGrid provides a way to display information from a database in a table 
 * format (aka "spreadsheet" or "datasheet" view). The SSDataGrid takes a rowset
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
 * underlying rowset and supply a primary key for that column when a new row is
 * being added.
 *
 * While using the headers always set them before you set the rowset.
 * Otherwise the headers will not appear.
 *
 * Also if you are using column names rather than column numbers for different function
 * you have to call them only after setting the rowset. Because SSDataGrid uses the 
 * rowset to convert the column names to column numbers. If you specify the column
 * numbers you can do before or after setting the rowset, it does not matter.
 *
 * You can simply remember this order
 *	1.Set the headers
 *	2.Set the rowset
 *	3.Any other function calls.
 *
 * Simple Example:
 *
 *		//  SET THE HEADER BEFORE SETTING THE ROWSET
 * 			dataGrid.setHeaders(new String[]{"Part Name", "Color Code", " Weight", "City"});
 *			dataGrid.setRowSet(rowset);
 *			// HIDE THE PART ID COLUMN
 *			// THIS SETS THE WIDTH OF THE COLUMN TO 0
 *			//dataGrid.setHiddenColumns(new String[]{"part_id"});
 *			dataGrid.setHiddenColumns(new String[]{"part_id"});
 *			
 *			dataGrid.setMessageWindow(this);
 *			dataGrid.setUneditableColumns(new String[]{"part_id"});
 *			
 *			dataGrid.setComboRenderer("color_code",new String[]{"Red","Green","Blue"}, 
 *					new Integer[]{new Integer(0),new Integer(1),new Integer(2)});
 *			dataGrid.setDefaultValues(new int[]{1,2,3},new Object[]{new Integer(0),
 *					new Integer(20),new String("New Orleans")});		
 *			
 *			dataGrid.setPrimaryColumn("part_id");
 *			dataGrid.setSSDataValue(new SSDataValue(){
 *				public Object getPrimaryColumnValue(){
 *					// YOUR PRIMARY KEY VALUE GENERATION GOES HERE
 *					// IF ITS SOME THING USER ENTERS THEN NO PROBLEM
 *					// IF ITS AN AUTO INCREMENT FIELD THEN IT DEPENDS ON
 *					// THE DATABASE DRIVER YOU ARE USING.
 *					// IF THE UPDATEROW CAN RETRIEVE THE VALUES FOR THE ROW
 *					// WITH OUT KNOWING THE PRIMARY  KEY VALUE ITS FINE
 *					// BUT POSTGRES CAN'T UPDATE ROW WITH OUT THE PRIMARY
 *					// COLUMN.
 *					
 *					// YOUR PRIMARY KEY VALUE GENERATION GOES HERE.
 *					........
 *					........
 *					........
 *				}
 *			});		
 *					
 * Also See Examples 5, 6, 7 in the samples. 			
 *</pre><p>
 * @author	$Author$
 * @version	$Revision$
 */
 
public class SSDataGrid extends JTable {
    
	// COMPONENT WHERE MESSAGES HAVE TO BE POPPED UP.
	protected Component window = null;
    
	// ROWSET CONTAINING THE VALUES
	private SSRowSet rowset = null;
    
	// NUMBER OF COLUMNS IN THE ROWSET
	private	int columnCount = -1;
    
	// NUMBER OF RECORDS RETRIVED
	private int rowCount = -1;
    
    /**
     * Minimum width of the columns in the data grid.
     */
    protected int minColumnWidth = 100;
     
	/**
     * Table model to construct the JTable
     */
	protected SSTableModel tableModel = null;
	
	/**
     * Scrollpane used to scroll datagrid.
     */
    protected JScrollPane scrollPane = null; //new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	/**
	 * Array used to store the column numbers that have to hidden.
	 */
	protected int[] hiddenColumns = null;
	
	/**
	 * Array used to store the column numbers that have to hidden.
	 */
	protected String[] hiddenColumnNames = null;
	
	/**
	 * Variable to indicate if execute should be called on the rowset.
	 */
	protected boolean callExecute = true;

	/**
	 * Constructs a data grid with the data source set to the given rowset.
     *
	 * @param _rowset    rowset from which values have to be retrieved.
	 */
    public SSDataGrid(SSRowSet _rowset) {
		super();
//super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED );		
		rowset = _rowset;	
//		addComponent();
		init();
	}
	
	/**
	 *	Constructs an empty data grid.
	 */
	public SSDataGrid() {
		super();
		tableModel = new SSTableModel();
		
//super(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
	}
	
	
	/**
	 *	Sets the minimum column width for the data grid.
	 *@param _width - minimum column width of the each column.
	 */
	public void setColumnWidth(int _width){
		minColumnWidth = _width;
	} 
	 
	/**
	 * Set the component on which the error messages will be popped up.
	 * The error dialog will use this component as its parent component.
     *
	 * @param _window    the component that should be used when displaying error messages.
	 */	
	public void setMessageWindow(Component _window) {
		window = _window;
		tableModel.setMessageWindow(window);
	}
	
	/**
	 * Sets the callExecute property.
	 * If set to true causes the navigator to skip the execute function call on the specified rowset.
	 * (See FAQ for further details)
     *
	 * @param _execute    true if execute function call has to be skipped else false.
	 */
	public void setCallExecute(boolean _execute) {
		callExecute = _execute;
	}
	
	
	/**
	 *	Sets the allowInsertion property of the table.
	 *If set to true an addition row for inserting new rows will be displayed
	 *@param _insertions - true if new rows can be added else false.
	 */
	public void setInsertion(boolean _insertions){
		tableModel.setInsertion(_insertions);
		updateUI();
	} 
	
	/**
	 * Returns the list of selected columns.
	 * This function gets the list of selected columns from parent class 
	 * and removes any columns which are present in hidden columns.
     *
     * @return array of selected columns
	 */
	// THIS IS A STRANGE BEHAVIOUR. FOR SOME REASON SOME TIMES THE 
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
     * @return number of selected columns
     */
    public int getSelectedColumnCount(){
		int[] selectedColumns = this.getSelectedColumns();
		if (selectedColumns == null) {
			return 0;
        }
		
		return selectedColumns.length;
	}
	
	/**
	 * Initializes the data grid control. Collects metadata information about the 
	 * given rowset.
	 */
    private void init() {
		try {
		// EXECUTE THE QUERY
			if (callExecute) {
				rowset.execute();
            }
			
		// SPECIFY THE ROWSET TO THE TABLE MODEL.
			if (tableModel == null) {
				tableModel = new SSTableModel(rowset);
			} else {
				tableModel.setRowSet(rowset);
            }
			
		// GET THE ROW COUNT 	
			rowCount = tableModel.getRowCount();
		// GET THE COLUMN COUNT
			columnCount = tableModel.getColumnCount();

		} catch(SQLException se) {
			se.printStackTrace();
		}
	// SET THE TABLE MODEL FOR JTABLE
		this.setModel(tableModel);
        
	// SPECIFY THE MESSAGE WINDOW TO WHICH THE TABLE MODEL HAS TO POP UP
	// ERROR MESSAGES.
		tableModel.setMessageWindow(window);
		tableModel.setJTable(this);
		hideColumns();
		
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
			
			
			public void keyReleased(KeyEvent ke) {
			// IF CONTROL KEY IS RELEASED SET THAT CONTROL IS NOT PRESSED.
				if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
					controlPressed = false;
                }
			// IF X IS PRESSED WHILE THE CONTROL KEY IS STILL PRESSED
			// DELETE THE SELECTED ROWS.	
				if (ke.getKeyCode() == KeyEvent.VK_X) {
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
					if (window != null) {
						int returnValue = JOptionPane.showConfirmDialog(window,"You are about to delete " + rows.length + " rows. " +
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
        // THIS CAUSES THE JTABLE TO DISPLAY THE HORIZONTAL SCROLL BAR AS NEEDED.	
            this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // ADD THE JTABLE TO A SCROLL BAR
            scrollPane = new JScrollPane(this,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
            
	} // end private void init() {
		
	/**
	 * Binds the rowset to the grid.
	 * Data is taken from the new rowset.
     *
	 * @param _rowset    the rowset which acts as the data source.
	 */
	 public void setRowSet(SSRowSet _rowset) {
	 	// VARIABLE TO DETERMINE IF UI HAS TO BE UPDATED
	 	boolean updateUI = false;
	 	// IF A ROW SET ALREADY EXISTS THEN UI HAS TO BE UPDATED
	 	// ELSE YOU WILL NOT SEE CHANGES IN THE JTABLE
	 	if (rowset != null) {
	 		updateUI = true;
        }
	 		
	 	if (rowset == null) {
	 		rowset = _rowset;
	 		init();
	 	} else {
	 		rowset = _rowset;
	 		try {
	 			tableModel.setRowSet(rowset);	
	 		} catch(SQLException se) {
	 			se.printStackTrace();
	 		}
	 	// THIS IS NEEDED IF THE NUMBER OF COLUMNS IN THE NEW ROWSET
	 	// DOES NOT MATCH WITH THE OLD COLUMNS.	
	 		createDefaultColumnModel();
	 	}
	 	// UPDATE UI IF NEEDED
	 	if (updateUI) {
	 		updateUI();
        }
	 } // end public void setRowSet(RowSet _rowset) {
	 
	 /**
	  * Sets the preferred size of the scroll pane in which the JTable is embedded.
      *
	  * @param _dimension    required dimension for JTable
	  */
	 public void setPreferredSize(Dimension _dimension) {
	 	scrollPane.setPreferredSize(_dimension);
	 }
	 
	 /**
	  * Returns scroll pane with the JTable embedded in it.
      *
      * @return scroll pane with embedded JTable
	  */
	 public Component getComponent(){
	 	return scrollPane;
	 }
	 
	 /**
	  *	Sets the default values for different columns.
	  * When a new row is added these default values will be added to the columns.
	  * Please make sure that the object specified for each column is of the same type
	  * as that of the column in the database.
	  * Use the getColumnClass function in JTable to determine the exact data type.
	  * @param _columnNumbers    array containing the column numbers for which the 
	  *    defaults apply.
	  * @param _values the values for the column numbers specified in _columnNumbers.
	  */	
	 public void setDefaultValues(int[] _columnNumbers, Object[] _values) {
         if (tableModel == null) {
	 		tableModel = new SSTableModel();
         }
	 	tableModel.setDefaultValues(_columnNumbers,_values);
	 }
	 
     /**
	  *	Sets the default values for different columns.
	  * When a new row is added these default values will be added to the columns.
	  * Please make sure that the object specified for each column is of the same type
	  * as that of the column in the database.
	  * Use the getColumnClass function in JTable to determine the exact data type.
	  * @param _columnNames    array containing the column names for which the
	  *    defaults apply.
	  * @param _values    the values for the column names specified in _columnNames.
      *
	  * @throws SQLException is the specified column name is not present in the rowset
	  */	
	 public void setDefaultValues(String[] _columnNames, Object[] _values) throws SQLException {
	 	
	 	int[] columnNumbers = null;
	 	
	 	if (tableModel == null) {
	 		tableModel = new SSTableModel();
        }
	 		
	 	if ( _columnNames != null) {
	 		columnNumbers = new int[_columnNames.length];
	 	
	 	 	for (int i=0; i< _columnNames.length;i++) {
	 			columnNumbers[i] = rowset.getColumnIndex(_columnNames[i]) -1 ;
            }
	 	}
	 	
	 	tableModel.setDefaultValues(columnNumbers, _values);
	 }	
	 
	 /**
	  *	Returns the default value being used for the specified column.
	  * Returns null if a default is not in use.
      *
	  * @param _columnNumber    the column number for which default value is to be returned.
      *
	  * @return returns an object containing the default value for the requested column.
	  */
	 public Object getDefaultValue(int _columnNumber) {
	 	return tableModel.getDefaultValue(_columnNumber);
	 }
	 
	 /**
	  *	Returns the default value being used for the specified column.
	  * Returns null if a default is not in use.
	  * @param _columnName    the column name for which default value is to be returned.
      *
	  * @return returns an object containing the default value for the requested column.
      *
	  * @throws SQLException is the specified column name is not present in the rowset
	  */
	 public Object getDefaultValue(String _columnName) throws SQLException {
	 	int columnNumber = rowset.getColumnIndex(_columnName);
	 	return tableModel.getDefaultValue(columnNumber -1);
	 }
	 
    /**
     * Notification from the UIManager that the L&F has changed. Replaces the current 
     * UI object with the latest version from the UIManager. 
     * This also causes the JTable to request new values for each cell.
     * Thus refreshes the screen.
     */
    public void updateUI() {
        super.updateUI();
    }

    /**
     * Sets the column number which is the primary column for the table.
     * This is required if new rows have to be added to the JTable.
     * For this to properly work the SSDataValue object should also be provided
     * SSDataValue is used to get the value for the primary column.
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
	 * @param _columnName    the column which is the primary column.
	 */	
	public void setPrimaryColumn(String _columnName) throws SQLException {
		int columnNumber = rowset.getColumnIndex(_columnName) -1;
    	tableModel.setPrimaryColumn(columnNumber);
    }
    
    /**
     * Sets the SSDataValue interface implemention. This interface specifies
     * function to retrieve primary column values for a new row to be added.
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
     * @param _column  column name for which a date renderer is needed.
     */    
    public void setDateRenderer(String _column) throws SQLException {
    	int column = rowset.getColumnIndex(_column) -1;
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
     * @param _column  column name for which combo renderer is to be provided.
     * @param _displayItems    the actual Objects to be displayed in the combo box.
     * @param _underlyingValues    the values that have to be written to the database when an
     *  item in the combo box is selected.
     * @param _columnWidth required minimum width for this column
     */
    public void setComboRenderer(String _column, Object[] _displayItems, Object[] _underlyingValues, int _columnWidth) throws SQLException {
    	int column = rowset.getColumnIndex(_column)-1;
    	setRowHeight(20);
    	TableColumnModel columnModel = getColumnModel();
    	TableColumn tableColumn = columnModel.getColumn(column);
    	tableColumn.setCellRenderer(new ComboRenderer(_displayItems, _underlyingValues));
    	tableColumn.setCellEditor(new ComboEditor(_displayItems, _underlyingValues));
    	tableColumn.setMinWidth(_columnWidth);
    }
    	
    
    /**
     * Sets the header for the JTable.
     * This function has to be called before setting the rowset for SSDataGrid.
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
     * @param _columnNames  array specifying the column names which should be 
     *  uneditable.
     */
    public void setUneditableColumns(String[] _columnNames) throws SQLException {
    	int[] columnNumbers = null;
    	if (_columnNames != null) {
    		columnNumbers = new int[_columnNames.length];
    	
    		for (int i=0;i<_columnNames.length;i++) {
    			columnNumbers[i] = rowset.getColumnIndex(_columnNames[i]) -1;
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
     *@param _columnNumbers   array specifying the column numbers which should be 
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
     * @param _columnNames    array specifying the column names which should be 
     *  hidden
     */
    public void setHiddenColumns(String[] _columnNames) throws SQLException {
    	hiddenColumns = null;
    	tableModel.setHiddenColumns(hiddenColumns);
    	if (_columnNames != null) {
    		hiddenColumns = new int[_columnNames.length];
    		for(int i=0; i<_columnNames.length; i++) {
    			hiddenColumns[i] = rowset.getColumnIndex(_columnNames[i]) -1;
    		}
		}
		hideColumns();
    }
    
    /**
     *	Hides the columns specified in the hidden columns list.
     */
    private void hideColumns(){
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
				if (j == hiddenColumns.length) {
					column.setMinWidth(minColumnWidth);
                }
			} else {
			// SET OTHER COLUMNS MIN WIDTH TO 100
				column.setMinWidth(minColumnWidth);
			}
		}
		updateUI();
    }
    
    /**
	 * If the user has to decide on which cell has to be editable and which is not
	 * then SSCellEditable interface has to be implemented and set it for the SSTableModel.
     *
	 * @param _cellEditable    implementation of SSCellEditable interface.
	 */
	 public void setSSCellEditing(SSCellEditing _cellEditing) {
	 	tableModel.setSSCellEditing( _cellEditing );
	 }
    
    // EDITOR TO EDIT THE DATE FIELD.
    // USES THE TEXT FIELD AS THE EDITOR BUT CHANGES THE FORMAT 
    // TO MM/DD/YYYY FORMAT FROM YYYY-MM-DD FORMAT.
    private class DateEditor extends DefaultCellEditor {
    	    	
    	// CONSTRUCTOR FOR THE EDITOR CLASS
   		public DateEditor(){
   			super(new SSTextField(SSTextField.MMDDYYYY));
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
    
    // DATE RENDERER CLASS FOR RENDERER DATE COLUMNS.
    // DISPLAYS THE DATE IN MM/DD/YYYY FORMAT.
    private class DateRenderer extends DefaultTableCellRenderer {
    	
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
    
    // COMBORENDERER RENDERS THE VALUES OF THE CELL IN A COMBO BOX.
    private class ComboRenderer extends JComboBox implements TableCellRenderer {
    	
    	Object[] underlyingValues = null;
    	JLabel label = new JLabel();
    	Object[] displayValues = null;
    	
    	public ComboRenderer(Object[] _items, Object[] _underlyingValues) {
    		super(_items);
    		underlyingValues = _underlyingValues;
    		displayValues = _items;
    	}
    	
    	public Component getTableCellRendererComponent(JTable _table, Object _value, 
    		boolean _selected, boolean _hasFocus, int _row, int _column){
    		int index = -1;	
            if (getItemCount() > 0) {
//    			setSelectedIndex(getIndexOf(_value));
    			index = getIndexOf(_value);
    		} else {
    			System.out.println("Combo Renderer: No item in combo that corresponds to " + _value );
            }
//    		return this;
			if(index == -1)
				label.setText("");
			else
				label.setText(displayValues[index].toString());
			return label;
    	}
    	
    	private int getIndexOf(Object _value) {
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
    
    // COMBO BOX EDITOR FOR COLUMNS HAVING COMBO RENDERERS.
	private class ComboEditor extends DefaultCellEditor {    	
    	Object[] underlyingValues = null;
    	// SET THE CLICK COUNT TO EDIT THE COMBO AS 2
    	int clickCountToStart = 2;
//    	JComboBox comboBox = null;
    	    	
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
//    		System.out.println("Index is "+ index);
            if (index == -1) {
                return underlyingValues[0];
            }
    				
    		return underlyingValues[index];
    	}
  	
    	private int getIndexOf(Object _value) {
    		if (underlyingValues == null) {
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


        
} // end public class SSDataGrid extends JTable {



/*
 * $Log$
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
 * Calling createDefaultColumnModel function in setRowSet if the rowset is not null.
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