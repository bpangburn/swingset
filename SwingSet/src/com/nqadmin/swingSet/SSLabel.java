/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2005, The Pangburn Company, Inc. and Prasanth R. Pasala.
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
 * SSLabel.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Used to display database values in a read-only JLabel.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
public class SSLabel extends JLabel {

    // TEXT FIELD BOUND TO THE DATABASE
    private JTextField textField = new JTextField();

    // BINDING INFORMATION
    private String columnName;
    private int columnType;

    // LISTENER FOR LABEL & TEXT FIELD
    private MyLabelListener labelListener = new MyLabelListener();
    private MyTextFieldListener textFieldListener = new MyTextFieldListener();

    /**
     * Empty constructor needed for deserialization. Creates a SSLabel instance
     * with no image and with an empty string for the title.
     */
    public SSLabel() {
    }

    /**
     * Creates a SSLabel instance with the specified image.
     *
     * @param _image    specified image for label
     */
    public SSLabel(Icon _image) {
		super(_image);
    }

    /**
     * Creates a SSLabel instance with the specified image and horizontal alignment.
     *
     * @param _image    specified image for label
     * @param _horizontalAlignment	horizontal alignment
     */
    public SSLabel(Icon _image, int _horizontalAlignment) {
		super(_image, _horizontalAlignment);
    }

    /**
     * Creates a SSLabel instance with no image and binds it to the specified
     * SSRowSet column.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    name of the column to which this label should be bound
     */
    public SSLabel(SSRowSet _rowset, String _columnName) throws java.sql.SQLException {
        columnName = _columnName;
        textField = new JTextField();
        textField.setDocument(new SSTextDocument(_rowset, _columnName));
        columnType = _rowset.getColumnType(_columnName);
    }

    /**
     * Sets the datasource and the columnName in the datasource to which the
     * SSLabel has to be bound to.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    Name of the column to which this label should be bound
     */
    public void bind(SSRowSet _rowset, String _columnName) throws java.sql.SQLException {
        columnName = _columnName;
        textField.setDocument(new SSTextDocument(_rowset, _columnName));
        columnType = _rowset.getColumnType(_columnName);
    }

    /**
     * returns the column name to which this label is bound to.
     *
     * @return column name to which the label is bound.
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Initializes the label by getting the value corresponding to
     * specified column from the SSRowSet.
     */
    public void execute() {
        initLabel();
    }

    /**
     * Initializes the label.
     */
    private void initLabel() {

        // ADD LISTENER FOR THE TEXT FIELD
            textField.getDocument().addDocumentListener(textFieldListener);

        // SET THE LABEL BASED ON THE VALUE IN THE TEXT FIELD
            setText(textField.getText());

        // ADD LISTENER FOR THE LABEL
        // REMOVE HAS TO BE CALLED SO MAKE SURE THAT YOU ARE NOT STACKING UP
        // LISTENERS WHEN EXECUTE IS CALLED MULTIPLE TIMES.
        //    removeChangeListener(labelListener);
        //    addChangeListener(labelListener);

    } // end private void initLabel() {

    /**
     * Listener for the text document.
     */
    private class MyTextFieldListener implements DocumentListener, Serializable {
        private void readObject(ObjectInputStream objIn) throws IOException, ClassNotFoundException {
            objIn.defaultReadObject();
        }

        private void writeObject(ObjectOutputStream objOut) throws IOException {
            objOut.defaultWriteObject();
        }

        public void changedUpdate(DocumentEvent de) {
            //removeChangeListener(labelListener);

            setText(textField.getText());

            //addChangeListener(labelListener);
        }

        // WHEN EVER THERE IS A CHANGE IN THE VALUE IN THE TEXT FIELD CHANGE THE LABEL
        // ACCORDINGLY.
        public void insertUpdate(DocumentEvent de) {
            //removeChangeListener(labelListener);

            setText(textField.getText());

            //addChangeListener(labelListener);
        }

        // IF A REMOVE UPDATE OCCURS ON THE TEXT FIELD CHECK THE CHANGE AND SET THE
        // CHECK BOX ACCORDINGLY.
        public void removeUpdate(DocumentEvent de) {
            //removeChangeListener(labelListener);

            setText(textField.getText());

            //addChangeListener(labelListener);
        }
    } // end private class MyTextFieldListener implements DocumentListener, Serializable {

    /**
     * Listener for the label.
     */
    private class MyLabelListener implements ChangeListener, Serializable {

        private void readObject(ObjectInputStream objIn) throws IOException, ClassNotFoundException {
            objIn.defaultReadObject();
        }

        private void writeObject(ObjectOutputStream objOut) throws IOException {
            objOut.defaultWriteObject();
        }

        public void stateChanged(ChangeEvent ce) {
            textField.getDocument().removeDocumentListener(textFieldListener);

            textField.setText(getText());

            textField.getDocument().addDocumentListener(textFieldListener);
        }

    } // end private class MyLabelListener implements ChangeListener, Serializable {

} // end public class SSLabel extends JLabel {



/*
 * $Log$
 * Revision 1.2  2005/01/02 18:33:48  yoda2
 * Added back empty constructor needed for deserialization along with other potentially useful constructors from parent classes.
 *
 * Revision 1.1  2005/01/01 05:05:47  yoda2
 * Adding preliminary SwingSet implementations for JLabel & JSlider.
 *
 */
