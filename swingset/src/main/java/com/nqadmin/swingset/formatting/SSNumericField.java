/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 ******************************************************************************/

package com.nqadmin.swingset.formatting;

import javax.swing.SwingConstants;

// SSNumericField.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to link a SSFormattedTextField to a numeric column in a database.
 */

public class SSNumericField extends SSFormattedTextField {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -5922010378310406148L;
	private int precision = 3;
	private int decimals = 2;

	/**
	 * Creates a new instance of SSNumericField
	 */
	public SSNumericField() {
		this(new SSNumericFormatterFactory());
	}

	/**
	 * Creates an instance of SSNumericField with the specified number of integer and
	 * fraction digits
	 * 
	 * @param _precision - number of digits needed for integer part of the number
	 * @param _decimals  - number of digits needed for the fraction part of the
	 *                   number
	 */
	public SSNumericField(final int _precision, final int _decimals) {
		this(new SSNumericFormatterFactory(_precision, _decimals));
	}

	/**
	 * Creates an SSCurrencyField with the specified formatter factory
	 * 
	 * @param factory - formatter factory to be used
	 */
	public SSNumericField(final javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
		super(factory);
		setHorizontalAlignment(SwingConstants.RIGHT);
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
	 * Returns the number of digits used for fraction part of the number
	 * 
	 * @return returns the number of digits used for fraction part of the number
	 */
	public int getDecimals() {
		return decimals;
	}

	/**
	 * Sets the number of digits needed for integer part of the number
	 * 
	 * @param _precision - number of digits needed for integer part of the number
	 */
	public void setPrecision(final int _precision) {
		precision = _precision;
		setFormatterFactory(new SSNumericFormatterFactory(_precision, decimals));
	}

	/**
	 * Sets the number of digits needed for fraction part of the number
	 * 
	 * @param _decimals - number of digits needed for fraction part of the number
	 */
	public void setDecimals(final int _decimals) {
		decimals = _decimals;
		setFormatterFactory(new SSNumericFormatterFactory(precision, _decimals));
	}

}

