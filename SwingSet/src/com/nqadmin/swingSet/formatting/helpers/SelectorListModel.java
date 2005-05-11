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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.TextFilterList;
import java.sql.*;
import com.nqadmin.swingSet.datasources.*;
import javax.swing.ComboBoxModel;
import javax.swing.JTextField;

/**
 *
 * @author  dags
 */
public class SelectorListModel extends javax.swing.AbstractListModel implements ComboBoxModel {
    
    private Object selectedOne = null;
    private BasicEventList data = new BasicEventList();
    private TextFilterList filtered_data = new TextFilterList(data);
    
    /**
     * Holds value of property dataColumn.
     */
    private String dataColumn = null;
    
    /**
     * Utility field used by bound properties.
     */
    private java.beans.PropertyChangeSupport propertyChangeSupport;
    
    /**
     * Holds value of property listColumn.
     */
    private String listColumn = null;
    
    /**
     * Holds value of property table.
     */
    private String table = null;
    
    /**
     * Holds value of property selectText.
     */
    private String selectText;
    
    /**
     * Holds value of property orderBy.
     */
    private String orderBy = null;
    
    /**
     * Holds value of property ssConnection.
     */
    private SSConnection ssConnection = null;
    
    private SSJdbcRowSetImpl ssRowset = null;
    
    /** Creates a new instance of SelectorListModel */
    
    public SelectorListModel() {
        this(null, null, null, null);
    }
    
    public SelectorListModel(String table, String bcolumn, String lcolumn) {
        this(table, bcolumn, lcolumn, null);
    }
    
    public int indexOf(Object object) {
        SelectorElement tmpEl = null;
        
        tmpEl = (SelectorElement)object;
        
        return data.indexOf(object);
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
    }
    
    public void refresh() {
        data = new BasicEventList();
        this.populateModel();
    }
    
    public Object getSelectedBoundData(int index) {
        Object itm = filtered_data.get(index);
        
        if (itm != null) {
            return ((SelectorElement)(itm)).getDataValue();
        } else {
            return "<null>";
        }
    }
    
    public void setFilterText(String[] newFilter) {
        filtered_data.setFilterText(newFilter);
    }
    
    private void populateModel() {
        
        Object dataValue = null;
        Object listValue = null;
        String sql = null;
        
        if (ssConnection == null || dataColumn == null || listColumn == null || table == null) {
            data.clear();
            filtered_data = new TextFilterList(data);
            return;
        }
        
        data.clear();
        
        if (orderBy != null) {
            sql = "select " + dataColumn + ", " + listColumn + " from " + table + " ORDER BY " + orderBy;
        } else
            sql = "select " + dataColumn + ", " + listColumn + " from " + table;
        
        ssRowset = new SSJdbcRowSetImpl();
        ssRowset.setSSConnection(ssConnection);
        ssRowset.setCommand(sql);
        
        try {
            ssRowset.execute();
            ssRowset.last();
            //            System.out.println("Hay " + ssRowset.getRow() + " registros");
        } catch (SQLException se) {
            System.out.println("sql = " + sql);
            System.out.println("ssRowset.execute() " + se);
        }
        
        try {
            ssRowset.beforeFirst();
            while (ssRowset.next()) {
                
                switch(ssRowset.getColumnType(1)) {
                    
                    case java.sql.Types.ARRAY://2003
                        dataValue = new String("<unsupported type: ARRAY>");
                        break;
                        
                    case java.sql.Types.BINARY://-2
                        dataValue = new String("<unsupported type: BINARY>");
                        break;
                        
                    case java.sql.Types.BIT://-7
                    case java.sql.Types.BOOLEAN://16
                        dataValue = new Boolean(ssRowset.getBoolean(1));
                        break;
                        
                    case java.sql.Types.BLOB://2004
                        dataValue = new String("<unsupported type: BLOB>");
                        break;
                        
                    case java.sql.Types.CLOB://2005
                        dataValue = new String("<unsupported type: CLOB>");
                        break;
                        
                    case java.sql.Types.DATALINK://70
                        dataValue = new String("<unsupported type: DATALINK>");
                        break;
                        
                    case java.sql.Types.DATE://91
                        dataValue = new java.util.Date(ssRowset.getDate(1).getTime());
                        break;
                        
                    case java.sql.Types.DECIMAL://3
                        dataValue = new String("<unsupported type: DECIMAL>");
                        break;
                        
                    case java.sql.Types.DISTINCT://2001
                        dataValue = new String("<unsupported type: DISTINCT>");
                        break;
                        
                    case java.sql.Types.DOUBLE://8
                        dataValue = new Double(ssRowset.getDouble(1));
                        break;
                        
                    case java.sql.Types.FLOAT://6
                        dataValue = new Float(ssRowset.getFloat(1));
                        break;
                        
                    case java.sql.Types.INTEGER://4
                        dataValue = new Integer(ssRowset.getInt(1));
                        break;
                        
                    case java.sql.Types.BIGINT://-5
                        dataValue = new Long(ssRowset.getLong(1));
                        break;
                        
                    case java.sql.Types.SMALLINT://5
                    case java.sql.Types.TINYINT://-6
                        dataValue  = new Integer(ssRowset.getInt(1));
                        break;
                        
                    case java.sql.Types.JAVA_OBJECT://2000
                        dataValue = new String("<unsupported type: JAVA_OBJECT>");
                        break;
                        
                    case java.sql.Types.LONGVARBINARY://-4
                        dataValue = new String("<unsupported type: LONGVARBINARY>");
                        break;

                    case java.sql.Types.VARBINARY://-3
                        dataValue = new String("<unsupported type: VARBINARY>");
                        break;
                        
                    case java.sql.Types.VARCHAR://
                    case java.sql.Types.LONGVARCHAR://-1
                    case java.sql.Types.CHAR://1
                        dataValue = ssRowset.getString(1);
                        break;
                        
                    case java.sql.Types.NULL://0
                        dataValue = new String("<unsupported type: NULL>");
                        break;
                        
                    case java.sql.Types.NUMERIC://2
                        dataValue = new String("<unsupported type: NUMERIC>");
                        break;
                        
                    case java.sql.Types.OTHER://1111
                        dataValue = new String("<unsupported type: OTHER>");
                        break;
                        
                    case java.sql.Types.REAL://7
                        dataValue = new String("<unsupported type: REAL>");
                        break;
                        
                    case java.sql.Types.REF://2006
                        dataValue = new String("<unsupported type: REF>");
                        break;
                        
                    case java.sql.Types.STRUCT://2002
                        dataValue = new String("<unsupported type: STRUCT>");
                        break;
                        
                    case java.sql.Types.TIME://92
                        dataValue = new String("<unsupported type: TIME>");

                        break;
                        
                    case java.sql.Types.TIMESTAMP://93
                        dataValue = new String("<unsupported type: TIMESTAMP>");
                        break;
                        
                    default:
                        dataValue = new String("<unknown type>");
                        break;
                }
                listValue = ssRowset.getString(2);
                data.add(new SelectorElement(dataValue,listValue));
            }
        } catch (SQLException se) {
            System.out.println("SelectorListModel :" + se);
        } catch (java.lang.NullPointerException np) {
            System.out.println("SelectorListModel :" + np);
        }
        
        filtered_data = new TextFilterList(data);
        
        this.fireContentsChanged(this, 0, filtered_data.size()-1);
        this.fireIntervalAdded(this, 0, 1);
        this.fireIntervalRemoved(this, 0, 1);
        
        ssRowset = null;
        ssConnection = null;
        
    }
    
