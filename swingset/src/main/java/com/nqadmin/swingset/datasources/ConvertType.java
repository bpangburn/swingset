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

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static com.nqadmin.swingset.datasources.ConvertType.Clazz.getClazz;
import static com.nqadmin.swingset.utils.SSUtils.sf;

// TODO: Wonder if a "X --> Y" sparse matrix filled with converters...

/**
 * Different database jdbc drivers support different type conversion;
 * this utility class juggles types around.
 * <p>
 * TODO: Consider situations where JDBC does the requested conversion and so
 *		 nothing to do. Maybe such a thing is checked before doing the convert
 *		 methods. Like "verifyConvertToType()" below; "checkDriverConvertToType()".
 * 
 */
public class ConvertType
{
	private ConvertType() { }

	/**
	 * To override default type mapping for local/dbms requirements.
	 * An entry with "Exception.class" generates an exception.
	 * <p>
	 * TODO: API/plugin
	 */
	private static final EnumMap<JDBCType, Class<?>> overrideJdbcToJavaType = new EnumMap<>(JDBCType.class);
	static { overrideJdbcStandard(); }
	private static void overrideJdbcStandard()
	{
		// The *_WITH_TIMEZONE aren't mentioned in appendix B.1 or B.3
		overrideJdbcToJavaType.put(JDBCType.TIME_WITH_TIMEZONE,
								   OffsetTime.class);
		overrideJdbcToJavaType.put(JDBCType.TIMESTAMP_WITH_TIMEZONE,
								   OffsetDateTime.class);

		// overrideJdbcToJavaType.put(JDBCType.SMALLINT, Byte.class);
		// overrideJdbcToJavaType.put(JDBCType.TINYINT, Short.class);
	}

	/**
	 * Throw if the specified JDBCType is not convertible, using methods in this
	 * class, to the type. Typically used during bind.
	 * <p>
	 * The exception is thrown whether assertions are enabled or not.
	 * <p>
	 * TODO: or is automatically converted by JDBC driver?
	 * 
	 * @param jdbcType jdbc column type
	 * @param targetType target type
	 * @param allow only allow these JDBC types, may be null
	 * @throws AssertionError if can't handle conversion to JDBCType
	 */
	public static void assertConvertFromJdbcType(JDBCType jdbcType, Class<?> targetType, EnumSet<JDBCType> allow)
	{
		if(allow != null && !allow.contains(jdbcType))
			throw new AssertionError(sf("'%s' not allowed in '%s'", jdbcType, allow));
		if(!checkConvertFromJdbcType(jdbcType, targetType, allow))
			throw new AssertionError(sf("'%s' to '%s' conversion not supported", jdbcType, targetType.getName()));
	}

	/**
	 * Check if the specified JDBCType is convertible, using methods in this
	 * class, to the type.
	 * <p>
	 * TODO: or is automatically converted by JDBC driver?
	 * 
	 * @param jdbcType jdbc column type
	 * @param targetType target type
	 * @param allow only allow these JDBC types, may be null
	 * @return true if conversion is handled
	 */
	public static boolean checkConvertFromJdbcType(JDBCType jdbcType, Class<?> targetType, EnumSet<JDBCType> allow)
	{
		if(allow != null && !allow.contains(jdbcType))
			return false;
		if(targetType == null)
			return true;
		try {
			Clazz sourceJDBC = getClazz(findJavaTypeClass(jdbcType));
			Clazz target = getClazz(targetType);

			if(sourceJDBC != null && target != null) {
				if(sourceJDBC.isDateTime() && target.isDateTime()) {
					return checkConvertFromJdbcDateType(sourceJDBC, target);
				}
				// All number to number types supported.
				if(sourceJDBC.isNumeric() && target.isNumeric())
					return true;
				switch (target) {
				case BOOL -> {
					if(sourceJDBC == Clazz.BOOL || sourceJDBC.isNumeric())
						return true;
				}
				case LONG, INT, SHORT, BYTE, FLOAT, DOUBLE, BIGD -> {
					if(sourceJDBC == Clazz.BOOL)
						return true;
				} }
			}
		}
		catch(SQLException ex) {
		}
		return false;
	}

