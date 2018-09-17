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
import java.util.Calendar;

/**
 *
 * @author dags
 */

public class SSTimeField extends SSFormattedTextField {
    
    /** 
     * Creates a new instance of SSTimeField 
     */
    public SSTimeField() {
        this(new SSTimeFormatterFactory());
    }
            
    /**
     * Creates an object of SSTimeField with the specified formatter factory
     * @param factory - formatter factory to be used
     */
    public SSTimeField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        super(factory);
        this.setValue( Calendar.getInstance().getTime() );
    }

    /* (non-Javadoc)
     * @see com.nqadmin.swingSet.formatting.SSField#cleanField()
     */
    public void cleanField() {
        this.setValue( Calendar.getInstance().getTime() );
    }
    

}


/*
 * $Log$
 * Revision 1.2  2005/05/26 22:20:36  dags
 * SSField interface implemented
 *
 * Revision 1.1  2005/05/26 12:16:20  dags
 * initial release
 *
  *
 */