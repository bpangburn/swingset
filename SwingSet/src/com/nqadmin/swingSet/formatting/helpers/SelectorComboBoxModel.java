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

import java.sql.*;
import com.nqadmin.swingSet.datasources.*;

/**
 *
 * @author  dags
 */
public class SelectorComboBoxModel extends javax.swing.DefaultComboBoxModel {
    
    /**
     * Holds value of property dataColumn.
     */
    private String dataColumn;
    
    /**
     * Utility field used by bound properties.
     */
    private java.beans.PropertyChangeSupport propertyChangeSupport;
    
    /**
     * Holds value of property listColumn.
     */
    private String listColumn;
    
    /**
     * Holds value of property table.
     */
    private String table;
    
    /**
     * Holds value of property selectText.
     */
    private String selectText;
    
    private String orderBy;
    
    /**
     * Holds value of property ssConnection.
     */
    private SSConnection     ssConnection;
    private SSJdbcRowSetImpl ssRowset;
    
    /** Creates a new instance of AccountSelectorModel */
    
    public SelectorComboBoxModel() {
        this(null, null, null, null);
    }
    
    public SelectorComboBoxModel(String table, String bcolumn, String lcolumn) {
        this(table, bcolumn, lcolumn, null);
    }
    
    public SelectorComboBoxModel(String table, String bcolumn, String lcolumn, String orderBy) {
        this(null, table, bcolumn, lcolumn, orderBy);
    }
    
    public SelectorComboBoxModel(SSConnection ssConnection, String table, String bcolumn, String lcolumn, String orderBy) { 
        super();
        propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
        setSsConnection(ssConnection);
        setTable(table);
        setDataColumn(bcolumn);
        setListColumn(lcolumn);
        setOrderBy(orderBy);
                
        //populateModel();
    }
    
    public void refresh() {
        System.out.println("---------------------- refresh() ---------------------");
        this.populateModel();
    }
    
    public Object getSelectedBoundData(int index) {
        return ((SelectorElement)this.getElementAt(index)).getDataValue();
    }
    
    private void populateModel() {
        
        String sql = null;
        
        System.out.println("populateModel();");
        
        //  if (table == null) table = "publications";
        
        if (dataColumn == null || listColumn == null || table == null) {
            System.out.println("dataColumn = " + dataColumn);
            System.out.println("listColumn = " + listColumn);
            System.out.println("table      = " + table);
            System.out.println("Sample Model");
            this.addElement(new SelectorElement(new String("0") , "Option 0"));
            this.addElement(new SelectorElement(new String("1") , "Option 1"));
            this.addElement(new SelectorElement(new String("2") , "Option 2"));
            this.addElement(new SelectorElement(new String("0") , "Option 3"));
            this.addElement(new SelectorElement(new String("1") , "Option 4"));
            this.addElement(new SelectorElement(new String("2") , "Option 5"));
            this.addElement(new SelectorElement(new String("0") , "Option 6"));
            this.addElement(new SelectorElement(new String("1") , "Option 7"));
            this.addElement(new SelectorElement(new String("2") , "Option 8"));
            this.addElement(new SelectorElement(new String("0") , "Option 9"));
            return;
        }
        
        System.out.println(dataColumn + " " + listColumn + " " + table);
        System.out.println("-----------------------------------------------------------");
        
        if (orderBy != null) {
            sql = "select " + dataColumn + ", " + listColumn + " from " + table + " ORDER BY " + orderBy;
        }
        else 
            sql = "select " + dataColumn + ", " + listColumn + " from " + table;

        System.out.println("sql1 = " + sql);
               
        ssRowset = new SSJdbcRowSetImpl();
        ssRowset.setSSConnection(ssConnection);
        
        
       System.out.println("sql2 = " + sql);
        
        ssRowset.setCommand(sql);
        
        try {
            ssRowset.execute();
            ssRowset.last();
            System.out.println("Hay " + ssRowset.getRow() + " registros");
        } catch (SQLException se) {
            System.out.println("sql = " + sql);
            System.out.println("ssRowset.execute() " + se);
        }
        
        //data.add(new SelectorElement(new String("-1"), selectText));
        
        try {
            ssRowset.beforeFirst();
            while (ssRowset.next()) {
                String s1 = ssRowset.getString(1);
                String s2 = ssRowset.getString(2);
                System.out.println(s2);
                this.addElement(new SelectorElement(s1,s2));
            }
        } catch (SQLException se) {
            System.out.println("ssRowset.next()" + se);
        } catch (java.lang.NullPointerException np) {
            System.out.println(np);
        }
        
        ssRowset = null;
        ssConnection = null;
    }
    
