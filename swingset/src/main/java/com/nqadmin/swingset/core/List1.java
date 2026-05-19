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
 * copyright (C) 2024-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.core;


import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.common.reflect.TypeToken;
import com.nqadmin.swingset.models.AbstractComboBoxListSwingModel;
import com.nqadmin.swingset.models.KeyDisplayValueSwingModel;
import com.nqadmin.swingset.models.SSCollectionModel;
import com.nqadmin.swingset.models.SSDbArrayModel;
import com.nqadmin.swingset.models.SSListItem;
import com.nqadmin.swingset.navigate.UndoRedo;
import com.nqadmin.swingset.navigate.UndoRedo.Change;
import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.models.KeyDisplayValueSwingModel.asKeyDisplayValueSwingModel;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * Provides a way to display a list of elements and map them to corresponding
 * database codes. These keys are typically provided by {@code setDisplayValues} method;
 * if param keys are null they default to zero to N-1.
 * The keys for the selected {@code JList} values are typically in a DB as
 * controlled by a {@link com.nqadmin.swingset.models.SSCollectionModel};
 * if not specified {@link com.nqadmin.swingset.models.SSDbArrayModel} is
 * used by default and this model saves the selected keys in a column of
 * type {@code JDBCType.ARRAY}.
 * 
 * {@snippet class=ListSnippets region=init1}
 * From the example above, if three values VLarge, medium, small are selected the
 * array element in the database will store {100.0,5.0,1.0}
 * 
 * @see KeyDisplayValueSwingModel
 * 
 * @param <K>
 * @param <D> 
 */
@SuppressWarnings("serial")
public class List1<K,D> extends JList<SSListItem> implements SSComponent
{
	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * text field.
	 */
	@SuppressWarnings("serial")
	protected class ListListener implements ListSelectionListener
	{
		/** {@inheritDoc} */
		@Override
		public void valueChanged(final ListSelectionEvent e)
		{
			// While adjusting don't need to update the database.
			if (e.getValueIsAdjusting())
				return;

			try {
				dbChange(() -> updateRowSet());
			} catch (SQLException ex) {
				logger.log(Level.ERROR, (String) null, ex);
			}
		}
	}

	// Updated based on ComboBox2
	private static class Model<K,D> extends KeyDisplayValueSwingModel<K, D, Object> {
		static <K,D>Model<K,D> install(JList<SSListItem> jl) {
			Model<K,D> model = new Model<>();
			AbstractComboBoxListSwingModel.install(jl, model);
			return model;
		}

		private Model() {
			// false means no displayValue2
			super(false);
		}
	}

	private Model<K,D> swingModel;

	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * This model read/write the database
	 */
	private SSCollectionModel chosenDBModel;

	/**
	 * Creates a List1 with default
	 * of {@link com.nqadmin.swingset.models.SSDbArrayModel}
	 * of specified jdbcType.
	 *
	 * @param jdbcType type of key of database elements
	 */
	public List1(JDBCType jdbcType) {
		// TODO: select proper model through the **DbPlugin**.
		this(new SSDbArrayModel(jdbcType));
	}

	/**
	 * @param chosenDBModel model to read/write the database
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public List1(SSCollectionModel chosenDBModel) {
		this.chosenDBModel = chosenDBModel;

		finishSSCommon();

		// last line of constructor safe to access this
		Model.install(this);

		// uncomment this to run some tests
		// testStuff(this);
	}

	/**
	 * Returns the underlying values for each of the items in the list box (e.g. the
	 * database values that map to the items displayed in the list box)
	 *
	 * @return the key values for the items displayed in the list box
	 */
	public List<K> getKeys() {
		return  swingModel.getKeys();
	}

	/**
	 * Returns the items displayed in the list box,
	 * both selected and not selected, in the list box.
	 *
	 * @return the items displayed in the list box
	 */
	public List<D> getDisplayValues() {
		return swingModel.getDisplayValues();
	}

