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
import com.nqadmin.swingSet.datasources.SSRowSet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;

/**
 * SSDBCheckBox.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Used to display the boolean values stored in the database. The SSDBCheckBox
 * can be bound to a numeric or boolean database column.  Currently, binding to
 * a boolean column has been tested only with postgresql. If bound to a numeric
 * database column, a checked SSDBCheckBox returns a '1' to the database and
 * an uncheck SSDBCheckBox will returns a '0'.  In the future an option may be
 * added to allow the user to specify the values returned for the checked and
 * unchecked checkbox states.
 *</pre><p>
 *
 * @deprecated
 * @see SSCheckBox
 *
 * @author  $Author$
 * @version $Revision$
 */
public class SSDBCheckBox extends JCheckBox {

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
     * Creates a object of SSDBCheckBox which synchronizes with the value in the specified
     * text field.
     *
     * @param _textField the text field with which the check box will be in sync.
     *
     * @deprecated
     */
    public SSDBCheckBox(JTextField _textField) {
        textField = _textField;
    }

    /**
     * Creates an object of SSDBCheckBox.
     */
    public SSDBCheckBox() {
        textField = new JTextField();
    }

    /**
     * Creates an object of SSDBCheckBox binding it so the specified column
     * in the given SSRowSet.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    name of the column to which this check box should be bound
     */
    public SSDBCheckBox(SSRowSet _rowset, String _columnName) throws java.sql.SQLException {
        columnName = _columnName;
        textField.setDocument(new SSTextDocument(_rowset, _columnName));
        columnType = _rowset.getColumnType(_columnName);
    }

    /**
     * Sets the text field with which the check box has to be synchronized.
     *
     * @param _textField    the text field with which the check box will be in sync.
     *
     * @deprecated
     * @see #bind
     */
    public void setTextField(JTextField _textField) {
        // IF THE OLD ONE IS NOT NULL REMOVE ANY LISTENERS BEING ADDED.
        // IT DOES NOT HURT TO CALL REMOVE IF WE ADDED ONE THEN IT WILL BE DELETED.
        // ELSE NOTHING HAPPENS
            if (textField != null) {
                textField.getDocument().removeDocumentListener(textFieldListener);
            }

            textField = _textField;
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
     * Returns the text field with which the check box is synchronizing.
     *
     * @return returns a JTextField which is used to set the check box.
     *
     * @deprecated
     */
    public JTextField getTextField() {
        return textField;
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

} // end public class SSDBCheckBox extends JCheckBox {



/*
 * $Log$
 * Revision 1.11  2005/01/10 15:08:05  yoda2
 * deprecated in favor of SSCheckBox to match naming conventions.
 *
 * Revision 1.10  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.9  2004/11/10 22:45:50  dags
 * Added support for boolean bound column. Tested only in postgresql.
 *
 * Revision 1.8  2004/11/01 15:53:30  yoda2
 * Fixed various JavaDoc errors.
 *
 * Revision 1.7  2004/10/25 22:03:18  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.6  2004/10/25 19:51:03  prasanth
 * Modified to use the new SSRowSet instead of  RowSet.
 *
 * Revision 1.5  2004/08/10 22:06:59  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.4  2004/08/02 15:13:43  prasanth
 * 1. Deprecated getTextField, setTextField and constructor which takes a
 *      TextField.
 * 2. Added constructor which takes a rowset and column name.
 * 3. Also added bind(rowset, columnname).
 * 4. Class implements Serializable.
 *
 * Revision 1.3  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.2  2003/09/25 14:27:45  yoda2
 * Removed unused Import statements and added preformatting tags to JavaDoc descriptions.
 *
 * Revision 1.1.1.1  2003/09/25 13:56:43  yoda2
 * Initial CVS import for SwingSet.
 *
 */
