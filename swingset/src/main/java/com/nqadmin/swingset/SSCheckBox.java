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
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.swing.JCheckBox;

import java.lang.System.Logger;

import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.datasources.ConvertType.verifyConvertToType;
import static com.nqadmin.swingset.utils.SSUtils.sf;

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
@SuppressWarnings("serial")
public class SSCheckBox extends JCheckBox implements SSComponentInterface
{
	private final boolean useObject = true; // To test read/write technique.

	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column
	 */
	protected class SSCheckBoxListener implements ItemListener
	{
		/** {@inheritDoc} */
		@Override
		public void itemStateChanged(final ItemEvent ie)
		{
			ssCommon.removeRowSetListener();

			if (useObject)
				setBoundColumnObject(isSelected());
			else {
				setBoundColumnText(switch (getBoundColumnJDBCType()) {
				case INTEGER, SMALLINT, TINYINT -> isSelected() ? CHECKED : UNCHECKED;
				case BIT, BOOLEAN -> isSelected() ? BOOLEAN_CHECKED : BOOLEAN_UNCHECKED;
				default -> "";
				});
			}

			ssCommon.addRowSetListener();
		}
	} // end private class SSCheckBoxListener

	/** Checked value for Boolean columns. */
	protected static String BOOLEAN_CHECKED = "true";

	/** Unchecked value for Boolean columns. */
	protected static String BOOLEAN_UNCHECKED = "false";

	/** Checked value for numeric columns. */
	protected String CHECKED = "1";

	/** Unchecked value for numeric columns. */
	protected String UNCHECKED = "0";

	/** Common fields shared across SwingSet components. */
	transient protected final SSCommon ssCommon = new SSCommon(this);

	/** System Logger for component. */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Creates an object of SSCheckBox.
	 */
	public SSCheckBox() {
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
	@SuppressWarnings("OverridableMethodCallInConstructor")
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

	/** {@inheritDoc} */
	@Override
	public void bind(RowSet _rowSet, String _boundColumnName)
	{
		verifyConvertToType(_rowSet, _boundColumnName, Boolean.class);

		// TODO: the debugger shows _rowSet as null
		SSComponentInterface.super.bind(_rowSet, _boundColumnName);

		//
		// TODO: RowSetOps.canConvertToType(this, Boolean.class)
		//

		// TODO: The following must be done before bind. Catch 22.
		//RowSetOps.verifyConvertToType(this, Boolean.class);

		//JDBCType typ = getBoundColumnJDBCType();
		//switch(typ) {
		//		case INTEGER, SMALLINT, TINYINT, BIT, BOOLEAN -> {}
		//		default -> throw new IllegalArgumentException(
		//				sf("'%s' invalid column type", typ));
		//}
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
	 * {@inheritDoc }
	 */
	@Override
	public SSCheckBoxListener getSSComponentListener() {
		return new SSCheckBoxListener();
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
		logger.log(DEBUG, () -> sf("%s: getBoundColumnText() - %s",getColumnForLog(), text));

		if (useObject)
			setSelected(getBoundColumnObject(Boolean.class));
		else {
			// SELECT/DESELECT BASED ON UNDERLYING SQL TYPE
			switch(getBoundColumnJDBCType()) {
			// Use "UNCHECKED", "0" is the only false, CHECKED COULD be many values
			case INTEGER, SMALLINT, TINYINT -> setSelected(!UNCHECKED.equals(text));
			case BIT, BOOLEAN -> setSelected(BOOLEAN_CHECKED.equals(text));
			}
		}
	} // end protected void updateSSComponent() {

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("SSCheckBox{selected=%s, %s}",
				isSelected(), SSUtils.ssComponentToString(this));
	}

} // end public class SSCheckBox
