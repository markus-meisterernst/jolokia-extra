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

package org.jolokia.extra.addon.scripting.eval;

import org.jolokia.extra.addon.scripting.eval.jexl.JexlEvaluator;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests to see how to formulate common JEXL operations.
 */
public class ArithmeticJexlEvaluatorTest {
    private ExpressionEvaluator evaluator = new JexlEvaluator();

    @Test
    public void test_division_resulting_in_double() {
        String jexlExpression = "new('java.lang.Double', heap.used) div new('java.lang.Double',heap.max)";

        JSONObject heap = new JSONObject();
        heap.put("used", 1000);
        heap.put("max", 10000);
        Context context = new Context();
        //context.put("heapMemoryUsageThreshold", 0.1);
        context.put("heap", heap);

        EvalResult result = evaluator.evaluate(jexlExpression, context);
        assertTrue(result.isSuccessful());
        assertTrue(result.getValue().equals(0.1D));
    }

    @Test
    public void test_division_resulting_in_failed_assertion() {
        String jexlExpression = "new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) < heapMemoryUsageThreshold";
        JSONObject heap = new JSONObject();
        heap.put("used", 1000);
        heap.put("max", 10000);
        Context context = new Context();
        context.put("heapMemoryUsageThreshold", 0.1);
        context.put("heap", heap);

        EvalResult result = evaluator.evaluate(jexlExpression, context);
        assertTrue(result.isSuccessful());
        assertFalse((Boolean)result.getValue());
    }

    @Test
    public void test_division_resulting_in_assertion_exception() {
        String jexlExpression = "new('java.lang.Double', heap.used) div 0 <= heapMemoryUsageThreshold";
        JSONObject heap = new JSONObject();
        heap.put("used", 1000);
        heap.put("max", 10000);
        Context context = new Context();
        context.put("heapMemoryUsageThreshold", 0.1);
        context.put("heap", heap);

        EvalResult result = evaluator.evaluate(jexlExpression, context);
        assertTrue(result.isFailure());
        assertNull(result.getValue());
        assertNotNull(result.getException());
        assertTrue(result.getException() instanceof EvaluationException);
        System.out.println(result.getException());
    }
}
