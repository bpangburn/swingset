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
package com.nqadmin.swingset.utils;

import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Set;

import javax.sql.RowSet;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import java.sql.Connection;

import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.decorators.BorderDecorator;
import com.nqadmin.swingset.decorators.Decorator;
import com.nqadmin.swingset.decorators.Validator;

// SSComponentInterface.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Interface with default methods shared by most SwingSet components.
 * <p>
 * Developer needs to insure that each implementation has an SSCommon data
 * member that is instantiated in the Component's constructor.
 * <p>
 * IMPORTANT: Do not call {@link #updateSSComponent()} directly from 
 * SwingSet components unless you disable/enable the component listener.
 * Best practice is to call {@code #getSSCommon().updateSSComponent()}
 */
public interface SSComponentInterface {

	// TODO Fire property changes where applicable.

//	/**
//	 * Convenience method to add both RowSet and SwingSet Component listeners.
//	 * <p>
//	 * Does not add DocumentListener.
//	 */
//	default void addListeners() {
//		addRowSetListener();
//		addSSComponentListener();
//	}

//	/**
//	 * Adds listener for Document if SwingSet component is a JTextComponent.
//	 * <p>
//	 * Implementation of addSSComponentListener() can just call this method when the
//	 * component is a JTextComponent.
//	 */
//	default void addDocumentListener() {
//		getSSCommon().addDocumentListener();
//	}

	/**
	 * Adds listener for RowSet to trigger update to SwingSet component.
	 * <p>
	 * IMPORTANT: All listeners related to binding should be added/removed
	 * using getSSCommon().
	 * 
	 * @param _component SwingSet component for which RowSet listener should be added
	 */
	static void addRowSetListener(SSComponentInterface _component) {
		_component.getSSCommon().addRowSetListener();
	}

	/**
	 * Adds listener for SwingSet component to trigger update to RowSet.
	 * <p>
	 * IMPORTANT: All listeners related to binding should be added/removed
	 * using getSSCommon().
	 * 
	 * @param _component SwingSet component for which value change listener should be added
	 */
	static void addSSComponentListener(SSComponentInterface _component) {
		_component.getSSCommon().addSSComponentListener();
	}

	/**
	 * Sets the rowSet and column name to which the component is to be bound.
	 * <p>
	 * Takes care of setting RowSet and Column Name for ssCommon and then calls
	 * bind(this.ssCommon);
	 *
	 * @param _rowSet datasource to be used.
	 * @param _boundColumnName Name of the column to which this check box should be bound
	 */
	default void bind(final RowSet _rowSet, final String _boundColumnName) {// throws java.sql.SQLException {
		getSSCommon().bind(_rowSet, _boundColumnName);
	}

	/**
	 * Override this method for notification of a change in metadata.
	 * Typically from setRowSet() or bind().
	 */
	default void metadataChange() {
	}

	/**
	 * Transfers focus to next Swing Component on the screen when Down Arrow or
	 * Enter are pressed.
	 */
	default void configureTraversalKeys() {
		
		// Forward traversal keys.
		final Set<AWTKeyStroke> forwardKeys = ((JComponent) this)
				.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		final Set<AWTKeyStroke> newForwardKeys = new HashSet<>(forwardKeys);
		if (!(this instanceof JTextArea)) {
			newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		}
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK));
		((JComponent) this).setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);

		// Backwards traversal keys.
		final Set<AWTKeyStroke> backwardKeys = ((JComponent) this)
				.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		final Set<AWTKeyStroke> newBackwardKeys = new HashSet<>(backwardKeys);
		newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_MASK));
		((JComponent) this).setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newBackwardKeys);
	}

	/**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * <p>
	 * It will actually be called from SSCommon.init() once the SSCommon data member
	 * is instantiated.
	 * <p>
	 * This method can be empty.
	 */
	void customInit();

	/**
	 * Retrieves the allowNull flag for the bound database column.
	 *
	 * @return true if bound database column can contain null values, otherwise
	 *         returns false
	 */
	default boolean getAllowNull() {
		return getSSCommon().getAllowNull();
	}

	/**
	 * Returns the index of the database column to which the SwingSet component is
	 * bound.
	 *
	 * @return returns the index of the column to which the SwingSet component is
	 *         bound
	 */
	default int getBoundColumnIndex() {
		return getSSCommon().getBoundColumnIndex();
	}

	/**
	 * Returns the JDBCType enum representing the bound database column data type.
	 * <p>
	 * Based on java.sql.JDBCType
	 *
	 * @return the enum value corresponding to the data type of the bound column
	 */
	default JDBCType getBoundColumnJDBCType() {
		return getSSCommon().getBoundColumnJDBCType();
	}

	/**
	 * Returns the database column name bound to the Swingset component
	 *
	 * @return the bound column name
	 */
	default String getBoundColumnName() {
		return getSSCommon().getBoundColumnName();
	}

	/**
	 * Returns a String representing the value in the bound database column.
	 * <p>
	 * If null, it will return an empty string.
	 *
	 * @return String containing the value in the bound database column
	 */
	default String getBoundColumnText() {
		return getSSCommon().getBoundColumnText();
	}

	/**
	 * Returns the integer code representing the bound database column data type.
	 * <p>
	 * Based on java.sql.Types
	 *
	 * @return the data type of the bound column
	 */
	default int getBoundColumnType() {
		return getSSCommon().getBoundColumnType();
	}

	/**
	 * Returns the bound column name in square brackets
	 *
	 * @return the bound column name in square brackets
	 */
	default String getColumnForLog() {
		return getSSCommon().getColumnForLog();
	}

	/**
	 * Returns the index of the database column to which the SwingSet component is
	 * bound.
	 *
	 * @return returns the index of the column to which the SwingSet component is
	 *         bound
	 *
	 * @deprecated use {@link #getBoundColumnIndex()} instead.
	 */
	@Deprecated
	default int getColumnIndex() {
		return getBoundColumnIndex();
	}

	/**
	 * Returns the database column name bound to the Swingset component
	 *
	 * @return the bound column name
	 *
	 * @deprecated use {@link #getBoundColumnName()} instead.
	 */
	@Deprecated
	default String getColumnName() {
		return getBoundColumnName();
	}

	/**
	 * Returns the integer code representing the bound database column data type.
	 * <p>
	 * Based on java.sql.Types
	 *
	 * @return the data type of the bound column
	 *
	 * @deprecated use {@link #getBoundColumnType()} instead.
	 */
	@Deprecated
	default int getColumnType() {
		return getBoundColumnType();
	}

	/**
	 * Returns the ssCommon data member of the Swingset component.
	 * <p>
	 * A typical implementation might look like: {@code
	 * 
	 * 	return ssCommon;
	 * }
	 *
	 * @return shared/common SwingSet component data and methods
	 */
	SSCommon getSSCommon();

	/**
	 * Returns the Connection to the database.
	 *
	 * @return the connection
	 */
	default Connection getConnection() {
		return getSSCommon().getConnection();
	}

	/**
	 * Returns the RowSet containing queried data from the database.
	 *
	 * @return the rowSet
	 */
	default RowSet getRowSet() {
		return getSSCommon().getRowSet();
	}

	/**
	 * Returns the SSDataNavigator associated with this component.
	 * <p>
	 * This was added per discussion #93 to support a legacy
	 * SwingSet with the need to setup key listeners on individual
	 * components to trigger action on the SSDataNavigator buttons.
	 * <p>
	 * @deprecated Use ActionMap instead.
	 * @return the SSDataNavigator
	 */
	@Deprecated
	default SSDataNavigator getSSDataNavigator() {
		return SSDataNavigator.getSSDataNavigator(getRowSet());
	}
	
	/**
	 * Return the listener that should detect a change in value for the current
	 * component.
	 * <p>
	 * IMPORTANT: A component will have exactly one listener for component
	 * changes related to RowSet binding so this method will only be called
	 * one time in the SSCommon constructor to obtain that listener.
	 * <p>
	 * Generally the developer will need to return an instance of an inner class
	 * that implements the appropriate listener for the JComponent
	 * involved (e.g., ItemListener for a class extending JCheckBox, ChangeListener
	 * for a class extending JSlider, DocumentListener for a class extending
	 * JTextField, etc.).
	 * <p>
	 * If the component is JTextComponent then the implementation can return
	 * an instance of SSCommon.SSCommonDocumentListener().
	 * <p>
	 * A typical implementation might look like: {@code
	 * 	return new SSCheckBoxListener();
	 * }
	 * 
	 * OR (for a JTextComponent): {@code
	 * 	return getSSCommon().getSSDocumentListener();
	 * }
	 * 
	 * @return single change listener for the current SwingSet component to trigger RowSet update
	 */
	EventListener getSSComponentListener();
	
