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


import java.lang.reflect.Array;
import java.util.*;

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
     * Parsing the String value to List&lt;String&gt;.
     * The string will be split by ",".
     *
     * @param key Key
     * @return A list type value
     */
    public List<String> getStringList(String key) {
        Object value = get(key);
        return DataUtil.getStringList(value);
    }

    /**
     * Convert value as List as possible
     *
     * @param key Key
     * @return A list type of value
     */
    @Override
    public <T> List<T> getList(String key) {
        Object obj = get(key);
        if (obj instanceof List) {
            List list = (List)obj;
            return (List<T>)list;
        }
        else if (obj instanceof Object[]) {
            Object[] array = (Object[])obj;
            List<T> list = new ArrayList<>(array.length);
            for (Object anArray : array) {
                list.add((T)anArray);
            }
            return list;
        }
        else if (obj.getClass().isArray()) {
            int len = Array.getLength(obj);
            List<T> list = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                list.add((T)Array.get(obj, i));
            }
            return list;
        }
        return null;
    }

    /**
     * Convert parameter as Date
     *
     * @param key Key
     * @return convert long or Date as Date
     */
    public Date getDate(String key) {
       return getDate(key, null);
    }

    /**
     * Convert parameter as Date
     *
     * @param key Key
     * @return convert long or Date as Date
     */
    public Date getDate(String key, Date defaultValue) {
        Object obj = get(key);
        if (obj instanceof Date) {
            return (Date)obj;
        }
        else if (obj instanceof Long) {
            return new Date((Long)obj);
        }
        return defaultValue;
    }

    /**
     * Convert string or int as Enum
     *
     * @param key Key
     * @return Convert string or int as Enum
     */
    public <T extends Enum> T getEnum(Class<T> clazz, String key) {
        return getEnum(clazz, key, null);
    }


    /**
     * Convert string or int as Enum
     *
     * @param key Key
     * @return Convert string or int as Enum
     */
    public <T extends Enum> T getEnum(Class<T> clazz, String key, T defaultValue) {
        Object obj = get(key);
        if (clazz.isInstance(obj)) {
            return (T) obj;
        }
        else if (obj instanceof String) {
            try {
                return (T)Enum.valueOf(clazz, ((String)obj));
            } catch (Exception ex) {
            }
        }
        else if (obj instanceof Number) {
            int v = ((Number)obj).intValue();
            T[] values = clazz.getEnumConstants();
            if (v >= 0 && v < values.length) {
                return values[v];
            }
        }
        return defaultValue;
    }

    public Map<String, V> toMap() {
        return this;
    }
}
