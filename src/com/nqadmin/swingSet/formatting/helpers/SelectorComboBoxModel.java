/* $Id: SelectorComboBoxModel.java,v 1.12 2013/12/09 22:32:53 prasanth Exp $
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

import java.beans.PropertyChangeSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.nqadmin.swingSet.datasources.SSConnection;


/**
 *
 * @author  dags
 */

public class SelectorComboBoxModel extends AbstractListModel<SelectorElement> implements ComboBoxModel<SelectorElement> {
	
	private static final long serialVersionUID = -1266028305085372287L;

	private Object selectedItem = null;
	
    public BasicEventList<SelectorElement> data = new BasicEventList<SelectorElement>();
    
	public Map<String, Long> itemMap;
	
	/**
	 * Changed TextFilterList to FilterList because of new glazedlist jar update.
	 * FilterList takes in its parameters a TextComponentMatcherEditor to account for
	 * the depreciated TextFilterList methods.
	 */
    public FilterList<SelectorElement> filteredData = new FilterList<SelectorElement>(data);
    
    public TextComponentMatcherEditor<SelectorElement> textMatch; 
    
    /**
     *  Holds value of JTextField used to filter
     */
    private JTextField filter;
    /**
     * Holds value of property primaryKeyColumn.
     */
    private String primaryKeyColumn = null;
    
    /**
     * Utility field used by bound properties.
     */
    private java.beans.PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    /**
     * Holds value of property displayColumn.
     */
    private String displayColumn = null;
    
    /**
     * Holds value of second display column
     */
    private String secondDisplayColumnName = "";
	
    /**
     * Separateor for the second display column
     */
    protected String seperator;// = " - ";
    
    private String dateFormat = "MM/dd/yyyy";
    
    /**
     * Holds value of property selectText.
     */
    private String selectText;
    
    /**
     * Holds value of property query.
     */
    private String query = "";
    
    /**
     * Holds value of property ssConnection.
     */
    private SSConnection ssConnection = null;
    
    /** 
     * Creates a new instance of SelectorListModel 
     */    
    public SelectorComboBoxModel() {
    	
    }
    
    /**
     * Creates an object of SelectorComboBoxModel with the specified values.
     * @param ssConnection -  connection object to be used for running the query
     * @param query - name of the query to be used
     * @param primaryKeyColumn - column name whose values should be used as underlying values
     * @param displayColumn - column name whose values should be used for displaying */
    public SelectorComboBoxModel(SSConnection ssConnection, String query, String primaryKeyColumn, String displayColumn) { 
        super();
        propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
        setSSConnection(ssConnection);
        setQuery(query);
        setPrimaryKeyColumn(primaryKeyColumn);
        setDisplayColumn(displayColumn);

    }
    
    /**
     *	This function refetches the information from the database. 
     */
    public void refresh() {
        this.populateModel();
    }
    
    /**
     * Returns the value corresponding to the item at the specified index. 
     * @param index - index of the item whose value should be returned.
     * @return returns the value of the item at the specified index
     */
    public Object getSelectedBoundData(int index) {
        Object itm = filteredData.get(index);
        if (itm != null) {
            return ((SelectorElement)(itm)).getDataValue();
        }        
        return null;
    }
    
    public void setSeparator(String separator) {
    	this.seperator = separator;
    }
    
    /**
     * Sets the text to be used to filter items in the list
     * @param newFilter - text to be used to filter item in the list
     */
	public void setFilterText(String[] newFilter) {
		
        textMatch.setFilterText(newFilter);
        filteredData = new FilterList<SelectorElement>(data, textMatch);
        
    }
	
	/**
	 * Converts the database column value into string. Only date columns are formated as specified by dateFormat variable
	 * all other column types are retrieved as strings
	 * @param _rs
	 * @param _columnName
	 * @return
	 */
	protected String getStringValue(ResultSet _rs, String _columnName) {
	        String strValue = "";
	    try {
	        int type = _rs.getMetaData().getColumnType(_rs.findColumn(_columnName));
	        switch(type){
	            case Types.DATE:
	                SimpleDateFormat myDateFormat = new SimpleDateFormat(getDateFormat());
	                strValue = myDateFormat.format(_rs.getDate(_columnName));
	            break;
	            default:
	                strValue = _rs.getString(_columnName);
	            break;
	        }
	        if(strValue == null) {
	        	strValue = "";
	        }
	    } catch(SQLException se) {
	        se.printStackTrace();
	    }
	    return strValue;
	
	}	 

