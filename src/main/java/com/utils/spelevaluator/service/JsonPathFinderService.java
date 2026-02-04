package com.utils.spelevaluator.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class JsonPathFinderService {

    private static final List<String> ID_FIELDS =
            List.of("id", "code", "key", "name", "type");

    /* =========================
       PUBLIC API
       ========================= */


    public String find(Map<String, Object> body) {
        Object valueObj = body.get("value");
        Object dataObj = body.get("data");

        if (valueObj == null || dataObj == null) {
            return "error: Missing value or data\n";
        }

        List<String> results = new ArrayList<>();
        findJsonPathMatches(dataObj, valueObj.toString(), "$", results);

        return toYaml(results);
    }

    private String toYaml(List<String> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("results:\n");
        for (String r : results) {
            // Agregamos comillas dobles y escapamos comillas internas
            sb.append("  - \"").append(r.replace("\"", "\\\"")).append("\"\n");
        }
        return sb.toString();
    }



    @SuppressWarnings("unchecked")
    private void findJsonPathMatches(
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
                    findJsonPathMatches(value, target, newPath, results);
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
                    findJsonPathMatches(element, target, newPath, results);
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