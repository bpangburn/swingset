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

import java.sql.SQLException;

import javax.sql.RowSet;

// SSRowSet.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Previously this was a custom Interface that was a subset of RowSet. Now we
 * will try to support the full RowSet interface, but still need to accommodate
 * code where SSRowSet was referenced as a type.
 * 
 * @deprecated Starting in 4.0.0+ use {@link com.nqadmin.swingset.datasources.RowSetOps} instead.
 */
@Deprecated
public interface SSRowSet extends RowSet {

	/**
	 * Returns the number of columns in the underlying ResultSet object
	 *
	 * @return the number of columns
	 * @throws SQLException - if a database access error occurs
	 */
	public default int getColumnCount() throws SQLException {
		return RowSetOps.getColumnCount(this);
		//return getMetaData().getColumnCount();
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
		return RowSetOps.getColumnIndex(this, _columnName);
		//return findColumn(_columnName);
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
		return RowSetOps.getColumnName(this, _columnIndex);
		//return getMetaData().getColumnName(_columnIndex);
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
		return RowSetOps.getColumnText(this, _columnName);
//		String value = null;
//
//		try {
//			// IF THERE ARE NO COLUMNS IN THE ROWSET, RETURN NULL
//			if (getColumnCount()==0) {
//				LogManager.getLogger().debug("Call to getColumnText() for column=" + _columnName + ", but the RowSet has no columns.");
//				return null;
//			}
//
//			final int columnType = getColumnType(_columnName);
//		
//			LogManager.getLogger().debug("getObject() for " + _columnName + " of type {} returns {}.", () ->  JDBCType.valueOf(columnType).getName(), () ->  {
//				try {
//					return getObject(_columnName);
//				} catch (SQLException e) {
//					return "*** getObject() threw an SQL Exception ***";
//				}
//			});
//			
//			// RETURN NULL IF THE OBJECT REQUESTED IS NULL
//			if (getObject(_columnName) == null) {
//				return null;
//			}
//
//			// BASED ON THE COLUMN DATA TYPE THE CORRESPONDING FUNCTION
//			// IS CALLED TO GET THE VALUE IN THE COLUMN
//			switch (columnType) {
//			case Types.INTEGER:
//			case Types.SMALLINT:
//			case Types.TINYINT:
//				value = String.valueOf(this.getInt(_columnName));
//				break;
//
//			case Types.BIGINT:
//				value = String.valueOf(this.getLong(_columnName));
//				break;
//
//			case Types.FLOAT:
//				value = String.valueOf(this.getFloat(_columnName));
//				break;
//
//			case Types.DOUBLE:
//			case Types.NUMERIC:
//			case Types.DECIMAL:
//				value = String.valueOf(this.getDouble(_columnName));
//				break;
//
//			case Types.BOOLEAN:
//			case Types.BIT:
//				value = String.valueOf(this.getBoolean(_columnName));
//				break;
//
//			case Types.DATE:
//			case Types.TIMESTAMP:
//				final Date date = this.getDate(_columnName);
//				if (date == null) {
//					value = "";
//				} else {
//					final GregorianCalendar calendar = new GregorianCalendar();
//					calendar.setTime(date);
//					value = "";
//					if ((calendar.get(Calendar.MONTH) + 1) < 10) {
//						value = "0";
//					}
//					value = value + (calendar.get(Calendar.MONTH) + 1) + "/";
//
//					if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
//						value = value + "0";
//					}
//					value = value + calendar.get(Calendar.DAY_OF_MONTH) + "/";
//					value = value + calendar.get(Calendar.YEAR);
//					// value = String.valueOf(sSRowSet.getDate(columnName));
//				}
//				break;
//
//			case Types.TIME:
//				final Time time = this.getTime(_columnName);
//				if (time == null) {
//					value = "";
//				} else {
//					value=time.toString();
//				}
//				break;
//
//			case Types.CHAR:
//			case Types.VARCHAR:
//			case Types.LONGVARCHAR:
//				final String str = this.getString(_columnName);
//				if (str == null) {
//					value = "";
//				} else {
//					value = String.valueOf(str);
//				}
//				break;
//
//			default:
//				LogManager.getLogger().error("Unsupported data type of " + JDBCType.valueOf(columnType).getName() + " for column " + _columnName + ".");
//			} // end switch
//
//		} catch (final SQLException se) {
//			LogManager.getLogger().error("SQL Exception for column " + _columnName + ".", se);
//		}
//
//		return value;

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
		return RowSetOps.getColumnType(this, _columnIndex);
		//return getMetaData().getColumnType(_columnIndex);
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
		return RowSetOps.getColumnType(this, _columnName);
		//return getMetaData().getColumnType(getColumnIndex(_columnName));
	}

