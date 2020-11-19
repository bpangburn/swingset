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

// DefaultGlazedListComboInfo.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * This class adds support for GlazedLists locking.
 * @see <a target="_top" href="https://javadoc.io/doc/com.glazedlists/glazedlists/latest/ca/odell/glazedlists/swing/AutoCompleteSupport.html">GlazedLists AutoCompletion javadoc</a>
 * @see <a href="https://publicobject.com/glazedlistsdeveloper/screencasts/autocompletesupport/">GlazedLists AutoCompletion Video</a>
 * @since 4.0.0
 */
public abstract class SSAbstractGlazedListComboInfo extends SSAbstractListInfo {

	protected SSAbstractGlazedListComboInfo(int itemNumElems, EventList<SSListItem> itemList) {
		super(itemNumElems, itemList);
	}

	/**
	 * Remodel that locks the GlazedLists EventList.
	 */
	protected abstract class Remodel extends SSAbstractListInfo.Remodel implements AutoCloseable {

		/**
		 * This is called during construction,
		 * take the EventList's write lock.
		 */
		@Override
		protected void takeWriteLock() {
			((EventList<SSListItem>) itemList).getReadWriteLock().writeLock().lock();
		}

		/**
		 * This is called during close,
		 * release the EventList's write lock.
		 */
		@Override
		protected void releaseWriteLock() {
			((EventList<SSListItem>) itemList).getReadWriteLock().writeLock().unlock();
		}
	}
	
}
