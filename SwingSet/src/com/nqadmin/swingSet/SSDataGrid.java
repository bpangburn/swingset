/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003, The Pangburn Company, Inc. and Prasanth R. Pasala
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
import javax.sql.*;
import java.sql.SQLException;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.EventObject;

/**
 * SSDataGrid.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>	
 *	SSDataGrid provides a way to display information from a database in a table 
 *format (aka "spreadsheet" or "datasheet" view). The SSDataGrid takes a rowset
 *as a source of data. It also provides different cell renderers including a
 *comboboxes renderer and a date renderer.
 *
 *	SSDataGrid internally uses the SSTableModel to display the information in a 
 *table format. SSDataGrid also provides an easy means for displaying headers.
 *Columns can be hidden or made uneditable. In addition, it provides much finer
 *control over which cells can be edited and which cells can't be edited.  It
 *uses the SSCellEditing interface for achieving this. The implementation of
 *this interface also provides a way to specify what kind of information is valid
 *for each cell.
 *
 *	SSDataGrid uses the isCellEditable() method in SSCellEditing to determine if a
 *cell is editable or not.  The cellUpdateRequested() method of SSCellEditing is
 *used to notify a user program when an update is requested. While doing so it
 *provides the present value in the cell and also the new value. Based on this
 *information the new value can be rejected or accepted by the program.
 *
 *	SSDataGrid also provides an "extra" row to facilitate the addition of rows to
 *the table.  Default values for various columns can be set programmatically.  A
 *programmer can also specify which column is the primary key column for the
 *underlying rowset and supply a primary key for that column when a new row is
 *being added.
 *
 *	While using the headers always set them before you set the rowset.
 *Otherwise the headers will not appear.
 *
 *	Also if you are using column names rather than column numbers for different function
 *you have to call them only after setting the rowset. Because SSDataGrid uses the 
 *rowset to convert the column names to column numbers. If you specify the column
 *numbers you can do before or after setting the rowset, it does not matter.
 *
 *You can simply remember this order
 *	1.Set the headers
 *	2.Set the rowset
 *	3.Any other function calls.
 *
 *Simple Example:
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
 */
 
public class SSDataGrid extends JTable
{
	// COMPONENT WHERE MESSAGES HAVE TO BE POPPED UP.
	Component window = null;
	
	
	// JTABLE TO DISPLAY THE VALUES
//	private  JTable     grid 			= null;
	// BUTTONS TO THE RIGHT OF EACH ROW
//	private  JButton[]  btnSelectRow 	= null;
	// ROWSET CONTAINING THE VALUES
	private  RowSet  	rowset 			= null;
	// NUMBER OF COLUMNS IN THE ROW SET
	private	 int 		columnCount 	= -1;
	// NUMBER OF RECORDS RETRIVED
	private  int 		rowCount    	= -1;
	// METADATA INFO OF THE GIVEN ROWSET
	private  RowSetMetaData  metaData	= null;
	//TABLE MODEL TO CONSTRUCT THE JTABLE
	SSTableModel tableModel 			= null;
	
	JScrollPane scrollPane = null; //ew JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	/**
	 *	Array used to store the column numbers that have to hidden.
	 */
	protected int[] hiddenColumns = null;
	
	/**
	 *	Array used to store the column numbers that have to hidden.
	 */
	protected String[] hiddenColumnNames = null;

	/**
	 *	Constructs a data grid with the data source set to the given rowset.
	 *@param _rowset  rowset from which values have to be retrieved.
	 */
	public SSDataGrid(RowSet _rowset)
	{
		super();
//super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED );		
		rowset = _rowset;	
		init();
	}
	
	/**
	 *	Constructs an empty data grid.
	 */
	public SSDataGrid(){
		super();
		tableModel = new SSTableModel();
//super(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
	}
	
	/**
	 *	Set the component on which the error messages will be popped up.
	 *The error dialog will use this component as its parent component.
	 *@param _window the component that should be used when displaying error messages.
	 */	
	public void setMessageWindow(Component _window){
		window = _window;
		tableModel.setMessageWindow(window);
	}
	
