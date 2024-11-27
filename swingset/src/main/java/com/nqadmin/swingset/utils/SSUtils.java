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
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.StackLocatorUtil;


/**
 *
 * @author err
 */
public class SSUtils {
	private SSUtils() {}
	
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
	public static org.apache.logging.log4j.Logger getLogger() {
		// NOTE: this can be re-implemented by examining
		// new Throwable().getStackTrace();
		org.apache.logging.log4j.Logger logger;
		try {
			return org.apache.logging.log4j.LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
		} catch(UnsupportedOperationException ex) {}
		logger = org.apache.logging.log4j.LogManager.getRootLogger();
		// Note: can check for root logger with
		// logger.getName().isEmpty()
		logger.error("Using RootLogger", new Throwable());
		return logger;
	}

	/**
	 * This is similar to LogManager.getLogger(), except that
	 * if getSystemLogger fails then this method returns the root logger.
	 * So this is suitable for UI components that might get instantiated
	 * by a gui builder.
	 *
	 * See: https://github.com/bpangburn/swingset/pull/123
	 * 
	 * @return the Logger
	 */
	public static Logger getSystemLogger() {
		Class<?> cc = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)
				.getCallerClass();
		return getSystemLogger(cc.getName());
	}

	/**
	 * Return a logger for the name.
	 * @param loggerName name
	 * @return logger
	 */
	public static Logger getSystemLogger(String loggerName) {
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
	 * @param _container - container in which to locate/identify compoents
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
	}

//	/**
//	 * Setup a {@linkplain CachedRowSet}'s primary keys, use the component's
//	 * row set to get the database table's keys.
//	 * If not a CachedRowSet or the key is already set, do nothing.
//	 * Note a JoinRowSet is skipped; only want to set keys for single table.
//	 * @param comp component
//	 */
//	// TODO: Could have an array of primary keys, one entry per column.
//	//		 Could this be needed for joins?
//	public static void setupDefaultPrimaryKeys(SSComponentInterface comp)
//	{
//		RowSet rs = comp.getRowSet();
//		if (rs instanceof JoinRowSet)
//			return;
//		if (!(rs instanceof CachedRowSet crs))
//			return;
//		try {
//			if (crs.getKeyColumns() != null)
//				return;
//			String tableName = crs.getMetaData().getTableName(comp.getBoundColumnIndex());
//			Set<Integer> key = getPrimaryKeyColumnsForTable(
//					SSDBSupport.getDefault().getTemporaryConnection(crs), tableName);
//			crs.setKeyColumns(key.stream().mapToInt(i -> i).toArray());
//		} catch (SQLException ex) {
//		}
//	}

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

//	/**
//	 *
//	 * @param connection
//	 * @param tableName
//	 * @return
//	 * @throws SQLException
//	 */
//
//	public static Set<Integer> getPrimaryKeyColumnsForTable(Connection connection, String tableName) throws SQLException
//	{
//		try(ResultSet pkColumns= connection.getMetaData().getPrimaryKeys(null,null,tableName);) {
//			SortedSet<Integer> pkColumnSet = new TreeSet<>();
//			while(pkColumns.next()) {
//				Integer pkPosition = pkColumns.getInt("KEY_SEQ");
//				//String pkColumnName = pkColumns.getString("COLUMN_NAME");
//				//System.out.println(""+pkColumnName+" is the "+pkPosition+". column of the primary key of the table "+tableName);
//				pkColumnSet.add(pkPosition);
//			}
//			return pkColumnSet;
//		}
//	}

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

}
