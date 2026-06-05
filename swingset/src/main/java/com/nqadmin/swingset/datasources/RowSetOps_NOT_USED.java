/* *****************************************************************************
 * Copyright (C) 2026, Ernie R Rael. All rights reserved.
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

import java.lang.System.Logger;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import javax.sql.RowSet;

import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.WARNING;

/**
 * x
 */
public class RowSetOps_NOT_USED
{
	private static final Logger logger = SSUtils.getLogger();
	
	/**
	 * Method used by RowSet listeners to get the new text when the RowSet
	 * events are triggered.
	 *
	 * @param comp this components rowset/column text
	 *
	 * @return text representation of data in specified column
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B
	 */
	public static String getColumnText_NOT_USED(final SSComponent comp)
	{
		final RowSet rowSet = comp.getRowSet();
		final int cIdx = comp.getColumnIndex();
		String value = null;

		try {
			// If the column is null, return null.
			if ((RowSetOps.getColumnCount(rowSet)==0) || (rowSet.getObject(cIdx) == null)) {
				return null;
			}

			final JDBCType jdbcType = ConvertType.getJDBCType(RowSetOps.getColumnType(rowSet, cIdx));

			// Based on the column data type convert column's value to a String.
			value = switch (jdbcType) {
			case INTEGER, SMALLINT, TINYINT -> String.valueOf(rowSet.getInt(cIdx));
			case BIGINT -> String.valueOf(rowSet.getLong(cIdx));
			case REAL -> String.valueOf(rowSet.getFloat(cIdx));
			case DOUBLE, FLOAT -> String.valueOf(rowSet.getDouble(cIdx));
			case NUMERIC, DECIMAL -> String.valueOf(rowSet.getBigDecimal(cIdx));
			case BIT, BOOLEAN -> String.valueOf(rowSet.getBoolean(cIdx));
			case DATE, TIME, TIMESTAMP -> DateTime.getDateTimeText(comp);
			case CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR -> {
				String str = rowSet.getString(cIdx);
				yield str == null ? "" : str;
			}
			default -> {
				// TODO: SSSQLExceptionUnhandledType
				logger.log(ERROR, () -> sf("Unsupported data type of %s for column %d.",
						jdbcType.getName(), cIdx));
				yield null;
			}
			}; // end switch
		} catch (final SQLException se) {
			logger.log(ERROR, "SQL Exception for column " + cIdx + ".", se);
		}
		return value;

	} // end protected String getColumnText(RowSet rs, String _columnName) {

	/**
	 * Returns an Object of the specified type
	 * representing the value in the component's bound database column.
	 * @param <T> type to return
	 * @param comp component
	 * @param type Class of returned type
	 * @return value
	 */
	static <T> T getColumnObject2(RSC comp, Class<T> type)
			throws SQLException
	{
		return comp.getRowSet().getObject(comp.getColumnIndex() , type);
	}


	//
	// This switch code is the original from SSDataGrid/SSTableModel,
	// amended to include additional column types and to meet JDBC spec.
	// Doing "rowset.getObject(_column)" should produce the same result,
	// possibly since JDBC 2. This is here as a fallback/just-in-case,
	// given the great divergence in JDBC drivers and database.
	// 
	// If it turns out that there is some need for flipping, maybe for
	// different environments, then it's a question of how?
	//
	// Explore what things should be flippable and at what granularity.
	// Should you be able to specify handling per column type?
	// What switch to flip, in a property file, pluggable, ...
	//
	static Object getColumnObject2(RSC comp) throws SQLException
	{
		RowSet rs = comp.getRowSet();
		int cIdx = comp.getColumnIndex();
		return switch (comp.getColumnJDBCType()) {
		case INTEGER, SMALLINT, TINYINT ->	rs.getInt(cIdx);
		case BIGINT ->				rs.getLong(cIdx);
		case REAL ->				rs.getFloat(cIdx);
		case DOUBLE, FLOAT ->		rs.getDouble(cIdx);
		case NUMERIC, DECIMAL ->	rs.getBigDecimal(cIdx);
		case BOOLEAN, BIT ->		rs.getBoolean(cIdx);
		case DATE ->				rs.getDate(cIdx);
		case TIME ->				rs.getTime(cIdx);
		case TIMESTAMP ->			rs.getTimestamp(cIdx);
		case CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR ->
									rs.getString(cIdx);
		default -> {
			logger.log(WARNING, () -> "Unknown data type of " + comp.getColumnJDBCType());
			yield rs.getObject(cIdx);
		}
		};
	}

	//
	// Following is available as a fallback if there is an issue.
	//

	/**
	 * Update the Grid's RowSet at the specified column index with the given Object value.
	 * RowSet. Operate on the current row.
	 * <p>
	 * When the user changes/edits the SSDataGrid cell this method propagates the
	 * change to the RowSet. A separate call is required to flush/commit the change
	 * to the database.
	 *
	 * @param _rowSet RowSet on which to operate
	 * @param _value string to be type-converted as needed and updated in
	 *                      underlying RowSet column
	 * @param _columnIndex   index of the database column
	 * @param type The JDBCType of the column
	 * @throws SQLException  thrown if a database error is encountered
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B
	 */
	public static void updateColumnObject2(RowSet _rowSet, int _columnIndex, Object _value, JDBCType type) throws SQLException {
		switch (type) {
		case INTEGER, SMALLINT, TINYINT
				-> _rowSet.updateInt(_columnIndex, ((Integer) _value));
		case BIGINT
				-> _rowSet.updateLong(_columnIndex, ((Long) _value));
		case REAL
				-> _rowSet.updateFloat(_columnIndex, ((Float) _value));
		case FLOAT, DOUBLE
				-> _rowSet.updateDouble(_columnIndex, ((Double) _value));
		case DECIMAL, NUMERIC
				-> _rowSet.updateBigDecimal(_columnIndex, ((BigDecimal) _value));
		case BOOLEAN, BIT
				-> _rowSet.updateBoolean(_columnIndex, ((Boolean) _value));
		case DATE
				-> _rowSet.updateDate(_columnIndex, (Date) _value);
		case TIME
				-> _rowSet.updateTime(_columnIndex, (Time) _value);
		case TIMESTAMP
				-> _rowSet.updateTimestamp(_columnIndex, (Timestamp) _value);
		case CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR
				-> _rowSet.updateString(_columnIndex, (String) _value);
		default
				//
				// TODO: SSSQLExceptionUnhandledType
				//
				-> logger.log(ERROR, () -> "Unknown data type of " + type);
		}
	}


	private RowSetOps_NOT_USED() { }
	
}
