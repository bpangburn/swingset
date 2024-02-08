/* *****************************************************************************
 * Copyright (C) 2024, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/
package com.nqadmin.swingset.navigate;

import java.awt.KeyboardFocusManager;

import javax.sql.RowSet;

import com.google.common.eventbus.EventBus;
import com.nqadmin.swingset.utils.CentralLookup;
import com.nqadmin.swingset.utils.SSComponentInterface;

/**
 * TODO: Replace EventBUs with https://dagger.dev/ and RxJava
 *			https://www.baeldung.com/rx-java
 */
public class Utils
{
	private Utils() { }


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
	 * @param _value new value
	 */
	public static void postRowSetModified(SSComponentInterface source, Object _value)
	{
		// May want to extend to handling local EventBus per Frame/Panel;
		// Use either/both source/rs to find a local eventBus.
		getLocalEventBus(source, source.getRowSet())
				.post(new RowSetModificationEvent(source, _value));
	}

	/**
	 * Post an error modification event; prefer a local eventBus.
	 * @param source SSComponent modifying the rowset
	 * @param _value new value
	 */
	public static void postRowSetModifiedError(SSComponentInterface source, Object _value)
	{
		getLocalEventBus(source, source.getRowSet())
				.post(new RowSetModificationEvent(source, _value, true));
	}

	/**
	 * EventBus to use Frame/Panel events
	 */
	private static EventBus globalEventBus = null;

	/**
	 * Get the global EventBus.
	 * @return EventBus for this
	 */
	public static EventBus getGlobalEventBus()
	{
		// TODO: CentralLookup could be set up by app, or some general init.
		if (globalEventBus == null) {
			globalEventBus = CentralLookup.getDefault().lookup(EventBus.class);
			if(globalEventBus == null) {
				globalEventBus = new EventBus("SwingSetGlobal");
				CentralLookup.getDefault().add(globalEventBus);
			}

			// TODO: be more careful about tacking who's managing focus
			//		 and the current focusOwner
			KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.addPropertyChangeListener("focusOwner",
							(pce) -> globalEventBus.post(new FocusChangeEvent(pce)));
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
	public static EventBus getLocalEventBus(Object component, RowSet rs)
	{
		// TODO: per navigator or per row set or per NavGroup event bus
		// TODO: get rid of this method, create local event bus
		//       as client property of root pane/panel of NavGroup
		return getGlobalEventBus();
	}
	
}
