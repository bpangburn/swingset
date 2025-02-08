/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
 * All rights reserved.
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
 * 
 * Contributors:
 *   Prasanth R. Pasala
 *   Brian E. Pangburn
 *   Diego Gil
 *   Man "Bee" Vo
 *   Ernie R. Rael
 ******************************************************************************/
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.datasources;

import java.lang.System.Logger;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.SyncResolver;

import com.nqadmin.swingset.datasources.Utils.ConflictRow;
import com.nqadmin.swingset.navigate.RowSetState;
import com.nqadmin.swingset.navigate.UndoRedo;
import com.nqadmin.swingset.utils.SSArray;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

import static com.google.common.collect.Sets.immutableEnumSet;
import static com.nqadmin.swingset.datasources.ConvertType.convertToType;
import static com.nqadmin.swingset.datasources.ConvertType.findJavaTypeClass;
import static com.nqadmin.swingset.datasources.ConvertType.getJDBCType;
import static com.nqadmin.swingset.datasources.DateTime.getSQLDateTimeObject;
import static com.nqadmin.swingset.datasources.JdbcDataTypeConversionTables.jdbcTypeToClass;
import static com.nqadmin.swingset.navigate.Utils.postRowSetModified;
import static com.nqadmin.swingset.utils.CentralLookup.defLookup;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * Utility class for working with {@link RowSet}s and {@link ResultSet}s.
 * Some methods for converting to/from text from/to objects according
 * to database type. Several convenience methods for accessing metadata.
 * 
 * @since 4.0.0
 */
public class RowSetOps {

	private RowSetOps(){}

	private static final Logger logger = SSUtils.getLogger();

	// TODO Audit type handling based on http://www.java2s.com/Code/Java/Database-SQL-JDBC/StandardSQLDataTypeswithTheirJavaEquivalents.htm

	/**
	 * Inserts the context of the insert row into this {@linkplain ResultSet}
	 * and into the database. Handle CachedRowSet.
	 * @param _resultSet
	 * @throws SQLException
	 */
	public static void insertRow(ResultSet _resultSet) throws SQLException {
		_resultSet.insertRow();
		if (_resultSet instanceof CachedRowSet crs) {
			logger.log(DEBUG, "using CachedRowSet");
			_resultSet.moveToCurrentRow();
			try {
				RowSetState.acceptChanges(crs, null);
			} catch (SyncProviderException ex) {
				//
				// TODO: test CRS undoInsert after accept changes
				//
				crs.undoInsert();
				throw ex;
			}
		}
	}

	private static class ResetRowPosition
	{
		SQLException ex;

		@SuppressWarnings("ResultOfObjectAllocationIgnored")
		static void doit(ResultSet rs, int targetRow)
		{
			new ResetRowPosition(rs, targetRow);
		}

		static void doit(ResultSet rs, int targetRow, boolean mayThrow)
				throws SQLException
		{
			ResetRowPosition rrp = new ResetRowPosition(rs, targetRow);
			if(mayThrow && rrp.ex != null)
				throw rrp.ex;
		}

		ResetRowPosition(ResultSet rs, int targetRow)
		{
			try {
				rs.absolute(targetRow);
			} catch (SQLException ex01) {
				logger.log(ERROR, "resetting row after acceptChanges", ex01);
				this.ex = ex01;
			}
		}
	}

