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
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2025-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.core;

import java.lang.System.Logger;
import java.util.EventListener;

import javax.swing.JTextArea;

import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSTextSupport;
import com.nqadmin.swingset.utils.SSTextSupport.SSDocumentListener;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * TextArea extends the JTextArea to add RowSet binding.
 */
@SuppressWarnings("serial")
public class TextArea extends JTextArea implements SSComponent {

	// TODO Consider adding an InputVerifier to prevent component from losing focus.
	//      Probably want component/system-wide option.
	// See SSFormattedTextField. May be able to add to SSDocumentListener in
	// SSCommon.

	/**
	 * Logger for component
	 */
	private static Logger logger = SSUtils.getLogger();

	/**
	 * Empty constructor needed for deserialization.
	 */
	public TextArea() {
		finishSSCommon();
	}

	/**
	 * Constructs a new empty TextArea with the specified number of rows and
	 * columns.
	 *
	 * @param _rows    {@literal the number of rows >= 0}
	 * @param _columns {@literal the number of columns >= 0}
	 */
	public TextArea(int _rows, int _columns) {
		super(_rows, _columns);
		finishSSCommon();
	}

	/**
	 * Creates a multi-line text box and binds it to the specified RowSet column.
	 *
	 * @param rowsModel          datasource to be used.
	 * @param columnName name of the column to which this text area should be bound
	 */
	public TextArea(RowsModel rowsModel, String columnName) {
		this();
		rowsModel.bind(this, columnName);
	}

	/**
	 * Set word linewrap.
	 */
	@Override
	public void customInit()
	{
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	/** {@inheritDoc } */
	@Override
	public void cleanField()
	{
		setText("");
	}

	private Hook hook;

	/** {@inheritDoc } */
	@Override
	public final Hook getSSComponentHook()
	{
		if (hook == null)
			hook = new Hook(this) {
				/**
				 * Updates the value stored and displayed in the SwingSet
				 * component based on getColumnText()
				 */
				@Override
				protected void updateSSComponent() {
					
					final String text = getColumnText();
					logger.log(DEBUG, ()->sf("%s: Setting text area to %s.", getColumnForLog(), text));
					setText(text);
				}
				
				/** {@inheritDoc } */
				@Override
				protected SSDocumentListener getSSComponentListener() {
					return SSTextSupport.getSSDocumentListener(TextArea.this);
				}
				
				/** {@inheritDoc } */
				@Override
				protected void addSSComponentListener(EventListener eventListener)
				{
					getDocument().addDocumentListener((SSDocumentListener)eventListener);
				}
				
				/** {@inheritDoc } */
				@Override
				protected void removeSSComponentListener(EventListener eventListener)
				{
					getDocument().removeDocumentListener((SSDocumentListener)eventListener);
				}
				
			};
		return hook;
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("%s{text=%s, %s}", getClass().getSimpleName(),
				getText(), SSUtils.ssComponentToString(this));
	}
} // end public class TextArea extends JTextArea {
