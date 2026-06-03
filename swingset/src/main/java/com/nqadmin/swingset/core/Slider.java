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
 * copyright (C) 2024-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.core;


import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.EventListener;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.datasources.ConvertType.assertConvertFromJdbcType;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.sql.JDBCType.*;

/**
 * Used to link a JSlider to a numeric column in a database.
 */
@SuppressWarnings("serial")
public class Slider extends JSlider implements SSComponent
{
	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * text field.
	 */
	protected class SliderListener implements ChangeListener {

		/** {@inheritDoc } */
		@Override
		public void stateChanged(ChangeEvent ce)
		{
			// While adjusting don't need to update the database.
			if (getValueIsAdjusting())
				return;

			try {
				dbChange(() -> setColumnObject(getValue()));
			} catch (SQLException ex) {
				logger.log(Level.ERROR, (String) null, ex);
			}
		}

	} // end protected class SliderListener implements ChangeListener, Serializable

	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Empty constructor needed for deserialization. Creates a horizontal slider
	 * with the range 0 to 100.
	 */
	public Slider() {
		finishSSCommon();
	}

	/**
	 * Creates a slider using the specified orientation with the range 0 to 100.
	 *
	 * @param orientation slider spatial orientation
	 */
	public Slider(int orientation) {
		super(orientation);
		finishSSCommon();
	}

	/**
	 * Creates a horizontal slider using the specified min and max.
	 *
	 * @param min minimum slider value
	 * @param max maximum slider value
	 */
	public Slider(int min, int max) {
		super(min, max);
		finishSSCommon();
	}

	/**
	 * Creates a horizontal slider with the range 0 to 100 and binds it to the
	 * specified RowSet column.
	 *
	 * @param rowsModel          datasource to be used.
	 * @param columnName name of the column to which this slider should be
	 *                         bound
	 * @throws java.sql.SQLException SQLException
	 */
	public Slider(RowsModel rowsModel, String columnName) throws java.sql.SQLException {
		this();
		rowsModel.bind(this, columnName);
	}

	/** {@inheritDoc } */
	@Override
	public void checkColumnType(JDBCType jdbcType) throws IllegalArgumentException
	{
		// only allow JDBC types that convert to numeric types
		assertConvertFromJdbcType(jdbcType, Integer.class,
				EnumSet.of(INTEGER, SMALLINT, TINYINT,
						BIGINT, REAL, FLOAT, DOUBLE, DECIMAL, NUMERIC));

	}

	/** {@inheritDoc } */
	@Override
	public void cleanField()
	{
		// Slider to the middle.
		setValue((getMinimum() + getMaximum()) / 2);
	}

	private Hook hook;

	/** {@inheritDoc } */
	@Override
	public final Hook getSSComponentHook()
	{
		if (hook == null)
			hook = new Hook(this) {
				/** {@inheritDoc } */
				@Override
				protected void updateSSComponent()
				{
					try {
						Integer n = getColumnObject(Integer.class);
						setValue(n != null ? n : 0);
					} catch (final NumberFormatException _nfe) {
						// TODO: Hmm, probably should be an SQL conversion error.
						// Output the text value
						String columnValue = getColumnText();
						logger.log(Level.ERROR, getColumnForLog() + ": Number Format Exception. Cannot update slider to " + columnValue,
								_nfe);
					}
				}
				
				/** {@inheritDoc } */
				@Override
				protected SliderListener getSSComponentListener() {
					return new SliderListener();
				}
				
				/** {@inheritDoc } */
				@Override
				protected void addSSComponentListener(EventListener eventListener)
				{
					addChangeListener((ChangeListener) eventListener);
				}
				
				/** {@inheritDoc } */
				@Override
				protected void removeSSComponentListener(EventListener eventListener)
				{
					removeChangeListener((ChangeListener) eventListener);
				}
				
			};
		return hook;
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("%s{value=%s, %s}", getClass().getSimpleName(),
				getValue(), SSUtils.ssComponentToString(this));
	}
} // end public class Slider extends JSlider
