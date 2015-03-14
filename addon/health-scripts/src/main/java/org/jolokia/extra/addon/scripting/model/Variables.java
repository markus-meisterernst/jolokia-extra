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

import org.jolokia.extra.addon.scripting.consts.ScriptStructureConsts;
import org.jolokia.extra.addon.scripting.consts.VocabularyConsts;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import java.util.*;

import static org.jolokia.extra.addon.scripting.model.ModelAssertions.assertFalse;

/**
 * Model Class to allow the Definition of Variables and their (default-) values.
 *
 * Variables are added under their names to the Context during Expression Evaluation.
 *
 * @see org.jolokia.extra.addon.scripting.eval.Context
 * @see org.jolokia.extra.addon.scripting.eval.ExpressionEvaluator
 */
public class Variables extends ScriptingModelItem implements Map<String, Object> {
    private Map<String, Object>  variables = new TreeMap<String, Object>();

    public Variables() {}

    public Variables(Map<String, Object> variables) {
        if(variables != null) {
            this.variables.putAll(variables);
        }
    }

    public int size() {
        return variables.size();
    }

    public boolean isEmpty() {
        return variables.isEmpty();
    }

    public boolean containsKey(Object o) {
        return variables.containsKey(o);
    }

    public boolean containsValue(Object o) {
        return variables.containsValue(o);
    }

    public Object get(Object o) {
        return variables.get(o);
    }

    public Object put(String s, Object o) {
        return variables.put(s, o);
    }

    public Object remove(Object o) {
        return variables.remove(o);
    }

    public void putAll(Map<? extends String, ?> map) {
        variables.putAll(map);
    }

    public void clear() {
        variables.clear();
    }

    public Set<String> keySet() {
        return variables.keySet();
    }

    public Collection<Object> values() {
        return variables.values();
    }

    public Set<Entry<String, Object>> entrySet() {
        return variables.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return variables.equals(o);
    }

    @Override
    public int hashCode() {
        return variables.hashCode();
    }


    @Override
    public void validate() {
        assertFalse(variables.containsKey(ScriptStructureConsts.SCRIPT_ID), "The property 'scriptId' is not allowed in the 'vars' block.");
        assertFalse(variables.containsKey(ScriptStructureConsts.META), "The property 'meta' is not allowed in the 'vars' block.");
        assertFalse(variables.containsKey(ScriptStructureConsts.ASSERTION), "The property 'assertion' is not allowed in the 'vars' block.");
    }

    @Override
    public JSONAware toJSON() {
        JSONObject jsonVars = new JSONObject();
        jsonVars.putAll(variables);
        return jsonVars;
    }
}
