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

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;

import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.datasources.SSRowSet;
import com.nqadmin.swingSet.formatting.helpers.SSFormattedComboBoxEditor;
import com.nqadmin.swingSet.formatting.helpers.SSFormattedComboBoxModel;
import com.nqadmin.swingSet.formatting.helpers.SSFormattedComboBoxRenderer;

/**
 * SSFormattedComboBox.java
 *
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Used to link a JComboBox to a database column.
 */
public class SSFormattedComboBox extends JComboBox<Object> implements ActionListener {
    
    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = 8096912066245475037L;
	protected SSFormattedComboBoxEditor editor = null;
    protected SSFormattedComboBoxRenderer renderer = null;
    
    private SSFormattedComboBoxModel model = null;
    
    private SSDataNavigator navigator = null;
    private SSRowSet rowset   = null;
    private SSConnection connection = null;
    private String table      = null;
    private String dataColumn = null;
    private String listColumn = null;
    private String orderBy    = null;
    private int colType       = 0;
    
    /** Creates a new instance of SSFormattedComboBox */
    public SSFormattedComboBox() {
        this(new SSFormattedComboBoxModel());
    }
    
    /**
     * @param model
     */
    public SSFormattedComboBox(ComboBoxModel<Object> model) {
        super(model);
        this.model = (SSFormattedComboBoxModel)this.getModel();
        
        editor = new SSFormattedComboBoxEditor();
        this.setEditor(editor);
        
        renderer = new SSFormattedComboBoxRenderer();
        this.setRenderer(renderer);
        
        this.setEditable(true);
    }
    
    /**
     * Sets the background color for odd rows
     * @param color - background color to be used for odd rows
     */
    public void setOddRowBackground(Color color) {
        this.renderer.setOddRowBackground(color);
    }
    
    /**
     * Sets the background color for even rows
     * @param color - background color to be used for even rows
     */
    public void setEvenRowBackground(Color color) {
        this.renderer.setEvenRowBackground(color);
    }
    
    /**
     * Sets the foreground color for odd rows
     * @param color - foreground color to be used for odd rows
     */
    public void setOddRowForeground(Color color) {
        this.renderer.setOddRowForeground(color);
    }
    
    /**
     * Sets the foreground color for even rows
     * @param color - foreground color to be used for even rows
     */
    public void setEvenRowForeground(Color color) {
        this.renderer.setEvenRowForeground(color);
    }
    
    /**
     * Returns the background color used for odd rows
     * @return - returns the background color used for odd rows
     */
    public Color getOddRowBackground() {
        return renderer.getOddRowBackground();
    }
    
    /**
     * Returns the background color used for even rows
     * @return - returns the background color used for even rows
     */
    public Color getEvenRowBackground() {
        return renderer.getEvenRowBackground();
    }
    
    /**
     * Returns the foreground color used for odd rows
     * @return - returns the foreground color used for odd rows
     */
    public Color getOddRowForeground() {
        return renderer.getOddRowForeground();
    }
    
    /**
     * Returns the foreground color used for even rows
     * @return - returns the foreground color used for even rows
     */
    public Color getEvenRowForeground() {
        return renderer.getEvenRowForeground();
    }
    
    /**
     * Sets the column name whose values should be displayed in the combo box
     * @param listColumn - column name whose values should be displayed in the combo box
     */
    public void setListColumn(String listColumn) {
        this.listColumn = listColumn;
        model.setListColumn(listColumn);
    }
    
    /**
     * Returns the name of the column whose values are displayed as combo box items
     * @return - returns the name of the column whose values are displayed as combo box items
     */
    public String getListColumn() {
        return listColumn;
    }
    
    /**
     * Sets the column name whose values are used as the underlying values for the displayed items
     * @param dataColumn - column name whose values are used as the underlying values for the displayed items
     */
    public void setDataColumn(String dataColumn) {
        this.dataColumn = dataColumn;
        model.setDataColumn(dataColumn);
    }
    
    /**
     * Retuns the data column name
     * @return - returns the data column name used in the query.
     */
    public String getDataColumn() {
        return dataColumn;
    }
    
