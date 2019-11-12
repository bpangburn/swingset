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

package com.nqadmin.swingset;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.nqadmin.swingset.datasources.SSRowSet;

/**
 * SSImage.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Used to load, store, & display images stored in a database.
 */
public class SSImage extends JPanel {

    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = -2726746843832259767L;

	/**
     * ImageIcon to store the image.
     */
    protected ImageIcon img;

    /**
     * Label to display the image
     */
    protected JLabel lblImage = new JLabel("No Picture");

    /**
     * Button to update the image.
     */
    protected JButton btnUpdateImage = new JButton("Update");

    /**
     * SSRowSet from which component will get/set values.
     */
    protected SSRowSet sSRowSet;

    /**
     * SSRowSet column to which the component will be bound.
     */
    protected String columnName = "";

    /**
     * The preferred size of the image component.
     */
    protected Dimension preferredSize = new Dimension(200,200);

    /**
     * RowSet listener
     */
    protected final MyRowSetListener sSRowSetListener = new MyRowSetListener();

    /**
     *
     *  Construct a default SSImage Object.
     */
    public SSImage() {
        init();
    }

    /**
     * Constructs a SSImage Object bound to the specified column in the specified sSRowSet.
     *
     * @param _sSRowSet - sSRowSet from/to which data has to be read/written
     * @param _columnName - column in the sSRowSet to which the component should be bound.
     */
    public SSImage(SSRowSet _sSRowSet, String _columnName) {
        this.sSRowSet = _sSRowSet;
        this.columnName = _columnName;
        init();
        bind();
    }

