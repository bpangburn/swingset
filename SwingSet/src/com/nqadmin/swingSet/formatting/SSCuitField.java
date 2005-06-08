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

import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;

/**
 * SSCuitField.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * SSCuitField extends the SSFormattedTextField. This class provides an implementation 
 * of Argentina's Tax ID. Every taxpayer in Argentina must have this Government supplied 
 * ID. It is an 10 digits code plus one verifier digit. Display format is ##-########-#.
 *</pre><p>
 * @author $Author$
 * @version $Revision$
 */

public class SSCuitField extends SSFormattedTextField {
  
    private Caret cuitCaret;
  
    /** Creates a new instance of SSCuitFieldField */
    public SSCuitField() {
        this(new SSCuitFormatterFactory());
    }
    
    /** Creates a new instance of SSCuitFieldField */
    public SSCuitField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        super(factory);
        
        cuitCaret = new DefaultCaret();
        cuitCaret.setBlinkRate(600);
        
        try {
            cuitCaret.setSelectionVisible(true);
        } catch(java.lang.NullPointerException np) {}
        

        this.setCaret(cuitCaret);
        
    }

    public boolean validateField(Object value) {

        boolean retValue;

        retValue = CheckCuit((String)value);
        
        return retValue;
    }

/**
 * Computes verifier digit and checks against supplied value.
 *
 * @param  cu    the CUIT value to be verified.
 * @return true if CUIT is valid, false elsewhere.
 */

    public boolean CheckCuit(String cu)
    {
        
        String base = new String("54 32765432  ");
        String c1, c2;
        StringBuffer cuit;
        StringBuffer ctrl;
        
        int mo, ba, mr, i;
        
        ctrl = new StringBuffer(cu);
        cuit = new StringBuffer(cu);
        
        for (mo=0, i=0; i < 12; i++)
        {
            if (i==2 || i==11) continue;
            mo += ((int)base.charAt(i) - (int)'0') * ((int)cuit.charAt(i) - (int)'0');
        }
        
        mr = mo%11;
        
        if (mr==0)
            ba = 0;
        else
            if (mr==1)
            {
            return false;
            }
            else
                ba = 11 - mr;
        
        ctrl.setCharAt(12, (char)((int)ba + (int)'0'));
        
        c1 = new String(cuit);
        c2 = new String(ctrl);
        
        if (c1.compareTo(c2) != 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}

/*
 * $Log$
 *
 */
