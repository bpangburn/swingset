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
 ******************************************************************************/

package com.nqadmin.swingset;

/**
 * SSDataValue.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * The SSDataValue interface specifies methods for SSTableModel to retrieve the
 * value for primary column in JTable.
 */
public interface SSDataValue {

	/**
	 * Returns the value for the primary column in the JTable (SSRowSet used for
	 * building the JTable). When addition of a row is taking place in the JTable,
	 * SSTableModel tries to insert a primary key value in to that row returned by
	 * this function.
	 *
	 * @return the value for the primary key column.
	 */
	public Object getPrimaryColumnValue();

} // end public interface SSDataValue {

/*
 * $Log$ Revision 1.7 2005/02/04 22:48:54 yoda2 API cleanup & updated Copyright
 * info.
 *
 * Revision 1.6 2004/11/11 14:45:48 yoda2 Using TextPad, converted all tabs to
 * "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.5 2004/10/25 22:03:17 yoda2 Updated JavaDoc for new datasource
 * abstraction layer in 0.9.0 release.
 *
 * Revision 1.4 2004/08/10 22:06:59 yoda2 Added/edited JavaDoc, made code layout
 * more uniform across classes, made various small coding improvements suggested
 * by PMD.
 *
 * Revision 1.3 2004/03/08 16:43:37 prasanth Updated copy right year.
 *
 * Revision 1.2 2003/12/16 18:01:40 prasanth Documented versions for release
 * 0.6.0
 *
 */
