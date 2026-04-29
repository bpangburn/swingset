package com.nqadmin.swingset.datasources;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.RowSet;

public final class AppSSDBSupport extends DefaultSSDBSupport {

	private final Connection connection;

	public AppSSDBSupport(Connection connection) {
		super(connection);
		this.connection = connection;
	}

	@Override
	public Connection getSharedConnection(RowSet rs) throws SQLException {
		if (connection == null || connection.isClosed()) {
			return null;
		}
		return connection;
	}
}