/*
 * SelectorElement.java
 *
 * Created on 24 de marzo de 2004, 15:17
 */

package com.nqadmin.swingSet.formatting.selectors;

/**
 *
 * @author  dags
 */
public class SelectorElement extends Object
{
    
    /**
     * Holds value of property listValue.
     */
    private Object listValue;
    
    /**
     * Utility field used by bound properties.
     */
    private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);
    
    /**
     * Holds value of property dataValue.
     */
    private Object dataValue;
    
    public SelectorElement() {
        
    }
    
    public SelectorElement(Object bD, Object lD)
    {
        this.setDataValue(bD);
        this.setListValue(lD);
    }
    
    public String toString()
    {
        return listValue.toString().trim() + " (" + dataValue.toString().trim() + ")";
    }
    
    /**
     * Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l)
    {
        
        propertyChangeSupport.addPropertyChangeListener(l);
    }
    
    /**
     * Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l)
    {
        
        propertyChangeSupport.removePropertyChangeListener(l);
    }
    
    /**
     * Getter for property listValue.
     * @return Value of property listValue.
     */
    public Object getListValue()
    
    {
        
        return this.listValue;
    }
    
    /**
     * Setter for property listValue.
     * @param listValue New value of property listValue.
     */
    public void setListValue(Object listValue)
    {
        
        Object oldListValue = this.listValue;
        this.listValue = listValue;
        propertyChangeSupport.firePropertyChange("listValue", oldListValue, listValue);
    }
    
    /**
     * Getter for property dataValue.
     * @return Value of property dataValue.
     */
    public Object getDataValue()
    
    {
        
        return this.dataValue;
    }
    
    /**
     * Setter for property dataValue.
     * @param dataValue New value of property dataValue.
     */
    public void setDataValue(Object dataValue)
    {
        Object olddataValue = this.dataValue;
        this.dataValue = dataValue;
        propertyChangeSupport.firePropertyChange("dataValue", olddataValue, dataValue);
    }
    
    
    
}
