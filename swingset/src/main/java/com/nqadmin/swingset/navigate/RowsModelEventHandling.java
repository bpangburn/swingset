/* *****************************************************************************
 * Copyright (C) 2025, Ernie R Rael. All rights reserved.
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
 * ****************************************************************************/
package com.nqadmin.swingset.navigate;

import java.awt.EventQueue;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

import javax.sql.RowSet;

import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.navigate.RowsEvent.OperatorKind;
import com.nqadmin.swingset.navigate.RowsEvent.RowSetEventType;
import com.nqadmin.swingset.navigate.RowsModel.EnqueueRowsEvent;

import static com.nqadmin.swingset.navigate.RowSetState.isAcceptingChanges;
import static com.nqadmin.swingset.navigate.RowsModel.getEventBus;
import static com.nqadmin.swingset.navigate.RowsModel.logger;
import static com.nqadmin.swingset.navigate.RowsModel.post;
import static com.nqadmin.swingset.utils.SSUtils.isJunit;
import static com.nqadmin.swingset.utils.SSUtils.isJunitPrint;
import static com.nqadmin.swingset.utils.SSUtils.objectID;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.awt.EventQueue.isDispatchThread;
import static java.lang.System.Logger.Level.*;

/**
 * Various ways to handle RowsModel events...
 * These event types are generated in here
 * <ul>
 * <li>RowsEvent derived from RowSetEvent
 * </ul>
 * Multiple RowSetEvent maybe coalesced into a single RowsEvent.
 */
public class RowsModelEventHandling
{
	private RowsModelEventHandling() { }

	record RowsEventSource(RowsModel rowsModel,
						   RowSet rs,
						   OperatorKind operatorKind,
						   Object operator) {
		@Override
		public String toString()
		{
			return sf("RowsEventSource{%s, %s, %s, %s}",
					objectID(rowsModel),
					objectID(rs),
					operatorKind,
					operator instanceof RowsAction
							? operator : objectID(operator));
		}
	}
	// TODO: use UNKNOWN ???
	// static RowsEventSource IDLE_EVENT = new RowsEventSource(null, null,
	// 														OperatorKind.UNKNOWN, null);
	static RowsEventSource IDLE_EVENT = new RowsEventSource(null, null, null, null);

	/**
	 * RowSet events are coalesced into a RowsEvent;
	 * the source is represented as a RowsEventSource.
	 * The preferred way to operate on a RowSet is like "rsOp(() -> {doStuff})"
	 * (or a similar construct)
	 * which brackets the "stuff" with startRowsEvent and firstRowsEvent.
	 * This makes it simple to coalesce events.
	 * <p>
	 * But all RowSet operations may not be bracketed. If a non-bracketed action
	 * (or an action on "something else") is encountered then events are broadcast
	 * individually until... eventSourceStack is empty.
	 * <p>
	 * This is constructed as a static singleton in RowsModel.java.
	 */
	static abstract class EnqueueRowsEventBase implements EnqueueRowsEvent
	{
		//
		// TODO: fix this comment, operations can be nested and/or anonymous.
		// NOTE: Only one "operation" can be in progress at a time.
		//       I think this is consistent with how SS works.
		//
		
		/** eventSource is the TOS, accumulating RowSet events from here. */
		//protected RowsEventSource eventSource = IDLE_EVENT;
		/** Keep a stack in the off chance of nested EventSource bracketing. */
		final Deque<RowsEventSource> eventSourceStack;

		EnqueueRowsEventBase()
		{
			this.eventSourceStack = new ArrayDeque<>(3);
			pushEventSource(IDLE_EVENT);
		}

		final RowsEventSource getCurrentEventSource()
		{
			RowsEventSource tos = eventSourceStack.peek();
			if (tos == null)
				throw new IllegalStateException("stack empty");
			return tos;
		}

		final void pushEventSource(RowsEventSource eventSource)
		{
			eventSourceStack.push(eventSource);
		}

		final RowsEventSource popEventSource()
		{
			RowsEventSource tosEventSource = eventSourceStack.pop();
			if (tosEventSource == null)
				throw new IllegalStateException("stack empty");
			return tosEventSource;
		}

