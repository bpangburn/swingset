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
 *   Ernie R. Rael
 ******************************************************************************/
package com.nqadmin.swingset;

import java.awt.Dimension;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.models.OptionMappingSwingListModel;
import com.nqadmin.swingset.models.SSCollectionModel;
import com.nqadmin.swingset.models.SSDbArrayModel;

// SSList.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Provides a way to display a list of elements and map them to corresponding
 * database codes.
 * These mappings are typically provided by {@code setOptions} method;
 * if provided mappings are null they default to zero to N.
 * The mappings for the selected {@code JList} values are stored in a DB as
 * controlled by a {@link com.nqadmin.swingset.models.SSCollectionModel};
 * if not specified {@link com.nqadmin.swingset.models.SSDbArrayModel} is
 * used by default and this model saves the selected mappings in a column of
 * type {@code JDBCType.ARRAY}.
 * <pre>
 * {@code 
 * SSList list = new SSList(JDBCType.DOUBLE);
 * String[] options = {"VLarge", "large", "medium", "small", "VSmall};
 * Double[] mappings = {100.0, 10.0, 5.0, 1.0, 0.1};
 * list.setOptions(options, mappings);
 * list.bind(myRowset, "my_column");}
 * </pre>
 * From the example above, if three values VLarge, medium, small are selected the
 * array element in the database will store {100.0,5.0,1.0}
 */
public class SSList extends JList<String> implements SSComponentInterface {
	// TODO: this should be class SSList<M> where M is the java type of mappings

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


	private SSCollectionModel collectionModel;

	/**
	 * Underlying values for each list item choice of 0, 1, 2, 3, etc.
	 * 
	 * @deprecated Use {@link #getMappings()} instead.
	 */
	// TODO: make this private, remove this not used anymore
	protected Object[] mappings = null;

	/**
	 * Options to be displayed in list box.
	 * 
	 * @deprecated Use {@link #getOptions()} instead.
	 */
	// TODO: make this private, remove this not used anymore
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
	 * Creates an object of SSList with mapping type of {@code JDBCType.NULL}.
	 */
	public SSList() {
		this(JDBCType.NULL);
	}

	/**
	 * Creates an object of SSList with default
	 * of {@link com.nqadmin.swingset.models.SSDbArrayModel}.
	 *
	 * @param _jdbcType type of mapping of database elements
	 */
	public SSList(JDBCType _jdbcType) {
		// TODO: select proper model through the **DbPlugin**.
		this(new SSDbArrayModel(_jdbcType));
	}

	/**
	 * @param _collectionModel model to read/write the database
	 */
	public SSList(SSCollectionModel _collectionModel) {
		// Note that call to parent default constructor is implicit.
		//super();
		this.collectionModel = _collectionModel;
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
	 * database values that map to the items displayed in the list box)
	 *
	 * @return the mapping values for the items displayed in the list box
	 */
	// TODO: post 4.0 either change return type to List or deprecate and add List variant
	public Object[] getMappings() {
		return  myModel().getMappings().toArray();
	}

	/**
	 * Returns the items displayed in the list box.
	 *
	 * @return the items displayed in the list box
	 */
	// TODO: post 4.0 either change return type to List or deprecate and add List variant
	public String[] getOptions() {
		return myModel().getOptions().toArray(new String[0]);
	}

	/**
	 * Returns the list value associated with the currently selected item.
	 *
	 * @return returns the value associated with the selected item OR -1 if nothing
	 *         is selected.
	 *
	 * @deprecated Use {@link #getSelectedMappings()} instead.
	 */
	@Deprecated
	@Override
	public Object[] getSelectedValues() {
		if (getSelectedIndex() == -1) {
			// TODO: what's this about?
			return new Object[] { Integer.valueOf(-1) };
		}
		return getSelectedMappings().toArray();
	}

	public List<Object> getSelectedMappings() {
		int[] selectedIndices = getSelectedIndices();
		List<Object> selectedMappings = new ArrayList<>(selectedIndices.length);
		for(int index : selectedIndices) {
			selectedMappings.add(myModel().getMappingAtIndex(index));
		}
		return selectedMappings;
	}

	@Override
	public void setListData(Vector<? extends String> listData) {
		throw new UnsupportedOperationException();
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
	 * @deprecated use {@link #setOptions(String[], Object[])}
	 */
	protected void setMappings(final Object[] _mappings) {
		Objects.requireNonNull(_mappings);
		// final Object[] oldValue = _mappings.clone(); TODO: BUG
		// TODO: Seems no need to clone, mappings is being dropped
		setModelInternal(options, _mappings);
		final Object[] oldValue = mappings;
		setMappingsInternal(oldValue);
	}

	/**
	 * Adds an array of strings as combo box items.
	 *
	 * TODO: exception if doesn't match other size?
	 * TODO: private?
	 * 
	 * @param _options the list of options that you want to appear in the list box.
	 * @deprecated use {@link #setOptions(String[], Object[])}
	 */
	protected void setOptions(final String[] _options) {
		Objects.requireNonNull(_options);
		// final String[] oldValue = _options.clone(); TODO: BUG
		setModelInternal(_options, mappings);
		final String[] oldValue = options;
		setOptionsInternal(oldValue);
	}

	// TODO: cleanup when remove mappings field
	private void setMappingsInternal(Object[] _oldValue) {
		mappings = getMappings(); // XXX TODO: REMOVE
		firePropertyChange("mappings", _oldValue, myModel().getMappings().toArray());
	}

	// TODO: cleanup when remove options field
	private void setOptionsInternal(String[] _oldValue) {
		options = getOptions(); // XXX TODO: REMOVE
		firePropertyChange("options", _oldValue, myModel().getMappings().toArray());
	}

	private void setModelInternal(final String[] _options, final Object[] _mappings) {
		OptionMappingSwingListModel<Object> model
				= new OptionMappingSwingListModel<Object>(_options, _mappings);
		setModel(model);
	}

	/**
	 * Sets the options to be displayed in the list box along with
	 * their corresponding mappings to database values. If {@code _mappings}
	 * is null, then a zero to N-1 mapping is automatically established.
	 * 
	 * @param _options  options to be displayed in the list box.
	 * @param _mappings database values that correspond to the options in
	 *					the list box. The type is set by JList constructor.
	 * @return returns true if the options and mappings are set successfully.
	 * @throws IllegalArgumentException if arrays are not the same size.
	 */
	public boolean setOptions(final String[] _options, final Object[] _mappings) {
		Objects.requireNonNull(_options);
		if (_mappings != null && _options.length != _mappings.length) {
			throw new IllegalArgumentException("Options and Mappings must be the same length");
		}

		String[] oldOptions = null;
		Object[] oldMappings = null;
		OptionMappingSwingListModel<Object> oldModel = myModel();
		if (oldModel != null) {
			oldOptions = oldModel.getOptions().toArray(new String[0]);
			oldMappings = oldModel.getMappings().toArray();
		}

		// ADD SPECIFIED ITEMS TO THE LIST BOX
		setModelInternal(_options, _mappings);

		// for the events
		setOptionsInternal(oldOptions);
		if (_mappings != null) {
			setMappingsInternal(oldMappings);
		}
		return true;
	}

	/** convenience method for accessing the model with proper casting */
	private OptionMappingSwingListModel<Object> myModel() {
		ListModel<String> curModel = getModel();
		if(!(curModel instanceof OptionMappingSwingListModel)) {
			return null;
		}
		@SuppressWarnings("unchecked")
		OptionMappingSwingListModel<Object> model = (OptionMappingSwingListModel<Object>) curModel;
		return model;
	}

	/**
	 * Selects appropriate elements in the list box
	 *
	 * @param _selectedMappings Values to be selected in list
	 */
	public void setSelectedValues(final Object[] _selectedMappings) {
		final int[] selectedIndices = new int[_selectedMappings.length];
		for (int i = 0; i < _selectedMappings.length; i++) {
			selectedIndices[i] = myModel().getMappingToIndex(_selectedMappings[i]);
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
		try {
			collectionModel.writeData(getSSRowSet(), getBoundColumnName(),
							getSelectedMappings().toArray());
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

		if (myModel() == null) {
			return;
		}
		// TODO: WHY IS THIS HERE
		//this.setListData(options);

		Object[] array = null;
		try {
			if (getSSRowSet().getRow() > 0) {
			    array = collectionModel.readData(getSSRowSet(), getBoundColumnName());
			}
		} catch (final SQLException se) {
			logger.error(getColumnForLog() + ": SQL Exception.", se);
		}

		if (array == null) {
			clearSelection();
			return;
		}

		setSelectedValues(array);
	}
}
