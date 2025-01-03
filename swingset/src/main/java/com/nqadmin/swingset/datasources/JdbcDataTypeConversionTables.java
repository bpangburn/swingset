/* *****************************************************************************
 * Copyright (C) 2024, Ernie R Rael. All rights reserved.
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

import java.sql.JDBCType;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import static java.sql.JDBCType.*;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;

/**
 * Data Type Conversion Tables;
 * Appendix B of JDBC 4.2 Specification JSR 221, March 2014.
 * In this class the tables are usually {@link ImmutableListMultimap}s,
 * jdbcTypeToClass is an exception, it's based on an EnumMap.
 * In addition to the tables, there is a method with the same name as the
 * table which takes a key as a parameter
 * and generally returns the first item in the list;
 * see {@link #jdbcTypeToClassStrict(java.sql.JDBCType) } for an exception.
 * <p>
 * Table B.3 is updated for the {@code java1.8} and later world.
 */
public class JdbcDataTypeConversionTables {

	private JdbcDataTypeConversionTables() {}

	/**
	 * Map JdbcType to Java Object Class;  JDBC 4.2 spec, Appendix B Table 3.
	 * <p>
	 * The differences from spec with their return values are
	 * <ul>
	 * <li> TINYINT Byte
	 * <li> SMALLINT Short
	 * <li> DATE java.time.LocalDate
	 * <li> TIME java.time.LocalTime
	 * <li> TIMESTAMP java.time.LocalDateTime
	 * <li> TIME_WITH_TIMEZONE java.time.OffetTime
	 * <li> TIMESTAMP_WITH_TIMEZONE java.time.OffetDateTime
	 * </ul>
	 * <p>
	 * Note that {@code TIME_WITH_TIMEZONE}
	 * and {@code TIMESTAMP_WITH_TIMEZONE }
	 * are not found at all in Table 3.
	 * <p>
	 * Use {@link #jdbcTypeToClassStrict(java.sql.JDBCType) } for strict
	 * adherence to the spec.
	 * 
	 * @param type JDBCType
	 * @return corresponding java object class
	 */
	public static Class<?> jdbcTypeToClass(JDBCType type) {
		List<Class<?>> l = (List<Class<?>>)jdbcTypeToClass.get(type);
		return l == null || l.isEmpty() ? null : l.get(0);
	}

	@SuppressWarnings("unused")
	private static final Set<JDBCType> hasStrictJavaTypeClass = EnumSet.of(
			TINYINT, SMALLINT, DATE, TIME, TIMESTAMP
	);

	@SuppressWarnings("unused")
	private static final Set<JDBCType> tzStrictJavaTypeClass = EnumSet.of(
			TIME_WITH_TIMEZONE, TIMESTAMP_WITH_TIMEZONE
	);

	/**
	 * Map JdbcType to Java Object Class;  JDBC 4.2 spec, Appendix B Table 3.
	 * 
	 * @param type JDBCType
	 * @return corresponding java object class
	 */
	public static Class<?> jdbcTypeToClassStrict(JDBCType type) {
		List<Class<?>> l = (List<Class<?>>)jdbcTypeToClass.get(type);

		// TODO: Use EnumSets to decide?

		return switch (type) {
		case TINYINT, SMALLINT, DATE, TIME, TIMESTAMP -> l.get(1);
		case TIME_WITH_TIMEZONE,TIMESTAMP_WITH_TIMEZONE -> null;
		default -> l == null || l.isEmpty() ? null : l.get(0);
		};
	}

	/**
	 * Map Java Object to JdbcTypes; JDBC 4.2 spec, Appendix B Table 4
	 * <p>
 	 * Note that the elements of the map are a list;
	 * typically do {@code ClassToJDBCType.get(String.class).get(0)}
	 * to get an item.
	 * @param _clazz class to lookup
	 * @return jdbc type corresponding to class
	 */
	public static JDBCType classToJdbcType(Class<?> _clazz) {
		ImmutableList<JDBCType> l = classToJdbcType.get(_clazz);
		return l.isEmpty() ? null : l.get(0);
	}

