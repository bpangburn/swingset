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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import com.nqadmin.swingset.utils.SSComponentInterface;

/**
 * Base class for decorators that use Focus.
 */
public abstract class FocusDecorator
		implements Decorator, FocusListener, TextDecorator
{
	/** this component */
	private SSComponentInterface component;
	/** current color set by decorateText() */
	protected Color textColor;

	/** Apply decoration */
	@Override
	public void focusGained(FocusEvent e) {
		decorate();
	}

	/** Remove decoration */
	@Override
	public void focusLost(FocusEvent e) {
		decorate();
	}

	/** {@inheritDoc} */
	@Override
	public void install(SSComponentInterface component) {
		this.component = component;
		fcomp().addFocusListener(this);
	}

	/** {@inheritDoc} */
	@Override
	public void uninstall() {
		fcomp().removeFocusListener(this);
	}

	/**
	 * Based on JComponent type, determine Component that gets focus.
	 * @return focus target
	 */
	protected Component fcomp() {
		return !(jc() instanceof JComboBox) ? jc()
				: ((JComboBox)jc()).getEditor().getEditorComponent();
	}

	/** {@inheritDoc}
	 * Put here for convenience; Signature of "enum<?>" is so plugin authors
	 * can define their own styles. Change the color of the text according
	 * to _style.
	 */
	@Override
	public <E extends Enum<E>> void decorateText(E _style) {
		Color color;
		if (_style instanceof TextDecorationStyle) {
			TextDecorationStyle style = (TextDecorationStyle) _style;
			switch (style) {
			case NEGATIVE_NUMBER:
				color = Color.RED;
				break;
			case RESET:
				color = Color.BLACK;
				break;
			case NO_CHANGE: // fallthrough
			default:
				color = null;
				break;
			}
			textColor = color;
			if (color != null) {
				jc().setForeground(color);
			}
		}
	}

	/**
	 * Return the component associated with this validator
	 * @return the component
	 */
	public SSComponentInterface getComponent() {
		return component;
	}

	/**
	 * Return the SSComponent as a JComponent.
	 * @return the SSComponent
	 */
	protected final JComponent jc() {
		return (JComponent) component;
	}
    
}