	/**
	 * Updates the underlying database with the new contents of the current row
	 * of this {@linkplain ResultSet} object. Handle CachedRowSet.
	 * @param _resultSet
	 * @throws SQLException
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void updateRow(ResultSet _resultSet) throws SQLException {
		_resultSet.updateRow();
		if (!(_resultSet instanceof CachedRowSet crs))
			return;

		final int maxTry = 2;
		List<ConflictRow> conflictRows = null;
		SyncProviderException srEx = null;
		int currentRow = _resultSet.getRow();

		int tryCount = 1;
		for (; tryCount <= maxTry; ++tryCount) {
			logger.log(DEBUG, sf("CachedRowSet.acceptChanges: try %d", tryCount));
			try {
				RowSetState.acceptChanges(crs, null);
				ResetRowPosition.doit(_resultSet, currentRow, true);
				break;
			} catch (SyncProviderException ex) {
				if (Boolean.TRUE) {
					// Just log and re-throw the exception and
					// try looping to do it again and see how conflicts go.
					// In a real-life implementation, collect the conflicts
					// and imediately re-run commit. Then use the collected
					// conflicts for the UI to pick and chose, then use
					// UI results to resolve.
					List<ConflictRow> cRows = Utils.collectConflictNoThrow(
							ex.getSyncResolver(), crs);
					Utils.dumpConflict((s) -> logger.log(DEBUG, s), cRows);
					Utils.dumpConflict((s) -> System.err.printf("%s\n", s), cRows);
					if(conflictRows == null) {
						conflictRows = cRows;
						continue;
					}

					// The following only guaranteed in controlled/debug situation.
					if(!Objects.equals(conflictRows, cRows))
						throw new IllegalStateException("Conflicts should be equal");

					ResetRowPosition.doit(_resultSet, currentRow);
					throw ex;
				}
				// ============================================================

				srEx = ex;
				//
				// TODO: test CRS undoUpdate after accept changes
				//
				SyncResolver sr = srEx.getSyncResolver();
				//
				// TODO: acceptChanges resolve persist DB
				//		If the CRS value is persisted, then all correct.
				//		If DB values persisted, then still dirty.
				// 
				boolean persistDB = Boolean.FALSE;
				Utils.processConflict(System.err, sr, crs, false);
				
				// HACK TODO: acceptChanges resolve persist DB
				//		There are still issues if the DB was selected.
				//		After the exception, if the user does
				//		cancel row update, then the value reverts
				//		to what's in the CRS, which is probably NOT
				//		what's in the DB. There is no indication that
				//		there's a difference. But maybe it's not really
				//		a problem because if the DB is async changed,
				//		there's no indication.
				//
				//		Could have a "diff" button, or maybe keep
				//		the row as "changed", and then the "diff"
				//		just sends it all back.
				//
				//		Maybe set a visible state/status indicating that
				//		the CRS should be reloaded from the database.
				//

				ResetRowPosition rrp = new ResetRowPosition(_resultSet, currentRow);
				if (persistDB)
					throw new SQLException("These value not persisted");
				if (rrp.ex != null)
					throw rrp.ex;
				break;
			}
		}

		if (tryCount > maxTry && srEx != null)
			throw srEx;
	}

	/**
	 * Deletes the current row from this {@linkplain ResultSet} and from the
	 * underlying database. Handle CachedRowSet.
	 * @param _resultSet
	 * @throws SQLException 
	 */
	public static void deleteRow(ResultSet _resultSet) throws SQLException {
		_resultSet.deleteRow();
		if (_resultSet instanceof CachedRowSet crs) {
			logger.log(DEBUG, "using CachedRowSet");
			try {
				RowSetState.acceptChanges(crs, null);
			} catch (SyncProviderException ex) {
				crs.undoDelete();
				throw ex;
			}
		}
	}

	/**
	 * Returns the number of columns in the underlying ResultSet object
	 *
	 * @param _resultSet ResultSet on which to operate
	 * @return the number of columns
	 * @throws SQLException - if a database access error occurs
	 */
	public static int getColumnCount(final ResultSet _resultSet) throws SQLException {
		return _resultSet.getMetaData().getColumnCount();
	}

	/**
	 * Get the designated column's index
	 *
	 * @param _resultSet ResultSet on which to operate
	 * @param _columnName - name of the column
	 *
	 * @return returns the corresponding column index (starting from 1)
	 *
	 * @throws SQLException - if a database access error occurs
	 */
	public static int getColumnIndex(final ResultSet _resultSet, final String _columnName) throws SQLException {
		return _resultSet.findColumn(_columnName);
	}

	/**
	 * Returns the column name for the column index provided
	 *
	 * @param _resultSet ResultSet on which to operate
	 * @param _columnIndex - the column index where the first column is 1, second
	 *                     column is 2, etc.
	 * @return the column name of the given column index
	 *
	 * @throws SQLException - if a database access error occurs
	 */
	public static String getColumnName(final ResultSet _resultSet, final int _columnIndex) throws SQLException {
		return _resultSet.getMetaData().getColumnName(_columnIndex);
	}

