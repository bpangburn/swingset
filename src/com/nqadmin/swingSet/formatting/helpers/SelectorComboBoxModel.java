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

import java.beans.PropertyChangeSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;

import com.nqadmin.swingSet.datasources.SSConnection;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

/**
 * SelectorComboBoxModel.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * CombobBox Data Model that handles populating of SSDBComboBox using
 * GlazedLists.
 * 
 * @see "https://docs.oracle.com/javase/8/docs/api/javax/swing/ComboBoxModel.html"
 * @see "http://www.glazedlists.com/Home"
 */

// TODO Very similar to SelectorListModel. Need to compare both to see if they can be merged.

//public class SelectorComboBoxModel extends AbstractListModel<SelectorElement> implements ComboBoxModel<SelectorElement> {
public class SelectorComboBoxModel extends DefaultComboBoxModel<Object> {

	private static final long serialVersionUID = -1266028305085372287L;

	private Object selectedItem = null;

	/**
	 * GlazedList data for ComboBox
	 */
	public BasicEventList<SelectorElement> data = new BasicEventList<>();

	/**
	 * Map of string/value pairings for the ComboBox.
	 */
	public Map<String, Long> itemMap;

	/**
	 * Changed TextFilterList to FilterList because of new glazedlist jar update.
	 * FilterList takes in its parameters a TextComponentMatcherEditor to account
	 * for the depreciated TextFilterList methods.
	 */
	public FilterList<SelectorElement> filteredData = new FilterList<>(this.data);

	/**
	 * Used for matching GlazedList items
	 */
	public TextComponentMatcherEditor<SelectorElement> textMatch;

	/**
	 * Holds value of JTextField used to filter
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
	 * 
	 * @param _ssConnection     - connection object to be used for running the query
	 * @param _query            - name of the query to be used
	 * @param _primaryKeyColumn - column name whose values should be used as
	 *                          underlying values
	 * @param _displayColumn    - column name whose values should be used for
	 *                          displaying
	 */
	public SelectorComboBoxModel(final SSConnection _ssConnection, final String _query, final String _primaryKeyColumn,
			final String _displayColumn) {
		super();
		this.propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
		setSSConnection(_ssConnection);
		setQuery(_query);
		setPrimaryKeyColumn(_primaryKeyColumn);
		setDisplayColumn(_displayColumn);

	}

	/**
	 * This function re-fetches the information from the database.
	 */
	public void refresh() {
		this.populateModel();
	}

	/**
	 * Returns the value corresponding to the item at the specified index.
	 * 
	 * @param _index - index of the item whose value should be returned.
	 * @return returns the value of the item at the specified index
	 */
	public Object getSelectedBoundData(final int _index) {
		Object itm = this.filteredData.get(_index);
		if (itm != null) {
			return ((SelectorElement) (itm)).getDataValue();
		}
		return null;
	}

	/**
	 * Sets the character(s) used to separate the first and second display columsn
	 * (where applicable)
	 * 
	 * @param _separator
	 */
	public void setSeparator(final String _separator) {
		this.seperator = _separator;
	}

	/**
	 * Sets the text to be used to filter items in the list
	 * 
	 * @param _newFilter - text to be used to filter item in the list
	 */
	public void setFilterText(final String[] _newFilter) {

		this.textMatch.setFilterText(_newFilter);
		this.filteredData = new FilterList<>(this.data, this.textMatch);

	}

