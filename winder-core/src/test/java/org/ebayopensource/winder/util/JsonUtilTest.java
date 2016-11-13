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