	/**
	 *	Initializes the data grid control. Collects metadata information about the 
	 *given rowset.
	 */
	private  void init()
	{
		try{
			// EXECUTE THE QUERY
			rowset.execute();
			
			// SPECIFY THE ROWSET TO THE TABLE MODEL.
			if(tableModel == null)
				tableModel = new SSTableModel(rowset);
			else
				tableModel.setRowSet(rowset);
			
			// GET THE ROW COUNT 	
			rowCount = tableModel.getRowCount();
			// GET THE COLUMN COUNT
			columnCount = tableModel.getColumnCount();
/*			btnSelectRow = new JButton[rowCount];
			Dimension dimension = new Dimension(15,15);
			for(int i=0;i<rowCount;i++)
			{
				
				btnSelectRow[i] = new JButton();
				if(i == rowCount -1)
					btnSelectRow[i].setText("*");
				btnSelectRow[i].setPreferredSize(dimension);
			}
			
*/		}catch(SQLException se){
			se.printStackTrace();
		}
		// SET THE TABLE MODEL FOR JTABLE
		this.setModel(tableModel);
		// SPECIFY THE MESSAGE WINDOW TO WHICH THE TABLE MODEL HAS TO POP UP
		// ERROR MESSAGES.
		tableModel.setMessageWindow(window);
		
		tableModel.setJTable(this);
		
			
		// SET THE MINIMUM WIDTH OF COLUMNS
		TableColumnModel columnModel = this.getColumnModel();
		TableColumn column;
		for(int i=columnModel.getColumnCount()-1;i>=0;i--){
			column = columnModel.getColumn(i);
			int j = -1;
			// SET THE WIDTH OF HIDDEN COLUMNS AS 0
			if(hiddenColumns != null){
				for(j=0; j<hiddenColumns.length;j++){
					if(hiddenColumns[j] == i){
						
						//columnModel.removeColumn(column);
						column.setMaxWidth(0);
						column.setMinWidth(0);
						column.setPreferredWidth(0);
						break;
					}
				}
				if(j == hiddenColumns.length)
					column.setMinWidth(100);
			}
			// SET OTHER COLUMNS MIN WIDTH TO 100
			else{
				column.setMinWidth(100);
			}
		}
		
		// ADD KEY LISTENER TO JTABLE.
		// THIS IS USED FOR DELETING THE ROWS
		// ALLOWS MULTIPLE ROW DELETION. 
		// KEY SEQUENCE FOR DELETING ROWS IS CTRL-X.
		this.addKeyListener(new KeyAdapter(){
			private boolean controlPressed = false;
			
			// IF THE KEY PRESSED IS CONTROL STORE THAT INFO.
			public void keyPressed(KeyEvent ke){
				if(ke.getKeyCode() == KeyEvent.VK_CONTROL)
					controlPressed = true;
			}
			
			
			public void keyReleased(KeyEvent ke){
				// IF CONTROL KEY IS RELEASED SET THAT CONTROL IS NOT PRESSED.
				if(ke.getKeyCode() == KeyEvent.VK_CONTROL)
					controlPressed = false;
				// IF X IS PRESSED WHILE THE CONTROL KEY IS STILL PRESSED
				// DELETE THE SELECTED ROWS.	
				if(ke.getKeyCode() == KeyEvent.VK_X){
					if(controlPressed != true)
						return;
					// GET THE NUMBER OF ROWS SELECTED 
					int numRows = getSelectedRowCount();
//					System.out.println("Num Rows Selected : " + numRows);
					if (numRows == 0)
						return;
					// GET LIST OF ROWS SELECTED	
					int[] rows = getSelectedRows();
					// IF USER HAS PROVIDED A PARENT COMPONENT FOR ERROR MESSAGES
					// CONFIRM THE DELETION
					if( window != null){
						int returnValue = JOptionPane.showConfirmDialog(window,"You are about to delete " + rows.length + " rows. " +
							"\nAre you sure you want to delete the rows?");
						if( returnValue != JOptionPane.YES_OPTION)	
							return;
					}
					// START DELETING THE ROWS IN BOTTON UP FASHION
					// IN DOING SO YOU RETAIN THE ROW NUMBERS THAT HAVE TO BE DELETED
					// IF YOU DO IT TOP DOWN THE ROW NUMBERING CHANGES AS SOON AS A 
					// ROW IS DELETED AS A RESULT LOT OF CARE HAS TO BE TAKEN
					// TO IDENTIFY THE NEW ROW NUMBERS AND THEN DELETE THE ROWS
					// INSTEAD OF THAT ITS MUCH EASIER IF YOU DO IT BOTTOM UP.
					for(int i=rows.length -1;i>=0;i--){
//						System.out.println("Selected Rows " + rows[i]);
						tableModel.deleteRow(rows[i]);
					}
					//setModel(tableModel);
					updateUI();
				}
			}
		});
						
		
/*		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx =0;
		for(int i=0;i<rowCount;i++)
		{
			constraints.gridy =i;
			this.add(btnSelectRow[i],constraints);
		}
		//constraints.weightx = 1;
		//constraints.weighty = 1;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridheight = rowCount;
		constraints.gridwidth = columnCount;
		this.add(grid,constraints);
*/		
		// THIS CAUSES THE JTABLE TO DISPLAY THE HORIZONTAL SCROLL BAR AS NEEDED.	
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// ADD THE JTABLE TO A SCROLL BAR
		scrollPane = new JScrollPane(this,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		
	}

		
	/**
	 * Binds the rowset to the grid.
	 *Data is taken from the new rowset.
	 *@param _rowset  the rowset which acts as the data source.
	 */
	 public void setRowSet(RowSet _rowset){
	 	// VARIABLE TO DETERMINE IF UI HAS TO BE UPDATED
	 	boolean updateUI = false;
	 	// IF A ROW SET ALREADY EXISTS THEN UI HAS TO BE UPDATED
	 	// ELSE YOU WILL NOT SEE CHANGES IN THE JTABLE
	 	if(rowset != null)
	 		updateUI = true;
	 		
	 	if(rowset == null){
	 		rowset = _rowset;
	 		init();
	 	}
	 	else{
	 		rowset = _rowset;
	 		try{
	 			tableModel.setRowSet(rowset);	
	 		}catch(SQLException se){
	 			se.printStackTrace();
	 		}
	 	}
	 	// UPDATE UI IF NEEDED
	 	if(updateUI)
	 		updateUI();
	 }
	 
		 
	 /**
	  *	 Sets the preferred size of the scroll pane in which the JTable is embedded.
	  *@param _dimension required dimension for JTable
	  */
	 public void setPreferredSize(Dimension _dimension){
	 	scrollPane.setPreferredSize(_dimension);
	 }
	 
	 /**
	  *	 Returns scroll pane with the JTable embedded in it.
	  */
	 public JComponent getComponent(){
	 	return scrollPane;
	 }
	 
	 /**
	  *	Sets the default values for different columns.
	  *When a new row is added these default values will be added to the columns.
	  *Please make sure that the object specified for each column is of the same type
	  *as that of the column in the database.
	  *Use the getColumnClass function in JTable to determine the exact data type.
	  *@param _columnNumbers array containing the column numbers for which the defaults
	  *apply.
	  *@param _values the values for the column numbers specified in _columnNumbers.
	  *
	  */	
	 public void setDefaultValues(int[] _columnNumbers, Object[] _values){
	 	if(tableModel == null)
	 		tableModel = new SSTableModel();
	 	tableModel.setDefaultValues(_columnNumbers,_values);
	 }
	 
	 /**
	  *	Sets the default values for different columns.
	  *When a new row is added these default values will be added to the columns.
	  *Please make sure that the object specified for each column is of the same type
	  *as that of the column in the database.
	  *Use the getColumnClass function in JTable to determine the exact data type.
	  *@param _columnNames array containing the column names for which the defaults
	  *apply.
	  *@param _values the values for the column names specified in _columnNames.
	  *@throws SQLException is the specified column name is not present in the rowset
	  *
	  */	
	 public void setDefaultValues(String[] _columnNames, Object[] _values) throws SQLException{
	 	
	 	int[] columnNumbers = null;
	 	
	 	if(tableModel == null)
	 		tableModel = new SSTableModel();
	 		
	 	if( _columnNames != null)
	 	{
	 		columnNumbers = new int[_columnNames.length];
	 	
	 	 	for(int i=0; i< _columnNames.length;i++)
	 			columnNumbers[i] = rowset.findColumn(_columnNames[i]) -1 ;
	 	}
	 	
	 	tableModel.setDefaultValues(columnNumbers, _values);
	 }	
	 
	 /**
	  *	Returns the default value being used for the specified column.
	  *Returns null if a default is not in use.
	  *@param _columnNumber the column number for which default value is to be returned.
	  *@return returns an object containing the default value for the requested column.
	  */
	 public Object getDefaultValue(int _columnNumber){
	 	return tableModel.getDefaultValue(_columnNumber);
	 }
	 
	 /**
	  *	Returns the default value being used for the specified column.
	  *Returns null if a default is not in use.
	  *@param _columnName the column name for which default value is to be returned.
	  *@return returns an object containing the default value for the requested column.
	  *@throws SQLException is the specified column name is not present in the rowset
	  */
	 public Object getDefaultValue(String _columnName) throws SQLException{
	 	int columnNumber = rowset.findColumn(_columnName);
	 	return tableModel.getDefaultValue(columnNumber -1);
	 }
	 
	 /**
	  *	Notification from the UIManager that the L&F has changed. Replaces the current 
	  *UI object with the latest version from the UIManager. 
	  *This also causes the JTable to request new values for each cell.
	  *Thus refreshes the screen.
	  */
	 public void updateUI(){
	 	super.updateUI();
	 }
		
	/**
	 *	Sets the column number which is the primary column for the table.
	 *This is required if new rows have to be added to the JTable.
	 *For this to properly work the SSDataValue object should also be provided
	 *SSDataValue is used to get the value for the primary column.
	 *@param _columnNumber the column which is the primary column.
	 */	
	public void setPrimaryColumn(int _columnNumber){
    	tableModel.setPrimaryColumn(_columnNumber);
    }
    
    /**
	 *	Sets the column number which is the primary column for the table.
	 *This is required if new rows have to be added to the JTable.
	 *For this to properly work the SSDataValue object should also be provided
	 *SSDataValue is used to get the value for the primary column.
	 *@param _columnName the column which is the primary column.
	 */	
	public void setPrimaryColumn(String _columnName) throws SQLException{
		int columnNumber = rowset.findColumn(_columnName) -1;
    	tableModel.setPrimaryColumn(columnNumber);
    }
    
    /**
     *	Sets the SSDataValue interface implemention. This interface specifies
     *function to retrieve primary column values for a new row to be added.
     *@param _dataValue implementation of 
     */
    public void setSSDataValue(SSDataValue _dataValue){
    	tableModel.setSSDataValue(_dataValue);
    }	
    
    /**
     *	Sets a date renderer for the specified column.
     *The date will be displayed in mm/dd/yyyy format. If a date renderer
     *is not requested then the date will be displayed in a standard format(yyyy-mm-dd).
     *@param _column column number for which a date renderer is needed.
     */    
    public void setDateRenderer(int _column){
    	TableColumnModel columnModel = getColumnModel();
    	TableColumn tableColumn = columnModel.getColumn(_column);
    	tableColumn.setCellRenderer(new DateRenderer());
    	tableColumn.setCellEditor(new DateEditor());
    }
    
    /**
     *	Sets a date renderer for the specified column.
     *The date will be displayed in mm/dd/yyyy format. If a date renderer
     *is not requested then the date will be displayed in a standard format(yyyy-mm-dd).
     *@param _column column name for which a date renderer is needed.
     */    
    public void setDateRenderer(String _column) throws SQLException{
    	int column = rowset.findColumn(_column) -1;
    	TableColumnModel columnModel = getColumnModel();
    	TableColumn tableColumn = columnModel.getColumn(column);
    	tableColumn.setCellRenderer(new DateRenderer());
    	tableColumn.setCellEditor(new DateEditor());
    }

    
    /**
     *	Sets a combo box renderer for the specified column.
     *This is use full to limit the values that go with a column or if an underlying code
     *is do be displayed in a more meaningfull manner.
     *@param _column column number for which combo renderer is to be provided.
     *@param _displayItems the actual Objects to be displayed in the combo box.
     *@param _underlyingValues the values that have to be written to the database when an
     *item in the combo box is selected.
     */
    public void setComboRenderer(int _column, Object[] _displayItems, Object[] _underlyingValues){
    	setRowHeight(20);
    	TableColumnModel columnModel = getColumnModel();
    	TableColumn tableColumn = columnModel.getColumn(_column);
    	tableColumn.setCellRenderer(new ComboRenderer(_displayItems, _underlyingValues));
    	tableColumn.setCellEditor(new ComboEditor(_displayItems, _underlyingValues));
    	tableColumn.setMinWidth(250);
    }
    
    /**
     *	Sets a combo box renderer for the specified column.
     *This is use full to limit the values that go with a column or if an underlying code
     *is do be displayed in a more meaningfull manner.
     *@param _column column name for which combo renderer is to be provided.
     *@param _displayItems the actual Objects to be displayed in the combo box.
     *@param _underlyingValues the values that have to be written to the database when an
     *item in the combo box is selected.
     */
    public void setComboRenderer(String _column, Object[] _displayItems, Object[] _underlyingValues) throws SQLException{
    	int column = rowset.findColumn(_column)-1;
    	setRowHeight(20);
    	TableColumnModel columnModel = getColumnModel();
    	TableColumn tableColumn = columnModel.getColumn(column);
    	tableColumn.setCellRenderer(new ComboRenderer(_displayItems, _underlyingValues));
    	tableColumn.setCellEditor(new ComboEditor(_displayItems, _underlyingValues));
    	tableColumn.setMinWidth(250);
    }
    
    /**
     *	Sets the header for the JTable.
     *This function has to be called before setting the rowset for SSDataGrid.
     *@param _headers array of string objects representing the header of each column.
     */
    public void setHeaders(String[] _headers){
    	tableModel.setHeaders(_headers);
    }
    
    /**
     *	Sets the uneditable columns.
     *The columns specified as uneditable will not be available for user to edit.
     *This overrides the isCellEditable function in SSCellEditing.
     *@param _columnNumbers  array specifying the column numbers which should be 
     *uneditable.
     */
    public void setUneditableColumns(int[] _columnNumbers){
    	tableModel.setUneditableColumns(_columnNumbers);
    }
    
    /**
     *	Sets the uneditable columns.
     *The columns specified as uneditable will not be available for user to edit.
     *This overrides the isCellEditable function in SSCellEditing.
     *@param _columnNames  array specifying the column names which should be 
     *uneditable.
     */
    public void setUneditableColumns(String[] _columnNames) throws SQLException{
    	int[] columnNumbers = null;
    	if(_columnNames != null)
    	{
    		columnNumbers = new int[_columnNames.length];
    	
    		for(int i=0;i<_columnNames.length;i++)
    			columnNumbers[i] = rowset.findColumn(_columnNames[i]) -1;
    	}
    		
    	tableModel.setUneditableColumns(columnNumbers);
    }
    
    /**
     *	Sets the column numbers that should be hidden.
     *The SSDataGrid sets the column width of these columns to 0.
     *The columns are set to zero width rather than removing the column from the table.
     *Thus preserving the column numbering.If a column is removed then the column numbers
     *for columns after the removed column will change.
     *Even if the column is specified as hidden user will be seeing a tiny strip.
     *Make sure that you specify the hidden column numbers in the uneditable column
     *list.
     *@param _columnNumbers  array specifying the column numbers which should be 
     *hidden
     */
    public void setHiddenColumns(int[] _columnNumbers){
    	hiddenColumns = _columnNumbers;
    	tableModel.setHiddenColumns(_columnNumbers);
    }
    
    /**
     *	Sets the column numbers that should be hidden.
     *The SSDataGrid sets the column width of these columns to 0.
     *The columns are set to zero width rather than removing the column from the table.
     *Thus preserving the column numbering.If a column is removed then the column numbers
     *for columns after the removed column will change.
     *Even if the column is specified as hidden user will be seeing a tiny strip.
     *Make sure that you specify the hidden column numbers in the uneditable column
     *list.
     *@param _columnNames  array specifying the column names which should be 
     *hidden
     */
    public void setHiddenColumns(String[] _columnNames) throws SQLException{
    	
    	hiddenColumns = null;
    	if(_columnNames != null)
    	{
    		hiddenColumns = new int[_columnNames.length];
//    		System.out.println("Hidden Columns");
    		for(int i=0; i<_columnNames.length; i++)
    		{
    			hiddenColumns[i] = rowset.findColumn(_columnNames[i]) -1;
//    			System.out.println(hiddenColumns[i]);
    		}
    		
    		// SET THE MINIMUM WIDTH OF COLUMNS
			TableColumnModel columnModel = this.getColumnModel();
			TableColumn column;
		
			// SET THE WIDTH OF HIDDEN COLUMNS AS 0
			for(int j=0; j<hiddenColumns.length;j++){
				column = columnModel.getColumn(hiddenColumns[j]);								
				column.setMinWidth(0);
				column.setMaxWidth(0);
				column.setPreferredWidth(0);
//				System.out.println("Set column " + hiddenColumns[j] + " width to zero");
			}
			updateUI();
		}	
    	
    	tableModel.setHiddenColumns(hiddenColumns);
    }
    
    /**
	 *	If the user has to decide on which cell has to be editable and which is not
	 *then SSCellEditable interface has to be implemented and set it for the SSTableModel.
	 *@param _cellEditable implementation of SSCellEditable interface.
	 *
	 */
	 public void setSSCellEditing(SSCellEditing _cellEditing){
	 	tableModel.setSSCellEditing( _cellEditing );
	 }
	 
    
    // EDITOR TO EDIT THE DATE FIELD.
    // USES THE TEXT FIELD AS THE EDITOR BUT CHANGES THE FORMAT 
    // TO MM/DD/YYYY FORMAT FROM YYYY-MM-DD FORMAT.
    private class DateEditor extends DefaultCellEditor {
    	
    	// CONSTRUCTOR FOR THE EDITOR CLASS
   		public DateEditor(){
   			super(new JTextField());
   			super.setClickCountToStart(2);
   			KeyListener[] keyListeners = getComponent().getKeyListeners();
   			for(int i=0;i<keyListeners.length;i++)
   				getComponent().removeKeyListener(keyListeners[i]);
   				
   			getComponent().addKeyListener(new KeyListener(){
   				public void keyReleased(KeyEvent ke){}
   				public void keyTyped(KeyEvent ke){}
   				public synchronized void keyPressed (KeyEvent ke){
   					if(ke.getKeyCode() == KeyEvent.VK_DELETE      ||
   						ke.getKeyCode() == KeyEvent.VK_BACK_SPACE ||
   						ke.getKeyCode() == KeyEvent.VK_LEFT       ||
	 					ke.getKeyCode() == KeyEvent.VK_RIGHT      ||
	 					ke.getKeyCode() == KeyEvent.VK_HOME       ||
	 					ke.getKeyCode() == KeyEvent.VK_END	      ||
	 					ke.getKeyCode() == KeyEvent.VK_ENTER	)
   						return;
   					String str = ((JTextField)(DateEditor.this.getComponent())).getText();
//   					System.out.println(str);
   					String newStr = dateMask(str,ke);
//   					System.out.println(newStr);
   					((JTextField)(DateEditor.this.getComponent())).setText(newStr);
   				}
   			});
   				
   			   			
   		}
   		// RETURNS THE TEXTFIELD WITH THE GIVEN DATE IN THE TEXTFIELD
   		// (AFTER THE FORMAT IS CHANGED TO MM/DD/YYYY
   		public synchronized Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

   			if(value instanceof Date){
   				Date date = (Date)value;
    			GregorianCalendar calendar = new GregorianCalendar();
    			calendar.setTime(date);
    			String strDate = "" + (calendar.get(Calendar.MONTH) + 1) + "/" +
    				 calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
    				return super.getTableCellEditorComponent(table, strDate, isSelected, row, column);
   			}
   			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
   			
   		}
   		
   		  		
   		public boolean isCellEditable(EventObject event){
   			// IF NUMBER OF CLICKS IS LESS THAN THE CLICKCOUNTTOSTART RETURN FALSE
   			// FOR CELL EDITING.
   			if(event instanceof MouseEvent){
   				return ((MouseEvent)event).getClickCount() >= getClickCountToStart();
   			}
    		return true;
    	}
    }
    
