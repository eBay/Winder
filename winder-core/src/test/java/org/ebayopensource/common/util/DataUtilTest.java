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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by xshao on 9/16/16.
 */
public class DataUtilTest {

    @Test
    public void getBoolean() throws Exception {
        new DataUtil();

        assertEquals(DataUtil.getBoolean(null, true), true);
        assertEquals(DataUtil.getBoolean(Boolean.TRUE, false), true);
        assertEquals(DataUtil.getBoolean(Boolean.TRUE, false), true);
        assertEquals(DataUtil.getBoolean("True", false), true);
        assertEquals(DataUtil.getBoolean("T", false), true);
        assertEquals(DataUtil.getBoolean("F", true), false);
        assertEquals(DataUtil.getBoolean("DDDDD", true), true);
        assertEquals(DataUtil.getBoolean("DDDDD", false), false);
        assertEquals(DataUtil.getBoolean(1, false), true);
        assertEquals(DataUtil.getBoolean(0, false), false);
        assertEquals(DataUtil.getBoolean(new Object(), false), false);
        assertEquals(DataUtil.getBoolean(new Object(), true), true);
    }

    @Test
    public void toBoolean() throws Exception {
        assertEquals(DataUtil.toBoolean("toBoolean"), false);
        assertEquals(DataUtil.toBoolean("t"), true);
    }

    @Test
    public void toBoolean1() throws Exception {
        assertEquals(DataUtil.toBoolean("toBoolean", false), false);
        assertEquals(DataUtil.toBoolean("toBoolean", true), true);
        assertEquals(DataUtil.convertToBoolean("toBoolean", null), null);
        assertEquals(DataUtil.convertToBoolean("toBoolean", false), false);
        assertEquals(DataUtil.convertToBoolean("toBoolean", true), true);
        assertEquals(DataUtil.toBoolean("t"), true);
    }

    @Test
    public void getInt() throws Exception {
        assertEquals(DataUtil.getInt(null, 8), 8);
        assertEquals(DataUtil.getInt(Boolean.TRUE, 8), 1);
        assertEquals(DataUtil.getInt(Boolean.FALSE, 9), 0);
        assertEquals(DataUtil.getInt("True", 8), 8);
        assertEquals(DataUtil.getInt("99", 88), 99);
        assertEquals(DataUtil.getInt(" 88 ", 99), 88);
        assertEquals(DataUtil.getInt("DDDDD", 999), 999);
        assertEquals(DataUtil.getInt(new Object(), 333), 333);
        assertEquals(DataUtil.getInt(888l, 333), 888);
    }

    @Test
    public void getLong() throws Exception {
        assertEquals(DataUtil.getLong(null, 8), 8);
        assertEquals(DataUtil.getLong(Boolean.TRUE, 8), 1);
        assertEquals(DataUtil.getLong(Boolean.FALSE, 9), 0);
        assertEquals(DataUtil.getLong("True", 8), 8);
        assertEquals(DataUtil.getLong("99", 88), 99);
        assertEquals(DataUtil.getLong(" 88 ", 99), 88);
        assertEquals(DataUtil.getLong("DDDDD", 999), 999);
        assertEquals(DataUtil.getLong(new Object(), 333), 333);
        assertEquals(DataUtil.getLong(888l, 333), 888);
    }

    @Test
    public void getDouble() throws Exception {
        assertEquals(DataUtil.getDouble(null, 8), 8, 0.1);
        assertEquals(DataUtil.getDouble(Boolean.TRUE, 8), 1, 0.1);
        assertEquals(DataUtil.getDouble(Boolean.FALSE, 9), 0, 0.1);
        assertEquals(DataUtil.getDouble("True", 8), 8, 0.1);
        assertEquals(DataUtil.getDouble("99", 88), 99, 0.1);
        assertEquals(DataUtil.getDouble(" 88 ", 99), 88, 0.1);
        assertEquals(DataUtil.getDouble("DDDDD", 999), 999, 0.1);
        assertEquals(DataUtil.getDouble(new Object(), 333), 333, 0.1);
        assertEquals(DataUtil.getDouble(888l, 333), 888, 0.1);
    }

