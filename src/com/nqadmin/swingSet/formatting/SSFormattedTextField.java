/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004-2006, The Pangburn Company, Prasanth R. Pasala and
 * Diego Gil
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  The names of its contributors may not be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.nqadmin.swingSet.formatting;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.beans.beancontext.BeanContextProxy;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.RowSetListener;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.datasources.SSRowSet;
import com.nqadmin.swingSet.formatting.helpers.HelperPopup;
import com.nqadmin.swingSet.formatting.helpers.RowSetHelperPopup;
import com.nqadmin.swingSet.formatting.helpers.SSFormattedComboBoxEditor;
import com.nqadmin.swingSet.formatting.helpers.SSFormattedComboBoxModel;


/**
 * SSFormattedTextField.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * SSFormattedTextField extends the JFormattedTextField.
 *</pre><p>
 * @author $Author$
 * @version $Revision$
 */
public class SSFormattedTextField extends JFormattedTextField implements RowSetListener, KeyListener, MouseListener, BeanContextProxy, FocusListener {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 5349618425984728006L;

	private BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport();
    
    private JPopupMenu menu       = null;
    private JPopupMenu calculator = null;
    private JPopupMenu helper     = null;
    
    private java.awt.Color std_color  = null;
    private String columnName         = null;
    private int colType               = -99;
    private SSDataNavigator navigator = null;
    private SSRowSet rowset           = null;
    
    private SSFormattedComboBox combo = null;
    
    /**
     * Holds value of property nullable.
     */
    private boolean nullable = true;
    
