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

package com.nqadmin.swingset.formatting.helpers;

import java.util.List;

import ca.odell.glazedlists.TextFilterable;

/**
 * SelectorElement.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * An item and corresponding underlying database value that can be compared to a
 * list of filters to see if it matches.
 * 
 * @see "https://docs.oracle.com/javase/8/docs/api/javax/swing/ComboBoxModel.html"
 * @see "https://github.com/glazedlists/glazedlists/blob/master/core/src/main/java/ca/odell/glazedlists/TextFilterable.java"
 */
public class SelectorElement implements TextFilterable {

	/**
	 * Holds value of property listValue.
	 */
	private Object listValue = null;

	/**
	 * Utility field used by bound properties.
	 */
	private java.beans.PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);

	/**
	 * Holds value of property dataValue.
	 */
	private Object dataValue = null;

	/**
	 * Creates a default instance of SelectorElement
	 */
	public SelectorElement() {
	}

	/**
	 * Creates an instance of SelectorElement with the given listValue & dataValue
	 * 
	 * @param _dataValue - underlying value of the object displayed
	 * @param _listValue - value to be used for display purpose
	 */
	public SelectorElement(final Object _dataValue, final Object _listValue) {
		this.setDataValue(_dataValue);
		this.setListValue(_listValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (this.listValue != null)
			return this.listValue.toString().trim();
		return null;
	}

	/**
	 * Adds a PropertyChangeListener to the listener list.
	 * 
	 * @param l The listener to add.
	 */
	public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {

		this.propertyChangeSupport.addPropertyChangeListener(l);
	}

	/**
	 * Removes a PropertyChangeListener from the listener list.
	 * 
	 * @param l The listener to remove.
	 */
	public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {

		this.propertyChangeSupport.removePropertyChangeListener(l);
	}

	/**
	 * Getter for property listValue.
	 * 
	 * @return Value of property listValue.
	 */
	public Object getListValue() {

		return this.listValue;
	}

	/**
	 * Setter for property listValue.
	 * 
	 * @param _listValue New value of property listValue.
	 */
	public void setListValue(final Object _listValue) {
		Object oldListValue = this.listValue;
		this.listValue = _listValue;
		this.propertyChangeSupport.firePropertyChange("listValue", oldListValue, _listValue);
	}

	/**
	 * Getter for property dataValue.
	 * 
	 * @return Value of property dataValue.
	 */
	public Object getDataValue() {
		return this.dataValue;
	}

	/**
	 * Setter for property dataValue.
	 * 
	 * @param _dataValue New value of property dataValue.
	 */
	public void setDataValue(final Object _dataValue) {
		Object olddataValue = this.dataValue;
		this.dataValue = _dataValue;
		this.propertyChangeSupport.firePropertyChange("dataValue", olddataValue, _dataValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.odell.glazedlists.TextFilterable#getFilterStrings(java.util.List)
	 */
	@Override
	public void getFilterStrings(List<String> _list) {
		_list.add((String) this.listValue);
	}

}

/*
 * $Log$ Revision 1.9 2013/08/02 20:31:33 prasanth Modified toString to return
 * null when listValue is null.
 *
 * Revision 1.8 2006/05/15 15:50:09 prasanth Updated javadoc
 *
 * Revision 1.7 2006/04/21 19:09:17 prasanth Added CVS tags & some comments
 *
 */
