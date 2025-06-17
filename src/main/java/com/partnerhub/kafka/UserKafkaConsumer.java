package com.partnerhub.kafka;

import com.partnerhub.domain.User;
import com.partnerhub.dto.UserRequestDTO;
import com.partnerhub.mapper.UserMapper;
import com.partnerhub.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserKafkaConsumer.class);

    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final UserMapper userMapper;

    public UserKafkaConsumer(UserService userService, StringRedisTemplate redisTemplate, UserMapper userMapper) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
        this.userMapper = userMapper;
    }

    @KafkaListener(topics = "user-creation-topic", groupId = "user-creation-group")
    public void consume(UserKafkaMessage message) {
        String reqId = message.getRequestId();
        String email = message.getUser() != null ? message.getUser().getEmail() : null;
        log.info("Consuming user creation message from Kafka. requestId={}, email={}", reqId, email);
        try {
            UserRequestDTO requestDTO = message.getUser();
            User user = userMapper.toEntity(requestDTO);
            userService.createUser(user);
            redisTemplate.opsForValue().set(reqId, "SUCCESS");
            log.info("User created successfully from Kafka. requestId={}", reqId);
        } catch (Exception ex) {
            redisTemplate.opsForValue().set(reqId, "FAILED");
            log.error("Failed to create user from Kafka. requestId={}, error={}", reqId, ex.getMessage(), ex);
        }
    }
}