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

package com.nqadmin.swingset;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JComboBox;

import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

/**
 * SSComboBox.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Provides a way of displaying text corresponding to codes that are stored in
 * the database. By default the codes start from zero. If you want to provide a
 * different mapping for the items in the combobox then a string of integers
 * containing the corresponding numeric values for each choice must be provided.
 *
 * Note that if you DO NOT want to use the default mappings, the custom mappings
 * must be set before calling the bind() method to bind the combobox to a
 * database column.
 * 
 * SSComboBox assumes that it will be bound to an integer column
 *
 * Also, if changing both a sSRowSet and column name consider using the bind()
 * method rather than individual setSSRowSet() and setColumName() calls.
 *
 * e.g. {@code
 * SSComboBox combo = new SSComboBox(); String[] options = {"111", "2222",
 * "33333"}; combo.setOptions(options); }
 *
 * For the above items the combobox assumes that the values start from zero:
 * {@literal "111" -> 0, "2222" -> 1, "33333" -> 2}
 *
 * To give your own mappings you can set the mappings separately or pass them
 * along with the options:
 *
 * {@code
 * SSComboBox combo = new SSComboBox(); String[] options = {"111", "2222",
 * "33333"}; int[] mappings = { 1,5,7 }; combo.setOptions(options, mappings);
 * 
 * // next line is assuming mysSRowSet has been initialized and my_column is a
 * // column in mysSRowSet combo.bind(mysSRowSet,"my_column"); }
 *
 */
// TODO Consider processing the delete key to allow de-selection (e.g., setSelectedIndex(-1);)
public class SSComboBox extends JComboBox<String> implements SSComponentInterface {

	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column.
	 */
	protected class SSComboBoxListener implements ActionListener, Serializable {

		/**
		 * unique serial ID
		 */
		private static final long serialVersionUID = -3131533966245488092L;

		@Override
		public void actionPerformed(ActionEvent ae) {

			removeSSRowSetListener();

			int index = getSelectedIndex();

			if (index == -1) {
				setBoundColumnText(null);
				getLogger().debug(getColumnForLog() + ": Setting to null.");
			} else {
				setBoundColumnText(String.valueOf(getSelectedValue()));
				getLogger().debug(getColumnForLog() + ": Setting to " + getSelectedValue() + ".");
			}

			addSSRowSetListener();
		}
	}

	/**
	 * Predefined "exclude" option.
	 */
	public static final int EXCLUDE = 0;

	/**
	 * Predefined "female" option.
	 */
	public static final int FEMALE = 1;

//	/**
//	 * Text field bound to the SSRowSet.
//	 */
//	protected SSTextField textField = new SSTextField();

	/**
	 * Constant indicating that combo box should display predefined gender options.
	 */
	public static final int GENDER_OPTION = 1;

//	/**
//	 * RowSet listener.
//	 */
//	private final MyRowSetListener rowsetListener = new MyRowSetListener();

	/**
	 * Predefined "include" option.
	 */
	public static final int INCLUDE = 1;

//	/**
//	 * SSRowSet from which component will get/set values.
//	 */
//	protected SSRowSet sSRowSet;

//	/**
//	 * SSRowSet column to which the component will be bound.
//	 */
//	protected String columnName = "";

	/**
	 * Constant indicating that combo box should display predefined include/exclude
	 * options.
	 */
	public static final int INCLUDE_EXCLUDE_OPTION = 2;

	/**
	 * Predefined "male" option.
	 */
	public static final int MALE = 0;

	/**
	 * Predefined "no" option.
	 */
	public static final int NO = 0;

	/**
	 * Value to represent that no item has been selected in the combo box.
	 */
	public static final int NON_SELECTED = (int) ((Math.pow(2, 32) - 1) / (-2));

	/**
	 * unique serial ID
	 */
	private static final long serialVersionUID = 521308332266885608L;

