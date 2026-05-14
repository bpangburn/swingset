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

import java.lang.System.Logger;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

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

	/** System Logger for component */
	private static final Logger logger = SSUtils.getLogger();

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

	/** {@inheritDoc} */
	@Override
	public Object[] readData(SSComponent comp) throws SQLException {
		String dbstring = RowSetOps.getColumnText(comp);
		return toObjArray(getJDBCType(), dbstring);
	}

	/** {@inheritDoc} */
	@Override
	public void writeData(SSComponent comp, Object[] _data) throws SQLException {
		List<String> arr = new ArrayList<>(_data.length);
		// Transform the array of object into an array of String representations
		for(Object object : Arrays.asList(_data)) {
			String collectionElement = object.toString();
			if(collectionElement.contains(separator))
				throw new SQLException(new IllegalArgumentException(
						sf("SET element '%s' has a '%s' in it",
								collectionElement, getSeparatorName())));
			arr.add(object.toString());
		}
		// Combine the array of strings into a single String
		String result = String.join(separator, arr);
		// and write it to the database
		RowSetOps.updateColumnText(comp, result);
	}

	// TODO: use a common parse String to Object
	// TODO: put into RowSetOps?
	// TODO: toObjArray additional JDBC types: TIME/TIMESTAMP/*_WITH_TIMEZONE/...
	private Object[] toObjArray(final JDBCType _jdbcType, final String _dbstring)
			throws SQLException
	{
		if (_dbstring == null) {
			return null;
		}
		
		logger.log(DEBUG, "SSList.toObjArray() contents: " + _dbstring);
		List<Object> data = new ArrayList<>();
		List<String> dbSplit= Arrays.asList(_dbstring.split(separator));

		// TODO: fixup the cases
		try {
			for(String s : dbSplit) {
				switch (_jdbcType) {
				case INTEGER, SMALLINT, TINYINT
						-> data.add(Integer.valueOf(s));
				case BIGINT
						-> data.add(Long.valueOf(s));
				case REAL
						-> data.add(Float.valueOf(s));
				case FLOAT, DOUBLE
						-> data.add(Double.valueOf(s));
				case DECIMAL, NUMERIC
						-> data.add(new BigDecimal(s));
				case DATE
						-> data.add(Date.valueOf(s));
				case CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR
						-> data.add(s);
				default -> // TODO: toObjArray exception later?
					data.add(s);
				}
			}
		} catch (IllegalArgumentException ex) {
			throw new SQLException(ex);
		}
		return data.toArray();
	}
}
