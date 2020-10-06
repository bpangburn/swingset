/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 ******************************************************************************/

package com.nqadmin.swingset.formatting;

import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.text.NumberFormatter;


// SSNumericFormatterFactory.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * SSNumericFormatterFactory extends DefaultFormatterFactory for numeric fields.
 */
public class SSNumericFormatterFactory extends javax.swing.text.DefaultFormatterFactory {

    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = 3335183379540169892L;
	NumberFormatter dnf = null;
    NumberFormatter enf = null;
    NumberFormatter snf = null;

    /**
     * Creates a default SSNumericFormatterFactory
     */
    public SSNumericFormatterFactory() {
        snf = new NumberFormatter(NumberFormat.getInstance());
        snf.setCommitsOnValidEdit(true);
        setDefaultFormatter(snf);

        setNullFormatter(null);

        enf = new NumberFormatter(NumberFormat.getInstance(Locale.US));
        enf.setCommitsOnValidEdit(true);
        setEditFormatter(enf);

        dnf = new NumberFormatter(NumberFormat.getInstance());
        dnf.setCommitsOnValidEdit(true);
        setDisplayFormatter(dnf);
    }

    /**
     * Creates an object of SSFormatterFactory with the specified precision and decimals
     * @param precision - number of digits needed for integer part of the number
     * @param decimals - number of digits needed for fraction part of the number
     */
    public SSNumericFormatterFactory(final int precision, final int decimals) {
        final NumberFormat nfd = NumberFormat.getInstance(Locale.US);
        nfd.setMaximumFractionDigits(decimals);
        nfd.setMinimumFractionDigits(decimals);

        nfd.setMaximumIntegerDigits(precision);
        nfd.setMinimumIntegerDigits(1);

        snf = new NumberFormatter(NumberFormat.getInstance());
        snf.setCommitsOnValidEdit(true);
        setDefaultFormatter(snf);

        setNullFormatter(null);

        enf = new NumberFormatter(NumberFormat.getInstance(Locale.US));
        enf.setCommitsOnValidEdit(true);
        setEditFormatter(enf);

        dnf = new NumberFormatter(nfd);
        dnf.setCommitsOnValidEdit(true);
        setDisplayFormatter(dnf);
    }
}

