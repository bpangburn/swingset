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
/* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2025, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/

package com.nqadmin.swingset.models;

import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;

/** Simple JList or JComboBox model that handle SSLIstItem 
 * without locking or listItemFormat. An instance is used as
 * either a {@link javax.swing.ComboBoxModel} or a
 * {@link javax.swing.ListModel}.
 * <p>
 * Example usage as an inner
 * class where all the combo data is accessed from an outer class
 * by the index in the comboList.
 * 
 * {@snippet class=SimpleComboListSwingModelsSnippets region=init1}
 */
// TODO: add <T> to avoid user's cast of get*Model() ???
public abstract class SimpleComboListSwingModel extends AbstractComboBoxListSwingModel
{
	/**
	 * Bounce to super.Typical usage:
	 * @param itemNumElems see super
	 * @param itemList  see super
	 */
	public SimpleComboListSwingModel(int itemNumElems, List<SSListItem> itemList) {
		super(itemNumElems, itemList);
	}

	/**
	 * @return this as a ListModel
	 */
	protected ListModel<?> getListModel() {
		return AbstractComboBoxListSwingModel.getSimpleListModel(this);
	}

	/**
	 * @return this as a ComboModel
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
	protected void remodelReleaseWriteLock(AbstractComboBoxListSwingModel.Remodel remodel) {
	}

	/**
	 * A no locking, re-use the model.
	 */
	final public class Remodel extends AbstractComboBoxListSwingModel.Remodel { }
	private final Remodel remodel = new Remodel();

	/** {@inheritDoc} */
	@Override
	public final Remodel getRemodel() {
		// default is no locking, re-use the model.
		return remodel;
	}
}