	/** Both source and target are date */
	// TODO: OFFSETTIME, OFFSETDATETIME
	private static boolean checkConvertFromJdbcDateType(Clazz sourceJDBC, Clazz target)
	{
		if (sourceJDBC == target)
			return true;
		return switch(target) {
		case UTILDATE -> true;
		case DATE -> switch (sourceJDBC) {
			case TIMESTAMP -> true;
			default -> false;
		};
		case TIME -> switch (sourceJDBC) {
			case TIMESTAMP -> true;
			default -> false;
		};
		case TIMESTAMP -> switch (sourceJDBC) {
			case DATE, TIME -> true;
			default -> false;
		};
		default -> false;
		};
	}

	/**
	 * Check if the specified sourceType is convertible, using methods in this
	 * class, to the JDBCType.
	 * <p>
	 * TODO: or is automatically converted by JDBC driver?
	 * 
	 * @param jdbcType target jdbc type
	 * @param sourceType source type
	 * @param allow only allow these JDBC types, may be null
	 * @return true if conversion is handled
	 */
	public static boolean checkConvertToJdbcType(JDBCType jdbcType, Class<?> sourceType, EnumSet<JDBCType> allow)
	{
		if(allow != null && !allow.contains(jdbcType))
			return false;
		if(sourceType == null)
			return true;
		try {
			Clazz targetJDBC = getClazz(findJavaTypeClass(jdbcType));
			Clazz source = getClazz(sourceType);

			if(targetJDBC != null && source != null) {
				if(targetJDBC.isDateTime() && source.isDateTime()) {
					return checkConvertToJdbcDateType(targetJDBC, source);
				}
				// All number to number types supported.
				if(targetJDBC.isNumeric() && source.isNumeric())
					return true;

				switch (source) {
				case BOOL -> {
					if(targetJDBC == Clazz.BOOL || targetJDBC.isNumeric())
						return true;
				}
				case LONG, INT, SHORT, BYTE, FLOAT, DOUBLE, BIGD -> {
					if(targetJDBC == Clazz.BOOL)
						return true;
				} }
			}
		}
		catch(SQLException ex) {
		}
		return false;
	}

	/** Both source and target are date. B.5. */
	// TODO: OFFSETTIME, OFFSETDATETIME
	private static boolean checkConvertToJdbcDateType(Clazz targetJDBC, Clazz source)
	{
		if (targetJDBC == source)
			return true;
		return switch(targetJDBC) {
		case DATE -> switch (source) {
			case TIMESTAMP, UTILDATE, LOCALDATE, LOCALDATETIME -> true;
			default -> false;
		};
		case TIME -> switch (source) {
			case TIMESTAMP, UTILDATE, LOCALTIME, LOCALDATETIME -> true;
			default -> false;
		};
		case TIMESTAMP -> switch (source) {
			case DATE, TIME, UTILDATE, LOCALDATETIME -> true;
			default -> false;
		};
		default -> false;
		};
	}

	/** 
	 * Convert the specified object to the object type
	 * for the specified JDBCType.
	 * This is not just casting; for example Boolen to Integer.
	 * <p>
	 * Note the spec B.2 vs B.4 updater methods vs setObject;
	 * and B.5 for setObject when a target type is specified.
	 * TODO: Is this is for when a target JDBCtype is NOT specified
	 * or in the setObject (not appendix B.5).???
	 * 
	 * @param value
	 * @param jdbcType
	 * @return 
	 * @throws java.sql.SQLException 
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B.4 Java Object Types Mapped to JDBC Types
	 */
	// TODO: move to the following?
	//@see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B.5 Conversions by setObject and setNull from Java Object Types to JDBC Types
	public static Object convertObjectType(Object value, JDBCType jdbcType)
			throws SQLException
	{
		Class<?> type = findJavaTypeClass(jdbcType);

		Object target = internalConvertObject(value, type);
		if (target == CanNotConvert)
			throw new SSSQLConversionException(sf("Missing conversion %s to %s",
					value.getClass().getName(), jdbcType));
		return target;
	}

	/**
	 * Convert specified value to specified type; this can be more than a cast.
	 * @param <T> target type
	 * @param value value to convert
	 * @param type class of target type
	 * @return converted type, may be the same object
	 * @throws com.nqadmin.swingset.datasources.SSSQLConversionException
	 */
	// TODO: handle primitive types??? See enum Clazz for notes.
	public static <T> T convertObjectType(Object value, Class<T> type)
			throws SSSQLConversionException
	{
		@SuppressWarnings("unchecked")
		T target = (T) internalConvertObject(value, type);
		if (target == CanNotConvert)
			throw new SSSQLConversionException(sf("Missing conversion %s to %s",
					value.getClass().getName(), type.getClass().getName()));
		return type.cast(target); // TODO: Don't really need the cast, but it's cheap.
	}

