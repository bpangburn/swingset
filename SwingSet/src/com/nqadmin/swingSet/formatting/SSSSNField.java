/*
 * SSSSNField.java
 *
 */

package com.nqadmin.swingSet.testing;

import javax.swing.JFormattedTextField.AbstractFormatterFactory;

/**
 *
 * @author dags
 */

public class SSSSNField extends SSFormattedTextField {
  
  
    /** Creates a new instance of SSSSNField */
    public SSSSNField() {
        this(new SSNFormatterFactory());
    }
    
    public SSSSNField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        super(factory);
    }
}

