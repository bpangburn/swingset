/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004, The Pangburn Company, Inc, Prasanth R. Pasala and
 * Deigo Gil
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

package com.nqadmin.swingSet.testing;

import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;
import java.util.Locale;
import javax.swing.JTextField;


/**
 *
 * @author dags
 */

public class SSIntegerField extends SSFormattedTextField {
    
    private int precision = 1;
    
    /**
     * Holds value of property minimumIntegerDigits.
     */
    private int minimumIntegerDigits;
    
    /** Creates a new instance of PgIntegerField */
    public SSIntegerField() {
        this(new SSIntegerFormatterFactory());
    }
    
    public SSIntegerField(int precision) {
        this(new SSIntegerFormatterFactory(precision));
    }
    
    public SSIntegerField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        super(factory);
        this.setHorizontalAlignment(JTextField.RIGHT);
    }
    
    public int getPrecision() {
        return precision;
    }
    
    public void setPrecision(int precision) {
        this.precision = precision;
        this.setFormatterFactory(new SSIntegerFormatterFactory(precision));
    }
    
    
    /**
     * Getter for property minimumIntegerDigits.
     * @return Value of property minimumIntegerDigits.
     */
    public int getMinimumIntegerDigits() {
        
        return this.minimumIntegerDigits;
    }
    
    /**
     * Setter for property minimumIntegerDigits.
     * @param minimumIntegerDigits New value of property minimumIntegerDigits.
     */
    public void setMinimumIntegerDigits(int minimumIntegerDigits) {
        
        this.minimumIntegerDigits = minimumIntegerDigits;
    }
}

/*
 * $Log$
 */