package com.utils.spelevaluator;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/evaluate")
public class SpelController {


    @PostMapping
    public Map<String, Object> eval(@RequestBody Object data) {
        Map<String, Object> response = new HashMap<>();

        if (!(data instanceof Map<?, ?> bodyMap) || !bodyMap.containsKey("expression")) {
            response.put("error", "Missing 'expression' or invalid request format.");
            return response;
        }
        String expression = bodyMap.get("expression").toString();
        Object jsonData = bodyMap.get("data");

        if (expression == null || expression.trim().isEmpty()) {
            response.put("error", "Expression is empty.");
            return response;
        }
        if (jsonData == null) {
            response.put("error", "Data is missing.");
            return response;
        }

        Object root = jsonData;

        StandardEvaluationContext ctx = new StandardEvaluationContext(root);
        ctx.setPropertyAccessors(List.of(new MapAccessor(), new ReflectivePropertyAccessor()));

        SpelExpressionParser parser = new SpelExpressionParser();
        try {
            Object result = parser.parseExpression(expression).getValue(ctx);
            response.put("result", result);
        } catch (Exception e) {
            String msg = e.getMessage();

            if (msg != null && msg.contains("EL1008E")) {
                msg = "Expression not found in JSON. Try a safe path or check your keys.";
            }
            response.put("error", msg);
        }
        try {
            Object result = parser.parseExpression(expression).getValue(ctx);
            response.put("result", result);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (expression == null || expression.isBlank()) {
                msg = "Expression is empty. Please provide a valid SpEL expression.";
            }
            else if (data == null) {
                msg = "JSON data is missing. Please provide valid JSON.";
            }
            else if (msg != null && msg.contains("EL1008E")) {
                msg = "Expression not found in JSON. Try a safe path or check your keys.";
            }
            else if (msg != null && msg.contains("EL1042E")) {
                msg = "Invalid SpEL syntax. Please check your expression formatting.";
            }
            else if (msg != null && msg.contains("EL1007E")) {
                msg = "Expression cannot be evaluated because a parent object is null. Check your path or use safe navigation.";
            }
            else if (msg != null) {
                msg = "Error evaluating expression: " + msg;
            } else {
                msg = "Unknown error evaluating expression.";
            }
            response.put("error", msg);
        }
        return response;
    }

    @PostMapping("/find")
    public Map<String, Object> findExpression(@RequestBody Map<String, Object> body) {
        Object valueObj = body.get("value");
        Object dataObj = body.get("data");
        Map<String, Object> response = new HashMap<>();
        if (valueObj == null) {
            response.put("error", "Missing field: value");
            return response;
        }
        if (dataObj == null) {
            response.put("error", "Missing field: data");
            return response;
        }

        String targetValue = valueObj.toString();
        List<Map<String, String>> results = new ArrayList<>();
        findMatches(dataObj, targetValue, "", "", results);
        response.put("results", results);
        return response;
    }

    @SuppressWarnings("unchecked")
    private void findMatches(Object node, String target, String shortPath, String safePath,
                             List<Map<String, String>> results) {

        if (node instanceof Map<?, ?> mapNode) {
            for (Map.Entry<?, ?> entry : mapNode.entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                String newSafe = safePath + "['" + key + "']";
                String newShort = shortPath.isEmpty() ? key : shortPath + "." + key;
                addResultIfMatch(target, results, value, newShort, newSafe);
            }
        } else if (node instanceof List<?> listNode) {
            for (int i = 0; i < listNode.size(); i++) {
                Object value = listNode.get(i);
                String newSafe = safePath + "[" + i + "]";
                String newShort = shortPath + "[" + i + "]";
                addResultIfMatch(target, results, value, newShort, newSafe);
            }
        }
    }

    private void addResultIfMatch(String target, List<Map<String, String>> results,
                                  Object value, String newShort, String newSafe) {
        if (value != null && target.equals(value.toString())) {
            Map<String, String> found = new HashMap<>();
            found.put("short", newShort);
            found.put("safe", newSafe);
            results.add(found);
        }
        if (value instanceof Map || value instanceof List) {
            findMatches(value, target, newShort, newSafe, results);
        }
    }
}