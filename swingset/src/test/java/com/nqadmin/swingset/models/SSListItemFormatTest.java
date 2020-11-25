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
 * The Original Code is jvi - vi editor clone.
 *
 * Contributor(s): Ernie Rael <errael@raelity.com>
 */
package com.nqadmin.swingset.models;

import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author err
 */
public class SSListItemFormatTest {
	
	public SSListItemFormatTest() {
	}
	
	@BeforeAll
	public static void setUpClass() {
	}
	
	@AfterAll
	public static void tearDownClass() {
	}
	
	@BeforeEach
	public void setUp() {
		LocalDate d1 = LocalDate.of(2021, Month.FEBRUARY, 13);
		date = Date.valueOf(d1);

		LocalTime t = LocalTime.of(4, 25, 26);
		time = Time.valueOf(t);

		LocalDateTime dt1 = LocalDateTime.of(2023, Month.APRIL, 15, 7, 38, 39);
		timestamp = Timestamp.valueOf(dt1);

		List<SSListItem> itemList = new ArrayList<>();

		// 5 items in listItem
		LI listInfo = new LI(5, itemList);
		listItem = listInfo.createListItem(integer, string, date, time, timestamp);
		fmt = new SSListItemFormat();
	}
	
	@AfterEach
	public void tearDown() {
	}

	static class LI extends SSAbstractListInfo {

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

	String string = "everything";
	Integer integer = 42;
	Date date;
	Time time;
	Timestamp timestamp;

	SSListItem listItem;
	SSListItemFormat fmt;

	/**
	 * Test of format method, of class SSListItemFormat.
	 * 
	 * This is the only test method, and it tests a superclass method
	 */
	@Test
	public void testFormat() {
		System.out.println("format");

		// format everything in the list item
		fmt.clear();
		fmt.addElemType(0, JDBCType.INTEGER);
		fmt.addElemType(1, JDBCType.VARCHAR);
		fmt.addElemType(2, JDBCType.DATE);
		fmt.addElemType(3, JDBCType.TIME);
		fmt.addElemType(4, JDBCType.TIMESTAMP);
		String format = fmt.format(listItem);
		String expect = "42 | everything | 2021/02/13 | 04:25:26 | 2023/04/15T07:38:39";
		assertEquals(expect, format);

		// use toString by setting null
		fmt.setPattern(JDBCType.DATE, null);
		fmt.setPattern(JDBCType.TIME, null);
		fmt.setPattern(JDBCType.TIMESTAMP, null);
		format = fmt.format(listItem);
		expect = "42 | everything | 2021-02-13 | 04:25:26 | 2023-04-15 07:38:39.0";
		assertEquals(expect, format);

		// change the order elements get formatted
		// leave something out
		// different format pattern
		fmt.setPattern(JDBCType.DATE, "YY");
		fmt.setPattern(JDBCType.TIME, "HH");
		fmt.setPattern(JDBCType.TIMESTAMP, "YY*HH");
		fmt.setSeparator(" @@@ ");
		fmt.clear();
		fmt.addElemType(4, JDBCType.TIMESTAMP);
		fmt.addElemType(1, JDBCType.VARCHAR);
		fmt.addElemType(3, JDBCType.TIME);
		fmt.addElemType(2, JDBCType.DATE);
		format = fmt.format(listItem);
		expect = "23*07 @@@ everything @@@ 04 @@@ 21";
		assertEquals(expect, format);
	}
	/**
	 * Test of setDatePattern method, of class SSListItemFormat.
	 */
	@Test
	public void testSetPattern() {
		System.out.println("setDatePattern");
		SSListItemFormat fmt = new SSListItemFormat();

		assertThrows(IllegalArgumentException.class,
				() -> fmt.setPattern(JDBCType.REF, null));

		// check initial patterns
		// by setting new pattern
		// and the retrun value is the previous pattern
		String previousPat;

		previousPat = fmt.setPattern(JDBCType.DATE, null);
		assertEquals(SSListItemFormat.dateDefault, previousPat);
		previousPat = fmt.setPattern(JDBCType.TIME, null);
		assertEquals(SSListItemFormat.timeDefault, previousPat);
		previousPat = fmt.setPattern(JDBCType.TIMESTAMP, null);
		assertEquals(SSListItemFormat.timestampDefault, previousPat);
	}

