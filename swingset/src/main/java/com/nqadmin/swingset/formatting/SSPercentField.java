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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import javax.swing.text.DefaultFormatterFactory;

import static com.nqadmin.swingset.formatting.NumberField.createNumberFormat;

// SSPercentField.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to bind a SSFormattedTextField to a percentage column in a database.
 */
@SuppressWarnings("serial")
public class SSPercentField extends NumberField {

	/**
     * Creates a default object of SSPercentField
     */
    public SSPercentField() {
        this(createFormatterFactory(SSFormat.CUSTOM, null, null));
    }

	/**
     * Creates a default object of SSPercentField.
	 * @param precision - number of digits needed for integer part of the number
	 * @param decimals  - number of digits needed for the fraction part of the number
     */
    public SSPercentField(int precision, int decimals) {
        this(createFormatterFactory(SSFormat.CUSTOM, precision, decimals));
    }

    /**
     * Creates an object of SSPercentField with the specified formatter factory
     * @param factory - formatter factory to be used
     */
    public SSPercentField(AbstractFormatterFactory factory) {
        super(factory);
    }

	/**
	 * Get the multiplier used in percent.
	 * @return multiplier
	 */
	public int getMultiplier() {
		return getNumberFormatParam((nf) -> nf.getMultiplier());
	}

	/**
	 * Sets the multiplier for use in percent. With multiplier 100,
	 * 1.23 is formatted as "123", and "123" is parsed into 1.23.
	 * @param multiplier the new multiplier
	 */
	public void setMultiplier(int multiplier) {
		setFormatParam((nf) -> nf.setMultiplier(multiplier));
	}

	/**
	 * Create a FormatterFactory.
	 * @param ssFormat
	 * @param precision - number of digits needed for integer part of the number
	 * @param decimals - number of digits needed for fraction part of the number
	 * @return FormatterFactory.
	 */
	public static DefaultFormatterFactory createFormatterFactory(
			SSFormat ssFormat, Integer precision, Integer decimals
			//, Locale editLocale, Locale displayLocale
	)
	{
		Objects.requireNonNull(ssFormat);

		NumberFormat displayFormat = createNumberFormat(
				()->NumberFormat.getPercentInstance(Locale.US));
		NumberFormat editFormat = createNumberFormat(
				()->NumberFormat.getInstance(Locale.US));
		// Make sure edit format has the multiplier default for percent.
		((DecimalFormat)editFormat).setMultiplier(
				((DecimalFormat)displayFormat).getMultiplier());

		if (precision!=null) {
			editFormat.setMaximumIntegerDigits(precision);
			editFormat.setMinimumIntegerDigits(1);
			displayFormat.setMaximumIntegerDigits(precision);
			displayFormat.setMinimumIntegerDigits(1);
		}
		if (decimals!=null) {
			editFormat.setMaximumFractionDigits(decimals);
			editFormat.setMinimumFractionDigits(decimals);
			displayFormat.setMaximumFractionDigits(decimals);
			displayFormat.setMinimumFractionDigits(decimals);
		}
		
		return new SSFormatterFactory.Builder<>()
				.ssFormat(ssFormat)
				.editFormatter(new SSNumberFormatter(editFormat))
				.displayFormatter(new SSNumberFormatter(displayFormat))
				.build();
	}
}
