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
 * copyright (C) 2024-2025, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.utils;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Path;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.sql.RowSet;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.datasources.SSSQLConversionException;
import com.nqadmin.swingset.datasources.SSSQLInternalException;
import com.nqadmin.swingset.datasources.SSSQLNullException;
import com.nqadmin.swingset.decorators.BorderDecorator;
import com.nqadmin.swingset.decorators.Decorator;
import com.nqadmin.swingset.decorators.Validator;
import com.nqadmin.swingset.formatting.SSFormat;
import com.nqadmin.swingset.navigate.RowSetState;
import com.nqadmin.swingset.navigate.RowsEvent;
import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.navigate.RowsModelNewRowSetEvent;
import com.nqadmin.swingset.navigate.UndoRedo;
import com.raelity.lib.eventbus.WeakEventBus;
import com.raelity.lib.eventbus.WeakSubscribe;

import static com.nqadmin.swingset.datasources.ConvertType.convertToType;
import static com.nqadmin.swingset.navigate.RowSetState.isAcceptingChanges;
import static com.nqadmin.swingset.navigate.Utils.getGlobalEventBus;
import static com.nqadmin.swingset.navigate.Utils.hasActiveRow;
import static com.nqadmin.swingset.navigate.Utils.postRowSetModifiedError;
import static com.nqadmin.swingset.utils.SSUtils.JDBCTypeMismatch;
import static com.nqadmin.swingset.utils.SSUtils.NullabilityMismatch;
import static com.nqadmin.swingset.utils.SSUtils.objectID;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * Datasource binding data members and methods common to all SwingSet
 * components.
 * <p>
 * All SwingSet components should have a ssCommon datamember of type SSCommon
 * and should implement SSComponentInterface
 * <p>
 * SwingSet components will implement addComponentListener() and
 * removeComponentListener() to maintain data flow from the JComponent to
 * ssCommon.rowSet and from ssCommon.rowSet to JComponent
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
final class SSCommon
{
	/**
	 * Get a partially constructed SSCommon. If {@linkplain partialSSCommon} is not null
	 * then return it, otherwise create and return a new partialSSCommon.
	 * 
	 * Typically partial initialization is done if SSCommon is needed before the
	 * constructor finishes. The caller should invoke SSCommon.createFinish()
	 * in the constructor.
	 * <p>
	 * Assert if a non null partialSSCommon doesn't match the ssComponent.
	 * See {@link SSTextField#getSSCommon() } for example usage.
	 *
	 * @param ssComponent SwingSet component to attach to this SSCommon.
	 * @param partialSSCommon if non null return it
	 * @return partially constructed ssCommon
	 */
	static SSCommon createStart(SSComponentInterface ssComponent) {
		return new SSCommon(ssComponent, false);
	}

	/**
	 * Form a fully constructed SSCommon. If {@linkplain partialSSCommon} is not null
	 * then finish it's construction, otherwise create and return a new SSCommon.
	 * Doing "SSCommon.createFinish(this, null)" is equivalent to "new SSCommon(this)".
	 * <p>
	 * Assert if a non null partialSSCommon doesn't match the ssComponent.
	 * See {@link SSTextField#SSTextField(javax.sql.RowSet, java.lang.String) }
	 * for example usage.
	 * 
	 * @param ssComponent SwingSet component to attach to this SSCommon.
	 * @param partialSSCommon if non null finish it's construction
	 * @return fully constructed SSCommon
	 */
	static SSCommon createFinish(SSComponentInterface ssComponent,
								 SSCommon partialSSCommon) {
		if (partialSSCommon != null && ssComponent != partialSSCommon.ssComponent)
			throw new IllegalArgumentException("ssComponent mismatch");
		return partialSSCommon == null ? new SSCommon(ssComponent, true)
										: partialSSCommon.finishInit();
	}

	private final BusReceiver busReceiver; // Must have a strong reference.

	/**
	 * RowSet Listeners to update the bound SwingSet component.
	 * When working with a {@linkplain javax.sql.rowset.CachedRowSet} there are
	 * extra steps involved which require the listener to ignore some events, see
	 * {@link RowsModel#addRowSetEvent(
	 * com.nqadmin.swingset.navigate.NavigationRowSetEvent.RowSetEventType,
	 * javax.sql.RowSetEvent)} and
	 * {@link RowSetState#acceptChanges(javax.sql.rowset.CachedRowSet, java.lang.Runnable)}.
	 */
	class BusReceiver {
		/**
		 * Catch RowSet events; update the component's display.
		 * Ignore events that came from this component; they are handled internally.
		 * Only events from "our" RowSet are handled;
		 * this includes events from other {@link RowsModel}s.
		 * @param ev 
		 */
		@WeakSubscribe
		public void handleRowSetEvent(RowsEvent ev)
		{
			logger.log(TRACE, () -> sf("%s %s %s",
					getColumnForLog(), objectID(getRowSet()), ev.toString()));

			// XXX needs testing
			if (isAcceptingChanges(getRowSet())) // only possible if CachedRowSet
				return;

			// Check not generated by this component, but for this components rowSet.
			if (ev.getOperComponent() != getSSComponent()
					&& ev.getRowSet() == getRowSet()) {
				if (!RowsModel.ENABLED)
					return;
				handleComponentEnableDisable();
				updateSSComponent();
			}
		}

