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

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.EventObject;

/**
 * Event to publish the MBeanServer and the ObjectName the BackendManager has been actually registered with.
 */
public class BackendManagerRegisteredEvent extends EventObject {
    private static final long serialVersionUID = 1368798236911072599L;

    public BackendManagerRegisteredEvent(BackendManagerInfo backendManagerInfo) {
        super(backendManagerInfo);
    }


    public BackendManagerInfo getBackendManagerInfo() {
        return getSource();
    }

    @Override
    public BackendManagerInfo getSource() {
        return (BackendManagerInfo) super.getSource();
    }
}
