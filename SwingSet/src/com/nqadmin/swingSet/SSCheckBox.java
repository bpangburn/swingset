/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2006, The Pangburn Company and Prasanth R. Pasala.
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

import java.awt.AWTKeyStroke;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nqadmin.swingSet.datasources.SSRowSet;

/**
 * SSCheckBox.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Used to display the boolean values stored in the database. The SSCheckBox
 * can be bound to a numeric or boolean database column.  Currently, binding to
 * a boolean column has been tested only with postgresql. If bound to a numeric
 * database column, a checked SSCheckBox returns a '1' to the database and
 * an uncheck SSCheckBox will returns a '0'.  In the future an option may be
 * added to allow the user to specify the values returned for the checked and
 * unchecked checkbox states.
 *
 * Note that for naming consistency, SSCheckBox replaced SSDBCheckBox 01-10-2005.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
public class SSCheckBox extends JCheckBox {

    /**
     * Text field bound to the SSRowSet.
     */
    protected JTextField textField = new JTextField();

    /**
     * SSRowSet from which component will get/set values.
     */
    protected SSRowSet sSRowSet;

    /**
     * SSRowSet column to which the component will be bound.
     */
    protected String columnName = "";

    /**
     * Column SQL data type.
     */
    protected int columnType = java.sql.Types.BIT;

    /**
     * Component listener.
     */
    private final MyCheckBoxListener checkBoxListener = new MyCheckBoxListener();
    
    /**
     * Bound text field document listener.
     */
    private final MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();

    /**
     * Checked value for numeric columns.
     */
    protected int CHECKED = 1;
    
    /**
     * Unchecked value for numeric columns.
     */
    protected int UNCHECKED = 0;

    /**
     * Checked value for Boolean columns.
     */
    protected static String BOOLEAN_CHECKED = "true";
    
    /**
     * Unchecked value for Boolean columns.
     */
    protected static String BOOLEAN_UNCHECKED = "false";

    /**
     * Creates an object of SSCheckBox.
     */
    public SSCheckBox() {
        init();
    }

    /**
     * Creates an object of SSCheckBox binding it so the specified column
     * in the given SSRowSet.
     *
     * @param _sSRowSet    datasource to be used.
     * @param _columnName    name of the column to which this check box should be bound
     */
    public SSCheckBox(SSRowSet _sSRowSet, String _columnName) throws java.sql.SQLException {
        sSRowSet = _sSRowSet;
        columnName = _columnName;
        init();
        bind();
    }
    
    /**
     * Sets the SSRowSet column name to which the component is bound.
     *
     * @param _columnName    column name in the SSRowSet to which the component
     *    is bound
     */
    public void setColumnName(String _columnName) throws java.sql.SQLException {
        String oldValue = columnName;
        columnName = _columnName;
        firePropertyChange("columnName", oldValue, columnName);
        bind();
    }    
    
    /**
     * Returns the SSRowSet column name to which the component is bound.
     *
     * @return column name to which the component is bound
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets the SSRowSet to which the component is bound.
     *
     * @param _sSRowSet    SSRowSet to which the component is bound
     */
    public void setSSRowSet(SSRowSet _sSRowSet) throws java.sql.SQLException {
        SSRowSet oldValue = sSRowSet;
        sSRowSet = _sSRowSet;
        firePropertyChange("sSRowSet", oldValue, sSRowSet);
        bind();
    }     
    
    /**
     * Returns the SSRowSet to which the component is bound.
     *
     * @return SSRowSet to which the component is bound
     */
    public SSRowSet getSSRowSet() {
        return sSRowSet;
    }

    /**
     * Sets the SSRowSet and column name to which the component is to be bound.
     *
     * @param _sSRowSet    datasource to be used.
     * @param _columnName    Name of the column to which this check box should be bound
     */
    public void bind(SSRowSet _sSRowSet, String _columnName) throws java.sql.SQLException {
        SSRowSet oldValue = sSRowSet;
        sSRowSet = _sSRowSet;
        //pChangeSupport.firePropertyChange("sSRowSet", oldValue, sSRowSet);
        firePropertyChange("sSRowSet", oldValue, sSRowSet);
        
        String oldValue2 = columnName;
        columnName = _columnName;
        //pChangeSupport.firePropertyChange("columnName", oldValue2, columnName);
        firePropertyChange("columnName", oldValue2, columnName);
        
        bind();
    }
    
    /**
     * Initialization code.
     */
    protected void init() {
    	
        // TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER KEY IS PRESSED
        Set<AWTKeyStroke> forwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK ));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,newForwardKeys);
            
        // SET PREFERRED AND MAXIMUM DIMENSIONS
            setPreferredSize(new Dimension(20,20));
            setMaximumSize(new Dimension(20,20));            
    }
    
    /**
     * Method for handling binding of component to a SSRowSet column.
     */
    protected void bind() throws java.sql.SQLException {
        
        // CHECK FOR NULL COLUMN/ROWSET
            if (columnName==null || columnName.trim().equals("") || sSRowSet==null) {
                return;
            }
            
        // REMOVE LISTENERS TO PREVENT DUPLICATION
            removeListeners();

        // DETERMINE COLUMN TYPE
			columnType = sSRowSet.getColumnType(columnName);            

        // BIND THE TEXT FIELD TO THE SPECIFIED COLUMN
            textField.setDocument(new SSTextDocument(sSRowSet, columnName));

        // SET THE COMBO BOX ITEM DISPLAYED
            updateDisplay();
            
        // ADD BACK LISTENERS
            addListeners();
               
    }
    
    /**
     * Adds listeners for component and bound text field (where applicable).
     */
    private void addListeners() {
        textField.getDocument().addDocumentListener(textFieldDocumentListener);
        //cmbDisplayed.addActionListener(cmbListener);
        addChangeListener(checkBoxListener);   
    }

    /**
     * Removes listeners for component and bound text field (where applicable).
     */
    private void removeListeners() {
        textField.getDocument().removeDocumentListener(textFieldDocumentListener);
        //cmbDisplayed.removeActionListener(cmbListener);
        removeChangeListener(checkBoxListener);
    }    


    /**
     * Updates the value displayed in the component based on the SSRowSet column
     * binding.
     */
    protected void updateDisplay() {
            
        // SELECT/DESELECT BASED ON UNDERLYING SQL TYPE
            switch(columnType) {
                case java.sql.Types.INTEGER:
                case java.sql.Types.SMALLINT:
                case java.sql.Types.TINYINT:
            // SET THE CHECK BOX BASED ON THE VALUE IN TEXT FIELD
                    if (textField.getText().equals(String.valueOf(CHECKED))) {
                        setSelected(true);
                    } else {
                        setSelected(false);
                    }
                    break;

                case java.sql.Types.BIT:
                case java.sql.Types.BOOLEAN:
            // SET THE CHECK BOX BASED ON THE VALUE IN TEXT FIELD
                    if (textField.getText().equals(BOOLEAN_CHECKED)) {
                        setSelected(true);
                    } else {
                        setSelected(false);
                    }
                    break;

                default:
                    break;
            }

    } // end protected void updateDisplay() {

    /**
     * Listener(s) for the bound text field used to propigate values back to the
     * component's value.
     */
    private class MyTextFieldDocumentListener implements DocumentListener, Serializable {

        public void changedUpdate(DocumentEvent de){
            removeChangeListener(checkBoxListener);
            
            updateDisplay();
            
            addChangeListener(checkBoxListener);
        }

        // WHEN EVER THERE IS A CHANGE IN THE VALUE IN THE TEXT FIELD CHANGE THE CHECK BOX
        // ACCORDINGLY.
        public void insertUpdate(DocumentEvent de) {
            removeChangeListener(checkBoxListener);
            
            updateDisplay();
            
            addChangeListener( checkBoxListener );
        }

        // IF A REMOVE UPDATE OCCURS ON THE TEXT FIELD CHECK THE CHANGE AND SET THE
        // CHECK BOX ACCORDINGLY.
        public void removeUpdate(DocumentEvent de) {
            removeChangeListener(checkBoxListener);
            
            updateDisplay();
            
            addChangeListener( checkBoxListener );
        }
    } // end private class MyTextFieldDocumentListener implements DocumentListener, Serializable {

    /**
     * Listener(s) for the component's value used to propigate changes back to
     * bound text field.
     */
    private class MyCheckBoxListener implements ChangeListener, Serializable {

        public void stateChanged(ChangeEvent ce) {
            textField.getDocument().removeDocumentListener(textFieldDocumentListener);

            if ( ((JCheckBox)ce.getSource()).isSelected() ) {
                switch(columnType) {
                    case java.sql.Types.INTEGER:
                    case java.sql.Types.SMALLINT:
                    case java.sql.Types.TINYINT:
                        textField.setText(String.valueOf(CHECKED));
                        break;
                    case java.sql.Types.BIT:
                    case java.sql.Types.BOOLEAN:
                        textField.setText(BOOLEAN_CHECKED);
                        break;
                }
            } else {
                switch(columnType) {
                    case java.sql.Types.INTEGER:
                    case java.sql.Types.SMALLINT:
                    case java.sql.Types.TINYINT:
                        textField.setText(String.valueOf(UNCHECKED));
                        break;
                    case java.sql.Types.BIT:
                    case java.sql.Types.BOOLEAN:
                        textField.setText(BOOLEAN_UNCHECKED);
                        break;
                }
            }

            textField.getDocument().addDocumentListener(textFieldDocumentListener);
        }

    } // end private class MyCheckBoxListener implements ChangeListener, Serializable {
        
        
        
