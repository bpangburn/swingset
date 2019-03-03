/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Contributors:
 *   Prasanth R. Pasala
 *   Brian E. Pangburn
 *   Diego Gil
 *   Man "Bee" Vo
 ******************************************************************************/

package com.nqadmin.swingSet.formatting.helpers;

import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;

import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

/**
 * SelectorListModel.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * CombobBox Data Model integrating Glazed Lists.
 * 
 * @see "https://docs.oracle.com/javase/8/docs/api/javax/swing/ComboBoxModel.html"
 * @see "http://www.glazedlists.com/Home"
 * 
 */

// TODO  Very similar to SelectorComboBoxModel. Need to compare both to see if they can be merged.

//public class SelectorListModel extends AbstractListModel implements ComboBoxModel {
public class SelectorListModel extends DefaultComboBoxModel<Object> {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 2436267331238687202L;
	private Object selectedOne = null;
	private BasicEventList<Object> data = new BasicEventList<>();
	/*
	 * Changed TextFilterList to FilterList because of new glazedlist jar update.
	 * FilterList takes in its parameters a TextComponentMatcherEditor to account
	 * for the depreciated TextFilterList methods.
	 */
	private FilterList<Object> filtered_data = new FilterList<>(this.data);
	private TextComponentMatcherEditor<Object> text_match;

	/*
	 * Holds value of JTextField used to filter
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
	public SelectorListModel() {
		this(null, null, null, null);
	}

	/**
	 * Creates an object of SelectorListModel with the given data
	 * 
	 * @param _table      - database table name
	 * @param _dataColumn - name of the column containing the values of the items
	 *                    displayed in the list
	 * @param _listColumn - column names whose values should be displayed in the
	 *                    list
	 */
	public SelectorListModel(final String _table, final String _dataColumn, final String _listColumn) {
		this(_table, _dataColumn, _listColumn, null);
	}

	/**
	 * Returns the index of the specified object in the actual data (unfiltered
	 * list)
	 * 
	 * @param _object - object whose index should be returned
	 * @return - returns the index of the specified object (in unfiltered list)
	 */
	public int indexOf(final Object _object) {

		return this.data.indexOf(_object);//
	}

	/**
	 * Creates an object of SelectorListModel with the given data
	 * 
	 * @param _table      - database table name
	 * @param _dataColumn - name of the column containing the values of the items
	 *                    displayed in the list
	 * @param _listColumn - column names whose values should be displayed in the
	 *                    list
	 * @param _orderBy    - column name based on which the list items should be
	 *                    ordered
	 */
	public SelectorListModel(final String _table, final String _dataColumn, final String _listColumn,
			final String _orderBy) {
		this(null, _table, _dataColumn, _listColumn, _orderBy);
	}

	/**
	 * Creates an object of SelectorListModel with the given data
	 * 
	 * @param _ssConnection - connection to be used for querying the database
	 * @param _table        - database table name
	 * @param _dataColumn   - name of the column containing the values of the items
	 *                      displayed in the list
	 * @param _listColumn   - column names whose values should be displayed in the
	 *                      list
	 * @param _orderBy      - column name based on which the list items should be
	 *                      ordered
	 */
	public SelectorListModel(final SSConnection _ssConnection, final String _table, final String _dataColumn,
			final String _listColumn, final String _orderBy) {
		super();
		this.propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
		setSsConnection(_ssConnection);
		setTable(_table);
		setDataColumn(_dataColumn);
		setListColumn(_listColumn);
		setOrderBy(_orderBy);
	}

	/**
	 * This function re-fetches the information from the database.
	 */
	public void refresh() {
		this.data = new BasicEventList<>();//
		this.populateModel();
	}

	/**
	 * Returns the value corresponding to the item at the specified index.
	 * 
	 * @param _index - index of the item whose value should be returned.
	 * @return returns the value of the item at the specified index
	 */
	public Object getSelectedBoundData(final int _index) {
		Object itm = this.filtered_data.get(_index);//
		if (itm != null) {
			return ((SelectorElement) (itm)).getDataValue();
		}
		return "<null>";
	}

