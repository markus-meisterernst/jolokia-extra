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

package org.jolokia.extra.addon.scripting.execution;

import org.jolokia.extra.addon.scripting.consts.*;
import org.jolokia.extra.addon.scripting.model.*;
import org.jolokia.extra.addon.scripting.backend.IBackendManager;
import org.jolokia.extra.addon.scripting.eval.*;
import org.jolokia.util.LogHandler;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Standard Coordinator for the actual Script Execution.
 */
public class StandardScriptExecutionCoordinator implements ScriptExecutionCoordinator {
    private IBackendManager backendManager;
    private ExpressionEvaluator expressionEvaluator;
    private BooleanExpressionEvaluator booleanExpressionEvaluator;
    private LogHandler logHandler;

    private StandardScriptExecutionCoordinator() {}
    public StandardScriptExecutionCoordinator(IBackendManager backendManager, LogHandler logHandler, ExpressionEvaluator generalExpressionEvaluator, BooleanExpressionEvaluator booleanExpressionEvaluator) {
        this.backendManager = backendManager;
        this.logHandler = logHandler;
        this.expressionEvaluator = generalExpressionEvaluator;
        this.booleanExpressionEvaluator = booleanExpressionEvaluator;
    }

    public JSONObject executeScript(DataCollectionScript script, Context context, Context varOverridingContext) {
        Context effectiveScriptContext = new Context();
        effectiveScriptContext.put(ScriptStructureConsts.SCRIPT_ID, script.getScriptId());
        mergeMetaIntoContext(script.getMeta(), effectiveScriptContext);
        mergeVarsIntoContext(script.getVariables(), effectiveScriptContext);
        effectiveScriptContext.putAll(context);
        filterContextOverridingVars(script.getVariables(), varOverridingContext);
        mergeOverridingVarsIntoEffectiveContext(varOverridingContext, effectiveScriptContext);


        if(conditionalMatches(script, logHandler, effectiveScriptContext)) {
            Map<String, Object> dataCollectionResultMap = executeJolokiaDataCollectionRequests(backendManager, logHandler, script.getScriptId(), script.getCollectionItems());
            if(script instanceof HealthCheckScript) {
                return executeAssertions((HealthCheckScript) script, logHandler, mergeDataCollectionResultsIntoContext(dataCollectionResultMap, effectiveScriptContext));
            }
            else {
                return new JSONObject(dataCollectionResultMap);
            }
        }
        else {
            if(backendManager.isDebug()) {
                logHandler.debug("Script with scriptId: " + script.getScriptId()
                        + " was filtered out, as its Conditional was not met: " + script.getConditional().getCheck().asString());
            }

            return null;
        }
    }

    Map<String, Object> executeJolokiaDataCollectionRequests(IBackendManager backendManager, LogHandler logHandler, ScriptId scriptId, CollectionItems collectionItems) {
        Map<String, Object> dataCollectionResultMap = new LinkedHashMap<String, Object>();
        for(CollectionItem dataCollectionItem : collectionItems) {
            JSONObject resultOfJolokiaCall;
            try {
                resultOfJolokiaCall= backendManager.handleRequest(dataCollectionItem.getRequest());
            } catch (Exception e) {
                logHandler.error("Call to script item: " + dataCollectionItem.toJSON() + " for script: "
                        + scriptId + " raised an exception: " + e.getMessage(), e);
                resultOfJolokiaCall = backendManager.convertExceptionToJson(e, dataCollectionItem.getRequest());
            }
            dataCollectionResultMap.put(dataCollectionItem.getVarName(), resultOfJolokiaCall.get(JolokiaConsts.JMX_REQUEST_RESULT_KEY));
        }
        return dataCollectionResultMap;
    }

    boolean conditionalMatches(DataCollectionScript script, LogHandler logHandler, Context effectiveContext) {
        Conditional conditional = script.getConditional();

        if(conditional == null) {
            return true;
        }
        Context newContext = new Context(effectiveContext);
        if(!conditional.getCollectionItems().isEmpty()) {
            Map<String, Object> dataCollectionResultMap = executeJolokiaDataCollectionRequests(backendManager, logHandler, script.getScriptId(), conditional.getCollectionItems());
            newContext.putAll(dataCollectionResultMap);
        }
        Expression booleanExpression =  conditional.getCheck();
        BooleanEvalResult result = evaluateBooleanExpression(newContext, booleanExpression);
        if(result.isFailure()) {
            logHandler.error("Conditional evaluation: "+ booleanExpression.asString() +" for Script: " + script.getScriptId() + " resulted in an Exception.", result.getException());
            return false;
        }

        return result.getValue();
    }

