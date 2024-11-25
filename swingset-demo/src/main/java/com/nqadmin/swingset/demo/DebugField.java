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
package com.nqadmin.swingset.demo;

import com.nqadmin.swingset.formatting.Field;
import com.nqadmin.swingset.formatting.SSFormat;
import static com.nqadmin.swingset.formatting.SSFormat.CUSTOM;
import com.nqadmin.swingset.formatting.SSMaskFormatterFactory;
import javax.swing.text.DefaultFormatterFactory;



// SSDateField.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to link a SSFormattedTextField to a date column in a database.
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
	 *  @param _format - an enum format to be used while the date field is in edit mode
	 */
	public DebugField(final SSFormat _format) {
		this(createFormatterFactory(_format));
	}

	/**
	 * Creates an object of SSDateField with the specified formatter factory
	 * @param factory - formatter factory to be used
	 */
	public DebugField(final AbstractFormatterFactory factory) {
		super(factory);
	}
		
	/**
	 * Create DATE formatter factory with specified format pattern.
	 * See https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
	 * @param _format - Format to be used for date while in editing mode.
	 * @return a DefaultFormatterFactory for the specified date format
	 */
	public static DefaultFormatterFactory createFormatterFactory(SSFormat _format) {
		SSFormat format = _format;
		format = SSFormat.getActualFormat(format);
		String formatMask = "###";
		
		return new SSMaskFormatterFactory.Builder<>(formatMask)
				.ssFormat(format)
				.build();
	}
}
