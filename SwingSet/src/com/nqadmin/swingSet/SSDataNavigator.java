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



import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.io.*;
import javax.sql.RowSet;
import sun.jdbc.rowset.CachedRowSet;
import sun.jdbc.rowset.JdbcRowSet;
import org.netbeans.lib.sql.ConnectionSource;



/**
 * SSDataNavigator.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p>
 * Component that can be used for data navigation. It provides buttons for
 * navigation, insertion, and deletion of records in a RowSet. The modification
 * of a RowSet can be prevented using the setModificaton() method.  Any changes
 * made to the columns of a record will be updated whenever there is a
 * navigation.
 *
 * For example if you are displaying three columns using the JTextField and the
 * user changes the text in the text fields then the columns will be updated to
 * the new values when the user navigates the RowSet. If the user wants to revert
 * the changes he made he can press the Undo button, however this must be done
 * be done before any navigation.  Once navigation takes place changes can't be
 * reverted using Undo button (has to be done manually by the user).
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class SSDataNavigator extends JPanel{

	JButton button1 = new JButton("<<");
	JButton button2 = new JButton("<");
	JButton button3 = new JButton(">");
	JButton button4 = new JButton(">>");

	JButton button5 = new JButton(); // Commit button
	JButton button6 = new JButton();
	JButton button7 = new JButton(); // REFRESH BUTTON
	JButton button8 = new JButton();
	JButton button9 = new JButton();

	// BASED ON THIS SSDataNavigator ALLOWS OR DISALLOWS MODIFICATION TO THE ROWSET
	boolean modification = true;

	// ROWSET TO WHICH THE NAVIGATOR IS LINKED TO
	RowSet rowset = null;

	// The container (Frame or Internal frame in which the navigator is present
	SSDBNav dbNav = null;

	// Is set to zero if next() on rowset returns false
	int numRows = -1;

//	Vector defaultValues = null;

	// SIZE OF THE BUTTONS TO WHICH THEY HAVE TO BE SET
	Dimension buttonSize = new Dimension( 60, 20);

	/**
	 *	Creates a object of SSDataNavigator.
	 *Note: you have to set the rowset before you can start using it.
	 */
	public SSDataNavigator(){
			addToolTips();
			createPanel();
			addListeners();

	}

	/**
	 *	Returns true if the rowset contains one or more rows else false
	 *@return return true if rowset contains data else false.
	 */
	 public boolean containsRows(){

	 	if( numRows == 0 )
	 		return false;

	 	return true;
	 }


	private void addToolTips(){

//	JButton button5 = new JButton("" + '\u2714'); // Commit button
//	JButton button6 = new JButton("" + '\u2716');
//	JButton button7 = new JButton("" + '\u2672'); // REFRESH BUTTON
//	JButton button8 = new JButton("" + '\u271A');
//	JButton button9 = new JButton("" + '\u2620');
/*
		button2.setFont(new Font("windings 3",Font.BOLD,14));
		button3.setFont(new Font("windings 3",Font.BOLD,14));
		button5.setFont(new Font("windings 3",Font.BOLD,12));
		button6.setFont(new Font("windings 3",Font.BOLD,14));
		button7.setFont(new Font("windings 3",Font.BOLD,12));
		button8.setFont(new Font("windings 3",Font.BOLD,12));
		button9.setFont(new Font("windings 3",Font.BOLD,12));

*/
		button2.setText("<");
		button3.setText(">");
		button5.setText("Commit");
		button6.setText("Undo");

		button7.setText("Refresh");
		button8.setText("Add");
		button9.setText("Delete");


		button1.setToolTipText("First");
		button2.setToolTipText("Previous");
		button3.setToolTipText("Next");
		button4.setToolTipText("Last");
		button5.setToolTipText("Commit");
		button6.setToolTipText("Undo");
		button7.setToolTipText("Refresh");
		button8.setToolTipText("Add Record");
		button9.setToolTipText("Delete Record");

	}

	/**
	 *Constructors a SSDataNavigator for the given rowset
	 *@param _rowset the rowset to which the SSDataNavigator has to be bound
	 */
	public SSDataNavigator(RowSet _rowset) {

		setRowSet(_rowset);
		addToolTips();
		createPanel();
		addListeners();
	}

	/**
	 *Constructs the SSDataNavigator with the given rowset and sets the size of the buttons
	 *on the navigator to the given size
	 *@param _rowset the rowset to which the navigator is bound to
	 *@param _buttonSize the size to which the button on navigator have to be set
	 */
	public SSDataNavigator(RowSet _rowset, Dimension _buttonSize) {

		buttonSize = _buttonSize;
		setRowSet(_rowset);
		addToolTips();
		createPanel();
		addListeners();
	}

	/**
	 * sets the preferredSize and the MinimumSize of the buttons to the specified size
	 *@param _buttonSize the required dimension of the buttons
	 */
	public void setButtonSize(Dimension _buttonSize){
		buttonSize = _buttonSize;
		setButtonSizes();
	}

	/**
	 * function that passes the implementation of the SSDBNav interface this is used when the
	 * insert button is pressed. The user of the SSDataNavigator can implement this and can perform
	 * some actions when the insert button is pressed
	 */
	public void setDBNav(SSDBNav _dbNav){
		dbNav = _dbNav;
	}

	/**
	 *enables or disables the modification buttons on the SSDataNavigator
	 *if you wanth the user to just navigate through the records with out making any changes
	 *set this to false. Default vaule is true. So if the modification is not set then the
	 *modification buttons are enabled
	 *
	 *@param _modification true or false
	 */
	public void setModification(boolean _modification){
		modification = _modification;
		if(!modification){
			button5.setEnabled(false);
			button6.setEnabled(false);
			button8.setEnabled(false);
			button9.setEnabled(false);
		}
		else{
			button5.setEnabled(true);
			button6.setEnabled(true);
			button8.setEnabled(true);
			button9.setEnabled(true);
		}
	}

	/**
	 * Enables or diables the row deletion button
	 * This method should be used if row deletions have to be disallowed
	 * The default value is true
	 *@param deletion - true or false
	 */
	public void setDeletion(boolean deletion){
		if(!deletion){
			button9.setEnabled(false);
		}
		else{
			button9.setEnabled(true);
		}
	}

	/**
	 * Enables or diables the row insertion button
	 * This method should be used if row insertions have to be disallowed
	 * The default value is true
	 *@param insertion - true or false
	 */
	public void setInsertion(boolean insertion){
		if(!insertion){
			button8.setEnabled(false);
		}
		else{
			button8.setEnabled(true);
		}
	}

