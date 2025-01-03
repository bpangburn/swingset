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
package com.nqadmin.swingset.datasources;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import javax.sql.RowSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.nqadmin.swingset.mock.H2;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is not SS directly; examine how DB does automatic conversions.
 */
public class DbConvertOpsTest
{
	
	/** x */
	public DbConvertOpsTest()
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

	RowSet g_rs;

	/** x
	 * @throws java.lang.Exception */
	//
	// TODO: Try conversions, check overflow.
	//		 Notice with H2 "supportsConvert" is unconditionally true.
	//
	// @Test
	@SuppressWarnings({"ResultOfObjectAllocationIgnored", "ThrowableResultIgnored"})
	public void testSupportsConvert() throws Exception
	{
		System.out.println("SupportsConvert");
		Connection con = H2.getCon();
		DatabaseMetaData md = con.getMetaData();

		if(!md.supportsConvert()) {
			System.out.println("    DOES NOT SUPPORT CONVERT");
			return;
		}
		for (JDBCType t1 : JDBCType.values()) {
			System.out.printf("    %s conversion from to\n", t1);
			boolean has_no_convert = false;
			for (JDBCType t2 : JDBCType.values()) {
				boolean supportsConvert = md.supportsConvert(
						t1.getVendorTypeNumber(), t2.getVendorTypeNumber());
				//System.out.printf("\t%s\t%s\n", supportsConvert ? "YES" : "No", t2);
				if (supportsConvert)
					;
				else {
					System.out.printf("\t%s\t%s\n", "No", t2);
					has_no_convert = true;
				}
			}
			if (!has_no_convert)
				System.out.printf("\tconverts ANYTHING\n");
		}
	}

	/** x
	 * @throws java.lang.Exception */
	//
	// TODO: Try conversions, check overflow.
	//		 Notice with H2 "supportsConvert" is unconditionally true.
	//
	// @Test
	@SuppressWarnings({"ResultOfObjectAllocationIgnored", "ThrowableResultIgnored"})
	public void testMetadata() throws Exception
	{
		System.out.println("Metadata");

		g_rs = H2.getRowSet("""
            CREATE TABLE tbl
            (
                c_pk INTEGER DEFAULT nextval('tbl_seq') NOT NULL PRIMARY KEY,

            	c_tinyint tinyint,
				c_real real
            );

			INSERT INTO tbl VALUES
				(1, 1, 1)
			;
            """);
		g_rs.setCommand("SELECT * FROM tbl");
		g_rs.execute();
		g_rs.next();

		DatabaseMetaData md = H2.getCon().getMetaData();
		System.out.println("ownUpdatesAreVisible: " + md.ownUpdatesAreVisible(
				ResultSet.TYPE_SCROLL_INSENSITIVE
		));
		// ownUpdatesAreVisible: true
		g_rs.updateObject("c_tinyint", 7);
		Object o = g_rs.getObject("c_tinyint"); // returns 1
		System.out.println("" + o);
		g_rs.updateRow();
		o = g_rs.getObject("c_tinyint"); // returns 7
		System.out.println("" + o);
	}

	private final Object NO_EXPECT = new Object();

	private void updateObject(String col, Object val, Object expect)
			throws Exception
	{
		System.out.printf("    %s from %s\n", col, val.getClass().getSimpleName());

		g_rs.updateObject(col, val);
		g_rs.updateRow();

		Object co = g_rs.getObject(col);
		//assertTrue(co.getClass() == val.getClass());

		if (expect == NO_EXPECT)
			return;
		if (expect != null)
			assertEquals(expect, co);
		else if (val instanceof BigDecimal bd)
			assertEquals(bd.compareTo((BigDecimal) co), 0);
		else
			assertEquals(val, co);
	}
	private void updateObject(DT dt, Object val)
			throws Exception
	{
		updateObject(dt.col, val, dt.fExpect.apply(dt));
	}

