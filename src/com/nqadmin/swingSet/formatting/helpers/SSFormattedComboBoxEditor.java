/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Contributors:
 *   Prasanth R. Pasala
 *   Brian E. Pangburn
 *   Diego Gil
 *   Man "Bee" Vo
 ******************************************************************************/

package com.nqadmin.swingSet.formatting.helpers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxEditor;
import javax.swing.InputVerifier;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.datasources.SSRowSet;
import com.nqadmin.swingSet.formatting.SSFormattedComboBox;
import com.nqadmin.swingSet.formatting.SSFormattedTextField;


/**
 * SSFormattedComboBoxEditor.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Used to support editing of values in SSFormattedComboBox.
 *  
 * This class is experimental an not recommended for production use. It may be materially modified
 * or depreciated.
 *
 */
public class SSFormattedComboBoxEditor extends JPanel implements ComboBoxEditor, PropertyChangeListener, MouseListener {
    
    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = -820040230616079711L;
	protected SSFormattedTextField editor;
    protected JLabel display;
    
    private SelectorElement editValue = null;
    
    private String columnName = null;
    private SSDataNavigator navigator = null;
    private SSRowSet rowset = null;
    private SSFormattedComboBox combo = null;
    private SSFormattedComboBoxModel model = null;
    private Border border = null;
    
    /**
     * 
     */
    public SSFormattedComboBoxEditor() {
        
        border = UIManager.getBorder("ComboBox.border");
        
        if (border == null)
            border = BorderFactory.createLineBorder(Color.BLACK, 1);
        
        this.setOpaque(true);
        this.setBorder(border);
        
        editor = new SSFormattedTextField();
        editor.setBorder(null);
        editor.setPreferredSize(new Dimension(50,this.getHeight()));
        editor.addPropertyChangeListener(this);
        
        display = new JLabel();
        display.setBorder(null);
        display.setPreferredSize(new Dimension(250,this.getHeight()));
        display.setFocusable(false);
        display.setFont(new Font(display.getFont().getName(), Font.BOLD, 11));
        display.addMouseListener(this);
        
        this.setBackground(new Color(204,255,204));
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(editor);
        this.add(display);
    }
    
    /**
     * @param combo
     */
    @SuppressWarnings("deprecation")
	public void setComboBox(SSFormattedComboBox combo) {
        this.combo = combo;
        this.model = (SSFormattedComboBoxModel)this.combo.getModel();
        this.editor.setSSFormattedComboBox(combo);
    }
    
    /**
     * @param value
     */
    public void setValue(Object value) {
        editValue = (SelectorElement) value;
        updateField(editValue);
    }
    
    /**
     * @param columnName
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
        editor.setColumnName(columnName);
    }
    
    /**
     * @return database column name
     */
    public String getColumnName() {
        return columnName;
    }
    
    /**
     * @param navigator
     */
    @Deprecated
    public void setNavigator(SSDataNavigator navigator) {
        this.navigator = navigator;
        editor.setNavigator(navigator);
    }
    
    /**
     * @return SSDataNavigator
     */
    public SSDataNavigator getNavigator() {
        return navigator;
    }
    
    /**
     * @param rowset
     */
    public void setSSRowSet(SSRowSet rowset) {
        this.rowset = rowset;
        editor.setSSRowSet(rowset);
    }
    
    /**
     * @return SSRowSet
     */
    public SSRowSet getSSRowSet() {
        return rowset;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ComboBoxEditor#getEditorComponent()
     */
    public Component getEditorComponent() {
        return this;
    }
    
    /**
     * @return associated SSFormattedTextField
     */
    public SSFormattedTextField getEditorField() {
        return editor;
    }
    
    /**
     * @return JLabel column label
     */
    public JLabel getDisplayField() {
        return display;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ComboBoxEditor#setItem(java.lang.Object)
     */
    public void setItem(Object anObject) {
        
        if (anObject == null) {
            updateField(new SelectorElement(null,null));
            return;
        }
        
        editValue = (SelectorElement) anObject;
        System.out.println("setItem(" + anObject + ")");
        updateField(anObject);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ComboBoxEditor#getItem()
     */
    public Object getItem() {
        
        try {
            editor.commitEdit();
        } catch (ParseException pe) {
            System.out.println("getItem ParseException " + pe);
        }
        
        // debo buscar el elemento, segun data_value.
        return updateField(new SelectorElement(editor.getValue(),null));
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ComboBoxEditor#selectAll()
     */
    public void selectAll() {
        editor.selectAll();
        editor.requestFocus();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ComboBoxEditor#addActionListener(java.awt.event.ActionListener)
     */
    public void addActionListener(ActionListener l) {
        editor.addActionListener(l);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ComboBoxEditor#removeActionListener(java.awt.event.ActionListener)
     */
    public void removeActionListener(ActionListener l) {
        editor.removeActionListener(l);
    }
    
    
    /**
     * @param columns
     */
    public void setEditColumns(int columns) {
        if (editor != null)
            editor.setColumns(columns);
    }
    
    
    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        //Object oValue = null;
        Object nValue = null;
        
        if (evt.getSource().equals(editor)) {
            
            if (evt.getPropertyName() == "value") {
                
                //oValue = evt.getOldValue();
                nValue = evt.getNewValue();
                
                if (nValue == null) {
                    display.setText(null);
                } else {
                    // debo buscar el elemento, segun data_value.
                    updateField(new SelectorElement(nValue,null));
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        System.out.println("mouseClicked");
        this.combo.showPopup();
    }
    
    /**
     * @param nValue new value for List
     * @return Object with edited value
     */
    private Object updateField(Object nValue) {
        
        SelectorElement item = (SelectorElement)nValue;
        
        int index = -99;
        
        try {
            if (model == null) {
                editor.setValue("null");
                display.setText("null element");
                display.setToolTipText("model is null !!");
                return editValue;
            }
            
            index = model.indexOf(item.getDataValue());
            
            if (index >= 0) {
                editValue = (SelectorElement)model.getElementAt(index);
                model.setSelectedItem(editValue);
                editor.setValue(editValue.getDataValue());
                display.setText(editValue.getListValue().toString());
                display.setToolTipText(editValue.getListValue().toString());
            }
        } catch (java.lang.NullPointerException npe) {
            System.out.println("updateField : ");
            npe.printStackTrace();
        }
        return editValue;
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#getInputVerifier()
     */
    public InputVerifier getInputVerifier() {

        InputVerifier retValue;
        System.out.println("SSFormattedComboBoxEditor.getInputVerifier()");
        retValue = editor.getInputVerifier();
        return retValue;
    }
}

/*
* $Log$
* Revision 1.2  2006/04/21 19:09:17  prasanth
* Added CVS tags & some comments
*
*/
