package com.example.gateway;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping(value = "/fallback", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> fallback() {
        return Mono.just(Map.of(
                "message", "Service temporarily unavailable. Please try again later.",
                "status", 503
        ));
    }
}




