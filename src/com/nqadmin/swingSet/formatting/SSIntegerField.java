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
    
    /** 
     * Creates a new instance of PgIntegerField 
     */
    public SSIntegerField() {
        this(new SSIntegerFormatterFactory());
    }
    
    /**
     * Creates an object of SSIntegerField with the specified number of digits.
     * @param precision - number of digits needed
     */
    public SSIntegerField(int precision) {
        this(new SSIntegerFormatterFactory(precision));
    }
    
    /**
     * Creates an object of SSIntegerField with the specified formatter factory
     * @param factory - formatter factory to be used
     */
    public SSIntegerField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        super(factory);
        this.setHorizontalAlignment(JTextField.RIGHT);
    }
    
    /**
     * Returns the number of digits used for displaying integer 
     * @return returns the number of digits used for displaying integer
     */
    public int getPrecision() {
        return precision;
    }
    
    /**
     * Sets the number of digits needed to display the number
     * @param precision - number of digits to be used to display the number
     */
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
 * Revision 1.6  2005/03/28 14:46:42  dags
 * syncro commit
 *
 * Revision 1.5  2005/02/04 22:42:06  yoda2
 * Updated Copyright info.
 *
 * Revision 1.4  2005/01/18 23:38:01  dags
 * Diego's name fix
 *
 * Revision 1.3  2004/12/13 20:50:16  dags
 * Fix package name
 *
 * Revision 1.2  2004/12/13 18:46:13  prasanth
 * Added License.
 *
 */
