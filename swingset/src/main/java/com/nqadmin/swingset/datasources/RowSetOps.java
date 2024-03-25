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
package com.nqadmin.swingset.datasources;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.Optional;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.spi.SyncProviderException;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSUtils;

// RowSetOps.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Utility class for working with {@link RowSet}s and {@link ResultSet}s.
 * Some methods for converting to/from text from/to objects according
 * to database type. Several convenience methods for accessing metadata.
 * 
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
		if (_resultSet instanceof CachedRowSet) {
			CachedRowSet crs = (CachedRowSet)_resultSet;
			_resultSet.moveToCurrentRow();
			try {
				SSDataNavigator.acceptChanges(crs, null);
			} catch (SyncProviderException ex) {
				// TODO: test CRS undoInsert after accept changes
				crs.undoInsert();
				throw ex;
			}
		}
	}

	/**
	 * Updates the underlying database with the new contents of the current row
	 * of this {@linkplain ResultSet} object. Handle CachedRowSet.
	 * @param _resultSet
	 * @throws SQLException
	 */
	public static void updateRow(ResultSet _resultSet) throws SQLException {
		_resultSet.updateRow();
		if (_resultSet instanceof CachedRowSet) {
			CachedRowSet crs = (CachedRowSet)_resultSet;
			//SQLException ex = null;
			int thisRow = _resultSet.getRow();
			try {
				SSDataNavigator.acceptChanges(crs, () -> {
					try {
						_resultSet.absolute(thisRow);
					} catch (SQLException ex) { }	// TODO: find nice way to propogate
				});
			} catch (SyncProviderException ex01) {
				// TODO: test CRS undoUpdate after accept changes
				//ex = ex01;
				crs.undoUpdate();
				throw ex01;
			}
			//_resultSet.absolute(thisRow);
			//if (ex != null) {
			//	throw ex;
			//}
		}
	}

	/**
	 * Deletes the current row from this {@linkplain ResultSet} and from the
	 * underlying database. Handle CachedRowSet.
	 * @param _resultSet
	 * @throws SQLException 
	 */
	public static void deleteRow(ResultSet _resultSet) throws SQLException {
		_resultSet.deleteRow();
		if (_resultSet instanceof CachedRowSet) {
			CachedRowSet crs = (CachedRowSet)_resultSet;
			try {
				SSDataNavigator.acceptChanges(crs, null);
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
			logger.error(() -> String.format("SQL Exception for column %d.",
					_columnIndex, ex));
			return Optional.empty();
		}
	}

	/**
	 * Determine if the specified column is nullable.If the nullability
	 * of the column is unknown, then an empty Optional is returned.
	 * @param _resultSet RowSet on which to operate
	 * @param _columnName column name
	 * @return Optional of true if nullable, empty Optional if unknown.
	 */
	public static Optional<Boolean> isNullable(final ResultSet _resultSet, final String _columnName) {
		try {
			return isNullable(_resultSet, getColumnIndex(_resultSet, _columnName));
		} catch (SQLException ex) {
			logger.error(() -> String.format("SQL Exception for column %s.",
					_columnName, ex));
			return Optional.empty();
		}
	}
	
	/**
	 * Method used by RowSet listeners to get the new text when the RowSet
	 * events are triggered.
	 *
	 * @param _rowSet RowSet on which to operate
	 * @param _columnName - name of database column to retrieve
	 *
	 * @return text representation of data in specified column
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B
	 */
	public static String getColumnText(final RowSet _rowSet, final String _columnName) {
		String value = null;

		try {
			// IF THE COLUMN IS NULL SO RETURN NULL
			if ((getColumnCount(_rowSet)==0) || (_rowSet.getObject(_columnName) == null)) {
				return null;
			}

			//final int columnType = getColumnType(_rowSet, _columnName);
			
			final JDBCType jdbcType = getJDBCType(getColumnType(_rowSet, _columnName));

			// BASED ON THE COLUMN DATA TYPE THE CORRESPONDING FUNCTION
			// IS CALLED TO GET THE VALUE IN THE COLUMN
			switch (jdbcType) {
			case INTEGER:
			case SMALLINT:
			case TINYINT:
				value = String.valueOf(_rowSet.getInt(_columnName));
				break;

			case BIGINT:
				value = String.valueOf(_rowSet.getLong(_columnName));
				break;

			case REAL:
				value = String.valueOf(_rowSet.getFloat(_columnName));
				break;

			case DOUBLE:
			case FLOAT:
				value = String.valueOf(_rowSet.getDouble(_columnName));
				break;
				
			case NUMERIC:
			case DECIMAL:
				value = String.valueOf(_rowSet.getBigDecimal(_columnName));
				break;

			case BIT:
			case BOOLEAN:
				value = String.valueOf(_rowSet.getBoolean(_columnName));
				break;
// TODO: Convert this to use java.time.LocalDate, LocalTime, or LocalDateTime as needed.
			case DATE:
				// NOTE: See SSCommon/getStringDate for a modified version
				// Convert to "##/##/####", month/day/year, month day has two digits.
				final Date date = _rowSet.getDate(_columnName);
				if (date == null) {
					value = "";
				} else {
					//
					// TODO: Would the "SSCommon.getStringDate" format be OK?
					//		 That format does not have leading zero on
					//		 single digit month or day.
					//
					final GregorianCalendar calendar = new GregorianCalendar();
					calendar.setTime(date);
					value = String.format("%02d/%02d/%d",
							calendar.get(Calendar.MONTH) + 1,
							calendar.get(Calendar.DAY_OF_MONTH),
							calendar.get(Calendar.YEAR));
				}
				break;
			case TIMESTAMP:
				Timestamp timestamp = _rowSet.getTimestamp(_columnName);
				if (timestamp == null) {
					value = "";
				} else {
					//
					// TODO: this isn't "month/day/year". DOES THAT MATTER?
					//
					// Convert to "yyyy-mm-dd hh:mm:ss.fffffffff"
					// substring(0, 19) // without the nanoseconds
					// This format matches what "updateColumnText" expects
					//System.err.println("TIMESTAMP get: " + timestamp);
					value = timestamp.toString();
				}
				break;

				
// TODO: Convert this to use java.time.LocalTime.
			case TIME:
				final Time time = _rowSet.getTime(_columnName);
				if (time == null) {
					value = "";
				} else {
					value=time.toString();
				}
				break;

			case CHAR:
			case VARCHAR:
			case LONGVARCHAR:
			case NCHAR:
			case NVARCHAR:
			case LONGNVARCHAR:
				final String str = _rowSet.getString(_columnName);
				if (str == null) {
					value = "";
				} else {
					value = String.valueOf(str);
				}
				break;

			default:
				// TODO: SSSQLExceptionUnhandledType
				logger.error("Unsupported data type of " + jdbcType.getName() + " for column " + _columnName + ".");
			} // end switch

		} catch (final SQLException se) {
			logger.error("SQL Exception for column " + _columnName + ".", se);
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
	 * @param _columnName - name of the column
	 *
	 * @param _resultSet ResultSet on which to operate
	 * @return JDBCType of the column
	 *
	 * @throws SQLException - if a database access error occurs
	 */
	public static int getColumnType(final ResultSet _resultSet, final String _columnName) throws SQLException {
		return _resultSet.getMetaData().getColumnType(getColumnIndex(_resultSet, _columnName));
	}

	/**
	 * Retrieves an int corresponding to the designated column's type based on the
	 * column name
	 *
	 * @see java.sql.JDBCType
	 *
	 * @param _columnName - name of the column
	 *
	 * @param _resultSet ResultSet on which to operate
	 * @return JDBCType of the column
	 *
	 * @throws SQLException - if a database access error occurs
	 */
	public static JDBCType getJDBCColumnType(final ResultSet _resultSet, final String _columnName) throws SQLException {
		return getJDBCType(getColumnType(_resultSet, _columnName));
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
		return RowSetOps.findJavaTypeClass(type);
	}

	private static final EnumSet<JDBCType> textUpdateEmptyOK = EnumSet.of(
			JDBCType.CHAR,
			JDBCType.VARCHAR,
			JDBCType.LONGVARCHAR
	);
	
	private static final EnumSet<JDBCType> textUpdateOK = EnumSet.of(
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
			JDBCType.LONGVARCHAR
	);

	/**
	 * Method used by SwingSet component listeners to update the underlying
	 * RowSet.
	 * <p>
	 * When the user changes/edits the SwingSet column this method propagates the
	 * change to the RowSet. A separate call is required to flush/commit the change
	 * to the database.
	 *
	 * @param _rowSet RowSet on which to operate
	 * @param _updatedValue string to be type-converted as needed and updated in
	 *                      underlying RowSet column
	 * @param _columnName   name of the database column
	 * @param _allowNull 	indicates if Component and underlying column can contain null values
	 * @throws SSSQLNullException thrown if null is not allowed
	 * @throws SQLException  thrown if a database error is encountered
	 * @throws NumberFormatException thrown if unable to parse a string to number format
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B
	 */
	// TODO: Eclipse is giving a Potential null pointer access, but we have assert(_updatedValue != null) so may be able to remove warning in future.
	@SuppressWarnings("null")
	public static void updateColumnText(final RowSet _rowSet, final String _updatedValue, final String _columnName, final boolean _allowNull) throws SSSQLNullException, SQLException, NumberFormatException {

		logger.debug("[" + _columnName + "]. Update to: " + _updatedValue + ". Allow null? [" + _allowNull + "]");

		JDBCType jdbcType = getJDBCType(getColumnType(_rowSet, _columnName));
		
		if (!textUpdateOK.contains(jdbcType)) {
			logger.error("Unsupported data type of " + jdbcType.getName() + " for column " + _columnName + ".");
			return;
		}

		// On insert row, write null if updatedValue is null or empty string, and do not perform other checks. 
		if ((_updatedValue == null || _updatedValue.isEmpty()) && SSDataNavigator.isInserting(_rowSet)) {
			_rowSet.updateNull(_columnName);
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
		 *  3. updateColumnText() is passed a 'blank' (whitespace) string for a non-character-based field
		 *     (e.g., "" or "   " for a double)
		 *     
		 * If !_allowNull then a character based field with 0 or more blank spaced will be allowed
		 * and code will continue to the switch/case statement below.
		 */
       if (_updatedValue == null
    		   || _updatedValue.isEmpty()
               || (_updatedValue.trim().isEmpty() && !textUpdateEmptyOK.contains(jdbcType))) {
    	    // TODO: Switch to isBlank) for Java 11+
    	   	// Java 11: || (_updatedValue.isBlank() && !textUpdateEmptyOK.contains(jdbcType))) {

            if (_allowNull) {
                _rowSet.updateNull(_columnName);
                return;
            } else if (!textUpdateEmptyOK.contains(jdbcType)) {
                // This will throw an exception for a non-char type, but allow a char-based type with
            	// an empty string to continue to the switch/case below and write the empty string via
            	// _rowSet.updateString(_columnName, _updatedValue)
            	//
            	// Note that if there is a UNIQUE constraint on such a text column then repeatedly writing the same 
            	// number (0 to N) spaces should throw an SQL exception (as should any other duplicate string)
                throw new SSSQLNullException("Null values are not allowed for this field.");
            }
        }
		assert(_updatedValue != null);

		/*
		 * SECOND - WRITING NON-NULL VALUES TO DATABASE BASED ON APPROPRIATE STRING CONVERSIONS
		 */
		// TODO: Use setObject(_updatedValue) for numerics?
		//		 But it is nice to catch problems early,
		//		 as long as TextField stays consistent.
		switch (jdbcType) {
	
		case INTEGER:
		case SMALLINT:
		case TINYINT:
			final int intValue = Integer.parseInt(_updatedValue);
			_rowSet.updateInt(_columnName, intValue);
			break;
			
		case BIGINT:
			final long longValue = Long.parseLong(_updatedValue);
			_rowSet.updateLong(_columnName, longValue);
			break;
			
		case REAL:
			final float floatValue = Float.parseFloat(_updatedValue);
			_rowSet.updateFloat(_columnName, floatValue);
			break;
			
		case DOUBLE:
		case FLOAT:
			final double doubleValue = Double.parseDouble(_updatedValue);
			_rowSet.updateDouble(_columnName, doubleValue);
			break;
			
		case DECIMAL:
		case NUMERIC:
			_rowSet.updateBigDecimal(_columnName, new BigDecimal(_updatedValue));
			break;
			
		case BOOLEAN:
		case BIT:
			// CONVERT THE GIVEN STRING TO BOOLEAN TYPE
			final boolean boolValue = Boolean.parseBoolean(_updatedValue);
			_rowSet.updateBoolean(_columnName, boolValue);
			break;
			
		case DATE:
			// TODO: there's an ignored IllegalArgumentException possible
			// Convert a 10 character date "mm/dd/yyyy" or "yyyy-mm-dd"
// TODO Good to get rid of getSQLDate if possible.
// 2022-05-31_BP: Probably best to cast to a LocalDate and return if that fails.
// 	Can convert to SQL Date if successful.
			if (_updatedValue.length() == 10) {
				try {
					Date dateValue = SSCommon.getSQLDate(_updatedValue);
					_rowSet.updateDate(_columnName, dateValue);
				} catch(IllegalArgumentException ex) {
					logger.warn("updateColumnText: DATE: " + ex.getMessage());
					throw ex;
				}
// Per https://github.com/bpangburn/swingset/issues/141,
// this else block is throwing an exception for every character pressed for the SSTextField date mask.				
//			} else {
//			// 2020-12-01_BP: Might as well at least try to process a date that is other than 10 characters
//				Date dateValue = java.sql.Date.valueOf(_updatedValue);
//				_rowSet.updateDate(_columnName, dateValue);
			}
			break;
			
		case TIME:
			// TODO: there's an ignored IllegalArgumentException possible
			// Convert a time like "hh:mm:ss"
// TODO: Better way to handle Time conversion? Formatter? 
			if (_updatedValue.length() == 8) {
				try {
					Time timeValue = java.sql.Time.valueOf(_updatedValue);
					_rowSet.updateTime(_columnName, timeValue);
				} catch(IllegalArgumentException ex) {
					logger.warn("updateColumnText: TIME: " + ex.getMessage());
					throw ex;
				}
			}
			break;
			
		case TIMESTAMP:
			// TODO: there's an ignored IllegalArgumentException possible
			// Convert something like
			// "yyyy-mm-dd hh:mm:ss" or "yyyy-mm-dd hh:mm:ss.f[ff...]"

// TODO: Probably a better way to handle date to timestamp conversion. Formatter?
// 2022-05-31_BP: Probably best to cast to a LocalDateTime and return if that fails.
			Timestamp timestampValue = null;
			try {
				if (_updatedValue.length() == 10) {
					Date dateValue = SSCommon.getSQLDate(_updatedValue);
					timestampValue = new Timestamp(dateValue.getTime());
				} else if (_updatedValue.length() == 19 || _updatedValue.length() > 20) {
					timestampValue = java.sql.Timestamp.valueOf(_updatedValue);
				}
				if (timestampValue != null) {
					_rowSet.updateTimestamp(_columnName, timestampValue);
				}
			} catch(IllegalArgumentException ex) {
				logger.warn("updateColumnText: TIMESTAMP: " + ex.getMessage());
				throw ex;
			}
			break;
			
		case CHAR:
		case VARCHAR:
		case LONGVARCHAR:
		case NCHAR:
		case NVARCHAR:
		case LONGNVARCHAR:
			_rowSet.updateString(_columnName, _updatedValue);
			break;

		default:
			// TODO: SSSQLExceptionUnhandledType
			throw new IllegalStateException("switch cases out of sync");
		} // end switch

	} // end protected void updateColumnText(String _updatedValue, String _columnName)

	/**
	 * Convenience method for getting {@link JDBCType} enum from
	 * {@link java.sql.Types}.
	 * <p>
	 * May perform better than using
	 * {@link JDBCType#valueOf(java.lang.String) }
	 * @param sqlType the type to translate
	 * @return the corresponding JDBCType
	 */
	public static JDBCType getJDBCType(int sqlType) {
		// TODO: can create a map of sqlType to JDBCType if performance issue
		return JDBCType.valueOf(sqlType);
	}

	/**
	 * Copy the elements of the _objects array into into
	 * an array of the correct type for the {@code JDBCType}d objects.
	 * <p>
	 * If an array or even a collection of the accurate type is desired,
	 * you can do the follow which is type safe, no compiler warnings.
	 * There will be an exception if something is afoul.
	 * <pre>
	 * {@code
	 * Object[] arr = f(); // But I "know" the elements are Integer
	 * Integer[] newarr = (Integer[]) castJDBCToJava(JDBCType.INTEGER, arr);
	 * List<Integer> properList = Arrays.asList(newarr);
	 * }
	 * </pre>
	 * @param _objects array of objects to cast
	 * @param _jdbcType cast objects to this JDBCType
	 * @return array of corresponding type to the cast input objects
	 * @throws SQLException This exception wraps a {@code ClassCastException}
	 */
	public static Object[] castJDBCToJava(final JDBCType _jdbcType, final Object[] _objects) throws SQLException {
		Class<?> clazz = findJavaTypeClass(_jdbcType);
		Object[] newArray = (Object[]) java.lang.reflect.Array.newInstance(clazz, _objects.length);
		try {
			System.arraycopy(_objects, 0, newArray, 0, _objects.length);
		} catch(ArrayStoreException ex) {
			throw new SQLException(ex);
		}
		return newArray;
	}

	/**
	 * Cast the object to {@code JDBCType}. The idea is to verify
	 * the the object is of the correct type.
	 * @param _object object to cast
	 * @param _jdbcType cast object to this JDBCType
	 * @return Essentially the same Object that was input
	 * @throws SQLException This exception wraps a {@code ClassCastException}
	 */
	public static Object castJDBCToJava(final JDBCType _jdbcType, final Object _object) throws SQLException {
		try {
			return findJavaTypeClass(_jdbcType).cast(_object);
		} catch (ClassCastException ex) {
			throw new SQLException(ex);
		}
	}


	// TODO: for override of type mapping for local/dbms requirements
	// with_timezone might be the perfect candidates
	private static final EnumMap<JDBCType, Class<?>> overrideJdbcToJavaType = new EnumMap<>(JDBCType.class);

  private static void overrideJdbcStandard()
	{
		// The *_WITH_TIMEZONE aren't mentioned in appendix B.1 or B.3
		overrideJdbcToJavaType.put(JDBCType.TIME_WITH_TIMEZONE,
								   OffsetTime.class);
		overrideJdbcToJavaType.put(JDBCType.TIMESTAMP_WITH_TIMEZONE,
								   OffsetDateTime.class);

		// overrideJdbcToJavaType.put(JDBCType.SMALLINT, Byte.class);
		// overrideJdbcToJavaType.put(JDBCType.TINYINT, Short.class);
	}
	static { overrideJdbcStandard(); }
	
//=======
//>>>>>>> 4.1.0-SNAPSHOT
  
	/**
	 * Determine the Java type class for the given database type.
	 * @param _jdbcType JDBCType of interest
	 * @return the class object used for the given type
	 * @throws SQLException if the JDBCType is not handled
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B.3 JDBC Types Mapped to Java Object Types
	 */
	public static Class<?> findJavaTypeClass(final JDBCType _jdbcType)
	throws SQLException {
		Class<?> clazz = overrideJdbcToJavaType.getOrDefault(_jdbcType, null);
		if (clazz != null) {
			if (clazz == Exception.class) {
				throw new SQLException("Unhandled type: " + _jdbcType);
			}
			return clazz;
		}

		switch (_jdbcType) {
			case INTEGER:
			case SMALLINT:
			case TINYINT:
				clazz = Integer.class;
				break;
			case BIGINT:
				clazz = Long.class;
				break;
			case REAL:
				clazz = Float.class;
				break;
			case FLOAT:
			case DOUBLE:
				clazz = Double.class;
				break;
			case DECIMAL:
			case NUMERIC:
				clazz = BigDecimal.class;
				break;
			case BIT:
			case BOOLEAN:
				clazz = Boolean.class;
				break;
			//case DATE: case TIME: case TIMESTAMP: clazz = java.util.Date.class; break;
			case DATE:
				clazz = java.sql.Date.class;
				break;
			case TIME:
				clazz = java.sql.Time.class;
				break;
			case TIMESTAMP:
				clazz = java.sql.Timestamp.class;
				break;
			case CHAR:
			case VARCHAR:
			case LONGVARCHAR:
			case NCHAR:
			case NVARCHAR:
			case LONGNVARCHAR:
				clazz = String.class;
				break;

			// case ARRAY:
			// 	clazz = java.sql.Array.class;
			// 	break;

			// case BINARY:
			// case VARBINARY:
			// case LONGVARBINARY:
			// 	clazz = byte[].class;
			// 	break;

			// case CLOB: clazz = java.sql.Clob.class; break;
			// case BLOB: clazz = java.sql.Blob.class; break;
			// case REF: clazz = java.sql.Ref.class; break;
			// case DATALINK: clazz = java.net.URL.class; break;
			// case ROWID: clazz = java.sql.RowId.class; break;
			// case NCLOB: clazz = java.sql.NClob.class; break;
			// case SQLXML: clazz = java.sql.SQLXML.class; break;
			default:
				// TODO: SSSQLExceptionUnhandledType
				throw new SQLException("Unhandled type: " + _jdbcType);
		}
		return clazz;

		// case DISTINCT: Object type of underlying type
		// case STRUCT: java.sql.Struct or java.sql.SQLData
		// case JAVA_OBJECT: Underlying Java class
	}

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
	public static void updateColumnObject(RowSet _rowSet, Object _value, int _columnIndex, JDBCType type) throws SQLException {
		if (Boolean.TRUE)
			updateColumnObject1(_rowSet, _value, _columnIndex);
		else
			updateColumnObject2(_rowSet, _value, _columnIndex, type);
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
	public static void updateColumnObject(RowSet _rowSet, Object _value, int _columnIndex) throws SQLException {
		if (Boolean.TRUE)
			updateColumnObject1(_rowSet, _value, _columnIndex);
		else
			updateColumnObject2(_rowSet, _value, _columnIndex, getJDBCColumnType(_rowSet, _columnIndex));
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
	public static void updateColumnObject1(RowSet _rowSet, Object _value, int _columnIndex) throws SQLException {
		_rowSet.updateObject(_columnIndex, _value);
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
	 * @param type The JDBCType of the column
	 * @throws SQLException  thrown if a database error is encountered
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B
	 */
	public static void updateColumnObject2(RowSet _rowSet, Object _value, int _columnIndex, JDBCType type) throws SQLException {
		switch (type) {
		case INTEGER:
		case SMALLINT:
		case TINYINT:
			_rowSet.updateInt(_columnIndex, ((Integer) _value));
			break;
		case BIGINT:
			_rowSet.updateLong(_columnIndex, ((Long) _value));
			break;
		case REAL:
			_rowSet.updateFloat(_columnIndex, ((Float) _value));
			break;
		case FLOAT:
		case DOUBLE:
			_rowSet.updateDouble(_columnIndex, ((Double) _value));
			break;
		case DECIMAL:
		case NUMERIC:
			_rowSet.updateBigDecimal(_columnIndex, ((BigDecimal) _value));
			break;
		case BOOLEAN:
		case BIT:
			_rowSet.updateBoolean(_columnIndex, ((Boolean) _value));
			break;
		case DATE:
			_rowSet.updateDate(_columnIndex, (Date) _value);
			break;
		case TIME:
			_rowSet.updateTime(_columnIndex, (Time) _value);
			break;
		case TIMESTAMP:
			_rowSet.updateTimestamp(_columnIndex, (Timestamp) _value);
			break;
		case CHAR:
		case VARCHAR:
		case LONGVARCHAR:
		case NCHAR:
		case NVARCHAR:
		case LONGNVARCHAR:
			_rowSet.updateString(_columnIndex, (String) _value);
			break;
		default:
			// TODO: SSSQLExceptionUnhandledType
			logger.warn("Unknown data type of " + type);
		}
		
	}

	/**
	 * Fetch and return object for specified rowset's specified column
	 * from the database; object type is determined by column's JDBCType.
	 * There is no filtering, for example null conversion.
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B-1
	 * @param rowset source rowset
	 * @param _column source column
	 * @return object from RowSet.getXxx
	 * @throws SQLException database error
	 */
	public static Object getColumnObject(RowSet rowset, int _column) throws SQLException
	{
		if (Boolean.TRUE) {
			return getColumnObjectDirect(rowset, _column);
		} else {
			// This returns null for items not enumerated in switch.
			return getColumnObjectDirectSwitch(rowset, _column);
		}
	}

	private static Object getColumnObjectDirect(RowSet rowset, int _column) throws SQLException
	{
		return rowset.getObject(_column);
	}

	//
	// This switch code is the original from SSDataGrid/SSTableModel,
	// amended to include additional column types and to meet JDBC spec.
	// The "rowset.getObject(_column)" should produce the same result,
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
	private static Object getColumnObjectDirectSwitch(RowSet rowset, int _column) throws SQLException
	{
		Object value = null;
		JDBCType type = RowSetOps.getJDBCColumnType(rowset, _column);

		switch (type) {
		case INTEGER:
		case SMALLINT:
		case TINYINT:
			value = rowset.getInt(_column);
			break;
		case BIGINT:
			value = rowset.getLong(_column);
			break;
		case REAL:
			value = rowset.getFloat(_column);
			break;
		case FLOAT:
		case DOUBLE:
			value = rowset.getDouble(_column);
			break;
		case DECIMAL:
		case NUMERIC:
			value = rowset.getBigDecimal(_column);
			break;
		case BOOLEAN:
		case BIT:
			value = rowset.getBoolean(_column);
			break;
		case DATE:
			value = rowset.getDate(_column);
			break;
		case TIME:
			value = rowset.getTime(_column);
			break;
		case TIMESTAMP:
			value = rowset.getTimestamp(_column);
			break;
		case CHAR:
		case VARCHAR:
		case LONGVARCHAR:
		case NCHAR:
		case NVARCHAR:
		case LONGNVARCHAR:
			value = rowset.getString(_column);
			break;
		default:
			logger.warn("Unknown data type of " + type);
		}
		return value;
	}

	/**
	 * Null used as database value where not allowed.
	 */
	@SuppressWarnings("serial")
	public static class SSSQLNullException extends SQLException {

		/**
		 * Construct an SQLException with given reason.
		 * 
		 * @param reason description of the exception
		 */
		public SSSQLNullException(String reason) {
			super(reason);
		}

	}
}
