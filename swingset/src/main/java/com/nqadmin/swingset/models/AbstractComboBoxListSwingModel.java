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

import java.awt.Component;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

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
 * methods to modify the list; getRemodel and sub-classes provide that.
 * This class implements MutableComboBoxModel; it installs into either
 * JList or JComboBox, adjusting as needed.
 * <p>
 * Where possible, this class and subclasses name methods
 * similarly to the List interface, such as "add*", "remove*".
 * <p>
 * The encapsulated data can be thought of as a two dimensional
 * array [ height X width ]; the height is the size of {@code List<SSListItem>},
 * the width is the number of elements in a SSListItem .
 * The number of elements in an SSListItem is controlled by a property,
 * see {@link #setItemNumElems};
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
 * <h2>Remodel</h2>
 * Inspections and modifications of the item list, and of its SSListItems,
 * are done through a Remodel Object see {@link #getRemodel() }. The
 * remodel object is "try with resource" compatible and subclasses
 * may implement locking in their implementations of takeWriteLock()
 * and releaseWriteLock(). See {@link GlazedListsOptionMappingInfo}
 * for an example.
 * <p>
 * Compatible with GlazedLists AutoComplete feature;
 * in which case an EventList is set in the constructor.
 * 
 * @see GlazedListsOptionMappingInfo SSDBComboBox
 *		for use with GlazedLists AutoComplete feature
 * @since 4.0.0
 */
//
// There is an issue that arises from modifying the contents of a list item;
// in this case, the list itself is not modified. There is a fireContentsChanged
// event. This all is good when this is used as a model in a JComponent.
// The setElem method is the only one that would modify a list item in place.
//
// However, when this is used to mangage a GlazedList, there is no event
// listener. Glazed listens to changes in the EventList which this manages.
// When working on a glazed list, a newListItem must be created and then
// list.set(index, newListItem) and that notifies the event list. Note that
// it is generally insufficient to do something like
//     listItem = list.get(index)
//     modify-listItem
//     list.set(index, listItem)
// becase if the set is optimized by currentListItem.equals(setListItem)
// that will say that nothing is changed. So must create a new list item.
// 
public abstract class AbstractComboBoxListSwingModel extends DefaultComboBoxModel<SSListItem> {
	private static final long serialVersionUID = 1L;

	/** when true, handle combo box selected item (about the events) */
	private boolean comboBoxModel;
	/** this has been installed into a JComponent */
	private boolean installed;
	/** do not modify list items in place, create new use set */
	private final boolean modifyListItemWithSet = true;
	
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
	 */
	// validElemsMask = (1 << nElem) - 1
	// if (!(validElemsMask & (1 << index))) error
	private int validElemsMask;


	/**
	 * The list of SSListItem elements.
	 */
	private final List<SSListItem> itemList;

	/**
	 * A read only list of SSListItem elements.
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
	private final List<WeakReference<ItemElementSlice>> createdLists = new ArrayList<>();

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
	 * Used for testing to set combo handling flag.
	 * @param _itemNumElems number of elements in an SSListItem
	 * @param _isCombo in a combo box
	 */
	/*package-test*/ AbstractComboBoxListSwingModel(int _itemNumElems, boolean _isCombo) {
		this(_itemNumElems);
		
		comboBoxModel = _isCombo;
	}

	/*package-test*/ boolean isComboBoxModel() {
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
	// Installation
	// Make sure there's a SSListItemFormat,
	// and install CellRenderer that uses it.
	//
	// TODO: uninstall
	//

	/**
	 * Installs a ListCellRenderer into the JComponent which
	 * uses {@link #getListItemFormat() }
	 * to get the value to render. The renderer is either 
	 * a {@code DefaultListCellRenderer} or a {@code  BasicComboBoxRenderer}
	 * as appropriate.
	 * <p>
	 * The model is installed into the JComponent as a convenience.
	 * 
	 * @param _jc Jcomponent to set up with model; must be JList or JComboBox
	 * @param _model associated model
	 */
	public static void install(JComponent _jc, AbstractComboBoxListSwingModel _model) {
		install(_jc, _model, null);
	}

	/**
	 * Installs the specified ListCellRenderer into the JComponent. 
	 * The model is installed into the JComponent as a convenience.
	 * 
	 * @param _jc Jcomponent to set up with model
	 * @param _model associated model
	 * @param _render list cell renderer
	 * @throws IllegalArgumentException if _jc is not JList or JComboBox
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	// TODO Remove warning suppression post Java 8.
	public static void install(JComponent _jc, AbstractComboBoxListSwingModel _model,
			ListCellRenderer<?> _render) {
		Objects.requireNonNull(_jc);
		Objects.requireNonNull(_model);
		if(_model.installed) {
			throw new IllegalStateException("model already installed");
		}

		_model.installed = true;

		if (_jc instanceof JList) {
			ListCellRenderer<?> render = _render == null
					? _model.new LocalListCellRenderer() : _render;
			((JList) _jc).setCellRenderer(render);
			((JList) _jc).setModel(_model);
		} else if (_jc instanceof JComboBox) {
			ListCellRenderer<?> render = _render == null
					? _model.new LocalComboBoxCellRenderer() : _render;
			((JComboBox) _jc).setRenderer(render);
			((JComboBox) _jc).setModel(_model);
			_model.comboBoxModel = true;
		} else {
			throw new IllegalArgumentException("must be JList or JComboBox");
		}
	}

	private SSListItemFormat listItemFormat;

	/**
	 * Set the format to use with this model.
	 * 
	 * @param _listItemFormat the format used with this model
	 */
	public void setListItemFormat(SSListItemFormat _listItemFormat) {
		listItemFormat = _listItemFormat;
	}

	/**
	 * Return the listItemFormat associated with this model.
	 * @return the associated listItemFormat
	 */
	public SSListItemFormat getListItemFormat() {
		if (listItemFormat == null) {
			listItemFormat = new SSListItemFormat();
		}
		return listItemFormat;
	}

	/**
	 * Cell renderer that works with a SSListItemFormat.
	 */
	protected class LocalListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent( JList<?> list,
				Object value, int index, boolean isSelected, boolean cellHasFocus) {
			String stringValue = getListItemFormat().format(value);
			return super.getListCellRendererComponent(list, stringValue, index, isSelected, cellHasFocus);
		}
	}

	/**
	 * Cell renderer that works with a SSListItemFormat.
	 */
	protected class LocalComboBoxCellRenderer extends BasicComboBoxRenderer {
		private static final long serialVersionUID = 1L;

		// In following, "JList<?> list" gets an error with 1.8 compiler,
		// and is OK with j-14 compiler. I remember the old one
		// has some inference issues with nested classes
		@SuppressWarnings("rawtypes")
		// TODO Remove warning suppression post Java 8.
		@Override
		public Component getListCellRendererComponent( JList list,
				Object value, int index, boolean isSelected, boolean cellHasFocus) {
			String stringValue = getListItemFormat().format(value);
			return super.getListCellRendererComponent(list, stringValue, index, isSelected, cellHasFocus);
		}
	}

	//////////////////////////////////////////////////////////////////////////
	//
	// Swing Model methods
	//

	// ListModel

	/** {@inheritDoc } */
	@Override
	public int getSize() {
		try (Remodel remodel = getRemodel()) {
			return itemList.size();
		}
	}

	/** {@inheritDoc } */
	@Override
	public SSListItem getElementAt(int index) {
		try (Remodel remodel = getRemodel()) {
			if (comboBoxModel) {
				// The DefaultComboBoxModel never throws an exception
				// Curiously, the DefaultListModel for this same method
				// does throw an exception.
				if ( index >= 0 && index < itemList.size() )
					return itemList.get(index);
				return null;
			}
			return itemList.get(index);
		}
	}

	// ComboBoxModel

	private SSListItem selectedObject;

	/** {@inheritDoc } */
	@Override
	public void setSelectedItem(Object anItem) {
		// TODO: exception if not combo?
		try (Remodel remodel = getRemodel()) {
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
	}

	/** {@inheritDoc } */
	@Override
	public SSListItem getSelectedItem() {
		// TODO: exception if not combo?
        return selectedObject;
	}

	// MutableComboBoxModel
	//		NOTE the helper methods for keeping selection
	//			comboAdjustSelectedAfterAdd and comboAdjustSelectedForRemove

	/** {@inheritDoc } */
	@Override
	public void addElement(SSListItem item) {
		try (Remodel remodel = getRemodel()) {
			remodel.add(item);
		}
	}

	/** {@inheritDoc } */
	@Override
	public void insertElementAt(SSListItem item, int index) {
		try (Remodel remodel = getRemodel()) {
			remodel.add(index, item);
		}
	}

	/** {@inheritDoc } */
	@Override
	public void removeElement(Object obj) {
		try (Remodel remodel = getRemodel()) {
			remodel.remove(obj);
		}
		if (!(obj instanceof SSListItem)) {
			logger.warn(() -> "ComboBox#removeElement(" + obj + ") not SSListItem");
		}
	}

	/** {@inheritDoc } */
	@Override
	public void removeElementAt(int index) {
		try (Remodel remodel = getRemodel()) {
			remodel.remove(index);
		}
	}

	// Methods from DefaultComboBoxModel (make sure Vector never gets referenced)

	// TODO: Add @Override and remove SuppressWarnings annotation post Java 8
	@SuppressWarnings({"all","javadoc"})
	/** {@inheritDoc} */
	// @Override not in jdk1.8
	public void addAll(int index, Collection<? extends SSListItem> c) {
		try (Remodel remodel = getRemodel()) {
			remodel.addAll(index, c);
		}
	}

	// TODO: Add @Override and remove SuppressWarnings annotation post Java 8
	@SuppressWarnings({"all","javadoc"})
	/** {@inheritDoc} */
	// @Override not in jdk1.8
	public void addAll(Collection<? extends SSListItem> c) {
		try (Remodel remodel = getRemodel()) {
			remodel.addAll(c);
		}
	}

	/** {@inheritDoc } */
	@Override
	public void removeAllElements() {
		try (Remodel remodel = getRemodel()) {
			remodel.clear();
		}
	}

	/** {@inheritDoc } */
	@Override
	public int getIndexOf(Object anObject) {
		try (Remodel remodel = getRemodel()) {
			return remodel.indexOf(anObject);
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
	private void comboAdjustSelectedAfterAdd(int oldSize) {
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
	 * @return number of active/referenced list slices
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

		/**
		 * state info
		 * @param _elemIndex elem index
		 * @param _isValid true if it's valid
		 */
		public SliceInfo(int _elemIndex, boolean _isValid) {
			elemIndex = _elemIndex;
			isValid = _isValid;
		}

		/**
		 * debug
		 * @return string
		 */
		@Override
		public String toString() {
			return "SliceInfo{" + "elemIndex=" + elemIndex + ", isValid=" + isValid + '}';
		}
	}
	/**
	 * Some state of slice. Not used. Package access for testing.
	 * @param l slice
	 * @return state info
	 */
	SliceInfo sliceInfo(List<Object> l) {
		if(l instanceof ItemElementSlice) {
			ItemElementSlice slice = (ItemElementSlice) l;
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
	private static Object getElem(SSListItem _listItem, int _elemIndex) {
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

	private boolean internalAddAll(Collection<? extends SSListItem> newItems) {
		// first new item goes here
		int oldSize = itemList.size();
		boolean isChanged = itemList.addAll(newItems);
		if (isChanged) {
			fireIntervalAdded(this, oldSize, itemList.size()-1);
			comboAdjustSelectedAfterAdd(oldSize);
		}
		return isChanged;
	}

	private boolean internalAddAll(int index, Collection<? extends SSListItem> newItems) {
		boolean isChanged = itemList.addAll(index, newItems);
		int oldSize = itemList.size();
		if (isChanged) {
			fireIntervalAdded(this, index, index + newItems.size() - 1);
			comboAdjustSelectedAfterAdd(oldSize);
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
	// Could just fire everything changed if that's needed,
	// but what about glazed lists...
	private Object setElem(int _listItemIndex, int _elemIndex, Object _newElem) {
		ListItemWrite0 listItem = (ListItemWrite0) itemList.get(_listItemIndex);
		if (modifyListItemWithSet) {
			try {
				listItem = (ListItemWrite0) listItem.clone();
			} catch (CloneNotSupportedException ex) {
			}
		}
		Object oldElem = listItem.getElem(_elemIndex);
		listItem.setElem(_elemIndex, _newElem);
		if (modifyListItemWithSet) {
			itemList.set(_listItemIndex, listItem);
		}
		fireContentsChanged(this, _listItemIndex, _listItemIndex);
		return oldElem;
	}
	
	/**
	 * for testing 
	 * @param _listItem clone this
	 * @return clone
	 */
	SSListItem getClone(SSListItem _listItem) {
		try {
			return (SSListItem) ((ListItemWrite0)_listItem).clone();
		} catch (CloneNotSupportedException ex) {
		}
		return null;
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

		private boolean isShadow(AbstractComboBoxListSwingModel model) {
			return AbstractComboBoxListSwingModel.this == model;
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
	 * Create a list slice of the item list.There is no checking on the
 element index.<p>
	 * If a returned element slice is not valid according to the
	 * itemNumElems property, {@link #setItemNumElems},
	 * then an attempt to use that slice
	 * causes an exception. An element slice becomes valid/invalid
	 * dynamically as itemNumElems changes.
	 * @param <T> list type
	 * @param elemIndex position in {@code SSListItem} of elements
	 * @return list of elements at the specified position
	 */
	protected <T>List<T> createElementSlice(int elemIndex) {
		if (elemIndex < 0) {
			throw new IllegalArgumentException("elemIndex must be positive");
		}
		ItemElementSlice el = new ItemElementSlice(elemIndex);
		createdLists.add(new WeakReference<>(el));
		@SuppressWarnings("unchecked")
		List<T> l = (List<T>) el;
		return l;
	}

	/**
	 * Determine if the argument list is a slice of this item list.
	 * A slice is a live list of an SSListItem element;
	 * it is backed by an item list.
	 * <p>
	 * Note that if the list is a slice from a different item list, 
	 * then false is returned.
	 * @param list check this list
	 * @return true if the specified list is backed by this
	 */
	public boolean hasShadow(List<?> list) {
		if (list instanceof ItemElementSlice) {
			return ((ItemElementSlice)list).isShadow(this);
		}
		return false;
	}

	/**
	 * This method checks if the specified list is
	 * a shadow of this itemlist.
	 * If it is a shadow, then a copy of the list is created.
	 * @see #hasShadow(java.util.List) 
	 * @param <T> type of list element
	 * @param list list to check
	 * @return a list disconnected from the item list.
	 */
	public <T> List<T> getDisconnectedList(List<T> list) {
		return hasShadow(list) ? new ArrayList<>(list) : list;
	}

	/**
	 * For debug.
	 * @return the item list as a string
	 */
	public String dump() {
		return itemList.stream().collect(StringBuilder::new,
				StringBuilder::append, StringBuilder::append).toString();
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
	 * It is anticipated that subclass may want to use Remodel
	 * as part of a locking scheme for multi-threaded
	 * access to AbstractComboBoxListSwingModel.
	 * The {@link #verifyOpened()}
	 * method is useful for that.
	 * Typically methods in a subclass invoke
	 * super.someMethod which does {@code verifyOpened()}.
	 * If a method in a subclass directly modifies
	 * the item list or its contents it should call verifyOpened
	 * as its first statement; this avoids modifications
	 * after the lock is released.
	 * 
	 * @see GlazedListsOptionMappingInfo for example of Remodel locking
	 */
	protected abstract class Remodel implements AutoCloseable {

		/** has this been closed? Error if access after close */
		protected boolean isClosed = false;

		// /** if optimized indexOfItem, following means must rebuild optimizations */
		// protected boolean isModifiedLength = false;

		/**
		 * This is invoked during construction, be careful.
		 * If there is no locking, implement an empty method
		 */
		protected abstract void takeWriteLock();

		/**
		 * This is invoked during close.
		 * If there is no locking, implement an empty method
		 */
		protected abstract void releaseWriteLock();

		/** a Remodel */
		// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
		@SuppressWarnings({"all","OverridableMethodCallInConstructor"})
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
		public boolean addAll(Collection<? extends SSListItem> _newItems) {
			verifyOpened();
			return AbstractComboBoxListSwingModel.this.internalAddAll(_newItems);
			// isModifiedLength = true;
		}

		/**
		 * Appends all of the list items in the specified list
		 * to the end of this list.
		 * 
		 * @param _index insert the items at this position in the list
		 * @param _newItems items to add to this list.
		 * @return true if the list changed
		 */
		public boolean addAll(int _index, Collection<? extends SSListItem> _newItems) {
			verifyOpened();
			return AbstractComboBoxListSwingModel.this.internalAddAll(_index, _newItems);
			// isModifiedLength = true;
		}

		/**
		 * Replaces the item at the specified position in the list
		 * with the specified item.
		 * @param _index index of the item to replace
		 * @param _newItem item to store at the index
		 * @return the list item that was at that position
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
		 * Remove the listItem from ite itemList.
		 * @param _listItem item to remove from item list
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
			return AbstractComboBoxListSwingModel.getElem(_listItem, _elemIndex);
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
		
		// TODO: Remove SuppressWarnings annotation post Java 8
		@SuppressWarnings({"all","javadoc"})
		/** {@inheritDoc} */
		Object clone() throws CloneNotSupportedException;
	}

	private interface ListItemWrite0 extends ListItem0, Cloneable {
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
	private static class ListItem1 implements ListItemWrite0, Cloneable {
		Object arg0;

		@SuppressWarnings("unused")
		// TODO Unused warning is a false positive. Used by reflection.
		public ListItem1(Object[] elems) {
			arg0 = elems[0];
		}

		private static void checkIndex(int index) {
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
			return Objects.equals(this.arg0, other.arg0);
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

	}

	/**
	 * An SSListItem with 2 Objects.
	 */
	private static class ListItem2 implements ListItemWrite0, Cloneable {
		Object arg0;
		Object arg1;

		@SuppressWarnings("unused")
		// TODO Unused warning is a false positive. Used by reflection.
		public ListItem2(Object[] elems) {
			arg0 = elems[0];
			arg1 = elems[1];
		}

		private static void checkIndex(int index) {
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
			return Objects.equals(this.arg1, other.arg1);
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

	}

	/**
	 * An SSListItem with 3 Objects.
	 */
	private static class ListItem3 implements ListItemWrite0, Cloneable {
		Object arg0;
		Object arg1;
		Object arg2;

		@SuppressWarnings("unused")
		// TODO Unused warning is a false positive. Used by reflection.
		public ListItem3(Object[] elems) {
			arg0 = elems[0];
			arg1 = elems[1];
			arg2 = elems[2];
		}

		private static void checkIndex(int index) {
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
			switch (index) {
			case 0:  arg0 = object; break;
			case 1:  arg1 = object; break;
			default: arg2 = object; break;
			}
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
			return Objects.equals(this.arg2, other.arg2);
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

	}

	// This can be used for an arbitrary, but fixed, number of items
	// in an SSListItem. Typically 4 or more items.
	private static class ListItemAsArray implements ListItemWrite0, Cloneable {
		Object[] elems;

		@SuppressWarnings("unused")
		// TODO Unused warning is a false positive. Used by reflection.
		public ListItemAsArray(Object [] _elems) {
			elems = Arrays.copyOf(_elems, _elems.length);
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
			hash = 83 * hash + Arrays.deepHashCode(elems);
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
			return Arrays.deepEquals(elems, other.elems);
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			 ListItemAsArray clone = (ListItemAsArray) super.clone();
			 clone.elems = elems.clone();
			 return clone;
		}
	}
}
