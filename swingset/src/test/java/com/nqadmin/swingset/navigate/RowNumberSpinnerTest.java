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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.sql.RowSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nqadmin.swingset.mock.H2;

import static com.nqadmin.swingset.utils.SSUtils.isJunit;
import static org.junit.jupiter.api.Assertions.*;

/**
 * x
 */
public class RowNumberSpinnerTest
{
	
	/** x */
	public RowNumberSpinnerTest()
	{
		isJunit();	// Make sure it's set; when using invokeLater, can be missed.
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

	RowSet getRS1() throws SQLException, ClassNotFoundException
	{
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl1
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, c_tinyint tinyint);
            INSERT INTO tbl1 VALUES
            	(11, 1), (12, 1), (13, 1), (14, 1)
            ;
            """);
		rs.setCommand("SELECT * FROM tbl1");
		return rs;
	}

	RowSet getRS2() throws SQLException, ClassNotFoundException
	{
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl2
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, c_tinyint tinyint);
            INSERT INTO tbl2 VALUES
            	(21, 1), (22, 1), (23, 1), (24, 1), (25, 1)
            ;
            """);
		rs.setCommand("SELECT * FROM tbl2");
		return rs;
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

	@SuppressWarnings("CallToPrintStackTrace")
	boolean invokeLatereventLatchWait(String tag, Runnable r)
			throws InterruptedException, InvocationTargetException
	{
		boolean error[] = new boolean[1];
		EventQueue.invokeAndWait(() -> {System.err.println(tag + "Enter");});
		CountDownLatch latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		EventQueue.invokeLater(() -> { // actually right away since not in EDT
			try {
				r.run();
			} catch (Exception ex) {
				ex.printStackTrace();
				latch.countDown();
				error[0] = true;
			}
		});
		await(latch);
		EventQueue.invokeAndWait(() -> {System.err.println(tag + "Exit");});
		return error[0];
	}

	/**
	 * Test of setAction method, of class RowNumberSpinner.
	 * @throws java.lang.Exception
	 */
	@Test
	@SuppressWarnings({"BroadCatchBlock", "TooBroadCatch", "CallToPrintStackTrace", "UseSpecificCatch"})
	public void testSetAction() throws Exception
	{
		System.out.println("setAction");

		H2.clean();

		RowSet rs1 = getRS1();
		RowsModel model1 = RowsModel.create(rs1);

		RowSet rs2 = getRS2();
		
		RowNumberSpinner spinner = new RowNumberSpinner(model1);

		// Verify that there is only one actionPerformed per setValue.
		// Verify that the correct rowSet cursor is modified,
		// and that the "other" rowSet cursor is not modified.

		// Can put the following after rs?.getRow()
		//checkRowSetPos(rs1_row, rs1, rs2_row, rs2);

		EventQueue.invokeAndWait(() -> {
			try {
				int rs1_row;
				@SuppressWarnings({"UnusedAssignment", "unused"})
				int rs2_row = -1;
				@SuppressWarnings("unused")
				int row = ((Number)spinner.getValue()).intValue();
				assertEquals(1, row);
				rs1_row = rs1.getRow();
				assertEquals(1, rs1_row);
				
				spinner.setValue(3);
				rs1_row = rs1.getRow();
				assertEquals(3, rs1_row);
				assertEquals(1, checkGoto());
				spinner.setValue(2);
				rs1_row = rs1.getRow();
				assertEquals(2, rs1_row);
				assertEquals(1, checkGoto());
				// rs1 has 4 rows
				assertEquals(4, spinner.getModel().getMaximum());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		int rs1_row = rs1.getRow();
		@SuppressWarnings("UnusedAssignment")
		int rs2_row = -1;

		assertFalse(invokeLatereventLatchWait("tick1", () -> model1.setRowSet(rs2)));
		
		// rs2 has 5 rows
		assertEquals(5, spinner.getModel().getMaximum());
		rs2_row = rs2.getRow();
		assertEquals(1, rs2_row);
		spinner.setValue(3);
		rs2_row = rs2.getRow();
		assertEquals(3, rs2_row);
		assertEquals(2, rs1_row);
		assertEquals(1, checkGoto());
		spinner.setValue(2);
		rs2_row = rs2.getRow();
		assertEquals(2, rs2_row);
		assertEquals(2, rs1_row);
		assertEquals(1, checkGoto());

		assertFalse(invokeLatereventLatchWait("tick2", () -> model1.setRowSet(rs1)));

		// rs1 has 4 rows
		assertEquals(4, spinner.getModel().getMaximum());
		spinner.setValue(3);
		rs1_row = rs1.getRow();
		assertEquals(3, rs1_row);
		assertEquals(2, rs2_row);
		assertEquals(1, checkGoto());
		spinner.setValue(2);
		rs1_row = rs1.getRow();
		assertEquals(2, rs1_row);
		assertEquals(2, rs2_row);
		assertEquals(1, checkGoto());
	}
	
	private int nGoto;
	private int checkGoto() {
		int prevGoto = nGoto;
		nGoto = RowsActions.getCount(RowsAction.ACT_GOTOROW);
		int n = nGoto - prevGoto;
		//System.err.printf("N_GOTO: %d\n", n);
		return n;
	}

	@SuppressWarnings("unused")
	private void checkRowSetPos(int r1, RowSet rs1, int r2, RowSet rs2) throws SQLException {
		// int prevGoto = nGoto;
		// nGoto = NavigateActions.getCount(RowsAction.ACT_GOTOROW);
		// int n = nGoto - prevGoto;
		System.err.printf("POS: rs1%s %d, rs2%s %d\n",
				rs1.getRow() != r1 ? " ERROR" : "", r1,
				rs2.getRow() != r2 ? " ERROR" : "", r2
		);
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
	// 	RowSet rs1 = getRS1();
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
