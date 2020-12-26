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
package com.nqadmin.swingset.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sql.RowSet;

import com.google.common.eventbus.EventBus;

/**
 *
 * @author err
 */
public class SSUtils {
	private SSUtils() {}

	//private static final Logger logger = LogManager.getLogger();

	/**
	 * Returns an unmodifiable list containing an arbitrary number of elements.
	 * This is not particularly efficient for small lists, but until java-9...
	 * @param <T> type of elements in the list
	 * @param args the elements of the list
	 * @return list
	 */
	@SafeVarargs
	static <T> List<T> listOf(T... args) {
		Object[] arr = Arrays.copyOf(args, args.length);
		@SuppressWarnings("unchecked")
		List<T> list = (List<T>) Collections.unmodifiableList(Arrays.asList(arr));
		return list;
	}
	////////////////////////////////////////////////////////////////////////////
	//
	// Debug Support
	//

	/**
	 * Return a unique name for an Object, for example "String@89AB".
	 * Name is SimpleClassName followed by identityHashCode in hex.
	 * Used primarily for debug messages.
	 * @param o The Object
	 * @return unique name for the object or "null"
	 */
	// TODO: put this in utils/SSUtil
	public static String objectID(Object o) {
		if (o == null) {
			return "null";
		}
		return String.format("%s@%X", o.getClass().getSimpleName(), System.identityHashCode(o));
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// EventBus
	//
	//     posting Events
	//     finding a bus
	//

	/**
	 * Post a modification event; prefer a local eventBus.
	 * @param source SSComponent modifying the rowset
	 * @param _rowSet The modified row set
	 */
	public static void postRowSetModified(SSComponentInterface source, RowSet _rowSet) {
		// May want to extend to handling local EventBus per Frame/Panel;
		// Use either/both source/rs to find a local eventBus.
		getLocalEventBus(source, _rowSet)
				.post(new RowSetModificationEvent(source, _rowSet));
	}

	/**
	 * Post an error modification event; prefer a local eventBus.
	 * @param source SSComponent modifying the rowset
	 * @param _rowSet The modified row set
	 */
	public static void postRowSetModifiedError(SSComponentInterface source, RowSet _rowSet) {
		getLocalEventBus(source, _rowSet)
				.post(new RowSetModificationEvent(source, _rowSet, true));
	}

	/**
	 * EventBus to use Frame/Panel events
	 */
	private static EventBus globalEventBus = null;

	/**
	 * Get the global EventBus.
	 * @return EventBus for this
	 */
	public static EventBus getGlobalEventBus() {
		// TODO: CentralLookup could be set up by app, or some general init.
		if (globalEventBus == null) {
			globalEventBus = CentralLookup.getDefault().lookup(EventBus.class);
			if(globalEventBus == null) {
				globalEventBus = new EventBus("SwingSetGlobal");
				CentralLookup.getDefault().add(globalEventBus);
			}
		}
		return globalEventBus;
	}

	/**
	 * Find the EventBus associated with the NavGroup to which
	 * the component belongs.
	 * @param component typically JComponent that wants the eventBus
	 * @param rs RowSet involved in EventBus
	 * @return component's NavGroup EventBus
	 */
	public static EventBus getLocalEventBus(Object component, RowSet rs) {
		// TODO: per navigator or per row set or per NavGroup event bus
		// TODO: get rid of this method, create local event bus
		//       as client property of root pane/panel of NavGroup
		return getGlobalEventBus();
	}

}
