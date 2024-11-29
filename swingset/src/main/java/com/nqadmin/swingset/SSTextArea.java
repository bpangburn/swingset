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
package com.nqadmin.swingset;

import static com.nqadmin.swingset.utils.SSUtils.sf;

import javax.sql.RowSet;
import javax.swing.JTextArea;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

// SSTextArea.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * SSTextArea extends the JTextArea to add RowSet binding.
 */
public class SSTextArea extends JTextArea implements SSComponentInterface {

	// TODO Consider adding an InputVerifier to prevent component from losing focus.
	// See SSFormattedTextField. May be able to add to SSDocumentListener in
	// SSCommon.

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = SSUtils.getLogger();

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -1256528482424744463L;

	/**
	 * Common fields shared across SwingSet components
	 */
	private final SSCommon ssCommon;

	/**
	 * Empty constructor needed for deserialization.
	 */
	public SSTextArea() {
		ssCommon = finishSSCommon();
	}

	/**
	 * Constructs a new empty SSTextArea with the specified number of rows and
	 * columns.
	 *
	 * @param _rows    {@literal the number of rows >= 0}
	 * @param _columns {@literal the number of columns >= 0}
	 */
	public SSTextArea(final int _rows, final int _columns) {
		super(_rows, _columns);
		ssCommon = finishSSCommon();
	}

	/**
	 * Creates a multi-line text box and binds it to the specified RowSet column.
	 *
	 * @param _rowSet          datasource to be used.
	 * @param _boundColumnName name of the column to which this text area should be
	 *                         bound
	 */
	public SSTextArea(final RowSet _rowSet, final String _boundColumnName) {
		this();
		bind(_rowSet, _boundColumnName);
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
		logger.debug("{}: Setting text area to " + text + ".", () -> getColumnForLog());
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

} // end public class SSTextArea extends JTextArea {
