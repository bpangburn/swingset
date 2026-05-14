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
package com.nqadmin.swingset.demo;

import javax.swing.text.DefaultFormatterFactory;

import com.nqadmin.swingset.formatting.Field;
import com.nqadmin.swingset.formatting.SSFormat;
import com.nqadmin.swingset.formatting.SSMaskFormatterFactory;

import static com.nqadmin.swingset.formatting.SSFormat.CUSTOM;

/**
 * A simple field for debug that is not in the formatting package.
 */
@SuppressWarnings("serial")
public class DebugField extends Field {
	/**
	 *  Creates a default SSDateField object using the default date format.
	 */
	public DebugField(){
		this(CUSTOM);
	}

	/**
	 *  Creates a new instance of SSDateField with the specified format.
	 *  @param format - an enum format to be used while the date field is in edit mode
	 */
	public DebugField(SSFormat format) {
		this(createFormatterFactory(format));
	}

	/**
	 * Creates an object of SSDateField with the specified formatter factory
	 * @param factory - formatter factory to be used
	 */
	public DebugField(AbstractFormatterFactory factory) {
		super(factory);
	}

	@Override
	public void cleanField()
	{
		setValue(getAllowNull() ? null : 777);
	}

	/**
	 * Create mask formatter factory with specified format pattern.
	 * @param format - Format to be used for date while in editing mode.
	 * @return a DefaultFormatterFactory for the specified date format
	 */
	public static DefaultFormatterFactory createFormatterFactory(SSFormat format) {
		format = SSFormat.getActualFormat(format);
		String formatMask = "###";
		
		return new SSMaskFormatterFactory.Builder<>(formatMask)
				.ssFormat(format)
				.build();
	}
}
