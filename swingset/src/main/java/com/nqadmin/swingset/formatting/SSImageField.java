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
 *   Ernie R. Rael
 ******************************************************************************/
package com.nqadmin.swingset.formatting;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.RowSetListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.SSDataNavigator;
import com.nqadmin.swingset.datasources.SSRowSet;

/**
 * SSImageField.java
 * <p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * <p>
 * Used to link a JPanel to an image column in a database.
 * <p>
 * Other than some function key handling, which is likely outside the scope of
 * SwingSet and should be customized at the application level, this class appears to
 * mostly duplicate SSImage.
 * <p>
 * There were methods to resize an image to make a thumbnail (Rescale, Thumbnail), but
 * the data members that they manipulated did not appear to be exposed anywhere.
 * <p>
 * SSImageField does not extend SSFormattedText field like the other classes in this package.
 * <p>
 * @deprecated Starting in 4.0.0+ use {@link com.nqadmin.swingset.SSImage} instead.
 */
@Deprecated
public class SSImageField extends JPanel implements RowSetListener, KeyListener, ComponentListener {

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();
	private static final long serialVersionUID = 889303691158522232L;
	private int colType = -99;
	protected String columnName = null;
	protected ImageIcon fullIcon;
	protected JButton getButton;
	private JButton imageButton;
	protected byte[] imageBytes;
	private SSDataNavigator navigator = null;
	private ImageIcon nullIcon;
	protected SSRowSet rowset = null;

	private ImageIcon scaledIcon;

