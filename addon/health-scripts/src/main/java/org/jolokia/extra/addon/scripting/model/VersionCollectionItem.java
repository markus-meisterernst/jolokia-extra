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

package org.jolokia.extra.addon.scripting.model;

import org.jolokia.request.JmxVersionRequest;

/**
 * Wrapper class for jolokia core's JmxVersionRequest.
 *
 * @see org.jolokia.request.JmxVersionRequest
 */
public class VersionCollectionItem extends CollectionItem {

    public VersionCollectionItem(String name, JmxVersionRequest versionRequest) {
        super(name, versionRequest);
    }

    @Override
    public JmxVersionRequest getRequest() {
        return (JmxVersionRequest) super.getRequest();
    }

}