	/**
	 * Sets the text to be used to filter items in the list
	 * 
	 * @param _newFilter - text to be used to filter item in the list
	 */
	public void setFilterText(final String[] _newFilter) {
		this.text_match.setFilterText(_newFilter);
		this.filtered_data = new FilterList<>(this.data, this.text_match);
	}

	/*
	 * Populates the list model with the data by fetching it from the database.
	 */
	private void populateModel() {

		Object dataValue = null;
		Object listValue = null;
		String sql = null;

		// IF ANY OF THE REQUIRED INFORMATION IS NOT PRESENT CLEAR THE DATA AND RETURN
		if (this.ssConnection == null || this.dataColumn == null || this.listColumn == null || this.table == null) {
			this.data.clear();
			this.filtered_data = new FilterList<>(this.data);//
			return;
		}

		// SEEMS LIKE WE HAVE THE USER INPUT REQUIRED TO FETCH INFORMATION FROM DATABASE
		// SO CLEAR THE OLD DATA FIRST
		this.data.clear();

		// BUILD THE SQL
		if (this.orderBy != null) {
			sql = "select " + this.dataColumn + ", " + this.listColumn + " from " + this.table + " ORDER BY "
					+ this.orderBy;
		} else
			sql = "select " + this.dataColumn + ", " + this.listColumn + " from " + this.table;

		// CREATE A ROWSET BASED ON THE ABOVE QUERY
		this.ssRowset = new SSJdbcRowSetImpl();
		this.ssRowset.setSSConnection(this.ssConnection);
		this.ssRowset.setCommand(sql);

		try {
			// EXECUTE THE QUERY
			this.ssRowset.execute();

			// LOOP THROUGH THE ROWSET THE ADD ELEMENTS TO THE DATA MODEL
			while (this.ssRowset.next()) {

				switch (this.ssRowset.getColumnType(1)) {

				case java.sql.Types.ARRAY:// 2003
					dataValue = new String("<unsupported type: ARRAY>");
					break;

				case java.sql.Types.BINARY:// -2
					dataValue = new String("<unsupported type: BINARY>");
					break;

				case java.sql.Types.BIT:// -7
				case java.sql.Types.BOOLEAN:// 16
					dataValue = new Boolean(this.ssRowset.getBoolean(1));
					break;

				case java.sql.Types.BLOB:// 2004
					dataValue = new String("<unsupported type: BLOB>");
					break;

				case java.sql.Types.CLOB:// 2005
					dataValue = new String("<unsupported type: CLOB>");
					break;

				case java.sql.Types.DATALINK:// 70
					dataValue = new String("<unsupported type: DATALINK>");
					break;

				case java.sql.Types.DATE:// 91
					dataValue = new java.util.Date(this.ssRowset.getDate(1).getTime());
					break;

				case java.sql.Types.DECIMAL:// 3
					dataValue = new String("<unsupported type: DECIMAL>");
					break;

				case java.sql.Types.DISTINCT:// 2001
					dataValue = new String("<unsupported type: DISTINCT>");
					break;

				case java.sql.Types.DOUBLE:// 8
					dataValue = new Double(this.ssRowset.getDouble(1));
					break;

				case java.sql.Types.FLOAT:// 6
					dataValue = new Float(this.ssRowset.getFloat(1));
					break;

				case java.sql.Types.INTEGER:// 4
					dataValue = new Integer(this.ssRowset.getInt(1));
					break;

				case java.sql.Types.BIGINT:// -5
					dataValue = new Long(this.ssRowset.getLong(1));
					break;

				case java.sql.Types.SMALLINT:// 5
				case java.sql.Types.TINYINT:// -6
					dataValue = new Integer(this.ssRowset.getInt(1));
					break;

				case java.sql.Types.JAVA_OBJECT:// 2000
					dataValue = new String("<unsupported type: JAVA_OBJECT>");
					break;

				case java.sql.Types.LONGVARBINARY:// -4
					dataValue = new String("<unsupported type: LONGVARBINARY>");
					break;

				case java.sql.Types.VARBINARY:// -3
					dataValue = new String("<unsupported type: VARBINARY>");
					break;

				case java.sql.Types.VARCHAR://
				case java.sql.Types.LONGVARCHAR:// -1
				case java.sql.Types.CHAR:// 1
					dataValue = this.ssRowset.getString(1);
					break;

				case java.sql.Types.NULL:// 0
					dataValue = new String("<unsupported type: NULL>");
					break;

				case java.sql.Types.NUMERIC:// 2
					dataValue = new String("<unsupported type: NUMERIC>");
					break;

				case java.sql.Types.OTHER:// 1111
					dataValue = new String("<unsupported type: OTHER>");
					break;

				case java.sql.Types.REAL:// 7
					dataValue = new String("<unsupported type: REAL>");
					break;

				case java.sql.Types.REF:// 2006
					dataValue = new String("<unsupported type: REF>");
					break;

				case java.sql.Types.STRUCT:// 2002
					dataValue = new String("<unsupported type: STRUCT>");
					break;

				case java.sql.Types.TIME:// 92
					dataValue = new String("<unsupported type: TIME>");

					break;

				case java.sql.Types.TIMESTAMP:// 93
					dataValue = new String("<unsupported type: TIMESTAMP>");
					break;

				default:
					dataValue = new String("<unknown type>");
					break;
				}
				listValue = this.ssRowset.getString(2);
				// ADD ELEMENT TO THE DATA MODEL
				this.data.add(new SelectorElement(dataValue, listValue));
			}

		} catch (SQLException se) {
			System.out.println("SelectorListModel :" + se);
		} catch (java.lang.NullPointerException np) {
			System.out.println("SelectorListModel :" + np);
		}
		// FILL THE FILTERED DATA WITH THE COMPLETE DATA GOT FROM DATABASE
		this.filtered_data = new FilterList<>(this.data);//

		this.fireContentsChanged(this, 0, this.filtered_data.size() - 1);//
		this.fireIntervalRemoved(this, 0, 1);
		this.fireIntervalAdded(this, 0, 1);

		// WE DON'T NEED THIS ROWSET ANY MORE SO SET IT TO NULL
		this.ssRowset = null;
	}

