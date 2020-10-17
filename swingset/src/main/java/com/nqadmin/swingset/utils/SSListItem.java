/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 *   Ernie R. Rael
 ******************************************************************************/
package com.nqadmin.swingset.utils;

// SSListItem.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Class for items to be displayed in list and combobox components.
 * Compatible with GlazedLists AutoComplete feature.
 * <p>
 * See https://publicobject.com/glazedlistsdeveloper/screencasts/autocompletesupport/
 */
public class SSListItem {

	/**
	 * String item to display (generally in a combobox or other list).
	 */
	private String listItem;
	
	/**
	 * Record primary key value corresponding to list item displayed.
	 * Generally used to store a foreign key in a related table.
	 */
	private Long primaryKey;

	/**
	 * Constructs an object to be stored in a list or combobox component
	 *
	 * @param _primaryKey Primary Key/index for the new list item
	 * @param _listItem String description for the new list item
	 */
	public SSListItem(final Long _primaryKey, final String _listItem) {
		super();
		primaryKey = _primaryKey;
		listItem =_listItem;
	}

	/**
	 * Returns the string to display for the current list item.
	 * 
	 * @return the String to display for the current list item
	 */
	public String getListItem() {
		return listItem;
	}
	/**
	 * Returns the primary key value associated with the current list item.
	 * 
     * @return the Primary Key/index for the current list item
     */
    public Long getPrimaryKey() {
		return primaryKey;
	}

    /**
	 * Sets the string to display for the current list item.
	 * 
	 * @param _listItem String description for the list item
	 */
	public void setListItem(final String _listItem) {
		listItem = _listItem;
	}

	/**
	 * Sets the primary key value associated with the current list item.
	 * 
	 * @param _primaryKey Primary Key/index for the current list item
	 */
	public void setPrimaryKey(final Long _primaryKey) {
		primaryKey = _primaryKey;
	}


	/**
	 * String representation of list item.
	 */
	@Override
	public String toString() {
		return listItem;
	}

}
