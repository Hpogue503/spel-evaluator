package com.utils.spelevaluator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.expression.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SpelEvaluatorService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SpelExpressionParser parser = new SpelExpressionParser();

    public Map<String, Object> evaluate(Map<String, Object> body) {

        Map<String, Object> response = new HashMap<>();

        String expression = (String) body.get("expression");
        Object data = body.get("data");

        if (expression == null || expression.isBlank()) {
            response.put("error", "Expression is empty");
            return response;
        }

        try {
            JsonNode root = (data == null)
                    ? MissingNode.getInstance()
                    : objectMapper.valueToTree(data);

            StandardEvaluationContext context =
                    new StandardEvaluationContext(root);

            context.addPropertyAccessor(new JsonNodePropertyAccessor());

            Object result = parser
                    .parseExpression(expression)
                    .getValue(context);

            response.put("result", result);

        } catch (Exception e) {
            response.put("error", e.getMessage());
        }

        return response;
    }

    /**
     * PropertyAccessor seguro para JsonNode
     */
    static class JsonNodePropertyAccessor implements PropertyAccessor {

        @Override
        public Class<?>[] getSpecificTargetClasses() {
            return new Class<?>[]{JsonNode.class};
        }

        @Override
        public boolean canRead(
                EvaluationContext context,
                Object target,
                String name
        ) {
            return target instanceof JsonNode;
        }

        @Override
        public TypedValue read(
                EvaluationContext context,
                Object target,
                String name
        ) {
            JsonNode node = (JsonNode) target;
            JsonNode value = node.get(name);

            if (value == null || value.isMissingNode() || value.isNull()) {
                return TypedValue.NULL;
            }

            if (value.isValueNode()) {
                return new TypedValue(value.asText());
            }

            return new TypedValue(value);
        }

        @Override
        public boolean canWrite(
                EvaluationContext context,
                Object target,
                String name
        ) {
            return false;
        }

        @Override
        public void write(
                EvaluationContext context,
                Object target,
                String name,
                Object newValue
        ) {
            throw new UnsupportedOperationException();
        }
    }
}