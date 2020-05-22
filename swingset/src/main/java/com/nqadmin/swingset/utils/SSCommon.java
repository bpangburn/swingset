/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

package com.nqadmin.swingset.utils;

import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.util.StringTokenizer;

import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nqadmin.swingset.datasources.SSConnection;
import com.nqadmin.swingset.datasources.SSRowSet;

/**
 * SSCommon.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Datasource binding data members and methods common to all SwingSet
 * components.
 * 
 * All SwingSet components should have a ssCommon datamember of type SSCommon
 * and should implement SSComponentInterface
 * 
 * SwingSet components will implement addComponentListener() and
 * removeComponentListener() to maintain data flow from the
 * JComponent to ssCommon.ssRowSet and from ssCommon.ssRowSet to JComponent
 * 
 * Generally the developer will need to add an inner class and corresponding
 * data member that implements the appropriate listener for the JComponent
 * involved (e.g., ItemListener for a class extending JCheckBox, ChangeListener
 * for a class extending JSlider, DocumentListener for a class extending
 * JTextField, etc.).
 * 
 * It is possible some SwingSet will be unbound where a change to the component
 * should not trigger a database change.
 * 
 * A good example of this is SSDBComboBox, which may be used solely for
 * navigation.
 */
public class SSCommon implements Serializable {

	/**
     * Document listener provided for convenience for SwingSet Components based on JTextComponents
     * 
     * Updates the underlying RowSet there is a change to the Document object. E.g., a call to setText() on a JTextField.
     * 
     * DocumentListener events generally, but not always get fired twice any time there is an update to the JTextField:
     * a removeUpdate() followed by insertUpdate().
     * See:
     * https://stackoverflow.com/questions/15209766/why-jtextfield-settext-will-fire-documentlisteners-removeupdate-before-change#15213813
     * 
     * Using partial solution here from here:
     * https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
     * 
     * Having removeUpdate() and insertUpdate() both call changedUpdate(). changedUpdate() uses counters
     * and SwingUtilities.invokeLater() to only update the display on the last method called.
     */

    protected class SSDocumentListener implements DocumentListener, Serializable {
    	
    	/**
		 * unique serial id
		 */
		private static final long serialVersionUID = 2287696691641310793L;
		
		/**
		 * variables needed to consolidate calls to removeUpdate() and insertUpdate() from DocumentListener
		 */
		private int lastChange=0;
    	private int lastNotifiedChange = 0;
    	
    	@Override    	
		public void changedUpdate(DocumentEvent de) {
			lastChange++;
			//System.out.println("SSTextDocument (" + boundColumnName + ") - changedUpdate(): lastChange=" + lastChange + ", lastNotifiedChange=" + lastNotifiedChange);
			// Delay execution of logic until all listener methods are called for current event
			// See: https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
			SwingUtilities.invokeLater(() -> {
				if (lastNotifiedChange != lastChange) {
					lastNotifiedChange = lastChange;
					
					removeSSRowSetListener();
					
		            try {
		                setBoundColumnText(((javax.swing.text.JTextComponent)getSSComponent()).getText());
		            } finally {
		            	addSSRowSetListener();
		            }
				}
			});
    	}
    	
        @Override
		public void insertUpdate(DocumentEvent de) {
        	//System.out.println("SSTextDocument (" + this.boundColumnName + ") - insertUpdate()");
        	changedUpdate(de);
        }
        
        @Override
		public void removeUpdate(DocumentEvent de) {
        	//System.out.println("SSTextDocument (" + this.boundColumnName + ") - removeUpdate()"); 
        	changedUpdate(de);
        }

    } // end protected class SSDocumentListener

	/**
	 * Listener(s) for the underlying SSRowSet used to update the bound SwingSet component.
	 */
	protected class SSRowSetListener implements RowSetListener, Serializable {
		/**
		 * unique serial ID
		 */
		private static final long serialVersionUID = -5194433723970625351L;

