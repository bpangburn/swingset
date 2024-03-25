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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;

/**
 * Provides a data source that returns a pre-specifies connection; typically
 * used to share a single connection with multiple JdbcRowSets.
 * To use with the demo's naming service.
 */
public class DataSourceShareConnection {
	/** Name of the DataSource. */
	public static final String DATA_SOURCE_NAME = "ShareConnection";

	private DataSourceShareConnection() { }

	/**
	 * Provide a DataSource that always returns a specified connection.
	 * @param conn hook up the DataSource to this connection
	 * @return DataSource
	 */
	public static DataSource getDataSource(Connection conn) {
		JdbcDataSource ds01 = new JdbcDataSource();
		ds01.setURL("jdbc:h2:mem:" + MainClass.DATABASE_NAME);
		return new MyDataSource(ds01, conn);
	}

	private static class MyDataSource implements DataSource
	{
		private final DataSource delegate;
		private final Connection sharedConnection;
		
		private MyDataSource(DataSource _delegate, Connection conn) {
			this.delegate = _delegate;
			this.sharedConnection = conn;
		}

		@Override
		public Connection getConnection() throws SQLException {
			return sharedConnection;
		}

		@Override
		public Connection getConnection(String username, String password) throws SQLException {
			return sharedConnection;
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

}
