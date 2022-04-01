/* *****************************************************************************
 * Copyright (C) 2022, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 * ****************************************************************************/
package com.nqadmin.swingset.formatting;

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.utils.SSUtils;

/**
 * A FormatterFactory, with formatters based on MaskFormatter, which uses
 * getAllowNull() from the SSFormattedTextField to determine when
 * a NullFormatter should be provided. In addition, the 
 * formatted text field is monitored and its value is changed
 * to null/String as needed. This factory only specifies the default formatter,
 * so edit and display formatters use the default formatter.
 * <p>
 * These formatters internally use a String value and the supplied converter
 * is used to do the final conversion to a value of the correct type.
 * Typically an AbstractFormatter is
 * provided to convert objects to/from strings; see the code snippet below.
 * The converter may not be needed if the value objects are a string.
 * <p>
 * Here's a code snippet that creates a {@link SSMaskFormatterFactory}
 * that handles a {@link java.util.Date} field.
 * <pre>{@code
 *     new SSMaskFormatterFactory.Builder<>("##/##/####").maskLiterals("/")
 *             .converter(new DateFormatter(new SimpleDateFormat("MMddyyyy")))
 *             .placeholder('_').build();
 * }</pre>
 * With this snippet, if the text field has AllowNull true and the Value is
 * null, then the text field is empty/blank; if the user then enters "1", 
 * the text field displays "1_/__/____"/; if then backspace, the field
 * becomes blank.
 * Note the maskLiterals: the placeholder and maskLiterals are used
 * when determining if the text field's value should be null;
 * in particular, when it would contain "__/__/____" the value is set to null
 * and the text field is empty; though conceivable, it is not simple to
 * automatically determine the literals.
 * Also note that the converter's format does not
 * contain "/" because in this example the factory's
 * {@link MaskFormatter}'s ValueContainsLiteralCharacters is false.
 * <p>
 * Finer control over the edit input characters can be obtained by
 * overriding the Builder's getSSMaskFormatter to create a
 * subclass of SSMaskFormatter which has a custom DocumentFilter. For example:
 * <pre>{@code
 * static class CustomBuilder extends SSMaskFormatterFactory.Builder<CustomBuilder> {
 * 	   CustomBuilder(String mask) {
 * 	   	super(mask);
 * 	   }
 * 	   @Override
 * 	   protected SSMaskFormatter getSSMaskFormatter(
 * 	   		SSMaskFormatterFactory.Builder<?> builder) throws ParseException {
 * 	   	return new CustomSSMaskFormatter(self());
 * 	   }
 * }
 * }</pre>
 * Use this custom formatter as: {@code new CustomBuilder(formatMask)...build();}.
 * <p>
 * TODO: Should maskLiterals contain the placeholder rather than
 * assuming placeholder should be filtered out?
 */
@SuppressWarnings("serial")
public class SSMaskFormatterFactory extends DefaultFormatterFactory {

	/**
	 * Log4j Logger for component
	 */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Get a new FormatterFactory with the specified parameters.Unless noted, a parameter is used when constructing the MaskFormatter.<p>
	 * <p>
	 * TODO: extend to allow specification of a displayFormatter.
	 * @see <em>Effective Java</em> Item 2 about override.
	 * @param <T>
	 */
	public static class Builder<T extends Builder<T>> {
		// Required params
		private final String mask;
		// Optional params
		private String maskLiterals = "";
		private boolean valueContainsLiterals = false;
		private AbstractFormatter converter = null;
		private Character placeholder = null;
		private String validCharacters = null;
		private String invalidCharacters = null;

		/**
		 * Create the builder.
		 * @param mask Formatter's mask
		 */
		public Builder(String mask) {
			this.mask = mask;
		}