	/**
	 * Method used by SwingSet component listeners to update the underlying
	 * SSRowSet.
	 * <p>
	 * When the user changes/edits the SwingSet column this method propagates the
	 * change to the RowSet. A separate call is required to flush/commit the change
	 * to the database.
	 *
	 * @param _updatedValue string to be type-converted as needed and updated in
	 *                      underlying RowSet column
	 * @param _columnName   name of the database column
	 * @param _allowNull 	indicates if Component and underlying column can contain null values
	 * @throws NullPointerException thrown if null is not allowed
	 * @throws SQLException  thrown if a database error is encountered
	 * @throws NumberFormatException thrown if unable to parse a string to number format
	 */
	public default void updateColumnText(final String _updatedValue, final String _columnName, final boolean _allowNull) throws NullPointerException, SQLException, NumberFormatException {
		RowSetOps.updateColumnText(this, _updatedValue, _columnName, _allowNull);
//			// 2020-09-11_BP: Probably not a good idea to trim here as it may be desirable to have padding for some strings
//			// Also, it's coded wrong.
//			// Should be:
//			//	if (_updatedValue!=null) _updatedValue = _updatedValue.trim();
//			// not:
//			// 	if (_updatedValue!=null) _updatedValue.trim();
//
//
//			LogManager.getLogger().debug("[" + _columnName + "]. Update to: " + _updatedValue + ". Allow null? [" + _allowNull + "].");
//
//			final int columnType = getColumnType(_columnName);
//
//			switch (columnType) {
//			// FOR NON-TEXT-BASED DATABASE COLUMNS, WRITE NULL INSTEAD OF AN EMPTY STRING
//
//			case Types.INTEGER:
//			case Types.SMALLINT:
//			case Types.TINYINT:
//				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//				if ((_updatedValue==null) || _updatedValue.equals("")) {
//					if (_allowNull) {
//						this.updateNull(_columnName);
//					} else {
//						throw new NullPointerException("Null values are not allowed for this field.");
//					}
//				} else {
//					final int intValue = Integer.parseInt(_updatedValue);
//					this.updateInt(_columnName, intValue);
//				}
//				break;
//
//			case Types.BIGINT:
//				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//				if ((_updatedValue==null) || _updatedValue.equals("")) {
//					if (_allowNull) {
//						this.updateNull(_columnName);
//					} else {
//						throw new NullPointerException("Null values are not allowed for this field.");
//					}
//				} else {
//					final long longValue = Long.parseLong(_updatedValue);
//					this.updateLong(_columnName, longValue);
//				}
//				break;
//
//			case Types.FLOAT:
//				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//				if ((_updatedValue==null) || _updatedValue.equals("")) {
//					if (_allowNull) {
//						this.updateNull(_columnName);
//					} else {
//						throw new NullPointerException("Null values are not allowed for this field.");
//					}
//				} else {
//					final float floatValue = Float.parseFloat(_updatedValue);
//					this.updateFloat(_columnName, floatValue);
//				}
//				break;
//
//			case Types.DOUBLE:
//			case Types.NUMERIC:
//			case Types.DECIMAL:
//				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//				if ((_updatedValue==null) || _updatedValue.equals("")) {
//					if (_allowNull) {
//						this.updateNull(_columnName);
//					} else {
//						throw new NullPointerException("Null values are not allowed for this field.");
//					}
//				} else {
//					final double doubleValue = Double.parseDouble(_updatedValue);
//					this.updateDouble(_columnName, doubleValue);
//				}
//				break;
//
//			case Types.BOOLEAN:
//			case Types.BIT:
//				if ((_updatedValue==null) || _updatedValue.equals("")) {
//					if (_allowNull) {
//						this.updateNull(_columnName);
//					} else {
//						throw new NullPointerException("Null values are not allowed for this field.");
//					}
//				} else {
//					// CONVERT THE GIVEN STRING TO BOOLEAN TYPE
//					final boolean boolValue = Boolean.valueOf(_updatedValue);
//					this.updateBoolean(_columnName, boolValue);
//				}
//				break;
//
//			case Types.DATE:
//				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//				if ((_updatedValue==null) || _updatedValue.equals("")) {
//					if (_allowNull) {
//						this.updateNull(_columnName);
//					} else {
//						throw new NullPointerException("Null values are not allowed for this field.");
//					}
//				} else if (_updatedValue.length() == 10) {
//					this.updateDate(_columnName, SSCommon.getSQLDate(_updatedValue));
//				} else {
//					// do nothing
//				}
//				break;
//
//			case Types.TIME:
//				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//				if ((_updatedValue==null) || _updatedValue.equals("")) {
//					if (_allowNull) {
//						this.updateNull(_columnName);
//					} else {
//						throw new NullPointerException("Null values are not allowed for this field.");
//					}
//				} else {
//					this.updateTime(_columnName, java.sql.Time.valueOf(_updatedValue));
//				}
//				break;
//
//			case Types.TIMESTAMP:
//				// IF TEXT IS EMPTY THEN UPDATE COLUMN TO NULL
//				if ((_updatedValue==null) || _updatedValue.equals("")) {
//					if (_allowNull) {
//						this.updateNull(_columnName);
//					} else {
//						throw new NullPointerException("Null values are not allowed for this field.");
//					}
//				} else if (_updatedValue.length() == 10) {
//					this.updateTimestamp(_columnName, new Timestamp(SSCommon.getSQLDate(_updatedValue).getTime()));
//				} else {
//					// do nothing
//				}
//				break;
//
//			case Types.CHAR:
//			case Types.VARCHAR:
//			case Types.LONGVARCHAR:
//				// FOR TEXT-BASED DATABASE COLUMNS WE CAN WRITE AN EMPTY STRING, BUT IF THERE IS
//				// A UNIQUE CONSTRAINT ON THE COLUMN
//				// THIS CAUSES A PROBLEM SO WE WRITE NULL
//				// TODO investigate if we can let the programmer indicate how this should be
//				// handled for a given column OR see if we can identify constraints
////				if (_updatedValue==null || _updatedValue.equals("")) {
////					this.updateNull(_columnName);
////				} else {
////					this.updateString(_columnName, _updatedValue);
////				}
//
//				if (_updatedValue==null) {
//					if (_allowNull) {
//						this.updateNull(_columnName);
//					} else {
//						throw new NullPointerException("Null values are not allowed for this field.");
//					}
//				} else {
//					this.updateString(_columnName, _updatedValue);
//				}
//				break;
//
//			default:
//				LogManager.getLogger().error("Unsupported data type of " + JDBCType.valueOf(columnType).getName() + " for column " + _columnName + ".");
//			} // end switch

	} // end protected void updateColumnText(String _updatedValue, String _columnName)

}