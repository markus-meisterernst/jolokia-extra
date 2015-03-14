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

import org.jolokia.extra.addon.scripting.consts.RequestTypeConsts;
import org.jolokia.extra.addon.scripting.consts.VocabularyConsts;
import org.jolokia.extra.addon.scripting.exception.ValidationException;
import org.jolokia.extra.addon.scripting.model.CollectionItem;
import org.jolokia.extra.addon.scripting.model.ReadCollectionItem;
import org.jolokia.request.JmxReadRequest;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Test cases for the ReadCollectionItem specific Factory.
 */
public class ReadCollectionItemFactoryTest {
    private CollectionItemFactory factory = new CollectionItemFactory();

    @Test
    public void test_create_successful_read_request() {
        JSONObject collectionItemJson = new JSONObject();
        collectionItemJson.put(VocabularyConsts.TYPE_KEY, RequestTypeConsts.READ);
        collectionItemJson.put(VocabularyConsts.VAR_KEY, "memory");
        collectionItemJson.put(VocabularyConsts.MBEAN_KEY, "java.lang:type=Memory");
        collectionItemJson.put(VocabularyConsts.ATTRIBUTE_KEY, "HeapMemoryUsage");

        CollectionItem item = factory.createItemFromJson(collectionItemJson);
        assertNotNull(item, "item is not expected to be null");
        assertTrue(item instanceof ReadCollectionItem, "item is expected to be an instance of VersionCollectionItem");
        assertEquals(item.getVarName(), "memory");
        assertNotNull(item.getRequest());
        assertTrue(item.getRequest() instanceof JmxReadRequest);

        item.validate();
    }


    @Test(expectedExceptions = ValidationException.class)
    public void test_create_failed_read_request() {
        JSONObject collectionItemJson = new JSONObject();
        collectionItemJson.put(VocabularyConsts.TYPE_KEY, RequestTypeConsts.READ);
        collectionItemJson.put(VocabularyConsts.VAR_KEY, "memory");
        // missed out the MBean Name ... collectionItemJson.put(ScriptingConstants.vocabulary.MBEAN_KEY, "java.lang:type=Memory");
        collectionItemJson.put(VocabularyConsts.ATTRIBUTE_KEY, "HeapMemoryUsage");

        CollectionItem item = factory.createItemFromJson(collectionItemJson);

        fail("creation of an item with an unset " + VocabularyConsts.MBEAN_KEY + " property is supposed to throw a ValidationException");
    }
}
