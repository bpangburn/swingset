/*
 * BaseFormatterFactory.java
 *
 * Created on 6 de diciembre de 2004, 19:38
 */

package com.nqadmin.swingSet.testing;

import javax.swing.text.MaskFormatter;
import java.util.Locale;

import java.io.Serializable;
import java.text.ParseException;

/**
 *
 * @author dags
 */
public class SSNFormatterFactory extends javax.swing.text.DefaultFormatterFactory implements Serializable {

    private MaskFormatter defaultFormatter;
    private MaskFormatter displayFormatter;
    private MaskFormatter editFormatter;
    private MaskFormatter nullFormatter;

    public SSNFormatterFactory() {
        
        try {
            defaultFormatter = new MaskFormatter("###-##-####");
            nullFormatter    = null;
            editFormatter    = new MaskFormatter("###-##-####");
            displayFormatter = new MaskFormatter("###-##-####");
            
            editFormatter.setPlaceholderCharacter('0');
                    
            this.setDefaultFormatter(defaultFormatter);
            this.setNullFormatter(nullFormatter);
            this.setEditFormatter(editFormatter);
            this.setDisplayFormatter(displayFormatter);
        }
        catch (java.text.ParseException pe) {
            
        }
    }
}

