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
    private JTextField textField = new JTextField();

    private String columnName;
    private int columnType = java.sql.Types.BIT;

    // LISTENER FOR CHECK BOX AND TEXT FEILD
    private MyCheckBoxListener  checkBoxListener  = new MyCheckBoxListener();
    private MyTextFieldListener textFieldListener = new MyTextFieldListener();

    // INITIALIZE CHECKED AND UNCHECKED VALUES
    private int CHECKED = 1;
    private int UNCHECKED = 0;

    // INITIALIZE CHECKED AND UNCHECKED VALUES FOR BOOLEAN COLUMN
    private static String BOOLEAN_CHECKED = "true";
    private static String BOOLEAN_UNCHECKED = "false";

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
     * Creates an object of SSCheckBox.
     */
    public SSCheckBox() {
        textField = new JTextField();
    }

    /**
     * Creates an object of SSCheckBox binding it so the specified column
     * in the given SSRowSet.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    name of the column to which this check box should be bound
     */
    public SSCheckBox(SSRowSet _rowset, String _columnName) throws java.sql.SQLException {
        columnName = _columnName;
        textField.setDocument(new SSTextDocument(_rowset, _columnName));
        columnType = _rowset.getColumnType(_columnName);
    }

    /**
     * Sets the datasource and the columnName in the datasource to which the
     * SSCheckBox has to be bound to.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    Name of the column to which this check box should be bound
     */
    public void bind(SSRowSet _rowset, String _columnName) throws java.sql.SQLException {
        columnName = _columnName;
        textField.setDocument(new SSTextDocument(_rowset, _columnName));
        columnType = _rowset.getColumnType(_columnName);
    }

    /**
     * returns the column name to which this check box is bound to.
     *
     * @return column name to which the check box is bound.
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Initializes the check box by getting the value corresponding to
     * specified column from the SSRowSet.
     */
    public void execute() {
        initCheckBox();
    }


    // INITIALIZES THE CHECK BOX.
    private void initCheckBox() {

        // ADD LISTENER FOR THE TEXT FIELD
            textField.getDocument().addDocumentListener(textFieldListener);

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

        //ADD LISTENER FOR THE CHECK BOX.
        // REMOVE HAS TO BE CALLED SO MAKE SURE THAT YOU ARE NOT STACKING UP
        // LISTENERS WHEN EXECUTE IS CALLED MULTIPLE TIMES.
            removeChangeListener(checkBoxListener);
            addChangeListener( checkBoxListener );
    } // end private void initCheckBox() {

    // LISTENER FOR THE TEXT FIELD
    private class MyTextFieldListener implements DocumentListener, Serializable {
        private void readObject(ObjectInputStream objIn) throws IOException, ClassNotFoundException {
            objIn.defaultReadObject();
        }

        private void writeObject(ObjectOutputStream objOut) throws IOException {
            objOut.defaultWriteObject();
        }

        public void changedUpdate(DocumentEvent de){
            removeChangeListener(checkBoxListener);

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
            addChangeListener( checkBoxListener );
        }

        // WHEN EVER THERE IS A CHANGE IN THE VALUE IN THE TEXT FIELD CHANGE THE CHECK BOX
        // ACCORDINGLY.
        public void insertUpdate(DocumentEvent de) {
            removeChangeListener( checkBoxListener );

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
            addChangeListener( checkBoxListener );
        }

        // IF A REMOVE UPDATE OCCURS ON THE TEXT FIELD CHECK THE CHANGE AND SET THE
        // CHECK BOX ACCORDINGLY.
        public void removeUpdate(DocumentEvent de) {
            removeChangeListener( checkBoxListener );

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
            addChangeListener( checkBoxListener );
        }
    } // end private class MyTextFieldListener implements DocumentListener, Serializable {

    // LISTENER FOR THE CHECK BOX.
    // ANY CHANGES MADE TO THE CHECK BOX BY THE USER ARE PROPOGATED BACK TO THE
    // TEXT FIELD FOR FURTHER PROPOGATION TO THE UNDERLYING STORAGE STRUCTURE.
    private class MyCheckBoxListener implements ChangeListener, Serializable {

        private void readObject(ObjectInputStream objIn) throws IOException, ClassNotFoundException {
            objIn.defaultReadObject();
        }

        private void writeObject(ObjectOutputStream objOut) throws IOException {
            objOut.defaultWriteObject();
        }

        public void stateChanged(ChangeEvent ce) {
            textField.getDocument().removeDocumentListener(textFieldListener);

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

            textField.getDocument().addDocumentListener(textFieldListener);
        }

    } // end private class MyCheckBoxListener implements ChangeListener, Serializable {

} // end public class SSCheckBox extends JCheckBox {



/*
 * $Log$
 */
