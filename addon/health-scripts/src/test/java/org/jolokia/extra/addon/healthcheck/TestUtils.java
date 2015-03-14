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
package org.jolokia.extra.addon.healthcheck;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * TestUtils for shared test methods.
 */
public class TestUtils {
    public static JSONParser jsonParser = new JSONParser();

    public static JSONObject readJson(String filename) {
        InputStream is = TestUtils.class.getClassLoader().getResourceAsStream(filename);

        try {
           return (JSONObject) jsonParser.parse(new InputStreamReader(is));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("filename '" + filename + "' cannot be loaded or is not a valid json file.", e);
        }
    }
}