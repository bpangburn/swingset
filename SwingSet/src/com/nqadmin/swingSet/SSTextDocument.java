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
 * @author  $Author$
 * @version $Revision$
 */
public class SSTextDocument extends javax.swing.text.PlainDocument {

    protected int columnType = -1;
    protected SSRowSet rowset = null;
    protected String columnName = null;
    protected int columnIndex = -1;
    protected SimpleAttributeSet attribute = new SimpleAttributeSet();

    private MyRowSetListener rowSetListener = new MyRowSetListener();
    private MyDocumentListener documentListener = new MyDocumentListener();


    /**
     * Constructs a Document with the given SSRowSet and column index.
     * The document is bound to the specified column in the SSRowSet
     *
     * @param _rowset   SSRowSet upon which document will be based
     * @param _columnName   column name within SSRowSet upon which document will be based
     */
    public SSTextDocument(SSRowSet _rowset, String _columnName) {
        rowset = _rowset;
        columnName = _columnName;
        bind();
    } // end public SSTextDocument(javax.sql.SSRowSet _rowset, String _columnName) {

    /**
     * Constructs a Document with the given SSRowSet and column index.
     * The document is bound to the specified column in the SSRowSet.
     *
     * @param _rowset   SSRowSet upon which document will be based
     * @param _columnIndex   column index within SSRowSet upon which document will be based
     */
    public SSTextDocument(SSRowSet _rowset, int _columnIndex) {
        rowset = _rowset;
        columnIndex = _columnIndex;
        bind();
    } // end public SSTextDocument(javax.sql.RowSet _rowset, int _columnIndex) {

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
     * Returns the SSRowSet being used for getting the values.
     *
     * @return returns the SSRowSet being used.
     */
    public SSRowSet getSSRowSet() {
        return rowset;
    }

    /**
     * Sets the SSRowSet to be used for binding the document to the specified column.
     * The column name or column index has to be specified prior to setting the SSRowSet.
     *
     * @param _rowset    SSRowSet to be used for binding the document to the specified column.
     */
    //public void setSSRowSet(SSRowSet _rowset) throws SQLException {
    public void setSSRowSet(SSRowSet _rowset) {
        rowset = _rowset;
        bind();
    } // end public void setSSRowSet(SSRowSet _rowset) throws SQLException {

    /**
     * Retrieves meta data for column & initializes document text if applicable.
     */
    protected void bind() {

        // CHECK FOR NULL COLUMN/ROWSET
            if ((columnName==null && columnIndex==-1) || rowset==null) {
                return;
            }

        // REMOVE LISTENERS TO PREVENT DUPLICATION
            removeListeners();

        // EXTRACT META DATA AND INITIALIZE DOCUMENT TEXT IF APPLICABLE
            try {
                // IF COLUMN NAME ISN'T NULL, GET COLUMN INDEX - OTHERWISE, GET
                // COLUMN NAME
                    if (columnName != null) {
                        columnIndex = rowset.getColumnIndex(columnName);
                    } else {
                        columnName = rowset.getColumnName(columnIndex);
                    }

                // GET COLUMN TYPE
                    columnType = rowset.getColumnType(columnIndex);

                // IF ROWS PRESENT IN PRESENT SSROWSET THEN INITIALIZE THE DOCUMENT WITH THE TEXT
                // GETROW RETURNS ZERO IF THERE ARE NO ROWS IN SSROWSET
                    if (rowset.getRow() != 0) {
                        String value = getText();
                        insertString(0,value, attribute);
                        insertUpdate( new AbstractDocument.DefaultDocumentEvent(0,getLength()  , DocumentEvent.EventType.INSERT), attribute );
                    }

            } catch(SQLException se) {
                se.printStackTrace();
            } catch(BadLocationException ble) {
                ble.printStackTrace();
            }

        // ADD BACK LISTENERS
            addListeners();

    } // end protected void bind() {

    // ADDS LISTENERS FOR THE SLIDER AND TEXT FIELD
    private void addListeners() {
        rowset.addRowSetListener(rowSetListener);
        addDocumentListener(documentListener);
    }

    // REMOVES THE LISTENERS FOR TEXT FIELD AND THE SLIDER DISPLAYED
    private void removeListeners() {
        rowset.removeRowSetListener(rowSetListener);
        removeDocumentListener(documentListener);
    }

