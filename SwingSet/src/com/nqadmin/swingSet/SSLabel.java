/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2005, The Pangburn Company and Prasanth R. Pasala.
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

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
    protected JTextField textField = new JTextField();

    // SSROWSET FROM WHICH THE LABEL WILL GET/SET VALUES
    protected SSRowSet rowset;

    // BINDING INFORMATION
    protected String columnName;

    // LISTENER FOR LABEL & TEXT FIELD
    private MyLabelTextListener labelTextListener = new MyLabelTextListener();
    private MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();

    /**
     * Empty constructor needed for deserialization. Creates a SSLabel instance
     * with no image and with an empty string for the title.
     */
    public SSLabel() {
        init();
    }

    /**
     * Creates a SSLabel instance with the specified image.
     *
     * @param _image    specified image for label
     */
    public SSLabel(Icon _image) {
		super(_image);
        init();
    }

    /**
     * Creates a SSLabel instance with the specified image and horizontal alignment.
     *
     * @param _image    specified image for label
     * @param _horizontalAlignment	horizontal alignment
     */
    public SSLabel(Icon _image, int _horizontalAlignment) {
		super(_image, _horizontalAlignment);
        init();
    }

    /**
     * Creates a SSLabel instance with no image and binds it to the specified
     * SSRowSet column.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    name of the column to which this label should be bound
     */
    public SSLabel(SSRowSet _rowset, String _columnName) {
        rowset = _rowset;
        columnName = _columnName;
        init();
        bind();
    }

    /**
     * Initialization code.
     */
    protected void init() {

        // SET PREFERRED DIMENSIONS
            setPreferredSize(new Dimension(200,20));
    }

    /**
     * Returns the column name to which the label is bound.
     *
     * @return returns the column name to which to label is bound.
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
     * Sets the column name to which the label has to be bound
     *
     * @param _columnName    column name in the SSRowSet to which the label
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
     * The column name and the SSRowSet should be set before calling this function.
     * If the column name and SSRowSet are set seperately then this function has to
     * be called to bind the label to the column in the SSRowSet.
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

        // SET THE LABEL DISPLAY
            setDisplay();

        // ADD BACK LISTENERS
            addListeners();;

    }

    /**
     * Binds the label to the specified column of the SSRowSet.
     * As the SSRowSet changes the label item displayed changes accordingly.
     *
     * @param _rowset    SSRowSet to be used for getting the value.
     * @param _columnName    Column to which the label has to be bound.
     */
    public void bind(SSRowSet _rowset, String _columnName) {
        rowset = _rowset;
        columnName = _columnName;
        bind();
    }

    // INITIALIZES THE LABEL DISPLAY
    protected void setDisplay() {

        // SET THE LABEL BASED ON THE VALUE IN THE TEXT FIELD
            setText(textField.getText());

    } // end protected void setDisplay() {

    // ADDS LISTENERS FOR THE LABEL AND TEXT FIELD
    private void addListeners() {
        textField.getDocument().addDocumentListener(textFieldDocumentListener);
        addPropertyChangeListener("text", labelTextListener);
    }

    // REMOVES THE LISTENERS FOR TEXT FIELD AND THE LABEL DISPLAYED
    private void removeListeners() {
        textField.getDocument().removeDocumentListener(textFieldDocumentListener);
        removePropertyChangeListener("text", labelTextListener);
    }

    /**
     * Listener for the text document.
     */
    private class MyTextFieldDocumentListener implements DocumentListener, Serializable {
        public void changedUpdate(DocumentEvent de) {
            removePropertyChangeListener("text", labelTextListener);

            setDisplay();

            addPropertyChangeListener("text", labelTextListener);
        }

        // WHEN EVER THERE IS A CHANGE IN THE VALUE IN THE TEXT FIELD CHANGE THE LABEL
        // ACCORDINGLY.
        public void insertUpdate(DocumentEvent de) {
            removePropertyChangeListener("text", labelTextListener);

            setDisplay();

            addPropertyChangeListener("text", labelTextListener);
        }

        // IF A REMOVE UPDATE OCCURS ON THE TEXT FIELD CHECK THE CHANGE AND SET THE
        // CHECK BOX ACCORDINGLY.
        public void removeUpdate(DocumentEvent de) {
            removePropertyChangeListener("text", labelTextListener);

            setDisplay();

            addPropertyChangeListener("text", labelTextListener);
        }
    } // end private class MyTextFieldDocumentListener implements DocumentListener, Serializable {

    /**
     * Listener for the label.
     */
    private class MyLabelTextListener implements PropertyChangeListener, Serializable {
        public void propertyChange(PropertyChangeEvent pce) {
            textField.getDocument().removeDocumentListener(textFieldDocumentListener);

            textField.setText(getText());

            textField.getDocument().addDocumentListener(textFieldDocumentListener);
        }

    } // end private class MyLabelTextListener implements ChangeListener, Serializable {

} // end public class SSLabel extends JLabel {



/*
 * $Log$
 * Revision 1.5  2005/02/04 22:48:54  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.4  2005/02/01 17:32:38  yoda2
 * API cleanup.
 *
 * Revision 1.3  2005/01/03 02:58:03  yoda2
 * Added appropriate super() calls to non-empty constructors.
 *
 * Revision 1.2  2005/01/02 18:33:48  yoda2
 * Added back empty constructor needed for deserialization along with other potentially useful constructors from parent classes.
 *
 * Revision 1.1  2005/01/01 05:05:47  yoda2
 * Adding preliminary SwingSet implementations for JLabel & JSlider.
 *
 */
