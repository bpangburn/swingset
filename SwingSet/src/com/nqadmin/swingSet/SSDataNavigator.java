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
import com.nqadmin.swingSet.datasources.SSRowSet;
import javax.sql.RowSetListener;
import javax.sql.RowSetEvent;

/**
 * SSDataNavigator.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Component that can be used for data navigation. It provides buttons for
 * navigation, insertion, and deletion of records in a SSRowSet. The modification
 * of a SSRowSet can be prevented using the setModificaton() method.  Any changes
 * made to the columns of a record will be updated whenever there is a
 * navigation.
 *
 * For example if you are displaying three columns using the JTextField and the
 * user changes the text in the text fields then the columns will be updated to
 * the new values when the user navigates the SSRowSet. If the user wants to revert
 * the changes he made he can press the Undo button, however this must be done
 * before any navigation.  Once navigation takes place changes can't be reverted
 * using Undo button (has to be done manually by the user).
 *</pre><p>
 * @author	$Author$
 * @version	$Revision$
 */
public class SSDataNavigator extends JPanel {

	protected JButton firstButton = new JButton();
	protected JButton previousButton = new JButton();
	protected JTextField txtCurrentRow = new JTextField();
	protected JButton nextButton = new JButton();
	protected JButton lastButton = new JButton();

	protected JButton commitButton = new JButton(); // Commit button
	protected JButton undoButton = new JButton();
	protected JButton refreshButton = new JButton(); // REFRESH BUTTON
	protected JButton addButton = new JButton();
	protected JButton deleteButton = new JButton();
	protected JLabel lblRowCount = new JLabel();

	// BASED ON THIS SSDataNavigator ALLOWS OR DISALLOWS MODIFICATION TO THE SSROWSET
	protected boolean modification = true;
	
	// USERS CAN ALSO UPDATES TO PRESENT RECORDS BUT DISALLOW DELETION OF RECORDS
	// BY SETTING THIS TO FALSE	
	protected boolean allowDeletions = true;
	protected boolean allowInsertions = true;
	
	protected boolean confirmDeletes = true;

	protected boolean callExecute = true;
	
	// SSROWSET TO WHICH THE NAVIGATOR IS LINKED TO
	protected SSRowSet rowset = null;

	// The container (Frame or Internal frame in which the navigator is present
	protected SSDBNav dbNav = null;

	// Is set to zero if next() on SSRowSet returns false
	protected int rowCount = 0;
	
	protected int currentRow = 0;

	// true if the SSRowSet is on insert row else false
	protected boolean onInsertRow = false;

	// SIZE OF THE BUTTONS TO WHICH THEY HAVE TO BE SET
	protected Dimension buttonSize = new Dimension( 40, 20);
	
	protected Dimension txtFieldSize = new Dimension( 65, 20);

	protected SSDBNavRowSetListener rowsetListener = new SSDBNavRowSetListener();
	/**
	 * Creates a object of SSDataNavigator.
	 * Note: you have to set the SSRowSet before you can start using it.
	 */
	public SSDataNavigator() {
        addToolTips();
        createPanel();
        addListeners();
	}

