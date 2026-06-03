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


import static com.nqadmin.swingset.utils.SSUtils.findRowsModel;
import static java.lang.System.Logger.Level.WARNING;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.System.Logger;
import java.util.StringTokenizer;

import javax.sql.RowSet;

import com.nqadmin.swingset.core.TextField;
import com.nqadmin.swingset.navigate.RowsModel;
import com.nqadmin.swingset.utils.SSUtils;


/**
 * SSTextField extends the JTextField.
 */
@SuppressWarnings("serial")
public class SSTextField extends TextField
{
	
	
//=====================================================================================
// 2025-12-31_BP: The code BELOW is needed for SwingSet 4.0.x compatibility
//=====================================================================================

	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();
	
	/**
	 * Use this if the text field contains decimal number and want to limit number
	 * of decimal places.
	 */
	@Deprecated
	public static final int DECIMAL = 4;

	/**
	 * Use this mask if mm/dd/yyyy format is required.
	 */
	@Deprecated
	public static final int MMDDYYYY = 1;
	
	/**
	 * Use this mask if dd/mm/yyyy format is required.
	 */
	@Deprecated
	public static final int DDMMYYYY = 2;

	/**
	 * Use this if the text field contains SSN
	 */
	@Deprecated
	public static final int SSN = 3;
	
	@Deprecated
	private static final int ANY_ALIGNMENT = 0xffff;

	@Deprecated
	private static final int DEFAULT_MASK = 0;
	
	/** Type of mask to be used for text field (default = none). */
	@Deprecated
	protected int mask = DEFAULT_MASK;

	@Deprecated
	private static final int DEFAULT_NUMBER_OF_DECIMAL_PLACES = 2;
	
	/** Default number of decimals to show for float, double, etc. */
	@Deprecated
	protected int numberOfDecimalPlaces = DEFAULT_NUMBER_OF_DECIMAL_PLACES;

	/**
	 * Constructs a new, empty text field with the specified mask.
	 *
	 * @param _mask the mask required for this textfield.
	 */
	@Deprecated
	public SSTextField(final int _mask) {
		this(null, null, null, _mask, DEFAULT_NUMBER_OF_DECIMAL_PLACES, ANY_ALIGNMENT);
	}

	/**
	 * Constructs a new, empty text field with the specified mask and number of
	 * decimal places. Use this constructor only if you are using a decimal mask.
	 *
	 * @param _mask                  the mask required for this textfield.
	 * @param _numberOfDecimalPlaces number of decimal places required
	 */
	@Deprecated
	public SSTextField(final int _mask, final int _numberOfDecimalPlaces) {
		this(null, null, null, _mask, _numberOfDecimalPlaces, ANY_ALIGNMENT);
	}

	/**
	 * Constructs a new, empty text field with the specified mask, number of decimal
	 * places, and alignment. Use this constructor only if you are using a decimal
	 * mask.
	 * 
	 * <pre>
	 * (Horizontal alignment).
	 * Valid aligmnets are:
	 *  JTextField.LEFT
	 *  JTextField.CENTER
	 *  JTextField.RIGHT
	 *  JTextField.LEADING
	 *  JTextField.TRAILING
	 *
	 * Use this constructor only if you are using a decimal mask.
	 * </pre>
	 * 
	 * @param _mask                  the mask required for this text field
	 * @param _numberOfDecimalPlaces number of decimal places required
	 * @param _align                 alignment required
	 */
	@Deprecated
	public SSTextField(final int _mask, final int _numberOfDecimalPlaces, final int _align) {
		this(null, null, null, _mask, _numberOfDecimalPlaces, _align);
	}