	/**
	 * Constant indicating that combo box should display predefined gender options.
	 *
	 * @deprecated Use {@link #GENDER_OPTION} instead.
	 */
	@Deprecated
	public static final int SEX_OPTION = 1;

	/**
	 * Predefined "unisex" option.
	 *
	 * @deprecated Use {@link #UNISEX} instead.
	 */
	@Deprecated
	public static final int UNI_SEX = 2;

	/**
	 * Predefined "unisex" option.
	 */
	public static final int UNISEX = 2;

	/**
	 * Predefined "yes" option.
	 */
	public static final int YES = 1;

	/**
	 * Constant indicating that combo box should display predefined yes/no options.
	 */
	public static final int YES_NO_OPTION = 0;

	/**
	 * Underlying values for each combo box choice if different from defaults of 0,
	 * 1, 2, 3, etc.
	 */
	// protected int[] mappings = null;
	protected ArrayList<Integer> mappings = null;

	/**
	 * Options to be displayed in combo box.
	 */
	// protected String[] options;
	protected ArrayList<String> options = null;

	/**
	 * Code representing of predefined options to be displayed in the combo box
	 * (e.g. yes/no, exclude/include, etc.
	 */
	protected int predefinedOptions = -1;

	/**
	 * Component listener.
	 */
	protected final SSComboBoxListener ssComboBoxListener = new SSComboBoxListener();

	/**
	 * Common fields shared across SwingSet components
	 */
	protected SSCommon ssCommon;

//	/**
//	 * Sets the SSRowSet column name to which the component is bound.
//	 *
//	 * @param _columnName column name in the SSRowSet to which the component is
//	 *                    bound
//	 */
//	public void setColumnName(String _columnName) {
//		String oldValue = this.columnName;
//		this.columnName = _columnName;
//		firePropertyChange("columnName", oldValue, this.columnName);
//		// try {
//		bind();
//		// } catch (SQLException e) {
//		// e.printStackTrace();
//		// }
//	}

//	/**
//	 * Returns the column name to which the combo is bound.
//	 *
//	 * @return returns the column name to which to combo box is bound.
//	 */
//	public String getColumnName() {
//		return this.columnName;
//	}

//	/**
//	 * Sets the SSRowSet to which the component is bound.
//	 *
//	 * @param _sSRowSet SSRowSet to which the component is bound
//	 */
//	public void setSSRowSet(SSRowSet _sSRowSet) {
//		SSRowSet oldValue = this.sSRowSet;
//		this.sSRowSet = _sSRowSet;
//		firePropertyChange("sSRowSet", oldValue, this.sSRowSet);
//		// try {
//		bind();
//		// } catch (SQLException e) {
//		// e.printStackTrace();
//		// }
//	}

//	/**
//	 * Returns the SSRowSet being used to get the values.
//	 *
//	 * @return returns the SSRowSet being used.
//	 */
//	public SSRowSet getSSRowSet() {
//		return this.sSRowSet;
//	}

	/**
	 * Creates an object of SSComboBox.
	 */
	public SSComboBox() {
		super();
		setSSCommon(new SSCommon(this));
		// SSCommon constructor calls init()
	}

	/**
	 * Adds any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 */
	@Override
	public void addSSComponentListener() {
		addActionListener(ssComboBoxListener);

	}

	/**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * 
	 * It will actually be called from SSCommon.init() once the SSCommon data member
	 * is instantiated.
	 */
	@Override
	public void customInit() {
		// SET PREFERRED DIMENSIONS
// TODO not sure SwingSet should be setting component dimensions    	
		setPreferredSize(new Dimension(200, 20));
// TODO This was added during SwingSet rewrite 4/2020. Need to confirm it doesn't break anything.             
		setEditable(false);
	}

	/**
	 * returns the combo box that has to be displayed on screen.
	 *
	 * @return returns the combo box that displays the items.
	 *
	 * @deprecated unnecessary - can reference object directly
	 */
	@Deprecated
	public JComboBox<?> getComboBox() {
		return this;
	}

