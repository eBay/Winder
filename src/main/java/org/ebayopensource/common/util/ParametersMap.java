package org.ebayopensource.common.util;

import java.util.*;

/**
 * Parameters based on HashMap
 *
 * Created by xshao on 9/16/16.
 */
public class ParametersMap<V> extends AbstractParameters<V> {

    protected Map<String, V> map;

    public ParametersMap() {
        map = new HashMap<>();
    }

    public ParametersMap(Map<String, V> map) {
        this.map = map;
    }

    public V get(Object key) {
        return map.get(key);
    }

    public V put(String key, V value) {
        return map.put(key, value);
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Collection<V> values() {
        return map.values();
    }

    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return map.entrySet();
    }

}

