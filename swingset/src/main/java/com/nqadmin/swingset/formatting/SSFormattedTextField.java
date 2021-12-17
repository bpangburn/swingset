/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 *   Ernie R. Rael
 ******************************************************************************/
package com.nqadmin.swingset.formatting;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

// SSFormattedTextField.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * SSFormattedTextField extends the JFormattedTextField.
 * <p>
 * Generally bound components are implemented by extending SSFormattedTextField and
 * instantiating with a custom FormatterFactory parameter. E.g. SSDateField ssdf = new SSDateField(SSDateFormatterFactory);
 * <p>
 * Each FormatterFactory will have calls to setDefaultFormatter(), setNullFormatter(), setEditFormatter(), and setDisplayFormatter()
 * <p>
 * It would be possible to instead use a MaskFormatter, but custom code has to be written if the field needs to be nullable/blanked
 * by the user. For a MaskFormatter, this triggers a ParseException, which would need to be caught in the code and surplanted by
 * a call to setValue(null); Using a MaskFormatter still requires additional validation of some sort. E.g. preventing a MM/dd/yyyy date of
 * 99/99/9999 from being entered.
 */
// TODO Consider adding back context help and calculators via popups. See 2020-01-07 revisions or earlier.
// TODO Add JDatePicker support or something similar: https://www.codejava.net/java-se/swing/how-to-use-jdatepicker-to-display-calendar-component and https://github.com/JDatePicker/JDatePicker

