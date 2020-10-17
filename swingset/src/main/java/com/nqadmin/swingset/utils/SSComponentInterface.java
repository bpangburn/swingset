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

import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.apache.logging.log4j.LogManager;

import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSRowSet;

// SSComponentInterface.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Interface with default methods shared by most SwingSet components.
 * <p>
 * Developer needs to insure that each implementation has an SSCommon data
 * member that is instantiated in the Component's constructor.
 */
public interface SSComponentInterface {

	// TODO Fire property changes where applicable.

	/**
	 * Convenience method to add both RowSet and SwingSet Component listeners.
	 */
	default void addListeners() {
		addSSRowSetListener();
		addSSComponentListener();
	}

	/**
	 * Add a listener to detect a change in value for the current component.
	 * SSCommon will manage any bound RowSet listeners.
	 * <p>
	 * Generally the developer will need to add an inner class and corresponding
	 * data member that implements the appropriate listener for the JComponent
	 * involved (e.g., ItemListener for a class extending JCheckBox, ChangeListener
	 * for a class extending JSlider, DocumentListener for a class extending
	 * JTextField, etc.).
	 * <p>
	 * If the component is JTextComponent then the implementation can simply call
	 * addSSDocumentListener()
	 */
	void addSSComponentListener();

	/**
	 * Adds listener for Document if SwingSet component is a JTextComponent.
	 * <p>
	 * Implementation of addSSComponentListener() can just call this method when the
	 * component is a JTextComponent.
	 */
	default void addSSDocumentListener() {
		getSSCommon().addSSDocumentListener();
	}

	/**
	 * Adds listener for RowSet.
	 */
	default void addSSRowSetListener() {
		getSSCommon().addSSRowSetListener();
	}

	/**
	 * Sets (or resets) the binding from the component to a database column
	 * <p>
	 * Assumes that RowSet and Column Name are already set properly
	 */
	default void bind() {// throws java.sql.SQLException {

		getSSCommon().bind();

	}

	/**
	 * Sets the SSRowSet and column name to which the component is to be bound.
	 * <p>
	 * Takes care of setting RowSet and Column Name for ssCommon and then calls
	 * bind(this.ssCommon);
	 *
	 * @param _ssRowSet        datasource to be used.
	 * @param _boundColumnName Name of the column to which this check box should be
	 *                         bound
	 */
	default void bind(final SSRowSet _ssRowSet, final String _boundColumnName) {// throws java.sql.SQLException {

		getSSCommon().bind(_ssRowSet, _boundColumnName);

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
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
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
	 *
	 * @return shared/common SwingSet component data and methods
	 */
	SSCommon getSSCommon();

	/**
	 * Returns the SSConnection to the database
	 *
	 * @return the ssConnection
	 */
	default SSConnection getSSConnection() {
		return getSSCommon().getSSConnection();
	}

	/**
	 * Returns the RowSet containing queried data from the database.
	 *
	 * @return the ssRowSet
	 */
	default SSRowSet getSSRowSet() {
		return getSSCommon().getSSRowSet();
	}

	/**
	 * Removes listeners for bound RowSet and SwingSet component.
	 */
	default void removeListeners() {
		removeSSRowSetListener();
		removeSSComponentListener();
	}

	/**
	 * Remove the listener detecting changes in value for the current component.
	 * SSCommon will manage any bound RowSet listeners.
	 * <p>
	 * Generally the developer will need to add an inner class and corresponding
	 * data member that implements the appropriate listener for the JComponent
	 * involved (e.g., ItemListener for a class extending JCheckBox, ChangeListener
	 * for a class extending JSlider, DocumentListener for a class extending
	 * JTextField, etc.).
	 * <p>
	 * If the component is JTextComponent then the implementation can simply call
	 * removeSSDocumentListener()
	 */
	void removeSSComponentListener();

	/**
	 * Removes listener for Document if SwingSet component is a JTextComponent.
	 * <p>
	 * Implementation of removeSSComponentListener() can just call this method when
	 * the component is a JTextComponent.
	 */
	default void removeSSDocumentListener() {
		getSSCommon().removeSSDocumentListener();
	};

	/**
	 * Removes listener for RowSet.
	 */
	default void removeSSRowSetListener() {
		getSSCommon().removeSSRowSetListener();
	};

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
	 * Sets the value of the bound database column
	 *
	 * @param _boundColumnText the value to set in the bound database column
	 */
	default void setBoundColumnText(final String _boundColumnText) {

		getSSCommon().setBoundColumnText(_boundColumnText);

		LogManager.getLogger().debug(getColumnForLog() + ": " + _boundColumnText);

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
	 * Sets the SSCommon data member of the Swingset Component.
	 *
	 * @param _ssCommon shared/common SwingSet component data and methods
	 */
	void setSSCommon(SSCommon _ssCommon);

	/**
	 * Sets the SSConnection to the database
	 *
	 * @param _ssConnection the ssConnection to set
	 */
	default void setSSConnection(final SSConnection _ssConnection) {
		getSSCommon().setSSConnection(_ssConnection);
	}

	/**
	 * Sets the RowSet to hold queried data from the database.
	 *
	 * @param _ssRowSet the ssRowSet to set
	 *
	 * @throws java.sql.SQLException - if a database access error occurs
	 */
	default void setSSRowSet(final SSRowSet _ssRowSet) throws java.sql.SQLException {
		getSSCommon().setSSRowSet(_ssRowSet);
	}

	/**
	 * Updates the value of the SwingSet component based on the bound database
	 * column.
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
	 * getBoundColumnText()
	 * <p>
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed
	 */
	void updateSSComponent();

}
