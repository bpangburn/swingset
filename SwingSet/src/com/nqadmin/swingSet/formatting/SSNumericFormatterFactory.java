/*
 * SSNumericFormatterFactory.java
 *
 */

package com.nqadmin.swingSet.testing;

import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;
import java.util.Locale;

import java.io.Serializable;
import java.text.ParseException;
import javax.swing.JFormattedTextField;


/**
 *
 * @author dags
 */
public class SSNumericFormatterFactory extends javax.swing.text.DefaultFormatterFactory implements Serializable {

    public SSNumericFormatterFactory() {
        this.setDefaultFormatter(new NumberFormatter(NumberFormat.getInstance()));
        this.setNullFormatter(null);
        this.setEditFormatter(new NumberFormatter(NumberFormat.getInstance(Locale.US)));
        this.setDisplayFormatter(new NumberFormatter(NumberFormat.getInstance()));
    }
    
    public SSNumericFormatterFactory(int precision, int decimals) {
        NumberFormat nfd = NumberFormat.getInstance(Locale.US);
        nfd.setMaximumFractionDigits(decimals);
        nfd.setMinimumFractionDigits(decimals);
        
        nfd.setMaximumIntegerDigits(precision);
        nfd.setMinimumIntegerDigits(1);
        
        this.setDefaultFormatter(new NumberFormatter(NumberFormat.getInstance()));
        this.setNullFormatter(null);
        this.setEditFormatter(new NumberFormatter(NumberFormat.getInstance(Locale.US)));
        this.setDisplayFormatter(new NumberFormatter(nfd));

    }
}