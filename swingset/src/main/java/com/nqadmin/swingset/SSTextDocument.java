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

import java.sql.SQLException;

import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import com.nqadmin.swingset.utils.SSCommon;

/**
 * SSTextDocument.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
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
 */
public class SSTextDocument extends javax.swing.text.PlainDocument {
    
    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = -8837650674496040218L;
	
	public void insertUpdate(final int _length, final AttributeSet _attributeSet) {
		insertUpdate(new AbstractDocument.DefaultDocumentEvent(0,_length, DocumentEvent.EventType.INSERT), _attributeSet);
	}


        
    /**
     * Method used by SSRowSet listeners to update the bound document.
     * 
     * @param _ssCommon Common fields shared across SwingSet components
     */
    public void updateDocument(SSCommon _ssCommon) {
            try {
                if (_ssCommon.getSSRowSet().getRow() != 0 ) {
                    //String value = getText(_ssCommon);
                    String value = _ssCommon.getSSRowSet().getColumnText(_ssCommon.getBoundColumnName());
                    if (value == null) {
                        value = "";
                    }
                    replace(0, getLength(), value, null);
         		} else {
                    if ( getLength() > 0 ) {
                        remove(0,getLength() );
    //                  removeUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength() , DocumentEvent.EventType.REMOVE) );
                    }
                }
            } catch(SQLException se) {
                se.printStackTrace();
            } catch(BadLocationException ble) {
                ble.printStackTrace();
            }

    } // end protected void updateDocument() {
    
    @Deprecated
    protected void updateText(String _strValue, SSCommon _ssCommon) {
    	_ssCommon.getSSRowSet().updateColumnText(_strValue, _ssCommon.getBoundColumnName());
    	//updateRowSetColumnText(_strValue, _ssCommon);
    }
    
