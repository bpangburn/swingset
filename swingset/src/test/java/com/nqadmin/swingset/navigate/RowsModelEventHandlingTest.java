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
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.sql.RowSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Throwables;
import com.nqadmin.swingset.mock.H2;
import com.raelity.lib.eventbus.WeakEventBus;
import com.raelity.lib.eventbus.WeakSubscribe;

import static com.nqadmin.swingset.navigate.RowsAction.*;
import static com.nqadmin.swingset.navigate.RowsEvent.OperatorKind.*;
import static com.nqadmin.swingset.navigate.Utils.getGlobalEventBus;
import static com.nqadmin.swingset.utils.SSUtils.isJunit;
import static com.raelity.lib.eventbus.WeakEventBus.register;
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
		PerTestDispatch = null;
		if (OneTestBusReceiver != null)
			WeakEventBus.unregister(OneTestBusReceiver, getGlobalEventBus());
		OneTestBusReceiver = null;
	}

	/** x */
	public interface DBRunnable
	{
		/** @throws SQLException */
		public void run() throws SQLException ;
	}
	Runnable asRunnable(DBRunnable r)
	{
		return () -> {
			try {
				r.run();
			} catch (SQLException ex) {
				Throwables.throwIfUnchecked(ex);
				throw new RuntimeException(ex);
			}
		};
	}

	int timeoutVal() {
		return 0;
	}
	void await(CountDownLatch latch) throws InterruptedException
	{
		int seconds = timeoutVal();
		if (seconds == 0)
			latch.await();
		else
			latch.await(seconds, TimeUnit.SECONDS);
	}

	BusReceiver OneTestBusReceiver; // Strong Reference
	ConsumerEx<RowsEvent> PerTestDispatch;

	class BusReceiver {
		/**
		 * Catch RowSet events; update the component's display.
		 * Ignore events that came from this component; they are handled internally.
		 * Only events from "our" RowSet are handled.
		 * @param ev 
		 */
		@WeakSubscribe
		public void handleRowSetEvent(RowsEvent ev)
		{
			System.out.println("EventBus: " + ev.toString());
			if (PerTestDispatch != null)
				try {
					PerTestDispatch.accept(ev);
			} catch (Exception ex) {
				System.err.println("EXCEPTION: " + ex.getLocalizedMessage());
				Throwables.throwIfUnchecked(ex);
				throw new RuntimeException(ex);
			}
		}
	}

	/** Like Runnable, but may throw SQLException
	 * @param <T>
	 */
	public interface ConsumerEx<T>
	{
		/**
		 * @param t
		 * @throws SQLException
		 */
		public void accept(T t) throws Exception ;
	}

	private void setupBusReceiver()
	{
		OneTestBusReceiver = new BusReceiver();
		register(OneTestBusReceiver, getGlobalEventBus());
	}

	private RowSet getRS1() throws SQLException, ClassNotFoundException
	{
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl1
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, c_tinyint tinyint);
            INSERT INTO tbl1 VALUES
				(11, 1), (12, 1), (13, 1), (14, 1), (15, 1),
				(16, 1), (17, 1), (18, 1)
            ;
            """);
		rs.setCommand("SELECT * FROM tbl1");
		return rs;
	}

	private RowSet getRS2() throws SQLException, ClassNotFoundException
	{
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl2
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, c_tinyint tinyint);
            INSERT INTO tbl2 VALUES
            	(21, 1), (22, 1), (23, 1), (24, 1), (25, 1),
				(26, 1), (27, 1), (28, 1), (29, 1)
            ;
            """);
		rs.setCommand("SELECT * FROM tbl2");
		return rs;
	}

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

		RowSet rs1 = getRS1();
		RowsModel model1 = RowsModel.create(rs1);

		RowSet rs2 = getRS2();
		RowsModel model2 = RowsModel.create(rs2);

		setupBusReceiver();


		List<RowsEvent> events = new ArrayList<>();
		CountDownLatch latch;
		PerTestDispatch = (ev) -> {
			events.add(ev);
		};

		latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		events.clear();
		// all the "next" collapsed into a single event
		EventQueue.invokeLater(() -> {	// actually right away since not in EDT
			model1.next();
			model1.next();	// merged
			model1.next();	// merged
		});
		await(latch);
		assertEquals(1, events.size());
		assertEquals(ACTION, events.get(0).getKindOperator());
		assertEquals(ACT_NEXT, events.get(0).getOperAct());

		System.out.println("===");
		latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		events.clear();
		EventQueue.invokeLater(asRunnable(() -> {	// actually right away since not in EDT
			model2.rsOp(this, () -> {
				rs2.next();
				rs2.next();
				rs2.next();
			});
		}));
		await(latch);
		assertEquals(1, events.size());
		assertEquals(OTHER, events.get(0).getKindOperator());
		assertTrue(this == events.get(0).getOperAny());

		// Anonymous RowSet
		System.out.println("===");
		latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		events.clear();
		EventQueue.invokeLater(asRunnable(() -> {	// actually right away since not in EDT
			rs2.next();
			rs2.next();
			rs2.next();
		}));
		await(latch);
		assertEquals(1, events.size());
		assertEquals(ANON, events.get(0).getKindOperator());
		assertTrue(null == events.get(0).getOperAny());
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

		RowSet rs1 = getRS1();
		RowsModel model1 = RowsModel.create(rs1);

		RowSet rs2 = getRS2();
		RowsModel model2 = RowsModel.create(rs2);

		setupBusReceiver();


		List<RowsEvent> events = new ArrayList<>();
		CountDownLatch latch;
		PerTestDispatch = (ev) -> {
			events.add(ev);
		};


		latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		events.clear();
		EventQueue.invokeLater(asRunnable(() -> {	// actually right away since not in EDT
			model1.first();
			model1.next();
			model1.next();	// merged
			model1.first();
			model1.next();
			model1.next();	// merged
		}));
		await(latch);
		assertEquals(4, events.size());
		assertEquals(ACT_FIRST, events.get(0).getOperAct());
		assertEquals(ACT_NEXT, events.get(1).getOperAct());
		assertEquals(ACT_FIRST, events.get(2).getOperAct());
		assertEquals(ACT_NEXT, events.get(3).getOperAct());
		

		System.out.println("===");
		latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		events.clear();
		EventQueue.invokeLater(asRunnable(() -> {	// actually right away since not in EDT
			// The events by model2 prevent merging
			model1.first();
			model1.next();
			model2.next();
			model1.next();
			model1.next();	// merged
			model1.next();	// merged
			model2.next();
		}));
		await(latch);
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
		latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		events.clear();
		EventQueue.invokeLater(asRunnable(() -> {	// actually right away since not in EDT
			model1.rsOp(this, () -> {
				rs1.first();
				rs1.first();
				rs1.first();
				rs1.first();
				rs1.first();
				rs1.first();
			});
		}));
		await(latch);
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

		RowSet rs1 = getRS1();
		RowsModel model1 = RowsModel.create(rs1);

		RowSet rs2 = getRS2();
		RowsModel model2 = RowsModel.create(rs2);

		setupBusReceiver();


		List<RowsEvent> events = new ArrayList<>();
		CountDownLatch latch;
		PerTestDispatch = (ev) -> {
			events.add(ev);
		};

		latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		events.clear();
		EventQueue.invokeLater(asRunnable(() -> {	// actually right away since not in EDT
			model1.rsOp(this, () -> {
				rs1.first();
				rs1.first();
			});
		}));
		await(latch);
		assertEquals(1, events.size());
		assertEquals(OTHER, events.get(0).getKindOperator());

		System.out.println("===");
		latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		events.clear();
		EventQueue.invokeLater(asRunnable(() -> {	// actually right away since not in EDT
			model1.rsOp(this, () -> {
				rs1.first();
				rs2.first();
				rs1.first();
			});
		}));
		await(latch);
		assertEquals(3, events.size());
		assertEquals(OTHER, events.get(0).getKindOperator());
		assertEquals(rs1, events.get(0).getRowSet());
		assertEquals(ANON, events.get(1).getKindOperator());
		assertEquals(rs2, events.get(1).getRowSet());
		assertEquals(OTHER, events.get(2).getKindOperator());
		assertEquals(rs1, events.get(2).getRowSet());

		System.out.println("===");
		latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		events.clear();
		EventQueue.invokeLater(asRunnable(() -> {	// actually right away since not in EDT
			model1.rsOp(this, () -> {
				rs1.first();
				model2.rsOp(this, () -> {
					rs2.first();
				});
				rs1.first();
			});
		}));
		await(latch);
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
		latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		events.clear();
		EventQueue.invokeLater(asRunnable(() -> {	// actually right away since not in EDT
			model1.rsOp(this, () -> {
				rs1.first();
				model2.next();
				rs1.first();
			});
		}));
		await(latch);
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
	//  * Test of dumpLatestEvents method, of class RowsModelEventHandling.
	//  */
	// @Test
	// public void testDumpAllEvents()
	// {
	// 	System.out.println("dumpLatestEvents");
	// 	String tag = "";
	// 	RowsModelEventHandling.dumpLatestEvents(tag);
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
