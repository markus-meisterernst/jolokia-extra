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

package org.jolokia.extra.addon.scripting.exec;

import org.jolokia.backend.BackendManager;
import org.jolokia.config.ConfigKey;
import org.jolokia.config.Configuration;
import org.jolokia.extra.addon.scripting.consts.RequestTypeConsts;
import org.jolokia.extra.addon.scripting.consts.VocabularyConsts;
import org.jolokia.extra.addon.scripting.factory.CollectionItemFactory;
import org.jolokia.extra.addon.scripting.model.ReadCollectionItem;
import org.jolokia.util.NetworkUtil;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import java.io.IOException;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Test to appraoch th eintegration of the BackendManager for actual JmxRequest excution
 */
public class BackendManagerIntegrationTest {
    private CollectionItemFactory factory = new CollectionItemFactory();

    @Test
    public void test_backenmanager_creation() throws MBeanException, AttributeNotFoundException, ReflectionException, InstanceNotFoundException, IOException {
        Configuration config = new Configuration(
                ConfigKey.AGENT_ID, NetworkUtil.getAgentId(hashCode(), "unit-test"));
        BackendManager backendManager = new BackendManager(config, null);

        JSONObject collectionItemJson = new JSONObject();
        collectionItemJson.put(VocabularyConsts.TYPE_KEY, RequestTypeConsts.READ);
        collectionItemJson.put(VocabularyConsts.VAR_KEY, "memory");
        collectionItemJson.put(VocabularyConsts.MBEAN_KEY, "java.lang:type=Memory");
        collectionItemJson.put(VocabularyConsts.ATTRIBUTE_KEY, "HeapMemoryUsage");

        ReadCollectionItem item = (ReadCollectionItem) factory.createItemFromJson(collectionItemJson);

        assertNotNull(item);
        JSONObject result = backendManager.handleRequest(item.getRequest());
        assertNotNull(result);
        assertTrue(result.containsKey("value"));
        JSONObject value = (JSONObject) result.get("value");
        assertTrue(value.containsKey("init"));
        assertTrue(value.containsKey("used"));
        assertTrue(value.containsKey("committed"));
        assertTrue(value.containsKey("max"));
    }
}
