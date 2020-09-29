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


// SSDateField.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to link a SSFormattedTextField to a date column in a database.
 */

public class SSDateField extends SSFormattedTextField {
    
	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 9138021901389692436L;
	
	/**
	 * constant representing the mm/dd/yyyy date format
	 */
	public static final int MMDDYYYY = 0;
	
	/**
	 * constant representing the dd/mm/yyyy date format
	 */
	public static final int DDMMYYYY = 1;
	
    /**
     *  Creates a new instance of SSDateField with the specified format 
     *  @param format - format to be used while the date field is in edit mode
     *  allowed values are MMDDYYYY or DDMMYYYY
     */
    public SSDateField(int format) {
        this(new SSDateFormatterFactory(format));
    }
            
    /**
     *  Creates a default SSDateField object
     */
    public SSDateField(){
    	this(new SSDateFormatterFactory());
    }
    
    /**
     * Creates an object of SSDateField with the specified formatter factory
     * @param factory - formatter factory to be used 
     */
    public SSDateField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        super(factory);
        // TODO Consider setting to null vs system date.
        this.setValue(new java.util.Date());
    }
    
    /* 
     * Sets the value of the field to the current system date
     */
    @Override
	public void cleanField() {
    	// TODO Consider setting to null vs system date.
        this.setValue(new java.util.Date());
    }
}