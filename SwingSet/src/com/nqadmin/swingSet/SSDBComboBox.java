/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2005, The Pangburn Company and Prasanth R. Pasala.
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

import java.io.*;
import java.util.Vector;
import java.util.Stack;
import java.text.SimpleDateFormat;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.nqadmin.swingSet.datasources.SSRowSet;
import com.nqadmin.swingSet.datasources.SSConnection;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;

/**
 * SSDBComboBox.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Similar to the SSComboBox, but used when both the 'bound' values and the
 * 'display' values are pulled from a database table.  Generally the bound
 * value represents a foreign key to another table, and the combobox needs to
 * diplay a list of one (or more) columns from the other table.
 *
 * Note, if changing both a rowset and column name consider using the bind()
 * method rather than individual setSSRowSet() and setColumName() calls. 
 *
 * e.g.
 *
 *      Consider two tables:
 *        1. part_data (part_id, part_name, ...)
 *        2. shipment_data (shipment_id, part_id, quantity, ...)
 *
 *      Assume you would like to develop a screen for the shipment table and you
 *      want to have a screen with a combobox where the user can choose a
 *      part and a textbox where the user can specify a  quantity.
 *
 *      In the combobox you would want to display the part name rather than
 *      part_id so that it is easier for the user to choose. At the same time you
 *      want to store the id of the part chosen by the user in the shipment
 *      table.
 *
 *      SSConnection connection = null;
 *      SSJdbcRowSetImpl rowset = null;
 *      SSDataNavigator navigator = null;
 *      SSDBComboBox combo = null;
 *
 *      try {
 *
 *      // CREATE A DATABASE CONNECTION OBJECT
 *           SSConnection connection = new SSConnection(........);
 *
 *      // CREATE AN INSTANCE OF SSJDBCROWSETIMPL
 *           SSJdbcRowsetImpl rowset = new SSJdbcRowsetImpl(connection);
 *           rowset.setCommand("SELECT * FROM shipment_data;");
 *
 *      // DATA NAVIGATOR CALLS THE EXECUTE AND NEXT FUNCTIONS ON THE SSROWSET.
 *      // IF YOU ARE NOT USING THE DATA NAVIGATOR YOU HAVE TO INCLUDE THOSE.
 *      //   rowset.execute();
 *      //   rowset.next();
 *           SSDataNavigator navigator = new SSDataNavigator(rowset);
 *
 *      // QUERY FOR THE COMBOBOX.
 *           String query = "SELECT * FROM part_data;";
 *
 *      // CREATE AN INSTANCE OF THE SSDBCOMBOBOX WITH THE CONNECTION OBJECT
 *      // QUERY AND COLUMN NAMES
 *           combo = new SSDBComboBox(connection,query,"part_id","part_name");
 *
 *      // THIS BASICALLY SPECIFIES THE COLUMN AND THE SSROWSET WHERE UPDATES HAVE
 *      // TO BE MADE.
 *           combo.bind(rowset,"part_id");
 *           combo.execute();
 *
 *      // CREATE A TEXTFIELD
 *           JTextField myText = new JTextField();
 *           myText.setDocument(new SSTextDocument(rowset, "quantity");
 *
 *      } catch(Exception e) {
 *      // EXCEPTION HANDLER HERE...
 *      }
 *
 *
 *      // ADD THE SSDBCOMBOBOX TO THE JFRAME
 *           getContentPane().add(combo.getComboBox());
 *
 *      // ADD THE JTEXTFIELD TO THE JFRAME
 *           getContentPane().add(myText);
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
//public class SSDBComboBox extends JComponent {
public class SSDBComboBox extends JComboBox {

    /**
     * Text field bound to the SSRowSet.
     */
    protected JTextField textField = new JTextField();

    /**
     * Database connection used to execute queries for combo population.
     */
    protected SSConnection conn = null;

    /**
     * Query used to populate combo box.
     */
    protected String query = null;

    /**
     * The column name whose value is written back to the database when the user
     * chooses an item in the combo box.  This is generally the PK of the table
     * to which a foreign key is mapped.
     */
    protected String queryPKColumnName = null;

    /**
     * The database column used to populate the first visible column of the
     * combo box.
     */
    protected String queryDisplayColumnName1 = null;

    /**
     * The database column used to populate the second (optional) visible column
     * of the combo box.
     */
    protected String queryDisplayColumnName2 = null;

    /**
     * Vector used to store all of the queryPKColumnName values for the
     * combo box.
     */
    protected Vector columnVector = new Vector();

    /**
     * Number of items in the combo box.
     */
    protected int numberOfItems = 0;
    
    /**
     * SSRowSet from which component will get/set values.
     */
    protected SSRowSet rowset;

    /**
     * SSRowSet column to which the component will be bound.
     */
    protected String columnName;

    /**
     * Component listener.
     */
    private final MyComboListener cmbListener = new MyComboListener();

    /**
     * Bound text field document listener.
     */
    private final MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();

    /**
     * Keystroke-based item selection listener.
     */
    private final MyKeyListener myKeyListener = new MyKeyListener();

    /**
     * Alphanumeric separator used to separate values in multi-column combo boxes.
     */
    protected String seperator = " - ";

    /**
     * Format for any date columns displayed in combo box.
     */
    protected String datePattern = "MM/dd/yyyy";

    /**
     * Creates an object of the SSDBComboBox.
     */
    public SSDBComboBox() {
        init();
    }
    
    /**
     * Constructs a SSDBComboBox  with the given parameters.
     *
     * @param _conn database connection to be used.
     * @param _query query to be used to retrieve the values from the database.
     * @param _queryPKColumnName column name whose value has to be stored.
     * @param _queryDisplayColumnName1 column name whose values are displayed in the combo box.
     */
    public SSDBComboBox(SSConnection _conn, String _query, String _queryPKColumnName, String _queryDisplayColumnName1) {
        conn = _conn;
        query = _query;
        queryPKColumnName = _queryPKColumnName;
        queryDisplayColumnName1 = _queryDisplayColumnName1;
        init();
    }
    
    /**
     * Initialization code.
     */
    protected void init() {
        // ADD KEY LISTENER TO TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER
        // THIS IS HANDLED IN MyKeyListener
            
        // SET PREFERRED DIMENSIONS
            setPreferredSize(new Dimension(200,20));
    }      
    
    /**
     * Sets the new SSRowSet for the combo box.
     *
     * @param _rowset  SSRowSet to which the combo has to update values.
     */
    public void setSSRowSet(SSRowSet _rowset) {
        rowset = _rowset;
        bind();
    }    

    /**
     * Sets the connection object to be used.
     *
     * @param _conn    connection object used for database.
     */
    public void setSSConnection(SSConnection _conn) {
        conn = _conn;
    }    

    /**
     * Sets the query used to display items in the combo box.
     *
     * @param _query   query to be used to get values from database (to display combo box items)
     */
    public void setQuery(String _query) {
        query = _query;
    }

    /**
     * Sets the column name for the combo box
     *
     * @param _columnName   name of column
     */
    public void setColumnName(String _columnName) {
        columnName = _columnName;
        bind();
    }

    /**
     * Sets the column name whose values have to be displayed in combo box.
     *
     * @param _queryDisplayColumnName1   column name whose values have to be displayed.
     */
    public void setDisplayColumnName(String _queryDisplayColumnName1) {
        queryDisplayColumnName1 = _queryDisplayColumnName1;
    }

    /**
     * When a display column is of type date you can choose the format in which it has
     * to be displayed. For the pattern refer SimpleDateFormat in java.text package.
     *
     * @param _format pattern in which date has to be displayed.
     */
     public void setDateFormat(String _format) {
        datePattern = _format;
     }

    /**
     * Sets the second display name.
     * If more than one column have to displayed then use this.
     * For the parts example given above. If you have a part description in part table.
     * Then you can display both part name and part description.
     *
     * @param _queryDisplayColumnName2    column name whose values have to be
     *  displayed in the combo in addition to the first column name.
     */
    public void setSecondDisplayColumnName(String _queryDisplayColumnName2) {
        queryDisplayColumnName2 = _queryDisplayColumnName2;
    }

   
    /**
     * Returns connection object used to get values from database.
     *
     * @return returns a SSConnection object.
     */
    public SSConnection getSSConnection() {
        return conn;
    }    

    /**
     * Returns the number of items present in the combo box.
     *
     * @return returns the number of items present in the combo box.
     */
    public int getNumberOfItems() {
        return numberOfItems;
    }

    /**
     * Returns the query used to retrieve values from database for the combo box.
     *
     * @return returns the query used.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Returns the column name to which the combo is bound.
     *
     * @return returns the column name to which to combo box is bound.
     */
    public String getColumnName() {
        return columnName;
    }
    
    /**
     * Returns the SSRowSet being used to get the values.
     *
     * @return returns the SSRowSet being used.
     */
    public SSRowSet getSSRowSet() {
        return rowset;
    }    

    /**
     * Returns the column name whose values are displayed in the combo box.
     *
     * @return returns the name of the column used to get values for combo box items.
     */
    public String getDisplayColumnName() {
        return queryDisplayColumnName1;
    }

    /**
     * Returns the second column name whose values are also displayed in the combo box.
     *
     * @return returns the name of the column used to get values for combo box items.
     *  returns NULL if the second display column is not provided.
     */
    public String getSecondDisplayColumnName() {
        return queryDisplayColumnName2;
    }

    /**
     * Set the seperator to be used when multiple columns are displayed
     *
     * @param _seperator   seperator to be used.
     */
     public void setSeperator(String _seperator) {
        seperator = _seperator;
     }

     /**
      * Returns the seperator used when multiple columns are displayed
      *
      * @return seperator used.
      */
     public String getSeperator() {
        return seperator;
     }

    /**
     * Returns the value of the selected item.
     *
     * @return value corresponding to the selected item in the combo.
     *     return -1 if no item is selected.
     */
    public long getSelectedValue() {

        int index = getSelectedIndex();

        if (index == -1) {
            return -1;
        }

        return Long.valueOf((String)columnVector.get(index)).longValue();

    }

    /**
     * Sets the currently selected value
     *
     * @param _value    value to set as currently selected.
     */
    public void setSelectedValue(long _value) {
        textField.setText(String.valueOf(_value));
    }
    
    /**
     * Returns the value of the selected item.
     *
     * @return value corresponding to the selected item in the combo.
     *     return null if no item is selected.
     */
    public String getSelectedStringValue() {

        int index = getSelectedIndex();

        if (index == -1) {
            return null;
        }

        return (String)columnVector.get(index);

    }

    /**
     * Sets the currently selected value
     *
     * @param _value    value to set as currently selected.
     */
    public void setSelectedStringValue(String _value) {
        textField.setText(_value);
    }    

    /**
     * Executes the query and adds items to the combo box based on the values
     * retrieved from the database.
     */
    public void execute() throws SQLException, Exception {

        // TURN OFF LISTENERS
            removeListeners();

        // DATABASE SETUP
            Statement statement = conn.getConnection().createStatement();
    
            if (query.equals("")) {
                throw new Exception("Query is empty");
            }
    
            ResultSet rs = statement.executeQuery(query);

        // CLEAR ALL ITEMS FROM COMBO AND VECTOR STORING ITS CORRESPONDING VALUES.
            removeAllItems();
            columnVector.clear();

        // LOOP THROUGH VALUES IN RESULTSET AND ADD TO COMBO BOX
            int i = 0;
            while (rs.next()) {
                // IF TWO COLUMNS HAVE TO BE DISPLAYED IN THE COMBO THEY SEPERATED BY SEMI-COLON
                if ( queryDisplayColumnName2 != null) {
                    addItem(getStringValue(rs,queryDisplayColumnName1) + seperator + rs.getString(queryDisplayColumnName2));
                } else {
                    addItem(getStringValue(rs,queryDisplayColumnName1));
                }
                // ADD THE PK TO A VECTOR.
                columnVector.add(i,rs.getString(queryPKColumnName));
                i++;
            }

        // STORE THE NUMBER OF ITEMS IN THE COMBO BOX.
            numberOfItems = i;

        // DISPLAYS THE ITEM CORRESPONDING TO THE PRESENT VALUE IN THE DATABASE BASE.
            updateDisplay();
            addListeners();

    } // end public void execute() throws SQLException, Exception {

    /**
     * Updates the value displayed in the component based on the SSRowSet column
     * binding.
     */
    protected void updateDisplay() {

        //try {
            // GET THE VALUE FROM TEXT FIELD
            String text = textField.getText().trim();
            if (!text.equals("")) {
                //long valueInText = Long.parseLong(text);
                // GET THE INDEX WHERE THIS VALUE IS IN THE VECTOR.
                //int indexCorrespondingToLong = columnVector.indexOf(new Long(valueInText));
                int columnVectorIndex = columnVector.indexOf(text);
                // SET THE SELECTED ITEM OF COMBO TO THE ITEM AT THE INDEX FOUND FROM
                // ABOVE STATEMENT
                //if (indexCorrespondingToLong != getSelectedIndex()) {
                if (columnVectorIndex != getSelectedIndex()) {
                    //setSelectedIndex(indexCorrespondingToLong);
                    setSelectedIndex(columnVectorIndex);
                }
            }
            else{
                setSelectedIndex(-1);
            }
        //} catch(NumberFormatException nfe) {
        //    System.out.println("Possible reason underlying column is not a number field");
        //    nfe.printStackTrace();
        //}

    }
    
    /**
     * Method for handling binding of component to a SSRowSet column.
     */
    protected void bind() {
        
        // CHECK FOR NULL COLUMN/ROWSET
            if (columnName==null || rowset==null) {
                return;
            }
            
        // REMOVE LISTENERS TO PREVENT DUPLICATION
            removeListeners();            

        // BIND THE TEXT FIELD TO THE SPECIFIED COLUMN
            textField.setDocument(new SSTextDocument(rowset, columnName));

        // SET THE COMBO BOX ITEM DISPLAYED
            updateDisplay();

        // ADD BACK LISTENERS
            addListeners();

    }    

    /**
     * Sets the SSRowSet and column name to which the component is to be bound.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    Name of the column to which this check box should be bound
     */
    public void bind(SSRowSet _rowset, String _columnName) {
        rowset  = _rowset;
        columnName = _columnName;
        bind();
    }

    /**
     * Adds listeners for component and bound text field (where applicable).
     */
    private void addListeners() {
        addActionListener(cmbListener);
        addFocusListener(cmbListener);
        addKeyListener(myKeyListener);
        textField.getDocument().addDocumentListener(textFieldDocumentListener);         
    }

    /**
     * Removes listeners for component and bound text field (where applicable).
     */
    private void removeListeners() {
        removeActionListener(cmbListener);
        removeFocusListener(cmbListener);
        removeKeyListener(myKeyListener);
        textField.getDocument().removeDocumentListener(textFieldDocumentListener);
          
    }

    /**
     * Listener(s) for the bound text field used to propigate values back to the
     * component's value.
     */
    private class MyTextFieldDocumentListener implements DocumentListener {

        public void changedUpdate(DocumentEvent de) {
            removeActionListener(cmbListener);
            updateDisplay();
            addActionListener(cmbListener);
        }

        public void insertUpdate(DocumentEvent de) {
            removeActionListener(cmbListener);
            updateDisplay();
            addActionListener(cmbListener);
        }

        public void removeUpdate(DocumentEvent de) {
            removeActionListener(cmbListener);
            updateDisplay();
            addActionListener(cmbListener);
        }

    } // end private class MyTextFieldDocumentListener implements DocumentListener {

    /**
     * Listener for keystroke-based, string matching, combo box navigation.
     */
    private class MyKeyListener extends KeyAdapter {

        String searchString = null;
        Stack searchStack = new Stack();
        int previousIndex = 0;

        public void resetSearchString() {
            searchString = null;
        }

        public void keyReleased(KeyEvent ke) {
            int i;
            if (ke.getKeyCode() == KeyEvent.VK_ESCAPE        ||
                    ke.getKeyCode() == KeyEvent.VK_PAGE_UP   ||
                    ke.getKeyCode() == KeyEvent.VK_PAGE_DOWN ||
                    ke.getKeyCode() == KeyEvent.VK_UP        ||
                    ke.getKeyCode() == KeyEvent.VK_DOWN      ||
                    ke.getKeyCode() == KeyEvent.VK_ENTER       ) {
                searchString = null;
                searchStack.removeAllElements();
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    ((Component)ke.getSource()).transferFocus();
                }
                return;

            }

            if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if (searchString == null ) {
                    setSelectedIndex(0);
                    searchStack.removeAllElements();
                    previousIndex = 0;
                    return;
                }
                if (searchString.length() > 0) {
                    searchString = searchString.substring(0,searchString.length()-1);
                    if (searchString.length() == 0) {
                        searchString = null;
                        searchStack.removeAllElements();
                        previousIndex = 0;
                    }
                }

            } else if(searchString == null) {
                searchString = new String(new char[]{ke.getKeyChar()});
            } else {
                searchString = searchString + new String(new char[]{ke.getKeyChar()});
            }

            if (searchString == null) {
                setSelectedIndex(0);
                return;
            }

            if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {

                if (searchStack.empty()) {
                    setSelectedIndex(0);
                    return;
                } else {
                    // PRESENT POSITION IN COMBO
                    searchStack.pop();
                    // PREVIOUS POSITION IN COMBO
                    if(!searchStack.empty()) {
                        previousIndex = ((Integer)searchStack.peek()).intValue();
                        setSelectedIndex(previousIndex);
                        return;
                    } else {
                        previousIndex = 0;
                        setSelectedIndex(0);
                        return;
                    }
                }

            } else {

                for (i=previousIndex;i<getItemCount();i++) {
                    if (searchString.length() == 0) {
                        setSelectedIndex(0);
                    } else {
                        if (((String)getItemAt(i)).length() >= searchString.length()) {
                            if (searchString.equalsIgnoreCase( ((String)getItemAt(i)).substring(0,searchString.length())) ) {
                                setSelectedIndex(i);
                                searchStack.push(new Integer(i));
                                return;
                            }
                        }
                    }

                }

                if (i == getItemCount()) {
                    for (i=0;i<previousIndex;i++) {
                        if (searchString.length() == 0) {
                            setSelectedIndex(0);
                        } else {
                            if (((String)getItemAt(i)).length() >= searchString.length()) {
                                if (searchString.equalsIgnoreCase( ((String)getItemAt(i)).substring(0,searchString.length())) ){
                                    setSelectedIndex(i);
                                    searchStack.push(new Integer(i));
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            if (searchStack.empty()) {
                setSelectedIndex(0);
            } else {
                setSelectedIndex(((Integer)searchStack.peek()).intValue());
            }

        }
    } // end private class MyKeyListener extends KeyAdapter {

    /**
     * Listener(s) for the component's value used to propigate changes back to
     * bound text field.
     */
    private class MyComboListener extends FocusAdapter implements ActionListener {

        public void actionPerformed(ActionEvent ae) {

            textField.getDocument().removeDocumentListener(textFieldDocumentListener);

            // GET THE INDEX CORRESPONDING TO THE SELECTED TEXT IN COMBO
            int index = getSelectedIndex();

            // IF THE USER WANTS TO REMOVE COMPLETELY THE VALUE IN THE FIELD HE CHOOSES
            // THE EMPTY STRING IN COMBO THEN THE TEXT FIELD IS SET TO EMPTY STRING
            if (index != -1) {
                try {
                    // NOW LOOK UP THE VECTOR AND GET THE VALUE CORRESPONDING TO THE TEXT SELECTED IN THE COMBO
                    //long valueCorresponingToIndex = ( (Long)columnVector.get(index) ).longValue() ;
                    //GET THE TEXT IN THE TEXT FIELD
                    //String strValueinTextField = textField.getText();
                    //INITIALIZE THE  LONG VALUE IN TEXT TO -1
                    //long valueInText = -1;
                    // IF THE TEXT IS NOT NULL PARSE ITS LONG VALUE
                    //if (!strValueinTextField.equals("")) {
                    //    valueInText = Long.parseLong(strValueinTextField);
                    //}
                    String textFieldText = textField.getText();
                    String columnVectorText = (String)columnVector.get(index);
                    
                    if (!textFieldText.equals(columnVectorText)) {
                        textField.setText(columnVectorText);
                    }


                    // IF THE LONG VALUE CORRESPONDING TO THE SELECTED TEXT OF COMBO NOT EQUAL
                    // TO THAT IN THE TEXT FIELD THEN CHANGE THE TEXT IN THE TEXT FIELD TO THAT VALUE
                    // IF ITS THE SAME LEAVE IT AS IS
                    //if (valueInText != valueCorresponingToIndex) {
                    //    textField.setText( String.valueOf(valueCorresponingToIndex) );
                    //}

                } catch(NullPointerException npe) {
                } catch(NumberFormatException nfe) {
                }
            }
            else{
                textField.setText("");
            }

            textField.getDocument().addDocumentListener(textFieldDocumentListener);

        }
        
        public void focusLost(FocusEvent fe){
            myKeyListener.resetSearchString();
        }
        
    } // private class MyComboListener implements ActionListener {

    /**
     * Adds an item to the existing list of items in the combo box.
     *
     * @param _name   name that should be displayed in the combo
     * @param _value   value corresponding the the name
     */
     public void addItem(String _name, long _value) {
        columnVector.add(Long.toString(_value));
        addItem(_name);
        numberOfItems++;
     }
     
    /**
     * Adds an item to the existing list of items in the combo box.
     *
     * @param _name   name that should be displayed in the combo
     * @param _value   value corresponding the the name
     */
     public void addStringItem(String _name, String _value) {
        columnVector.add(_value);
        addItem(_name);
        numberOfItems++;
     }     

     /**
      * Deletes the item which has value equal to _value.
      * If more than one item is present in the combo for that value the first one is changed.      
      *
      * @param _value  value of the item to be deleted.
      *
      * @return returns true on successful deletion else returns false.
      */
     public boolean deleteItem(long _value) {
        int index = columnVector.indexOf(Long.toString(_value));
        if (index == -1) {
            return false;
        }
        columnVector.removeElementAt(index);
        removeItemAt(index);
        numberOfItems--;
        return true;
     }
     
     /**
      * Deletes the item which has value equal to _value.
      * If more than one item is present in the combo for that value the first one is changed.      
      *
      * @param _value  value of the item to be deleted.
      *
      * @return returns true on successful deletion else returns false.
      */
     public boolean deleteStringItem(String _value) {
        int index = columnVector.indexOf(_value);
        if (index == -1) {
            return false;
        }
        columnVector.removeElementAt(index);
        removeItemAt(index);
        numberOfItems--;
        return true;
     }     

    /**
     * Updates the string thats being displayed.
     * If more than one item is present in the combo for that value the first one is changed.
     *
     * NOTE: To retain changes made to current SSRowSet call updateRow before calling the
     * updateItem on SSDBComboBox. (Only if you are using the SSDBComboBox and SSDataNavigator
     * for navigation in the screen. If you are not using the SSDBComboBox for navigation
     * then no need to call updateRow on the SSRowSet. Also if you are using only SSDBComboBox
     * for navigation you need not call the updateRow.)
     *
     * @param _value  the value corresponding to the item in combo to be updated.
     * @param _name   the new name that replace old one.
     *
     * @return returns true if successful else false.
     */
    public boolean updateItem(long _value, String _name) {
        int index = columnVector.indexOf(Long.toString(_value));
        if (index == -1) {
            return false;
        }
        removeActionListener(cmbListener);
        insertItemAt(_name,index+1);
        removeItemAt(index);
        setSelectedIndex(index);
        addActionListener(cmbListener);
        return true;
    }
    
    /**
     * Updates the string thats being displayed.
     * If more than one item is present in the combo for that value the first one is changed.
     *
     * NOTE: To retain changes made to current SSRowSet call updateRow before calling the
     * updateItem on SSDBComboBox. (Only if you are using the SSDBComboBox and SSDataNavigator
     * for navigation in the screen. If you are not using the SSDBComboBox for navigation
     * then no need to call updateRow on the SSRowSet. Also if you are using only SSDBComboBox
     * for navigation you need not call the updateRow.)
     *
     * @param _value  the value corresponding to the item in combo to be updated.
     * @param _name   the new name that replace old one.
     *
     * @return returns true if successful else false.
     */
    public boolean updateStringItem(String _value, String _name) {
        int index = columnVector.indexOf(_value);
        if (index == -1) {
            return false;
        }
        removeActionListener(cmbListener);
        insertItemAt(_name,index+1);
        removeItemAt(index);
        setSelectedIndex(index);
        addActionListener(cmbListener);
        return true;
    }    

    /**
     * Method to return string equalivent of a given resultset column.
     *
     * @param _rs   ResultSet containing column to analyize
     * @param _columnName   column to convert to string
     *
     * @return string equilivent of specified resultset column
     */
    protected String getStringValue(ResultSet _rs, String _columnName) {
        String strValue = "";
        try {
            int type = _rs.getMetaData().getColumnType(_rs.findColumn(_columnName));
            switch(type){
                case Types.DATE:
                    SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
                    strValue = dateFormat.format(_rs.getDate(_columnName));
                break;
                default:
                    strValue = _rs.getString(_columnName);
                break;
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return strValue;

    }
    
    
    
// DEPRECATED STUFF....................

    /**
     * Constructs a SSDBComboBox  with the given parameters.
     *
     * @param _conn    database connection to be used.
     * @param _query   query to be used to retrieve the values from the database.
     * @param _queryPKColumnName  column name whose value has to be stored.
     * @param _queryDisplayColumnName1   column name whose values are displayed in the combo box.
     * @param _textField   a text field to which the combo box has to be synchronized
     *
     * @deprecated
     */
    public SSDBComboBox(SSConnection _conn, String _query, String _queryPKColumnName, String _queryDisplayColumnName1, JTextField _textField) {

        conn                = _conn;
        query               = _query;
        queryPKColumnName   = _queryPKColumnName;
        queryDisplayColumnName1  = _queryDisplayColumnName1;
        textField           = _textField;

    }
    
    /**
     * Sets the connection object to be used.
     *
     * @param _conn    connection object used for database.
     *
     * @deprecated
     * @see #setSSConnection
     */
    public void setConnection(SSConnection _conn) {
        conn = _conn;
    }
        

    /**
     * Sets the new SSRowSet for the combo box.
     *
     * @param _rowset  SSRowSet to which the combo has to update values.
     *
     * @deprecated
     * @see #setSSRowSet     
     */
    public void setRowSet(SSRowSet _rowset) {
        rowset = _rowset;
        bind();
    }

    /**
     * Sets the text field to which the underlying value is written to.
     *
     * @param _textField    text field to which the selected item value has to
     *  be written.
     *
     * @deprecated
     */
    public void setTextField(JTextField _textField) {
        textField = _textField;
    }    

    /**
     * Returns connection object used to get values from database.
     *
     * @return returns a SSConnection object.
     *
     * @deprecated
     * @see #getSSConnection     
     */
    public SSConnection getConnection() {
        return conn;
    }

    /**
     * Returns the text field used to synchronize with the SSRowSet.
     *
     * @return returns the text field used to synchronize with the SSRowSet.
     *
     * @deprecated
     */
    public JTextField getTextField() {
        return textField;
    }

    /**
     * returns the combo box that has to be displayed on screen.
     *
     * @return returns the combo box that displays the items.
     *
     * @deprecated
     */
    public JComboBox getComboBox() {
        return this;
    }

    /**
     * Returns the combo box to be displayed on the screen.
     *
     * @return returns the combo box that displays the items.
     *
     * @deprecated
     */
    public Component getComponent() {
        return this;
    }
    

     /**
      * Deletes the item which has name equal to _name. If there are
      * more than one item with the same name then the first occurance is deleted.
      *
      * @param _name  value of the item to be deleted.
      *
      * @return returns true on successful deletion else returns false.
      *
      * @deprecated      
      */
     public boolean deleteItem(String _name) {

        for (int i=0; i<getItemCount();i++) {
            if ( ((String)getItemAt(i)).equals(_name) ) {
                removeItemAt(i);
                columnVector.removeElementAt(i);
                numberOfItems--;
                return true;
            }
        }
        return false;
     }
     

     /**
      * Deletes the item which has display name equal to _name and corresponding value
      * as _value. If there is more than one item with same name and value then the first
      * occurance is deleted.
      *
      * @param _name   name of item to be deleted
      * @param _value  value of the item to be deleted.
      *
      * @return returns true on successful deletion else returns false.
      *
      * @deprecated       
      */
     public boolean deleteItem(String _name, long _value) {
        for (int i=0; i<getItemCount();i++) {
            if ( ((String)getItemAt(i)).equals(_name) ) {
                if (((String)columnVector.elementAt(i)).equals(Long.toString(_value))) {
                    removeItemAt(i);
                    columnVector.removeElementAt(i);
                    numberOfItems--;
                    return true;
                }
            }
        }
        return false;
     }
     

} // end public class SSDBComboBox extends JComponent {



/*
 * $Log$
 * Revision 1.23  2005/02/07 20:26:06  yoda2
 * Updated to allow non-numeric primary keys. JavaDoc cleanup.
 *
 * Revision 1.22  2005/02/04 22:48:53  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.21  2005/02/04 00:02:48  prasanth
 * 1. Removed commented out code.
 * 2. Using setDisplay in document listener.
 *
 * Revision 1.20  2005/02/02 23:36:58  yoda2
 * Removed setMaximiumSize() calls.
 *
 * Revision 1.19  2005/01/19 20:54:44  yoda2
 * API cleanup.
 *
 * Revision 1.18  2005/01/19 16:47:25  yoda2
 * Finished debugging and renamed some private variables to better distinguish between the bound column name and the column name representing the PK from the DB query.
 *
 * Revision 1.17  2005/01/19 03:17:08  yoda2
 * Rewrote to extend JComboBox rather than JComponent.
 *
 * Revision 1.16  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.15  2004/11/01 15:53:30  yoda2
 * Fixed various JavaDoc errors.
 *
 * Revision 1.14  2004/10/25 22:57:40  prasanth
 * Changed Connection to SSConnection.
 *
 * Revision 1.13  2004/10/25 22:03:18  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.12  2004/10/25 19:51:03  prasanth
 * Modified to use the new SSRowSet instead of  RowSet.
 *
 * Revision 1.11  2004/08/24 22:08:54  prasanth
 * Updating the numberOfItems variable in deleteItem & addItem.
 *
 * Revision 1.10  2004/08/12 23:52:36  prasanth
 * If seleted index is -1 the column value was not updated.
 * Now setting to null if the selected index is -1.
 *
 * Revision 1.9  2004/08/10 22:06:58  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.8  2004/08/09 15:39:48  prasanth
 * Added key listener to transfer focus on enter key.
 *
 * Revision 1.7  2004/08/02 15:21:55  prasanth
 * 1. Deprecated  setTextField function.
 * 2. Added setSelectedValue function.
 * 3. Also added addComponent (private) method.
 *
 * Revision 1.6  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.5  2004/02/23 16:37:41  prasanth
 * Added GENDER_OPTION.
 *
 * Revision 1.4  2003/11/26 21:21:14  prasanth
 * Functionality to format a date object before displaying in the combo.
 *
 * Revision 1.3  2003/10/31 16:06:25  prasanth
 * Added method setSeperator().
 *
 * Revision 1.2  2003/09/25 14:27:45  yoda2
 * Removed unused Import statements and added preformatting tags to JavaDoc descriptions.
 *
 * Revision 1.1.1.1  2003/09/25 13:56:43  yoda2
 * Initial CVS import for SwingSet.
 *
 */