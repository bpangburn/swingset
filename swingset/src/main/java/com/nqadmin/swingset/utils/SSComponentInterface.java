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
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.utils;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.EventListener;
import java.util.Objects;
import java.util.function.Supplier;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JComponent;

import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.decorators.Decorator;
import com.nqadmin.swingset.decorators.Validator;
import com.nqadmin.swingset.formatting.SSFormat;
import com.nqadmin.swingset.formatting.SSFormattedTextField;
import com.nqadmin.swingset.navigate.RowSetModificationEvent;
import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.navigate.UndoRedo;

import static com.nqadmin.swingset.utils.SSUtils.findRowsModel;

/**
 * Interface with default methods shared by most SwingSet components.
 * This interface acts like a class to simulate multiple inheritance.
 * There are only a few methods, and the class Hook, that need to be implemented.
 */
public interface SSComponentInterface extends RSC
{
	/** Initialize component to an empty or default value.
	 * Action could be conditioned on getAllowNull() or whatever.
	 */
	void cleanField();

	/**
	 * Return the {@linkplain Hook} used by SSCommon.
	 * <p>
	 * <b>Generally should not be used except by SSCommon</b>.
	 * @return Hook
	 */
	Hook getSSComponentHook();
	
	/**
	 * An SSComponent must create a Hook.
	 * <p>
	 * <b>Generally should not be used except by SSCommon</b>.
	 */
	abstract class Hook
	{
		private final SSComponentInterface ssComponent;

		/**
		 * Create.
		 * @param ssComponent
		 */
		protected Hook(SSComponentInterface ssComponent)
		{
			this.ssComponent = ssComponent;
		}

		/**
		 * Updates the value stored and displayed in the SwingSet component
		 * based on the object obtained from getBoundColumnText().
		 * Use {@code ssComponent.getSSCommon().updateSSComponent()}.
		 * <p>
		 * This method is invoked by SSCommon and insures removal (and subsequent
		 * restoration) of the component's listener.
		 */
		protected abstract void updateSSComponent();

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
		protected abstract EventListener getSSComponentListener();

		/**
		 * Method to add SSComponent listener. DO NOT CALL THIS METHOD DIRECTLY;
		 * use {@code ssComponent.getSSCommon().addSSComponentListener()}.
		 * <p>
		 * IMPORTANT: All listeners related to binding should be added/removed
		 * using getSSCommon().
		 * 
		 * @param eventListener 
		 */
		protected abstract void addSSComponentListener(EventListener eventListener);

		/**
		 * Method to remove SSComponent listener. DO NOT CALL THIS METHOD DIRECTLY;
		 * use {@code ssComponent.getSSCommon().removeSSComponentListener()}.
		 * <p>
		 * IMPORTANT: All listeners related to binding should be added/removed
		 * using getSSCommon().
		 * 
		 * @param eventListener
		 */
		protected abstract void removeSSComponentListener(EventListener eventListener);

		private SSCommon ssCommon;
		/**
		 * Returns ssCommon for the current Swingset component.
		 *
		 * @return common SwingSet component data and methods
		 */
		final SSCommon getSSCommon() {
			if (ssCommon == null)
				return ssCommon = SSCommon.createStart(ssComponent);
			return ssCommon;
		}

