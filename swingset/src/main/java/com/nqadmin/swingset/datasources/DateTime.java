/* *****************************************************************************
 * Copyright (C) 2024, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 * ****************************************************************************/
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.datasources;

import java.lang.System.Logger;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.JTextComponent;

import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

import static java.lang.System.Logger.Level.*;
import static java.sql.JDBCType.DATE;
import static java.sql.JDBCType.TIME;
import static java.sql.JDBCType.TIMESTAMP;

import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * Utility class for Date/Time handling. Parsing/formatting SQL
 * Date/Time/TimeStamp to/from text.
 */
public class DateTime
{
	private DateTime() { }

	private static final Logger logger = SSUtils.getLogger();

	private static final Map<JDBCType, List<DateTimeFormatter>> dateTimeParsersMap
			= new EnumMap<>(JDBCType.class);
	private static final Map<JDBCType, DateTimeFormatter> dateTimeFormatterMap
			= new EnumMap<>(JDBCType.class);

	private static final boolean USE_MDY = true;

	/**
	 * Check if column is a handled; must be JDBC date/type and text component.
	 * @param comp
	 * @return true is OK
	 */
	public static boolean isHandledDateTimeComp(SSComponentInterface comp)
	{
		return comp instanceof JTextComponent jtc
				&& dateTimeHandled.contains(comp.getBoundColumnJDBCType());
	}

	/**
	 * Check if jdbcType is handled; must be JDBC date/type.
	 * @param jdbcType
	 * @return true is OK
	 */
	public static boolean isHandledDateTimeJDBCType(JDBCType jdbcType)
	{
		return dateTimeHandled.contains(jdbcType);
	}

	/**
	 * Check if the component is a valid date/time. To avoid exceptions
	 * use {@link #isHandledDateTimeComp(com.nqadmin.swingset.utils.SSComponentInterface)}
	 * @param comp
	 * @return
	 */
	public static boolean dateTimeColumnValidate(SSComponentInterface comp)
	{
		DtoParse dtoParse = internalDateTimeColumnParse(comp);
		return !dtoParse.isError();
	}

	/**
	 * Get a list of parsers for date/time of the specified JDBCType.
	 * The first parser are typically used in order, the first success wins.
	 * 
	 * @param jdbcType
	 * @return list of parser
	 */
	// TODO: General this to formatter for any jdbcType? maybe part of
	// per sscomponent lookup.
	// TODO: API for working with formatter/parsing maps.
	// TODO: date/time formatters per component with app map as backup/default.
	public static List<DateTimeFormatter> getDateTimeParsers(JDBCType jdbcType)
	{
		return Collections.unmodifiableList(getInternalDateTimeParsers(jdbcType));
	}

	private static List<DateTimeFormatter> getInternalDateTimeParsers(JDBCType jdbcType)
	{
		return dateTimeParsersMap.computeIfAbsent(jdbcType, 
				(type) -> {
					return switch(type) {
					case DATE -> new ArrayList<>(USE_MDY
							? List.of(getMDYParser())
							: List.of(DateTimeFormatter.BASIC_ISO_DATE,
									DateTimeFormatter.ISO_LOCAL_DATE));
					case TIME -> new ArrayList<>(List.of(
							DateTimeFormatter.ISO_LOCAL_TIME));
					case TIMESTAMP -> new ArrayList<>(List.of(
							// DATE_TIMEs should be first
							DateTimeFormatter.ISO_LOCAL_DATE_TIME,
							DateTimeFormatter.ISO_LOCAL_DATE));
					default -> null;
					};
				});
	}

	/**
	 * Get formatter to produce date/time text for the specified JDBCType.
	 * 
	 * @param jdbcType
	 * @return text formatter
	 */
	public static DateTimeFormatter getDateTimeFormatter(JDBCType jdbcType)
	{
		return dateTimeFormatterMap.computeIfAbsent(jdbcType, 
				(type) -> {
					return switch(type) {
					case DATE -> USE_MDY
							? getMDYFormatter() : DateTimeFormatter.ISO_LOCAL_DATE;
					case TIME -> DateTimeFormatter.ISO_LOCAL_TIME;
					case TIMESTAMP -> DateTimeFormatter.ISO_LOCAL_DATE_TIME;
					default -> null;
					};
				});
	}

