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
 *   Ernie R. Rael
 ******************************************************************************/
package com.nqadmin.swingset;

import java.io.Serializable;

// SSDataGridAdapter.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * This abstract class is provided as a convenience for creating
 * custom SSDataGridHandler objects. Extend this class to create a
 * SSDataGridHandler implementation.
 * <p>
 * SSDataGridHandlerImpl defines empty functions so that the programmer can define
 * only the functions desired.
 */
public abstract class SSDataGridAdapter implements SSDataGridHandler, Serializable {

    /**
	 * unique serial id
	 */
	private static final long serialVersionUID = -6957488891365154999L;

	/**
     * This empty implementation always returns true.
     * For description about the function look in SSRowDeletion class.
     *
     * @param _row    the row number in data grid.
     */
    @Override
	public boolean allowDeletion(final int _row) {
        return true;
    }

    /**
     * Method to perform post-deletion operations.
     *
	 * @param _row the position of deleted row in the data grid.
     */
    @Override
	public void performPostDeletionOps(final int _row){
    	// do nothing
    }

    /**
     * Method to perform post-insertion operations.
     *
     * @param _row position of added row in the data grid.
     */
    @Override
	public void performPostInsertOps(final int _row) {
    	// do nothing
    }

    /**
     * Method to perform pre-deletion operations.
     *
     * @param _row position of data grid row being deleted.
     */
    @Override
	public void performPreDeletionOps(final int _row) {
    	// do nothing
    }

    /**
     * Method to perform pre-insertion operations.
     *
     * @param _row position of new row in the data grid.
     */
    @Override
	public void performPreInsertOps(final int _row){
    	// do nothing
    }
}
