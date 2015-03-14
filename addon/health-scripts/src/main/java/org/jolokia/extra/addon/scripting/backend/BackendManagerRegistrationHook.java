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

package org.jolokia.extra.addon.scripting.backend;

import org.jolokia.extra.addon.scripting.exception.RegistrationException;
import org.jolokia.extra.addon.scripting.management.ScriptManagementRegistration;

/**
 * ScriptManagerRegistrationHook base class to listen on BackendManagerRegisteredEvents for ScriptManagement Initialization.
 * The actual registration process is to be implemented by subclasses that deal with the specific execution context (WebApp, OSGi...).
 */
public abstract class BackendManagerRegistrationHook<CALLEE extends ScriptManagementRegistration> implements RegistrationHook, BackendManagerRegistrationListener {
    private CALLEE callee;

    public BackendManagerRegistrationHook(CALLEE callee) {
        this.callee = callee;
    }

    public void notify(BackendManagerRegisteredEvent event) throws RegistrationException {
        try {
            callee.executeRegistration(event.getBackendManagerInfo());
        } catch (Exception e) {
            throw new RegistrationException(e);
        }
    }

    public void unregister() {
        callee.unregister();
    }
}