	/**
	 * Leave this here so it's use can be detected.
	 * @deprecated Use {@link #getChosenKeys()} instead.
	 */
	@Deprecated
	@Override
	public Object[] getSelectedValues() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Leave this here so it's use can be detected.
	 * @deprecated Use {@link #getChosenKeys()} instead.
	 */
	@Deprecated
	@Override
	public List<SSListItem> getSelectedValuesList()
	{
		throw new UnsupportedOperationException();
	}

	// TODO: Item2 getChosenItem()
	// TODO: List<Item> getChosenItems()

	/**
	 * Return the chosenItem with methods
	 * @return 
	 */
	// TODO: provide D2
	// TODO: OPTIM: only get the SSListItem once.
	public Item2<K,D,Object> getChosenItem() {
		return new Item2<>(getChosenKey(), getChosenDisplayValue());
	}

	/**
	 * The Key at the smallest selected index; null if no selection.
	 * @return the Key at the smallest selected index
	 */
	public K getChosenKey() {
		int idx = getSelectedIndex();
		return idx != -1 ? swingModel.getKeys().get(idx) : null;
	}

	/**
	 * @return a list with the Keys corresponding to the selected indices
	 */
	public List<K> getChosenKeys() {
		return Arrays.stream(getSelectedIndices())
				.mapToObj((index) -> swingModel.getKeys().get(index))
				.collect(Collectors.toList());
	}
	
	/**
	 * @return a list with the displayValues corresponding to the selected indices
	 */
	public D getChosenDisplayValue() {
		int idx = getSelectedIndex();
		return idx != -1 ? swingModel.getDisplayValues().get(idx) : null;
	}
	
	/**
	 * @return a list with the displayValues corresponding to the selected indices
	 */
	public List<D> getChosenDisplayValues() {
		return Arrays.stream(getSelectedIndices())
				.mapToObj((index) -> swingModel.getDisplayValues().get(index))
				.collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
 Set up KeyDisplayValueSwingModel if parameter matches.
	 */
	// TODO: Tag the models with an interface, exception if not.
	//		 Then setListData will cause an exception.
	@Override
	@SuppressWarnings("unchecked")
	public void setModel(ListModel<SSListItem> model) {
		KeyDisplayValueSwingModel<?, ?, ?> tModel = asKeyDisplayValueSwingModel(model);
		swingModel = tModel instanceof Model ? (Model<K,D>)tModel : null;

		super.setModel(model);
	}

	/**
	 * Convenience method for accessing the model with proper casting.
	 * <p>
	 * TESTING ONLY.
	 * 
	 * @return key list model with proper casting
	 */
	//
	// TODO: Don't think this is needed, or desirable, but for testing...
	@SuppressWarnings("unchecked")
	KeyDisplayValueSwingModel<Object, String, Object> getSwingModel() {
		return (KeyDisplayValueSwingModel<Object, String, Object>) swingModel;
	}

	/**
	 * Sets the displayValues to be displayed in the list box;
	 * zero to N-1 keys are established.
	 * 
	 * @param displayValues  displayValues to be displayed in the list box.
	 */
	public void setDisplayValues(List<D> displayValues) {
		setDisplayValues(displayValues, null);
	}

	/**
	 * Sets the displayValues to be displayed in the list based on
	 * the enum class' value's toString().Generate {@literal [0-N)}
 keys, which corresponds to e.ordinal().
	 *
	 * @param <T> inferred enum type
	 * @param enumDisplayValues enum class with values to display
	 */
	public <T extends Enum<T>> void setDisplayValues(Class<T> enumDisplayValues)
	{
		if (getDisplayValueType() != String.class)
			throw new IllegalArgumentException(
					"DisplayValue type must be String for Enum displayValues");

		List<String> l01 = Stream.of(enumDisplayValues.getEnumConstants())
				.map((t) -> t.toString()).collect(Collectors.toList());

		@SuppressWarnings("unchecked")
		List<D> l02 = (List<D>)l01;
		setDisplayValues(l02, null);
	}

	/**
	 * Sets the displayValues to be displayed in the list box along with
 their corresponding keys to database values.If {@code keys}
 is null, then zero to N-1 keys are automatically established.
	 * <p>
 If keys is null, then autogenerate the keys;
 keys type must be Integer or Long.
	 * 
	 * @param displayValues  displayed in the list box.
	 * @param keys null or database values that correspond to the displayValues,
	 *             1 to 1, in the list box.
	 * @throws IllegalArgumentException if lists are not the same size.
	 */
	public void setDisplayValues(List<D> displayValues, List<K> keys) {
		setDisplayValuesInternal(displayValues, keys);
	}

	/**
	 * Set up the specified keys/displayValues.
	 * <p>
 If _keys is null, then autogenerate the keys;
 _keys type must be Integer or Long.
	 * @param _displayValues
	 * @param _keys
	 */
	// TODO: Don't allow Object for autogenerated keys
	protected void setDisplayValuesInternal(List<D> _displayValues, List<K> _keys) {
		Objects.requireNonNull(_displayValues);
		if (_keys == null
				&& getKeyType() != Integer.class
				&& getKeyType() != Long.class
				&& getKeyType() != Object.class)
			throw new IllegalArgumentException(
					"Auto generated key only avaialable for int/long keys");
		if (_keys != null && _displayValues.size() != _keys.size())
			throw new IllegalArgumentException("_DisplayValues and Keys different length");
		try (Model<K,D>.Remodel remodel = swingModel.getRemodel()) {

			///// autoGeneratedKeys = false;
			List<K> keys = _keys;
			if (keys == null) {
				// Provide a [0,N) key
				List<Object> autoKeys;
				if (getKeyType() == Long.class)
					autoKeys = LongStream.range(0, _displayValues.size())
							.collect(ArrayList::new, List::add, List::addAll);
				else // defautl to Integer
					autoKeys = IntStream.range(0, _displayValues.size())
							.collect(ArrayList::new, List::add, List::addAll);
				@SuppressWarnings("unchecked")
				List<K> xxx = (List<K>)autoKeys;
				keys = xxx;
				///// autoGeneratedKeys = true;
			}

			List<D> displayValues = swingModel.getDisconnectedList(_displayValues);
			keys = swingModel.getDisconnectedList(keys);
			
			remodel.clear();
			remodel.addAll(keys, displayValues);
		}
	}

	// TODO: public void setChosenKey(K chosenKey) {

	/**
	 * Selects appropriate elements in the list box
	 *
	 * @param chosenKeys Values to be selected in list
	 */
	// TODO: list<M>
	// TODO: K[]
	public void setChosenKeys(Object[] chosenKeys) {
		setSelectedIndices(Arrays.stream(chosenKeys)
				.mapToInt(o -> swingModel.getKeys().indexOf(o))
				.toArray());
	}

	private Class<K> keyType;

	/**
	 * Return the actual type of the Key parameter.
	 * @return the type
	 */
	final public Class<K> getKeyType() {
		if (keyType == null) {
			//@SuppressWarnings("unchecked")
			TypeToken<K> typeToken = new TypeToken<K>(getClass()) { };
			@SuppressWarnings("unchecked")
			Class<K> t = (Class<K>) typeToken.getType();
			keyType = t;
		}
		return keyType;
	}

	// The actual type of a displayValue.
	private Class<D> visualType;

	/**
	 * Return the actual type of the Visual parameter.
	 * @return the type
	 */
	final public Class<D> getDisplayValueType() {
		if (visualType == null) {
			//@SuppressWarnings("unchecked")
			TypeToken<D> typeToken = new TypeToken<D>(getClass()) { };
			@SuppressWarnings("unchecked")
			Class<D> t = (Class<D>) typeToken.getType();
			visualType = t;
		}
		return visualType;
	}

	/**
	 * updates the corresponding column of the rowset with the values selected in
	 * the list
	 */
	// TODO: quiet failure.
	protected void updateRowSet() {
		try {
			chosenDBModel.writeData(this, getChosenKeys().toArray());
		} catch (final SQLException se) {
			logger.log(Level.ERROR, () -> sf("%s: SQL Exception.", getColumnForLog()), se);
		}
	}

	/** {@inheritDoc } */
	@Override
	public void undoRedoUpdateObject(UndoRedo cmd, Change change) throws SQLException
	{
		SSComponent.super.undoRedoUpdateObject(cmd, change);
		// TODO: does the following seem right
				// - If there is a selection, and none of the selection is visible
				//   then pick something an make sure it's visible.
				// - Diff the selection change and do a highlight based on that.
				//   If change adds something, then disply it.
				//   If change takes something away, what's the right thing.

		// After doing the undo/redo, make sure something selected is visible
		//SwingUtilities.invokeLater(() -> this.hidePopup());
	}

	/** {@inheritDoc } */
	@Override
	public void cleanField()
	{
		clearSelection();
	}

	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText()
	 * <p>
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed
	 */
	public void updateComponent() {

		if (swingModel == null) {
			return;
		}

		Object[] array = null;
		//
		// TODO: Should getBoundColumnObject() be used here?
		//		 Seems like it, it's used just about everywhere else.
		//
		try {
			if (getRowSet().getRow() > 0) {
				array = chosenDBModel.readData(List1.this);
			}
		} catch (final SQLException se) {
			logger.log(Level.ERROR, () -> sf("%s: SQL Exception.", getColumnForLog()), se);
		}
		
		if (array == null) {
			logger.log(DEBUG, () -> sf("%s: Array is null. Clearing selection.", getColumnForLog()));
			clearSelection();
			return;
		}

		Object[] finalArray = array;
		logger.log(DEBUG, () -> sf("%s: Updating component with array of %s.", getColumnForLog(), Arrays.toString(finalArray)));
		setChosenKeys(finalArray);
	}

	private Hook hook;

	/** {@inheritDoc } */
	@Override
	public final Hook getSSComponentHook()
	{
		if (hook == null)
			hook = new Hook(this) {
				@Override
				protected void updateSSComponent()
				{
					updateComponent();
				}
				
				/** {@inheritDoc } */
				@Override
				protected ListListener getSSComponentListener() {
					return new ListListener();
				}
				
				/** {@inheritDoc } */
				@Override
				protected void addSSComponentListener(EventListener eventListener)
				{
					addListSelectionListener((ListSelectionListener) eventListener);
				}
				
				/** {@inheritDoc } */
				@Override
				protected void removeSSComponentListener(EventListener eventListener)
				{
					removeListSelectionListener((ListSelectionListener) eventListener);
				}
				
			};
		return hook;
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("%s{items=%s, %s}", getClass().getSimpleName(),
				getChosenKeys(), SSUtils.ssComponentToString(this));
	}
}
