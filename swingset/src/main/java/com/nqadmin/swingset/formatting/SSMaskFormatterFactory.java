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
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.formatting;

import java.text.ParseException;

import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.MaskFormatter;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.swing.JFormattedTextField;

import com.nqadmin.swingset.datasources.RSC;

import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;

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
 * {@snippet :
 *     new SSMaskFormatterFactory.Builder<>("##/##/####")
 *             .converter(new DateFormatter(new SimpleDateFormat("MMddyyyy")))
 *             .placeholder('_').build();
 * }
 * With this snippet, if the text field has AllowNull true and the Value is
 * null, then the text field is empty/blank; if the user then enters "1", 
 * the text field displays "1_/__/____"/; if then backspace, the field
 * becomes blank.
 * The placeholder and maskLiterals are used
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
 * {@snippet class=SSMaskFormatterFactorySnippets region=init1}
 * <p>
 */
@SuppressWarnings("serial")
public class SSMaskFormatterFactory extends FormatterFactory
{
	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();
	
	/**
	 * To build a new FormatterFactory with the specified parameters. Unless noted,
	 * a parameter is used when constructing the MaskFormatter.
	 *
	 * @param <T>
	 * @see <em>Effective Java</em> Item 2 about override.
	 * @see <a href="https://www.baeldung.com/java-builder-pattern-inheritance">
	 * Baeldung's java-builder-pattern-inheritance</a>
	 */
	public static class Builder<T extends Builder<T>>
			extends FormatterFactory.Builder<T>
	{
		// Required params
		private final String mask;
		// Optional params
		private String validCharacters = null;
		private String invalidCharacters = null;
		private String placeholder = null;
		private Character placeholderCharacter = null;
		private boolean valueContainsLiterals = false;
		private BiFunction<List<String>, RSC, Boolean> stringValidator;

		/**
		 * Create the builder.
		 * @param mask Formatter's mask
		 */
		public Builder(String mask) {
			this.mask = mask;
		}

		/** formatter
		 * @param val
		 * @return  builder */
		public T validCharacters(String val) { validCharacters = val; return self(); }
		/** formatter
		 * @param val
		 * @return  builder */
		public T invalidCharacters(String val) { invalidCharacters = val; return self(); }
		/** formatter
		 * @param val
		 * @return  builder */
		public T placeholder(String val) { placeholder = val; return self(); }
		/** formatter
		 * @param val
		 * @return  builder */
		public T placeholderCharacter(char val) { placeholderCharacter = val; return self(); }
		/** formatter
		 * @param val
		 * @return  builder */
		public T valueContainsLiterals(boolean val) { valueContainsLiterals = val; return self(); }
		/**
		 * Function that validates a string for the SSComponent, function returns
		 * false if not valid.
		 * After stringToValue, before converter.
		 * @param val function used to validate
		 * @return builder
		 */
		public T stringValidator(BiFunction<List<String>, RSC, Boolean> val)
		{ stringValidator = val; return self(); }

		/** create the factory
		 * @return the factory */
		public SSMaskFormatterFactory build() {
			return new SSMaskFormatterFactory(this);
		}

		/**
		 * Override this to provide custom SSMaskFormatter.
		 * @return
		 * @throws ParseException 
		 */
		// TODO: use builder not getConver...
		protected SSMaskFormatter getSSMaskFormatter() throws ParseException
		{
			Objects.requireNonNull(mask, "must specify mask");
			SSMaskFormatter mf = new SSMaskFormatter(this);
			if (validCharacters != null) {
				mf.setValidCharacters(validCharacters);
			}
			if (invalidCharacters != null) {
				mf.setValidCharacters(invalidCharacters);
			}
			if (placeholder != null) {
				mf.setPlaceholder(placeholder);
			}
			if (placeholderCharacter != null) {
				mf.setPlaceholderCharacter(placeholderCharacter);
			}
			mf.setValueContainsLiteralCharacters(valueContainsLiterals);

			// NOTE:  following required to flip as needed
			// DO NOT CHANGE
			mf.setCommitsOnValidEdit(true);

			// DO NOT CHANGE --- TODO: NEEDED?
			mf.setValueClass(String.class);
			return mf;
		}
	}

	/**
	 * Create the factory, populate it with mask formatter.
	 * @param builder
	 */
	protected SSMaskFormatterFactory(Builder<?> builder) {
		super(builder);
		try {
			SSMaskFormatter mf = builder.getSSMaskFormatter();
			setDefaultFormatter(mf);
		} catch (ParseException ex) {
			logger.log(ERROR, "Bad mask format: " + builder.mask);
		}
	}

	/**
	 * For a MaskFormatter simply slam the stringValue into the FormattedTextField.
	 * {@inheritDoc }
	 */
	@Override
	public void switchToNonNullValue(JFormattedTextField ftf, String stringValue)
			throws ParseException
	{
		ftf.setValue(stringValue);
		if (logger.isLoggable(TRACE)) {
			Object v = ftf.getValue();
			logger.log(TRACE, ()->sf("switch: '%s' to %s %s", stringValue,
					v != null ? v.getClass().getSimpleName() : null, v));
		}
	}

	/** Uses setEditValid method to check that formatter should flip. */
	@SuppressWarnings("serial")
	public static class SSMaskFormatter extends MaskFormatter implements FormatterAssist {

