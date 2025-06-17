package com.partnerhub.kafka;

import com.partnerhub.dto.UserRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(UserKafkaProducer.class);

    private final KafkaTemplate<String, UserKafkaMessage> kafkaTemplate;

    public UserKafkaProducer(KafkaTemplate<String, UserKafkaMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreationRequest(String requestId, UserRequestDTO userRequest) {
        UserKafkaMessage message = new UserKafkaMessage(requestId, userRequest);
        log.info("Sending user creation message to Kafka. requestId={}, email={}", requestId, userRequest.getEmail());
        kafkaTemplate.send("user-creation-topic", requestId, message);
    }
}