		//////////////////////////////////////////////////////////////////////
		//
		// CreateEvents
		//

		// startRowsEvent - no current model - typical
		// startRowsEvent - match current model
		// startRowsEvent - different current model
		// startRowsEvent - further mix - different model, same row set

		// Anonymous RowSet Event - no current model for eventsNextQ
		// Anonymous RowSet Event - event with different RowSet

		// Additional strategies
		// Merge eventsNextQ for same RowSet.

		/**
		 * Invoke this when starting an Operation that manipulates a RowSet.
		 * Initialize info for the event in progress, becomes top of stack.
		 * @param _operatorKind
		 * @param model
		 * @param compOrNav
		 */
		// TODO: synchronized? Possible that two thread modify at same time?
		@Override
		public void startRowsEvent(OperatorKind _operatorKind, RowsModel model,
				Object compOrNav)
		{
			verifyEDT();
			Objects.requireNonNull(model.getRowSet());
			if (getCurrentEventSource() != IDLE_EVENT) {
				flushPendingEvent(getCurrentEventSource());
			}
			
			OperatorKind operatorKind;
			if (_operatorKind != null)
				operatorKind = _operatorKind;
			else {
				operatorKind = switch (compOrNav) {
				case @SuppressWarnings("unused") RSC x        -> OperatorKind.COMPONENT;
				case @SuppressWarnings("unused") RowsAction x -> OperatorKind.ACTION;
				case null                                     -> OperatorKind.UNKNOWN;
				default -> throw new IllegalArgumentException("Must be RSC or RowsAction");
				};
			}
			RowsEventSource eventSource = new RowsEventSource(
					model, model.getRowSet(), operatorKind, compOrNav);
			pushEventSource(eventSource);

			logger.log(DEBUG, () -> dumpEventSourceStack("startRowsEvent-push", null, "####### ").toString());
		}

		@Override
		public RowsEventSource finishRowsEvent(RowsModel model)
		{
			logger.log(DEBUG, () -> dumpEventSourceStack(
					"finishRowsEvent-pop", null, "####### ").toString());
			RowsEventSource finishingEventSource = popEventSource();
			if (finishingEventSource.rowsModel() != model)
				throw new IllegalStateException("Different model");
			return finishingEventSource;
		}

		StringBuilder dumpEventSourceStack(String tag, StringBuilder _sb, String tag2)
		{
			Deque<RowsEventSource> evs = eventSourceStack;
			StringBuilder sb = _sb != null ? _sb : new StringBuilder();
			sb.append(sf("******* %s%s Event Sources (%d) *******\n", tag2, tag, evs.size()));
			evs.forEach(ev -> sb.append("    ").append(tag2).append(ev).append('\n'));
			// Remove trailing newline.
			sb.setLength(sb.length() - 1);
			return sb;
		}

		protected abstract void flushPendingEvent(RowsEventSource eventSource);
	}

	/** If event comes in and no current model, start flushing. */
	@SuppressWarnings("unused")
	static class SimpleEvents extends EnqueueRowsEventBase
	{
		//protected final List<RowSetEventType> eventTypes = new ArrayList<>(6);
		protected final Set<RowSetEventType> eventTypes
				= EnumSet.noneOf(RowSetEventType.class);

		@Override
		public void addRowSetEvents(Set<RowSetEventType> rsEventTypes, RowSet rs)
		{
			verifyEDT();
			throw new UnsupportedOperationException("Not supported.");
		}
		
