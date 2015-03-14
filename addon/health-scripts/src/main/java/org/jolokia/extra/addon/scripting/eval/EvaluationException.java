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
package org.jolokia.extra.addon.scripting.eval;

/**
 * Wrapper Class for exceptions that are kept in an EvalResult in case of Expression evaluation exceptions.
 *
 * @see org.jolokia.extra.addon.scripting.eval.EvalResult
 * @see org.jolokia.extra.addon.scripting.eval.BooleanEvalResult
 *
 */
public class EvaluationException extends RuntimeException {

    public EvaluationException() {
    }

    public EvaluationException(String s) {
        super(s);
    }

    public EvaluationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EvaluationException(Throwable throwable) {
        super(throwable);
    }
}
