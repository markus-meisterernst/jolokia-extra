/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jolokia.extra.addon.scripting.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Script Addon Configuration.
 */
public class ScriptConfiguration implements Map<String, Object> {
    private Map<String, Object> configMap = new HashMap<String, Object>();

    public int size() {
        return configMap.size();
    }

    public boolean isEmpty() {
        return configMap.isEmpty();
    }

    public boolean containsKey(Object o) {
        return configMap.containsKey(o);
    }

    public boolean containsValue(Object o) {
        return configMap.containsValue(o);
    }

    public Object get(Object o) {
        return configMap.get(o);
    }

    public Object put(String s, Object o) {
        return configMap.put(s, o);
    }

    public Object remove(Object o) {
        return configMap.remove(o);
    }

    public void putAll(Map<? extends String, ?> map) {
        configMap.putAll(map);
    }

    public void clear() {
        configMap.clear();
    }

    public Set<String> keySet() {
        return configMap.keySet();
    }

    public Collection<Object> values() {
        return configMap.values();
    }

    public Set<Entry<String, Object>> entrySet() {
        return configMap.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return configMap.equals(o);
    }

    @Override
    public int hashCode() {
        return configMap.hashCode();
    }
}
