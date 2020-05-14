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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.sql.SQLException;

import javax.swing.JCheckBox;

import com.nqadmin.swingset.datasources.SSRowSet;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

/**
 * SSCheckBox.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Used to display the boolean values stored in the database. The SSCheckBox can
 * be bound to a numeric or boolean database column. Currently, binding to a
 * boolean column has been tested only with PostgreSQL. If bound to a numeric
 * database column, a checked SSCheckBox returns a '1' to the database and an
 * unchecked SSCheckBox will returns a '0'. In the future an option may be added
 * to allow the user to specify the values returned for the checked and
 * unchecked SSCheckBox states.
 *
 * Note that for naming consistency, SSCheckBox replaced SSDBCheckBox
 * 01-10-2005.
 */
public class SSCheckBox extends JCheckBox implements SSComponentInterface {

	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column
	 */
	protected class SSCheckBoxListener implements ItemListener, Serializable {

		/**
		 * unique serial id
		 */
		private static final long serialVersionUID = -8006881399306841024L;

		@Override
		public void itemStateChanged(ItemEvent ie) {
			// SSCheckBox.this.textField.getDocument().removeDocumentListener(SSCheckBox.this.textFieldDocumentListener);
			// removeTextDocumentListener();
			removeSSRowSetListener();

			if (((JCheckBox) ie.getSource()).isSelected()) {
				// switch(SSCheckBox.this.columnType) {
				switch (getBoundColumnType()) {
				case java.sql.Types.INTEGER:
				case java.sql.Types.SMALLINT:
				case java.sql.Types.TINYINT:
					// SSCheckBox.this.textField.setText(String.valueOf(SSCheckBox.this.CHECKED));
					setBoundColumnText(String.valueOf(SSCheckBox.this.CHECKED));
					break;
				case java.sql.Types.BIT:
				case java.sql.Types.BOOLEAN:
					// SSCheckBox.this.textField.setText(BOOLEAN_CHECKED);
					setBoundColumnText(BOOLEAN_CHECKED);
					break;
				default:
					System.out.println("Unknown column type of " + getBoundColumnType());
					break;
				}
			} else {
				// switch(SSCheckBox.this.columnType) {
				switch (getBoundColumnType()) {
				case java.sql.Types.INTEGER:
				case java.sql.Types.SMALLINT:
				case java.sql.Types.TINYINT:
					setBoundColumnText(String.valueOf(SSCheckBox.this.UNCHECKED));
					break;
				case java.sql.Types.BIT:
				case java.sql.Types.BOOLEAN:
					setBoundColumnText(BOOLEAN_UNCHECKED);
					break;
				default:
					System.out.println("Unknown column type of " + getBoundColumnType());
					break;
				}
			}

			// SSCheckBox.this.textField.getDocument().addDocumentListener(SSCheckBox.this.textFieldDocumentListener);
			// addTextDocumentListener();
			addSSRowSetListener();
		}

	} // end private class SSCheckBoxListener
		// {

	/**
	 * Checked value for Boolean columns.
	 */
	protected static String BOOLEAN_CHECKED = "true";

//	/**
//     * Text field bound to the SSRowSet.
//     */
//    protected JTextField textField = new JTextField();

//    /**
//     * SSRowSet from which component will get/set values.
//     */
//    protected SSRowSet sSRowSet;
//
//    /**
//     * SSRowSet column to which the component will be bound.
//     */
//    protected String columnName = "";

//    /**
//     * Column SQL data type.
//     */
//    protected int columnType = java.sql.Types.BIT;

	/**
	 * Unchecked value for Boolean columns.
	 */
	protected static String BOOLEAN_UNCHECKED = "false";

//    /**
//     * Bound text field document listener.
//     */
//    protected final MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -1204307502900668225L;

	/**
	 * Checked value for numeric columns.
	 */
	protected int CHECKED = 1;

	/**
	 * Component listener.
	 */
	protected final SSCheckBoxListener ssCheckBoxListener = new SSCheckBoxListener();

	/**
	 * Common fields shared across SwingSet components
	 */
	protected SSCommon ssCommon;

	/**
	 * Unchecked value for numeric columns.
	 */
	protected int UNCHECKED = 0;

	/**
	 * Creates an object of SSCheckBox.
	 */
	public SSCheckBox() {
		super();
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
	}

