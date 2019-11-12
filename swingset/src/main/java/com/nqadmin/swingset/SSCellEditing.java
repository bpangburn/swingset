/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

import java.io.Serializable;

/**
 * SSCellEditing.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * The SSCellEditing interface specifies the methods the SSTableModel will use
 * to determine whether or not a given cell can be edited or if a user-specified
 * value for a cell is valid or invalid.
 */
public interface SSCellEditing extends Serializable {

	/**
	 * unique serial id
	 */
	static final long serialVersionUID = -8081589343308592606L;

	/**
	 * Returns true if the cell at row _row and at column _column is editable else
	 * false.
	 *
	 * SSTableModel first looks in to uneditable columns, if the column is not in
	 * the uneditable columns list then this function is called (If SSCellEditing is
	 * implemented).
	 *
	 * @param _row    the row to which the cell belongs.
	 * @param _column the column to which the cell belongs.
	 *
	 * @return returns true is the cell is editable else false.
	 */
	default boolean isCellEditable(int _row, int _column) {
		return true;
	}

	/**
	 * This function is called when ever a update to a cell is done but before the
	 * value is updated in the database.<BR>
	 * If the function returns false the update is cancelled, if it returns true the
	 * value will be updated in the database.<BR>
	 *
	 * @param _row      the row in which update is taking place.
	 * @param _column   the column at which update is taking place.
	 * @param _oldValue the present value in the cell being edited.
	 * @param _newValue the new value entered in the cell being edited.
	 *
	 * @return returns true if update should be made else false.
	 */
	default boolean cellUpdateRequested(int _row, int _column, Object _oldValue, Object _newValue) {
		return true;
	}

} // end public interface SSCellEditing {

/*
 * $Log$ Revision 1.6 2005/02/04 22:48:52 yoda2 API cleanup & updated Copyright
 * info.
 *
 * Revision 1.5 2004/11/11 14:45:33 yoda2 Using TextPad, converted all tabs to
 * "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.4 2004/08/10 22:06:59 yoda2 Added/edited JavaDoc, made code layout
 * more uniform across classes, made various small coding improvements suggested
 * by PMD.
 *
 * Revision 1.3 2004/03/08 16:43:37 prasanth Updated copyright year.
 *
 * Revision 1.2 2003/12/18 20:12:40 prasanth Update class description.
 *
 * Revision 1.1 2003/12/16 18:02:47 prasanth Initial version.
 *
 */