/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003, The Pangburn Company, Inc. and Prasanth R. Pasala.
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
import java.util.Vector;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sql.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;



/**
 * SSDBComboBox.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Similar to the SSComboBox, but used when both the 'bound' values and the
 * 'display' values are pulled from a database table.  Generally the bound
 * value represents a foreign key to another table, and the combobox needs to
 * diplay a list of one (or more) columns from the other table.
 *
 * e.g.
 *
 *      Consider two tables:
 *        1. part_data (part_id, part_name, ...)
 *        2. shipment_data (shipment_id, part_id, quantity, ...)
 *
 *      Assume you would like to develop a screen for the shipment table and you
 *      want to have a screen with a combobox where the user can choose a
 *      part and a textbox where the user can specify a  quantity.
 *
 *      In the combobox you would want to display the part name rather than
 *      part_id so that it is easier for the user to choose. At the same time you
 *      want to store the id of the part chosen by the user in the shipment
 *      table.
 *
 *      Connection connection = null;
 *      JdbcRowSet rowset = null;
 *      SSDataNavigator navigator = null;
 *      SSDBComboBox combo = null;
 *
 *      try {
 *
 *      // CREATE A DATABASE CONNECTION OBJECT
 *           Connection connection = new Connection(........);
 *
 *      // CREATE AN INSTANCE OF JDBC ROWSET
 *           JdbcRowset rowset = new JdbcRowSet();
 *           rowset.setUrl("<database path>");
 *           rowset.setUsername("user");
 *           rowset.setPassword("pass");
 *           rowset.setCommand("SELECT * FROM shipment_data;");
 *
 *      // DATA NAVIGATOR CALLS THE EXECUTE AND NEXT FUNCTIONS ON THE ROWSET.
 *      // IF YOU ARE NOT USING THE DATA NAVIGATOR YOU HAVE TO INCLUDE THOSE.
 *      //   rowset.execute();
 *      //   rowset.next();
 *           SSDataNavigator navigator = new SSDataNavigator(rowset);
 *
 *      // QUERY FOR THE COMBOBOX.
 *           String query = "SELECT * FROM part_data;";
 *
 *      // CREATE AN INSTANCE OF THE SSDBCOMBOBOX WITH THE CONNECTION OBJECT
 *      // QUERY AND COLUMN NAMES
 *           combo = new SSDBComboBox(connection,query,"part_id","part_name");
 *
 *      // THIS BASICALLY SPECIFIES THE COLUMN AND THE ROWSET WHERE UPDATES HAVE
 *      // TO BE MADE.
 *           combo.bind(rowset,"part_id");
 *           combo.execute();
 *
 *     // CREATE A TEXTFIELD
 *         JTextField myText = new JTextField();
 *      	myText.setDocument(new SSTextDocument(rowset, "quantity");
 *
 *      } catch(Exception e) {
 *      // EXCEPTION HANDLER HERE...
 *      }
 *
 *
 *      // ADD THE SSDBCOMBOBOX TO THE JFRAME
 *           getContentPane().add(combo.getComboBox());
 *
 *      // ADD THE JTEXTFIELD TO THE JFRAME
 *  	  getContentPane().add(myText);
 *</pre><p>
 * @author	$Author$
 * @version	$Revision$
 */
public class SSDBComboBox  extends JComponent{


		// text field that is used as an intermediatery storage point between the database
		// and the combo.
		private JTextField textField = null;

		/*  ComboBox used to display the values.
		 */
		private JComboBox  cmbDisplayed  = new JComboBox();

		/*	Database connection object
		 */
		private Connection conn = null;

		/*	Query used to retrieve all possible values.
		 */
		private String query = null;

		/*	the column name  whose value has to be written back
		 * to the database when user chooses an item in the combo. This is generally the
		 * column in the foreign table to which the foreign key maps to.
		 */
		private String columnName = null;

		/*	the column name whose values have to be displayed in the combo.
		 */
		private String displayColumnName = null;

