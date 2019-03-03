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

package com.nqadmin.swingSet.formatting;

import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.sql.RowSetListener;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.datasources.SSRowSet;

/**
 * SSBooleanField.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Used to link a JCheckBox to a boolean column in a database.
 */
public class SSBooleanField extends JCheckBox implements RowSetListener, KeyListener {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -856226927518717477L;
	protected String columnName = null;
	protected int colType = -99;
	protected SSRowSet rowset = null;
	private SSDataNavigator navigator = null;

	/** Creates a new instance of SSBooleanField */
	public SSBooleanField() {
		super();

		Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newForwardKeys = new HashSet<>(forwardKeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);

		Set<AWTKeyStroke> backwardKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newBackwardKeys = new HashSet<>(backwardKeys);
		newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_MASK));
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newBackwardKeys);

		this.addKeyListener(this);

		this.setInputVerifier(new internalVerifier());
	}

	/**
	 * Sets the SSRowSet column name to which the component is bound.
	 * 
	 * @param _columnName - column name to which the component is bound
	 */
	public void setColumnName(final String _columnName) {
		this.columnName = _columnName;
		bind();
	}

	/**
	 * Returns the SSRowSet column name to which the component is bound.
	 * 
	 * @return returns column name to which the component is bound
	 */
	public String getColumnName() {
		return this.columnName;
	}

	/**
	 * Sets the SSRowSet to which the component is bound
	 * 
	 * @param _rowset SSRowSet to which the component is bound
	 * @deprecated
	 * @see #setSSRowSet(SSRowSet)
	 */
	@Deprecated
	public void setRowSet(final SSRowSet _rowset) {
		this.setSSRowSet(_rowset);
	}

	/**
	 * Sets the SSRowSet to which the component is bound.
	 * 
	 * @param _rowset SSRowSet to which the component is bound.
	 */
	public void setSSRowSet(final SSRowSet _rowset) {
		this.rowset = _rowset;
		bind();
	}

	/**
	 * Returns the SSRowSet to which the component is bound
	 * 
	 * @return returns the SSRowSet to which the component is bound
	 */
	public SSRowSet getRowSet() {
		return this.getSSRowSet();
	}

	/**
	 * Returns the SSRowSet to which the component is bound
	 * 
	 * @return returns the SSRowSet to which the component is bound
	 */
	public SSRowSet getSSRowSet() {
		return this.rowset;
	}

	/**
	 * Sets the SSDataNavigator used for navigating the SSRowSet to which this
	 * component is bound to. <font color=red>The functionality for this is not yet
	 * finalized so try to avoid using this </font>
	 * 
	 * @param _navigator - SSDataNavigator used for navigating the SSRowSet to which
	 *                   this component is bound to
	 * @deprecated
	 * @see #setSSDataNavigator(SSDataNavigator)
	 **/
	@Deprecated
	public void setNavigator(final SSDataNavigator _navigator) {
		this.setSSDataNavigator(_navigator);
	}

	/**
	 * Returns the SSDataNavigator being used
	 * 
	 * @return returns the SSDataNavigator being used
	 * @deprecated
	 * @see #setSSDataNavigator(SSDataNavigator)
	 **/
	@Deprecated
	public SSDataNavigator getNavigator() {
		return this.getSSDataNavigator();
	}

	/**
	 * Sets the SSDataNavigator used for navigating the SSRowSet to which this
	 * component is bound to. <font color=red>The functionality for this is not yet
	 * finalized so try to avoid using this </font>
	 * 
	 * @param _navigator - SSDataNavigator used for navigating the SSRowSet to which
	 *                   this component is bound to
	 */
	public void setSSDataNavigator(final SSDataNavigator _navigator) {
		this.navigator = _navigator;
		setSSRowSet(_navigator.getSSRowSet());
		bind();
	}

	/**
	 * Returns the SSDataNavigator being used
	 * 
	 * @return returns the SSDataNavigator being used
	 */
	public SSDataNavigator getSSDataNavigator() {
		return this.navigator;
	}

	/**
	 * Fetches the value from rowset and updates the field accordingly
	 */
	private void DbToFm() {

		try {
			if (this.rowset.getRow() == 0)
				return;

			switch (this.colType) {

			case java.sql.Types.BIT:// -7
				this.setSelected(this.rowset.getBoolean(this.columnName));
				break;

			case java.sql.Types.BOOLEAN:// 16
				this.setSelected(this.rowset.getBoolean(this.columnName));
				break;

			case java.sql.Types.INTEGER:// 4
			case java.sql.Types.BIGINT:// -5
			case java.sql.Types.SMALLINT:// 5
			case java.sql.Types.TINYINT:// -6
				if (this.rowset.getInt(this.columnName) == 1)
					this.setSelected(true);
				else
					this.setSelected(false);
				break;

			default:
				break;
			}
		} catch (java.sql.SQLException sqe) {
			System.out.println("Error in DbToFm() = " + sqe);
		}
	}

	/**
	 * Sets the SSRowSet and column name to which the component is to be bound.
	 *
	 * @param _sSRowSet   datasource to be used.
	 * @param _columnName Name of the column to which this check box should be bound
	 */
	public void bind(final SSRowSet _sSRowSet, final String _columnName) {
		this.rowset = _sSRowSet;
		this.columnName = _columnName;
		bind();
	}

	/**
	 * Binds this component to the specified column in the given rowset.
	 */
	private void bind() {

		if (this.columnName == null)
			return;
		if (this.rowset == null)
			return;

		try {
			this.colType = this.rowset.getColumnType(this.columnName);
		} catch (java.sql.SQLException sqe) {
			System.out.println("bind error = " + sqe);
		}
		this.rowset.addRowSetListener(this);
		DbToFm();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sql.RowSetListener#rowSetChanged(javax.sql.RowSetEvent)
	 */
	@Override
	public void rowSetChanged(final javax.sql.RowSetEvent _event) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sql.RowSetListener#rowChanged(javax.sql.RowSetEvent)
	 */
	@Override
	public void rowChanged(final javax.sql.RowSetEvent _event) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sql.RowSetListener#cursorMoved(javax.sql.RowSetEvent)
	 */
	@Override
	public void cursorMoved(final javax.sql.RowSetEvent _event) {
		DbToFm();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(final KeyEvent _event) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(final KeyEvent _event) {
		// do nothing
	}

	/**
	 * Catch severals keys, to implement some forms functionality (To be don_event).
	 */
	@Override
	public void keyPressed(final KeyEvent _event) {

		if (_event.getKeyCode() == KeyEvent.VK_F1) {
			// do nothing
		}

		if (_event.getKeyCode() == KeyEvent.VK_F2) {
			// do nothing
		}

		if (_event.getKeyCode() == KeyEvent.VK_F3) {
			// do nothing
		}

		if (_event.getKeyCode() == KeyEvent.VK_F4) {
			// do nothing
		}

		if (_event.getKeyCode() == KeyEvent.VK_F5) {
			System.out.println("F5 = PROCESS");
			this.navigator.doCommitButtonClick();
		}

		if (_event.getKeyCode() == KeyEvent.VK_F6) {
			System.out.println("F6 = DELETE");
			this.navigator.doDeleteButtonClick();
		}

		if (_event.getKeyCode() == KeyEvent.VK_F8) {
			System.out.println("F8 ");
			this.navigator.doUndoButtonClick();
		}

		if (_event.getKeyCode() == KeyEvent.VK_END) {
			System.out.println("END ");
		}

		if (_event.getKeyCode() == KeyEvent.VK_DELETE) {
			System.out.println("DELETE ");
		}

		if (_event.getKeyCode() == KeyEvent.VK_HOME) {
			System.out.println("HOME ");
		}

	}

	/**
	 * This method should implements validation AND, most important for our purposes
	 * implements actual rowset fields updates.
	 *
	 */
	protected class internalVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {

			SSBooleanField tf = (SSBooleanField) input;
			boolean selected = tf.isSelected();

			setBackground(java.awt.Color.WHITE);

			// if not linked to a db field, returns.
			if (SSBooleanField.this.getColumnName() == null || SSBooleanField.this.rowset == null)
				return true;

			try {
				SSBooleanField.this.rowset.removeRowSetListener(tf);

				switch (SSBooleanField.this.colType) {

				case java.sql.Types.BIT:// -7
					SSBooleanField.this.rowset.updateBoolean(SSBooleanField.this.columnName, selected);
					break;

				case java.sql.Types.BOOLEAN:// 16
					SSBooleanField.this.rowset.updateBoolean(SSBooleanField.this.columnName, selected);
					break;

				case java.sql.Types.INTEGER: // 4
				case java.sql.Types.BIGINT: // -5
				case java.sql.Types.SMALLINT: // 5
				case java.sql.Types.TINYINT: // -6
					if (selected == true) {
						SSBooleanField.this.rowset.updateInt(SSBooleanField.this.columnName, 1);
					} else {
						SSBooleanField.this.rowset.updateInt(SSBooleanField.this.columnName, 0);
					}
					break;

				default:
					break;
				}
				SSBooleanField.this.rowset.addRowSetListener(tf);
			} catch (java.sql.SQLException se) {
				System.out.println("SSBooleanField ---> SQLException -----------> " + se);
			} catch (java.lang.NullPointerException np) {
				System.out.println("SSBooleanField ---> NullPointerException ---> " + np);
			}
			return true;
		}
	}
}