	/**
	 * Determine if the specified column is nullable. If the nullability
	 * of the column is unknown, then an empty Optional is returned.
	 * @param _resultSet RowSet on which to operate
	 * @param _columnIndex column index
	 * @return Optional of true if nullable, empty Optional if unknown.
	 */
	public static Optional<Boolean> isNullable(final ResultSet _resultSet, final int _columnIndex) {
		try {
			int nullable = _resultSet.getMetaData().isNullable(_columnIndex);
			return nullable == ResultSetMetaData.columnNullableUnknown
					? Optional.empty()
					: Optional.of(nullable == ResultSetMetaData.columnNullable);
		} catch (SQLException ex) {
			logger.log(ERROR, () -> sf("SQL Exception for column %d.",
					_columnIndex, ex));
			return Optional.empty();
		}
	}

	/**
	 * Determine if the specified column is nullable. If the nullability
	 * of the column is unknown, then an empty Optional is returned.
	 * @param _resultSet RowSet on which to operate
	 * @param _columnName column name
	 * @return Optional of true if nullable, empty Optional if unknown.
	 */
	public static Optional<Boolean> isNullable(final ResultSet _resultSet, final String _columnName) {
		try {
			return isNullable(_resultSet, getColumnIndex(_resultSet, _columnName));
		} catch (SQLException ex) {
			logger.log(ERROR, () -> sf("SQL Exception for column %s.",
					_columnName, ex));
			return Optional.empty();
		}
	}

	/**
	 *
	 * @param comp
	 * @return
	 */
	public static Array getColumnArray(SSComponentInterface comp)
	{
		try {
			if (getColumnCount(comp.getRowSet())==0)
				return null;
			return (Array)(UndoRedo.isUndoRedoEnabled(comp)
					? UndoRedo.fetchCurrentValue(comp)
					: comp.getRowSet().getArray(comp.getBoundColumnIndex()));
		} catch (SQLException ex) {
			logger.log(ERROR, "SQL Exception for column " + comp.getBoundColumnName() + ".", ex);
		}
		return null;
	}

	/**
	 * Returns the Object from the rowset's specified column;
	 * no object conversion.
	 * There is no filtering, for example null conversion.
	 * @param comp component
	 * @return value
	 * @throws java.sql.SQLException
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B-1
	 */
	public static Object getColumnObject(RSC comp) throws SQLException
	{
		return UndoRedo.isUndoRedoEnabled(comp)
				? UndoRedo.fetchCurrentValue(comp)
				: comp.getRowSet().getObject(comp.getBoundColumnIndex());
	}

	/**
	 * Returns the Object from the rowset's specified column;
	 * no object conversion.
	 * There is no filtering, for example null conversion.
	 * @param comp component
	 * @return value
	 * @throws java.sql.SQLException
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B-1
	 */
	//TODO: rename this to "getColumnObjectDirect"? only used with grid
	public static Object getColumnObjectLegacy(RSC comp) throws SQLException
	{
		if(Boolean.TRUE)
			return comp.getRowSet().getObject(comp.getBoundColumnIndex());
		else
			return getColumnObject2(comp);
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
	private static Object getColumnObject2(RSC comp) throws SQLException
	{
		RowSet rs = comp.getRowSet();
		int cIdx = comp.getBoundColumnIndex();
		return switch (comp.getBoundColumnJDBCType()) {
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
			logger.log(WARNING, "Unknown data type of " + comp.getBoundColumnJDBCType());
			yield rs.getObject(cIdx);
		}
		};
	}

	//
	// TODO: revisit these ColumnObject methods
	//
	/**
	 * Returns an Object of the specified type
	 * representing the value in the component's bound database column.
	 * This may involve a conversion.
	 * <p>
	 * Note that if a String type is specified, a null is not automatically
	 * turned into "" use getColumnText for that.
	 * @param <T> type to return
	 * @param comp component
	 * @param type Class of returned type
	 * @return object
	 * @throws java.sql.SQLException
	 */
	public static <T> T getColumnObject(RSC comp, Class<T> type) throws SQLException
	{
		// If there are no columns, return null.
		if (getColumnCount(comp.getRowSet()) == 0)
			return null;

		if(Boolean.TRUE)
			return getColumnObject1(comp, type); // undo/redo,convert
		else
			return getColumnObject2(comp, type); // getObject direct
	}

	/**
	 * Returns an Object of the specified type
	 * representing the value in the component's bound database column.
	 * @param <T> type to return
	 * @param comp component
	 * @param type Class of returned type
	 * @return value
	 */
	private static <T> T getColumnObject1(RSC comp, Class<T> type)
			throws SQLException
	{
		Object objectValue = UndoRedo.isUndoRedoEnabled(comp)
				? UndoRedo.fetchCurrentValue(comp)
				: comp.getRowSet().getObject(comp.getBoundColumnIndex());
		return convertToType(objectValue, type);
	}

