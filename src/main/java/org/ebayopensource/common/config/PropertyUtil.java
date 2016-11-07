package org.ebayopensource.common.config;


import org.ebayopensource.common.util.DataUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Auto injection properties
 *
 * Decouple the dependency to Configuration
 *
 * Created by xshao on 10/1/16.
 */
public class PropertyUtil {


    public static class PropertyInjector {

        String name;

        Field field;

        Class type;

        int start;

        int end;

        String var;

        protected <V> String replaceName(Map<String, V> parameters) {
            String realName = name;
            if (var != null) {
                if (start > 0) {
                    realName = name.substring(0, start);
                }
                else {
                    realName = "";
                }
                String exp = (String)parameters.get(var);
                if (exp != null) {
                    realName += exp;
                    if (end+1< name.length()) {
                        realName += name.substring(end+1);
                    }
                }
                else {
                    realName = name;
                }
            }
            return realName;
        }

        public <V> Object getValue(Map<String, V> parameters) {
            String realName = replaceName(parameters);
            return convert(type, parameters.get(realName));
        }

        public <V> void inject(Object obj, Map<String, V> parameters) {
            Object value = getValue(parameters);
            if (value != null) {
                try {
                    field.setAccessible(true);
                    field.set(obj, value);
                } catch (Exception e) {
                }
            }
        }

        public void setName(String name) {
            start = name.indexOf("${");
            end = name.indexOf("}");
            if (start >= 0 && end >= start) {
                var = name.substring(start+2, end);
            }
            this.name = name;
        }
    }

    private static final ConcurrentHashMap<Class, List<PropertyInjector>> map = new ConcurrentHashMap<>();

    private static final int NULL_INT = Integer.MIN_VALUE + 1;
    private static final long NULL_LONG = Long.MIN_VALUE + 1;

    public static <V> Object convert(Class type, V value) {
        if (type == Integer.TYPE || type == Integer.class) {
            int v = DataUtil.getInt(value, NULL_INT);
            return v == NULL_INT ? null : v;
        }
        else if (type == Long.TYPE || type == Long.class) {
            long v = DataUtil.getLong(value, NULL_LONG);
            return v == NULL_LONG ? null : v;
        }
        else if (type == Boolean.TYPE || type == Boolean.class) {
            return DataUtil.doGetBoolean(value, null);
        }
        else {
            return DataUtil.getString(value, null);
        }
    }

    private static boolean isSupportedType(Class type) {
        return type == Integer.TYPE || type == Integer.class ||
                type == Long.TYPE || type == Long.class ||
                type == Boolean.TYPE || type == Boolean.class ||
                type == String.class;
    }

    private static List<PropertyInjector> getInjectors(Class clazz) {
        List<PropertyInjector> injectors = map.get(clazz);
        if (injectors == null) {
            synchronized (map) {
                injectors = doGetInjectors(clazz);
                map.put(clazz, injectors);
            }
        }
        return injectors;
    }

    private static List<PropertyInjector> doGetInjectors(Class clazz) {
        List<PropertyInjector> injectors = new ArrayList<>(2);
        doGetInjectors(clazz, injectors);
        return injectors.isEmpty() ? Collections.<PropertyInjector>emptyList() : injectors;
    }

    private static void doGetInjectors(Class clazz, List<PropertyInjector> injectors) {
        if (clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Class type = field.getType();
                InjectProperty anno = field.getAnnotation(InjectProperty.class);
                if (anno != null) {
                    if (isSupportedType(type)) {
                        PropertyInjector injector = new PropertyInjector();
                        injector.field = field;
                        injector.setName(anno.name());
                        injector.type = type;
                        injectors.add(injector);
                    }
                    else {
                        throw new IllegalStateException("Unsupported argument type, it should be [I/L/B/S]:" + type);
                    }
                }
            }

            Class superClass = clazz.getSuperclass();
            doGetInjectors(superClass, injectors);
        }
    }

    /**
     * Inject the properties from parameters
     *
     * @param obj
     * @param parameters
     */
    public static <V> void inject(Object obj, Map<String, V> parameters) {
        Class clazz = obj.getClass();
        List<PropertyInjector> injectors = getInjectors(clazz);
        if (!injectors.isEmpty()) {
            for(PropertyInjector injector: injectors) {
                injector.inject(obj, parameters);
            }
        }
    }
}
