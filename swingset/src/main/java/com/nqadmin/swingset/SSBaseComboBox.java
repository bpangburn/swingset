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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

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
 * Initially no Option2.
 * @param <M> mapping type
 * @param <O> option type
 * @param <O2> option2 type
 * @since 4.0.0
 *
 */
//
// TODO: There are more things that can be pulled into here.
//       The following are identical in both subclasses, there may be more.
//       - protected class ComboBoxListener ....
//       - protected final ComboBoxListener ssComboBoxListener = new ComboBoxListener();
//       - getSelectedMapping()
//       - public void addSSComponentListener() {...}
//       - public void removeSSComponentListener() {...}
//       - public void customInit() {...}
//       - getMappings()
//       - getOptions()
//       - 
//
public abstract class SSBaseComboBox<M,O,O2> extends JComboBox<SSListItem> implements SSComponentInterface
{
	private static final long serialVersionUID = 1L;

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
		protected static <M,O,O2>BaseModel<M,O,O2> install(JComboBox<SSListItem> _jc) {
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
		protected static <M,O,O2>BaseGlazedModel<M,O,O2> install(JComboBox<SSListItem> _jc) {
			BaseGlazedModel<M,O,O2> model = new BaseGlazedModel<>();
			model.autoComplete = AutoCompleteSupport.install(_jc, model.getEventList(), null, model.getListItemFormat());
			model.autoComplete.setFilterMode(TextMatcherEditor.CONTAINS);
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
	 * When {@link #getAllowNull() } is true, this is the null item;
	 * when false this is null. So {@link #setSelectedItem(java.lang.Object)
	 * setSelectedItem(nullItem)} does the right thing whether getAllowNull()
	 * is true or false.
	 * <p>
	 * <b>When the item list is cleared, for example
	 * {@link OptionMappingSwingModel.Remodel#clear() remodel.clear()},
	 * the nullItem must be set to null.</b>
	 * @see #createNullItem(com.nqadmin.swingset.models.OptionMappingSwingModel.Remodel)
	 */
	protected SSListItem nullItem;

	/**
	 * Common fields shared across SwingSet components
	 */
	protected SSCommon ssCommon = new SSCommon(this);

	/**
	 * The combo model.
	 */
	protected OptionMappingSwingModel<M,O,O2> optionModel;

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
	 * After this, make some adjustments.
	 * {@inheritDoc }
	 */
	@Override
	public void setAllowNull(boolean _allowNull) {
		ssCommon.setAllowNull(_allowNull);
		adjustForNullItem();
	}

	/**
	 * Catch this to make some adjustments after a change in metadata.
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
	 */
	@Override
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

	////////////////////////////////////////////////////////////////////////
	//
	// for testing
	//

	/**
	 * Can call this from an example for example. Tested with example4.
	 * @param combo test this
	 */

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
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
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static void track(String tag) {
		String s = "tag: " + tracker + " " + tag;
		trackout.add(s);
		System.err.println(s);
		tracker++;
	}
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static void p(SSComboBox combo) {
		String s = "allowNull " + combo.getAllowNull();
		trackout.add(s);
		System.err.println(s);
		p(combo.getItemCount(), combo.getSelectedIndex(), combo.getSelectedItem());
	}
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static void p(int n, int i, Object item) {
		String s = String.format("n %d, i %d, item %s", n, i, item);
		trackout.add(s);
		System.err.println(s);
	}
}
