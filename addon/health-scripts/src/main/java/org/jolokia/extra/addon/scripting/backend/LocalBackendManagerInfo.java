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

package org.jolokia.extra.addon.scripting.backend;

import org.jolokia.backend.BackendManager;
import org.jolokia.util.LogHandler;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Wrapper Class around the ObjectName and the MBeanServer with which the BackendManager is registered.
 */
public class LocalBackendManagerInfo  extends BackendManagerInfo {
    private IBackendManager backendManager;
    private LogHandler logHandler;

    public LocalBackendManagerInfo(IBackendManager backendManager, LogHandler logHandler, ObjectName objectName, MBeanServer managementServer) {
        super(objectName, managementServer);
        this.backendManager = backendManager;
        this.logHandler = logHandler;
    }

    public IBackendManager getBackendManager() {
        return backendManager;
    }

    public LogHandler getLogHandler() {
        return logHandler;
    }
}
