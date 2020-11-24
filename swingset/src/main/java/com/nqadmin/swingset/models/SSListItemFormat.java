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
import java.util.List;

import com.nqadmin.swingset.models.SSAbstractListInfo.ListItem0;

// SSListItemFormat.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Use this to produce a string representation of an SSListItem.
 * Configure information about the list item elements,
 * such as an element's JDBCType.
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
	private static final long serialVersionUID = 1L;
	/** default date format pattern */
	public static final String defaultDatePattern = "yyyy/dd/MM";
	/** default elem separator */
	public static final String defaultSeparator = " | ";
	private static final FieldPosition FP0 = new FieldPosition(0);

	private String datePattern = defaultDatePattern;
	private String separator = defaultSeparator;
	/** elemTypes.get(elemIndex) == jdbcType. Cheap map. Null is unspecified type */
	protected List<JDBCType> elemTypes = new ArrayList<>(4);
	/** format these item elem in order */
	protected List<Integer> itemElemIndexes = new ArrayList<>(4);
	
	/**
	 * Create a Format. Use {@code setElemType} to specify
	 * elements, in order, that are formatted.
	 */
	public SSListItemFormat() {
	}

	/**
	 * Clear list item element information in preparation
	 * to add elements to format.
	 * Note that formatting defaults are not restored.
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
	 * Set the pattern to format dates.
	 * Default pattern in "yyyy/dd/MM".
	 * @param _datePattern pattern used with SimpleDateFormat
	 */
	public void setDatePattern(String _datePattern) {
		datePattern = _datePattern;
	}

	/**
	 * @return the current date pattern
	 */
	public String getDatePattern() {
		return datePattern;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * The separator is goes between elements in a formatted string.
	 * @param separator the separator
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
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
			SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
			dateFormat.format(elem, _sb, FP0);
			break;
		default:
			if(elem != null) {
				_sb.append(elem.toString());
			}
			break;
		}
	}
	
}
