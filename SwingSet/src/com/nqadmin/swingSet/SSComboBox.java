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
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.beans.*;
import javax.sql.*;
import com.nqadmin.swingSet.datasources.SSRowSet;

/**
 * SSComboBox.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Provides a way of displaying text corresponding to codes that are stored in
 * the database. By default the codes start from zero. If you want to provide a
 * different mapping for the items in the combobox then a string of integers
 * containing the corresponding numeric values for each choice must be provided.
 *
 * Note that if you DO NOT want to use the default mappings, the custom
 * mappings must be set before calling the bind() method to bind the
 * combobox to a database column.
 *
 * Also, if changing both a rowset and column name consider using the bind()
 * method rather than individual setSSRowSet() and setColumName() calls. 
 *
 * e.g.
 *      SSComboBox combo = new SSComboBox();
 *      String[] options = {"111", "2222", "33333"};
 *      combo.setOption(options);
 *
 *      For the above items the combobox assumes that the values start from zero:
 *           "111" -> 0, "2222" -> 1, "33333" -> 2
 *
 *      To give your own mappings  you can set the mappings separately or pass
 *      them along with the options:
 *
 *      SSComboBox combo = new SSComboBox();
 *      String[] options = {"111", "2222", "33333"};
 *      int[] mappings = { 1,5,7 };
 *      combo.setOption(options, mappings);
 *
 *      // next line is assuming myrowset has been initialized and my_column is a
 *      // column in myrowset
 *      combo.bind(myrowset,"my_column");
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
//public class SSComboBox extends JComponent {
public class SSComboBox extends JComboBox {

    // THIS VALUE WILL BE RETURNED WHEN NO ITEM IS SELECTED IN THE COMBO BOX
    public static final int NON_SELECTED = (int)((Math.pow(2, 32) -1)/(-2));

    // TEXT FIELD THAT WILL BE BOUND TO THE DATABASE
    protected JTextField textField = new JTextField();

    // INSTANCE OF LISTENER FOR COMBO BOX
    private MyComboListener cmbListener = new MyComboListener();

    // INSTANCE  OF LISTENER FOR THE TEXT FIELD BOUND TO DATABASE
    private MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();

    // PREDEFINED SET OF OPTIONS/CHOICES TO BE DISPLAYED IN A COMBO BOX
    protected int option = 0;

    // MAPPINGS FOR THE COMBO BOX ITEMS IF DIFFERENT FROM DEFAULTS (0,1,2,..)
    protected int[] mappingValues = null;

    // SSROWSET FROM WHICH THE COMBO WILL GET/SET VALUES
    protected SSRowSet rowset;

    // COLUMN NAME TO WHICH THE COMBO WILL BE BOUND TO
    protected String columnName;

    /**
     *  Type used for combo box.
     */
    public static final int YES_NO_OPTION = 0;
    public static final int YES = 1;
    public static final int NO  = 0;

    /**
     *  Type used for combo box.
     */
    public static final int SEX_OPTION = 1;
    public static final int GENDER_OPTION = 1;
    public static final int MALE = 0;
    public static final int FEMALE = 1;
    public static final int UNI_SEX = 2;

    /**
     *  Type used for combo box.
     */
    public static final int INCLUDE_EXCLUDE_OPTION  = 2;
    public static final int EXCLUDE = 0;
    public static final int INCLUDE = 1;

    /**
     *  Creates an object of SSComboBox.
     */
    public SSComboBox() {
        init();
    }

    /**
     * Initialization code.
     */
    protected void init() {
        // ADD KEY LISTENER TO TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER
        // KEY IS PRESSED.
            addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                        ((Component)ke.getSource()).transferFocus();
                    }
                }
            });
            
        // SET PREFERRED DIMENSIONS
            setPreferredSize(new Dimension(200,20));
    }

    /**
     * Returns the index of the selected item in the combo box.
     *
     * @return index of selected item. -1 if none selected.
     */
    //public int getSelectedIndex() {
    //    return cmbDisplayed.getSelectedIndex();
    //}

    /**
     * Returns the value associated with the selected item.
     *
     * @return returns the value associated with the item selected. -1 if none selected.
     */
    public int getSelectedValue() {
        //if (cmbDisplayed.getSelectedIndex() == -1) {
        if (getSelectedIndex() == -1) {
            return NON_SELECTED;
        }

        if (mappingValues != null) {
            //return mappingValues[cmbDisplayed.getSelectedIndex()];
            return mappingValues[getSelectedIndex()];
        }
        //return cmbDisplayed.getSelectedIndex();
        return getSelectedIndex();

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
     * Sets the column name to which the combo box has to be bound
     *
     * @param _columnName    column name in the SSRowSet to which the combo box
     *    is bound.
     */
    public void setColumnName(String _columnName) {
        columnName = _columnName;
        bind();
    }

    /**
     * Sets the SSRowSet to be used.
     *
     * @param _rowset    SSRowSet to be used for getting the values.
     */
    public void setSSRowSet(SSRowSet _rowset) {
        rowset = _rowset;
        bind();
    }

    /**
     * Sets the value.
     *
     * @param _value    value to assign to combo box
     */
    public void setSelectedValue(int _value) {
        textField.setText(String.valueOf(_value));
    }

    /**
     * Sets preferred dimensions for combo box
     *
     * @param _dimension    preferred dimensions for combo box
     */
    //public void setPreferredSize(Dimension _dimension) {
    //    cmbDisplayed.setPreferredSize(_dimension);
    //}

    /**
     * Added the combo box to the JComponent
     */
    //private void addComponent() {
    //    // SET THE BOX LAYOUT
    //        setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
    //    // SET PREFERRED SIZE FOR COMBO BOX
    //        cmbDisplayed.setPreferredSize(new Dimension(150,20));
    //    // ADD THE COMBO BOX TO THE JCOMPONENT
    //        add(cmbDisplayed);
    //}

    /**
     * The column name and the SSRowSet should be set before calling this function.
     * If the column name and SSRowSet are set seperately then this function has to
     * be called to bind the combo box to the column in the SSRowSet.
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
            setDisplay();

        // ADD BACK LISTENERS
            addListeners();
               
    }

    /**
     * Binds the combo box to the specified column of the SSRowSet.
     * As the SSRowSet changes the combo box item displayed changes accordingly.
     *
     * @param _rowset    SSRowSet to be used for getting the value.
     * @param _columnName    Column to which the combo box has to be bound.
     */
    public void bind(SSRowSet _rowset, String _columnName) {
        rowset = _rowset;
        columnName = _columnName;
        bind();
    }

    // SET THE COMBO BOX ITEM TO THE ITEM THAT CORRESPONDS TO THE VALUE IN TEXT FIELD
    protected void setDisplay(){
        try {
            String text = textField.getText().trim();
            // GET THE INTEGER EQUIVALENT OF THE TEXT IN THE TEXT FIELD
            int intValue = 0;
            if ( !(text.trim().equals("")) ) {
                intValue = Integer.parseInt(text);
            }
            // CHECK IF THE VALUE DISPLAYED IS THE SAME AS THAT IN TEXT FIELD
            // TWO CASES 1. THE MAPPINGS FOR THE STRINGS DISPLAYED ARE GIVEN BY USER
            //  2. VALUES FOR THE ITEMS IN COMBO START FROM ZERO
            // IN CASE ONE: YOU CAN JUST CHECK FOR EQUALITY OF THE SELECTEDINDEX AND VALUE IN TEXT FIELD
            // IN CASE TWO: YOU HAVE TO CHECK IF THE VALUE IN THE MAPPINGVALUES ARRAY AT INDEX EQUAL
            // TO THE SELECTED INDEX OF THE COMBO BOX EQUALS THE VALUE IN TEXT FIELD
            // IF THESE CONDITIONS ARE MET YOU NEED NOT CHANGE COMBO BOX SELECTED ITEM
            if ( (mappingValues==null && intValue != getSelectedIndex()) ||
                 (mappingValues!=null && getSelectedIndex() == -1)       ||
                 (mappingValues!=null && mappingValues[getSelectedIndex()] != intValue) ) {

                if (mappingValues==null && (intValue <0 || intValue >= getItemCount() )) {
                // IF EXPLICIT VALUES FOR THE ITEMS IN COMBO ARE NOT SPECIFIED THEN CODES START
                // FROM ZERO. IN SUCH A CASE CHECK IF THE NUMBER EXCEEDS THE NUMBER OF ITEMS
                // IN COMBO BOX (THIS IS ERROR CONDITION SO NOTIFY USER)
                    System.out.println("Error: value from DB:" + intValue + "  items in combo box: " + getItemCount());
                    setSelectedIndex(-1);
                } else {
                // IF MAPPINGS  ARE SPECIFIED THEN GET THE INDEX AT WHICH THE VALUE IN TEXT FIELD
                // APPEARS IN THE MAPPINGVALUES ARRAY. SET THE SELECTED ITEM OF COMBO SO THAT INDEX
                    if (mappingValues!=null) {
                        int i=0;
                        for (;i<mappingValues.length;i++) {
                            if (mappingValues[i] == intValue) {
                                setSelectedIndex(i);
                                break;
                            }
                        }
                        // IF THAT VALUE IS NOT FOUND IN THE GIVEN MAPPING VALUES PRINT AN ERROR MESSAGE
                        if (i==mappingValues.length) {
                            System.out.println("change ERROR: could not find a corresponding item in combo for value " + intValue);
                            setSelectedIndex(-1);
                        }
                    } else {
                    // IF MAPPINGS ARE NOT SPECIFIED SET THE SELECTED ITEM AS THE ITEM AT INDEX
                    // EQUAL TO THE VALUE IN TEXT FIELD
                        setSelectedIndex(intValue);
                    }
                }
            }

        } catch(NumberFormatException nfe) {
            nfe.printStackTrace();
        }

    }
    
    // ADDS LISTENERS FOR THE COMBO BOX AND TEXT FIELD
    private void addListeners() {
        textField.getDocument().addDocumentListener(textFieldDocumentListener);
        addActionListener(cmbListener);
    }

    // REMOVES THE LISTENERS FOR TEXT FIELD AND THE COMBO BOX DISPLAYED
    private void removeListeners() {
        textField.getDocument().removeDocumentListener(textFieldDocumentListener);
        removeActionListener(cmbListener);
    }

    /**
     * Adds the given array of strings as combo box items.
     *
     * @param _options    the list of options that you want to appear in the combo box.
     */
    public boolean setOption(String[] _options){
        // ADD THE SPECIFIED ITEMS TO THE COMBO BOX
        // REMOVE ANY OLD ITEMS SO THAT MULTIPLE CALLS TO THIS FUNCTION DOES NOT AFFECT
        // THE DISPLAYED ITEMS
        if (getItemCount() != 0) {
            removeAllItems();
        }
        for (int i=0;i<_options.length;i++) {
            addItem(_options[i]);
        }

        return true;
    }

    /**
     * Sets the values for each of the items in the combo. (Values that map to the
     * items in the combo box)
     *
     * @param _mappings    an array of values that correspond to those in the combo box.
     */
    public void setMappingValues(int[] _mappings) {
        // INITIALIZE THE ARRAY AND COPY THE MAPPING VALUES
        mappingValues = new int[_mappings.length];
        for (int i=0;i<_mappings.length;i++) {
            mappingValues[i] = _mappings[i];
        }
    }

    /**
     * Sets the options to be displayed in the combo box and their corresponding values.
     *
     *@param _options    options to be displayed in the combo box.
     *@param _mappings    integer values that correspond to the options in the combo box.
     *
     *@return returns true if the options and mappings are set successfully.
     *    returns false if the size of arrays do not match or if the values could
     *    not be set.
     */
    public boolean setOption(String[] _options, int[]_mappings) {
        if (_options.length != _mappings.length) {
            return false;
        }
        
        // REMOVE ANY OLD ITEMS SO THAT MULTIPLE CALLS TO THIS FUNCTION DOES NOT AFFECT
        // THE DISPLAYED ITEMS
        if (getItemCount() != 0) {
            removeAllItems();
        }
        // ADD THE ITEMS TO THE COMBOBOX
        for (int i=0;i<_options.length;i++) {
            addItem(_options[i]);
        }
        // COPY THE MAPPING VALUES
        mappingValues = new int[_mappings.length];
        for (int i=0;i<_mappings.length;i++) {
            mappingValues[i] = _mappings[i];
        }

        return true;
    }

    /**
     * Sets the options to be displayed in the combo box and their corresponding values.
     *
     * @param _options    predefined options to be displayed in the combo box.
     * @param _mappings    integer values that correspond to the options in the combo box.
     */
    public void setOption(int _options, int[]_mappings) {
        // COPY THE MAPPING VALUES
        mappingValues = new int[_mappings.length];
        for (int i=0;i<_mappings.length;i++) {
            mappingValues[i] = _mappings[i];
        }
        // SET THE OPTIONS IN THE COMBO BOX
        setOption(_options);
    }

    /**
     * Sets the option to be displayed in the combo box.
     *
     * @param _option predefined options to be displayed in the combo box.
     */
    public boolean setOption(int _option) {

        option = _option;

        if (getItemCount() != 0) {
            removeAllItems();
        }
        if (option == YES_NO_OPTION) {
            addItem(new String("NO"));
            addItem(new String("YES"));
        } else if (option == SEX_OPTION || option == GENDER_OPTION) {
            addItem(new String("MALE"));
            addItem(new String("FEMALE"));
            addItem(new String("UNI_SEX"));
        } else if (option == INCLUDE_EXCLUDE_OPTION) {
            addItem(new String("INCLUDE"));
            addItem(new String("EXCLUDE"));
        } else {
            return false;
        }

        return true;
    }

    // LISTENER FOR THE TEXT FIELD THAT CONTAINS THE INTEGER VALUE
    private class MyTextFieldDocumentListener implements DocumentListener, Serializable {

        public void changedUpdate(DocumentEvent de) {
            removeActionListener(cmbListener);            
            setDisplay();            
            addActionListener(cmbListener);
        } // end public void changedUpdate(DocumentEvent de) 

        public void insertUpdate(DocumentEvent de) {
            removeActionListener(cmbListener);
            setDisplay();
            addActionListener(cmbListener);
        } // end public void insertUpdate(DocumentEvent de) 

        public void removeUpdate(DocumentEvent de) {

        /* Nothing to do here....*/

        }
    }

    // LISTENER FOR THE COMBO BOX. CHANGES MADE IN THE COMBO BOX ARE PASSED ON TO THE
    // TEXT FIELD THEY BY MOVING THE CHANGE TO UNDERLYING STRUCTURE (DATABASE).
    private class MyComboListener implements ActionListener, Serializable {

        public void actionPerformed(ActionEvent ae) {
            textField.getDocument().removeDocumentListener(textFieldDocumentListener);
            int index = getSelectedIndex();
            try {
                if (index == -1) {
                    textField.setText("");
                } else {
                    String strValueInText = textField.getText();
                    int valueOfText = -1;
                    strValueInText = strValueInText.trim();
                    if ( !strValueInText.equals("") ) {
                        valueOfText = Integer.parseInt(strValueInText);
                    }

                    if ( mappingValues == null && valueOfText != index ) {
                        textField.setText( String.valueOf(index) );
                    }
                    else if(mappingValues != null && mappingValues.length > index && valueOfText != mappingValues[index]){
                        textField.setText(String.valueOf(mappingValues[index]));
                    }
                }
            } catch(NullPointerException npe) {
                npe.printStackTrace();
            } catch(NumberFormatException nfe) {
                nfe.printStackTrace();
            }
            textField.getDocument().addDocumentListener(textFieldDocumentListener);
        }
    }



// DEPRECATED STUFF....................
    /**
     * Creates an instance of SSComboBox and sets the text field with which the combo
     * box will be synchronized with.
     *
     * @deprecated
     */
    public SSComboBox(SSTextDocument document) {

        //super();
        init();
        //addComponent();

        this.setDocument(document);
    }

    /**
     * Sets the document to which the combo box will be bound to. Changes to this
     * will immediately reflect in the combo box.
     *
     * @param _document    text document to which the combo box has to be bound
     *
     * @deprecated
     * @see #bind
     */
    public void setDocument(SSTextDocument _document) {
            textField.setDocument(_document);
            setDisplay();
        // NEEDED, IF THIS FUNCTION IS CALLED MORE THAN ONCE.
        // SO THAT WE DON'T STACK UP LISTENERS AS THE NUMBER OF CALLS TO THIS
        // FUNCTION INCREASES
            removeListeners();
            addListeners();
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


} // end public class SSComboBox extends JComboBox {



/*
 * $Log$
 * Revision 1.25  2005/02/03 23:50:56  prasanth
 * 1. Removed commented out code.
 * 2. Modified setDisplay function to change value only when underlying value
 *      does not match with that displayed.
 * 3. Using setDisplay in document listener.
 *
 * Revision 1.24  2005/02/02 23:36:58  yoda2
 * Removed setMaximiumSize() calls.
 *
 * Revision 1.23  2005/01/19 20:54:43  yoda2
 * API cleanup.
 *
 * Revision 1.22  2005/01/19 03:15:44  yoda2
 * Got rid of setBinding and retooled public/private bind() methods and how they are called.
 *
 * Revision 1.21  2005/01/18 22:27:24  yoda2
 * Changed to extend JComboBox rather than JComponent.  Deprecated bind(), setSSRowSet(), & setColumnName().
 *
 * Revision 1.20  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.19  2004/11/01 15:53:30  yoda2
 * Fixed various JavaDoc errors.
 *
 * Revision 1.18  2004/10/25 23:09:01  prasanth
 * In setOption using the class variable rather than function argument.
 *
 * Revision 1.17  2004/10/25 22:03:18  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.16  2004/10/25 19:51:02  prasanth
 * Modified to use the new SSRowSet instead of  RowSet.
 *
 * Revision 1.15  2004/09/21 18:57:47  prasanth
 * Added Unisex as an option.
 *
 * Revision 1.14  2004/09/21 14:14:14  prasanth
 * Swapped the codes for FEMALE & MALE.
 *
 * Revision 1.13  2004/09/13 15:41:25  prasanth
 * Added a constant to indicate that there is no selection in combobox.
 * This value will be returned when  the selected index in combo box is -1.
 *
 * Revision 1.12  2004/09/02 16:20:17  prasanth
 * Added support for mapping values in setDisplay function.
 * Combo Listener was not handling mapping values  so added that.
 *
 * Revision 1.11  2004/08/12 23:51:16  prasanth
 * Updating the value to null if the selected index is -1.
 *
 * Revision 1.10  2004/08/10 22:06:59  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.9  2004/08/09 21:31:13  prasanth
 * Corrected wrong function call. removeAll() --> removeAllItems()
 *
 * Revision 1.8  2004/08/09 15:37:36  prasanth
 * 1. Removing elements in the combo before adding any new one.
 * 2. Added key listener to transfer focus on enter key.
 *
 * Revision 1.7  2004/08/02 14:41:10  prasanth
 * 1. Added set methods for rowset, columnname, selectedvalue.
 * 2. Added get methods for rowset, columname.
 * 3. Added addComponent and removeListener functions (private).
 *
 * Revision 1.6  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.5  2004/02/23 16:39:35  prasanth
 * Added GENDER_OPTION.
 *
 * Revision 1.4  2003/12/16 18:01:40  prasanth
 * Documented versions for release 0.6.0
 *
 * Revision 1.3  2003/10/31 16:04:44  prasanth
 * Added method getSelectedIndex() and getSelectedValue().
 *
 * Revision 1.2  2003/09/25 14:27:45  yoda2
 * Removed unused Import statements and added preformatting tags to JavaDoc descriptions.
 *
 * Revision 1.1.1.1  2003/09/25 13:56:43  yoda2
 * Initial CVS import for SwingSet.
 *
 */