    private class MyDocumentListener implements DocumentListener, Serializable {
        // WHEN EVER THERE IS ANY CHANGE IN THE DOCUMENT CAN BE REMOVE UPDATE
        // CHANGED UPDATE OR INSERT UPDATE GET THE TEXT IN THE DOCUMENT
        // AND UPDATE THE COLUMN IN THE SSROWSET
        // TO AVOID THE TRIGGERING OF UPDATE ON THE SSROWSET AS A RESULT OF UPDATING THE COLUMN VALUE
        // IN SSROWSET  FIRST REMOVE THE LISTENER ON SSROWSET THEN MAKE THE CHANGES TO THE COLUMN VALUE.
        // AFTER THE CHANGES  ARE MADE ADD BACK THE LISTENER TO SSROWSET.
        public void removeUpdate(DocumentEvent de) {
            rowset.removeRowSetListener(rowSetListener);

            try {
            //  System.out.println("remove update" + getText(0,getLength()) );
                updateText(getText(0,getLength()));
            } catch(BadLocationException ble) {
                ble.printStackTrace();
            }
            rowset.addRowSetListener(rowSetListener);
        }

        public void changedUpdate(DocumentEvent de) {
        //  System.out.println("changed update");
        }

        // WHEN EVER THERE IS ANY CHANGE IN THE DOCUMENT CAN BE REMOVE UPDATE
        // CHANGED UPDATE OR INSERT UPDATE GET THE TEXT IN THE DOCUMENT
        // AND UPDATE THE COLUMN IN THE SSROWSET
        public void insertUpdate(DocumentEvent de) {

            rowset.removeRowSetListener(rowSetListener);
            try {
            //  System.out.println("insert update" + getText(0,getLength()));
                updateText(getText( 0,getLength() ) );
            } catch(BadLocationException ble) {
                ble.printStackTrace();
            }
            rowset.addRowSetListener(rowSetListener);
        }

    } // end private class MyDocumentListener implements DocumentListener, Serializable {


    // REMOVE UPDATES ARE NOT REQUIRED WHEN DOING A IMMEDIATE INSERT AND
    // CALLING  INSERT UPDATE
    private class MyRowSetListener implements RowSetListener, Serializable {
        // WHEN EVER THERE IS A CHANGE IN SSROWSET CAN BE ROW-CHANGED OR SSROWSET-CHANGED
        // OR CURSOR-MOVED GET THE NEW TEXT CORRESPONDING TO THE COLUMN AND UPDATE THE DOCUMENT
        // WHILE DOING SO YOU CAN CAUSE A EVENT TO FIRE WHEN DOCUMENT CHANGES SO REMOVE THE
        // LISTENER ON THE DOCUMENT THEN CHANGE THE DOCUMENT AND THEN ADD BACK THE LISTENER
        public void cursorMoved(RowSetEvent event) {
            removeDocumentListener(documentListener);
//          System.out.println("Cursor Moved");
            updateDocument();

            addDocumentListener(documentListener);
        }

        // WHEN EVER THERE IS A CHANGE IN SSROWSET CAN BE ROW-CHANGED OR SSROWSET-CHANGED
        // OR CURSOR-MOVED GET THE NEW TEXT CORRESPONDING TO THE COLUMN AND UPDATE THE DOCUMENT
        // WHILE DOING SO YOU CAN CAUSE A EVENT TO FIRE WHEN DOCUMENT CHANGES SO REMOVE THE
        // LISTENER ON THE DOCUMENT THEN CHANGE THE DOCUMENT AND THEN ADD BACK THE LISTENER
        public void rowChanged(RowSetEvent event) {
            removeDocumentListener(documentListener);
//          System.out.println("Row Changed");
            updateDocument();

            addDocumentListener(documentListener);
        }

        // WHEN EVER THERE IS A CHANGE IN SSROWSET CAN BE ROW-CHANGED OR SSROWSET-CHANGED
        // OR CURSOR-MOVED GET THE NEW TEXT CORRESPONDING TO THE COLUMN AND UPDATE THE DOCUMENT
        // WHILE DOING SO YOU CAN CAUSE A EVENT TO FIRE WHEN DOCUMENT CHANGES SO REMOVE THE
        // LISTENER ON THE DOCUMENT THEN CHANGE THE DOCUMENT AND THEN ADD BACK THE LISTENER
        public void rowSetChanged(RowSetEvent event) {
            removeDocumentListener(documentListener);
//          System.out.println("RowSet Changed");
            updateDocument();

            addDocumentListener(documentListener);
        }

    } // end private class MyRowSetListener implements RowSetListener, Serializable {

