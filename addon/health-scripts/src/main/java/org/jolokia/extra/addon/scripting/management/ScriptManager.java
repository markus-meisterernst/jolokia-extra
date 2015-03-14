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

/**
 * Healthcheck ScriptManager API.
 *
 * The Manager serves as the key component for HC Script management and execution on behalf of the Jolokia Agent.
 */
public interface ScriptManager extends JolokiaScriptManagerMBean, ScriptManagerLifecycle {
    static final String OBJECT_NAME = "jolokia-extra:type=ScriptManager";

}
