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

package com.nqadmin.swingset;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.datasources.SSRowSet;

// SSDataGrid.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * SSDataGrid provides a way to display information from a database in a table
 * format (aka "spreadsheet" or "datasheet" view). The SSDataGrid takes a
 * SSRowSet as a source of data. It also provides different cell renderers
 * including a comboboxes renderer and a date renderer.
 * <p>
 * SSDataGrid internally uses the SSTableModel to display the information in a
 * table format. SSDataGrid also provides an easy means for displaying headers.
 * Columns can be hidden or made uneditable. In addition, it provides much finer
 * control over which cells can be edited and which cells can't be edited. It
 * uses the SSCellEditing interface for achieving this. The implementation of
 * this interface also provides a way to specify what kind of information is
 * valid for each cell.
 * <p>
 * SSDataGrid uses the isCellEditable() method in SSCellEditing to determine if
 * a cell is editable or not. The cellUpdateRequested() method of SSCellEditing
 * is used to notify a user program when an update is requested. While doing so
 * it provides the present value in the cell and also the new value. Based on
 * this information the new value can be rejected or accepted by the program.
 * <p>
 * SSDataGrid also provides an "extra" row to facilitate the addition of rows to
 * the table. Default values for various columns can be set programmatically. A
 * programmer can also specify which column is the primary key column for the
 * underlying SSRowSet and supply a primary key for that column when a new row
 * is being added.
 * <p>
 * While using the headers always set them before you set the SSRowSet.
 * Otherwise the headers will not appear.
 * <p>
 * Also if you are using column names rather than column numbers for different
 * function you have to call them only after setting the SSRowSet. Because
 * SSDataGrid uses the SSRowSet to convert the column names to column numbers.
 * If you specify the column numbers you can do before or after setting the
 * SSRowSet, it does not matter.
 * <p>
 * You can simply remember this order 1.Set the headers 2.Set the SSRowSet 3.Any
 * other function calls.
 * <pre>
 * Simple Example:
 *
 *{@code
 * // SET THE HEADER BEFORE SETTING THE SSROWSET
 * 	dataGrid.setHeaders(new String[]{"Part Name", "Color Code", " Weight", "City"});
 * 	dataGrid.setSSRowSet(ssRowSet);
 *
 * // HIDE THE PART ID COLUMN
 * // THIS SETS THE WIDTH OF THE COLUMN TO 0
 * 	dataGrid.setHiddenColumns(new String[]{"part_id"});
 *
 * 	dataGrid.setMessageWindow(this);
 * 	dataGrid.setUneditableColumns(new String[]{"part_id"});
 *
 * 	dataGrid.setComboRenderer("color_code",new String[]{"Red","Green","Blue"}, new Integer[]{new Integer(0),new Integer(1),new Integer(2)});
 * 	dataGrid.setDefaultValues(new int[]{1,2,3},new Object[]{new Integer(0), new Integer(20),new String("New Orleans")});
 *
 * 	dataGrid.setPrimaryColumn("part_id");
 * 	dataGrid.setSSDataValue(new SSDataValue(){ public Object getPrimaryColumnValue() {
 * 		// YOUR PRIMARY KEY VALUE GENERATION GOES HERE
 * 		// IF IT'S SOME THING USER ENTERS THEN NO PROBLEM
 * 		// IF IT'S AN AUTO INCREMENT FIELD THEN IT DEPENDS ON
 * 		// THE DATABASE DRIVER YOU ARE USING.
 * 		// IF THE UPDATEROW CAN RETRIEVE THE VALUES FOR THE ROW WITHOUT KNOWING THE PRIMARY KEY VALUE ITS FINE
 * 		// BUT POSTGRES CAN'T UPDATE ROW WITHOUT THE PRIMARY / COLUMN.
 *
 * 		// YOUR PRIMARY KEY VALUE GENERATION GOES HERE. ........ ........ ........
 *
 * 		}
 * 	});
 * }
 *
 * Also See Examples 5, 6, 7 in the samples.
 * </pre>
 */

public class SSDataGrid extends JTable {

	// TODO Add support for JFormattedTextField.
	// TODO Add support for GlazedList table features.

	/**
	 * Editor for check box fields.
	 */
	protected class CheckBoxEditor extends DefaultCellEditor {
		/**
		 * Unique serial ID
		 */
		private static final long serialVersionUID = 966225988861238964L;
		
		// Variable to store the java.sql.Type
		protected int columnClass = 0;

		public CheckBoxEditor() {
			super(new JCheckBox());
		}

		@Override
		public Object getCellEditorValue() {
			// GET THE COMPONENT AND CHECK IF IT IS CHECKED OR NOT.
			if (((JCheckBox) getComponent()).isSelected()) {
				// CHECK THE COLUMN TYPE AND RETURN CORRESPONDING OBJECT.
				// IF IT IS INTEGER THEN 1 IS CONSIDERED TRUE AND 0 FALSE.
				if (columnClass == java.sql.Types.BOOLEAN) {
					return new Boolean(true);
				}
				return new Integer(1);
			}
			if (columnClass == java.sql.Types.BOOLEAN) {
				return new Boolean(false);
			}
			return new Integer(0);
		}

