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
 * copyright (C) 2025-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.core;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.EventListener;

import javax.swing.JCheckBox;
import javax.swing.border.Border;

import com.nqadmin.swingset.decorators.BorderDecorator;
import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.datasources.ConvertType.assertConvertFromJdbcType;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.sql.JDBCType.*;

/**
 * Used to display values stored in the database as a boolean.
 * The CheckBox can be bound to a numeric or boolean database column. 
 * The boolean value is converted to the data base type by the
 * {@linkplain #setBoundColumnObject(java.lang.Object) } infrastructure.
 * Currently, Dec 2024, if bound to a numeric database column, a checked
 * CheckBox puts a '1' to the database and an unchecked CheckBox puts a '0'.
 * <p>
 * TODO: In the future an option may be added to allow the user to specify the
 * values returned for the checked and unchecked CheckBox states.
 */
@SuppressWarnings("serial")
public class CheckBox extends JCheckBox implements SSComponent
{
	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column
	 */
	protected class CheckBoxListener implements ItemListener
	{
		/** {@inheritDoc} */
		@Override
		public void itemStateChanged(final ItemEvent ie)
		{
			try {
				dbChange(() -> setBoundColumnObject(isSelected()));
			} catch (SQLException ex) {
				logger.log(Level.ERROR, (String) null, ex);
			}
		}
	}

	/** System Logger for component. */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Creates an object of CheckBox.
	 */
	public CheckBox() {
		this(null);
	}
	
	/**
	 * Creates an object of CheckBox binding it to the specified column in the
	 * given RowSet.
	 *
	 * @param rowsModel        model for a rowSet
	 * @param boundColumnName name of the column to which this check box should be
	 *                         bound
	 */
	public CheckBox(RowsModel rowsModel, final String boundColumnName) {
		this(null);
		rowsModel.bind(this, boundColumnName);
	}

	/**
	 * Creates an object of CheckBox.
	 *
	 * @param _text Checkbox label
	 */
	public CheckBox(final String _text) {
		super(_text);
		logger.log(Level.DEBUG, () -> sf("original border: %s",
				BorderDecorator.asString(getBorder(), this)));
		// JCheckBox disables painting the borders.
		// Replace the JCheckBox border with an empty border.
		Border b = BorderDecorator.createEmptyBorder(this);
		setBorder(b);
		setBorderPainted(true);
		finishSSCommon();
	}

	/** {@inheritDoc } */
	@Override
	public void checkColumnType(JDBCType jdbcType) throws IllegalArgumentException
	{
		assertConvertFromJdbcType(jdbcType, Boolean.class,
				EnumSet.of(BIT, BOOLEAN, INTEGER, SMALLINT, TINYINT));
	}

	/** {@inheritDoc } */
	@Override
	public void cleanField()
	{
		setSelected(false);
	}

	private Hook hook;

	/** {@inheritDoc } */
	@Override
	public final Hook getSSComponentHook()
	{
		if (hook == null)
			hook = new Hook(this) {
				/**
				 * Updates the value stored and displayed in the SwingSet component
				 * based on getBoundColumnText()
				 */
				@Override
				protected void updateSSComponent()
				{
					logger.log(Level.DEBUG, () -> sf("%s: getBoundColumnText() - %s",getColumnForLog(), getBoundColumnText()));
					
					Boolean value = getBoundColumnObject(Boolean.class);
					setSelected(value == null ? false : value);
				}
				
				/** {@inheritDoc } */
				@Override
				protected CheckBoxListener getSSComponentListener() {
					return new CheckBoxListener();
				}
				
				/** {@inheritDoc } */
				@Override
				protected void addSSComponentListener(EventListener eventListener)
				{
					addItemListener((ItemListener) eventListener);
				}
				
				/** {@inheritDoc } */
				@Override
				protected void removeSSComponentListener(EventListener eventListener)
				{
					removeItemListener((ItemListener) eventListener);
				}
				
			};
		return hook;
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("CheckBox{selected=%s, %s}",
				isSelected(), SSUtils.ssComponentToString(this));
	}

} // end public class CheckBox
