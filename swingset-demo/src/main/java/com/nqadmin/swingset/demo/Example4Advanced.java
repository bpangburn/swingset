/* *****************************************************************************
 * Copyright (C) 2022, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 * ****************************************************************************/
package com.nqadmin.swingset.demo;

import com.nqadmin.swingset.SSBaseComboBox.MissingOptionControl;
import com.nqadmin.swingset.models.SSListItem;
import com.nqadmin.swingset.models.SSListItemFormat;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.sql.Connection;
import java.util.EnumSet;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * Demonstrate some advanced features of SSCombobox
 * and using Navigator actions.
 * <ul>
 * <li>Using a custom ListItemFormat for null/missing Option</li>
 * <li>MissingOptionControl</li>
 * <li>Assigning navigator actions to function keys</li>
 * <li>Buttons that invoke navigation actions</li>
 * </ul>
 */
@SuppressWarnings("serial")
public class Example4Advanced extends Example4 {

	@Override
	void cmbPartColorChangeOptions() {
		// This method must be called from Example4 before
		// some other initialization, like bind, SSSyncManager.
		cmbPartColor.setOptions(new String[] { "Green", "Blue" }, new int[] {1,2});
	}

	public Example4Advanced(Connection _dbConn) {
		super(_dbConn);

		setTitle("Example4Advanced");

		// NOTE: cmbPartColorChangeOptions has alread been called.

		//////////////////////////////////////////////////////////////////////

		// Example of using SSComboBox.setListItemFormat().
		// Use a custom message when a null/missing Option
		// is encountered in an SSComboBox.
		// NOTE: the default message is "# - Option Not Found"
		cmbPartColor.setListItemFormat(new SSListItemFormat() {
			@Override
			protected void appendValue(StringBuffer _sb, int _elemIndex, SSListItem _listItem) {
				if (cmbPartColor.getOptionFormatIndex() == _elemIndex
						&& getElem(_elemIndex, _listItem) == null) {
					Object key = getElem(cmbPartColor.getMappingFormatIndex(), _listItem);
					_sb.append(key != null ? key.toString() : null)
							.append(" - BUG: MISSING OPTION");
				} else {
					super.appendValue(_sb, _elemIndex, _listItem);
				}
			}
		});

		//////////////////////////////////////////////////////////////////////

		// Missing option handling has some controls.
		// Here we just get the current value, log it and put it back;
		// so we're still using the default handling. See javadoc as needed.

		// Get previous value of MissingOptionControl
		EnumSet<MissingOptionControl> mmc = cmbPartColor.setMissingOptionControl(
				EnumSet.noneOf(MissingOptionControl.class));
		// restore the default
		cmbPartColor.setMissingOptionControl(mmc);

		// With the following two lines retain the missing mapping option in list
		// for all records, even those without a missing option.
		//mmc.remove(MissingOptionControl.MOC_CLEANUP);
		//cmbPartColor.setMissingOptionControl(mmc);

		// log the MissingOptionControl values
		logger.info(() -> ("MissingMappingFlags: " + mmc));


		//////////////////////////////////////////////////////////////////////
		
		// Illustrate use of InputMap/ActionMap for custom key and extra button handling.
		// Setup F3-F11 mnemonics to correspond to the buttons on Navigator.
		// There are also two new buttons below the Navigator (extra first and last)
		//
		// The actions here are currently the only Actions available in the SSDataNavigator
		// ActionMap.
		//
		// See https://docs.oracle.com/javase/tutorial/uiswing/misc/action.html
			
		// Hotkeys/mnemonics
		navigator.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F3"),"NavFirst");
		navigator.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F4"),"NavPrevious");
		navigator.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"),"NavNext");
		navigator.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F6"),"NavLast");
		navigator.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F7"),"NavCommit");
		navigator.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F8"),"NavUndo");
		navigator.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F9"),"NavRefresh");
		navigator.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F10"),"NavAdd");
		navigator.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"),"NavDelete");

		final Container contentPane = getContentPane();
		final GridBagConstraints constraints = new GridBagConstraints();
		
	// "Extra" buttons
		Action tmpAction;
		JButton tmpButton;

		// First record
		tmpAction = navigator.getActionMap().get("NavFirst");
		tmpButton = new JButton(tmpAction);
		constraints.gridx = 0;
		contentPane.add(tmpButton, constraints);
		
		// Last record
		tmpAction = navigator.getActionMap().get("NavLast");
		tmpButton = new JButton(tmpAction);
		constraints.gridx = 1;
		contentPane.add(tmpButton, constraints);

		pack();

		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////

		// The following is not a feature per se. It is for tracking down
		// a problem using a new feature, setListItemFormat, after the
		// combobox is fully initialized.

		// Test that chaning ListItemFormat redraws the combo.
		// Change the formatter when first char of txtPartName set to '***'

		// *******************************************************************
		// If set the following to TRUE, then when insert '***' at the
		// beginning of txtPartName a setListItemFormat(new format...) is done.
		// *******************************************************************
		if(Boolean.TRUE) {
			// Use this to demonstrate a failure.
			// Forces the row to be updated changes collor to null
			// if the current row has a missing option
			AbstractDocument doc = (AbstractDocument) txtPartName.getDocument();
			// both branches show the same failure mode.
			if(Boolean.TRUE) {
				doc.setDocumentFilter(new DocumentFilter() {
					@Override
					public void insertString(DocumentFilter.FilterBypass fb,
							int offset, String string, AttributeSet attr)
							throws BadLocationException {
						if(checkText(fb, offset, 0, string, attr))
							super.insertString(fb, offset, string, attr);
					}
					
					@Override
					public void replace(DocumentFilter.FilterBypass fb,
							int offset, int length, String text, AttributeSet attrs)
							throws BadLocationException {
						if(checkText(fb, offset, length, text, attrs))
							super.replace(fb, offset, length, text, attrs);
					}
					
					/** return true to proceed with action */
					boolean checkText(FilterBypass fb,
							int offset, int length, String text, AttributeSet attrs)
							throws BadLocationException {
						Document doc = fb.getDocument();
						String newText = doc.getText(0, offset) + text
								+ doc.getText(offset + length,
										doc.getLength() - (offset + length));
						if(newText.startsWith("***") && offset < 3) {
							super.remove(fb, 0, 2);
							changeListFormatter("2");
							return false;
						} else
							return true;
					}
				});
			} else {
				doc.addDocumentListener(new DocumentListener() {
					@Override public void insertUpdate(DocumentEvent e) { check(); }
					@Override public void removeUpdate(DocumentEvent e) { }
					@Override public void changedUpdate(DocumentEvent e) { }
					
					void check() {
						String text = txtPartName.getText();
						if(text.startsWith("***")) {
							EventQueue.invokeLater(() -> txtPartName.setText(text.substring(3)));
							changeListFormatter("1");
						}
					}
				});
			}
		}
	}

	private int countChange = 2;
	// Test that chaning ListItemFormat redraws the combo
	private void changeListFormatter(String tag) {
		countChange++;
		int captureCountChange = countChange;
		logger.info(String.format("change%s formatter: %d", tag, countChange));
		EventQueue.invokeLater(() -> {
			cmbPartColor.setListItemFormat(new SSListItemFormat() {
				@Override
				protected void appendValue(StringBuffer _sb, int _elemIndex, SSListItem _listItem) {
					if (cmbPartColor.getOptionFormatIndex() == _elemIndex
							&& getElem(_elemIndex, _listItem) == null) {
						Object key = getElem(cmbPartColor.getMappingFormatIndex(), _listItem);
						_sb.append(key != null ? key.toString() : null)
								.append(" - change: ")
								.append(captureCountChange);
					} else {
						super.appendValue(_sb, _elemIndex, _listItem);
					}
				}
			});
		});
	}
}
