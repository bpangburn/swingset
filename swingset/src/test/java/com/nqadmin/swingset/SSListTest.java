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

import java.util.Collections;
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

	// /**
	//  * Test of getMappings method, of class SSList.
	//  */
	// @Test
	// public void testGetMappings()
	// {
	// 	System.out.println("getMappings");
	// 	SSList instance = new SSList();
	// 	List<Object> expResult = null;
	// 	List<Object> result = instance.getMappings();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getOptions method, of class SSList.
	//  */
	// @Test
	// public void testGetOptions()
	// {
	// 	System.out.println("getOptions");
	// 	SSList instance = new SSList();
	// 	List<String> expResult = null;
	// 	List<String> result = instance.getOptions();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getSelectedValues method, of class SSList.
	//  */
	// @Test
	// public void testGetSelectedValues()
	// {
	// 	System.out.println("getSelectedValues");
	// 	SSList instance = new SSList();
	// 	Object[] expResult = null;
	// 	Object[] result = instance.getSelectedValues();
	// 	assertArrayEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getSelectedValuesList method, of class SSList.
	//  */
	// @Test
	// public void testGetSelectedValuesList()
	// {
	// 	System.out.println("getSelectedValuesList");
	// 	SSList instance = new SSList();
	// 	List<SSListItem> expResult = null;
	// 	List<SSListItem> result = instance.getSelectedValuesList();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getSSComponentListener method, of class SSList.
	//  */
	// @Test
	// public void testGetSSComponentListener()
	// {
	// 	System.out.println("getSSComponentListener");
	// 	SSList instance = new SSList();
	// 	SSList.SSListListener expResult = null;
	// 	SSList.SSListListener result = instance.getSSComponentListener();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setModel method, of class SSList.
	//  */
	// @Test
	// public void testSetModel()
	// {
	// 	System.out.println("setModel");
	// 	ListModel<SSListItem> _model = null;
	// 	SSList instance = new SSList();
	// 	instance.setModel(_model);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getOptionModel method, of class SSList.
	//  */
	// @Test
	// public void testGetOptionModel()
	// {
	// 	System.out.println("getOptionModel");
	// 	SSList instance = new SSList();
	// 	OptionMappingSwingModel<Object, String, Object> expResult = null;
	// 	OptionMappingSwingModel<Object, String, Object> result = instance.getOptionModel();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setOptions method, of class SSList.
	//  */
	// @Test
	// public void testSetOptions_List()
	// {
	// 	System.out.println("setOptions");
	// 	List<String> _options = null;
	// 	SSList instance = new SSList();
	// 	instance.setOptions(_options);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setOptions method, of class SSList.
	//  */
	// @Test
	// public void testSetOptions_Class()
	// {
	// 	System.out.println("setOptions");
	// 	SSList instance = new SSList();
	// 	instance.setOptions(null);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setOptions method, of class SSList.
	//  */
	// @Test
	// public void testSetOptions_List_List()
	// {
	// 	System.out.println("setOptions");
	// 	List<String> _options = null;
	// 	List<Object> _mappings = null;
	// 	SSList instance = new SSList();
	// 	instance.setOptions(_options, _mappings);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setSelectedValues method, of class SSList.
	//  */
	// @Test
	// public void testSetSelectedValues()
	// {
	// 	System.out.println("setSelectedValues");
	// 	Object[] _selectedMappings = null;
	// 	SSList instance = new SSList();
	// 	instance.setSelectedValues(_selectedMappings);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of updateRowSet method, of class SSList.
	//  */
	// @Test
	// public void testUpdateRowSet()
	// {
	// 	System.out.println("updateRowSet");
	// 	SSList instance = new SSList();
	// 	instance.updateRowSet();
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of undoRedoUpdateObject method, of class SSList.
	//  */
	// @Test
	// public void testUndoRedoUpdateObject() throws Exception
	// {
	// 	System.out.println("undoRedoUpdateObject");
	// 	NavigateActions.UndoRedo cmd = null;
	// 	Object value = null;
	// 	SSList instance = new SSList();
	// 	instance.undoRedoUpdateObject(cmd, value);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of updateSSComponent method, of class SSList.
	//  */
	// @Test
	// public void testUpdateSSComponent()
	// {
	// 	System.out.println("updateSSComponent");
	// 	SSList instance = new SSList();
	// 	instance.updateSSComponent();
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of testStuff method, of class SSList.
	//  */
	// @Test
	// public void testTestStuff()
	// {
	// 	System.out.println("testStuff");
	// 	SSList ssList = null;
	// 	SSList.testStuff(ssList);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of toString method, of class SSList.
	//  */
	// @Test
	// public void testToString()
	// {
	// 	System.out.println("toString");
	// 	SSList instance = new SSList();
	// 	String expResult = "";
	// 	String result = instance.toString();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getSSCommon method, of class SSList.
	//  */
	// @Test
	// public void testGetSSCommon()
	// {
	// 	System.out.println("getSSCommon");
	// 	SSList instance = new SSList();
	// 	SSCommon expResult = null;
	// 	SSCommon result = instance.getSSCommon();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }
	
}
