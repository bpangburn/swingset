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
 * copyright (C) 2024-2025, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.navigate;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;

/**
 * Actions for working with {@link RowSet}s; used with
 * {@link com.nqadmin.swingset.navigate.RowsActions}.
 */
// TODO: RowsAction not RowsAction???
public enum RowsAction
{
	/** Go to first record. */
	ACT_FIRST,
	/** Go to last record. */
	ACT_LAST,
	/** Go to next record. */
	ACT_NEXT,
	/** Go to previous record. */
	ACT_PREVIOUS,
	/** Commit current record to data base. */
	ACT_COMMIT,
	/** Undo changes to current record. */
	ACT_REVERT,
	/** Refresh record. */
	ACT_REFRESH,
	/** Add record. */
	ACT_ADD(true), // This action has no associated RowSet event.
	/** Delete record. */
	ACT_DELETE,
	/** A specialized action for "goto row number".
	 * This action has a Property, see {@link javax.swing.Action#getValue(java.lang.String)},
	 * NavigateActions.KEY_SPINNER_MODEL, whose value is a
	 * {@link javax.swing.SpinnerNumberModel} which models the ResultSet's current row.
	 */
	ACT_GOTOROW,
	;

	private final boolean forceEvent;

	private RowsAction()
	{
		forceEvent = false;
	}

	private RowsAction(boolean special)
	{
		forceEvent = special;
	}

	/**
	 * Flag means to create a {@link RowsEvent} for this {@code RowsAction}
	 * even if there is no associated {@link RowSetEvent}.
	 * 
	 * @return true means unconditionally create event
	 */
	public boolean forceEvent()
	{
		return forceEvent;
	}

	/**
	 * Following could be used by spinner as event's command to indicate that if
	 * already on the row don't need to do "rs.absolute()".
	 * But that might be the default behavior anyway, in which case this would
	 * be a no-op.
	*/
	public static final String OK_SKIP_CURSOR_MOVE = "MAY_SKIP_GOTOROW";
}
