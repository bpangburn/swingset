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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.io.*;
import javax.sql.RowSet;

/**
 * SSDataNavigator.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
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
 * before any navigation.  Once navigation takes place changes can't be reverted
 * using Undo button (has to be done manually by the user).
 *</pre><p>
 * @author	$Author$
 * @version	$Revision$
 */
public class SSDataNavigator extends JPanel {

	protected JButton button1 = new JButton();
	protected JButton button2 = new JButton();
	protected JTextField txtCurrentRow = new JTextField();
	protected JButton button3 = new JButton();
	protected JButton button4 = new JButton();

	protected JButton button5 = new JButton(); // Commit button
	protected JButton button6 = new JButton();
	protected JButton button7 = new JButton(); // REFRESH BUTTON
	protected JButton button8 = new JButton();
	protected JButton button9 = new JButton();
	protected JLabel  lblRowCount = new JLabel();

	// BASED ON THIS SSDataNavigator ALLOWS OR DISALLOWS MODIFICATION TO THE ROWSET
	protected boolean modification = true;
	
	// USERS CAN ALSO UPDATES TO PRESENT RECORDS BUT DISALLOW DELETION OF RECORDS
	// BY SETTING THIS TO FALSE	
	protected boolean allowDeletions = true;
	protected boolean allowInsertions = true;
	
	protected boolean confirmDeletes = true;

	protected boolean callExecute = true;
	
	// ROWSET TO WHICH THE NAVIGATOR IS LINKED TO
	protected transient RowSet rowset = null;

	// The container (Frame or Internal frame in which the navigator is present
	protected SSDBNav dbNav = null;

	// Is set to zero if next() on rowset returns false
	protected int rowCount = 0;
	
	protected int currentRow = 0;

	// true if the rowset is on insert row else false
	protected boolean onInsertRow = false;

	// SIZE OF THE BUTTONS TO WHICH THEY HAVE TO BE SET
	protected Dimension buttonSize = new Dimension( 50, 20);

	/**
	 * Creates a object of SSDataNavigator.
	 * Note: you have to set the rowset before you can start using it.
	 */
	public SSDataNavigator() {
        addToolTips();
        createPanel();
        addListeners();
	}

	/**
	 * Returns true if the rowset contains one or more rows else false
     *
	 * @return return true if rowset contains data else false.
	 */
	 public boolean containsRows() {

         if ( rowCount == 0 ) {
            return false;
         }

         return true;
	 }
     
	// METHOD TO ADD TOOLTIPS AND BUTTON GRPAHICS OR TEXT 
	private void addToolTips() {

		try {
			button1.setIcon(new ImageIcon(java.net.URLClassLoader.getSystemResource("images/first.gif")));
			button2.setIcon(new ImageIcon(java.net.URLClassLoader.getSystemResource("images/prev.gif")));
			button3.setIcon(new ImageIcon(java.net.URLClassLoader.getSystemResource("images/next.gif")));
			button4.setIcon(new ImageIcon(java.net.URLClassLoader.getSystemResource("images/last.gif")));
			button5.setIcon(new ImageIcon(java.net.URLClassLoader.getSystemResource("images/commit.gif")));
			button6.setIcon(new ImageIcon(java.net.URLClassLoader.getSystemResource("images/undo.gif")));		
			button7.setIcon(new ImageIcon(java.net.URLClassLoader.getSystemResource("images/refresh.gif")));
			button8.setIcon(new ImageIcon(java.net.URLClassLoader.getSystemResource("images/add.gif")));
			button9.setIcon(new ImageIcon(java.net.URLClassLoader.getSystemResource("images/delete.gif")));
		} catch(Exception e) {
			button1.setText("<<");
			button2.setText("<");
			button3.setText(">");
			button4.setText(">>");
			button5.setText("Commit");
			button6.setText("Undo");
			button7.setText("Refresh");
			button8.setText("Add");
			button9.setText("Delete");
			System.out.println("Unable to load images for navigator buttons");
		}

		button1.setToolTipText("First");
		button2.setToolTipText("Previous");
		button3.setToolTipText("Next");
		button4.setToolTipText("Last");
		button5.setToolTipText("Commit");
		button6.setToolTipText("Undo");
		button7.setToolTipText("Refresh");
		button8.setToolTipText("Add Record");
		button9.setToolTipText("Delete Record");

	} // end private void addToolTips() {

