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

import com.nqadmin.swingset.datasources.DateTime;

/**
 * Base class for date time fields; provides specialized component validation.
 */
@SuppressWarnings("serial")
abstract public class DateTimeField extends SSFormattedTextField
{
	/**
	 * Create.
	 * @param factory formatter factory
	 */
	public DateTimeField(AbstractFormatterFactory factory) {
        super(factory);
	}

	/**
	 * Sets the value of the field to an initial state consistent with
	 * the AllowNull property. If not AllowNull then use the current system date.
	 */
	@Override
	public void cleanField() {
		if (getAllowNull()) {
			setValue(null);
		} else {
			setValue(new java.util.Date());
		}
	}

	/**
	 * Specialized Date/Time/Timestamp component validation.
	 * @return false if the component does not have valid data.
	 */
	@Override
	public boolean componentValidate()
	{
		if (DateTime.isHandledDateTimeComp(this)) {
			String text = getText();
			// Check if the MaskFormatter has data;
			// if not then treat it as an empty string.
			if (!containsUserText()) {
				text = "";
			}
			if (!DateTime.dateTimeColumnValidate(text, this)) {
				return false;
			}
		}

		return true;
	}
}
