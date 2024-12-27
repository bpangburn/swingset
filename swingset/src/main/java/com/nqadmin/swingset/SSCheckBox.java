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

import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.sql.RowSet;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

import java.lang.System.Logger;
import java.sql.JDBCType;
import java.util.EnumSet;
import java.util.EventListener;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import com.nqadmin.swingset.decorators.BorderDecorator;

import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.sql.JDBCType.*;
import static com.nqadmin.swingset.datasources.ConvertType.assertConvertFromJdbcType;

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
public class SSCheckBox extends JCheckBox implements SSComponentInterface
{
	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column
	 */
	protected class SSCheckBoxListener implements ItemListener
	{
		/** {@inheritDoc} */
		@Override
		public void itemStateChanged(final ItemEvent ie)
		{
			if (!checkRowOK())
				return;

			getSSCommon().removeRowSetListener();

			setBoundColumnObject(isSelected());

			getSSCommon().addRowSetListener();
		}
	} // end private class SSCheckBoxListener

	/** System Logger for component. */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Creates an object of SSCheckBox.
	 */
	public SSCheckBox() {
		this(null);
	}
	
	/**
	 * Creates an object of SSCheckBox binding it to the specified column in the
	 * given RowSet.
	 *
	 * @param _rowSet        datasource to be used.
	 * @param _boundColumnName name of the column to which this check box should be
	 *                         bound
	 */
	@SuppressWarnings("OverridableMethodCallInConstructor")
	public SSCheckBox(final RowSet _rowSet, final String _boundColumnName) {
		this(null);
		bind(_rowSet, _boundColumnName);
	}

	/**
	 * Creates an object of SSCheckBox.
	 *
	 * @param _text Checkbox label
	 */
	public SSCheckBox(final String _text) {
		super(_text);
		logger.log(DEBUG, () -> sf("original border: %s",
				BorderDecorator.asString(getBorder(), this)));
		// JCheckBox disables painting the borders.
		// Replace the JCheckBox border with an empty border.
		Border b = getBorder();
		if (b instanceof CompoundBorder cb) {
			Insets oInsets = toInsets(cb.getOutsideBorder());
			Insets iInsets = toInsets(cb.getInsideBorder());
			b = BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(
							oInsets.top, oInsets.left, oInsets.bottom, oInsets.right),
					BorderFactory.createEmptyBorder(
							iInsets.top, iInsets.left, iInsets.bottom, iInsets.right));
		} else {
			Insets i = getInsets();
			b = BorderFactory.createEmptyBorder(i.top, i.left, i.bottom, i.right);
		}
		setBorder(b);
		setBorderPainted(true);
		finishSSCommon();
	}

	private Insets toInsets(Border b)
	{
		return b.getBorderInsets(this);
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
					logger.log(DEBUG, () -> sf("%s: getBoundColumnText() - %s",getColumnForLog(), getBoundColumnText()));
					
					Boolean value = getBoundColumnObject(Boolean.class);
					setSelected(value == null ? false : value);
				}
				
				/** {@inheritDoc } */
				@Override
				protected SSCheckBoxListener getSSComponentListener() {
					return new SSCheckBoxListener();
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
		return sf("SSCheckBox{selected=%s, %s}",
				isSelected(), SSUtils.ssComponentToString(this));
	}

} // end public class SSCheckBox
