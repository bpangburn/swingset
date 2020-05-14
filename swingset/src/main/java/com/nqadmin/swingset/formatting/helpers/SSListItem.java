/**
 * class for items to be displayed in lists and comboboxes
 * 
 * thought about having data member for primary key, but want to replicate 
 * data structure presented here: https://publicobject.com/glazedlistsdeveloper/screencasts/autocompletesupport/
 * which needs a valueOf(String s) method
 */
package com.nqadmin.swingset.formatting.helpers;

/**
 * @author brian
 *
 */
public class SSListItem {
	
	private Long primaryKey;
	private String listItem;

		
	public SSListItem(Long _primaryKey, String _listItem) {
		super();
		this.primaryKey = _primaryKey;
		this.listItem =_listItem;
	}

	public String getListItem() {
		return listItem;
	}
	public void setListItem(String _listItem) {
		this.listItem = _listItem;
	}
	
    public Long getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Long _primaryKey) {
		this.primaryKey = _primaryKey;
	}

	/*
	 * public static SSListItem valueOf(String s) { return new SSListItem(s); }
	 */
	
	@Override
	public String toString() {
		return listItem;
	}

}
