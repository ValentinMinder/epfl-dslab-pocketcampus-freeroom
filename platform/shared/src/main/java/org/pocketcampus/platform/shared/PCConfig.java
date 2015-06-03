package org.pocketcampus.platform.shared;

import java.util.Properties;

/**
 * Abstraction over configuration files.
 */
public class PCConfig extends Properties {
    public static final long serialVersionUID = 41216938188L;

    public String getString(final String k) {
        return (String) get((Object) k);
    }

    public Integer getInteger(final String k) {
        return (k != null ? Integer.parseInt((String) get((Object) k)) : null);
    }
}