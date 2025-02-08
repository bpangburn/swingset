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

import com.nqadmin.swingset.core.Slider;
import com.nqadmin.swingset.navigate.RowsModel;

import static com.nqadmin.swingset.utils.SSUtils.findRowsModel;

/**
 * Used to link a JSlider to a numeric column in a database.
 */
@SuppressWarnings("serial")
public class SSSlider extends Slider {

	/**
	 * Empty constructor needed for deserialization. Creates a horizontal slider
	 * with the range 0 to 100.
	 */
	public SSSlider() {
	}

	/**
	 * Creates a slider using the specified orientation with the range 0 to 100.
	 *
	 * @param _orientation slider spatial orientation
	 */
	public SSSlider(final int _orientation) {
		super(_orientation);
	}

	/**
	 * Creates a horizontal slider using the specified min and max.
	 *
	 * @param _min minimum slider value
	 * @param _max maximum slider value
	 */
	public SSSlider(final int _min, final int _max) {
		super(_min, _max);
	}

	/**
	 * Creates a horizontal slider with the range 0 to 100 and binds it to the
	 * specified RowSet column.
	 *
	 * @param rowsModel          datasource to be used.
	 * @param boundColumnName name of the column to which this slider should be
	 *                         bound
	 * @throws java.sql.SQLException SQLException
	 */
	public SSSlider(RowsModel rowsModel, String boundColumnName) throws java.sql.SQLException {
		super(rowsModel, boundColumnName);
	}

	/**
	 * Creates a horizontal slider with the range 0 to 100 and binds it to the
	 * specified RowSet column.
	 *
	 * @param _rowSet          datasource to be used.
	 * @param _boundColumnName name of the column to which this slider should be
	 *                         bound
	 * @throws java.sql.SQLException SQLException
	 * @deprecated use RowsModel insted of RowSet
	 */
	@Deprecated
	public SSSlider(final RowSet _rowSet, final String _boundColumnName) throws java.sql.SQLException {
		super(findRowsModel(_rowSet), _boundColumnName);
	}
} // end public class SSSlider extends JSlider
