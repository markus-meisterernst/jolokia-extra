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

import org.jolokia.config.ConfigKey;
import org.jolokia.config.ProcessingParameters;
import org.jolokia.extra.addon.scripting.consts.RequestTypeConsts;
import org.jolokia.extra.addon.scripting.consts.VocabularyConsts;
import org.jolokia.extra.addon.scripting.exception.ValidationException;
import org.jolokia.extra.addon.scripting.model.CollectionItem;
import org.jolokia.extra.addon.scripting.model.CollectionItems;
import org.jolokia.extra.addon.scripting.model.ReadCollectionItem;
import org.jolokia.extra.addon.scripting.model.VersionCollectionItem;
import org.jolokia.request.JmxReadRequest;
import org.jolokia.request.JmxRequestFactory;
import org.jolokia.request.JmxVersionRequest;
import org.jolokia.util.RequestType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory to allow the creation of CollectionItem Subclasses directly from JSON Objects.
 */
public class CollectionItemFactory {
    private Map<Object, CollectionItemSubclassFactory> factory = new HashMap<Object, CollectionItemSubclassFactory>();

    public CollectionItemFactory()  {
        addCollectionItemTypeFactory(RequestTypeConsts.VERSION, new VersionFactory());
        addCollectionItemTypeFactory(RequestTypeConsts.READ, new ReadFactory());
    }

    protected void addCollectionItemTypeFactory(String requestType, CollectionItemSubclassFactory subclassFactory) {
        factory.put(requestType, subclassFactory);
    }

    public CollectionItems createCollectionItemsFromJson(Object jsonAware) {
        CollectionItems items = new CollectionItems();
        if(jsonAware != null) {
            if(jsonAware instanceof JSONArray) {
                JSONArray jsonCollection = (JSONArray) jsonAware;
                for(Object item: jsonCollection) {
                    if(item instanceof JSONObject) {
                        items.add(createItemFromJson((JSONObject) item));
                    }
                    else {
                        throw new ValidationException("The CollectionItem " + item + " does not represent a JSONObject. Beware that nested JSONArrays aren't supported.");
                    }
                }
            }
            else if(jsonAware instanceof JSONObject) {
                items.add(createItemFromJson((JSONObject) jsonAware));
            }
            else {
                throw new ValidationException("The CollectionItem " + jsonAware + " is of unsupported type: " + jsonAware.getClass());
            }
        }
        return items;
    }

    public CollectionItem createItemFromJson(JSONObject rawItem) throws ValidationException {
        Object typeObject = rawItem.get(VocabularyConsts.TYPE_KEY);
        CollectionItemSubclassFactory concreteFactory = factory.get(typeObject);
        if(concreteFactory != null) {
            try {
                return concreteFactory.createCollectionItem(rawItem);
            } catch (Exception e) {
                throw new ValidationException(e);
            }
        }

        throw new ValidationException("The CollectionItem " + rawItem + " defines an unknown "
                + VocabularyConsts.TYPE_KEY + " key: " + typeObject + ". It should match one of these: "
                + Arrays.deepToString(RequestType.values()).toLowerCase());
    }


    private static ProcessingParameters createEmptyProcessingParameters() {
        Map<ConfigKey, String> params = new HashMap<ConfigKey, String>();
        try {
            Constructor<ProcessingParameters> constructor = ProcessingParameters.class.getDeclaredConstructor(Map.class, String.class);
            constructor.setAccessible(true);
            ProcessingParameters processingParameters =  constructor.newInstance(params, null);
            return processingParameters;
        } catch (Exception e) {
            throw new ValidationException(e);
        }
    }

    public interface CollectionItemSubclassFactory {
        CollectionItem createCollectionItem(JSONObject rawItem);
    }

    private class VersionFactory implements CollectionItemSubclassFactory {
        public CollectionItem createCollectionItem(JSONObject rawItem) {
            JmxVersionRequest jmxVersionRequest = JmxRequestFactory.createGetRequest(RequestTypeConsts.VERSION, createEmptyProcessingParameters());
            return new VersionCollectionItem((String) rawItem.get(VocabularyConsts.VAR_KEY), jmxVersionRequest);
        }
    }

    private class ReadFactory implements CollectionItemSubclassFactory {
        public CollectionItem createCollectionItem(JSONObject rawItem) {
            Map<String, Object> requestMap = new HashMap<String, Object>();
            requestMap.put(VocabularyConsts.TYPE_KEY, rawItem.get(VocabularyConsts.TYPE_KEY));
            requestMap.put(VocabularyConsts.MBEAN_KEY, rawItem.get(VocabularyConsts.MBEAN_KEY));

            if(rawItem.containsKey(VocabularyConsts.ATTRIBUTE_KEY)) {
                requestMap.put(VocabularyConsts.ATTRIBUTE_KEY, rawItem.get(VocabularyConsts.ATTRIBUTE_KEY));
            }
            else {
                requestMap.put(VocabularyConsts.ATTRIBUTE_KEY, rawItem.get(VocabularyConsts.ATTRIBUTES_KEY));
            }
            requestMap.put(VocabularyConsts.PATH_KEY, rawItem.get(VocabularyConsts.PATH_KEY));

            JmxReadRequest jmxReadRequest = JmxRequestFactory.createPostRequest(requestMap, createEmptyProcessingParameters());
            return new ReadCollectionItem((String) rawItem.get(VocabularyConsts.VAR_KEY), jmxReadRequest);
        }
    }
}
