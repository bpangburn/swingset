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

package com.nqadmin.swingSet;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.nqadmin.swingSet.datasources.SSRowSet;
import com.nqadmin.swingSet.utils.SSArray;

/**
 * SSList.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Provides a way to display a list of elements and map them to corresponding
 * database codes. The selected values can be stored in a DB array element.
 * These mappings can be provided by setOptions method.
 * 
 * e.g.
 *      SSList list = new SSList();
 *      String[] options = {"VLarge""large", "medium", "small", "VSmall};
 *      Double[] mappings = {100.0, 10.0, 5.0, 1.0, 0.1};
 *      list.setOptions(options, mappings);
 *      list.bind(myRowset, "my_column", "myDataType");
 *      
 * If three values VLarge, medium, small are selected the array element in the
 * database will store {100.0,5.0,1.0}
 */
public class SSList extends JList<Object> {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -5698401719124062031L;

	/**
	 * SSRowSet from which component will get/set values.
	 */
	protected SSRowSet sSRowSet;

	/**
	 * SSRowSet column to which the component will be bound.
	 */
	protected String columnName = "";

	/**
	 * Underlying values for each list item choice
	 * of 0, 1, 2, 3, etc.
	 */
	protected Object[] mappings = null;

	/**
	 * Options to be displayed in list box.
	 */
	protected String[] options;

	/**
	 * Data Type name of the underlying elements of database array
	 */
	private String baseTypeName;

	/**
	 * Component listener for list selection changes.
	 */
	private final MyListListener listListener = new MyListListener();

	/**
	 * Database bound rowset listener.
	 */
	private final MyRowSetListener rowsetListener = new MyRowSetListener();

	/**
	 *  Creates an object of SSComboBox.
	 */
	public SSList() {
		init();
	}

