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

import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;
import java.util.Locale;

import java.io.Serializable;
import java.text.ParseException;
import javax.swing.JFormattedTextField;


/**
 *
 * @author dags
 */
public class SSNumericFormatterFactory extends javax.swing.text.DefaultFormatterFactory implements Serializable {

    public SSNumericFormatterFactory() {
        this.setDefaultFormatter(new NumberFormatter(NumberFormat.getInstance()));
        this.setNullFormatter(null);
        this.setEditFormatter(new NumberFormatter(NumberFormat.getInstance(Locale.US)));
        this.setDisplayFormatter(new NumberFormatter(NumberFormat.getInstance()));
    }
    
    public SSNumericFormatterFactory(int precision, int decimals) {
        NumberFormat nfd = NumberFormat.getInstance(Locale.US);
        nfd.setMaximumFractionDigits(decimals);
        nfd.setMinimumFractionDigits(decimals);
        
        nfd.setMaximumIntegerDigits(precision);
        nfd.setMinimumIntegerDigits(1);
        
        this.setDefaultFormatter(new NumberFormatter(NumberFormat.getInstance()));
        this.setNullFormatter(null);
        this.setEditFormatter(new NumberFormatter(NumberFormat.getInstance(Locale.US)));
        this.setDisplayFormatter(new NumberFormatter(nfd));

    }
}

/*
 * $Log$
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
