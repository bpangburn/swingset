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

import static com.nqadmin.swingset.utils.SSUtils.sf;

// SSTimestampField.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to link a SSTimestampField to a timestamp column in a database.
 */
@SuppressWarnings("serial")
public class SSTimestampField extends DateTimeField
{
	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

    /**
     * Create an SSTimestampField using the default format.
     */
    public SSTimestampField() {
        this(Format.TIMESTAMP);
    }

    /**
     * Create an SSTimestampField using the specified format.
	 *  @param format - an enum format for the timestamp field
     */
    public SSTimestampField(Format format) {
        this(createFormatterFactory(format));
    }

    /**
     * Creates an instance of SSTimestampField with the specified formatter factory
     * @param factory - formatter factory to be used
     */
    public SSTimestampField(final AbstractFormatterFactory factory) {
        super(factory);
        setValue(new java.util.Date(  ));
    }

    /**
     * Sets the value of the field to the current system date/time
     */
    @Override
	public void cleanField() {
		setValue(new java.util.Date());
    }

	/**
	 * Create TIMESTAMP formatter factory with specified format pattern.
	 * @param _format - Format to use for timestamp while in editing mode.
	 * @return a DefaultFormatterFactory for the specified time format
	 */
	public static DefaultFormatterFactory createFormatterFactory(Format _format) {
		Format format = _format;
		if (_format.getType() != Format.TIMESTAMP) {
			logger.log(Level.ERROR, () -> sf("%s is not a TIMESTAMP, using default",
					_format.toString()));
			format = Format.TIMESTAMP;
		}
		if (format.isBase()) {
			format = Format.getDefaultFormat(format);
		}
		String formatMask;
		String editPattern;
		String maskLiterals;
		switch(format) {
		case TIMESTAMP_YYYYMMDD_STROKE_HHMMSS_SSSZ -> {
			formatMask = "####-##-## ##:##:##.### *##:##";
			// TODO: try "xxx" not "Z".
			editPattern = "yyyyMMddHHmmssSSSZ";
			maskLiterals = "-: .";
		}
		case TIMESTAMP_YYYYMMDD_STROKE_HHMMSS -> {
			formatMask = "####-##-## ##:##:##";
			editPattern = "yyyyMMddHHmmss";
			maskLiterals = "-: ";
		}
		case TIMESTAMP_YYYYMMDD_STROKE -> {
			formatMask = "####-##-##";
			editPattern = "yyyyMMdd";
			maskLiterals = "-";
		}
		default -> {
			logger.log(Level.ERROR, "Unknown date format type of " + format);
			return null;
		}
		}
		return new SSMaskFormatterFactory.Builder<>(formatMask)
				.converter(new DateFormatter(new SimpleDateFormat(editPattern)))
				.maskLiterals(maskLiterals).placeholder('_')
				.build();
	}
}

