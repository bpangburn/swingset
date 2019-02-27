/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

package com.nqadmin.swingSet.formatting;


/**
 * SSDateField.java
 *
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Used to link a SSFormattedTextField to a date column in a database.
 */

public class SSDateField extends SSFormattedTextField {
    
	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 9138021901389692436L;
	public static final int MMDDYYYY = 0;
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
        this.setValue(new java.util.Date());
    }
    
    /* 
     * Sets the value of the field to the current system date
     */
    public void cleanField() {
        this.setValue(new java.util.Date());
    }
}

/*
 * $Log$
 * Revision 1.8  2006/03/28 16:10:33  prasanth
 * Added a constructor to take the date format needed.
 *
 * Revision 1.7  2005/05/26 22:20:36  dags
 * SSField interface implemented
 *
 * Revision 1.6  2005/03/28 14:46:42  dags
 * syncro commit
 *
 * Revision 1.5  2005/02/04 22:42:06  yoda2
 * Updated Copyright info.
 *
 * Revision 1.4  2005/01/18 22:34:30  dags
 * sincronization update
 *
 * Revision 1.3  2004/12/13 20:50:16  dags
 * Fix package name
 *
 * Revision 1.2  2004/12/13 18:46:13  prasanth
 * Added License.
 *
 */
