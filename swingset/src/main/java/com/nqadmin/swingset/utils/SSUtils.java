/* *****************************************************************************
 * Copyright (C) 2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.lang.StackWalker.Option;
import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JoinRowSet;

import com.nqadmin.swingset.datasources.SSDBSupport;
import com.nqadmin.swingset.navigate.RowsModel;


/**
 * General.
 */
public class SSUtils {
	private SSUtils() {}

	/** Temporary for hiding SSCommon; used from SSDBComboBox.
	 * @param comp component to update
	 */
	public static void updateSSComponent_HACK(SSComponentInterface comp) {
		comp.getSSCommon().updateSSComponent();
	}

	/**
	 * Signal that the current row has changed for CachedRowSet.
	 * @param comp to notify of row change
	 */
	public static void issueRowChanged_HACK(SSComponentInterface comp) {
		comp.getSSCommon().issueRowChanged();
	}

	/**
	 * Use this if you want a 1-1 correspondence between {@code RowSet} and {@code RowsModel};
	 * for 1-1 only this method should be used.
	 * If an existing RowsModel for the RowSet is
	 * not found, a new RowsModel is created.
	 * If {@link RowsModel#create(javax.sql.RowSet) }
	 * is used multiple RowsModel can be created for the same RowSet;
	 * and see {@link RowsModel#getActiveRowModels(javax.sql.RowSet) }
	 * <p>
	 * Can also use as a transition aid to RowsModel.
	 * @param rs
	 * @return 
	 */
	// TODO: could throw exception if more than one model for specified RowSet.
	public static RowsModel findRowsModel(RowSet rs) {
		RowsModel rowsModel = RowsModel.getActiveRowModel(rs);
		if (rowsModel == null)
			rowsModel = RowsModel.create(rs);
		return rowsModel;
	}

	/**
	 * Check if the SSComponent's listener is added for debug/logging.
	 * @param comp 
	 * @return true if the listener is added
	 */
	public static boolean isSSComponentListenerAddedDebug(SSComponentInterface comp) {
		return comp.getSSCommon().isSSComponentListenerAdded();
	}

	/** Put this in the global lookup to create debug row set listeners */
	public static class DebugRowSetListenerFlag {
	}

