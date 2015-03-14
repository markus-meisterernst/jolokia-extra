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

package org.jolokia.extra.addon.scripting.factory;

import org.jolokia.extra.addon.scripting.consts.ScriptStructureConsts;
import org.jolokia.extra.addon.scripting.eval.BooleanExpressionEvaluator;
import org.jolokia.extra.addon.scripting.exception.ValidationException;
import org.jolokia.extra.addon.scripting.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * Factory for creating DataCollectionScripts and HealthCheckScripts out of JSON.
 */
public class StandardScriptFactory implements ScriptFactory {
    private ScriptIdFactory scriptIdFactory = new ScriptIdFactory();
    private MetaFactory metaFactory = new MetaFactory();
    private VariableFactory variableFactory = new VariableFactory();
    private CollectionItemFactory collectionItemFactory = new CollectionItemFactory();
    private AssertionFactory assertionFactory;
    private ConditionalFactory conditionalFactory;

    public StandardScriptFactory(BooleanExpressionEvaluator booleanExpressionEvaluator) {
        assertionFactory =  new AssertionFactory(booleanExpressionEvaluator);
        conditionalFactory = new ConditionalFactory(collectionItemFactory, booleanExpressionEvaluator);
    }

    public DataCollectionScript createScriptFromJson(JSONAware jsonAware) throws ValidationException {
        if(jsonAware instanceof JSONObject) {
            JSONObject jsonScript = (JSONObject) jsonAware;
            DataCollectionScript script;

            ScriptId scriptId = null;
            Meta meta = null;
            Variables variables = null;
            CollectionItems collectionItems = null;
            Assertions assertions = null;
            Conditional conditional = null;


            if(jsonScript.containsKey(ScriptStructureConsts.SCRIPT_ID)) {
               scriptId = scriptIdFactory.createScriptIdFromJson(jsonScript.get(ScriptStructureConsts.SCRIPT_ID));
            }
            if(jsonScript.containsKey(ScriptStructureConsts.META)) {
                meta = metaFactory.createMetaFromJson(jsonScript.get(ScriptStructureConsts.META));
            }
            if(jsonScript.containsKey(ScriptStructureConsts.VARS)) {
                variables = variableFactory.createVariablesFromJson(jsonScript.get(ScriptStructureConsts.VARS));
            }
            if(jsonScript.containsKey(ScriptStructureConsts.COLLECTION_ITEMS)) {
                collectionItems = collectionItemFactory.createCollectionItemsFromJson(jsonScript.get(ScriptStructureConsts.COLLECTION_ITEMS));
            }
            if(jsonScript.containsKey(ScriptStructureConsts.CONDITIONAL)) {
                conditional = conditionalFactory.createConditionalFromJson(jsonScript.get(ScriptStructureConsts.CONDITIONAL));
            }

            boolean hasAssertions = false;
            Object assertionObject = null;
            if(jsonScript.containsKey(ScriptStructureConsts.ASSERTIONS)) {
                assertionObject = jsonScript.get(ScriptStructureConsts.ASSERTIONS);
                if(assertionObject instanceof JSONArray && !((JSONArray) assertionObject).isEmpty()) {
                    hasAssertions = true;
                }
                else if(assertionObject instanceof JSONObject && !((JSONObject) assertionObject).isEmpty()) {
                    hasAssertions = true;
                }
            }

            if(hasAssertions) {
                assertions = assertionFactory.createAssertionsFromJson((JSONAware) assertionObject);
                script = new HealthCheckScript(scriptId, meta, variables, collectionItems, assertions, conditional);
            }
            else {
                script = new DataCollectionScript(scriptId, meta, variables, collectionItems, conditional);
            }

            script.validate();

            return script;
        }

        throw new ValidationException("The Script is of wrong type. Expected a JSONObject, but it is of type: " + jsonAware.getClass());
    }
}
