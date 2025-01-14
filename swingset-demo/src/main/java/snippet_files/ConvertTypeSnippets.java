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
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.nqadmin.swingset.datasources.ConvertType.castJDBCToJava;

/**
 *
 */
public class ConvertTypeSnippets
{
	void castArray() throws SQLException {
		// @start region=convert_array
		Object[] arr = f(); // But I "know" the elements are Integer
		Integer[] newarr = (Integer[]) castJDBCToJava(JDBCType.INTEGER, arr);
		List<Integer> properList = Arrays.asList(newarr);
		// @end region=convert_array
	}

	Object[] f(){
		return null;
	}
}
