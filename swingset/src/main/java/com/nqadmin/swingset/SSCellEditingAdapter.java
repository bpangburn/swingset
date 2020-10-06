/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 ******************************************************************************/

package com.nqadmin.swingset;

// SSCellEditingAdapter.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * This abstract adapter class is provided as a convenience for creating custom
 * SSCellEditing objects. Extend this class to create a SSCellEditing
 * implementation.
 * <p>
 * SSCellEditingAdapter defines empty functions so that the programmer can
 * define only the functions desired. Both isCellEditable() and
 * cellUpdateRequested() always return true.
 *
 * @deprecated Starting in 2.3.0+ use {@link SSCellEditing} instead.
 *
 */
@Deprecated
public abstract class SSCellEditingAdapter implements SSCellEditing {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -8081589658308592606L;

	/**
	 * This empty implementation always returns true. For description about the
	 * function look in SSCellEditing class.
	 */
	@Override
	public boolean isCellEditable(final int _row, final int _column) {
		return true;
	}

	/**
	 * This empty implementation always returns true. For description about the
	 * function look in SSCellEditing class.
	 */
	@Override
	public boolean cellUpdateRequested(final int _row, final int _column, final Object _oldValue, final Object _newValue) {
		return true;
	}

} // end public abstract class SSCellEditingAdapter

