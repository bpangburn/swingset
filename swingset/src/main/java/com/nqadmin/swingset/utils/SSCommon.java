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
package com.nqadmin.swingset.utils;

import java.io.Serializable;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.StringTokenizer;

import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSRowSet;

// SSCommon.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Datasource binding data members and methods common to all SwingSet
 * components.
 * <p>
 * All SwingSet components should have a ssCommon datamember of type SSCommon
 * and should implement SSComponentInterface
 * <p>
 * SwingSet components will implement addComponentListener() and
 * removeComponentListener() to maintain data flow from the JComponent to
 * ssCommon.ssRowSet and from ssCommon.ssRowSet to JComponent
 * <p>
 * Generally the developer will need to add an inner class and corresponding
 * data member that implements the appropriate listener for the JComponent
 * involved (e.g., ItemListener for a class extending JCheckBox, ChangeListener
 * for a class extending JSlider, DocumentListener for a class extending
 * JTextField, etc.).
 * <p>
 * It is possible some SwingSet will be unbound where a change to the component
 * should not trigger a database change.
 * <p>
 * A good example of this is SSDBComboBox, which may be used solely for
 * navigation.
 */
public class SSCommon implements Serializable {

	/**
	 * Document listener provided for convenience for SwingSet Components based on
	 * JTextComponents
	 * <p>
	 * Updates the underlying RowSet there is a change to the Document object. E.g.,
	 * a call to setText() on a JTextField.
	 * <p>
	 * DocumentListener events generally, but not always get fired twice any time
	 * there is an update to the JTextField: a removeUpdate() followed by
	 * insertUpdate(). See:
	 * https://stackoverflow.com/questions/15209766/why-jtextfield-settext-will-fire-documentlisteners-removeupdate-before-change#15213813
	 * <p>
	 * Using partial solution here from here:
	 * https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
	 * <p>
	 * Having removeUpdate() and insertUpdate() both call changedUpdate().
	 * changedUpdate() uses counters and SwingUtilities.invokeLater() to only update
	 * the display on the last method called.
	 */
	protected class SSDocumentListener implements DocumentListener, Serializable {

		/**
		 * unique serial id
		 */
		private static final long serialVersionUID = 2287696691641310793L;

		/**
		 * variables needed to consolidate calls to removeUpdate() and insertUpdate()
		 * from DocumentListener
		 */
		private int lastChange = 0;
		private int lastNotifiedChange = 0;

		@Override
		public void changedUpdate(final DocumentEvent de) {
			lastChange++;
			logger.trace("{} - changedUpdate(): lastChange=" + lastChange
					+ ", lastNotifiedChange=" + lastNotifiedChange, () -> getColumnForLog());
			
			// Delay execution of logic until all listener methods are called for current event
			// See: https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
			SwingUtilities.invokeLater(() -> {
				if (lastNotifiedChange != lastChange) {
					lastNotifiedChange = lastChange;

					removeSSRowSetListener();

					try {
						setBoundColumnText(((javax.swing.text.JTextComponent) getSSComponent()).getText());
					} finally {
						addSSRowSetListener();
					}
				}
			});
		}

		@Override
		public void insertUpdate(final DocumentEvent de) {
			logger.trace("{}", () -> getColumnForLog());
			changedUpdate(de);
		}

		@Override
		public void removeUpdate(final DocumentEvent de) {
			logger.trace("{}", () -> getColumnForLog());
			changedUpdate(de);
		}

	} // end protected class SSDocumentListener

	/**
	 * Listener(s) for the underlying SSRowSet used to update the bound SwingSet
	 * component.
	 */
	protected class SSRowSetListener implements RowSetListener, Serializable {
		/**
		 * unique serial ID
		 */
		private static final long serialVersionUID = -5194433723970625351L;

		/**
		 * When the cursor moves we want to trigger a change to the bound Component
		 * display/value.
		 */
		@Override
		public void cursorMoved(final RowSetEvent event) {
			logger.trace("Rowset cursor moved. {}", () -> getColumnForLog());
			updateSSComponent();
		}

