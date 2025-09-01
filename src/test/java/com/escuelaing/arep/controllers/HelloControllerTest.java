package com.escuelaing.arep.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HelloControllerTest {

    @Test
    @DisplayName("index should return greeting message")
    void index_should_returnGreetingMessage() {
        // Arrange
        HelloController ctrl = new HelloController();

        // Act
        String result = ctrl.index();

        // Assert
        assertEquals("Greetings from MicroSpringBoot!", result);
    }
}