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
}