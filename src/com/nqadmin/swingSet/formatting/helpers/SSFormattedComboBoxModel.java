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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.MutableComboBoxModel;

import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;

/**
 *
 * <font color=red> <B> It is not recommended to use this class as there could be drastic changes to this classes API. </B> </font>
 * 
 * @author  dags
 */
public class SSFormattedComboBoxModel extends javax.swing.AbstractListModel implements MutableComboBoxModel, Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -8033305026029930428L;

	/**
     * Holds selected Object.
     */
    private Object selectedOne = null;
    
    /**
     * Holds value of property dataColumn.
     */
    private ArrayList dataList = new ArrayList();
    
    /**
     * Holds value of property listColumn.
     */
    private ArrayList listList = new ArrayList();
    
    /**
     * Holds value of property dataColumn.
     */
    private String dataColumn = null;
    
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
    
    /** 
     * Creates a new instance of FormattedComboBoxModel 
     */
    public SSFormattedComboBoxModel() {
        this(null, null, null, null);
    }
    
    /**
     * @param table - database table name
     * @param dataColumn - name of the column containing the values of the items displayed in the list
     * @param listColumn - column names whose values should be displayed in the list
     */
    public SSFormattedComboBoxModel(String table, String dataColumn, String listColumn) {
        this(table, dataColumn, listColumn, null);
    }
    
    /**
     * @param table - database table name
     * @param dataColumn - name of the column containing the values of the items displayed in the list
     * @param listColumn - column names whose values should be displayed in the list
     * @param orderBy - column name based on which the list items should be ordered
     */
    public SSFormattedComboBoxModel(String table, String dataColumn, String listColumn, String orderBy) {
        this(null, table, dataColumn,listColumn, orderBy);
    }
    
    /**
     * @param ssConnection - connection to be used for querying the database
     * @param table - database table name
     * @param dataColumn - name of the column containing the values of the items displayed in the list
     * @param listColumn - column names whose values should be displayed in the list
     * @param orderBy - column name based on which the list items should be ordered
     */
    public SSFormattedComboBoxModel(SSConnection ssConnection, String table, String dataColumn, String listColumn, String orderBy) {
        this.ssConnection = ssConnection;
        this.table = table;
        this.dataColumn = dataColumn;
        this.listColumn = listColumn;
        this.orderBy = orderBy;
        this.refresh();
    }
    
    /**
     * @param object
     * @return
     */
    public int indexOf(Object object) {
        return dataList.indexOf(object);
    }
    
    /**
     * 
     */
    public void refresh() {
        dataList = new ArrayList();
        listList = new ArrayList();
        
        this.populateModel();
    }
    
    /**
     * @param index
     * @return
     */
    public Object getSelectedBoundData(int index) {
        Object itm = listList.get(index);
        
        if (itm != null) {
            return ((SelectorElement)(itm)).getDataValue();
        } 
        return "<null>";
    }
    
    /**
     * This function builds the query based on the specified information and populated the data model with the 
     * data fetched from the database.
     */
    private void populateModel() {
        
        Object dataValue = null;
        Object listValue = null;
        
        String sql = null;
        
        dataList.clear();
        listList.clear();
        
        if (ssConnection == null || dataColumn == null || listColumn == null || table == null) {
            return;
        }
        
        if (selectText == null) {
            if (orderBy != null) {
                sql = "select " + dataColumn + ", " + listColumn + " from " + table + " ORDER BY " + orderBy;
            } else
                sql = "select " + dataColumn + ", " + listColumn + " from " + table;
        } else {
            if (orderBy != null) {
                sql = "select " + dataColumn + ", " + listColumn + " from " + table + " WHERE " + selectText + " ORDER BY " + orderBy;
            } else
                sql = "select " + dataColumn + ", " + listColumn + " from " + table + " WHERE " + selectText;
        }
        
        ssRowset = new SSJdbcRowSetImpl();
        ssRowset.setSSConnection(ssConnection);
        ssRowset.setCommand(sql);
        
        try {
            ssRowset.execute();
            //ssRowset.last();
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
                dataList.add(dataValue);
                listList.add(new SelectorElement(dataValue,listValue));
            }
        } catch (SQLException se) {
            System.out.println("FormattedComboBoxModel :" + se);
        } catch (java.lang.NullPointerException np) {
            System.out.println("FormattedComboBoxModel :" + np);
        }
        this.fireContentsChanged(this, 0, listList.size()-1);
        
        ssRowset = null;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.MutableComboBoxModel#addElement(java.lang.Object)
     */
    public void addElement(Object ob) {
        listList.add(ob);
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
        this.dataColumn = dataColumn;
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
        this.listColumn = listColumn;
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
        this.table = table;
        this.refresh();
    }
    
    /**
     * Setter for property orderBy.
     * @param orderBy New value of orderBy property
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        this.refresh();
    }
    
    /**
     * @return
     */
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
        this.selectText = selectText;
        this.refresh();
    }
    
    /**
     * 
     */
    public void execute() {
        refresh();
    }
    
    /**
     * Getter for property ssConnection.
     * @return Value of property ssConnection.
     * @deprecated
     */
    public com.nqadmin.swingSet.datasources.SSConnection getSsConnection() {
        return this.ssConnection;
    }
    
    /**
     * Setter for property ssConnection.
     * @param ssConnection New value of property ssConnection.
     * @deprecated
     */
    public void setSsConnection(com.nqadmin.swingSet.datasources.SSConnection ssConnection) {
        this.ssConnection = ssConnection;
        this.refresh();
    }
     /**
     * Getter for property ssConnection.
     * @return Value of property ssConnection.
     */
    public com.nqadmin.swingSet.datasources.SSConnection getSSConnection() {
        return this.ssConnection;
    }
    
    /**
     * Setter for property ssConnection.
     * @param ssConnection New value of property ssConnection.
     */
    public void setSSConnection(com.nqadmin.swingSet.datasources.SSConnection ssConnection) {
        this.ssConnection = ssConnection;
        this.refresh();
    }
    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
        return listList.get(index);
        
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        if (listList == null)
            return 0;
        else
            return listList.size();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object anItem) {
        if ((selectedOne != null && !selectedOne.equals(anItem)) || (selectedOne == null && anItem != null) ) {
            selectedOne = anItem;
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem() {
        return selectedOne;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.MutableComboBoxModel#removeElement(java.lang.Object)
     */
    public void removeElement(Object obj) {
        dataList.remove( ((SelectorElement)obj).getDataValue() );
        listList.remove(obj);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.MutableComboBoxModel#removeElementAt(int)
     */
    public void removeElementAt(int index) {
        dataList.remove(index);
        listList.remove(index);
    }
    
    public void insertElementAt(Object obj, int index) {
        dataList.add(index, ((SelectorElement)obj).getDataValue());
        listList.add(index, obj);
    }
}

/*
* $Log$
* Revision 1.3  2006/04/21 19:09:17  prasanth
* Added CVS tags & some comments
*
*/