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

import java.text.SimpleDateFormat;

import javax.swing.text.DateFormatter;


/**
 * SSDateFormatterFactory.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * SSDateFormatterFactory extends DefaultFormatterFactory for Date fields.
 */
public class SSDateFormatterFactory extends javax.swing.text.DefaultFormatterFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8205600502325364394L;

	/**
	 * Constant for date format
	 */
	public static final int MMDDYYYY = 0;
	
	/**
	 * Constant for date format
	 */
	public static final int DDMMYYYY = 1;
	
    /**
     * Constructs a default SSDateFormatterFactory. 
     */
    public SSDateFormatterFactory() {
        this.setDefaultFormatter(new DateFormatter(new SimpleDateFormat("DD/mm/yyyy")));
        this.setNullFormatter(null);
        this.setEditFormatter(new DateFormatter(new SimpleDateFormat("ddMMyyyy")));
        this.setDisplayFormatter(new DateFormatter(new SimpleDateFormat("MMM dd, yyyy")));
    } 
    
    /**
     * Creates an object of SSDateFormatterFactory with the specified format.
     * @param format  - format to be used for date while in editing mode.
     * The default format is DDMMYYYY
     */
    public SSDateFormatterFactory(int format) {
    	switch(format){
    	case 0:
    		this.setDefaultFormatter(new DateFormatter(new SimpleDateFormat("MM/dd/yyyy")));
            this.setNullFormatter(null);
            this.setEditFormatter(new DateFormatter(new SimpleDateFormat("MMddyyyy")));
            this.setDisplayFormatter(new DateFormatter(new SimpleDateFormat("MMM dd, yyyy")));
    		break;
    	case 1:
    		this.setDefaultFormatter(new DateFormatter(new SimpleDateFormat("DD/mm/yyyy")));
            this.setNullFormatter(null);
            this.setEditFormatter(new DateFormatter(new SimpleDateFormat("ddMMyyyy")));
            this.setDisplayFormatter(new DateFormatter(new SimpleDateFormat("MMM dd, yyyy")));
    		break;
    	default:
        	System.out.println("Unknown date format type of " + format);
        	break;
    	}
    }
}

/*
 * $Log$
 * Revision 1.6  2006/03/28 16:09:40  prasanth
 * Added a constructor to take the date format.
 *
 * Revision 1.5  2005/02/04 22:42:06  yoda2
 * Updated Copyright info.
 *
 * Revision 1.4  2005/01/18 23:37:59  dags
 * Diego's name fix
 *
 * Revision 1.3  2004/12/13 20:50:16  dags
 * Fix package name
 *
 * Revision 1.2  2004/12/13 18:46:13  prasanth
 * Added License.
 *
 */
