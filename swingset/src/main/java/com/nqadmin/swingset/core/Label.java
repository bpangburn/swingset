/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 ******************************************************************************/
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2025-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.System.Logger;
import java.util.EventListener;

import javax.swing.Icon;
import javax.swing.JLabel;

import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSComponent;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * Used to display database values in a read-only JLabel.
 * By default, programmatic changes to the label are not propagated,
 * except of course to set a label's value from a RowSet.
 */
@SuppressWarnings("serial")
public class Label extends JLabel implements SSComponent
{
	/** ugh */
	// TODO: Come up with general way to allow selective prop change disable.
	protected boolean allowPropertyChangePropagation = false;
	/**
	 * Listener for label changed externally; propagate the value to the
	 * database column. By default not enabled.
	 */
	protected class LabelListener implements PropertyChangeListener
	{
		/** Propogate "text" property change to database.
		 * {@inheritDoc} */
		@Override
		public void propertyChange(PropertyChangeEvent pce)
		{
			if (!allowPropertyChangePropagation)
				return;
			if (!"text".equals(pce.getPropertyName()))
				return;

			dbChange(() -> setBoundColumnText(getText()));
		}
	} // end protected class LabelListener

	/** Log4j Logger for component */
	private static Logger logger = SSUtils.getLogger();

	/**
	 * Empty constructor needed for deserialization. Creates a Label instance with
	 * no image and no text.
	 */
	public Label() {
		finishSSCommon();
	}

	/**
	 * Creates a Label instance with the specified image.
	 *
	 * @param image specified image for label
	 */
	public Label(Icon image) {
		super(image);
		finishSSCommon();
	}

	/**
	 * Creates a Label instance with the specified image and horizontal alignment.
	 *
	 * @param image               specified image for label
	 * @param horizontalAlignment horizontal alignment
	 */
	public Label(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		finishSSCommon();
	}

	/**
	 * Creates a Label instance with no image and binds it to the specified RowSet
	 * column.
	 *
	 * @param rowsModel          datasource to be used.
	 * @param boundColumnName name of the column to which this label should be bound
	 */
	public Label(RowsModel rowsModel, String boundColumnName) {
		this();
		rowsModel.bind(this, boundColumnName);
	}

	/** {@inheritDoc } */
	@Override
	public void cleanField()
	{
		setText("");
	}

	private Hook hook;

	/** {@inheritDoc } */
	@Override
	public final Hook getSSComponentHook()
	{
		if (hook == null)
			hook = new Hook(this) {
				/**
				 * Updates the value stored and displayed in the SwingSet
				 * component based on getBoundColumnText()
				 */
				@Override
				protected void updateSSComponent() {
					final String text = getBoundColumnText();
					logger.log(DEBUG, ()->sf("%s: Setting label to %s.", getColumnForLog(), text));
					setText(text);
				}
				
				/** {@inheritDoc } */
				@Override
				protected LabelListener getSSComponentListener() {
					return new LabelListener();
				}
				
				/** {@inheritDoc } */
				@Override
				protected void addSSComponentListener(EventListener eventListener)
				{
					addPropertyChangeListener("text", ((PropertyChangeListener) eventListener));
				}
				
				/** {@inheritDoc } */
				@Override
				protected void removeSSComponentListener(EventListener eventListener)
				{
					removePropertyChangeListener("text", ((PropertyChangeListener) eventListener));
				}
				
			};
		return hook;
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("%s{text=%s, %s}", getClass().getSimpleName(),
				getText(), SSUtils.ssComponentToString(this));
	}
} // end public class Label extends JLabel {
