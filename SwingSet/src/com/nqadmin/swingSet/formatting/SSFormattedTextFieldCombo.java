/*
 * SSFormattedTextFieldCombo.java
 *
 * Created on 20 de diciembre de 2004, 16:19
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