	/**
	 * All the constructors feed through here
	 * 
	 * @param _text                  the text to be displayed
	 * @param _rowSet                rowset with column to be bound
	 * @param _boundColumnName       name of bound column
	 * @param _mask                  the mask required for this text field
	 * @param _numberOfDecimalPlaces number of decimal places required
	 * @param _align                 alignment required
	 */
	@Deprecated
	private SSTextField(String _text, final RowSet _rowSet, final String _boundColumnName,
	final int _mask, final int _numberOfDecimalPlaces, final int _align) {
		super(_text);
		//ssCommon = finishSSCommon();
		mask = _mask;
		numberOfDecimalPlaces = _numberOfDecimalPlaces;
		
		if (_align != ANY_ALIGNMENT) {
			setHorizontalAlignment(_align);
		}

		if (_rowSet != null) {
			bind(findRowsModel(_rowSet), _boundColumnName);
		}

	}
	
// FROM TextField.java...		
//		/**
//		 * Add focus listener that selects all text.
//		 * Add key listener for when this is used with mask. Use Mask Formatters.
//		 */
//		@Override
//		public void customInit()
//		{
//			// ADD FOCUS LISTENER TO THE TEXT FIELD SO THAT WHEN THE FOCUS IS GAINED
//			// COMPLETE TEXT SHOULD BE SELECTED
//			addFocusListener(new FocusAdapter() {
//				@Override
//				public void focusGained(final FocusEvent fe) {
//					// TODO: Turn off any TextDecorator while focused
//					TextField.this.selectAll();
//				}
//			});
//		}
	
	
	/**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * <p>
	 * It will actually be called from SSCommon.init() once the SSCommon data member
	 * is instantiated.
	 */
	@Override
	public void customInit()
	{
		super.customInit(); // takes care of the 10+/- commented lines below
//			// ADD FOCUS LISTENER TO THE TEXT FIELD SO THAT WHEN THE FOCUS IS GAINED
//			// COMPLETE TEXT SHOULD BE SELECTED
//			addFocusListener(new FocusAdapter() {
//				@Override
//				public void focusGained(final FocusEvent fe) {
//					// TODO: Turn off any TextDecorator while focused
//					SSTextField.this.selectAll();
//				}
//			});

		// ADD KEY LISTENER FOR THE TEXT FIELD
		addKeyListener(new KeyListener() {

			@Override
			public synchronized void keyPressed(final KeyEvent ke) {

				if ((mask == MMDDYYYY) || (mask == DDMMYYYY)) {
					mask(ke);
				}
			}

			@Override
			public void keyReleased(final KeyEvent ke) {
				if ((mask == DECIMAL) || (mask == SSN)) {
					final int position = SSTextField.this.getCaretPosition();
					final int length = SSTextField.this.getText().length();
					if (mask(ke)) {
						final int newLength = SSTextField.this.getText().length();
						if (newLength > length) {
							SSTextField.this.setCaretPosition(position + 1);
						} else {
							SSTextField.this.setCaretPosition(position);
						}
					}

				}
			}

			@Override
			public void keyTyped(final KeyEvent ke) {
				// do nothing
			}

		});
	}
	
	/**
	 * Function to manage formatting date _strings with slashes as the user types to
	 * format date _string.
	 *
	 * @param _str the present string in the text field.
	 * @param _ke  the KeyEvent that occurred
	 *
	 * @return returns the formated string.
	 */
	@Deprecated
	protected static String dateMask(final String _str, final KeyEvent _ke) {

		String result = _str;

		switch (_str.length()) {
		case 1:
			if (_ke.getKeyChar() == '/') {
				result = "0" + _str;
			}
			break;
		case 2:
			if (_ke.getKeyChar() == '/') {
				// do nothing
			} else {
				result = _str + "/";
			}
			break;
		case 4:
			if (_ke.getKeyChar() == '/') {
				String newStr = _str.substring(0, 3);
				newStr = newStr + "0" + _str.substring(3, 4);
				result = newStr;
			}
			break;
		case 5:
			if (_ke.getKeyChar() != '/') {
				result = _str + "/";
			}
			break;
		default:
			// do nothing
			break;
		} // end switch

		return result;

	} // end protected String dateMask(String _str, KeyEvent _ke) {
	
	/**
	 * Function to modify the text for a decimal number as needed.
	 *
	 * @param _str                  the present string in the text field.
	 * @param numberOfDecimalPlaces number of decimal places allowed
	 *
	 * @return returns the formatted string.
	 */
	@Deprecated
	protected static String decimalMask(final String _str, final int numberOfDecimalPlaces) {
		final StringTokenizer strtok = new StringTokenizer(_str, ".", false);
		String intPart = "";
		String decimalPart = "";
		String returnStr = _str;
		// BREAK THE STRING IN TO INTERGER AND DECIMAL PARTS
		if (strtok.hasMoreTokens()) {
			intPart = strtok.nextToken();
		}
		if (strtok.hasMoreTokens()) {
			decimalPart = strtok.nextToken();
		}
		// IF THE DECIMAL PART IS MORE THAN SPECIFIED
		// TRUNCATE THE EXTRA DECIMAL PLACES
		if (decimalPart.length() > numberOfDecimalPlaces) {
			returnStr = intPart + "." + decimalPart.substring(0, numberOfDecimalPlaces);
		}

		return returnStr;
	}
		
	/**
	 * Function to format SSN
	 *
	 * @param _str the present string in the text field.
	 * @param _ke  the KeyEvent that occurred
	 *
	 * @return returns the formated string.
	 */
	@Deprecated
	protected static String ssnMask(final String _str, final KeyEvent _ke) {

		String result = _str;

		switch (_str.length()) {
		case 3:
		case 6:
			result = _str + "-";
			break;
		case 5:
		case 8:
			if (_ke.getKeyChar() == '-') {
				result = _str.substring(0, _str.length() - 1);
			}
			break;
		default:
			// do nothing
			break;
		}

		return result;

	}
	
