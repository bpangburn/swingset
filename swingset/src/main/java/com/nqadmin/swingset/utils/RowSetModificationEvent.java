/* *****************************************************************************
 * Copyright (C) 2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
package com.nqadmin.swingset.utils;

import java.util.EventObject;
import java.util.Objects;

import javax.sql.RowSet;

import static com.nqadmin.swingset.utils.SSUtils.objectID;

/**
 * Sent by a SSComponnent when it modifies its value.
 * Note, the value may not yet have been written to the row.
 */
//
// Maybe the base of Nav stuff should be RowSetEvent.
// Subclasses could have type, eg ERROR; type could event be enumSet.
// 
public class RowSetModificationEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	final private RowSet rowSet;
	final private boolean error;

	/**
	 * Create a modification event.
	 * @param source the component making the modification
	 * @param rs the rowSet being modified
	 */
	public RowSetModificationEvent(SSComponentInterface source, RowSet rs) {
		this(source, rs, false);
	}

	/**
	 * Create a modification event.
	 * @param source the component making the modification
	 * @param rs the rowSet being modified
	 * @param error true if the component value is in error
	 */
	public RowSetModificationEvent(SSComponentInterface source, RowSet rs, boolean error) {
		super(source);
		Objects.requireNonNull(rs);
		rowSet = rs;
		this.error = error;
	}

	/**
	 *
	 * {@inheritDoc }
	 */
	@Override
	public SSComponentInterface getSource() {
		return (SSComponentInterface) super.getSource();
	}

	/**
	 * Test if this event is for the specified rowSet.
	 * @param _rowSet check against this rowSet
	 * @return true if the event is for the specified rowSet
	 */
	public boolean matches(RowSet _rowSet) {
		return rowSet == _rowSet;
	}

	/**
	 * Test if this event's component's value is in error.
	 * @return true if in error.
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RowSetModificationEvent{")
				.append("source=").append(objectID(getSource()))
				.append(',')
				.append("rowSet=").append(objectID(rowSet))
				.append(',')
				.append("error=").append(error)
				.append('}');
		return sb.toString();
	}
}
