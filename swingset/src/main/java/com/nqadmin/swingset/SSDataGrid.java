/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 *   Ernie R. Rael
 ******************************************************************************/
package com.nqadmin.swingset;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.sql.RowSet;
import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.datasources.RowSetOps;
import com.nqadmin.swingset.models.SimpleComboListSwingModels;
import com.nqadmin.swingset.utils.SSUtils;

// SSDataGrid.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * SSDataGrid provides a way to display information from a database in a table
 * format (aka "spreadsheet" or "datasheet" view). The SSDataGrid takes a
 * RowSet as a source of data. It also provides different cell renderers
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
 * underlying RowSet and supply a primary key for that column when a new row
 * is being added.
 * <p>
 * While using the headers always set them before you set the RowSet.
 * Otherwise the headers will not appear.
 * <p>
 * Also if you are using column names rather than column numbers for different
 * function you have to call them only after setting the RowSet. Because
 * SSDataGrid uses the RowSet to convert the column names to column numbers.
 * If you specify the column numbers you can do before or after setting the
 * RowSet, it does not matter.
 * <p>
 * You can simply remember this order 1.Set the headers 2.Set the RowSet 3.Any
 * other function calls.
 * <pre>
 * Simple Example:
 *
 *{@code
 * // SET THE HEADER BEFORE SETTING THE SSROWSET
 * 	dataGrid.setHeaders(new String[]{"Part Name", "Color Code", " Weight", "City"});
 * 	dataGrid.setRowSet(rowSet);
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
					return true;
				}
				return 1;
			}
			if (columnClass == java.sql.Types.BOOLEAN) {
				return false;
			}
			return 0;
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
				if (((Integer) _value) != 0) {
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
				if (((Integer) _value) != 0) {
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
		final int tmpClickCountToStart = 2;
		transient final Object[] items;
		transient final Object[] underlyingValues;

		/** {@inheritDoc} */
		public ComboEditor(final Object[] _items, final Object[] _underlyingValues) {
			super(new GridComboEditorComboBox());
			// TODO: copy the arrays? Or just agree that they are never modified.
			//		 Could use guava immutable then not worry about it.
			items = _items;
			underlyingValues = _underlyingValues;

			getComponent().setModel(new GridComboModels().getComboModel());
		}

		/** {@inheritDoc} */
		@Override
		@SuppressWarnings("NonPublicExported")
		final public GridComboEditorComboBox getComponent() {
			return (GridComboEditorComboBox) super.getComponent();
		}

		/** {@inheritDoc} */
		@Override
		public Object getCellEditorValue() {

			final GridComboModels.GridComboItem item = getComponent().getGridSelItem();

			// TODO: -1 seems weird, why not 0?
			if(item == null) {
				return underlyingValues != null ? underlyingValues[0] : -1;
			}

			logger.trace(() -> String.format("Item %s:%s",
					item.getElem(0), item.getElem(1)));
			return item.getElem(1);
		}

		/** {@inheritDoc} */
		protected int getIndexOf(final Object _value) {
			if (underlyingValues == null) {
				// IF THE VALUE IS NULL THEN SET THE DISPLAY ON THE COMBO TO BLANK (INDEX -1)
				// TODO: does this have to be null compatible?
				if (_value == null) {
					return -1;
				}
				// TODO: Could the following extract a class cast exception?
				//		 Guess not, since null underlyingVals means int
				return ((Integer) _value);
			}
			for (int i = 0; i < underlyingValues.length; i++) {
				if (underlyingValues[i].equals(_value)) {
					return i;
				}
			}

			return -1;
		}

		/** {@inheritDoc} */
		@Override
		public Component getTableCellEditorComponent(final JTable _table, final Object _value, final boolean _selected, final int _row,
				final int _column) {

			final JComboBox<?> comboBox = getComponent();
			comboBox.setSelectedIndex(getIndexOf(_value));
			return comboBox;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isCellEditable(final EventObject event) {
			if (event instanceof MouseEvent) {
				return ((MouseEvent) event).getClickCount() >= tmpClickCountToStart;
			}
			return true;
		}

		/** Simple combox model that maps each combobox item to
		 * the index into this ComboEditor's items array.
		 */
		private final class GridComboModels extends SimpleComboListSwingModels {
			
			private GridComboModels() {
				super(2, new ArrayList<>(items.length));
				for (int i = 0; i < items.length; i++) {
					getRemodel().add(new GridComboItem(i));
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public ComboBoxModel<GridComboItem> getComboModel() {
				return (ComboBoxModel<GridComboItem>) super.getComboModel();
			}
			
			class GridComboItem implements ListItem0, Cloneable {
				private final int listIdx;

				public GridComboItem(int _listIdx) { listIdx = _listIdx; }

				@Override
				public Object getElem(int index) {
					return index == 0 ? items[listIdx]
							: underlyingValues != null ? underlyingValues[listIdx] : listIdx;
				}
				@Override public Object clone() throws CloneNotSupportedException {
					return super.clone(); }
				@Override public String toString() { return items[listIdx].toString(); }
			}
		}
	}

	@SuppressWarnings("serial")
	private final class GridComboEditorComboBox
			extends JComboBox<ComboEditor.GridComboModels.GridComboItem> {
		public ComboEditor.GridComboModels.GridComboItem getGridSelItem() {
			return (ComboEditor.GridComboModels.GridComboItem) super.getSelectedItem();
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
		
		transient Object[] displayValues = null;
		transient Object[] underlyingValues = null;

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
		transient Constructor<?> constructor;

		/**
		 * Value of the editor.
		 */
		// TODO: get rid of value, see getCellEditorValue/stopCellEditing
		transient Object value;

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
		// TODO: get rid of value, put the stop Cell Editing code here.
		@Override
		public Object getCellEditorValue() {
			return value;
		}

		@Override
		@SuppressWarnings({"UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch"})
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
		@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
		public boolean stopCellEditing() {

			final String s = (String) super.getCellEditorValue();

			if (s.trim().isEmpty()) {
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
	private static Logger logger = SSUtils.getLogger();

	/**
	 * Unique serial ID
	 */
	private static final long serialVersionUID = -7924790696188174770L;

	/**
	 * Variable to indicate if rows can be deleted.
	 */
	private boolean allowDeletion = true;

	/**
	 * Variable to indicate if execute() should be called on the RowSet.
	 */
	private boolean callExecute = true;

	/**
	 * Minimum width of the columns in the data grid.
	 */
	private int columnWidth = 100;

	/**
	 * Column numbers that have to be hidden.
	 */
	private transient List<Integer> hiddenColumnsList = Collections.emptyList();

	/**
	 * Variable to indicate if the data grid will display an additional row for
	 * inserting new rows.
	 */
	private boolean insertion = true;

	/**
	 * Variable indicating that sorting, by clicking on column header,
	 * is allowed.
	 */
	private boolean sorting = false;

	/**
	 * Keep rowSorter state here while it's disabled by insertion row.
	 * Should be null if sorting not enabled.
	 */
	private transient List<? extends RowSorter.SortKey> savedRowSorterKeys;
	private transient RowFilter<SSTableModel,Integer> savedRowFilter;

	/**
	 * Component where messages should be popped up.
	 */
	private Component messageWindow = null;

	/**
	 * Scrollpane used to scroll datagrid.
	 */
	private JScrollPane scrollPane = null;

	/**
	 * RowSet from which component will get/set values.
	 */
	transient private RowSet rowSet = null;

	/**
	 * Constructs an empty data grid.
	 */
	public SSDataGrid() {
		super(new SSTableModel());
		init();
	}

	/**
	 * Constructs a data grid with the data source set to the given RowSet.
	 *
	 * @param _rowSet RowSet from which values have to be retrieved.
	 */
	public SSDataGrid(final RowSet _rowSet) {
		this();
		rowSet = _rowSet;
		bind();
	}

	/**
	 * Cast model for use.
	 * @return the model
	 */
	@Override
	public final SSTableModel getModel() {
		return (SSTableModel) super.getModel();
	}

	/**
	 * Create row sorter as needed.
	 * @param _dataModel 
	 * @throws IllegalArgumentException if would change SSTableModel
	 */
	@Override
	public void setModel(TableModel _dataModel) {
		// TODO: Support setModel to change SSTableModel?
		if(getModel() instanceof SSTableModel)
			throw new IllegalArgumentException("Can not change SSTableModel");
		super.setModel(_dataModel);
	}

	private class Sorter extends TableRowSorter<SSTableModel> {
		
		public Sorter(SSTableModel model) {
			super(model);
		}
		
		/** Cycle: {@literal ASC --> DESC --> UNSORTED --> ASC} ... */
		@Override
		public void toggleSortOrder(int column) {
			List<SortKey> keys = new ArrayList<>(getSortKeys());
			// The primary sorting key may get some special handling
			if(!keys.isEmpty() && keys.get(0).getColumn() == column) {
				if(keys.get(0).getSortOrder() == SortOrder.DESCENDING) {
					// cycle from descending no sort on this key
					if(Boolean.TRUE) {
						// Like removing all keys.
						// Next toggle restores sorts.
						keys.set(0, new SortKey(column, SortOrder.UNSORTED));
					} else {
						// Remove the primary key, any remaining keys will sort.
						// Like backing out key by key.
						keys.remove(0);
					}
					setSortKeys(keys);
					return;
				}
			}
			super.toggleSortOrder(column);
		}
	}

	/** Note: toggling insertion on/off/on/off does not create new sorter. */
	// TODO: both setInsertion/setSorting toggle; is that the right thing?
	private void checkCreateAddSorter(boolean clearFirst) {
		logger.debug(() -> String.format("clear: %b, sorting %b, insert %b",
					 clearFirst, getSorting(), getInsertion()));
		if(clearFirst || !getSorting()) {
			RowSorter<?> rowSorter = getRowSorter();

			if(rowSorter != null) // clear column sorted indicators
				rowSorter.setSortKeys(null);
			setRowSorter(null);
			savedRowSorterKeys = null;
			savedRowFilter = null;
		}
		if(!getSorting())
			return;

		// sorting's enabled, save or restore state as needed

		if(getInsertion()) { // save rowSorter state: keys, filter
			RowSorter<?> rowSorter = getRowSorter();
			if(rowSorter == null) {
				savedRowSorterKeys = null; // to be sure
				savedRowFilter = null;
				return;
			}
			// make sure the types are OK
			if(!(rowSorter instanceof Sorter)
					|| !(rowSorter.getModel() instanceof SSTableModel)) {
				logger.error(() -> "Wrong sorter type: " + rowSorter);
				return;
			}
			// cast to correct types
			@SuppressWarnings("unchecked")
			Sorter sorter = (Sorter) rowSorter;
			@SuppressWarnings("unchecked")
			RowFilter<SSTableModel, Integer> t = (RowFilter<SSTableModel, Integer>) sorter.getRowFilter();
			// and save the state
			savedRowFilter = t;
			savedRowSorterKeys = sorter.getSortKeys();
			// and clear current
			sorter.setSortKeys(null);
			sorter.setRowFilter(null);
			setRowSorter(null);
		} else { // restore sorting/filtering
			Sorter rowSorter= new Sorter(getModel());
			setRowSorter(rowSorter);
			rowSorter.setSortKeys(savedRowSorterKeys);
			rowSorter.setRowFilter(savedRowFilter);
		}
	}


	/**
	 * Initializes the data grid control. Collects metadata information about the
	 * given RowSet.
	 */
	// TODO: does this need to be overridable? It's called from constructor.
	protected final void bind() {

		try {
			// EXECUTE THE QUERY
			if (callExecute) {
				rowSet.execute();
			}

			// SPECIFY THE SSROWSET TO THE TABLE MODEL.
			getModel().setRowSet(rowSet);
			getModel().fireTableStructureChanged();

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
	 * the execute function call on the specified RowSet. (See FAQ for further
	 * details).
	 *
	 * @return true if execute function call has to be skipped else false
	 */
	public boolean getCallExecute() {
		return callExecute;
	}

	/**
	 * Return the enumeration of {@linkplain getColumnModel().getColumns()}
	 * as a List.
	 * @return the List
	 */
	public List<TableColumn> getColumnsList() {
		return Collections.list(getColumnModel().getColumns());
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
	public JScrollPane getComponent() {
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
		return getModel().getDefaultValue(_columnNumber);
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
	 *                      RowSet
	 */
	public Object getDefaultValue(final String _columnName) throws SQLException {
		//final int columnNumber = rowSet.getColumnIndex(_columnName);
		final int columnNumber = RowSetOps.getColumnIndex(rowSet,_columnName);
		return getModel().getDefaultValue(columnNumber - 1);
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
		if (hiddenColumnsList.isEmpty()) {
			return super.getSelectedColumns();
		}

		return IntStream.of(super.getSelectedColumns())
				.filter((selCol) -> !hiddenColumnsList.contains(selCol))
				.toArray();
	}

	/**
	 * Returns the RowSet being used to get the values.
	 *
	 * @return returns the RowSet being used.
	 */
	public RowSet getRowSet() {
		return rowSet;
	}

	/**
	 * Get whether or not sorting is enable.
	 * 
	 * @return true if sorting enabled
	 */
	public boolean getSorting() {
		return sorting;
	}

	/**
	 * Hides the columns specified in the hidden columns list.
	 */
	protected void hideColumns() {
		// Set width of hidden columns to 0
		// TODO: maybe remove the column instead, with ability to restore.

		for (TableColumn col : getColumnsList()) {
			if(hiddenColumnsList.contains(col.getModelIndex())) {
				// Set hidden column width to 0
				col.setMinWidth(0);
				col.setMaxWidth(0);
				col.setPreferredWidth(0);
			} else {
				// TODO: Does this belong in init()?
				if (getAutoResizeMode() == AUTO_RESIZE_OFF) {
					col.setPreferredWidth(columnWidth);
				}
				// Restore the column to some visibility
				if(col.getMinWidth() == 0 && col.getMaxWidth() <= 15) {
					// If the column used to be hidden, then restore it to defaults
					// TODO: not sure where the 15 comes from, but ...
					col.setMaxWidth(Integer.MAX_VALUE);
					col.setPreferredWidth(columnWidth);
					col.setMinWidth(15);
				}
			}
		}
	}

	/**
	 * Initialization code.
	 */
	protected final void init() {

		// FORCE JTABLE TO SURRENDER TO THE EDITOR WHEN KEYSTROKES CAUSE THE EDITOR TO
		// BE ACTIVATED
		setSurrendersFocusOnKeystroke(true);
		setDefaultEditor(Number.class, new DefaultEditor());
		setDefaultEditor(String.class, new DefaultEditor());
		setDefaultEditor(Object.class, new DefaultEditor());

		// Handle Control-X for selected rows deletion
		KeyStroke ks = KeyStroke.getKeyStroke("control released X");
		String key = "GridDeleteSelectedRows";
		getInputMap(WHEN_FOCUSED).put(ks, key);
		getActionMap().put(key, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!allowDeletion)
					return;
				final int numRows = getSelectedRowCount();
				if (numRows == 0) {
					return;
				}
				final int[] rows = getSelectedRows();
				// CONFIRM THE DELETION
				// TODO: why not: "JOptionPane.showConfirmDialog(this, ..."
				//		 Is this for a "confirm flag"?
				if (messageWindow != null) {
					final int returnValue = JOptionPane.showConfirmDialog(messageWindow,
							"You are about to delete " + rows.length + " rows. "
									+ "\nAre you sure you want to delete the rows?");
					if (returnValue != JOptionPane.YES_OPTION) {
						return;
					}
				}
				// Delete in reverse order so row numbers don't change while deleting
				for (int i = rows.length - 1; i >= 0; i--) {
					getModel().deleteRow(convertRowIndexToModel(rows[i]));
				}
				//updateUI(); // TODO: test 
			}
		});

		// TODO: why is this copy/paste thing needed?

		// CREATE AN INSTANCE OF KEY ADAPTER ADD PROVIDE THE PRESET GRID TO THE ADAPTER.
		// THIS IS FOR COPY AND PASTE SUPPORT
		final SSTableKeyAdapter keyAdapter = new SSTableKeyAdapter(this);
		keyAdapter.setAllowInsertion(true);

		// SPECIFY THE MESSAGE WINDOW TO WHICH THE TABLE MODEL HAS TO POP UP
		// ERROR MESSAGES.
		getModel().setMessageWindow(messageWindow);

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
	 * the execute function call on the specified RowSet. (See FAQ for further
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
		//final int column = rowSet.getColumnIndex(_column) - 1;
		final int column = RowSetOps.getColumnIndex(rowSet,_column) - 1;
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
		//final int column = rowSet.getColumnIndex(_column) - 1;
		final int column = RowSetOps.getColumnIndex(rowSet,_column) - 1;
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
		//final int tmpColumn = rowSet.getColumnIndex(_column) - 1;
		final int tmpColumn = RowSetOps.getColumnIndex(rowSet,_column) - 1;
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
	// TODO: Use List not Array
	public void setDefaultValues(final int[] _columnNumbers, final Object[] _values) {

		getModel().setDefaultValues(_columnNumbers, _values);
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
	 *                      RowSet
	 */
	// TODO: Use List not Array
	public void setDefaultValues(final String[] _columnNames, final Object[] _values) throws SQLException {

		int[] columnNumbers = null;

		if (_columnNames != null) {
			columnNumbers = new int[_columnNames.length];

			for (int i = 0; i < _columnNames.length; i++) {
				//columnNumbers[i] = rowSet.getColumnIndex(_columnNames[i]) - 1;
				columnNumbers[i] = RowSetOps.getColumnIndex(rowSet,_columnNames[i]) - 1;
			}
		}

		getModel().setDefaultValues(columnNumbers, _values);
	}

	/**
	 * Sets the header for the JTable. This function has to be called before setting
	 * the RowSet for SSDataGrid.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _headers array of string objects representing the header of each
	 *                 column.
	 */
	// TODO: Use List not Array
	public void setHeaders(final String[] _headers) {
		getModel().setHeaders(_headers);
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
	public void setHiddenColumns(List<Integer> _columnNumbers) {
		Objects.requireNonNull(_columnNumbers);

		hiddenColumnsList = new ArrayList<>(_columnNumbers);
		hideColumns();
	}
	/**
	 * Sets the column numbers that should be hidden. 
	 * @param _columnNumbers columms to hide
	 * @deprecated use setHiddenColumns(List)}
	 */
	@Deprecated
	public void setHiddenColumns(final int[] _columnNumbers) {
		if(_columnNumbers == null) {
			setHiddenColumns(Collections.emptyList());
			return;
		}
		setHiddenColumns(IntStream.of(_columnNumbers).boxed().collect(Collectors.toList()));
	}

	/**
	 * Sets the columns, by name, that should be hidden. The SSDataGrid sets the column
	 * width of these columns to 0. The columns are set to zero width rather than
	 * removing the column from the table. Thus preserving the column numbering. If a
	 * column is removed then the column numbers for columns after the removed
	 * column will change. Make sure that you specify the hidden column numbers in
	 * the uneditable column list.
	 * <p>
	 * Currently not a bean property since there is no associated variable.
	 *
	 * @param _columnNames array specifying the column names which should be hidden
	 * @throws SQLException	SQLException
	 */
	public void setHiddenColumnsByName(List<String> _columnNames) throws SQLException {
		Objects.requireNonNull(_columnNames);
		List<Integer> hiddenCols = new ArrayList<>(_columnNames.size());
		for(String colName : _columnNames) {
			hiddenCols.add(RowSetOps.getColumnIndex(rowSet, colName) - 1);
		}
		setHiddenColumns(hiddenCols);
	}
	/**
	 * Sets the column numbers that should be hidden.
	 * @param _columnNames names
	 * @throws SQLException 
	 * @deprecated use SetHiddenColumnsByName
	 */
	@Deprecated
	public void setHiddenColumns(final String[] _columnNames) throws SQLException {
		// TODO: does null need to be supported?
		if(_columnNames == null) {
			setHiddenColumnsByName(Collections.emptyList());
			return;
		}
		setHiddenColumnsByName(Arrays.stream(_columnNames).collect(Collectors.toList()));
	}

	/**
	 * Retrieve the hidden columns.
	 * @return an unmodifiable list of the columns
	 */
	public List<Integer> getHiddenColumns() {
		return Collections.unmodifiableList(hiddenColumnsList);
		// TODO: if need int[] getHiddenColumns
		//		int[] array = new int[list.size()];
		//		array = list.toArray(array);
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
		if(insertion) // remove sorter before events for adding row
			checkCreateAddSorter(false);
		getModel().setInsertion(_insertion);
		if(!insertion) // add sorter after events for removing rows
			checkCreateAddSorter(false);
		// TODO: moved fire after the changes; is that right?
		firePropertyChange("insertion", oldValue, insertion);
		//updateUI();
	}

	/**
	 * Sets the component on which error messages will be popped up. The error
	 * dialog will use this component as its parent component.
	 *
	 * @param _messageWindow the component that should be used when displaying error
	 *                       messages
	 */
	public void setMessageWindow(final Component _messageWindow) {
		// TODO: Why does this exist? Is it used?
		final Component oldValue = messageWindow;
		messageWindow = _messageWindow;
		firePropertyChange("messageWindow", oldValue, messageWindow);
		getModel().setMessageWindow(messageWindow);
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
		getModel().setPrimaryColumn(_columnNumber);
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
		//final int columnNumber = rowSet.getColumnIndex(_columnName) - 1;
		final int columnNumber = RowSetOps.getColumnIndex(rowSet,_columnName) - 1;
		
		getModel().setPrimaryColumn(columnNumber);
	}

	/**
	 * Sets the new RowSet for the combo box.
	 *
	 * @param _rowSet RowSet to which the combo has to update values.
	 */
	public void setRowSet(final RowSet _rowSet) {
		final RowSet oldValue = rowSet;
		rowSet = _rowSet;
		firePropertyChange("rowSet", oldValue, rowSet);
		bind();
	}

	/**
	 * Enable/disable sorting by column gesture;
	 * when true, always create new sorter.
	 * Note that if insertRow, then the sorter is not applied.
	 * @param _sorting 
	 */
	// TODO: support a getCreatedSorter to 
	//		 allow getting the sorter while insert row.
	//		 Not absolutely required, since can get the
	//		 sorter directly from the JTable; just turn off insert row.
	public void setSorting(boolean _sorting) {
		boolean oldValue = sorting;
		sorting = _sorting;
		checkCreateAddSorter(true);
			
		firePropertyChange("sorting", oldValue, sorting);
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
		getModel().setSSCellEditing(_cellEditing);
	}

	/**
	 * Set the implementation of DataGridHandler which can be use to handle row
	 * deletions and insertions
	 *
	 * @param _dataGridHandler implementation of SSDataGridHandler interface.
	 * @deprecated Use SSTableModel.setSSDataGridHandler
	 */
	@Deprecated
	public void setSSDataGridHandler(final SSDataGridHandler _dataGridHandler) {
		getModel().setSSDataGridHandler(_dataGridHandler);
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
		getModel().setSSDataValue(_dataValue);
	}

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
	// TODO: Use List not Array
	public void setUneditableColumns(final int[] _columnNumbers) {
		getModel().setUneditableColumns(_columnNumbers);
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
	// TODO: Use List not Array
	public void setUneditableColumns(final String[] _columnNames) throws SQLException {
		int[] columnNumbers = null;
		if (_columnNames != null) {
			columnNumbers = new int[_columnNames.length];

			for (int i = 0; i < _columnNames.length; i++) {
				//columnNumbers[i] = rowSet.getColumnIndex(_columnNames[i]) - 1;
				columnNumbers[i] = RowSetOps.getColumnIndex(rowSet,_columnNames[i]) - 1;
			}
		}

		getModel().setUneditableColumns(columnNumbers);
	}

} // end public class SSDataGrid extends JTable {
//  vi: ts=4 sw=4