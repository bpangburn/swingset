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
package com.nqadmin.swingset.formatting;


import javax.swing.text.DefaultFormatterFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nqadmin.swingset.mock.Util;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Random tests mostly examining subclasses and their use of superclass features.
 */
public class NumberFieldTest
{
	
	/** x */
	public NumberFieldTest()
	{
	}
	
	/** x */
	@BeforeAll
	public static void setUpClass()
	{
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
	
	/**
	 * Test of cleanField method, of class NumberField.
	 */
	@Test
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void testParamAccessMethods()
	{
		System.out.println("accessMethods");

		SSNumericField nf = new SSNumericField();
		int precision = nf.getPrecision();
		int decimals = nf.getDecimals();

		// check the defaults
		assertEquals(2147483647, precision);
		assertEquals(3, decimals);

		nf.setPrecision(precision = 100);
		assertEquals(precision, nf.getPrecision());
		assertEquals(decimals, nf.getDecimals());

		nf.setDecimals(decimals = 5);
		assertEquals(precision, nf.getPrecision());
		assertEquals(decimals, nf.getDecimals());

		// Check out percent's multiplier.
		SSPercentField pf = new SSPercentField();
		NumberField.Params params;
		params = pf.getFormatParams((x) -> x.getMultiplier());
		assertEquals(new NumberField.Params(100,100,100,null), params);

		assertEquals(100, pf.getMultiplier());
		pf.setMultiplier(1);
		assertEquals(1, pf.getMultiplier());
		params = pf.getFormatParams((x) -> x.getMultiplier());
		assertEquals(new NumberField.Params(1,1,1,null), params);
	}

	/**
	 * Test the precision initialization.
	 */
	@Test
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void testFieldFormatsInit()
	{
		System.out.println("fieldFormatsInit");

		NumberField ssnf;
		NumberField.Params params;
		ssnf = new SSIntegerField(11);
		params = ssnf.getFormatParams((nf) -> nf.getMaximumIntegerDigits());
		assertEquals(new NumberField.Params(11,11,11,null), params);

		ssnf = new SSNumericField(11, 5);
		params = ssnf.getFormatParams((nf) -> nf.getMaximumIntegerDigits());
		assertEquals(new NumberField.Params(11,11,11,null), params);
		params = ssnf.getFormatParams((nf) -> nf.getMaximumFractionDigits());
		assertEquals(new NumberField.Params(5,5,5,null), params);

		ssnf = new SSCurrencyField(11, 5);
		params = ssnf.getFormatParams((nf) -> nf.getMaximumIntegerDigits());
		assertEquals(new NumberField.Params(11,11,11,null), params);
		params = ssnf.getFormatParams((nf) -> nf.getMaximumFractionDigits());
		assertEquals(new NumberField.Params(5,5,5,null), params);

		ssnf = new SSPercentField(11, 5);
		params = ssnf.getFormatParams((nf) -> nf.getMaximumIntegerDigits());
		assertEquals(new NumberField.Params(11,11,11,null), params);
		params = ssnf.getFormatParams((nf) -> nf.getMaximumFractionDigits());
		assertEquals(new NumberField.Params(5,5,5,null), params);
	}

	/**
	 * For each formatter.
	 */
	@Test
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void testFactoryParamsAccessMethods()
	{
		System.out.println("factoryParamsAccessMethods");

		SSNumericField ssnf = new SSNumericField(15, 5);
		NumberField.Params params;
		params = ssnf.getFormatParams((nf) -> nf.getMaximumFractionDigits());
		assertEquals(new NumberField.Params(5,5,5,null), params);
		assertFalse(params.hasAccessError());
		assertFalse(NumberField.isAccessError(params.editP()));
		assertFalse(NumberField.isAccessError(params.nullP()));

		ssnf.setAllowNull(true);
		params = ssnf.getFormatParams((nf) -> nf.getMaximumFractionDigits());
		assertEquals(new NumberField.Params(5,5,5,null), params);
		assertFalse(params.hasAccessError());
		assertFalse(NumberField.isAccessError(params.editP()));
		assertFalse(NumberField.isAccessError(params.nullP()));

		DefaultFormatterFactory ff = (DefaultFormatterFactory)ssnf.getFormatterFactory();
		ff.setNullFormatter(ff.getDefaultFormatter());

		params = ssnf.getFormatParams((nf) -> nf.getMaximumFractionDigits());
		assertEquals(new NumberField.Params(5,5,5,NumberField.Error.NULL_FORMATTER), params);
		assertTrue(params.hasAccessError());
		assertTrue(NumberField.isAccessError(params.nullP()));

		// Write to the factory's formats.
		ssnf.setDecimals(7);
		params = ssnf.getFormatParams((nf) -> nf.getMaximumFractionDigits());
		assertEquals(new NumberField.Params(7,7,7,NumberField.Error.NULL_FORMATTER), params);
		assertTrue(params.hasAccessError());
		assertTrue(NumberField.isAccessError(params.nullP()));

		// Test the internal function making the change
		params = ssnf.setFormatParam((nf) -> nf.setMaximumFractionDigits(11));
		assertEquals(new NumberField.Params(null,null,null,null), params);
		assertFalse(params.hasAccessError());
		assertFalse(NumberField.isAccessError(params.nullP()));

		// and check that the change did happen
		params = ssnf.getFormatParams((nf) -> nf.getMaximumFractionDigits());
		assertEquals(new NumberField.Params(11,11,11,NumberField.Error.NULL_FORMATTER), params);
		assertTrue(params.hasAccessError());
		assertTrue(NumberField.isAccessError(params.nullP()));

		// Currency has two formats.
		SSCurrencyField sscf = new SSCurrencyField(15, 5);
		params = sscf.setFormatParam((nf) -> nf.setMaximumFractionDigits(7));
		assertEquals(new NumberField.Params(null,null,null,null), params);
		assertFalse(params.hasAccessError());
		assertFalse(NumberField.isAccessError(params.nullP()));

		// and check that the change did happen
		params = sscf.getFormatParams((nf) -> nf.getMaximumFractionDigits());
		assertEquals(new NumberField.Params(7,7,7,null), params);
		assertFalse(params.hasAccessError());
		assertFalse(NumberField.isAccessError(params.nullP()));
	}


	// /**
	//  * Test of cleanField method, of class NumberField.
	//  */
	// @Test
	// public void testCleanField()
	// {
	// 	System.out.println("cleanField");
	// 	NumberField instance = null;
	// 	instance.cleanField();
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getPrecision method, of class NumberField.
	//  */
	// @Test
	// public void testGetPrecision()
	// {
	// 	System.out.println("getPrecision");
	// 	NumberField instance = null;
	// 	int expResult = 0;
	// 	int result = instance.getPrecision();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getDecimals method, of class NumberField.
	//  */
	// @Test
	// public void testGetDecimals()
	// {
	// 	System.out.println("getDecimals");
	// 	NumberField instance = null;
	// 	int expResult = 0;
	// 	int result = instance.getDecimals();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getNumberFormatParam method, of class NumberField.
	//  */
	// @Test
	// public void testGetNumberFormatParam()
	// {
	// 	System.out.println("getNumberFormatParam");
	// 	Function<NumberFormat, Object> param = null;
	// 	NumberField instance = null;
	// 	int expResult = 0;
	// 	int result = instance.getNumberFormatParam(param);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getFormatParam method, of class NumberField.
	//  */
	// @Test
	// public void testGetFormatParam()
	// {
	// 	System.out.println("getFormatParam");
	// 	Function<NumberFormat, Object> param = null;
	// 	NumberField instance = null;
	// 	Object expResult = null;
	// 	Object result = instance.getFormatParam(param);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of createFormat method, of class NumberField.
	//  */
	// @Test
	// public void testCreateFormat()
	// {
	// 	System.out.println("createFormat");
	// 	Supplier<NumberFormat> f = null;
	// 	NumberFormat expResult = null;
	// 	NumberFormat result = NumberField.createFormat(f);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of parseBigDecimal method, of class NumberField.
	//  */
	// @Test
	// public void testParseBigDecimal()
	// {
	// 	System.out.println("parseBigDecimal");
	// 	NumberFormat format = null;
	// 	boolean flag = false;
	// 	boolean expResult = false;
	// 	boolean result = NumberField.parseBigDecimal(format, flag);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }
}
