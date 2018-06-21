/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2006, The Pangburn Company and Prasanth R. Pasala.
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

import java.io.Serializable;

/**
 * SSDBNavAdapter.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Abstract class that provides empty implementations of all the methods for the
 * SSDBNav interface.
 *
 * This class is provided for convenience. so that users wishing to write their
 * own SSDBNav implementations can just extend the abstract class and override
 * the desired methods.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
public class SSDBNavAdapter implements SSDBNav, Serializable {

    /**
     * Method to perform pre-insertion operations.
     */
    public void performPreInsertOps(){
    }

    /**
     *  This function always return true. This would allow the insertions
     *if an implementation is not provided.
     */ 
    public boolean allowInsertion(){
        return true;
    }
    
    /**
     * Method to perform post-insertion operations.
     *
     * In addition to this you can have a listener on the SSRowSet attached
     * to a SSDataNavigator to get notified when a row is inserted.
     */
    public void performPostInsertOps(){
    }

    /**
     * Method to perform operations when the user is on the insert row and
     * cancels the insert by clicking on the undo button.
     */    
    public void performCancelOps(){
    }
    
    /**
     * Method to perform pre-deletion operations.
     *
     * SSRowSet does not provide any notifications before the deletion of a row. but a notification
     * will be received after the deletion if you have listener for the SSRowSet.
     */
    public void performPreDeletionOps(){
    }

    /**
     *  This function returns true by default. This allows for deletion of rows
     *if an implementation is not provided.
     *@return returns true.
     */
    public boolean allowDeletion(){
        return true;
    }
    
    /**
     * Method to perform post-deletion operations.
     *
     * The SSRowSet listener also provides the notification after the deletion of the row.
     */    
    public void performPostDeletionOps(){
    }

    /**
     * Method to perform navigation-related operations.
     *
     * Possible values are NAVIGATION_NEXT, NAVIGATION_PREVIOUS, NAVIGATION_FIRST,
     * NAVIGATION_LAST.
     *
     * @param _navigationType    this indicates the type of navigation.
     */    
    public void performNavigationOps(int _navigationType){
    }

    /**
     * Method to perform operations when the user hits the refresh button.
     */    
    public void performRefreshOps(){
    }
    
    /**
     * This functions is called just before calling the updateRow on the rowset.
     * @return true is the row can be updated else false.
     */
    public boolean allowUpdate(){
    	return true;
    }
    
    /**
     * Method to perform operations after the updateRow has been called.
     */
    public void performPostUpdateOps(){
    	
    }

} // end public class SSDBNavAdapter implements SSDBNav, Serializable {



/*
 * $Log$
 * Revision 1.11  2005/11/02 17:17:26  prasanth
 * Added empty implementations for allowUpdate & performPostUpdateOps.
 *
 * Revision 1.10  2005/05/03 15:22:28  prasanth
 * Added default implementations for new functions allowInsertion & allowDeletion.
 *
 * Revision 1.9  2005/02/09 17:21:21  yoda2
 * JavaDoc cleanup.
 *
 * Revision 1.8  2005/02/04 22:48:53  yoda2
 * API cleanup & updated Copyright info.
 *
 * Revision 1.7  2004/11/11 14:45:48  yoda2
 * Using TextPad, converted all tabs to "soft" tabs comprised of four actual spaces.
 *
 * Revision 1.6  2004/08/10 22:06:59  yoda2
 * Added/edited JavaDoc, made code layout more uniform across classes, made various small coding improvements suggested by PMD.
 *
 * Revision 1.5  2004/08/02 15:22:51  prasanth
 * Implements Serializable.
 *
 * Revision 1.4  2004/03/08 16:43:37  prasanth
 * Updated copy right year.
 *
 * Revision 1.3  2003/11/26 21:22:11  prasanth
 * Added function performCancelOps().
 *
 * Revision 1.2  2003/09/25 14:27:45  yoda2
 * Removed unused Import statements and added preformatting tags to JavaDoc descriptions.
 *
 * Revision 1.1.1.1  2003/09/25 13:56:43  yoda2
 * Initial CVS import for SwingSet.
 *
 */