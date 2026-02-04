package com.utils.spelevaluator.controller;

import com.utils.spelevaluator.service.SpelEvaluatorService;
import com.utils.spelevaluator.service.SpelPathFinderService;
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
@RequestMapping("/spel")
public class SpelController {

    private final SpelEvaluatorService evaluator;
    private final SpelPathFinderService finder;

    public SpelController(
            SpelEvaluatorService evaluator,
            SpelPathFinderService finder
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
}