    /**
     * Utility field used by bound properties.
     */
    //private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);
    
    
    /** 
     * Creates a new instance of SSFormattedTextField 
     */
    public SSFormattedTextField() {
        super();
        this.setFont(new Font(this.getFont().getName(), Font.BOLD, 11));
        this.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        
        Set<AWTKeyStroke> forwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK ));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,newForwardKeys);
        
        Set<AWTKeyStroke> backwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        Set<AWTKeyStroke> newBackwardKeys = new HashSet<AWTKeyStroke>(backwardKeys);
        newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_MASK ));
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,newBackwardKeys);
        
        /*
         * add this as a self KeyListener      *
         */
        addKeyListener(this);
        
        /*
         * add this as a self FocusListener
         *
         */
        addFocusListener(this);
        
        /*
         * add this as a self MouseListener
         *
         */
        addMouseListener(this);
        
        
        /*
         *
         */
        menu = new JPopupMenu();
        menu.add("Option 1");
        menu.add("Option 2");
        menu.add("Option 3");
        
        /*
         * set InputVerifier. Rowset's updates are handled by this class. Is the preferred method instead of focus change.
         *
         */
        setInputVerifier(new internalVerifier());
    }
    
    /**
     * @param factory
     */
    public SSFormattedTextField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        this();
        this.setFormatterFactory(factory);
    }
    
    /**
     * Sets the column name to which the component should be bound to
     * @param columnName - column name to which the component will be bound to
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
        bind();
    }
    
    /**
     * Returns the column name to which the component is bound to
     * @return - returns the column name to which the component is bound to
     */
    public String getColumnName() {
        return columnName;
    }
    
    /**
      * @deprecated
      * @param combo
     */
    public void setSSFormattedComboBox(SSFormattedComboBox combo) {
        this.combo = combo;
        if (this.combo != null)
            combo.setSelectedIndex(((SSFormattedComboBoxModel)combo.getModel()).indexOf(this.getValue()));
    }
    
    /**
     * @deprecated
     * @return SSFormattedComboBox associated with SSFormattedTextField
     */
    public SSFormattedComboBox getSSFormattedComboBox() {
        return combo;
    }
    
    /**
     * Sets the SSRowSet object to be used to get/set the value of the bound column
     * @param rowset - SSRowSet object to be used to get/set the value of the bound column
     * @deprecated
     * @see #setSSRowSet(SSRowSet)
     */
    public void setRowSet(SSRowSet rowset) {
        this.rowset = rowset;
        bind();
    }
    
    /**
     * Sets the SSRowSet object to be used to get/set the value of the bound column
     * @param rowset - SSRowSet object to be used to get/set the value of the bound column
     */
    public void setSSRowSet(SSRowSet rowset) {
        this.rowset = rowset;
        bind();
    }
    
    /**
     * SSRowSet object being used to get/set the bound column value
     * @return - returns the SSRowSet object being used to get/set the bound column value
     */
    public SSRowSet getSSRowSet() {
        return rowset;
    }
    
    /**
     * Sets the SSDataNavigator being used to navigate the SSRowSet
     * This is needed only if you want to include the function keys as short cuts to perform operations on the DataNavigator
     * like saving the current row/ undo changes/ delete current row.
     * <font color=red>The functionality for this is not yet finalized so try to avoid using this </font>
     * @param navigator - SSDataNavigator being used to navigate the SSRowSet   
     * @deprecated
     * @see #setSSDataNavigator(SSDataNavigator) 
     */
    public void setNavigator(SSDataNavigator navigator) {
        this.setSSDataNavigator(navigator);
    }
    
    /**
     * Returns the SSDataNavigator object being used.
     * @return returns the SSDataNavigator object being used.
     * @deprecated
     * @see #getSSDataNavigator()
     **/
    public SSDataNavigator getNavigator() {
        return this.getSSDataNavigator();
    }
    
    /**
     * Sets the SSDataNavigator being used to navigate the SSRowSet
     * This is needed only if you want to include the function keys as short cuts to perform operations on the DataNavigator
     * like saving the current row/ undo changes/ delete current row.
     * <font color=red>The functionality for this is not yet finalized so try to avoid using this </font>
     * @param navigator - SSDataNavigator being used to navigate the SSRowSet
     */
    public void setSSDataNavigator(SSDataNavigator navigator) {
        this.navigator = navigator;
        setSSRowSet(navigator.getSSRowSet());
        bind();
    }
    
    /**
     * Returns the SSDataNavigator object being used.
     * @return returns the SSDataNavigator object being used.
     */
    public SSDataNavigator getSSDataNavigator() {
        return this.navigator;
    }

    /**
     * Sets the SSRowSet and column name to which the component is to be bound.
     * @param _sSRowSet    datasource to be used.
     * @param _columnName  Name of the column to which this check box should be bound
     */
    public void bind(SSRowSet _sSRowSet, String _columnName) {
        rowset = _sSRowSet;
        columnName = _columnName;
        bind();
    }
    
    /**
     * Binds the components to the specified column in the given rowset.
     */
    private void bind() {
        
        if (columnName == null || rowset  == null) return;
        
        try {
            colType = rowset.getColumnType(columnName);
        } catch(java.sql.SQLException sqe) {
            System.out.println("bind error = " + sqe);
        }
        rowset.addRowSetListener(this);
        DbToFm("bind");
    }
    
    /* (non-Javadoc)
     * @see javax.sql.RowSetListener#rowSetChanged(javax.sql.RowSetEvent)
     */
    public void rowSetChanged(javax.sql.RowSetEvent event) {
        //System.out.println("rowSetChanged" + event.getSource());
    }
    
    /* (non-Javadoc)
     * @see javax.sql.RowSetListener#rowChanged(javax.sql.RowSetEvent)
     */
    public void rowChanged(javax.sql.RowSetEvent event) {
        //System.out.println("rowChanged " + event.getSource());
    }
    
    /* (non-Javadoc)
     * @see javax.sql.RowSetListener#cursorMoved(javax.sql.RowSetEvent)
     */
    public void cursorMoved(javax.sql.RowSetEvent event) {
        //System.out.println("cursorMoved " + event.getSource());
        DbToFm("cursorMoved");
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent e) {
    }
    
    /**
     *  Catch severals keys, to implement some forms functionality (To be done).
     *  This is to mimic console legacy systems behavior.
     *
     */
    public void keyPressed(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_F1) {
            showHelper(e);
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F2) {
            
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F3) {
            calculator = new javax.swing.JPopupMenu();
            //calculator.add(new com.nqadmin.swingSet.formatting.utils.JCalculator());
            JFormattedTextField ob = (JFormattedTextField)(e.getSource());
            java.awt.Dimension d = ob.getSize();
            calculator.show(ob, 0, d.height);
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F4) {
            System.out.println("F4 ");
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            System.out.println("F5 = COMMIT");
            navigator.doCommitButtonClick();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F6) {
            System.out.println("F6 = DELETE");
            navigator.doDeleteButtonClick();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F8) {
            System.out.println("F8 = UNDO");
            navigator.doUndoButtonClick();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_END) {
            System.out.println("END ");
        }
        
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            System.out.println("DELETE ");
        }
        
        if (e.getKeyCode() == KeyEvent.VK_HOME) {
            System.out.println("HOME ");
        }
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        /**
         * some code to highlight the component with the focus
         *
         */
        setBackground(std_color);
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e) {
        
        /**
         * some code to highlight the component with the focus
         *
         */
        java.awt.Color col = new java.awt.Color(204,255,255);
        std_color = getBackground();
        setBackground(col);
        
        
        /**
         * This is a bug workaround
         * see : http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4740914
         *
         */
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                selectAll();
            }
        });
    }
    
    /**
     * This method perform the actual data transfer from rowset to this object Value field.
     * depending on the column Type.
     */
    
    private void DbToFm(String texto) {
        Object oValue = null;
        Object nValue = null;
        int nrow = 0;
        
        try {
            nrow = rowset.getRow();
        } catch (SQLException s3) {
            System.out.println(s3);
        }
        
        if (nrow == 0) return;
        
        try {
        	// IF THE COLUMN VALUE IS NULL SET THE FIELD TO NULL AND RETURN
            if(rowset.getObject(columnName) == null){
            	super.setValue(null);
            	return;
            }
            
            switch(colType) {
                
                case java.sql.Types.ARRAY://2003A
                    System.out.println("ARRAY not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.BINARY://-2
                    System.out.println("BINARY not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.BIT    ://-7
                    //System.out.println("BIT implemented as Boolean --> " + columnName);
                    nValue = new Boolean(rowset.getBoolean(columnName));
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.BOOLEAN://16
                    //System.out.println("BOOLEAN implemented as Boolean --> " + columnName);
                    nValue = new Boolean(rowset.getBoolean(columnName));
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.BLOB://2004
                    System.out.println("BLOB not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.CLOB://2005
                    System.out.println("CLOB not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.DATALINK://70
                    System.out.println("DATALINK not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.DATE://91
                    //System.out.println("DATE implemented as java.util.Date --> " + columnName);
                	Date date = rowset.getDate(columnName);
                	if(date != null){
                		nValue = new java.util.Date(rowset.getDate(columnName).getTime());
                		super.setValue(nValue);
                	}
                	else{
                		super.setValue(null);
                	}
                    break;
                    
                case java.sql.Types.DECIMAL://3
                    //System.out.println("DECIMAL implemented as BigDecimal --> " + columnName);
                    nValue = new java.math.BigDecimal(rowset.getDouble(columnName));
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.DISTINCT://2001
                    System.out.println("DISTINCT not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.DOUBLE://8
                    //System.out.println("DOUBLE implemented as Double --> " + columnName);
                    nValue = new Double(rowset.getDouble(columnName));
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.REAL: //7
                    //System.out.println("REAL implemented as Float --> " + columnName);
                    nValue = new Float(rowset.getFloat(columnName));
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.FLOAT://6
                    //System.out.println("FLOAT implemented as Float --> " + columnName);
                    nValue = new Float(rowset.getFloat(columnName));
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.TINYINT ://-6
                    //System.out.println("TINYINT implemented as Integer --> " + columnName);
                    nValue = new Integer(rowset.getInt(columnName));
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.SMALLINT://5
                    //System.out.println("SMALLINT implemented as Integer --> " + columnName);
                    nValue = new Integer(rowset.getInt(columnName));
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.INTEGER ://4
                    //System.out.println("INTEGER implemented as Integer --> " + columnName);
                    nValue = new Integer(rowset.getInt(columnName));
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.BIGINT  ://-5
                    //System.out.println("BIGINT implemented as Integer --> " + columnName);
                    nValue = new Integer(rowset.getInt(columnName));
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.JAVA_OBJECT://2000
                    System.out.println("JAVA_OBJECT not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.LONGVARBINARY://-4
                    System.out.println("LONGVARBINARY not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.VARBINARY://-3
                    System.out.println("VARBINARY not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.VARCHAR://
                    //System.out.println("VARCHAR implemented as String --> " + columnName);
                    nValue = rowset.getString(columnName);
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.LONGVARCHAR://-1
                    //System.out.println("LONGVARCHAR implemented as String --> " + columnName);
                    nValue = rowset.getString(columnName);
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.CHAR://1
                    //System.out.println("CHAR implemented as String --> " + columnName);
                    nValue = rowset.getString(columnName);
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.NULL://0
                    System.out.println("NULL not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.NUMERIC://2
                    //System.out.println("NUMERIC implemented as BigDecimal --> " + columnName);
                    nValue = new java.math.BigDecimal(rowset.getDouble(columnName));
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.OTHER://1111
                    System.out.println("OTHER not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.REF://2006
                    System.out.println("REF not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.STRUCT://2002
                    System.out.println("STRUCT not implemented : " + columnName);
                    break;
                    
                case java.sql.Types.TIME://92
                    //System.out.println("TIME implemented as java.util.Date --> " + columnName);
                    //nValue = new java.util.Date(rowset.getTime(columnName).getTime());
                    nValue = rowset.getTime(columnName);
                    super.setValue(nValue);
                    break;
                    
                case java.sql.Types.TIMESTAMP://93
                    //System.out.println("TIMESTAMP implemented as java.util.Date --> " + columnName);
//                    nValue = new java.util.Date(rowset.getTimestamp(columnName).getTime());
                    nValue = rowset.getTimestamp(columnName);
                    super.setValue(nValue);
                    break;
                    
                default:
                    System.out.println("UNKNOWN Type not implemented (" + colType +  ")");
                    break;
            }
        } catch (java.sql.SQLException sqe) {
            System.out.println("Error in DbToFm() = " + sqe);
            sqe.printStackTrace();
            super.setValue(null);
        }
        
        if ( (nValue instanceof Double  && ((Double)  nValue).doubleValue() < 0.0) ||
                (nValue instanceof Float   && ((Float)   nValue).floatValue()  < 0.0) ||
                (nValue instanceof Long    && ((Long)    nValue).intValue()    < 0)   ||
                (nValue instanceof Integer && ((Integer) nValue).longValue()   < 0) ) {
            this.setForeground(Color.RED);
        } else {
            this.setForeground(Color.BLACK);
        }
        
        if (combo != null) {
            combo.setSelectedIndex(((SSFormattedComboBoxModel)combo.getModel()).indexOf(nValue));
        }
        
        this.firePropertyChange("value", oValue, nValue);
        oValue = nValue;
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
        //System.out.println("mouseExited");
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
        //System.out.println("mouseEntered");
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        //System.out.println("mouseClicked");
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            menu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            menu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }
    
    /**
     * Sets the HelperPopup to be used.
     * @param helper - HelperPopup to be used.
     */
    public void setHelper(JPopupMenu helper) {
        this.helper = helper;
        
        if (helper instanceof HelperPopup)
            ((HelperPopup)this.helper).setTarget(this);
        
        if (helper instanceof RowSetHelperPopup)
            ((RowSetHelperPopup)this.helper).setTarget(this);
        
    }
    
    /**
     * Displays the HelperPopup screen.
     * @param e - the key event which triggered the helper popup
     */
    public void showHelper(KeyEvent e) {
        if (helper == null) return;
        
        JFormattedTextField ob = (JFormattedTextField)(e.getSource());
        java.awt.Dimension d = ob.getSize();
        helper.requestFocusInWindow();
        helper.show(ob, 0, d.height);
    }
    
    /**
     * This method should implements validation AND, most important for our purposes
     * implements actual rowset's fields updates.
     *
     */
    
    class internalVerifier extends InputVerifier {
        
        public boolean verify(JComponent input) {
            
            SSFormattedTextField tf = null;
            Object aux = null;
            //boolean passed = true;
            
            /**
             * field to be validated and updated
             */
            
            if (input instanceof SSFormattedTextField) {
                tf = (SSFormattedTextField) input;
            } else if (input instanceof SSFormattedComboBoxEditor){
                tf = ((SSFormattedComboBoxEditor)input).getEditorField() ;
            } else if (input instanceof SSFormattedComboBox){
                tf = ( ( (SSFormattedComboBoxEditor) ((SSFormattedComboBox)input).getEditor())).getEditorField();
            }
            
            try {
                tf.commitEdit();
            } catch (java.text.ParseException pe) {
                pe.printStackTrace();
            	System.out.println("inputVerifier --> ParseException  POSITION:" + pe.getErrorOffset());
                tf.setValue(null);
                setBackground(java.awt.Color.RED);
                return false;
            }
           
            aux = tf.getValue();
            return updateFieldValue(aux);

        }
    }
    
    
    /**
     * This function has been deprecated, use setValue to set the value in database. This is done to reduce the confusion of when to use setValue and when to use updateValue.
     * Before this change setValue is a function in JFormattedText field and it would not set the value in database, so we have overridden this function in
     * this class to update database and then update the display.
     * 
     * Sets the value of the field to the specified value
     * @param value - The value to be set for this component (this will also update the underlying column value)
     * @return returns true if update is successful else false
     */
    @Deprecated
    public boolean updateValue(Object value){
    	if(updateFieldValue(value)){
    		super.setValue(value);
    		return true;
    	}
    	return false;
    }
    
    
    /**
     * Sets the value of the field to the specified value
     * @param value - The value to be set for this component (this will also update the underlying column value)
     */
    public void setValue(Object value){
    	if(updateFieldValue(value)){
    		super.setValue(value);
    	}
    }
    

    
    /**
     * Updates the value of the componenet
     * @param aux - value with whcih the component should be updated
     * @return returns true upon successful update else false
     */
    private boolean updateFieldValue(Object aux){
    	
    	/**
         * field to be validated and updated
         */
        
    	SSFormattedTextField tf = this;
        
        boolean passed = validateField(aux);
        
        if (passed == true) {
            
            setBackground(java.awt.Color.WHITE);
            
            
            if (columnName == null) return true;
            if (colType    == -99 ) return true;
            if (rowset     == null) return true;
            
            try {
                
                rowset.removeRowSetListener(tf);
                if (aux == null) {
                    rowset.updateNull(columnName);
                    rowset.addRowSetListener(tf);
                    tf.firePropertyChange("value", null, aux);
                    return true;
                }
                
                updateRowSet(aux);
                
           // SHOW NEGATIVE NUMBERS IN RED     
                if (    (aux instanceof BigDecimal  && ((Double)  aux).doubleValue() < 0.0) ||
                        (aux instanceof Double      && ((Double)  aux).doubleValue() < 0.0) ||
                        (aux instanceof Float       && ((Float)   aux).floatValue() < 0.0) ||
                        (aux instanceof Integer     && ((Integer) aux).intValue() < 0) ||
                        (aux instanceof Long        && ((Long)    aux).longValue() < 0)  ) {
                    tf.setForeground(Color.RED);
                } else {
                    tf.setForeground(Color.BLACK);
                }
                
            } catch (java.sql.SQLException se) {
                System.out.println("---> SQLException -----------> " + se);
                super.setValue(null);
            } catch(java.lang.NullPointerException np) {
                System.out.println("<---> NullPointerException <---> " + np + " columnName : " + columnName);
                super.setValue(null);
            }
            
            rowset.addRowSetListener(tf);
            System.out.println("inputVerifier : " + columnName + " nValue = " + aux);
            tf.firePropertyChange("value", null, aux);
            return true;
            
        } else {
            /*
             * Validation fails.
             *
             */
            
            setBackground(java.awt.Color.RED);
            return false;
        }
    	
    }
    
    /**
     * Updates the bound column in the rowset with the specified value
     * @param aux - value with which the rowset column has to be updated
     * @throws SQLException
     */
    private void updateRowSet(Object aux) throws SQLException{
        switch(colType) {
            
            case java.sql.Types.ARRAY://2003
                break;
                
            case java.sql.Types.BINARY://-2
                break;
                
            case java.sql.Types.BIT://-7
                rowset.updateBoolean(columnName, ((Boolean)aux).booleanValue());
                break;
                
            case java.sql.Types.BLOB://2004
                break;
                
            case java.sql.Types.BOOLEAN://16
                rowset.updateBoolean(columnName, ((Boolean)aux).booleanValue());
                break;
                
            case java.sql.Types.CLOB://2005
                break;
                
            case java.sql.Types.DATALINK://70
                break;
                
            case java.sql.Types.DATE://91
                rowset.updateDate(columnName, new java.sql.Date(((java.util.Date) aux).getTime()));
                break;
                
            case java.sql.Types.DECIMAL://3
            case java.sql.Types.NUMERIC:
            case java.sql.Types.BIGINT:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.FLOAT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.REAL:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.TINYINT:
                if (aux instanceof java.math.BigDecimal) {
                    rowset.updateDouble(columnName, ((Double)aux).doubleValue());
                } else if (aux instanceof Double) {
                    rowset.updateDouble(columnName, ((Double)aux).doubleValue());
                } else if (aux instanceof Float) {
                    rowset.updateFloat(columnName, ((Float)aux).floatValue());
                } else if (aux instanceof Integer) {
                    rowset.updateInt(columnName, ((Integer)aux).intValue());
                } else if (aux instanceof Long) {
                    rowset.updateLong(columnName, ((Long)aux).longValue());
                } else {
                    System.out.println("Value aux is of unknown type......unable to update database");
                }
                
                break;
                
            case java.sql.Types.DISTINCT://2001
                break;
                
            case java.sql.Types.JAVA_OBJECT://2000
                break;
                
            case java.sql.Types.LONGVARBINARY://-4
            case java.sql.Types.VARBINARY://-3
                break;
                
            case java.sql.Types.VARCHAR://
                System.out.println("VARCHAR --> updateString()");
                rowset.updateString(columnName, aux.toString());
                break;
                
            case java.sql.Types.LONGVARCHAR://-1
                System.out.println("LONGVARCHAR --> updateString()");
                rowset.updateString(columnName, aux.toString());
                break;
                
            case java.sql.Types.CHAR://1
                System.out.println("CHAR --> updateString()");
                rowset.updateString(columnName, aux.toString());
                break;
                
            case java.sql.Types.NULL://0
                break;
                
            case java.sql.Types.OTHER://1111
                break;
                
            case java.sql.Types.REF://2006
                break;
                
            case java.sql.Types.STRUCT://2002
                break;
                
            case java.sql.Types.TIME://92
                System.out.println("TIME --> updateTime()");
                System.out.println("TIME : " + aux.getClass().getName());
                rowset.updateTime(columnName, new java.sql.Time(((java.util.Date) aux).getTime()));
                break;
                
            case java.sql.Types.TIMESTAMP://93
                System.out.println("TIMESTAMP --> updateTimestamp()");
                System.out.println("TIMESTAMP : " + aux.getClass().getName());
                rowset.updateTimestamp(columnName, new java.sql.Timestamp(((java.util.Date) aux).getTime()));
                break;
                
            default:
                System.out.println("============================================================================");
            	System.out.println("Unknown column type");
                System.out.println("default = " + colType);
                System.out.println("columnName = " + columnName);
                System.out.println("============================================================================");
                break;
        }
    }
  
    /* (non-Javadoc)
     * @see java.beans.beancontext.BeanContextProxy#getBeanContextProxy()
     */
    public BeanContextChild getBeanContextProxy(){
        return beanContextChildSupport;
    }
    
   /*    
    protected void processFocusEvent(FocusEvent e) {
        System.out.println("processFocusEvent()" + e);
    
        if ( e.getID() == FocusEvent.FOCUS_LOST ) {
            System.out.println("FOCUS_LOST");
            try {
                if ( this.getText().length() == 0 ) {
                    setValue(null);
                }
            } catch (NullPointerException npe) {
                System.out.println("processFocusEvent() --> NullPointerException");
            }
        }
        super.processFocusEvent(e);
    }
    */
    
 
    /* (non-Javadoc)
     * @see javax.swing.JFormattedTextField#commitEdit()
     */
    public void commitEdit() throws ParseException {
        
        if (getText()==null || getText().length()==0){
            super.setValue(null);
        } 
        else{
        	super.commitEdit();
        }        
    }
    
    // this method can be override to make custom validation.
    /**
     * Checks if the value is valid of the component
     * @param value - value to be validated
     * @return returns true if the value is valid else false
     */
    public boolean validateField(Object value) {
        
        if (nullable == false && value == null)
            return false;
        else
            return true;
    }
    
    /**
     * Getter for property nullable.
     * @return Value of property nullable.
     */
    public boolean isNullable() {
        
        return this.nullable;
    }
    
    /**
     * Setter for property nullable.
     * @param nullable New value of property nullable.
     */
    public void setNullable(boolean nullable) {
        
        boolean oldNullable = this.nullable;
        this.nullable = nullable;
        this.firePropertyChange("nullable", new Boolean(oldNullable), new Boolean(nullable));
    }
    
    /* (non-Javadoc)
     * @see com.nqadmin.swingSet.formatting.SSField#cleanField()
     */
    public void cleanField() {
        super.setValue(null);
    }
}

/*
 * $Log$
 * Revision 1.25  2010/03/17 14:47:59  prasanth
 * Deprecated updateValue and added setValue.
 * When the underlying value is null setting value of null to the component.
 *
 * Revision 1.24  2006/05/15 15:51:46  prasanth
 * Removed implementation of SSField
 *
 * Revision 1.23  2006/04/27 22:02:45  prasanth
 * Added/updated java doc
 *
 * Revision 1.22  2006/03/28 16:12:28  prasanth
 * Added functions to provide the ability to set value programatically that works with the formatter.
 *
 * Revision 1.21  2005/05/29 02:24:37  dags
 * SSConnection and SSRowSet getters and setter refactoring
 *
 * Revision 1.20  2005/05/27 00:37:11  dags
 * added setValue(null) on exceptions
 *
 * Revision 1.19  2005/05/26 22:20:36  dags
 * SSField interface implemented
 *
 * Revision 1.18  2005/05/26 12:12:36  dags
 * added bind(SSRowSet, columnName) method and some java.sql.Types checking and support
 *
 * Revision 1.17  2005/05/23 22:10:23  dags
 * Fix for numeric fields
 *
 * Revision 1.16  2005/05/11 17:25:56  dags
 * several modifications to allow SSFormattedComboBox work as expected
 *
 * Revision 1.15  2005/03/30 13:03:51  dags
 * Accept null dates values
 *
 * Revision 1.14  2005/03/28 14:46:42  dags
 * syncro commit
 *
 * Revision 1.13  2005/03/21 20:09:37  dags
 *
 *  Removed Files:
 *  	SSFormattedTextFieldColumnNamePropertyEditor.java
 *  	SSFormattedTextFieldCustomizer.form
 *  	SSFormattedTextFieldCustomizer.java
 *  ----------------------------------------------------------------------
 *
 * Revision 1.12  2005/02/22 15:14:34  yoda2
 * Fixed some JavaDoc & deprecation errors/warnings.
 *
 * Revision 1.11  2005/02/04 22:42:06  yoda2
 * Updated Copyright info.
 *
 * Revision 1.10  2005/01/24 14:04:16  dags
 * bean preparation
 *
 * Revision 1.9  2005/01/19 19:33:50  dags
 * bind refactoring
 *
 * Revision 1.8  2005/01/19 19:12:40  dags
 * bind refactoring
 *
 * Revision 1.7  2005/01/18 22:43:08  dags
 * Eliminate JCalculator test code, due to licence issues
 *
 * Revision 1.6  2005/01/18 22:36:55  dags
 * Helpers refactoring
 *
 * Revision 1.5  2005/01/18 22:34:30  dags
 * sincronization update
 *
 * Revision 1.4  2004/12/13 20:58:49  dags
 * Added some javadoc tags
 *
 * Revision 1.3  2004/12/13 20:50:16  dags
 * Fix package name
 *
 * Revision 1.2  2004/12/13 18:46:13  prasanth
 * Added License.
 *
 */
