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

import java.sql.Array;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.EventListener;
import java.util.function.Supplier;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JComponent;

import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.datasources.SSDBSupport.DbReader;
import com.nqadmin.swingset.datasources.SSDBSupport.DbWriter;
import com.nqadmin.swingset.datasources.SSDBSupport.RunnableSQL;
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
 * This Interface presents a {@link RowSet} column as seen by the visual
 * components in the library. It has default methods supporting binding
 * a {@linkplain RowSet} column to this component, dbms access,
 * undo/redo, validation, decoration. A database column is associated
 * with this component via {@link RowsModel#bind(SSComponent,String) }.
 * There are only a few methods, and the class Hook, that need to be implemented
 * by the visual component: {@link #cleanField() }, {@link #getSSComponentHook() }.
 * <p>
 * There are several methods that can be overridden for
 * customization, notification, and verification: {@link #customInit() },
 * {@link #checkColumnType(JDBCType) }, {@link #metadataChange() },
 * {@link #finishBind() }, {@link #baseValidate() }, {@link #componentValidate()},
 * and {@link #createDefaultDecorator()}.
 * <p>
 * Typical usage: {@code class MyComponent extends SomeJComponent implements SScomponent}
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
		 * based on value obtained with getColumn*().
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
		 * Invoked by ssComponent.finishSSCommon which
		 * should be invoked as the last statement
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
	// There are also some validation/decoration related methods near the end
	// of this file that are commonly overridden:
	//			baseValidate, componentValidate, createDefaultDecorator.
	//

	/**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * <p>
	 * It will actually be called from SSCommon.init() once the SSCommon data member
	 * is instantiated.
	 */
	default void customInit() {}

	/** Invoked during bind, component should verify that the database column's
	 * JDBCType is ok and handled by this component.
	 * 
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
	 * Invoked by the infrastructure at the end of bind;
	 * default sets up primary keys for CachedRowSet.
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
	 * Sets the RowsModel and column name to which the component is to be bound.
	 * <p>
	 * Takes care of setting RowSet and Column Name for ssCommon and then calls
	 * bind(this.ssCommon);
	 *
	 * @param rowsModel holds RowSet to be used.
	 * @param columnName Name of the column to which this check box should be bound
	 * 
	 * @deprecated Use rowsModel.bind(sscomp, columnName)
	 */
	@Deprecated
	default void bind(RowsModel rowsModel, String columnName)
	{
		getSSCommon().bind(rowsModel, columnName);
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
	 * @param columnName
	 * @deprecated use RowsModel not RowSet
	 */
	@Deprecated
	default void bind(RowSet rowSet, String columnName)
	{
		bind(findRowsModel(rowSet), columnName);
	}

	/**
	 * Setup additional focus transfer keys.
	 * Typically invoke super if override.
	 */
	default void configureTraversalKeys()
	{
		SSCommon.configureTraversalKeys((JComponent)this);
	}

	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	//
	// Read/Write column data
	//

	/**
	 * Returns a String representation of the value in the bound database column.
	 * <p>
	 * If null, it will return an empty string.
	 *
	 * @return the database value as String to display in the SSComponent, may be from undo/redo stack.
	 */
	@Override
	default String getColumnText() {
		return getSSCommon().getColumnText();
	}

	/**
	 * Returns the Object for the bound database column
	 * as returned by {@link RowSet#getObject(int) }.
	 * <p>
	 * Note a null is never converted into ""; use getColumnText for that.
	 * @return the value to display in the SSComponent, may be from undo/redo stack.
	 */
	// TODO: put this in RSC?
	default Object getColumnObject() {
		return getSSCommon().getColumnObject();
	}

	/** {@inheritDoc } */
	@Override
	default <T> T getColumnObject(Class<T> clazz) {
		return getSSCommon().getColumnObject(clazz);
	}

	/**
	 * Returns the Array for the bound database column
	 * as returned by {@link RowSet#getArray(int) }.
	 * <p>
	 * @return the value to display in the SSComponent, may be from undo/redo stack.
	 */
	default Array getColumnArray() {
		return getSSCommon().getColumnArray();
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
	 * Get the columnReader used by {@link #getColumn()} and internally,
	 * when not null, for capturing initial value for undo/redo.
	 * This is useful for dealing with ColumnTypes that are are not
	 * handled internally, like BLOB and VARBINARY.
	 * For exampe, see {@link com.nqadmin.swingset.core.Image} source code.
	 * 
	 * The {@code columnReader} is typically invoked like
	 * {@code .apply(comp.getRowSet(), comp.getColumnIndex(), comp)}.
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
	 * handled internally, like BLOB and VARBINARY.
	 * For exampe, see {@link com.nqadmin.swingset.core.Image} source code.
	 * 
	 * The {@code columnReader} is typically invoked like
	 * {@code .apply(comp.getRowSet(), comp.getColumnIndex(), comp)}.
	 * The comp is rarely used, and provided for complex situations.
	 * 
	 * @param columnReader the DbReader used to fetch values from the database
	 */
	default void setColumnReader(DbReader<RowSet, Integer, SSComponent, ?> columnReader) {
		getSSCommon().setColumnReader(columnReader);
	}

	/**
	 * Used when making a change to the database.
	 * Typically used by a component listener. It avoids extra RowSet events.
	 * May bring up a dialog if there is no row to change.
	 * @param r code that changes the database
	 * @throws java.sql.SQLException
	 */
	default void dbChange(RunnableSQL r) throws SQLException
	{
			getSSCommon().dbChange(r);
	}

	/**
	 * Updates the value of the bound database column;
	 * method used by SwingSet component listeners to update the underlying RowSet.
	 * The real action, like null handling and conversion checking, happens
	 * in {@link RowSetOps#updateColumnText(com.nqadmin.swingset.utils.SSComponent, java.lang.String) }.
	 * Does not commit the update row.
	 *
	 * @param columnText the value to set in the bound database column
	 * @return true if no error
	 */
	default boolean setColumnText(final String columnText) {
		return getSSCommon().setColumnText(columnText);
	}

	/**
	 * Updates the value of the bound database column;
	 * method used by SwingSet component listeners to update the underlying RowSet.
	 * Does not commit the update row.
	 *
	 * @param columnObject the value to set in the bound database column
	 * @return true if no error
	 */
	default boolean setColumnObject(final Object columnObject) {
		return getSSCommon().setColumnObject(columnObject);
	}

	/**
	 * Updates the bound database column with the specified Array.
	 * <p>
	 * Used for SSList or other component where multiple items can be selected.
	 * See {@link com.nqadmin.swingset.core.List1} and
	 * {@link com.nqadmin.swingset.models.SSCollection} for low level
	 * details on how arrays are read and written.
	 * Does not commit the update row.
	 *
	 * @param columnArray Array to write to bound database column
	 * @return true if no error
	 * @throws SQLException thrown if there is a problem writing the array to the
	 *                      RowSet
	 */
	default boolean setColumnArray(final Array columnArray) throws SQLException {
		return getSSCommon().setColumnArray(columnArray);
	}

	/**
	 * Updates the value of the bound database column;
	 * method used by SwingSet component listeners to update the underlying RowSet.
	 * Sets the value of the bound database column using the SSComponent's
	 * {@link DbWriter}. See {@link #getColumnWriter() }.
	 * NPE if no columnWriter. Useful for dealing with JDBCTypes not handled
	 * internally.
	 * Does not commit the update row.
	 * 
	 * @param value to write to the database, may write to the undo/redo stack.
	 * @return true if no error
	 */
	default boolean setColumn(Object value) {
		return getSSCommon().setColumn(value);
	}

	/**
	 * Get the columnWriter used by {@link #setColumn(Object)}.
	 * This is useful for dealing with ColumnTypes that are are not
	 * handled internally, like BLOB and VARBINARY.
	 * For exampe, see {@link com.nqadmin.swingset.core.Image} source code.
	 * 
	 * The {@code columnWriter} is typically invoked like
	 * {@code .apply(comp.getRowSet(), comp.getColumnIndex(), comp, value)}.
	 * The comp is rarely used, and provided for complex situations.
	 * 
	 * @return the DbWriter used to update to the database
	 */
	default DbWriter<RowSet, Integer, SSComponent, Object> getColumnWriter() {
		return getSSCommon().getColumnWriter();
	}

	/**
	 * Set the columnWriter used by {@link #setColumn(Object)}.
	 * This is useful for dealing with ColumnTypes that are are not
	 * handled internally, like BLOB and VARBINARY.
	 * For exampe, see {@link com.nqadmin.swingset.core.Image} source code.
	 * 
	 * The {@code columnWriter} is typically invoked like
	 * {@code .apply(comp.getRowSet(), comp.getColumnIndex(), comp, value)}.
	 * The comp is rarely used, and provided for complex situations.
	 * 
	 * @param columnWriter the DbWriter used to update the database
	 */
	default void setColumnWriter(DbWriter<RowSet,Integer,SSComponent,Object> columnWriter) {
		getSSCommon().setColumnWriter(columnWriter);
	}

	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	//
	// Column information, like index/name, some derived from metadata
	//

	/**
	 * Retrieves the allowNull flag for the bound database column.
	 * Initialized from RowSet metadata. May be overridden.
	 *
	 * @return true if bound database column can contain null values, otherwise
	 *         returns false
	 */
	@Override
	default boolean getAllowNull() {
		return getSSCommon().getAllowNull();
	}

	/**
	 * Sets the allowNull flag for the bound database column.
	 * This overrides the RowSet metadata.
	 * Set to null to go back to the database metadata.
	 *
	 * @param allowNull flag to indicate if the bound database column can be null
	 */
	default void setAllowNull(final boolean allowNull) {
		getSSCommon().setAllowNull(allowNull);
	}

	/**
	 * @return true if this component's value is different from what's in the database
	 */
	default boolean isDirty() {
		return getRowsModel() != null && getRowsModel().isDirty(this);
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

	/**
	 * Returns the index of the database column to which the SwingSet component is
	 * bound.
	 *
	 * @return returns the index of the column to which the SwingSet component is
	 *         bound
	 */
	@Override
	default int getColumnIndex() {
		return getSSCommon().getColumnIndex();
	}

	/**
	 * Returns the database column name bound to the Swingset component
	 *
	 * @return the bound column name
	 */
	@Override
	default String getColumnName() {
		return getSSCommon().getColumnName();
	}

	/**
	 * Returns the JDBCType representing the bound database column data type.
	 *
	 * @return the enum value corresponding to the data type of the bound column
	 */
	@Override
	default JDBCType getColumnJDBCType() {
		return getSSCommon().getColumnJDBCType();
	}

	/**
	 * Returns the integer code representing the bound database column data type.
	 * <p>
	 * Based on java.sql.Types
	 *
	 * @return the data type of the bound column
	 * @deprecated use getBoundColumnJDBCType
	 */
	@Deprecated
	default int getBoundColumnType() {
		return getColumnJDBCType().getVendorTypeNumber();
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
	 * Get the backup text for log entries which is only used if columnName is null.
	 * 
	 * @return text for log entries, null if never set
	 */
	default String getLogColumnName() {
		return getSSCommon().getLogColumnName();
	}

	/**
	 * Set the text for log entries which is only used if columnName is null.
	 * @param logColumnName show this in log entry if columnName is null
	 */
	default void setLogColumnName(final String logColumnName) {
		getSSCommon().setLogColumnName(logColumnName);
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

	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	//
	// General state, like around the RowSet.
	//

	/**
	 * Returns the RowsModel, encapulating a RowSet, associated with this
	 * components column.
	 *
	 * @return the rowsModel
	 */
	@Override
	default RowsModel getRowsModel() {
		return getSSCommon().getRowsModel();
	}

	/**
	 * Returns the RowSet containing queried data from the database.
	 *
	 * @return the rowSet
	 */
	// TODO: deprecate in favor of RowsModel, it's widely used.
	@Override
	default RowSet getRowSet() {
		return getSSCommon().getRowSet();
	}

	/**
	 * Determine if there's a row that can be modified; dialog if not.
	 * Typically used in an SSComponent's listener.
	 * @return true if there's a row
	 */
	// TODO: move to Utils or SSUtils. Around hasActiveRow.
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

	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	//
	// Undo/Redo related
	//

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

	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
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
	 * Override this to do a low level validation of whether or not the
	 * component data is valid.
	 * For example, a mask formatter indicates valid; generally simple
	 * constraints that are context independent; e.g. {@literal month <= 12}.
	 * There may be additional checks defined by {@link #componentValidate() }
	 * and/or a {@link Validator}, see
	 * {@link #setValidator(Validator) }; those are check after baseValidate.
	 * The default implementation returns true.
	 * 
	 * @return false for error in data, otherwise true
	 */
	 //{@link #setValidator(com.nqadmin.swingset.decorators.Validator) }; those are check after baseValidate.
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
	 * @param other this component is the target of an error event,
	 * see {@link RowsModel#hasError(SSComponent) }
	 * @param all true if everything validated
	 */
	record ValidationResult(boolean base, boolean comp, boolean other, boolean all){}

	/**
	 * Run the validators: baseValidate, componentValidate, pluginValidate;
	 * and possibly check RowsModel.hasError().
	 * The checks are done in order, they stop with any failure.
	 * @return result
	 */
	default ValidationResult allValidate() {
		boolean baseValid = baseValidate();
		boolean compValid = baseValid && componentValidate();
		boolean otherValid = compValid;
		if (compValid && !getSSCommon().pendingDbChange) {
			RowsModel rowsModel = getRowsModel();
			if (rowsModel != null && rowsModel.getRowSet() != null)
				otherValid = !rowsModel.hasError(this);
		}
		boolean allValid = otherValid && getSSCommon().pluginValidate();
		return new ValidationResult(baseValid, compValid, otherValid, allValid);
	}

	/**
	 * Create and return the default {@link Decorator}
	 * setup during construction. The default is generally good for
	 * a single {@linkplain JComponent}, for example a {@linkplain com.nqadmin.swingset.SSTextField}.
	 * When a visual component is made up of multiple components a custom
	 * border may be required.
	 * 
	 * @return decorator
	 */
	default Decorator createDefaultDecorator() {
		return SSCommon.createDefaultDecorator();
	}

	/**
	 * Return the decorator used by this component.
	 * @return the decorator
	 */
	default Decorator getDecorator() {
		return getSSCommon().getDecorator();
	}

	/**
	 * Install the given decorator.
	 * @param deco decorator to install
	 */
	default void setDecorator(Decorator deco) {
		getSSCommon().setDecorator(deco);
	}

	/**
	 * Run the decorator.
	 * @return true if component data valid
	 */
	default boolean decorate() {
		return getSSCommon().decorate();
	}

	// Methods that have the word Bound in them
	// :g/Bound.*{/
	// default String getBoundColumnText() { return getColumnText(); }
	// default Object getBoundColumnObject() { return getColumnObject(); }
	// default <T> T getBoundColumnObject(Class<T> type) { return getColumnObject(type); }
	// default void setBoundColumnText(final String boundColumnText) { setColumnText(boundColumnText); }
	// default void setBoundColumnObject(final Object boundColumnObject) { setColumnObject(boundColumnObject); }
	// default void setBoundColumnArray(final SSArray boundColumnArray) throws SQLException { setColumnArray(boundColumnArray); }
	// default int getBoundColumnIndex() { return getColumnIndex(); }
	// default String getBoundColumnName() { return getColumnName(); }
	// default JDBCType getBoundColumnJDBCType() { return getColumnJDBCType(); }
	// default int getBoundColumnType() { return getColumnType(); }
	// default void setBoundColumnName(final String boundColumnName) { setColumnName(boundColumnName); }
}
