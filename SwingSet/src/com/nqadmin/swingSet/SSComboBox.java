/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2004, The Pangburn Company, Inc. and Prasanth R. Pasala.
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



import java.io.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.beans.*;
import javax.sql.*;



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
 * e.g.
 *      SSComboBox combo = new SSComboBox();
 *      String[] options = {"111", "2222", "33333"};
 *      combo.setOption(options);
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
 *      combo.setOption(options, mappings);
 *
 *      // next line is assuming myrowset has been initialized and my_column is a
 *      // column in myrowset
 *      combo.bind(myrowset,"my_column");
 *
 *
 *      Note that if you DO NOT want to use the default mappings, the custom
 *      mappings must be set before calling the bind() method to bind the
 *      combobox to a database column.
 *</pre><p>
 * @author	$Author$
 * @version	$Revision$
 */
 
public class SSComboBox   extends JComponent {


		// TEXT FIELD THAT WILL BE BOUND TO THE DATABASE
		protected  JTextField textField = new JTextField();

		// COMBO BOX THAT DISPLAYS THE DESIRED ITEMS
		protected  JComboBox  cmbDisplayed  = new JComboBox();

		// INSTANCE OF LISTENER FOR COMBO BOX
		protected MyComboListener cmbListener = new MyComboListener();

		// INSTANCE  OF LISTENER FOR THE TEXT FIELD BOUND TO DATABASE
		protected MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();

		protected  int option = 0;

		// MAPPINGS FOR THE COMBO BOX ITEMS IF DIFFERENT FROM DEFAULTS (0,1,2,..)
		protected int[] mappingValues = null;
		
		// ROWSET TO WHICH THE COMBO WILL GET VALUES FROM
		protected transient RowSet rowset;
		
		// COLUMN NAME TO WHICH THE COMBO WILL BE BOUND TO
		protected String columnName;

		/**	
		 *	Type used for combo box.
		 */
		public static final int YES_NO_OPTION        	= 0;
		public static final int YES = 1;
		public static final int NO  = 0;
		/**	
		 *	Type used for combo box.
		 */
		public static final int SEX_OPTION           	= 1;
		public static final int GENDER_OPTION		   	= 1;
		public static final int MALE  	= 1;
		public static final int FEMALE  = 0;
		/**	
		 *	Type used for combo box.
		 */
		public static final int INCLUDE_EXCLUDE_OPTION  = 2;
		public static final int EXCLUDE = 0;
		public static final int INCLUDE = 1;
		/**
		 *	Creates an object of SSComboBox.
		 */
		public SSComboBox() {
			super();
			init();
			addComponent();			
		}

		/**
		 *	Creates an instance of SSComboBox and sets the text field with which the combo
		 *box will be synchronized with.
		 *@depricated
		 *
		 */
		public SSComboBox(SSTextDocument document) {

			super();
			init();
			addComponent();
	
			this.setDocument(document);
		}
		
		/**
		 *	Any initialization code.
		 */
		protected void init(){
		// ADD KEY LISTENER TO TRANSFER FOCUS TO NEXT ELEMENT WHEN ENTER
		// KEY IS PRESSED.
			cmbDisplayed.addKeyListener(new KeyAdapter(){
				public void keyReleased(KeyEvent ke){
					if(ke.getKeyCode() == KeyEvent.VK_ENTER){
						((Component)ke.getSource()).transferFocus();
					}
				}
			});
		}
		
		/**
		 * Returns the index of the selected item in the combo box.
		 *@return index of selected item. -1 if none selected.
		 */
		public int getSelectedIndex(){
			return cmbDisplayed.getSelectedIndex();
		}
		 
		/**
		 * Returns the value associated with the selected item.
		 *@return returns the value associated with the item selected. Returns
		 * -1 if none selected.
		 */
		public int getSelectedValue(){
		  	if(mappingValues != null)
		  		return mappingValues[cmbDisplayed.getSelectedIndex()];
		  	return cmbDisplayed.getSelectedIndex();
		  	
		}
		
		/**
		 *	Returns the column name to which the combo is bound.
		 *@return returns the column name to which to combo box is bound.
		 */
		public String getColumnName(){
			return columnName;
		}
		
		/**
		 *	Returns the rowset being used to get the values.
		 *@return returns the rowset being used.
		 */
		public RowSet getRowSet(){
			return rowset;
		} 
		  
