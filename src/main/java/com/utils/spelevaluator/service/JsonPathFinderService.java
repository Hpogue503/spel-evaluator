package com.utils.spelevaluator.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JsonPathFinderService {

    private static final List<String> ID_FIELDS =
            List.of("id", "code", "key", "name", "type");

    /* =========================
       PUBLIC API
       ========================= */

    public Map<String, Object> find(Map<String, Object> body) {

        Map<String, Object> response = new HashMap<>();

        Object valueObj = body.get("value");
        Object dataObj = body.get("data");

        if (valueObj == null || dataObj == null) {
            response.put("error", "Missing value or data");
            return response;
        }

        List<String> results = new ArrayList<>();
        findJsonPathMatches(dataObj, valueObj.toString(), "$", results);

        response.put("results", results);
        return response;
    }

    public Map<String, Object> findSmart(Map<String, Object> body) {

        Map<String, Object> response = new HashMap<>();

        Object valueObj = body.get("value");
        Object dataObj = body.get("data");

        if (valueObj == null || dataObj == null) {
            response.put("error", "Missing value or data");
            return response;
        }

        List<String> results = new ArrayList<>();
        findJsonPathMatchesSmart(dataObj, valueObj.toString(), "$", results);

        response.put("results", results);
        return response;
    }

    /* =========================
       INTERNAL LOGIC
       ========================= */

    @SuppressWarnings("unchecked")
    private void findJsonPathMatches(
            Object node,
            String target,
            String path,
            List<String> results
    ) {

        if (node instanceof Map<?, ?> mapNode) {
            for (Map.Entry<?, ?> entry : mapNode.entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();

                String newPath = path + "." + key;

                if (value != null && target.equals(value.toString())) {
                    results.add(newPath);
                }

                if (value instanceof Map || value instanceof List) {
                    findJsonPathMatches(value, target, newPath, results);
                }
            }
        }
        else if (node instanceof List<?> listNode) {
            for (int i = 0; i < listNode.size(); i++) {
                Object value = listNode.get(i);
                String newPath = path + "[" + i + "]";

                if (value != null && target.equals(value.toString())) {
                    results.add(newPath);
                }

                if (value instanceof Map || value instanceof List) {
                    findJsonPathMatches(value, target, newPath, results);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void findJsonPathMatchesSmart(
            Object node,
            String target,
            String path,
            List<String> results
    ) {

        if (node instanceof Map<?, ?> map) {

            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();

                String newPath = path + jsonPathField(key);

                if (value != null && target.equals(value.toString())) {
                    results.add(newPath);
                }

                if (value instanceof Map || value instanceof List) {
                    findJsonPathMatchesSmart(value, target, newPath, results);
                }
            }
        }
        else if (node instanceof List<?> list) {

            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);

                String selector = "[" + i + "]";

                if (element instanceof Map<?, ?> mapElement) {
                    selector = buildSelector(mapElement, i);
                }

                String newPath = path + selector;

                if (element != null && target.equals(element.toString())) {
                    results.add(newPath);
                }

                if (element instanceof Map || element instanceof List) {
                    findJsonPathMatchesSmart(element, target, newPath, results);
                }
            }
        }
    }

    private String buildSelector(Map<?, ?> element, int index) {

        for (String field : ID_FIELDS) {
            Object value = element.get(field);
            if (value != null) {
                return "[?(@." + field + "=='" +
                        value.toString().replace("'", "\\'") +
                        "')]";
            }
        }

        return "[" + index + "]";
    }

    private static String jsonPathField(String fieldName) {
        if (fieldName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            return "." + fieldName;
        }
        return "['" + fieldName + "']";
    }
}