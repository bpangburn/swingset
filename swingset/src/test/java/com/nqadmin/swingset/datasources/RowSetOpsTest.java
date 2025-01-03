/* *****************************************************************************
 * Copyright (C) 2024, Ernie R Rael. All rights reserved.
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
 * ****************************************************************************/
package com.nqadmin.swingset.datasources;


import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import javax.sql.RowSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.mock.H2;
import com.nqadmin.swingset.mock.NavigateHook;
import com.nqadmin.swingset.utils.SSComponentInterface;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RowSetOpsTest.
 */
public class RowSetOpsTest
{
	
	/** x */
	public RowSetOpsTest()
	{
	}
	
	/** x */
	@BeforeAll
	public static void setUpClass()
	{
	}
	
	/** x */
	@AfterAll
	public static void tearDownClass()
	{
	}
	
	/** x */
	@BeforeEach
	public void setUp()
	{
	}
	
	/** x */
	@AfterEach
	public void tearDown()
	{
	}

	/**
	 * Check out that we can use an embedded H2 for our tests.
	 * @throws Exception 
	 */
	@Test
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void testDB() throws Exception
	{
		System.out.println("DB");
		RowSet rs = H2.getRowSet(null);
		rs.setCommand("SELECT * FROM tbl");
		@SuppressWarnings("UnusedAssignment")
		NavigateHook nav = new NavigateHook(rs);

		Object fetch;
		// Expected from the database
		Date d1 = Date.from(LocalDate.of(2000, 1, 11).atStartOfDay(
				ZoneId.systemDefault()).toInstant());
		Time t1 = java.sql.Time.valueOf(LocalTime.of(11, 11, 11));
		Date d2 = Date.from(LocalDate.of(2000, 2, 22).atStartOfDay(
				ZoneId.systemDefault()).toInstant());
		Time t2 = java.sql.Time.valueOf(LocalTime.of(22, 22, 22));

		fetch = RowSetOps.getColumnObject(RSC.getEx(rs, 4));
		assertEquals(d1, fetch);

		fetch = RowSetOps.getColumnObject(RSC.getEx(rs, 5));
		assertEquals(t1, fetch);

		rs = H2.getRowSet("""
            CREATE TABLE tbl
            (
            	c_date DATE,
            	c_time TIME,
				c_timestamp TIMESTAMP
            );

			INSERT INTO tbl VALUES	('2000-01-11','11:11:11','2222-01-11'),
									('2000-02-22','22:22:22','2222-02-22') ;
            """);
		rs.setCommand("SELECT * FROM tbl");
		nav = new NavigateHook(rs);

		assertEquals(2, nav.rowCount());
		fetch = RowSetOps.getColumnObject(RSC.getEx(rs, 1));
		assertEquals(d1, fetch);
		fetch = RowSetOps.getColumnObject(RSC.getEx(rs, 2));
		assertEquals(t1, fetch);

		nav.next();
		fetch = RowSetOps.getColumnObject(RSC.getEx(rs, 1));
		assertEquals(d2, fetch);
		fetch = RowSetOps.getColumnObject(RSC.getEx(rs, 2));
		assertEquals(t2, fetch);

		if (Boolean.FALSE) {
			nav.go(2);
		}

		Timestamp ts = (Timestamp) rs.getObject(3);
		LocalDateTime ldt = RowSetOps.getColumnObject(RSC.getEx(rs, 3),
													  LocalDateTime.class);
		Timestamp ts2 = Timestamp.valueOf(ldt);
		assertEquals(ts, ts2);
		System.err.println("");
	}

