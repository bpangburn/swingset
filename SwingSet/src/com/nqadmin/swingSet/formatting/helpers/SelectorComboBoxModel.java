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

import java.sql.SQLException;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import ca.odell.glazedlists.*;

import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;


/**
 *
 * @author  dags
 */

public class SelectorComboBoxModel extends AbstractListModel implements ComboBoxModel {
	
    private Object selectedOne = null;
	private BasicEventList data = new BasicEventList();
	/*
	 * Changed TextFilterList to FilterList because of new glazedlist jar update.
	 * FilterList takes in its parameters a TextComponentMatcherEditor to account for
	 * the depreciated TextFilterList methods.
	 */
    private FilterList filtered_data = new FilterList(data);
    private TextComponentMatcherEditor text_match; 
    
    /*
     *  Holds value of JTextField used to filter
     */
    private JTextField filter;
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
    
    /** 
     * Creates a new instance of SelectorListModel 
     */    
    public SelectorComboBoxModel() {
        this(null, null, null, null);
    }
    
    /**
     * Creates an object of SelectorComboBoxModel with the specified values.
     * @param table - name of the table to be queried
     * @param dataColumn - column name whose values should be used as underlying values
     * @param listColumn - column name whose values shoudl be used for displaying
     */
    public SelectorComboBoxModel(String table, String dataColumn, String listColumn) {
        this(table, dataColumn, listColumn, null);
    }
    
    /**
     * Creates an object of SelectorComboBoxModel with the specified values.
     * @param table - name of the table to be queried
     * @param dataColumn - column name whose values should be used as underlying values
     * @param listColumn - column name whose values shoudl be used for displaying
     * @param orderBy - column name based on which data should be ordered
     */
    public SelectorComboBoxModel(String table, String dataColumn, String listColumn, String orderBy) {
        this(null, table, dataColumn, listColumn, orderBy);
    }
    
    /**
     * Creates an object of SelectorComboBoxModel with the specified values.
     * @param ssConnection -  connection object to be used for running the query
     * @param table - name of the table to be queried
     * @param dataColumn - column name whose values should be used as underlying values
     * @param listColumn - column name whose values shoudl be used for displaying
     * @param orderBy - column name based on which data should be ordered     */
    public SelectorComboBoxModel(SSConnection ssConnection, String table, String dataColumn, String listColumn, String orderBy) { 
        super();
        propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
        setSsConnection(ssConnection);
        setTable(table);
        setDataColumn(dataColumn);
        setListColumn(listColumn);
        setOrderBy(orderBy);
                
        //populateModel();
    }
    
    /**
     *	This function refetches the information from the database. 
     */
    public void refresh() {
        data = new BasicEventList();//
        this.populateModel();
    }
    
    /**
     * Returns the value corresponding to the item at the specified index. 
     * @param index - index of the item whose value should be returned.
     * @return returns the value of the item at the specified index
     */
    public Object getSelectedBoundData(int index) {
        Object itm = filtered_data.get(index);//
        if (itm != null) {
            return ((SelectorElement)(itm)).getDataValue();
        }
        return "<null>";
    }
    
    /**
     * Sets the text to be used to filter items in the list
     * @param newFilter - text to be used to filter item in the list
     */
	@SuppressWarnings("unchecked")
	public void setFilterText(String[] newFilter) {
        //filtered_data.setFilterText(newFilter);
		
		/*
		 *  
		 */
        text_match.setFilterText(newFilter);
        filtered_data = new FilterList(data, text_match);
        
    }
    
