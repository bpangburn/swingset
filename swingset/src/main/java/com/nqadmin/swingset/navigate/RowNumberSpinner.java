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
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.navigate;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.plaf.basic.BasicSpinnerUI;

import com.nqadmin.swingset.navigate.NavigateActions.NavGotoRowAction;

/**
 * Spinner for {@linkplain RowSet}'s row number that accepts an Action;
 * it listens to an Action for enabled and forwards events to the Action.
 * Note setAction must be called for things to work properly;
 * the action contains a {@link javax.swing.SpinnerNumberModel} which
 * tracks the RowSet's current row and its limits.
 * <p>
 * There are some static helper methods for configuring a JSpinner:
 * {@link #removeTinyArrows(javax.swing.JSpinner, java.awt.Dimension)},
 * {@link #inWindowUpDownKeys(javax.swing.JComponent)},
 * and {@link #disableUpDownKeys(javax.swing.JComponent)}.
 */
@SuppressWarnings("serial")
public class RowNumberSpinner extends JSpinner
{
	private Action action;

	/**
	 * Construct spinner for row number in a data navigator.
	 */
	public RowNumberSpinner()
	{
	}

	/** {@inheritDoc} */
	@Override
	public SpinnerNumberModel getModel()
	{
		return (SpinnerNumberModel) super.getModel();
	}

	private boolean actionSetModel;
	/** {@inheritDoc} */
	@Override
	public void setModel(SpinnerModel model)
	{
		if(!actionSetModel)
			throw new IllegalArgumentException("Must use setAction");
		super.setModel(model);
	}

	/** Use this to track enabled. */
	final private PropertyChangeListener pclEnableDisableAction = (evt) -> {
		if ("enabled".equals(evt.getPropertyName()))
			setEnabled((boolean) evt.getNewValue());
	};

	/** 
	 * Listen to the specified action for enabled; send events to it.
	 * The action contains the model for the JSpinner.
	 * @param action provides enabled
	 */
	public void setAction(Action action)
	{
		if (this.action != null)
			this.action.removePropertyChangeListener(pclEnableDisableAction);
		if(!(action instanceof NavGotoRowAction gotoRowAction))
			throw new IllegalArgumentException("Must be NavGotoRowAction");
		this.action = gotoRowAction;

		// Set the JSpinner model taken from the gotAction.
		actionSetModel = true;
		try {
			setModel(gotoRowAction.rowNumberModel());
		} finally {
			actionSetModel = false;
		}
		// Listen to the gotoAction for enable/disable Spinner.
		gotoRowAction.addPropertyChangeListener(pclEnableDisableAction);
		// Copy the enable/disable state.
		setEnabled(gotoRowAction.isEnabled());
		// Forward JSpinner events through the action.
		addChangeListener((e) -> {
			gotoRowAction.actionPerformed(new ActionEvent(RowNumberSpinner.this,
										  AWTEvent.RESERVED_ID_MAX + 1, ""));
		});
	}

	/**
	 * Customize the spinner to get rid of the tiny spinner up/down arrows.
	 * There's also example code to disable/enable the keyboard up/down arrows.
	 * @param spinner the spinnner to customize
	 * @param targetSize used to set the width after removing the arrows
	 */
	//https://stackoverflow.com/questions/16284594/disable-up-and-down-arrow-buttons-on-jspinner
	public static void removeTinyArrows(JSpinner spinner, Dimension targetSize)
	{
		Dimension d = spinner.getPreferredSize();
		d.width = targetSize.width;
		spinner.setUI(new BasicSpinnerUI() {
			@Override
			protected Component createNextButton() {
				return null;
			}
			
			@Override
			protected Component createPreviousButton() {
				return null;
			}
		});
		spinner.setPreferredSize(d);

	}

	/**
	 * Wider use of the specified component's up/down arrow keys.
	 * @param comp
	 */
	public static void inWindowUpDownKeys(JComponent comp)
	{
		InputMap im = new ComponentInputMap(comp);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "increment");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "decrement");
		im.setParent(comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW));
		comp.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, im);
	}

	/**
	 * Disable the specified component's up/down arrow keys.
	 * @param comp
	 */
	public static void disableUpDownKeys(JComponent comp)
	{
		InputMap im = new InputMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "none");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "none");
		im.setParent(comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT));
		comp.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im);
	}

}
