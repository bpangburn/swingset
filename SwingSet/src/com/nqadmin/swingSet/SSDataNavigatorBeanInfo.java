/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2005, The Pangburn Company, Inc. and Prasanth R. Pasala.
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

import java.beans.*;

/**
 * SSDataNavigatorBeanInfo.java
 *<p>
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 *<p><pre>
 * Contains & controls various bean properties for SSDataNavigator.
 *</pre><p>
 * @author  $Author$
 * @version $Revision$
 */
 public class SSDataNavigatorBeanInfo extends SimpleBeanInfo {

    /**
     * Provides the icon representation for the related bean.
     *
     * @param _iconKind The kind of icon requested. This should be one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32, ICON_MONO_16x16, or ICON_MONO_32x32.
     *
     * @return An image object representing the requested icon. May return null if no suitable icon is available.
     */
    public java.awt.Image getIcon(int iconKind) {
        
        if (iconKind == BeanInfo.ICON_MONO_16x16 ||
            iconKind == BeanInfo.ICON_COLOR_16x16 )
        {
            java.awt.Image img = loadImage("/images/icons/ssdatanavigator.gif");
            return img;
        }
        if (iconKind == BeanInfo.ICON_MONO_32x32 ||
            iconKind == BeanInfo.ICON_COLOR_32x32 )
        {
            java.awt.Image img = loadImage("/images/icons/ssdatanavigator32.gif");
            return img;
        }
        
        return null;
        
    } // end public java.awt.Image getIcon(int iconKind) {

}

/*
 * $Log
 */
