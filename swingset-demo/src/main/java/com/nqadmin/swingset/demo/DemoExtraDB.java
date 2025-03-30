/*
 * Portions created by Ernie Rael are
 * Copyright (C) 2025 Ernie Rael.  All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Ernie Rael <errael@raelity.com>
 */
package com.nqadmin.swingset.demo;

import java.lang.System.Logger;
import java.sql.SQLException;
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

	private static final Map<Integer,RowSet> simpleSupplierData = new MapMaker().weakValues().makeMap();

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
		logger.log(Logger.Level.INFO, () -> sf("Using tbl%d, nRows %d", idxTbl, nRow));
		if (!DemoUtil.hasDriver(DemoUtil.DemoDriver.H2_MEM))
			return null;
		RowSet rowSet = simpleSupplierData.get(idxTbl);
		if (rowSet != null)
			return rowSet;

		rowSet = createSimpleSupplierData(idxTbl, nRow);
		simpleSupplierData.put(idxTbl, rowSet);
		return rowSet;
	}

	/**
	 * Create and return the RowSet for the specified table.
	 * Exception if the table already exists.
	 */
	static RowSet createSimpleSupplierData(int idxTbl, int nRow)
			throws SQLException, ClassNotFoundException
	{
		StringBuilder sb = new StringBuilder("""
            CREATE TABLE tbl{tbl}
            (
                 supplier_id INTEGER DEFAULT NOT NULL PRIMARY KEY,
                     supplier_name varchar(50), status smallint, city varchar(50)
            );
            INSERT INTO tbl{tbl} VALUES
            """);
		for (int row = 1; row <= nRow; row++) {
			String s = "    ({tbl}0{row}, 'name{tbl}{row}', {tbl}{row}, 'city{tbl}{row}'),\n"
					.replace("{row}", String.valueOf(row));
			sb.append(s);
		}
		// replace last line's trailing ",\n" with ";"
		sb.replace(sb.length() - 2, Integer.MAX_VALUE, ";");

		String s = sb.toString().replace("{tbl}", "" + String.valueOf(idxTbl));
		
		RowSet rowset = H2Demo.getRowSet(s);
		rowset.setCommand("SELECT * FROM tbl" + String.valueOf(idxTbl));
		return rowset;
	}
}
