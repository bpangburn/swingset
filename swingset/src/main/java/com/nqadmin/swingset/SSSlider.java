/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Contributors:
 *   Prasanth R. Pasala
 *   Brian E. Pangburn
 *   Diego Gil
 *   Man "Bee" Vo
 ******************************************************************************/

package com.nqadmin.swingset;

import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.datasources.SSRowSet;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

// SSSlider.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to link a JSlider to a numeric column in a database.
 */
public class SSSlider extends JSlider implements SSComponentInterface {

    /**
     * Listener(s) for the component's value used to propagate changes back to
     * bound text field.
     */
    protected class SSSliderListener implements ChangeListener, Serializable {

        /**
		 * unique serial ID
		 */
		private static final long serialVersionUID = -5004328872032247853L;

		@Override
		public void stateChanged(final ChangeEvent ce) {
			
			removeSSRowSetListener();
			
			setBoundColumnText(String.valueOf(getValue()));
			
			addSSRowSetListener();
//            SSSlider.this.textField.getDocument().removeDocumentListener(SSSlider.this.textFieldDocumentListener);
//
//            SSSlider.this.textField.setText(String.valueOf(getValue()));
//
//            SSSlider.this.textField.getDocument().addDocumentListener(SSSlider.this.textFieldDocumentListener);
        }

    } // end protected class SSSliderListener implements ChangeListener, Serializable {
	
    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = 8477179080546081481L;

//	/**
//     * Text field bound to the SSRowSet.
//     */
//    protected JTextField textField = new JTextField();
//
//    /**
//     * SSRowSet from which component will get/set values.
//     */
//    protected SSRowSet sSRowSet;
//
//    /**
//     * SSRowSet column to which the component will be bound.
//     */
//    protected String columnName = "";
//
//    /**
//     * Column SQL data type.
//     */
//    protected int columnType;

    /**
     * Common fields shared across SwingSet components
     */
    protected SSCommon ssCommon;

//    /**
//     * Bound text field document listener.
//     */
//    protected final MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();

    /**
     * Component listener.
     */
    protected final SSSliderListener ssSliderListener = new SSSliderListener();
    
	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

    /**
     * Empty constructor needed for deserialization. Creates a horizontal
     * slider with the range 0 to 100.
     */
    public SSSlider() {
    	super();
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
    }

    /**
     * Creates a slider using the specified orientation with the range 0 to 100.
     *
     * @param _orientation	slider spatial orientation
     */
    public SSSlider(final int _orientation) {
		super(_orientation);
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
    }

