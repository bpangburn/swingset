/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004-2006, The Pangburn Company, Prasanth R. Pasala and
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

package com.nqadmin.swingSet.formatting.helpers;

import ca.odell.glazedlists.TextFilterable;

/**
 *
 * @author  dags
 */
public class SelectorElement implements TextFilterable {
    
    /**
     * Holds value of property listValue.
     */
    private Object listValue = null;
    
    /**
     * Utility field used by bound properties.
     */
    private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);
    
    /**
     * Holds value of property dataValue.
     */
    private Object dataValue = null;
    
    /**
     * Creates a default instance of SelectorElement
     */
    public SelectorElement() {
        listValue = new String("listValue");
        dataValue = new String("dataValue");
    }
    
    /**
     * Creates an instance of SelectorElement with the given listValue & dataValue
     * @param dataValue - underlying value of the object displayed
     * @param listValue - value to be used for display purpose
     */
    public SelectorElement(Object dataValue, Object listValue) {
        this.setDataValue(dataValue);
        this.setListValue(listValue);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return listValue.toString().trim();
    }
    
    /**
     * Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        
        propertyChangeSupport.addPropertyChangeListener(l);
    }
    
    /**
     * Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        
        propertyChangeSupport.removePropertyChangeListener(l);
    }
    
    /**
     * Getter for property listValue.
     * @return Value of property listValue.
     */
    public Object getListValue() {
        
        return this.listValue;
    }
    
    /**
     * Setter for property listValue.
     * @param listValue New value of property listValue.
     */
    public void setListValue(Object listValue) {
        Object oldListValue = this.listValue;
        this.listValue = listValue;
        propertyChangeSupport.firePropertyChange("listValue", oldListValue, listValue);
    }
    
    /**
     * Getter for property dataValue.
     * @return Value of property dataValue.
     */
    public Object getDataValue() {
        return this.dataValue;
    }
    
    /**
     * Setter for property dataValue.
     * @param dataValue New value of property dataValue.
     */
    public void setDataValue(Object dataValue) {
        Object olddataValue = this.dataValue;
        this.dataValue = dataValue;
        propertyChangeSupport.firePropertyChange("dataValue", olddataValue, dataValue);
    }
    
    /* (non-Javadoc)
     * @see ca.odell.glazedlists.TextFilterable#getFilterStrings(java.util.List)
     */
    public void getFilterStrings(java.util.List list) {
        list.add(listValue.toString());
        list.add(dataValue.toString());
    }
}

/*
* $Log$
* Revision 1.7  2006/04/21 19:09:17  prasanth
* Added CVS tags & some comments
*
*/