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
 ******************************************************************************/

package com.nqadmin.swingset.datasources;

import java.sql.Date;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.sql.RowSet;
import javax.sql.RowSetListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.utils.SSCommon;

/**
 * SSRowSet.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Previously this was a custom Interface that was a subset of RowSet. Now we
 * will try to support the full RowSet interface, but still need to accommodate
 * code where SSRowSet was referenced as a type.
 */
public interface SSRowSet extends RowSet {
	
	/**
	 * Log4j2 Logger - can't be private in an interface
	 */
    static final Logger logger = LogManager.getLogger();

	/**
	 * Wrapper/convenience method for SwingSet method naming consistency.
	 * 
	 * @param _rowSetListener RowSetListener to add to current SSRowSet
	 */
	public default void addSSRowSetListener(RowSetListener _rowSetListener) {
		addRowSetListener(_rowSetListener);
	}

	/**
	 * Returns the number of columns in the underlying ResultSet object
	 * 
	 * @return the number of columns
	 * @throws SQLException - if a database access error occurs
	 */
	public default int getColumnCount() throws SQLException {
		return this.getMetaData().getColumnCount();
	}

	/**
	 * Get the designated column's index
	 * 
	 * @param _columnName - name of the column
	 * 
	 * @return returns the corresponding column index (starting from 1)
	 * 
	 * @throws SQLException - if a database access error occurs
	 */
	public default int getColumnIndex(final String _columnName) throws SQLException {
		return this.findColumn(_columnName);
	}

	/**
	 * Returns the column name for the column index provided
	 * 
	 * @param _columnIndex - the column index where the first column is 1, second
	 *                     column is 2, etc.
	 * @return the column name of the given column index
	 * 
	 * @throws SQLException - if a database access error occurs
	 */
	public default String getColumnName(final int _columnIndex) throws SQLException {
		return this.getMetaData().getColumnName(_columnIndex);
	}

