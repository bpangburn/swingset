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
package com.nqadmin.swingset;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.models.OptionMappingSwingModel;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.models.SSListItem;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import static com.nqadmin.swingset.datasources.RowSetOps.*;
import com.nqadmin.swingset.models.GlazedListsOptionMappingInfo;
import com.nqadmin.swingset.models.SSListItemFormat;


// SSDBComboBox.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Similar to the SSComboBox, but used when both the 'bound' values and the
 * 'display' values are pulled from a database table. Generally the bound value
 * represents a foreign key to another table, and the combobox needs to display
 * a list of one (or more) columns from the other table.
 * <p>
 * Note, if changing both a sSRowSet and column name consider using the bind()
 * method rather than individual setSSRowSet() and setColumName() calls.
 * <p>
 * e.g.
 * <p>
 * Consider two tables: 1. part_data (part_id, part_name, ...) 2. shipment_data
 * (shipment_id, part_id, quantity, ...)
 * <p>
 * Assume you would like to develop a screen for the shipment table and you want
 * to have a screen with a combobox where the user can choose a part and a
 * textbox where the user can specify a quantity.
 * <p>
 * In the combobox you would want to display the part name rather than part_id
 * so that it is easier for the user to choose. At the same time you want to
 * store the id of the part chosen by the user in the shipment table.
 * <pre>
 * {@code
 * SSConnection connection = null;
 * SSJdbcRowSetImpl sSRowSet = null;
 * SSDataNavigator navigator = null;
 * SSDBComboBox combo = null;
 *
 * try {
 *
 * // CREATE A DATABASE CONNECTION OBJECT
 * SSConnection connection = new SSConnection(........);
 *
 * // CREATE AN INSTANCE OF SSJDBCROWSETIMPL
 * SSJdbcRowsetImpl sSRowSet = new SSJdbcRowsetImpl(connection);
 * sSRowSet.setCommand("SELECT * FROM shipment_data;");
 *
 * // DATA NAVIGATOR CALLS THE EXECUTE AND NEXT FUNCTIONS ON THE SSROWSET.
 * // IF YOU ARE NOT USING THE DATA NAVIGATOR YOU HAVE TO INCLUDE THOSE.
 * // sSRowSet.execute();
 * // sSRowSet.next();
 * SSDataNavigator navigator = new SSDataNavigator(sSRowSet);
 *
 * // QUERY FOR THE COMBOBOX.
 * String query = "SELECT * FROM part_data;";
 *
 * // CREATE AN INSTANCE OF THE SSDBCOMBOBOX WITH THE CONNECTION OBJECT
 * // QUERY AND COLUMN NAMES
 * combo = new SSDBComboBox(connection,query,"part_id","part_name");
 *
 * // THIS BASICALLY SPECIFIES THE COLUMN AND THE SSROWSET WHERE UPDATES HAVE
 * // TO BE MADE.
 * combo.bind(sSRowSet,"part_id");
 * combo.execute();
 *
 * // CREATE A TEXTFIELD
 * JTextField myText = new JTextField();
 * myText.setDocument(new SSTextDocument(sSRowSet, "quantity");
 *
 * } catch(Exception e) {
 *	// EXCEPTION HANDLER HERE...
 * }
 *
 *
 * // ADD THE SSDBCOMBOBOX TO THE JFRAME
 * getContentPane().add(combo.getComboBox());
 *
 * // ADD THE JTEXTFIELD TO THE JFRAME
 * getContentPane().add(myText);
 * }
 * </pre>
 */

public class SSDBComboBox extends JComboBox<SSListItem> implements SSComponentInterface {

	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column.
	 */
	protected class SSDBComboBoxListener implements ActionListener, Serializable {

		/**
		 * unique serial ID
		 */
		private static final long serialVersionUID = 5078725576768393489L;

		@Override
		public void actionPerformed(final ActionEvent ae) {

			removeSSRowSetListener();

			final int index = getSelectedIndex();

			if (index == -1) {
				logger.debug("{}: SSDBComboListener.actionPerformed setting bound column to  null.", () -> getColumnForLog());
				setBoundColumnText(null);
			} else {
				logger.debug("{}: SSDBComboListener.actionPerformed setting bound column to {}.", () -> getColumnForLog(), () -> getSelectedValue());
				setBoundColumnText(String.valueOf(getSelectedValue()));

			}

			addSSRowSetListener();
		}
	}


	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * Value to represent that no item has been selected in the combo box.
	 */
	// TODO: why not Integer.MIN_VALUE?
	public static final int NON_SELECTED = (int) ((Math.pow(2, 32) - 1) / (-2));

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -4203338788107410027L;

	/**
	 * Indicates if GlazedList autocompletion has already been installed
	 */
	private boolean autoCompleteInstalled = false;


	/**
	 * Define this class here because {@literal DefaultGla....<.....>}
	 * messes up the code. In particular:
	 * {@code try (OptionMappingSwingModel<Long,String,String>.Remodel remodel = xxx) }
	 */
	private static class ComboInfo extends GlazedListsOptionMappingInfo<Long,Object,Object> {
		private static final long serialVersionUID = 1L;

		public ComboInfo(boolean _hasOption2) {
			super(_hasOption2, new BasicEventList<SSListItem>());
		}

		/** this only works once, use it for install */
		public EventList<SSListItem> getEventList() {
			return super.getEventList();
		}
	}

	/**
	 * comboInfo handles eventList, mappings, options access.
	 */
	private final ComboInfo comboInfo;

	/**
	 * Format an SSListItem. Used to AutoCompleteSupport.install.
	 */
	private final SSListItemFormat listItemFormat;

	/**
	 * Format for any date columns displayed in combo box.
	 */
	protected String dateFormat = "MM/dd/yyyy";

	/**
	 * The database column used to populate the first visible column of the combo
	 * box.
	 */
	protected String displayColumnName = "";

	/**
	 * Map of string/value pairings for the ComboBox (generally the text to be
	 * display (SSListItem) and its corresponding primary key)
	 */
	protected EventList<SSListItem> eventList;

	/**
	 * counter for # times that execute() method is called - for testing
	 */
	// TODO remove this
	protected int executeCount = 0;

	/**
	 * boolean value for activating or disabling the filter
	 * <p>
	 * Appears to determine if GlazedList is used for filtering or original
	 * keystroke listener/filter.
	 * @deprecated 
	 */
	protected boolean filterSwitch = true;

	/**
	 * Underlying database table primary key values corresponding to text displayed.
	 * <p>
	 * Note that mappings for the SSDBComboBox are Longs whereas in SSComboBox they
	 * are Integers.
	 * @deprecated
	 */
	protected ArrayList<Long> mappings = null;

	/**
	 * Options to be displayed in the combobox (based on a query).
	 * @deprecated
	 */
	protected ArrayList<String> options = null;

	/**
	 * The column name whose value is written back to the database when the user
	 * chooses an item in the combo box. This is generally the PK of the table to
	 * which a foreign key is mapped.
	 */
	protected String primaryKeyColumnName = "";

	/**
	 * String typed by user into combobox
	 */
	protected String priorEditorText = "";

	/**
	 * Query used to populate combo box.
	 */
	protected String query = "";

	/**
	 * The database column used to populate the second (optional) visible column of
	 * the combo box.
	 */
	protected String secondDisplayColumnName = null;

	/**
	 * SSListItem currently selected in combobox. Needed because GlazedList can cause getSelectedIndex()
	 * to return -1 (while editing) or 0 (after selection is made from list subset)
	 */
	private SSListItem selectedItem = null;

	/**
	 * Alphanumeric separator used to separate values in multi-column comboboxes.
	 * <p>
	 * Changing default from " - " to " | " for 2020 rewrite.
	 */
	//protected String separator = " - ";
	protected String separator = " | ";

	/**
	 * Boolean to indicated that a call to setSelectedItem() is in progress.
	 */
	private boolean settingSelectedItem = false;

	/**
	 * Common fields shared across SwingSet components
	 */
	protected SSCommon ssCommon = new SSCommon(this);

	/**
	 * Component listener.
	 */
	protected final SSDBComboBoxListener ssDBComboBoxListener = new SSDBComboBoxListener();

	/**
	 * Creates an object of the SSDBComboBox.
	 */
	public SSDBComboBox() {
		// Note that call to parent default constructor is implicit.
		//super();
		eventList = new BasicEventList<SSListItem>();
		comboInfo = new ComboInfo(false);
		listItemFormat = new SSListItemFormat();
		listItemFormat.setPattern(JDBCType.DATE, dateFormat);
	}

	/**
	 * Constructs a SSDBComboBox with the given parameters.
	 *
	 * @param _ssConnection         database connection to be used.
	 * @param _query                query to be used to retrieve the values from the
	 *                              database.
	 * @param _primaryKeyColumnName column name whose value has to be stored.
	 * @param _displayColumnName    column name whose values are displayed in the
	 *                              combo box.
	 */
	public SSDBComboBox(final SSConnection _ssConnection, final String _query,
			final String _primaryKeyColumnName, final String _displayColumnName) {
		this();
		setSSConnection(_ssConnection);
		setQuery(_query);
		setPrimaryKeyColumnName(_primaryKeyColumnName);
		setDisplayColumnName(_displayColumnName);
	}

	/**
	 * Adds an item to the existing list of items in the combo box.
	 *
	 * @param _displayText text that should be displayed in the combobox
	 * @param _primaryKey  primary key value corresponding the the display text
	 */
	public void addItem(final String _displayText, final long _primaryKey) {

		// TODO Determine if any change is needed to actually add item to combobox.

		try (ComboInfo.Remodel remodel = comboInfo.getRemodel()) {
			remodel.add(_primaryKey, _displayText);
		} catch (final Exception e) {
			logger.error(getColumnForLog() + ": Exception.", e);
		}

		// // LOCK EVENT LIST
		// eventList.getReadWriteLock().writeLock().lock();

		// // INITIALIZE LISTS IF NULL
		// if (eventList == null) {
		// 	eventList = new BasicEventList<>();
		// }
		// if (mappings == null) {
		// 	mappings = new ArrayList<Long>();
		// }
		// if (options == null) {
		// 	options = new ArrayList<String>();
		// }

		// try {

		// 	// create new list item
		// 	final SSListItem listItem = new SSListItem(_primaryKey, _displayText);

		// 	// add to lists
		// 	eventList.add(listItem);
		// 	mappings.add(listItem.getPrimaryKey());
		// 	options.add(listItem.getListItem());

		// } catch (final Exception e) {
		// 	logger.error(getColumnForLog() + ": Exception.", e);
		// } finally {
		// 	eventList.getReadWriteLock().writeLock().unlock();
		// }

	}

	/**
	 * Adds any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 */
	@Override
	public void addSSComponentListener() {
		addActionListener(ssDBComboBoxListener);

	}

	/**
	 * Adds an item to the existing list of items in the combo box.
	 *
	 * @param _name  name that should be displayed in the combo
	 * @param _value value corresponding the the name
	 */
	@Deprecated
	public void addStringItem(final String _name, final String _value) {
		addItem(_name, Long.valueOf(_value));

	}

	/**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * <p>
	 * It will actually be called from SSCommon.init() once the SSCommon data member
	 * is instantiated.
	 */
	@Override
	public void customInit() {
		// SET PREFERRED DIMENSIONS
// TODO not sure SwingSet should be setting component dimensions
		setPreferredSize(new Dimension(200, 20));
// TODO This was added during SwingSet rewrite 4/2020. Need to confirm it doesn't break anything.
		setEditable(false); // GlazedList overrides this and sets it to true
	}

	/**
	 * Removes an item from the combobox and underlying lists based on the record
	 * primary key provided.
	 * <p>
	 * If more than one item is present in the combo for that value the first one is
	 * changed.
	 *
	 * @param _primaryKey primary key value for the item that should be removed
	 *
	 * @return returns true on successful deletion otherwise returns false.
	 */
	public boolean deleteItem(final long _primaryKey) {

		// TODO Determine if any change is needed to actually remove item from combobox.

		boolean result = false;

		try (ComboInfo.Remodel remodel = comboInfo.getRemodel()) {
			// GET INDEX FOR mappings and options
			int index = remodel.getMappings().indexOf(_primaryKey);
			// PROCEED IF INDEX WAS FOUND
			if (index != -1) {
				remodel.remove(index);
				result = true;
			}
		} catch (final Exception e) {
			logger.error(getColumnForLog() + ": Exception.", e);
		}

		return result;

//		if (eventList != null) {
//
//			// LOCK EVENT LIST
//			eventList.getReadWriteLock().writeLock().lock();
//
//			try {
//
//				// GET INDEX FOR mappings and options
//				final int index = mappings.indexOf(_primaryKey);
//
//				// PROCEED IF INDEX WAS FOUND
//				if (index != -1) {
//					options.remove(index);
//					mappings.remove(index);
//					eventList.remove(index);
//					result = true;
//
//				}
//
//			} catch (final Exception e) {
//				logger.error(getColumnForLog() + ": Exception.", e);
//			} finally {
//				eventList.getReadWriteLock().writeLock().unlock();
//			}
//
//		}
//
//		return result;

	}

	/**
	 * Removes the display text provided from the combobox and removes any
	 * corresponding list items.
	 * <p>
	 * If more than one item is present in the combo for the specified value, only
	 * the first one is removed.
	 *
	 * @param _displayText value of the item to be deleted.
	 *
	 * @return returns true on successful deletion otherwise returns false.
	 */
	public boolean deleteStringItem(final String _displayText) {

		boolean result = false;

		// TODO: javadoc says "list items" plural. Is that what's wanted.
		try (ComboInfo.Remodel remodel = comboInfo.getRemodel()) {
			final int index = remodel.getOptions().indexOf(_displayText);
			result = deleteItem(index);
		}

		// if (options != null) {
		// 	final int index = options.indexOf(_displayText);
		// 	result = deleteItem(mappings.get(index));
		// }

		return result;

	}

	/**
	 * Executes the query specified with setQuery(), populates combobox, and turns on AutoCompleteSupport
	 * <p>
	 * @throws Exception exception that occurs querying data or turning on AutoComplete
	 */
	public void execute() throws Exception {

		//System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.execute() - setting execute count: " + executeCount++);
		// (re)query data
		queryData();

		// Only install AutoCompleteSupport once.
		// See https://stackoverflow.com/questions/15210771/autocomplete-with-glazedlists for info on modifying lists.
		// See https://javadoc.io/doc/com.glazedlists/glazedlists/latest/ca/odell/glazedlists/swing/AutoCompleteSupport.html
		// We would like to call autoComplete.setStrict(true), but it is not currently compatible with TextMatcherEditor.CONTAINS, which is the more important feature.
		// There is a support request to support STRICT and CONTAINS: https://github.com/glazedlists/glazedlists/issues/676
		// Note that installing AutoComplete support makes the ComboBox editable.
		// Should already in the event dispatch thread so don't use invokeAndWait()
		if (!autoCompleteInstalled) {
			final AutoCompleteSupport<SSListItem> autoComplete = AutoCompleteSupport.install(this, comboInfo.getEventList(), null, listItemFormat);
			autoComplete.setFilterMode(TextMatcherEditor.CONTAINS);
			autoCompleteInstalled = true;
		}

		// autoComplete.setStrict(true);

		// since the list was likely blank when the component was bound we need to update the component again so it can get the text from the list
		// we don't want to do this if the component is unbound as with an SSDBComboBox used for navigation.
		if (getSSRowSet() != null) {
			updateSSComponent();
		}
	}

	/**
	 * Returns the pattern in which dates have to be displayed
	 *
	 * @return returns the pattern in which dates have to be displayed
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * Returns the column name whose values are displayed in the combo box.
	 *
	 * @return returns the name of the column used to get values for combo box
	 *         items.
	 */
	public String getDisplayColumnName() {
		return displayColumnName;
	}

	/**
	 * @return the eventList
	 */
	// TODO: public method? Why? How/where used.
	//		Make private, see what happens
	private EventList<SSListItem> getEventList() {
		return eventList;
	}

	/**
	 * Provides the initial number of items in the list underlying the CombobBox
	 * <p>
	 * NOTE: There does not appear to be any code that sets this value so marking as
	 * Deprecated.
	 *
	 * @return the initial number of items in the combobox list
	 */
	@Deprecated
	public int getInitialNumberOfItems() {
		// it appears code was never written to set this value so Depreciated and
		// returning 0
		// TODO Remove completely from future release.

		logger.warn(getColumnForLog() + ": This method was never properly implemented so it has been Deprecated and just returns 0. \n" + Thread.currentThread().getStackTrace());
		return 0;
	}

	/**
	 * @return the mappings
	 */
	public List<Long> getMappings() {
		// TODO: the signature should be List<>, not ArrayList.
		//		 Changed signature to avoid strange casting exception
		//		 having to do with unmodifiable lists...
		//return mappings;
		return comboInfo.getMappings();
	}

	/**
	 * Returns the number of items present in the combo box.
	 * <p>
	 * This is a read-only bean property.
	 *
	 * @return returns the number of items present in the combo box.
	 */
	@Deprecated
	public int getNumberOfItems() {
	
		// TODO Determine where/how this is/was used.
		
		// int result = 0;

		return comboInfo.getSize();

		// if (eventList != null) {
		// 	result = eventList.size();
		// }

		// return result;
	}

	/**
	 * @return the options
	 */
	public List<Object> getOptions() {
		// NOTE: this was List<String>, now List<Object>
		// NOTE: changed signature to List<> from ArrayList<>.
		// TODO: is this used?
		//return options;

		return comboInfo.getOptions();

		// NOTE: IF A LIST OF THE STRINGS IN COMBOBOX IS WANTED,
		//		 THEN THE FOLLOWING CAN BE USED.

		// List<String> options = new ArrayList<>();
		// try (ComboInfo.Remodel remodel = comboInfo.getRemodel()) {
		// 	List<SSListItem> items = remodel.getEventList();
		// 	for(SSListItem item : items) {
		// 		options.add(listItemFormat.format(item));
		// 	}
		// }
		// return options;

		// or something like should work. Know that comboInfo has strings.
		// depends on what the caller is looking for.

		// @SuppressWarnings("unchecked")
		// List<String> foo = (List)comboInfo.getOptions();
		// return foo;
	}

	/**
	 * @return the primaryKeyColumnName
	 */
	public String getPrimaryKeyColumnName() {
		return primaryKeyColumnName;
	}

	/**
//	 * Returns the query used to retrieve values from database for the combo box.
	 *
	 * @return returns the query used.
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Returns the second column name whose values are also displayed in the combo
	 * box.
	 *
	 * @return returns the name of the column used to get values for combo box
	 *         items. returns NULL if the second display column is not provided.
	 */
	public String getSecondDisplayColumnName() {
		return secondDisplayColumnName;
	}

	/**
	 * Returns the text displayed in the combobox.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @return value corresponding to the selected item in the combo. return null if
	 *         no item is selected.
	 */
	public String getSelectedStringValue() {
		// try (ComboInfo.Remodel remodel = comboInfo.getRemodel()) {
		// 	SSListItem currentItem = (SSListItem)getSelectedItem();
		// 	return currentItem != null ? remodel.getOption(currentItem) : null;
		// }
		Object currentItem = getSelectedItem();
		return currentItem != null ? listItemFormat.format(currentItem) : null;
	}

	/**
	 * Returns the underlying database record primary key value corresponding to the
	 * currently selected item in the combobox.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @return returns the value associated with the selected item OR -1 if nothing
	 *         is selected.
	 */
	public long getSelectedValue() {
		
		// TODO Consider overriding getSelectedIndex() to account for GlazedList impact
		
		logger.debug("{}: Call to getSelectedValue().", () -> getColumnForLog());

		Long result = null;

		// 2020-10-03_BP: getSelectedValue() seems to be the root of problems with filtered/glazed lists.
		// When filtering is taking place, getSelectedIndex() returns -1

		// Determine if the call to getSelectedValue() is happening during a call to setSelectedItem()
		try (ComboInfo.Remodel remodel = comboInfo.getRemodel()) {
			if (settingSelectedItem) {
				if (selectedItem == null) {
					result = (long) NON_SELECTED;
				} else {
					//result = selectedItem.getPrimaryKey();
					result = remodel.getMapping(selectedItem);
				}
			} else {
				// Existing code not impacted by GlazedList dynamically impacting the list.
				if (getSelectedIndex() == -1) {
					result = (long) NON_SELECTED;
				} else {
					result = remodel.getMapping(getSelectedIndex());
					
				}
			}
		}

		// If anything above returned null, change to NON_SELECTED.
		if (result==null) {
			result = (long) NON_SELECTED;
		}

		return result;
	}

	/**
	 * Returns the separator used when multiple columns are displayed
	 *
	 * @return separator used.
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * Returns the separator used when multiple columns are displayed
	 * <p>
	 * Deprecated for misspelling.
	 *
	 * @return separator used.
	 */
	@Deprecated
	public String getSeperator() {
		return separator;
	}

	/**
	 * Returns the ssCommon data member for the current Swingset component.
	 *
	 * @return shared/common SwingSet component data and methods
	 */
	@Override
	public SSCommon getSSCommon() {
		return ssCommon;
	}

//	/** type of column being formatted, date types special */
//	private JDBCType optionColumnType = null;
//	/** type of secondary column being formatted, date types special */
//	private JDBCType option2ColumnType = null;
//
//	private class ComboItemFormat extends Format {
//		private static final long serialVersionUID = 1L;
//		@Override
//		public Object parseObject(String source, ParsePosition pos) {
//			// Do not create objects from here
//			return source;
//		}
//		@Override
//		public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
//			// GlazedLists guarentees only format(Object), so ignore pos.
//			SSListItem listItem = (SSListItem)obj;
//			toAppendTo.append(comboInfo.getOption(listItem).toString());
//			if (hasOption2()) {
//				Object option2 = comboInfo.getOption2(listItem);
//				if (option2 != null) {
//					toAppendTo.append(separator).append(option2.toString());
//				}
//			}
//			return toAppendTo;
//		}
//	};

	// This is the old method, now handled in format
	protected String getStringValue(final ResultSet _rs, final String _columnName) {
		throw new UnsupportedOperationException();
	}

//	/**
//	 * Converts the database column value into string.Only date columns are
//	 * formatted as specified by dateFormat variable all other column types are
//	 * retrieved as strings
//	 *
//	 * @param _jdbcType	  the type of data to get as a String
//	 * @param _rs         ResultSet containing database column to convert to string
//	 * @param _columnName database column to convert to string
//	 * @return string value of database column
//	 */
//	// TODO: this goes into format; like getStringValue(type, object)
//	protected String getStringValue(final JDBCType _jdbcType, final ResultSet _rs, final String _columnName) {
//		String strValue = "";
//		try {
//			switch (_jdbcType) {
//			case DATE:
//				final SimpleDateFormat myDateFormat = new SimpleDateFormat(dateFormat);
//				strValue = myDateFormat.format(_rs.getDate(_columnName));
//				break;
//			default:
//				strValue = _rs.getString(_columnName);
//				break;
//			}
//			if (strValue == null) {
//				strValue = "";
//			}
//		} catch (final SQLException se) {
//			logger.error(getColumnForLog() + ": SQL Exception.", se);
//		}
//		return strValue;
//
//	}

	private boolean hasOption2() {
		return secondDisplayColumnName != null && !secondDisplayColumnName.equals("");
	}

	/**
	 * Populates the list model with the data by fetching it from the database.
	 */
	private void queryData() {

		//Long primaryKey = null;
		ResultSet rs;

		// this.data.getReadWriteLock().writeLock().lock();
		try (ComboInfo.Remodel remodel = comboInfo.getRemodel()) {
			logger.trace("{}: Clearing eventList.", () -> getColumnForLog());
			remodel.clear();
			logger.debug("{}: Nulls allowed? [{}].", () -> getColumnForLog(), () -> getAllowNull());
			// 2020-07-24: adding support for a nullable first item if nulls are supported
			// 2020-10-02: For a SSDBComboBox used as a navigator, we don't want a null first item. Look at getBoundColumnName().
			if (getAllowNull() && (getBoundColumnName()!=null)) {
				logger.debug("{}: Adding blank list item", () -> getColumnForLog());
				remodel.add(null, "");
			}

			Statement statement = ssCommon.getSSConnection().getConnection().createStatement();
			rs = statement.executeQuery(getQuery());

			//optionColumnType = getJDBCColumnType(rs, rs.findColumn(displayColumnName));

			// Configure the listItemFormat with this queries column types
			listItemFormat.clear();
			listItemFormat.addElemType(comboInfo.getOptionListItemElemIndex(),
					getJDBCColumnType(rs, rs.findColumn(displayColumnName)));
			if (hasOption2()) {
				//option2ColumnType = getJDBCColumnType(rs, rs.findColumn(secondDisplayColumnName));
				listItemFormat.addElemType(comboInfo.getOption2ListItemElemIndex(),
						getJDBCColumnType(rs, rs.findColumn(secondDisplayColumnName)));
			}

			logger.debug("{}: Query [{}].", () -> getColumnForLog(), () -> getQuery());

			List<SSListItem> newItems = new ArrayList<>();
			while (rs.next()) {
				Long pk = rs.getLong(getPrimaryKeyColumnName());
				Object opt = rs.getObject(displayColumnName);
				Object opt2 = hasOption2() ? rs.getObject(secondDisplayColumnName) : null;
				logger.trace("{}: First column to display - " + opt, () -> getColumnForLog());
				if (hasOption2()) {
					logger.trace("{}: Second column to display - " + opt2, () -> getColumnForLog());
				}
				newItems.add(remodel.createComboItem(pk, opt, opt2));
			}
			remodel.addAll(newItems);
			rs.close();

//				// extract primary key
//				primaryKey = rs.getLong(getPrimaryKeyColumnName());
//
//				// extract first column string
//				// getStringValue() takes care of formatting dates
//				// TODO: just get an object and use toString
//				String displayString = getStringValue(optionColumnType, rs, displayColumnName);
//				logger.trace("{}: First column to display - " + displayString, () -> getColumnForLog());
//
//				// extract second column string, if applicable
//				// getStringValue() takes care of formatting dates
//				if (hasOption2()) {
//					String secondColumnString = getStringValue(option2ColumnType, rs, secondDisplayColumnName);
//					if (!secondColumnString.isEmpty()) {
//						displayString += separator + secondColumnString;
//					}
//					logger.trace("{}: Second column to display - " + secondColumnString, () -> getColumnForLog());
//				}
//
//				// add to lists
//				remodel.add(primaryKey, displayString);
//			}
//			rs.close();

		} catch (final SQLException se) {
			logger.error(getColumnForLog() + ": SQL Exception.", se);
		} catch (final java.lang.NullPointerException npe) {
			logger.error(getColumnForLog() + ": Null Pointer Exception.", npe);
		}
	}
//	private void queryDataXXX() {
//
//		if (eventList != null) {
//		// .clear() appears to be the correct method vs. .dispose() for working with GlazedLists
//			logger.trace("{}: Clearing eventList.", () -> getColumnForLog());
//			eventList.clear();
//		} else {
//			eventList = new BasicEventList<>();
//		}
//		if (mappings != null) {
//			mappings.clear();
//		} else {
//			mappings = new ArrayList<Long>();
//		}
//		if (options != null) {
//			options.clear();
//		} else {
//			options = new ArrayList<String>();
//		}
//
//		eventList.getReadWriteLock().writeLock().lock();
//
//		Long primaryKey = null;
//		String firstColumnString = null;
//		String secondColumnString = null;
//		SSListItem listItem = null;
//		ResultSet rs = null;
//
//		// this.data.getReadWriteLock().writeLock().lock();
//		try {
//			logger.debug("{}: Nulls allowed? [{}].", () -> getColumnForLog(), () -> getAllowNull());
//			// 2020-07-24: adding support for a nullable first item if nulls are supported
//			// 2020-10-02: For a SSDBComboBox used as a navigator, we don't want a null first item. Look at getBoundColumnName().
//			if (getAllowNull() && (getBoundColumnName()!=null)) {
//				listItem = new SSListItem(null, "");
//				logger.debug("{}: Adding blank list item - " + listItem, () -> getColumnForLog());
//				eventList.add(listItem);
//				mappings.add(listItem.getPrimaryKey());
//				options.add(listItem.getListItem());
//			}
//
//			final Statement statement = ssCommon.getSSConnection().getConnection().createStatement();
//			rs = statement.executeQuery(getQuery());
//
//			logger.debug("{}: Query [{}].", () -> getColumnForLog(), () -> getQuery());
//
//			while (rs.next()) {
//				// extract primary key
//				primaryKey = rs.getLong(getPrimaryKeyColumnName());
//
//				// extract first column string
//				// getStringValue() takes care of formatting dates
//				firstColumnString = getStringValue(rs, displayColumnName, 0);
//				logger.trace("{}: First column to display - " + firstColumnString, () -> getColumnForLog());
//
//				// extract second column string, if applicable
//				// getStringValue() takes care of formatting dates
//				secondColumnString = null;
//				if ((secondDisplayColumnName != null) && !secondDisplayColumnName.equals("")) {
//					secondColumnString = rs.getString(secondDisplayColumnName);
//					if (secondColumnString.equals("")) {
//						secondColumnString = null;
//					}
//					logger.trace("{}: Second column to display - " + secondColumnString, () -> getColumnForLog());
//				}
//
//				// build eventList item
//				if (secondColumnString != null) {
//					listItem = new SSListItem(primaryKey, firstColumnString + separator + secondColumnString);
//				} else {
//					listItem = new SSListItem(primaryKey, firstColumnString);
//				}
//
//				// add to lists
//				eventList.add(listItem);
//				mappings.add(listItem.getPrimaryKey());
//				options.add(listItem.getListItem());
//
//			}
//			rs.close();
//
//		} catch (final SQLException se) {
//			logger.error(getColumnForLog() + ": SQL Exception.", se);
//		} catch (final java.lang.NullPointerException npe) {
//			logger.error(getColumnForLog() + ": Null Pointer Exception.", npe);
//		} finally {
//			eventList.getReadWriteLock().writeLock().unlock();
//		}
//	}

	/**
	 * Removes any necessary listeners for the current SwingSet component. These
	 * will trigger changes in the underlying RowSet column.
	 */
	@Override
	public void removeSSComponentListener() {
		removeActionListener(ssDBComboBoxListener);

	}

	/**
	 * When a display column is of type date you can choose the format in which it
	 * has to be displayed. For the pattern refer SimpleDateFormat in java.text
	 * package.
	 *
	 * @param _dateFormat pattern in which dates have to be displayed
	 */
	public void setDateFormat(final String _dateFormat) {
		final String oldValue = dateFormat;
		dateFormat = _dateFormat;
		firePropertyChange("dateFormat", oldValue, dateFormat);
		listItemFormat.setPattern(JDBCType.DATE, _dateFormat);
	}

	/**
	 * Sets the column name whose values have to be displayed in combo box.
	 *
	 * @param _displayColumnName column name whose values have to be displayed.
	 */
	public void setDisplayColumnName(final String _displayColumnName) {
		final String oldValue = displayColumnName;
		displayColumnName = _displayColumnName;
		firePropertyChange("displayColumnName", oldValue, displayColumnName);
	}

	/**
	 * @param eventList the eventList to set
	 */
	public void setEventList(final EventList<SSListItem> eventList) {
		// TODO: what/how is this used?
		throw new UnsupportedOperationException();
		//this.eventList = eventList;
	}

	/**
	 * Method that sets the combo box to be filterable.
	 * <p>
	 * GlazedList filtering is now fully integrated so this no longer serves a
	 * purpose.
	 *
	 * @param _filter boolean to turn filtering on or off
	 */
	@Deprecated
	public void setFilterable(final boolean _filter) {
		// TODO remove this method in future release
		filterSwitch = _filter;
		logger.warn(getColumnForLog() + ": This method has been Deprecated because GlazedList filtering is now fully integrated.\n" + Thread.currentThread().getStackTrace());
	}

	/**
	 * @param mappings the mappings to set
	 * @deprecated
	 */
	public void setMappings(final ArrayList<Long> mappings) {
		throw new UnsupportedOperationException();
		//this.mappings = mappings;
	}

	/**
	 * @param options the options to set
	 * @deprecated
	 */
	public void setOptions(final ArrayList<String> options) {
		throw new UnsupportedOperationException();
		//this.options = options;
	}

	/**
	 * Sets the database table primary column name.
	 *
	 * @param _primaryKeyColumnName name of primary key column
	 */
	public void setPrimaryKeyColumnName(final String _primaryKeyColumnName) {
		final String oldValue = primaryKeyColumnName;
		primaryKeyColumnName = _primaryKeyColumnName;
		firePropertyChange("primaryKeyColumnName", oldValue, primaryKeyColumnName);
	}

	/**
	 * Sets the query used to display items in the combo box.
	 *
	 * @param _query query to be used to get values from database (to display combo
	 *               box items)
	 */
	public void setQuery(final String _query) {
		final String oldValue = query;
		query = _query;
		firePropertyChange("query", oldValue, query);
	}

	/**
	 * Sets the second display name. If more than one column have to displayed then
	 * use this. For the parts example given above. If you have a part description
	 * in part table. Then you can display both part name and part description.
	 *
	 * @param _secondDisplayColumnName column name whose values have to be displayed
	 *                                 in the combo in addition to the first column
	 *                                 name.
	 */
	public void setSecondDisplayColumnName(final String _secondDisplayColumnName) {
		final String oldValue = secondDisplayColumnName;
		secondDisplayColumnName = _secondDisplayColumnName;
		comboInfo.setOption2Enabled(hasOption2());
		firePropertyChange("secondDisplayColumnName", oldValue, secondDisplayColumnName);
	}

	/**
	 * Sets the currently selected value. This is called when the user clicks on an
	 * item or when they type in the combo's textfield.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _value value to set as currently selected.
	 */
	@Override
	public void setSelectedItem(final Object _value) {

// TODO Need to deal with null on focus lost event. SSDBComboListener.actionPerformed setting bound column to  null when focus lost.
// TODO Could add back logic to revert typed text to restore matches.

// INTERCEPTING GLAZEDLISTS CALLS TO setSelectedItem() SO THAT WE CAN PREVENT IT FROM TRYING TO SET VALUES NOT IN THE LIST

		settingSelectedItem = true;

		try {

// NOTE THAT CALLING setSelectedIndex(-1) IN THIS METHOD CAUSES  CYCLE HERE BECAUSE setSelectedIndex() CALLS setSelectedItem()
		logger.debug("{}: Selected Item=" + _value, () -> getColumnForLog());

//		logger.debug("{}: Selected Index BEFORE hidePopup()={}", () -> getColumnForLog(), () -> getSelectedIndex());
//
//		int possibleMatches = getItemCount();
//		logger.debug("{}: Possible matches BEFORE hidePopup() - " + possibleMatches, () -> getColumnForLog());
//
//		//hidePopup();
//
//		possibleMatches = getItemCount();
//		logger.debug("{}: Possible matches AFTER hidePopup() - " + possibleMatches, () -> getColumnForLog());
//		logger.debug("{}: Selected Index AFTER hidePopup()={}", () -> getColumnForLog(), () -> getSelectedIndex());

		// Extract selected item
			//selectedItem = (SSListItem) _value;
			selectedItem = (SSListItem) _value;

		// Call to super.setSelectedItem() triggers SSDBComboListener.actionPerformed, which calls getSelectedValue(), which calls getSelectedIndex(), which returns -1 while still in the editor
		// and returns 0 after focus is lost.
		//
		// Calling hidePopup() restores the list, but messes up the GlazedList filtering.
		//
		// 2020-10-03_BP: Updated getSelectedValue() to properly return the primary key rather than using getSelectedIndex() during a call to this method.
		// Only try to update item for a valid list item.
			if (selectedItem!=null) {
				super.setSelectedItem(_value);
				logger.debug("{}: Selected Index AFTER setSelectedItem()={}", () -> getColumnForLog(), () -> getSelectedIndex());
			} else {
				logger.debug("{}: No matching list item found so not updating. Current editor text is '{}'", () -> getColumnForLog(), () -> getEditor().getItem().toString());
			}



		} finally {
			settingSelectedItem = false;
			selectedItem = null;
		}

		return;
//		// DECLARATIONS
//		String currentEditorText = "";
//		int possibleMatches;
//		SSListItem selectedItem;
//
//		// WE COULD BE HERE DUE TO:
//		// 1. MOUSE CLICK ON AN ITEM
//		// 2. KEYBASED NAVIGATION
//		// 3. USER TYPING SEQUENTIALLY:
//		// THIS MAY TRIGGER MATCHING ITEMS, OR MAY NOT MATCH ANY SUBSTRINGS SO WE DELETE
//		// THE LAST CHARACTER
//		// 4. USER DOING SOMETHING UNEXPECTED LIKE INSERTING CHARACTERS, DELETING ALL
//		// TEXT, ETC.
//		// THIS MAY TRIGGER MATCHING ITEMS, OR MAY NOT MATCH ANY SUBSTRINGS SO WE REVERT
//		// TO THE LAST STRING AVAILABLE
//		// IF NOT MATCH, COULD ALSO REVERT TO EMPTY STRING
//
//		// GET LATEST TEXT TYPED BY USER
//
//		if (getEditor().getItem() != null) {
//			currentEditorText = getEditor().getItem().toString();
//		}
//
//		selectedItem = (SSListItem) _value;
//
//		// FOUR OUTCOMES:
//		// 1. _value is null, but selectedItem is not null, indicating a match (so null
//		// is a valid choice)
//		// 2. _value is null and selectedItem is null, indicating no match
//		// 3. neither _value nor selectedItem are null, indicating a match
//		// 4. _value is not null, but selectedItem is null, indicating no match (have to
//		// revert text)
//
//		if (selectedItem != null) {
//			// OUTCOME 1 & 3 ABOVE, MAKE CALL TO SUPER AND MOVE ALONG
//			// Display contents of selectedItem for debugging
//			logger.debug("{}: PK={}, Item={}.", () -> getColumnForLog(), () -> selectedItem.getPrimaryKey(), () -> selectedItem.getListItem());
//			logger.debug("{}: Prior text was '" + priorEditorText + "'. Current text is '" + currentEditorText + "'.", () -> getColumnForLog());
//
//			// We have to be VERY careful with calls to setSelectedItem() because it will
//			// set the value based on the index of any SUBSET list returned by GlazedList,
//			// not the full list
//			//
//			// Calling hidePopup() clears the subset list so that the subsequent
//			// call to setSelectedItem works as intended.
//
//			possibleMatches = getItemCount();
//			logger.debug("{}: Possible matches BEFORE hidePopup() - " + possibleMatches, () -> getColumnForLog());
//
//			hidePopup();
//
//			possibleMatches = getItemCount();
//			logger.debug("{}: Possible matches AFTER hidePopup() - " + possibleMatches, () -> getColumnForLog());
//
//			// Call to parent method.
//			// Don't call setSelectedIndex() as this causes a cycle
//			// setSelectedIndex()->setSelectedItem().
//			logger.debug("{}: Calling super.setSelectedItem(" + selectedItem + ")", () -> getColumnForLog());
//			super.setSelectedItem(selectedItem);
//
//			// Update editor text
//			currentEditorText = selectedItem.getListItem();
//			getEditor().setItem(currentEditorText);
//			updateUI();
//
//			logger.debug("{}: Prior text was '" + priorEditorText + "'. Current text is '" + currentEditorText + "'.", () -> getColumnForLog());
//
//			// update priorEditorText
//			priorEditorText = currentEditorText;
//
//		} else if (_value == null) {
//			// OUTCOME 2 ABOVE
//			// setSelectedItem() was called with null, but there is no match (so null is not a valid selection in the list)
//			// There may be partial matches from GlazedList.
//			logger.debug("{}: Method called with null. Prior text was '" + priorEditorText + "'. Current text is '" + currentEditorText + "'.", () -> getColumnForLog());
//
//			// Determine if there are partial matches on the popup list due to user typing.
//			possibleMatches = getItemCount();
//			logger.debug("{}: Possible matches - " + possibleMatches, () -> getColumnForLog());
//
//			if (possibleMatches > 0) {
//				// update the latestTypedText, but don't make a call to super.setSelectedItem(). No change to bound value.
//				priorEditorText = currentEditorText;
//			} else {
//// 2020-08-03: if user types "x" and it is not a choice we land here
//// on call to updateUI(), focus is lost and list items revert to 6 for "ss_db_combo_box" column in swingset_tests.sql
//// if "x" is typed a 2nd time, the popup does not become visible again and there are zero items in the list before and after the call
//// to setItem() and/or to updateUI()
//
//
//// This could also be the result of the first call to execute() where nothing has been typed and the popup is not visible.
//// This will throw a 'java.awt.IllegalComponentStateException' exception when showPopup() is called.
//				//if (!this.isVisible()) {
//				if (currentEditorText.isEmpty()) {
//					logger.debug("{}: Method called with null, but nothing has been typed. This occurs during screen initialization.", () -> getColumnForLog());
//					super.setSelectedItem(selectedItem);
//					// 2020-10-03_BP: Probably need to update priorEditorText here
//					priorEditorText = currentEditorText;
//				} else {
//					logger.debug("{}: Reverting to prior typed text.", () -> getColumnForLog());
//					getEditor().setItem(priorEditorText);
//					// IMPORTANT: The particular order here of showPopup() and then updateUI() seems to restore the
//					// underlying GlazedList to all of the items. Reversing this order breaks things. Calling hidePopup() does not work.
//					showPopup();
//					updateUI(); // This refreshes the characters displayed. Display does not update without call to updateUI();
//								// updateUI() triggers focus lost
//					possibleMatches = getItemCount();
//
//					logger.debug("{}: Possible matches AFTER reverting text - " + possibleMatches, () -> getColumnForLog());
//				}
//			}
//
//		} else {
//			// OUTCOME 4 ABOVE
//			// generally not expecting this outcome
//			// revert to prior string and don't select anything
//			logger.warn(getColumnForLog() + ": Method called with " + _value + ", but there is no match. Prior text was '" + priorEditorText + "'. Current text is '" + currentEditorText + "'.");
//
//			// TODO Throw an exception here? May be the result of a coding error.
//			getEditor().setItem(priorEditorText);
//			currentEditorText = priorEditorText;
//			updateUI(); // This refreshes the characters displayed.
//		}

	}

	/**
	 * Sets the currently selected value
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _value value to set as currently selected.
	 */
	public void setSelectedStringValue(final String _value) {

		// ONLY NEED TO PROCEED IF THERE IS A CHANGE
		// TODO consider firing a property change
		try (ComboInfo.Remodel remodel = comboInfo.getRemodel()) {
			if(!remodel.isEmpty()) {
				if (_value != getSelectedItem()) {
					
					// IF OPTIONS ARE NON-NULL THEN LOCATE THE SEQUENTIAL INDEX AT WHICH THE
					// SPECIFIED TEXT IS STORED
					//final int index = options.indexOf(_value);
					final int index = remodel.getOptions().indexOf(_value);
					
					if (index == -1) {
						logger.warn(getColumnForLog() + ": Could not find a corresponding item in combobox for display text of " + _value + ". Setting index to -1 (blank).");
					}
					
					setSelectedIndex(index);
					//updateUI();
					
				}
			}
		}

	}

	/**
	 * Sets the value stored in the component.
	 * <p>
	 * If called from updateSSComponent() from a RowSet change then the Component
	 * listener should already be turned off. Otherwise we want it on so the
	 * ultimate call to setSelectedIndex() will trigger an update the to RowSet.
	 * <p>
	 * The mappings ArrayList will be null until execute is called so
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _value database record primary key value to assign to combobox
	 */
	public void setSelectedValue(final long _value) {

		// TODO consider firing a property change
		// TODO what happens if user tries to pass null or if getSelectedValue() is null?

		// 2020-08-03: Removing conditional as this could be called when consecutive records
		// have the same value and we want to make sure to update the editor Strings

		// IF MAPPINGS ARE SPECIFIED THEN LOCATE THE SEQUENTIAL INDEX AT WHICH THE
		// SPECIFIED CODE IS STORED

		try (ComboInfo.Remodel remodel = comboInfo.getRemodel()) {
			if (!remodel.isEmpty()) {
				//final int index = mappings.indexOf(_value);
				final int index = remodel.getMappings().indexOf(_value);
				
				if (index == -1) {
					logger.warn(getColumnForLog() + ": Could not find a corresponding item in combobox for value of " + _value + ". Setting index to -1 (blank).");
				}
				
				logger.trace("{}: eventList - [{}].", () -> getColumnForLog(), () ->  remodel.getItemList().toString());
				logger.trace("{}: options - [{}].", () -> getColumnForLog(), () ->  remodel.getOptions().toString());
				logger.trace("{}: mappings - [{}].", () -> getColumnForLog(), () ->  remodel.getMappings().toString());
				
				setSelectedItem(index == -1 ? null : remodel.get(index));
				//setSelectedIndex(index);
			} else {
				logger.warn(getColumnForLog() + ": No mappings available for current component. No value set by setSelectedValue().");
			}
		}

	}

	/**
	 * Set the separator to be used when multiple columns are displayed
	 *
	 * @param _separator separator to be used.
	 */
	public void setSeparator(final String _separator) {
		final String oldValue = separator;
		separator = _separator;
		firePropertyChange("separator", oldValue, separator);
		listItemFormat.setSeparator(separator);
	}

	/**
	 * Set the separator to be used when multiple columns are displayed
	 * <p>
	 * Deprecated for misspelling.
	 *
	 * @param _separator separator to be used.
	 */
	@Deprecated
	public void setSeperator(final String _separator) {
		setSeparator(_separator);
	}

	/**
	 * Sets the SSCommon data member for the current Swingset Component.
	 *
	 * @param _ssCommon shared/common SwingSet component data and methods
	 */
	@Override
	public void setSSCommon(final SSCommon _ssCommon) {
		ssCommon = _ssCommon;

	}

	/**
	 * Updates an item available in the combobox and associated lists.
	 * <p>
	 * If more than one item is present in the combo for that value, only the first
	 * one is changed.
	 * <p>
	 * NOTE: To retain changes made to current SSRowSet call updateRow before
	 * calling the updateItem on SSDBComboBox. (Only if you are using the
	 * SSDBComboBox and SSDataNavigator for navigation in the screen. If you are not
	 * using the SSDBComboBox for navigation then no need to call updateRow on the
	 * SSRowSet. Also if you are using only SSDBComboBox for navigation you need not
	 * call the updateRow.)
	 *
	 * @param _primaryKey         primary key value corresponding the the display
	 *                            text to be updated
	 * @param _updatedDisplayText text that should be updated in the combobox
	 *
	 * @return returns true if update is successful otherwise returns false.
	 */
	public boolean updateItem(final long _primaryKey, final String _updatedDisplayText) {

		boolean result = false;

		try (ComboInfo.Remodel remodel = comboInfo.getRemodel()) {
			final int index = remodel.getMappings().indexOf(_primaryKey);
			if (index < 0) {
				remodel.setOption(index, _updatedDisplayText);
				result = true;
// TODO Confirm that eventList is not reordered by GlazedLists code.
			}
// TODO may need to call repaint()
		} catch (final Exception e) {
			logger.error(getColumnForLog() + ": Exception.", e);
		}

		return result;

// 		if (eventList != null) {
// 
// 			// LOCK EVENT LIST
// 			eventList.getReadWriteLock().writeLock().lock();
// 
// 			try {
// 
// 				// GET INDEX FOR mappings and options
// 				final int index = mappings.indexOf(_primaryKey);
// 
// 				// PROCEED IF INDEX WAS FOUND
// 				if (index != -1) {
// 					options.set(index, _updatedDisplayText);
// 					// mappings.remove(index);
// // TODO Confirm that eventList is not reordered by GlazedLists code.
// 					eventList.get(index).setListItem(_updatedDisplayText);
// 					result = true;
// 
// 				}
// 
// // TODO may need to call repaint()
// 
// 			} catch (final Exception e) {
// 				logger.error(getColumnForLog() + ": Exception.", e);
// 			} finally {
// 				eventList.getReadWriteLock().writeLock().unlock();
// 			}
// 
// 		}
//
//		return result;
	}

	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText()
	 * <p>
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed
	 */
	@Override
	public void updateSSComponent() {
		// TODO Modify this class similar to updateSSComponent() in SSFormattedTextField and only allow JDBC types that convert to Long or Integer
		try {
			// 2020-10-05_BP: If initialization is taking place then there won't be any mappings so don't try to update anything yet.

			// TODO: how does this happen?
			//if (eventList==null) {
			//	return;
			//}
			if (comboInfo.getItemList().isEmpty()) {
				return;
			}

			// If the user was on this component and the GlazedList had a subset of items, then
			// navigating resulting in a call to updateSSComponent()->setSelectedValue() may try to do a lookup based on
			// the GlazedList subset and generate:
			// Exception in thread "AWT-EventQueue-0" java.lang.IllegalArgumentException: setSelectedIndex: X out of bounds
			//int possibleMatches = getItemCount();
			//logger.debug("{}: Possible matches BEFORE setPopupVisible(false): " + possibleMatches, () -> getColumnForLog());

			//this.setPopupVisible(false);
			//updateUI();

			//possibleMatches = getItemCount();
			//logger.debug("{}: Possible matches AFTER setPopupVisible(false): " + possibleMatches, () -> getColumnForLog());

			// THIS SHOULD BE CALLED AS A RESULT OF SOME ACTION ON THE ROWSET SO RESET THE EDITOR STRINGS BEFORE DOING ANYTHING ELSE
			priorEditorText = "";
			getEditor().setItem(priorEditorText);


			// Combobox primary key column data queried from the database will generally be of data type long.
			// The bound column text should generally be a long integer as well, but trimming to be safe.
			// TODO Consider starting with a Long and passing directly to setSelectedValue(primaryKey). Modify setSelectedValue to accept a Long vs long.
			final String text = getBoundColumnText();

			logger.debug("{}: getBoundColumnText() - " + text, () -> getColumnForLog());

			// GET THE BOUND VALUE STORED IN THE ROWSET
			//if (text != null && !(text.equals(""))) {
			if ((text != null) && !text.isEmpty()) {

				final long primaryKey = Long.parseLong(text);

				logger.debug("{}: Calling setSelectedValue(" + primaryKey + ").", () -> getColumnForLog());

				setSelectedValue(primaryKey);

			} else {
				logger.debug("{}: Calling setSelectedIndex(-1).", () -> getColumnForLog());

				setSelectedIndex(-1);
				//updateUI();
			}

			// TODO Consider commenting this out for performance.
			//String editorString = null;
			//if (getEditor().getItem() != null) {
			//	editorString = getEditor().getItem().toString();
			//}
			//logger.debug("{}: Combo editor string: " + editorString, () -> getColumnForLog());
			logger.debug(() -> {
				String editorString = null;
				if (getEditor().getItem() != null) {
					editorString = getEditor().getItem().toString();
				}
				return getColumnForLog() + ": Combo editor string: " + editorString;
			});

		} catch (final NumberFormatException nfe) {
			logger.error(getColumnForLog() + ": Number Format Exception.", nfe);
		}
	}

	/**
	 * Updates the string thats being displayed.
	 * <p>
	 * If more than one item is present in the combo for that value the first one is
	 * changed.
	 *
	 * @param _existingDisplayText existing display text to be updated
	 * @param _updatedDisplayText  text that should be updated in the combobox
	 *
	 * @return returns true if successful otherwise returns false.
	 */
	public boolean updateStringItem(final String _existingDisplayText, final String _updatedDisplayText) {

		boolean result = false;

		try (ComboInfo.Remodel remodel = comboInfo.getRemodel()) {
			final int index = remodel.getOptions().indexOf(_existingDisplayText);
			result = updateItem(remodel.getMapping(index), _updatedDisplayText);
		}

		//if (options != null) {
		//	final int index = options.indexOf(_existingDisplayText);
		//	result = updateItem(mappings.get(index), _updatedDisplayText);
		//}

		return result;
	}

} // end public class SSDBComboBox
