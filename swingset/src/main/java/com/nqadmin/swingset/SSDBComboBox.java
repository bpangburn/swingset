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
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;


import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSRowSet;
import com.nqadmin.swingset.formatting.helpers.SelectorComboBoxModel;
import com.nqadmin.swingset.formatting.helpers.SelectorElement;

/**
 * SSDBComboBox.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Similar to the SSComboBox, but used when both the 'bound' values and the
 * 'display' values are pulled from a database table.  Generally the bound
 * value represents a foreign key to another table, and the combobox needs to
 * display a list of one (or more) columns from the other table.
 *
 * Note, if changing both a sSRowSet and column name consider using the bind()
 * method rather than individual setSSRowSet() and setColumName() calls.
 *
 * e.g.
 *
 *      Consider two tables:
 *        1. part_data (part_id, part_name, ...)
 *        2. shipment_data (shipment_id, part_id, quantity, ...)
 *
 *      Assume you would like to develop a screen for the shipment table and you
 *      want to have a screen with a combobox where the user can choose a
 *      part and a textbox where the user can specify a  quantity.
 *
 *      In the combobox you would want to display the part name rather than
 *      part_id so that it is easier for the user to choose. At the same time you
 *      want to store the id of the part chosen by the user in the shipment
 *      table.
 *
 *      SSConnection connection = null;
 *      SSJdbcRowSetImpl sSRowSet = null;
 *      SSDataNavigator navigator = null;
 *      SSDBComboBox combo = null;
 *
 *      try {
 *
 *      // CREATE A DATABASE CONNECTION OBJECT
 *           SSConnection connection = new SSConnection(........);
 *
 *      // CREATE AN INSTANCE OF SSJDBCROWSETIMPL
 *           SSJdbcRowsetImpl sSRowSet = new SSJdbcRowsetImpl(connection);
 *           sSRowSet.setCommand("SELECT * FROM shipment_data;");
 *
 *      // DATA NAVIGATOR CALLS THE EXECUTE AND NEXT FUNCTIONS ON THE SSROWSET.
 *      // IF YOU ARE NOT USING THE DATA NAVIGATOR YOU HAVE TO INCLUDE THOSE.
 *      //   sSRowSet.execute();
 *      //   sSRowSet.next();
 *           SSDataNavigator navigator = new SSDataNavigator(sSRowSet);
 *
 *      // QUERY FOR THE COMBOBOX.
 *           String query = "SELECT * FROM part_data;";
 *
 *      // CREATE AN INSTANCE OF THE SSDBCOMBOBOX WITH THE CONNECTION OBJECT
 *      // QUERY AND COLUMN NAMES
 *           combo = new SSDBComboBox(connection,query,"part_id","part_name");
 *
 *      // THIS BASICALLY SPECIFIES THE COLUMN AND THE SSROWSET WHERE UPDATES HAVE
 *      // TO BE MADE.
 *           combo.bind(sSRowSet,"part_id");
 *           combo.execute();
 *
 *      // CREATE A TEXTFIELD
 *           JTextField myText = new JTextField();
 *           myText.setDocument(new SSTextDocument(sSRowSet, "quantity");
 *
 *      } catch(Exception e) {
 *      // EXCEPTION HANDLER HERE...
 *      }
 *
 *
 *      // ADD THE SSDBCOMBOBOX TO THE JFRAME
 *           getContentPane().add(combo.getComboBox());
 *
 *      // ADD THE JTEXTFIELD TO THE JFRAME
 *           getContentPane().add(myText);
 */

public class SSDBComboBox extends JComboBox<Object> {
	
	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -4203338788107410027L;

	private int initialItemCount;
	
	/**	
	 * Model to be used for holding and filtering data in combo box.
	 */
	 protected SelectorComboBoxModel selectorCBM = new SelectorComboBoxModel();
	  
    /**
     * Text field bound to the SSRowSet.
     * Bee changed to public
     */
    public JTextField textField = new JTextField();

    /**
     * Database connection used to execute queries for combo population.
     */
    protected SSConnection sSConnection = null;

    /**
     * Query used to populate combo box.
     */
    protected String query = "";

    /**
     * The column name whose value is written back to the database when the user
     * chooses an item in the combo box.  This is generally the PK of the table
     * to which a foreign key is mapped.
     */
    protected String primaryKeyColumnName = "";

    /**
     * The database column used to populate the first visible column of the
     * combo box.
     */
    protected String displayColumnName = "";

    /**
     * The database column used to populate the second (optional) visible column
     * of the combo box.
     */
    protected String secondDisplayColumnName = "";

    /**
     * Number of items in the combo box.
     */
    protected int numberOfItems = 0;

    /**
     * SSRowSet from which component will get/set values.
     */
    protected SSRowSet sSRowSet;

    /**
     * SSRowSet column to which the component will be bound.
     */
    protected String columnName = "";

