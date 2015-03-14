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

package org.jolokia.extra.addon.scripting.model;

import org.jolokia.extra.addon.scripting.consts.VocabularyConsts;
import org.jolokia.extra.addon.scripting.exception.ValidationException;
import org.json.simple.JSONObject;

import static org.jolokia.extra.addon.scripting.model.ModelAssertions.assertNotEmpty;

/**
 * Class to encapsulate the Identity of a DataCollectionScript and its subclass HealthCheckScript.
 *
 * The ScriptId is added to the Context using the key "scriptId" as part of a Expression Evaluation.
 *
 * @see org.jolokia.extra.addon.scripting.management.ScriptRegistry
 * @see org.jolokia.extra.addon.scripting.eval.Context
 * @see org.jolokia.extra.addon.scripting.eval.ExpressionEvaluator
 *
 */
public class ScriptId extends ScriptingModelItem implements Comparable<ScriptId> {
    public static final String LATEST_VERSION = "Z" + Integer.toString(Integer.MAX_VALUE);
    private String name;
    private String version;

    private ScriptId() {}

    public ScriptId(String canonicalName) {
        this(canonicalName, LATEST_VERSION);
    }

    public ScriptId(String canonicalName, String version) {
        this.name = canonicalName;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public int compareTo(ScriptId otherScriptId) {
        if (name.equals(otherScriptId.name) && version.equals(otherScriptId.version)) {
            return 0;
        } else if (name.equals(otherScriptId.name)) {
            return otherScriptId.version.compareTo(version); // highest Version first
        } else {
            return name.compareTo(otherScriptId.name);
        }
    }

    @Override
    public void validate() throws ValidationException {
        ModelAssertions.assertNotEmpty(name, "The property 'name' is not allowed to be null or empty.");
        ModelAssertions.assertNotEmpty(version, "The property 'version' is not allowed to be null or empty.");
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(VocabularyConsts.NAME_KEY, name);
        jsonObject.put(VocabularyConsts.VERSION_KEY, version);
        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScriptId)) return false;

        ScriptId scriptId = (ScriptId) o;

        if (!name.equals(scriptId.name)) return false;
        if (version != null ? !version.equals(scriptId.version) : scriptId.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ScriptId{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