	/**
	 * Test of ResultSet setObject.
	 * @throws java.lang.Exception
	 */
	@Test
	@SuppressWarnings({"ResultOfObjectAllocationIgnored", "ThrowableResultIgnored"})
	public void testNumericConversions() throws Exception
	{
		System.out.println("NumericConversions by updateObject");

		g_rs = H2.getRowSet("""
            CREATE TABLE tbl
            (
                c_pk INTEGER DEFAULT nextval('tbl_seq') NOT NULL PRIMARY KEY,

            	c_tinyint tinyint,
				c_real real
            );

			INSERT INTO tbl VALUES
				(1, 1, 1)
			;
            """);
		g_rs.setCommand("SELECT * FROM tbl");
		g_rs.execute();
		g_rs.next();

		updateObject("c_tinyint", 127.1, 127); // NO EXCEPTION

		// updateObject("c_tinyint", n_smallint); SQLException: 
		updateObject("c_tinyint", 3, null);
		updateObject("c_tinyint", 4L, 4);
		updateObject("c_tinyint", BigDecimal.valueOf(5), 5);
		updateObject("c_tinyint", "6", 6);
		updateObject("c_tinyint", 7.1, 7); // NO EXCEPTION
		updateObject("c_tinyint", 7.8, 8); // ROUNDING

		updateObject("c_tinyint", 127.1, 127); // NO EXCEPTION
		assertThrows(SQLException.class,
				()->updateObject("c_tinyint", 127.8, 127));

		assertThrows(SQLException.class,
				()->updateObject("c_tinyint", BigDecimal.valueOf(128), null));
		assertThrows(SQLException.class,
				()->updateObject("c_tinyint", "128", 3));

		g_rs.updateObject("c_tinyint", null);
		g_rs.updateRow();

		updateObject("c_real", (double)Float.MAX_VALUE, Float.MAX_VALUE);
		// NOTE: no overflow generated, but do get infinity
		updateObject("c_real", Double.MAX_VALUE, Float.POSITIVE_INFINITY);
		
		g_rs = null;
	}

	/**
	 * Test of ResultSet setObject.
	 * @throws java.lang.Exception
	 */
	@Test
	@SuppressWarnings({"ResultOfObjectAllocationIgnored", "ThrowableResultIgnored"})
	public void testNumericConversionsNoDB() throws Exception
	{
		System.out.println("NumericConversionsNoDB");

		float f = (float)Double.MAX_VALUE;
		assertEquals(Float.POSITIVE_INFINITY, f);

		Double d = Double.MAX_VALUE;
		f = d.floatValue();
		assertEquals(Float.POSITIVE_INFINITY, f);

		assertThrows(NumberFormatException.class, () -> Byte.valueOf("128"));
	}

	//private record DT(String date, String time){}
	private class DT {
		final String col;
		final String date;
		final String time;
		final Function<DT,Object> fExpect;

		public DT(String col, String date, String time, Function<DT,Object> fExpect)
		{
			this.col = col;
			this.date = date;
			this.time = time;
			this.fExpect = fExpect;
		}
	}

	/**
	 * Test of updateColumnText method, of class RowSetOps.
	 * @throws java.lang.Exception
	 */
	@Test
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void testDateConversions() throws Exception
	{
		System.out.println("DateConversions");

		g_rs = H2.getRowSet("""
            CREATE TABLE tbl
            (
                c_pk INTEGER DEFAULT nextval('tbl_seq') NOT NULL PRIMARY KEY,
            	c_date DATE,
            	c_time TIME,
				c_timestamp TIMESTAMP,
				c_varchar varchar
            );

			INSERT INTO tbl VALUES
                      (1, '2000-01-11','11:11:11','2000-01-11 11:11:11','');
            """);
		g_rs.setCommand("SELECT * FROM tbl");
		g_rs.execute();
		g_rs.next();

		String sDate = "2222-02-22";
		String sTime = "12:12:12";
		String sTimestamp = "2222-02-22 22:22:22";

		updateObject("c_date", sDate, Date.valueOf(sDate));
		updateObject("c_time", sTime, Time.valueOf(sTime));
		updateObject("c_timestamp", sTimestamp, Timestamp.valueOf(sTimestamp));
		
		DT dt1 = new DT("c_timestamp", "2222-02-23", "22:22:23",
				(dt) -> Timestamp.valueOf(dt.date + " " + dt.time));
		DT dt2 = new DT("c_date", "2222-02-24", "22:22:24",
				(dt) -> Date.valueOf(dt.date));
		DT dt3 = new DT("c_time", "2222-02-25", "22:22:25",
				(dt) -> Time.valueOf(dt.time));

		for (DT dt : List.of(dt1, dt2, dt3)) {
			Timestamp ts = Timestamp.valueOf(dt.date + " " + dt.time);
			updateObject(dt, ts);
		}

		g_rs.updateObject("c_date", null);
		g_rs.updateObject("c_time", null);
		g_rs.updateObject("c_timestamp", null);
		g_rs.updateRow();

		for (DT dt : List.of(dt1, dt2, dt3)) {
			LocalDateTime ldt = LocalDateTime.parse(dt.date + "T" + dt.time);
			updateObject(dt, ldt);
		}
	}