	/**
	 * Constructors a SSDataNavigator for the given rowset
     *
	 * @param _rowset    The rowset to which the SSDataNavigator has to be bound
	 */
	public SSDataNavigator(RowSet _rowset) {
		setRowSet(_rowset);
		addToolTips();
		createPanel();
		addListeners();
	}

	/**
	 * Constructs the SSDataNavigator with the given rowset and sets the size of the buttons
	 * on the navigator to the given size
     *
	 * @param _rowset    the rowset to which the navigator is bound to
	 * @param _buttonSize    the size to which the button on navigator have to be set
	 */
	public SSDataNavigator(RowSet _rowset, Dimension _buttonSize) {
		buttonSize = _buttonSize;
		setRowSet(_rowset);
		addToolTips();
		createPanel();
		addListeners();
	}
	
	/**
	 * Sets the mySQL property to true.
	 * This causes the navigator to skip the execute function call on the specified rowset.
	 * (See FAQ for further details)
     *
	 * @param _mySQL    true if using MySQL database else false.
	 */
	public void setCallExecute(boolean _execute) {
		callExecute = _execute;
	}
	 
	/**
	 * Sets the preferredSize and the MinimumSize of the buttons to the specified size
     *
	 * @param _buttonSize    the required dimension of the buttons
	 */
	public void setButtonSize(Dimension _buttonSize) {
		buttonSize = _buttonSize;
		setButtonSizes();
	}
	
	/**
	 * Returns the size of buttons on the data navigator.
     *
	 * @return returns a Dimension object representing the size of each button 
	 *    on the data navigator.
	 */
	public Dimension getButtonSize() {
		return buttonSize;
	} 

	/**
	 * Function that passes the implementation of the SSDBNav interface this is used when the
	 * insert button is pressed. The user of the SSDataNavigator can implement this and can perform
	 * some actions when the insert button is pressed
     *
     * @param _dbNav    implementation of the SSDBNav interface
	 */
	public void setDBNav(SSDBNav _dbNav) {
		dbNav = _dbNav;
	}

	/**
	 * Enables or disables the modification buttons on the SSDataNavigator
	 * if you want the user to just navigate through the records with out making any changes
	 * set this to false. Default vaule is true. So if the modification is not set then the
	 * modification buttons are enabled
	 *
	 * @param _modification    true or false
	 */
	public void setModification(boolean _modification) {
		modification = _modification;
		if (!modification) {
			button5.setEnabled(false);
			button6.setEnabled(false);
			button8.setEnabled(false);
			button9.setEnabled(false);
		} else {
			button5.setEnabled(true);
			button6.setEnabled(true);
			button8.setEnabled(true);
			button9.setEnabled(true);
		}
	}
	
	/**
	 * Returns true if the user can modify the data in the rowset else false.
     *
	 * @return returns true if the user modifications are written back to the 
	 *    database else false.
	 */
	public  boolean getModification() {
		return modification;
	}

	/**
	 * Enables or disables the row deletion button
	 * This method should be used if row deletions have to be disallowed
	 * The default value is true
     *
	 * @param deletion    indicates whether or not to allow deletions
	 */
	public void setDeletion(boolean deletion) {
		allowDeletions = deletion;
		if (!deletion) {
			button9.setEnabled(false);
		} else {
			button9.setEnabled(true);
		}
	}
	
	/**
	 * Returns true if deletions are allowed else false.
     *
	 * @return returns true if deletions are allowed on the present data
	 *    else false.
	 */
	public boolean getDeletion(){
		return allowDeletions;
	} 

	/**
	 * Enables or diables the row insertion button
	 * This method should be used if row insertions have to be disallowed
	 * The default value is true
     *
	 * @param insertion    indicates whether or not to allow insertions
	 */
	public void setInsertion(boolean insertion){
		allowInsertions = insertion;
		if (!insertion) {
			button8.setEnabled(false);
		} else{
			button8.setEnabled(true);
		}
	}
	
