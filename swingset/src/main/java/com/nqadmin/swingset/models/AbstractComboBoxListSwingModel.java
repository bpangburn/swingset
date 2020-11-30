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

import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// AbstractComboBoxListSwingModel.java
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
 * The number of elements in an SSListItem is controlled by a property;
 * the property may only be changed when the item list is empty.
 * <p>
 * {@code createElementSlice} creates live, read-only, lists of
 * the individual elements of an SSListItem; it is [ height X 1 ].
 * This live list track changes to the main list.
 * The slice always has the same number of
 * items as the main list.
 * <p>
 * The contents of an SSListItem are read through the protected
 * {@link ListItem0} interface. The contents are modified through
 * methods in this class.
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
public abstract class AbstractComboBoxListSwingModel extends AbstractListModel<SSListItem>
implements MutableComboBoxModel<SSListItem> {
	private static final long serialVersionUID = 1L;

	/** when true, handle combo box selected item (about the events) */
	private boolean comboBoxModel;
	/** this has been installed into a JComponent */
	private boolean installed;
	
	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * Log4j Logger for component
	 */
	private static Logger eventLogger = LogManager.getLogger(AbstractComboBoxListSwingModel.class.getName() + ".events");

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
	 * The list of SSListItem elements.
	 * <p>
	 */
	private final List<SSListItem> itemList;

	/**
	 * A read only list of SSListItem elements.
	 * <p>
	 */
	private final List<SSListItem> readOnlyItemList = new AbstractList<SSListItem>() {
		@Override
		public SSListItem get(int index) {
			return itemList.get(index);
		}

		@Override
		public int size() {
			return itemList.size();
		}
	};

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
	 * @param _itemNumElems number of elements in an SSListItem
	 */
	protected AbstractComboBoxListSwingModel(int _itemNumElems) {
		this(_itemNumElems, null);
	}

	/**
	 * Construct a info container; if the specified itemList is
	 * null an array list is created. Only use this method directly
	 * if you are sure you must.
	 * <p>
	 * If an itemList is passed in, <b>lose the reference</b>;
	 * if the list, or its contents, are modified directly
	 * then swing model events are lost.
	 * @param _itemNumElems number of elements in an SSListItem
	 * @param _itemList list to manage, may be null
	 */
	
	
	protected AbstractComboBoxListSwingModel(int _itemNumElems, List<SSListItem> _itemList) {
		if(_itemList != null && !_itemList.isEmpty()) {
			throw new IllegalArgumentException("item list must be empty");
		}
		if (eventLogger.isTraceEnabled()) {
			addEventLogging();
		}
		itemList = _itemList != null ? _itemList : new ArrayList<>();
		this.itemNumElems = _itemNumElems;
		setupNumElems(_itemNumElems);
	}

	/**
	 * Used for testing to set combo handling flags.
	 * @param _itemNumElems
	 * @param _isCombo in a combo box
	 * @param _comboOld default combo box semantics
	 */
	/*package-test*/ AbstractComboBoxListSwingModel(int _itemNumElems, boolean _isCombo) {
		this(_itemNumElems);
		
		comboBoxModel = _isCombo;
	}

	boolean isComboBoxModel() {
		return comboBoxModel;
	}

	private void addEventLogging() {
		addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e) {
				eventLogger.trace(() -> e.toString());
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
				eventLogger.trace(() -> e.toString());
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				eventLogger.trace(() -> e.toString());
			}
		});
	}

	//////////////////////////////////////////////////////////////////////////
	//
	// Swing Model methods
	//

	// ListModel

	@Override
	public int getSize() {
		return itemList.size();
	}

	@Override
	public SSListItem getElementAt(int index) {
		if (comboBoxModel) {
			// The DefaultComboBoxModel never throws an exception
			// Curiously, the DefaultListModel for this same method
			// does throw an exception.
			if ( index >= 0 && index < itemList.size() )
				return itemList.get(index);
			else
				return null;
		}
		return itemList.get(index);
	}

	// ComboBoxModel

	private SSListItem selectedObject;

	@Override
	public void setSelectedItem(Object anItem) {
		// TODO: exception if not combo?
		if (comboBoxModel) {
			if (!Objects.equals(selectedObject, anItem)) {
				if (anItem == null || anItem instanceof SSListItem) {
					selectedObject = (SSListItem)anItem;
					fireContentsChanged(this, -1, -1);
				} else {
					logger.warn(() -> "ComboBox#setSelectedItem(" + anItem + ") not SSListItem");
				}
			}
		}
	}

	@Override
	public SSListItem getSelectedItem() {
		// TODO: exception if not combo?
        return selectedObject;
	}

	// MutableComboBoxModel
	//		NOTE the helper methods for keeping selection
	//			comboAdjustSelectedAfterAdd and comboAdjustSelectedForRemove

	@Override
	public void addElement(SSListItem item) {
		try (Remodel remodel = getRemodel()) {
			remodel.add(item);
		}
	}

	@Override
	public void insertElementAt(SSListItem item, int index) {
		try (Remodel remodel = getRemodel()) {
			remodel.add(index, item);
		}
	}

	@Override
	public void removeElement(Object obj) {
		try (Remodel remodel = getRemodel()) {
			remodel.remove(obj);
		}
		if (!(obj instanceof SSListItem)) {
			logger.warn(() -> "ComboBox#removeElement(" + obj + ") not SSListItem");
		}
	}

	@Override
	public void removeElementAt(int index) {
		try (Remodel remodel = getRemodel()) {
			remodel.remove(index);
		}
	}

	// Helper methods for adjusting selected

	/**
	 * After adding what becomes the only list item in the list
	 * adjust the selection if... See DefaultComboBoxModel
	 * 
	 * @param item the item just added
	 */
	private void comboAdjustSelectedAfterAdd(SSListItem item) {
		if (comboBoxModel) {
			if ( itemList.size() == 1 && selectedObject == null && item != null ) {
				setSelectedItem(item);
			}
		}
	}

	/**
	 * After adding to a list that was empty,
	 * adjust the selection if... See DefaultComboBoxModel
	 * 
	 * @param oldSize the size before additions
	 */
	private void adjustSelectedAfterAdd(int oldSize) {
		if (comboBoxModel) {
			if (oldSize == 0) {
				if ( itemList.size() >= 1 && selectedObject == null ) {
					SSListItem item = itemList.get(0);
					if (item != null) {
						setSelectedItem(item);
					}
				}
			}
		}
	}

	private void comboAdjustSelectedForRemove(int _index) {
		if (comboBoxModel) {
			if ( getElementAt( _index ) == selectedObject ) {
				if ( _index == 0 ) {
					setSelectedItem( getSize() == 1 ? null : getElementAt( _index + 1 ) );
				}
				else {
					setSelectedItem( getElementAt( _index - 1 ) );
				}
			}
		}
	}

	private void comboAdjustSelectedForClear() {
		// NOTE seems like should throw change event,
		// but guess it's covered by the remove event.
		selectedObject = null;
	}

	//////////////////////////////////////////////////////////////////////////
	//
	// ListInfo methods
	//
	// Note that the methods that modify the item list are private
	// and fire events as needed.
	// They are exposed through Remodel.
	//

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
		if (nElems < 1 || nElems > 30) {
			throw new IllegalArgumentException(
					"Only [1:30] items in a ListItem handled, not " + nElems);
		}
		validElemsMask = 0;
		Class<?> clazz;
		switch (nElems) {
		case 1:
			clazz = ListItem1.class; break;
		case 2:
			clazz = ListItem2.class; break;
		case 3:
			clazz = ListItem3.class; break;
		default:
			clazz = ListItemAsArray.class; break;
		}
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

	/**
	 * Retire any list slice that was garbage collected.
	 * Usually used for testing.
	 * A subclass may use this if it wants the affect.
	 */
	protected int checkCreatedLists() {
		// toss cleared references.
		for (Iterator<WeakReference<ItemElementSlice>> iterator = createdLists.iterator(); iterator.hasNext();) {
			WeakReference<ItemElementSlice> elRef = iterator.next();
			ItemElementSlice el = elRef.get();
			if (el == null) {
				iterator.remove();
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
	 * Get a read only reference to the item list managed by this container.
	 * <p>
	 * Note this is not locked.
	 * @return the item list
	 */
	public List<SSListItem> getItemList() {
		return  readOnlyItemList;
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
	 * From the list item at the specified list item index,
	 * get the element at the specified element position.
	 * @param _listItemIndex index of the list item
	 * @param _elemIndex which element to extract
	 * @return the element extracted from the list item.
	 */
	private Object getElem(int _listItemIndex, int _elemIndex) {
		return getElem(itemList.get(_listItemIndex), _elemIndex);
	}

	/**
	 * Get an element from the list item at the specified position.
	 * @param _listItem extract an element from this
	 * @param _elemIndex which element to extract
	 * @return the element extracted from the list item.
	 */
	private Object getElem(SSListItem _listItem, int _elemIndex) {
		return ((ListItem0)_listItem).getElem(_elemIndex);
	}

	//////////////////////////////////////////////////////////////////////////
	//
	// Modifications, list or item.
	//
	// These should only be invoked from constructor or remodel.
	//

	private boolean add(SSListItem _listItem) {
		int addAt = itemList.size();
		boolean isChanged = itemList.add(_listItem);
		if (isChanged) {
            fireIntervalAdded(this, addAt, addAt);
			comboAdjustSelectedAfterAdd(_listItem);
		}
		return isChanged;
	}

	private void add(int _index, SSListItem _listItem) {
		itemList.add(_index, _listItem);
		fireIntervalAdded(this, _index, _index);
		comboAdjustSelectedAfterAdd(_listItem);
	}

	private boolean addAll(List<SSListItem> newItems) {
		// first new item goes here
		int firstAddAt = itemList.size();
		boolean isChanged = itemList.addAll(newItems);
		if (isChanged) {
            fireIntervalAdded(this, firstAddAt, itemList.size()-1);
			adjustSelectedAfterAdd(firstAddAt);
		}
		return isChanged;
	}

	private SSListItem set(int _index, SSListItem _newItem) {
		SSListItem oldVal = itemList.set(_index, _newItem);
		fireContentsChanged(this, _index, _index);
		return oldVal;
	}

	private void clear() {
		comboAdjustSelectedForClear();
		if (itemList.size() > 0) {
			int firstIndex = 0;
			int lastIndex = itemList.size() - 1;
			itemList.clear();
            fireIntervalRemoved(this, firstIndex, lastIndex);
		}
	}

	private SSListItem remove(int _index) {
		comboAdjustSelectedForRemove(_index);
		SSListItem item = itemList.remove(_index);
        fireIntervalRemoved(this, _index, _index);
		return item;
	}

	private boolean remove(Object _listItem) {
		int index = itemList.indexOf(_listItem);
		if(index < 0) {
			return false;
		}
		// following fires event
		remove(index);
		return true;
	}

	/**
	 * With the list item at the specified list item index,
	 * replace an element in the list item at the specified position.
	 * @param _listItemIndex operate on the list item at this index
	 * @param _elemIndex index of elem to replace
	 * @param _newElem elem to put into the list item
	 * @return the previous contents of the list item at the specified position
	 */
	// NOTE: setElem(listItem, elemIndex, newElem)
	// is a problem because need listitemindex for fireContentsChanged
	private Object setElem(int _listItemIndex, int _elemIndex, Object _newElem) {
		SSListItem listItem = itemList.get(_listItemIndex);
		Object oldElem = ((ListItemWrite0)listItem).getElem(_elemIndex);
		((ListItemWrite0)listItem).setElem(_elemIndex, _newElem);
		fireContentsChanged(this, _listItemIndex, _listItemIndex);
		return oldElem;
	}

	//////////////////////////////////////////////////////////////////////////
	//
	// Slices
	//

	/**
	 * This doesn't lock. Is that a problem?
	 */
	private class ItemElementSlice extends AbstractList<Object> {
		private final int elemIndex;
		private final boolean isReadOnly = true; // NEVER TURN THIS OFF
		private boolean isValid;

		public ItemElementSlice(int objectIndex) {
			this.elemIndex = objectIndex;
			isValid = (validElemsMask & (1<<elemIndex)) != 0;
		}

		@Override
		public Object get(int index) {
			checkValid();
			return getElem(index, elemIndex);
		}
		
		@Override
		public int size() {
			checkValid();
			return itemList.size();
		}

		// THIS IS A NICE IDEA, IT DOES THROW EVENTS. BUT TOO EASY
		// TO CIRCUMVENT LOCKING WHILE MODIFYING
		@Override
		public Object set(int index, Object element) {
			checkValid();
			if(isReadOnly) {
				return super.set(index, element);
			}
			return setElem(index, elemIndex, element);
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
	//
	// Remodel
	//

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
 It is anticipated that subclass may want to use Remodel
 as part of a locking scheme for multi-threaded
 access to AbstractComboBoxListSwingModel.
 The {@link #verifyOpened()}
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
		 * Get a read only reference to the item list managed by this container
		 * @return the item list
		 */
		public List<SSListItem> getItemList() {
			verifyOpened();
			return  readOnlyItemList;
		}

		/** @return true if there are no list items in the item list */
		public boolean isEmpty() {
			verifyOpened();
			return itemList.isEmpty();
		}

		/**
		 * @param _listItem listItem to find in the itemList
		 * @return index of listItem in the itemList
		 */
		public int indexOf(Object _listItem) {
			verifyOpened();
			return itemList.indexOf(_listItem);
		}

		/**
		 * Return the list item at the specified position in this item list.
		 * @param _index index of the item to return
		 * @return the item at the specified position
		 */
		public SSListItem get(int _index) {
			verifyOpened();
			return itemList.get(_index);
		}

		/**
		 * Appends the specified list item to the end of this list.
		 * @param _newItem item to be appended to this list
		 * @return true if the item was appended
		 */
		public boolean add(SSListItem _newItem) {
			verifyOpened();
			return AbstractComboBoxListSwingModel.this.add(_newItem);
			// isModifiedLength = true;
		}

		/**
		 * Inserts the specified list item at the specified position in this list.
		 * @param _index index at which the specified item is to be inserted
		 * @param _newItem list item to inserted
		 */
		public void add(int _index, SSListItem _newItem) {
			verifyOpened();
			AbstractComboBoxListSwingModel.this.add(_index, _newItem);
			// isModifiedLength = true;
		}
		/**
		 * Appends all of the list items in the specified list
		 * to the end of this list.
		 * @param _newItems items to add to this list.
		 * @return true if the list changed
		 */
		public boolean addAll(List<SSListItem> _newItems) {
			verifyOpened();
			return AbstractComboBoxListSwingModel.this.addAll(_newItems);
			// isModifiedLength = true;
		}
		/**
		 * Replaces the item at the specified position in the list
		 * with the specified item.
		 * @param _index index of the item to replace
		 * @param _newItem item to store at the index
		 * @return 
		 */
		public SSListItem set(int _index, SSListItem _newItem) {
			verifyOpened();
			return AbstractComboBoxListSwingModel.this.set(_index, _newItem);
		}

		/** remove all list items from the itemList */
		public void clear() {
			verifyOpened();
			AbstractComboBoxListSwingModel.this.clear();
			// isModifiedLength = true;
		}
		
		/**
		 * Remove the listItem at the specified position from the itemList.
		 * @param _index remove listItem at this position
		 * @return the listItem that was removed from the list
		 */
		public SSListItem remove(int _index) {
			verifyOpened();
			SSListItem item = AbstractComboBoxListSwingModel.this.remove(_index);
			// isModifiedLength = true;
			return item;
		}

		/**
		 * @param _listItem item to remove from combobox
		 * @return true if the item was removed
		 */
		public boolean remove(Object _listItem) {
			verifyOpened();
			return AbstractComboBoxListSwingModel.this.remove(_listItem);
			// isModifiedLength = true;
		}
		
		/**
		 * From the list item at the specified list item index,
		 * get the element at the specified element position.
		 * @param _listItemIndex index of the list item
		 * @param _elemIndex which element to extract
		 * @return the element extracted from the list item.
		 */
		public Object getElem(int _listItemIndex, int _elemIndex) {
			verifyOpened();
			return AbstractComboBoxListSwingModel.this.getElem(_listItemIndex, _elemIndex);
		}
		
		/**
		 * From the list item,
		 * get the element at the specified element position.
		 * @param _listItem the list item
		 * @param _elemIndex which element to extract
		 * @return the element extracted from the list item.
		 */
		public Object getElem(SSListItem _listItem, int _elemIndex) {
			verifyOpened();
			return AbstractComboBoxListSwingModel.this.getElem(_listItem, _elemIndex);
		}
		
		/**
		 * With the list item at the specified list item index,
		 * replace an element in the list item at the specified position.
		 * @param _listItemIndex operate on the list item at this index
		 * @param _elemIndex index of elem to replace
		 * @param _newElem elem to put into the list item
		 * @return the previous contents of the list item at the specified position
		 */
		public Object setElem(int _listItemIndex, int _elemIndex, Object _newElem) {
			verifyOpened();
			return AbstractComboBoxListSwingModel.this.setElem(_listItemIndex, _elemIndex, _newElem);
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
	 * An SSListItem typically has 1, 2 or 3 Objects in it.
	 * The first Object is often a key, the next
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
	}

	private interface ListItemWrite0 extends ListItem0 {
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
	 * An SSListItem with 1 Objects.
	 */
	private static class ListItem1 implements ListItemWrite0 {
		Object arg0;

		public ListItem1(Object[] elems) {
			arg0 = elems[0];
		}

		private void checkIndex(int index) {
			if (index != 0) {
				throw new ArrayIndexOutOfBoundsException(
						"Only 0 index available for this ListItem, not " + index);
			}
		}

		@Override
		public Object getElem(int index) {
			checkIndex(index);
			return arg0;
		}

		@Override
		public void setElem(int index, Object object) {
			checkIndex(index);
			arg0 = object;
		}

		@Override
		public String toString() {
			return "{" + arg0 + '}';
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 67 * hash + Objects.hashCode(this.arg0);
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
			final ListItem1 other = (ListItem1) obj;
			if (!Objects.equals(this.arg0, other.arg0)) {
				return false;
			}
			return true;
		}

	}

	/**
	 * An SSListItem with 2 Objects.
	 */
	private static class ListItem2 implements ListItemWrite0 {
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
	private static class ListItem3 implements ListItemWrite0 {
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
	private static class ListItemAsArray implements ListItemWrite0 {
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
