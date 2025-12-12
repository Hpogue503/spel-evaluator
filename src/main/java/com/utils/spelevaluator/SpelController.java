package com.utils.spelevaluator;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@RestController
@RequestMapping("/evaluate")
public class SpelController {

    private final ObjectMapper mapper = new ObjectMapper();

    // ===========================
    //     1) EVALUATE
    // ===========================
    // POST /evaluate/{expression}

    @PostMapping
    public Object eval(
            @RequestParam("exp") String expression,
            @RequestBody Map<String, Object> jsonBody) {

        // Crear el contexto para evaluar SpEL sobre cualquier estructura
        StandardEvaluationContext ctx = new StandardEvaluationContext(jsonBody);
        ctx.setPropertyAccessors(List.of(new MapAccessor()));

        // Ejecutar la expresión
        SpelExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(expression).getValue(ctx);
    }

    // ===========================
    //     2) GET EXPRESSION
    // ===========================
    // POST /evaluate/get-expression/{value}
    @PostMapping("/get-expression/{value}")
    public Map<String, Object> getExpression(
            @PathVariable String value,
            @RequestBody Object jsonBody) {

        List<Map<String, String>> results = new ArrayList<>();

        // NOTA: shortPath y safePath empiezan vacíos ("")
        findMatches(jsonBody, value, "", "", results);

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);

        return response;
    }

    // ===========================
    //     RECURSIVE SEARCH
    // ===========================
    @SuppressWarnings("unchecked")
    private void findMatches(Object node,
                             String target,
                             String shortPath,
                             String safePath,
                             List<Map<String, String>> results) {

        if (node instanceof Map<?, ?> mapNode) {

            for (Map.Entry<?, ?> entry : mapNode.entrySet()) {

                String key = entry.getKey().toString();
                Object value = entry.getValue();

                // Ejemplo:  shortPath = "nested.model"
                String newShort = shortPath.isEmpty() ? key : shortPath + "." + key;

                // Ejemplo: safePath = "['nested']['model']"
                String newSafe = safePath + "['" + key + "']";

                processNode(target, results, value, newShort, newSafe);
            }

        } else if (node instanceof List<?> listNode) {

            for (int i = 0; i < listNode.size(); i++) {

                Object value = listNode.get(i);

                String newShort = shortPath + "[" + i + "]";
                String newSafe = safePath + "[" + i + "]";

                processNode(target, results, value, newShort, newSafe);
            }
        }
    }

    private void processNode(String target,
                             List<Map<String, String>> results,
                             Object value,
                             String newShort,
                             String newSafe) {

        // Coincidencia exacta
        if (value != null && target.equals(value.toString())) {
            Map<String, String> found = new HashMap<>();
            found.put("short", newShort);
            found.put("safe", newSafe);
            results.add(found);
        }

        // Recursión profunda
        findMatches(value, target, newShort, newSafe, results);
    }
}