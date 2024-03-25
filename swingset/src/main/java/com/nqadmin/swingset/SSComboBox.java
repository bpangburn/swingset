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
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import java.lang.System.Logger;
import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.models.OptionMappingSwingModel;
import com.nqadmin.swingset.models.SSListItem;
import com.nqadmin.swingset.utils.SSUtils;


// SSComboBox.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Provides a way of displaying text corresponding to codes that are stored in
 * the database. By default the codes start from zero. If you want to provide a
 * different mapping for the items in the combobox then a list of integers
 * containing the corresponding numeric values for each choice must be provided.
 * <p>
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
 * {@link SSBaseComboBox#getSelectedMapping() getSelectedMapping()}
 * not something that is based on {@code getSelectedIndex()}.
 * Change the current combo box item with methods
 * such as:
 * {@link SSBaseComboBox#setSelectedMapping(java.lang.Object) setSelectedMapping(Integer)}
 * and
 * {@link SSBaseComboBox#setSelectedOption(java.lang.Object) setSelectedOption(String)}.
 * Use the methods {@link SSBaseComboBox#hasItems() hasItems() } and
 * {@link SSBaseComboBox#hasSelection() hasSelection() } which take into account
 * {@code getAllowNull()}.
 * <p>
 * Notice that: {@link SSBaseComboBox#getSelectedMapping() getSelectedMapping()}
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
public class SSComboBox extends SSBaseComboBox<Integer, String, Object>
{
	private static final long serialVersionUID = 521308332266885608L;

	/** A convenience for variable declarations. Can not instantiate. */
	private static class Model extends OptionMappingSwingModel<Integer,String,Object> {
		/** Exception if invoked. */
		@SuppressWarnings("unused")
		public Model() { Objects.requireNonNull(null); } 
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	protected SSListItem createNullItem(Model.Remodel remodel) {
		return remodel.createOptionMappingItem(null, "", null);
	}

	/**
	 * when true, mappings were autoGenerated
	 */
	private boolean autoGeneratedMapping;

	/**
	 * Log4j Logger for component
	 */
	@SuppressWarnings("unused")
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * enum being displayed in combo box
	 */
	// TODO: needed?
	private Class<?> enumOption;

	private static final boolean USE_GLAZED_MODEL = true;

	/**
	 * Creates an object of SSComboBox.
	 */
	public SSComboBox() {
		this(USE_GLAZED_MODEL);
	}

	/**
	 * Creates an object of SSComboBox.
	 * <p>
	 * If useGlazedLists is specified, it is configured strict.
	 * Use {@link #getAutoComplete() } to change its configuration
	 * 
	 * @param useGlazedLists install glazed lists
	 */
	// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
	public SSComboBox(boolean useGlazedLists) {
		super(useGlazedLists);
	}
	
	/**
	 * Return the selected enum.
	 * 
	 * @return selected enum.
	 * @throws IllegalStateException if not an enum.
	 */
	public Enum<?> getSelectedEnum() {
		if (enumOption == null) {
			throw new IllegalStateException("SSComboBox values not an enum");
		}
		Integer mapping = getSelectedMapping();
		if (mapping == null) {
			return null;
		}
		
		return (Enum<?>)enumOption.getEnumConstants()[mapping];
	}
	
	/**
	 * Finds the listItem that matches the specified enum and make it the selected
	 * listItem.
	 *
	 * @param _option select list item for this
	 * @throws ClassCastException if _option is wrong enum type
	 */
	public void setSelectedEnum(Enum<?> _option) {
		if (enumOption == null) {
			throw new IllegalStateException("SSComboBox values not an enum.");
		}
		Objects.requireNonNull(_option, "Enum to be selected cannnot be null.");
		enumOption.cast(_option);

		// The following an alternate way to check
		// if (!enumOption.isAssignableFrom(_option.getClass())) {
		// throw new IllegalArgumentException("Wrong enum type, " +
		// enumOption.getSimpleName() + " required");
		// }
		// enumOption == _option.getClass() // also works?, feels more brittle

		// This could be performance optimized since itemList index == ordinal()
		// within the current constraints of mapping == ordinal().
		// But the whole nullItem comes into play to affect the index.

		setSelectedMapping(_option.ordinal());
	}

	/**
	 * Returns the underlying values for each of the items in the combo box (e.g.
	 * the values that map to the items displayed in the combo box)
	 *
	 * @return returns the underlying values for each of the items in the combo box
	 */
	public int[] getMappingsInt() {
		return optionModel.getMappings().stream().mapToInt(i -> i).toArray();
		// https://stackoverflow.com/questions/718554/how-to-convert-an-arraylist-containing-integers-to-primitive-int-array
		// will choke on null, but shouldn't have nulls
	}

	/**
	 * Adds an array of strings as combo box items
	 * with {@literal [0-N)} mapping.
	 * <p>
	 * Convenience method for {@link #setOptions(java.util.List) }
	 *
	 * @param _options the list of options that you want to appear in the combo box.
	 */
	public void setOptions(final String[] _options) {
		setOptions(_options, null);

		// // TODO why bother clearing? GC doesn't care; other references?
		// if (options != null) {
		// 	options.clear();
		// }

		// // TODO Add empty string first item if getAllowNull()==true. Will also impact mappings, getSelectedValue(), getSelectedIndex(), etc.

		// options = new ArrayList<String>(Arrays.asList(_options));
		// // TODO in Java 9, we can use List.of(_options)

		// // ADD THE SPECIFIED ITEMS TO THE COMBO BOX
		// // REMOVE ANY OLD ITEMS FIRST SO THAT MULTIPLE CALLS TO THIS FUNCTION DOES NOT AFFECT
		// // THE DISPLAYED ITEMS
		// if (getItemCount() != 0) {
		// 	removeAllItems();
		// }

		// options.forEach(option -> addItem(option));

	}

	/**
	 * Sets the options to be displayed in the combo box and their corresponding
	 * values. If _mappings is null generate a {@literal [0-N)} mapping.
	 * 
	 * Convenience method for
	 * {@link #setOptions(java.util.List, java.util.List)}.
	 *
	 * @param _options  options to be displayed in the combo box.
	 * @param _mappings integer values that correspond to the options in the combo
	 *                  box. May be null for {@literal [0-N)} mapping
	 *
	 * @return returns true if the options and mappings are set successfully -
	 *         returns false if the size of arrays do not match or if the values
	 *         could not be set
	 */
	public boolean setOptions(final String[] _options, final int[] _mappings) {
		Objects.requireNonNull(_options);
		// if (_options.length != _mappings.length) {
		// 	return false;
		// }

		setOptions(Arrays.asList(_options), _mappings == null ? null
				: Arrays.stream(_mappings).collect(ArrayList::new, List::add, List::addAll));

		//setOptions(_options);

		//setMappings(_mappings);

		return true;

	}

	/**
	 * Sets the options to be displayed in the list box along with
	 * their corresponding mappings to database values. If {@code _mappings}
	 * is null, then a zero to N-1 mapping is automatically established.
	 * 
	 * @param _options  options to be displayed in the list box.
	 * @param _mappings null or database values that correspond to the options, 1 to 1, in
	 *					the list box.
	 * @throws IllegalArgumentException if lists are not the same size.
	 */
	public void setOptions(List<String> _options, List<Integer> _mappings) {
		enumOption = null;
		setOptionsInternal(_options, _mappings);
	}

	private void setOptionsInternal(List<String> _options, List<Integer> _mappings) {
		Objects.requireNonNull(_options);
		try (Model.Remodel remodel = optionModel.getRemodel()) {
			if (_mappings != null && _options.size() != _mappings.size()) {
				throw new IllegalArgumentException("Options and Mappings must be the same length");
			}

			remodel.clear();
			nullItem = null;

		// TODO first item is nullItem if getAllowNull()==true.
		//      Impacts mappings, getSelectedValue(), getSelectedIndex(), etc.

			adjustForNullItem();

			autoGeneratedMapping = false;
			List<Integer> mpings = _mappings;
			if (mpings == null) {
				// Provide a [0,N) mapping
				// TODO: in the future let it be implicit, don't create it?
				mpings = IntStream.range(0, _options.size())
						.collect(ArrayList::new, List::add, List::addAll);
				autoGeneratedMapping = true;
			}

			List<String> opts = optionModel.getDisconnectedList(_options);
			mpings = optionModel.getDisconnectedList(mpings);
			
			remodel.addAll(mpings, opts);
		}
	}

	/**
	 * Determine if mappings are autoGenerated {@literal [0-N)}
	 * @return true if mappings are autoGenerated
	 */
	// TODO: public? Get rid of the field
	protected boolean isAutoGeneratedMapping() {
		return autoGeneratedMapping;
	}

	/**
	 * Adds a list of strings as combo box items
	 * with {@literal [0-N)} mapping.
	 *
	 * @param _options the list of options that you want to appear in the combo box.
	 */
	public void setOptions(final List<String> _options) {
		setOptions(_options, null);
	}

	//
	// TODO: Study use case for enums
	// TODO: When an enum is specified, should there be a way to
	//		specify a mapping as well? Two possibilities, the first
	//		is like the general pattern, mapping should have the
	//		same number of elements as there are enum values.
	//		The second could also be provided as setOptions([], Function)
	//		 1) setOptions(enum, mapping)
	//		 2) setOptions(enum, Function<enum, Object>)
	//			For example
	//				setOptions(ComboEnum.class, (e) -> e.someMethod())
	//				setOptions(ComboEnum.class, (e) -> someMap.get(e))

	/**
	 * Sets the options to be displayed in the combo box based on
	 * the enum class' value's toString(). Generate a {@literal [0-N)}
	 * mapping.
	 *
	 * @param <T> inferred enum type
	 * @param _enumOptions enum class with values to display
	 */
	public <T extends Enum<T>> void setOptions(Class<T> _enumOptions) {
		// Could mark known enums for special handling,
		// Could have a special method for getting the display string.
		// But what's wrong with toString()

		enumOption = _enumOptions;
		setOptionsInternal(Stream.of(_enumOptions.getEnumConstants())
				.map(e -> e.toString()).collect(Collectors.toList()), null);
	}

	/**
	 * Get the enum class currently displayed by this combo box.
	 * If combo box is not displaying an enum, then reutrn null.
	 * @return the enum class
	 */
	// TODO: public? Get rid of the field
	protected Class<?>getEnumOption() {
		return enumOption;
	}

	/**
	 * Returns the underlying values for each of the items in the combo box (e.g.
	 * the values that map to the items displayed in the combo box)
	 * <p>
	 * <b>When getAllowNull() is true, the first list item is null/""</b>
	 *
	 * @return returns the underlying values for each of the items in the combo box
	 */
	// TODO: deprecate this in favor of "List<> getMappings()"
	//       when ready to remove "int[] getMappings()"
	public List<Integer> getMappingsList() {
		return optionModel.getMappings();
	}

	/**
	 * Returns the items displayed in the combo box.
	 * <p>
	 * <b>When getAllowNull() is true, the first list item is null/""</b>
	 *
	 * @return returns the items displayed in the combo box
	 */
	public List<String> getOptionsList() {
		return optionModel.getOptions();
	}

	// TODO: Is this needed or the remains of wanting to be a good bean?
	//       It could be useful to distinguish when an enum was used.
	//
	//       To keep it, must clear enumOption in any setOption path
	//       that doesn't originate with an enum, a little tricky but not bad.
	// /**
	//  * Returns the option code used to display predefined options in the combo box.
	//  *
	//  * @return returns the predefined option code
	//  */
	// public Class<?> getEnumOption() {
	// 	return enumOption;
	// }

} // end public class SSComboBox extends JComboBox {