	/**
	 * Map JdbcType to Java Object;  JDBC 4.2 spec, Appendix B Table 3.
	 * <p>
	 * <b>Implementation note:</b>
	 * <p>
	 * The JDBC 4.2 spec only has one class listed per JDBCType since they
	 * are the types returned by getObject for a given type. However, for
	 * certain types (not constrained to JDBC 1.0) newer JDK types are
	 * recommended and or course there are the new *WITH_TIMEZONE types.
	 * Since this is a list multimap, the updated information is included
	 * and is provided by the default method
	 * {@link #jdbcTypeToClass(java.sql.JDBCType) }; see that method's javadoc
	 * for the details of the change.
	 * To get strict adherence to the spec,
	 * use {@link #jdbcTypeToClassStrict(java.sql.JDBCType) }
	 */
	public static final Map<JDBCType, Collection<Class<?>>> jdbcTypeToClass = createTable3();

	// Want immutable EnumListMultimap, but it doesn't exist
	// https://github.com/google/guava/issues/977

	/**
	 * Guava has no immutable enum multimap, so...
	 * @return unmodifiable enum multimap
	 */
	private static Map<JDBCType, Collection<Class<?>>> createTable3() {

		ListMultimap<JDBCType, Class<?>> table3 = MultimapBuilder
				.enumKeys(JDBCType.class)
				.arrayListValues(2)
				.build()
				;
		table3.put(CHAR, String.class);
		table3.put(VARCHAR, String.class);
		table3.put(LONGVARCHAR, String.class);
		table3.put(NUMERIC, java.math.BigDecimal.class);
		table3.put(DECIMAL, java.math.BigDecimal.class);
		table3.put(BIT, Boolean.class);
		table3.put(BOOLEAN, Boolean.class);

		table3.putAll(TINYINT, List.of(Byte.class, Integer.class));
		table3.putAll(SMALLINT, List.of(Short.class, Integer.class));

		table3.put(INTEGER, Integer.class);
		table3.put(BIGINT, Long.class);
		table3.put(REAL, Float.class);
		table3.put(FLOAT, Double.class);
		table3.put(DOUBLE, Double.class);
		table3.put(BINARY, byte[].class);
		table3.put(VARBINARY, byte[].class);
		table3.put(LONGVARBINARY, byte[].class);

		table3.putAll(DATE, List.of(java.time.LocalDate.class, java.sql.Date.class));
		table3.putAll(TIME, List.of(java.time.LocalTime.class, java.sql.Time.class));
		table3.putAll(TIMESTAMP, List.of(java.time.LocalDateTime.class, java.sql.Timestamp.class));

		table3.put(TIME_WITH_TIMEZONE, java.time.OffsetTime.class);
		table3.put(TIMESTAMP_WITH_TIMEZONE, java.time.OffsetDateTime.class);
		// DISTINCTObject type of underlying type.class);
		table3.put(CLOB, java.sql.Clob.class);
		table3.put(BLOB, java.sql.Blob.class);
		table3.put(ARRAY, java.sql.Array.class);
		// STRUCT java.sql.Struct or.class or java.sql.SQLData.class;
		table3.put(REF, java.sql.Ref.class);
		table3.put(DATALINK, java.net.URL.class);
		// JAVA_OBJECT Underlying Java class.class;
		table3.put(ROWID, java.sql.RowId.class);
		table3.put(NCHAR, String.class);
		table3.put(NVARCHAR, String.class);
		table3.put(LONGNVARCHAR, String.class);
		table3.put(NCLOB, java.sql.NClob.class);
		table3.put(SQLXML, java.sql.SQLXML.class);

		return Multimaps.unmodifiableListMultimap(table3).asMap();
	}