    public void setSelectedItem(String bdata) {
        SelectorElement cual;
        String tofind;
        
        tofind = bdata.toUpperCase().trim();
        
        System.out.println("setSelectedItem = " + tofind);
        
        for (int i=0; i < this.getSize(); i++) {
            cual = (SelectorElement)(this.getElementAt(i));
            
            System.out.println("BoundData = '" + cual.getDataValue() + "'");
            
            if ((cual.getDataValue()).equals(bdata)) {
                //                super.setSelectedItem(cual);
                return;
            }
        }
        System.out.println("parece que no encontro a '" + bdata +"'");
    }
    
    /**
     * Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }
    
    /**
     * Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
    
    /**
     * Getter for property dataColumn.
     * @return Value of property dataColumn.
     */
    public String getDataColumn() {
        return this.dataColumn;
    }
    
    /**
     * Setter for property dataColumn.
     * @param dataColumn New value of property dataColumn.
     */
    public void setDataColumn(String dataColumn) {
        String oldDataColumn = this.dataColumn;
        this.dataColumn = dataColumn;
        try {
            propertyChangeSupport.firePropertyChange("dataColumn", oldDataColumn, dataColumn);
        } catch(java.lang.NullPointerException npe) {
            
        }
        //this.refresh();
    }
    
    /**
     * Getter for property listColumn.
     * @return Value of property listColumn.
     */
    public String getListColumn() {
        
        return this.listColumn;
    }
    
    /**
     * Setter for property listColumn.
     * @param listColumn New value of property listColumn.
     */
    public void setListColumn(String listColumn) {
        String oldListColumn = this.listColumn;
        this.listColumn = listColumn;
        try {
            propertyChangeSupport.firePropertyChange("listColumn", oldListColumn, listColumn);
        } catch(java.lang.NullPointerException npe) {
            
        }
        //this.refresh();
        
    }
    
    /**
     * Getter for property table.
     * @return Value of property table.
     */
    public String getTable() {
        return this.table;
    }
    
    /**
     * Setter for property table.
     * @param table New value of property table.
     */
    public void setTable(String table) {
        
        String oldTable = this.table;
        this.table = table;
        
        try {
            propertyChangeSupport.firePropertyChange("table", oldTable, table);
        } catch(java.lang.NullPointerException npe) {
            
        }
        //this.refresh();
    }
    
    /**
     * Setter for property orderBy.
     * @param orderBy New value of orderBy property
     */
    public void setOrderBy(String orderBy) {
        
        String oldorderBy = this.orderBy;
        this.orderBy = orderBy;
        
        try {
            propertyChangeSupport.firePropertyChange("orderBy", oldorderBy, orderBy);
        } catch(java.lang.NullPointerException npe) {
            
        }
        //this.refresh();
    }

    public String getOrderBy() {
        return orderBy;
    }

    /**
     * Getter for property selectText.
     * @return Value of property selectText.
     */
    public String getSelectText() {
        
        return this.selectText;
    }
    
    /**
     * Setter for property selectText.
     * @param selectText New value of property selectText.
     */
    public void setSelectText(String selectText) {
        
        String oldSelectText = this.selectText;
        this.selectText = selectText;
        try {
            propertyChangeSupport.firePropertyChange("selectText", oldSelectText, selectText);
        } catch (java.lang.NullPointerException npe) {
            
        }
        //this.refresh();
    }
    
    public void execute() {
        refresh();
    }
    
    /**
     * Getter for property ssConnection.
     * @return Value of property ssConnection.
     */
    public com.nqadmin.swingSet.datasources.SSConnection getSsConnection() {
        
        return this.ssConnection;
    }
    
    /**
     * Setter for property ssConnection.
     * @param ssConnection New value of property ssConnection.
     */
    public void setSsConnection(com.nqadmin.swingSet.datasources.SSConnection ssConnection)
    
    {
        try {
            com.nqadmin.swingSet.datasources.SSConnection oldSsConnection = this.ssConnection;
            this.ssConnection = ssConnection;
            propertyChangeSupport.firePropertyChange("ssConnection", oldSsConnection, ssConnection);
        } catch(java.lang.NullPointerException nop) {
            
        }
    }
}
