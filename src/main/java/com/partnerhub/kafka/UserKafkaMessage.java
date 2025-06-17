package com.partnerhub.kafka;

import com.partnerhub.dto.UserRequestDTO;

public class UserKafkaMessage {

    private String requestId;
    private UserRequestDTO user;

    public UserKafkaMessage() {} // Required for deserialization

    public UserKafkaMessage(String requestId, UserRequestDTO user) {
        this.requestId = requestId;
        this.user = user;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public UserRequestDTO getUser() {
        return user;
    }

    public void setUser(UserRequestDTO user) {
        this.user = user;
    }
}