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

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * MBean Interface to expose the JolokiaScriptManager as a Standard MBean.
 */
public interface JolokiaScriptManagerMBean {
    /**
     * Registers the passed in DataCollectionScript serialized as a String.
     *
     * @param jolokiaCollectionScriptJsonAsString DataCollectionScript serialized as a String
     * @return ScriptId as a JSONObject
     */
    JSONObject registerScript(String jolokiaCollectionScriptJsonAsString);

    /**
     * Unregisters a Script by its ScriptId
     * @param canonicalName as part of the ID
     * @param version as part of the ID
     * @return true if a script with the supplied scriptId was found and removed. false otherwise
     */
    boolean unregisterScript(String canonicalName, String version);

    /**
     * Checks if a certain Script identified by its scriptId is present
     *
     * @param canonicalName
     * @param version
     * @return true if the Script with the supplied scriptId is registered.
     */
    boolean containsScript(String canonicalName, String version);

    /**
     * Gets a List of ScriptIds represented as JSONArray.
     *
     * @return the registered ScriptIds.
     */
    JSONArray listScripts();

    /**
     * Gets the actual JSON script definition of JolokiaScript defined by its scriptId (canonicalName plus version).
     * If there are is more than one version managed by this JolokiaScriptManager instance and the passed in version is null, then the latest Version will be returned.
     *
     * @param canonicalName
     * @param version
     *
     * @return the matching JolokiaScript Definition as a String or an empty result {} if no script with the supplied scriptId is registered.
     */
    JSONObject scriptDefinition(String canonicalName, String version);

    /**
     * Executes the HealthCheck (HCS) or DataCollection Script (DCS) that is identified by its scriptId ((canonicalName plus version).
     * If there are is more than one version managed by this JolokiaScriptManager instance and the passed in version is null, then the latest Version will be executed.
     * @param canonicalName
     * @param version
     * @return the actual Result of the Script execution, which depends on the type of Script (HCS or DCS)
     */
    JSONObject executeScript(String canonicalName, String version);

    /**
     * Executes the HealthCheck (HCS) or DataCollection Script (DCS) that is identified by its scriptId ((canonicalName plus version).
     * This method allows to pass in overrides as a stringified JSONObject for individual (default) parameters defined in the script's vars-section.
     * Note that properties that are not part of the vars section get stripped out for security reasons.
     *
     * @param canonicalName
     * @param version
     * @param parameterJSONAsString
     * @return the actual Result of the Script execution, which depends on the type of Script (HCS or DCS)
     */
    JSONObject executeScriptWithParams(String canonicalName, String version, String parameterJSONAsString);
}
