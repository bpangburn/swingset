/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024-2025, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset;

/**
 * Provides a way of displaying text corresponding to codes that are stored in
 * the database.By default the codes start from zero. If you want to provide a
 different mapping for the items in the combobox then a list of integers
 containing the corresponding numeric values for each choice must be provided.
 <p>
 * Note that if you DO NOT want to use the default mappings, the custom mappings
 * must be set before calling the bind() method to bind the combobox to a
 * database column.
 * <p>
 * <b>Warning. This combobox may use GlazedLists which changes the contents
 * of the combo box and an item is automatically
 * inserted when {@link #getAllowNull()} is true. Do not use methods
 * that are based on index in the combo box list, unless you're sure...</b>
 * 
 * For example use 
 * {@link ComboBox2#getSelectedMapping() getSelectedMapping()}
 * not something that is based on {@code getSelectedIndex()}.
 * Change the current combo box item with methods
 * such as:
 * {@link ComboBox2#setSelectedMapping(java.lang.Object) setSelectedMapping(Integer)}
 * and
 * {@link ComboBox2#setSelectedOption(java.lang.Object) setSelectedOption(String)}.
 * Use the methods {@link ComboBox2#hasItems() hasItems() } and
 * {@link ComboBox2#hasSelection() hasSelection() } which take into account
 * {@code getAllowNull()}.
 * <p>
 * Notice that: {@link ComboBox2#getSelectedMapping() getSelectedMapping()}
 * returns null in two situations related to {@link #getAllowNull() }
 * <ul>
 *   <li>nothing is selected in this combo box
 *   <li>the <em>nullItem</em> is selected in this combo box
 * </ul>
 * <p>
 * {@code getSelectedItem() == null } indicates there is no
 * combo box selection; it is different than {@code !hasSelection()}
 * when {@code getAllowNull()} is true.
 * <p>
 * If subclasses need to work directly with the combo box model,
 * refer to {@link com.nqadmin.swingset.models.OptionMappingSwingModel}
 * and especially
 * {@link com.nqadmin.swingset.models.OptionMappingSwingModel.Remodel}
 * <p>
 * SSComboBox assumes that it will be bound to an integer column
 * <p>
 * Also, if changing both a rowSet and column name consider using the bind()
 * method rather than individual setRowSet() and setColumName() calls. <br>
 * For example,
 * <pre>
 * {@code
 * SSComboBox combo = new SSComboBox();
 * List<String> options = {"111", "2222", "33333"};
 * combo.setOptions(options);
 * }
 * </pre>
 * <p>
 * For the above items the combobox assumes that the values start from zero:
 * {@literal "111" -> 0, "2222" -> 1, "33333" -> 2}
 * <p>
 * To give your own mappings you can set the mappings along with the options.
 * <pre>
 * {@code
 * SSComboBox combo = new SSComboBox();
 * List<String> options = {"111", "2222", "33333"};
 * List<Integer> mappings = { 1,5,7 };
 * combo.setOptions(options, mappings);
 *
 * // next line is assuming myrowSet has been initialized and my_column is a
 * // column in myrowSet
 * combo.bind(myrowSet,"my_column"); }
 * </pre>
 * Previously this class supported "predefinedOptions". These options are
 * now encapsulated in enums, see {@link com.nqadmin.swingset.enums.YesNo},
 * {@link com.nqadmin.swingset.enums.IncludeExclude}, and
 * {@link com.nqadmin.swingset.enums.Gender3}.
 * The {@link #setOptions(java.lang.Class)}
 * takes any enum, not just these three.
 */
@SuppressWarnings("serial")
public class SSComboBox extends ComboBox<Integer, String>
{
	/**
	 * Creates an object of SSComboBox.
	 */
	public SSComboBox() {
		this(ModelType.GLAZED);
	}

	/**
	 * Creates an object of SSComboBox.
	 * <p>
	 * If useGlazedLists is specified, it is configured strict.
	 * Use {@link #getAutoComplete() } to change its configuration
	 * 
	 * @param modelType whether to use SWING or GLAZED combo model
	 */
	// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
	public SSComboBox(ModelType modelType) {
		super(modelType);
	}
} // end public class SSComboBox extends JComboBox {
