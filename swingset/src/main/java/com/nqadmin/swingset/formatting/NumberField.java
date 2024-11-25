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
import java.text.NumberFormat;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.SwingConstants;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * Number field.
 */
@SuppressWarnings("serial")
public abstract class NumberField extends Field
{
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
	 * Apply the function to this' FormatFactory's DisplayFormatter's Format.
	 * @param f the function to apply
	 * @return the value from the NumberFormat or -1 if no NumberFormat
	 */
	protected int getNumberFormatParam(Function<NumberFormat, Integer> f) {
		if (getFormatterFactory() instanceof DefaultFormatterFactory ff
				&& ff.getDisplayFormatter() instanceof NumberFormatter nfer
				&& nfer.getFormat() instanceof NumberFormat nf) {
			return f.apply(nf);
		}
		return -1;
	}

	/**
	 * Take the supplied number format and configure it with defaults.
	 * A number format that does big decimal.
	 * @param f
	 * @return 
	 */
	public static NumberFormat createFormat(Supplier<NumberFormat> f) {
		NumberFormat format = f.get();
		parseBigDecimal(format, true);
		return format;
	}

	/**
	 * Attempt to control whether the format generates a BigDecimal result
	 * or not.Only {@link DecimalFormat} can control it.
	 * 
	 * @param format modify this format if possible
	 * @param flag if true turn on BigDecimal format/parse
	 * @return true if the modification was possible
	 */
	static public boolean parseBigDecimal(NumberFormat format, boolean flag) {
		if (format instanceof DecimalFormat df) {
			df.setParseBigDecimal(flag);
			return true;
		}
		return false;
	}
}
