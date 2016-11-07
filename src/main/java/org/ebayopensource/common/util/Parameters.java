package org.ebayopensource.common.util;


import org.apache.commons.lang3.ArrayUtils;

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
}
