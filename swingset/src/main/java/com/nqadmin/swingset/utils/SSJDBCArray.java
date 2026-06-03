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
package com.nqadmin.swingset.utils;

import java.sql.Array;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of SQL array for SSList; most methods unsupported.
 * Array data is not copied/safe.
 */

public class SSJDBCArray implements Array
{
	// TODO May be able to eliminate. Only reference is in com.nqadmin.swingset.SSList.

	/**
	 * Underlying database type name for array elements
	 */
	final private JDBCType baseType;

	/**
	 * an array containing elements for an sql array
	 */
	private Object data;

	/**
	 * Creates SSArray with the object array and data base type.
	 * <em>The data is not copied</em>; the caller should not modify the array.
	 * 
	 * @param data     object array of SSArray
	 * @param baseType Array elements database type
	 */
	public SSJDBCArray(final Object data, final JDBCType baseType) {
		Objects.requireNonNull(data);
		if (!data.getClass().isArray())
			throw new IllegalArgumentException("Must be an array");
		this.data = data;
		this.baseType = baseType;
	}

	/** {@inheritDoc } */
	@Override
	public void free() throws SQLException {
		data = null;
	}

	/** {@inheritDoc } */
	@Override
	public Object getArray() throws SQLException {
		return data;
	}

	/**
	 * throws UnsupportedOperationException
	 * {@inheritDoc }
	 */
	@Override
	public Object getArray(final long index, final int count) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/**
	 * throws UnsupportedOperationException
	 * {@inheritDoc }
	 */
	@Override
	public Object getArray(final long index, final int count, final Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/**
	 * throws UnsupportedOperationException
	 * {@inheritDoc }
	 */
	@Override
	public Object getArray(final Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc } */
	@Override
	public int getBaseType() throws SQLException {
		return baseType.getVendorTypeNumber();
	}

	/** {@inheritDoc } */
	@Override
	public String getBaseTypeName() throws SQLException {
		return baseType.getName();
	}

	/**
	 * throws UnsupportedOperationException
	 * {@inheritDoc }
	 */
	@Override
	public ResultSet getResultSet() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/**
	 * throws UnsupportedOperationException
	 * {@inheritDoc }
	 */
	@Override
	public ResultSet getResultSet(final long index, final int count) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/**
	 * throws UnsupportedOperationException
	 * {@inheritDoc }
	 */
	@Override
	public ResultSet getResultSet(final long index, final int count, final Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/**
	 * throws UnsupportedOperationException
	 * {@inheritDoc }
	 */
	@Override
	public ResultSet getResultSet(final Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a string value with comma separated values. e.g. "{100,200,300}"
	 * @return 
	 */
	@Override
	public String toString() {
		String text = "SSArray " + Arrays.asList(data);
		return text;
	}
}