public class SSFormattedTextField extends JFormattedTextField
		implements FocusListener, SSComponentInterface {

	/**
	 * We want an InputVerifier in order to lock the focus down while the JFormattedTextField is in
	 * an invalid edit state.
	 * 
	 * Also, we add a call to validateField(), the default implementation of which just returns true.
	 * This allows for the developer to add additional checks (normally range validation) beyond what
	 * is provided by the Formatter/FormatterFactory, which generally only handles display of values
	 * and/or character masks.
	 * 
	 * This class should perform validation AND call setValue() which will trigger a RowSet update.
	 * <p>
	 * See https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/javax/swing/JFormattedTextField.html
	 */
	// TODO: This still needs some work.
	public class FormattedTextFieldVerifier extends InputVerifier {

		@Override
		public boolean verify(final JComponent input) {

			boolean result = true;
			Object value = null;

			if (input instanceof SSFormattedTextField) {
				logger.debug("{}: Instance of SSFormattedTextField.", () -> getColumnForLog());
				
				final SSFormattedTextField ssftf = (SSFormattedTextField) input;
				String formattedText = ssftf.getText();
				
				AbstractFormatter formatter = ssftf.getFormatter();
				logger.debug("Formatter is: " + formatter + ".");
				if (formatter==null) {
					logger.warn("Null formatter encountered for formatted text field.");
				}
				
				if (formatter!=null && formattedText!=null && !formattedText.isEmpty()) {
					try {
// TODO: Determine if call to stringToValue() sets the value for the SSTextField (or allows it to be set after exiting method). If so, we don't need to call .setValue() again below.					
						value = formatter.stringToValue(formattedText);
						// Apparently formatter.stringToValue(formattedText) accomplished the same thing as commitEdit(),
						// but this approach lets us know if the formatter is null.
						//ssftf.commitEdit(); 
					} catch (ParseException pe) {
						logger.warn(getColumnForLog() + ": String of '" + formattedText + "' generated a Parse Exception at " + pe.getErrorOffset() + ".", pe);
						result = false;
						// We're not going to call setValue(null) if result is false.
					}
				} else {
				// value variable is set to null be default, but make a log entry
					logger.debug("Null formatter, empty string, or null text.");
				}

				// now perform custom validation/range checks
				if (result) {
					result = validateField(value);
				}

				// update text color for negatives, where applicable
				if (result) {
					updateTextColor(value);
				}
				
				// set the value to null manually if stringToValue() was not called above, but null
				// is a valid result (e.g., result==true)
				if (result && value==null) {
					// TODO: this call is resulting in 2 property change events - both passing null to the database
					ssftf.setValue(null);
				}

			}

			// Update background color to RED for invalid value.
			// Also force foreground color to BLACK in case it was previous RED (negative number)
			// If value is valid, the background color will change when focus is lost.
			if (result==false) {
				setBackground(Color.RED);
				setForeground(Color.BLACK);
			}

			// return
			return result;
		}
		
		@Override
		public boolean shouldYieldFocus(JComponent input) {
			return verify(input);
		}
	}

	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column
	 */
	protected class SSFormattedTextFieldListener implements PropertyChangeListener, Serializable {

		/**
		 * unique serial id
		 */
		private static final long serialVersionUID = -8060207437911787572L;

		@Override
		public void propertyChange(final PropertyChangeEvent _pce) {

			if (_pce.getPropertyName().equals("value")) {

				ssCommon.removeRowSetListener();

			    final SSFormattedTextField ftf = (SSFormattedTextField)_pce.getSource();

			    final Object currentValue = ftf.getValue();
			    logger.info(getColumnForLog() + ": Object to be passed to database is " + currentValue + ".");

			    // TODO May want to see if we can veto invalid updates
			    // 2020-12-14_BP: allow null if on insert row
			    if (!getAllowNull() && (currentValue==null) && !SSDataNavigator.isInserting(getRowSet())) {
			    	logger.warn("Null value encounted, but not allowed.");
					JOptionPane.showMessageDialog(ftf,
							"Null values are not allowed for " + getBoundColumnName(), "Null Exception", JOptionPane.ERROR_MESSAGE);
			    } else {

				    try {
				    	// 2020-10-02_BP: Date (and presumably Time & Timestamp) fields are returned as java.util.Date and Postgres JDBC doesn't know how to handle them
				    	// java.sql.Date, java.sql.Time, and java.sql.Timestamp are all subclasses of java.util.Date
// TODO This may be where we want to check and deal with NULL				    	
				    	if (currentValue instanceof java.util.Date) {
				    		switch (getBoundColumnJDBCType()) {
				    		case DATE:
				    			getRowSet().updateObject(getBoundColumnName(), new java.sql.Date(((java.util.Date)currentValue).getTime()));
				    			break;
				    		case TIME:
				    			getRowSet().updateObject(getBoundColumnName(), new java.sql.Time(((java.util.Date)currentValue).getTime()));
				    			break;
				    		case TIMESTAMP:
				    			getRowSet().updateObject(getBoundColumnName(), new java.sql.Timestamp(((java.util.Date)currentValue).getTime()));
				    			break;
				    		default:
				    			logger.warn(getColumnForLog() + ": getValue() returned a java.sql.Date, but JDBCType is " + getBoundColumnJDBCType() + ". Unable to update column.");
				    		}
				    	} else{
				    		getRowSet().updateObject(getBoundColumnName(), currentValue);
				    	}		    	

					} catch (final SQLException _se) {
						logger.error(getColumnForLog() + ": RowSet update triggered SQL Exception.", _se);
						JOptionPane.showMessageDialog(ftf,
								"SQL Exception encountered for " + getBoundColumnName(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
					}
			    }

			    ssCommon.addRowSetListener();
			}

		}

	}

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = 5349618425984728006L;

	/**
	 * color for the field that has the focus
	 */
	private java.awt.Color focusBackgroundColor = new java.awt.Color(204, 255, 255);

	/**
     * Common fields shared across SwingSet components
     */
    protected SSCommon ssCommon = new SSCommon(this);

	/**
	 * Used to store background color prior to change following focusGained event
	 * so that the color can be restored upon focusLost.
	 */
    private java.awt.Color standardBackgroundColor = null;
	
	/**
	 * Creates a new instance of SSFormattedTextField
	 */
	public SSFormattedTextField() {
		// Note that call to parent default constructor is implicit.
		//super();
	}

	/**
	 * Creates a new instance of SSFormattedTextField
	 *
	 * @param _formatter AbstractFormatter to use for formatting.
	 */
	public SSFormattedTextField(final AbstractFormatter _formatter) {
    	super(_formatter);
	}

	/**
	 * Creates a new instance of SSFormattedTextField
	 *
	 * @param _factory AbstractFormatterFactory used for formatting.
	 */
	public SSFormattedTextField(final AbstractFormatterFactory _factory) {
    	super(_factory);
	}

	// WE DON'T WANT TO REPLICATE THE JFormattedTextField CONSTRUCTOR THAT ACCEPTS AN OBJECT.
	// FOR SWINGSET THAT SHOULD BE HANDLED SEPARATELY WITH BINDING.

	/**
	 * Creates a new instance of SSFormattedTextField
	 *
	 * @param _format Format used to look up an AbstractFormatter
	 */
	public SSFormattedTextField(final Format _format) {
    	super(_format);
	}

	/**
	 * Sets the value that will be formatted to null
	 */
	public void cleanField() {
		setValue(null);
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

		// COMMIT_OR_REVERT SHOULD ALREADY BE THE DEFAULT
		setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

		// Adding focus listener to assist with highlighting and text selection
		addFocusListener(this);

		// Setting inputVerifier to validate field before focus is lost
		// See https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/javax/swing/JFormattedTextField.html
		setInputVerifier(new FormattedTextFieldVerifier());

	}

	/**
	 * Add highlighting when the focus is gained and select all of the text so
	 * overwriting is easier.
	 *
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(final FocusEvent _event) {

		// USE A DIFFERENT COLOR TO HIGHLIGHT THE FIELD WITH THE FOCUS
		standardBackgroundColor = getBackground();
		setBackground(focusBackgroundColor);


		// HIGHLIGHT THE TEXT IN THE FIELD WHEN FOCUS IS GAINED SO USE CAN JUST TYPE OVER WHAT IS THERE
		//
		// This is a workaround based on the following thread:
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4740914
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				selectAll();
			}
		});
	}

	/**
	 * Remove highlighting (custom background color) when the focus is lost.
	 *
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(final FocusEvent _event) {
		// Restore original background color
		setBackground(standardBackgroundColor);
	}
	
	/**
	 * Returns the background color to be used when this component has the focus
	 * 
	 * @return background color used for component with the focus
	 */
	public java.awt.Color getFocusBackgroundColor() {
		return focusBackgroundColor;
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
	 * {@inheritDoc }
	 */
	@Override
	public SSFormattedTextFieldListener getSSComponentListener() {
		return new SSFormattedTextFieldListener();
	}

	/**
	 * Getter for property nullable.
	 *
	 * @return Value of property nullable.
	 */
	@Deprecated
	public boolean isNullable() {

		return getAllowNull();
	}
	
	/**
	 * Setter for the background color to be used when this component has the focus
	 * 
	 * @param _focusBackgroundColor background color to be used when this component has the focus
	 */
	public void setFocusBackgroundColor(final java.awt.Color _focusBackgroundColor) {
		focusBackgroundColor = _focusBackgroundColor;
	}

    /**
	 * Setter for property nullable.
	 *
	 * @param _nullable New value of property nullable.
	 */
	@Deprecated
	public void setNullable(final boolean _nullable) {

		setAllowNull(_nullable);

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

	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * the object obtained from getValue().
	 * <p>
	 * Calls to this method should be coming from SSCommon and should already have
	 * the Component listener removed.
	 */
	@Override
	public void updateSSComponent() {

		// It is OK to pass a String to SSFormattedTextField or a child class expecting a string (e.g., SSCuitField), but for
		// child classes expecting a number format (e.g., SSCurrencyField) then it will not accept a string an an exception will be thrown.
		//
		// Previously, data type conversions for the component and rowset were handled with DbToFm() and updateRowSet().

		Object newValue = null;

		// TODO Review date/time handling: https://stackoverflow.com/questions/21162753/jdbc-resultset-i-need-a-getdatetime-but-there-is-only-getdate-and-gettimestamp

		try {
			// IF THERE ARE NO RECORDS OR THE COLUMN VALUE IS NULL SET THE FIELD TO NULL AND RETURN
			//if ((getRowSet().getColumnCount()==0) || (getRowSet().getObject(getBoundColumnName()) == null)) {
			if ((RowSetOps.getColumnCount(getRowSet())==0) || (getRowSet().getObject(getBoundColumnName()) == null)) {
				setValue(null);
				return;
			}

			final JDBCType jdbcType = getBoundColumnJDBCType();
			final String columnName = getBoundColumnName();

			// SQL TO JAVA CONVERSIONS: https://stackoverflow.com/questions/5251140/map-database-type-to-concrete-java-class
			//
			// 2020-09-14: Will use java <-> SQL mapping in JDBC to get appropriate Java object and then exclude ones that would cause a problem for JFormattedTextField
			// Based on: https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#getObject-java.lang.String-
			//
			// getObject() will return the given column as a Java object. JDBC specification should contain the mappings for built in types.
			newValue = getRowSet().getObject(columnName);

			/* Java types we want to support for JFormattedTextFields:
			 * 	String
			 *  Boolean
			 *  Float
			 *  Double
			 *  Integer
			 *  Long
			 *  java.math.BigDecimal
			 *  java.sql.Date
			 *  java.sql.Time
			 *  java.sql.Timestamp
			 */
			if ((newValue instanceof String) ||
					(newValue instanceof Boolean) ||
					(newValue instanceof Float) ||
					(newValue instanceof Double) ||
					(newValue instanceof Integer) ||
					(newValue instanceof Long) ||
					(newValue instanceof java.math.BigDecimal) ||
					(newValue instanceof java.sql.Date) ||
					(newValue instanceof java.sql.Time) ||
					(newValue instanceof java.sql.Timestamp)) {
				
				logger.debug("{}: getObject() - " + newValue, () -> getColumnForLog());
				setValue(newValue);
			} else {
				logger.error(getColumnForLog() + ": JDBCType of " + jdbcType.toString() + " was cast to unsupported type of " + newValue.getClass().getName() + " based on JDBC connection getTypeMap().");
			}

		} catch (final java.sql.SQLException sqe) {
			logger.error(getColumnForLog() + ": SQL Exception while updating rowset from formatted component.", sqe);
			setValue(null);
		}

		// SET TEXT COLOR TO RED FOR ANY NEGATIVE NUMBERS
			updateTextColor(newValue);

	}

	/**
	 * Sets text color to red for negative numbers, otherwise black.
	 *
	 * @param _value - value to be validated
	 */
	public void updateTextColor(final Object _value) {

		if (((_value instanceof Double) && ((Double) _value < 0.0))
				|| ((_value instanceof Float) && ((Float) _value < 0.0))
				|| ((_value instanceof Long) && ((Long) _value < 0))
				|| ((_value instanceof Integer) && ((Integer) _value < 0))) {
			setForeground(Color.RED);
		} else {
			setForeground(Color.BLACK);
		}

	}


	/**
	 * Checks if the value is valid of the component. Override for custom validations
	 * not handled by formatter / formatter factory.
	 *
	 * @param _value - value to be validated
	 * @return returns true if the value is valid else false
	 */
	public boolean validateField(final Object _value) {

		// just return true for default implementation
		return true;
	}
}