    /**
     * Sets the SSRowSet to which the component is bound.
     *
     * @param _sSRowSet    SSRowSet to which the component is bound
     */
    public void setSSRowSet(SSRowSet _sSRowSet) {
        SSRowSet oldValue = this.sSRowSet;
        this.sSRowSet = _sSRowSet;
        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);
        bind();
    }

    /**
     * Returns the SSRowSet to which the component is bound.
     *
     * @return SSRowSet to which the component is bound
     */
    public SSRowSet getSSRowSet() {
        return this.sSRowSet;
    }

    /**
     * Sets the SSRowSet column name to which the component is bound.
     *
     * @param _columnName    column name in the SSRowSet to which the component
     *    is bound
     */
    public void setColumnName(String _columnName) {
        String oldValue = this.columnName;
        this.columnName = _columnName;
        firePropertyChange("columnName", oldValue, this.columnName);
        bind();
    }

    /**
     * Returns the SSRowSet column name to which the component is bound.
     *
     * @return column name to which the component is bound
     */
    public String getColumnName() {
        return this.columnName;
    }

    /**
     * Changes the image to the specified image.
     *
     * @param _img GIF or JPEG to store to sSRowSet & display
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
     * Sets the preferred size of the image component.
     *
     * @param _preferredSize - preferred size of the image component
     */
    @Override
	public void setPreferredSize(Dimension _preferredSize) {
        Dimension oldValue = this.preferredSize;
        this.preferredSize = _preferredSize;
        firePropertyChange("preferredSize", oldValue, this.preferredSize);

        this.lblImage.setPreferredSize(new Dimension((int)_preferredSize.getWidth(), (int)_preferredSize.getHeight() - 20));
        this.btnUpdateImage.setPreferredSize(new Dimension((int)_preferredSize.getWidth(), 20));
        super.setPreferredSize(_preferredSize);
    }

    /**
     * Returns the preferred size of the image component.
     *
     * @return returns preferred size of the image component
     */
    @Override
	public Dimension getPreferredSize() {
        return this.preferredSize;
    }

    /**
     * Removes the current image. The image is not removed from the underlying sSRowSet.
     */
    public void clearImage(){
        this.lblImage.setIcon(null);
        this.lblImage.setText("No Picture");
        Dimension dimension = getPreferredSize();
        this.lblImage.setPreferredSize(new Dimension((int)dimension.getWidth(), (int)dimension.getHeight()-20));
        updateUI();
    }

    /**
     * Sets the SSRowSet and column name to which the component is to be bound.
     *
     * @param _sSRowSet    datasource to be used.
     * @param _columnName    Name of the column to which this check box should be bound
     */
    public void bind(SSRowSet _sSRowSet, String _columnName) {
        SSRowSet oldValue = this.sSRowSet;
        this.sSRowSet = _sSRowSet;
        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);

        String oldValue2 = this.columnName;
        this.columnName = _columnName;
        firePropertyChange("columnName", oldValue2, this.columnName);

        bind();
    }

    /**
     * Initialization code.
     */
    protected void init() {

        // ADD UPDATE BUTTON LISTENER
            this.btnUpdateImage.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent ae) {
                    try{
                        if (SSImage.this.sSRowSet != null) {
                            //FileInputStream inStream = null;
                            File inFile = null;
                            JFileChooser fileChooser = new JFileChooser();
                            if(fileChooser.showOpenDialog(SSImage.this.btnUpdateImage) == JFileChooser.APPROVE_OPTION){
                                inFile = fileChooser.getSelectedFile();
                                try (FileInputStream inStream = new FileInputStream(inFile)) {
	                                int totalLength = (int)inFile.length();
	                                byte[] bytes = new byte[totalLength];
	                                int bytesRead = inStream.read(bytes);
	                                while (bytesRead < totalLength){
	                                    int read = inStream.read(bytes, bytesRead, totalLength - bytesRead);
	                                    if(read == -1)
	                                        break;
										bytesRead += read;
	                                }
	                                //inStream.close();
	                                SSImage.this.sSRowSet.updateBytes(SSImage.this.columnName, bytes);
	                                SSImage.this.img = new ImageIcon(bytes);
	                                SSImage.this.lblImage.setPreferredSize(new Dimension(SSImage.this.img.getIconWidth(), SSImage.this.img.getIconHeight()));
	                                SSImage.this.lblImage.setIcon(SSImage.this.img);
	                                SSImage.this.lblImage.setText("");
	                                updateUI();
                                }
                            } else {
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
            setPreferredSize(this.preferredSize);

        // ADD LABEL & BUTTON TO PANEL
            addComponents();
    }

    /**
     *  Adds the label and button to the panel
     */
    protected void addComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy= 0;
        JScrollPane scrollPane = new JScrollPane(this.lblImage, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(200, 180));
        this.btnUpdateImage.setPreferredSize(new Dimension(200,20));
        add(scrollPane, constraints);
        constraints.gridy = 1;
        add(this.btnUpdateImage, constraints);
    }

    /**
     * Method for handling binding of component to a SSRowSet column.
     */
    protected void bind() {

        // CHECK FOR NULL COLUMN/ROWSET
            if (this.columnName==null || this.columnName.trim().equals("") || this.sSRowSet==null) {
                return;
            }

        // REMOVE LISTENERS TO PREVENT DUPLICATION
            removeListeners();

        // UPDATE DISPLAY
            updateDisplay();

        // ADD BACK LISTENERS
            addListeners();
    }

    /**
     * Updates the value displayed in the component based on the SSRowSet column
     * binding.
     */
    protected void updateDisplay() {

        try {
            byte[] imageData = this.sSRowSet.getRow() >0 ? this.sSRowSet.getBytes(this.columnName) : null;
            if(imageData != null){
                this.img = new ImageIcon(imageData);
                this.lblImage.setPreferredSize(new Dimension(this.img.getIconWidth(), this.img.getIconHeight()));
                this.lblImage.setText("");
            } else {
                this.img = null;
                this.lblImage.setText("No Picture");
            }
        } catch(SQLException se) {
            se.printStackTrace();
            this.img = null;
        }

        this.lblImage.setIcon(this.img);
        updateUI();

    }

    /**
     * Adds listeners for component and bound text field (where applicable).
     */
    private void addListeners() {
        this.sSRowSet.addRowSetListener(this.sSRowSetListener);
    }

    /**
     * Removes listeners for component and bound text field (where applicable).
     */
    private void removeListeners() {
        this.sSRowSet.removeRowSetListener(this.sSRowSetListener);
    }

    /**
     *  Listener for the RowSet.
     */
    protected class MyRowSetListener implements RowSetListener {
        @Override
		public void cursorMoved(RowSetEvent rse){
            //System.out.println("Cursor Moved");
            updateDisplay();
        }

        @Override
		public void rowChanged(RowSetEvent rse){
            //System.out.println("Row Changed");
            updateDisplay();
        }

        @Override
		public void rowSetChanged(RowSetEvent rse){
            //System.out.println("RowSet Changed");
            updateDisplay();
        }

    }
}

/*
 *$Log$
 *Revision 1.12  2005/02/21 16:31:33  prasanth
 *In bind checking for empty columnName before binding the component.
 *
 *Revision 1.11  2005/02/13 15:38:20  yoda2
 *Removed redundant PropertyChangeListener and VetoableChangeListener class variables and methods from components with JComponent as an ancestor.
 *
 *Revision 1.10  2005/02/12 03:29:26  yoda2
 *Added bound properties (for beans).
 *
 *Revision 1.9  2005/02/11 22:59:46  yoda2
 *Imported PropertyVetoException and added some bound properties.
 *
 *Revision 1.8  2005/02/11 20:16:05  yoda2
 *Added infrastructure to support property & vetoable change listeners (for beans).
 *
 *Revision 1.7  2005/02/10 20:13:02  yoda2
 *Setter/getter cleanup & method reordering for consistency.
 *
 *Revision 1.6  2005/02/10 03:46:47  yoda2
 *Replaced all setDisplay() methods & calls with updateDisplay() methods & calls to prevent any setter/getter confusion.
 *
 *Revision 1.5  2005/02/10 03:39:17  yoda2
 *Added JavaDoc for class description.
 *
 *Revision 1.4  2005/02/07 22:54:52  yoda2
 *JavaDoc cleanup.
 *
 *Revision 1.3  2005/02/04 22:48:54  yoda2
 *API cleanup & updated Copyright info.
 *
 *Revision 1.2  2005/02/02 23:37:19  yoda2
 *API cleanup.
 *
 *Revision 1.1  2005/01/18 20:59:13  prasanth
 *Initial Commit.
 *
 */
