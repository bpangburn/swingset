/*
 * SSDateFormatterfactory.java
 *
 */


package com.nqadmin.swingSet.testing;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import java.io.Serializable;
import java.text.ParseException;


/**
 *
 * @author dags
 */
public class SSDateFormatterFactory extends javax.swing.text.DefaultFormatterFactory implements Serializable {

    public SSDateFormatterFactory() {
        this.setDefaultFormatter(new DateFormatter(new SimpleDateFormat("DD/mm/yyyy")));
        this.setNullFormatter(null);
        this.setEditFormatter(new DateFormatter(new SimpleDateFormat("ddMMyyyy")));
        this.setDisplayFormatter(new DateFormatter(new SimpleDateFormat("MMM dd, yyyy")));
    }
}