		/**
		 * When the database row changes we want to trigger a change to the bound
		 * Component display/value.
		 * <p>
		 * In SSDataNavigator, when a navigation is performed (first, previous,
		 * next, last) a call is made to updateRow() to flush the rowset to the 
		 * underlying database prior to a call to first(), previous(), next(),
		 * or last(). updateRow() triggers rowChanged, but we don't want to update
		 * the components for the database flush.
		 * <p>
		 * Calls to first(), previous(), next(), and last() trigger cursorMoved.
		 * For a navigation we will updated the components following cursorMoved.
		 * <p>
		 * In JdbcRowSetImpl, notifyRowChanged() is called for insertRow(),
		 * updateRow(), deleteRow(), &amp; cancelRowUpdates(). We only want to block
		 * component updates for calls resulting from updateRow().
		 */
		@Override
		public void rowChanged(final RowSetEvent event) {
			logger.trace("Rowset row changed. {}", () -> getColumnForLog());
			if (!getSSRowSet().isUpdatingRow()) {
				updateSSComponent();
			}
		}

		/**
		 * When the RowSet is modified we want to trigger a change to the bound
		 * Component display/value.
		 */
		@Override
		public void rowSetChanged(final RowSetEvent event) {
			logger.trace("Rowset changed. {}", () -> getColumnForLog());
			updateSSComponent();
		}

	} // end protected class SSRowSetListener

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * Constant to indicate that no RowSet column index has been specified.
	 */
	public static final int NO_COLUMN_INDEX = -1;

	/**
	 * Constant to indicate that no RowSet column type has been specified.
	 * <p>
	 * Per https://www.tutorialspoint.com/java-resultsetmetadata-getcolumntype-method-with-example
	 * value can be positive or negative so it's dangerous to presume -1 can represent that
	 * no column type has been specified.
	 * <p>
	 * There is a java.sql.Type of of NULL
	 */
	@Deprecated
	public static final int NO_COLUMN_TYPE = -1;

	/**
	 * Unique serial ID.
	 */
	protected static final long serialVersionUID = -7670575893542057725L;

	/**
	 * Converts a date string in "MM/dd/yyyy" format to an SQL Date.
	 *
	 * @param _strDate date string in "MM/dd/yyyy" format
	 *
	 * @return return SQL date for the string specified
	 */
	public static Date getSQLDate(final String _strDate) {
		final StringTokenizer strtok = new StringTokenizer(_strDate, "/", false);
		final String month = strtok.nextToken();
		final String day = strtok.nextToken();
		final String newStrDate = strtok.nextToken() + "-" + month + "-" + day;
		return Date.valueOf(newStrDate);
	}

	/**
	 * Flag to indicate if the bound database column can be null.
	 * <p>
	 * Adds in a blank item being added to SSCombobox and SSDBComboBox.
	 * <p>
	 * Setting to true by default and will let database throw exceptions if not overwritten.
	 */
	private boolean allowNull = true;

	/**
	 * Index of SSRowSet column to which the SwingSet component will be bound.
	 */
	private int boundColumnIndex = NO_COLUMN_INDEX;

	/**
	 * Column JDBCType enum.
	 */
	private JDBCType boundColumnJDBCType = java.sql.JDBCType.NULL;

	/**
	 * Name of SSRowSet column to which the SwingSet component will be bound.
	 */
	private String boundColumnName = null;

	/**
	 * Column SQL data type.
	 */
	private int boundColumnType = java.sql.Types.NULL;

	/**
	 * flag to indicate if we're inside of a bind() method
	 */
	private volatile boolean inBinding = false;

	/**
	 * parent SwingSet component
	 */
	private SSComponentInterface ssComponent = null;

	/**
	 * database connection
	 */
	private SSConnection ssConnection = null;

	/**
	 * Underlying Document listener (where SwingSet component is a JTextComponent)
	 */
	private final SSDocumentListener ssDocumentListener = new SSDocumentListener();

	/**
	 * SSRowSet from which component will get/set values.
	 */
	private SSRowSet ssRowSet = null;

	/**
	 * Underlying SSRowSet listener.
	 */
	private final SSRowSetListener ssRowSetListener = new SSRowSetListener();

	/**
	 * Constructor expecting a SwingSet component as an argument (usually called as
	 * = new SSCommon(this);)
	 *
	 * @param _ssComponent SwingSet component having this SSCommon instance as a
	 *                     datamember
	 */
	public SSCommon(final SSComponentInterface _ssComponent) {
		setSSComponent(_ssComponent);
		init();
	}

	/**
	 * Convenience method to add both RowSet and SwingSet Component listeners.
	 */
	public void addListeners() {
		addSSRowSetListener();
		addSSComponentListener();
	}

