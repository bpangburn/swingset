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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.JLabel;

import com.nqadmin.swingset.datasources.SSRowSet;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

/**
 * SSLabel.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Used to display database values in a read-only JLabel.
 */
public class SSLabel extends JLabel implements SSComponentInterface {

    /**
     * Listener(s) for the component's value used to propagate changes back to
     * bound database column.
     * 
     * There is not an obvious use-case where a label would change, but could be 
     * tied to a menu, screen logic, or some other Developer driven change that could
     * conceivably need to be synchronized back to the RowSet.
     */
    protected class SSLabelListener implements PropertyChangeListener, Serializable {
        /**
		 * unique serial ID
		 */
		private static final long serialVersionUID = 6786673052979566820L;
		
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			
			// CONFIRM THE PROPERTY NAME IN CASE SOMEONE ADDS A DIFFERENT PROPERTY LISTENER TO ssLabelListener
			if (pce.getPropertyName()=="text") {
			
				removeSSRowSetListener();
				
				setBoundColumnText(getText());
				
				addSSRowSetListener();
			}
			
		}

    } // end protected class SSLabelListener
	
    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = -5232780793538061537L;

//	/**
//     * Text field bound to the SSRowSet.
//     */
//    protected JTextField textField = new JTextField();
//
//    /**
//     * SSRowSet from which component will get/set values.
//     */
//    protected SSRowSet sSRowSet;
//
//    /**
//     * SSRowSet column to which the component will be bound.
//     */
//    protected String columnName = "";

    /**
     * Common fields shared across SwingSet components
     */
    protected SSCommon ssCommon;

//    /**
//     * Bound text field document listener.
//     */
//    protected final MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();

    /**
     * Component listener.
     */
    protected final SSLabelListener ssLabelListener = new SSLabelListener();

    /**
     * Empty constructor needed for deserialization. Creates a SSLabel instance
     * with no image and with an empty string for the title.
     */
    public SSLabel() {
        super("<label text here>");
        setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
        //init();
    }

    /**
     * Creates a SSLabel instance with the specified image.
     *
     * @param _image    specified image for label
     */
    public SSLabel(Icon _image) {
		super(_image);
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
        //init();
    }

    /**
     * Creates a SSLabel instance with the specified image and horizontal alignment.
     *
     * @param _image    specified image for label
     * @param _horizontalAlignment	horizontal alignment
     */
    public SSLabel(Icon _image, int _horizontalAlignment) {
		super(_image, _horizontalAlignment);
		setSSCommon(new SSCommon(this));
        //init();
    }

//    /**
//     * Sets the SSRowSet column name to which the component is bound.
//     *
//     * @param _columnName    column name in the SSRowSet to which the component
//     *    is bound
//     */
//    public void setColumnName(String _columnName) {
//        String oldValue = this.columnName;
//        this.columnName = _columnName;
//        firePropertyChange("columnName", oldValue, this.columnName);
//        bind();
//    }

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
//     */
//    public void setSSRowSet(SSRowSet _sSRowSet) {
//        SSRowSet oldValue = this.sSRowSet;
//        this.sSRowSet = _sSRowSet;
//        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);
//        bind();
//    }

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
     * Creates a SSLabel instance with no image and binds it to the specified
     * SSRowSet column.
     *
     * @param _ssRowSet    datasource to be used.
     * @param _boundColumnName    name of the column to which this label should be bound
     */
    public SSLabel(SSRowSet _ssRowSet, String _boundColumnName) {
		super();
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
		bind(_ssRowSet, _boundColumnName);
        //this.sSRowSet = _sSRowSet;
        //this.columnName = _columnName;
        //bind();
    }

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
//            
//        // BIND AND UPDATE DISPLAY
//            try {
//
//	        // BIND THE TEXT FIELD TO THE SPECIFIED COLUMN
//	            this.textField.setDocument(new SSTextDocument(this.sSRowSet, this.columnName));
//	
//	        // SET THE LABEL DISPLAY
//	            updateDisplay();
//	            
//            } finally {
//	        // ADD BACK LISTENERS
//	            addListeners();
//            }
//
//    }


//    /**
//     * Updates the value displayed in the component based on the SSRowSet column
//     * binding.
//     */
//    
//    public void updateDisplay() {
//
//        // SET THE LABEL BASED ON THE VALUE IN THE TEXT FIELD
//            //setText(this.textField.getText());
//    	
//    } // end protected void updateDisplay() {