	/**
	 * Returns the combo box to be displayed on the screen.
	 *
	 * @return returns the combo box that displays the items.
	 *
	 * @deprecated unnecessary - can reference object directly
	 */
	@Deprecated
	public Component getComponent() {
		return this;
	}

	/**
	 * Returns the underlying values for each of the items in the combo box (e.g.
	 * the values that map to the items displayed in the combo box)
	 *
	 * @return returns the underlying values for each of the items in the combo box
	 */
	public int[] getMappings() {
		// return this.mappings;
		return mappings.stream().mapToInt(i -> i).toArray();
		// https://stackoverflow.com/questions/718554/how-to-convert-an-arraylist-containing-integers-to-primitive-int-array
		// will choke on null, but shouldn't have nulls
	}

	/**
	 * Returns the items displayed in the combo box.
	 *
	 * @return returns the items displayed in the combo box
	 */
	public String[] getOptions() {
		// return this.options;
		return (String[]) options.toArray();
	}

//	/**
//	 * Sets the SSRowSet and column name to which the component is to be bound.
//	 *
//	 * @param _sSRowSet   datasource to be used.
//	 * @param _columnName Name of the column to which this check box should be bound
//	 */
//	public void bind(SSRowSet _sSRowSet, String _columnName) {
//		SSRowSet oldValue = this.sSRowSet;
//		this.sSRowSet = _sSRowSet;
//		firePropertyChange("sSRowSet", oldValue, this.sSRowSet);
//
//		String oldValue2 = this.columnName;
//		this.columnName = _columnName;
//		firePropertyChange("columnName", oldValue2, this.columnName);
//
//		// try {
//		bind();
//		// } catch (SQLException e) {
//		// e.printStackTrace();
//		// }
//	}

//	/**
//	 * Initialization code.
//	 */
//	protected void init() {
//		// TO TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER KEY IS PRESSED.
//		Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
//		Set<AWTKeyStroke> newForwardKeys = new HashSet<>(forwardKeys);
//		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
//		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK));
//		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
//
//		// SET PREFERRED DIMENSIONS
//		setPreferredSize(new Dimension(200, 20));
//	}