    /**
     * Sets the table name from which data should be pulled
     * @param table - database table name to be used
     */
    public void setTable(String table) {
        this.table = table;
        model.setTable(table);
    }
    
    /**
     * Returns the database table name being used 
     * @return returns the database table name being used
     */
    public String getTable() {
        return table;
    }
    
    /**
     * Sets the SSDataNavigator used for navigating the SSRowSet
     * @param navigator - SSDataNavigator instance used for navigating the SSRowSet
     * @deprecated Use {@link #setSSDataNavigator()} instead.  
     */
    @Deprecated
    public void setNavigator(SSDataNavigator navigator) {
        this.navigator = navigator;
        editor.setNavigator(navigator);
    }
    
    /**
     * Returns the SSDataNavigator object being used
     * @return returns the SSDataNavigator object being used
     * @deprecated Use {@link #getSSDataNavigator()} instead. 
     */
    @Deprecated
    public SSDataNavigator getNavigator() {
        return navigator;
    }
    
    /**
     * Sets the SSDataNavigator used for navigating the SSRowSet
     * @param navigator - SSDataNavigator instance used for navigating the SSRowSet
     */
    public void setSSDataNavigator(SSDataNavigator navigator) {
        this.navigator = navigator;
        editor.setSSRowSet(navigator.getSSRowSet());
    }
    
    /**
     * Returns the SSDataNavigator object being used
     * @return returns the SSDataNavigator object being used
     */
    public SSDataNavigator getSSDataNavigator() {
        return this.navigator;
    }
    
    /**
     * Sets the SSRowSet to which the component is bound
     * @param rowset - SSRowSet object to which the component is bound 
     */
    public void setSSRowSet(SSRowSet rowset) {
        this.rowset = rowset;
        editor.setSSRowSet(rowset);
    }
    
    /**
     * Returns the SSRowSet object to which the component is bound
     * @return returns the SSRowSet object to which the component is bound
     */
    public SSRowSet getSSRowSet() {
        return rowset;
    }
    
    /**
     * Sets the column name by which the data has to be ordered
     * @param orderBy - column name based on which data should be ordered
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        model.setOrderBy(orderBy);
    }
    
    /**
     * The column name used for ordering the data
     * @return returns the column name used for ordering the data
     */
    public String getOrderBy() {
        return orderBy;
    }
    
    /**
     * Sets the type of columns to which this component is bound to.
     * @param colType - column type of the bound column
     */
    public void setColumnType(int colType) {
        this.colType = colType;
    }
    
    /**
     * Returns the column type of the bound column
     * @return - returns the column type of the bound column
     */
    public int getColumnType() {
        return colType;
    }
    
    /**
     * Database connection to be used for executing the query
     * @param connection - SSConnection object to be used for querying the database
     * @deprecated Use {@link #setSSConnection()} instead. 
     */
    @Deprecated
    public void setConnection(SSConnection connection) {
        this.setSSConnection(connection);
    }
    
    /**
     * Database connection used for executing the query
     * @return connection - SSConnection object used for querying the database
     * @deprecated Use {@link #getSSConnection()} instead. 
     */
    @Deprecated
    public SSConnection getConnection() {
        return this.getSSConnection();
    }
    
    /**
     * Database connection to be used for executing the query
     * @param connection - SSConnection object to be used for querying the database
     */
    public void setSSConnection(SSConnection connection) {
        this.connection = connection;
        model.setSSConnection(connection);
    }
    
    /**
     * Database connection used for executing the query
     * @return connection - SSConnection object used for querying the database
     */
    public SSConnection getSSConnection() {
        return this.connection;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComboBox#configureEditor(javax.swing.ComboBoxEditor, java.lang.Object)
     */
    public void configureEditor(javax.swing.ComboBoxEditor anEditor, Object anItem) {
        if (anEditor instanceof SSFormattedComboBoxEditor) {
            ((SSFormattedComboBoxEditor) anEditor).setColumnName(dataColumn);
            ((SSFormattedComboBoxEditor) anEditor).setComboBox(this);
        }
        anEditor.setItem(anItem);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#getInputVerifier()
     */
    public InputVerifier getInputVerifier() {
        InputVerifier retValue;
        retValue = editor.getEditorField().getInputVerifier();
        return retValue;
    }
    
}