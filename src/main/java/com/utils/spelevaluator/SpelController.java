package com.utils.spelevaluator;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@RestController
@RequestMapping("/evaluate")
public class SpelController {

    private final ObjectMapper mapper = new ObjectMapper();

    /** Evalúa una expresión SpEL sobre un JSON enviado */
    @PostMapping
    public Map<String, Object> eval(@RequestBody Map<String, Object> body) {
        String expression = (String) body.get("expression");
        Object data = body.get("data");

        Map<String, Object> response = new HashMap<>();
        if (expression == null || data == null) {
            response.put("error", "Missing 'expression' or 'data' in request");
            return response;
        }

        Map<String, Object> jsonMap;
        if (data instanceof Map) {
            jsonMap = (Map<String, Object>) data;
        } else {
            jsonMap = mapper.convertValue(data, Map.class);
        }

        StandardEvaluationContext ctx = new StandardEvaluationContext(jsonMap);
        ctx.setPropertyAccessors(List.of(new MapAccessor(), new ReflectivePropertyAccessor()));

        SpelExpressionParser parser = new SpelExpressionParser();
        try {
            Object result = parser.parseExpression(expression).getValue(ctx);
            response.put("result", result);
        } catch (Exception e) {
            String msg = e.getMessage();

            if (msg != null && msg.contains("EL1008E")) {
                msg = "The specified SpEL expression was not found. Try using the 'safe' notation. ";
            }
            response.put("error", msg);
        }
        return response;
    }

    /** Busca todas las expresiones que coincidan con un valor */
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
        // Continuar recursión solo si es mapa o lista
        if (value instanceof Map || value instanceof List) {
            findMatches(value, target, newShort, newSafe, results);
        }
    }
}