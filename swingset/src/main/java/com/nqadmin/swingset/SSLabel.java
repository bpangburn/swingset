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
package com.nqadmin.swingset;
import static com.nqadmin.swingset.utils.SSUtils.sf;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.sql.RowSet;
import javax.swing.Icon;
import javax.swing.JLabel;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

// SSLabel.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to display database values in a read-only JLabel.
 */
public class SSLabel extends JLabel implements SSComponentInterface {

	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column.
	 * <p>
	 * There is not an obvious use-case where a label would change, but could be
	 * tied to a menu, screen logic, or some other Developer driven change that
	 * could conceivably need to be synchronized back to the RowSet.
	 */
	protected class SSLabelListener implements PropertyChangeListener, Serializable {

		/**
		 * unique serial ID
		 */
		private static final long serialVersionUID = 6786673052979566820L;

		/** {@inheritDoc} */
		@Override
		public void propertyChange(final PropertyChangeEvent pce) {

			// CONFIRM THE PROPERTY NAME IN CASE SOMEONE ADDS A DIFFERENT PROPERTY LISTENER
			// TO ssLabelListener
			if ("text".equals(pce.getPropertyName())) {

				getSSCommon().removeRowSetListener();

				setBoundColumnText(getText());

				getSSCommon().addRowSetListener();
			}

		}

	} // end protected class SSLabelListener

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = SSUtils.getLogger();

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -5232780793538061537L;

	/**
	 * Common fields shared across SwingSet components
	 */
	private final SSCommon ssCommon;
	
	/**
	 * Empty constructor needed for deserialization. Creates a SSLabel instance with
	 * no image and no text.
	 */
	public SSLabel() {
		ssCommon = finishSSCommon();
	}

	/**
	 * Creates a SSLabel instance with the specified image.
	 *
	 * @param _image specified image for label
	 */
	public SSLabel(final Icon _image) {
		super(_image);
		ssCommon = finishSSCommon();
	}

	/**
	 * Creates a SSLabel instance with the specified image and horizontal alignment.
	 *
	 * @param _image               specified image for label
	 * @param _horizontalAlignment horizontal alignment
	 */
	public SSLabel(final Icon _image, final int _horizontalAlignment) {
		super(_image, _horizontalAlignment);
		ssCommon = finishSSCommon();
	}

	/**
	 * Creates a SSLabel instance with no image and binds it to the specified RowSet
	 * column.
	 *
	 * @param _rowSet          datasource to be used.
	 * @param _boundColumnName name of the column to which this label should be
	 *                         bound
	 */
	public SSLabel(final RowSet _rowSet, final String _boundColumnName) {
		this();
		bind(_rowSet, _boundColumnName);
	}

	/**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * <p>
	 * It will actually be called from SSCommon.init() once the SSCommon data member
	 * is instantiated.
	 */
	@Override
	public void customInit() {
		// SET PREFERRED DIMENSIONS
// TODO not sure SwingSet should be setting component dimensions
		setPreferredSize(new Dimension(200, 20));
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public SSLabelListener getSSComponentListener() {
		return new SSLabelListener();
	}

	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText()
	 * <p>
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed
	 */
	@Override
	public void updateSSComponent() {
		final String text = getBoundColumnText();
		logger.debug("{}: Setting label to " + text + ".", () -> getColumnForLog());
		setText(text);
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("%s{text=%s, %s}", getClass().getSimpleName(),
				getText(), SSUtils.ssComponentToString(this));
	}

	/**
	 * Returns ssCommon for the current Swingset component.
	 *
	 * @return common SwingSet component data and methods
	 */
    @Override
	public SSCommon getSSCommon() {
		if (ssCommon == null)
			return partialSSCommon = SSCommon.createStart(this, partialSSCommon);
		return ssCommon;
	}

	private SSCommon partialSSCommon;

	/**
	 * Either return a new create ssCommon or 
	 * Only call from constructor; "ssCommon = finishSSCommon()".
	 */
	private SSCommon finishSSCommon() {
		SSCommon rv = SSCommon.createFinish(this, partialSSCommon);
		partialSSCommon = null;
		return rv;
	}

} // end public class SSLabel extends JLabel {
