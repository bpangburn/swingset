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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.sql.RowSetListener;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.datasources.SSRowSet;

/**
 * SSMemoField.java
 * <p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * <p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * <p>
 * Used to link a JTextArea to a text (generally multi-line) column in a
 * database.
 * <p>
 * Other than some function key handling, which is likely outside the scope of
 * SwingSet and should be customized at the application level, this class appears to 
 * mostly duplicate SSTextArea. It may be desirable to add an InputVerifier to 
 * SSTextArea.
 * <p>
 * It does not extend SSFormattedText field like the other classes in this package.
 * <p>
 * @deprecated Starting in 4.0.0+ use {@link com.nqadmin.swingset.SSTextArea} instead.
 */
@Deprecated
public class SSMemoField extends JTextArea implements RowSetListener, KeyListener, FocusListener {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -7984808092295218942L;
	private java.awt.Color std_color = null;
	protected String columnName = null;
	protected int colType = -99;
	protected SSRowSet rowset = null;
	private SSDataNavigator navigator = null;
	
	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * Creates a new instance of SSBooleanField
	 */
	public SSMemoField() {
		super();

		this.setLineWrap(true);
		this.setWrapStyleWord(true);

		Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newForwardKeys = new HashSet<>(forwardKeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);

		Set<AWTKeyStroke> backwardKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newBackwardKeys = new HashSet<>(backwardKeys);
		newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_MASK));
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newBackwardKeys);

		addKeyListener(this);
		addFocusListener(this);
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
	 * Returns the column name to which the component is bound to
	 * 
	 * @return - returns the column name to which the component is bound to
	 */
	public String getColumnName() {
		return this.columnName;
	}

	/**
	 * Sets the column name to which the component should be bound to
	 * 
	 * @param _columnName - column name to which the component will be bound to
	 */
	public void setColumnName(final String _columnName) {
		this.columnName = _columnName;
		bind();
	}

	/**
	 * Sets the SSRowSet object to be used to get/set the value of the bound column
	 * 
	 * @param _rowset - SSRowSet object to be used to get/set the value of the bound
	 *                column
	 * @deprecated renamed setSSRowSet()
	 * @see #setSSRowSet(SSRowSet)
	 */
	@Deprecated
	public void setRowSet(final SSRowSet _rowset) {
		setSSRowSet(_rowset);
	}

	/**
	 * SSRowSet object being used to get/set the bound column value
	 * 
	 * @return - returns the SSRowSet object being used to get/set the bound column
	 *         value
	 * @deprecated renamed getSSRowSet()
	 * @see #getSSRowSet()
	 **/
	@Deprecated
	public SSRowSet getRowSet() {
		return getSSRowSet();
	}

	/**
	 * Sets the SSRowSet object to be used to get/set the value of the bound column
	 * 
	 * @param _rowset - SSRowSet object to be used to get/set the value of the bound
	 *                column
	 */
	public void setSSRowSet(final SSRowSet _rowset) {
		this.rowset = _rowset;
		bind();
	}

	/**
	 * SSRowSet object being used to get/set the bound column value
	 * 
	 * @return - returns the SSRowSet object being used to get/set the bound column
	 *         value
	 */
	public SSRowSet getSSRowSet() {
		return this.rowset;
	}

	/**
	 * Sets the SSDataNavigator being used to navigate the SSRowSet This is needed
	 * only if you want to include the function keys as short cuts to perform
	 * operations on the DataNavigator like saving the current row/ undo changes/
	 * delete current row. <b><i>The functionality for this is not yet
	 * finalized so try to avoid using this </i></b>
	 * 
	 * @param _navigator - SSDataNavigator being used to navigate the SSRowSet
	 * @deprecated renamed setSSDataNavigator()
	 * @see #setSSDataNavigator(SSDataNavigator)
	 */
	@Deprecated
	public void setNavigator(final SSDataNavigator _navigator) {
		this.setSSDataNavigator(_navigator);
	}

	/**
	 * Returns the SSDataNavigator object being used.
	 * 
	 * @return returns the SSDataNavigator object being used.
	 * @deprecated renamed getSSDataNavigator()
	 * @see #getSSDataNavigator()
	 **/
	@Deprecated
	public SSDataNavigator getNavigator() {
		return this.getSSDataNavigator();
	}

	/**
	 * Sets the SSDataNavigator being used to navigate the SSRowSet This is needed
	 * only if you want to include the function keys as short cuts to perform
	 * operations on the DataNavigator like saving the current row/ undo changes/
	 * delete current row. <b><i>The functionality for this is not yet
	 * finalized so try to avoid using this </i></b>
	 * 
	 * @param _navigator - SSDataNavigator being used to navigate the SSRowSet
	 */
	public void setSSDataNavigator(final SSDataNavigator _navigator) {
		this.navigator = _navigator;
		setSSRowSet(_navigator.getSSRowSet());
		bind();
	}

	/**
	 * Returns the SSDataNavigator object being used.
	 * 
	 * @return returns the SSDataNavigator object being used.
	 */
	public SSDataNavigator getSSDataNavigator() {
		return this.navigator;
	}

	private void DbToFm() {

		try {

			if (this.rowset.getRow() == 0) {
				return;
			}

			switch (this.colType) {

			case java.sql.Types.VARCHAR:
			case java.sql.Types.LONGVARCHAR:
			case java.sql.Types.CHAR:
				this.setText(this.rowset.getString(this.columnName));
				break;

			default:
				break;
			}
		} catch (java.sql.SQLException sqe) {
			logger.error(getColumnForLog() + ": SQL Exception.", sqe);
			this.setText("");
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

	private void bind() {

		if (this.columnName == null) {
			return;
		}
		if (this.rowset == null) {
			return;
		}

		try {
			this.colType = this.rowset.getColumnType(this.columnName);
		} catch (java.sql.SQLException sqe) {
			logger.error(getColumnForLog() + ": SQL Exception.", sqe);
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
	 * Catch severals keys, to implement some forms functionality (To be done).
	 */
	@Override
	public void keyPressed(final KeyEvent _event) {

		if (_event.getKeyCode() == KeyEvent.VK_F1) {
			// showHelper(e);
		}

		if (_event.getKeyCode() == KeyEvent.VK_F2) {
			// do nothing
		}

		if (_event.getKeyCode() == KeyEvent.VK_F3) {
			logger.debug(getColumnForLog() + ": F3");
			// calculator = new javax.swing.JPopupMenu();
			// calculator.add(new com.nqadmin.swingset.formatting.utils.JCalculator());
			// JFormattedTextField ob = (JFormattedTextField)(_event.getSource());
			// java.awt.Dimension d = ob.getSize();
			// calculator.show(ob, 0, d.height);

			// ((Component)e.getSource()).transferFocus();
		}

		if (_event.getKeyCode() == KeyEvent.VK_F4) {
			logger.debug(getColumnForLog() + ": F4");
			// ((Component)e.getSource()).transferFocus();
		}

		if (_event.getKeyCode() == KeyEvent.VK_F5) {
			logger.debug(getColumnForLog() + ": F5 = PROCESS");
			if (this.navigator.updatePresentRow() == true) {
				logger.info(getColumnForLog() + ": Update successful.");
			}
		}

		if (_event.getKeyCode() == KeyEvent.VK_F6) {
			logger.debug(getColumnForLog() + ": F6 = DELETE");
			this.navigator.doDeleteButtonClick();
		}

		if (_event.getKeyCode() == KeyEvent.VK_F8) {
			logger.debug(getColumnForLog() + ": F8");
			// ((Component)e.getSource()).transferFocus();
			this.navigator.doUndoButtonClick();
		}

		if (_event.getKeyCode() == KeyEvent.VK_END) {
			logger.debug(getColumnForLog() + ": END");
			// ((Component)e.getSource()).transferFocus();
		}

		if (_event.getKeyCode() == KeyEvent.VK_DELETE) {
			logger.debug(getColumnForLog() + ": DELETE");
			// ((Component)e.getSource()).transferFocus();
		}

		if (_event.getKeyCode() == KeyEvent.VK_HOME) {
			logger.debug(getColumnForLog() + ": HOME");
			// ((Component)e.getSource()).transferFocus();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(final FocusEvent _event) {
		/**
		 * some code to highlight the component with the focus
		 * <p>
		 */
		setBackground(this.std_color);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(final FocusEvent _event) {

		/**
		 * some code to highlight the component with the focus
		 * <p>
		 */
		java.awt.Color col = new java.awt.Color(204, 255, 255);
		this.std_color = getBackground();
		setBackground(col);

		/**
		 * This is a bug workaround see :
		 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4740914
		 * <p>
		 */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				selectAll();
			}
		});
	}

	/**
	 * This method should implements validation AND, most important for our purposes
	 * implements actual rowset fields updates.
	 * <p>
	 */

	class internalVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {

			String aux = null;
			boolean passed = true;

			SSMemoField tf = (SSMemoField) input;
			aux = tf.getText();

			if (passed == true) {

				setBackground(java.awt.Color.WHITE);

				// if not linked to a db field, returns.
				if ((SSMemoField.this.columnName == null) || (SSMemoField.this.rowset == null)) {
					return true;
				}

				try {
					SSMemoField.this.rowset.removeRowSetListener(tf);

					switch (SSMemoField.this.colType) {

					case java.sql.Types.VARCHAR:// -7
					case java.sql.Types.LONGVARCHAR:// -7
					case java.sql.Types.CHAR:// -7
						SSMemoField.this.rowset.updateString(SSMemoField.this.columnName, aux);
						break;

					default:
						break;
					}
					SSMemoField.this.rowset.addRowSetListener(tf);
				} catch (java.sql.SQLException se) {
					logger.error(getColumnForLog() + ": SQL Exception.", se);
					tf.setText("");
				} catch (java.lang.NullPointerException np) {
					logger.error(getColumnForLog() + ": Null Pointer Exception.", np);
					tf.setText("");
				}
				return true;
			}
			/*
			 * Validation fails.
			 *
			 */

			setBackground(java.awt.Color.RED);
			return false;
		}
	}
}

