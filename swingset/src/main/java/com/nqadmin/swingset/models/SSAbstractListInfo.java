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

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

// SSAbstractListInfo.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * This class encapsulates the list and list data used for SwingSet 
 * list and combobox components. 
 * The class holds a reference to a {@code List<SSListItem>} and
 * it's method {@link #createListItem(java.lang.Object...) }
 * is a factory that creates SSListItem objects. It does not have
 * methods to modify the list; sub-classes provide that.
 * <p>
 * Where possible, this class and subclasses name methods
 * similarly to the List interface, such as "add*", "remove*".
 * <p>
 * The encapsulated data can be thought of as a two dimensional
 * array [ height X width ]; the height is the size of {@code List<SSListItem>},
 * the width is the number of elements in a SSListItem .
 * The number of elements is controlled by a property.
 * <p>
 * {@code createElementSlice} creates live, read-only, lists of
 * the individual elements of an SSListItem; it is [ height X 1 ].
 * This live list track changes to the main list.
 * The slice always has the same number of
 * items as the main list.
 * <p>
 * The contents of an SSListItem is read and written through the protected
 * {@link ListItem0} interface.
 * <p>
 * This class is not parameterized; all SSListItem elements are Objects.
 * It is expected that sub-classes are parameterized and cast as needed.
 * <h3>Remodel</h3>
 * Inspections and modifications of the item list, and of its SSListItems,
 * are done through a Remodel Object see {@link #getRemodel() }. The
 * remodel object is "try with resource" compatible and subclasses
 * may implement locking in their implementations of takeWriteLock()
 * and releaseWriteLock(). See {@link SSAbstractGlazedListComboInfo}
 * for an example.
 * <p>
 * Compatible with GlazedLists AutoComplete feature;
 * in which case an EventList is set in the constructor.
 * 
 * @see SSAbstractGlazedListComboInfo SSAbstractGlazedListComboInfo
 *		for use with GlazedLists AutoComplete feature
 * @since 4.0.0
 */
public abstract class SSAbstractListInfo {
	/**
	 * number of objects in the SSListItem
	 */
	private int itemNumElems;

	/**
	 * For fast check of valid element index.
	 * <p>
	 * {@code validElemsMask = (1 << nElem) - 1;} <br/>
	 * {@code if (!(validElemsMask & (1 << index))) error;}
	 */
	private int validElemsMask;

	/**
	 * This is the list of SSListItem elements.
	 * <p>
	 * <b>This should be an ArrayList.</b>
	 */
	protected final List<SSListItem> itemList;

	/**
	 * Any created slice-list is added here.
	 * If the number of SSListItem elements is changed,
	 * then created lists that no longer have active slices are marked invalid
	 * and slices that are now active are marked valid.
	 * Keep a weakreference so they can go away gracefully.
	 */
	private List<WeakReference<ItemElementSlice>> createdLists = new ArrayList<>();

	/**
	 * The constructor to create SSListItem
	 */
	private Constructor<?> listItemConstructor;

	/**
	 * Construct an empty list info container.
	 * @param itemNumElems number of elements in an SSListItem
	 * @param itemList must be empty
	 */
	protected SSAbstractListInfo(int itemNumElems, List<SSListItem> itemList) {
		Objects.requireNonNull(itemList);
		if (!itemList.isEmpty()) {
			throw new IllegalArgumentException("ListInfo requires empty list");
		}
		this.itemNumElems = itemNumElems;
		this.itemList = itemList;
		setupNumElems(itemNumElems);
	}

	/**
	 * This is used to configure the number of elements in an SSListItem.
	 * The itemList must be empty.
	 * 
	 * @param nElems number of elements, only 2,3 currently supported
	 */
	private void setupNumElems(int nElems) {
		if (!itemList.isEmpty()) {
			throw new IllegalArgumentException(
					"Only change number of items in a ListItem, when SSItemList is empty");
		}
		if (nElems < 2 || nElems > 30) {
			throw new IllegalArgumentException(
					"Only [2:30] items in a ListItem handled, not " + nElems);
		}
		validElemsMask = 0;
		Class<?> clazz = nElems == 2 ? ListItem2.class
				: nElems == 3 ? ListItem3.class : ListItemAsArray.class;
		try {
			listItemConstructor = clazz.getConstructor((new Object[0]).getClass());
		} catch (NoSuchMethodException|SecurityException ex) {
			throw new RuntimeException("SSAbstractListInfo impossible", ex);
		}
		validElemsMask = (1 << nElems) - 1;

		// Validate or invalidate any existing slices.
		// Also toss cleared references.
		for (Iterator<WeakReference<ItemElementSlice>> iterator = createdLists.iterator(); iterator.hasNext();) {
			WeakReference<ItemElementSlice> elRef = iterator.next();
			ItemElementSlice el = elRef.get();
			if (el == null) {
				iterator.remove();
				continue;
			}
			el.isValid = el.elemIndex < nElems;
		}
	}

	/** package access for testing */
	int checkCreatedLists() {
		// toss cleared references.
		for (Iterator<WeakReference<ItemElementSlice>> iterator = createdLists.iterator(); iterator.hasNext();) {
			WeakReference<ItemElementSlice> elRef = iterator.next();
			ItemElementSlice el = elRef.get();
			if (el == null) {
				iterator.remove();
				continue;
			}
		}
		return createdLists.size();
	}

	/** not used. package access for testing */
	static class SliceInfo {
		final int elemIndex;
		final boolean isValid;

		public SliceInfo(int elemIndex, boolean isValid) {
			this.elemIndex = elemIndex;
			this.isValid = isValid;
		}

		@Override
		public String toString() {
			return "SliceInfo{" + "elemIndex=" + elemIndex + ", isValid=" + isValid + '}';
		}
	}
	/** not used. package access for testing */
	SliceInfo sliceInfo(List<Object> l) {
		ItemElementSlice slice = null;
		if(l instanceof ItemElementSlice) {
			slice = (ItemElementSlice) l;
			return new SliceInfo(slice.elemIndex, slice.isValid);
		}
		return null;
	}

	/**
	 * Convenience method to test if the {@code List<SSListItem>} is empty.
	 * @return true if the item list is empty.
	 */
	public boolean isEmpty() {
		return itemList.isEmpty();
	}

	/**
	 * Convenience.
	 * @param _listItem find the index of this item
	 * @return index of item in the item list
	 */
	public int indexOfItem(SSListItem _listItem) {
		// TODO: optimized access methods
		return itemList.indexOf(_listItem);
	}

	/**
	 * @return number of elements in an SSListItem
	 */
	public int getItemNumElems() {
		return itemNumElems;
	}

	/**
	 * Configure the number of elements contained in an SSListItem.
	 * An exception is thrown if the item list is not empty.
	 * Currently only 2 or 3 items are allowed.
	 * ElementSlices are marked valid/invalid as appropriate.
	 * @param _itemNumElems number of elements in SSListItem
	 */
	protected void setItemNumElems(int _itemNumElems) {
		try (Remodel remodel = getRemodel()) {
			setupNumElems(_itemNumElems);
		}
		itemNumElems = _itemNumElems;
	}

	/**
	 * Create a list item.
	 * Exception if the number of elems does not match
	 * the number of elements configured with
	 * {@link #setItemNumElems } or constructor.
	 * @param elems elems, in order, to set into the SSListItem
	 * @return created list item
	 */
	protected SSListItem createListItem(Object... elems) {
		if (elems.length != itemNumElems) {
			throw new IllegalArgumentException(
					"Only " + itemNumElems + " elements accpeted in a ListItem, not " + elems.length);
		}
		try {
			return (SSListItem) listItemConstructor.newInstance((Object)elems);
		} catch (InstantiationException|IllegalAccessException
				|InvocationTargetException ex) {
			throw new RuntimeException("SSAbstractListInfo impossible", ex);
		}
	}

	/**
	 * Get an element from the list item at the specified position.
	 * @param _listItem extract an element from this
	 * @param _elemIndex which element to extract
	 * @return the element extracted from the list item.
	 */
	protected Object getElem(SSListItem _listItem, int _elemIndex) {
		return ((ListItem0)_listItem).getElem(_elemIndex);
	}

	/**
	 * Replace an element in the list item at the specified position
	 * @param _eventListItem operate on this list item
	 * @param _elemIndex index of elem to replace
	 * @param _newElem elem to put into the list item
	 * @return the previous contents of the list item at the specified position
	 */
	protected Object setElem(SSListItem _eventListItem, int _elemIndex, Object _newElem) {
		Object oldElem = ((ListItem0)_eventListItem).getElem(_elemIndex);
		((ListItem0)_eventListItem).setElem(_elemIndex, _newElem);
		return oldElem;
	}

	private class ItemElementSlice extends AbstractList<Object> {
		private final int elemIndex;
		private boolean isValid;

		public ItemElementSlice(int objectIndex) {
			this.elemIndex = objectIndex;
			isValid = (validElemsMask & (1<<elemIndex)) != 0;
		}

		@Override
		public Object get(int index) {
			checkValid();
			return ((ListItem0)itemList.get(index)).getElem(elemIndex);
		}
		
		@Override
		public int size() {
			checkValid();
			// Size gets called because this sits in a hashmap, albeit weak.
			return itemList.size();
		}

		private void checkValid() {
			if (!isValid) {
				throw new IllegalAccessError(String.format(
						"SSListItem element slice %d must be in [0:%d]",
						elemIndex, itemNumElems-1));
			}
		}
	}

	/**
	 * Create a list slice of the item list. There is no checking on the
	 * element index.
	 * <p>
	 * If a returned element slice is not valid according to the
	 * itemNumElems property, {@link #setItemNumElems},
	 * then an attempt to use that slice
	 * causes an exception. An element slice becomes valid/invalid
	 * dynamically as itemNumElems changes.
	 * @param elemIndex position in {@code SSListItem} of elements
	 * @return list of elements at the specified position
	 */
	protected List<Object> createElementSlice(int elemIndex) {
		if (elemIndex < 0) {
			throw new IllegalArgumentException("elemIndex must be positive");
		}
		ItemElementSlice el = new ItemElementSlice(elemIndex);
		createdLists.add(new WeakReference<>(el));
		return el;
	}

	//////////////////////////////////////////////////////////////////////////
	///////////////////  REMODEL  ////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////

	/**
	 * This is called by {@link Remodel#verifyOpened()} which
	 * is called by every method in Remodel. For debug or otherwise
	 * consistency checks can be placed in this method.
	 */
	protected abstract void checkState();

	/**
	 * This returns a Remodel which has method for
	 * reading and writing the itemList and its contained listItems.
	 * This is typically used in a try with resources
	 * <pre>
	 * {@code
	 *     class ListInfo extends XxxListInfo {...}
	 * 
	 *     try (ListInfo.Remodel remodel = listInfo.getRemodel()) {
	 *     	// examine and modify the list info
	 *     	if (!remodel.isEmpty()) { ... }
	 *     	else { ... }
	 *     }
	 * }
	 * </pre>
	 * 
	 * @return a Remodel permit
	 */
	protected abstract Remodel getRemodel();

	// /**
	//  * This must be called after a modification.
	//  */
	// protected void buildEventListItems() {
	// 	// FOR NOW SIMPLY USE THE MAPPING, primary key, as the eventListItem
	// 	// eventList.clear();
	// 	// eventList.add(mappings);
	// }

	/**
	 * This provides methods to perform inspections and
	 * modification of XxxListInfo.
	 * There is a class Inspect, similar to Remodel,
	 * that has methods that only read data.
	 * <p>
	 * It is anticipated that subclass may want to use Remodel
	 * as part of a locking scheme for multi-threaded
	 * access to SSAbstractListInfo.
	 * The {@link #verifyOpened()}
	 * method is useful for that.
	 * Methods in subclasses should call verifyOpened
	 * as their first statement; this avoids modifications
	 * after the lock is released.
	 * 
	 * @see SSAbstractGlazedListComboInfo for example of Remodel locking
	 */
	protected abstract class Remodel implements AutoCloseable {

		/** has this been closed? Error if access after close */
		protected boolean isClosed = false;

		// /** if optimized indexOfItem, following means must rebuild optimizations */
		// protected boolean isModifiedLength = false;

		/**
		 * This is invoked during construction.
		 * If there is no locking, implement an empty method
		 */
		protected abstract void takeWriteLock();

		/**
		 * This is invoked during close.
		 * If there is no locking, implement an empty method
		 */
		protected abstract void releaseWriteLock();

		/** a Remodel */
		protected Remodel() {
			takeWriteLock();
		}

		/**
		 * First statement of each method. Prevents use after close/unlock.
		 * 
		 * @throws IllegalStateException if this Remodel is closed
		 */
		protected void verifyOpened() {
			if (isClosed) {
				throw new IllegalStateException("Remodel completed; can not reuse");
			}
			checkState();
		}

		// /** if optimized indexOfItem, following means must rebuild optimizations */
		// protected boolean isModifiedLength = false;

		/**
		 * @return the list managed by this container
		 */
		protected List<SSListItem> getItemList() {
			verifyOpened();
			return  itemList;
		}

		/** @return true if there are no elements in the item list */
		public boolean isEmpty() {
			verifyOpened();
			return SSAbstractListInfo.this.isEmpty();
		}

		/**
		 * @param _listItem listItem to find in the itemList
		 * @return index of listItem in the itemList
		 */
		public int indexOfItem(SSListItem _listItem) {
			verifyOpened();
			return SSAbstractListInfo.this.indexOfItem(_listItem);
		}

		/** remove all elments from the itemList */
		public void clear() {
			verifyOpened();
			itemList.clear();
			// isModifiedLength = true;
		}

		/**
		 * Remove the listItem at the specified position from the itemList.
		 * @param _index remove listItem at this position
		 * @return the listItem that was removed from the list
		 */
		public SSListItem remove(int _index) {
			verifyOpened();
			SSListItem item = itemList.remove(_index);
			// isModifiedLength = true;
			return item;
		}

		/**
		 * @param _listItem item to remove from combobox
		 * @return true if the item was removed
		 */
		public boolean remove(SSListItem _listItem) {
			verifyOpened();
			int index = indexOfItem(_listItem);
			if(index < 0) {
				return false;
			}
			remove(index);
			return true;
		}

		/**
		 * Release write lock.
		 * Mark this Remodel closed, any further method invocations
		 * throw an exception.
		 */
		@Override
		final public void close() {
			// if (isModifiedLength) {
			// 	// TODO: rethink this
			// 	// NOT NEEDED UNTIL OPTIMIZATIONS
			// 	// buildEventListItems();
			// }
			releaseWriteLock();
			isClosed = true;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	//
	// The SSListItem container interface
	// and containers for an SSListItem
	//

	/**
	 * The access methods for an SSListItem.
	 * An SSListItem typically has 2 or 3 Objects in it.
	 * The first Object is typically a key, the next
	 * 1 or 2 objects are typically database columns related to the key.
	 */
	protected interface ListItem0 extends SSListItem {
		/**
		 * Get an item from the SSListItem.
		 * <p>
		 * Typically index == 0 is a primary key
		 * 
		 * @param index which item to get
		 * @return the object from the SSListItem
		 */
		Object getElem(int index);

		/**
		 * Put an object into the SSListItem.
		 * @param index which item to set
		 * @param object the object to put into the SSListItem
		 */
		void setElem(int index, Object object);
	}

	//
	// DO NOT LOOK BELOW THIS LINE
	//
	/////////////////////////////////////////////////////////////////////
	//
	// Use one of the following object, ListItem2 or ListItem3,
	// for an SSListItem depending on 2 or 3 objects in the list item.
	// Note that this is smaller that having an array.
	//
	// If want to handle 4 objects, might use an array. See ListItemAsArray below
	// That's a container object + 4 data object + array object = 6 objects;
	// around 17% of storage, the array object, is unneeded overhead.
	//

	/**
	 * An SSListItem with 2 Objects.
	 */
	private static class ListItem2 implements ListItem0 {
		Object arg0;
		Object arg1;

		public ListItem2(Object[] elems) {
			arg0 = elems[0];
			arg1 = elems[1];
		}

		private void checkIndex(int index) {
			if ((0b011 & (1 << index)) == 0) {
				throw new ArrayIndexOutOfBoundsException(
						"Only 0 or 1 index available for this ListItem, not " + index);
			}
		}

		@Override
		public Object getElem(int index) {
			checkIndex(index);
			return index == 0 ? arg0 : arg1;
		}

		@Override
		public void setElem(int index, Object object) {
			checkIndex(index);
			if (index == 0) arg0 = object;
			else			arg1 = object;
		}

		@Override
		public String toString() {
			return "{" + arg0 + "," + arg1 + '}';
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 19 * hash + Objects.hashCode(this.arg0);
			hash = 19 * hash + Objects.hashCode(this.arg1);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final ListItem2 other = (ListItem2) obj;
			if (!Objects.equals(this.arg0, other.arg0)) {
				return false;
			}
			if (!Objects.equals(this.arg1, other.arg1)) {
				return false;
			}
			return true;
		}

	}

	/**
	 * An SSListItem with 3 Objects.
	 */
	private static class ListItem3 implements ListItem0 {
		Object arg0;
		Object arg1;
		Object arg2;

		public ListItem3(Object[] elems) {
			arg0 = elems[0];
			arg1 = elems[1];
			arg2 = elems[2];
		}

		private void checkIndex(int index) {
			if ((0b0111 & (1 << index)) == 0) {
				throw new ArrayIndexOutOfBoundsException(
						"Only 0, 1 or 2 index available for this ListItem, not " + index);
			}
		}

		@Override
		public Object getElem(int index) {
			checkIndex(index);
			return index == 0 ? arg0 : index == 1 ? arg1 : arg2;
		}

		@Override
		public void setElem(int index, Object object) {
			checkIndex(index);
			if (index == 0)		 arg0 = object;
			else if (index == 1) arg1 = object;
			else				 arg2 = object;
		}

		@Override
		public String toString() {
			return "{" + arg0 + "," + arg1 + "," + arg2 + '}';
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 47 * hash + Objects.hashCode(this.arg0);
			hash = 47 * hash + Objects.hashCode(this.arg1);
			hash = 47 * hash + Objects.hashCode(this.arg2);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final ListItem3 other = (ListItem3) obj;
			if (!Objects.equals(this.arg0, other.arg0)) {
				return false;
			}
			if (!Objects.equals(this.arg1, other.arg1)) {
				return false;
			}
			if (!Objects.equals(this.arg2, other.arg2)) {
				return false;
			}
			return true;
		}
	}

	// This can be used for an arbitrary, but fixed, number of items
	// in an SSListItem. Typically 4 or more items.
	private static class ListItemAsArray implements ListItem0 {
		Object[] elems;

		public ListItemAsArray(Object [] elems) {
			this.elems = Arrays.copyOf(elems, elems.length);
		}

		@Override
		public Object getElem(int index) {
			return elems[index];
		}

		@Override
		public void setElem(int index, Object object) {
			elems[index] = object;
		}

		@Override
		public String toString() {
			return "{" + Arrays.toString(elems) + '}';
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 83 * hash + Arrays.deepHashCode(this.elems);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final ListItemAsArray other = (ListItemAsArray) obj;
			if (!Arrays.deepEquals(this.elems, other.elems)) {
				return false;
			}
			return true;
		}


	}
}
