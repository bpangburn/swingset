/* *****************************************************************************
 * Copyright (C) 2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 * ****************************************************************************/
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import com.google.common.reflect.TypeToken;
import com.nqadmin.swingset.models.AbstractComboBoxListSwingModel;
import com.nqadmin.swingset.models.GlazedListsKeyDisplayValueInfo;
import com.nqadmin.swingset.models.KeyDisplayValueSwingModel;
import com.nqadmin.swingset.models.SSListItem;
import com.nqadmin.swingset.models.SSListItemFormat;
import com.nqadmin.swingset.navigate.RowSetModificationEvent;
import com.nqadmin.swingset.navigate.UndoRedo;
import com.nqadmin.swingset.navigate.UndoRedo.Change;
import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import static com.nqadmin.swingset.models.AbstractComboBoxListSwingModel.addEventLogging;
import static com.nqadmin.swingset.models.KeyDisplayValueSwingModel.asKeyDisplayValueSwingModel;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * Provide a way of displaying "options/text", corresponding to
 * "keys/mappings" in a {@code JComboBox} . The combo display values are created
 * by an {@linkplain SSListItemFormat}. If not specified, keys are auto-generated
 * [0-N); and in this case {@literal "<K>"} must be "Integer" or "Long". In some
 * applications, a key may be a key in some database table, see
 * {@linkplain DBComboBox2}. There are convenience methods for using an enum as
 * the basis of "options"/"keys", see {@link #setDisplayValues(java.lang.Class)}.
 * <p>
 * This is the base class for combo boxes; it uses/handles {@link #getAllowNull()
 * getAllowNull()}/{@link #setAllowNull(boolean) setAllowNull(boolean)},
 * and tracks metadata changes, for adding/removing nullItem to the item list.
 * In addition, there are JComboBox models for use with or without glazed lists;
 * these models have static methods for hooking things up to the JComboBox,
 * see {@linkplain ModelType}, which may be used in constructors to select the model type.
 * <p>
 * <b>Warning</b>.<br>
 * This combo box automatically inserts an item when {@link #getAllowNull()} is true.
 * This comboBox may use GlazedLists which dynamically changes the contents
 * of the combo box list.
 * Do not use methods that are based on index in the combo box list, unless
 * you're sure...
 * <p>
 * For example use 
 * {@link ComboBox2#getChosenKey() getChosenKey()}
 * not something that is based on {@code getSelectedIndex()}.
 * Change the current/selected combo box item with methods
 * such as
 * {@link ComboBox2#setChosenKey(java.lang.Object) setChosenKey(Integer)}
 * and
 * {@link ComboBox2#setChosenDisplayValue(java.lang.Object) setChosenDisplayValue(String)}.
 * Use the methods {@link ComboBox2#hasItems() hasItems() } and
 * {@link ComboBox2#hasSelection() hasSelection() } which take into account
 * {@code getAllowNull()}.
 * <p>
 * Notice that: {@link ComboBox2#getChosenKey() getChosenKey()}
 * returns null in two situations related to {@link #getAllowNull() }
 * <ul>
 *   <li>nothing is selected in this combo box
 *   <li>the <em>nullItem</em> is selected in this combo box
 * </ul>
 * <p>
 * {@code getSelectedItem() == null } indicates there is no
 * combo box selection; it is different than {@code !hasSelection()}
 * when {@code getAllowNull()} is true.
 * <p>
 * If subclasses need to work directly with the combo box model,
 * refer to {@link com.nqadmin.swingset.models.KeyDisplayValueSwingModel}
 * and especially
 * {@link KeyDisplayValueSwingModel.Remodel}
 * <p>
 * Here's an example of using auto-generated keys
 * {@snippet class=ComboBoxSnippets region=auto_gen}
 * The generated keys start at zero, the key:value pairs for the above are:
 * {@literal {0: "111", 1: "2222", 2: "33333"}}.
 * <p>
 * To provide your own keys, set the keys along with the options.
 * <br>
 * {@snippet class=ComboBoxSnippets region=custom_key}
 * <p>
 * Classes that extend this class, should use {@link #adjustForNullItem() }
 * after clearing the itemList or when a change is made that might affect
 * whether or not a nullItem is required.
 <p>
 * Note that if you do not want to use the auto-generated keys, the keys
 * must be set before invoking the
 * {@link com.nqadmin.swingset.navigate.RowsModel#bind(com.nqadmin.swingset.utils.SSComponent, java.lang.String) bind()}
 * method to associate the combobox to a database column.
 * <p>
 * Initially no DisplayValue2.
 * @param <K> key type
 * @param <D> displayValue type
 * @param <D2> displayValue2 type
 * @since 4.0.0
 */
@SuppressWarnings("serial")
public abstract class ComboBox2<K,D,D2>
		extends JComboBox<SSListItem>
		implements SSComponent
{
	/** A convenience for variable declarations. Do not instantiate. */
	protected abstract class Model extends KeyDisplayValueSwingModel<K,D,D2> { }

	/** Constructor argument to indicate whether or not to use GlazedList model. */
	public enum ModelType {
		/** use glazed model */
		GLAZED,
		/** use swing model */
		SWING,
	}

	/**
	 * Listener(s) for the component's value used to propagate changes back to the rowset in certain instances.
	 * <p>
	 * NOTE: No guaranty whether ActionListener or FocusLister will be called first when focus is lost.
	 */
	protected class ComboBox2Listener implements ActionListener
	{
		/**
		 * For JComboBox ActionListener and ItemListener are similar, but ActionListener
		 * seems more appropriate since we don't care about the de-selection of the
		 * previously selected item.
		 * 
		 * Per https://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html#listeners
		 * ActionListener just tells us that the selection changed whereas
		 * ItemListener fires separate events for deselection of previous item
		 * and selection of current item.
		 *
		 * {@inheritDoc} */
		@Override
		public void actionPerformed(final ActionEvent ae)
		{
			// Return if a combo navigator, SSSyncManager will have it's own
			// listeners. This listener for keeping a bound column in sync.
			if (isComboBoxNavigator()) {
				logger.log(DEBUG, () -> sf("%s: Action Listener isComboBoxNavigator.",
						getColumnForLog()));
				return;
			}

			logger.log(DEBUG, () -> sf("%s: About to update RowSet with %s.",
					getColumnForLog(), getSelectedItem()));

			K key = getChosenKey();
			try {
				dbChange(() -> setBoundColumnObject(key));
			} catch (SQLException ex) {
				logger.log(Level.ERROR, (String) null, ex);
			}
		}
	}

	/**
	 * Listens/waits for selected item not nullItem. When not nullItem is
	 * set, selectionPending set to false.
	 */
	private final class ComboBox2ItemListener implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			//System.err.println(e.paramString());
			if (e.getStateChange() == ItemEvent.SELECTED) {
				// Move cleanupMissing... to updateSSComponent so that the
				// combo list doesn't change while a row is displayed.
				// If needed, could have a control option for when
				// to do the cleanup.
				// cleanupMissingKeyVisuals();
				if (getSelectedItem() != nullItem && selectionPending) {
					setSelectionPending(false);
					//System.err.println("itemStateChanged true --> false");
				}
			}
		}
	}

	/**
	 * KeyDisplayValue model that can be installed in a JComboBox; this model has methods
	 * for building and manipulating the item list.
	 * @param <K> key type
	 * @param <D> displayValue type
	 * @param <D2> displayValue2 type
	 */
	protected static class BaseModel<K,D,D2> extends KeyDisplayValueSwingModel<K,D,D2>
	{

		/**
		 * Create model and install it in the specified JComboBox.
		 * 
		 * @param <K> key type
		 * @param <D> displayValue type
		 * @param <D2> displayValue2 type
		 * @param _jc install model into this
		 * @return KeyDisplayValue model
		 */
		protected static <K,D,D2>BaseModel<K,D,D2> install(ComboBox2<K,D,D2> _jc) {
			BaseModel<K,D,D2> model = new BaseModel<>();
			AbstractComboBoxListSwingModel.install(_jc, model);

			return model;
		}

		/**
		 * Create a model.
		 */
		protected BaseModel() {
			// false means no displayValues2
			super(false);
		}
	}

	/**
	 * KeyDisplayValue model for use with GlazedLists; this model has methods
	 * for building and manipulating the EventList and supports locking.
	 * @param <K> key type
	 * @param <D> displayValue type
	 * @param <D2> displayValue2 type
	 */
	protected static class BaseGlazedModel<K,D,D2> extends GlazedListsKeyDisplayValueInfo<K,D,D2>
	{

		/**
		 * The GlazedLists auto completion support for the
		 * associated JComboBox.
		 */
		protected AutoCompleteSupport<SSListItem> autoComplete;

		/**
		 * Create a model that has a glazed EventList;
		 * install autocompletion in the specified JComboBox.
		 *
		 * @param <K> key type
		 * @param <D> displayValue type
		 * @param <D2> displayValue2 type
		 * @param _jc install auto completion into this
		 * @return KeyDisplayValue model
		 */
		protected static <K,D,D2>BaseGlazedModel<K,D,D2> install(ComboBox2<K,D,D2> _jc) {
			BaseGlazedModel<K,D,D2> model = new BaseGlazedModel<>();
			model.autoComplete = AutoCompleteSupport.install(_jc, model.getEventList(), null, model.getListItemFormatDelegate());
			
			model.autoComplete.setFilterMode(TextMatcherEditor.CONTAINS);
			model.autoComplete.setStrict(true);
			model.autoComplete.setPositionCaretTowardZero(true);

			// RESTORE JCOMBOBOX UP/DOWN ARROW HANDLING OVERRIDING GLAZEDLIST
			_jc.glazedListArrowHandler();

			return model;
		}

		/**
		 * Create a model with a glazed EventList.
		 */
		@SuppressWarnings("Convert2Diamond")
		protected BaseGlazedModel() {
			// false means no DisplayValue2
			super(false, new BasicEventList<SSListItem>());
		}
	}

	/** {@inheritDoc } */
	@Override
	public void addUndoableChange(RowSetModificationEvent ev) throws SQLException
	{
		SSComponent.super.addUndoableChange(ev);
		UndoRedo.newSlot(this);
	}

	/** {@inheritDoc } */
	@Override
	public void undoRedoUpdateObject(UndoRedo cmd, Change change) throws SQLException
	{
		SSComponent.super.undoRedoUpdateObject(cmd, change);
		SwingUtilities.invokeLater(() -> this.hidePopup());
	}

	/**
	 * @return the key index in an SSListItem
	 */
	public int getKeyFormatIndex() {
		return keyVisual.getKeyListItemElemIndex();
	}

	/**
	 * @return the displayValue index in an SSListItem
	 */
	public int getDisplayValueFormatIndex() {
		return keyVisual.getDisplayValueListItemElemIndex();
	}

	/**
	 * @return the displayValue2 index in an SSListItem
	 */
	public int getDisplayValue2FormatIndex() {
		return keyVisual.getDisplayValue2ListItemElemIndex();
	}

	/**
	 * A list item formatter that displays the key if the displayValue is null.
	 */
	@SuppressWarnings("serial")
	class ShowKeyIfNullDisplayValue extends SSListItemFormat {

		/**
		 * If null displayValue, then show the key; otherwise do the default
		 * formatting.
		 *
		 * @param sb display value goes in here
		 * @param elemIndex which displayValue to format
		 * @param listItem combobox list item
		 */
		@Override
		protected void appendValue(StringBuffer sb, int elemIndex, SSListItem listItem) {
			if (getDisplayValueFormatIndex() == elemIndex
					&& getElem(elemIndex, listItem) == null
					&& !Objects.equals(getNullItem(), listItem)) {
				Object key = getElem(getKeyFormatIndex(), listItem);
				sb.append(key != null ? key.toString() : null)
						.append(" - DisplayValue Not Found");
			} else {
				super.appendValue(sb, elemIndex, listItem);
			}
		}
	}

	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * When {@link #getAllowNull() } is true, this is the null item;
	 * when false this is null. So {@link #setSelectedItem(java.lang.Object)
	 * setSelectedItem(nullItem)} does the right thing whether getAllowNull()
	 * is true or false.
	 * <p>
	 * <b>When the item list is cleared, for example
	 * {@link KeyDisplayValueSwingModel.Remodel#clear() remodel.clear()}
	 * the nullItem must be set to null.</b>
	 * @see #createNullItem(KeyDisplayValueSwingModel.Remodel)
	 */
	protected SSListItem nullItem;

	/**
	 * This is used when moving to a new row when getAllowNull() == false 
	 * and strict glazed is true. The ComboBox needs to be shown empty,
	 * ie. without a value, until something is selected. After something
	 * is selected then strict applies.
	 * 
	 * There are some event issues check out {@link #setSelectionPending(boolean) }.
	 * With cleaner event model, Navigator events?, can probably ...
	 */
	private boolean selectionPending;

	/**
	 * The combo model.
	 */
	protected KeyDisplayValueSwingModel<K,D,D2> keyVisual;

	// The actual type of the key.
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

	// The actual type of the displayValue.
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

	// The actual type of the displayValue.
	private Class<D2> visual2Type;

	/**
	 * Return the actual type of the Visual2 parameter.
	 * @return the type
	 */
	final public Class<D2> getDisplayValue2Type() {
		if (visual2Type == null) {
			//@SuppressWarnings("unchecked")
			TypeToken<D2> typeToken = new TypeToken<D2>(getClass()) { };
			@SuppressWarnings("unchecked")
			Class<D2> t = (Class<D2>) typeToken.getType();
			visual2Type = t;
		}
		return visual2Type;
	}

	//////////////////////////////////////////////////////////////////////////
	//
	// Model stuff
	//

	/**
	 * <b>It is probably an error to use this.</b>
	 * {@inheritDoc}
	 */
	// TODO: throw exception?
	@Override
	@SuppressWarnings("unchecked")
	public void setModel(ComboBoxModel<SSListItem> model) {
		addEventLogging(model);
		KeyDisplayValueSwingModel<?, ?, ?> tModel = asKeyDisplayValueSwingModel(model);
		keyVisual = tModel instanceof BaseModel ? (BaseModel<K,D,D2>)tModel
				: tModel instanceof BaseGlazedModel ? (BaseGlazedModel<K,D,D2>)tModel
				: null;

		super.setModel(model);
	}

	/**
	 * Get the support for the glazed lists installed in this combo box.
	 * 
	 * @return the support or null if GlazedLists not installed
	 */
	protected AutoCompleteSupport<SSListItem> getAutoComplete() {
		return keyVisual instanceof BaseGlazedModel
				? ((BaseGlazedModel<K,D,D2>)keyVisual).autoComplete
				: null;
	}

	/**
	 * Create BaseComboBox.
	 */
	public ComboBox2() {
		this(ModelType.SWING);
	}

	/**
	 * Create BaseComboBox.
	 * @param modelType whether to use SWING or GLAZED combo model
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public ComboBox2(ModelType modelType) {
		addItemListener(new ComboBox2ItemListener());
		finishSSCommon();

		try {
			getKeyType();
		} catch(ClassCastException ex) {
			throw new IllegalStateException(
					sf("Non generic subclass required, like '%s<...>(){}'",
							getClass().getSimpleName()),
					ex);
		}

		keyVisual = switch(modelType) {
		case GLAZED -> BaseGlazedModel.install(this);
		case SWING -> BaseModel.install(this);
		};
		keyVisual.setListItemFormat(new ShowKeyIfNullDisplayValue());
	}

	/**
	 * Set the format to use with this model. <br>
	 * TODO: consider deferring the actual change until row change
	 * 
	 * @param listItemFormat the format used with this model
	 */
	public final void setListItemFormat(SSListItemFormat listItemFormat) {
		Object selectedItem = getSelectedItem();
		boolean keep = Objects.equals(getEditor().getItem(), selectedItem);
		keyVisual.setListItemFormat(listItemFormat);
		// A new format may change the target contents of the comboBox editor.
		// When it does change, the contents of the combo editor is changed
		// from ListItem to a String with the old value.
		// If the combo editor held the selected item before,
		// then it should hold the selected item after.
		if(keep)
			getEditor().setItem(selectedItem);
	}

	/**
	 * Return the listItemFormat associated with this combobox.
	 * @return the associated listItemFormat
	 */
	public final SSListItemFormat getListItemFormat() {
		return keyVisual.getListItemFormat();
	}



	/////////////////////////////////////////////////////////////////////////
	//
	// DisplayValue/Key
	//

	/**
	 * Determine if there are displayValue/key items 
	 * that have been added to this combo box; i.e. not empty.
	 * This is tricky because
	 * if getAllowNull is true then nullItem is in the item list and
	 * shouldn't be consider an item.
	 * 
	 * @return true if there are displayValues/key
	 */
	public boolean hasItems() {
		return keyVisual.getItemList().size() != (getAllowNull() ? 1 : 0);
	}

	/**
	 * Determine if the combo box has a selection, either an item that
	 * has been added or if the combo box is editable then user entry.
	 * This is tricky because
	 * if getAllowNull is true then nullItem may be selected
	 * and that should not be considered. In particular
	 * {@code getSelectedItem() != null} and {@code getSelectedIndex() != -1}
	 * may not work as expected.
	 * 
	 * @return true if the combo box has a selection
	 */
	public boolean hasSelection() {
		Object item = getSelectedItem();
		return item != null && item != nullItem;
	}

	/**
	 * Typically true when at a new row waiting for use to select something;
	 * it is as though getAlowNull() is temporarily true.
	 * @return true if waiting for not nullItem
	 */
	public boolean isSelectionPending() {
		return selectionPending;
	}

	/**
	 * Control whether or not waiting for pending user selection.
	 * When * selectionPending is true, there is a nullItem and it is selected in
	 * the * combo. selectionPending is set to false automatically when a non nullItem
	 * is selected.
	 *
	 * @param selectionPending true selects a possibly tempory nullItem
	 */
	public void setSelectionPending(boolean selectionPending) {
		if (this.selectionPending == selectionPending) {
			return;
		}
		this.selectionPending = selectionPending;

		adjustForNullItem();
		if (selectionPending) {
			// Setting to true from false, select the nullItem;
			// Events are a problem. After SSDBNavImpl.setSelectionPending
			// the following ends up in ComboBox2Listener.actionPerformed()
			// then into setBoundColumnText then exception in RowSetOps.updateColumnText.
			// TODO: merge this into adjustForNullItem, to avoid extra unregister/register
			//
			// Or better, with cleaner event model, like Navigator events,
			// might be easier to avoid RowSetOps.updateColumnText.
			// Actually, if there was an error badge next to the ComboBox
			// (instead of those f*ing dialogs) then when AllowNull is false
			// the blank combo would start with the error badge after inster row.
			// That sounds pretty good.
			final ActionListener[] listeners = unregisterAllActionListeners(this);
			try {
				super.setSelectedItem(nullItem);
			} finally {
				registerAllActionListeners(this, listeners);
			}
		}
	}

	/**
	 * Adds a list of strings as combo box items
	 * with {@literal [0-N)} key.
	 *
	 * @param displayValues the list of displayValues that you want to appear in the combo box.
	 */
	public void setDisplayValues(List<D> displayValues) {
		setDisplayValues(displayValues, null);
	}

	/**
	 * Sets the displayValues to be displayed in the list box along with their
	 * corresponding keys to database values. If {@code keys} is null, then a
	 * zero to N-1 key is automatically established.
	 *
	 * @param displayValues  displayValues to be displayed in the list box.
	 * @param keys null or database values that correspond to the displayValues, 1 to 1,
	 * in the list box.
	 * @throws IllegalArgumentException if lists are not the same size.
	 */
	public void setDisplayValues(List<D> displayValues, List<K> keys) {
		enumVisual = null;
		setDisplayValuesInternal(displayValues, keys);
	}

	/**
	 * Returns the items displayed in the combo box.
	 * <p>
	 * <b>When getAllowNull() is true, the first list item is null/""</b>
	 *
	 * @return returns the items displayed in the combo box
	 */
	public List<D> getDisplayValues() {
		return keyVisual.getDisplayValues();
	}

	/**
	 * when true, keys were autoGenerated
	 */
	private boolean autoGeneratedKeys;

	/**
	 * Determine if keys are autoGenerated {@literal [0-N)}
	 * @return true if keys are autoGenerated
	 */
	// TODO: public? Get rid of the field
	protected boolean isAutoGeneratedKeys() {
		return autoGeneratedKeys;
	}

	/**
	 * Set up the specified keys/displayValues.
	 * <p>
	 * If _keys is null, then autogenerate the keys; _keys type must be Integer
	 * or Long.
	 *
	 * @param _displayValues
	 * @param _keys
	 */
	protected void setDisplayValuesInternal(List<D> _displayValues, List<K> _keys) {
		Objects.requireNonNull(_displayValues);
		if (_keys == null
				&& getKeyType() != Integer.class
				&& getKeyType() != Long.class)
			throw new IllegalArgumentException(
					"Auto generated key only avaialable for int/long keys");
		if (_keys != null && _displayValues.size() != _keys.size())
			throw new IllegalArgumentException("DisplayValues and Keys different length");
		try (Model.Remodel remodel = keyVisual.getRemodel()) {
			List<K> keys = _keys;
			autoGeneratedKeys = false;
			if (keys == null) {
				// Provide a [0,N) key
				// TODO: in the future let it be implicit, don't create it?
				List<Object> autoKeys;
				if (getKeyType() == Integer.class)
					autoKeys = IntStream.range(0, _displayValues.size())
							.collect(ArrayList::new, List::add, List::addAll);
				else // must be Long
					autoKeys = LongStream.range(0, _displayValues.size())
							.collect(ArrayList::new, List::add, List::addAll);
				@SuppressWarnings("unchecked")
				List<K> xxx = (List<K>)autoKeys;
				keys = xxx;
				autoGeneratedKeys = true;
			}
			keys = keyVisual.getDisconnectedList(keys);
			List<D> displayValues = keyVisual.getDisconnectedList(_displayValues);
			
			remodel.clear();
			nullItem = null;

			// TODO first item is nullItem if getAllowNull()==true.
			//      Impacts keys, getSelectedValue(), getSelectedIndex(), etc.
			adjustForNullItem();
			remodel.addAll(keys, displayValues);
		}
	}

	/**
	 * Get the keys currently in use.
	 * <p>
	 * <b>When getAllowNull() is true, the first list item is null/""</b>
	 * @return the keys
	 */
	public List<K> getKeys() {
		return keyVisual.getKeys();
	}

	/**
	 * Removes an item from the combobox's item list where the
	 * list item's key equals the param.
	 * <p>
	 * If more than one item is present in the combo for that value the first one is
	 * changed.
	 *
	 * @param key of the item that should be removed
	 *
	 * @return returns true on successful deletion otherwise returns false.
	 */
	public boolean removeKey(K key) {
		
		boolean result = false;

		try (Model.Remodel remodel = keyVisual.getRemodel()) {
			// Get index of list item for key.
			int index = remodel.getKeys().indexOf(key);
			// Proceed if key was found.
			if (index != -1) {
				remodel.remove(index);
				result = true;
			}
		} catch (final Exception e) {
			logger.log(Level.ERROR, getColumnForLog() + ": Exception.", e);
		}

		return result;
	}

	/**
	 * Adds an item to the existing list of items in the combo box.
	 *
	 * @param displayValue item that should be displayed in the combobox
	 * @param key  key of displayValue, commonly a primary key
	 */
	public void addDisplayValue(D displayValue, K key) {
		addDisplayValue(displayValue, null, key);
	}
	
	/**
	 * Adds an item to the existing list of items in the combo box.
	 *
	 * @param displayValue item that should be displayed in the combobox
	 * @param displayValue2  second display item for combobox
	 * @param key  key of displayValue, commonly a primary key
	 */
	public void addDisplayValue(D displayValue, D2 displayValue2, K key) {
		try (Model.Remodel remodel = keyVisual.getRemodel()) {
			final int index = remodel.getKeys().indexOf(key);
			if (index >= 0) {
				logger.log(WARNING, () -> sf("%s: Key of [%s] already exists. Creating duplicate Key with DisplayValue of '%s'.",
					getColumnForLog(), key, displayValue));
			}
			remodel.add(key, displayValue, displayValue2);
		} catch (final Exception e) {
			logger.log(Level.ERROR, getColumnForLog() + ": Exception.", e);
		}
	}

	/**
	 * Update displayValue of an item in the combobox's item list for given key.
	 * <p>
	 * If more than one item is present in the combo for that key, only the
	 * first one is changed.
	 *
	 * @param key typically a primary key value corresponding to the
	 *                 chosenDisplayValue to be updated
	 * @param displayValue  chosenDisplayValue that should be updated in the combobox
	 *
	 * @return returns true if update is successful otherwise returns false.
	 */
	public boolean updateDisplayValue(K key, D displayValue) {

		// 2021-02-28: @errael patched this method to deal with inconsistent
		// updating of combo editor. See https://github.com/bpangburn/swingset/issues/85
		
		boolean result = false;

		try (Model.Remodel remodel = keyVisual.getRemodel()) {
			final int index = remodel.getKeys().indexOf(key);
			if (index >= 0) {
				boolean isSelectedItem = Objects.equals(key, getChosenKey());
				remodel.setDisplayValue(index, displayValue);
				result = true;
				// Changing what's in the ComboEditor, which may be done indirectly when
				// modifying the current item, might change the currently selected item
				// when GlazedList is set to STRICT. So something is needed to insure
				// that the selected key before the change is the selected key
				// after the change. Otherwise the first item in the list becomes
				// selected. If the combo is used for navigation, this can trigger a
				// change in the current row.
				//
				// The call to setSelectedItem() below has the added benefit of working
				// around a possible bug in GlazedList
				// (see https://github.com/glazedlists/glazedlists/issues/702),
				// but it's likely best to keep this block even if the issue is
				// determined to be a bug and resolved.
				if (isSelectedItem) {
					// Modifying the underlying list item that corresponds to the
					// current selection; strict glazed may change the selection.
					// Select the modified item so the same key is selected.
					SSListItem item = remodel.get(index);
					setSelectedItem(item);
				}
// TODO Confirm that eventList is not reordered by GlazedLists code.
			}
		}

		return result;
	}

	/**
	 * Return the chosenItem with methods getKey() and getDisplayValue().
	 * <p>
	 * Here's an example of creating a non-generic version of
	 * getChosenItem().
	 * {@snippet class=ComboBoxSnippets region=chosen_item}
	 * @return the chosen item
	 */
	// TODO: neturn new Item2<>(SSListItem)
	protected Item2<K,D,D2> getChosenItem() {
		return new Item2<>(getChosenKey(), getChosenDisplayValue());
	}

	/**
	 * Returns the key code corresponding to the currently selected item in the
	 * combobox. Commonly, this is an underlying database record
	 * primary key value corresponding to the currently selected item.
	 *
	 * @return returns the value associated with the selected item
	 * OR null if nothing is selected.
	 */
	public K getChosenKey() {
		logger.log(TRACE, () -> sf("%s: getChosenKey(), idx:map %d:%s.",
				getColumnForLog(), getSelectedIndex(), getSelectedItem()));

		K result = null;

		try (Model.Remodel remodel = keyVisual.getRemodel()) {
			Object item = getSelectedItem();
			if (item instanceof SSListItem lItem) {
				result = remodel.getKey(lItem);
			}
		}

		return result;
	}

	private final List<K> generatedKeyVisuals = new ArrayList<>();
	private int addMissingKeyVisual(Model.Remodel remodel, K key) {
		int index = -1;
		if(key != null) {
			if(missingDisplayValueControl.contains(MissingDisplayValueControl.MC_ADD)) {
				remodel.add(key, null);
				index = remodel.getKeys().indexOf(key);
				logger.log(DEBUG, () -> "missingKeyDisplayvalue added: " + key);
				generatedKeyVisuals.add(key);
			}
		}
		return index;
	}

	/**
	 * If current selection does not have a null displayValue,
	 * then remove any created missingKeyDisplayValues from list.
	 */
	private void cleanupMissingKeyVisuals() {
		if (!SwingUtilities.isEventDispatchThread())
			throw new IllegalStateException("Must be EDT.");
		if(generatedKeyVisuals.isEmpty()
				|| !missingDisplayValueControl.contains(MissingDisplayValueControl.MC_CLEANUP))
			return;
		if(generatedKeyVisuals.size() > 1)
			logger.log(WARNING, () -> "size: " + generatedKeyVisuals.size());
		try (Model.Remodel remodel = keyVisual.getRemodel()) {
			for (Iterator<K> it = generatedKeyVisuals.iterator(); it.hasNext();) {
				K missingKeyVisual = it.next();
				// Sequential records might have different missing displayValues;
				// never remove a missing displayValue for the current selected key.
				if(Objects.equals(missingKeyVisual, getChosenKey()))
					continue;
				int index = remodel.getKeys().indexOf(missingKeyVisual);
				if(index != -1) {
					SSListItem removed = remodel.remove(index);
					logger.log(DEBUG, () -> "missingKeyDisplayValue removed: " + removed);
				} else {
					logger.log(WARNING, () -> "not in combo: " + missingKeyVisual);
				}
				it.remove(); // it's not in the combo list anymore
			}
		}

	}

	private final EnumSet<MissingDisplayValueControl> missingDisplayValueControl
			= EnumSet.allOf(MissingDisplayValueControl.class);

	/**
	 * Flags to control handling of Key
	 * without a specified DisplayValue;
	 * see {@link #setMissingDisplayValueControl(java.util.EnumSet) } for
	 * meaning of the flags.
	 */
	public enum MissingDisplayValueControl {

		/**
		 * Automatically generate list item entry when current key
		 * does not have a displayValue.
		 */
		MC_ADD,

		/**
		 * Remove any auto gen entry if current current key has a displayValue.
		 */
		MC_CLEANUP;
	}

	/**
	 * Set the flags that control how a Key
	 * without a specified displayValue is
	 * handled. If {@code MC_ADD} and a Key is encountered and no DisplayValue
	 * was specified, a combobox list entry is created for the Key with null
	 * displayValue; a custom message, see
	 * {@link #setListItemFormat(com.nqadmin.swingset.models.SSListItemFormat)},
	 * may be setup for handling these entries for the combobox display.If
	 * {@code MC_CLEANUP} then when the combobox navigates away from an entry
	 * with a missing displayValue, the automatically created list entry is
	 * removed.<p>
	 * Flags default to {@code MC_ADD} and {@code MC_CLEANUP}.
	 *
	 * @param flags new value
	 * @return previous value
	 */
	public EnumSet<MissingDisplayValueControl> setMissingDisplayValueControl(
			EnumSet<MissingDisplayValueControl> flags)
	{
		EnumSet<MissingDisplayValueControl> prev = missingDisplayValueControl.clone();
		missingDisplayValueControl.clear();
		missingDisplayValueControl.addAll(flags);
		return prev;
	}

	/**
	 * Sets the selected ComboBox item according to the specified key.
	 * The selectedItem is set to nullItem or null if key not found.
	 * <p>
	 * If called from updateSSComponent() from a RowSet change then the Component
	 * listener should already be turned off. Otherwise we want it on so the
	 * ultimate call to setSelectedItem() will trigger an update the to RowSet.
	 *
	 * @param key key of item value to assign to combobox,
	 *                 which may or may not correlate to the combobox index
	 */
	public void setChosenKey(K key) {
		
		logger.log(DEBUG, sf("%s: current value: %s, new value: %s.",
				getColumnForLog(), getChosenKey(), key ));

		try (Model.Remodel remodel = keyVisual.getRemodel()) {
			if (!hasItems()) {
				logger.log(WARNING, sf("%s: combobox is empty", getColumnForLog()));
				// Doesn't have items, that doesn't mean that the list is empty.
				// Make sure the appropriate null is selected.
				setSelectedItem(nullItem);
				return;
			}
			
			// only need to proceed if there is a change.
			if (Objects.equals(key, getChosenKey())) {
				return;
			}
			
			int index = remodel.getKeys().indexOf(key);
			SSListItem item;
			if (index != -1) {
				item = remodel.get(index);
			} else if((index = addMissingKeyVisual(remodel, key)) != -1) {
				item = remodel.get(index);
			} else {
				// nullItem is either special first list item
				// or it is null. It is null when getAllowNull() is false
				item = nullItem;
				// BP_2021-02-16:
				// We expect to get here if we have a child combo where the contents
				// are requeried on each record. As soon as a navigation occurs, the
				// component values are cleared and then the new value is loaded,
				// but the combo has not been re-queried yet so there are no matches
				// for setChosenKey() and we call setSelectedItem(nullItem).
				// This is OK so long as the component listener used for binding
				// is removed/disabled because the rowset will not get the null value.
				// Later when the combo is requeried, the component will try to load
				// the current column value from the rowset and this time
				// setChosenKey() should succeed.
				if (SSUtils.isSSComponentListenerAddedDebug(this)) {
					logger.log(Level.ERROR, () -> sf("%s: No key for %s in combobox, setSelectedItem(null)",
							getColumnForLog(), key));
				}
			}
			setSelectedItem(item);

			logger.log(TRACE, () -> sf("%s: eventList - [%s].",
					getColumnForLog(), remodel.getItemList().toString()));
			logger.log(TRACE, () -> sf("%s: displayvalues - [%s].",
					getColumnForLog(), remodel.getDisplayValues().toString()));
			logger.log(TRACE, () -> sf("%s: keys - [%s].",
					getColumnForLog(), remodel.getKeys().toString()));
		}
	}

	/**
	 * Returns the DisplayValue corresponding to the currently selected item in the
	 * combobox.
	 *
	 * @return returns the DisplayValue object associated with the selected item
	 * OR null if nothing is selected.
	 */
	public D getChosenDisplayValue() {
		D result = null;
		try (Model.Remodel remodel = keyVisual.getRemodel()) {
			Object item = getSelectedItem();
			if (item instanceof SSListItem lItem) {
				result = remodel.getDisplayValue(lItem);
			}
		}
		return result;
	}

	/**
	 * Returns the DisplayValue corresponding to the currently selected item in the
	 * combobox.
	 *
	 * @return returns the DisplayValue object associated with the selected item
	 * OR null if nothing is selected.
	 */
	public D2 getChosenDisplayValue2() {
		D2 result = null;
		try (Model.Remodel remodel = keyVisual.getRemodel()) {
			Object item = getSelectedItem();
			if (item instanceof SSListItem lItem) {
				result = remodel.getDisplayValue2(lItem);
			}
		}
		return result;
	}

	/**
	 * Finds the listItem having displayValue that matches the specified
	 * displayValue and make it the selected listItem. If no matching item is
	 * found the displayValue is used for
	 * {@link #setSelectedItem(java.lang.Object) setSelectedItem(displayValue)}.
	 *
	 * @param displayValue element of list item
	 */
	public void setChosenDisplayValue(D displayValue) {

		try (Model.Remodel remodel = keyVisual.getRemodel()) {
			if(!hasItems()) {
				logger.log(WARNING, sf("%s: combobox is empty", getColumnForLog()));
				// Even if combo is empty, stick displayValue in editor. Do not return;
			}
			
			Object tItem = getSelectedItem();
			// Extract the displayValue from the selected list item.
			// If not an SSListItem, it's editable, use it as current displayValue.
			Object chosenDisplayValue = tItem instanceof SSListItem
					? remodel.getDisplayValue((SSListItem)tItem)
					: tItem;
			
			// only need to proceed if there is a change.
			if (Objects.equals(displayValue, chosenDisplayValue)) {
				return;
			}
			
			// find the first matching displayValue in the list
			final int index = remodel.getDisplayValues().indexOf(displayValue);
			
			Object item;
			if (index != -1) {
				item = remodel.get(index);
			} else {
				// Didn't find it in the list, so just use it as is.
				item = displayValue != null ? displayValue : nullItem;
				logger.log(WARNING, () -> sf(
						"%s: displayValue %s not in combobox, do setSelectedItem.",
						getColumnForLog(), displayValue));
			}
			
			setSelectedItem(item);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// Using an enum as an displayValue/key.
	//

	//
	// TODO: Study use case for enums
	// TODO: When an enum is specified, should there be a way to
	//		specify a key as well? Two possibilities, the first
	//		is like the general pattern, key should have the
	//		same number of elements as there are enum values.
	//		The second could also be provided as setDisplayValues([], Function)
	//		 1) setDisplayValues(enum, key)
	//		 2) setDisplayValues(enum, Function<enum, Object>)
	//			For example
	//				setDisplayValues(ComboEnum.class, (e) -> e.someMethod())
	//				setDisplayValues(ComboEnum.class, (e) -> someMap.get(e))

	private Class<?> enumVisual;

	/**
	 * Sets the displayValues to be displayed in the combo box based on
	 * the {@code Enum}'s {@code toString()}. Generate {@literal [0-N)} keys.
	 * <p>
	 * Convenience method, throws if displayValue type/class, {@code <D>},
	 * is not String.
	 *
	 * @param <T> inferred enum type
	 * @param enumDisplayValues enum class with values to display
	 */
	public <T extends Enum<T>> void setDisplayValues(Class<T> enumDisplayValues)
	{
		if (getDisplayValueType() != String.class)
			throw new IllegalArgumentException(
					"DisplayValue type must be String for Enum displayValues");

		this.enumVisual = enumDisplayValues;
		List<String> l01 = Stream.of(enumDisplayValues.getEnumConstants())
				.map(e -> e.toString()).collect(Collectors.toList());

		@SuppressWarnings("unchecked")
		List<D> l02 = (List<D>)l01;
		setDisplayValuesInternal(l02, null);
	}

	/**
	 * Get the enum class currently displayed by this combo box.
	 * If combo box is not displaying an enum, then reutrn null.
	 * @return the enum class
	 */
	// TODO: public? Get rid of the field
	protected Class<?>getEnumDisplayValue() {
		return enumVisual;
	}
	
	/**
	 * Finds the listItem that matches the specified enum and make it the selected
	 * listItem.
	 *
	 * @param displayValue select list item for this
	 * @throws ClassCastException if displayValue is wrong enum type
	 */
	public void setChosenEnum(Enum<?> displayValue) {
		if (enumVisual == null) {
			throw new IllegalStateException("SSComboBox values not an enum.");
		}
		Objects.requireNonNull(displayValue, "Enum to be selected cannnot be null.");

		// Verify the enum of of the correct type.
		enumVisual.cast(displayValue);
		// This could be performance optimized since itemList index == ordinal()
		// within the current constraints of key == ordinal().
		// But the whole nullItem comes into play to affect the index.
		setChosenKey(getKeyType().cast(displayValue.ordinal()));
	}
	
	/**
	 * Return the selected enum.
	 * 
	 * @return selected enum.
	 * @throws IllegalStateException if not an enum.
	 */
	public Enum<?> getChosenEnum() {
		if (enumVisual == null)
			throw new IllegalStateException("SSComboBox values not an enum");
		K key = getChosenKey();
		if (key == null)
			return null;
		int enumIdx = ((Number)key).intValue();
		return (Enum<?>)enumVisual.getEnumConstants()[enumIdx];
	}

	
	/////////////////////////////////////////////////////////////////////////
	//
	// Deal with non-standard GlazedList UP/DOWN arrow handling
	//
	
	/**
	 * When dealing with GlazedLists (1.11) restore expected combo behavior.
	 * <p>
	 * Per GL JavaDoc: {@link ca.odell.glazedlists.swing.AutoCompleteSupport}
	 * <p>
	 * 4. typing the up arrow key when the popup is visible and the selected element
	 * is the first element causes the autocompletion to be cleared and the popup's
	 * selection to be removed.
	 * 6. typing the down arrow key when the popup is visible and the selected element is
	 * the last element causes the autocompletion to be cleared and the popup's selection
	 * to be removed
	 *<p>
	 * We want to restore the normal JComboBox behavior of not going past the first or
	 * last item. This would be the ideal case, matching JComboBox.
	 * If GlazedLists ever changes the arrow key behavior, this can be removed.
	 */
	protected void glazedListArrowHandler() {

		// ADD KEY LISTENER - INTERCEPTING KEYPRESSED APPEARS TO BLOCK GL
		getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {

			@Override
			// OVERRIDE KEYPRESSED, NOT KEYTYPED OR KEY RELEASED
			public void keyPressed(KeyEvent keyEvent) {
				int keyCode = keyEvent.getKeyCode();
				if (keyCode == KeyEvent.VK_UP) {
					logger.log(TRACE, () -> sf("%s: Intercepted UP key.", getColumnForLog()));
					if (getSelectedIndex() == 0) {
						keyEvent.consume();
						logger.log(DEBUG, () -> sf("%s: UP key consumed.", getColumnForLog()));
					}
				} else if (keyCode == KeyEvent.VK_DOWN) {
					logger.log(TRACE, () -> sf("%s: Intercepted DOWN key.", getColumnForLog()));
					if (getSelectedIndex() == getModel().getSize() - 1) {
						keyEvent.consume();
						logger.log(DEBUG, () -> sf("%s: DOWN key consumed.", getColumnForLog()));
					}
				}
			}
		});
	}


	/////////////////////////////////////////////////////////////////////////
	//
	// Here's SwingSet scaffolding and nullItem maintenance.
	//

	/**
	 * By default, the combo is not editable; Glazed lists will change.
	 */
	@Override
	public void customInit()
	{
		setEditable(false);
	}

// TODO: Review this. GlazedLists is probably still calling it so need to detect those calls.
//	/**
//	 * SwingSet combo boxes works under the assumption that the only time a combo is editable is when GlazedLists
//	 * are in use.
//	 * <p>
//	 * Don't call this yourself.
//	 * {@inheritDoc }
//	 */
//	@Override
//	@Deprecated
//	public void setEditable(boolean _editable) {
//		if (_editable && getAutoComplete() == null) {
//			logger.warn(sf("%s: SwingSet requires non-editable combo boxes.", getColumnForLog()));
//		} else {
//			super.setEditable(_editable);
//		}
//	}

	/**
	 * Catch this to make some adjustments after a change in metadata;
	 * in particular checking for a change in nullability.
	 * {@inheritDoc }
	 */
	@Override
	public void metadataChange() {
		adjustForNullItem();
	}

	private static Object nullElement(Class<?> clazz)
	{
		return clazz == String.class ? "" : null;
	}

	/**
	 * Create a null item for the itemList.
	 * This default implementation uses an empty string for a String,
	 * and null for everything else.
	 * @param remodel context for the creation of the nullItem.
	 * @return the created null item.
	 */
	protected SSListItem createNullItem(Model.Remodel remodel) {
		@SuppressWarnings("unchecked") K m = (K) nullElement(getKeyType());
		@SuppressWarnings("unchecked") D o = (D) nullElement(getDisplayValueType());
		@SuppressWarnings("unchecked") D2 o2 = (D2) nullElement(getDisplayValue2Type());
		return remodel.createKeyDisplayValueItem(m, o, o2);
	}

	/**
	 * Return this ComboBox's nullItem.
	 * @return the nullItem
	 */
	public SSListItem getNullItem() {
		return nullItem;
	}

	/**
	 * A combobox used as a navigator has some restriction; for example,
	 * it can not have nullItem. Override this as needed.
	 * @return true if this ComboBox is a navigator
	 */
	protected boolean isComboBoxNavigator() {
		return false;
	}

	/**
	 * After this, make some adjustments.
	 * {@inheritDoc }
	 */
	@Override
	public void setAllowNull(boolean allowNull) {
		SSComponent.super.setAllowNull(allowNull);
		adjustForNullItem();
	}

	/**
	 * Override the default implementation to take combobox navigator
	 * into account. If navigator then always false, don't need to check
	 * database.
	 * @return true if combo box can have null value
	 */
	@Override
	public boolean getAllowNull() {
		return !isComboBoxNavigator()
				&& SSComponent.super.getAllowNull();
	}

	/**
	 * Add or remove the nullItem from the itemList if needed depending
	 * on {@code getAllowNull()} and the current existence of a nullItem.
	 * If logical null is selected before, keep it selected after.
	 */
	protected void adjustForNullItem() {
		// 2021-01-20_BP: Slight modification needed to avoid Beep in combo navigator on
		//   insert row as SSDBNavImpl calls setSelectionPending(true) and that will fail
		//   for the combo navigator without this tweak.
		//boolean wantNull = (getAllowNull() || selectionPending) && !isComboBoxNavigator();
		boolean wantNull = getAllowNull() || selectionPending;
		boolean hasNull = nullItem != null;
		
		if (wantNull == hasNull) {
			// things are set up as wanted
			return;
		}
		try (Model.Remodel remodel = keyVisual.getRemodel()) {
			// Need to add or remove the nullItem
			// Since selection is based on item, not index, we only need
			// to consider adjusting the selection if the current selection:
			// A) null/-1 and nullItem is being added then select nullItem/0
			// B) nullItem/0 and nullItem is being removed then select null/-1.
			//
			// The whole idea is that if null was selected before this
			// change, want it seleted after the change.
			//
			// If listeners are enabled, may exceptions about using null
			boolean selectNull = false;
			boolean selectNone = false;
			if (wantNull) {
				selectNull = super.getSelectedIndex() == -1;
				//nullItem = remodel.createComboItem(null, "", null);
				nullItem = createNullItem(remodel);
				remodel.add(0, nullItem);
			} else {
				selectNone = super.getSelectedItem() == nullItem;
				remodel.remove(nullItem);
				nullItem = null;
			}

			// Only manipulate listeners and selection if needed.
			// Note that if selected item was not null, then followin is not entered.
			if (selectNull || selectNone) {
				final ActionListener[] listeners = unregisterAllActionListeners(this);
				try {
					if (selectNull) {
						super.setSelectedItem(nullItem);
					}
					if (selectNone) {
						super.setSelectedItem(null);
					}
				} finally {
					registerAllActionListeners(this, listeners);
				}
			}
		}
	}
	
	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText().
	 */
	@SuppressWarnings({"BroadCatchBlock", "TooBroadCatch", "UseSpecificCatch"})
	private void updateComponent() {
		// TODO Modify this class similar to updateSSComponent() in SSFormattedTextField and only limit JDBC types accepted
		try {
			// If initialization is taking place then there won't be any
			// keys so don't try to update anything yet.
			if (!hasItems()) {
				return;
			}

			// Maybe insures blank in case of later exception.
			setSelectionPending(true);

			logger.log(DEBUG, ()->sf("%s: getBoundColumnText() - %s",
					getColumnForLog(), getBoundColumnText()));

			// https://github.com/bpangburn/swingset/issues/46
			K targetValue = getBoundColumnObject(getKeyType());
			
			logger.log(DEBUG, () -> sf("%s: targetValue - %s",  getColumnForLog(), targetValue));
			
			// Update component.
			setChosenKey(targetValue);
		} finally {
			cleanupMissingKeyVisuals();
		}
	}

	/** {@inheritDoc } */
	@Override
	public void cleanField()
	{
		setSelectionPending(true);
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
				protected ComboBox2Listener getSSComponentListener() {
					return new ComboBox2Listener();
				}
				
				/** {@inheritDoc } */
				@Override
				protected void addSSComponentListener(EventListener eventListener)
				{
					addActionListener((ActionListener) eventListener);
				}
				
				/** {@inheritDoc } */
				@Override
				protected void removeSSComponentListener(EventListener eventListener)
				{
					removeActionListener((ActionListener) eventListener);
				}
			};
		return hook;
	}

	//
	// unregisterAllActionListeners/registerAllActionListeners kudos glazed lists
	//
	/**
	 * A convenience method to unregister and return all {@link ActionListener}s
	 * currently installed on the given <code>comboBox</code>. This is the only
	 * technique we can rely on to prevent the <code>comboBox</code> from
	 * broadcasting {@link ActionEvent}s at inappropriate times.
	 * <p>
	 * This method is the logical inverse of {@link #registerAllActionListeners}.
	 * <p>
	 * Note that the ActionListener used for binding will be removed without
	 * passing through SSCommon.removeSSComponentListener()
	 * 
	 * @param comboBox combo box from which to remove listeners
	 * @return array of ActionListeners removed from combo box (for adding back later)
	 */
	// TODO: Consider passing in ComboBox2 and identifying ComboBox2Listener
	static ActionListener[] unregisterAllActionListeners(JComboBox<?> comboBox) {
		final ActionListener[] listeners = comboBox.getActionListeners();
		for (ActionListener listener : listeners) {
			comboBox.removeActionListener(listener);
		}

		return listeners;
	}

	/**
	 * A convenience method to register all of the given <code>listeners</code>
	 * with the given <code>comboBox</code>.
	 * <p>
	 * This method is the logical inverse of {@link #unregisterAllActionListeners}.
	 * <p>
	 * Note that the ActionListener used for binding will be removed without
	 * passing through SSCommon.addSSComponentListener()
	 * 
	 * @param comboBox combo box for which to add listeners
	 * @param listeners array of ActionListners to be 
	 */
	// TODO: Consider passing in ComboBox2 and identifying ComboBox2Listener
	static void registerAllActionListeners(JComboBox<?> comboBox, ActionListener[] listeners) {
		for (ActionListener listener : listeners) {
			comboBox.addActionListener(listener);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("%s{item=%s, %s}", getClass().getSimpleName(),
				getSelectedItem(), SSUtils.ssComponentToString(this));
	}
}
