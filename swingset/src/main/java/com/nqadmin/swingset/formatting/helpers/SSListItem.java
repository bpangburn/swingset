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
 ******************************************************************************/

package com.nqadmin.swingset.formatting.helpers;

/**
 * SSListItem.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Class for items to be displayed in list and combobox components.
 * Compatible with GlazedLists AutoComplete feature.
 * 
 * See https://publicobject.com/glazedlistsdeveloper/screencasts/autocompletesupport/
 */
public class SSListItem {
	
	private Long primaryKey;
	private String listItem;

		
	/**
	 * Constructs an object to be stored in a list or combobox component
	 * 
	 * @param _primaryKey Primary Key/index for the new list item
	 * @param _listItem String description for the new list item
	 */
	public SSListItem(Long _primaryKey, String _listItem) {
		super();
		this.primaryKey = _primaryKey;
		this.listItem =_listItem;
	}

	/**
	 * @return the String description for the current list item
	 */
	public String getListItem() {
		return listItem;
	}
	/**
	 * @param _listItem String description for the list item
	 */
	public void setListItem(String _listItem) {
		this.listItem = _listItem;
	}
	
    /**
     * @return the Primary Key/index for the current list item
     */
    public Long getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @param _primaryKey Primary Key/index for the current list item
	 */
	public void setPrimaryKey(Long _primaryKey) {
		this.primaryKey = _primaryKey;
	}


	@Override
	public String toString() {
		return listItem;
	}

}
