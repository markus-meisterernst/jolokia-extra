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
import org.jolokia.request.JmxRequest;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import static org.jolokia.extra.addon.scripting.model.ModelAssertions.assertNotEmpty;

/**
 * Central model object to wrap jolokia core's JmxRequest as part of the Data Collection process of an actual Script or Conditional execution.
 *
 * @see org.jolokia.extra.addon.scripting.model.DataCollectionScript
 * @see org.jolokia.extra.addon.scripting.model.HealthCheckScript
 * @see org.jolokia.extra.addon.scripting.model.Conditional
 */
public class CollectionItem extends ScriptingModelItem implements ModelValidation {
    private String varName;
    private JmxRequest request;

    private CollectionItem() {}

    public CollectionItem(String name, JmxRequest request) {
        this.varName = name;
        this.request = request;
    }

    public String getVarName() {
        return varName;
    }

    public JmxRequest getRequest() {
        return request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollectionItem)) return false;

        CollectionItem that = (CollectionItem) o;

        if (!varName.equals(that.varName)) return false;
        if (!request.equals(that.request)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = varName.hashCode();
        result = 31 * result + request.hashCode();
        return result;
    }

    public void validate() {
        ModelAssertions.assertNotEmpty(varName, "The property 'varName' is not allowed to be empty.");
        ModelAssertions.assertNotNull(request, "The property 'request' is not allowed to be null.");
    }

    @Override
    public JSONAware toJSON() {
        JSONObject jsonRequest = getRequest().toJSON();
        jsonRequest.put(VocabularyConsts.VAR_KEY, getVarName());
        return jsonRequest;
    }
}
