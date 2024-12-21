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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import com.nqadmin.swingset.decorators.BorderDecorator;

import static java.lang.System.Logger.Level.*;

import com.nqadmin.swingset.decorators.Decorator;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.utils.SSUtils.sf;

// SSImage.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Used to load, store, and display images stored in a database.
 */
// TODO: SSImage make all the load/store buttons/capabilities optional.
@SuppressWarnings("serial")
public class SSImage extends JPanel implements SSComponentInterface
{
	// TODO: try to get this initialized
	private String fName = "";
	/**
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column
	 */
	protected class SSImageListener implements ActionListener {

		/** {@inheritDoc} */
		@Override
		public void actionPerformed(final ActionEvent ae)
		{
			if(!checkRowOK())
				return;

			getSSCommon().removeRowSetListener();

			try {
				if (getRowSet() != null) {
					// FileInputStream inStream = null;
					File inFile;
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
							fName = inFile.getPath();
							// TODO: why is updateUI here?
							updateUI();
						}
					} else {
						return;
					}
				}
			} catch (final SQLException se) {
				logger.log(Level.ERROR, getColumnForLog() + ": SQL Exception.", se);
			} catch (final IOException ioe) {
				logger.log(Level.ERROR, getColumnForLog() + ": IO Exception.", ioe);
			}

			getSSCommon().addRowSetListener();
		}

	} // end private class SSImageListener

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = SSUtils.getLogger();

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
	private final SSCommon ssCommon;

	/**
	 * Construct a default SSImage Object.
	 */
	public SSImage() {
		ssCommon = finishSSCommon();
	}

	// TODO: Why do decorators interfere with SSImage?
	// In particular the following lines from BorderDecorator
	// cause a miniscule scrollpane. Maybe some kind of decorator wrapper?
	//		jc().setBorder(jc().isFocusOwner() ? focusBorder : standardBorder);
	//		jc().setForeground(textColor != null ? textColor : Color.BLACK);
	
	// TODO: This is a workaround because if default decorator is used
	//		 then the SSImage doesn't display properly.
	/**
	 * Highlight the update button when this component gets focus.
	 * {@inheritDoc }
	 */
	@Override
	public Decorator createDefaultDecorator() {
		Decorator decorator = SSComponentInterface.super.createDefaultDecorator();
		if (!(decorator instanceof BorderDecorator))
			return decorator;

		return new BorderDecorator() {
			@Override
			protected Border getBorder(BorderState state)
			{
				// The default border when just running the demo is
				// the CompoundBorder: [[3,3,3,3],[2,14,2,14]].
				Color color = getBorderColor(state);
				if (color == null)
					return defaultBorder;

				if (jc().getBorder() instanceof CompoundBorder cb) {
					return BorderDecorator.lineEmpty_empty(
							cb.getOutsideBorder().getBorderInsets(jc()),
							cb.getInsideBorder().getBorderInsets(jc()),
							color);
				}
				return empty_line(jc().getInsets(), color);
			}

			@Override
			protected JComponent jc()
			{
				return btnUpdateImage;
			}
			
			@Override
			protected Component fcomp()
			{
				return btnUpdateImage;
			}
		};
	}

	/**
	 * Constructs a SSImage Object bound to the specified column in the specified
	 * rowSet.
	 *
	 * @param _rowSet          - RowSet from/to which data has to be read/written
	 * @param _boundColumnName - column in the rowSet to which the component should
	 *                         be bound.
	 */
	public SSImage(final RowSet _rowSet, final String _boundColumnName)
	{
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
	 * Add custom button for loading image from disk saving to database.
	 * Add some preferredSize handling.
	 */
	// TODO: remove the prefered size stuff, let programmer handle it.
	// TODO: make custom components optional, not builtin.
	@Override
	public void customInit()
	{
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

	/** {@inheritDoc } */
	@Override
	public SSImageListener getSSComponentListener() {
		return new SSImageListener();
	}

	/**
	 * Sets the preferred size of the image component.
	 *
	 * @param _preferredSize - preferred size of the image component
	 */
	// TODO: listen to preferredsize property to adjust btnUpdateImage.
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
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText().
	 * <p>
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed.
	 */
	@Override
	public void updateSSComponent() {

		// TODO: If CachedRowSet, BLOBs don't work. As a convenience could,
		//		 grab a connection, find the primary keys, and read the BLOB.

		// TODO: Should getBoundColumnObject() be used here?
		//		 Seems like it, it's used just about everywhere else.

		try {
			final byte[] imageData = getRowSet().getRow() > 0 ? getRowSet().getBytes(getBoundColumnName()) : null;
			if (imageData != null) {
				logger.log(DEBUG, () -> sf("%s: Setting non-null image.", getColumnForLog()));
				img = new ImageIcon(imageData);
				lblImage.setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
				lblImage.setText("");
			} else {
				logger.log(DEBUG, () -> sf("%s: Setting null image.", getColumnForLog()));
				img = null;
				lblImage.setText("No Picture");
			}
		} catch (final SQLException se) {
			logger.log(Level.ERROR, getColumnForLog() + ": SQL Exception.", se);
			img = null;
		}

		lblImage.setIcon(img);

		// TODO Confirm updateUI is needed here.
		updateUI();

	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("%s{file=%s, %s}", getClass().getSimpleName(),
				fName, SSUtils.ssComponentToString(this));
	}

	/**
	 * Returns ssCommon for the current Swingset component.
	 *
	 * @return common SwingSet component data and methods
	 */
    @Override
	public SSCommon getSSCommon() {
		if (ssCommon == null)
			return partialSSCommon = SSCommon.createStart(this, partialSSCommon);
		return ssCommon;
	}

	private SSCommon partialSSCommon;

	/**
	 * Either return a new create ssCommon or 
	 * Only call from constructor; "ssCommon = finishSSCommon()".
	 */
	private SSCommon finishSSCommon() {
		SSCommon rv = SSCommon.createFinish(this, partialSSCommon);
		partialSSCommon = null;
		return rv;
	}
}
