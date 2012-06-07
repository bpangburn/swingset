/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2011, The Pangburn Company and Prasanth R. Pasala
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  The names of its contributors may not be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.nqadmin.swingSet;

/**
 * SSDataGridHandler.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * The SSDataGridHandler interface specifies set of methods that can be used to
 * determine whether or not a given row can be deleted, and operation to be 
 * performed before and after deletion or insertion of a record.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
public interface SSDataGridHandler {

    /**
     * Returns true if the row _row can be deleted
     *
     * SSTableModel calls this function if the row deletion is requested 
	 * (If SSDataGridHandler is implemented).
     *
     * @param _row    the row number in data grid.
     * @return returns true is the row can be deleted else false.
     */
    public boolean allowDeletion(int _row);
    
    /**
     * Method to specify any pre-deletion operations.
     * 
     *  @param _row position of data grid row being deleted.
     */
    public void performPreDeletionOps(int _row);

    /**
     * Method to perform post-deletion operations.
     *
	 * @param _row  position of deleted row in the data grid.
     */
    public void performPostDeletionOps(int _row);
    
    /**
     * Method to perform pre-insertion operations.
     * 
     * @param _row position of new row in the data grid.
     */
    public void performPreInsertOps(int _row);

    /**
     * Method to perform post-insertion operations.
     *
     * @param _row position of added grid row.
     */
    public void performPostInsertOps(int _row);
}



/* $Log$
 */