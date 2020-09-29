/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

package com.nqadmin.swingset;

import java.awt.Dimension;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.utils.SSArray;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

// SSList.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Provides a way to display a list of elements and map them to corresponding
 * database codes. The selected values can be stored in a DB array element.
 * These mappings can be provided by setOptions method.
 * <p>
 * e.g. SSList list = new SSList(); String[] options = {"VLarge""large",
 * "medium", "small", "VSmall}; Double[] mappings = {100.0, 10.0, 5.0, 1.0,
 * 0.1}; list.setOptions(options, mappings); list.bind(myRowset, "my_column",
 * "myDataType");
 * <p>
 * If three values VLarge, medium, small are selected the array element in the
 * database will store {100.0,5.0,1.0}
 */
public class SSList extends JList<Object> implements SSComponentInterface {

	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * text field.
	 */
	protected class SSListListener implements ListSelectionListener, Serializable {

		private static final long serialVersionUID = 4337396603209239909L;

		@Override
		public void valueChanged(ListSelectionEvent e) {
			removeListeners();
			updateRowSet();
			addListeners();
		}
	}

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -5698401719124062031L;

//	/**
//	 * SSRowSet from which component will get/set values.
//	 */
//	protected SSRowSet sSRowSet;
//
//	/**
//	 * SSRowSet column to which the component will be bound.
//	 */
//	protected String columnName = "";
	
	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * Converts SQL array to object array
	 * 
	 * @param array SQL array
	 * @return Object array
	 * @throws SQLException SQLException
	 */
	private static Object[] toObjArray(Array array) throws SQLException {
		if (array == null) {
			return null;
		}
		
		logger.debug("SSList.toObjArray() contents: " + array);
	
		Vector<Object> data = new Vector<>();
		switch (array.getBaseType()) {
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			try {
				for (Integer num : (Integer[]) array.getArray())
					data.add(num);
			} catch (ClassCastException ex) {
				for (int num : (int[]) array.getArray())
					data.add(Integer.valueOf(num));
			}
			break;
		case Types.BIGINT:
			try {
				for (Long num : (Long[]) array.getArray())
					data.add(num);
			} catch (ClassCastException ex) {
				for (long num : (long[]) array.getArray())
					data.add(Long.valueOf(num));
			}
			break;
		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.REAL:
			try {
				for (Double num : (Double[]) array.getArray())
					data.add(num);
			} catch (ClassCastException ex) {
				for (double num : (double[]) array.getArray())
					data.add(Double.valueOf(num));
			}
			break;
		case Types.DECIMAL:
		case Types.NUMERIC:
			try {
				for (BigDecimal num : (BigDecimal[]) array.getArray())
					data.add(num);
			} catch (ClassCastException cce) {
				logger.error("Class Cast Exception.", cce);
			}
			break;
		case Types.DATE:
			for (Date dt : (Date[]) array.getArray())
				data.add(dt);
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			for (String txt : (String[]) array.getArray())
				data.add(txt);
			break;
		default:
		// H2 ARRAY RETURNS NULL FOR getBaseType()
		// FOR THIS AND OTHER FAILURES, TRY A LIST OF OBJECTS
			try {
				for (Object val : (Object[]) array.getArray())
					data.add(val);
			} catch (SQLException se) {
				logger.error("DataType: " + array.getBaseTypeName() + " not supported and unable to convert to generic object.", se);
			}
			break;
		}
		return data.toArray();
	}

	/**
	 * Data Type name of the underlying elements of database array
	 */
	private String baseTypeName;

	/**
	 * Underlying values for each list item choice of 0, 1, 2, 3, etc.
	 */
	protected Object[] mappings = null;

	/**
	 * Options to be displayed in list box.
	 */
	protected String[] options;

//	/**
//	 * Database bound rowset listener.
//	 */
//	private final MyRowSetListener rowsetListener = new MyRowSetListener();

