/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2004, The Pangburn Company, Inc. and Prasanth R. Pasala.
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
import javax.sql.*;
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
 *      Connection connection = null;
 *      JdbcRowSet rowset = null;
 *      SSDataNavigator navigator = null;
 *      SSDBComboBox combo = null;
 *
 *      try {
 *
 *      // CREATE A DATABASE CONNECTION OBJECT
 *           Connection connection = new Connection(........);
 *
 *      // CREATE AN INSTANCE OF JDBC ROWSET
 *           JdbcRowset rowset = new JdbcRowSet();
 *           rowset.setUrl("<database path>");
 *           rowset.setUsername("user");
 *           rowset.setPassword("pass");
 *           rowset.setCommand("SELECT * FROM shipment_data;");
 *
 *      // DATA NAVIGATOR CALLS THE EXECUTE AND NEXT FUNCTIONS ON THE ROWSET.
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
 *      // THIS BASICALLY SPECIFIES THE COLUMN AND THE ROWSET WHERE UPDATES HAVE
 *      // TO BE MADE.
 *           combo.bind(rowset,"part_id");
 *           combo.execute();
 *
 *     // CREATE A TEXTFIELD
 *         JTextField myText = new JTextField();
 *      	myText.setDocument(new SSTextDocument(rowset, "quantity");
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
 *  	  getContentPane().add(myText);
 *</pre><p>
 * @author	$Author$
 * @version	$Revision$
 */
public class SSDBComboBox extends JComponent {


    // TEXT FIELD THAT IS USED AS AN INTERMEDIATERY STORAGE POINT BETWEEN THE DATABASE
    // AND THE COMBO.
    private JTextField textField = null;

    // COMBOBOX USED TO DISPLAY THE VALUES.
    private JComboBox  cmbDisplayed  = new JComboBox();

    // DATABASE CONNECTION OBJECT
    private transient Connection conn = null;

    // QUERY USED TO RETRIEVE ALL POSSIBLE VALUES.
    private String query = null;

    // THE COLUMN NAME  WHOSE VALUE HAS TO BE WRITTEN BACK
    // TO THE DATABASE WHEN USER CHOOSES AN ITEM IN THE COMBO. THIS IS GENERALLY THE
    // COLUMN IN THE FOREIGN TABLE TO WHICH THE FOREIGN KEY MAPS TO.
    private String columnName = null;

    // THE COLUMN NAME WHOSE VALUES HAVE TO BE DISPLAYED IN THE COMBO.
    private String displayColumnName = null;

    // AN ADDITIONAL COLUMN (IF DESIRED) WHOSE VALUES WILL ALSO BE DISPLAYED IN THE COMBO
    private String secondDisplayColumnName = null;

    private Vector columnVector = new Vector();

    // NUMBER OF ITEMS IN THE COMBO BOX.
    private int numberOfItems = 0;

    // ROWSET USED TO RETRIEVE THE INFO FROM THE DATABASE.
    private transient RowSet rowset = null;

    // INSTANCE OF THE LISTENER FOR THE COMBO BOX.
    final MyComboListener cmbListener = new MyComboListener();

    // INSTANCE OF THE LISTENER FOR THE TEXT FIELD.
    final MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();
    
    final MyKeyListener  myKeyListener = new MyKeyListener();
    
    // SEPERATOR TO BE USED IF TWO COLUMN VALUES ARE DISPLAYED
    String seperator = " - ";

    String datePattern = "MM/dd/yyyy";
    
    /**
     *	Creates an object of the SSDBComboBox.
     */
    public SSDBComboBox() {
        super();
        addComponent();
    }

    /**
     * Constructs a SSDBComboBox  with the given parameters.
     *
     * @param _conn    database connection to be used.
     * @param _query   query to be used to retrieve the values from the database.
     * @param _columnName  column name whose value has to be stored.
     * @param _displayColumnName   column name whose values are displayed in the combo box.
     * @param _textField   a text field to which the combo box has to be synchronized
     *
     * @deprecated
     */
    public SSDBComboBox(Connection _conn, String _query, String _columnName, String _displayColumnName, JTextField _textField) {

        super();
        
        conn 				= _conn;
        query 				= _query;
        columnName 			= _columnName;
        displayColumnName 	= _displayColumnName;
        textField  			= _textField;
        textField.setPreferredSize(new Dimension(200,20));
        textField.setMaximumSize(new Dimension(200,20));
        
        addComponent();

    }

    /**
     * Constructs a SSDBComboBox  with the given parameters.
     *
     * @param _conn database connection to be used.
     * @param _query query to be used to retrieve the values from the database.
     * @param _columnName column name whose value has to be stored.
     * @param _displayColumnName column name whose values are displayed in the combo box.
     */
    public SSDBComboBox(Connection _conn, String _query, String _columnName, String _displayColumnName) {

        super();

        conn 				= _conn;
        query 				= _query;
        columnName 			= _columnName;
        displayColumnName 	= _displayColumnName;
        textField			= new JTextField();
        textField.setPreferredSize(new Dimension(200,20));
        cmbDisplayed.setPreferredSize(new Dimension(200,20));
        textField.setMaximumSize(new Dimension(200,20));
        cmbDisplayed.setMaximumSize(new Dimension(200,20));

    }

    /**
     * Sets the new rowset for the combo box.
     *
     * @param _rowset  rowset to which the combo has to update values.
     */
    public void setRowSet(RowSet _rowset) {
        rowset = _rowset;
    }

    /**
     * Sets the connection object to be used.
     *
     * @param _conn    connection object used for database.
     */
    public void setConnection(Connection _conn) {
        conn = _conn;
    }

    /**
     * Sets the query used to display items in the combo box.
     *
     * @param _query   query to be used to get values from database( to display combo box items)
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
    }

    /**
     * Sets the column name whose values have to be displayed in combo box.
     *
     * @param _displayColumnName   column name whose values have to be displayed.
     */
    public void setDisplayColumnName(String _displayColumnName) {
        displayColumnName = _displayColumnName;
    }
    
    /**
     * When a display column is of type date you can choose the format in which it has
     * to be displayed. For the pattern refer SimpleDateFormat in java.text package.
     *
     * @param _pattern pattern in which date has to be displayed.
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
     * @param _secondDisplayColumnName    column name whose values have to be
     *  displayed in the combo in addition to the first column name.
     */
    public void setSecondDisplayColumnName(String _secondDisplayColumnName) {
        secondDisplayColumnName = _secondDisplayColumnName;
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
     * Sets preferred dimensions for combo box.
     *
     * @param _dimension    dimensions for combo box
     */
    public void setPreferredSize(Dimension _dimension) {
        cmbDisplayed.setPreferredSize(_dimension);
    }

    /**
     * Returns connection object used to get values from database.
     *
     * @return returns a Connection object.
     */
    public Connection getConnection() {
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
     * Returns the name of column from which values for items in combo are taken from.
     *
     * @return returns column name representing the values for combo box items.
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Returns the column name whose values are displayed in the combo box.
     *
     * @return returns the name of the column used to get values for combo box items.
     */
    public String getDisplayColumnName() {
        return displayColumnName;
    }

    /**
     * Returns the second column name whose values are also displayed in the combo box.
     *
     * @return returns the name of the column used to get values for combo box items.
     *  returns NULL if the second display column is not provided.
     */
    public String getSecondDisplayColumnName() {
        return secondDisplayColumnName;
    }

    /**
     * Returns the text field used to synchronize with the rowset.
     *
     * @return returns the text field used to synchronize with the rowset.
     */
    public JTextField getTextField() {
        return textField;
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
        
        int index = cmbDisplayed.getSelectedIndex();
        
        if (index == -1) {
            return -1;
        }
        
        return ((Long)columnVector.get(index)).longValue();
    
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
     * Executes the query and adds items to the combo box based on the values
     * retrieved from the database.
     */
    public void execute() throws SQLException, Exception {

        this.removeListeners();

        Statement statement = conn.createStatement();

        if (query.equals("")) {
            throw new Exception("Query is empty");
        }

        ResultSet rs = statement.executeQuery(query);

        // CLEAR ALL ITEMS FROM COMBO AND VECTOR STORING ITS CORRESPONDING VALUES.
            cmbDisplayed.removeAllItems();
            columnVector.clear();

        // LOOP THROUGH VALUES IN RESULTSET AND ADD TO COMBO BOX
            int i = 0;
            while (rs.next()) {
                // IF TWO COLUMNS HAVE TO BE DISPLAYED IN THE COMBO THEY SEPERATED BY SEMI-COLON
                if ( secondDisplayColumnName != null) {
                    cmbDisplayed.addItem(getStringValue(rs,displayColumnName) + seperator + rs.getString(secondDisplayColumnName));
                } else {
                    cmbDisplayed.addItem(getStringValue(rs,displayColumnName));
                }
                // ADD THE ID OF THE ITEM TO A VECTOR.
                columnVector.add(i,new Long(rs.getLong(columnName)));
                i++;
            }

        // STORE THE NUMBER OF ITEMS IN THE COMBO BOX.
            numberOfItems = i;

        // DISPLAYS THE ITEM CORRESPONDING TO THE PRESENT VALUE IN THE DATABASE BASE.
            this.setCmbDisplay();
            this.addListeners();

    } // end public void execute() throws SQLException, Exception {

    // SETS THE COMBOBOX ITEM TO THE ONE CORRESPONDING TO THE VALUE PRESENT AT
    // COLUMN TO WHICH COMBO IS BOUND.
    private void setCmbDisplay() {

        Document doc = textField.getDocument();
        try {
            // GET THE VALUE FROM TEXT FIELD
            String text = doc.getText(0,doc.getLength());
            if (text != null) {
                long valueInText = Long.parseLong(text);
                // GET THE INDEX WHERE THIS VALUE IS IN THE VECTOR.
                int indexCorrespondingToLong = columnVector.indexOf(new Long(valueInText));
                // SET THE SELECTED ITEM OF COMBO TO THE ITEM AT THE INDEX FOUND FROM
                // ABOVE STATEMENT
                if (indexCorrespondingToLong != cmbDisplayed.getSelectedIndex()) {
                    cmbDisplayed.setSelectedIndex(indexCorrespondingToLong);
                }
            }
        } catch(BadLocationException ble) {
            ble.printStackTrace();
        } catch(NullPointerException npe) {
        } catch(NumberFormatException nfe) {
        }

    }

    /**
     * Binds the comboBox to the specified column in the given rowset.
     *
     * @param _rowset   rowset to which updates have to be made.
     * @param -column   column name in the rowset to which these updates have to be made.
     */
    public void bind(RowSet _rs, String _column) {
        textField.setDocument(new SSTextDocument(_rs,_column));
        cmbDisplayed.addFocusListener(new FocusAdapter(){
            public void focusLost(FocusEvent fe){
                myKeyListener.resetSearchString();
            }
        });
    }

    // ADDS LISTENERS FOR TEXT FIELD AND COMBO BOX.
    private void addListeners() {
        cmbDisplayed.addActionListener(cmbListener);
        cmbDisplayed.addKeyListener(myKeyListener);
        textField.getDocument().addDocumentListener(textFieldDocumentListener);
    }

    // REMOVES THE LISTENERS FOR THE COMBOBOX AND TEXT FIELD
    private void removeListeners() {
        cmbDisplayed.removeActionListener(cmbListener);
        cmbDisplayed.removeKeyListener(myKeyListener);
        textField.getDocument().removeDocumentListener(textFieldDocumentListener);
    }

    // LISTENER FOR THE TEXT FIELD
    private class MyTextFieldDocumentListener implements DocumentListener {

        public void changedUpdate(DocumentEvent de) {
            //System.out.println("changed Document Changed: " + de);

            cmbDisplayed.removeActionListener(cmbListener);

            Document doc = textField.getDocument();
            try {
                String text = doc.getText(0,doc.getLength());
                if (text != null) {
                    long valueInText = Long.parseLong(text);
                    int indexCorrespondingToLong = columnVector.indexOf(new Long(valueInText));
                    if (indexCorrespondingToLong != cmbDisplayed.getSelectedIndex()) {
                        cmbDisplayed.setSelectedIndex(indexCorrespondingToLong);
                    }
                }

            } catch(BadLocationException ble) {
                ble.printStackTrace();
            } catch(NullPointerException npe) {
            } catch(NumberFormatException nfe) {
            }

            cmbDisplayed.addActionListener(cmbListener);

        }

        public void insertUpdate(DocumentEvent de) {
            //System.out.println("insert Document Changed: " + de);
            cmbDisplayed.removeActionListener(cmbListener);

            Document doc = textField.getDocument();
            try {

                String text = doc.getText(0,doc.getLength());
                if (text != null) {
                    long valueInText = Long.parseLong(text);
                    int indexCorrespondingToLong = columnVector.indexOf(new Long(valueInText));
                    if (indexCorrespondingToLong != cmbDisplayed.getSelectedIndex()) {
                        cmbDisplayed.setSelectedIndex(indexCorrespondingToLong);
                    }
                }

            } catch(BadLocationException ble) {
                ble.printStackTrace();
            } catch(NullPointerException npe) {
            } catch(NumberFormatException nfe) {
            }

            cmbDisplayed.addActionListener(cmbListener);
        }

        public void removeUpdate(DocumentEvent de) {

            //System.out.println("remove Document Changed: " + de);
            cmbDisplayed.removeActionListener(cmbListener);

            Document doc = textField.getDocument();
            try {

                String text = doc.getText(0,doc.getLength());
                if (text != null) {
                    long valueInText = Long.parseLong(text);
                    int indexCorrespondingToLong = columnVector.indexOf(new Long(valueInText));
                    if (indexCorrespondingToLong != cmbDisplayed.getSelectedIndex()) {
                        cmbDisplayed.setSelectedIndex(indexCorrespondingToLong);
                    }
                }

            } catch(BadLocationException ble) {
                ble.printStackTrace();
            } catch(NullPointerException npe) {
            } catch(NumberFormatException nfe) {
            }

            cmbDisplayed.addActionListener(cmbListener);
        }

    } // end private class MyTextFieldDocumentListener implements DocumentListener {
    
    // KEYSTROKE LISTENER
    private class MyKeyListener extends KeyAdapter {
        
        String searchString = null;
        Stack searchStack = new Stack();
        int previousIndex = 0;
        
        public void resetSearchString() {
            searchString = null;
        }
        
        public void keyReleased(KeyEvent ke) {
            int i;
            if (ke.getKeyCode() == KeyEvent.VK_ESCAPE		 ||
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
//				System.out.println("Initial String is " + searchString);	
//				System.out.println("ppr" + new String(new char[]{ke.getKeyChar()}) + "ppr");
            if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if (searchString == null ) {
                    cmbDisplayed.setSelectedIndex(0);
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
            
//				System.out.println("Search String is " + searchString);					
            
            if (searchString == null) {
                cmbDisplayed.setSelectedIndex(0);
                return;
            }
            
            if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                
                if (searchStack.empty()) {
                    cmbDisplayed.setSelectedIndex(0);
                    return;
                } else {
                    // PRESENT POSITION IN COMBO
                    searchStack.pop();
                    // PREVIOUS POSITION IN COMBO
                    if(!searchStack.empty()) {
                        previousIndex = ((Integer)searchStack.peek()).intValue();
                        cmbDisplayed.setSelectedIndex(previousIndex);
                        return;
                    } else {
                        previousIndex = 0;
                        cmbDisplayed.setSelectedIndex(0);
                        return;
                    }
                }
                
            } else {
                
                for (i=previousIndex;i<cmbDisplayed.getItemCount();i++) {
                    if (searchString.length() == 0) {
                        cmbDisplayed.setSelectedIndex(0);
                    } else {
                        if (((String)cmbDisplayed.getItemAt(i)).length() >= searchString.length()) {
                            if (searchString.equalsIgnoreCase( ((String)cmbDisplayed.getItemAt(i)).substring(0,searchString.length())) ) {
    //							System.out.println("Found Match at index " + i);
                                cmbDisplayed.setSelectedIndex(i);
                                searchStack.push(new Integer(i));
                                return;
                            }
                        }
                    }
                    
                }
                if (i == cmbDisplayed.getItemCount()) {
                    for (i=0;i<previousIndex;i++) {
                        if (searchString.length() == 0) {
                            cmbDisplayed.setSelectedIndex(0);
                        } else {
                            if (((String)cmbDisplayed.getItemAt(i)).length() >= searchString.length()) {
                                if (searchString.equalsIgnoreCase( ((String)cmbDisplayed.getItemAt(i)).substring(0,searchString.length())) ){
                                    cmbDisplayed.setSelectedIndex(i);
                                    searchStack.push(new Integer(i));
                                    return;
                                }
                            }
                        }
                    }	
                }
            }
            
            if (searchStack.empty()) {
                cmbDisplayed.setSelectedIndex(0);
            } else {	
                cmbDisplayed.setSelectedIndex(((Integer)searchStack.peek()).intValue());
            }
                        
        }
    } // end private class MyKeyListener extends KeyAdapter {

    // LISTENER FOR THE COMBO BOX.
    private class MyComboListener implements ActionListener {

        public void actionPerformed(ActionEvent ae) {

            textField.getDocument().removeDocumentListener(textFieldDocumentListener);

            // GET THE INDEX CORRESPONDING TO THE SELECTED TEXT IN COMBO
            int index = cmbDisplayed.getSelectedIndex();

            // IF THE USER WANTS TO REMOVE COMPLETELY THE VALUE IN THE FIELD HE CHOOSES
            // THE EMPTY STRING IN COMBO THEN THE TEXT FIELD IS SET TO EMPTY STRING
            if ( index != -1 ) {
                try {
                    // NOW LOOK UP THE VECTOR AND GET THE VALUE CORRESPONDING TO THE TEXT SELECTED IN THE COMBO
                    long valueCorresponingToIndex = ( (Long)columnVector.get(index) ).longValue() ;
//						System.out.println("Value Corresponding To CMB: " + valueCorresponingToIndex);
                    //GET THE TEXT IN THE TEXT FIELD
                    String strValueinTextField = textField.getText();
                    //INITIALIZE THE  LONG VALUE IN TEXT TO -1
                    long valueInText = -1;
                    // IF THE TEXT IS NOT NULL PARSE ITS LONG VALUE
                    if (!strValueinTextField.equals("")) {
                        valueInText = Long.parseLong(strValueinTextField);
                    }


                    // IF THE LONG VALUE CORRESPONDING TO THE SELECTED TEXT OF COMBO NOT EQUAL
                    // TO THAT IN THE TEXT FIELD THEN CHANGE THE TEXT IN THE TEXT FIELD TO THAT VALUE
                    // IF ITS THE SAME LEAVE IT AS IS
                    if (valueInText != valueCorresponingToIndex) {
                        textField.setText( String.valueOf(valueCorresponingToIndex) );
                    }

                } catch(NullPointerException npe) {
                } catch(NumberFormatException nfe) {
                }
            }

            textField.getDocument().addDocumentListener(textFieldDocumentListener);

        }
    } // private class MyComboListener implements ActionListener {
    
    // ADD THE COMBO BOX TO THE JCOMPONENT
    private void addComponent() {
        //SET THE BOX LAYOUT
            setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
        // SET PREFERRED SIZE FOR COMBO BOX	
            cmbDisplayed.setPreferredSize(new Dimension(150,20));
        // ADD THE COMBO BOX TO THE JCOMPONENT	
            add(cmbDisplayed);
    }
    
    /**
     * Returns the combobox so that it can be added to a container.
     *
     * @return a JComboBox which displays the required items.
     */
    public JComboBox getComboBox() {
        return cmbDisplayed;
    }
    
    /**
     * Returns the combo box to be displayed on the screen.
     *
     * @return returns the combo box that displays the items.
     */
    public Component getComponent() {
        return cmbDisplayed;
    }
    
    /**
     * Adds an item to the existing list of items in the combo box.
     *
     * @param _name   name that should be displayed in the combo
     * @param _value   value corresponding the the name
     */
     public void addItem(String _name, long _value) {
        columnVector.add(new Long(_value));
        cmbDisplayed.addItem(_name);
     }
     
     /**
      *	Deletes the item which has name equal to _name. If there are
      * more than one item with the same name then the first occurance is deleted.
      *
      * @param _value  value of the item to be deleted.
      *
      * @return returns true on successful deletion else returns false.
      */
     public boolean deleteItem(String _name) {
        for (int i=0; i<cmbDisplayed.getItemCount();i++) {
            if ( ((String)cmbDisplayed.getItemAt(i)).equals(_name) ) {
                cmbDisplayed.removeItemAt(i);
                columnVector.removeElementAt(i);	
                return true;
            }
        }
        return false;
     }
    
     /**
      *	Deletes the item which has value equal to _value. If there are
      * more than one item with the same value then the first occurance is deleted.
      *
      * @param _value  value of the item to be deleted.
      *
      * @return returns true on successful deletion else returns false.
      */
     public boolean deleteItem(long _value) {
        int index = columnVector.indexOf(new Long(_value));
        if (index == -1) {
            return false;
        }
        columnVector.removeElementAt(index);
        cmbDisplayed.removeItemAt(index);
        return true;
     }	
     
     /**
      *	Deletes the item which has display name equal to _name and corresponding value
      * as _value. If there is more than one item with same name and value then the first
      * occurance is deleted.
      *
      * @param _name   name of item to be deleted
      * @param _value  value of the item to be deleted.
      *
      * @return returns true on successful deletion else returns false.
      */
     public boolean deleteItem(String _name, long _value) {
        for (int i=0; i<cmbDisplayed.getItemCount();i++) {
            if ( ((String)cmbDisplayed.getItemAt(i)).equals(_name) ) {
                if (((Long)(columnVector.elementAt(i))).longValue() == _value) {
                    cmbDisplayed.removeItemAt(i);
                    columnVector.removeElementAt(i);	
                    return true;
                }
            }
        }
        return false;
     }
     
    /**
     * Updates the string thats being displayed.
     * If more than one item is present in the combo for that value the first one is changed.
     *
     * NOTE: To retain changes made to current rowset call updateRow before calling the 
     * updateItem on SSDBComboBox. (Only if you are using the SSDBComboBox and SSDataNavigator
     * for navigation in the screen. If you are not using the SSDBComboBox for navigation
     * then no need to call updateRow on the rowset. Also if you are using only SSDBComboBox
     * for navigation you need not call the updateRow.)      
     *
     * @param _value  the value corresponding to the item in combo to be updated.
     * @param _name   the new name that replace old one.
     *
     * @return returns true if successful else false.
     */
    public boolean updateItem(long _value, String _name) {
        int index = columnVector.indexOf(new Long(_value));
        if (index == -1) {
            return false;
        }
        cmbDisplayed.removeActionListener(cmbListener);	
        cmbDisplayed.insertItemAt(_name,index+1);
        cmbDisplayed.removeItemAt(index);
        cmbDisplayed.setSelectedIndex(index);
        cmbDisplayed.addActionListener(cmbListener);
        return true;
    }
      
    // RETURN STRING EQUILIVENT OF DATA IN A COLUMN
    private String getStringValue(ResultSet _rs, String _columnName) {
        String strValue = "";
        try {
            int type = _rs.getMetaData().getColumnType(_rs.findColumn(_columnName));
            switch(type){
                case Types.DATE:
                    //Calendar calendar = Calendar.getInstance();
                    //calendar.setTime(_rs.getDate(_columnName));
                    SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
                    //dateFormat.setCalendar(calendar);
                    //strValue = dateFormat.toLocalizedPattern();
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

} // end public class SSDBComboBox extends JComponent {



/*
 * $Log$
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