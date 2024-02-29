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

import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * Different database jdbc drivers support different type conversion;
 * this utility class juggles types around.
 */
public class ConvertType
{
	private ConvertType() { }

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
	public static Object convertObject2JdbcObject(Object value, JDBCType jdbcType)
			throws SQLException
	{
		Class<?> type = findJavaTypeClass(jdbcType);
		if (type.isAssignableFrom(value.getClass()))
			return value;

		switch (value.getClass().getName()) {
		case "java.lang.Boolean" -> {
			switch(jdbcType) { case INTEGER, SMALLINT, TINYINT
					-> { return ((Boolean)value) ? 1 : 0; } } // Boolean to Integer
		}
		case "java.lang.Integer" -> {
			switch(jdbcType) { case BIT, BOOLEAN
					-> { return ((Integer)value) != 0; } } // Integer to Boolean
		}
		}
		throw new SSSQLConversionException(sf("Missing conversion %s to %s", value.getClass().getName(), jdbcType));
	}

	/**
	 * Determine the Java type class for the given database type.
	 * @param _jdbcType JDBCType of interest
	 * @return the class object used for the given type
	 * @throws SQLException if the JDBCType is not handled
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B.3 JDBC Types Mapped to Java Object Types
	 */
	/**
	 * Determine the Java type class for the given database type.
	 * @param _jdbcType JDBCType of interest
	 * @return the class object used for the given type
	 * @throws SQLException if the JDBCType is not handled
	 * @see <a href="https://download.oracle.com/otn-pub/jcp/jdbc-4_3-mrel3-eval-spec/jdbc4.3-fr-spec.pdf">JDBC 4.3 Specification</a> Appendix B.3 JDBC Types Mapped to Java Object Types
	 */
	public static Class<?> findJavaTypeClass(final JDBCType _jdbcType)
	throws SQLException {
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

	// TODO: for override of type mapping for local/dbms requirements
	// with_timezone might be the perfect candidates
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
	
}