		/** MaskFormatter valid formatted input chars */
		public static final String FORMATTING_CHARS = "#ULA?*H";

		private final AbstractFormatter converter;
		private final BiFunction<List<String>, RSC, Boolean> stringValidator;

		/**
		 * Create the factory's MaskFormatter.
		 * @param builder (null is for testing)
		 * @throws ParseException 
		 */
		protected SSMaskFormatter(Builder<?> builder) throws ParseException
		{
			super(builder != null ? builder.mask : "");
			if (builder != null) {
				this.converter = builder.getConverter();
				this.stringValidator = builder.stringValidator;
			} else {
				this.converter = null;
				this.stringValidator = null;
				
			}
		}

		/**
		 *
		 * @return
		 */
		@Override
		public AbstractFormatter getConverter() {
			return converter;
		}

		/**
		 *
		 * @return
		 */
		public BiFunction<List<String>, RSC, Boolean> getStringValidator() {
			return stringValidator;
		}

		/**
		 * The Format of the FormattedTextField.
		 * @return format
		 */
		@Override
		public SSFormat getSSFormat() {
			if(getFormattedTextField() instanceof SSFormattedTextField ftf)
				return ftf.getSSFormat();
			return null;
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
				if (value instanceof String string) {
					// handle the case where where was null formatter
					s = string;
				} else {
					if (getConverter() != null) {
						s = getConverter().valueToString(value);
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
		 * the value object. This may be setEditValid arg.
		 * @param masked
		 * @return value for SSFTF
		 * @throws ParseException 
		 */
		@Override
		public Object stringToValue(String masked) throws ParseException {
			if (masked == null || masked.isBlank()) {
				if(getFormattedTextField() instanceof SSFormattedTextField ftf
						&& !ftf.getAllowNull())
					throw new ParseException("Null value not allowed", 0);
				return null;
			}
			// "string" is without mask caracters.
			String string = (String) super.stringToValue(masked);

			Object value = string;
			if (getConverter() != null) {
				value = getConverter().stringToValue(string);
			}
			
			if (getFormattedTextField() instanceof SSFormattedTextField ftf
					&& getStringValidator() != null
					&& !getStringValidator().apply(List.of(masked, string), ftf))
				throw new ParseException("stringValidator failed", 0);

			return value;
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
			// TODO: should this be done if not valid?
			assistSetEditValidMaySwitchNull(getFormattedTextField());
		}

		// getMaskLiterals() is somewhat expensive. (not too bad, but my obsession)
		// Cache the results. Monitor setMask and setPlaceholderCharacter
		// to flush the parts of the cache as needed.
		
		private LiteralsAndPositions maskLiteralsAndPositions;
		private String maskLiterals_noPlaceholder;
		private String maskLiterals_withPlaceholder;

		/**
		 * {@inheritDoc }
		 */
		@Override
		public void setMask(String mask) throws ParseException
		{
			super.setMask(mask);
			maskLiteralsAndPositions = null;
			maskLiterals_noPlaceholder = null;
			maskLiterals_withPlaceholder = null;
		}

		/**
		 * {@inheritDoc }
		 */
		@Override
		public void setPlaceholderCharacter(char placeholder)
		{
			super.setPlaceholderCharacter(placeholder);
			maskLiterals_noPlaceholder = null;
			maskLiterals_withPlaceholder = null;
		}

		private LiteralsAndPositions getMaskLiteralsAndPositions()
		{
			if (maskLiteralsAndPositions == null)
				maskLiteralsAndPositions = FormatterAssist
						.getLiteralsAndPositions(getMask(), FORMATTING_CHARS);
			return maskLiteralsAndPositions;
		}

		/**
		 * Find the characters in the specified {@linkplain MaskFormatter} that are
		 * displayed literally; there's a flag for including the placeholder character.
		 * These do not include the formatting characters like '#' or 'H';
		 * they are not displayed. Common examples are '/', '$'.
		 * Note that there is no consideration for chars beyond utf-16.
		 * 
		 * @param includePlaceholder when true include the placeholderChar in result
		 * @return the literals in the mask
		 */
		public String getMaskLiterals(boolean includePlaceholder)
		{
			if (includePlaceholder) {
				if (maskLiterals_withPlaceholder != null)
					return maskLiterals_withPlaceholder;
			} else {
				if (maskLiterals_noPlaceholder != null)
					return maskLiterals_noPlaceholder;
			}
			List<String> allLiterals = getMaskLiteralsAndPositions().literals();
			if (includePlaceholder) {
				allLiterals = new ArrayList<>(allLiterals);
				allLiterals.add(String.valueOf(getPlaceholderCharacter()));
			}
			
			String noDuplicates = allLiterals
					.stream()
					.distinct()
					.collect(Collectors.joining());
			if (includePlaceholder) {
				maskLiterals_withPlaceholder = noDuplicates;
			} else {
				maskLiterals_noPlaceholder = noDuplicates;
			}
			return noDuplicates;
		}
		
		// For some reason, says it doesn't override anything when running javadoc.
		// {@inheritDoc }
		/**
		 * Most formats show extra characters that user use has not input.
		 * For a mask this probably includes mask literals and placeholder characters.
		 * @return characters not input by the user
		 */
		@Override
		public String getFormatLiterals()
		{
			return getMaskLiterals(true);
		}
		
	}
}
