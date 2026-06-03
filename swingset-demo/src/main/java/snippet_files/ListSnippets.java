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

import java.sql.JDBCType;
import java.util.List;

import com.nqadmin.swingset.SSList;
import com.nqadmin.swingset.navigate.RowsModel;

/**
 * xxx
 */
public class ListSnippets {
	SSList list;
	RowsModel rowsModel;

	// @start region=init1
	/**
	 * Create an SSList, initialize its contents,
	 * bind the list selection to a column in the RowsModel.
	 */
	@SuppressWarnings("unused")
	void init() {
		list = new SSList(JDBCType.DOUBLE);
		List<String> options = List.of("VLarge", "large", "medium", "small", "VSmall");
		List<Object> mappings = List.of(100.0, 10.0, 5.0, 1.0, 0.1);
		list.setDisplayValues(options, mappings);
		rowsModel.bind(list, "my_column");
	}
	// @end region=init1
}
