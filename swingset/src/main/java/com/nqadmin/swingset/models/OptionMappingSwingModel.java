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

package com.nqadmin.swingset.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.ListModel;


// OptionMappingSwingModel.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * This class manages a GlazedLists' EventList and the associated list items.
 * The SSListItem elements are {@literal {mapping, option, option2}};
 * where mapping is the primary key, option has display info,
 * option2 has additional display info.
 * option2 is optional, see {@link #setOption2Enabled(boolean) }
 * and the constructors.
 * There are ways to create, modify and examine the SSListItem.
 * Individual read-only lists for mapping, option, option2 are available.
 * <p>
 * Use {@link #getRemodel} for a multi-thread safe Object 
 * for modifying and examining the itemList and list items.
 * The remodel lock uses the EventList lock.
 * 
 * @param <M> mapping type; mapping is typically primary key
 * @param <O> option type; option provides display string
 * @param <O2>  option2 type; if present, supplementary display string
 * 
 * @see AbstractComboBoxListSwingModel
 * AbstractComboBoxListSwingModel for general description of using this class
 * @see <a href="https://javadoc.io/doc/com.glazedlists/glazedlists/latest/ca/odell/glazedlists/swing/AutoCompleteSupport.html" target="_blank" rel="noopener noreferrer">GlazedLists AutoCompletion javadoc</a>
 * 
 * @since 4.0.0
 */
public class OptionMappingSwingModel<M,O,O2> extends AbstractComboBoxListSwingModel {

	/** index of option in SSListItem */
	// Option IS FIRST. THIS IS THE DEFAULT FOR SSListItemFormat
	// In addition, only Option is required.
	// LOOK AT createOptionItem BEFORE THINKING ABOUT CHANGING THESE DEFINES
	protected final static int OPT_IDX = 0; // 
	/** index of primary key in SSListItem */
	protected final static int KEY_IDX = 1;
	/** index of option2 in SSListItem */
	protected final static int OPT2_IDX = 2;

	/** up to 3 slices created, OPT, KEY, OPT2 */
	private final List<?>[] slices = new List<?>[3];
	/** when true, there can be options2 */
	private boolean option2Enabled = false;

	/**
	 * Given some model, if possible return the model
	 * cast as a OptionMappingSwingModel.
	 * @param _model model to check
	 * @return OptionMappingSwingModel or null
	 */
	public static OptionMappingSwingModel<?,?,?> asOptionMappingSwingModel(ListModel<SSListItem> _model) {

		if (_model instanceof OptionMappingSwingModel) {
			return (OptionMappingSwingModel<?,?,?>) _model;
		}
		if (_model instanceof ComboBoxListSwingModel) {
			Object model = ((ComboBoxListSwingModel)_model).getComboBoxListSwingModel();
			if (model instanceof OptionMappingSwingModel) {
				return (OptionMappingSwingModel<?,?,?>) model;
			}
		}
		return null;
	}

	/**
	 * Create an empty OptionMappingSwingModel with no options2.
	 */
	protected OptionMappingSwingModel() {
		this(false);
	}

	/**
	 * Create an empty OptionMappingSwingModel .
	 * 
	 * @param _option2Enabled true says to provide an options2 field in SSListItem
	 */
	public OptionMappingSwingModel(boolean _option2Enabled) {
		this(_option2Enabled, null);
	}


	/**
	 * Create an empty OptionMappingSwingModel.
	 * @param _option2Enabled true says to provide an options2 field in SSListItem
	 * @param _itemList model backing store
	 */
	public OptionMappingSwingModel(boolean _option2Enabled, List<SSListItem> _itemList) {
		super(_option2Enabled ? 3 : 2, _itemList);
		option2Enabled = _option2Enabled;
	}

	/**
	 * create List slices lazily
	 * @param sliceIndex elem index in SSListItem
	 * @return the slice for the specified elem
	 */
	private <T>List<T> getSlice(int sliceIndex) {
		@SuppressWarnings("unchecked")
		List<T> slice = (List<T>) slices[sliceIndex];
		if (slice == null) {
			slice = createElementSlice(sliceIndex);
			slices[sliceIndex] = slice;
		}

		return slice;
	}
	
	/**
	 * Note when getAllowNull is true, the first list item is null/""
	 * @return unmodifiable list of mappings
	 */
	public List<M> getMappings() {
		return getSlice(KEY_IDX);
	}
	
	/**
	 * Note when getAllowNull is true, the first list item is null/""
	 * @return unmodifiable list of options
	 */
	public List<O> getOptions() {
		return getSlice(OPT_IDX);
	}
	
	/**
	 * Note when getAllowNull is true, the first list item is null/""
	 * @return unmodifiable list of options2
	 */
	public List<O2> getOptions2() {
		return getSlice(OPT2_IDX);
	}
	
	/**
	 * Change whether the layout of an SSListItem managed by this class
	 * contains an option2. An exception is thrown if the item list
	 * is not empty. An existing options2 list {@link #getOptions2}
	 * is invalidated or validated accordingly.
	 * @param _option2Enabled true if list item shall contain option2
	 */
	public void setOption2Enabled(boolean _option2Enabled) {
		if(option2Enabled == _option2Enabled) {
			return;
		}
		if (!getItemList().isEmpty()) {
			throw new IllegalStateException("Only change option2enabled when empty");
		}
		option2Enabled = _option2Enabled;
		// normally 2 elements in ListItem {key,option}, option2 add 3rd.
		setItemNumElems(_option2Enabled ? 3 : 2);
	}

	/**
	 * convenient verification that option2 is enabled
	 */
	private void usingOption2() {
		if (!option2Enabled) {
			throw new IllegalStateException("option2enabled is false");
		}
	}

	/**
	 * Consistency checks can be placed in here. It is called
	 * from every method in remodel. Keep it cheap for production code.
	 */
	@Override
	protected void checkState() {
		// consistency check can be placed here
	}

	/**
	 * Create a list item with the specified contents.
	 * @param _mapping mapping value (primary key)
	 * @param _option display info derived from this
	 * @param _option2 additional display info
	 * @return the new list item
	 */
	protected SSListItem createOptionItem(M _mapping, O _option, O2 _option2) { 
		// ALL ListItem CREATION GOES THROUGH HERE
		// THE DEFINES FOR KEY_INDEX, OPT_INDEX ARE RELEVANT TO THE ORDER.
		// _option MUST GO FIRST
		return option2Enabled ? createListItem(_option, _mapping, _option2)
				: createListItem(_option, _mapping);
		//return option2Enabled ? createListItem(_mapping, _option, _option2)
		//		: createListItem(_mapping, _option);
	}

	/**
	 * Create a list of list items with the specified contents;
	 * the lists must be the same size.
	 * @param _mappings list of mapping values (primary key)
	 * @param _options display info derived from this
	 * @param _options2 additional display info
	 * @return the new list item
	 */
	protected List<SSListItem> createOptionItems(List<M> _mappings, List<O> _options, List<O2>_options2) {
		Objects.requireNonNull(_mappings);
		Objects.requireNonNull(_options);
		if (option2Enabled) {
			Objects.requireNonNull(_options2);
		} else {
			if (_options2 != null) {
				throw new IllegalArgumentException("options2 provided, but not enabled");
			}
		}
		int n = _mappings.size();
		if (_options.size() != n || _options2 != null && _options2.size() != n) {
			throw new IllegalArgumentException("Lists must be the same size");
		}
		List<SSListItem> optionItems = new ArrayList<>();
		if(option2Enabled && _options2 != null) {
			for (int i = 0; i < _mappings.size(); i++) {
				optionItems.add(createOptionItem(_mappings.get(i), _options.get(i),
						_options2.get(i)));
			}
		} else {
			for (int i = 0; i < _mappings.size(); i++) {
				optionItems.add(createOptionItem(_mappings.get(i), _options.get(i), null));
			}
		}
		return optionItems;
	}

	/**
	 * @return true if a list item contains option2
	 */
	public boolean isOption2Enabled() {
		return option2Enabled;
	}

	/**
	 * Get the opaque index for the mapping element in a SSListItem.
	 * Useful for use with {@link SSListItemFormat} or setElem.
	 * @return the index of mapping
	 */
	public int getMappingListItemElemIndex() {
		return KEY_IDX;
	}

	/**
	 * Get the opaque index for the option element in a SSListItem.
	 * Useful for use with {@link SSListItemFormat}.
	 * @return the index of option
	 */
	public int getOptionListItemElemIndex() {
		return OPT_IDX;
	}

	/**
	 * Get the opaque index for the option2 element in a SSListItem.
	 * Useful for use with {@link SSListItemFormat}.
	 * @return the index of option2
	 */
	public int getOption2ListItemElemIndex() {
		return OPT2_IDX;
	}

	/**
	 * Get object to make changes.
	 * @return Remodel
	 * @see Remodel
	 */
	@Override
	public Remodel getRemodel() {
		// default is no locking, re-use the model.
		if (remodel == null) {
			remodel = new Remodel();
		}
		return remodel;
	}
	private Remodel remodel;
	
	// no locking by default
	/** {@inheritDoc} */
	@Override protected void remodelTakeWriteLock() { }
	/** {@inheritDoc} */
	@Override protected void remodelReleaseWriteLock(AbstractComboBoxListSwingModel.Remodel _remodel) { }
	
	/** Methods for inspecting and modifying list info */
	public class Remodel extends AbstractComboBoxListSwingModel.Remodel {
		
		/**
		 * Return an unmodifiable list of mappings.
		 * <p>
		 * <b>When getAllowNull() is true, the first list item is null/""</b>
		 * @return list of mappings
		 */
		public List<M> getMappings() {
			verifyOpened();
			return OptionMappingSwingModel.this.getMappings();
		}
		
		/**
		 * Return an unmodifiable list of option.
		 * <p>
		 * <b>When getAllowNull() is true, the first list item is null/""</b>
		 * @return list of options
		 */
		public List<O> getOptions() {
			verifyOpened();
			return OptionMappingSwingModel.this.getOptions();
		}
		
		/**
		 * Return an unmodifiable list of option2.
		 * <p>
		 * <b>When getAllowNull() is true, the first list item is null/""</b>
		 * @return list of options2
		 */
		public List<O2> getOptions2() {
			verifyOpened();
			return OptionMappingSwingModel.this.getOptions2();
		}
		
		/**
		 * Extract the mapping from the list item.
		 * @param _eventListItem list item from which mapping is extracted
		 * @return the mapping
		 */
		@SuppressWarnings("unchecked")
		public M getMapping(SSListItem _eventListItem) {
			return (M)super.getElem(_eventListItem, KEY_IDX);
		}
		
		/**
		 * Extract the option from the list item.
		 * @param _eventListItem list item from which option is extracted
		 * @return the option
		 */
		@SuppressWarnings("unchecked")
		public O getOption(SSListItem _eventListItem) {
			return (O)super.getElem(_eventListItem, OPT_IDX);
		}
		
		/**
		 * Extract the option from the list item.
		 * @param _eventListItem list item from which option is extracted
		 * @return the option
		 */
		@SuppressWarnings("unchecked")
		public O2 getOption2(SSListItem _eventListItem) {
			usingOption2();
			return (O2)super.getElem(_eventListItem, OPT2_IDX);
		}

		/**
		 * Extract the mapping from the list item
		 * at the specified item list index.
		 * Convenience method for {@code getMapping(getEventList().get(_index))}
		 * @param _index an item list index
		 * @return the option
		 */
		@SuppressWarnings("unchecked")
		public M getMapping(int _index) {
			return (M)super.getElem(_index, KEY_IDX);
		}
		
		/**
		 * Set the mapping element of the list item
		 * at the specified item list index.
		 * @param _index of list item
		 * @param _mapping put this into list item
		 * @return previous contents; the replaced element.
		 */
		@SuppressWarnings("unchecked")
		public O setMapping(int _index, O _mapping) {
			return (O) super.setElem(_index, OPT_IDX, _mapping);
		}
		
		/**
		 * Set the option element of the list item
		 * at the specified item list index.
		 * @param _index of list item
		 * @param _option put this into list item
		 * @return previous contents; the replaced element.
		 */
		@SuppressWarnings("unchecked")
		public O setOption(int _index, O _option) {
			return (O) super.setElem(_index, OPT_IDX, _option);
		}

		/**
		 * Set the option element of the list item
		 * at the specified item list index.
		 * @param _index of list item
		 * @param _option2 put this into list item
		 * @return previous contents; the replaced element.
		 */
		@SuppressWarnings("unchecked")
		public O2 setOption2(int _index, O2 _option2) {
			usingOption2();
			return (O2) super.setElem(_index, OPT2_IDX, _option2);
		}
		
		/**
		 * Add list items with the specified contents.
		 * The input lists shall be the same size.
		 * @param _mappings list of mapping values (primary key)
		 * @param _options list of display info 
		 * @return the new list item
		 */
		public boolean addAll(List<M> _mappings, List<O> _options) {
			return addAll(_mappings, _options, null);
		}

		/**
		 * Add list items with the specified contents.
		 * The input lists shall be the same size.
		 * @param _mappings list of mapping values (primary key)
		 * @param _options display info derived from this
		 * @param _options2 additional display info
		 * @return true if the list changed
		 */
		public boolean addAll(List<M> _mappings, List<O> _options, List<O2> _options2) {
			if (_options2 != null) {
				usingOption2();
			}
			List<SSListItem> list = OptionMappingSwingModel.this.createOptionItems(_mappings, _options, _options2);
			return super.addAll(list);
			//isModifiedLength = true;
		}

		/**
		 * Create an SSListItem with the specified contents
		 * and add it to the item list.
		 * The input lists shall be the same size.
		 * @param _mapping list of mapping values mapping value (primary key)
		 * @param _option display info derived from this
		 * @return the new list item
		 */
		public boolean add(M _mapping, O _option) {
			return add(_mapping, _option, null);
		}

		/**
		 * Create an SSListItem with the specified contents
		 * and add it to the item list.
		 * The input lists shall be the same size.
		 * @param _mapping list of mapping values mapping value (primary key)
		 * @param _option display info derived from this
		 * @param _option2 additional display info
		 * @return the new list item
		 */
		public boolean add(M _mapping, O _option, O2 _option2) {
			if (_option2 != null) {
				usingOption2();
			}
			return super.add(createOptionMappingItem(_mapping, _option, _option2));
			//itemList.add(createOptionItem(_mapping, _option, _option2));
			//isModifiedLength = true;
			//return true;
		}
		
		/**
		 * Create a list item with the specified contents.
		 * @param _mapping mapping value (primary key)
		 * @param _option display info derived from this
		 * @param _option2 additional display info
		 * @return the new list item
		 */
		public SSListItem createOptionMappingItem(M _mapping, O _option, O2 _option2) {
			return OptionMappingSwingModel.this.createOptionItem(_mapping, _option, _option2);
		}
	}
}