		/**
		 * NOTE: Events that occur while processing CachedRowSet changes back to
		 * database are discarded.
		 *
		 * @param rsEventType
		 * @param rs
		 */
		@Override
		public void addRowSetEvent(RowSetEventType rsEventType, RowSet rs) {
			verifyEDT();
			Objects.requireNonNull(rs);
			if (isAcceptingChanges(rs)) // only possible if CachedRowSet
				return;
			logger.log(TRACE, () -> sf(
					"####### rs %s evType %s", objectID(rs), rsEventType));
			RowsEventSource eventSource = getCurrentEventSource();
			
			if (eventSource == IDLE_EVENT) {
				if (isJunitPrint())
					System.out.println("Anonymous RowSet");
				else
					logger.log(WARNING, "Anonymous RowSet event"); //, new Throwable());
			}
			
			if (eventSource.rs != rs) { // IDLE_EVENT or OutOfTheBlue RowSet.
				if (eventSource.rs != null)
					logger.log(ERROR, "WRONG ROW SET");
				if (!isJunitPrint())
					logger.log(WARNING, () -> sf("Different RowSet: orig %s, new %s",
							eventSource.rs, rs)); //, new Throwable());

				flushPendingEvent(eventSource);

				post(new RowsEvent(new RowsEventSource(
						null, rs, OperatorKind.ANON, null),
						rsEventType));
				// Note that the eventSourceStack has not changed.
				return;
			}
			
			if (!eventTypes.isEmpty()) {
				if (isJunitPrint())
					System.out.println("merge: " + eventTypes);
				else
					logger.log(TRACE, () -> sf("merge: %s", eventTypes));
			}
			eventTypes.add(rsEventType);
		}
		
		/**
		 * Invoke this when finishing an Operation that manipulates a RowSet.
		 * All the RowSet events that occurred during the operation are
		 * coalesced into a single event.
		 * @param model must match the model associated with startRowsEvent
		 */
		@Override
		public RowsEventSource finishRowsEvent(RowsModel model) {
			verifyEDT();
			RowsEventSource eventSource = super.finishRowsEvent(model);

			flushPendingEvent(eventSource);

			return eventSource;
		}

		@Override
		protected void flushPendingEvent(RowsEventSource eventSource)
		{
			// TODO: add the RowSet, consider case where model == null ... UNKNOWN
			RowsEvent ev;
			
			// Only create an event if there were RowSetEvents
			// TODO: if needed, have a different event type for start/finish without event.
			if (eventTypes.isEmpty()) {
				ev = null;
				Supplier<String> msg = () -> sf("RowsEvent %s: No RowSet event",
						eventSource.operatorKind);
				//if(isJunit())System.out.println(msg.get());
				logger.log(TRACE, msg);
			} else {
				ev = new RowsEvent(eventSource, eventTypes);
				//if(isJunit())System.out.println(ev.toString());
				logger.log(TRACE, () -> sf(""+ev));
			}
			
			if (eventSource.operatorKind == OperatorKind.COMPONENT && ev != null) {
				System.err.printf("\n\n***** COMPONENT EVENTS %s *****\n\n", ev);
				new Exception().printStackTrace(System.out);
			}
			
			eventTypes.clear();
			
			if (ev != null)
				post(ev);
		}
	}

	/**
	 * Queue event for dispatch; merge with previous event if possible.
	 * There are two event queues
	 * <ul>
	 * <li> "eventsNextQ" accumulates events for the next round of "dispatchLoop".
	 * <li> "eventsActive" are currently being dispatched.
	 * </ul>
	 * When "dispatchLoop" starts, "eventsActive" is empty. It grabs eventsNextQ
	 * and assigns it to "eventsActive" and creates a new/empty "eventsNextQ".
	 * <p>
	 * Everything is EDT, so no locking consideration.
	 * @param event 
	 */
	static void postAsync(RowsModelEvent event) {
		verifyEDT();
		addToAllEvents(event);

		// TODO: OPTIM: but is it possible?
		//       When in EDT
		//       If activated && !running then how can NextQ be empty?
		// RowsModelEvent tail;

		// // The dispatchLoop isn't running and there's nothing in the NextQ
		// // then try to absorb into ActiveQ
		// if (dispatchLoopActivated && !dispatchLoopRunning && eventsNextQ.isEmpty())
		// 	tail = eventsActiveQ.peekLast();
		// else
		// 	tail = eventsNextQ.peekLast();

		RowsModelEvent tail = eventsNextQ.peekLast();
		if (tail instanceof RowsEvent tailEv && event instanceof RowsEvent newEv
				&& tailEv.absorb(newEv)) {
			logger.log(TRACE, () -> "####### postAsync: absorbed " + event);
			return; 
		}
		eventsNextQ.add(event);

		logger.log(TRACE, () -> "####### postAsync: " + event);

		if (!dispatchLoopActivated && !dispatchLoopRunning)
			startDispatcher("postAsync startDispatcher:", "####### ");
	}