    // TODO see if we should just use toString()
    public String getText() {
    	
    	String result = "";
    	
    	try {
			result = getText(0,getLength());
		} catch (BadLocationException _ble) {
			_ble.printStackTrace();
		}
    	
    	return result;
    }

//    /**
//     * Method used by document listeners to update the underlying sSRowSet.
//     * @param strValue string to be type-converted as needed and updated in underlying rowset column
//     */
//    protected void updateRowSetColumnText(String strValue, SSCommon _ssCommon) {
//    // THIS METHOD IS USED BY THE DOCUMENT LISTENERS WHEN EVER THE USER CHANGES THE TEXT IN
//    // THE DOCUMENT THE CHANGES ARE PROPOGATED TO THE BOUND SSROWSET.
//    // THE UPDATE ROW WILL NOT BE CALLED BY THIS.
//    //
//    // THIS FUNCTION UPDATES THE VALUE OF THE COLUM IN THE SSROWSET.
//    // FOR THIS LOOK AT THE DATA TYPE OF THE COLUMN AND THEN CALL THE
//    // APPROPIATE FUNCTION        
//        try {
//            strValue.trim();
////          System.out.println("Update Text:" + columnName);
//
//            switch(_ssCommon.getColumnType()) {
//                // IF THE TEXT IS EMPTY THEN YOU HAVE TO INSERT A NULL
//                // THIS IS ESPECIALLY THE CASE IF THE DATA TYPE IS NOT TEXT.
//                // SO CHECK TO SEE IF THE GIVEN TEXT IS EMPTY IF SO AND NULL TO THE DATABASE
//
//                case Types.INTEGER:
//                case Types.SMALLINT:
//                case Types.TINYINT:
//                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//                    if ( strValue.equals("") ) {
//                        _ssCommon.getSSRowSet().updateNull(_ssCommon.getColumnName());
//                    } else {
//                        int intValue = Integer.parseInt(strValue);
//                        _ssCommon.getSSRowSet().updateInt(_ssCommon.getColumnName(), intValue);
//                    }
//                    break;
//
//                case Types.BIGINT:
//                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//                    if ( strValue.equals("") ) {
//                        _ssCommon.getSSRowSet().updateNull(_ssCommon.getColumnName());
//                    } else {
//                        long longValue = Long.parseLong(strValue);
//                        _ssCommon.getSSRowSet().updateLong(_ssCommon.getColumnName(), longValue);
//                    }
//                    break;
//
//                case Types.FLOAT:
//                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//                    if ( strValue.equals("") ) {
//                        _ssCommon.getSSRowSet().updateNull(_ssCommon.getColumnName());
//                    } else {
//                        float floatValue = Float.parseFloat(strValue);
//                        _ssCommon.getSSRowSet().updateFloat(_ssCommon.getColumnName(), floatValue);
//                    }
//                    break;
//
//                case Types.DOUBLE:
//                case Types.NUMERIC:
//                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//    //              System.out.println("ppr" + strValue + "ppr");
//                    if ( strValue.equals("") ) {
//                        _ssCommon.getSSRowSet().updateNull(_ssCommon.getColumnName());
//                    } else {
//                        double doubleValue = Double.parseDouble(strValue);
//                        _ssCommon.getSSRowSet().updateDouble(_ssCommon.getColumnName(), doubleValue);
//                    }
//                    break;
//
//                case Types.BOOLEAN:
//                case Types.BIT:
//                    if (strValue.equals("")) {
//                        _ssCommon.getSSRowSet().updateNull(_ssCommon.getColumnName());
//                    } else {
//                        // CONVERT THE GIVEN STRING TO BOOLEAN TYPE
//                        boolean boolValue = Boolean.valueOf(strValue).booleanValue();
//                        _ssCommon.getSSRowSet().updateBoolean(_ssCommon.getColumnName(), boolValue);
//                    }
//                    break;
//
//                case Types.DATE:
//                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//                    if ( strValue.equals("") ) {
//                        _ssCommon.getSSRowSet().updateNull(_ssCommon.getColumnName());
//                    } else if (strValue.length() == 10) {
//                        _ssCommon.getSSRowSet().updateDate(_ssCommon.getColumnName(), getSQLDate(strValue));
//                    } else {
//                        // do nothing
//                    }
//                    break;
//                case Types.TIMESTAMP:
//                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//                    if ( strValue.equals("") ) {
//                        _ssCommon.getSSRowSet().updateNull(_ssCommon.getColumnName());
//                    } else if (strValue.length() == 10) {
//                        _ssCommon.getSSRowSet().updateTimestamp(_ssCommon.getColumnName(), new Timestamp(getSQLDate(strValue).getTime()));
//                    } else {
//                        // do nothing
//                    }
//                    break;
//                case Types.CHAR:
//                case Types.VARCHAR:
//                case Types.LONGVARCHAR:
//                    // SINCE THIS IS TEXT FILED WE CAN INSERT AN EMPTY STRING TO THE DATABASE
//                    // BUT IF THERE ARE UNIQUE CONSTRAINTS ON THIS COLUMN THEN THERE IS NO WAY
//                    // FOR USER TO REMOVE THE VALUE SO INSERT NULL IF THE VALUE IS EMPTY STRING.
//                    if ( strValue.equals("") ) {
//                        _ssCommon.getSSRowSet().updateNull(_ssCommon.getColumnName());
//                    }
//                    else{
//                        _ssCommon.getSSRowSet().updateString(_ssCommon.getColumnName(), strValue);
//                    }
//                    
//                    break;
//
//                default:
//                    System.out.println("Unknown data type");
//            } // end switch
//
//        } catch(SQLException se) {
//            se.printStackTrace();
////          System.out.println(se.getMessage());
//        } catch(NumberFormatException nfe) {
////          System.out.println(nfe.getMessage());
//        }
//
//    } // end protected void updateText(String strValue) {
//    
    
    
    @Deprecated
    protected String getText(SSCommon _ssCommon) {
    	return _ssCommon.getSSRowSet().getColumnText(_ssCommon.getBoundColumnName());
    }

//    /**
//     * Method used by ssRowSet listeners to get the new text when the SSRowSet
//     * events are triggered.
//     * 
//     * @return text representation of data in column
//     */
//    protected String getRowSetColumnText(SSCommon _ssCommon) {
//        String value = null;
//        try {
//        	// IF THE COLUMN HAS NULL RETURN NULL
//            if(_ssCommon.getSSRowSet().getObject(_ssCommon.getColumnName()) == null) {
//            	return null;
//            }
//            // BASED ON THE COLUMN DATA TYPE THE CORRESPONDING FUNCTION
//            // IS CALLED TO GET THE VALUE IN THE COLUMN
//            switch(_ssCommon.getColumnType()) {
//                case Types.INTEGER:
//                case Types.SMALLINT:
//                case Types.TINYINT:
//                    value = String.valueOf(_ssCommon.getSSRowSet().getInt(_ssCommon.getColumnName()));
//                    break;
//
//                case Types.BIGINT:
//                    value = String.valueOf(_ssCommon.getSSRowSet().getLong(_ssCommon.getColumnName()));
//                    break;
//
//                case Types.FLOAT:
//                    value = String.valueOf(_ssCommon.getSSRowSet().getFloat(_ssCommon.getColumnName()));
//                    break;
//
//                case Types.DOUBLE:
//                case Types.NUMERIC:
//                    value = String.valueOf(_ssCommon.getSSRowSet().getDouble(_ssCommon.getColumnName()));
//                    break;
//
//                case Types.BOOLEAN:
//                case Types.BIT:
//                    value = String.valueOf(_ssCommon.getSSRowSet().getBoolean(_ssCommon.getColumnName()));
//                    break;
//
//                case Types.DATE:
//                case Types.TIMESTAMP:
//                    Date date = _ssCommon.getSSRowSet().getDate(_ssCommon.getColumnName());
//                    if (date == null) {
//                        value = "";
//                    } else {
//                        GregorianCalendar calendar = new GregorianCalendar();
//                        calendar.setTime(date);
//                        value = "";
//                        if (calendar.get(Calendar.MONTH) + 1 < 10 ) {
//                            value = "0";
//                        }
//                        value = value + (calendar.get(Calendar.MONTH) + 1) + "/";
//
//                        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
//                            value = value + "0";
//                        }
//                        value = value + calendar.get(Calendar.DAY_OF_MONTH) + "/";
//                        value = value + calendar.get(Calendar.YEAR);
//                        //value = String.valueOf(sSRowSet.getDate(columnName));
//                    }
//                    break;
//
//                case Types.CHAR:
//                case Types.VARCHAR:
//                case Types.LONGVARCHAR:
//                    String str = _ssCommon.getSSRowSet().getString(_ssCommon.getColumnName());
//                    if (str == null) {
//                        value = "";
//                    } else {
//                        value = String.valueOf(str);
//                    }
//                    break;
//
//                default:
//                    System.out.println(_ssCommon.getColumnName() + " : UNKNOWN DATA TYPE ");
//            } // end switch
//
//        } catch(SQLException se) {
//            se.printStackTrace();
//        }
//              
//        return value;
//
//    } // end protected String getRowSetColumnText() {
//    
//    /**
//     * Converts a date string in "MM/dd/yyyy" format to an SQL Date.
//     *
//     * @param _strDate   date string in "MM/dd/yyyy" format
//     *
//     * @return return SQL date for the string specified
//     */
//    protected static Date getSQLDate(String _strDate) {
//        StringTokenizer strtok = new StringTokenizer(_strDate,"/",false);
//        String month = strtok.nextToken();
//        String day   = strtok.nextToken();
//        String newStrDate = strtok.nextToken() + "-" + month + "-" + day;
//        return Date.valueOf(newStrDate);
//    }          


//    /**
//     * Listener(s) for the bound document used to update the underlying SSRowSet.
//     */    
//    protected class SSTextDocumentListener implements DocumentListener, Serializable {
//        /**
//		 * unique serial id
//		 */
//		private static final long serialVersionUID = 2287696691641310793L;
//
//		// WHEN EVER THERE IS ANY CHANGE IN THE DOCUMENT CAN BE REMOVE UPDATE
//        // CHANGED UPDATE OR INSERT UPDATE GET THE TEXT IN THE DOCUMENT
//        // AND UPDATE THE COLUMN IN THE SSROWSET
//        // TO AVOID THE TRIGGERING OF UPDATE ON THE SSROWSET AS A RESULT OF UPDATING THE COLUMN VALUE
//        // IN SSROWSET  FIRST REMOVE THE LISTENER ON SSROWSET THEN MAKE THE CHANGES TO THE COLUMN VALUE.
//        // AFTER THE CHANGES  ARE MADE ADD BACK THE LISTENER TO SSROWSET.
//        @Override
//		public void removeUpdate(DocumentEvent de) {
//            SSTextDocument._ssCommon.getSSRowSet().removeRowSetListener(SSTextDocument.this.rowSetListener);
//
//            try {
//            //  System.out.println("remove update" + getText(0,getLength()) );
//                updateText(getText(0,getLength()));
//            } catch(BadLocationException ble) {
//                ble.printStackTrace();
//            }
//            SSTextDocument._ssCommon.getSSRowSet().addRowSetListener(SSTextDocument.this.rowSetListener);
//        }
//
//        @Override
//		public void changedUpdate(DocumentEvent de) {
//        //  System.out.println("changed update");
//        }
//
//        // WHEN EVER THERE IS ANY CHANGE IN THE DOCUMENT CAN BE REMOVE UPDATE
//        // CHANGED UPDATE OR INSERT UPDATE GET THE TEXT IN THE DOCUMENT
//        // AND UPDATE THE COLUMN IN THE SSROWSET
//        @Override
//		public void insertUpdate(DocumentEvent de) {
//
//            SSTextDocument._ssCommon.getSSRowSet().removeRowSetListener(SSTextDocument.this.rowSetListener);
//            try {
//            //  System.out.println("insert update" + getText(0,getLength()));
//                updateText(getText( 0,getLength() ) );
//            } catch(BadLocationException ble) {
//                ble.printStackTrace();
//            }
//            SSTextDocument._ssCommon.getSSRowSet().addRowSetListener(SSTextDocument.this.rowSetListener);
//        }
//
//    } // end protected class SSTextDocumentListener implements DocumentListener, Serializable {
    
    


} // end public class SSTextDocument extends javax.swing.text.PlainDocument {