		@WeakSubscribe
		public void handleNewRowSetEvent(RowsModelNewRowSetEvent ev)
		{
			if (ev.getRowsModel() != rowsModel)
				return;

			// XXX needs testing
			if (isAcceptingChanges(getRowSet())) // only possible if CachedRowSet
				return;

			updateBindingForNewRowSet();
			handleComponentEnableDisable();
			// TODO: catch exceptions for bus
			updateSSComponent();
		}
	}

	private void handleComponentEnableDisable() {
		// Don't enable if primary key or there's no RowSet
		// Might want API for component disable, with a temporary override when RS null.
		// TODO: is it required that an SSCompo be JComponent?

		if (getSSComponent() instanceof JComponent jc) {
			// if (rowsModel.getRowSet() == null)
			if (rowsModel.getRowSet() == null
					|| !(rowsModel.containsRows() || rowsModel.isOnInsertRow()))
				jc.setEnabled(false);
			else {
				// TODO: not null return protection.
				Boolean isKey = RowSetState.isKey(getSSComponent());
				jc.setEnabled(!isKey);
			}
		}
	}

	private void updateBindingForNewRowSet() {
		boundColumnIndex = NO_COLUMN_INDEX; // In case the field is in a different position

		if (rowsModel.getRowSet() == null) {
			getSSComponent().metadataChange();
			return;
		}

		if (!fullyBound) {
			bind(rowsModel, boundColumnName, false);
			return;
		}

		// Verify same ColumnType and nullability
		try {
			JDBCType typ = JDBCType.valueOf(
					RowSetOps.getColumnType(getRowSet(), boundColumnName));
			if (boundColumnJDBCType != typ)
				throw new IllegalArgumentException(JDBCTypeMismatch(boundColumnJDBCType, typ));
			boolean nulbl = RowSetOps.isNullable(getRowSet(), boundColumnName).get();
			if (isNullable.get() != nulbl)
				throw new IllegalArgumentException(NullabilityMismatch(getAllowNull(), nulbl));
		} catch (SQLException ex) {
			logger.log(Level.ERROR, (String) null, ex);
		}
		getSSComponent().metadataChange();
	}

	// /**
	//  * When the database row changes we want to trigger a change to the bound
	//  * Component display/value.
	//  * <p>
	//  * In {@link RowsModel}, when a navigation is performed (first, previous,
	//  * next, last) a call may be made to updateRow() to flush the rowset to the 
	//  * underlying database prior to a call to first(), previous(), next(),
	//  * or last(). updateRow() triggers rowChanged, but we don't want to update
	//  * the components for the database flush.
	//  * <p>
	//  * Calls to first(), previous(), next(), and last() trigger cursorMoved.
	//  * For a navigation we will updated the components following cursorMoved.
	//  * <p>
	//  * In JdbcRowSetImpl, notifyRowChanged() is called for insertRow(),
	//  * updateRow(), deleteRow(), &amp; cancelRowUpdates(). We only want to block
	//  * component updates for calls resulting from updateRow().
	//  */

	// protected class SSRowSetListener implements RowSetListener
	// ...

	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	/** Constant to indicate that no RowSet column index has been specified. */
	static final int NO_COLUMN_INDEX = -1;

	/** Index of RowSet column to which the SwingSet component will be bound. */
	private int boundColumnIndex = NO_COLUMN_INDEX;

	/** Column JDBCType enum. */
	private JDBCType boundColumnJDBCType = java.sql.JDBCType.NULL;

	/** Name of RowSet column to which the SwingSet component will be bound. */
	private String boundColumnName = null;
	
	/** EventListener use for detecting component changes for RowSet column binding. */
	private EventListener eventListener = null;

	/** Name for log in boundColumnName is not set. */
	private String logColumnName = null;

	/** true for component bound with RowSet not null. */
	boolean fullyBound;

	private Decorator decorator;
	private Validator validator;

	//
	// isNullable is a cache of metadata.
	// TODO: Incorporate into a more formal metadata cache
	//       if/when there is one.
	//
	
	/**
	 * Flag to indicate if the bound database column can be null.
	 * <p>
	 * Adds in a blank item being added to SSCombobox and SSDBComboBox.
	 * <p>
	 * Usage determines default when allowNull.isPresent() == false.
	 */
	private Optional<Boolean> allowNull = Optional.empty();

