package com.escuelaing.arep.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GreetingControllerTest {

    @Test
    @DisplayName("greeting should return Hola + name when provided")
    void greeting_should_returnHolaName_when_nameProvided() {
        // Arrange
        GreetingController ctrl = new GreetingController();

        // Act
        String result = ctrl.greeting("Carlos");

        // Assert
        assertEquals("Hola Carlos", result);
    }

    @Test
    @DisplayName("count should increment and return Count: N")
    void count_should_incrementCounter_when_called() {
        // Arrange
        GreetingController ctrl = new GreetingController();

        // Act & Assert
        String r1 = ctrl.count();
        String r2 = ctrl.count();

        assertTrue(r1.matches("Count: \\d+"));
        assertTrue(r2.matches("Count: \\d+"));
        int v1 = Integer.parseInt(r1.split(": ")[1]);
        int v2 = Integer.parseInt(r2.split(": ")[1]);
        assertTrue(v2 > v1);
    }
}