		/**
		 *	Sets the column name to which the combo box has to be bound
		 *@param columnName - column name in the rowset to which the combo box 
		 *is bound to.
		 */
		public void setColumnName(String _columnName){
			columnName = _columnName;
		} 
		
		/**
		 *	Sets the rowset to be used.
		 *@param _rowset - rowset to be used for getting the values.
		 */
		public void setRowSet(RowSet _rowset){
			rowset = _rowset;
		}
		
		/**
		 *	Sets the value.
		 */  
		public void setSelectedValue(int _value){
			textField.setText(String.valueOf(_value));
		}
		
		public void setPreferredSize(Dimension _dimension){
			cmbDisplayed.setPreferredSize(_dimension);
		}
		
		/**
		 *	Added the combo box to the JComponent
		 */
		private void addComponent(){
		//SET THE BOX LAYOUT
			setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS) );
		// SET PREFERRED SIZE FOR COMBO BOX	
			cmbDisplayed.setPreferredSize(new Dimension(150,20));
		// ADD THE COMBO BOX TO THE JCOMPONENT	
			add(cmbDisplayed);
		} 
		
		
		/**
		 *	The column name and the rowset should be set before calling this function.
		 *If the column name and rowset are set seperately then this function has to
		 *be called to bind the combo box to the column in the rowset.
		 */
		public void bind(){
			// bind the text field to the specified column
			textField.setDocument(new SSTextDocument(rowset,columnName));
//			try{
				// INITIALIZE THE VALUE OF THE TEXT FIELD TO THAT OF THE COLUMN VALUE
				// FOR THE PRESENT RECORD IN THE ROWSET.
				// THIS IS REQUIRED AS THE TEXT FIELD CAN UPDATE ITSELF ONLY WHEN THE ROWSET
				// CHANGES OR MOVES.
				textField.setDocument(new SSTextDocument(rowset, columnName));
//			}catch(SQLException se){
//				se.printStackTrace();
//			}
		// SET THE COMBO BOX ITEM DISPLAYED
			setDisplay();
		// ADDS LISTENERS FOR TEXT FIELD AND COMBO
		// IF BIND IS CALLED FOR SECOND TIME OLD LISTENERS HAVE TO BE REMOVED
		// INSTEAD OF THIS WE CAN HAVE SOME VARIABLE TO INDICATE IT LISTENERS
		// ARE ADDED ALREADY
			removeListeners();
			addListeners();
		}
			 
		/**
		 *	Binds the combo box to the specified column of the rowset.
		 *As the rowset changes the combo box item displayed changes accordingly.
		 *@param _rowset - RowSet to be used for getting the value.
		 *@param _columnName - Column to which the combo has to be bound.
		 */
		public void bind(RowSet _rowset, String _columnName){
			rowset = _rowset;
			columnName = _columnName;
			bind();
		}

		// SET THE COMBO BOX ITEM TO THE ITEM THAT CORRESPONDS TO THE VALUE IN TEXT FEILD
		private void setDisplay(){
			// GET THE DOCUMENT OF THE TEXT FIELD
			Document doc = textField.getDocument();
			try {

				String text = doc.getText(0,doc.getLength());
				if(text != null) {
					int intValue = 0;
					if( !(text.trim().equals("")) )
						intValue = Integer.parseInt(text);
					if(mappingValues == null ){
						if( intValue != cmbDisplayed.getSelectedIndex())
							cmbDisplayed.setSelectedIndex(intValue);
					}else {
// SHOULD ADD CODE TO DEAL WITH THE MAPPING VALUES
					}
				}

			}catch(BadLocationException ble){
				ble.printStackTrace();
			}catch(NullPointerException npe) {
				npe.printStackTrace();
			}catch(NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		}

		// ADDS LISTENERS FOR THE COMBO BOX AND TEXT FIELD
		private void addListeners(){
			textField.getDocument().addDocumentListener(textFieldDocumentListener);
			cmbDisplayed.addActionListener(cmbListener);
		}
		
		// REMOVES THE LISTENERS FOR TEXT FIELD AND THE COMBO BOX DISPLAYED
		private void removeListeners(){
			textField.getDocument().removeDocumentListener(textFieldDocumentListener);
			cmbDisplayed.removeActionListener(cmbListener);
		}

		/**
		 *	Sets the document to which the combo box will be bound to. Changes to this
		 *will immediately reflect in the combo box.
		 *@param document text document to which the combo box has to be bound
		 *@depricated
		 *@see bind
		 */
		public void setDocument(SSTextDocument document) {
				textField.setDocument(document);
				setDisplay();
			// NEEDED, IF THIS FUNCTION IS CALLED MORE THAN ONCE.	
			// SO THAT WE DON'T STACK UP LISTENERS AS THE NUMBER OF CALLS TO THIS
			// FUNCTION INCREASES
				removeListeners();
				addListeners();
		}

		/**
		 *	returns the combo box that has to be displayed on screen.
		 *@return returns the combo box that displays the items.
		 */
		public JComboBox getComboBox() {
			return cmbDisplayed;
		}
		
		/**
		 *	Returns the combo box to be displayed on the screen.
		 *@return returns the combo box that displays the items.
		 */
		public Component getComponent(){
			return cmbDisplayed;
		} 

		/**
		 *	adds the given array of strings as combo box items.
		 *@param options the list of options that you want to appear in the combo box.
		 */
		public boolean setOption(String[] options){
			// ADD THE SPECIFIED ITEMS TO THE COMBO BOX
			// REMOVE ANY OLD ITEMS SO THAT MULTIPLE CALLS TO THIS FUNCTION DOES NOT AFFECT
			// THE DISPLAYED ITEMS
			if(cmbDisplayed.getItemCount() != 0){
				cmbDisplayed.removeAllItems();
			}
			for(int i=0;i<options.length;i++){
				cmbDisplayed.addItem(options[i]);
			}

			return true;
		}

		/**
		 *	Sets the values for each of the items in the combo.( Values that map to the
		 *items in the combo box)
		 *@param mappings an array of values that correspond to those in the combo box.
		 */
		public void setMappingValues(int[] mappings){
			// INITIALIZE THE ARRAY AND COPY THE MAPPING VALUES
			mappingValues = new int[mappings.length];
			for(int i=0;i<mappings.length;i++){
				mappingValues[i] = mappings[i];
			}
		}

		/**
		 *	sets the options to be displayed in the combo box and their corresponding values.
		 *@param options options to be displayed in the combo box.
		 *@param mappings integer values that correspond to the options in the combo box.
		 *
		 *@return returns true if the options and mappings are set successfully.
		 *		  returns false if the size of arrays do not match or if the values could
		 *		  not be set.
		 */
		public boolean setOption(String[] options, int[]mappings){
			if(options.length != mappings.length)
				return false;
			// add the items to combo
			// REMOVE ANY OLD ITEMS SO THAT MULTIPLE CALLS TO THIS FUNCTION DOES NOT AFFECT
			// THE DISPLAYED ITEMS
			if(cmbDisplayed.getItemCount() != 0){
				cmbDisplayed.removeAllItems();
			}
			for(int i=0;i<options.length;i++){
				cmbDisplayed.addItem(options[i]);
			}
			// COPY THE MAPPING VALUES
			mappingValues = new int[mappings.length];
			for(int i=0;i<mappings.length;i++){
				mappingValues[i] = mappings[i];
			}
			return true;
		}

		/**
		 *sets the options to be displayed in the combo box and their corresponding values.
		 *@param options predefined options to be displayed in the combo box.
		 *@param mappings integer values that correspond to the options in the combo box.
		 *
		 *@return returns true if the options and mappings are set successfully.
		 *		  returns false the values could not be set.
		 */
		public void setOption(int options, int[]mappings){
			// COPY THE MAPPING VALUES
			mappingValues = new int[mappings.length];
			for(int i=0;i<mappings.length;i++){
				mappingValues[i] = mappings[i];
			}
			// SET THE OPTIONS IN THE COMBO BOX
			setOption(options);
		}

		/**
		 *sets the options to be displayed in the combo box.
		 *@param options predefined options to be displayed in the combo box.
		 */
		public boolean setOption(int options) {

			option = options;
//			System.out.println("Requested  Option: " + options);
			// REMOVE ANY OLD ITEMS SO THAT MULTIPLE CALLS TO THIS FUNCTION DOES NOT AFFECT
			// THE DISPLAYED ITEMS
			if(cmbDisplayed.getItemCount() != 0){
				cmbDisplayed.removeAllItems();
			}
			if( options == YES_NO_OPTION) {
				cmbDisplayed.addItem(new String("NO"));
				cmbDisplayed.addItem(new String("YES"));
			}
			else if( options == SEX_OPTION || options == GENDER_OPTION) {
				cmbDisplayed.addItem(new String("FEMALE"));
				cmbDisplayed.addItem(new String("MALE"));
			}

			else if( options == INCLUDE_EXCLUDE_OPTION ){
				cmbDisplayed.addItem(new String("INCLUDE"));
				cmbDisplayed.addItem(new String("EXCLUDE"));
			}
			else {
				return false;
			}

			return true;
		}

		
	
		// LISTENER FOR THE TEXT FIELD THAT CONTAINS THE INTEGER VALUE
		private class MyTextFieldDocumentListener implements DocumentListener, Serializable {

			public void changedUpdate(DocumentEvent de) {
			//	System.out.println("changed Document Changed: " + de);
				cmbDisplayed.removeActionListener(cmbListener);
				Document doc = textField.getDocument();
				try {

					String text = doc.getText(0,doc.getLength());
					if(text != null) {
						// GET THE INTEGER EQUIVALENT OF THE TEXT IN THE TEXT FIELD
						int intValue = 0;
						if( !(text.trim().equals("")) )
							intValue = Integer.parseInt(text);
						// CHECK IF THE VALUE DISPLAYED IS THE SAME AS THAT IN TEXT FIELD
						// TWO CASES 1. THE MAPPINGS FOR THE STRINGS DISPLAYED ARE GIVEN BY USER
						//  2. VALUES FOR THE ITEMS IN COMBO START FROM ZERO
						// IN CASE ONE: YOU CAN JUST CHECK FOR EQUALITY OF THE SELECTEDINDEX AND VALUE IN TEXT FIELD
						// IN CASE TWO: YOU HAVE TO CHECK IF THE VALUE IN THE MAPPINGVALUES ARRAY AT INDEX EQUAL
						// TO THE SELECTED INDEX OF THE COMBO BOX EQUALS THE VALUE IN TEXT FIELD
						// IF THESE CONDITIONS ARE MET YOU NEED NOT CHANGE COMBO BOX SELECTED ITEM
						if( (mappingValues==null && intValue != cmbDisplayed.getSelectedIndex()) || (mappingValues!=null && mappingValues[cmbDisplayed.getSelectedIndex()] != intValue) ){

		// IF EXPLICIT VALUES FOR THE ITEMS IN COMBO ARE NOT SPECIFIED THEN CODES START
		// FROM ZERO. IN SUCH A CASE CHECK IF THE NUMBER EXCEEDS THE NUMBER OF ITEMS
		// IN COMBO BOX (THIS IS ERROR CONDITION SO NOTIFY USER)

							if(mappingValues==null && (intValue <0 || intValue >= cmbDisplayed.getItemCount() )){
//								System.out.println("Option: " +option );
								System.out.println("Error: value from DB:" + intValue + "  items in combo box: " + cmbDisplayed.getItemCount());
							}
		// IF MAPPINGS  ARE SPECIFIED THEN GET THE INDEX AT WHICH THE VALUE IN TEXT FIELD
		// APPEARS IN THE MAPPINGVALUES ARRAY. SET THE SELECTED ITEM OF COMBO SO THAT INDEX
							else{
								if(mappingValues!=null){
									int i=0;
									for(;i<mappingValues.length;i++){
										if(mappingValues[i] == intValue){
											cmbDisplayed.setSelectedIndex(i);
											break;
										}
									}
									// IF THAT VALUE IS NOT FOUND IN THE GIVEN MAPPING VALUES PRINT AN ERROR MESSAGE
									if(i==mappingValues.length){
										System.out.println("change ERROR: could not find a corresponding item in combo for value " + intValue);
//										System.out.println(cmbDisplayed.getItemAt(0));
//										System.out.println(cmbDisplayed.getSelectedItem());
									}

								}
		// IF MAPPINGS ARE NOT SPECIFIED SET THE SELECTED ITEM AS THE ITEM AT INDEX
		// EQUAL TO THE VALUE IN TEXT FIELD
								else{
									cmbDisplayed.setSelectedIndex(intValue);
								}
							}
						}
					}

				}catch(BadLocationException ble){
					ble.printStackTrace();
				}catch(NullPointerException npe) {
					npe.printStackTrace();
				}catch(NumberFormatException nfe) {
					nfe.printStackTrace();
				}
				cmbDisplayed.addActionListener(cmbListener);
			}

			public void insertUpdate(DocumentEvent de) {
			//	System.out.println("insert Document Changed: " + de);
				cmbDisplayed.removeActionListener(cmbListener);
				Document doc = textField.getDocument();
				try {

					String text = doc.getText(0,doc.getLength());
					if(text != null) {
						// GET THE INTEGER EQUIVALENT OF THE TEXT IN THE TEXT FIELD
						int intValue = 0;
						if( !(text.trim().equals("")) )
							intValue = Integer.parseInt(text);
						// CHECK IF THE VALUE DISPLAYED IS THE SAME AS THAT IN TEXT FIELD
						// TWO CASES 1. THE MAPPINGS FOR THE STRINGS DISPLAYED ARE GIVEN BY USER
						//  2. VALUES FOR THE ITEMS IN COMBO START FROM ZERO
						// IN CASE ONE: YOU CAN JUST CHECK FOR EQUALITY OF THE SELECTEDINDEX AND VALUE IN TEXT FIELD
						// IN CASE TWO: YOU HAVE TO CHECK IF THE VALUE IN THE MAPPINGVALUES ARRAY AT INDEX EQUAL
						// TO THE SELECTED INDEX OF THE COMBO BOX EQUALS THE VALUE IN TEXT FIELD
						// IF THESE CONDITIONS ARE MET YOU NEED NOT CHANGE COMBO BOX SELECTED ITEM
						if( (mappingValues==null && intValue != cmbDisplayed.getSelectedIndex()) || (mappingValues!=null && mappingValues[cmbDisplayed.getSelectedIndex()] != intValue) ){

		// IF EXPLICIT VALUES FOR THE ITEMS IN COMBO ARE NOT SPECIFIED THEN CODES START
		// FROM ZERO. IN SUCH A CASE CHECK IF THE NUMBER EXCEEDS THE NUMBER OF ITEMS
		// IN COMBO BOX (THIS IS ERROR CONDITION SO NOTIFY USER)

							if(mappingValues==null && (intValue <0 || intValue >= cmbDisplayed.getItemCount()) ){
//								System.out.println("Option: " +option );
								System.out.println("Error: value from DB:" + intValue + "  items in combo box: " + cmbDisplayed.getItemCount());
							}
		// IF MAPPINGS  ARE SPECIFIED THEN GET THE INDEX AT WHICH THE VALUE IN TEXT FIELD
		// APPEARS IN THE MAPPINGVALUES ARRAY. SET THE SELECTED ITEM OF COMBO SO THAT INDEX
							else{
								if(mappingValues!=null){
									int i=0;
									for(;i<mappingValues.length;i++){
										if(mappingValues[i] == intValue){
											cmbDisplayed.setSelectedIndex(i);
											break;
										}
									}
									// IF THAT VALUE IS NOT FOUND IN THE GIVEN MAPPING VALUES PRINT AN ERROR MESSAGE
									if(i==mappingValues.length){
										System.out.println("insert ERROR: could not find a corresponding item in combo for value " + intValue);
//										System.out.println(cmbDisplayed.getItemAt(0));
//										System.out.println(cmbDisplayed.getSelectedItem());
									}

								}
		// IF MAPPINGS ARE NOT SPECIFIED SET THE SELECTED ITEM AS THE ITEM AT INDEX
		// EQUAL TO THE VALUE IN TEXT FIELD
								else{
									cmbDisplayed.setSelectedIndex(intValue);
								}
							}
						}
					}

				}catch(BadLocationException ble){
					ble.printStackTrace();
				}catch(NullPointerException npe) {
					npe.printStackTrace();
				}catch(NumberFormatException nfe) {
					nfe.printStackTrace();
				}
				cmbDisplayed.addActionListener(cmbListener);
			}

			public void removeUpdate(DocumentEvent de) {

/*			//	System.out.println("remove Document Changed: " + de);
				cmbDisplayed.removeActionListener(cmbListener);
				Document doc = textField.getDocument();
				try {

					String text = doc.getText(0,doc.getLength());
					if(text != null) {
						// GET THE INTEGER EQUIVALENT OF THE TEXT IN THE TEXT FIELD
						int intValue = 0;
						if( !(text.trim().equals("")) )
							intValue = Integer.parseInt(text);
						// CHECK IF THE VALUE DISPLAYED IS THE SAME AS THAT IN TEXT FIELD
						// TWO CASES 1. THE MAPPINGS FOR THE STRINGS DISPLAYED ARE GIVEN BY USER
						//  2. VALUES FOR THE ITEMS IN COMBO START FROM ZERO
						// IN CASE ONE: YOU CAN JUST CHECK FOR EQUALITY OF THE SELECTEDINDEX AND VALUE IN TEXT FIELD
						// IN CASE TWO: YOU HAVE TO CHECK IF THE VALUE IN THE MAPPINGVALUES ARRAY AT INDEX EQUAL
						// TO THE SELECTED INDEX OF THE COMBO BOX EQUALS THE VALUE IN TEXT FIELD
						// IF THESE CONDITIONS ARE MET YOU NEED NOT CHANGE COMBO BOX SELECTED ITEM
						if( (mappingValues==null && intValue != cmbDisplayed.getSelectedIndex()) ||
						    (mappingValues!=null && mappingValues[cmbDisplayed.getSelectedIndex()] != intValue)
						  ){

		// IF EXPLICIT VALUES FOR THE ITEMS IN COMBO ARE NOT SPECIFIED THEN CODES START
		// FROM ZERO. IN SUCH A CASE CHECK IF THE NUMBER EXCEEDS THE NUMBER OF ITEMS
		// IN COMBO BOX (THIS IS ERROR CONDITION SO NOTIFY USER)

							if( mappingValues==null && (intValue <0 || intValue >= cmbDisplayed.getItemCount() ) ){
								System.out.println("Option: " +option );
								System.out.println("Error: value from DB:" + intValue + "  items in combo box: " + cmbDisplayed.getItemCount());
							}
		// IF MAPPINGS  ARE SPECIFIED THEN GET THE INDEX AT WHICH THE VALUE IN TEXT FIELD
		// APPEARS IN THE MAPPINGVALUES ARRAY. SET THE SELECTED ITEM OF COMBO SO THAT INDEX
							else{
								if(mappingValues!=null){
									int i=0;
									for(;i<mappingValues.length;i++){
										if(mappingValues[i] == intValue){
											cmbDisplayed.setSelectedIndex(i);
											break;
										}
									}
									// IF THAT VALUE IS NOT FOUND IN THE GIVEN MAPPING VALUES PRINT AN ERROR MESSAGE
									if(i==mappingValues.length){
										System.out.println("Remove ERROR: could not find a corresponding item in combo for value " + intValue);
										System.out.println(cmbDisplayed.getItemAt(0));
										System.out.println(cmbDisplayed.getSelectedItem());
									}

								}
		// IF MAPPINGS ARE NOT SPECIFIED SET THE SELECTED ITEM AS THE ITEM AT INDEX
		// EQUAL TO THE VALUE IN TEXT FIELD
								else{
									cmbDisplayed.setSelectedIndex(intValue);
								}
							}
						}
					}

				}catch(BadLocationException ble){
					ble.printStackTrace();
				}catch(NullPointerException npe) {
					npe.printStackTrace();
				}catch(NumberFormatException nfe) {
					nfe.printStackTrace();
				}

				cmbDisplayed.addActionListener(cmbListener);
*/			}
		}

		// LISTENER FOR THE COMBO BOX. CHANGES MADE IN THE COMBO BOX ARE PASSED ON TO THE
		// TEXT FIELD THEY BY MOVING THE CHANGE TO UNDERLYING STRUCTURE (DATABASE).
		private class MyComboListener implements ActionListener, Serializable{

			public void actionPerformed(ActionEvent ae){
			//	System.out.println("action performed triggered prasanth");
				textField.getDocument().removeDocumentListener(textFieldDocumentListener);
				//System.out.flush();
				int index = cmbDisplayed.getSelectedIndex();
				try {
					String strValueInText = textField.getText();
					int valueOfText = -1;
					strValueInText.trim();
					if( !strValueInText.equals("") )
						valueOfText = Integer.parseInt(strValueInText);
			//		System.out.println("text value = " + valueOfText + "   " + index);
					if( valueOfText != index ){
						textField.setText( String.valueOf(index) );
			//			System.out.println("TextChangedTo " + String.valueOf(index) );
					}
				}catch(NullPointerException npe) {
					npe.printStackTrace();
				}catch(NumberFormatException nfe) {
					nfe.printStackTrace();
				}
				//System.out.println("Option :" + option + "  Item : " + textField.getText());

				textField.getDocument().addDocumentListener(textFieldDocumentListener);
			}
		}
}



/*
 * $Log$
 * Revision 1.8  2004/08/09 15:37:36  prasanth
 * 1. Removing elements in the combo before adding any new one.
 * 2. Added key listener to transfer focus on enter key.
 *
 * Revision 1.7  2004/08/02 14:41:10  prasanth
 * 1. Added set methods for rowset, columnname, selectedvalue.
 * 2. Added get methods for rowset, columname.
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