		/** Literals in the mask; used in determining null data.
		 * @param val
		 * @return  builder */
		public T maskLiterals(String val) { maskLiterals = val; return self(); }
		/** formatter
		 * @param val
		 * @return  builder */
		public T valueContainsLiteral(boolean val) { valueContainsLiterals = val; return self(); }
		/** Used by the mask formatter in string2Value and value2String.
		 * It's the last step in stringToValue; it produces the Value
		 * in the formatted text field.
		 * @param val
		 * @return  builder */
		public T converter(AbstractFormatter val) { converter = val; return self(); }
		/** formatter
		 * @param val
		 * @return  builder */
		public T placeholder(char val) { placeholder = val; return self(); }
		/** formatter
		 * @param val
		 * @return  builder */
		public T validCharacters(String val) { validCharacters = val; return self(); }
		/** formatter
		 * @param val
		 * @return  builder */
		public T invalidCharacters(String val) { invalidCharacters = val; return self(); }

		/** create the factory
		 * @return the factory */
		public SSMaskFormatterFactory build() { return new SSMaskFormatterFactory(this); }
		/**
		 *
		 * @return
		 */

		@SuppressWarnings("unchecked")
		protected T self() {
			return (T) this;
		}

		/**
		 * Override this to provide custom SSMaskFormatter.
		 * @param builder
		 * @return
		 * @throws ParseException 
		 */
		protected SSMaskFormatter getSSMaskFormatter(Builder<?> builder) throws ParseException {
			return new SSMaskFormatter(builder);
		}
	}

	/**
	 * Create the factory, populate it with mask formatter.
	 * @param builder
	 */

	protected SSMaskFormatterFactory(Builder<?> builder) {
		try {
			SSMaskFormatter mf = builder.getSSMaskFormatter(builder);
			setDefaultFormatter(mf);
		} catch (ParseException ex) {
			logger.error("Bad mask format: " + builder.mask);
		}
	}
	
	/**
	 * Ensure that a FormattedTextField's
	 * DefaultFormatterFactory's NullFormatter is set
	 * consistent with getAllowNull. If they do not agree, then
	 * the factory's null formatter is set/cleared as needed.
	 * 
	 * @param _ftf 
	 */
	protected static void adjustNullFormatter(SSFormattedTextField _ftf) {
		if (_ftf.getFormatterFactory() instanceof SSMaskFormatterFactory) {
			SSMaskFormatterFactory ff = (SSMaskFormatterFactory) _ftf.getFormatterFactory();
			boolean allowNull = _ftf.getAllowNull();
			boolean hasNullFormatter = ff.getNullFormatter() != null;
			if (allowNull ^ hasNullFormatter) {
				ff.setNullFormatter(allowNull ? new SSNullFormatter() : null);
			}
		}
	}

	/** Uses setEditValid method to check that formatter should flip. */
	@SuppressWarnings("serial")
	protected static class SSMaskFormatter extends MaskFormatter {

		private final AbstractFormatter converter;
		private final String maskLiterals;

		/**
		 * 
		 * @param builder
		 * @throws ParseException 
		 */
		protected SSMaskFormatter(Builder<?> builder) throws ParseException {
			super(builder.mask);
			maskLiterals = builder.maskLiterals;
			setValueContainsLiteralCharacters(builder.valueContainsLiterals);
			converter = builder.converter;
			if (builder.placeholder != null) {
				setPlaceholderCharacter(builder.placeholder);
			}
			if (builder.validCharacters != null) {
				setValidCharacters(builder.validCharacters);
			}
			if (builder.invalidCharacters != null) {
				setValidCharacters(builder.invalidCharacters);
			}
			
			// NOTE:  following required to flip as needed
			// DO NOT CHANGE
			setCommitsOnValidEdit(true);

			// DO NOT CHANGE
			setValueClass(String.class);
		}

		/**
		 *
		 * @return
		 */
		public AbstractFormatter getConverter() {
			return converter;
		}

		/**
		 *
		 * @return
		 */
		public String getLiterals() {
			return maskLiterals;
		}

