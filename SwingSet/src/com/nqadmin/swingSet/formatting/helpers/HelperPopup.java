/*
 * HelperPopup.java
 *
 * Created on 20 de diciembre de 2004, 12:25
 */

package com.nqadmin.swingSet.formatting.selectors;

import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.formatting.SSFormattedTextField;
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

/**
 *
 * @author dags
 */
public class HelperPopup extends JPopupMenu implements MouseListener, KeyListener, ActionListener, ListSelectionListener, PopupMenuListener, FocusListener {
    
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
    private SelectorListModel model = null;
    private SelectorList lista;
    private int colType = 0;
    
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
    
    public void setModel(SelectorListModel model) {
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
    
    public void setColumnType(int colType) {
        this.colType = colType;
    }
    
    public void createHelper() {
        
        try {
            connection.createConnection();
        } catch(java.lang.ClassNotFoundException nfe) {
            
        } catch(java.lang.Exception ex) {
            
        }
        
        model = new SelectorListModel(connection, table, dataColumn, listColumn, orderBy);
        model.refresh();
        
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
        
        if (e.getClickCount() == 1) {
            this.setVisible(false);
        }
    }
    
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
        Object current = target.getValue();
        Object dataval = null;
        
        System.out.println("popupMenuWillBecomeVisible();");
        // trying to select current value
        if (current == null) return;
        
        for (int j= 0; j < lista.getModel().getSize(); j++) {
            dataval = ( (SelectorElement)lista.getModel().getElementAt(j) ).getDataValue();
            
            if (dataval.toString().equals(current.toString()) ) {
                lista.setSelectedIndex(j);
                lista.ensureIndexIsVisible(j);
                break;
            }
        }
        //this.setSize(target.getWidth(), searchText.getHeight() * 15);
        searchText.requestFocusInWindow();
    }
    
    public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
        int index;
        System.out.println("popupMenuWillBecomeInvisible();");
        index = lista.getSelectedIndex();
        
        if (index > -1 && target != null) {
            SelectorElement se = (SelectorElement)(lista.getModel().getElementAt(index));
            System.out.println("DataValue = " + se.getDataValue());
            switch(colType) {
                case 0:
                    target.setValue(new Integer((String)se.getDataValue()));
                    break;
                case 1:
                    target.setValue((String)se.getDataValue());
                    break;
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
