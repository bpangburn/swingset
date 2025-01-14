/*
 * Portions created by Ernie Rael are
 * Copyright (C) 2025 Ernie Rael.  All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Ernie Rael <errael@raelity.com>
 */
package snippet_files;

import java.util.ArrayList;

import javax.swing.ComboBoxModel;

import com.nqadmin.swingset.models.SimpleComboListSwingModel;

/**
 *
 */
public class SimpleComboListSwingModelsSnippets
{
	// items[x] is displayed, the index 'x' is the value.
	Object[] items;
	// outer class does
	ComboBoxModel<MyComboModel.MyComboItem> model
			= new MyComboModel().getComboModel();
	
	// @start region=init1
	/**
	 * Simple combox model that maps each combobox item, "items[index]",
	 * to the index into this ComboEditor's items array.
	 * Given
	 *		sel = combo.getSelectedItem();
	 *		// sel.toString(), aka sel.getElem(0), is displayed in the combo box.
	 *		// sel.getElem(1) is it's value in range [0-N).
	 */
	private final class MyComboModel extends SimpleComboListSwingModel
	{
		// There is one MyComboItem for each item in MyComboModel.
		private MyComboModel() {
			super(2, new ArrayList<>(items.length));
			// initialization
			for (int i = 0; i < items.length; i++) {
				getRemodel().add(new MyComboItem(i));
			}
		}
		
		public ComboBoxModel<MyComboItem> getComboModel() {
			return (ComboBoxModel<MyComboItem>) super.getComboModel();
		}
		
		// The display data for a MyComboItem is backed by items[].
		private class MyComboItem implements ListItem0, Cloneable
		{
			private final int listIdx;
			
			public MyComboItem(int listIdx) { this.listIdx = listIdx; }
			// ...
			
			// This ListItem's getElem(0) from items, getElem(1) is it's index.
			@Override
			public Object getElem(int index) {
				return index == 0 ? items[listIdx] : listIdx;
			}
			@Override public String toString() { return getElem(0).toString(); }
			@Override public Object clone() throws CloneNotSupportedException {
				return super.clone();
			}
		}
	}
	// @end region=init1
	
}
