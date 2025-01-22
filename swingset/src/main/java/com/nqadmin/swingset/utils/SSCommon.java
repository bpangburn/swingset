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

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.datasources.SSSQLConversionException;
import com.nqadmin.swingset.datasources.SSSQLInternalException;
import com.nqadmin.swingset.datasources.SSSQLNullException;
import com.nqadmin.swingset.decorators.BorderDecorator;
import com.nqadmin.swingset.decorators.Decorator;
import com.nqadmin.swingset.decorators.Validator;
import com.nqadmin.swingset.formatting.SSFormat;
import com.nqadmin.swingset.navigate.NavigateActions;
import com.nqadmin.swingset.navigate.NavigateActions.UndoRedo;
import com.nqadmin.swingset.navigate.NavigationModel;
import com.nqadmin.swingset.navigate.NavigationRowSetEvent;
import com.nqadmin.swingset.navigate.RowSetState;
import com.nqadmin.swingset.utils.SSUtils.DebugRowSetListener;
import com.raelity.lib.eventbus.WeakEventBus;
import com.raelity.lib.eventbus.WeakSubscribe;

import static com.nqadmin.swingset.datasources.ConvertType.convertToType;
import static com.nqadmin.swingset.navigate.RowSetState.isAcceptingChanges;
import static com.nqadmin.swingset.navigate.Utils.getGlobalEventBus;
import static com.nqadmin.swingset.navigate.Utils.postRowSetModifiedError;
import static com.nqadmin.swingset.utils.CentralLookup.defLookup;
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
	 * Get a fully constructed SSCommon. If {@linkplain partialSSCommon} is not null
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
	 * Listener(s) for the underlying RowSet used to update the bound SwingSet
	 * component.
	 * When working with a {@linkplain javax.sql.rowset.CachedRowSet} there are
	 * extra steps involved which require the listener to ignore some events, see
	 * {@link NavigationModel#addRowSetEvent(com.nqadmin.swingset.navigate.NavigationRowSetEvent.RowSetEventType, javax.sql.RowSetEvent) } and
	 * {@link RowSetState#acceptChanges(javax.sql.rowset.CachedRowSet, java.lang.Runnable) }.
	 */
	class BusReceiver {
		/**
		 * Catch RowSet events; update the component's display.
		 * Ignore events that came from this component; they are handled internally.
		 * Only events from "our" RowSet are handled.
		 * @param ev 
		 */
		@WeakSubscribe
		public void handleRowSetEvent(NavigationRowSetEvent ev)
		{
			logger.log(DEBUG, () -> sf("%s %s %s",
					getColumnForLog(), getRowSet(), ev.toString()));

			// TODO: ev.getModel != getModel /// not RowSet ???

			if (ev.getComponent() != getSSComponent()
					&& ev.getRowSet() == getRowSet()) {
				if (!NavigationModel.ENABLED)
					return;
				updateSSComponent();
			}
		}
	}

	/**
	 * When the database row changes we want to trigger a change to the bound
	 * Component display/value.
	 * <p>
	 * In {@link NavigationModel}, when a navigation is performed (first, previous,
	 * next, last) a call may be made to updateRow() to flush the rowset to the 
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

	protected class SSRowSetListener implements RowSetListener
	{
		// variables needed to consolidate multiple calls
		private int lastChange = 0;
		private int lastNotifiedChange = 0;

		/**
		 * When the cursor moves we want to trigger a change to the bound Component
		 * display/value.
		 */
		@Override
		public void cursorMoved(RowSetEvent event) {
			if (isAcceptingChanges(rowSet)) { // only possible if CachedRowSet
				return;
			}
			logger.log(TRACE, () -> sf("%s - RowSet cursor moved.", getColumnForLog()));
			performUpdateSSComponent();
		}

		@Override
		public void rowChanged(RowSetEvent event) {
			if (isAcceptingChanges(rowSet)) { // only possible if CachedRowSet
				return;
			}
			logger.log(TRACE, () -> sf("%s - RowSet row changed.", getColumnForLog()));
			performUpdateSSComponent();
		}

		/**
		 * When the RowSet is modified we want to trigger a change to the bound
		 * Component display/value.
		 */
		@Override
		public void rowSetChanged(RowSetEvent event) {
			if (isAcceptingChanges(rowSet)) { // only possible if CachedRowSet
				return;
			}
			logger.log(TRACE, () -> sf("%s - RowSet changed.", getColumnForLog()));
			performUpdateSSComponent();
		}
		

		private void performUpdateSSComponent() {
			lastChange++;
			logger.log(TRACE, () -> sf("%s - performUpdates(): lastChange=%s, lastNotifiedChange=%s",
					getColumnForLog(), lastChange, lastNotifiedChange));
			
			// Delay execution of logic until all listener methods are called for current event
			// Based on: https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
			SwingUtilities.invokeLater(() -> {
				if (lastNotifiedChange != lastChange) {
					lastNotifiedChange = lastChange;

					if (NavigationModel.ENABLED)
						return;
					updateSSComponent();
				}
			});
		}
	} // end protected class SSRowSetListener

	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Constant to indicate that no RowSet column index has been specified.
	 */
	static final int NO_COLUMN_INDEX = -1;
	
	/**
	 * Flag to indicate if the bound database column can be null.
	 * <p>
	 * Adds in a blank item being added to SSCombobox and SSDBComboBox.
	 * <p>
	 * Usage determines default when allowNull.isPresent() == false.
	 */
	private Optional<Boolean> allowNull = Optional.empty();

	/**
	 * Index of RowSet column to which the SwingSet component will be bound.
	 */
	private int boundColumnIndex = NO_COLUMN_INDEX;

	/**
	 * Column JDBCType enum.
	 */
	private JDBCType boundColumnJDBCType = java.sql.JDBCType.NULL;

	/**
	 * Name of RowSet column to which the SwingSet component will be bound.
	 */
	private String boundColumnName = null;
	
	/**
	 * EventListener use for detecting component changes for RowSet column binding
	 */
	private EventListener eventListener = null;

	/**
	 * Name for log in boundColumnName is not set.
	 */
	private String logColumnName = null;

	private Decorator decorator;
	private Validator validator;

	//
	// isNullable is a cache of metadata.
	// TODO: Incorporate into a more formal metadata cache
	//       if/when there is one.
	//

	/**
	 * Reflects the state of the data source metadata about nullability
	 * of the bound column. False when there's a "NOT NULL" constraint.
	 * Empty if the metadata specifies unknown.
	 */
	private Optional<Boolean> isNullable = Optional.empty();

	/**
	 * flag to indicate if we're inside of a bind() method
	 */
	private volatile boolean inBinding = false;

	/**
	 * parent SwingSet component
	 */
	private final SSComponentInterface ssComponent;

	/**
	 * database connection
	 */
	private Connection connection = null;

	/**
	 * RowSet from which component will get/set values.
	 */
	private RowSet rowSet = null;
	
	/**
	 * Indicates if rowset listener is added (or removed)
	 */
	private boolean rowSetListenerAdded = false;
	
	/**
	 * Indicates if swingset component listener is added (or removed)
	 */
	private boolean ssComponentListenerAdded = false;

	/**
	 * Underlying RowSet listener.
	 */
	private SSRowSetListener rowSetListener = null;

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

			// TODO: Get rid of this; use navigationModel.register
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

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private void debugTrackRowSetListener()
	{
		if (defLookup(DebugRowSetListener.class) == null)
			return;
		if (getRowSet() == null)
			return;
		getRowSet().addRowSetListener(new RowSetListener()
		{
			@Override
			public void rowSetChanged(RowSetEvent event)
			{
				System.err.println("DEBUG LISTENER: rowSetChanged: " + getBoundColumnName());
			}

			@Override
			public void rowChanged(RowSetEvent event)
			{
				System.err.println("DEBUG LISTENER: rowChanged: " + getBoundColumnName());
			}

			@Override
			public void cursorMoved(RowSetEvent event)
			{
				System.err.println("DEBUG LISTENER: cursorMoved: " + getBoundColumnName());
			}
		});
	}

	/**
	 * Used by an SSComponent when making a change to the database.
	 * Typically used by a component listener. It avoids extra RowSet events.
	 * @param r code that changes the database
	 */
	void dbChange(Runnable r)
	{
			if (!checkRowOK())
				return;
			NavigationModel.startNavigationEvent(getNavigationModel(), getSSComponent());
			removeRowSetListener();
			try {
				r.run();
			} finally {
				addRowSetListener();
				NavigationModel.finishNavigationEvent(getNavigationModel());
			}
	}
	
	/**
	 * Indicates if the components RowSet listener is added/enabled
	 *
	 * @return true if the components RowSet listener is added/enabled, otherwise false
	 */
	boolean isRowSetListenerAdded() {
		return rowSetListenerAdded;
	}

	/**
	 * Method to add the RowSet listener.
	 */
	private void addRowSetListener()
	{
		if (rowSetListener==null) {
			rowSetListener = new SSRowSetListener();
		}
		if (!rowSetListenerAdded && rowSet!=null) {
			rowSet.addRowSetListener(rowSetListener);
			rowSetListenerAdded = true;
			logger.log(DEBUG, () -> sf("%s - RowSet Listener added.", getColumnForLog()));
		}
	}

	/**
	 * Method to remove the RowSet listener.
	 */
	private void removeRowSetListener()
	{
		// rowSetListenerAdded==true indicates that rowset is not null, and we
		// do not let the user call setRowSet(null), so not checking
		if (rowSetListenerAdded) {
			rowSet.removeRowSetListener(rowSetListener);
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
			logger.log(DEBUG, () -> sf("%s - Component Listener added.", getColumnForLog()));
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
			logger.log(DEBUG, () -> sf("%s - Component Listener removed.", getColumnForLog()));
		}
	}

	EventListener getEventListener()
	{
		return eventListener;
	}


	/**
	 * Updates the SSComponent with a valid RowSet and Column (Name or Index)
	 */
	private void bind()
	{
		verifyInitialized();
		if (eventListener==null) {
			eventListener = getSSComponent().getSSComponentHook().getSSComponentListener();
		}

		// By default, if bind fails or database error,
		// isNullable metadata is unknown.
		isNullable = Optional.empty();

		// TODO consider updating Component to null/zero/empty string if not valid column name, column index, or rowset
		
		// CHECK FOR NULL COLUMN/ROWSET
		if (((boundColumnName == null) && (boundColumnIndex == NO_COLUMN_INDEX)) || (rowSet == null)) {
			logger.log(WARNING, () -> sf("Binding failed: column name=%s, column index=%s%s.",
					boundColumnName, boundColumnIndex, rowSet==null ? ", rowset=null" : ""));
			return;
		}

		logger.log(TRACE, () -> sf("Column bind succeeded: name=%s, index=%d %s.",
				boundColumnName, boundColumnIndex, rowSet==null ? ", rowset=null" : ""));

		//
		// This is used a lot, just get it now.
		// If doing this lazy elsewhere, flush the cache here.

		isNullable = RowSetOps.isNullable(rowSet, boundColumnIndex);
		logger.log(TRACE, () -> sf("Column isNullable: %s.", isNullable));

		// Provide notification of a change in metadata
		ssComponent.metadataChange();

		// UPDATE COMPONENT
		// For an SSDBComboBox, we have likely not yet called execute to populate the
		// combo lists so the text for the first record will be blank, but
		// updateSSComponent() for SSDBComboBox checks for a null list and returns.
		updateSSComponent();
	}

	/**
	 * Takes care of setting RowSet and Column Index for ssCommon and then calls
	 * bind() to update Component;
	 *
	 * @param rowSet         datasource to be used
	 * @param boundColumnIndex index of the column to which this check box should
	 *                          be bound
	 */
	void bind(RowSet rowSet, int boundColumnIndex)
	{
		verifyInitialized();
		// Indicate that we're updating the bindings.
		inBinding = true;
		try {
			
			// TODO: WORK WITH A NavigationModel
			// Update rowset.
			removeRowSetListener();
			setRowSet(rowSet);
			addRowSetListener();
			
			// STORE COLUMN INDEX & NAME
			setBoundColumnIndex(boundColumnIndex);
			
			// INDICATE THAT WE'RE DONE SETTING THE BINDINGS
			inBinding = false;
			
			// UPDATE THE COMPONENT
			bind();
		} finally {
			inBinding = false;
		}
	}

	/**
	 * Takes care of setting RowSet and Column Name for ssCommon and then calls
	 * bind() to update Component;
	 *
	 * @param _rowSet        datasource to be used
	 * @param _boundColumnName name of the column to which this check box should be
	 *                         bound
	 */
	void bind(RowSet _rowSet, String _boundColumnName)
	{
		try {
			bind(_rowSet, RowSetOps.getColumnIndex(_rowSet, _boundColumnName));
		} catch (SQLException se) {
			logger.log(ERROR, "[" + _boundColumnName + "] - Failed to retrieve column index while binding.", se);
		}
	}

	/**
	 * Retrieves the allowNull flag for the bound database column.
	 * If setAllowNull() is not set, then the database metadata is used
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
			if (NavigateActions.hasActiveRow(getSSComponent())) {
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
			if (NavigateActions.hasActiveRow(ssComponent)) {
				// if (NavigateActions.ENABLE_UNDO_REDO) {
				// 	value = RowSetOps.getColumnObjectText(ssComponent);
				// } else {
				// 	value = RowSetOps.getColumnText(getSSComponent());
				// }
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
		// return boundColumnType;
		return boundColumnJDBCType.getVendorTypeNumber();
	}

	/**
	 * Returns the bound column name in square brackets.
	 *
	 * @return the boundColumnName in square brackets
	 */
	String getColumnForLog() {
		return "[" + (boundColumnName != null ? boundColumnName : logColumnName) + "]";
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
		return rowSet;
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
	 * @param _allowNull flag to indicate if the bound database column can be null
	 */
	void setAllowNull(Boolean _allowNull) {
		allowNull = _allowNull == null ? Optional.empty() : Optional.of(_allowNull);
	}

	/**
	 * Sets the column index to which the Component is to be bound.
	 *
	 * @param _boundColumnIndex column index to which the Component is to be bound
	 */
	void setBoundColumnIndex(int _boundColumnIndex) {

		// SET COLUMN INDEX
		if (_boundColumnIndex > NO_COLUMN_INDEX) {
			boundColumnIndex = _boundColumnIndex;
		} else {
			boundColumnIndex = NO_COLUMN_INDEX;
		}

		// DETERMINE COLUMN NAME AND TYPE
		try {
			// IF COLUMN INDEX IS VALID, GET COLUMN NAME, OTHERWISE SET TO NULL
// TODO Update RowSet to return constant or throw Exception if invalid/out of bounds.
			int boundColumnType;
			if (boundColumnIndex != NO_COLUMN_INDEX) {
				boundColumnName = RowSetOps.getColumnName(getRowSet(),boundColumnIndex);
				boundColumnType = RowSetOps.getColumnType(getRowSet(),boundColumnIndex);
			} else {
				boundColumnName = null;
				boundColumnType = java.sql.Types.NULL;
			}
			boundColumnJDBCType = JDBCType.valueOf(boundColumnType);

		} catch (SQLException se) {
			logger.log(ERROR, getColumnForLog() + " - SQL Exception.", se);
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
	void setBoundColumnName(String _boundColumnName) {

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
// TODO Update RowSet to return constant or throw Exception if invalid/out of bounds.
			int boundColumnType;
			if (boundColumnName != null) {
				//boundColumnIndex = getRowSet().getColumnIndex(boundColumnName);
				//boundColumnType = getRowSet().getColumnType(boundColumnIndex);
				boundColumnIndex = RowSetOps.getColumnIndex(getRowSet(),boundColumnName);
				boundColumnType = RowSetOps.getColumnType(getRowSet(),boundColumnIndex);
			} else {
				boundColumnIndex = NO_COLUMN_INDEX;
				boundColumnType = java.sql.Types.NULL;
			}
			boundColumnJDBCType = JDBCType.valueOf(boundColumnType);

		} catch (SQLException se) {
			logger.log(ERROR, getColumnForLog() + " - SQL Exception.", se);
		}

		// BIND UPDATED COLUMN IF APPLICABLE
		if (!inBinding) {
			bind();
		}
	}

	/**
	 * Name/text to display in log messages if boundColumnName is not set.
	 * @param _logColumnName text
	 */
	void setLogColumnName(String _logColumnName) {
		Objects.requireNonNull(_logColumnName);
		logColumnName = _logColumnName;
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
	 * @param _boundColumnArray Array to write to bound database column
	 * @throws SQLException thrown if there is a problem writing the array to the
	 *                      RowSet
	 */
	// TODO: SHOULD IT RETURN AN ERROR LIKE setBoundColumnText?
	void setBoundColumnArray(SSArray _boundColumnArray) throws SQLException {
		logger.log(DEBUG, () -> sf("%s: %s", getColumnForLog(), _boundColumnArray));
		boolean is_error = true;
		try {
			RowSetOps.updateColumnArray(getSSComponent(), _boundColumnArray);
			is_error = false;
		} catch(SQLException ex) {
			userErrorReporting(_boundColumnArray, ex);
		} finally {
			if (is_error)
				postRowSetModifiedError(getSSComponent(), _boundColumnArray);
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
		case SSSQLNullException x -> { Objects.isNull(x);
			ex_title = "Null Exception";
			ex_msg = "Null values are not allowed for " + getBoundColumnName();
		}
		case SQLException x -> { Objects.isNull(x);
			ex_title = "SQL Exception";
			ex_msg = "SQL Exception encountered for " + getBoundColumnName();
		}
		case NumberFormatException x -> { Objects.isNull(x);
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
	 * Sets the Connection to the database
	 *
	 * @param _connection the connection to set
	 */
	void setConnection(Connection _connection) {
		connection = _connection;
	}

	/**
	 * Returns the Connection to the database
	 *
	 * @return the connection
	 */
	Connection getConnection() {
		return connection;
	}

	/**
	 * Sets the RowSet to which the Component is bound.
	 *
	 * @param _rowSet RowSet to which the component is bound
	 */
	// TODO: Fix this, and getNavigationModel to use model.
	void setRowSet(RowSet _rowSet) {
		Objects.requireNonNull(_rowSet);
		rowSet = _rowSet;
		if (!inBinding) {
			bind();
		}
		debugTrackRowSetListener();
		if (navigationModel == null)
			navigationModel = new NavigationModel(rowSet);
		if (navigationModel.getRowSet() != _rowSet)
			navigationModel.setRowSet(rowSet);
	}

	// TODO: STATIC, for initial testing.
	private static NavigationModel navigationModel;
	private NavigationModel getNavigationModel() {
		if (navigationModel == null) {
			navigationModel = rowSet != null
					? new NavigationModel(rowSet) : NavigationModel.getDummy(rowSet);
		}
		return navigationModel;
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
				NavigateActions.undoRedo(comp, UndoRedo.UNDO);
			}
		});
		am.put(REDO_ACTION_KEY, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				NavigateActions.undoRedo(comp, UndoRedo.REDO);
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
		if (!NavigateActions.isUndoRedoEnabled(ssComponent))
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

		// TODO: NavigationModel UNDO/REDO
		if (NavigationModel.ENABLED)
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
				return NavigateActions.hasActiveRow(getSSComponent());
			} catch (SQLException ex) {
				return false;
			}

		doingCheckRowOK = true;
		try {
			try {
				if (NavigateActions.hasActiveRow(getSSComponent()))
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
	// TODO: Cleanup issueRowChanged for NavigationModel
	//       Want to run updateCompent as through from a RowSetListener.
	//       Why not call it directly?
	void issueRowChanged() {
		if(isRowSetListenerAdded()) {
			// TODO: could create a valid event, but since it is not used...
			rowSetListener.rowChanged(null);
		}
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