	/**
	 * Test of getColumnObject method, of class RowSetOps.
	 * @throws java.lang.Exception
	 */
	// TODO: more testGetColumnObject_RSC_Class cases
	@Test
	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "ResultOfObjectAllocationIgnored"})
	public void testGetColumnObject_RSC_Class() throws Exception
	{
		System.out.println("getColumnObject");

		RowSet rs = H2.getRowSet(null);
		rs.setCommand("SELECT * FROM tbl");
		new NavigateHook(rs);

		Object fetch;
		Timestamp fetch2;
		fetch = RowSetOps.getColumnObject(RSC.getEx(rs, "c_date"));
		fetch2 = RowSetOps.getColumnObject(RSC.getEx(rs, "c_date"), Timestamp.class);
		assertTrue(fetch instanceof java.sql.Date);
		assertTrue(fetch2 instanceof java.sql.Timestamp);
		assertEquals(fetch, new Date(fetch2.getTime()));
	}

	RowSet g_rs;
	NavigateHook g_nav;

	private void updateColumnText(String col, String sVal, Object val)
			throws Exception
	{
		System.out.println("    " + col);
		SSComponentInterface comp = new SSTextField(g_rs, col);
		RowSetOps.updateColumnText(comp, sVal);
		g_nav.commit();
		Object co = RowSetOps.getColumnObject(comp);
		//assertTrue(co.getClass() == val.getClass());
		if (val instanceof BigDecimal bd)
			assertEquals(bd.compareTo((BigDecimal) co), 0);
		else
			assertEquals(val, co);
	}

	/**
	 * Test of updateColumnText method, of class RowSetOps.
	 * @throws java.lang.Exception
	 */
	@Test
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void testUpdateColumnText() throws Exception
	{
		System.out.println("updateColumnText");
		RowSet rs;
		NavigateHook nav;

		g_rs = H2.getRowSet("""
            CREATE TABLE tbl
            (
                c_pk INTEGER DEFAULT nextval('tbl_seq') NOT NULL PRIMARY KEY,

            	c_integer integer,
            	c_smallint smallint,
            	c_tinyint tinyint,
            	c_bigint bigint,
            	c_decimal decimal(10,5),
            	c_numeric numeric,

            	c_real real,
            	c_double double,
            	c_float float,

            	c_boolean boolean,
            	// c_bit bit,

            	c_char char(3),
            	c_varchar varchar,

            	// c_longvarchar longvarchar,
            	// c_nvarchar nvarchar,
            	// c_longnvarchar longnvarchar

            	c_nchar nchar(5)
            );

			INSERT INTO tbl VALUES
				(1,
                    1, 1, 1, 1, 1, 1,
                    1.0, 1.0, 1.0,
                    false,
                    'aaa', 'a', 'a'
                    )
			;
            """);
		g_rs.setCommand("SELECT * FROM tbl");
		g_nav = new NavigateHook(g_rs);

		updateColumnText("c_integer", "13", 13);
		updateColumnText("c_smallint", "14", 14);
		updateColumnText("c_tinyint", "15", 15);
		updateColumnText("c_bigint", "16", 16L);
		updateColumnText("c_decimal", "17.1", new BigDecimal("17.1"));
		updateColumnText("c_numeric", "18", new BigDecimal("18"));

		updateColumnText("c_real", "19.3", 19.3F);
		updateColumnText("c_double", "20.3", 20.3);
		updateColumnText("c_float", "21.3", 21.3);

		updateColumnText("c_boolean", "true", true);

		updateColumnText("c_char", "one", "one");
		updateColumnText("c_varchar", "two", "two");
		updateColumnText("c_nchar", "three", "three");

		g_rs = null;
		g_nav = null;

		// Date types

		rs = H2.getRowSet("""
            CREATE TABLE tbl
            (
                c_pk INTEGER DEFAULT nextval('tbl_seq') NOT NULL PRIMARY KEY,
            	c_date DATE,
            	c_time TIME,
				c_timestamp TIMESTAMP
            );

			INSERT INTO tbl VALUES	(1, '2000-01-11','11:11:11','2000-01-11 11:11:11'),
									(2, '2000-02-22','22:22:22','2000-02-22 22:22:22') ;
            """);
		rs.setCommand("SELECT * FROM tbl");
		nav = new NavigateHook(rs);

		String sDate = "2222-02-22";
		String sTime = "12:12:12";
		String sTimestamp = "2222-02-22 22:22:22";
		SSComponentInterface comp1 = new SSTextField(rs, "c_date");
		SSComponentInterface comp2 = new SSTextField(rs, "c_time");
		SSComponentInterface comp3 = new SSTextField(rs, "c_timestamp");
		RowSetOps.updateColumnText(comp1, sDate);
		RowSetOps.updateColumnText(comp2, sTime);
		RowSetOps.updateColumnText(comp3, sTimestamp);
		nav.commit();

		Object co;
		co = RowSetOps.getColumnObject(comp1);
		assertEquals(java.sql.Date.valueOf(sDate), co);
		co = RowSetOps.getColumnObject(comp2);
		assertEquals(java.sql.Time.valueOf(sTime), co);
		co = RowSetOps.getColumnObject(comp3);
		assertEquals(java.sql.Timestamp.valueOf(sTimestamp), co);
	}




