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

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.jolokia.extra.addon.scripting.eval.Context;
import org.jolokia.extra.addon.scripting.eval.EvalResult;
import org.jolokia.extra.addon.scripting.eval.EvaluationException;
import org.jolokia.extra.addon.scripting.eval.ExpressionEvaluator;

/**
 * Jexl specific implementation of the ExpressionEvaluator API.
 *
 * TODO make Jexl Expression cache, jexl modes and security configurable through the use of ScriptConfiguration
 */
public class JexlEvaluator implements ExpressionEvaluator {
    protected JexlEngine jexl;

    public JexlEvaluator() {
        JexlEngine jexl = new JexlEngine();
        jexl.setStrict(true);
        jexl.setSilent(false);
        this.jexl = jexl;
    }

    public org.jolokia.extra.addon.scripting.eval.Expression createExpression(String scriptlet) throws EvaluationException {
        Expression expression = jexl.createExpression(scriptlet);
        try {
            return new org.jolokia.extra.addon.scripting.eval.jexl.JexlExpression(scriptlet, expression);
        } catch (Exception e) {
            throw new EvaluationException(e.getMessage(), e.getCause());
        }
    }

    public EvalResult evaluate(String scriptlet, Context context) {
        Expression expression = jexl.createExpression(scriptlet);
        JexlContext jc = new MapContext(context);
        return evaluate(expression, jc);
    }

    public EvalResult evaluate(org.jolokia.extra.addon.scripting.eval.Expression expression, Context context) {
        JexlContext jc = new MapContext(context);
        Expression jexlExpression = (Expression) expression.getInstance();
        return evaluate(jexlExpression, jc);
    }

    protected EvalResult evaluate(Expression expression, JexlContext context) {
        try {
            return new EvalResult(expression.evaluate(context));
        } catch (Exception e) {
            return new EvalResult(new EvaluationException(e.getMessage(), e.getCause()));
        }
    }

}