	private static Deque<RowsModelEvent> eventsNextQ = new ArrayDeque<>(4);
	private static Deque<RowsModelEvent> eventsActiveQ = new ArrayDeque<>(0);

	private static boolean dispatchLoopActivated;
	private static boolean dispatchLoopRunning;

	private static void startDispatcher(String tag, String tag2) {
		logger.log(DEBUG, dumpQueuedEvents(tag, eventsNextQ, null, tag2).toString());
		if (dispatchLoopActivated)
			throw new IllegalStateException("startDispatcher, dispatchLoopActivated");
		dispatchLoopActivated = true;
		EventQueue.invokeLater(() -> dispatchLoop());
	}

	/** Dispatch events invokeLater. */
	private static void dispatchLoop() {
		if (!eventsActiveQ.isEmpty())
			throw new IllegalStateException("starting dispatchLoop, Q not empty");
		if (eventsNextQ.isEmpty())
			throw new IllegalStateException("starting dispatchLoop, Q empty");
		if (dispatchLoopRunning)
			throw new IllegalStateException("starting dispatchLoop, dispatchLoopRunning");

		dispatchLoopActivated = false;
		dispatchLoopRunning = true;
		// Start working on the next queue of events.
		eventsActiveQ = eventsNextQ;
		eventsNextQ = new ArrayDeque<>(4);

		RowsModelEvent curEv;
		while ((curEv = eventsActiveQ.poll()) != null) {
			logger.log(TRACE, "####### dispatch start: " + curEv);
			getEventBus().post(curEv);
			logger.log(TRACE, "####### dispatch end: " + curEv);
		}

		dispatchLoopRunning = false;
		// If more events have come in...
		if (!eventsNextQ.isEmpty())
			startDispatcher("dispatchLoop startDispatcher:", "####### ");
		else
			logger.log(TRACE, "####### dispatchLoop exit: ");

		// Unit tests may set latch.
		if (latch != null) {
			// could use: if (!dispatchLoopActivated && !dispatchLoopRunning)
			// could use: if (eventsNextQ.isEmpty() && eventsActiveQ.isEmpty())
			if (eventsNextQ.isEmpty())
				latch.countDown();
		}
	}
	static CountDownLatch latch;

	// static void dumpQueuedEvents(String tag, List<RowsModelEvent> evs) {
	// 	System.err.printf("******* %s Events (%d) *******\n", tag, evs.size());
	// 	evs.forEach((ev) -> System.err.println("    " + ev));
	// }

	static StringBuilder dumpQueuedEvents(String tag, Deque<RowsModelEvent> evs,
			StringBuilder _sb, String tag2) {
		StringBuilder sb = _sb != null ? _sb : new StringBuilder();
		sb.append(sf("******* %s%s Events (%d) *******\n", tag2, tag, evs.size()));
		evs.forEach(ev -> sb.append("    ").append(tag2).append(ev).append('\n'));
		// Remove trailing newline.
		sb.setLength(sb.length() - 1);
		return sb;
	}

	//////////////////////////////////////////////////////////////////////

	private static final int N_EVENTS = 50;
	private static final Queue<RowsModelEvent> latestEvents = new ArrayDeque<>();
	private static void addToAllEvents(RowsModelEvent event) {
		while (latestEvents.size() >= N_EVENTS)
			latestEvents.remove();
		latestEvents.add(event);
	}

	/**
	 * 
	 * @param tag
	 */
	public static void dumpAllEvents(String tag) {
		System.err.printf("******* %s All Events (%d) *******\n", tag, latestEvents.size());
		latestEvents.forEach((ev) -> System.err.println("    " + ev));
	}

