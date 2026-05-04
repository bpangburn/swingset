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
 * copyright (C) 2024-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.navigate;

import java.lang.ref.WeakReference;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.spi.SyncProviderException;

import org.openide.util.WeakListeners;

import com.google.common.collect.MapMaker;
import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.utils.SSUtils;
import com.nqadmin.swingset.utils.SSUtils.DebugRowSetListenerFlag;

import static com.nqadmin.swingset.navigate.RowsEvent.RowSetEventType.*;
import static com.nqadmin.swingset.utils.CentralLookup.defLookup;
import static com.nqadmin.swingset.utils.SSUtils.objectID;
import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * Track some global state for a row set.
 * Access to some of this state is delegate to other classes in this package.
 */
public class RowSetState
{
	private boolean inserting;
	private boolean acceptingChanges;
	private boolean preInsertOps;
	//private NavigateState navigateState;
	private final WeakReference<RowSet> rsRef;

	private RowSetState(RowSet rs) {
		rsRef = new WeakReference<>(rs);
		debugRowSetListener = DebugRowSetListener.create(rs); // May be null.
	}
	// TODO: fix when case sensitivity handled in plugin.
	// Ignore case for column names as key.
	private final Map<String, Boolean> keys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	/**
	 * Determine if the column in our rowset is a primary key.
	 * Throw an exception if problem is encountered.
	 * 
	 * @param _columnName Must be a column in associated RowSet.
	 * @return true if primary key else false
	 */
	// TODO: isKey: base this on labelName, not columnName?
	private boolean isKey(String columnName)
	{
		if (columnName == null) // POSSIBLE?. IF POSSIBLE, WHY CAN'T IT BE A KEY?
			throw new IllegalArgumentException("null column name");
		RowSet rs = rsRef.get();
		if (rs == null)
			throw new IllegalStateException("must be a rowset");

		Boolean rv = keys.get(columnName);
		if (rv != null)
			return rv;
		// This method is frequently used, create a map entry for each column
		// Saving each RowSets column minimizes metadata access.
		// May want to cache more metadata info in the future.
		try {
			List<String> names = SSUtils.getPrimaryKeyInfoForTable(rs, columnName)
					.stream()
					.map((ki) -> ki.columnName())
					.toList();
			ResultSetMetaData rsMetaData = rs.getMetaData();
			for(int i = 1; i <= rsMetaData.getColumnCount(); i++) {
				String name = rsMetaData.getColumnName(i);
				keys.put(name, names.contains(name));
			}
			
		} catch (SQLException ex) {
			// TODO: isKey: random SQLEX report. No exception?
			throw new IllegalStateException(ex);
		}
		rv = keys.get(columnName);
		if (rv == null)
			throw new IllegalArgumentException(sf("Column not in RowSet: %s", columnName));
		return rv;
	}

	/**
	 * Find out if this RowSet is on the insert row.
	 * @return true if on the insert row
	 */
	private boolean isInserting() {
		return inserting;
	}

	/**
	 * Find out if this RowSet is
	 * a {@linkplain CachedRowSet} doing {@linkplain CachedRowSet#acceptChanges}.
	 * @return true if executing acceptingChanges
	 */
	private boolean isAcceptingChanges() {
		return acceptingChanges;
	}

	/**
	 * Is the current row of the RowSet dirty?
	 * @return is dirty
	 */
	private boolean isDirty() {
		return getNavigateState() != null && getNavigateState().undoRow.isDirty();
	}

	NavigateState getNavigateState() {
		return NavigateState.get(rsRef.get());
	}

	// TODO: make more stuff instance accessible.


	@SuppressWarnings("unused")
	private final RowSetListener debugRowSetListener; // Strong reference needed.
	private static class DebugRowSetListener implements RowSetListener {
		static RowSetListener create(RowSet rs)
		{
			if (defLookup(DebugRowSetListenerFlag.class) == null)
				return null;
			DebugRowSetListener l = new DebugRowSetListener();
			rs.addRowSetListener(WeakListeners.create(
					RowSetListener.class, l, rs));
			return l;
		}

		@Override
		public void rowSetChanged(RowSetEvent event)
		{
			String s = sf("DEBUG: %s: %s", ROW_SET_CHANGED, objectID(event.getSource()));
			System.err.println(s);
		}
		
		@Override
		public void rowChanged(RowSetEvent event)
		{
			String s = sf("DEBUG: %s: %s", ROW_CHANGED, objectID(event.getSource()));
			System.err.println(s);
		}
		