//	/**
//	 * Returns the RowSet containing queried data from the database.
//	 *
//	 * @return the rowSet
//	 *
//	 * @deprecated use {@link #getRowSet()} instead.
//	 */
//	@Deprecated
//	default RowSet getSSRowSet() {
//		return getRowSet();
//	}

//	/**
//	 * Removes listeners for bound RowSet and SwingSet component.
//	 * <p>
//	 * Does not remove DocumentListener.
//	 */
//	default void removeListeners() {
//		removeRowSetListener();
//		removeSSComponentListener();
//	}

//	/**
//	 * Removes listener for Document if SwingSet component is a JTextComponent.
//	 * <p>
//	 * Implementation of removeSSComponentListener() can just call this method when
//	 * the component is a JTextComponent.
//	 */
//	default void removeDocumentListener() {
//		getSSCommon().removeDocumentListener();
//	}
	
	/**
	 * Indicates if the components RowSet listener is added/enabled.
	 * 
	 * @param _component SwingSet component to be checked for a RowSet listener
	 *
	 * @return true if the component's RowSet listener is added/enabled, otherwise false
	 */
	static boolean isRowSetListenerAdded(SSComponentInterface _component) {
		return _component.getSSCommon().isRowSetListenerAdded();
	}

	/**
	 * Indicates if the components value change listener is currently added/enabled.
	 * <p>
	 * IMPORTANT: All listeners related to binding should be added/removed
	 * using getSSCommon().
	 * 
	 * @param _component SwingSet component to be checked for a value change listener
	 * 
	 * @return true if the component's value change listener is added/enabled, otherwise false
	 */
	static boolean isSSComponentListenerAdded(SSComponentInterface _component) {
		return _component.getSSCommon().isSSComponentListenerAdded();
	}

	/**
	 * Removes listener for RowSet to trigger update to SwingSet component.
	 * <p>
	 * IMPORTANT: All listeners related to binding should be added/removed
	 * using getSSCommon().
	 * 
	 * @param _component SwingSet component for which RowSet listener should be removed
	 */
	static void removeRowSetListener(SSComponentInterface _component) {
		_component.getSSCommon().removeRowSetListener();
	}

	/**
	 * Removes listener for SwingSet component to trigger update to RowSet.
	 * <p>
	 * IMPORTANT: All listeners related to binding should be added/removed
	 * using getSSCommon().
	 * 
	 * @param _component SwingSet component for which value change listener should be removed
	 */
	static void removeSSComponentListener(SSComponentInterface _component) {
		_component.getSSCommon().removeSSComponentListener();
	}

	/**
	 * Sets the allowNull flag for the bound database column.
	 *
	 * @param _allowNull flag to indicate if the bound database column can be null
	 */
	default void setAllowNull(final boolean _allowNull) {
		getSSCommon().setAllowNull(_allowNull);
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
	default void setBoundColumnArray(final SSArray _boundColumnArray) throws SQLException {
		getSSCommon().setBoundColumnArray(_boundColumnArray);
	}

	/**
	 * Sets the rowset column index to which the Component is to be bound.
	 *
	 * @param _boundColumnIndex rowset column index to which the Component is to be bound
	 */
	default void setBoundColumnIndex(final int _boundColumnIndex) { // throws SQLException {
		getSSCommon().setBoundColumnIndex(_boundColumnIndex);
	}

	/**
	 * Sets the database column name bound to the Swingset component
	 *
	 * @param _boundColumnName the columnName to set
	 */
	default void setBoundColumnName(final String _boundColumnName) {// throws java.sql.SQLException {
		getSSCommon().setBoundColumnName(_boundColumnName);
	}

	/**
	 * Set the text for log entries; only used if boundColumnName is null.
	 * @param _logColumnName show this in log entry if boundColumnName is null
	 */
	default void setLogColumnName(final String _logColumnName) {
		getSSCommon().setLogColumnName(_logColumnName);
	}

	/**
	 * Get the text for log entries; only used if boundColumnName is null.
	 * @return text for log entries, null if never set
	 */
	default String getLogColumnName() {
		return getSSCommon().getLogColumnName();
	}

	/**
	 * Sets the value of the bound database column
	 *
	 * @param _boundColumnText the value to set in the bound database column
	 */
	default void setBoundColumnText(final String _boundColumnText) {
		getSSCommon().setBoundColumnText(_boundColumnText);
	}

	/**
	 * Sets the rowset column index to which the Component is to be bound.
	 *
	 * @param _columnIndex rowset column index to which the Component is to be bound
	 *
	 * @throws java.sql.SQLException - if a database access error occurs
	 *
	 * @deprecated use {@link #setBoundColumnIndex(int _boundColumnIndex)} instead.
	 */
	@Deprecated
	default void setColumnIndex(final int _columnIndex) throws SQLException {
		setBoundColumnIndex(_columnIndex);
	}

	/**
	 * Sets the database column name bound to the Swingset component
	 *
	 * @param _columnName the columnName to set
	 *
	 * @throws java.sql.SQLException - if a database access error occurs
	 *
	 * @deprecated use {@link #setBoundColumnName(String _boundColumnName)} instead.
	 */
	@Deprecated
	default void setColumnName(final String _columnName) throws SQLException {
		setBoundColumnName(_columnName);
	}
	
	/**
	 * Sets the RowSet to hold queried data from the database.
	 *
	 * @param _rowSet datasource
	 *
	 * @throws java.sql.SQLException - if a database access error occurs
	 */
	default void setRowSet(final RowSet _rowSet) throws java.sql.SQLException {
		getSSCommon().setRowSet(_rowSet);
	}

	/**
	 * Sets the Connection to the database
	 *
	 * @param _connection the connection to set
	 */
	default void setConnection(final Connection _connection) {
		getSSCommon().setConnection(_connection);
	}

//	/**
//	 * Sets the RowSet to hold queried data from the database.
//	 *
//	 * @param _rowSet datasource
//	 *
//	 * @throws java.sql.SQLException - if a database access error occurs
//	 * 
//	 * @deprecated use {@link #setRowSet(RowSet)} instead.
//	 */
//	@Deprecated
//	default void setSSRowSet(final RowSet _rowSet) throws java.sql.SQLException {
//		setRowSet(_rowSet);
//	}

	/**
	 * Updates the value of the SwingSet component based on the bound database
	 * column.
	 * <p>
	 * IMPORTANT: Do not call {@link #updateDisplay()} directly from 
	 * SwingSet components unless you disable/enable the component listener.
	 * Best practice is to call {@code #getSSCommon().updateSSComponent()}
	 * <p>
	 * updateSSComponent() should eliminate the need for updateDisplay()
	 *
	 * @deprecated {@link #updateSSComponent()} should generally handle display
	 *             update.
	 */
	@Deprecated
	default void updateDisplay() {
		updateSSComponent();
	}

	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText().
	 * <p>
	 * IMPORTANT. Any call to this method should be coming from SSCommon to insure
	 * removal (and subsequent restoration) of the component's value change listener.
	 */
	void updateSSComponent();

	/**
	 * Indication of whether or not the component decides its data is valid.
	 * There may be additional checks defined by a {@link Validator}; those
	 * are not considered here.
	 * @return false for error in data, otherwise true
	 */
	default boolean isDataValid() { return true; }

	/**
	 * Create and return the default {@link Decorator}
	 * used during construction.
	 * @return decorator
	 */
	default Decorator createDefaultDecorator() {
		return SSCommon.createDefaultDecorator();
	}

}
