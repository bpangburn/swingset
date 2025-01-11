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
 * copyright (C) 2024-2025, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset;

import static com.nqadmin.swingset.models.AbstractComboBoxListSwingModel.addEventLogging;
import static com.nqadmin.swingset.models.OptionMappingSwingModel.asOptionMappingSwingModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.EventListener;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.google.common.reflect.TypeToken;

import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.models.AbstractComboBoxListSwingModel;
import com.nqadmin.swingset.models.GlazedListsOptionMappingInfo;
import com.nqadmin.swingset.models.OptionMappingSwingModel;
import com.nqadmin.swingset.models.SSListItem;
import com.nqadmin.swingset.models.SSListItemFormat;
import com.nqadmin.swingset.navigate.NavigateActions;
import com.nqadmin.swingset.navigate.RowSetModificationEvent;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * OptionMapping base class for combo box that uses/handles
 * {@link #getAllowNull() getAllowNull()}/{@link #setAllowNull(boolean) setAllowNull(boolean)},
 * and tracks metadata changes,
 * for adding/removing nullItem to the item list.
 * ssCommon is defined in here. In addition, there are JComboBox models for
 * use with or without glazed lists; these models have static methods for
 * hooking things up to the JComboBox.
 * <p>
 * Classes that extend this class, should use {@link #adjustForNullItem() }
 * after clearing the itemList or when a change is made that might affect
 * whether or not a nullItem is required.
 * <p>
 * Initially no Option2.
 * @param <M> mapping type
 * @param <O> option type
 * @param <O2> option2 type
 * @since 4.0.0
 *
 */
@SuppressWarnings("serial")
public abstract class ComboBox2<M,O,O2>
		extends JComboBox<SSListItem> implements SSComponentInterface
{
	/** A convenience for variable declarations. Do not instantiate. */
	protected abstract class Model extends OptionMappingSwingModel<M,O,O2> { }

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

			M mapping = getSelectedMapping();
			dbChange(() -> setBoundColumnObject(mapping));
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
				// cleanupMissingMappingOptions();
				if (getSelectedItem() != nullItem && selectionPending) {
					setSelectionPending(false);
					//System.err.println("itemStateChanged true --> false");
				}
			}
		}
	}

	/**
	 * OptionMapping model that can be installed in a JComboBox; this model has methods
	 * for building and manipulating the item list.
	 * @param <M> mapping type
	 * @param <O> option type
	 * @param <O2> option2 type
	 */
	protected static class BaseModel<M,O,O2> extends OptionMappingSwingModel<M,O,O2>
	{

		/**
		 * Create model and install it in the specified JComboBox.
		 * 
		 * @param <M> mapping type
		 * @param <O> option type
		 * @param <O2> option2 type
		 * @param _jc install model into this
		 * @return OptionMapping model
		 */
		protected static <M,O,O2>BaseModel<M,O,O2> install(ComboBox2<M,O,O2> _jc) {
			BaseModel<M,O,O2> model = new BaseModel<>();
			AbstractComboBoxListSwingModel.install(_jc, model);

			return model;
		}

		/**
		 * Create a model.
		 */
		protected BaseModel() {
			// false means no Options2
			super(false);
		}
	}

	/**
	 * OptionMapping model for use with GlazedLists; this model has methods
	 * for building and manipulating the EventList and supports locking.
	 * @param <M> mapping type
	 * @param <O> option type
	 * @param <O2> option2 type
	 */
	protected static class BaseGlazedModel<M,O,O2> extends GlazedListsOptionMappingInfo<M,O,O2>
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
		 * @param <M> mapping type
		 * @param <O> option type
		 * @param <O2> option2 type
		 * @param _jc install auto completion into this
		 * @return OptionMapping model
		 */
		protected static <M,O,O2>BaseGlazedModel<M,O,O2> install(ComboBox2<M,O,O2> _jc) {
			BaseGlazedModel<M,O,O2> model = new BaseGlazedModel<>();
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
			// false means no Options2
			super(false, new BasicEventList<SSListItem>());
		}
	}

	/** {@inheritDoc } */
	@Override
	public void addUndoableChange(RowSetModificationEvent ev) throws SQLException
	{
		SSComponentInterface.super.addUndoableChange(ev);
		NavigateActions.newSlot(this);
	}

	/** {@inheritDoc } */
	@Override
	public void undoRedoUpdateObject(NavigateActions.UndoRedo cmd, Object value) throws SQLException
	{
		SSComponentInterface.super.undoRedoUpdateObject(cmd, value);
		SwingUtilities.invokeLater(() -> this.hidePopup());
	}

	/**
	 * @return the mapping index in an SSListItem
	 */
	public int getMappingFormatIndex() {
		return optionModel.getMappingListItemElemIndex();
	}

	/**
	 * @return the option index in an SSListItem
	 */
	public int getOptionFormatIndex() {
		return optionModel.getOptionListItemElemIndex();
	}

	/**
	 * @return the option2 index in an SSListItem
	 */
	public int getOption2FormatIndex() {
		return optionModel.getOption2ListItemElemIndex();
	}

	/**
	 * A list item formatter that displays the mapping if the option is null.
	 */
	@SuppressWarnings("serial")
	class ShowMappingIfNullOption extends SSListItemFormat {

		/**
		 * If null option, then show the mapping;
		 * otherwise do the default formatting.
		 * @param sb display value goes here
		 * @param elemIndex which option to format
		 * @param listItem combobox list item
		 */
		@Override
		protected void appendValue(StringBuffer sb, int elemIndex, SSListItem listItem) {
			if (getOptionFormatIndex() == elemIndex
					&& getElem(elemIndex, listItem) == null
					&& !Objects.equals(getNullItem(), listItem)) {
				Object key = getElem(getMappingFormatIndex(), listItem);
				sb.append(key != null ? key.toString() : null)
						.append(" - Option Not Found");
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
	 * {@link com.nqadmin.swingset.models.OptionMappingSwingModel.Remodel#clear() remodel.clear()},
	 * the nullItem must be set to null.</b>
	 * @see #createNullItem(com.nqadmin.swingset.models.OptionMappingSwingModel.Remodel)
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
	protected OptionMappingSwingModel<M,O,O2> optionModel;

	// The actual type of the mapping.
	private Class<M> mappingType;

	/**
	 * Return the actual type of the Mapping parameter.
	 * @return the type
	 */
	final public Class<M> getMappingType() {
		if (mappingType == null) {
			//@SuppressWarnings("unchecked")
			TypeToken<M> typeToken = new TypeToken<M>(getClass()) { };
			@SuppressWarnings("unchecked")
			Class<M> t = (Class<M>) typeToken.getType();
			mappingType = t;
		}
		return mappingType;
	}

	// The actual type of the option.
	private Class<O> optionType;

	/**
	 * Return the actual type of the Mapping parameter.
	 * @return the type
	 */
	final public Class<O> getOptionType() {
		if (optionType == null) {
			//@SuppressWarnings("unchecked")
			TypeToken<O> typeToken = new TypeToken<O>(getClass()) { };
			@SuppressWarnings("unchecked")
			Class<O> t = (Class<O>) typeToken.getType();
			optionType = t;
		}
		return optionType;
	}

	// The actual type of the option.
	private Class<O2> option2Type;

	/**
	 * Return the actual type of the Mapping parameter.
	 * @return the type
	 */
	final public Class<O2> getOption2Type() {
		if (option2Type == null) {
			//@SuppressWarnings("unchecked")
			TypeToken<O2> typeToken = new TypeToken<O2>(getClass()) { };
			@SuppressWarnings("unchecked")
			Class<O2> t = (Class<O2>) typeToken.getType();
			option2Type = t;
		}
		return option2Type;
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
	public void setModel(ComboBoxModel<SSListItem> _model) {
		addEventLogging(_model);
		OptionMappingSwingModel<?, ?, ?> model = asOptionMappingSwingModel(_model);
		optionModel = model instanceof BaseModel ? (BaseModel<M,O,O2>)model
				: model instanceof BaseGlazedModel ? (BaseGlazedModel<M,O,O2>)model
				: null;

		super.setModel(_model);
	}

	/**
	 * Get the support for the glazed lists installed in this combo box.
	 * 
	 * @return the support or null if GlazedLists not installed
	 */
	protected AutoCompleteSupport<SSListItem> getAutoComplete() {
		return optionModel instanceof BaseGlazedModel
				? ((BaseGlazedModel<M,O,O2>)optionModel).autoComplete
				: null;
	}

	/**
	 * Create SSBaseComboBox.
	 */
	public ComboBox2() {
		this(ModelType.SWING);
	}

	/**
	 * Create SSBaseComboBox.
	 * @param modelType whether to use SWING or GLAZED combo model
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public ComboBox2(ModelType modelType) {
		addItemListener(new ComboBox2ItemListener());
		finishSSCommon();

		try {
			getMappingType();
		} catch(ClassCastException ex) {
			throw new IllegalStateException(
					sf("Non generic subclass required, like '%s<...>(){}'",
							getClass().getSimpleName()),
					ex);
		}

		optionModel = switch(modelType) {
		case GLAZED -> BaseGlazedModel.install(this);
		case SWING -> BaseModel.install(this);
		};
		optionModel.setListItemFormat(new ShowMappingIfNullOption());
	}

	/**
	 * Set the format to use with this model. <br>
	 * TODO: consider deferring the actual change until row change
	 * 
	 * @param listItemFormat the format used with this model
	 */
	public void setListItemFormat(SSListItemFormat listItemFormat) {
		Object selectedItem = getSelectedItem();
		boolean keep = Objects.equals(getEditor().getItem(), selectedItem);
		optionModel.setListItemFormat(listItemFormat);
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
	public SSListItemFormat getListItemFormat() {
		return optionModel.getListItemFormat();
	}



	/////////////////////////////////////////////////////////////////////////
	//
	// Option/Mapping
	//

	/**
	 * Determine if there are option/mapping items 
	 * that have been added to this combo box.
	 * This is tricky because
	 * if getAllowNull is true then nullItem is in the item list and
	 * shouldn't be consider an item.
	 * 
	 * @return true if there are options/mappings
	 */
	public boolean hasItems() {
		return optionModel.getItemList().size() != (getAllowNull() ? 1 : 0);
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
	 * with {@literal [0-N)} mapping.
	 *
	 * @param options the list of options that you want to appear in the combo box.
	 */
	public void setOptions(List<O> options) {
		setOptions(options, null);
	}

	/**
	 * Sets the options to be displayed in the list box along with
	 * their corresponding mappings to database values. If {@code mappings}
	 * is null, then a zero to N-1 mapping is automatically established.
	 * 
	 * @param options  options to be displayed in the list box.
	 * @param mappings null or database values that correspond to the options, 1 to 1, in
	 *					the list box.
	 * @throws IllegalArgumentException if lists are not the same size.
	 */
	public void setOptions(List<O> options, List<M> mappings) {
		enumOption = null;
		setOptionsInternal(options, mappings);
	}

	/**
	 * Returns the items displayed in the combo box.
	 * <p>
	 * <b>When getAllowNull() is true, the first list item is null/""</b>
	 *
	 * @return returns the items displayed in the combo box
	 */
	public List<O> getOptions() {
		return optionModel.getOptions();
	}

	/**
	 * when true, mappings were autoGenerated
	 */
	private boolean autoGeneratedMapping;

	/**
	 * Determine if mappings are autoGenerated {@literal [0-N)}
	 * @return true if mappings are autoGenerated
	 */
	// TODO: public? Get rid of the field
	protected boolean isAutoGeneratedMapping() {
		return autoGeneratedMapping;
	}

	/**
	 * Set up the specified mappings/options.
	 * <p>
	 * If _mappings is null, then autogenerate the mappings;
	 * _mappings type must be Integer or Long.
	 * @param options
	 * @param _mappings
	 */
	protected void setOptionsInternal(List<O> options, List<M> _mappings) {
		Objects.requireNonNull(options);
		if (_mappings == null
				&& getMappingType() != Integer.class
				&& getMappingType() != Long.class)
			throw new IllegalArgumentException(
					"Auto generated mapping only avaialable for int/long mappings");
		if (_mappings != null && options.size() != _mappings.size())
			throw new IllegalArgumentException("Options and Mappings different length");
		try (Model.Remodel remodel = optionModel.getRemodel()) {

			remodel.clear();
			nullItem = null;

			// TODO first item is nullItem if getAllowNull()==true.
			//      Impacts mappings, getSelectedValue(), getSelectedIndex(), etc.

			adjustForNullItem();

			autoGeneratedMapping = false;
			List<M> mappings = _mappings;
			if (mappings == null) {
				// Provide a [0,N) mapping
				// TODO: in the future let it be implicit, don't create it?
				List<Object> autoMapping;
				if (getMappingType() == Integer.class)
					autoMapping = IntStream.range(0, options.size())
							.collect(ArrayList::new, List::add, List::addAll);
				else // must be Long
					autoMapping = LongStream.range(0, options.size())
							.collect(ArrayList::new, List::add, List::addAll);
				@SuppressWarnings("unchecked")
				List<M> xxx = (List<M>)autoMapping;
				mappings = xxx;
				autoGeneratedMapping = true;
			}

			List<O> opts = optionModel.getDisconnectedList(options);
			mappings = optionModel.getDisconnectedList(mappings);
			
			remodel.addAll(mappings, opts);
		}
	}

	/**
	 * Get the mappings currently in use.
	 * <p>
	 * <b>When getAllowNull() is true, the first list item is null/""</b>
	 * @return the mappings
	 */
	public List<M> getMappings() {
		return optionModel.getMappings();
	}

	/**
	 * Removes an item from the combobox's item list where the
	 * list item's mapping equals the param.
	 * <p>
	 * If more than one item is present in the combo for that value the first one is
	 * changed.
	 *
	 * @param mapping keymapping value for the item that should be removed
	 *
	 * @return returns true on successful deletion otherwise returns false.
	 */
	public boolean removeMapping(M mapping) {
		
		boolean result = false;

		try (Model.Remodel remodel = optionModel.getRemodel()) {
			// Get index of list item for mapping.
			int index = remodel.getMappings().indexOf(mapping);
			// Proceed if mapping was found.
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
	 * @param option item that should be displayed in the combobox
	 * @param mapping  mapping of _option, commonly a primary key
	 */
	public void addOption(O option, M mapping) {
		addOption(option, null, mapping);
	}
	
	/**
	 * Adds an item to the existing list of items in the combo box.
	 *
	 * @param option item that should be displayed in the combobox
	 * @param option2  second display item for combobox
	 * @param mapping  mapping of _option, commonly a primary key
	 */
	public void addOption(O option, O2 option2, M mapping) {
		try (Model.Remodel remodel = optionModel.getRemodel()) {
			final int index = remodel.getMappings().indexOf(mapping);
			if (index >= 0) {
				logger.log(WARNING, () -> sf("%s: Mapping of [%s] already exists. Creating duplicate Mapping with Option of '%s'.",
					getColumnForLog(), mapping, option));
			}
			remodel.add(mapping, option, option2);
		} catch (final Exception e) {
			logger.log(Level.ERROR, getColumnForLog() + ": Exception.", e);
		}
	}

	/**
	 * Update an option of an item in the combobox's item list based on a mapping
	 * value.
	 * <p>
	 * If more than one item is present in the combo for that mapping, only the
	 * first one is changed.
	 *
	 * @param mapping typically a primary key value corresponding to the displayed
	 *                 currentSelectedOption to be updated
	 * @param option  currentSelectedOption that should be updated in the combobox
	 *
	 * @return returns true if update is successful otherwise returns false.
	 */
	public boolean updateOption(M mapping, O option) {

		// 2021-02-28: @errael patched this method to deal with inconsistent
		// updating of combo editor. See https://github.com/bpangburn/swingset/issues/85
		
		boolean result = false;

		try (Model.Remodel remodel = optionModel.getRemodel()) {
			final int index = remodel.getMappings().indexOf(mapping);
			if (index >= 0) {
				boolean isSelectedItem = Objects.equals(mapping, getSelectedMapping());
				remodel.setOption(index, option);
				result = true;
				// Changing what's in the ComboEditor, which may be done indirectly when
				// modifying the current item, might change the currently selected item
				// when GlazedList is set to STRICT. So something is needed to insure
				// that the selected mapping before the change is the selected mapping
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
					// Select the modified item so the same mapping is selected.
					SSListItem item = remodel.get(index);
					setSelectedItem(item);
				}
// TODO Confirm that eventList is not reordered by GlazedLists code.
			}
		}

		return result;
	}

	/**
	 * Returns the mapping code corresponding to the currently selected item in the
	 * combobox. Commonly, this is an underlying database record
	 * primary key value corresponding to the currently selected item.
	 *
	 * @return returns the value associated with the selected item
	 * OR null if nothing is selected.
	 */
	public M getSelectedMapping() {
		logger.log(TRACE, () -> sf("%s: getSelectedMapping(), idx:map %d:%s.",
				getColumnForLog(), getSelectedIndex(), getSelectedItem()));

		M result = null;

		try (Model.Remodel remodel = optionModel.getRemodel()) {
			Object item = getSelectedItem();
			if (item instanceof SSListItem lItem) {
				result = remodel.getMapping(lItem);
			}
		}

		return result;
	}

	private final List<M> generatedMappingOptions = new ArrayList<>();
	private int addMissingMappingOption(Model.Remodel remodel, M mapping) {
		int index = -1;
		if(mapping != null) {
			if(missingOptionControl.contains(MissingOptionControl.MOC_ADD)) {
				remodel.add(mapping, null);
				index = remodel.getMappings().indexOf(mapping);
				logger.log(DEBUG, () -> "missingMappingOption added: " + mapping);
				generatedMappingOptions.add(mapping);
			}
		}
		return index;
	}

	/**
	 * If current selection does not have a null option,
	 * then remove any created missingMappingOptionss from list.
	 */
	private void cleanupMissingMappingOptions() {
		if (!SwingUtilities.isEventDispatchThread())
			throw new IllegalStateException("Must be EDT.");
		if(generatedMappingOptions.isEmpty()
				|| !missingOptionControl.contains(MissingOptionControl.MOC_CLEANUP))
			return;
		if(generatedMappingOptions.size() > 1)
			logger.log(WARNING, () -> "size: " + generatedMappingOptions.size());
		try (Model.Remodel remodel = optionModel.getRemodel()) {
			for (Iterator<M> it = generatedMappingOptions.iterator(); it.hasNext();) {
				M missingMappingOption = it.next();
				// Sequential records might have different missing options;
				// never remove a missing option for the current selected mapping.
				if(Objects.equals(missingMappingOption, getSelectedMapping()))
					continue;
				int index = remodel.getMappings().indexOf(missingMappingOption);
				if(index != -1) {
					SSListItem removed = remodel.remove(index);
					logger.log(DEBUG, () -> "missingMappingOption removed: " + removed);
				} else {
					logger.log(WARNING, () -> "not in combo: " + missingMappingOption);
				}
				it.remove(); // it's not in the combo list anymore
			}
		}

	}

	private final EnumSet<MissingOptionControl> missingOptionControl
			= EnumSet.allOf(MissingOptionControl.class);

	/**
	 * Flags to control handling of Mapping, database value,
	 * without a specified Option;
	 * see {@link #setMissingOptionControl(java.util.EnumSet) } for
	 * meaning of the flags.
	 */
	public enum MissingOptionControl {

		/**
		 * Autmatically generate list item entry
		 * when current database record, mapping, does not have an option.
		 */
		MOC_ADD,

		/**
		 * Remove any auto gen entry if current database record has an option.
		 */
		MOC_CLEANUP;
	}

	/**
	 * Set the flags that control how a Mapping, database value,
	 * without a specified option is handled.
	 * If {@code MOC_ADD} and a Mapping
	 * from the database is encountered and no Option was specified,
	 * a combobox list entry is created for the Mapping with null option;
	 * a custom message, see
 	 * {@link #setListItemFormat(com.nqadmin.swingset.models.SSListItemFormat)},
	 * may be setup for handling these entries for the combobox display.
	 * If {@code MOC_CLEANUP} then when the combobox navigates away
	 * from a record with a missing option, the automatically created
	 * list entry is removed.
	 * <p>
	 * Flags default to {@code MOC_ADD} and {@code MOC_CLEANUP}.
	 * 
	 * @param flags new value
	 * @return previous value
	 */
	public EnumSet<MissingOptionControl> setMissingOptionControl(
			EnumSet<MissingOptionControl> flags)
	{
		EnumSet<MissingOptionControl> prev = missingOptionControl.clone();
		missingOptionControl.clear();
		missingOptionControl.addAll(flags);
		return prev;
	}

	/**
	 * Sets the selected ComboBox item according to the specified mapping/key.
	 * The selectedItem is set to nullItem or null if mapping not found.
	 * <p>
	 * If called from updateSSComponent() from a RowSet change then the Component
	 * listener should already be turned off. Otherwise we want it on so the
	 * ultimate call to setSelectedItem() will trigger an update the to RowSet.
	 *
	 * @param mapping key of item value to assign to combobox,
	 *                 which may or may not correlate to the combobox index
	 */
	public void setSelectedMapping(M mapping) {
		
		logger.log(DEBUG, sf("%s: current value: %s, new value: %s.",
				getColumnForLog(), getSelectedMapping(), mapping ));

		try (Model.Remodel remodel = optionModel.getRemodel()) {
			if (!hasItems()) {
				logger.log(WARNING, sf("%s: combobox is empty", getColumnForLog()));
				// Doesn't have items, that doesn't mean that the list is empty.
				// Make sure the appropriate null is selected.
				setSelectedItem(nullItem);
				return;
			}
			
			// only need to proceed if there is a change.
			if (Objects.equals(mapping, getSelectedMapping())) {
				return;
			}
			
			int index = remodel.getMappings().indexOf(mapping);
			SSListItem item;
			if (index != -1) {
				item = remodel.get(index);
			} else if((index = addMissingMappingOption(remodel, mapping)) != -1) {
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
				// for setSelectedMapping() and we call setSelectedItem(nullItem).
				// This is OK so long as the component listener used for binding
				// is removed/disabled because the rowset will not get the null value.
				// Later when the combo is requeried, the component will try to load
				// the current column value from the rowset and this time
				// setSelectedMapping() should succeed.
				if (SSUtils.isSSComponentListenerAddedDebug(this)) {
					logger.log(Level.ERROR, () -> sf(
							"%s: No mapping for %s in combobox, setSelectedItem(null)",
							getColumnForLog(), mapping));
				}
			}
			setSelectedItem(item);

			logger.log(TRACE, () -> sf("%s: eventList - [%s].",
					getColumnForLog(), remodel.getItemList().toString()));
			logger.log(TRACE, () -> sf("%s: options - [%s].",
					getColumnForLog(), remodel.getOptions().toString()));
			logger.log(TRACE, () -> sf("%s: mappings - [%s].",
					getColumnForLog(), remodel.getMappings().toString()));
		}
	}

	/**
	 * Returns the Option corresponding to the currently selected item in the
	 * combobox.
	 *
	 * @return returns the Option object associated with the selected item
	 * OR null if nothing is selected.
	 */
	public O getSelectedOption() {
		O result = null;

		try (Model.Remodel remodel = optionModel.getRemodel()) {
			Object item = getSelectedItem();
			if (item instanceof SSListItem lItem) {
				result = remodel.getOption(lItem);
			}
		}

		return result;
	}

	/**
	 * Finds the listItem having option that matches the specified option
	 * and make it the selected listItem. If no matching item is found the _option
	 * is used for {@link #setSelectedItem(java.lang.Object) setSelectedItem(option)}.
	 *
	 * @param option option value of list item
	 */
	public void setSelectedOption(O option) {

		try (Model.Remodel remodel = optionModel.getRemodel()) {
			if(!hasItems()) {
				logger.log(WARNING, sf("%s: combobox is empty", getColumnForLog()));
				// Even if combo is empty, stick option in editor. Do not return;
			}
			
			Object tItem = getSelectedItem();
			// Extract the option from the selected list item.
			// If not an SSListItem, it's editable, use it as current option.
			Object currentSelectedOption = tItem instanceof SSListItem
					? remodel.getOption((SSListItem)tItem)
					: tItem;
			
			// only need to proceed if there is a change.
			if (Objects.equals(option, currentSelectedOption)) {
				return;
			}
			
			// find the first matching option in the list
			final int index = remodel.getOptions().indexOf(option);
			
			Object item;
			if (index != -1) {
				item = remodel.get(index);
			} else {
				// Didn't find it in the list, so just use it as is.
				item = option != null ? option : nullItem;
				logger.log(WARNING, () -> sf("%s: option %s not in combobox, do setSelectedItem.", getColumnForLog(), option));
			}
			
			setSelectedItem(item);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// Using an enum as an option/mapping.
	//

	//
	// TODO: Study use case for enums
	// TODO: When an enum is specified, should there be a way to
	//		specify a mapping as well? Two possibilities, the first
	//		is like the general pattern, mapping should have the
	//		same number of elements as there are enum values.
	//		The second could also be provided as setOptions([], Function)
	//		 1) setOptions(enum, mapping)
	//		 2) setOptions(enum, Function<enum, Object>)
	//			For example
	//				setOptions(ComboEnum.class, (e) -> e.someMethod())
	//				setOptions(ComboEnum.class, (e) -> someMap.get(e))

	private Class<?> enumOption;

	/**
	 * Sets the options to be displayed in the combo box based on
	 * the enum class' value's toString().Generate a {@literal [0-N)} mapping.
	 * <p>
	 * Convenience method, throws if option type/class is not String.
	 *
	 * @param <T> inferred enum type
	 * @param enumOption enum class with values to display
	 */
	public <T extends Enum<T>> void setOptions(Class<T> enumOption) {

		if (getOptionType() != String.class)
			throw new IllegalArgumentException(
					"Option type must be String for Enum options");

		this.enumOption = enumOption;
		List<String> l01 = Stream.of(enumOption.getEnumConstants())
				.map(e -> e.toString()).collect(Collectors.toList());

		@SuppressWarnings("unchecked")
		List<O> l02 = (List<O>)l01;
		setOptionsInternal(l02, null);
	}

	/**
	 * Get the enum class currently displayed by this combo box.
	 * If combo box is not displaying an enum, then reutrn null.
	 * @return the enum class
	 */
	// TODO: public? Get rid of the field
	protected Class<?>getEnumOption() {
		return enumOption;
	}
	
	/**
	 * Finds the listItem that matches the specified enum and make it the selected
	 * listItem.
	 *
	 * @param option select list item for this
	 * @throws ClassCastException if _option is wrong enum type
	 */
	public void setSelectedEnum(Enum<?> option) {
		if (enumOption == null) {
			throw new IllegalStateException("SSComboBox values not an enum.");
		}
		Objects.requireNonNull(option, "Enum to be selected cannnot be null.");

		// Verify the enum of of the correct type.
		enumOption.cast(option);
		// This could be performance optimized since itemList index == ordinal()
		// within the current constraints of mapping == ordinal().
		// But the whole nullItem comes into play to affect the index.
		setSelectedMapping(getMappingType().cast(option.ordinal()));
	}
	
	/**
	 * Return the selected enum.
	 * 
	 * @return selected enum.
	 * @throws IllegalStateException if not an enum.
	 */
	public Enum<?> getSelectedEnum() {
		if (enumOption == null)
			throw new IllegalStateException("SSComboBox values not an enum");
		M mapping = getSelectedMapping();
		if (mapping == null)
			return null;
		int enumIdx = ((Number)mapping).intValue();
		return (Enum<?>)enumOption.getEnumConstants()[enumIdx];
	}

	
	/////////////////////////////////////////////////////////////////////////
	//
	// Deal with non-standard GlazedList UP/DOWN arrow handling
	//
	
	/**
	 * When dealing with GlazedLists (1.11) restore expected combo behavior.
	 *
	 * Per GL JavaDoc:
	 * https://javadoc.io/doc/com.glazedlists/glazedlists/latest/ca/odell/glazedlists/swing/AutoCompleteSupport.html
	 *<p>
	 *  4. typing the up arrow key when the popup is visible and the selected element is the first element causes the autocompletion to be cleared and the popup's selection to be removed.
	 *  6. typing the down arrow key when the popup is visible and the selected element is the last element causes the autocompletion to be cleared and the popup's selection to be removed
	 *<p>
	 * We want to restore the normal JComboBox behavior of not going past the first or last item. This would be the ideal case, matching JComboBox.
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
		@SuppressWarnings("unchecked") M m = (M) nullElement(getMappingType());
		@SuppressWarnings("unchecked") O o = (O) nullElement(getOptionType());
		@SuppressWarnings("unchecked") O2 o2 = (O2) nullElement(getOption2Type());
		return remodel.createOptionMappingItem(m, o, o2);
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
		SSComponentInterface.super.setAllowNull(allowNull);
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
				&& SSComponentInterface.super.getAllowNull();
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
		try (Model.Remodel remodel = optionModel.getRemodel()) {
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
			// mappings so don't try to update anything yet.
			if (!hasItems()) {
				return;
			}

			// Maybe insures blank in case of later exception.
			setSelectionPending(true);

			logger.log(DEBUG, ()->sf("%s: getBoundColumnText() - %s",
					getColumnForLog(), getBoundColumnText()));

			// https://github.com/bpangburn/swingset/issues/46
			M targetValue = getBoundColumnObject(getMappingType());
			
			logger.log(DEBUG, () -> sf("%s: targetValue - %s",  getColumnForLog(), targetValue));
			
			// Update component.
			setSelectedMapping(targetValue);
		} finally {
			cleanupMissingMappingOptions();
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
