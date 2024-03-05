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
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static com.nqadmin.swingset.datasources.ConvertType.Clazz.getClazz;
import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * Different database jdbc drivers support different type conversion;
 * this utility class juggles types around.
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
	 * Check if the specified type is convertible, using methods in this
	 * class, to the JDBC type  of the specified RowSet column.
	 * Typically used during bind.
	 * @param jdbcType jdbc column type
	 * @param type target type
	 * @param restrict only allow these JDBC types, may be null
	 * @throws IllegalArgumentException if can't handle JDBCType
	 */
	public static void verifyConvertToType(JDBCType jdbcType, Class<?> type, EnumSet<JDBCType> restrict)
	{
		if(restrict != null && !restrict.contains(jdbcType))
			throw new IllegalArgumentException(sf("'%s' not in '%s'", jdbcType, restrict));
		if(type == null)
			return;
		try {
			Clazz source = getClazz(findJavaTypeClass(jdbcType));
			Clazz target = getClazz(type);
			if(source != null && target != null) {
				// All number to number types supported.
				if(source.isNumeric() && target.isNumeric())
					return;
				switch (target) {
				case BOOL -> {
					if(source == Clazz.BOOL || source.isNumeric())
						return;
				}
				case LONG, INT, SHORT, BYTE, FLOAT, DOUBLE, BIGD -> {
					if(source == Clazz.BOOL)
						return;
				}
				}
			}
		} catch(SQLException ex) {
		}
		throw new IllegalArgumentException(sf("'%s' to '%s' conversion not supported", jdbcType, type.getName()));
	}

	/** 
	 * Convert the specified object to the object type
	 * for the specified JDBCType.
	 * This is not just casting; for example Boolen to Integer.
	 * 
	 * @param value
	 * @param jdbcType
	 * @return 
	 * @throws java.sql.SQLException 
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B.4 Java Object Types Mapped to JDBC
	 * Types
	 */
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
	public static <T> T convertObjectType(Object value, Class<T> type) throws SSSQLConversionException
	{
		@SuppressWarnings("unchecked")
		T target = (T) internalConvertObject(value, type);
		if (target == CanNotConvert)
			throw new SSSQLConversionException(sf("Missing conversion %s to %s",
					value.getClass().getName(), type.getClass().getName()));
		return type.cast(target); // Don't really need the cast, but it's cheap.
	}

	/** Return something of the specified type,
	 * unless can't convert then "CanNotConvert" Object is returned.
	 */
	private static Object internalConvertObject(Object sourceValue, Class<?> type)
	{
		if (type.isAssignableFrom(sourceValue.getClass()))
			return sourceValue;

		Clazz source = getClazz(sourceValue.getClass());
		Clazz target = getClazz(type);
		if(source == null || target == null)
			return CanNotConvert;

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
			case BOOL -> { return BigDecimal.valueOf(0).compareTo(n) != 0; }
			}
		}
		default -> {}
		}
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
	 * @param _objects array of objects to cast
	 * @param _jdbcType cast objects to this JDBCType
	 * @return array of corresponding type to the cast input objects
	 * @throws SQLException This exception wraps a {@code ClassCastException}
	 */
	public static Object[] castJDBCToJava(final JDBCType _jdbcType, final Object[] _objects) throws SQLException {
		Class<?> clazz = findJavaTypeClass(_jdbcType);
		Object[] newArray = (Object[]) java.lang.reflect.Array.newInstance(clazz, _objects.length);
		try {
			System.arraycopy(_objects, 0, newArray, 0, _objects.length);
		} catch(ArrayStoreException ex) {
			throw new SQLException(ex);
		}
		return newArray;
	}

	/**
	 * Cast the object to {@code JDBCType}. The idea is to verify
	 * the the object is of the correct type.
	 * @param _object object to cast
	 * @param _jdbcType cast object to this JDBCType
	 * @return Essentially the same Object that was input
	 * @throws SQLException This exception wraps a {@code ClassCastException}
	 */
	public static Object castJDBCToJava(final JDBCType _jdbcType, final Object _object) throws SQLException {
		try {
			return findJavaTypeClass(_jdbcType).cast(_object);
		} catch (ClassCastException ex) {
			throw new SQLException(ex);
		}
	}

	/**
	 * Determine the Java type class for the given database type.
	 * @param _jdbcType JDBCType of interest
	 * @return the class object used for the given type
	 * @throws SQLException if the JDBCType is not handled
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B.3 JDBC Types Mapped to Java Object Types
	 */
	public static Class<?> findJavaTypeClass(final JDBCType _jdbcType)
			throws SQLException
	{
		Class<?> clazz = overrideJdbcToJavaType.getOrDefault(_jdbcType, null);
		if (clazz != null) {
			if (clazz == Exception.class) {
				throw new SSSQLUnhandledTypeException(_jdbcType.toString());
			}
			return clazz;
		}

		switch (_jdbcType) {
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
				throw new SSSQLUnhandledTypeException(_jdbcType.toString());
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
		INT(Integer.class, true),
		SHORT(Short.class, true),
		BYTE(Byte.class, true),
		LONG(Long.class, true),
		FLOAT(Float.class, true),
		DOUBLE(Double.class, true),
		BIGD(BigDecimal.class, true),
		BOOL(Boolean.class)
		;

		public static Clazz getClazz(Class<?> c)
		{
			return mapClazz.get(c);
		}

		private final boolean isNumeric;
		Clazz(Class<?> clazz)
		{
			this(clazz, false);
		}
		@SuppressWarnings("LeakingThisInConstructor")
		Clazz(Class<?> clazz, boolean isNumeric)
		{
			mapClazz.put(clazz, this);
			this.isNumeric = isNumeric;
		}

		boolean isNumeric()
		{
			return isNumeric;
		}
	}

}
