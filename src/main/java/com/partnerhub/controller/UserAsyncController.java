package com.partnerhub.controller;

import com.partnerhub.dto.UserRequestDTO;
import com.partnerhub.kafka.UserKafkaMessage;
import com.partnerhub.kafka.UserKafkaProducer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users/async")
public class UserAsyncController {

    private static final Logger log = LoggerFactory.getLogger(UserAsyncController.class);

    private final UserKafkaProducer kafkaProducer;
    private final StringRedisTemplate redisTemplate;

    public UserAsyncController(UserKafkaProducer kafkaProducer, StringRedisTemplate redisTemplate) {
        this.kafkaProducer = kafkaProducer;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/kafka")
    public ResponseEntity<Map<String, Object>> createUserAsync(
            @Valid @RequestBody UserRequestDTO userRequest
    ) {
        String requestId = UUID.randomUUID().toString();
        log.info("Received async user creation request. requestId={}, email={}", requestId, userRequest.getEmail());

        kafkaProducer.sendUserCreationRequest(requestId, userRequest);
        redisTemplate.opsForValue().set(requestId, "PENDING");

        log.info("Published user creation message to Kafka and set PENDING in Redis for requestId={}", requestId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "PENDING");
        response.put("requestId", requestId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/kafka/status/{requestId}")
    public ResponseEntity<Map<String, String>> getAsyncUserCreationStatus(
            @PathVariable String requestId
    ) {
        log.info("Received status check for async user creation. requestId={}", requestId);

        String status = redisTemplate.opsForValue().get(requestId);
        if (status == null) {
            log.warn("No status found in Redis for requestId={}", requestId);
            return ResponseEntity.notFound().build();
        }

        log.info("Returning status '{}' for requestId={}", status, requestId);

        Map<String, String> response = new HashMap<>();
        response.put("status", status);

        return ResponseEntity.ok(response);
    }
}