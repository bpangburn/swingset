/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004, The Pangburn Company, Inc, Prasanth R. Pasala and
 * Deigo Gil
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;

import javax.sql.RowSetListener;
import javax.swing.JFormattedTextField;
import java.text.*;
import java.util.Set;
import java.util.HashSet;

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
public class SSFormattedTextField extends JFormattedTextField implements RowSetListener, KeyListener, FocusListener {
    
    private java.awt.Color std_color = null;
    private String colName = null;
    private int colType = -99;
    private com.nqadmin.swingSet.datasources.SSRowSet rowset;
    
    
    /** Creates a new instance of SSFormattedTextField */
    public SSFormattedTextField() {
        super();
        
        Set forwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set newForwardKeys = new HashSet(forwardKeys);
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK ));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,newForwardKeys);
        
        Set backwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        Set newBackwardKeys = new HashSet(backwardKeys);
        newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_MASK ));
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,newBackwardKeys);
        
        addKeyListener(this);
        addFocusListener(this);
        
        setInputVerifier(new internalVerifier());
        
    }
    
    public SSFormattedTextField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        this();
        
        this.setFormatterFactory(factory);
    }
    
    public void bind(com.nqadmin.swingSet.datasources.SSRowSet rowset, String colName) {
        this.colName = colName;
        this.rowset = rowset;
        
        try {
            colType = rowset.getColumnType(colName);
            System.out.println("BIND ---------------------------------------------------");
            System.out.println("bind() ---> colName = " + colName);
            System.out.println("bind() ---> colType = " + colType);
            System.out.println("--------------------------------------------------------");
        } catch(java.sql.SQLException sqe) {
            System.out.println("bind error = " + sqe);
        }
        
        rowset.addRowSetListener(this);
        
        DbToFm();
    }
    
    public void rowSetChanged(javax.sql.RowSetEvent event) {
        
    }
    
    public void rowChanged(javax.sql.RowSetEvent event) {
        
    }
    
    public void cursorMoved(javax.sql.RowSetEvent event) {
        DbToFm();
    }
    
    public void keyTyped(KeyEvent e) {
    }
    
    public void keyReleased(KeyEvent e) {
    }
    
    /**
     *  Catch severals keys, to implement some forms functionality (To be done).
     *
     *
     *
     *
     */
    public void keyPressed(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_F1) {
            System.out.println("F1 = HELP ");
            //((Component)e.getSource()).transferFocus();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F2) {
            System.out.println("F2 ");
            //((Component)e.getSource()).transferFocus();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F3) {
            System.out.println("F3 ");
            //((Component)e.getSource()).transferFocus();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F4) {
            System.out.println("F4 ");
            //((Component)e.getSource()).transferFocus();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            System.out.println("F5 = PROCESS");
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F6) {
            System.out.println("F6 = DELETE");
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F8) {
            System.out.println("F8 ");
            //((Component)e.getSource()).transferFocus();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_END) {
            System.out.println("END ");
            //((Component)e.getSource()).transferFocus();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            System.out.println("DELETE ");
            //((Component)e.getSource()).transferFocus();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_HOME) {
            System.out.println("HOME ");
            //((Component)e.getSource()).transferFocus();
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
     *
     *
     *
     *
     */
    
    private void DbToFm() {
        
        try {
            switch(colType) {
                case java.sql.Types.ARRAY://2003
                    break;
                    
                case java.sql.Types.BINARY://-2
                    break;
                    
                case java.sql.Types.BIT://-7
                    //                    System.out.println("BIT");
                    this.setValue(new Boolean(rowset.getBoolean(colName)));
                    break;
                    
                case java.sql.Types.BLOB://2004
                    break;
                    
                case java.sql.Types.BOOLEAN://16
                    System.out.println("BOOLEAN");
                    break;
                    
                case java.sql.Types.CLOB://2005
                    break;
                    
                case java.sql.Types.DATALINK://70
                    break;
                    
                case java.sql.Types.DATE://91
                    this.setValue(new java.util.Date(rowset.getDate(colName).getTime()));
                    break;
                    
                case java.sql.Types.DECIMAL://3
                    break;
                    
                case java.sql.Types.DISTINCT://2001
                    break;
                    
                case java.sql.Types.DOUBLE://8
                    break;
                    
                case java.sql.Types.FLOAT://6
                    break;
                    
                case java.sql.Types.INTEGER://4
                case java.sql.Types.BIGINT://-5
                case java.sql.Types.SMALLINT://5
                case java.sql.Types.TINYINT://-6
                    this.setValue(new Integer(rowset.getInt(colName)));
                    break;
                    
                case java.sql.Types.JAVA_OBJECT://2000
                    break;
                    
                case java.sql.Types.LONGVARBINARY://-4
                case java.sql.Types.VARBINARY://-3
                    break;
                    
                case java.sql.Types.VARCHAR://
                case java.sql.Types.LONGVARCHAR://-1
                case java.sql.Types.CHAR://1
                    this.setValue(rowset.getString(colName));
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
    }
    
    /**
     * This method should implements validation AND, most important for our purposes
     * implements actual rowset fields updates.
     *
     */
    
    class internalVerifier extends InputVerifier {
        
        public boolean verify(JComponent input) {
            
            Object aux = null;
            boolean passed = true;
            
            /**
             * field to be validated and updated
             */
            
            SSFormattedTextField tf = (SSFormattedTextField) input;
            
            /**
             * future NULL validation ....
             *
             * test null
             */
            //if (tf.isNullable() == false && tf.getValue().equals(null)) {
            //    passed = false;
            //}
            
            /**
             *
             * future test of numeric ranges
             *
             * properties to add:
             *      minValue
             *      maxValue
             *
             * If value is outside range, returns false and focus transfer is canceled (stay in same field, background set to RED).
             */
            
            //int val = ((Integer)tf.getValue()).intValue();
            //if (val < minValue) passed = false;
            //if (val > maxValue) passed = false;
            
            if (passed == true) {
                
                setBackground(java.awt.Color.WHITE);
                
                try {
                    tf.commitEdit();
                    System.out.println("Committed();");
                } catch (java.text.ParseException pe) {
                    System.out.println("ParseException");
                }
                
                try {
                    rowset.removeRowSetListener(tf);
                    
                    aux = tf.getValue();
                    
                    switch(colType) {
                        
                        case java.sql.Types.ARRAY://2003
                            break;
                            
                        case java.sql.Types.BINARY://-2
                            break;
                            
                        case java.sql.Types.BIT://-7
                            System.out.println("BIT - Set");
                            rowset.updateBoolean(colName, Boolean.parseBoolean(tf.getText()));
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
                            rowset.updateDate(colName, new java.sql.Date(((java.util.Date) aux).getTime()));
                            break;
                            
                        case java.sql.Types.DECIMAL://3
                            System.out.println("DECIMAL");
                            break;
                            
                        case java.sql.Types.DISTINCT://2001
                            break;
                            
                        case java.sql.Types.FLOAT://6
                        case java.sql.Types.DOUBLE://8
                            System.out.println("DOUBLE");
                            if (aux instanceof Double) {
                                System.out.println("Double = colName => " + colName);
                                System.out.println("getValue() = " + aux);
                                rowset.updateDouble(colName, ((Double)aux).intValue());
                            } else if (aux instanceof Float) {
                                System.out.println("Float    = colName => " + colName);
                                System.out.println("getValue() = " + aux);
                                rowset.updateInt(colName, ((Float)aux).intValue());
                            } else {
                                System.out.println("ELSE ???");
                            }
                            System.out.println("getValue() = " + aux);
                            break;
                            
                        case java.sql.Types.INTEGER:    //4
                            
                        case java.sql.Types.BIGINT:     //-5
                            
                        case java.sql.Types.SMALLINT:   //5
                            
                        case java.sql.Types.TINYINT:    //-6
                            
                            if (aux instanceof Integer) {
                                System.out.println("Integer = colName => " + colName);
                                System.out.println("getValue() = " + aux);
                                rowset.updateInt(colName, ((Integer)aux).intValue());
                            } else if (aux instanceof Long) {
                                System.out.println("Long    = colName => " + colName);
                                System.out.println("getValue() = " + aux);
                                rowset.updateInt(colName, ((Long)aux).intValue());
                            } else {
                                System.out.println("ELSE ???");
                            }
                            System.out.println("getValue() = " + aux);
                            break;
                            
                        case java.sql.Types.JAVA_OBJECT://2000
                            break;
                            
                        case java.sql.Types.LONGVARBINARY://-4
                        case java.sql.Types.VARBINARY://-3
                            break;
                            
                        case java.sql.Types.VARCHAR://
                        case java.sql.Types.LONGVARCHAR://-1
                        case java.sql.Types.CHAR://1
                            System.out.println("CHAR    = colName => " + colName);
                            System.out.println("getValue() = " + aux);
                            rowset.updateString(colName, aux.toString());
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
                            System.out.println("ColName = " + colName);
                            System.out.println("============================================================================");

                            if (aux instanceof java.lang.Double
                                    && ((java.lang.Double) aux).doubleValue() < 0.0) {
                                tf.setForeground(Color.RED);
                            } else {
                                tf.setForeground(Color.BLACK);
                            }
                            break;
                    }
                    rowset.addRowSetListener(tf);
                } catch (java.sql.SQLException se) {
                    System.out.println("SQLException");
                }
                catch(java.lang.NullPointerException np) {
                    System.out.println("NullPointerException " + np);
                }
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
}

/*
 * $Log$
 * Revision 1.3  2004/12/13 20:50:16  dags
 * Fix package name
 *
 * Revision 1.2  2004/12/13 18:46:13  prasanth
 * Added License.
 *
 */
