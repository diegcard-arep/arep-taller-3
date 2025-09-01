package com.escuelaing.arep.utils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClassScannerTest {

    @Test
    @DisplayName("findRestControllers should find controllers in controllers package")
    void findRestControllers_should_returnControllers_when_packageHasRestControllers() {
        // Arrange
        String packageName = "com.escuelaing.arep.controllers";

        // Act
        List<Class<?>> result = ClassScanner.findRestControllers(packageName);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty(), "Debe encontrar al menos un controlador anotado");
        assertTrue(result.stream().anyMatch(c -> c.getName().endsWith("HelloController")));
        assertTrue(result.stream().anyMatch(c -> c.getName().endsWith("GreetingController")));
    }
}