	/** Return something of the specified type,
	 * unless can't convert then "CanNotConvert" Object is returned.
	 */
	private static Object internalConvertObject(Object sourceValue, Class<?> type)
			throws SSSQLConversionException
	{
		if (sourceValue == null || type.isAssignableFrom(sourceValue.getClass()))
			return sourceValue;

		Clazz source = getClazz(sourceValue.getClass());
		Clazz target = getClazz(type);
		if(source == null || target == null)
			return CanNotConvert;

		if(source.isDateTime() && target.isDateTime()) {
			if (source == target)
				return sourceValue;
			return internalConvertObjectDateType(sourceValue, source, target);
		}

		if(source.isNumeric() && target.isNumeric()) {
			Number n = (Number) sourceValue;
			switch(target) {
			case LONG -> { return n.longValue(); }
			case INT -> { return n.intValue(); }
			case SHORT -> { return n.shortValue(); }
			case BYTE -> { return n.byteValue(); }
			case FLOAT -> { return n.floatValue(); }
			case DOUBLE -> { return n.doubleValue(); }
			case BIGD -> {
				switch(source) {
				case LONG, INT, SHORT, BYTE -> {
					return BigDecimal.valueOf(n.longValue());
				}
				case FLOAT, DOUBLE -> { return BigDecimal.valueOf(n.doubleValue()); }
				case BIGD -> { return n; }	// Should have been caught at entry.
				}
			}
			}
			assert false;
		}

		switch (sourceValue) {

		case Boolean b -> {
			Number n = b ? 1 : 0;
			switch(target) {
			case LONG -> { return n.longValue(); }
			case INT -> { return n.intValue(); }
			case SHORT -> { return n.shortValue(); }
			case BYTE -> { return n.byteValue(); }
			case FLOAT -> { return n.floatValue(); }
			case DOUBLE -> { return n.doubleValue(); }
			case BIGD -> { return BigDecimal.valueOf(b ? 1 : 0); }
			}
		}
		case Integer n -> {
			switch(target) {
			case BOOL -> { return n != 0; }
			}
		}
		case Long n -> {
			switch(target) {
			case BOOL -> { return n != 0; }
			}
		}
		case Short n -> {
			switch(target) {
			case BOOL -> { return n != 0; }
			}
		}
		case Byte n -> {
			switch(target) {
			case BOOL -> { return n != 0; }
			}
		}
		case BigDecimal n -> {
			switch(target) {
			case BOOL -> { return n.signum() != 0; }
			}
		}
		case String s -> {
			try {
				switch(target) {
				case LONG -> { return Long.valueOf(s); }
				case INT -> { return Integer.valueOf(s); }
				case SHORT -> { return Short.valueOf(s); }
				case BYTE -> { return Byte.valueOf(s); }
				case FLOAT -> { return Float.valueOf(s); }
				case DOUBLE -> { return Double.valueOf(s); }
				case BIGD -> { return new BigDecimal(s); }

				// TODO: add date converstions.
				// TODO: add other converstions.
				}
			} catch (NumberFormatException ex) {
				throw new SSSQLConversionException(sf("Convert '%s' to %s: %s",
						s, target, ex.getMessage()), ex);
			}
		}
		default -> {}
		}
		return CanNotConvert;
	}

