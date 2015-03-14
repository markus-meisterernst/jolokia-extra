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
package org.jolokia.extra.addon.scripting.model;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import java.util.*;

import static org.jolokia.extra.addon.scripting.model.ModelAssertions.assertNotEmpty;

/**
 * Model object to allow for explicit Meta Data definition within a Script.
 *
 * The Meta Data is added to the Context using key "meta" during Expression Evaluation.
 *
 * @see org.jolokia.extra.addon.scripting.eval.Context
 * @see org.jolokia.extra.addon.scripting.eval.ExpressionEvaluator
 */
public class Meta extends ScriptingModelItem implements Map<String, String>, ModelValidation  {
    public static final String AUTHOR_KEY = "author";
    public static final String DESCRIPTION_KEY = "description";
    public static final String LONG_DESCRIPTION_KEY = "longDescription";
    public static final String SUPPORT_LINK_KEY = "supportLink";
    public static final String[] mandatoryProperties = {DESCRIPTION_KEY, AUTHOR_KEY};

    private Map<String, String> metaMap = new TreeMap<String, String>();

    public Meta() {}

    public Meta(Map<String, String> metaMap) {
        if(metaMap != null) {
            this.metaMap.putAll(metaMap);
        }
    }

    public int size() {
        return metaMap.size();
    }

    public boolean isEmpty() {
        return metaMap.isEmpty();
    }

    public boolean containsKey(Object o) {
        return metaMap.containsKey(o);
    }

    public boolean containsValue(Object o) {
        return metaMap.containsValue(o);
    }

    public String get(Object o) {
        return metaMap.get(o);
    }

    public String put(String s, String s2) {
        return metaMap.put(s, s2);
    }

    public String remove(Object o) {
        return metaMap.remove(o);
    }

    public void putAll(Map<? extends String, ? extends String> map) {
        metaMap.putAll(map);
    }

    public void clear() {
        metaMap.clear();
    }

    public Set<String> keySet() {
        return metaMap.keySet();
    }

    public Collection<String> values() {
        return metaMap.values();
    }

    public Set<Entry<String, String>> entrySet() {
        return metaMap.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return metaMap.equals(o);
    }

    @Override
    public int hashCode() {
        return metaMap.hashCode();
    }

    public void validate() {
        ModelAssertions.assertNotEmpty(metaMap != null ? metaMap.entrySet() : new HashSet(), "The property 'metaMap' is not allowed to be null or empty.");
        for (String propertyKey : mandatoryProperties) {
            ModelAssertions.assertNotEmpty(metaMap.get(propertyKey), "The property '" + propertyKey + "' is mandatory but not provided.");
        }
    }

    @Override
    public JSONAware toJSON() {
        JSONObject jsonMap = new JSONObject();
        jsonMap.putAll(metaMap);
        return jsonMap;
    }
}
