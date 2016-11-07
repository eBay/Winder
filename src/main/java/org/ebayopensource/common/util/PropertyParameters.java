package org.ebayopensource.common.util;

import java.util.*;

/**
 * @author Sheldon Shao xshao@ebay.com on 10/16/16.
 * @version 1.0
 */
public class PropertyParameters extends AbstractParameters<Object> {

    protected Properties properties;

    public PropertyParameters(Properties properties) {
        this.properties = properties;
    }

    public String get(Object key) {
        return properties.getProperty(String.valueOf(key));
    }

    public String put(String key, String value) {
        Object old = properties.setProperty(key, value);
        return old != null ? old.toString() : null;
    }

    public Set<String> keySet() {
        return properties.stringPropertyNames();
    }

    public Collection<Object> values() {
        return properties.values();
    }

    public void clear() {
        properties.clear();
    }

    public int size() {
        return properties.size();
    }

    public boolean containsKey(Object key) {
        return properties.containsKey(key);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        HashSet<Entry<String, Object>> newSet = new HashSet<>(size());
        Set<Entry<Object, Object>>  entries = properties.entrySet();
        for(final Entry<Object, Object> entry:entries) {
            newSet.add(new Entry<String, Object>() {
                public String getKey() {
                    return (String)entry.getKey();
                }

                /**
                 * Returns the value corresponding to this entry.  If the mapping
                 * has been removed from the backing map (by the iterator's
                 * <tt>remove</tt> operation), the results of this call are undefined.
                 *
                 * @return the value corresponding to this entry
                 * @throws IllegalStateException implementations may, but are not
                 *         required to, throw this exception if the entry has been
                 *         removed from the backing map.
                 */
                public Object getValue() {
                    return entry.getValue();
                }

                @Override
                public Object setValue(Object value) {
                    return entry.setValue(value);
                }
            });
        }
        return newSet;
    }
}