	/**
	 * Returns an Object of the specified type
	 * representing the value in the component's bound database column.
	 * @param <T> type to return
	 * @param comp component
	 * @param type Class of returned type
	 * @return value
	 */
	private static <T> T getColumnObject2(RSC comp, Class<T> type)
			throws SQLException
	{
		return comp.getRowSet().getObject(comp.getBoundColumnIndex() , type);
	}
	
	/**
	 * Method used by RowSet listeners to get the new text when the RowSet
	 * events are triggered.
	 *
	 * @param comp this components rowset/column text
	 *
	 * @return text representation of data in specified column
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B
	 */
	public static String getColumnText(final SSComponentInterface comp)
	{
		final RowSet rowSet = comp.getRowSet();
		final int cIdx = comp.getBoundColumnIndex();
		String value = null;

		try {
			// If the column is null, return null.
			if ((getColumnCount(rowSet)==0) || (rowSet.getObject(cIdx) == null)) {
				return null;
			}

			final JDBCType jdbcType = getJDBCType(getColumnType(rowSet, cIdx));

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
	 * Get the column text from the current undo/redo value.
	 * @param comp this components rowset/column text
	 * @return
	 */
	public static String getColumnObjectText(RSC comp)
	{
		final RowSet _rowSet = comp.getRowSet();
		final String _columnName = comp.getBoundColumnName();

		String value = null;
		try {
			// IF THE COLUMN IS NULL SO RETURN NULL
			if (getColumnCount(_rowSet)==0) {
				return null;
			}

			Object objectValue = UndoRedo.isUndoRedoEnabled(comp)
					? UndoRedo.fetchCurrentValue(comp)
					: comp.getRowSet().getObject(comp.getBoundColumnIndex());
			if (objectValue == null)
				return null;

			if (objectValue instanceof String s)
				return s;

			final JDBCType jdbcType = getJDBCType(getColumnType(_rowSet, _columnName));

			switch (jdbcType) {
			case INTEGER, SMALLINT, TINYINT, BIGINT, REAL, DOUBLE, FLOAT,
					NUMERIC, DECIMAL, BIT, BOOLEAN,
					// the CHAR... cases already handled, but...
					CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR ->
				value = objectValue.toString();
			case DATE, TIME, TIMESTAMP ->
				value = DateTime.getDateTimeText(objectValue, comp);
			default -> // TODO: SSSQLExceptionUnhandledType
				logger.log(ERROR, "Unsupported data type of " + jdbcType.getName() + " for column " + _columnName + ".");
			} // end switch
			//
			// TODO: Convert this to use java.time.LocalDate, LocalTime,
			//		 or LocalDateTime as needed.
			//
			

		} catch (final SQLException se) {
			logger.log(ERROR, "SQL Exception for column " + _columnName + ".", se);
		}

		return value;

	} // end protected String getColumnText(RowSet rs, String _columnName) {

	/**
	 * Retrieves an integer corresponding to the designated column's type based on
	 * the column index (starting from 1)
	 *
	 * @see "https://docs.oracle.com/javase/7/docs/api/java/sql/Types.html"
	 *
	 * @param _resultSet ResultSet on which to operate
	 * @param _columnIndex - the column index where the first column is 1, second
	 *                     column is 2, etc.
	 * @return SQL type from java.sql.Types
	 *
	 * @throws SQLException - if a database access error occurs
	 */
	public static int getColumnType(final ResultSet _resultSet, final int _columnIndex) throws SQLException {
		return _resultSet.getMetaData().getColumnType(_columnIndex);
	}

	/**
	 * Retrieves JDBCType corresponding to the designated column's type based on
	 * the column index (starting from 1)
	 *
	 * @see java.sql.JDBCType
	 *
	 * @param _resultSet ResultSet on which to operate
	 * @param _columnIndex - the column index where the first column is 1, second
	 *                     column is 2, etc.
	 * @return JDBCType of the column
	 *
	 * @throws SQLException - if a database access error occurs
	 */
	public static JDBCType getJDBCColumnType(final ResultSet _resultSet, final int _columnIndex) throws SQLException {
		return getJDBCType(getColumnType(_resultSet, _columnIndex));
	}

	/**
	 * Retrieves an int corresponding to the designated column's type based on the
	 * column name
	 *
	 * @see "https://docs.oracle.com/javase/7/docs/api/java/sql/Types.html"
	 *
	 * @param columnName - name of the column
	 *
	 * @param resultSet ResultSet on which to operate
	 * @return JDBCType of the column
	 *
	 * @throws SQLException - if a database access error occurs
	 */
	public static int getColumnType(ResultSet resultSet, String columnName)
			throws SQLException {
		return resultSet.getMetaData().getColumnType(getColumnIndex(resultSet, columnName));
	}

	/**
	 * Retrieves an int corresponding to the designated column's type based on the
	 * column name
	 *
	 * @see java.sql.JDBCType
	 *
	 * @param columnName - name of the column
	 *
	 * @param resultSet ResultSet on which to operate
	 * @return JDBCType of the column
	 *
	 * @throws SQLException - if a database access error occurs
	 */
	public static JDBCType getJDBCColumnType(ResultSet resultSet, String columnName) throws SQLException {
		return getJDBCType(getColumnType(resultSet, columnName));
	}

	/**
	 * Retrieve the Java Class corresponding to the designated column
	 * based on the column name.
	 * 
	 * @param _columnName - name of the column
	 *
	 * @param _resultSet ResultSet on which to operate
	 * @return
	 * @throws SQLException
	 */
	public static Class<?> getClassColumnType(final ResultSet _resultSet, final String _columnName) throws SQLException {
		return getClassColumnType(_resultSet, getColumnIndex(_resultSet, _columnName));
	}

	/**
	 * Retrieve the Java Class corresponding to the designated column
	 * based on the column index (starting from 1).
	 * 
	 * @param _columnIndex - index of the column
	 *
	 * @param _resultSet ResultSet on which to operate
	 * @return
	 * @throws SQLException
	 */
	public static Class<?> getClassColumnType(final ResultSet _resultSet, final int _columnIndex) throws SQLException {
		JDBCType type = RowSetOps.getJDBCColumnType(_resultSet, _columnIndex);
		return findJavaTypeClass(type);
	}

	/**
	 * Jdbc types that can be set to an empty string.
	 */
	public static final Set<JDBCType> textUpdateEmptyOK = immutableEnumSet(
			JDBCType.CHAR,
			JDBCType.VARCHAR,
			JDBCType.LONGVARCHAR,

			JDBCType.NCHAR,
			JDBCType.NVARCHAR,
			JDBCType.LONGNVARCHAR
	);
	
	/**
	 * Jdbc types that can be set to a non-empty string.
	 */
	public static final Set<JDBCType> textUpdateOK = immutableEnumSet(
			JDBCType.INTEGER,
			JDBCType.SMALLINT,
			JDBCType.TINYINT,
			JDBCType.BIGINT,
			JDBCType.FLOAT,
			JDBCType.DOUBLE,
			JDBCType.REAL,
			JDBCType.NUMERIC,
			JDBCType.DECIMAL,
			JDBCType.BOOLEAN,
			JDBCType.BIT,
			JDBCType.DATE,
			JDBCType.TIME,
			JDBCType.TIMESTAMP,
			JDBCType.CHAR,
			JDBCType.VARCHAR,
			JDBCType.LONGVARCHAR,

			JDBCType.NCHAR,
			JDBCType.NVARCHAR,
			JDBCType.LONGNVARCHAR
	);

	/**
	 * Method used by SwingSet component listeners to update the underlying
	 * RowSet.
	 * <p>
	 * When the user changes/edits the SwingSet column this method propagates the
	 * change to the RowSet. A separate call is required to flush/commit the change
	 * to the database.
	 *
	 * @param comp The SSComponent doing the update
	 * @param _updatedValue Array
	 * @throws SSSQLNullException thrown if null is not allowed
	 * @throws SQLException  thrown if a database error is encountered
	 */
	public static void updateColumnArray(final SSComponentInterface comp, final SSArray _updatedValue) throws SSSQLNullException, SQLException {
		updateColumnArray(comp, comp.getRowSet(), _updatedValue, comp.getBoundColumnName(), comp.getAllowNull());
	}

	/**
	 * Method used by SwingSet component listeners to update the underlying
	 * RowSet.
	 * <p>
	 * When the user changes/edits the SwingSet column this method propagates the
	 * change to the RowSet. A separate call is required to flush/commit the change
	 * to the database.
	 *
	 * @param comp The SSComponent doing the update
	 * @param _rowSet RowSet on which to operate
	 * @param _updatedValue Array
	 * @param _columnName   name of the database column
	 * @param _allowNull 	indicates if Component and underlying column can contain null values
	 * @throws SSSQLNullException thrown if null is not allowed
	 * @throws SQLException  thrown if a database error is encountered
	 */
	private static void updateColumnArray(final SSComponentInterface comp, final RowSet _rowSet, final SSArray _updatedValue, final String _columnName, final boolean _allowNull) throws SSSQLNullException, SQLException
	{
		logger.log(DEBUG, "[" + _columnName + "]. Update to: " + _updatedValue + ". Allow null? [" + _allowNull + "]");

		UndoRedo.captureInitialValue(comp); // undo/redo

		// On insert row, write null if updatedValue is null, and do not perform other checks. 
		boolean did_update = false;
		try {
			if (_updatedValue == null && RowSetState.isInserting(_rowSet)) {
				_rowSet.updateNull(_columnName);
				did_update = true;
				return;
			}
			
			if (_updatedValue == null) {
				if (_allowNull) {
					_rowSet.updateNull(_columnName);
					did_update = true;
					return;
				} else
					throw new SSSQLNullException("NULL not allowed for this field.");
			}
			
			_rowSet.updateArray(_columnName, _updatedValue);
			did_update = true;
		} finally {
			if (did_update)
				postRowSetModified(comp, _updatedValue);
		}
	}

	/**
	 * Method used by SwingSet component listeners to update the underlying
	 * RowSet.
	 * <p>
	 * When the user changes/edits the SwingSet column this method propagates the
	 * change to the RowSet. A separate call is required to flush/commit the change
	 * to the database.
	 *
	 * @param comp The SSComponent doing the update
	 * @param updatedValue value to write to underlying RowSet column
	 * @throws SSSQLNullException thrown if null is not allowed
	 * @throws SQLException  thrown if a database error is encountered
	 */
	public static void updateColumnObject(SSComponentInterface comp, Object updatedValue)
			throws SSSQLNullException, SQLException, NumberFormatException
	{
		if (updatedValue instanceof String s) {
			// This method doesn't have all the string checks,
			// use updateColumnText if String Object.
			updateColumnText(comp, s);
			return;
		}
		final RowSet rowSet = comp.getRowSet();
		final int columnIndex = comp.getBoundColumnIndex();
		boolean allowNull = comp.getAllowNull();
		logger.log(DEBUG,  comp.getColumnForLog() + " Update to: " + updatedValue + ". Allow null? [" + allowNull + "]");

		UndoRedo.captureInitialValue(comp); // undo/redo

		boolean did_update = false;
		try {
			// On insert row, write null and do not perform other checks.
			if (updatedValue == null) {
				if (RowSetState.isInserting(rowSet) || allowNull) {
					rowSet.updateNull(columnIndex);
					did_update = true;
					return;
				} else
					throw new SSSQLNullException("NULL not allowed for this field.");
			}

			//_rowSet.updateObject(_columnIndex, _updatedValue);
			JDBCType jdbcType = comp.getBoundColumnJDBCType();
			// TODO: Maybe a component field that says use jdbc conversion.
			//		 Better, checkDriverConvertToType(),
			//		 so "obj = convertObjectTypeIfNeeded(...)"
			// TODO: Why isn't updateObject(index, object, type) used anywhere?
			//		 Could always catch SQLFeatureNotSupportedException and do
			//		 manual conversions as a last resort.
			// TODO: It's weird that updateObject(idx,obj,type) javadoc says
			//		 "type to be sent to the database". Does that mean the specified
			//		 conversions for setObject kick in at that point?
			Object obj = convertToType(updatedValue, jdbcType);
			updateColumnObjectDirect(rowSet, columnIndex, obj, jdbcType);
			did_update = true;
		} finally {
			if (did_update)
				postRowSetModified(comp, updatedValue);
		}
	}

	/** DEBUG ASSIST: Use this to force "n" acceptChanges conflicts after
	 * modifying a character column in the database.
	 * Only works with CachedRowSet and after
	 * {@linkplain #updateColumnText(com.nqadmin.swingset.utils.SSComponentInterface, java.lang.String)}.
	 */
	public static class ForceConflict
	{
		/** Atomic just in case */
		private final AtomicInteger nForce;

		/** Argument is number of conflicts to create.
		 * @param n */
		public ForceConflict(int n)
		{
			nForce = new AtomicInteger(n);
		}

		/** Argument is number of conflicts to add.
		 * @param n */
		public void force(int n) {
			if (n < 0)
				throw new IllegalArgumentException();
			nForce.getAndAdd(n);
		}
		/**
		 * Check if conflict should be forced. If this returns true,
		 * the nForce counter is decremented.
		 * Return true to force a conflict
		 */
		boolean doForce() {
			int n = nForce.getAndUpdate((val) -> (val > 0 ? val - 1 : 0));
			return n != 0;
		}
	}

	/**
	 *
	 * @param comp
	 * @param updatedValue
	 * @throws SQLException
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void checkForceConflict(SSComponentInterface comp, String updatedValue)
			throws SQLException
	{
		// Only do this for strings.
		if (jdbcTypeToClass(comp.getBoundColumnJDBCType()) != String.class)
				return;

		ForceConflict fc = defLookup(ForceConflict.class);
		if (fc == null || !fc.doForce())
			return;
		
		try(RowSet rs = defLookup(SSDBSupport.class).getJdbcRowSet(comp.getRowSet());) {
			rs.setCommand(comp.getRowSet().getCommand());
			rs.execute();
			rs.absolute(comp.getRowSet().getRow());
			System.err.printf("FORCE_CONFLICT: %s\n",
					rs.getObject(comp.getBoundColumnIndex()));
			rs.updateString(comp.getBoundColumnIndex(), updatedValue + "_ForceConflict");
			rs.updateRow();
		}
	}

	/**
	 * Method used by SwingSet component listeners to update the underlying
	 * RowSet.
	 * <p>
	 * When the user changes/edits the SwingSet column this method propagates the
	 * change to the RowSet. A separate call is required to flush/commit the change
	 * to the database.
	 *
	 * @param comp The SSComponent doing the update
	 * @param updatedValue string to be type-converted as needed and updated in
	 *                      underlying RowSet column
	 * @throws SSSQLNullException thrown if null is not allowed
	 * @throws SQLException  thrown if a database error is encountered
	 * @throws NumberFormatException thrown if unable to parse a string to number format
	 */
	public static void updateColumnText(SSComponentInterface comp, String updatedValue)
			throws SSSQLNullException, SQLException, NumberFormatException
	{ 
		// TODO: This is only for debug
		checkForceConflict(comp, updatedValue);
		updateColumnText(comp, comp.getRowSet(), updatedValue,
						 comp.getBoundColumnIndex(), comp.getAllowNull());
	}

	/**
	 * Method used by SwingSet component listeners to update the underlying
	 * RowSet.
	 * <p>
	 * When the user changes/edits the SwingSet column this method propagates the
	 * change to the RowSet. A separate call is required to flush/commit the change
	 * to the database.
	 *
	 * @param comp The SSComponent doing the update
	 * @param rowSet RowSet on which to operate
	 * @param updatedValue string to be type-converted as needed and updated in
	 *                      underlying RowSet column
	 * @param columnIndex   name of the database column
	 * @param allowNull 	indicates if Component and underlying column can contain null values
	 * @throws SSSQLNullException thrown if null is not allowed
	 * @throws SQLException  thrown if a database error is encountered
	 * @throws NumberFormatException thrown if unable to parse a string to number format
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B
	 */
	// TODO: test this and conversions
	private static void updateColumnText(SSComponentInterface comp, RowSet
			rowSet, String updatedValue, int columnIndex, boolean allowNull)
			throws SSSQLNullException, SQLException, NumberFormatException
	{
		int row = logger.isLoggable(DEBUG) ? rowSet.getRow() : -1;
		logger.log(DEBUG, () -> sf("[%s] row %d. Update to: %s. Allow null? [%s]",
				   comp.getColumnForLog(), row, updatedValue, allowNull));

		JDBCType jdbcType = getJDBCType(getColumnType(rowSet, columnIndex));
		
		if (!textUpdateOK.contains(jdbcType)) {
			logger.log(ERROR, "Unsupported data type of " + jdbcType.getName() + " for column " + comp.getColumnForLog() + ".");
			return;
		}

		UndoRedo.captureInitialValue(comp); // undo/redo

		boolean did_update = false;
		Object dbValue = null;
		try {
			// On insert row, write null if updatedValue is null or empty string,
			// and do not perform other checks.
			// TODO: isBlank???
			if ((updatedValue == null || updatedValue.isEmpty()) && RowSetState.isInserting(rowSet)) {
				rowSet.updateNull(columnIndex);
				did_update = true;
				return;
			}
			
			/*
			* FIRST - NULL HANDLING:
			*
			* For character-based columns where _allowNull==true, we write null rather than an empty string
			* We do this because a column could allow null, but have a UNIQUE constraint and each null
			* should be unique.
			*
			* We want to enter this code under 3 conditions:
			*  1. updateColumnText() is passed a null string
			*  2. updateColumnText() is passed an empty string (any column type)
			*  3. updateColumnText() is passed a 'blank' (whitespace) string
			*     for a non-character-based field (e.g., "" or "   " for a double)
			*
			* If !allowNull then a character based field with 0 or more blank spaced
			* will be allowed and code will continue to the switch/case statement below.
			*/
			if (updatedValue == null
					|| updatedValue.isEmpty()
					|| (updatedValue.isBlank() && !textUpdateEmptyOK.contains(jdbcType))) {
				if (allowNull) {
					rowSet.updateNull(columnIndex);
					did_update = true;
					return;
				} else if (!textUpdateEmptyOK.contains(jdbcType)) {
					// This will throw an exception for a non-char type, but allow
					// a char-based type with an empty string to continue to the
					// switch/case below and write the empty string via
					// rowSet.updateString(_columnName, _updatedValue)
					//
					// Note that if there is a UNIQUE constraint on such a text
					// column then repeatedly writing the same number (0 to N)
					// spaces should throw an SQL exception
					// (as should any other duplicate string)
			
					// TODO: Have a method "CreateMessage(RSC) see also
					//		 SSFormattedTextField, SSCommon
					// NOTE: in following should mention column name
					throw new SSSQLNullException("Null values are not allowed for this field.");
				}
			}
			assert(updatedValue != null);
			
			/*
			 * SECOND - update non-null values based on string conversions
			 */
			switch (jdbcType) {
			
			case INTEGER, SMALLINT, TINYINT, BIGINT,
					REAL, DOUBLE, FLOAT, DECIMAL, NUMERIC,
					BOOLEAN, BIT,
					CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR ->
				dbValue = convertToType(updatedValue, jdbcType);
				//dbValue = updatedValue; // Let DB convert.
			case DATE, TIME, TIMESTAMP -> // TODO: use convertObjectType when...
				dbValue = getSQLDateTimeObject(updatedValue, comp);
			default ->
				// TODO: SSSQLExceptionUnhandledType
				throw new IllegalStateException("switch cases out of sync");
			} // end switch
			rowSet.updateObject(columnIndex, dbValue);
			did_update = true;
		} finally {
			if (did_update)
				postRowSetModified(comp, dbValue);

		}

	} // end protected void updateColumnText(String _updatedValue, String _columnName)

	// FOLLOWING ONLY USED FROM SSTableModel (AT LEAST FOR NOW)

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
	 * @param type NOT USED, the jdbc driver does the conversion
	 * @throws SQLException  thrown if a database error is encountered
	 */
	public static void updateColumnObjectDirect(RowSet _rowSet, int _columnIndex, Object _value, JDBCType type) throws SQLException {
		if (Boolean.TRUE)
			updateColumnObject1(_rowSet, _columnIndex, _value);
		else
			updateColumnObject2(_rowSet, _columnIndex, _value, type);
	}

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
	 * @throws SQLException  thrown if a database error is encountered
	 */
	public static void updateColumnObjectDirect(RowSet _rowSet, int _columnIndex, Object _value) throws SQLException {
		if (Boolean.TRUE)
			updateColumnObject1(_rowSet, _columnIndex, _value);
		else
			updateColumnObject2(_rowSet, _columnIndex, _value, getJDBCColumnType(_rowSet, _columnIndex));
	}

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
	 * @throws SQLException  thrown if a database error is encountered
	 */
	public static void updateColumnObject1(RowSet _rowSet, int _columnIndex, Object _value) throws SQLException {
		_rowSet.updateObject(_columnIndex, _value);
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
				-> logger.log(ERROR, "Unknown data type of " + type);
		}
	}

}