	private static Object internalConvertObjectDateType(
			Object sourceValue, Clazz source, Clazz target) {
		switch(target) {
		case TIMESTAMP -> {
			switch (source) {
			case DATE, TIME, UTILDATE -> {
				return new java.sql.Timestamp(((Date)sourceValue).getTime());
			}
			case LOCALDATETIME -> {
				return java.sql.Timestamp.valueOf((LocalDateTime)sourceValue);
			} }
		}
		case DATE -> {	// year,month,day at midnight - no time
			// TODO: switch on sourceValue
			switch (source) {
			case UTILDATE -> {
				java.sql.Timestamp ts = new java.sql.Timestamp(
						((Date)sourceValue).getTime());
				return java.sql.Date.valueOf(ts.toLocalDateTime().toLocalDate());
			}
			case TIMESTAMP -> {
				java.sql.Timestamp ts = ((java.sql.Timestamp)sourceValue);
				return java.sql.Date.valueOf(ts.toLocalDateTime().toLocalDate());
			}
			case LOCALDATETIME -> {
				return java.sql.Date.valueOf(
						((LocalDateTime)sourceValue).toLocalDate());
			}
			case LOCALDATE -> {
				return java.sql.Date.valueOf((LocalDate)sourceValue);
			} }
		}
		case TIME -> {
			switch (source) {
			case UTILDATE -> {
				java.sql.Timestamp ts = new java.sql.Timestamp(
						((Date)sourceValue).getTime());
				return java.sql.Time.valueOf(ts.toLocalDateTime().toLocalTime());
			}
			case TIMESTAMP -> {
				java.sql.Timestamp ts = ((java.sql.Timestamp)sourceValue);
				return java.sql.Time.valueOf(ts.toLocalDateTime().toLocalTime());
			}
			case LOCALDATETIME -> {
				return java.sql.Time.valueOf(
						((LocalDateTime)sourceValue).toLocalTime());
			}
			case LOCALTIME -> {
				return java.sql.Time.valueOf((LocalTime)sourceValue);
			}
			}
		}
		case LOCALDATE -> {
			switch(source) {
			case DATE -> { return ((java.sql.Date)sourceValue).toLocalDate(); }
			}
		}
		case LOCALTIME -> {
			switch(source) {
			case TIME -> { return ((java.sql.Time)sourceValue).toLocalTime(); }
			}
		}
		case LOCALDATETIME -> {
			switch(source) {
			case TIMESTAMP -> {
				return ((java.sql.Timestamp)sourceValue).toLocalDateTime();
			}
			case DATE -> {
				return ((java.sql.Date)sourceValue).toLocalDate().atStartOfDay();
			} }
			// case TIME -> {
			// 	LocalTime lt = ((java.sql.Time)sourceValue).toLocalTime()...;
			// }
		} }
		return CanNotConvert;
	}

	/**
	 * Convenience method for getting {@link JDBCType} enum from
	 * {@link java.sql.Types}.
	 * <p>
	 * May perform better than using
	 * {@link JDBCType#valueOf(java.lang.String) }
	 * @param sqlType the type to translate
	 * @return the corresponding JDBCType
	 */
	public static JDBCType getJDBCType(int sqlType) {
		// TODO: can create a map of sqlType to JDBCType if performance issue
		return JDBCType.valueOf(sqlType);
	}

	/**
	 * Copy the elements of the _objects array into into
	 * an array of the correct type for the {@code JDBCType}d objects.
	 * <p>
	 * If an array or even a collection of the accurate type is desired,
	 * you can do the follow which is type safe, no compiler warnings.
	 * There will be an exception if something is afoul.
	 * <pre>
	 * {@code
	 * Object[] arr = f(); // But I "know" the elements are Integer
	 * Integer[] newarr = (Integer[]) castJDBCToJava(JDBCType.INTEGER, arr);
	 * List<Integer> properList = Arrays.asList(newarr);
	 * }
	 * </pre>
	 * @param objects array of objects to cast
	 * @param jdbcType cast objects to this JDBCType
	 * @return array of corresponding type to the cast input objects
	 * @throws SQLException This exception wraps a {@code ClassCastException}
	 */
	public static Object[] castJDBCToJava(JDBCType jdbcType, Object[] objects) throws SQLException {
		Class<?> clazz = findJavaTypeClass(jdbcType);
		Object[] newArray = (Object[]) java.lang.reflect.Array.newInstance(clazz, objects.length);
		try {
			System.arraycopy(objects, 0, newArray, 0, objects.length);
		} catch(ArrayStoreException ex) {
			throw new SQLException(ex);
		}
		return newArray;
	}

	/**
	 * Cast the object to {@code JDBCType}.The idea is to verify
 the the object is of the correct type.
	 * @param object object to cast
	 * @param jdbcType cast object to this JDBCType
	 * @return Essentially the same Object that was input
	 * @throws SQLException This exception wraps a {@code ClassCastException}
	 */
	public static Object castJDBCToJava(JDBCType jdbcType, Object object) throws SQLException {
		try {
			return findJavaTypeClass(jdbcType).cast(object);
		} catch (ClassCastException ex) {
			throw new SQLException(ex);
		}
	}

