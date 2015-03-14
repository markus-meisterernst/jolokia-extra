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

import org.jolokia.discovery.AgentDetails;
import org.jolokia.request.JmxRequest;
import org.jolokia.util.LogHandler;
import org.json.simple.JSONObject;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import java.io.IOException;

/**
 * Backend Manager API.
 *
 * TODO the BackendManager in jolokia-core would ideally implement an interface.
 */
public interface IBackendManager {
    JSONObject handleRequest(JmxRequest pJmxReq) throws InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IOException;
    JSONObject convertExceptionToJson(Throwable throwable, JmxRequest jmxRequest);

    AgentDetails getAgentDetails();

    /**
     * Log a debug messages
     * @param message debug message
     */
    void debug(String message);

    /**
     * Log informal message
     * @param message message to log
     */
    void info(String message);

    /**
     * Log an error
     *
     * @param message error message
     * @param t exception causing this error
     * */
    void error(String message, Throwable t);

    boolean isDebug();
}