	/**
	 * Common fields shared across SwingSet components
	 */
	protected SSCommon ssCommon;

//	/**
//	 * Sets the SSRowSet column name to which the component is bound.
//	 * 
//	 * @param _columnName Column name to which the component is bound.
//	 */
//	public void setColumnName(String _columnName) {
//		String oldValue = getBoundColumnName();
//		getBoundColumnName() = _columnName;
//		firePropertyChange("columnName", oldValue, getBoundColumnName());
//		try {
//			bind();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Returns the column name to which the component is bound.
//	 * 
//	 * @return returns the column name to which to component is bound.
//	 */
//	public String getBoundColumnName() {
//		return getBoundColumnName();
//	}

//	/**
//	 * Sets the SSRowSet to which the component is bound.
//	 * 
//	 * @param _sSRowSet SSRowSet to which the component is bound
//	 */
//	public void setSSRowSet(SSRowSet _sSRowSet) {
//		SSRowSet oldValue = getSSRowSet();
//		getSSRowSet() = _sSRowSet;
//		firePropertyChange("sSRowSet", oldValue, getSSRowSet());
//		try {
//			bind();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

//	/**
//	 * Returns the SSRowSet being used to get the values.
//	 * 
//	 * @return returns the SSRowSet being used.
//	 */
//	public SSRowSet getSSRowSet() {
//		return getSSRowSet();
//	}

	/**
	 * Component listener for list selection changes.
	 */
	protected final SSListListener ssListListener = new SSListListener();

	/**
	 * Creates an object of SSComboBox.
	 */
	public SSList() {
		super();
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
	}

	/**
	 * Adds any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 */
	@Override
	public void addSSComponentListener() {
		addListSelectionListener(this.ssListListener);
	}

	/**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * <p>
	 * It will actually be called from SSCommon.init() once the SSCommon data member
	 * is instantiated.
	 */
	@Override
	public void customInit() {
		// SET PREFERRED DIMENSIONS
		setPreferredSize(new Dimension(200, 40));
	}

	/**
	 * Returns the underlying values for each of the items in the list box (e.g. the
	 * values that map to the items displayed in the list box)
	 * 
	 * @return returns the underlying values for each of the items in the list box
	 */
	public Object[] getMappings() {
		return this.mappings;
	}

	/**
	 * Returns the items displayed in the list box.
	 * 
	 * @return returns the items displayed in the list box
	 */
	public String[] getOptions() {
		return this.options;
	}

	/**
	 * Returns the list value associated with the currently selected item.
	 * 
	 * @return returns the value associated with the selected item OR -1 if nothing
	 *         is selected.
	 * 
	 * @deprecated Use {@link #getSelectedValuesList()} instead.
	 * 
	 */
	@Deprecated
	@Override
	public Object[] getSelectedValues() {
		if (getSelectedIndex() == -1) {
			return new Object[] { Integer.valueOf(-1) };
		}
		Object[] selectedValues = new Object[getSelectedIndices().length];
		for (int i = 0; i < selectedValues.length; i++) {
			selectedValues[i] = this.mappings != null ? this.mappings[getSelectedIndices()[i]]
					: Integer.valueOf(getSelectedIndices()[i]);
		}
		return selectedValues;
	}

//	/**
//	 * Initialization code.
//	 */
//	protected void init() {
//		// ADD KEY LISTENER TO TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER
//		// KEY IS PRESSED.
//		addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyReleased(KeyEvent ke) {
//				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
//					((Component) ke.getSource()).transferFocus();
//				}
//			}
//		});
//
//		// SET PREFERRED DIMENSIONS
//		setPreferredSize(new Dimension(200, 40));
//	}

//	/**
//	 * Sets the SSRowSet and column name to which the component is to be bound.
//	 * 
//	 * @param _sSRowSet     datasource to be used.
//	 * @param _columnName   Name of the column to which this check box should be
//	 *                      bound
//	 * @param _baseTypeName Underlying DataType Name(specific to database provider)
//	 *                      of the array elements in the specified column
//	 */
//	public void bind(SSRowSet _sSRowSet, String _columnName, String _baseTypeName) {
//		this.baseTypeName = _baseTypeName;
//
//		SSRowSet oldValue = getSSRowSet();
//		getSSRowSet() = _sSRowSet;
//		firePropertyChange("sSRowSet", oldValue, getSSRowSet());
//
//		String oldValue2 = getBoundColumnName();
//		getBoundColumnName() = _columnName;
//		firePropertyChange("columnName", oldValue2, getBoundColumnName());
//
//		try {
//			bind();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Method for handling binding of component to a SSRowSet column.
//	 * @throws SQLException 	SQLException
//	 */
//	protected void bind() throws SQLException {
//
//		// CHECK FOR NULL COLUMN/ROWSET
//		if (getBoundColumnName() == null || getBoundColumnName().trim().equals("") || getSSRowSet() == null) {
//			return;
//		}
//
//		// REMOVE LISTENERS TO PREVENT DUPLICATION
//		removeListeners();
//
//		if (this.mappings == null || this.options == null)
//			return;
//		this.setListData(this.options);
//		updateDisplay();
//
//		// ADD BACK LISTENERS
//		addListeners();
//
//	}

