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
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;

import com.nqadmin.swingset.decorators.BorderDecorator;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import java.lang.System.Logger;

import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.SSBaseComboBox;
import com.nqadmin.swingset.SSCheckBox;
import com.nqadmin.swingset.SSImage;
import com.nqadmin.swingset.SSLabel;
import com.nqadmin.swingset.SSList;
import com.nqadmin.swingset.SSSlider;
import com.nqadmin.swingset.datasources.DateTime;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.datasources.SSSQLInternalException;
import com.nqadmin.swingset.datasources.SSSQLNullException;
import com.nqadmin.swingset.formatting.SSFormattedTextField;
import com.nqadmin.swingset.decorators.Decorator;
import com.nqadmin.swingset.decorators.Validator;
import com.nqadmin.swingset.formatting.SSMaskFormatterFactory.SSMaskFormatter;
import com.nqadmin.swingset.navigate.NavigateActions;
import com.nqadmin.swingset.navigate.NavigateActions.UndoRedo;
import com.nqadmin.swingset.navigate.RowSetState;

import static com.nqadmin.swingset.navigate.RowSetState.isAcceptingChanges;
import static com.nqadmin.swingset.navigate.Utils.postRowSetModifiedError;
import static com.nqadmin.swingset.utils.SSUtils.sf;

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
public class SSCommon
{
	// TODO: get rid of this.
	private static final Boolean DISABLE_GENERAL_VALIDATION = false;

	/**
	 * Document listener provided for convenience for SwingSet Components that are
	 * based on JTextComponents. SwingSet components that need a Document listener
	 * to trigger a change to the bound RowSet should return an instance of
	 * SSCommonDocumentListener() when implementing the abstract method
	 * getSSComponentListener().
	 * <p>
	 * A typical implementation might look like: {@code
	 * 	return getSSCommon().getSSDocumentListener();
	 * }
	 * <p>
	 * This listener updates the underlying RowSet when there is a change to the Document
	 * object. E.g., a call to setText() on a JTextField.
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
	public class SSDocumentListener implements DocumentListener
	{
		/**
		 * variables needed to consolidate calls to removeUpdate() and insertUpdate()
		 * from DocumentListener
		 */
		private int lastChange = 0;
		private int lastNotifiedChange = 0;
		private String previousValue = null;
		/** True when listener is temporarily removed. */
		private boolean listenerNeedsRestoration;
	

		/** {@inheritDoc} */
		@Override
		public void changedUpdate(final DocumentEvent de) {
			lastChange++;
			logger.log(TRACE, () -> sf("%s - changedUpdate(): lastChange=%s, lastNotifiedChange=%s",
					getColumnForLog(), lastChange, lastNotifiedChange));
			
			// Delay execution of logic until all listener methods are called for current event
			// See: https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
			SwingUtilities.invokeLater(() -> {
				if (lastNotifiedChange != lastChange) {
					lastNotifiedChange = lastChange;

					String text = ((JTextComponent) getSSComponent()).getText();

					if (!isValidChange()) {
						postRowSetModifiedError(getSSComponent(), text);
						return;
					}

					removeRowSetListener();

					boolean ok = true;
					try {
						ok = setBoundColumnText(text);
					} finally {
						if (!ok) {
							// TODO:
							// How about moving a state tracking variable to
							// add/removeSSDocumentListener()?
							// addSSDocumentListener() then uses listenerNeedsRestoration.

							// restore previous text value
							if(previousValue != null) {
								if (ssComponentListenerAdded) {
									// avoid generating events while restoring text
									removeSSDocumentListener();
									listenerNeedsRestoration = true;
								}
								try {
									logger.log(DEBUG, () -> sf("%s: restoring previous value '%s'", getColumnForLog(), previousValue));
									((JTextComponent) getSSComponent()).setText(previousValue);
								} finally {
									if (listenerNeedsRestoration) {
										listenerNeedsRestoration = false;
										addSSDocumentListener();
									}
								}
							}
						}
						previousValue = null;	// Seems safer, is this the right spot?
						addRowSetListener();
					}
				}
			});
		}

		/** {@inheritDoc} */
		@Override
		public void insertUpdate(final DocumentEvent de) {
			logger.log(TRACE, () -> sf("%s - insertUpdate().", getColumnForLog()));
			changedUpdate(de);
		}

