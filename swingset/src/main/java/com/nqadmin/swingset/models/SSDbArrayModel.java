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
package com.nqadmin.swingset.models;

import com.nqadmin.swingset.utils.SSArray;
import java.sql.Array;
import java.sql.JDBCType;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static com.nqadmin.swingset.datasources.RowSetOps.*;

// SSDbArrayModel.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Implementation of SSCollectionModel as an array that uses a database
 * {@code JDBCType.ARRAY} for storage. The order of items is preserved by
 * {@link #readData(javax.sql.RowSet, java.lang.String) readData} and
 * {@link #writeData(javax.sql.RowSet, java.lang.String, java.lang.Object[])
 * writeData}.
 * 
 * @since 4.0.0
 */
public class SSDbArrayModel extends SSAbstractCollectionModel {

	/**
	 * Create SSDbArrayModel
	 * @param _jdbcType type of elements in database array
	 */
	public SSDbArrayModel(final JDBCType _jdbcType) {
		super(_jdbcType);
	}

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/** {@inheritDoc } */
	@Override
	public Object[] readData(final RowSet _rowSet, final String _columnName) throws SQLException {
		return toObjList(getJDBCType(), _rowSet.getArray(_columnName)).toArray();
	}

	/** {@inheritDoc } */
	@Override
	public void writeData(final RowSet _rowSet, final String _columnName, final Object[] _data) throws SQLException {
		SSArray array = new SSArray(_data, getJDBCType());
		_rowSet.updateArray(_columnName, array);
	}

	/**
	 * Converts SQL array to object array
	 *
	 * @param _jdbcType type of objects in array
	 * @param _array SQL array
	 * @return Object array
	 * @throws SQLException SQLException
	 */
	private static List<Object> toObjList(final JDBCType _jdbcType, final Array _array) throws SQLException {
		if (_array == null) {
			return null;
		}
		
		logger.debug("SSList.toObjArray() contents: " + _array);
		
		Object dbArray = _array.getArray();
		
		final List<Object> data;
		
		try {
			if (dbArray instanceof Object[]) {
				data = castJDBCToJava(_jdbcType, (Object[])dbArray);
			} else {
				data = new ArrayList<>();
				// Handle array of primitives
				switch (_jdbcType) {
					case INTEGER:
					case SMALLINT:
					case TINYINT:
						for (final int num : (int[]) dbArray) {
							data.add(Integer.valueOf(num));
						}
						break;
					case BIT:
						for (final boolean bit : (boolean[]) dbArray) {
							data.add(Boolean.valueOf(bit));
						}
						break;
					case BIGINT:
						for (final long num : (long[]) dbArray) {
							data.add(Long.valueOf(num));
						}
						break;
					case FLOAT:
					case DOUBLE:
					case REAL:
						for (final double num : (double[]) dbArray) {
							data.add(Double.valueOf(num));
						}
						break;
					default:
						logger.error("DataType: " + _array.getBaseTypeName() + " not supported and unable to convert to generic object.");
						throw new SQLDataException("Unknown primitive array type");
				}
			}
		} catch (final ClassCastException cce) {
			logger.error("Class Cast Exception.", cce);
			throw new SQLDataException(cce);
		}
		return data;
	}
	
}