	/**
	 * Reflects the state of the data source metadata about nullability
	 * of the bound column. False when there's a "NOT NULL" constraint.
	 * Empty if the metadata specifies unknown.
	 */
	private Optional<Boolean> isNullable = Optional.empty();

	/** parent SwingSet component */
	private final SSComponentInterface ssComponent;

	/** RowsModel from which component will get/set values. */
	private RowsModel rowsModel;
	
	/** Indicates if rowset listener is added (or removed) */
	// XXX
	private boolean rowSetListenerAdded = false;
	
	/** Indicates if swingset component listener is added (or removed) */
	private boolean ssComponentListenerAdded = false;

	/**
	 * Constructor that has a flag to only "half" initialize; typically half
	 * initialization is done iff SSCommon is needed before the constructor finishes.
	 * The caller should invoke SSCommon in the constructor.
	 *
	 * @param ssComponent SwingSet component having this SSCommon instance as a
	 *                     data member
	 * @param finishInit if false, the SSComponent still needs to call finishSSCommon.
	 */
	private SSCommon(SSComponentInterface ssComponent, boolean finishInit) {
		this.ssComponent = ssComponent;
		decorator = Decorator.nullDecorator;
		validator = Validator.nullValidator;
		busReceiver = new BusReceiver();
		if (finishInit)
			finishInit();
	}

	//
	// TODO: long term get rid of this half init stuff. Maybe a builder...???
	//

	/**
	 * Finish the initialization, used if "half" construction.
	 */
	private SSCommon finishInit() {
		if (!isFullyInitialized) {
			isFullyInitialized = true;
			initDecorator();
			init();

			// TODO: Get rid of this; use rowsModel.register
			WeakEventBus.register(busReceiver, getGlobalEventBus());
		}
		return this;
	}

	/** Can use this to error if doing something that required fully constructed. */
	private boolean isFullyInitialized;

	/**
	 * Check if this SSCommon is initialized.
	 * Throw {@linkplain IllegalStateException} if not ready for prime time.
	 */
	void verifyInitialized() {
		if (!isFullyInitialized)
			throw new IllegalStateException("Missing SSComponent's finishSSCommon");
	}

	/**
	 * Check if this SSCommon is initialized.
	 * Throw {@linkplain IllegalStateException} if not ready for prime time.
	 * @return
	 */
	@SuppressWarnings("unused")
	boolean isInitialized() {
		return isFullyInitialized;
	}

	/**
	 * Used by an SSComponent when making a change to the database.
	 * Typically used by a component listener. It avoids extra RowSet events.
	 * @param r code that changes the database
	 */
	void dbChange(Runnable r)
	{
		if (rowsModel.getRowSet() == null)
			return;
		if (!checkRowOK())
			return;
		RowsModel.startRowsEvent(getRowsModel(), getSSComponent());
		disableRowSetListening();
		try {
			r.run();
		} finally {
			enableRowSetListening();
			RowsModel.finishRowsEvent(getRowsModel());
		}
	}

	/**
	 * Indicates if the components RowSet listener is added/enabled
	 *
	 * @return true if the components RowSet listener is added/enabled, otherwise false
	 */
	boolean isRowSetListenerAdded() {
		// XXX
		return rowSetListenerAdded;
	}

	/**
	 * Method to add the RowSet listener.
	 */
	private void enableRowSetListening()
	{
		// XXX
		if (!rowSetListenerAdded && getRowSet()!=null) {
			rowSetListenerAdded = true;
			logger.log(DEBUG, () -> sf("%s - RowSet Listener added.", getColumnForLog()));
		}
	}

	/**
	 * Method to remove the RowSet listener.
	 */
	private void disableRowSetListening()
	{
		// XXX
		if (rowSetListenerAdded) {
			rowSetListenerAdded = false;
			logger.log(DEBUG, () -> sf("%s - RowSet Listener removed.", getColumnForLog()));
		}
	}

	/**
	 * Indicates if the components value change listener is currently added/enabled
	 *
	 * @return true if the components value change listener is added/enabled, otherwise false
	 */
	boolean isSSComponentListenerAdded() {
		return ssComponentListenerAdded;
	}
	
	/**
	 * Method to add any SwingSet Component listener(s).
	 */
	void addSSComponentListener()
	{
		// Probably should not have a null eventListener here, but just in case
		if (eventListener==null) {
			return;
		}
		verifyInitialized();
		
		if (!ssComponentListenerAdded) {
			getSSComponent().getSSComponentHook().addSSComponentListener(eventListener);
			ssComponentListenerAdded = true;
		}
		if (ssComponentListenerAdded) {
			logger.log(TRACE, () -> sf("%s - Component Listener added.", getColumnForLog()));
		}
	}
	
