/* *****************************************************************************
 * Copyright (C) 2023, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
package com.nqadmin.swingset.demo;

import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
//JDK-9 import java.sql.ConnectionBuilder;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
//JDK-9 import java.sql.ShardingKey;
//JDK-9 import java.sql.ShardingKeyBuilder;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;

/**
 * Provides a data source to use with the demo's naming service.
 */
public class DataSourcePool {
	private DataSourcePool() { }

	/** Data source name for binding lookup.  */
	public static final String DATA_SOURCE_NAME = "PoolDataSource";
	private static final Logger logger = LogManager.getLogger(MainClass.class);

	private static DataSource ds;
	private static JdbcConnectionPool cp;

	/**
	 * Createn a {@linkplain DataSource} object for the "H2" database's
	 * connection pool.
	 * @return datasource
	 */
	public static DataSource getDataSource() {
		if (cp == null) {
			// Setup the connection pool
			JdbcDataSource ds01 = new JdbcDataSource();
			ds01.setURL("jdbc:h2:mem:" + MainClass.DATABASE_NAME);
			cp = JdbcConnectionPool.create(ds01);
			ds = ds01;
		}
		return new MyDataSource(ds);
	}

	/**
	 * The maximum number of connections in the pool is tracked.
	 * @return max connection in pool
	 */
	public static int cMax() {
		return cMax;
	}

	/**
	 * The number of connection opens for connections from the pool is tracked.
	 * @return how many opens
	 */
	public static int nOpen() {
		return nOpen;
	}

	/**
	 * The number of connection closes for connections from the pool is tracked.
	 * @return how many closes
	 */
	public static int nClose() {
		return nClose;
	}

	private static int cMax;	// max active connections
	private static int nOpen;
	private static int nClose;

	// TODO: don't allow changing url, ...
	/** A DataSource wrapper so usage statistics can be gathered. */
	private static class MyDataSource implements DataSource
	{
		private final DataSource delegate;

		private MyDataSource(DataSource _delegate) {
			this.delegate = _delegate;
		}

		// Count the opens
		@Override
		public Connection getConnection() throws SQLException {
			nOpen++;
			MyConnection conn = new MyConnection(cp.getConnection());
			checkMax();
			return conn;
		}

		@Override
		public Connection getConnection(String username, String password) throws SQLException {
			nOpen++;
			MyConnection conn = new MyConnection(cp.getConnection(username, password));
			checkMax();
			return conn;
		}

		private static void checkMax() {
			int activeConnections = cp.getActiveConnections();
			if (activeConnections > cMax) {
				cMax = activeConnections;
				logger.info("ConnectionPool new max: " + activeConnections);
			}
		}

