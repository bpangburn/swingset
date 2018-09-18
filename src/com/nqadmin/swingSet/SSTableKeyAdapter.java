/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004-2006, The Pangburn Company and Prasanth R. Pasala
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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 * SSTableKeyAdapter.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Key adapter for JTable & SSDataGrid that manages cut & paste functionality
 * between a table and either another table or a spreadsheet.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
public class SSTableKeyAdapter extends KeyAdapter implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2748762202415891694L;

	/**
     * On state for copying or pasting.
     */
    protected int onMask = KeyEvent.CTRL_DOWN_MASK;

    /**
     * Off state for copying or pasting.
     */
    protected int offMask = KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK;

    /**
     * Indicates whether or not row insertions are allowed via cut/copy & paste.
     */
    protected boolean allowInsertion = false;

    /**
     * Indicates row used for insertion in SSDataGrid.
     */
    protected boolean forSSDataGrid = false;

    /**
     * Constructs a KeyAdapter for the JTable.
     *
     * @param _jTable    JTable for which copy and paste support should be added.
     */
    public SSTableKeyAdapter(JTable _jTable) {
        init(_jTable);
    }

    /**
     * Sets allowInsertion indicator. Set true if new rows can be added to
     * JTable via cut/copy & paste - otherwise false.  False by default.
     *
     * @param _allowInsertion  true if new rows can be added when pasting data
     *  from clipboard, else false.
     */
    public void setAllowInsertion(boolean _allowInsertion) {
        allowInsertion = _allowInsertion;
    }

    /**
     * Sets forSSDataGrid indicator. True if the key adapter is
     * used for SSDataGrid -- otherwise false. False by default.
     *
     * @param _forSSDataGrid - true if this key adapter is used for SSDataGrid,
     *  else false.
     */
    public void setForSSDataGrid(boolean _forSSDataGrid) {
        forSSDataGrid = _forSSDataGrid;
    }

    /**
     *  Adds the key listener for the specified JTable.
     */
    protected void init(JTable _jTable) {
        _jTable.addKeyListener(this);
    }

    /**
     *  Invoked when a key is released.
     */
    public void keyReleased(KeyEvent ke) {
        StringBuffer strBuf = new StringBuffer();

        JTable jTable = (JTable)ke.getSource();
//      System.out.println("Key Released on GRID");
 //     System.out.println("Key Released: " + ke.getKeyCode() + "   " + ((ke.getModifiersEx() & (onMask | offMask)) == onMask));

        if (((ke.getModifiersEx() & (onMask | offMask)) == onMask) && ke.getKeyCode() == KeyEvent.VK_C) {
        // CHECK IF CONTROL-C IS PRESSED
        // SHIFT OR ALT SHOULD NOT BE DOWN

            // ALERT USER
            //    System.out.println("Going to handle copy");

            // GET COLUMNS INVOLVED
                int numRows = jTable.getSelectedRowCount();
                int numColumns = 0;
                if (jTable instanceof SSDataGrid) {
                    numColumns = ((SSDataGrid)jTable).getSelectedColumnCount();
                } else {
                    numColumns = jTable.getSelectedColumnCount();
                }

            // CHECK IF THERE IS ATLEAST ONE SELECTED CELL.
            // IF NOT NOTHING TO COPY JUST RETURN.
                if (numRows < 1 || numColumns < 1) {
                    return;
                }

            // GET THE ROWS AND COLUMNS SELECTED.
                int[] selectedRows = jTable.getSelectedRows();
                int[] selectedColumns = null;
                if (jTable instanceof SSDataGrid) {
                    selectedColumns = ((SSDataGrid)jTable).getSelectedColumns();
                } else {
                    selectedColumns = jTable.getSelectedColumns();
                }

            // COPY THE DATA IN THE SELECTED ROWS AND COLUMNS
            // APPEND A TAB AFTER EACH CELL AND A NEW LINE CHAR AT END OF EACH ROW.
                for (int i=0; i<selectedRows.length; i++) {
                    for (int j=0; j<selectedColumns.length; j++) {
                        strBuf.append(jTable.getValueAt(selectedRows[i], selectedColumns[j]));
                        if (j < (numColumns -1)) {
                            strBuf.append("\t");
                        }
                    }
                    strBuf.append("\n");
                }

            // GET THE SYSTEM CLIPBOARD
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            // CREATE A TRANSFERABLE OBJECT
                StringSelection stringSelection = new StringSelection(strBuf.toString());

            // COPY THE DATA TO CLIP BOARD
                clipboard.setContents(stringSelection,stringSelection);

        } else if (((ke.getModifiersEx() & (onMask | offMask)) == onMask) && ke.getKeyCode() == KeyEvent.VK_V) {
        // CHECK IF CONTROL-V IS PRESSED
        // SHIFT OR ALT SHOULD NOT BE DOWN

            // GET CLIPBOARD
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            // GET THE CONTENTS OF THE CLIPBOARD
                Transferable transferable = clipboard.getContents(this);

            // IF THE CONTENT TYPE SUPPORTS STRING  TYPE GET THE DATA.
                if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                // GET DATA FROM CLIPBOARD
                    String strData = "";
                    try {
                        strData = (String)transferable.getTransferData(DataFlavor.stringFlavor);
                    } catch(UnsupportedFlavorException ufe) {
                        ufe.printStackTrace();
                        return;
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                        return;
                    }

                // IF USER HAS NOT SELECTED ANY CELL THEN DO NOT COPY.
                    if (jTable.getSelectedRowCount() < 1 || jTable.getSelectedColumnCount() < 1) {
                        return;
                    }

                // GET THE SELECTED ROWS AND COLUMNS
                    int[] selectedRows = jTable.getSelectedRows();
                    int[] selectedColumns = jTable.getSelectedColumns();

                // CREATE A STRING TOKENIZER TO BREAK THE DATA INTO ROWS.
                    StringTokenizer rowTokens = new StringTokenizer(strData, "\n", false);

                // INITIALIZE THE NUMBER OF ROWS AND COLUMNS
                    int numRows = 0;
                    int numColumns = 0;

                    StringTokenizer columnTokens;

                // SEE IF THE DATA HAS ATLEAST ONE ROW
                // IF IT DOES GET THE NUMBER OF COLUMNS PRESENT.
                    if (rowTokens.hasMoreTokens()) {
                        numRows++;
                        columnTokens = new StringTokenizer(rowTokens.nextToken(), "\t", false);
                        // WHILE THERE ARE TOKENS INCREMENT THE COLUMN COUNT
                            for (; columnTokens.hasMoreTokens(); numColumns++) {
                                columnTokens.nextToken();
                            }
                    }

                // GET THE NUMBER OF ROWS PRESENT
                    for(; rowTokens.hasMoreTokens(); numRows++){
                        rowTokens.nextToken();
                    }

                // GET THE NUMBER OF COLUMNS AND ROWS IN THE JTABLE.
                    int rowCount = jTable.getRowCount();
                    int columnCount = jTable.getColumnCount();

                    if (forSSDataGrid || jTable instanceof SSDataGrid) {
                        rowCount--;
                    }

                // IF THE NUMBER OF COLUMNS NEEDED TO COPY FROM CLIPBOARD
                // IS MORE THAN THAT IN JTABLE CANCEL COPY
                // YOU HAVE TO CHECK THIS FROM THE STARTING SELECTED CELL
                // NO NEED TO ADD ONE FOR SELECTEDCOLUMNS[0] AS THE PASTING STARTS FROM THAT ITSELF
                    if (columnCount < (numColumns + selectedColumns[0])) {
                        JOptionPane.showMessageDialog(jTable, "There are not enough columns in the table to copy into.\n");
                        return;
                    }

                    int numRowsToCopy = numRows;

                // SEE IF SUFFICENT ROWS ARE PRESENT.
                    if (rowCount < (numRows + selectedRows[0])) {
                        JOptionPane.showMessageDialog(jTable, "There are not enough rows in the table to copy into.\n");
                        if (allowInsertion) {
                            int option = JOptionPane.showConfirmDialog(jTable, "Do you want to insert new rows?", "Add Rows", JOptionPane.YES_NO_OPTION);
                            if (option == JOptionPane.YES_OPTION) {
                            // SET THE NUMBER OF ROWS TO COPY EQUAL TO
                            // NUMBER OF ROWS IN DATA.
                                numRowsToCopy = numRows;
                            } else if (JOptionPane.showConfirmDialog(jTable, "Do you want to copy data into just the rows present?", "Limited Copy", JOptionPane.YES_NO_OPTION)
                                == JOptionPane.YES_OPTION) {
                            // GET THE NUMBER OF ROWS FROM SELECTED ROW TO LAST ROW
                            // NO NEED TO ADD A ONE
                                numRowsToCopy = rowCount - selectedRows[0];
                            }
                        } else if (JOptionPane.showConfirmDialog(jTable, "Do you want to copy data into just the rows present?", "Limited Copy", JOptionPane.YES_NO_OPTION)
                            == JOptionPane.YES_OPTION) {
                        // GET THE NUMBER OF ROWS FROM SELECTED ROW TO LAST ROW
                        // NO NEED TO ADD A ONE
                            numRowsToCopy = rowCount - selectedRows[0];
                        }

                    }

                // COPY THE DATA FROM CLIP BOARD TO JTABLE
                    try {
                    // TOKENIZE DATA IN TO ROWS
                        rowTokens = new StringTokenizer(strData, "\n", false);

                    // WHILE THERE ARE ROWS IN THE CLIP BOARD DATA AND NUMBER OF ROWS
                    // COPIED IS LESS THEN WE SHOULD COPY DATA TO JTABLE.
                        for (int i=0;rowTokens.hasMoreTokens() && i<numRowsToCopy; i++) {
                        // TOKENIZE THE ROW INFORMATION IN TO COLUMNS
                            columnTokens = new StringTokenizer(rowTokens.nextToken(), "\t", false);
                        // PASTE THE DATA IN TO JTABLE ROW.
                            for (int j=0; columnTokens.hasMoreTokens(); j++) {
                            // GET THE VALUE FOR THIS CELL IN THE FORM OF THIS COLUMN CLASS OBJECT
                                Object newValue = getObjectToSet(jTable, selectedColumns[0]+j, columnTokens.nextToken());
                            // SET THE VALUE FOR THE COLUMN
                                jTable.setValueAt(newValue, selectedRows[0]+i, selectedColumns[0]+j);
                            }
                        }

                    // UPDATE THE UI AS WE HAVE UPDATED UNDERLIYING DATA
                    // THIS HAS TO PROPOGATE TO THE SCREEN.
                        jTable.updateUI();

                    } catch(NoSuchMethodException nsme) {
                        nsme.printStackTrace();
                        JOptionPane.showMessageDialog(jTable, "One of the column class does not provide a constructor"
                            + "that takes a single String Argument");
                    } catch(SecurityException se) {
                        se.printStackTrace();
                        JOptionPane.showMessageDialog(jTable, "One of the column class does not provide a constructor"
                            + "that takes a single String Argument");
                    } catch(InstantiationException ie) {
                        ie.printStackTrace();
                        JOptionPane.showMessageDialog(jTable, "Failed to copy data. Error occured while instantiating"
                            +"a single String argument constructor for a column ");
                    } catch(Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(jTable, "Failed to copy data.");
                    }

                } // end if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {

        } // end if (((ke.getModifiersEx() & (onMask | offMask)) == onMask) && ke.getKeyCode() == KeyEvent.VK_C) {

    } // end public void keyReleased(KeyEvent ke) {

    /**
     * Takes the column number and string value to be set for that column and
     * converts the string in to appropriate class.
     * The class is found by calling the getColumnClass() method of the JTable.
     *
     * @param _jTable   JTable containing target object
     * @param _column   the column number for which new value has to be set.
     * @param _value   string representation of the new value.
     *
     * @return returns the value as a column class object.
     */
    protected Object getObjectToSet(JTable _jTable, int _column, String _value) throws Exception {
    // GET THE COLUMN CLASS
        Class objectClass = _jTable.getColumnClass(_column);
        Object newValue = null;
        try {
        	// CONSTRUCT THE OBJECT ONLY IF THE STRING IS NOT NULL
        	if(_value != null) {
	        // GET THE CONSTRUCTOR FOR THE CLASS WHICH TAKES A STRING
	            Constructor constructor = objectClass.getConstructor(new Class[]{String.class});
	
	        // CREATE AN INSTANCE OF THE OBJECT
	            newValue = constructor.newInstance(new Object[]{_value});
        	}
        } catch(NoSuchMethodException nsme) {
            newValue = _value;
        }

    // RETURN THE NEWLY CREATED OBJECT.
        return newValue;

    }

 } // end public class SSTableKeyAdapter extends KeyAdapter implements Serializable {

/*
 * $Log$
 * Revision 1.7  2006/05/15 16:10:38  prasanth
 * Updated copy right
 *
 * Revision 1.6  2005/02/09 21:22:35  yoda2
 * JavaDoc cleanup.
 *
 * Revision 1.5  2005/02/04 22:48:54  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.4  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.3  2004/08/11 14:44:31  prasanth
 * Added java doc.
 *
 * Revision 1.2  2004/08/10 22:06:59  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.1  2004/07/30 22:38:54  prasanth
 * Table listener for copy and paste support.
 *
 */