/**
	 * Test of getColumnObject method, of class RowSetOps.
	 * @throws java.lang.Exception
	 */
	//@Test
	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "ResultOfObjectAllocationIgnored"})
	public void testGetColumnObject_RSC() throws Exception
	{
		System.out.println("getColumnObject");
		RSC comp = null;
		Object expResult = null;
		Object result = RowSetOps.getColumnObject(comp);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of updateColumnObject method, of class RowSetOps.
	 * @throws java.lang.Exception
	 */
	//@Test
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void testUpdateColumnObject() throws Exception
	{
		System.out.println("updateColumnObject");
		SSComponentInterface comp = null;
		Object _updatedValue = null;
		RowSetOps.updateColumnObject(comp, _updatedValue);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	


//	/**
//	 * Test of insertRow method, of class RowSetOps.
//	 */
//	@Test
//	public void testInsertRow() throws Exception
//	{
//		System.out.println("insertRow");
//		ResultSet _resultSet = null;
//		RowSetOps.insertRow(_resultSet);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of updateRow method, of class RowSetOps.
//	 */
//	@Test
//	public void testUpdateRow() throws Exception
//	{
//		System.out.println("updateRow");
//		ResultSet _resultSet = null;
//		RowSetOps.updateRow(_resultSet);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of deleteRow method, of class RowSetOps.
//	 */
//	@Test
//	public void testDeleteRow() throws Exception
//	{
//		System.out.println("deleteRow");
//		ResultSet _resultSet = null;
//		RowSetOps.deleteRow(_resultSet);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getColumnCount method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetColumnCount() throws Exception
//	{
//		System.out.println("getColumnCount");
//		ResultSet _resultSet = null;
//		int expResult = 0;
//		int result = RowSetOps.getColumnCount(_resultSet);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getColumnIndex method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetColumnIndex() throws Exception
//	{
//		System.out.println("getColumnIndex");
//		ResultSet _resultSet = null;
//		String _columnName = "";
//		int expResult = 0;
//		int result = RowSetOps.getColumnIndex(_resultSet, _columnName);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getColumnName method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetColumnName() throws Exception
//	{
//		System.out.println("getColumnName");
//		ResultSet _resultSet = null;
//		int _columnIndex = 0;
//		String expResult = "";
//		String result = RowSetOps.getColumnName(_resultSet, _columnIndex);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of isNullable method, of class RowSetOps.
//	 */
//	@Test
//	public void testIsNullable_ResultSet_int()
//	{
//		System.out.println("isNullable");
//		ResultSet _resultSet = null;
//		int _columnIndex = 0;
//		Optional<Boolean> expResult = null;
//		Optional<Boolean> result = RowSetOps.isNullable(_resultSet, _columnIndex);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of isNullable method, of class RowSetOps.
//	 */
//	@Test
//	public void testIsNullable_ResultSet_String()
//	{
//		System.out.println("isNullable");
//		ResultSet _resultSet = null;
//		String _columnName = "";
//		Optional<Boolean> expResult = null;
//		Optional<Boolean> result = RowSetOps.isNullable(_resultSet, _columnName);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getColumnArray method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetColumnArray()
//	{
//		System.out.println("getColumnArray");
//		SSComponentInterface comp = null;
//		Array expResult = null;
//		Array result = RowSetOps.getColumnArray(comp);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getColumnObjectLegacy method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetColumnObjectLegacy() throws Exception
//	{
//		System.out.println("getColumnObjectLegacy");
//		RSC comp = null;
//		Object expResult = null;
//		Object result = RowSetOps.getColumnObjectLegacy(comp);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getColumnText method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetColumnText()
//	{
//		System.out.println("getColumnText");
//		SSComponentInterface comp = null;
//		String expResult = "";
//		String result = RowSetOps.getColumnText(comp);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getColumnObjectText method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetColumnObjectText()
//	{
//		System.out.println("getColumnObjectText");
//		RSC comp = null;
//		String expResult = "";
//		String result = RowSetOps.getColumnObjectText(comp);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getColumnType method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetColumnType_ResultSet_int() throws Exception
//	{
//		System.out.println("getColumnType");
//		ResultSet _resultSet = null;
//		int _columnIndex = 0;
//		int expResult = 0;
//		int result = RowSetOps.getColumnType(_resultSet, _columnIndex);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getJDBCColumnType method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetJDBCColumnType_ResultSet_int() throws Exception
//	{
//		System.out.println("getJDBCColumnType");
//		ResultSet _resultSet = null;
//		int _columnIndex = 0;
//		JDBCType expResult = null;
//		JDBCType result = RowSetOps.getJDBCColumnType(_resultSet, _columnIndex);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getColumnType method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetColumnType_ResultSet_String() throws Exception
//	{
//		System.out.println("getColumnType");
//		ResultSet _resultSet = null;
//		String _columnName = "";
//		int expResult = 0;
//		int result = RowSetOps.getColumnType(_resultSet, _columnName);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getJDBCColumnType method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetJDBCColumnType_ResultSet_String() throws Exception
//	{
//		System.out.println("getJDBCColumnType");
//		ResultSet _resultSet = null;
//		String _columnName = "";
//		JDBCType expResult = null;
//		JDBCType result = RowSetOps.getJDBCColumnType(_resultSet, _columnName);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getClassColumnType method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetClassColumnType_ResultSet_String() throws Exception
//	{
//		System.out.println("getClassColumnType");
//		ResultSet _resultSet = null;
//		String _columnName = "";
//		Class expResult = null;
//		Class result = RowSetOps.getClassColumnType(_resultSet, _columnName);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getClassColumnType method, of class RowSetOps.
//	 */
//	@Test
//	public void testGetClassColumnType_ResultSet_int() throws Exception
//	{
//		System.out.println("getClassColumnType");
//		ResultSet _resultSet = null;
//		int _columnIndex = 0;
//		Class expResult = null;
//		Class result = RowSetOps.getClassColumnType(_resultSet, _columnIndex);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of updateColumnArray method, of class RowSetOps.
//	 */
//	@Test
//	public void testUpdateColumnArray() throws Exception
//	{
//		System.out.println("updateColumnArray");
//		SSComponentInterface comp = null;
//		SSArray _updatedValue = null;
//		RowSetOps.updateColumnArray(comp, _updatedValue);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of checkForceConflict method, of class RowSetOps.
//	 */
//	@Test
//	public void testCheckForceConflict() throws Exception
//	{
//		System.out.println("checkForceConflict");
//		SSComponentInterface comp = null;
//		String _updatedValue = "";
//		RowSetOps.checkForceConflict(comp, _updatedValue);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of updateColumnObjectDirect method, of class RowSetOps.
//	 */
//	@Test
//	public void testUpdateColumnObjectDirect_4args() throws Exception
//	{
//		System.out.println("updateColumnObjectDirect");
//		RowSet _rowSet = null;
//		int _columnIndex = 0;
//		Object _value = null;
//		JDBCType type = null;
//		RowSetOps.updateColumnObjectDirect(_rowSet, _columnIndex, _value, type);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of updateColumnObjectDirect method, of class RowSetOps.
//	 */
//	@Test
//	public void testUpdateColumnObjectDirect_3args() throws Exception
//	{
//		System.out.println("updateColumnObjectDirect");
//		RowSet _rowSet = null;
//		int _columnIndex = 0;
//		Object _value = null;
//		RowSetOps.updateColumnObjectDirect(_rowSet, _columnIndex, _value);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of updateColumnObject1 method, of class RowSetOps.
//	 */
//	@Test
//	public void testUpdateColumnObject1() throws Exception
//	{
//		System.out.println("updateColumnObject1");
//		RowSet _rowSet = null;
//		int _columnIndex = 0;
//		Object _value = null;
//		RowSetOps.updateColumnObject1(_rowSet, _columnIndex, _value);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of updateColumnObject2 method, of class RowSetOps.
//	 */
//	@Test
//	public void testUpdateColumnObject2() throws Exception
//	{
//		System.out.println("updateColumnObject2");
//		RowSet _rowSet = null;
//		int _columnIndex = 0;
//		Object _value = null;
//		JDBCType type = null;
//		RowSetOps.updateColumnObject2(_rowSet, _columnIndex, _value, type);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
	
}
