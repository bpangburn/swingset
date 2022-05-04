/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

import static com.nqadmin.swingset.models.OptionMappingSwingModel.asOptionMappingSwingModel;

import java.awt.Dimension;
import java.io.Serializable;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.models.AbstractComboBoxListSwingModel;
import com.nqadmin.swingset.models.OptionMappingSwingModel;
import com.nqadmin.swingset.models.SSCollectionModel;
import com.nqadmin.swingset.models.SSDbArrayModel;
import com.nqadmin.swingset.models.SSListItem;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

// SSList.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Provides a way to display a list of elements and map them to corresponding
 * database codes.
 * These mappings are typically provided by {@code setOptions} method;
 * if provided mappings are null they default to zero to N-1.
 * The mappings for the selected {@code JList} values are stored in a DB as
 * controlled by a {@link com.nqadmin.swingset.models.SSCollectionModel};
 * if not specified {@link com.nqadmin.swingset.models.SSDbArrayModel} is
 * used by default and this model saves the selected mappings in a column of
 * type {@code JDBCType.ARRAY}.
 * <pre>
 * {@code 
 * SSList list = new SSList(JDBCType.DOUBLE);
 * List<String> options = {"VLarge", "large", "medium", "small", "VSmall};
 * List<Double> mappings = {100.0, 10.0, 5.0, 1.0, 0.1};
 * list.setOptions(options, mappings);
 * list.bind(myRowset, "my_column");}
 * </pre>
 * From the example above, if three values VLarge, medium, small are selected the
 * array element in the database will store {100.0,5.0,1.0}
 * 
 * @see OptionMappingSwingModel
 */
// TODO: this should be class SSList<M,O>
public class SSList extends JList<SSListItem> implements SSComponentInterface {

	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * text field.
	 */
	protected class SSListListener implements ListSelectionListener, Serializable {

		private static final long serialVersionUID = 4337396603209239909L;
		
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			ssCommon.removeRowSetListener();
			updateRowSet();
			ssCommon.addRowSetListener();
		}
	}

	private static class Model extends OptionMappingSwingModel<Object, String, Object> {
		static Model install(JList<SSListItem> _jl) {
			Model model = new Model();
			AbstractComboBoxListSwingModel.install(_jl, model);
			return model;
		}

		private Model() {
			// false means no Options2
			super(false);
		}
	}

	private Model optionSwingModel;

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = SSUtils.getLogger();

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -5698401719124062031L;


	/**
	 * This model read/write the database
	 */
	private SSCollectionModel selectedDBModel;

	/**
	 * when true, mappings were autoGenerated
	 */
	//TODO: this goes away when all the deprecated setOptions/Mappings go away?
	private boolean autoGeneratedMapping;

	/**
	 * Underlying values for each list item choice of 0, 1, 2, 3, etc.
	 * 
	 * @deprecated Use {@link #getMappings()} instead.
	 */
	// TODO: make this private, remove this not used anymore
	@Deprecated
	protected Object[] mappings = null;

	/**
	 * Options to be displayed in list box.
	 * 
	 * @deprecated Use {@link #getOptions()} instead.
	 */
	// TODO: make this private, remove this not used anymore
	@Deprecated
	protected String[] options = null;

	/**
	 * Common fields shared across SwingSet components
	 */
	protected SSCommon ssCommon = new SSCommon(this);

	/**
	 * Creates an object of SSList with mapping type of {@code JDBCType.INTEGER}.
	 */
	public SSList() {
		// 2022-05-04: Changing from JDBCType.NULL to INTEGER as that will be the most likely
		//  mapping type and NULL is known to generate errors.
		this(JDBCType.INTEGER);
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
		this.selectedDBModel = _collectionModel;
		// last line of constructor safe to access this
		Model.install(this);

		// uncomment this to run some tests
		// testStuff(this);
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
	 * @deprecated use getMappingsList() or getMapingsArray
	 */
	//
	// When ready to remove, just change signature to
	// List<Object> and deprecate getMappingsList
	//
	@Deprecated
	public Object[] getMappings() {
		return  optionSwingModel.getMappings().toArray();
	}

	/**
	 * Returns the items displayed in the list box.
	 *
	 * @return the items displayed in the list box
	 * @deprecated use getOptionsList()
	 */
	//
	// When ready to remove, just change signature to
	// List<String> and deprecate getOptionsList
	//
	@Deprecated
	public String[] getOptions() {
		return optionSwingModel.getOptions().toArray(new String[0]);
	}

	/**
	 * Returns the items displayed in the list box.
	 * This is read only.
	 * <p>
	 * The returned list is live, tracking the active options in the JList.
	 * Use copy or toArray for a static copy.
	 * 
	 * @return the items displayed in the list box. Read Only.
	 */
	public List<String> getOptionsList() {
		return optionSwingModel.getOptions();
	}

	/**
	 * Returns the underlying values for each of the items in the list box (e.g. the
	 * database values that map to the items displayed in the list box).
	 * This is read only.
	 * <p>
	 * The returned list is live, tracking the active mappings in the JList.
	 * Use copy or toArray for a static copy.
	 *
	 * @return the mapping values for the items displayed in the list box. Read Only.
	 */
	public List<Object> getMappingsList() {
		return optionSwingModel.getMappings();
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

	/**
	 * @return a list with the mappings values corresponding to the selected indices
	 */
	public List<Object> getSelectedMappings() {
		return Arrays.stream(getSelectedIndices())
				.mapToObj((index) -> optionSwingModel.getMappings().get(index))
				.collect(Collectors.toList());
	}

	@Override
	public void setListData(Vector<? extends SSListItem> listData) {
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
	 * {@inheritDoc }
	 */
	@Override
	public SSListListener getSSComponentListener() {
		return new SSListListener();
	}

	/**
	 * {@inheritDoc}
	 * Set up OptionMappingSwingModel if parameter matches.
	 */
	@Override
	public void setModel(ListModel<SSListItem> _model) {
		OptionMappingSwingModel<?, ?, ?> model = asOptionMappingSwingModel(_model);
		optionSwingModel = model instanceof Model ? (Model)model : null;

		super.setModel(_model);
	}

	/**
	 * Convenience method for accessing the model with proper casting.
	 * 
	 * @return mapping list model with proper casting
	 */
	//
	// Don't think this is needed, or desirable, but for testing...
	//
	public OptionMappingSwingModel<Object, String, Object> getOptionModel() {
		return optionSwingModel;
	}

	/**
	 * Sets the underlying values for each of the items in the list box (e.g. the
	 * values that map to the items displayed in the list box)
	 * 
	 * @param _mappings An array of values that correspond to those in the list box.
	 * @deprecated use {@link #setOptions(java.util.List, java.util.List) }
	 */
	@Deprecated
	protected void setMappings(final Object[] _mappings) {
		Objects.requireNonNull(_mappings);
		
		try (Model.Remodel remodel = optionSwingModel.getRemodel()) {
			if (remodel.isEmpty()) {
				logger.warn(() -> "Setting mappings before options", new Exception());
			} else if (!autoGeneratedMapping) {
				logger.warn(() -> "Setting mappings over existing mappings", new Exception());
			}
			// Simplest, recreate the item list from scratch.
			// Need to capture current options (or array of nulls).
			List<String> currentOptions;
			if (remodel.isEmpty()) {
				currentOptions = Arrays.asList(new String[_mappings.length]);
			} else {
				currentOptions = new ArrayList<String> (remodel.getOptions());
			}
			setOptions(currentOptions, Arrays.asList(_mappings));
		}
	}

	/**
	 * Adds an array of strings as combo box items.
	 * 
	 * @param _options the list of options that you want to appear in the list box.
	 * @deprecated use {@link #setOptions(String[], Object[])}
	 */
	// TODO: private?
	// TODO: should this discard current mappings and establish zero to N-1?
	@Deprecated
	protected void setOptions(final String[] _options) {
		if (!(optionSwingModel.getItemList().isEmpty() || autoGeneratedMapping)) {
			logger.warn(() -> "Setting options, old mappings discarded ", new Exception());
		}
		setOptions(_options, null);
	}

	/**
	 * Sets the options to be displayed in the list box along with
	 * their corresponding mappings to database values. If {@code _mappings}
	 * is null, then a zero to N-1 mapping is automatically established.
	 * 
	 * @param _options  options to be displayed in the list box.
	 * @param _mappings null or database values that correspond to the options in
	 *					the list box. The type is set by JList constructor.
	 * @return returns true if the options and mappings are set successfully.
	 * @throws IllegalArgumentException if arrays are not the same size.
	 * @deprecated use {@code setOptions(List<String> options, List<Object> mappings)}
	 */
	@Deprecated
	public boolean setOptions(final String[] _options, final Object[] _mappings) {
		Objects.requireNonNull(_options);

		List<Object> mappingsList = _mappings == null ? null : Arrays.asList(_mappings);
		setOptions(Arrays.asList(_options), mappingsList);

		// Do copy in this method. Once this method gets removed,
		// these fields should already be deleted.
		mappings = _mappings == null ? null : Arrays.copyOf(_mappings, _mappings.length); 
		options = Arrays.copyOf(_options, _options.length);
		return true;
	}

	/**
	 * Sets the options to be displayed in the list box;
	 * a zero to N-1 mapping is established.
	 * 
	 * @param _options  options to be displayed in the list box.
	 */
	public void setOptions(List<String> _options) {
		setOptions(_options, null);
	}

	/**
	 * Sets the options to be displayed in the combo box based on
	 * the enum class' value's toString(). Generate a {@literal [0-N)}
	 * mapping.
	 *
	 * @param <T> inferred enum type
	 * @param _enumOptions enum class with values to display
	 */
	public <T extends Enum<T>> void setOptions(Class<T> _enumOptions) {
		// Could mark known enums for special handling,
		// Could have a special method for getting the display string.
		// But what's wrong with toString()

		setOptions(Stream.of(_enumOptions.getEnumConstants())
				.map((t) -> t.toString()).collect(Collectors.toList()));
	}

	/**
	 * Sets the options to be displayed in the list box along with
	 * their corresponding mappings to database values. If {@code _mappings}
	 * is null, then a zero to N-1 mapping is automatically established.
	 * 
	 * @param _options  options to be displayed in the list box.
	 * @param _mappings null or database values that correspond to the options, 1 to 1, in
	 *					the list box.
	 * @throws IllegalArgumentException if lists are not the same size.
	 */
	public void setOptions(List<String> _options, List<Object> _mappings) {
		Objects.requireNonNull(_options);
		try (Model.Remodel remodel = optionSwingModel.getRemodel()) {
			if (_mappings != null && _options.size() != _mappings.size()) {
				throw new IllegalArgumentException("Options and Mappings must be the same length");
			}
			
			autoGeneratedMapping = false;
			List<Object> mappings1 = _mappings;
			if (mappings1 == null) {
				// Provide a [0,N) mapping
				// TODO: in the future let it be implicit, don't create it
				mappings1 = IntStream.range(0, _options.size())
						.collect(ArrayList::new, List::add, List::addAll);
				autoGeneratedMapping = true;
			}

			List<String> opts = optionSwingModel.getDisconnectedList(_options);
			mappings1 = optionSwingModel.getDisconnectedList(mappings1);
			
			remodel.clear();
			remodel.addAll(mappings1, opts);
		}
	}

	/**
	 * Selects appropriate elements in the list box
	 *
	 * @param _selectedMappings Values to be selected in list
	 */
	public void setSelectedValues(final Object[] _selectedMappings) {
		setSelectedIndices(Arrays.stream(_selectedMappings)
				.mapToInt(o -> optionSwingModel.getMappings().indexOf(o))
				.toArray());
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
			selectedDBModel.writeData(getRowSet(), getBoundColumnName(),
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

		if (optionSwingModel == null) {
			return;
		}

		Object[] array = null;
		try {
			if (getRowSet().getRow() > 0) {
			    array = selectedDBModel.readData(getRowSet(), getBoundColumnName());
			}
		} catch (final SQLException se) {
			logger.error(getColumnForLog() + ": SQL Exception.", se);
		}
		
		if (array == null) {
			logger.debug("{}: Array is null. Clearing selection.", () -> getColumnForLog());
			clearSelection();
			return;
		}

		logger.debug("{}: Updating component with array of " + Arrays.toString(array) + ".", () -> getColumnForLog());
		setSelectedValues(array);
	}

	/**
	 * Call this when  ssList is empty and it is still usable after return.
	 * @param ssList test this sslist
	 */
	public static void testStuff(SSList ssList) {
		OptionMappingSwingModel<Object, String, Object> optionModel = ssList.getOptionModel();
		String[] listItems = {"LI 1","LI 2", "LI 3", "LI 4", "LI 5", "LI 6", "LI 7"};
		Object[] listCodes = {1,2,3,4,5,6,7};
		String[] otherOptions = new String[] {"one", "two", "three", "four"};
		Object[] otherMappings = new Object[] {"oneM", "twoM", "threeM", "fourM"};
		try (OptionMappingSwingModel<Object, String, Object>.Remodel remodel = optionModel.getRemodel()) {
			ssList.setOptions(listItems, listCodes);
			List<Object> mappings = optionModel. getMappings();
			boolean isShadow = optionModel.hasShadow(mappings);
			System.out.println("Shadow true: " + isShadow);
			isShadow = optionModel.hasShadow(Arrays.asList(listCodes));
			System.out.println("Shadow false: " + isShadow);

			ssList.setOptions(listItems);
			System.out.println("T1" + optionModel.dump());
			ssList.setOptions(otherOptions);
			System.out.println("T2" + optionModel.dump());
			ssList.setOptions(listItems, listCodes);
			System.out.println("T3" + optionModel.dump());
			System.out.println("EXPECT WARNING");
			ssList.setOptions(otherOptions); // WARN old discarded
			System.out.println("T4" + optionModel.dump());
			ssList.setOptions(listItems, listCodes);
			System.out.println("T5" + optionModel.dump());
			System.out.println("EXPECT WARNING");
			ssList.setMappings(listCodes); // WARN  other mapping exist
			System.out.println("T6" + optionModel.dump());
			remodel.clear();
			System.out.println("EXPECT WARNING");
			ssList.setMappings(otherMappings); // WARN  should set options first
			System.out.println("T7" + optionModel.dump());
			System.out.println("EXPECT WARNING");
			ssList.setOptions(otherOptions); // OOPS: this discard the mappings.
											 // Options are all null, could detect
			System.out.println("T8" + optionModel.dump());
			try {
				System.out.println("EXPECT EXCEPTION");
				ssList.setOptions(otherOptions, listCodes); // EXCEPTION sizes
			} catch(Exception ex) { System.out.println("EXCEPTION"); }
			System.out.println("T9" + optionModel.dump());

			// check shadows
			// If shadows are used, there are exceptions and empty lists
			ssList.setOptions(otherOptions , otherMappings);
			System.out.println("T10" + optionModel.dump());
			try { ssList.setOptions(optionModel.getOptions(), Arrays.asList(otherMappings));
			} catch(Exception ex) { System.out.println("EXCEPTION"); }
			System.out.println("T11" + optionModel.dump());
			try { ssList.setOptions(Arrays.asList(otherOptions), optionModel.getMappings());
			} catch(Exception ex) { System.out.println("EXCEPTION"); }
			System.out.println("T12" + optionModel.dump());
			try { ssList.setOptions(optionModel.getOptions(), optionModel.getMappings());
			} catch(Exception ex) { System.out.println("EXCEPTION"); }
			System.out.println("T13" + optionModel.dump());


			remodel.clear();
		}
	}
}