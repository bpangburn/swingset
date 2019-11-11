/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

package com.nqadmin.swingSet;

import java.awt.AWTKeyStroke;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.nqadmin.swingSet.datasources.SSRowSet;

/**
 * SSTextField.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * SSTextField extends the JTextField. This class provides different masks
 * like date mask, SSN mask etc.
 */
public class SSTextField extends JTextField {

    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = -8162614390803158456L;

	/**
     * Use this mask if mm/dd/yyyy format is required.
     */
    public static final int MMDDYYYY = 1;

    /**
     * Use this mask if mm/dd/yyyy format is required.
     */
    public static final int DDMMYYYY = 2;

    /**
     * Use this if the text field contains SSN
     */
    public static final int SSN = 3;

    /**
     * Use this if the text field contains decimal number and want to limit
     * number of decimal places.
     */
    public static final int DECIMAL = 4;

    /**
     * SSRowSet from which component will get/set values.
     */
    protected SSRowSet sSRowSet;

    /**
     * SSRowSet column to which the component will be bound.
     */
    protected String columnName = "";

    /**
     * Type of mask to be used for text field (default = none).
     */
    protected int mask = 0;

    /**
     * Default number of decimals to show for float, double, etc.
     */
    protected int numberOfDecimalPlaces = 2;

    /**
     * Constructs a new text field with the specified text & mask.
     *
     * @param _text    the text to be displayed.
     * @param _mask    the mask required for this textfield.
     */
    public SSTextField(String _text, int _mask) {
        super(_text);
        this.mask = _mask;
        init();
    }

    /**
     * Constructs a new, empty text field with the specified mask.
     *
     * @param _mask the mask required for this textfield.
     */
    public SSTextField(int _mask) {
        super();
        this.mask = _mask;
        init();
    }

    /**
     * Constructs a new, empty text field.
     */
    public SSTextField() {
        init();
    }

    /**
     * Constructs a new, empty text field with the specified mask & number of
     * decimal places. Use this constructor only if you are using a decimal mask.
     *
     * @param _mask    the mask required for this textfield.
     * @param _numberOfDecimalPlaces    number of decimal places required
     */
     public SSTextField(int _mask, int _numberOfDecimalPlaces) {
        this.mask = _mask;
        this.numberOfDecimalPlaces = _numberOfDecimalPlaces;
        init();
     }

    /**
     * Constructs a new, empty text field with the specified mask, number of
     * decimal places, & alignment. Use this constructor only if you are using
     * a decimal mask.
     *<pre>
     * (Horizontal alignment).
     * Valid aligmnets are:
     *  JTextField.LEFT
     *  JTextField.CENTER
     *  JTextField.RIGHT
     *  JTextField.LEADING
     *  JTextField.TRAILING
     *
     * Use this constructor only if you are using a decimal mask.
     *</pre>
     * @param _mask    the mask required for this text field.
     * @param _numberOfDecimalPlaces    number of decimal places required
     * @param _align    alignment required
     */
     public SSTextField(int _mask, int _numberOfDecimalPlaces, int _align) {
        this.mask = _mask;
        this.numberOfDecimalPlaces = _numberOfDecimalPlaces;
        setHorizontalAlignment(_align);
        init();
     }

    /**
     * Creates a SSTextField instance and binds it to the specified
     * SSRowSet column.
     *
     * @param _sSRowSet    datasource to be used.
     * @param _columnName    name of the column to which this label should be bound
     */
    public SSTextField(SSRowSet _sSRowSet, String _columnName) {
		this.sSRowSet = _sSRowSet;
        this.columnName = _columnName;
        init();
        bind();
    }

    /**
     * Sets the SSRowSet column name to which the component is bound.
     *
     * @param _columnName    column name in the SSRowSet to which the component
     *    is bound
     */
    public void setColumnName(String _columnName) {
        String oldValue = this.columnName;
        this.columnName = _columnName;
        firePropertyChange("columnName", oldValue, this.columnName);
        bind();
    }

    /**
     * Returns the SSRowSet column name to which the component is bound.
     *
     * @return column name to which the component is bound
     */
    public String getColumnName() {
        return this.columnName;
    }