		/*	An additional column (if desired) whose values will also be displayed in the combo
		 */
		private String secondDisplayColumnName = null;

		private Vector columnVector = new Vector();

		// number of items in the combo box.
		private int numberOfItems = 0;

		// rowset used to retrieve the info from the database.
		private RowSet rowset = null;

		// instance of the listener for the combo box.
		final MyComboListener cmbListener = new MyComboListener();

		// instance of the listener for the text field.
		final MyTextFieldDocumentListener textFieldDocumentListener = new MyTextFieldDocumentListener();
		
		// seperator to be used if two column values are displayed
		String seperator = " - ";

		/**
		 *	Creates an object of the SSDBComboBox.
		 */
		public SSDBComboBox() {
			super();
		}

		/**
		 *	Constructs a SSDBComboBox  with the given parameters.
		 *@param _conn database connection to be used.
		 *@param _query query to be used to retrieve the values from the database.
		 *@param _columnName column name whose value has to be stored.
		 *@param _displayColumnName column name whose values are displayed in the combo box.
		 *@param _textField a text field to which the combo box has to be synchronized
		 */
		public SSDBComboBox(Connection _conn, String _query, String _columnName, String _displayColumnName, JTextField _textField){

			super();

			conn 				= _conn;
			query 				= _query;
			columnName 			= _columnName;
			displayColumnName 	= _displayColumnName;
			textField  			= _textField;
			textField.setPreferredSize(new Dimension(200,20));
			cmbDisplayed.setPreferredSize(new Dimension(200,20));
			textField.setMaximumSize(new Dimension(200,20));
			cmbDisplayed.setMaximumSize(new Dimension(200,20));


		}

		/**
		 *	Constructs a SSDBComboBox  with the given parameters.
		 *@param _conn database connection to be used.
		 *@param _query query to be used to retrieve the values from the database.
		 *@param _columnName column name whose value has to be stored.
		 *@param _displayColumnName column name whose values are displayed in the combo box.
		 */
		public SSDBComboBox(Connection _conn, String _query, String _columnName, String _displayColumnName){

			super();

			conn 				= _conn;
			query 				= _query;
			columnName 			= _columnName;
			displayColumnName 	= _displayColumnName;
			textField			= new JTextField();
			textField.setPreferredSize(new Dimension(200,20));
			cmbDisplayed.setPreferredSize(new Dimension(200,20));
			textField.setMaximumSize(new Dimension(200,20));
			cmbDisplayed.setMaximumSize(new Dimension(200,20));


		}

		/**
		 *	sets the new rowset for the combo box.
		 *@param _rowset rowset to which the combo has to update values.
		 */
		public void setRowSet(RowSet _rowset){
			rowset = _rowset;
		}

		/**
		 *	sets the connection object to be used.
		 *@param _conn connection object used for database.
		 */
		public void setConnection( Connection _conn) {
			conn = _conn;
		}

		/**
		 *	sets the query used to display items in the combo box.
		 *@param _query query to be used to get values from database( to display combo box items)
		 */
		public void setQuery(String _query){
			query = _query;
		}

		/**
		 *
		 */
		public void setColumnName(String _columnName){
			columnName = _columnName;
		}

		/**
		 *	sets the column name whose values have to be displayed in combo box.
		 *@param _displayColumnName column name whose values have to be displayed.
		 */
		public void setDisplayColumnName(String _displayColumnName){
			displayColumnName = _displayColumnName;
		}

		/**
		 *	sets the second display name.
		 *If more than one column have to displayed then use this.
		 *For the parts example given above. If you have a part description in part table.
		 *Then you can display both part name and part description.
		 *@param _secondDisplayColumnName  column name whose values have to be
		 *		displayed in the combo in addition to the first column name.
		 */
		public void setSecondDisplayColumnName(String _secondDisplayColumnName){
			secondDisplayColumnName = _secondDisplayColumnName;
		}

		public void setTextField(JTextField _textField){
			textField = _textField;
		}