	/**
	 * Determine the Java type class for the given database type.
	 * @param jdbcType JDBCType of interest
	 * @return the class object used for the given type
	 * @throws SQLException if the JDBCType is not handled
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B.3 JDBC Types Mapped to Java Object Types
	 */
	public static Class<?> findJavaTypeClass(JDBCType jdbcType)
			throws SQLException
	{
		Class<?> clazz = overrideJdbcToJavaType.getOrDefault(jdbcType, null);
		if (clazz != null) {
			if (clazz == Exception.class) {
				throw new SSSQLUnhandledTypeException(jdbcType.toString());
			}
			return clazz;
		}

		switch (jdbcType) {
			case INTEGER, SMALLINT, TINYINT	-> clazz = Integer.class;
			case BIGINT -> clazz = Long.class;
			case REAL -> clazz = Float.class;
			case FLOAT, DOUBLE -> clazz = Double.class;
			case DECIMAL, NUMERIC -> clazz = BigDecimal.class;
			case BIT, BOOLEAN -> clazz = Boolean.class;
			case DATE -> clazz = java.sql.Date.class;
			case TIME -> clazz = java.sql.Time.class;
			case TIMESTAMP -> clazz = java.sql.Timestamp.class;
			case CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR
					-> clazz = String.class;
			default ->
				throw new SSSQLUnhandledTypeException(jdbcType.toString());
		}
		//case DATE: case TIME: case TIMESTAMP: clazz = java.util.Date.class; break;

			// case ARRAY:
			// 	clazz = java.sql.Array.class;
			// 	break;

			// case BINARY:
			// case VARBINARY:
			// case LONGVARBINARY:
			// 	clazz = byte[].class;
			// 	break;

			// case CLOB: clazz = java.sql.Clob.class; break;
			// case BLOB: clazz = java.sql.Blob.class; break;
			// case REF: clazz = java.sql.Ref.class; break;
			// case DATALINK: clazz = java.net.URL.class; break;
			// case ROWID: clazz = java.sql.RowId.class; break;
			// case NCLOB: clazz = java.sql.NClob.class; break;
			// case SQLXML: clazz = java.sql.SQLXML.class; break;
		return clazz;

		// case DISTINCT: Object type of underlying type
		// case STRUCT: java.sql.Struct or java.sql.SQLData
		// case JAVA_OBJECT: Underlying Java class
	}

	private static final Object CanNotConvert = new Object();

	private static Map<Class<?>,Clazz> mapClazz = new HashMap<>();
	static enum Clazz {
		STRING(String.class),
		BOOL(Boolean.class),

		// TODO: how to handle primitives
		//		- INT(List.of(Integer.class, int.class))
		//		- PINT
		//		- don't have primitives *** this seems right

		INT(Integer.class,			true, false),
		SHORT(Short.class,			true, false),
		BYTE(Byte.class,			true, false),
		LONG(Long.class,			true, false),
		
		FLOAT(Float.class,			true, false),
		DOUBLE(Double.class,		true, false),
		BIGD(BigDecimal.class,		true, false),

		UTILDATE(Date.class,				false, true),
		DATE(java.sql.Date.class,			false, true),
		TIME(java.sql.Time.class,			false, true),
		TIMESTAMP(java.sql.Timestamp.class,	false, true),

		LOCALDATE(java.time.LocalDate.class,			false, true),
		LOCALTIME(java.time.LocalTime.class,			false, true),
		LOCALDATETIME(java.time.LocalDateTime.class,	false, true),

		// TODO: OFFSETTIME, OFFSETDATETIME
		;

		public static Clazz getClazz(Class<?> c)
		{
			return mapClazz.get(c);
		}

		private final boolean isNumeric;
		private final boolean isDateTime;
		Clazz(Class<?> clazz)
		{
			this(clazz, false, false);
		}
		@SuppressWarnings("LeakingThisInConstructor")
		Clazz(Class<?> clazz, boolean isNumeric, boolean isDateTime)
		{
			mapClazz.put(clazz, this);
			this.isNumeric = isNumeric;
			this.isDateTime = isDateTime;
		}

		boolean isNumeric()
		{
			return isNumeric;
		}

		boolean isDateTime()
		{
			return isDateTime;
		}
	}

}
