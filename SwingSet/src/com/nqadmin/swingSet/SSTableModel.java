package com.nqadmin.swingSet;

import javax.sql.*;
import javax.swing.table.*;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.StringTokenizer;

public class SSTableModel  extends AbstractTableModel {
	
	RowSet rowset = null;
	ResultSetMetaData metaData = null;
	int rowCount 			= 0;
	int columnCount			= 0;
//	int[] columnsWithDefaults = null;
//	Object[] defaultValues	  = null;
	HashMap defaultValuesMap = null;
	private boolean inInsertRow = false;
	SSDataGrid grid = null;
	
	String[] headers = null;
	
	int primaryColumn = -1;
	Object primaryValue = null;
	SSDataValue		dataValue	= null;
	SSCellEditing  cellEditing = null;
	
	
	int[] uneditableColumns  = null;
	int[] hiddenColumns		 = null;
	/**
	 * Constructs a SSTableModel object.
	 *If this contructor is used the setRowSet method has to be used to set the rowset
	 *before constructing the JTable.
	 */	
	public SSTableModel(){
		super();
	}
	
	/**
	 * Constructs a SSTableModel object with the given rowset.
	 *This will call the execute method on the given rowset.
	 *@param _rowset rowset object whose records has to be displayed in JTable.
	 */
	public SSTableModel(RowSet _rowset) throws SQLException{
		super();
		rowset = _rowset;
		init();
	}
	
	/**
	 *	Sets the rowset for SSTableModel to the given rowset.
	 *This rowset will be used to get the data for JTable.
	 *@param _rowset rowset object whose records has to be displayed in JTable.
	 */
	public void setRowSet(RowSet _rowset) throws SQLException{
		rowset = _rowset;
		init();
	}	
	
	/**
	 *	If the user has to decide on which cell has to be editable and which is not
	 *then SSCellEditing interface has to be implemented and set it for the SSTableModel.
	 *@param _cellEditing implementation of SSCellEditing interface.
	 *
	 */
	 public void setSSCellEditing(SSCellEditing _cellEditing){
	 	cellEditing = _cellEditing;
	 }
	
	/**
	 *	Initializes the SSTableModel. (Gets  the column count and rowcount for the
	 *given rowset.)
	 */
	private void init(){
		try{
			metaData = rowset.getMetaData();
			columnCount = metaData.getColumnCount();
			rowset.last();
			// ROWS IN THE ROWSET ARE NUMBERED FROM 1, SO LAST ROW NUMBER GIVES THE 
			// ROW COUNT
			rowCount = rowset.getRow();
			rowset.first();
		
		}catch(SQLException se){
			se.printStackTrace();
		}
	}
	
		
	/**
	 *Returns the number of columns in the model. A JTable uses this method to
	 *determine how many columns it should create and display by default. 	
	 *@return  the number of columns in the SSTableModel
	 */
	public int getColumnCount(){
		return columnCount ;
	}
	
	/**
	 *Returns the number of rows in the model. A JTable uses this method to determine
	 *how many rows it should display. 
	 *@return the number of rows in the SSTableModel
	 */
	public int getRowCount(){
		return rowCount +1;
	}
	
	/**Returns true if the cell at rowIndex and columnIndex is editable. Otherwise,
	 *setValueAt on the cell will not change the value of that cell. 
	 *@param _row    - the row whose value to be queried
	 *@param _column - the column whose value to be queried 
	 */
	public boolean isCellEditable(int _row, int _column){
		
//		if(rowset.isReadOnly()){
//			System.out.println("Is Cell Editable : false");
//			return false;
//		}
//		System.out.println("Is Cell Editable : true");
		if( uneditableColumns != null){
			for(int i=0; i<uneditableColumns.length;i++){
				if( _column + 1 == uneditableColumns[i])
					return false;
			}
		}
		if(cellEditing != null)
			return cellEditing.isCellEditable(_row+1, _column+1);
		return true;
	}
	
