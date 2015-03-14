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
package org.jolokia.extra.addon.scripting.model;


import org.jolokia.extra.addon.scripting.exception.ValidationException;

/**
 * Util Class for Assertions.
 */
public class ModelAssertions {

    public static void assertNotNull(Object o, String assertionFailureText) throws ValidationException {
        if(o == null)
            createAndThrowValidationException(assertionFailureText);
    }

    public static void assertNotEmpty(Iterable<?> iterable, String assertionFailureText) {
        if(iterable != null && iterable.iterator().hasNext()) {
            return;
        }
        createAndThrowValidationException(assertionFailureText);
    }

    public static void assertNotEmpty(String string, String assertionFailureText) {
        if(string == null || string.trim().length() == 0) {
            createAndThrowValidationException(assertionFailureText);
        }
    }

    public static void assertTrue(boolean condition, String assertionFailureText) {
        if(!condition) {
            createAndThrowValidationException(assertionFailureText);
        }
    }

    public static void assertFalse(boolean condition, String assertionFailureText) {
        if(condition) {
            createAndThrowValidationException(assertionFailureText);
        }
    }

    protected static void createAndThrowValidationException(String assertionText) {
        throw new ValidationException(assertionText);
    }
}