	/**
	 * Method to add any SwingSet Component listener(s).
	 */
	public void addSSComponentListener() {
		ssComponent.addSSComponentListener();
	}

	/**
	 * Class to add a Document listener when the SwingSet component is a
	 * JTextComponent
	 */
	public void addSSDocumentListener() {
		((javax.swing.text.JTextComponent) getSSComponent()).getDocument().addDocumentListener(ssDocumentListener);

	}

	/**
	 * Method to add the RowSet listener.
	 */
	public void addSSRowSetListener() {
		ssRowSet.addRowSetListener(ssRowSetListener);
	}
	
	// TODO Merge common code across bind() methods.

	/**
	 * Updates the SSComponent with a valid RowSet and Column (Name or Index)
	 */
	protected void bind() {

		// TODO consider updating Component to null/zero/empty string if not valid column name, column index, or rowset
		
		// CHECK FOR NULL COLUMN/ROWSET
		if (((boundColumnName == null) && (boundColumnIndex == NO_COLUMN_INDEX)) || (ssRowSet == null)) {
			return;
		}

		// UPDATE COMPONENT
		// For an SSDBComboBox, we have likely not yet called execute to populate the
		// combo lists so the text for the first record will be blank.
		updateSSComponent();

	}

	/**
	 * Takes care of setting RowSet and Column Index for ssCommon and then calls
	 * bind() to update Component;
	 *
	 * @param _ssRowSet         datasource to be used
	 * @param _boundColumnIndex index of the column to which this check box should
	 *                          be bound
	 */
	public void bind(final SSRowSet _ssRowSet, final int _boundColumnIndex) {// throws java.sql.SQLException {
		// INDICATE THAT WE'RE UPDATING THE BINDINGS
		inBinding = true;

		// UPDATE ROWSET
		removeSSRowSetListener();
		setSSRowSet(_ssRowSet);
		addSSRowSetListener();

		// UPDATE COLUMN NAME
		setBoundColumnIndex(_boundColumnIndex);

		// INDICATE THAT WE'RE DONE SETTING THE BINDINGS
		inBinding = false;

		// UPDATE THE COMPONENT
		bind();

	}

	/**
	 * Takes care of setting RowSet and Column Name for ssCommon and then calls
	 * bind() to update Component;
	 *
	 * @param _ssRowSet        datasource to be used
	 * @param _boundColumnName name of the column to which this check box should be
	 *                         bound
	 */
	public void bind(final SSRowSet _ssRowSet, final String _boundColumnName) {// throws java.sql.SQLException {
		// INDICATE THAT WE'RE UPDATING THE BINDINGS
		inBinding = true;

		// UPDATE ROWSET
		removeSSRowSetListener();
		setSSRowSet(_ssRowSet);
		addSSRowSetListener();

		// UPDATE COLUMN NAME
		setBoundColumnName(_boundColumnName);

		// INDICATE THAT WE'RE DONE SETTING THE BINDINGS
		inBinding = false;

		// UPDATE THE COMPONENT
		bind();

	}

	/**
	 * Retrieves the allowNull flag for the bound database column.
	 *
	 * @return true if bound database column can contain null values, otherwise
	 *         returns false
	 */
	public boolean getAllowNull() {
		return allowNull;
	}

	/**
	 * Returns the index of the database column to which the SwingSet component is
	 * bound.
	 *
	 * @return returns the index of the column to which the SwingSet component is
	 *         bound
	 */
	public int getBoundColumnIndex() {
		return boundColumnIndex;
	}

	/**
	 * Returns the JDBCType enum representing the bound database column data type.
	 * <p>
	 * Based on java.sql.JDBCType
	 *
	 * @return the enum value corresponding to the data type of the bound column
	 */
	public JDBCType getBoundColumnJDBCType() {
		return boundColumnJDBCType;
	}

	/**
	 * Returns the name of the database column to which the SwingSet component is
	 * bound.
	 *
	 * @return the boundColumnName
	 */
	public String getBoundColumnName() {
		return boundColumnName;
	}