		@Override
		public Component getTableCellEditorComponent(final JTable _table, final Object _value, final boolean _selected, final int _row,
				final int _column) {

			// GET THE COMPONENT RENDERING THE VALUE.
			final JCheckBox checkBox = (JCheckBox) getComponent();

			// CHECK THE TYPE OF COLUMN, IT SHOULD BE THE SAME AS THE TYPE OF _VALUE.
			if (_value instanceof Boolean) {
				// STORE THE TYPE OF COLUMN WE NEED THIS WHEN EDITOR HAS TO RETURN
				// VALUE BACK.
				columnClass = java.sql.Types.BOOLEAN;
				// BASED ON THE VALUE CHECK THE BOX OR UNCHECK IT.
				if (((Boolean) _value)) {
					checkBox.setSelected(true);
				} else {
					checkBox.setSelected(false);
				}
			}
			// IF THE COLUMN CLASS IS INTEGER
			else if (_value instanceof Integer) {
				// STORE THE COLUMN CLASS.
				columnClass = java.sql.Types.INTEGER;
				// BASED ON THE INTEGER VALUE CHECK OR UNCHECK THE CHECK BOX.
				// A VALUE OF 0 IS CONSIDERED TRUE - CHECK BOX IS CHECKED.
				// ANY OTHER VALUE IS CONSIDERED FALSE - UNCHECK THE CHECK BOX.
				if (((Integer) _value).intValue() != 0) {
					checkBox.setSelected(true);
				} else {
					checkBox.setSelected(false);
				}
			}
			// IF THE COLUMN CLASS IS NOT BOOLEAN OR INTEGER
			// LOG ERROR MESSAGE.
			else {
				logger.error("Can't set check box value. Unknown data type. Column type should be Boolean or Integer for check box columns.");
			}
			// RETURN THE EDITOR COMPONENT
			return checkBox;
		}
	}

	/**
	 * Renderer for check box fields.
	 */
	protected class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

		/**
		 * Unique serial ID.
		 */
		private static final long serialVersionUID = -8310278203475303010L;