    /**
     *	Date renderer class for renderer date columns.
     *Displays the date in mm/dd/yyyy format.
     */ 
    private class DateRenderer extends DefaultTableCellRenderer{
    	
    	public  void setValue(Object value){
    		if(value instanceof java.sql.Date){
    			Date date = (Date)value;
    			GregorianCalendar calendar = new GregorianCalendar();
    			calendar.setTime(date);
    			String strDate = "" + (calendar.get(Calendar.MONTH)+1) + "/" + 
    				calendar.get(Calendar.DAY_OF_MONTH)  + "/" + calendar.get(Calendar.YEAR);
    			setHorizontalAlignment(SwingConstants.CENTER);	
    			setText(strDate);
//    			System.out.println(" Indeed date instance");
    		}
    		else{
//    			System.out.println("Not a date instance");
    			super.setValue(value);
    		}
    	}
    }
    
    /**
     *	 ComboRenderer renders the values of the cell in a combo box.
     */
    private class ComboRenderer extends JComboBox implements TableCellRenderer {
    	
    	Object[] underlyingValues = null;
    	
    	public ComboRenderer(Object[] _items, Object[] _underlyingValues) {
    		super(_items);
    		underlyingValues = _underlyingValues;
    	}
    	
    	public Component getTableCellRendererComponent(JTable _table, Object _value, 
    		boolean _selected, boolean _hasFocus, int _row, int _column){
    		if(getItemCount() > 0 )
    			setSelectedIndex(getIndexOf(_value));
    		else
    			System.out.println("Combo Renderer: No item in combo that corresponds to " + _value );	
    		return this;		
    	}
    	
