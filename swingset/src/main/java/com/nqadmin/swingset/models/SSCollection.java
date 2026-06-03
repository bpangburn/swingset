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
 * copyright (C) 2024-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.models;

import java.sql.Array;
import java.sql.JDBCType;
import java.sql.ResultSetMetaData;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.nqadmin.swingset.utils.SSComponent;

import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * Read and write a collection of data items from/to database.
 * All items are the same jdbctype. How the items are stored is
 * independent of this interface. There is no presumption that
 * input/output data is copied; it should not be modified.
 * <p>
 * Some static methods useful for working with collections.
 * 
 * @since 4.0.0
 */
// TODO: <K>
public interface SSCollection {

	/**
	 * @return the type of array elements handled by this model
	 */
	JDBCType getJDBCType();

	/**
	 * Get the data from the comp's RowSet.
	 * 
	 * @param comp has RowSet and column
	 * @return an array, see {@link java.sql.Array#getArray() }
	 * @throws SQLException if a database related error occurs
	 */
	Object readData(SSComponent comp) throws SQLException;

	/**
	 * Put the data from a java object array to the RowSet.
	 *
	 * @param comp has RowSet and column
	 * @param data array of data to write to the database
	 * @throws SQLException if a database related error occurs
	 */
	void writeData(SSComponent comp, Object data) throws SQLException;

	/**
	 * Create an SSCollection for holding elements of the specified type for
	 * read/write of the component's table column.
	 * 
	 * @param comp The component
	 * @param jdbcType element type of the collection
	 * @return A collection that works with the component
	 * @throws SQLException
	 * @throws IllegalArgumentException if the component's column type is not
	 * suitable for saving a collection of the specified jdbcType.
	 */
	public static SSCollection getSuitableDbCollection(SSComponent comp, JDBCType jdbcType) throws SQLException {

		SSCollection dbCollection;
		JDBCType collectionType = jdbcType;
		// there's also: DatabaseMetaData.getColumns() - TYPE_NAME is a column
		ResultSetMetaData md = comp.getRowSet().getMetaData();
		int columnTyp = md.getColumnType(comp.getColumnIndex());
		JDBCType columnType = JDBCType.valueOf(columnTyp);
		dbCollection = switch (columnType) {
		case ARRAY -> {
			// May not be any rows, so only use metadata to determine elemtype
			// if (Utils.hasActiveRow(this)) {
			// 	Array array = this.getColumnArray();
			// 	elemtype = JDBCType.valueOf(array.getBaseType());
			// }
			
			// First word of column type is element type, eg "INTEGER ARRAY".
			String typnam = md.getColumnTypeName(comp.getColumnIndex());
			JDBCType elemtype = JDBCType.valueOf(typnam.split(" ")[0]);
			if (collectionType != elemtype) {
				String s = sf("collection type '%s' != ARRAY type '%s'", jdbcType, elemtype);
				throw new IllegalArgumentException(s);
			}
			yield new SSDbArray(elemtype);
		}
		case CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR -> {
			// TODO: have plugin specify default Delim
			yield new SSDbStringCollection(collectionType, SSDbStringCollection.COMMA_SEP);
		}
		default -> {
			String s = sf("Column type '%s' not handled for SSCollection.", columnType);
			throw new IllegalArgumentException(s);
		}
		};
		return dbCollection;
	}

	/**
	 * Converts a java array to list. Turns array of primitives into
	 * array of Objects.
	 *
	 * @param array a java array as described by {@link Array#getArray() }
	 * @return List with elements corresponding to input array's elements.
	 * @throws java.sql.SQLDataException
	 */
	// TODO: put this into ConvertType
	public static List<?> convertArrayToObjectList(Object array) throws SQLDataException {
		Objects.requireNonNull(array);
		if (!array.getClass().isArray())
			throw new IllegalArgumentException("Must be an array");
		// logger.log(DEBUG, () -> "SSList.toObjArray() contents: " + array);
		
		// TODO: Switch on type of array? dbArray.getClass().getComponentType()
		//       java.lang.reflect.Array.get(Object array, int index)
		int len = java.lang.reflect.Array.getLength(array);
		try {
			if (array.getClass().getComponentType().isPrimitive()) {
				Object[] objects = new Object[len];
				for (int i = 0; i < len; i++)
					objects[i] = java.lang.reflect.Array.get(array, i);
				return Arrays.asList(objects);
			} else {
				return (List<?>) Arrays.asList((Object[])array);
			}
		} catch (final ClassCastException cce) {
			// logger.log(ERROR, "Class Cast Exception.", cce);
			throw new SQLDataException(cce);
		}
	}
}
