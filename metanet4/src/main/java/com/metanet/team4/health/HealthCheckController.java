package com.metanet.team4.health;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {
	
    private final IHealthCheckRepository healthCheckMapper;

    public HealthCheckController(IHealthCheckRepository healthCheckMapper) {
        this.healthCheckMapper = healthCheckMapper;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> healthStatus = new HashMap<>();
        healthStatus.put("status", "healthy");
        return ResponseEntity.ok(healthStatus);
    }
    
    @GetMapping("/db")
    public ResponseEntity<Map<String, String>> healthDBCheck() {
        Map<String, String> healthStatus = new HashMap<>();
        healthStatus.put("status", "healthy");

        try {
            int employeeCount = healthCheckMapper.getEmployeeCount();
            healthStatus.put("database", "connected");
            healthStatus.put("employee_count", String.valueOf(employeeCount));
        } catch (Exception e) {
            healthStatus.put("database", "error");
        }

        return ResponseEntity.ok(healthStatus);
    }
}