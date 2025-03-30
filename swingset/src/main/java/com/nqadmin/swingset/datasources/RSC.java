/* *****************************************************************************
 * Copyright (C) 2024, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 * ****************************************************************************/
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.datasources;

import java.sql.JDBCType;
import java.sql.SQLException;

import javax.sql.RowSet;

import com.nqadmin.swingset.formatting.SSFormat;
import com.nqadmin.swingset.navigate.RowsModel;

/**
 * This interface is an accessor to a {@linkplain RowSet} and one of its
 * columns; it is extended by {@link com.nqadmin.swingset.utils.SSComponentInterface}.
 * Some components, in particular {@linkplain com.nqadmin.swingset.SSDataGrid},
 * have multiple columns; this
 * interface allows a lightweight object to provide row set access for
 * a specified column. Many
 * methods can declare a {@linkplain RSC} parameter instead of a
 * {@linkplain com.nqadmin.swingset.utils.SSComponentInterface}.
 */
// TODO: getAllowNull() is an issue
// TODO: some of these must carefully be specified to only use a subset of the
//		 methods. The getBoundColumn[Text/Object] could be problematic.
//		They should have a way to check for recursion.
public interface RSC
{
	/**
	 * Construct and return an object representing a RowsModel column.
	 * <p>
	 * TODO: SO FAR ONLY USED WITH/FROM GRID. Issue with getColumnObject undo/redo.
	 * @param rowsModel
	 * @param columnIndex
	 * @return
	 * @throws SSSQLRuntimeException
	 */
	static RSC get(RowsModel rowsModel, int columnIndex) {
		//
		// TODO: accept format, implement in SimpleRSC
		//
		try {
			return new SimpleRSC(rowsModel, columnIndex);
		} catch (SQLException ex) {
			throw new SSSQLRuntimeException(ex);
		}
	}

	/**
	 * Construct and return an object representing a RowSet column.
	 * <p>
	 * TODO: NOT USED
	 * @param rowsModel
	 * @param columnIndex
	 * @return
	 * @throws java.sql.SQLException
	 */
	static RSC getEx(RowsModel rowsModel, int columnIndex) throws SQLException {
		return new SimpleRSC(rowsModel, columnIndex);
	}

	/**
	 * Construct and return an object representing a RowSet column.
	 * <p>
	 * TODO: NOT USED
	 * @param rowsModel
	 * @param columnName 
	 * @return
	 * @throws java.sql.SQLException
	 */
	static RSC getEx(RowsModel rowsModel, String columnName) throws SQLException {
		return new SimpleRSC(rowsModel, columnName);
	}

	/**
	 * @return the RowsModel
	 */
	RowsModel getRowsModel();

	/**
	 * @return the row set
	 */
	RowSet getRowSet();

	/**
	 * @return the column index in the rowset
	 */
	int getBoundColumnIndex();

	/**
	 * @return the column name in the rowset
	 */
	String getBoundColumnName();

	/**
	 * @return The JDBCType of the column in the rowset
	 */
	JDBCType getBoundColumnJDBCType();

	/**
	 * @return column value as a String
	 */
	String getBoundColumnText();
	//Object getBoundColumnObject() throws SQLException;

	/**
	 * Returns an Object of the specified type
	 * representing the value in the bound database column.
	 *
	 * Note a null is never converted into ""; use getBoundColumnText for that.
	 * @param <T> type to return
	 * @param clazz Class of returned type
	 * @return column object
	 */
	<T> T getBoundColumnObject(Class<T> clazz);
	//<T> T getTypedObject() throws SQLException;

	/**
	 * A component may have a display/parse format.
	 * @return The format for this component
	 */
	default SSFormat getSSFormat() { return null; }

	/**
	 * @return Does the column allow null?
	 */
	boolean getAllowNull();

	/**
	 * @return typically column name wrapped in "[]"
	 */
	String getColumnForLog();
}
