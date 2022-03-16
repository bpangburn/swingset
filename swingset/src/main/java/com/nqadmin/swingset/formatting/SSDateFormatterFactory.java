/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 *   Ernie R. Rael
 ******************************************************************************/
package com.nqadmin.swingset.formatting;

import java.text.SimpleDateFormat;

import javax.swing.text.DateFormatter;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.utils.SSUtils;

// SSDateFormatterFactory.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * SSDateFormatterFactory extends DefaultFormatterFactory for Date fields.
 */
public class SSDateFormatterFactory extends javax.swing.text.DefaultFormatterFactory {

	/**
	 * Constant for dd/MM/yyyy date format
	 */
	public static final int DDMMYYYY = 1;

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = SSUtils.getLogger();

	/**
	 * Constant for MM/dd/yyyy date format
	 */
	public static final int MMDDYYYY = 0;

	/**
	 * Unique serial ID
	 */
	private static final long serialVersionUID = -8205600502325364394L;

	/**
	 * Constant for yyyy-MM-dd date format
	 */
	public static final int YYYYMMDD = 2;

    /**
     * Constructs a default SSDateFormatterFactory.
     */
    public SSDateFormatterFactory() {
    	this(DDMMYYYY);
    }

    /**
     * Creates an object of SSDateFormatterFactory with the specified format.
     * See https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
     *
     * @param format - Format to be used for date while in editing mode. The default format is DDMMYYYY
     */
    public SSDateFormatterFactory(final int format) {
    	switch(format){
    	case MMDDYYYY:
    		setDefaultFormatter(new DateFormatter(new SimpleDateFormat("MM/dd/yyyy")));
            setNullFormatter(null);
            setEditFormatter(new DateFormatter(new SimpleDateFormat("MMddyyyy")));
            setDisplayFormatter(new DateFormatter(new SimpleDateFormat("MM/dd/yyyy")));
    		break;
    	case DDMMYYYY:
			setDefaultFormatter(new DateFormatter(new SimpleDateFormat("dd/MM/yyyy")));
			setNullFormatter(null);
			setEditFormatter(new DateFormatter(new SimpleDateFormat("ddMMyyyy")));
			setDisplayFormatter(new DateFormatter(new SimpleDateFormat("dd/MM/yyyy")));
    		break;
    	case YYYYMMDD:
			setDefaultFormatter(new DateFormatter(new SimpleDateFormat("yyyy-MM-dd")));
			setNullFormatter(null);
			setEditFormatter(new DateFormatter(new SimpleDateFormat("yyyyMMdd")));
			setDisplayFormatter(new DateFormatter(new SimpleDateFormat("yyyy-MM-dd")));
    		break;
    	default:
    		logger.warn("Unknown date format type of " + format);
        	break;
    	}
    }
}