	/**
	 * Method to add any SwingSet Component listener(s).
	 */
	void removeSSComponentListener()
	{
		// Probably should not have a null eventListener here, but just in case
		if (eventListener==null) {
			return;
		}
		verifyInitialized();
		
		if (ssComponentListenerAdded) {
			ssComponentListenerAdded = false;
			getSSComponent().getSSComponentHook().removeSSComponentListener(eventListener);
		}
		if (!ssComponentListenerAdded) {
			logger.log(TRACE, () -> sf("%s - Component Listener removed.", getColumnForLog()));
		}
	}

	EventListener getEventListener()
	{
		return eventListener;
	}

	void bind(RowsModel rowsModel, String boundColumnName)
	{
		bind(rowsModel, boundColumnName, true);
	}

	boolean isFullyBound()
	{
		return fullyBound;
	}

	/**
	 * Sets the RowsModel and column name to which the component is to be bound.
	 * <p>
	 * Takes care of setting RowSet and Column Name for ssCommon and then calls
	 * bind(this.ssCommon);
	 *
	 * @param rowsModel holds RowSet to be used.
	 * @param boundColumnName Name of the column to which this check box should be bound
	 */
	private void bind(RowsModel rowsModel, String boundColumnName, boolean doStart)
	{
		Objects.requireNonNull(rowsModel);
		RowSet rs = rowsModel.getRowSet();
		if (rs != null) {
			try {
				getSSComponent().checkColumnType(RowSetOps.getJDBCColumnType(rs, boundColumnName));
			} catch (SQLException ex) {
				// TODO: This should invalidate the RowsModel
				throw new IllegalArgumentException("SQLException getting column type", ex);
			}
		}

		if (doStart)
			startBind(rowsModel, boundColumnName);
		else
			completeBind();

		if (rs != null)
			getSSComponent().finishBind(); // Primary keys for SyncResolver, joins
	}

	/**
	 * Takes care of setting RowSet and Column Name for ssCommon and then calls
	 * startBind() to update Component;
	 *
	 * @param rowsModel        datasource to be used
	 * @param boundColumnName name of the column to which this check box should be
	 *                         bound
	 */
	private void startBind(RowsModel rowsModel, String boundColumnName)
	{
		Objects.requireNonNull(rowsModel);
		verifyInitialized();

		// Insure not already bound and columnName OK.
		if (this.rowsModel != null)
			throw new IllegalStateException(sf("Component already bound to a model: %s %s %s/%s",
					objectID(getSSComponent()), objectID(this.rowsModel),
					objectID(rowsModel), objectID(rowsModel.getRowSet())));
		if (this.boundColumnName != null)
			throw new IllegalStateException(getColumnForLog() + " already has columnName: " + objectID(rowsModel.getRowSet()));
		// TODO: what's the meaning of an empty boundColumnName? should this be an error?
		if (boundColumnName.isEmpty())
			throw new IllegalStateException("Emply boundColumnName: " + objectID(rowsModel.getRowSet()));

		// Stash the model and columnName and housecleaning and get out if no RowSet
		
		this.boundColumnName = boundColumnName;
		this.rowsModel = rowsModel;
		if (eventListener==null)
			eventListener = getSSComponent().getSSComponentHook().getSSComponentListener();
		enableRowSetListening();

		if (rowsModel.getRowSet() == null) {
			// TODO: combine this with some of the code in handleNewRowSetEvent.
			if (getSSComponent() instanceof JComponent jc)
				jc.setEnabled(false);
			return;
		}
		completeBind();
		
		// Update component.
		// For an SSDBComboBox, we have likely not yet called execute to populate the
		// combo lists so the text for the first record will be blank, but
		// updateSSComponent() for SSDBComboBox checks for a null list and returns.
		handleComponentEnableDisable();
		updateSSComponent();
	}

	private void completeBind()
	{
		try {
			boundColumnJDBCType = JDBCType.valueOf(
					RowSetOps.getColumnType(getRowSet(), boundColumnName));

			// isNullable used a lot. Do it here.
			isNullable = RowSetOps.isNullable(getRowSet(), boundColumnName);
			logger.log(TRACE, () -> sf("Column isNullable: %s.", isNullable));
			logger.log(TRACE, () -> sf("Column bind succeeded: name=%s %s.",
					boundColumnName, getRowSet()==null ? ", rowset=null" : ""));

			ssComponent.metadataChange();
			fullyBound = true;
		} catch (SQLException ex) {
			logger.log(ERROR, getColumnForLog() + " - SQL Exception.", ex);
		}
	}

	/**
	 * Retrieves the allowNull flag for the bound database column.
	 * If setAllowNull() wasn't used, then the database metadata is used
	 * to determine nullability; if database state unknown then return true;
	 *
	 * @return true if bound database column can contain null values, otherwise
	 *         returns false
	 */
	boolean getAllowNull() {
		return allowNull.orElseGet(() -> isNullable.orElse(true));
	}

