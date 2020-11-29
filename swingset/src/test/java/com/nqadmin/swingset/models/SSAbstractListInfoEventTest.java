/*
 * Portions created by Ernie Rael are
 * Copyright (C) 2020 Ernie Rael.  All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Ernie Rael <errael@raelity.com>
 */
package com.nqadmin.swingset.models;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * This is a copy of SSAbstractListInfo just for events.
 * @author err
 */
public class SSAbstractListInfoEventTest {
	
	public SSAbstractListInfoEventTest() {
	}
	
	@BeforeAll
	public static void setUpClass() {
	}
	
	@AfterAll
	public static void tearDownClass() {
	}

	static class LI extends SSAbstractListInfo {
		private static final long serialVersionUID = 1L;

		public LI(int itemNumElems, boolean isCombo) {
			super(itemNumElems, isCombo);
		}

		public LI(int itemNumElems) {
			super(itemNumElems);
		}
		public LI(int itemNumElems, List<SSListItem> itemList) {
			super(itemNumElems, itemList);
		}
		@Override protected void checkState() { }
		@Override protected Remodel getRemodel() { return new RM(); }

		class RM extends SSAbstractListInfo.Remodel {
			@Override protected void takeWriteLock() { }
			@Override protected void releaseWriteLock() { }
		}
	}
	
	//
	// At the start of each test create 4 lists
	// and an empty listInfo that handles 3 elements.
	//
	@BeforeEach
	@SuppressWarnings("unchecked")
	public void setUp() {
		clearAll();

		//listInfo = new LI(3);

		LocalDate d2 = LocalDate.of(2021, Month.JANUARY, 1);
		d2.plusDays(1);

		LocalDate d1 = LocalDate.of(2021, Month.JANUARY, 1);
		LocalDateTime dt1 = LocalDateTime.of(2021, Month.JANUARY, 1, 1, 0);
		LocalDate[] d = new LocalDate[]{d1, d1.plusDays(1),
			d1.plusDays(2), d1.plusDays(3), d1.plusDays(4)};
		LocalDateTime[] dt = new LocalDateTime[]{dt1, dt1.plusHours(1),
			dt1.plusHours(2), dt1.plusHours(3), dt1.plusHours(4)};

		// initialze some arrays to work with
		l1.addAll(Arrays.asList(new Integer[] { 1, 2, 3, 4, 5}));
		l2.addAll(Arrays.asList(new String[] { "one", "two", "three", "four", "five"}));
		l3.addAll(Arrays.asList(new Date[] {Date.valueOf(d[0]), Date.valueOf(d[1]),
			Date.valueOf(d[2]), Date.valueOf(d[3]), Date.valueOf(d[4])}));
		l4.addAll(Arrays.asList(new Timestamp[] {Timestamp.valueOf(dt[0]),
			Timestamp.valueOf(dt[1]), Timestamp.valueOf(dt[2]),
			Timestamp.valueOf(dt[3]), Timestamp.valueOf(dt[4])}));

		// something to catch events
		events = new ArrayList<>();
	}
	
	@AfterEach
	public void tearDown() {
		clearAll();
	}

	void clearAll() {
		itemList.clear();
		l1.clear();
		l2.clear();
		l3.clear();
		l4.clear();
	}

	// The item list
	List<SSListItem> itemList = new ArrayList<>();

	// 4 lists, "original", of the same length
	List<Integer> l1 = new ArrayList<>();
	List<String> l2 = new ArrayList<>();
	List<Date> l3 = new ArrayList<>();
	List<Timestamp> l4 = new ArrayList<>();

	List<String> events;

	// return an aray for ListItem creation
	// n is how many elments in list item, index is which original
	private Object [] liCreateArray(int n, int index) {
		Object[] o = new Object[] {l1.get(index), l2.get(index), l3.get(index), l4.get(index)};
		return Arrays.copyOf(o, n);
	}


