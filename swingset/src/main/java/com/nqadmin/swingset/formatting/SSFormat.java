/* *****************************************************************************
 * Copyright (C) 2022, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 * ****************************************************************************/
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.formatting;

/**
 * The various formats that are supported for use in JFormattedTextField.
 * There are Types, such as DATE and subtypes such as DATE_MMDDYYYY.
 * If the type is used, this is the default format.
 */
// TODO: Need to make Format available for when somethings parsed by DateTime,
//		 consider MMDDYYYY vs DDMMYYYY. The "editPattern" with the "*Field"
//		 components has that info. Want the field tooltip for the format.
//		 Maybe end up with a "FormatInfo" that has format specific stuff.
//
//		 Consider: Format has SLASH vs STROKE property.
//
//		 One question, how can a user define a Format? Have a base
//		 "class Format<E extends Enum<E>>" that can be extended?
//
public enum SSFormat {
	/** special circumstances or if format not needed (testing) */
	CUSTOM,
	/** default date format */
	DATE,
	/** Date MMDDYYYY */
	DATE_MMDDYYYY_SLASH  (DATE),
	/** Date DDMMYYYY */
	DATE_DDMMYYYY_SLASH (DATE),
	/** Date YYYYMMDD */
	DATE_YYYYMMDD_STROKE (DATE),

	/** default time format */
	TIME,
	/** Time HHMMSS */
	TIME_HHMMSS (TIME),

	/** default timestamp format */
	TIMESTAMP,
	/** Timestamp big */
	TIMESTAMP_YYYYMMDD_STROKE_HHMMSS_SSSZ (TIMESTAMP),
	/** Timestamp date-time, no milliseconds timezone */
	TIMESTAMP_YYYYMMDD_STROKE_HHMMSS (TIMESTAMP),
	/** Timestamp date, no date, milliseconds, timezone */
	TIMESTAMP_YYYYMMDD_STROKE (TIMESTAMP),
	;

	// TODO: look up the format as needed.
	static SSFormat getAssignedFormat(SSFormat format) {
		return format.isBase() ? getDefaultFormat(format) : format;
	}

	static SSFormat getDefaultFormat(SSFormat _format) {
		return switch(_format.getType()) {
		case DATE -> DATE_MMDDYYYY_SLASH;
		case TIME -> TIME_HHMMSS;
		//case TIMESTAMP -> TIMESTAMP_YYYYMMDD_HHMMSS;
		case TIMESTAMP -> TIMESTAMP_YYYYMMDD_STROKE_HHMMSS_SSSZ;
		default -> null;
		};
	}

	final private SSFormat type;

	/** the default has no args */
	SSFormat() {
		type = this;
	}

	SSFormat(SSFormat _base) {
		type = _base;
	}

	/** Is this enum value the default for the given type
	 * @return true if this enum is the default.
	 */
	boolean isBase() {
		return type == this;
	}

	/** The type of this format, e.g. DATE. 
	 * @return type
	 */
	public SSFormat getType() {
		return type;
	}
}
