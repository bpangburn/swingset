/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004-2005, The Pangburn Company, Prasanth R. Pasala and
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

import com.nqadmin.swingSet.formatting.SSDateFormatterFactory;

/**
 *
 * @author dags
 */

public class SSDateField extends SSFormattedTextField {
    
	public static final int MMDDYYYY = 0;
	public static final int DDMMYYYY = 1;
	
    /** Creates a new instance of SSDateField */
    public SSDateField(int format) {
        this(new SSDateFormatterFactory(format));
    }
            
    public SSDateField(){
    	this(new SSDateFormatterFactory());
    }
    
    public SSDateField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        super(factory);
        this.setValue(new java.util.Date());
    }
    
    public void cleanField() {
        this.setValue(new java.util.Date());
    }
}


/*
 * $Log$
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