	/**
	 * Returns true if insertions are allowed else false.
     *
	 * @return returns true if insertiona are allowed else false.
	 */
	public boolean getInsertion() {
		return allowInsertions;
	}
	 
	/**
	 * Sets the confirm deletes. If set to true, every time delete button is pressed
	 * navigator pops up a confirmation dialog to the user. So that he can continue
	 * with deletion or cancel the deletion. Default value is true.
     *
	 * @param _confirmDeletes    indicates whether or not to confirm deletions
	 */
	 public void setConfirmDeletes(boolean _confirmDeletes) {
	 	confirmDeletes = _confirmDeletes;
	 }
	 
	 /**
	  *	Returns true if deletions are confirmed by user else false.
      *
	  * @return returns true if a confirmation dialog is displayed when the user 
	  *    deletes a record else false.
	  */
	 public boolean getConfirmDeletes() {
	 	return confirmDeletes;
	 } 
	 
	 /**
	  *	Returns the rowset being used.
      *
	  * @return returns the rowset being used.
	  */
	 public RowSet getRowSet() {
	 	return rowset;
	 } 
	 
	 /**
	  * Updates the present row. This is done automatically when navigation takes place.
	  * In addition to that if the user wants to update present row this function has to
	  * be called.
      *
	  * @return returns true if update succeeds else false.
	  */
	  public boolean updatePresentRow() {
	  	try {
	  		if (!onInsertRow) {
	  			rowset.updateRow();
            }
	  		return true;
	  	} catch(Exception e) {
	  		return false;
	  	}
	  }

	/**
	 * This method changes the rowset to which the navigator is bound
	 * You have to call execute() and next() on the rowset before you set rowset to the SSDataNavigator
     *
	 * @param _rowset    a RowSet object to which the navigator has to be bound
	 */
	public void setRowSet(RowSet _rowset) {
		rowset = _rowset;

		//SEE IF THERE ARE ANY ROWS IN THE GIVEN ROWSET
		try {
			if (callExecute) {
				rowset.execute();
            }

			if (!rowset.next()) {
				rowCount = 0;
			} else {
			// IF THERE ARE ROWS GET THE ROW COUNT	
				rowset.last();
				rowCount = rowset.getRow();
				rowset.first();
				currentRow = rowset.getRow();
			// SET THE ROW COUNT AS LABEL
				lblRowCount.setText(String.valueOf(rowCount));	
				txtCurrentRow.setText(String.valueOf(currentRow));
			}
		} catch(SQLException se) {
			se.printStackTrace();
		}

		//IF NO ROWS ARE PRESENT DISABLE NAVIGATION
		//ELSE ENABLE THEN ELSE IS USEFUL WHEN THE ROWSET IS CHNAGED
		// IF THE INITIAL ROWSET HAS ZERO ROWS NEXT IF THE USER SETS A NEW ROWSET THEN THE
		// BUTTONS HAVE TO BE ENABLED
		if (rowCount == 0) {
			button1.setEnabled(false);
			button2.setEnabled(false);
			button3.setEnabled(false);
			button4.setEnabled(false);
		} else{
			button1.setEnabled(true);
			button2.setEnabled(true);
			button3.setEnabled(true);
			button4.setEnabled(true);
		}

		try {
			if (rowset.isLast()) {
				button3.setEnabled(false);
				button4.setEnabled(false);
			}
			if (rowset.isFirst()) {
				button1.setEnabled(false);
				button2.setEnabled(false);
			}
				
		} catch(SQLException se) {
			se.printStackTrace();
		}
	}

    // SET BUTTON DIMENSIONS
	private void setButtonSizes() {

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
            txtCurrentRow.setPreferredSize(buttonSize);
            lblRowCount.setPreferredSize(buttonSize);
            lblRowCount.setHorizontalAlignment(SwingConstants.CENTER);

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
            txtCurrentRow.setMinimumSize(buttonSize);
            lblRowCount.setMinimumSize(buttonSize);
	}

