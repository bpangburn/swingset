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

import java.awt.Color;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;

/**
 *
 * @author dags
 */
public class SSFormattedTextFieldCombo extends JComboBox implements ListCellRenderer, ComboBoxEditor {
    
    private SSFormattedTextField editor;
    private SSFormattedTextField renderer;
    
    /** Creates a new instance of SSFormattedTextFieldCombo */
    public SSFormattedTextFieldCombo() {
        
        editor   = new SSFormattedTextField();
        renderer = editor;

        this.setRenderer(this);
        this.setEditor(this);

    }
    
    /**
     *
     * BasicComboBoxEditor implementation
     *
     *
     */

    public void setItem(Object anObject) {
        editor.setValue(anObject);
    }

    public void selectAll() {
        
    }

    public Object getItem() {
        return editor.getValue();
    }

    public java.awt.Component getEditorComponent() {
        return editor;
    }

    /**
     *
     *   ListCellRenderer implementation
     *
     */
    public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        if (isSelected == false) {
            renderer.setBackground(Color.WHITE);
            renderer.setForeground(Color.BLACK);
            renderer.setBorder(null);
        }
        else {
            renderer.setBackground(Color.BLUE);
            renderer.setForeground(Color.WHITE);
            renderer.setBorder(null);
        }
        
        if (cellHasFocus){
            
        }
        else {
            
        }
        renderer.setValue(value);
        return renderer;
        
    }

}
