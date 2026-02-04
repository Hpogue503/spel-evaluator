package com.utils.spelevaluator.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpelPathFinderService {

    private static final List<String> ID_FIELDS =
            List.of("id", "code", "key", "name", "type");

    public Map<String, Object> find(Map<String, Object> body) {

        Map<String, Object> response = new HashMap<>();

        Object value = body.get("value");
        Object data = body.get("data");

        if (value == null || data == null) {
            response.put("error", "Missing value or data");
            return response;
        }

        List<Map<String, String>> results = new ArrayList<>();
        findMatchesSmartSpel(data, value.toString(), "", "", results);

        response.put("results", results);
        return response;
    }

    @SuppressWarnings("unchecked")
    private void findMatchesSmartSpel(
            Object node,
            String target,
            String path,
            String safePath,
            List<Map<String, String>> results
    ) {

        if (node instanceof Map<?, ?> map) {

            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();

                String newPath = path.isEmpty() ? key : path + "." + key;
                String newSafe = safePath + "['" + key + "']";

                if (value != null && target.equals(value.toString())) {
                    Map<String, String> found = new HashMap<>();

                    if (isSpelDotSafe(newPath)) {
                        found.put("short", newPath);
                    }

                    found.put("safe", newSafe); // safe siempre válido
                    results.add(found);
                }

                if (value instanceof Map || value instanceof List) {
                    findMatchesSmartSpel(value, target, newPath, newSafe, results);
                }
            }
        }
        else if (node instanceof List<?> list) {

            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);

                String selector = "[" + i + "]";
                String safeSelector = "[" + i + "]";

                if (element instanceof Map<?, ?> mapElement) {
                    selector = buildSpelSelector(mapElement, i);
                    safeSelector = selector; // SpEL safe == short aquí
                }

                String newPath = path + selector;
                String newSafe = safePath + safeSelector;

                if (element != null && target.equals(element.toString())) {
                    Map<String, String> found = new HashMap<>();

                    if (isSpelDotSafe(newPath)) {
                        found.put("short", newPath);
                    }

                    found.put("safe", newSafe); // safe siempre válido
                    results.add(found);
                }

                if (element instanceof Map || element instanceof List) {
                    findMatchesSmartSpel(element, target, newPath, newSafe, results);
                }
            }
        }
    }

        private String buildSpelSelector(Map<?, ?> element, int index) {

        for (String field : ID_FIELDS) {
            Object value = element.get(field);
            if (value != null) {
                return ".?[" + field + "=='" + value + "'][0]";
            }
        }

        return "[" + index + "]";
    }
    private boolean isSpelDotSafe(String path) {
        if (path == null || path.isBlank()) return true;

        String[] parts = path.split("\\.");
        for (String part : parts) {
            // ignora selectores [?] y [0]
            if (part.contains("[") || part.contains("?")) {
                continue;
            }
            if (!part.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                return false;
            }
        }
        return true;
    }
}