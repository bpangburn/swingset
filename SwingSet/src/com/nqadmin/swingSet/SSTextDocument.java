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

import java.sql.*;
import java.io.*;
import java.text.*;
import javax.sql.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.StringTokenizer;
import com.nqadmin.swingSet.datasources.SSRowSet;

/**
 * SSTextDocument.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Java PlainDocument that is 'database-aware'.  When developing a database
 * application the SSTextDocument can be used in conjunction with the
 * SSDataNavigator to allow for both editing and navigation of the rows in a
 * database table.
 *
 * The SSTextDocument takes a SSRowSet and either a column index or a column name
 * as arguments.  Whenever the cursor is moved (e.g. navigation occurs on the
 * SSDataNavigator), the document property of the bound Swing control changes to
 * reflect the new value for the database column.
 *
 * Note that a SSRowSet insert doesn't implicitly modify the cursor which is why
 * the SSDBNavImp is provided for clearing controls following an insert.
 *</pre><p>
 * @author	$Author$
 * @version	$Revision$
 */
public class SSTextDocument extends javax.swing.text.PlainDocument {

	protected int columnType = -1;
	protected SSRowSet rs = null;
	protected String columnName = null;
	protected int columnIndex = -1;
	protected SimpleAttributeSet attribute = new SimpleAttributeSet();
	
	protected MyRowSetListener rowSetListener = new MyRowSetListener();
	protected MyDocumentListener documentListener = new MyDocumentListener();