	/**
	 * Creates an object of SSCheckBox binding it so the specified column in the
	 * given SSRowSet.
	 *
	 * @param _ssRowSet        datasource to be used.
	 * @param _boundColumnName name of the column to which this check box should be
	 *                         bound
	 * 
	 * @throws SQLException - if a database access error occurs
	 */
	public SSCheckBox(SSRowSet _ssRowSet, String _boundColumnName) throws java.sql.SQLException {
		super();
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
		bind(_ssRowSet, _boundColumnName);
//        this.sSRowSet = _sSRowSet;
//        this.columnName = _columnName;
//        init();
//        bind();
	}

//    /**
//     * Sets the SSRowSet column name to which the component is bound.
//     *
//     * @param _columnName    column name in the SSRowSet to which the component is bound
//     * @throws SQLException - if a database access error occurs
//     */
//    public void setColumnName(String _columnName) throws java.sql.SQLException {
//        String oldValue = this.columnName;
//        this.columnName = _columnName;
//        firePropertyChange("columnName", oldValue, this.columnName);
//        bind();
//    }    
//    
//    /**
//     * Returns the SSRowSet column name to which the component is bound.
//     *
//     * @return column name to which the component is bound
//     */
//    public String getColumnName() {
//        return this.columnName;
//    }

//    /**
//     * Sets the SSRowSet to which the component is bound.
//     *
//     * @param _sSRowSet    SSRowSet to which the component is bound
//     * @throws SQLException - if a database access error occurs
//     */
//    public void setSSRowSet(SSRowSet _sSRowSet) throws java.sql.SQLException {
//        SSRowSet oldValue = this.sSRowSet;
//        this.sSRowSet = _sSRowSet;
//        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);
//        bind();
//    }     
//    
//    /**
//     * Returns the SSRowSet to which the component is bound.
//     *
//     * @return SSRowSet to which the component is bound
//     */
//    public SSRowSet getSSRowSet() {
//        return this.sSRowSet;
//    }

//    /**
//     * Sets the SSRowSet and column name to which the component is to be bound.
//     *
//     * @param _sSRowSet    datasource to be used.
//     * @param _columnName    Name of the column to which this check box should be bound
//     * @throws SQLException - if a database access error occurs
//     */
//    public void bind(SSRowSet _sSRowSet, String _columnName) throws java.sql.SQLException {
//        SSRowSet oldValue = this.sSRowSet;
//        this.sSRowSet = _sSRowSet;
//        //pChangeSupport.firePropertyChange("sSRowSet", oldValue, sSRowSet);
//        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);
//        
//        String oldValue2 = this.columnName;
//        this.columnName = _columnName;
//        //pChangeSupport.firePropertyChange("columnName", oldValue2, columnName);
//        firePropertyChange("columnName", oldValue2, this.columnName);
//        
//        bind();
//    }

//    /**
//     * Initialization code.
//     */
//    protected void init() {
//    	
//        // TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER KEY IS PRESSED
//        Set<AWTKeyStroke> forwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
//        Set<AWTKeyStroke> newForwardKeys = new HashSet<>(forwardKeys);
//        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
//        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK ));
//        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,newForwardKeys);       
//     
//    }

//    /**
//     * Method for handling binding of component to a SSRowSet column.
//     * 
//     * @throws SQLException - if a database access error occurs
//     */
//    protected void bind() throws java.sql.SQLException {
//        
//        // CHECK FOR NULL COLUMN/ROWSET
//            if (this.columnName==null || this.columnName.trim().equals("") || this.sSRowSet==null) {
//                return;
//            }
//            
//        // REMOVE LISTENERS TO PREVENT DUPLICATION
//            removeListeners();
//            
//         // BIND AND UPDATE DISPLAY            
//            try {
//
//	        // DETERMINE COLUMN TYPE
//				this.columnType = this.sSRowSet.getColumnType(this.columnName);            
//	
//	        // BIND THE TEXT FIELD TO THE SPECIFIED COLUMN
//	            this.textField.setDocument(new SSTextDocument(this.sSRowSet, this.columnName));
//	
//	        // SET THE COMBO BOX ITEM DISPLAYED
//	            updateDisplay();
//            } finally {
//	        // ADD BACK LISTENERS
//	            addListeners();
//            }
//               
//    }

//    /**
//     * Adds listeners for component and bound text field (where applicable).
//     */
//    private void addListeners() {
//        this.textField.getDocument().addDocumentListener(this.textFieldDocumentListener);
//        addItemListener(this.checkBoxListener);   
//    }

//    /**
//     * Removes listeners for component and bound text field (where applicable).
//     */
//    private void removeListeners() {
//        this.textField.getDocument().removeDocumentListener(this.textFieldDocumentListener);
//        removeItemListener(this.checkBoxListener);
//    }    

