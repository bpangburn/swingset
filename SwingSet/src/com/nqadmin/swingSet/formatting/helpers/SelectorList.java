/*
 * SelectorList.java
 *
 * Created on 17 de diciembre de 2004, 15:23
 */

package com.nqadmin.swingSet.formatting.selectors;

import javax.swing.*;
import java.sql.*;
import com.nqadmin.swingSet.formatting.selectors.*;
/**
 *
 * @author  dags
 */
public class SelectorList extends JList {
    
    /**
     * Creates a new instance of SelectorList
     */
    
    public SelectorList() {
        super(new SelectorListModel());
    }
    
    public SelectorList(SelectorListModel model) {
        this.setModel(model);
    }
    
    private void Init() {
    }
    
    /**
     * Getter for property dataValue.
     * @return Value of property dataValue.
     */
    public Object getDataValue() {
        return ((SelectorListModel)getModel()).getSelectedBoundData(this.getSelectedIndex());
    }
}
