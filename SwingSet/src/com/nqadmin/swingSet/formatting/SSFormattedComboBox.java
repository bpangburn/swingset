/*
 * SSFormattedComboBox.java
 *
 * Created on 1 de abril de 2005, 10:19
 */

package com.nqadmin.swingSet.formatting;

import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.datasources.SSRowSet;
import com.nqadmin.swingSet.formatting.helpers.SSFormattedComboBoxEditor;
import com.nqadmin.swingSet.formatting.helpers.SSFormattedComboBoxRenderer;
import com.nqadmin.swingSet.formatting.helpers.SSFormattedComboBoxModel;
import java.awt.Color;

import java.awt.event.ActionListener;
import javax.swing.ComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;

/**
 *
 * @author dags
 */
public class SSFormattedComboBox extends JComboBox implements ActionListener {
    
    protected SSFormattedComboBoxEditor editor = null;
    protected SSFormattedComboBoxRenderer renderer = null;
    
    private SSFormattedComboBoxModel model = null;
    
    private Object dataValue = null;
    private Object listValue = null;
    
    private SSDataNavigator navigator = null;
    private SSRowSet rowset   = null;
    private SSConnection connection = null;
    private String table      = null;
    private String dataColumn = null;
    private String listColumn = null;
    private String orderBy    = null;
    private String query      = null;
    private int colType       = 0;
    
    /** Creates a new instance of SSFormattedComboBox */
    public SSFormattedComboBox() {
        this(new SSFormattedComboBoxModel());
    }
    
    public SSFormattedComboBox(ComboBoxModel model) {
        super(model);
        this.model = (SSFormattedComboBoxModel)this.getModel();
        
        editor = new SSFormattedComboBoxEditor();
        this.setEditor(editor);
        
        renderer = new SSFormattedComboBoxRenderer();
        this.setRenderer(renderer);
        
        this.setEditable(true);
    }
    
    public void setOddRowBackground(Color color) {
        this.renderer.setOddRowBackground(color);
    }
    
    public void setEvenRowBackground(Color color) {
        this.renderer.setEvenRowBackground(color);
    }
    
    public void setOddRowForeground(Color color) {
        this.renderer.setOddRowForeground(color);
    }
    
    public void setEvenRowForeground(Color color) {
        this.renderer.setEvenRowForeground(color);
    }
    
    public Color getOddRowBackground() {
        return renderer.getOddRowBackground();
    }
    
    public Color getEvenRowBackground() {
        return renderer.getEvenRowBackground();
    }
    
    public Color getOddRowForeground() {
        return renderer.getOddRowForeground();
    }
    
    public Color getEvenRowForeground() {
        return renderer.getEvenRowForeground();
    }
    
    public void setListColumn(String listColumn) {
        this.listColumn = listColumn;
        model.setListColumn(listColumn);
    }
    
    public String getListColumn() {
        return listColumn;
    }
    
    public void setDataColumn(String dataColumn) {
        this.dataColumn = dataColumn;
        model.setDataColumn(dataColumn);
    }
    
    public String getDataColumn() {
        return dataColumn;
    }
    
    public void setTable(String table) {
        this.table = table;
        model.setTable(table);
    }
    
    public String getTable() {
        return table;
    }
    
    public void setNavigator(SSDataNavigator navigator) {
        this.navigator = navigator;
        editor.setNavigator(navigator);
    }
    
    public SSDataNavigator getNavigator() {
        return navigator;
    }
    
    public void setSSRowSet(SSRowSet rowset) {
        this.rowset = rowset;
        editor.setSSRowSet(rowset);
    }
    
    public SSRowSet getSSRowSet() {
        return rowset;
    }
    
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        model.setOrderBy(orderBy);
    }
    
    public String getOrderBy() {
        return orderBy;
    }
    
    public void setColumnType(int colType) {
        this.colType = colType;
    }
    
    public int getColumnType() {
        return colType;
    }
    
    public void setConnection(SSConnection connection) {
        this.connection = connection;
        model.setSsConnection(connection);
    }
    
    public SSConnection getConnection() {
        return connection;
    }
    
    public void configureEditor(javax.swing.ComboBoxEditor anEditor, Object anItem) {
        if (anEditor instanceof SSFormattedComboBoxEditor) {
            ((SSFormattedComboBoxEditor) anEditor).setColumnName(dataColumn);
            ((SSFormattedComboBoxEditor) anEditor).setComboBox(this);
        }
        anEditor.setItem(anItem);
    }
    
    public InputVerifier getInputVerifier() {
        InputVerifier retValue;
        System.out.println("SSFormattedComboBox.getInputVerifier()");
        retValue = editor.getEditorField().getInputVerifier();
        return retValue;
    }
}
