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
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeSupport;
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;

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
    protected int columnType;

    /**
     * Component listener.
     */
    private final MySliderListener sliderListener = new MySliderListener();
    
    /**
     * Bound text field document listener.
     */    
    private final MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();
    
	/**
	 * Convenience class for providing the property change listener support
	 */
	private PropertyChangeSupport pChangeSupport = new PropertyChangeSupport(this);
    
	/**
	 * Convenience class for providing the vetoable change listener support
	 */
	private VetoableChangeSupport vChangeSupport = new VetoableChangeSupport(this);    

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
     * @param _sSRowSet    datasource to be used.
     * @param _columnName    name of the column to which this slider should be bound
     */
    public SSSlider(SSRowSet _sSRowSet, String _columnName) throws java.sql.SQLException {
		sSRowSet = _sSRowSet;
        columnName = _columnName;
        init();
        bind();
    }
    
    /**
     * Method to add bean property change listeners.
     *
     * @param _listener bean property change listener
     */
    public void addPropertyChangeListener(PropertyChangeListener _listener) {
    	pChangeSupport.addPropertyChangeListener(_listener);
    }
    
    /**
     * Method to remove bean property change listeners.
     *
     * @param _listener bean property change listener
     */    
    public void removePropertyChangeListener(PropertyChangeListener _listener) {
    	pChangeSupport.removePropertyChangeListener(_listener);
    }
    
    /**
     * Method to add bean vetoable change listeners.
     *
     * @param _listener bean vetoable change listener
     */
    public void addVetoableChangeListener(VetoableChangeListener _listener) {
    	vChangeSupport.addVetoableChangeListener(_listener);
    }
    
    /**
     * Method to remove bean veto change listeners.
     *
     * @param _listener bean veto change listener
     */    
    public void removeVetoableChangeListener(VetoableChangeListener _listener) {
    	vChangeSupport.removeVetoableChangeListener(_listener);
    }
    /**
     * Sets the SSRowSet column name to which the component is bound.
     *
     * @param _columnName    column name in the SSRowSet to which the component
     *    is bound
     */
    public void setColumnName(String _columnName) throws java.sql.SQLException {
        columnName = _columnName;
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
        sSRowSet = _sSRowSet;
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
		sSRowSet = _sSRowSet;
        columnName = _columnName;
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
     * Method for handling binding of component to a SSRowSet column.
     */
    protected void bind() throws java.sql.SQLException {

        // CHECK FOR NULL COLUMN/ROWSET
            if (columnName==null || sSRowSet==null) {
                return;
            }

        // REMOVE LISTENERS TO PREVENT DUPLICATION
            removeListeners();

        // DETERMINE COLUMN TYPE
			columnType = sSRowSet.getColumnType(columnName);

        // BIND THE TEXT FIELD TO THE SPECIFIED COLUMN
            textField.setDocument(new SSTextDocument(sSRowSet, columnName));

        // SET THE LABEL DISPLAY
            updateDisplay();

        // ADD BACK LISTENERS
            addListeners();

    }

    /**
     * Updates the value displayed in the component based on the SSRowSet column
     * binding.
     */
	protected void updateDisplay() {

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

    } // end protected void updateDisplay() {

    /**
     * Adds listeners for component and bound text field (where applicable).
     */
    private void addListeners() {
        textField.getDocument().addDocumentListener(textFieldDocumentListener);
        addChangeListener(sliderListener);
    }

    /**
     * Removes listeners for component and bound text field (where applicable).
     */
    private void removeListeners() {
        textField.getDocument().removeDocumentListener(textFieldDocumentListener);
        removeChangeListener(sliderListener);
    }

    /**
     * Listener(s) for the bound text field used to propigate values back to the
     * component's value.
     */
    private class MyTextFieldDocumentListener implements DocumentListener, Serializable {

        public void changedUpdate(DocumentEvent de) {
            removeChangeListener(sliderListener);

        	updateDisplay();

            addChangeListener(sliderListener);
        }

        // WHEN EVER THERE IS A CHANGE IN THE VALUE IN THE TEXT FIELD CHANGE THE SLIDER
        // ACCORDINGLY.
        public void insertUpdate(DocumentEvent de) {
            removeChangeListener(sliderListener);

            updateDisplay();

            addChangeListener(sliderListener);
        }

        // IF A REMOVE UPDATE OCCURS ON THE TEXT FIELD CHECK THE CHANGE AND SET THE
        // SLIDER ACCORDINGLY.
        public void removeUpdate(DocumentEvent de) {
            removeChangeListener(sliderListener);

            updateDisplay();

            addChangeListener(sliderListener);
        }

    } // end private class MyTextFieldDocumentListener implements DocumentListener, Serializable {

    /**
     * Listener(s) for the component's value used to propigate changes back to
     * bound text field.
     */
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
 * Revision 1.11  2005/02/11 20:16:05  yoda2
 * Added infrastructure to support property & vetoable change listeners (for beans).
 *
 * Revision 1.10  2005/02/10 20:13:03  yoda2
 * Setter/getter cleanup & method reordering for consistency.
 *
 * Revision 1.9  2005/02/10 03:46:47  yoda2
 * Replaced all setDisplay() methods & calls with updateDisplay() methods & calls to prevent any setter/getter confusion.
 *
 * Revision 1.8  2005/02/09 19:01:59  yoda2
 * JavaDoc cleanup.
 *
 * Revision 1.7  2005/02/07 20:36:40  yoda2
 * Made private listener data members final.
 *
 * Revision 1.6  2005/02/05 05:16:33  yoda2
 * API cleanup.
 *
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
