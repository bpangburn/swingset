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

package com.nqadmin.swingSet.formatting.helpers;

import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.datasources.SSRowSet;
import com.nqadmin.swingSet.formatting.SSFormattedTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author dags
 */
public class RowSetHelperPopup extends JPopupMenu implements MouseListener, KeyListener, ActionListener, ListSelectionListener, PopupMenuListener, FocusListener {
    
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
    private String query = null;
    
    private SSFormattedTextField target = null;
    private JScrollPane sc;
    private SSConnection connection;
    private DefaultListModel model = null;
    private SelectorList lista;
    
    private SSRowSet rowset = null;
    
    /** Creates a new instance of HelperPopup */
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
        
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        
        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this);
        
        helpButton = new JButton("Help");
        helpButton.addActionListener(this);
        
        searchText = new JTextField();
        searchText.setColumns(20);
        searchText.addActionListener(this);
        searchText.addFocusListener(this);
        tpane.add(searchText, BorderLayout.NORTH);
        
        buttons.add(searchButton);
        buttons.add(closeButton);
        buttons.add(refreshButton);
        buttons.add(helpButton);
        
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
    
    public  void setRowSet(SSRowSet rowset) {
        this.rowset = rowset;
    }
    
    public void setModel(DefaultListModel model) {
        this.model = model;
        lista.setModel(model);
    }
    
    public void setTarget(SSFormattedTextField target) {
        this.target = target;
        
        //if (target != null) this.setPreferredSize(new Dimension(target.getWidth(),300));
        
    }
    
    public void setTable(String table) {
        this.table = table;
    }
    
    public void setDataColumn(String dataColumn) {
        this.dataColumn = dataColumn;
    }
    
    public void setListColumn(String listColumn) {
        this.listColumn = listColumn;
    }
    
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
    
    public void setConnection(SSConnection connection) {
        this.connection = connection;
        
        try {
            connection.createConnection();
        } catch(java.lang.ClassNotFoundException nfe) {
            
        } catch(java.lang.Exception ex) {
            
        }
    }
    
    public void createHelper() {
        /*
        try {
            connection.createConnection();
        } catch(java.lang.ClassNotFoundException nfe) {
            
        } catch(java.lang.Exception ex) {
            
        }
        */
        model = new DefaultListModel();
        
         try {
            System.out.println("beforeFirst();");
            System.out.println("dataColumn = " + dataColumn);
            System.out.println("listColumn = " + listColumn);
            
            rowset.beforeFirst();
            while (rowset.next()) {
                System.out.println("rowset.next() =" + rowset.getString(listColumn));
                String s1 = rowset.getString(dataColumn);
                String s2 = rowset.getString(listColumn);
                model.addElement(new SelectorElement(s1,s2));
            }
        } catch (SQLException se) {
            System.out.println("rowset.next()" + se);
        } catch (java.lang.NullPointerException np) {
            System.out.println(np);
        }
        
        lista.setModel(model);
        lista.getSelectionModel().addListSelectionListener(this);
        lista.setVisibleRowCount(10);
        pack();
    }
    
    public void keyPressed(java.awt.event.KeyEvent e) {
        System.out.println("keyPressed");
        System.out.println("KeyCode = " + KeyEvent.getKeyText(e.getKeyCode()));
        
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            this.setVisible(false);
        }
    }
    
    public void keyTyped(java.awt.event.KeyEvent e) {
        System.out.println("keyTyped");
    }
    
    public void keyReleased(java.awt.event.KeyEvent e) {
        System.out.println("keyReleased");
    }
    
    public void mouseReleased(java.awt.event.MouseEvent e) {
    }
    
    public void mousePressed(java.awt.event.MouseEvent e) {
    }
    
    public void mouseExited(java.awt.event.MouseEvent e) {
    }
    
    public void mouseEntered(java.awt.event.MouseEvent e) {
    }
    
    public void mouseClicked(java.awt.event.MouseEvent e) {
        System.out.println("mouseClicked");
        
        if (e.getClickCount() == 2) {
            this.setVisible(false);
        }
        
        if (e.getClickCount() == 321) {
            this.setVisible(false);
        }
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        
        if (e.getSource().equals(searchButton)) {
            System.out.println("searchButton");
            search();
        }
        if (e.getSource().equals(closeButton)) {
            System.out.println("closeButton");
            this.setVisible(false);
        }
        if (e.getSource().equals(refreshButton)) {
            System.out.println("refreshButton");
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
    
    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
        int desde;
        int hasta;
        int selected;
        
        //        System.out.println("ValueIsAdjusting = " + e.getValueIsAdjusting());
        
        if (e.getValueIsAdjusting() == false) {
            desde = e.getFirstIndex();
            hasta = e.getLastIndex();
            //            System.out.println("Desde " + desde + " hasta " + hasta);
            
            DefaultListSelectionModel lm = ((DefaultListSelectionModel)e.getSource());
            
            selected = lm.getLeadSelectionIndex();
            
            System.out.println("--------------------- desde --------------------------------------");
            SelectorElement se1 = (SelectorElement) (lista.getModel().getElementAt(desde));
            //            System.out.println("DataValue = " + se1.getDataValue().toString());
            //            System.out.println("ListValue = " + se1.getListValue().toString());
            
            System.out.println("--------------------- hasta --------------------------------------");
            SelectorElement se2 = (SelectorElement) (lista.getModel().getElementAt(hasta));
            //            System.out.println("DataValue = " + se2.getDataValue().toString());
            //            System.out.println("ListValue = " + se2.getListValue().toString());
            
            System.out.println("--------------------- selected --------------------------------------");
            SelectorElement se3 = (SelectorElement) (lista.getModel().getElementAt(selected));
            //            System.out.println("DataValue = " + se3.getDataValue().toString());
            //            System.out.println("ListValue = " + se3.getListValue().toString());
            
        }
    }
    
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
    
    public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
        System.out.println("popupMenuCanceled();");
    }
    
    public void show(java.awt.Component invoker, int x, int y) {
        System.out.println("show(" + x + "," + y + ")");
        this.setSize(target.getWidth(), searchText.getHeight() * 15);
        searchText.requestFocusInWindow();
        super.show(invoker, x, y);
    }
    
    public void focusLost(java.awt.event.FocusEvent e) {
        System.out.println("focusLost");
    }
    
    public void focusGained(java.awt.event.FocusEvent e) {
        System.out.println("focusGained");
    }
}
