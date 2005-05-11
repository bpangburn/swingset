/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004-2005, The Pangburn Company, Prasanth R. Pasala and
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

import com.nqadmin.swingSet.datasources.SSRowSet;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.sql.RowSetListener;
import javax.swing.JFormattedTextField;
import java.util.Set;
import java.util.HashSet;

import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.formatting.helpers.*;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.beans.beancontext.BeanContextProxy;
import java.text.ParseException;


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
    
    
    /** Creates a new instance of SSFormattedTextField */
    public SSFormattedTextField() {
        super();
        this.setFont(new Font(this.getFont().getName(), Font.BOLD, 11));
        this.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        
        /*
         *
         *
         */
        
        Set forwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set newForwardKeys = new HashSet(forwardKeys);
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK ));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,newForwardKeys);
        
        Set backwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        Set newBackwardKeys = new HashSet(backwardKeys);
        newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_MASK ));
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,newBackwardKeys);
        
        /*
         * add this as a self KeyListener
         *
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
        menu.add("Opcion 1");
        menu.add("Opcion 2");
        menu.add("Opcion 3");
        
        /*
         * set InputVerifier. Rowset's updates are handled by this class. Is the preferred method instead of focus change.
         *
         */
        setInputVerifier(new internalVerifier());
    }
    
    public SSFormattedTextField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        this();
        this.setFormatterFactory(factory);
    }
    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
        bind();
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    public void setSSFormattedComboBox(SSFormattedComboBox combo) {
        this.combo = combo;
        if (this.combo != null)
            combo.setSelectedIndex(((SSFormattedComboBoxModel)combo.getModel()).indexOf(this.getValue()));
    }
    
    public SSFormattedComboBox getSSFormattedComboBox() {
        return combo;
    }
    
    /**
     *
     * @deprecated
     * @see #setSSRowSet
     *
     */
    public void setRowSet(SSRowSet rowset) {
        this.rowset = rowset;
        bind();
    }
    
    public void setSSRowSet(SSRowSet rowset) {
        this.rowset = rowset;
        bind();
    }
    
    public SSRowSet getSSRowSet() {
        return rowset;
    }
    
    public void setNavigator(SSDataNavigator navigator) {
        this.navigator = navigator;
        setSSRowSet(navigator.getSSRowSet());
        bind();
    }
    
    public SSDataNavigator getNavigator() {
        return this.navigator;
    }
    
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
    
    public void rowSetChanged(javax.sql.RowSetEvent event) {
        //System.out.println("rowSetChanged" + event.getSource());
    }
    
    public void rowChanged(javax.sql.RowSetEvent event) {
        //System.out.println("rowChanged " + event.getSource());
    }
    
    public void cursorMoved(javax.sql.RowSetEvent event) {
        //System.out.println("cursorMoved " + event.getSource());
        DbToFm("cursorMoved");
    }
    
    public void keyTyped(KeyEvent e) {
    }
    
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
            
            System.out.println("F3 ");
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
            System.out.println("F5 = PROCESS");
            navigator.doCommitButtonClick();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F6) {
            System.out.println("F6 = DELETE");
            navigator.doDeleteButtonClick();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F8) {
            System.out.println("F8 ");
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
    
    public void focusLost(FocusEvent e) {
        /**
         * some code to highlight the component with the focus
         *
         */
        setBackground(std_color);
    }
    
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
     *
     */
    
    private void DbToFm(String texto) {
        Object oValue = null;
        Object nValue = null;
        
        //System.out.println("DbToFm.texto = " + texto);
        try {
            
            switch(colType) {
                
                case java.sql.Types.ARRAY://2003
                    break;
                    
                case java.sql.Types.BINARY://-2
                    break;
                    
                case java.sql.Types.BIT://-7
                case java.sql.Types.BOOLEAN://16
                    nValue = new Boolean(rowset.getBoolean(columnName));
                    this.setValue(nValue);
                    break;
                    
                case java.sql.Types.BLOB://2004
                    break;
                    
                case java.sql.Types.CLOB://2005
                    break;
                    
                case java.sql.Types.DATALINK://70
                    break;
                    
                case java.sql.Types.DATE://91
                    nValue = new java.util.Date(rowset.getDate(columnName).getTime());
                    this.setValue(nValue);
                    break;
                    
                case java.sql.Types.DECIMAL://3
                    break;
                    
                case java.sql.Types.DISTINCT://2001
                    break;
                    
                case java.sql.Types.DOUBLE://8
                    nValue = new Double(rowset.getFloat(columnName));
                    this.setValue(nValue);
                    break;
                    
                case java.sql.Types.FLOAT://6
                    nValue = new Float(rowset.getFloat(columnName));
                    this.setValue(nValue);
                    break;
                    
                case java.sql.Types.INTEGER://4
                case java.sql.Types.BIGINT://-5
                case java.sql.Types.SMALLINT://5
                case java.sql.Types.TINYINT://-6
                    nValue = new Integer(rowset.getInt(columnName));
                    this.setValue(nValue);
                    break;
                    
                case java.sql.Types.JAVA_OBJECT://2000
                    break;
                    
                case java.sql.Types.LONGVARBINARY://-4
                case java.sql.Types.VARBINARY://-3
                    break;
                    
                case java.sql.Types.VARCHAR://
                case java.sql.Types.LONGVARCHAR://-1
                case java.sql.Types.CHAR://1
                    nValue = rowset.getString(columnName);
                    this.setValue(nValue);
                    break;
                    
                case java.sql.Types.NULL://0
                    break;
                    
                case java.sql.Types.NUMERIC://2
                    break;
                    
                case java.sql.Types.OTHER://1111
                    break;
                    
                case java.sql.Types.REAL://7
                    break;
                    
                case java.sql.Types.REF://2006
                    break;
                    
                case java.sql.Types.STRUCT://2002
                    break;
                    
                case java.sql.Types.TIME://92
                    break;
                    
                case java.sql.Types.TIMESTAMP://93
                    break;
                    
                default:
                    break;
            }
        } catch (java.sql.SQLException sqe) {
            System.out.println("Error in DbToFm() = " + sqe);
        }
        
        //System.out.println("nValue = " + nValue);
        
        if (combo != null) {
            combo.setSelectedIndex(((SSFormattedComboBoxModel)combo.getModel()).indexOf(nValue));
        }
        
        this.firePropertyChange("value", oValue, nValue);
        oValue = nValue;
    }
    
    public void mouseExited(MouseEvent e) {
        //        System.out.println("mouseExited");
    }
    
    public void mouseEntered(MouseEvent e) {
        //        System.out.println("mouseEntered");
    }
    
    public void mouseClicked(MouseEvent e) {
        //        System.out.println("mouseClicked");
        //        System.out.println("x= " + e.getComponent().getX() + " y= " + e.getComponent().getY());
    }
    
    public void mousePressed(MouseEvent evt) {
        //        System.out.println("mousePressed");
        if (evt.isPopupTrigger()) {
            //            System.out.println("isPopupTrigger");
            menu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }
    
    public void mouseReleased(MouseEvent evt) {
        //        System.out.println("mouseReleased");
        if (evt.isPopupTrigger()) {
            //            System.out.println("isPopupTrigger");
            menu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }
    
    public void setHelper(JPopupMenu helper) {
        this.helper = helper;
        
        if (helper instanceof HelperPopup)
            ((HelperPopup)this.helper).setTarget(this);
        
        if (helper instanceof RowSetHelperPopup)
            ((RowSetHelperPopup)this.helper).setTarget(this);
        
    }
    
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
            boolean passed = true;
            
            System.out.println("Entro a internalVerifier()");
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
                System.out.println("inputVerifier --> ParseException");
            }
            
            aux = tf.getValue();
            
            passed = validateField(aux);
            
            if (passed == true) {
                
                setBackground(java.awt.Color.WHITE);
                
                aux = tf.getValue();
                System.out.println("inputVerifier(): " + columnName + " aux = " + aux);
                
                if (columnName == null) return true;
                if (colType    == -99 ) return true;
                if (rowset     == null) return true;
                
                try {
                    
                    rowset.removeRowSetListener(tf);
                    
                    if (aux == null) {
                        System.out.println("aux IS null");
                        rowset.updateNull(columnName);
                        rowset.addRowSetListener(tf);
                        tf.firePropertyChange("value", null, aux);
                        return true;
                    }
                    
                    switch(colType) {
                        
                        case java.sql.Types.ARRAY://2003
                            break;
                            
                        case java.sql.Types.BINARY://-2
                            break;
                            
                        case java.sql.Types.BIT://-7
                            //System.out.println("BIT - Set");
                            rowset.updateBoolean(columnName, ((Boolean)tf.getValue()).booleanValue());
                            break;
                            
                        case java.sql.Types.BLOB://2004
                            break;
                            
                        case java.sql.Types.BOOLEAN://16
                            System.out.println("BOOLEAN - Set");
                            break;
                            
                        case java.sql.Types.CLOB://2005
                            break;
                            
                        case java.sql.Types.DATALINK://70
                            break;
                            
                        case java.sql.Types.DATE://91
                            rowset.updateDate(columnName, new java.sql.Date(((java.util.Date) aux).getTime()));
                            break;
                            
                        case java.sql.Types.DECIMAL://3
                            System.out.println("DECIMAL");
                            break;
                            
                        case java.sql.Types.DISTINCT://2001
                            break;
                            
                        case java.sql.Types.FLOAT://6
                        case java.sql.Types.DOUBLE://8
                            //System.out.println("DOUBLE");
                            if (aux instanceof Double) {
                                //System.out.println("Double = columnName => " + columnName);
                                rowset.updateDouble(columnName, ((Double)aux).doubleValue());
                            } else if (aux instanceof Float) {
                                //System.out.println("Float    = columnName => " + columnName);
                                rowset.updateFloat(columnName, ((Float)aux).floatValue());
                            } else {
                                System.out.println("ELSE ???");
                            }
                            
                            if (aux instanceof Double && ((Double) aux).doubleValue() < 0.0) {
                                tf.setForeground(Color.RED);
                            } else {
                                tf.setForeground(Color.BLACK);
                            }
                            break;
                            
                        case java.sql.Types.INTEGER:    //4
                            
                        case java.sql.Types.BIGINT:     //-5
                            
                        case java.sql.Types.SMALLINT:   //5
                            
                        case java.sql.Types.TINYINT:    //-6
                            
                            if (aux instanceof Integer) {
                                //System.out.println("Integer = columnName => " + columnName);
                                rowset.updateInt(columnName, ((Integer)aux).intValue());
                            } else if (aux instanceof Long) {
                                //System.out.println("Long    = columnName => " + columnName);
                                rowset.updateLong(columnName, ((Long)aux).intValue());
                            } else {
                                System.out.println("ELSE ???");
                            }
                            break;
                            
                        case java.sql.Types.JAVA_OBJECT://2000
                            break;
                            
                        case java.sql.Types.LONGVARBINARY://-4
                        case java.sql.Types.VARBINARY://-3
                            break;
                            
                        case java.sql.Types.VARCHAR://
                        case java.sql.Types.LONGVARCHAR://-1
                        case java.sql.Types.CHAR://1
                            //System.out.println("CHAR    = columnName => " + columnName);
                            rowset.updateString(columnName, aux.toString());
                            break;
                            
                        case java.sql.Types.NULL://0
                            break;
                            
                        case java.sql.Types.NUMERIC://2
                            break;
                            
                        case java.sql.Types.OTHER://1111
                            break;
                            
                        case java.sql.Types.REAL://7
                            break;
                            
                        case java.sql.Types.REF://2006
                            break;
                            
                        case java.sql.Types.STRUCT://2002
                            break;
                            
                        case java.sql.Types.TIME://92
                            break;
                            
                        case java.sql.Types.TIMESTAMP://93
                            break;
                            
                        default:
                            System.out.println("============================================================================");
                            System.out.println("default = " + colType);
                            System.out.println("columnName = " + columnName);
                            System.out.println("============================================================================");
                            break;
                    }
                } catch (java.sql.SQLException se) {
                    System.out.println("---> SQLException -----------> " + se);
                } catch(java.lang.NullPointerException np) {
                    System.out.println("<---> NullPointerException <---> " + np + " columnName : " + columnName);
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
    }
    
    public BeanContextChild getBeanContextProxy(){
        System.err.println("getBeanContextProxy Called");
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
    
    /**
     * Overridden from superclass
     */
    public void commitEdit() throws ParseException {
        
        if (getText()==null || getText().length()==0){
            setValue(null);
        } else super.commitEdit();
    }
    
    // this method can be override to make custom validation.
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
}

/*
 * $Log$
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
