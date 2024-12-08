/* *****************************************************************************
 * Copyright (C) 2024, Ernie R Rael. All rights reserved.
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
package com.nqadmin.swingset.mock;

import java.sql.SQLException;

import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;

import org.mockito.Mockito;

/**
 * Some well known mocks for SS.
 */
public class Mock
{
	private Mock() { }

	/**
	 *	name		age
	 *	VARCHAR		INTEGER
	 * { "John"		30 }
	 * @return rowset
	 * @throws java.sql.SQLException
	 */
	public static RowSet getRowSet() throws SQLException {
        JdbcRowSet mockRowSet = Mockito.mock(JdbcRowSet.class);
        Mockito.when(mockRowSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(mockRowSet.getString("name")).thenReturn("John");
        Mockito.when(mockRowSet.getInt("age")).thenReturn(30);
		return mockRowSet;
	}
}
/* https://www.baeldung.com/spring-jdbctemplate-testing
   Shows using H2 or Mock
*/
/*	google search AI
import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

public class JdbcRowSetTest {

    @Test
    public void testRowSet() throws SQLException {
        // Create a mock JdbcRowSet
        JdbcRowSet mockRowSet = Mockito.mock(JdbcRowSet.class);

        // Set up expectations for the mock
        Mockito.when(mockRowSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(mockRowSet.getString("name")).thenReturn("John");
        Mockito.when(mockRowSet.getInt("age")).thenReturn(30);

        // Use the mock in your test
        while (mockRowSet.next()) {
            String name = mockRowSet.getString("name");
            int age = mockRowSet.getInt("age");

            // Assert the values
            assertEquals("John", name);
            assertEquals(30, age);
        }
    }
}
 */