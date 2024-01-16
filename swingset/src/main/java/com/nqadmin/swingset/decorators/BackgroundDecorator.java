/* *****************************************************************************
 * Copyright (C) 2024, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 * ****************************************************************************/

package com.nqadmin.swingset.decorators;

import java.awt.Color;

import javax.swing.UIManager;


/**
 * Decorate the background when SSComponent has focus, chose color dependent
 * on having valid data.
 */
public class BackgroundDecorator extends FocusDecorator
{

	private final Color standardBackgroundColor = getDefaultBackgroundColor();
	private Color focusBackgroundColor = new Color(204, 255, 255); // Tealish
	private final Color errorBackgroundColor = Color.PINK;

	/** Decorate the component using current state. */
	@Override
	public void decorate() {
		if (component.isDataValid()) {
			jc().setBackground(jc().isFocusOwner()
					? focusBackgroundColor : standardBackgroundColor);
		} else {
			jc().setBackground(errorBackgroundColor);
		}
	}

	private static Color defaultBackgroundColor;

	private static Color getDefaultBackgroundColor() {
		if (defaultBackgroundColor == null) {
			defaultBackgroundColor = UIManager.getColor("FormattedTextField.background");
			if (defaultBackgroundColor == null) {
				defaultBackgroundColor = Color.WHITE;
			}
		}
		return defaultBackgroundColor;
	}

	// TODO: these [sg]etters probably better as named properties?

	// Implement the get/setFocusBackgroundColor since SSFormattedTextField...
	/** @return the background color */
	public Color getFocusBackgroundColor() {
		return focusBackgroundColor;
	}

	/**
	 * @param _focusBackgroundColor the new background color to use
	 */
	public void setFocusBackgroundColor(final Color _focusBackgroundColor) {
		focusBackgroundColor = _focusBackgroundColor;
	}
    
}