		/**
		 * When the cursor moves we want to trigger a change to the bound Component
		 * display/value.
		 */
		@Override
		public void cursorMoved(RowSetEvent event) {
//          System.out.println("Cursor Moved");
			updateSSComponent();
		}

		/**
		 * When the database row changes we want to trigger a change to the bound
		 * Component display/value.
		 */
		@Override
		public void rowChanged(RowSetEvent event) {
//          System.out.println("Row Changed");
			updateSSComponent();
		}

		/**
		 * When the RowSet is modified we want to trigger a change to the bound
		 * Component display/value.
		 */
		@Override
		public void rowSetChanged(RowSetEvent event) {
//          System.out.println("RowSet Changed");
			updateSSComponent();
		}

	} // end protected class SSRowSetListener

	/**
	 * Constant to indicate that no RowSet column index has been specified.
	 */
	public static final int NO_COLUMN_INDEX = -1;

	/**
	 * Constant to indicate that no RowSet column type has been specified.
	 */
	public static final int NO_COLUMN_TYPE = -1;

	/**
	 * Unique serial ID.
	 */
	protected static final long serialVersionUID = -7670575893542057725L;

	/**
	 * Converts a date string in "MM/dd/yyyy" format to an SQL Date.
	 *
	 * @param _strDate date string in "MM/dd/yyyy" format
	 *
	 * @return return SQL date for the string specified
	 */
	public static Date getSQLDate(String _strDate) {
		StringTokenizer strtok = new StringTokenizer(_strDate, "/", false);
		String month = strtok.nextToken();
		String day = strtok.nextToken();
		String newStrDate = strtok.nextToken() + "-" + month + "-" + day;
		return Date.valueOf(newStrDate);
	}

	/**
	 * flag to indicate if the bound database column can be null
	 */
	private boolean allowNull = false;

	/**
	 * Index of SSRowSet column to which the SwingSet component will be bound.
	 */
	private int boundColumnIndex = NO_COLUMN_INDEX;
    
    /**
	 * Name of SSRowSet column to which the SwingSet component will be bound.
	 */
	private String boundColumnName = null;
    
	/**
	 * Column SQL data type.
	 */
	private int boundColumnType = NO_COLUMN_TYPE;
	
	/**
	 * flag to indicate if we're inside of a bind() method
	 */
	private volatile boolean inBinding = false;
    
//	/**
//	 * SSRowSet column containing the primary key.
//	 */
//	private String primaryKeyColumn = null;

	/**
	 * parent SwingSet component
	 */
	private Object ssComponent = null;

	/**
	 * database connection
	 */
	private SSConnection ssConnection = null;

	/**
	 * Underlying Document listener (where SwingSet component is a JTextComponent)
	 */
	private SSDocumentListener ssDocumentListener = new SSDocumentListener();

	/**
	 * SSRowSet from which component will get/set values.
	 */
	private SSRowSet ssRowSet = null;

	/**
	 * Underlying SSRowSet listener.
	 */
	private SSRowSetListener ssRowSetListener = new SSRowSetListener();

	/**
	 * Constructor expecting a SwingSet component as an argument (usually called as
	 * = new SSCommon(this);)
	 * 
	 * @param _ssComponent SwingSet component having this SSCommon instance as a
	 *                     datamember
	 */
	public SSCommon(Object _ssComponent) {
		setSSComponent(_ssComponent);

		init();

	}

	/**
	 * Convenience method to add both RowSet and SwingSet Component listeners.
	 */
	public void addListeners() {
		addSSRowSetListener();
		addSSComponentListener();
	}

	/**
	 * Method to add any SwingSet Component listener(s).
	 */
	public void addSSComponentListener() {
		((SSComponentInterface) ssComponent).addSSComponentListener();
	}
	
	/**
     * Class to add a Document listener when the SwingSet component is a JTextComponent
     */
    public void addSSDocumentListener() {
    	//ssRowSet.addSSRowSetListener(ssRowSetListener);
    	((javax.swing.text.JTextComponent)getSSComponent()).getDocument().addDocumentListener(ssDocumentListener);
    	
    }

