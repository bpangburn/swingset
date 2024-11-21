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
package com.nqadmin.swingset.formatting;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.MaskFormatter;

import com.nqadmin.swingset.formatting.FormatterFactory.SSNullFormatter;

import static com.nqadmin.swingset.formatting.SSMaskFormatterFactory.SSMaskFormatter.FORMATTING_CHARS;
import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * Common things for working with {@link SSFormattedTextField} and their
 * {@linkplain AbstractFormatter}'s. Particularly for implementing
 * AllowNull aware {@linkplain AbstractFormatterFactory}s.
 * This mostly has default implementations and static methods.
 */
interface FormatterAssist
{
	/**
	 * The Format associated with the FormattedTextField.
	 * @return format
	 */
	SSFormat getSSFormat();

	default AbstractFormatter getConverter() { return null; }

	/**
	 * @see AbstractFormatter#stringToValue(java.lang.String) 
	 */
	Object stringToValue(String string) throws ParseException;

	/**
	 * @see AbstractFormatter#valueToString(java.lang.Object) 
	 */
	String valueToString(Object value) throws ParseException;

	/**
	 * Most formats show extra characters that user use has not input.
	 * For a mask this probably includes mask literals and placeholder characters.
	 * @return characters not input by the user
	 */
	default String getFormatLiterals() { return ""; }

	/* *************************************************************************
	 * Example usage: in SSMaskFormatterFactory:
	 *
	 *	public Object stringToValue(String s) throws ParseException {
	 *		if (s == null || s.trim().isEmpty()) {
	 *			// TODO: should check AllowNull, if not return "" ???
	 *			return null;
	 *		}
	 *		// TODO: why/when is this dance needed?
	 *		Object v = super.stringToValue(s);
	 *		return assistStringToValue(v);
	 * ************************************************************************/
	
	/**
	 * If the value is not a String and there is a converter,
	 * then first convert the value before caller's valueToString.
	 * @param value
	 * @return String representation of the value
	 * @throws ParseException
	 */
	//
	// TODO: should this be Object --> Object, let the caller sort it out
	default String assistValueToString(Object value) throws ParseException {
		String s = "";
		if (value != null) {
			if (value instanceof String string) {
				// handle the case where where was null formatter
				s = string;
			} else {
				if (getConverter() != null) {
					s = getConverter().valueToString(value);
				} else {
					s = value.toString();
				}
			}
		}
		return s;
	}
	
	/**
	 * Take the caller's stringToValue,
	 * then use the converter (if there is one) to create
	 * the value object.
	 * @param s
	 * @return
	 * @throws ParseException
	 */
	default Object assistStringToValue(Object s) throws ParseException {
		Object v = s;
		if (getConverter() != null) {
			v = getConverter().stringToValue((String)s);
		}
		return v;
	}
	
	/**
	 * This method is invoked by the formatter when it is almost done.
	 * After setEditValid, if the text field does not have
	 * user input, set the value to null (which switches the formatter).
	 * @param valid
	 */
	default void assistSetEditValid(JFormattedTextField ftf) {
		// Check for empty string input.
		// If empty and allows null, flip to NullFormatter
		if (ftf instanceof SSFormattedTextField ssftf) {
			// If doesn't allow null, don't even consider switching value
			if (!ssftf.getAllowNull()) {
				return;
			}
			if (ssftf.containsUserText()) {
				return;
			}
			ftf.setValue(null);
		}
	}
	
	/**
	 * Ensure that a FormattedTextField's
	 * DefaultFormatterFactory's NullFormatter is set
	 * consistent with getAllowNull. If they do not agree, then
	 * the factory's null formatter is set/cleared as needed.
	 * 
	 * @param ftf 
	 */
	static void adjustNullFormatter(SSFormattedTextField ftf) {
		if (ftf.getFormatterFactory() instanceof FormatterFactory ff) {
			boolean allowNull = ftf.getAllowNull();
			boolean hasNullFormatter = ff.getNullFormatter() != null;
			if (allowNull ^ hasNullFormatter) {
				ff.setNullFormatter(allowNull ? new SSNullFormatter() : null);
			}
		}
	}

	// TODO: OPTIM: provide a version that iterates through the characters
	//				and returns true on first user character encountered.
	// TODO: Implement optimized containsUserText(String text, String mask,
	//						String formattingChars, Character placeholderChar)
	//		 and use it here.
	/**
	 * Similar to {@linkplain #userText(java.lang.String, javax.swing.text.MaskFormatter) }
	 * but only needs to check for any use input.
	 * @param text text
	 * @param mf MaskFormatter
	 * @return 
	 */
	static boolean containsUserText (String text, MaskFormatter mf) {
		return !userText(text, mf).isEmpty();
	}

