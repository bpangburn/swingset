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

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import com.nqadmin.swingSet.datasources.SSRowSet;

/**
 * SSTextArea.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * SSTextArea extends the JTextArea to add SSRowSet binding.
 *</pre><p>
 * @author $Author$
 * @version $Revision$
 */
public class SSTextArea extends JTextArea {
    
    /**
     * SSRowSet from which component will get/set values.
     */
    protected SSRowSet rowset;

    /**
     * SSRowSet column to which the component will be bound.
     */
    protected String columnName;

    /**
     * Empty constructor needed for deserialization.
     */
    public SSTextArea() {
		init();
    }

    /**
     * Constructs a new empty SSTextArea with the specified number of rows and columns.    
     *
     * @param _rows     the number of rows >= 0
     * @param _columns     the number of columns >= 0
     */
    public SSTextArea(int _rows, int _columns) {
        super(_rows, _columns);
		//init(); - NO NEED TO SET DIMENSIONS AS THAT IS THE POINT OF THIS CONSTRUCTOR
    }     

    /**
     * Creates a multi-line text box and binds it to the specified SSRowSet column.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    name of the column to which this text area should be bound
     */
    public SSTextArea(SSRowSet _rowset, String _columnName) {
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
            setPreferredSize(new Dimension(200,80));
    }

    /**
     * Returns the SSRowSet column name to which the component is bound.
     *
     * @return column name to which the component is bound
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Returns the SSRowSet to which the component is bound.
     *
     * @return SSRowSet to which the component is bound
     */
    public SSRowSet getSSRowSet() {
        return rowset;
    }

    /**
     * Sets the SSRowSet column name to which the component is bound.
     *
     * @param _columnName    column name in the SSRowSet to which the component
     *    is bound
     */
    public void setColumnName(String _columnName) {
        columnName = _columnName;
        bind();
    }

    /**
     * Sets the SSRowSet to which the component is bound.
     *
     * @param _rowset    SSRowSet to which the component is bound
     */
    public void setSSRowSet(SSRowSet _rowset) {
        rowset = _rowset;
        bind();
    }

    /**
     * Method for handling binding of component to a SSRowSet column.
     */
    protected void bind() {

        // CHECK FOR NULL COLUMN/ROWSET
            if (columnName==null || rowset==null) {
                return;
            }

        // REMOVE LISTENERS TO PREVENT DUPLICATION
        //    removeListeners();

        // BIND THE TEXT AREA TO THE SPECIFIED COLUMN
            setDocument(new SSTextDocument(rowset, columnName));

        // ADD BACK LISTENERS
        //    addListeners();;

    }    

    /**
     * Sets the SSRowSet and column name to which the component is to be bound.
     *
     * @param _rowset    datasource to be used.
     * @param _columnName    Name of the column to which this check box should be bound
     */
    public void bind(SSRowSet _rowset, String _columnName) {
        rowset = _rowset;
        columnName = _columnName;
        bind();
    }

} // end public class SSTextArea extends JTextArea {



/*
 * $Log$
 * Revision 1.7  2005/02/07 22:20:10  yoda2
 * Added constructor to set component dimensions.
 *
 * Revision 1.6  2005/02/05 05:16:33  yoda2
 * API cleanup.
 *
 * Revision 1.5  2005/02/04 22:48:54  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.4  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.3  2004/10/25 22:13:43  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.2  2004/10/25 19:51:03  prasanth
 * Modified to use the new SSRowSet instead of  RowSet.
 *
 * Revision 1.1  2004/10/01 20:43:53  yoda2
 * Added SSTextArea which is a simple extension of JTextArea with support for rowset binding.
 *
 */