
/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2005, The Pangburn Company and Prasanth R. Pasala
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
    public SSImage() {
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
        
        // ADD UPDATE BUTTON LISTENER
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
           
        // SET PREFERRED DIMENSIONS
            setPreferredSize(new Dimension(200,200));
            
        // ADD LABEL & BUTTON TO PANEL
            addComponents(); 
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
     * Sets the new SSRowSet for the image component.
     *
     * @param _rowset  SSRowSet to which the image has to update values.
     */
    public void setSSRowSet(SSRowSet _rowset) {
        rowset = _rowset;
        bind();
    }    

    /**
     * Sets the column name for the combo box
     *
     * @param _columnName   name of column
     */
    public void setColumnName(String _columnName) {
        columnName = _columnName;
        bind();
    }
    
    /**
     * Returns the column name to which the combo is bound.
     *
     * @return returns the column name to which to combo box is bound.
     */
    public String getColumnName() {
        return columnName;
    }
    
    /**
     * Returns the SSRowSet being used to get the values.
     *
     * @return returns the SSRowSet being used.
     */
    public SSRowSet getSSRowSet() {
        return rowset;
    }      
    
    
    /**
     * Changes the image to the specified image.
     *
     * @param _img GIF or JPEG to store to rowset & display
     */
/*     
    public void setImage(ImageIcon _img){
        img = _img;
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
*/    
    
    /**
     * Returns the current image.
     */
/*     
    public void getImage(ImageIcon _img) {
        return(img);
    }
*/    
    
    /**
     *  Binds SSImage to the columnName in the rowset.
     *@param rowset - rowset from/to which data has to be read/written
     *@param columnName - column in the rowset to which the component should be bound.
     */
    public void bind(SSRowSet _rowset, String _columnName) {
        rowset = _rowset;
        columnName = _columnName;
        bind();
    }
    
    /**
     * Binds SSImage to the columnName in the rowset.
     */
    protected void bind() {
        
        // CHECK FOR NULL COLUMN/ROWSET
            if (columnName==null || rowset==null) {
                return;
            }
            
        // REMOVE LISTENERS TO PREVENT DUPLICATION
            removeListeners();
            
        // UPDATE DISPLAY
            setDisplay();
        
        // ADD BACK LISTENERS
            addListeners();        
    }

    // ADDS LISTENERS FOR THE COMBO BOX AND TEXT FIELD
    private void addListeners() {
        rowset.addRowSetListener(rowsetListener);
    }

    // REMOVES THE LISTENERS FOR TEXT FIELD AND THE COMBO BOX DISPLAYED
    private void removeListeners() {
        rowset.removeRowSetListener(rowsetListener);
    }    
    
    /**
     * Reads the image from the rowset and sets it to the label for display.
     */
    protected void setDisplay() {

        try {
            byte[] imageData = rowset.getRow() >0 ? rowset.getBytes(columnName) : null;
            if(imageData != null){
                img = new ImageIcon(imageData);
                lblImage.setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
                lblImage.setText("");
            } else {
                img = null;
                lblImage.setText("No Picture");
            }
        } catch(SQLException se) {
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
            //System.out.println("Cursor Moved");
            setDisplay();
        }
        
        public void rowChanged(RowSetEvent rse){
            //System.out.println("Row Changed");
            setDisplay();
        }
        
        public void rowSetChanged(RowSetEvent rse){
            //System.out.println("RowSet Changed");
            setDisplay();   
        }
                
    }
    
    /**
     * Sets the preferred size of this component.
     *
     * @param dimension - preferred dimension for component.
     */
    public void setPreferredSize(Dimension dimension){
        lblImage.setPreferredSize(new Dimension((int)dimension.getWidth(), (int)dimension.getHeight() - 20));
        btnUpdateImage.setPreferredSize(new Dimension((int)dimension.getWidth(), 20));
        super.setPreferredSize(dimension);
    }
    
    /**
     * Removes the current image. The image is not removed from the underlying rowset.
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
 *Revision 1.2  2005/02/02 23:37:19  yoda2
 *API cleanup.
 *
 *Revision 1.1  2005/01/18 20:59:13  prasanth
 *Initial Commit.
 *
 */