package com.utils.spelevaluator.controller;

import com.utils.spelevaluator.service.JsonPathEvaluatorService;
import com.utils.spelevaluator.service.JsonPathFinderService;
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
@RequestMapping("/jsonpath")
public class JsonPathController {

    private final JsonPathEvaluatorService evaluator;
    private final JsonPathFinderService finder;

    public JsonPathController(
            JsonPathEvaluatorService evaluator,
            JsonPathFinderService finder
    ) {
        this.evaluator = evaluator;
        this.finder = finder;
    }

    @PostMapping("/evaluate")
    public Map<String, Object> evaluate(@RequestBody Map<String, Object> body) {
        return evaluator.evaluate(body);
    }

    @PostMapping("/find")
    public Map<String, Object> find(@RequestBody Map<String, Object> body) {
        return finder.find(body);
    }

    @PostMapping("/find-smart")
    public Map<String, Object> findSmart(@RequestBody Map<String, Object> body) {
        return finder.findSmart(body);
    }
}