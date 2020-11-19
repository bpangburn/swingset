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
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.sql.RowSet;
import javax.sql.RowSetListener;

import org.apache.logging.log4j.LogManager;

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
	 * Wrapper/convenience method for SwingSet method naming consistency.
	 *
	 * @param _rowSet RowSet on which to operate
	 * @param _rowSetListener RowSetListener to add to current SSRowSet
	 */
	//TODO: should this type be SSRowSetListener?
	public static void addSSRowSetListener(final RowSet _rowSet, final RowSetListener _rowSetListener) {
		_rowSet.addRowSetListener(_rowSetListener);
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
	 * Method used by ssRowSet listeners to get the new text when the SSRowSet
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

			final int columnType = getColumnType(_rowSet, _columnName);

			// BASED ON THE COLUMN DATA TYPE THE CORRESPONDING FUNCTION
			// IS CALLED TO GET THE VALUE IN THE COLUMN
			switch (columnType) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				value = String.valueOf(_rowSet.getInt(_columnName));
				break;

			case Types.BIGINT:
				value = String.valueOf(_rowSet.getLong(_columnName));
				break;

			case Types.FLOAT:
				value = String.valueOf(_rowSet.getFloat(_columnName));
				break;

			case Types.DOUBLE:
			case Types.NUMERIC:
			case Types.DECIMAL:
				value = String.valueOf(_rowSet.getDouble(_columnName));
				break;

			case Types.BOOLEAN:
			case Types.BIT:
				value = String.valueOf(_rowSet.getBoolean(_columnName));
				break;

			case Types.DATE:
			case Types.TIMESTAMP:
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
					// value = String.valueOf(sSRowSet.getDate(columnName));
				}
				break;

			case Types.TIME:
				final Time time = _rowSet.getTime(_columnName);
				if (time == null) {
					value = "";
				} else {
					value=time.toString();
				}
				break;

			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				final String str = _rowSet.getString(_columnName);
				if (str == null) {
					value = "";
				} else {
					value = String.valueOf(str);
				}
				break;

			default:
				LogManager.getLogger().error("Unsupported data type of " + JDBCType.valueOf(columnType).getName() + " for column " + _columnName + ".");
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

	/**
	 * Wrapper/convenience method for SwingSet method naming consistency.
	 *
	 * @param _rowSet RowSet on which to operate
	 * @param _rowSetListener RowSetListener to remove from current SSRowSet
	 */
	public static void removeSSRowSetListener(final RowSet _rowSet, final RowSetListener _rowSetListener) {
		_rowSet.removeRowSetListener(_rowSetListener);
	}

	/**
	 * Method used by SwingSet component listeners to update the underlying
	 * SSRowSet.
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
	public static void updateColumnText(final RowSet _rowSet, final String _updatedValue, final String _columnName, final boolean _allowNull) throws NullPointerException, SQLException, NumberFormatException {

//		try {

			// TODO Add proper support for null vs "" based on _allowNull. For Char types all "" are currently forced to null,
//			if (!_allowNull && _updatedValue==null) {
//				_updatedValue = "";
//			}

			// TODO Convert this code to use Java 8 JDBCType enum

			// 2020-09-11_BP: Probably not a good idea to trim here as it may be desirable to have padding for some strings
			// Also, it's coded wrong.
			// Should be:
			//	if (_updatedValue!=null) _updatedValue = _updatedValue.trim();
			// not:
			// 	if (_updatedValue!=null) _updatedValue.trim();


			LogManager.getLogger().debug("[" + _columnName + "]. Update to: " + _updatedValue + ". Allow null? " + _allowNull);

			final int columnType = getColumnType(_rowSet, _columnName);

			switch (columnType) {
			// FOR NON-TEXT-BASED DATABASE COLUMNS, WRITE NULL INSTEAD OF AN EMPTY STRING

			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if ((_updatedValue==null) || _updatedValue.equals("")) {
					if (_allowNull) {
						_rowSet.updateNull(_columnName);
					} else {
						throw new NullPointerException("Null values are not allowed for this field.");
					}
				} else {
					final int intValue = Integer.parseInt(_updatedValue);
					_rowSet.updateInt(_columnName, intValue);
				}
				break;

			case Types.BIGINT:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if ((_updatedValue==null) || _updatedValue.equals("")) {
					if (_allowNull) {
						_rowSet.updateNull(_columnName);
					} else {
						throw new NullPointerException("Null values are not allowed for this field.");
					}
				} else {
					final long longValue = Long.parseLong(_updatedValue);
					_rowSet.updateLong(_columnName, longValue);
				}
				break;

			case Types.FLOAT:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if ((_updatedValue==null) || _updatedValue.equals("")) {
					if (_allowNull) {
						_rowSet.updateNull(_columnName);
					} else {
						throw new NullPointerException("Null values are not allowed for this field.");
					}
				} else {
					final float floatValue = Float.parseFloat(_updatedValue);
					_rowSet.updateFloat(_columnName, floatValue);
				}
				break;

			case Types.DOUBLE:
			case Types.NUMERIC:
			case Types.DECIMAL:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if ((_updatedValue==null) || _updatedValue.equals("")) {
					if (_allowNull) {
						_rowSet.updateNull(_columnName);
					} else {
						throw new NullPointerException("Null values are not allowed for this field.");
					}
				} else {
					final double doubleValue = Double.parseDouble(_updatedValue);
					_rowSet.updateDouble(_columnName, doubleValue);
				}
				break;

			case Types.BOOLEAN:
			case Types.BIT:
				if ((_updatedValue==null) || _updatedValue.equals("")) {
					if (_allowNull) {
						_rowSet.updateNull(_columnName);
					} else {
						throw new NullPointerException("Null values are not allowed for this field.");
					}
				} else {
					// CONVERT THE GIVEN STRING TO BOOLEAN TYPE
					final boolean boolValue = Boolean.valueOf(_updatedValue);
					_rowSet.updateBoolean(_columnName, boolValue);
				}
				break;

			case Types.DATE:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if ((_updatedValue==null) || _updatedValue.equals("")) {
					if (_allowNull) {
						_rowSet.updateNull(_columnName);
					} else {
						throw new NullPointerException("Null values are not allowed for this field.");
					}
// TODO Good to get rid of getSQLDate if possible.
				} else if (_updatedValue.length() == 10) {
					_rowSet.updateDate(_columnName, SSCommon.getSQLDate(_updatedValue));
				} else {
					// do nothing
				}
				break;

			case Types.TIME:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if ((_updatedValue==null) || _updatedValue.equals("")) {
					if (_allowNull) {
						_rowSet.updateNull(_columnName);
					} else {
						throw new NullPointerException("Null values are not allowed for this field.");
					}
				} else {
					_rowSet.updateTime(_columnName, java.sql.Time.valueOf(_updatedValue));
				}
				break;

			case Types.TIMESTAMP:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if ((_updatedValue==null) || _updatedValue.equals("")) {
					if (_allowNull) {
						_rowSet.updateNull(_columnName);
					} else {
						throw new NullPointerException("Null values are not allowed for this field.");
					}
// TODO Probably do not want a length of 10 characters here. Good to get rid of getSQLDate if possible.
				} else if (_updatedValue.length() == 10) {
					_rowSet.updateTimestamp(_columnName, new Timestamp(SSCommon.getSQLDate(_updatedValue).getTime()));
				} else {
					// do nothing
				}
				break;

			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				// FOR TEXT-BASED DATABASE COLUMNS WE CAN WRITE AN EMPTY STRING, BUT IF THERE IS
				// A UNIQUE CONSTRAINT ON THE COLUMN
				// THIS CAUSES A PROBLEM SO WE WRITE NULL
				// TODO investigate if we can let the programmer indicate how this should be
				// handled for a given column OR see if we can identify constraints
//				if (_updatedValue==null || _updatedValue.equals("")) {
//					this.updateNull(_columnName);
//				} else {
//					this.updateString(_columnName, _updatedValue);
//				}

				if (_updatedValue==null) {
					if (_allowNull) {
						_rowSet.updateNull(_columnName);
					} else {
						throw new NullPointerException("Null values are not allowed for this field.");
					}
				} else {
					_rowSet.updateString(_columnName, _updatedValue);
				}
				break;

			default:
				LogManager.getLogger().error("Unsupported data type of " + JDBCType.valueOf(columnType).getName() + " for column " + _columnName + ".");
			} // end switch

//		} catch (SQLException se) {
//			LogManager.getLogger().error("SQL Exception for column " + _columnName + ".", se);
//		} catch (NumberFormatException nfe) {
//			LogManager.getLogger().error("Number Format Exception for column " + _columnName + ".", nfe);
//		}

	} // end protected void updateColumnText(String _updatedValue, String _columnName)
		// {

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
	 * Cast each element of the object array to {@code JDBCType}.
	 * Convenience method that invokes {@code castJDBCToJava}.
	 * This is a convenience method.
	 * @param _objects array of objects to cast
	 * @param _jdbcType cast objects to this JDBCType
	 * @return List of cast objects
	 * @throws SQLException This exception wraps a {@code ClassCastException}
	 */
	public static List<Object> castJDBCToJava(final JDBCType _jdbcType, final Object[] _objects) throws SQLException {
		// TODO: get an array object of the correct type.
		final List<Object> data = new ArrayList<>();
		for (final Object val : _objects) {
			data.add(castJDBCToJava(_jdbcType, val));
		}
		return data;
		
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
		Object outputObject = null;
		
		try {
			switch (_jdbcType) {
			case INTEGER:
			case SMALLINT:
			case TINYINT:
				outputObject = (Integer)_object;
				break;
			case BIT:
				outputObject = (Boolean)_object;
				break;
			case BIGINT:
				outputObject = (Long)_object;
				break;
			case FLOAT:
			case DOUBLE:
			case REAL:
				outputObject = (Double)_object;
				break;
			case DECIMAL:
			case NUMERIC:
				outputObject = (BigDecimal)_object;
				break;
			case DATE:
			case TIME:
			case TIMESTAMP:
				// TODO: _WITH_TIMEZONE handling
				// case TIME_WITH_TIMEZONE:
				// case TIMESTAMP_WITH_TIMEZONE:
				outputObject = (java.util.Date)_object;
				break;
			case CHAR:
			case VARCHAR:
			case LONGVARCHAR:
			case NCHAR:
			case NVARCHAR:
			case LONGNVARCHAR:
				outputObject = (String)_object;
				break;
			default:
				outputObject = _object;
				break;
			}
		} catch (ClassCastException ex) {
			throw new SQLException(ex);
		}
		return outputObject;
	}

}
