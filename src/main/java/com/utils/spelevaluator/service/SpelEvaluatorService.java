package com.utils.spelevaluator.service;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpelEvaluatorService {

    public Map<String, Object> evaluate(Map<String, Object> body) {

        Map<String, Object> response = new HashMap<>();

        String expression = (String) body.get("expression");
        Object data = body.get("data");

        if (expression == null || expression.isBlank()) {
            response.put("error", "Expression is empty");
            return response;
        }

        if (data == null) {
            response.put("error", "Data is missing");
            return response;
        }

        StandardEvaluationContext ctx = new StandardEvaluationContext(data);
        ctx.setPropertyAccessors(List.of(
                new MapAccessor(),
                new ReflectivePropertyAccessor()
        ));

        try {
            Object result = new SpelExpressionParser()
                    .parseExpression(expression)
                    .getValue(ctx);

            response.put("result", result);
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }

        return response;
    }
}