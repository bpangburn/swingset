/*
 * PgCurrencyField.java
 *
 */

package com.nqadmin.swingSet.testing;

import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JTextField;

/**
 *
 * @author dags
 */

public class SSCurrencyField extends SSFormattedTextField {
    
    private int precision = 1;
    private int decimals  = 0;

    /**
     * Holds value of property minimumIntegerDigits.
     */
    private int minimumIntegerDigits;

    /** Creates a new instance of PgCurrencyField */
    public SSCurrencyField() {
        this(new SSCurrencyFormatterFactory());
    }
    
    public SSCurrencyField(int precision, int decimals) {
        this(new SSCurrencyFormatterFactory(precision, decimals));
    }
            
    
    public SSCurrencyField(javax.swing.JFormattedTextField.AbstractFormatterFactory factory) {
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
        this.setFormatterFactory(new SSCurrencyFormatterFactory(precision, decimals));
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