	/**
	 * The literal characters and their index in formatted text; commonly
	 * derived from a {@linkplain MaskFormatter}'s mask.
	 * There is a 1-1 corespondance between the literals and the positions
	 * of where they show up in a value formatted by the mask.
	 * Except when one of them is null.
	 * So {@linkplain literals} may have duplicates and {@linkplain positions}
	 * will not match the mask when there are escaped formatting characters.
	 * @param literals
	 * @param positions
	 */
	record LiteralsAndPositions(List<String> literals, List<Integer> positions) {
		@SuppressWarnings("AssignmentToMethodParameter")
		public LiteralsAndPositions {
			if (!(positions == null || literals == null
					|| literals.size() == positions.size()))
				throw new IllegalArgumentException("size mismatch");
			literals = Collections.unmodifiableList(literals);
			positions = Collections.unmodifiableList(positions);
		}
	}

	// TODO: OPTIM: could have a private version of following that only creates
	//		 sb/l if needed. Use null and adjust record valid check.
	/** 
	 * Find the characters in mask that are literals. The literals are not
	 * the formatting characters. Chars beyond utf-16, "supplementary characters",
	 * in the mask cause a runtime exception. In the mask, a "{@literal '}"
	 * is used to escape a formattingChar.
	 * @param mask a formatting mask
	 * @param formattingChars non literal characters
	 * @return the literals and their positions in the mask
	 */
	public static LiteralsAndPositions getLiteralsAndPositions(
			String mask, String formattingChars)
	{
		List<String> literalList = new ArrayList<>();
		List<Integer> posList = new ArrayList<>();	// positions of literals in mask
		int position = 0;
		for (int i = 0; i < mask.length(); i++, position++) {
			char c = mask.charAt(i);
			if (Character.isHighSurrogate(c))
				throw new IllegalArgumentException(sf("mask '%s' contains Unicode supplementary characters", mask));
			if (formattingChars.indexOf(c) >= 0)
				continue;
			if (c == '\'') {
				// The following character is a literal
				i++;
				c = mask.charAt(i);
			}
			literalList.add(String.valueOf(c));
			posList.add(position);
		}
		return new LiteralsAndPositions(literalList, posList);
	}

	/**
	 * Convenience method for invoking {@linkplain #userText(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.Character) }.
	 * @param text text
	 * @param mf MaskFormatter
	 * @return characters that are input
	 */
	public static String userText(String text, MaskFormatter mf) {
		return userText( text, mf.getMask(),
				FORMATTING_CHARS, mf.getPlaceholderCharacter());
	}
	
	/**
	 * This strips formatting literals from text so that only user input
	 * remains. Example "userText("1_/3_", "##/##", "@#", '_')" returns
	 * "13". In a {@linkplain MaskFormatter}, if the placeholder character is
	 * a legitimate data character then it should not be specified in here.
	 * @param text text field contents that may contain literals
	 * @param mask used to determine positions of literals
	 * @param formattingChars the literal characters
	 * @param placeholderChar if not null, stip all occurances of this character
	 * @return characters that are input
	 */
	public static String userText(String text, String mask,
			String formattingChars, Character placeholderChar) {
		List<Integer> posLiterals = getLiteralsAndPositions(mask, formattingChars).positions();
		// TODO: use reversed when full jdk-21
		//if (!posLiterals.isEmpty() || mf.getPlaceholder() != null)
		if (!posLiterals.isEmpty() || placeholderChar != null) {
			StringBuilder sb = new StringBuilder(text);
			// start at the end so the indexes don't change
			for (int i = posLiterals.size() - 1; i >= 0; i--) {
				int pos = posLiterals.get(i);
				if (pos < sb.length())
					sb.deleteCharAt(pos);
			}
			if (placeholderChar != null) {
				// TODO: needs work
				// Following gets weird if the place holder char is legit.
				// Is it weird if initial text is shorter that mask.
				//char c = mf.getPlaceholderCharacter();
				for (int pos = sb.length() - 1; pos >= 0; pos--) {
					if (sb.charAt(pos) == placeholderChar)
						sb.deleteCharAt(pos);
				}
			}
			return sb.toString();
		}
		return "";
	}
}
