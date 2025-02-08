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
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset;


import javax.sql.RowSet;

import com.nqadmin.swingset.core.TextField;
import com.nqadmin.swingset.navigate.RowsModel;

import static com.nqadmin.swingset.utils.SSUtils.findRowsModel;


/**
 * SSTextField extends the JTextField.
 */
@SuppressWarnings("serial")
public class SSTextField extends TextField
{
	/**
	 * Constructs a new, empty text field.
	 */
	public SSTextField() {
		super();
	}

	/**
	 * Constructs a new text field with the given text.
	 * @param _text initial text
	 */
	public SSTextField(String _text) {
		super(_text);
	}

	/**
	 * Creates a SSTextField instance and binds it to the specified RowSet column.
	 *
	 * @param rowsModel          datasource to be used
	 * @param _boundColumnName name of the column to which this label should be bound
	 */
	public SSTextField(RowsModel rowsModel, String _boundColumnName) {
		super(rowsModel, _boundColumnName);
	}

	/**
	 * Creates a SSTextField instance and binds it to the specified RowSet column.
	 *
	 * @param _rowSet          datasource to be used.
	 * @param _boundColumnName name of the column to which this label should be bound
	 * @deprecated use RowsModel insted of RowSet
	 */
	@Deprecated
	public SSTextField(final RowSet _rowSet, final String _boundColumnName) {
		super(findRowsModel(_rowSet), _boundColumnName);
	}
}
