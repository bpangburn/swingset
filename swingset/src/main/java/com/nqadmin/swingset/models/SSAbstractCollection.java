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
 * copyright (C) 2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.models;

import java.lang.System.Logger;
import java.sql.JDBCType;
import java.sql.SQLDataException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.nqadmin.swingset.utils.SSUtils;

import static java.lang.System.Logger.Level.*;

/**
 * This is the superclass for all collection models.
 * Handle the jdbcType info for the collection elements.
 * 
 * @since 4.0.0
 */
public abstract class SSAbstractCollection implements SSCollection {
	private final JDBCType jdbcType;

	/** logger */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Indicate and save the type of the collection.
	 * @param jdbcType the collection type
	 */
	public SSAbstractCollection(JDBCType jdbcType) {
		this.jdbcType = jdbcType != null ? jdbcType : JDBCType.NULL;
	}

	/** {@inheritDoc } */
	@Override
	public JDBCType getJDBCType() {
		return jdbcType;
	}

	/**
	 * Converts SQL array to object array. Turns array of primitives int
	 * array of Objects.
	 *
	 * @param array SQL array
	 * @return Object an array, can be of primitives or of Objects
	 * @throws java.sql.SQLDataException
	 */
	// TODO: put this into ConvertType
	public static List<?> convertArrayToObjectList(Object array) throws SQLDataException {
		Objects.requireNonNull(array);
		if (!array.getClass().isArray())
			throw new IllegalArgumentException("Must be an array");
		logger.log(DEBUG, () -> "SSList.toObjArray() contents: " + array);
		
		// TODO: Switch on type of array? dbArray.getClass().getComponentType()
		//       java.lang.reflect.Array.get(Object array, int index)
		int len = java.lang.reflect.Array.getLength(array);
		try {
			if (array.getClass().getComponentType().isPrimitive()) {
				Object[] objects = new Object[len];
				for (int i = 0; i < len; i++)
					objects[i] = (java.lang.reflect.Array.get(array, i));
				return Arrays.asList(objects);
			} else {
				return (List<?>) Arrays.asList((Object[])array);
			}
		} catch (final ClassCastException cce) {
			logger.log(ERROR, "Class Cast Exception.", cce);
			throw new SQLDataException(cce);
		}
	}

}
