/*
 * SSFormattedComboBoxBeanInfo.java
 *
 * Created on 19 de abril de 2005, 14:51
 */

package com.nqadmin.swingSet.formatting;

import java.beans.*;

/**
 * @author dags
 */
public class SSFormattedComboBoxBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( SSFormattedComboBox.class , null );//GEN-HEADEREND:BeanDescriptor
        
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;         }//GEN-LAST:BeanDescriptor
    
    
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_columnType = 0;
    private static final int PROPERTY_connection = 1;
    private static final int PROPERTY_dataColumn = 2;
    private static final int PROPERTY_editable = 3;
    private static final int PROPERTY_editor = 4;
    private static final int PROPERTY_evenRowBackground = 5;
    private static final int PROPERTY_evenRowForeground = 6;
    private static final int PROPERTY_listColumn = 7;
    private static final int PROPERTY_locale = 8;
    private static final int PROPERTY_model = 9;
    private static final int PROPERTY_navigator = 10;
    private static final int PROPERTY_nextFocusableComponent = 11;
    private static final int PROPERTY_oddRowBackground = 12;
    private static final int PROPERTY_oddRowForeground = 13;
    private static final int PROPERTY_orderBy = 14;
    private static final int PROPERTY_preferredSize = 15;
    private static final int PROPERTY_prototypeDisplayValue = 16;
    private static final int PROPERTY_renderer = 17;
    private static final int PROPERTY_SSRowSet = 18;
    private static final int PROPERTY_table = 19;
    private static final int PROPERTY_width = 20;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[21];
    
        try {
            properties[PROPERTY_columnType] = new PropertyDescriptor ( "columnType", SSFormattedComboBox.class, "getColumnType", "setColumnType" );
            properties[PROPERTY_columnType].setPreferred ( true );
            properties[PROPERTY_connection] = new PropertyDescriptor ( "connection", SSFormattedComboBox.class, "getConnection", "setConnection" );
            properties[PROPERTY_connection].setPreferred ( true );
            properties[PROPERTY_dataColumn] = new PropertyDescriptor ( "dataColumn", SSFormattedComboBox.class, "getDataColumn", "setDataColumn" );
            properties[PROPERTY_dataColumn].setPreferred ( true );
            properties[PROPERTY_editable] = new PropertyDescriptor ( "editable", SSFormattedComboBox.class, "isEditable", "setEditable" );
            properties[PROPERTY_editor] = new PropertyDescriptor ( "editor", SSFormattedComboBox.class, "getEditor", "setEditor" );
            properties[PROPERTY_evenRowBackground] = new PropertyDescriptor ( "evenRowBackground", SSFormattedComboBox.class, "getEvenRowBackground", "setEvenRowBackground" );
            properties[PROPERTY_evenRowBackground].setPreferred ( true );
            properties[PROPERTY_evenRowForeground] = new PropertyDescriptor ( "evenRowForeground", SSFormattedComboBox.class, "getEvenRowForeground", "setEvenRowForeground" );
            properties[PROPERTY_evenRowForeground].setPreferred ( true );
            properties[PROPERTY_listColumn] = new PropertyDescriptor ( "listColumn", SSFormattedComboBox.class, "getListColumn", "setListColumn" );
            properties[PROPERTY_listColumn].setPreferred ( true );
            properties[PROPERTY_locale] = new PropertyDescriptor ( "locale", SSFormattedComboBox.class, "getLocale", "setLocale" );
            properties[PROPERTY_model] = new PropertyDescriptor ( "model", SSFormattedComboBox.class, "getModel", "setModel" );
            properties[PROPERTY_navigator] = new PropertyDescriptor ( "navigator", SSFormattedComboBox.class, "getNavigator", "setNavigator" );
            properties[PROPERTY_navigator].setPreferred ( true );
            properties[PROPERTY_nextFocusableComponent] = new PropertyDescriptor ( "nextFocusableComponent", SSFormattedComboBox.class, "getNextFocusableComponent", "setNextFocusableComponent" );
            properties[PROPERTY_oddRowBackground] = new PropertyDescriptor ( "oddRowBackground", SSFormattedComboBox.class, "getOddRowBackground", "setOddRowBackground" );
            properties[PROPERTY_oddRowBackground].setPreferred ( true );
            properties[PROPERTY_oddRowForeground] = new PropertyDescriptor ( "oddRowForeground", SSFormattedComboBox.class, "getOddRowForeground", "setOddRowForeground" );
            properties[PROPERTY_oddRowForeground].setPreferred ( true );
            properties[PROPERTY_orderBy] = new PropertyDescriptor ( "orderBy", SSFormattedComboBox.class, "getOrderBy", "setOrderBy" );
            properties[PROPERTY_orderBy].setPreferred ( true );
            properties[PROPERTY_preferredSize] = new PropertyDescriptor ( "preferredSize", SSFormattedComboBox.class, "getPreferredSize", "setPreferredSize" );
            properties[PROPERTY_prototypeDisplayValue] = new PropertyDescriptor ( "prototypeDisplayValue", SSFormattedComboBox.class, "getPrototypeDisplayValue", "setPrototypeDisplayValue" );
            properties[PROPERTY_renderer] = new PropertyDescriptor ( "renderer", SSFormattedComboBox.class, "getRenderer", "setRenderer" );
            properties[PROPERTY_SSRowSet] = new PropertyDescriptor ( "SSRowSet", SSFormattedComboBox.class, "getSSRowSet", "setSSRowSet" );
            properties[PROPERTY_SSRowSet].setPreferred ( true );
            properties[PROPERTY_table] = new PropertyDescriptor ( "table", SSFormattedComboBox.class, "getTable", "setTable" );
            properties[PROPERTY_table].setPreferred ( true );
            properties[PROPERTY_width] = new PropertyDescriptor ( "width", SSFormattedComboBox.class, "getWidth", null );
        }
        catch( IntrospectionException e) {}//GEN-HEADEREND:Properties
        
        // Here you can add code for customizing the properties array.
        
        return properties;         }//GEN-LAST:Properties
    
    // Event set information will be obtained from introspection.//GEN-FIRST:Events
    private static EventSetDescriptor[] eventSets = null;
    private static EventSetDescriptor[] getEdescriptor(){//GEN-HEADEREND:Events
        
        // Here you can add code for customizing the event sets array.
        
        return eventSets;     }//GEN-LAST:Events
    
    // Method information will be obtained from introspection.//GEN-FIRST:Methods
    private static MethodDescriptor[] methods = null;
    private static MethodDescriptor[] getMdescriptor(){//GEN-HEADEREND:Methods
        
        // Here you can add code for customizing the methods array.
        
        return methods;     }//GEN-LAST:Methods
    
    
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

