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

import org.jolokia.extra.addon.scripting.backend.RegistrationHook;
import org.jolokia.extra.addon.scripting.config.ScriptConfiguration;
import org.jolokia.extra.addon.scripting.management.ScriptManagementRegistration;
import org.jolokia.extra.addon.scripting.management.ScriptManagerFactory;
import org.jolokia.extra.addon.scripting.management.StandardRegistrationStrategy;
import org.jolokia.extra.addon.scripting.management.StandardScriptManagerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * ServletContextListener implementation for Hook Registration.
 */
public class RegistrationHookServletContextListener implements ServletContextListener {
    private RegistrationHook registrationHook;

    public void contextInitialized(ServletContextEvent sce) {
        registrationHook = createBackendManagerRegistrationHook(sce.getServletContext());
        registrationHook.register();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        if(registrationHook != null) {
            registrationHook.unregister();
        }
    }

    protected RegistrationHook createBackendManagerRegistrationHook(ServletContext servletContext) {
        return new ServletContextAwareJolokiaRegistrationHook(
                createRegistrationStrategy(
                        extractScriptConfiguration(servletContext)), servletContext);
    }

    protected ScriptManagementRegistration createRegistrationStrategy(ScriptConfiguration configuration) {
        return new StandardRegistrationStrategy(createScriptManagerFactory(), configuration);
    }

    protected ScriptManagerFactory createScriptManagerFactory() {
        return new StandardScriptManagerFactory();
    }

    protected ScriptConfiguration extractScriptConfiguration(ServletContext servletContext) {
        // TODO implement RegistrationHookServletContextListener#extractScriptConfiguration
        return new ScriptConfiguration();
    }
}
