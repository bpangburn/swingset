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

import java.sql.JDBCType;
import java.util.Date;
import java.util.EnumSet;

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

		assertDoesNotThrow(()->ConvertType.assertConvertToType(
				JDBCType.DATE, Date.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertToType(
				JDBCType.DATE, java.sql.Date.class, allow));
		assertThrows(AssertionError.class, ()->ConvertType.assertConvertToType(
				JDBCType.DATE, java.sql.Time.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertToType(
				JDBCType.DATE, java.sql.Timestamp.class, allow));

		assertDoesNotThrow(()->ConvertType.assertConvertToType(
				JDBCType.TIME, Date.class, allow));
		assertThrows(AssertionError.class, ()->ConvertType.assertConvertToType(
				JDBCType.TIME, java.sql.Date.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertToType(
				JDBCType.TIME, java.sql.Time.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertToType(
				JDBCType.TIME, java.sql.Timestamp.class, allow));

		assertDoesNotThrow(()->ConvertType.assertConvertToType(
				JDBCType.TIMESTAMP, Date.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertToType(
				JDBCType.TIMESTAMP, java.sql.Date.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertToType(
				JDBCType.TIMESTAMP, java.sql.Time.class, allow));
		assertDoesNotThrow(()->ConvertType.assertConvertToType(
				JDBCType.TIMESTAMP, java.sql.Timestamp.class, allow));
	}

	// /**
	//  * Test of convertObjectType method, of class ConvertType.
	//  */
	// @Test
	// public void testConvertObjectType_Object_JDBCType() throws Exception
	// {
	// 	System.out.println("convertObjectType");
	// 	Object value = null;
	// 	JDBCType jdbcType = null;
	// 	Object expResult = null;
	// 	Object result = ConvertType.convertObjectType(value, jdbcType);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

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
