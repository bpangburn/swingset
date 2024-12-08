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
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.math.BigDecimal;

import javax.swing.text.MaskFormatter;

import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.decorators.TextDecorationStyle;
import com.nqadmin.swingset.decorators.TextDecorator;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.datasources.ConvertType.checkConvertToJdbcType;
import static com.nqadmin.swingset.datasources.ConvertType.convertObjectType;
import static com.nqadmin.swingset.utils.SSUtils.sf;

// TODO: Review state transitions (where it can happen).
//		 Make sure to decorate at these points.
// TODO: Remove extraneous decorate.

/**
 * SSFormattedTextField extends the JFormattedTextField.
 * This is the pivotal class for this package. It operates as a {@link SSComponentInterface}.
 * It locks focus while data is invalid and updates the database while editing.
 *
 * Note {@link #isAllValid()} is used to do validation; it does
 * {@linkplain #isDataValid() } and maybe {@linkplain SSCommon#validate() }.
 *
 * The component can implement {@link #componentValidate() } to add additional
 * checks (like date validation) beyond what is provided by the
 * Formatter/FormatterFactory (which generally only handles display of values
 * and/or character masks). Add application validity checks with
 * {@link SSCommon#setValidator(com.nqadmin.swingset.decorators.Validator)}.
 *
 * @see https://docs.oracle.com/javase/8/docs/api/javax/swing/JFormattedTextField.html
 * @see https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/javax/swing/JFormattedTextField.html
 * @see {@link FormattedTextFieldVerifier} which locks focus while data is invalid.
 * @see {@link SSFormattedTextFieldListener} which may update the database.
 */

