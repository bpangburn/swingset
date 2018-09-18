/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004-2006, The Pangburn Company, Prasanth R. Pasala and
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

package com.nqadmin.swingSet.formatting.helpers;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuListener;

import com.nqadmin.swingSet.datasources.SSRowSet;
import com.nqadmin.swingSet.formatting.SSFormattedTextField;

/**
 *
 * @author dags
 */
public class RowSetHelperPopup extends JPopupMenu implements MouseListener, KeyListener, ActionListener, ListSelectionListener, PopupMenuListener, FocusListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -469422538841985553L;
	private JPanel spane;
    private JPanel buttons;
    private JPanel tpane;
    private JButton searchButton;
    private JButton closeButton;
    private JButton refreshButton;
    private JButton helpButton;
    private JTextField searchText;
    
    private String dataColumn = null;
    private String listColumn = null;
   
    private SSFormattedTextField target = null;
    private JScrollPane sc;
    private SelectorListModel model = null;
    private SelectorList lista;
    
    private SSRowSet rowset = null;
    
    /** 
     * Creates a new instance of HelperPopup 
     */
    public RowSetHelperPopup() {
        
        // main panel
        spane = new JPanel();
        spane.setLayout(new BorderLayout());
        spane.setBorder(new javax.swing.border.TitledBorder(" RowSet Helper "));
        
        // search text panel
        tpane = new JPanel();
        tpane.setLayout(new BorderLayout());
        
        // button bar panel
        buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this);
        
        searchText = new JTextField();
        searchText.setColumns(20);
        searchText.addActionListener(this);
        searchText.addFocusListener(this);
        buttons.add(searchText);
        buttons.add(refreshButton);
        
        tpane.add(buttons, BorderLayout.SOUTH);
        
        lista = new SelectorList();
        lista.addKeyListener(this);
        lista.addMouseListener(this);
        lista.setVisibleRowCount(10);
        
        sc = new JScrollPane(lista);
        
        spane.add(tpane, BorderLayout.NORTH);
        spane.add(sc   , BorderLayout.CENTER);
        
        this.add(spane);
        this.addPopupMenuListener(this);
        this.setEnabled(true);
        this.setFocusable(true);
        this.addFocusListener(this);
        this.pack();

        
    }
    
    /**
     * Sets the SSRowSet object to be used to fetch the values for dataColumn & listColumn values
     * @param rowset - SSRowSet object to be used to fetch the values
     */
    public  void setSSRowSet(SSRowSet rowset) {
        this.rowset = rowset;
        createHelper();
    }
    
    /**
     * Sets the list model to be used 
     * @param model - list model to be used
     */
    public void setModel(SelectorListModel model) {
        this.model = model;
        lista.setModel(model);
    }
    
    /**
     * Sets the text field for which this helper popup is being used.
     * @param target - text field for which this helper popup is being used
     */
    public void setTarget(SSFormattedTextField target) {
        this.target = target;
        
        //if (target != null) this.setPreferredSize(new Dimension(target.getWidth(),300));
    }
    
    /**
     * Sets the column name whose values should be used as underlying/bound values
     * @param dataColumn - column name whose values should be used as underlying/bound values
     */
    public void setDataColumn(String dataColumn) {
        this.dataColumn = dataColumn;
        createHelper();
    }
    
    /**
     * Sets the column name whose values should be used for displaying.
     * @param listColumn - column name whose values should be used for displaying.
     */
    public void setListColumn(String listColumn) {
        this.listColumn = listColumn;
        createHelper();
    }
    
    
 
    /**
     * Fetches the values from the rowset and populates the model
     */
    public void createHelper() {
        if (dataColumn == null || listColumn == null || rowset == null) 
        	return;

        model = new SelectorListModel();
        
        try {
            rowset.beforeFirst();
            while (rowset.next()) {
                String s1 = rowset.getString(dataColumn);
                String s2 = rowset.getString(listColumn);
                model.addElement(new SelectorElement(s1,s2));
            }
        } catch (SQLException se) {
            System.out.println("rowset.next()" + se);
        } catch (java.lang.NullPointerException np) {
            System.out.println(np);
        }
        
        model.setFilterEdit(searchText);
        lista.setModel(model);
        lista.getSelectionModel().addListSelectionListener(this);
        lista.setVisibleRowCount(10);
        pack();
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(java.awt.event.KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            this.setVisible(false);
        }
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(java.awt.event.KeyEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(java.awt.event.KeyEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(java.awt.event.MouseEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(java.awt.event.MouseEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(java.awt.event.MouseEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(java.awt.event.MouseEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() == 2) {
            this.setVisible(false);
        }
        
        if (e.getClickCount() == 321) {
            this.setVisible(false);
        }
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        
        if (e.getSource().equals(searchButton)) {
            search();
        }
        if (e.getSource().equals(closeButton)) {
            this.setVisible(false);
        }
        if (e.getSource().equals(refreshButton)) {
            createHelper();
        }
        
        if (e.getSource().equals(helpButton)) {
            int index = lista.getSelectedIndex();
            lista.ensureIndexIsVisible(index);
        }
        
        if (e.getSource().equals(searchText)) {
            search();
        }
    }
    
    
    /**
     * 
     */
    private void search() {
        
        int j, n;
        
        // text to find
        String toFind = searchText.getText().toUpperCase().trim();
        searchText.setText(toFind);
        System.out.println("Texto a buscar : " + toFind);
        n = lista.getModel().getSize();
        
        /**
         * Here implements list search logic.
         *
         *
         */
        j = lista.getSelectedIndex() + 1;
        
        System.out.println("j = " + j + " n = " + n);
        for (; j < n; j++) {
            String texto = lista.getModel().getElementAt(j).toString().toUpperCase();
            System.out.println("Comparando con " + texto);
            
            if (texto.indexOf(toFind) != -1) {  // texto.contains(toFind) in jdk5
                lista.setSelectedIndex(j);
                lista.ensureIndexIsVisible(j);
                lista.requestFocus();
                break;
            }
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
        int desde;
        int hasta;
        int selected;
        
        if (e.getValueIsAdjusting() == false) {
            desde = e.getFirstIndex();
            hasta = e.getLastIndex();
            
            DefaultListSelectionModel lm = ((DefaultListSelectionModel)e.getSource());
            
            selected = lm.getLeadSelectionIndex();
            
            SelectorElement se1 = (SelectorElement) (lista.getModel().getElementAt(desde));
            
            SelectorElement se2 = (SelectorElement) (lista.getModel().getElementAt(hasta));
            
            SelectorElement se3 = (SelectorElement) (lista.getModel().getElementAt(selected));
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent)
     */
    public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
        
        int index = -1;
        
        try {
            index = rowset.getRow() -1;
        }
        catch(java.sql.SQLException se) {
            
        }

        lista.setSelectedIndex(index);
        lista.ensureIndexIsVisible(index);
        searchText.requestFocusInWindow();
        searchText.selectAll();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent)
     */
    public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {

        System.out.println("popupMenuWillBecomeInvisible();");
        int index = lista.getSelectedIndex() + 1;
        
        if (index > -1 && target != null) {
            try {
                rowset.absolute(index);
            }
            catch(java.sql.SQLException se) {
                
            }
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.PopupMenuListener#popupMenuCanceled(javax.swing.event.PopupMenuEvent)
     */
    public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JPopupMenu#show(java.awt.Component, int, int)
     */
    public void show(java.awt.Component invoker, int x, int y) {
        this.setSize(target.getWidth(), searchText.getHeight() * 15);
        searchText.requestFocusInWindow();
        super.show(invoker, x, y);
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(java.awt.event.FocusEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(java.awt.event.FocusEvent e) {
    }
}

/*
* $Log$
* Revision 1.6  2006/04/21 19:09:17  prasanth
* Added CVS tags & some comments
*
*/