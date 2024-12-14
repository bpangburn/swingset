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

import java.lang.System.Logger;
import java.sql.JDBCType;
import java.sql.SQLException;

import javax.sql.RowSet;

import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.datasources.ConvertType.findJavaTypeClass;
import static java.lang.System.Logger.Level.*;

/**
 * Row set column based only a RowSet and column.
 * <p>
 * TODO: SO FAR ONLY USED WITH/FROM GRID. Issue with getColumnObject undo/redo.
 */
//
// TODO: SimpleRSC exceptions
// During contruction both name/index are verified.
// Any SQL exceptions beyond that are probably due to 
// a dropped connectsion, AFAICT.
// Compare/consider what SSComponent does.
//
public class SimpleRSC implements RSC
{
	private final RowSet rs;
	private final int index;
	private final String name;
	private static final Logger logger = SSUtils.getLogger();

	private SimpleRSC(RowSet rs, Integer index, String name) throws SQLException
	{
		this.rs = rs;
		this.index = index != null ? index : RowSetOps.getColumnIndex(rs, name);
		this.name = name != null ? name : RowSetOps.getColumnName(rs, index);
	}

	SimpleRSC(RowSet rs, int index) throws SQLException
	{
		this(rs, index, null);
	}

	SimpleRSC(RowSet rs, String name) throws SQLException
	{
		this(rs, null, name);
	}
	
	/** {@inheritDoc} */
	@Override
	public RowSet getRowSet()
	{
		return rs;
	}

	/** {@inheritDoc} */
	@Override
	public int getBoundColumnIndex()
	{
		return index;
	}

	/** {@inheritDoc} */
	@Override
	public String getBoundColumnName()
	{
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public JDBCType getBoundColumnJDBCType()
	{
		try {
			return RowSetOps.getJDBCColumnType(rs, index);
		} catch (SQLException ex) {
			throw new SSSQLRuntimeException(ex);
		}
	}

	/** {@inheritDoc} */
	// TODO: SQLException better?
	@Override
	public String getBoundColumnText()
	{
		String value = "";

		try {
			if (getRowSet().getRow() != 0) {
				value = RowSetOps.getColumnObjectText(this);
				if (!getAllowNull() && (value == null)) {
					value = "";
				}
			}
		} catch (final SQLException ex) {
			logger.log(ERROR, getColumnForLog() + " - SQL Exception.", ex);
			throw new SSSQLRuntimeException(ex);
		}

		return value;
	}

	/** {@inheritDoc} */
	// TODO: SQLException better?
	@Override
	public <T> T getBoundColumnObject(Class<T> clazz)
	{

		T value = null;
		
		try {
			if (getRowSet().getRow() != 0) {
				value = clazz.cast(RowSetOps.getColumnObject(
						this, findJavaTypeClass(getBoundColumnJDBCType())));
			}
		} catch (final SQLException ex) {
			logger.log(ERROR, getColumnForLog() + " - SQL Exception.", ex);
			throw new SSSQLRuntimeException(ex);
		}
		
		return value;
	}

	// @Override
	// public Object getBoundColumnObject() throws SQLException
	// {
	// 	if (getColumnCount() == 0)
	// 		return null;
	// 	
	// 	Object objectValue = fetchCurrentValue(this);
	// 	return convertObjectType(objectValue, getBoundColumnJDBCType());
	// }


	// // TODO: getTypedObject
	// @Override
	// public <T> T getTypedObject() throws SQLException
	// {
	// 	throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	// }

	/** {@inheritDoc} */
	@Override
	public boolean getAllowNull()
	{
		return RowSetOps.isNullable(rs, index).orElse(true);
	}

	/** {@inheritDoc} */
	@Override
	public String getColumnForLog()
	{
		return "["+name+"]";
	}

	// Following to avoid not used error
	static {
		if(Boolean.FALSE)
			try {new SimpleRSC(null, 0).getColumnCount();}
			catch(SQLException ex) {}
	}

	private int getColumnCount() {
		try {
			return rs.getMetaData().getColumnCount();
		} catch (final SQLException ex) {
			logger.log(ERROR, getColumnForLog() + " - SQL Exception.", ex);
			throw new SSSQLRuntimeException(ex);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return String.format("SimpleRSC{%s, [%s], %d}", SSUtils.objectID(rs), name, index);
	}
}
