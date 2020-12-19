/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

// OptionMappingSwingModel.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * This class adds support for GlazedLists locking,
 * see {@link Remodel}.
 * @param <M> mapping type; mapping is typically primary key
 * @param <O> option type; option provides display string
 * @param <O2>  option2 type; if present, supplementary display string
 * 
 * @see <a href="https://javadoc.io/doc/com.glazedlists/glazedlists/latest/ca/odell/glazedlists/swing/AutoCompleteSupport.html" target="_blank">GlazedLists AutoCompletion javadoc</a>
 * @see <a href="https://publicobject.com/glazedlistsdeveloper/screencasts/autocompletesupport/AutoCompleteSupport.wmv" target="_blank">GlazedLists AutoCompletion Video</a>
 * 
 * @since 4.0.0
 */
public class GlazedListsOptionMappingInfo<M,O,O2> extends OptionMappingSwingModel<M,O,O2> {
	private static final long serialVersionUID = 1L;
	private EventList<SSListItem> eventList;
	private boolean hasReturnedEventList;

	/**
	 * Create an empty ComboInfo.
	 * @param _option2Enabled true says to provide an options2 field in SSListItem
	 * @param _eventList which is installed into AutoCompleteSupport
	 */
	public GlazedListsOptionMappingInfo(boolean _option2Enabled, EventList<SSListItem> _eventList) {
		super(_option2Enabled, _eventList);
		eventList = _eventList;
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

	// protected GlazedListsOptionMappingInfo(int itemNumElems, List<SSListItem> itemList) {
	// 	super(itemNumElems, itemList);
	// 	eventList = (EventList<SSListItem>) itemList;
	// }

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
	 * Remodel that locks the GlazedLists EventList.
	 */
	public class Remodel extends OptionMappingSwingModel<M, O, O2>.Remodel implements AutoCloseable {

		/**
		 * This is called during construction,
		 * take the EventList's write lock.
		 */
		@Override
		protected void takeWriteLock() {
			eventList.getReadWriteLock().writeLock().lock();
		}

		/**
		 * This is called during close,
		 * release the EventList's write lock.
		 */
		@Override
		protected void releaseWriteLock() {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}
	
}
