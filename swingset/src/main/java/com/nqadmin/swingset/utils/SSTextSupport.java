/* *****************************************************************************
 * Copyright (C) 2025, Ernie R Rael. All rights reserved.
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
 * ****************************************************************************/

package com.nqadmin.swingset.utils;

import java.lang.System.Logger;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import static com.nqadmin.swingset.navigate.Utils.postRowSetModifiedError;
import static java.lang.System.Logger.Level.*;

/**
 * Support classes used with SSComponents that extend JTextComponent.
 */
public class SSTextSupport
{
	private SSTextSupport() { }
	
	/** Logger for component */
	private static final Logger logger = SSUtils.getLogger();
	
	/**
	 * Returns SSDocumentListener; error if not SSComponent.
	 * <p>
	 * Should only be called once per component.
	 *
	 * @param jtc Listener for this.
	 * @return SSDocumentListener for a JTextComponent
	 */
	public static SSDocumentListener getSSDocumentListener(JTextComponent jtc) {
		if (!(jtc instanceof SSComponentInterface comp))
			throw new IllegalArgumentException("Not an SSComponent");
		// TODO: assert not called before
		return new SSTextSupport.SSDocumentListener(comp);
	}
	
	/**
	 * For JTextField to track previous text field value.
	 * Used in conjunction with {@link SSDocumentListener}.
	 * <p>
	 * Part of the fix for<br>
	 * Text field has wrong value after error while editing<br>
	 * https://github.com/bpangburn/swingset/issues/175<br>
	 * Which came in with<br>
	 * Fix error recovery after errors during SSTextField edit<br>
	 * https://github.com/bpangburn/swingset/pull/178<br>
	 *
	 */
	@SuppressWarnings(value = "serial")
	public static class SSPlainDocument extends PlainDocument
	{
		
		DocumentFilter filter;
		private final SSCommon ssCommon;
		/** Create DocumentListener for the component.
		 * @param comp associated component
		 */
		public SSPlainDocument(SSComponentInterface comp)
		{
			this.ssCommon = comp.getSSCommon();
		}
		
		void capturePrevious(DocumentFilter.FilterBypass fb)
		{
			try {
				String prev = fb.getDocument().getText(0, fb.getDocument().getLength());
				logger.log(TRACE, () -> "Capture previous text value: " + prev);
				if (ssCommon.getEventListener() instanceof SSDocumentListener listener)
					listener.previousValue = prev;
			} catch (BadLocationException ex) {
				logger.log(DEBUG, "Capture previous text value", ex);
			}
		}
		
		/** {@inheritDoc} */
		@Override
		public DocumentFilter getDocumentFilter()
		{
			if (filter == null)
				filter = new DocumentFilter() {
					@Override
					public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException
					{
						capturePrevious(fb);
						super.replace(fb, offset, length, text, attrs);
					}
					
					@Override
					public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException
					{
						capturePrevious(fb);
						super.insertString(fb, offset, string, attr);
					}
					
					@Override
					public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException
					{
						capturePrevious(fb);
						super.remove(fb, offset, length);
					}
				};
			return filter;
		}
		
	}

	/**
	 * Document listener provided for convenience for SwingSet Components that are
	 * based on JTextComponents. SwingSet components that need a Document listener
	 * to trigger a change to the bound RowSet should return an instance of
	 * SSCommonDocumentListener() when implementing the abstract method
	 * getSSComponentListener().
	 * <p>
	 * A typical implementation might look like: {@code
	 * 	return getSSCommon().getSSDocumentListener();
	 * }
	 * <p>
	 * This listener updates the underlying RowSet when there is a change to the Document
	 * object. E.g., a call to setText() on a JTextField. If the update has an error
	 * the text field is reverted to the current contents of the database.
	 * <p>
	 * DocumentListener events generally, but not always get fired twice any time
	 * there is an update to the JTextField: a removeUpdate() followed by
	 * insertUpdate(). See:
	 * https://stackoverflow.com/questions/15209766/why-jtextfield-settext-will-fire-documentlisteners-removeupdate-before-change#15213813
	 * <p>
	 * Using partial solution here from here:
	 * https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
	 * <p>
	 * Having removeUpdate() and insertUpdate() both call changedUpdate().
	 * changedUpdate() uses counters and SwingUtilities.invokeLater() to only update
	 * the display on the last method called.
	 */
	public static class SSDocumentListener implements DocumentListener
	{

		/**
		 * variables needed to consolidate calls to removeUpdate() and insertUpdate()
		 * from DocumentListener
		 */
		private int lastChange = 0;
		private int lastNotifiedChange = 0;
		private String previousValue = null;
		/** True when listener is temporarily removed. */
		private final SSCommon ssCommon;

		/** Create DocumentListener for the component.
		 * @param comp associated component
		 */
		public SSDocumentListener(SSComponentInterface comp)
		{
			this.ssCommon = comp.getSSCommon();
		}

		/** {@inheritDoc} */
		@Override
		public void changedUpdate(final DocumentEvent de)
		{
			lastChange++;
			logger.log(TRACE, () -> SSUtils.sf(
					"%s - changedUpdate(): lastChange=%s, lastNotifiedChange=%s",
					ssCommon.getColumnForLog(), lastChange, lastNotifiedChange));
			// Delay updateTextComponent until all Document listeners inovked for event.
			// See: https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
			SwingUtilities.invokeLater(() -> {
				if (lastNotifiedChange != lastChange) {
					lastNotifiedChange = lastChange;
					ssCommon.dbChange(() -> updateTextComponent());
				}
			});
		}

		/** This could be in updateTextComponent (like as an array element). */
		private boolean listenerNeedsRestoration;

		private void updateTextComponent()
		{
			String text = ((JTextComponent) ssCommon.getSSComponent()).getText();
			// update decorator per keystroke.
			if (!ssCommon.decorate()) {
				postRowSetModifiedError(ssCommon.getSSComponent(), text);
				return;
			}
			boolean ok = true;
			// boolean inErrorState = NavigateAcitons.inErrorState();
			try {
				ok = ssCommon.setBoundColumnText(text);
			} finally {
				if (!ok) {
					// restore previous text value
					if (previousValue != null) {
						if (ssCommon.isSSComponentListenerAdded()) {
							// avoid generating events while restoring text
							ssCommon.removeSSComponentListener();
							listenerNeedsRestoration = true;
						}
						try {
							logger.log(DEBUG, () -> SSUtils.sf("%s: restoring previous value '%s'", ssCommon.getColumnForLog(), previousValue));
							((JTextComponent) ssCommon.getSSComponent()).setText(previousValue);
						} finally {
							if (listenerNeedsRestoration) {
								listenerNeedsRestoration = false;
								ssCommon.addSSComponentListener();
							}
						}
						// RESTORE ERROR STATE
					}
				}
				previousValue = null; // Seems safer, is this the right spot?
			}
		}

		/** {@inheritDoc} */
		@Override
		public void insertUpdate(final DocumentEvent de)
		{
			logger.log(TRACE, () -> SSUtils.sf("%s - insertUpdate().", ssCommon.getColumnForLog()));
			changedUpdate(de);
		}

		/** {@inheritDoc} */
		@Override
		public void removeUpdate(final DocumentEvent de)
		{
			logger.log(TRACE, () -> SSUtils.sf("%s - removeUpdate().", ssCommon.getColumnForLog()));
			changedUpdate(de);
		}
		
	} // end protected class SSDocumentListener
	
}