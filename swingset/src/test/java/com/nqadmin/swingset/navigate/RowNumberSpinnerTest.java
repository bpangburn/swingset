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


import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.RowSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nqadmin.swingset.mock.H2;
import com.nqadmin.swingset.mock.TestLogging;
import com.nqadmin.swingset.mock.TinyRS;
import com.nqadmin.swingset.navigate.EQ.BusReceiver;
import com.raelity.lib.eventbus.WeakEventBus;

import static com.nqadmin.swingset.navigate.Utils.getGlobalEventBus;
import static com.nqadmin.swingset.utils.SSUtils.getLoggerName;
import static com.nqadmin.swingset.utils.SSUtils.isJunit;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.util.logging.Level.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * x
 */
public class RowNumberSpinnerTest
{
	private static final Logger LOG = Logger.getLogger(getLoggerName());
	
	/** x */
	public RowNumberSpinnerTest()
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
		if (busReceiver != null)
			WeakEventBus.unregister(busReceiver, getGlobalEventBus());
		busReceiver = null;
	}

	BusReceiver busReceiver; // Strong Reference

	/**
	 * Test of setAction method, of class RowNumberSpinner.
	 * @throws java.lang.Exception
	 */
	@Test
	@SuppressWarnings({"BroadCatchBlock", "TooBroadCatch", "CallToPrintStackTrace", "UseSpecificCatch"})
	public void testSetAction() throws Exception
	{
		LOG.log(INFO, "setAction");

		busReceiver = EQ.setupBusReceiver();
		EQ.GetRowsModelEvent events = busReceiver.events();

		H2.clean();
		RowSet rs1 = TinyRS.getRS1_4();
		RowsModel model1 = RowsModel.create(rs1);
		RowSet rs2 = TinyRS.getRS2_5();
		
		RowNumberSpinner spinner = new RowNumberSpinner(model1);

		// Verify that there is only one actionPerformed per setValue.
		// Verify that the correct rowSet cursor is modified,
		// and that the "other" rowSet cursor is not modified.

		checkGoto(); // initialize state
		int row = ((Number)spinner.getValue()).intValue();
		assertEquals(1, row);
		assertEquals(1, rs1.getRow());

		assertTrue(EQ.invokeLatchWait("tick1", s -> LOG.log(INFO, s), null,
				() -> spinner.setValue(3)));
		assertEquals(1, events.size());
		assertEquals(3, rs1.getRow());
		assertEquals(1, checkGoto());

		events.clear();
		assertTrue(EQ.invokeLatchWait("tick2", s -> LOG.log(INFO, s), null,
				() -> spinner.setValue(2)));
		assertEquals(1, events.size());
		assertEquals(2, rs1.getRow());
		assertEquals(1, checkGoto());
		// rs1 has 4 rows
		assertEquals(4, spinner.getModel().getMaximum());


		events.clear();
		assertTrue(EQ.invokeLatchWait("tick3", s -> LOG.log(INFO, s), null,
				() -> model1.setRowSet(rs2)));
		assertEquals(1, events.size());
		checkGoto(); // initialize state
		// rs2 has 5 rows
		assertEquals(5, spinner.getModel().getMaximum());
		assertEquals(1, rs2.getRow());

		events.clear();
		assertTrue(EQ.invokeLatchWait("tick4", s -> LOG.log(INFO, s), null,
				() -> spinner.setValue(3)));
		assertEquals(1, events.size());
		assertEquals(3, rs2.getRow());
		assertEquals(2, rs1.getRow());
		assertEquals(1, checkGoto());

		events.clear();
		assertTrue(EQ.invokeLatchWait("tick5", s -> LOG.log(INFO, s), null,
				() -> spinner.setValue(2)));
		assertEquals(1, events.size());
		assertEquals(2, rs2.getRow());
		assertEquals(2, rs1.getRow());
		assertEquals(1, checkGoto());


		events.clear();
		assertTrue(EQ.invokeLatchWait("tick6", s -> LOG.log(INFO, s), null,
				() -> model1.setRowSet(rs1)));
		assertEquals(1, events.size());
		checkGoto(); // initialize state

		// rs1 has 4 rows
		assertEquals(4, spinner.getModel().getMaximum());

		events.clear();
		assertTrue(EQ.invokeLatchWait("tick7", s -> LOG.log(INFO, s), null,
				() -> spinner.setValue(3)));
		assertEquals(1, events.size());
		assertEquals(3, rs1.getRow());
		assertEquals(2, rs2.getRow());
		assertEquals(1, checkGoto());

		events.clear();
		assertTrue(EQ.invokeLatchWait("tick8", s -> LOG.log(INFO, s), null,
				() -> spinner.setValue(2)));
		assertEquals(1, events.size());
		assertEquals(2, rs1.getRow());
		assertEquals(2, rs2.getRow());
		assertEquals(1, checkGoto());
	}
	
	private int nGoto;
	/** return number of goto actions */
	private int checkGoto() {
		int prevGoto = nGoto;
		nGoto = RowsActions.getCount(RowsAction.ACT_GOTOROW);
		int n = nGoto - prevGoto;
		//System.out.printf("N_GOTO: %d\n", n);
		return n;
	}

	@SuppressWarnings("unused")
	private void checkRowSetPos(int r1, RowSet rs1, int r2, RowSet rs2) throws SQLException {
		// int prevGoto = nGoto;
		// nGoto = NavigateActions.getCount(RowsAction.ACT_GOTOROW);
		// int n = nGoto - prevGoto;
		LOG.log(INFO, sf("POS: rs1%s %d, rs2%s %d\n",
				rs1.getRow() != r1 ? " ERROR" : "", r1,
				rs2.getRow() != r2 ? " ERROR" : "", r2
		));
	}

	// /**
	//  * Test of setModel method, of class RowNumberSpinner.
	//  * @throws java.sql.SQLException
	//  * @throws java.lang.ClassNotFoundException
	//  */
	// @Test
	// @SuppressWarnings({"ThrowableResultIgnored", "deprecation"})
	// public void testSetModel() throws SQLException, ClassNotFoundException
	// {
	// 	System.out.println("setModel");

	// 	H2.clean();
	// 	RowSet rs1 = getRS1_4();
	// 	RowsModel rowsModel = RowsModel.create(rs1);

	// 	RowNumberSpinner spinner = new RowNumberSpinner(rowsModel);
	// 	SpinnerNumberModel defaultSpinnerModel = spinner.getModel();

	// 	assertThrows(IllegalCallerException.class,
	// 				 () -> spinner.setModel(new SpinnerNumberModel()));
	// 	assertTrue(defaultSpinnerModel == spinner.getModel());

	// 	spinner.setModel(rowsModel);
	// 	assertFalse(defaultSpinnerModel == spinner.getModel());
	// }




	// /**
	//  * Test of removeTinyArrows method, of class RowNumberSpinner.
	//  */
	// @Test
	// public void testRemoveTinyArrows()
	// {
	// 	System.out.println("removeTinyArrows");
	// 	Dimension targetSpinnerSize = null;
	// 	RowNumberSpinner instance = new RowNumberSpinner();
	// 	instance.removeTinyArrows(targetSpinnerSize);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setWindowUpDownKeysEnable method, of class RowNumberSpinner.
	//  */
	// @Test
	// public void testSetWindowUpDownKeysEnable()
	// {
	// 	System.out.println("setWindowUpDownKeysEnable");
	// 	boolean enable = false;
	// 	RowNumberSpinner instance = new RowNumberSpinner();
	// 	instance.setWindowUpDownKeysEnable(enable);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }

	// /**
	//  * Test of setUpDownKeysEnable method, of class RowNumberSpinner.
	//  */
	// @Test
	// public void testSetUpDownKeysEnable()
	// {
	// 	System.out.println("setUpDownKeysEnable");
	// 	boolean enable = false;
	// 	RowNumberSpinner instance = new RowNumberSpinner();
	// 	instance.setUpDownKeysEnable(enable);
	// 	// TODO review the generated test code and remove the default call to fail.
	// 	fail("The test case is a prototype.");
	// }
	
}
