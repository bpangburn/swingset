/*
 * SSCurrencyFormatterFactory.java
 *
 */

package com.nqadmin.swingSet.testing;

import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;
import java.util.Locale;

import java.io.Serializable;
import java.text.ParseException;

/**
 *
 * @author dags
 */
public class SSCurrencyFormatterFactory extends javax.swing.text.DefaultFormatterFactory implements Serializable {
    
    public SSCurrencyFormatterFactory() {
        this.setDefaultFormatter(new NumberFormatter(NumberFormat.getCurrencyInstance()));
        this.setNullFormatter(null);
        this.setEditFormatter(new NumberFormatter(NumberFormat.getInstance(Locale.US)));
        this.setEditFormatter(new NumberFormatter(NumberFormat.getCurrencyInstance()));
    }
    
    public SSCurrencyFormatterFactory(int precision, int decimals) {
        NumberFormat nfd = NumberFormat.getCurrencyInstance(Locale.US);
        nfd.setMaximumFractionDigits(decimals);
        nfd.setMinimumFractionDigits(decimals);
        
        nfd.setMaximumIntegerDigits(precision);
        nfd.setMinimumIntegerDigits(1);
        
        this.setDefaultFormatter(new NumberFormatter(NumberFormat.getCurrencyInstance()));
        this.setNullFormatter(null);
        this.setEditFormatter(new NumberFormatter(NumberFormat.getInstance(Locale.US)));
        this.setDisplayFormatter(new NumberFormatter(nfd));
    }
}