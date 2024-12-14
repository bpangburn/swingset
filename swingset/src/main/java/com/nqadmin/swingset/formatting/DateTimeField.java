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

import java.util.List;

import com.nqadmin.swingset.datasources.DateTime;
import com.nqadmin.swingset.datasources.RSC;

/**
 * Base class for date time fields; provides specialized component validation.
 */
@SuppressWarnings("serial")
abstract public class DateTimeField extends Field
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
	 * @param strings text to check: list[0] is masked, list[1] is plain
	 * @param comp
	 * @return false if the component does not have valid data.
	 */
	public static boolean stringValidator(List<String> strings, RSC comp)
	{
		if (!(comp instanceof DateTimeField dtfield))
			return false;
		if (DateTime.isHandledDateTimeComp(dtfield)) {
			if (!dtfield.containsUserText()) {
				if (DateTime.dateTimeColumnValidate("", dtfield))
					return true;
			}
			for (String string : strings) {
				if (DateTime.dateTimeColumnValidate(string, dtfield))
					return true;
			}
		}
		return false;
	}
}
