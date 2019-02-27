/*******************************************************************************
 * Copyright (C) 2003-2019, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

package com.nqadmin.swingSet;

/**
 * SSDBNav.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
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
 *           SSRowSet.
 *
 *      performPostDeletionOps() is called when the user presses the delete
 *           button and after the deleteRow() method is called on the SSRowSet.
 *
 *      Note that both the performPreDeletionOps() and performPostDeletionOps()
 *      will be executed when the user presses the delete button.
 */
public interface SSDBNav {

    /**
     *  Constant indicating the navigation button next.
     */
    public static final int NAVIGATION_NEXT      = 1;

    /**
     *  Constant indicating the navigation button previous.
     */
    public static final int NAVIGATION_PREVIOUS  = 2;

    /**
     *  Constant indicating the navigation button first.
     */
    public static final int NAVIGATION_FIRST     = 3;

    /**
     *  Constant indicating the navigation button last.
     */
    public static final int NAVIGATION_LAST      = 4;

    /**
     * Method to perform pre-insertion operations.
     */
    public void performPreInsertOps();

    /**
     *    This function is called just before inserting the row into the database
     *@return true is row can be inserted else false.
     */
    public boolean allowInsertion();
    
    /**
     * Method to perform post-insertion operations.
     *
     * In addition to this you can have a listener on the SSRowSet attached
     * to a SSDataNavigator to get notified when a row is inserted.
     */
    public void performPostInsertOps();

    /**
     * Method to perform operations when the user is on the insert row and
     * cancels the insert by clicking on the undo button.
     */
    public void performCancelOps();

    /**
     *  This function will be called after performPreDeletionOps is  called but before
     *the row is deleted.
     *@return true if the row can be deleted else false.
     */
    public boolean allowDeletion();
    
    /**
     * Method to perform pre-deletion operations.
     *
     * SSRowSet provides notification before the deletion of a row. 
     */
    public void performPreDeletionOps();

    /**
     * Method to perform post-deletion operations.
     *
     * The SSRowSet listener also provides the notification after the deletion of the row.
     */
    public void performPostDeletionOps();

    /**
     * Method to perform navigation-related operations.
     *
     * Possible values are NAVIGATION_NEXT, NAVIGATION_PREVIOUS, NAVIGATION_FIRST,
     * NAVIGATION_LAST.
     *
     * @param _navigationType    this indicates the type of navigation.
     */
    public void performNavigationOps(int _navigationType);

    /**
     * Method to perform operations when the user hits the refresh button.
     */
    public void performRefreshOps();
    
    /**
     * This functions is called just before calling the updateRow on the rowset.
     * @return true is the row can be updated else false.
     */
    public boolean allowUpdate();
    
    /**
     * Method to perform operations after the updateRow has been called.
     */
    public void performPostUpdateOps();

} // end public interface SSDBNav {

/*
 * $Log$
 * Revision 1.13  2005/11/02 17:10:23  yoda2
 * Added two functions: allowUpdate() & performPostUpdateOps()
 *
 * Revision 1.12  2005/05/03 15:17:38  prasanth
 * Added two new functions to the interface.
 * 1. allowInsertion
 * 2. allowDeletion
 *
 * Revision 1.11  2005/02/09 17:21:21  yoda2
 * JavaDoc cleanup.
 *
 * Revision 1.10  2005/02/04 22:48:53  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.9  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.8  2004/11/01 15:53:30  yoda2
 * Fixed various JavaDoc errors.
 *
 * Revision 1.7  2004/10/25 22:13:44  yoda2
 * Updated JavaDoc for new datasource abstraction layer in 0.9.0 release.
 *
 * Revision 1.6  2004/08/10 22:06:59  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.5  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.4  2003/12/16 18:01:40  prasanth
 * Documented versions for release 0.6.0
 *
 * Revision 1.3  2003/11/26 21:21:50  prasanth
 * Added function performCancelOps().
 *
 * Revision 1.2  2003/09/25 14:27:45  yoda2
 * Removed unused Import statements and added preformatting tags to JavaDoc descriptions.
 *
 * Revision 1.1.1.1  2003/09/25 13:56:43  yoda2
 * Initial CVS import for SwingSet.
 *
 */