	/**
	 * Returns the index of the database column to which the SwingSet component is
	 * bound.
	 *
	 * @return returns the index of the column to which the SwingSet component is
	 *         bound
	 */
	int getBoundColumnIndex() {
		if (boundColumnIndex == NO_COLUMN_INDEX) {
			try {
				boundColumnIndex = RowSetOps.getColumnIndex(getRowSet(), boundColumnName);
			} catch (SQLException ex) {
				// TODO: Ex should be impossible, wrap in runtime error (see google Ex)
				logger.log(Level.ERROR, (String) null, ex);
			}
		}
		return boundColumnIndex;
	}

	/**
	 * Returns the JDBCType enum representing the bound database column data type.
	 * <p>
	 * Based on java.sql.JDBCType
	 *
	 * @return the enum value corresponding to the data type of the bound column
	 */
	JDBCType getBoundColumnJDBCType() {
		return boundColumnJDBCType;
	}

	/**
	 * Returns the name of the database column to which the SwingSet component is
	 * bound.
	 *
	 * @return the boundColumnName
	 */
	String getBoundColumnName() {
		return boundColumnName;
	}

	/**
	 * Returns an Object 
	 * representing the value in the bound database column.
	 * <p>
	 * Note a null is never converted into ""; use getBoundColumnText for that.
	 * @return value
	 */
	Object getBoundColumnObject()
	{
		Object value = null;

		try {
			if (hasActiveRow(getSSComponent())) {
				value = RowSetOps.getColumnObject(ssComponent);
			}
		} catch (SQLException se) {
			logger.log(ERROR, getColumnForLog() + " - SQL Exception.", se);
		}

		return value;
	}

	/**
	 * Returns an Object of the specified type
	 * representing the value in the bound database column.
	 *
	 * Note a null is never converted into ""; use getBoundColumnText for that.
	 * @param <T> type to return
	 * @param type Class of returned type
	 * @return value
	 */
	<T> T getBoundColumnObject(Class<T> type)
	{
		T value = null;

		try {
			if (getRowSet().getRow() != 0) {
				value = RowSetOps.getColumnObject(ssComponent, type);
			}
		} catch (SQLException se) {
			// TODO: Shouldn't an error be propogated? Related methods as well.
			logger.log(ERROR, getColumnForLog() + " - SQL Exception.", se);
		}

		return value;
	}

