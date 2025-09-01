package com.escuelaing.arep.http;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestTest {

    @Test
    @DisplayName("Request should parse query string into params")
    void getValues_should_returnQueryParam_when_present() {
        // Arrange
        Map<String, String> headers = new HashMap<>();
        headers.put("host", "localhost");

        // Act
        Request req = new Request("GET", "/path?name=Juan&msg=Hola%20Mundo", headers);

        // Assert
        assertEquals("Juan", req.getValues("name"));
        assertEquals("Hola Mundo", req.getValues("msg"));
        assertEquals("/path", req.getPath());
        assertEquals("GET", req.getMethod());
        assertEquals("localhost", req.getHeader("host"));
        assertTrue(req.getQueryParams().containsKey("name"));
    }

    @Test
    @DisplayName("Request should return empty string for missing param")
    void getValues_should_returnEmpty_when_paramMissing() {
        // Arrange & Act
        Request req = new Request("GET", "/path", Map.of());

        // Assert
        assertEquals("", req.getValues("nope"));
    }
}
