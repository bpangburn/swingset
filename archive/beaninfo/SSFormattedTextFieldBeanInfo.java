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

package com.nqadmin.swingSet.formatting;

import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * SSFormattedTextFieldBeanInfo.java
 * 
 * SwingSet - Open Toolkit For Making Swing Controls Database-Aware
 * 
 * Contains & controls various bean properties for SSFormattedTextField
 */
public class SSFormattedTextFieldBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.nqadmin.swingSet.formatting.SSFormattedTextField.class , null );
        beanDescriptor.setPreferred ( true );
        beanDescriptor.setDisplayName ( "SSFormattedTextField" );
        beanDescriptor.setShortDescription ( "A FormattedTextField bound to a jdbc column" );//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;
    }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_background = 0;
    private static final int PROPERTY_border = 1;
    private static final int PROPERTY_columnName = 2;
    private static final int PROPERTY_columns = 3;
    private static final int PROPERTY_editable = 4;
    private static final int PROPERTY_enabled = 5;
    private static final int PROPERTY_font = 6;
    private static final int PROPERTY_foreground = 7;
    private static final int PROPERTY_formatterFactory = 8;
    private static final int PROPERTY_helper = 9;
    private static final int PROPERTY_horizontalAlignment = 10;
    private static final int PROPERTY_locale = 11;
    private static final int PROPERTY_nextFocusableComponent = 12;
    private static final int PROPERTY_nullable = 13;
    private static final int PROPERTY_preferredSize = 14;
    private static final int PROPERTY_SSDataNavigator = 15;
    private static final int PROPERTY_SSFormattedComboBox = 16;
    private static final int PROPERTY_SSRowSet = 17;
    private static final int PROPERTY_text = 18;
    private static final int PROPERTY_toolTipText = 19;
    private static final int PROPERTY_value = 20;
    private static final int PROPERTY_width = 21;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[22];
    
        try {
            properties[PROPERTY_background] = new PropertyDescriptor ( "background", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getBackground", "setBackground" );
            properties[PROPERTY_border] = new PropertyDescriptor ( "border", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getBorder", "setBorder" );
            properties[PROPERTY_columnName] = new PropertyDescriptor ( "columnName", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getColumnName", "setColumnName" );
            properties[PROPERTY_columnName].setPreferred ( true );
            properties[PROPERTY_columnName].setDisplayName ( "Column Name" );
            properties[PROPERTY_columns] = new PropertyDescriptor ( "columns", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getColumns", "setColumns" );
            properties[PROPERTY_editable] = new PropertyDescriptor ( "editable", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "isEditable", "setEditable" );
            properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "isEnabled", "setEnabled" );
            properties[PROPERTY_font] = new PropertyDescriptor ( "font", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getFont", "setFont" );
            properties[PROPERTY_foreground] = new PropertyDescriptor ( "foreground", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getForeground", "setForeground" );
            properties[PROPERTY_formatterFactory] = new PropertyDescriptor ( "formatterFactory", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getFormatterFactory", "setFormatterFactory" );
            properties[PROPERTY_helper] = new PropertyDescriptor ( "helper", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, null, "setHelper" );
            properties[PROPERTY_helper].setPreferred ( true );
            properties[PROPERTY_helper].setDisplayName ( "HelperPopup" );
            properties[PROPERTY_horizontalAlignment] = new PropertyDescriptor ( "horizontalAlignment", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getHorizontalAlignment", "setHorizontalAlignment" );
            properties[PROPERTY_locale] = new PropertyDescriptor ( "locale", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getLocale", "setLocale" );
            properties[PROPERTY_nextFocusableComponent] = new PropertyDescriptor ( "nextFocusableComponent", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getNextFocusableComponent", "setNextFocusableComponent" );
            properties[PROPERTY_nullable] = new PropertyDescriptor ( "nullable", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "isNullable", "setNullable" );
            properties[PROPERTY_nullable].setPreferred ( true );
            properties[PROPERTY_preferredSize] = new PropertyDescriptor ( "preferredSize", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getPreferredSize", "setPreferredSize" );
            properties[PROPERTY_SSDataNavigator] = new PropertyDescriptor ( "SSDataNavigator", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getSSDataNavigator", "setSSDataNavigator" );
            properties[PROPERTY_SSDataNavigator].setPreferred ( true );
            properties[PROPERTY_SSDataNavigator].setDisplayName ( "SSDataNavigator" );
            properties[PROPERTY_SSFormattedComboBox] = new PropertyDescriptor ( "SSFormattedComboBox", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getSSFormattedComboBox", "setSSFormattedComboBox" );
            properties[PROPERTY_SSFormattedComboBox].setPreferred ( true );
            properties[PROPERTY_SSRowSet] = new PropertyDescriptor ( "SSRowSet", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getSSRowSet", "setSSRowSet" );
            properties[PROPERTY_SSRowSet].setPreferred ( true );
            properties[PROPERTY_SSRowSet].setDisplayName ( "SSRowSet" );
            properties[PROPERTY_text] = new PropertyDescriptor ( "text", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getText", "setText" );
            properties[PROPERTY_toolTipText] = new PropertyDescriptor ( "toolTipText", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getToolTipText", "setToolTipText" );
            properties[PROPERTY_toolTipText].setPreferred ( true );
            properties[PROPERTY_value] = new PropertyDescriptor ( "value", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getValue", "setValue" );
            properties[PROPERTY_width] = new PropertyDescriptor ( "width", com.nqadmin.swingSet.formatting.SSFormattedTextField.class, "getWidth", null );
        }
        catch( IntrospectionException e) {}//GEN-HEADEREND:Properties
        
        // Here you can add code for customizing the properties array.
        
        return properties;
    }//GEN-LAST:Properties
    
    // Event set information will be obtained from introspection.//GEN-FIRST:Events
    private static EventSetDescriptor[] eventSets = null;
    private static EventSetDescriptor[] getEdescriptor(){//GEN-HEADEREND:Events
        
        // Here you can add code for customizing the event sets array.
        
        return eventSets;
    }//GEN-LAST:Events
    
    // Method information will be obtained from introspection.//GEN-FIRST:Methods
    private static MethodDescriptor[] methods = null;
    private static MethodDescriptor[] getMdescriptor(){//GEN-HEADEREND:Methods
        
        // Here you can add code for customizing the methods array.
        
        return methods;
    }//GEN-LAST:Methods
    
    
    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx
    
    
//GEN-FIRST:Superclass
    
    // Here you can add code for customizing the Superclass BeanInfo.
    
//GEN-LAST:Superclass
    
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        return getBdescriptor();
    }
    
    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getPdescriptor();
    }
    
    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return  An array of EventSetDescriptors describing the kinds of
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return getEdescriptor();
    }
    
    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return  An array of MethodDescriptors describing the methods
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return getMdescriptor();
    }
    
    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }
    
    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean.
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }
}
