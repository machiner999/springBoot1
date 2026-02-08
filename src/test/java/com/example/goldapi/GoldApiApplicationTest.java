package com.example.goldapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GoldApiApplicationTest {

    @Test
    void hasSpringBootApplicationAnnotation() {
        assertTrue(GoldApiApplication.class.isAnnotationPresent(SpringBootApplication.class));
    }
}
