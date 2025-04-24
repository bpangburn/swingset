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
package com.nqadmin.swingset.mock;

import java.sql.SQLException;

import javax.sql.RowSet;

/**
 *
 * @author err
 */
public class TinyRS
{
	private TinyRS() { }

	public static RowSet getRS1() throws SQLException, ClassNotFoundException
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

	public static RowSet getRSEmpty() throws SQLException, ClassNotFoundException
	{
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl1Empty
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, some_int tinyint);
            """);
		rs.setCommand("SELECT * FROM tbl1Empty");
		return rs;
	}

	public static RowSet getRS1NotNull() throws SQLException, ClassNotFoundException
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

	public static RowSet getRS2() throws SQLException, ClassNotFoundException
	{
		// This table has a different type for some_int
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl2
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, some_int int);
            INSERT INTO tbl2 VALUES
            	(21, 1), (22, 1), (23, 1), (24, 1), (25, 1), (26, 1)
            ;
            """);
		rs.setCommand("SELECT * FROM tbl2");
		return rs;
	}

	/**
	 * x
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static RowSet getRS1_4() throws SQLException, ClassNotFoundException
	{
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl1
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, c_tinyint tinyint, c_char varchar);
            INSERT INTO tbl1 VALUES
            	(11, 1, 'a1'), (12, 1, 'b1'), (13, 1, 'c1'), (14, 1, 'd1')
            ;
            """);
		rs.setCommand("SELECT * FROM tbl1");
		return rs;
	}

	/**
	 * x
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static RowSet getRS2_5() throws SQLException, ClassNotFoundException
	{
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl2
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, c_tinyint tinyint, c_char varchar);
            INSERT INTO tbl2 VALUES
            	(21, 1, 'a2'), (22, 1, 'b2'), (23, 1, 'c2'), (24, 1, 'd2'), (25, 1, 'e2')
            ;
            """);
		rs.setCommand("SELECT * FROM tbl2");
		return rs;
	}
	
}