	/**
	 * Method to add the RowSet listener.
	 */
	public void addSSRowSetListener() {
		ssRowSet.addSSRowSetListener(ssRowSetListener);
	}

	/**
	 * Updates the SSComponent with a valid RowSet and Column (Name or Index)
	 */
	protected void bind() {

// TODO consider updating Component to null/zero/empty string if not valid column name, column index, or rowset    	
		// CHECK FOR NULL COLUMN/ROWSET
		if ((this.boundColumnName == null && this.boundColumnIndex == NO_COLUMN_INDEX) || this.ssRowSet == null) {
			return;
		}

		// UPDATE COMPONENT
		// For an SSDBComboBox, we have likely not yet called execute to populate the combo lists so the text for the first record will be blank.
		updateSSComponent();
		
		//Thread.dumpStack();

	}

	/**
	 * Takes care of setting RowSet and Column Index for ssCommon and then calls
	 * bind() to update Component;
	 *
	 * @param _ssRowSet         datasource to be used
	 * @param _boundColumnIndex index of the column to which this check box should
	 *                          be bound
	 */
	public void bind(SSRowSet _ssRowSet, int _boundColumnIndex) {// throws java.sql.SQLException {
		// INDICATE THAT WE'RE UPDATING THE BINDINGS
		inBinding = true;

		// UPDATE ROWSET
		removeSSRowSetListener();
		setSSRowSet(_ssRowSet);
		addSSRowSetListener();

		// UPDATE COLUMN NAME
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
	 * @param _ssRowSet        datasource to be used
	 * @param _boundColumnName name of the column to which this check box should be
	 *                         bound
	 */
	public void bind(SSRowSet _ssRowSet, String _boundColumnName) {// throws java.sql.SQLException {
		// INDICATE THAT WE'RE UPDATING THE BINDINGS
		inBinding = true;

		// UPDATE ROWSET
		removeSSRowSetListener();
		setSSRowSet(_ssRowSet);
		addSSRowSetListener();

		// UPDATE COLUMN NAME
		setBoundColumnName(_boundColumnName);

		// INDICATE THAT WE'RE DONE SETTING THE BINDINGS
		inBinding = false;

		// UPDATE THE COMPONENT
		bind();

	}

	/**
	 * Retrieves the allowNull flag for the bound database column.
	 * 
	 * @return true if bound database column can contain null values, otherwise returns false
	 */
	public boolean getAllowNull() {
		return allowNull;
	}

	/**
	 * Returns the index of the database column to which the SwingSet component is bound.
	 *
	 * @return returns the index of the column to which the SwingSet component is bound
	 */
	public int getBoundColumnIndex() {
		return this.boundColumnIndex;
	}

	/**
	 * Returns the name of the database column to which the SwingSet component is bound.
	 * 
	 * @return the boundColumnName
	 */
	public String getBoundColumnName() {
		return boundColumnName;
	}

	/**
	 * Returns a String representing the value in the bound database column.
	 * 
	 * New functionality added (2020) to allow this method to return a null String
	 * if allowNull==true. allowNull is false by default so nulls will be converted to empty strings.
	 * 
	 * @return String containing the value in the bound database column
	 */
	public String getBoundColumnText() {
		
// TODO Consider checking for a null RowSet. This would be the case for an unbound SSDBComboBox used for navigation.		

		String value = "";

		try {
			if (getSSRowSet().getRow() != 0) {
				value = getSSRowSet().getColumnText(getBoundColumnName());
				if (!getAllowNull() && value == null) {
					value = "";
				}
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}

		return value;
	}

	/**
	 * Returns the integer code representing the bound database column data type.
	 * 
	 * Based on java.sql.Types
	 * 
	 * @return the data type of the bound column
	 */
	public int getBoundColumnType() {
		return boundColumnType;
	}

//	/**
//	 * Returns the primary key column name for the RowSet query
//	 * 
//	 * Used primarily for associating a primary key value with another column in a SwingSet list component.
//	 * 
//	 * @return the primaryKeyColumn
//	 */
//// TODO if this is just used for lists then we may want to put into a separate SSListCommon class
//	public String getPrimaryKeyColumn() {
//		return primaryKeyColumn;
//	}

	/**
	 * @return the parent/calling SwingSet JComponent implementing
	 *         SSComponentInterface
	 */
	public Object getSSComponent() {
		return this.ssComponent;
	}

	/**
	 * Returns the SSConnection to the database
	 * 
	 * @return the ssConnection
	 */
	public SSConnection getSSConnection() {
		return ssConnection;
	}

	/**
	 * Returns the SSRowSet to which the SwingSet component is bound.
	 *
	 * @return SSRowSet to which the SwingSet component is bound
	 */
	public SSRowSet getSSRowSet() {
		return this.ssRowSet;
	}

	/**
	 * Returns Listener for the RowSet bound to the database
	 * 
	 * @return listener for the bound RowSet
	 */
	public SSRowSetListener getSSRowSetListener() {
		return this.ssRowSetListener;
	}

	/**
	 * Method called from Constructor to perform one-time setup tasks including
	 * field traversal and any custom initialization method specific to the SwingSet
	 * component.
	 */
	protected void init() {
		((SSComponentInterface) getSSComponent()).configureTraversalKeys();
		((SSComponentInterface) getSSComponent()).customInit();
	}

	/**
	 * Convenience method to remove both RowSet and SwingSet Component listeners.
	 */
	public void removeListeners() {
		removeSSRowSetListener();
		removeSSComponentListener();
	}

	/**
	 * Method to remove any SwingSet Component listener(s).
	 */
	public void removeSSComponentListener() {
		((SSComponentInterface) ssComponent).removeSSComponentListener();
	}

	/**
     * Class to remove a Document listener when the SwingSet component is a JTextComponent
     */
    public void removeSSDocumentListener() {
    	//ssRowSet.addSSRowSetListener(ssRowSetListener);
    	((javax.swing.text.JTextComponent)getSSComponent()).getDocument().removeDocumentListener(ssDocumentListener);
    	
    }

	/**
	 * Method to remove the RowSet listener.
	 */
	public void removeSSRowSetListener() {
		if (ssRowSet!=null) {
			ssRowSet.removeSSRowSetListener(ssRowSetListener);
		}
	}

	/**
	 * Sets the allowNull flag for the bound database column.
	 * 
	 * @param _allowNull flag to indicate if the bound database column can be null
	 */
	public void setAllowNull(boolean _allowNull) {
		this.allowNull = _allowNull;
	}

	/**
	 * Updates the bound database column with the specified Array.
	 * 
	 * Used for SSList or other component where multiple items can be selected.
	 * 
	 * @param _boundColumnArray Array to write to bound database column
	 * @throws SQLException thrown if there is a problem writing the array to the RowSet
	 */
	public void setBoundColumnArray(SSArray _boundColumnArray) throws SQLException {
		//getSSRowSet().updateColumnText( _boundColumnText, getBoundColumnName(), getAllowNull());
		
		getSSRowSet().updateArray(getBoundColumnName(), _boundColumnArray);
	}

	/**
	 * Sets the column index to which the Component is to be bound.
	 *
	 * @param _boundColumnIndex column index to which the Component is to be bound
	 */
	public void setBoundColumnIndex(int _boundColumnIndex) {

		// SET COLUMN INDEX
		if (_boundColumnIndex > NO_COLUMN_INDEX) {
			this.boundColumnIndex = _boundColumnIndex;
		} else {
			this.boundColumnIndex = NO_COLUMN_INDEX;
		}

		// DETERMINE COLUMN NAME AND TYPE
		try {
			// IF COLUMN INDEX IS VALID, GET COLUMN NAME, OTHERWISE SET TO NULL
// TODO update SSRowSet to return constant or throw Exception if invalid/out of bounds	        	
			if (this.boundColumnIndex != NO_COLUMN_INDEX) {
				this.boundColumnName = getSSRowSet().getColumnName(this.getBoundColumnIndex());
				this.boundColumnType = getSSRowSet().getColumnType(this.boundColumnIndex);
			} else {
				this.boundColumnName = null;
				this.boundColumnType = NO_COLUMN_TYPE;
			}

		} catch (SQLException se) {
			se.printStackTrace();
		}

		// BIND UPDATED COLUMN IF APPLICABLE
		if (!inBinding)
			bind();

	}

	/**
	 * Sets the name of the bound database column.
	 * 
	 * @param _boundColumnName column name to which the Component is to be bound.
	 */
	public void setBoundColumnName(String _boundColumnName) {

		// SET COLUMN NAME
		if (!_boundColumnName.isEmpty()) {
			this.boundColumnName = _boundColumnName;
		} else {
			this.boundColumnName = null;
		}

		// DETERMINE COLUMN INDEX AND TYPE
		try {
			// IF COLUMN NAME ISN'T NULL, SET COLUMN INDEX - OTHERWISE, SET INDEX TO
			// NO_INDEX
// TODO update SSRowSet to return constant or throw Exception if invalid/out of bounds	        	
			if (this.boundColumnName != null) {
				this.boundColumnIndex = getSSRowSet().getColumnIndex(this.boundColumnName);
				this.boundColumnType = getSSRowSet().getColumnType(this.boundColumnIndex);
			} else {
				this.boundColumnIndex = NO_COLUMN_INDEX;
				this.boundColumnType = NO_COLUMN_TYPE;
			}

		} catch (SQLException se) {
			se.printStackTrace();
		}

		// BIND UPDATED COLUMN IF APPLICABLE
		if (!inBinding)
			bind();
	}
	
	/**
	 * Updates the bound database column with the specified String.
	 * 
	 * @param _boundColumnText value to write to bound database column
	 */
	public void setBoundColumnText(String _boundColumnText) {
		getSSRowSet().updateColumnText(_boundColumnText, getBoundColumnName(), getAllowNull());
	}

//	/**
//	 * @param primaryKeyColumn the primaryKeyColumn to set
//	 */
//	public void setPrimaryKeyColumn(String primaryKeyColumn) {
//		this.primaryKeyColumn = primaryKeyColumn;
//	}

	/**
	 * Sets the SwingSet component of which this SSCommon instance is a datamember.
	 * 
	 * @param _ssComponent the parent/calling SwingSet JComponent implementing
	 *                     SSComponentInterface
	 */
	public void setSSComponent(Object _ssComponent) {
		this.ssComponent = _ssComponent;
	}

	/**
	 * Sets the SSConnection to the database
	 * 
	 * @param _ssConnection the ssConnection to set
	 */
	public void setSSConnection(SSConnection _ssConnection) {
		this.ssConnection = _ssConnection;
	}

	/**
	 * Sets the SSRowSet to which the Component is bound.
	 *
	 * @param _ssRowSet SSRowSet to which the component is bound
	 */
	// public void setSSRowSet(SSRowSet _ssRowSet) throws SQLException {
	public void setSSRowSet(SSRowSet _ssRowSet) {
		this.ssRowSet = _ssRowSet;
		if (!inBinding)
			bind();

	}

	/**
	 * Method used by SSRowSet listeners to update the bound SwingSet component.
	 * 
	 * Handles removal of Component listener before update and addition of listener
	 * after update.
	 */
	public void updateSSComponent() {
		((SSComponentInterface) ssComponent).removeSSComponentListener();

		((SSComponentInterface) ssComponent).updateSSComponent();

		((SSComponentInterface) ssComponent).addSSComponentListener();

	}

}