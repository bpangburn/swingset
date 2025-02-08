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
 * copyright (C) 2024-2025, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset;

import javax.sql.RowSet;

import com.nqadmin.swingset.core.CheckBox;
import com.nqadmin.swingset.navigate.RowsModel;

import static com.nqadmin.swingset.utils.SSUtils.findRowsModel;

// SSCheckBox.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to display values stored in the database as a boolean.
 * The SSCheckBox can be bound to a numeric or boolean database column. 
 * The boolean value is converted to the data base type by the
 * {@linkplain #setBoundColumnObject(java.lang.Object) } infrastructure.
 * Currently, Dec 2024, if bound to a numeric database column, a checked
 * SSCheckBox puts a '1' to the database and an unchecked SSCheckBox puts a '0'.
 * <p>
 * TODO: In the future an option may be added to allow the user to specify the
 * values returned for the checked and unchecked SSCheckBox states.
 */
@SuppressWarnings("serial")
public class SSCheckBox extends CheckBox
{
	/**
	 * Creates an object of SSCheckBox.
	 */
	public SSCheckBox() {
		super();
	}

	/**
	 * Creates an object of SSCheckBox.
	 *
	 * @param _text Checkbox label
	 */
	public SSCheckBox(String _text) {
		super(_text);
	}
	
	/**
	 * Creates an object of SSCheckBox binding it to the specified column in the
	 * given RowSet.
	 *
	 * @param rowsModel        datasource to be used.
	 * @param boundColumnName name of the column to which this check box should be
	 */
	public SSCheckBox(RowsModel rowsModel, String boundColumnName) {
		super(rowsModel, boundColumnName);
	}
	
	/**
	 * Creates an object of SSCheckBox binding it to the specified column in the
	 * given RowSet.
	 *
	 * @param _rowSet        datasource to be used.
	 * @param _boundColumnName name of the column to which this check box should be
	 * @deprecated use RowsModel insted of RowSet
	 */
	@Deprecated
	public SSCheckBox(RowSet _rowSet, String _boundColumnName) {
		super(findRowsModel(_rowSet), _boundColumnName);
	}

} // end public class SSCheckBox
