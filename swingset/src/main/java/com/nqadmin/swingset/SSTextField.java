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
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.sql.RowSet;
import javax.swing.JTextField;
import javax.swing.text.Document;

import java.lang.System.Logger;

import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;

/**
 * SSTextField extends the JTextField.
 */
@SuppressWarnings("serial")
public class SSTextField extends JTextField implements SSComponentInterface {

	// TODO Consider adding an InputVerifier to prevent component from losing focus.
	// See SSFormattedTextField. May be able to add to SSDocumentListener in
	// SSCommon.
	// TODO Convert masks to SSFormattedTextFields
	// TODO Add YYYYMMDD mask.

	/** Logger for component */
	private static Logger logger = SSUtils.getLogger();

	/**
	 * Common fields shared across SwingSet components
	 */
	private final SSCommon ssCommon;

	/**
	 * Constructs a new, empty text field.
	 */
	public SSTextField() {
		this(null);
	}

	/**
	 * Constructs a new text field with the given text.
	 * @param _text initial text
	 */
	public SSTextField(String _text) {
		this(null, null, null);
	}

	/**
	 * Creates a SSTextField instance and binds it to the specified RowSet column.
	 *
	 * @param _rowSet          datasource to be used.
	 * @param _boundColumnName name of the column to which this label should be
	 *                         bound
	 */
	public SSTextField(final RowSet _rowSet, final String _boundColumnName) {
		this(null, _rowSet, _boundColumnName);
	}

	/** All the constructors feed through here */
	private SSTextField(String _text, final RowSet _rowSet, final String _boundColumnName) {
		super(_text);
		ssCommon = finishSSCommon();
		if (_rowSet != null) {
			bind(_rowSet, _boundColumnName);
		}
	}

	/**
	 * Part of the scheme to keep text field in sync with data base.
	 * See {@link SSCommon.SSPlainDocument}.
	 */
	@Override
	protected Document createDefaultModel() {
		return getSSCommon().new SSPlainDocument();
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
				SSTextField.this.selectAll();
			}
		});
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public SSCommon.SSDocumentListener getSSComponentListener() {
		return getSSCommon().getSSDocumentListener();
	}

	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText()
	 * <p>
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed
	 */
	@Override
	public void updateSSComponent() {

		final String text = getBoundColumnText();
		logger.log(DEBUG, ()->sf("%s: Setting text field to %s.", getColumnForLog(), text));
		setText(text);
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("%s{text=%s, %s}", getClass().getSimpleName(),
				getText(), SSUtils.ssComponentToString(this));
	}

	/**
	 * Returns ssCommon for the current Swingset component.
	 *
	 * @return common SwingSet component data and methods
	 */
    @Override
	public SSCommon getSSCommon() {
		if (ssCommon == null)
			return partialSSCommon = SSCommon.createStart(this, partialSSCommon);
		return ssCommon;
	}

	//
	// TODO: long term get rid of this half init stuff. Maybe a builder...
	// NOTE: this variable could be used in methods that require a fully
	//		 constructed SSCommon for error checking.
	//
	private SSCommon partialSSCommon;

	/**
	 * Either return a new create ssCommon or 
	 * Only call from constructor; "ssCommon = finishSSCommon()".
	 */
	private SSCommon finishSSCommon() {
		SSCommon rv = SSCommon.createFinish(this, partialSSCommon);
		partialSSCommon = null;
		return rv;
	}
} // end public class SSTextField extends JTextField {