@SuppressWarnings("serial")
public class SSFormattedTextField extends JFormattedTextField
		implements SSComponentInterface {

	/**
	 * This InputVerifier locks the focus down while the 
	 * JFormattedTextField has invalid data.
	 * If non empty text do stringToValue validation check, then use
	 * {@link #isAllValid()} for more validation checks. If the data is valid
	 * and the value is null then do {@link #setValue(null) } to make sure the
	 * null formatter is used subsequently.
	 * <p>
	 * If weird errors occur, return true to avoid a focus lock hang.
	 */
	public class FormattedTextFieldVerifier extends InputVerifier
	{
		/**
		 * Check component text valid; allow focus change if it is.
		 * If weird errors are encountered, allow focus to change
		 * so focus doesn't get stuck.
		 * @param input Component to be validated/verified
		 * @return true if the input is valid, otherwise false
		 */
		@Override
		public boolean verify(final JComponent input)
		{
			// assert input == SSFormattedTextField.this; 
			final SSFormattedTextField ftf = (SSFormattedTextField) input;
			Object value = null;
			String formattedText = ftf.getText();
			AbstractFormatter formatter = ftf.getFormatter();
			
			logger.log((formatter == null || formattedText == null) ? Level.ERROR : DEBUG,
					()->sf("FormattedText %s, Formatter %s",
							getColumnForLog(), formatter == null ? null
									: formatter.getClass().getSimpleName()));
			if (formatter == null || formattedText == null)
				return true; // Impossible. Just get out and let focus change.
			
			boolean ok = true;
			// Suppress "value" property change event if a call is made to setValue()
			verifyingText = true;
			try {
				if (formattedText.isBlank())
					logger.log(DEBUG, "formattedText is blank");
				try {
					// Convert the text looking for ParseError. Not like commitEdit().
					value = formatter.stringToValue(formattedText);
				} catch (ParseException pe) {
					// Generally bad user input.
					String finalFormattedText = formattedText;
					logger.log(DEBUG, ()->sf("%s: '%s' Parse Exception offset %s.",
							getColumnForLog(), finalFormattedText, pe.getErrorOffset()));
					ok = false;
				}
				
				// Perform component and custom validation.
				// TODO: ok = getSSCommon.decorate();??? But still want exit decorate
				if (ok)
					ok = isAllValid().all();
				// If ok with null, make sure to use the null formatter. Needed?
				if (ok && value == null)
					ftf.setValue(null); // Note: "verifyingText" skips pce.
				// Update text decoration, e.g. red for negative.
				if (ok)
					updateTextDecorator();
			} catch (final Exception e) {
				// TODO: Not right. What runtime exceptions should be looked for?
				logger.log(Level.ERROR, getColumnForLog() + ": PROGRAM/RUNTIME ERROR");
				ok = false;
			} finally {
				// Stop supressing "value" property change event handling.
				verifyingText = false;
			}
			getSSCommon().decorate();
			return ok;
		}
	}

	/**
	 * Component's property change event handler.
	 * Only handles "value" property.
	 */
	protected class SSFormattedTextFieldListener implements PropertyChangeListener {
		/**
		 * @param pce property change event
		 */
		@Override
		public void propertyChange(final PropertyChangeEvent pce) {
			if (pce.getPropertyName().equals("value"))
				handleValuePropertyChange(pce);
		}
	}

	/**
	 * Handle a "value" property change event; used to propagate changes back to bound
	 * database column. Avoid cascading events and always decorate.
	 * Does nothing if {@link #isAllValid()} via decorate indicates invalid.
	 * @param pce "value" from property change event
	 */
	private void handleValuePropertyChange(
			@SuppressWarnings("unused") PropertyChangeEvent pce)
	{
		final SSFormattedTextField ftf = this;
		
		// Ignore event if triggered by FormattedTextFieldVerifier.
		if (verifyingText)
			return;
		
		getSSCommon().removeRowSetListener();

		try {
			final Object currentValue = ftf.getValue();
			logger.log(INFO, ()->sf("%s: to database '%s' type %s.",
					getColumnForLog(), currentValue,
					currentValue == null ? null : currentValue.getClass().getName()));
			
			// The formatter says it's valid, but there's more to check
			if (getSSCommon().decorate())
				setBoundColumnObject(currentValue);

		} finally {
			getSSCommon().addRowSetListener();
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
	private final SSCommon ssCommon;
		
	/** Creates a new instance of SSFormattedTextField */
	public SSFormattedTextField() {
		ssCommon = finishSSCommon();
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
	@SuppressWarnings("OverridableMethodCallInConstructor")
	public SSFormattedTextField(final AbstractFormatterFactory _factory) {
		super(_factory);
		ssCommon = finishSSCommon();
	}

	// WE DON'T WANT TO REPLICATE THE JFormattedTextField CONSTRUCTOR THAT ACCEPTS
	// AN OBJECT. FOR SWINGSET THAT SHOULD BE HANDLED SEPARATELY WITH BINDING.

	/**
	 * Creates a new instance of SSFormattedTextField
	 *
	 * @param format Format used to look up an AbstractFormatter
	 */
	public SSFormattedTextField(SSFormat format) {
		super(format);
		ssCommon = finishSSCommon();
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void setFormatterFactory(AbstractFormatterFactory factory)
	{
		super.setFormatterFactory(factory);
		if (getFormatterFactory() instanceof FormatterFactory ff) {
			setSSFormat(ff.getSSFormat());
		}
		adjustFont();
	}

	// TODO: plugin for default mono font and/or if should be used.
	// Use Courier New?
	private void adjustFont() {
		if (getFormatterFactory() instanceof SSMaskFormatterFactory) {
			Font currentFont = getFont();
			Font monoFont = new Font(Font.MONOSPACED, currentFont.getStyle(), currentFont.getSize());
			setFont(monoFont);
		}
	}

	/**
	 * Sets the value that will be formatted to null.
	 * If getAllowNull() is false subclasses may chose to do something else.
	 */
	public void cleanField() {
		setValue(null);
	}

	/**
	 * Returns the ssCommon data member for the current Swingset component.
	 *
	 * @return shared/common SwingSet component data and methods
	 */
    @Override
	public SSCommon getSSCommon() {
		if (ssCommon == null)
			return partialSSCommon = SSCommon.createStart(this, partialSSCommon);
		return ssCommon;
	}

	//
	// TODO: long term get rid of this half init stuff. Maybe a builder...
	//
	private SSCommon partialSSCommon;

	/**
	 * Either return a new create ssCommon or 
	 * Only call from constructor; "ssCommon = finishInit()".
	 */
	private SSCommon finishSSCommon() {
		SSCommon rv = SSCommon.createFinish(this, partialSSCommon);
		partialSSCommon = null;
		return rv;
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
				if (getSSCommon().getDecorator() instanceof TextDecorator td) {
					// Turn off any text decorations while focused
					td.decorateText(TextDecorationStyle.RESET);
				}

				SwingUtilities.invokeLater(() -> { selectAll(); });
			}
		});

		// Setting inputVerifier to lock focus until valid.
		setInputVerifier(new FormattedTextFieldVerifier());

		addPropertyChangeListener("editValid", (e)->{
			logger.log(TRACE, sf("editValid: isValid %s", e.getNewValue()));
			getSSCommon().decorate();
		});
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
	 * the object obtained from getValue(). Currently only numeric, boolean and date
	 * types are handled.
	 * <p>
	 * Calls to this method should be coming from SSCommon and should already have
	 * the Component listener removed.
	 *
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B.3 JDBC Types Mapped to Java Object Types
	 */
	@Override
	public void updateSSComponent() {

		// TODO: put discussion of type handling/mapping elsewhere,
		//		 and reference it from here.

		// It is OK to pass a String to SSFormattedTextField or a child class expecting a string (e.g., SSCuitField), but for
		// child classes expecting a number format (e.g., SSCurrencyField) then it will not accept a string an an exception will be thrown.
		//
		// Previously, data type conversions for the component and rowset were handled with DbToFm() and updateRowSet().

		// TODO Review date/time handling: https://stackoverflow.com/questions/21162753/jdbc-resultset-i-need-a-getdatetime-but-there-is-only-getdate-and-gettimestamp

		// SQL TO JAVA CONVERSIONS: https://stackoverflow.com/questions/5251140/map-database-type-to-concrete-java-class
		//
		// 2020-09-14: Will use java <-> SQL mapping in JDBC to get appropriate Java object and then exclude ones that would cause a problem for JFormattedTextField
		// Based on: https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#getObject-java.lang.String-
		//
		// getObject() will return the given column as a Java object. JDBC specification should contain the mappings for built in types.

		try {
			do {
				// If no records, no columns, bail.
				// TODO: is this check needed?
				if ( getRowSet().getRow() < 1
						|| RowSetOps.getColumnCount(getRowSet())==0) {
					// TODO: should this check allow null?
					setValue(null);
					break;
				}
				
				final Object dbValue = getBoundColumnObject();

				// If record field null then set value null, bail.
				if (dbValue == null ) {
					// TODO: should this check allow null?
					setValue(null);
					break;
				}

				final JDBCType jdbcType = getBoundColumnJDBCType();
				if (!(checkConvertToJdbcType(jdbcType, dbValue.getClass(), null)))
					logger.log(Level.ERROR, ()->sf("%s CAN'T CONVERT %s to %s",
							getColumnForLog(), jdbcType, dbValue.getClass().getName()));
				
				// NOTE: H2's "rx.updateObject(idx, obj)" converts Date
				//		 to sql.Timestamp which is converted on commit (I guess)
				//		 to sql.Date
				// Avoid need for "(newValue instanceof Date) ||".
				// See RowSetOps.updateColumnObject().
				final Object newValue = Boolean.TRUE
						? convertObjectType(dbValue, jdbcType) : dbValue;

				// Only support some Java types for JFormattedTextFields.
				// TODO: Wonder if "newValue instanceof Number" would work.
				// TODO: What if an installed formatter doesn't handle the type?
				//		 Where is that checked.
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
					Object finalNewValue = newValue;
					logger.log(DEBUG, ()->sf("%s: getObject() - %s",
							getColumnForLog(), finalNewValue));
					setValue(newValue);
				} else {
					logger.log(Level.ERROR, sf("%s: JDBCType %s to %s not supported",
							getColumnForLog(), jdbcType,
							newValue != null ? newValue.getClass().getName() : null));
					//
					// TODO: there is no "setValue()". Should do "setValue(null)"?
					//
				}
			} while (false);
		} catch (SQLException sqe) {
			logger.log(Level.ERROR, sf("%s: Exception updating rowset.",
					getColumnForLog(), sqe));
			setValue(null);
		}
		updateTextDecorator(); // For example: color red for negative number
		getSSCommon().decorate();
	}

	private boolean enableTextDecorator = true;
	/**
	 * Set/reset the flag to enable text decoration.
	 * @param flag
	 */
	public final void setTextDecoratorEnabled(boolean flag) {
		enableTextDecorator = flag;
	}

	/**
	 * Whether or not text decoration is enabled.
	 * @return true if enabled
	 */
	public final boolean isTextDecoratorEnabled() {
		return enableTextDecorator;
	}

	/**
	 * Decorate text based on current value of this component.
	 * The following default text decorator distinguishes negative
	 * numbers. Typically red for negative numbers, otherwise black.
	 * Non numeric values are ignored.
	 * Override this method to do other things.
	 */
	// Note: this might make more sense in NumberField.
	// TODO: should this be updated while focused and any value change?
	// TODO: should this be in SSComponentInterface?
	public void updateTextDecorator() {
		if (!isTextDecoratorEnabled())
			return;
		Object value = getValue();
		if (getSSCommon().getDecorator() instanceof TextDecorator textDecorator) {
			boolean isNeg = switch(value) {
			case Double val ->		val < 0.0;
			case Float val ->		val < 0.0;
			case Long val ->		val < 0;
			case Integer val ->		val < 0;
			case BigDecimal val ->	val.signum() < 0;
			case null, default ->	false;
			};

			textDecorator.decorateText(isNeg ? TextDecorationStyle.NEGATIVE_NUMBER
									   : TextDecorationStyle.RESET);
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDataValid() {
		return isEditValid();
	}

	/**
	 * Determine if there are user input characters. A formatted text field might
	 * contain only the format pattern and no input characters. If there is
	 * insufficient information to determine the result, true is returned.
	 * @return true if there is user input
	 */
	public boolean containsUserText() {

		AbstractFormatter f = getFormatter();
		return switch (f) {
		case MaskFormatter mf -> FormatterAssist.containsUserText( getText(), mf);
		default -> {
			if (f instanceof FormatterAssist fa) {
				System.err.printf("NOTICE ME HERE: %s", fa);
			}
			yield !getText().isEmpty();	// TODO: isBlank()?
		}
		};
	}

	//
	// Handle changes that might affect value/AllowNull
	//

	/**
	 * Set the text fields value.
	 * @param value set text field to this value
	 */
	@Override
	public void setValue(Object value) {
		logger.log(TRACE, ()->sf("new value %s", value));
		checkNeedsNullFormatter();	// TODO: not needed? depends on metadata change
		super.setValue(value);
		getSSCommon().decorate();
	}

	/**
	 * If metadataChange, might affect AllowNull.
	 */
	@Override
	public void metadataChange() {
		SSComponentInterface.super.metadataChange();
		checkNeedsNullFormatter();
	}

	/**
	 * Whether or not this component accepts NULL.
	 * @param allowNull if true, null is allowed
	 */
	@Override
	public void setAllowNull(boolean allowNull) {
		SSComponentInterface.super.setAllowNull(allowNull);
		checkNeedsNullFormatter();
	}
	
	private void checkNeedsNullFormatter() {
		FormatterAssist.adjustNullFormatter(this);
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("%s{text=%s, %s}", getClass().getSimpleName(),
				getText(), SSUtils.ssComponentToString(this));
	}
	
}