	private static DateTimeFormatter getMDYFormatter()
	{
		return DateTimeFormatter.ofPattern("MM/dd/uuuu");
	}

	private static DateTimeFormatter getMDYParser()
	{
		return DateTimeFormatter.ofPattern("M/d/uuuu");
	}

	private record DtoParse(Temporal dto, boolean isError, Exception ex) {}

	private static final Set<JDBCType> dateTimeHandled = EnumSet.of(
			JDBCType.DATE,
			JDBCType.TIME,
			JDBCType.TIMESTAMP
	);

	private static DtoParse internalDateTimeColumnParse(SSComponentInterface comp)
	{
		// Only handle JTextComponent for now
		if (!(comp instanceof JTextComponent jtc))
			throw new IllegalArgumentException("only JTextComponent handled");
		String text = jtc.getText();
		return internalDateTimeColumnParse(text, comp);
	}

	private static DtoParse internalDateTimeColumnParse(String text, RSC comp)
	{
		List<DateTimeFormatter> formatters = getInternalDateTimeParsers(comp.getBoundColumnJDBCType());
		if (formatters == null || formatters.isEmpty())
			throw new IllegalArgumentException("only JTextComponent handled");
		if(text.isBlank())
			return new DtoParse(null, !comp.getAllowNull(), null); // maybe error

		Exception tex = null;
		for (DateTimeFormatter formatter : formatters) {
			try {
				Temporal dto = null;
				switch(comp.getBoundColumnJDBCType()) {
				case TIME -> dto = LocalTime.parse(text, formatter);
				case DATE -> dto = LocalDate.parse(text, formatter);
				case TIMESTAMP -> {
					// Prefer date/time but handle date only.
					TemporalAccessor dt = formatter.parseBest(text,
							LocalDateTime::from, LocalDate::from);
					dto = switch(dt) {
					case LocalDateTime ldt -> ldt;
					case LocalDate ld -> LocalDateTime.of(ld, LocalTime.of(0, 0));
					default -> null; // impossible
					};
				}
				}
				return new DtoParse(dto, false, null);
			} catch(DateTimeParseException ex) {
				logger.log(DEBUG, () -> sf("%s '%s' fails for '%s' index %d",
						comp.getColumnForLog(), formatter, text, ex.getErrorIndex()));
				tex = ex;
			}
		}
		return new DtoParse(null, true, tex);
	}

	/**
	 * Parse the specified text using this column's type's default parser,
	 * and return the corresponding {@link java.time} object; for example,
	 * {@link java.time.LocalDate}.
	 * @param text new text
	 * @param comp target column
	 * @return a date/time object, null if can't parse
	 */
	public static Temporal getDateTimeObject(String text, RSC comp)
	{
		DtoParse dtoParse = internalDateTimeColumnParse(text, comp);
		return dtoParse.dto();
	}

	/**
	 * Parse the specified text using this column's type's default parser,
	 * and return the corresponding {@link java.time} object; for example,
	 * {@link java.time.LocalDate}.
	 * @param text new text
	 * @param comp target column
	 * @return a date/time object, null if can't parse
	 */
	public static Object getSQLDateTimeObject(String text, RSC comp)
	{
		DtoParse dtoParse = internalDateTimeColumnParse(text, comp);
		JDBCType jdbcType = comp.getBoundColumnJDBCType();
		Temporal dto = dtoParse.dto();
		return switch(jdbcType) {
		case DATE -> Date.valueOf((LocalDate) dto);
		case TIME -> Time.valueOf((LocalTime) dto);
		case TIMESTAMP -> Timestamp.valueOf((LocalDateTime) dto);
		default -> throw new IllegalArgumentException(sf("%s not handled", jdbcType));
		};
	}