		/**
		 * If the value is not a String and there is a converter,
		 * then first convert the value before super.valueToString.
		 * @param value
		 * @return String representation of the value
		 * @throws ParseException 
		 */
		@Override
		public String valueToString(Object value) throws ParseException {
			String s = "";
			if (value != null) {
				if (value instanceof String) {
					// handle the case where where was null formatter
					s = (String) value;
				} else {
					if (converter != null) {
						s = converter.valueToString(value);
					} else {
						s = value.toString();
					}
				}
			}
			s = super.valueToString(s);
			return s;
		}

		/**
		 * First convert the string with super.stringToValue,
		 * then use the converter (if there is one) to create
		 * the value object.
		 * @param s
		 * @return
		 * @throws ParseException 
		 */
		@Override
		public Object stringToValue(String s) throws ParseException {
			if (s == null || s.trim().isEmpty()) {
				return null;
			}
			Object v = super.stringToValue(s);
			if (converter != null) {
				v = converter.stringToValue((String) v);
			}
			return v;
		}

		/**
		 * This method is invoked by the formatter when it is almost done.
		 * After super.setEditValid, if the text field does not have
		 * user input, set the value to null (which switches the formatter).
		 * @param valid
		 */
		@Override
		protected void setEditValid(boolean valid) {
			super.setEditValid(valid);
			// Check for empty string input.
			// If empty and allows null, flip to NullFormatter
			final JFormattedTextField ftf = getFormattedTextField();
			if (ftf instanceof SSFormattedTextField) {
				// If doesn't allow null, don't even consider switching value
				if (!((SSFormattedTextField)ftf).getAllowNull()) {
					return;
				}
				if (containsData(ftf.getText())) {
					return;
				}
				ftf.setValue(null);
			}
		}

		/**
		 * Check the string for user data.
		 * @param _text generally formatted text field
		 * @return true if there is user input in the text
		 */
		protected boolean containsData (String _text) {
			// first remove placeholder characters from the text
			String text = _text.replace(String.valueOf(getPlaceholderCharacter()), "");
			for (int i = 0; i < text.length(); i++) {
				String c = String.valueOf(text.charAt(i));
				if (!maskLiterals.contains(c)) {
					// encountered some data, not empty
					return true;
				}
			}
			return false;
		}
	}

	/** use setEditValid method to check that formatter should flip */
	@SuppressWarnings("serial")
	protected static class SSNullFormatter extends DefaultFormatter {

		/**
		 * The null formatter.
		 */
		public SSNullFormatter() {
			// DO NOT CHANGE
			setValueClass(String.class);
			// DO NOT CHANGE
			setCommitsOnValidEdit(true);
		}

		/**
		 * This method is invoked by the formatter when it is almost done.
		 * After super.setEditValid, if the text field is a String
		 * set the value to the String (which switches the formatter).
		 * @param valid
		 */
		@Override
		protected void setEditValid(boolean valid) {
			super.setEditValid(valid);
			
			// if a character was added to the Null Formatter,
			// then use setValue to flip to the MaskFormatter.
			final JFormattedTextField ftf = getFormattedTextField();
			Object value = ftf.getValue();
			// May not need to check for SSNullFormatter, but things change :-)
			if(value instanceof String
					&& ftf.getFormatter() instanceof SSNullFormatter) {
				try {
					ftf.setValue(value);
					// If ftf is still the null formatter,
					// then MaskFormatter didn't like the value;
					// notify and get out.
					if (ftf.getFormatter() instanceof SSNullFormatter) {
						invalidEdit();
						return;
					}
					ftf.getFormatter().valueToString(value);
					// Attempt to put the caret after the character.
					try {
						ftf.setCaretPosition(((String)value).length());
					} catch (IllegalArgumentException ex) {
					}
				} catch(ParseException ex) {
					// mask formatter (probably) got an exception,
					// back to the null formatter
					ftf.setValue(null);
				}
			}
		}
	}
	
}