	/**
	 * Returns a String representing the value in the bound database column.
	 * <p>
	 * New functionality added (2020) to allow this method to return a null String
	 * if allowNull==true. allowNull is false by default so nulls will be converted
	 * to empty strings.
	 *
	 * @return String containing the value in the bound database column
	 */
	public String getBoundColumnText() {

// TODO Consider checking for a null RowSet. This would be the case for an unbound SSDBComboBox used for navigation.

		String value = "";

		try {
			if (getSSRowSet().getRow() != 0) {
				value = getSSRowSet().getColumnText(getBoundColumnName());
				if (!getAllowNull() && (value == null)) {
					value = "";
				}
			}
		} catch (final SQLException se) {
			logger.error(getColumnForLog() + " - SQL Exception.", se);
		}

		return value;
	}

	/**
	 * Returns the integer code representing the bound database column data type.
	 * <p>
	 * Based on java.sql.Types
	 *
	 * @return the data type of the bound column
	 */
	public int getBoundColumnType() {
		return boundColumnType;
	}

	/**
	 * Returns the bound column name in square brackets.
	 *
	 * @return the boundColumnName in square brackets
	 */
	public String getColumnForLog() {
		return "[" + boundColumnName + "]";
	}

	/**
	 * @return the parent/calling SwingSet JComponent implementing
	 *         SSComponentInterface
	 */
	public SSComponentInterface getSSComponent() {
		return ssComponent;
	}

	/**
	 * Returns the SSConnection to the database
	 *
	 * @return the ssConnection
	 */
	public SSConnection getSSConnection() {
		return ssConnection;
	}

	/**
	 * Returns the SSRowSet to which the SwingSet component is bound.
	 *
	 * @return SSRowSet to which the SwingSet component is bound
	 */
	public SSRowSet getSSRowSet() {
		return ssRowSet;
	}

	/**
	 * Returns Listener for the RowSet bound to the database
	 *
	 * @return listener for the bound RowSet
	 */
	public SSRowSetListener getSSRowSetListener() {
		return ssRowSetListener;
	}

	/**
	 * Method called from Constructor to perform one-time setup tasks including
	 * field traversal and any custom initialization method specific to the SwingSet
	 * component.
	 */
	protected void init() {
		getSSComponent().configureTraversalKeys();
		getSSComponent().customInit();
	}

	/**
	 * Convenience method to remove both RowSet and SwingSet Component listeners.
	 */
	public void removeListeners() {
		removeSSRowSetListener();
		removeSSComponentListener();
	}

	/**
	 * Method to remove any SwingSet Component listener(s).
	 */
	public void removeSSComponentListener() {
		ssComponent.removeSSComponentListener();
	}

	/**
	 * Class to remove a Document listener when the SwingSet component is a
	 * JTextComponent
	 */
	public void removeSSDocumentListener() {
		((javax.swing.text.JTextComponent) getSSComponent()).getDocument().removeDocumentListener(ssDocumentListener);

	}

	/**
	 * Method to remove the RowSet listener.
	 */
	public void removeSSRowSetListener() {
		if (ssRowSet != null) {
			ssRowSet.removeRowSetListener(ssRowSetListener);
		}
	}

	/**
	 * Sets the allowNull flag for the bound database column.
	 *
	 * @param _allowNull flag to indicate if the bound database column can be null
	 */
	public void setAllowNull(final boolean _allowNull) {
		allowNull = _allowNull;
	}

	/**
	 * Updates the bound database column with the specified Array.
	 * <p>
	 * Used for SSList or other component where multiple items can be selected.
	 *
	 * @param _boundColumnArray Array to write to bound database column
	 * @throws SQLException thrown if there is a problem writing the array to the
	 *                      RowSet
	 */
	public void setBoundColumnArray(final SSArray _boundColumnArray) throws SQLException {
		getSSRowSet().updateArray(getBoundColumnName(), _boundColumnArray);
	}

	/**
	 * Sets the column index to which the Component is to be bound.
	 *
	 * @param _boundColumnIndex column index to which the Component is to be bound
	 */
	public void setBoundColumnIndex(final int _boundColumnIndex) {

		// SET COLUMN INDEX
		if (_boundColumnIndex > NO_COLUMN_INDEX) {
			boundColumnIndex = _boundColumnIndex;
		} else {
			boundColumnIndex = NO_COLUMN_INDEX;
		}

		// DETERMINE COLUMN NAME AND TYPE
		try {
			// IF COLUMN INDEX IS VALID, GET COLUMN NAME, OTHERWISE SET TO NULL
// TODO Update SSRowSet to return constant or throw Exception if invalid/out of bounds.
			if (boundColumnIndex != NO_COLUMN_INDEX) {
				boundColumnName = getSSRowSet().getColumnName(boundColumnIndex);
				boundColumnType = getSSRowSet().getColumnType(boundColumnIndex);
			} else {
				boundColumnName = null;
				boundColumnType = java.sql.Types.NULL;
			}
			boundColumnJDBCType = JDBCType.valueOf(boundColumnType);

		} catch (final SQLException se) {
			logger.error(getColumnForLog() + " - SQL Exception.", se);
		}

		// BIND UPDATED COLUMN IF APPLICABLE
		if (!inBinding) {
			bind();
		}

	}

