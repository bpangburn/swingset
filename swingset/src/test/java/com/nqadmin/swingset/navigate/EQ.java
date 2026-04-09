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
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.google.common.base.Throwables;
import com.raelity.lib.eventbus.WeakEventBus;
import com.raelity.lib.eventbus.WeakSubscribe;

import static com.nqadmin.swingset.navigate.Utils.getGlobalEventBus;

/**
 * x
 */
public class EQ
{
	private EQ() { }

	/** x */
	public interface DBRunnable
	{
		/** *  @throws SQLException
		 * @throws java.lang.ClassNotFoundException */
		public void run() throws SQLException, ClassNotFoundException ;
	}

	/** x
	 * @return 
	 */
	static int timeoutVal() {
		return 3;
	}

	/**
	 * x
	 * @param latch
	 * @throws InterruptedException
	 */
	static void await(CountDownLatch latch) throws InterruptedException
	{
		int seconds = timeoutVal();
		if (seconds == 0)
			latch.await();
		else
			latch.await(seconds, TimeUnit.SECONDS);
	}

	static BusReceiver setupBusReceiver()
	{
		//List<RowsEvent> events = new ArrayList<>();
		BusReceiver br = new BusReceiver();
		WeakEventBus.register(br, getGlobalEventBus());
		return br;
	}

	static class GetRowsModelEvent {
		private final List<EventObjectBacktrace> basicEvents;

		GetRowsModelEvent(List<EventObjectBacktrace> basicEvents)
		{
			this.basicEvents = basicEvents;
		}

		int size()
		{
			return basicEvents.size();
		}

		void clear()
		{
			basicEvents.clear();
		}

		RowsEvent get(int idx)
		{
			return (RowsEvent) basicEvents.get(idx);
		}

		@SuppressWarnings("unused")
		EventObjectBacktrace oget(int idx)
		{
			return basicEvents.get(idx);
		}

		@SuppressWarnings("unused")
		RowsModelNewRowSetEvent newrsget(int idx)
		{
			return (RowsModelNewRowSetEvent) basicEvents.get(idx);
		}

		@Override
		public String toString()
		{
			return "GetRowsModelEvent{" + "size=" + basicEvents.size() + '}';
		}


	}

	static class BusReceiver {
		private final ConsumerEx<EventObjectBacktrace> PerTestDispatch;
		private final List<EventObjectBacktrace> events = new ArrayList<>();

		BusReceiver()
		{
			PerTestDispatch = (ev) -> {
				events.add(ev);
			};
		}

		GetRowsModelEvent events() {
			return new GetRowsModelEvent(events);
		}

		/**
		 * Catch RowSet events; update the component's display.
		 * Ignore events that came from this component; they are handled internally.
		 * Only events from "our" RowSet are handled.
		 * @param ev 
		 */
		@WeakSubscribe
		public void handleRowSetEvent(RowsEvent ev)
		{
			report(ev);
		}

		@WeakSubscribe
		public void handleNewRowSetEvent(RowsModelNewRowSetEvent ev)
		{
			report(ev);
		}

		private void report(EventObjectBacktrace ev) {
			System.out.println("EventBus: " + ev.toString());
			if (PerTestDispatch != null)
				try {
					PerTestDispatch.accept(ev);
				} catch (Exception ex) {
					System.err.println("EXCEPTION: " + ex.getLocalizedMessage());
					Throwables.throwIfUnchecked(ex);
					throw new RuntimeException(ex);
				}
		}
	}

	/** Like Consumer, but may throw SQLException
	 * @param <T>
	 */
	public interface ConsumerEx<T>
	{
		/**
		 * @param t
		 * @throws SQLException
		 */
		public void accept(T t) throws Exception ;
	}

	private static CountDownLatch initLatch()
	{
		CountDownLatch latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		return latch;
	}

	/**
	 * Similar to EventQueue.invokeAndWait() but use invokeLater and a latch.
	 * "exs" filled with exceptions for checking.
	 * 
	 * @param tag
	 * @param msg
	 * @param exs
	 * @param r
	 * @return true when no problems encountered.
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static boolean invokeLatchWait(String tag, Consumer<String> msg,
			List<Exception> exs, DBRunnable r)
			throws InterruptedException, InvocationTargetException
	{
		return invokeLatchWait(tag, msg, exs, initLatch(), r);
	}

	/**
	 * Similar to EventQueue.invokeAndWait() but use invokeLater and a latch.
	 * An exception is output and causes an error return;
	 *
	 * @param tag
	 * @param msg
	 * @param r
	 * @return true when no problems encountered.
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static boolean invokeLatchWait(String tag, Consumer<String> msg, DBRunnable r)
			throws InterruptedException, InvocationTargetException
	{
		return invokeLatchWait(tag, msg, null, initLatch(), r);
	}

	/**
	 *
	 * @param tag
	 * @param msg
	 * @param exs
	 * @param r
	 * @return
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static boolean invokeWait(String tag, Consumer<String> msg,
			List<Exception> exs, DBRunnable r)
			throws InterruptedException, InvocationTargetException
	{
		return invokeLatchWait(tag, msg, exs, null, r);
	}

	/**
	 * This version allows the latch to be specified, typically not used.
	 * @param tag
	 * @param r
	 * @param exs collects exceptions; if null an exception causes an error return
	 * @param _latch when null internally countDown when runnable complete
	 * @param msg
	 * @return true when no problems encountered.
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	// TODO: to be totally careful, should track RowsModelEventHandling insure never busy.
	@SuppressWarnings({"CallToPrintStackTrace", "UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch"})
	public static boolean invokeLatchWait(String tag, Consumer<String> msg,
			List<Exception> exs, CountDownLatch _latch, DBRunnable r)
			throws InterruptedException, InvocationTargetException
	{
		CountDownLatch latch = _latch != null ? _latch : new CountDownLatch(1);
		boolean ok[] = new boolean[] {true};
		outMsg(msg, tag + "Enter");
		EventQueue.invokeLater(() -> { // typically right away since probably not in EDT
			try {
				r.run();
				if (_latch == null) // internal countdown?
					latch.countDown();
			} catch (Exception ex) {
				if (exs != null)
					exs.add(ex);
				else {
					ex.printStackTrace();
					ok[0] = false;
				}
				latch.countDown();
			}
		});
		await(latch);
		if (latch.getCount() > 0) {
			outMsg(msg, tag + ": LATCH FULL, NO EVENT");
			ok[0] = false;
		}
		outMsg(msg, tag + "Exit");
		return ok[0];
	}

	private static void outMsg(Consumer<String> cmsg, String msg)
			throws InterruptedException, InvocationTargetException
	{
		EventQueue.invokeAndWait(() -> {cmsg.accept(msg);});
	}
	
}
