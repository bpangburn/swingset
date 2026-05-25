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
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/

package com.nqadmin.swingset.decorators;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSComponent.ValidationResult;

/**
 * Base class for decorators that use Focus.
 */
public abstract class FocusDecorator
		implements Decorator, FocusListener, TextDecorator
{
	/** The state of a component. Used to create the proper decoration */
	// TODO: may want to treat as bit field for: error/focus/warning/dirty
	public enum ComponentState {
		/** not focused, no error, not modified */
		OK, // TODO: This is actually OK
		/** focus gained, no error, not modified */
		FOCUSED_OK,
		/** modified without focus */
		MODIFIED,
		/** modified with focus */
		FOCUSED_MODIFIED,
		/** error with/without focus */
		ERROR,
		/** error with/without focus */
		FOCUSED_ERROR;

		boolean isFocused() {
			return this == FOCUSED_OK || this == FOCUSED_MODIFIED
					|| this == FOCUSED_ERROR;
		}

		boolean isModified() {
			return this == MODIFIED || this == FOCUSED_MODIFIED;
		}

		boolean isError() {
			return this == ERROR || this == FOCUSED_ERROR;
		}

		// TODO: isModified, isError
	}

	/**
	 * Determine the state of the component.
	 * @param valid
	 * @return the component state
	 */
	public ComponentState getComponentState(ValidationResult valid) {
		ComponentState borderState;
		if (valid.all()) {
			borderState = getComponent().isDirty()
					? ComponentState.MODIFIED
					: ComponentState.OK;
		} else
			borderState = ComponentState.ERROR;
		if (fcomp().isFocusOwner()) {
			borderState = switch(borderState) {
			case OK -> ComponentState.FOCUSED_OK;
			case MODIFIED -> ComponentState.FOCUSED_MODIFIED;
			case ERROR -> ComponentState.FOCUSED_ERROR;
			default -> throw new IllegalStateException("Unexpected value: " + (borderState));
			};
		}

		return borderState;
	}

	/** this component */
	private SSComponent component;
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
	public void install(SSComponent component) {
		this.component = component;
		fcomp().addFocusListener(this);
	}

	/** {@inheritDoc} */
	@Override
	public void uninstall() {
		fcomp().removeFocusListener(this);
	}

	/**
	 * Return the Component that gets focus when the associated SSComponent
	 * is focused.
	 * 
	 * @return focus target
	 */
	// TODO: fcomp() - maybe just return jc() and dynamically override
	//		 in combobox components, but would want to wrap default
	//		 so the default decorator can be change but fcomp() is overridden.
	protected Component fcomp() {
		return !(jc() instanceof JComboBox) ? jc()
				: ((JComboBox)jc()).getEditor().getEditorComponent();
	}

	/**
	 * Set the color of the text/foreground according to _style.
	 */
	@Override
	public <E extends Enum<E>> void decorateText(E _style) {
		if (_style instanceof TextDecorationStyle style) {
			textColor = switch (style) {
			case NEGATIVE_NUMBER  -> Color.RED;
			case RESET            -> Color.BLACK;
			case NO_CHANGE        -> null;
			};
			if (textColor != null) {
				jc().setForeground(textColor);
			}
		}
	}

	/**
	 * Return the SSComponent associated with this decorator.
	 * 
	 * @return the component
	 */
	public SSComponent getComponent() {
		return component;
	}

	/**
	 * Return the JComponent that gets decorated.
	 * It may not be the same as whats returned by {@link #getComponent() }.
	 * 
	 * @return the JComponent
	 */
	protected JComponent jc() {
		return (JComponent) component;
	}
    
}
