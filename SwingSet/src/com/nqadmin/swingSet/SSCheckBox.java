/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2005, The Pangburn Company, Inc. and Prasanth R. Pasala.
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
import com.nqadmin.swingSet.datasources.SSRowSet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;

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

    // TEXT FIELD BOUND TO THE DATABASE
    protected JTextField textField = new JTextField();

    // SSROWSET FROM WHICH THE COMBO WILL GET/SET VALUES
    protected SSRowSet rowset;

    // COLUMN NAME TO WHICH THE COMBO WILL BE BOUND TO
    protected String columnName;

    // SQL DATA TYPE OF COLUMN
    protected int columnType = java.sql.Types.BIT;

    // LISTENER FOR CHECK BOX AND TEXT FEILD
    private MyCheckBoxListener checkBoxListener = new MyCheckBoxListener();
    private MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();

    // INITIALIZE CHECKED AND UNCHECKED VALUES
    protected int CHECKED = 1;
    protected int UNCHECKED = 0;

    // INITIALIZE CHECKED AND UNCHECKED VALUES FOR BOOLEAN COLUMN
    protected static String BOOLEAN_CHECKED = "true";
    protected static String BOOLEAN_UNCHECKED = "false";

    /**
     * Creates an object of SSCheckBox.
     */
    public SSCheckBox() {
        //textField = new JTextField();
        init();
    }

    /**
     * Creates an object of SSCheckBox binding it so the specified column
     * in the given SSRowSet.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    name of the column to which this check box should be bound
     */
    public SSCheckBox(SSRowSet _rowset, String _columnName) throws java.sql.SQLException {
        rowset = _rowset;
        columnName = _columnName;
        //textField.setDocument(new SSTextDocument(_rowset, _columnName));
        columnType = _rowset.getColumnType(_columnName);
        init();
        bind();
    }
    
    /**
     * Initialization code.
     */
    protected void init() {
        // ADD KEY LISTENER TO TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER
        // KEY IS PRESSED.
            //cmbDisplayed.addKeyListener(new KeyAdapter() {
            addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                        ((Component)ke.getSource()).transferFocus();
                    }
                }
            });
            
        // SET PREFERRED AND MAXIMUM DIMENSIONS
            setPreferredSize(new Dimension(20,20));
            setMaximumSize(new Dimension(20,20));            
    }    
    
    /**
     * The column name and the SSRowSet should be set before calling this function.
     * If the column name and SSRowSet are set seperately then this function has to
     * be called to bind the combo box to the column in the SSRowSet.
     */
    private void bind() {
        
        // CHECK FOR NULL COLUMN/ROWSET
            if (columnName==null || rowset==null) {
                return;
            }

        // BIND THE TEXT FIELD TO THE SPECIFIED COLUMN
            textField.setDocument(new SSTextDocument(rowset, columnName));

        // SET THE COMBO BOX ITEM DISPLAYED
            setDisplay();
            
        // ADDS LISTENERS FOR TEXT FIELD AND CHECKBOX
        // IF BIND IS CALLED MULTIPLE TIMES OLD LISTENERS HAVE TO BE REMOVED
            removeListeners();
            addListeners();
               
    }
    
    // ADDS LISTENERS FOR THE COMBO BOX AND TEXT FIELD
    private void addListeners() {
        textField.getDocument().addDocumentListener(textFieldDocumentListener);
        //cmbDisplayed.addActionListener(cmbListener);
        addChangeListener(checkBoxListener);   
    }

    // REMOVES THE LISTENERS FOR TEXT FIELD AND THE COMBO BOX DISPLAYED
    private void removeListeners() {
        textField.getDocument().removeDocumentListener(textFieldDocumentListener);
        //cmbDisplayed.removeActionListener(cmbListener);
        removeChangeListener(checkBoxListener);
    }    

    /**
     * Sets the datasource and the columnName in the datasource to which the
     * SSCheckBox has to be bound to.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    Name of the column to which this check box should be bound
     */
    public void bind(SSRowSet _rowset, String _columnName) throws java.sql.SQLException {
        rowset = _rowset;
        columnName = _columnName;
        //textField.setDocument(new SSTextDocument(_rowset, _columnName));
        columnType = _rowset.getColumnType(_columnName);
        bind();
    }

    /**
     * returns the column name to which this check box is bound to.
     *
     * @return column name to which the check box is bound.
     */
    public String getColumnName() {
        return columnName;
    }

    // INITIALIZES THE CHECK BOX.
    private void setDisplay() {
            
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

    } // end private void setDisplay() {

    // LISTENER FOR THE TEXT FIELD
    private class MyTextFieldDocumentListener implements DocumentListener, Serializable {

        public void changedUpdate(DocumentEvent de){
            removeChangeListener(checkBoxListener);
            
            setDisplay();

            /*
            switch(columnType) {
                case java.sql.Types.INTEGER:
                case java.sql.Types.SMALLINT:
                case java.sql.Types.TINYINT:
                // SET THE CHECK BOX BASED ON THE VALUE IN TEXT FIELD
                    if ( textField.getText().equals(String.valueOf(CHECKED)) ) {
                        setSelected(true);
                    } else {
                        setSelected(false);
                    }
                    break;

                case java.sql.Types.BIT:
                case java.sql.Types.BOOLEAN:
                // SET THE CHECK BOX BASED ON THE VALUE IN TEXT FIELD
                    if ( textField.getText().equals(BOOLEAN_CHECKED) ) {
                        setSelected(true);
                    } else {
                        setSelected(false);
                    }
                    break;

                default:
                    break;
            }
            */
            
            addChangeListener(checkBoxListener);
        }

        // WHEN EVER THERE IS A CHANGE IN THE VALUE IN THE TEXT FIELD CHANGE THE CHECK BOX
        // ACCORDINGLY.
        public void insertUpdate(DocumentEvent de) {
            removeChangeListener(checkBoxListener);
            
            setDisplay();
            
            /*

            switch(columnType) {
                case java.sql.Types.INTEGER:
                case java.sql.Types.SMALLINT:
                case java.sql.Types.TINYINT:
                // SET THE CHECK BOX BASED ON THE VALUE IN TEXT FIELD
                    if ( textField.getText().equals(String.valueOf(CHECKED)) ) {
                        setSelected(true);
                    } else {
                        setSelected(false);
                    }
                    break;

                case java.sql.Types.BIT:
                case java.sql.Types.BOOLEAN:
                // SET THE CHECK BOX BASED ON THE VALUE IN TEXT FIELD
                    if ( textField.getText().equals(BOOLEAN_CHECKED) ) {
                        setSelected(true);
                    } else {
                        setSelected(false);
                    }
                    break;

                default:
                    break;
            }
            */
            
            addChangeListener( checkBoxListener );
        }

        // IF A REMOVE UPDATE OCCURS ON THE TEXT FIELD CHECK THE CHANGE AND SET THE
        // CHECK BOX ACCORDINGLY.
        public void removeUpdate(DocumentEvent de) {
            removeChangeListener(checkBoxListener);
            
            setDisplay();
            
            /*

            switch(columnType) {
                case java.sql.Types.INTEGER:
                case java.sql.Types.SMALLINT:
                case java.sql.Types.TINYINT:
                // SET THE CHECK BOX BASED ON THE VALUE IN TEXT FIELD
                    if ( textField.getText().equals(String.valueOf(CHECKED)) ) {
                        setSelected(true);
                    } else {
                        setSelected(false);
                    }
                    break;

                case java.sql.Types.BIT:
                case java.sql.Types.BOOLEAN:
                // SET THE CHECK BOX BASED ON THE VALUE IN TEXT FIELD
                    if ( textField.getText().equals(BOOLEAN_CHECKED) ) {
                        setSelected(true);
                    } else {
                        setSelected(false);
                    }
                    break;

                default:
                    break;
            }
            */
            
            addChangeListener( checkBoxListener );
        }
    } // end private class MyTextFieldDocumentListener implements DocumentListener, Serializable {

    // LISTENER FOR THE CHECK BOX.
    // ANY CHANGES MADE TO THE CHECK BOX BY THE USER ARE PROPOGATED BACK TO THE
    // TEXT FIELD FOR FURTHER PROPOGATION TO THE UNDERLYING STORAGE STRUCTURE.
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