    /**
     * Populates the list model with the data by fetching it from the database.
     */
	private void populateModel() {
    	itemMap  = new HashMap<String, Long>();
        Object primaryValue = null;
        Object displayValue = null;
       
        data.getReadWriteLock().writeLock().lock();
        try {
        	 Statement statement = ssConnection.getConnection().createStatement();
             ResultSet rs = statement.executeQuery(query);
             data.clear();
             int i = 0;
             while (rs.next()) {
                 // IF TWO COLUMNS HAVE TO BE DISPLAYED IN THE COMBO THEY SEPERATED BY SEMI-COLON
                 if (secondDisplayColumnName != null && !secondDisplayColumnName.trim().equals("")) {
                     displayValue = getStringValue(rs,displayColumn) + seperator + rs.getString(secondDisplayColumnName);
                	
                 } else {
                     displayValue = getStringValue(rs,displayColumn);
                     
                 }
                 // ADD THE PK TO A VECTOR.
                 primaryValue = rs.getString(primaryKeyColumn);
                 data.add(new SelectorElement(primaryValue,displayValue));
                 itemMap.put(rs.getString(primaryKeyColumn),(long)i);
                 i++;
             }

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (java.lang.NullPointerException npe) {
            npe.printStackTrace();
        } finally {
        	data.getReadWriteLock().writeLock().unlock();
        }
        
    // FILL THE FILTERED DATA WITH THE COMPLETE DATA GOT FROM DATABASE
        filteredData.getReadWriteLock().writeLock().lock();
        try {
        	filteredData.dispose();
        	filteredData = new FilterList<SelectorElement>(data);
        }finally {
        	filteredData.getReadWriteLock().writeLock().unlock();
        }
        
        this.fireContentsChanged(this, 0, filteredData.size()-1);
        this.fireIntervalRemoved(this, 0, 1);
        this.fireIntervalAdded(this, 0, 1);

    }
    
    
    /**
     * Adds an element to the data
     * @param ob - object to be added to the data
     */
    public void addElement(SelectorElement ob) {
    	data.getReadWriteLock().writeLock().lock();
    	try {
    		data.add(ob);
    	}finally {
    		data.getReadWriteLock().writeLock().unlock();
    	}
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
     * Getter for property primaryKeyColumn.
     * @return Value of property primaryKeyColumn.
     */
    public String getPrimaryKeyColumn() {
        return this.primaryKeyColumn;
    }
    
    /**
     * Setter for property primaryKeyColumn.
     * @param primaryKeyColumn New value of property primaryKeyColumn.
     */
    public void setPrimaryKeyColumn(String primaryKeyColumn) {
        String oldPrimaryKeyColumn = this.primaryKeyColumn;
        this.primaryKeyColumn = primaryKeyColumn;
        try {
            propertyChangeSupport.firePropertyChange("primaryKeyColumn", oldPrimaryKeyColumn, primaryKeyColumn);
        } catch(java.lang.NullPointerException npe) {
        	npe.printStackTrace();
        }
    }
    
    /**
     * Getter for property displayColumn.
     * @return Value of property displayColumn.
     */
    public String getDisplayColumn() {        
        return this.displayColumn;
    }
    
    /**
     * Setter for property displayColumn.
     * @param displayColumn New value of property displayColumn.
     */
    public void setDisplayColumn(String displayColumn) {
        String oldDisplayColumn = this.displayColumn;
        this.displayColumn = displayColumn;
        try {
            propertyChangeSupport.firePropertyChange("displayColumn", oldDisplayColumn, displayColumn);
        } catch(java.lang.NullPointerException npe) {
        	npe.printStackTrace();
        }
        
    }

    /**
     * Setter for property displayColumn.
     * @param displayColumn New value of property displayColumn.
     */
    public void setSecondDisplayColumn(String secondDisplayColumn) {
        String oldDisplayColumn = this.secondDisplayColumnName;
        this.secondDisplayColumnName = secondDisplayColumn;
        try {
            propertyChangeSupport.firePropertyChange("secondDisplayColumn", oldDisplayColumn, secondDisplayColumnName);
        } catch(java.lang.NullPointerException npe) {
            npe.printStackTrace();
        }
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
            npe.printStackTrace();
        }

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
    public void setSSConnection(SSConnection ssConnection) {
        try {
            SSConnection oldSsConnection = this.ssConnection;
            this.ssConnection = ssConnection;
            propertyChangeSupport.firePropertyChange("ssConnection", oldSsConnection, ssConnection);
        } catch(java.lang.NullPointerException npe) {
            npe.printStackTrace();
        }
    }
    
    /**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
    
    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public SelectorElement getElementAt(int index) {
        return filteredData.get(index);        
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        return filteredData.size();
    }
    
    /**
     * Returns the text field used as the filter.
     * @return - returns the text field used as the filter text field.
     */
    public JTextField getFilterEdit() {
       return filter;
    }
    
    /**
     * Sets the query used to display items in the combo box.
     *
     * @param _query   query to be used to get values from database (to display combo box items)
     */
    public void setQuery(String _query) {
        query = _query;
    }
    
    /**
     * Sets the JTextField to be used as the filter field.
     * @param filter - JTextField to be used to get the filter text.
     */
	public void setFilterEdit(JTextField filter) {
    	this.filter = filter;
    	if(textMatch != null)
    		textMatch.dispose();
    	textMatch = new TextComponentMatcherEditor<SelectorElement>(filter, null);
    	filteredData.setMatcherEditor(textMatch);
    }
    
    /**
     * Adds the event listener for the filtered list
     * @param listChangeListener - list listener to be added to filtered list
     */
    public void addListEventListener(ListEventListener listChangeListener) {
        filteredData.addListEventListener(listChangeListener);
       
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
     */
    public void addListDataListener(ListDataListener l) {
        super.addListDataListener(l);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem() {
        return selectedItem;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object anItem) {
   		selectedItem = anItem;
    }

}

/*
* $Log: SelectorComboBoxModel.java,v $
* Revision 1.12  2013/12/09 22:32:53  prasanth
* Updated the variable names, specifying the data types for lists and printing exceptions when there is an exception.
*
* Revision 1.11  2012/08/08 20:14:49  beevo
* Modified to support the navigation in the SSDBCOMBOBOX filter.
*
* Revision 1.10  2006/05/15 15:50:09  prasanth
* Updated javadoc
*
* Revision 1.9  2006/04/21 19:11:32  prasanth
* Added comments & CVS tags.
* ssConnection was set to null in populate model remoted this line of code.
*
*/