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
 ******************************************************************************/

package com.nqadmin.swingset.formatting;

import javax.swing.text.MaskFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// 2019-02-27-BP: this should be named SSSSSSNFormatterFactory for consistency.
// SSSSNFormatterFactory.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * SSSSNFormatterFactory extends DefaultFormatterFactory for US Social Security
 * Number fields.
 */
public class SSSSNFormatterFactory extends javax.swing.text.DefaultFormatterFactory {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 7141905652057051134L;
	private MaskFormatter defaultFormatter;
	private MaskFormatter displayFormatter;
	private MaskFormatter editFormatter;
	private MaskFormatter nullFormatter;
	
	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * Creates a default SSSSNFormatterFactory
	 */
	public SSSSNFormatterFactory() {

		try {
			defaultFormatter = new MaskFormatter("###-##-####");
			nullFormatter = null;
			editFormatter = new MaskFormatter("###-##-####");
			displayFormatter = new MaskFormatter("###-##-####");

			editFormatter.setPlaceholderCharacter('0');

			setDefaultFormatter(defaultFormatter);
			setNullFormatter(nullFormatter);
			setEditFormatter(editFormatter);
			setDisplayFormatter(displayFormatter);
		} catch (final java.text.ParseException pe) {
			logger.warn("Parse Exception.", pe);
			// do nothing
		}
	}
}

