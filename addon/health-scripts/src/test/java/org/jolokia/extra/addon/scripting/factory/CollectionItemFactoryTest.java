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

import org.jolokia.extra.addon.scripting.consts.RequestTypeConsts;
import org.jolokia.extra.addon.scripting.consts.ScriptStructureConsts;
import org.jolokia.extra.addon.scripting.consts.VocabularyConsts;
import org.jolokia.extra.addon.scripting.exception.ValidationException;
import org.jolokia.extra.addon.scripting.model.CollectionItem;
import org.jolokia.extra.addon.scripting.model.CollectionItems;
import org.jolokia.extra.addon.scripting.model.ReadCollectionItem;
import org.jolokia.extra.addon.scripting.model.VersionCollectionItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Test cases for the ReadCollectionItem specific Factory.
 */
public class CollectionItemFactoryTest {
    private CollectionItemFactory factory = new CollectionItemFactory();

    @Test
    public void test_create_successful_items_collection_creation_one_entry() {
        JSONObject collectionItemJson = new JSONObject();
        collectionItemJson.put(VocabularyConsts.TYPE_KEY, RequestTypeConsts.READ);
        collectionItemJson.put(VocabularyConsts.VAR_KEY, "memory");
        collectionItemJson.put(VocabularyConsts.MBEAN_KEY, "java.lang:type=Memory");
        collectionItemJson.put(VocabularyConsts.ATTRIBUTE_KEY, "HeapMemoryUsage");

        CollectionItems items = factory.createCollectionItemsFromJson(collectionItemJson);

        assertNotNull(items, "items is not expected to be null");
        assertTrue(items.get(0) instanceof ReadCollectionItem, "item[0] is expected to be an instance of ReadCollectionItem");
        assertEquals(items.get(0).getVarName(), "memory");
    }

    @Test
    public void test_create_successful_items_collection_creation() {
        JSONArray colllectionItemJson = new JSONArray();

        JSONObject collectionItemJson = new JSONObject();
        collectionItemJson.put(VocabularyConsts.TYPE_KEY, RequestTypeConsts.READ);
        collectionItemJson.put(VocabularyConsts.VAR_KEY, "memory");
        collectionItemJson.put(VocabularyConsts.MBEAN_KEY, "java.lang:type=Memory");
        collectionItemJson.put(VocabularyConsts.ATTRIBUTE_KEY, "HeapMemoryUsage");

        JSONObject collectionItemJson2 = new JSONObject();
        collectionItemJson2.put(VocabularyConsts.TYPE_KEY, RequestTypeConsts.VERSION);
        collectionItemJson2.put(VocabularyConsts.VAR_KEY, "jolokiaVersion");

        colllectionItemJson.add(collectionItemJson);
        colllectionItemJson.add(collectionItemJson2);

        CollectionItems items = factory.createCollectionItemsFromJson(colllectionItemJson);

        assertNotNull(items, "items is not expected to be null");
        assertTrue(items.get(0) instanceof ReadCollectionItem, "item[0] is expected to be an instance of ReadCollectionItem");
        assertTrue(items.get(1) instanceof VersionCollectionItem, "item[1] is expected to be an instance of VersionCollectionItem");
        assertEquals(items.get(0).getVarName(), "memory");
        assertEquals(items.get(1).getVarName(), "jolokiaVersion");

        items.validate();
    }


    @Test(expectedExceptions = ValidationException.class)
    public void test_create_failed_items_collection_creation() {
        JSONArray colllectionItemJson = new JSONArray();

        JSONObject collectionItemJson = new JSONObject();
        collectionItemJson.put(VocabularyConsts.TYPE_KEY, RequestTypeConsts.READ);
        collectionItemJson.put(VocabularyConsts.VAR_KEY, "memory");
        collectionItemJson.put(VocabularyConsts.MBEAN_KEY, "java.lang:type=Memory");
        collectionItemJson.put(VocabularyConsts.ATTRIBUTE_KEY, "HeapMemoryUsage");

        JSONObject collectionItemJson2 = new JSONObject();
        collectionItemJson2.put(VocabularyConsts.TYPE_KEY, RequestTypeConsts.VERSION);
        collectionItemJson2.put(VocabularyConsts.VAR_KEY, "jolokiaVersion");

        JSONArray illegalNestedArray = new JSONArray();
        illegalNestedArray.add(collectionItemJson2);

        colllectionItemJson.add(collectionItemJson);
        colllectionItemJson.add(illegalNestedArray);

        CollectionItems items = factory.createCollectionItemsFromJson(colllectionItemJson);

        fail("The use of a nested Array within the " + ScriptStructureConsts.COLLECTION_ITEMS + " block is supposed to throw a ValidationException");
    }

    @Test(expectedExceptions = ValidationException.class)
    public void test_create_failed_type_request() {
        JSONObject collectionItemJson = new JSONObject();
        collectionItemJson.put(VocabularyConsts.TYPE_KEY, "nonsense");

        CollectionItem item = factory.createItemFromJson(collectionItemJson);
        fail("creation of an item with an unknown " + RequestTypeConsts.KEY + " is supposed to throw a ValidationException");
    }

    @Test(expectedExceptions = ValidationException.class)
    public void test_items_collection_validation_failed() {
        CollectionItems items = factory.createCollectionItemsFromJson(new JSONAware() {
            public String toJSONString() {
                return "I'm a special one";
            }
        });

        fail("The " + ScriptStructureConsts.COLLECTION_ITEMS + " block is not supposed to be empty.");
    }
}
