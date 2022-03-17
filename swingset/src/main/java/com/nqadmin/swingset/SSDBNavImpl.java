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

import java.awt.Component;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.formatting.SSFormattedTextField;
import com.nqadmin.swingset.utils.SSUtils;

// SSDBNavImpl.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Custom implementation of SSDBNav that overrides performPreInsertOps() to
 * clear/reset the various database-aware fields on a screen when the user adds
 * a new record. An instance of this class can be created for the container
 * where the fields are to be cleared and passed to the data navigator.
 * <p>
 * The data navigator will call the performPreInsertOps() method whenever the
 * user presses the insert button on the navigator. This functions recursively
 * clears (null, empty string, deselect) any SwingSet components or other
 * JComponents on the screen
 * <p>
 * This recursive behavior performed on all the components inside the JPanel or
 * JTabbedPane inside the specified container.
 */
public class SSDBNavImpl implements SSDBNav {

	/**
	 * Log4j Logger for component
	 */
	private static Logger logger = SSUtils.getLogger();

	/**
	 * unique serial id
	 */
	private static final long serialVersionUID = -4632505399798312457L;

	/**
	 * Screen where components to be cleared are located.
	 */
	protected Container container = null;

	/**
	 * Constructs a SSDBNavImpl with the specified container.
	 *
	 * @param _container	GUI Container to scan for Swing components to clear/reset
	 */
	public SSDBNavImpl(final Container _container) {
		container = _container;
	}

	/**
	 * Performs pre-insertion operations.
	 */
	@Override
	public void performPreInsertOps() {

		logger.debug("About to call setComponents() to clear values.");
		setComponents(container);

	} // end public void performPreInsertOps() {

	/**
	 * Clears all the JTextFields and resets the combo boxes to empty item before
	 * first item.
	 * <p>
	 * This is done for all SwingSet components, text fields, and text areas,
	 * recursively looking in to the JTabbedPanes and JPanels inside the given
	 * container as needed.
	 *
	 * @param _container container in which to recursively initialize components
	 */
	protected void setComponents(final Container _container) {

		final Component[] comps = _container.getComponents();

		for (int i = 0; i < comps.length; i++) {
			
			//logger.debug("Clearing component type of: {}. Loop index=" + i, () -> comps[i].getClass().getSimpleName());

			if (comps[i] instanceof JTextField) {
				// IF IT IS A SSFormattedTextField SET ITS VALUE TO NULL (to avoid parse
				// exception)
				if (comps[i] instanceof SSFormattedTextField) {
					((SSFormattedTextField) comps[i]).cleanField();
				} else {
					// IF IT IS A JTextField SET ITS TEXT TO EMPTY STRING
					((JTextField) comps[i]).setText("");
				}
			} else if (comps[i] instanceof JList) {
				// IF IT IS A JList, CLEAR IT
				((JList<?>) comps[i]).clearSelection();
			} else if (comps[i] instanceof JTextArea) {
				// IF IT IS A JTextArea, SET TO EMPTY STRING
				((JTextArea) comps[i]).setText("");
			} else if (comps[i] instanceof SSBaseComboBox) {
				// IF IT IS A SSBaseComboBox THEN SET IT TO 'EMPTY' ITEM BEFORE FIRST ITEM
				((SSBaseComboBox<?, ?, ?>) comps[i]).setSelectionPending(true);
			} else if (comps[i] instanceof JComboBox) {
				// IF IT IS A JComboBox THEN SET IT TO 'EMPTY' ITEM BEFORE FIRST ITEM
				((JComboBox<?>) comps[i]).setSelectedIndex(-1);
			} else if (comps[i] instanceof SSImage) {
				// IF IT IS A SSImage CLEAR THE IMAGE.
				((SSImage) comps[i]).clearImage();
			} else if (comps[i] instanceof JCheckBox) {
				// IF IT IS A JCheckBox UNCHECK
				((JCheckBox) comps[i]).setSelected(false);
			} else if (comps[i] instanceof SSLabel) {
				// IF IT IS A SSLabel, SET TO EMPTY STRING
				((SSLabel) comps[i]).setText("");
			} else if (comps[i] instanceof JLabel) {
				// IF IT IS A JLabel - DO NOTHING
				// nothing to do...
			} else if (comps[i] instanceof JButton) {
				// IF IT IS A JButton - DO NOTHING
				// nothing to do...
			} else if (comps[i] instanceof JMenuBar) {
				// IF IT IS A JMenuBar - DO NOTHING
				// nothing to do...
			} else if (comps[i] instanceof BasicInternalFrameTitlePane) {
				// IF IT IS A BasicInternalFrameTitlePane (including MetalInternalFrameTitlePane
				// - DO NOTHING)
				// nothing to do...
			} else if (comps[i] instanceof JSlider) {
				// IF IT IS A JSlider, SET TO AVERAGE OF MIN/MAX VALUES
				((JSlider) comps[i])
						.setValue((((JSlider) comps[i]).getMinimum() + ((JSlider) comps[i]).getMaximum()) / 2);
			} else if (comps[i] instanceof JRootPane) {
				// IF IT IS A JRootPane RECURSIVELY SET THE FIELDS
				setComponents((Container) comps[i]);
			} else if (comps[i] instanceof JPanel) {
				// IF IT IS A JPanel RECURSIVELY SET THE FIELDS
				setComponents((Container) comps[i]);
			} else if (comps[i] instanceof JLayeredPane) {
				// IF IT IS A JLayeredPane RECURSIVELY SET THE FIELDS
				setComponents((Container) comps[i]);
			} else if (comps[i] instanceof JTabbedPane) {
				// IF IT IS A JTabbedPane RECURSIVELY SET THE FIELDS
				setComponents((Container) comps[i]);
			} else if (comps[i] instanceof JScrollPane) {
				// IF IT IS A JScrollPane GET THE VIEW PORT AND RECURSIVELY SET THE FIELDS IN
				// VIEW PORT
				setComponents(((JScrollPane) comps[i]).getViewport());
			} else {
				// DIPLAY WARNING FOR UNKNOWN COMPONENT
				logger.warn("Encountered unknown component type of: " + comps[i].getClass().getSimpleName()
						+ ". Unable to clear component.");
			}

		}

	} // end protected void setComponents(Container _container) {

} // end public class SSDBNavImpl
