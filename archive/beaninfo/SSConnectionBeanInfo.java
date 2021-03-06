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
 
package com.nqadmin.swingSet.datasources;

import java.beans.*;
import java.beans.IntrospectionException;

/**
 * SSConnectionBeanInfo.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Contains & controls various bean properties for SSConnection.
 */
 public class SSConnectionBeanInfo extends SimpleBeanInfo {

    /**
     * Provides the icon representation for the related bean.
     *
     * @param _iconKind The kind of icon requested. This should be one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32, ICON_MONO_16x16, or ICON_MONO_32x32.
     *
     * @return An image object representing the requested icon. May return null if no suitable icon is available.
     */
    public java.awt.Image getIcon(int _iconKind) {
        
        if (_iconKind == BeanInfo.ICON_MONO_16x16 ||
            _iconKind == BeanInfo.ICON_COLOR_16x16 )
        {
            java.awt.Image img = loadImage("/images/icons/ssconnection.gif");
            return img;
        }
        if (_iconKind == BeanInfo.ICON_MONO_32x32 ||
            _iconKind == BeanInfo.ICON_COLOR_32x32 )
        {
            java.awt.Image img = loadImage("/images/icons/ssconnection32.gif");
            return img;
        }
        
        return null;
        
    } // end public java.awt.Image getIcon(int _iconKind) {
    	
    public PropertyDescriptor[] getPropertyDescriptors(){
    	try{
    	
	    	PropertyDescriptor urlDescriptor = new PropertyDescriptor("url", SSConnection.class, "getUrl", "setUrl");
	    	urlDescriptor.setBound(true);
	    	PropertyDescriptor usernameDescriptor = new PropertyDescriptor("username", SSConnection.class, "getUsername", "setUsername");
	    	usernameDescriptor.setBound(true);
	    	PropertyDescriptor passwordDescriptor = new PropertyDescriptor("password", SSConnection.class, "getPassword", "setPassword");
	    	passwordDescriptor.setBound(true);
	    	PropertyDescriptor driverNameDescriptor = new PropertyDescriptor("driverName", SSConnection.class, "getDriverName", "setDriverName");
	    	driverNameDescriptor.setBound(true);
	    	
	    	PropertyDescriptor[] propertyDescriptors = new PropertyDescriptor[]{urlDescriptor, usernameDescriptor, passwordDescriptor, driverNameDescriptor};
	    	return propertyDescriptors;
	    }catch(IntrospectionException ie){
	    	ie.printStackTrace();
	    }
	    return null;
    }	

}

/*
 * $Log$
 * Revision 1.4  2005/02/07 19:56:16  yoda2
 * Fixing JavaDoc errors with _iconKind parameter name.
 *
 * Revision 1.3  2005/02/04 22:41:35  yoda2
 * Updated Copyright info.
 *
 * Revision 1.2  2005/02/02 23:19:04  yoda2
 * Fixed log tag.
 *
 */
