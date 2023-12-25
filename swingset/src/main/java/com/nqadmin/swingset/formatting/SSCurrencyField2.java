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
package com.nqadmin.swingset.formatting;

import java.util.Locale;

import javax.swing.SwingConstants;

import com.nqadmin.swingset.formatting.factories.SSCurrencyFormatterFactory;
import com.nqadmin.swingset.formatting.factories.SSCurrencyFormatterFactory2;

// SSCurrencyField.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to link a SSFormattedTextField to a currency column in a database.
 */
public class SSCurrencyField2 extends SSFormattedTextField {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 1636264407572416306L;
	
	private int decimals = 2;
	private int precision = 3;

	/**
	 * Creates a new instance of SSCurrencyField
	 */
	public SSCurrencyField2() {
		this(new SSCurrencyFormatterFactory2());
	}

	/**
	 * Creates an instance of SSCurrenyField with the specified number of integer and
	 * fraction digits
	 *
	 * @param _precision - number of digits needed for integer part of the number
	 * @param _decimals  - number of digits needed for the fraction part of the
	 *                   number
	 */
	public SSCurrencyField2(final int _precision, final int _decimals) {
		this(new SSCurrencyFormatterFactory2(_precision, _decimals));
	}

	/**
	 * Creates an instance of SSCurrenyField with the specified number of integer and
	 * fraction digits using the given locale
	 *
	 * @param _precision     - number of digits needed for integer part of the
	 *                       number
	 * @param _decimals      - number of digits needed for the fraction part of the
	 *                       number
	 * @param _editorLocale  - locale to be used while in editing mode
	 * @param _displayLocale - locate to be used for displaying the number
	 */
	public SSCurrencyField2(final int _precision, final int _decimals, final Locale _editorLocale,
			final Locale _displayLocale) {
		this(new SSCurrencyFormatterFactory2(_precision, _decimals, _editorLocale, _displayLocale));
	}

	/**
	 * Creates an SSCurrencyField with the specified formatter factory
	 *
	 * @param _factory - formatter factory to be used
	 */
	public SSCurrencyField2(final javax.swing.JFormattedTextField.AbstractFormatterFactory _factory) {
		super(_factory);
		setHorizontalAlignment(SwingConstants.RIGHT);
		setValue(new java.lang.Double(0.00));
	}

	/**
	 * Getter for property decimals.
	 *
	 * @return Value of property decimals.
	 */
	public int getDecimals() {
		return decimals;
	}

	/**
	 * Returns the number digits used for integer part of the number
	 *
	 * @return returns the number digits used for integer part of the number
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * Sets the number of digits needed for fraction part of the number
	 *
	 * @param _decimals - number of digits needed for fraction part of the number
	 */
	public void setDecimals(final int _decimals) {
		decimals = _decimals;
		setFormatterFactory(new SSCurrencyFormatterFactory(precision, _decimals));
	}

	/**
	 * Sets the number of digits needed for integer part of the number
	 *
	 * @param _precision - number of digits needed for integer part of the number
	 */
	public void setPrecision(final int _precision) {
		precision = _precision;
		setFormatterFactory(new SSCurrencyFormatterFactory(_precision, decimals));
	}
}
