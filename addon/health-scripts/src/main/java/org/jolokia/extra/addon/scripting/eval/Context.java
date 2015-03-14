package org.jolokia.extra.addon.scripting.eval;

import org.jolokia.extra.addon.scripting.model.Conditional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
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
public class Context implements Map<String, Object> {
    private Map<String, Object> map = new HashMap<String, Object>();

    public Context() {}
    public Context(Map<String, Object> map) {
        this.map = map;
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(Object o) {
        return map.containsKey(o);
    }

    public boolean containsValue(Object o) {
        return map.containsValue(o);
    }

    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    public Object get(Object o) {
        return map.get(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Object put(String s, Object o) {
        return map.put(s, o);
    }

    public void putAll(Map<? extends String, ?> map) {
        this.map.putAll(map);
    }

    public Object remove(Object o) {
        return map.remove(o);
    }

    public int size() {
        return map.size();
    }

    public Collection<Object> values() {
        return map.values();
    }
}
