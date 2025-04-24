/* *****************************************************************************
 * Copyright (C) 2024, Ernie R Rael. All rights reserved.
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


import javax.sql.RowSet;

import com.nqadmin.swingset.utils.SSComponentInterface;

/**
 * Sent by an SSComponent when value changes from undo/redo.
 * The contents of the undo/redo stack is unchanged; note the error.
 */
@SuppressWarnings("serial")
public class RowSetUndoRedoEvent extends EventObjectBacktrace
{
	final private Object value;
	final private boolean error;


	/**
	 * Create a undo/redo stack event.
	 * Signals change in components current value and if newValue is an error.
	 * @param source the component making the modification
	 * @param value the value written to the rowSet
	 * @param error true if the component value is in error
	 */
	public RowSetUndoRedoEvent( SSComponentInterface source, Object value,
							   boolean error)
	{
		super(source);
		this.value = value;
		this.error = error;
	}

	/**
	 * Test if this event is for the specified rowSet.
	 * @param _rowSet check against this rowSet
	 * @return true if the event is for the specified rowSet
	 */
	public boolean matches(RowSet _rowSet) {
		return getSource().getRowSet() == _rowSet;
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
	 * Value written to rowSet.
	 * @return value
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Test if this event's component's value is in error.
	 * @return true if in error.
	 */
	public boolean isError() {
		return error;
	}
}