		public CheckBoxRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(final JTable _table, final Object _value, final boolean _selected,
				final boolean _hasFocus, final int _row, final int _column) {

			if (_value instanceof Boolean) {
				if (((Boolean) _value)) {
					setSelected(true);
				} else {
					setSelected(false);
				}
			} else if (_value instanceof Integer) {
				if (((Integer) _value).intValue() != 0) {
					setSelected(true);
				} else {
					setSelected(false);
				}
			} else {
				logger.error("Can't set check box value. Unknown data type. Column type should be Boolean or Integer for check box columns.");
			}

			return this;
		}

	}

	/**
	 * Editor for combo box fields.
	 */
	protected class ComboEditor extends DefaultCellEditor {
		
		/**
		 * unique serial id
		 */
		private static final long serialVersionUID = -6439941232160386725L;
		
		// Set the # of clicks required to edit the combo to 2.
		int tmpClickCountToStart = 2;
		Object[] underlyingValues = null;

		public ComboEditor(final Object[] _items, final Object[] _underlyingValues) {
			super(new JComboBox<>(_items));
			underlyingValues = _underlyingValues;
		}

		@Override
		public Object getCellEditorValue() {
			if (underlyingValues == null) {
				return new Integer(((JComboBox<?>) getComponent()).getSelectedIndex());
			}

			final int index = ((JComboBox<?>) getComponent()).getSelectedIndex();

			logger.trace("Index is "+ index);

			if (index == -1) {
				return underlyingValues[0];
			}

			return underlyingValues[index];
		}

		protected int getIndexOf(final Object _value) {
			if (underlyingValues == null) {
				// IF THE VALUE IS NULL THEN SET THE DISPLAY ON THE COMBO TO BLANK (INDEX -1)
				if (_value == null) {
					return -1;
				}
				return ((Integer) _value);
			}
			for (int i = 0; i < underlyingValues.length; i++) {
				if (underlyingValues[i].equals(_value)) {
					return i;
				}
			}

			return -1;
		}

		@Override
		public Component getTableCellEditorComponent(final JTable _table, final Object _value, final boolean _selected, final int _row,
				final int _column) {

			final JComboBox<?> comboBox = (JComboBox<?>) getComponent();
			comboBox.setSelectedIndex(getIndexOf(_value));
			return comboBox;
		}

		@Override
		public boolean isCellEditable(final EventObject event) {
			if (event instanceof MouseEvent) {
				return ((MouseEvent) event).getClickCount() >= tmpClickCountToStart;
			}
			return true;
		}
	}

	/**
	 * Renderer for combo box fields.
	 */
	protected class ComboRenderer extends DefaultTableCellRenderer.UIResource {
		
		/**
		 * Unique serial ID.
		 */
		private static final long serialVersionUID = 2010609036458432567L;
		
		Object[] displayValues = null;
		Object[] underlyingValues = null;

		public ComboRenderer(final Object[] _items, final Object[] _underlyingValues) {
			underlyingValues = _underlyingValues;
			displayValues = _items;
		}

		protected int getIndexOf(final Object _value) {
			if (_value == null) {
				return -1;
			}
			if (underlyingValues == null) {
				return ((Integer) _value);
			}
			for (int i = 0; i < underlyingValues.length; i++) {
				if (underlyingValues[i].equals(_value)) {
					return i;
				}
			}
			return 0;
		}

		@Override
		public Component getTableCellRendererComponent(final JTable _table, final Object _value, final boolean _selected,
				final boolean _hasFocus, final int _row, final int _column) {

			final JLabel label = (JLabel) super.getTableCellRendererComponent(_table, _value, _selected, _hasFocus, _row,
					_column);

			int index = -1;
			if (displayValues.length > 0) {
				index = getIndexOf(_value);
			} else {
				logger.error("No item in combo that corresponds to " + _value);
			}

			if (index == -1) {
				label.setText("");
			} else {
				label.setText(displayValues[index].toString());
			}
			return label;
		}
	}

	/**
	 * Editor for date fields. Used the SSTextField as the editor, but changes the
	 * format to mm/dd/yyy from yyyy-mm-dd.
	 */
	protected class DateEditor extends DefaultCellEditor {
		/**
		 * Unique serial ID.
		 */
		private static final long serialVersionUID = 8741829961228359406L;

		// CONSTRUCTOR FOR THE EDITOR CLASS
		public DateEditor() {
			super(new SSTextField(SSTextField.MMDDYYYY));
			getComponent().setFocusTraversalKeysEnabled(false);
			getComponent().addKeyListener(new KeyAdapter() {
				int keyPressed = 0;

				@Override
				public void keyPressed(final KeyEvent ke) {
					// changed date key listener to clear date field when a new key is pressed
					if ((ke.getKeyCode() == KeyEvent.VK_UP) || (ke.getKeyCode() == KeyEvent.VK_DOWN)
							|| (ke.getKeyCode() == KeyEvent.VK_LEFT) || (ke.getKeyCode() == KeyEvent.VK_RIGHT)
							|| (ke.getKeyCode() == KeyEvent.VK_ENTER) || (ke.getKeyCode() == KeyEvent.VK_TAB)) {
						return;
					}

					keyPressed++;
				}

				@Override
				public void keyReleased(final KeyEvent ke) {
					final JComponent editor = (JComponent) DateEditor.this.getComponent();
					if (editor instanceof JTextField) {
						if (keyPressed == 0) {
							((JTextField) editor).setText(String.valueOf(ke.getKeyChar()));
						}
					}
					keyPressed--;
					if (keyPressed < 0) {
						keyPressed = 0;
					}
				}
			});
		}

		// RETURNS A DATE OBJECT REPRESENTING THE VALUE IN THE CELL.
		@Override
		public Object getCellEditorValue() {
			final String strDate = ((JTextField) (DateEditor.this.getComponent())).getText();
			// IF THE FIELD IS EMPTY RETURN NULL
			if ((strDate == null) || "".equals(strDate.trim())) {
				return null;
			}
			final StringTokenizer strtok = new StringTokenizer(strDate, "/", false);
			final Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, Integer.parseInt(strtok.nextToken()) - 1);
			calendar.set(Calendar.DATE, Integer.parseInt(strtok.nextToken()));
			calendar.set(Calendar.YEAR, Integer.parseInt(strtok.nextToken()));
			return new Date(calendar.getTimeInMillis());
		}

		// RETURNS THE TEXTFIELD WITH THE GIVEN DATE IN THE TEXTFIELD
		// (AFTER THE FORMAT IS CHANGED TO MM/DD/YYYY
		@Override
		public synchronized Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
				final int row, final int column) {

			if (value instanceof Date) {
				final Date date = (Date) value;
				final GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				final String strDate = "" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH)
						+ "/" + calendar.get(Calendar.YEAR);
				return super.getTableCellEditorComponent(table, strDate, isSelected, row, column);
			}

			return super.getTableCellEditorComponent(table, value, isSelected, row, column);

		}

		@Override
		public boolean isCellEditable(final EventObject event) {
			// IF NUMBER OF CLICKS IS LESS THAN THE CLICKCOUNTTOSTART RETURN FALSE
			// FOR CELL EDITING.
			if (event instanceof MouseEvent) {
				return ((MouseEvent) event).getClickCount() >= getClickCountToStart();
			}

			return true;
		}
	}

	/**
	 * Renderer for date fields. Displays dates using mm/dd/yyyy format.
	 */
	protected class DateRenderer extends DefaultTableCellRenderer {

		/**
		 * Unique serial ID.
		 */
		private static final long serialVersionUID = 2167118906692276587L;

		@Override
		public void setValue(final Object value) {
			if (value instanceof java.sql.Date) {
				final Date date = (Date) value;
				final GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				final String strDate = "" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH)
						+ "/" + calendar.get(Calendar.YEAR);
				setHorizontalAlignment(SwingConstants.CENTER);
				setText(strDate);
			} else {
				super.setValue(value);
			}
		}
	}

	/**
	 * This is the default editor for Numeric, String and Object column types.
	 */
	class DefaultEditor extends DefaultCellEditor {
		
		/**
		 * Implementation of KeyListener and FocusListener for the editor component.
		 */
		protected class MyListener implements KeyListener, FocusListener {

			int keyPressed = 0;
			
			/**
			 * Select all text when focus is gained
			 */
			@Override
			public void focusGained(final FocusEvent fe) {
				((SSTextField) getComponent()).selectAll();
			}

			/**
			 * sets the keyPressed variable to zero.
			 */
			@Override
			public void focusLost(final FocusEvent fe) {
				// Set the key press tracker to 0 when the field loses focus.
				keyPressed = 0;
			}

			/**
			 * Increment the key pressed variable when ever there is a key pressed event, other than the Tab key.
			 * <p>
			 * It is assumed that the editor will not get the keyPressed event for the first key,
			 * which triggers the editor. That event is consumed by the JTable.
			 */
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() != KeyEvent.VK_TAB) {
					keyPressed++;
				}
			}

			/**
			 * Based on if this is first key release event the contents will be cleared
			 */
			@Override
			public void keyReleased(final KeyEvent ke) {
				final JComponent editor = (JComponent) DefaultEditor.this.getComponent();
				if (editor instanceof JTextField) {
					if ((keyPressed == 0) && Character.isLetterOrDigit(ke.getKeyChar())) {
						((JTextField) editor).setText(String.valueOf(ke.getKeyChar()));
					}
				}
				keyPressed--;
				if (keyPressed < 0) {
					keyPressed = 0;
				}

			}

			@Override
			public void keyTyped(final KeyEvent ke) {
				// do nothing
			}
		}

		/**
		 * Unique serial ID.
		 */
		private static final long serialVersionUID = -5408829003545103686L;

		/**
		 * Constructor to instantiate an object of column type from a string.
		 */
		Constructor<?> constructor;

		/**
		 * Value of the editor.
		 */
		Object value;

		/**
		 * Constructs Default Editor.
		 */
		public DefaultEditor() {
			super(new SSTextField());
			getComponent().setFocusTraversalKeysEnabled(false);
			final MyListener listener = new MyListener();
			getComponent().addFocusListener(listener);
			getComponent().addKeyListener(listener);
		}

		/**
		 * Returns the cell value.
		 */
		@Override
		public Object getCellEditorValue() {
			return value;
		}

		@Override
		public Component getTableCellEditorComponent(final JTable _table, final Object _value,
				final boolean _isSelected, final int _row, final int _column) {

			// SET INITIAL VALUE TO NULL.
			value = null;

			((JComponent) getComponent()).setBorder(new LineBorder(Color.black));

			// GET A CONSTRUCTOR FOR AN OBJECT OF THE CURRENT COLUMN TYPE.
			// THIS IS NEEDED FOR RETURNING THE VALUE IN COLUMN CLASS OBJECT
			try {
				Class<?> type = _table.getColumnClass(_column);
				if (type == Object.class) {
					type = String.class;
				}
				constructor = type.getConstructor(new Class<?>[] { String.class });
			} catch (final Exception e) {
				return null;
			}

			return super.getTableCellEditorComponent(_table, _value, _isSelected, _row, _column);
		}

		@Override
		public boolean stopCellEditing() {

			final String s = (String) super.getCellEditorValue();

			if (s.trim().equals("")) {
				if (constructor.getDeclaringClass() == String.class) {
					value = s;
				}
			} else {
				try {
					value = constructor.newInstance(new Object[] { s });
				} catch (final Exception e) {
					// DRAW A RED BORDER IF THE VALUE OBJECT CAN'T BE CREATED.
					// PROBABLY THE DATA ENTERED IS NOT RIGHT (STRING IN NUMBER FIELD OR VICE-VERSA)
					((JComponent) getComponent()).setBorder(new LineBorder(Color.red));
					return false;
				}
			}

			return super.stopCellEditing();

		}

	}

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * Unique serial ID
	 */
	private static final long serialVersionUID = -7924790696188174770L;

	/**
	 * Variable to indicate if rows can be deleted.
	 */
	protected boolean allowDeletion = true;

	/**
	 * Variable to indicate if execute() should be called on the SSRowSet.
	 */
	protected boolean callExecute = true;

	/**
	 * Number of columns in the SSRowSet.
	 */
	protected int columnCount = -1;

	/**
	 * Minimum width of the columns in the data grid.
	 */
	protected int columnWidth = 100;

	/**
	 * DagaGridHandler to help with row deletions, and insertions
	 */
	protected SSDataGridHandler dataGridHandler;

	/**
	 * Array used to store the column names that have to hidden.
	 */
	protected String[] hiddenColumnNames = null;

	/**
	 * Array used to store the column numbers that have to be hidden.
	 */
	protected int[] hiddenColumns = null;

	/**
	 * Variable to indicate if the data grid will display an additional row for
	 * inserting new rows.
	 */
	protected boolean insertion = true;

	/**
	 * Component where messages should be popped up.
	 */
	protected Component messageWindow = null;

	/**
	 * Number of records retrieved from the SSRowSet.
	 */
	protected int rowCount = -1;

	/**
	 * Scrollpane used to scroll datagrid.
	 */
	protected JScrollPane scrollPane = null; // new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	/**
	 * SSRowSet from which component will get/set values.
	 */
	protected SSRowSet sSRowSet = null;

	/**
	 * Table model to construct the JTable
	 */
	protected SSTableModel tableModel = new SSTableModel();

	/**
	 * Constructs an empty data grid.
	 */
	public SSDataGrid() {
		init();
	}

	/**
	 * Constructs a data grid with the data source set to the given SSRowSet.
	 *
	 * @param _sSRowSet SSRowSet from which values have to be retrieved.
	 */
	public SSDataGrid(final SSRowSet _sSRowSet) {
		this();
		sSRowSet = _sSRowSet;
		bind();
	}

	/**
	 * Initializes the data grid control. Collects metadata information about the
	 * given SSRowSet.
	 */
	protected void bind() {

		try {
			// EXECUTE THE QUERY
			if (callExecute) {
				sSRowSet.execute();
			}

			// SPECIFY THE SSROWSET TO THE TABLE MODEL.
			tableModel.setSSRowSet(sSRowSet);

			// SET THE TABLE MODEL FOR JTABLE
			setModel(tableModel);

			// GET THE ROW COUNT
			rowCount = tableModel.getRowCount();

			// GET THE COLUMN COUNT
			columnCount = tableModel.getColumnCount();

		} catch (final SQLException se) {
			logger.error("SQL Exception.", se);
		}

		// THIS IS NEEDED IF THE NUMBER OF COLUMNS IN THE NEW SSROWSET
		// DOES NOT MATCH WITH THE OLD COLUMNS.
		createDefaultColumnModel();

		// HIDE COLUMNS AS NEEDED - ALSO CALLS updateUI()
		hideColumns();

		// UPDATE DISPLAY
		// updateUI();

	} // end protected void bind() {

	/**
	 * Returns the callExecute property. If set to true causes the navigator to skip
	 * the execute function call on the specified SSRowSet. (See FAQ for further
	 * details).
	 *
	 * @return true if execute function call has to be skipped else false
	 */
	public boolean getCallExecute() {
		return callExecute;
	}

	/**
	 * Returns the minimum column width for the data grid.
	 *
	 * @return minimum column width of the each column
	 */
	public int getColumnWidth() {
		return columnWidth;
	}

	/**
	 * Returns scroll pane with the JTable embedded in it.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @return scroll pane with embedded JTable
	 */
	public Component getComponent() {
		return scrollPane;
	}

	/**
	 * Returns the default value being used for the specified column. Returns null
	 * if a default is not in use.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _columnNumber the column number for which default value is to be
	 *                      returned.
	 *
	 * @return returns an object containing the default value for the requested
	 *         column.
	 */
	public Object getDefaultValue(final int _columnNumber) {
		return tableModel.getDefaultValue(_columnNumber);
	}

	/**
	 * Returns the default value being used for the specified column. Returns null
	 * if a default is not in use.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _columnName the column name for which default value is to be returned.
	 *
	 * @return returns an object containing the default value for the requested
	 *         column.
	 *
	 * @throws SQLException is the specified column name is not present in the
	 *                      SSRowSet
	 */
	public Object getDefaultValue(final String _columnName) throws SQLException {
		final int columnNumber = sSRowSet.getColumnIndex(_columnName);
		return tableModel.getDefaultValue(columnNumber - 1);
	}

	/**
	 * Returns the allowInsertion property of the table. If set to true an
	 * additional row for inserting new rows will be displayed
	 *
	 * @return true if new rows can be added else false.
	 */
	public boolean getInsertion() {
		return insertion;
	}

	/**
	 * Returns the component on which error messages will be popped up. The error
	 * dialog will use this component as its parent component.
	 *
	 * @return the component that should be used when displaying error messages
	 */
	public Component getMessageWindow() {
		return messageWindow;
	}

	/**
	 * Returns number of selected columns.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @return number of selected columns
	 */
	@Override
	public int getSelectedColumnCount() {
		final int[] selectedColumns = getSelectedColumns();
		if (selectedColumns == null) {
			return 0;
		}

		return selectedColumns.length;
	}

	/**
	 * Returns the list of selected, visible columns. This function gets the list of selected
	 * columns from parent class and removes any columns which are present in hidden
	 * columns.
	 * <p>
	 * This was needed because when doing a copy/paste operation, hidden columns were included
	 * with the initial selection.
	 *
	 * @return array of selected columns
	 */
	@Override
	public int[] getSelectedColumns() {
		// IF THERE ARE NO HIDDEN COLUMNS THEN RETURN THE SAME LIST
		if (hiddenColumns == null) {
			return super.getSelectedColumns();
		}

		// GET THE LIST OF SELECTED COLUMNS FROM SUPER CLASS.
		final int[] selectedColumns = super.getSelectedColumns();
		final Vector<Object> filteredColumns = new Vector<>();

		// FILTER OUT THE HIDDEN COLUMNS FROM THIS LIST.
		for (int i = 0; i < selectedColumns.length; i++) {
			boolean found = false;
			// CHECK THIS COLUMN NUMBER WITH HIDDEN COLUMNS
			for (int j = 0; j < hiddenColumns.length; j++) {
				// IF ITS THERES INDICATE THE SAME AND BREAK OUT.
				if (selectedColumns[i] == hiddenColumns[j]) {
					found = true;
					break;
				}
			}
			// IF THIS COLUMN IS NOT IN HIDDEN COLUMNS ADD IT TO FILTERED LIST
			if (!found) {
				filteredColumns.add(new Integer(selectedColumns[i]));
			}
		}

		// CREATE AN INT ARRAY CONTAINING THE FILETED LIST OF COLUMNS
		final int[] result = new int[filteredColumns.size()];
		for (int i = 0; i < filteredColumns.size(); i++) {
			result[i] = ((Integer) filteredColumns.elementAt(i));
		}

		return result;
	}

	/**
	 * Returns the SSRowSet being used to get the values.
	 *
	 * @return returns the SSRowSet being used.
	 */
	public SSRowSet getSSRowSet() {
		return sSRowSet;
	}

	/**
	 * Hides the columns specified in the hidden columns list.
	 */
	protected void hideColumns() {

		// SET THE MINIMUM WIDTH OF COLUMNS
		final TableColumnModel tmpColumnModel = getColumnModel();
		TableColumn tmpColumn;
		for (int i = tmpColumnModel.getColumnCount() - 1; i >= 0; i--) {
			tmpColumn = tmpColumnModel.getColumn(i);
			int j = -1;

			if (hiddenColumns != null) {
				// SET THE WIDTH OF HIDDEN COLUMNS AS 0
				for (j = 0; j < hiddenColumns.length; j++) {
					if (hiddenColumns[j] == i) {
						tmpColumn.setMaxWidth(0);
						tmpColumn.setMinWidth(0);
						tmpColumn.setPreferredWidth(0);
						break;
					}
				}
				// AUTO RESIZE IS SET TO OFF IN THE INIT FUNCTION.
				// SO IF IT IS NOT IN AUTO RESIZE MODE THEN USER HAS REQUESTED
				// AUTO RESIZING. SO DON'T SET ANY SPECIFIC SIZE TO THE COLUMNS.
				if (j == hiddenColumns.length) {
					if (getAutoResizeMode() == AUTO_RESIZE_OFF) {
						tmpColumn.setPreferredWidth(columnWidth);
					}

				}
			} else {
				// SET OTHER COLUMNS MIN WIDTH TO 100
				if (getAutoResizeMode() == AUTO_RESIZE_OFF) {
					tmpColumn.setPreferredWidth(columnWidth);
				}

			}
		}
		updateUI();
	}

	/**
	 * Initialization code.
	 */
	protected void init() {

		// FORCE JTABLE TO SURRENDER TO THE EDITOR WHEN KEYSTROKES CAUSE THE EDITOR TO
		// BE ACTIVATED
		setSurrendersFocusOnKeystroke(true);
		setDefaultEditor(Number.class, new DefaultEditor());
		setDefaultEditor(String.class, new DefaultEditor());
		setDefaultEditor(Object.class, new DefaultEditor());

		// ADD KEY LISTENER TO JTABLE.
		// THIS IS USED FOR DELETING THE ROWS
		// ALLOWS MULTIPLE ROW DELETION.
		// KEY SEQUENCE FOR DELETING ROWS IS CTRL-X.
		addKeyListener(new KeyAdapter() {
			private boolean controlPressed = false;

			// IF THE KEY PRESSED IS CONTROL STORE THAT INFO.
			@Override
			public void keyPressed(final KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
					controlPressed = true;
				}
			}

			// HANDLE KEY RELEASES
			@Override
			public void keyReleased(final KeyEvent ke) {
				// IF CONTROL KEY IS RELEASED SET THAT CONTROL IS NOT PRESSED.
				if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
					controlPressed = false;
				}
				// IF X IS PRESSED WHILE THE CONTROL KEY IS STILL PRESSED
				// DELETE THE SELECTED ROWS.
				if (ke.getKeyCode() == KeyEvent.VK_X) {
					if (!allowDeletion) {
						return;
					}

					if (!controlPressed) {
						return;
					}
					// GET THE NUMBER OF ROWS SELECTED
					final int numRows = getSelectedRowCount();
					if (numRows == 0) {
						return;
					}
					// GET LIST OF ROWS SELECTED
					final int[] rows = getSelectedRows();
					// IF USER HAS PROVIDED A PARENT COMPONENT FOR ERROR MESSAGES
					// CONFIRM THE DELETION
					if (messageWindow != null) {
						final int returnValue = JOptionPane.showConfirmDialog(messageWindow,
								"You are about to delete " + rows.length + " rows. "
										+ "\nAre you sure you want to delete the rows?");
						if (returnValue != JOptionPane.YES_OPTION) {
							return;
						}
					}
					// START DELETING THE ROWS IN BOTTON UP FASHION
					// IN DOING SO YOU RETAIN THE ROW NUMBERS THAT HAVE TO BE DELETED
					// IF YOU DO IT TOP DOWN THE ROW NUMBERING CHANGES AS SOON AS A
					// ROW IS DELETED AS A RESULT LOT OF CARE HAS TO BE TAKEN
					// TO IDENTIFY THE NEW ROW NUMBERS AND THEN DELETE THE ROWS
					// INSTEAD OF THAT ITS MUCH EASIER IF YOU DO IT BOTTOM UP.
					for (int i = rows.length - 1; i >= 0; i--) {
						tableModel.deleteRow(rows[i]);
					}
					updateUI();
				}
			}
		});

		// CREATE AN INSTANCE OF KEY ADAPTER ADD PROVIDE THE PRESET GRID TO THE ADAPTER.
		// THIS IS FOR COPY AND PASTE SUPPORT
		final SSTableKeyAdapter keyAdapter = new SSTableKeyAdapter(this);
		keyAdapter.setAllowInsertion(true);

		// SET THE TABLE MODEL FOR JTABLE
		// this.setModel(tableModel);

		// SPECIFY THE MESSAGE WINDOW TO WHICH THE TABLE MODEL HAS TO POP UP
		// ERROR MESSAGES.
		tableModel.setMessageWindow(messageWindow);
		tableModel.setJTable(this);

		// THIS CAUSES THE JTABLE TO DISPLAY THE HORIZONTAL SCROLL BAR AS NEEDED.
		// CODE IN HIDECOLUMNS FUNCTION DEPENDS ON THIS VARIABLE.
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// ADD THE JTABLE TO A SCROLL BAR
		scrollPane = new JScrollPane(this, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	} // end protected void init() {

	/**
	 * @return the allowDeletion flag
	 */
	public boolean isAllowDeletion() {
		return allowDeletion;
	}

	/**
	 * @param _allowDeletion boolean indicating if deletions are allowed
	 */
	public void setAllowDeletion(final boolean _allowDeletion) {
		allowDeletion = _allowDeletion;
	}

	/**
	 * Sets the callExecute property. If set to true causes the navigator to skip
	 * the execute function call on the specified SSRowSet. (See FAQ for further
	 * details)
	 *
	 * @param _callExecute true if execute function call has to be skipped else
	 *                     false
	 */
	public void setCallExecute(final boolean _callExecute) {
		final boolean oldValue = callExecute;
		callExecute = _callExecute;
		firePropertyChange("callExecute", oldValue, callExecute);
	}

	/**
	 * Sets a check box renderer for the specified column.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _column - column number for which check box rendering is needed.
	 */
	public void setCheckBoxRenderer(final int _column) {
		final TableColumnModel tmpColumnModel = getColumnModel();
		final TableColumn tmpTableColumn = tmpColumnModel.getColumn(_column);
		tmpTableColumn.setCellRenderer(new CheckBoxRenderer());
		tmpTableColumn.setCellEditor(new CheckBoxEditor());
	}

	/**
	 * Sets a check box renderer for the specified column.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _column - name of the column for which check box rendering is needed.
	 * @throws SQLException	SQLException
	 */
	public void setCheckBoxRenderer(final String _column) throws SQLException {
		final int column = sSRowSet.getColumnIndex(_column) - 1;
		setCheckBoxRenderer(column);
	}

	/**
	 * Sets the minimum column width for the data grid.
	 *
	 * @param _columnWidth minimum column width of the each column
	 */
	public void setColumnWidth(final int _columnWidth) {
		final int oldValue = columnWidth;
		columnWidth = _columnWidth;
		firePropertyChange("columnWidth", oldValue, columnWidth);
	}

	/**
	 * Sets a combo box renderer for the specified column. This is use full to limit
	 * the values that go with a column or if an underlying code is do be displayed
	 * in a more meaningfull manner.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _column           column number for which combo renderer is to be
	 *                          provided.
	 * @param _displayItems     the actual Objects to be displayed in the combo box.
	 * @param _underlyingValues the values that have to be written to the database
	 *                          when an item in the combo box is selected.
	 */
	public void setComboRenderer(final int _column, final Object[] _displayItems, final Object[] _underlyingValues) {
		setComboRenderer(_column, _displayItems, _underlyingValues, 250);
	}

	/**
	 * Sets a combo box renderer for the specified column. This is use full to limit
	 * the values that go with a column or if an underlying code is do be displayed
	 * in a more meaningful manner.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _column           column number for which combo renderer is to be
	 *                          provided.
	 * @param _displayItems     the actual Objects to be displayed in the combo box.
	 * @param _underlyingValues the values that have to be written to the database
	 *                          when an item in the combo box is selected.
	 * @param _columnWidth		minimium width for table column
	 */
	public void setComboRenderer(final int _column, final Object[] _displayItems, final Object[] _underlyingValues, final int _columnWidth) {
		setRowHeight(20);
		final TableColumnModel tmpColumnModel = getColumnModel();
		final TableColumn tmpTableColumn = tmpColumnModel.getColumn(_column);
		tmpTableColumn.setCellRenderer(new ComboRenderer(_displayItems, _underlyingValues));
		tmpTableColumn.setCellEditor(new ComboEditor(_displayItems, _underlyingValues));
		tmpTableColumn.setMinWidth(_columnWidth);
	}

	/**
	 * Sets a combo box renderer for the specified column. This is use full to limit
	 * the values that go with a column or if an underlying code is do be displayed
	 * in a more meaningfull manner.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _column           column name for which combo renderer is to be
	 *                          provided.
	 * @param _displayItems     the actual Objects to be displayed in the combo box.
	 * @param _underlyingValues the values that have to be written to the database
	 *                          when an item in the combo box is selected.
	 * @throws SQLException	SQLException
	 */
	public void setComboRenderer(final String _column, final Object[] _displayItems, final Object[] _underlyingValues)
			throws SQLException {
		setComboRenderer(_column, _displayItems, _underlyingValues, 250);
	}

	/**
	 * Sets a combo box renderer for the specified column. This is use full to limit
	 * the values that go with a column or if an underlying code is do be displayed
	 * in a more meaningfull manner.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _column           column name for which combo renderer is to be
	 *                          provided.
	 * @param _displayItems     the actual Objects to be displayed in the combo box.
	 * @param _underlyingValues the values that have to be written to the database
	 *                          when an item in the combo box is selected.
	 * @param _columnWidth      required minimum width for this column
	 * @throws SQLException	SQLException
	 */
	public void setComboRenderer(final String _column, final Object[] _displayItems, final Object[] _underlyingValues, final int _columnWidth)
			throws SQLException {
		final int column = sSRowSet.getColumnIndex(_column) - 1;
		setComboRenderer(column, _displayItems, _underlyingValues, _columnWidth);
	}

	/**
	 * Sets a date renderer for the specified column. The date will be displayed in
	 * mm/dd/yyyy format. If a date renderer is not requested then the date will be
	 * displayed in a standard format(yyyy-mm-dd).
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _column column number for which a date renderer is needed.
	 */
	public void setDateRenderer(final int _column) {
		final TableColumnModel tmpColumnModel = getColumnModel();
		final TableColumn tmpTableColumn = tmpColumnModel.getColumn(_column);
		tmpTableColumn.setCellRenderer(new DateRenderer());
		tmpTableColumn.setCellEditor(new DateEditor());
	}

	/**
	 * Sets a date renderer for the specified column. The date will be displayed in
	 * mm/dd/yyyy format. If a date renderer is not requested then the date will be
	 * displayed in a standard format(yyyy-mm-dd).
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _column column name for which a date renderer is needed.
	 * @throws SQLException	SQLException
	 */
	public void setDateRenderer(final String _column) throws SQLException {
		final int tmpColumn = sSRowSet.getColumnIndex(_column) - 1;
		final TableColumnModel tmpColumnModel = getColumnModel();
		final TableColumn tmpTableColumn = tmpColumnModel.getColumn(tmpColumn);
		tmpTableColumn.setCellRenderer(new DateRenderer());
		tmpTableColumn.setCellEditor(new DateEditor());
	}

	/**
	 * Sets the default values for different columns. When a new row is added these
	 * default values will be added to the columns. Please make sure that the object
	 * specified for each column is of the same type as that of the column in the
	 * database. Use the getColumnClass function in JTable to determine the exact
	 * data type.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _columnNumbers array containing the column numbers for which the
	 *                       defaults apply.
	 * @param _values        the values for the column numbers specified in
	 *                       _columnNumbers.
	 */
	public void setDefaultValues(final int[] _columnNumbers, final Object[] _values) {

		tableModel.setDefaultValues(_columnNumbers, _values);
	}

	/**
	 * Sets the default values for different columns. When a new row is added these
	 * default values will be added to the columns. Please make sure that the object
	 * specified for each column is of the same type as that of the column in the
	 * database. Use the getColumnClass function in JTable to determine the exact
	 * data type.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _columnNames array containing the column names for which the defaults
	 *                     apply.
	 * @param _values      the values for the column names specified in
	 *                     _columnNames.
	 *
	 * @throws SQLException if the specified column name is not present in the
	 *                      SSRowSet
	 */
	public void setDefaultValues(final String[] _columnNames, final Object[] _values) throws SQLException {

		int[] columnNumbers = null;

		if (_columnNames != null) {
			columnNumbers = new int[_columnNames.length];

			for (int i = 0; i < _columnNames.length; i++) {
				columnNumbers[i] = sSRowSet.getColumnIndex(_columnNames[i]) - 1;
			}
		}

		tableModel.setDefaultValues(columnNumbers, _values);
	}

	/**
	 * Sets the header for the JTable. This function has to be called before setting
	 * the SSRowSet for SSDataGrid.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _headers array of string objects representing the header of each
	 *                 column.
	 */
	public void setHeaders(final String[] _headers) {
		tableModel.setHeaders(_headers);
	}

	/**
	 * Sets the column numbers that should be hidden. The SSDataGrid sets the column
	 * width of these columns to 0. The columns are set to zero width rather than
	 * removing the column from the table. Thus preserving the column numbering.If a
	 * column is removed then the column numbers for columns after the removed
	 * column will change. Even if the column is specified as hidden user will be
	 * seeing a tiny strip. Make sure that you specify the hidden column numbers in
	 * the uneditable column list.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _columnNumbers array specifying the column numbers which should be
	 *                       hidden
	 */
	public void setHiddenColumns(final int[] _columnNumbers) {
		hiddenColumns = _columnNumbers;
		tableModel.setHiddenColumns(_columnNumbers);
		hideColumns();
	}

	/**
	 * Sets the column numbers that should be hidden. The SSDataGrid sets the column
	 * width of these columns to 0. The columns are set to zero width rather than
	 * removing the column from the table. Thus preserving the column numbering.If a
	 * column is removed then the column numbers for columns after the removed
	 * column will change. Even if the column is specified as hidden user will be
	 * seeing a tiny strip. Make sure that you specify the hidden column numbers in
	 * the uneditable column list.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _columnNames array specifying the column names which should be hidden
	 * @throws SQLException	SQLException
	 */
	public void setHiddenColumns(final String[] _columnNames) throws SQLException {
		hiddenColumns = null;
		tableModel.setHiddenColumns(hiddenColumns);
		if (_columnNames != null) {
			hiddenColumns = new int[_columnNames.length];
			for (int i = 0; i < _columnNames.length; i++) {
				hiddenColumns[i] = sSRowSet.getColumnIndex(_columnNames[i]) - 1;
			}
		}
		hideColumns();
	}

	/**
	 * Sets the allowInsertion property of the table. If set to true an additional
	 * row for inserting new rows will be displayed
	 *
	 * @param _insertion true if new rows can be added else false.
	 */
	public void setInsertion(final boolean _insertion) {
		final boolean oldValue = insertion;
		insertion = _insertion;
		firePropertyChange("insertion", oldValue, insertion);
		tableModel.setInsertion(_insertion);
		updateUI();
	}

	/**
	 * Sets the component on which error messages will be popped up. The error
	 * dialog will use this component as its parent component.
	 *
	 * @param _messageWindow the component that should be used when displaying error
	 *                       messages
	 */
	public void setMessageWindow(final Component _messageWindow) {
		final Component oldValue = messageWindow;
		messageWindow = _messageWindow;
		firePropertyChange("messageWindow", oldValue, messageWindow);
		tableModel.setMessageWindow(messageWindow);
	}

	/**
	 * Sets the column number which is the primary column for the table. This is
	 * required if new rows have to be added to the JTable. For this to properly
	 * work the SSDataValue object should also be provided SSDataValue is used to
	 * get the value for the primary column.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _columnNumber the column which is the primary column.
	 */
	public void setPrimaryColumn(final int _columnNumber) {
		tableModel.setPrimaryColumn(_columnNumber);
	}

	/**
	 * Sets the column number which is the primary column for the table. This is
	 * required if new rows have to be added to the JTable. For this to properly
	 * work the SSDataValue object should also be provided SSDataValue is used to
	 * get the value for the primary column.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _columnName the column which is the primary column.
	 * @throws SQLException	SQLException
	 */
	public void setPrimaryColumn(final String _columnName) throws SQLException {
		final int columnNumber = sSRowSet.getColumnIndex(_columnName) - 1;
		tableModel.setPrimaryColumn(columnNumber);
	}

	/**
	 * Sets the new SSRowSet for the combo box.
	 *
	 * @param _sSRowSet SSRowSet to which the combo has to update values.
	 *
	 * @deprecated Use {@link #setSSRowSet(SSRowSet _rowset)} instead.
	 */
	@Deprecated
	public void setRowSet(final SSRowSet _sSRowSet) {
		setSSRowSet(_sSRowSet);
	}

	/**
	 * If the user has to decide on which cell has to be editable and which is not
	 * then SSCellEditable interface has to be implemented and set it for the
	 * SSTableModel.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _cellEditing implementation of SSCellEditable interface.
	 */
	public void setSSCellEditing(final SSCellEditing _cellEditing) {
		tableModel.setSSCellEditing(_cellEditing);
	}

	/**
	 * Set the implementation of DataGridHandler which can be use to handle row
	 * deletions and insertions
	 *
	 * @param _dataGridHandler implementation of SSDataGridHandler interface.
	 */
	public void setSSDataGridHandler(final SSDataGridHandler _dataGridHandler) {
		tableModel.setSSDataGridHandler(_dataGridHandler);
	}

	/**
	 * Sets the SSDataValue interface implemention. This interface specifies
	 * function to retrieve primary column values for a new row to be added.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _dataValue implementation of SSDataValue
	 */
	public void setSSDataValue(final SSDataValue _dataValue) {
		tableModel.setSSDataValue(_dataValue);
	}

	/**
	 * Binds the SSRowSet to the grid. Data is taken from the new SSRowSet.
	 *
	 * @param _sSRowSet the SSRowSet which acts as the data source.
	 */
	public void setSSRowSet(final SSRowSet _sSRowSet) {
		final SSRowSet oldValue = sSRowSet;
		sSRowSet = _sSRowSet;
		firePropertyChange("sSRowSet", oldValue, sSRowSet);
		bind();
	} // end public void setSSRowSet(SSRowSet _sSRowSet) {

	/**
	 * Sets the uneditable columns. The columns specified as uneditable will not be
	 * available for user to edit. This overrides the isCellEditable function in
	 * SSCellEditing.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _columnNumbers array specifying the column numbers which should be
	 *                       uneditable.
	 */
	public void setUneditableColumns(final int[] _columnNumbers) {
		tableModel.setUneditableColumns(_columnNumbers);
	}

	/**
	 * Sets the uneditable columns. The columns specified as uneditable will not be
	 * available for user to edit. This overrides the isCellEditable function in
	 * SSCellEditing.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _columnNames array specifying the column names which should be
	 *                     uneditable.
	 * @throws SQLException	SQLException
	 */
	public void setUneditableColumns(final String[] _columnNames) throws SQLException {
		int[] columnNumbers = null;
		if (_columnNames != null) {
			columnNumbers = new int[_columnNames.length];

			for (int i = 0; i < _columnNames.length; i++) {
				columnNumbers[i] = sSRowSet.getColumnIndex(_columnNames[i]) - 1;
			}
		}

		tableModel.setUneditableColumns(columnNumbers);
	}

} // end public class SSDataGrid extends JTable {