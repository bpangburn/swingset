/*
 * PgIntegerField.java
 *
 */

package com.nqadmin.swingSet.testing;

import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;
import java.util.Locale;
import javax.swing.JTextField;


/**
 *
 * @author dags
 */

public class SSIntegerField extends SSFormattedTextField {
    
    private int precision = 1;
    
    /**
     * Holds value of property minimumIntegerDigits.
     */
    private int minimumIntegerDigits;
    
    /** Creates a new instance of PgIntegerField */
    public SSIntegerField() {
        this(new SSIntegerFormatterFactory());
    }
    
    public SSIntegerField(int precision) {
        this(new SSIntegerFormatterFactory(precision));
    }
    
    public SSIntegerField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        super(factory);
        this.setHorizontalAlignment(JTextField.RIGHT);
    }
    
    public int getPrecision() {
        return precision;
    }
    
    public void setPrecision(int precision) {
        this.precision = precision;
        this.setFormatterFactory(new SSIntegerFormatterFactory(precision));
    }
    
    
    /**
     * Getter for property minimumIntegerDigits.
     * @return Value of property minimumIntegerDigits.
     */
    public int getMinimumIntegerDigits() {
        
        return this.minimumIntegerDigits;
    }
    
    /**
     * Setter for property minimumIntegerDigits.
     * @param minimumIntegerDigits New value of property minimumIntegerDigits.
     */
    public void setMinimumIntegerDigits(int minimumIntegerDigits) {
        
        this.minimumIntegerDigits = minimumIntegerDigits;
    }
}
