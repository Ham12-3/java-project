package com.example.authservice.web;

import com.example.authservice.service.OperationsMetricsService;
import com.example.authservice.web.dto.OpsMetricsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ops")
public class OperationsController {

    private final OperationsMetricsService operationsMetricsService;

    public OperationsController(OperationsMetricsService operationsMetricsService) {
        this.operationsMetricsService = operationsMetricsService;
    }

    @GetMapping("/metrics")
    public OpsMetricsResponse metrics() {
        return operationsMetricsService.fetchMetrics();
    }
}
