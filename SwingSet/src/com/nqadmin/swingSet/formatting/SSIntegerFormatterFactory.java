/*
 * SSIntegerFormatterFactory.java
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
public class SSIntegerFormatterFactory extends javax.swing.text.DefaultFormatterFactory implements Serializable {

    public SSIntegerFormatterFactory() {
        this.setDefaultFormatter(new NumberFormatter(NumberFormat.getIntegerInstance()));
        this.setNullFormatter(null);
        this.setEditFormatter(new NumberFormatter(NumberFormat.getIntegerInstance(Locale.US)));
        this.setDisplayFormatter(new NumberFormatter(NumberFormat.getIntegerInstance()));
    }
    
    public SSIntegerFormatterFactory(int precision) {
        NumberFormat nfd = NumberFormat.getIntegerInstance();
        
        nfd.setMaximumIntegerDigits(precision);
        nfd.setMinimumIntegerDigits(1);
        
        this.setDefaultFormatter(new NumberFormatter(NumberFormat.getIntegerInstance()));
        this.setNullFormatter(null);
        this.setEditFormatter(new NumberFormatter(NumberFormat.getIntegerInstance(Locale.US)));
        this.setDisplayFormatter(new NumberFormatter(nfd));

    }
}

