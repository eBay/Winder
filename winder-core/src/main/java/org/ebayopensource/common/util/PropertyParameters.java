/**
 * Copyright (c) 2016 eBay Software Foundation. All rights reserved.
 *
 * Licensed under the MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.ebayopensource.common.util;

import java.util.*;

/**
 * Parameters on Properties
 *
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

    @Override
    public Parameters<Object> getParameters(String key) {
        Object value = get(key);
        if (value instanceof Parameters) {
            return (Parameters)value;
        }
        else if (value instanceof Map) {
            return new ParametersMap<>((Map)value);
        }
        return null;
    }
}
