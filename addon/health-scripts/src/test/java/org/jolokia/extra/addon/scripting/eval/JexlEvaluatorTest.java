package org.jolokia.extra.addon.scripting.eval;

import org.jolokia.extra.addon.healthcheck.TestUtils;
import org.jolokia.extra.addon.scripting.eval.jexl.JexlEvaluator;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for JexlEvaluator to test the actual Expression execution as defined in Variables, Conditionals and Assertions.
 */
public class JexlEvaluatorTest {

    private ExpressionEvaluator evaluator = new JexlEvaluator();

    @Test
    public void evaluate_version_of_tomcat_successful() {
        JSONObject versionJson = TestUtils.readJson("jolokia-requests/version_raw_result.json");
        String jexlExpression = "version.info.product.toUpperCase()  == 'TOMCAT' && version.info.version == '7.0.57'";

        Object var = versionJson.get("value");
        Context context = new Context();
        context.put("version", var);

        EvalResult result = evaluator.evaluate(jexlExpression, context);
        System.out.println(result);

        Assert.assertTrue(result.isSuccessful());
        Assert.assertTrue(result.getValue() != null);
        Assert.assertTrue(result.getValue() instanceof Boolean);
        Assert.assertTrue((Boolean) result.getValue());
    }

    @Test
    public void evaluate_version_of_tomcat_successful_using_cached_expression() {
        JSONObject versionJson = TestUtils.readJson("jolokia-requests/version_raw_result.json");
        String jexlExpression = "version.info.product.toUpperCase()  == 'TOMCAT' && version.info.version == '7.0.57'";

        Object var = versionJson.get("value");
        Context context = new Context();
        context.put("version", var);

        Expression expression = evaluator.createExpression(jexlExpression);

        EvalResult result = evaluator.evaluate(expression, context);
        System.out.println(result);

        Assert.assertTrue(result.isSuccessful());
        Assert.assertTrue(result.getValue() != null);
        Assert.assertTrue(result.getValue() instanceof Boolean);
        Assert.assertTrue((Boolean) result.getValue());
    }

    @Test
    public void evaluate_version_of_tomcat_expression_returns_false() {
        JSONObject versionJson = TestUtils.readJson("jolokia-requests/version_raw_result.json");
        String expression = "version.info.product.toUpperCase()  == 'TOMCAT' && version.info.version == '7.0.58'";

        Object var = versionJson.get("value");
        Context context = new Context();
        context.put("version", var);

        EvalResult result = evaluator.evaluate(expression, context);
        System.out.println(result);

        Assert.assertTrue(result.isSuccessful());
        Assert.assertTrue(result.getValue() != null);
        Assert.assertTrue(result.getValue() instanceof Boolean);
        Assert.assertFalse((Boolean) result.getValue());
    }

    @Test
    public void evaluate_version_of_tomcat_throws_exception() {
        JSONObject versionJson = TestUtils.readJson("jolokia-requests/version_raw_result.json");
        String jexlExpression = "version.info.product.methodDoesNotExist()  == 'TOMCAT' && version.info.version == '7.0.58'";

        Object var = versionJson.get("value");
        Context context = new Context();
        context.put("version", var);

        EvalResult result = evaluator.evaluate(jexlExpression, context);
        System.out.println(result);

        Assert.assertTrue(result.isFailure());
        Assert.assertTrue(result.getValue() == null);
        Assert.assertTrue(result.getException() != null);
        Assert.assertTrue(result.getException() instanceof EvaluationException);
        result.getException().printStackTrace();
    }

}
