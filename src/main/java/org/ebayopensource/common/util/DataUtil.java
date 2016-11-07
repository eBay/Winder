package org.ebayopensource.common.util;


import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashSet;

/**
 * Data Util
 *
 * Created by xshao on 9/16/16.
 */
public class DataUtil {
    /**
     * Convert value to boolean
     *
     * @param value the object to convert from
     * @return the converted boolean
     */
    public static boolean getBoolean(Object value, boolean defValue)
    {
        return doGetBoolean(value, defValue);
    }

    public static Boolean doGetBoolean(Object value, Boolean defValue)
    {
        if (value == null) {
            return defValue;
        }

        boolean result;
        if (value instanceof Boolean) {
            result = ((Boolean)value);
        }
        else if (value instanceof String) {
            return convertToBoolean((String)value, defValue);
        }
        else if (value instanceof Number) {
            long l = ((Number)value).longValue();
            result = l != 0;
        }
        else {
            result = defValue;
        }
        return result;
    }


    private static final String[] VALID_TRUE
            = {"True", "true", "TRUE", "Yes", "yes", "YES",
            "On", "on", "ON", "T", "t", "Y", "y", "1"};

    private static final String[] VALID_FALSE
            = {"False", "false", "FALSE", "No", "no", "NO",
            "Off", "off", "OFF", "F", "f", "N", "n", "0"};

    private static final HashSet<String> TRUE_SET = new HashSet<String>(VALID_TRUE.length);
    private static final HashSet<String> FALSE_SET = new HashSet<String>(VALID_FALSE.length);

    static {
        Collections.addAll(TRUE_SET, VALID_TRUE);
        Collections.addAll(FALSE_SET, VALID_FALSE);
    }


    /**
     * String to Boolean
     *
     * @param value <pre>
     *                                                                     True   -->   TRUE         False  -->   FALSE
     *                                                                     true   -->   TRUE         false  -->   FALSE
     *                                                                     TRUE   -->   TRUE         FALSE  -->   FALSE
     *                                                                     Yes    -->   TRUE         No     -->   FALSE
     *                                                                     yes    -->   TRUE         no     -->   FALSE
     *                                                                     YES    -->   TRUE         NO     -->   FALSE
     *                                                                     On     -->   TRUE         Off    -->   FALSE
     *                                                                     on     -->   TRUE         off    -->   FALSE
     *                                                                     ON     -->   TRUE         OFF    -->   FALSE
     *                                                                     T      -->   TRUE         F      -->   FALSE
     *                                                                     t      -->   TRUE         f      -->   FALSE
     *                                                                     Y      -->   TRUE         N      -->   FALSE
     *                                                                     y      -->   TRUE         n      -->   FALSE
     *                                                                  </pre>
     *
     */
    public static boolean toBoolean(String value, boolean defValue)
    {
        return TRUE_SET.contains(value) || !FALSE_SET.contains(value) && defValue;
    }

    public static Boolean convertToBoolean(String value, Boolean defValue)
    {
        if (TRUE_SET.contains(value)) {
            return true;
        }
        else if (FALSE_SET.contains(value)) {
            return false;
        }
        else {
            return defValue;
        }
    }

    /**
     * Convert string to boolean
     *              <pre>
     *                                                                     True   -->   TRUE         False  -->   FALSE
     *                                                                     true   -->   TRUE         false  -->   FALSE
     *                                                                     TRUE   -->   TRUE         FALSE  -->   FALSE
     *                                                                     Yes    -->   TRUE         No     -->   FALSE
     *                                                                     yes    -->   TRUE         no     -->   FALSE
     *                                                                     YES    -->   TRUE         NO     -->   FALSE
     *                                                                     On     -->   TRUE         Off    -->   FALSE
     *                                                                     on     -->   TRUE         off    -->   FALSE
     *                                                                     ON     -->   TRUE         OFF    -->   FALSE
     *                                                                     T      -->   TRUE         F      -->   FALSE
     *                                                                     t      -->   TRUE         f      -->   FALSE
     *                                                                     Y      -->   TRUE         N      -->   FALSE
     *                                                                     y      -->   TRUE         n      -->   FALSE
     *                                                                  </pre>
     *
     */
    public static boolean toBoolean(String value)
    {
        return toBoolean(value, false);
    }


