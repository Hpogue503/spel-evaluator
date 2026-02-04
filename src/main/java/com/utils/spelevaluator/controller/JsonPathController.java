package com.utils.spelevaluator.controller;

import com.utils.spelevaluator.service.JsonPathEvaluatorService;
import com.utils.spelevaluator.service.JsonPathFinderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public String find(@RequestBody Map<String, Object> body) {
        return finder.find(body);
    }
}