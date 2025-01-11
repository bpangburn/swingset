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
package com.nqadmin.swingset;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.nqadmin.swingset.models.OptionMappingSwingModel;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SSListTest.
 */
public class SSListTest
{
	
	/** x */
	public SSListTest()
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
	 * Test of getSelectedMappings method, of class SSList;
	 * Also getSelectedOptions.
	 */
	@Test
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void testGetSelectedMappingsAndOptions()
	{
		System.out.println("getSelectedMappings");

		SSList list = new SSList();
		List<Object> result;

		result = list.getSelectedMappings();
		assertEquals(Collections.emptyList(), result);
		result = list.getSelectedOptions();
		assertEquals(Collections.emptyList(), result);

		List<String> options = List.of("zero", "one", "two", "three");
		list.setOptions(options);

		result = list.getSelectedMappings();
		assertEquals(Collections.emptyList(), result);
		result = list.getSelectedOptions();
		assertEquals(Collections.emptyList(), result);

		list.setSelectedValues(new Integer[] {1, 2});

		result = list.getSelectedMappings();
		assertEquals(List.of(1, 2), result);
		result = list.getSelectedOptions();
		assertEquals(List.of("one", "two"), result);
	}

	/**
	 * Test of list shadows.
	 */
	@Test
	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "ConvertToTryWithResources", "ThrowableResultIgnored"})
	public void testShadows()
	{
		System.out.println("Shadows");

		SSList ssList = new SSList();
		OptionMappingSwingModel<Object, String, Object> optionModel = ssList.getOptionModel();
		List<String> listItems = List.of("LI 1","LI 2", "LI 3", "LI 4", "LI 5", "LI 6", "LI 7");
		List<Object> listCodes = List.of(1,2,3,4,5,6,7);
		List<String> otherOptions = List.of("one", "two", "three", "four");
		List<Object> otherMappings = List.of("oneM", "twoM", "threeM", "fourM");
		OptionMappingSwingModel<Object, String, Object>.Remodel remodel
				= optionModel.getRemodel();

		// Set up a map with "LI N" --> N, for example: "LI 1" --> 1
		ssList.setOptions(listItems, listCodes);

		List<Object> mappings = optionModel.getMappings();
		boolean isShadow = optionModel.hasShadow(mappings);
		assertEquals(true, isShadow);
		isShadow = optionModel.hasShadow(listCodes);
		assertEquals(false, isShadow);

		String expect1 = "{LI 1,1}{LI 2,2}{LI 3,3}{LI 4,4}{LI 5,5}{LI 6,6}{LI 7,7}";
		String expect0 = "{LI 1,0}{LI 2,1}{LI 3,2}{LI 4,3}{LI 5,4}{LI 6,5}{LI 7,6}";

		assertEquals(expect1, optionModel.dump());
		// auto generated, [0,N)
		ssList.setOptions(listItems);
		assertEquals(expect0, optionModel.dump());

		// back to original
		ssList.setOptions(listItems, listCodes);
		assertEquals(expect1, optionModel.dump());
		
		ssList.setOptions(otherOptions); // WARN old discarded
		assertEquals("{one,0}{two,1}{three,2}{four,3}", optionModel.dump());

		// back to original
		ssList.setOptions(listItems, listCodes);
		assertEquals(expect1, optionModel.dump());

		remodel.clear();
		assertEquals("", optionModel.dump());

		// different sizes
		assertThrows(IllegalArgumentException.class,
					 () -> ssList.setOptions(otherOptions, listCodes));
		assertEquals("", optionModel.dump());

		// check shadows
		// If shadows are used, there are exceptions and empty lists; but
		// shadows should be copied when populating SSList, see getDisconnectedList()

		String expect = "{one,oneM}{two,twoM}{three,threeM}{four,fourM}";
		ssList.setOptions(otherOptions , otherMappings);
		assertEquals(expect, optionModel.dump());

		ssList.setOptions(optionModel.getOptions(), otherMappings);
		assertEquals(expect, optionModel.dump());

		ssList.setOptions(otherOptions, optionModel.getMappings());
		assertEquals(expect, optionModel.dump());

		ssList.setOptions(optionModel.getOptions(), optionModel.getMappings());
		assertEquals(expect, optionModel.dump());

		remodel.close();
	}
}
