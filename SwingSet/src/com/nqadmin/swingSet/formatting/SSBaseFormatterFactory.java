/*
 * SSBaseFormatterFactory.java
 *
  */

package com.nqadmin.swingSet.testing;

import javax.swing.text.MaskFormatter;
import java.io.Serializable;
import java.text.ParseException;


/**
 *
 * @author dags
 */
public class SSBaseFormatterFactory extends javax.swing.text.DefaultFormatterFactory implements Serializable {


    public SSBaseFormatterFactory() {
        
        try {
            this.setDefaultFormatter(new MaskFormatter("##-########-#"));
            this.setNullFormatter(null);
            this.setEditFormatter(new MaskFormatter("###########"));
            this.setDisplayFormatter(new MaskFormatter("##-########-#"));
        }
        catch (java.text.ParseException pe) {
            
        }
    }
}

