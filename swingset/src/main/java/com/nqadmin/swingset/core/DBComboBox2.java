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
package com.nqadmin.swingset.core;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.nqadmin.swingset.models.SSListItem;
import com.nqadmin.swingset.models.SSListItemFormat;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.datasources.ConvertType.convertToType;
import static com.nqadmin.swingset.datasources.RowSetOps.getJDBCColumnType;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * Similar to the ComboBox, but used when both the 'bound' values and the
 * 'display' values are pulled from a database table. The bound value is called
 * the 'mapping' and the display value the 'option'. An 'option2' may be
 * specified; in that case the display value for the mapping is a composite of
 * option and option2.Generally the mapping represents a foreign key to another
 * table, and the combobox needs to display a list of one (or more) columns from
 * the other table.<p>
 * Several methods inherited from ComboBox2 directly manipulate the combobox
 * contents. These methods throw UnsupportedOperationException.
 * <p>
 * <b>Warning. This combobox may use GlazedLists which changes the contents
 * of the combo box and an item is automatically
 * inserted when {@link #getAllowNull()} is true. Do not use methods
 * that are based on index in the combo box list, unless you're sure...</b>
 * 
 * For example use 
 * {@link ComboBox2#getChosenKey() getChosenKey()}
 * not something that is based on {@code getSelectedIndex()}.
 * Change the current combo box item with methods
 * such as:
 * {@link ComboBox2#setChosenKey(java.lang.Object) setChosenKey(Long)}
 * and
 * {@link ComboBox2#setChosenDisplayValue(java.lang.Object) setChosenDisplayValue(String)}.
 * Use the methods {@link ComboBox2#hasItems() hasItems() } and
 * {@link ComboBox2#hasSelection() hasSelection() } which take into account
 * {@code getAllowNull()}.
 * <p>
 * Notice that {@link #getChosenKey() }
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
 * refer to {@link com.nqadmin.swingset.models.KeyDisplayValueSwingModel}
 * and especially
 * {@link com.nqadmin.swingset.models.KeyDisplayValueSwingModel.Remodel}
 * <p>
 * Note, if changing both a rowSet and column name consider using the bind()
 * method rather than individual setRowSet() and setColumName() calls.
 * <p>
 * <b>Example</b>: consider two tables
 * <ol>
 * <li>part_data (part_id, part_name, ...)
 * <li>shipment_data (shipment_id, part_id, quantity, ...)
 * </ol>
 * <p>
 * Assume you would like to develop a screen for the shipment_data table and
 * you want to have a screen with a combobox where the user can choose a part
 * and a textbox where the user can specify a quantity.
 * <p>
 * In the combobox you would want to display the part name rather than part_id
 * so that it is easier for the user to choose. At the same time you want to
 * store the id of the part chosen by the user in the shipment table.
 * 
 * {@snippet class=ComboBoxSnippets region=init}
 * 
 * Initially no DisplayValue2.
 * @param <K> key type
 * @param <D> displayValue type
 * @param <D2> displayValue2 type
 */
@SuppressWarnings("serial")
public class DBComboBox2<K,D,D2> extends ComboBox2<K,D,D2>
{
	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * Format an SSListItem. Used to AutoCompleteSupport.install.
	 */
	private final SSListItemFormat listItemFormat;

	/**
	 * Format for any date columns displayed in combo box.
	 */
	// TODO: Use a SSFormat.
	protected String dateFormat = "MM/dd/yyyy";

	/**
	 * The database column used to populate the first visible column of the combo
	 * box.
	 */
	protected String displayColumnName = "";

	/**
	 * counter for # times that execute() method is called - for testing
	 */
	// TODO remove this
	protected int executeCount = 0;

	/**
	 * The column name used to query the values for the bound column keys.
	 * This is generally the PK of the table to which a foreign key is mapped.
	 * NOTE: This is NOT the bound column. It is the source of the keys.
	 */
	protected String primaryKeyColumnName = "";

	/** database connection to populate combobox */
	private Connection connection = null;

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
	 */
	protected String separator = " | ";

	// TODO: configuration option
	private static final ModelType USE_GLAZED_MODEL = ModelType.GLAZED;

	/**
	 * Creates an object of the DBComboBox.
	 */
	// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
	public DBComboBox2() {
		super(USE_GLAZED_MODEL);

		listItemFormat = getListItemFormat();
		listItemFormat.setFormat(JDBCType.DATE, new SimpleDateFormat(dateFormat));
		listItemFormat.setSeparator(separator);
	}
	
	/**
	 * Constructs a DBComboBox with the given parameters. It is best practice
	 * to pair setQuery() and execute() when building a screen where
	 * DBComboBoxes may need to be re-queried so this is the preferred constructor.
	 *
	 * @param connection           database connection to be used.
	 * @param primaryKeyColumnName column name used to query/generate the combo
	 *                             Keys
	 * @param displayColumnName    column name used to query/generate the combo
	 *                             DisplayValues
	 */
	// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
	@SuppressWarnings({ "all", "OverridableMethodCallInConstructor" })
	public DBComboBox2(Connection connection, String primaryKeyColumnName,
			String displayColumnName) {
		this(connection, null, primaryKeyColumnName, displayColumnName);
	}

	/**
	 * Constructs a DBComboBox with the given parameters.
	 *
	 * @param connection         database connection to be used.
	 * @param query                query to be used to retrieve the values to display in
	 *                              the combo from the database.
	 * @param primaryKeyColumnName column name used to query/generate the combo Keys
	 * @param displayColumnName    column name used to query/generate the combo DisplayValues
	 */
	// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
	// TODO: Need to handle multi-column key?
	@SuppressWarnings({"all","OverridableMethodCallInConstructor"})
	public DBComboBox2(Connection connection, String query,
			String primaryKeyColumnName, String displayColumnName) {
		this();
		setConnection(connection);
		setQuery(query);
		setPrimaryKeyColumnName(primaryKeyColumnName);
		setDisplayColumnName(displayColumnName);
	}

	/**
	 * Executes the query specified with setQuery(), populates combobox,
	 * and turns on AutoCompleteSupport.
	 * 
	 * @throws Exception may occur querying data or turning on AutoComplete
	 */
	// See https://stackoverflow.com/questions/15210771/autocomplete-with-glazedlists
	// for info on modifying lists.
	// TODO: What's the deal with the Exception?
	public void execute() throws Exception {

		logger.log(DEBUG, () -> sf("%s setting execute count: %d",
				getColumnForLog(), executeCount++));
		// (re)query data
		queryData();


		// since the list was likely blank when the component was bound we need to update
		// the component again so it can get the text from the list we don't want to do
		// this if the component is unbound as with an DBComboBox used for navigation.
		// TODO: rework combo navigation somehow; maybe there's a navigator "thing".
		if (getRowSet() != null) {
			// 	call using getSSCommon()
			SSUtils.updateSSComponent_HACK(this);
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

	// NOTE: IF A LIST OF THE STRINGS IN COMBOBOX IS WANTED,
	//		 THEN THE FOLLOWING CAN BE USED.

	// List<String> displayValues = new ArrayList<>();
	// try (Model.Remodel remodel = comboInfo.getRemodel()) {
	// 	List<SSListItem> items = remodel.getEventList();
	// 	for(SSListItem item : items) {
	// 		displayValues.add(listItemFormat.format(item));
	// 	}
	// }
	// return displayValues;

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
	 *
	 * @return value corresponding to the selected item in the combo. return null if
	 *         no item is selected.
	 */
	public String getSelectedStringValue() {
		Object currentItem = getSelectedItem();
		return currentItem != null ? listItemFormat.format(currentItem) : null;
	}

	/**
	 * Returns the separator used when multiple columns are displayed
	 *
	 * @return separator used.
	 */
	public String getSeparator() {
		return separator;
	}

	private boolean hasDisplayValue2() {
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
	 * 
	 * Deprecated in SSComponentInterface.
	 * @deprecated Use bind()
	 */
	@Override
	@Deprecated
	public void setBoundColumnName(String boundColumnName) {
		super.setBoundColumnName(boundColumnName);
		adjustForNullItem();
	}

	/**
	 * Populates the list model with the data by fetching it from the database.
	 */
	private void queryData() {
		// this.data.getReadWriteLock().writeLock().lock();
		try (Model.Remodel remodel = keyVisual.getRemodel()) {
			logger.log(TRACE, () -> sf("%s Clearing eventList.", getColumnForLog()));
			remodel.clear();
			nullItem = null;

			logger.log(DEBUG, () -> sf("%s Nulls allowed? [%s].",
									   getColumnForLog(), getAllowNull()));
			adjustForNullItem();

			Statement statement = getConnection().createStatement();
			try (ResultSet rs = statement.executeQuery(getQuery());) {
				// Configure the listItemFormat with this queries column types
				listItemFormat.clear();
				listItemFormat.addElemType(keyVisual.getDisplayValueListItemElemIndex(),
						getJDBCColumnType(rs, rs.findColumn(displayColumnName)));
				if (hasDisplayValue2()) {
					listItemFormat.addElemType(keyVisual.getDisplayValue2ListItemElemIndex(),
							getJDBCColumnType(rs, rs.findColumn(secondDisplayColumnName)));
				}
				
				logger.log(DEBUG, () -> sf("%s Query [%s].", getColumnForLog(), getQuery()));
				
				List<SSListItem> newItems = new ArrayList<>();
				while (rs.next()) {
					// TODO: multikey
					// NOTE: direct ResultSet access.
					// TODO: Can't use RowSetOps.getColumnObject(comp, class)
					//       because RSC take a RowSet (not a ResultSet),
					//       maybe more so because there's the undo/redo stuff.
					K pk = convertToType(rs.getObject(getPrimaryKeyColumnName()),
										 getKeyType());
					D opt = convertToType(rs.getObject(displayColumnName),
										  getDisplayValueType());
					logger.log(TRACE, () -> sf("%s pk: %s, opt: %s",
							pk, getColumnForLog(), opt));
					D2 opt2;
					if (hasDisplayValue2()) {
						opt2 = convertToType(rs.getObject(secondDisplayColumnName),
											 getDisplayValue2Type());
						logger.log(TRACE, () -> sf("%s opt2: %s", getColumnForLog(), opt2));
					} else {
						opt2 = null;
					}
					
					newItems.add(remodel.createKeyDisplayValueItem(pk, opt, opt2));
				}
				remodel.addAll(newItems);
			}
		} catch (final SQLException se) {
			logger.log(Level.ERROR, getColumnForLog() + ": SQL Exception.", se);
		} catch (final java.lang.NullPointerException npe) {
			// TODO: why is NullPointerException here?
			logger.log(Level.ERROR, getColumnForLog() + ": Null Pointer Exception.", npe);
		}
	}

	/**
	 * Sets the Connection to the database to populate combobox.
	 *
	 * @param _connection the connection to set
	 */
	private void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Returns the Connection to the database to populate combobox.
	 *
	 * @return the connection
	 */
	private Connection getConnection() {
		return connection;
	}

	/**
	 * When a display column is of type date you can choose the format in which it
	 * has to be displayed. For the pattern refer SimpleDateFormat in java.text package.
	 *
	 * @param dateFormat pattern in which dates have to be displayed
	 */
	public void setDateFormat(final String dateFormat) {
		final String oldValue = this.dateFormat;
		this.dateFormat = dateFormat;
		firePropertyChange("dateFormat", oldValue, this.dateFormat);
		listItemFormat.setFormat(JDBCType.DATE, new SimpleDateFormat(dateFormat));
	}

	/**
	 * Sets the column name whose values have to be displayed in combo box.
	 *
	 * @param displayColumnName column name whose values have to be displayed.
	 */
	public void setDisplayColumnName(final String displayColumnName) {
		final String oldValue = this.displayColumnName;
		this.displayColumnName = displayColumnName;
		firePropertyChange("displayColumnName", oldValue, this.displayColumnName);
	}

	/**
	 * Sets database column (normally a primary key) from which to query the keys
	 * for the bound column.
	 *
	 * @param primaryKeyColumnName name of the PK value to query for the bound column keys
	 */
	public void setPrimaryKeyColumnName(final String primaryKeyColumnName) {
		final String oldValue = this.primaryKeyColumnName;
		this.primaryKeyColumnName = primaryKeyColumnName;
		firePropertyChange("primaryKeyColumnName", oldValue, this.primaryKeyColumnName);
	}

	/**
	 * Retrieves the database column (normally a primary key) from which
	 * to query the keys for the bound column.
	 *
	 * @return name of the PK value to query for the bound column keys
	 */
	public String getPrimaryKeyColumnName() {
		return primaryKeyColumnName;
	}

	/**
	 * Sets the query used to display items in the combo box.
	 *
	 * @param query query to be used to get values from database (to display combo
	 *               box items)
	 */
	// get rid of this, add query as argument to execute()?
	public void setQuery(final String query) {
		final String oldValue = this.query;
		this.query = query;
		firePropertyChange("query", oldValue, this.query);
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
	 * Sets the second display name. If more than one column have to displayed
	 * then use this. For the parts example given above. If you have a part
	 * description in part table. Then you can display both part name and part description.
	 *
	 * @param secondDisplayColumnName column name whose values have to be displayed
	 *                                 in the combo in addition to the first column
	 *                                 name.
	 */
	public void setSecondDisplayColumnName(final String secondDisplayColumnName) {
		final String oldValue = this.secondDisplayColumnName;
		this.secondDisplayColumnName = secondDisplayColumnName;
		keyVisual.setDisplayValue2Enabled(hasDisplayValue2());
		firePropertyChange("secondDisplayColumnName", oldValue, this.secondDisplayColumnName);
	}

	/**
	 * Set the separator to be used when multiple columns are displayed
	 *
	 * @param separator separator to be used.
	 */
	public void setSeparator(final String separator) {
		final String oldValue = this.separator;
		this.separator = separator;
		firePropertyChange("separator", oldValue, this.separator);
		listItemFormat.setSeparator(this.separator);
	}

	/**
	 * {@inheritDoc }
	 * @throws IllegalStateException if displayValue2 enabled
	 */
	@Override
	public void setChosenDisplayValue(D displayValue) {
		if (hasDisplayValue2()) {
			throw new IllegalStateException("displayValue2 enabled");
		}
		super.setChosenDisplayValue(displayValue);
	}

	/** {@inheritDoc } */
	@Override
	public boolean updateDisplayValue(K key, D displayValue)
	{
		boolean result = false;
		try {
			result = super.updateDisplayValue(key, displayValue);
		} catch (final Exception e) {
			logger.log(Level.ERROR, getColumnForLog() + ": Exception.", e);
		}
		if (!result) {
			logger.log(WARNING, () -> sf("%s: Unable to update Keys of [%s] with DisplayValue of '%s'.",
				getColumnForLog(), key, displayValue));
		}

		return result;
	}

	/**
	 * Unconditionally throws UnsupportedOperationException.
	 * @param displayValues
	 * @param keys 
	 */
	@Override
	public void setDisplayValues(List<D> displayValues, List<K> keys)
	{
		throw new UnsupportedOperationException("DBComboBox doesn't support");
	}

	/**
	 * Unconditionally throws UnsupportedOperationException.
	 * @param displayValues 
	 */
	@Override
	public void setDisplayValues(List<D> displayValues)
	{
		throw new UnsupportedOperationException("DBComboBox doesn't support");
	}

	/**
	 * Unconditionally throws UnsupportedOperationException.
	 * @param <T>
	 * @param enumDisplayValues 
	 */
	@Override
	public <T extends Enum<T>> void setDisplayValues(Class<T> enumDisplayValues) {
		throw new UnsupportedOperationException("DBComboBox doesn't support");
	}

} // end public class DBComboBox
