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

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	 * unique serial id
	 */
	private static final long serialVersionUID = 5349618425984728006L;

	private java.awt.Color standardColor = null;
	
	/**
	 * color for the field that has the focus
	 */
	// TODO Add setters/getters and allow developer to customize
	private final java.awt.Color focusColor = new java.awt.Color(204, 255, 255);
	
    /**
     * Common fields shared across SwingSet components
     */
    protected SSCommon ssCommon;
    
	/**
	 * Component listener.
	 */
	protected final SSFormattedValueListener ssFormattedValueListener = new SSFormattedValueListener();
	
	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();
	
	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column
	 */
	protected class SSFormattedValueListener implements PropertyChangeListener, Serializable {

		/**
		 * unique serial id
		 */
		private static final long serialVersionUID = -8060207437911787572L;

		@Override
		public void propertyChange(PropertyChangeEvent _pce) {
			
			if (_pce.getPropertyName().equals("value")) {
			
				removeSSRowSetListener();
	
			    SSFormattedTextField ftf = (SSFormattedTextField)_pce.getSource();
			    
			    Object currentValue = ftf.getValue();
			    logger.info(getColumnForLog() + ": Object to be passed to database is " + currentValue + ".");
			    
			    // TODO May want to see if we can veto invalid updates
			    if (!getAllowNull() && currentValue==null) {
			    	logger.warn("Null value encounted, but not allowed.");
					JOptionPane.showMessageDialog((JComponent)ftf,
							"Null values are not allowed for " + getBoundColumnName(), "Null Exception", JOptionPane.ERROR_MESSAGE);
			    } else {
			    
				    try {
						getSSRowSet().updateObject(getBoundColumnName(), currentValue);
					} catch (SQLException _se) {
						logger.error(getColumnForLog() + ": RowSet update triggered SQL Exception.", _se);
						JOptionPane.showMessageDialog((JComponent)ftf,
								"SQL Exception encountered for " + getBoundColumnName(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
					}
			    }
			    
				addSSRowSetListener();
			}
			
		}

	}

	/**
	 * Creates a new instance of SSFormattedTextField
	 */
	public SSFormattedTextField() {
		
    	super();
    	setSSCommon(new SSCommon(this));
    	// SSCommon constructor calls init()

	}
	
	
	/**
	 * Creates a new instance of SSFormattedTextField
	 * 
	 * @param _format Format used to look up an AbstractFormatter
	 */
	public SSFormattedTextField(final Format _format) {
		
    	super(_format);
    	setSSCommon(new SSCommon(this));
    	// SSCommon constructor calls init()

	}
	
	/**
	 * Creates a new instance of SSFormattedTextField
	 * 
	 * @param _formatter AbstractFormatter to use for formatting.
	 */
	public SSFormattedTextField(final AbstractFormatter _formatter) {
		
    	super(_formatter);
    	setSSCommon(new SSCommon(this));
    	// SSCommon constructor calls init()

	}

	/**
	 * Creates a new instance of SSFormattedTextField
	 * 
	 * @param _factory AbstractFormatterFactory used for formatting.
	 */
	public SSFormattedTextField(final AbstractFormatterFactory _factory) {
    	super(_factory);
    	setSSCommon(new SSCommon(this));
    	// SSCommon constructor calls init()
	}
	
	// WE DON'T WANT TO REPLICATE THE JFormattedTextField CONSTRUCTOR THAT ACCEPTS AN OBJECT.
	// FOR SWINGSET THAT SHOULD BE HANDLED SEPARATELY WITH BINDING.

	/**
	 * Remove highlighting when the focus is lost.
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(final FocusEvent _event) {
		// Remove highlighting
		setBackground(this.standardColor);
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
		standardColor = getBackground();
		setBackground(focusColor);

		
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

//	KEEPING DbToFm in comments for reference.
//	
//	/**
//	 * This method perform the actual data transfer from rowset to this object Value
//	 * field. depending on the column Type.
//	 * @param texto this parameter is never used and method should probably be re-written
//	 */
//	private void DbToFm(final String texto) {
//		Object oValue = null;
//		Object nValue = null;
//		int nrow = 0;
//
//		try {
//			nrow = this.rowset.getRow();
//		} catch (SQLException s3) {
//			System.out.println(s3);
//		}
//
//		if (nrow == 0)
//			return;
//
//		try {
//			// IF THE COLUMN VALUE IS NULL SET THE FIELD TO NULL AND RETURN
//			if (this.rowset.getObject(this.columnName) == null) {
//				super.setValue(null);
//				return;
//			}
//
//			switch (this.colType) {
//
//			case java.sql.Types.ARRAY:// 2003A
//				System.out.println("ARRAY not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.BINARY:// -2
//				System.out.println("BINARY not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.BIT:// -7
//				// System.out.println("BIT implemented as Boolean --> " + columnName);
//				nValue = new Boolean(this.rowset.getBoolean(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.BOOLEAN:// 16
//				// System.out.println("BOOLEAN implemented as Boolean --> " + columnName);
//				nValue = new Boolean(this.rowset.getBoolean(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.BLOB:// 2004
//				System.out.println("BLOB not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.CLOB:// 2005
//				System.out.println("CLOB not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.DATALINK:// 70
//				System.out.println("DATALINK not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.DATE:// 91
//				// System.out.println("DATE implemented as java.util.Date --> " + columnName);
//				Date date = this.rowset.getDate(this.columnName);
//				if (date != null) {
//					nValue = new java.util.Date(this.rowset.getDate(this.columnName).getTime());
//					super.setValue(nValue);
//				} else {
//					super.setValue(null);
//				}
//				break;
//
//			case java.sql.Types.DECIMAL:// 3
//				// System.out.println("DECIMAL implemented as BigDecimal --> " + columnName);
//				nValue = new java.math.BigDecimal(this.rowset.getDouble(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.DISTINCT:// 2001
//				System.out.println("DISTINCT not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.DOUBLE:// 8
//				// System.out.println("DOUBLE implemented as Double --> " + columnName);
//				nValue = new Double(this.rowset.getDouble(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.REAL: // 7
//				// System.out.println("REAL implemented as Float --> " + columnName);
//				nValue = new Float(this.rowset.getFloat(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.FLOAT:// 6
//				// System.out.println("FLOAT implemented as Float --> " + columnName);
//				nValue = new Float(this.rowset.getFloat(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.TINYINT:// -6
//				// System.out.println("TINYINT implemented as Integer --> " + columnName);
//				nValue = new Integer(this.rowset.getInt(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.SMALLINT:// 5
//				// System.out.println("SMALLINT implemented as Integer --> " + columnName);
//				nValue = new Integer(this.rowset.getInt(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.INTEGER:// 4
//				// System.out.println("INTEGER implemented as Integer --> " + columnName);
//				nValue = new Integer(this.rowset.getInt(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.BIGINT:// -5
//				// System.out.println("BIGINT implemented as Integer --> " + columnName);
//				nValue = new Integer(this.rowset.getInt(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.JAVA_OBJECT:// 2000
//				System.out.println("JAVA_OBJECT not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.LONGVARBINARY:// -4
//				System.out.println("LONGVARBINARY not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.VARBINARY:// -3
//				System.out.println("VARBINARY not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.VARCHAR://
//				// System.out.println("VARCHAR implemented as String --> " + columnName);
//				nValue = this.rowset.getString(this.columnName);
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.LONGVARCHAR:// -1
//				// System.out.println("LONGVARCHAR implemented as String --> " + columnName);
//				nValue = this.rowset.getString(this.columnName);
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.CHAR:// 1
//				// System.out.println("CHAR implemented as String --> " + columnName);
//				nValue = this.rowset.getString(this.columnName);
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.NULL:// 0
//				System.out.println("NULL not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.NUMERIC:// 2
//				// System.out.println("NUMERIC implemented as BigDecimal --> " + columnName);
//				nValue = new java.math.BigDecimal(this.rowset.getDouble(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.OTHER:// 1111
//				System.out.println("OTHER not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.REF:// 2006
//				System.out.println("REF not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.STRUCT:// 2002
//				System.out.println("STRUCT not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.TIME:// 92
//				// System.out.println("TIME implemented as java.util.Date --> " + columnName);
//				// nValue = new java.util.Date(rowset.getTime(columnName).getTime());
//				nValue = this.rowset.getTime(this.columnName);
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.TIMESTAMP:// 93
//				// System.out.println("TIMESTAMP implemented as java.util.Date --> " +
//				// columnName);
////                    nValue = new java.util.Date(rowset.getTimestamp(columnName).getTime());
//				nValue = this.rowset.getTimestamp(this.columnName);
//				super.setValue(nValue);
//				break;
//
//			default:
//				System.out.println("UNKNOWN Type not implemented (" + this.colType + ")");
//				break;
//			}
//		} catch (java.sql.SQLException sqe) {
//			System.out.println("Error in DbToFm() = " + sqe);
//			sqe.printStackTrace();
//			super.setValue(null);
//		}
//
//		if ((nValue instanceof Double && ((Double) nValue).doubleValue() < 0.0)
//				|| (nValue instanceof Float && ((Float) nValue).floatValue() < 0.0)
//				|| (nValue instanceof Long && ((Long) nValue).intValue() < 0)
//				|| (nValue instanceof Integer && ((Integer) nValue).longValue() < 0)) {
//			this.setForeground(Color.RED);
//		} else {
//			this.setForeground(Color.BLACK);
//		}
//
//		this.firePropertyChange("value", oValue, nValue);
//		oValue = nValue;
//	}

	/**
	 * This class should implements validation AND calls setValue() which triggers RowSet updates.
	 * <p>
	 * See https://docs.oracle.com/javase/8/docs/api/javax/swing/JFormattedTextField.html
	 * <p>
	 * More on input verifiers here: https://www.drdobbs.com/jvm/java-better-interfaces-via-jformattedtex/224700979?pgno=1
	 * Some discussion on dates and validation here: https://docs.oracle.com/javase/tutorial/uiswing/misc/focus.html#inputVerification
	 */

	public class FormattedTextFieldVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {

			boolean result = false;

			if (input instanceof SSFormattedTextField) {
				logger.debug(getColumnForLog() + ": Instance of SSFormattedTextField.");
				SSFormattedTextField ssftf = (SSFormattedTextField) input;
				try {
					logger.debug(getColumnForLog() + ": Text in Formatted Text Field is " + ssftf.getText() + ".");
					// this will throw a parse exception if something goes wrong
					ssftf.commitEdit();
					
					// get current value
					Object value = (ssftf.getValue());

					// now perform custom checks
					result = validateField(value);
					
					// update text color for negatives
					if (result) {
						updateTextColor(value);
					}

				} catch (java.text.ParseException pe) {
					logger.warn(getColumnForLog() + ": Parse Exception at " + pe.getErrorOffset() + ".", pe);
				}
			}

			// Update background color to RED for invalid value.
			// Also force foreground color to BLACK in case it was previous RED (negative number)
			// If value is valid, the background color will change when focus is lost.
			if (result==false) {
				setBackground(Color.RED);
				setForeground(Color.BLACK);
			}

			return result;
		}
	}

//	KEEPING updateRowSet in comments for reference.
//	
//	/**
//	 * Updates the bound column in the rowset with the specified value
//	 * 
//	 * @param _aux - value with which the rowset column has to be updated
//	 * @throws SQLException SQLException
//	 */
//	private void updateRowSet(final Object _aux) throws SQLException {
//		
//		// THIS SHOULD ALL BE HANDLED BY SSRowSet.updateColumnText()
//		switch (this.colType) {
//
//		case java.sql.Types.ARRAY:// 2003
//			break;
//
//		case java.sql.Types.BINARY:// -2
//			break;
//
//		case java.sql.Types.BIT:// -7
//			this.rowset.updateBoolean(this.columnName, ((Boolean) _aux).booleanValue());
//			break;
//
//		case java.sql.Types.BLOB:// 2004
//			break;
//
//		case java.sql.Types.BOOLEAN:// 16
//			this.rowset.updateBoolean(this.columnName, ((Boolean) _aux).booleanValue());
//			break;
//
//		case java.sql.Types.CLOB:// 2005
//			break;
//
//		case java.sql.Types.DATALINK:// 70
//			break;
//
//		case java.sql.Types.DATE:// 91
//			this.rowset.updateDate(this.columnName, new java.sql.Date(((java.util.Date) _aux).getTime()));
//			break;
//
//		case java.sql.Types.DECIMAL:// 3
//		case java.sql.Types.NUMERIC:
//		case java.sql.Types.BIGINT:
//		case java.sql.Types.DOUBLE:
//		case java.sql.Types.FLOAT:
//		case java.sql.Types.INTEGER:
//		case java.sql.Types.REAL:
//		case java.sql.Types.SMALLINT:
//		case java.sql.Types.TINYINT:
//			if (_aux instanceof java.math.BigDecimal) {
//				this.rowset.updateDouble(this.columnName, ((Double) _aux).doubleValue());
//			} else if (_aux instanceof Double) {
//				this.rowset.updateDouble(this.columnName, ((Double) _aux).doubleValue());
//			} else if (_aux instanceof Float) {
//				this.rowset.updateFloat(this.columnName, ((Float) _aux).floatValue());
//			} else if (_aux instanceof Integer) {
//				this.rowset.updateInt(this.columnName, ((Integer) _aux).intValue());
//			} else if (_aux instanceof Long) {
//				this.rowset.updateLong(this.columnName, ((Long) _aux).longValue());
//			} else {
//				System.out.println("Value aux is of unknown type......unable to update database");
//			}
//
//			break;
//
//		case java.sql.Types.DISTINCT:// 2001
//			break;
//
//		case java.sql.Types.JAVA_OBJECT:// 2000
//			break;
//
//		case java.sql.Types.LONGVARBINARY:// -4
//		case java.sql.Types.VARBINARY:// -3
//			break;
//
//		case java.sql.Types.VARCHAR://
//			System.out.println("VARCHAR --> updateString()");
//			this.rowset.updateString(this.columnName, _aux.toString());
//			break;
//
//		case java.sql.Types.LONGVARCHAR:// -1
//			System.out.println("LONGVARCHAR --> updateString()");
//			this.rowset.updateString(this.columnName, _aux.toString());
//			break;
//
//		case java.sql.Types.CHAR:// 1
//			System.out.println("CHAR --> updateString()");
//			this.rowset.updateString(this.columnName, _aux.toString());
//			break;
//
//		case java.sql.Types.NULL:// 0
//			break;
//
//		case java.sql.Types.OTHER:// 1111
//			break;
//
//		case java.sql.Types.REF:// 2006
//			break;
//
//		case java.sql.Types.STRUCT:// 2002
//			break;
//
//		case java.sql.Types.TIME:// 92
//			System.out.println("TIME --> updateTime()");
//			System.out.println("TIME : " + _aux.getClass().getName());
//			this.rowset.updateTime(this.columnName, new java.sql.Time(((java.util.Date) _aux).getTime()));
//			break;
//
//		case java.sql.Types.TIMESTAMP:// 93
//			System.out.println("TIMESTAMP --> updateTimestamp()");
//			System.out.println("TIMESTAMP : " + _aux.getClass().getName());
//			this.rowset.updateTimestamp(this.columnName, new java.sql.Timestamp(((java.util.Date) _aux).getTime()));
//			break;
//
//		default:
//			System.out.println("============================================================================");
//			System.out.println("Unknown column type");
//			System.out.println("default = " + this.colType);
//			System.out.println("columnName = " + this.columnName);
//			System.out.println("============================================================================");
//			break;
//		}
//	}

	/**
	 * Checks if the value is valid of the component. Override for custom validations
	 * not handled by formatter / formatter factory.
	 * 
	 * @param _value - value to be validated
	 * @return returns true if the value is valid else false
	 */
	public boolean validateField(final Object _value) {

		
		// TODO May want to add null check here or let SSRowSet handle. Hard to enforce if method overridden.
		//if (this.getAllowNull() == false && _value == null)
		//	return false;
			
		// RETURN
			return true;
	}
	
	/**
	 * Sets text color to red for negative numbers, otherwise black.
	 * 
	 * @param _value - value to be validated
	 */
	public void updateTextColor(final Object _value) {
	
		if ((_value instanceof Double && (Double) _value < 0.0)
				|| (_value instanceof Float && (Float) _value < 0.0)
				|| (_value instanceof Long && (Long) _value < 0)
				|| (_value instanceof Integer && (Integer) _value < 0)) {
			this.setForeground(Color.RED);
		} else {
			this.setForeground(Color.BLACK);
		}
		
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
	 * Setter for property nullable.
	 * 
	 * @param _nullable New value of property nullable.
	 */
	@Deprecated
	public void setNullable(final boolean _nullable) {
		
		setAllowNull(_nullable);

	}

	/**
	 * Sets the value that will be formatted to null
	 */
	public void cleanField() {
		setValue(null);
	}

    /**
	 * Adds any necessary listeners for the current SwingSet component. These will trigger changes
	 * in the underlying RowSet column.
	 */
	@Override
	public void addSSComponentListener() {
		addPropertyChangeListener(ssFormattedValueListener);
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
		// See https://docs.oracle.com/javase/8/docs/api/javax/swing/JFormattedTextField.html
		setInputVerifier(new FormattedTextFieldVerifier());
		
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
	 * Removes any necessary listeners for the current SwingSet component. These will trigger changes
	 * in the underlying RowSet column.
	 */
	@Override
	public void removeSSComponentListener() {
		removePropertyChangeListener(ssFormattedValueListener);
	}

	/**
	 * Sets the SSCommon data member for the current Swingset Component.
	 * 
	 * @param _ssCommon shared/common SwingSet component data and methods
	 */
	@Override
	public void setSSCommon(SSCommon _ssCommon) {
		ssCommon = _ssCommon;
	}


	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * the object obtained from getValue()
	 * <p>
	 * Calls to this method should be coming from SSCommon and should already have
	 * the Component listener removed
	 */
	@Override
	public void updateSSComponent() {

		// DbToFm() was doing this work previously and had some special handling for dates, etc.
		
		// It is OK to pass a String to SSFormattedTextField or a child class expecting a string (e.g., SSCuitField), but for
		// child classes expecting a number format (e.g., SSCurrencyField) then it will not accept a string an an exception will be thrown.
		//
		// Previously, data type conversions for the component and rowset were handled with DbToFm() and updateRowSet().
		
			//Object oValue = null;
			Object newValue = null;
			
			// TODO Review date/time handling: https://stackoverflow.com/questions/21162753/jdbc-resultset-i-need-a-getdatetime-but-there-is-only-getdate-and-gettimestamp

			try {
				// IF THERE ARE NO RECORDS OR THE COLUMN VALUE IS NULL SET THE FIELD TO NULL AND RETURN
				if (getSSRowSet().getColumnCount()==0 || getSSRowSet().getObject(getBoundColumnName()) == null) {
					setValue(null);
					return;
				}
				
				final JDBCType jdbcType = getBoundColumnJDBCType();
				String columnName = getBoundColumnName();
	
 				// SQL TO JAVA CONVERSIONS: https://stackoverflow.com/questions/5251140/map-database-type-to-concrete-java-class
				//
				// 2020-09-14: Will use java <-> SQL mapping in JDBC to get appropriate Java object and then exclude ones that would cause a problem for JFormattedTextField
				// Based on: https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#getObject-java.lang.String-
				//
				// getObject() will return the given column as a Java object. JDBC specification should contain the mappings for built in types. 
				newValue = getSSRowSet().getObject(columnName);
				
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
				if (newValue instanceof String ||
						newValue instanceof Boolean ||
						newValue instanceof Float ||
						newValue instanceof Double ||
						newValue instanceof Integer ||
						newValue instanceof Long ||
						newValue instanceof java.math.BigDecimal ||
						newValue instanceof java.sql.Date ||
						newValue instanceof java.sql.Time ||
						newValue instanceof java.sql.Timestamp) {
					
					setValue(newValue);
				} else {
					logger.error(getColumnForLog() + ": JDBCType of " + jdbcType.toString() + " was cast to unsupported type of " + newValue.getClass().getName() + " based on JDBC connection getTypeMap().");
				}

			} catch (java.sql.SQLException sqe) {
				logger.error(getColumnForLog() + ": SQL Exception while updating rowset from formatted component.", sqe);
				setValue(null);
			}
	
			// SET TEXT COLOR TO RED FOR ANY NEGATIVE NUMBERS
				updateTextColor(newValue);


	}
}
