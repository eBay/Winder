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
public class AbstractParametersTest {

    ParametersMap parameters = new ParametersMap();

    @Test
    public void getString() throws Exception {
        assertEquals(parameters.getString("test"), Parameters.DEFAULT_STRING);
        assertEquals(parameters.getString("test", "abc"), "abc");
    }

    @Test
    public void getStringArray() throws Exception {
        assertArrayEquals(parameters.getStringArray("test"), Parameters.DEFAULT_STRING_ARRAY);
        assertArrayEquals(parameters.getStringArray("test", null), null);
    }

    @Test
    public void getBoolean() throws Exception {
        assertEquals(parameters.getBoolean("test"), false);
        assertEquals(parameters.getBoolean("test", true), true);
    }


    @Test
    public void getInt() throws Exception {
        assertEquals(parameters.getInt("test"), 0);
        assertEquals(parameters.getInt("test", 1), 1);
    }

    @Test
    public void getIntArray() throws Exception {
        assertArrayEquals(parameters.getIntArray("test"), Parameters.DEFAULT_INT_ARRAY);
        assertArrayEquals(parameters.getIntArray("test", null), null);
    }

    @Test
    public void getDouble() throws Exception {
        assertEquals(parameters.getDouble("test"), 0, 0.1);
        assertEquals(parameters.getDouble("test", 1), 1, 0.1);
    }


    @Test
    public void getLong() throws Exception {
        assertEquals(parameters.getLong("test"), 0);
        assertEquals(parameters.getLong("test", 1), 1);
    }

    @Test
    public void get() throws Exception {
        assertEquals(parameters.get("test"), null);
        assertEquals(parameters.get("test", 1), 1);
    }

    @Test
    public void getArray() throws Exception {
        assertArrayEquals(parameters.getArray("test"), null);
        assertArrayEquals(parameters.getArray("test", new String[] { "test" }), new String[] { "test" });

        ParametersMap map = new ParametersMap();
        map.put("test1", new Object[] {"abc", "bcd"});
        map.put("test2", "abc");

        assertArrayEquals(map.getArray("test1", new String[] { "test" }), new Object[] {"abc", "bcd"});
        assertArrayEquals(map.getArray("test2", new String[] { "test" }), new String[] { "abc" });
    }

    @Test
    public void getList() throws Exception {
        assertNull(parameters.getStringList("test"));

        ParametersMap map = new ParametersMap();
        map.put("test1", new Object[] {"abc", "bcd"});
        map.put("test2", "abc");

        assertArrayEquals(map.getStringList("test1").toArray(), new String[] {"abc", "bcd"});
        assertArrayEquals(map.getStringList("test2").toArray(), new String[] { "abc" });
    }
}