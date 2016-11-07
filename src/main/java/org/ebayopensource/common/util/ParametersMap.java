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
 * Parameters based on HashMap
 *
 * Created by xshao on 9/16/16.
 */
public class ParametersMap<V> extends AbstractParameters<V> {

    protected Map<String, V> map;

    public ParametersMap() {
        map = new HashMap<>();
    }

    public ParametersMap(Map<String, V> map) {
        this.map = map;
    }

    public V get(Object key) {
        return map.get(key);
    }

    public V put(String key, V value) {
        return map.put(key, value);
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Collection<V> values() {
        return map.values();
    }

    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Parameters<V> getParameters(String key) {
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

