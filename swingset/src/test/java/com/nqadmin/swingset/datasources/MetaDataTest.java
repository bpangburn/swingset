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


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.RowSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.nqadmin.swingset.mock.H2;

import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * This is not SS directly; examine how DB does automatic conversions.
 */
@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class MetaDataTest
{
	
	/** x */
	public MetaDataTest()
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

	List<Object[]> rsType = List.of(
			new Object[] {"TYPE_FORWARD_ONLY",       ResultSet.TYPE_FORWARD_ONLY},
			new Object[] {"TYPE_SCROLL_INSENSITIVE", ResultSet.TYPE_SCROLL_INSENSITIVE},
			new Object[] {"TYPE_SCROLL_SENSITIVE",   ResultSet.TYPE_SCROLL_SENSITIVE});
	interface DbFunction<T, R> {
		R apply(T t) throws SQLException;
	}

	static final Map<String, DbFunction<Integer,Object>> dbCap = new TreeMap<>();
	static DatabaseMetaData g_md;

	//g_md - @SuppressWarnings("StaticNonFinalUsedInInitialization")
	static {
		dbCap.put("detected - deletes", typ -> g_md.deletesAreDetected(typ));
		dbCap.put("detected - inserts", typ -> g_md.insertsAreDetected(typ));
		dbCap.put("detected - updates", typ -> g_md.updatesAreDetected(typ));
		dbCap.put("own_vsbl - deletes", typ -> g_md.ownDeletesAreVisible(typ));
		dbCap.put("own_vsbl - inserts", typ -> g_md.ownInsertsAreVisible(typ));
		dbCap.put("own_vsbl - updates", typ -> g_md.ownUpdatesAreVisible(typ));
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

		g_rs = H2.getRowSetCleanDB("""
            CREATE TABLE tbl
            (
                c_pk INTEGER DEFAULT nextval('tbl_seq') NOT NULL PRIMARY KEY,

            	c_tinyint tinyint,
				c_real real
            );

			INSERT INTO tbl VALUES
				(1, 1, 1),
				(2, 2, 2),
				(3, 3, 3)
			;
            """);
		g_rs.setCommand("SELECT * FROM tbl");
		g_rs.execute();
		g_rs.next();

		DatabaseMetaData md = H2.getCon().getMetaData();
		g_md = md;

		System.out.println("Capabilities");
		for (Map.Entry<String, DbFunction<Integer, Object>> entry : dbCap.entrySet()) {
			for (Object[] objects : rsType) {
				String s = sf("    %s %-25s  %s", entry.getKey(), objects[0],
				   entry.getValue().apply((Integer) objects[1]));
				System.out.println(s);
			}
		}

		// System.out.println("ownUpdatesAreVisible: " + md.ownUpdatesAreVisible(
		// 		ResultSet.TYPE_SCROLL_INSENSITIVE
		// ));
		// // ownUpdatesAreVisible: true

		g_rs.absolute(2);
		g_rs.deleteRow();
		g_rs.last();
		System.out.println("After deleting 2nd row from 3 rows: nrows " + g_rs.getRow());
		g_rs.execute();
		g_rs.last();
		System.out.println("    After re-query: nrows " + g_rs.getRow());


		g_rs.updateObject("c_tinyint", 7);
		Object o = g_rs.getObject("c_tinyint"); // returns 1
		System.out.println("" + o);
		g_rs.updateRow();
		o = g_rs.getObject("c_tinyint"); // returns 7
		System.out.println("" + o);
	}

}