	/**
	 * Method used by ssRowSet listeners to get the new text when the SSRowSet
	 * events are triggered.
	 * 
	 * @param _columnName - name of database column to retrieve
	 * 
	 * @return text representation of data in specified column
	 */
	public default String getColumnText(final String _columnName) {
		String value = null;
		try {
			// IF THE COLUMN IS NULL SO RETURN NULL
			if (this.getObject(_columnName) == null) {
				return null;
			}
			
			int columnType = getColumnType(_columnName);

			// BASED ON THE COLUMN DATA TYPE THE CORRESPONDING FUNCTION
			// IS CALLED TO GET THE VALUE IN THE COLUMN
			switch (columnType) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				value = String.valueOf(this.getInt(_columnName));
				break;

			case Types.BIGINT:
				value = String.valueOf(this.getLong(_columnName));
				break;

			case Types.FLOAT:
				value = String.valueOf(this.getFloat(_columnName));
				break;

			case Types.DOUBLE:
			case Types.NUMERIC:
				value = String.valueOf(this.getDouble(_columnName));
				break;

			case Types.BOOLEAN:
			case Types.BIT:
				value = String.valueOf(this.getBoolean(_columnName));
				break;

			case Types.DATE:
			case Types.TIMESTAMP:
				Date date = this.getDate(_columnName);
				if (date == null) {
					value = "";
				} else {
					GregorianCalendar calendar = new GregorianCalendar();
					calendar.setTime(date);
					value = "";
					if (calendar.get(Calendar.MONTH) + 1 < 10) {
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

			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				String str = this.getString(_columnName);
				if (str == null) {
					value = "";
				} else {
					value = String.valueOf(str);
				}
				break;

			default:
				logger.error("Unsupported data type of " + JDBCType.valueOf(columnType).getName() + " for column " + _columnName + ".");
			} // end switch

		} catch (SQLException se) {
			logger.error("SQL Exception for column " + _columnName + ".", se);
		}

		return value;

	} // end protected String getColumnText(String _columnName) {

	/**
	 * Retrieves an integer corresponding to the designated column's type based on
	 * the column index (starting from 1)
	 * 
	 * @see "https://docs.oracle.com/javase/7/docs/api/java/sql/Types.html"
	 *
	 * @param _columnIndex - the column index where the first column is 1, second
	 *                     column is 2, etc.
	 * @return SQL type from java.sql.Types
	 * 
	 * @throws SQLException - if a database access error occurs
	 */
	public default int getColumnType(final int _columnIndex) throws SQLException {
		return this.getMetaData().getColumnType(_columnIndex);
	}

	/**
	 * Retrieves an int corresponding to the designated column's type based on the
	 * column name
	 * 
	 * @see "https://docs.oracle.com/javase/7/docs/api/java/sql/Types.html"
	 * 
	 * @param _columnName - name of the column
	 * 
	 * @return SQL type from java.sql.Types
	 * 
	 * @throws SQLException - if a database access error occurs
	 */
	public default int getColumnType(final String _columnName) throws SQLException {
		return this.getMetaData().getColumnType(getColumnIndex(_columnName));
	}

	/**
	 * Wrapper/convenience method for SwingSet method naming consistency.
	 * 
	 * @param _rowSetListener RowSetListener to remove from current SSRowSet
	 */
	public default void removeSSRowSetListener(RowSetListener _rowSetListener) {
		removeRowSetListener(_rowSetListener);
	}

	/**
	 * Method used by SwingSet component listeners to update the underlying
	 * SSRowSet.
	 * 
	 * When the user changes/edits the SwingSet column this method propagates the
	 * change to the RowSet. A separate call is required to flush/commit the change
	 * to the database.
	 * 
	 * @param _updatedValue string to be type-converted as needed and updated in
	 *                      underlying RowSet column
	 * @param _columnName   name of the database column
	 * @param _allowNull 	indicates if Component and underlying column can contain null values
	 */
	public default void updateColumnText(String _updatedValue, String _columnName, boolean _allowNull) {

		try {
			
			// TODO Add proper support for null vs "" based on _allowNull. For Char types all "" are currently forced to null, 
//			if (!_allowNull && _updatedValue==null) {
//				_updatedValue = "";
//			}
			
			// TODO Convert this code to use Java 8 JDBCType enum
			
			if (_updatedValue!=null) _updatedValue.trim();
			
			logger.debug("[" + _columnName + "]. Update to: " + _updatedValue + ". Allow null? " + _allowNull);
					
			int columnType = getColumnType(_columnName);

			switch (columnType) {
			// FOR NON-TEXT-BASED DATABASE COLUMNS, WRITE NULL INSTEAD OF AN EMPTY STRING

			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if (_updatedValue==null || _updatedValue.equals("")) {
					this.updateNull(_columnName);
				} else {
					int intValue = Integer.parseInt(_updatedValue);
					this.updateInt(_columnName, intValue);
				}
				break;

			case Types.BIGINT:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if (_updatedValue==null || _updatedValue.equals("")) {
					this.updateNull(_columnName);
				} else {
					long longValue = Long.parseLong(_updatedValue);
					this.updateLong(_columnName, longValue);
				}
				break;

			case Types.FLOAT:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if (_updatedValue==null || _updatedValue.equals("")) {
					this.updateNull(_columnName);
				} else {
					float floatValue = Float.parseFloat(_updatedValue);
					this.updateFloat(_columnName, floatValue);
				}
				break;

			case Types.DOUBLE:
			case Types.NUMERIC:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if (_updatedValue==null || _updatedValue.equals("")) {
					this.updateNull(_columnName);
				} else {
					double doubleValue = Double.parseDouble(_updatedValue);
					this.updateDouble(_columnName, doubleValue);
				}
				break;

			case Types.BOOLEAN:
			case Types.BIT:
				if (_updatedValue==null || _updatedValue.equals("")) {
					this.updateNull(_columnName);
				} else {
					// CONVERT THE GIVEN STRING TO BOOLEAN TYPE
					boolean boolValue = Boolean.valueOf(_updatedValue).booleanValue();
					this.updateBoolean(_columnName, boolValue);
				}
				break;

			case Types.DATE:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if (_updatedValue==null || _updatedValue.equals("")) {
					this.updateNull(_columnName);
				} else if (_updatedValue.length() == 10) {
					this.updateDate(_columnName, SSCommon.getSQLDate(_updatedValue));
				} else {
					// do nothing
				}
				break;
			case Types.TIMESTAMP:
				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
				if (_updatedValue==null || _updatedValue.equals("")) {
					this.updateNull(_columnName);
				} else if (_updatedValue.length() == 10) {
					this.updateTimestamp(_columnName, new Timestamp(SSCommon.getSQLDate(_updatedValue).getTime()));
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
				if (_updatedValue==null || _updatedValue.equals("")) {
					this.updateNull(_columnName);
				} else {
					this.updateString(_columnName, _updatedValue);
				}

				break;

			default:
				logger.error("Unsupported data type of " + JDBCType.valueOf(columnType).getName() + " for column " + _columnName + ".");
			} // end switch

		} catch (SQLException se) {
			logger.error("SQL Exception for column " + _columnName + ".", se);
		} catch (NumberFormatException nfe) {
			logger.error("Number Format Exception for column " + _columnName + ".", nfe);
		}

	} // end protected void updateColumnText(String _updatedValue, String _columnName)
		// {

}