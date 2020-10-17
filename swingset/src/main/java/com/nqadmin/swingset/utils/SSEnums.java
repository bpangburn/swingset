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
package com.nqadmin.swingset.utils;

// SSEnums.java

import com.nqadmin.swingset.SSDBNav;

//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Various Enumerations for SwingSet
 */
public class SSEnums {

	/**
	 * Enumeration for navigation buttons.
	 */
	@SuppressWarnings("javadoc")
	public enum CheckBoxValues {

		// protected static String BOOLEAN_CHECKED = "true";

		// protected static String BOOLEAN_UNCHECKED = "false";
		Checked(1, "true"), Unchecked(0, "false");

		private final int intValue;
		private final String stringValue;

		CheckBoxValues(final int newIntValue, final String newStringValue) {
			intValue = newIntValue;
			stringValue = newStringValue;
		}

		/**
		 * @return integer corresponding to enumerated value
		 */
		public int getIntValue() {
			return intValue;
		}

		/**
		 * @return String corresponding to enumerated value
		 */
		public String getStringValue() {
			return stringValue;
		}
	}

	/**
	 * Enumeration for navigation buttons.
	 */
	@SuppressWarnings("javadoc")
	public enum Navigation {
		First(2, SSDBNav.NAVIGATION_FIRST),
		Last(3, SSDBNav.NAVIGATION_LAST),
		Next(1, SSDBNav.NAVIGATION_NEXT),
		Previous(1, SSDBNav.NAVIGATION_PREVIOUS);

		private final int value;
		private final int deprecatedConstant;

		Navigation(final int newValue, int deprecatedConstant) {
			value = newValue;
			this.deprecatedConstant = deprecatedConstant;
		}

		/**
		 * @return integer corresponding to enumerated value
		 */
		public int getValue() {
			return value;
		}

		/**
		 * @return integer for use with the deprecated method
		 */
		@Deprecated
		public int getDeprecatedConstant() {
			return deprecatedConstant;
		}
	}

	/**
	 * Enumeration for SwingSet component types.
	 */
	@SuppressWarnings("javadoc")
	public enum SSComponent {

		JPanel, JScrollPane, JTabbedPane,

		SSCheckBox, SSComboBox, SSFormattedTextField, SSImage, SSLabel, SSSlider, SSTextArea, SSTextField

	}

}
