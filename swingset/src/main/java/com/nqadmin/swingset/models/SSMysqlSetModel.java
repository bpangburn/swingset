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

import java.sql.JDBCType;

/**
 * Implementation of SSCollectionModel as a set that uses a MySQL
 * SET datatype. In a MySQL database,
 * elements are in any order without duplicates.
 * Note the restriction that a comma, {@literal ','}, can not be
 * included in an element.
 * <p>
 * This is no enforcement that this class only be used with a MySQL
 * database column of type SET.
 * If it is used with a general string column in any database, then
 * it behaves like the {@code SSStringArrayModel}, except that commas
 * can not appear in elements.
 * 
 * @author err
 */
public class SSMysqlSetModel extends SSAbstractStringCollectionModel {
	/**
	 * MySQL uses a comma to separate elements of a SET.
	 * One implication is can't have comma in set element.
	 */
	private static final char COMMA_SEP = ',';

	/**
	 * Create model.
	 * @param _jdbcType the jdbcType of the elements in the array.
	 */
	public SSMysqlSetModel(JDBCType _jdbcType) {
		super(_jdbcType, COMMA_SEP, "COMMA(,)");
	}
}
