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
import org.jolokia.extra.addon.scripting.backend.IBackendManager;
import org.jolokia.extra.addon.scripting.backend.LocalBackendManagerInfo;
import org.jolokia.extra.addon.scripting.config.ScriptConfiguration;
import org.jolokia.extra.addon.scripting.eval.BooleanExpressionEvaluator;
import org.jolokia.extra.addon.scripting.eval.ExpressionEvaluator;
import org.jolokia.extra.addon.scripting.eval.jexl.JexlBooleanEvaluator;
import org.jolokia.extra.addon.scripting.eval.jexl.JexlEvaluator;
import org.jolokia.extra.addon.scripting.exception.RegistrationException;
import org.jolokia.extra.addon.scripting.execution.ScriptExecutionCoordinator;
import org.jolokia.extra.addon.scripting.execution.StandardScriptExecutionCoordinator;
import org.jolokia.extra.addon.scripting.factory.ScriptFactory;
import org.jolokia.extra.addon.scripting.factory.StandardScriptFactory;
import org.jolokia.extra.addon.scripting.marshalling.JsonMarshaller;
import org.jolokia.extra.addon.scripting.marshalling.StandardJsonMarshaller;
import org.jolokia.util.LogHandler;


/**
 * Stateful Standard Factory Implementation for ScriptManager creation and configuration.
 */
public class StandardScriptManagerFactory implements ScriptManagerFactory {
    private LogHandler logHandler;

    public ScriptManager createScriptManager(BackendManagerInfo backendManagerInfo, ScriptConfiguration configuration) {
        JolokiaScriptManager scriptManager = new JolokiaScriptManager();
        scriptManager.setMarshaller(createMarshaller());
        BooleanExpressionEvaluator booleanExpressionEvaluator = createBooleanExpressionEvaluator();
        ExpressionEvaluator generalExpressionEvaluator = createExpressionEvaluator();
        scriptManager.setScriptFactory(createScriptFactory(booleanExpressionEvaluator));
        scriptManager.setScriptRegistry(createScriptRegistry());
        scriptManager.setConfiguration(configuration);

        IBackendManager backendManager;
        if(backendManagerInfo instanceof LocalBackendManagerInfo) {
            LocalBackendManagerInfo localBackendManagerInfo = (LocalBackendManagerInfo) backendManagerInfo;
            backendManager = localBackendManagerInfo.getBackendManager();
            logHandler = localBackendManagerInfo.getLogHandler();
        }
        else {
            backendManager = createBackendManagerProxy(backendManagerInfo);
            logHandler = createLogHandlerProxy(backendManagerInfo);
        }

        scriptManager.setScriptExecutionCoordinator(createScriptExecutionCoordinator(backendManager, logHandler, generalExpressionEvaluator, booleanExpressionEvaluator));

        return scriptManager;
    }

    /**
     * Gets the LogHandler as soon as the createScriptManager() method has been called.
     *
     * @return LogHandler
     */
    public LogHandler getLogHandler() {
        return logHandler;
    }

    protected IBackendManager createBackendManagerProxy(BackendManagerInfo backendManagerInfo) {
        // TODO implement proxy creation. StandardScriptManagerFactory#createBackendManagerProxy
        throw new RegistrationException(getClass().getCanonicalName() + "#" + "createBackendManagerProxy is not implemented yet.");
    }

    protected LogHandler createLogHandlerProxy(BackendManagerInfo backendManagerInfo) {
        // TODO implement proxy creation. StandardScriptManagerFactory#createLogHandlerProxy
        throw new RegistrationException(getClass().getCanonicalName() + "#" + "createLogHandlerProxy is not implemented yet.");
    }

    protected JsonMarshaller createMarshaller() {
        return new StandardJsonMarshaller();
    }

    protected ScriptRegistry createScriptRegistry() {
        return new StandardScriptRegistry();
    }

    protected ScriptFactory createScriptFactory(BooleanExpressionEvaluator booleanExpressionEvaluator) {
        return new StandardScriptFactory(booleanExpressionEvaluator);
    }

    protected ScriptExecutionCoordinator createScriptExecutionCoordinator(IBackendManager backendManager, LogHandler logHandler, ExpressionEvaluator generalExpressionEvaluator, BooleanExpressionEvaluator booleanExpressionEvaluator) {
        return new StandardScriptExecutionCoordinator(backendManager, logHandler, generalExpressionEvaluator, booleanExpressionEvaluator);
    }

    protected ExpressionEvaluator createExpressionEvaluator() {
        return new JexlEvaluator();
    }

    protected BooleanExpressionEvaluator createBooleanExpressionEvaluator() {
        return new JexlBooleanEvaluator();
    }

}
