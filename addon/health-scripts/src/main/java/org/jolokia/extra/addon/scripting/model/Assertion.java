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


import org.jolokia.extra.addon.scripting.consts.VocabularyConsts;
import org.jolokia.extra.addon.scripting.eval.Expression;
import org.jolokia.extra.addon.scripting.exception.ValidationException;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import static org.jolokia.extra.addon.scripting.model.ModelAssertions.assertNotEmpty;
import static org.jolokia.extra.addon.scripting.model.ModelAssertions.assertNotNull;

/**
 * Model Class for defining a HealthCheck Assertion.
 *
 * If an Assertion fails, then either a static failure description or a dynamic descriptionExpression is to be supplied.
 */
public class Assertion extends ScriptingModelItem implements ModelValidation {
    private String name;
    private Expression check;
    private String description;
    private Expression descriptionExpression;

    private Assertion() {}

    public Assertion(String name, Expression check, String description) {
        this.name = name;
        this.check = check;
        this.description = description;
    }

    public Assertion(String name, Expression check, Expression descriptionExpression) {
        this.name = name;
        this.check = check;
        this.descriptionExpression = descriptionExpression;
    }

    public String getName() {
        return name;
    }

    public Expression getCheck() {
        return check;
    }

    public String getDescription() {
        return description;
    }

    public Expression getDescriptionExpression() {
        return descriptionExpression;
    }

    public void validate() {
        assertNotEmpty(name, "The property 'name' is not allowed to be null or empty.");
        assertNotNull(check, "The property 'check' is not allowed to be null.");
        if(description == null && descriptionExpression == null) {
            throw new ValidationException("One of the properties '" + VocabularyConsts.DESCRIPTION_KEY
                    + ": OR '" + VocabularyConsts.DESCRIPTION_EXPRESSION_KEY + "' must be supplied.");
        }
        else if(description != null && descriptionExpression != null) {
            throw new ValidationException("Either '" + VocabularyConsts.DESCRIPTION_KEY
                    + ": OR '" + VocabularyConsts.DESCRIPTION_EXPRESSION_KEY + "' must be supplied, but not both.");
        }
    }

    @Override
    public JSONAware toJSON() {
        JSONObject jsonAssertion = new JSONObject();
        jsonAssertion.put(VocabularyConsts.NAME_KEY, name);
        jsonAssertion.put(VocabularyConsts.CHECK_KEY, check.asString());
        if(description != null) {
            jsonAssertion.put(VocabularyConsts.DESCRIPTION_KEY, description);
        }
        else {
            jsonAssertion.put(VocabularyConsts.DESCRIPTION_EXPRESSION_KEY, descriptionExpression.asString());
        }
        return jsonAssertion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assertion)) return false;

        Assertion assertion = (Assertion) o;

        if (!check.equals(assertion.check)) return false;
        if (description != null ? !description.equals(assertion.description) : assertion.description != null)
            return false;
        if (descriptionExpression != null ? !descriptionExpression.equals(assertion.descriptionExpression) : assertion.descriptionExpression != null)
            return false;
        if (!name.equals(assertion.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + check.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (descriptionExpression != null ? descriptionExpression.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String desc = description != null ? (", " + VocabularyConsts.DESCRIPTION_KEY+ "='" + description + "\'") : (", "+ VocabularyConsts.DESCRIPTION_EXPRESSION_KEY+"='" + descriptionExpression + "\'");
        return "Assertion{" +
                "name='" + name + '\'' +
                ", check='" + check + '\'' +
                desc + "}";
    }
}
