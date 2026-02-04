package com.utils.spelevaluator.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JsonPathEvaluatorService {

    public Map<String, Object> evaluate(Map<String, Object> body) {

        Map<String, Object> response = new HashMap<>();

        String path = (String) body.get("path");
        Object data = body.get("data");

        if (path == null || path.isBlank()) {
            response.put("error", "JSONPath is empty");
            return response;
        }

        try {
            Object result = com.jayway.jsonpath.JsonPath.read(data, path);
            response.put("result", result);
        }
        catch (Exception e) {
            response.put("error", e.getMessage());
        }

        return response;
    }
}