		/**
		 *	returns connection object used to get values from database.
		 *@return returns a Connection object.
		 */
		public Connection getConnection() {
			return conn;
		}

		/**
		 *	returns the number of items present in the combo box.
		 *@return returns the number of items present in the combo box.
		 */
		public int getNumberOfItems(){
			return numberOfItems;
		}

		/**
		 *	returns the query used to retrieve values from database for the combo box.
		 *@return returns the query used.
		 */
		public String getQuery(){
			return query;
		}

		/**
		 *	returns the name of column from which values for items in combo are taken from.
		 *@return returns column name  representing the values for combo box items.
		 */
		public String getColumnName(){
			return columnName;
		}

		/**
		 *	returns the column name whose values are displayed in the combo box.
		 *@return returns the name of the column used to get values for combo box items.
		 */
		public String getDisplayColumnName(){
			return displayColumnName;
		}

		/**
		 *	returns the second column name whose values are also displayed in the combo box.
		 *@return returns the name of the column used to get values for combo box items.
		 *			returns NULL if the second display column is not provided.
		 */
		public String getSecondDisplayColumnName(){
			return secondDisplayColumnName;
		}

		/**
		 *	returns the text field used to synchronize with the rowset.
		 *@return  returns the text field used to synchronize with the rowset.
		 */
		public JTextField getTextField(){
			return textField;
		}
/*
		public void setVisible(boolean isVisible) {
			cmbDisplayed.setVisible(isVisible);
		}
*/

		/**
		 *	set the seperator to be used when multiple columns are displayed
		 *@param _seperator seperator to be used.
		 *
		 */
		 public void setSeperator(String _seperator){
		 	seperator = _seperator;
		 }
		 
		/**
		 *	Executes the query and adds items to the combo box based on the values
		 *retrieved from the database.
		 */
		public void execute() throws SQLException,Exception {

			this.removeListeners();

			Statement statement = conn.createStatement();

			if(query.equals(""))
				throw new Exception("Query is empty");

			ResultSet rs = statement.executeQuery(query);

			// CLEAR ALL ITEMS FROM COMBO AND VECTOR STORING ITS CORRESPONDING VALUES.
			cmbDisplayed.removeAllItems();
			columnVector.clear();

			// USER CAN SPECIFY A ROWSET INSTEAD OF THE QUERY.
			// IN SUCH A CASE THE VALUES HAVE TO BE TAKEN FROM ROWSET
			int i = 0;
			if(rowset == null) {
				while(rs.next()){
					// IF TWO COLUMNS HAVE TO BE DISPLAYED IN THE COMBO THEY SEPERATED BY SEMI-COLON
					if( secondDisplayColumnName != null){
						cmbDisplayed.addItem(rs.getString(displayColumnName) + seperator + rs.getString(secondDisplayColumnName));
					} else {
						cmbDisplayed.addItem(rs.getString(displayColumnName));
					}
					// ADD THE ID OF THE ITEM TO A VECTOR.
					columnVector.add(i,new Long(rs.getLong(columnName)));
					i++;
				}
			// IF THE ROWSET IS GIVEN GET VALUES FROM ROWSET
			}else {
				// NOTE THE POSITION OF ROWSET , MOVE TO THE FIRST RECORD AND GET ALL VALUES
				// AND THEN MOVE THE ROWSET BACK TO ITS PREVIOUS POSITION.
				int rowNumber = rowset.getRow();
				System.out.println(" present row number " + rowNumber);
				rowset.beforeFirst();
				while(rowset.next()){
					if( i == 0)
						rowset.first();
					// IF TWO COLUMNS HAVE TO BE DISPLAYED IN THE COMBO THEY SEPERATED BY SEMI-COLON
					if( secondDisplayColumnName != null){
						cmbDisplayed.addItem(rowset.getString(displayColumnName) + " : " + rowset.getString(secondDisplayColumnName));
					} else {
						cmbDisplayed.addItem(rowset.getString(displayColumnName));
					}
					// ADD THE ID OF THE ITEM TO A VECTOR.
					columnVector.add(i,new Long(rowset.getLong(columnName)));
					i++;
				}
				// POSITION THE ROWSET TO ITS PREVIOUS POSITION.
				if( rowNumber == 0)
					rowset.beforeFirst();
				else
					rowset.absolute(rowNumber);
				System.out.println(" present row number " + rowset.getRow());
			}

			// STORE THE NUMBER OF ITEMS IN THE COMBO BOX.
			numberOfItems = i;

			// DISPLAYS THE ITEM CORRESPONDING TO THE PRESENT VALUE IN THE DATABASE BASE.
			this.setCmbDisplay();
			this.addListeners();

		}

