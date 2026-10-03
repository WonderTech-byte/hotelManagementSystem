package org.sammy.hotelmanagement.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Slf4j
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Hotel Management System");
        
        log.info("Health check performed");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> healthDetailed() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        
        Map<String, Object> application = new HashMap<>();
        application.put("name", "Hotel Management System");
        application.put("encoding", System.getProperty("file.encoding"));
        application.put("java_version", System.getProperty("java.version"));
        
        Map<String, Object> system = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        system.put("total_memory_mb", runtime.totalMemory() / (1024 * 1024));
        system.put("free_memory_mb", runtime.freeMemory() / (1024 * 1024));
        system.put("max_memory_mb", runtime.maxMemory() / (1024 * 1024));
        system.put("available_processors", runtime.availableProcessors());
        
        response.put("application", application);
        response.put("system", system);
        
        log.info("Detailed health check performed");
        return ResponseEntity.ok(response);
    }
}
