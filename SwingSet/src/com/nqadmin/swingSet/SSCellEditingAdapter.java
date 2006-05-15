/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2006, The Pangburn Company and Prasanth R. Pasala
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

import java.io.Serializable;

/**
 * SSCellEditingAdapter.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * This abstract adapter class is provided as a convenience for creating
 * custom SSCellEditing objects. Extend this class to create a SSCellEditing
 * implementation.
 *
 * SSCellEditingAdapter defines empty functions so that the programmer can define
 * only the functions desired. Both isCellEditable() and cellUpdateRequested()
 * always return true.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
public abstract class SSCellEditingAdapter implements SSCellEditing, Serializable {

    /**
     * This empty implementation always returns true.
     * For description about the function look in SSCellEditing class.
     */
    public boolean isCellEditable(int _row, int _column) {
        return true;
    }

    /**
     * This empty implementation always returns true.
     * For description about the function look in SSCellEditing class.
     */
    public boolean cellUpdateRequested(int _row, int _column, Object _oldValue, Object _newValue){
        return true;
    }

} // end public abstract class SSCellEditingAdapter



/*
 * $Log$
 * Revision 1.7  2005/02/04 22:48:52  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.6  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.5  2004/08/10 22:06:58  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.4  2004/08/02 14:29:46  prasanth
 * implements serializable.
 *
 * Revision 1.3  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.2  2003/12/18 20:13:00  prasanth
 * Update class description.
 *
 * Revision 1.1  2003/12/16 18:02:47  prasanth
 * Initial version.
 *
 */