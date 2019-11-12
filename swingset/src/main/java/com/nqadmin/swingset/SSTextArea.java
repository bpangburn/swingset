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

import javax.swing.JTextArea;

import com.nqadmin.swingset.datasources.SSRowSet;

/**
 * SSTextArea.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * SSTextArea extends the JTextArea to add SSRowSet binding.
 */
public class SSTextArea extends JTextArea {

    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = -1256528482424744463L;

	/**
     * SSRowSet from which component will get/set values.
     */
    protected SSRowSet sSRowSet;

    /**
     * SSRowSet column to which the component will be bound.
     */
    protected String columnName = "";

    /**
     * Empty constructor needed for deserialization.
     */
    public SSTextArea() {

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
     * @param _sSRowSet    datasource to be used.
     * @param _columnName    name of the column to which this text area should be bound
     */
    public SSTextArea(SSRowSet _sSRowSet, String _columnName) {
		this.sSRowSet = _sSRowSet;
        this.columnName = _columnName;
        bind();
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
     * Method for handling binding of component to a SSRowSet column.
     */
    protected void bind() {

        // CHECK FOR NULL COLUMN/ROWSET
            if (this.columnName==null || this.columnName.trim().equals("") || this.sSRowSet==null) {
                return;
            }

        // REMOVE LISTENERS TO PREVENT DUPLICATION
        //    removeListeners();

        // BIND THE TEXT AREA TO THE SPECIFIED COLUMN
            setDocument(new SSTextDocument(this.sSRowSet, this.columnName));

        // ADD BACK LISTENERS
        //    addListeners();;

    }


} // end public class SSTextArea extends JTextArea {

/*
 * $Log$
 * Revision 1.15  2006/05/15 16:10:38  prasanth
 * Updated copy right
 *
 * Revision 1.14  2005/02/21 16:31:33  prasanth
 * In bind checking for empty columnName before binding the component.
 *
 * Revision 1.13  2005/02/13 15:38:20  yoda2
 * Removed redundant PropertyChangeListener and VetoableChangeListener class variables and methods from components with JComponent as an ancestor.
 *
 * Revision 1.12  2005/02/12 03:29:26  yoda2
 * Added bound properties (for beans).
 *
 * Revision 1.11  2005/02/11 22:59:46  yoda2
 * Imported PropertyVetoException and added some bound properties.
 *
 * Revision 1.10  2005/02/11 20:16:06  yoda2
 * Added infrastructure to support property & vetoable change listeners (for beans).
 *
 * Revision 1.9  2005/02/10 20:13:03  yoda2
 * Setter/getter cleanup & method reordering for consistency.
 *
 * Revision 1.8  2005/02/09 19:06:06  yoda2
 * JavaDoc cleanup.
 *
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
 * Added SSTextArea which is a simple extension of JTextArea with support for sSRowSet binding.
 *
 */