    /**
     * Creates a horizontal slider using the specified min and max.
     *
     * @param _min	minimum slider value
     * @param _max	maximum slider value
     */
    public SSSlider(final int _min, final int _max) {
		super(_min, _max);
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
    }

//    /**
//     * Sets the SSRowSet column name to which the component is bound.
//     *
//     * @param _columnName    column name in the SSRowSet to which the component
//     *    is bound
//     * @throws java.sql.SQLException SQLException
//     */
//    public void setColumnName(String _columnName) throws java.sql.SQLException {
//        String oldValue = this.columnName;
//        this.columnName = _columnName;
//        firePropertyChange("columnName", oldValue, this.columnName);
//        bind();
//    }
//
//    /**
//     * Returns the SSRowSet column name to which the component is bound.
//     *
//     * @return column name to which the component is bound
//     */
//    public String getColumnName() {
//        return this.columnName;
//    }
//
//    /**
//     * Sets the SSRowSet to which the component is bound.
//     *
//     * @param _sSRowSet    SSRowSet to which the component is bound
//     * @throws java.sql.SQLException SQLException
//     */
//    public void setSSRowSet(SSRowSet _sSRowSet) throws java.sql.SQLException {
//        SSRowSet oldValue = this.sSRowSet;
//        this.sSRowSet = _sSRowSet;
//        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);
//        bind();
//    }
//
//    /**
//     * Returns the SSRowSet to which the component is bound.
//     *
//     * @return SSRowSet to which the component is bound
//     */
//    public SSRowSet getSSRowSet() {
//        return this.sSRowSet;
//    }
//
//    /**
//     * Sets the SSRowSet and column name to which the component is to be bound.
//     *
//     * @param _sSRowSet    datasource to be used.
//     * @param _columnName    Name of the column to which this check box should be bound
//     * @throws java.sql.SQLException 	SQL Exception
//     */
//    public void bind(SSRowSet _sSRowSet, String _columnName) throws java.sql.SQLException {
//        SSRowSet oldValue = this.sSRowSet;
//        this.sSRowSet = _sSRowSet;
//        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);
//
//        String oldValue2 = this.columnName;
//        this.columnName = _columnName;
//        firePropertyChange("columnName", oldValue2, this.columnName);
//
//        bind();
//    }

//    /**
//     * Initialization code.
//     */
//    protected void init() {
//
//        // SET PREFERRED DIMENSIONS
//            setPreferredSize(new Dimension(200,20));
//    }
//
//    /**
//     * Method for handling binding of component to a SSRowSet column.
//     * @throws java.sql.SQLException SQLException
//     */
//    protected void bind() throws java.sql.SQLException {
//
//        // CHECK FOR NULL COLUMN/ROWSET
//            if (this.columnName==null || this.columnName.trim().equals("") || this.sSRowSet==null) {
//                return;
//            }
//
//        // REMOVE LISTENERS TO PREVENT DUPLICATION
//            removeListeners();
//            
//        // BIND AND UPDATE DISPLAY
//            try {
//
//	        // DETERMINE COLUMN TYPE
//				this.columnType = this.sSRowSet.getColumnType(this.columnName);
//	
//	        // BIND THE TEXT FIELD TO THE SPECIFIED COLUMN
//	            this.textField.setDocument(new SSTextDocument(this.sSRowSet, this.columnName));
//	
//	        // SET THE LABEL DISPLAY
//	            updateDisplay();
//	            
//            } finally {
//	        // ADD BACK LISTENERS
//	            addListeners();
//            }
//
//    }

//    /**
//     * Updates the value displayed in the component based on the SSRowSet column
//     * binding.
//     */
//	protected void updateDisplay() {
//
//		// SET THE SLIDER BASED ON THE VALUE IN THE TEXT FIELD
//            switch(this.columnType) {
//                case java.sql.Types.INTEGER:
//                case java.sql.Types.SMALLINT:
//                case java.sql.Types.TINYINT:
//                case java.sql.Types.BIGINT:
//                case java.sql.Types.FLOAT:
//                case java.sql.Types.DOUBLE:
//                case java.sql.Types.NUMERIC:
//            	// SET THE SLIDER BASED ON THE VALUE IN TEXT FIELD
//            		setValue(Integer.parseInt(this.textField.getText()));
//                    break;
//
//                default:
//                    break;
//            }
//
//    } // end protected void updateDisplay() {

//    /**
//     * Adds listeners for component and bound text field (where applicable).
//     */
//    private void addListeners() {
//        this.textField.getDocument().addDocumentListener(this.textFieldDocumentListener);
//        addChangeListener(this.sliderListener);
//    }
//
//    /**
//     * Removes listeners for component and bound text field (where applicable).
//     */
//    private void removeListeners() {
//        this.textField.getDocument().removeDocumentListener(this.textFieldDocumentListener);
//        removeChangeListener(this.sliderListener);
//    }

//    /**
//     * Listener(s) for the bound text field used to propigate values back to the
//     * component's value.
//     */
//    protected class MyTextFieldDocumentListener implements DocumentListener, Serializable {
//
//        /**
//		 * unique serial id
//		 */
//		private static final long serialVersionUID = 2592351765135476620L;
//
//		@Override
//		public void changedUpdate(DocumentEvent de) {
//            removeChangeListener(SSSlider.this.sliderListener);
//
//        	updateDisplay();
//
//            addChangeListener(SSSlider.this.sliderListener);
//        }
//
//        // WHEN EVER THERE IS A CHANGE IN THE VALUE IN THE TEXT FIELD CHANGE THE SLIDER
//        // ACCORDINGLY.
//        @Override
//		public void insertUpdate(DocumentEvent de) {
//            removeChangeListener(SSSlider.this.sliderListener);
//
//            updateDisplay();
//
//            addChangeListener(SSSlider.this.sliderListener);
//        }
//
//        // IF A REMOVE UPDATE OCCURS ON THE TEXT FIELD CHECK THE CHANGE AND SET THE
//        // SLIDER ACCORDINGLY.
//        @Override
//		public void removeUpdate(DocumentEvent de) {
//            removeChangeListener(SSSlider.this.sliderListener);
//
//            updateDisplay();
//
//            addChangeListener(SSSlider.this.sliderListener);
//        }
//
//    } // end protected class MyTextFieldDocumentListener implements DocumentListener, Serializable {
    
//    /**
//     * Updates the underlying RowSet when there is a change to the Document object.
//     * 
//     * These types of changes can result from a change in the RowSet pushed to the Document or a call to setText() on the
//     * JTextField.
//     * 
//     * DocumentListener events generally, but not always get fired twice any time there is an update to the JTextField:
//     * a removeUpdate() followed by insertUpdate().
//     * See:
//     * https://stackoverflow.com/questions/15209766/why-jtextfield-settext-will-fire-documentlisteners-removeupdate-before-change#15213813
//     * 
//     * Using partial solution here from here:
//     * https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
//     * 
//     * Having removeUpdate() and insertUpdate() both call changedUpdate(). changedUpdate() uses counters
//     * and SwingUtilities.invokeLater() to only update the display on the last method called.
//     * 
//     * Note that we do not want to handle removal/addition of listeners in updateText() because other
//     * code could call it directly.
//     */
//    // TODO audit which listeners need to be removed in changedUpdate()
//
//    protected class MyTextFieldDocumentListener implements DocumentListener, Serializable {
//    	
//    	/**
//		 * unique serial id
//		 */
//		private static final long serialVersionUID = 2592351765135476620L;
//		
//		/**
//		 * variables needed to consolidate calls to removeUpdate() and insertUpdate() from DocumentListener
//		 */
//		private int lastChange=0;
//    	private int lastNotifiedChange = 0;
//    	
//    	@Override    	
//		public void changedUpdate(DocumentEvent de) {
//			lastChange++;
//			//System.out.println("SSSlider (" + SSSlider.this.getColumnName() + ") - changedUpdate(): lastChange + ", lastNotifiedChange=" + lastNotifiedChange);
//			// Delay execution of logic until all listener methods are called for current event
//			// See: https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
//			SwingUtilities.invokeLater(() -> {
//				if (lastNotifiedChange != lastChange) {
//					lastNotifiedChange = lastChange;
//					
//					removeChangeListener(SSSlider.this.sliderListener);
//					updateDisplay();
//					addChangeListener(SSSlider.this.sliderListener);
//				}
//			});
//    	}
//    	
//        @Override
//		public void insertUpdate(DocumentEvent de) {
//        	//System.out.println("SSSlider (" + this.columnName + ") - insertUpdate()");
//        	changedUpdate(de);
//        }
//        
//        @Override
//		public void removeUpdate(DocumentEvent de) {
//        	//System.out.println("SSSlider (" + this.columnName + ") - removeUpdate()"); 
//        	changedUpdate(de);
//        }
//
//    } // end protected class MyTextFieldDocumentListener implements DocumentListener {    