	/** Creates a new instance of SSImageField */
	public SSImageField() {
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

		addKeyListener(this);

		addComponentListener(this);

		setLayout(new BorderLayout());

		imageButton = new JButton();
		imageButton.setFocusable(false);

		imageButton.setIconTextGap(0);
		imageButton.setBorder(null);
		imageButton.setMargin(new Insets(0, 0, 0, 0));
		imageButton.setText("");
		nullIcon = new ImageIcon(getClass().getResource("/com/nqadmin/swingSet/formatting/image.png"));
		fullIcon = nullIcon;
		imageButton.setIcon(fullIcon);
		add(imageButton, BorderLayout.CENTER);
		validate();

//        scrollPane = new JScrollPane(imageButton);
//        add(scrollPane, BorderLayout.CENTER);

		getButton = new JButton("from file ...");
		getButton.setFocusable(false);

		getButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ae) {
				try {
					// if (rowset != null) {
					// FileInputStream inStream = null;
					File inFile = null;
					final JFileChooser fileChooser = new JFileChooser();
					if (fileChooser.showOpenDialog(getButton) == JFileChooser.APPROVE_OPTION) {
						inFile = fileChooser.getSelectedFile();
						try (FileInputStream inStream = new FileInputStream(inFile)) {
							final int totalLength = (int) inFile.length();
							final byte[] bytes = new byte[totalLength];
							int bytesRead = inStream.read(bytes);
							while (bytesRead < totalLength) {
								final int read = inStream.read(bytes, bytesRead, totalLength - bytesRead);
								if (read == -1) {
									break;
								}
								bytesRead += read;
							}
							// inStream.close();
							// rowset.updateBytes(columnName, bytes);
							imageBytes = bytes;
							fullIcon = new ImageIcon(bytes);
						}
						try {
							rowset.updateBytes(columnName,
									imageBytes);
						} catch (final SQLException se) {
							// do nothing
						}
						Rescale();
					} else {
						return;
					}
					// }
					// }catch(SQLException se){
					// se.printStackTrace();
				} catch (final IOException ioe) {
					logger.error(getColumnForLog() + ": IO Exception.", ioe);
				}
			}
		});
		add(getButton, BorderLayout.SOUTH);
	}

	/**
	 * Binds the component to the specified column in the given SSRowSet
	 */
	private void bind() {

		if (columnName == null) {
			return;
		}
		if (rowset == null) {
			return;
		}

		try {
			colType = rowset.getColumnType(columnName);
		} catch (final java.sql.SQLException sqe) {
			logger.error(getColumnForLog() + ": SQL Exception.", sqe);
		}
		rowset.addRowSetListener(this);
		DbToFm();
	}


	/**
	 * Sets the SSRowSet and column name to which the component is to be bound.
	 *
	 * @param _sSRowSet   datasource to be used.
	 * @param _columnName Name of the column to which this check box should be bound
	 */
	public void bind(final SSRowSet _sSRowSet, final String _columnName) {
		rowset = _sSRowSet;
		columnName = _columnName;
		bind();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.
	 * ComponentEvent)
	 */
	@Override
	public void componentHidden(final java.awt.event.ComponentEvent _event) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.
	 * ComponentEvent)
	 */
	@Override
	public void componentMoved(final java.awt.event.ComponentEvent _event) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.
	 * ComponentEvent)
	 */
	@Override
	public void componentResized(final java.awt.event.ComponentEvent _event) {
		Rescale();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.
	 * ComponentEvent)
	 */
	@Override
	public void componentShown(final java.awt.event.ComponentEvent _event) {
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

	/**
	 * Gets the value from the rowset and sets it to the component
	 */
	private void DbToFm() {

		try {
			if (rowset.getRow() == 0) {
				return;
			}

			switch (colType) {

			case java.sql.Types.BINARY:
				imageBytes = rowset.getBytes(columnName);

				if (imageBytes == null) {
					fullIcon = nullIcon;
					Rescale();
					break;
				}

				if (imageBytes.length > 0) {
					fullIcon = new ImageIcon(imageBytes);
				} else {
					fullIcon = nullIcon;
				}
				Rescale();
				break;

			default:
				break;
			}
		} catch (final java.sql.SQLException sqe) {
			logger.error(getColumnForLog() + ": SQL Exception.", sqe);
		}
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
		return columnName;
	}

	/**
	 * Returns the SSDataNavigator object being used.
	 *
	 * @return returns the SSDataNavigator object being used.
	 * @deprecated Use {@link #getSSDataNavigator()} instead.
	 **/
	@Deprecated
	public SSDataNavigator getNavigator() {
		return getSSDataNavigator();
	}

	/**
	 * SSRowSet object being used to get/set the bound column value
	 *
	 * @return - returns the SSRowSet object being used to get/set the bound column
	 *         value
	 * @deprecated Use {@link #getSSRowSet()} instead.
	 */
	@Deprecated
	public SSRowSet getRowSet() {
		return getSSRowSet();
	}

	/**
	 * Returns the SSDataNavigator object being used.
	 *
	 * @return returns the SSDataNavigator object being used.
	 */
	public SSDataNavigator getSSDataNavigator() {
		return navigator;
	}

	/**
	 * SSRowSet object being used to get/set the bound column value
	 *
	 * @return - returns the SSRowSet object being used to get/set the bound column
	 *         value
	 */
	public SSRowSet getSSRowSet() {
		return rowset;
	}

	/**
	 * Catch severals keys, to implement some forms functionality (To be donevent).
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
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
			logger.debug("{}: F4", () -> getColumnForLog());
		}

		if (_event.getKeyCode() == KeyEvent.VK_F5) {
			logger.debug("{}: F5 = PROCESS", () -> getColumnForLog());
			navigator.doCommitButtonClick();
		}

		if (_event.getKeyCode() == KeyEvent.VK_F6) {
			logger.debug("{}: F6 = DELETE", () -> getColumnForLog());
			navigator.doDeleteButtonClick();
		}

		if (_event.getKeyCode() == KeyEvent.VK_F8) {
			logger.debug("{}: F8", () -> getColumnForLog());
			navigator.doUndoButtonClick();
		}

		if (_event.getKeyCode() == KeyEvent.VK_END) {
			logger.debug("{}: END", () -> getColumnForLog());
		}

		if (_event.getKeyCode() == KeyEvent.VK_DELETE) {
			logger.debug("{}: DELETE", () -> getColumnForLog());
		}

		if (_event.getKeyCode() == KeyEvent.VK_HOME) {
			logger.debug("{}: HOME", () -> getColumnForLog());
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

	/**
	 *
	 */
	protected void Rescale() {
		if (fullIcon != null) {
			if (!fullIcon.equals(nullIcon)) {
				scaledIcon = Thumbnail(fullIcon.getImage());
			} else {
				scaledIcon = fullIcon;
			}
			imageButton.setIcon(scaledIcon);
			updateUI();
		}
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
	 * Column name in the SSRowSet to which this component will be bound to
	 *
	 * @param _columnName - column name in the SSRowSet to which this component will
	 *                    be bound to
	 */
	public void setColumnName(final String _columnName) {
		columnName = _columnName;
		bind();
	}

	/**
	 * Sets the SSDataNavigator being used to navigate the SSRowSet This is needed
	 * only if you want to include the function keys as short cuts to perform
	 * operations on the DataNavigator like saving the current row/ undo changes/
	 * delete current row. <b><i>The functionality for this is not yet
	 * finalized so try to avoid using this </i></b>
	 *
	 * @param _navigator - SSDataNavigator being used to navigate the SSRowSet
	 * @deprecated Use {@link #setSSDataNavigator(SSDataNavigator _navigator)}
	 *             instead.
	 **/
	@Deprecated
	public void setNavigator(final SSDataNavigator _navigator) {
		setSSDataNavigator(_navigator);
	}

	/**
	 * Sets the SSRowSet object to be used to get/set the value of the bound column
	 *
	 * @param _rowset - SSRowSet object to be used to get/set the value of the bound
	 *                column
	 * @deprecated Use {@link #setSSRowSet(SSRowSet _rowset)} instead.
	 */
	@Deprecated
	public void setRowSet(final SSRowSet _rowset) {
		setSSRowSet(_rowset);
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
		navigator = _navigator;
		setSSRowSet(_navigator.getSSRowSet());
		bind();
	}

	/**
	 * Sets the SSRowSet object to be used to get/set the value of the bound column
	 *
	 * @param _rowset - SSRowSet object to be used to get/set the value of the bound
	 *                column
	 */
	public void setSSRowSet(final SSRowSet _rowset) {
		rowset = _rowset;
		bind();
	}

	/**
	 * Creates a image icon from the specified image
	 *
	 * @param _image - image to be used to create image icon
	 * @return return the image icon created
	 */
	private ImageIcon Thumbnail(final Image _image) {
		double scale, fw, fh;
		int wi, hi;
		int wo, ho;
		int ws, hs;
		Image scaled;

		imageButton.setIcon(null);
		validate();

		wi = imageButton.getWidth();
		hi = imageButton.getHeight();

		wo = _image.getWidth(this);
		ho = _image.getHeight(this);

		fw = (double) wi / (double) wo;
		fh = (double) hi / (double) ho;

		if (fw > fh) {
			scale = fh;
		} else {
			scale = fw;
		}

		ws = (int) (scale * wo);
		hs = (int) (scale * ho);

		if ((wi == 0) && (hi == 0)) {
			ws = wo;
			hs = ho;
		}
		scaled = _image.getScaledInstance(ws, hs, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}

}
