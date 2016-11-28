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


import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract Task Parameters
 *
 * Created by xshao on 9/16/16.
 */
public abstract class AbstractParameters<V> extends AbstractMap<String, V>
        implements Parameters<V> {

    public String getString(String key) {
        return getString(key, DEFAULT_STRING);
    }

    public String getString(String key, String defValue) {
        return DataUtil.getString(get(key), defValue);
    }

    public String[] getStringArray(String key) {
        return getStringArray(key, DEFAULT_STRING_ARRAY);
    }

    public String[] getStringArray(String key, String[] defValue) {
        return DataUtil.getStringArray(get(key), defValue);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, DEFAULT_BOOLEAN);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return DataUtil.getBoolean(get(key), defValue);
    }

    public int getInt(String key) {
        return getInt(key, DEFAULT_INT);
    }

    public int getInt(String key, int defValue) {
        return DataUtil.getInt(get(key), defValue);
    }

    public int[] getIntArray(String key) {
        return getIntArray(key, DEFAULT_INT_ARRAY);
    }

    public int[] getIntArray(String key, int[] defValue) {
        return DataUtil.getIntArray(get(key), defValue);
    }

    public double getDouble(String key) {
        return getDouble(key, DEFAULT_DOUBLE);
    }

    public double getDouble(String key, double defValue) {
        return DataUtil.getDouble(get(key), defValue);
    }


    public long getLong(String key) {
        return getLong(key, DEFAULT_LONG);
    }

    public long getLong(String key, long defValue) {
        return DataUtil.getLong(get(key), defValue);
    }

    public V get(String key, V defValue) {
        V value = get(key);
        return value == null ? defValue : value;
    }

    public Object[] getArray(String key) {
        return getArray(key, null);
    }

    public Object[] getArray(String key, Object[] defValue) {
        Object obj = get(key);
        if (obj == null) {
            return defValue;
        }
        if (obj instanceof Object[]) {
            return (Object[])obj;
        }
        else {
            return new Object[]{obj};
        }
    }

    public static <V> Parameters<V> toParameters(Map value) {
        if (value instanceof Parameters) {
            return (Parameters)value;
        }
        else {
            return new ParametersMap<>(value);
        }
    }

    /**
     * Parsing the String value to List<String>.
     * The string will be split by ",".
     *
     * @param key Key
     * @return
     */
    public List<String> getList(String key) {
        Object value = get(key);
        return DataUtil.getStringList(value);
    }
}
