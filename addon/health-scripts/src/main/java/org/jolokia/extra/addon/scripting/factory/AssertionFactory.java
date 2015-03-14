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

import org.jolokia.extra.addon.scripting.consts.VocabularyConsts;
import org.jolokia.extra.addon.scripting.eval.Expression;
import org.jolokia.extra.addon.scripting.eval.ExpressionEvaluator;
import org.jolokia.extra.addon.scripting.exception.ValidationException;
import org.jolokia.extra.addon.scripting.model.Assertion;
import org.jolokia.extra.addon.scripting.model.Assertions;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * Factory to allow the creation of Assertions directly from JSON Objects.
 */
public class AssertionFactory {
    private ExpressionEvaluator expressionEvaluator;

    public AssertionFactory(ExpressionEvaluator expressionEvaluator)  {
        this.expressionEvaluator = expressionEvaluator;
    }

    public Assertions createAssertionsFromJson(JSONAware jsonAware) {
        Assertions assertions = new Assertions();
        if(jsonAware != null) {
            if(jsonAware instanceof JSONArray) {
                JSONArray jsonCollection = (JSONArray) jsonAware;
                for(Object assertion: jsonCollection) {
                    if(assertion instanceof JSONObject) {
                        assertions.add(createAssertionFromJson((JSONObject) assertion));
                    }
                    else {
                        throw new ValidationException("The Assertion " + assertion + " does not represent a JSONObject. Beware that nested JSONArrays aren't supported.");
                    }
                }
            }
            else if(jsonAware instanceof JSONObject) {
                assertions.add(createAssertionFromJson((JSONObject) jsonAware));
            }
            else {
                throw new ValidationException("The Assertion " + jsonAware + " is of unsupported type: " + jsonAware.getClass());
            }
        }
        return assertions;
    }

    public Assertion createAssertionFromJson(JSONObject jsonAssertion) throws ValidationException {
        try {
            String name = jsonAssertion.get(VocabularyConsts.NAME_KEY)  != null ? String.valueOf(jsonAssertion.get(VocabularyConsts.NAME_KEY)) : null;
            String check = jsonAssertion.get(VocabularyConsts.CHECK_KEY) != null ? String.valueOf(jsonAssertion.get(VocabularyConsts.CHECK_KEY)) : null;
            String description = jsonAssertion.get(VocabularyConsts.DESCRIPTION_KEY) != null ? String.valueOf(jsonAssertion.get(VocabularyConsts.DESCRIPTION_KEY)) : null;
            String descExpressionString = jsonAssertion.get(VocabularyConsts.DESCRIPTION_EXPRESSION_KEY) != null ? String.valueOf(jsonAssertion.get(VocabularyConsts.DESCRIPTION_EXPRESSION_KEY)) : null;

            Expression checkExpression = check != null ? expressionEvaluator.createExpression(check) : null;

            if(descExpressionString != null) {
                Expression descriptionExpression = expressionEvaluator.createExpression(descExpressionString);
                return new Assertion(name, checkExpression, descriptionExpression);
            }

            return new Assertion(name, checkExpression, description);
        } catch (Exception e) {
            throw new ValidationException("Failed to create Assertion instance from JSON: " + jsonAssertion, e);
        }
    }
}
