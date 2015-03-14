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
import org.jolokia.extra.addon.scripting.model.VersionCollectionItem;
import org.jolokia.request.JmxVersionRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Test Cases for the VersionCollectionItem specific Factory.
 */
public class VersionCollectionItemFactoryTest  {
    private CollectionItemFactory factory = new CollectionItemFactory();

    @Test
    public void test_create_successful_version_request() throws IOException, ParseException {
        JSONObject collectionItemJson = new JSONObject();
        collectionItemJson.put(VocabularyConsts.TYPE_KEY, RequestTypeConsts.VERSION);
        collectionItemJson.put(VocabularyConsts.VAR_KEY, "jolokiaVersion");

        CollectionItem item = factory.createItemFromJson(collectionItemJson);
        assertNotNull(item, "item is not expected to be null");
        assertTrue(item instanceof VersionCollectionItem, "item is expected to be an instance of VersionCollectionItem");
        assertEquals(item.getVarName(), "jolokiaVersion");
        assertNotNull(item.getRequest());
        assertTrue(item.getRequest() instanceof JmxVersionRequest);

        item.validate();
    }

    @Test(expectedExceptions = ValidationException.class)
    public void test_failed_validation() {
        JSONObject collectionItemJson = new JSONObject();
        collectionItemJson.put(VocabularyConsts.TYPE_KEY, RequestTypeConsts.VERSION);
        // var is not set: collectionItemJson.put(ScriptConsts.vocabulary.VAR_KEY, "jolokiaVersion");
        CollectionItem item = factory.createItemFromJson(collectionItemJson);
        assertNotNull(item, "item is not expected to be null");
        assertTrue(item instanceof VersionCollectionItem, "item is expected to be an instance of VersionCollectionItem");
        assertTrue(item.getVarName() == null, "varName is supposed to be null");

        item.validate();
    }
}