    /**
     * Component listener.
     */
    private final MyComboListener cmbListener = new MyComboListener();

    /**
     * Bound text field document listener.
     */
    protected final MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();

    /**
     * Keystroke-based item selection listener.
     */
    protected final MyKeyListener myKeyListener = new MyKeyListener();
    
    /**
     * Listener for PopupMenu
     */
    protected MyPopupMenuListener myPopupMenuListener = new MyPopupMenuListener();
    
    protected FilterFocusListener filterFocusListener = new FilterFocusListener();

    /**
     * Alphanumeric separator used to separate values in multi-column combo boxes.
     */
    protected String seperator = " - ";
    
    /**
     * boolean value for activating or disabling the filter
     */
   protected boolean filterSwitch = true;
   

    /**
     * Format for any date columns displayed in combo box.
     */
    protected String dateFormat = "MM/dd/yyyy";
    
    protected volatile boolean inUpdateDisplay = false;

    /**
     * Creates an object of the SSDBComboBox.
     */
    public SSDBComboBox() {
        init();
    }

    /**
     * Constructs a SSDBComboBox  with the given parameters.
     *
     * @param _sSConnection database connection to be used.
     * @param _query query to be used to retrieve the values from the database.
     * @param _primaryKeyColumnName column name whose value has to be stored.
     * @param _displayColumnName column name whose values are displayed in the combo box.
     */
    public SSDBComboBox(SSConnection _sSConnection, String _query, String _primaryKeyColumnName, String _displayColumnName) {
        this.sSConnection = _sSConnection;
        this.query = _query;
        this.primaryKeyColumnName = _primaryKeyColumnName;
        this.displayColumnName = _displayColumnName;
        init();
    }
 

