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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;

import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;
//import com.nqadmin.swingset.formatting.helpers.SSFormattedComboBoxEditor;
//import com.nqadmin.swingset.formatting.helpers.SSFormattedComboBoxModel;

/**
 * SSFormattedTextField.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * SSFormattedTextField extends the JFormattedTextField.
 */
// TODO Consider adding back context help and calculators via popups. See 2020-01-07 revisions or earlier.
// TODO Consider modifying colors/fonts/bold in child classes using custom document listeners. See http://www.java2s.com/Tutorials/Java/Swing_How_to/JFormattedTextField/Highlight_JFormattedTextField_for_larger_value.htm
// TODO Add JDatePicker support or something similar: https://www.codejava.net/java-se/swing/how-to-use-jdatepicker-to-display-calendar-component

//public class SSFormattedTextField extends JFormattedTextField implements RowSetListener, KeyListener, MouseListener, BeanContextProxy, FocusListener {

public class SSFormattedTextField extends JFormattedTextField
		implements FocusListener, SSComponentInterface {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 5349618425984728006L;

//	private BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport();
//
//	private JPopupMenu menu = null;
//	private JPopupMenu calculator = null;
//	private JPopupMenu helper = null;

	private java.awt.Color stdColor = null;
//	private String columnName = null;
//	private int colType = -99;
//	private SSDataNavigator navigator = null;
//	private SSRowSet rowset = null;

//    private SSFormattedComboBox combo = null;
	
    /**
     * Common fields shared across SwingSet components
     */
    protected SSCommon ssCommon;
    
    
//    // ANOTHER OPTION FOR HANDLING FOCUS GAINED/LOST
//    // see http://www.java2s.com/Tutorials/Java/Swing_How_to/JFormattedTextField/Handle_focus_lost_and_document_event_for_JFormattedTextField.htm
//    private JFormattedTextField init(JFormattedTextField jtf) {
//        jtf.setValue(0);
//        jtf.addFocusListener(new FocusAdapter() {
//          @Override
//          public void focusLost(FocusEvent e) {
//            Number v1 = (Number) a.getValue();
//            Number v2 = (Number) b.getValue();
//            sum.setValue(v1.longValue() + v2.longValue());
//          }
//        });
//        return jtf;
//      }

//	/**
//	 * Holds value of property nullable.
//	 * 
//	 * Changing to use allowNull in SSCommon
//	 */
//	private boolean nullable = true;

//	/**
//	 * Utility field used by bound properties.
//	 */
//	// private java.beans.PropertyChangeSupport propertyChangeSupport = new
//	// java.beans.PropertyChangeSupport(this);

	/**
	 * Creates a new instance of SSFormattedTextField
	 */
	public SSFormattedTextField() {
		
    	super();
    	setSSCommon(new SSCommon(this));
    	// SSCommon constructor calls init()
    	
//		super();
//		this.setFont(new Font(this.getFont().getName(), Font.BOLD, 11));
//		this.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
//
//		Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
//		Set<AWTKeyStroke> newForwardKeys = new HashSet<>(forwardKeys);
//		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
//		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK));
//		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
//
//		Set<AWTKeyStroke> backwardKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
//		Set<AWTKeyStroke> newBackwardKeys = new HashSet<>(backwardKeys);
//		newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_MASK));
//		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newBackwardKeys);
//
//		/*
//		 * add this as a self KeyListener *
//		 */
//		addKeyListener(this);
//
//		/*
//		 * add this as a self FocusListener
//		 *
//		 */
//		addFocusListener(this);
//
//		/*
//		 * add this as a self MouseListener
//		 *
//		 */
//		addMouseListener(this);
//
//		/*
//		 *
//		 */
//		this.menu = new JPopupMenu();
//		this.menu.add("Option 1");
//		this.menu.add("Option 2");
//		this.menu.add("Option 3");
//
//		/*
//		 * set InputVerifier. Rowset's updates are handled by this class. Is the
//		 * preferred method instead of focus change.
//		 *
//		 */
//		setInputVerifier(new internalVerifier());
	}

	/**
	 * Creates a new instance of SSFormattedTextField
	 * 
	 * @param _factory formatter to use for text field
	 */
	public SSFormattedTextField(final javax.swing.JFormattedTextField.AbstractFormatterFactory _factory) {
    	super();
    	setSSCommon(new SSCommon(this));
    	// SSCommon constructor calls init()
		this.setFormatterFactory(_factory);
	}

//	/**
//	 * Sets the column name to which the component should be bound to
//	 * 
//	 * @param _columnName - column name to which the component will be bound to
//	 */
//	public void setColumnName(final String _columnName) {
//		this.columnName = _columnName;
//		bind();
//	}

//	/**
//	 * Returns the column name to which the component is bound to
//	 * 
//	 * @return - returns the column name to which the component is bound to
//	 */
//	public String getColumnName() {
//		return this.columnName;
//	}

//	/**
//	 * Sets the SSRowSet object to be used to get/set the value of the bound column
//	 * 
//	 * @param _rowset - SSRowSet object to be used to get/set the value of the bound
//	 *                column
//	 * @deprecated Use {@link #setSSRowSet(SSRowSet _rowset)} instead.
//	 */
//	@Deprecated
//	public void setRowSet(final SSRowSet _rowset) {
//		this.rowset = _rowset;
//		bind();
//	}

//	/**
//	 * Sets the SSRowSet object to be used to get/set the value of the bound column
//	 * 
//	 * @param _rowset - SSRowSet object to be used to get/set the value of the bound
//	 *                column
//	 */
//	public void setSSRowSet(SSRowSet _rowset) {
//		this.rowset = _rowset;
//		bind();
//	}

//	/**
//	 * SSRowSet object being used to get/set the bound column value
//	 * 
//	 * @return - returns the SSRowSet object being used to get/set the bound column
//	 *         value
//	 */
//	public SSRowSet getSSRowSet() {
//		return this.rowset;
//	}

//	/**
//	 * Sets the SSDataNavigator being used to navigate the SSRowSet This is needed
//	 * only if you want to include the function keys as short cuts to perform
//	 * operations on the DataNavigator like saving the current row/ undo changes/
//	 * delete current row. <b><i>The functionality for this is not yet
//	 * finalized so try to avoid using this </i></b>
//	 * 
//	 * @param _navigator - SSDataNavigator being used to navigate the SSRowSet
//	 * @deprecated Use {@link #setSSDataNavigator(SSDataNavigator _navigator)}
//	 *             instead.
//	 */
//	@Deprecated
//	public void setNavigator(final SSDataNavigator _navigator) {
//		this.setSSDataNavigator(_navigator);
//	}

//	/**
//	 * Returns the SSDataNavigator object being used.
//	 * 
//	 * @return returns the SSDataNavigator object being used.
//	 * @deprecated Use {@link #getSSDataNavigator()} instead.
//	 **/
//	@Deprecated
//	public SSDataNavigator getNavigator() {
//		return this.getSSDataNavigator();
//	}

//	/**
//	 * Sets the SSDataNavigator being used to navigate the SSRowSet This is needed
//	 * only if you want to include the function keys as short cuts to perform
//	 * operations on the DataNavigator like saving the current row/ undo changes/
//	 * delete current row. <b><i>The functionality for this is not yet
//	 * finalized so try to avoid using this </i></b>
//	 * 
//	 * @param _navigator - SSDataNavigator being used to navigate the SSRowSet
//	 */
//	public void setSSDataNavigator(final SSDataNavigator _navigator) {
//		this.navigator = _navigator;
//		setSSRowSet(_navigator.getSSRowSet());
//		bind();
//	}

//	/**
//	 * Returns the SSDataNavigator object being used.
//	 * 
//	 * @return returns the SSDataNavigator object being used.
//	 */
//	public SSDataNavigator getSSDataNavigator() {
//		return this.navigator;
//	}

//	/**
//	 * Sets the SSRowSet and column name to which the component is to be bound.
//	 * 
//	 * @param _sSRowSet   datasource to be used.
//	 * @param _columnName Name of the column to which this check box should be bound
//	 */
//	public void bind(final SSRowSet _sSRowSet, final String _columnName) {
//		this.rowset = _sSRowSet;
//		this.columnName = _columnName;
//		bind();
//	}

//	/**
//	 * Binds the components to the specified column in the given rowset.
//	 */
//	private void bind() {
//
//		if (this.columnName == null || this.rowset == null)
//			return;
//
//		try {
//			this.colType = this.rowset.getColumnType(this.columnName);
//		} catch (java.sql.SQLException sqe) {
//			System.out.println("bind error = " + sqe);
//		}
//		this.rowset.addRowSetListener(this);
//		DbToFm("bind");
//	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see javax.sql.RowSetListener#rowSetChanged(javax.sql.RowSetEvent)
//	 */
//	@Override
//	public void rowSetChanged(final javax.sql.RowSetEvent _event) {
//		// System.out.println("rowSetChanged" + event.getSource());
//	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see javax.sql.RowSetListener#rowChanged(javax.sql.RowSetEvent)
//	 */
//	@Override
//	public void rowChanged(final javax.sql.RowSetEvent _event) {
//		// System.out.println("rowChanged " + event.getSource());
//	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see javax.sql.RowSetListener#cursorMoved(javax.sql.RowSetEvent)
//	 */
//	@Override
//	public void cursorMoved(final javax.sql.RowSetEvent _event) {
//		// System.out.println("cursorMoved " + event.getSource());
//		DbToFm("cursorMoved");
//	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
//	 */
//	@Override
//	public void keyTyped(KeyEvent _event) {
//		// do nothing
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
//	 */
//	@Override
//	public void keyReleased(KeyEvent _event) {
//		// do nothing
//	}
//
//	/**
//	 * Catch severals keys, to implement some forms functionality (To be don_event).
//	 * This is to mimic console legacy systems behavior.
//	 *
//	 */
//	@Override
//	public void keyPressed(KeyEvent _event) {
//
//		if (_event.getKeyCode() == KeyEvent.VK_F1) {
//			showHelper(_event);
//		}
//
//		if (_event.getKeyCode() == KeyEvent.VK_F2) {
//			// do nothing
//		}
//
//		if (_event.getKeyCode() == KeyEvent.VK_F3) {
//			this.calculator = new javax.swing.JPopupMenu();
//			// calculator.add(new com.nqadmin.swingset.formatting.utils.JCalculator());
//			JFormattedTextField ob = (JFormattedTextField) (_event.getSource());
//			java.awt.Dimension d = ob.getSize();
//			this.calculator.show(ob, 0, d.height);
//		}
//
//		if (_event.getKeyCode() == KeyEvent.VK_F4) {
//			System.out.println("F4 ");
//		}
//
//		if (_event.getKeyCode() == KeyEvent.VK_F5) {
//			System.out.println("F5 = COMMIT");
//			this.navigator.doCommitButtonClick();
//		}
//
//		if (_event.getKeyCode() == KeyEvent.VK_F6) {
//			System.out.println("F6 = DELETE");
//			this.navigator.doDeleteButtonClick();
//		}
//
//		if (_event.getKeyCode() == KeyEvent.VK_F8) {
//			System.out.println("F8 = UNDO");
//			this.navigator.doUndoButtonClick();
//		}
//
//		if (_event.getKeyCode() == KeyEvent.VK_END) {
//			System.out.println("END ");
//		}
//
//		if (_event.getKeyCode() == KeyEvent.VK_DELETE) {
//			System.out.println("DELETE ");
//		}
//
//		if (_event.getKeyCode() == KeyEvent.VK_HOME) {
//			System.out.println("HOME ");
//		}
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(final FocusEvent _event) {
		/**
		 * some code to highlight the component with the focus
		 */
		setBackground(this.stdColor);
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
		 *
		 */
		java.awt.Color col = new java.awt.Color(204, 255, 255);
		this.stdColor = getBackground();
		setBackground(col);

		/**
		 * This is a bug workaround see :
		 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4740914
		 * 
		 * 2020-06-02: Per the thread above this isn't really a bug, but a feature of SSFormattedTextField
		 *
		 */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				selectAll();
			}
		});
	}