    public void addElement(Object ob) {
        data.add(ob);
    }
    
    public void createFilteredData() {
        filtered_data = new TextFilterList(data);
        this.fireContentsChanged(this, 0, filtered_data.size()-1);
        this.fireIntervalAdded(this, 0, 1);
        this.fireIntervalRemoved(this, 0, 1);
    }
    
    public void setSelectedItem(String bdata) {
        SelectorElement cual;
        String tofind;
        
        tofind = bdata.toUpperCase().trim();
        
        System.out.println("setSelectedItem = " + tofind);
        
        for (int i=0; i < filtered_data.size(); i++) {
            cual = (SelectorElement)(filtered_data.get(i));
            
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
        this.refresh();
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
        this.refresh();
        
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
        this.refresh();
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
        this.refresh();
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
    public void setSsConnection(com.nqadmin.swingSet.datasources.SSConnection ssConnection) {
        try {
            com.nqadmin.swingSet.datasources.SSConnection oldSsConnection = this.ssConnection;
            this.ssConnection = ssConnection;
            propertyChangeSupport.firePropertyChange("ssConnection", oldSsConnection, ssConnection);
        } catch(java.lang.NullPointerException nop) {
            
        }
        this.refresh();
    }
    
    public Object getElementAt(int index) {
        //return data.get(index);
        return filtered_data.get(index);
        
    }
    
    public int getSize() {
        //return data.size();
        return filtered_data.size();
    }
    
    public JTextField getFilterEdit() {
        return filtered_data.getFilterEdit();
    }
    
    public void setFilterEdit(JTextField filter) {
        filtered_data.setFilterEdit(filter);
    }
    
    public void addListEventListener(ListEventListener listChangeListener) {
        filtered_data.addListEventListener(listChangeListener);
    }
    
    public void addListDataListener(javax.swing.event.ListDataListener l) {
        super.addListDataListener(l);
    }
    
    public void setSelectedItem(Object anItem) {
        System.out.println("setSelectedItem = " + anItem);
        selectedOne = anItem;
    }
    
    public Object getSelectedItem() {
        System.out.println("getSelectedItem = " + selectedOne);
        
        return selectedOne;
    }
}
