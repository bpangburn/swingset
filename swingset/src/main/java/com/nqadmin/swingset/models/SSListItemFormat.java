/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
package com.nqadmin.swingset.models;

import java.sql.JDBCType;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

import com.nqadmin.swingset.models.AbstractComboBoxListSwingModel.ListItem0;

// SSListItemFormat.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Use this to produce a string representation of an SSListItem.
 * Configure the order in which the list item elements are formatted,
 * and each element type, with {@link #addElemType}.
 * After this object is created, by default
 * element 0 is formatted with toString(). Start with {@link #clear()}
 * to set up a different formatting specification.
 * <p>
 * Time related elements can have a format pattern specified;
 * The time patterns use {@link java.text.SimpleDateFormat}.
 * If the pattern is null, then toString() is used to format the pattern.
 * <pre>
 * {@code
 * The default patterns are:
 *    JDBCType.DATE       "yyyy/MM/dd"
 *    JDBCType.TIME,      "HH:mm:ss"
 *    JDBCType.TIMESTAMP, "yyyy/MM/dd'T'HH:mm:ss"
 * }
 * </pre>
 * <p>
 * Use {@link #format(java.lang.Object)}, where the argument
 * is an SSListItem, to get the String representation. Note
 * that if format's argument is not an SSListItem, then an
 * empty String is produced.
 * <p>
 * This is compatible with GlazedLists AutoCompleteSupport.
 * 
 * @since 4.0.0
 */
// TODO: Handle considerably more config for specific elem formatting
//		map: {elemIndex:jdbcType} DONE
//		map: {jdbcType:format} NO, there's a format for DATE
//		map: {elemIndex:format} to override the defaults
//		possibly option to specify function associated with elemIndex
//
// TODO: How about an object which describes an SSListItem properties,
//		 this would include elements database type, format, ...
//		 Then use this in preference to individually configuring elems.
//		 But there's typically only one elem of SSListItem that
//		 contributes to string description, so...
//
public class SSListItemFormat extends Format {
	public static final String dateDefault = "yyyy/MM/dd";
	public static final String timeDefault = "HH:mm:ss";
	public static final String timestampDefault = "yyyy/MM/dd'T'HH:mm:ss";
	private static final long serialVersionUID = 1L;
	/** default elem separator */
	public static final String defaultSeparator = " | ";
	private static final FieldPosition FP0 = new FieldPosition(0);

	private String separator = defaultSeparator;
	/** elemTypes.get(elemIndex) == jdbcType. Cheap map. Null is unspecified type */
	protected List<JDBCType> elemTypes = new ArrayList<>(4);
	/** format these item elem in order */
	protected List<Integer> itemElemIndexes = new ArrayList<>(4);

	// allow customization of date/time formats
	private EnumMap<JDBCType, String> patterns = new EnumMap<>(JDBCType.class);

	
	/**
	 * Create a Format. Use {@code addElemType} to specify
	 * elements, in order, that are formatted.
	 * By default, element 0 is formatted with toString()
	 */
	public SSListItemFormat() {
		// format elment 0 with toString()
		addElemType(0, JDBCType.NULL);

		// initialize default format patterns
		patterns.put(JDBCType.DATE, dateDefault);
		patterns.put(JDBCType.TIME, timeDefault);
		patterns.put(JDBCType.TIMESTAMP, timestampDefault);
	}

	/**
	 * Clear list item element information in preparation
	 * to establish elements to format.
	 * Note that default formatting patterns are not restored.
	 */
	public void clear() {
		elemTypes.clear();
		itemElemIndexes.clear();
	}

	/**
	 * Add element for formatting. Elements are formatted in
	 * the same order as they are added. If the same elemIndex
	 * is added, the previous information is discarded.
	 * 
	 * @param _elemIndex ListItem elemIndex for formatting
	 * @param _jdbcType type of element
	 */
	public void addElemType(int _elemIndex, JDBCType _jdbcType) {
		Objects.requireNonNull(_jdbcType);
		// first make sure there's room
		while (_elemIndex >= elemTypes.size()) {
			elemTypes.add(null);
		}
		elemTypes.set(_elemIndex, _jdbcType);
		Integer indexAsObject = _elemIndex;
		itemElemIndexes.remove(indexAsObject);
		itemElemIndexes.add(indexAsObject);
	}

	/**
	 * Set SimpleDateFormat's format pattern to use for the specified jdbc type.
	 * Only DATE, TIME, TIMESTAMP jdbctype are allowed. If the pattern
	 * is null, then toString is used for the specified type.
	 * 
	 * @param _jdbcType all elements of this type use the specified pattern
	 * @param _pattern format pattern
	 * @return the previous format string
	 */
	public String setPattern(JDBCType _jdbcType, String _pattern) {
		if (!patterns.containsKey(_jdbcType)) {
			throw new IllegalArgumentException("JDBCType " + _jdbcType + " not handled");
		}
		return patterns.put(_jdbcType, _pattern);
	}

	/**
	 * Get the pattern used for the specified JDBCType.
	 * @param _jdbcType the JDBCTyep
	 * @return the pattern or null
	 */
	public String getPattern(JDBCType _jdbcType) {
		if (!patterns.containsKey(_jdbcType)) {
			throw new IllegalArgumentException("JDBCType " + _jdbcType + " not handled");
		}
		return patterns.get(_jdbcType);
	}

	/**
	 * The separator is goes between elements in a formatted string.
	 * @param separator the separator
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}
	
	@Override
	public Object parseObject(String source, ParsePosition pos) {
		// Do not create objects from here
		return source;
	}
	
	@Override
	public StringBuffer format(Object _listItem, StringBuffer toAppendTo, FieldPosition pos) {
		if (_listItem != null && _listItem instanceof ListItem0) {
			// GlazedLists guarentees only format(Object), so ignore pos.
			ListItem0 listItem = (ListItem0)_listItem;
			for (int i = 0; i < itemElemIndexes.size(); i++) {
				// if this isn't the first element, add the separator
				if (i != 0) {
					toAppendTo.append(separator);
				}
				int elemIndex = itemElemIndexes.get(i);
				appendValue(toAppendTo, elemIndex, listItem);
			}
		}
		return toAppendTo;
	}

	/**
	 * Format the indicated element, by default use toString().
	 * @param _sb append string value to this
	 * @param _elemIndex index of element
	 * @param _listItem container holding the element
	 */
	protected void appendValue(StringBuffer _sb, int _elemIndex, ListItem0 _listItem) {
		Object elem = _listItem.getElem(_elemIndex);
		JDBCType jdbcType = elemTypes.get(_elemIndex);
		switch(jdbcType) {
		case DATE:
		case TIME:
		case TIMESTAMP:
			String pattern = patterns.get(jdbcType);
			if (pattern != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
				dateFormat.format(elem, _sb, FP0);
			} else {
				_sb.append(elem.toString());
			}
			break;
		default:
			if(elem != null) {
				_sb.append(elem.toString());
			}
			break;
		}
	}
	
}
