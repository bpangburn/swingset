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

//package com.nqadmin.swingset.formatting;

// TODO: THIS NEEDS LOTS OF WORK.
//
// The initial text in here is copied from SSFormattedTextField.
//
// TODO Consider adding back context help and calculators via popups. See 2020-01-07 revisions or earlier.
// TODO Add JDatePicker support or something similar: https://www.codejava.net/java-se/swing/how-to-use-jdatepicker-to-display-calendar-component and https://github.com/JDatePicker/JDatePicker

/**
 * This package contains components based on {@link JFormattedTextField} and has
 * support/base classes for creating these components. The components interoperate
 * with {@link com.nqadmin.swingset.decorators.Decorator}
 * to provide visual feedback as data is entered into the component
 * and {@link com.nqadmin.swingset.decorators.Validator} to detect bogus values
 * as early as possible and avoid them getting into the database.
 * The components also respects {@link RSC#getAllowNull()}.
 * 
 * Formatters and FormatterFactory
 * 
 * <p>
 * OLD STUFF
 * <p>
 * Generally bound components are implemented by extending SSFormattedTextField and
 * instantiating with a custom FormatterFactory. E.g. {@link SSDateField }
 * 
 * <p>
 * It would be possible to instead use a MaskFormatter, but custom code has to be written if the field needs to be nullable/blanked
 * by the user. For a MaskFormatter, this triggers a ParseException, which would need to be caught in the code and surplanted by
 * a call to setValue(null); Using a MaskFormatter still requires additional validation of some sort. E.g. preventing a MM/dd/yyyy date of
 * 99/99/9999 from being entered.
 */
package com.nqadmin.swingset.formatting;

// public class package_info
// {
// }
