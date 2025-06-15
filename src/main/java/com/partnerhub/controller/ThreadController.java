package com.partnerhub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Thread", description = "Thread information endpoints")
@RequestMapping("/thread")
public class ThreadController {

    @Operation(
            summary = "Get current thread name and info",
            description = "Returns the current thread name and details as a string. Useful for debugging and tracing thread usage in the backend."
    )
    @GetMapping("/name")
    public String getThreadName() {
        return Thread.currentThread().toString();
    }
}
