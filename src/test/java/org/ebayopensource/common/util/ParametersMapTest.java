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

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by xshao on 9/16/16.
 */
public class ParametersMapTest {

    @Test
    public void clear() throws Exception {
        ParametersMap parametersMap = new ParametersMap();
        HashMap<String, Object> map = new HashMap<>();
        ParametersMap<Object> parametersMap2 = new ParametersMap<Object>(map);

        assertEquals(map.get("test"), parametersMap.get("test"));
        assertEquals(map.put("test", "abc"), parametersMap.put("test", "abc"));
        parametersMap.keySet();
        parametersMap.values();
        parametersMap.entrySet();

        assertEquals(map.containsKey("test"), parametersMap2.containsKey("test"));
        assertEquals(map.size(), parametersMap2.size());
        assertFalse(parametersMap2.isEmpty());
        parametersMap2.clear();
        assertEquals(0, parametersMap2.size());
        assertTrue(parametersMap2.isEmpty());
    }
}