	/**
	 * Returns true if the SSRowSet contains one or more rows, else false
     *
	 * @return return true if SSRowSet contains data else false.
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
			ClassLoader cl = this.getClass().getClassLoader();
			firstButton.setIcon(new ImageIcon(cl.getResource("images/first.gif")));
			previousButton.setIcon(new ImageIcon(cl.getResource("images/prev.gif")));
			nextButton.setIcon(new ImageIcon(cl.getResource("images/next.gif")));
			lastButton.setIcon(new ImageIcon(cl.getResource("images/last.gif")));
			commitButton.setIcon(new ImageIcon(cl.getResource("images/commit.gif")));
			undoButton.setIcon(new ImageIcon(cl.getResource("images/undo.gif")));		
			refreshButton.setIcon(new ImageIcon(cl.getResource("images/refresh.gif")));
			addButton.setIcon(new ImageIcon(cl.getResource("images/add.gif")));
			deleteButton.setIcon(new ImageIcon(cl.getResource("images/delete.gif")));
		} catch(Exception e) {
			firstButton.setText("<<");
			previousButton.setText("<");
			nextButton.setText(">");
			lastButton.setText(">>");
			commitButton.setText("Commit");
			undoButton.setText("Undo");
			refreshButton.setText("Refresh");
			addButton.setText("Add");
			deleteButton.setText("Delete");
			System.out.println("Unable to load images for navigator buttons");
		}

		firstButton.setToolTipText("First");
		previousButton.setToolTipText("Previous");
		nextButton.setToolTipText("Next");
		lastButton.setToolTipText("Last");
		commitButton.setToolTipText("Commit");
		undoButton.setToolTipText("Undo");
		refreshButton.setToolTipText("Refresh");
		addButton.setToolTipText("Add Record");
		deleteButton.setToolTipText("Delete Record");

	} // end private void addToolTips() {

	/**
	 * Constructors a SSDataNavigator for the given SSRowSet
     *
	 * @param _rowset The SSRowSet to which the SSDataNavigator has to be bound
	 */
	public SSDataNavigator(SSRowSet _rowset) {
		setRowSet(_rowset);
		addToolTips();
		createPanel();
		addListeners();
	}

	/**
	 * Constructs the SSDataNavigator with the given SSRowSet and sets the size of the buttons
	 * on the navigator to the given size
     *
	 * @param _rowset    the SSRowSet to which the navigator is bound to
	 * @param _buttonSize    the size to which the button on navigator have to be set
	 */
	public SSDataNavigator(SSRowSet _rowset, Dimension _buttonSize) {
		buttonSize = _buttonSize;
		setRowSet(_rowset);
		addToolTips();
		createPanel();
		addListeners();
	}
	
	/**
	 * Sets the mySQL property to true.
	 * This causes the navigator to skip the execute function call on the specified SSRowSet.
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
			commitButton.setEnabled(false);
			undoButton.setEnabled(false);
			addButton.setEnabled(false);
			deleteButton.setEnabled(false);
		} else {
			commitButton.setEnabled(true);
			undoButton.setEnabled(true);
			addButton.setEnabled(true);
			deleteButton.setEnabled(true);
		}
	}
	
	/**
	 * Returns true if the user can modify the data in the SSRowSet, else false.
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
			deleteButton.setEnabled(false);
		} else {
			deleteButton.setEnabled(true);
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
	 * Enables or disables the row insertion button
	 * This method should be used if row insertions have to be disallowed
	 * The default value is true
     *
	 * @param insertion    indicates whether or not to allow insertions
	 */
	public void setInsertion(boolean insertion){
		allowInsertions = insertion;
		if (!insertion) {
			addButton.setEnabled(false);
		} else{
			addButton.setEnabled(true);
		}
	}
	
