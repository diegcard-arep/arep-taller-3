package com.escuelaing.arep;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpServerTest {

    @ParameterizedTest(name = "mime({0}) -> {1}")
    @CsvSource({
        "index.html, text/html",
        "main.htm, text/html",
        "styles.css, text/css",
        "app.js, application/javascript",
        "img.png, image/png",
        "photo.jpg, image/jpeg",
        "icon.gif, image/gif",
        "vector.svg, image/svg+xml",
        "favicon.ico, image/x-icon",
        "readme.txt, text/plain",
        "file.unknown, application/octet-stream"
    })
    @DisplayName("getSimpleMimeType debe resolver tipos conocidos")
    void getSimpleMimeType_should_resolveKnownTypes(String fileName, String expected) throws Exception {
        // Arrange
        HttpServer server = new HttpServer();
        Method method = HttpServer.class.getDeclaredMethod("getSimpleMimeType", String.class);
        method.setAccessible(true);
        
        // Act
        String mime = (String) method.invoke(server, fileName);
        
        // Assert
        assertEquals(expected, mime);
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 400, 404, 500, 123})
    @DisplayName("getStatusMessage debe retornar mensaje para código")
    void getStatusMessage_should_returnMessage_forCodes(int code) throws Exception {
        // Arrange
        HttpServer server = new HttpServer();
        Method method = HttpServer.class.getDeclaredMethod("getStatusMessage", int.class);
        method.setAccessible(true);

        // Act
        String msg = (String) method.invoke(server, code);

        // Assert
        switch (code) {
            case 200 -> assertEquals("OK", msg);
            case 400 -> assertEquals("Bad Request", msg);
            case 404 -> assertEquals("Not Found", msg);
            case 500 -> assertEquals("Internal Server Error", msg);
            default -> assertEquals("Unknown", msg);
        }
    }
}