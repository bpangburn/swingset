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

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import javax.swing.text.DefaultFormatterFactory;

import static com.nqadmin.swingset.formatting.SSFormat.CUSTOM;

// TODO: The locale maintenance is screwy; for example if you spec a locale,
//		 and then do setPrecision() you lose the locale. There does not seem
//		 to be any way to determine the locale of a given NumberFormat/Format.

/**
 * Used to link a SSFormattedTextField to a currency column in a database.
 */
@SuppressWarnings("serial")
public class SSCurrencyField extends NumberField
{
	/**
	 * Creates a new instance of SSCurrencyField
	 */
	public SSCurrencyField() {
		this(createFormatterFactory(CUSTOM, null, null, null, null));
	}

	/**
	 * Creates an instance of SSCurrenyField with the specified number of integer and
	 * fraction digits
	 *
	 * @param precision - number of digits needed for integer part of the number
	 * @param decimals  - number of digits needed for the fraction part of the
	 *                   number
	 */
	public SSCurrencyField(int precision, int decimals) {
		this(createFormatterFactory(CUSTOM, precision, decimals, null, null));
	}

	/**
	 * Creates an instance of SSCurrenyField with the specified number of integer and
	 * fraction digits using the given locale
	 *
	 * @param precision     - number of digits needed for integer part of the
	 *                       number
	 * @param decimals      - number of digits needed for the fraction part of the
	 *                       number
	 * @param editorLocale  - locale to be used while in editing mode
	 * @param displayLocale - locate to be used for displaying the number
	 */
	public SSCurrencyField(int precision, int decimals, Locale editorLocale,
			Locale displayLocale) {
		this(createFormatterFactory(CUSTOM, precision, decimals, editorLocale, displayLocale));
	}

	/**
	 * Creates an SSCurrencyField with the specified formatter factory
	 *
	 * @param factory - formatter factory to be used
	 */
	public SSCurrencyField(AbstractFormatterFactory factory) {
		super(factory);
	}

	/**
	 * Getter for property decimals.
	 *
	 * @return Value of property decimals, -1 if a problem
	 */
	public int getDecimals() {
		return getNumberFormatParam((nf) -> nf.getMaximumFractionDigits());
	}

	/**
	 * Returns the number digits used for integer part of the number
	 *
	 * @return returns the number digits used for integer part of the number, -1 if a problem
	 */
	public int getPrecision() {
		return getNumberFormatParam((nf) -> nf.getMaximumIntegerDigits());
	}

	/**
	 * Sets the number of digits needed for fraction part of the number
	 * <p>
	 * <b>NOTE:</b> This does not preserve a previously set locale.
	 * 
	 * @param decimals - number of digits needed for fraction part of the number
	 */
	public void setDecimals(final int decimals) {
		setFormatterFactory(createFormatterFactory(CUSTOM, getPrecision(), decimals, null, null));
	}

	/**
	 * Sets the number of digits needed for integer part of the number
	 * <p>
	 * <b>NOTE:</b> This does not preserve a previously set locale.
	 * 
	 * @param precision - number of digits needed for integer part of the number
	 */
	public void setPrecision(final int precision) {
		setFormatterFactory(createFormatterFactory(CUSTOM, precision, getDecimals(), null, null));
	}

	/**
	 * Create a FormatterFactory.
	 * @param ssFormat
	 * @param precision - number of digits needed for integer part of the number
	 * @param decimals - number of digits needed for fraction part of the number
	 * @param editorLocale - locale while editing
	 * @param displayLocale - locale while not editing
	 * @return FormatterFactory.
	 */
	public static DefaultFormatterFactory createFormatterFactory(
			SSFormat ssFormat, Integer precision, Integer decimals,
			Locale editorLocale, Locale displayLocale)
	{
		Objects.requireNonNull(ssFormat);

		Locale defaultLocale = Locale.getDefault(Locale.Category.FORMAT);
		// For display use currency, e.g. might see '$'. TODO: use the same for both?
		NumberFormat displayFormat = NumberFormat.getCurrencyInstance(
				displayLocale != null ? displayLocale : defaultLocale);
		//NumberFormat editorFormat = displayFormat;
		// For editing use a plain number
		NumberFormat editorFormat = NumberFormat.getInstance(
				displayLocale != null ? displayLocale : defaultLocale);

		if (precision!=null) {
			editorFormat.setMaximumIntegerDigits(precision);
			editorFormat.setMinimumIntegerDigits(1);
			displayFormat.setMaximumIntegerDigits(precision);
			displayFormat.setMinimumIntegerDigits(1);
		}
		if (decimals!=null) {
			editorFormat.setMaximumFractionDigits(decimals);
			editorFormat.setMinimumFractionDigits(decimals);
			displayFormat.setMaximumFractionDigits(decimals);
			displayFormat.setMinimumFractionDigits(decimals);
		}
		
		// Note that the editFormatter does not specify precision/decimals.
		
		return new SSFormatterFactory.Builder<>()
				.ssFormat(ssFormat)
				.editFormatter(new SSNumberFormatter(editorFormat))
				.displayFormatter(new SSNumberFormatter(displayFormat))
				.build();
	}
}
