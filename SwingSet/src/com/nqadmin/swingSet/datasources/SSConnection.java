/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004-2005, The Pangburn Company and Prasanth R. Pasala
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  The names of its contributors may not be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

 package com.nqadmin.swingSet.datasources;

 import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.SQLException;
 import java.io.IOException;
 import java.lang.ClassNotFoundException;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import java.beans.PropertyChangeSupport;
 import java.beans.PropertyChangeListener;
 
 /**
 * SSConnection.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * The SSConnection class is a wrapper for Connection.
 * It provides methods to specify the url, username, password & driver class name.
 * The createConnection should be called before calling the getConnection method.
 * When ever any connection parameters are changed createConnection has to be called
 * to change to connection object.
  *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
 public class SSConnection implements Serializable {

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
    transient protected Connection connection;

	/**
	 *Convenience class for providing the property change listener support
	 */
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	 
    /**
     * Constructs a default SSConnection object.
     */
    public SSConnection() {
    }

    /**
     * Constructs a SSConnection object with the specified database url.
     *
     * @param _url - url to the database to which connection has to be established.
     * the url should be of the form jdbc:subprotocol:subname
     */
    public SSConnection(String _url) {
        url = _url;
    }

    /**
     * Constructs a SSConnection object with the specified database url.
     *
     * @param _url - url to the database to which connection has to be established.
     * the url should be of the form jdbc:subprotocol:subname
     * @param _username - the database username on whose behalf the connection is being made
     * @param _password - the user's password
     */
    public SSConnection(String _url, String _username, String _password) {
        url = _url;
        username = _username;
        password = _password;
    }

    /**
     * Constructs a SSConnection object with the specified database url.
     *
     * @param _url - url to the database to which connection has to be established.
     * the url should be of the form jdbc:subprotocol:subname
     * @param _username - the database username on whose behalf the connection is being made
     * @param _password - the user's password
     * @param _driverName - name of the database driver to be used.
     */
    public SSConnection(String _url, String _username, String _password, String _driverName) {
        url = _url;
        username = _username;
        password = _password;
        driverName = _driverName;
    }

    /**
     * Sets the url to the database.
     *
     * @param _url - url to the database to which connection has to be established.
     * the url should be of the form jdbc:subprotocol:subname
     */
    public void setUrl(String _url) {
        String oldValue = url;
        url = _url;
        changeSupport.firePropertyChange("url", oldValue, url);
        
    }
    
    /**
     * Returns the url to the database.
     *
     * @return returns the database url.
     */
    public String getUrl() {
        return url;
    }    

    /**
     * Sets the username to be used while connecting to the database.
     *
     * @param _username - the database username on whose behalf the connection is being made
     */
    public void setUsername(String _username) {
    	String oldValue = username;
        username = _username;
        changeSupport.firePropertyChange("username", oldValue, username);
    }
    
    /**
     * Returns the username being used to connect to the database.
     *
     * @return returns the database username.
     */
    public String getUsername() {
        return username;
    }    

    /**
     * Sets the password to be used while connecting to the database.
     *
     * @param _password - the user's password to be used.
     */
    public void setPassword(String _password) {
    	String oldValue = password;
        password = _password;
        changeSupport.firePropertyChange("password", oldValue, password);
    }
    
    /**
     * Returns the password being used to connect to the database.
     *
     * @return returns the user's password.
     */
    public String getPassword() {
        return password;
    }    

    /**
     * Sets the database driver class name.
     *
     * @param _driverName - name of the database driver to be used.
     */
    public void setDriverName(String _driverName) {
    	String oldValue = driverName;
        driverName = _driverName;
        changeSupport.firePropertyChange("driverName", oldValue, password);
    }
    
    /**
     * Returns the database driver being used.
     *
     * @return returns the database driver being used.
     */
    public String getDriverName() {
        return driverName;
    }
    
    /**
     * Returns the database connection object.
     *
     * @return returns the database connection object.
     */
    public Connection getConnection() {
        return connection;
    }    

    /**
     * Method to add bean property change listeners.
     *
     * @param _listener bean property change listener
     */
    public void addPropertyChangeListener(PropertyChangeListener _listener) {
    	changeSupport.addPropertyChangeListener(_listener);
    }
    
    /**
     * Method to remove bean property change listeners.
     *
     * @param _listener bean property change listener
     */    
    public void removePropertyChangeListener(PropertyChangeListener _listener) {
    	changeSupport.removePropertyChangeListener(_listener);
    }

    /**
     * Creates a connection to the database based on the information provided
     * by the user.
     */
    public void createConnection() throws SQLException, ClassNotFoundException{
        Class.forName(driverName);
        connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Recreates the connection when the object is deserialized.
     */
    protected void readObject(ObjectInputStream objIn) throws IOException, ClassNotFoundException {
        objIn.defaultReadObject();
        try{
            createConnection();
        }catch(SQLException se) {
            se.printStackTrace();
        }
    }
 }

/*
 * $Log$
 * Revision 1.5  2005/02/09 23:04:01  yoda2
 * JavaDoc cleanup.
 *
 * Revision 1.4  2005/02/09 06:39:06  prasanth
 * Added PropertyChangeSupport
 *
 * Revision 1.3  2005/02/04 22:49:09  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.2  2004/11/11 14:45:57  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.1  2004/10/25 21:47:50  prasanth
 * Initial Commit
 *
 */