	/**
	 * Returns true if insertions are allowed else false.
     *
	 * @return returns true if insertions are allowed else false.
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
	  *	Returns the SSRowSet being used.
      *
	  * @return returns the SSRowSet being used.
	  */
	 public SSRowSet getRowSet() {
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
	 * This method changes the SSRowSet to which the navigator is bound
	 * You have to call execute() and next() on the SSRowSet before you set
	 * SSRowSet to the SSDataNavigator
     *
	 * @param _rowset    a SSRowSet object to which the navigator has to be bound
	 */
	public void setRowSet(SSRowSet _rowset) {
		if(rowset != null){
			rowset.removeRowSetListener(rowsetListener);
		}
		
		rowset = _rowset;
		
		//SEE IF THERE ARE ANY ROWS IN THE GIVEN SSROWSET
		try {
			if (callExecute) {
				rowset.execute();
            }

			if (!rowset.next()) {
				rowCount = 0;
				currentRow = 0;
			} else {
			// IF THERE ARE ROWS GET THE ROW COUNT	
				rowset.last();
				rowCount = rowset.getRow();
				rowset.first();
				currentRow = rowset.getRow();
			}
		// SET THE ROW COUNT AS LABEL
			lblRowCount.setText("of " + rowCount);	
			txtCurrentRow.setText(String.valueOf(currentRow));

			rowset.addRowSetListener(rowsetListener);
		} catch(SQLException se) {
			se.printStackTrace();
		}

		//IF NO ROWS ARE PRESENT DISABLE NAVIGATION
		//ELSE ENABLE THEN ELSE IS USEFUL WHEN THE SSROWSET IS CHNAGED
		// IF THE INITIAL SSROWSET HAS ZERO ROWS NEXT IF THE USER SETS A NEW SSROWSET THEN THE
		// BUTTONS HAVE TO BE ENABLED
		if (rowCount == 0) {
			firstButton.setEnabled(false);
			previousButton.setEnabled(false);
			nextButton.setEnabled(false);
			lastButton.setEnabled(false);
		} else{
			firstButton.setEnabled(true);
			previousButton.setEnabled(true);
			nextButton.setEnabled(true);
			lastButton.setEnabled(true);
		}

		try {
			if (rowset.isLast()) {
				nextButton.setEnabled(false);
				lastButton.setEnabled(false);
			}
			if (rowset.isFirst()) {
				firstButton.setEnabled(false);
				previousButton.setEnabled(false);
			}
				
		} catch(SQLException se) {
			se.printStackTrace();
		}
		
	// ENABLE OTHER BUTTONS IF NEED BE.
		
		// THIS IS NEEDED TO HANDLE USER LEAVING THE SCREEN IN AN INCONSISTENT 
		// STATE EXAMPLE: USER CLICKS ADD BUTTON, THIS DISABLES ALL THE BUTTONS
		// EXCEPT COMMIT & UNDO. WITH OUT COMMITING OR UNDOING THE ADD USER
		// CLOSES THE SCREEN. NOW IF THE SCREEN IS OPENED WITH A NEW SSROWSET.
		// THE REFRESH, ADD & DELETE WILL BE DISABLED.
		refreshButton.setEnabled(true);
		if (allowInsertions) {
			addButton.setEnabled(true);
        }
		if (allowDeletions) {
			deleteButton.setEnabled(true);
        }
	}

    // SET BUTTON DIMENSIONS
	private void setButtonSizes() {

		// SET THE PREFERRED SIZES
            firstButton.setPreferredSize(buttonSize);
            previousButton.setPreferredSize(buttonSize);
            nextButton.setPreferredSize(buttonSize);
            lastButton.setPreferredSize(buttonSize);
            commitButton.setPreferredSize(buttonSize);
            undoButton.setPreferredSize(buttonSize);
            refreshButton.setPreferredSize(buttonSize);
            addButton.setPreferredSize(buttonSize);
            deleteButton.setPreferredSize(buttonSize);
            txtCurrentRow.setPreferredSize(txtFieldSize);
            lblRowCount.setPreferredSize(txtFieldSize);
            lblRowCount.setHorizontalAlignment(SwingConstants.CENTER);

		// SET MINIMUM BUTTON SIZES
            firstButton.setMinimumSize(buttonSize);
            previousButton.setMinimumSize(buttonSize);
            nextButton.setMinimumSize(buttonSize);
            lastButton.setMinimumSize(buttonSize);
            commitButton.setMinimumSize(buttonSize);
            undoButton.setMinimumSize(buttonSize);
            refreshButton.setMinimumSize(buttonSize);
            addButton.setMinimumSize(buttonSize);
            deleteButton.setMinimumSize(buttonSize);
            txtCurrentRow.setMinimumSize(txtFieldSize);
            lblRowCount.setMinimumSize(txtFieldSize);
	}

	// ADDS THE BUTTONS ON TO THE PANEL
	private void createPanel() {

		setButtonSizes();
		//SET THE BOX LAYOUT
		setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS) );

