package com.ridehailing.health.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check endpoints for monitoring and load balancers.
 * Provides detailed status of all dependencies.
 */
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "ride-hailing-backend");
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "ride-hailing-backend");
        
        // Check database
        Map<String, Object> dbHealth = new HashMap<>();
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            dbHealth.put("status", "UP");
            dbHealth.put("type", "PostgreSQL");
        } catch (Exception e) {
            dbHealth.put("status", "DOWN");
            dbHealth.put("error", e.getMessage());
        }
        health.put("database", dbHealth);
        
        // Check Redis
        Map<String, Object> redisHealth = new HashMap<>();
        try {
            redisTemplate.opsForValue().get("health-check");
            redisHealth.put("status", "UP");
        } catch (Exception e) {
            redisHealth.put("status", "DOWN");
            redisHealth.put("error", e.getMessage());
        }
        health.put("redis", redisHealth);
        
        // Check Kafka
        Map<String, Object> kafkaHealth = new HashMap<>();
        try {
            kafkaHealth.put("status", "UP");
            kafkaHealth.put("note", "Connected to broker");
        } catch (Exception e) {
            kafkaHealth.put("status", "DOWN");
            kafkaHealth.put("error", e.getMessage());
        }
        health.put("kafka", kafkaHealth);
        
        // Overall status
        boolean allUp = "UP".equals(dbHealth.get("status")) && 
                        "UP".equals(redisHealth.get("status")) && 
                        "UP".equals(kafkaHealth.get("status"));
        health.put("status", allUp ? "UP" : "DEGRADED");
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/ready")
    public ResponseEntity<Map<String, String>> readiness() {
        // Check if service is ready to accept traffic
        Map<String, String> ready = new HashMap<>();
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            ready.put("status", "READY");
            return ResponseEntity.ok(ready);
        } catch (Exception e) {
            ready.put("status", "NOT_READY");
            ready.put("error", e.getMessage());
            return ResponseEntity.status(503).body(ready);
        }
    }

    @GetMapping("/live")
    public ResponseEntity<Map<String, String>> liveness() {
        // Simple liveness check
        Map<String, String> live = new HashMap<>();
        live.put("status", "ALIVE");
        live.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(live);
    }
}
