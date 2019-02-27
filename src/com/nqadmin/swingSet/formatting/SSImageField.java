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

package com.nqadmin.swingSet.formatting;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.RowSetListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.datasources.SSRowSet;

/**
 * SSImageField.java
 *
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Used to link a JPanel to an image column in a database.
 */
public class SSImageField extends JPanel implements RowSetListener, KeyListener, ComponentListener {
    
	private static final long serialVersionUID = 889303691158522232L;
	private byte[] imageBytes;
    private ImageIcon fullIcon;
    private ImageIcon scaledIcon;
    private ImageIcon nullIcon;
    private JButton imageButton;
    private JButton getButton;
    private String columnName = null;
    private int colType = -99;
    private SSRowSet rowset = null;
    private SSDataNavigator navigator = null;
    
    /** Creates a new instance of SSImageField */
    public SSImageField() {
        super();
        
        Set<AWTKeyStroke> forwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK ));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,newForwardKeys);
        
        Set<AWTKeyStroke> backwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        Set<AWTKeyStroke> newBackwardKeys = new HashSet<AWTKeyStroke>(backwardKeys);
        newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_MASK ));
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,newBackwardKeys);
        
        this.addKeyListener(this);
        
        this.addComponentListener(this);
        
        setLayout(new BorderLayout());
        
        imageButton = new JButton();
        imageButton.setFocusable(false);
        
        imageButton.setIconTextGap(0);
        imageButton.setBorder(null);
        imageButton.setMargin(new Insets(0,0,0,0));
        imageButton.setText("");
        nullIcon = new ImageIcon(getClass().getResource("/com/nqadmin/swingSet/formatting/image.png"));
        fullIcon = nullIcon;
        imageButton.setIcon(fullIcon);
        add(imageButton, BorderLayout.CENTER);
        validate();
        
//        scrollPane = new JScrollPane(imageButton);
//        add(scrollPane, BorderLayout.CENTER);
        
        getButton = new JButton("from file ...");
        getButton.setFocusable(false);
        
        getButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try{
                    //if (rowset != null) {
                    FileInputStream inStream = null;
                    File inFile = null;
                    JFileChooser fileChooser = new JFileChooser();
                    if(fileChooser.showOpenDialog(getButton) == JFileChooser.APPROVE_OPTION){
                        inFile = fileChooser.getSelectedFile();
                        inStream = new FileInputStream(inFile);
                        int totalLength = (int)inFile.length();
                        byte[] bytes = new byte[totalLength];
                        int bytesRead = inStream.read(bytes);
                        while (bytesRead < totalLength){
                            int read = inStream.read(bytes, bytesRead, totalLength - bytesRead);
                            if(read == -1)
                                break;
                            else
                                bytesRead += read;
                        }
                        inStream.close();
                        //rowset.updateBytes(columnName, bytes);
                        imageBytes = bytes;
                        fullIcon = new ImageIcon(bytes);
                        try {
                            rowset.updateBytes(columnName, imageBytes);
                        } catch( SQLException se) {
                            
                        }
                        Rescale();
                    } else {
                        return;
                    }
                    //}
                    //}catch(SQLException se){
                    //    se.printStackTrace();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });
        add(getButton, BorderLayout.SOUTH);
    }
    
    /**
     * Creates a image icon from the specified image
     * @param image - image to be used to create image icon
     * @return return the image icon created
     */
    private ImageIcon Thumbnail(Image image) {
        double scale, fw, fh;
        int wi, hi;
        int wo, ho;
        int ws, hs;
        Image scaled;
        
        imageButton.setIcon(null);
        this.validate();
        
        wi = imageButton.getWidth();
        hi = imageButton.getHeight();
        
        wo = image.getWidth(this);
        ho = image.getHeight(this);
        
        fw = (double) wi / (double) wo;
        fh = (double) hi / (double) ho;
        
        if (fw > fh) scale = fh;
        else scale = fw;
        
        ws = (int) (scale * wo);
        hs = (int) (scale * ho);
        
        if (wi == 0 && hi ==0 ) {
            ws = wo; hs = ho;
        }
        scaled = image.getScaledInstance(ws, hs, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
    
    /**
     * Column name in the SSRowSet to which this component will be bound to
     * @param columnName - column name in the SSRowSet to which this component will be bound to
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
        return this.columnName;
    }
    
    /**
     * Sets the SSRowSet object to be used to get/set the value of the bound column
     * @param rowset - SSRowSet object to be used to get/set the value of the bound column
     * @deprecated
     * @see #setSSRowSet(SSRowSet)
     */
    public void setRowSet(SSRowSet rowset) {
        this.setSSRowSet(rowset);
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
     * @deprecated
     * @see #getSSRowSet()
     */
    public SSRowSet getRowSet() {
        return this.getSSRowSet();
    }
    
    /**
     * SSRowSet object being used to get/set the bound column value
     * @return - returns the SSRowSet object being used to get/set the bound column value
     */
    public SSRowSet getSSRowSet() {
        return this.rowset;
    }
    
     /**
     * Sets the SSDataNavigator being used to navigate the SSRowSet
     * This is needed only if you want to include the function keys as short cuts to perform operations on the DataNavigator
     * like saving the current row/ undo changes/ delete current row.
     * <font color=red>The functionality for this is not yet finalized so try to avoid using this </font>
     * @param navigator - SSDataNavigator being used to navigate the SSRowSet   
     * @deprecated
     * @see #setSSDataNavigator(SSDataNavigator) 
     **/
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
     * Gets the value from the rowset and sets it to the component 
     */
    private void DbToFm() {
        
        try {
            if (rowset.getRow() == 0) return;
            
            switch(colType) {
                
                case java.sql.Types.BINARY:
                    imageBytes = rowset.getBytes(columnName);
                    
                    if (imageBytes == null) {
                        fullIcon = nullIcon;
                        Rescale();
                        break;
                    }
                    
                    if (imageBytes.length > 0)
                        
                        fullIcon = new ImageIcon(imageBytes);
                    else
                        fullIcon = nullIcon;
                    Rescale();
                    break;
                    
                default:
                    break;
            }
        } catch (java.sql.SQLException sqe) {
            System.out.println("SSImageField --> Error in DbToFm() = " + sqe);
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

    /**
     * Binds the component to the specified column in the given SSRowSet 
     */
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
    
    
    /* (non-Javadoc)
     * @see javax.sql.RowSetListener#rowSetChanged(javax.sql.RowSetEvent)
     */
    public void rowSetChanged(javax.sql.RowSetEvent event) {
        
    }
    
    /* (non-Javadoc)
     * @see javax.sql.RowSetListener#rowChanged(javax.sql.RowSetEvent)
     */
    public void rowChanged(javax.sql.RowSetEvent event) {
        
    }
    
    /* (non-Javadoc)
     * @see javax.sql.RowSetListener#cursorMoved(javax.sql.RowSetEvent)
     */
    public void cursorMoved(javax.sql.RowSetEvent event) {
        DbToFm();
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
     *
     */
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
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
    
    
    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    public void componentShown(java.awt.event.ComponentEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(java.awt.event.ComponentEvent e) {
        Rescale();
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    public void componentMoved(java.awt.event.ComponentEvent e) {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    public void componentHidden(java.awt.event.ComponentEvent e) {
    }
    
    /**
     * 
     */
    private void Rescale() {
        if (fullIcon != null) {
            if (!fullIcon.equals(nullIcon))
                scaledIcon = Thumbnail(fullIcon.getImage());
            else
                scaledIcon = fullIcon;
            imageButton.setIcon(scaledIcon);
            updateUI();
        }
    }

}
