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

package org.jolokia.extra.addon.scripting.management;

import org.jolokia.extra.addon.scripting.model.DataCollectionScript;
import org.jolokia.extra.addon.scripting.model.ScriptId;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standard Implementation of a Registry based on a Map.
 */
public class StandardScriptRegistry implements ScriptRegistry {
    private Map<ScriptId, DataCollectionScript> repository = new ConcurrentHashMap<ScriptId, DataCollectionScript>();

    public void init() {
        // no-op
    }

    public void shutdown() {
        repository = null;
    }

    public void register(ScriptId scriptId, DataCollectionScript dataCollectionScript) {
        repository.put(scriptId, dataCollectionScript);
    }

    public boolean unregister(ScriptId scriptId) {
        return repository.remove(scriptId) != null;
    }

    public boolean containsScript(ScriptId scriptId) {
        return repository.containsKey(scriptId);
    }

    public List<ScriptId> registeredIds() {
        List<ScriptId> ids = new ArrayList<ScriptId>(repository.keySet());
        Collections.sort(ids);
        return ids;
    }

    public DataCollectionScript get(ScriptId scriptId) {
        return repository.get(scriptId);
    }



}
