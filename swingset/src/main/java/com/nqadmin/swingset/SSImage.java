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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

// SSImage.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to load, store, and display images stored in a database.
 */
public class SSImage extends JPanel implements SSComponentInterface {

	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column
	 */
	protected class SSImageListener implements ActionListener, Serializable {

		/**
		 * Unique serial ID
		 */
		private static final long serialVersionUID = -997068820028544504L;

		@Override
		public void actionPerformed(final ActionEvent ae) {

			ssCommon.removeRowSetListener();

			try {
				if (getRowSet() != null) {
					// FileInputStream inStream = null;
					File inFile = null;
					final JFileChooser fileChooser = new JFileChooser();
					if (fileChooser.showOpenDialog(btnUpdateImage) == JFileChooser.APPROVE_OPTION) {
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
							getRowSet().updateBytes(getBoundColumnName(), bytes);
							img = new ImageIcon(bytes);
							lblImage.setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
							lblImage.setIcon(img);
							lblImage.setText("");
							updateUI();
						}
					} else {
						return;
					}
				}
			} catch (final SQLException se) {
				logger.error(getColumnForLog() + ": SQL Exception.", se);
			} catch (final IOException ioe) {
				logger.error(getColumnForLog() + ": IO Exception.", ioe);
			}

			ssCommon.addRowSetListener();
		}

	} // end private class SSImageListener

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -2726746843832259767L;

	/**
	 * Button to update the image.
	 */
	protected JButton btnUpdateImage = new JButton("Update");

	/**
	 * ImageIcon to store the image.
	 */
	protected ImageIcon img;

	/**
	 * Label to display the image
	 */
	protected JLabel lblImage = new JLabel("No Picture");

	/**
	 * The preferred size of the image component.
	 */
	protected Dimension preferredSize = new Dimension(200, 200);

	/**
	 * Common fields shared across SwingSet components
	 */
	protected SSCommon ssCommon = new SSCommon(this);

	/**
	 * Component listener.
	 */
	protected SSImageListener ssImageListener;

	/**
	 * Construct a default SSImage Object.
	 */
	public SSImage() {
		// Note that call to parent default constructor is implicit.
		// super();
	}

	/**
	 * Constructs a SSImage Object bound to the specified column in the specified
	 * rowSet.
	 *
	 * @param _rowSet          - RowSet from/to which data has to be read/written
	 * @param _boundColumnName - column in the rowSet to which the component should
	 *                         be bound.
	 */
	public SSImage(final RowSet _rowSet, final String _boundColumnName) {
		this();
		bind(_rowSet, _boundColumnName);
	}

	/**
	 * Adds the label and button to the panel
	 */
	protected void addComponents() {
		setLayout(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		final JScrollPane scrollPane = new JScrollPane(lblImage, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(200, 180));
		btnUpdateImage.setPreferredSize(new Dimension(200, 20));
		add(scrollPane, constraints);
		constraints.gridy = 1;
		add(btnUpdateImage, constraints);
	}

	/**
	 * Removes the current image. The image is not removed from the underlying
	 * rowSet.
	 */
	public void clearImage() {
		lblImage.setIcon(null);
		lblImage.setText("No Picture");
		final Dimension dimension = getPreferredSize();
		lblImage.setPreferredSize(new Dimension((int) dimension.getWidth(), (int) dimension.getHeight() - 20));
		updateUI();
	}

	/**
	 * Method to allow Developer to add functionality when SwingSet component is
	 * instantiated.
	 * <p>
	 * It will actually be called from SSCommon.init() once the SSCommon data member
	 * is instantiated.
	 */
	@Override
	public void customInit() {
		// SET PREFERRED DIMENSIONS
		setPreferredSize(preferredSize);

		// ADD LABEL & BUTTON TO PANEL
		addComponents();
	}
	
	/**
	 * Returns the button that indicates a new image has been selected and accepted.
	 * <p>
	 * Getter is needed for action listener processing in SSCommon.
	 *
	 * @return button that indicates a new image has been selected and accepted
	 */
	public JButton getBtnUpdateImage() {
		return btnUpdateImage;
	}

	/**
	 * Returns the preferred size of the image component.
	 *
	 * @return returns preferred size of the image component
	 */
	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
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
	 * {@inheritDoc }
	 */
	@Override
	public SSImageListener getSSComponentListener() {
		if (ssImageListener == null) {
			ssImageListener = new SSImageListener();
		}
		return ssImageListener;
	}

	/**
	 * Sets the preferred size of the image component.
	 *
	 * @param _preferredSize - preferred size of the image component
	 */
	@Override
	public void setPreferredSize(final Dimension _preferredSize) {
		final Dimension oldValue = preferredSize;
		preferredSize = _preferredSize;
		firePropertyChange("preferredSize", oldValue, preferredSize);

		lblImage.setPreferredSize(
				new Dimension((int) _preferredSize.getWidth(), (int) _preferredSize.getHeight() - 20));
		btnUpdateImage.setPreferredSize(new Dimension((int) _preferredSize.getWidth(), 20));
		super.setPreferredSize(_preferredSize);
	}

	/**
	 * Sets the SSCommon data member for the current Swingset Component.
	 *
	 * @param _ssCommon shared/common SwingSet component data and methods
	 */
	@Override
	public void setSSCommon(final SSCommon _ssCommon) {
		ssCommon = _ssCommon;

	}

	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText().
	 * <p>
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed.
	 */
	@Override
	public void updateSSComponent() {

		try {
			final byte[] imageData = getRowSet().getRow() > 0 ? getRowSet().getBytes(getBoundColumnName()) : null;
			if (imageData != null) {
				logger.debug("{}: Setting non-null image.", () -> getColumnForLog());
				img = new ImageIcon(imageData);
				lblImage.setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
				lblImage.setText("");
			} else {
				logger.debug("{}: Setting null image.", () -> getColumnForLog());
				img = null;
				lblImage.setText("No Picture");
			}
		} catch (final SQLException se) {
			logger.error(getColumnForLog() + ": SQL Exception.", se);
			img = null;
		}

		lblImage.setIcon(img);

		// TODO Confirm updateUI is needed here.
		updateUI();

	}

}
