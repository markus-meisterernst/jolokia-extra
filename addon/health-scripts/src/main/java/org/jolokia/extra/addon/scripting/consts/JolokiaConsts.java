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

package org.jolokia.extra.addon.scripting.consts;

/**
 * Constants related to Jolokia Core and its integration specifics.
 */
public class JolokiaConsts {
    public static final String BACKEND_MANAGER_OBJECT_NAME = "jolokia:type=BackendManager";
    public static final String REGISTRATION_HOOK_KEY_WEB_APP = "org.jolokia.extra.addon.scripting.http.BackendManagerRegistrationListener";
    public static final String PROXIED_ACCESS_TO_BACKEND_MANAGER = "proxiedAccessToBackendManager";
    public static final String JMX_REQUEST_RESULT_KEY = "value";
}
