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

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.sql.RowSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nqadmin.swingset.mock.H2;
import com.nqadmin.swingset.mock.TinyRS;
import com.nqadmin.swingset.navigate.EQ.BusReceiver;
import com.raelity.lib.eventbus.WeakEventBus;

import static com.nqadmin.swingset.navigate.RowsAction.*;
import static com.nqadmin.swingset.navigate.RowsEvent.OperatorKind.*;
import static com.nqadmin.swingset.navigate.Utils.getGlobalEventBus;
import static com.nqadmin.swingset.utils.SSUtils.isJunit;
import static org.junit.jupiter.api.Assertions.*;

// Google AI : junit 5 eventqueue
//
// In JUnit 5, the TestExecutionListener class is used to register an instance with
// a Launcher to receive notifications about events that occur during test execution. [1]  
// Explanation [2]  
// 
// • The java.awt.EventQueue class is a platform-independent class that queues events
//   from trusted application classes and the underlying peer classes. [2]  
// • The TestExecutionListener class is used to register an instance with a Launcher
//   to receive notifications about events that occur during test execution. [1]  
// • To be notified of events that occur during test execution, register an instance
//   of the TestExecutionListener class with a Launcher. [1]  
// 
// Some annotations used in JUnit 5 include: [3]  
// 
// • @ExtendWith: Used to annotate test classes in JUnit 5 [3]  
// • @BeforeEach: Used to execute a piece of code before each test [4]  
// • @AfterEach: Used to execute a piece of code after each test [4]  
// • @BeforeAll: Used to execute code once for all tests in the test instance [4]  
// • @AfterAll: Used to execute code once for all tests in the test instance [4]  
// 
// 
// Generative AI is experimental.
// 
// [1] https://junit.org/junit5/docs/5.0.3/api/org/junit/platform/launcher/TestExecutionListener.html[2] https://docs.oracle.com/cd/E17802_01/j2se/j2se/1.5.0/jcp/rc/apidiffs/java/awt/EventQueue.html[3] https://devonblog.com/continuous-delivery/migration-from-junit-4-to-junit-5/[4] https://www.arhohuttunen.com/junit-5-test-lifecycle/

/** x */
public class RowsModelEventHandlingTest
{
	