	/**
	 * Sets the name of the bound database column.
	 *
	 * @param _boundColumnName column name to which the Component is to be bound.
	 */
	public void setBoundColumnName(final String _boundColumnName) {

		// SET COLUMN NAME
		if (!_boundColumnName.isEmpty()) {
			boundColumnName = _boundColumnName;
		} else {
			boundColumnName = null;
		}

		// DETERMINE COLUMN INDEX AND TYPE
		try {
			// IF COLUMN NAME ISN'T NULL, SET COLUMN INDEX - OTHERWISE, SET INDEX TO
			// NO_INDEX
// TODO Update SSRowSet to return constant or throw Exception if invalid/out of bounds.
			if (boundColumnName != null) {
				boundColumnIndex = getSSRowSet().getColumnIndex(boundColumnName);
				boundColumnType = getSSRowSet().getColumnType(boundColumnIndex);
			} else {
				boundColumnIndex = NO_COLUMN_INDEX;
				boundColumnType = java.sql.Types.NULL;
			}
			boundColumnJDBCType = JDBCType.valueOf(boundColumnType);

		} catch (final SQLException se) {
			logger.error(getColumnForLog() + " - SQL Exception.", se);
		}

		// BIND UPDATED COLUMN IF APPLICABLE
		if (!inBinding) {
			bind();
		}
	}

	/**
	 * Updates the bound database column with the specified String.
	 *
	 * @param _boundColumnText value to write to bound database column
	 */
	public void setBoundColumnText(final String _boundColumnText) {
		try {
			getSSRowSet().updateColumnText(_boundColumnText, getBoundColumnName(), getAllowNull());
		} catch(final NullPointerException _npe) {
			logger.warn("Null Pointer Exception.", _npe);
			JOptionPane.showMessageDialog((JComponent)getSSComponent(),
					"Null values are not allowed for " + getBoundColumnName(), "Null Exception", JOptionPane.ERROR_MESSAGE);

		} catch(final SQLException _se) {
			logger.warn("SQL Exception.", _se);
			JOptionPane.showMessageDialog((JComponent)getSSComponent(),
					"SQL Exception encountered for " + getBoundColumnName(), "SQL Exception", JOptionPane.ERROR_MESSAGE);

		} catch(final NumberFormatException _pe) {
			logger.warn("Number Format Exception.", _pe);
			JOptionPane.showMessageDialog((JComponent)getSSComponent(),
					"Number Format Exception encountered for " + getBoundColumnName() + " converting " + _boundColumnText + " to a number.",
					"Number Format Exception", JOptionPane.ERROR_MESSAGE);

		}

	}

	/**
	 * Sets the SwingSet component of which this SSCommon instance is a datamember.
	 *
	 * @param _ssComponent the parent/calling SwingSet JComponent implementing
	 *                     SSComponentInterface
	 */
	public void setSSComponent(final SSComponentInterface _ssComponent) {
		ssComponent = _ssComponent;
	}

	/**
	 * Sets the SSConnection to the database
	 *
	 * @param _ssConnection the ssConnection to set
	 */
	public void setSSConnection(final SSConnection _ssConnection) {
		ssConnection = _ssConnection;
	}

	/**
	 * Sets the SSRowSet to which the Component is bound.
	 *
	 * @param _ssRowSet SSRowSet to which the component is bound
	 */
	public void setSSRowSet(final SSRowSet _ssRowSet) {
		ssRowSet = _ssRowSet;
		if (!inBinding) {
			bind();
		}
	}

	/**
	 * Method used by SSRowSet listeners to update the bound SwingSet component.
	 * <p>
	 * Handles removal of Component listener before update and addition of listener
	 * after update.
	 */
	public void updateSSComponent() {
		ssComponent.removeSSComponentListener();

		ssComponent.updateSSComponent();

		ssComponent.addSSComponentListener();

	}

}
