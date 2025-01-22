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
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.lang.StackWalker.Option;
import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JoinRowSet;

import com.nqadmin.swingset.datasources.SSDBSupport;


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
	 * Check if the SSComponent's listener is added for debug/logging.
	 * @param comp 
	 * @return true if the listener is added
	 */
	public static boolean isSSComponentListenerAddedDebug(SSComponentInterface comp) {
		return comp.getSSCommon().isSSComponentListenerAdded();
	}

	/** Put this in the global lookup to create debug row set listeners */
	public static class DebugRowSetListener {
	}

	/**
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
			String tableName = crs.getMetaData().getTableName(comp.getBoundColumnIndex());
			Set<Integer> key = getPrimaryKeyColumnsForTable(
					SSDBSupport.getDefault().getTemporaryConnection(crs), tableName);
			crs.setKeyColumns(key.stream().mapToInt(i -> i).toArray());
		} catch (SQLException ex) {
		}
	}

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

	// https://stackoverflow.com/questions/21328371/get-primary-key-column-from-resultset-java

	/**
	 *
	 * @param connection
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */

	public static Set<Integer> getPrimaryKeyColumnsForTable(Connection connection, String tableName) throws SQLException
	{
		try(ResultSet pkColumns= connection.getMetaData().getPrimaryKeys(null,null,tableName);) {
			SortedSet<Integer> pkColumnSet = new TreeSet<>();
			while(pkColumns.next()) {
				Integer pkPosition = pkColumns.getInt("KEY_SEQ");
				//String pkColumnName = pkColumns.getString("COLUMN_NAME");
				//System.out.println(""+pkColumnName+" is the "+pkPosition+". column of the primary key of the table "+tableName);
				pkColumnSet.add(pkPosition);
			}
			return pkColumnSet;
		}
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
		return sf("%s@%X", o.getClass().getSimpleName(), System.identityHashCode(o));
	}

	private static boolean isJunit;
	private static boolean didJunitCheck;
	/**
	 * @return true if junit is running.
	 */
	public static boolean isJunit() {
		if (!didJunitCheck) {
			StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
			Optional<StackWalker.StackFrame> frame = walker.walk(s -> s
					//.filter((f)->{System.err.println("    "+f.getClassName());return true; })
					.filter((f) -> f.getClassName().startsWith("org.junit"))
					.findFirst());
			if (frame.isPresent())
				isJunit = true;
			didJunitCheck = true;
		}
		return isJunit;
	}

}
