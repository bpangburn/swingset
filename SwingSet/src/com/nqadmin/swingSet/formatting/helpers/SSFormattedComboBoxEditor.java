package com.nqadmin.swingSet.formatting.helpers;

import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.datasources.SSRowSet;
import com.nqadmin.swingSet.formatting.SSFormattedComboBox;
import com.nqadmin.swingSet.formatting.SSFormattedTextField;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;


import java.text.ParseException;
import javax.swing.border.Border;



public class SSFormattedComboBoxEditor extends JPanel implements ComboBoxEditor, PropertyChangeListener, MouseListener {
    
    protected SSFormattedTextField editor;
    protected JLabel display;
    
    private SelectorElement editValue = null;
    
    private int editColumns = 10;
    private int displayColumns = 50;
    private String columnName = null;
    private SSDataNavigator navigator = null;
    private SSRowSet rowset = null;
    private SSFormattedComboBox combo = null;
    private SSFormattedComboBoxModel model = null;
    private Border border = null;
    
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
    
    public void setComboBox(SSFormattedComboBox combo) {
        this.combo = combo;
        this.model = (SSFormattedComboBoxModel)this.combo.getModel();
        this.editor.setSSFormattedComboBox(combo);
    }
    
    public void setValue(Object value) {
        editValue = (SelectorElement) value;
        updateField(editValue);
    }
    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
        editor.setColumnName(columnName);
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    public void setNavigator(SSDataNavigator navigator) {
        this.navigator = navigator;
        editor.setNavigator(navigator);
    }
    
    public SSDataNavigator getNavigator() {
        return navigator;
    }
    
    public void setSSRowSet(SSRowSet rowset) {
        this.rowset = rowset;
        editor.setSSRowSet(rowset);
    }
    
    public SSRowSet getSSRowSet() {
        return rowset;
    }
    
    public Component getEditorComponent() {
        return this;
    }
    
    public SSFormattedTextField getEditorField() {
        return editor;
    }
    
    public JLabel getDisplayField() {
        return display;
    }
    
    public void setItem(Object anObject) {
        
        if (anObject == null) {
            updateField(new SelectorElement(null,null));
            return;
        }
        
        editValue = (SelectorElement) anObject;
        System.out.println("setItem(" + anObject + ")");
        updateField(anObject);
    }
    
    public Object getItem() {
        
        int index = -99;
        
        try {
            editor.commitEdit();
            System.out.println("getItem() :" + editor.getValue());
            
        } catch (ParseException pe) {
            System.out.println("getItem ParseException " + pe);
        }
        
        // debo buscar el elemento, segun data_value.
        return updateField(new SelectorElement(editor.getValue(),null));
    }
    
    public void selectAll() {
        editor.selectAll();
        editor.requestFocus();
    }
    
    public void addActionListener(ActionListener l) {
        editor.addActionListener(l);
    }
    
    public void removeActionListener(ActionListener l) {
        editor.removeActionListener(l);
    }
    
    
    public void setEditColumns(int columns) {
        this.editColumns = columns;
        if (editor != null)
            editor.setColumns(columns);
    }
    
    public void setDisplayColumns(int columns) {
        this.displayColumns = columns;
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        Object oValue = null;
        Object nValue = null;
        
        if (evt.getSource().equals(editor)) {
            
            if (evt.getPropertyName() == "value") {
                
                oValue = evt.getOldValue();
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
    
    public void mouseReleased(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseClicked(MouseEvent e) {
        System.out.println("mouseClicked");
        this.combo.showPopup();
    }
    
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
                //editor.getInputVerifier().verify(editor);
                display.setText(editValue.getListValue().toString());
                display.setToolTipText(editValue.getListValue().toString());
            }
        } catch (java.lang.NullPointerException npe) {
            System.out.println("updateField : ");
            npe.printStackTrace();
        }
        return editValue;
    }

    public InputVerifier getInputVerifier() {

        InputVerifier retValue;
        System.out.println("SSFormattedComboBoxEditor.getInputVerifier()");
        retValue = editor.getInputVerifier();
        return retValue;
    }
}
