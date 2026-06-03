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
 * copyright (C) 2024-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.core;


import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.lang.System.Logger;
import java.util.EventListener;

import javax.swing.JTextField;
import javax.swing.text.Document;

import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSTextSupport;
import com.nqadmin.swingset.utils.SSTextSupport.SSDocumentListener;
import com.nqadmin.swingset.utils.SSTextSupport.SSPlainDocument;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * TextField extends the JTextField.
 */
@SuppressWarnings("serial")
public class TextField extends JTextField implements SSComponent
{
	// TODO Consider adding an InputVerifier to prevent component from
	// losing focus; see FormattedTextField.

	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Constructs a new, empty text field.
	 */
	public TextField() {
		this(null);
	}

	/**
	 * Constructs a new text field with the given text.
	 * @param text initial text
	 */
	public TextField(String text) {
		this(text, null, null);
	}

	/**
	 * Creates a TextField instance and binds it to the specified RowSet column.
	 *
	 * @param rowsModel        model for a RowSet
	 * @param columnName name of the column to which this label should be bound
	 */
	public TextField(RowsModel rowsModel, String columnName) {
		this(null, rowsModel, columnName);
	}

	/** All the constructors feed through here */
	private TextField(String text, RowsModel rowsModel, String columnName) {
		super(text);
		finishSSCommon();
		if (rowsModel != null)
			rowsModel.bind(this, columnName);
	}

	/**
	 * Part of the scheme to keep text field in sync with data base.
	 * See {@link SSTextSupport.SSPlainDocument}.
	 */
	@Override
	protected Document createDefaultModel() {
		return new SSPlainDocument(this);
	}

	/**
	 * Add focus listener that selects all text.
	 * Add key listener for when this is used with mask. Use Mask Formatters.
	 */
	@Override
	public void customInit()
	{
		// ADD FOCUS LISTENER TO THE TEXT FIELD SO THAT WHEN THE FOCUS IS GAINED
		// COMPLETE TEXT SHOULD BE SELECTED
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent fe) {
				// TODO: Turn off any TextDecorator while focused
				TextField.this.selectAll();
			}
		});
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
					logger.log(DEBUG, ()->sf("%s: Setting text field to %s.", getColumnForLog(), text));
					setText(text);
				}
				
				/** {@inheritDoc } */
				@Override
				protected SSDocumentListener getSSComponentListener() {
					return SSTextSupport.getSSDocumentListener(TextField.this);
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

} // end public class TextField extends JTextField {