/*
 * $Log$ Revision 1.14 2006/04/27 22:02:45 prasanth Added/updated java doc
 *
 * Revision 1.13 2005/05/29 02:24:37 dags SSConnection and SSRowSet getters and
 * setter refactoring
 *
 * Revision 1.12 2005/05/26 22:20:36 dags SSField interface implemented
 *
 * Revision 1.11 2005/05/26 12:12:36 dags added bind(SSRowSet, columnName)
 * method and some java.sql.Types checking and support
 *
 * Revision 1.10 2005/03/30 13:03:51 dags Accept null dates values
 *
 * Revision 1.9 2005/03/28 14:46:42 dags syncro commit
 *
 * Revision 1.8 2005/02/22 15:14:34 yoda2 Fixed some JavaDoc & deprecation
 * errors/warnings.
 *
 * Revision 1.7 2005/02/04 22:42:06 yoda2 Updated Copyright info.
 *
 * Revision 1.6 2005/01/19 19:12:26 dags bind refactoring
 *
 * Revision 1.5 2005/01/14 00:06:42 dags Deep Refactoring
 *
 * Revision 1.4 2004/12/21 05:07:02 dags Remove SSFormattedTextField dependency.
 * Simplified, I hope.
 *
 * Revision 1.3 2004/12/13 20:50:16 dags Fix package name
 *
 * Revision 1.2 2004/12/13 18:46:13 prasanth Added License.
 *
 */