    /*
     * Populates the list model with the data by fetching it from the database.
     */
    @SuppressWarnings("unchecked")
	private void populateModel() {
        
        Object dataValue = null;
        Object listValue = null;
        String sql = null;
        
    // IF ANY OF THE REQUIRED INFORMATION IS NOT PRESENT CLEAR THE DATA AND RETURN     
        if (ssConnection == null || dataColumn == null || listColumn == null || table == null) {
            data.clear();
            filtered_data = new FilterList(data);//
            return;
        }

    //  SEEMS LIKE WE HAVE THE USER INPUT REQUIRED TO FETCH INFORMATION FROM DATABASE
    //  SO CLEAR THE OLD DATA FIRST
        data.clear();
        
    // BUILD THE SQL    
        if (orderBy != null) {
            sql = "select " + dataColumn + ", " + listColumn + " from " + table + " ORDER BY " + orderBy;
        } else
            sql = "select " + dataColumn + ", " + listColumn + " from " + table;
        
    // CREATE A ROWSET BASED ON THE ABOVE QUERY    
        ssRowset = new SSJdbcRowSetImpl();
        ssRowset.setSSConnection(ssConnection);
        ssRowset.setCommand(sql);
        
        try {
        // EXECUTE THE QUERY	
            ssRowset.execute();
            
        // LOOP THROUGH THE ROWSET THE ADD ELEMENTS TO THE DATA MODEL    
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
            // ADD ELEMENT TO THE DATA MODEL    
                data.add(new SelectorElement(dataValue,listValue));
            }

        } catch (SQLException se) {
            System.out.println("SelectorListModel :" + se);
        } catch (java.lang.NullPointerException np) {
            System.out.println("SelectorListModel :" + np);
        }
    // FILL THE FILTERED DATA WITH THE COMPLETE DATA GOT FROM DATABASE    
        filtered_data = new FilterList(data);//
        
        
        this.fireContentsChanged(this, 0, filtered_data.size()-1);//
        this.fireIntervalRemoved(this, 0, 1);
        this.fireIntervalAdded(this, 0, 1);

    // WE DON'T NEED THIS ROWSET ANY MORE SO SET IT TO NULL    
        ssRowset = null;
    }
    
    
    /**
     * Adds an element to the data
     * @param ob - object to be added to the data
     */
    public void addElement(Object ob) {
        data.add(ob);
    }
    
    /**
     * Creates filtered data based on the actual data
     */
    public void createFilteredData() {
        filtered_data = new FilterList(data);//
        this.fireContentsChanged(this, 0, filtered_data.size()-1);//
        this.fireIntervalAdded(this, 0, 1);
        this.fireIntervalRemoved(this, 0, 1);
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
    
    /**
     * Returns the column names based on which items are ordered
     * @return returns the column names based on which items are ordered
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
        
        String oldSelectText = this.selectText;
        this.selectText = selectText;
        try {
            propertyChangeSupport.firePropertyChange("selectText", oldSelectText, selectText);
        } catch (java.lang.NullPointerException npe) {
            
        }
        //this.refresh();
    }
    
    /**
     * This will execute the query and fetch the information from database and 
     * updates the model with the new data fetched from the database
     */
    public void execute() {
        refresh();
    }
    
    /**
     * Getter for property ssConnection.
     * @return Value of property ssConnection.
     */
    public SSConnection getSsConnection() {
        
        return this.ssConnection;
    }
    
    /**
     * Setter for property ssConnection.
     * @param ssConnection New value of property ssConnection.
     */
    public void setSsConnection(SSConnection ssConnection) {
        try {
            SSConnection oldSsConnection = this.ssConnection;
            this.ssConnection = ssConnection;
            propertyChangeSupport.firePropertyChange("ssConnection", oldSsConnection, ssConnection);
        } catch(java.lang.NullPointerException nop) {
            
        }
        this.refresh();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
       //return data.get(index);
        return filtered_data.get(index);//
        
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        //return data.size();
        return filtered_data.size();//
    }
    
    /**
     * Returns the text field used as the filter.
     * @return - returns the text field used as the filter text field.
     */
    public JTextField getFilterEdit() {
    	
        //return filtered_data.getFilterEdit();//
       return filter;
        
    }
    
    /**
     * Sets the JTextField to be used as the filter field.
     * @param filter - JTextField to be used to get the filter text.
     */
	public void setFilterEdit(JTextField filter) {
        //filtered_data.setFilterEdit(filter);
    	this.filter = filter;
    	text_match = new TextComponentMatcherEditor(filter, null);    
        filtered_data = new FilterList(data, text_match);
        

    }
    
    /**
     * Adds the event listener for the filtered list
     * @param listChangeListener - list listener to be added to filtered list
     */
    public void addListEventListener(ListEventListener listChangeListener) {//
        filtered_data.addListEventListener(listChangeListener);//
       
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
     */
    public void addListDataListener(ListDataListener l) {
        super.addListDataListener(l);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object anItem) {
        selectedOne = anItem;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem() {
        return selectedOne;
    }
}

/*
* $Log$
* Revision 1.10  2006/05/15 15:50:09  prasanth
* Updated javadoc
*
* Revision 1.9  2006/04/21 19:11:32  prasanth
* Added comments & CVS tags.
* ssConnection was set to null in populate model remoted this line of code.
*
*/