    /**
     * Sets the new SSRowSet for the combo box.
     *
     * @param _sSRowSet  SSRowSet to which the combo has to update values.
     */
    public void setSSRowSet(SSRowSet _sSRowSet) {
        SSRowSet oldValue = this.sSRowSet;
        this.sSRowSet = _sSRowSet;
        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);
        bind();
    }

    /**
     * Returns the SSRowSet being used to get the values.
     *
     * @return returns the SSRowSet being used.
     */
    public SSRowSet getSSRowSet() {
        return this.sSRowSet;
    }

    /**
     * Sets the connection object to be used.
     *
     * @param _sSConnection    connection object used for database.
     */
    public void setSSConnection(SSConnection _sSConnection) {
        SSConnection oldValue = this.sSConnection;
        this.sSConnection = _sSConnection;
        firePropertyChange("sSConnection", oldValue, this.sSConnection);
        bind();
    }

    /**
     * Returns connection object used to get values from database.
     *
     * @return returns a SSConnection object.
     */
    public SSConnection getSSConnection() {
        return this.sSConnection;
    }

    /**
     * Sets the query used to display items in the combo box.
     *
     * @param _query   query to be used to get values from database (to display combo box items)
     */
    public void setQuery(String _query) {
        String oldValue = this.query;
        this.query = _query;
        firePropertyChange("query", oldValue, this.query);
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
     * Sets the column name for the combo box
     *
     * @param _columnName   name of column
     */
    public void setColumnName(String _columnName) {
        String oldValue = this.columnName;
        this.columnName = _columnName;
        firePropertyChange("columnName", oldValue, this.columnName);
        bind();
    }

    /**
     * Returns the column name to which the combo is bound.
     *
     * @return returns the column name to which to combo box is bound.
     */
    public String getColumnName() {
        return this.columnName;
    }

    /**
     * Sets the column name whose values have to be displayed in combo box.
     *
     * @param _displayColumnName   column name whose values have to be displayed.
     */
    public void setDisplayColumnName(String _displayColumnName) {
        String oldValue = this.displayColumnName;
        this.displayColumnName = _displayColumnName;
        firePropertyChange("displayColumnName", oldValue, this.displayColumnName);
    }

    /**
     * Returns the column name whose values are displayed in the combo box.
     *
     * @return returns the name of the column used to get values for combo box items.
     */
    public String getDisplayColumnName() {
        return this.displayColumnName;
    }

    /**
     * Sets the database table primary column name.
     * 
     * @param _primaryKeyColumnName name of primary key column
     */
    public void setPrimaryKeyColumnName(String _primaryKeyColumnName){
        String oldValue = this.primaryKeyColumnName;
        this.primaryKeyColumnName = _primaryKeyColumnName;
        firePropertyChange("primaryKeyColumnName", oldValue, this.primaryKeyColumnName);
    }
    /**
     * When a display column is of type date you can choose the format in which it has
     * to be displayed. For the pattern refer SimpleDateFormat in java.text package.
     *
     * @param _dateFormat pattern in which dates have to be displayed
     */
     public void setDateFormat(String _dateFormat) {
        String oldValue = this.dateFormat;
        this.dateFormat = _dateFormat;
        firePropertyChange("dateFormat", oldValue, this.dateFormat);
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
     * Sets the second display name.
     * If more than one column have to displayed then use this.
     * For the parts example given above. If you have a part description in part table.
     * Then you can display both part name and part description.
     *
     * @param _secondDisplayColumnName    column name whose values have to be
     *  displayed in the combo in addition to the first column name.
     */
    public void setSecondDisplayColumnName(String _secondDisplayColumnName) {
        String oldValue = this.secondDisplayColumnName;
        this.secondDisplayColumnName = _secondDisplayColumnName;
        firePropertyChange("secondDisplayColumnName", oldValue, this.secondDisplayColumnName);
    }

    /**
     * Returns the second column name whose values are also displayed in the combo box.
     *
     * @return returns the name of the column used to get values for combo box items.
     *  returns NULL if the second display column is not provided.
     */
    public String getSecondDisplayColumnName() {
        return this.secondDisplayColumnName;
    }

    /**
     * Set the seperator to be used when multiple columns are displayed
     *
     * @param _seperator   seperator to be used.
     */
     public void setSeperator(String _seperator) {
        String oldValue = this.seperator;
        this.seperator = _seperator;
        firePropertyChange("seperator", oldValue, this.seperator);
     }

     /**
      * Returns the seperator used when multiple columns are displayed
      *
      * @return seperator used.
      */
     public String getSeperator() {
        return this.seperator;
     }

    /**
     * Returns the number of items present in the combo box.
     *
     * This is a read-only bean property.
     *
     * @return returns the number of items present in the combo box.
     */
    public int getNumberOfItems() {
        return this.numberOfItems;
    }
    
    /**
     * Provides the initial number of items in the list underlying the CombobBox
     * 
     * @return the initial number of items in the ComboBox list
     */
    public int getInitialNumberOfItems() {
    	return this.initialItemCount;
    }

    /**
     * Sets the currently selected value
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _value    value to set as currently selected.
     */
    public void setSelectedValue(long _value) {
        this.textField.setText(String.valueOf(_value));
    }

    /**
     * Returns the value of the selected item.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @return value corresponding to the selected item in the combo.
     *     return -1 if no item is selected.
     */
    public long getSelectedValue() {
        int index = getSelectedIndex();

        if (index == -1) {
            return -1;
        }
		long returnVal =  Long.valueOf(this.selectorCBM.getSelectedBoundData(index).toString());
        return returnVal;
        
    }

    /**
     * Sets the currently selected value
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @param _value    value to set as currently selected.
     */
    public void setSelectedStringValue(String _value) {
        this.textField.setText(_value);
    }

    /**
     * Returns the value of the selected item.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @return value corresponding to the selected item in the combo.
     *     return null if no item is selected.
     */
    public String getSelectedStringValue() {

        int index = getSelectedIndex();

        if (index == -1) {
            return null;
        }
 
        return ""+this.selectorCBM.getSelectedBoundData(index);
    } 
    
    /**
     * Map of string/value pairings for the ComboBox.
     */
    public Map<String, Long> itemMap;
    
    /**
     * Executes the query and adds items to the combo box based on the values
     * retrieved from the database.
     * @throws SQLException 	SQLException
     * @throws Exception 	Exception
     */
    public void execute() throws SQLException, Exception {
        // TURN OFF LISTENERS
            removeListeners();

            if (this.query.equals("")) {
                throw new Exception("Query is empty");
            }
        	this.selectorCBM.setQuery(this.query);
        	this.selectorCBM.setDateFormat(this.dateFormat);
        	this.selectorCBM.setPrimaryKeyColumn(this.primaryKeyColumnName);
    		this.selectorCBM.setDisplayColumn(this.displayColumnName);
    		this.selectorCBM.setSecondDisplayColumn(this.secondDisplayColumnName);
    		this.selectorCBM.setSeparator(this.seperator);
    		this.selectorCBM.setSSConnection(this.sSConnection);
    		this.selectorCBM.refresh();
    		setModel(this.selectorCBM);
    		this.itemMap = this.selectorCBM.itemMap;
    		this.numberOfItems = this.selectorCBM.getSize();

    		// UPDATE DISPLAY WILL ADD THE LISTENERS AT THE END SO NO NEED TO ADD IT AGAIN.
            updateDisplay();
            
            //ADD THE LISTENERS BACK
            addListeners();

    }

    /**
     * Sets the SSRowSet and column name to which the component is to be bound.
     *
     * @param _sSRowSet    datasource to be used.
     * @param _columnName    Name of the column to which this check box should be bound
     */
    public void bind(SSRowSet _sSRowSet, String _columnName) {
        SSRowSet oldValue = this.sSRowSet;
        this.sSRowSet = _sSRowSet;
        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);

        String oldValue2 = this.columnName;
        this.columnName = _columnName;
        firePropertyChange("columnName", oldValue2, this.columnName);

        bind();
    }

    /**
     * Adds an item to the existing list of items in the combo box.
     *
     * @param _name   name that should be displayed in the combo
     * @param _value   value corresponding the the name
     */
     public void addItem(String _name, long _value) {
    	 addStringItem(_name, String.valueOf(_value));
     }

    /**
     * Adds an item to the existing list of items in the combo box.
     *
     * @param _name   name that should be displayed in the combo
     * @param _value   value corresponding the the name
     */
     public void addStringItem(String _name, String _value) {
    	 // WHEN EVER CHANGING THE ITEMS IN THE FILTER LIST USE LOCKS TO AVOID CONCURRENT MODIFICATION EXCEPTION
    	 this.selectorCBM.data.getReadWriteLock().writeLock().lock();
    	 try {
    		 this.selectorCBM.data.add(new SelectorElement(_value,_name));
    		 this.itemMap.put(_value, Long.valueOf(this.itemMap.size()));
    		 this.numberOfItems++;
    	 }finally {
    		 this.selectorCBM.data.getReadWriteLock().writeLock().unlock();
    	 }
     }

     /**
      * Deletes the item which has value equal to _value.
      * If more than one item is present in the combo for that value the first one is changed.
      *
      * @param _value  value of the item to be deleted.
      *
      * @return returns true on successful deletion else returns false.
      */
     public boolean deleteItem(long _value) {
    	 return deleteStringItem(String.valueOf(_value));
     }

     /**
      * Deletes the item which has value equal to _value.
      * If more than one item is present in the combo for that value the first one is changed.
      *
      * @param _value  value of the item to be deleted.
      *
      * @return returns true on successful deletion else returns false.
      */
	public boolean deleteStringItem(String _value) {
    	this.selectorCBM.data.getReadWriteLock().writeLock().lock();
    	try {
			if (this.itemMap.get(_value) == null) return false;
		    
			long indexL = this.itemMap.get(_value);
		    int index = (int) indexL;
		    this.selectorCBM.data.remove(index);
		    this.itemMap.remove(_value);
		    Map<String, Long> tempItemMap = new HashMap<>();
		    
		    for (Map.Entry<String, Long> entry : this.itemMap.entrySet()){
		    	if ( entry.getValue() > indexL ) 
		  			tempItemMap.put(entry.getKey(), indexL++);
		  	}
		  	this.itemMap.putAll(tempItemMap);
		  	this.numberOfItems--;
		    return true;
    	}finally {
    		this.selectorCBM.data.getReadWriteLock().writeLock().unlock();
    	}
     }

    /**
     * Updates the string thats being displayed.
     * If more than one item is present in the combo for that value the first one is changed.
     *
     * NOTE: To retain changes made to current SSRowSet call updateRow before calling the
     * updateItem on SSDBComboBox. (Only if you are using the SSDBComboBox and SSDataNavigator
     * for navigation in the screen. If you are not using the SSDBComboBox for navigation
     * then no need to call updateRow on the SSRowSet. Also if you are using only SSDBComboBox
     * for navigation you need not call the updateRow.)
     *
     * @param _value  the value corresponding to the item in combo to be updated.
     * @param _name   the new name that replace old one.
     *
     * @return returns true if successful else false.
     */
    public boolean updateItem(long _value, String _name) {
    	return updateStringItem(String.valueOf(_value), _name);
    }

    /**
     * Updates the string thats being displayed.
     * If more than one item is present in the combo for that value the first one is changed.
     *
     * NOTE: To retain changes made to current SSRowSet call updateRow before calling the
     * updateItem on SSDBComboBox. (Only if you are using the SSDBComboBox and SSDataNavigator
     * for navigation in the screen. If you are not using the SSDBComboBox for navigation
     * then no need to call updateRow on the SSRowSet. Also if you are using only SSDBComboBox
     * for navigation you need not call the updateRow.)
     *
     * @param _value  the value corresponding to the item in combo to be updated.
     * @param _name   the new name that replace old one.
     *
     * @return returns true if successful else false.
     */
    public boolean updateStringItem(String _value, String _name) {
    	if (this.itemMap.get(_value) == null) return false;
		
		long indexL = this.itemMap.get(_value);
		int index = (int) indexL;
		
		SelectorElement item = this.selectorCBM.getElementAt(index);
		item.setListValue(_name);
		// IF WE DON'T REPAIT THE SCREEN WILL DISPLAY OLD VALUE.
		repaint();
		return true;
    }

    /**
     * Initialization code.
     */
    protected void init() {
       // // TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER KEY IS PRESSED
        //Set<AWTKeyStroke> forwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        //Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
        //newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        //newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK ));
        //setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,newForwardKeys);
        
        // SET PREFERRED DIMENSIONS
            setPreferredSize(new Dimension(200,20));
    }

    /**
     * Method for handling binding of component to a SSRowSet column.
     */
    protected void bind() {

        // CHECK FOR NULL COLUMN/ROWSET
            if (this.columnName==null || this.columnName.trim().equals("") || this.sSRowSet==null) {
                return;
            }

        // REMOVE LISTENERS TO PREVENT DUPLICATION
            removeListeners();
            try {
	            
	        // BIND THE TEXT FIELD TO THE SPECIFIED COLUMN
	            this.textField.setDocument(new SSTextDocument(this.sSRowSet, this.columnName));
	
	        // SET THE COMBO BOX ITEM DISPLAYED
	            updateDisplay();
	            
            }finally { 
            	// ADD BACK LISTENERS
            	addListeners();
            }
    }

    /**
     * Updates the value displayed in the component based on the SSRowSet column
     * binding.
     */
	protected void updateDisplay() {

    	// THIS WILL MAKE SURE COMBO BOX ACTION LISTENER DOESN'T DO ANY THING EVEN IF IT GETS CALLED
    	this.inUpdateDisplay = true;
    	try {
	        // GET THE VALUE FROM TEXT FIELD
	        String text = this.textField.getText().trim();
	
	        if (!text.equals("") && this.itemMap != null && this.itemMap.get(text) != null ) {
	            //long valueInText = Long.parseLong(text);
	            // GET THE INDEX WHERE THIS VALUE IS IN THE VECTOR.
	        	//long longIndex = this.itemMap.get(text);
	        	Long index = this.itemMap.get(text);
	            //int index = (int) longIndex;
	            if (index != getSelectedIndex()) {
	                setSelectedIndex(index.intValue());
	                updateUI();
	            }
	        }
	        else {
	            setSelectedIndex(-1);
	            updateUI();
	        }
    	}finally {
    		this.inUpdateDisplay = false;
    	}
    }

    /**
     * Adds listeners for component and bound text field (where applicable).
     */
    void addListeners() {
        addActionListener(this.cmbListener);
        addKeyListener(this.myKeyListener);
        addPopupMenuListener(this.myPopupMenuListener);
        this.textField.getDocument().addDocumentListener(this.textFieldDocumentListener);
       
    }

    /**
     * Removes listeners for component and bound text field (where applicable).
     */
    void removeListeners() {
        removeActionListener(this.cmbListener);
        removeKeyListener(this.myKeyListener);
        removePopupMenuListener(this.myPopupMenuListener);
        this.textField.getDocument().removeDocumentListener(this.textFieldDocumentListener);        
    }
    
    /**
     * @author mvo
     * Listener for the combobox's popup menu which resets the combobox's list to the original data if it is invisible.
     */
    protected class MyPopupMenuListener implements PopupMenuListener{	
    	ActionListener[] saveActionListeners = new ActionListener[getActionListeners().length];
    	boolean saveSwitch = true;
    	
		public void addAllActionListeners(){	
    		for (ActionListener al: this.saveActionListeners) addActionListener(al);
			fireActionEvent();
		}
		
		public void removeAllActionListeners(){	
			if (getActionListeners().length != 0){	
				for (ActionListener al : getActionListeners()){
					removeActionListener(al);
				}
			}
		}
    	
		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
    		if (isEditable()){
    			hidePopup();
    		}
    	}
	
		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e){
			
			//if the popup was open before filtering, return as to not add action listeners
			if(SSDBComboBox.this.myKeyListener.openPopupFilter){	
				SSDBComboBox.this.myKeyListener.openPopupFilter = false;
				return;
			}
			//when menu closes, change out textfield to re-insert original items before filtering.
			if (SSDBComboBox.this.selectorCBM != null) SSDBComboBox.this.selectorCBM.setFilterEdit(new JTextField());
			//set editable to false if value is clicked while filtering and set text field to selected item.
			if (isEditable()){	
				setEditable(false);	
				SSDBComboBox.this.textField.setText(""+getSelectedFilteredValue());
			}
			addAllActionListeners();	
			requestFocus();
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			if (this.saveSwitch){	
				this.saveActionListeners = getActionListeners();
    			this.saveSwitch = false;
    			removeAllActionListeners();
			}
		}
    }
    
    /**
     * Listener(s) for the bound text field used to propigate values back to the
     * component's value.
     */
    protected class MyTextFieldDocumentListener implements DocumentListener {
    	@Override
		public void changedUpdate(DocumentEvent de) {
    		// DON'T MOVE THE REMOVE ADD LISTENER CALLS TO UPDATEDISPLAY
    		// AS UPDATE DISPLAY IS CALLED IN OTHER PLACES WHICH REMOVE AND ADD LISTENERS
    		// THIS WOULD MAKE THE ADDLISTENER CALL TWICE SO THERE WILL BE TWO LISTENERS
    		// AND A CALL TO REMOVE LISTENER IS NOT GOOD ENOUGH
    		removeListeners();
       		updateDisplay();
       		addListeners();
        }
    	
        @Override
		public void insertUpdate(DocumentEvent de) {
        	removeListeners();
       		updateDisplay();
       		addListeners();
        }
        
        @Override
		public void removeUpdate(DocumentEvent de) {
        	removeListeners();
       		updateDisplay();
       		addListeners();
        }

    } // end protected class MyTextFieldDocumentListener implements DocumentListener {
    
 
    /**
     * @author mvo
     * Listener for JTextField "filterText" which allows for navigation of filtered items in combo box.
     */
    protected class FilterKeyListener extends KeyAdapter {	
    	String savePrimary;
    	
    	@Override
		public void keyPressed(KeyEvent ke){	
    		
    		if(ke.getKeyCode() == KeyEvent.VK_ESCAPE){	
    			setEditable(false);
    			hidePopup();
				setSelectedIndex(SSDBComboBox.this.myKeyListener.saveIndex);
				SSDBComboBox.this.textField.setText(""+getSelectedFilteredValue());
    			return;
    		}
    	}
    	@Override
		public void keyReleased(KeyEvent ke){	
    		//tab will traverse to next component
    		if (ke.getKeyCode() == KeyEvent.VK_TAB){	
    			setEditable(false);
				hidePopup();
				transferFocus();
    		}    		
    		//turn off filter if arrows are pressed and keep focus on combo box
    		else if (ke.getKeyCode() == KeyEvent.VK_DOWN || ke.getKeyCode() == KeyEvent.VK_UP){	
    			setEditable(false);
    			showPopup();
    			requestFocus();
			}
    		else if (0 == getItemCount()){
    			repaint();
    			showPopup();
    		}
    		else {
	    		this.savePrimary = getSelectedFilteredValue();
	    		
	    		//if the combo box has less items than the popup's row length, refresh the popup box.
	    		if(getItemCount() < 9 || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE)
	    		{	SSDBComboBox.this.myKeyListener.openPopupFilter = true;
	    			hidePopup();
	    		}
	    		showPopup();
    		}
    	}
    }
    
    /**
     * Gets the selected value of the selected item in the filtered list.
     * @return a String corresponding to the currently selected value in the SSDBCombobBox
     */
    public String getSelectedFilteredValue() {	
    	if(getSelectedIndex() < 0) setSelectedIndex(0);
    	//gets the primary value of the selected index of the filtered list.
    	Object selectedValue = this.selectorCBM.getSelectedBoundData(getSelectedIndex());
    	if(selectedValue == null)
    		return null;
    	return selectedValue.toString();
    }
   
    /**
     * Listener for focus in filter text field.
     */
    protected class FilterFocusListener extends FocusAdapter{	
		@Override
		public void focusGained(FocusEvent fe){	
			showPopup();
		}
	}
   
    /**
     * Listener for keystroke-based, string matching, combo box navigation.
     */
    protected class MyKeyListener extends KeyAdapter {
    	private JTextField filterText;
    	private FilterKeyListener filterKeyListener = new FilterKeyListener();
    	boolean openPopupFilter = false;
    	int saveIndex;
    	@Override
		public void keyPressed(KeyEvent ke){	
    		this.saveIndex = getSelectedIndex();
    		if (SSDBComboBox.this.myPopupMenuListener.saveSwitch) {	
    			SSDBComboBox.this.myPopupMenuListener.saveActionListeners = getActionListeners();
    			SSDBComboBox.this.myPopupMenuListener.saveSwitch = false;
    		}
    		SSDBComboBox.this.myPopupMenuListener.removeAllActionListeners();
    	}
    	@Override
		public void keyReleased(KeyEvent ke){	
    		//reset the list and set the text field to the selected item's primary key 
    		if (ke.getKeyCode() == KeyEvent.VK_ENTER){
    			
    			//if enter is pressed inside of the filter textfield and no item is selected
    			//pick the last item selected item and set it as the current selected item
    			if (-1 == getSelectedIndex()){
    				setSelectedItem(null);
    				SSDBComboBox.this.textField.setText(this.filterKeyListener.savePrimary);
    			}
    			return;
    		}
    		
    		if (ke.getKeyCode() == KeyEvent.VK_DOWN ||  ke.getKeyCode() == KeyEvent.VK_UP || !SSDBComboBox.this.filterSwitch) return;
    	
    		//take the first key pressed, set combo box to editable, turn on filter, and set the text field to that saved key
    		if (ke.getKeyCode() >= KeyEvent.VK_A & ke.getKeyCode() <= KeyEvent.VK_BACK_SLASH 					||
    			ke.getKeyCode() >= KeyEvent.VK_COMMA & ke.getKeyCode() <= KeyEvent.VK_9 	  					||
    			ke.getKeyCode() >= KeyEvent.VK_OPEN_BRACKET & ke.getKeyCode() <= KeyEvent.VK_CLOSE_BRACKET		||
    			ke.getKeyCode() == KeyEvent.VK_PLUS																||
    			ke.getKeyCode() == KeyEvent.VK_QUOTE) {	
    			// if the popup is open, close it and do not add listeners
        		if (isPopupVisible()){
        			this.openPopupFilter = true;
        			hidePopup();
        		}
    			setEditable(true);
    			this.filterText = (JTextField) getEditor().getEditorComponent();
    			SSDBComboBox.this.selectorCBM.setFilterEdit(this.filterText);
    			this.filterText.setText(""+ke.getKeyChar());
    			this.filterText.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
    			
    			// SINCE WE HAVE TO ADD THE LISTENER IN COMBO KEY LISTENER
    			// MAKE SURE WE ARE NOT ADDING IT MULTIPLE TIMES.
    			// EASY WAY TO DO IT IS TO REMOVE IT AND THEN ADD IT.
       			this.filterText.removeKeyListener(this.filterKeyListener);
       			this.filterText.removeFocusListener(SSDBComboBox.this.filterFocusListener);
       			this.filterText.addKeyListener(this.filterKeyListener);
       			this.filterText.addFocusListener(SSDBComboBox.this.filterFocusListener);

        	}
    	}
    }

    /**
     * Method that sets the combo box to be filterable. 
     * 
     * @param filter boolean to turn filtering on or off
     */
    public void setFilterable(boolean filter) {	
    	this.filterSwitch = filter;
    	return;
    }
    
    /**
     * Listener(s) for the component's value used to propagate changes back to
     * bound text field.
     */
    protected class MyComboListener implements ActionListener {
    	@Override
		public void actionPerformed(ActionEvent ae) {
    		// IF WE ARE UPDATING THE DISPLAY DON'T DO ANY THING.
    		if(SSDBComboBox.this.inUpdateDisplay) {
    			return;
    		}
    		
        	//dont fire an action if the size of the filtered model is not the same as the initial item size
        	if (SSDBComboBox.this.selectorCBM != null){
        		if (SSDBComboBox.this.selectorCBM.getSize() != SSDBComboBox.this.selectorCBM.data.size()) return;
        	}
        	
        	SSDBComboBox.this.textField.getDocument().removeDocumentListener(SSDBComboBox.this.textFieldDocumentListener);

        	try {
	            // GET THE INDEX CORRESPONDING TO THE SELECTED TEXT IN COMBO
	            int index = getSelectedIndex();
	            // IF THE USER WANTS TO REMOVE COMPLETELY THE VALUE IN THE FIELD HE CHOOSES
	            // THE EMPTY STRING IN COMBO THEN THE TEXT FIELD IS SET TO EMPTY STRING
	            if (index != -1) {
	                try {
	                    String textFieldText = SSDBComboBox.this.textField.getText();
	                    String textPK= SSDBComboBox.this.selectorCBM.getSelectedBoundData(index).toString();
	                    if (!textFieldText.equals(textPK)) {
	                        SSDBComboBox.this.textField.setText(textPK);
	                    }
	                    // IF THE LONG VALUE CORRESPONDING TO THE SELECTED TEXT OF COMBO NOT EQUAL
	                    // TO THAT IN THE TEXT FIELD THEN CHANGE THE TEXT IN THE TEXT FIELD TO THAT VALUE
	                    // IF ITS THE SAME LEAVE IT AS IS
	                } catch(NullPointerException npe) {
	                	npe.printStackTrace();
	                } catch(NumberFormatException nfe) {
	                	nfe.printStackTrace();
	                }
	            }
	            else {
	                SSDBComboBox.this.textField.setText("");
	            }
	            
        	}finally {
        		SSDBComboBox.this.textField.getDocument().addDocumentListener(SSDBComboBox.this.textFieldDocumentListener);
        	}
            
            // WHEN SET SELECTED INDEX IS CALLED SET SELECTED ITEM WILL BE CALLED ON THE MODEL AND THIS FUNCTION 
            // IS SUPPOSED TO FIRE A EVENT TO CHANGE THE TEXT BUT IT WILL CAUSE ISSUES IN OUR IMPLEMENTATION
            // BUT WE WILL GET ACTION EVENT SO REPAIT TO REFLECT THE CHANGE IN COMBO SELECTION
            repaint();
        }

    } // protected class MyComboListener implements ActionListener {

} // end public class SSDBComboBox extends JComponent {

