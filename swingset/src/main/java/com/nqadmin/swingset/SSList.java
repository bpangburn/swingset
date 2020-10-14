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
		public void valueChanged(final ListSelectionEvent e) {
			removeListeners();
			updateRowSet();
			addListeners();
		}
	}

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -5698401719124062031L;

	/**
	 * Converts SQL array to object array
	 *
	 * @param array SQL array
	 * @return Object array
	 * @throws SQLException SQLException
	 */
	private static Object[] toObjArray(final Array array) throws SQLException {
		if (array == null) {
			return null;
		}

		logger.debug("SSList.toObjArray() contents: " + array);
		
		// TODO May be able to utilize JDBCType Enum here.
		// TODO This may be better as a static method in SSJdbcRowSetImpl

		final Vector<Object> data = new Vector<>();
		switch (array.getBaseType()) {
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			try {
				for (final Integer num : (Integer[]) array.getArray()) {
					data.add(num);
				}
			} catch (final ClassCastException ex) {
				for (final int num : (int[]) array.getArray()) {
					data.add(Integer.valueOf(num));
				}
			}
			break;
		case Types.BIGINT:
			try {
				for (final Long num : (Long[]) array.getArray()) {
					data.add(num);
				}
			} catch (final ClassCastException ex) {
				for (final long num : (long[]) array.getArray()) {
					data.add(Long.valueOf(num));
				}
			}
			break;
		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.REAL:
			try {
				for (final Double num : (Double[]) array.getArray()) {
					data.add(num);
				}
			} catch (final ClassCastException ex) {
				for (final double num : (double[]) array.getArray()) {
					data.add(Double.valueOf(num));
				}
			}
			break;
		case Types.DECIMAL:
		case Types.NUMERIC:
			try {
				for (final BigDecimal num : (BigDecimal[]) array.getArray()) {
					data.add(num);
				}
			} catch (final ClassCastException cce) {
				logger.error("Class Cast Exception.", cce);
			}
			break;
		case Types.DATE:
			for (final Date dt : (Date[]) array.getArray()) {
				data.add(dt);
			}
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			for (final String txt : (String[]) array.getArray()) {
				data.add(txt);
			}
			break;
		default:
		// H2 ARRAY RETURNS NULL FOR getBaseType()
		// FOR THIS AND OTHER FAILURES, TRY A LIST OF OBJECTS
			try {
				for (final Object val : (Object[]) array.getArray()) {
					data.add(val);
				}
			} catch (final SQLException se) {
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

	/**
	 * Common fields shared across SwingSet components
	 */
	protected SSCommon ssCommon = new SSCommon(this);

	/**
	 * Component listener for list selection changes.
	 */
	protected final SSListListener ssListListener = new SSListListener();

	/**
	 * Creates an object of SSComboBox.
	 */
	public SSList() {
		// Note that call to parent default constructor is implicit.
		//super();
	}

	/**
	 * Adds any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 */
	@Override
	public void addSSComponentListener() {
		addListSelectionListener(ssListListener);
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
		return mappings;
	}

	/**
	 * Returns the items displayed in the list box.
	 *
	 * @return returns the items displayed in the list box
	 */
	public String[] getOptions() {
		return options;
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
		final Object[] selectedValues = new Object[getSelectedIndices().length];
		for (int i = 0; i < selectedValues.length; i++) {
			selectedValues[i] = mappings != null ? mappings[getSelectedIndices()[i]]
					: Integer.valueOf(getSelectedIndices()[i]);
		}
		return selectedValues;
	}

	/**
	 * Returns the ssCommon data member for the current Swingset component.
	 *
	 * @return shared/common SwingSet component data and methods
	 */
	@Override
	public SSCommon getSSCommon() {
		return ssCommon;
	}

	/**
	 * Removes any necessary listeners for the current SwingSet component. These
	 * will trigger changes in the underlying RowSet column.
	 */
	@Override
	public void removeSSComponentListener() {
		removeListSelectionListener(ssListListener);
	}

	/**
	 * Sets the underlying values for each of the items in the list box (e.g. the
	 * values that map to the items displayed in the list box)
	 *
	 * @param _mappings An array of values that correspond to those in the list box.
	 */
	protected void setMappings(final Object[] _mappings) {
		final Object[] oldValue = _mappings.clone();
		mappings = _mappings.clone();
		firePropertyChange("mappings", oldValue, mappings);
	}

	/**
	 * Adds an array of strings as combo box items.
	 *
	 * @param _options the list of options that you want to appear in the list box.
	 */
	protected void setOptions(final String[] _options) {
		final String[] oldValue = _options.clone();
		options = _options.clone();
		firePropertyChange("options", oldValue, options);
		// ADD SPECIFIED ITEMS TO THE LIST BOX
		setListData(options);
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
	public boolean setOptions(final String[] _options, final Object[] _mappings) {
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
	public void setSelectedValues(final Object[] values) {
		final int[] selectedIndices = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < mappings.length; j++) {
				if (values[i] == mappings[j]) {
					selectedIndices[i] = j;
				}
			}
		}
		setSelectedIndices(selectedIndices);
	}

	/**
	 * Sets the SSCommon data member for the current Swingset Component.
	 *
	 * @param _ssCommon shared/common SwingSet component data and methods
	 */
	@Override
	public void setSSCommon(final SSCommon _ssCommon) {
		ssCommon = _ssCommon;

	}

	/**
	 * updates the corresponding column of the rowset with the values selected in
	 * the list
	 */
	protected void updateRowSet() {
		Array array;
		if (getSelectedIndices().length == 0) {
			array = new SSArray(new Object[] {}, baseTypeName);
		} else {
			array = new SSArray(getSelectedValues(), baseTypeName);
		}
		try {
			getSSRowSet().updateArray(getBoundColumnName(), array);
		} catch (final SQLException se) {
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

		if ((mappings == null) || (options == null)) {
			return;
		}
		this.setListData(options);

		Object[] array = null;
		try {

			if (getSSRowSet().getRow() > 0) {
			    array = toObjArray(getSSRowSet().getArray(getBoundColumnName()));
			}

		} catch (final SQLException se) {
			logger.error(getColumnForLog() + ": SQL Exception.", se);
		}

		if (array == null) {
			clearSelection();
			return;
		}
		final int[] indices = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < mappings.length; j++) {
				if (array[i].equals(mappings[j])) {
					indices[i] = j;
					break;
				}
				indices[i] = -1;
			}
		}
		setSelectedIndices(indices);

	}
}