	// These values do not "narrow" without overflow.
	// Object n_tinyint = Byte.valueOf((byte)1);
	// Object n_smallint = Short.valueOf((short)(Byte.MAX_VALUE + 1));
	// Object n_integer = Short.MAX_VALUE + 1;
	// Object n_bigint = Long.valueOf(Integer.MAX_VALUE + 1);

	// Object c_decimal", "17.1", new BigDecimal("17.1"));
	// Object c_numeric", "18", new BigDecimal("18"));

	// Object c_real", "19.3", 19.3F);
	// Object c_double", "20.3", 20.3);
	// Object c_float", "21.3", 21.3);

	// Object c_boolean", "true", true);

	// Object c_char", "one", "one");
	// Object c_varchar", "two", "two");
	// Object c_nchar", "three", "three");


		// SSComponentInterface comp1 = new SSTextField(rs, "c_date");
		// SSComponentInterface comp2 = new SSTextField(rs, "c_time");
		// SSComponentInterface comp3 = new SSTextField(rs, "c_timestamp");
		// RowSetOps.updateColumnText(comp1, sDate);
		// RowSetOps.updateColumnText(comp2, sTime);
		// RowSetOps.updateColumnText(comp3, sTimestamp);
		// nav.commit();

		// Object co;
		// co = RowSetOps.getColumnObject(comp1);
		// assertEquals(java.sql.Date.valueOf(sDate), co);
		// co = RowSetOps.getColumnObject(comp2);
		// assertEquals(java.sql.Time.valueOf(sTime), co);
		// co = RowSetOps.getColumnObject(comp3);
		// assertEquals(java.sql.Timestamp.valueOf(sTimestamp), co);
}

		// updateColumnText("c_integer", "13", 13);
		// updateColumnText("c_smallint", "14", 14);
		// updateColumnText("c_tinyint", "15", 15);
		// updateColumnText("c_bigint", "16", 16L);
		// updateColumnText("c_decimal", "17.1", new BigDecimal("17.1"));
		// updateColumnText("c_numeric", "18", new BigDecimal("18"));

		// updateColumnText("c_real", "19.3", 19.3F);
		// updateColumnText("c_double", "20.3", 20.3);
		// updateColumnText("c_float", "21.3", 21.3);

		// updateColumnText("c_boolean", "true", true);

		// updateColumnText("c_char", "one", "one");
		// updateColumnText("c_varchar", "two", "two");
		// updateColumnText("c_nchar", "three", "three");


		// g_rs = H2.getRowSet("""
        //     CREATE TABLE tbl
        //     (
        //         c_pk INTEGER DEFAULT nextval('tbl_seq') NOT NULL PRIMARY KEY,

        //     	c_integer integer,
        //     	c_smallint smallint,
        //     	c_tinyint tinyint,
        //     	c_bigint bigint,
        //     	c_decimal decimal(10,5),
        //     	c_numeric numeric,

        //     	c_real real,
        //     	c_double double,
        //     	c_float float,

        //     	c_boolean boolean,
        //     	// c_bit bit,

        //     	c_char char(3),
        //     	c_varchar varchar,

        //     	// c_longvarchar longvarchar,
        //     	// c_nvarchar nvarchar,
        //     	// c_longnvarchar longnvarchar

        //     	c_nchar nchar(5)
        //     );

		// 	INSERT INTO tbl VALUES
		// 		(1,
        //             1, 1, 1, 1, 1, 1,
        //             1.0, 1.0, 1.0,
        //             false,
        //             'aaa', 'a', 'a'
        //             )
		// 	;
        //     """);