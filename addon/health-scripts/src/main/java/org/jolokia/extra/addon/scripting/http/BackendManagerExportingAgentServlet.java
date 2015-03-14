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

/**
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

import org.jolokia.backend.BackendManager;
import org.jolokia.config.ConfigKey;
import org.jolokia.extra.addon.scripting.backend.*;
import org.jolokia.extra.addon.scripting.consts.JolokiaConsts;
import org.jolokia.extra.addon.scripting.exception.RegistrationException;
import org.jolokia.http.AgentServlet;
import org.jolokia.util.ClassUtil;
import org.jolokia.util.LogHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * BackendManager exporting Agent servlet which connects to a local JMX MBeanServer for
 * JMX operations.
 *
 * <p>
 * It uses a REST based approach which translates a GET Url into a
 * request. See the <a href="http://www.jolokia.org/reference/index.html">reference documentation</a>
 * for a detailed description of this servlet's features.
 * </p>
 * 
 * original author roland@jolokia.org
 *
 * @see org.jolokia.http.AgentServlet
 */
public class BackendManagerExportingAgentServlet extends AgentServlet {
    private static final long serialVersionUID = 42L + 1L;
    private BackendManagerWrapperMBean backendManager;
    private BackendManagerExporter mBeanExporter;
    private boolean isProxiedMode;

    /**
     * Initialize the backend systems, the log handler and the restrictor. A subclass can tune
     * this step by overriding {@link #createRestrictor(String)} and {@link #createLogHandler(ServletConfig, boolean)}
     *
     * @param pServletConfig servlet configuration
     */
    @Override
    public void init(ServletConfig pServletConfig) throws ServletException {
        super.init(pServletConfig);

        String logHandlerClass =  pServletConfig.getInitParameter(ConfigKey.LOGHANDLER_CLASS.name());
        LogHandler logHandler = logHandlerClass != null ?
                (LogHandler) ClassUtil.newInstance(logHandlerClass) :
                createLogHandler(pServletConfig,Boolean.valueOf(pServletConfig.getInitParameter(ConfigKey.DEBUG.name())));

        backendManager = new BackendManagerWrapper(getInternalBackendManager());
        List<BackendManagerRegistrationListener> listeners = new ArrayList<BackendManagerRegistrationListener>();
        BackendManagerRegistrationListener listener = (BackendManagerRegistrationListener) pServletConfig.getServletContext().getAttribute(JolokiaConsts.REGISTRATION_HOOK_KEY_WEB_APP);
        if(listener != null) {
            listeners.add(listener);
        }

        String isProxiedModeValue = pServletConfig.getServletContext().getInitParameter(JolokiaConsts.PROXIED_ACCESS_TO_BACKEND_MANAGER);
        if(isProxiedModeValue != null && Boolean.valueOf(isProxiedModeValue)) {
            isProxiedMode = true;
        }

        mBeanExporter = new BackendManagerExporter(listeners, isProxiedMode);
        mBeanExporter.export(getLogHandler(), backendManager);
    }

    private BackendManager getInternalBackendManager() {
        Object backendManager=null;
        try {
            Class agentServlet = this.getClass().getSuperclass();
            Field backendManagerField = agentServlet.getDeclaredField("backendManager");
            backendManagerField.setAccessible(true);
            backendManager =  backendManagerField.get(this);
            backendManagerField.setAccessible(false);
        } catch (Exception e) {
            throw new RegistrationException(e);
        }
        return (BackendManager) backendManager;
    }

    protected IBackendManager getBackendManager() {
        return backendManager;
    }

    /** {@inheritDoc} */
    @Override
    public void destroy() {
        super.destroy();

        mBeanExporter.unregister();
    }
}