    // THIS METHOD IS USED BY THE ROWSET LISTENERS TO UPDATE THE DOCUMENT
    protected void updateDocument() {
            try {
                if ( rowset.getRow() != 0 ) {
                    String value = getText();
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

    // THIS METHOD IS USED BY THE DOCUMENT LISTENERS WHEN EVER THE USER CHANGES THE TEXT IN
    // THE DOCUMENT THE CHANGES ARE PROPOGATED TO THE BOUND SSROWSET.
    // THE UPDATE ROW WILL NOT BE CALLED BY THIS.
    //
    // THIS FUNCTION UPDATES THE VALUE OF THE COLUM IN THE SSROWSET.
    // FOR THIS LOOK AT THE DATA TYPE OF THE COLUMN AND THEN CALL THE
    // APPROPIATE FUNCTION
    protected void updateText(String strValue) {
        try {
            strValue.trim();
//          System.out.println("Update Text:" + columnName);

            switch(columnType) {
                // IF THE TEXT IS EMPTY THEN YOU HAVE TO INSERT A NULL
                // THIS IS ESPECIALLY THE CASE IF THE DATA TYPE IS NOT TEXT.
                // SO CHECK TO SEE IF THE GIVEN TEXT IS EMPTY IF SO AND NULL TO THE DATABASE

                case Types.INTEGER:
                case Types.SMALLINT:
                case Types.TINYINT:
                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
                    if ( strValue.equals("") ) {
                        rowset.updateNull(columnName);
                    } else {
                        int intValue = Integer.parseInt(strValue);
                        rowset.updateInt(columnName, intValue);
                    }
                    break;

                case Types.BIGINT:
                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
                    if ( strValue.equals("") ) {
                        rowset.updateNull(columnName);
                    } else {
                        long longValue = Long.parseLong(strValue);
                        rowset.updateLong(columnName, longValue);
                    }
                    break;

                case Types.FLOAT:
                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
                    if ( strValue.equals("") ) {
                        rowset.updateNull(columnName);
                    } else {
                        float floatValue = Float.parseFloat(strValue);
                        rowset.updateFloat(columnName, floatValue);
                    }
                    break;

                case Types.DOUBLE:
                case Types.NUMERIC:
                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
    //              System.out.println("ppr" + strValue + "ppr");
                    if ( strValue.equals("") ) {
                        rowset.updateNull(columnName);
                    } else {
                        double doubleValue = Double.parseDouble(strValue);
                        rowset.updateDouble(columnName, doubleValue);
                    }
                    break;

                case Types.BOOLEAN:
                case Types.BIT:
                    if (strValue.equals("")) {
                        rowset.updateNull(columnName);
                    } else {
                        // CONVERT THE GIVEN STRING TO BOOLEAN TYPE
                        boolean boolValue = Boolean.valueOf(strValue).booleanValue();
                        rowset.updateBoolean(columnName, boolValue);
                    }
                    break;

                case Types.DATE:
                    // IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
                    if ( strValue.equals("") ) {
                        rowset.updateNull(columnName);
                    } else if (strValue.length() == 10) {
    //                  System.out.println(strValue);
    //                  Date dateValue = Date.valueOf(strValue);
                        rowset.updateDate(columnName, getSQLDate(strValue));
                    } else {
                        // do nothing
                    }
                    break;

                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    // SINCE THIS IS TEXT FILED WE CAN INSERT AN EMPTY STRING TO THE DATABASE
    //              System.out.println( columnName + "      " + strValue);
                    rowset.updateString(columnName, strValue);
                    break;

                default:
                    System.out.println("Unknown data type");
            } // end switch

        } catch(SQLException se) {
            se.printStackTrace();
//          System.out.println(se.getMessage());
        } catch(NumberFormatException nfe) {
//          System.out.println(nfe.getMessage());
        }

    } // end protected void updateText(String strValue) {

    // THIS METHOD IS USED IN THE LISTENER OF THE SSROWSET TO GET THE NEW TEXT WHEN EVEN
    // THE SSROWSET EVENTS ARE TRIGGERED
    protected String getText() {
        String value = null;
        try {
            // BASED ON THE COLUMN DATA TYPE THE CORRESPONDING FUNCTION
            // IS CALLED TO GET THE VALUE IN THE COLUMN
            switch(columnType) {
                case Types.INTEGER:
                case Types.SMALLINT:
                case Types.TINYINT:
                    value = String.valueOf(rowset.getInt(columnName));
                    break;

                case Types.BIGINT:
                    value = String.valueOf(rowset.getLong(columnName));
                    break;

                case Types.FLOAT:
                    value = String.valueOf(rowset.getFloat(columnName));
                    break;

                case Types.DOUBLE:
                case Types.NUMERIC:
                    value = String.valueOf(rowset.getDouble(columnName));
                    break;

                case Types.BOOLEAN:
                case Types.BIT:
                    value = String.valueOf(rowset.getBoolean(columnName));
                    break;

                case Types.DATE:
                    Date date = rowset.getDate(columnName);
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
                        //value = String.valueOf(rowset.getDate(columnName));
                    }
                    break;

                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    String str = rowset.getString(columnName);
                    if (str == null) {
                        value = "";
                    } else {
                        value = String.valueOf(str);
                    }
                    break;

                default:
                    System.out.println(columnName + " : UNKNOWN DATA TYPE ");
            } // end switch

        } catch(SQLException se) {
            se.printStackTrace();
        }

        return value;

    } // end protected String getText() {

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