		// ADD BUTTONS TO THE PANEL
		add(firstButton);
		add(previousButton);
		add(txtCurrentRow);
		add(nextButton);
		add(lastButton);
		add(commitButton);
		add(undoButton);
		add(refreshButton);
		add(addButton);
		add(deleteButton);
		add(lblRowCount);
		//pack();

	}

	// ADDS THE LISTENERS FOR THE BUTTONS ON THE PANEL
	private void addListeners() {

		// WHEN THIS BUTTON IS PRESSED THE RECORD ON WHICH USER WAS WORKING IS SAVED
		// AND MOVES THE SSROWSET TO THE FIRST ROW
		// SINCE ROW SET IS IN FIRST ROW DISABLE PREVIOUS BUTTON AND ENABLE NEXT BUTTON
		firstButton.addActionListener(new ActionListener(){ 
			public void actionPerformed(ActionEvent ae) {
				try {
					if ( modification ) {
						rowset.updateRow();
                    }
					rowset.first();
					
					firstButton.setEnabled(false);
					previousButton.setEnabled(false);
					if (!rowset.isLast()) {
						nextButton.setEnabled(true);
						lastButton.setEnabled(true);
					} else {
						nextButton.setEnabled(false);
						lastButton.setEnabled(false);
					}
					if ( dbNav != null ) {
						dbNav.performNavigationOps(SSDBNav.NAVIGATION_FIRST);
                    }
				// GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD	
					currentRow = 1;
					txtCurrentRow.setText(String.valueOf(currentRow));	
				} catch(SQLException se) {
					se.printStackTrace();
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while updating row or moving the cursor.\n"+se.getMessage());
				}
			}
		});

		// WHEN BUTTON 2 IS PRESSED THE CURRENT RECORD IS SAVED AND SSROWSET IS
		// MOVED TO PREVIOUS RECORD
		// CALLING PREVIOUS ON ENPTY SSROWSET IS ILLEGAL SO A CHECK IS MADE FOR THAT
		// IF NUMBER OF ROWS == 0 THEN SSROWSET IS EMPTY
		previousButton.addActionListener(new ActionListener() {
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
						firstButton.setEnabled(false);
						previousButton.setEnabled(false);
					}

					// IF NEXT BUTTON IS DISABLED ENABLE IT.
					if ( !rowset.isLast() ) {
						nextButton.setEnabled(true);
						lastButton.setEnabled(true);
					}
						
					if ( dbNav != null ) {
						dbNav.performNavigationOps(SSDBNav.NAVIGATION_PREVIOUS);
                    }
				// GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD		
					currentRow = rowset.getRow();	
					txtCurrentRow.setText(String.valueOf(currentRow));
				} catch(SQLException se) {
					se.printStackTrace();
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while updating row or moving the cursor.\n"+se.getMessage());
				}
			}
		});

		// WHEN BUTTON 3 PRESSED THE CURRENT RECORD IS SAVED AND THE SSROWSET IS
		// MOVED TO NEXT RECORD. IF THIS IS THE LAST RECORD THEN BUTTON 3 IS DISABLED
		// ALSO IF THE PREVIOUS BUTTON IS NOT ENABLED THEN IT IS ENABLED
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					//if( rowset.rowUpdated() )
					if ( modification ) {
						rowset.updateRow();
                    }
					if ( !rowset.next() ) {
						nextButton.setEnabled(false);
						lastButton.setEnabled(false);
						rowset.last();
					}
					// IF LAST RECORD THEN DISABLE NEXT BUTTON
					if ( rowset.isLast() ) {
						nextButton.setEnabled(false);
						lastButton.setEnabled(false);
					}

					// IF THIS IS NOT FIRST ROW ENABLE FIRST AND PREVIOUS BUTTONS
					if ( !rowset.isFirst() ) {
						previousButton.setEnabled(true);
						firstButton.setEnabled(true);	
					}
											
					if ( dbNav != null ) {
						dbNav.performNavigationOps(SSDBNav.NAVIGATION_NEXT);
                    }
				// GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD	
					currentRow = rowset.getRow();
					txtCurrentRow.setText(String.valueOf(currentRow));
				} catch(SQLException se) {
					se.printStackTrace();
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while updating row or moving the cursor.\n"+se.getMessage());
				}
			}
		});


		// BUTTON 4 ( "LAST" BUTTON )  CAUSED THE SSROWSET TO MOVE TO LAST RECORD.
		// BEFORE MOVING CURRENT RECORD IS SAVED
		// AFTER MOVING TO LAST RECORD THE NEXT BUTTON IS DIAABLED AND PREVIOUS BUTTON
		// ENABLED
		lastButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					//if( rowset.rowUpdated() )
					if ( modification ) {
						rowset.updateRow();
                    }
					rowset.last();
					
					nextButton.setEnabled(false);
					lastButton.setEnabled(false);
					if (!rowset.isFirst()) {
						firstButton.setEnabled(true);
						previousButton.setEnabled(true);
					} else {
						firstButton.setEnabled(false);
						previousButton.setEnabled(false);
					}
					if ( dbNav != null ) {
						dbNav.performNavigationOps(SSDBNav.NAVIGATION_LAST);
                    }
				// GET THE ROW NUMBER AND SET IT TO ROW NUMBER TEXT FIELD	
					currentRow = rowset.getRow();
					txtCurrentRow.setText(String.valueOf(currentRow));	
				} catch(SQLException se) {
					se.printStackTrace();
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while updating row or moving the cursor.\n"+se.getMessage());
				}
			}
		});

		// THIS BUTTON INSERTS THE ROW AND MOVES TO THE NEWLY INSERTED ROW.
		// WHEN INSERT BUTTON IS PRESSED NAVIGATION WILL BE DISABLED SO THOSE HAVE TO BE
		// ENABLED HERE
		commitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					if (onInsertRow) {
                    // IF ON INSERT ROW ADD THE ROW.
                        rowset.insertRow();
						if ( dbNav != null ) {
							dbNav.performPostInsertOps();
                        }
					
						rowset.moveToCurrentRow();
					// MOVE TO CURRENT ROW MOVES SSROWSET TO RECORD AT WHICH ADD WAS PRESSED.
					// BUT IT NICE TO BE ON THE ADDED ROW WHICH IS THE LAST ONE IN THE SSROWSET.
					// ALSO MOVE TO CURRENT ROW MOVES THE SSROWSET POSITION BUT DOES NOT TRIGGER
					// ANY EVENT FOR THE LISTENERS AS A RESULT VALUES ON THE SCREEN WILL NOT
					// DISPLAY THE CURRENT RECORD VALUES.
						rowset.last();
					// INCREMENT THE ROW COUNT
						rowCount++;
					// SET THE ROW COUNT AS LABEL
						lblRowCount.setText("of " + rowCount);	
					// GET CURRENT ROW NUMBER
						currentRow = rowset.getRow();
					// UPDATE THE TEXT FEILD
						txtCurrentRow.setText(String.valueOf(currentRow));		
					} else {
                    // ELSE UPDATE THE PRESENT ROW VALUES.	
						rowset.updateRow();
                    }
						
					onInsertRow = false;
					
					if (!rowset.isFirst()) {
						firstButton.setEnabled(true);
						previousButton.setEnabled(true);
					}
					if (!rowset.isLast()) {
						nextButton.setEnabled(true);
						lastButton.setEnabled(true);
					}
					refreshButton.setEnabled(true);
					
					if (allowInsertions) {
						addButton.setEnabled(true);
                    }
					if (allowDeletions) {
						deleteButton.setEnabled(true);
                    }
				} catch(SQLException se) {
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while inserting row.\n"+se.getMessage());
					se.printStackTrace();
				}
			}
		});

		// THIS BUTTON IS USED TO CANCEL THE CHANGES MADE TO THE RECORD.
		// IT CAN ALSO BE USED TO CANCEL INSERT ROW.
		// SO THE BUTTONS DISABLED AT THE INSERT BUTTON EVENT HAVE TO BE ENABLED
		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					rowset.cancelRowUpdates();
					onInsertRow = false;
					if (dbNav != null) {
						dbNav.performCancelOps();
                    }
					//rowset.deleteRow();
					//rowset.moveToCurrentRow();
					firstButton.setEnabled(true);
					previousButton.setEnabled(true);
					nextButton.setEnabled(true);
					lastButton.setEnabled(true);
					refreshButton.setEnabled(true);
					if (allowInsertions) {
						addButton.setEnabled(true);
                    }
					if (allowDeletions) {
						deleteButton.setEnabled(true);
                    }
				} catch(SQLException se) {
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while undoing changes.\n"+se.getMessage());
					se.printStackTrace();
				}
			}
		});

		// REFETCH REFETCHES THE ROWS FROMS THE DATABASE AND MOVES THE CURSOR TO THE FIRST
		// RECORD IF THERE ARE NO RECORDS NAVIGATION BUTTONS ARE DIABLED
		// EVEN IS THERE ARE RECORDS PREVIOUS BUTTON IS DISABLED BECAUSE THE SSROWSET IS ON
		// THE FIRST ROW
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				rowset.removeRowSetListener(rowsetListener);
				try {
					if (callExecute) {
						rowset.execute();
						if (!rowset.next()) {
							rowCount = 0;
							currentRow = 0;
							firstButton.setEnabled(false);
							previousButton.setEnabled(false);
							nextButton.setEnabled(false);
							lastButton.setEnabled(false);
							
						} else {
						// IF THERE ARE ROWS GET THE ROW COUNT	
							rowset.last();
							rowCount = rowset.getRow();
							rowset.first();
							currentRow = rowset.getRow();
							firstButton.setEnabled(false);
							previousButton.setEnabled(false);
							nextButton.setEnabled(true);
							lastButton.setEnabled(true);
						}
					// SET THE ROW COUNT AS LABEL
						lblRowCount.setText("of " + rowCount);	
						txtCurrentRow.setText(String.valueOf(currentRow));
                    }
						
					if ( dbNav != null ) {
						dbNav.performRefreshOps();
                    }
						
				} catch(SQLException se) {
					se.printStackTrace();
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured refreshing the data.\n"+se.getMessage());
				}
				rowset.addRowSetListener(rowsetListener);
			}
		});

		// INSERT ROW BUTTON MOVES THE SSROWSET TO THE INSERT ROW POSITION
		// AT THIS TIME NAVIGATION HAS TO BE DISABLED
		// ONLY COMMIT AND CANCEL ARE ENABLED
		addButton.addActionListener(new ActionListener() {
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
					firstButton.setEnabled(false);
					previousButton.setEnabled(false);
					nextButton.setEnabled(false);
					lastButton.setEnabled(false);
					commitButton.setEnabled(true);
					undoButton.setEnabled(true);
					refreshButton.setEnabled(false);
					addButton.setEnabled(false);
					deleteButton.setEnabled(false);

				} catch(SQLException se) {
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while moving to insert row.\n"+se.getMessage());
					se.printStackTrace();
				}
			}
		});

		// DELETES THE CURRENT ROW AND MOVES TO NEXT ROW
		// IF THE DELETED ROW IS THE LAST ROW THEN MOVES TO LAST ROW IN SSROWSET
		// AFTER THE DELETION IS MADE (THATS THE PREVIOUS ROW TO THE DELETED ROW)
		deleteButton.addActionListener(new ActionListener() {
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
                // SEEMS DELETION WAS SUCCESSFULL DECREMENT ROWCOUNT
                	rowCount--;
                // SET THE ROW COUNT AS LABEL
					lblRowCount.setText("of " + rowCount);	
				// GET CURRENT ROW NUMBER
					currentRow = rowset.getRow();
				// UPDATE THE TEXT FEILD
					txtCurrentRow.setText(String.valueOf(currentRow));			
				} catch(SQLException se) {
					JOptionPane.showMessageDialog(SSDataNavigator.this,"Exception occured while deleting row.\n"+se.getMessage());
					se.printStackTrace();
				}
			}
		});
		
	// LISTENER FOR THE TEXT FIELD. USER CAN ENTER A ROW NUMBER IN THE TEXT
	// FIELD TO MOVE THE THE SPEICIFIED ROW. 
	// IF ITS NOT A NUMBER OR IF ITS NOT VALID FOR THE CURRENT SSROWSET
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
	
	private class SSDBNavRowSetListener implements RowSetListener{
		
		public void cursorMoved(RowSetEvent rse){
		// IF THERE ARE ROWS GET THE ROW COUNT	
			try{
				currentRow = rowset.getRow();
				updateInfo();
			}catch(SQLException se){
				se.printStackTrace();
			}
		}
		
		public void rowChanged(RowSetEvent rse){
		// DO NOTHING
		}
		
		public void rowSetChanged(RowSetEvent rse){
		// IF THERE ARE ROWS GET THE ROW COUNT		
			try{
				rowset.last();
				rowCount = rowset.getRow();
				rowset.first();
				currentRow = rowset.getRow();
				updateInfo();
			}catch(SQLException se){
				se.printStackTrace();
			}
			updateInfo();
		}
		
		protected void updateInfo(){
		// SET THE ROW COUNT AS LABEL
			lblRowCount.setText("of " + rowCount);	
			txtCurrentRow.setText(String.valueOf(currentRow));
		// ENABLE OR DISABLE BUTTONS
			if (rowCount == 0) {
				firstButton.setEnabled(false);
				previousButton.setEnabled(false);
				nextButton.setEnabled(false);
				lastButton.setEnabled(false);
			} else{
				firstButton.setEnabled(true);
				previousButton.setEnabled(true);
				nextButton.setEnabled(true);
				lastButton.setEnabled(true);
			}
	
			try {
				if (rowset.isLast()) {
					nextButton.setEnabled(false);
					lastButton.setEnabled(false);
				}
				if (rowset.isFirst()) {
					firstButton.setEnabled(false);
					previousButton.setEnabled(false);
				}
					
			} catch(SQLException se) {
				se.printStackTrace();
			}
		}
	}
		
} // end public class SSDataNavigator extends JPanel {



