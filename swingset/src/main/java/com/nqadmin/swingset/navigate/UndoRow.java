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

import java.io.PrintStream;
import java.sql.SQLException;

import javax.sql.RowSet;

import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.navigate.UndoRedo.Change;
import com.nqadmin.swingset.utils.SSComponent;


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

	private void setupCols(RowSet rowSet) throws SQLException
	{
		if (cols == null) {
			int n = RowSetOps.getColumnCount(rowSet);
			cols = new UndoCol[n + 1];	// cols[0] not used
		}
	}

	/**
	 * Moving to the insertRow; capture preIsertOps' values from the undo/redo stack.
	 * cols is fully built (not lazily).
	 * See comments in NavigateActions.
	 */
	// TODO: might be handy to have access to each column's component,
	//		 but that's a whole new thing. And don't want to prevent
	//		 multiple components from binding to the same column.
	//		 So access to at least one of a column's components.
	void clearInsertRow(RowSet rowSet)
	{
		try {
			setupCols(rowSet);
		} catch (SQLException ex) {
			// TODO: random exception strategy
			System.getLogger(UndoRow.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
		}
		//dump(cols);
		UndoCol[] insertRowCols = new UndoCol[cols.length];
		for (int i = 1; i < cols.length; i++) {
			UndoCol prevCol = cols[i];
			// if (prevCol == null)	// not so impossible
			// 	NavigateActions.getLogger().log(ERROR,
			// 			"clearInsertRow has null column; impossible");

			// TODO: prevCol null, where/how to get initial value
			insertRowCols[i] = new UndoCol(prevCol != null
					? prevCol.fetchCurrentChange()
					: new UndoRedo.Change(null, false));
		}
		cols = insertRowCols;
		//dump(cols);
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
		setupCols(comp.getRowSet());
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

	boolean isDirty(SSComponent comp)
	{
		if (cols == null)
			return false;
		UndoCol col = cols[comp.getBoundColumnIndex()];
		return col != null && col.isDirty();
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
	void captureInitialValue(RSC comp) throws SQLException
	{
		// TODO: have a fast path check if column already captured. java.util.BitSet?
		//       Also want fast path to the undoRow from the component
		getCol(comp);
	}

	/** return the current Value for the comp: the last rowset.updateXxx. */
	Change fetchCurrentChange(RSC comp) throws SQLException
	{
		return getCol(comp).fetchCurrentChange();
	}

	/**
	 * Process the undo/redo command for the specified component.
	 * Forward the new value to the component.
	 * If there's nothing to do, for example UNDO but there has been
	 * no changes, then do nothing.
	 * 
	 * @param comp rowset/col to adjust
	 * @param cmd undo/redo
	 * @return new value (only for logging)
	 * @throws SQLException 
	 */
	Change undoRedoChange(RSC comp, UndoRedo cmd) throws SQLException
	{
		if (cols == null)
			return UndoRedo.NO_CHANGE;
		UndoCol col = cols[comp.getBoundColumnIndex()];
		if (col == null)
			return UndoRedo.NO_CHANGE;
		return col.findUndoRedoChange(cmd);
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void dump(UndoCol[] cols)
	{
		dump(cols, System.err);
	}
	public static void dump(UndoCol[] cols, PrintStream out)
	{
		out.printf("UndoRow with %d columns\n", cols.length - 1);
		for (int i = 1; i < cols.length; i++) {
			UndoCol col = cols[i];
			out.printf("    col %d: %s\n", i, col != null ? col.toString() : null);
		}
	}
}