//	public void setDefaultValue(String columnName, Object

	/**
	 *this method changes the rowset to which the navigator is bound
	 * You have to call execute() and next() on the rowset before you set rowset to the SSDataNavigator
	 *@param _rowset a RowSet object to which the navigator has to be bound
	 */
	public void setRowSet( RowSet _rowset){
		rowset = _rowset;

		//SEE IF THERE ARE ANY ROWS IN THE GIVEN ROWSET
		try{
			rowset.execute();

			if(!rowset.next())
				numRows = 0;
			else
				numRows = -1;
		}catch(SQLException se){
			se.printStackTrace();
		}

		//IF NO ROWS ARE PRESENT DISABLE NAVIGATION
		//ELSE ENABLE THEN ELSE IS USEFUL WHEN THE ROWSET IS CHNAGED
		// IF THE INITIAL ROWSET HAS ZERO ROWS NEXT IF THE USER SETS A NEW ROWSET THEN THE
		// BUTTONS HAVE TO BE ENABLED
		if(  numRows == 0){
			button1.setEnabled(false);
			button2.setEnabled(false);
			button3.setEnabled(false);
			button4.setEnabled(false);
		}
		else{
			button1.setEnabled(true);
			button2.setEnabled(true);
			button3.setEnabled(true);
			button4.setEnabled(true);
		}
		// THE COMMIT BUTTON IS SHOULB BE USED ONLY AFTER INSERT ROW IS REQUESTED
		// THIS BUTTONS COMMITES THE INSERTED ROW
		button5.setEnabled(false);
	}

	private void setButtonSizes(){

		// SET THE PREFERRED SIZES
		button1.setPreferredSize(buttonSize);
		button2.setPreferredSize(buttonSize);
		button3.setPreferredSize(buttonSize);
		button4.setPreferredSize(buttonSize);
		button5.setPreferredSize(buttonSize);
		button6.setPreferredSize(buttonSize);
		button7.setPreferredSize(buttonSize);
		button8.setPreferredSize(buttonSize);
		button9.setPreferredSize(buttonSize);

		// SET MINIMUM BUTTON SIZES
		button1.setMinimumSize(buttonSize);
		button2.setMinimumSize(buttonSize);
		button3.setMinimumSize(buttonSize);
		button4.setMinimumSize(buttonSize);
		button5.setMinimumSize(buttonSize);
		button6.setMinimumSize(buttonSize);
		button7.setMinimumSize(buttonSize);
		button8.setMinimumSize(buttonSize);
		button9.setMinimumSize(buttonSize);
	}

	/**
	 * adds the buttons on to the panel
	 */
	private void createPanel(){

		setButtonSizes();
		//SET THE BOX LAYOUT
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS) );

		// ADD BUTTONS TO THE PANEL
		add(button1);
		add(button2);
		add(button3);
		add(button4);
		add(button5);
		add(button6);
		add(button7);
		add(button8);
		add(button9);

	}

	/**
	 * adds the listeners for the buttons on the panel
	 */
	private void addListeners(){

		// WHEN THIS BUTTON IS PRESSED THE RECORD ON WHICH USER WAS WORKING IS SAVED
		// AND MOVES THE ROWSET TO THE FIRST ROW
		// SINCE ROW SET IS IN FIRST ROW DISABLE PREVIOUS BUTTON AND ENABLE NEXT BUTTON
		button1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					if( modification )
						rowset.updateRow();
					rowset.first();
					button2.setEnabled(false);
					button3.setEnabled(true);
				}catch(SQLException se){
					se.printStackTrace();
				}
			}
		});

		// WHEN BUTTON 2 IS PRESSED THE CURRENT RECORD IS SAVED AND ROWSET IS
		// MOVED TO PREVIOUS RECORD
		// CALLING PREVIOUS ON ENPTY ROWSET IS ILLEGAL SO A CHECK IS MADE FOR THAT
		// IF NUMBER OF ROWS == 0 THEN ROWSET IS EMPTY

		button2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					//if( rowset.rowUpdated() )
					if( modification )
						rowset.updateRow();
					if( rowset.getRow() != 0 && !rowset.previous() ){
						rowset.first();
					}
					// IF IN THE FIRST RECORD DISABLE PREVIOUS BUTTON
					if( rowset.isFirst() || rowset.getRow() == 0)
							button2.setEnabled(false);

					// IF NEXT BUTTON IS DISABLED ENABLE IT.
					if( !button3.isEnabled() )
						button3.setEnabled(true);
				}catch(SQLException se){
					se.printStackTrace();
				}
			}
		});

		// WHEN BUTTON 3 PRESSED THE CURRENT RECORD IS SAVED AND THE ROWSET IS
		// MOVED TO NEXT RECORD. IF THIS IS THE LAST RECORD THEN BUTTON 3 IS DISABLED
		// ALSO IF THE PREVIOUS BUTTON IS NOT ENABLED THEN IT IS ENABLED
		button3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					//if( rowset.rowUpdated() )
					if( modification )
						rowset.updateRow();
					if( !rowset.next() ) {
						button3.setEnabled(false);
						rowset.last();
					}
					// IF LAST RECORD THEN DISABLE NEXT BUTTON
					if( rowset.isLast() )
						button3.setEnabled(false);

					// IF PREVIOUS BUTTON IS DISABLED THEN ENABLE THE PREVIOUS BUTTON
					if( !button2.isEnabled() )
						button2.setEnabled(true);
				}catch(SQLException se){
					se.printStackTrace();
				}
			}
		});


		// BUTTON 4 ( "LAST" BUTTON )  CAUSED THE ROWSET TO MOVE TO LAST RECORD.
		// BEFORE MOVING CURRENT RECORD IS SAVED
		// AFTER MOVING TO LAST RECORD THE NEXT BUTTON IS DIAABLED AND PREVIOUS BUTTON
		// ENABLED
		button4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					//if( rowset.rowUpdated() )
					if( modification )
						rowset.updateRow();
					rowset.last();
					button3.setEnabled(false);
					button2.setEnabled(true);
				}catch(SQLException se){
					se.printStackTrace();
				}
			}
		});

		// THIS BUTTON INSERTS THE ROW AND MOVES TO THE NEWLY INSERTED ROW.
		// WHEN INSERT BUTTON IS PRESSED NAVIGATION WILL BE DISABLED SO THOSE HAVE TO BE
		// ENABLED HERE
		button5.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{

					rowset.insertRow();

					rowset.moveToCurrentRow();


					button1.setEnabled(true);
					button2.setEnabled(true);
					button3.setEnabled(true);
					button4.setEnabled(true);
					button5.setEnabled(false);
					button7.setEnabled(true);
					button8.setEnabled(true);
					button9.setEnabled(true);
				}catch(SQLException se){
					se.printStackTrace();
				}
			}
		});

		// THIS BUTTON IS USED TO CANCEL THE CHANGES MADE TO THE RECORD.
		// IT CAN ALSO BE USED TO CANCEL INSERT ROW.
		// SO THE BUTTONS DIASABLED AT THE INSERT BUTTON EVENT HAVE TO BE ENABLED
		button6.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					rowset.cancelRowUpdates();
					//rowset.deleteRow();
					//rowset.moveToCurrentRow();
					button1.setEnabled(true);
					button2.setEnabled(true);
					button3.setEnabled(true);
					button4.setEnabled(true);
					button7.setEnabled(true);
					button8.setEnabled(true);
					button9.setEnabled(true);
				}catch(SQLException se){
					se.printStackTrace();
				}
			}
		});

		// REFETCH REFETCHES THE ROWS FROMS THE DATABASE AND MOVES THE CURSOR TO THE FIRST
		// RECORD IF THERE ARE NO RECORDS NAVIGATION BUTTONS ARE DIABLED
		// EVEN IS THERE ARE RECORDS PREVIOUS BUTTON IS DISABLED BECAUSE THE ROWSET IS ON
		// THE FIRST ROW
		button7.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					rowset.execute();
					if( rowset.next() ){
						button1.setEnabled(true);
						button2.setEnabled(false);
						button3.setEnabled(true);
						button4.setEnabled(true);
					}
					else{
						button1.setEnabled(false);
						button2.setEnabled(false);
						button3.setEnabled(false);
						button4.setEnabled(false);
					}


				}catch(SQLException se){
					se.printStackTrace();
				}
			}
		});

		// INSERT ROW BUTTON MOVES THE ROWSET TO THE INSERT ROW POSITION
		// AT THIS TIME NAVIGATION HAS TO BE DISABLED
		// ONLY COMMIT AND CANCEL ARE ENABLED
		button8.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					rowset.moveToInsertRow();

					if( dbNav != null )
						dbNav.performPreInsertOps();
					//rowset.updateString("client_name", "prasanh reddy");
					//rowset.insertRow();
					//rowset.moveToCurrentRow();
					button1.setEnabled(false);
					button2.setEnabled(false);
					button3.setEnabled(false);
					button4.setEnabled(false);
					button5.setEnabled(true);
					button6.setEnabled(true);
					button7.setEnabled(false);
					button8.setEnabled(false);
					button9.setEnabled(false);

				}catch(SQLException se){
					se.printStackTrace();
				}
			}
		});

		// DELETES THE CURRENT ROW AND MOVES TO NEXT ROW
		// IF THE DELETED ROW IS THE LAST ROW THEN MOVES TO LAST ROW IN ROWSET
		// AFTER THE DELETION IS MADE( THATS THE PREVIOUS ROW TO THE DELETED ROW)
		button9.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					rowset.deleteRow();

					if(! rowset.next() )
						rowset.last();
				}catch(SQLException se){
					se.printStackTrace();
				}
			}
		});

	}

}



/*
 * $Log$
 */