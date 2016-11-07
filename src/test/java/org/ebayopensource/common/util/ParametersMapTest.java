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
