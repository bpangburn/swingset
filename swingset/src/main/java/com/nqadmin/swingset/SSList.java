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
package com.nqadmin.swingset;


import java.sql.JDBCType;

import com.nqadmin.swingset.core.List1;
import com.nqadmin.swingset.models.SSCollection;
import com.nqadmin.swingset.utils.SSComponent;

/**
 * See {@link List1}.
 */
// TODO: Long or Integer?
@SuppressWarnings("serial")
public class SSList extends List1<Object,String> implements SSComponent
{
	/**
	 * Creates an object of SSList with key type of {@code JDBCType.INTEGER}.
	 */
	public SSList()
	{
		// 2022-05-04: Changing from JDBCType.NULL to INTEGER as that will be the most likely
		//  key type and NULL is known to generate errors.
		this(JDBCType.INTEGER);
	}

	/**
	 * Creates a List1 with default
	 * of {@link com.nqadmin.swingset.models.SSDbArray}
	 * of specified jdbcType.
	 *
	 * @param jdbcType type of key of database elements
	 */
	public SSList(JDBCType jdbcType)
	{
		super(jdbcType);
	}

	/**
	 * Creates a List1 with specified model.
	 *
	 * @param collectionModel model to read/write the database
	 */
	public SSList(SSCollection collectionModel)
	{
		super(collectionModel);
	}
}