/*
 * $Log: SSDBComboBox.java,v $
 * Revision 1.39  2013/12/09 22:43:01  prasanth
 * Did some code cleanup and modified code to display the value when we set value programatically. The value was being set but the display was not updated.
 *
 * Revision 1.38  2012/08/09 20:39:21  beevo
 * Added removePopupMenuListener() to removeListeners() method to fix bug that occured when bind() was called.
 *
 * Revision 1.37  2012/08/09 17:22:16  prasanth
 * Removed a @override annotation  as jdk1.5 doesn't like it.
 *
 * Revision 1.36  2012/08/08 19:55:03  beevo
 * Added a text filter for the items in the combo box. The setFilterable() method can turn the filter on or off.
 *
 * Revision 1.35  2011/10/24 17:13:30  prasanth
 * Changed the way focus is transfered for ENTER key.
 *
 * Revision 1.34  2008/07/18 14:49:58  prasanth
 * In key listener ignoring function keys.
 *
 * Revision 1.33  2006/05/15 16:10:38  prasanth
 * Updated copy right
 *
 * Revision 1.32  2005/05/19 16:14:35  prasanth
 * Added setPrimaryKeyColumnName method. The set method was missing.
 *
 * Revision 1.31  2005/02/22 16:07:10  prasanth
 * While checking if secondDisplayColumnName has  to be used for displaying
 * text, in addition to checking for null, checking for empty string.
 *
 * Revision 1.30  2005/02/21 16:31:32  prasanth
 * In bind checking for empty columnName before binding the component.
 *
 * Revision 1.29  2005/02/13 15:38:20  yoda2
 * Removed redundant PropertyChangeListener and VetoableChangeListener class variables and methods from components with JComponent as an ancestor.
 *
 * Revision 1.28  2005/02/12 03:29:26  yoda2
 * Added bound properties (for beans).
 *
 * Revision 1.27  2005/02/11 22:59:27  yoda2
 * Imported PropertyVetoException and added some bound properties.
 *
 * Revision 1.26  2005/02/11 20:16:01  yoda2  private SelectorComboBoxModel selectorCBM;
 * Added infrastructure to support property & vetoable change listeners (for beans).
 *
 * Revision 1.25  2005/02/10 20:12:57  yoda2
 * Setter/getter cleanup & method reordering for consistency.
 *
 * Revision 1.24  2005/02/10 03:46:47  yoda2
 * Replaced all setDisplay() methods & calls with updateDisplay() methods & calls to prevent any setter/getter confusion.
 *
 * Revision 1.23  2005/02/07 20:26:06  yoda2
 * Updated to allow non-numeric primary keys. JavaDoc cleanup.
 *
 * Revision 1.22  2005/02/04 22:48:53  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.21  2005/02/04 00:02:48  prasanth
 * 1. Removed commented out code.
 * 2. Using setDisplay in document listener.
 *
 * Revision 1.20  2005/02/02 23:36:58  yoda2
 * Removed setMaximiumSize() calls.
 *
 * Revision 1.19  2005/01/19 20:54:44  yoda2
 * API cleanup.
 *
 * Revision 1.18  2005/01/19 16:47:25  yoda2
 * Finished debugging and renamed some private variables to better distinguish between the bound column name and the column name representing the PK from the DB query.
 *
 * Revision 1.17  2005/01/19 03:17:08  yoda2
 * Rewrote to extend JComboBox rather than JComponent.
 *
 * Revision 1.16  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.15  2004/11/01 15:53:30  yoda2
 * Fixed various JavaDoc errors.
 *
 * Revision 1.14  2004/10/25 22:57:40  prasanth
 * Changed Connection to SSConnection.
 *
 * Revision 1.13  2004/10/25 22:03:18  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.12  2004/10/25 19:51:03  prasanth
 * Modified to use the new SSRowSet instead of  RowSet.
 *
 * Revision 1.11  2004/08/24 22:08:54  prasanth
 * Updating the numberOfItems variable in deleteItem & addItem.
 *
 * Revision 1.10  2004/08/12 23:52:36  prasanth
 * If seleted index is -1 the column value was not updated.
 * Now setting to null if the selected index is -1.
 *
 * Revision 1.9  2004/08/10 22:06:58  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.8  2004/08/09 15:39:48  prasanth
 * Added key listener to transfer focus on enter key.
 *
 * Revision 1.7  2004/08/02 15:21:55  prasanth
 * 1. Deprecated  setTextField function.
 * 2. Added setSelectedValue function.
 * 3. Also added addComponent (private) method.
 *
 * Revision 1.6  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.5  2004/02/23 16:37:41  prasanth
 * Added GENDER_OPTION.
 *
 * Revision 1.4  2003/11/26 21:21:14  prasanth
 * Functionality to format a date object before displaying in the combo.
 *
 * Revision 1.3  2003/10/31 16:06:25  prasanth
 * Added method setSeperator().
 *
 * Revision 1.2  2003/09/25 14:27:45  yoda2
 * Removed unused Import statements and added preformatting tags to JavaDoc descriptions.
 *
 * Revision 1.1.1.1  2003/09/25 13:56:43  yoda2
 * Initial CVS import for SwingSet.
 *
 */