		/** {@inheritDoc} */
		@Override
		public void removeUpdate(final DocumentEvent de) {
			logger.log(TRACE, () -> sf("%s - removeUpdate().", getColumnForLog()));
			changedUpdate(de);
		}

	} // end protected class SSDocumentListener
	
	/**
	 * For JTextField to track previous text field value.
	 */
	@SuppressWarnings("serial")
	public class SSPlainDocument extends PlainDocument {
		DocumentFilter filter;

		void capturePrevious(DocumentFilter.FilterBypass fb) {
			try {
				String prev = fb.getDocument().getText(0, fb.getDocument().getLength());
				logger.log(TRACE, () -> "Capture previous text value: " + prev);
				if (eventListener instanceof SSDocumentListener listener) {
					listener.previousValue = prev;
				}
			} catch (BadLocationException ex) {
				logger.log(DEBUG, "Capture previous text value", ex);
			}
		}

		/** {@inheritDoc} */
		@Override
		public DocumentFilter getDocumentFilter() {
			if (filter == null) {
				filter = new DocumentFilter() {
					@Override
					public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
						capturePrevious(fb);
						super.replace(fb, offset, length, text, attrs);
					}

					@Override
					public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
						capturePrevious(fb);
						super.insertString(fb, offset, string, attr);
					}

					@Override
					public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
						capturePrevious(fb);
						super.remove(fb, offset, length);
					}
				};
			}
			return filter;
		}
	}

	/**
	 * Listener(s) for the underlying RowSet used to update the bound SwingSet
	 * component. When working with a {@linkplain javax.sql.rowset.CachedRowSet} there are
	 * extra steps involved which require the listener to ignore some events,
	 * see {@link RowSetState#acceptChanges(javax.sql.rowset.CachedRowSet, java.lang.Runnable) }.
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
		public void cursorMoved(final RowSetEvent event) {
			if (isAcceptingChanges(rowSet)) { // only possible if CachedRowSet
				return;
			}
			logger.log(TRACE, () -> sf("%s - RowSet cursor moved.", getColumnForLog()));
			performUpdateSSComponent();
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
			if (isAcceptingChanges(rowSet)) { // only possible if CachedRowSet
				return;
			}
			logger.log(TRACE, () -> sf("%s - RowSet row changed.", getColumnForLog()));
//			if (!getRowSet().isUpdatingRow()) {
//				updateSSComponent();
//			}
			performUpdateSSComponent();
		}

		/**
		 * When the RowSet is modified we want to trigger a change to the bound
		 * Component display/value.
		 */
		@Override
		public void rowSetChanged(final RowSetEvent event) {
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

					updateSSComponent();
				}
			});
		}

	} // end protected class SSRowSetListener

	/**
	 * Log4j Logger for component
	 */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Constant to indicate that no RowSet column index has been specified.
	 */
	public static final int NO_COLUMN_INDEX = -1;
	
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
	 * Constructor expecting a SwingSet component as an argument (usually called as
	 * = new SSCommon(this);)
	 *
	 * @param _ssComponent SwingSet component having this SSCommon instance as a
	 *                     datamember
	 */
	@SuppressWarnings("OverridableMethodCallInConstructor")
	public SSCommon(final SSComponentInterface _ssComponent) {
		ssComponent = _ssComponent;
		decorator = Decorator.nullDecorator;
		validator = Validator.nullValidator;
		initDecorator();
		init();
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private void debugTrackRowSetListener()
	{
		if (Boolean.TRUE)
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
	 * Method to add a document listener when the SwingSet component is a
	 * JTextComponent.
	 */
	private void addSSDocumentListener() {
		((JTextComponent) getSSComponent()).getDocument().addDocumentListener((SSDocumentListener)eventListener);
	}

	/**
	 * Method to add the RowSet listener.
	 */
	public void addRowSetListener() {
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
	 * Method to add any SwingSet Component listener(s).
	 */
	//
	// TODO: Create SSComponent.addListener()/removeListener.
	//       Then get rid of this switch statement.
	//
	public final void addSSComponentListener() {
		
		// Probably should not have a null eventListener here, but just in case
		if (eventListener==null) {
			return;
		}
		
		if (!ssComponentListenerAdded) {
			
			ssComponentListenerAdded = true;
			/* JDK-21 and preview for "_" */
			switch(ssComponent) {
			case SSCheckBox c -> c.addItemListener((ItemListener) eventListener);
			case SSBaseComboBox<?,?,?> c -> c.addActionListener((ActionListener) eventListener);
			case SSImage c -> c.getBtnUpdateImage().addActionListener((ActionListener) eventListener);
			case SSLabel c -> c.addPropertyChangeListener("text", ((PropertyChangeListener) eventListener));
			case SSList c -> c.addListSelectionListener((ListSelectionListener) eventListener);
			case SSSlider c -> c.addChangeListener((ChangeListener) eventListener);
			case SSFormattedTextField c -> c.addPropertyChangeListener("value", ((PropertyChangeListener) eventListener));
			case JTextComponent x -> addSSDocumentListener();

			default -> {
				// DIPLAY WARNING FOR UNKNOWN EVENT LISTENER
				String message = sf("%s - Encountered unknown Component Event Listener for: %s. Unable to add component listener.",
						getColumnForLog(), ssComponent.getClass().getSimpleName());
				logger.log(ERROR, message);
				JOptionPane.showMessageDialog((JComponent)getSSComponent(), message, "Unknown Component Event Listener", JOptionPane.ERROR_MESSAGE);
				
				// INDICATE FAILURE TO ADD LISTENER
				ssComponentListenerAdded = false;
			}
			}
		}

		if (ssComponentListenerAdded) {
			logger.log(DEBUG, () -> sf("%s - Component Listener added.", getColumnForLog()));
		}
	}


	/**
	 * Updates the SSComponent with a valid RowSet and Column (Name or Index)
	 */
	private void bind() {
		
		if (eventListener==null) {
			eventListener = getSSComponent().getSSComponentListener();
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
		// combo lists so the text for the first record will be blank, but updateSSComponent() for
		// SSDBComboBox checks for a null list and returns.
		updateSSComponent();
	}

	/**
	 * Takes care of setting RowSet and Column Index for ssCommon and then calls
	 * bind() to update Component;
	 *
	 * @param _rowSet         datasource to be used
	 * @param _boundColumnIndex index of the column to which this check box should
	 *                          be bound
	 */
	public void bind(final RowSet _rowSet, final int _boundColumnIndex) {// throws java.sql.SQLException {
		// INDICATE THAT WE'RE UPDATING THE BINDINGS
		inBinding = true;

		// UPDATE ROWSET
		removeRowSetListener();
		setRowSet(_rowSet);
		addRowSetListener();

		// STORE COLUMN INDEX & NAME
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
	 * @param _rowSet        datasource to be used
	 * @param _boundColumnName name of the column to which this check box should be
	 *                         bound
	 */
	public void bind(final RowSet _rowSet, final String _boundColumnName)
	{ // throws java.sql.SQLException {
		try {
			bind(_rowSet, RowSetOps.getColumnIndex(_rowSet, _boundColumnName));
		} catch (final SQLException se) {
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
	public boolean getAllowNull() {
		return allowNull.orElseGet(() -> isNullable.orElse(true));
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
	 * Returns an Object of the specified type
	 * representing the value in the bound database column.
	 * <p>
	 * Note a null is never converted into ""; use getBoundColumnText for that.
	 * @param <T> type to return
	 * @param type Class of returned type
	 * @return value
	 */
	public <T> T getBoundColumnObject(Class<T> type)
	{
		T value = null;

		try {
			if (getRowSet().getRow() != 0) {
				value = RowSetOps.getColumnObject(ssComponent, type);
			}
		} catch (final SQLException se) {
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
	public String getBoundColumnText() {

// TODO Consider checking for a null RowSet. This would be the case for an unbound SSDBComboBox used for navigation.

		String value = "";

		try {
			if (getRowSet().getRow() != 0) {
				if (NavigateActions.ENABLE_UNDO_REDO) {
					value = RowSetOps.getColumnObjectText(ssComponent);
				} else {
					value = RowSetOps.getColumnText(getSSComponent());
				}
				if (!getAllowNull() && (value == null)) {
					value = "";
				}
			}
		} catch (final SQLException se) {
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
	public int getBoundColumnType() {
		// return boundColumnType;
		return boundColumnJDBCType.getVendorTypeNumber();
	}

	/**
	 * Returns the bound column name in square brackets.
	 *
	 * @return the boundColumnName in square brackets
	 */
	public String getColumnForLog() {
		return "[" + (boundColumnName != null ? boundColumnName : logColumnName) + "]";
	}

	/**
	 * @return the parent/calling SwingSet JComponent implementing
	 *         SSComponentInterface
	 */
	public SSComponentInterface getSSComponent() {
		return ssComponent;
	}

	/**
	 * Returns the Connection to the database
	 *
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * Returns SSDocumentListener; assumes the component is a JTextComponent.
	 * <p>
	 * Should only be called once per component.
	 *
	 * @return SSDocumentListener for a JTextComponent
	 */
	public SSDocumentListener getSSDocumentListener() {
		// TODO: assert not called before
		return new SSDocumentListener();
	}

	/**
	 * Returns the RowSet to which the SwingSet component is bound.
	 *
	 * @return RowSet to which the SwingSet component is bound
	 */
	public RowSet getRowSet() {
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
	 * Indicates if the components RowSet listener is added/enabled
	 *
	 * @return true if the components RowSet listener is added/enabled, otherwise false
	 */
	public final boolean isRowSetListenerAdded() {
		return rowSetListenerAdded;
	}

	/**
	 * Indicates if the components value change listener is currently added/enabled
	 *
	 * @return true if the components value change listener is added/enabled, otherwise false
	 */
	public final boolean isSSComponentListenerAdded() {
		return ssComponentListenerAdded;
	}
	
	/**
	 * Method to add any SwingSet Component listener(s).
	 */
	public final void removeSSComponentListener() {
		
		// Probably should not have a null eventListener here, but just in case
		if (eventListener==null) {
			return;
		}
		
		if (ssComponentListenerAdded) {
			
			ssComponentListenerAdded = false;
			/* JDK-21 and preview for "_" */
			switch(ssComponent) {
			case SSCheckBox c -> c.removeItemListener((ItemListener) eventListener);
			case SSBaseComboBox<?,?,?> c -> c.removeActionListener((ActionListener) eventListener);
			case SSImage c -> c.getBtnUpdateImage().removeActionListener((ActionListener) eventListener);
			case SSLabel c -> c.removePropertyChangeListener("text", ((PropertyChangeListener) eventListener));
			case SSList c -> c.removeListSelectionListener((ListSelectionListener) eventListener);
			case SSSlider c -> c.removeChangeListener((ChangeListener) eventListener);
			case SSFormattedTextField c -> c.removePropertyChangeListener("value", ((PropertyChangeListener) eventListener));
			case JTextComponent x -> removeSSDocumentListener();
			default -> {
				// DIPLAY WARNING FOR UNKNOWN EVENT LISTENER
				String message = sf("%s - Encountered unknown Component Event Listener for: %s. Unable to remove component listener.",
						getColumnForLog(), ssComponent.getClass().getSimpleName());
				logger.log(ERROR, message);
				JOptionPane.showMessageDialog((JComponent)getSSComponent(), message, "Unknown Component Event Listener", JOptionPane.ERROR_MESSAGE);
				
				// INDICATE FAILURE TO REMOVE LISTENER
				ssComponentListenerAdded = true;
			}
			}
		}
		if (!ssComponentListenerAdded) {
			logger.log(DEBUG, () -> sf("%s - Component Listener removed.", getColumnForLog()));
		}
	}

	/**
	 * Method to remove a Document listener when the SwingSet component is a
	 * JTextComponent.
	 */
	private void removeSSDocumentListener() {
		((JTextComponent) getSSComponent()).getDocument().removeDocumentListener((SSDocumentListener)eventListener);
	}

	/**
	 * Method to remove the RowSet listener.
	 */
	public final void removeRowSetListener() {
		// rowSetListenerAdded==true indicates that rowset is not null and we do not let the user call setRowSet(null), so not checking
		// rowSetListenerAdded==true indicates that rowSetListener is not null, so not checking
		if (rowSetListenerAdded) {
			rowSet.removeRowSetListener(rowSetListener);
			rowSetListenerAdded = false;
			logger.log(DEBUG, () -> sf("%s - RowSet Listener removed.", getColumnForLog()));
		}
	}

	/**
	 * Sets the allowNull flag for the bound database column, this
	 * overrides the database metadata for isNullable. Set to null
	 * to use the database metadata.
	 *
	 * @param _allowNull flag to indicate if the bound database column can be null
	 */
	public void setAllowNull(final Boolean _allowNull) {
		allowNull = _allowNull == null ? Optional.empty() : Optional.of(_allowNull);
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
// TODO Update RowSet to return constant or throw Exception if invalid/out of bounds.
			int boundColumnType;
			if (boundColumnIndex != NO_COLUMN_INDEX) {
				//boundColumnName = getRowSet().getColumnName(boundColumnIndex);
				//boundColumnType = getRowSet().getColumnType(boundColumnIndex);
				boundColumnName = RowSetOps.getColumnName(getRowSet(),boundColumnIndex);
				boundColumnType = RowSetOps.getColumnType(getRowSet(),boundColumnIndex);
			} else {
				boundColumnName = null;
				boundColumnType = java.sql.Types.NULL;
			}
			boundColumnJDBCType = JDBCType.valueOf(boundColumnType);

		} catch (final SQLException se) {
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

		} catch (final SQLException se) {
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
	public void setLogColumnName(final String _logColumnName) {
		Objects.requireNonNull(_logColumnName);
		logColumnName = _logColumnName;
	}

	/**
	 * Name/text to display in log messages if boundColumnName is not set.
	 * @return text for log entries, null if never set
	 */
	public String getLogColumnName() {
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
	public void setBoundColumnArray(final SSArray _boundColumnArray) throws SQLException {
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
	 * Updates the bound database column with the specified String.
	 *
	 * @param _boundColumnObject value to write to bound database column
	 * @return true if no error
	 */
	public boolean setBoundColumnObject(final Object _boundColumnObject) {
		logger.log(DEBUG, () -> sf("%s: %s", getColumnForLog(), _boundColumnObject));
		boolean ok = false;
		try {
			RowSetOps.updateColumnObject(getSSComponent(), _boundColumnObject);
			ok = true;
		} catch(SQLException | NumberFormatException ex) {
			userErrorReporting(_boundColumnObject, ex);
		} finally {
			if (!ok)
				postRowSetModifiedError(getSSComponent(), _boundColumnObject);
		}
		return ok;
	}

	/**
	 * Updates the bound database column with the specified String.
	 *
	 * @param _boundColumnText value to write to bound database column
	 * @return true if no error
	 */
	public boolean setBoundColumnText(final String _boundColumnText) {
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
		case SSSQLNullException x -> {
			ex_title = "Null Exception";
			ex_msg = "Null values are not allowed for " + getBoundColumnName();
		}
		case SQLException x -> {
			ex_title = "SQL Exception";
			ex_msg = "SQL Exception encountered for " + getBoundColumnName();
		}
		case NumberFormatException x -> {
			ex_title = "Number Format Exception";
			ex_msg = "Number Format Exception encountered for " + getBoundColumnName() + " converting " + value + " to a number.";
		}
		default -> {}
		}
		logger.log(WARNING, getBoundColumnName() + " - " + ex_title + ".", ex);
		JOptionPane.showMessageDialog((JComponent)getSSComponent(), ex_msg,
									  ex_title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Sets the Connection to the database
	 *
	 * @param _connection the connection to set
	 */
	public void setConnection(final Connection _connection) {
		connection = _connection;
	}

	/**
	 * Sets the RowSet to which the Component is bound.
	 *
	 * @param _rowSet RowSet to which the component is bound
	 */
	public void setRowSet(final RowSet _rowSet) {
		Objects.requireNonNull(_rowSet);
		rowSet = _rowSet;
		if (!inBinding) {
			bind();
		}
		debugTrackRowSetListener();
	}

	/**
	 * Transfers focus to next Swing Component on the screen when either
	 * Shift-Down-Arrow or Enter are pressed; previous is Shift-Up-Arrow.
	 * 
	 * @param jc configure this JComponent
	 */
	public static void configureTraversalKeys(JComponent jc)
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
	public static void setupUndoRedoKeys(SSComponentInterface comp)
	{
		if (!NavigateActions.ENABLE_UNDO_REDO)
			return;
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
				try {
					NavigateActions.undoRedo(comp, UndoRedo.UNDO);
				} catch (SQLException ex) {
					logger.log(ERROR, "UNDO action:", ex);
				}
			}
		});
		am.put(REDO_ACTION_KEY, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try {
					NavigateActions.undoRedo(comp, UndoRedo.REDO);
				} catch (SQLException ex) {
					logger.log(ERROR, "REDO action:", ex);
				}
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
	public void undoRedoUpdateObject(UndoRedo cmd, Object value) throws SQLException
	{
		if (!NavigateActions.ENABLE_UNDO_REDO)
			throw new IllegalStateException("UNDO/REDO disabled");
		logger.log(DEBUG, () -> sf("%s: %s", cmd, value));
		// TODO: put following in RowSetOps
		// NOTE: following does not generate any events
		getRowSet().updateObject(getBoundColumnIndex(), value);
		// NOTE: Previous line sets value in RowSet's pending update.
		//		 Following line causes updateSSComponent which gets the
		//		 value to display from undoRedo; the update values
		//		 can not always be reliably read JdbcRowSet vs CachedRowSet.
		issueRowChanged();
	}

	/**
	 * Method used by RowSet listeners to update the bound SwingSet component.
	 * <p>
	 * Handles removal of Component listener before update and addition of listener
	 * after update.
	 */
	public void updateSSComponent() {
		
		// If you see this in the logs back to back for the same component
		// a listener is likely not handled properly.
		// Maybe incorporate SwingUtilities.invokeLater()? 
		logger.log(TRACE, () -> sf("Updating component %s.", getColumnForLog()));
		
		removeSSComponentListener();
		ssComponent.updateSSComponent();
		addSSComponentListener();
		decorate();
	}
	/**
	 * Issue a row changed event if there's an active RowSetListener.
	 */
	public void issueRowChanged() {
		if(rowSetListenerAdded) {
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

		// Only decorate/validate for SSFormattedTextField, as in v4.0.12.
		if (!(getSSComponent() instanceof SSFormattedTextField)
				&& DISABLE_GENERAL_VALIDATION) {
			return;
		}

		setDecorator(getSSComponent().createDefaultDecorator());
	}
	/**
	 * Run the decorator.
	 */
	public final void decorate() {
		decorator.decorate();
	}

	/**
	 * Install the given decorator.
	 * @param deco decorator to install
	 */
	public final void setDecorator(Decorator deco) {
		decorator.uninstall();
		deco.install(getSSComponent());
		decorator = deco;
	}

	/**
	 * Return the decorator used by this component.
	 * @return the decorator
	 */
	public final Decorator getDecorator() {
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
	public final void setValidator(Validator _validator) {
		if (DISABLE_GENERAL_VALIDATION) {
			return;
		}
		validator.uninstall();
		_validator.install(ssComponent);
		validator = _validator;
	}

	/**
	 * Specialized component validation, like for dates/times.
	 * @return false if the component does not have valid data.
	 */
	// For now put this date/time code here.
	// TODO: add componentValidate() as a default method to SSComponentInterface
	// and move this to the date/time components.
	private boolean componentValidate()
	{
		// NOTE: does not change any state.

		SSComponentInterface comp = getSSComponent();
		if (DateTime.isHandledDateTimeComp(comp)) {
			if (!(comp instanceof JTextComponent jtc))
				throw new IllegalArgumentException("only JTextComponent handled");
			String text = jtc.getText();
			// If the component has an SSMaskFormatter and there's no data
			// then treat it as an empty string
			if (comp instanceof SSFormattedTextField ftf) {
				if (!ftf.containsUserText()) {
					text = "";
				}
			}
			if (!DateTime.dateTimeColumnValidate(text, comp)) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * Run the SSComponents validator, return the result.
	 * 
	 * @return true if valid
	 */
	public final boolean validate()
	{
		if (!componentValidate()) {
			return false;
		}

		// Now invoke the user's validator
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
	/**
	 * This is called per change, like on a keystroke, if allowed may decorate
	 * the component. 
	 * 
	 * @return true if the there is no detected error
	 */
	private boolean isValidChange() {
		if (DISABLE_GENERAL_VALIDATION) {
			return true;
		}

		// decorator does validation
		return decorator.decorate();
	}

}
