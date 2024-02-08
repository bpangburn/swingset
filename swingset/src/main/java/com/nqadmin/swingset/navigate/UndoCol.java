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

import javax.sql.RowSet;

import com.nqadmin.swingset.navigate.NavigateActions.UndoRedo;
import com.nqadmin.swingset.utils.SSComponentInterface;

/**
 * This class holds the undo/redo data for a single column.
 * After a column gets focus, the next change adds a new current value.
 * When a change comes in, the change replaces the current value
 * and all values after the current value are discarded.
 */
// TODO: 
final class UndoCol
{
	/** One item per change, changes.get(0) is the current value in the database. */
	private final List<Object> changes = new ArrayList<>(3);
	/** The index of the previous Value. */
	private int curIdx;
	/**
	 * When set, the next change starts a new item on the undo stack.
	 * Set to true when column gets focus, or column value changed by undo/redo.
	 */
	private boolean needNewSlot;

	/**
	 * Create UndoCol and initialize undo/redo stack from database value.
	 */
	private UndoCol(RowSet rs, int columnIdx) throws SQLException
	{
		changes.add(rs.getObject(columnIdx));
		curIdx = 0;
		needNewSlot = true;
	}

	/**
	 * Create UndoCol based on an SSComponent;
	 * @param comp ssComponent
	 */
	UndoCol(SSComponentInterface comp) throws SQLException
	{
		this(comp.getRowSet(), comp.getBoundColumnIndex());
	}

	/** Check if there a undo (previous) value. */
	boolean hasPrev()
	{
		return curIdx > 0;
	}

	/** Return the undo (prevValue). MUST check hasPrev() first. */
	private Object prevValue()
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
	private Object nextValue()
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
		if (needNewSlot) {
			// First modification after a focus change.
			// Put change in a new spot.
			++curIdx;
			if (curIdx >= changes.size())
				changes.add(null); // need a new slot
			needNewSlot = false;
		}
		assert curIdx > 0 : "can not be on the original value";
		// Clear possible entries after where the next entry goes.
		// Start clearing from the end, going backwards, to avoid moving data.
		for (int idx = changes.size() - 1; idx > curIdx; --idx)
			changes.remove(idx);
		assert curIdx == changes.size() - 1 : "curIdx must be last item";

		changes.set(curIdx, me.getValue());
		NavigateActions.getLogger().debug(() -> String.format("UNDO/REDO change: %s - %s", me.getColumnName(), changes));
	}

	boolean isDirty()
	{
		return curIdx != 0;
	}

	static final Object none = new Object();
	Object findUndoRedoValue(UndoRedo cmd)
	{
		return switch(cmd) {
		case UNDO -> hasPrev() ? prevValue() : none;
		case REDO -> hasNext() ? nextValue() : none;
		};
	}

	/**
	 * The current value may be what's in the data base, if curIdx is 0;
	 * or it may be the value from a modification event.
	 * @return value
	 */
	Object fetchCurrentValue()
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

}
