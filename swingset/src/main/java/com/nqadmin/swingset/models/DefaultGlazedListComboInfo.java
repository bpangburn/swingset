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

package com.nqadmin.swingset.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.odell.glazedlists.EventList;

// DefaultGlazedListComboInfo.java
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
 * @param <O2>  option2 type; if present, suplementary display string
 * 
 * @see SSAbstractListInfo for general description of using this class
 * @see <a target="_top" href="https://javadoc.io/doc/com.glazedlists/glazedlists/latest/ca/odell/glazedlists/swing/AutoCompleteSupport.html">GlazedLists AutoCompletion javadoc</a>
 * 
 * @since 4.0.0
 */
public class DefaultGlazedListComboInfo<M,O,O2> extends SSAbstractGlazedListComboInfo {

	/** index of primary key in SSListItem */
	protected static int KEY_IDX = 0;
	/** index of option in SSListItem */
	protected static int OPT_IDX = 1;
	/** index of option2 in SSListItem */
	protected static int OPT2_IDX = 2;

	/** read only */
	private final List<M> mappings; // typically a primary key
	/** read only */
	private final List<O> options;
	/** read only */
	private final List<O2> options2; // may be null, depend option2Enabled
	/** when true, there can be options2 */
	private boolean option2Enabled = false;
	
	/**
	 * Create an empty ComboInfo.
	 * @param _eventList which is installed into AutoCompleteSupport
	 * @param _option2Enabled true says to provide an options2 field in SSListItem
	 */
	@SuppressWarnings("unchecked")
	public DefaultGlazedListComboInfo(EventList<SSListItem> _eventList, boolean _option2Enabled) {
		super(_option2Enabled ? 3 : 2, _eventList);
		option2Enabled = _option2Enabled;
		mappings = (List<M>) createElementSlice(0);
		options = (List<O>) createElementSlice(1);
		options2 = (List<O2>) createElementSlice(2);
	}

	/**
	 * Create a ComboInfo with the specified contents.
	 * @param _eventList which is installed into AutoCompleteSupport
	 * @param _option2Enabled true says to provide an options2 field in SSListItem
	 * @param _mappings initial mappings
	 * @param _options initial options
	 * @param _options2  initial options2
	 */
	public DefaultGlazedListComboInfo(EventList<SSListItem> _eventList, boolean _option2Enabled,
			List<M>_mappings, List<O>_options, List<O2>_options2) {
		this(_eventList, _option2Enabled);
		addAll(_mappings, _options, _options2);
	}
	
	/**
	 * @return unmodifiable list of mappings
	 */
	public List<M> getMappings() {
		return mappings;
	}
	
	/**
	 * @return unmodifiable list of options
	 */
	public List<O> getOptions() {
		return options;
	}
	
