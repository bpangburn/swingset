
package com.nqadmin.swingset.utils;

import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Class used to house anything one might want to store
 * in a central lookup which can affect anything within
 * the application. It can be thought of as a central context
 * where any application data may be stored and watched.
 * 
 * A singleton instance is created using @see getDefault().
 * This class is as thread safe as Lookup. Lookup appears to be safe.
 * @author Wade Chandler
 * @version 1.0
 */
@SuppressWarnings("serial")
public class CentralLookup extends AbstractLookup {
    private InstanceContent content = null;
    private static CentralLookup def = new CentralLookup();
    /**
     * Creates a CentralLookup instances with a specific content set.
     * @param content the 
InstanceContent
 to use
     */
    public CentralLookup(InstanceContent content) {
        super(content);
        this.content = content;
    }
    /**
     * Creates a new CentralLookup
     */
    public CentralLookup() {
        this(new InstanceContent());
    }
    /**
     * Adds an instance to the Lookup. The instance will be added with the classes
     * in its hierarchy as keys which may be used to lookup the instance(s).
     * @param instance The instance to add
     */
    public void add(Object instance) {
        content.add(instance);
    }
    /**
     * Removes the specific instance from the Lookup content.
     * @param instance The specific instance to remove.
     */
    public void remove(Object instance) {
        content.remove(instance);
    }
    /**
     * Returns the default CentralLookup. This can be used as an application context for
     * the entire application. If needed CentralLookup may be used directly through the
     * constructors to allow for more than one if needed. CentralLookup is nothing more
     * than an InstanceContent instance wrapped in a Lookup with the add and remove methods
     * added to make updating the data easier.
     * @return The default CentralLookup which is global in nature.
     */
    public static CentralLookup getDefault() {
        return def;
    }
}