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
package com.nqadmin.swingset;

import static com.nqadmin.swingset.datasources.RowSetOps.getJDBCColumnType;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.models.OptionMappingSwingModel;
import com.nqadmin.swingset.models.SSListItem;
import com.nqadmin.swingset.models.SSListItemFormat;
import com.nqadmin.swingset.utils.SSUtils;

import ca.odell.glazedlists.EventList;


// SSDBComboBox.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Similar to the SSComboBox, but used when both the 'bound' values and the
 * 'display' values are pulled from a database table.
 * The bound value is called the 'mapping' and the display value the 'option'.
 * An 'option2' may be specified; in that case the display value
 * for the mapping is a composite of option and option2.
 * Generally the mapping 
 * represents a foreign key to another table, and the combobox needs to display
 * a list of one (or more) columns from the other table.
 * <p>
 * <b>Warning. This combobox may use GlazedLists which changes the contents
 * of the combo box and an item is automatically
 * inserted when {@link #getAllowNull()} is true. Do not use methods
 * that are based on index in the combo box list, unless you're sure...</b>
 * 
 * For example use 
 * {@link SSBaseComboBox#getSelectedMapping() getSelectedMapping()}
 * not something that is based on {@code getSelectedIndex()}.
 * Change the current combo box item with methods
 * such as:
 * {@link SSBaseComboBox#setSelectedMapping(java.lang.Object) setSelectedMapping(Long)}
 * and
 * {@link SSBaseComboBox#setSelectedOption(java.lang.Object) setSelectedOption(String)}.
 * Use the methods {@link SSBaseComboBox#hasItems() hasItems() } and
 * {@link SSBaseComboBox#hasSelection() hasSelection() } which take into account
 * {@code getAllowNull()}.
 * <p>
 * Notice that {@link #getSelectedMapping() }
 * returns null in two situations related to {@link #getAllowNull() }
 * <ul>
 *   <li>nothing is selected in this combo box
 *   <li>the <em>nullItem</em> is selected in this combo box
 * </ul>
 * <p>
 * {@code getSelectedItem() == null } indicates there is no
 * combo box selection; it is different than {@code !hasSelection()}
 * when {@code getAllowNull()} is true.
 * <p>
 * If subclasses need to work directly with the combo box model,
 * refer to {@link com.nqadmin.swingset.models.OptionMappingSwingModel}
 * and especially
 * {@link com.nqadmin.swingset.models.OptionMappingSwingModel.Remodel}
 * <p>
 * Note, if changing both a rowSet and column name consider using the bind()
 * method rather than individual setRowSet() and setColumName() calls.
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
 * Connection connection = null;
 * RowSet rowSet = null;
 * SSDataNavigator navigator = null;
 * SSDBComboBox combo = null;
 *
 * try {
 *
 * // CREATE A DATABASE CONNECTION OBJECT
 * Connection connection = new Connection(........);
 *
 * // CREATE AN INSTANCE OF SSJDBCROWSETIMPL
 * JdbcRowsetImpl rowSet = new JdbcRowsetImpl(connection);
 * rowSet.setCommand("SELECT * FROM shipment_data;");
 *
 * // DATA NAVIGATOR CALLS THE EXECUTE AND NEXT FUNCTIONS ON THE SSROWSET.
 * // IF YOU ARE NOT USING THE DATA NAVIGATOR YOU HAVE TO INCLUDE THOSE.
 * // rowSet.execute();
 * // rowSet.next();
 * SSDataNavigator navigator = new SSDataNavigator(rowSet);
 *
 * // QUERY FOR THE COMBOBOX.
 * String query = "SELECT * FROM part_data;";
 *
 * // CREATE AN INSTANCE OF THE SSDBCOMBOBOX WITH THE CONNECTION OBJECT
 * // QUERY AND COLUMN NAMES
 * combo = new SSDBComboBox(connection,query,"part_id","part_name");
 * 
 * // EXECUTE THE QUERY
 * combo.execute();
 * 
 * // THIS BASICALLY SPECIFIES THE COLUMN AND THE ROWSET WHERE UPDATES HAVE
 * // TO BE MADE.
 * combo.bind(rowSet,"part_id");

 * } catch(Exception e) {
 *	// EXCEPTION HANDLER HERE...
 * }
 *
 * // ADD THE SSDBCOMBOBOX TO THE JFRAME
 * getContentPane().add(combo.getComboBox());
 * 
 * }
 * </pre>
 */

public class SSDBComboBox extends SSBaseComboBox<Long, Object, Object>
{
	private static final long serialVersionUID = -4203338788107410027L;

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = SSUtils.getLogger();

	/**
	 * Value to represent that no item has been selected in the combo box.
	 * 
	 * @deprecated check for {@code #getSelectedMapping()==null}
	 */
	@Deprecated
	public static final int NON_SELECTED = Integer.MIN_VALUE + 1;

	/** A convenience for variable declarations. Can not instantiate. */
	private static class Model extends OptionMappingSwingModel<Long,Object,Object> {
		/** Exception if invoked. */
		public Model() { Objects.requireNonNull(null); } 
	}
	// avoid warning for Model() constructor unused
	static { if(Boolean.FALSE) Objects.isNull(new Model()); }

	/**
	 * {@inheritDoc }
	 */
	@Override
	protected SSListItem createNullItem(Model.Remodel remodel) {
		return remodel.createOptionMappingItem(null, null, null);
	}

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
	 * @deprecated can't use
	 */
	@Deprecated
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
	 * @deprecated unneeded
	 */
	@Deprecated
	protected boolean filterSwitch = true;

	/**
	 * Underlying database table primary key values corresponding to text displayed.
	 * <p>
	 * Note that mappings for the SSDBComboBox are Longs whereas in SSComboBox they
	 * are Integers.
	 * @deprecated unneeded
	 */
	@Deprecated
	protected ArrayList<Long> mappings = null;

	/**
	 * Options to be displayed in the combobox (based on a query).
	 * @deprecated unneeded
	 */
	@Deprecated
	protected ArrayList<String> options = null;

	/**
	 * The column name used to query the values for the bound column mappings.
	 * This is generally the PK of the table to which a foreign key is mapped.
	 * NOTE: This is NOT the bound column. It is the source of the mappings.
	 */
	protected String primaryKeyColumnName = "";

//	/**
//	 * String typed by user into combobox
//	 */
//	protected String priorEditorText = "";

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
	 * Alphanumeric separator used to separate values in multi-column comboboxes.
	 * <p>
	 * Changing default from " - " to " | " for 2020 rewrite.
	 */
	//protected String separator = " - ";
	protected String separator = " | ";

	private static final boolean USE_GLAZED_MODEL = true;

	/**
	 * Creates an object of the SSDBComboBox.
	 */
	// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
	public SSDBComboBox() {
		super(USE_GLAZED_MODEL);

		listItemFormat = getListItemFormat();
		listItemFormat.setFormat(JDBCType.DATE, new SimpleDateFormat(dateFormat));
		listItemFormat.setSeparator(separator);
	}
	
	/**
	 * Constructs a SSDBComboBox with the given parameters. It is best practice to
	 * pair setQuery() and execute() when building a screen where SSDBComboBoxes may
	 * need to be requeried so this is the preferred constructor.
	 *
	 * @param _connection           database connection to be used.
	 * @param _primaryKeyColumnName column name used to query/generate the combo
	 *                              Mappings
	 * @param _displayColumnName    column name used to query/generate the combo
	 *                              Options
	 */
	// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
	@SuppressWarnings({ "all", "OverridableMethodCallInConstructor" })
	public SSDBComboBox(final Connection _connection, final String _primaryKeyColumnName,
			final String _displayColumnName) {
		this(_connection, null, _primaryKeyColumnName, _displayColumnName);
	}

	/**
	 * Constructs a SSDBComboBox with the given parameters.
	 *
	 * @param _connection         database connection to be used.
	 * @param _query                query to be used to retrieve the values to display in
	 *                              the combo from the database.
	 * @param _primaryKeyColumnName column name used to query/generate the combo Mappings
	 * @param _displayColumnName    column name used to query/generate the combo Options
	 */
	// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
	@SuppressWarnings({"all","OverridableMethodCallInConstructor"})
	public SSDBComboBox(final Connection _connection, final String _query,
			final String _primaryKeyColumnName, final String _displayColumnName) {
		this();
		setConnection(_connection);
		setQuery(_query);
		setPrimaryKeyColumnName(_primaryKeyColumnName);
		setDisplayColumnName(_displayColumnName);
	}

	/**
	 * Adds an item to the existing list of items in the combo box.
	 *
	 * @param _option item that should be displayed in the combobox
	 * @param _mapping  mapping of _option, typically a primary key
	 */
	public void addOption(Object _option, Long _mapping) {
		addOption(_option, null, _mapping);
	}
	
	/**
	 * Adds an item to the existing list of items in the combo box.
	 *
	 * @param _option item that should be displayed in the combobox
	 * @param _option2  second display item for combobox
	 * @param _mapping  mapping of _option, typically a primary key
	 */
	public void addOption(Object _option, Object _option2, Long _mapping) {
		try (Model.Remodel remodel = optionModel.getRemodel()) {
			final int index = remodel.getMappings().indexOf(_mapping);
			if (index >= 0) {
				logger.warn(() -> String.format("%s: Mapping of [%s] already exists. Creating duplicate Mapping with Option of '%s'.",
					getColumnForLog(), _mapping, _option));
			}
			remodel.add(_mapping, _option, _option2);
		} catch (final Exception e) {
			logger.error(getColumnForLog() + ": Exception.", e);
		}
	}

	/**
	 * Adds an item to the existing list of items in the combo box.
	 *
	 * @param _displayText text that should be displayed in the combobox
	 * @param _primaryKey  primary key value corresponding the the display text
	 * @deprecated use {@link #addOption(java.lang.Object, java.lang.Long) }
	 */
	@Deprecated
	public void addItem(final String _displayText, final long _primaryKey) {

		// TODO Determine if any change is needed to actually add item to combobox.

		addOption(_displayText, _primaryKey);
	}

	/**
	 * Adds an item to the existing list of items in the combo box.
	 *
	 * @param _name  name that should be displayed in the combo
	 * @param _value value corresponding the the name
	 * @deprecated use {@link #addOption(java.lang.Object, java.lang.Long) }
	 */
	@Deprecated
	protected void addStringItem(final String _name, final String _value) {
		addItem(_name, Long.valueOf(_value));

	}

	/**
	 * Removes an item from the combobox's item list where the
	 * list item's mapping equals the param.
	 * <p>
	 * If more than one item is present in the combo for that value the first one is
	 * changed.
	 *
	 * @param _mapping mapping value for the item that should be removed
	 *
	 * @return returns true on successful deletion otherwise returns false.
	 */
	public boolean removeMapping(final Long _mapping) {

		boolean result = false;

		try (Model.Remodel remodel = optionModel.getRemodel()) {
			// GET INDEX FOR mappings and options
			int index = remodel.getMappings().indexOf(_mapping);
			// PROCEED IF INDEX WAS FOUND
			if (index != -1) {
				remodel.remove(index);
				result = true;
			}
		} catch (final Exception e) {
			logger.error(getColumnForLog() + ": Exception.", e);
		}

		return result;
	}

	/**
	 * Removes an item from the combobox and underlying lists based on the record
	 * primary key provided.
	 *
	 * @param _primaryKey primary key value for the item that should be removed
	 *
	 * @return returns true on successful deletion otherwise returns false.
	 * @deprecated use {@link #removeMapping(java.lang.Long) }
	 */
	@Deprecated
	public boolean deleteItem(final long _primaryKey) {
		return removeMapping(_primaryKey);

		// TODO Determine if any change is needed to actually remove item from combobox.

	}

	/**
	 * Removes the list item that has an option that equals the parameter.
	 * <p>
	 * If more than one item is present in the combo that matches, only
	 * the first one is removed.
	 *
	 * @param _option list item with this currentSelectedOption is deleted
	 *
	 * @return returns true on successful deletion otherwise returns false.
	 * @deprecated no replacement, throws exception
	 */
	@Deprecated
	public boolean deleteStringItem(final Object _option) {
		throw new UnsupportedOperationException();


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

		// See https://stackoverflow.com/questions/15210771/autocomplete-with-glazedlists for info on modifying lists.

		// since the list was likely blank when the component was bound we need to update the component again so it can get the text from the list
		// we don't want to do this if the component is unbound as with an SSDBComboBox used for navigation.
		if (getRowSet() != null) {
			// 2020-12-03_BP: If we call updateSSComponent() directly from a component, we MUST turn the component listeners off first, but cleaner to
			// 	call using getSSCommon()
			getSSCommon().updateSSComponent();
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
	 * @deprecated no replacement
	 */
	// TODO: public method? Why? How/where used.
	//		Make private, see what happens
	@Deprecated
	protected EventList<SSListItem> getEventList() {
		throw new UnsupportedOperationException();
		//return eventList;
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

		logger.warn(getColumnForLog() + ": This method was never properly implemented so it has been Deprecated and just returns 0. \n", new Exception());
		return 0;
	}

	/**
	 * Get the mappings currently in use.
	 * <p>
	 * <b>When getAllowNull() is true, the first list item is null/""</b>
	 * @return the mappings
	 */
	public List<Long> getMappings() {
		// TODO: the signature should be List<>, not ArrayList.
		//		 Changed signature to avoid strange casting exception
		//		 having to do with unmodifiable lists...
		//return mappings;
		return optionModel.getMappings();
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

		//return optionModel.getSize();
		return optionModel.getItemList().size();

		// if (eventList != null) {
		// 	result = eventList.size();
		// }

		// return result;
	}

	/**
	 * Get the options currently in use.
	 * <p>
	 * <b>When getAllowNull() is true, the first list item is null/""</b>
	 * @return the options
	 */
	public List<Object> getOptions() {
		// NOTE: this was List<String>, now List<Object>
		// NOTE: changed signature to List<> from ArrayList<>.
		// TODO: is this used?
		//return options;

		return optionModel.getOptions();

		// NOTE: IF A LIST OF THE STRINGS IN COMBOBOX IS WANTED,
		//		 THEN THE FOLLOWING CAN BE USED.

		// List<String> options = new ArrayList<>();
		// try (Model.Remodel remodel = comboInfo.getRemodel()) {
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
	 * Retrieves the database column (normally a primary key) from which
	 * to query the mappings for the bound column.
	 *
	 * @return name of the PK value to query for the bound column mappings
	 */
	public String getPrimaryKeyColumnName() {
		return primaryKeyColumnName;
	}

	/**
	 * Returns the query used to retrieve values from database for the combo box.
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
		// try (Model.Remodel remodel = comboInfo.getRemodel()) {
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
	 * 
	 * @deprecated use {@link #getSelectedMapping() }
	 */
	@Deprecated
	public Long getSelectedValue() {
		return getSelectedMapping();
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
	 *  deprecated
	 * @param _rs deprecated
	 * @param _columnName deprecated
	 * @return  deprecated
	 * @deprecated unneeded, the old method, now handled in format
	 */
	@Deprecated
	protected String getStringValue(final ResultSet _rs, final String _columnName) {
		throw new UnsupportedOperationException();
	}

	private boolean hasOption2() {
		return secondDisplayColumnName != null && !secondDisplayColumnName.isEmpty();
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	protected boolean isComboBoxNavigator() {
		return getBoundColumnName() == null;
	}

	/**
	 * After this, make some adjustments.
	 * {@inheritDoc }
	 */
	@Override
	public void setBoundColumnName(String _boundColumnName) {
		ssCommon.setBoundColumnName(_boundColumnName);
		adjustForNullItem();
	}

	/**
	 * Populates the list model with the data by fetching it from the database.
	 */
	private void queryData() {

		ResultSet rs; // TODO: autocloseable

		// this.data.getReadWriteLock().writeLock().lock();
		try (Model.Remodel remodel = optionModel.getRemodel()) {
			logger.trace("{}: Clearing eventList.", () -> getColumnForLog());
			remodel.clear();
			nullItem = null;

			logger.debug("{}: Nulls allowed? [{}].", () -> getColumnForLog(), () -> getAllowNull());
			adjustForNullItem();

			Statement statement = ssCommon.getConnection().createStatement();
			rs = statement.executeQuery(getQuery());

			//optionColumnType = getJDBCColumnType(rs, rs.findColumn(displayColumnName));

			// Configure the listItemFormat with this queries column types
			listItemFormat.clear();
			listItemFormat.addElemType(optionModel.getOptionListItemElemIndex(),
					getJDBCColumnType(rs, rs.findColumn(displayColumnName)));
			if (hasOption2()) {
				listItemFormat.addElemType(optionModel.getOption2ListItemElemIndex(),
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
				newItems.add(remodel.createOptionMappingItem(pk, opt, opt2));
			}
			remodel.addAll(newItems);
			rs.close();
		} catch (final SQLException se) {
			logger.error(getColumnForLog() + ": SQL Exception.", se);
		} catch (final java.lang.NullPointerException npe) {
			// TODO: why is NullPointerException here?
			logger.error(getColumnForLog() + ": Null Pointer Exception.", npe);
		}
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
		listItemFormat.setFormat(JDBCType.DATE, new SimpleDateFormat(_dateFormat));
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
		logger.warn(getColumnForLog() + ": This method has been Deprecated because GlazedList filtering is now fully integrated.\n", new Exception());
	}

	/**
	 * Sets database column (normally a primary key) from which to query the mappings for the bound column.
	 *
	 * @param _primaryKeyColumnName name of the PK value to query for the bound column mappings
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
		optionModel.setOption2Enabled(hasOption2());
		firePropertyChange("secondDisplayColumnName", oldValue, secondDisplayColumnName);
	}

	/**
	 * {@inheritDoc }
	 * @throws IllegalStateException if option2 enabled
	 */
	//public void setSelectedOption(final String _option) {
	@Override
	public void setSelectedOption(final Object _option) {
		if (hasOption2()) {
			throw new IllegalStateException("option2 enabled");
		}
		super.setSelectedOption(_option);
	}

	/**
	 * 
	 * @param _value option
	 * @deprecated use {@link #setSelectedOption(Object) }
	 */
	@Deprecated
	public void setSelectedStringValue(final String _value) {
		setSelectedOption(_value);
	}

	/**
	 * Sets the selected ComboBox item according to the specified mapping/key.
	 * 
	 * @param _value database record mapping/key
	 * @deprecated use {@link SSBaseComboBox#setSelectedMapping(java.lang.Object) setSelectedMapping(Long)}
	 */
	@Deprecated
	public void setSelectedValue(final long _value) {
		setSelectedMapping(_value);
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
	 * 
	 * @deprecated Use {@link #setSeparator(String)}
	 */
	@Deprecated
	public void setSeperator(final String _separator) {
		setSeparator(_separator);
	}

	/**
	 * Update an option of an item in the combobox's item list based on a mapping
	 * value.
	 * <p>
	 * If more than one item is present in the combo for that mapping, only the
	 * first one is changed.
	 *
	 * @param _mapping typically a primary key value corresponding to the displayed
	 *                 currentSelectedOption to be updated
	 * @param _option  currentSelectedOption that should be updated in the combobox
	 *
	 * @return returns true if update is successful otherwise returns false.
	 */
	public boolean updateOption(final Long _mapping, final String _option) {

		// 2021-02-28: @errael patched this method to deal with inconsistent
		// updating of combo editor. See https://github.com/bpangburn/swingset/issues/85
		
		boolean result = false;

		try (Model.Remodel remodel = optionModel.getRemodel()) {
			final int index = remodel.getMappings().indexOf(_mapping);
			if (index >= 0) {
				boolean isSelectedItem = Objects.equals(_mapping, getSelectedMapping());
				remodel.setOption(index, _option);
				result = true;
				// Changing what's in the ComboEditor, which may be done indirectly when
				// modifying the current item, might change the currently selected item when
				// GlazedList is set to STRICT. So something is needed to insure that the
				// selected mapping before the change is the selected mapping after the change.
				// Otherwise the first item in the list becomes selected. If the combo is used
				// for navigation, this can trigger a change in the current row. 
				//
				// The call to setSelectedItem() below has the added benefit of working around
				// a possible bug in GlazedList (see https://github.com/glazedlists/glazedlists/issues/702),
				// but it's likely best to keep this block even if the issue is determine to be
				// a bug and resolved.
				if (isSelectedItem) {
					// Modifying the underlying list item that corresponds to the
					// current selection; strict glazed may change the selection.
					// Select the modified item so the same mapping is selected.
					SSListItem item = remodel.get(index);
					setSelectedItem(item);
				}
// TODO Confirm that eventList is not reordered by GlazedLists code.
			}
		} catch (final Exception e) {
			logger.error(getColumnForLog() + ": Exception.", e);
		}
		
		if (!result) {
			logger.warn(() -> String.format("%s: Unable to update Mapping of [%s] with Option of '%s'.",
				getColumnForLog(), _mapping, _option));
		}

		return result;
	}

	/**
	 * Updates an item available in the combobox and associated lists.
	 * <p>
	 * If more than one item is present in the combo for that value, only the first
	 * one is changed.
	 * <p>
	 * NOTE: To retain changes made to current RowSet call updateRow before
	 * calling the updateItem on SSDBComboBox. (Only if you are using the
	 * SSDBComboBox and SSDataNavigator for navigation in the screen. If you are not
	 * using the SSDBComboBox for navigation then no need to call updateRow on the
	 * RowSet. Also if you are using only SSDBComboBox for navigation you need not
	 * call the updateRow.)
	 *
	 * @param _primaryKey         primary key value corresponding the the display
	 *                            text to be updated
	 * @param _updatedDisplayText text that should be updated in the combobox
	 *
	 * @return returns true if update is successful otherwise returns false.
	 * @deprecated use {@link #updateOption(java.lang.Long, java.lang.String) }
	 */
	@Deprecated
	public boolean updateItem(final long _primaryKey, final String _updatedDisplayText) {
		return updateOption(_primaryKey, _updatedDisplayText);

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
	 * @deprecated use {@link #updateOption(java.lang.Long, java.lang.String) }
	 */
	@Deprecated
	public boolean updateStringItem(final String _existingDisplayText, final String _updatedDisplayText) {
		throw new UnsupportedOperationException();
	}

} // end public class SSDBComboBox
