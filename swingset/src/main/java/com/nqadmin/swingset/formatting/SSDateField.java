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

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.SimpleDateFormat;

import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import com.nqadmin.swingset.utils.SSUtils;

/**
 * Used to link a SSFormattedTextField to a date column in a database.
 */

@SuppressWarnings("serial")
public class SSDateField extends DateTimeField {
	
	
//=====================================================================================
// 2026-01-06_BP: The code BELOW is needed for SwingSet 4.0.x compatibility
//=====================================================================================
	
	/**
	 * Constant for dd/MM/yyyy date format
	 * @deprecated use {@link SSFormat#DATE_DDMMYYYY_SLASH}
	 */
	@Deprecated
	public static final int DDMMYYYY = 1;

	/**
	 * Constant for MM/dd/yyyy date format
	 * @deprecated use {@link SSFormat#DATE_MMDDYYYY_SLASH}
	 */
	@Deprecated
	public static final int MMDDYYYY = 0;

	/**
	 * Constant for yyyy-MM-dd date format
	 * @deprecated use {@link SSFormat#DATE_YYYYMMDD_STROKE}
	 */
	@Deprecated
	public static final int YYYYMMDD = 2;
	
	/**
	 * Returns Format enum for old style constant.
	 * @param _format int constant for style
	 * @return enum
	 * @deprecated use enum, never int constants
	 */
	// NOTE: package private
	@Deprecated
	static SSFormat getFormat(final int _format) {
		switch (_format) {
		case MMDDYYYY:
			return SSFormat.DATE_MMDDYYYY_SLASH;
		case DDMMYYYY:
			return SSFormat.DATE_DDMMYYYY_SLASH;
		case YYYYMMDD:
			return SSFormat.DATE_YYYYMMDD_STROKE;
		default:
			return SSFormat.DATE;
		}
	}
	
	/**
	 *  Creates a new instance of SSDateField with the specified format
	 *  @param format - format to be used while the date field is in edit mode
	 *  allowed values are MMDDYYYY or DDMMYYYY
	 * @deprecated use {@link SSDateField#SSDateField(SSFormat) }
	 */
	@Deprecated
	public SSDateField(final int format) {
		this(getFormat(format));
	}
		
//=====================================================================================
// 2026-01-06_BP: The code ABOVE is needed for SwingSet 4.0.x compatibility
//=====================================================================================
		
	
	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 *  Creates a default SSDateField object using the default date format.
	 */
	public SSDateField(){
		this(SSFormat.DATE);
	}

	/**
	 *  Creates a new instance of SSDateField with the specified format.
	 *  @param _format - an enum format to be used while the date field is in edit mode
	 */
	public SSDateField(final SSFormat _format) {
		this(createFormatterFactory(_format));
	}

	/**
	 * Creates an object of SSDateField with the specified formatter factory
	 * @param factory - formatter factory to be used
	 */
	public SSDateField(final AbstractFormatterFactory factory) {
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
		if ( _format.getType() != SSFormat.DATE ) {
			// I'd be inclined to exception
			// throw new IllegalArgumentException(
			// 		String.format("% is not a DATE", _format.toString()));
			logger.log(Level.ERROR, () -> String.format("%s is not a DATE, using default",
					_format.toString()));
			format = SSFormat.DATE;
		}
		format = SSFormat.getActualFormat(format);
		String formatMask;
		String editPattern;
		switch(format) {
		case DATE_MMDDYYYY_SLASH -> {
			formatMask = "##/##/####";
			editPattern = "MMddyyyy";
		}
		case DATE_DDMMYYYY_SLASH -> {
			formatMask = "##/##/####";
			editPattern = "ddMMyyyy";
		}
		case DATE_YYYYMMDD_STROKE -> {
			formatMask = "####-##-##";
			editPattern = "yyyyMMdd";
		}
		default -> {
			logger.log(Level.ERROR, "Unknown date format type of " + format);
			return null;
		}
		}
		
		return new SSMaskFormatterFactory.Builder<>(formatMask)
				.ssFormat(format)
				.stringValidator(DateTimeField::stringValidator)
				.converter(new DateFormatter(new SimpleDateFormat(editPattern)))
				.placeholderCharacter('_')
				.build();
	}
}