	/**
	 * Converts the database column value into string. Only date columns are
	 * formated as specified by dateFormat variable all other column types are
	 * retrieved as strings
	 * 
	 * @param _rs
	 * @param _columnName
	 * @return string value of database column
	 */
	protected String getStringValue(final ResultSet _rs, final String _columnName) {
		String strValue = "";
		try {
			int type = _rs.getMetaData().getColumnType(_rs.findColumn(_columnName));
			switch (type) {
			case Types.DATE:
				SimpleDateFormat myDateFormat = new SimpleDateFormat(getDateFormat());
				strValue = myDateFormat.format(_rs.getDate(_columnName));
				break;
			default:
				strValue = _rs.getString(_columnName);
				break;
			}
			if (strValue == null) {
				strValue = "";
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return strValue;

	}

	/**
	 * Populates the list model with the data by fetching it from the database.
	 */
	@SuppressWarnings("boxing")
	private void populateModel() {
		this.itemMap = new HashMap<>();
		Object primaryValue = null;
		Object displayValue = null;

		this.data.getReadWriteLock().writeLock().lock();
		try {

			try (Statement statement = this.ssConnection.getConnection().createStatement();
					ResultSet rs = statement.executeQuery(this.query)) {
				this.data.clear();
				int i = 0;
				while (rs.next()) {
					// IF TWO COLUMNS HAVE TO BE DISPLAYED IN THE COMBO THEY SEPERATED BY SEMI-COLON
					if (this.secondDisplayColumnName != null && !this.secondDisplayColumnName.trim().equals("")) {
						displayValue = getStringValue(rs, this.displayColumn) + this.seperator
								+ rs.getString(this.secondDisplayColumnName);
					} else {
						displayValue = getStringValue(rs, this.displayColumn);
					}

					// ADD THE PK TO A VECTOR.
					primaryValue = rs.getString(this.primaryKeyColumn);
					this.data.add(new SelectorElement(primaryValue, displayValue));
					this.itemMap.put(rs.getString(this.primaryKeyColumn), (long) i);

					// INCREMENT
					i++;
				}
			}

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (java.lang.NullPointerException npe) {
			npe.printStackTrace();
		} finally {
			this.data.getReadWriteLock().writeLock().unlock();
		}

		// FILL THE FILTERED DATA WITH THE COMPLETE DATA GOT FROM DATABASE
		this.filteredData.getReadWriteLock().writeLock().lock();
		try {
			this.filteredData.dispose();
			this.filteredData = new FilterList<>(this.data);
		} finally {
			this.filteredData.getReadWriteLock().writeLock().unlock();
		}

		this.fireContentsChanged(this, 0, this.filteredData.size() - 1);
		this.fireIntervalRemoved(this, 0, 1);
		this.fireIntervalAdded(this, 0, 1);

	}

	/**
	 * Adds an element to the data
	 * 
	 * @param _ob - object to be added to the data
	 */
	public void addElement(final SelectorElement _ob) {
		this.data.getReadWriteLock().writeLock().lock();
		try {
			this.data.add(_ob);
		} finally {
			this.data.getReadWriteLock().writeLock().unlock();
		}
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
	 * Getter for property primaryKeyColumn.
	 * 
	 * @return Value of property primaryKeyColumn.
	 */
	public String getPrimaryKeyColumn() {
		return this.primaryKeyColumn;
	}

	/**
	 * Setter for property primaryKeyColumn.
	 * 
	 * @param _primaryKeyColumn New value of property primaryKeyColumn.
	 */
	public void setPrimaryKeyColumn(final String _primaryKeyColumn) {
		String oldPrimaryKeyColumn = this.primaryKeyColumn;
		this.primaryKeyColumn = _primaryKeyColumn;
		try {
			this.propertyChangeSupport.firePropertyChange("primaryKeyColumn", oldPrimaryKeyColumn, _primaryKeyColumn);
		} catch (java.lang.NullPointerException npe) {
			npe.printStackTrace();
		}
	}

	/**
	 * Getter for property displayColumn.
	 * 
	 * @return Value of property displayColumn.
	 */
	public String getDisplayColumn() {
		return this.displayColumn;
	}

	/**
	 * Setter for property displayColumn.
	 * 
	 * @param _displayColumn New value of property displayColumn.
	 */
	public void setDisplayColumn(final String _displayColumn) {
		String oldDisplayColumn = this.displayColumn;
		this.displayColumn = _displayColumn;
		try {
			this.propertyChangeSupport.firePropertyChange("displayColumn", oldDisplayColumn, _displayColumn);
		} catch (java.lang.NullPointerException npe) {
			npe.printStackTrace();
		}

	}

	/**
	 * Setter for property displayColumn.
	 * 
	 * @param _secondDisplayColumn New value of property displayColumn.
	 */
	public void setSecondDisplayColumn(final String _secondDisplayColumn) {
		String oldDisplayColumn = this.secondDisplayColumnName;
		this.secondDisplayColumnName = _secondDisplayColumn;
		try {
			// 2019-03-02_BP: changing the last parameter to match the other set methods
			this.propertyChangeSupport.firePropertyChange("secondDisplayColumn", oldDisplayColumn,
					_secondDisplayColumn);
			// this.propertyChangeSupport.firePropertyChange("secondDisplayColumn",
			// oldDisplayColumn,
			// this.secondDisplayColumnName);
		} catch (java.lang.NullPointerException npe) {
			npe.printStackTrace();
		}
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
	public void setSSConnection(final SSConnection _ssConnection) {
		try {
			SSConnection oldSsConnection = this.ssConnection;
			this.ssConnection = _ssConnection;
			this.propertyChangeSupport.firePropertyChange("ssConnection", oldSsConnection, _ssConnection);
		} catch (java.lang.NullPointerException npe) {
			npe.printStackTrace();
		}
	}

	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return this.dateFormat;
	}

	/**
	 * @param _dateFormat the dateFormat to set
	 */
	public void setDateFormat(final String _dateFormat) {
		this.dateFormat = _dateFormat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public SelectorElement getElementAt(int index) {
		return this.filteredData.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		return this.filteredData.size();
	}

	/**
	 * Returns the text field used as the filter.
	 * 
	 * @return - returns the text field used as the filter text field.
	 */
	public JTextField getFilterEdit() {
		return this.filter;
	}

	/**
	 * Sets the query used to display items in the combo box.
	 *
	 * @param _query query to be used to get values from database (to display combo
	 *               box items)
	 */
	public void setQuery(String _query) {
		this.query = _query;
	}

	/**
	 * Sets the JTextField to be used as the filter field.
	 * 
	 * @param _filter - JTextField to be used to get the filter text.
	 */
	public void setFilterEdit(final JTextField _filter) {
		this.filter = _filter;
		if (this.textMatch != null)
			this.textMatch.dispose();
		this.textMatch = new TextComponentMatcherEditor<>(_filter, null);
		this.filteredData.setMatcherEditor(this.textMatch);
	}

	/**
	 * Adds the event listener for the filtered list
	 * 
	 * @param _listChangeListener - list listener to be added to filtered list
	 */
	public void addListEventListener(final ListEventListener<SelectorElement> _listChangeListener) {
		this.filteredData.addListEventListener(_listChangeListener);

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
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	@Override
	public Object getSelectedItem() {
		return this.selectedItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	@Override
	public void setSelectedItem(final Object _anItem) {
		this.selectedItem = _anItem;
	}

}

/*
 * $Log: SelectorComboBoxModel.java,v $ Revision 1.12 2013/12/09 22:32:53
 * prasanth Updated the variable names, specifying the data types for lists and
 * printing exceptions when there is an exception.
 *
 * Revision 1.11 2012/08/08 20:14:49 beevo Modified to support the navigation in
 * the SSDBCOMBOBOX filter.
 *
 * Revision 1.10 2006/05/15 15:50:09 prasanth Updated javadoc
 *
 * Revision 1.9 2006/04/21 19:11:32 prasanth Added comments & CVS tags.
 * ssConnection was set to null in populate model remoted this line of code.
 *
 */
