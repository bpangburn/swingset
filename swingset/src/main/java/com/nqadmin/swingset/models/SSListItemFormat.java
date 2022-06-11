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

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.models.AbstractComboBoxListSwingModel.ListItem0;
import com.nqadmin.swingset.utils.SSUtils;

// SSListItemFormat.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Use this to produce a string representation of an SSListItem.
 * Configure the order in which the list item elements are formatted,
 * each element type, and optionally Format,
 * with {@link #addElemType(int, java.sql.JDBCType, java.text.Format) }.
 * After this object is created, by default
 * element 0 is formatted with toString(). Start with {@link #clear()}
 * to set up a different formatting specification.
 * <p>
 * Each JDBCType can have a default Format specified; use
 * {@link #setFormat(java.sql.JDBCType, java.text.Format) }.
 * There are preset defaults for the data/time types
 * <pre>
 * {@code
 * The builtin default Formats {@link java.text.SimpleDateFormat} are:
 *    JDBCType.DATE       "yyyy/MM/dd"
 *    JDBCType.TIME,      "HH:mm:ss"
 *    JDBCType.TIMESTAMP, "yyyy/MM/dd'T'HH:mm:ss"
 * }
 * </pre>
 * When an elem is formatted, first a format assigned to the elem is checked,
 * then the default format for the elem type is checked,
 * if neither is available, then toString() is used to format the elem.
 * the default time formats use .
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
public class SSListItemFormat extends Format {
	/** default date format */
	public static final String dateDefault = "yyyy/MM/dd";
	/** default time format */
	public static final String timeDefault = "HH:mm:ss";
	/** default timestamp format */
	public static final String timestampDefault = "yyyy/MM/dd'T'HH:mm:ss";
	private static final long serialVersionUID = 1L;
	/** default elem separator */
	public static final String defaultSeparator = " | ";
	private static final FieldPosition FP0 = new FieldPosition(0);

	private String separator = defaultSeparator;
	/** elemInfos.get(elemIndex) == elemInfo. */
	protected List<ElemInfo> elemInfos = new ArrayList<>(4);
	/** format these elem in order of List. */
	protected List<Integer> itemElemIndexes = new ArrayList<>(4);

	// allow customization of date/time formats
	private final EnumMap<JDBCType, Format> formats = new EnumMap<>(JDBCType.class);

	private static Logger logger = SSUtils.getLogger();

	/**
	 * Encapsulate info about element in SSListInfo.
	 */
	protected static class ElemInfo {
		/** type of the elem */
		final JDBCType type;
		/** Format to use with this elem, may be null */
		final Format format;

		/**
		 * @param type
		 * @param format
		 */
		protected ElemInfo(JDBCType type, Format format) {
			this.type = type;
			this.format = format;
		}
	}

	
	/**
	 * Create a Format. Use {@code addElemType} to specify
	 * elements, in order, that are formatted.
	 * By default, element 0 is formatted with toString()
	 */
	@SuppressWarnings("OverridableMethodCallInConstructor")
	public SSListItemFormat() {
		// format elment 0 with toString()
		addElemType(0, JDBCType.NULL);

		// initialize default format patterns
		formats.put(JDBCType.DATE,      new SimpleDateFormat(dateDefault));
		formats.put(JDBCType.TIME,      new SimpleDateFormat(timeDefault));
		formats.put(JDBCType.TIMESTAMP, new SimpleDateFormat(timestampDefault));
	}

	/**
	 * Clear list item element information in preparation
	 * to establish elements to format.
	 * Note that default formatting patterns are not restored.
	 */
	public void clear() {
		elemInfos.clear();
		itemElemIndexes.clear();
	}

	/**
	 * Add element for formatting.Elements are formatted in
	 * the same order as they are added. If the same elemIndex
 	 * is added, the previous information is discarded.
	 * 
	 * @param _elemIndex ListItem elemIndex for formatting
	 * @param _jdbcType type of element
	 * @param _format format to use for the element, may be null
	 */
	public void addElemType(int _elemIndex, JDBCType _jdbcType, Format _format) {
		Objects.requireNonNull(_jdbcType);
		// first make sure there's room
		while (_elemIndex >= elemInfos.size()) {
			elemInfos.add(null);
		}
		elemInfos.set(_elemIndex, new ElemInfo(_jdbcType, _format));

		// SSListItem is formatted in the order the items are added
		Integer indexAsObject = _elemIndex;
		itemElemIndexes.remove(indexAsObject);
		itemElemIndexes.add(indexAsObject);
	}

	/**
	 * Add element for formatting.Elements are formatted in
	 * the same order as they are added. If the same elemIndex
 	 * is added, the previous information is discarded.
	 * The default Format for this type is used.
	 * 
	 * @param _elemIndex ListItem elemIndex for formatting
	 * @param _jdbcType type of element
	 */
	public void addElemType(int _elemIndex, JDBCType _jdbcType) {
		addElemType(_elemIndex, _jdbcType, null);
	}

	/**
	 * Set the default Format for the specified jdbc type.
	 * Only the {@link Format#format(Object, StringBuffer, java.text.FieldPosition)}
	 * method is used with the argument Format.
	 * @param _jdbcType all elements of this type use the specified format
	 * @param _format the format
	 * @return the previous format
	 */
	public Format setFormat(JDBCType _jdbcType, Format _format) {
		return formats.put(_jdbcType, _format);
	}

	/**
	 * Get the default Format for the specified JDBCType.
	 * @param _jdbcType
	 * @return format or null if no format has been set
	 */
	public Format getFormat(JDBCType _jdbcType) {
		return formats.get(_jdbcType);
	}

	/**
	 * Set SimpleDateFormat's format pattern to use for the specified jdbc type.
	 * Only DATE, TIME, TIMESTAMP jdbctype are allowed. If the pattern
	 * is null, then toString is used for the specified type.
	 * 
	 * @param _jdbcType all elements of this type use the specified pattern
	 * @param _pattern format pattern
	 * @return the previous format string
	 * @deprecated Use {@link #setFormat(java.sql.JDBCType, java.text.Format) }
	 */
	@Deprecated
	public String setPattern(JDBCType _jdbcType, String _pattern) {
		if (!formats.containsKey(_jdbcType)) {
			throw new IllegalArgumentException("JDBCType " + _jdbcType + " not handled");
		}
		String pat = null;
		Format f;
		if (_pattern == null) {
			f = formats.put(_jdbcType, null);
		} else {
			f = formats.put(_jdbcType, new SimpleDateFormat(_pattern));
		}
		if (f instanceof SimpleDateFormat) {
			pat = ((SimpleDateFormat)f).toPattern();
		}
		return pat;
	}

	/**
	 * Get the pattern used for the specified JDBCType.
	 * @param _jdbcType the JDBCTyep
	 * @return the pattern or null
	 */
	@Deprecated
	public String getPattern(JDBCType _jdbcType) {
		if (!formats.containsKey(_jdbcType)) {
			throw new IllegalArgumentException("JDBCType " + _jdbcType + " not handled");
		}
		String pat = null;
		Format f = formats.get(_jdbcType);
		if (f instanceof SimpleDateFormat) {
			pat = ((SimpleDateFormat)f).toPattern();
		}
		return pat;
	}

	/**
	 * The separator is goes between elements in a formatted string.
	 * @param _separator the separator
	 */
	public void setSeparator(String _separator) {
		separator = _separator;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}
	
	/**
	 * @param source
	 * @param pos
	 * @return
	 */
	@Override
	public Object parseObject(String source, ParsePosition pos) {
		// Do not create objects from here
		return source;
	}
	
	/**
	 * Note that pos is ignored. (at least for now)
	 * @param _listItem
	 * @param toAppendTo
	 * @param pos
	 * @return
	 */
	@Override
	public StringBuffer format(Object _listItem, StringBuffer toAppendTo, FieldPosition pos) {
		if (_listItem != null && _listItem instanceof ListItem0) {
			// GlazedLists guarantees only format(Object), so ignore pos.
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
	 * This convenience method allows overriding classes to access
	 * the list item elements directly without going through remodel.
	 * 
	 * @param _elemIndex index of element
	 * @param _listItem container holding the element
	 * @return the element
	 */
	protected Object getElem(int _elemIndex, SSListItem _listItem) {
		return ((ListItem0)_listItem).getElem(_elemIndex);
	}

	/**
	 * Format the indicated element, by default use toString().
	 * @param _sb append string value to this
	 * @param _elemIndex index of element
	 * @param _listItem container holding the element
	 */
	protected void appendValue(StringBuffer _sb, int _elemIndex, SSListItem _listItem) {
		Object elem = getElem(_elemIndex, _listItem);
		if (elem == null) {
			return;
		}
		
		ElemInfo elemInfo = elemInfos.get(_elemIndex);
		JDBCType jdbcType = elemInfo.type;
		Format format = elemInfo.format;
		if (format == null) {
			format = formats.get(jdbcType);
		}
		if (format != null) {
			try {
				format.format(elem, _sb, FP0);
				return;
			} catch (Exception ex) {
				logger.error(String.format("can't format %s with %s. Exception: %s",
						elem.toString(), format.toString(), ex.getMessage()));
			}
		}
		// No formatter, or formatter got an exception
		_sb.append(elem.toString());
	}
	
}
