/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
import java.time.Instant;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.Optional;

import javax.sql.RowSet;

import org.apache.logging.log4j.LogManager;

import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.utils.SSCommon;

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

	// TODO Audit type handling based on http://www.java2s.com/Code/Java/Database-SQL-JDBC/StandardSQLDataTypeswithTheirJavaEquivalents.htm

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
			LogManager.getLogger().error(() -> String.format("SQL Exception for column %d.",
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
			LogManager.getLogger().error(() -> String.format("SQL Exception for column %s.",
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

			case FLOAT:
				value = String.valueOf(_rowSet.getFloat(_columnName));
				break;

			case DOUBLE:
			case REAL:
				value = String.valueOf(_rowSet.getDouble(_columnName));
				break;
				
			case NUMERIC:
			case DECIMAL:
				value = String.valueOf(_rowSet.getBigDecimal(_columnName));
				break;

			case BOOLEAN:
			case BIT:
				value = String.valueOf(_rowSet.getBoolean(_columnName));
				break;

			case DATE:
			case TIMESTAMP:
				final Date date = _rowSet.getDate(_columnName);
				if (date == null) {
					value = "";
				} else {
					final GregorianCalendar calendar = new GregorianCalendar();
					calendar.setTime(date);
					value = "";
					if ((calendar.get(Calendar.MONTH) + 1) < 10) {
						value = "0";
					}
					value = value + (calendar.get(Calendar.MONTH) + 1) + "/";

					if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
						value = value + "0";
					}
					value = value + calendar.get(Calendar.DAY_OF_MONTH) + "/";
					value = value + calendar.get(Calendar.YEAR);
				}
				break;

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
				final String str = _rowSet.getString(_columnName);
				if (str == null) {
					value = "";
				} else {
					value = String.valueOf(str);
				}
				break;

			default:
				LogManager.getLogger().error("Unsupported data type of " + jdbcType.getName() + " for column " + _columnName + ".");
			} // end switch

		} catch (final SQLException se) {
			LogManager.getLogger().error("SQL Exception for column " + _columnName + ".", se);
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
	 * @throws NullPointerException thrown if null is not allowed
	 * @throws SQLException  thrown if a database error is encountered
	 * @throws NumberFormatException thrown if unable to parse a string to number format
	 */
	// TODO: Eclipse is giving a Potential null pointer access, but we have assert(_updatedValue != null) so may be able to remove warning in future.
	@SuppressWarnings("null")
	public static void updateColumnText(final RowSet _rowSet, final String _updatedValue, final String _columnName, final boolean _allowNull) throws NullPointerException, SQLException, NumberFormatException {

		LogManager.getLogger().debug("[" + _columnName + "]. Update to: " + _updatedValue + ". Allow null? [" + _allowNull + "]");

		JDBCType jdbcType = getJDBCType(getColumnType(_rowSet, _columnName));
		
		if (!textUpdateOK.contains(jdbcType)) {
			LogManager.getLogger().error("Unsupported data type of " + jdbcType.getName() + " for column " + _columnName + ".");
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
                throw new NullPointerException("Null values are not allowed for this field.");
            }
        }
		assert(_updatedValue != null);

		/*
		 * SECOND - WRITING NON-NULL VALUES TO DATABASE BASED ON APPROPRIATE STRING CONVERSIONS
		 */
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
			
		case FLOAT:
			final float floatValue = Float.parseFloat(_updatedValue);
			_rowSet.updateFloat(_columnName, floatValue);
			break;
			
		case DOUBLE:
		//case NUMERIC: 
		//case DECIMAL:
		case REAL:
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
			final boolean boolValue = Boolean.valueOf(_updatedValue);
			_rowSet.updateBoolean(_columnName, boolValue);
			break;
			
		case DATE:
// TODO Good to get rid of getSQLDate if possible.
			if (_updatedValue.length() == 10) {
				Date dateValue = SSCommon.getSQLDate(_updatedValue);
				_rowSet.updateDate(_columnName, dateValue);
			} else {
			// 2020-12-01_BP: Might as well at least try to process a date that is other than 10 characters
				Date dateValue = java.sql.Date.valueOf(_updatedValue);
				_rowSet.updateDate(_columnName, dateValue);
			}
			break;
			
		case TIME:
			Time timeValue = java.sql.Time.valueOf(_updatedValue);
			_rowSet.updateTime(_columnName, timeValue);
			break;
			
		case TIMESTAMP:
		// TODO: We're not doing anything here
		// TODO: Probably a better way to handle date to timestamp conversion. Formatter? Get rid of getSQLDate() if possible.
			// CONVERT ANY 10 CHARACTER DATE (e.g., yyyy-mm-dd, mm/dd/yyyy to a date/time)
			if (_updatedValue.length() == 10) {
				Timestamp timestampValue = new Timestamp(SSCommon.getSQLDate(_updatedValue).getTime());
				_rowSet.updateTimestamp(_columnName, timestampValue);
			} else {
			// Per ER email 2020-11-25, we weren't even trying to handle a legitimate timestamp
				Timestamp timeStampValue = java.sql.Timestamp.valueOf(_updatedValue);
				_rowSet.updateTimestamp(_columnName, timeStampValue);
			}
			break;
			
		case CHAR:
		case VARCHAR:
		case LONGVARCHAR:
			_rowSet.updateString(_columnName, _updatedValue);
			break;

		default:
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
	private static final EnumMap<JDBCType, Class<?>> overrideJdbcToJavaType = new EnumMap<JDBCType, Class<?>>(JDBCType.class);
	/**
	 * Determine the Java type class for the given database type.
	 * @param _jdbcType JDBCType of interest
	 * @return the class object used for the given type
	 * @throws SQLException if the JDBCType is not handled
	 */
	public static Class<?> findJavaTypeClass(final JDBCType _jdbcType)
	throws SQLException {
		Class<?> clazz = overrideJdbcToJavaType.getOrDefault(_jdbcType, null);
		if (clazz != null) {
			return clazz;
		}

		switch (_jdbcType) {
			case INTEGER:
			case SMALLINT:
			case TINYINT:
				clazz = Integer.class;
				break;
			case BIT:
				clazz = Boolean.class;
				break;
			case BIGINT:
				clazz = Long.class;
				break;
			case FLOAT:
			case DOUBLE:
			case REAL:
				clazz = Double.class;
				break;
			case DECIMAL:
			case NUMERIC:
				clazz = BigDecimal.class;
				break;
			case DATE:
			case TIME:
			case TIMESTAMP:
				clazz = java.util.Date.class;
				break;
			case TIME_WITH_TIMEZONE:
			case TIMESTAMP_WITH_TIMEZONE:
				clazz = Instant.class;
				break;
			case CHAR:
			case VARCHAR:
			case LONGVARCHAR:
			case NCHAR:
			case NVARCHAR:
			case LONGNVARCHAR:
				clazz = String.class;
				break;
			default:
				throw new SQLException("Unhandled type: " + _jdbcType);
		}
		return clazz;
	}
}
