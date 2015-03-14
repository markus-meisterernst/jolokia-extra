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
import org.jolokia.extra.addon.scripting.eval.Expression;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import static org.jolokia.extra.addon.scripting.model.ModelAssertions.assertNotEmpty;
import static org.jolokia.extra.addon.scripting.model.ModelAssertions.assertNotNull;

/**
 * Model class to allow for conditional Script execution based on some form of pre-processing during script execution.
 * This pre-processing is implemented in the same way as the actual script execution through the use of collectionItems.
 * The gathered information is then made available for Expression Evaluation.
 *
 * @see org.jolokia.extra.addon.scripting.model.CollectionItems
 * @see org.jolokia.extra.addon.scripting.eval.Expression
 */
public class Conditional extends ScriptingModelItem implements ModelValidation {
    private Expression check;
    private String description;
    private CollectionItems collectionItems;
    private Conditional() {}

    public Conditional(Expression check, String description, CollectionItems collectionItems) {
        this.check = check;
        this.collectionItems = new CollectionItems(collectionItems);
        this.description = description;
    }

    public Expression getCheck() {
        return check;
    }

    public String getDescription() {
        return description;
    }

    public CollectionItems getCollectionItems() {
        return collectionItems;
    }

    public void validate() {
        assertNotNull(check, "The property 'check' is not allowed to be null∆.");
        assertNotEmpty(collectionItems, "The property 'collectionItems' is not allowed to be null or empty.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conditional)) return false;

        Conditional that = (Conditional) o;

        if (!check.equals(that.check)) return false;
        if (!collectionItems.equals(that.collectionItems)) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = check.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + collectionItems.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Conditional{" +
                "check='" + check +
                "', description='" + description +
                "', collectionItems=" + collectionItems +
                '}';
    }

    @Override
    public JSONAware toJSON() {
        JSONObject jsonAssertion = new JSONObject();
        jsonAssertion.put(VocabularyConsts.CHECK_KEY, check.asString());
        jsonAssertion.put(VocabularyConsts.DESCRIPTION_KEY, description);
        jsonAssertion.put(ScriptStructureConsts.COLLECTION_ITEMS, collectionItems.toJSON());
        return jsonAssertion;
    }
}
