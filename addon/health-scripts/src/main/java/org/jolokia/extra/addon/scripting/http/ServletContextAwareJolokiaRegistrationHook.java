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

package org.jolokia.extra.addon.scripting.http;

import org.jolokia.extra.addon.scripting.backend.BackendManagerRegistrationHook;
import org.jolokia.extra.addon.scripting.backend.RegistrationHook;
import org.jolokia.extra.addon.scripting.consts.JolokiaConsts;
import org.jolokia.extra.addon.scripting.management.ScriptManagementRegistration;

import javax.servlet.ServletContext;

/**
 * ServletContext specific Hook Registration.
 */
public class ServletContextAwareJolokiaRegistrationHook extends BackendManagerRegistrationHook<ScriptManagementRegistration> implements RegistrationHook {
    private ServletContext servletContext;

    public ServletContextAwareJolokiaRegistrationHook(ScriptManagementRegistration registrationStrategy, ServletContext servletContext) {
        super(registrationStrategy);
        this.servletContext = servletContext;
    }

    public void register() {
        servletContext.setAttribute(JolokiaConsts.REGISTRATION_HOOK_KEY_WEB_APP, this);
    }

    public void unregister() {
        servletContext.removeAttribute(JolokiaConsts.REGISTRATION_HOOK_KEY_WEB_APP);
        super.unregister();
    }
}
