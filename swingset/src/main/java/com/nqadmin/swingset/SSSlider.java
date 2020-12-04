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
 *   Ernie R. Rael
 ******************************************************************************/
package com.nqadmin.swingset;

import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.datasources.SSRowSet;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

// SSSlider.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to link a JSlider to a numeric column in a database.
 */
public class SSSlider extends JSlider implements SSComponentInterface {

    /**
     * Listener(s) for the component's value used to propagate changes back to
     * bound text field.
     */
    protected class SSSliderListener implements ChangeListener, Serializable {

        /**
		 * unique serial ID
		 */
		private static final long serialVersionUID = -5004328872032247853L;

		@Override
		public void stateChanged(final ChangeEvent ce) {

			removeSSRowSetListener();

			setBoundColumnText(String.valueOf(getValue()));

			addSSRowSetListener();

        }

    } // end protected class SSSliderListener implements ChangeListener, Serializable {

    /**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = 8477179080546081481L;

    /**
     * Common fields shared across SwingSet components
     */
	protected SSCommon ssCommon = new SSCommon(this);

	/**
     * Component listener.
     */
    protected final SSSliderListener ssSliderListener = new SSSliderListener();

    /**
     * Empty constructor needed for deserialization. Creates a horizontal
     * slider with the range 0 to 100.
     */
    public SSSlider() {
		// Note that call to parent default constructor is implicit.
		//super();
    }

    /**
     * Creates a slider using the specified orientation with the range 0 to 100.
     *
     * @param _orientation	slider spatial orientation
     */
    public SSSlider(final int _orientation) {
		super(_orientation);
    }

    /**
     * Creates a horizontal slider using the specified min and max.
     *
     * @param _min	minimum slider value
     * @param _max	maximum slider value
     */
    public SSSlider(final int _min, final int _max) {
		super(_min, _max);
    }

    /**
     * Creates a horizontal slider with the range 0 to 100 and binds it
     * to the specified SSRowSet column.
     *
     * @param _ssRowSet    datasource to be used.
     * @param _boundColumnName    name of the column to which this slider should be bound
     * @throws java.sql.SQLException SQLException
     */
    public SSSlider(final SSRowSet _ssRowSet, final String _boundColumnName) throws java.sql.SQLException {
    	this();
		bind(_ssRowSet, _boundColumnName);
    }

	/**
	 * Adds any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 */
	@Override
	public void addSSComponentListener() {
		addChangeListener(ssSliderListener);

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
		// TODO Consider removing default dimensions.
        // SET PREFERRED DIMENSIONS
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
	 * Removes any necessary listeners for the current SwingSet component. These
	 * will trigger changes in the underlying RowSet column.
	 */
	@Override
	public void removeSSComponentListener() {
		removeChangeListener(ssSliderListener);

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

	@Override
	public void updateSSComponent() {

		// TODO Modify this class similar to updateSSComponent() in SSFormattedTextField and only allow JDBC types that convert to numeric types

		// SET THE SLIDER BASED ON THE VALUE IN THE TEXT FIELD
		switch (getBoundColumnType()) {
            case java.sql.Types.INTEGER:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.TINYINT:
            case java.sql.Types.BIGINT:
            case java.sql.Types.FLOAT:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.NUMERIC:
        	// SET THE SLIDER BASED ON THE VALUE IN TEXT FIELD
            	final String columnValue = getBoundColumnText();
            	try {
            		if ((columnValue==null) || columnValue.isEmpty()) {
            			logger.debug("{}: Setting slider to 0.", () -> getColumnForLog());
            			setValue(0);
	            	} else {
	            		logger.debug("{}: Setting slider to " + columnValue + ".", () -> getColumnForLog());
	            		setValue(Integer.parseInt(columnValue));
	            	}
            	} catch (final NumberFormatException _nfe) {
            		logger.error(getColumnForLog() + ": Number Format Exception. Cannot update slider to " + columnValue, _nfe);
            	}
                break;

            default:
            	logger.warn(getColumnForLog() + ": Unable to update Slider bound to " + getBoundColumnName() + " because the data type is not supported (" + getBoundColumnType() + ".");
                break;
        }

	}

} // end public class SSSlider extends JSlider