	/**
	 * Returns the ssCommon data member for the current Swingset component.
	 * 
	 * @return shared/common SwingSet component data and methods
	 */
	@Override
	public SSCommon getSSCommon() {
		return ssCommon;
	}

//	/**
//	 * Adds listeners for component and rowset
//	 */
//	protected void addListeners() {
//		getSSRowSet().addRowSetListener(this.rowsetListener);
//		addListSelectionListener(this.ssListListener);
//	}
//
//	/**
//	 * Removes listeners for component and rowset.
//	 */
//	protected void removeListeners() {
//		getSSRowSet().removeRowSetListener(this.rowsetListener);
//		removeListSelectionListener(this.ssListListener);
//	}

	/**
	 * Removes any necessary listeners for the current SwingSet component. These
	 * will trigger changes in the underlying RowSet column.
	 */
	@Override
	public void removeSSComponentListener() {
		removeListSelectionListener(this.ssListListener);
	}

//	/**
//	 * Updates the value displayed in the component based on the SSRowSet column
//	 * binding.
//	 * 
//	 * @throws SQLException SQLException
//	 */
//	protected void updateDisplay() throws SQLException {
//
//		Object[] array = null;
//		if (getSSRowSet().getRow() > 0) {
//			array = toObjArray(getSSRowSet().getArray(getBoundColumnName()));
//		}
//		if (array == null) {
//			this.clearSelection();
//			return;
//		}
//		int[] indices = new int[array.length];
//		for (int i = 0; i < array.length; i++) {
//			for (int j = 0; j < this.mappings.length; j++) {
//				if (array[i].equals(this.mappings[j])) {
//					indices[i] = j;
//					break;
//				}
//				indices[i] = -1;
//			}
//		}
//		this.setSelectedIndices(indices);
//	}

//	/**
//	 * Rowset Listener for updating the value displayed.
//	 */
//	protected class MyRowSetListener implements RowSetListener, Serializable {
//
//		private static final long serialVersionUID = 8375973600687061491L;
//
//		@Override
//		public void cursorMoved(RowSetEvent arg0) {
//			removeListSelectionListener(SSList.this.ssListListener);
//			try {
//				updateDisplay();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			addListSelectionListener(SSList.this.ssListListener);
//		}
//
//		@Override
//		public void rowChanged(RowSetEvent event) {
//			removeListSelectionListener(SSList.this.ssListListener);
//			try {
//				updateDisplay();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			addListSelectionListener(SSList.this.ssListListener);
//		}
//
//		@Override
//		public void rowSetChanged(RowSetEvent event) {
//			removeListSelectionListener(SSList.this.ssListListener);
//			try {
//				updateDisplay();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			addListSelectionListener(SSList.this.ssListListener);
//		}
//
//	}

	/**
	 * Sets the underlying values for each of the items in the list box (e.g. the
	 * values that map to the items displayed in the list box)
	 * 
	 * @param _mappings An array of values that correspond to those in the list box.
	 */
	protected void setMappings(Object[] _mappings) {
		Object[] oldValue = _mappings.clone();
		this.mappings = _mappings.clone();
		firePropertyChange("mappings", oldValue, this.mappings);
	}

