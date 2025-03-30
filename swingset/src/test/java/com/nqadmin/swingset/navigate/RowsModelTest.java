/* *****************************************************************************
 * Copyright (C) 2025, Ernie R Rael. All rights reserved.
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
package com.nqadmin.swingset.navigate;

import java.awt.EventQueue;
import java.util.logging.Logger;

import javax.sql.RowSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nqadmin.swingset.core.TextField;
import com.nqadmin.swingset.mock.H2;
import com.nqadmin.swingset.mock.TestLogging;

import static com.nqadmin.swingset.navigate.Support.getRS1_4;
import static com.nqadmin.swingset.navigate.Support.getRS2_5;
import static com.nqadmin.swingset.utils.SSUtils.getLoggerName;
import static com.nqadmin.swingset.utils.SSUtils.isJunit;
import static java.util.logging.Level.*;
import static org.junit.jupiter.api.Assertions.*;

/** x */
public class RowsModelTest
{
	private static final Logger LOG = Logger.getLogger(getLoggerName());
	
	/** x */
	public RowsModelTest()
	{
	}
	
	/** x */
	@BeforeAll
	public static void setUpClass()
	{
		isJunit();	// Make sure it's set; when using invokeLater, can be missed.
		TestLogging.load();
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
	 * Test of create method, of class RowsModel.
	 */
	// @Test
	public void testCreate()
	{
		LOG.log(INFO, "create");
		RowSet rs = null;
		RowsModel expResult = null;
		RowsModel result = RowsModel.create(rs);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setRowSet method, of class RowsModel.
	 * @throws java.lang.Exception
	 */
	@Test
	@SuppressWarnings("LoggerStringConcat")
	public void testSetRowSet() throws Exception
	{
		LOG.log(INFO, "setRowSet");

		H2.clean();
		RowSet rs1 = getRS1_4();
		RowsModel rowsModel = RowsModel.create(rs1);
		RowSet rs2 = getRS2_5();
		
		TextField[] p_tf = new TextField[1];
		EventQueue.invokeAndWait(() -> {
			p_tf[0] = new TextField(rowsModel, "c_char");
		});

		TextField tf = p_tf[0];
		EventQueue.invokeAndWait(() -> {
			assertEquals("a1", tf.getText());
		});

		assertTrue(Support.invokeLaterEventLatchWait(
				"tick1", () -> rowsModel.setRowSet(rs2), s -> LOG.log(INFO, s)));

		EventQueue.invokeAndWait(() -> { });
		// Change TextField's updateSSComponent as
		//			setText(text);
		//			if ("a2".equals(text))
		//				throw new IllegalStateException("Fake Exception");
		// to force a BusReceiver exception
		assertEquals("a2", tf.getText());
	}

	// /**
	//  * Test of getRowSet method, of class RowsModel.
	//  */
	// @Test
	// public void testGetRowSet()
	// {
	// 	System.out.println("getRowSet");
	// 	RowsModel instance = null;
	// 	RowSet expResult = null;
	// 	RowSet result = instance.getRowSet();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getAction method, of class RowsModel.
	//  */
	// @Test
	// public void testGetAction()
	// {
	// 	System.out.println("getAction");
	// 	RowsAction navAction = null;
	// 	RowsModel instance = null;
	// 	Action expResult = null;
	// 	Action result = instance.getAction(navAction);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of fillNavActionMap method, of class RowsModel.
	//  */
	// @Test
	// public void testFillNavActionMap()
	// {
	// 	System.out.println("fillNavActionMap");
	// 	ActionMap actionMap = null;
	// 	RowsModel instance = null;
	// 	ActionMap expResult = null;
	// 	ActionMap result = instance.fillNavActionMap(actionMap);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getRow method, of class RowsModel.
	//  */
	// @Test
	// public void testGetRow()
	// {
	// 	System.out.println("getRow");
	// 	RowsModel instance = null;
	// 	int expResult = 0;
	// 	int result = instance.getRow();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of first method, of class RowsModel.
	//  */
	// @Test
	// public void testFirst()
	// {
	// 	System.out.println("first");
	// 	RowsModel instance = null;
	// 	instance.first();
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of last method, of class RowsModel.
	//  */
	// @Test
	// public void testLast()
	// {
	// 	System.out.println("last");
	// 	RowsModel instance = null;
	// 	instance.last();
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of next method, of class RowsModel.
	//  */
	// @Test
	// public void testNext()
	// {
	// 	System.out.println("next");
	// 	RowsModel instance = null;
	// 	instance.next();
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of previous method, of class RowsModel.
	//  */
	// @Test
	// public void testPrevious()
	// {
	// 	System.out.println("previous");
	// 	RowsModel instance = null;
	// 	instance.previous();
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of commit method, of class RowsModel.
	//  */
	// @Test
	// public void testCommit()
	// {
	// 	System.out.println("commit");
	// 	RowsModel instance = null;
	// 	instance.commit();
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setRow method, of class RowsModel.
	//  */
	// @Test
	// public void testSetRow()
	// {
	// 	System.out.println("setRow");
	// 	int row = 0;
	// 	RowsModel instance = null;
	// 	instance.setRow(row);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getRowCount method, of class RowsModel.
	//  */
	// @Test
	// public void testGetRowCount()
	// {
	// 	System.out.println("getRowCount");
	// 	RowsModel instance = null;
	// 	int expResult = 0;
	// 	int result = instance.getRowCount();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of isEmpty method, of class RowsModel.
	//  */
	// @Test
	// public void testIsEmpty()
	// {
	// 	System.out.println("isEmpty");
	// 	RowsModel instance = null;
	// 	boolean expResult = false;
	// 	boolean result = instance.isEmpty();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of rsOp method, of class RowsModel.
	//  */
	// @Test
	// public void testRsOp() throws Exception
	// {
	// 	System.out.println("rsOp");
	// 	Object operator = null;
	// 	RowsModel.DBRunnable r = null;
	// 	RowsModel instance = null;
	// 	instance.rsOp(operator, r);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of registerBusReceiver method, of class RowsModel.
	//  */
	// @Test
	// public void testRegisterBusReceiver()
	// {
	// 	System.out.println("registerBusReceiver");
	// 	Object busReceiver = null;
	// 	RowsModel instance = null;
	// 	instance.registerBusReceiver(busReceiver);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of unregisterBusReceiver method, of class RowsModel.
	//  */
	// @Test
	// public void testUnregisterBusReceiver()
	// {
	// 	System.out.println("unregisterBusReceiver");
	// 	Object busReceiver = null;
	// 	RowsModel instance = null;
	// 	instance.unregisterBusReceiver(busReceiver);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of startRowsEvent method, of class RowsModel.
	//  */
	// @Test
	// public void testStartRowsEvent_RowsModel_Object()
	// {
	// 	System.out.println("startRowsEvent");
	// 	RowsModel model = null;
	// 	Object compOrNav = null;
	// 	RowsModel.startRowsEvent(model, compOrNav);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of startRowsEvent method, of class RowsModel.
	//  */
	// @Test
	// public void testStartRowsEvent_3args()
	// {
	// 	System.out.println("startRowsEvent");
	// 	RowsEvent.OperatorKind _operatorKind = null;
	// 	RowsModel model = null;
	// 	Object compOrNav = null;
	// 	RowsModel.startRowsEvent(_operatorKind, model, compOrNav);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of finishRowsEvent method, of class RowsModel.
	//  */
	// @Test
	// public void testFinishRowsEvent()
	// {
	// 	System.out.println("finishRowsEvent");
	// 	RowsModel model = null;
	// 	RowsModel.finishRowsEvent(model);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of post method, of class RowsModel.
	//  */
	// @Test
	// public void testPost()
	// {
	// 	System.out.println("post");
	// 	RowsModelEvent event = null;
	// 	RowsModel.post(event);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getEventBus method, of class RowsModel.
	//  */
	// @Test
	// public void testGetEventBus()
	// {
	// 	System.out.println("getEventBus");
	// 	EventBus expResult = null;
	// 	EventBus result = RowsModel.getEventBus();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of dumpLatestEvents method, of class RowsModel.
	//  */
	// @Test
	// public void testDumpAllEvents()
	// {
	// 	System.out.println("dumpLatestEvents");
	// 	String tag = "";
	// 	RowsModel.dumpLatestEvents(tag);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setCallExecute method, of class RowsModel.
	//  */
	// @Test
	// public void testSetCallExecute()
	// {
	// 	System.out.println("setCallExecute");
	// 	boolean callExecute = false;
	// 	RowsModel instance = null;
	// 	instance.setCallExecute(callExecute);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getCallExecute method, of class RowsModel.
	//  */
	// @Test
	// public void testGetCallExecute()
	// {
	// 	System.out.println("getCallExecute");
	// 	RowsModel instance = null;
	// 	boolean expResult = false;
	// 	boolean result = instance.getCallExecute();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setConfirmDeletes method, of class RowsModel.
	//  */
	// @Test
	// public void testSetConfirmDeletes()
	// {
	// 	System.out.println("setConfirmDeletes");
	// 	boolean confirmDeletes = false;
	// 	RowsModel instance = null;
	// 	instance.setConfirmDeletes(confirmDeletes);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getConfirmDeletes method, of class RowsModel.
	//  */
	// @Test
	// public void testGetConfirmDeletes()
	// {
	// 	System.out.println("getConfirmDeletes");
	// 	RowsModel instance = null;
	// 	boolean expResult = false;
	// 	boolean result = instance.getConfirmDeletes();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setDBNav method, of class RowsModel.
	//  */
	// @Test
	// public void testSetDBNav()
	// {
	// 	System.out.println("setDBNav");
	// 	SSDBNav dBNav = null;
	// 	RowsModel instance = null;
	// 	instance.setDBNav(dBNav);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getDBNav method, of class RowsModel.
	//  */
	// @Test
	// public void testGetDBNav()
	// {
	// 	System.out.println("getDBNav");
	// 	RowsModel instance = null;
	// 	SSDBNav expResult = null;
	// 	SSDBNav result = instance.getDBNav();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setDeletion method, of class RowsModel.
	//  */
	// @Test
	// public void testSetDeletion()
	// {
	// 	System.out.println("setDeletion");
	// 	boolean deletion = false;
	// 	RowsModel instance = null;
	// 	instance.setDeletion(deletion);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getDeletion method, of class RowsModel.
	//  */
	// @Test
	// public void testGetDeletion()
	// {
	// 	System.out.println("getDeletion");
	// 	RowsModel instance = null;
	// 	boolean expResult = false;
	// 	boolean result = instance.getDeletion();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getInsertion method, of class RowsModel.
	//  */
	// @Test
	// public void testGetInsertion()
	// {
	// 	System.out.println("getInsertion");
	// 	RowsModel instance = null;
	// 	boolean expResult = false;
	// 	boolean result = instance.getInsertion();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setInsertion method, of class RowsModel.
	//  */
	// @Test
	// public void testSetInsertion()
	// {
	// 	System.out.println("setInsertion");
	// 	boolean insertion = false;
	// 	RowsModel instance = null;
	// 	instance.setInsertion(insertion);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setWritable method, of class RowsModel.
	//  */
	// @Test
	// public void testSetWritable()
	// {
	// 	System.out.println("setWritable");
	// 	boolean writable = false;
	// 	RowsModel instance = null;
	// 	instance.setWritable(writable);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getWritable method, of class RowsModel.
	//  */
	// @Test
	// public void testGetWritable()
	// {
	// 	System.out.println("getWritable");
	// 	RowsModel instance = null;
	// 	boolean expResult = false;
	// 	boolean result = instance.getWritable();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setModification method, of class RowsModel.
	//  */
	// @Test
	// public void testSetModification()
	// {
	// 	System.out.println("setModification");
	// 	boolean modification = false;
	// 	RowsModel instance = null;
	// 	instance.setModification(modification);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getModification method, of class RowsModel.
	//  */
	// @Test
	// public void testGetModification()
	// {
	// 	System.out.println("getModification");
	// 	RowsModel instance = null;
	// 	boolean expResult = false;
	// 	boolean result = instance.getModification();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setNavCombo method, of class RowsModel.
	//  */
	// @Test
	// public void testSetNavCombo()
	// {
	// 	System.out.println("setNavCombo");
	// 	SSDBComboBox navCombo = null;
	// 	RowsModel instance = null;
	// 	instance.setNavCombo(navCombo);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of getNavCombo method, of class RowsModel.
	//  */
	// @Test
	// public void testGetNavCombo()
	// {
	// 	System.out.println("getNavCombo");
	// 	RowsModel instance = null;
	// 	SSDBComboBox expResult = null;
	// 	SSDBComboBox result = instance.getNavCombo();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of containsRows method, of class RowsModel.
	//  */
	// @Test
	// public void testContainsRows()
	// {
	// 	System.out.println("containsRows");
	// 	RowsModel instance = null;
	// 	boolean expResult = false;
	// 	boolean result = instance.containsRows();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of isOnInsertRow method, of class RowsModel.
	//  */
	// @Test
	// public void testIsOnInsertRow()
	// {
	// 	System.out.println("isOnInsertRow");
	// 	RowsModel instance = null;
	// 	boolean expResult = false;
	// 	boolean result = instance.isOnInsertRow();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of updatePresentRow method, of class RowsModel.
	//  */
	// @Test
	// public void testUpdatePresentRow()
	// {
	// 	System.out.println("updatePresentRow");
	// 	RowsModel instance = null;
	// 	boolean expResult = false;
	// 	boolean result = instance.updatePresentRow();
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }
	
}
