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
package com.nqadmin.swingset.demo;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import javax.sql.RowSet;

import com.google.common.collect.MapMaker;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * x
 */
public class DemoExtraDB
{
	private DemoExtraDB() { }

	private static final Logger logger = SSUtils.getLogger();

	// NOTE: During play, might not want the RowSet to just disapear.
	//private static final Map<Integer,RowSet> simpleSupplierData = new MapMaker().weakValues().makeMap();
	private static final Map<Integer,RowSet> simpleSupplierData = new MapMaker().makeMap();

	/** Verify initial state; cursors position same as table index */
	static void check()
	{
		if (Boolean.TRUE)
			return;
		simpleSupplierData.forEach((idx, rs) -> {
			try {
				if (rs.getRow() != idx) {
					String s = sf("*********** %s MISMATCHED ROW POSITION ***********",
							rs.getMetaData().getTableName(1));
					System.err.println(s);
					logger.log(Level.ERROR, s);
				}
			} catch (SQLException ex) {
			}
		});
	};

	static boolean isExecuted(RowSet rs)
	{
		boolean rc = false;
		try {
			rs.getRow();
			rc = true;
		} catch (SQLException ex) { }
		return rc;
	}

	static Integer findIdxTbl(RowSet rs)
	{
		for (Map.Entry<Integer, RowSet> entry : simpleSupplierData.entrySet()) {
			if (entry.getValue() == rs)
				return entry.getKey();
		}
		return null;
	}

	static void derefSupplierData(RowSet rs)
	{
		for (Iterator<Map.Entry<Integer, RowSet>> iterator
				= simpleSupplierData.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<Integer, RowSet> next = iterator.next();
			if (next.getValue() != rs)
				iterator.remove();
		}
	}
	
	/**
	 * Return the RowsModel for the specified table; shared if already exists.
	 * If it doesn't already exist, the table is created with the specified number of rows.
	 * <p>
	 * Each row of the table looks like
	 * <pre>
	 *  supplier_id, supplier_name,    status,     city);
	 * ({tbl}0{row}, 'name{tbl}{row}', {tbl}{row}, 'city{tbl}{row}'),
	 * </pre>
	 * 
	 * @param idxTbl table name ends with this number, like "tbl7"
	 * @param nRow only used if table doesn't already exist
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	static RowSet findSimpleSupplierData(int idxTbl, int nRow)
			throws SQLException, ClassNotFoundException
	{
		if (!DemoUtil.hasDriver(DemoUtil.DemoDriver.H2_MEM))
			return null;
		RowSet rowSet = simpleSupplierData.get(idxTbl);
		if (rowSet != null) {
			logger.log(Level.INFO, () -> sf("Reuse tbl%d, nRows %d", idxTbl, nRow));
			return rowSet;
		}

		logger.log(Level.INFO, () -> sf("Create tbl%d, nRows %d", idxTbl, nRow));
		rowSet = createSimpleSupplierData(idxTbl, nRow);
		simpleSupplierData.put(idxTbl, rowSet);
		return rowSet;
	}

	//
	// Something very similar also in tests H2.java
	//

	/**
	 * Create and return the RowSet for the specified table.
	 * Exception if the table already exists.
	 */
	static RowSet createSimpleSupplierData(int idxTbl, int nRow)
			throws SQLException, ClassNotFoundException
	{
		RowSet rowset = H2Demo.getRowSet(createSimpleSupplierDataSql(idxTbl, nRow, idxTbl));
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
	static String createSimpleSupplierDataSql(int idxTbl, int nRow, int start_idx)
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
