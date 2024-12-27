/* *****************************************************************************
 * Copyright (C) 2024, Ernie R. Rael. All rights reserved.
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
import java.time.OffsetDateTime;
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

import com.google.common.collect.ImmutableMap;
import com.nqadmin.swingset.formatting.SSFormat;
import com.nqadmin.swingset.utils.SSUtils;

import static java.lang.System.Logger.Level.*;
import static java.sql.JDBCType.DATE;
import static java.sql.JDBCType.TIME;
import static java.sql.JDBCType.TIMESTAMP;

import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

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

	/** A style is one or more formats. Used to specify date types for parse. */
	public enum DateParseStyle {
		/** Custom Month/Day/Year */
		MDY,
		/** Some well know ISO formats. */
		ISO,
		/** All of the above. */
		ALL
	}
	private static DateParseStyle date_parse = DateParseStyle.ALL;

	private static DateTimeFormatter date_formatter = getMDYFormatter();
	private static DateTimeFormatter time_formatter = DateTimeFormatter.ISO_LOCAL_TIME;
	private static DateTimeFormatter timestamp_formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	static {
		if (Boolean.FALSE) {
			date_parse = DateParseStyle.ALL;
			date_formatter = ISO_LOCAL_DATE;
			time_formatter = DateTimeFormatter.ISO_LOCAL_TIME;
			timestamp_formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		}
	}

	/**
	 * Check if column is a handled; must be JDBC date/type and text component.
	 * @param comp
	 * @return true is OK
	 */
	public static boolean isHandledDateTimeComp(RSC comp)
	{
		return comp instanceof JTextComponent
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
	 * Check if the component is a valid date/time. To avoid exceptions, first
	 * use {@link #isHandledDateTimeComp(com.nqadmin.swingset.utils.SSComponentInterface)}.
	 * @param text The text to validate
	 * @param comp The component that the text is derived from
	 * @return
	 */
	public static boolean dateTimeColumnValidate(String text, RSC comp)
	{
		DtoParse dtoParse = internalDateTimeColumnParse(text, comp);
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
		return Collections.unmodifiableList(getInternalDateTimeParsers(jdbcType, null));
	}

	private static final ImmutableMap<SSFormat,List<DateTimeFormatter>> ssFormatters
			= new ImmutableMap.Builder<SSFormat, List<DateTimeFormatter>>()
					.put(SSFormat.DATE_MMDDYYYY_SLASH,
							List.of(DateTimeFormatter.ofPattern("M/d/uuuu"),
									DateTimeFormatter.ofPattern("MMdduuuu")))
					.put(SSFormat.DATE_DDMMYYYY_SLASH,
							List.of(DateTimeFormatter.ofPattern("d/M/uuuu"),
									DateTimeFormatter.ofPattern("ddMMuuuu")))
					.put(SSFormat.TIME_HHMMSS,
							List.of(DateTimeFormatter.ISO_LOCAL_TIME,
									DateTimeFormatter.ofPattern("HHmmss")))
					.put(SSFormat.TIMESTAMP_YYYYMMDD_STROKE_HHMMSS_SSSZ,
							List.of(DateTimeFormatter
									.ofPattern("uuuu-M-d HH:mm:ss[.SSS[ xxx]]")))
			.buildOrThrow();

	private static List<DateTimeFormatter> getInternalDateTimeParsers(RSC comp)
	{
		return getInternalDateTimeParsers(comp.getBoundColumnJDBCType(),
										  comp.getSSFormat());
	}
	private static List<DateTimeFormatter> getInternalDateTimeParsers(
			JDBCType jdbcType, SSFormat ssFormat)
	{
		if (ssFormat != null) {
			List<DateTimeFormatter> rv = ssFormatters.get(ssFormat);
			if (rv != null)
				return rv;
		}
		return dateTimeParsersMap.computeIfAbsent(jdbcType, 
				(type) -> {
					return switch(type) {
					case DATE -> 
						new ArrayList<>(switch(date_parse) {
						case MDY -> List.of(getMDYParser());
						case ISO -> List.of(
								DateTimeFormatter.BASIC_ISO_DATE,
								DateTimeFormatter.ISO_LOCAL_DATE);
						case ALL -> List.of(
								getMDYParser(),
								DateTimeFormatter.BASIC_ISO_DATE,
								DateTimeFormatter.ISO_LOCAL_DATE);
						default -> null;
						});
					case TIME -> new ArrayList<>(List.of(
							DateTimeFormatter.ISO_LOCAL_TIME));
					case TIMESTAMP -> {
						ArrayList<DateTimeFormatter> l;
						l = new ArrayList<>(getLongTimestampParsers());
						l.addAll(List.of(DateTimeFormatter.ISO_LOCAL_DATE_TIME,
										 //DateTimeFormatter.ofPattern("uuuu-M-d HH:mm:ss"),
										 DateTimeFormatter.ISO_LOCAL_DATE));
						yield l;
					}
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
					case DATE -> date_formatter;
					case TIME -> time_formatter;
					case TIMESTAMP -> timestamp_formatter;
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

	// These patterns have optional fraction and TZ.
	// The one with '-' is like ISO, but no 'T'.
	private static List<DateTimeFormatter> getLongTimestampParsers()
	{
		return List.of(DateTimeFormatter.ofPattern("M/d/uuuu HH:mm:ss[.SSS [xxx]]"),
					   DateTimeFormatter.ofPattern("uuuu-M-d HH:mm:ss[.SSS[ xxx]]"));
	}

	private record DtoParse(Temporal dto, boolean isError, Exception ex) {}

	private static final Set<JDBCType> dateTimeHandled = EnumSet.of(
			JDBCType.DATE,
			JDBCType.TIME,
			JDBCType.TIMESTAMP
	);

	private static DtoParse internalDateTimeColumnParse(String text, RSC comp)
	{
		List<DateTimeFormatter> formatters = getInternalDateTimeParsers(comp);
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
							OffsetDateTime::from,
							LocalDateTime::from,
							LocalDate::from);
					dto = switch(dt) {
					case LocalDateTime ldt -> ldt;
					case LocalDate ld -> LocalDateTime.of(ld, LocalTime.of(0, 0));
					default -> null; // impossible
					};
				}
				}
				return new DtoParse(dto, false, null);
			} catch(DateTimeParseException ex) {
				logger.log(DEBUG, () -> sf("%s at '%s', %s' fails for '%s' index %d",
						comp.getColumnForLog(), text.substring(ex.getErrorIndex()),
						formatter, text, ex.getErrorIndex()));
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
		if (dtoParse.isError())
			return null;
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
		// TODO: derived 3rd arg from comp.getSSFormat
		return getDateTimeText(o, jdbcType, null);
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
		// TODO: derived 3rd arg from comp.getSSFormat
		return getDateTimeText(jdbcDateTimeObject, jdbcType, null);
	}

	/**
	 * Using the formatter for the column's specifiedJDBCType,
	 * format the column's specified value. 
	 * Return empty string if column is null.
	 * <p>
	 * The corresponding java.sql and java.time objects are handled the same;
	 * for example, Date and LocalDate.
	 * 
	 * @param jdbcDateTimeObject convert this to text
	 * @param jdbcTypefix
	 * @return date/time text for column
	 * @throws SQLException if database access error
	 */
	private static String getDateTimeText(Object jdbcDateTimeObject,
										  JDBCType jdbcType,
										  DateTimeFormatter _formatter)
	{
		if(jdbcDateTimeObject instanceof String s)
			return s;
		if (jdbcDateTimeObject == null)
			return "";
		DateTimeFormatter formatter = _formatter != null
				? _formatter : getDateTimeFormatter(jdbcType);
		if (formatter == null)
			throw new IllegalArgumentException(sf("%s not handled", jdbcType));

		switch(jdbcType) {
		case TIME -> {
			LocalTime lt = jdbcDateTimeObject instanceof LocalTime lt1
					? lt1 : ((Time)jdbcDateTimeObject).toLocalTime();
			return lt.format(formatter);
		}
		case DATE -> {
			LocalDate ld = jdbcDateTimeObject instanceof LocalDate ld1
					? ld1 : ((Date)jdbcDateTimeObject).toLocalDate();
			return ld.format(formatter);
		}
		case TIMESTAMP -> {
			LocalDateTime ldt = jdbcDateTimeObject instanceof LocalDateTime ldt1
					? ldt1 : ((Timestamp)jdbcDateTimeObject).toLocalDateTime();
			return ldt.format(formatter);
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

		List<DateTimeFormatter> formatters = getInternalDateTimeParsers(JDBCType.DATE, null);
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
