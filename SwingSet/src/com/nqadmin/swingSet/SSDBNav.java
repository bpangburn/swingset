/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003, The Pangburn Company, Inc. and Prasanth R. Pasala.
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
 * SSDBNav.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p>
 * Interface that provides a set of functions to perform some custom operation
 * before a record is added, after a record is added, before a record is deleted
 * and after a record is deleted.
 *
 * These functions are called by the SSDataNavigator if the SSDBNav datamember of
 * the SSDataNavigator is set using the setDBNav() function of the
 * SSDataNavigator.
 *
 *      performPreInsertOps() is called when the user presses the insert button.
 *
 *      performPostInsertOps() is called when the user presses the commit button
 *           after updating the values for the newly inserted row. If the user
 *           presses the Undo button after the insert button is pressed the
 *           insertion is cancelled and this function will not be called.
 *
 *      performPreDeletionOps() is called when the user presses the delete
 *           button, but just before the deleteRow() method is called on the
 *           RowSet.
 *
 *      performPostDeletionOps() is called when the user presses the delete
 *           button and after the deleteRow() method is called on the RowSet.
 *
 *      Note that both the performPreDeletionOps() and performPostDeletionOps()
 *      will be executed when the user presses the delete button.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public interface SSDBNav {

	/**
	 * if the user intends to perform some actions before insertin the row this function should be used
	 */
	public void performPreInsertOps();

	/**
	 * Post insert operations should be performed in this function.
	 *
	 * In addition to this you can have a listener on the rowset attached to SSDataNavigator
	 *to get notiifcation when a row is inserted.
	 */
	public void performPostInsertOps();

	/**
	 * The user should perform the pre deletion operations using this function.
	 *
	 *	Rowset does not provide any notifications before the deletion of a row. but a notification
	 *will be receied after the deletion if you have listener for the rowset.
	 */
	public void performPreDeletionOps();

	/**
	 *	This function should perform post deletion operations if the user intends to do so.
	 *
	 *	The rowset listener also provides the notification after the deletion of the row.
	 */
	public void performPostDeletionOps();

}



/*
 * $Log$
 */