	/**
	 * Returns a String representing the value in the bound database column.
	 * <p>
	 * New functionality added (2020) to allow this method to return a null String
	 * if allowNull==true.
	 *
	 * @return String containing the value in the bound database column
	 */
	String getBoundColumnText() {

// TODO Consider checking for a null RowSet. This would be the case for an unbound SSDBComboBox used for navigation.

		String value = "";

		try {
			if (hasActiveRow(ssComponent)) {
				value = RowSetOps.getColumnObjectText(ssComponent);
				if (!getAllowNull() && (value == null)) {
					value = "";
				}
			}
		} catch (SQLException se) {
			logger.log(ERROR, getColumnForLog() + " - SQL Exception.", se);
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
	int getBoundColumnType() {
		return boundColumnJDBCType.getVendorTypeNumber();
	}

	/**
	 * Returns the bound column name in square brackets.
	 *
	 * @return the boundColumnName in square brackets
	 */
	String getColumnForLog() {
		return sf("[%s:%d]", boundColumnName != null ? boundColumnName : logColumnName,
				boundColumnIndex);
	}

	/**
	 * @return the parent/calling SwingSet JComponent implementing
	 *         SSComponentInterface
	 */
	SSComponentInterface getSSComponent() {
		return ssComponent;
	}

	/**
	 * Returns the RowSet to which the SwingSet component is bound.
	 *
	 * @return RowSet to which the SwingSet component is bound
	 */
	RowSet getRowSet() {
		return rowsModel != null ? getRowsModel().getRowSet() : null;
	}

	/**
	 * Returns the RowsModel to which the SwingSet component is bound.
	 * @return 
	 */
	RowsModel getRowsModel() {
		return rowsModel;
	}

	/**
	 * Method called from Constructor to perform one-time setup tasks including
	 * field traversal and any custom initialization method specific to the SwingSet
	 * component.
	 */
	protected void init()
	{
		getSSComponent().configureTraversalKeys();
		getSSComponent().setupUndoRedoKeys();
		getSSComponent().customInit();
	}

	/**
	 * Sets the allowNull flag for the bound database column, this
	 * overrides the database metadata for isNullable. Set to null
	 * to use the database metadata.
	 *
	 * @param allowNull flag to indicate if the bound database column can be null
	 */
	void setAllowNull(Boolean allowNull) {
		this.allowNull = allowNull == null ? Optional.empty() : Optional.of(allowNull);
	}

	/**
	 * Sets the name of the bound database column.
	 *
	 * @param boundColumnName column name to which the Component is to be bound.
	 * @deprecated use bind()
	 */
	@Deprecated
	void setBoundColumnName(String boundColumnName) {
		throw new IllegalAccessError("use bind()");
	}

	/**
	 * Name/text to display in log messages if boundColumnName is not set.
	 * @param logColumnName text
	 */
	void setLogColumnName(String logColumnName) {
		Objects.requireNonNull(logColumnName);
		this.logColumnName = logColumnName;
	}

	/**
	 * Name/text to display in log messages if boundColumnName is not set.
	 * @return text for log entries, null if never set
	 */
	String getLogColumnName() {
		return logColumnName;
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
	// TODO: SHOULD IT RETURN AN ERROR LIKE setBoundColumnText?
	void setBoundColumnArray(SSArray boundColumnArray) throws SQLException {
		logger.log(DEBUG, () -> sf("%s: %s", getColumnForLog(), boundColumnArray));
		boolean is_error = true;
		try {
			RowSetOps.updateColumnArray(getSSComponent(), boundColumnArray);
			is_error = false;
		} catch(SQLException ex) {
			userErrorReporting(boundColumnArray, ex);
		} finally {
			if (is_error)
				postRowSetModifiedError(getSSComponent(), boundColumnArray);
		}
	}

	/**
	 * Updates the bound database column with the specified Object.
	 *
	 * @param boundColumnObject value to write to bound database column
	 * @return true if no error
	 */
	boolean setBoundColumnObject(Object boundColumnObject) {
		logger.log(DEBUG, () -> sf("%s: %s", getColumnForLog(), boundColumnObject));
		boolean ok = false;
		try {
			RowSetOps.updateColumnObject(getSSComponent(), boundColumnObject);
			ok = true;
		} catch(SQLException | NumberFormatException ex) {
			userErrorReporting(boundColumnObject, ex);
		} finally {
			if (!ok)
				postRowSetModifiedError(getSSComponent(), boundColumnObject);
		}
		return ok;
	}

	/**
	 * Updates the bound database column with the specified String.
	 *
	 * @param _boundColumnText value to write to bound database column
	 * @return true if no error
	 */
	boolean setBoundColumnText(String _boundColumnText) {
		logger.log(DEBUG, () -> sf("%s: %s", getColumnForLog(), _boundColumnText));
		boolean ok = false;
		try {
			RowSetOps.updateColumnText(getSSComponent(), _boundColumnText);
			ok = true;
		} catch(SQLException | NumberFormatException ex) {
			userErrorReporting(_boundColumnText, ex);
		} finally {
			if (!ok)
				postRowSetModifiedError(getSSComponent(), _boundColumnText);
		}
		return ok;
	}

	private void userErrorReporting(Object value, Exception ex)
	{
		String ex_title = null;
		String ex_msg = null;
		switch(ex) {
		case SSSQLInternalException e -> {
			ex_title = "SS Internal Error";
			ex_msg = sf("%s: %s", getBoundColumnName(), e.getMessage());
		}
		case SSSQLConversionException e -> {
			ex_title = "Conversion Error";
			ex_msg = e.getLocalizedMessage();
		}
		case SSSQLNullException _ -> {
			ex_title = "Null Exception";
			ex_msg = "Null values are not allowed for " + getBoundColumnName();
		}
		case SQLException _ -> {
			ex_title = "SQL Exception";
			ex_msg = "SQL Exception encountered for " + getBoundColumnName();
		}
		case NumberFormatException _ -> {
			ex_title = "Number Format Exception";
			ex_msg = "Number Format Exception encountered for " + getBoundColumnName() + " converting " + value + " to a number.";
		}
		default -> {}
		}
		logger.log(WARNING, getBoundColumnName() + " - " + ex_title + ".", ex);
		JOptionPane.showMessageDialog((JComponent)getSSComponent(), ex_msg,
									  ex_title, JOptionPane.ERROR_MESSAGE);
	}

	//public enum SSMessage { NO_ROW }
    // public void userErrorReporting(SSMessage msg)
	/**
	 * Entering data into black hole.
	 */
	void reportNeedRow()
	{
		JOptionPane.showMessageDialog((JComponent)getSSComponent(),
				"Please add a row before entering data.",
				"", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Report problem accessing image file to user.
	 * @param title dialog title
	 * @param path file path
	 * @param ex error
	 */
	void reportError(String title, Path path, Exception ex)
	{
		String pathName = path != null ? path.toAbsolutePath().toString() : "";
		logger.log(Level.ERROR, () -> sf("%s: IO Exception %s: file %s: %s",
				getColumnForLog(), ex.getClass().getSimpleName(),
				pathName, ex.getMessage()));

		// TODO: Alter message according to parameters.
		//		 For example, if path is null, leave out "file: 'xxx'"

		String msg = sf("<html>"
				+ "<center>%s</center>"
				+ "<br/>Details:<br/>"
				+ "<center>DB column: %s</center>"
				+ "<center>File: '%s'</center>"
				+ "<center>Exception: %s</center>",
				ex.getLocalizedMessage(), getColumnForLog(),
				pathName, ex.getClass().getSimpleName()
		);
		JOptionPane.showMessageDialog((Component)getSSComponent(), msg,
				title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Transfers focus to next Swing Component on the screen when either
	 * Shift-Down-Arrow or Enter are pressed; previous is Shift-Up-Arrow.
	 * 
	 * @param jc configure this JComponent
	 */
	static void configureTraversalKeys(JComponent jc)
	{
		// Forward traversal keys.
		final Set<AWTKeyStroke> forwardKeys = jc
				.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		final Set<AWTKeyStroke> newForwardKeys = new HashSet<>(forwardKeys);
		if (!(jc instanceof JTextArea)) {
			newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		}
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
		jc.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);

		// Backwards traversal keys.
		final Set<AWTKeyStroke> backwardKeys = jc
				.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		final Set<AWTKeyStroke> newBackwardKeys = new HashSet<>(backwardKeys);
		newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
		jc.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newBackwardKeys);

	}

	private static final String UNDO_ACTION_KEY = "SwingSetColumnUndo";
	private static final String REDO_ACTION_KEY = "SwingSetColumnRedo";
	/**
	 * Setup undo/redo action bindings for a component.
	 * @param comp
	 */
	static void setupUndoRedoKeys(SSComponentInterface comp)
	{
		logger.log(DEBUG, () -> sf("UndoRedoKeys: %s", comp.getClass().getSimpleName()));

		JComponent jc = (JComponent)comp;

		//int cond = JComponent.WHEN_FOCUSED;
		KeyStroke ksUndo = KeyStroke.getKeyStroke("ctrl Z");
		KeyStroke ksRedo = KeyStroke.getKeyStroke("ctrl Y");
		int cond = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
		InputMap im = new InputMap();
		im.put(ksUndo, UNDO_ACTION_KEY);
		im.put(ksRedo, REDO_ACTION_KEY);
		im.setParent(jc.getInputMap(cond));
		jc.setInputMap(cond, im);
		ActionMap am = new ActionMap();
		am.put(UNDO_ACTION_KEY, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				UndoRedo.undoRedo(comp, UndoRedo.UNDO);
			}
		});
		am.put(REDO_ACTION_KEY, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				UndoRedo.undoRedo(comp, UndoRedo.REDO);
			}
		});
		am.setParent(jc.getActionMap());
		jc.setActionMap(am);
	};

	/**
	 * Use the specified argument, which comes from an undo or redo command,
	 * to set the components value.
	 * Whether the command was undo or redo generally doesn't matter.
	 * @param cmd undo or redo
	 * @param value the new value
	 * @throws java.sql.SQLException if...
	 */
	void undoRedoUpdateObject(UndoRedo cmd, Object value) throws SQLException
	{
		if (!UndoRedo.isUndoRedoEnabled(ssComponent))
			throw new IllegalStateException("UNDO/REDO disabled");
		logger.log(DEBUG, () -> sf("%s: %s", cmd, value));

		// TODO: put following in RowSetOps
		
		Object obj = value;
		if (Boolean.TRUE)
			obj = convertToType(obj, getBoundColumnJDBCType());
		// NOTE: following does not generate any events
		getRowSet().updateObject(getBoundColumnIndex(), obj);
		// NOTE: Previous line sets value in RowSet's pending update.
		//		 Following line causes updateSSComponent which gets the
		//		 value to display from undoRedo; the update values
		//		 can not always be reliably read JdbcRowSet vs CachedRowSet.
		issueRowChanged();

		// TODO: RowsModel UNDO/REDO
		if (RowsModel.ENABLED)
			updateSSComponent();
	}

	/**
	 * Method used by RowSet listeners to update the bound SwingSet component.
	 * <p>
	 * Handles removal of Component listener before update and addition of listener
	 * after update.
	 */
	void updateSSComponent()
	{
		// If you see this in the logs back to back for the same component
		// a listener is likely not handled properly.
		// Maybe incorporate SwingUtilities.invokeLater()? 
		logger.log(TRACE, () -> sf("Updating component %s", getColumnForLog()));
		verifyInitialized();
		
		removeSSComponentListener();
		if (rowsModel.getRowSet() == null)
			getSSComponent().cleanField();
		else
			ssComponent.getSSComponentHook().updateSSComponent();
		addSSComponentListener();
		decorate();
	}

	/**
	 * Determine if there's a row that can be modified; dialog if not.
	 * Typically used in an SSComponent's listener.
	 * @return true if there's a row
	 */
	boolean checkRowOK()
	{
		return checkRowOK(null);
	}

	/** part of avoiding multiple dialogs for same user action */
	private boolean doingCheckRowOK;

	/**
	 * Determine if there's a row that can be modified; optionally dialog if not.
	 * If there's no row, only dialog if dialogOK is true.
	 * A nested check never does the dialog.
	 * Typically used in an SSComponent's listener.
	 *
	 * @param dialogOK if null or evaluates true then dialog
	 * @return true if there's a row
	 */
	boolean checkRowOK(Supplier<Boolean> dialogOK)
	{
		// Focus change events may cause listeners to trigger. Don't want
		// to give multiple dialogs.
		if (doingCheckRowOK) 
			try {
				return hasActiveRow(getSSComponent());
			} catch (SQLException ex) {
				return false;
			}

		doingCheckRowOK = true;
		try {
			try {
				if (hasActiveRow(getSSComponent()))
					return true;
			} catch (SQLException ex) {
			}
			if (dialogOK == null || dialogOK.get())
				reportNeedRow();
		} finally {
			doingCheckRowOK = false;
		}
		return false;
	}

	/**
	 * Issue a row changed event if there's an active RowSetListener.
	 */
	// TODO: Cleanup issueRowChanged for RowsModel
	//       Want to run updateCompent as through from a RowSetListener.
	//       Why not call it directly?
	void issueRowChanged() {
		if(isRowSetListenerAdded()) {
			// XXX

			// Following used to be in SSRowSetListener.
			if (isAcceptingChanges(getRowSet())) // only possible if CachedRowSet
				return;
			// XXX
			logger.log(TRACE, () -> sf("%s - RowSet changed.", getColumnForLog()));
			// Use invokeLater for updateSSComponent? It was when part of listener.
			updateSSComponent();
		}
		else logger.log(WARNING, () -> sf("%s - NO LISTENER.", getColumnForLog()));
	}

	///////////////////////////////////////////////////////////////////////////
	//
	// The idea is to extend the decorators to handle a variety of component
	// types and ways to decorate. Wonder how to do that?
	//
	// TODO: Could have one interface that is both validator and decorator
	//		 and whatever else is needed: InputVerifier, ???.
	//

	static Decorator createDefaultDecorator() {
		return new BorderDecorator();
		//return new BackgroundDecorator();
	}

	private SSFormat ssFormat;
	/**
	 * @param ssFormat
	 * @see SSComponentInterface#setFormat(com.nqadmin.swingset.formatting.SSFormat) 
	 */
	void setSSFormat(SSFormat ssFormat) { this.ssFormat = ssFormat; }

	/**
	 * @return 
	 * @see SSComponentInterface#getSSFormat() 
	 */
	SSFormat getSSFormat() { return ssFormat; }

	/**
	 * Find the default decorator for this component type and set it.
	 */
	private void initDecorator() {
		// For now just pick any decorator as the default.
		// Probably want to get it from a factory/provider and use this
		// component type as part of the decision. If a component wants to
		// extend behavior, could get the current decorator, delegate to it,
		// with some custom behavior used by the component.

		// Is the following snippet a good way to override the default?
		//		decorator deco = getSSComponent().createDefaultDecorator();
		// where the default implementation returns null

		setDecorator(getSSComponent().createDefaultDecorator());
	}
	/**
	 * Run the decorator.
	 * @return true if component data valid
	 */
	boolean decorate() {
		return decorator.decorate();
	}

	/**
	 * Install the given decorator.
	 * @param deco decorator to install
	 */
	void setDecorator(Decorator deco) {
		decorator.uninstall();
		deco.install(getSSComponent());
		decorator = deco;
	}

	/**
	 * Return the decorator used by this component.
	 * @return the decorator
	 */
	Decorator getDecorator() {
		return decorator;
	}

	///////////////////////////////////////////////////////////////////////////
	//
	// Validator
	//

	/**
	 * Install the given validator into the component
	 * @param _validator validator to install
	 */
	void setValidator(Validator _validator) {
		validator.uninstall();
		_validator.install(ssComponent);
		validator = _validator;
	}
	
	/**
	 * Run the SSComponent's plugin validator, return the result.
	 * First check component specific validator, then plugin validator.
	 * 
	 * @return true if successful validation
	 */
	boolean pluginValidate()
	{
		// Invoke the user's validator
		return validator.validate();
	}

	///////////////////////////////////////////////////////////////////////////
	//
	// Component Value Change
	//

	// TODO:	Implement a way to select when to do validation.
	//			For example, OnChangeOrAction, InputVerifier, OnFocusChange, ...?
	//
	// TODO:	May need a plugin for whether or not to allow decorate().
	//			Incorporate check into Validator? Decorator? ChangeHandler?

}
