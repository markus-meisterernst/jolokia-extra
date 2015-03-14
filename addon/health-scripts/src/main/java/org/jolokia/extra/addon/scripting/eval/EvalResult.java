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
 * Evaluation Result Wrapper for ExpressionEvaluator.
 */
public class EvalResult {
    private Object value;
    private Exception exception;

    protected EvalResult() {}
    public EvalResult(Object value) {
        this.value = value;
    }

    public EvalResult(Exception exception) {
        this.exception = exception;
    }

    public boolean isSuccessful() {
        return exception == null;
    }

    public boolean isFailure() {
        return !isSuccessful();
    }

    public Exception getException() {
        return exception;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "EvalResult{" +
                (value != null ? ("isSuccessful="+ isSuccessful() +", value=" + value)
                                : ("isFailure="+ isFailure() + ", exception=" + exception))
                + '}';
    }

    protected void setValue(Object value) {
        this.value = value;
    }

    protected void setException(Exception exception) {
        this.exception = exception;
    }
}