    @Test
    public void getString() throws Exception {
        assertEquals(DataUtil.getString(null, ""), "");
        assertEquals(DataUtil.getString(Boolean.TRUE, ""), "true");
        assertEquals(DataUtil.getString(Boolean.FALSE, ""), "false");
        assertEquals(DataUtil.getString("True", ""), "True");
        assertEquals(DataUtil.getString("99", ""), "99");
        assertEquals(DataUtil.getString(" 88 ", ""), " 88 ");
        assertEquals(DataUtil.getString("DDDDD", "   "), "DDDDD");
        assertEquals(DataUtil.getString(new Object(), ""), "");
        assertEquals(DataUtil.getString(888l, ""), "888");
        assertEquals(DataUtil.getString('a', ""), "a");
    }

    private static final String[] defaultStringArray = new String[0];

    @Test
    public void getStringArray() throws Exception {
        assertArrayEquals(DataUtil.getStringArray(null, defaultStringArray), defaultStringArray);
        assertArrayEquals(DataUtil.getStringArray(Boolean.TRUE, defaultStringArray), new String[] {"true"});
        assertArrayEquals(DataUtil.getStringArray(Boolean.FALSE, defaultStringArray), new String[] {"false"});
        assertArrayEquals(DataUtil.getStringArray("True", defaultStringArray), new String[] { "True" });
        assertArrayEquals(DataUtil.getStringArray("99", defaultStringArray), new String[] {"99"});
        assertArrayEquals(DataUtil.getStringArray(" 88 ", defaultStringArray), new String[] {"88"});
        assertArrayEquals(DataUtil.getStringArray(new String[] {"DDDDD"}, defaultStringArray), new String[] {"DDDDD"});
        assertArrayEquals(DataUtil.getStringArray(new Object(), defaultStringArray), defaultStringArray);
        assertArrayEquals(DataUtil.getStringArray(888l, defaultStringArray), new String[] {"888"});
        assertArrayEquals(DataUtil.getStringArray('a', defaultStringArray), new String[] {"a"});

        assertArrayEquals(DataUtil.getStringArray(new Object[] {'a', 123} , defaultStringArray), new String[] {"a", "123"});
        assertArrayEquals(DataUtil.getStringArray(new int[] {345, 123} , defaultStringArray), new String[] {"345", "123"});
    }

    private static final int[] defaultIntArray = new int[0];

    @Test
    public void getIntArray() throws Exception {
        assertArrayEquals(DataUtil.getIntArray(null, defaultIntArray), defaultIntArray);
        assertArrayEquals(DataUtil.getIntArray(Boolean.TRUE, defaultIntArray), new int[] {1});
        assertArrayEquals(DataUtil.getIntArray(Boolean.FALSE, defaultIntArray), new int[] {0});
        assertArrayEquals(DataUtil.getIntArray("True", defaultIntArray), defaultIntArray);
        assertArrayEquals(DataUtil.getIntArray(new Integer[] {777, 888}, defaultIntArray), new int[] { 777, 888});
        assertArrayEquals(DataUtil.getIntArray(new Object(), defaultIntArray), defaultIntArray);
        assertArrayEquals(DataUtil.getIntArray(888l, defaultIntArray), new int[] {888});
        assertArrayEquals(DataUtil.getIntArray(new int[] { 777, 999}, defaultIntArray), new int[] {777, 999});
        assertArrayEquals(DataUtil.getIntArray(new long[] { 777, 999}, defaultIntArray), new int[] {777, 999});
        assertArrayEquals(DataUtil.getIntArray('a', defaultIntArray), defaultIntArray);

        assertArrayEquals(DataUtil.getIntArray(new int[] {345, 123} , defaultIntArray), new int[] {345, 123});
    }
}