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

import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuListener;

import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.formatting.SSFormattedTextField;

/**
 *
 * @author dags
 */
public class HelperPopup extends JPopupMenu implements MouseListener, KeyListener, ActionListener, ListSelectionListener, PopupMenuListener, FocusListener {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 2615782240022599464L;
	private JPanel spane;
    private JPanel buttons;
    private JPanel tpane;
    private JButton searchButton;
    private JButton closeButton;
    private JButton refreshButton;
    private JButton helpButton;
    private JTextField searchText;
    
    private String table = null;
    private String dataColumn = null;
    private String listColumn = null;
    private String orderBy = null;
    
    private SSFormattedTextField target = null;
    private JScrollPane sc;
    private SSConnection connection = null;
    private SelectorListModel model = null;
    private SelectorList lista;
    
    /** Creates a new instance of HelperPopup */
    public HelperPopup() {
        
        // main panel
        spane = new JPanel();
        spane.setLayout(new BorderLayout());
        spane.setBorder(new javax.swing.border.TitledBorder(" Helper "));
        
        // search text panel
        tpane = new JPanel();
        tpane.setLayout(new BorderLayout());
        
        // button bar panel
        buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        
        //searchButton = new JButton("Search");
        //searchButton.addActionListener(this);
        
        //closeButton = new JButton("Close");
        //closeButton.addActionListener(this);
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this);
        
        //helpButton = new JButton("Help");
        //helpButton.addActionListener(this);
        
        searchText = new JTextField();
        searchText.setColumns(20);
        searchText.addActionListener(this);
        searchText.addFocusListener(this);
        //tpane.add(searchText, BorderLayout.NORTH);
        
        //buttons.add(searchButton);
        //buttons.add(closeButton);
        buttons.add(searchText);
        buttons.add(refreshButton);
        //buttons.add(helpButton);
        
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
     * Sets the list model to be used 
     * @param model - list model to be used
     */
    public void setModel(SelectorListModel model) {
        this.model = model;
        lista.setModel(model);
        model.setFilterEdit(searchText);
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
     * Sets the table name from which data should be pulled
     * @param table - table name to be used in the query to pull the data
     */
    public void setTable(String table) {
        this.table = table;
        createHelper();
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
     * Sets the column name to used for ordering the items
     * @param orderBy - column name to used for ordering the items
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        createHelper();
    }
    
    /**
     * Sets the SSConnection object to used for database access.
     * @param connection - connection object to be used for accessing the database
     */
    public void setConnection(SSConnection connection) {
        this.connection = connection;
        
        try {
            connection.createConnection();
        } catch(java.lang.ClassNotFoundException nfe) {
            
        } catch(java.lang.Exception ex) {
            
        }
        createHelper();
    }
    
    
    /**
     * 
     */
    private void createHelper() {
        
        if (connection == null || table == null || dataColumn == null || listColumn == null) return;
        
        try {
            connection.createConnection();
        } catch(java.lang.ClassNotFoundException nfe) {
            
        } catch(java.lang.Exception ex) {
            
        }
        
        model = new SelectorListModel(connection, table, dataColumn, listColumn, orderBy);
        model.refresh();
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
        System.out.println("keyPressed");
        System.out.println("KeyCode = " + KeyEvent.getKeyText(e.getKeyCode()));
        
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            this.setVisible(false);
        }
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(java.awt.event.KeyEvent e) {
        System.out.println("keyTyped");
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(java.awt.event.KeyEvent e) {
        System.out.println("keyReleased");
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
        System.out.println("mouseClicked");
        
        if (e.getClickCount() == 2) {
            this.setVisible(false);
        }
        
        if (e.getClickCount() == 1) {
            this.setVisible(false);
        }
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        
        if (e.getSource().equals(searchButton)) {
            System.out.println("searchButton");
            search(e);
        }
        if (e.getSource().equals(closeButton)) {
            System.out.println("closeButton");
            this.setVisible(false);
        }
        if (e.getSource().equals(refreshButton)) {
            System.out.println("refreshButton");
            
            model = new SelectorListModel(connection, table, dataColumn, listColumn, orderBy);
            model.refresh();
            model.setFilterEdit(searchText);
            
            lista.setModel(model);
            lista.updateUI();
            lista.getSelectionModel().addListSelectionListener(this);
            lista.setVisibleRowCount(10);
        }
        
        if (e.getSource().equals(helpButton)) {
            int index = lista.getSelectedIndex();
            lista.ensureIndexIsVisible(index);
        }
        
        if (e.getSource().equals(searchText)) {
            search(e);
        }
    }
    
    
    /**
     * @param evt
     */
    private void search(java.awt.event.ActionEvent evt) {
        
        javax.swing.JTextField s;
        int j, n;
        
        // text to find
        String toFind = null;
        
        s= ((javax.swing.JTextField)evt.getSource());
        toFind = s.getText().toUpperCase().trim();
        s.setText(toFind);
        System.out.println("Texto a buscar : " + toFind);
        n = lista.getModel().getSize();
        
        /**
         * Here implements list search logic.
         *
         *
         */
        
        for (j= 0; j < n; j++) {
            String texto = lista.getModel().getElementAt(j).toString().toUpperCase();
            System.out.println("Comparando con " + texto);
            if (texto.startsWith(toFind)) {
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
        Object current = target.getValue();
        Object dataval = null;
        
        searchText.requestFocusInWindow();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent)
     */
    public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.PopupMenuListener#popupMenuCanceled(javax.swing.event.PopupMenuEvent)
     */
    public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
        System.out.println("popupMenuCanceled();");
    }

    /* (non-Javadoc)
     * @see javax.swing.JPopupMenu#show(java.awt.Component, int, int)
     */
    public void show(java.awt.Component invoker, int x, int y) {
        System.out.println("show(" + x + "," + y + ")");
        this.setSize(target.getWidth(), searchText.getHeight() * 15);
        searchText.requestFocusInWindow();
        super.show(invoker, x, y);
    }

    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(java.awt.event.FocusEvent e) {
        System.out.println("focusLost");
    }

    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(java.awt.event.FocusEvent e) {
        System.out.println("focusGained");
    }
}

/*
* $Log$
* Revision 1.7  2006/04/21 19:09:17  prasanth
* Added CVS tags & some comments
*
*/