	/**
	 * Adds an element to the data
	 * 
	 * @param _ob - object to be added to the data
	 */
	@Override
	public void addElement(final Object _ob) {
		this.data.add(_ob);
	}

	/**
	 * Creates filtered data based on the actual data
	 */
	public void createFilteredData() {
		this.filtered_data = new FilterList<>(this.data);//
		this.fireContentsChanged(this, 0, this.filtered_data.size() - 1);//
		this.fireIntervalAdded(this, 0, 1);
		this.fireIntervalRemoved(this, 0, 1);
	}

	/**
	 * Adds a PropertyChangeListener to the listener list.
	 * 
	 * @param l The listener to add.
	 */
	public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
		this.propertyChangeSupport.addPropertyChangeListener(l);
	}

	/**
	 * Removes a PropertyChangeListener from the listener list.
	 * 
	 * @param l The listener to remove.
	 */
	public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
		this.propertyChangeSupport.removePropertyChangeListener(l);
	}

	/**
	 * Getter for property dataColumn.
	 * 
	 * @return Value of property dataColumn.
	 */
	public String getDataColumn() {
		return this.dataColumn;
	}

	/**
	 * Setter for property dataColumn.
	 * 
	 * @param _dataColumn New value of property dataColumn.
	 */
	public void setDataColumn(final String _dataColumn) {
		String oldDataColumn = this.dataColumn;
		this.dataColumn = _dataColumn;
		try {
			this.propertyChangeSupport.firePropertyChange("dataColumn", oldDataColumn, _dataColumn);
		} catch (java.lang.NullPointerException npe) {
			// do nothing
		}
		this.refresh();
	}

	/**
	 * Getter for property listColumn.
	 * 
	 * @return Value of property listColumn.
	 */
	public String getListColumn() {
		return this.listColumn;
	}

	/**
	 * Setter for property listColumn.
	 * 
	 * @param _listColumn New value of property listColumn.
	 */
	public void setListColumn(final String _listColumn) {
		String oldListColumn = this.listColumn;
		this.listColumn = _listColumn;
		try {
			this.propertyChangeSupport.firePropertyChange("listColumn", oldListColumn, _listColumn);
		} catch (java.lang.NullPointerException npe) {
			// do nothing
		}
		this.refresh();

	}

	/**
	 * Getter for property table.
	 * 
	 * @return Value of property table.
	 */
	public String getTable() {
		return this.table;
	}

	/**
	 * Setter for property table.
	 * 
	 * @param _table New value of property table.
	 */
	public void setTable(final String _table) {

		String oldTable = this.table;
		this.table = _table;

		try {
			this.propertyChangeSupport.firePropertyChange("table", oldTable, _table);
		} catch (java.lang.NullPointerException npe) {
			// do nothing
		}
		this.refresh();
	}

	/**
	 * Setter for property orderBy.
	 * 
	 * @param _orderBy New value of orderBy property
	 */
	public void setOrderBy(final String _orderBy) {

		String oldorderBy = this.orderBy;
		this.orderBy = _orderBy;

		try {
			this.propertyChangeSupport.firePropertyChange("orderBy", oldorderBy, _orderBy);
		} catch (java.lang.NullPointerException npe) {
			// do nothing
		}
		this.refresh();
	}

	/**
	 * Returns the column names based on which items are ordered
	 * 
	 * @return returns the column names based on which items are ordered
	 */
	public String getOrderBy() {
		return this.orderBy;
	}

	/**
	 * Getter for property selectText.
	 * 
	 * @return Value of property selectText.
	 */
	public String getSelectText() {

		return this.selectText;
	}

	/**
	 * Setter for property selectText.
	 * 
	 * @param _selectText New value of property selectText.
	 */
	public void setSelectText(final String _selectText) {

		String oldSelectText = this.selectText;
		this.selectText = _selectText;
		try {
			this.propertyChangeSupport.firePropertyChange("selectText", oldSelectText, _selectText);
		} catch (java.lang.NullPointerException npe) {
			// do nothing
		}
		// this.refresh();
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
	 * 
	 * @return Value of property ssConnection.
	 */
	public SSConnection getSsConnection() {

		return this.ssConnection;
	}

	/**
	 * Setter for property ssConnection.
	 * 
	 * @param _ssConnection New value of property ssConnection.
	 */
	public void setSsConnection(final SSConnection _ssConnection) {
		try {
			SSConnection oldSsConnection = this.ssConnection;
			this.ssConnection = _ssConnection;
			this.propertyChangeSupport.firePropertyChange("ssConnection", oldSsConnection, _ssConnection);
		} catch (java.lang.NullPointerException nop) {
			// do nothing
		}
		this.refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public Object getElementAt(int index) {
		// return data.get(index);
		return this.filtered_data.get(index);//

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		// return data.size();
		return this.filtered_data.size();//
	}

	/**
	 * Returns the text field used as the filter.
	 * 
	 * @return - returns the text field used as the filter text field.
	 */
	public JTextField getFilterEdit() {

		// return filtered_data.getFilterEdit();//
		return this.filter;

	}

	/**
	 * Sets the JTextField to be used as the filter field.
	 * 
	 * @param _filter - JTextField to be used to get the filter text.
	 */
	public void setFilterEdit(final JTextField _filter) {
		// filtered_data.setFilterEdit(filter);
		this.filter = _filter;
		this.text_match = new TextComponentMatcherEditor<>(_filter, null);
		this.filtered_data = new FilterList<>(this.data, this.text_match);

	}

	/**
	 * Adds the event listener for the filtered list
	 * 
	 * @param listChangeListener - list listener to be added to filtered list
	 */
	public void addListEventListener(ListEventListener<Object> listChangeListener) {//
		this.filtered_data.addListEventListener(listChangeListener);//

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	@Override
	public void addListDataListener(ListDataListener l) {
		super.addListDataListener(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	@Override
	public void setSelectedItem(Object anItem) {
		this.selectedOne = anItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	@Override
	public Object getSelectedItem() {
		return this.selectedOne;
	}
}

/*
 * $Log$ Revision 1.10 2006/05/15 15:50:09 prasanth Updated javadoc
 *
 * Revision 1.9 2006/04/21 19:11:32 prasanth Added comments & CVS tags.
 * ssConnection was set to null in populate model remoted this line of code.
 *
 */
