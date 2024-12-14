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

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.SwingConstants;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import com.nqadmin.swingset.formatting.FormatterFactory.SSNullFormatter;

/**
 * Number field; this class has methods to assist in accessing a NumberFormat.
 * Subclasses may create one or more formats for the FormatterFactory;
 * Often subclasses use a single format for the FormatterFactory's Formatters;
 * but they may use more than one and usually they will use the same parameters
 * for precision. In any event, the edit format is assumed the most precise;
 * the "get" methods in this class obtain the returned value from the
 * edit format. The set methods will update all the formats in the associated
 * factory and should be overridden if this is not wanted.
 * <p>
 * Note, the get/set methods do nothing when a Formatter's Format is not a
 * {@linkplain DecimalFormat}.
 */
@SuppressWarnings("serial")
public abstract class NumberField extends Field
{

	/**
	 * Take the supplied number format and configure it with defaults.
	 * A number format that does big decimal.
	 * @param f function that returns/creates a Format.
	 * @return Format with some defaults
	 */
	public static NumberFormat createNumberFormat(Supplier<NumberFormat> f) {
		NumberFormat format = f.get();
		if (format instanceof DecimalFormat df)
			df.setParseBigDecimal(true);
		return format;
	}

	/**
	 * Error indicator for get*Param type of  methods.
	 */
	enum Error {
		/** Not DefaultFormatterFactory */
		FACTORY,
		/** Not NumberFormatter */
		FORMATTER,
		/** Not NumberFormat */
		FORMAT,
		/** getNullFormatter formatter is neither NullFormatter nor null */
		NULL_FORMATTER,
	}

	/**
	 * Check if the param is an error.
	 * @param o check this
	 * @return true if error
	 */
	public static boolean isAccessError(Object o) {
		return o instanceof Error;
	}

	/**
	 * Indicators for each {@link Format} for each of
	 * a {@link DefaultFormatterFactory}'s formatter's param state.
	 * On a read option, the elements are the values which are read;
	 * on a write either null or error. A field may be {@linkplain Error}. 
	 */
	public record Params(Object defaultP, Object displayP, Object editP, Object nullP){
		/**
		 * Check if any of the elements is an error.
		 * @return true if any error
		 */
		public boolean hasAccessError() {
			return isAccessError(defaultP) || isAccessError(displayP)
					|| isAccessError(editP) || isAccessError(nullP);
		}
	}

	/**
	 * Constructor.
	 * @param factory formatter factory
	 */
	public NumberField(AbstractFormatterFactory factory)
	{
		super(factory);
		setHorizontalAlignment(SwingConstants.RIGHT);
	}

	/**
	 * Sets the value of the field to an initial state consistent with
	 * the AllowNull property. If not AllowNull then 0.
	 */
	@Override
	public void cleanField() {
		setValue(getAllowNull() ? null : 0);
	}

	/**
	 * Sets the number of digits needed for integer part of the number.
	 * <p>
	 * <b>NOTE:</b> This does not preserve a previously set locale.
	 * 
	 * @param precision - number of digits needed for integer part of the number
	 */
	public void setPrecision(int precision) {
		setFormatParam((nf) -> nf.setMaximumIntegerDigits(precision));
	}

	/**
	 * Sets the number of digits needed for fraction part of the number.
	 * <p>
	 * <b>NOTE:</b> This does not preserve a previously set locale.
	 * 
	 * @param decimals - number of digits needed for fraction part of the number
	 */
	public void setDecimals(int decimals) {
		setFormatParam((nf) -> nf.setMaximumFractionDigits(decimals));
	}

	/**
	 * Returns the max number digits used for integer part of the number.
	 *
	 * @return the number digits used for integer part of the number, -1 if a problem
	 */
	public int getPrecision() {
		return getNumberFormatParam((nf) -> nf.getMaximumIntegerDigits());
	}

	/**
	 * Returns the number of digits used for fraction part of the number,
	 * from the editFormatter.
	 *
	 * @return the number of digits used for fraction part of the number, -1 if a problem
	 */
	public int getDecimals() {
		return getNumberFormatParam((nf) -> nf.getMaximumFractionDigits());
	}

	/**
	 * Sets whether the parse method returns big decimal.
	 * <p>
	 * <b>NOTE:</b> This does not preserve a previously set locale.
	 * 
	 * @param val true to return BigDecimal
	 */
	public void setParseBigDecimal(boolean val) {
		setFormatParam((nf) -> nf.setParseBigDecimal(val));
	}

	/**
	 * During initialization set the precision for the specified formats,
	 * do nothing if precision is null.
	 * @param precision precision
	 * @param formats initialize these formats
	 */
	protected static void initPrecision(Integer precision, NumberFormat... formats) {
		if (precision == null)
			return;
		for (NumberFormat format : formats) {
			format.setMaximumIntegerDigits(precision);
			format.setMinimumIntegerDigits(1);
		}
	}

	/**
	 * During initialization set the precision for the specified formats,
	 * do nothing if precision is null.
	 * @param decimals decimals
	 * @param formats initialize these formats
	 */
	protected static void initDecimals(Integer decimals, NumberFormat... formats) {
		if (decimals == null)
			return;
		for (NumberFormat format : formats) {
			format.setMaximumFractionDigits(decimals);
			format.setMinimumFractionDigits(decimals);
		}
	}

