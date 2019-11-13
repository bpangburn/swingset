/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 ******************************************************************************/

package com.nqadmin.swingset.utils;

/**
 * SSEnums.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Various Enumerations for SwingSet
 */
public class SSEnums {

	/*
	 * Enumeration for SwingSet component types.
	 */
	public enum SSComponent {

		JPanel, JScrollPane, JTabbedPane,

		SSCheckBox, SSComboBox, SSFormattedTextField, SSImage, SSLabel, SSSlider, SSTextArea, SSTextField

	}

	/**
	 * Enumeration for navigation buttons.
	 */
	public enum Navigation {
		Next(1), Previous(1), First(2), Last(3);

		private final int value;

		Navigation(final int newValue) {
			this.value = newValue;
		}

		/**
		 * @return integer corresponding to enumerated value
		 */
		public int getValue() {
			return this.value;
		}
	}

	/**
	 * Enumeration for navigation buttons.
	 */
	public enum CheckBoxValues {

		// protected static String BOOLEAN_CHECKED = "true";

		// protected static String BOOLEAN_UNCHECKED = "false";
		Checked(1, "true"), Unchecked(0, "false");

		private final int intValue;
		private final String stringValue;

		CheckBoxValues(final int newIntValue, final String newStringValue) {
			this.intValue = newIntValue;
			this.stringValue = newStringValue;
		}

		/**
		 * @return integer corresponding to enumerated value
		 */
		public int getIntValue() {
			return this.intValue;
		}

		/**
		 * @return String corresponding to enumerated value
		 */
		public String getStringValue() {
			return this.stringValue;
		}
	}

}