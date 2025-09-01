package com.escuelaing.arep.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseTest {

    @Test
    @DisplayName("Response should support fluent configuration")
    void response_should_configureFluently() {
        // Arrange
        Response resp = new Response();

        // Act
        resp.status(404).type("application/json").header("X-Test", "1");

        // Assert
        assertEquals(404, resp.getStatusCode());
        assertEquals("application/json", resp.getContentType());
        assertEquals("1", resp.getHeaders().get("X-Test"));
    }
}
