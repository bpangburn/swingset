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
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.JTextComponent;

import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.navigate.UndoRedo.Change;

import static com.nqadmin.swingset.navigate.RowSetState.isPreInsertOps;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * This class holds the undo/redo data for a single column.
 * After a column gets focus, the next change adds a new current value.
 * When a change comes in, the change replaces the current value
 * and all values after the current value are discarded.
 * <p>
 * The database is read for the initial value when this is created,
 * except for the insertRow.
 */
final class UndoCol
{
	/** One item per change, changes.get(0) is the current value in the database;
	 * or the value from preInsertOps. */
	private final List<Change> changes;
	/** The index of the previous Value. */
	private int curIdx;
	/**
	 * When set, the next change starts a new item on the undo stack.
	 * Set to true when column gets focus, or column value changed by undo/redo.
	 */
	private boolean needNewSlot;

	/**
	 * Create an empty UndoCol.
	 */
	private UndoCol()
	{
		changes = new ArrayList<>(3);
		curIdx = 0;
		needNewSlot = true;
	}

	/**
	 * Create UndoCol; initialize undo/redo stack from the value.
	 * @param change
	 */
	UndoCol(Change change)
	{
		this();
		changes.add(change);
	}

	/**
	 * Create UndoCol; initialize undo/redo stack from comp's database value;
	 * @param comp RowSet Column
	 */
	UndoCol(RSC comp) throws SQLException
	{
		this(new Change(initialValue(comp), false));
	}

	private static Object initialValue(RSC comp) throws SQLException
	{
		//return isPreInsertOps(comp.getRowSet())
		//		? null : comp.getRowSet().getObject(comp.getBoundColumnIndex());

		// If doing preInsertOps, just use a null for the initial value
		// (special case text field); the real value is on the way.
		if (isPreInsertOps(comp.getRowSet())) {
			if (comp instanceof JTextComponent)
				return comp.getAllowNull() ? null : "";
			else
				return null;
		} else
			return RowSetOps.getColumnDirect(comp);
	}

	/** Check if there is an undo (previous) value. */
	boolean hasPrev()
	{
		return curIdx > 0;
	}

	/** Return the undo (prevValue). MUST check hasPrev() first. */
	private Change prevValue()
	{
		needNewSlot = true;
		--curIdx;
		return changes.get(curIdx);
	}

	/** Check if there a redo (next) value. */
	boolean hasNext()
	{
		return curIdx < changes.size() - 1;
	}

	/** Return the redo (nextValue. MUST check hasNext() first. */
	private Change nextValue()
	{
		needNewSlot = true;
		++curIdx;
		return changes.get(curIdx);
	}

	/**
	 * Update the undo history with a change.
	 * A bunch of changes in a row, go to the same slot.
	 * On the first change for the column grab the database value as an object.
	 * @param me modification data
	 */
	void addChange(RowSetModificationEvent me) throws SQLException
	{
		// TODO: only do this if null?
		// Don't push something on the top if it equals what's on the top.
		Change newChange = new Change(me.getValue(), me.isError());
		if (fetchCurrentChange().equals(newChange))
			return;

		if (needNewSlot) {
			// First modification after a focus change or change that needs new slot.
			// Put change in a new spot.
			++curIdx;
			if (curIdx >= changes.size())
				changes.add(null); // need a new slot
			needNewSlot = false;
		}
		assert curIdx > 0 : "can not be on the original value";

		// Remove entries after where the next entry goes.
		changes.subList(curIdx+1, changes.size()).clear();
		assert curIdx == changes.size() - 1 : "curIdx must be last item";

		changes.set(curIdx, newChange);
		NavigateState.getLogger().log(DEBUG, () -> sf("UNDO/REDO change: %s - %s", me.getColumnName(), changes));
	}

	boolean isDirty()
	{
		return curIdx != 0;
	}

	Change findUndoRedoChange(UndoRedo cmd)
	{
		return switch(cmd) {
		case UNDO -> hasPrev() ? prevValue() : UndoRedo.NO_CHANGE;
		case REDO -> hasNext() ? nextValue() : UndoRedo.NO_CHANGE;
		};
	}

	/**
	 * The current value may be what's in the data base, if curIdx is 0;
	 * or it may be the value from a modification event.
	 * @return value
	 */
	Change fetchCurrentChange()
	{
		return changes.get(curIdx);
	}

	/**
	 * Handle focus change.
	 * @param ev event may be null
	 */
	void focusChange(FocusChangeEvent ev)
	{
		needNewSlot = true;
	}

	@Override
	public String toString()
	{
		return sf("UndoCol{curIdx=%s, needNewSlot=%s, nChanges %d, changes=%s}",
				curIdx, needNewSlot, changes.size(), changes);
	}
}