	/**
	 * @return unmodifiable list of options2
	 */
	public List<O2> getOptions2() {
		return options2;
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
		if (!mappings.isEmpty()) {
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
	protected SSListItem createComboItem(M _mapping, O _option, O2 _option2) {
		return option2Enabled ? createListItem(_mapping, _option, _option2)
				: createListItem(_mapping, _option);
	}

	/**
	 * Create a list of list items with the specified contents.
	 * @param _mappings list of mapping values (primary key)
	 * @param _options display info derived from this
	 * @param _options2 additional display info
	 * @return the new list item
	 */
	protected List<SSListItem> createComboItems(List<M> _mappings, List<O> _options, List<O2> _options2) {
		List<SSListItem> comboItems = new ArrayList<>();
		for (int i = 0; i < _mappings.size(); i++) {
			comboItems.add(createComboItem(_mappings.get(i), _options.get(i),
					option2Enabled ? _options2.get(i) : null));
		}
		return comboItems;
	}

	/** make sure sizes match up */
	private void addAll(List<M> _mappings, List<O> _options, List<O2>_options2) {
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
		List<SSListItem> newItems = createComboItems(_mappings, _options, _options2);
		itemList.addAll(newItems);
	}

	/**
	 * @return true if a list item contains option2
	 */
	public boolean isOption2Enabled() {
		return option2Enabled;
	}

	/**
	 * Extract the mapping from the list item.
	 * @param _eventListItem list item from which mapping is extracted
	 * @return the mapping
	 */
	@SuppressWarnings("unchecked")
	public M getMapping(SSListItem _eventListItem) {
		return (M) getElem(_eventListItem, KEY_IDX);
	}
	
	/**
	 * Extract the option from the list item.
	 * @param _eventListItem list item from which option is extracted
	 * @return the option
	 */
	@SuppressWarnings("unchecked")
	public O getOption(SSListItem _eventListItem) {
		return (O) getElem(_eventListItem, OPT_IDX);
	}
	
	/**
	 * Extract the option from the list item.
	 * @param _eventListItem list item from which option is extracted
	 * @return the option
	 */
	@SuppressWarnings("unchecked")
	public O2 getOption2(SSListItem _eventListItem) {
		usingOption2();
		return (O2) getElem(_eventListItem, OPT2_IDX);
	}

	/**
	 * Get the opaque index for the option element in a SSListeItem.
	 * Useful for use with {@link SSListItemFormat}.
	 * @return the index of option
	 */
	public int getOptionListItemElemIndex() {
		return OPT_IDX;
	}

	/**
	 * Get the opaque index for the option2 element in a SSListeItem.
	 * Useful for use with {@link SSListItemFormat}.
	 * @return the index of option2
	 */
	public int getOption2ListItemElemIndex() {
		return OPT2_IDX;
	}

	public Remodel getRemodel() {
		return new Remodel();
	}

	/** Methods for inspecting and modifying list info */
	public class Remodel extends SSAbstractGlazedListComboInfo.Remodel {

		/**
		 * Convenience method.
		 * @return the EventList being worked on
		 */
		public EventList<SSListItem> getEventList() {
			verifyOpened();
			return (EventList<SSListItem>) itemList;
		}
		
		/**
		 * Return an unmodifiable list of mappings.
		 * @return list of mappings
		 */
		public List<M> getMappings() {
			verifyOpened();
			return DefaultGlazedListComboInfo.this.getMappings();
		}
		
		/**
		 * Return an unmodifiable list of option.
		 * @return list of options
		 */
		public List<O> getOptions() {
			verifyOpened();
			return DefaultGlazedListComboInfo.this.getOptions();
		}
		
		/**
		 * Return an unmodifiable list of option2.
		 * @return list of options2
		 */
		public List<O2> getOptions2() {
			verifyOpened();
			return DefaultGlazedListComboInfo.this.getOptions2();
		}
		
		/**
		 * Extract the mapping from the list item.
		 * @param _eventListItem list item from which mapping is extracted
		 * @return the mapping
		 */
		public M getMapping(SSListItem _eventListItem) {
			verifyOpened();
			return DefaultGlazedListComboInfo.this.getMapping(_eventListItem);
		}
		
		/**
		 * Extract the option from the list item.
		 * @param _eventListItem list item from which option is extracted
		 * @return the option
		 */
		public O getOption(SSListItem _eventListItem) {
			verifyOpened();
			return DefaultGlazedListComboInfo.this.getOption(_eventListItem);
		}
		
		/**
		 * Extract the option from the list item.
		 * @param _eventListItem list item from which option is extracted
		 * @return the option
		 */
		public O2 getOption2(SSListItem _eventListItem) {
			verifyOpened();
			usingOption2();
			return DefaultGlazedListComboInfo.this.getOption2(_eventListItem);
		}

		/**
		 * Extract the mapping from the list item
		 * at the specified item list index.
		 * Convenience method for {@code getMapping(getEventList().get(_index))}
		 * @param _index an item list index
		 * @return the option
		 */
		public M getMapping(int _index) {
			verifyOpened();
			return getMapping(itemList.get(_index));
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
			verifyOpened();
			SSListItem listItem = itemList.get(_index);
			return (O) setElem(listItem, OPT2_IDX, _option);
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
			verifyOpened();
			usingOption2();
			SSListItem listItem = itemList.get(_index);
			return (O2) setElem(listItem, OPT2_IDX, _option2);
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
		 * @return the new list item
		 */
		public boolean addAll(List<M> _mappings, List<O> _options, List<O2> _options2) {
			verifyOpened();
			usingOption2();
			DefaultGlazedListComboInfo.this.addAll(_mappings, _options, _options2);
			//isModifiedLength = true;
			return !_mappings.isEmpty();
		}

		/**
		 * Create an SSListeItem with the specified contents
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
		 * Create an SSListeItem with the specified contents
		 * and add it to the item list.
		 * The input lists shall be the same size.
		 * @param _mapping list of mapping values mapping value (primary key)
		 * @param _option display info derived from this
		 * @param _option2 additional display info
		 * @return the new list item
		 */
		public boolean add(M _mapping, O _option, O2 _option2) {
			verifyOpened();
			if (_option2 != null) {
				usingOption2();
			}
			itemList.add(createComboItem(_mapping, _option, _option2));
			//isModifiedLength = true;
			return true;
		}
	}
}
