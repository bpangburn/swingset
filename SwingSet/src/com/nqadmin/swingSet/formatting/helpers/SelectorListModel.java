/*
 * SelectorListModel.java
 *
 * Created on 17 de diciembre de 2004, 15:30
 */

package com.nqadmin.swingSet.formatting.helpers;

import java.sql.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.nqadmin.swingSet.datasources.*;

/**
 *
 * @author  dags
 */
public class SelectorListModel extends javax.swing.AbstractListModel {
    
    private java.util.ArrayList data = new java.util.ArrayList();
    
    
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
    
    public SelectorListModel() {
        this(null, null, null, null);
    }
    
    public SelectorListModel(String table, String bcolumn, String lcolumn) {
        this(table, bcolumn, lcolumn, null);
    }
    
    public SelectorListModel(String table, String bcolumn, String lcolumn, String orderBy) {
        this(null, table, bcolumn, lcolumn, orderBy);
    }
    
    public SelectorListModel(SSConnection ssConnection, String table, String bcolumn, String lcolumn, String orderBy) { 
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
        data = new java.util.ArrayList();
        this.populateModel();
    }
    
    public Object getSelectedBoundData(int index) {
        Object itm = data.get(index);
        
        if (itm != null) {
            return ((SelectorElement)(itm)).getDataValue();
        } else {
            return "<null>";
        }
    }
    
    private void populateModel() {
        
        String sql = null;
        
//        System.out.println("populateModel();");
        
        //  if (table == null) table = "publications";
        
        if (dataColumn == null || listColumn == null || table == null) {
            System.out.println("dataColumn = " + dataColumn);
            System.out.println("listColumn = " + listColumn);
            System.out.println("table      = " + table);
            System.out.println("Sample Model");
            data.add(new SelectorElement(new String("0") , "Option 0"));
            data.add(new SelectorElement(new String("1") , "Option 1"));
            data.add(new SelectorElement(new String("2") , "Option 2"));
            data.add(new SelectorElement(new String("0") , "Option 3"));
            data.add(new SelectorElement(new String("1") , "Option 4"));
            data.add(new SelectorElement(new String("2") , "Option 5"));
            data.add(new SelectorElement(new String("0") , "Option 6"));
            data.add(new SelectorElement(new String("1") , "Option 7"));
            data.add(new SelectorElement(new String("2") , "Option 8"));
            data.add(new SelectorElement(new String("0") , "Option 9"));
            data.add(new SelectorElement(new String("1") , "Option 0"));
            data.add(new SelectorElement(new String("2") , "Option 1"));
            data.add(new SelectorElement(new String("0") , "Option 2"));
            data.add(new SelectorElement(new String("1") , "Option 3"));
            data.add(new SelectorElement(new String("2") , "Option 4"));
            return;
        }
        
        System.out.println(dataColumn + " " + listColumn + " " + table);
        System.out.println("-----------------------------------------------------------");
        
        if (orderBy != null) {
            sql = "select " + dataColumn + ", " + listColumn + " from " + table + " ORDER BY " + orderBy;
        }
        else 
            sql = "select " + dataColumn + ", " + listColumn + " from " + table;

//        System.out.println("sql1 = " + sql);
               
        ssRowset = new SSJdbcRowSetImpl();
        ssRowset.setSSConnection(ssConnection);
        
        
//       System.out.println("sql2 = " + sql);
        
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
//                System.out.println(s2);
                data.add(new SelectorElement(s1,s2));
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
        
        for (int i=0; i < data.size(); i++) {
            cual = (SelectorElement)(data.get(i));
            
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
     * @param table New value of property table.
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
    
    public Object getElementAt(int index) {
        return data.get(index);
    }
    
    public int getSize() {
        return data.size();
    }
}
