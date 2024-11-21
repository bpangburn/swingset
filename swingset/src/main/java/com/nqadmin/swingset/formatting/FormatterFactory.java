/* *****************************************************************************
 * Copyright (C) 2024, Ernie R Rael. All rights reserved.
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
 * ****************************************************************************/
package com.nqadmin.swingset.formatting;

import java.text.ParseException;
import java.util.Objects;
import java.util.function.BiFunction;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 * Specialized formatter; handles allow null and format.
 */
@SuppressWarnings("serial")
public abstract class FormatterFactory extends DefaultFormatterFactory
{
	private final SSFormat ssFormat;
	private final AbstractFormatter converter;
	private final BiFunction<JFormattedTextField, AbstractFormatter, Boolean> containsUserText;

	/**
	 * To build a new FormatterFactory with the specified parameters.Unless noted,
	 * a parameter is used when constructing the MaskFormatter.<p>
	 * <p>
	 * @see <em>Effective Java</em> Item 2 about override.
	 * @param <T>
	 */
	abstract protected static class Builder<T extends Builder<T>> {
		private AbstractFormatter converter = null;
		private SSFormat ssFormat = null;
		private BiFunction<JFormattedTextField, AbstractFormatter, Boolean> containsUserText;

		/** Used by the mask formatter in string2Value and value2String.
		 * It's the last step in stringToValue; it produces the Value
		 * in the formatted text field.
		 * @param val
		 * @return  builder */
		public T converter(AbstractFormatter val) { converter = val; return self(); }
		/** *  The {@link SSFormat} used when generating this format factory.
		 * @param val
		 * @return  builder */
		public T ssFormat(SSFormat val) { ssFormat = val; return self(); }
		/**
		 *  This overrides the default check for user input data present.The default
		 * check is done using {@link FormatterAssist#userText(java.lang.String,
		 * java.lang.String, java.lang.String, java.lang.Character)}. If set, this
		 * is used by {@link SSFormattedTextField#containsUserText() }.
		 * @param val
		 * @return  builder */
		public T containsUserText(BiFunction<JFormattedTextField, AbstractFormatter,
								   Boolean> val)
		{ containsUserText = val; return self(); }

		/**
		 *
		 * @return
		 */
		@SuppressWarnings("unchecked")
		protected T self() {
			return (T) this;
		}

		/**
		 * builder
		 * @return converter
		 */
		public AbstractFormatter getConverter()
		{
			return converter;
		}
	}

	/**
	 * The Format used to create factory.
	 * @param builder
	 */
	public FormatterFactory(Builder<?> builder)
	{
		this.ssFormat = Objects.requireNonNull(builder.ssFormat, "format can not be null");
		this.converter = builder.converter;
		this.containsUserText = builder.containsUserText;
	}
	
	/**
	 * The Format used to create factory.
	 * @return format
	 */
	public SSFormat getSSFormat()
	{
		return ssFormat;
	}

	/**
	 * Convert the string as needed and do
	 * {@link JFormattedTextField#setValue(java.lang.Object) }.
	 * @param ftf set this text field's value
	 * @param string convert to a value
	 * @throws ParseException
	 */
	public abstract void switchToNonNullValue(JFormattedTextField ftf, String string)
			throws ParseException;

	/**
	 * Converter used with stringToValue and valueToString; typically null.
	 * @return converter
	 */
	public AbstractFormatter getConverter()
	{
		return converter;
	}

	/**
	 * Function used to determine if text field has user input; typically null.
	 * @return containsUserText
	 */
	public BiFunction<JFormattedTextField, AbstractFormatter, Boolean> getContainsUserText()
	{
		return containsUserText;
	}

	/** use setEditValid method to check that formatter should flip */
	@SuppressWarnings("serial")
	protected static class SSNullFormatter extends DefaultFormatter
	{
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
		 * attempt to set the value (which switches the formatter).
		 * @param valid
		 */
		@Override
		protected void setEditValid(boolean valid) {
			super.setEditValid(valid);
			
			// if a character was added to the Null Formatter,
			// then set a value to flip to the  (presumably) edit formatter.
			final JFormattedTextField ftf = getFormattedTextField();
			Object value = ftf.getValue();
			// May not need to check for SSNullFormatter, but things change :-)
			if(value instanceof String stringValue
					&& ftf.getFormatter() instanceof SSNullFormatter
					&& ftf.getFormatterFactory() instanceof FormatterFactory ff) {
				try {
					ff.switchToNonNullValue(ftf, stringValue);
					// If ftf is still the null formatter,
					// then Formatter didn't like the value; notify and get out.
					if (ftf.getFormatter() instanceof SSNullFormatter) {
						invalidEdit();
						return;
					}

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
