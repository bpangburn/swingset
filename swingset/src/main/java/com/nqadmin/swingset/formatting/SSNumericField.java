/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

/**
 * SSNumericField.java
 *
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
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
		this.setHorizontalAlignment(SwingConstants.RIGHT);
	}

	/**
	 * Returns the number digits used for integer part of the number
	 * 
	 * @return returns the number digits used for integer part of the number
	 */
	public int getPrecision() {
		return this.precision;
	}

	/**
	 * Returns the number of digits used for fraction part of the number
	 * 
	 * @return returns the number of digits used for fraction part of the number
	 */
	public int getDecimals() {
		return this.decimals;
	}

	/**
	 * Sets the number of digits needed for integer part of the number
	 * 
	 * @param _precision - number of digits needed for integer part of the number
	 */
	public void setPrecision(final int _precision) {
		this.precision = _precision;
		this.setFormatterFactory(new SSNumericFormatterFactory(_precision, this.decimals));
	}

	/**
	 * Sets the number of digits needed for fraction part of the number
	 * 
	 * @param _decimals - number of digits needed for fraction part of the number
	 */
	public void setDecimals(final int _decimals) {
		this.decimals = _decimals;
		this.setFormatterFactory(new SSNumericFormatterFactory(this.precision, _decimals));
	}

}

/*
 * $Log$ Revision 1.7 2005/05/26 12:12:36 dags added bind(SSRowSet, columnName)
 * method and some java.sql.Types checking and support
 *
 * Revision 1.6 2005/03/28 14:46:43 dags syncro commit
 *
 * Revision 1.5 2005/02/04 22:42:06 yoda2 Updated Copyright info.
 *
 * Revision 1.4 2005/01/18 23:38:01 dags Diego's name fix
 *
 * Revision 1.3 2004/12/13 20:50:16 dags Fix package name
 *
 * Revision 1.2 2004/12/13 18:46:13 prasanth Added License.
 *
 */
