/*
 * SSDateField.java
 *
 */

package com.nqadmin.swingSet.testing;

/**
 *
 * @author dags
 */

public class SSDateField extends SSFormattedTextField {
    
    /** Creates a new instance of SSDateField */
    public SSDateField() {
        this(new SSDateFormatterFactory());
    }
            
    public SSDateField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        super(factory);
    }
}
