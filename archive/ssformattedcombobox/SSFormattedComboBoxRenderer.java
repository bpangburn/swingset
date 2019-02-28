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
import java.io.Serializable;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 * SSFormattedComboBoxRenderer.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Used to support editing of values in SSFormattedComboBox.
 *  
 * This class is experimental an not recommended for production use. It may be materially modified
 * or depreciated.
 *
 */
public class SSFormattedComboBoxRenderer extends JPanel implements ListCellRenderer<Object>, Serializable {
    
    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = 4893131319725819812L;
	private JLabel listLabel = null;
    private SelectorElement el = null;

    private Color evenRowBackground = Color.WHITE;
    private Color evenRowForeground = Color.BLACK;
    
    private Color oddRowBackground = new Color(204,255,204);
    private Color oddRowForeground = Color.BLACK;
    
    
    /**
     * 
     */
    public SSFormattedComboBoxRenderer() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(true);

        listLabel = new JLabel();
        listLabel.setBorder(null);
        listLabel.setHorizontalAlignment(JLabel.LEADING);
        
        add(listLabel);
    }

    /**
     * @param color
     */
    public void setOddRowBackground(Color color) {
        this.oddRowBackground = color;
    }

    /**
     * @param color
     */
    public void setEvenRowBackground(Color color) {
        this.evenRowBackground = color;
    }

    /**
     * @param color
     */
    public void setOddRowForeground(Color color) {
        this.oddRowForeground = color;
    }

    /**
     * @param color
     */
    public void setEvenRowForeground(Color color) {
        this.evenRowForeground = color;
    }

    /**
     * @return background color for odd rows
     */
    public Color getOddRowBackground() {
        return oddRowBackground;
    }

    /**
     * @return background color for even rows
     */
    public Color getEvenRowBackground() {
        return evenRowBackground;
    }

    /**
     * @return text/foreground color for odd rows
     */
    public Color getOddRowForeground() {
        return oddRowForeground;
    }

    /**
     * @return text/foreground color for even rows
     */
    public Color getEvenRowForeground() {
        return evenRowForeground;
    }
    
    /* (non-Javadoc)
     * @see java.awt.Component#getPreferredSize()
     */
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
    
    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
       
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

/* 
* $Log$
* Revision 1.2  2006/04/21 19:09:17  prasanth
* Added CVS tags & some comments
*
*/