	/**
	 * Using the formatter for the column's specifiedJDBCType,
	 * format the column's specified value.
	 * Return empty string if column is null.
	 * @param comp target column
	 * @return text for column
	 * @throws java.sql.SQLException
	 */
	public static String getDateTimeText(RSC comp) throws SQLException
	{
		JDBCType jdbcType = comp.getBoundColumnJDBCType();
		// TODO: should there be a way to do a switch on type, use getDate...?
		Object o = comp.getRowSet().getObject(comp.getBoundColumnIndex());
		return getDateTimeText(o, jdbcType);
	}

	/**
	 * Using the formatter for the column's specifiedJDBCType,
	 * format the column's specified value.
	 * Return empty string if column is null.
	 * @param jdbcDateTimeObject convert this to text
	 * @param comp target column
	 * @return text for column
	 */
	public static String getDateTimeText(Object jdbcDateTimeObject, RSC comp)
	{
		JDBCType jdbcType = comp.getBoundColumnJDBCType();
		return getDateTimeText(jdbcDateTimeObject, jdbcType);
	}

	/**
	 * Using the formatter for the column's specifiedJDBCType,
	 * format the column's specified value.
	 * Return empty string if column is null.
	 * @param jdbcDateTimeObject convert this to text
	 * @param jdbcType
	 * @return date/time text for column
	 * @throws SQLException if database access error
	 */
	private static String getDateTimeText(Object jdbcDateTimeObject, JDBCType jdbcType)
	{
		if(jdbcDateTimeObject instanceof String s)
			return s;
		DateTimeFormatter formatter = getDateTimeFormatter(jdbcType);
		if (formatter == null)
			throw new IllegalArgumentException(sf("%s not handled", jdbcType));
		switch(jdbcType) {
		case TIME -> {
			Time time = (Time) jdbcDateTimeObject;
			if(time == null)
				return "";
			return time.toLocalTime().format(formatter);
		}
		case DATE -> {
			Date date = (Date) jdbcDateTimeObject;
			if(date == null)
				return "";
			return date.toLocalDate().format(formatter);
		}
		case TIMESTAMP -> {
			Timestamp timestamp = (Timestamp) jdbcDateTimeObject;
			if(timestamp == null)
				return "";
			return timestamp.toLocalDateTime().format(formatter);
		}
		default -> {
			// Impossible since have a formatter.
			throw new IllegalArgumentException(sf("Impossible: %s", jdbcType));
		}
		}
	}

	// These unused static methods, could be revived and take a JDBCType param.
	// That's basically what getDateTimeText(...) does for formatting
	// and getSQLDateTimeObject(...) for parsing.
	// See getDateTimeObject(...) for returning java.time object.

	static { if(Boolean.FALSE) { getSQLDate(""); getStringDate(null); } }

	/**
	 * Convert string to Date using default parser.
	 *
	 * @param _strDate date string in "[m]m/[d]d/yyyy" format
	 *
	 * @return return SQL date for the string specified
	 * @throws IllegalArgumentException if data conversion fails
	 */
	// TODO: check that all callers handle excepted as needed
	private static Date getSQLDate(final String _strDate)
			throws IllegalArgumentException
	{
		if (_strDate == null) {
			return null;
		}
		String strDate = _strDate.trim();
		if (strDate.isEmpty()) {
			return null;
		}

		List<DateTimeFormatter> formatters = getInternalDateTimeParsers(JDBCType.DATE);
		if (formatters == null || formatters.isEmpty())
			return null;

		for (DateTimeFormatter formatter : formatters) {
			try {
				return Date.valueOf(LocalDate.parse(_strDate, formatter));
			} catch(DateTimeParseException ex) {
				logger.log(DEBUG, () -> sf("'%s' fails for '%s' index %d",
						formatter, _strDate, ex.getErrorIndex()));
			}
		}
		return null;
	}

	/**
	 * Format Date using default formatter.
	 * @param _date
	 * @return
	 */
	private static String getStringDate(Date _date)
	{
		DateTimeFormatter formatter = getDateTimeFormatter(JDBCType.DATE);
		return _date.toLocalDate().format(formatter);
		
	}
	
}
