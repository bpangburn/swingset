/* *****************************************************************************
 * Copyright (C) 2024, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
package com.nqadmin.swingset.datasources;

import java.io.PrintStream;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.spi.SyncResolver;

import java.lang.System.Logger;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;
import static javax.sql.rowset.spi.SyncResolver.DELETE_ROW_CONFLICT;
import static javax.sql.rowset.spi.SyncResolver.INSERT_ROW_CONFLICT;
import static javax.sql.rowset.spi.SyncResolver.NO_ROW_CONFLICT;
import static javax.sql.rowset.spi.SyncResolver.UPDATE_ROW_CONFLICT;
/**
 * Sql related.
 */
public class Utils
{
	private Utils() { }
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Return either the unwrapped object of the specified type or null.
	 * @param <T>
	 * @param wrappedObj
	 * @param clazz
	 * @return
	 */
	public static <T extends Wrapper> T unwrap(T wrappedObj, Class<T> clazz)
	{
		try {
			if (!wrappedObj.isWrapperFor(clazz))
				return null;
			return wrappedObj.unwrap(clazz);
		} catch (SQLException ex) {
			return null;
		}
	}

	/**
	 * Output conflict information after acceptChanges from the
	 * specified SyncResolver. For test and debug;
	 * note that SyncResolver is "used up" with default implementation
	 * since rowset navigation doesn't work.
	 *
	 * @param out
	 * @param resolver
	 * @param crs
	 */
	static void dumpConflict(Consumer<String> out, List<ConflictRow> conflictRows)
	{
		out.accept("=== SyncResolver ===");
		for(ConflictRow cRow : conflictRows) {
			out.accept(sf("    Row %d, status: %s",
					cRow.row(), conflictStatus(cRow.status())));
			for(ConflictColumn cc : cRow.conflictCol())
				out.accept(sf("    column %d, conflict: '%s', crs: '%s'",
						cc.columnIndex, cc.resolveValue(), cc.crsValue));
		}
	}

	/**
	 * Output conflict information after acceptChanges from the
	 * specified SyncResolver. For test and debug;
	 * note that SyncResolver is "used up" with default implementation
	 * since rowset navigation doesn't work.
	 *
	 * @param out
	 * @param resolver
	 * @param crs
	 */
	static void dumpConflict(Consumer<String> out, SyncResolver resolver, CachedRowSet crs)
	{
		try {
			dumpConflict(out, collectConflict(resolver, crs));
		} catch (SQLException ex) {
			logger.log(ERROR, "dumping SyncResolver conflicts", ex);
		}
	}

	record ConflictColumn(int columnIndex, Object resolveValue, Object crsValue){}
	record ConflictRow(int row, int status, List<ConflictColumn> conflictCol){}

	static List<ConflictRow> collectConflictNoThrow(SyncResolver sr, CachedRowSet crs)
	{
		List<ConflictRow> conflicts = null;
		try {
			conflicts = collectConflict(sr, crs);
		} catch (SQLException ex) {
			logger.log(ERROR, "Collecting SyncResolver conflicts", ex);
		}
		return conflicts;
	}

	static List<ConflictRow> collectConflict(SyncResolver sr, CachedRowSet crs)
			throws SQLException
	{
		List<ConflictRow> cr = new ArrayList<>(5);
		int colCount = crs.getMetaData().getColumnCount();
		while(sr.nextConflict()) {
			List<ConflictColumn> cc = new ArrayList<>(colCount);
			int row = sr.getRow();
			crs.absolute(row);
			
			for(int j = 1; j <= colCount; j++) {
				cc.add(new ConflictColumn(j, sr.getConflictValue(j), crs.getObject(j)));
			}
			cr.add(new ConflictRow(row, sr.getStatus(), cc));
		}
		return cr;
	}
	
	/**
	 * Output conflict information from the specified SyncResolver
	 * after acceptChanges; resolve. For test and debug.
	 * @param ps output info here
	 * @param sr from acceptChanges exception
	 * @param crs source of acceptChanges
	 * @param selectDB if false, use values from CachedRowSet, if true from database
	 */
	// TODO: What about delete/insert conflicts
	public static void processConflict(PrintStream ps, SyncResolver sr, CachedRowSet crs, boolean selectDB)
	{
		int initialRow = -1;
		try {
			initialRow = crs.getRow();
			ps.println("=== SyncResolver ===");
			
			while(sr.nextConflict()) {
				Object crsValue;  // value in the RowSet object
				Object rslvValue;  // value in the SyncResolver object
				int row = sr.getRow();
				ps.printf("    Row %d, status: %s\n", row, conflictStatus(sr.getStatus()));
				crs.absolute(row);
				
				int colCount = crs.getMetaData().getColumnCount();
				for(int j = 1; j <= colCount; j++) {
					rslvValue = sr.getConflictValue(j);
					crsValue = crs.getObject(j);
					ps.printf("    column %d, conflict: '%s', crs: '%s'\n",
							j, rslvValue, crsValue);
					if (rslvValue != null) {
						Object persist = selectDB ? rslvValue : crsValue;
						sr.setResolvedValue(j, persist);
					}
				}
			}
		} catch (SQLException ex) {
			logger.log(ERROR, "dumping SyncResolver conflicts", ex);
		}
		try {
			if (initialRow > 0)
				crs.absolute(initialRow);
			// Following is an exception, looks like the JDK's minimal
			// com.sun.rowset.internal.SyncResolverImpl is non-compliant.
			//sr.first();
		} catch (SQLException ex) {
			logger.log(ERROR, "restoring position after dumping SyncResolver", ex);
		}
	}

	/**
	 * Printable conflict status.
	 * @param status in
	 * @return conflict as string
	 */
	public static String conflictStatus(int status)
	{
		return switch(status) {
			case UPDATE_ROW_CONFLICT -> "UPDATE_ROW_CONFLICT";
			case DELETE_ROW_CONFLICT -> "DELETE_ROW_CONFLICT";
			case INSERT_ROW_CONFLICT -> "INSERT_ROW_CONFLICT";
			case NO_ROW_CONFLICT     -> "NO_ROW_CONFLICT";
			default -> "INVALID_CONFLICT";
		};
	}
}
