
/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2005, The Pangburn Company, Inc. and Prasanth R. Pasala
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

package com.nqadmin.swingSet;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import java.sql.SQLException;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.File;
import javax.swing.JFileChooser;
import java.io.IOException;
import javax.swing.JScrollPane;

import com.nqadmin.swingSet.datasources.SSRowSet;

public class SSImage extends JPanel{
    
    /**
     *  ImageIcon to store the image.
     */
    protected ImageIcon img;
    
    /**
     *  Label to display the image
     */
    protected JLabel lblImage = new JLabel("No Picture");
    
    /**
     *  Button to update the image.
     */
    protected JButton btnUpdateImage = new JButton("Update");
    
    /**
     *  RowSet to which image has to be written or from which image has to be read.
     */
    protected SSRowSet rowset;
    
    /**
     *  Column name in which image is stored.
     */
    protected String columnName;
    
    /**
     *  RowSet listener
     */
    protected MyRowSetListener rowsetListener = new MyRowSetListener();
    
    /**
     *
     *  Construct a default SSImage Object.
     */
    public SSImage(){
        init();
    }
    
    /**
     * Constructs a SSImage Object bound to the specified column in the specified rowset.
     *
     * @param rowset - rowset from/to which data has to be read/written
     * @param columnName - column in the rowset to which the component should be bound.
     */
    public SSImage(SSRowSet _rowset, String _columnName) {
        rowset = _rowset;
        columnName = _columnName;
        init();
        bind();
    }
    
    /**
     * Initialization code.
     */
    protected void init() {
           
        // SET PREFERRED DIMENSIONS
            setPreferredSize(new Dimension(200,200));
            
// ADD LISTENERS & COMPONENTS
        addListener();
        addComponents(); 
    }      
    
    /**
     *  adds listener to the update button.
     */
    protected void addListener(){
        btnUpdateImage.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                try{
                    if(rowset != null){
                        FileInputStream inStream = null;
                        File inFile = null;
                        JFileChooser fileChooser = new JFileChooser();
                        if(fileChooser.showOpenDialog(btnUpdateImage) == JFileChooser.APPROVE_OPTION){
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
                            rowset.updateBytes(columnName, bytes);
                            img = new ImageIcon(bytes);
                            lblImage.setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
                            lblImage.setIcon(img);
                            lblImage.setText("");
                            updateUI();
                        }
                        else{
                            return;
                        }                   
                    }
                }catch(SQLException se){
                    se.printStackTrace();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }   
            }
        });
    }
    
    /**
     *  Adds the label and button to the panel
     */
    protected void addComponents(){
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy= 0;
        JScrollPane scrollPane = new JScrollPane(lblImage, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(200, 180));
        btnUpdateImage.setPreferredSize(new Dimension(200,20));
        add(scrollPane, constraints);
        constraints.gridy = 1;
        add(btnUpdateImage, constraints);
    }
    
    /**
     *    Changes the image to the specified image.
     */
    public void setImage(ImageIcon img){
        this.img = img;
        if(img != null){
            lblImage.setIcon(img);
            lblImage.setText("");
        }
        else{
            lblImage.setIcon(null);
            lblImage.setText("No Picture");
        }
        updateUI();
    }
    
    /**
     *  Binds SSImage to the columnName in the rowset.
     *@param rowset - rowset from/to which data has to be read/written
     *@param columnName - column in the rowset to which the component should be bound.
     */
    public void bind(SSRowSet _rowset, String _columnName){
        if(rowset != null)
            rowset.removeRowSetListener(rowsetListener);
        rowset = _rowset;
        columnName = _columnName;
        rowset.addRowSetListener(rowsetListener);
        
        bind();
    }
    
    /**
     * Binds SSImage to the columnName in the rowset.
     */
    private void bind(){
        try{
            byte[] imageData = rowset.getRow() >0 ? rowset.getBytes(columnName) : null;
            if(imageData != null){
                img = new ImageIcon(imageData);
                lblImage.setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
                lblImage.setText("");
            }
            else{
                img = null;
                lblImage.setText("No Picture");
            }
        }catch(SQLException se){
            se.printStackTrace();
            img = null;
        }
        lblImage.setIcon(img);
        updateUI();
    }    
    
    /**
     *  Reads the image from the rowset and sets it to the label for display.
     *@param rse - RowSetEvent that triggered the event.
     */
    protected void updateImage(RowSetEvent rse){
        try{
            RowSet rs = (RowSet)rse.getSource();
            byte[] imageData = rs.getRow() >0 ? rs.getBytes(columnName) : null;
            if(imageData != null){
                img = new ImageIcon(imageData);
                lblImage.setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
                lblImage.setText("");
            }
            else{
                img = null;
                lblImage.setText("No Picture");
            }
                        
        }catch(SQLException se){
            se.printStackTrace();
            img = null;
        }
        lblImage.setIcon(img);
        updateUI();
    }

    /**
     *  Listener for the RowSet.
     */
    private class MyRowSetListener  implements RowSetListener {
        public void cursorMoved(RowSetEvent rse){
            System.out.println("Cursor Moved");
            updateImage(rse);
        }
        
        public void rowChanged(RowSetEvent rse){
            System.out.println("Row Changed");
            updateImage(rse);
        }
        
        public void rowSetChanged(RowSetEvent rse){
            System.out.println("RowSet Changed");
            updateImage(rse);   
        }
                
    }
    
    /**
     *  Sets the preferred size of this component.
     *@param dimension - preferred dimension for component.
     */
    public void setPreferredSize(Dimension dimension){
        lblImage.setPreferredSize(new Dimension((int)dimension.getWidth(), (int)dimension.getHeight() - 20));
        btnUpdateImage.setPreferredSize(new Dimension((int)dimension.getWidth(), 20));
        super.setPreferredSize(dimension);
    }
    
    /**
     *  Removes the current image. The image is not removed from the underlying rowset.
     */
    public void clearImage(){
        lblImage.setIcon(null);
        lblImage.setText("No Picture");
        Dimension dimension = getPreferredSize();
        lblImage.setPreferredSize(new Dimension((int)dimension.getWidth(), (int)dimension.getHeight()-20));
        updateUI();
    }
}

/*
 *$Log$
 *Revision 1.1  2005/01/18 20:59:13  prasanth
 *Initial Commit.
 *
 */