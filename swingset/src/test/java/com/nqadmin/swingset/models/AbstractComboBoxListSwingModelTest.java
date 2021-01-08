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
//import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 * @author err
 */
@SuppressWarnings("javadoc")
public class AbstractComboBoxListSwingModelTest {
	
	public AbstractComboBoxListSwingModelTest() {
	}
	
	@BeforeAll
	public static void setUpClass() {
	}
	
	@AfterAll
	public static void tearDownClass() {
	}

	@SuppressWarnings("serial")
	static class LI extends AbstractComboBoxListSwingModel {
		ComboBoxModelProxy proxy;

		public LI(int itemNumElems, List<SSListItem> itemList) {
			super(itemNumElems, itemList);
			proxy = getProxyJunitTextOnly();
		}

		@Override protected void checkState() { }
		@Override protected void remodelTakeWriteLock() { }
		@Override protected void remodelReleaseWriteLock(AbstractComboBoxListSwingModel.Remodel remodel) { }
		@Override protected Remodel getRemodel() { return new RM(); }

		class RM extends AbstractComboBoxListSwingModel.Remodel {
		}
	}
	
	//
	// At the start of each test create 4 lists
	// and an empty listInfo that handles 3 elements.
	//
	@BeforeEach
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setUp() {
		clearAll();

		listInfo = new LI(3, itemList);
		l1Shadow = (List)listInfo.createElementSlice(0);
		l2Shadow = (List)listInfo.createElementSlice(1);
		l3Shadow = (List)listInfo.createElementSlice(2);
		l4Shadow = (List)listInfo.createElementSlice(3);

		// divide to get milliseconds from nanosec
		//long day = TimeUnit.DAYS.convert(1, TimeUnit.DAYS) / (1000 * 1000);
		//Instant now = Instant.now();
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

	LI listInfo;

	// The item list
	List<SSListItem> itemList = new ArrayList<>();

	// 4 lists, "original", of the same length
	List<Integer> l1 = new ArrayList<>();
	List<String> l2 = new ArrayList<>();
	List<Date> l3 = new ArrayList<>();
	List<Timestamp> l4 = new ArrayList<>();

	List<Integer> l1Shadow;
	List<String> l2Shadow;
	List<Date> l3Shadow;
	List<Timestamp> l4Shadow;

	// return an aray for ListItem creation
	// n is how many elments in list item, index is which original
	private Object [] liCreateArray(int n, int index) {
		Object[] o = new Object[] {l1.get(index), l2.get(index), l3.get(index), l4.get(index)};
		return Arrays.copyOf(o, n);
	}


	List<SSListItem> liCreateMany(int n) {
		List<SSListItem> items = new ArrayList<>();
		for (int i = 0; i < l1.size(); i++) {
			items.add(listInfo.createListItem(liCreateArray(n, i)));
		}
		return items;
	}

	/**
	 * Test of isEmpty method, of class AbstractComboBoxListSwingModel.
	 */
	@Test
	public void testIsEmpty() {
		System.out.println("isEmpty");
		//boolean expResult = true;
		boolean result = listInfo.getItemList().isEmpty();
		assertEquals(true, result);
		SSListItem li = listInfo.createListItem(liCreateArray(3,0));
		itemList.add(li);
		result = listInfo.getItemList().isEmpty();
		assertEquals(false, result);
	}

	/**
	 * Test of indexOfItem method, of class AbstractComboBoxListSwingModel.
	 */
	@Test
	public void testIndexOfItem() {
		System.out.println("indexOfItem");
		itemList.addAll(liCreateMany(3)); // 3 elems in list item
		int expResult = 2; 
		// check that equals works (not just ==)
		SSListItem li = listInfo.createListItem(liCreateArray(3, expResult));
		int result = listInfo.getItemList().indexOf(li);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getItemNumElems method, of class AbstractComboBoxListSwingModel.
	 */
	@Test
	public void testGetItemNumElems() {
		System.out.println("getItemNumElems");
		int expResult = 3;
		int result = listInfo.getItemNumElems();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setItemNumElems method, of class AbstractComboBoxListSwingModel.
	 */
	@Test
	public void testSetItemNumElems() {
		System.out.println("setItemNumElems");
		itemList.addAll(liCreateMany(3)); // 3 elems in list item
		// can't change nElem when stuff in list
		assertThrows(IllegalArgumentException.class, () -> listInfo.setItemNumElems(2));

		// With 3 elems, these two should NOT throw exceptions
		l3Shadow.size();
		l3Shadow.get(2);

		// check that slices are disabled/enabled appropriately

		// With 3 elems, these two should throw exceptions
		assertThrows(IllegalAccessError.class, () -> l4Shadow.size());
		assertThrows(IllegalAccessError.class, () -> l4Shadow.get(2));


		itemList.clear();
		listInfo.setItemNumElems(2);
		assertEquals(2, listInfo.getItemNumElems());

		// can't create 3 items, only 2 ok now
		assertThrows(IllegalArgumentException.class,
				() -> listInfo.createListItem(liCreateArray(3,0)));

		// can now create 2 element list items
		itemList.addAll(liCreateMany(2));

		// check that slices disabled/enabled changes appropriately

		// With 2 elems, these two should NOT throw exceptions
		l2Shadow.size();
		l2Shadow.get(2);

		// With 2 elems, these two should throw exceptions
		assertThrows(IllegalAccessError.class, () -> l3Shadow.size());
		assertThrows(IllegalAccessError.class, () -> l3Shadow.get(2));
	}

	/**
	 * Test of createListItem method, of class AbstractComboBoxListSwingModel.
	 */
	@Test
	public void testCreateListItem() {
		System.out.println("createListItem");
		//
		// Check out 5 elems in a listitem, other cases 2,3 are handled
		// This is testing the array version of SSListItem
		//
		listInfo.setItemNumElems(5);
		Object [] elems5 = new Object[] {"a", "b", "c", "d", "e"};
		SSListItem result = listInfo.createListItem(elems5);
		Object [] elems6 = new Object[] {"a", "b", "c", "d", "e", "f"};
		assertThrows(IllegalArgumentException.class, () -> listInfo.createListItem(elems6));

		// 
		SSListItem result2 = listInfo.createListItem(elems5);
		assertTrue(result.equals(result2));

		Object [] elems5a = new Object[] {"a", "b", "c", "d", "a"};
		result2 = listInfo.createListItem(elems5a);
		assertFalse(result.equals(result2));
	}

	private static boolean getElemEquals(Object[] elems, int listItemIndex, LI.Remodel remodel) {
		for (int i = 0; i < elems.length; i++) {
			if(!elems[i].equals(remodel.getElem(listItemIndex, i))) {
				return false;
			}
		}
		return true;
	}

	// number of listItems for GetSet test
	private static int nGetSetItems = 6;
	/**
	 * For each type of SSListItem
	 * Test of getElem method, of class SSAbstractListInfo.
	 * AND
	 * Test equals for each list item type
	 * AND
	 * Test of setElem method, of class SSAbstractListInfo.
	 */
	@ParameterizedTest
	@MethodSource
	public void testGetSetElem(LI linfo) {
		System.err.println("" + linfo);
		// calculate expected item element value, slice is 0 - n-1
		@SuppressWarnings("unused")
		BiFunction<Integer, Integer, Integer> f = (slice, listIndex) -> slice*10 + listIndex;
		// test equals/get/set for item index 3
		int testItemIndex = 3;

		// elements for list item length 7
		Integer[] elems = new Integer[] { 03, 13, 23, 33, 43, 53, 63 };
		// truncate elements for the list item we're dealing with
		int nElem = linfo.getItemNumElems();
		elems = Arrays.copyOf(elems, nElem);

		try (LI.Remodel remodel = linfo.getRemodel()) {
			// verify the test item has the expected elements
			assertTrue(getElemEquals(elems, testItemIndex, remodel));

			SSListItem testItem = linfo.createListItem((Object[])elems);
			SSListItem originalListItem = remodel.get(testItemIndex);
			assertTrue(testItem.equals(originalListItem));

			int modElemIndex = 2 % nElem;
			// create a testItem that won't match what's in the item list
			elems[modElemIndex] = 99;
			testItem = linfo.createListItem((Object[])elems);
			assertFalse(testItem.equals(originalListItem));

			// modify the element in the list item, should match now
			// note, using the same reference for item in the list
			remodel.setElem(testItemIndex, modElemIndex, 99);
			// should be a clone, so must refetch
			SSListItem newListItem = remodel.get(testItemIndex);
			assertTrue(testItem.equals(newListItem));

			// try the clone directly
			SSListItem cloneListItem = linfo.getClone(newListItem);
			assertTrue(newListItem.equals(cloneListItem));
			assertTrue(newListItem != cloneListItem);


			// change the item to 70,71,72,73
			for (int i = 0; i < nElem; i++) {
				elems[i] = 70 + i;
				remodel.setElem(testItemIndex, i, 70 + i);
			}

			// Read back the same items
			for (int i = 0; i < nElem; i++) {
				assertEquals(elems[i], remodel.getElem(testItemIndex, i));
			}

			// test some get/set exceptions
			assertThrows(ArrayIndexOutOfBoundsException.class,
					() -> remodel.setElem(testItemIndex, nElem, null));
			assertThrows(ArrayIndexOutOfBoundsException.class,
					() -> remodel.getElem(testItemIndex, nElem));
		}
	}
	static Stream<LI> testGetSetElem() {
		List<LI> listInfos = new ArrayList<>();
		// Gererate 4 LI, num elements 1,2,3,4, All elements are Integer
		// This tests the four ListItem implementations
		// Each slice is x0, x1, x2, x3, x4, x5; where x is 0,1,2,3
		for (int nElem = 1; nElem <= 7; nElem++) {
			LI listInfo = new LI(nElem, new ArrayList<>());
			try (LI.Remodel remodel = listInfo.getRemodel()) {
				for (int listItemIndex = 0; listItemIndex < nGetSetItems; listItemIndex++) {
					Integer[] elems = new Integer[nElem];
					for(int slice = 0; slice < nElem; slice++) {
						elems[slice] = slice * 10 + listItemIndex;
					}
					remodel.add(listInfo.createListItem((Object[])elems));
				}
			}
			listInfos.add(listInfo);
		}
		return listInfos.stream();
	}

	@Test
	public void testBoundaries() {
		assertThrows(IllegalArgumentException.class,
				() -> listInfo.setItemNumElems(0));
		listInfo.setItemNumElems(1);
		listInfo.setItemNumElems(30);
		assertThrows(IllegalArgumentException.class,
				() -> listInfo.setItemNumElems(31));
	}

	/**
	 * Test of createElementSlice method, of class AbstractComboBoxListSwingModel.
	 */
	@Test
	public void testCreateElementSlice() {
		System.out.println("createElementSlice");
		//
		// element slices are somewhat tested, valid/invalid in testSetItemNumElems
		// here test
		//		equals real list, shadow list
		//		unused lists are garbage collected

		itemList.addAll(liCreateMany(3)); // 3 elems in list item
		assertTrue(l1.equals(l1Shadow));
		assertTrue(l2.equals(l2Shadow));
		assertTrue(l3.equals(l3Shadow));

		List<List<?>> lists = new ArrayList<>();
		// create and reference 8 more slices
		for (int i = 0; i < 8; i++) {
			lists.add(listInfo.createElementSlice(i % 4));
		}
		System.gc();
		// The original 4 slices, plus the 8 just created
		assertEquals(12, listInfo.checkCreatedLists());
		lists = null;
		// should only be the original 4 left now
		System.gc();
		assertEquals(4, listInfo.checkCreatedLists());
	}
}
