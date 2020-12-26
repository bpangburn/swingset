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

// SSBaseComboBox.java

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nqadmin.swingset.models.AbstractComboBoxListSwingModel;
import com.nqadmin.swingset.models.GlazedListsOptionMappingInfo;
import com.nqadmin.swingset.models.OptionMappingSwingModel;
import com.nqadmin.swingset.models.SSListItem;
import com.nqadmin.swingset.utils.SSCommon;
import com.nqadmin.swingset.utils.SSComponentInterface;

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
	 * Listener(s) for the component's value used to propagate changes back to bound
	 * database column.
	 */
	protected class SSBaseComboBoxListener implements ActionListener, Serializable
	{
		private static final long serialVersionUID = -3131533966245488092L;

		/** {@inheritDoc} */
		@Override
		public void actionPerformed(final ActionEvent ae) {

			removeRowSetListener();

			M mapping = getSelectedMapping();

			if (mapping == null) {
				logger.debug(() -> String.format("%s: Setting to null.", getColumnForLog()));
				setBoundColumnText(null);
			} else {
				logger.debug(String.format("%s: Setting to %s.",  getColumnForLog(), mapping));
				// TODO: need to avoid setting to same value
				// for NavGroupState. Wonder why, avoids event?
				//setBoundColumnText(String.valueOf(mapping));

				// not sure this is a reliable way to check.
				// TODO: check should probably be in
				//       setBoundColumnText or RowSetOps.updateColumnText

				String tStringMapping = String.valueOf(mapping);
				if (!getBoundColumnText().equals(tStringMapping)) {
					setBoundColumnText(tStringMapping);
				}
			}

			addRowSetListener();
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
		private static final long serialVersionUID = 1L;

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
		private static final long serialVersionUID = 1L;

		/**
		 * The GlazedLists auto completion support for the
		 * associated JComboBox.
		 */
		protected AutoCompleteSupport<SSListItem> autoComplete;

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
			// RESTORE JCOMBOBOX UP/DOWN ARROW HANDLING OVERRIDING GLAZEDLIST
			_jc.glazedListArrowHandler();
			//model.autoComplete.setStrict(true);
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
	private static final Logger logger = LogManager.getLogger();

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

	/**
	 * Common fields shared across SwingSet components
	 */
	protected SSCommon ssCommon = new SSCommon(this);

	/**
	 * Component listener.
	 */
	protected final SSBaseComboBoxListener ssBaseComboBoxListener = new SSBaseComboBoxListener();

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
	public void setModel(ComboBoxModel<SSListItem> model) {
		optionModel = model instanceof BaseModel ? (BaseModel<M,O,O2>)model
				: model instanceof BaseGlazedModel ? (BaseGlazedModel<M,O,O2>)model
				: null;

		super.setModel(model);
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
				logger.warn(String.format("%s: No mapping available for %s in combobox, setSelectedItem(null)", getColumnForLog(), _mapping));
			}
			setSelectedItem(item);
			
			logger.trace("{}: eventList - [{}].", () -> getColumnForLog(), () ->  remodel.getItemList().toString());
			logger.trace("{}: options - [{}].", () -> getColumnForLog(), () ->  remodel.getOptions().toString());
			logger.trace("{}: mappings - [{}].", () -> getColumnForLog(), () ->  remodel.getMappings().toString());
		}
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
                    System.out.println("");
                    if (getSelectedIndex() == 0) {
                        keyEvent.consume();
                        logger.debug(() -> String.format("%s: UP key consumed.", getColumnForLog()));
                    }
                } else if (keyCode == KeyEvent.VK_DOWN) {
                	logger.trace(() -> String.format("%s: Intercepted DOWN key.", getColumnForLog()));
                    if (getSelectedIndex() == getModel().getSize()-1) {
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
	public void setSSCommon(final SSCommon _ssCommon) {
		ssCommon = _ssCommon;

	}

	/**
	 * Adds any necessary listeners for the current SwingSet component. These will
	 * trigger changes in the underlying RowSet column.
	 */
	@Override
	public void addSSComponentListener() {
		addActionListener(ssBaseComboBoxListener);

	}

	/**
	 * Removes any necessary listeners for the current SwingSet component. These
	 * will trigger changes in the underlying RowSet column.
	 */
	@Override
	public void removeSSComponentListener() {
		removeActionListener(ssBaseComboBoxListener);
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
// TODO This was added during SwingSet rewrite 4/2020. Need to confirm it doesn't break anything.
		setEditable(false);
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
	 * Add or remove the nullItem from the itemList if needed depending
	 * on {@code getAllowNull()} and the current existence of a nullItem.
	 * If logical null is selected before, keep it selected after.
	 */
	protected void adjustForNullItem() {
		boolean wantNull = getAllowNull() && !isComboBoxNavigator();
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

			// only manipulate listeners and selection if needed
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

	//
	// unregisterAllActionListeners/registerAllActionListeners kudos glazed lists
	//
	/**
	 * A convenience method to unregister and return all {@link ActionListener}s
	 * currently installed on the given <code>comboBox</code>. This is the only
	 * technique we can rely on to prevent the <code>comboBox</code> from
	 * broadcasting {@link ActionEvent}s at inappropriate times.
	 *
	 * This method is the logical inverse of {@link #registerAllActionListeners}.
	 * 
	 * @param comboBox combo box from which to remove listeners
	 * @return array of ActionListeners removed from combo box (for adding back later)
	 */
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
	 *
	 * This method is the logical inverse of {@link #unregisterAllActionListeners}.
	 * 
	 * @param comboBox combo box for which to add listeners
	 * @param listeners array of ActionListners to be 
	 */
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
