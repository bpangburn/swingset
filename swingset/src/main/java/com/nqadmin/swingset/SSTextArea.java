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
package com.nqadmin.swingset;

import javax.swing.JTextArea;

import com.nqadmin.swingset.datasources.SSRowSet;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

// SSTextArea.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * SSTextArea extends the JTextArea to add SSRowSet binding.
 */
public class SSTextArea extends JTextArea implements SSComponentInterface {

	// TODO Consider adding an InputVerifier to prevent component from losing focus. See SSFormattedTextField. May be able to add to SSDocumentListener in SSCommon.

    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = -1256528482424744463L;

    /**
     * Common fields shared across SwingSet components
     */
    protected SSCommon ssCommon = new SSCommon(this);

    /**
     * Empty constructor needed for deserialization.
     */
    public SSTextArea() {
		// Note that call to parent default constructor is implicit.
		//super();
    }

    /**
     * Constructs a new empty SSTextArea with the specified number of rows and columns.
     *
     * @param _rows     {@literal the number of rows >= 0}
     * @param _columns     {@literal the number of columns >= 0}
     */
    public SSTextArea(final int _rows, final int _columns) {
        super(_rows, _columns);
    }

    /**
     * Creates a multi-line text box and binds it to the specified SSRowSet column.
     *
     * @param _ssRowSet    datasource to be used.
     * @param _boundColumnName    name of the column to which this text area should be bound
     */
    public SSTextArea(final SSRowSet _ssRowSet, final String _boundColumnName) {
    	this();
        bind(_ssRowSet, _boundColumnName);
    }

    /**
	 * Adds any necessary listeners for the current SwingSet component. These will trigger changes
	 * in the underlying RowSet column.
	 */
	@Override
	public void addSSComponentListener() {
		addSSDocumentListener();
	}

    @Override
	public void customInit() {
    	// Adding some logic from SSMemoField (deprecated), which seems generally helpful.
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	/**
	 * Returns the ssCommon data member for the current Swingset component.
	 *
	 * @return shared/common SwingSet component data and methods
	 */
    @Override
	public SSCommon getSSCommon() {
		return ssCommon;
	}

	/**
	 * Removes any necessary listeners for the current SwingSet component. These will trigger changes
	 * in the underlying RowSet column.
	 */
	@Override
	public void removeSSComponentListener() {
		removeSSDocumentListener();
	}

	/**
	 * Sets the SSCommon data member for the current Swingset Component.
	 *
	 * @param _ssCommon shared/common SwingSet component data and methods
	 */
	@Override
	public void setSSCommon(final SSCommon _ssCommon) {
		ssCommon = _ssCommon;
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
		setText(getBoundColumnText());
	}

} // end public class SSTextArea extends JTextArea {
