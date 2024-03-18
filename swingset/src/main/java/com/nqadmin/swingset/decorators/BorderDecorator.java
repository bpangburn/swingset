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
import java.awt.Component;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

/**
 * Decorate the border when SSComponent has focus, chose color dependent
 * on having valid data. A state dependent one line border is drawn.
 * The size of the original border is preserved.
 * If the border is null, then set a border of width 1.
 */
// NOTE: TODO: if there is a border and an element is 0, then may not quite right.
// TODO: could listen to components border property and adjust accordingly.
public class BorderDecorator extends FocusDecorator
{
	private static Logger logger = SSUtils.getLogger();

	/** The type of border to use */
	// TODO: may want to treat as bit field for: error/focus/warning/dirty
	protected enum BorderState {
		/** not focused no error */
		DEFAULT,
		/** focus gained, no error */
		OK,
		/** error with/without focus */
		ERROR
	}

	/** Typically the border that the component started with;
	 * not foucus, no error. */
	protected Border defaultBorder;

	/** Decorate the component using current state. */
	@Override
	public boolean decorate() {
		boolean dataValid = getComponent().getSSCommon().validate() && getComponent().isDataValid();
		logger.trace(() -> String.format("%s focus: %s, dataValid: %s",
				jc().getClass().getSimpleName(), fcomp().isFocusOwner(), dataValid));
		if (dataValid) {
			jc().setBorder(getBorder(fcomp().isFocusOwner() ? BorderState.OK : BorderState.DEFAULT));
			// Why is the following here? it was in ss_formatted_text_field.
			jc().setForeground(textColor != null ? textColor : Color.BLACK);
		} else {
			jc().setBorder(getBorder(BorderState.ERROR));
			// Why is the following here? it was in ss_formatted_text_field.
			jc().setForeground(textColor != null ? textColor : Color.BLACK);
		}
		return dataValid;
	}

	/** {@inheritDoc } */
	@Override
	public void install(SSComponentInterface component) {
		super.install(component);
		setupDefaultBorder();
	}

	/** {@inheritDoc } */
	@Override
	public void uninstall() {
		super.uninstall();
		defaultBorder = null;
	}

	/** Create a compound border the size of defaultBorder.
	 * Outside is empty, inside is 1 line.
	 * @param state
	 * @return 
	 */
	protected Border getBorder(BorderState state) {
		if (state == BorderState.DEFAULT)
			return defaultBorder;

		Color color = null;
		switch(state) {
		case OK: color = Color.GREEN; break;
		case ERROR: color = Color.RED; break;
		}
		Insets i = jc().getInsets();
		Color fColor = color;
		logger.trace(() -> String.format("%s %s", fColor, asString(i)));
		return BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(Math.max(0, i.top - 1),
											    Math.max(0, i.left - 1),
											    Math.max(0, i.bottom - 1),
											    Math.max(0, i.right - 1)),
				BorderFactory.createLineBorder(color));
	}

	/**
	 * Examine the component's bo
	 */
	protected void setupDefaultBorder() {
		if (defaultBorder == null) {
			logger.debug(() -> {
				Border b = jc().getBorder();
				String bi = asString(jc().getInsets());
				String bc = b != null ? b.getClass().getSimpleName() : null;
				String bs = asString(b);
				return String.format("%s-%s %s %s",
					jc().getClass().getSimpleName(), bc, bi, bs);
			});
			Border b = jc().getBorder();
			if (b == null) {
				b = createDefaultBorder();
				jc().setBorder(b);
			}
			defaultBorder = b;
		}
	}

	/**
	 * This is used to create a border in situations where the component
	 * does not have a border; typically an empty border.
	 * @return border
	 */
	protected Border createDefaultBorder() {
		//return UIManager.getBorder("TextField.border");
		Insets i = jc().getInsets();
		return BorderFactory.createEmptyBorder(Math.max(1, i.top),
											   Math.max(1, i.left),
											   Math.max(1, i.bottom),
											   Math.max(1, i.right));
	}

	/**
	 * Convert a border to a display string which shows compound border nesting
	 * and terminal border insets.
	 * @param b a border
	 * @return String of border for output
	 */
	protected String asString(Border b) {
		if(b == null)
			return null;
		if (b instanceof CompoundBorder) {
			CompoundBorder cb = (CompoundBorder)b;
			return String.format("[%s,%s]",
					asString(cb.getOutsideBorder()),
					asString(cb.getInsideBorder()));
		}
		return asString(b.getBorderInsets((Component) getComponent()));
	}

	/**
	 * Convert inset to a display String; like "[2,2,2,2]".
	 * @param i inset
	 * @return String for output
	 */
	protected String asString(Insets i) {
		return String.format("[%d,%d,%d,%d]", i.top, i.left, i.bottom, i.right);
	}
    
}