	/**
	 * Map Java Object to JdbcTypes; JDBC 4.2 spec, Appendix B Table 4
	 * <p>
 	 * Note that the elements of the map are a list.
	 * Typically use the method {@link #classToJdbcType(java.lang.Class) }
	 * which returns the first JDBCType for the class with
	 * {@code ClassToJDBCType.get(Some.class).get(0)}
	 * to get an item.
	 */
	public static final ImmutableListMultimap<Class<?>, JDBCType> classToJdbcType
			= ImmutableListMultimap.<Class<?>, JDBCType>builder()
				.putAll(String.class, CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR)
					// include DECIMAL
				.put(java.math.BigDecimal.class, NUMERIC)
				.putAll(Boolean.class, BIT, BOOLEAN)
					// note Byte not Integer
				.put(Byte.class, TINYINT)
					// note Short not Integer
				.put(Short.class, SMALLINT)
				.put(Integer.class, INTEGER)
				.put(Long.class, BIGINT)
				.put(Float.class, REAL)
					// include FLOAT
				.putAll(Double.class, DOUBLE, FLOAT)
				.putAll(byte[].class, BINARY, VARBINARY, LONGVARBINARY)
				.put(java.math.BigInteger.class, BIGINT)
				.put(java.sql.Date.class, DATE)
				.put(java.sql.Time.class, TIME)
				.put(java.sql.Timestamp.class, TIMESTAMP)
				.put(java.sql.Clob.class, CLOB)
				.put(java.sql.Blob.class, BLOB)
				.put(java.sql.Array.class, ARRAY)
				.put(java.sql.Struct.class, STRUCT)
				.put(java.sql.Ref.class, REF)
				.put(java.net.URL.class, DATALINK)
				// java.class JAVA_OBJECT)
				.put(java.sql.RowId.class, ROWID)
				.put(java.sql.NClob.class, NCLOB)
				.put(java.sql.SQLXML.class, SQLXML)
				.put(java.util.Calendar.class, TIMESTAMP)
				.put(java.util.Date.class, TIMESTAMP)
				.put(java.time.LocalDate.class, DATE)
				.put(java.time.LocalTime.class, TIME)
				.put(java.time.LocalDateTime.class, TIMESTAMP)
				.put(java.time.OffsetTime.class, TIME_WITH_TIMEZONE)
				.put(java.time.OffsetDateTime.class, TIMESTAMP_WITH_TIMEZONE)
				.build();

	///////////////////////////////////////////////////////////////////////////
	//
	// Below here are copied from the spec.

	// B.1 JDBC Types Mapped to Java Types
	// ===================================
	// TABLE B-1 shows the conceptual correspondence between JDBC types and Java types.
	// A programmer should write code with this mapping in mind. For example, if a
	// value in the database is a SMALLINT, a short should be the data type used in a
	// JDBC application.
	// All CallableStatement getter methods except for getObject use this mapping.
	// The getObject methods for both the CallableStatement and ResultSet
	// interfaces use the mapping in TABLE B-3.
	// TABLE B-1 JDBC Types Mapped to Java Types
	// -----------------------------------

	/*
	JdbcAppxB_1

	CHAR String
	VARCHAR String
	LONGVARCHAR String
	NUMERIC java.math.BigDecimal
	DECIMAL java.math.BigDecimal
	BIT boolean
	BOOLEAN boolean
	TINYINT byte
	SMALLINT short
	INTEGER int
	BIGINT long
	REAL float
	FLOAT double
	DOUBLE double
	BINARY byte[]
	VARBINARY byte[]
	LONGVARBINARY byte[]
	DATE java.sql.Date
	TIME java.sql.Time
	TIMESTAMP java.sql.Timestamp
	CLOB java.sql.Clob
	BLOB java.sql.Blob
	ARRAY java.sql.array
	DISTINCT Mapping of underlying type
	STRUCT java.sql.Struct
	REF java.sql.Ref
	DATALINK java.net.URL
	JAVA_OBJECT Underlying Java class
	ROWID java.sql.RowId
	NCHAR String
	NVARCHAR String
	LONGNVARCHAR String
	NCLOB java.sql.NClob
	SQLXML java.sql.SQLXML
	*/

	// B.2 Java Types Mapped to JDBC Types
	// ===================================
	// TABLE B-2 shows the mapping a driver should use for the updater methods in the
	// ResultSet interface and for IN parameters. PreparedStatement setter methods
	// and RowSet setter methods use this table for mapping an IN parameter, which is a
	// Java type, to the JDBC type that will be sent to the database. Note that the
	// setObject methods for these two interfaces use the mapping shown in TABLE B-4.
	// TABLE B-2 Standard Mapping from Java Types to JDBC Types
	// -----------------------------------

	/*
	JdbcAppxB_2

	String CHAR, VARCHAR, LONGVARCHAR, NCHAR,
		NVARCHAR, LONGNVARCHAR
	java.math.BigDecimal NUMERIC
	boolean BIT, BOOLEAN
	byte TINYINT
	short SMALLINT
	int INTEGER
	long BIGINT
	float REAL
	double DOUBLE
	byte[] BINARY, VARBINARY, LONGVARBINARY
	java.sql.Date DATE
	java.sql.Time TIME
	java.sql.Timestamp TIMESTAMP
	java.sql.Clob CLOB
	java.sql.Blob BLOB
	java.sql.Array ARRAY
	java.sql.Struct STRUCT
	java.sql.Ref REF
	java.net.URL DATALINK
	Java class JAVA_OBJECT
	java.sql.RowId ROWID
	java.sql.NClob NCLOB
	java.sql.SQLXML SQLXML
	*/


