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

/**
 * This class manages a with list items.
 * See {@link GlazedListsKeyDisplayValueInfo} for GlazedList specific handling.
 * The SSListItem elements are {@literal {key, displayValue, displayValue2}};
 * where key is generally a primary key, displayValue has display info, displayValue2 has
 * additional display info. displayValue2 is optional, see
 * {@link #setDisplayValue2Enabled(boolean)} and the constructors. There are ways
 * to create, modify and examine the SSListItem. Individual read-only lists for
 * keys, displayValues, displayValues2 are available.
 * <p>
 * Use {@link #getRemodel} for a multi-thread safe Object 
 * for modifying and examining the itemList and list items.
 * The remodel lock uses the EventList lock.
 * 
 * @param <K> key type; key is typically primary key
 * @param <D> displayValue type; displayValue provides display string
 * @param <D2>  displayValue2 type; if present, supplementary display string
 * 
 * @see AbstractComboBoxListSwingModel
 * AbstractComboBoxListSwingModel for general description of using this class
 * @see <a href="https://javadoc.io/doc/com.glazedlists/glazedlists/latest/ca/odell/glazedlists/swing/AutoCompleteSupport.html" target="_blank" rel="noopener noreferrer">GlazedLists AutoCompletion javadoc</a>
 * 
 * @since 4.0.0
 */
public class KeyDisplayValueSwingModel<K,D,D2> extends AbstractComboBoxListSwingModel {

	/** index of displayValue in SSListItem */
	// DisplayValue IS FIRST. THIS IS THE DEFAULT FOR SSListItemFormat
	// In addition, only DisplayValue is required.
	// LOOK AT createDisplayValueItem BEFORE THINKING ABOUT CHANGING THESE DEFINES
	protected final static int DISP_IDX = 0; // 
	/** index of primary key in SSListItem */
	protected final static int KEY_IDX = 1;
	/** index of displayValue2 in SSListItem */
	protected final static int DISP2_IDX = 2;

	/** up to 3 slices created, DISP, KEY, DISP2 */
	private final List<?>[] slices = new List<?>[3];
	/** when true, there can be displayValues2 */
	private boolean displayValue2Enabled = false;

	/**
	 * Given some tModel, if possible return the tModel cast as a KeyDisplayValueSwingModel.
	 * @param model tModel to check
	 * @return KeyDisplayValueSwingModel or null
	 */
	public static KeyDisplayValueSwingModel<?,?,?> asKeyDisplayValueSwingModel(ListModel<SSListItem> model) {

		if (model instanceof KeyDisplayValueSwingModel) {
			return (KeyDisplayValueSwingModel<?,?,?>) model;
		}
		if (model instanceof ComboBoxListSwingModel comboBoxListSwingModel) {
			Object tModel = comboBoxListSwingModel.getComboBoxListSwingModel();
			if (tModel instanceof KeyDisplayValueSwingModel) {
				return (KeyDisplayValueSwingModel<?,?,?>) tModel;
			}
		}
		return null;
	}

	/**
	 * Create an empty KeyDisplayValueSwingModel with no displayValues2.
	 */
	protected KeyDisplayValueSwingModel() {
		this(false);
	}

	/**
	 * Create an empty KeyDisplayValueSwingModel.
	 * 
	 * @param displayValue2Enabled true says to provide a displayValue2 field in SSListItem
	 */
	public KeyDisplayValueSwingModel(boolean displayValue2Enabled) {
		this(displayValue2Enabled, null);
	}


