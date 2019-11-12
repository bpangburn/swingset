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

package com.nqadmin.swingset.formatting;

import javax.swing.text.MaskFormatter;

// 2019-02-27-BP: this should be named SSSSSSNFormatterFactory for consistency.
/**
 * SSSSNFormatterFactory.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
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
	 * Creates a default SSSSNFormatterFactory
	 */
	public SSSSNFormatterFactory() {

		try {
			this.defaultFormatter = new MaskFormatter("###-##-####");
			this.nullFormatter = null;
			this.editFormatter = new MaskFormatter("###-##-####");
			this.displayFormatter = new MaskFormatter("###-##-####");

			this.editFormatter.setPlaceholderCharacter('0');

			this.setDefaultFormatter(this.defaultFormatter);
			this.setNullFormatter(this.nullFormatter);
			this.setEditFormatter(this.editFormatter);
			this.setDisplayFormatter(this.displayFormatter);
		} catch (java.text.ParseException pe) {
			// do nothing
		}
	}
}

/*
 * $Log$ Revision 1.5 2005/02/04 22:42:06 yoda2 Updated Copyright info.
 *
 * Revision 1.4 2005/01/18 23:38:01 dags Diego's name fix
 *
 * Revision 1.3 2004/12/13 20:50:16 dags Fix package name
 *
 * Revision 1.2 2004/12/13 18:46:13 prasanth Added License.
 *
 */