	/**
	 * Returns the option code used to display predefined options in the combo box.
	 *
	 * @return returns the predefined option code
	 */
	public int getPredefinedOptions() {
		return this.predefinedOptions;
	}

//	/**
//	 * Method for handling binding of component to a SSRowSet column.
//	 */
//	protected void bind() {
//
//		// CHECK FOR NULL COLUMN/ROWSET
//		if (this.columnName == null || this.columnName.trim().equals("") || this.sSRowSet == null) {
//			return;
//		}
//
//		// REMOVE LISTENERS TO PREVENT DUPLICATION
//		removeListeners();
//
//		// BIND THE TEXT FIELD TO THE SPECIFIED COLUMN
//		this.textField.bind(this.sSRowSet, this.columnName);
//
//		// SET THE COMBO BOX ITEM DISPLAYED
//		updateDisplay();
//
//		// ADD BACK LISTENERS
//		addListeners();
//
//	}

//	/**
//	 * Updates the value displayed in the component based on the SSRowSet column
//	 * binding.
//	 */
//	protected void updateDisplay() {
//		updateDisplay(false);
//	}

//	/**
//	 * Updates the value displayed in the component based on the SSRowSet column
//	 * binding.
//	 * 
//	 * @param useTextField If this is true the value from the text in the text field
//	 *                     will be used to update the display even if the rowset is
//	 *                     on a valid row. This is needed when setSelectedValue is
//	 *                     used.
//	 */
//	protected void updateDisplay(boolean useTextField) {
//		try {
//			String text = "";
//			try {
//				
//				if (useTextField) {
//					text = this.textField.getText().trim();
//				} else {
//					text = this.sSRowSet.getRow() > 0 ? this.sSRowSet.getString(this.columnName)
//							: this.textField.getText().trim();
//				}
//				// System.out.println(columnName + " " + text + " " + textField.getText());
//			} catch (SQLException se) {
//				// do nothing
//			}
//
//			// GET THE INTEGER EQUIVALENT OF THE TEXT IN THE TEXT FIELD
//			if (text != null && !(text.trim().equals(""))) {
//				int intValue = 0;
//				intValue = Integer.parseInt(text.trim());
//
//				// CHECK IF THE VALUE DISPLAYED IS THE SAME AS THAT IN TEXT FIELD
//				// TWO CASES 1. THE MAPPINGS FOR THE STRINGS DISPLAYED ARE GIVEN BY USER
//				// 2. VALUES FOR THE ITEMS IN COMBO START FROM ZERO
//				// IN CASE ONE: YOU CAN JUST CHECK FOR EQUALITY OF THE SELECTEDINDEX AND VALUE
//				// IN TEXT FIELD
//				// IN CASE TWO: YOU HAVE TO CHECK IF THE VALUE IN THE MAPPINGVALUES ARRAY AT
//				// INDEX EQUAL
//				// TO THE SELECTED INDEX OF THE COMBO BOX EQUALS THE VALUE IN TEXT FIELD
//				// IF THESE CONDITIONS ARE MET YOU NEED NOT CHANGE COMBO BOX SELECTED ITEM
//				if ((this.mappings == null && intValue != getSelectedIndex())
//						|| (this.mappings != null && getSelectedIndex() == -1)
//						|| (this.mappings != null && this.mappings[getSelectedIndex()] != intValue)) {
//
//					if (this.mappings == null && (intValue < 0 || intValue >= getItemCount())) {
//						// IF EXPLICIT VALUES FOR THE ITEMS IN COMBO ARE NOT SPECIFIED THEN CODES START
//						// FROM ZERO. IN SUCH A CASE CHECK IF THE NUMBER EXCEEDS THE NUMBER OF ITEMS
//						// IN COMBO BOX (THIS IS ERROR CONDITION SO NOTIFY USER)
//						System.out.println(
//								"Error: value from DB:" + intValue + "  items in combo box: " + getItemCount());
//						setSelectedIndex(-1);
//					} else {
//						// IF MAPPINGS ARE SPECIFIED THEN GET THE INDEX AT WHICH THE VALUE IN TEXT FIELD
//						// APPEARS IN THE MAPPINGVALUES ARRAY. SET THE SELECTED ITEM OF COMBO SO THAT
//						// INDEX
//						if (this.mappings != null) {
//							int i = 0;
//							for (; i < this.mappings.length; i++) {
//								if (this.mappings[i] == intValue) {
//									setSelectedIndex(i);
//									break;
//								}
//							}
//							// IF THAT VALUE IS NOT FOUND IN THE GIVEN MAPPING VALUES PRINT AN ERROR MESSAGE
//							if (i == this.mappings.length) {
//								System.out
//										.println("change ERROR: could not find a corresponding item in combo for value "
//												+ intValue);
//								setSelectedIndex(-1);
//							}
//						} else {
//							// IF MAPPINGS ARE NOT SPECIFIED SET THE SELECTED ITEM AS THE ITEM AT INDEX
//							// EQUAL TO THE VALUE IN TEXT FIELD
//							setSelectedIndex(intValue);
//						}
//					}
//				}
//			} else {
//				setSelectedIndex(-1);
//			}
//		} catch (NumberFormatException nfe) {
//			nfe.printStackTrace();
//		}
//
//	}

//	/**
//	 * Adds listeners for component and rowset
//	 */
//	private void addListeners() {
//		this.sSRowSet.addRowSetListener(this.rowsetListener);
//		addActionListener(this.cmbListener);
//	}

//	/**
//	 * Removes listeners for component and rowset.
//	 */
//	private void removeListeners() {
//		this.sSRowSet.removeRowSetListener(this.rowsetListener);
//		removeActionListener(this.cmbListener);
//	}

//	/**
//	 * Rowset Listener for updating the value displayed.
//	 */
//	protected class MyRowSetListener implements RowSetListener, Serializable {
//
//		/**
//		 * unique serial id
//		 */
//		private static final long serialVersionUID = 5996555547965507000L;
//
//		@Override
//		public void cursorMoved(RowSetEvent arg0) {
//			removeActionListener(SSComboBox.this.cmbListener);
//			// try {
//			updateDisplay();
//			// } catch (SQLException e) {
//			// e.printStackTrace();
//			// }
//			addActionListener(SSComboBox.this.cmbListener);
//		}
//
//		@Override
//		public void rowChanged(RowSetEvent event) {
//			removeActionListener(SSComboBox.this.cmbListener);
//			// try {
//			updateDisplay();
//			// } catch (SQLException e) {
//			// e.printStackTrace();
//			// }
//			addActionListener(SSComboBox.this.cmbListener);
//		}
//
//		@Override
//		public void rowSetChanged(RowSetEvent event) {
//			removeActionListener(SSComboBox.this.cmbListener);
//			// try {
//			updateDisplay();
//			// } catch (SQLException e) {
//			// e.printStackTrace();
//			// }
//			addActionListener(SSComboBox.this.cmbListener);
//		}
//
//	}

