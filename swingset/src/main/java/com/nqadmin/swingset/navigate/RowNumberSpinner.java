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
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSpinnerUI;

import com.nqadmin.swingset.navigate.NavigateActions.NavGotoRowAction;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_UP;
import static javax.swing.KeyStroke.getKeyStroke;

/**
 * Spinner for {@linkplain javax.sql.RowSet}'s row number that accepts an Action;
 * it listens to an Action for enabled and forwards events to the Action.
 * The action is associated with the RowSet's {@link NavigateActions}.
 * Note setAction must be called for things to work properly;
 * the action contains a {@link javax.swing.SpinnerNumberModel} which
 * tracks the ResultSet's current row and its limits.
 * <p>
 * There are some methods for configuring this Spinner:
 * <ul>
 * <li>{@link #removeTinyArrows(java.awt.Dimension)}
 * <li>{@link #setWindowUpDownKeysEnable(boolean) }
 * <li>{@link #setUpDownKeysEnable(boolean) }
 * </ul>
 * These methods are accessible through {@link com.nqadmin.swingset.SSDataNavigator} methods.
 */
@SuppressWarnings("serial")
public class RowNumberSpinner extends JSpinner
{
	private NavGotoRowAction gotoRowAction; // TODO: replace with NavigationModel?

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
	/**
	 * {@inheritDoc}
	 * 
	 * <b>An exception is thrown</b>
	 * @deprecated use {@link #setModel(com.nqadmin.swingset.navigate.NavigationModel) }.
	 */
	@Override
	@Deprecated
	public void setModel(SpinnerModel model)
	{
		if(!actionSetModel)
			throw new IllegalCallerException("Use the other setModel");
		super.setModel(model);
	}

	/**
	 * Sets the model to the SpinnerNumberModel associated with a
	 * given RowSet's NavigateActions's NavGotoRowAction.
	 * @param navModel 
	 */
	public void setModel(NavigationModel navModel)
	{
		setAction(navModel.getAction(NavAction.NAV_GOTOROW));
	}


	/** Use this to track enabled. */
	private final PropertyChangeListener pclEnableDisableAction = (evt) -> {
		if ("enabled".equals(evt.getPropertyName()))
			setEnabled((boolean) evt.getNewValue());
	};

	/** forward spinner events to goto row action */
	private final ChangeListener changeListener = (evt) -> {
		//System.err.println("changeListener: " + objectID(gotoRowAction));
		gotoRowAction.actionPerformed(new ActionEvent(RowNumberSpinner.this,
				AWTEvent.RESERVED_ID_MAX + 1, ""));
	};

	/** 
	 * Listen to the specified action for Spinner enabled; send events to it.
	 * The action contains the model for the JSpinner.
	 * @param action provides enabled
	 */
	// TODO: make setAction private (or maybe package) in favor of setModel(nav model)
	public void setAction(Action action) {
		if(!(action instanceof NavGotoRowAction tmpGotoRowAction))
			throw new IllegalArgumentException("Must be NavGotoRowAction");

		if (gotoRowAction != null)
			gotoRowAction.removePropertyChangeListener(pclEnableDisableAction);
		removeChangeListener(changeListener);

		gotoRowAction = tmpGotoRowAction;

		// Set the JSpinner model taken from the gotAction.
		// If JSpinner setModel doesn't come from right here,
		// an exception is thrown.
		actionSetModel = true;
		try {
			setModel(gotoRowAction.rowNumberModel());
		} finally {
			actionSetModel = false;
		}
		// Copy the enable/disable state.
		setEnabled(gotoRowAction.isEnabled());

		// Listen to the gotoAction for enable/disable Spinner.
		gotoRowAction.addPropertyChangeListener(pclEnableDisableAction);

		// Forward JSpinner events through the action.
		addChangeListener(changeListener);
	}

	/**
	 * Customize the spinner to get rid of the tiny spinner up/down arrows. There's also example code to disable/enable the keyboard up/down arrows.
	 * @param targetSpinnerSize used to set the width after removing the arrows
	 */
	//https://stackoverflow.com/questions/16284594/disable-up-and-down-arrow-buttons-on-jspinner
	public void removeTinyArrows(Dimension targetSpinnerSize)
	{
		Dimension d = getPreferredSize();
		d.width = targetSpinnerSize.width;
		setUI(new BasicSpinnerUI() {
			@Override
			protected Component createNextButton() {
				return null;
			}
			
			@Override
			protected Component createPreviousButton() {
				return null;
			}
		});
		setPreferredSize(d);
	}

	/**
	 * Get one of this component's local input maps. If it doesn't exist
	 * then create it and hook it in to the component.
	 * @return the specified input map
	 */
	private InputMap getMyInputMap(int whichMap)
	{
		return switch (whichMap) {
		case WHEN_IN_FOCUSED_WINDOW -> {
			if (inFocusedWindowInputMap == null) {
				InputMap im = new ComponentInputMap(this);
				im.setParent(getInputMap(WHEN_IN_FOCUSED_WINDOW));
				setInputMap(WHEN_IN_FOCUSED_WINDOW, im);
				inFocusedWindowInputMap = im;
			}
			yield inFocusedWindowInputMap;
		}
		case WHEN_ANCESTOR_OF_FOCUSED_COMPONENT -> {
			if (ancestorOfFocusedComponentInputMap == null) {
				InputMap im = new InputMap();
				im.setParent(getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT));
				setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im);
				ancestorOfFocusedComponentInputMap = im;
			}
			yield ancestorOfFocusedComponentInputMap;
		}
		default -> null;
		};
	}

	private InputMap inFocusedWindowInputMap;
	private InputMap ancestorOfFocusedComponentInputMap;

	/**
	 * Use the up/down arrow keys while this spinner's window is focused
	 * to adjust row number.
	 * @param enable true enables up/down keys when window has focus
	 */
	public void setWindowUpDownKeysEnable(boolean enable)
	{
		InputMap im = getMyInputMap(WHEN_IN_FOCUSED_WINDOW);
		im.put(getKeyStroke(VK_UP, 0), enable ? "increment" : null);
		im.put(getKeyStroke(VK_DOWN, 0), enable ? "decrement" : null);
	}

	/**
	 * Disable the up/down arrow keys for this spinner component.
	 * When disabling, window up/down keys are disabled
	 * using {@link #setWindowUpDownKeysEnable(boolean)}.
	 * 
	 * @param enable true enables default
	 */
	public void setUpDownKeysEnable(boolean enable)
	{
		InputMap im = getMyInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(getKeyStroke(VK_UP, 0), enable ? null : "none");
		im.put(getKeyStroke(VK_DOWN, 0), enable ? null : "none");
	}
}
