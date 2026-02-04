//package com.utils.spelevaluator.controller;
//
//import org.springframework.context.expression.MapAccessor;
//import org.springframework.expression.spel.standard.SpelExpressionParser;
//import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
//import org.springframework.expression.spel.support.StandardEvaluationContext;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/evaluate")
//public class BackupController {
//
//
//
//
//
//    @PostMapping
//    public Map<String, Object> eval(@RequestBody Map<String, Object> body) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        String expression = (String) body.get("expression");
//        Object jsonData = body.get("data");
//
//        if (expression == null || expression.isBlank()) {
//            response.put("error", "Expression is empty.");
//            return response;
//        }
//
//        if (jsonData == null) {
//            response.put("error", "Data is missing.");
//            return response;
//        }
//
//        StandardEvaluationContext ctx = new StandardEvaluationContext(jsonData);
//        ctx.setPropertyAccessors(List.of(
//                new MapAccessor(),
//                new ReflectivePropertyAccessor()
//        ));
//
//        try {
//            Object result = new SpelExpressionParser()
//                    .parseExpression(expression)
//                    .getValue(ctx);
//
//            response.put("result", result);
//        }
//        catch (Exception e) {
//            response.put("error", e.getMessage());
//        }
//
//        return response;
//    }
//    @PostMapping("/find")
//    public Map<String, Object> findExpression(@RequestBody Map<String, Object> body) {
//        Object valueObj = body.get("value");
//        Object dataObj = body.get("data");
//        Map<String, Object> response = new HashMap<>();
//        if (valueObj == null) {
//            response.put("error", "Missing field: value");
//            return response;
//        }
//        if (dataObj == null) {
//            response.put("error", "Missing field: data");
//            return response;
//        }
//
//        String targetValue = valueObj.toString();
//        List<Map<String, String>> results = new ArrayList<>();
//        findMatches(dataObj, targetValue, "", "", results);
//        response.put("results", results);
//        return response;
//    }
//
//    @SuppressWarnings("unchecked")
//    private void findMatches(Object node, String target, String shortPath, String safePath,
//                             List<Map<String, String>> results) {
//
//        if (node instanceof Map<?, ?> mapNode) {
//            for (Map.Entry<?, ?> entry : mapNode.entrySet()) {
//                String key = entry.getKey().toString();
//                Object value = entry.getValue();
//                String newSafe = safePath + "['" + key + "']";
//                String newShort = shortPath.isEmpty() ? key : shortPath + "." + key;
//                addResultIfMatch(target, results, value, newShort, newSafe);
//            }
//        } else if (node instanceof List<?> listNode) {
//            for (int i = 0; i < listNode.size(); i++) {
//                Object value = listNode.get(i);
//                String newSafe = safePath + "[" + i + "]";
//                String newShort = shortPath + "[" + i + "]";
//                addResultIfMatch(target, results, value, newShort, newSafe);
//            }
//        }
//    }
//
//    private void addResultIfMatch(String target, List<Map<String, String>> results,
//                                  Object value, String newShort, String newSafe) {
//        if (value != null && target.equals(value.toString())) {
//            Map<String, String> found = new HashMap<>();
//            found.put("short", newShort);
//            found.put("safe", newSafe);
//            results.add(found);
//        }
//        if (value instanceof Map || value instanceof List) {
//            findMatchesSmartSpel(value, target, newShort, newSafe, results);
//        }
//    }
//
//    @PostMapping("/jsonpath/find")
//    public Map<String, Object> findJsonPath(@RequestBody Map<String, Object> body) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        Object valueObj = body.get("value");
//        Object dataObj = body.get("data");
//
//        if (valueObj == null) {
//            response.put("error", "Missing field: value");
//            return response;
//        }
//        if (dataObj == null) {
//            response.put("error", "Missing field: data");
//            return response;
//        }
//
//        String target = valueObj.toString();
//        List<String> results = new ArrayList<>();
//
//        findJsonPathMatches(dataObj, target, "$", results);
//
//        response.put("results", results);
//        return response;
//    }
//
//    @PostMapping("/jsonpath")
//    public Map<String, Object> evalJsonPath(@RequestBody Map<String, Object> body) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        Object pathObj = body.get("path");
//        Object dataObj = body.get("data");
//
//        if (pathObj == null || pathObj.toString().isBlank()) {
//            response.put("error", "JSONPath is missing or empty.");
//            return response;
//        }
//
//        if (dataObj == null) {
//            response.put("error", "JSON data is missing.");
//            return response;
//        }
//
//        String jsonPath = pathObj.toString();
//
//        try {
//            Object result = com.jayway.jsonpath.JsonPath.read(dataObj, jsonPath);
//            response.put("result", result);
//        }
//        catch (com.jayway.jsonpath.PathNotFoundException e) {
//            response.put("error", "JSONPath not found in JSON.");
//        }
//        catch (IllegalArgumentException e) {
//            response.put("error", "Invalid JSONPath syntax.");
//        }
//        catch (Exception e) {
//            response.put("error", "Error evaluating JSONPath: " + e.getMessage());
//        }
//
//        return response;
//    }
//
//
//
//
//
//
//    @PostMapping("/jsonpath/find-smart")
//    public Map<String, Object> findJsonPathSmart(@RequestBody Map<String, Object> body) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        Object valueObj = body.get("value");
//        Object dataObj = body.get("data");
//
//        if (valueObj == null || dataObj == null) {
//            response.put("error", "Missing value or data");
//            return response;
//        }
//
//        List<String> results = new ArrayList<>();
//
//        findJsonPathMatchesSmart(
//                dataObj,
//                valueObj.toString(),
//                "$",
//                results
//        );
//
//        response.put("results", results);
//        return response;
//    }
//
//
//
//
//    private String buildSpelSelector(Map<?, ?> element, int index) {
//
//        for (String field : ID_FIELDS) {
//            Object value = element.get(field);
//            if (value != null) {
//                return ".?[" + field + "=='" + value + "'][0]";
//            }
//        }
//
//        // fallback
//        return "[" + index + "]";
//    }
//
//
//    @SuppressWarnings("unchecked")
//    private void findMatchesSmartSpel(
//            Object node,
//            String target,
//            String path,
//            String safePath,
//            List<Map<String, String>> results
//    ) {
//
//        if (node instanceof Map<?, ?> map) {
//
//            for (Map.Entry<?, ?> entry : map.entrySet()) {
//                String key = entry.getKey().toString();
//                Object value = entry.getValue();
//
//                String newPath = path.isEmpty() ? key : path + "." + key;
//                String newSafe = safePath + "['" + key + "']";
//
//                if (value != null && target.equals(value.toString())) {
//                    Map<String, String> found = new HashMap<>();
//                    found.put("short", newPath);
//                    found.put("safe", newSafe);
//                    results.add(found);
//                }
//
//                if (value instanceof Map || value instanceof List) {
//                    findMatchesSmartSpel(value, target, newPath, newSafe, results);
//                }
//            }
//        }
//        else if (node instanceof List<?> list) {
//
//            for (int i = 0; i < list.size(); i++) {
//                Object element = list.get(i);
//
//                String selector = "[" + i + "]";
//                String safeSelector = "[" + i + "]";
//
//                if (element instanceof Map<?, ?> mapElement) {
//                    selector = buildSpelSelector(mapElement, i);
//                    safeSelector = selector; // SpEL safe == short aquí
//                }
//
//                String newPath = path + selector;
//                String newSafe = safePath + safeSelector;
//
//                if (element != null && target.equals(element.toString())) {
//                    Map<String, String> found = new HashMap<>();
//                    found.put("short", newPath);
//                    found.put("safe", newSafe);
//                    results.add(found);
//                }
//
//                if (element instanceof Map || element instanceof List) {
//                    findMatchesSmartSpel(element, target, newPath, newSafe, results);
//                }
//            }
//        }
//    }
//}