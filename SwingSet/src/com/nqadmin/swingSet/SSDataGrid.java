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
	
	int[] hiddenColumns = null;

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
	
	public SSDataGrid(){
		super();
		tableModel = new SSTableModel();
//super(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
	}
	
	public void setMessageWindow(Component _window){
		window = _window;
	}
	
	/**
	 *	Initializes the data grid control. Collects metadata information about the 
	 *given rowset.
	 */
	private  void init()
	{
		try{
			rowset.execute();
			
			if(tableModel == null)
				tableModel = new SSTableModel(rowset);
			else
				tableModel.setRowSet(rowset);
				
			rowCount = tableModel.getRowCount();
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
		
		this.setModel(tableModel);
		tableModel.setGrid(this);
		
		setHeader();
		
		// SET THE MINIMUM WIDTH OF COLUMNS TO 20
		TableColumnModel columnModel = this.getColumnModel();
		TableColumn column;
		for(int i=0; i<columnModel.getColumnCount();i++){
			column = columnModel.getColumn(i);
			int j = -1;
			if(hiddenColumns != null){
				for(j=0; j<hiddenColumns.length;j++){
					if(hiddenColumns[j] == i +1){
						column.setMaxWidth(0);
						break;
					}
				}
				if(j == hiddenColumns.length)
					column.setMinWidth(100);
			}
			else{
				column.setMinWidth(100);
			}
		}
		
		this.addKeyListener(new KeyAdapter(){
			private boolean controlPressed = false;
			
			public void keyPressed(KeyEvent ke){
				if(ke.getKeyCode() == KeyEvent.VK_CONTROL)
					controlPressed = true;
			}
			
			public void keyReleased(KeyEvent ke){
				if(ke.getKeyCode() == KeyEvent.VK_CONTROL)
					controlPressed = false;
					
				if(ke.getKeyCode() == KeyEvent.VK_X){
					if(controlPressed != true)
						return;
					
					int numRows = getSelectedRowCount();
//					System.out.println("Num Rows Selected : " + numRows);
					if (numRows == 0)
						return;
					int[] rows = getSelectedRows();
					if( window != null){
						int returnValue = JOptionPane.showConfirmDialog(window,"You are about to delete " + rows.length + " rows. " +
							"\nAre you sure you want to delete the rows?");
						if( returnValue != JOptionPane.YES_OPTION)	
							return;
					}
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
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane = new JScrollPane(this,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );

		
	}
	
	private void setHeader(){
		
	}
	
	/**
	 * Binds the rowset to the grid.
	 *Data is taken from the new rowset.
	 *@param _rowset  the rowset which acts as the data source.
	 */
	 public void setRowSet(RowSet _rowset){
	 	boolean updateUI = false;
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
	 			setHeader();
	 		}catch(SQLException se){
	 			se.printStackTrace();
	 		}
	 	}
	 	
	 	if(updateUI)
	 		updateUI();
	 }
	 
	 /**
	  *	return the value of the cell corresponding to _row and _column.
	  *The rows and columns are numbers as 1, 2, so on.
	  */
	 public Object getValue(int _row, int _column){
	 	return tableModel.getValueAt(_row-1, _column-1);
	 }
	 
	 public void setPreferredSize(Dimension _dimension){
	 	System.out.println("Setting Dimension of data grid.");
	 	scrollPane.setPreferredSize(_dimension);
	 }
	 
	 public JComponent getComponent(){
	 	return scrollPane;
	 }
	 	
	 public void setDefaultValues(int[] _columnNumbers, Object[] _values){
	 	if(tableModel == null)
	 		tableModel = new SSTableModel();
	 	tableModel.setDefaultValues(_columnNumbers,_values);
	 }	
	 
	 public Object getDefaultValue(int _columnNumber){
	 	return tableModel.getDefaultValue(_columnNumber);
	 }
	 
	 public void updateUI(){
	 	super.updateUI();
	 }
		
	public void setPrimaryColumn(int _columnNumber){
    	tableModel.setPrimaryColumn(_columnNumber);
    }
    
    public void setSSDataValue(SSDataValue _dataValue){
    	tableModel.setSSDataValue(_dataValue);
    }	
    
        
    public void setDateRenderer(int _column){
    	TableColumnModel columnModel = getColumnModel();
    	TableColumn tableColumn = columnModel.getColumn(_column -1);
    	tableColumn.setCellRenderer(new DateRenderer());
    	tableColumn.setCellEditor(new DateEditor());
    }
    
    public void setComboRenderer(int _column, Object[] _displayItems, Object[] _underlyingValues){
    	setRowHeight(20);
    	TableColumnModel columnModel = getColumnModel();
    	TableColumn tableColumn = columnModel.getColumn(_column -1);
    	tableColumn.setCellRenderer(new ComboRenderer(_displayItems, _underlyingValues));
    	tableColumn.setCellEditor(new ComboEditor(_displayItems, _underlyingValues));
    	tableColumn.setMinWidth(250);
    }
    
    public void setHeaders(String[] _headers){
    	tableModel.setHeaders(_headers);
    }
    
    public void setUneditableColumns(int[] _columnNumbers){
    	tableModel.setUneditableColumns(_columnNumbers);
    }
    
    public void setHiddenColumns(int[] _columnNumbers){
    	hiddenColumns = _columnNumbers;
    	tableModel.setHiddenColumns(_columnNumbers);
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
   			getComponent().addKeyListener(new KeyListener(){
   				public void keyPressed(KeyEvent ke){}
   				public void keyTyped(KeyEvent ke){}
   				public void keyReleased(KeyEvent ke){
   					if(ke.getKeyCode() == KeyEvent.VK_DELETE      ||
   						ke.getKeyCode() == KeyEvent.VK_BACK_SPACE ||
   						ke.getKeyCode() == KeyEvent.VK_LEFT       ||
	 					ke.getKeyCode() == KeyEvent.VK_RIGHT      
	 				  )
   						return;
   					String str = ((JTextField)(DateEditor.this.getComponent())).getText();
   					((JTextField)(DateEditor.this.getComponent())).setText(dateMask(str,ke));
   				}
   			});
   				
   			   			
   		}
   		// RETURNS THE TEXTFIELD WITH THE GIVEN DATE IN THE TEXTFIELD
   		// (AFTER THE FORMAT IS CHANGED TO MM/DD/YYYY
   		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

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
    
    private class DateRenderer extends DefaultTableCellRenderer{
    	
    	public void setValue(Object value){
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
    
    
	private class ComboEditor extends DefaultCellEditor{    	
    	Object[] underlyingValues = null;
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
*/     // HANDLES THE DATE MASK.
	 // SETTING THE SLASHES FOR THE USER.
	 private String dateMask(String str, KeyEvent ke){
	 	switch(str.length()){
			case 2:
				if( ke.getKeyChar() == '/' ){
					
					str =  "0" + str ;
					
				}
				else{
					str = str + "/";
					
				}
				break;
			case 5:
				if( ke.getKeyChar() == '/' ){
					String newStr = str.substring(0,3);
					newStr = newStr + "0" + str.substring(3,4) + "/";
					str = newStr;
					
				}
				else{
					str = str + "/";
					
				}
				break;
			case 3:
			case 6:
				if( ke.getKeyChar() != '/' ){
					str = str + "/";
					
				}
				break;
			case 4:
			case 7:
				if( ke.getKeyChar() == '/' ){
					str = str.substring(0,str.length()-1);
					
				}
				break;
		}
		return str;	
	}    		 
}
	
