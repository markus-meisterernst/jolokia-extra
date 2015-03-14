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
import org.json.simple.JSONObject;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import java.io.IOException;

/**
 * Public API to the BackendManager.
 *
 * TODO would be nice if the BackendManager in jolokia-core would implement such an official API and let the core expose the BackendManager as an MBean straight away.
 */
public interface BackendManagerWrapperMBean extends IBackendManager {
    JSONObject handleRequest(JmxRequest pJmxReq) throws InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IOException;

    AgentDetails getAgentDetails();

    boolean isDebug();

    void debug(String msg);

    void info(String msg);

    void error(String message, Throwable t);
}
