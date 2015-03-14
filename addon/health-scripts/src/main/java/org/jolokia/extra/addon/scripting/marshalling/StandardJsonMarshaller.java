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

package org.jolokia.extra.addon.scripting.marshalling;

import org.jolokia.extra.addon.scripting.consts.ScriptStructureConsts;
import org.jolokia.extra.addon.scripting.exception.ValidationException;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple JSON Marshaller implementation.
 */
public class StandardJsonMarshaller implements JsonMarshaller {
    private JSONParser jsonParser = new JSONParser();

    public JSONAware unmarshal(String jsonAsString) {
        try {
            Object unmarshalled = jsonParser.parse(jsonAsString);
            if(unmarshalled instanceof JSONAware) {
                return (JSONAware) unmarshalled;
            }

            throw new ValidationException("The Unmarshalled script is supposed to be of type JSONAware, however, it's actual type is " + (unmarshalled != null ? unmarshalled.getClass(): "null"));
        } catch (ParseException e) {
            throw new ValidationException(e);
        }
    }

    public JSONObject unmarshalJSONObject(String jsonAsString) {
        JSONAware json = unmarshal(jsonAsString);
        if(json instanceof JSONObject) {
            return (JSONObject) json;
        }

        throw new ValidationException("The Unmarshalled String is supposed to be of type JSONObject, however, it's actual type is " + (json != null ? json.getClass(): "null"));
    }

    /**
     * Marshaller for Scripts that keeps the ordering intact: scriptId, meta, vars, collectionItems, assertions, conditional.
     *
     * @param jsonScript
     * @return JSON Script as a String
     */
    public String marshalScript(JSONObject jsonScript) {
        Map<String, Object> orderedScriptMap = new LinkedHashMap<String, Object>();
        orderedScriptMap.put(ScriptStructureConsts.SCRIPT_ID, jsonScript.get(ScriptStructureConsts.SCRIPT_ID));
        orderedScriptMap.put(ScriptStructureConsts.META, jsonScript.get(ScriptStructureConsts.META));
        orderedScriptMap.put(ScriptStructureConsts.VARS, jsonScript.get(ScriptStructureConsts.VARS));
        orderedScriptMap.put(ScriptStructureConsts.COLLECTION_ITEMS, jsonScript.get(ScriptStructureConsts.COLLECTION_ITEMS));
        if(jsonScript.containsKey(ScriptStructureConsts.ASSERTIONS)) {
            orderedScriptMap.put(ScriptStructureConsts.ASSERTIONS, jsonScript.get(ScriptStructureConsts.ASSERTIONS));
        }
        if(jsonScript.containsKey(ScriptStructureConsts.SCRIPT_ID)) {
            orderedScriptMap.put(ScriptStructureConsts.SCRIPT_ID, jsonScript.get(ScriptStructureConsts.SCRIPT_ID));
        }

        return JSONObject.toJSONString(orderedScriptMap);
    }

    public String marshal(JSONAware jsonAware) {
        return jsonAware.toJSONString();
    }
}
