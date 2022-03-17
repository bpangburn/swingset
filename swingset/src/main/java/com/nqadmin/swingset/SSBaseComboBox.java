/* *****************************************************************************
 * Copyright (C) 2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
package com.nqadmin.swingset;

import static com.nqadmin.swingset.models.AbstractComboBoxListSwingModel.addEventLogging;
import static com.nqadmin.swingset.models.OptionMappingSwingModel.asOptionMappingSwingModel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.models.AbstractComboBoxListSwingModel;
import com.nqadmin.swingset.models.GlazedListsOptionMappingInfo;
import com.nqadmin.swingset.models.OptionMappingSwingModel;
import com.nqadmin.swingset.models.SSListItem;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;
import com.nqadmin.swingset.utils.SSUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

// SSBaseComboBox.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * OptionMapping base class for combo box that uses/handles
 * {@link #getAllowNull() getAllowNull()}/{@link #setAllowNull(boolean) setAllowNull(boolean)},
 * and tracks metadata changes,
 * for adding/removing nullItem to the item list.
 * ssCommon is defined in here. In addition, there are JComboBox models for
 * use with or without glazed lists; these models have static methods for
 * hooking things up to the JComboBox.
 * <p>
 * Classes that extend this class, should use {@link #adjustForNullItem() }
 * after clearing the itemList or when a change is made that might affect
 * whether or not a nullItem is required.
 * <p>
 * Initially no Option2.
 * @param <M> mapping type
 * @param <O> option type
 * @param <O2> option2 type
 * @since 4.0.0
 *
 */
//
// TODO: There are more things that can be pulled into here.
//
//       Might want to put listItemFormat field in here.
//
//       Following are the same and can be moved into here
//       after deprecated stuff is removed. There may be more.
//       - getMappings()
//       - getOptions()
//       - 
//
//       Some things are only in one subclass, but feel like
//       they belong in here.
//       - removeMapping(M)
//       - 
//
public abstract class SSBaseComboBox<M,O,O2> extends JComboBox<SSListItem> implements SSComponentInterface
{
	private static final long serialVersionUID = 1L;

	/**
	 * Listener(s) for the component's value used to propagate changes back to the rowset in certain instances.
	 * <p>
	 * NOTE: No guaranty whether ActionListener or FocusLister will be called first when focus is lost.
	 */
	protected class SSBaseComboBoxListener implements ActionListener, Serializable
	{
		private static final long serialVersionUID = -3131533966245488092L;

		/**
		 * For JComboBox ActionListener and ItemListener are similar, but ActionListener
		 * seems more appropriate since we don't care about the de-selection of the
		 * previously selected item.
		 * 
		 * Per https://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html#listeners
		 * ActionListener just tells us that the selection changed whereas ItemListener fires
		 * separate events for deselection of previous item and selection of current item.
		 *
		 * {@inheritDoc} */
		@Override
		public void actionPerformed(final ActionEvent ae) {

			// If this is a combo navigator, SSSyncManager will have it's own listeners.
			// This is just for keeping a bound column in sync.
			//
			// Could be combined with next block, but keeping them separate for debugging.
			if (isComboBoxNavigator()) {
				logger.debug("{}: Action Listener returning. No bound column.", () -> getColumnForLog());
				return;
			}
			
//			// **** GL STRICT/CONTAINS ****
//			//
//			// Could be combined with prior block, but keeping them separate for debugging.
//			// May be able to eliminate actionListenerNoUpdate when GlazedLists fully supports STRICT/CONTAINS
//			//
//			// actionListenerNoUpdate will be set to true in setSelectedItem() when the user enters a non-matching string.
//			// It is also in the 'else' for unforeseen outcomes. The user is notified with a popup in both cases.
//			// 
//			// actionListenerNoUpdate is likely STRICT/CONTAINS workaround specific. Presumably if GL fully supported it,
//			// we'd only ever have null or an SSListItem passed to setSelectedItem(). Will have to put some thought into
//			// when we'd expect null (insert row, some escaping from/clearing of the combo editor)? Maybe we'd only ever
//			// have null for the insert row OR getModel().getSize()==0 ?
//			if (actionListenerNoUpdate) {
//				logger.debug("{}: Action Listener returning. actionListenerNoUpdate set to TRUE.", () -> getColumnForLog());
//				return;
//			}
//			
//			// EXTRACT SELECTED ITEM
//			Object selectedItem = getSelectedItem();
//			logger.debug("{}: ACTION LISTENER: getSelectedItem() has '{}'.", () -> getColumnForLog(), () -> selectedItem);
//			
//			// **** GL STRICT/CONTAINS ****
//			//
//			// IF SELECTED ITEM IS NOT AN SSLISTITEM, CHECK THE glGlitchItem
//			// 
//			// This seems to be a strange timing issue. The logs indicate that setSelectedItem() below has encountered 
//			// SCENARIO 1-B and has made a call to super.setSelectedItem() to select the correct/updated SSListItem, but
//			// selectedItem here still has a String. glGlitchItem has the correct/updated SSListItem.
//			//
//			// Making a 2nd call to super.setSelectedItem(glGlitchItem) appears to resolve the issue and does not trigger an
//			// additional ActionListener event.
//			if (selectedItem==null || !(selectedItem instanceof SSListItem)) {
//				if (glGlitchItem!=null) {
//					logger.debug("{}:  -- About to call super.setSelectedItem({}) {}.", () -> getColumnForLog(), () -> glGlitchItem);
//					SSBaseComboBox.super.setSelectedItem(glGlitchItem);
//					logger.debug("{}:  -- getSelectedItem() now returns {}.", () -> getColumnForLog(), () -> getSelectedItem());
//				}
//			}
			
			// UPDATE ROWSET
			logger.debug("{}: About to update RowSet with {}.", () -> getColumnForLog(), () -> getSelectedItem());
			updateRowset();
		}
	}