	/**
	 * Returns the mapping code corresponding to the currently selected item in the
	 * combobox.
	 *
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @return returns the value associated with the selected item OR -1 if nothing
	 *         is selected.
	 */
	public int getSelectedValue() {

		int result;

		if (getSelectedIndex() == -1) {
			result = NON_SELECTED;
		} else if (this.mappings != null) {
			// result = this.mappings[getSelectedIndex()];
			result = (int) mappings.get(getSelectedIndex());
		} else {
			result = getSelectedIndex();
		}

		return result;
	}

	/**
	 * Returns the ssCommon data member for the current Swingset component.
	 * 
	 * @return shared/common SwingSet component data and methods
	 */
	@Override
	public SSCommon getSSCommon() {
		return ssCommon;
	}

	/**
	 * Removes any necessary listeners for the current SwingSet component. These
	 * will trigger changes in the underlying RowSet column.
	 */
	@Override
	public void removeSSComponentListener() {
		removeActionListener(ssComboBoxListener);

	}

	/**
	 * Sets the underlying values for each of the items in the combo box (e.g. the
	 * values that map to the items displayed in the combo box)
	 *
	 * @param _mappings an array of values that correspond to those in the combo
	 *                  box.
	 */
	public void setMappings(int[] _mappings) {
		// int[] oldValue = mappings.clone();
		// this.mappings = _mappings.clone();
		if (mappings != null)
			mappings.clear();
		// https://examples.javacodegeeks.com/core-java/java8-convert-array-list-example/
		mappings = new ArrayList<Integer>(IntStream.of(_mappings).boxed().collect(Collectors.toList()));
		// TODO in Java 9, we can use List.of(_mappings) - can be immutable or mutable -
		// see https://stackoverflow.com/questions/30122439/converting-array-to-list
		// mappings = new ArrayList(Arrays.<Integer>asList(_mappings);
		// System.out.println(mappings.toString());
		// firePropertyChange("mappings", oldValue, this.mappings);
		// INITIALIZE THE ARRAY AND COPY THE MAPPING VALUES
		// mappings = new int[_mappings.length];
		// for (int i=0;i<_mappings.length;i++) {
		// mappings[i] = _mappings[i];
		// }
	}

