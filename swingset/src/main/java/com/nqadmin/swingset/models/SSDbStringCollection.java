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
import java.util.List;

import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.datasources.ConvertType.convertToType;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * A Collection can be represented in a database as a string containing
 * the collection elements separated by a special character. This base
 * class handles such a collection.
 * 
 * @since 4.0.0
 */
public class SSDbStringCollection extends SSAbstractCollection {
	/**
	 * A delimiter and its English name.
	 */
	public record Delim(char delim, String name){};
	/**
	 * The ascii UnitSeparator control character to delimits data items in string.
	 */
	public static final Delim US = new Delim('\u001f', "UnitSeparator(0x1f)");
	/**
	 * Comma to delimit.
	 */
	public static final Delim COMMA_SEP = new Delim(',', "Comma(,)");

	private final String separator;
	private final String separatorName;

	/** System Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Create model.
	 * @param jdbcType the jdbcType of the elements in the array.
	 * @param separator the character that separates elements in the string.
	 * @param separatorName human readable name of the separator
	 */
	public SSDbStringCollection(JDBCType jdbcType,
			char separator, String separatorName) {
		super(jdbcType);
		this.separator = String.valueOf(separator);
		this.separatorName = separatorName;
	}

	/**
	 * Create model.
	 * @param jdbcType
	 * @param delim
	 */
	public SSDbStringCollection(JDBCType jdbcType, Delim delim) {
		this(jdbcType, delim.delim(), delim.name);
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
	 * The database string is converted and returned as an array
	 * whose type is determined by collection's JDBCType.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public Object readData(SSComponent comp) throws SQLException {
		String dbstring = comp.getBoundColumnText();
		return toArray(getJDBCType(), dbstring);
	}

	/** {@inheritDoc} */
	@Override
	public void writeData(SSComponent comp, Object _data) throws SQLException {
		if (!_data.getClass().isArray())
			throw new IllegalArgumentException("Must be an array");
		Object[] data = (Object[]) _data; // TODO: Could be any type of array...

		List<String> arr = new ArrayList<>(data.length);
		// Transform the array of object into an array of String representations
		//for(Object object : Arrays.asList(data)) {
		for(Object object : data) {
			String collectionElement = object.toString();
			if(collectionElement.contains(separator))
				throw new SQLException(new IllegalArgumentException(
						sf("Collection element '%s' has a '%s' in it",
								collectionElement, getSeparatorName())));
			arr.add(object.toString());
		}
		// Combine the array of strings into a single String
		String result = String.join(separator, arr);
		// and write it to the database
		comp.setBoundColumnText(result);
	}

	// TODO: use a common parse String to Object
	// TODO: put into RowSetOps?
	// TODO: toObjArray additional JDBC types: TIME/TIMESTAMP/*_WITH_TIMEZONE/...
	private Object toArray(final JDBCType jdbcType, final String dbstring)
			throws SQLException
	{
		if (dbstring == null) {
			return null;
		}
		
		logger.log(DEBUG, () -> "toArray() contents: " + dbstring);
		String[] split = dbstring.split(separator);

		// TODO: fixup the cases
		try {
			switch (jdbcType) {
			case INTEGER, SMALLINT, TINYINT -> {
				int[] data = new int[split.length];
				for (int i = 0; i < split.length; i++)
					data[i] = Integer.parseInt(split[i]);
				return data;
			}
			case BIT, BOOLEAN -> {
				boolean[] data = new boolean[split.length];
				for (int i = 0; i < split.length; i++)
					data[i] = convertToType(split[i], boolean.class);
				return data;
			}
			case BIGINT -> {
				long[] data = new long[split.length];
				for (int i = 0; i < split.length; i++)
					data[i] = Long.parseLong(split[i]);
				return data;
			}
			case REAL -> {
				float[] data = new float[split.length];
				for (int i = 0; i < split.length; i++)
					data[i] = Float.parseFloat(split[i]);
				return data;
			}
			case FLOAT, DOUBLE -> {
				double[] data = new double[split.length];
				for (int i = 0; i < split.length; i++)
					data[i] = Double.parseDouble(split[i]);
				return data;
			}
			case DECIMAL, NUMERIC -> {
				BigDecimal[] data = new BigDecimal[split.length];
				for (int i = 0; i < split.length; i++)
					data[i] = new BigDecimal(split[i]);
				return data;
			}
			case DATE -> {
				Date[] data = new Date[split.length];
				for (int i = 0; i < split.length; i++)
					data[i] = Date.valueOf(split[i]);
				return data;
			}
			case CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR -> {
				return split;
			}
			default -> {
				throw new IllegalArgumentException(sf("String collection of '%s' not handled", jdbcType));
			}
			}
		} catch (IllegalArgumentException ex) {
			throw new SQLException(ex);
		}
	}
}
				