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
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.formatting;

import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import javax.swing.text.MaskFormatter;

import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.navigate.RowSetState;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;
import com.nqadmin.swingset.decorators.TextDecorationStyle;
import com.nqadmin.swingset.decorators.TextDecorator;
import com.nqadmin.swingset.decorators.Decorator;

import static com.nqadmin.swingset.formatting.SSMaskFormatterFactory.containsData;
import static com.nqadmin.swingset.utils.SSUtils.sf;

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

@SuppressWarnings("serial")
public class SSFormattedTextField extends JFormattedTextField
		implements SSComponentInterface {

	/**
	 * Implementing an InputVerifier in order to lock the focus down while the JFormattedTextField is in
	 * an invalid edit state.
	 * 
	 * This implementation makes a call to componentValidate(), the default implementation of which just returns true.
	 * 
	 * The developer can override componentValidate() for a SwingSet formatted component to add additional checks
	 * (normally range validation) beyond what is provided by the Formatter/FormatterFactory, which generally
	 * only handles display of values and/or character masks.
	 * 
	 * This inner class performs validation via verifyField() AND calls setValue(),
	 * which triggers a RowSet update.
	 * <p>
	 * See https://docs.oracle.com/javase/8/docs/api/javax/swing/JFormattedTextField.html
	 * See https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/javax/swing/JFormattedTextField.html
	 */
	public class FormattedTextFieldVerifier extends InputVerifier
	{
		/**
		 * @param input Component to be validated/verified
		 * @return true if the input is valid, otherwise false
		 */
		@Override
		public boolean verify(final JComponent input) {

			boolean result = true;
			Object value = null;

			// Set indicator to suppress duplicate property change event if
			// a call is made to setValue()
			verifyingText = true;

			try {
				if (input instanceof SSFormattedTextField ftf) {
					logger.log(DEBUG, ()->sf("%s: Instance of SSFormattedTextField.", getColumnForLog()));

					String formattedText = ftf.getText();

					AbstractFormatter formatter = ftf.getFormatter();
					logger.log(DEBUG, ()->sf("Formatter is: %s.", formatter));
					if (formatter == null) {
						logger.log(Level.ERROR, "Null formatter encountered for formatted text field.");
					}

					if (formatter != null && formattedText != null && !formattedText.isEmpty()) {
						try {
							value = formatter.stringToValue(formattedText);
							// Apparently formatter.stringToValue(formattedText) accomplishes the same thing
							// as commitEdit(), but this approach lets us know if the formatter is null.
						} catch (ParseException pe) {
							// Changing logging from 'warn' to 'debug' since we expect a ParseException for
							// any user keystroke error.
							logger.log(DEBUG, ()->sf("%s: String of '%s' generated a Parse Exception at %s.",
									getColumnForLog(), formattedText, pe.getErrorOffset()), pe);
							result = false;
							// We're not going to call setValue(null) if result is false, rather we'll keep
							// the
							// focus in the current field.
						}
					} else {
						// value variable is set to null by default, but make a log entry
						logger.log(DEBUG, "Null formatter, empty string, or null text.");
					}

					// now perform custom validation/range checks
					if (result) {
						result = isAllValid().all();
					}

					// update text color for negatives, where applicable
					if (result) {
						updateTextDecorator(value);
					}

					// Set the value to null manually if stringToValue() was not called above, but
					// null is a valid result (e.g., result==true)
					//
					// Note that any call to setValue() in this method was triggering a second
					// property change event so we added a boolean, verifyingText, that will
					// immediately return from the second property change, without additional
					// action.
					if (result && value == null) {
						ftf.setValue(null);
					}

				}

				if (result == false) {
					forceError();
				}

			} catch (final Exception _e) {
				// Log the error and fail the validation.
				logger.log(Level.ERROR, getColumnForLog() + ": Field validation triggered an exception.", _e);
				result = false;

			} finally {
				// Update indicator used to suppress duplicate property change event if
				// a call is made to setValue()
				verifyingText = false;
			}

			// return
			return result;

		}
	}

	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column.
	 */
	protected class SSFormattedTextFieldListener implements PropertyChangeListener {
		/**
		 * @param _pce property change event
		 */
		@Override
		public void propertyChange(final PropertyChangeEvent _pce) {

			if (_pce.getPropertyName().equals("value")) {

				final SSFormattedTextField ftf = SSFormattedTextField.this;
				
				// If this property change was triggered by FormattedTextFieldVerifier
				// return with no action
				if (verifyingText || !isAllValid().all()) return;

				ssCommon.removeRowSetListener();


				final Object currentValue = ftf.getValue();
				logger.log(INFO, ()->sf("%s: Object passed to database: '%s'.",
						getColumnForLog(), currentValue));

				// TODO May want to see if we can veto invalid updates
				// 2020-12-14_BP: allow null if on insert row
				if (!getAllowNull() && (currentValue == null) && !RowSetState.isInserting(getRowSet())) {
					logger.log(WARNING, "Null value encounted, but not allowed.");
					JOptionPane.showMessageDialog(ftf, "Null values are not allowed for " + getBoundColumnName(),
							"Null Exception", JOptionPane.ERROR_MESSAGE);
				} else {

					try {
						// TODO: put the specialized handling into some kind
						//		 of data base adaptor plugin.

						// 2020-10-02_BP: Date (and presumably Time & Timestamp) fields are returned as
						// java.util.Date and Postgres JDBC doesn't know how to handle them
						// java.sql.Date, java.sql.Time, and java.sql.Timestamp are all subclasses of
						// java.util.Date
// TODO This may be where we want to check and deal with NULL				    	
						if (currentValue instanceof java.util.Date date) {
							switch (getBoundColumnJDBCType()) {
							case DATE -> getRowSet().updateObject(getBoundColumnName(),
										new java.sql.Date(date.getTime()));
							case TIME -> getRowSet().updateObject(getBoundColumnName(),
										new java.sql.Time(date.getTime()));
							case TIMESTAMP -> getRowSet().updateObject(getBoundColumnName(),
										new java.sql.Timestamp(date.getTime()));
							default -> logger.log(Level.ERROR, getColumnForLog() + ": getValue() returned a java.sql.Date, but JDBCType is "
												+ getBoundColumnJDBCType() + ". Unable to update column.");
							}
						} else {
							getRowSet().updateObject(getBoundColumnName(), currentValue);
						}

					} catch (final SQLException _se) {
						logger.log(Level.ERROR, getColumnForLog() + ": RowSet update triggered SQL Exception.", _se);
						JOptionPane.showMessageDialog(ftf, "SQL Exception encountered for " + getBoundColumnName(),
								"SQL Exception", JOptionPane.ERROR_MESSAGE);
					}
				}

				ssCommon.addRowSetListener();
			}

		}

	}
	
	/**
	 * Indicates if text is current being validated by InputVerifier. This allows
	 * suppression of second property change if a call is made to setValue().
	 */
	private boolean verifyingText = false;

	/** Logger for component */
	private static Logger logger = SSUtils.getLogger();

	/** Common fields shared across SwingSet components */
	protected final SSCommon ssCommon = new SSCommon(this);
		
	/** Creates a new instance of SSFormattedTextField */
	public SSFormattedTextField() {
	}

	/**
	 * Creates a new instance of SSFormattedTextField
	 *
	 * @param _formatter AbstractFormatter to use for formatting.
	 * 
	 * TODO: Consider using this() to force all constructors through one method
	 * so that any constructor customizations don't have to be duplicated. 
	 */
	public SSFormattedTextField(final AbstractFormatter _formatter) {
		this(new DefaultFormatterFactory(_formatter));
	}

	/**
	 * Creates a new instance of SSFormattedTextField
	 *
	 * @param _factory AbstractFormatterFactory used for formatting.
	 */
	public SSFormattedTextField(final AbstractFormatterFactory _factory) {
		this();
		setFormatterFactory(_factory);
	}

	// WE DON'T WANT TO REPLICATE THE JFormattedTextField CONSTRUCTOR THAT ACCEPTS
	// AN OBJECT. FOR SWINGSET THAT SHOULD BE HANDLED SEPARATELY WITH BINDING.

	/**
	 * Creates a new instance of SSFormattedTextField
	 *
	 * @param _format Format used to look up an AbstractFormatter
	 */
	public SSFormattedTextField(final Format _format) {
		// Don't need "this()" since _format can't access
		// things in SSFormattedTextField.
		super(_format);
	}

	/**
	 * Sets the value that will be formatted to null
	 */
	public void cleanField() {
		setValue(null);
	}

	/**
	 * Add custom focus behavior: gain selectAll, lose COMMIT_OR_REVERT.
	 * Related behavior.
	 */
	@Override
	public void customInit()
	{
		// COMMIT_OR_REVERT SHOULD ALREADY BE THE DEFAULT
		setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

		// Adding focus listener to assist with highlighting and text selection
		// addFocusListener(this);
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent fe) {
				//SSTextField.this.selectAll();
				SwingUtilities.invokeLater(() -> { selectAll(); });
			}
		});

		// Setting inputVerifier to validate field before focus is lost
		// See https://docs.oracle.com/javase/8/docs/api/javax/swing/JFormattedTextField.html
		// See https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/javax/swing/JFormattedTextField.html
		setInputVerifier(new FormattedTextFieldVerifier());

		addPropertyChangeListener("editValid", (e)->{
			// System.err.println("EditValid: " + e.getNewValue());
			//displayValidIndicator((boolean) e.getNewValue());
			logger.log(TRACE, sf("editValid: isValid %s", e.getNewValue()));
			clearForceErrorFlag();
		});

		// TODO: plugin for default mono font and if should be used.
		// Courier New?
		if (getFormatter() instanceof MaskFormatter) {
			Font currentFont = getFont();
			//Font monoFont = new Font("Monospaced", currentFont.getStyle(), currentFont.getSize());
			Font monoFont = new Font(Font.MONOSPACED, currentFont.getStyle(), currentFont.getSize());
			//String fontName = monoFont.getFontName();
			setFont(monoFont);
		}
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
			if ( getRowSet().getRow() < 1 || RowSetOps.getColumnCount(getRowSet())==0 || getRowSet().getObject(getBoundColumnName()) == null ) {
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
				
				var finalNewValue = newValue;
				logger.log(DEBUG, ()->sf("%s: getObject() - %s",
						getColumnForLog(), finalNewValue));
				setValue(finalNewValue);
			} else {
				String newValueString = newValue == null
						? "(null)"
						: newValue.getClass().getName();
				logger.log(Level.ERROR, getColumnForLog() + ": JDBCType of " + jdbcType.toString() + " was cast to unsupported type of " + newValueString + " based on JDBC connection getTypeMap().");
			}

		} catch (final java.sql.SQLException sqe) {
			logger.log(Level.ERROR, getColumnForLog() + ": SQL Exception while updating rowset from formatted component.", sqe);
			setValue(null);
		}

		// For example: color red for negative number
		updateTextDecorator(newValue);

	}
	
	/**
	 * The default border decorator sets red for negative numbers, otherwise black.
	 * A developer could override this method to do other things.
	 * 
	 * TODO: Should this apply after keytype?
	 *
	 * @param _value - value to be validated
	 */
	public void updateTextDecorator(final Object _value) {
		Decorator hl = getSSCommon().getDecorator();
		if (hl instanceof TextDecorator textDecorator) {
			TextDecorationStyle style;
			if (((_value instanceof Double) && ((Double) _value < 0.0))
					|| ((_value instanceof Float) && ((Float) _value < 0.0))
					|| ((_value instanceof Long) && ((Long) _value < 0))
					|| ((_value instanceof Integer) && ((Integer) _value < 0))) {
				style = TextDecorationStyle.NEGATIVE_NUMBER;
			} else {
				style = TextDecorationStyle.RESET;
			}
			textDecorator.decorateText(style);
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDataValid() {
		return !forceErrorState && isEditValid();
	}

	/**
	 * Determine if there are user input characters. A formatted text field might
	 * contain only the format pattern and no input characters.
	 * @return true if there is user input
	 */
	public boolean containsUserText() {
		AbstractFormatter f = getFormatter();
		boolean hasText = switch (f) {
		case MaskFormatter mf -> containsData(mf, getText());
		//case SSDefaultFormatter -> only to provide literal chars not in data?
		default -> {
			yield true;
		}
		};
		return hasText;
	}

	/** Set this if verify fails, gets cleared if "editValid" property change. */
	private boolean forceErrorState;
	private void forceError() {
		logger.log(TRACE, "forceError");
		forceErrorState = true;
		getSSCommon().decorate();
	}
	private void clearForceErrorFlag() {
		logger.log(TRACE, "clearForceErrorFlag");
		forceErrorState = false;
		getSSCommon().decorate();
	}

	//
	// Handle changes that might affect value/AllowNull
	//
	// TODO: Wonder if listeners are better? Probably not.
	//

	/**
	 * Set the text fields value.
	 * @param value set text field to this value
	 */
	@Override
	public void setValue(Object value) {
		checkNeedsNullFormatter();
		super.setValue(value);
	}

	/**
	 * If metadataChang, might affect AllowNull
	 */
	@Override
	public void metadataChange() {
		SSComponentInterface.super.metadataChange();
		checkNeedsNullFormatter();
	}

	/**
	 * 
	 * @param _allowNull if true, null are allowed
	 */
	@Override
	public void setAllowNull(boolean _allowNull) {
		// Too bad can't simply override setAllowNull,
		// guess that's a disadvantage of default methods.
		SSComponentInterface.super.setAllowNull(_allowNull);
		checkNeedsNullFormatter();
	}

	private void checkNeedsNullFormatter() {
		// NOTE: following does nothing, if not our mask formatter
		SSMaskFormatterFactory.adjustNullFormatter(this);
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("%s{text=%s, %s}", getClass().getSimpleName(),
				getText(), SSUtils.ssComponentToString(this));
	}

}
