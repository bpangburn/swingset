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
 * SSSlider.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Used to link a JSlider to a numeric column in a database.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
public class SSSlider extends JSlider {

    // TEXT FIELD BOUND TO THE DATABASE
    private JTextField textField;

    private String columnName;
    private int columnType;

    // LISTENER FOR SLIDER AND TEXT FEILD
    private MySliderListener sliderListener = new MySliderListener();
    private MyTextFieldListener textFieldListener = new MyTextFieldListener();

    /**
     * Creates an object of SSSlider.
     */
    public SSSlider() {
        textField = new JTextField();
    }

    /**
     * Creates an object of SSSlider binding it so the specified column
     * in the given SSRowSet.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    name of the column to which this slider should be bound
     */
    public SSSlider(SSRowSet _rowset, String _columnName) throws java.sql.SQLException {
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
        initSlider();
    }


    // INITIALIZES THE SLIDER.
    private void initSlider() {

        // ADD LISTENER FOR THE TEXT FIELD
            textField.getDocument().addDocumentListener(textFieldListener);

            switch(columnType) {
                case java.sql.Types.INTEGER:
                case java.sql.Types.SMALLINT:
                case java.sql.Types.TINYINT:
                case java.sql.Types.BIGINT:
                case java.sql.Types.FLOAT:
                case java.sql.Types.DOUBLE:
                case java.sql.Types.NUMERIC:
            	// SET THE SLIDER BASED ON THE VALUE IN TEXT FIELD
            		setValue(Integer.parseInt(textField.getText()));
                    break;

                default:
                    break;
            }

        //ADD LISTENER FOR THE SLIDER.
        // REMOVE HAS TO BE CALLED SO MAKE SURE THAT YOU ARE NOT STACKING UP
        // LISTENERS WHEN EXECUTE IS CALLED MULTIPLE TIMES.
            removeChangeListener(sliderListener);
            addChangeListener( sliderListener );
    } // end private void initSlider() {

    // LISTENER FOR THE TEXT FIELD
    private class MyTextFieldListener implements DocumentListener, Serializable {
        private void readObject(ObjectInputStream objIn) throws IOException, ClassNotFoundException {
            objIn.defaultReadObject();
        }

        private void writeObject(ObjectOutputStream objOut) throws IOException {
            objOut.defaultWriteObject();
        }

        public void changedUpdate(DocumentEvent de){
            removeChangeListener(sliderListener);

            switch(columnType) {
                case java.sql.Types.INTEGER:
                case java.sql.Types.SMALLINT:
                case java.sql.Types.TINYINT:
                case java.sql.Types.BIGINT:
                case java.sql.Types.FLOAT:
                case java.sql.Types.DOUBLE:
                case java.sql.Types.NUMERIC:
            	// SET THE SLIDER BASED ON THE VALUE IN TEXT FIELD
            		setValue(Integer.parseInt(textField.getText()));
                    break;

                default:
                    break;
            }

            addChangeListener( sliderListener );
        }

        // WHEN EVER THERE IS A CHANGE IN THE VALUE IN THE TEXT FIELD CHANGE THE SLIDER
        // ACCORDINGLY.
        public void insertUpdate(DocumentEvent de) {
            removeChangeListener( sliderListener );

            switch(columnType) {
                case java.sql.Types.INTEGER:
                case java.sql.Types.SMALLINT:
                case java.sql.Types.TINYINT:
                case java.sql.Types.BIGINT:
                case java.sql.Types.FLOAT:
                case java.sql.Types.DOUBLE:
                case java.sql.Types.NUMERIC:
            	// SET THE SLIDER BASED ON THE VALUE IN TEXT FIELD
            		setValue(Integer.parseInt(textField.getText()));
                    break;

                default:
                    break;
            }

            addChangeListener( sliderListener );
        }

        // IF A REMOVE UPDATE OCCURS ON THE TEXT FIELD CHECK THE CHANGE AND SET THE
        // SLIDER ACCORDINGLY.
        public void removeUpdate(DocumentEvent de) {
            removeChangeListener( sliderListener );

            switch(columnType) {
                case java.sql.Types.INTEGER:
                case java.sql.Types.SMALLINT:
                case java.sql.Types.TINYINT:
                case java.sql.Types.BIGINT:
                case java.sql.Types.FLOAT:
                case java.sql.Types.DOUBLE:
                case java.sql.Types.NUMERIC:
            	// SET THE SLIDER BASED ON THE VALUE IN TEXT FIELD
            		setValue(Integer.parseInt(textField.getText()));
                    break;

                default:
                    break;
            }

            addChangeListener( sliderListener );
        }
    } // end private class MyTextFieldListener implements DocumentListener, Serializable {

    // LISTENER FOR THE SLIDER.
    // ANY CHANGES MADE TO THE SLIDER BY THE USER ARE PROPOGATED BACK TO THE
    // TEXT FIELD FOR FURTHER PROPOGATION TO THE UNDERLYING STORAGE STRUCTURE.
    private class MySliderListener implements ChangeListener, Serializable {

        private void readObject(ObjectInputStream objIn) throws IOException, ClassNotFoundException {
            objIn.defaultReadObject();
        }

        private void writeObject(ObjectOutputStream objOut) throws IOException {
            objOut.defaultWriteObject();
        }

        public void stateChanged(ChangeEvent ce) {
            textField.getDocument().removeDocumentListener(textFieldListener);

            textField.setText(String.valueOf(getValue()));

            textField.getDocument().addDocumentListener(textFieldListener);
        }

    } // end private class MySliderListener implements ChangeListener, Serializable {

} // end public class SSSlider extends JSlider {



/*
 * $Log$
 */
