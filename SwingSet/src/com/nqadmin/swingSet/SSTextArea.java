/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2004, The Pangburn Company, Inc. and Prasanth R. Pasala
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

package com.nqadmin.swingSet;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import com.nqadmin.swingSet.datasources.SSRowSet;

/**
 * SSTextArea.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>	
 * SSTextArea extends the JTextArea to add rowset binding.
 *</pre><p>
 * @author $Author$
 * @version	$Revision$
 */
public class SSTextArea extends JTextArea {
	 
    /**
     * Binds the text field to a SSTextDocument which is in turn bound to
     * a particular column in a SSRowSet.
     *
     * @param _rowset  SSRowSet containing column to bind to
     * @param _columnName  name of column within RowSet to bind to
     */
	public void bind(SSRowSet _rowset, String _columnName) {
        this.setDocument(new SSTextDocument(_rowset, _columnName));
    }
	
} // end public class SSTextArea extends JTextArea {



/*
 * $Log$
 * Revision 1.1  2004/10/01 20:43:53  yoda2
 * Added SSTextArea which is a simple extension of JTextArea with support for rowset binding.
 *
  *
 */