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

import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.datasources.SSRowSet;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import javax.sql.RowSetListener;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author dags
 */
public class SSBooleanField extends JCheckBox implements SSField, RowSetListener, KeyListener {
    
    private java.awt.Color std_color = null;
    private String columnName = null;
    private int colType = -99;
    private SSRowSet rowset = null;
    private SSDataNavigator navigator = null;
    
    /** Creates a new instance of SSBooleanField */
    public SSBooleanField() {
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
        
        this.addKeyListener(this);
        
        this.setInputVerifier(new internalVerifier());
    }
    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
        bind();
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public void setRowSet(SSRowSet rowset) {
        this.setSSRowSet(rowset);
    }
    
    public void setSSRowSet(SSRowSet rowset) {
        this.rowset = rowset;
        bind();
    }
    
    public SSRowSet getRowSet() {
        return this.getSSRowSet();
    }
    
    public SSRowSet getSSRowSet() {
        return this.rowset;
    }
    
    public void setNavigator(SSDataNavigator navigator) {
        this.navigator = navigator;
        setRowSet(navigator.getSSRowSet());
        bind();
    }
    
    public SSDataNavigator getNavigator() {
        return this.navigator;
    }
    
    private void DbToFm() {
        
        try {
            if (rowset.getRow() == 0) return;
            
            switch(colType) {
                
                case java.sql.Types.BIT://-7
                    this.setSelected(rowset.getBoolean(columnName));
                    break;
                    
                case java.sql.Types.BOOLEAN://16
                    this.setSelected(rowset.getBoolean(columnName));
                    break;
                    
                case java.sql.Types.INTEGER://4
                case java.sql.Types.BIGINT://-5
                case java.sql.Types.SMALLINT://5
                case java.sql.Types.TINYINT://-6
                    if (rowset.getInt(columnName) == 1)
                        this.setSelected(true);
                    else
                        this.setSelected(false);
                    break;
                    
                default:
                    break;
            }
        } catch (java.sql.SQLException sqe) {
            System.out.println("Error in DbToFm() = " + sqe);
        }
    }
    
    /**
     * Sets the SSRowSet and column name to which the component is to be bound.
     *
     * @param _sSRowSet    datasource to be used.
     * @param _columnName  Name of the column to which this check box should be bound
     */
    public void bind(SSRowSet _sSRowSet, String _columnName) {
        rowset = _sSRowSet;
        columnName = _columnName;
        bind();
    }

    private void bind() {
        
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
     */
    public void keyPressed(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_F1) {
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F2) {
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F3) {
            
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

    public void cleanField() {
        setDefaultValue();
    }
    
    public void setDefaultValue() {
        this.setSelected(false);
    }
    
    
    /**
     * This method should implements validation AND, most important for our purposes
     * implements actual rowset fields updates.
     *
     */
    
    class internalVerifier extends InputVerifier {
        
        public boolean verify(JComponent input) {
            
            SSBooleanField tf = (SSBooleanField) input;
            boolean selected = tf.isSelected();
            
            setBackground(java.awt.Color.WHITE);
            
            // if not linked to a db field, returns.
            if (columnName == null || rowset == null) return true;
            
            try {
                rowset.removeRowSetListener(tf);
                
                switch(colType) {
                    
                    case java.sql.Types.BIT://-7
                        rowset.updateBoolean(columnName, selected);
                        break;
                        
                    case java.sql.Types.BOOLEAN://16
                        rowset.updateBoolean(columnName, selected);
                        break;
                        
                    case java.sql.Types.INTEGER:    //4
                    case java.sql.Types.BIGINT:     //-5
                    case java.sql.Types.SMALLINT:   //5
                    case java.sql.Types.TINYINT:    //-6
                        if (selected == true) {
                            rowset.updateInt(columnName, 1);
                        } else {
                            rowset.updateInt(columnName, 0);
                        }
                        break;
                        
                    default:
                        break;
                }
                rowset.addRowSetListener(tf);
            } catch (java.sql.SQLException se) {
                System.out.println("SSBooleanField ---> SQLException -----------> " + se);
            } catch(java.lang.NullPointerException np) {
                System.out.println("SSBooleanField ---> NullPointerException ---> " + np);
            }
            return true;
        }
    }
}

/*
 * $Log$
 * Revision 1.11  2005/05/26 12:12:36  dags
 * added bind(SSRowSet, columnName) method and some java.sql.Types checking and support
 *
 * Revision 1.10  2005/03/30 13:03:51  dags
 * Accept null dates values
 *
 * Revision 1.9  2005/03/28 14:46:42  dags
 * syncro commit
 *
 * Revision 1.8  2005/02/22 15:14:34  yoda2
 * Fixed some JavaDoc & deprecation errors/warnings.
 *
 * Revision 1.7  2005/02/04 22:42:06  yoda2
 * Updated Copyright info.
 *
 * Revision 1.6  2005/01/19 19:12:26  dags
 * bind refactoring
 *
 * Revision 1.5  2005/01/14 00:06:42  dags
 * Deep Refactoring
 *
 * Revision 1.4  2004/12/21 05:07:02  dags
 * Remove SSFormattedTextField dependency. Simplified, I hope.
 *
 * Revision 1.3  2004/12/13 20:50:16  dags
 * Fix package name
 *
 * Revision 1.2  2004/12/13 18:46:13  prasanth
 * Added License.
 *
 */