    /**
     * Creates a horizontal slider with the range 0 to 100 and binds it
     * to the specified SSRowSet column.
     *
     * @param _ssRowSet    datasource to be used.
     * @param _boundColumnName    name of the column to which this slider should be bound
     * @throws java.sql.SQLException SQLException
     */
    public SSSlider(final SSRowSet _ssRowSet, final String _boundColumnName) throws java.sql.SQLException {
    	super();
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
		bind(_ssRowSet, _boundColumnName);
//		this.sSRowSet = _sSRowSet;
//        this.columnName = _columnName;
//        init();
//        bind();
    }

	/**
	 * Adds any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 */
	@Override
	public void addSSComponentListener() {
		addChangeListener(ssSliderListener);

	}

	/**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * <p>
	 * It will actually be called from SSCommon.init() once the SSCommon data member
	 * is instantiated.
	 */
	@Override
	public void customInit() {
		// TODO Consider removing default dimensions.
        // SET PREFERRED DIMENSIONS
        	setPreferredSize(new Dimension(200,20));		
	}

	/**
	 * Returns the ssCommon data member for the current Swingset component.
	 * 
	 * @return shared/common SwingSet component data and methods
	 */
	@Override
	public SSCommon getSSCommon() {
		return ssCommon;
	}

	/**
	 * Removes any necessary listeners for the current SwingSet component. These
	 * will trigger changes in the underlying RowSet column.
	 */
	@Override
	public void removeSSComponentListener() {
		removeChangeListener(ssSliderListener);

	}

	/**
	 * Sets the SSCommon data member for the current Swingset Component.
	 * 
	 * @param _ssCommon shared/common SwingSet component data and methods
	 */
	@Override
	public void setSSCommon(final SSCommon _ssCommon) {
		ssCommon = _ssCommon;

	}

	@Override
	public void updateSSComponent() {
		
		// TODO Modify this class similar to updateSSComponent() in SSFormattedTextField and only allow JDBC types that convert to numeric types
		
		// SET THE SLIDER BASED ON THE VALUE IN THE TEXT FIELD
		switch (getBoundColumnType()) {
            case java.sql.Types.INTEGER:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.TINYINT:
            case java.sql.Types.BIGINT:
            case java.sql.Types.FLOAT:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.NUMERIC:
        	// SET THE SLIDER BASED ON THE VALUE IN TEXT FIELD
            	final String columnValue = getBoundColumnText();
            	try {
            		if ((columnValue==null) || columnValue.isEmpty()) {
            			setValue(0);
	            	} else {
	            		setValue(Integer.parseInt(columnValue));
	            	}
            	} catch (final NumberFormatException _nfe) {
            		logger.error(getColumnForLog() + ": Number Format Exception. Cannot update slider to " + columnValue, _nfe);
            	}
                break;

            default:
            	logger.warn(getColumnForLog() + ": Unable to update Slider bound to " + getBoundColumnName() + " because the data type is not supported (" + getBoundColumnType() + ".");
                break;
        }
		
	}

} // end public class SSSlider extends JSlider