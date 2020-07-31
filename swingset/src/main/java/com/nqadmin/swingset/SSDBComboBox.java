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
 ******************************************************************************/

package com.nqadmin.swingset;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JComboBox;

import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.formatting.helpers.SSListItem;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

/**
 * SSDBComboBox.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Similar to the SSComboBox, but used when both the 'bound' values and the
 * 'display' values are pulled from a database table. Generally the bound value
 * represents a foreign key to another table, and the combobox needs to display
 * a list of one (or more) columns from the other table.
 *
 * Note, if changing both a sSRowSet and column name consider using the bind()
 * method rather than individual setSSRowSet() and setColumName() calls.
 *
 * e.g.
 *
 * Consider two tables: 1. part_data (part_id, part_name, ...) 2. shipment_data
 * (shipment_id, part_id, quantity, ...)
 *
 * Assume you would like to develop a screen for the shipment table and you want
 * to have a screen with a combobox where the user can choose a part and a
 * textbox where the user can specify a quantity.
 *
 * In the combobox you would want to display the part name rather than part_id
 * so that it is easier for the user to choose. At the same time you want to
 * store the id of the part chosen by the user in the shipment table.
 *
 * SSConnection connection = null; SSJdbcRowSetImpl sSRowSet = null;
 * SSDataNavigator navigator = null; SSDBComboBox combo = null;
 *
 * try {
 *
 * // CREATE A DATABASE CONNECTION OBJECT SSConnection connection = new
 * SSConnection(........);
 *
 * // CREATE AN INSTANCE OF SSJDBCROWSETIMPL SSJdbcRowsetImpl sSRowSet = new
 * SSJdbcRowsetImpl(connection); sSRowSet.setCommand("SELECT * FROM
 * shipment_data;");
 *
 * // DATA NAVIGATOR CALLS THE EXECUTE AND NEXT FUNCTIONS ON THE SSROWSET. // IF
 * YOU ARE NOT USING THE DATA NAVIGATOR YOU HAVE TO INCLUDE THOSE. //
 * sSRowSet.execute(); // sSRowSet.next(); SSDataNavigator navigator = new
 * SSDataNavigator(sSRowSet);
 *
 * // QUERY FOR THE COMBOBOX. String query = "SELECT * FROM part_data;";
 *
 * // CREATE AN INSTANCE OF THE SSDBCOMBOBOX WITH THE CONNECTION OBJECT // QUERY
 * AND COLUMN NAMES combo = new
 * SSDBComboBox(connection,query,"part_id","part_name");
 *
 * // THIS BASICALLY SPECIFIES THE COLUMN AND THE SSROWSET WHERE UPDATES HAVE //
 * TO BE MADE. combo.bind(sSRowSet,"part_id"); combo.execute();
 *
 * // CREATE A TEXTFIELD JTextField myText = new JTextField();
 * myText.setDocument(new SSTextDocument(sSRowSet, "quantity");
 *
 * } catch(Exception e) { // EXCEPTION HANDLER HERE... }
 *
 *
 * // ADD THE SSDBCOMBOBOX TO THE JFRAME
 * getContentPane().add(combo.getComboBox());
 *
 * // ADD THE JTEXTFIELD TO THE JFRAME getContentPane().add(myText);
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
		public void actionPerformed(ActionEvent ae) {

			removeSSRowSetListener();

			int index = getSelectedIndex();

			if (index == -1) {
				setBoundColumnText(null);
				System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.SSDBComboListener.actionPerformed() - setting " + getBoundColumnName() + " to null.");
			} else {
				setBoundColumnText(String.valueOf(getSelectedValue()));
				System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.SSDBComboListener.actionPerformed() - setting " + getBoundColumnName() + " to " + getSelectedValue() + ".");
			}

			addSSRowSetListener();
		}
	}
	
	/**
	 * Listener(s) to deal with the GlazedList popup when a SSDBComboBox loses focus.
	 */
	protected class SSDBComboBoxFocusListener implements FocusListener, Serializable {

		/**
		 * unique serial ID
		 */
		private static final long serialVersionUID = -8229894238917299438L;

		@Override
		public void focusGained(FocusEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.SSDBComboFocusListener.focusLost().");
			
		}
	}

	/**
	 * Value to represent that no item has been selected in the combo box.
	 */
	public static final int NON_SELECTED = (int) ((Math.pow(2, 32) - 1) / (-2));

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -4203338788107410027L;

	
	/**
	 * Indicates if GlazedList autocompletion has already been installed
	 */
	private boolean autoCompleteInstalled = false;

//	/**	
//	 * Model to be used for holding and filtering data in combo box.
//	 */
//	 protected SelectorComboBoxModel selectorCBM = new SelectorComboBoxModel();

//    /**
//     * Text field bound to the SSRowSet.
//     * Bee changed to public
//     */
//    public JTextField textField = new JTextField();

//    /**
//     * Database connection used to execute queries for combo population.
//     */
//    protected SSConnection sSConnection = null;

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
	 * String typed by user into combobox
	 */
	protected String priorTypedText = "";

	/**
	 * Map of string/value pairings for the ComboBox (generally the text to be
	 * display (SSListItem) and its corresponding primary key)
	 */
	protected EventList<SSListItem> eventList;

	/**
	 * @return the eventList
	 */
	public EventList<SSListItem> getEventList() {
		return eventList;
	}

	/**
	 * @param eventList the eventList to set
	 */
	public void setEventList(EventList<SSListItem> eventList) {
		this.eventList = eventList;
	}

	/**
	 * counter for # times that execute() method is called - for testing
	 */
	// TODO remove this
	protected int executeCount = 0;

	/**
	 * boolean value for activating or disabling the filter
	 * 
	 * Appears to determine if GlazedList is used for filtering or original
	 * keystroke listener/filter.
	 */
	protected boolean filterSwitch = true;

	/**
	 * True if inside a call to updateDisplay()
	 */
	//protected volatile boolean inUpdateDisplay = false;

//    /**
//     * Number of items in the combo box.
//     */
//    protected int numberOfItems = 0;

//    /**
//     * SSRowSet from which component will get/set values.
//     */
//    protected SSRowSet sSRowSet;

//    /**
//     * SSRowSet column to which the component will be bound.
//     */
//    protected String columnName = "";

//    /**
//     * Component listener.
//     */
//    private final MyComboListener cmbListener = new MyComboListener();

//    /**
//     * Bound text field document listener.
//     */
//    protected final MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();