    public static int getInt(Object value, int defValue)
    {
        if (value == null) {
            return defValue;
        }

        int result;
        if (value instanceof Number) {
            result = ((Number)value).intValue();
        }
        else if (value instanceof String) {
            String str = (String)value;
            try {
                result = Integer.parseInt(str.trim(), 10);
            }
            catch (Exception ex) {
                result = defValue;
            }
        }
        else if (value instanceof Boolean) {
            Boolean b = (Boolean)value;
            return b ? 1 : 0;
        }
        else {
            result = defValue;
        }
        return result;
    }


    /**
     * Convert value to long
     *
     * @param value the object to convert from
     * @return the converted long
     */
    public static long getLong(Object value, long defValue)
    {
        if (value == null) {
            return defValue;
        }

        long result;
        if (value instanceof Number) {
            result = ((Number)value).longValue();
        }
        else if (value instanceof String) {
            String str = (String)value;
            try {
                result = Long.parseLong(str.trim(), 10);
            }
            catch (Exception ex) {
                result = defValue;
            }
        }
        else if (value instanceof Boolean) {
            Boolean b = (Boolean)value;
            return b ? 1l : 0l;
        }
        else {
            result = defValue;
        }

        return result;
    }

    /**
     * Convert value to double
     *
     * @param value the object to convert from
     * @return the converted double
     */
    public static double getDouble(Object value, double defValue)
    {
        if (value == null) {
            return defValue;
        }

        double result;
        if (value instanceof Number) {
            result = ((Number)value).doubleValue();
        }
        else if (value instanceof String) {
            String str = (String)value;
            try {
                result = Double.parseDouble(str.trim());
            }
            catch (Exception ex) {
                result = defValue;
            }
        }
        else if (value instanceof Boolean) {
            Boolean b = (Boolean)value;
            return b ? 1d : 0d;
        }
        else {
            result = defValue;
        }

        return result;
    }

    public static String getString(Object value, String defValue)
    {
        if (value == null) {
            return defValue;
        }

        String str;
        if (value instanceof String) {
            str = (String)value;
        }
        else if (value instanceof Number
                || value instanceof Boolean
                || value instanceof Character) {
            str = value.toString();
        }
        else {
            str = defValue;
        }
        return str;
    }

    public static String[] getStringArray(Object object, String[] defValue)
    {
        if (object == null) {
            return defValue;
        }

        String[] result;
        if (object instanceof String[]) {
            result = (String[])object;
        }
        else if (object instanceof String) {
            String str = (String)object;
            str = str.trim();

            result = StringUtils.split(str, ' ');
        }
        else if (object instanceof Object[]) {
            Object[] array = (Object[])object;
            result = new String[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = getString(array[i], null);
            }
        }
        else if (object.getClass().isArray()) {
            int len = Array.getLength(object);
            result = new String[len];
            for (int i = 0; i < len; i++) {
                result[i] = getString(Array.get(object, i), null);
            }
        }
        else if (object instanceof Number
                || object instanceof Boolean
                || object instanceof Character) {
            result = new String[]{object.toString()};
        }
        else {
            result = defValue;
        }
        return result;
    }

    public static int[] getIntArray(Object object, int[] defValue)
    {
        if (object == null) {
            return defValue;
        }

        int[] result;
        if (object instanceof int[]) {
            result = (int[])object;
        }
        else if (object instanceof Number) {
            result = new int[]{getInt(object, 0)};
        }
        else if (object instanceof Number[]) {
            Number[] array = (Number[])object;
            result = new int[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = array[i].intValue();
            }
        }
        else if (object.getClass().isArray()) {
            int len = Array.getLength(object);
            result = new int[len];
            for (int i = 0; i < len; i++) {
                result[i] = getInt(Array.get(object, i), 0);
            }
        }
        else if (object instanceof Boolean) {
            result = new int[]{getInt(object, 0)};
        }
        else {
            result = defValue;
        }
        return result;
    }
}