/*
 * $Log$
 * Revision 1.26  2007/10/26 20:36:14  prasanth
 * getText now returns null if getObject returns null.
 *
 * Revision 1.25  2006/05/15 16:10:38  prasanth
 * Updated copy right
 *
 * Revision 1.24  2005/04/14 15:38:34  prasanth
 * Setting the column value to null when the string value is empty and the column
 * type is varchar. This was needed to support string columns with unique constraints.
 *
 * Revision 1.23  2005/03/09 21:55:20  prasanth
 * Added TIMESTAMP column type in updateText & getText functions.
 *
 * Revision 1.22  2005/02/10 20:13:03  yoda2
 * Setter/getter cleanup & method reordering for consistency.
 *
 * Revision 1.21  2005/02/09 22:42:34  yoda2
 * JavaDoc cleanup.
 *
 * Revision 1.20  2005/02/07 20:36:41  yoda2
 * Made private listener data members final.
 *
 * Revision 1.19  2005/02/05 18:16:20  yoda2
 * API cleanup.
 *
 * Revision 1.18  2005/02/04 23:05:10  yoda2
 * no message
 *
 * Revision 1.17  2005/02/04 22:48:54  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.16  2004/11/11 14:45:33  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.15  2004/11/10 22:55:51  dags
 * Modified boolean support, as a side efect of SSDBCheckBox update.
 *
 * Revision 1.14  2004/11/01 15:53:30  yoda2
 * Fixed various JavaDoc errors.
 *
 * Revision 1.13  2004/11/01 15:48:39  yoda2
 * Added support for NUMERIC.  Made type support consistent across SwingSet: INTEGER, SMALLINT, TINYINT (Integer); BIGINT (Long); FLOAT (Float); DOUBLE, NUMERIC (Double); BOOLEAN, BIT (Boolean); DATE (Date); CHAR, VARCHAR, LONGVARCHAR (String).
 *
 * Revision 1.12  2004/10/25 22:13:43  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.11  2004/10/25 19:51:03  prasanth
 * Modified to use the new SSRowSet instead of  RowSet.
 *
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
