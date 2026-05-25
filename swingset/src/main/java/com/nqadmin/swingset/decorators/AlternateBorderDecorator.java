/* *****************************************************************************
 * Copyright (C) 2026, Ernie R Rael. All rights reserved.
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
 * ****************************************************************************/
package com.nqadmin.swingset.decorators;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

/**
 * Useful when a component associated with an SSComponent is decorated
 * and/or an associated component is used to catch focus. The decorated
 * and focused components may or may not be the same.
 *
 * Notice that lineEmpty_empty give maximal room for the component.
 * See {@link BorderDecorator}
 */
public class AlternateBorderDecorator extends BorderDecorator
{
	private final JComponent decoratedComponent;
	private final Component focusedComponent;

	/**
	 * Convenience for when both decorated and focused component are the same.
	 * 
	 * @param component
	 */
	public AlternateBorderDecorator(JComponent component)
	{
		this(component, component);
	}

	/**
	 * Use different decorated and focused components.
	 * 
	 * @param decoratedComponent
	 * @param focusedComponent
	 */
	public AlternateBorderDecorator(JComponent decoratedComponent, Component focusedComponent)
	{
		this.decoratedComponent = decoratedComponent;
		this.focusedComponent = focusedComponent;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	protected Border getBorder(ComponentState state)
	{
		// The default border when just running the demo is
		// the CompoundBorder: [[3,3,3,3],[2,14,2,14]].
		if (state == ComponentState.CLEAN)
			return defaultBorder;
		
		if (jc().getBorder() instanceof CompoundBorder cb) {
			return BorderDecorator.lineEmpty_empty(
					cb.getOutsideBorder().getBorderInsets(jc()),
					cb.getInsideBorder().getBorderInsets(jc()),
					state);
		}
		return empty_line(jc().getInsets(), state);
	}
	
	/**
	 * {@inheritDoc }
	 */
	@Override
	protected JComponent jc()
	{
		return decoratedComponent;
	}
	
	/**
	 * {@inheritDoc }
	 */
	@Override
	protected Component fcomp()
	{
		return focusedComponent;
	}
	
}