		// SETS THE COMBOBOX ITEM TO THE ONE CORRESPONDING TO THE VALUE PRESENT AT
		// COLUMN TO WHICH COMBO IS BOUND.
		private void setCmbDisplay() {

			Document doc = textField.getDocument();
			try {
				// GET THE VALUE FROM TEXT FIELD
				String text = doc.getText(0,doc.getLength());
				if(text != null) {
					long valueInText = Long.parseLong(text);
					// GET THE INDEX WHERE THIS VALUE IS IN THE VECTOR.
					int indexCorrespondingToLong = columnVector.indexOf(new Long(valueInText));
					// SET THE SELECTED ITEM OF COMBO TO THE ITEM AT THE INDEX FOUND FROM
					// ABOVE STATEMENT
					if( indexCorrespondingToLong != cmbDisplayed.getSelectedIndex())
						cmbDisplayed.setSelectedIndex(indexCorrespondingToLong);
				}
			}catch(BadLocationException ble){
				ble.printStackTrace();
			}catch(NullPointerException npe) {
			}catch(NumberFormatException nfe) {
			}

		}

		/**
		 *	Binds the comboBox to the specified column in the given rowset.
		 *@param rowset rowset to which updates have to be made.
		 *@param column column name in the rowset to which these updates have to be made.
		 */
		public void bind(RowSet rs, String column){
			textField.setDocument(new SSTextDocument(rs,column));
		}

		// ADDS LISTENERS FOR TEXT FIELD AND COMBO BOX.
		private void addListeners(){

			cmbDisplayed.addActionListener(cmbListener);
			textField.getDocument().addDocumentListener(textFieldDocumentListener);

		}

		// REMOVES THE LISTENERS FOR THE COMBOBOX AND TEXT FIELD
		private void removeListeners(){

			cmbDisplayed.removeActionListener(cmbListener);
			textField.getDocument().removeDocumentListener(textFieldDocumentListener);

		}

		// LISTENER FOR THE TEXT FIELD
		private class MyTextFieldDocumentListener	implements DocumentListener {

			public void changedUpdate(DocumentEvent de) {
				//System.out.println("changed Document Changed: " + de);

				cmbDisplayed.removeActionListener(cmbListener);

				Document doc = textField.getDocument();
				try {
					String text = doc.getText(0,doc.getLength());
					if(text != null) {
						long valueInText = Long.parseLong(text);
						int indexCorrespondingToLong = columnVector.indexOf(new Long(valueInText));
						if( indexCorrespondingToLong != cmbDisplayed.getSelectedIndex())
							cmbDisplayed.setSelectedIndex(indexCorrespondingToLong);
					}

				}catch(BadLocationException ble){
					ble.printStackTrace();
				}catch(NullPointerException npe) {
				}catch(NumberFormatException nfe) {
				}

				cmbDisplayed.addActionListener(cmbListener);

			}

			public void insertUpdate(DocumentEvent de) {
				//System.out.println("insert Document Changed: " + de);
				cmbDisplayed.removeActionListener(cmbListener);

				Document doc = textField.getDocument();
				try {

					String text = doc.getText(0,doc.getLength());
					if(text != null) {
						long valueInText = Long.parseLong(text);
						int indexCorrespondingToLong = columnVector.indexOf(new Long(valueInText));
						if( indexCorrespondingToLong != cmbDisplayed.getSelectedIndex())
							cmbDisplayed.setSelectedIndex(indexCorrespondingToLong);
					}

				}catch(BadLocationException ble){
					ble.printStackTrace();
				}catch(NullPointerException npe) {
				}catch(NumberFormatException nfe) {
				}

				cmbDisplayed.addActionListener(cmbListener);
			}

