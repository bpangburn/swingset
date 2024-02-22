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
import java.util.logging.Level;

import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.utils.SSUtils;

import static javax.sql.rowset.spi.SyncResolver.DELETE_ROW_CONFLICT;
import static javax.sql.rowset.spi.SyncResolver.INSERT_ROW_CONFLICT;
import static javax.sql.rowset.spi.SyncResolver.NO_ROW_CONFLICT;
import static javax.sql.rowset.spi.SyncResolver.UPDATE_ROW_CONFLICT;
/**
 *
 * @author err
 */
public class Utils
{
	private Utils() { }
	private static final Logger logger = SSUtils.getLogger();

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
	 * Output conflict information from the specified SyncResolver
	 * after acceptChanges.
	 * @param ps output info here
	 * @param sr from acceptChanges exception
	 * @param crs source of acceptChanges
	 */
	public static void dump(PrintStream ps, SyncResolver sr, CachedRowSet crs)
	{
		int initialRow = -1;
		try {
			initialRow = crs.getRow();
			ps.println("=== SyncResolver ===");
			
			while(sr.nextConflict()) {
				Object crsValue;  // value in the RowSet object
				Object srValue;  // value in the SyncResolver object
				int row = sr.getRow();
				ps.printf("    Row %d, status: %s\n", row, conflictStatus(sr.getStatus()));
				crs.absolute(row);
				
				int colCount = crs.getMetaData().getColumnCount();
				for(int j = 1; j <= colCount; j++) {
					srValue = sr.getConflictValue(j);
					crsValue = crs.getObject(j);
					ps.printf("    column %d, conflict: '%s', crs: '%s'\n",
							j, srValue, crsValue);
					if (Boolean.FALSE) {
						// java.sql.SQLException: This column not in conflict
						// at java.sql.rowset/com.sun.rowset.internal.SyncResolverImpl.setResolvedValue(SyncResolverImpl.java:209) ~[java.sql.rowset:?]
						// Experiment
						ps.printf("    HACK resolved value\n");
						sr.setResolvedValue(j, crsValue);
					} else {
						if (srValue != null) {
							// TODO: for now, the cached row set value wins.
							sr.setResolvedValue(j, crsValue);
						}
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
