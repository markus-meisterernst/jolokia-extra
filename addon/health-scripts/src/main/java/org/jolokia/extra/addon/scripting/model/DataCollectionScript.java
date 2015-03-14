package org.jolokia.extra.addon.scripting.model;

import org.jolokia.extra.addon.scripting.consts.ScriptStructureConsts;
import org.jolokia.extra.addon.scripting.eval.Context;
import org.jolokia.extra.addon.scripting.exception.ValidationException;
import org.json.simple.JSONObject;

import static org.jolokia.extra.addon.scripting.model.ModelAssertions.assertNotEmpty;

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
public class DataCollectionScript extends ScriptingModelItem implements ModelValidation {
    private ScriptId scriptId;
    private Meta meta;
    private Variables variables;
    private CollectionItems collectionItems;
    private Conditional conditional;

    public DataCollectionScript() {}

    public DataCollectionScript(ScriptId scriptId, Meta meta, Variables variables, CollectionItems collectionItems) {
        this(scriptId, meta, variables, collectionItems, null);
    }

    public DataCollectionScript(ScriptId scriptId, Meta meta, Variables variables, CollectionItems collectionItems, Conditional conditional) {
        this.scriptId = scriptId;
        this.meta = meta;
        this.variables = variables;
        this.collectionItems = collectionItems;
        this.conditional = conditional;
    }

    public ScriptId getScriptId() {
        return scriptId;
    }

    public Meta getMeta() {
        return meta;
    }

    public Variables getVariables() {
        return variables;
    }

    public CollectionItems getCollectionItems() {
        return collectionItems;
    }

    public Conditional getConditional() {
        return conditional;
    }

    public void validate() throws ValidationException {
        ModelAssertions.assertNotNull(scriptId, "The property 'scriptId' is not allowed to be null.");
        ModelAssertions.assertNotNull(meta, "The property 'meta' is not allowed to be null.");
        ModelAssertions.assertNotNull(collectionItems, "The property 'collectionItems' is not allowed to be null.");
        meta.validate();
        collectionItems.validate();
        if(variables != null) {
            variables.validate();
        }
        if(conditional != null) {
            conditional.validate();
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonScript = new JSONObject();
        jsonScript.put(ScriptStructureConsts.SCRIPT_ID, scriptId.toJSON());
        jsonScript.put(ScriptStructureConsts.META, meta.toJSON());
        jsonScript.put(ScriptStructureConsts.VARS, variables.toJSON());
        jsonScript.put(ScriptStructureConsts.COLLECTION_ITEMS, collectionItems.toJSON());
        if(conditional != null) {
            jsonScript.put(ScriptStructureConsts.CONDITIONAL, conditional.toJSON());
        }
        return jsonScript;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataCollectionScript)) return false;

        DataCollectionScript that = (DataCollectionScript) o;

        if (!scriptId.equals(that.scriptId)) return false;
        if (collectionItems != null ? !collectionItems.equals(that.collectionItems) : that.collectionItems != null)
            return false;
        if (meta != null ? !meta.equals(that.meta) : that.meta != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = scriptId.hashCode();
        result = 31 * result + (meta != null ? meta.hashCode() : 0);
        result = 31 * result + (collectionItems != null ? collectionItems.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JolokiaDataCollection{" +
                "canonicalName='" + scriptId + '\'' +
                ", meta=" + meta +
                ", variables=" + variables +
                ", collectionItems=" + collectionItems +
                ", conditional=" + conditional +
                '}';
    }
}