	/**
	 *Returns the value for the cell at  _row and _column.
	 *@param _row    - the row whose value to be queried.
	 *@param _column - the column whose value to be queried.
	 *@return value at the requested cell.
	 */
	public Object getValueAt(int _row, int _column){
		
		Object value = null;
		
		if(_row == rowCount){
	        value = getDefaultValue(_column +1);
	        return value;
		}
		
		try{
			// ROW NUMBERS IN ROWSET START FROM 1 WHERE AS ROW NUMBERING FOR JTABLE START FROM 0
			rowset.absolute(_row + 1);
			// COLUMN NUMBERS IN ROWSET START FROM 1 WHERE AS COLUMN NUMBERING FOR JTABLE START FROM 0
			int type = metaData.getColumnType(_column + 1);
			switch(type){
				case Types.INTEGER:
	        	case Types.SMALLINT:
	        	case Types.TINYINT:
	       			value = new Integer(rowset.getInt(_column+1));
	        		break;
	        	case Types.BIGINT:
	       			value = new Long(rowset.getLong(_column+1));
	        		break;	
	        	case Types.DOUBLE:
	        	case Types.FLOAT:	
	       			value = new Double(rowset.getDouble(_column+1));
	        		break;
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
	     }catch(SQLException se){
	     	se.printStackTrace();
	     	if(grid != null)
	     		JOptionPane.showMessageDialog(grid,"Error while retrieving value.\n" + se.getMessage());
	    
	     }
	     return value;
    }
    
    /**
     *Sets the value in the cell at _row and _column to _value. 
     *@param _value  - the new value
	 *@param _row    - the row whose value is to be changed
     *@param _column - the column whose value is to be changed
     */
    public void setValueAt(Object _value, int _row, int _column){
    	
    	// IF CELL EDITING INTERFACE IMPLEMENTATION IS PROVIDED INFO THE USER
    	// THAT AN UPDATE FOR CELL HAS BEEN REQUESTED.
    	if(cellEditing != null){
    		// THE ROW AND COLUMN NUMBERING STARTS FROM 0 FOR JTABLE BUT THE COLUMNS AND
    		// ROWS ARE NUMBERED FROM 1 FOR ROWSET.
    		boolean allowEdit;
    		// IF ITS NEW ROW SEND A NULL FOR THE OLD VALUE
    		if(_row == rowCount)
    			allowEdit = cellEditing.cellUpdateRequested(_row+1, _column+1, null, _value);
    		 else
    		 	allowEdit = cellEditing.cellUpdateRequested(_row+1, _column+1, getValueAt(_row,_column), _value);
    		// IF THE USER DOES NOT PERMIT THE UPDATE RETURN ELSE GO AHEAD AND UPDATE THE
    		// DATABASE.
    		if(!allowEdit)
    			return;
    	}
    	
    	if( _row == rowCount ){
    		insertRow(_value,_column);
    		return;
    	}
//    	System.out.println("Set value at "+ _row + "  " + _column + " with "+ _value);	
    	try{
    		if(rowset.getRow() != _row +1)
    			rowset.absolute(_row +1);
    		if( _value == null){
    			rowset.updateNull(_column+1);
    			return;
    		}
    		
    		
    		int type = metaData.getColumnType(_column + 1);
    		switch(type){
				case Types.INTEGER:
	        	case Types.SMALLINT:
	        	case Types.TINYINT:
	        		rowset.updateInt(_column+1, ((Integer)_value).intValue());
	        		break;
	        	case Types.BIGINT:
	        		break;	
	        	case Types.DOUBLE:
	        	case Types.FLOAT:	
	        		rowset.updateDouble(_column+1, ((Double)_value).doubleValue());
	        		
	        		break;
	        	case Types.BIT:
	        		rowset.updateBoolean(_column+1, ((Boolean)_value).booleanValue());
	        		break;
	        	case Types.DATE:
	        	//  IF A DATE RENDERER AND EDITOR IS USED THE DATE IS DISPLAYED AS STRING.
	        	// SO A STRING WILL BE SENT FOR UPDATE
	        	// IN SUCH A CASE CHECK IS THE STRING IS EMPTY IF SO UPDATE NULL FOR THE FEILD
	        		if(_value instanceof String){
	        			if(getSQLDate((String)_value) == null)
	        				rowset.updateNull(_column+1);
	        			else
	        				rowset.updateDate(_column+1,getSQLDate((String)_value));
	        		}	
	        		else
	        			rowset.updateDate(_column+1,(Date)_value);
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
//	        System.out.println("Updated value: " + getValueAt(_row,_column));
	     }catch(SQLException se){
	     	se.printStackTrace();
	     	if(grid != null)
	     		JOptionPane.showMessageDialog(grid,"Error while updating the value.\n" + se.getMessage());
	     }
    }
    
    private void insertRow(Object _value, int _column){
    	if( _value == null)
    		return;
    		   		
    	try{
    		if(!inInsertRow){
    			rowset.moveToInsertRow();
    			setDefaults();
    			inInsertRow = true;
    			if(dataValue != null)
    				setPrimaryColumn();
    				
    		}
    		int type = metaData.getColumnType(_column + 1);
			switch(type){
				case Types.INTEGER:
	        	case Types.SMALLINT:
	        	case Types.TINYINT:
	        		rowset.updateInt(_column+1, ((Integer)_value).intValue());
	        		break;
	        	case Types.BIGINT:
	        		break;	
	        	case Types.DOUBLE:
	        	case Types.FLOAT:	
	        		rowset.updateDouble(_column+1, ((Double)_value).doubleValue());
	        		break;
	        	case Types.BIT:
	        		rowset.updateBoolean(_column+1, ((Boolean)_value).booleanValue());
	        		break;
	        	case Types.DATE:
	        		if(_value instanceof String)
	        			rowset.updateDate(_column+1,getSQLDate((String)_value));
	        		else
	        			rowset.updateDate(_column+1,(Date)_value);
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
	    	if(rowCount != 0)
	    		rowset.moveToCurrentRow();
	    	else{
	    		rowset.first();
	    	}
	    	rowset.refreshRow();
//	    	System.out.println("Row number of inserted row : "+ rowset.getRow());
	    	if(grid != null)
	    		grid.updateUI();	
	        inInsertRow = false;
	        rowCount++;
	        
	        	        
	   }catch(SQLException se){
	     	se.printStackTrace();
	   }
//	   System.out.println("Successfully added row");
	}
	
	public void setDefaults(){
		if( defaultValuesMap == null)
			return;
			
		Set keySet = defaultValuesMap.keySet();
        Iterator iterator = keySet.iterator();
        try{
	        while(iterator.hasNext()){
	        	Integer column = (Integer)iterator.next();
	        	System.out.println("Column number is:" + column);
	        	int type = metaData.getColumnType(column.intValue());
				switch(type){
					case Types.INTEGER:
		        	case Types.SMALLINT:
		        	case Types.TINYINT:
		        		rowset.updateInt(column.intValue(), ((Integer)defaultValuesMap.get(column)).intValue());
		        		break;
		        	case Types.BIGINT:
		        		rowset.updateLong(column.intValue(), ((Long)defaultValuesMap.get(column)).longValue());
		        		break;	
		        	case Types.DOUBLE:
		        	case Types.FLOAT:	
		        		rowset.updateDouble(column.intValue(), ((Double)defaultValuesMap.get(column)).doubleValue());
		        		break;
		        	case Types.BIT:
		        		rowset.updateBoolean(column.intValue(), ((Boolean)defaultValuesMap.get(column)).booleanValue());
		        		break;
		        	case Types.DATE:
		        		rowset.updateDate(column.intValue(), (Date)defaultValuesMap.get(column));
		        		break;
		        	case Types.CHAR:
		        	case Types.VARCHAR:
		        	case Types.LONGVARCHAR:		
		        		rowset.updateString(column.intValue(), (String)defaultValuesMap.get(column));
		        		break;
		        	default:
		        		System.out.println("SSTableModel.setValueAt(): Unknown data type");	
		        }// END OF SWITCH
		      
        	}//END OF WHILE
        }catch(SQLException se){
        	se.printStackTrace();
        }
	}
    
    public Class getColumnClass(int _column) {
        int type;
        try {
            type = metaData.getColumnType(_column+1);
        }
        catch (SQLException e) {
            return super.getColumnClass(_column);
        }

        switch(type) {
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
            return String.class;

        case Types.BIT:
            return Boolean.class;

        case Types.TINYINT:
        case Types.SMALLINT:
        case Types.INTEGER:
            return Integer.class;

        case Types.BIGINT:
            return Long.class;

        case Types.FLOAT:
        case Types.DOUBLE:
            return Double.class;

        case Types.DATE:
            return java.sql.Date.class;

        default:
            return Object.class;
        }
    }
    
    /**
     * Deletes the specified row from the database.
     *The rows  are numbered as 0,1,......
     *@param _row the row number that has to be deleted.
     *@return returns true on succesful deletion else false.
     */
    public boolean deleteRow(int _row){
    	
    	if(_row < rowCount){
    		try{
    			rowset.absolute(_row +1);
    			rowset.deleteRow();
    			rowCount--;
    			return true;
    		}catch(SQLException se){
    			se.printStackTrace();
    		}
    	}
    	return false;
    }
    
/*    public void rowSelectionChanged(int _row){
    	try{
	    	if(_row != rowCount && inInsertRow == true){
	    		System.out.println("RowCount before insert: " + rowCount);
	    		rowset.insertRow();
	    		if(rowCount != 0)
	    			rowset.moveToCurrentRow();
	    		System.out.println("RowCount before insert: " + rowCount);	
	    	}
	    }catch(SQLException se){
	    	se.printStackTrace();
	    }
    	  	
   }
*/    
    public void setDefaultValues(int[] _columnNumbers, Object[] _values){
    	if(defaultValuesMap == null)
    		defaultValuesMap = new HashMap();
    	else
    		defaultValuesMap.clear();
    		
    	for(int i=0;i<_columnNumbers.length;i++){
    		defaultValuesMap.put(new Integer(_columnNumbers[i]), _values[i]);
    	}
    }
    
    public Object getDefaultValue(int _columnNumber){
    	Object value = null;
    	if(defaultValuesMap != null){
    		value = defaultValuesMap.get(new Integer(_columnNumber));
    	}
    	return value;
    }
    
    
    public void setGrid(SSDataGrid _grid){
    	grid = _grid;
    }
    
    public void setPrimaryColumn(int _columnNumber){
    	primaryColumn = _columnNumber;
    }
    
    public void setSSDataValue(SSDataValue _dataValue){
    	dataValue = _dataValue;
    }
    
    public void setPrimaryColumn(){
    	try{
    		
    		int type = metaData.getColumnType(primaryColumn);
			switch(type){
				case Types.INTEGER:
	        	case Types.SMALLINT:
	        	case Types.TINYINT:
	        		rowset.updateInt(primaryColumn,((Integer)dataValue.getPrimaryColumnValue()).intValue());
	        		break;
	        	case Types.BIGINT:
	        		rowset.updateLong(primaryColumn,((Long)dataValue.getPrimaryColumnValue()).longValue());
	        		break;	
	        	case Types.DOUBLE:
	        	case Types.FLOAT:	
	        		rowset.updateDouble(primaryColumn,((Double)dataValue.getPrimaryColumnValue()).doubleValue());
	        		break;
	        	case Types.BIT:
	        		rowset.updateBoolean(primaryColumn,((Boolean)dataValue.getPrimaryColumnValue()).booleanValue());
	        		break;
	        	case Types.DATE:
	        		rowset.updateDate(primaryColumn,(Date)dataValue.getPrimaryColumnValue());
	        		break;
	        	case Types.CHAR:
	        	case Types.VARCHAR:
	        	case Types.LONGVARCHAR:		
	        		rowset.updateString(primaryColumn,(String)dataValue.getPrimaryColumnValue());
	        		break;
	        	default:
	        		System.out.println("SSTableModel.setPrimaryColumn(): Unknown data type");	
	        }
	    }catch(SQLException se){
	      	se.printStackTrace();
	    }
    }
    
    
    public Date getSQLDate(String _strDate){
    	if(_strDate.trim().equals(""))
    		return null;
    	StringTokenizer strtok = new StringTokenizer(_strDate,"/",false);
    	String month = strtok.nextToken();
    	String day   = strtok.nextToken();
    	String newStrDate = strtok.nextToken() + "-" + month + "-" + day;
    	return Date.valueOf(newStrDate);
    }
    
    public void setHeaders(String[] _headers){
    	headers = _headers;
    }
    
    public String getColumnName(int _columnNumber){
    	if(headers != null){
    		if(_columnNumber < headers.length){
//    			System.out.println("sending header " + headers[_columnNumber]);
    			return headers[_columnNumber];
    		}
    	}
//    	System.out.println(" Not able to supply header name");
    	return "";
    }
    		
    public void setUneditableColumns(int[] _columnNumbers){
    	uneditableColumns = _columnNumbers;
    }
    
    public void setHiddenColumns(int[] _columnNumbers){
    	hiddenColumns = _columnNumbers;
    }	
    
}