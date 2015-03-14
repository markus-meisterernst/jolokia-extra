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

package org.jolokia.extra.addon.scripting.eval.jexl;

import org.jolokia.extra.addon.scripting.eval.Expression;

/**
 * Jexl specific Expression Wrapper Implementation.
 *
 * @see org.jolokia.extra.addon.scripting.eval.Expression
 */
public class JexlExpression implements Expression {
    private org.apache.commons.jexl2.Expression jexlExpression;
    private String expressionString;

    public JexlExpression(String expressionString, org.apache.commons.jexl2.Expression jexlExpression) {
        this.jexlExpression = jexlExpression;
        this.expressionString = expressionString;
    }

    public String asString() {
        return expressionString;
    }

    public org.apache.commons.jexl2.Expression getInstance() {
        return jexlExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JexlExpression)) return false;

        JexlExpression that = (JexlExpression) o;

        if (!expressionString.equals(that.expressionString)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return expressionString.hashCode();
    }

    @Override
    public String toString() {
        return "JexlExpression{'" + expressionString + "'}'";
    }
}
