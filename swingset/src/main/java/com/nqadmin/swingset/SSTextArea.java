/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 ******************************************************************************/
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2025, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset;

import javax.sql.RowSet;

import com.nqadmin.swingset.core.TextArea;
import com.nqadmin.swingset.navigate.RowsModel;

import static com.nqadmin.swingset.utils.SSUtils.findRowsModel;

/**
 * SSTextArea extends the JTextArea to add RowSet binding.
 */
@SuppressWarnings("serial")
public class SSTextArea extends TextArea {

	/**
	 * Empty constructor needed for deserialization.
	 */
	public SSTextArea() {
	}

	/**
	 * Constructs a new empty SSTextArea with the specified number of rows and
	 * columns.
	 *
	 * @param _rows    {@literal the number of rows >= 0}
	 * @param _columns {@literal the number of columns >= 0}
	 */
	public SSTextArea(final int _rows, final int _columns) {
		super(_rows, _columns);
	}

	/**
	 * Creates a multi-line text box and binds it to the specified RowSet column.
	 *
	 * @param rowsModel          datasource to be used.
	 * @param boundColumnName name of the column to which this text area should be
	 *                         bound
	 */
	public SSTextArea(RowsModel rowsModel, String boundColumnName) {
		super(rowsModel, boundColumnName);
	}

	/**
	 * Creates a multi-line text box and binds it to the specified RowSet column.
	 *
	 * @param _rowSet          datasource to be used.
	 * @param _boundColumnName name of the column to which this text area should be
	 *                         bound
	 * @deprecated use RowsModel insted of RowSet
	 */
	@Deprecated
	public SSTextArea(final RowSet _rowSet, final String _boundColumnName) {
		super(findRowsModel(_rowSet), _boundColumnName);
	}

} // end public class SSTextArea extends JTextArea {
