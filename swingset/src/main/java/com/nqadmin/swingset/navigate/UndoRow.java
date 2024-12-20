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
package com.nqadmin.swingset.navigate;

import java.sql.SQLException;

import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.navigate.NavigateActions.UndoRedo;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

/**
 * A support class for undo/redo on a single row in a rowsest.
 * There is one UndowRow per NavigateActions instance (it is also per RowSet);
 * upon each change of row, UndoRow is cleared.
 * The base of the undo/redo stack is the value in the database
 * <p>
 * For a SSTextField, changes are squashed as separated by focus changes
 * or undo/redo actions. 
 */
final class UndoRow
{
	/** The undo/redo for the columns in a row; cols[0] is not used. */
	private UndoCol[] cols;

	/** Use this when going to a different row or row set. */
	void clear()
	{
		cols = null;
	}
	
	/**
	 * 
	 * @param me
	 * @throws SQLException 
	 */
	void addChange(RowSetModificationEvent me) throws SQLException
	{
		getCol(me.getSource()).addChange(me);
	}

	/** return the column involved in the modification event */
	private UndoCol getCol(RSC comp) throws SQLException
	{
		if (cols == null) {
			int n = RowSetOps.getColumnCount(comp.getRowSet());
			cols = new UndoCol[n + 1];	// cols[0] not used
		}
		int columnIdx = comp.getBoundColumnIndex();
		UndoCol col = cols[columnIdx];
		if (col == null) {
			col = new UndoCol(comp);
			cols[columnIdx] = col;
		}
		return col;
	}

	/** There's been a focus change, alert the active columns.
	 * ev may be null.
	 */
	void focusChange(FocusChangeEvent ev)
	{
		if (cols == null)
			return;
		for(UndoCol col : cols) {
			if (col != null)
				col.focusChange(ev);
		}
	}

	/** return true if there's a column value which is not from the database */
	boolean isDirty()
	{
		if (cols == null)
			return false;
		for(UndoCol col : cols) {
			if (col != null)
				if (col.isDirty())
					return true;
		}
		return false;
	}

	// /** return true if the specified column's value is not from the database */
	// boolean isDirty(int columnIndex)
	// {
	// 	if (cols == null)
	// 		return false;
	// 	UndoCol col = cols[columnIndex];
	// 	if (col != null)
	// 		return col.isDirty();
	// 	return false;
	// }

	/**
	 * Force the capture of the database column for the comp;
	 * does nothing if the column is already captured.
	 */
	void captureInitialValue(SSComponentInterface comp) throws SQLException
	{
		getCol(comp);
	}

	/** return the current Value for the comp: the last rowset.updateXxx. */
	Object fetchCurrentValue(RSC comp) throws SQLException
	{
		return getCol(comp).fetchCurrentValue();
	}

	/**
	 * Process the undo/redo command for the specified component.
	 * Forward the new value to the component.
	 * If there's nothing to do, for example UNDO but there has been
	 * no changes, then do nothing.
	 * 
	 * @param comp SSComponent to adjust
	 * @param cmd undo/redo
	 * @return new value (only for logging)
	 * @throws SQLException 
	 */
	Object doUndoRedo(SSComponentInterface comp, UndoRedo cmd) throws SQLException
	{
		if (cols == null)
			return UndoCol.none;
		UndoCol col = cols[comp.getBoundColumnIndex()];
		if (col == null)
			return UndoCol.none;
		Object value = col.findUndoRedoValue(cmd);
		if (value == UndoCol.none)
			SSUtils.beep();
		else
			comp.undoRedoUpdateObject(cmd, value);
		return value;
	}
}
