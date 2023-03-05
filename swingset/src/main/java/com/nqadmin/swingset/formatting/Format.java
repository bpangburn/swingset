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
package com.nqadmin.swingset.formatting;

/**
 * The various formats that are supported. There are Types, such as DATE and
 * subtypes such as DATE_MMDDYYYY. If the type is used, this is the default
 * format.
 */
public enum Format {
	/** default date format */
	DATE,
	/** Date MMDDYYYY */
	DATE_MMDDYYYY (DATE),
	/** Date DDMMYYYY */
	DATE_DDMMYYYY (DATE),
	/** Date YYYYMMDD */
	DATE_YYYYMMDD (DATE),
	/** default currency format */
	CURRENCY,
	/** US Currency: $#.## */
	CURRENCY_US (CURRENCY)
	;

	final private Format type;

	/** the default has no args */
	Format() {
		type = this;
	}

	Format(Format _base) {
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
	public Format getType() {
		return type;
	}

	static Format getDefaultFormat(Format _format) {
		switch(_format.getType()) {
		case DATE: return DATE_DDMMYYYY;
		case CURRENCY: return CURRENCY_US;
		}
		return null;
	}
}
