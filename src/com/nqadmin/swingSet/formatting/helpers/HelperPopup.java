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

package com.nqadmin.swingSet.formatting.helpers;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuListener;

import com.nqadmin.swingSet.datasources.SSConnection;
import com.nqadmin.swingSet.formatting.SSFormattedTextField;

/**
 * HelperPopup.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Used to set a helper popup for a SSFormattedTextField.
 */
public class HelperPopup extends JPopupMenu
		implements MouseListener, KeyListener, ActionListener, ListSelectionListener, PopupMenuListener, FocusListener {

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = 2615782240022599464L;
	private JPanel spane;
	private JPanel buttons;
	private JPanel tpane;
	private JButton searchButton;
	private JButton closeButton;
	private JButton refreshButton;
	private JButton helpButton;
	private JTextField searchText;

	private String table = null;
	private String dataColumn = null;
	private String listColumn = null;
	private String orderBy = null;

	private SSFormattedTextField target = null;
	private JScrollPane sc;
	private SSConnection connection = null;
	private SelectorListModel model = null;
	private SelectorList lista;

	/** Creates a new instance of HelperPopup */
	public HelperPopup() {

		// main panel
		this.spane = new JPanel();
		this.spane.setLayout(new BorderLayout());
		this.spane.setBorder(new javax.swing.border.TitledBorder(" Helper "));

		// search text panel
		this.tpane = new JPanel();
		this.tpane.setLayout(new BorderLayout());

		// button bar panel
		this.buttons = new JPanel();
		this.buttons.setLayout(new BoxLayout(this.buttons, BoxLayout.X_AXIS));

		// searchButton = new JButton("Search");
		// searchButton.addActionListener(this);

		// closeButton = new JButton("Close");
		// closeButton.addActionListener(this);

		this.refreshButton = new JButton("Refresh");
		this.refreshButton.addActionListener(this);

		// helpButton = new JButton("Help");
		// helpButton.addActionListener(this);

		this.searchText = new JTextField();
		this.searchText.setColumns(20);
		this.searchText.addActionListener(this);
		this.searchText.addFocusListener(this);
		// tpane.add(searchText, BorderLayout.NORTH);

		// buttons.add(searchButton);
		// buttons.add(closeButton);
		this.buttons.add(this.searchText);
		this.buttons.add(this.refreshButton);
		// buttons.add(helpButton);

		this.tpane.add(this.buttons, BorderLayout.SOUTH);

		this.lista = new SelectorList();
		this.lista.addKeyListener(this);
		this.lista.addMouseListener(this);
		this.lista.setVisibleRowCount(10);

		this.sc = new JScrollPane(this.lista);

		this.spane.add(this.tpane, BorderLayout.NORTH);
		this.spane.add(this.sc, BorderLayout.CENTER);

		this.add(this.spane);
		this.addPopupMenuListener(this);
		this.setEnabled(true);
		this.setFocusable(true);
		this.addFocusListener(this);
		this.pack();
	}

	/**
	 * Sets the list model to be used
	 * 
	 * @param _model - list model to be used
	 */
	// TODO confirm that .setFilteredEdit should be called for _model parameter
	public void setModel(SelectorListModel _model) {
		this.model = _model;
		this.lista.setModel(_model);
		_model.setFilterEdit(this.searchText);
	}

	/**
	 * Sets the text field for which this helper popup is being used.
	 * 
	 * @param _target - text field for which this helper popup is being used
	 */
	public void setTarget(final SSFormattedTextField _target) {
		this.target = _target;

		// if (target != null) this.setPreferredSize(new
		// Dimension(target.getWidth(),300));

	}

	/**
	 * Sets the table name from which data should be pulled
	 * 
	 * @param _table - table name to be used in the query to pull the data
	 */
	public void setTable(final String _table) {
		this.table = _table;
		createHelper();
	}

	/**
	 * Sets the column name whose values should be used as underlying/bound values
	 * 
	 * @param _dataColumn - column name whose values should be used as
	 *                    underlying/bound values
	 */
	public void setDataColumn(final String _dataColumn) {
		this.dataColumn = _dataColumn;
		createHelper();
	}

	/**
	 * Sets the column name whose values should be used for displaying.
	 * 
	 * @param _listColumn - column name whose values should be used for displaying.
	 */
	public void setListColumn(final String _listColumn) {
		this.listColumn = _listColumn;
		createHelper();
	}

	/**
	 * Sets the column name to used for ordering the items
	 * 
	 * @param _orderBy - column name to used for ordering the items
	 */
	public void setOrderBy(final String _orderBy) {
		this.orderBy = _orderBy;
		createHelper();
	}

	/**
	 * Sets the SSConnection object to used for database access.
	 * 
	 * @param _connection - connection object to be used for accessing the database
	 */
	public void setConnection(final SSConnection _connection) {
		this.connection = _connection;

		try {
			_connection.createConnection();
		} catch (java.lang.ClassNotFoundException nfe) {
			// do nothing
		} catch (java.lang.Exception ex) {
			// do nothing
		}
		createHelper();
	}

	/**
	 * 
	 */
	private void createHelper() {

		if (this.connection == null || this.table == null || this.dataColumn == null || this.listColumn == null)
			return;

		try {
			this.connection.createConnection();
		} catch (java.lang.ClassNotFoundException nfe) {
			// do nothing
		} catch (java.lang.Exception ex) {
			// do nothing
		}

		this.model = new SelectorListModel(this.connection, this.table, this.dataColumn, this.listColumn, this.orderBy);
		this.model.refresh();
		this.model.setFilterEdit(this.searchText);

		this.lista.setModel(this.model);
		this.lista.getSelectionModel().addListSelectionListener(this);
		this.lista.setVisibleRowCount(10);
		pack();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(final java.awt.event.KeyEvent _event) {
		System.out.println("keyPressed");
		System.out.println("KeyCode = " + KeyEvent.getKeyText(_event.getKeyCode()));

		if (_event.getKeyCode() == KeyEvent.VK_ENTER) {
			this.setVisible(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(final java.awt.event.KeyEvent _event) {
		System.out.println("keyTyped");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(final java.awt.event.KeyEvent _event) {
		System.out.println("keyReleased");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(final java.awt.event.MouseEvent _event) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(final java.awt.event.MouseEvent _event) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(final java.awt.event.MouseEvent _event) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(final java.awt.event.MouseEvent _event) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(final java.awt.event.MouseEvent _event) {
		System.out.println("mouseClicked");

		if (_event.getClickCount() == 2) {
			this.setVisible(false);
		}

		if (_event.getClickCount() == 1) {
			this.setVisible(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final java.awt.event.ActionEvent _event) {

		if (_event.getSource().equals(this.searchButton)) {
			System.out.println("searchButton");
			search(_event);
		}
		if (_event.getSource().equals(this.closeButton)) {
			System.out.println("closeButton");
			this.setVisible(false);
		}
		if (_event.getSource().equals(this.refreshButton)) {
			System.out.println("refreshButton");

			this.model = new SelectorListModel(this.connection, this.table, this.dataColumn, this.listColumn,
					this.orderBy);
			this.model.refresh();
			this.model.setFilterEdit(this.searchText);

			this.lista.setModel(this.model);
			this.lista.updateUI();
			this.lista.getSelectionModel().addListSelectionListener(this);
			this.lista.setVisibleRowCount(10);
		}

		if (_event.getSource().equals(this.helpButton)) {
			int index = this.lista.getSelectedIndex();
			this.lista.ensureIndexIsVisible(index);
		}

		if (_event.getSource().equals(this.searchText)) {
			search(_event);
		}
	}

	/**
	 * @param _event
	 */
	private void search(final java.awt.event.ActionEvent _event) {

		javax.swing.JTextField s;
		int j, n;

		// text to find
		String toFind = null;

		s = ((javax.swing.JTextField) _event.getSource());
		toFind = s.getText().toUpperCase().trim();
		s.setText(toFind);
		System.out.println("Texto a buscar : " + toFind);
		n = this.lista.getModel().getSize();

		/**
		 * Here implements list search logic.
		 */

		for (j = 0; j < n; j++) {
			String texto = this.lista.getModel().getElementAt(j).toString().toUpperCase();
			System.out.println("Comparando con " + texto);
			if (texto.startsWith(toFind)) {
				this.lista.setSelectedIndex(j);
				this.lista.ensureIndexIsVisible(j);
				this.lista.requestFocus();
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.
	 * ListSelectionEvent)
	 */
	@Override
	public void valueChanged(final javax.swing.event.ListSelectionEvent _event) {

		// 2019-02-23-BP: this method doesn't seem to do anything
		return;

		/*
		 * int desde; int hasta; int selected;
		 * 
		 * if (_event.getValueIsAdjusting() == false) { desde = e.getFirstIndex(); hasta
		 * = e.getLastIndex();
		 * 
		 * DefaultListSelectionModel lm = ((DefaultListSelectionModel)e.getSource());
		 * 
		 * selected = lm.getLeadSelectionIndex();
		 * 
		 * //SelectorElement se1 = (SelectorElement)
		 * (lista.getModel().getElementAt(desde));
		 * 
		 * //SelectorElement se2 = (SelectorElement)
		 * (lista.getModel().getElementAt(hasta));
		 * 
		 * //SelectorElement se3 = (SelectorElement)
		 * (lista.getModel().getElementAt(selected));
		 * 
		 * }
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.PopupMenuListener#popupMenuWillBecomeVisible(javax.swing.
	 * event.PopupMenuEvent)
	 */
	@Override
	public void popupMenuWillBecomeVisible(final javax.swing.event.PopupMenuEvent _event) {
		// Object current = target.getValue();
		// Object dataval = null;

		this.searchText.requestFocusInWindow();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(javax.swing.
	 * event.PopupMenuEvent)
	 */
	@Override
	public void popupMenuWillBecomeInvisible(final javax.swing.event.PopupMenuEvent _event) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.PopupMenuListener#popupMenuCanceled(javax.swing.event.
	 * PopupMenuEvent)
	 */
	@Override
	public void popupMenuCanceled(final javax.swing.event.PopupMenuEvent _event) {
		System.out.println("popupMenuCanceled();");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JPopupMenu#show(java.awt.Component, int, int)
	 */
	@Override
	public void show(final java.awt.Component invoker, final int x, final int y) {
		System.out.println("show(" + x + "," + y + ")");
		this.setSize(this.target.getWidth(), this.searchText.getHeight() * 15);
		this.searchText.requestFocusInWindow();
		super.show(invoker, x, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(final java.awt.event.FocusEvent _event) {
		System.out.println("focusLost");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(final java.awt.event.FocusEvent _event) {
		System.out.println("focusGained");
	}
}

/*
 * $Log$ Revision 1.7 2006/04/21 19:09:17 prasanth Added CVS tags & some
 * comments
 *
 */