//	
//	/**
//	 * This method perform the actual data transfer from rowset to this object Value
//	 * field. depending on the column Type.
//	 * @param texto this parameter is never used and method should probably be re-written
//	 */
////TODO: remove "texto" as parameter and depreciate this method
//	private void DbToFm(final String texto) {
//		Object oValue = null;
//		Object nValue = null;
//		int nrow = 0;
//
//		try {
//			nrow = this.rowset.getRow();
//		} catch (SQLException s3) {
//			System.out.println(s3);
//		}
//
//		if (nrow == 0)
//			return;
//
//		try {
//			// IF THE COLUMN VALUE IS NULL SET THE FIELD TO NULL AND RETURN
//			if (this.rowset.getObject(this.columnName) == null) {
//				super.setValue(null);
//				return;
//			}
//
//			switch (this.colType) {
//
//			case java.sql.Types.ARRAY:// 2003A
//				System.out.println("ARRAY not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.BINARY:// -2
//				System.out.println("BINARY not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.BIT:// -7
//				// System.out.println("BIT implemented as Boolean --> " + columnName);
//				nValue = new Boolean(this.rowset.getBoolean(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.BOOLEAN:// 16
//				// System.out.println("BOOLEAN implemented as Boolean --> " + columnName);
//				nValue = new Boolean(this.rowset.getBoolean(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.BLOB:// 2004
//				System.out.println("BLOB not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.CLOB:// 2005
//				System.out.println("CLOB not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.DATALINK:// 70
//				System.out.println("DATALINK not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.DATE:// 91
//				// System.out.println("DATE implemented as java.util.Date --> " + columnName);
//				Date date = this.rowset.getDate(this.columnName);
//				if (date != null) {
//					nValue = new java.util.Date(this.rowset.getDate(this.columnName).getTime());
//					super.setValue(nValue);
//				} else {
//					super.setValue(null);
//				}
//				break;
//
//			case java.sql.Types.DECIMAL:// 3
//				// System.out.println("DECIMAL implemented as BigDecimal --> " + columnName);
//				nValue = new java.math.BigDecimal(this.rowset.getDouble(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.DISTINCT:// 2001
//				System.out.println("DISTINCT not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.DOUBLE:// 8
//				// System.out.println("DOUBLE implemented as Double --> " + columnName);
//				nValue = new Double(this.rowset.getDouble(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.REAL: // 7
//				// System.out.println("REAL implemented as Float --> " + columnName);
//				nValue = new Float(this.rowset.getFloat(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.FLOAT:// 6
//				// System.out.println("FLOAT implemented as Float --> " + columnName);
//				nValue = new Float(this.rowset.getFloat(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.TINYINT:// -6
//				// System.out.println("TINYINT implemented as Integer --> " + columnName);
//				nValue = new Integer(this.rowset.getInt(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.SMALLINT:// 5
//				// System.out.println("SMALLINT implemented as Integer --> " + columnName);
//				nValue = new Integer(this.rowset.getInt(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.INTEGER:// 4
//				// System.out.println("INTEGER implemented as Integer --> " + columnName);
//				nValue = new Integer(this.rowset.getInt(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.BIGINT:// -5
//				// System.out.println("BIGINT implemented as Integer --> " + columnName);
//				nValue = new Integer(this.rowset.getInt(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.JAVA_OBJECT:// 2000
//				System.out.println("JAVA_OBJECT not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.LONGVARBINARY:// -4
//				System.out.println("LONGVARBINARY not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.VARBINARY:// -3
//				System.out.println("VARBINARY not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.VARCHAR://
//				// System.out.println("VARCHAR implemented as String --> " + columnName);
//				nValue = this.rowset.getString(this.columnName);
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.LONGVARCHAR:// -1
//				// System.out.println("LONGVARCHAR implemented as String --> " + columnName);
//				nValue = this.rowset.getString(this.columnName);
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.CHAR:// 1
//				// System.out.println("CHAR implemented as String --> " + columnName);
//				nValue = this.rowset.getString(this.columnName);
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.NULL:// 0
//				System.out.println("NULL not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.NUMERIC:// 2
//				// System.out.println("NUMERIC implemented as BigDecimal --> " + columnName);
//				nValue = new java.math.BigDecimal(this.rowset.getDouble(this.columnName));
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.OTHER:// 1111
//				System.out.println("OTHER not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.REF:// 2006
//				System.out.println("REF not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.STRUCT:// 2002
//				System.out.println("STRUCT not implemented : " + this.columnName);
//				break;
//
//			case java.sql.Types.TIME:// 92
//				// System.out.println("TIME implemented as java.util.Date --> " + columnName);
//				// nValue = new java.util.Date(rowset.getTime(columnName).getTime());
//				nValue = this.rowset.getTime(this.columnName);
//				super.setValue(nValue);
//				break;
//
//			case java.sql.Types.TIMESTAMP:// 93
//				// System.out.println("TIMESTAMP implemented as java.util.Date --> " +
//				// columnName);
////                    nValue = new java.util.Date(rowset.getTimestamp(columnName).getTime());
//				nValue = this.rowset.getTimestamp(this.columnName);
//				super.setValue(nValue);
//				break;
//
//			default:
//				System.out.println("UNKNOWN Type not implemented (" + this.colType + ")");
//				break;
//			}
//		} catch (java.sql.SQLException sqe) {
//			System.out.println("Error in DbToFm() = " + sqe);
//			sqe.printStackTrace();
//			super.setValue(null);
//		}
//
//		if ((nValue instanceof Double && ((Double) nValue).doubleValue() < 0.0)
//				|| (nValue instanceof Float && ((Float) nValue).floatValue() < 0.0)
//				|| (nValue instanceof Long && ((Long) nValue).intValue() < 0)
//				|| (nValue instanceof Integer && ((Integer) nValue).longValue() < 0)) {
//			this.setForeground(Color.RED);
//		} else {
//			this.setForeground(Color.BLACK);
//		}
//
//		this.firePropertyChange("value", oValue, nValue);
//		oValue = nValue;
//	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
//	 */
//	@Override
//	public void mouseExited(final MouseEvent _event) {
//		// System.out.println("mouseExited");
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
//	 */
//	@Override
//	public void mouseEntered(final MouseEvent _event) {
//		// System.out.println("mouseEntered");
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
//	 */
//	@Override
//	public void mouseClicked(final MouseEvent _event) {
//		// System.out.println("mouseClicked");
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
//	 */
//	@Override
//	public void mousePressed(final MouseEvent _event) {
//		if (_event.isPopupTrigger()) {
//			this.menu.show(_event.getComponent(), _event.getX(), _event.getY());
//		}
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
//	 */
//	@Override
//	public void mouseReleased(final MouseEvent _event) {
//		if (_event.isPopupTrigger()) {
//			this.menu.show(_event.getComponent(), _event.getX(), _event.getY());
//		}
//	}
//
//	/**
//	 * Sets the HelperPopup to be used.
//	 * 
//	 * @param _helper - HelperPopup to be used.
//	 */
//	public void setHelper(final JPopupMenu _helper) {
//		this.helper = _helper;
//
//		if (_helper instanceof HelperPopup)
//			((HelperPopup) this.helper).setTarget(this);
//
//		if (_helper instanceof RowSetHelperPopup)
//			((RowSetHelperPopup) this.helper).setTarget(this);
//
//	}
//
//	/**
//	 * Displays the HelperPopup screen.
//	 * 
//	 * @param _event - the key event which triggered the helper popup
//	 */
//	public void showHelper(final KeyEvent _event) {
//		if (this.helper == null)
//			return;
//
//		JFormattedTextField ob = (JFormattedTextField) (_event.getSource());
//		java.awt.Dimension d = ob.getSize();
//		this.helper.requestFocusInWindow();
//		this.helper.show(ob, 0, d.height);
//	}

	/**
	 * This method should implements validation AND, most important for our purposes
	 * implements actual rowset's fields updates.
	 *
	 */

	class internalVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {

			SSFormattedTextField tf = null;
			Object aux = null;
			// boolean passed = true;

			/**
			 * field to be validated and updated
			 */

			if (input instanceof SSFormattedTextField) {
				tf = (SSFormattedTextField) input;
				// } else if (input instanceof SSFormattedComboBoxEditor){
				// tf = ((SSFormattedComboBoxEditor)input).getEditorField() ;
				// } else if (input instanceof SSFormattedComboBox){
				// tf = ( ( (SSFormattedComboBoxEditor)
				// ((SSFormattedComboBox)input).getEditor())).getEditorField();
			}

			if (tf != null) {
				try {
					tf.commitEdit();
				} catch (java.text.ParseException pe) {
					getLogger().error(getColumnForLog() + ": Parse Exception at " + pe.getErrorOffset() + ".", pe);
					tf.setValue(null);
					setBackground(java.awt.Color.RED);
					return false;
				}

				aux = tf.getValue();
			}

			return updateFieldValue(aux);

		}
	}

