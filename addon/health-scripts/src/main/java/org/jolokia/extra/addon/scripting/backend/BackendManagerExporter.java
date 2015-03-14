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

import org.jolokia.backend.MBeanServerHandlerMBean;
import org.jolokia.extra.addon.scripting.consts.JolokiaConsts;
import org.jolokia.util.JmxUtil;
import org.jolokia.util.LogHandler;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.Arrays;
import java.util.List;

/**
 * Class to optionally register the BackendManager as an MBean if in proxiedMode and emit an BackendManagerRegisteredEvent Event.
 * The main purpose is to support eager initialization and the coordination of the HC infrastructure creation with the creation of the BackendManager.
 *
 * The Event produced by the BackendManagerMBeanExporter either transports a BackendManagerInfo if in proxiedMode or a LocalBackendManagerInfo otherwise.
 *
 * @see BackendManagerRegisteredEvent
 * @see LocalBackendManagerInfo
 * @see BackendManagerInfo
 */
public class BackendManagerExporter {
    private MBeanServer mBeanServer;
    private ObjectName backendManagerObjectName;
    private List<BackendManagerRegistrationListener> listeners;
    private boolean useProxiedMode;

    public BackendManagerExporter(List<BackendManagerRegistrationListener> backendManagerRegistrationListeners, boolean useProxiedMode) {
        this.listeners = backendManagerRegistrationListeners;
        this.useProxiedMode = useProxiedMode;
    }

    public void export(LogHandler logHandler, BackendManagerWrapperMBean backendManager) throws RuntimeException {
        if (backendManager != null) {
            List<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(null);
            for (MBeanServer mbeanServer : mbeanServers) {
                if (mbeanServer.isRegistered(JmxUtil.newObjectName(MBeanServerHandlerMBean.OBJECT_NAME))) {
                    try {
                        backendManagerObjectName = JmxUtil.newObjectName(JolokiaConsts.BACKEND_MANAGER_OBJECT_NAME);
                        this.mBeanServer = mbeanServer;

                        logHandler.info("################ Jolokia HC ####################");
                        BackendManagerRegisteredEvent backendManagerRegisteredEvent;

                        if(!useProxiedMode) {
                            logHandler.info("##  Using local BackendManager. As useProxiedMode = false won't export the BackendManager as an MBean.");
                            backendManagerRegisteredEvent = new BackendManagerRegisteredEvent(new LocalBackendManagerInfo(backendManager,logHandler, backendManagerObjectName, mbeanServer));
                        }
                        else {
                            mbeanServer.registerMBean(backendManager, backendManagerObjectName);

                            logHandler.info("##  Successfully registered the BackendManager as : "
                                    + JolokiaConsts.BACKEND_MANAGER_OBJECT_NAME + " on the same MBeanServer as jolokia ("
                                    + mbeanServer + ").");
                            backendManagerRegisteredEvent = new BackendManagerRegisteredEvent(new BackendManagerInfo(backendManagerObjectName, mbeanServer));
                        }

                        if(listeners != null) {
                            logHandler.info("##  Sending a Notification to RegistrationListeners: " + Arrays.deepToString(listeners.toArray()));
                            for(BackendManagerRegistrationListener listener: listeners) {
                                listener.notify(backendManagerRegisteredEvent);
                            }
                        }

                        logHandler.info("################################################");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void unregister() {
        if(useProxiedMode) {
            try {
                mBeanServer.unregisterMBean(backendManagerObjectName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
