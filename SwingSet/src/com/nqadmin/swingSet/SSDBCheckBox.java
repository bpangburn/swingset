
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
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;



/**
 * SSDBCheckBox.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p>
 * Used to display the boolean values stored in the database. The SSDBCheckBox
 * can currently only be bound to a numeric database column.  A checked
 * SSDBCheckBox returns a '1' to the database and an uncheck SSDBCheckBox will
 * returns a '0'.  In the future an option may be added to allow the user to
 * specify the values returned for the checked and unchecked checkbox states.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class SSDBCheckBox extends JCheckBox {


	// TEXT FIELD BOUND TO THE DATABASE
	private JTextField textField = null;

	// LISTENER FOR CHECK BOX AND TEXT FEILD
	private MyCheckBoxListener  checkBoxListener  = new MyCheckBoxListener();
	private MyTextFieldListener textFieldListener = new MyTextFieldListener();

	// INITIALIZE CHECKED AND UNCHECKED VALUES
	private int CHECKED = 1;
	private int UNCHECKED = 0;

	/**
	 *	Creates a object of SSDBCheckBox which synchronizes with the value in the specified
	 *text field.
	 *@param _textfield the text field with which the check box will be in sync.
	 */
	public SSDBCheckBox(JTextField _textField){
		textField = _textField;
	}

	/**
	 *	Creates an object of SSDBCheckBox.
	 */
	public SSDBCheckBox(){
		textField = new JTextField();
	}

	/**
	 *	Sets the text field with which the check box has to be synchronized.
	 *@param _textField the text field with which the check box will be in sync.
	 */
	public void setTextField(JTextField _textField){
		textField = _textField;
	}

	/**
	 *	returns the text field with which the check box is synchronizing.
	 *@return returns a JTextField which is used to  set the check box.
	 */
	public JTextField getTextField(){
		return textField;
	}

	/**
	 *	Initializes the check box by getting the value from the textfield.
	 */
	public void execute() {
		initCheckBox();
	}

	// Initializes the check box.
	private void initCheckBox(){

		// ADD LISTENER FOR THE TEXT FIELD
		textField.getDocument().addDocumentListener(textFieldListener);

		// SET THE CHECK BOX BASED ON THE VALUE IN TEXT FIELD
		if( textField.getText().equals(String.valueOf(CHECKED)) ) {
			setSelected(true);
		}
		else {
			setSelected(false);
		}
		//ADD LISTENER FOR THE CHECK BOX.
		addChangeListener( checkBoxListener );
	}

	// LISTENER FOR THE TEXT FIELD
	private class MyTextFieldListener implements DocumentListener {

		public void changedUpdate(DocumentEvent de){
		}

		// WHEN EVER THERE IS A CHANGE IN THE VALUE IN THE TEXT FIELD CHANGE THE CHECK BOX
		// ACCORDINGLY.
		public void insertUpdate(DocumentEvent de){
			removeChangeListener( checkBoxListener );
			if( textField.getText().equals(String.valueOf(CHECKED)) ) {
				setSelected(true);
			}
			else {
				setSelected(false);
			}
			addChangeListener( checkBoxListener );
		}

		// IF A REMOVE UPDATE OCCURS ON THE TEXT FIELD CHECK THE CHANGE AND SET THE
		// CHECK BOX ACCORDINGLY.
		public void removeUpdate(DocumentEvent de){

			removeChangeListener( checkBoxListener );
			if( textField.getText().equals( String.valueOf(CHECKED)) ) {
				setSelected(true);
			}
			else {
				setSelected(false);
			}
			addChangeListener( checkBoxListener );
		}
	}

	// LISTENER FOR THE CHECK BOX.
	// ANY CHANGES MADE TO THE CHECK BOX BY THE USER ARE PROPOGATED BACK TO THE
	// TEXT FIELD FOR FURTHER PROPOGATION TO THE UNDERLYING STORAGE STRUCTURE.
	private class MyCheckBoxListener implements ChangeListener {

		public void stateChanged(ChangeEvent ce){

			textField.getDocument().removeDocumentListener(textFieldListener);

			if ( ((JCheckBox)ce.getSource()).isSelected() ) {
				textField.setText(String.valueOf(CHECKED) );
			}
			else {
				textField.setText(String.valueOf(UNCHECKED) );
			}

			textField.getDocument().addDocumentListener(textFieldListener);
		}

	}

}



/*
 * $Log$
 */