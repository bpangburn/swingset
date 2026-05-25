/* *****************************************************************************
 * Copyright (C) 2024-2026, Ernie R Rael. All rights reserved.
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
import java.util.logging.Logger;

import javax.sql.RowSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.mock.H2;
import com.nqadmin.swingset.mock.TestLogging;
import com.nqadmin.swingset.mock.Util;
import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSComponent;

import static com.nqadmin.swingset.utils.SSUtils.getLoggerName;
import static com.nqadmin.swingset.utils.SSUtils.isJunit;
import static java.util.logging.Level.INFO;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RowSetOpsTest.
 */
public class RowSetOpsTest
{
	private static final Logger LOG = Logger.getLogger(getLoggerName());
	
	/** x */
	public RowSetOpsTest()
	{
	}
	
	/** x */
	@BeforeAll
	public static void setUpClass()
	{
		isJunit();	// Make sure it's set; when using invokeLater, can be missed.
		TestLogging.load();
		Util.initLookup();
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

	private RowsModel getRowsModel(RowSet rs)
	{
		return RowsModel.create(rs, null);
	}

	/**
	 * Check out that we can use an embedded H2 for our tests.
	 * @throws Exception 
	 */
	@Test
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void testDB() throws Exception
	{
		LOG.log(INFO, "TEST: DB");
		RowSet rs = H2.getRowSetCleanDB(null);
		rs.setCommand("SELECT * FROM tbl");
		RowsModel rowsModel = getRowsModel(rs);

		Object fetch;
		// Expected from the database
		Date d1 = Date.from(LocalDate.of(2000, 1, 11).atStartOfDay(
				ZoneId.systemDefault()).toInstant());
		Time t1 = java.sql.Time.valueOf(LocalTime.of(11, 11, 11));
		Date d2 = Date.from(LocalDate.of(2000, 2, 22).atStartOfDay(
				ZoneId.systemDefault()).toInstant());
		Time t2 = java.sql.Time.valueOf(LocalTime.of(22, 22, 22));

		fetch = RowSetOps.getColumnObject(RSC.getEx(rowsModel, 4));
		assertEquals(d1, fetch);

		fetch = RowSetOps.getColumnObject(RSC.getEx(rowsModel, 5));
		assertEquals(t1, fetch);

		rs = H2.getRowSetCleanDB("""
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
		rowsModel = getRowsModel(rs);

		assertEquals(2, rowsModel.getRowCount());
		fetch = RowSetOps.getColumnObject(RSC.getEx(rowsModel, 1));
		assertEquals(d1, fetch);
		fetch = RowSetOps.getColumnObject(RSC.getEx(rowsModel, 2));
		assertEquals(t1, fetch);

		rowsModel.next();
		fetch = RowSetOps.getColumnObject(RSC.getEx(rowsModel, 1));
		assertEquals(d2, fetch);
		fetch = RowSetOps.getColumnObject(RSC.getEx(rowsModel, 2));
		assertEquals(t2, fetch);

		if (Boolean.FALSE) {
			rowsModel.setRow(2);
		}

		Timestamp ts = (Timestamp) rs.getObject(3);
		LocalDateTime ldt = RowSetOps.getColumnObject(RSC.getEx(rowsModel, 3),
													  LocalDateTime.class);
		Timestamp ts2 = Timestamp.valueOf(ldt);
		assertEquals(ts, ts2);
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
		LOG.log(INFO, "TEST: getColumnObject");

		RowSet rs = H2.getRowSetCleanDB(null);
		rs.setCommand("SELECT * FROM tbl");
		RowsModel rowsModel = getRowsModel(rs);

		Object fetch;
		Timestamp fetch2;
		fetch = RowSetOps.getColumnObject(RSC.getEx(rowsModel, "c_date"));
		fetch2 = RowSetOps.getColumnObject(RSC.getEx(rowsModel, "c_date"), Timestamp.class);
		assertTrue(fetch instanceof java.sql.Date);
		assertTrue(fetch2 instanceof java.sql.Timestamp);
		assertEquals(fetch, new Date(fetch2.getTime()));
	}

	RowsModel g_rm;

	@SuppressWarnings("LoggerStringConcat")
	private void updateColumnText(String col, String sVal, Object val)
			throws Exception
	{
		LOG.log(INFO, "    " + col);
		SSComponent comp = new SSTextField(g_rm, col);
		RowSetOps.updateColumnText(comp, sVal);
		g_rm.commit();
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
		LOG.log(INFO, "TEST: updateColumnText");
		RowSet rs;
		RowsModel rowsModel;

		rs = H2.getRowSetCleanDB("""
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
		rs.setCommand("SELECT * FROM tbl");
		g_rm = getRowsModel(rs);

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

		//g_rs = null;
		//g_nav = null;

		// Date types
		rs = H2.getRowSetCleanDB("""
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
		rowsModel = getRowsModel(rs);

		String sDate = "2222-02-22";
		String sTime = "12:12:12";
		String sTimestamp = "2222-02-22 22:22:22";
		SSComponent comp1 = new SSTextField(rowsModel, "c_date");
		SSComponent comp2 = new SSTextField(rowsModel, "c_time");
		SSComponent comp3 = new SSTextField(rowsModel, "c_timestamp");
		RowSetOps.updateColumnText(comp1, sDate);
		RowSetOps.updateColumnText(comp2, sTime);
		RowSetOps.updateColumnText(comp3, sTimestamp);
		rowsModel.commit();

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
		LOG.log(INFO, "TEST: getColumnObject");
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
		LOG.log(INFO, "TEST: updateColumnObject");
		SSComponent comp = null;
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
//		LOG.log(INFO, "insertRow");
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
//		LOG.log(INFO, "updateRow");
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
//		LOG.log(INFO, "deleteRow");
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
//		LOG.log(INFO, "getColumnCount");
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
//		LOG.log(INFO, "getColumnIndex");
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
//		LOG.log(INFO, "getColumnName");
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
//		LOG.log(INFO, "isNullable");
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
//		LOG.log(INFO, "isNullable");
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
//		LOG.log(INFO, "getColumnArray");
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
//		LOG.log(INFO, "getColumnObjectLegacy");
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
//		LOG.log(INFO, "getColumnText");
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
//		LOG.log(INFO, "getColumnObjectText");
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
//		LOG.log(INFO, "getColumnType");
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
//		LOG.log(INFO, "getJDBCColumnType");
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
//		LOG.log(INFO, "getColumnType");
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
//		LOG.log(INFO, "getJDBCColumnType");
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
//		LOG.log(INFO, "getClassColumnType");
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
//		LOG.log(INFO, "getClassColumnType");
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
//		LOG.log(INFO, "updateColumnArray");
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
//		LOG.log(INFO, "checkForceConflict");
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
//		LOG.log(INFO, "updateColumnObjectDirect");
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
//		LOG.log(INFO, "updateColumnObjectDirect");
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
//		LOG.log(INFO, "updateColumnObject1");
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
//		LOG.log(INFO, "updateColumnObject2");
//		RowSet _rowSet = null;
//		int _columnIndex = 0;
//		Object _value = null;
//		JDBCType type = null;
//		RowSetOps.updateColumnObject2(_rowSet, _columnIndex, _value, type);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
	
}