	/**
	 * Function to manage keystrokes for masks.
	 *
	 * @param _ke the KeyEvent that occurred
	 * @return returns true if function has detected a key it does not want to
	 *         respond to like function keys etc else true.
	 */
	@Deprecated
	protected boolean mask(final KeyEvent _ke) {
		// DECLARATIONS
		String str = getText();
		final char ch = _ke.getKeyChar();

		// IF THE KEY PRESSED IS ANY OF THE FOLLOWING DO NOTHING
		if ((_ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) || (_ke.getKeyCode() == KeyEvent.VK_DELETE)
				|| (_ke.getKeyCode() == KeyEvent.VK_END) || (_ke.getKeyCode() == KeyEvent.VK_ENTER)
				|| (_ke.getKeyCode() == KeyEvent.VK_ESCAPE)) {

			return false;
		} else if (((_ke.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK)
				|| ((_ke.getModifiersEx() & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK)) {

			return false;
		} else if (!Character.isDefined(ch)) {
			return false;
		}

		if (getSelectionStart() != getSelectionEnd()) {
			str = str.substring(0, getSelectionStart()) + str.substring(getSelectionEnd(), str.length());
		}

		// BASED ON TYPE OF MASK REQUESTED MODIFY THE TEXT
		// ACCORDINGLY
		switch (mask) {
		case MMDDYYYY:
		case DDMMYYYY:
			if (getCaretPosition() < str.length()) {
				return false;
			}
			setText(dateMask(str, _ke));
			break;
		case SSN:
			setText(ssnMask(str, _ke));
			break;
		case DECIMAL:
			setText(decimalMask(str, numberOfDecimalPlaces));
			break;
		default:
			logger.log(WARNING, getColumnForLog() + ": Unknown textbox mask type of " + mask);
			break;
		} // end switch
		
		return true;
		
	} // end protected void mask(KeyEvent _ke) {
	
	/**
	 * Returns the text field mask.
	 *
	 * @return editing mask for text field
	 */
	@Deprecated
	public int getMask() {
		return mask;
	}

	/**
	 * Returns the number of decimal places required. This number is used only when
	 * mask is set to DECIMAL. Default value is 2.
	 *
	 * @return desired # of decimal places
	 */
	@Deprecated
	public int getNumberOfDecimalPlaces() {
		return numberOfDecimalPlaces;
	}

	/**
	 * Sets the text field mask.
	 *
	 * @param _mask the mask required for this text field.
	 */
	@Deprecated
	public void setMask(final int _mask) {
		final int oldValue = mask;
		mask = _mask;
		firePropertyChange("mask", oldValue, mask);

		// init();
	}

	/**
	 * Sets the number of decimal places required. This number is used only when
	 * mask is set to DECIMAL. Default value is 2.
	 *
	 * @param _numberOfDecimalPlaces desired # of decimal places
	 */
	@Deprecated
	public void setNumberOfDecimalPlaces(final int _numberOfDecimalPlaces) {
		final int oldValue = numberOfDecimalPlaces;
		numberOfDecimalPlaces = _numberOfDecimalPlaces;
		firePropertyChange("numberOfDecimalPlaces", oldValue, numberOfDecimalPlaces);
	}
		
//=====================================================================================
// 2025-12-31_BP: The code ABOVE is needed for SwingSet 4.0.x compatibility
//=====================================================================================		
		
		
	/**
	 * Constructs a new, empty text field.
	 */
	public SSTextField() {
		super();
	}

	/**
	 * Constructs a new text field with the given text.
	 * @param _text initial text
	 */
	public SSTextField(String _text) {
		super(_text);
	}

	/**
	 * Creates a SSTextField instance and binds it to the specified RowSet column.
	 *
	 * @param rowsModel          datasource to be used
	 * @param columnName name of the column to which this label should be bound
	 */
	public SSTextField(RowsModel rowsModel, String columnName) {
		super(rowsModel, columnName);
	}

	/**
	 * Creates a SSTextField instance and binds it to the specified RowSet column.
	 *
	 * @param _rowSet          datasource to be used.
	 * @param _boundColumnName name of the column to which this label should be bound
	 * @deprecated use RowsModel instead of RowSet
	 */
	@Deprecated
	public SSTextField(final RowSet _rowSet, final String _boundColumnName) {
		super(findRowsModel(_rowSet), _boundColumnName);
	}
}