	// B.3 JDBC Types Mapped to Java Object Types
	// ===================================
	// ResultSet.getObject and CallableStatement.getObject use the mapping
	// shown in TABLE B-3 for standard mappings.
	// Note – The JDBC 1.0 specification defined the Java object mapping for the
	// SMALLINT and TINYINT JDBC types to be Integer. The Java language did not
	// include the Byte and Short data types when the JDBC 1.0 specification was
	// finalized. The mapping of SMALLINT and TINYINT to Integer is maintained to
	// preserve backwards compatibility.
	// TABLE B-3 Mapping from JDBC Types to Java Object Types
	// -----------------------------------

	/*
	JdbcAppxB_3

	CHAR String
	VARCHAR String
	LONGVARCHAR String
	NUMERIC java.math.BigDecimal
	DECIMAL java.math.BigDecimal
	BIT Boolean
	BOOLEAN Boolean
	TINYINT Integer
	SMALLINT Integer
	INTEGER Integer
	BIGINT Long
	REAL Float
	FLOAT Double
	DOUBLE Double
	BINARY byte[]
	VARBINARY byte[]
	LONGVARBINARY byte[]
	DATE java.sql.Date
	TIME java.sql.Time
	TIMESTAMP java.sql.Timestamp
	DISTINCT Object type of underlying type
	CLOB java.sql.Clob
	BLOB java.sql.Blob
	ARRAY java.sql.Array
	STRUCT java.sql.Struct or
	java.sql.SQLData
	REF java.sql.Ref
	DATALINK java.net.URL
	JAVA_OBJECT Underlying Java class
	ROWID java.sql.RowId
	NCHAR String
	NVARCHAR String
	LONGNVARCHAR String
	NCLOB java.sql.NClob
	SQLXML java.sql.SQLXML
	*/

	// B.4 Java Object Types Mapped to JDBC Types
	// ===================================
	// PreparedStatement.setObject, PreparedStatement.setNull, RowSet.setNull and
	// RowSet.setObject use the mapping shown TABLE B-4 when no parameter specifying a
	// target JDBC type is provided.
	// TABLE B-4 Mapping from Java Object Types to JDBC Types
	// -----------------------------------

	/*
	JdbcAppxB_4

	String CHAR, VARCHAR, LONGVARCHAR, NCHAR,
		NVARCHAR, LONGNVARCHAR
	java.math.BigDecimal NUMERIC
	Boolean BIT, BOOLEAN
	Byte TINYINT
	Short SMALLINT
	Integer INTEGER
	Long BIGINT
	Float REAL
	Double DOUBLE
	byte[] BINARY, VARBINARY, LONGVARBINARY
	java.math.BigInteger BIGINT
	java.sql.Date DATE
	java.sql.Time TIME
	java.sql.Timestamp TIMESTAMP
	java.sql.Clob CLOB
	java.sql.Blob BLOB
	java.sql.Array ARRAY
	java.sql.Struct STRUCT
	java.sql.Ref REF
	java.net.URL DATALINK
	Java class JAVA_OBJECT
	java.sql.RowId ROWID
	java.sql.NClob NCLOB
	java.sql.SQLXML SQLXML
	java.util.Calendar TIMESTAMP
	java.util.Date TIMESTAMP
	java.time.LocalDate DATE
	java.time.LocalTime TIME
	java.time.LocalDateTime TIMESTAMP
	java.time.OffsetTime TIME_WITH_TIMEZONE
	java.time.OffsetDatetime TIMESTAMP_WITH_TIMEZONE
	*/


	// B.5 Conversions by setObject and setNull from Java Object Types to JDBC Types
	// ===================================
	// TABLE B-5 shows which JDBC types may be specified as the target JDBC type to the
	// methods PreparedStatement.setObject, PreparedStatement.setNull,
	// RowSet.setNull, and RowSet.setObject.
	// TABLE B-5 Conversions Performed by setObject and setNull Between Java Object Types and Target JDBC Types
	// -----------------------------------

