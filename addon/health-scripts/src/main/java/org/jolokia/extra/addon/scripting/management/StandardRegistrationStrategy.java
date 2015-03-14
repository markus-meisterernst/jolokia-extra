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

package org.jolokia.extra.addon.scripting.management;

import org.jolokia.extra.addon.scripting.backend.BackendManagerInfo;
import org.jolokia.extra.addon.scripting.config.ScriptConfiguration;
import org.jolokia.extra.addon.scripting.exception.RegistrationException;
import org.jolokia.util.JmxUtil;
import org.jolokia.util.LogHandler;

import javax.management.MBeanServer;

/**
 * Standard Registration Strategy of the JolokiaScriptManager.
 */
public class StandardRegistrationStrategy implements ScriptManagementRegistration {
    private ScriptConfiguration configuration;
    private ScriptManagerFactory scriptManagerFactory;
    private LogHandler logger;
    private MBeanServer mBeanServer;
    private ScriptManager scriptManager;

    public StandardRegistrationStrategy(ScriptManagerFactory scriptManagerFactory, ScriptConfiguration configuration) {
        this.scriptManagerFactory = scriptManagerFactory;
        this.configuration = configuration;
    }

    public void executeRegistration(BackendManagerInfo backendManagerInfo) throws RegistrationException {
        scriptManager = scriptManagerFactory.createScriptManager(backendManagerInfo, configuration);
        scriptManager.init();
        logger = scriptManagerFactory.getLogHandler();
        mBeanServer = backendManagerInfo.getManagementServer();

        logger.info("##  Successfully registered and called RegistrationHook. Now registering " + scriptManager.getClass().getSimpleName() + " as an MBean using ["+ getClass().getSimpleName() + "] ...");

        try {
            mBeanServer.registerMBean(scriptManager, JmxUtil.newObjectName(ScriptManager.OBJECT_NAME));
            logger.info("##  Successfully registered ScriptManager MBean under: " + ScriptManager.OBJECT_NAME);
        } catch (Exception e) {
            throw new RegistrationException("##  Failed to register ScriptManager with MBeanServer", e);
        }
    }

    public void unregister() {
        if(scriptManager != null) {
            try {
                mBeanServer.unregisterMBean(JmxUtil.newObjectName(ScriptManager.OBJECT_NAME));
                logger.info("##  Successfully unregistered the " + scriptManager.getClass().getSimpleName() + " MBean.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            scriptManager.shutdown();
        }
    }


}
