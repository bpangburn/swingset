/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2005, The Pangburn Company and Prasanth R. Pasala.
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
 *<p><pre>
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
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
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
     * if the user intends to perform some actions before insertin the row this function should be used
     */
    public void performPreInsertOps();

    /**
     * Post insert operations should be performed in this function.
     *
     * In addition to this you can have a listener on the SSRowSet attached to SSDataNavigator
     * to get notiifcation when a row is inserted.
     */
    public void performPostInsertOps();

    /**
     * This function is called when the user is on the insert row and cancels the insert
     * by clicking on the undo button.
     */
    public void performCancelOps();

    /**
     * The user should perform the pre deletion operations using this function.
     *
     * SSRowSet does not provide any notifications before the deletion of a row. but a notification
     * will be received after the deletion if you have listener for the SSRowSet.
     */
    public void performPreDeletionOps();

    /**
     * This function should perform post deletion operations if the user intends to do so.
     *
     * The SSRowSet listener also provides the notification after the deletion of the row.
     */
    public void performPostDeletionOps();

    /**
     * This function is called when ever navigation takes place.
     *
     * Possible values are NAVIGATION_NEXT, NAVIGATION_PREVIOUS, NAVIGATION_FIRST,
     * NAVIGATION_LAST.
     *
     * @param _navigationType    this indicates the type of navigation.
     */
    public void performNavigationOps(int _navigationType);

    /**
     * This function is called when the user what to refresh the information.
     * (When the user presses the refresh button on the navigator.
     */
    public void performRefreshOps();

} // end public interface SSDBNav {



/*
 * $Log$
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
