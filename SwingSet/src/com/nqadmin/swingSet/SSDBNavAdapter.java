/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2004, The Pangburn Company, Inc. and Prasanth R. Pasala.
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
 * @author	$Author$
 * @version	$Revision$
 */
public class SSDBNavAdapter implements SSDBNav, Serializable {

	public void performPreInsertOps(){
	}

	public void performPostInsertOps(){
	}

	public void performPreDeletionOps(){
	}

	public void performPostDeletionOps(){
	}
	
	public void performNavigationOps(int _navigationType){
	}
	
	public void performRefreshOps(){
	}
    
	public void performCancelOps(){
	}

} // end public class SSDBNavAdapter implements SSDBNav, Serializable {



/*
 * $Log$
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