	List<SSListItem> liCreateMany(int n, LI listInfo) {
		List<SSListItem> items = new ArrayList<>();
		for (int i = 0; i < l1.size(); i++) {
			items.add(listInfo.createListItem(liCreateArray(n, i)));
		}
		return items;
	}

	private void addListener(LI listInfo) {
		events.clear();
		listInfo.addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e) {
				events.add(e.toString());
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
				events.add(e.toString());
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				events.add(e.toString());
			}
		});
	}

	void expectEvent(int type, int lower, int upper) {
		String lde = new ListDataEvent(this, type, lower, upper).toString();
		assertTrue(events.remove(lde));
	}

	static Stream<LI> generateLI3() {
		return Arrays.stream(new LI[] {
			new LI(4, false),
			new LI(4, true)});
	}

	static int CHANGED = 0;
	static int ADDED = 1;
	static int REMOVED = 2;

	/**
	 * This isn't event related,
	 * but this method behaves differently depending on iscombo, is default
	 */
	@ParameterizedTest
	@MethodSource("generateLI3")
	public void testGetElementAt(LI li) {
		addListener(li);
		// don't throw an exception if old semantics
		if (li.isComboBoxModel()) {
			li.getElementAt(0);
		}
		else {
			assertThrows(IndexOutOfBoundsException.class,
					() -> li.getElementAt(0));
		}
		assertTrue(events.isEmpty());

		try (LI.Remodel remodel = li.getRemodel()) {
			List<SSListItem> items = liCreateMany(li.getItemNumElems(), li);
			remodel.addAll(items);
			SSListItem item2 = remodel.get(2);
			assertEquals(items.get(2).toString(), item2.toString());
		}
	}

	/**
	 * SetSelectedItem, GetSelectedItem and add and addAll
	 * NOTE: if not combo box, setSelectedItem does nothing
	 *			getSelectedItem is always null
	 * @param li 
	 */
	@ParameterizedTest
	@MethodSource("generateLI3")
	public void testSetSelectedItem(LI li) {
		addListener(li);
		try (LI.Remodel remodel = li.getRemodel()) {
			SSListItem item;

			List<SSListItem> items = liCreateMany(li.getItemNumElems(), li);

			remodel.add(items.get(2));
			expectEvent(ADDED, 0, 0);
			// after adding to an empty combo, first item gets selected
			if (li.isComboBoxModel()) {
				expectEvent(CHANGED, -1, -1);
				assertTrue(li.getSelectedItem() != null);
			} else {
				assertTrue(li.getSelectedItem() == null);
			}
			assertTrue(events.isEmpty());

			remodel.clear();
			expectEvent(REMOVED, 0, 0);
			// should lose selection,
			// but default combobox model doesn't send event,
			// so neither do we
			// if (li.isComboBoxModel()) {
			// 	expectEvent(CHANGED, -1, -1);
			// }
			assertTrue(li.getSelectedItem() == null);
			assertTrue(events.isEmpty());

			remodel.addAll(items);
			expectEvent(ADDED, 0, 4);
			// after adding to an empty combo, first item gets selected
			if (li.isComboBoxModel()) {
				expectEvent(CHANGED, -1, -1);
			}
			assertTrue(events.isEmpty());

			// verify the first item got automatically selected
			item = li.getSelectedItem();
			if (li.isComboBoxModel()) {
				assertEquals(items.get(0).toString(), item.toString());
			} else {
				assertEquals(null, item);
			}

			item = items.get(2);

			// set/get item 2
			li.setSelectedItem(item);
			if (li.isComboBoxModel()) {
				expectEvent(CHANGED, -1, -1);
			}
			assertTrue(events.isEmpty());

			SSListItem selectedItem = li.getSelectedItem();
			if (li.isComboBoxModel()) {
				assertEquals(item.toString(), selectedItem.toString());
			} else {
				assertEquals(null, selectedItem);
			}
			assertTrue(events.isEmpty());
		}
	}

	/**
	 * Test exception if used after close.
	 * Test exception if modify itemlist.
	 */
	@Test
	public void testRemodelClose() {
		LI li = new LI(4, false);
		LI.Remodel remodel = li.getRemodel();

		assertTrue(remodel.isEmpty());

		// can't modify the list
		List<SSListItem> il = remodel.getItemList();
		assertThrows(UnsupportedOperationException.class, () -> il.add(null));

		remodel.close();
		// don't touch after close
		assertThrows(IllegalStateException.class, () -> remodel.isEmpty());
	}
	
	/**
	 * Test add(obj), add(int,obj) both to non empty list
	 * and a little more addall.
	 * @param li 
	 */
	@Test
	public void testAdd() {
		LI li = new LI(4);
		addListener(li);
		try (LI.Remodel remodel = li.getRemodel()) {
			// split list into 2 + 1-item + 2 
			List<SSListItem> items1 = liCreateMany(li.getItemNumElems(), li);
			List<SSListItem> items2 = liCreateMany(li.getItemNumElems(), li);
			items1 = new ArrayList<>(items1.subList(0, 2));
			items2.removeAll(items1);
			SSListItem itemMiddle = items2.remove(0);

			remodel.addAll(items1);
			expectEvent(ADDED, 0, 1);
			// after adding to an empty combo, first item gets selected
			// Just a list, not combo
			// if (li.isComboBoxModel()) {
			// 	expectEvent(CHANGED, -1, -1);
			// }
			assertTrue(events.isEmpty());
			assertEquals(2, remodel.getItemList().size());

			remodel.addAll(items2);
			expectEvent(ADDED, 2, 3);
			assertTrue(events.isEmpty());
			assertEquals(4, remodel.getItemList().size());

			remodel.add(3, itemMiddle);
			expectEvent(ADDED, 3, 3);
			assertTrue(events.isEmpty());
			assertEquals(5, remodel.getItemList().size());

			remodel.clear();
			expectEvent(REMOVED, 0, 4);
		}

		assertTrue(events.isEmpty());
	}
	
	/**
	 * Test both flavors of remove.
	 * Test set (replace)
	 * 
	 * @param li 
	 */
	@Test
	public void testRemove() {
		LI li = new LI(4);
		addListener(li);
		try (LI.Remodel remodel = li.getRemodel()) {
			List<SSListItem> items = liCreateMany(li.getItemNumElems(), li);
			remodel.addAll(items);
			events.clear();

			SSListItem item = items.get(1);
			SSListItem itemRemoved = remodel.remove(1);
			assertEquals(item, itemRemoved);
			expectEvent(REMOVED, 1, 1);
			// list contains 1,3,4,5
			assertTrue(events.isEmpty());

			remodel.add(2, itemRemoved);
			expectEvent(ADDED, 2, 2);
			// list 1,3,2,4,5
			assertTrue(events.isEmpty());

			List<SSListItem> itemsCopy = new ArrayList<>(items);
			item = itemsCopy.remove(1);
			itemsCopy.add(2, item);
			assertEquals(itemsCopy, remodel.getItemList());

			itemRemoved = remodel.remove(1); // 3
			// list 1,2,4,5
			events.clear();

			remodel.set(2, itemRemoved); // replace 4 with 3
			// list 1,2,3,5
			expectEvent(CHANGED , 2, 2);
			itemsCopy = new ArrayList<>(items);
			itemsCopy.remove(3); // 4
			assertEquals(itemsCopy, remodel.getItemList());

			item = items.get(1); // 2
			remodel.remove(item);
			// list 1,3,5
			itemsCopy.remove(1);
			assertEquals(itemsCopy, remodel.getItemList());
		}
	}
	 /**
	  * Test selected change if the item is removed
	  * 
	  * The algorithm seems to be,
	  * <pre>
	  * if the selected item is being removed,
	  *		if it is the first item in the list
	  *			then select the item following it, (the new first item)
	  *		otherwise select the item in front of it (smaller index)
	  * </pre>
	  */
	
	@ParameterizedTest
	@MethodSource("generateLI3")
	public void testRemoveSelected(LI li) {
		addListener(li);
		try (LI.Remodel remodel = li.getRemodel()) {
			List<SSListItem> items = liCreateMany(li.getItemNumElems(), li);
			remodel.addAll(items);
			events.clear();

			SSListItem item = remodel.get(2); // 3
			li.setSelectedItem(item);
			SSListItem selectedItem = li.getSelectedItem();
			//assertEquals(li.isComboBoxModel() ? item : null, selectedItem);
			if(li.isComboBoxModel()) {
				assertEquals(item, selectedItem);
				expectEvent(CHANGED, -1, -1);
			} else {
				assertEquals(null, selectedItem);
			}
			assertTrue(events.isEmpty());

			events.clear();
			remodel.remove(item);
			// list 1,2,4,5
			selectedItem = li.getSelectedItem();
			item = items.get(1); // 2
			expectEvent(REMOVED, 2, 2);
			if(li.isComboBoxModel()) {
				assertEquals(item, selectedItem);
				expectEvent(CHANGED, -1, -1);
			} else {
				assertEquals(null, selectedItem);
			}
			assertTrue(events.isEmpty());
			

			item = items.get(4); // 5
			li.setSelectedItem(item);
			selectedItem = li.getSelectedItem();
			assertEquals(li.isComboBoxModel() ? item : null, selectedItem);
			events.clear();
			remodel.remove(item); // remove 5
			// list 1,2,4 - 4 is selected
			item = items.get(3); // 4
			selectedItem = li.getSelectedItem();
			expectEvent(REMOVED, 3, 3);
			if(li.isComboBoxModel()) {
				assertEquals(item, selectedItem);
				expectEvent(CHANGED, -1, -1);
			} else {
				assertEquals(null, selectedItem);
			}
			assertTrue(events.isEmpty());

			item = items.get(0); // 1
			li.setSelectedItem(item);
			selectedItem = li.getSelectedItem();
			assertEquals(li.isComboBoxModel() ? item : null, selectedItem);
			events.clear();
			remodel.remove(item); // remove 1
			// list 2,4 - 2 is selected
			item = items.get(1); // 2
			selectedItem = li.getSelectedItem();
			expectEvent(REMOVED, 0, 0);
			if(li.isComboBoxModel()) {
				assertEquals(item, selectedItem);
				expectEvent(CHANGED, -1, -1);
			} else {
				assertEquals(null, selectedItem);
			}
			assertTrue(events.isEmpty());

			List<SSListItem> itemsCopy = new ArrayList<>();
			itemsCopy.add(items.get(1)); // 2
			itemsCopy.add(items.get(3)); // 4
			assertEquals(itemsCopy, remodel.getItemList());
		}
	}
	
	/**
	 * Test set Elem in list item
	 * @param li 
	 */
	@Test
	public void testSetElem() {
		LI li = new LI(4);
		addListener(li);
		try (LI.Remodel remodel = li.getRemodel()) {
			List<SSListItem> items = liCreateMany(li.getItemNumElems(), li);
			remodel.addAll(items);
			events.clear();

			remodel.setElem(3, 1, null);
			expectEvent(CHANGED, 3, 3);
		}
		assertTrue(events.isEmpty());
	}
	
	// @ParameterizedTest
	// @MethodSource("generateLI3")
	// public void testXXX(LI li) {
	// 	addListener(li);
	// 	try (LI.Remodel remodel = li.getRemodel()) {
	// 		List<SSListItem> items = liCreateMany(li.getItemNumElems(), li);
	// 		remodel.addAll(items);
	// 	}
	// 	assertTrue(events.isEmpty());
	// }


}
