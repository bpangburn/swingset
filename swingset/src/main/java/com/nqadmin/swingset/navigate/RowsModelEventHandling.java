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
import java.lang.System.Logger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

import javax.sql.RowSet;

import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.navigate.RowsEvent.OperatorKind;
import com.nqadmin.swingset.navigate.RowsEvent.RowSetEventType;
import com.nqadmin.swingset.navigate.RowsModel.EnqueueRowsModelEvent;
import com.nqadmin.swingset.utils.SSUtils;

import static com.nqadmin.swingset.navigate.RowSetState.isAcceptingChanges;
import static com.nqadmin.swingset.navigate.RowsModel.getEventBus;
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
	private static final Logger logger = SSUtils.getLogger();
	private RowsModelEventHandling() { }

	record RowsEventSource(RowsModel rowsModel,
						   RowSet rowSet,
						   OperatorKind operatorKind,
						   Object operator) {
		@Override
		public String toString()
		{
			return sf("RowsEventSource{%s, %s, %s, %s}",
					objectID(rowsModel),
					objectID(rowSet),
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
	 * which brackets the "stuff" with startRowsEvent and finishRowsEvent.
	 * This makes it simple to coalesce events.
	 * <p>
	 * But all RowSet operations may not be bracketed. If a non-bracketed action
	 * (or an action on "something else") is encountered then events are broadcast
	 * individually until... eventSourceStack is empty.
	 * <p>
	 * This is constructed as a static singleton in RowsModel.java.
	 */
	static abstract class EnqueueRowsEventBase implements EnqueueRowsModelEvent
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

		@Override
		public void startRowsEvent(RowsModel model, Object compOrNav) {
			startRowsEvent(null, model, compOrNav);
		}

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

			logger.log(DEBUG, () -> dumpEventSourceStack("push", null, "####### ").toString());
		}

		@Override
		public RowsEventSource finishRowsEvent(RowsModel model)
		{
			logger.log(DEBUG, () -> dumpEventSourceStack(
					"pop", null, "####### ").toString());
			RowsEventSource finishingEventSource = popEventSource();
			if (finishingEventSource.rowsModel() != model)
				throw new IllegalStateException("Different model");
			return finishingEventSource;
		}

		StringBuilder dumpEventSourceStack(String tag, StringBuilder _sb, String tag2)
		{
			Deque<RowsEventSource> evs = eventSourceStack;
			StringBuilder sb = _sb != null ? _sb : new StringBuilder();

			// If not TRACE, then include info for TOS
			if (logger.isLoggable(TRACE))
				sb.append(sf("%s%s Source Stack (%d)\n", tag2, tag, evs.size()));
			else
				sb.append(sf("******* %s (%d) %s\n", tag, evs.size(),evs.peek()));

			// Only add the stack if TRACE.
			if (logger.isLoggable(TRACE))
				evs.forEach(ev -> sb.append("    ").append(tag2).append(ev).append('\n'));
			// Remove trailing newline.
			sb.setLength(sb.length() - 1);
			return sb;
		}

		@Override
		public void postNewRowSetEvent(RowsModel model, RowSet oldRowSet)
		{
			flushPendingEvent(getCurrentEventSource());
			post(new RowsModelNewRowSetEvent(model, oldRowSet));
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
			logger.log(DEBUG, () -> sf(
					"####### rs %s evType %s", objectID(rs), rsEventType));
			RowsEventSource eventSource = getCurrentEventSource();
			
			if (eventSource == IDLE_EVENT) {
				if (isJunitPrint())
					System.out.println("Anonymous RowSet");
				else
					logger.log(WARNING, "Anonymous RowSet event"); //, new Throwable());
			}
			
			if (eventSource.rowSet != rs) { // IDLE_EVENT or OutOfTheBlue RowSet.
				if (eventSource.rowSet != null)
					logger.log(ERROR, "WRONG ROW SET");
				if (!isJunitPrint())
					logger.log(WARNING, () -> sf("Different RowSet: orig %s, new %s",
							eventSource.rowSet, rs)); //, new Throwable());

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

			// Merge for same row set.
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
			
			// Only create an event if there were RowSetEvents;
			// except for an ACTION with forceEvent.
			// TODO: if needed, have a different event type for start/finish without event.
			// TODO: is the forceEvent() stuff needed? Always generate an event?
			if (eventTypes.isEmpty()
					&& !(eventSource.operatorKind == OperatorKind.ACTION
						&& ((RowsAction)eventSource.operator).forceEvent())) {
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

		//addToEventHistory(event);
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
			throw new IllegalStateException("starting dispatchLoop, active Q not empty");
		if (eventsNextQ.isEmpty())
			throw new IllegalStateException("starting dispatchLoop, next Q empty");
		if (dispatchLoopRunning)
			throw new IllegalStateException("starting dispatchLoop, dispatchLoopRunning");

		dispatchLoopActivated = false;
		dispatchLoopRunning = true;
		// Start working on the next queue of events.
		eventsActiveQ = eventsNextQ;
		eventsNextQ = new ArrayDeque<>(4);

		while (!eventsActiveQ.isEmpty()) {
			RowsModelEvent curEv = eventsActiveQ.poll();
			logger.log(TRACE, () -> "####### dispatch start: " + curEv);
			Utils.addToEventHistory((EventObjectBacktrace) curEv);
			getEventBus().post(curEv);
			logger.log(TRACE, () -> "####### dispatch end: " + curEv);
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
	// For unit tests.
	static CountDownLatch latch;
	@SuppressWarnings("unused")
	static boolean isBusy() {
		return dispatchLoopActivated || dispatchLoopRunning;
	}

	// Dump stuff queued up for dispatch.
	static StringBuilder dumpQueuedEvents(String tag, Deque<RowsModelEvent> evs,
			StringBuilder _sb, String tag2) {
		StringBuilder sb = _sb != null ? _sb : new StringBuilder();
		
		// If not TRACE, then include info for TOS
		if (logger.isLoggable(TRACE))
			sb.append(sf("%s%s Events (%d)\n", tag2, tag, evs.size()));
		else
			sb.append(sf("******* %s Events (%d) %s\n", tag, evs.size(), evs.peek()));

		// Only add the stack if TRACE.
		if (logger.isLoggable(TRACE))
			evs.forEach(ev -> sb.append("    ").append(tag2).append(ev).append('\n'));
		// Remove trailing newline.
		sb.setLength(sb.length() - 1);
		return sb;
	}

	static void verifyEDT() {
		if (!isDispatchThread() && !isJunit())
			logger.log(ERROR, "Should be EDT", new Throwable());
	}
}