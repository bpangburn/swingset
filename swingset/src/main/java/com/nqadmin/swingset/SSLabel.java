/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 ******************************************************************************/


package com.nqadmin.swingset;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.JLabel;

import com.nqadmin.swingset.datasources.SSRowSet;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

// SSLabel.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to display database values in a read-only JLabel.
 */
public class SSLabel extends JLabel implements SSComponentInterface {

    /**
     * Listener(s) for the component's value used to propagate changes back to
     * bound database column.
     * <p>
     * There is not an obvious use-case where a label would change, but could be
     * tied to a menu, screen logic, or some other Developer driven change that could
     * conceivably need to be synchronized back to the RowSet.
     */
    protected class SSLabelListener implements PropertyChangeListener, Serializable {
    	
        /**
		 * unique serial ID
		 */
		private static final long serialVersionUID = 6786673052979566820L;

		@Override
		public void propertyChange(final PropertyChangeEvent pce) {

			// CONFIRM THE PROPERTY NAME IN CASE SOMEONE ADDS A DIFFERENT PROPERTY LISTENER TO ssLabelListener
			if (pce.getPropertyName()=="text") {

				removeSSRowSetListener();

				setBoundColumnText(getText());

				addSSRowSetListener();
			}

		}

    } // end protected class SSLabelListener

    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = -5232780793538061537L;

    /**
     * Common fields shared across SwingSet components
     */
    protected SSCommon ssCommon;

    /**
     * Component listener.
     */
    protected final SSLabelListener ssLabelListener = new SSLabelListener();

    /**
     * Empty constructor needed for deserialization. Creates a SSLabel instance
     * with no image and with an empty string for the title.
     */
    public SSLabel() {
        super("<label text here>");
        setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
    }

    /**
     * Creates a SSLabel instance with the specified image.
     *
     * @param _image    specified image for label
     */
    public SSLabel(final Icon _image) {
		super(_image);
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
    }

    /**
     * Creates a SSLabel instance with the specified image and horizontal alignment.
     *
     * @param _image    specified image for label
     * @param _horizontalAlignment	horizontal alignment
     */
    public SSLabel(final Icon _image, final int _horizontalAlignment) {
		super(_image, _horizontalAlignment);
		setSSCommon(new SSCommon(this));
    }

	/**
     * Creates a SSLabel instance with no image and binds it to the specified
     * SSRowSet column.
     *
     * @param _ssRowSet    datasource to be used.
     * @param _boundColumnName    name of the column to which this label should be bound
     */
    public SSLabel(final SSRowSet _ssRowSet, final String _boundColumnName) {
		super();
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
		bind(_ssRowSet, _boundColumnName);
    }

	/**
	 * Adds any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 * <p>
	 * Generally an SSLabel will be read-only, but Developer could change the text
	 * so we'll support a property listener.
	 */
	@Override
	public void addSSComponentListener() {
		addPropertyChangeListener("text", ssLabelListener);
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
            setPreferredSize(new Dimension(200,20));
    }

	/**
	 * Returns the ssCommon data member for the current Swingset component.
	 *
	 * @return shared/common SwingSet component data and methods
	 */
    @Override
	public SSCommon getSSCommon() {
		return ssCommon;
	}

	/**
	 * Removes any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 * <p>
	 * Generally an SSLabel will be read-only, but Developer could change the text so
	 * we'll support a property listener.
	 */
	@Override
	public void removeSSComponentListener() {
		removePropertyChangeListener("text", ssLabelListener);
	}



	/**
	 * Sets the SSCommon data member for the current Swingset Component.
	 *
	 * @param _ssCommon shared/common SwingSet component data and methods
	 */
	@Override
	public void setSSCommon(final SSCommon _ssCommon) {
		ssCommon = _ssCommon;
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
		setText(getBoundColumnText());
	}

} // end public class SSLabel extends JLabel {