	static void verifyEDT() {
		if (!isDispatchThread() && !isJunit())
			logger.log(ERROR, "Should be EDT", new Throwable());
	}
}

	// /**
	//  * A single RowSet op may invoke listener for mutliple things.
	//  */
	// // PROBLEM: With the invoke later have to consider that the "finish" needs
	// //          to be syncronized/come-after.
	// @SuppressWarnings("unused")
	// private class AsyncRowSetListener extends RowSetListenerBase
	// {
	// 	/** Variables needed to consolidate multiple calls. */
	// 	private int lastChange = 0;
	// 	private int lastNotifiedChange = 0;

	// 	@Override
	// 	public void rowSetChanged(RowSetEvent event)
	// 	{
	// 		addEvent(RowSetEventType.ROW_SET_CHANGED, (RowSet) event.getSource());
	// 	}

	// 	@Override
	// 	public void rowChanged(RowSetEvent event)
	// 	{
	// 		addEvent(RowSetEventType.ROW_CHANGED, (RowSet) event.getSource());
	// 	}

	// 	@Override
	// 	public void cursorMoved(RowSetEvent event)
	// 	{
	// 		addEvent(RowSetEventType.CURSOR_MOVED, (RowSet) event.getSource());
	// 	}

	// 	private final Set<RowSetEventType> whichEv = EnumSet.noneOf(RowSetEventType.class);
	// 	private void addEvent(RowSetEventType rsEventType, RowSet rs)
	// 	{
	// 		lastChange++;
	// 		whichEv.add(rsEventType);

	// 		// Delay logic until all listener methods are called for current event.
	// 		// May be further coalescing.
	// 		// https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
	// 		SwingUtilities.invokeLater(() -> {
	// 			if (lastNotifiedChange != lastChange) {
	// 				lastNotifiedChange = lastChange;

	// 				addRowSetEvents(whichEv, rs);
	// 				whichEv.clear();
	// 			}
	// 		});
	// 	}
	// };

	// private static List<RowsModelEvent> eventsImmediate = new ArrayList<>();
	// private static boolean eventsBusy;
	// static void postImmediate(RowsModelEvent event) {
	// 	verifyEDT();
	// 	addToAllEvents(event);
	// 	eventsImmediate.add(event);
	// 	dumpQueuedEvents("ENQUEUE", eventsImmediate);
	// 	if (eventsImmediate.size() > 1) {
	// 		if (!eventsBusy)
	// 			throw new IllegalStateException("should be busy");
	// 		System.err.println("post, busy, returning");
	// 		return;
	// 	}
	// 	eventsBusy = true;
	// 	try {
	// 		while (!eventsImmediate.isEmpty()) {
	// 			getEventBus().post(eventsImmediate.get(0));
	// 			dumpQueuedEvents("DEQUEUE", eventsImmediate);
	// 			eventsImmediate.remove(0);
	// 		}
	// 	} finally {
	// 		eventsBusy = false;
	// 	}
	// }

	// /** If event comes in and no current model, start flushing. */
	// @SuppressWarnings("unused")
	// static class FlushNotCurrent extends EnqueueRowsEventBase
	// {
	// 	/** If there's confusion, eg unexpected events,
	// 	 * events go out singularly until re-sync. */
	// 	protected boolean flushIndividualEvents;
	// 	protected final List<RowSetEventType> eventTypes = new ArrayList<>(6);

	// 	@Override
	// 	public void addRowSetEvents(Set<RowSetEventType> rsEventTypes, RowSet rs)
	// 	{
	// 		verifyEDT();
	// 		throw new UnsupportedOperationException("Not supported.");
	// 	}
	// 	
	// 	/**
	// 	 * NOTE: Events that occur while processing CachedRowSet changes back to
	// 	 * database are discarded.
	// 	 *
	// 	 * @param rsEventType
	// 	 * @param rs
	// 	 */
	// 	@Override
	// 	public void addRowSetEvent(RowSetEventType rsEventType, RowSet rs) {
	// 		verifyEDT();
	// 		if (isAcceptingChanges(rs)) // only possible if CachedRowSet
	// 			return;
	// 		logger.log(TRACE, () -> sf(
	// 				"####### rs %s evType %s", objectID(rs), rsEventType));
	// 		
	// 		Objects.requireNonNull(eventSource.operatorKind);
	// 		if (eventSource.operatorKind == OperatorKind.UNKNOWN)
	// 			logger.log(WARNING, "Anonymous RowSet event", new Throwable());
	// 		
	// 		if (eventSource.rs != rs) {
	// 			if (eventSource.rs != null)
	// 				logger.log(ERROR, "WRONG ROW SET");
	// 			logger.log(WARNING, () -> sf("Different RowSet orig %s, new %s",
	// 					eventSource.rs, rs), new Throwable());
	// 			if (eventSource.rs == null) {
	// 				//eventSource.rs = rs;
	// 				eventSource = new RowsEventSource(eventSource.rowsModel, rs,
	// 						eventSource.operatorKind, eventSource.operator);
	// 			}
	// 			flushIndividualEvents = true;
	// 		}
	// 		
	// 		boolean hasDup = false;
	// 		if (eventSource.operatorKind != OperatorKind.UNKNOWN) {
	// 			//
	// 			// TODO: ??? if wrong rowset, flush current and clear operatorKind
	// 			//
	// 			if (eventTypes.contains(rsEventType)) {
	// 				//System.err.println("MULTIPLE EVENTS OF SAME TYPE");
	// 				hasDup = true;
	// 			}
	// 		}
	// 		
	// 		// TODO: SYNCHRO?
	// 		eventTypes.add(rsEventType);
	// 		
	// 		// Send the event is not associated with a known operation or RowSet.
	// 		if (flushIndividualEvents || eventSource.operatorKind == OperatorKind.UNKNOWN)
	// 			finishRowsEventInternal(null, rs);
	// 		
	// 		if (hasDup) {
	// 			// if (isJunit()) System.err.println("Multiple: " + eventTypes);
	// 			logger.log(DEBUG, () -> "Multiple: " + eventTypes);
	// 		}
	// 	}
	// 	
	// 	/**
	// 	 * Invoke this when finishing an Operation that manipulates a RowSet.
	// 	 * All the RowSet events that occurred during the operation are
	// 	 * coalesced into a single event.
	// 	 * @param model must match the model associated with startRowsEvent
	// 	 */
	// 	@Override
	// 	public RowsEventSource finishRowsEvent(RowsModel model) {
	// 		verifyEDT();
	// 		finishRowsEventInternal(model, null);
	// 		flushIndividualEvents = false;
	// 		// TODO: delete class or fixup return.
	// 		return null;
	// 	}
	// 	
	// 	@SuppressWarnings("CallToPrintStackTrace")
	// 	private void finishRowsEventInternal(RowsModel _model, RowSet _rs)
	// 	{
	// 		super.finishRowsEvent(_model);

	// 		//
	// 		// TODO: when there's a "flush...", a startNav will set sourceModel
	// 		//       and a subsequent addRowSetEvent does finish which clears
	// 		//       sourceModel. So the following can easily trigger.
	// 		//
	// 		
	// 		//if (_model != null && sourceModel != null && _model != sourceModel)
	// 		//if (_model != null && _model != sourceModel)
	// 		
	// 		if (!flushIndividualEvents && _model != null && _model != eventSource.rowsModel)
	// 			throw new IllegalStateException("Different model");
	// 		
	// 		RowSet rs = _rs != null ? _rs : eventSource.rs();
	// 		@SuppressWarnings("unused")
	// 		RowsModel model = _model != null ? _model : getDummy(rs);
	// 		
	// 		// TODO: add the RowSet, consider case where model == null ... UNKNOWN
	// 		RowsEvent ev;
	// 		
	// 		// Only create an event if there were RowSetEvents
	// 		// TODO: if needed, have a different event type for start/finish without event.
	// 		if (eventTypes.isEmpty()) {
	// 			ev = null;
	// 			Supplier<String> msg = () -> sf("RowsEvent %s: No RowSet event", eventSource.operatorKind);
	// 			if(isJunit())System.out.println(msg.get());
	// 			logger.log(TRACE, msg);
	// 		} else {
	// 			//ev = new RowsEvent(model, rs,
	// 			//		eventSource.operatorKind, eventSource.operator,
	// 			ev = new RowsEvent(eventSource,
	// 					eventTypes.isEmpty()
	// 							? EnumSet.noneOf(RowSetEventType.class)
	// 							: EnumSet.copyOf(eventTypes));
	// 			if(isJunit())System.out.println(ev.toString());
	// 			logger.log(TRACE, () -> sf(""+ev));
	// 		}
	// 		
	// 		if (eventSource.operatorKind == OperatorKind.COMPONENT && ev != null) {
	// 			System.err.printf("\n\n***** COMPONENT EVENTS %s *****\n\n", ev);
	// 			new Exception().printStackTrace(System.out);
	// 		}
	// 		
	// 		eventSource = IDLE_EVENT;
	// 		// sourceModel = null;
	// 		// sourceRowSet = null;
	// 		// operatorKind = OperatorKind.UNKNOWN;
	// 		// originatingObject = null;
	// 		eventTypes.clear();
	// 		
	// 		if (ev != null)
	// 			post(ev);
	// 	}
	// }

	// /**
	//  * The addRowSetEvents is done via invokeLater. This means that code like
	//  * {@code dbChange(() -> rs.Xxx()} completes without any event handling.
	//  */
	// @SuppressWarnings("unused")
	// static class AsyncEvents extends EnqueueRowsEventBase
	// {
	// 	/** If there's confusion, eg unexpected events,
	// 	 * events go out singularly until re-sync. */
	// 	protected boolean flushIndividualEvents;
	// 	private Set<RowSetEventType> eventTypes;
	// 	@Override
	// 	public void addRowSetEvent(RowSetEventType rsEventType, RowSet rs)
	// 	{
	// 		verifyEDT();
	// 		throw new UnsupportedOperationException("Not supported.");
	// 	}

	// 	/**
	// 	 * Can be called in these states.
	// 	 * <pre>
	// 	 * - no event in progress
	// 	 * - bracketed event in progress
	// 	 *   - different row set
	// 	 *   - matching row set, normal case
	// 	 * </pre>
	// 	 * 
	// 	 * @param rsEventTypes
	// 	 * @param rs 
	// 	 */
	// 	@Override
	// 	public void addRowSetEvents(Set<RowSetEventType> rsEventTypes, RowSet rs)
	// 	{
	// 		verifyEDT();
	// 		if (isAcceptingChanges(rs)) // only possible if CachedRowSet
	// 			return;
	// 		
	// 		// ???
	// 		Objects.requireNonNull(eventSource.operatorKind);

	// 		if (eventSource == IDLE_EVENT) {
	// 			// No event in progress.
	// 			logger.log(WARNING, "Anonymous RowSet event", new Throwable());
	// 			post(new RowsEvent(
	// 					new RowsEventSource(getDummy(rs), rs, OperatorKind.UNKNOWN, null),
	// 					rsEventTypes));
	// 			return; // Still no event in progress.
	// 		}

	// 		// Bracketed event in progress.
	// 		if (eventSource.rs != rs) {
	// 			// Different RowSet. Should generally be impossible.
	// 			// It could only happen if bracketed ops manipulate multiple RowSets;
	// 			// or if there is an actor outside of SS.
	// 			if (eventSource.rs != null)
	// 				logger.log(ERROR, "WRONG ROW SET");
	// 			logger.log(WARNING, () -> sf(
	// 					"Different RowSet orig %s, new %s", eventSource.rs, rs));
	// 			postInProgress();
	// 			flushIndividualEvents = true;
	// 		}
/*
	// 		if (eventSource.rs != rs) {
	// 			// This should be impossible since there is no nested event handling.
	// 			if (eventSource.rs != null)
	// 				logger.log(ERROR, "WRONG ROW SET");
	// 			logger.log(WARNING, () -> sf("Different RowSet orig %s, new %s", sourceRowSet, rs));
	// 			if (sourceRowSet == null)
	// 				sourceRowSet = rs;
	// 			//flushIndividualEvents = true;
	// 			// TODO: 
	// 		}
*/
	// 	}

	// 	@Override
	// 	public RowsEventSource finishRowsEvent(RowsModel model)
	// 	{
	// 		return super.finishRowsEvent(model);
	// 	}

	// 	private void postInProgress()
	// 	{
	// 		post(new RowsEvent(eventSource, eventTypes));
	// 		eventSource = IDLE_EVENT;
	// 	}
	// }