//	/**
//	 * This function has been deprecated, use setValue to set the value in database.
//	 * This is done to reduce the confusion of when to use setValue and when to use
//	 * updateValue. Before this change setValue is a function in JFormattedText
//	 * field and it would not set the value in database, so we have overridden this
//	 * function in this class to update database and then update the display.
//	 * 
//	 * Sets the value of the field to the specified value
//	 * 
//	 * @param _value - The value to be set for this component (this will also update
//	 *               the underlying column value)
//	 * @return returns true if update is successful else false
//	 * @deprecated Use {@link #setValue(Object _value)} instead.
//	 */
//	@Deprecated
//	public boolean updateValue(final Object _value) {
//		if (updateFieldValue(_value)) {
//			super.setValue(_value);
//			return true;
//		}
//		return false;
//	}

//	/**
//	 * Sets the value of the field to the specified value
//	 * 
//	 * @param _value - The value to be set for this component (this will also update
//	 *               the underlying column value)
//	 */
//	@Override
//	public void setValue(final Object _value) {
//		if (updateFieldValue(_value)) {
//			super.setValue(_value);
//		}
//	}

	/**
	 * Updates the value of the component
	 * 
	 * @param _aux - value with which the component should be updated
	 * @return returns true upon successful update else false
	 */
	@Deprecated
	protected boolean updateFieldValue(final Object _aux) {
		
		// Very similar to updateRowSet()
		
		// Converting to string and calling setBoundColumnText()
		String stringValue = String.valueOf(_aux);
		
		setBoundColumnText(stringValue);
		return true;

//		/**
//		 * field to be validated and updated
//		 */
//
//		SSFormattedTextField tf = this;
//
//		boolean passed = validateField(_aux);
//
//		if (passed == true) {
//
//			setBackground(java.awt.Color.WHITE);
//
//			if (this.columnName == null)
//				return true;
//			if (this.colType == -99)
//				return true;
//			if (this.rowset == null)
//				return true;
//
//			try {
//
//				this.rowset.removeRowSetListener(tf);
//				if (_aux == null) {
//					this.rowset.updateNull(this.columnName);
//					this.rowset.addRowSetListener(tf);
//					tf.firePropertyChange("value", null, _aux);
//					return true;
//				}
//
//				updateRowSet(_aux);
//
//				// SHOW NEGATIVE NUMBERS IN RED
//				if ((_aux instanceof BigDecimal && ((Double) _aux).doubleValue() < 0.0)
//						|| (_aux instanceof Double && ((Double) _aux).doubleValue() < 0.0)
//						|| (_aux instanceof Float && ((Float) _aux).floatValue() < 0.0)
//						|| (_aux instanceof Integer && ((Integer) _aux).intValue() < 0)
//						|| (_aux instanceof Long && ((Long) _aux).longValue() < 0)) {
//					tf.setForeground(Color.RED);
//				} else {
//					tf.setForeground(Color.BLACK);
//				}
//
//			} catch (java.sql.SQLException se) {
//				System.out.println("---> SQLException -----------> " + se);
//				super.setValue(null);
//			} catch (java.lang.NullPointerException np) {
//				System.out.println("<---> NullPointerException <---> " + np + " columnName : " + this.columnName);
//				super.setValue(null);
//			}
//
//			this.rowset.addRowSetListener(tf);
//			System.out.println("inputVerifier : " + this.columnName + " nValue = " + _aux);
//			tf.firePropertyChange("value", null, _aux);
//			return true;
//
//		}
//		setBackground(java.awt.Color.RED);
//		return false;

	}

	
	
