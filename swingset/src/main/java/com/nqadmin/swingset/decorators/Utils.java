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

import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import com.nqadmin.swingset.utils.SSComponent;

/**
 * Assists for working with decorators.
 */
public class Utils
{
	private Utils() {}

	/**
	 * For cases where the JComponent doesn't have a usable border.
	 * @param comp
	 * @return border to use with the SSComponent
	 */
	public static Border createEmptyBorder(SSComponent comp)
	{
		JComponent jc = (JComponent) comp;
		Border b = jc.getBorder();
		if (b instanceof CompoundBorder cb) {
			Insets oInsets = toInsets(cb.getOutsideBorder(), jc);
			Insets iInsets = toInsets(cb.getInsideBorder(), jc);
			b = BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(
							oInsets.top, oInsets.left, oInsets.bottom, oInsets.right),
					BorderFactory.createEmptyBorder(
							iInsets.top, iInsets.left, iInsets.bottom, iInsets.right));
		} else {
			Insets i = jc.getInsets();
			b = BorderFactory.createEmptyBorder(i.top, i.left, i.bottom, i.right);
		}
		return b;
	}
	private static Insets toInsets(Border b, JComponent jc)
	{
		return b.getBorderInsets(jc);
	}
	
}
