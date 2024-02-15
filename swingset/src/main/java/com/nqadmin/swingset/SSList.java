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
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset;

import static com.nqadmin.swingset.models.OptionMappingSwingModel.asOptionMappingSwingModel;

import java.awt.Dimension;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.lang.System.Logger;
import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.models.AbstractComboBoxListSwingModel;
import com.nqadmin.swingset.models.OptionMappingSwingModel;
import com.nqadmin.swingset.models.SSCollectionModel;
import com.nqadmin.swingset.models.SSDbArrayModel;
import com.nqadmin.swingset.models.SSListItem;
import com.nqadmin.swingset.navigate.NavigateActions;
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
@SuppressWarnings("serial")
public class SSList extends JList<SSListItem> implements SSComponentInterface {

	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * text field.
	 */
	@SuppressWarnings("serial")
	protected class SSListListener implements ListSelectionListener
	{
		/** {@inheritDoc} */
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			// While adjusting don't need to update the database.
			if (e.getValueIsAdjusting())
				return;

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
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * This model read/write the database
	 */
	private SSCollectionModel selectedDBModel;

	/**
	 * Common fields shared across SwingSet components
	 */
	protected final SSCommon ssCommon = new SSCommon(this);

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
	@SuppressWarnings("LeakingThisInConstructor")
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
	 */
	public List<Object> getMappings() {
		return  optionSwingModel.getMappings();
	}

	/**
	 * Returns the items displayed in the list box.
	 *
	 * @return the items displayed in the list box
	 */
	public List<String> getOptions() {
		return optionSwingModel.getOptions();
	}

	/**
	 * @deprecated Use {@link #getSelectedMappings()} instead.
	 */
	@Deprecated
	@Override
	public Object[] getSelectedValues() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated Use {@link #getSelectedMappings()} instead.
	 */
	@Deprecated
	@Override
	public List<SSListItem> getSelectedValuesList()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @return a list with the mappings values corresponding to the selected indices
	 */
	public List<Object> getSelectedMappings() {
		return Arrays.stream(getSelectedIndices())
				.mapToObj((index) -> optionSwingModel.getMappings().get(index))
				.collect(Collectors.toList());
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
	// TODO: Tag the models with an interface, exception if not.
	//		 Then setListData will cause an exception.
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
	// TODO: Don't think this is needed, or desirable, but for testing...
	//
	public OptionMappingSwingModel<Object, String, Object> getOptionModel() {
		return optionSwingModel;
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
			
			//autoGeneratedMapping = false;
			List<Object> mappings1 = _mappings;
			if (mappings1 == null) {
				// Provide a [0,N) mapping
				// TODO: in the future let it be implicit, don't create it
				mappings1 = IntStream.range(0, _options.size())
						.collect(ArrayList::new, List::add, List::addAll);
				//autoGeneratedMapping = true;
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
	 * updates the corresponding column of the rowset with the values selected in
	 * the list
	 */
	protected void updateRowSet() {
		try {
			selectedDBModel.writeData(this, getSelectedMappings().toArray());
		} catch (final SQLException se) {
			logger.log(ERROR, () -> String.format("%s: SQL Exception.", getColumnForLog()), se);
		}
	}

	/** {@inheritDoc } */
	@Override
	public void undoRedoUpdateObject(NavigateActions.UndoRedo cmd, Object value) throws SQLException
	{
		SSComponentInterface.super.undoRedoUpdateObject(cmd, value);
		// TODO: does the following seem right
				// - If there is a selection, and none of the selection is visible
				//   then pick something an make sure it's visible.
				// - Diff the selection change and do a highlight based on that.
				//   If change adds something, then disply it.
				//   If change takes something away, what's the right thing.

		// After doing the undo/redo, make sure something selected is visible
		//SwingUtilities.invokeLater(() -> this.hidePopup());
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
			    array = selectedDBModel.readData(this);
			}
		} catch (final SQLException se) {
			logger.log(ERROR, () -> String.format("%s: SQL Exception.", getColumnForLog()), se);
		}
		
		if (array == null) {
			logger.log(DEBUG, () -> String.format("%s: Array is null. Clearing selection.", getColumnForLog()));
			clearSelection();
			return;
		}

		Object[] finalArray = array;
		logger.log(DEBUG, () -> String.format("%s: Updating component with array of %s.", getColumnForLog(), Arrays.toString(finalArray)));
		setSelectedValues(finalArray);
	}

	/**
	 * Call this when  ssList is empty and it is still usable after return.
	 * @param ssList test this sslist
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void testStuff(SSList ssList) {
		OptionMappingSwingModel<Object, String, Object> optionModel = ssList.getOptionModel();
		//String[] listItems = {"LI 1","LI 2", "LI 3", "LI 4", "LI 5", "LI 6", "LI 7"};
		List<String> listItems = List.of("LI 1","LI 2", "LI 3", "LI 4", "LI 5", "LI 6", "LI 7");
		//Object[] listCodes = {1,2,3,4,5,6,7};
		List<Object> listCodes = List.of(1,2,3,4,5,6,7);
		//String[] otherOptions = new String[] {"one", "two", "three", "four"};
		List<String> otherOptions = List.of("one", "two", "three", "four");
		//Object[] otherMappings = new Object[] {"oneM", "twoM", "threeM", "fourM"};
		List<Object> otherMappings = List.of("oneM", "twoM", "threeM", "fourM");
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
			//??? System.out.println("EXPECT WARNING");
			//??? ssList.setMappings(listCodes); // WARN  other mapping exist
			System.out.println("T6" + optionModel.dump());
			remodel.clear();
			//??? System.out.println("EXPECT WARNING");
			//??? ssList.setMappings(otherMappings); // WARN  should set options first
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
			try { ssList.setOptions(otherOptions, optionModel.getMappings());
			} catch(Exception ex) { System.out.println("EXCEPTION"); }
			System.out.println("T12" + optionModel.dump());
			try { ssList.setOptions(optionModel.getOptions(), optionModel.getMappings());
			} catch(Exception ex) { System.out.println("EXCEPTION"); }
			System.out.println("T13" + optionModel.dump());


			remodel.clear();
		}
	}
}