//    /**
//     * Adds listeners for component and bound text field (where applicable).
//     */
//    private void addListeners() {
//        this.textField.getDocument().addDocumentListener(this.textFieldDocumentListener);
//        addPropertyChangeListener("text", this.labelTextListener);
//    }
//
//    /**
//     * Removes listeners for component and bound text field (where applicable).
//     */
//    private void removeListeners() {
//        this.textField.getDocument().removeDocumentListener(this.textFieldDocumentListener);
//        removePropertyChangeListener("text", this.labelTextListener);
//    }

//    /**
//     * Listener(s) for the bound text field used to propigate values back to the
//     * component's value.
//     */
//    protected class MyTextFieldDocumentListener implements DocumentListener, Serializable {
//    	
//        /**
//		 * unique serial id
//		 */
//		private static final long serialVersionUID = -6911906045174819801L;
//
//		@Override
//		public void changedUpdate(DocumentEvent de) {
//            removePropertyChangeListener("text", SSLabel.this.labelTextListener);
//
//            updateDisplay();
//
//            addPropertyChangeListener("text", SSLabel.this.labelTextListener);
//        }
//
//        // WHEN EVER THERE IS A CHANGE IN THE VALUE IN THE TEXT FIELD CHANGE THE LABEL
//        // ACCORDINGLY.
//        @Override
//		public void insertUpdate(DocumentEvent de) {
//            removePropertyChangeListener("text", SSLabel.this.labelTextListener);
//
//            updateDisplay();
//
//            addPropertyChangeListener("text", SSLabel.this.labelTextListener);
//        }
//
//        // IF A REMOVE UPDATE OCCURS ON THE TEXT FIELD CHECK THE CHANGE AND SET THE
//        // CHECK BOX ACCORDINGLY.
//        @Override
//		public void removeUpdate(DocumentEvent de) {
//            removePropertyChangeListener("text", SSLabel.this.labelTextListener);
//
//            updateDisplay();
//
//            addPropertyChangeListener("text", SSLabel.this.labelTextListener);
//        }
//    } // end protected class MyTextFieldDocumentListener implements DocumentListener, Serializable {
    
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
//    // TODO audit which listeners need to be removed in changedUpdate()
//
//    protected class MyTextFieldDocumentListener implements DocumentListener, Serializable {
//    	
//    	/**
//		 * unique serial id
//		 */
//		private static final long serialVersionUID = -6911906045174819801L;
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
//			//System.out.println("SSLabel (" + SSLabel.this.getColumnName() + ") - changedUpdate(): lastChange=" + lastChange + ", lastNotifiedChange=" + lastNotifiedChange);
//			// Delay execution of logic until all listener methods are called for current event
//			// See: https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
//			SwingUtilities.invokeLater(() -> {
//				if (lastNotifiedChange != lastChange) {
//					lastNotifiedChange = lastChange;
//					
//					removePropertyChangeListener("text", SSLabel.this.labelTextListener);
//					updateDisplay();
//					addPropertyChangeListener("text", SSLabel.this.labelTextListener);
//				}
//			});
//    	}
//    	
//        @Override
//		public void insertUpdate(DocumentEvent de) {
//        	//System.out.println("SSLabel (" + this.columnName + ") - insertUpdate()");
//        	changedUpdate(de);
//        }
//        
//        @Override
//		public void removeUpdate(DocumentEvent de) {
//        	//System.out.println("SSLabel (" + this.columnName + ") - removeUpdate()"); 
//        	changedUpdate(de);
//        }
//
//    } // end protected class MyTextFieldDocumentListener implements DocumentListener {

	/**
	 * Adds any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 * 
	 * Generally an SSLabel will be read-only, but Developer could change the text
	 * so we'll support a property listener.
	 */
	@Override
	public void addSSComponentListener() {
		addPropertyChangeListener("text", ssLabelListener);
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
            setPreferredSize(new Dimension(200,20));
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
	 * Removes any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 * 
	 * Generally an SSLabel will be read-only, but Developer could change the text so
	 * we'll support a property listener.
	 */
	@Override
	public void removeSSComponentListener() {
		removePropertyChangeListener("text", ssLabelListener);
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
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText()
	 * 
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed
	 */
	@Override
	public void updateSSComponent() {
		this.setText(getBoundColumnText());
	}

} // end public class SSLabel extends JLabel {
