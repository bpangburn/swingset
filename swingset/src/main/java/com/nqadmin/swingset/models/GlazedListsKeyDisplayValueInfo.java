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
package com.nqadmin.swingset.models;

import ca.odell.glazedlists.EventList;

/**
 * This class adds support for GlazedLists EventList and locking,
 * see {@link KeyDisplayValueSwingModel.Remodel}.
 * @param <K> key type; key is typically primary key
 * @param <D> displayValue type; displayValue provides display string
 * @param <D2>  displayValue2 type; if present, supplementary display string
 * 
 * @see <a href="https://javadoc.io/doc/com.glazedlists/glazedlists/latest/ca/odell/glazedlists/swing/AutoCompleteSupport.html" target="_blank" rel="noopener noreferrer">GlazedLists AutoCompletion javadoc</a>
 * @see <a href="https://publicobject.com/glazedlistsdeveloper/screencasts/autocompletesupport/AutoCompleteSupport.wmv" target="_blank" rel="noopener noreferrer">GlazedLists AutoCompletion Video</a>
 * 
 * @since 4.0.0
 */
public class GlazedListsKeyDisplayValueInfo<K,D,D2> extends KeyDisplayValueSwingModel<K,D,D2> {
	private final EventList<SSListItem> eventList;
	private boolean hasReturnedEventList;

	/**
	 * Create an empty ComboInfo.
	 * @param displayValue2Enabled true says to provide an options2 field in SSListItem
	 * @param eventList which is installed into AutoCompleteSupport
	 */
	public GlazedListsKeyDisplayValueInfo(boolean displayValue2Enabled,
										  EventList<SSListItem> eventList) {
		super(displayValue2Enabled, eventList);
		this.eventList = eventList;
	}

	/**
	 * This dance only returns the event list once; it helps make it
	 * clear that no reference should be held to the list. All access
	 * to the list should be done through this object and remodel.
	 * There is {@link AbstractComboBoxListSwingModel#getItemList}
	 * for a read only reference.
	 * @return GlazedLists event list.
	 */
	protected EventList<SSListItem> getEventList() {
		EventList<SSListItem> temp = hasReturnedEventList ? null : eventList;
		hasReturnedEventList = true;
		return temp;
	}

	/**
	 * The Remodel is the core object to support locked access to this object.
	 * It has methods for inspecting and changing the EventList and
	 * the contents of the SSListItems that it contains.
	 * <p>
	 * This getRemodel object can be used with try with resources.
	 * It returns with the EventList write locked, and its close method
	 * releases the lock.
	 * @return a Remodel with read/write access
	 */
	@Override
	public Remodel getRemodel() {
		return new Remodel();
	}
	
	/**
	 * This is called during Remodel construction,
	 * take the EventList's write lock.
	 */
	@Override
	protected void remodelTakeWriteLock() {
		eventList.getReadWriteLock().writeLock().lock();
	}
	
	/**
	 * This is called during Remodel close,
	 * release the EventList's write lock.
	 * NOTE: {@code remodel.isClosed = true} prevents re-use of the remodel.
	 * @param remodel base remodel
	 */
	@Override
	protected void remodelReleaseWriteLock(AbstractComboBoxListSwingModel.Remodel remodel) {
		eventList.getReadWriteLock().writeLock().unlock();
		remodel.isClosed = true;
	}
	
}