	/**
	 * Return the Logger for the caller.
	 * This is similar to LogManager.getLogger(), except that
	 * if getLogger fails then this method returns the root logger.
	 * So this is suitable for UI components that might get instantiated
	 * by a gui builder.
	 *
	 * See: https://github.com/bpangburn/swingset/pull/123
	 * 
	 * @return the Logger
	 */
	public static Logger getLogger() {
		Class<?> cc = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)
				.getCallerClass();
		return getLogger(cc.getName());
	}

	/**
	 * Return the logger name for the caller.
	 * @return logger name
	 */
	public static String getLoggerName() {
		Class<?> cc = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)
				.getCallerClass();
		return cc.getName();
	}

	/**
	 * Return a logger for the name.
	 * @param loggerName name
	 * @return logger
	 */
	public static Logger getLogger(String loggerName) {
		return System.getLogger(loggerName);
	}

	/**
	 * Shorthand for "String.format(fmt, args)".
	 * @param fmt format
	 * @param args args
	 * @return string
	 */
	public static String sf(String fmt, Object... args) {
		return args.length == 0 ? fmt : String.format(fmt, args);
	}


	/**
	 * Find all SSComponents in the specified container.
	 * @param _container
	 * @return List of SScomponents
	 */
	public static List<SSComponentInterface> findSSComponents(Container _container)
	{
		ArrayList<SSComponentInterface> l = new ArrayList<>();
		findSSComponents(_container, l);
		return l;
	}
	private static void findSSComponents(Container _container, List<SSComponentInterface> l)
	{
		//
		// TODO: Need a special case for getViewport() or anything else ???????
		//
		for (Component comp : _container.getComponents()) {
			switch (comp) {
			case SSComponentInterface c -> l.add(c);
			case Container c -> findSSComponents(c, l);
			default -> { }
			}
		}
	}

	/**
	 * Notify the user of something...
	 */
	// TODO: add option to flash window/panel...
	public static void beep()
	{
		Toolkit.getDefaultToolkit().beep();

		// TODO:
		// UIManager.getLookAndFeel().provideErrorFeedback(JFormattedTextField.this);
	}

	/**
	 * Get the size of a map taking into account possible weak keys.
	 * 
	 * @param map get size of this map
	 * @return map size
	 */
	@SuppressWarnings("null")
	public static int size(Map<?,?> map)
	{
		if (!(map instanceof ConcurrentMap))
			return map.size();
		// Can't depend on size() method when weakKeys.
		int counter = 0;
		for (Map.Entry<?, ?> _ : map.entrySet()) {
			counter++;
		}
		return counter;
	}

	/**
	 * Setup a {@linkplain CachedRowSet}'s primary keys, use the component's
	 * row set to get the database table's keys.
	 * If not a CachedRowSet or the key is already set, do nothing.
	 * Note a JoinRowSet is skipped; only want to set keys for single table.
	 * @param comp component
	 */
	// TODO: Could have an array of primary keys, one entry per column.
	//		 Could this be needed for joins?
	public static void setupDefaultPrimaryKeys(SSComponentInterface comp)
	{
		RowSet rs = comp.getRowSet();
		if (rs instanceof JoinRowSet)
			return;
		if (!(rs instanceof CachedRowSet crs))
			return;
		try {
			if (crs.getKeyColumns() != null)
				return;
			int[] keys = getPrimaryKeyColumns(
					SSDBSupport.getDefault().getSharedConnection(crs), crs);
			crs.setKeyColumns(keys);
		} catch (SQLException ex) {
		}
	}
	private static int[] getPrimaryKeyColumns(Connection connection, CachedRowSet crs) throws SQLException
	{
		return getPrimaryKeyColumns(connection.getMetaData(), crs);
	}

	private static int[] getPrimaryKeyColumns(DatabaseMetaData dbMetaData, CachedRowSet crs) throws SQLException
	{
		List<KeyInfo> kinfo = getPrimaryKeyInfoForTable(dbMetaData, crs.getTableName().toUpperCase());
		int[] keyCols = new int[kinfo.size()];
		for (KeyInfo ki : kinfo)
			keyCols[ki.keySeq - 1] = crs.findColumn(ki.columnName);
		return keyCols;
	}

	public record KeyInfo(int keySeq, String columnName){}

	// TODO: keys: should spec catalog/schema?
	/**
	 * @param dbMetaData
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static List<KeyInfo> getPrimaryKeyInfoForTable(DatabaseMetaData dbMetaData, String tableName) throws SQLException
	{
		try(ResultSet pKeys = dbMetaData.getPrimaryKeys(null,null,tableName);) {
			List<KeyInfo> keyInfo = new ArrayList<>(3);
			while(pKeys.next()) {
				keyInfo.add(new KeyInfo(pKeys.getInt("KEY_SEQ"), pKeys.getString("COLUMN_NAME")));
			}
			return keyInfo;
		}
	}

	// NOTES on isKey
	// TODO: isKey: Use a wrapper for the value, to represent no table, sql error, ...
	// TODO: isKey: How to get tableName/columnName for result set column consider joins...
	// TODO: isKey: How to set if automatic detection not wanted.
	// TODO: isKey: Should this be in RowSetOps
	// TODO: isKey: columnName/columnLabel
	// dbMetaData.getDatabaseProductName()

	/**
	 * Find {@link KeyInfo} for table associated with the specified column.
	 * Starting with the column's RowSet, get the dbMetaData, determine keys.
	 * @param rs
	 * @param _columnName
	 * @return
	 * @throws SQLException
	 */
	// TODO: isKey: lookup specializations for databases to access special result set info.
	//public static List<KeyInfo> getPrimaryKeyInfoForTable(RSC comp)
	public static List<KeyInfo> getPrimaryKeyInfoForTable(RowSet rs, String _columnName)
			throws SQLException
	{
		// TODO: isKey: this doesn't seem reliable. Consider join...
		// TODO: isKey: For now assume column names match

		String columnName = _columnName.toUpperCase();
		//RowSet rs = comp.getRowSet();
		int colIdx = rs.findColumn(columnName);
		ResultSetMetaData rsMetaData = rs.getMetaData();
		String tableName = rsMetaData.getTableName(colIdx);

		// TODO: just call all the names keys. Buggy if join ...
		List<KeyInfo> ki = SSDBSupport.getDefault().runWithConnection(rs,
				conn -> {
					DatabaseMetaData dbMetaData = conn.getMetaData();
					return getPrimaryKeyInfoForTable(dbMetaData, tableName);
				});
		return ki;
	}

	
	// https://stackoverflow.com/questions/21328371/get-primary-key-column-from-resultset-java

	//public static Set<Integer> getPrimaryKeyColumnsForTable(RowSet rs, int columnIndex)
	//		throws SQLException
	//{
	//	String tableName = rs.getMetaData().getTableName(2);
	//	DataSource ds = null;
	//	try {
	//		ds = InitialContext.doLookup(rs.getDataSourceName());
	//	} catch (NamingException ex) {
	//		ex.printStackTrace();
	//	}
	//	if(ds == null)
	//		return Collections.emptySet();
	//	try (Connection conn = ds.getConnection()) {
	//		return SSUtils.getPrimaryKeyColumnsForTable(conn, tableName);
	//	}
	//}

	////////////////////////////////////////////////////////////////////////////
	//
	// Common Messages
	//

	/**
	 * Error msg.
	 * @param oldType
	 * @param newType
	 * @return 
	 */
	public static String JDBCTypeMismatch(JDBCType oldType, JDBCType newType) {
		return sf("JDBCType mismatch: old %s, new %s", oldType, newType);
	}

	/**
	 * Error msg.
	 * @param oldVal
	 * @param newVal
	 * @return
	 */
	public static String NullabilityMismatch(boolean oldVal, boolean newVal) {
		return sf("Nullability mismatch: old %s, new %s", oldVal, newVal);
	}



	////////////////////////////////////////////////////////////////////////////
	//
	// Debug Support
	//

	/**
	 * This is for toString() of an SSComponent, rather than the JComponents string.
	 * @param comp string for this
	 * @return string from component
	 */
	public static String ssComponentToString(SSComponentInterface comp)
	{
		return sf("%s, column=%s", objectID(comp.getRowSet()), comp.getBoundColumnName());
	}

	/**
	 * Return a unique name for an Object, for example "String@89AB".
	 * Name is SimpleClassName followed by identityHashCode in hex.
	 * Used primarily for debug messages.
	 * @param o The Object
	 * @return unique name for the object or "null"
	 */
	// TODO: put this in utils/SSUtil
	public static String objectID(Object o) {
		if (o == null) {
			return "null";
		}
		String s = switch(o) {
		case RowSet rs -> sf("%s[%s]@%X",
				o.getClass().getSimpleName(), tableName(rs), System.identityHashCode(o));
		default -> sf("%s@%X",
				o.getClass().getSimpleName(), System.identityHashCode(o));
		};
		return s;
	}

	/**
	 * Return the name of the table where column 1 came from.
	 * @param rs
	 * @return 
	 */
	public static String tableName(RowSet rs) {
		try {
			return rs.getMetaData().getTableName(1);
		} catch (SQLException ex) { }
		return null;
	}

	private static boolean isJunit;
	private static boolean didJunitCheck;
	/**
	 * @return true if junit is running.
	 */
	public static boolean isJunit() {
		if (!didJunitCheck) {
			//.filter((f)->{System.err.println("    "+f.getClassName());return true; })
			StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
			Optional<StackWalker.StackFrame> frame = walker.walk(s -> s
					.filter((f) -> f.getClassName().startsWith("org.junit"))
					.findFirst());
			if (frame.isPresent())
				isJunit = true;
			didJunitCheck = true;
		}
		return isJunit;
	}

	/** true if in JUnit and NOT using JUnit logging
	 * @return  */
	public static boolean isJunitPrint()
	{
		if (!isJunit())
			return false;
		java.util.logging.Logger juLog = LogManager.getLogManager().getLogger("com.nqadmin.swingset");
		if (juLog != null) {
			Handler[] handlers = juLog.getHandlers();
			if (handlers.length > 0
					&& handlers[0].getFormatter() instanceof TestFormatterBase)
				return false;
		}
		return true;
	}

	/**
	 * This is used to distinguish formatter used in JUnit tests.
	 * Define it here for use by isJunitPrint.
	 * This is extended by the unit test logging infrastructure.
	 */
	public static class TestFormatterBase extends SimpleFormatter
	{
	}

}
