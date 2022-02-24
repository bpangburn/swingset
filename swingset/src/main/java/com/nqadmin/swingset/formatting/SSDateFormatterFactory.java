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

import java.text.SimpleDateFormat;

import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// SSDateFormatterFactory.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Static methods to create a DefaultFormatterFactory for Date fields.
 */
public class SSDateFormatterFactory {

	private SSDateFormatterFactory(){}

	/**
	 * Constant for dd/MM/yyyy date format
	 * @deprecated use {@link Format}'s DATE_DDMMYYYY
	 */
	@Deprecated
	public static final int DDMMYYYY = 1;

	/**
	 * Log4j Logger for component
	 */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Constant for MM/dd/yyyy date format
	 * @deprecated use {@link Format}'s DATE_MMDDYYYY
	 */
	@Deprecated
	public static final int MMDDYYYY = 0;

	/**
	 * Constant for yyyy-MM-dd date format
	 * @deprecated use {@link Format}'s DATE_YYYYMMDD
	 */
	@Deprecated
	public static final int YYYYMMDD = 2;

	/**
	 * Create formatter factory with default pattern.
	 * @return
	 */
	public static DefaultFormatterFactory get() {
		return get(Format.DATE);
	}
	/** compatibility
	 * @param f
	 * @return 
	 */
	@Deprecated
	public static DefaultFormatterFactory get(int f) {
		// Don't like the way default is buried in get
		return get(getFormat(f));
	}

	// NOTE: package private
	@Deprecated
	static Format getFormat(final int _format) {
		switch (_format) {
		case MMDDYYYY:
			return Format.DATE_MMDDYYYY;
		case DDMMYYYY:
			return Format.DATE_DDMMYYYY;
		case YYYYMMDD:
			return Format.DATE_YYYYMMDD;
		default:
			return Format.DATE;
		}
	}

	/**
	 * Create formatter factory with specified format pattern.See https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
	 * @param _format - Format to be used for date while in editing mode. The default format is DDMMYYYY
	 * @return a DefaultFormatterFactory
	 */
	public static DefaultFormatterFactory get(Format _format) {
		Format format = _format;
		if ( _format.getType() != Format.DATE ) {
			// I'd be inclined to exception
			// throw new IllegalArgumentException(
			// 		String.format("% is not a DATE", _format.toString()));
			logger.error(() -> String.format("%s is not a DATE, using default",
					_format.toString()));
			format = Format.DATE;
		}
		if (format.isBase()) {
			format = Format.getDefaultFormat(format);
		}
		String formatMask;
		String editPattern;
		String maskLiterals;
		switch(format){
			case DATE_MMDDYYYY:
				formatMask = "##/##/####";
				editPattern = "MMddyyyy";
				maskLiterals = "/";
				break;

			case DATE_DDMMYYYY:
				formatMask = "##/##/####";
				editPattern = "ddMMyyyy";
				maskLiterals = "/";
				break;
			case DATE_YYYYMMDD:
				formatMask = "####-##-##";
				editPattern = "yyyyMMdd";
				maskLiterals = "-";
				break;
			default:
				logger.error("Unknown date format type of " + format);
				return null;
		}

		return new SSMaskFormatterFactory.Builder<>(formatMask)
			.converter(new DateFormatter(new SimpleDateFormat(editPattern)))
			.maskLiterals(maskLiterals).placeholder('_')
			.build();
	}
}