// DEPRECATED STUFF....................

    /**
     * Creates a object of SSCheckBox which synchronizes with the value in the specified
     * text field.
     *
     * @param _textField the text field with which the check box will be in sync.
     *
     * @deprecated
     */
    public SSCheckBox(JTextField _textField) {
        textField = _textField;
    }

    /**
     * Initializes the check box by getting the value corresponding to
     * specified column from the SSRowSet.
     *
     * @deprecated
     */
    public void execute() {
        //init();
    }    
        

} // end public class SSCheckBox extends JCheckBox {



/*
 * $Log$
 * Revision 1.16  2006/05/15 16:10:38  prasanth
 * Updated copy right
 *
 * Revision 1.15  2005/02/21 16:31:18  prasanth
 * In bind checking for empty columnName before binding the component.
 *
 * Revision 1.14  2005/02/13 15:38:19  yoda2
 * Removed redundant PropertyChangeListener and VetoableChangeListener class variables and methods from components with JComponent as an ancestor.
 *
 * Revision 1.13  2005/02/11 22:59:23  yoda2
 * Imported PropertyVetoException and added some bound properties.
 *
 * Revision 1.12  2005/02/11 20:15:57  yoda2
 * Added infrastructure to support property & vetoable change listeners (for beans).
 *
 * Revision 1.11  2005/02/10 20:12:36  yoda2
 * Setter/getter cleanup & method reordering for consistency.
 *
 * Revision 1.10  2005/02/10 03:46:47  yoda2
 * Replaced all setDisplay() methods & calls with updateDisplay() methods & calls to prevent any setter/getter confusion.
 *
 * Revision 1.9  2005/02/07 20:36:33  yoda2
 * Made private listener data members final.
 *
 * Revision 1.8  2005/02/05 18:13:29  yoda2
 * JavaDoc cleanup.
 *
 * Revision 1.7  2005/02/05 05:16:33  yoda2
 * API cleanup.
 *
 * Revision 1.6  2005/02/04 22:48:52  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.5  2005/02/02 23:37:19  yoda2
 * API cleanup.
 *
 * Revision 1.4  2005/02/01 17:32:37  yoda2
 * API cleanup.
 *
 * Revision 1.3  2005/01/21 22:55:00  yoda2
 * API cleanup.
 *
 * Revision 1.2  2005/01/19 20:54:43  yoda2
 * API cleanup.
 *
 * Revision 1.1  2005/01/10 15:09:11  yoda2
 * Added to replace deprecated SSDBCheckBox to match naming conventions.
 *
 */