	/**
	 * Listens/waits for selected item not nullItem. When not nullItem is
	 * set, selectionPending set to false.
	 */
	private final class SSBaseComboBoxItemListener implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			//System.err.println(e.paramString());
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (getSelectedItem() != nullItem && selectionPending) {
					setSelectionPending(false);
					//System.err.println("itemStateChanged true --> false");
				}
			}
		}
	}

	/**
	 * OptionMapping model that can be installed in a JComboBox; this model has methods
	 * for building and manipulating the item list.
	 * @param <M> mapping type
	 * @param <O> option type
	 * @param <O2> option2 type
	 */
	protected static class BaseModel<M,O,O2> extends OptionMappingSwingModel<M,O,O2>
	{

		/**
		 * Create model and install it in the specified JComboBox.
		 * 
		 * @param <M> mapping type
		 * @param <O> option type
		 * @param <O2> option2 type
		 * @param _jc install model into this
		 * @return OptionMapping model
		 */
		protected static <M,O,O2>BaseModel<M,O,O2> install(SSBaseComboBox<M,O,O2> _jc) {
			BaseModel<M,O,O2> model = new BaseModel<>();
			AbstractComboBoxListSwingModel.install(_jc, model);

			return model;
		}

		/**
		 * Create a model.
		 */
		protected BaseModel() {
			// false means no Options2
			super(false);
		}
	}

	/**
	 * OptionMapping model for use with GlazedLists; this model has methods
	 * for building and manipulating the EventList and supports locking.
	 * @param <M> mapping type
	 * @param <O> option type
	 * @param <O2> option2 type
	 */
	protected static class BaseGlazedModel<M,O,O2> extends GlazedListsOptionMappingInfo<M,O,O2>
	{

		/**
		 * The GlazedLists auto completion support for the
		 * associated JComboBox.
		 */
		protected AutoCompleteSupport<SSListItem> autoComplete;

		// **** GL STRICT/CONTAINS ****
		//
		// See https://stackoverflow.com/questions/15210771/autocomplete-with-glazedlists for info on modifying lists.
		// See https://javadoc.io/doc/com.glazedlists/glazedlists/latest/ca/odell/glazedlists/swing/AutoCompleteSupport.html
		// We would like to call autoComplete.setStrict(true), but it is not currently compatible with TextMatcherEditor.CONTAINS, which is the more important feature.
		// There is a support request to support STRICT and CONTAINS: https://github.com/glazedlists/glazedlists/issues/676
		// Note that installing AutoComplete support makes the ComboBox editable.
		// Should already in the event dispatch thread so don't use invokeAndWait()

		/**
		 * Create a model that has a glazed EventList;
		 * install autocompletion in the specified JComboBox.
		 *
		 * @param <M> mapping type
		 * @param <O> option type
		 * @param <O2> option2 type
		 * @param _jc install auto completion into this
		 * @return OptionMapping model
		 */
		protected static <M,O,O2>BaseGlazedModel<M,O,O2> install(SSBaseComboBox<M,O,O2> _jc) {
			BaseGlazedModel<M,O,O2> model = new BaseGlazedModel<>();
			model.autoComplete = AutoCompleteSupport.install(_jc, model.getEventList(), null, model.getListItemFormat());
			
			model.autoComplete.setFilterMode(TextMatcherEditor.CONTAINS);
			model.autoComplete.setStrict(true);
			model.autoComplete.setPositionCaretTowardZero(true);

			// RESTORE JCOMBOBOX UP/DOWN ARROW HANDLING OVERRIDING GLAZEDLIST
			_jc.glazedListArrowHandler();

			return model;
		}

		/**
		 * Create a model with a glazed EventList.
		 */
		protected BaseGlazedModel() {
			// false means no Options2
			super(false, new BasicEventList<SSListItem>());
		}
	}

	/**
	 * Log4j Logger for component
	 */
	private static final Logger logger = SSUtils.getLogger();

	/**
	 * When {@link #getAllowNull() } is true, this is the null item;
	 * when false this is null. So {@link #setSelectedItem(java.lang.Object)
	 * setSelectedItem(nullItem)} does the right thing whether getAllowNull()
	 * is true or false.
	 * <p>
	 * <b>When the item list is cleared, for example
	 * {@link com.nqadmin.swingset.models.OptionMappingSwingModel.Remodel#clear() remodel.clear()},
	 * the nullItem must be set to null.</b>
	 * @see #createNullItem(com.nqadmin.swingset.models.OptionMappingSwingModel.Remodel)
	 */
	protected SSListItem nullItem;
	
//	/**
//	 * String typed by user into combobox
//	 */
//	// **** GL STRICT/CONTAINS ****
//	private String priorEditorText = "";

	/**
	 * Common fields shared across SwingSet components
	 */
	protected SSCommon ssCommon = new SSCommon(this);

//	/**
//	 * List item used to workaround GlazedList CONTAINS bug/glitch.
//	 */
//	// **** GL STRICT/CONTAINS ****
//	private SSListItem glGlitchItem = null;
	