	/** x */
	public RowsModelEventHandlingTest()
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
		// Force the check, to set the isJunit flag to true.
		// This is needed because of the use of 'invokeLater'
		// where junit is not on the stack.
		isJunit();
	}
	
	/** x */
	@AfterEach
	public void tearDown()
	{
		if (oneTestBusReceiver != null)
			WeakEventBus.unregister(oneTestBusReceiver, getGlobalEventBus());
		oneTestBusReceiver = null;
	}

	BusReceiver oneTestBusReceiver; // Strong Reference

	/**
	 * Test of Basics for of class RowsModelEventHandling.
	 * @throws java.sql.SQLException
	 * @throws java.lang.ClassNotFoundException
	 * @throws java.lang.InterruptedException
	 * @throws java.lang.reflect.InvocationTargetException
	 */
	@Test
	public void testBasicHandling()
	throws SQLException, ClassNotFoundException,
			InterruptedException, InvocationTargetException
	{
		System.out.println("Basics");

		H2.clean();

		RowSet rs1 = TinyRS.getRS1();
		RowsModel model1 = RowsModel.create(rs1, null);

		RowSet rs2 = TinyRS.getRS2();
		RowsModel model2 = RowsModel.create(rs2, null);

		oneTestBusReceiver = EQ.setupBusReceiver();
		EQ.GetRowsModelEvent events = oneTestBusReceiver.events();

		System.out.println("=== next");
		events.clear();
		// all the "next" collapsed into a single event
		assertTrue(EQ.invokeLatchWait("tick1", s -> System.out.println(s),
				() -> {
					model1.next();
					model1.next();	// merged
					model1.next();	// merged
				}));
		assertEquals(1, events.size());
		assertEquals(ACTION, events.get(0).getKindOperator());
		assertEquals(ACT_NEXT, events.get(0).getOperAct());

		System.out.println("=== rsOp next");
		events.clear();
		assertTrue(EQ.invokeLatchWait("tick2", s -> System.out.println(s),
				() -> {
					model2.rsOp(this, () -> {
						rs2.next();
						rs2.next();
						rs2.next();
					});
				}));
		assertEquals(1, events.size());
		assertEquals(OTHER, events.get(0).getKindOperator());
		assertTrue(this == events.get(0).getOperAny());

		// Anonymous RowSet
		System.out.println("=== anon next");
		events.clear();
		assertTrue(EQ.invokeLatchWait("tick3", s -> System.out.println(s),
				() -> {
					rs2.next();
					rs2.next();
					rs2.next();
				}));
		assertEquals(1, events.size());
		assertEquals(ANON, events.get(0).getKindOperator());
		assertTrue(null == events.get(0).getOperAny());

		// Opening insert row
		System.out.println("=== insert");
		events.clear();
		assertTrue(EQ.invokeLatchWait("tick4", s -> System.out.println(s),
				() -> { model2.getAction(RowsAction.ACT_ADD).actionPerformed(null); }));
		assertEquals(1, events.size());
		assertTrue(RowSetState.isInserting(model2.getRowSet()));

		events.clear();
		assertTrue(EQ.invokeLatchWait("tick5", s -> System.out.println(s),
				() -> { model2.getAction(RowsAction.ACT_REVERT).actionPerformed(null); }));
		assertFalse(RowSetState.isInserting(model2.getRowSet()));

		// Opening insert row on *empty* rowSet
		RowsModel model3 = RowsModel.create(TinyRS.getRSEmpty(), null);
		System.out.println("=== empty insert");
		events.clear();
		assertTrue(EQ.invokeLatchWait("tick6", s -> System.out.println(s),
				() -> { model3.getAction(RowsAction.ACT_ADD).actionPerformed(null); }));
		assertEquals(1, events.size());
		assertTrue(RowSetState.isInserting(model3.getRowSet()));

		events.clear();
		assertTrue(EQ.invokeLatchWait("tick7", s -> System.out.println(s),
				() -> { model3.getAction(RowsAction.ACT_REVERT).actionPerformed(null); }));
		assertFalse(RowSetState.isInserting(model3.getRowSet()));
	}

	/**
	 * Test of Basics for of class RowsModelEventHandling.
	 * @throws java.sql.SQLException
	 * @throws java.lang.ClassNotFoundException
	 * @throws java.lang.InterruptedException
	 * @throws java.lang.reflect.InvocationTargetException
	 */
	@Test
	public void testAbsorbHandling()
			throws SQLException, ClassNotFoundException,
			InterruptedException, InvocationTargetException
	{
		System.out.println("Absorb");

		H2.clean();

		RowSet rs1 = TinyRS.getRS1();
		RowsModel model1 = RowsModel.create(rs1, null);

		RowSet rs2 = TinyRS.getRS2();
		RowsModel model2 = RowsModel.create(rs2, null);

		oneTestBusReceiver = EQ.setupBusReceiver();
		EQ.GetRowsModelEvent events = oneTestBusReceiver.events();


		events.clear();
		assertTrue(EQ.invokeLatchWait("tick1", s -> System.out.println(s),
				() -> {
					model1.first();
					model1.next();
					model1.next();	// merged
					model1.first();
					model1.next();
					model1.next();	// merged
				}));
		assertEquals(4, events.size());
		assertEquals(ACT_FIRST, events.get(0).getOperAct());
		assertEquals(ACT_NEXT,  events.get(1).getOperAct());
		assertEquals(ACT_FIRST, events.get(2).getOperAct());
		assertEquals(ACT_NEXT,  events.get(3).getOperAct());
		

		System.out.println("===");
		events.clear();
		assertTrue(EQ.invokeLatchWait("tick2", s -> System.out.println(s),
				() -> {
					// The events by model2 prevent merging
					model1.first();
					model1.next();
					model2.next();
					model1.next();
					model1.next();	// merged
					model1.next();	// merged
					model2.next();
				}));
		assertEquals(5, events.size());
		assertEquals(model1, events.get(0).getRowsModel());
		assertEquals(ACT_FIRST, events.get(0).getOperAct());
		assertEquals(model1, events.get(1).getRowsModel());
		assertEquals(ACT_NEXT, events.get(1).getOperAct());

		assertEquals(model2, events.get(2).getRowsModel());
		assertEquals(ACT_NEXT, events.get(2).getOperAct());

		assertEquals(model1, events.get(3).getRowsModel());
		assertEquals(ACT_NEXT, events.get(3).getOperAct());

		assertEquals(model2, events.get(4).getRowsModel());
		assertEquals(ACT_NEXT, events.get(4).getOperAct());

		System.out.println("===");
		events.clear();
		assertTrue(EQ.invokeLatchWait("tick3", s -> System.out.println(s),
				() -> {
					model1.rsOp(this, () -> {
						rs1.first();
						rs1.first();
						rs1.first();
						rs1.first();
						rs1.first();
						rs1.first();
					});
				}));
		assertEquals(1, events.size());
		assertEquals(model1, events.get(0).getRowsModel());
		assertEquals(null, events.get(0).getOperAct());
		assertEquals(OTHER, events.get(0).getKindOperator());
	}

	/**
	 * Test of Nested for of class RowsModelEventHandling.
	 * @throws java.sql.SQLException
	 * @throws java.lang.ClassNotFoundException
	 * @throws java.lang.InterruptedException
	 * @throws java.lang.reflect.InvocationTargetException
	 */
	@Test
	public void testNestedHandling()
	throws SQLException, ClassNotFoundException,
			InterruptedException, InvocationTargetException
	{
		System.out.println("Nested");

		H2.clean();

		RowSet rs1 = TinyRS.getRS1();
		RowsModel model1 = RowsModel.create(rs1, null);

		RowSet rs2 = TinyRS.getRS2();
		RowsModel model2 = RowsModel.create(rs2, null);

		oneTestBusReceiver = EQ.setupBusReceiver();
		EQ.GetRowsModelEvent events = oneTestBusReceiver.events();

		events.clear();
		assertTrue(EQ.invokeLatchWait("tick1", s -> System.out.println(s),
				() -> {
					model1.rsOp(this, () -> {
						rs1.first();
						rs1.first();
					});
				}));
		assertEquals(1, events.size());
		assertEquals(OTHER, events.get(0).getKindOperator());

		System.out.println("===");
		events.clear();
		assertTrue(EQ.invokeLatchWait("tick1", s -> System.out.println(s),
				() -> {
					model1.rsOp(this, () -> {
						rs1.first();
						rs2.first();
						rs1.first();
					});
				}));
		assertEquals(3, events.size());
		assertEquals(OTHER, events.get(0).getKindOperator());
		assertEquals(rs1,   events.get(0).getRowSet());
		assertEquals(ANON,  events.get(1).getKindOperator());
		assertEquals(rs2,   events.get(1).getRowSet());
		assertEquals(OTHER, events.get(2).getKindOperator());
		assertEquals(rs1,   events.get(2).getRowSet());

		System.out.println("===");
		events.clear();
		assertTrue(EQ.invokeLatchWait("tick1", s -> System.out.println(s),
				() -> {
					model1.rsOp(this, () -> {
						rs1.first();
						model2.rsOp(this, () -> {
							rs2.first();
						});
						rs1.first();
					});
				}));
		assertEquals(3, events.size());
		assertEquals(OTHER, events.get(0).getKindOperator());
		assertEquals(model1, events.get(0).getRowsModel());
		assertEquals(rs1, events.get(0).getRowSet());

		assertEquals(OTHER, events.get(1).getKindOperator());
		assertEquals(model2, events.get(1).getRowsModel());
		assertEquals(rs2, events.get(1).getRowSet());

		assertEquals(OTHER, events.get(2).getKindOperator());
		assertEquals(model1, events.get(2).getRowsModel());
		assertEquals(rs1, events.get(2).getRowSet());

		System.out.println("===");
		events.clear();
		assertTrue(EQ.invokeLatchWait("tick1", s -> System.out.println(s),
				() -> {
					model1.rsOp(this, () -> {
						rs1.first();
						model2.next();
						rs1.first();
					});
				}));
		assertEquals(3, events.size());
		assertEquals(OTHER, events.get(0).getKindOperator());
		assertEquals(model1, events.get(0).getRowsModel());
		assertEquals(rs1, events.get(0).getRowSet());

		assertEquals(ACTION, events.get(1).getKindOperator());
		assertEquals(model2, events.get(1).getRowsModel());
		assertEquals(rs2, events.get(1).getRowSet());

		assertEquals(OTHER, events.get(2).getKindOperator());
		assertEquals(model1, events.get(2).getRowsModel());
		assertEquals(rs1, events.get(2).getRowSet());
	}

	// /**
	//  * Test of postAsync method, of class RowsModelEventHandling.
	//  */
	// // @Test
	// public void testPostAsync()
	// {
	// 	System.out.println("postAsync");
	// 	RowsModelEvent event = null;
	// 	RowsModelEventHandling.postAsync(event);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of dumpQueuedEvents method, of class RowsModelEventHandling.
	//  */
	// @Test
	// public void testDumpQueuedEvents()
	// {
	// 	System.out.println("dumpQueuedEvents");
	// 	String tag = "";
	// 	Deque<RowsModelEvent> evs = null;
	// 	StringBuilder _sb = null;
	// 	String tag2 = "";
	// 	StringBuilder expResult = null;
	// 	StringBuilder result = RowsModelEventHandling.dumpQueuedEvents(tag, evs, _sb, tag2);
	// 	assertEquals(expResult, result);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of addToAllEvents method, of class RowsModelEventHandling.
	//  */
	// @Test
	// public void testAddToAllEvents()
	// {
	// 	System.out.println("addToAllEvents");
	// 	RowsModelEvent event = null;
	// 	RowsModelEventHandling.addToAllEvents(event);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of latestEvents method, of class RowsModelEventHandling.
	//  */
	// @Test
	// public void testDumpAllEvents()
	// {
	// 	System.out.println("latestEvents");
	// 	String tag = "";
	// 	RowsModelEventHandling.latestEvents(tag);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of verifyEDT method, of class RowsModelEventHandling.
	//  */
	// @Test
	// public void testVerifyEDT()
	// {
	// 	System.out.println("verifyEDT");
	// 	RowsModelEventHandling.verifyEDT();
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }
	
}
