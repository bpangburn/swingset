/*
 * Portions created by Ernie Rael are
 * Copyright (C) 2024 Ernie Rael.  All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Ernie Rael <errael@raelity.com>
 */
package com.nqadmin.swingset.demo.datepicker;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

import java.lang.System.Logger;
import java.sql.JDBCType;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.EventListener;

import javax.sql.RowSet;

import static com.nqadmin.swingset.utils.SSUtils.sf;
import static com.nqadmin.swingset.datasources.ConvertType.assertConvertFromJdbcType;

import static java.lang.System.Logger.Level.DEBUG;
import static java.sql.JDBCType.DATE;

/**
 * Example of building a component that interoperates with SS but is not
 * in the SS library.
 */
@SuppressWarnings("serial")
public class DbDatePicker extends DatePicker implements SSComponentInterface
{
	private class DbDatePickerListener implements EventListener,DateChangeListener {
		/** {@inheritDoc} */
		@Override
		public void dateChanged(final DateChangeEvent dce)
		{
			if (!checkRowOK())
				return;

			getSSCommon().removeRowSetListener();

			setBoundColumnObject(dce.getNewDate());

			getSSCommon().addRowSetListener();
		}
	}
	/** System Logger for component. */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Create date picker.
	 */
	public DbDatePicker()
	{
		//setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		finishSSCommon();
	}

	/**
	 * Create date picker and bind it to the specified column in the
	 * given RowSet.
	 *
	 * @param rowSet        datasource to be used.
	 * @param boundColumnName name of the column to which this check box should
	 *                        be bound
	 */
	public DbDatePicker(RowSet rowSet, String boundColumnName)
	{
		this();
		bind(rowSet, boundColumnName);
	}

	/** {@inheritDoc} */
	@Override
	public void metadataChange() {
		getSettings().setAllowEmptyDates(getAllowNull());
	}

	/** {@inheritDoc } */
	@Override
	public void checkColumnType(JDBCType jdbcType) throws IllegalArgumentException
	{
		assertConvertFromJdbcType(jdbcType, LocalDate.class, EnumSet.of(DATE));
	}
	
	/** {@inheritDoc } */
	@Override
	public void cleanField()
	{
		if (getAllowNull()) {
			clear();
		} else {
			setDateToToday();
		}
	}

	private Hook hook;

	/** {@inheritDoc } */
	@Override
	public final Hook getSSComponentHook()
	{
		if (hook == null)
			hook = new Hook(this) {
				@Override
				protected void updateSSComponent()
				{
					logger.log(DEBUG, () -> sf("%s: getBoundColumnText() - %s",getColumnForLog(), getBoundColumnText()));
					LocalDate value = getBoundColumnObject(LocalDate.class);
					setDate(value);
				}
				
				@Override
				protected EventListener getSSComponentListener()
				{
					return new DbDatePickerListener();
				}
				
				@Override
				protected void addSSComponentListener(EventListener eventListener)
				{
					addDateChangeListener((DateChangeListener) eventListener);
				}
				
				@Override
				protected void removeSSComponentListener(EventListener eventListener)
				{
					removeDateChangeListener((DateChangeListener) eventListener);
				}
			};
		return hook;
	}
}