			public void removeUpdate(DocumentEvent de) {

				//System.out.println("remove Document Changed: " + de);
				cmbDisplayed.removeActionListener(cmbListener);

				Document doc = textField.getDocument();
				try {

					String text = doc.getText(0,doc.getLength());
					if(text != null) {
						long valueInText = Long.parseLong(text);
						int indexCorrespondingToLong = columnVector.indexOf(new Long(valueInText));
						if( indexCorrespondingToLong != cmbDisplayed.getSelectedIndex())
							cmbDisplayed.setSelectedIndex(indexCorrespondingToLong);
					}

				}catch(BadLocationException ble){
					ble.printStackTrace();
				}catch(NullPointerException npe) {
				}catch(NumberFormatException nfe) {
				}

				cmbDisplayed.addActionListener(cmbListener);
			}

		}

		// LISTENER FOR THE COMBO BOX.
		private class MyComboListener implements ActionListener {

			public void actionPerformed(ActionEvent ae){

				textField.getDocument().removeDocumentListener(textFieldDocumentListener);

				//System.out.println("action performed triggered");
				// GET THE INDEX CORRESPONDING TO THE SELECTED TEXT IN COMBO
				int index = cmbDisplayed.getSelectedIndex();
				System.out.println(index);
				//System.out.println(index);
				// IF THE USER WANTS TO REMOVE COMPLETELY THE VALUE IN THE FIELD HE CHOOSES
				// THE EMPTY STRING IN COMBO THEN THE TEXT FIELD IS SET TO EMPTY STRING
				if( index != -1 ) {
					try {
						// NOW LOOK UP THE VECTOR AND GET THE VALUE CORRESPONDING TO THE TEXT SELECTED IN THE COMBO
						long valueCorresponingToIndex = ( (Long)columnVector.get(index) ).longValue() ;
						System.out.println("Value Corresponding To CMB: " + valueCorresponingToIndex);
						//GET THE TEXT IN THE TEXT FIELD
						String strValueinTextField = textField.getText();
						//INITIALIZE THE  LONG VALUE IN TEXT TO -1
						long valueInText = -1;
						// IF THE TEXT IS NOT NULL PARSE ITS LONG VALUE
						if(!strValueinTextField.equals("") )
							valueInText = Long.parseLong(strValueinTextField);
						//System.out.println("Value in text: " + valueInText );

						// IF THE LONG VALUE CORRESPONDING TO THE SELECTED TEXT OF COMBO NOT EQUAL
						// TO THAT IN THE TEXT FIELD THEN CHANGE THE TEXT IN THE TEXT FIELD TO THAT VALUE
						// IF ITS THE SAME LEAVE IT AS IS
						if( valueInText != valueCorresponingToIndex){
							textField.setText( String.valueOf(valueCorresponingToIndex) );
							//System.out.println(textField.getText());
						}

					}catch(NullPointerException npe) {
					}catch(NumberFormatException nfe) {
					}
				}

				textField.getDocument().addDocumentListener(textFieldDocumentListener);

				//System.out.println("Option :" + option + "  Item : " + textField.getText());
			}
		}


		/**
		 *	Returns the combobox so that it can be added to a container.
		 *@returns a JComboBox which displays the required items.
		 */
		public JComboBox getComboBox() {
			return cmbDisplayed;
		}


}



/*
 * $Log$
 * Revision 1.2  2003/09/25 14:27:45  yoda2
 * Removed unused Import statements and added preformatting tags to JavaDoc descriptions.
 *
 * Revision 1.1.1.1  2003/09/25 13:56:43  yoda2
 * Initial CVS import for SwingSet.
 *
 */