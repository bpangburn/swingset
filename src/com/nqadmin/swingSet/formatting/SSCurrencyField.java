/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004-2006, The Pangburn Company, Prasanth R. Pasala and
 * Diego Gil
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  The names of its contributors may not be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.nqadmin.swingSet.formatting;

import java.util.Locale;

import javax.swing.JTextField;

/**
 *
 * @author dags
 */

public class SSCurrencyField extends SSFormattedTextField {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1636264407572416306L;
	private int precision = 3;
    private int decimals  = 2;

    /**
     * Holds value of property minimumIntegerDigits.
     */
    private int minimumIntegerDigits;

    /** 
     * Creates a new instance of SSCurrencyField 
     */
    public SSCurrencyField() {
        this(new SSCurrencyFormatterFactory());
    }
    
    /**
     * Creates an instance of SSCurrenyField with the specified number of integer & fraction digits
     * @param precision - number of digits needed for integer part of the number
     * @param decimals - number of digits needed for the fraction part of the number
     */
    public SSCurrencyField(int precision, int decimals) {
        this(new SSCurrencyFormatterFactory(precision, decimals));
    }
    
    /**
     * Creates an instance of SSCurrenyField with the specified number of integer & fraction digits using the 
     * given locale
     * @param precision - number of digits needed for integer part of the number
     * @param decimals - number of digits needed for the fraction part of the number
     * @param editorLocale -  locale to be used while in editing mode
     * @param displayLocale - locate to be used for displaying the number
     */
    public SSCurrencyField(int precision, int decimals, Locale editorLocale, Locale displayLocale) {
        this(new SSCurrencyFormatterFactory(precision, decimals, editorLocale, displayLocale));
    }
    
    /**
     * Creates an SSCurrencyField with the specified formatter factory
     * @param factory - formatter factory to be used
     */
    public SSCurrencyField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        super(factory);
        this.setHorizontalAlignment(JTextField.RIGHT);
        this.setValue(new java.lang.Double(0.00));
    }
    
    /**
     * Returns the number digits used for integer part of the number
     * @return returns the number digits used for integer part of the number
     */
    public int getPrecision() {
        return precision;
    }
    
    /**
     * Getter for property decimals.
     * @return Value of property decimals.
     */
    public int getDecimals() {
        return decimals;
    }
    
    /**
     * Sets the number of digits needed for integer part of the number
     * @param precision - number of digits needed for integer part of the number
     */
    public void setPrecision(int precision) {
        this.precision = precision;
        this.setFormatterFactory(new SSCurrencyFormatterFactory(precision, decimals));
    }
    
    /**
     * Sets the number of digits needed for fraction part of the number
     * @param decimals - number of digits needed for fraction part of the number
     */
    public void setDecimals(int decimals) {
        this.decimals = decimals;
        this.setFormatterFactory(new SSCurrencyFormatterFactory(precision, decimals));
    }
}

/*
 * $Log$
 * Revision 1.7  2005/05/26 12:12:36  dags
 * added bind(SSRowSet, columnName) method and some java.sql.Types checking and support
 *
 * Revision 1.6  2005/03/28 14:46:42  dags
 * syncro commit
 *
 * Revision 1.5  2005/02/04 22:42:06  yoda2
 * Updated Copyright info.
 *
 * Revision 1.4  2005/01/18 22:34:29  dags
 * sincronization update
 *
 * Revision 1.3  2004/12/13 20:50:16  dags
 * Fix package name
 *
 * Revision 1.2  2004/12/13 18:46:13  prasanth
 * Added License.
 *
 */