	// ADDS THE BUTTONS ON TO THE PANEL
	private void createPanel() {

		setButtonSizes();
		//SET THE BOX LAYOUT
		setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS) );

		// ADD BUTTONS TO THE PANEL
		add(button1);
		add(button2);
		add(txtCurrentRow);
		add(button3);
		add(button4);
		add(button5);
		add(button6);
		add(button7);
		add(button8);
		add(button9);
		add(lblRowCount);
		//pack();

	}

	// ADDS THE LISTENERS FOR THE BUTTONS ON THE PANEL
	private void addListeners() {

		// WHEN THIS BUTTON IS PRESSED THE RECORD ON WHICH USER WAS WORKING IS SAVED
		// AND MOVES THE ROWSET TO THE FIRST ROW
		// SINCE ROW SET IS IN FIRST ROW DISABLE PREVIOUS BUTTON AND ENABLE NEXT BUTTON
		button1.addActionListener(new ActionListener(){ 
			public void actionPerformed(ActionEvent ae) {
				try {
					if ( modification ) {
						rowset.updateRow();
                    }
					rowset.first();
					
					button1.setEnabled(false);
					button2.setEnabled(false);
					if (!rowset.isLast()) {
						button3.setEnabled(true);
						button4.setEnabled(true);
					} else {
						button3.setEnabled(false);
						button4.setEnabled(false);
					}
					if ( dbNav != null ) {
						dbNav.performNavigationOps(SSDBNav.NAVIGATION_FIRST);
                    }
				// GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD	
					currentRow = 1;
					txtCurrentRow.setText(String.valueOf(currentRow));	
				} catch(SQLException se) {
					se.printStackTrace();
				}
			}
		});

		// WHEN BUTTON 2 IS PRESSED THE CURRENT RECORD IS SAVED AND ROWSET IS
		// MOVED TO PREVIOUS RECORD
		// CALLING PREVIOUS ON ENPTY ROWSET IS ILLEGAL SO A CHECK IS MADE FOR THAT
		// IF NUMBER OF ROWS == 0 THEN ROWSET IS EMPTY
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					//if( rowset.rowUpdated() )
					if ( modification ) {
						rowset.updateRow();
                    }
					if ( rowset.getRow() != 0 && !rowset.previous() ) {
						rowset.first();
					}
					// IF IN THE FIRST RECORD DISABLE PREVIOUS BUTTON
					if (rowset.isFirst() || rowset.getRow() == 0){
						button1.setEnabled(false);
						button2.setEnabled(false);
					}

					// IF NEXT BUTTON IS DISABLED ENABLE IT.
					if ( !rowset.isLast() ) {
						button3.setEnabled(true);
						button4.setEnabled(true);
					}
						
					if ( dbNav != null ) {
						dbNav.performNavigationOps(SSDBNav.NAVIGATION_PREVIOUS);
                    }
				// GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD		
					currentRow = rowset.getRow();	
					txtCurrentRow.setText(String.valueOf(currentRow));
				} catch(SQLException se) {
					se.printStackTrace();
				}
			}
		});

		// WHEN BUTTON 3 PRESSED THE CURRENT RECORD IS SAVED AND THE ROWSET IS
		// MOVED TO NEXT RECORD. IF THIS IS THE LAST RECORD THEN BUTTON 3 IS DISABLED
		// ALSO IF THE PREVIOUS BUTTON IS NOT ENABLED THEN IT IS ENABLED
		button3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					//if( rowset.rowUpdated() )
					if ( modification ) {
						rowset.updateRow();
                    }
					if ( !rowset.next() ) {
						button3.setEnabled(false);
						button4.setEnabled(false);
						rowset.last();
					}
					// IF LAST RECORD THEN DISABLE NEXT BUTTON
					if ( rowset.isLast() ) {
						button3.setEnabled(false);
						button4.setEnabled(false);
					}

					// IF THIS IS NOT FIRST ROW ENABLE FIRST AND PREVIOUS BUTTONS
					if ( !rowset.isFirst() ) {
						button2.setEnabled(true);
						button1.setEnabled(true);	
					}
											
					if ( dbNav != null ) {
						dbNav.performNavigationOps(SSDBNav.NAVIGATION_NEXT);
                    }
				// GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD	
					currentRow = rowset.getRow();
					txtCurrentRow.setText(String.valueOf(currentRow));
				} catch(SQLException se) {
					se.printStackTrace();
				}
			}
		});


		// BUTTON 4 ( "LAST" BUTTON )  CAUSED THE ROWSET TO MOVE TO LAST RECORD.
		// BEFORE MOVING CURRENT RECORD IS SAVED
		// AFTER MOVING TO LAST RECORD THE NEXT BUTTON IS DIAABLED AND PREVIOUS BUTTON
		// ENABLED
		button4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					//if( rowset.rowUpdated() )
					if ( modification ) {
						rowset.updateRow();
                    }
					rowset.last();
					
					button3.setEnabled(false);
					button4.setEnabled(false);
					if (!rowset.isFirst()) {
						button1.setEnabled(true);
						button2.setEnabled(true);
					} else {
						button1.setEnabled(false);
						button2.setEnabled(false);
					}
					if ( dbNav != null ) {
						dbNav.performNavigationOps(SSDBNav.NAVIGATION_LAST);
                    }
				// GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD	
					currentRow = rowset.getRow();
					txtCurrentRow.setText(String.valueOf(currentRow));	
				} catch(SQLException se) {
					se.printStackTrace();
				}
			}
		});

		// THIS BUTTON INSERTS THE ROW AND MOVES TO THE NEWLY INSERTED ROW.
		// WHEN INSERT BUTTON IS PRESSED NAVIGATION WILL BE DISABLED SO THOSE HAVE TO BE
		// ENABLED HERE
		button5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					if (onInsertRow) {
                    // IF ON INSERT ROW ADD THE ROW.
                        rowset.insertRow();
						if ( dbNav != null ) {
							dbNav.performPostInsertOps();
                        }
						rowset.moveToCurrentRow();
					} else {
                    // ELSE UPDATE THE PRESENT ROW VALUES.	
						rowset.updateRow();
                    }
						
					onInsertRow = false;
					
					if (!rowset.isFirst()) {
						button1.setEnabled(true);
						button2.setEnabled(true);
					}
					if (!rowset.isLast()) {
						button3.setEnabled(true);
						button4.setEnabled(true);
					}
					button7.setEnabled(true);
					
					if (allowInsertions) {
						button8.setEnabled(true);
                    }
					if (allowDeletions) {
						button9.setEnabled(true);
                    }
				} catch(SQLException se) {
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while inserting row.\n"+se.getMessage());
					se.printStackTrace();
				}
			}
		});

		// THIS BUTTON IS USED TO CANCEL THE CHANGES MADE TO THE RECORD.
		// IT CAN ALSO BE USED TO CANCEL INSERT ROW.
		// SO THE BUTTONS DIASABLED AT THE INSERT BUTTON EVENT HAVE TO BE ENABLED
		button6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					rowset.cancelRowUpdates();
					onInsertRow = false;
					if (dbNav != null) {
						dbNav.performCancelOps();
                    }
					//rowset.deleteRow();
					//rowset.moveToCurrentRow();
					button1.setEnabled(true);
					button2.setEnabled(true);
					button3.setEnabled(true);
					button4.setEnabled(true);
					button7.setEnabled(true);
					if (allowInsertions) {
						button8.setEnabled(true);
                    }
					if (allowDeletions) {
						button9.setEnabled(true);
                    }
				} catch(SQLException se) {
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while undoing changes.\n"+se.getMessage());
					se.printStackTrace();
				}
			}
		});

		// REFETCH REFETCHES THE ROWS FROMS THE DATABASE AND MOVES THE CURSOR TO THE FIRST
		// RECORD IF THERE ARE NO RECORDS NAVIGATION BUTTONS ARE DIABLED
		// EVEN IS THERE ARE RECORDS PREVIOUS BUTTON IS DISABLED BECAUSE THE ROWSET IS ON
		// THE FIRST ROW
		button7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					if (callExecute) {
						rowset.execute();
                    }
						
					if ( dbNav != null ) {
						dbNav.performRefreshOps();
                    }
						
					if ( rowset.next() ) {
						button1.setEnabled(true);
						button2.setEnabled(false);
						button3.setEnabled(true);
						button4.setEnabled(true);
					} else {
						button1.setEnabled(false);
						button2.setEnabled(false);
						button3.setEnabled(false);
						button4.setEnabled(false);
					}
				} catch(SQLException se) {
					se.printStackTrace();
				}
			}
		});

		// INSERT ROW BUTTON MOVES THE ROWSET TO THE INSERT ROW POSITION
		// AT THIS TIME NAVIGATION HAS TO BE DISABLED
		// ONLY COMMIT AND CANCEL ARE ENABLED
		button8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					rowset.moveToInsertRow();
					onInsertRow = true;

					if ( dbNav != null ) {
						dbNav.performPreInsertOps();
                    }
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

				} catch(SQLException se) {
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while moving to insert row.\n"+se.getMessage());
					se.printStackTrace();
				}
			}
		});

		// DELETES THE CURRENT ROW AND MOVES TO NEXT ROW
		// IF THE DELETED ROW IS THE LAST ROW THEN MOVES TO LAST ROW IN ROWSET
		// AFTER THE DELETION IS MADE( THATS THE PREVIOUS ROW TO THE DELETED ROW)
		button9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					int answer = JOptionPane.showConfirmDialog(SSDataNavigator.this,"Are you sure you want to delete this record?","Delete Present Record", JOptionPane.YES_NO_OPTION);
					if ( answer != JOptionPane.YES_OPTION ) {
						return;
                    }
						
					if ( dbNav != null ) {
						dbNav.performPreDeletionOps();
                    }
					rowset.deleteRow();
					if ( dbNav != null ) {
						dbNav.performPostDeletionOps();
                    }

					if (! rowset.next() ) {
						rowset.last();
                    }
				} catch(SQLException se) {
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while deleting row.\n"+se.getMessage());
					se.printStackTrace();
				}
			}
		});
		
	// LISTENER FOR THE TEXT FIELD. USER CAN ENTER A ROW NUMBER IN THE TEXT
	// FIELD TO MOVE THE THE SPEICIFIED ROW. 
	// IF ITS NOT A NUMBER OR IF ITS NOT VALID FOR THE CURRENT ROWSET
	// NOTHING HAPPENS.	
		txtCurrentRow.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						int row = Integer.parseInt(txtCurrentRow.getText().trim());
						if (row <= rowCount && row >0) {
							rowset.absolute(row);
						}
					} catch(Exception e) {
                        // do nothing
					}
				}
			}
		});
		
	}
		
} // end public class SSDataNavigator extends JPanel {