/*
 * $Log$
 * Revision 1.21  2004/10/25 19:51:02  prasanth
 * Modified to use the new SSRowSet instead of  RowSet.
 *
 * Revision 1.20  2004/09/21 18:58:28  prasanth
 * removing the rowset listener while doing the refresh ops.
 *
 * Revision 1.19  2004/09/21 14:15:33  prasanth
 * Displaying error messages when an exception occurs, when the user presses
 * any button.
 *
 * Revision 1.18  2004/09/08 18:41:54  prasanth
 * Added a rowset listener.
 *
 * Revision 1.17  2004/09/02 16:37:05  prasanth
 * Moving to the last record if your has added a record & pressed commit button.
 * This would keep the user in the added record.
 *
 * Revision 1.16  2004/09/01 18:42:08  prasanth
 * Was calling next in the refresh listener. This would move to second record
 * if refresh is pressed. If there is only one record then user will not be able to
 * see as the navigation buttons are also disabled.
 *
 * Revision 1.15  2004/08/26 22:00:00  prasanth
 * Setting all the buttons when a new rowset is set.
 *
 * Revision 1.14  2004/08/24 22:21:03  prasanth
 * Changed the way images are loaded.
 *
 * Revision 1.13  2004/08/16 20:51:16  yoda2
 * Gave button names in code more meaningful names (e.g. button1 -> firstButton).
 *
 * Revision 1.12  2004/08/13 14:55:28  prasanth
 * Changed the default size of buttons (decreased).
 * Also that of text field  & label (increased).
 * Displaying "of" before rowcount.
 *
 * Revision 1.11  2004/08/12 23:50:24  prasanth
 * Changing the row count when a new row is added or a row is deleted.
 *
 * Revision 1.10  2004/08/11 20:29:01  prasanth
 * When rowset has no rows the values in text field and row count label were
 * not updated so corrected this.
 *
 * Revision 1.9  2004/08/10 22:06:59  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
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