	/*
	JdbcAppxB_5

	String TINYINT, SMALLINT, INTEGER, BIGINT, REAL,
		FLOAT, DOUBLE, DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR, LONGVARCHAR,
		BINARY, VARBINARY, LONVARBINARY, DATE,
		TIME, TIMESTAMP, NCHAR, NVARCHAR,
		LONGNVARCHAR
	java.math.BigDecimal TINYINT, SMALLINT, INTEGER, BIGINT, REAL,
		FLOAT, DOUBLE, DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR, LONGVARCHAR
	Boolean TINYINT, SMALLINT, INTEGER, BIGINT, REAL,
		FLOAT, DOUBLE, DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR, LONGVARCHAR
	Byte TINYINT, SMALLINT, INTEGER, BIGINT, REAL,
		FLOAT, DOUBLE, DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR, LONGVARCHAR
	Short TINYINT, SMALLINT, INTEGER, BIGINT, REAL,
		FLOAT, DOUBLE, DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR, LONGVARCHAR
	Integer TINYINT, SMALLINT, INTEGER, BIGINT, REAL,
		FLOAT, DOUBLE, DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR, LONGVARCHAR
	Long TINYINT, SMALLINT, INTEGER, BIGINT, REAL,
		FLOAT, DOUBLE, DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR, LONGVARCHAR
	Float TINYINT, SMALLINT, INTEGER, BIGINT, REAL,
		FLOAT, DOUBLE, DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR, LONGVARCHAR
	Double TINYINT, SMALLINT, INTEGER, BIGINT, REAL,
		FLOAT, DOUBLE, DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR, LONGVARCHAR
	byte[] BINARY, VARBINARY, LONGVARBINARY
	java.math.BigInteger BIGINT, CHAR, VARCHAR, LONGVARCHAR
	java.sql.Date CHAR, VARCHAR, LONGVARCHAR, DATE, TIMESTAMP
	java.sql.Time CHAR, VARCHAR, LONGVARCHAR, TIME, TIMESTAMP
	java.sql.Timestamp CHAR, VARCHAR, LONGVARCHAR, DATE, TIME, TIMESTAMP
	java.sql.Array ARRAY
	java.sql.Blob BLOB
	java.sql.Clob CLOB
	java.sql.Struct STRUCT
	java.sql.Ref REF
	java.net.URL DATALINK
	Java class JAVA_OBJECT
	java.sql.RowId ROWID
	java.sql.NClob NCLOB
	java.sql.SQLXML SQLXML
	java.util.Calendar CHAR, VARCHAR, LONGVARCHAR, DATE, TIME, TIMESTAMP, ARRAY
	java.util.Date CHAR, VARCHAR, LONGVARCHAR, DATE, TIME, TIMESTAMP, ARRAY
	java.time.LocalDate CHAR, VARCHAR, LONGVARCHAR, DATE
	java.time.LocalTime CHAR, VARCHAR, LONGVARCHAR, TIME
	java.time.LocalDateTime CHAR, VARCHAR, LONGVARCHAR, DATE, TIME, TIMESTAMP
	java.time.OffsetTime CHAR, VARCHAR, LONGVARCHAR, TIME_WITH_TIMEZONE
	java.time.OffsetDatetime CHAR, VARCHAR, LONGVARCHAR,
		TIME_WITH_TIMEZONE, TIMESTAMP_WITH_TIMEZONE
	*/

	// B.6 Type Conversions Supported by ResultSet getter Methods
	// ===================================
	// TABLE B-6 shows which JDBC types may be returned by ResultSet getter methods.
	// This table also shows the conversions used by the SQLInput reader methods, except
	// that they use only the recommended conversions.
	// TABLE B-6 Use of ResultSet getter Methods to Retrieve JDBC Data Types
	// -----------------------------------