	/**
	 * Adds an array of strings as combo box items.
	 * 
	 * @param _options the list of options that you want to appear in the list box.
	 */
	protected void setOptions(String[] _options) {
		String[] oldValue = _options.clone();
		this.options = _options.clone();
		firePropertyChange("options", oldValue, this.options);
		// ADD SPECIFIED ITEMS TO THE LIST BOX
		setListData(this.options);
	}

	/**
	 * Sets the options to be displayed in the list box and their corresponding
	 * values.
	 * 
	 * @param _options  options to be displayed in the list box.
	 * @param _mappings integer values that correspond to the options in the list
	 *                  box.
	 * @return returns true if the options and mappings are set successfully -
	 *         returns false if the size of arrays do not match or if the values
	 *         could not be set
	 */
	public boolean setOptions(String[] _options, Object[] _mappings) {
		if (_options.length != _mappings.length) {
			return false;
		}
		setOptions(_options);
		setMappings(_mappings);
		return true;
	}

	/**
	 * Selects appropriate elements in the list box
	 * 
	 * @param values Values to be selected in list
	 */
	public void setSelectedValues(Object[] values) {
		int[] selectedIndices = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < this.mappings.length; j++) {
				if (values[i] == this.mappings[j])
					selectedIndices[i] = j;
			}
		}
		this.setSelectedIndices(selectedIndices);
	}

	/**
	 * Sets the SSCommon data member for the current Swingset Component.
	 * 
	 * @param _ssCommon shared/common SwingSet component data and methods
	 */
	@Override
	public void setSSCommon(SSCommon _ssCommon) {
		ssCommon = _ssCommon;

	}

	/**
	 * updates the corresponding column of the rowset with the values selected in
	 * the list
	 */
	protected void updateRowSet() {
		Array array;
		if (this.getSelectedIndices().length == 0)
			array = new SSArray(new Object[] {}, this.baseTypeName);
		else
			array = new SSArray(this.getSelectedValues(), this.baseTypeName);
		try {
			getSSRowSet().updateArray(getBoundColumnName(), array);
		} catch (SQLException se) {
			logger.error(getColumnForLog() + ": SQL Exception.", se);
		}
	}

	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText()
	 * <p>
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed
	 */
	@Override
	public void updateSSComponent() {

		// taken from original bind()
//		// CHECK FOR NULL COLUMN/ROWSET
//		if (getBoundColumnName() == null || getBoundColumnName().trim().equals("") || getSSRowSet() == null) {
//			return;
//		}
//
//		// REMOVE LISTENERS TO PREVENT DUPLICATION
//		removeListeners();
//
		if (this.mappings == null || this.options == null)
			return;
		this.setListData(this.options);
//		updateDisplay();
//
//		// ADD BACK LISTENERS
//		addListeners();

		// taken from updateDisplay()
		Object[] array = null;
		try {

			if (getSSRowSet().getRow() > 0) {
				
//			    Array sqlArray = getSSRowSet().getArray(getBoundColumnName());
//			    System.out.println("SQL array retrieved for SSList: " + sqlArray);
//			    Object[] objArray = (Object[])sqlArray.getArray();
//			    for (int i = 0; i < objArray.length; i++) {
//			    	System.out.println("Array element [" + i + "] contains: " + objArray[i]);
//			    }
			    
			    array = toObjArray(getSSRowSet().getArray(getBoundColumnName()));

			}

		} catch (SQLException se) {
			logger.error(getColumnForLog() + ": SQL Exception.", se);
		}

		if (array == null) {
			this.clearSelection();
			return;
		}
		int[] indices = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < this.mappings.length; j++) {
				if (array[i].equals(this.mappings[j])) {
					indices[i] = j;
					break;
				}
				indices[i] = -1;
			}
		}
		this.setSelectedIndices(indices);

	}
}