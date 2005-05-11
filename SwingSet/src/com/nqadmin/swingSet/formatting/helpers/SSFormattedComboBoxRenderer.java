package com.nqadmin.swingSet.formatting.helpers;

import javax.swing.*;
import java.awt.*;

import java.io.Serializable;

public class SSFormattedComboBoxRenderer extends JPanel implements ListCellRenderer, Serializable {
    
    private JLabel listLabel = null;
    private SelectorElement el = null;

    private Color evenRowBackground = Color.WHITE;
    private Color evenRowForeground = Color.BLACK;
    
    private Color oddRowBackground = new Color(204,255,204);
    private Color oddRowForeground = Color.BLACK;
    
    
    public SSFormattedComboBoxRenderer() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(true);

        listLabel = new JLabel();
        listLabel.setBorder(null);
        listLabel.setHorizontalAlignment(JLabel.LEADING);
        
        add(listLabel);
    }

    public void setOddRowBackground(Color color) {
        this.oddRowBackground = color;
    }

    public void setEvenRowBackground(Color color) {
        this.evenRowBackground = color;
    }

    public void setOddRowForeground(Color color) {
        this.oddRowForeground = color;
    }

    public void setEvenRowForeground(Color color) {
        this.evenRowForeground = color;
    }

    public Color getOddRowBackground() {
        return oddRowBackground;
    }

    public Color getEvenRowBackground() {
        return evenRowBackground;
    }

    public Color getOddRowForeground() {
        return oddRowForeground;
    }

    public Color getEvenRowForeground() {
        return evenRowForeground;
    }
    
    public Dimension getPreferredSize() {
        
        Dimension size;
        
        if ((this.listLabel.getText() == null) || (this.listLabel.getText().equals( "" )) ) {
            listLabel.setText( " " );
            size = super.getPreferredSize();
            listLabel.setText( "" );
        } else {
            size = super.getPreferredSize();
        }
        return size;
    }
    
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
/*    
        if (isSelected == false && cellHasFocus == false) System.out.println("getListCellRendererCoponent()               " + value);
        if (isSelected == false && cellHasFocus == true ) System.out.println("getListCellRendererCoponent()          FOCUS" + value);
        if (isSelected == true  && cellHasFocus == false) System.out.println("getListCellRendererCoponent() SELECTED      " + value);
        if (isSelected == true  && cellHasFocus == true ) System.out.println("getListCellRendererCoponent() SELECTED FOCUS" + value);
*/        
        el = (SelectorElement)value;
        
        if (index == -1) {
            setBackground(UIManager.getColor("ComboBox.background"));
            setForeground(UIManager.getColor("ComboBox.foreground"));
        }
        
        
        if (isSelected) {
            setBackground(UIManager.getColor("ComboBox.selectionBackground"));
            setForeground(UIManager.getColor("ComboBox.selectionForeground"));
        } else {
            switch (index % 2) {
                case 0:
                    setBackground(evenRowBackground);
                    setForeground(evenRowForeground);
                    break;
                case 1:
                    setBackground(oddRowBackground);
                    setForeground(oddRowForeground);
                    break;
            }
        }
        
        setFont(list.getFont());
        
        listLabel.setText((value == null) ? "<null>" : el.getListValue().toString() + " (" + el.getDataValue().toString() + ")");
        listLabel.setToolTipText((value == null) ? "<null>" : el.getListValue().toString());
        
        return this;
    }
}


