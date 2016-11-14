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
package org.ebayopensource.winder.util;

import org.ebayopensource.common.util.Parameters;
import org.ebayopensource.common.util.ParametersMap;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test case for JsonUtil
 *
 * @author Sheldon Shao xshao@ebay.com on 11/7/16.
 * @version 1.0
 */
public class JsonUtilTest {


    @Test
    public void jsonToMap() throws Exception {
        Map map = JsonUtil.jsonToMap("{}");
        assertEquals(map.size(), 0);
    }

    @Test
    public void jsonToParameters() throws Exception {
        Parameters map = JsonUtil.jsonToParameters("{}");
        assertEquals(map.size(), 0);
        Parameters map2 = JsonUtil.jsonToParameters("{\"name\":\"value\"}");
        assertEquals(map2.size(), 1);
        assertEquals(map2.getString("name"), "value");
    }

    @Test
    public void mapToJson() throws Exception {
        Parameters parameters = new ParametersMap();
        parameters.put("name", "value");

        assertEquals("{\"name\":\"value\"}", JsonUtil.writeValueAsString(parameters));
    }

    @Test
    public void jsonToParameters1() throws Exception {
        String str = "{\"parameters\":{\"name\":\"value\",\"parameters\":{\"parameters\":{\"name\":\"value\"},\"test\":2}},\"test\":2}";

        Parameters map = JsonUtil.jsonToParameters(str);
        assertNotNull(map);
        assertNotNull(map.getParameters("parameters"));
        assertNotNull(map.getParameters("parameters").getParameters("parameters"));
        assertNotNull(map.getParameters("parameters").getParameters("parameters").getParameters("parameters"));
    }
}