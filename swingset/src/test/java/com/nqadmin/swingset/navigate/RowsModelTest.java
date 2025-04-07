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

import java.awt.Container;
import java.awt.EventQueue;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.RowSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nqadmin.swingset.SSDBNavImpl;
import com.nqadmin.swingset.core.TextField;
import com.nqadmin.swingset.datasources.DefaultSSDBSupport;
import com.nqadmin.swingset.mock.H2;
import com.nqadmin.swingset.mock.TestLogging;
import com.nqadmin.swingset.utils.CentralLookup;

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
		CentralLookup.getDefault().add(new DefaultSSDBSupport());
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

	private RowSet getRS1() throws SQLException, ClassNotFoundException
	{
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl1
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, some_int tinyint);
            INSERT INTO tbl1 VALUES
				(11, 1), (12, 1), (13, 1), (14, 1), (15, 1)
            ;
            """);
		rs.setCommand("SELECT * FROM tbl1");
		return rs;
	}

	private RowSet getRS1NotNull() throws SQLException, ClassNotFoundException
	{
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl1NN
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, some_int tinyint not null);
            INSERT INTO tbl1NN VALUES
				(11, 1), (12, 1), (13, 1), (14, 1), (15, 1)
            ;
            """);
		rs.setCommand("SELECT * FROM tbl1NN");
		return rs;
	}

	private RowSet getRS2() throws SQLException, ClassNotFoundException
	{
		// This table has a different type for some_int
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl2
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, some_int int);
            INSERT INTO tbl2 VALUES
            	(21, 1), (22, 1), (23, 1), (24, 1), (25, 1)
            ;
            """);
		rs.setCommand("SELECT * FROM tbl2");
		return rs;
	}

	//SSDBNav dbNav = new SSDBNavImpl(this)
	class DbNav extends SSDBNavImpl
	{
		private final RowsModel rowsModel;
		private final Connection connection;

		public DbNav(Container container, RowsModel rowsModel)
				throws ClassNotFoundException, SQLException
		{
			super(container);
			this.rowsModel = rowsModel;
			this.connection = H2.getCon();
		}

		private RowSet getRowSet() { return rowsModel.getRowSet(); }

		/**
		 * Re-query the RowSet following a deletion. This is needed for H2.
		 */
		@Override
		public void performPostDeletionOps()
		{
			super.performPostDeletionOps();
			try {
				getRowSet().execute();
			} catch (final SQLException se) {
				logger.log(Level.ERROR, "SQL Exception.", se);
			}
		}

		/**
		 * Requery the rowset following an insertion. This is needed for H2.
		 */
		@Override
		public void performPostInsertOps()
		{
			super.performPostInsertOps();
			try {
				getRowSet().execute();
			} catch (final SQLException se) {
				logger.log(Level.ERROR, "SQL Exception.", se);
			}
		}

		/**
		 * Obtain and set the PK value for the new record & perform any other
		 * actions needed before an insert.
		 */
		@Override
		public void performPreInsertOps()
		{
			// SSDBNavImpl will clear the component values
			super.performPreInsertOps();

			try (final ResultSet rs = connection.createStatement(
					ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
					.executeQuery("SELECT nextval('supplier_data_seq') as nextVal;");
					) {
				// Get the new record id.
				rs.next();

				//
				// NOTE: insert NOT_USED
				//
				// final int supplierID = rs.getInt("nextVal");
				// txtSupplierID.setText(String.valueOf(supplierID));
				
				// // SET OTHER DEFAULTS
				//  txtSupplierName.setText(null);
				//  txtSupplierCity.setText(null);
				//  txtSupplierStatus.setText("0");

			} catch(final SQLException se) {
				logger.log(Level.ERROR, "SQL Exception occured initializing new record.", se);
			} catch(final Exception e) {
				logger.log(Level.ERROR, "Exception occured initializing new record.", e);
			}
		}
	};

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

	/**
	 * Test of bind() method, of class RowsModel.
	 * @throws java.lang.Exception
	 */
	@Test
	public void testBind() throws Exception
	{
		LOG.log(INFO, "bind");
		String keyCol = "supplier_id";
		List<Exception> exs = new ArrayList<>();

		H2.clean();

		// Create two RowSets, the same columnNames have different column indexes.
		RowSet rs1 = H2.createSimpleSupplierData(7, 5, 0);
		RowSet rs2 = H2.createSimpleSupplierData(8, 6, 1);

		RowsModel rowsModel = RowsModel.create(null);
		TextField tf = new TextField();
		DbNav _dbNav = null;
		try {
			_dbNav = new DbNav(tf, rowsModel);
		} catch (ClassNotFoundException | SQLException ex) {
			exs.add(ex);
		}
		assertTrue(exs.isEmpty());
		DbNav dbNav = _dbNav;
		
		// Make sure the row sets are set up correctly, different nRow, colIdx.
		rowsModel.setRowSet(rs1, dbNav);
		assertEquals(5, rowsModel.getRowCount());
		assertEquals(1, rowsModel.getRowSet().findColumn(keyCol));
		rowsModel.setRowSet(rs2, dbNav);
		assertEquals(6, rowsModel.getRowCount());
		assertEquals(4, rowsModel.getRowSet().findColumn(keyCol));

		int idInt;
		String id;

		rowsModel.setRowSet(null, dbNav);
		rowsModel.bind(tf, keyCol);

		rowsModel.setRowSet(rs1, dbNav);
		EventQueue.invokeAndWait(() -> { });
		idInt = rowsModel.getRowSet().getInt(keyCol);
		assertEquals(701, idInt);
		id = tf.getText();
		assertEquals("701", id);

		rowsModel.setRowSet(rs2, dbNav);
		EventQueue.invokeAndWait(() -> { });
		idInt = rowsModel.getRowSet().getInt(keyCol);
		assertEquals(801, idInt);
		id = tf.getText();
		assertEquals("801", id);
	}

	/**
	 * Test of setRowSet method, of class RowsModel.
	 * @throws java.lang.Exception
	 */
	@Test
	@SuppressWarnings("LoggerStringConcat")
	public void testSetRowSetError() throws Exception
	{
		LOG.log(INFO, "setRowSetError");
		String some_int = "some_int";
		List<Exception> exs = new ArrayList<>();

		H2.clean();

		RowsModel rowsModel = RowsModel.create(null);
		TextField tf = new TextField();
		DbNav _dbNav = null;
		try {
			_dbNav = new DbNav(tf, rowsModel);
		} catch (ClassNotFoundException | SQLException ex) {
			exs.add(ex);
		}
		assertTrue(exs.isEmpty());
		DbNav dbNav = _dbNav;

		// Create two RowSets, the same columnNames have different column indexes.
		RowSet rs1 = getRS1();
		RowSet rs2 = getRS2();

		// setRowSet(rs1)
		EventQueue.invokeLater(() -> {
			rowsModel.bind(tf, some_int);
			try {
				rowsModel.setRowSet(rs1, dbNav);
			} catch(Exception ex) {
				exs.add(ex);
			}
		});
		EventQueue.invokeAndWait(() -> { });
		assertTrue(exs.isEmpty());

		// setRowSet(rs2) expect an error since there's a type difference
		EventQueue.invokeLater(() -> {
			try {
				rowsModel.setRowSet(rs2, dbNav);
			} catch(Exception ex) {
				exs.add(ex);
			}
		});
		EventQueue.invokeAndWait(() -> { });
		assertEquals(1, exs.size());
		Exception exx = assertInstanceOf(IllegalArgumentException.class, exs.get(0));
		assertTrue(exx.getMessage().startsWith("JDBCType mismatch"), exx.getMessage());

		//
		// Verify exception if different nullability.
		//
		exs.clear();
		RowSet rs1NN = getRS1NotNull();

		RowsModel rowsModelNN = RowsModel.create(null);
		TextField tfNN = new TextField();

		_dbNav = null;
		try {
			_dbNav = new DbNav(tfNN, rowsModelNN);
		} catch (ClassNotFoundException | SQLException ex) {
			exs.add(ex);
		}
		assertTrue(exs.isEmpty());
		DbNav dbNavNN = _dbNav;

		// setRowSet(rs1)
		EventQueue.invokeLater(() -> {
			rowsModelNN.bind(tfNN, some_int);
			try {
				rowsModelNN.setRowSet(rs1, dbNavNN);
			} catch(Exception ex) {
				exs.add(ex);
			}
		});
		EventQueue.invokeAndWait(() -> { });
		assertTrue(exs.isEmpty());

		// setRowSet(rs1NN) expect an error since there's a type difference
		RowsModel.verifyExecuted(rs1NN);
		EventQueue.invokeLater(() -> {
			try {
				rowsModelNN.setRowSet(rs1NN, dbNavNN);
			} catch(Exception ex) {
				exs.add(ex);
			}
		});
		EventQueue.invokeAndWait(() -> { });
		assertEquals(1, exs.size());
		Exception ex = assertInstanceOf(IllegalArgumentException.class, exs.get(0));
		assertTrue(ex.getMessage().startsWith("Nullability mismatch"), ex.getMessage());
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
