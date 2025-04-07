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
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/

package com.nqadmin.swingset.datasources;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.RowSet;

import com.nqadmin.swingset.utils.CentralLookup;


/**
 * Database specific handling and database access strategy.
 */
public interface SSDBSupport {

	static SSDBSupport getDefault() {
		SSDBSupport support = CentralLookup.getDefault().lookup(SSDBSupport.class);
		return support;
	}

	/**
	 * Return a connection for short term use that connects to the
	 * database where the row set comes from.
	 * @param rs row set from target database
	 * @return connection
	 */
	Connection getTemporaryConnection(RowSet rs) throws SQLException;

	/**
	 * Return a connection that connects to the database where the row set comes from;
	 * Close when finished.
	 * Tries url, dataSource, fallback.
	 * @param rs row set from target database
	 * @return connection
	 */
	Connection getConnection(RowSet rs) throws SQLException;

	/** A row set with a connection (dataSource, url, or whatever)
	 * that connects to same database as specified row set.
	 * Should be closed when done with it.
	 * @param rs row set from target database
	 * @return rowset for "temporary" use.
	 */
	RowSet getJdbcRowSet(RowSet rs) throws SQLException;
}