    JSONObject executeAssertions(HealthCheckScript script, LogHandler logHandler, Context effectiveContext) {
        JSONObject resultJSON = new JSONObject();
        Assertions assertions = script.getAssertions();
        for(Assertion assertion: assertions) {
            Expression booleanExpression =  assertion.getCheck();

            BooleanEvalResult result = evaluateBooleanExpression(effectiveContext, booleanExpression);
            if(result.isFailure()) {
                logHandler.error("Assertion evaluation: "+ booleanExpression.asString() +" for Script: " + script.getScriptId() + " resulted in an Exception.", result.getException());
            }

            resultJSON.put(assertion.getName(), mapAssertionResult(assertion, result, effectiveContext));
        }
        return resultJSON;
    }

    Object mapAssertionResult(Assertion assertion, BooleanEvalResult result, Context effectiveContext) {
        JSONObject resultJSON = new JSONObject();
        String status;

        if(result.isSuccessful() && result.getValue()) {
            status = ResponseTypeConsts.SUCCESS;
            resultJSON.put(ResponseTypeConsts.RESPONSE_STATUS_KEY, status);
        }
        else {
            if(result.getException() != null) {
                status = ResponseTypeConsts.EXCEPTION;
                resultJSON.put(ResponseTypeConsts.RESPONSE_STATUS_KEY, status);
            }
            else {
                status = ResponseTypeConsts.FAILED;
                resultJSON.put(ResponseTypeConsts.RESPONSE_STATUS_KEY, status);
            }

            resultJSON.put(VocabularyConsts.CHECK_KEY, assertion.getCheck().asString());



            if(assertion.getDescriptionExpression() != null) {
                Map<String, Object> currentAssertion = new HashMap<String, Object>();
                currentAssertion.put(VocabularyConsts.NAME_KEY, assertion.getName());
                currentAssertion.put(VocabularyConsts.CHECK_KEY, assertion.getCheck());
                currentAssertion.put(ResponseTypeConsts.RESPONSE_STATUS_KEY, status);
                currentAssertion.put(ResponseTypeConsts.RESPONSE_VALUE_KEY, result.getException() != null ? result.getException() : result.getValue());
                effectiveContext.put(ScriptStructureConsts.ASSERTION, currentAssertion);

                EvalResult descriptionEvalResult = expressionEvaluator.evaluate(assertion.getDescriptionExpression(), effectiveContext);
                if (descriptionEvalResult.isSuccessful()) {
                    resultJSON.put(VocabularyConsts.DESCRIPTION_KEY, String.valueOf(descriptionEvalResult.getValue()));
                } else {
                    resultJSON.put(VocabularyConsts.DESCRIPTION_KEY, "FAILURE in evaluating description ('" + assertion.getDescription() + "'): " + descriptionEvalResult.getException());
                }
            }
            else {
                resultJSON.put(VocabularyConsts.DESCRIPTION_KEY, assertion.getDescription());
            }
        }

        return resultJSON;
    }

    private void mergeMetaIntoContext(Meta meta, Context context) {
        context.put(ScriptStructureConsts.META, meta);
    }

    private void mergeVarsIntoContext(Variables variables, Context context) {
        context.putAll(variables);
    }

    private void filterContextOverridingVars(Variables variables, Context varOverridingContext) {
        varOverridingContext.keySet().retainAll(variables.keySet());
    }

    private void mergeOverridingVarsIntoEffectiveContext(Context varOverridingContext, Context effectiveScriptContext) {
        effectiveScriptContext.putAll(varOverridingContext);
    }

    private Context mergeDataCollectionResultsIntoContext(Map<String, Object> dataCollectionResultMap, Context context) {
        for(Map.Entry<String, Object> entry : dataCollectionResultMap.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }

        return context;
    }

    private BooleanEvalResult evaluateBooleanExpression(Context context, Expression booleanExpression) {
        return booleanExpressionEvaluator.evaluate(booleanExpression, context);
    }
}
