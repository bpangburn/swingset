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

import java.io.Serializable;

import javax.swing.text.MaskFormatter;

/**
 * SSCuitFormatterFactory.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * SSCuitFormatterFactory extends DefaultFormattertFactory.
 *</pre><p>
 * @author $Author$
 * @version $Revision$
 */

public class SSCuitFormatterFactory extends javax.swing.text.DefaultFormatterFactory implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3206796666162203982L;
	private MaskFormatter defaultFormatter;
    private MaskFormatter displayFormatter;
    private MaskFormatter editFormatter;
    private MaskFormatter nullFormatter;
    
    /**
     * Creates an default object of SSCuitFormatterFactory
     */
    public SSCuitFormatterFactory() {
        
        try {
            defaultFormatter = new MaskFormatter("##-########-#");
            nullFormatter    = null;
            editFormatter    = new MaskFormatter("##-########-#");
            displayFormatter = new MaskFormatter("##-########-#");

            defaultFormatter.setPlaceholderCharacter('0');
            defaultFormatter.setAllowsInvalid(false);
            
            editFormatter.setPlaceholderCharacter('0');
            editFormatter.setAllowsInvalid(false);

            displayFormatter.setPlaceholderCharacter('0');
            displayFormatter.setAllowsInvalid(false);
            
            this.setDefaultFormatter(defaultFormatter);
            this.setNullFormatter(nullFormatter);
            this.setEditFormatter(editFormatter);
            this.setDisplayFormatter(displayFormatter);
        }
        catch (java.text.ParseException pe) {
            
        }
    }
}

/*
 * $Log$
 * Revision 1.1  2005/06/08 02:26:02  dags
 * initial release
 *
 *
 */
