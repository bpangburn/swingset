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

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Decorate the border when SSComponent has focus, chose color dependent
 * on having valid data.
 */
public class BorderDecorator extends FocusDecorator
{

	private final Border focusBorder = BorderFactory.createLineBorder(Color.GREEN);
	private final Border standardBorder = getDefaultBorder();

	/** Decorate the component using current state. */
	@Override
	public void decorate() {
		if (component.isDataValid()) {
			jc().setBorder(jc().isFocusOwner() ? focusBorder : standardBorder);
			// WHY IS THE FOLLOWING HERE? IT WAS IN SS_FORMATTED_TEXT_FIELD
			jc().setForeground(textColor != null ? textColor : Color.BLACK);
		} else {
			jc().setBorder(BorderFactory.createLineBorder(Color.RED));
			// WHY IS THE FOLLOWING HERE? IT WAS IN SS_FORMATTED_TEXT_FIELD
			jc().setForeground(textColor != null ? textColor : Color.BLACK);
		}
	}

	private static Border defaultBorder;

	private static Border getDefaultBorder() {
		if (defaultBorder == null) {
			// TODO: get initial border and use that?
			defaultBorder = UIManager.getBorder("FormattedTextField.border");
			if (defaultBorder == null) {
				defaultBorder = BorderFactory.createLineBorder(Color.BLACK);
			}
		}
		return defaultBorder;
	}
    
}