	/**
	 * Test of getPattern method, of class SSListItemFormat.
	 */
	@Test
	public void testGetPattern() {
		System.out.println("getDatePattern");
		SSListItemFormat fmt = new SSListItemFormat();

		assertThrows(IllegalArgumentException.class,
				() -> fmt.getPattern(JDBCType.REF));

		// check initial patterns
		// by getting the patterns
		String currentPattern;

		currentPattern = fmt.getPattern(JDBCType.DATE);
		assertEquals(SSListItemFormat.dateDefault, currentPattern);
		currentPattern = fmt.getPattern(JDBCType.TIME);
		assertEquals(SSListItemFormat.timeDefault, currentPattern);
		currentPattern = fmt.getPattern(JDBCType.TIMESTAMP);
		assertEquals(SSListItemFormat.timestampDefault, currentPattern);

		// set some arbitrary strings and read them back
		fmt.setPattern(JDBCType.DATE, "date");
		fmt.setPattern(JDBCType.TIME, "time");
		fmt.setPattern(JDBCType.TIMESTAMP, "timestamp");

		currentPattern = fmt.getPattern(JDBCType.DATE);
		assertEquals(currentPattern, "date");
		currentPattern = fmt.getPattern(JDBCType.TIME);
		assertEquals(currentPattern, "time");
		currentPattern = fmt.getPattern(JDBCType.TIMESTAMP);
		assertEquals(currentPattern, "timestamp");
	}

	// /**
	//  * Test of clear method, of class SSListItemFormat.
	//  */
	// //@Test
	// public void testClear() {
	// 	System.out.println("clear");
	// 	SSListItemFormat instance = new SSListItemFormat();
	// 	instance.clear();
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of addElemType method, of class SSListItemFormat.
	//  */
	// //@Test
	// public void testAddElemType() {
	// 	System.out.println("addElemType");
	// 	int _elemIndex = 0;
	// 	JDBCType _jdbcType = null;
	// 	SSListItemFormat instance = new SSListItemFormat();
	// 	instance.addElemType(_elemIndex, _jdbcType);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }


	// /**
	//  * Test of getSeparator method, of class SSListItemFormat.
	//  */
	// //@Test
	// public void testGetSeparator() {
	// 	System.out.println("getSeparator");
	// 	SSListItemFormat instance = new SSListItemFormat();
	// 	String expResult = "";
	// 	String result = instance.getSeparator();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setSeparator method, of class SSListItemFormat.
	//  */
	// //@Test
	// public void testSetSeparator() {
	// 	System.out.println("setSeparator");
	// 	String separator = "";
	// 	SSListItemFormat instance = new SSListItemFormat();
	// 	instance.setSeparator(separator);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of parseObject method, of class SSListItemFormat.
	//  */
	// //@Test
	// public void testParseObject() {
	// 	System.out.println("parseObject");
	// 	String source = "";
	// 	ParsePosition pos = null;
	// 	SSListItemFormat instance = new SSListItemFormat();
	// 	Object expResult = null;
	// 	Object result = instance.parseObject(source, pos);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of format method, of class SSListItemFormat.
	//  */
	// //@Test
	// public void testFormat3args() {
	// 	System.out.println("format");
	// 	Object _listItem = null;
	// 	StringBuffer toAppendTo = null;
	// 	FieldPosition pos = null;
	// 	SSListItemFormat instance = new SSListItemFormat();
	// 	StringBuffer expResult = null;
	// 	StringBuffer result = instance.format(_listItem, toAppendTo, pos);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of appendValue method, of class SSListItemFormat.
	//  */
	// //@Test
	// public void testAppendValue() {
	// 	System.out.println("appendValue");
	// 	StringBuffer _sb = null;
	// 	int _elemIndex = 0;
	// 	SSAbstractListInfo.ListItem0 _listItem = null;
	// 	SSListItemFormat instance = new SSListItemFormat();
	// 	instance.appendValue(_sb, _elemIndex, _listItem);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }
	
}
