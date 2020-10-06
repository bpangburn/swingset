/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

package com.nqadmin.swingset.formatting;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.datasources.SSRowSet;

/**
 * SSBooleanField.java
 * <p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * <p>
 * Used to link a JCheckBox to a boolean column in a database.
 * <p>
 * Other than some function key handling, which is likely outside the scope of
 * SwingSet and should be customized at the application level, this class appears to 
 * mostly duplicate SSCheckBox.
 * <p>
 * It does not extend SSFormattedText field like the other classes in this package.
 * <p>
 * @deprecated Starting in 4.0.0+ use {@link com.nqadmin.swingset.SSCheckBox} instead.
 */
@Deprecated
public class SSBooleanField extends JCheckBox implements RowSetListener, KeyListener {

	/**
	 * This method should implements validation AND, most important for our purposes
	 * implements actual rowset fields updates.
	 */
	protected class internalVerifier extends InputVerifier {

		@Override
		public boolean verify(final JComponent input) {

			final SSBooleanField tf = (SSBooleanField) input;
			final boolean selected = tf.isSelected();

			setBackground(java.awt.Color.WHITE);

			// if not linked to a db field, returns.
			if ((SSBooleanField.this.getColumnName() == null) || (SSBooleanField.this.rowset == null)) {
				return true;
			}

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
			} catch (final java.sql.SQLException se) {
				logger.error(getColumnForLog() + ": SQL Exception.", se);
			} catch (final java.lang.NullPointerException np) {
				logger.error(getColumnForLog() + ": Null Pointer Exception.", np);
			}
			return true;
		}
	}

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -856226927518717477L;
	protected String columnName = null;
	protected int colType = -99;
	protected SSRowSet rowset = null;

	private SSDataNavigator navigator = null;
	
	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/** Creates a new instance of SSBooleanField */
	public SSBooleanField() {
		super();

		final Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		final Set<AWTKeyStroke> newForwardKeys = new HashSet<>(forwardKeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);

		final Set<AWTKeyStroke> backwardKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		final Set<AWTKeyStroke> newBackwardKeys = new HashSet<>(backwardKeys);
		newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_MASK));
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newBackwardKeys);

		this.addKeyListener(this);

		this.setInputVerifier(new internalVerifier());
	}
	
	/**
	 * Returns the bound column name in square brackets.
	 * 
	 * @return the boundColumnName in square brackets
	 */
	public String getColumnForLog() {
		return "[" + columnName + "]";
	}

	/**
	 * Binds this component to the specified column in the given rowset.
	 */
	private void bind() {

		if (this.columnName == null) {
			return;
		}
		if (this.rowset == null) {
			return;
		}

		try {
			this.colType = this.rowset.getColumnType(this.columnName);
		} catch (final java.sql.SQLException se) {
			logger.error(getColumnForLog() + ": SQL Exception.", se);
		}
		this.rowset.addRowSetListener(this);
		DbToFm();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sql.RowSetListener#cursorMoved(javax.sql.RowSetEvent)
	 */
	@Override
	public void cursorMoved(final javax.sql.RowSetEvent _event) {
		DbToFm();
	}

	/**
	 * Fetches the value from rowset and updates the field accordingly
	 */
	private void DbToFm() {

		try {
			if (this.rowset.getRow() == 0) {
				return;
			}

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
				if (this.rowset.getInt(this.columnName) == 1) {
					this.setSelected(true);
				} else {
					this.setSelected(false);
				}
				break;

			default:
				break;
			}
		} catch (final java.sql.SQLException se) {
			logger.error(getColumnForLog() + ": SQL Exception.", se);
		}
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
	 * Returns the SSDataNavigator being used
	 * 
	 * @return returns the SSDataNavigator being used
	 * @deprecated Use {@link #getSSDataNavigator()} instead.
	 **/
	@Deprecated
	public SSDataNavigator getNavigator() {
		return this.getSSDataNavigator();
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
	 * Returns the SSDataNavigator being used
	 * 
	 * @return returns the SSDataNavigator being used
	 */
	public SSDataNavigator getSSDataNavigator() {
		return this.navigator;
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
			logger.debug(getColumnForLog() + ": F5 = PROCESS");
			this.navigator.doCommitButtonClick();
		}

		if (_event.getKeyCode() == KeyEvent.VK_F6) {
			logger.debug(getColumnForLog() + ": F6 = DELETE");
			this.navigator.doDeleteButtonClick();
		}

		if (_event.getKeyCode() == KeyEvent.VK_F8) {
			logger.debug(getColumnForLog() + ": F8");
			this.navigator.doUndoButtonClick();
		}

		if (_event.getKeyCode() == KeyEvent.VK_END) {
			logger.debug(getColumnForLog() + ": END");
		}

		if (_event.getKeyCode() == KeyEvent.VK_DELETE) {
			logger.debug(getColumnForLog() + ": DELETE");
		}

		if (_event.getKeyCode() == KeyEvent.VK_HOME) {
			logger.debug(getColumnForLog() + ": HOME");
		}

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
	 * @see javax.sql.RowSetListener#rowChanged(javax.sql.RowSetEvent)
	 */
	@Override
	public void rowChanged(final javax.sql.RowSetEvent _event) {
		// do nothing
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
	 * Sets the SSDataNavigator used for navigating the SSRowSet to which this
	 * component is bound. <b><i>The functionality for this is not yet
	 * finalized so try to avoid using this </i></b>
	 * 
	 * @param _navigator - SSDataNavigator used for navigating the SSRowSet to which
	 *                   this component is bound to
	 * @deprecated Use {@link #setSSDataNavigator(SSDataNavigator _navigator)}
	 *             instead.
	 **/
	@Deprecated
	public void setNavigator(final SSDataNavigator _navigator) {
		this.setSSDataNavigator(_navigator);
	}

	/**
	 * Sets the SSRowSet to which the component is bound
	 * 
	 * @param _rowset SSRowSet to which the component is bound
	 * @deprecated Use {@link #setSSRowSet(SSRowSet _rowset)} instead.
	 */
	@Deprecated
	public void setRowSet(final SSRowSet _rowset) {
		this.setSSRowSet(_rowset);
	}

	/**
	 * Sets the SSDataNavigator used for navigating the SSRowSet to which this
	 * component is bound to. <b>The functionality for this is not yet
	 * finalized so try to avoid using this </b>
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
	 * Sets the SSRowSet to which the component is bound.
	 * 
	 * @param _rowset SSRowSet to which the component is bound.
	 */
	public void setSSRowSet(final SSRowSet _rowset) {
		this.rowset = _rowset;
		bind();
	}
}