    /**
     * Sets the SSRowSet to which the component is bound.
     *
     * @param _sSRowSet    SSRowSet to which the component is bound
     */
    public void setSSRowSet(SSRowSet _sSRowSet) {
        SSRowSet oldValue = this.sSRowSet;
        this.sSRowSet = _sSRowSet;
        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);
        bind();
    }

    /**
     * Returns the SSRowSet to which the component is bound.
     *
     * @return SSRowSet to which the component is bound
     */
    public SSRowSet getSSRowSet() {
        return this.sSRowSet;
    }

    /**
     * Sets the text field mask.
     *
     * @param _mask    the mask required for this text field.
     */
    public void setMask(int _mask) {
        int oldValue = this.mask;
        this.mask = _mask;
        firePropertyChange("mask", oldValue, this.mask);

        //init();
    }

    /**
     * Returns the text field mask.
     *
     * @return editing mask for text field
     */
    public int getMask() {
        return this.mask;
    }

    /**
     * Sets the number of decimal places required.
     * This number is used only when mask is set to DECIMAL.
     * Default value is 2.
     *
     * @param _numberOfDecimalPlaces desired # of decimal places
     */
    public void setNumberOfDecimalPlaces(int _numberOfDecimalPlaces) {
        int oldValue = this.numberOfDecimalPlaces;
        this.numberOfDecimalPlaces = _numberOfDecimalPlaces;
        firePropertyChange("numberOfDecimalPlaces", oldValue, this.numberOfDecimalPlaces);
    }

    /**
     * Returns the number of decimal places required.
     * This number is used only when mask is set to DECIMAL.
     * Default value is 2.
     *
     * @return desired # of decimal places
     */
    public int getNumberOfDecimalPlaces() {
        return this.numberOfDecimalPlaces;
    }

    /**
     * Sets the SSRowSet and column name to which the component is to be bound.
     *
     * @param _sSRowSet    datasource to be used.
     * @param _columnName    Name of the column to which this check box should be bound
     */
     public void bind(SSRowSet _sSRowSet, String _columnName) {
        SSRowSet oldValue = this.sSRowSet;
        this.sSRowSet = _sSRowSet;
        firePropertyChange("sSRowSet", oldValue, this.sSRowSet);

        String oldValue2 = this.columnName;
        this.columnName = _columnName;
        firePropertyChange("columnName", oldValue2, this.columnName);

        bind();
     }

    /**
     * Initialization code.
     */
    protected void init() {

        // SET PREFERRED DIMENSIONS
            setPreferredSize(new Dimension(200,20));

         // ADD FOCUS LISTENER TO THE TEXT FEILD SO THAT WHEN THE FOCUS IS GAINED
         // COMPLETE TEXT SHOULD BE SELECTED
            this.addFocusListener(new FocusAdapter(){
                @Override
				public void focusGained(FocusEvent fe){
                    SSTextField.this.selectAll();
                }
            });
            
            // TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER KEY IS PRESSED
            Set<AWTKeyStroke> forwardKeys    = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
            Set<AWTKeyStroke> newForwardKeys = new HashSet<>(forwardKeys);
            newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
            newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK ));
            setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,newForwardKeys);

         // ADD KEY LISTENER FOR THE TEXT FIELD
            this.addKeyListener(new KeyListener() {

                @Override
				public void keyReleased(KeyEvent ke) {
                    if(SSTextField.this.mask == DECIMAL || SSTextField.this.mask == SSN){
                        int position = SSTextField.this.getCaretPosition();
                        int length = SSTextField.this.getText().length();
                        if(mask(ke)){
                           int newLength = SSTextField.this.getText().length();
                           if(newLength > length){
                                SSTextField.this.setCaretPosition(position+1);
                            }
                            else{
                                SSTextField.this.setCaretPosition(position);
                            }    
                        }
                            
                    }
                }

                @Override
				public void keyTyped(KeyEvent ke) {
                	// do nothing
                }

                @Override
				public synchronized void keyPressed(KeyEvent ke) {

                    if(SSTextField.this.mask == MMDDYYYY || SSTextField.this.mask == DDMMYYYY){
                        mask(ke);
                    }
                }

            });

    } // end protected void init() {

    /**
     * Method for handling binding of component to a SSRowSet column.
     */
    protected void bind() {

        // CHECK FOR NULL COLUMN/ROWSET
            if (this.columnName==null || this.columnName.trim().equals("") || this.sSRowSet==null) {
                return;
            }

        // REMOVE LISTENERS TO PREVENT DUPLICATION
        //    removeListeners();

        // BIND THE TEXT AREA TO THE SPECIFIED COLUMN
            setDocument(new SSTextDocument(this.sSRowSet, this.columnName));

        // ADD BACK LISTENERS
        //    addListeners();;

    }

    /**
     * Function to manage keystrokes for masks.
     *
     * @param _ke    the KeyEvent that occured
     *@return returns true if function has detected a key it does not want to respond to
     *like function keys etc else true.
     */
    protected boolean mask(KeyEvent _ke) {
         // DECLARATIONS
            String str = getText();
            char ch = _ke.getKeyChar();

         // IF THE KEY PRESSED IS ANY OF THE FOLLOWING DO NOTHING
            if (_ke.getKeyCode() == KeyEvent.VK_BACK_SPACE  ||
                    _ke.getKeyCode() == KeyEvent.VK_DELETE  ||
                    _ke.getKeyCode() == KeyEvent.VK_END     ||
                    _ke.getKeyCode() == KeyEvent.VK_ENTER   ||
                    _ke.getKeyCode() == KeyEvent.VK_ESCAPE) {

                return false;
            } else if ( (_ke.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK ||
                     (_ke.getModifiersEx() & InputEvent.ALT_DOWN_MASK)  == InputEvent.ALT_DOWN_MASK    ) {

                return false;
            } else if(!Character.isDefined(ch)) {
                return false;
            }

            if (getSelectionStart() != getSelectionEnd()) {
                str = str.substring(0,getSelectionStart())
                    + str.substring(getSelectionEnd(), str.length());
            }

         // BASED ON TYPE OF MASK REQUESTED MODIFY THE TEXT
         // ACCORDINGLY
            switch(this.mask) {
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
                    setText(decimalMask(str, this.numberOfDecimalPlaces));
                    break;
                default:
                	System.out.println("Unknown textbox mask type of " + this.mask);
                	break;
            } // end switch
            return true;
     } // end protected void mask(KeyEvent _ke) {

    /**
     * Function to manage formatting date _strings with slashes as the user types
     * to format date _string.
     *
     * @param _str    the present string in the text field.
     * @param _ke    the KeyEvent that occured
     *
     * @return returns the formated string.
     */
    protected static String dateMask(final String _str, final KeyEvent _ke) {
    	
    	String result = _str;
    	
        switch(_str.length()) {
            case 1:
                if (_ke.getKeyChar() == '/') {
                	result =  "0" + _str ;
                }
                break;
            case 2:
                if ( _ke.getKeyChar() == '/' ) {
                    // do nothing
                } else {
                	result = _str +  "/";
                }
                break;
            case 4:
                if ( _ke.getKeyChar() == '/' ){
                    String newStr = _str.substring(0,3);
                    newStr = newStr + "0" + _str.substring(3,4);
                    result = newStr;
                }
                break;
            case 5:
                if ( _ke.getKeyChar() != '/' ) {
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
     * Function to format SSN
     *
     * @param _str    the present string in the text field.
     * @param _ke    the KeyEvent that occurred
     *
     * @return returns the formated string.
     */
    protected static String ssnMask(final String _str, final KeyEvent _ke) {
    	
    	String result = _str;
    	
        switch(_str.length()) {
            case 3:
            case 6:
                result = _str + "-";
                break;
            case 5:
            case 8:
                if (_ke.getKeyChar() == '-') {
                    result = _str.substring(0,_str.length()-1);
                }
                break;
           default:
        	   // do nothing
        	   break;
        }

        return result;

    }

    /**
     * Function to modify the text for a decimal number as needed.
     *
     * @param _str    the present string in the text field.
     * @param numberOfDecimalPlaces    number of decimal places allowed
     *
     * @return returns the formatted string.
     */
    protected static String decimalMask(String _str, int numberOfDecimalPlaces) {
        StringTokenizer strtok = new StringTokenizer(_str,".",false);
        String intPart = "";
        String decimalPart = "";
        String returnStr = _str;
        //  BREAK THE STRING IN TO INTERGER AND DECIMAL PARTS
        if (strtok.hasMoreTokens()) {
            intPart = strtok.nextToken();
        }
        if (strtok.hasMoreTokens()) {
            decimalPart = strtok.nextToken();
        }
        // IF THE DECIMAL PART IS MORE THAN SPECIFIED
        // TRUNCATE THE EXTRA DECIMAL PLACES
        if ( decimalPart.length() > numberOfDecimalPlaces ) {
            returnStr = intPart +"."+ decimalPart.substring(0,numberOfDecimalPlaces);
        }

        return returnStr;
    }

} // end public class SSTextField extends JTextField {

/*
 * $Log$
 * Revision 1.25  2006/05/15 16:10:38  prasanth
 * Updated copy right
 *
 * Revision 1.24  2006/04/14 22:04:33  prasanth
 * Doing the focus transfer on receiving key released event of VK_ENTER rather than on  key pressed.
 *
 * Revision 1.23  2005/04/06 15:27:21  prasanth
 * Made the return type of mask a boolean.  This is to know if mask function
 * has messed with the text or left it alone as a result of some non alphanumeric
 * key being pressed. (This is done to fix the mask problem for SSN)
 *
 * Revision 1.22  2005/02/21 16:31:33  prasanth
 * In bind checking for empty columnName before binding the component.
 *
 * Revision 1.21  2005/02/13 15:40:15  yoda2
 * Removed redundant PropertyChangeListener and VetoableChangeListener class variables and methods from components with JComponent as an ancestor.  Also removed call to init() from setMask() method.
 *
 * Revision 1.20  2005/02/12 03:29:26  yoda2
 * Added bound properties (for beans).
 *
 * Revision 1.19  2005/02/11 22:59:46  yoda2
 * Imported PropertyVetoException and added some bound properties.
 *
 * Revision 1.18  2005/02/11 20:16:06  yoda2
 * Added infrastructure to support property & vetoable change listeners (for beans).
 *
 * Revision 1.17  2005/02/10 20:13:04  yoda2
 * Setter/getter cleanup & method reordering for consistency.
 *
 * Revision 1.16  2005/02/09 19:46:32  yoda2
 * JavaDoc cleanup.
 *
 * Revision 1.15  2005/02/05 05:16:33  yoda2
 * API cleanup.
 *
 * Revision 1.14  2005/02/04 23:05:10  yoda2
 * no message
 *
 * Revision 1.13  2005/02/04 22:48:54  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.12  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.11  2004/10/25 22:13:43  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.10  2004/10/25 19:51:03  prasanth
 * Modified to use the new SSRowSet instead of  RowSet.
 *
 * Revision 1.9  2004/10/19 21:17:03  prasanth
 * Transfering focus on enter key. It was doing so only if a mask was applied
 * to the text field.
 *
 * Revision 1.8  2004/10/07 14:35:27  prasanth
 * Updated the way the masks work.
 *
 * Revision 1.7  2004/09/13 15:42:15  prasanth
 * Changed the default mask to non.
 * It used to be MMDDYYYY.
 *
 * Revision 1.6  2004/08/10 22:06:59  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.5  2004/08/09 15:34:40  prasanth
 * 1. Added bind function.
 * 2. In the key listener transferring focus on enter key.
 *
 * Revision 1.4  2004/08/02 15:48:09  prasanth
 * 1. Added the readObject method.
 *
 * Revision 1.3  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.2  2004/02/23 16:45:51  prasanth
 * Added new constructor
 * public SSTextField(int _mask, int _numberOfDecimalPlaces, int _align)
 *
 * Revision 1.1  2003/12/16 18:02:47  prasanth
 * Initial version.
 *
 */