	/**
	 * Create an empty KeyDisplayValueSwingModel.
	 * @param displayValue2Enabled true says to provide a displayValue2 field in SSListItem
	 * @param itemList tModel backing store
	 */
	public KeyDisplayValueSwingModel(boolean displayValue2Enabled, List<SSListItem> itemList) {
		super(displayValue2Enabled ? 3 : 2, itemList);
		this.displayValue2Enabled = displayValue2Enabled;
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
	 * @return unmodifiable list of keys
	 */
	public List<K> getKeys() {
		return getSlice(KEY_IDX);
	}
	
	/**
	 * Note when getAllowNull is true, the first list item is null/""
	 * @return unmodifiable list of displayValue
	 */
	public List<D> getDisplayValues() {
		return getSlice(DISP_IDX);
	}
	
	/**
	 * Note when getAllowNull is true, the first list item is null/""
	 * @return unmodifiable list of displayValue2
	 */
	public List<D2> getDisplayValues2() {
		return getSlice(DISP2_IDX);
	}
	
	/**
	 * Change whether the layout of an SSListItem managed by this class
	 * contains an displayValue. An exception is thrown if the item list is not empty. An
 existing displayValue2 list {@link #getDisplayValues2}
	 * is invalidated or validated accordingly.
	 * @param displayValue2Enabled true if list item shall contain displayValue
	 */
	public void setDisplayValue2Enabled(boolean displayValue2Enabled) {
		if(displayValue2Enabled == displayValue2Enabled) {
			return;
		}
		if (!getItemList().isEmpty()) {
			throw new IllegalStateException("Only change displaValue2enabled when empty");
		}
		this.displayValue2Enabled = displayValue2Enabled;
		// normally 2 elements in ListItem {key,displaValue}, displaValue2 add 3rd.
		setItemNumElems(displayValue2Enabled ? 3 : 2);
	}

	/**
	 * convenient verification that displaValue2 is enabled
	 */
	private void usingDisplayValue2() {
		if (!displayValue2Enabled) {
			throw new IllegalStateException("displaValue2enabled is false");
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
	 * @param key value (primary key)
	 * @param displayValue display info derived from this
	 * @param displayValue2 additional display info
	 * @return the new list item
	 */
	protected SSListItem createDisplayValueItem(K key, D displayValue, D2 displayValue2) { 
		// ALL ListItem CREATION GOES THROUGH HERE
		// THE DEFINES FOR KEY_IDX, DISP_IDX ARE RELEVANT TO THE ORDER.
		// displayValue MUST GO FIRST
		return displayValue2Enabled ? createListItem(displayValue, key, displayValue2)
				: createListItem(displayValue, key);
		//return displayValue2Enabled ? createListItem(key, displayValue, displayValue2)
		//		: createListItem(key, displayValue);
	}

	/**
	 * Create a list of list items with the specified contents;
	 * the lists must be the same size.
	 * @param keys list of key values (primary key)
	 * @param displayValues display info derived from this
	 * @param displayValues2 additional display info
	 * @return the new list item
	 */
	protected List<SSListItem> createDisplayValueItems(List<K> keys, List<D> displayValues, List<D2> displayValues2) {
		Objects.requireNonNull(keys);
		Objects.requireNonNull(displayValues);
		if (displayValue2Enabled) {
			Objects.requireNonNull(displayValues2);
		} else {
			if (displayValues2 != null) {
				throw new IllegalArgumentException("displayValues2 provided, but not enabled");
			}
		}
		int n = keys.size();
		if (displayValues.size() != n || displayValues2 != null && displayValues2.size() != n) {
			throw new IllegalArgumentException("Lists must be the same size");
		}
		List<SSListItem> displayValueItems = new ArrayList<>(keys.size());
		if(displayValue2Enabled && displayValues2 != null) {
			for (int i = 0; i < keys.size(); i++) {
				displayValueItems.add(createDisplayValueItem(
						keys.get(i), displayValues.get(i), displayValues2.get(i)));
			}
		} else {
			for (int i = 0; i < keys.size(); i++) {
				displayValueItems.add(createDisplayValueItem(
						keys.get(i), displayValues.get(i), null));
			}
		}
		return displayValueItems;
	}

	/**
	 * @return true if a list item contains displaValue2
	 */
	public boolean isdisplaValue2Enabled() {
		return displayValue2Enabled;
	}

	/**
	 * Get the opaque index for the key element in a SSListItem.
	 * Useful for use with {@link SSListItemFormat} or setElem.
	 * @return the index of the key
	 */
	public int getKeyListItemElemIndex() {
		return KEY_IDX;
	}

	/**
	 * Get the opaque index for the displayValue element in a SSListItem.
	 * Useful for use with {@link SSListItemFormat}.
	 * @return the index of displayValue
	 */
	public int getDisplayValueListItemElemIndex() {
		return DISP_IDX;
	}

	/**
	 * Get the opaque index for the displaValue2 element in a SSListItem.
	 * Useful for use with {@link SSListItemFormat}.
	 * @return the index of displaValue2
	 */
	public int getDisplayValue2ListItemElemIndex() {
		return DISP2_IDX;
	}

	/**
	 * Get object to make changes.
	 * @return Remodel
	 * @see Remodel
	 */
	@Override
	public Remodel getRemodel() {
		// default is no locking, re-use the tModel.
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
	@Override protected void remodelReleaseWriteLock(AbstractComboBoxListSwingModel.Remodel remodel) { }
	
	/** Methods for inspecting and modifying list info */
	public class Remodel extends AbstractComboBoxListSwingModel.Remodel {
		
		/**
		 * Return an unmodifiable list of keys.
		 * <p>
		 * <b>When getAllowNull() is true, the first list item is null/""</b>
		 * @return list of keys
		 */
		public List<K> getKeys() {
			verifyOpened();
			return KeyDisplayValueSwingModel.this.getKeys();
		}
		
		/**
		 * Return an unmodifiable list of displayValue.
		 * <p>
		 * <b>When getAllowNull() is true, the first list item is null/""</b>
		 * @return list of displayValue
		 */
		public List<D> getDisplayValues() {
			verifyOpened();
			return KeyDisplayValueSwingModel.this.getDisplayValues();
		}
		
		/**
		 * Return an unmodifiable list of displaValue2.
		 * <p>
		 * <b>When getAllowNull() is true, the first list item is null/""</b>
		 * @return list of displayValue2
		 */
		public List<D2> getDisplayValues2() {
			verifyOpened();
			return KeyDisplayValueSwingModel.this.getDisplayValues2();
		}
		
		/**
		 * Extract the key from the list item.
		 * @param listItem list item from which key is extracted
		 * @return the key
		 */
		@SuppressWarnings("unchecked")
		public K getKey(SSListItem listItem) {
			return (K)super.getElem(listItem, KEY_IDX);
		}
		
		/**
		 * Extract the displayValue from the list item.
		 * @param listItem list item from which displayValue is extracted
		 * @return the displayValue
		 */
		@SuppressWarnings("unchecked")
		public D getDisplayValue(SSListItem listItem) {
			return (D)super.getElem(listItem, DISP_IDX);
		}
		
		/**
		 * Extract the displayValue from the list item.
		 * @param listItem list item from which displayValue is extracted
		 * @return the displayValue
		 */
		@SuppressWarnings("unchecked")
		public D2 getDisplayValue2(SSListItem listItem) {
			usingDisplayValue2();
			return (D2)super.getElem(listItem, DISP2_IDX);
		}

		/**
		 * Extract the key from the list item at the specified item list index.
		 * Convenience method for {@code getKey(getEventList().get(index))}
		 * @param index an item list index
		 * @return the displayValue
		 */
		@SuppressWarnings("unchecked")
		public K getKey(int index) {
			return (K)super.getElem(index, KEY_IDX);
		}
		
		/**
		 * Set the key element of the list item
		 * at the specified item list index.
		 * @param index of list item
		 * @param key put this into list item
		 * @return previous contents; the replaced element.
		 */
		@SuppressWarnings("unchecked")
		public D setKey(int index, D key) {
			return (D) super.setElem(index, DISP_IDX, key);
		}
		
		/**
		 * Set the displayValue element of the list item
		 * at the specified item list index.
		 * @param index of list item
		 * @param displayValue put this into list item
		 * @return previous contents; the replaced element.
		 */
		@SuppressWarnings("unchecked")
		public D setDisplayValue(int index, D displayValue) {
			return (D) super.setElem(index, DISP_IDX, displayValue);
		}

		/**
		 * Set the displayValue element of the list item
		 * at the specified item list index.
		 * @param index of list item
		 * @param displayValue2 put this into list item
		 * @return previous contents; the replaced element
		 */
		@SuppressWarnings("unchecked")
		public D2 setDisplayValue2(int index, D2 displayValue2) {
			usingDisplayValue2();
			return (D2) super.setElem(index, DISP2_IDX, displayValue2);
		}
		
		/**
		 * Add list items with the specified contents. The input lists shall be the same size.
		 * @param keys list of key values (primary keys)
		 * @param displayValues list of display info 
		 * @return the new list item
		 */
		public boolean addAll(List<K> keys, List<D> displayValues) {
			return addAll(keys, displayValues, null);
		}

		/**
		 * Add list items with the specified contents. The input lists shall be the same size.
		 * @param keys list of values (often a primary key)
		 * @param displayValues display info derived from this
		 * @param displayValues2 additional display info
		 * @return true if the list changed
		 */
		public boolean addAll(List<K> keys, List<D> displayValues, List<D2> displayValues2) {
			if (displayValues2 != null) {
				usingDisplayValue2();
			}
			List<SSListItem> list = KeyDisplayValueSwingModel.this
					.createDisplayValueItems(keys, displayValues, displayValues2);
			return super.addAll(list);
			//isModifiedLength = true;
		}

		/**
		 * Create an SSListItem with the specified contents
		 * and add it to the item list. The input lists shall be the same size.
		 * @param key list of key values key value (primary key)
		 * @param displayValue display info
		 * @return the new list item
		 */
		public boolean add(K key, D displayValue) {
			return add(key, displayValue, null);
		}

		/**
		 * Create an SSListItem with the specified contents
		 * and add it to the item list. The input lists shall be the same size.
		 * @param key commonly a primary key
		 * @param displayValue display info derived from this
		 * @param displayValue2 additional display info
		 * @return the new list item
		 */
		public boolean add(K key, D displayValue, D2 displayValue2) {
			if (displayValue2 != null) {
				usingDisplayValue2();
			}
			return super.add(createKeyDisplayValueItem(key, displayValue, displayValue2));
			//itemList.add(createDisplayValueItem(key, displayValue, displayValue2));
			//isModifiedLength = true;
			//return true;
		}
		
		/**
		 * Create a list item with the specified contents.
		 * @param key key value (primary key)
		 * @param displayValue display info derived from this
		 * @param displayValue2 additional display info
		 * @return the new list item
		 */
		public SSListItem createKeyDisplayValueItem(K key, D displayValue, D2 displayValue2) {
			return KeyDisplayValueSwingModel.this.createDisplayValueItem(key, displayValue, displayValue2);
		}
	}
}