/*
 * $Log$
 * Revision 1.8  2004/08/02 15:04:06  prasanth
 * 1. Added a text field to display current row number.
 * 2. Added a label to display the total number of rows in the rowset.
 * 3. Added listener to text field so that user can enter a row number
 *     to navigate to that row.
 *
 * Revision 1.7  2004/03/08 16:40:00  prasanth
 * Added the if condition to check for callExecute in the listener for refresh button.
 *
 * Revision 1.6  2004/02/23 16:36:00  prasanth
 * Added  setMySQLDB function.
 * Skipping execute call on the rowset if mySQL is true.
 *
 * Revision 1.5  2004/01/27 17:13:03  prasanth
 * Changed the behaviour of commit button. When not on insert row will update
 * present row.
 *
 * Also modified enabling and disabling of navigation buttons.
 *
 * Revision 1.4  2003/11/26 21:24:49  prasanth
 * Calling performCancelOps().
 *
 * Revision 1.3  2003/10/31 16:02:52  prasanth
 * Added login to disable the navigation buttons when only one record is present
 * in the rowset.
 *
 * Revision 1.2  2003/09/25 14:27:45  yoda2
 * Removed unused Import statements and added preformatting tags to JavaDoc descriptions.
 *
 * Revision 1.1.1.1  2003/09/25 13:56:43  yoda2
 * Initial CVS import for SwingSet.
 *
 */