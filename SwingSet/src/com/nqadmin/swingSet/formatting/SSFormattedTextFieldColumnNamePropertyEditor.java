/*
 * SSFormattedTextFieldColumnNamePropertyEditor.java
 *
 * Created on 19 de enero de 2005, 19:16
 */

package com.nqadmin.swingSet.formatting;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.beans.*;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author dags
 */
public class SSFormattedTextFieldColumnNamePropertyEditor extends PropertyEditorSupport implements ActionListener {
    
    private String cn = "<none>";
    
    public SSFormattedTextFieldColumnNamePropertyEditor() {
        super();
        cn = new String("<none>");
    }
    
    public boolean supportsCustomEditor() {
        return true;
    }
    
    public String[] getTags() {
        
        String[] retValue = {
            "<none>",
            "account_id",
                    "account_name",
                    "currency_id",
                    "instructions"
        };
        return retValue;
    }
    
    public void setAsText(String sValue) {
        cn = sValue;
        setValue(cn);
        firePropertyChange();
    }
    
    public String getAsText() {
        return cn;
    }
    
    public String getJavaInitializationString() {
        String is = null;
        
        if (cn == "<none>") return null;
        
        is = "\"" + cn + "\"";
        
        return is;
    }
    
    
    public java.awt.Component getCustomEditor() {
        JComboBox jcombo = null;
        JLabel lEditor = null;
        JPanel cEditor = null;
        
        cEditor = new JPanel();
        lEditor = new JLabel("Column Name");
        jcombo = new JComboBox(getTags());
        jcombo.addActionListener(this);
        
        cEditor.setLayout(new BorderLayout());
//        cEditor.add(lEditor, BorderLayout.NORTH);
        cEditor.add(jcombo, BorderLayout.NORTH);
        
        return cEditor;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        this.setAsText((String)(((JComboBox)e.getSource()).getSelectedItem()));
    }
    
}
