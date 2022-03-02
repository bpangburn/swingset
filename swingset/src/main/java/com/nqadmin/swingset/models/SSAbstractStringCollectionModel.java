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
package com.nqadmin.swingset.models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.RowSet;

import com.nqadmin.swingset.utils.SSUtils;

// SSAbstractStringCollectionModel.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * A Collection can be represented in a database as a string containing
 * the collection elements separated by a special character. This base
 * class handles such a collection.
 * 
 * @since 4.0.0
 */
public abstract class SSAbstractStringCollectionModel extends SSAbstractCollectionModel {
	private final String separator;
	private final String separatorName;

	/**
	 * Create model.
	 * @param _jdbcType the jdbcType of the elements in the array.
	 * @param _separator the character that separates elements in the string.
	 * @param _separatorName human readable name of the separator
	 */
	public SSAbstractStringCollectionModel(JDBCType _jdbcType,
			char _separator, String _separatorName) {
		super(_jdbcType);
		separator = String.valueOf(_separator);
		separatorName = _separatorName;
	}
	
	/**
	 * The separator used to delimit collection elements in the string
	 * @return the separator
	 */
	public char getSeparator() {
		return separator.charAt(0);
	}
	
	/**
	 * The name of the separator.May be used in messages.
	 * @return human readable name of the separator
	 */
	public String getSeparatorName() {
		return separatorName;
	}
	
	/**
	 * Log4j Logger for component
	 */
	//private static Logger logger = LogManager.getLogger();

	/** {@inheritDoc} */
	@Override
	public Object[] readData(RowSet _rowSet, String _columnName) throws SQLException {
		String dbstring = _rowSet.getString(_columnName);
		return toObjArray(getJDBCType(), dbstring);
	}

	/** {@inheritDoc} */
	@Override
	public void writeData(RowSet _rowSet, String _columnName, Object[] _data) throws SQLException {
		List<String> arr = new ArrayList<>(_data.length);
		// Transform the array of object into an array of String representations
		for(Object object : Arrays.asList(_data)) {
			String collectionElement = object.toString();
			if(collectionElement.contains(separator))
				throw new SQLException(new IllegalArgumentException(
						String.format("SET element '%s' has a '%s' in it",
								collectionElement, getSeparatorName())));
			arr.add(object.toString());
		}
		// Combine the array of strings into a single String
		String result = String.join(separator, arr);
		// and write it to the database
		_rowSet.updateString(_columnName, result);
	}

	// TODO: use a common parse String to Object
	private Object[] toObjArray(final JDBCType _jdbcType, final String _dbstring) throws SQLException {
		if (_dbstring == null) {
			return null;
		}
		
		SSUtils.getLogger().debug("SSList.toObjArray() contents: " + _dbstring);
		List<Object> data = new ArrayList<>();
		List<String> dbSplit= Arrays.asList(_dbstring.split(separator));
		try {
			for(String s : dbSplit) {
				switch (_jdbcType) {
				case INTEGER:
				case SMALLINT:
				case TINYINT:
					data.add(Integer.parseInt(s));
					break;
				case BIGINT:
					data.add(Long.parseLong(s));
					break;
				case FLOAT:
				case DOUBLE:
				case REAL:
					data.add(Double.parseDouble(s));
					break;
				case DECIMAL:
				case NUMERIC:
					data.add(new BigDecimal(s));
					break;
				case DATE:
					data.add(Date.valueOf(s));
					break;
				case CHAR:
				case VARCHAR:
				case LONGVARCHAR:
					data.add(s);
					break;
				default:
					// TODO: exception later?
					data.add(s);
					break;
				}
			}
		} catch (IllegalArgumentException ex) {
			throw new SQLException(ex);
		}
		return data.toArray();
	}
}
