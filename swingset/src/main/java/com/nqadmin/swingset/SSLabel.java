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
import javax.swing.Icon;

import com.nqadmin.swingset.core.Label;
import com.nqadmin.swingset.navigate.RowsModel;

import static com.nqadmin.swingset.utils.SSUtils.findRowsModel;

// SSLabel.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to display database values in a read-only JLabel.
 * By default, programmatic changes to the label are not propagated,
 * except of course to set a label's value from a RowSet.
 */
@SuppressWarnings("serial")
public class SSLabel extends Label
{

	/**
	 * Empty constructor needed for deserialization. Creates a SSLabel instance with
	 * no image and no text.
	 */
	public SSLabel() {
	}

	/**
	 * Creates a SSLabel instance with the specified image.
	 *
	 * @param _image specified image for label
	 */
	public SSLabel(final Icon _image) {
		super(_image);
	}

	/**
	 * Creates a SSLabel instance with the specified image and horizontal alignment.
	 *
	 * @param _image               specified image for label
	 * @param _horizontalAlignment horizontal alignment
	 */
	public SSLabel(final Icon _image, final int _horizontalAlignment) {
		super(_image, _horizontalAlignment);
	}

	/**
	 * Creates a SSLabel instance with no image and binds it to the specified RowSet
	 * column.
	 *
	 * @param rowsModel          datasource to be used.
	 * @param boundColumnName name of the column to which this label should be
	 *                         bound
	 */
	public SSLabel(RowsModel rowsModel, String boundColumnName) {
		super(rowsModel, boundColumnName);
	}

	/**
	 * Creates a SSLabel instance with no image and binds it to the specified RowSet
	 * column.
	 *
	 * @param _rowSet          datasource to be used.
	 * @param _boundColumnName name of the column to which this label should be
	 *                         bound
	 * @deprecated use RowsModel insted of RowSet
	 */
	@Deprecated
	public SSLabel(final RowSet _rowSet, final String _boundColumnName) {
		super(findRowsModel(_rowSet), _boundColumnName);
	}
} // end public class SSLabel extends JLabel {
