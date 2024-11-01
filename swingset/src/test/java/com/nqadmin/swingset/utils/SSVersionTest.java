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
package com.nqadmin.swingset.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** xxx */
public class SSVersionTest {
	
	/**
	 * Test stuff.
	 */
	@Test
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void testMain() {
		System.err.println("" + SSVersion.get("1.2.3-SNAPSOT"));
		SSVersion vNull = SSVersion.get("");
		SSVersion v1 = SSVersion.get("1.2.3");
		SSVersion v2 = SSVersion.get("1.2.3-SNAPSHOT");

		assertTrue(v1.compareTo(v2) > 0);
		assertTrue(v2.compareTo(v1) < 0);
		assertTrue(v1.compareTo(v1) == 0);
		assertTrue(v2.compareTo(v2) == 0);

		assertTrue(!v1.equals(v2));
		assertTrue(!v2.equals(v1));
		assertTrue(v1.equals(v1));
		assertTrue(v2.equals(v2));

		v2 = SSVersion.get("1.2.3");
		assertTrue(v1.equals(v2));
		assertTrue(v2.equals(v1));
		v2 = SSVersion.get("1.2.4");
		assertTrue(!v1.equals(v2));
		assertTrue(!v2.equals(v1));

		assertEquals("1.2.3", SSVersion.get("1.2.3").toString());
		assertEquals("1.2.0", SSVersion.get("1.2").toString());
		assertEquals("1.0.0", SSVersion.get("1").toString());
		assertEquals(vNull, SSVersion.get("1.2.3."));
		assertEquals(vNull, SSVersion.get("1.2."));
		assertEquals(vNull, SSVersion.get("1."));
		assertEquals("123.234.345", SSVersion.get("123.234.345").toString());
		assertEquals(vNull, SSVersion.get("123.234.345.456"));
		assertEquals("123.234.345-SNAPSHOT", SSVersion.get("123.234.345-SNAPSHOT").toString());

		assertTrue(v1.compareTo(SSVersion.get("1.2.3")) == 0);
		assertTrue(v1.compareTo(SSVersion.get("1.10.1")) < 0);
		assertTrue(v1.compareTo(SSVersion.get("1.1.3")) > 0);
		assertTrue(v1.compareTo(SSVersion.get("2.2.3")) < 0);
		assertTrue(v1.compareTo(SSVersion.get("1.2.4")) < 0);
		assertTrue(v1.compareTo(SSVersion.get("1.2.2")) > 0);
	}

}