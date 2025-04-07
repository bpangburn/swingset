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
import java.lang.System.Logger;

import javax.swing.Action;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSpinnerUI;

import com.nqadmin.swingset.navigate.RowsActions.NavGotoRowAction;
import com.nqadmin.swingset.utils.SSUtils;
import com.raelity.lib.eventbus.WeakEventBus;
import com.raelity.lib.eventbus.WeakSubscribe;

import static com.nqadmin.swingset.navigate.Utils.getGlobalEventBus;
import static com.nqadmin.swingset.utils.SSUtils.isJunitPrint;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_UP;
import static java.lang.System.Logger.Level.*;
import static javax.swing.KeyStroke.getKeyStroke;

/**
 * A {@code JSpinner} for {@linkplain javax.sql.RowSet}'s row number;
 * the {@code JSpinner} is associated with a {@code RowsModel}.
 * The {@code JSpinner} uses a {@code SpinnerNumberModel} which tracks
 * the {@code RowsModel}'s {@code ResultSet}'s current row and its limits.
 * <p>
 * There are some methods for configuring this Spinner:
 * <ul>
 * <li>{@link #removeTinyArrows(java.awt.Dimension)}
 * <li>{@link #setWindowUpDownKeysEnable(boolean) }
 * <li>{@link #setUpDownKeysEnable(boolean) }
 * </ul>
 * These methods are accessible through {@link com.nqadmin.swingset.SSDataNavigator} methods.
 * <p>
 * There's a Spinner API tweak such that {@code setModel()}
 * sends a {@code ChangeEvent}. This is convenient when considering the spinner
 * as part of a navigator and {@code RowsModel.setRowSet()}.
 */
@SuppressWarnings("serial")
public class RowNumberSpinner extends JSpinner
{
	private static final Logger logger = SSUtils.getLogger();
	//private NavGotoRowAction gotoRowAction;
	private RowsModel rowsModel;

	/**
	 * Construct spinner for row number in a data navigator.
	 * @param rowsModel
	 */
	public RowNumberSpinner(RowsModel rowsModel)
	{
		this.rowsModel = rowsModel;
		busReceiver = new BusReceiver();
		WeakEventBus.register(busReceiver, getGlobalEventBus());

		setAction();
	}

	BusReceiver busReceiver; // Must have a strong reference.
	class BusReceiver
	{
		// Each RowSet has it's own SpinnerModel.
		// Need to note model change to update spinner, no gain in wrapping spinner model.
		@WeakSubscribe
		public void handleNewRowSetEvent(RowsModelNewRowSetEvent ev)
		{
			if (ev.getRowsModel() != rowsModel)
				return;
			if (isJunitPrint())
				System.err.printf("RowNumberSpinner: %s\n", ev.toString());
			else
				logger.log(DEBUG, () -> sf("Change spinner rowSet/model %s", ev.toString()));
			internalChangeSpinnerModel();
		}
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
	 * <b>An exception is thrown if invoked unexpectedly.</b>
	 */
	@Override
	public void setModel(SpinnerModel model)
	{
		if(!actionSetModel)
			throw new IllegalCallerException("Can not change the model");
		super.setModel(model);
	}

	/** Use this to track enabled. */
	private final PropertyChangeListener pclEnableDisableAction = (evt) -> {
		if ("enabled".equals(evt.getPropertyName()))
			setEnabled((boolean) evt.getNewValue());
	};

	/** forward spinner events to goto row action */
	private final ChangeListener changeListener = (evt) -> {
		if (rowsModel.getRowSet() == null)
			return;
		rowsModel.getAction(RowsAction.ACT_GOTOROW)
				.actionPerformed(new ActionEvent(RowNumberSpinner.this,
				AWTEvent.RESERVED_ID_MAX + 1, RowsAction.OK_SKIP_CURSOR_MOVE));
	};

	private void internalChangeSpinnerModel()
	{
		// If JSpinner setModel doesn't come from right here,
		// an exception is thrown.
		actionSetModel = true;
		try {
			if (rowsModel.getRowSet() == null)
				setModel( new SpinnerNumberModel());
			else
				setModel(rowsModel.getSpinnerModel());
			fireStateChanged(); // Treat a model change as a state change
		} finally {
			actionSetModel = false;
		}
	}

	/** 
	 * Listen to the specified action for Spinner enabled; send events to it.
	 * The action contains the model for the JSpinner.
	 * @param action provides enabled
	 */
	private void setAction() {
		Action action = rowsModel.getAction(RowsAction.ACT_GOTOROW);

		if(!(action instanceof NavGotoRowAction gotoRowAction))
			throw new IllegalArgumentException("Must be NavGotoRowAction");

		removeChangeListener(changeListener);

		internalChangeSpinnerModel();

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
