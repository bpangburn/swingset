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
    protected JTextField textField = new JTextField();
    
    // SSROWSET FROM WHICH THE LABEL WILL GET/SET VALUES
    protected SSRowSet rowset;

    // BINDING INFORMATION
    protected String columnName;
    protected int columnType;

    // LISTENER FOR SLIDER AND TEXT FIELD
    private MySliderListener sliderListener = new MySliderListener();
    private MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();

    /**
     * Empty constructor needed for deserialization. Creates a horizontal
     * slider with the range 0 to 100.
     */
    public SSSlider() {
		init();
    }

    /**
     * Creates a slider using the specified orientation with the range 0 to 100.
     *
     * @param _orientation	slider spatial orientation
     */
    public SSSlider(int _orientation) {
		super(_orientation);
		init();
    }

    /**
     * Creates a horizontal slider using the specified min and max.
     *
     * @param _min	minimum slider value
     * @param _max	maximum slider value
     */
    public SSSlider(int _min, int _max) {
		super(_min, _max);
		init();
    }

    /**
     * Creates a horizontal slider with the range 0 to 100 and binds it
     * to the specified SSRowSet column.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    name of the column to which this slider should be bound
     */
    public SSSlider(SSRowSet _rowset, String _columnName) throws java.sql.SQLException {
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
     * Returns the column name to which the slider is bound.
     *
     * @return returns the column name to which to slider is bound.
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
     * Sets the column name to which the slider has to be bound
     *
     * @param _columnName    column name in the SSRowSet to which the slider
     *    is bound.
     */
    public void setColumnName(String _columnName) throws java.sql.SQLException {
        columnName = _columnName;
        bind();
    }

    /**
     * Sets the SSRowSet to be used.
     *
     * @param _rowset    SSRowSet to be used for getting the values.
     */
    public void setSSRowSet(SSRowSet _rowset) throws java.sql.SQLException {
        rowset = _rowset;
        bind();
    }

    /**
     * The column name and the SSRowSet should be set before calling this function.
     * If the column name and SSRowSet are set seperately then this function has to
     * be called to bind the slider to the column in the SSRowSet.
     */
    protected void bind() throws java.sql.SQLException {

        // CHECK FOR NULL COLUMN/ROWSET
            if (columnName==null || rowset==null) {
                return;
            }

        // REMOVE LISTENERS TO PREVENT DUPLICATION
            removeListeners();

        // DETERMINE COLUMN TYPE
			columnType = rowset.getColumnType(columnName);

        // BIND THE TEXT FIELD TO THE SPECIFIED COLUMN
            textField.setDocument(new SSTextDocument(rowset, columnName));

        // SET THE LABEL DISPLAY
            setDisplay();

        // ADD BACK LISTENERS
            addListeners();

    }

    /**
     * Sets the datasource and the columnName in the datasource to which the
     * SSLabel has to be bound to.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    Name of the column to which this label should be bound
     */
    public void bind(SSRowSet _rowset, String _columnName) throws java.sql.SQLException {
		rowset = _rowset;
        columnName = _columnName;
        bind();
    }

	protected void setDisplay() {

		// SET THE SLIDER BASED ON THE VALUE IN THE TEXT FIELD
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

    } // end protected void setDisplay() {

    // ADDS LISTENERS FOR THE SLIDER AND TEXT FIELD
    private void addListeners() {
        textField.getDocument().addDocumentListener(textFieldDocumentListener);
        addChangeListener(sliderListener);
    }

    // REMOVES THE LISTENERS FOR TEXT FIELD AND THE SLIDER DISPLAYED
    private void removeListeners() {
        textField.getDocument().removeDocumentListener(textFieldDocumentListener);
        removeChangeListener(sliderListener);
    }

    // LISTENER FOR THE TEXT FIELD
    private class MyTextFieldDocumentListener implements DocumentListener, Serializable {

        public void changedUpdate(DocumentEvent de) {
            removeChangeListener(sliderListener);

        	setDisplay();

            addChangeListener(sliderListener);
        }

        // WHEN EVER THERE IS A CHANGE IN THE VALUE IN THE TEXT FIELD CHANGE THE SLIDER
        // ACCORDINGLY.
        public void insertUpdate(DocumentEvent de) {
            removeChangeListener(sliderListener);

            setDisplay();

            addChangeListener(sliderListener);
        }

        // IF A REMOVE UPDATE OCCURS ON THE TEXT FIELD CHECK THE CHANGE AND SET THE
        // SLIDER ACCORDINGLY.
        public void removeUpdate(DocumentEvent de) {
            removeChangeListener(sliderListener);

            setDisplay();

            addChangeListener(sliderListener);
        }

    } // end private class MyTextFieldDocumentListener implements DocumentListener, Serializable {

    // LISTENER FOR THE SLIDER.
    // ANY CHANGES MADE TO THE SLIDER BY THE USER ARE PROPOGATED BACK TO THE
    // TEXT FIELD FOR FURTHER PROPOGATION TO THE UNDERLYING STORAGE STRUCTURE.
    private class MySliderListener implements ChangeListener, Serializable {

        public void stateChanged(ChangeEvent ce) {
            textField.getDocument().removeDocumentListener(textFieldDocumentListener);

            textField.setText(String.valueOf(getValue()));

            textField.getDocument().addDocumentListener(textFieldDocumentListener);
        }

    } // end private class MySliderListener implements ChangeListener, Serializable {

} // end public class SSSlider extends JSlider {



/*
 * $Log$
 * Revision 1.5  2005/02/04 23:05:10  yoda2
 * no message
 *
 * Revision 1.4  2005/02/04 22:48:54  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.3  2005/01/03 02:58:04  yoda2
 * Added appropriate super() calls to non-empty constructors.
 *
 * Revision 1.2  2005/01/02 18:33:49  yoda2
 * Added back empty constructor needed for deserialization along with other potentially useful constructors from parent classes.
 *
 * Revision 1.1  2005/01/01 05:05:48  yoda2
 * Adding preliminary SwingSet implementations for JLabel & JSlider.
 *
 */
