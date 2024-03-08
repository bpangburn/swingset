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

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSBaseComboBox;
import com.nqadmin.swingset.SSCheckBox;
import com.nqadmin.swingset.SSImage;
import com.nqadmin.swingset.SSLabel;
import com.nqadmin.swingset.SSList;
import com.nqadmin.swingset.SSSlider;
import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.formatting.SSFormattedTextField;

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
public class SSCommon implements Serializable {

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
	 * This listener updates the underlying RowSet there is a change to the Document
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
	public class SSDocumentListener implements DocumentListener, Serializable {

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

					removeRowSetListener();

					try {
						setBoundColumnText(((javax.swing.text.JTextComponent) getSSComponent()).getText());
					} finally {
						addRowSetListener();
					}
				}
			});
		}

		@Override
		public void insertUpdate(final DocumentEvent de) {
			logger.trace("{} - insertUpdate().", () -> getColumnForLog());
			changedUpdate(de);
		}

		@Override
		public void removeUpdate(final DocumentEvent de) {
			logger.trace("{} - removeUpdate().", () -> getColumnForLog());
			changedUpdate(de);
		}

	} // end protected class SSDocumentListener

	/**
	 * Listener(s) for the underlying RowSet used to update the bound SwingSet
	 * component.
	 */
	protected class SSRowSetListener implements RowSetListener, Serializable {
		/**
		 * unique serial ID
		 */
		private static final long serialVersionUID = -5194433723970625351L;
		
		/**
		 * variables needed to consolidate multiple calls
		 */
		private int lastChange = 0;
		private int lastNotifiedChange = 0;

		/**
		 * When the cursor moves we want to trigger a change to the bound Component
		 * display/value.
		 */
		@Override
		public void cursorMoved(final RowSetEvent event) {
			logger.trace("{} - RowSet cursor moved.", () -> getColumnForLog());
			//updateSSComponent();
			performUpdates();
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
			logger.trace("{} - RowSet row changed.", () -> getColumnForLog());
//			if (!getRowSet().isUpdatingRow()) {
//				updateSSComponent();
//			}
			performUpdates();
		}

		/**
		 * When the RowSet is modified we want to trigger a change to the bound
		 * Component display/value.
		 */
		@Override
		public void rowSetChanged(final RowSetEvent event) {
			logger.trace("{} - RowSet changed.", () -> getColumnForLog());
			//updateSSComponent();
			performUpdates();
		}
		

		private void performUpdates() {
			lastChange++;
			logger.trace("{} - performUpdates(): lastChange=" + lastChange
					+ ", lastNotifiedChange=" + lastNotifiedChange, () -> getColumnForLog());
			
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
	private static Logger logger = SSUtils.getLogger();

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
	 * Converts a date string in "[m]m/[d]d/yyyy" format to an SQL Date.
	 *
	 * @param _strDate date string in "[m]m/[d]d/yyyy" format
	 *
	 * @return return SQL date for the string specified
	 * @throws NoSuchElementException if there are no more tokens in this tokenizer's string
	 */
	public static Date getSQLDate(final String _strDate) {
		if (_strDate == null) {
			return null;
		}
		String strDate = _strDate.trim();
		if (strDate.isEmpty()) {
			return null;
		}
		if (strDate.contains("/")) {
			StringTokenizer strtok = new StringTokenizer(strDate, "/", false);
			String month = strtok.nextToken();
			String day = strtok.nextToken();
			strDate = strtok.nextToken() + "-" + month + "-" + day;
		}
		return Date.valueOf(strDate);
	}

	/**
	 * Convert argument to "m[m]/d[d]/yyyy" format.
	 * @param _date
	 * @return
	 */
	public static String getStringDate(Date _date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(_date);
		String strDate = "" + (calendar.get(Calendar.MONTH) + 1)
				+ "/" + calendar.get(Calendar.DAY_OF_MONTH)
				+ "/" + calendar.get(Calendar.YEAR);
		return strDate;
		
	}
	
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

	// /**
	//  * Column SQL data type.
	//  */
	// private int boundColumnType = java.sql.Types.NULL;

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
	public SSCommon(final SSComponentInterface _ssComponent) {
		setSSComponent(_ssComponent);
		init();
	}

//	/**
//	 * Convenience method to add both RowSet and SwingSet Component listeners.
//	 * <p>
//	 * Does not add DocumentListener.
//	 */
//	public void addListeners() {
//		addRowSetListener();
//		addSSComponentListener();
//	}

	/**
	 * Method to add a document listener when the SwingSet component is a
	 * JTextComponent.
	 */
	private void addSSDocumentListener() {
		((javax.swing.text.JTextComponent) getSSComponent()).getDocument().addDocumentListener((SSDocumentListener)eventListener);
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
			logger.debug("{} - RowSet Listener added.", () -> getColumnForLog());
		}
	}
	
	/**
	 * Method to add any SwingSet Component listener(s).
	 */
	public final void addSSComponentListener() {
		
		// Probably should not have a null eventListener here, but just in case
		if (eventListener==null) {
			return;
		}
		
		if (!ssComponentListenerAdded) {
			
			ssComponentListenerAdded = true;
			
			if (ssComponent instanceof SSCheckBox) {
				((SSCheckBox)ssComponent).addItemListener((ItemListener) eventListener);
			} else if (ssComponent instanceof SSBaseComboBox) {
				((SSBaseComboBox<?, ?, ?>)ssComponent).addActionListener((ActionListener) eventListener);
			} else if (ssComponent instanceof SSImage) {
				((SSImage)ssComponent).getBtnUpdateImage().addActionListener((ActionListener) eventListener);
			} else if (ssComponent instanceof SSLabel) {
				((SSLabel)ssComponent).addPropertyChangeListener("text", ((PropertyChangeListener) eventListener));
			} else if (ssComponent instanceof SSList) {
				((SSList)ssComponent).addListSelectionListener((ListSelectionListener) eventListener);
			} else if (ssComponent instanceof SSSlider) {
				((SSSlider)ssComponent).addChangeListener((ChangeListener) eventListener);
			} else if (ssComponent instanceof SSFormattedTextField) {
				((SSFormattedTextField)ssComponent).addPropertyChangeListener("value", ((PropertyChangeListener) eventListener));
//			} else if (ssComponent instanceof SSTextArea) {
//			} else if (ssComponent instanceof SSTextField) {
			} else if (ssComponent instanceof JTextComponent) {
				addSSDocumentListener();
			} else {
				// DIPLAY WARNING FOR UNKNOWN EVENT LISTENER
				String message = String.format("%s - Encountered unknown Component Event Listener for: %s. Unable to add component listener.",
						getColumnForLog(), ssComponent.getClass().getSimpleName());
				logger.error(message);
				JOptionPane.showMessageDialog((JComponent)getSSComponent(), message, "Unknown Component Event Listener", JOptionPane.ERROR_MESSAGE);
				
				// INDICATE FAILURE TO ADD LISTENER
				ssComponentListenerAdded = false;
			}
		}

		if (ssComponentListenerAdded) {
			logger.debug("{} - Component Listener added.", () -> getColumnForLog());
		}
	}

	/**
	 * Updates the SSComponent with a valid RowSet and Column (Name or Index)
	 */
	private void bind() {
		
		if (eventListener==null) {
			eventListener = getSSComponent().getSSComponentListener();
		}

		// Not sure of implications of bind fail,
		// set default in case case bind fails.
		// TODO: is this the right thing to do if bind fails?
		isNullable = Optional.empty();

		// TODO consider updating Component to null/zero/empty string if not valid column name, column index, or rowset
		
		// CHECK FOR NULL COLUMN/ROWSET
		if (((boundColumnName == null) && (boundColumnIndex == NO_COLUMN_INDEX)) || (rowSet == null)) {
			logger.warn("Binding failed: column name={}, column index={}{}.", ()->boundColumnName, ()->boundColumnIndex, ()->rowSet==null ? ", rowset=null" : "");
			return;
		}

		logger.trace(() -> String.format("Column bind succeeded: name=%s, index=%d %s.",
				boundColumnName, boundColumnIndex, rowSet==null ? ", rowset=null" : ""));

		//
		// This is used a lot, just get it now.
		// If doing this lazy elsewhere, flush the cache here.

		isNullable = RowSetOps.isNullable(rowSet, boundColumnIndex);
		logger.trace(() -> String.format("Column isNullable: %s.", isNullable));

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
	public void bind(final RowSet _rowSet, final String _boundColumnName) { // throws java.sql.SQLException {
		try {
			bind(_rowSet, RowSetOps.getColumnIndex(_rowSet, _boundColumnName));
		} catch (final SQLException se) {
			logger.error("[" + _boundColumnName + "] - Failed to retrieve column index while binding.", se);
		}
//		// INDICATE THAT WE'RE UPDATING THE BINDINGS
//		inBinding = true;
//
//		// UPDATE ROWSET
//		removeRowSetListener();
//		setRowSet(_rowSet);
//		addRowSetListener();
//
//		// UPDATE COLUMN NAME
//		setBoundColumnName(_boundColumnName);
//
//		// INDICATE THAT WE'RE DONE SETTING THE BINDINGS
//		inBinding = false;
//
//		// UPDATE THE COMPONENT
//		bind();

	}

	/**
	 * Retrieves the allowNull flag for the bound database column.
	 * If setAllowNull() is not set, then the database metadata is used
	 * to determine nullability; if unknown then return true;
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
				//value = getRowSet().getColumnText(getBoundColumnName());
				value = RowSetOps.getColumnText(getRowSet(),getBoundColumnName());
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
	 * Returns SSDocumentListener if the component is a JTextComponent
	 * <p>
	 * Should only be called once per component.
	 *
	 * @return SSDocumentListener for a JTextComponent
	 */
	public SSDocumentListener getSSDocumentListener() {
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

// BP_2021-02-11: Eliminating this method. Seems dangerous to provide access.
//	/**
//	 * Returns Listener for the RowSet bound to the database
//	 *
//	 * @return listener for the bound RowSet
//	 */
//	public SSRowSetListener getRowSetListener() {
//		return rowSetListener;
//	}

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

//	/**
//	 * Convenience method to remove both RowSet and SwingSet Component listeners.
//	 * <p>
//	 * Does not remove DocumentListener.
//	 */
//	public void removeListeners() {
//		removeRowSetListener();
//		removeSSComponentListener();
//	}
	
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
			
			if (ssComponent instanceof SSCheckBox) {
				((SSCheckBox)ssComponent).removeItemListener((ItemListener) eventListener);
			} else if (ssComponent instanceof SSBaseComboBox) {
				((SSBaseComboBox<?, ?, ?>)ssComponent).removeActionListener((ActionListener) eventListener);
			} else if (ssComponent instanceof SSImage) {
				((SSImage)ssComponent).getBtnUpdateImage().removeActionListener((ActionListener) eventListener);
			} else if (ssComponent instanceof SSLabel) {
				((SSLabel)ssComponent).removePropertyChangeListener("text", ((PropertyChangeListener) eventListener));
			} else if (ssComponent instanceof SSList) {
				((SSList)ssComponent).removeListSelectionListener((ListSelectionListener) eventListener);
			} else if (ssComponent instanceof SSSlider) {
				((SSSlider)ssComponent).removeChangeListener((ChangeListener) eventListener);
			} else if (ssComponent instanceof SSFormattedTextField) {
				((SSFormattedTextField)ssComponent).removePropertyChangeListener("value", ((PropertyChangeListener) eventListener));
//			} else if (ssComponent instanceof SSTextArea) {
//			} else if (ssComponent instanceof SSTextField) {
			} else if (ssComponent instanceof JTextComponent) {
				removeSSDocumentListener();
			} else {
				// DIPLAY WARNING FOR UNKNOWN EVENT LISTENER
				String message = String.format("%s - Encountered unknown Component Event Listener for: %s. Unable to remove component listener.",
						getColumnForLog(), ssComponent.getClass().getSimpleName());
				logger.error(message);
				JOptionPane.showMessageDialog((JComponent)getSSComponent(), message, "Unknown Component Event Listener", JOptionPane.ERROR_MESSAGE);
				
				// INDICATE FAILURE TO REMOVE LISTENER
				ssComponentListenerAdded = true;
			}
		}

		if (!ssComponentListenerAdded) {
			logger.debug("{} - Component Listener removed.", () -> getColumnForLog());
		}
	}

	/**
	 * Class to remove a Document listener when the SwingSet component is a
	 * JTextComponent
	 */
	private void removeSSDocumentListener() {
		((javax.swing.text.JTextComponent) getSSComponent()).getDocument().removeDocumentListener((SSDocumentListener)eventListener);
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
			logger.debug("{} - RowSet Listener removed.", () -> getColumnForLog());
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
	 * Updates the bound database column with the specified Array.
	 * <p>
	 * Used for SSList or other component where multiple items can be selected.
	 *
	 * @param _boundColumnArray Array to write to bound database column
	 * @throws SQLException thrown if there is a problem writing the array to the
	 *                      RowSet
	 */
	public void setBoundColumnArray(final SSArray _boundColumnArray) throws SQLException {
		getRowSet().updateArray(getBoundColumnName(), _boundColumnArray);
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
			logger.error(getColumnForLog() + " - SQL Exception.", se);
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
	 * Updates the bound database column with the specified String.
	 *
	 * @param _boundColumnText value to write to bound database column
	 */
	public void setBoundColumnText(final String _boundColumnText) {
		logger.debug("{}: " + _boundColumnText, () -> getColumnForLog());
		try {
			//getRowSet().updateColumnText(_boundColumnText, getBoundColumnName(), getAllowNull());
			RowSetOps.updateColumnText(getRowSet(),_boundColumnText, getBoundColumnName(), getAllowNull());
		} catch(final NullPointerException _npe) {
			logger.warn(getBoundColumnName() + " - Null Pointer Exception.", _npe);
			JOptionPane.showMessageDialog((JComponent)getSSComponent(),
					"Null values are not allowed for " + getBoundColumnName(), "Null Exception", JOptionPane.ERROR_MESSAGE);

		} catch(final SQLException _se) {
			logger.warn(getBoundColumnName() + " - SQL Exception.", _se);
			JOptionPane.showMessageDialog((JComponent)getSSComponent(),
					"SQL Exception encountered for " + getBoundColumnName(), "SQL Exception", JOptionPane.ERROR_MESSAGE);

		} catch(final NumberFormatException _pe) {
			logger.warn(getBoundColumnName() + " - Number Format Exception.", _pe);
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
	}

	/**
	 * Method used by RowSet listeners to update the bound SwingSet component.
	 * <p>
	 * Handles removal of Component listener before update and addition of listener
	 * after update.
	 */
	public void updateSSComponent() {
		
		// If you see this in the logs back to back for the same component a listener is likely
		// not handled properly. Maybe incorporate SwingUtilities.invokeLater()? 
		logger.trace("Updating component {}.", () -> getColumnForLog());
		
		removeSSComponentListener();

		ssComponent.updateSSComponent();

		addSSComponentListener();

	}

}
