/* *****************************************************************************
 * Copyright (C) 2022, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 * ****************************************************************************/
package com.nqadmin.swingset.formatting.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nqadmin.swingset.formatting.SSMaskFormatterFactory;

/**
 * Most of the testing in here is to build class hierarchies
 * of factory/builder pairs and seeing if they compile.
 * These can provide examples.
 * The tests verify that the data gets through the builder to the formatter.
 * @author err
 */
@SuppressWarnings("javadoc")
public class SSMaskFormatterFactoryTest {
	
	/** x */
	public SSMaskFormatterFactoryTest() {
	}
	
	/** x */
	@BeforeAll
	public static void setUpClass() {
	}
	
	/** x */
	@AfterAll
	public static void tearDownClass() {
	}
	
	/** x */
	@BeforeEach
	public void setUp() {
	}
	
	/** x */
	@AfterEach
	public void tearDown() {
	}

	/** x */
	@SuppressWarnings("serial")
	public static class CustomParentFormatterFactory
			extends SSMaskFormatterFactory {

		/** @param <T> */
		public static class CustomParentBuilder<T extends CustomParentBuilder<T>>
				extends SSMaskFormatterFactory.Builder<CustomParentBuilder<T>> {
			private final String parentArg;
			private String parentParam = "defaultParentParam";

			/** *  @param mask
			 * @param arg */
			public CustomParentBuilder(String mask, String arg) {
				super(mask);
				parentArg = arg;
			}

			/** @param val
			 * @return */
			public T parentParam(String val) { parentParam = val; return self(); }

			/** @return */
			@Override
			public CustomParentFormatterFactory build() { return new CustomParentFormatterFactory(this); }
			
			/** @return */
			@Override
			@SuppressWarnings("unchecked")
			protected T self() {
				return (T) this;
			}
		}

		private final String parentArg;
		private final String parentParam;
		/** @param builder */
		public CustomParentFormatterFactory(CustomParentBuilder<?> builder) {
			super(builder);
			parentArg = builder.parentArg;
			parentParam = builder.parentParam;
		}

		String getParentArg() { return parentArg; }
		String getParentParam() { return parentParam; }
	}

	/** x */
	@SuppressWarnings("serial")
	public static class CustomChildFormatterFactory
			extends CustomParentFormatterFactory {

		/** @param <T> */
		public static class CustomChildBuilder<T extends CustomChildBuilder<T>>
				extends CustomParentBuilder<CustomChildBuilder<T>> {
			private final String childArg;
			private String childParam = "defaultChildParam";

			/** @param mask
			 * @param argP
			 * @param argC */
			public CustomChildBuilder(String mask, String argP, String argC) {
				super(mask, argP);
				childArg = argC;
			}

			/** @param val
			 * @return */
			public T childParam(String val) { childParam = val; return self(); }

			/** @return */
			@Override
			public CustomChildFormatterFactory build() { return new CustomChildFormatterFactory(this); }

			/** @return */
			@Override
			@SuppressWarnings("unchecked")
			protected T self() {
				return (T) this;
			}
		}

		private final String childArg;
		private final String childParam;
		/** @param builder */
		public CustomChildFormatterFactory(CustomChildBuilder<?> builder) {
			super(builder);
			childArg = builder.childArg;
			childParam = builder.childParam;
		}

		String getChildArg() { return childArg; }
		String getChildParam() { return childParam; }
	}

	/** x */
	@Test
	public void testParentHier() {
		CustomParentFormatterFactory.CustomParentBuilder<?> b
				= new CustomParentFormatterFactory
						.CustomParentBuilder<>("##/##", "parentArg")
						.maskLiterals("AB").parentParam("parentParam");
		CustomParentFormatterFactory ff = b.build();
		assertEquals("parentArg", ff.getParentArg());
		assertEquals("parentParam", ff.getParentParam());
		SSMaskFormatterFactory.SSMaskFormatter f
				= (SSMaskFormatterFactory.SSMaskFormatter) ff.getDefaultFormatter();
		assertEquals("##/##", f.getMask());
		assertEquals("AB", f.getLiterals());
	}

	/** x */
	@Test
	public void testChildHier() {
		CustomChildFormatterFactory.CustomChildBuilder<?> b
				= new CustomChildFormatterFactory
						.CustomChildBuilder<>("##/##", "parentArg", "childArg")
						.maskLiterals("AB")
						.parentParam("parentParam").childParam("childParam");
		CustomChildFormatterFactory ff = b.build();
		assertEquals("parentArg", ff.getParentArg());
		assertEquals("parentParam", ff.getParentParam());
		assertEquals("childArg", ff.getChildArg());
		assertEquals("childParam", ff.getChildParam());
		SSMaskFormatterFactory.SSMaskFormatter f
				= (SSMaskFormatterFactory.SSMaskFormatter) ff.getDefaultFormatter();
		assertEquals("##/##", f.getMask());
		assertEquals("AB", f.getLiterals());
	}

	/**
	 * Test of builder method, of class SSMaskFormatterFactory.
	 */
	//@Test
	//@SuppressWarnings("UseOfSystemOutOrSystemErr")
	//public void testBuilder() {
	//	System.out.println("builder");
	//	String mask = "";
	//	@SuppressWarnings("rawtypes")
	//	SSMaskFormatterFactory.Builder expResult = null;
	//	@SuppressWarnings("rawtypes")
	//	SSMaskFormatterFactory.Builder result = SSMaskFormatterFactory.builder(mask);
	//	assertEquals(expResult, result);
	//	// TODO review the generated test code and remove the default call to fail.
	//	fail("The test case is a prototype.");
	//}

	/**
	 * Test of adjustNullFormatter method, of class SSMaskFormatterFactory.
	 */
	//@Test
	//@SuppressWarnings("UseOfSystemOutOrSystemErr")
	//public void testAdjustNullFormatter() {
	//	System.out.println("adjustNullFormatter");
	//	SSFormattedTextField _ftf = null;
	//	SSMaskFormatterFactory.adjustNullFormatter(_ftf);
	//	// TODO review the generated test code and remove the default call to fail.
	//	fail("The test case is a prototype.");
	//}
	
}
