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
import org.jolokia.extra.addon.scripting.exception.ValidationException;
import org.json.simple.JSONObject;

import static org.jolokia.extra.addon.scripting.model.ModelAssertions.assertNotEmpty;

/**
 * Data Collection Script derivation that allows the execution of Assertions on the gathered information as some kind of post-processing.
 */
public class HealthCheckScript extends DataCollectionScript {
    private Assertions assertions;

    public HealthCheckScript() {
        super();
    }

    public HealthCheckScript(ScriptId scriptId, Meta meta, Variables variables, CollectionItems collectionItems, Assertions assertions) {
        this(scriptId, meta, variables, collectionItems, assertions, null);
    }

    public HealthCheckScript(ScriptId scriptId, Meta meta, Variables variables, CollectionItems collectionItems, Assertions assertions, Conditional conditional) {
        super(scriptId, meta, variables, collectionItems, conditional);
        this.assertions = assertions;
    }

    public Assertions getAssertions() {
        return assertions;
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();

        ModelAssertions.assertNotNull(assertions, "The assertions property is not allowed ot be null");
        assertNotEmpty(assertions, "There should be at least one assertion for a JolokiaHealthCheck instance.");
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonScript = super.toJSON();
        jsonScript.put(ScriptStructureConsts.ASSERTIONS, assertions.toJSON());
        return jsonScript;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HealthCheckScript)) return false;
        if (!super.equals(o)) return false;

        HealthCheckScript that = (HealthCheckScript) o;

        if (!assertions.equals(that.assertions)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + assertions.hashCode();
        return result;
    }
}
