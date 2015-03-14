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
import org.jolokia.extra.addon.scripting.consts.VocabularyConsts;
import org.jolokia.extra.addon.scripting.model.Conditional;
import org.jolokia.extra.addon.scripting.eval.Expression;
import org.jolokia.extra.addon.scripting.eval.ExpressionEvaluator;
import org.jolokia.extra.addon.scripting.exception.ValidationException;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * Factory to allow the creation of a Conditional from an JSON Objects.
 */
public class ConditionalFactory {
    private ExpressionEvaluator expressionEvaluator;
    private CollectionItemFactory collectionItemFactory;

    public ConditionalFactory(CollectionItemFactory collectionItemFactory, ExpressionEvaluator expressionEvaluator)  {
        this.expressionEvaluator = expressionEvaluator;
        this.collectionItemFactory = collectionItemFactory;
    }

    public Conditional createConditionalFromJson(Object rawConditional) throws ValidationException {
        try {
            if(rawConditional instanceof JSONObject) {
                JSONObject jsonConditional = (JSONObject) rawConditional;
                String check = jsonConditional.get(VocabularyConsts.CHECK_KEY) != null ? String.valueOf(jsonConditional.get(VocabularyConsts.CHECK_KEY)) : null;
                String description = jsonConditional.get(VocabularyConsts.DESCRIPTION_KEY) != null ? String.valueOf(jsonConditional.get(VocabularyConsts.DESCRIPTION_KEY)) : null;
                JSONAware conditionalItems = jsonConditional.get(ScriptStructureConsts.COLLECTION_ITEMS) != null ? (JSONAware) jsonConditional.get(ScriptStructureConsts.COLLECTION_ITEMS) : null;

                Expression checkExpression = check != null ? expressionEvaluator.createExpression(check) : null;
                return new Conditional(checkExpression, description, collectionItemFactory.createCollectionItemsFromJson(conditionalItems));
            }
            throw new ValidationException("Conditional is of wrong type. Expected a JSONObject, but it is of type: " + rawConditional.getClass());
        } catch (Exception e) {
            throw new ValidationException("Failed to create Conditional model from JSON: " + rawConditional, e);
        }
    }
}