	/**
	 * Sets the underlying values for each of the items in the combo box (e.g. the
	 * values that map to the items displayed in the combo box)
	 *
	 * @param _mappings an array of values that correspond to those in the combo
	 *                  box.
	 *
	 * @deprecated Use {@link #setMappings(int[] _mappings)} instead.
	 */
	@Deprecated
	public void setMappingValues(int[] _mappings) {
		setMappings(_mappings);
	}

	/**
	 * Sets the options to be displayed in the combo box based on common predefined
	 * options.
	 *
	 * @param _option predefined options to be displayed in the combo box.
	 * @return a boolean indicating success/failure setting predefined options.
	 *
	 * @deprecated Use {@link #setPredefinedOptions(int _option)} instead.
	 */
	@Deprecated
	public boolean setOption(int _option) {
		return setPredefinedOptions(_option);
	}

	/**
	 * Sets the options to be displayed in the combo box and their corresponding
	 * values.
	 *
	 * @param _options  predefined options to be displayed in the combo box.
	 * @param _mappings integer values that correspond to the options in the combo
	 *                  box.
	 *
	 * @deprecated Use {@link #setOptions(String[] _options, int[] _mappings)}
	 *             instead.
	 * 
	 */
	@Deprecated
	public void setOption(int _options, int[] _mappings) {
		setPredefinedOptions(_options);
		setMappings(_mappings);
	}

	/**
	 * Adds an array of strings as combo box items.
	 *
	 * @param _options the list of options that you want to appear in the combo box.
	 *
	 * @return returns true if the options and mappings are set successfully -
	 *         returns false if the size of arrays do not match or if the values
	 *         could not be set
	 *
	 * @deprecated Use {@link #setOptions(String[] _options)} instead.
	 */
	@Deprecated
	public boolean setOption(String[] _options) {
		setOptions(_options);
		return true;
	}

	/**
	 * Sets the options to be displayed in the combo box and their corresponding
	 * values.
	 *
	 * @param _options  options to be displayed in the combo box.
	 * @param _mappings integer values that correspond to the options in the combo
	 *                  box.
	 *
	 * @return returns true if the options and mappings are set successfully -
	 *         returns false if the size of arrays do not match or if the values
	 *         could not be set
	 *
	 * @deprecated Use {@link #setOptions(String[] _options, int[] _mappings)}
	 *             instead.
	 */
	@Deprecated
	public boolean setOption(String[] _options, int[] _mappings) {
		return setOptions(_options, _mappings);
	}

	/**
	 * Adds an array of strings as combo box items.
	 *
	 * @param _options the list of options that you want to appear in the combo box.
	 */
	public void setOptions(String[] _options) {
		// String[] oldValue = options.clone();
		// this.options = _options.clone();
		// firePropertyChange("options", oldValue, this.options);

		if (options != null)
			options.clear();
		options = new ArrayList<String>(Arrays.asList(_options));
		// TODO in Java 9, we can use List.of(_options)

		// ADD THE SPECIFIED ITEMS TO THE COMBO BOX
		// REMOVE ANY OLD ITEMS SO THAT MULTIPLE CALLS TO THIS FUNCTION DOES NOT AFFECT
		// THE DISPLAYED ITEMS
		if (getItemCount() != 0) {
			removeAllItems();
		}

		options.forEach(option -> addItem(option));
//		for (int i = 0; i < _options.length; i++) {
//			addItem(_options[i]);
//		}
	}

	/**
	 * Sets the options to be displayed in the combo box and their corresponding
	 * values.
	 *
	 * @param _options  options to be displayed in the combo box.
	 * @param _mappings integer values that correspond to the options in the combo
	 *                  box.
	 *
	 * @return returns true if the options and mappings are set successfully -
	 *         returns false if the size of arrays do not match or if the values
	 *         could not be set
	 */
	public boolean setOptions(String[] _options, int[] _mappings) {
		if (_options.length != _mappings.length) {
			return false;
		}

		setOptions(_options);

		setMappings(_mappings);

		return true;

	}

