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

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import javax.swing.text.NumberFormatter;

import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * Number formatter that requires a parse of the complete string to succeed.
 */
@SuppressWarnings("serial")
public class SSNumberFormatter extends NumberFormatter implements FormatterAssist
{
	/**
	 *
	 * @param format
	 */
	public SSNumberFormatter(NumberFormat format)
	{
		super(format);
		setCommitsOnValidEdit(true);
	}
	
	/** {@inheritDoc } */
	@Override
	public SSFormat getSSFormat() {
		if(getFormattedTextField() instanceof SSFormattedTextField ftf)
			return ftf.getFormat();
		return null;
	}

	/**
	 * If the value is not a String and there is a converter,
	 * then first convert the value before super.valueToString.
	 * @param value
	 * @return String representation of the value
	 * @throws ParseException
	 */
	@Override
	public String valueToString(Object value) throws ParseException {
		String string;

		// TODO: handle a converter, see SSMaskFormatterFactory
		//s = assistValueToString(value);

		//Object v = value instanceof String s ? stringToValue(s) : value;
		Object v = value;

		string = super.valueToString(v);
		return string;
	}
	
	/**
	 * First convert the string with super.stringToValue,
	 * then use the converter (if there is one) to create
	 * the value object.
	 * @param s
	 * @return
	 * @throws ParseException
	 */
	@Override
	public Object stringToValue(String s) throws ParseException {
		if (s == null || s.isBlank()) {
			if(getFormattedTextField() instanceof SSFormattedTextField ftf
					&& !ftf.getAllowNull())
				throw new ParseException("Null value not allowed", 0);
			return null;
		}
		ParsePosition ppos = new ParsePosition(0);
		getFormat().parseObject(s, ppos);
		if (s.length() != ppos.getIndex())
			throw new ParseException(sf( "In '%s' parse finished at %d ",
					s, ppos.getIndex()), ppos.getIndex());
		// Only need to do following so that min/max constraints are checked.
		return super.stringToValue(s);
		// TODO: handle a converter, see SSMaskFormatterFactory
	}
}