	/**
	 * Creates an object of SSCheckBox.
	 * 
	 * @param _text Checkbox label
	 */
	public SSCheckBox(String _text) {
		super(_text);
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
	}

//    /**
//     * Listener(s) for the bound text field used to propigate values back to the
//     * component's value.
//     */
//    protected class MyTextFieldDocumentListener implements DocumentListener, Serializable {
//
//        /**
//		 * unique serial id
//		 */
//		private static final long serialVersionUID = 662317066187756908L;
//
//		@Override
//		public void changedUpdate(DocumentEvent de){
//            removeItemListener(SSCheckBox.this.checkBoxListener);
//            
//            updateDisplay();
//            
//            addItemListener(SSCheckBox.this.checkBoxListener);
//        }
//
//        // WHEN EVER THERE IS A CHANGE IN THE VALUE IN THE TEXT FIELD CHANGE THE CHECK BOX
//        // ACCORDINGLY.
//        @Override
//		public void insertUpdate(DocumentEvent de) {
//            removeItemListener(SSCheckBox.this.checkBoxListener);
//            
//            updateDisplay();
//            
//            addItemListener( SSCheckBox.this.checkBoxListener );
//        }
//
//        // IF A REMOVE UPDATE OCCURS ON THE TEXT FIELD CHECK THE CHANGE AND SET THE
//        // CHECK BOX ACCORDINGLY.
//        @Override
//		public void removeUpdate(DocumentEvent de) {
//            removeItemListener(SSCheckBox.this.checkBoxListener);
//            
//            updateDisplay();
//            
//            addItemListener( SSCheckBox.this.checkBoxListener );
//        }
//    } // end private class MyTextFieldDocumentListener implements DocumentListener, Serializable {

//    /**
//     * Updates the underlying RowSet when there is a change to the Document object.
//     * 
//     * These types of changes can result from a change in the RowSet pushed to the Document or a call to setText() on the
//     * JTextField.
//     * 
//     * DocumentListener events generally, but not always get fired twice any time there is an update to the JTextField:
//     * a removeUpdate() followed by insertUpdate().
//     * See:
//     * https://stackoverflow.com/questions/15209766/why-jtextfield-settext-will-fire-documentlisteners-removeupdate-before-change#15213813
//     * 
//     * Using partial solution here from here:
//     * https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
//     * 
//     * Having removeUpdate() and insertUpdate() both call changedUpdate(). changedUpdate() uses counters
//     * and SwingUtilities.invokeLater() to only update the display on the last method called.
//     * 
//     * Note that we do not want to handle removal/addition of listeners in updateText() because other
//     * code could call it directly.
//     */
//
//    protected class MyTextFieldDocumentListener implements DocumentListener, Serializable {
//    	
//    	/**
//		 * unique serial id
//		 */
//		private static final long serialVersionUID = 662317066187756908L;
//		
//		/**
//		 * variables needed to consolidate calls to removeUpdate() and insertUpdate() from DocumentListener
//		 */
//		private int lastChange=0;
//    	private int lastNotifiedChange = 0;
//    	
//    	@Override    	
//		public void changedUpdate(DocumentEvent de) {
//			lastChange++;
//			//System.out.println("SSCheckBox (" + SSCheckBox.this.getColumnName() + ") - changedUpdate(): lastChange + ", lastNotifiedChange=" + lastNotifiedChange);
//			// Delay execution of logic until all listener methods are called for current event
//			// See: https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
//			SwingUtilities.invokeLater(() -> {
//				if (lastNotifiedChange != lastChange) {
//					lastNotifiedChange = lastChange;
//					
//					removeItemListener(SSCheckBox.this.checkBoxListener);
//					updateDisplay();
//					addItemListener( SSCheckBox.this.checkBoxListener );
//				}
//			});
//    	}
//    	
//        @Override
//		public void insertUpdate(DocumentEvent de) {
//        	//System.out.println("SSCheckBox (" + this.columnName + ") - insertUpdate()");
//        	changedUpdate(de);
//        }
//        
//        @Override
//		public void removeUpdate(DocumentEvent de) {
//        	//System.out.println("SSCheckBox (" + this.columnName + ") - removeUpdate()"); 
//        	changedUpdate(de);
//        }
//
//    } // end protected class MyTextFieldDocumentListener implements DocumentListener {

	/**
	 * Adds any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 */
	@Override
	public void addSSComponentListener() {
		addItemListener(ssCheckBoxListener);

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
		// NOTHING TO DO

	}

	/**
	 * Returns the ssCommon data member for the current Swingset component.
	 * 
	 * @return shared/common SwingSet component data & methods
	 */
	@Override
	public SSCommon getSSCommon() {
		return ssCommon;
	}

	/**
	 * Removes any necessary listeners for the current SwingSet component. These
	 * will trigger changes in the underlying RowSet column.
	 */
	@Override
	public void removeSSComponentListener() {
		removeItemListener(ssCheckBoxListener);

	}

	/**
	 * Sets the SSCommon data member for the current Swingset Component.
	 * 
	 * @param _ssCommon shared/common SwingSet component data & methods
	 */
	@Override
	public void setSSCommon(SSCommon _ssCommon) {
		ssCommon = _ssCommon;

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

		// SELECT/DESELECT BASED ON UNDERLYING SQL TYPE
		// switch(this.columnType) {
		switch (getBoundColumnType()) {
		case java.sql.Types.INTEGER:
		case java.sql.Types.SMALLINT:
		case java.sql.Types.TINYINT:
			// SET THE CHECK BOX BASED ON THE VALUE IN ROWSET
			// if (this.textField.getText().equals(String.valueOf(this.CHECKED))) {
			if (getBoundColumnText().equals(String.valueOf(this.CHECKED))) {
				setSelected(true);
			} else {
				setSelected(false);
			}
			break;

		case java.sql.Types.BIT:
		case java.sql.Types.BOOLEAN:
			// SET THE CHECK BOX BASED ON THE VALUE IN TEXT FIELD
			if (getBoundColumnText().equals(BOOLEAN_CHECKED)) {
				setSelected(true);
			} else {
				setSelected(false);
			}
			break;

		default:
			break;
		}

	} // end protected void updateSSComponent() {

} // end public class SSCheckBox