		@Override
		public void cursorMoved(RowSetEvent event)
		{
			String s = sf("DEBUG: %s: %s", CURSOR_MOVED, objectID(event.getSource()));
			System.err.println(s);
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// Instance above, static below

	// /**
	//  * Find the data navigator for the specified RowSet.
	//  * <p>
	//  * Originally added to support SSComponentInterface.getSSDataNavigator(),
	//  * see discussion #93,
	//  * but may come in handy when implementing ActionMap interface.
	//  * @param rs get information for this RowSet
	//  * @return the associated data navigator
	//  */
	// static NavigateState getNavigateState(RowSet rs) {
	// 	return rs == null ? null : getRowSetState(rs).getNavigateState();
	// }

	private static final Map<RowSet,RowSetState> rowSetState
			= new MapMaker().weakKeys().makeMap();
			//= new MapMaker().weakKeys().weakValues().makeMap();

	/**
	 * @return
	 */
	public static int count() {
		// Can't depend on size() method when weakKeys.
		return SSUtils.size(rowSetState);
	}

	/**
	 * Only use the returned RowSetState's methods while RowSet has a reference.
	 * @param rs
	 * @return RowSetState kept alive by RowSet reference
	 */
	public static RowSetState getRowSetState(RowSet rs) {
		return rowSetState.computeIfAbsent(rs, k -> new RowSetState(rs));
	}

	/**
	 * Only use the returned RowSetState's methods while RowSet has a reference.
	 * @param rs
	 * @return RowSetState kept alive by RowSet reference
	 */
	public static RowSetState getExistingRowSetState(RowSet rs) {
		return rowSetState.get(rs);
	}

	// /**
	//  * True if this is a known rowset.
	//  * @param rs
	//  * @return
	//  */
	// public static boolean hasRowSetState(RowSet rs) {
	// 	return rowSetState.containsKey(rs);
	// }

	static void setInserting(RowSet rs, boolean flag) {
		if (rs != null) {
			getRowSetState(rs).inserting = flag;
		}
	}

	// // NOTE: only invoked from one method which is syncronized.
	// static void setNavigateState(RowSet rs, NavigateState navState) {
	// 	if (rs != null) {
	// 		getRowSetState(rs).navigateState = navState;
	// 	}
	// }

	/**
	 * Determine if the column in our rowset is a primary key.
	 * Return false if problem is encountered.
	 * @param comp
	 * @return true if primary key else false
	 */
	public static boolean isKey(RSC comp) {
		return getRowSetState(comp.getRowSet()).isKey(comp.getBoundColumnName());
	}

	/**
	 * Is the current row of the RowSet dirty?
	 * @param rs
	 * @return is dirty
	 */
	public static boolean isDirty(RowSet rs) {
		//return navigateState != null && navigateState.undoRow.isDirty();
		return rs == null ? false : getRowSetState(rs).isDirty();
	}

	/**
	 * Find out if the specified RowSet is on the insert row.
	 * @param rs get state for this RowSet
	 * @return true if on the insert row
	 */
	public static boolean isInserting(RowSet rs) {
		return rs == null ? false : getRowSetState(rs).isInserting();
	}

	static void setPreInsertOps(RowSet rs, boolean flag) {
		if (rs != null) {
			getRowSetState(rs).preInsertOps = flag;
		}
	}

	static boolean isPreInsertOps(RowSet rs) {
		return rs == null ? false : getRowSetState(rs).preInsertOps;
	}

	/**
	 * Set or clear the state indicating that the CachedRowSet is accepting changes.
	 * @param _crs modify state for this.
	 * @param flag state set to this.
	 */
	private static void setAcceptingChanges(CachedRowSet _crs, boolean flag) {
		if (_crs != null) {
			getRowSetState(_crs).acceptingChanges = flag;
		}
	}

	/**
	 * Find out if the specified RowSet is
	 * a {@linkplain CachedRowSet} doing {@linkplain CachedRowSet#acceptChanges}.
	 * @param rs check this rowset
	 * @return true if executing acceptingChanges
	 */
	public static boolean isAcceptingChanges(RowSet rs) {
		return rs == null ? false : getRowSetState(rs).isAcceptingChanges();
	}

	/**
	 * A {@linkplain CachedRowSet} requires an extra step to effect changes
	 * in its underlying data source;
	 * this method does {@linkplain #acceptChanges(CachedRowSet, Runnable)} on the given {@linkplain CachedRowSet}.
	 * Set the state for the CachedRowSet so that it's listeners can ignore
	 * the extra events.
	 * The runnable {@code runAfterChanges}, if not null, is executed after
	 * acceptChanges on the CachedRowSet is successful.
	 * @param _crs accept change on this.
	 * @param runAfterChanges execute if not null
	 * @throws SQLException
	 */
	//
	// TODO: Flag to always runAfterChanges
	//		 Maybe instead of runnable,
	//		 something that contains the flag,
	//		 something that can throw SQLException
	//
	public static void acceptChanges(CachedRowSet _crs, Runnable runAfterChanges) throws SQLException {
		SQLException sqlEx = null;
		try {
			setAcceptingChanges(_crs, true);
			_crs.acceptChanges();
			//if (runAfterChanges != null) {
			//	runAfterChanges.run();		// assume if acceptChanges throws, don't need "code"
			//}
		} catch(SyncProviderException ex) {
			sqlEx = ex;
		} finally {
			if (runAfterChanges != null) {
				runAfterChanges.run();
			}
			setAcceptingChanges(_crs, false);
		}
		if (sqlEx != null)
			throw sqlEx;
	}
	
}
