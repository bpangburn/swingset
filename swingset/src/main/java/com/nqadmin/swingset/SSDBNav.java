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

import com.nqadmin.swingset.utils.SSEnums.Navigation;

// SSDBNav.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * Interface that provides a set of methods to perform custom operations before
 * a record is added, after a record is added, before a record is deleted and
 * after a record is deleted.
 * <p>
 * These functions are called by the SSDataNavigator if the SSDBNav datamember
 * of the SSDataNavigator is set using the setDBNav() function of the
 * SSDataNavigator.
 * <p>
 * performPreInsertOps() is called when the user presses the insert button.
 * <p>
 * performPostInsertOps() is called when the user presses the commit button
 * after updating the values for the newly inserted row. If the user presses the
 * Undo button after the insert button is pressed the insertion is cancelled and
 * this function will not be called.
 * <p>
 * performPreDeletionOps() is called when the user presses the delete button,
 * but just before the deleteRow() method is called on the SSRowSet.
 * <p>
 * performPostDeletionOps() is called when the user presses the delete button
 * and after the deleteRow() method is called on the SSRowSet.
 * <p>
 * Note that both the performPreDeletionOps() and performPostDeletionOps() will
 * be executed when the user presses the delete button.
 * <p>
 * Generally the user will want to use/extend SSDBNavImp as it has an
 * implementation of performPreInsertOps() that will clear/reset component
 * values when a new record is added.
 */
public interface SSDBNav extends Serializable {

	/**
	 * Constant indicating the navigation button first.
	 *
	 * @deprecated Starting in 2.3.0+ use {@link Navigation#First} instead.
	 */
	@Deprecated
	public static final int NAVIGATION_FIRST = 3;

	/**
	 * Constant indicating the navigation button last.
	 *
	 * @deprecated Starting in 2.3.0+ use {@link Navigation#Last} instead.
	 */
	@Deprecated
	public static final int NAVIGATION_LAST = 4;

	/**
	 * Constant indicating the navigation button next.
	 *
	 * @deprecated Starting in 2.3.0+ use {@link Navigation#Next} instead.
	 */
	@Deprecated
	public static final int NAVIGATION_NEXT = 1;

	/**
	 * Constant indicating the navigation button previous.
	 *
	 * @deprecated Starting in 2.3.0+ use {@link Navigation#Previous} instead.
	 */
	@Deprecated
	public static final int NAVIGATION_PREVIOUS = 2;

	/**
	 * unique serial id
	 */
	static final long serialVersionUID = -4632504656498312457L;

	/**
	 * This function will be called after performPreDeletionOps is called but before
	 * the row is deleted.
	 *
	 * @return true if the row can be deleted else false.
	 */
	default boolean allowDeletion() {

		return true;

	}

	/**
	 * This function is called just before inserting the row into the database
	 *
	 * @return true is row can be inserted else false.
	 */
	default boolean allowInsertion() {
		return true;

	}

	/**
	 * This functions is called just before calling the updateRow on the rowset.
	 *
	 * @return true is the row can be updated else false.
	 */
	default boolean allowUpdate() {

		return true;

	}

	/**
	 * Method to perform operations when the user is on the insert row and cancels
	 * the insert by clicking on the undo button.
	 */
	default void performCancelOps() {
		// no action by default

	}

	/**
	 * Method to perform navigation-related operations.
	 * <p>
	 * Possible values are NAVIGATION_NEXT, NAVIGATION_PREVIOUS, NAVIGATION_FIRST,
	 * NAVIGATION_LAST.
	 *
	 * @param _navigationType this indicates the type of navigation.
	 *
	 * @deprecated Use
	 *             {@link #performNavigationOps(com.nqadmin.swingset.utils.SSEnums.Navigation _navType)}
	 *             instead.
	 */
	@Deprecated
	default void performNavigationOps(final int _navigationType) {
		// no action by default

	}

	/**
	 * Method to perform navigation-related operations.
	 *
	 * @param _navType - indicates type of navigation that is occurring
	 *
	 */
	default void performNavigationOps(final Navigation _navType) {
		performNavigationOps(_navType.getDeprecatedConstant());

	}

	/**
	 * Method to perform post-deletion operations.
	 * <p>
	 * The SSRowSet listener also provides the notification after the deletion of
	 * the row.
	 */
	default void performPostDeletionOps() {
		// no action by default

	}

	/**
	 * Method to perform post-insertion operations.
	 * <p>
	 * In addition to this you can have a listener on the SSRowSet attached to a
	 * SSDataNavigator to get notified when a row is inserted.
	 */
	default void performPostInsertOps() {
		// no action by default

	}

	/**
	 * Method to perform operations after the updateRow has been called.
	 */
	default void performPostUpdateOps() {
		// no action by default

	}

	/**
	 * Method to perform pre-deletion operations.
	 * <p>
	 * SSRowSet provides notification before the deletion of a row.
	 */
	default void performPreDeletionOps() {
		// no action by default

	}

	/**
	 * Method to perform pre-insertion operations.
	 */
	default void performPreInsertOps() {
		// no action by default

	}

	/**
	 * Method to perform operations when the user hits the refresh button.
	 */
	default void performRefreshOps() {
		// no action by default

	}

} // end public interface SSDBNav {

