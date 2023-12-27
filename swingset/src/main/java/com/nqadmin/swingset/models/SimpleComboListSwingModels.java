/* *****************************************************************************
 * Copyright (C) 2023, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

package com.nqadmin.swingset.models;

import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;

/** Simple JList JComboBox models that handle SSLIstItem 
 * without locking or listItemFormat.
 * <p>
 * Example usage as an inner
 * class where all the combo data is accessed from an outer class
 * by the index in the comboList.
 * <pre>
 * {@code 
 * // outer class does
 * 	ComboBoxModel<MyComboModels.MyComboItem> model
 * 		= new MyComboModels().getComboModel()
 * 
 * private final class MyComboModels extends SimpleComboListSwingModels {
 * 	
 * 	private MyComboModels() {
 * 		super(2, new ArrayList<>(items.length));
 * 		// initialization
 * 		for (int i = 0; i < items.length; i++) {
 * 			getRemodel().add(new MyComboItem(i));
 * 		}
 * 	}
 * 
 * 	@SuppressWarnings("unchecked")
 * 	@Override
 * 	public ComboBoxModel<MyComboItem> getComboModel() {
 * 		return (ComboBoxModel<MyComboItem>) super.getComboModel();
 * 	}
 * 
 * 	class MyComboItem implements ListItem0, Cloneable {
 * 		private final int listIdx;
 * 
 * 		public MyComboItem(int _listIdx) { listIdx = _listIdx; }
 * 		// ...
 * 	}
 * }
 * }
 * </pre>
 * {@inheritDoc} */
public abstract class SimpleComboListSwingModels extends AbstractComboBoxListSwingModel
{

	/**
	 * Bounce to super. Typical usage:
	 * @param _itemNumElems see super
	 * @param _itemList  see super
	 */
	public SimpleComboListSwingModels(int _itemNumElems, List<SSListItem> _itemList) {
		super(_itemNumElems, _itemList);
	}

	/** {@inheritDoc}
	 */
	protected ListModel<?> getListModel() {
		return AbstractComboBoxListSwingModel.getSimpleListModel(this);
	}

	/** {@inheritDoc}
	 */
	protected ComboBoxModel<?> getComboModel() {
		return AbstractComboBoxListSwingModel.getSimpleComboBoxModel(this);
	}

	/** {@inheritDoc} */
	@Override
	protected void checkState() {
	}

	/** {@inheritDoc} */
	@Override
	protected void remodelTakeWriteLock() {
	}

	/** {@inheritDoc} */
	@Override
	protected void remodelReleaseWriteLock(AbstractComboBoxListSwingModel.Remodel _remodel) {
	}

	/** {@inheritDoc} */
	final public class Remodel extends AbstractComboBoxListSwingModel.Remodel { }
	private final Remodel remodel = new Remodel();

	/** {@inheritDoc} */
	@Override
	public final Remodel getRemodel() {
		// default is no locking, re-use the model.
		return remodel;
	}
    
}
