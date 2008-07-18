/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2006, The Pangburn Company and Prasanth R. Pasala.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  The names of its contributors may not be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.nqadmin.swingSet;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.sql.SQLException;

import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.JComboBox;

import com.nqadmin.swingSet.datasources.SSRowSet;

/**
 * SSComboBox.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Provides a way of displaying text corresponding to codes that are stored in
 * the database. By default the codes start from zero. If you want to provide a
 * different mapping for the items in the combobox then a string of integers
 * containing the corresponding numeric values for each choice must be provided.
 *
 * Note that if you DO NOT want to use the default mappings, the custom
 * mappings must be set before calling the bind() method to bind the
 * combobox to a database column.
 *
 * Also, if changing both a sSRowSet and column name consider using the bind()
 * method rather than individual setSSRowSet() and setColumName() calls. 
 *
 * e.g.
 *      SSComboBox combo = new SSComboBox();
 *      String[] options = {"111", "2222", "33333"};
 *      combo.setOptions(options);
 *
 *      For the above items the combobox assumes that the values start from zero:
 *           "111" -> 0, "2222" -> 1, "33333" -> 2
 *
 *      To give your own mappings  you can set the mappings separately or pass
 *      them along with the options:
 *
 *      SSComboBox combo = new SSComboBox();
 *      String[] options = {"111", "2222", "33333"};
 *      int[] mappings = { 1,5,7 };
 *      combo.setOptions(options, mappings);
 *
 *      // next line is assuming mysSRowSet has been initialized and my_column is a
 *      // column in mysSRowSet
 *      combo.bind(mysSRowSet,"my_column");
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
//public class SSComboBox extends JComponent {
public class SSComboBox extends JComboBox {
    
/* NOTE: It would probably be best to retool mappings such that they are stored
         as strings, similar to SSDBComboBox. */

    /**
     * Value to represent that no item has been selected in the combo box.
     */
    public static final int NON_SELECTED = (int)((Math.pow(2, 32) -1)/(-2));

    /**
     * Text field bound to the SSRowSet.
     */
    protected SSTextField textField = new SSTextField();

    /**
     * Component listener.
     */
    private final MyComboListener cmbListener = new MyComboListener();

    /**
     * Bound text field document listener.
     */

    private final MyRowSetListener rowsetListener = new MyRowSetListener();
    /**
     * Underlying values for each combo box choice if different from defaults
     * of 0, 1, 2, 3, etc.
     */
    protected int[] mappings = null;

    /**
     * SSRowSet from which component will get/set values.
     */
    protected SSRowSet sSRowSet;

    /**
     * SSRowSet column to which the component will be bound.
     */
    protected String columnName = "";
    
    /**
     * Options to be displayed in combo box.
     */
    protected String[] options;
    
    /**
     * Code representing of predefined options to be displayed in the combo box
     * (e.g. yes/no, exclude/include, etc.
     */
    protected int predefinedOptions = -1;    

    /**
     * Constant indicating that combo box should display predefined yes/no
     * options.
     */
    public static final int YES_NO_OPTION = 0;
    
    /**
     * Predefined "yes" option.
     */
    public static final int YES = 1;
    
    /**
     * Predefined "no" option.
     */    
    public static final int NO  = 0;
    
    /**
     * Constant indicating that combo box should display predefined gender
     * options.
     */    
    public static final int GENDER_OPTION = 1;
    
   /**
     * Predefined "male" option.
     */
    public static final int MALE = 0;
    
    /**
     * Predefined "female" option.
     */    
    public static final int FEMALE = 1;
    
    /**
     * Predefined "unisex" option.
     */    
    public static final int UNISEX = 2;    

    /**
     * Constant indicating that combo box should display predefined
     * include/exclude options.
     */ 
    public static final int INCLUDE_EXCLUDE_OPTION  = 2;
    
    /**
     * Predefined "exclude" option.
     */    
    public static final int EXCLUDE = 0;
    
    /**
     * Predefined "include" option.
     */    
    public static final int INCLUDE = 1;
    
    /**
     *  Creates an object of SSComboBox.
     */
    public SSComboBox() {
        init();
    }

    /**
     * Sets the value stored in the component.
     *
     * Currently not a bean property since there is no associated variable.     
     *
     * @param _value    value to assign to combo box
     */
    public void setSelectedValue(int _value) {
        try {
        	removeListeners();
            textField.setText(String.valueOf(_value));
			updateDisplay();
			addListeners();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    /**
     * Returns the combo box value associated with the currently selected item.
     *
     * Currently not a bean property since there is no associated variable.
     *
     * @return returns the value associated with the selected item OR -1 if
     * nothing is selected.
     */
    public int getSelectedValue() {
        //if (cmbDisplayed.getSelectedIndex() == -1) {
        if (getSelectedIndex() == -1) {
            return NON_SELECTED;
        }

        if (mappings != null) {
            //return mappings[cmbDisplayed.getSelectedIndex()];
            return mappings[getSelectedIndex()];
        }
        //return cmbDisplayed.getSelectedIndex();
        return getSelectedIndex();

    }
    
    /**
     * Sets the SSRowSet column name to which the component is bound.
     *
     * @param _columnName    column name in the SSRowSet to which the component
     *    is bound
     */
    public void setColumnName(String _columnName) {
        String oldValue = columnName;
        columnName = _columnName;
        firePropertyChange("columnName", oldValue, columnName);
        try {
			bind();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }    

    /**
     * Returns the column name to which the combo is bound.
     *
     * @return returns the column name to which to combo box is bound.
     */
    public String getColumnName() {
        return columnName;
    }
    
    /**
     * Sets the SSRowSet to which the component is bound.
     *
     * @param _sSRowSet    SSRowSet to which the component is bound
     */
    public void setSSRowSet(SSRowSet _sSRowSet) {
        SSRowSet oldValue = sSRowSet;
        sSRowSet = _sSRowSet;
        firePropertyChange("sSRowSet", oldValue, sSRowSet);
        try {
			bind();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }     

    /**
     * Returns the SSRowSet being used to get the values.
     *
     * @return returns the SSRowSet being used.
     */
    public SSRowSet getSSRowSet() {
        return sSRowSet;
    }
    
    /**
     * Sets the underlying values for each of the items in the combo box
     * (e.g. the values that map to the items displayed in the combo box)
     *
     * @param _mappings    an array of values that correspond to those in the combo box.
     */
    public void setMappings(int[] _mappings) {
        int[] oldValue = (int[])_mappings.clone();
        mappings = (int[])_mappings.clone();
        firePropertyChange("mappings", oldValue, mappings);
        // INITIALIZE THE ARRAY AND COPY THE MAPPING VALUES
        //mappings = new int[_mappings.length];
        //for (int i=0;i<_mappings.length;i++) {
        //    mappings[i] = _mappings[i];
        //}
    }
    
    /**
     * Returns the underlying values for each of the items in the combo box
     * (e.g. the values that map to the items displayed in the combo box)
     *
     * @return returns the underlying values for each of the items in the combo box
     */
    public int[] getMappings() {
        return mappings;
    }
    
    /**
     * Adds an array of strings as combo box items.
     *
     * @param _options    the list of options that you want to appear in the combo box.
     */
    public void setOptions(String[] _options) {
        String[] oldValue = (String[])_options.clone();
        options = (String[])_options.clone();
        firePropertyChange("options", oldValue, options);
            
        // ADD THE SPECIFIED ITEMS TO THE COMBO BOX
        // REMOVE ANY OLD ITEMS SO THAT MULTIPLE CALLS TO THIS FUNCTION DOES NOT AFFECT
        // THE DISPLAYED ITEMS
            if (getItemCount() != 0) {
                removeAllItems();
            }
            for (int i=0;i<_options.length;i++) {
                addItem(_options[i]);
            }
    }

    /**
     * Returns the items displayed in the combo box.
     *
     * @return returns the items displayed in the combo box
     */
    public String[] getOptions() {
        return options;
// if developer is adding items with addItem() then options won't have the data
// may be better to loop through all items and return that way...
    }    

    /**
     * Sets the options to be displayed in the combo box and their corresponding values.
     *
     * @param _options    options to be displayed in the combo box.
     * @param _mappings    integer values that correspond to the options in the combo box.
     *
     * @return returns true if the options and mappings are set successfully -
     *    returns false if the size of arrays do not match or if the values could
     *    not be set
     */
    public boolean setOptions(String[] _options, int[]_mappings) {
        if (_options.length != _mappings.length) {
            return false;
        }
        
        setOptions(_options);
        
        setMappings(_mappings);
        
        return true;
        
    }

    /**
     * Sets the options to be displayed in the combo box based on common
     * predefined options.
     *
     * @param _predefinedOptions predefined options to be displayed in the combo box.
     */
    public boolean setPredefinedOptions(int _predefinedOptions) {
        int oldValue = predefinedOptions;
        
        if (_predefinedOptions == YES_NO_OPTION) {
            setOptions(new String[]{"No", "Yes"});
        } else if (_predefinedOptions == SEX_OPTION || _predefinedOptions == GENDER_OPTION) {
            setOptions(new String[]{"Male", "Female", "Unisex"}); 
        } else if (_predefinedOptions == INCLUDE_EXCLUDE_OPTION) {
            setOptions(new String[]{"Include", "Exclude"});
        } else {
            return false;
        }
        
        predefinedOptions = _predefinedOptions;
        firePropertyChange("predefinedOptions", oldValue, predefinedOptions);
        
        return true;
    }
    
    /**
     * Returns the option code used to display predefined options in the
     * combo box.
     *
     * @return returns the predefined option code
     */
    public int getPredefinedOptions() {
        return predefinedOptions;
    }

    /**
     * Sets the SSRowSet and column name to which the component is to be bound.
     *
     * @param _sSRowSet    datasource to be used.
     * @param _columnName    Name of the column to which this check box should be bound
     */
    public void bind(SSRowSet _sSRowSet, String _columnName) {
        SSRowSet oldValue = sSRowSet;
        sSRowSet = _sSRowSet;
        firePropertyChange("sSRowSet", oldValue, sSRowSet);
        
        String oldValue2 = columnName;
        columnName = _columnName;
        firePropertyChange("columnName", oldValue2, columnName);
        
        try {
			bind();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Initialization code.
     */
    protected void init() {
        // ADD KEY LISTENER TO TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER
        // KEY IS PRESSED.
            addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                        ((Component)ke.getSource()).transferFocus();
                    }
                }
            });
            
        // SET PREFERRED DIMENSIONS
            setPreferredSize(new Dimension(200,20));
    }    

    /**
     * Method for handling binding of component to a SSRowSet column.
     */
    protected void bind() throws SQLException {
        
        // CHECK FOR NULL COLUMN/ROWSET
            if (columnName==null || columnName.trim().equals("") || sSRowSet==null) {
                return;
            }
            
        // REMOVE LISTENERS TO PREVENT DUPLICATION
            removeListeners();

        // BIND THE TEXT FIELD TO THE SPECIFIED COLUMN
            textField.bind(sSRowSet, columnName);

        // SET THE COMBO BOX ITEM DISPLAYED
            updateDisplay();

        // ADD BACK LISTENERS
            addListeners();
               
    }

    /**
     * Updates the value displayed in the component based on the SSRowSet column
     * binding.
     */
    protected void updateDisplay() throws SQLException {
        try {
            String text = "";
            try{
            	text =  sSRowSet.getRow() > 0 ? sSRowSet.getString(columnName) : textField.getText().trim();
            	//System.out.println(columnName + "  " + text + " " + textField.getText());
            }catch(SQLException se) {
            	
            }

            // GET THE INTEGER EQUIVALENT OF THE TEXT IN THE TEXT FIELD            
            if ( text != null && !(text.trim().equals("")) ) {
            	int intValue = 0;
                intValue = Integer.parseInt(text.trim());
            
	            // CHECK IF THE VALUE DISPLAYED IS THE SAME AS THAT IN TEXT FIELD
	            // TWO CASES 1. THE MAPPINGS FOR THE STRINGS DISPLAYED ARE GIVEN BY USER
	            //  2. VALUES FOR THE ITEMS IN COMBO START FROM ZERO
	            // IN CASE ONE: YOU CAN JUST CHECK FOR EQUALITY OF THE SELECTEDINDEX AND VALUE IN TEXT FIELD
	            // IN CASE TWO: YOU HAVE TO CHECK IF THE VALUE IN THE MAPPINGVALUES ARRAY AT INDEX EQUAL
	            // TO THE SELECTED INDEX OF THE COMBO BOX EQUALS THE VALUE IN TEXT FIELD
	            // IF THESE CONDITIONS ARE MET YOU NEED NOT CHANGE COMBO BOX SELECTED ITEM
	            if ( (mappings==null && intValue != getSelectedIndex()) ||
	                 (mappings!=null && getSelectedIndex() == -1)       ||
	                 (mappings!=null && mappings[getSelectedIndex()] != intValue) ) {
	
	                if (mappings==null && (intValue <0 || intValue >= getItemCount() )) {
	                // IF EXPLICIT VALUES FOR THE ITEMS IN COMBO ARE NOT SPECIFIED THEN CODES START
	                // FROM ZERO. IN SUCH A CASE CHECK IF THE NUMBER EXCEEDS THE NUMBER OF ITEMS
	                // IN COMBO BOX (THIS IS ERROR CONDITION SO NOTIFY USER)
	                    System.out.println("Error: value from DB:" + intValue + "  items in combo box: " + getItemCount());
	                    setSelectedIndex(-1);
	                } else {
	                // IF MAPPINGS  ARE SPECIFIED THEN GET THE INDEX AT WHICH THE VALUE IN TEXT FIELD
	                // APPEARS IN THE MAPPINGVALUES ARRAY. SET THE SELECTED ITEM OF COMBO SO THAT INDEX
	                    if (mappings!=null) {
	                        int i=0;
	                        for (;i<mappings.length;i++) {
	                            if (mappings[i] == intValue) {
	                                setSelectedIndex(i);
	                                break;
	                            }
	                        }
	                        // IF THAT VALUE IS NOT FOUND IN THE GIVEN MAPPING VALUES PRINT AN ERROR MESSAGE
	                        if (i==mappings.length) {
	                            System.out.println("change ERROR: could not find a corresponding item in combo for value " + intValue);
	                            setSelectedIndex(-1);
	                        }
	                    } else {
	                    // IF MAPPINGS ARE NOT SPECIFIED SET THE SELECTED ITEM AS THE ITEM AT INDEX
	                    // EQUAL TO THE VALUE IN TEXT FIELD
	                        setSelectedIndex(intValue);
	                    }
	                }
	            }	            
            }
            else {
            	setSelectedIndex(-1);
            }
        } catch(NumberFormatException nfe) {
            nfe.printStackTrace();
        }

    }
    
    /**
     * Adds listeners for component and rowset
     */
    private void addListeners() {
    	sSRowSet.addRowSetListener(rowsetListener);
        addActionListener(cmbListener);
    }

    /**
     * Removes listeners for component and rowset.
     */
    private void removeListeners() {
    	sSRowSet.removeRowSetListener(rowsetListener);
        removeActionListener(cmbListener);
    }


    /**
     * Rowset Listener for updating the value displayed.
     */
    private class MyRowSetListener implements RowSetListener, Serializable {

		public void cursorMoved(RowSetEvent arg0) {
            removeActionListener(cmbListener);            
            try {
				updateDisplay();
			} catch (SQLException e) {
				e.printStackTrace();
			}            
			addActionListener(cmbListener);
		}

		public void rowChanged(RowSetEvent event) {
			removeActionListener(cmbListener);            
            try {
				updateDisplay();
			} catch (SQLException e) {
				e.printStackTrace();
			}            
			addActionListener(cmbListener);
		}

		public void rowSetChanged(RowSetEvent event) {
			removeActionListener(cmbListener);            
            try {
				updateDisplay();
			} catch (SQLException e) {
				e.printStackTrace();
			}            
			addActionListener(cmbListener);
		}
    	
    }
    /**
     * Listener(s) for the component's value used to propigate changes back to
     * bound text field.
     */
    private class MyComboListener implements ActionListener, Serializable {

        public void actionPerformed(ActionEvent ae) {
            int index = getSelectedIndex();
            try {
                if (index == -1) {
                    textField.setText("");
                } else {
                    String strValueInText = textField.getText();
                    Integer valueOfText = null;
                    if(strValueInText != null && !"".equals(strValueInText.trim())) {
                    	strValueInText = strValueInText.trim();
                   		valueOfText = Integer.valueOf(strValueInText);
                    }

                    if( mappings == null && ( valueOfText == null || valueOfText.intValue() != index )) {
                        textField.setText( String.valueOf(index) );
                    }
                    else if(mappings != null && mappings.length > index && (valueOfText ==null || valueOfText.intValue() != mappings[index])){
                        textField.setText(String.valueOf(mappings[index]));
                    }
                }
            } catch(NullPointerException npe) {
                npe.printStackTrace();
            } catch(NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
    }



// DEPRECATED STUFF....................
    /**
     * Constant indicating that combo box should display predefined gender
     * options.
     *
     * @deprecated
     * @see #GENDER_OPTION     
     */
    public static final int SEX_OPTION = 1;
    
    /**
     * Predefined "unisex" option.
     *
     * @deprecated
     * @see #UNISEX     
     */    
    public static final int UNI_SEX = 2;


    /**
     * returns the combo box that has to be displayed on screen.
     *
     * @return returns the combo box that displays the items.
     *
     * @deprecated
     */
    public JComboBox getComboBox() {
        return this;
    }

    /**
     * Returns the combo box to be displayed on the screen.
     *
     * @return returns the combo box that displays the items.
     *
     * @deprecated
     */
    public Component getComponent() {
        return this;
    }
    
    /**
     * Adds an array of strings as combo box items.
     *
     * @param _options    the list of options that you want to appear in the combo box.
     *
     * @return returns true if the options and mappings are set successfully -
     *    returns false if the size of arrays do not match or if the values could
     *    not be set     
     *
     * @deprecated
     * @see #setOptions     
     */
    public boolean setOption(String[] _options) {
        setOptions(_options);
        return true;
    }
    
    /**
     * Sets the options to be displayed in the combo box and their corresponding values.
     *
     * @param _options    options to be displayed in the combo box.
     * @param _mappings    integer values that correspond to the options in the combo box.
     *
     * @return returns true if the options and mappings are set successfully -
     *    returns false if the size of arrays do not match or if the values could
     *    not be set
     *
     * @deprecated
     * @see #setOptions
     */
    public boolean setOption(String[] _options, int[]_mappings) {
        return setOptions(_options, _mappings);
    }

    /**
     * Sets the options to be displayed in the combo box and their corresponding values.
     *
     * @param _options    predefined options to be displayed in the combo box.
     * @param _mappings    integer values that correspond to the options in the combo box.
     *
     * @deprecated
     * @see #setPredefinedOptions
     * @see #setMappings    
     */
    public void setOption(int _options, int[]_mappings) throws PropertyVetoException {
        setPredefinedOptions(_options);
        setMappings(_mappings);
    }

    /**
     * Sets the options to be displayed in the combo box based on common
     * predefined options.
     *
     * @param _option predefined options to be displayed in the combo box.
     *
     * @deprecated
     * @see #setPredefinedOptions     
     */
    public boolean setOption(int _option) {
        return setPredefinedOptions(_option);
    }
    
    /**
     * Sets the underlying values for each of the items in the combo box
     * (e.g. the values that map to the items displayed in the combo box)
     *
     * @param _mappings    an array of values that correspond to those in the combo box.
     *
     * @deprecated
     * @see #setMappings       
     */
    public void setMappingValues(int[] _mappings) {
        setMappings(_mappings);
    }    

} // end public class SSComboBox extends JComboBox {



/*
 * $Log$
 * Revision 1.35  2006/05/15 16:10:38  prasanth
 * Updated copy right
 *
 * Revision 1.34  2005/02/21 16:31:29  prasanth
 * In bind checking for empty columnName before binding the component.
 *
 * Revision 1.33  2005/02/13 15:38:20  yoda2
 * Removed redundant PropertyChangeListener and VetoableChangeListener class variables and methods from components with JComponent as an ancestor.
 *
 * Revision 1.32  2005/02/11 22:59:25  yoda2
 * Imported PropertyVetoException and added some bound properties.
 *
 * Revision 1.31  2005/02/11 20:15:58  yoda2
 * Added infrastructure to support property & vetoable change listeners (for beans).
 *
 * Revision 1.30  2005/02/10 20:12:54  yoda2
 * Setter/getter cleanup & method reordering for consistency.
 *
 * Revision 1.29  2005/02/10 03:46:47  yoda2
 * Replaced all setDisplay() methods & calls with updateDisplay() methods & calls to prevent any setter/getter confusion.
 *
 * Revision 1.28  2005/02/07 20:36:35  yoda2
 * Made private listener data members final.
 *
 * Revision 1.27  2005/02/07 04:20:13  yoda2
 * JavaDoc cleanup.
 *
 * Revision 1.26  2005/02/04 22:48:52  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.25  2005/02/03 23:50:56  prasanth
 * 1. Removed commented out code.
 * 2. Modified setDisplay function to change value only when underlying value
 *      does not match with that displayed.
 * 3. Using setDisplay in document listener.
 *
 * Revision 1.24  2005/02/02 23:36:58  yoda2
 * Removed setMaximiumSize() calls.
 *
 * Revision 1.23  2005/01/19 20:54:43  yoda2
 * API cleanup.
 *
 * Revision 1.22  2005/01/19 03:15:44  yoda2
 * Got rid of setBinding and retooled public/private bind() methods and how they are called.
 *
 * Revision 1.21  2005/01/18 22:27:24  yoda2
 * Changed to extend JComboBox rather than JComponent.  Deprecated bind(), setSSRowSet(), & setColumnName().
 *
 * Revision 1.20  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.19  2004/11/01 15:53:30  yoda2
 * Fixed various JavaDoc errors.
 *
 * Revision 1.18  2004/10/25 23:09:01  prasanth
 * In setOption using the class variable rather than function argument.
 *
 * Revision 1.17  2004/10/25 22:03:18  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.16  2004/10/25 19:51:02  prasanth
 * Modified to use the new SSRowSet instead of  RowSet.
 *
 * Revision 1.15  2004/09/21 18:57:47  prasanth
 * Added Unisex as an option.
 *
 * Revision 1.14  2004/09/21 14:14:14  prasanth
 * Swapped the codes for FEMALE & MALE.
 *
 * Revision 1.13  2004/09/13 15:41:25  prasanth
 * Added a constant to indicate that there is no selection in combobox.
 * This value will be returned when  the selected index in combo box is -1.
 *
 * Revision 1.12  2004/09/02 16:20:17  prasanth
 * Added support for mapping values in setDisplay function.
 * Combo Listener was not handling mapping values  so added that.
 *
 * Revision 1.11  2004/08/12 23:51:16  prasanth
 * Updating the value to null if the selected index is -1.
 *
 * Revision 1.10  2004/08/10 22:06:59  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.9  2004/08/09 21:31:13  prasanth
 * Corrected wrong function call. removeAll() --> removeAllItems()
 *
 * Revision 1.8  2004/08/09 15:37:36  prasanth
 * 1. Removing elements in the combo before adding any new one.
 * 2. Added key listener to transfer focus on enter key.
 *
 * Revision 1.7  2004/08/02 14:41:10  prasanth
 * 1. Added set methods for sSRowSet, columnname, selectedvalue.
 * 2. Added get methods for sSRowSet, columname.
 * 3. Added addComponent and removeListener functions (private).
 *
 * Revision 1.6  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.5  2004/02/23 16:39:35  prasanth
 * Added GENDER_OPTION.
 *
 * Revision 1.4  2003/12/16 18:01:40  prasanth
 * Documented versions for release 0.6.0
 *
 * Revision 1.3  2003/10/31 16:04:44  prasanth
 * Added method getSelectedIndex() and getSelectedValue().
 *
 * Revision 1.2  2003/09/25 14:27:45  yoda2
 * Removed unused Import statements and added preformatting tags to JavaDoc descriptions.
 *
 * Revision 1.1.1.1  2003/09/25 13:56:43  yoda2
 * Initial CVS import for SwingSet.
 *
 */