	/*
	JdbcAppxB_6

	getByte TINYINT
		TINYINT, SMALLINT, INTEGER,
		BIGINT, REAL, FLOAT, DOUBLE,
		DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR,
		LONGVARCHAR, ROWID
	getShort SMALLINT
		TINYINT, SMALLINT, INTEGER,
		BIGINT, REAL, FLOAT, DOUBLE,
		DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR,
		LONGVARCHAR
	getInt INTEGER
		TINYINT, SMALLINT, INTEGER,
		BIGINT, REAL, FLOAT, DOUBLE,
		DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR,
		LONGVARCHAR
	getLong BIGINT
		TINYINT, SMALLINT, INTEGER,
		BIGINT, REAL, FLOAT, DOUBLE,
		DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR,
		LONGVARCHAR
	getFloat REAL
		TINYINT, SMALLINT, INTEGER,
		BIGINT, REAL, FLOAT, DOUBLE,
		DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR,
		LONGVARCHAR
	getDouble FLOAT, DOUBLE
		TINYINT, SMALLINT, INTEGER,
		BIGINT, REAL, FLOAT, DOUBLE,
		DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR,
		LONGVARCHAR
	getBigDecimal DECIMAL, NUMERIC
		TINYINT, SMALLINT, INTEGER,
		BIGINT, REAL, FLOAT, DOUBLE,
		DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR,
		LONGVARCHAR
	getBoolean BIT, BOOLEAN
		TINYINT, SMALLINT, INTEGER,
		BIGINT, REAL, FLOAT, DOUBLE,
		DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR,
		LONGVARCHAR
	getString CHAR, VARCHAR
		TINYINT, SMALLINT, INTEGER,
		BIGINT, REAL, FLOAT, DOUBLE,
		DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR,
		LONGVARCHAR, BINARY,
		VARBINARY, LONVARBINARY,
		DATE, TIME, TIMESTAMP,
		DATALINK, NCHAR, NVARCHAR,
		LONGNVARCHAR
	getNString NCHAR, NVARCHAR
		TINYINT, SMALLINT, INTEGER,
		BIGINT, REAL, FLOAT, DOUBLE,
		DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR,
		LONGVARCHAR, BINARY,
		VARBINARY, LONVARBINARY,
		DATE, TIME, TIMESTAMP,
		DATALINK, NCHAR, NVARCHAR,
		LONGNVARCHAR
	getBytes BINARY, VARBINARY
		BINARY, VARBINARY,
		LONGVARBINARY
	getDate DATE
		CHAR, VARCHAR,
		LONGVARCHAR, DATE,
		TIMESTAMP
	getTime TIME
		CHAR, VARCHAR,
		LONGVARCHAR, TIME,
		TIMESTAMP
	getTimestamp TIMESTAMP
		CHAR, VARCHAR,
		LONGVARCHAR, DATE, TIME,
		TIMESTAMP
	getAsciiStream LONGVARCHAR
		CHAR, VARCHAR,
		LONGVARCHAR, BINARY,
		VARBINARY, LONGVARBINARY,
		CLOB, NCLOB
	getBinaryStream LONGVARBINARY
		BINARY, VARBINARY,
		LONGVARBINARY
	getCharacterStream LONGVARCHAR
		CHAR, VARCHAR,
		LONGVARCHAR, BINARY,
		VARBINARY, LONGVARBINARY,
		CLOB, NCHAR, NVARCHAR,
		LONGNVARCHAR, NCLOB,
		SQLXML
	getNCharacterStream LONGNVARCHAR
		CHAR, VARCHAR,
		LONGVARCHAR, BINARY,
		VARBINARY, LONGVARBINARY,
		CLOB, NCHAR, NVARCHAR,
		LONGNVARCHAR, NCLOB,
		SQLXML
	getClob CLOB
		CLOB, NCLOB
	getNClob NCLOB
		CLOB, NCLOB
	getBlob BLOB
		BLOB
	getArray ARRAY
		ARRAY
	getRef REF
		REF
	getURL DATALINK
		DATALINK
	getObject STRUCT, JAVA_OBJECT
		TINYINT, SMALLINT, INTEGER,
		BIGINT, REAL, FLOAT, DOUBLE,
		DECIMAL, NUMERIC, BIT,
		BOOLEAN, CHAR, VARCHAR,
		LONGVARCHAR, BINARY,
		VARBINARY, LONVARBINARY,
		DATE, TIME, TIMESTAMP, CLOB,
		BLOB, ARRAY, REF, DATALINK,
		STRUCT, JAVA_OBJECT, ROWID,
		NCHAR, NVARCHAR,
		LONGNVARCHAR, NCLOB,
		SQLXML, TIME_WITH_TIMEZONE,
		TIMESTAMP_WITH_TIMEZONE
	getRowId ROWID
		ROWID
	getSQLXML SQLXML
		SQLXML
	*/


}
