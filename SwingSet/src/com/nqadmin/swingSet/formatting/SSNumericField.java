/*
 * SSNumericField.java
 *
 */

package com.nqadmin.swingSet.testing;

import javax.swing.JFormattedTextField.AbstractFormatterFactory;

import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;
import java.util.Locale;
import javax.swing.JTextField;

/**
 *
 * @author dags
 */

public class SSNumericField extends SSFormattedTextField {
    
    private int precision = 1;
    private int decimals  = 0;

    /**
     * Holds value of property minimumIntegerDigits.
     */
    private int minimumIntegerDigits;

    /** Creates a new instance of PgNumericField */
    public SSNumericField() {
        this(new SSNumericFormatterFactory());
    }
    
    public SSNumericField(int precision, int decimals) {
        this(new SSNumericFormatterFactory(precision, decimals));
    }
            
    
    public SSNumericField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
        super(factory);
        this.setHorizontalAlignment(JTextField.RIGHT);
    }
    
    public int getPrecision() {
        return precision;
    }
    
    public int getDecimals() {
        return decimals;
    }
    
    public void setPrecision(int precision) {
        this.precision = precision;
        this.setFormatterFactory(new SSNumericFormatterFactory(precision, decimals));
    }
    
    public void setDecimals(int decimals) {
        this.decimals = decimals;
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

