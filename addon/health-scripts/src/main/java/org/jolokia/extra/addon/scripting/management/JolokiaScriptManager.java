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

package org.jolokia.extra.addon.scripting.management;

import org.jolokia.extra.addon.scripting.config.ScriptConfiguration;
import org.jolokia.extra.addon.scripting.eval.Context;
import org.jolokia.extra.addon.scripting.execution.ScriptExecutionCoordinator;
import org.jolokia.extra.addon.scripting.factory.ScriptFactory;
import org.jolokia.extra.addon.scripting.marshalling.JsonMarshaller;
import org.jolokia.extra.addon.scripting.model.DataCollectionScript;
import org.jolokia.extra.addon.scripting.model.ScriptId;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * JolokiaScriptManager as the key component for HC Script management and execution delegation.
 */
public class JolokiaScriptManager implements ScriptManager {
    private JsonMarshaller marshaller;
    private ScriptFactory scriptFactory;
    private ScriptRegistry scriptRegistry;
    private ScriptExecutionCoordinator scriptExecutionCoordinator;
    private ScriptConfiguration configuration;

    public JolokiaScriptManager() {}


    public void init() {
        scriptRegistry.init();
    }

    public void shutdown() {
        scriptRegistry.shutdown();
    }

    public JSONObject registerScript(String jolokiaCollectionScriptJsonAsString) {
        JSONAware jsonScript = marshaller.unmarshal(jolokiaCollectionScriptJsonAsString);

        DataCollectionScript dataCollectionScript = scriptFactory.createScriptFromJson(jsonScript);
        scriptRegistry.register(dataCollectionScript.getScriptId(), dataCollectionScript);

        return dataCollectionScript.getScriptId().toJSON();
    }

    public boolean unregisterScript(String canonicalName, String version) {
        return scriptRegistry.unregister(createScriptId(canonicalName, version));
    }

    public boolean containsScript(String canonicalName, String version) {
        return scriptRegistry.containsScript(createScriptId(canonicalName, version));
    }

    public JSONArray listScripts() {
        JSONArray array = new JSONArray();
        for(ScriptId scriptId : scriptRegistry.registeredIds()) {
            array.add(scriptId.toJSON());
        }
        return array;
    }

    public JSONObject scriptDefinition(String canonicalName, String version) {
        DataCollectionScript script = scriptRegistry.get(createScriptId(canonicalName, version));
        if(script != null) {
            return script.toJSON();
        }

        return null;
    }

    public JSONObject executeScript(String canonicalName, String version) {
        return executeScriptWithParams(canonicalName, version, null);
    }

    public JSONObject executeScriptWithParams(String canonicalName, String version, String parameterJSONAsString) {
        DataCollectionScript script = scriptRegistry.get(createScriptId(canonicalName, version));
        Context context = new Context(configuration);
        Context varOverridingContext = new Context();

        if(parameterJSONAsString != null && parameterJSONAsString.trim().length() > 0) {
            varOverridingContext.putAll(marshaller.unmarshalJSONObject(parameterJSONAsString));
        }

        JSONObject result = null;
        if(script != null) {
            result = scriptExecutionCoordinator.executeScript(script, context, varOverridingContext);
        }
        return result;
    }

    protected ScriptId createScriptId(String canonicalName, String version) {
        return new ScriptId(canonicalName, version);
    }

    public void setMarshaller(JsonMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setScriptFactory(ScriptFactory scriptFactory) {
        this.scriptFactory = scriptFactory;
    }

    public void setScriptRegistry(ScriptRegistry scriptRegistry) {
        this.scriptRegistry = scriptRegistry;
    }

    public void setConfiguration(ScriptConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setScriptExecutionCoordinator(ScriptExecutionCoordinator scriptExecutionCoordinator) {
        this.scriptExecutionCoordinator = scriptExecutionCoordinator;
    }
}
