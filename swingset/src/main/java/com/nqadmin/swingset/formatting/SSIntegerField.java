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
import java.util.Objects;

import javax.swing.text.DefaultFormatterFactory;

import static com.nqadmin.swingset.formatting.SSFormat.CUSTOM;

/**
 * Typically used to bind a SSFormattedTextField to an integer column in a database.
 */
@SuppressWarnings("serial")
public class SSIntegerField extends NumberField
{
	/**
	 * Creates a new instance of SSIntegerField
	 */
	public SSIntegerField()
	{
		this(createFormatterFactory(CUSTOM, null));
	}

	/**
	 * Creates an object of SSIntegerField with the specified number of digits.
	 *
	 * @param precision - number of digits needed
	 */
	public SSIntegerField(int precision) {
		this(createFormatterFactory(CUSTOM, precision));
	}

	/**
	 * Creates an object of SSIntegerField with the specified formatter factory
	 *
	 * @param factory - formatter factory to be used
	 */
	public SSIntegerField(AbstractFormatterFactory factory) {
		super(factory);
	}

	/**
	 * This class should throw a run-time exception if this method is used;
	 * but it is silently ignored.
	 * {@inheritDoc }
	 */
	@Override
	public void setDecimals(final int decimals) {
	}

	/**
	 * Create a FormatterFactory.
	 * @param ssFormat
	 * @param precision
	 * @return FormatterFactory.
	 */
	public static DefaultFormatterFactory createFormatterFactory(
			SSFormat ssFormat, Integer precision)
	{
		Objects.requireNonNull(ssFormat);

		NumberFormat integerFormat = createNumberFormat(()->NumberFormat.getIntegerInstance());

		initPrecision(precision, integerFormat);
		
		return new SSFormatterFactory.Builder<>()
				.ssFormat(ssFormat)
				.displayFormatter(new SSNumberFormatter(integerFormat))
				.editFormatter(new SSNumberFormatter(integerFormat))
				.build();
	}
}