		/**
		 * This should be invoked as the last statement
		 * in the SSComponent's constructor, but before bind.
		 */
		protected final void finishSSCommon() {
			ssCommon = SSCommon.createFinish(ssComponent, ssCommon);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// The methods beyond this point have default implementations.
	// Typically, they are convenience methods that bounce to SSCommon
	// and they should NOT be overridden.
	//

	/**
	 * Returns the SSCommon associated with this Swingset component.
	 *
	 * @return common SwingSet component data and methods
	 */
	@SuppressWarnings("NonPublicExported")
	default SSCommon getSSCommon() {
		return getSSComponentHook().getSSCommon();
	}

	/**
	 * This should be invoked as the last statement
	 * in the SSComponent's constructor, but before bind.
	 */
	default void finishSSCommon() {
		getSSComponentHook().finishSSCommon();
	}

	/**
	 * Used when making a change to the database.
	 * Typically used by a component listener. It avoids extra RowSet events.
	 * May bring up a dialog if there is no row to change.
	 * @param r code that changes the database
	 */
	default void dbChange(Runnable r)
	{
			getSSCommon().dbChange(r);
	}


	/** Invoked during bind, component should verify that jdbcType is ok.
	 * @param jdbcType column JDBCType
	 * @throws AssertionError if can't handle JDBCType
	 */
	// TODO: checkColumnType use a different exception?
	default void checkColumnType(JDBCType jdbcType) throws IllegalArgumentException
	{
	}

	/**
	 * Sets the rowSet and column name to which the component is to be bound.
	 * <p>
	 * Takes care of setting RowSet and Column Name for ssCommon and then calls
	 * bind(this.ssCommon);
	 *
	 * @param rowsModel holds RowSet to be used.
	 * @param boundColumnName Name of the column to which this check box should be bound
	 */
	default void bind(RowsModel rowsModel, String boundColumnName)
	{
		Objects.requireNonNull(rowsModel);
		RowSet rs = rowsModel.getRowSet();
		try {
			checkColumnType(RowSetOps.getJDBCColumnType(rs, boundColumnName));
		} catch (SQLException ex) {
			throw new IllegalArgumentException("SQLException getting column type", ex);
		}
		getSSCommon().bind(rowsModel, boundColumnName);
		// Primary keys for SyncResolver, joins
		if (rs instanceof CachedRowSet)
			SSUtils.setupDefaultPrimaryKeys(this);
	}

	/**
	 * Transition support.
	 * @param rowSet
	 * @param boundColumnName
	 * @deprecated use RowsModel not RowSet
	 */
	@Deprecated
	default void bind(RowSet rowSet, String boundColumnName)
	{
		bind(findRowsModel(rowSet), boundColumnName);
	}

	/**
	 * Override this method for notification of a change in metadata.
	 * Typically from setRowSet() or bind().
	 */
	default void metadataChange() {
	}

	/**
	 * Setup additional focus transfer keys.
	 */
	default void configureTraversalKeys()
	{
		SSCommon.configureTraversalKeys((JComponent)this);
	}

	/**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * <p>
	 * It will actually be called from SSCommon.init() once the SSCommon data member
	 * is instantiated.
	 */
	default void customInit() {}

	/**
	 * Retrieves the allowNull flag for the bound database column.
	 *
	 * @return true if bound database column can contain null values, otherwise
	 *         returns false
	 */
	@Override
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
	@Override
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
	@Override
	default JDBCType getBoundColumnJDBCType() {
		return getSSCommon().getBoundColumnJDBCType();
	}

	/**
	 * Returns the database column name bound to the Swingset component
	 *
	 * @return the bound column name
	 */
	@Override
	default String getBoundColumnName() {
		return getSSCommon().getBoundColumnName();
	}

	/**
	 * Returns an Object 
	 * representing the value in the bound database column.
	 * <p>
	 * Note a null is never converted into ""; use getBoundColumnText for that.
	 * @return value
	 */
	// TODO: put this in RSC?
	default Object getBoundColumnObject() {
		return getSSCommon().getBoundColumnObject();
	}

	/** {@inheritDoc } */
	@Override
	default <T> T getBoundColumnObject(Class<T> type) {
		return getSSCommon().getBoundColumnObject(type);
	}

	/**
	 * Returns a String representing the value in the bound database column.
	 * <p>
	 * If null, it will return an empty string.
	 *
	 * @return String containing the value in the bound database column
	 */
	@Override
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
	// TODO: deprecate
	default int getBoundColumnType() {
		return getSSCommon().getBoundColumnType();
	}

	/**
	 * Returns the bound column name in square brackets
	 *
	 * @return the bound column name in square brackets
	 */
	@Override
	default String getColumnForLog() {
		return getSSCommon().getColumnForLog();
	}

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
	@Override
	default RowSet getRowSet() {
		return getSSCommon().getRowSet();
	}

	/**
	 * Returns the RowSet containing queried data from the database.
	 *
	 * @return the rowSet
	 */
	@Override
	default RowsModel getRowsModel() {
		return getSSCommon().getRowsModel();
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
	default void setBoundColumnIndex(final int _boundColumnIndex) {
		getSSCommon().setBoundColumnIndex(_boundColumnIndex);
	}

	/**
	 * Sets the database column name bound to the Swingset component
	 *
	 * @param _boundColumnName the columnName to set
	 */
	default void setBoundColumnName(final String _boundColumnName) {
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
	 * @param _boundColumnObject the value to set in the bound database column
	 */
	default void setBoundColumnObject(final Object _boundColumnObject) {
		getSSCommon().setBoundColumnObject(_boundColumnObject);
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
	 * Sets the Connection to the database
	 *
	 * @param _connection the connection to set
	 */
	default void setConnection(final Connection _connection) {
		getSSCommon().setConnection(_connection);
	}

	/**
	 * Determine if there's a row that can be modified; dialog if not.
	 * Typically used in an SSComponent's listener.
	 * @return true if there's a row
	 */
	default boolean checkRowOK() {
		return getSSCommon().checkRowOK();
	}

	/**
	 * Determine if there's a row that can be modified; optionally dialog if not.
	 * If there's no row, only dialog if dialogOK is true.
	 * A nested check never does the dialog.
	 * Typically used in an SSComponent's listener.
	 *
	 * @param dialogOK if null or evaluates true then dialog if no row.
	 * @return true if there's a row
	 */
	default boolean checkRowOK(Supplier<Boolean> dialogOK) {
		return getSSCommon().checkRowOK(dialogOK);
	}

	/**
	 * Setup action bindings for undo/redo.
	 */
	default void setupUndoRedoKeys() {
		SSCommon.setupUndoRedoKeys(this);
	}

	/**
	 * Add a change to this components undo/redo stack.
	 * @param ev modification event
	 * @throws java.sql.SQLException
	 */
	default void addUndoableChange(RowSetModificationEvent ev) throws SQLException {
		UndoRedo.addUndoableChange(ev);
	}

	/**
	 * Set the component to the new value.
	 * <p>
	 * WARNING: do not override unless ...
	 * @param cmd undo or redo
	 * @param value new value
	 * @throws java.sql.SQLException
	 */
	default void undoRedoUpdateObject(UndoRedo cmd, Object value) throws SQLException
	{
		getSSCommon().undoRedoUpdateObject(cmd, value);
	}

	/**
	 * A component may have a display/parse format.Especially used in
	 * conjunction with, but not limited to, {@linkplain SSFormattedTextField}.
	 * @param format format for this component
	 */
	default void setSSFormat(SSFormat format) { getSSCommon().setSSFormat(format); }

	/**
	 * {@inheritDoc }
	 */
	@Override
	default SSFormat getSSFormat() { return getSSCommon().getSSFormat(); }

	// There are three levels of componenent validation.
	// 1 - baseValidate is inherit to the component structure,
	//     for example a mask formatters valid indicator.
	//     This "validation" might simply return state indicator.
	//     Defaults true.
	// 2 - componentValidate
	//     TODO: this is fuzzy...
	//     Defaults true.
	// 3 - pluginValidate
	//     Application specific validation for a component instance.

	/**
	 * Install the given validator into the component;
	 * this is the pluginValidator.
	 * @param validator validator to install
	 */
	default void setValidator(Validator validator) {
		getSSCommon().setValidator(validator);
	}

	/**
	 * A low level indication of whether or not the component data is valid.
	 * For example, a mask formatter indicates valid; generally simple
	 * constraints that are context independent; e.g. {@literal month <= 12}.
	 * There may be additional checks defined by {@link #componentValidate() }
	 * and/or a {@link Validator}; those are not considered here.
	 * This might be determined by stringToValue.
	 * @return false for error in data, otherwise true
	 */
	default boolean baseValidate() { return true; }

	/**
	 * This has component specific validation, for example for a SSDateField.
	 * @return true if successful validation for the type of component
	 */
	default boolean componentValidate() { return true; }

	/**
	 * The results of doing SSComponent validations.
	 * 
	 * @param base result of baseValidate()
	 * @param comp result of base and componentValidate()
	 * @param all result of comp and pluginValidate()
	 */
	record validateResult(boolean base, boolean comp, boolean all){}

	/**
	 * Run the validators: baseValidate, componentValidate, pluginValidate.
	 * The checks are done in order, they stop with any failure.
	 * @return result
	 */
	default validateResult allValidate() {
		boolean baseValid = baseValidate();
		boolean compValid = baseValid && componentValidate();
		boolean allValid = compValid && getSSCommon().pluginValidate();
		return new validateResult(baseValid, compValid, allValid);
	}

	/**
	 * Create and return the default {@link Decorator}
	 * used during construction.
	 * @return decorator
	 */
	default Decorator createDefaultDecorator() {
		return SSCommon.createDefaultDecorator();
	}

	/**
	 * Install the given decorator.
	 * @param deco decorator to install
	 */
	default void setDecorator(Decorator deco) {
		getSSCommon().setDecorator(deco);
	}

	/**
	 * Return the decorator used by this component.
	 * @return the decorator
	 */
	default Decorator getDecorator() {
		return getSSCommon().getDecorator();
	}

	/**
	 * Run the decorator.
	 * @return true if component data valid
	 */
	default boolean decorate() {
		return getSSCommon().decorate();
	}

	/**
	 * Report problem accessing image file to user.
	 * @param title dialog title
	 * @param path file path
	 * @param ex error
	 */
	default void reportError(String title, Path path, Exception ex) {
		getSSCommon().reportError(title, path, ex);
	}

}
