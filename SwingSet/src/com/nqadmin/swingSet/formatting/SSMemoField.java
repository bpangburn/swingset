/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004, The Pangburn Company, Inc, Prasanth R. Pasala and
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

import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.datasources.SSRowSet;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import javax.sql.RowSetListener;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author dags
 */
public class SSMemoField extends JTextArea implements RowSetListener, KeyListener, FocusListener {
    
    private java.awt.Color std_color = null;
    private String columnName = null;
    private int colType = -99;
    private SSRowSet rowset = null;
    private SSDataNavigator navigator = null;
    
    /** Creates a new instance of SSBooleanField */
    public SSMemoField() {
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
        this.setInputVerifier(new internalVerifier());
    }
    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
        bind(rowset, columnName);
    }
    
    public void setRowSet(SSRowSet rowset) {
        this.rowset = rowset;
        bind(rowset, columnName);
    }
    
    public void setNavigator(SSDataNavigator navigator) {
        this.navigator = navigator;
        setRowSet(navigator.getRowSet());
    }
    
    public SSDataNavigator getNavigator() {
        return this.navigator;
    }
    
    
    private void DbToFm() {
        
        try {
            
            switch(colType) {
                
                case java.sql.Types.VARCHAR://-7
                case java.sql.Types.LONGVARCHAR://-7
                case java.sql.Types.CHAR://-7
                    this.setText(rowset.getString(columnName));
                    break;
                    
                default:
                    break;
            }
        } catch (java.sql.SQLException sqe) {
            System.out.println("Error in DbToFm() = " + sqe);
        }
    }
    
    public void bind(com.nqadmin.swingSet.datasources.SSRowSet rowset, String columnName) {
        this.columnName = columnName;
        this.rowset = rowset;
        
        if (this.columnName == null) return;
        if (this.rowset  == null) return;
        
        try {
            colType = rowset.getColumnType(columnName);
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
            //showHelper(e);
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F2) {
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F3) {
            System.out.println("F3 ");
            //calculator = new javax.swing.JPopupMenu();
            //calculator.add(new com.nqadmin.swingSet.formatting.utils.JCalculator());
            //JFormattedTextField ob = (JFormattedTextField)(e.getSource());
            //java.awt.Dimension d = ob.getSize();
            //calculator.show(ob, 0, d.height);
            
            //((Component)e.getSource()).transferFocus();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F4) {
            System.out.println("F4 ");
            //((Component)e.getSource()).transferFocus();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            System.out.println("F5 = PROCESS");
            if (navigator.updatePresentRow()==true) {
                System.out.println("Update Sucessfully");
            }
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F6) {
            System.out.println("F6 = DELETE");
            navigator.doDeleteButtonClick();
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F8) {
            System.out.println("F8 ");
            //((Component)e.getSource()).transferFocus();
            navigator.doUndoButtonClick();
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
     * This method should implements validation AND, most important for our purposes
     * implements actual rowset fields updates.
     *
     */
    
    class internalVerifier extends InputVerifier {
        
        public boolean verify(JComponent input) {
            
            String aux = null;
            boolean passed = true;
            
            SSMemoField tf = (SSMemoField) input;
            aux = tf.getText();
            
            System.out.println("inputVerifier():");
            
            if (aux == null) {
                passed = false;
            }
            
            if (passed == true) {
                
                setBackground(java.awt.Color.WHITE);
                
                try {
                    rowset.removeRowSetListener(tf);
                    
                    switch(colType) {
                        
                        case java.sql.Types.VARCHAR://-7
                        case java.sql.Types.LONGVARCHAR://-7
                        case java.sql.Types.CHAR://-7
                            rowset.updateString(columnName, aux);
                            break;
                            
                        default:
                            break;
                    }
                    rowset.addRowSetListener(tf);
                } catch (java.sql.SQLException se) {
                    System.out.println("---> SQLException -----------> " + se);
                } catch(java.lang.NullPointerException np) {
                    System.out.println("---> NullPointerException ---> " + np);
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