//	/**
//	 * Updates the bound column in the rowset with the specified value
//	 * 
//	 * @param _aux - value with which the rowset column has to be updated
//	 * @throws SQLException SQLException
//	 */
//	private void updateRowSet(final Object _aux) throws SQLException {
//		
//		// THIS SHOULD ALL BE HANDLED BY SSRowSet.updateColumnText()
//		switch (this.colType) {
//
//		case java.sql.Types.ARRAY:// 2003
//			break;
//
//		case java.sql.Types.BINARY:// -2
//			break;
//
//		case java.sql.Types.BIT:// -7
//			this.rowset.updateBoolean(this.columnName, ((Boolean) _aux).booleanValue());
//			break;
//
//		case java.sql.Types.BLOB:// 2004
//			break;
//
//		case java.sql.Types.BOOLEAN:// 16
//			this.rowset.updateBoolean(this.columnName, ((Boolean) _aux).booleanValue());
//			break;
//
//		case java.sql.Types.CLOB:// 2005
//			break;
//
//		case java.sql.Types.DATALINK:// 70
//			break;
//
//		case java.sql.Types.DATE:// 91
//			this.rowset.updateDate(this.columnName, new java.sql.Date(((java.util.Date) _aux).getTime()));
//			break;
//
//		case java.sql.Types.DECIMAL:// 3
//		case java.sql.Types.NUMERIC:
//		case java.sql.Types.BIGINT:
//		case java.sql.Types.DOUBLE:
//		case java.sql.Types.FLOAT:
//		case java.sql.Types.INTEGER:
//		case java.sql.Types.REAL:
//		case java.sql.Types.SMALLINT:
//		case java.sql.Types.TINYINT:
//			if (_aux instanceof java.math.BigDecimal) {
//				this.rowset.updateDouble(this.columnName, ((Double) _aux).doubleValue());
//			} else if (_aux instanceof Double) {
//				this.rowset.updateDouble(this.columnName, ((Double) _aux).doubleValue());
//			} else if (_aux instanceof Float) {
//				this.rowset.updateFloat(this.columnName, ((Float) _aux).floatValue());
//			} else if (_aux instanceof Integer) {
//				this.rowset.updateInt(this.columnName, ((Integer) _aux).intValue());
//			} else if (_aux instanceof Long) {
//				this.rowset.updateLong(this.columnName, ((Long) _aux).longValue());
//			} else {
//				System.out.println("Value aux is of unknown type......unable to update database");
//			}
//
//			break;
//
//		case java.sql.Types.DISTINCT:// 2001
//			break;
//
//		case java.sql.Types.JAVA_OBJECT:// 2000
//			break;
//
//		case java.sql.Types.LONGVARBINARY:// -4
//		case java.sql.Types.VARBINARY:// -3
//			break;
//
//		case java.sql.Types.VARCHAR://
//			System.out.println("VARCHAR --> updateString()");
//			this.rowset.updateString(this.columnName, _aux.toString());
//			break;
//
//		case java.sql.Types.LONGVARCHAR:// -1
//			System.out.println("LONGVARCHAR --> updateString()");
//			this.rowset.updateString(this.columnName, _aux.toString());
//			break;
//
//		case java.sql.Types.CHAR:// 1
//			System.out.println("CHAR --> updateString()");
//			this.rowset.updateString(this.columnName, _aux.toString());
//			break;
//
//		case java.sql.Types.NULL:// 0
//			break;
//
//		case java.sql.Types.OTHER:// 1111
//			break;
//
//		case java.sql.Types.REF:// 2006
//			break;
//
//		case java.sql.Types.STRUCT:// 2002
//			break;
//
//		case java.sql.Types.TIME:// 92
//			System.out.println("TIME --> updateTime()");
//			System.out.println("TIME : " + _aux.getClass().getName());
//			this.rowset.updateTime(this.columnName, new java.sql.Time(((java.util.Date) _aux).getTime()));
//			break;
//
//		case java.sql.Types.TIMESTAMP:// 93
//			System.out.println("TIMESTAMP --> updateTimestamp()");
//			System.out.println("TIMESTAMP : " + _aux.getClass().getName());
//			this.rowset.updateTimestamp(this.columnName, new java.sql.Timestamp(((java.util.Date) _aux).getTime()));
//			break;
//
//		default:
//			System.out.println("============================================================================");
//			System.out.println("Unknown column type");
//			System.out.println("default = " + this.colType);
//			System.out.println("columnName = " + this.columnName);
//			System.out.println("============================================================================");
//			break;
//		}
//	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.beans.beancontext.BeanContextProxy#getBeanContextProxy()
//	 */
//	@Override
//	public BeanContextChild getBeanContextProxy() {
//		return this.beanContextChildSupport;
//	}

	/*
	 * protected void processFocusEvent(FocusEvent e) {
	 * System.out.println("processFocusEvent()" + e);
	 * 
	 * if ( e.getID() == FocusEvent.FOCUS_LOST ) { System.out.println("FOCUS_LOST");
	 * try { if ( this.getText().length() == 0 ) { setValue(null); } } catch
	 * (NullPointerException npe) {
	 * System.out.println("processFocusEvent() --> NullPointerException"); } }
	 * super.processFocusEvent(e); }
	 */

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see javax.swing.JFormattedTextField#commitEdit()
//	 */
//	@Override
//	public void commitEdit() throws ParseException {
//
//		if (getText() == null || getText().length() == 0) {
//			super.setValue(null);
//		} else {
//			super.commitEdit();
//		}
//	}

	// this method can be override to make custom validation.
	/**
	 * Checks if the value is valid of the component
	 * 
	 * @param _value - value to be validated
	 * @return returns true if the value is valid else false
	 */
	@Deprecated
	public boolean validateField(final Object _value) {

		if (this.getAllowNull() == false && _value == null)
			return false;
		return true;
	}

	/**
	 * Getter for property nullable.
	 * 
	 * @return Value of property nullable.
	 */
	@Deprecated
	public boolean isNullable() {

		return getAllowNull();
	}

	/**
	 * Setter for property nullable.
	 * 
	 * @param _nullable New value of property nullable.
	 */
	@Deprecated
	public void setNullable(final boolean _nullable) {
		
		setAllowNull(_nullable);

//		boolean oldNullable = this.nullable;
//		this.nullable = _nullable;
//		this.firePropertyChange("nullable", new Boolean(oldNullable), new Boolean(_nullable));
	}

	/**
	 * Sets the value that will be formatted to null
	 */
	// TODO consider deprecating this
	@Deprecated
	public void cleanField() {
		super.setValue(null);
	}

    /**
	 * Adds any necessary listeners for the current SwingSet component. These will trigger changes
	 * in the underlying RowSet column.
	 */
	@Override
	public void addSSComponentListener() {
		addSSDocumentListener();
	}

    /**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * 
	 * It will actually be called from SSCommon.init() once the SSCommon data member
	 * is instantiated.
	 */
	@Override
	public void customInit() {
		//this.setFont(new Font(this.getFont().getName(), Font.BOLD, 11));
		this.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		
//		/*
//		 * add this as a self KeyListener *
//		 */
//		addKeyListener(this);

		/*
		 * add this as a self FocusListener - this allows for color highlighting
		 */
		addFocusListener(this);

//		/*
//		 * add this as a self MouseListener
//		 *
//		 */
//		addMouseListener(this);
//
//		/*
//		 *
//		 */
//		this.menu = new JPopupMenu();
//		this.menu.add("Option 1");
//		this.menu.add("Option 2");
//		this.menu.add("Option 3");

		/*
		 * set InputVerifier. Rowset's updates are handled by this class. Is the
		 * preferred method instead of focus change.
		 */
		setInputVerifier(new internalVerifier());
		
		
	}

    /**
	 * Returns the ssCommon data member for the current Swingset component.
	 * 
	 * @return shared/common SwingSet component data and methods
	 */
    @Override
	public SSCommon getSSCommon() {
		return ssCommon;
	}

	/**
	 * Removes any necessary listeners for the current SwingSet component. These will trigger changes
	 * in the underlying RowSet column.
	 */
	@Override
	public void removeSSComponentListener() {
		removeSSDocumentListener();
	}

	/**
	 * Sets the SSCommon data member for the current Swingset Component.
	 * 
	 * @param _ssCommon shared/common SwingSet component data and methods
	 */
	@Override
	public void setSSCommon(SSCommon _ssCommon) {
		ssCommon = _ssCommon;
	}


	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText()
	 * 
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed
	 */
	@Override
	public void updateSSComponent() {
		// TODO This is taken right from SSTextField. May need further modifications. See DbToFm()
		// TODO Consider adding back color coding for numeric types based on sign
		
		// DbToFm() was doing this work previously and had some special handling for dates, etc.
		this.setText(getBoundColumnText());
	}
}