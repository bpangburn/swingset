/* *****************************************************************************
 * Copyright (C) 2025, Ernie R Rael. All rights reserved.
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
 * ****************************************************************************/
package com.nqadmin.swingset.navigate;

import java.lang.System.Logger;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.swing.SwingUtilities;

import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.navigate.Utils.postRowSetUndoRedo;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * UndoRedo static commands.
 */
public enum UndoRedo
{
	/** Undo command */
	UNDO,
	/** Redo command */
	REDO;

	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	// UNDO/REDO NOTES
	//
	// Currently undo/redo can NOT be changed while running.
	// If/when changing is supported, it should only be changed
	// when going to a new row or insertRow. Any user interface to
	// enable/disable undo/redo should queue the change request
	// and then apply it during the transition to a new row.
	//
	// Part of undo/redo for insertRow is capturing the values set
	// by preInsertOps; the values are captured by the undo/redo
	// logic. So if going from disabled to enabled, the enable must
	// come before preInsertOps.

	/**
	 * Check if the specified {@linkplain RowSet} is currently enabled
	 * for undo/redo. The enable state may only change when the RowSet's
	 * current row changes.
	 * @param rs RowSet of interest
	 * @return true if undo/redo is OK
	 */
	public static boolean isUndoRedoEnabled(RowSet rs)
	{
		return true;
		//return !get(rs).isOnInsertRow();
	}

	/**
	 * Check if the specified {@linkplain RSC} is currently enabled
	 * for undo/redo. The enable state may only change when the RowSet's
	 * current row changes.
	 * <p>
	 * This method currently delegates to check the rowset. In the future
	 * it's possible that per column enable may be implemented.
	 * @param comp RSC of interest
	 * @return true if undo/redo is OK
	 */
	public static boolean isUndoRedoEnabled(RSC comp)
	{
		return isUndoRedoEnabled(comp.getRowSet());
	}

	// /**
	//  * Check if the rowSet's cursor is on a row or on the insert row.
	//  * @param rs rowset for this component
	//  * @return true if cursor on a row or insert row
	//  * @throws SQLException 
	//  */
	// public static boolean hasActiveRow(RowSet rs) throws SQLException
	// {
	// 	return rs.getRow() != 0
	// 			|| RowSetState.isInserting(rs);
	// }

	// /**
	//  * Check if the rowSet's cursor is on a row or on the insert row.
	//  * @param comp rowset for this component
	//  * @return true if cursor on a row or insert row
	//  * @throws SQLException 
	//  */
	// public static boolean hasActiveRow(RSC comp) throws SQLException
	// {
	// 	return hasActiveRow(comp.getRowSet());
	// }

	/**
	 * Make sure the column's undo/redo stack is initialized; the
	 * database value (an object) is the base.
	 * This must be used before any updates are done to the rowset.
	 * @param comp rowset/column
	 * @throws SQLException
	 */
	public static void captureInitialValue(SSComponentInterface comp)
			throws SQLException
	{
		if (!isUndoRedoEnabled(comp))
			return;
		NavigateActions navActs = NavigateActions.get(comp.getRowSet());
		navActs.undoRow.captureInitialValue(comp);
	}

	/**
	 * Return the current undo/redo value for the specified component.
	 * @param comp ssComponent
	 * @return current value
	 * @throws SQLException
	 */
	public static Object fetchCurrentValue(RSC comp)
			throws SQLException
	{
		if (!isUndoRedoEnabled(comp))
			throw new IllegalStateException("UNDO/REDO disabled");
		NavigateActions navActs = NavigateActions.get(comp.getRowSet());
		return navActs.undoRow.fetchCurrentValue(comp);
	}

	/**
	 * Perform the specified undo/redo cmd on the specified component.
	 * @param comp ssComponent
	 * @param cmd undo or redo
	 */
	public static void undoRedo(SSComponentInterface comp, UndoRedo cmd)
	{
		if (!isUndoRedoEnabled(comp))
			return;
		logger.log(DEBUG, () -> sf("%s: %s for %s", cmd,
				comp.getClass().getSimpleName(), comp.getBoundColumnName()));
		try {
			NavigateActions navActs = NavigateActions.get(comp.getRowSet());
			Object value = navActs.doUndoRedo(comp, cmd);
			// Wait until value propogates to the component.
			if (value != UndoCol.none)
				SwingUtilities.invokeLater(() -> {
					postRowSetUndoRedo(comp, value, !comp.allValidate().all());
				});
		} catch (SQLException ex) {
			logger.log(ERROR, sf("%s:", cmd), ex);
			// TODO: error dialog?
			//postRowSetUndoRedo(comp, UndoCol.none, true); // show error?
		}
	}

	/**
	 * Make the next undo/redo change goes into a new slot.
	 * @param comp ssComponent
	 */
	public static void newSlot(SSComponentInterface comp)
	{
		if (!isUndoRedoEnabled(comp))
			return;
		NavigateActions navActs = NavigateActions.get(comp.getRowSet());
		navActs.undoRow.focusChange(null);
	}

	/**
	 * Add a modification to the undo/redo stack.
	 * @param ev
	 * @throws SQLException
	 */
	public static void addUndoableChange(RowSetModificationEvent ev) throws SQLException
	{
		if (!isUndoRedoEnabled(ev.getSource()))
			return;
		NavigateActions navActs = NavigateActions.get(ev.getSource().getRowSet());
		navActs.undoRow.addChange(ev);
	}

}