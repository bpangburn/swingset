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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.swing.JCheckBox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

// SSCheckBox.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to display the boolean values stored in the database. The SSCheckBox can
 * be bound to a numeric or boolean database column. Currently, binding to a
 * boolean column has been tested only with PostgreSQL. If bound to a numeric
 * database column, a checked SSCheckBox returns a '1' to the database and an
 * unchecked SSCheckBox will returns a '0'. In the future an option may be added
 * to allow the user to specify the values returned for the checked and
 * unchecked SSCheckBox states.
 * <p>
 * Note that for naming consistency, SSCheckBox replaced SSDBCheckBox
 * 01-10-2005.
 */
public class SSCheckBox extends JCheckBox implements SSComponentInterface {

	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column
	 */
	protected class SSCheckBoxListener implements ItemListener, Serializable {

		/**
		 * unique serial id
		 */
		private static final long serialVersionUID = -8006881399306841024L;

		@Override
		public void itemStateChanged(final ItemEvent ie) {

			removeRowSetListener();

			if (((JCheckBox) ie.getSource()).isSelected()) {
				// switch(SSCheckBox.this.columnType) {
				switch (getBoundColumnType()) {
				case java.sql.Types.INTEGER:
				case java.sql.Types.SMALLINT:
				case java.sql.Types.TINYINT:
					// SSCheckBox.this.textField.setText(String.valueOf(SSCheckBox.this.CHECKED));
					setBoundColumnText(String.valueOf(CHECKED));
					break;
				case java.sql.Types.BIT:
				case java.sql.Types.BOOLEAN:
					// SSCheckBox.this.textField.setText(BOOLEAN_CHECKED);
					setBoundColumnText(BOOLEAN_CHECKED);
					break;
				default:
					logger.warn(getColumnForLog() + ": Unknown column type of " + getBoundColumnType());
					break;
				}
			} else {
				// switch(SSCheckBox.this.columnType) {
				switch (getBoundColumnType()) {
				case java.sql.Types.INTEGER:
				case java.sql.Types.SMALLINT:
				case java.sql.Types.TINYINT:
					setBoundColumnText(String.valueOf(UNCHECKED));
					break;
				case java.sql.Types.BIT:
				case java.sql.Types.BOOLEAN:
					setBoundColumnText(BOOLEAN_UNCHECKED);
					break;
				default:
					logger.warn(getColumnForLog() + ": Unknown column type of " + getBoundColumnType());
					break;
				}
			}

			addRowSetListener();
		}

	} // end private class SSCheckBoxListener
		// {

	/**
	 * Checked value for Boolean columns.
	 */
	protected static String BOOLEAN_CHECKED = "true";

	/**
	 * Unchecked value for Boolean columns.
	 */
	protected static String BOOLEAN_UNCHECKED = "false";

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -1204307502900668225L;

	/**
	 * Checked value for numeric columns.
	 */
	protected int CHECKED = 1;

	/**
	 * Component listener.
	 */
	protected final SSCheckBoxListener ssCheckBoxListener = new SSCheckBoxListener();

	/**
	 * Common fields shared across SwingSet components
	 */
	protected SSCommon ssCommon = new SSCommon(this);

	/**
	 * Unchecked value for numeric columns.
	 */
	protected int UNCHECKED = 0;

	/**
	 * Creates an object of SSCheckBox.
	 */
	public SSCheckBox() {
		// Note that call to parent default constructor is implicit.
		//super();
	}

	/**
	 * Creates an object of SSCheckBox binding it so the specified column in the
	 * given RowSet.
	 *
	 * @param _rowSet        datasource to be used.
	 * @param _boundColumnName name of the column to which this check box should be
	 *                         bound
	 *
	 * @throws SQLException - if a database access error occurs
	 */
	public SSCheckBox(final RowSet _rowSet, final String _boundColumnName) throws java.sql.SQLException {
		this();
		bind(_rowSet, _boundColumnName);
	}

	/**
	 * Creates an object of SSCheckBox.
	 *
	 * @param _text Checkbox label
	 */
	public SSCheckBox(final String _text) {
		super(_text);
	}

	/**
	 * Adds any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 */
	@Override
	public void addSSComponentListener() {
		addItemListener(ssCheckBoxListener);

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
		// NOTHING TO DO

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
		removeItemListener(ssCheckBoxListener);

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
		// TODO Modify this class similar to updateSSComponent() in SSFormattedTextField and only allow JDBC types that convert to Long, Integer, Boolean
		
		final String text = getBoundColumnText();
		logger.debug("{}: getBoundColumnText() - " + text, () -> getColumnForLog());

		// SELECT/DESELECT BASED ON UNDERLYING SQL TYPE
		switch (getBoundColumnType()) {
		case java.sql.Types.INTEGER:
		case java.sql.Types.SMALLINT:
		case java.sql.Types.TINYINT:
			// SET THE CHECK BOX BASED ON THE VALUE IN ROWSET
			if (text.equals(String.valueOf(CHECKED))) {
				setSelected(true);
			} else {
				setSelected(false);
			}
			break;

		case java.sql.Types.BIT:
		case java.sql.Types.BOOLEAN:
			// SET THE CHECK BOX BASED ON THE VALUE IN TEXT FIELD
			if (text.equals(BOOLEAN_CHECKED)) {
				setSelected(true);
			} else {
				setSelected(false);
			}
			break;

		default:
			break;
		}

	} // end protected void updateSSComponent() {

} // end public class SSCheckBox
