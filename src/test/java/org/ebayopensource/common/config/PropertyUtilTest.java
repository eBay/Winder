package org.ebayopensource.common.config;


import org.ebayopensource.common.util.Parameters;
import org.ebayopensource.common.util.ParametersMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by xshao on 10/1/16.
 */
public class PropertyUtilTest {

    @Test
    public void convert() throws Exception {
        new PropertyUtil();

        PropertyUtil.PropertyInjector propertyInjector = new PropertyUtil.PropertyInjector();

        propertyInjector.setName("platform.${platform.primary}.trace.uri");

        Parameters<String> parameters = new ParametersMap<>();
        parameters.put("platform.primary", "raptor");
        parameters.put("platform.raptor.trace.uri", "raptor");
        parameters.put("platform.smoke.trace.uri", "smoke");

        assertEquals("platform.raptor.trace.uri", propertyInjector.replaceName(parameters));

        parameters.put("platform.primary", "smoke");
        assertEquals("platform.smoke.trace.uri", propertyInjector.replaceName(parameters));


        propertyInjector.setName("${platform.primary}");
        parameters.put("smoke", "Smoking");
        parameters.put("raptor", "Flying");
        assertEquals("smoke", propertyInjector.replaceName(parameters));

    }

    @Test
    public void inject() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("int1", 123);
        map.put("int2", new Object());

        map.put("long1", 123);
        map.put("long2", "");

        map.put("boolean1", true);
        map.put("boolean2", "");

        map.put("str1", "abc");
        map.put("str2", new Object());

        SimpleBean bean = new SimpleBean();
        PropertyUtil.inject(bean, map);

        assertEquals(bean.int1, 123);
        assertEquals(bean.int2, 2);
        assertEquals(bean.long1, 123);
        assertEquals(bean.long2, 2);

        assertEquals(bean.boolean1, true);
        assertEquals(bean.boolean2, true);

        assertEquals(bean.str1, "abc");
        assertEquals(bean.str2, "str2");

        PropertyUtil.inject(new NoInjection(), map);
        PropertyUtil.inject(new IllegalAccess(), map);

        try {
            PropertyUtil.inject(new UnsupportedType(), map);
            fail("Unsupported");
        }
        catch(Exception ex) {

        }

    }

    public static class UnsupportedType {

        @InjectProperty(name="test")
        Object obj;

    }

    public static class IllegalAccess {
        @InjectProperty(name="int1")
        int int1;
    }

    public static class NoInjection {
        private int int1;
        private int int2;
        private long long1;
        private long long2;
        private boolean boolean1;
        private boolean boolean2;
        private String str1;
        private String str2;
    }

    public static class SimpleBean {

        @InjectProperty(name="int1")
        private int int1;
        @InjectProperty(name="int2")
        private int int2 = 2;
        @InjectProperty(name="long1")
        private long long1;
        @InjectProperty(name="long2")
        private long long2 = 2;
        @InjectProperty(name="boolean1")
        private boolean boolean1;
        @InjectProperty(name="boolean2")
        private boolean boolean2 = true;
        @InjectProperty(name="str1")
        private String str1;
        @InjectProperty(name="str2")
        private String str2 = "str2";

    }
}
