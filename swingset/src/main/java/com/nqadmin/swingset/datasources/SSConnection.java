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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * SSConnection.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * The SSConnection class is a wrapper for Connection. It provides methods to
 * specify the url, username, password and driver class name. The createConnection
 * should be called before calling the getConnection method. Whenever any
 * connection parameters are changed, createConnection has to be called to
 * change the connection object.
 */
public class SSConnection implements Serializable {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 844522706318494234L;

	/**
	 * URL to the database.
	 */
	protected String url = "";

	/**
	 * Username to be used while connecting to the database.
	 */
	protected String username = "";

	/**
	 * Password to be used for the username specified.
	 */
	protected String password = "";

	/**
	 * Database driver class name.
	 */
	protected String driverName = "";

	/**
	 * Database connection object.
	 */
	protected Connection connection;
	
	/**
	 * Log4j2 Logger
	 */
    private static final Logger logger = LogManager.getLogger(SSConnection.class);

	/**
	 * Constructs a default SSConnection object.
	 */
	public SSConnection() {
	}

	/**
	 * Constructs a SSConnection object with the specified connection
	 *
	 * @param _connection - database connection
	 */
	public SSConnection(Connection _connection) {
		this.connection = _connection;
	}

	/**
	 * Constructs a SSConnection object with the specified database url.
	 *
	 * @param _url - url to the database to which connection has to be established.
	 *             the url should be of the form jdbc:subprotocol:subname
	 */
	public SSConnection(String _url) {
		this.url = _url;
	}

	/**
	 * Constructs a SSConnection object with the specified database url.
	 *
	 * @param _url      - url to the database to which connection has to be
	 *                  established. the url should be of the form
	 *                  jdbc:subprotocol:subname
	 * @param _username - the database username on whose behalf the connection is
	 *                  being made
	 * @param _password - the user's password
	 */
	public SSConnection(String _url, String _username, String _password) {
		this.url = _url;
		this.username = _username;
		this.password = _password;
	}

	/**
	 * Constructs a SSConnection object with the specified database url.
	 *
	 * @param _url        - url to the database to which connection has to be
	 *                    established. the url should be of the form
	 *                    jdbc:subprotocol:subname
	 * @param _username   - the database username on whose behalf the connection is
	 *                    being made
	 * @param _password   - the user's password
	 * @param _driverName - name of the database driver to be used.
	 */
	public SSConnection(String _url, String _username, String _password, String _driverName) {
		this.url = _url;
		this.username = _username;
		this.password = _password;
		this.driverName = _driverName;
	}

	/**
	 * Creates a connection to the database based on the information provided by the
	 * user.
	 * 
	 * @throws SQLException	SQLException
	 * @throws ClassNotFoundException	ClassNotFoundException
	 */
	public void createConnection() throws SQLException, ClassNotFoundException {
		Class.forName(this.driverName);
		this.connection = DriverManager.getConnection(this.url, this.username, this.password);
	}

	/**
	 * Returns the database connection object. Buddh
	 * 
	 * @return returns the database connection object.
	 */
	public Connection getConnection() {
		// IF THE CONNECTION IS NOT YET CREATED, BUT WE HAVE ALL THE INFORMATION TO
		// CREATE ONE, THEN GO AHEAD AND TRY CREATING THE CONNECTION
		if (this.connection == null && this.url != null && this.driverName != null && this.username != null
				&& this.password != null && !this.url.trim().equals("") && !this.driverName.trim().equals("")) {
			try {
				createConnection();
			} catch (SQLException se) {
				logger.error("SQL Exception.", se);
			} catch (ClassNotFoundException cnfe) {
				logger.error("Class Not Found Exception.", cnfe);
			}
		}
		return this.connection;
	}

	/**
	 * Returns the database driver being used.
	 *
	 * @return returns the database driver being used.
	 */
	public String getDriverName() {
		return this.driverName;
	}

	/**
	 * Returns the password being used to connect to the database.
	 *
	 * @return returns the user's password.
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Returns the url to the database.
	 *
	 * @return returns the database url.
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Returns the username being used to connect to the database.
	 *
	 * @return returns the database username.
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Recreates the connection when the object is deserialized.
	 * @param objIn deserialized object
	 * @throws IOException 	IOException
	 * @throws ClassNotFoundException 	ClassNotFoundException
	 */
	// TODO It would probably be best to add SQLException to the exceptions thrown here.
	protected void readObject(ObjectInputStream objIn) throws IOException, ClassNotFoundException {
		objIn.defaultReadObject();
		try {
			createConnection();
		} catch (SQLException se) {
			logger.error("SQL Exception.", se);
		}
	}

	/**
	 * Sets the database connection
	 * 
	 * @param _connection 	database connection
	 */
	public void setConnection(Connection _connection) {
		this.connection = _connection;
	}

	/**
	 * Sets the database driver class name.
	 *
	 * @param _driverName - name of the database driver to be used.
	 */
	public void setDriverName(String _driverName) {
		// String oldValue = this.driverName;
		this.driverName = _driverName;
		// this.pChangeSupport.firePropertyChange("driverName", oldValue,
		// this.password);
	}

	/**
	 * Sets the password to be used while connecting to the database.
	 *
	 * @param _password - the user's password to be used.
	 */
	public void setPassword(String _password) {
		// String oldValue = this.password;
		this.password = _password;
		// this.pChangeSupport.firePropertyChange("password", oldValue, this.password);
	}

	/**
	 * Sets the url to the database.
	 *
	 * @param _url - url to the database to which connection has to be established.
	 *             the url should be of the form jdbc:subprotocol:subname
	 */
	public void setUrl(String _url) {
		// String oldValue = this.url;
		this.url = _url;
		// this.pChangeSupport.firePropertyChange("url", oldValue, this.url);

	}

	/**
	 * Sets the username to be used while connecting to the database.
	 *
	 * @param _username - the database username on whose behalf the connection is
	 *                  being made
	 */
	public void setUsername(String _username) {
		/// String oldValue = this.username;
		this.username = _username;
		// this.pChangeSupport.firePropertyChange("username", oldValue, this.username);
	}
}

/*
 * $Log$ Revision 1.8 2005/02/11 22:59:55 yoda2 Imported PropertyVetoException
 * and added some bound properties.
 *
 * Revision 1.7 2005/02/11 20:16:27 yoda2 Added infrastructure to support
 * property & vetoable change listeners (for beans).
 *
 * Revision 1.6 2005/02/10 20:13:20 yoda2 Setter/getter cleanup & method
 * reordering for consistency.
 *
 * Revision 1.5 2005/02/09 23:04:01 yoda2 JavaDoc cleanup.
 *
 * Revision 1.4 2005/02/09 06:39:06 prasanth Added PropertyChangeSupport
 *
 * Revision 1.3 2005/02/04 22:49:09 yoda2 API cleanup & updated Copyright info.
 *
 * Revision 1.2 2004/11/11 14:45:57 yoda2 Using TextPad, converted all tabs to
 * "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.1 2004/10/25 21:47:50 prasanth Initial Commit
 *
 */
