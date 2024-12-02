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
package com.nqadmin.swingset.mock;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetProvider;

import org.h2.tools.RunScript;

// TODO: track databases created, have delete all method. Just drop?
//		 h2 delete database after test
// DROP ALL OBJECTS [DELETE FILES]
//	https://stackoverflow.com/questions/8523423/reset-embedded-h2-database-periodically
//
// jdbc:h2:mem:test;DB_CLOSE_DELAY=-1

/**
 * An in memory DB for unit testsing. One connection, recreates the database
 * when getting a rowset.
 */
public class H2
{
	private H2() { }
	public static final String DB_NAME = "db";
	private static Connection conn;

	public static RowSet getRowSet(String sql)
			throws SQLException, ClassNotFoundException {
		clean();
		execute(sql != null ? sql : SQL_INIT);
		JdbcRowSet rs = RowSetProvider.newFactory().createJdbcRowSet();
		rs.setUrl(dbUrl());
		return rs;
	}

	public static void clean() throws SQLException, ClassNotFoundException {
		execute("DROP ALL OBJECTS");
	}

	// public static void createNewDb() {
	// }

	public static Connection getCon() throws ClassNotFoundException, SQLException {
		if (conn == null)
			conn = create();
		return conn;
	}

	public static String dbUrl() {
		return "jdbc:h2:mem:" + DB_NAME;
	}

	private static void execute(String sql) throws SQLException, ClassNotFoundException
	{
		RunScript.execute(getCon(), new StringReader(sql));
	}

	// TODO: args sqlInit and urltag
	private static Connection create() throws ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver");
		Connection c = DriverManager.getConnection(dbUrl());
		//+ defLookup(H2Trace.class).getTraceUrlFlags());
		//RunScript.execute(c, new StringReader(SQL_INIT));
		return c;
	}
	
	private static final String SQL_INIT =
		"""
		DROP TABLE IF EXISTS tbl;
		DROP SEQUENCE IF EXISTS tbl_seq;

		/* tbl */
		CREATE SEQUENCE IF NOT EXISTS tbl_seq START WITH 1000;
		CREATE TABLE IF NOT EXISTS tbl 
		( 
			tbl_pk INTEGER DEFAULT nextval('tbl_seq') NOT NULL PRIMARY KEY,
			c_text VARCHAR(50),
			c_int INT,
			c_date DATE,
			c_time TIME,
			c_list INTEGER ARRAY /* ARRAY is typed for 2.x+, arbitrary range 2-8 */
		);

		MERGE INTO tbl VALUES ( 1,'text-1',3,'2000-01-11','11:11:11',ARRAY[1,2,3]) ;
		MERGE INTO tbl VALUES ( 2,'text-2',7,'2000-02-22','22:22:22',ARRAY[3,4,5]) ;

        """;
}