	/**
	 * Constructs a Document with the given rowset and column index.
	 * The document is bound to the specified column in the rowset
     *
     * @param _rs   SSRowSet upon which document will be based
     * @param _columnName   column name within SSRowSet upon which document will be based
	 */
	public SSTextDocument(SSRowSet _rs, String _columnName) {

		rs = _rs;
		columnName = _columnName;
		try {
		// FINDS THE COLUMN INDEX (REQUIRED TO GET ANY META DATA)
			columnIndex = rs.getColumnIndex(columnName);
		// GETS THE COLUMN DATA TYPE
			columnType = rs.getColumnType(columnIndex);
		// IF ROWS PRESENT IN PRESENT ROWSET THEN INITIALIZE THE DOCUMENT WITH THE TEXT
		// GETROW RETURNS ZERO IF THERE ARE NO ROWS IN ROWSET
			if (rs.getRow() != 0) {
				String value = getText();
				insertString(0,value, attribute);
				insertUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength()  , DocumentEvent.EventType.INSERT), attribute );
			}

		} catch(SQLException se) {
			se.printStackTrace();
		} catch(BadLocationException ble) {
			ble.printStackTrace();
		}
		// ADD LISTENERS FOR THE ROWSET AND THE DOCUMENT
		rs.addRowSetListener(rowSetListener);
		addDocumentListener(documentListener);

	} // end public SSTextDocument(javax.sql.SSRowSet _rs, String _columnName) {

	/**
	 * Constructs a Document with the given rowset and column index.
	 * The document is bound to the specified column in the rowset\
     *
     * @param _rs   SSRowSet upon which document will be based
     * @param _columnName   column index within SSRowSet upon which document will be based     
	 */
	public SSTextDocument(SSRowSet _rs, int _columnIndex) {

		rs = _rs;
		columnIndex = _columnIndex;

		try {

		// GET THE COLUMN TYPE AND COLUMN NAME FROM THE META DATA
			columnType = rs.getColumnType(columnIndex);
			columnName = rs.getColumnName(columnIndex);
//			System.out.println(columnType + "   " + columnName);// + "  " + metaData.getSchemaName(columnIndex));

			// CHECK IF THERE ARE ROWS IN THE ROWSET THEN
			// SET THE DOCUMENT TO THE TEXT CORRESPONDING TO THE COLUMN
			if (rs.getRow() != 0) {
				String value = getText();
				insertString(0,value, attribute);
				insertUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength()  , DocumentEvent.EventType.INSERT), attribute );
			}

		} catch(SQLException se) {
			se.printStackTrace();
		} catch(BadLocationException ble) {
			ble.printStackTrace();
		}

	// ADD LISTENERS TO THE DOCUMENT AND ROWSET
		rs.addRowSetListener(rowSetListener);
		addDocumentListener(documentListener);
	
	} // end public SSTextDocument(javax.sql.RowSet _rs, int _columnIndex) {

	/**
	 * Sets the column name to which the document has to be bound to.
     *
	 * @param _columnName    Column Name to which the document has to be bound to.
	 */
	public void setColumnName(String _columnName) {
		columnName = _columnName;
		columnIndex = -1;
	}
	
	/**
	 * Sets the column index to which the document has to be bound to.
     *
	 * @param _columnIndex    Column index to which the document has to be bound to.
	 */
	public void setColumnIndex(int _columnIndex) {
		columnIndex = _columnIndex;
		columnName = null;
	}
	
	/**
	 * Returns the column name to which the document is bound to.
     *
	 * @return returns the column name to which the document is bound to.
	 */
	public String getColumnName() {
		return columnName;
	}
	
	/**
	 * Returns the index of the column to which this document is bound.
     *
	 * @return returns the index of the column to which this document is bound.
	 */
	public int getColumnIndex() {
		return columnIndex;
	}
	
	/**
	 * Returns the rowset being used for getting the values.
     *
	 * @return returns the rowset being used.
	 */
	public SSRowSet getSSRowSet() {
		return rs;
	}
	
	/**
	 * Sets the rowset to be used for binding the document to the specified column.
	 * The column name or column index has to be specified prior to setting the rowset.
     *
	 * @param _rowset    rowset to be used for binding the document to the specified column.
	 */
	public void setSSRowSet(SSRowSet _rowset) throws SQLException {
		rs = _rowset;
		try {
			if (columnName != null) {
			// FINDS THE COLUMN INDEX (REQUIRED TO GET ANY META DATA)
				columnIndex = rs.getColumnIndex(columnName);
			} else if(columnIndex != -1) {
				// do nothing
			} else {
				throw new SQLException("Column Name not specified");
			}
			
		// GET THE COLUMN TYPE AND COLUMN NAME FROM THE META DATA
			columnType = rs.getColumnType(columnIndex);
			columnName = rs.getColumnName(columnIndex);
		
		// CHECK IF THERE ARE ROWS IN THE ROWSET THEN
		// SET THE DOCUMENT TO THE TEXT CORRESPONDING TO THE COLUMN
			if (rs.getRow() != 0) {
				String value = getText();
				if (value != null && value.length() > 0) {
					value = value.replace('\r',' ');
					insertString(0,value, attribute);
					insertUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength()  , DocumentEvent.EventType.INSERT), attribute );
				}
			}
			
		} catch(BadLocationException ble) {
			ble.printStackTrace();
		}
        
		
	} // end public void setSSRowSet(SSRowSet _rowset) throws SQLException {

	/**
	 * Initializes the values to default values.
	 */
	private void init(){
		columnIndex = -1;
		columnName = null;
		columnType = -1;
		if (rs != null) {
			rs.removeRowSetListener(rowSetListener);
		}
		removeDocumentListener(documentListener);
	}
	
	private class MyDocumentListener implements DocumentListener, Serializable {
		// WHEN EVER THERE IS ANY CHANGE IN THE DOCUMENT CAN BE REMOVE UPDATE
		// CHANGED UPDATE OR INSERT UPDATE GET THE TEXT IN THE DOCUMENT
		// AND UPDATE THE COLUMN IN THE ROWSET
		// TO AVOID THE TRIGGERING OF UPDATE ON THE ROWSET AS A RESULT OF UPDATING THE COLUMN VALUE
		// IN ROWSET  FIRST REMOVE THE LISTENER ON ROWSET THEN MAKE THE CHANGES TO THE COLUMN VALUE.
		// AFTER THE CHANGES  ARE MADE ADD BACK THE LISTENER TO ROWSET.
		public void removeUpdate(DocumentEvent de) {
			rs.removeRowSetListener(rowSetListener);

			try {
			//	System.out.println("remove update" + getText(0,getLength()) );
				updateText(getText(0,getLength()));
			} catch(BadLocationException ble) {
				ble.printStackTrace();
			}
			rs.addRowSetListener(rowSetListener);
		}

		public void changedUpdate(DocumentEvent de) {
		//	System.out.println("changed update");
		}
        
		// WHEN EVER THERE IS ANY CHANGE IN THE DOCUMENT CAN BE REMOVE UPDATE
		// CHANGED UPDATE OR INSERT UPDATE GET THE TEXT IN THE DOCUMENT
		// AND UPDATE THE COLUMN IN THE ROWSET
		public void insertUpdate(DocumentEvent de) {

			rs.removeRowSetListener(rowSetListener);
			try {
			//	System.out.println("insert update" + getText(0,getLength()));
				updateText(getText( 0,getLength() ) );
			} catch(BadLocationException ble) {
				ble.printStackTrace();
			}
			rs.addRowSetListener(rowSetListener);
		}
        
	} // end private class MyDocumentListener implements DocumentListener, Serializable {


	// REMOVE UPDATES ARE NOT REQUIRED WHEN DOING A IMMEDIATE INSERT AND
	// CALLING  INSERT UPDATE
	private class MyRowSetListener implements RowSetListener, Serializable {
		// WHEN EVER THERE IS A CHANGE IN ROWSET CAN BE ROW-CHANGED OR ROWSET-CHANGED
		// OR CURSOR-MOVED GET THE NEW TEXT CORRESPONDING TO THE COLUMN AND UPDATE THE DOCUMENT
		// WHILE DOING SO YOU CAN CAUSE A EVENT TO FIRE WHEN DOCUMENT CHANGES SO REMOVE THE
		// LISTENER ON THE DOCUMENT THEN CHANGE THE DOCUMENT AND THEN ADD BACK THE LISTENER
		public void cursorMoved(RowSetEvent event) {
			removeDocumentListener(documentListener);
//			System.out.println("Cursor Moved");
			try {
                if (rs.getRow() != 0) {
                    String value = getText();
                    if (value == null) {
                        value = "";
                    }
                    replace(0, getLength(), value, null);	
    /*				if( getLength() > 0 ){
                        remove(0,getLength() );
                        removeUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength() , DocumentEvent.EventType.REMOVE) );
                    }
                    if(value!=null && value.length() > 0) {
                        insertString(0,value, attribute);
    //					insertUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength() , DocumentEvent.EventType.INSERT), attribute );
                    }
                    
    */			} else {
                    if ( getLength() > 0 ) {
                        remove(0,getLength() );
    //					removeUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength() , DocumentEvent.EventType.REMOVE) );
                    }
                }
			} catch(SQLException se) {
				se.printStackTrace();
			} catch(BadLocationException ble) {
				ble.printStackTrace();
			}
			addDocumentListener(documentListener);
		}

		// WHEN EVER THERE IS A CHANGE IN ROWSET CAN BE ROW-CHANGED OR ROWSET-CHANGED
		// OR CURSOR-MOVED GET THE NEW TEXT CORRESPONDING TO THE COLUMN AND UPDATE THE DOCUMENT
		// WHILE DOING SO YOU CAN CAUSE A EVENT TO FIRE WHEN DOCUMENT CHANGES SO REMOVE THE
		// LISTENER ON THE DOCUMENT THEN CHANGE THE DOCUMENT AND THEN ADD BACK THE LISTENER
		public void rowChanged(RowSetEvent event) {
			removeDocumentListener(documentListener);
//			System.out.println("Row Changed");
			try {
                if ( rs.getRow() != 0 ) {
                    String value = getText();
                    if (value == null) {
                        value = "";
                    }
                    replace(0, getLength(), value, null);		
    
    /*				if( getLength() > 0 ){
                        remove(0,getLength() );
                        //removeUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength() , DocumentEvent.EventType.REMOVE) );
                    }
                    if(value!=null && value.length() > 0) {
                        insertString(0,value, attribute);
    //					insertUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength() , DocumentEvent.EventType.INSERT), attribute );
                    }
    */			} else {
                    if ( getLength() > 0 ) {
                        remove(0,getLength() );
    //					removeUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength() , DocumentEvent.EventType.REMOVE) );
                    }
                }
			} catch(SQLException se) {
				se.printStackTrace();
			} catch(BadLocationException ble) {
				ble.printStackTrace();
			}
			addDocumentListener(documentListener);
		}

		// WHEN EVER THERE IS A CHANGE IN ROWSET CAN BE ROW-CHANGED OR ROWSET-CHANGED
		// OR CURSOR-MOVED GET THE NEW TEXT CORRESPONDING TO THE COLUMN AND UPDATE THE DOCUMENT
		// WHILE DOING SO YOU CAN CAUSE A EVENT TO FIRE WHEN DOCUMENT CHANGES SO REMOVE THE
		// LISTENER ON THE DOCUMENT THEN CHANGE THE DOCUMENT AND THEN ADD BACK THE LISTENER
		public void rowSetChanged(RowSetEvent event) {
			removeDocumentListener(documentListener);
//			System.out.println("RowSet Changed");
			try {
                if ( rs.getRow() != 0 ) {
                    String value = getText();
                    if (value == null) {
                        value = "";
                    }
                    replace(0, getLength(), value, null);	
                    
    /*				if( getLength() > 0 ){
                        remove(0,getLength() );
                        //removeUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength() , DocumentEvent.EventType.REMOVE) );
                    }
                    if(value!=null && value.length() > 0) {
                        insertString(0,value, attribute);
    //					insertUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength() , DocumentEvent.EventType.INSERT), attribute );
                    }
    */			} else {
                    if ( getLength() > 0 ) {
                        remove(0,getLength() );
    //					removeUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength() , DocumentEvent.EventType.REMOVE) );
                    }
                }
			} catch(SQLException se) {
				se.printStackTrace();
			} catch(BadLocationException ble) {
				ble.printStackTrace();
			}
			addDocumentListener(documentListener);
		}

	} // end private class MyRowSetListener implements RowSetListener, Serializable {

 	// THIS METHOD IS USED BY THE DOCUMENT LISTENERS WHEN EVER THE USER CHANGES THE TEXT IN
 	// THE DOCUMENT THE CHANGES ARE PROPOGATED TO THE BOUND ROWSET.
 	// THE UPDATE ROW WILL NOT BE CALLED BY THIS.
 	//
	// THIS FUNCTION UPDATES THE VALUE OF THE COLUM IN THE ROWSET.
	// FOR THIS LOOK AT THE DATA TYPE OF THE COLUMN AND THEN CALL THE
	// APPROPIATE FUNCTION
	private void updateText(String strValue) {
		try {
			strValue.trim();
//			System.out.println("Update Text:" + columnName);

            switch(columnType) {
                // IF THE TEXT IS EMPTY THEN YOU HAVE TO INSERT A NULL
                // THIS IS ESPECIALLY THE CASE IF THE DATA TYPE IS NOT TEXT.
                // SO CHECK TO SEE IF THE GIVEN TEXT IS EMPTY IF SO AND NULL TO THE DATABASE
    
                // IF DATA TYPE IS BOOLEAN THEN CALL UPDATEBOOLEAN FUNCTION
                case Types.BOOLEAN:
                    if (strValue.equals("")) {
                        rs.updateNull(columnName);
                    } else {
                        // CONVERT THE GIVEN STRING TO BOOLEAN TYPE
                        boolean boolValue = Boolean.getBoolean(strValue);
                        rs.updateBoolean(columnName, boolValue);
                    }
                    break;
                case Types.SMALLINT:
                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
                    if ( strValue.equals("") ) {
                        rs.updateNull(columnName);
                    } else {
                        int intValue = Integer.parseInt(strValue);
                        rs.updateInt(columnName, intValue);
                    }
                    break;
                case Types.INTEGER:
                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
                    if ( strValue.equals("") ) {
                        rs.updateNull(columnName);
                    } else {
                        int intValue = Integer.parseInt(strValue);
                        rs.updateInt(columnName, intValue);
                    }
                    break;
                case Types.BIGINT:
                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
                    if ( strValue.equals("") ) {
                        rs.updateNull(columnName);
                    } else {
                        long longValue = Long.parseLong(strValue);
                        rs.updateLong(columnName, longValue);
                    }
                    break;
                case Types.DOUBLE:
                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
    //				System.out.println("ppr" + strValue + "ppr");
                    if ( strValue.equals("") ) {
                        rs.updateNull(columnName);
                    } else {
                        double doubleValue = Double.parseDouble(strValue);
                        rs.updateDouble(columnName, doubleValue);
                    }
                    break;
                case Types.FLOAT:
                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
                    if ( strValue.equals("") ) {
                        rs.updateNull(columnName);
                    } else {
                        float floatValue = Float.parseFloat(strValue);
                        rs.updateFloat(columnName, floatValue);
                    }
                    break;
                case Types.VARCHAR:
                case Types.CHAR:
                    // SINCE THIS IS TEXT FILED WE CAN INSERT AN EMPTY STRING TO THE DATABASE
    //				System.out.println( columnName + "      " + strValue);
                    rs.updateString(columnName, strValue);
                    break;
                case Types.DATE:
                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
                    if ( strValue.equals("") ) {
                        rs.updateNull(columnName);
                    } else if (strValue.length() ==10) {
    //					System.out.println(strValue);
    //					Date dateValue = Date.valueOf(strValue);
                        rs.updateDate(columnName, getSQLDate(strValue));
                    } else {
                        // do nothing                    
                    }
                    break;
                default:
                    System.out.println("Unknown data type");
            } // end switch

		} catch(SQLException se) {
			se.printStackTrace();
//			System.out.println(se.getMessage());
		} catch(NumberFormatException nfe) {
//			System.out.println(nfe.getMessage());
		}
        
	} // end private void updateText(String strValue) {

	// THIS METHOD IS USED IN THE LISTENER OF THE ROWSET TO GET THE NEW TEXT WHEN EVEN
	// THE ROWSET EVENTS ARE TRIGGERED
	private String getText() {
		String value = null;
		try {
			// BASED ON THE COLUMN DATA TYPE THE CORRESPONDING FUNCTION
			// IS CALLED TO GET THE VALUE IN THE COLUMN
            switch(columnType) {
                case Types.BOOLEAN:
                    value = String.valueOf(rs.getBoolean(columnName));
                    break;
                case Types.INTEGER:
                case Types.SMALLINT:
                    value = String.valueOf(rs.getInt(columnName));
                    break;
                case Types.BIGINT:
                    value = String.valueOf(rs.getLong(columnName));
                    break;
                case Types.DOUBLE:
                    value = String.valueOf(rs.getDouble(columnName));
                    break;
                case Types.FLOAT:
                    value = String.valueOf(rs.getFloat(columnName));
                    break;
                case Types.VARCHAR:
                    String str = rs.getString(columnName);
                    if (str == null) {
                        value = "";
                    } else {
                        value = String.valueOf(str);
                    }
                    break;
                case Types.CHAR:
                	value = rs.getString(columnName);
                	break;    
                case Types.DATE:
                    Date date = rs.getDate(columnName);
                    if (date == null) {
                        value = "";
                    } else {
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTime(date);
                        value = "";
                        if (calendar.get(Calendar.MONTH) + 1 < 10 ) {
                            value = "0"; 
                        }
                        value = value + (calendar.get(Calendar.MONTH) + 1) + "/";
                        
                        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
                            value = value + "0";
                        }
                        value = value + calendar.get(Calendar.DAY_OF_MONTH) + "/";
                        value = value + calendar.get(Calendar.YEAR);
                        //value = String.valueOf(rs.getDate(columnName));
                    }
                    break;
                default:
                    System.out.println(columnName + " : UNKNOWN DATA TYPE ");
            } // end switch
       
		} catch(SQLException se) {
			se.printStackTrace();
		}

		return value;

	} // end private String getText() {
	
	/**
	 * Converts a date str (mm/dd/yyyy) in to a sql Date.
     *
	 * @param _strDate   date in mm/dd/yyyy format
     *
	 * @return return java.sql.Date corresponding to _strDate. 
	 */
	public Date getSQLDate(String _strDate) {
    	StringTokenizer strtok = new StringTokenizer(_strDate,"/",false);
    	String month = strtok.nextToken();
    	String day   = strtok.nextToken();
    	String newStrDate = strtok.nextToken() + "-" + month + "-" + day;
    	return Date.valueOf(newStrDate);
    }

} // end public class SSTextDocument extends javax.swing.text.PlainDocument {