	/**
	 * Sets the SSRowSet column name to which the component is bound.
	 * @param _columnName	Column name to which the component is bound.
	 */
	public void setColumnName(String _columnName) {
		String oldValue = columnName;
		columnName = _columnName;
		firePropertyChange("columnName", oldValue, columnName);
		try {
			bind();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}    

	/**
	 * Returns the column name to which the component is bound.
	 * @return returns the column name to which to component is bound.
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * Sets the SSRowSet to which the component is bound.
	 * @param _sSRowSet    SSRowSet to which the component is bound
	 */
	public void setSSRowSet(SSRowSet _sSRowSet) {
		SSRowSet oldValue = sSRowSet;
		sSRowSet = _sSRowSet;
		firePropertyChange("sSRowSet", oldValue, sSRowSet);
		try {
			bind();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}     

	/**
	 * Returns the SSRowSet being used to get the values.
	 * @return returns the SSRowSet being used.
	 */
	public SSRowSet getSSRowSet() {
		return sSRowSet;
	}

	/**
	 * Sets the underlying values for each of the items in the list box
	 * (e.g. the values that map to the items displayed in the list box)
	 * @param _mappings		An array of values that correspond to those in the list box.
	 */
	protected void setMappings(Object[] _mappings) {
		Object[] oldValue = (Object[])_mappings.clone();
		mappings = (Object[])_mappings.clone();
		firePropertyChange("mappings", oldValue, mappings);
	}

	/**
	 * Returns the underlying values for each of the items in the list box
	 * (e.g. the values that map to the items displayed in the list box)
	 * @return returns the underlying values for each of the items in the list box
	 */
	public Object[] getMappings() {
		return mappings;
	}

	/**
	 * Adds an array of strings as combo box items.
	 * @param _options    the list of options that you want to appear in the list box.
	 */
	protected void setOptions(String[] _options) {
		String[] oldValue = (String[])_options.clone();
		options = (String[])_options.clone();
		firePropertyChange("options", oldValue, options);
		// ADD SPECIFIED ITEMS TO THE LIST BOX
		setListData(options);
	}

	/**
	 * Returns the items displayed in the list box.
	 * @return returns the items displayed in the list box
	 */
	public String[] getOptions() {
		return options;
	}    

	/**
	 * Sets the options to be displayed in the list box and their corresponding values.
	 * @param _options    options to be displayed in the list box.
	 * @param _mappings    integer values that correspond to the options in the list box.
	 * @return returns true if the options and mappings are set successfully -
	 *    returns false if the size of arrays do not match or if the values could
	 *    not be set
	 */
	public boolean setOptions(String[] _options, Object[]_mappings) {
		if (_options.length != _mappings.length) {
			return false;
		}
		setOptions(_options);
		setMappings(_mappings);
		return true;
	}

	/**
	 * Selects appropriate elements in the list box
	 * @param values Values to be selected in list
	 */
	public void setSelectedValues(Object[] values) {
		int[] selectedIndices = new int[values.length];
		for(int i = 0; i < values.length; i++) {
			for(int j = 0; j < mappings.length; j++) {
				if(values[i] == mappings[j])
					selectedIndices[i] = j;
			}
		}
		this.setSelectedIndices(selectedIndices);
	}

	/**
	 * Returns the list value associated with the currently selected item.
	 * @return returns the value associated with the selected item OR -1 if nothing is selected.
	 */
	public Object[] getSelectedValues() {
		if (getSelectedIndex() == -1) {
			return new Object[]{-1};
		}
		Object[] selectedValues = new Object[getSelectedIndices().length];
		for(int i = 0; i < selectedValues.length; i++) {
			selectedValues[i] = mappings != null? mappings[getSelectedIndices()[i]] : Integer.valueOf(getSelectedIndices()[i]);
		}
		return selectedValues;
	}

	/**
	 * Initialization code.
	 */
	protected void init() {
		// ADD KEY LISTENER TO TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER
		// KEY IS PRESSED.
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					((Component) ke.getSource()).transferFocus();
				}
			}
		});

		// SET PREFERRED DIMENSIONS
		setPreferredSize(new Dimension(200, 40));
	}    

	/**
	 * Sets the SSRowSet and column name to which the component is to be bound.
	 * @param _sSRowSet    datasource to be used.
	 * @param _columnName    Name of the column to which this check box should be bound
	 * @param _baseTypeName		Underlying DataType Name(specific to database provider)
	 * 							of the array elements in the specified column
	 */
	public void bind(SSRowSet _sSRowSet, String _columnName, String _baseTypeName) {
		baseTypeName = _baseTypeName;
		
		SSRowSet oldValue = sSRowSet;
		sSRowSet = _sSRowSet;
		firePropertyChange("sSRowSet", oldValue, sSRowSet);

		String oldValue2 = columnName;
		columnName = _columnName;
		firePropertyChange("columnName", oldValue2, columnName);

		try {
			bind();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for handling binding of component to a SSRowSet column.
	 */
	protected void bind() throws SQLException {

		// CHECK FOR NULL COLUMN/ROWSET
		if (columnName==null || columnName.trim().equals("") || sSRowSet==null) {
			return;
		}

		// REMOVE LISTENERS TO PREVENT DUPLICATION
		removeListeners();

		if(mappings == null || options == null)
			return;
		this.setListData(options);
		updateDisplay();

		// ADD BACK LISTENERS
		addListeners();

	}

	/**
	 * Converts SQL array to object array
	 * @param array SQL array
	 * @return	Object array
	 * @throws SQLException
	 */
	private Object[] toObjArray(Array array) throws SQLException{
		if(array == null) {
			return null;
		}
		Vector<Object> data= new Vector<Object>();
		switch(array.getBaseType()){
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			try{
				for(Integer num: (Integer[])array.getArray())
					data.add(num);
			} catch(ClassCastException ex) {
				for(int num: (int[])array.getArray())
					data.add(Integer.valueOf(num));
			}
			break;
		case Types.BIGINT:
			try{
				for(Long num: (Long[])array.getArray())
					data.add(num);
			} catch(ClassCastException ex) {
				for(long num: (long[])array.getArray())
					data.add(Long.valueOf(num));
			}
			break;
		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.REAL:
			try{
				for(Double num: (Double[])array.getArray())
					data.add(num);
			} catch(ClassCastException ex) {
				for(double num: (double[])array.getArray())
					data.add(Double.valueOf(num));
			}
			break;
		case Types.DECIMAL:
		case Types.NUMERIC:
			try{
				for(BigDecimal num: (BigDecimal[])array.getArray())
					data.add(num);
			} catch(ClassCastException ex) {
				ex.printStackTrace();
			}
			break;
		case Types.DATE:
			for(Date dt: (Date[])array.getArray())
				data.add(dt);
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			for(String txt: (String[])array.getArray())
				data.add(txt);
			break;
		default: 
			throw new SQLException("DataType: "+ array.getBaseTypeName() +" not supported");
		}
		return data.toArray();
	}
	
	/**
	 * Adds listeners for component and rowset
	 */
	private void addListeners() {
		sSRowSet.addRowSetListener(rowsetListener);
		addListSelectionListener(listListener);
	}

	/**
	 * Removes listeners for component and rowset.
	 */
	private void removeListeners() {
		sSRowSet.removeRowSetListener(rowsetListener);
		removeListSelectionListener(listListener);
	}
	
	/**
	 * updates the corresponding column of the rowset with the values selected in the list
	 */
	private void updateRowSet(){
		Array array;
		if(this.getSelectedIndices().length == 0)
			array = new SSArray(new Object[]{}, baseTypeName);
		else
			array = new SSArray(this.getSelectedValues(), baseTypeName);
		try {
			sSRowSet.updateArray(columnName, array);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the value displayed in the component based on the SSRowSet column
	 * binding.
	 */
	protected void updateDisplay() throws SQLException {
		
		Object[] array = null;
		if(sSRowSet.getRow() > 0 ) {
			array = toObjArray(sSRowSet.getArray(columnName));
		}
		if(array == null) {
			this.clearSelection();
			return;
		}
		int[] indices = new int[array.length];
		for(int i = 0; i < array.length; i++) {
			for(int j = 0; j < mappings.length; j++) {
				if(array[i].equals(mappings[j])){
					indices[i] = j;
					break;
				}
				else
					indices[i] = -1;
			}
		}
		this.setSelectedIndices(indices);
	}

	/**
	 * Rowset Listener for updating the value displayed.
	 */
	private class MyRowSetListener implements RowSetListener, Serializable {

		private static final long serialVersionUID = 8375973600687061491L;

		public void cursorMoved(RowSetEvent arg0) {
			removeListSelectionListener(listListener);
			try {
				updateDisplay();
			} catch (SQLException e) {
				e.printStackTrace();
			}            
			addListSelectionListener(listListener);
		}

		public void rowChanged(RowSetEvent event) {
			removeListSelectionListener(listListener);
			try {
				updateDisplay();
			} catch (SQLException e) {
				e.printStackTrace();
			}            
			addListSelectionListener(listListener);
		}

		public void rowSetChanged(RowSetEvent event) {
			removeListSelectionListener(listListener);
			try {
				updateDisplay();
			} catch (SQLException e) {
				e.printStackTrace();
			}            
			addListSelectionListener(listListener);
		}

	}
	/**
	 * Listener(s) for the component's value used to propagate changes back to
	 * bound text field.
	 */
	private class MyListListener implements ListSelectionListener, Serializable {

		private static final long serialVersionUID = 4337396603209239909L;

		public void valueChanged(ListSelectionEvent e) {
			removeListeners();
			updateRowSet();
			addListeners();
		}
	}
}

/*
* $Log$
* Revision 1.1  2009/11/16 17:24:55  prasanth
* Initial Commit.
*
*/
