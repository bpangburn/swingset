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
import java.lang.System.Logger.Level;
import java.sql.SQLException;

import javax.sql.RowSet;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.utils.CentralLookup;
import com.nqadmin.swingset.utils.SSComponentInterface;

import static com.nqadmin.swingset.utils.SSUtils.getLogger;

/**
 * TODO: Replace EventBUs with https://dagger.dev/ and RxJava
 *			https://www.baeldung.com/rx-java
 */
public class Utils
{

	private static final System.Logger logger = getLogger();

	private Utils() { }

	/**
	 * Check if the rowSet's cursor is on a row or on the insert row.
	 * @param rs rowset for this component
	 * @return true if cursor on a row or insert row
	 * @throws SQLException 
	 */
	public static boolean hasActiveRow(RowSet rs) throws SQLException
	{
		return rs.getRow() != 0
				|| RowSetState.isInserting(rs);
	}

	/**
	 * Check if the rowSet's cursor is on a row or on the insert row.
	 * @param comp rowset for this component
	 * @return true if cursor on a row or insert row
	 * @throws SQLException 
	 */
	public static boolean hasActiveRow(RSC comp) throws SQLException
	{
		return hasActiveRow(comp.getRowSet());
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
	 * @param value new value
	 */
	public static void postRowSetModified(SSComponentInterface source, Object value)
	{
		// May want to extend to handling local EventBus per Frame/Panel;
		// Use either/both source/rs to find a local eventBus.
		getGlobalEventBus().post(new RowSetModificationEvent(source, value));
	}

	/**
	 * Post an error modification event; prefer a local eventBus.
	 * @param source SSComponent modifying the rowset
	 * @param value new value
	 */
	public static void postRowSetModifiedError(SSComponentInterface source, Object value)
	{
		getGlobalEventBus().post(new RowSetModificationEvent(source, value, true));
	}

	/**
	 * Post an undo/redo event and if value an error; prefer a local eventBus.
	 * @param source SSComponent modifying the rowset
	 * @param value new value
	 * @param isError value is an error
	 */
	public static void postRowSetUndoRedo(SSComponentInterface source, Object value,
									boolean isError)
	{
		getGlobalEventBus().post(new RowSetUndoRedoEvent(source, value, isError));
	}
	
	// Notes on implementing a weak subscriber
	//		https://github.com/google/guava/issues/807#issuecomment-61328188
	// Consider the following. much like event bus, does weak listener
	//		https://github.com/bennidi/mbassador
	// And see NavigateActions for example; includes use of Cleaner.register.

	/**
	 * EventBus to use Frame/Panel events
	 */
	private static EventBus globalEventBus = null;

	/**
	 * Get the global EventBus.
	 * Side affect on first call is creating a broadcaster for "focusOwner" changes.
	 * @return EventBus for this
	 */
	public static EventBus getGlobalEventBus()
	{
		// TODO: CentralLookup could be set up by app, or some general init.
		if (globalEventBus == null) {
			globalEventBus = CentralLookup.getDefault().lookup(EventBus.class);
			if(globalEventBus == null) {
				globalEventBus = new EventBus(new BusExceptionMonitor());
				CentralLookup.getDefault().add(globalEventBus);
			}

			// TODO: Be more careful about tracking who's managing focus
			//		 and the current focusOwner so that the events continue
			//		 if the focus manager is changed.
			KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.addPropertyChangeListener("focusOwner",
							(pce) -> globalEventBus.post(new FocusChangeEvent(pce)));
		}
		return globalEventBus;
	}
	
	private static class BusExceptionMonitor implements SubscriberExceptionHandler
	{
		@Override
		@SuppressWarnings("CallToPrintStackTrace")
		public void handleException(Throwable exception, SubscriberExceptionContext context)
		{
			exception.printStackTrace();
			logger.log(Level.ERROR, () -> "BusException", exception);
			logger.log(Level.ERROR, () -> "    " + context.getEventBus());
			logger.log(Level.ERROR, () -> "    " + context.getEvent());
			logger.log(Level.ERROR, () -> "    " + context.getSubscriber());
			logger.log(Level.ERROR, () -> "    " + context.getSubscriberMethod());
		}
	}
}
