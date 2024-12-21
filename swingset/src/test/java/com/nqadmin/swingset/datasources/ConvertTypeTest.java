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
import java.sql.JDBCType;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.EnumSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.nqadmin.swingset.datasources.ConvertType.convertObjectType;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author err
 */
public class ConvertTypeTest
{
	
	/** x */
	public ConvertTypeTest()
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

	// TODO: more tests

	/**
	 * Test of verifyConvertToType method, of class ConvertType.
	 */
	@Test
	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "ThrowableResultIgnored"})
	public void testVerifyConvertToType()
	{
		System.out.println("verifyConvertToType");

		EnumSet<JDBCType> allow = null;

		assertDoesNotThrow(()->ConvertType.assertConvertFromJdbcType(
				JDBCType.DATE, Date.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertFromJdbcType(
				JDBCType.DATE, java.sql.Date.class, allow));
		assertThrows(AssertionError.class, ()->ConvertType.assertConvertFromJdbcType(
				JDBCType.DATE, java.sql.Time.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertFromJdbcType(
				JDBCType.DATE, java.sql.Timestamp.class, allow));

		assertDoesNotThrow(()->ConvertType.assertConvertFromJdbcType(
				JDBCType.TIME, Date.class, allow));
		assertThrows(AssertionError.class, ()->ConvertType.assertConvertFromJdbcType(
				JDBCType.TIME, java.sql.Date.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertFromJdbcType(
				JDBCType.TIME, java.sql.Time.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertFromJdbcType(
				JDBCType.TIME, java.sql.Timestamp.class, allow));

		assertDoesNotThrow(()->ConvertType.assertConvertFromJdbcType(
				JDBCType.TIMESTAMP, Date.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertFromJdbcType(
				JDBCType.TIMESTAMP, java.sql.Date.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertFromJdbcType(
				JDBCType.TIMESTAMP, java.sql.Time.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertFromJdbcType(
				JDBCType.TIMESTAMP, java.sql.Timestamp.class, allow));
	}

	/** x
	 * @throws java.lang.Exception */
	@Test
	@SuppressWarnings({"ThrowableResultIgnored", "UseOfSystemOutOrSystemErr"})
	public void testConvertStringToNumber() throws Exception
	{
		System.out.println("convertStringToNumber");
		Object rv;
		
		rv = convertObjectType("123", JDBCType.TINYINT);
		assertEquals(Integer.class, rv.getClass());
		assertEquals(123, rv);
		rv = convertObjectType("123", JDBCType.SMALLINT);
		assertEquals(Integer.class, rv.getClass());
		assertEquals((Integer)123, rv);
		rv = convertObjectType("123", JDBCType.INTEGER);
		assertEquals(Integer.class, rv.getClass());
		assertEquals((Integer)123, rv);
		rv = convertObjectType("123", JDBCType.BIGINT);
		assertEquals(Long.class, rv.getClass());
		assertEquals(123L, rv);
		rv = convertObjectType("123", JDBCType.REAL);
		assertEquals(Float.class, rv.getClass());
		assertEquals(123F, rv);
		rv = convertObjectType("123", JDBCType.FLOAT);
		assertEquals(Double.class, rv.getClass());
		assertEquals(123D, rv);
		rv = convertObjectType("123", JDBCType.DOUBLE);
		assertEquals(Double.class, rv.getClass());
		assertEquals(123D, rv);
		rv = convertObjectType("123", JDBCType.NUMERIC);
		assertEquals(BigDecimal.class, rv.getClass());
		assertEquals(BigDecimal.valueOf(123), rv);
		rv = convertObjectType("123", JDBCType.DECIMAL);
		assertEquals(BigDecimal.class, rv.getClass());
		assertEquals(BigDecimal.valueOf(123), rv);

		// Java types
		rv = convertObjectType("123", Byte.class);
		assertEquals(Byte.class, rv.getClass());
		assertEquals((byte)123, rv);
		rv = convertObjectType("123", Short.class);
		assertEquals(Short.class, rv.getClass());
		assertEquals((short)123, rv);
		rv = convertObjectType("123", Integer.class);
		assertEquals(Integer.class, rv.getClass());
		assertEquals(123, rv);
		rv = convertObjectType("123", Long.class);
		assertEquals(Long.class, rv.getClass());
		assertEquals(123L, rv);
		rv = convertObjectType("123", Float.class);
		assertEquals(Float.class, rv.getClass());
		assertEquals(123F, rv);
		rv = convertObjectType("123", Double.class);
		assertEquals(Double.class, rv.getClass());
		assertEquals(123D, rv);
		rv = convertObjectType("123", BigDecimal.class);
		assertEquals(BigDecimal.class, rv.getClass());
		assertEquals(BigDecimal.valueOf(123), rv);

		// Primitive types ???
		// rv = convertObjectType("123", long.class);
		// assertEquals(Long.class, rv.getClass());
		// assertEquals(123L, rv);
	}

	/**
	 * Test of convertObjectType method, of class ConvertType.
	 * @throws java.lang.Exception
	 */
	@Test
	@SuppressWarnings({"ThrowableResultIgnored", "UseOfSystemOutOrSystemErr"})
	public void testConvertObjectType_Object_JDBCType() throws Exception
	{
		System.out.println("convertObjectType");
		Object rv;

		LocalDateTime ldt = LocalDateTime.of(2111, 11, 11, 11, 11, 11);
		LocalDate ld = LocalDate.of(2111, 11, 11);
		LocalTime lt = LocalTime.of(11, 11, 11);

		java.sql.Timestamp ts = Timestamp.valueOf(ldt);
		java.sql.Date d = java.sql.Date.valueOf(ld);
		java.sql.Time t = java.sql.Time.valueOf(lt);

		Date ud = new java.util.Date(ts.getTime());

		// to java.sql.TIMESTAMP
		rv = convertObjectType(ud, JDBCType.TIMESTAMP);
		assertEquals(java.sql.Timestamp.class, rv.getClass());
		assertEquals(ts, rv);

		rv = convertObjectType(ts, JDBCType.TIMESTAMP);
		assertEquals(java.sql.Timestamp.class, rv.getClass());
		assertEquals(ts, rv);

		rv = convertObjectType(d, JDBCType.TIMESTAMP);
		assertEquals(java.sql.Timestamp.class, rv.getClass());
		assertEquals(java.sql.Timestamp.valueOf(ld.atStartOfDay()), rv);

		rv = convertObjectType(t, JDBCType.TIMESTAMP);
		assertEquals(java.sql.Timestamp.class, rv.getClass());
		assertEquals(java.sql.Timestamp.valueOf(lt.atDate(LocalDate.EPOCH)), rv);

		rv = convertObjectType(ldt, JDBCType.TIMESTAMP);
		assertEquals(java.sql.Timestamp.class, rv.getClass());
		assertEquals(ts, rv);

		assertThrows(SSSQLConversionException.class,
				()->convertObjectType(ld, JDBCType.TIMESTAMP));

		assertThrows(SSSQLConversionException.class,
					 ()->convertObjectType(lt, JDBCType.TIMESTAMP));


		// to java.sql.DATE
		rv = convertObjectType(ud, JDBCType.DATE);
		assertEquals(java.sql.Date.class, rv.getClass());
		assertEquals(d, rv);

		rv = convertObjectType(ts, JDBCType.DATE);
		assertEquals(java.sql.Date.class, rv.getClass());
		assertEquals(d, rv);

		rv = convertObjectType(d, JDBCType.DATE);
		assertEquals(java.sql.Date.class, rv.getClass());
		assertEquals(d, rv);

		assertThrows(SSSQLConversionException.class,
					 ()->convertObjectType(t, JDBCType.DATE));

		rv = convertObjectType(ldt, JDBCType.DATE);
		assertEquals(java.sql.Date.class, rv.getClass());
		assertEquals(d, rv);

		rv = convertObjectType(ld, JDBCType.DATE);
		assertEquals(java.sql.Date.class, rv.getClass());
		assertEquals(d, rv);

		assertThrows(SSSQLConversionException.class,
					 ()->convertObjectType(lt, JDBCType.DATE));


		// to java.sql.TIME
		rv = convertObjectType(ud, JDBCType.TIME);
		assertEquals(java.sql.Time.class, rv.getClass());
		assertEquals(t, rv);

		rv = convertObjectType(ts, JDBCType.TIME);
		assertEquals(java.sql.Time.class, rv.getClass());
		assertEquals(t, rv);

		assertThrows(SSSQLConversionException.class,
				()->convertObjectType(d, JDBCType.TIME));

		rv = convertObjectType(t, JDBCType.TIME);
		assertEquals(java.sql.Time.class, rv.getClass());
		assertEquals(t, rv);

		rv = convertObjectType(ldt, JDBCType.TIME);
		assertEquals(java.sql.Time.class, rv.getClass());
		assertEquals(t, rv);

		assertThrows(SSSQLConversionException.class,
				()->convertObjectType(ld, JDBCType.TIME));

		rv = convertObjectType(lt, JDBCType.TIME);
		assertEquals(java.sql.Time.class, rv.getClass());
		assertEquals(t, rv);
	}

	//
	// TODO: the other date conversions to non-jdbc things

	// /**
	//  * Test of convertObjectType method, of class ConvertType.
	//  */
	// @Test
	// public void testConvertObjectType_Object_Class() throws Exception
	// {
	// 	System.out.println("convertObjectType");
	// 	Object expResult = null;
	// 	Object result = ConvertType.convertObjectType(null);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }




	// /**
	//  * Test of getJDBCType method, of class ConvertType.
	//  */
	// @Test
	// public void testGetJDBCType()
	// {
	// 	System.out.println("getJDBCType");
	// 	int sqlType = 0;
	// 	JDBCType expResult = null;
	// 	JDBCType result = ConvertType.getJDBCType(sqlType);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of castJDBCToJava method, of class ConvertType.
	//  */
	// @Test
	// public void testCastJDBCToJava_JDBCType_ObjectArr() throws Exception
	// {
	// 	System.out.println("castJDBCToJava");
	// 	JDBCType _jdbcType = null;
	// 	Object[] _objects = null;
	// 	Object[] expResult = null;
	// 	Object[] result = ConvertType.castJDBCToJava(_jdbcType, _objects);
	// 	assertArrayEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of castJDBCToJava method, of class ConvertType.
	//  */
	// @Test
	// public void testCastJDBCToJava_JDBCType_Object() throws Exception
	// {
	// 	System.out.println("castJDBCToJava");
	// 	JDBCType _jdbcType = null;
	// 	Object _object = null;
	// 	Object expResult = null;
	// 	Object result = ConvertType.castJDBCToJava(_jdbcType, _object);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of findJavaTypeClass method, of class ConvertType.
	//  */
	// @Test
	// public void testFindJavaTypeClass() throws Exception
	// {
	// 	System.out.println("findJavaTypeClass");
	// 	JDBCType _jdbcType = null;
	// 	Class expResult = null;
	// 	Class result = ConvertType.findJavaTypeClass(_jdbcType);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }
	
}