/*
 * $Log$
 * Revision 1.10  2004/10/22 16:27:24  prasanth
 * Added CHAR types to the list of supported data types.
 *
 * Revision 1.9  2004/08/11 15:59:49  prasanth
 * Removed check for \r' in one of the constructors.
 *
 * Revision 1.8  2004/08/10 22:06:59  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.7  2004/08/02 15:47:14  prasanth
 * 1. Added setColumnName, setColumnIndex, and setRowSet functions.
 * 2. Added getColumnName, getColumnIndex, and getRowSet functions.
 * 3. Added init method.
 *
 * Revision 1.6  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.5  2004/01/27 19:09:22  prasanth
 * In the RowSet Listener replaces remove and insert functions with
 * replace function.
 *
 * Revision 1.4  2003/11/26 21:31:51  prasanth
 * Calling performCancelOps().
 *
 * Revision 1.3  2003/10/31 16:08:46  prasanth
 * Added method getSQLDate().
 * Corrected a bug.( when a text field is linked to a date column, the column
 * should not be updated until the user enters the complete date.)
 * The text document waits till a 10 char date is entered.
 *
 * Revision 1.2  2003/09/25 14:27:45  yoda2
 * Removed unused Import statements and added preformatting tags to JavaDoc descriptions.
 *
 * Revision 1.1.1.1  2003/09/25 13:56:43  yoda2
 * Initial CVS import for SwingSet.
 *
 */