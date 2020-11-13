/*
 * Portions created by Ernie Rael are
 * Copyright (C) 2020 Ernie Rael.  All Rights Reserved.
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
 * The Original Code is jvi - vi editor clone.
 *
 * Contributor(s): Ernie Rael <errael@raelity.com>
 */

package com.nqadmin.swingset.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.AbstractListModel;

/**
 * The options and mappings for a {@link com.nqadmin.swingset.SSList}
 * <p>
 * _options is the visible strings in the list.
 * <p>
 * _mappings is the underlying values for each of the items in the
 * list box (e.g. the values that map to the items displayed in the list box).
 * Values from _mappings are what get read/written to the database.
 * <p>
 * The mappings is not required, in which case the mapping is the index;
 * note that in this case the generic mapping type, M, must be Object or Integer.
 */
// TODO: get the mapping type at runtime for verifiction if null _mappings.
// http://download.oracle.com/javase/6/docs/api/java/lang/reflect/ParameterizedType.html#getActualTypeArguments%28%29
// Can use guava, if present in SS; see example at end of this file.
@SuppressWarnings(value = "serial")
public class OptionMappingSwingListModel<M> extends AbstractListModel<String>
{
	/**
	 * If there are more items than "CALL_IT_BIG",
	 * then create a hashmap for _mapping object to list index.
	 */
	// TODO: API for CALL_IT_BIG, or forcing wantMappingToIndex on/off
	private static int CALL_IT_BIG = 50;
	/** options */
	final private List<String> options;
	/** mappings */
	final private List<M> mappings;

	/**
	 * Optional map of mapping to index, may be null.
	 * This can be created if there are a large number of mappings
	 * to minimize performance overhead of database value to selected index.
	 */
	final private Map<M, Integer> mappingToIndex;

	/**
	 * This version has a _mapping to the database values
	 * zero to N-1.
	 * @param _options The visible labels in the JList
	 */
	public OptionMappingSwingListModel(String[] _options) {
		this(_options, null);
	}

	/**
	 * Create a JList model used with multi selction. The selected
	 * items is determined from, and saved to, the database.
	 * There must be a one to one correspondence
	 * between {@code _options} and {@code _mappings}
	 * @param _options The visible labels in the JList
	 * @param _mappings The database values corresponding to the _options,
	 *		null is zero to N-1 mapping.
	 */
	public OptionMappingSwingListModel(String[] _options, M[] _mappings) {
		Objects.requireNonNull(_options);
		boolean wantMappingToIndex = _options.length > CALL_IT_BIG;
		if (_mappings != null && _options.length != _mappings.length) {
			throw new IllegalArgumentException("options and mappings must be the same length");
		}
		options = Arrays.asList(Arrays.copyOf(_options, _options.length));
		mappings = _mappings == null ? null
				: Arrays.asList(Arrays.copyOf(_mappings, _mappings.length));
		mappingToIndex = wantMappingToIndex ? new HashMap<>() : null;
		if (wantMappingToIndex) {
			for (int i = 0; i < _options.length; i++) {
				@SuppressWarnings({"unchecked", "UnnecessaryBoxing"})
				M mapping = _mappings != null ? _mappings[i] : (M)Integer.valueOf(i);
				mappingToIndex.put(mapping, i);
			}
		}
	}

	/**
	 *
	 * @return list of options
	 */
	public List<String> getOptions() {
		return Collections.unmodifiableList(options);
	}

	/**
	 *
	 * @return list of mappings
	 */
	public List<M> getMappings() {
		return Collections.unmodifiableList(mappings);
	}

	/**
	 * Determine the index in the model of the given mapping.
	 * @param _mapping mapping of which the index is wanted
	 * @return return index of the mapping
	 */
	@SuppressWarnings("null")
	public int getMappingToIndex(M _mapping) {
		Objects.requireNonNull(_mapping);
		return mappingToIndex != null
				? mappingToIndex.get(_mapping) : mappings.indexOf(_mapping);
	}

	/**
	 * Get mapping for JList item at index. Returned items typically
	 * are propagated to database.
	 * @param index index into JList
	 * @return mapping for item at index
	 */
	public M getMappingAtIndex(int index) {
		return mappings.get(index);
	}

	@Override
	public int getSize() {
		return options.size();
	}

	@Override
	public String getElementAt(int i) {
		return options.get(i);
	}
    
}

// import com.google.common.reflect.TypeToken;
// import java.lang.reflect.Type;
// 
// public abstract class GenericClass<T> {
//   private final TypeToken<T> typeToken = new TypeToken<T>(getClass()) { };
//   private final Type type = typeToken.getType(); // or getRawType() to return Class<? super T>
// 
//   public Type getType() {
//     return type;
//   }
// 
//   public static void main(String[] args) {
//     GenericClass<String> example = new GenericClass<String>() { };
//     System.out.println(example.getType()); // => class java.lang.String
//   }
// }
