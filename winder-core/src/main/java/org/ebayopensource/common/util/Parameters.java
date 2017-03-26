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


import org.apache.commons.lang3.ArrayUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Parameters to simplify the data accessing
 *
 * Created by xshao on 9/16/16.
 */
public interface Parameters<V> extends Map<String, V> {

    String DEFAULT_STRING = null;
    int DEFAULT_INT = 0;
    boolean DEFAULT_BOOLEAN = false;
    long DEFAULT_LONG = 0L;
    double DEFAULT_DOUBLE = 0.0d;

    String[] DEFAULT_STRING_ARRAY = ArrayUtils.EMPTY_STRING_ARRAY;

    int[] DEFAULT_INT_ARRAY = ArrayUtils.EMPTY_INT_ARRAY;

    String getString(String key);

    String getString(String key, String defValue);

    boolean getBoolean(String key);

    boolean getBoolean(String key, boolean defValue);

    int getInt(String key);

    int getInt(String key, int defValue);

    double getDouble(String key);

    double getDouble(String key, double defValue);

    long getLong(String key);

    long getLong(String key, long defValue);

    V get(String key, V defValue);

    int[] getIntArray(String key);

    int[] getIntArray(String key, int[] defValue);

    Object[] getArray(String key);

    Object[] getArray(String key, Object[] defValue);

    String[] getStringArray(String key);

    String[] getStringArray(String key, String[] defValue);

    /**
     * Return value as Parameters
     *
     * @param key Key
     * @return value as Parameters, if the value is map, the map will wrapped as a Parameters
     */
    Parameters<V> getParameters(String key);

    /**
     * Parsing the String value to List&lt;String&gt;.
     * The string will be split by ",".
     *
     * @param key Key
     * @return A list type of value
     */
    List<String> getStringList(String key);

    /**
     * Convert value as List as possible
     *
     * @param key Key
     * @return A list type of value
     */
    <T> List<T> getList(String key);

    /**
     * Convert parameter as Date
     *
     * @param key Key
     * @return convert long or Date as Date
     */
    Date getDate(String key);


    /**
     * Convert parameter as Date
     *
     * @param key Key
     * @return convert long or Date as Date
     */
    Date getDate(String key, Date defaultValue);

    /**
     * Convert string or int as Enum
     *
     * @param key Key
     * @return Convert string or int as Enum
     */
    <T extends Enum> T getEnum(Class<T> clazz, String key);

    /**
     * Convert string or int as Enum
     *
     * @param key Key
     * @return Convert string or int as Enum
     */
    <T extends Enum> T getEnum(Class<T> clazz, String key, T defaultValue);


    Map<String, V> toMap();
}
