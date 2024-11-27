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

import java.lang.System.Logger;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.Format;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.InternationalFormatter;

import com.nqadmin.swingset.utils.SSUtils;

import static java.lang.System.Logger.Level.*;

import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * General NullAware formatter factory for SS.
 * During build, if no defaultFromatter, try to create one from displayFormatter;
 * just in case.
 * <p>
 * MaskFormatter doesn't use this.
 */
@SuppressWarnings("serial")
public class SSFormatterFactory extends FormatterFactory
{
	/** Logger for component */
	private static final Logger logger = SSUtils.getSystemLogger();

	/**
	 *
	 * @param builder
	 */
	protected SSFormatterFactory(Builder<?> builder)
	{
		super(builder);

		createDefaultFormatterIfNeeded(builder);

		setDefaultFormatter(builder.defaultFormatter);
		setDisplayFormatter(builder.displayFormatter);
		setEditFormatter(builder.editFormatter);
	}

	/**
	 * Use this factory's EditorFormatter to convert stringValue to value.
	 * {@inheritDoc }
	 */
	@Override
	public void switchToNonNullValue(JFormattedTextField ftf, String stringValue)
			throws ParseException
	{
		if (!(getEditFormatter() instanceof FormatterAssist fa)) {
			logger.log(WARNING, "Unexpected type of edit formatter");
			return;
		}
		ftf.setValue(fa.stringToValue(stringValue));
		if (logger.isLoggable(TRACE)) {
			Object v = ftf.getValue();
			logger.log(TRACE, ()->sf("switch: '%s' to %s %s", stringValue,
					v != null ? v.getClass().getSimpleName() : null, v));
		}
	}

	/** {@inheritdoc}
	 * @param <T>
	 */
	public static class Builder<T extends Builder<T>>
			extends FormatterFactory.Builder<T>
	{
		private AbstractFormatter defaultFormatter;
		private AbstractFormatter displayFormatter;
		private AbstractFormatter editFormatter;

		/**
		 * Create the builder.
		 */
		public Builder() {
		}

		/** formatter
		 * @param val
		 * @return  builder */
		public T defaultFormatter(AbstractFormatter val)
		{ defaultFormatter = val; return self(); }
		/** formatter
		 * @param val
		 * @return  builder */
		public T displayFormatter(AbstractFormatter val)
		{ displayFormatter = val; return self(); }
		/** formatter
		 * @param val
		 * @return  builder */
		public T editFormatter(AbstractFormatter val)
		{ editFormatter = val; return self(); }

		/** create the factory
		 * @return the factory */
		public SSFormatterFactory build() {
			return new SSFormatterFactory(this);
		}
	}

	/**
	 * During build, if the default formatter is not specified and the display
	 * formatter is a subclass of International formatter then a default
	 * formatter is created with the display formatter's class and format.
	 * <p>
	 * This modifies the builder.
	 */
	private static void createDefaultFormatterIfNeeded(Builder<?> builder)
	{
		if (builder.defaultFormatter == null
				&& builder.displayFormatter instanceof InternationalFormatter ifm) {
			Format dispFormat = ifm.getFormat();
			try {
				Constructor<?>[] ctors = builder.displayFormatter.getClass()
						.getDeclaredConstructors();
				for (Constructor<?> ctor : ctors) {
					if (ctor.getParameterCount() != 1)
						continue;
					if (ctor.getParameterTypes()[0].isInstance(dispFormat)) {
						builder.defaultFormatter = (AbstractFormatter) ctor.newInstance(dispFormat);
						logger.log(DEBUG, ()->sf("Creating defaultFormatter %s for %s",
								builder.defaultFormatter.getClass().getSimpleName(),
								dispFormat.getClass().getSimpleName()));
						break;
					}
				}
			} catch (SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException ex) {
						logger.log(WARNING, ()->sf("Creating defaultFormatter for %s",
								dispFormat.getClass().getSimpleName()), ex);
			}
		}
	}
}
