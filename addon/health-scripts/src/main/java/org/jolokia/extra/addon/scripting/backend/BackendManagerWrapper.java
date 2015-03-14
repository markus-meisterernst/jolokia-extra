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

import org.jolokia.backend.BackendManager;
import org.jolokia.discovery.AgentDetails;
import org.jolokia.request.JmxRequest;
import org.json.simple.JSONObject;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import java.io.IOException;

/**
 * Wrapper around the BackendManager to allow for MBean exposure.
 */
public class BackendManagerWrapper implements BackendManagerWrapperMBean {
    private BackendManager backendManager;

    public BackendManagerWrapper(BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    public JSONObject handleRequest(JmxRequest pJmxReq) throws InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IOException {
        return getBackendManager().handleRequest(pJmxReq);
    }

    public JSONObject convertExceptionToJson(Throwable throwable, JmxRequest jmxRequest) {
        return (JSONObject) backendManager.convertExceptionToJson(throwable, jmxRequest);
    }

    public void error(String message, Throwable t) {
        getBackendManager().error(message, t);
    }

    public AgentDetails getAgentDetails() {
        return getBackendManager().getAgentDetails();
    }

    public void debug(String msg) {
        getBackendManager().debug(msg);
    }

    public boolean isDebug() {
        return getBackendManager().isDebug();
    }

    public void info(String msg) {
        getBackendManager().info(msg);
    }

    protected BackendManager getBackendManager() {
        return backendManager;
    }
}
