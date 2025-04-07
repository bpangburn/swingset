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
import java.util.Objects;

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
		Objects.requireNonNull(sql);
		execute(sql);
		JdbcRowSet rs = RowSetProvider.newFactory().createJdbcRowSet();
		rs.setUrl(dbUrl());
		return rs;
	}

	/**
	 * Starting with an empty data base, run some sql commands.
	 * SQL_INIT is the default sql script if sql is null.
	 * @param sql initialization command
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	public static RowSet getRowSetCleanDB(String sql)
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
		return c;
	}
	
	/** The default sql script to initialize the database */
	public static final String SQL_INIT =
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
			c_timestamp TIMESTAMP,
			c_list INTEGER ARRAY /* ARRAY is typed for 2.x+, arbitrary range 2-8 */
		);

		MERGE INTO tbl VALUES ( 1,'text-1',3,'2000-01-11','11:11:11','2222-01-11',ARRAY[1,2,3]) ;
		MERGE INTO tbl VALUES ( 2,'text-2',7,'2000-02-22','22:22:22','2222-02-22',ARRAY[3,4,5]) ;

        """;

	/**
	 * Create and return the RowSet for the specified table.
	 * Exception if the table already exists.
	 */
	public static RowSet createSimpleSupplierData(int idxTbl, int nRow)
			throws SQLException, ClassNotFoundException
	{
		return createSimpleSupplierData(idxTbl, nRow, idxTbl);
	}

	public static RowSet createSimpleSupplierData(int idxTbl, int nRow, int start_idx)
			throws SQLException, ClassNotFoundException
	{
		RowSet rowset = getRowSet(createSimpleSupplierDataSql(idxTbl, nRow, start_idx));
		rowset.setCommand("SELECT * FROM tbl" + String.valueOf(idxTbl));
		return rowset;
	}

	//
	// The start_idx is all about giving columnNames a different columnIndex for testing.
	//

	/**
	 * Create and return the RowSet for the specified table.
	 * Exception if the table already exists.
	 */
	private static String createSimpleSupplierDataSql(int idxTbl, int nRow, int start_idx)
			throws SQLException, ClassNotFoundException
	{
		String colDefs[] = new String[] {
			"supplier_id INTEGER DEFAULT NOT NULL PRIMARY KEY",
			"supplier_name varchar(50)",
			"status smallint",
			"city varchar(50)"
		};
		//String colDefsTemplate = "%s, %s, %s, %s";
		String colVals[] = new String[] {
			"{tbl}0{row}",
			"'name{tbl}{row}'",
			"{tbl}{row}",
			"'city{tbl}{row}'" };
		//String colValsTemplate = "%s, %s, %s, %s";

		//StringBuilder sb = new StringBuilder("""
		String createSql = """
            DROP TABLE IF EXISTS tbl{tbl};
            CREATE TABLE tbl{tbl}
            (
            {colDefs}
            );
            INSERT INTO tbl{tbl} VALUES
            """;
		StringBuilder sb = new StringBuilder(
				createSql.replace("{colDefs}", rotate(colDefs, "    ", '\n', start_idx)));
		
		String valsTemplate = "    (" + rotate(colVals, "", ' ', start_idx) + "),\n";
		for (int row = 1; row <= nRow; row++) {
			String s = valsTemplate.replace("{row}", String.valueOf(row));
			sb.append(s);
		}
		// replace last line's trailing ",\n" with ";"
		sb.replace(sb.length() - 2, Integer.MAX_VALUE, ";");

		return sb.toString().replace("{tbl}", "" + String.valueOf(idxTbl));
	}

	/** Create a single comma seperated string with values from rotating input array */
	private static String rotate(String[] strings, String pre, char end_char, int start_idx)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			String string = strings[(start_idx + i) % strings.length];
			sb.append(pre).append(string).append(',').append(end_char);
		}
		sb.setLength(sb.length() - 2); // remove trailing ",x"

		return sb.toString();
	}
}
