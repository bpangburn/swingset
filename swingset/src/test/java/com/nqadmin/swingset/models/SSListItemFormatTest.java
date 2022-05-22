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

import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
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
@SuppressWarnings("javadoc")
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

		listItemOld = listInfo.createListItem(integer, string, date, time, timestamp);
		fmtOld = new SSListItemFormat();
	}
	
	@AfterEach
	public void tearDown() {
	}

	static class LI extends AbstractComboBoxListSwingModel {

		public LI(int itemNumElems, List<SSListItem> itemList) {
			super(itemNumElems, itemList);
		}

		@Override protected void checkState() { }
		@Override protected void remodelTakeWriteLock() { }
		@Override protected void remodelReleaseWriteLock(AbstractComboBoxListSwingModel.Remodel remodel) { }
		@Override protected Remodel getRemodel() { return new RM(); }

		class RM extends AbstractComboBoxListSwingModel.Remodel {
		}
	}

	String string = "everything";
	Float floatnum =  (float)3.14159;
	Integer integer = 42;
	Date date;
	Time time;
	Timestamp timestamp;

	SSListItem listItem;
	SSListItemFormat fmt;

	SSListItem listItemOld;
	SSListItemFormat fmtOld;

	/**
	 * Test of format method, of class SSListItemFormat.
	 * 
	 * This is the only test method, and it tests a superclass method
	 */
	@Test
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void testFormat() {
		System.out.print("format");

		List<SSListItem> itemList = new ArrayList<>();
		LI listInfo = new LI(5, itemList);
		listItem = listInfo.createListItem(integer, floatnum, date, time, timestamp);
		fmt = new SSListItemFormat();

		// format everything in the list item
		fmt.clear();
		fmt.addElemType(0, JDBCType.INTEGER);
		fmt.addElemType(1, JDBCType.FLOAT);
		fmt.addElemType(2, JDBCType.DATE);
		fmt.addElemType(3, JDBCType.TIME);
		fmt.addElemType(4, JDBCType.TIMESTAMP);
		String format = fmt.format(listItem);
		String expect = "42 | 3.14159 | 2021/02/13 | 04:25:26 | 2023/04/15T07:38:39";
		assertEquals(expect, format);

		// use toString by setting null
		fmt.setFormat(JDBCType.DATE, null);
		fmt.setFormat(JDBCType.TIME, null);
		fmt.setFormat(JDBCType.TIMESTAMP, null);
		format = fmt.format(listItem);
		expect = "42 | 3.14159 | 2021-02-13 | 04:25:26 | 2023-04-15 07:38:39.0";
		assertEquals(expect, format);

		// change the order elements get formatted
		// leave something out
		// different format pattern
		fmt.setFormat(JDBCType.DATE,      new SimpleDateFormat("YY"));
		fmt.setFormat(JDBCType.TIME,      new SimpleDateFormat("HH"));
		fmt.setFormat(JDBCType.TIMESTAMP, new SimpleDateFormat("YY*HH"));
		fmt.setSeparator(" @@@ ");
		fmt.clear();
		fmt.addElemType(4, JDBCType.TIMESTAMP);
		fmt.addElemType(1, JDBCType.FLOAT);
		fmt.addElemType(3, JDBCType.TIME);
		fmt.addElemType(2, JDBCType.DATE);
		format = fmt.format(listItem);
		expect = "23*07 @@@ 3.14159 @@@ 04 @@@ 21";
		assertEquals(expect, format);

		// change the order elements get formatted
		// leave something out
		// different format
		// format for INTEGER and FLOAT

		fmt.setFormat(JDBCType.INTEGER, new DecimalFormat("$#0000.00"));
		fmt.setFormat(JDBCType.FLOAT, new DecimalFormat(".00"));
		fmt.setFormat(JDBCType.DATE,      new SimpleDateFormat("YY"));
		fmt.setFormat(JDBCType.TIME,      new SimpleDateFormat("HH"));
		fmt.setFormat(JDBCType.TIMESTAMP, new SimpleDateFormat("YY*HH"));
		fmt.setSeparator(" @@@ ");
		fmt.clear();
		fmt.addElemType(4, JDBCType.TIMESTAMP);
		fmt.addElemType(1, JDBCType.FLOAT);
		fmt.addElemType(0, JDBCType.INTEGER);
		fmt.addElemType(2, JDBCType.DATE);
		format = fmt.format(listItem);
		expect = "23*07 @@@ 3.14 @@@ $0042.00 @@@ 21";
		assertEquals(expect, format);

		// add element format to override the default
		fmt.clear();
		fmt.addElemType(4, JDBCType.TIMESTAMP);
		fmt.addElemType(1, JDBCType.FLOAT, new DecimalFormat(".0000"));
		fmt.addElemType(0, JDBCType.INTEGER, new DecimalFormat("$.00"));
		fmt.addElemType(2, JDBCType.DATE);
		format = fmt.format(listItem);
		expect = "23*07 @@@ 3.1416 @@@ $42.00 @@@ 21";
		assertEquals(expect, format);

		// Check exceptions in Format are handled.
		Format exceptionFormat = new Format() {
			@Override
			public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public Object parseObject(String source, ParsePosition pos) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		};
		// NOTE: TIMESTAMP is formatted with toString()
		// because of exception
		fmt.clear();
		fmt.addElemType(4, JDBCType.TIMESTAMP, exceptionFormat);
		fmt.addElemType(1, JDBCType.FLOAT, new DecimalFormat(".0000"));
		fmt.addElemType(0, JDBCType.INTEGER, new DecimalFormat("$.00"));
		fmt.addElemType(2, JDBCType.DATE);
		format = fmt.format(listItem);
		// TIMESTAMP formatted with toString().
		expect = "2023-04-15 07:38:39.0 @@@ 3.1416 @@@ $42.00 @@@ 21";
		assertEquals(expect, format);

		// Get an exception from the date formatter
		listItem = listInfo.createListItem(true, null, null, null, null);
		fmt.clear();
		fmt.addElemType(0, JDBCType.TIMESTAMP);
		format = fmt.format(listItem);
		expect = "true";
		assertEquals(expect, format);
	}
	/**
	 * Test of setDatePattern method, of class SSListItemFormat.
	 */
	@Test
	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "ThrowableResultIgnored"})
	public void testSetPattern() {
		System.out.print("setDatePattern");
		SSListItemFormat fmt1 = new SSListItemFormat();

		// check initial patterns
		// by setting new pattern
		// and the retrun value is the previous pattern
		String previousPat;

		previousPat = ((SimpleDateFormat)fmt1.setFormat(JDBCType.DATE, null)).toPattern();
		assertEquals(SSListItemFormat.dateDefault, previousPat);
		previousPat = ((SimpleDateFormat)fmt1.setFormat(JDBCType.TIME, null)).toPattern();
		assertEquals(SSListItemFormat.timeDefault, previousPat);
		previousPat = ((SimpleDateFormat)fmt1.setFormat(JDBCType.TIMESTAMP, null)).toPattern();
		assertEquals(SSListItemFormat.timestampDefault, previousPat);
	}

	/**
	 * Test of getPattern method, of class SSListItemFormat.
	 */
	@Test
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void testGetPattern() {
		System.out.print("getDatePattern");
		SSListItemFormat fmt1 = new SSListItemFormat();

		// check initial patterns
		// by getting the patterns
		Format currentFormat;

		currentFormat = fmt1.getFormat(JDBCType.DATE);
		assertEquals(new SimpleDateFormat(SSListItemFormat.dateDefault),
					 currentFormat);
		currentFormat = fmt1.getFormat(JDBCType.TIME);
		assertEquals(new SimpleDateFormat(SSListItemFormat.timeDefault),
					 currentFormat);
		currentFormat = fmt1.getFormat(JDBCType.TIMESTAMP);
		assertEquals(new SimpleDateFormat(SSListItemFormat.timestampDefault),
					 currentFormat);

		// set some arbitrary strings and read them back
		Format f1 = new SimpleDateFormat("yyyy");
		Format f2 = new SimpleDateFormat("HH");
		Format f3 = new SimpleDateFormat("MM");
		fmt1.setFormat(JDBCType.DATE, f1);
		fmt1.setFormat(JDBCType.TIME, f2);
		fmt1.setFormat(JDBCType.TIMESTAMP, f3);

		currentFormat = fmt1.getFormat(JDBCType.DATE);
		assertEquals(currentFormat, f1);
		currentFormat = fmt1.getFormat(JDBCType.TIME);
		assertEquals(currentFormat, f2);
		currentFormat = fmt1.getFormat(JDBCType.TIMESTAMP);
		assertEquals(currentFormat, f3);
	}

	/**
	 * Test of format method, of class SSListItemFormat.
	 * 
	 * This is the only test method, and it tests a superclass method
	 */
	@Test
	@SuppressWarnings({"deprecation", "UseOfSystemOutOrSystemErr"})
	public void testFormatDeprecated() {
		System.out.print("formatDeprecated");

		// format everything in the list item
		fmtOld.clear();
		fmtOld.addElemType(0, JDBCType.INTEGER);
		fmtOld.addElemType(1, JDBCType.VARCHAR);
		fmtOld.addElemType(2, JDBCType.DATE);
		fmtOld.addElemType(3, JDBCType.TIME);
		fmtOld.addElemType(4, JDBCType.TIMESTAMP);
		String format = fmtOld.format(listItemOld);
		String expect = "42 | everything | 2021/02/13 | 04:25:26 | 2023/04/15T07:38:39";
		assertEquals(expect, format);

		// use toString by setting null
		fmtOld.setPattern(JDBCType.DATE, null);
		fmtOld.setPattern(JDBCType.TIME, null);
		fmtOld.setPattern(JDBCType.TIMESTAMP, null);
		format = fmtOld.format(listItemOld);
		expect = "42 | everything | 2021-02-13 | 04:25:26 | 2023-04-15 07:38:39.0";
		assertEquals(expect, format);

		// change the order elements get formatted
		// leave something out
		// different format pattern
		fmtOld.setPattern(JDBCType.DATE, "YY");
		fmtOld.setPattern(JDBCType.TIME, "HH");
		fmtOld.setPattern(JDBCType.TIMESTAMP, "YY*HH");
		fmtOld.setSeparator(" @@@ ");
		fmtOld.clear();
		fmtOld.addElemType(4, JDBCType.TIMESTAMP);
		fmtOld.addElemType(1, JDBCType.VARCHAR);
		fmtOld.addElemType(3, JDBCType.TIME);
		fmtOld.addElemType(2, JDBCType.DATE);
		format = fmtOld.format(listItemOld);
		expect = "23*07 @@@ everything @@@ 04 @@@ 21";
		assertEquals(expect, format);
	}
	/**
	 * Test of setDatePattern method, of class SSListItemFormat.
	 */
	@Test
	@SuppressWarnings({"deprecation", "UseOfSystemOutOrSystemErr", "ThrowableResultIgnored"})
	public void testSetPatternDeprecated() {
		System.out.print("setDatePatternDeprecated");
		SSListItemFormat fmt1 = new SSListItemFormat();

		assertThrows(IllegalArgumentException.class,
				() -> fmt1.setPattern(JDBCType.REF, null));

		// check initial patterns
		// by setting new pattern
		// and the retrun value is the previous pattern
		String previousPat;

		previousPat = fmt1.setPattern(JDBCType.DATE, null);
		assertEquals(SSListItemFormat.dateDefault, previousPat);
		previousPat = fmt1.setPattern(JDBCType.TIME, null);
		assertEquals(SSListItemFormat.timeDefault, previousPat);
		previousPat = fmt1.setPattern(JDBCType.TIMESTAMP, null);
		assertEquals(SSListItemFormat.timestampDefault, previousPat);
	}

	/**
	 * Test of getPattern method, of class SSListItemFormat.
	 */
	@Test
	@SuppressWarnings({"deprecation", "UseOfSystemOutOrSystemErr", "ThrowableResultIgnored"})
	public void testGetPatternDeprecated() {
		System.out.print("getDatePatternDeprecated");
		SSListItemFormat fmt1 = new SSListItemFormat();

		assertThrows(IllegalArgumentException.class,
				() -> fmt1.getPattern(JDBCType.REF));

		// check initial patterns
		// by getting the patterns
		String currentPattern;

		currentPattern = fmt1.getPattern(JDBCType.DATE);
		assertEquals(SSListItemFormat.dateDefault, currentPattern);
		currentPattern = fmt1.getPattern(JDBCType.TIME);
		assertEquals(SSListItemFormat.timeDefault, currentPattern);
		currentPattern = fmt1.getPattern(JDBCType.TIMESTAMP);
		assertEquals(SSListItemFormat.timestampDefault, currentPattern);

		// set some arbitrary strings and read them back
		fmt1.setPattern(JDBCType.DATE, "yyyy");
		fmt1.setPattern(JDBCType.TIME, "HH");
		fmt1.setPattern(JDBCType.TIMESTAMP, "MM");

		currentPattern = fmt1.getPattern(JDBCType.DATE);
		assertEquals(currentPattern, "yyyy");
		currentPattern = fmt1.getPattern(JDBCType.TIME);
		assertEquals(currentPattern, "HH");
		currentPattern = fmt1.getPattern(JDBCType.TIMESTAMP);
		assertEquals(currentPattern, "MM");
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
	// 	AbstractComboBoxListSwingModel.ListItem0 _listItem = null;
	// 	SSListItemFormat instance = new SSListItemFormat();
	// 	instance.appendValue(_sb, _elemIndex, _listItem);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }
	
}
