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

import java.sql.JDBCType;
import java.sql.SQLException;
import javax.sql.RowSet;

// SSCollectionModel.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Read and write a collection of data items from/to database.
 * All items are the same jdbctype. How the items are stored is
 * independent of this interface.
 * 
 * @since 4.0.0
 */
public interface SSCollectionModel {

	/**
	 * @return the type of array elements handled by this model
	 */
	JDBCType getJDBCType();

	/**
	 * Get the data from the RowSet as a java object array.
	 * @param rowSet source of data, read from current row
	 * @param columnName database column containing the data
	 * @return array of objects of the data
	 * @throws SQLException if a database related error occurs
	 */
	Object[] readData(RowSet rowSet, String columnName) throws SQLException;

	/**
	 * Put the data from a java object array to the RowSet.
	 *
	 * @param rowSet source of data, read from current row
	 * @param columnName database column containing the data
	 * @param data array of data to write to the database
	 * @throws SQLException if a database related error occurs
	 */
	void writeData(RowSet rowSet, String columnName, Object[] data) throws SQLException;
}