//	/**
//	 * Indicates that a garbage string has been entered and the Action Listener should not
//	 * try to update the rowset.
//	 */
//	// **** GL STRICT/CONTAINS ****
//	private boolean actionListenerNoUpdate = false;

	/**
	 * This is used when moving to a new row when getAllowNull() == false 
	 * and strict glazed is true. The ComboBox needs to be shown empty,
	 * ie. without a value, until something is selected. After something
	 * is selected then strict applies.
	 * 
	 * There are some event issues check out {@link #setSelectionPending(boolean) }.
	 * With cleaner event model, Navigator events?, can probably ...
	 */
	private boolean selectionPending;

	/**
	 * The combo model.
	 */
	protected OptionMappingSwingModel<M,O,O2> optionModel;

	//////////////////////////////////////////////////////////////////////////
	//
	// Model stuff
	//

	/**
	 * <b>It is probably an error to use this.</b>
	 * {@inheritDoc}
	 */
	// TODO: throw exception?
	@Override
	@SuppressWarnings("unchecked")
	public void setModel(ComboBoxModel<SSListItem> _model) {
		addEventLogging(_model);
		OptionMappingSwingModel<?, ?, ?> model = asOptionMappingSwingModel(_model);
		optionModel = model instanceof BaseModel ? (BaseModel<M,O,O2>)model
				: model instanceof BaseGlazedModel ? (BaseGlazedModel<M,O,O2>)model
				: null;

		super.setModel(_model);
	}

	/**
	 * Convenience method for accessing the model with proper casting.
	 * 
	 * @return mapping list model with proper casting, may be null
	 */
	protected OptionMappingSwingModel<M, O, O2> getOptionModel() {
		return optionModel;
	}

	/**
	 * Get the support for the glazed lists installed in this combo box.
	 * 
	 * @return the support or null if GlazedLists not installed
	 */
	protected AutoCompleteSupport<SSListItem> getAutoComplete() {
		return optionModel instanceof BaseGlazedModel
				? ((BaseGlazedModel<M,O,O2>)optionModel).autoComplete
				: null;
	}

	/**
	 * create SSBaseComboBox
	 */
	public SSBaseComboBox() {
		addItemListener(new SSBaseComboBoxItemListener());
	}



	/////////////////////////////////////////////////////////////////////////
	//
	// Option/Mapping
	//

	/**
	 * Determine if there are option/mapping items 
	 * that have been added to this combo box.
	 * This is tricky because
	 * if getAllowNull is true then nullItem is in the item list and
	 * shouldn't be consider an item.
	 * 
	 * @return true if there are options/mappings
	 */
	public boolean hasItems() {
		return optionModel.getItemList().size() != (getAllowNull() ? 1 : 0);
	}

	/**
	 * Determine if the combo box has a selection, either an item that
	 * has been added or if the combo box is editable then user entry.
	 * This is tricky because
	 * if getAllowNull is true then nullItem may be selected
	 * and that should not be considered. In particular
	 * {@code getSelectedItem() != null} and {@code getSelectedIndex() != -1}
	 * may not work as expected.
	 * 
	 * @return true if the combo box has a selection
	 */
	public boolean hasSelection() {
		Object item = getSelectedItem();
		return item != null && item != nullItem;
	}
	