	/**
	 * Retrieve a value from this' FormatterFactory's DisplayFormatter's Format.
	 * @param param function that access the param value
	 * @return the value from the NumberFormat or -1 if no NumberFormat
	 */
	protected int getNumberFormatParam(Function<DecimalFormat, Object> param) {
		Object p = getFormatParam(param);
		if (p instanceof Integer i)
			return i;
		return -1;
	}

	/**
	 * Retrieve a value from this' FormatterFactory's DisplayFormatter's Format.
	 * The argument is like {@code (df) -> df.getMaximumIntegerDigits()} where
	 * {@code df} is a {@linkplain DecimalFormat}.
	 * @param paramF function that access the param value
	 * @return the value from the NumberFormat or -1 if no NumberFormat
	 */
	protected Object getFormatParam(Function<DecimalFormat, Object> paramF) {
		return getFormatParam(paramF, (ff)->ff.getEditFormatter());
	}

	/**
	 * Retrieve a value from this' FormatterFactory's DisplayFormatter's Format.
	 * Does nothing if the formatter is not a NumberFormatter.
	 * The argument formatterF is like {@code (ff)->ff.getEditFormatter()} where
	 * {@code ff} is a {@linkplain DefaultFormatterFactory}.
	 * 
	 * @param paramF accesses the param value
	 * @param formatterF access the formatter
	 * @return the value from the NumberFormat or -1 if no NumberFormat
	 */
	protected Object getFormatParam(Function<DecimalFormat, Object> paramF,
			Function<DefaultFormatterFactory, AbstractFormatter> formatterF) {
		Object o = getNumberFormat(formatterF);
		if (o instanceof DecimalFormat nf)
			return paramF.apply(nf);
		return o;
	}

	private Object getNumberFormat(
			Function<DefaultFormatterFactory, AbstractFormatter> formatterF)
	{
		if (!(getFormatterFactory() instanceof DefaultFormatterFactory ff))
			return Error.FACTORY;
		if (!(formatterF.apply(ff) instanceof NumberFormatter nfer))
			return Error.FORMATTER;
		if (!(nfer.getFormat() instanceof NumberFormat nf))
			return Error.FORMAT;
		return nf;
	}

	private static final List<Function<DefaultFormatterFactory, AbstractFormatter>> formatterFL
			= List.of((ff)->ff.getDefaultFormatter(),
					  (ff)->ff.getDisplayFormatter(),
					  (ff)->ff.getEditFormatter(),
					  (ff)->ff.getNullFormatter());

	/**
	 * Set the param in each of the FormatterFactory's Format.
	 * The argument is like {@code (df) -> df.setMaximumIntegerDigits(123)} where
	 * {@code df} is a {@linkplain DecimalFormat}.
	 * @param paramF function that writes the param value
	 * @return param status from each factory/formatter/format
	 */
	protected Params setFormatParam(Consumer<DecimalFormat> paramF) {
		// Note: if the same format is in more than one formatter,
		// then it is adjusted more than once.
		// Ignore the null formatter.
		List<Object> l = formatterFL.subList(0, 3).stream()
				.map((f)-> {
					Object rv = getNumberFormat(f);
					if (rv instanceof DecimalFormat df) {
						paramF.accept(df);	// write the parameter to "df"
						rv = null; // all is well
					}
					return rv;
				})
				.collect(Collectors.toList());
		return new Params(l.get(0), l.get(1), l.get(2), null);
	}
	// If it's important to avoid doing the same format more than once...
		///// // Track formats that are adjusted so they're only adjusted once.
		///// List<NumberFormat> formats = new ArrayList<>(3);
						///// // Want: "if (!formats.contains(nf))", but require identity.
						///// // Actually, it still works with equals, but weirdly so.
						///// boolean contains = false;
						///// for (Iterator<NumberFormat> it = formats.iterator(); it.hasNext();)
						///// 	if (df == it.next()) { contains = true; break; }
						///// if (!contains) {
						///// 	paramF.accept(df);	// write the parameter to "df"
						///// 	formats.add(df);	// keep track of "df"
						///// }

	/**
	 * Get the param for each of the FormatterFactory's Format.
	 * <p>
	 * Reading all the values is primarily for testing.
	 * @param paramF function to access param
	 * @return param from each format
	 */
	protected Params getFormatParams(Function<DecimalFormat, Object> paramF) {
		List<Object> l = formatterFL.stream()
				.map((f)-> getFormatParam(paramF, f))
				.collect(Collectors.toList());

		// The return for null formatter 
		Error nullFormatterError = null; // no error
		switch (l.get(3)) {
		case Error.FACTORY -> { nullFormatterError = Error.FACTORY; }
		case Error.FORMATTER -> { // the null formatter should get this error
			AbstractFormatter nullFormatter
					= ((DefaultFormatterFactory)getFormatterFactory()).getNullFormatter();
			// leave it as null unless...
			if (nullFormatter != null && !(nullFormatter instanceof SSNullFormatter))
				nullFormatterError = Error.NULL_FORMATTER;
		}
		case null, default -> nullFormatterError = Error.NULL_FORMATTER;
		}

		return new Params(l.get(0), l.get(1), l.get(2), nullFormatterError);
	}
}