    	private int getIndexOf(Object _value){
    		if(_value == null)
    			return 0;
    		if(underlyingValues == null)
    			return ((Integer)_value).intValue();
    		for(int i=0;i<underlyingValues.length;i++){
    			if(underlyingValues[i].equals(_value))
    				return i;
    		}
    		return 0;
    	}
    }
    
    /**
     *	Combo Box Editor for columns having combo renderers.
     */
	private class ComboEditor extends DefaultCellEditor{    	
    	Object[] underlyingValues = null;
    	// SET THE CLICK COUNT TO EDIT THE COMBO AS 2
    	int clickCountToStart = 2;
//    	JComboBox comboBox = null;
    	    	
    	public ComboEditor(Object[] _items, Object[] _underlyingValues) {
    		super(new JComboBox(_items));
    		underlyingValues = _underlyingValues;	
      	}
    	
    	public boolean isCellEditable(EventObject event){
    		if(event instanceof MouseEvent){
    			return ((MouseEvent)event).getClickCount() >= clickCountToStart;
    		}
    		return true;
    	}
    	
    	public Component getTableCellEditorComponent(JTable _table, Object _value, 
    	    					boolean _selected, int _row, int _column){
    		JComboBox comboBox = (JComboBox)getComponent();
    		comboBox.setSelectedIndex(getIndexOf(_value));
    		return comboBox;
    	}
    	