	/**
	 * Sets the options to be displayed in the combo box based on common predefined
	 * options.
	 *
	 * @param _predefinedOptions predefined options to be displayed in the combo
	 *                           box.
	 * @return true or false indicating if the predefined options were set
	 *         successfully
	 */
	public boolean setPredefinedOptions(int _predefinedOptions) {
		int oldValue = this.predefinedOptions;

		if (_predefinedOptions == YES_NO_OPTION) {
			setOptions(new String[] { "No", "Yes" });
		} else if (_predefinedOptions == SEX_OPTION || _predefinedOptions == GENDER_OPTION) {
			setOptions(new String[] { "Male", "Female", "Unisex" });
		} else if (_predefinedOptions == INCLUDE_EXCLUDE_OPTION) {
			setOptions(new String[] { "Include", "Exclude" });
		} else {
			return false;
		}

		this.predefinedOptions = _predefinedOptions;
		firePropertyChange("predefinedOptions", oldValue, this.predefinedOptions);

		return true;
	}

	/**
	 * Sets the value stored in the component.
	 * 
	 * If called from updateSSComponent() from a RowSet change then the Component
	 * listener should already be turned off. Otherwise we want it on so the
	 * ultimate call to setSelectedIndex() will trigger an update the to RowSet.
	 *
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _value value to assign to combobox, which may or may not correlate to
	 *               the combobox index
	 */
	public void setSelectedValue(int _value) {

		// ONLY NEED TO PROCEED IF THERE IS A CHANGE
		// TODO consider firing a property change
		if (_value != getSelectedValue()) {

			if (this.mappings == null && (_value < 0 || _value >= getItemCount())) {
				// IF EXPLICIT VALUES FOR THE ITEMS IN COMBO ARE NOT SPECIFIED THEN CODES START
				// FROM ZERO. IN SUCH A CASE CHECK IF THE NUMBER EXCEEDS THE NUMBER OF ITEMS
				// IN COMBO BOX (THIS IS ERROR CONDITION SO NOTIFY USER)
				getLogger().warn(getColumnForLog() + ": Value from database:" + _value + ". Items in combobox: " + getItemCount());
				
				setSelectedIndex(-1);
			} else {
				// IF MAPPINGS ARE SPECIFIED THEN LOCATE THE SEQUENTIAL INDEX AT WHICH THE
				// SPECIFIED CODE IS STORED
				if (this.mappings != null) {
					int index = mappings.indexOf(_value);

					if (index == -1) {
						getLogger().warn(getColumnForLog() + ": Warning: could not find a corresponding item in combobox for code of " + _value + ". Setting index to -1 (blank).");
					}

					setSelectedIndex(index);
				} else {
					// IF MAPPINGS ARE NOT SPECIFIED SET THE SELECTED ITEM AS THE ITEM AT INDEX
					// EQUAL TO THE VALUE IN TEXT FIELD
					setSelectedIndex(_value);
				}
			}

		}

	}

	/**
	 * Sets the SSCommon data member for the current Swingset Component.
	 * 
	 * @param _ssCommon shared/common SwingSet component data and methods
	 */
	@Override
	public void setSSCommon(SSCommon _ssCommon) {
		ssCommon = _ssCommon;

	}

	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText()
	 * 
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed
	 */
	@Override
	public void updateSSComponent() {
		try {
			String text = getBoundColumnText().trim();

			// GET THE INTEGER EQUIVALENT OF THE TEXT IN THE TEXT FIELD
			if (text != null && !(text.equals(""))) {
				int comboCode = Integer.parseInt(text);

				setSelectedValue(comboCode);

			} else {
				setSelectedIndex(-1);
			}

		} catch (NumberFormatException nfe) {
			getLogger().warn(getColumnForLog() + ": Number Format Exception.", nfe);
		}
	}

} // end public class SSComboBox extends JComboBox {