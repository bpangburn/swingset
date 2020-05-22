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

import com.sun.rowset.JdbcRowSetImpl;

/**
 * SSJdbcRowSetImpl.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * The SSJdbcRowSetImpl class is a wrapper for JdbcRowSetImpl. It provides all
 * rowset-related functionality for linking SwingSet components to an
 * SSConnection.
 */
@SuppressWarnings("restriction")
public class SSJdbcRowSetImpl extends JdbcRowSetImpl implements SSRowSet {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -3556990832719097405L;

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
	public SSJdbcRowSetImpl(Connection _connection) throws SQLException {
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
	public SSJdbcRowSetImpl(Connection _connection, String _command) throws SQLException {
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
	 *             {@link #SSJdbcRowSetImpl(Connection _connection)} instead.
	 */
	@Deprecated
	public SSJdbcRowSetImpl(SSConnection _ssConnection) {
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
	 *             {@link #SSJdbcRowSetImpl(Connection _connection, String _command)}
	 *             instead.
	 * 
	 */
	@Deprecated
	public SSJdbcRowSetImpl(SSConnection _ssConnection, String _command) {
		setSSConnection(_ssConnection);

		// try {
		setCommand(_command);
		// } catch (SQLException se) {
		// se.printStackTrace();
		// }

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
	 * 
	 * Overriding to catch any SQLException, but probably better to force calling
	 * class to handle in which case, there is no need to override this method.
	 * 
	 * @param _command SQL query to be executed.
	 */
	@Override
	public void setCommand(String _command) {
		try {
			super.setCommand(_command);
		} catch (SQLException e) {
			// TODO consider letting the calling class handle any SQLException and do away
			// with this overridden method.
			e.printStackTrace();
		}
	}

	/**
	 * Sets the Connection object to be used.
	 * 
	 * @param _connection connection object to be used to connect to the database.
	 */
	@Override
	public void setConnection(Connection _connection) {
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
	public void setSSConnection(SSConnection _ssConnection) {
		setConnection(_ssConnection.getConnection());
	}

}

/*
 * $Log$ Revision 1.15 2005/05/26 19:26:35 prasanth Added method get/update
 * methods for Time & TimeStamp.
 *
 * Revision 1.14 2005/05/24 23:07:37 prasanth 1. Added get/set methods for
 * object. 2. Added rowDeleted, rowInserted, rowUpdated methods 3. Added
 * getMetaData method.
 *
 * Revision 1.13 2005/02/12 03:27:09 yoda2 Added bound properties (for beans).
 *
 * Revision 1.12 2005/02/11 22:59:56 yoda2 Imported PropertyVetoException and
 * added some bound properties.
 *
 * Revision 1.11 2005/02/11 20:16:31 yoda2 Added infrastructure to support
 * property & vetoable change listeners (for beans).
 *
 * Revision 1.10 2005/02/10 15:53:09 yoda2 Added class descriptions to JavaDoc.
 *
 * Revision 1.9 2005/02/09 23:04:01 yoda2 JavaDoc cleanup.
 *
 * Revision 1.8 2005/02/09 06:39:44 prasanth Added PropertyChangeSupport
 *
 * Revision 1.7 2005/02/04 22:49:09 yoda2 API cleanup & updated Copyright info.
 *
 * Revision 1.6 2005/01/18 20:58:11 prasanth Added function to get & set bytes.
 *
 * Revision 1.5 2004/11/11 14:45:57 yoda2 Using TextPad, converted all tabs to
 * "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.4 2004/11/01 15:53:19 yoda2 Fixed various JavaDoc errors.
 *
 * Revision 1.3 2004/10/29 20:45:30 yoda2 Fixed issue with setCommand() not
 * updating underlying JdbcRowSetImpl.
 *
 * Revision 1.2 2004/10/28 15:27:17 prasanth Calling setType & setConcurrency
 * after instanciating JdbcRowSetImpl
 *
 * Revision 1.1 2004/10/25 21:47:50 prasanth Initial Commit
 *
 */