    	public Object getCellEditorValue(){
    		if( underlyingValues == null)
    			return new Integer( ((JComboBox)getComponent()).getSelectedIndex()); 
    			
    		int index = ((JComboBox)getComponent()).getSelectedIndex(); 	
//    		System.out.println("Index is "+ index);
    		if (index == -1)
    				return underlyingValues[0];
    				
    		return underlyingValues[index];
    	}
  	
    	private int getIndexOf(Object _value){
    		if(underlyingValues == null)
    			return ((Integer)_value).intValue();
    		for(int i=0;i<underlyingValues.length;i++){
    			if(underlyingValues[i].equals(_value))
    				return i;
    		}
    		return 0;
    	}
    }	
    
    
/*    private class DBComboRenderer extends SSDBComboBox implements TableCellRenderer {
    	
      	public Component getTableCellRendererComponent(JTable _table, Object _value, 
    		boolean _selected, boolean _hasFocus, int _row, int _column){
    		super.getTextField().setText(_value);
    		return this;		
    	}
    	
   	}    
   	
   	private class DBComboEditor extends DefaultCellEditor{    	
    	
    	int clickCountToStart = 2;
    	    	
    	public DBComboEditor(Connection _conn, String _query, String _columnName, String _displayColumnName) {
    		super(new SSDBComboBox());
    		underlyingValues = _underlyingValues;	
      	}
    	
    	public boolean isCellEditable(EventObject event){
    		if(event instanceof MouseEvent){
    			return ((MouseEvent)event).getClickCount() >= clickCountToStart;
    		}
    		return true;
    	}
    	
    	public Component getTableCellEditorComponent(JTable _table, Object _value, 
    	    					boolean _selected, int _row, int _column){
    		JComboBox comboBox = (JComboBox)getComponent();
    		comboBox.setSelectedIndex(getIndexOf(_value));
    		return comboBox;
    	}
    	
    	public Object getCellEditorValue(){
    		if( underlyingValues == null)
    			return new Integer( ((JComboBox)getComponent()).getSelectedIndex()); 
    			
    		int index = ((JComboBox)getComponent()).getSelectedIndex(); 	
//    		System.out.println("Index is "+ index);
    		if (index == -1)
    				return underlyingValues[0];
    				
    		return underlyingValues[index];
    	}
  	
    	private int getIndexOf(Object _value){
    		if(underlyingValues == null)
    			return ((Integer)_value).intValue();
    		for(int i=0;i<underlyingValues.length;i++){
    			if(underlyingValues[i].equals(_value))
    				return i;
    		}
    		return 0;
    	}
    }	
*/
     // HANDLES THE DATE MASK.
	 // SETTING THE SLASHES FOR THE USER.
	 private String dateMask(String str, KeyEvent ke){
//	 	System.out.println("Length: " + str.length());
//	 	System.out.println("Key Char :" + ke.getKeyChar());
	 	
	 	switch(str.length()){
	 		case 1:
	 			if(ke.getKeyChar() == '/'){
	 				str =  "0" + str ;
	 			}
	 			break;
			case 2:
				if( ke.getKeyChar() == '/' ){
					
										
				}
				else{
					str = str +  "/";
					
				}
				break;
			case 4:
				if( ke.getKeyChar() == '/' ){
					String newStr = str.substring(0,3);
					newStr = newStr + "0" + str.substring(3,4);
					str = newStr;
					
				}
				break;
			
			case 5:
				if( ke.getKeyChar() != '/' ){
					str = str + "/";
					
				}
				break;
			
		}
		return str;	
	}    		 
}
	



/*
 * $Log$
 * Revision 1.4  2003/12/18 20:12:01  prasanth
 * Update class description.
 *
 * Revision 1.3  2003/12/16 18:01:40  prasanth
 * Documented versions for release 0.6.0
 *
 */