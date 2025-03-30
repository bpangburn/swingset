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


import java.sql.SQLException;
import java.util.List;

import javax.sql.RowSet;
import javax.swing.JFrame;

import com.nqadmin.swingset.SSDataGrid;
import com.nqadmin.swingset.navigate.RowsModel;

/**
 *
 */
@SuppressWarnings("serial")
public class DataGridSnippets extends JFrame
{
	SSDataGrid dataGrid;
	RowSet rowSet;
	RowsModel rowsModel;

	// @start region=init1
	/**
	 * Set up a data grid with customizations for some known table.
	 * @param dataGrid initialize this data grid
	 * @throws SQLException 
	 */
	void init(SSDataGrid dataGrid) throws SQLException {
		// Set the header before setting the RowsModel.
		dataGrid.setHeaders(new String[]{"Part Name", "Color Code", " Weight", "City"});
		dataGrid.setRowsModel(rowsModel);
		
		// HIDE THE PART ID COLUMN
		dataGrid.setHiddenColumnsByName(List.of("part_id"));
		
		dataGrid.setMessageWindow(this);
		dataGrid.setUneditableColumns(new String[]{"part_id"});
		
		dataGrid.setComboRenderer("color_code",
								  new String[]{"Red","Green","Blue"},
								  new Integer[]{0, 1, 2});
		dataGrid.setDefaultValues(new int[]{1, 2, 3},
								  new Object[]{0, 20, "New Orleans"});
		
		dataGrid.setPrimaryColumn("part_id");

		// Setup the primary key generation as needed.
		dataGrid.setSSDataValue(() -> {
			Integer key = null;
			
			// your primary key value generation goes here.
			// If it's something the user enters then no problem.
			// If it's an auto increment field then it depends on
			// the database driver you are using.
			// If the updaterow can retrieve the values for the row
			// without knowing the primary key value its fine.
			
			return key;
		});
	}
	// @end region=init1
}
