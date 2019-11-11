/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

import java.sql.SQLException;

import javax.sql.RowSet;

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
	 * Returns the number of columns in the underlying ResultSet object
	 * 
	 * @return the number of columns
	 * @throws SQLException - if a database access error occurs
	 */
	public default int getColumnCount() throws SQLException {
		return this.getMetaData().getColumnCount();
	}

	/**
	 * Returns the column name for the column index provided
	 * 
	 * @param columnIndex - the column index where the first column is 1, second
	 *                    column is 2, etc.
	 * @return the column name of the given column index
	 * @throws SQLException - if a database access error occurs
	 */
	public default String getColumnName(int columnIndex) throws SQLException {
		return this.getMetaData().getColumnName(columnIndex);
	}

	/**
	 * Get the designated column's index
	 * 
	 * @param columnName - name of the column
	 * @return returns the corresponding column index (starting from 1)
	 * @throws SQLException - if a database access error occurs
	 */
	public default int getColumnIndex(String columnName) throws SQLException {
		return this.findColumn(columnName);
	}

	/**
	 * Retrieves an int corresponding to the designated column's type based on the
	 * column name
	 * 
	 * @see "https://docs.oracle.com/javase/7/docs/api/java/sql/Types.html"
	 * 
	 * @param columnName - name of the column
	 * @return SQL type from java.sql.Types
	 * @throws SQLException - if a database access error occurs
	 */
	public default int getColumnType(String columnName) throws SQLException {
		return this.getMetaData().getColumnType(getColumnIndex(columnName));
	}

	/**
	 * Retrieves an int corresponding to the designated column's type based on the
	 * column index (starting from 1)
	 * 
	 * @see "https://docs.oracle.com/javase/7/docs/api/java/sql/Types.html"
	 *
	 * @param columnIndex - the column index where the first column is 1, second
	 *                    column is 2, etc.
	 * @return SQL type from java.sql.Types
	 * @throws SQLException - if a database access error occurs
	 */
	public default int getColumnType(int columnIndex) throws SQLException {
		return this.getMetaData().getColumnType(columnIndex);
	}

}