//    /**
//     * Keystroke-based item selection listener.
//     */
//    protected final MyKeyListener myKeyListener = new MyKeyListener();
//    
//    /**
//     * Listener for PopupMenu
//     */
//    protected MyPopupMenuListener myPopupMenuListener = new MyPopupMenuListener();
//    
//    /**
//     * Listener for Filter
//     */
//    protected FilterFocusListener filterFocusListener = new FilterFocusListener();

	/**
	 * Underlying database table primary key values corresponding to text displayed.
	 * 
	 * Note that mappings for the SSDBComboBox are Longs whereas in SSComboBox they
	 * are Integers.
	 */
	protected ArrayList<Long> mappings = null;

	/**
	 * Options to be displayed in the combobox (based on a query).
	 */
	protected ArrayList<String> options = null;

	/**
	 * @return the mappings
	 */
	public ArrayList<Long> getMappings() {
		return mappings;
	}

	/**
	 * @param mappings the mappings to set
	 */
	public void setMappings(ArrayList<Long> mappings) {
		this.mappings = mappings;
	}

	/**
	 * @return the options
	 */
	public ArrayList<String> getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(ArrayList<String> options) {
		this.options = options;
	}

	/**
	 * The column name whose value is written back to the database when the user
	 * chooses an item in the combo box. This is generally the PK of the table to
	 * which a foreign key is mapped.
	 */
	protected String primaryKeyColumnName = "";

	/**
	 * @return the primaryKeyColumnName
	 */
	public String getPrimaryKeyColumnName() {
		return primaryKeyColumnName;
	}

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
	 * 
	 * Changing from " - " to " | " for 2020 rewrite.
	 */
	protected String separator = " - ";

	/**
	 * Common fields shared across SwingSet components
	 */
	protected SSCommon ssCommon;

	/**
	 * Component listener.
	 */
	protected final SSDBComboBoxListener ssDBComboBoxListener = new SSDBComboBoxListener();
	
	/**
	 * Focus listener.
	 */
	protected final SSDBComboBoxFocusListener ssDBComboBoxFocusListener = new SSDBComboBoxFocusListener();

	/**
	 * Creates an object of the SSDBComboBox.
	 */
	public SSDBComboBox() {
		super();
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
		addFocusListener(ssDBComboBoxFocusListener);
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
	public SSDBComboBox(SSConnection _ssConnection, String _query, String _primaryKeyColumnName,
			String _displayColumnName) {
		super();
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
		setSSConnection(_ssConnection);
		// this.sSConnection = _sSConnection;
		setQuery(_query);
		setPrimaryKeyColumnName(_primaryKeyColumnName);
		setDisplayColumnName(_displayColumnName);
		// init();
		addFocusListener(ssDBComboBoxFocusListener);
	}

//    /**
//     * Sets the new SSRowSet for the combo box.
//     *
//     * @param _sSRowSet  SSRowSet to which the combo has to update values.
//     */
//    public void setSSRowSet(SSRowSet _sSRowSet) {
//        SSRowSet oldValue = this.sSRowSet;
//        this.sSRowSet = _sSRowSet;
//        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);
//        bind();
//    }

//    /**
//     * Returns the SSRowSet being used to get the values.
//     *
//     * @return returns the SSRowSet being used.
//     */
//    public SSRowSet getSSRowSet() {
//        return this.sSRowSet;
//    }

//    /**
//     * Sets the connection object to be used.
//     *
//     * @param _sSConnection    connection object used for database.
//     */
//    public void setSSConnection(SSConnection _sSConnection) {
//        SSConnection oldValue = this.sSConnection;
//        this.sSConnection = _sSConnection;
//        firePropertyChange("sSConnection", oldValue, this.sSConnection);
//        bind();
//    }

//    /**
//     * Returns connection object used to get values from database.
//     *
//     * @return returns a SSConnection object.
//     */
//    public SSConnection getSSConnection() {
//        return this.sSConnection;
//    }

	/**
	 * Adds an item to the existing list of items in the combo box.
	 *
	 * @param _displayText text that should be displayed in the combobox
	 * @param _primaryKey  primary key value corresponding the the display text
	 */
	public void addItem(String _displayText, long _primaryKey) {

		// LOCK EVENT LIST
		eventList.getReadWriteLock().writeLock().lock();

		// INITIALIZE LISTS IF NULL
		if (eventList == null)
			eventList = new BasicEventList<>();
		if (mappings == null)
			mappings = new ArrayList<Long>();
		if (options == null)
			options = new ArrayList<String>();

		try {

			// create new list item
			SSListItem listItem = new SSListItem(_primaryKey, _displayText);

			// add to lists
			eventList.add(listItem);
			mappings.add(listItem.getPrimaryKey());
			options.add(listItem.getListItem());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}

// TODO Determine if any change is needed to actually add item to combobox. 		

	}

	/**
	 * Adds listeners for Component, RowSet, Keyboard, and PopupMenu
	 */
	public void addListeners() {
		SSComponentInterface.super.addListeners();
		// addKeyListener(this.myKeyListener);
		// addPopupMenuListener(this.myPopupMenuListener);
	}

//    /**
//     * Sets the column name for the combo box
//     *
//     * @param _columnName   name of column
//     */
//    public void setColumnName(String _columnName) {
//        String oldValue = this.columnName;
//        this.columnName = _columnName;
//        firePropertyChange("columnName", oldValue, this.columnName);
//        bind();
//    }

//    /**
//     * Returns the column name to which the combo is bound.
//     *
//     * @return returns the column name to which to combo box is bound.
//     */
//    public String getColumnName() {
//        return this.columnName;
//    }

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
	public void addStringItem(String _name, String _value) {
		addItem(_name, Long.valueOf(_value));

	}

	/**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * 
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
	 * 
	 * If more than one item is present in the combo for that value the first one is
	 * changed.
	 * 
	 * @param _primaryKey primary key value for the item that should be removed
	 * 
	 * @return returns true on successful deletion otherwise returns false.
	 */
	public boolean deleteItem(long _primaryKey) {

		boolean result = false;

		if (eventList != null) {

			// LOCK EVENT LIST
			eventList.getReadWriteLock().writeLock().lock();

			try {

				// GET INDEX FOR mappings and options
				int index = mappings.indexOf(_primaryKey);

				// PROCEED IF INDEX WAS FOUND
				if (index != -1) {
					options.remove(index);
					mappings.remove(index);
// TODO Confirm that eventList is not reordered by GlazedLists code.					
					eventList.remove(index);
					result = true;

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				eventList.getReadWriteLock().writeLock().unlock();
			}

		}

		return result;

// TODO Determine if any change is needed to actually remove item from combobox. 		

	}

	/**
	 * Removes the display text provided from the combobox and removes any
	 * corresponding list items.
	 * 
	 * If more than one item is present in the combo for the specified value, only
	 * the first one is removed.
	 *
	 * @param _displayText value of the item to be deleted.
	 *
	 * @return returns true on successful deletion otherwise returns false.
	 */
	public boolean deleteStringItem(String _displayText) {

		boolean result = false;

		if (options != null) {
			int index = options.indexOf(_displayText);
			result = deleteItem(mappings.get(index));
		}

		return result;

	}

	public void execute() throws Exception {

		//System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.execute() - setting execute count: " + executeCount++);
		// (re)query data
		queryData();

//		DefaultEventComboBoxModel<SSListItem> model = new DefaultEventComboBoxModel<SSListItem>(eventList);
//		this.setModel(model);

//TODO do we need to install once or after every query???
// Per: https://stackoverflow.com/questions/15210771/autocomplete-with-glazedlists
// use eventList.addAll() to modify list contents and the 

// NOTE: install method makes the ComboBox editable
//		// should already in the event dispatch thread so don't use invokeAndWait()
		if (!autoCompleteInstalled) {
			AutoCompleteSupport<SSListItem> autoComplete = AutoCompleteSupport.install(this, eventList);
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
		return this.dateFormat;
	}

	/**
	 * Returns the column name whose values are displayed in the combo box.
	 *
	 * @return returns the name of the column used to get values for combo box
	 *         items.
	 */
	public String getDisplayColumnName() {
		return this.displayColumnName;
	}

	/**
	 * Provides the initial number of items in the list underlying the CombobBox
	 * 
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
		System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.getInitialNumberOfItems() - this method was never properly implemented so it has been Deprecated and just returns 0.");
		Thread.dumpStack();
		return 0;
	}

	/**
	 * Returns the number of items present in the combo box.
	 *
	 * This is a read-only bean property.
	 *
	 * @return returns the number of items present in the combo box.
	 */
	@Deprecated
	public int getNumberOfItems() {
// TODO Determine where/how this is/was used.   
		int result = 0;

		if (eventList != null)
			result = eventList.size();

		return result;
	}

	/**
	 * Returns the query used to retrieve values from database for the combo box.
	 *
	 * @return returns the query used.
	 */
	public String getQuery() {
		return this.query;
	}

	/**
	 * Returns the second column name whose values are also displayed in the combo
	 * box.
	 *
	 * @return returns the name of the column used to get values for combo box
	 *         items. returns NULL if the second display column is not provided.
	 */
	public String getSecondDisplayColumnName() {
		return this.secondDisplayColumnName;
	}

	/**
	 * Returns the text displayed in the combobox.
	 *
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @return value corresponding to the selected item in the combo. return null if
	 *         no item is selected.
	 */
	public String getSelectedStringValue() {
		
		String result = null;
		
		SSListItem currentItem = (SSListItem)getSelectedItem();
		
		if (currentItem!=null) {
			result = currentItem.getListItem();
		}
		

		return result;

//        int index = getSelectedIndex();
//
//        if (index == -1) {
//            return null;
//        }
// 
//        return ""+this.selectorCBM.getSelectedBoundData(index);
	} // public String getSelectedStringValue() {
//    
//    /**
//     * Map of string/value pairings for the ComboBox.
//     */
//    public Map<String, Long> itemMap;
//    
//    /**
//     * Executes the query and adds items to the combo box based on the values
//     * retrieved from the database.
//     * @throws SQLException 	SQLException
//     * @throws Exception 	Exception
//     */
//    public void execute() throws SQLException, Exception {
//        // TURN OFF LISTENERS
//            removeListeners();
//
//            if (this.query.equals("")) {
//                throw new Exception("Query is empty");
//            }
//        	this.selectorCBM.setQuery(this.query);
//        	this.selectorCBM.setDateFormat(this.dateFormat);
//        	this.selectorCBM.setPrimaryKeyColumn(this.primaryKeyColumnName);
//    		this.selectorCBM.setDisplayColumn(this.displayColumnName);
//    		this.selectorCBM.setSecondDisplayColumn(this.secondDisplayColumnName);
//    		this.selectorCBM.setSeparator(this.seperator);
//    		this.selectorCBM.setSSConnection(this.sSConnection);
//    		this.selectorCBM.refresh();
//    		setModel(this.selectorCBM);
//    		this.itemMap = this.selectorCBM.itemMap;
//    		this.numberOfItems = this.selectorCBM.getSize();
//
//    		// UPDATE DISPLAY WILL ADD THE LISTENERS AT THE END SO NO NEED TO ADD IT AGAIN.
//            updateDisplay();
//            
//            //ADD THE LISTENERS BACK
//            addListeners();
//
//    }

	/**
	 * Returns the underlying database record primary key value corresponding to the
	 * currently selected item in the combobox.
	 *
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @return returns the value associated with the selected item OR -1 if nothing
	 *         is selected.
	 */
	public long getSelectedValue() {

		Long result;

		if (getSelectedIndex() == -1) {
			result = (long) NON_SELECTED;
		} else {
			result = mappings.get(getSelectedIndex());
			if (result==null) {
				result = (long) NON_SELECTED;
			}
		}

		return result;
	}

//    /**
//     * Sets the currently selected value
//     *
//     * Currently not a bean property since there is no associated variable.
//     *
//     * @param _value    value to set as currently selected.
//     */
//    public void setSelectedValue(long _value) {
//        this.textField.setText(String.valueOf(_value));
//    }

	/**
	 * Returns the separator used when multiple columns are displayed
	 *
	 * @return separator used.
	 */
	public String getSeparator() {
		return this.separator;
	}

//    /**
//     * Returns the bound key value of the currently selected item.
//     *
//     * Currently not a bean property since there is no associated variable.
//     *
//     * @return value corresponding to the selected item in the combo.
//     *     return -1 if no item is selected.
//     */
//    public long getSelectedValue() {
//    	
//// TODO revisit returning -1 if nothing is selected as that could be a legitimate bound pk value (unlikely)	
//    	
//    	long returnValue = -1;
//    	
//        int index = getSelectedIndex();
//
//        if (index == -1) {
//            // NOTHING TO DO return -1;
//        } else {
//        	returnValue = comboMap.
//        }
//        
//        returnValue 
//		long returnVal =  Long.valueOf(this.selectorCBM.getSelectedBoundData(index).toString());
//        
//        
//        return returnVal;
//        
//    }

	/**
	 * Returns the separator used when multiple columns are displayed
	 * 
	 * Deprecated for misspelling.
	 *
	 * @return separator used.
	 */
	@Deprecated
	public String getSeperator() {
		return this.separator;
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

	/**
	 * Converts the database column value into string. Only date columns are
	 * formated as specified by dateFormat variable all other column types are
	 * retrieved as strings
	 * 
	 * @param _rs         ResultSet containing database column to convert to string
	 * @param _columnName database column to convert to string
	 * @return string value of database column
	 */
	protected String getStringValue(final ResultSet _rs, final String _columnName) {
		String strValue = "";
		try {
			int type = _rs.getMetaData().getColumnType(_rs.findColumn(_columnName));
			switch (type) {
			case Types.DATE:
				SimpleDateFormat myDateFormat = new SimpleDateFormat(dateFormat);
				strValue = myDateFormat.format(_rs.getDate(_columnName));
				break;
			default:
				strValue = _rs.getString(_columnName);
				break;
			}
			if (strValue == null) {
				strValue = "";
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return strValue;

	}

	/**
	 * Populates the list model with the data by fetching it from the database.
	 */
	private void queryData() {

		if (eventList != null) {
// TODO look at .dispose() vs .clear()
			
System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.queryData() - clearing eventList.");			
			
			//eventList.dispose();
			eventList.clear();
		} else {
			eventList = new BasicEventList<>();
		}
		if (mappings != null) {
			mappings.clear();
		} else {
			mappings = new ArrayList<Long>();
		}
		if (options != null) {
			options.clear();
		} else {
			options = new ArrayList<String>();
		}

		eventList.getReadWriteLock().writeLock().lock();

		// this.listItemMap = new HashMap<>();
		Long primaryKey = null;
		String firstColumnString = null;
		String secondColumnString = null;
		SSListItem listItem = null;
		ResultSet rs = null;

		// this.data.getReadWriteLock().writeLock().lock();
		try {
System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.queryData() - nulls allowed? " + getAllowNull());			
			// 2020-07-24: adding support for a nullable first item if nulls are supported
			if (getAllowNull()) {
				listItem = new SSListItem(null, "");
System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.queryData() - adding blank listItem: " + listItem);
				eventList.add(listItem);
				mappings.add(listItem.getPrimaryKey());
				options.add(listItem.getListItem());
			}

			Statement statement = ssCommon.getSSConnection().getConnection().createStatement();
			rs = statement.executeQuery(getQuery());
			
//System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.queryData() - query: " + getQuery());	
			// eventList.clear();
			// int i = 0;
			while (rs.next()) {
				// extract primary key
				primaryKey = rs.getLong(getPrimaryKeyColumnName());

				// extract first column string
				// getStringValue() takes care of formatting dates
				firstColumnString = getStringValue(rs, this.displayColumnName).trim();
				//System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.queryData() - firstColumnString: " + firstColumnString);

				// extract second column string, if applicable
				// getStringValue() takes care of formatting dates
				secondColumnString = null;
				if (secondDisplayColumnName != null && !secondDisplayColumnName.equals("")) {
					secondColumnString = rs.getString(this.secondDisplayColumnName).trim();
					if (secondColumnString.equals("")) {
						secondColumnString = null;
					}
				}

				// build eventList item
				if (secondColumnString != null) {
					listItem = new SSListItem(primaryKey, firstColumnString + separator + secondColumnString);
				} else {
					listItem = new SSListItem(primaryKey, firstColumnString);
				}

				// add to lists
				eventList.add(listItem);
				mappings.add(listItem.getPrimaryKey());
				options.add(listItem.getListItem());

				// this.data.add(new SelectorElement(primaryValue, displayValue));
				// this.listItemMap.put(primaryKey, listItem);

				// INCREMENT
				// i++;
			}
			rs.close();

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (java.lang.NullPointerException npe) {
			npe.printStackTrace();
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	/**
	 * Adds listeners for Component, RowSet, Keyboard, and PopupMenu
	 */
	public void removeListeners() {
		SSComponentInterface.super.removeListeners();
		// removeKeyListener(this.myKeyListener);
		// removePopupMenuListener(this.myPopupMenuListener);
	}

//    /**
//     * Sets the SSRowSet and column name to which the component is to be bound.
//     *
//     * @param _sSRowSet    datasource to be used.
//     * @param _columnName    Name of the column to which this check box should be bound
//     */
//    public void bind(SSRowSet _sSRowSet, String _columnName) {
//        SSRowSet oldValue = this.sSRowSet;
//        this.sSRowSet = _sSRowSet;
//        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);
//
//        String oldValue2 = this.columnName;
//        this.columnName = _columnName;
//        firePropertyChange("columnName", oldValue2, this.columnName);
//
//        bind();
//    }

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
	public void setDateFormat(String _dateFormat) {
		String oldValue = this.dateFormat;
		this.dateFormat = _dateFormat;
		firePropertyChange("dateFormat", oldValue, this.dateFormat);
	}

	/**
	 * Sets the column name whose values have to be displayed in combo box.
	 *
	 * @param _displayColumnName column name whose values have to be displayed.
	 */
	public void setDisplayColumnName(String _displayColumnName) {
		String oldValue = this.displayColumnName;
		this.displayColumnName = _displayColumnName;
		firePropertyChange("displayColumnName", oldValue, this.displayColumnName);
	}

	/**
	 * Method that sets the combo box to be filterable.
	 * 
	 * GlazedList filtering is now fully integrated so this no longer serves a
	 * purpose.
	 * 
	 * @param _filter boolean to turn filtering on or off
	 */
	@Deprecated
	public void setFilterable(boolean _filter) {
		// TODO remove this method in future release
		this.filterSwitch = _filter;
		System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setFilterable() - this method has been Deprecated because GlazedList filtering is now fully integrated.");
		Thread.dumpStack();
	}

	/**
	 * Sets the database table primary column name.
	 * 
	 * @param _primaryKeyColumnName name of primary key column
	 */
	public void setPrimaryKeyColumnName(String _primaryKeyColumnName) {
		String oldValue = this.primaryKeyColumnName;
		this.primaryKeyColumnName = _primaryKeyColumnName;
		firePropertyChange("primaryKeyColumnName", oldValue, this.primaryKeyColumnName);
	}

	/**
	 * Sets the query used to display items in the combo box.
	 *
	 * @param _query query to be used to get values from database (to display combo
	 *               box items)
	 */
	public void setQuery(String _query) {
		String oldValue = this.query;
		this.query = _query;
		firePropertyChange("query", oldValue, this.query);
	}

//    /**
//     * Initialization code.
//     */
//    protected void init() {
//       // // TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER KEY IS PRESSED
//        //Set<AWTKeyStroke> forwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
//        //Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
//        //newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
//        //newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK ));
//        //setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,newForwardKeys);
//        
//        // SET PREFERRED DIMENSIONS
//            setPreferredSize(new Dimension(200,20));
//    }

//    /**
//     * Method for handling binding of component to a SSRowSet column.
//     */
//    protected void bind() {
//
//        // CHECK FOR NULL COLUMN/ROWSET
//            if (this.columnName==null || this.columnName.trim().equals("") || this.sSRowSet==null) {
//                return;
//            }
//
//        // REMOVE LISTENERS TO PREVENT DUPLICATION
//            removeListeners();
//            try {
//	            
//	        // BIND THE TEXT FIELD TO THE SPECIFIED COLUMN
//	            this.textField.setDocument(new SSTextDocument(this.sSRowSet, this.columnName));
//	
//	        // SET THE COMBO BOX ITEM DISPLAYED
//	            updateDisplay();
//	            
//            }finally { 
//            	// ADD BACK LISTENERS
//            	addListeners();
//            }
//    }

//    /**
//     * Updates the value displayed in the component based on the SSRowSet column
//     * binding.
//     */
//	protected void updateDisplay() {
//
//    	// THIS WILL MAKE SURE COMBO BOX ACTION LISTENER DOESN'T DO ANY THING EVEN IF IT GETS CALLED
//    	this.inUpdateDisplay = true;
//    	try {
//	        // GET THE VALUE FROM TEXT FIELD
//	        String text = this.textField.getText().trim();
//	
//	        if (!text.equals("") && this.itemMap != null && this.itemMap.get(text) != null ) {
//	            //long valueInText = Long.parseLong(text);
//	            // GET THE INDEX WHERE THIS VALUE IS IN THE VECTOR.
//	        	//long longIndex = this.itemMap.get(text);
//	        	Long index = this.itemMap.get(text);
//	            //int index = (int) longIndex;
//	            if (index != getSelectedIndex()) {
//	                setSelectedIndex(index.intValue());
//	                updateUI();
//	            }
//	        }
//	        else {
//	            setSelectedIndex(-1);
//	            updateUI();
//	        }
//    	}finally {
//    		this.inUpdateDisplay = false;
//    	}
//    }

	/**
	 * Sets the second display name. If more than one column have to displayed then
	 * use this. For the parts example given above. If you have a part description
	 * in part table. Then you can display both part name and part description.
	 *
	 * @param _secondDisplayColumnName column name whose values have to be displayed
	 *                                 in the combo in addition to the first column
	 *                                 name.
	 */
	public void setSecondDisplayColumnName(String _secondDisplayColumnName) {
		String oldValue = this.secondDisplayColumnName;
		this.secondDisplayColumnName = _secondDisplayColumnName;
		firePropertyChange("secondDisplayColumnName", oldValue, this.secondDisplayColumnName);
	}

	/**
	 * Sets the currently selected value
	 *
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _value value to set as currently selected.
	 */
	public void setSelectedStringValue(String _value) {

		// ONLY NEED TO PROCEED IF THERE IS A CHANGE
		// TODO consider firing a property change
		if (_value != getSelectedItem()) {

			// IF OPTIONS ARE NON-NULL THEN LOCATE THE SEQUENTIAL INDEX AT WHICH THE
			// SPECIFIED TEXT IS STORED
			if (this.options != null) {
				int index = options.indexOf(_value);

				if (index == -1) {
					System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedStringValue() - warning: could not find a corresponding item in combobox for display text of "
							+ _value + ". Setting index to -1 (blank).");
				}

				setSelectedIndex(index);
				//updateUI();
			}

		}

//    	mappings.get(getSelectedIndex())
//    	
//        this.textField.setText(_value);
	}
	
	/**
	 * Sets the currently selected value
	 *
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _value value to set as currently selected.
	 */
	/*
	@Override
	public void setSelectedItem(Object _value) {
// INTERCEPTING GLAZEDLISTS CALLS TO setSelectedItem() SO THAT WE CAN PREVENT IT FROM TRYING TO SET VALUES NOT IN THE LIST
		
		SSListItem selectedItem = (SSListItem)_value;
		
// TODO will have to modify if we want to set to null in some cases. May also want to veto
		//if (selectedItem.getPrimaryKey()==null) {
		if (selectedItem==null) {
			// capture what user actually typed
			String typedText = "";
			if (getEditor().getItem()!=null) {
				typedText = getEditor().getItem().toString();
				
				// warning
				System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedItem() - user has entered an item not in the list: " + typedText + ". No action taken.");
			}
			
			// reset typed text to remove a character
			if (!typedText.equals("")) {
				typedText = typedText.substring(0, typedText.length()-1);
				System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedItem() - modified text: " + typedText);
				getEditor().setItem(typedText);
				updateUI(); // this refreshes the typed text. Confirmed it does not update without call to updateUI();
			}
			
			
			
			//setSelectedIndex(-1); //causes a cycle setSelectedIndex->setSelectedItem
		} else {
			super.setSelectedItem(_value);
		}
		
		

	}	
	*/

	
// TODO TEST NAV TOOLBAR REFRESH	
	/**
	 * Sets the currently selected value. This is called when the user clicks on an
	 * item or when they type in the combo's textfield.
	 *
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _value value to set as currently selected.
	 */
	@Override
	public void setSelectedItem(Object _value) {
// INTERCEPTING GLAZEDLISTS CALLS TO setSelectedItem() SO THAT WE CAN PREVENT IT FROM TRYING TO SET VALUES NOT IN THE LIST

// NOTE THAT CALLING setSelectedIndex(-1) IN THIS METHOD CAUSES A CYCLE HERE BECAUSE setSelectedIndex() CALLS setSelectedItem()

		// WE COULD BE HERE DUE TO:
		// 1. MOUSE CLICK ON AN ITEM
		// 2. KEYBASED NAVIGATION
		// 3. USER TYPING SEQUENTIALLY:
		// THIS MAY TRIGGER MATCHING ITEMS, OR MAY NOT MATCH ANY SUBSTRINGS SO WE DELETE
		// THE LAST CHARACTER
		// 4. USER DOING SOMETHING UNEXPECTED LIKE INSERTING CHARACTERS, DELETING ALL
		// TEXT, ETC.
		// THIS MAY TRIGGER MATCHING ITEMS, OR MAY NOT MATCH ANY SUBSTRINGS SO WE REVERT
		// TO THE LAST STRING AVAILABLE
		// IF NOT MATCH, COULD ALSO REVERT TO EMPTY STRING

		// GET LATEST TEXT TYPED BY USER
		String latestTypedText = "";
		if (getEditor().getItem() != null) {
			latestTypedText = getEditor().getItem().toString();
		}

		SSListItem selectedItem = (SSListItem) _value;

		// FOUR OUTCOMES:
		// 1. _value is null, but selectedItem is not null, indicating a match (so null
		// is a valid choice)
		// 2. _value is null and selectedItem is null, indicating no match
		// 3. neither _value nor selectedItem are null, indicating a match
		// 4. _value is not null, but selectedItem is null, indicating no match (have to
		// revert text)

		if (selectedItem != null) {
			// OUTCOME 1 & 3 ABOVE, MAKE CALL TO SUPER AND MOVE ALONG
			// Display contents of selectedItem for debugging
			System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedItem() - PK="
					+ selectedItem.getPrimaryKey() + ", Item=" + selectedItem.getListItem());

			// We have to be VERY careful with calls to setSelectedItem() because it will
			// set the value based on the index of any SUBSET list returned by GlazedList,
			// not the full list
			// Calling setPopupVisilbe(false) clears the subset list so that the subsequent
			// call to setSelectedItem works as intended.
			this.setPopupVisible(false);

			// Call to parent method.
			// Don't call setSelectedIndex() as this causes a cycle
			// setSelectedIndex()->setSelectedItem().
			System.out.println(getBoundColumnName() + " - "
					+ "SSDBComboBox.setSelectedItem() - calling super.setSelectedItem(" + selectedItem + ")");
			super.setSelectedItem(selectedItem);

			// call to super() is going to change the value of the latest typed text so
			// update it
			latestTypedText = getEditor().getItem().toString();

			System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedItem() - Prior text was '"
					+ priorTypedText + "'. Current text is '" + latestTypedText + "'.");

			// update priorTypedText
			priorTypedText = latestTypedText;

// TODO if _value==null then priorTypedText should probably be reset to ""??

		} else if (_value == null) {
			// OUTCOME 2 ABOVE
			// setSelectedItem() was called with null, but there is no match (so null is not a valid selection in the list)
			// There may be partial matches from GlazedList.
			System.out.println(
					getBoundColumnName() + " - " + "SSDBComboBox.setSelectedItem() called with null. Prior text was '"
							+ priorTypedText + "'. Current text is '" + latestTypedText + "'.");
			
// TODO HERE WE WILL CHECK TO SEE IF THERE ARE MATCHING ITEMS BASED ON getItemCount(). If yes, select first. If no, revert string.
			
			// Determine if there are partial matches on the popup list due to user typing.
			int possibleMatches = getItemCount();
			System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedItem() - possible matches: "
					+ possibleMatches);
			if (possibleMatches > 0) {
				// update the latestTypedText, but don't make a call to super.setSelectedItem(). No change to bound value.
				priorTypedText = latestTypedText;
			} else {
				System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedItem() reverting to prior typed text.");
				 getEditor().setItem(priorTypedText);
				 updateUI(); // This refreshes the characters displayed. Display does not update without call to updateUI();
			}

// TODO Likely need a focusListener to reset the text to the selectedItem when focus is lost.
// Based on the logic above, an item should be selected at all times so we can just call getEditor().setItem() with selectedItem.toString() 

		} else {
			// OUTCOME 4 ABOVE
			// revert to prior string and don't select anything
			System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedItem() called with " + _value
					+ ", but there is no match. Prior text was '" + priorTypedText + "'. Current text is '" + latestTypedText + "'.");
			// TODO Throw an exception here? May be the result of a coding error.
			//getEditor().setItem(priorTypedText);
			//updateUI(); // This refreshes the characters displayed.
		}

	}

//    /**
//     * @author mvo
//     * Listener for the combobox's popup menu which resets the combobox's list to the original data if it is invisible.
//     */
//    protected class MyPopupMenuListener implements PopupMenuListener{	
//    	ActionListener[] saveActionListeners = new ActionListener[getActionListeners().length];
//    	boolean saveSwitch = true;
//    	
//		public void addAllActionListeners(){	
//    		for (ActionListener al: this.saveActionListeners) addActionListener(al);
//			fireActionEvent();
//		}
//		
//		public void removeAllActionListeners(){	
//			if (getActionListeners().length != 0){	
//				for (ActionListener al : getActionListeners()){
//					removeActionListener(al);
//				}
//			}
//		}
//    	
//		@Override
//		public void popupMenuCanceled(PopupMenuEvent e) {
//    		if (isEditable()){
//    			hidePopup();
//    		}
//    	}
//	
//		@Override
//		public void popupMenuWillBecomeInvisible(PopupMenuEvent e){
//			
//			//if the popup was open before filtering, return as to not add action listeners
//			if(SSDBComboBox.this.myKeyListener.openPopupFilter){	
//				SSDBComboBox.this.myKeyListener.openPopupFilter = false;
//				return;
//			}
//			//when menu closes, change out textfield to re-insert original items before filtering.
//			if (SSDBComboBox.this.selectorCBM != null) SSDBComboBox.this.selectorCBM.setFilterEdit(new JTextField());
//			//set editable to false if value is clicked while filtering and set text field to selected item.
//			if (isEditable()){	
//				setEditable(false);	
//				SSDBComboBox.this.textField.setText(""+getSelectedFilteredValue());
//			}
//			addAllActionListeners();	
//			requestFocus();
//		}
//
//		@Override
//		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//			if (this.saveSwitch){	
//				this.saveActionListeners = getActionListeners();
//    			this.saveSwitch = false;
//    			// moving this line out of if block
//    			//removeAllActionListeners();
//			}
//			removeAllActionListeners();
//		}
//    }

//    /**
//     * Listener(s) for the bound text field used to propigate values back to the
//     * component's value.
//     */
//    protected class MyTextFieldDocumentListener implements DocumentListener {
//    	@Override
//		public void changedUpdate(DocumentEvent de) {
//    		// DON'T MOVE THE REMOVE ADD LISTENER CALLS TO UPDATEDISPLAY
//    		// AS UPDATE DISPLAY IS CALLED IN OTHER PLACES WHICH REMOVE AND ADD LISTENERS
//    		// THIS WOULD MAKE THE ADDLISTENER CALL TWICE SO THERE WILL BE TWO LISTENERS
//    		// AND A CALL TO REMOVE LISTENER IS NOT GOOD ENOUGH
//    		removeListeners();
//       		updateDisplay();
//       		addListeners();
//        }
//    	
//        @Override
//		public void insertUpdate(DocumentEvent de) {
//        	removeListeners();
//       		updateDisplay();
//       		addListeners();
//        }
//        
//        @Override
//		public void removeUpdate(DocumentEvent de) {
//        	removeListeners();
//       		updateDisplay();
//       		addListeners();
//        }
//
//    } // end protected class MyTextFieldDocumentListener implements DocumentListener {

//    /**
//     * @author mvo
//     * Listener for JTextField "filterText" which allows for navigation of filtered items in combo box.
//     */
//    protected class FilterKeyListener extends KeyAdapter {	
//    	String savePrimary;
//    	
//    	@Override
//		public void keyPressed(KeyEvent ke){	
//    		
//    		if(ke.getKeyCode() == KeyEvent.VK_ESCAPE){	
//    			setEditable(false);
//    			hidePopup();
//				setSelectedIndex(SSDBComboBox.this.myKeyListener.saveIndex);
//				SSDBComboBox.this.textField.setText(""+getSelectedFilteredValue());
//    			return;
//    		}
//    	}
//    	@Override
//		public void keyReleased(KeyEvent ke){	
//    		//tab will traverse to next component
//    		if (ke.getKeyCode() == KeyEvent.VK_TAB){	
//    			setEditable(false);
//				hidePopup();
//				transferFocus();
//    		}    		
//    		//turn off filter if arrows are pressed and keep focus on combo box
//    		else if (ke.getKeyCode() == KeyEvent.VK_DOWN || ke.getKeyCode() == KeyEvent.VK_UP){	
//    			setEditable(false);
//    			showPopup();
//    			requestFocus();
//			}
//    		else if (0 == getItemCount()){
//    			repaint();
//    			showPopup();
//    		}
//    		else {
//	    		this.savePrimary = getSelectedFilteredValue();
//	    		
//	    		//if the combo box has less items than the popup's row length, refresh the popup box.
//	    		if(getItemCount() < 9 || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE)
//	    		{	SSDBComboBox.this.myKeyListener.openPopupFilter = true;
//	    			hidePopup();
//	    		}
//	    		showPopup();
//    		}
//    	}
//    }

//    /**
//     * Gets the selected value of the selected item in the filtered list.
//     * @return a String corresponding to the currently selected value in the SSDBCombobBox
//     */
//    public String getSelectedFilteredValue() {	
//    	if(getSelectedIndex() < 0) setSelectedIndex(0);
//    	//gets the primary value of the selected index of the filtered list.
//    	Object selectedValue = this.selectorCBM.getSelectedBoundData(getSelectedIndex());
//    	if(selectedValue == null)
//    		return null;
//    	return selectedValue.toString();
//    }

//    /**
//     * Listener for focus in filter text field.
//     */
//    protected class FilterFocusListener extends FocusAdapter{	
//		@Override
//		public void focusGained(FocusEvent fe){	
//			showPopup();
//		}
//	}

//    /**
//     * Listener for keystroke-based, string matching, combo box navigation.
//     */
//    protected class MyKeyListener extends KeyAdapter {
//    	private JTextField filterText;
//    	private FilterKeyListener filterKeyListener = new FilterKeyListener();
//    	boolean openPopupFilter = false;
//    	int saveIndex;
//    	@Override
//		public void keyPressed(KeyEvent ke){	
//    		this.saveIndex = getSelectedIndex();
//    		if (SSDBComboBox.this.myPopupMenuListener.saveSwitch) {	
//    			SSDBComboBox.this.myPopupMenuListener.saveActionListeners = getActionListeners();
//    			SSDBComboBox.this.myPopupMenuListener.saveSwitch = false;
//    		}
//    		SSDBComboBox.this.myPopupMenuListener.removeAllActionListeners();
//    	}
//    	@Override
//		public void keyReleased(KeyEvent ke){	
//    		//reset the list and set the text field to the selected item's primary key 
//    		if (ke.getKeyCode() == KeyEvent.VK_ENTER){
//    			
//    			//if enter is pressed inside of the filter textfield and no item is selected
//    			//pick the last item selected item and set it as the current selected item
//    			if (-1 == getSelectedIndex()){
//    				setSelectedItem(null);
//    				SSDBComboBox.this.textField.setText(this.filterKeyListener.savePrimary);
//    			}
//    			return;
//    		}
//    		
//    		if (ke.getKeyCode() == KeyEvent.VK_DOWN ||  ke.getKeyCode() == KeyEvent.VK_UP || !SSDBComboBox.this.filterSwitch) return;
//    	
//    		//take the first key pressed, set combo box to editable, turn on filter, and set the text field to that saved key
//    		if (ke.getKeyCode() >= KeyEvent.VK_A & ke.getKeyCode() <= KeyEvent.VK_BACK_SLASH 					||
//    			ke.getKeyCode() >= KeyEvent.VK_COMMA & ke.getKeyCode() <= KeyEvent.VK_9 	  					||
//    			ke.getKeyCode() >= KeyEvent.VK_OPEN_BRACKET & ke.getKeyCode() <= KeyEvent.VK_CLOSE_BRACKET		||
//    			ke.getKeyCode() == KeyEvent.VK_PLUS																||
//    			ke.getKeyCode() == KeyEvent.VK_QUOTE) {	
//    			// if the popup is open, close it and do not add listeners
//        		if (isPopupVisible()){
//        			this.openPopupFilter = true;
//        			hidePopup();
//        		}
//    			setEditable(true);
//    			this.filterText = (JTextField) getEditor().getEditorComponent();
//    			SSDBComboBox.this.selectorCBM.setFilterEdit(this.filterText);
//    			this.filterText.setText(""+ke.getKeyChar());
//    			this.filterText.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
//    			
//    			// SINCE WE HAVE TO ADD THE LISTENER IN COMBO KEY LISTENER
//    			// MAKE SURE WE ARE NOT ADDING IT MULTIPLE TIMES.
//    			// EASY WAY TO DO IT IS TO REMOVE IT AND THEN ADD IT.
//       			this.filterText.removeKeyListener(this.filterKeyListener);
//       			this.filterText.removeFocusListener(SSDBComboBox.this.filterFocusListener);
//       			this.filterText.addKeyListener(this.filterKeyListener);
//       			this.filterText.addFocusListener(SSDBComboBox.this.filterFocusListener);
//
//        	}
//    	}
//    }

	/**
	 * Sets the value stored in the component.
	 * 
	 * If called from updateSSComponent() from a RowSet change then the Component
	 * listener should already be turned off. Otherwise we want it on so the
	 * ultimate call to setSelectedIndex() will trigger an update the to RowSet.
	 * 
	 * The mappings ArrayList will be null until execute is called so
	 *
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _value database record primary key value to assign to combobox
	 */
	public void setSelectedValue(long _value) {

		// ONLY NEED TO PROCEED IF THERE IS A CHANGE
		// TODO consider firing a property change
		// TODO what happens if user tries to pass null or if getSelectedValue() is null?
		if (_value != getSelectedValue()) {

			// IF MAPPINGS ARE SPECIFIED THEN LOCATE THE SEQUENTIAL INDEX AT WHICH THE
			// SPECIFIED CODE IS STORED
			if (this.mappings != null) {
				int index = mappings.indexOf(_value);

				if (index == -1) {
					System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedValue() - warning: could not find a corresponding item in combobox for value of " + _value
							+ ". Setting index to -1 (blank).");
				}

				//System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedValue() - eventList: " + eventList.toString());
				//System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedValue() - options: " + options.toString());
				//System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedValue() - mappings: " + mappings.toString());
				
				setSelectedIndex(index);
				//updateUI();
			} else {
				System.out.println(getBoundColumnName() + " - " + "SSDBComboBox.setSelectedValue() - no mappings available for current component. No value set in setSelectedValue().");
			}

		}

	}
	


//    /**
//     * Listener(s) for the component's value used to propagate changes back to
//     * bound text field.
//     */
//    protected class MyComboListener implements ActionListener {
//    	@Override
//		public void actionPerformed(ActionEvent ae) {
//    		// IF WE ARE UPDATING THE DISPLAY DON'T DO ANY THING.
//    		if(SSDBComboBox.this.inUpdateDisplay) {
//    			return;
//    		}
//    		
//        	//dont fire an action if the size of the filtered model is not the same as the initial item size
//        	if (SSDBComboBox.this.selectorCBM != null){
//        		if (SSDBComboBox.this.selectorCBM.getSize() != SSDBComboBox.this.selectorCBM.data.size()) return;
//        	}
//        	
//        	SSDBComboBox.this.textField.getDocument().removeDocumentListener(SSDBComboBox.this.textFieldDocumentListener);
//
//        	try {
//	            // GET THE INDEX CORRESPONDING TO THE SELECTED TEXT IN COMBO
//	            int index = getSelectedIndex();
//	            // IF THE USER WANTS TO REMOVE COMPLETELY THE VALUE IN THE FIELD HE CHOOSES
//	            // THE EMPTY STRING IN COMBO THEN THE TEXT FIELD IS SET TO EMPTY STRING
//	            if (index != -1) {
//	                try {
//	                    String textFieldText = SSDBComboBox.this.textField.getText();
//	                    String textPK= SSDBComboBox.this.selectorCBM.getSelectedBoundData(index).toString();
//	                    if (!textFieldText.equals(textPK)) {
//	                        SSDBComboBox.this.textField.setText(textPK);
//	                    }
//	                    // IF THE LONG VALUE CORRESPONDING TO THE SELECTED TEXT OF COMBO NOT EQUAL
//	                    // TO THAT IN THE TEXT FIELD THEN CHANGE THE TEXT IN THE TEXT FIELD TO THAT VALUE
//	                    // IF ITS THE SAME LEAVE IT AS IS
//	                } catch(NullPointerException npe) {
//	                	npe.printStackTrace();
//	                } catch(NumberFormatException nfe) {
//	                	nfe.printStackTrace();
//	                }
//	            }
//	            else {
//	                SSDBComboBox.this.textField.setText("");
//	            }
//	            
//        	}finally {
//        		SSDBComboBox.this.textField.getDocument().addDocumentListener(SSDBComboBox.this.textFieldDocumentListener);
//        	}
//            
//            // WHEN SET SELECTED INDEX IS CALLED SET SELECTED ITEM WILL BE CALLED ON THE MODEL AND THIS FUNCTION 
//            // IS SUPPOSED TO FIRE A EVENT TO CHANGE THE TEXT BUT IT WILL CAUSE ISSUES IN OUR IMPLEMENTATION
//            // BUT WE WILL GET ACTION EVENT SO REPAIT TO REFLECT THE CHANGE IN COMBO SELECTION
//            repaint();
//        }
//
//    } // protected class MyComboListener implements ActionListener {

	/**
	 * Set the separator to be used when multiple columns are displayed
	 *
	 * @param _separator separator to be used.
	 */
	public void setSeparator(String _separator) {
		String oldValue = this.separator;
		this.separator = _separator;
		firePropertyChange("separator", oldValue, this.separator);
	}

	/**
	 * Set the separator to be used when multiple columns are displayed
	 * 
	 * Deprecated for misspelling.
	 *
	 * @param _separator separator to be used.
	 */
	@Deprecated
	public void setSeperator(String _separator) {
		setSeparator(_separator);
	}

	/**
	 * Sets the SSCommon data member for the current Swingset Component.
	 * 
	 * @param _ssCommon shared/common SwingSet component data and methods
	 */
	@Override
	public void setSSCommon(SSCommon _ssCommon) {
		ssCommon = _ssCommon;

	}

	/**
	 * Updates an item available in the combobox and associated lists.
	 * 
	 * If more than one item is present in the combo for that value, only the first
	 * one is changed.
	 *
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
	public boolean updateItem(long _primaryKey, String _updatedDisplayText) {

		boolean result = false;

		if (eventList != null) {

			// LOCK EVENT LIST
			eventList.getReadWriteLock().writeLock().lock();

			try {

				// GET INDEX FOR mappings and options
				int index = mappings.indexOf(_primaryKey);

				// PROCEED IF INDEX WAS FOUND
				if (index != -1) {
					options.set(index, _updatedDisplayText);
					// mappings.remove(index);
// TODO Confirm that eventList is not reordered by GlazedLists code.					
					eventList.get(index).setListItem(_updatedDisplayText);
					result = true;

				}

// TODO may need to call repaint()				

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				eventList.getReadWriteLock().writeLock().unlock();
			}

		}

		return result;
	}

	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText()
	 * 
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed
	 */
	@Override
	public void updateSSComponent() {
		try {
			String text = getBoundColumnText().trim();

			// GET THE BOUND VALUE STORED IN THE ROWSET
			//if (text != null && !(text.equals(""))) {
			if (text != null && !text.isEmpty()) {
				long primaryKey = Long.parseLong(text);

				setSelectedValue(primaryKey);

			} else {
				setSelectedIndex(-1);
				//updateUI();
			}
			
			//
			
			//updateUI();

		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}

	/**
	 * Updates the string thats being displayed.
	 * 
	 * If more than one item is present in the combo for that value the first one is
	 * changed.
	 *
	 * @param _existingDisplayText existing display text to be updated
	 * @param _updatedDisplayText  text that should be updated in the combobox
	 *
	 * @return returns true if successful otherwise returns false.
	 */
	public boolean updateStringItem(String _existingDisplayText, String _updatedDisplayText) {

		boolean result = false;

		if (options != null) {
			int index = options.indexOf(_existingDisplayText);
			result = updateItem(mappings.get(index), _updatedDisplayText);
		}

		return result;
	}

} // end public class SSDBComboBox