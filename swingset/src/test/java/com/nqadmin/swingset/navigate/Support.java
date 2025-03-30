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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.sql.RowSet;

import com.nqadmin.swingset.mock.H2;

/**
 * x
 */
public class Support
{
	private Support() { }

	/**
	 * x
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static RowSet getRS1_4() throws SQLException, ClassNotFoundException
	{
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl1
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, c_tinyint tinyint, c_char varchar);
            INSERT INTO tbl1 VALUES
            	(11, 1, 'a1'), (12, 1, 'b1'), (13, 1, 'c1'), (14, 1, 'd1')
            ;
            """);
		rs.setCommand("SELECT * FROM tbl1");
		return rs;
	}

	/**
	 * x
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static RowSet getRS2_5() throws SQLException, ClassNotFoundException
	{
		RowSet rs = H2.getRowSet("""
			CREATE TABLE tbl2
			( c_pk INTEGER DEFAULT NOT NULL PRIMARY KEY, c_tinyint tinyint, c_char varchar);
            INSERT INTO tbl2 VALUES
            	(21, 1, 'a2'), (22, 1, 'b2'), (23, 1, 'c2'), (24, 1, 'd2'), (25, 1, 'e2')
            ;
            """);
		rs.setCommand("SELECT * FROM tbl2");
		return rs;
	}

	/** x
	 * @return 
	 */
	public static int timeoutVal() {
		return 0;
	}

	/**
	 * x
	 * @param latch
	 * @throws InterruptedException
	 */
	public static void await(CountDownLatch latch) throws InterruptedException
	{
		int seconds = timeoutVal();
		if (seconds == 0)
			latch.await();
		else
			latch.await(seconds, TimeUnit.SECONDS);
	}

	/**
	 * x
	 * @param tag
	 * @param r
	 * @param msg
	 * @return
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("CallToPrintStackTrace")
	public static boolean invokeLaterEventLatchWait(String tag, Runnable r, Consumer<String> msg)
			throws InterruptedException, InvocationTargetException
	{
		boolean ok[] = new boolean[] {true};
		EventQueue.invokeAndWait(() -> {msg.accept(tag + "Enter");});
		CountDownLatch latch = new CountDownLatch(1);
		RowsModelEventHandling.latch = latch;
		EventQueue.invokeLater(() -> { // typically right away since probably not in EDT
			try {
				r.run();
			} catch (Exception ex) {
				ex.printStackTrace();
				latch.countDown();
				ok[0] = false;
			}
		});
		await(latch);
		EventQueue.invokeAndWait(() -> {msg.accept(tag + "Exit");});
		return ok[0];
	}
	
}
