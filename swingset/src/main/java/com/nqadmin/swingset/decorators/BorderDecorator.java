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
import java.awt.Insets;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
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
	 * not focus, no error. */
	protected Border defaultBorder;

	/** Decorate the component using current state. */
	@Override
	public boolean decorate() {
		boolean dataValid = getComponent().getSSCommon().validate() && getComponent().isDataValid();
		logger.trace(() -> String.format("%s focus: %s, dataValid: %s",
				jc().getClass().getSimpleName(), fcomp().isFocusOwner(), dataValid));
		Border b;
		if (dataValid) {
			b = getBorder(fcomp().isFocusOwner() ? BorderState.OK : BorderState.DEFAULT);
		} else {
			b = getBorder(BorderState.ERROR);
		}

		jc().setBorder(b);
		// Why is the following here? It was in ss_formatted_text_field.
		jc().setForeground(textColor != null ? textColor : Color.BLACK);
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
		Color color = getBorderColor(state);
		if (color == null)
			return defaultBorder;
		logger.trace(() -> String.format("%s %s", color, asString(jc().getInsets())));
		Border b;
		if (jc().getBorder() instanceof CompoundBorder) {
			CompoundBorder cb = (CompoundBorder) jc().getBorder();
			b = emptyLine_empty(cb.getOutsideBorder().getBorderInsets(jc()),
					cb.getInsideBorder().getBorderInsets(jc()), color);
		} else {
			b = empty_line(jc().getInsets(), color);
		}
		return b;
	}

	/**
	 * If a component has a border, then the defaultBorder is the
	 * components original border.
	 * If the component has no border, give it a default border
	 * the size of it's insets, but with at least thickness 1.
	 */
	protected void setupDefaultBorder() {
		if (defaultBorder == null) {
			logger.debug(() -> {
				Border b = jc().getBorder();
				String bi = asString(jc().getInsets());
				String bc = b != null ? b.getClass().getSimpleName() : null;
				String bs = asString(b, jc());
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
	 * does not have a border. It returns an empty border
	 * the size of it's insets, but with at least thickness 1.
	 * @return an empty border
	 */
	protected Border createDefaultBorder() {
		Insets i = jc().getInsets();
		return BorderFactory.createEmptyBorder(Math.max(1, i.top),
											   Math.max(1, i.left),
											   Math.max(1, i.bottom),
											   Math.max(1, i.right));
	}

	/**
	 * Determine color for specified BorderState; null return means
	 * use the defaultBorder.
	 * @param state
	 * @return
	 */
	protected Color getBorderColor(BorderState state)
	{
		Color c;
		switch(state) {
		case DEFAULT: c = null; break;
		case OK: c = Color.GREEN; break;
		case ERROR: c = Color.RED; break;
		default: c = null;
		}
		return c;
	}

	//
	// Below are a few methods that create a CompoundBorder
	// from either insets or a compound border.
	// Each direction, top,left,bottom,right is computed separately.
	// 
	// The description of the output for each direction uses
	// "_" for empty or space, and "|" for a line
	// and the returned border is delineated with brackets
	// "[outside][inside]". Note the brackets take no space
	// The line is usually at, or near, the edge of either
	// the outside or indide.
	//
	// For example
	//         outside   inside 
	//        [_______][_______]
	//

	/**
	 * Create a simple compound border with size specified by param i,
	 * and a line on the inside of the param color.
	 * <p>
	 * [_______][|]
	 * <p>
	 * @param i insets that specify size of output border
	 * @param color color of line
	 * @return border
	 */
	public static Border empty_line(Insets i, Color color)
	{
		Border b = BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(Math.max(0, i.top - 1),
						Math.max(0, i.left - 1),
						Math.max(0, i.bottom - 1),
						Math.max(0, i.right - 1)),
				BorderFactory.createLineBorder(color));
		return b;
	}

	/**
	 * Create a simple compound border with size specified by param i,
	 * and a line on the inside of the param color.
	 * <p>
	 * [|][_______]
	 * <p>
	 * @param i insets that specify size of output border
	 * @param color color of line
	 * @return border
	 */
	public static Border line_empty(Insets i, Color color)
	{
		Border b = BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(color),
				BorderFactory.createEmptyBorder(
						Math.max(0, i.top - 1),
						Math.max(0, i.left - 1),
						Math.max(0, i.bottom - 1),
						Math.max(0, i.right - 1)));
		return b;
	}

	/**
	 * Create a Compound border the same size as the specified Insets where the
	 * inside is a compound border of a line and a space, and the outside
	 * border is the remaining space.
	 * <p>
	 * [_______][|_]
	 * <p>
	 * @param i insets for size of border
	 * @param color line color
	 * @return border, null if problem
	 */
	public static Border empty_lineSpace(Insets i, Color color)
	{
		Border b = BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(
						Math.max(0, i.top - 2),
						Math.max(0, i.left - 2),
						Math.max(0, i.bottom - 2),
						Math.max(0, i.right - 2)),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(color),
						BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		return b;
	}

	/**
	 * Create a CompoundBorder wth the sizes indicated by the outside-inside
	 * parameters.
	 * The top level outside-inside are the same sizes as the input;
	 * the top level outside is a compound border where the inside is
	 * a line and the outside is the remaining.
	 * <p>
	 * [______|][_______]
	 * <p>
	 * @param outside
	 * @param inside
	 * @param color line color
	 * @return border
	 */
	public static Border emptyLine_empty(Insets outside, Insets inside, Color color)
	{
		Objects.requireNonNull(outside);
		Objects.requireNonNull(inside);
		Objects.requireNonNull(color);
		//Insets inside = cb.getInsideBorder().getBorderInsets(jc());
		//Insets outside = cb.getOutsideBorder().getBorderInsets(jc());
		Border b = BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(
								Math.max(0, outside.top - 1),
								Math.max(0, outside.left - 1),
								Math.max(0, outside.bottom - 1),
								Math.max(0, outside.right - 1)),
						BorderFactory.createLineBorder(color)),
				BorderFactory.createEmptyBorder(
						inside.top, inside.left, inside.bottom, inside.right));
		return b;
	}

	/**
	 * Create a CompoundBorder wth the sizes indicated by the outside-inside
	 * parameters.
	 * The top level outside-inside are the same sizes as the input;
	 * the top level outside is a compound border where the outside is
	 * a line and the inside is the remaining.
	 * <p>
	 * [|______][_______]
	 * <p>
	 * @param outside
	 * @param inside
	 * @param color line color
	 * @return border
	 */
	public static Border lineEmpty_empty(Insets outside, Insets inside, Color color)
	{
		Objects.requireNonNull(outside);
		Objects.requireNonNull(inside);
		Objects.requireNonNull(color);
		//Insets inside = cb.getInsideBorder().getBorderInsets(jc());
		//Insets outside = cb.getOutsideBorder().getBorderInsets(jc());
		Border b = BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(color),
						BorderFactory.createEmptyBorder(
								Math.max(0, outside.top - 1),
								Math.max(0, outside.left - 1),
								Math.max(0, outside.bottom - 1),
								Math.max(0, outside.right - 1))),
				BorderFactory.createEmptyBorder(
						inside.top, inside.left, inside.bottom, inside.right));
		return b;
	}

	/**
	 * Convert a border to a display string which shows compound border nesting
	 * and terminal border insets.
	 * @param b a border
	 * @param jc
	 * @return String of border for output
	 */
	public static String asString(Border b, JComponent jc) {
		if(b == null)
			return null;
		if (b instanceof CompoundBorder) {
			CompoundBorder cb = (CompoundBorder) b;
			return String.format("[%s,%s]",
					asString(cb.getOutsideBorder(), jc),
					asString(cb.getInsideBorder(), jc));
		}
		return asString(b.getBorderInsets(jc));
	}

	/**
	 * Convert inset to a display String; like "[2,2,2,2]".
	 * @param i inset
	 * @return String for output
	 */
	public static String asString(Insets i) {
		return String.format("[%d,%d,%d,%d]", i.top, i.left, i.bottom, i.right);
	}
    
}
