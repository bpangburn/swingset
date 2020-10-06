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
 ******************************************************************************/

package com.nqadmin.swingset.datasources;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import com.sun.rowset.JdbcRowSetImpl;
import com.nqadmin.rowset.JdbcRowSetImpl;

// SSJdbcRowSetImpl.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * The SSJdbcRowSetImpl class is a wrapper for JdbcRowSetImpl. It provides all
 * rowset-related functionality for linking SwingSet components to an
 * SSConnection.
 */
public class SSJdbcRowSetImpl extends JdbcRowSetImpl implements SSRowSet {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -3556990832719097405L;

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * Empty constructor
	 */
	public SSJdbcRowSetImpl() {
		super();
	}

	/**
	 * Constructs a SSJdbcRowSetImpl object with the specified Connection.
	 *
	 * @param _connection Connection object to be used to connect to the database.
	 * @throws SQLException	SQLException
	 */
	public SSJdbcRowSetImpl(final Connection _connection) throws SQLException {
		super(_connection);
	}

	/**
	 * Constructs a SSJdbcRowSetImpl object with the specified Connection and SQL
	 * command.
	 *
	 * @param _connection Connection object to be used to connect to the database.
	 * @param _command    SQL query to be executed.
	 * @throws SQLException	SQLException
	 */
	public SSJdbcRowSetImpl(final Connection _connection, final String _command) throws SQLException {
		super(_connection);
		setCommand(_command);
	}

	/**
	 * Constructs a SSJdbcRowSetImpl object with the specified SSConnection.
	 *
	 * @param _ssConnection SSConnection object to be used to connect to the
	 *                      database.
	 *
	 * @deprecated Starting in 2.3.0+ use
	 *             {@link #setConnection(Connection _connection)} instead.
	 */
	@Deprecated
	public SSJdbcRowSetImpl(final SSConnection _ssConnection) {
		setSSConnection(_ssConnection);
	}

	/**
	 * Constructs a SSJdbcRowSetImpl object with the specified SSConnection and SQL
	 * command.
	 *
	 * @param _ssConnection SSConnection object to be used to connect to the
	 *                      database.
	 * @param _command      SQL query to be executed.
	 *
	 * @deprecated Starting in 2.3.0+ use
	 *             {@link #setConnection(Connection _connection) and @link #setCommand(String _command)}
	 *             instead.
	 *
	 */
	@Deprecated
	public SSJdbcRowSetImpl(final SSConnection _ssConnection, final String _command) {
		setSSConnection(_ssConnection);

		setCommand(_command);

	}

	/**
	 * Returns the Connection object being used.
	 *
	 * @return returns the Connection object being used.
	 */
	@Override
	public Connection getConnection() {
		return super.getConnection();
	}

	/**
	 * Returns the SSConnection object being used.
	 *
	 * @return returns the SSConnection object being used.
	 * @deprecated Starting in 2.3.0+ use {@link #getConnection()} instead.
	 */
	@Deprecated
	public SSConnection getSSConnection() {
		return new SSConnection(getConnection());
	}

	/**
	 * Sets and executes the query for a RowSet.
	 * <p>
	 * Overriding to catch any SQLException, but probably better to force calling
	 * class to handle in which case, there is no need to override this method.
	 *
	 * @param _command SQL query to be executed.
	 */
	@Override
	public void setCommand(final String _command) {
		try {
			super.setCommand(_command);
		} catch (final SQLException se) {
			// TODO consider letting the calling class handle any SQLException and do away
			// with this overridden method.
			logger.error("SQL Exception for command " + _command + ".", se);
		}
	}

	/**
	 * Sets the Connection object to be used.
	 *
	 * @param _connection connection object to be used to connect to the database.
	 */
	@Override
	public void setConnection(final Connection _connection) {
		super.setConnection(_connection);
	}

	/**
	 * Sets the Connection object to be used.
	 *
	 * @param _ssConnection connection object to be used to connect to the database.
	 *
	 * @deprecated Starting in 2.3.0+ use
	 *             {@link #setConnection(Connection _connection)} instead.
	 */
	@Deprecated
	public void setSSConnection(final SSConnection _ssConnection) {
		setConnection(_ssConnection.getConnection());
	}

}

