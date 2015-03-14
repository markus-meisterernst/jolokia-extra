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

import java.util.List;

/**
 * Script Registry API.
 */
public interface ScriptRegistry {
    void register(ScriptId scriptId, DataCollectionScript dataCollectionScript);

    boolean unregister(ScriptId scriptId);

    boolean containsScript(ScriptId scriptId);

    List<ScriptId> registeredIds();

    DataCollectionScript get(ScriptId scriptId);

    void init();

    void shutdown();
}