//	/**
//	 * {@inheritDoc }
//	 * Deal with edge cases for combo editor interaction with GlazedLists and lack of support
//	 * for STRICT/CONTAINS in GlazedLists.
//	 */
//	// **** GL STRICT/CONTAINS ****
//	//
//	// IT IS POSSIBLE THAT NO OVERRIDE OF setSelectedItem() WILL BE NEEDED IF GLAZEDLISTS FULLY SUPPORTS
//	// STRICT/CONTAINS
//	@Override
//	public void setSelectedItem(final Object _value) {
//		// 2020-12-30_BP: setSelectedItem outcomes:
//		//
//		// See https://docs.oracle.com/javase/8/docs/api/javax/swing/JComboBox.html#setSelectedItem-java.lang.Object-
//		//
//		//  1. We get a null:
//		//      A. A direct/indirect non-UI call was made to setSelectedItem(null).
//		//         This could be a value from the rowset listener, a programmatically set default, etc.
//		//         In this case the editorComponent should NOT have the focus.
//		//         -> call super.setSelectedItem(null) and continue
//		//      B. GlazedList bug where for the first character entered, the START_WITH matching is performed over the CONTAINS matching. In this case,
//		//         (getModel().getSize() > 0)
//		//         -> call super.setSelectedItem(getItemAt(0)) and continue
//		//      C. The user's entry into the editor results in zero matches. In this case ((getModel().getSize()==0) && (hasItems()==true))
//		//         -> set actionListenerNoUpdate to true so the Action Listener does not try to update the rowset
//		//         -> warn user, (attempt to) revert the editor string, and continue (simulated STRICT)
//		//      D. SOMETHING ELSE???
//		//         -> set actionListenerNoUpdate to true so the Action Listener does not try to update the rowset
//		//         -> warn user and continue
//		//         
//		//  2. We get a valid SSListItem including nullItem if getAllowNull()==true. This could be from the UI or from a 
//		//     direct/indirect non UI call to setSelectedItem().
//		//	   -> call super.setSelectedItem(_value) and continue
//		//
//		//  3. We get a String or something else.
//		//     -> set actionListenerNoUpdate to true so the Action Listener does not try to update the rowset
//		//     -> warn user and return
//	
//		// INITIALIZATION
//		glGlitchItem = null; // WORKAROUND FOR GL 'CONTAINS' GLITCH
//		actionListenerNoUpdate = false; // TRUE WHEN THE USER HAS ENTERED SOMETHING RESULTING IN NO MATCHES
//
//		// GET CURRENT EDITOR TEXT IF AVAILABLE
//		final String currentEditorText;
//		if (getEditor().getItem() == null) {
//			currentEditorText = "";
//		} else {
//			currentEditorText = getEditor().getItem().toString();
//		}
//		
//		// INITIAL LOGGING
//		logger.debug(() -> String.format("%s: CALL TO setSelectedItem(%s), allowNull: %b, priorEditorText: '%s', currentEditorText: '%s'",
//				getColumnForLog(), _value, getAllowNull(), priorEditorText, currentEditorText));
//		
//		// EVALUATE SCENARIOS DOCUMENTED ABOVE
//		if (_value == null) {
//			// #1 - WE HAVE A NULL
//			if (!getEditor().getEditorComponent().hasFocus()) {
//				// SCENARIO #1-A ABOVE - setting null from a rowset listener or programmatically
//				// (e.g., a default)
//				super.setSelectedItem(null);
//				priorEditorText = currentEditorText;
//				logger.debug(
//						"{}: Null not from UI (e.g., rowset, default). Selected item after super.setSelectedItem()={}",
//						() -> getColumnForLog(), () -> getSelectedItem());
//
//			} else if (getModel().getSize() > 0) {
//				// SCENARIO #1-B ABOVE - null due to GlazedList glitch when items contain the
//				// first character, but GL does not match the first item returned
//				glGlitchItem = getItemAt(0);
//				super.setSelectedItem(glGlitchItem);
//				priorEditorText = getEditor().getItem().toString();
//				logger.debug(
//						"{}: Null due to GlazedLists glitch so selecting first item in model. Selected item after super.setSelectedItem()={}",
//						() -> getColumnForLog(), () -> getSelectedItem());
//
//			} else if (!currentEditorText.isEmpty()) {
//				// SCENARIO #1-C ABOVE - null because user likely entered garbage - revert
//				// editor and return without a call to setSelectedItem()
//				// 2020-12-29_BP: AT ONE POINT THE FOLLOWING CODE SEEMED TO WORK, BUT NOW THE
//				// CALL TO getEditor().setItem(priorEditorText) DOES NOT APPEAR TO
//				// REVERT THE TEXT
//				// - HAVE TINKERED WITH THE ORDERING OF showPopup() AND updateUI()
//				// - HAVE CONFIRMED THAT isEditable() IS TRUE
//				logger.debug(() -> String.format(
//						"%s: User entered string of '%s' did not match any list items. Attempting to revert to '%s'.",
//						getColumnForLog(), currentEditorText, priorEditorText));
//
//				// TELL ACTION LISTENER NOT TO UPDATE ITEM OR ROWSET
//				actionListenerNoUpdate = true;
//
//				// REVERT STRING
//				getEditor().setItem(priorEditorText);
//				// showPopup();// When this was working as intended, the ordering of showPopup()
//				// before updateUI() was relevant.
//				updateUI(); // NEEDED TO SHOW REVERTED ITEM (ORIGINALLY INTENDED TO SHOW REVERTED TEXT IN
//							// EDITOR)
//
//				// WARN USER AND LOG
//				String editorText = getEditor().getItem().toString();
//
//				JOptionPane.showMessageDialog(SSBaseComboBox.this,
//						"The text entered does not match any item in the list. Reverting to '" + editorText + "'.");
//
//				logger.debug(() -> String.format("%s:   Editor string following revert action: '%s'.",
//						getColumnForLog(), editorText));
//
//			} else {
//				// SCENARIO #1-D ABOVE - SOMETHING ELSE NOT ANTICIPATED
//				// SAME RESULT AS SCENARIO #3 BELOW
//				// TELL ACTION LISTENER NOT TO UPDATE ITEM OR ROWSET
//				actionListenerNoUpdate = true; // TELL ACTION LISTENER NOT TO UPDATE ITEM OR ROWSET
//
//				// WARN USER AND LOG
//				JOptionPane.showMessageDialog(SSBaseComboBox.this,
//						String.format("Unexpected call to setSelectedItem() for column %s with value '%s'.",
//								getColumnForLog(), _value));
//				logger.warn(() -> String.format("%s: Unexpected call to setSelectedItem() with '%s'.)",
//						getColumnForLog(), _value));
//			}
//		} else if (_value instanceof SSListItem) {
//			// #2 - WE HAVE A SSLISTITEM (including nullItem if getAllowNull()==true)
//			super.setSelectedItem(_value);
//			priorEditorText = currentEditorText;
//			// 2020-12-29_BP: CONFIRMED selectAll() IS NEEDED FOLLOWING A NORMAL ITEM
//			// SELECTION
//			// OTHERWISE USER IS APPENDING EXISTING ITEM STRING IF THEY START TO TYPE
//			if (getEditor().getEditorComponent().hasFocus()) {
//				// after we find a match, do a select all on the editor so
//				// if the user starts typing again it won't be appended
//				// 2020-12-21_BP: if we don't limited to field with focus, the comboboxes blink
//				// on navigation
//				// this also causes the focus to jump out of a navigation combo
//				getEditor().selectAll();
//			}
//			logger.debug("{}: Valid match. Selected item after super.setSelectedItem()={}", () -> getColumnForLog(),
//					() -> getSelectedItem());
//		} else {
//			// SCENARIO #3 - WE HAVE A STRING OR SOME OBJECT OTHER THAN NULL OR SSLISTITEM
//			// SAME RESULT AS SCENARIO #1-D ABOVE
//			// TELL ACTION LISTENER NOT TO UPDATE ITEM OR ROWSET
//			actionListenerNoUpdate = true; // TELL ACTION LISTENER NOT TO UPDATE ITEM OR ROWSET
//
//			// WARN USER AND LOG
//			JOptionPane.showMessageDialog(SSBaseComboBox.this, String.format(
//					"Unexpected call to setSelectedItem() for column %s with value '%s'.", getColumnForLog(), _value));
//			logger.warn(() -> String.format("%s: Unexpected call to setSelectedItem() with '%s'.)", getColumnForLog(),
//					_value));
//
//		}
//	}

	/**
	 * Typically true when at a new row waiting for use to select something;
	 * it is as though getAlowNull() is temporarily true.
	 * @return true if waiting for not nullItem
	 */
	public boolean isSelectionPending() {
		return selectionPending;
	}

	/**
	 * Control whether or not waiting for pending user selection. When selectionPending
	 * is true, there is a nullItem and it is selected in the combo.
	 * selectionPending is set to false automatically when a non nullItem is selected.
	 * @param _selectionPending true selects a possibly tempory nullItem
	 */
	public void setSelectionPending(boolean _selectionPending) {
		if (selectionPending == _selectionPending) {
			return;
		}
		selectionPending = _selectionPending;

		adjustForNullItem();
		if (selectionPending) {
			// Setting to true from false, select the nullItem;
			// Events are a problem. After SSDBNavImpl.setSelectionPending
			// the following ends up in SSBaseComboBoxListener.actionPerformed()
			// then into setBoundColumnText then exception in RowSetOps.updateColumnText.
			// TODO: merge this into adjustForNullItem, to avoid extra unregister/register
			//
			// Or better, with cleaner event model, like Navigator events,
			// might be easier to avoid RowSetOps.updateColumnText.
			// Actually, if there was an error badge next to the ComboBox
			// (instead of those f*ing dialogs) then when AllowNull is false
			// the blank combo would start with the error badge after inster row.
			// That sounds pretty good.
			final ActionListener[] listeners = unregisterAllActionListeners(this);
			try {
				super.setSelectedItem(nullItem);
			} finally {
				registerAllActionListeners(this, listeners);
			}
		}
	}

	/**
	 * Returns the mapping code corresponding to the currently selected item in the
	 * combobox. Typically, this is the underlying database record
	 * primary key value corresponding to the currently selected item.
	 *
	 * @return returns the value associated with the selected item
	 * OR null if nothing is selected.
	 */
	public M getSelectedMapping() {
		logger.trace(() -> String.format("%s: getSelectedMapping(), idx:map %d:%s.",
				getColumnForLog(), getSelectedIndex(), getSelectedItem()));

		M result = null;

		try (BaseModel<M,O,O2>.Remodel remodel = optionModel.getRemodel()) {
			Object item = getSelectedItem();
			if (item instanceof SSListItem) {
				result = remodel.getMapping((SSListItem)item);
			}
		}

		return result;
	}

	/**
	 * Sets the selected ComboBox item according to the specified mapping/key.
	 * The selectedItem is set to nullItem or null if mapping not found.
	 * <p>
	 * If called from updateSSComponent() from a RowSet change then the Component
	 * listener should already be turned off. Otherwise we want it on so the
	 * ultimate call to setSelectedItem() will trigger an update the to RowSet.
	 *
	 * @param _mapping key of item value to assign to combobox,
	 *                 which may or may not correlate to the combobox index
	 */
	public void setSelectedMapping(final M _mapping) {
		
		logger.debug(String.format("%s: current value: %s, new value: %s.",
				getColumnForLog(), getSelectedMapping(), _mapping ));

		try (BaseModel<M,O,O2>.Remodel remodel = optionModel.getRemodel()) {
			if (!hasItems()) {
				logger.warn(String.format("%s: combobox is empty", getColumnForLog()));
				// Doesn't have items, that doesn't mean that the list is empty.
				// Make sure the appropriate null is selected.
				setSelectedItem(nullItem);
				return;
			}
			
			// only need to proceed if there is a change.
			if (Objects.equals(_mapping, getSelectedMapping())) {
				return;
			}
			
			final int index = remodel.getMappings().indexOf(_mapping);
			SSListItem item;
			if (index != -1) {
				item = remodel.get(index);
			} else {
				// nullItem is either special first list item
				// or it is null. It is null when getAllowNull() is false
				item = nullItem;
				// BP_2021-02-16:
				// We expect to get here if we have a child combo where the contents are requeried on each record.
				// As soon as a navigation occurs, the component values are cleared and then the new value is loaded,
				// but the combo has not been re-queried yet so there are no matches for setSelectedMapping() and we
				// call setSelectedItem(nullItem). This is OK so long as the component listener used for binding
				// is removed/disabled because the rowset will not get the null value. Later when the combo is
				// requeried, the component will try to load the current column value from the rowset and this time
				// setSelectedMapping() should succeed.
				if (getSSCommon().isSSComponentListenerAdded()) {
					logger.warn(String.format("%s: No mapping available for %s in combobox, setSelectedItem(null)", getColumnForLog(), _mapping));
				}
			}
			setSelectedItem(item);
			
			logger.trace("{}: eventList - [{}].", () -> getColumnForLog(), () ->  remodel.getItemList().toString());
			logger.trace("{}: options - [{}].", () -> getColumnForLog(), () ->  remodel.getOptions().toString());
			logger.trace("{}: mappings - [{}].", () -> getColumnForLog(), () ->  remodel.getMappings().toString());
		}
	}

	/**
	 * Returns the Option corresponding to the currently selected item in the
	 * combobox.
	 *
	 * @return returns the Option object associated with the selected item
	 * OR null if nothing is selected.
	 */
	public O getSelectedOption() {
		O result = null;

		try (BaseModel<M,O,O2>.Remodel remodel = optionModel.getRemodel()) {
			Object item = getSelectedItem();
			if (item instanceof SSListItem) {
				result = remodel.getOption((SSListItem)item);
			}
		}

		return result;
	}

	/**
	 * Finds the listItem having option that matches the specified option
	 * and make it the selected listItem. If no matching item is found
	 * the _option is used for {@link #setSelectedItem(java.lang.Object) 
	 * setSelectedItem(_option)}
	 *
	 * @param _option option value of list item
	 */
	public void setSelectedOption(final O _option) {

		try (BaseModel<M,O,O2>.Remodel remodel = optionModel.getRemodel()) {
			if(!hasItems()) {
				logger.warn(String.format("%s: combobox is empty", getColumnForLog()));
				// Even if combo is empty, stick _option in editor. Do not return;
			}
			
			Object tItem = getSelectedItem();
			// Extract the option from the selected list item.
			// If not an SSListItem, it's editable, use it as current option.
			Object currentSelectedOption = tItem instanceof SSListItem
					? remodel.getOption((SSListItem)tItem)
					: tItem;
			
			// only need to proceed if there is a change.
			if (Objects.equals(_option, currentSelectedOption)) {
				return;
			}
			
			// find the first matching option in the list
			final int index = remodel.getOptions().indexOf(_option);
			
			Object item;
			if (index != -1) {
				item = remodel.get(index);
			} else {
				// Didn't find it in the list, so just use it as is.
				item = _option != null ? _option : nullItem;
				logger.warn(() -> String.format("%s: option %s not in combobox, do setSelectedItem.", getColumnForLog(), _option));
			}
			
			setSelectedItem(item);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// Deal with non-standard GlazedList UP/DOWN arrow handling
	//
	
	/**
	 * When dealing with GlazedLists (1.11) restore expected combo behavior.
	 *
	 * Per GL JavaDoc:
	 * https://javadoc.io/doc/com.glazedlists/glazedlists/latest/ca/odell/glazedlists/swing/AutoCompleteSupport.html
	 *<p>
	 *  4. typing the up arrow key when the popup is visible and the selected element is the first element causes the autocompletion to be cleared and the popup's selection to be removed.
	 *  6. typing the down arrow key when the popup is visible and the selected element is the last element causes the autocompletion to be cleared and the popup's selection to be removed
	 *<p>
	 * We want to restore the normal JComboBox behavior of not going past the first or last item. This would be the ideal case, matching JComboBox.
	 * If GlazedLists ever changes the arrow key behavior, this can be removed.
	 */
	protected void glazedListArrowHandler() {

		// ADD KEY LISTENER - INTERCEPTING KEYPRESSED APPEARS TO BLOCK GL
		getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {

			@Override
			// OVERRIDE KEYPRESSED, NOT KEYTYPED OR KEY RELEASED
			public void keyPressed(KeyEvent keyEvent) {
				int keyCode = keyEvent.getKeyCode();
				if (keyCode == KeyEvent.VK_UP) {
					logger.trace(() -> String.format("%s: Intercepted UP key.", getColumnForLog()));
					if (getSelectedIndex() == 0) {
						keyEvent.consume();
						logger.debug(() -> String.format("%s: UP key consumed.", getColumnForLog()));
					}
				} else if (keyCode == KeyEvent.VK_DOWN) {
					logger.trace(() -> String.format("%s: Intercepted DOWN key.", getColumnForLog()));
					if (getSelectedIndex() == getModel().getSize() - 1) {
						keyEvent.consume();
						logger.debug(() -> String.format("%s: DOWN key consumed.", getColumnForLog()));
					}
				}
			}
		});
	}


	/////////////////////////////////////////////////////////////////////////
	//
	// Here's SwingSet scaffolding and nullItem maintenance.
	//

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
	public SSBaseComboBoxListener getSSComponentListener() {
		return new SSBaseComboBoxListener();
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void setSSCommon(final SSCommon _ssCommon) {
		ssCommon = _ssCommon;

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
// TODO not sure SwingSet should be setting component dimensions
		setPreferredSize(new Dimension(200, 20));
		setEditable(false); // NOTE: GLAZED LIST WILL OVERRIDE THIS
	}

	/**
	 * After this, make some adjustments.
	 * {@inheritDoc }
	 */
	@Override
	public void setAllowNull(boolean _allowNull) {
		ssCommon.setAllowNull(_allowNull);
		adjustForNullItem();
	}

// TODO: Review this. GlazedLists is probably still calling it so need to detect those calls.
//	/**
//	 * SwingSet combo boxes works under the assumption that the only time a combo is editable is when GlazedLists
//	 * are in use.
//	 * <p>
//	 * Don't call this yourself.
//	 * {@inheritDoc }
//	 */
//	@Override
//	@Deprecated
//	public void setEditable(boolean _editable) {
//		if (_editable && getAutoComplete() == null) {
//			logger.warn(String.format("%s: SwingSet requires non-editable combo boxes.", getColumnForLog()));
//		} else {
//			super.setEditable(_editable);
//		}
//	}

	/**
	 * Catch this to make some adjustments after a change in metadata;
	 * in particular checking for a change in nullability.
	 * {@inheritDoc }
	 */
	@Override
	public void metadataChange() {
		adjustForNullItem();
	}

	/**
	 * Create a null item for the itemList
	 * @param remodel context for the creation of the nullItem.
	 * @return the created null item.
	 */
	protected abstract SSListItem createNullItem(BaseModel<M,O,O2>.Remodel remodel);

	/**
	 * A combobox used as a navigator has some restriction; for example,
	 * it can not have nullItem. Override this as needed.
	 * @return true if this ComboBox is a navigator
	 */
	protected boolean isComboBoxNavigator() {
		return false;
	}

	/**
	 * Override the default implementation to take combobox navigator
	 * into account. If navigator then always false, don't need to check
	 * database.
	 * @return true if combo box can have null value
	 */
	@Override
	public boolean getAllowNull() {
		return !isComboBoxNavigator() && getSSCommon().getAllowNull();
	}

	/**
	 * Add or remove the nullItem from the itemList if needed depending
	 * on {@code getAllowNull()} and the current existence of a nullItem.
	 * If logical null is selected before, keep it selected after.
	 */
	protected void adjustForNullItem() {
		// 2021-01-20_BP: Slight modification needed to avoid Beep in combo navigator on
		//   insert row as SSDBNavImpl calls setSelectionPending(true) and that will fail
		//   for the combo navigator without this tweak.
		//boolean wantNull = (getAllowNull() || selectionPending) && !isComboBoxNavigator();
		boolean wantNull = getAllowNull() || selectionPending;
		boolean hasNull = nullItem != null;
		
		if (wantNull == hasNull) {
			// things are set up as wanted
			return;
		}
		try (BaseModel<M,O,O2>.Remodel remodel = optionModel.getRemodel()) {
			// Need to add or remove the nullItem
			// Since selection is based on item, not index, we only need
			// to consider adjusting the selection if the current selection:
			// A) null/-1 and nullItem is being added then select nullItem/0
			// B) nullItem/0 and nullItem is being removed then select null/-1.
			//
			// The whole idea is that if null was selected before this
			// change, want it seleted after the change.
			//
			// If listeners are enabled, may exceptions about using null
			boolean selectNull = false;
			boolean selectNone = false;
			if (wantNull) {
				selectNull = super.getSelectedIndex() == -1;
				//nullItem = remodel.createComboItem(null, "", null);
				nullItem = createNullItem(remodel);
				remodel.add(0, nullItem);
			} else {
				selectNone = super.getSelectedItem() == nullItem;
				remodel.remove(nullItem);
				nullItem = null;
			}

			// Only manipulate listeners and selection if needed.
			// Note that if selected item was not null, then followin is not entered.
			if (selectNull || selectNone) {
				final ActionListener[] listeners = unregisterAllActionListeners(this);
				try {
					if (selectNull) {
						super.setSelectedItem(nullItem);
					}
					if (selectNone) {
						super.setSelectedItem(null);
					}
				} finally {
					registerAllActionListeners(this, listeners);
				}
			}
		}
	}
	
	/**
	 * Common code to update the rowset based on getSelectedMapping().
	 */
	private void updateRowset() {
		
		ssCommon.removeRowSetListener();
		
		M mapping = getSelectedMapping();
	
		if (mapping == null) {
			logger.debug(() -> String.format("%s: Setting to null.", getColumnForLog()));
			setBoundColumnText(null);
		} else {
			logger.debug(() -> String.format("%s: Setting to %s.",  getColumnForLog(), mapping));
			// TODO: need to avoid setting to same value
			// for NavGroupState. Wonder why, avoids event?
			//setBoundColumnText(String.valueOf(mapping));

			// not sure this is a reliable way to check.
			// TODO: check should probably be in
			//       setBoundColumnText or RowSetOps.updateColumnText

			String tStringMapping = String.valueOf(mapping);
			// 2021-02-22_BP: RowSet does not seem to support 'dirty' reads so 
			// a call to getBoundColumnText() won't reflect any updates using
			// setBoundColumnText() until after a call to updateRow().
			
			//if (!Objects.equals(getBoundColumnText(), tStringMapping)) {
				setBoundColumnText(tStringMapping);
			//}
		}
	
		ssCommon.addRowSetListener();
	}
	
	/**
	 * Updates the value stored and displayed in the SwingSet component based on
	 * getBoundColumnText().
	 * <p>
	 * Call to this method should be coming from SSCommon and should already have
	 * the Component listener removed.
	 * <p>
	 * This is a quick fix for https://github.com/bpangburn/swingset/issues/46
	 * As discussed in Issue 46, @errael has proposed a more thorough fix to work
	 * with any Mapping type, but it requires additional dependencies (Guava)
	 * which we'd like to avoid for SwingSet 4.x. The generic solution should be
	 * integrated into SwingSet 5 and the changes can be found here:
	 * https://github.com/errael/swingset/tree/updateSSComponent-into-SSBaseComboBox
	 * <p>
	 * As written this method will only work with the two current implementations of
	 * SSBaseComboBox: SSComboBox where M is Integer and SSDBComboBox where M is Long.
	 */
	@Override
	public void updateSSComponent() {
		// TODO Modify this class similar to updateSSComponent() in SSFormattedTextField and only limit JDBC types accepted
		try {
			// If initialization is taking place then there won't be any mappings so don't try to update anything yet.
			if (!hasItems()) {
				return;
			}

			// Maybe insures blank in case of later exception.
			setSelectionPending(true);

			// SSDBComboBox will generally work with primary key column data queried from the database, which will generally be of data type long.
			// SSComboBox is generally used with 2 or 4 byte integer columns.
			final String boundColumnText = getBoundColumnText();

			// LOGGING
			logger.debug(() -> String.format("%s: getBoundColumnText() - %s", getColumnForLog(), boundColumnText));
			
			// GET THE BOUND VALUE STORED IN THE ROWSET - may throw a NumberFormatException
			Object objValue = null;
			if ((boundColumnText != null) && !boundColumnText.isEmpty()) {
				// https://github.com/bpangburn/swingset/issues/46
				if (this instanceof SSComboBox) {
					objValue = Integer.parseInt(boundColumnText);
				} else if (this instanceof SSDBComboBox) {
					objValue = Long.parseLong(boundColumnText);
				} else {
					throw new Exception();
				}
			}
			@SuppressWarnings("unchecked")
			M targetValue = (M) objValue;
			
			// LOGGING
			logger.debug(() -> String.format("%s: targetValue - %s", getColumnForLog(), targetValue));
			
			// UPDATE COMPONENT
			setSelectedMapping(targetValue);// setSelectedMapping() should handle null OK.}

		} catch (final NumberFormatException nfe) {
			JOptionPane.showMessageDialog(this, String.format(
					"Encountered database value of '%s' for column [%s], which cannot be converted to a number.", getBoundColumnText(), getColumnForLog()));
			logger.error(getColumnForLog() + ": Number Format Exception.", nfe);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, String.format(
					"Expecting SSComboBox or SSDBComboBox, but component for column [%s] is of type %s.", this.getClass(), getColumnForLog()));
			logger.error(getColumnForLog() + ": Unknown SwingSet component of " + this.getClass() + ".", e);
		}
	}

	//
	// unregisterAllActionListeners/registerAllActionListeners kudos glazed lists
	//
	/**
	 * A convenience method to unregister and return all {@link ActionListener}s
	 * currently installed on the given <code>comboBox</code>. This is the only
	 * technique we can rely on to prevent the <code>comboBox</code> from
	 * broadcasting {@link ActionEvent}s at inappropriate times.
	 * <p>
	 * This method is the logical inverse of {@link #registerAllActionListeners}.
	 * <p>
	 * Note that the ActionListener used for binding will be removed without
	 * passing through SSCommon.removeSSComponentListener()
	 * 
	 * @param comboBox combo box from which to remove listeners
	 * @return array of ActionListeners removed from combo box (for adding back later)
	 */
	// TODO: Consider passing in SSBaseComboBox and identifying SSBaseComboBoxListener
	static ActionListener[] unregisterAllActionListeners(JComboBox<?> comboBox) {
		final ActionListener[] listeners = comboBox.getActionListeners();
		for (ActionListener listener : listeners) {
			comboBox.removeActionListener(listener);
		}

		return listeners;
	}

	/**
	 * A convenience method to register all of the given <code>listeners</code>
	 * with the given <code>comboBox</code>.
	 * <p>
	 * This method is the logical inverse of {@link #unregisterAllActionListeners}.
	 * <p>
	 * Note that the ActionListener used for binding will be removed without
	 * passing through SSCommon.addSSComponentListener()
	 * 
	 * @param comboBox combo box for which to add listeners
	 * @param listeners array of ActionListners to be 
	 */
	// TODO: Consider passing in SSBaseComboBox and identifying SSBaseComboBoxListener
	static void registerAllActionListeners(JComboBox<?> comboBox, ActionListener[] listeners) {
		for (ActionListener listener : listeners) {
			comboBox.addActionListener(listener);
		}
	}

	////////////////////////////////////////////////////////////////////////
	//
	// THIS getSelectedIndex() IS A TRANSITION AID.
	//
	// Remove it when...

	/**
	 * If getAllowNull() then throw an exception if this method is used
	 * from "com.nqadmin" outside of swingset itself.
	 * {@inheritDoc }
	 * @deprecated Avoid using getSelectedIndex() unless you are very familiar with GlazedLists and SwingSet nullItem
	 */
	@Override
	@Deprecated
	public int getSelectedIndex() {

		StackTraceElement[] stack = new Throwable().getStackTrace();
		String caller = stack.length >= 2 ? stack[1].getClassName() : "";

		if((caller.startsWith("com.nqadmin") && !caller.startsWith("com.nqadmin.swingset")
				|| caller.startsWith("com.nqadmin.swingset.demo")
			) && ssCommon != null && ssCommon.getAllowNull()) {
			throw new IllegalStateException("App::getSelectedIndex && getAllowNull()");
		}

		return super.getSelectedIndex();
	}
	
	/**
	 * {@inheritDoc }
	 * @deprecated Avoid using setSelectedIndex() unless you are very familiar with GlazedLists and SwingSet nullItem
	 */
	@Override
	@Deprecated
	public void setSelectedIndex(int _index) {
		super.setSelectedIndex(_index);
	}

	////////////////////////////////////////////////////////////////////////
	//
	// for testing
	//

	/**
	 * Can call this from an example for example. Tested with example4.
	 * @param combo test this
	 */
	// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
	@SuppressWarnings({"all","UseOfSystemOutOrSystemErr"})
	public static void testComboAdjustForNull(SSComboBox combo) {
		tracker = 0;
		trackout.clear();
		track("start");
		combo.setAllowNull(false);
		p(combo); // example4: n 3, i 0, item {Red,0}

		// to get into the mood
		// my cause an EXCEPTION
		track("select -1");
		combo.setSelectedIndex(-1);
		p(combo); // example4: n 3, i -1, item null

		// should not see an exception
		// and should see consistent state

		track("allow null true");
		combo.setAllowNull(true);
		p(combo); // example4: n 4, i 0, item {,null}

		// track("select 0");
		// combo.setSelectedIndex(0);
		// p(combo);

		track("allow null false");
		combo.setAllowNull(false);
		p(combo); // example4: n 3, i -1, item null

		// track("select -1");
		// combo.setSelectedIndex(-1);
		// p(combo);


		System.err.println("summary");
		trackout.forEach(s -> System.err.println(s));
		System.err.println("done");
	}
	
	private static final List<String> trackout = new ArrayList<>();
	private static int tracker;
	
	// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
	@SuppressWarnings({"all","UseOfSystemOutOrSystemErr"})
	private static void track(String tag) {
		String s = "tag: " + tracker + " " + tag;
		trackout.add(s);
		System.err.println(s);
		tracker++;
	}
	
	// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
	@SuppressWarnings({"all","UseOfSystemOutOrSystemErr"})
	private static void p(SSComboBox combo) {
		String s = "allowNull " + combo.getAllowNull();
		trackout.add(s);
		System.err.println(s);
		p(combo.getItemCount(), combo.getSelectedIndex(), combo.getSelectedItem());
	}
	
	// TODO: See if we can remove "all" in later JDK, but may be IDE-specific.
	@SuppressWarnings({"all","UseOfSystemOutOrSystemErr"})
	private static void p(int n, int i, Object item) {
		String s = String.format("n %d, i %d, item %s", n, i, item);
		trackout.add(s);
		System.err.println(s);
	}
}
