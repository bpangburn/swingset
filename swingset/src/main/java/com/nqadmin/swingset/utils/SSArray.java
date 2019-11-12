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

package com.nqadmin.swingset.utils;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * SSArray.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Implementation of SQL array for SSList.
 */

// TODO only reference is in com.nqadmin.swingset.SSList
public class SSArray implements Array {

	/**
	 * object array containing elements of sql array
	 */
	private Object[] data;

	/**
	 * Underlying database type name for array elements
	 */
	private String baseTypeName = "";

	/**
	 * Creates SSArray with the object array and data base type
	 * 
	 * @param _data         object array of SSArray
	 * @param _baseTypeName Array elements database type name
	 */
	public SSArray(Object[] _data, String _baseTypeName) {
		if (_data == null)
			throw new IllegalArgumentException("Parameter should not be null");
		this.data = _data.clone();
		this.baseTypeName = _baseTypeName;
	}

	@Override
	public void free() throws SQLException {
		// do nothing
	}

	/**
	 * returns Object Array contained in SSArray
	 */
	@Override
	public Object getArray() throws SQLException {
		return this.data;
	}

	@Override
	public Object getArray(Map<String, Class<?>> map) throws SQLException {
		return null;
	}

	@Override
	public Object getArray(long index, int count) throws SQLException {
		return null;
	}

	@Override
	public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
		return null;
	}

	@Override
	public int getBaseType() throws SQLException {
		return 0;
	}

	/**
	 * returns the base type name of db array elements
	 * 
	 * @return _baseTypeName data base type name
	 */
	@Override
	public String getBaseTypeName() throws SQLException {
		return this.baseTypeName;
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return null;
	}

	@Override
	public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
		return null;
	}

	@Override
	public ResultSet getResultSet(long index, int count) throws SQLException {
		return null;
	}

	@Override
	public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
		return null;
	}

	/**
	 * Returns a string value with comma separated values. e.g. "{100,200,300}"
	 */
	@Override
	public String toString() {
		String text = "{";
		for (int i = 0; i < this.data.length; ++i) {
			if (i > 0) {
				text += ",";
			}
			text += this.data[i];
		}

		text += "}";
		return text;
	}
}