		////////////////////////////////////////////////////////////////////////
		//
		// unmodified delegation
		//

		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return delegate.unwrap(iface);
		}

		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return delegate.isWrapperFor(iface);
		}

		@Override
		public PrintWriter getLogWriter() throws SQLException {
			return delegate.getLogWriter();
		}

		@Override
		public void setLogWriter(PrintWriter out) throws SQLException {
			delegate.setLogWriter(out);
		}

		@Override
		public void setLoginTimeout(int seconds) throws SQLException {
			delegate.setLoginTimeout(seconds);
		}

		@Override
		public int getLoginTimeout() throws SQLException {
			return delegate.getLoginTimeout();
		}

		// JDK-9
		//@Override
		//public ConnectionBuilder createConnectionBuilder() throws SQLException {
		//	return delegate.createConnectionBuilder();
		//}

		@Override
		public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return delegate.getParentLogger();
		}

		// JDK-9
		//@Override
		//public ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
		//	return delegate.createShardingKeyBuilder();
		//}
	}

	private static class MyConnection implements Connection
	{
		private final Connection delegate;

		public MyConnection(Connection _delegate) {
			this.delegate = _delegate;
		}

		// Count the closes
		@Override
		public void close() throws SQLException {
			nClose++;
			delegate.close();
		}

		@Override
		//@SuppressWarnings("unchecked")
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return delegate.unwrap(iface);
			//try {
			//	if (isWrapperFor(iface)) {
			//		return (T) this;
			//	}
			//	throw DbException.getInvalidValueException("iface", iface);
			//} catch (Exception e) {
			//	throw logAndConvert(e);
			//}
		}

		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return delegate.isWrapperFor(iface);
			// if (iface == null)
			// 	return false;
			// return iface.isAssignableFrom(getClass()) || delegate.isWrapperFor(iface);
		}

		////////////////////////////////////////////////////////////////////////
		//
		// unmodified delegation
		//

		@Override
		public Statement createStatement() throws SQLException {
			return delegate.createStatement();
		}

		@Override
		public PreparedStatement prepareStatement(String sql) throws SQLException {
			return delegate.prepareStatement(sql);
		}

		@Override
		public CallableStatement prepareCall(String sql) throws SQLException {
			return delegate.prepareCall(sql);
		}

		@Override
		public String nativeSQL(String sql) throws SQLException {
			return delegate.nativeSQL(sql);
		}

		@Override
		public void setAutoCommit(boolean autoCommit) throws SQLException {
			delegate.setAutoCommit(autoCommit);
		}

		@Override
		public boolean getAutoCommit() throws SQLException {
			return delegate.getAutoCommit();
		}

		@Override
		public void commit() throws SQLException {
			delegate.commit();
		}

		@Override
		public void rollback() throws SQLException {
			delegate.rollback();
		}

		@Override
		public boolean isClosed() throws SQLException {
			return delegate.isClosed();
		}

		@Override
		public DatabaseMetaData getMetaData() throws SQLException {
			return delegate.getMetaData();
		}

		@Override
		public void setReadOnly(boolean readOnly) throws SQLException {
			delegate.setReadOnly(readOnly);
		}

		@Override
		public boolean isReadOnly() throws SQLException {
			return delegate.isReadOnly();
		}

		@Override
		public void setCatalog(String catalog) throws SQLException {
			delegate.setCatalog(catalog);
		}

		@Override
		public String getCatalog() throws SQLException {
			return delegate.getCatalog();
		}

		@Override
		public void setTransactionIsolation(int level) throws SQLException {
			delegate.setTransactionIsolation(level);
		}

		@Override
		public int getTransactionIsolation() throws SQLException {
			return delegate.getTransactionIsolation();
		}

		@Override
		public SQLWarning getWarnings() throws SQLException {
			return delegate.getWarnings();
		}

		@Override
		public void clearWarnings() throws SQLException {
			delegate.clearWarnings();
		}

		@Override
		public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
			return delegate.createStatement(resultSetType, resultSetConcurrency);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
		}

		@Override
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			return delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
		}

		@Override
		public Map<String, Class<?>> getTypeMap() throws SQLException {
			return delegate.getTypeMap();
		}

		@Override
		public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
			delegate.setTypeMap(map);
		}

		@Override
		public void setHoldability(int holdability) throws SQLException {
			delegate.setHoldability(holdability);
		}

		@Override
		public int getHoldability() throws SQLException {
			return delegate.getHoldability();
		}

		@Override
		public Savepoint setSavepoint() throws SQLException {
			return delegate.setSavepoint();
		}

		@Override
		public Savepoint setSavepoint(String name) throws SQLException {
			return delegate.setSavepoint(name);
		}

		@Override
		public void rollback(Savepoint savepoint) throws SQLException {
			delegate.rollback(savepoint);
		}

		@Override
		public void releaseSavepoint(Savepoint savepoint) throws SQLException {
			delegate.releaseSavepoint(savepoint);
		}

		@Override
		public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		@Override
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
			return delegate.prepareStatement(sql, autoGeneratedKeys);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
			return delegate.prepareStatement(sql, columnIndexes);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
			return delegate.prepareStatement(sql, columnNames);
		}

		@Override
		public Clob createClob() throws SQLException {
			return delegate.createClob();
		}

		@Override
		public Blob createBlob() throws SQLException {
			return delegate.createBlob();
		}

		@Override
		public NClob createNClob() throws SQLException {
			return delegate.createNClob();
		}

		@Override
		public SQLXML createSQLXML() throws SQLException {
			return delegate.createSQLXML();
		}

		@Override
		public boolean isValid(int timeout) throws SQLException {
			return delegate.isValid(timeout);
		}

		@Override
		public void setClientInfo(String name, String value) throws SQLClientInfoException {
			delegate.setClientInfo(name, value);
		}

		@Override
		public void setClientInfo(Properties properties) throws SQLClientInfoException {
			delegate.setClientInfo(properties);
		}

		@Override
		public String getClientInfo(String name) throws SQLException {
			return delegate.getClientInfo(name);
		}

		@Override
		public Properties getClientInfo() throws SQLException {
			return delegate.getClientInfo();
		}

		@Override
		public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
			return delegate.createArrayOf(typeName, elements);
		}

		@Override
		public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
			return delegate.createStruct(typeName, attributes);
		}

		@Override
		public void setSchema(String schema) throws SQLException {
			delegate.setSchema(schema);
		}

		@Override
		public String getSchema() throws SQLException {
			return delegate.getSchema();
		}

		@Override
		public void abort(Executor executor) throws SQLException {
			delegate.abort(executor);
		}

		@Override
		public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
			delegate.setNetworkTimeout(executor, milliseconds);
		}

		@Override
		public int getNetworkTimeout() throws SQLException {
			return delegate.getNetworkTimeout();
		}

		// JDK-9
		// @Override
		// public void beginRequest() throws SQLException {
		// 	delegate.beginRequest();
		// }

		// @Override
		// public void endRequest() throws SQLException {
		// 	delegate.endRequest();
		// }

		// JDK-9
		// @Override
		// public boolean setShardingKeyIfValid(ShardingKey shardingKey, ShardingKey superShardingKey, int timeout) throws SQLException {
		// 	return delegate.setShardingKeyIfValid(shardingKey, superShardingKey, timeout);
		// }

		// @Override
		// public boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout) throws SQLException {
		// 	return delegate.setShardingKeyIfValid(shardingKey, timeout);
		// }

		// @Override
		// public void setShardingKey(ShardingKey shardingKey, ShardingKey superShardingKey) throws SQLException {
		// 	delegate.setShardingKey(shardingKey, superShardingKey);
		// }

		// @Override
		// public void setShardingKey(ShardingKey shardingKey) throws SQLException {
		// 	delegate.setShardingKey(shardingKey);
		// }
	}
}
