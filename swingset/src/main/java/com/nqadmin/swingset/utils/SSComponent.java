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
 * copyright (C) 2024-2026, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.utils;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.EventListener;
import java.util.function.Supplier;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JComponent;

import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.datasources.SSDBSupport.DbReader;
import com.nqadmin.swingset.datasources.SSDBSupport.DbRunnable;
import com.nqadmin.swingset.datasources.SSDBSupport.DbWriter;
import com.nqadmin.swingset.decorators.Decorator;
import com.nqadmin.swingset.decorators.Validator;
import com.nqadmin.swingset.formatting.SSFormat;
import com.nqadmin.swingset.formatting.SSFormattedTextField;
import com.nqadmin.swingset.navigate.RowSetModificationEvent;
import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.navigate.UndoRedo;
import com.nqadmin.swingset.navigate.UndoRedo.Change;

import static com.nqadmin.swingset.utils.SSUtils.findRowsModel;

/**
 * Interface with default methods shared by most SwingSet components.
 * This interface acts like a class to simulate multiple inheritance.
 * There are only a few methods, and the class Hook, that need to be implemented.
 */
public interface SSComponent extends RSC
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
		private final SSComponent ssComponent;

		/**
		 * Create.
		 * @param ssComponent
		 */
		protected Hook(SSComponent ssComponent)
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
				ssCommon = SSCommon.createStart(ssComponent);
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
	//
	// The first group are commonly overriden.
	//

	/**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * <p>
	 * It will actually be called from SSCommon.init() once the SSCommon data member
	 * is instantiated.
	 */
	default void customInit() {}

	/** Invoked during bind, component should verify that jdbcType is ok.
	 * @param jdbcType column JDBCType
	 * @throws IllegalArgumentException if can't handle JDBCType
	 */
	default void checkColumnType(JDBCType jdbcType) throws IllegalArgumentException { }

	/**
	 * Override this method for notification of a change in metadata.
	 * Typically from setRowSet() or bind().
	 */
	default void metadataChange() { }

	/**
	 * Invoked at the end of bind; default sets up primary keys for CachedRowSet.
	 * Invoke super if override.
	 */
	// TODO: currently called once per column, only needed once per RowSet?
	default void finishBind()
	{
		// Primary keys for SyncResolver, joins
		if (getRowSet() instanceof CachedRowSet)
			SSUtils.setupDefaultPrimaryKeys(this);
	}


	////////////////////////////////////////////////////////////////////////////
	//
	// The rest of these methods are convenience methods that bounce
	// to SSCommon and typically are not overridden.
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
	 * @throws java.sql.SQLException
	 */
	default void dbChange(DbRunnable r) throws SQLException
	{
			getSSCommon().dbChange(r);
	}
	
	/**
	 * Sets the RowsModel and column name to which the component is to be bound.
	 * <p>
	 * Takes care of setting RowSet and Column Name for ssCommon and then calls
	 * bind(this.ssCommon);
	 *
	 * @param rowsModel holds RowSet to be used.
	 * @param boundColumnName Name of the column to which this check box should be bound
	 * 
	 * @deprecated Use rowsModel.bind(sscomp, boundColumnName)
	 */
	@Deprecated
	default void bind(RowsModel rowsModel, String boundColumnName)
	{
		getSSCommon().bind(rowsModel, boundColumnName);
	}

	/**
	 * Indicate whether or not the Component has been bound to a RowSet.
	 * A fully bound component has information based on RowSet's metadata,
	 * for example jdbc column type and isNullable.
	 * @return true if fullyBound
	 */
	default boolean isFullyBound()
	{
		return getSSCommon().isFullyBound();
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
	 * Setup additional focus transfer keys.
	 */
	default void configureTraversalKeys()
	{
		SSCommon.configureTraversalKeys((JComponent)this);
	}

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
	 * @param allowNull flag to indicate if the bound database column can be null
	 */
	default void setAllowNull(final boolean allowNull) {
		getSSCommon().setAllowNull(allowNull);
	}

	/**
	 * Updates the bound database column with the specified Array.
	 * <p>
	 * Used for SSList or other component where multiple items can be selected.
	 *
	 * @param boundColumnArray Array to write to bound database column
	 * @throws SQLException thrown if there is a problem writing the array to the
	 *                      RowSet
	 */
	default void setBoundColumnArray(final SSArray boundColumnArray) throws SQLException {
		getSSCommon().setBoundColumnArray(boundColumnArray);
	}

	/**
	 * Sets the database column name bound to the Swingset component
	 *
	 * @param boundColumnName the columnName to set
	 * @deprecated Use bind()
	 */
	@Deprecated
	default void setBoundColumnName(final String boundColumnName) {
		getSSCommon().setBoundColumnName(boundColumnName);
	}

	/**
	 * Set the text for log entries; only used if boundColumnName is null.
	 * @param logColumnName show this in log entry if boundColumnName is null
	 */
	default void setLogColumnName(final String logColumnName) {
		getSSCommon().setLogColumnName(logColumnName);
	}

	/**
	 * Get the text for log entries; only used if boundColumnName is null.
	 * @return text for log entries, null if never set
	 */
	default String getLogColumnName() {
		return getSSCommon().getLogColumnName();
	}

	/**
	 * Get the columnReader used by {@link #getColumn()} and internally
	 * for capturing initial value.
	 * This is useful for dealing with ColumnTypes that are are not
	 * handled internally, like BLOB and VARBINARY. For exampe, see Image source code.
	 * 
	 * The {@code columnReader} is typically invoked like
	 * {@code .apply(comp.getRowSet(), comp.getBoundColumnIndex(), comp)}.
	 * The comp is rarely used, and provided for complex situations. The
	 * columnReader return the value fetched from the datbase.
	 * 
	 * @return the DbReader used to fetch values from the database
	 */
	default DbReader<RowSet, Integer, SSComponent, ?> getColumnReader() {
		return getSSCommon().getColumnReader();
	}

	/**
	 * Set the columnReader used by {@link #getColumn()} and internally for capturing
	 * initial value. This is useful for dealing with ColumnTypes that are are not
	 * handled internally, like BLOB and VARBINARY. For exampe, see Image source code.
	 * 
	 * The {@code columnReader} is typically invoked like
	 * {@code .apply(comp.getRowSet(), comp.getBoundColumnIndex(), comp)}.
	 * The comp is rarely used, and provided for complex situations.
	 * 
	 * @param columnReader the DbReader used to fetch values from the database
	 */
	default void setColumnReader(DbReader<RowSet, Integer, SSComponent, ?> columnReader) {
		getSSCommon().setColumnReader(columnReader);
	}

	/**
	 * Get the columnWriter used by {@link #setColumn(Object)}.
	 * This is useful for dealing with ColumnTypes that are are not
	 * handled internally, like BLOB and VARBINARY. For exampe, see Image source code.
	 * 
	 * The {@code columnReader} is typically invoked like
	 * {@code .apply(comp.getRowSet(), comp.getBoundColumnIndex(), comp)}.
	 * The comp is rarely used, and provided for complex situations.
	 * 
	 * @return the DbWriter used to fetch values from the database
	 */
	default DbWriter<RowSet, Integer, SSComponent, Object> getColumnWriter() {
		return getSSCommon().getColumnWriter();
	}


	/**
	 * Set the columnWriter used by {@link #setColumn(Object)}.
	 * This is useful for dealing with ColumnTypes that are are not
	 * handled internally, like BLOB and VARBINARY. For exampe, see Image source code.
	 * 
	 * The {@code columnReader} is typically invoked like
	 * {@code .apply(comp.getRowSet(), comp.getBoundColumnIndex(), comp)}.
	 * The comp is rarely used, and provided for complex situations.
	 * 
	 * @param columnWriter the DbWriter used to fetch values from the database
	 */
	default void setColumnWriter(DbWriter<RowSet,Integer,SSComponent,Object> columnWriter) {
		getSSCommon().setColumnWriter(columnWriter);
	}

	/**
	 * Sets the value of the bound database column using the SSComponent's
	 * {@link DbWriter}. See {@link #getColumnWriter() }.
	 * NPE if no columnReader. Useful for dealing with JDBCTypes not handled
	 * internally.
	 * 
	 * @return the value to display in the SSComponent, may be from undo/redo stack.
	 * @throws java.sql.SQLException
	 */
	default Object getColumn() throws SQLException {
		return getSSCommon().getColumn();
	}

	/**
	 * Sets the value of the bound database column using the SSComponent's
	 * {@link DbWriter}. See {@link #getColumnWriter() }.
	 * NPE if no columnWriter. Useful for dealing with JDBCTypes not handled
	 * internally.
	 * 
	 * @param value to write to the database, may write to the undo/redo stack.
	 */
	default void setColumn(Object value) {
		getSSCommon().setColumn(value);
	}

	/**
	 * Sets the value of the bound database column
	 *
	 * @param boundColumnObject the value to set in the bound database column
	 */
	default void setBoundColumnObject(final Object boundColumnObject) {
		getSSCommon().setBoundColumnObject(boundColumnObject);
	}

	/**
	 * Sets the value of the bound database column
	 *
	 * @param boundColumnText the value to set in the bound database column
	 */
	default void setBoundColumnText(final String boundColumnText) {
		getSSCommon().setBoundColumnText(boundColumnText);
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
		getSSCommon().addUndoableChange(ev);
	}

	/**
	 * Set the component to the new value.
	 * <p>
	 * WARNING: do not override unless ...
	 * @param cmd undo or redo
	 * @param change new value
	 * @throws java.sql.SQLException
	 */
	default void undoRedoUpdateObject(UndoRedo cmd, Change change) throws SQLException
	{
		getSSCommon().undoRedoUpdateObject(cmd, change);
	}

	/**
	 * A component may have a display/parse format. Especially used in
	 * conjunction with, but not limited to, {@linkplain SSFormattedTextField}.
	 * @param format format for this component
	 */
	default void setSSFormat(SSFormat format) { getSSCommon().setSSFormat(format); }

	/**
	 * {@inheritDoc }
	 */
	@Override
	default SSFormat getSSFormat() { return getSSCommon().getSSFormat(); }

	////////////////////////////////////////////////////////////////////////////
	//
	// Validation/Decoration
	//

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
	record validateResult(boolean base, boolean comp, boolean other, boolean all){}

	/**
	 * Run the validators: baseValidate, componentValidate, pluginValidate.
	 * The checks are done in order, they stop with any failure.
	 * @return result
	 */
	default validateResult allValidate() {
		boolean baseValid = baseValidate();
		boolean compValid = baseValid && componentValidate();
		boolean otherValid = compValid;
		if (compValid && !getSSCommon().pendingDbChange) {
			RowsModel rowsModel = getRowsModel();
			if (rowsModel != null && rowsModel.getRowSet() != null)
				otherValid = !rowsModel.hasError(this);
		}
		boolean allValid = otherValid && getSSCommon().pluginValidate();
		return new validateResult(baseValid, compValid, otherValid, allValid);
	}

	/**
	 * @return true if this component is different from what's in the database
	 */
	default boolean isDirty() {
		return getRowsModel() != null && getRowsModel().isDirty(this);
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
}
