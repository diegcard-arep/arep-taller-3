package com.escuelaing.arep;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class HttpServerTest {

    @Test
    void testGetWeatherInfoReturnsJsonWithBogota() throws Exception {
        HttpServer server = new HttpServer();
        Method method = HttpServer.class.getDeclaredMethod("getWeatherInfo");
        method.setAccessible(true);
        String result = (String) method.invoke(server);
        assertTrue(result.contains("\"city\": \"Bogotá\""));
        assertTrue(result.contains("\"temperature\": \"18°C\""));
        assertTrue(result.contains("\"message\": \"Datos simulados del clima\""));
    }

    @Test
    void testGetRandomQuoteReturnsJsonWithContentAndAuthor() throws Exception {
        HttpServer server = new HttpServer();
        Method method = HttpServer.class.getDeclaredMethod("getRandomQuote");
        method.setAccessible(true);
        String result = (String) method.invoke(server);
        assertTrue(result.contains("\"content\":"));
        assertTrue(result.contains("\"author\":"));
        assertTrue(result.contains("\"message\": \"Cita inspiradora del día\""));
    }

    @Test
    void testHandleHelloServiceGetWithNameParameter() throws Exception {
        HttpServer server = new HttpServer();
        Method method = HttpServer.class.getDeclaredMethod("handleHelloServiceGet", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(server, "/api/hello?name=Diego");
        assertTrue(result.contains("\"message\": \"Hello, Diego! (GET)\""));
    }

    @Test
    void testHandleHelloServiceGetWithoutNameParameter() throws Exception {
        HttpServer server = new HttpServer();
        Method method = HttpServer.class.getDeclaredMethod("handleHelloServiceGet", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(server, "/api/hello");
        assertTrue(result.contains("\"message\": \"Hello, World! (GET)\""));
    }

    @Test
    void testHandleHelloServicePostWithNameParameter() throws Exception {
        HttpServer server = new HttpServer();
        Method method = HttpServer.class.getDeclaredMethod("handleHelloServicePost", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(server, "name=Diego");
        assertTrue(result.contains("\"message\": \"Hello, Diego! (POST)\""));
    }

    @Test
    void testHandleHelloServicePostWithoutNameParameter() throws Exception {
        HttpServer server = new HttpServer();
        Method method = HttpServer.class.getDeclaredMethod("handleHelloServicePost", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(server, "");
        assertTrue(result.contains("\"message\": \"Hello, World! (POST)\""));
    }

    @Test
    void testGetSimpleMimeTypeHtml() throws Exception {
        HttpServer server = new HttpServer();
        Method method = HttpServer.class.getDeclaredMethod("getSimpleMimeType", String.class);
        method.setAccessible(true);
        String mime = (String) method.invoke(server, "index.html");
        assertEquals("text/html", mime);
    }

    @Test
    void testGetSimpleMimeTypeUnknownExtension() throws Exception {
        HttpServer server = new HttpServer();
        Method method = HttpServer.class.getDeclaredMethod("getSimpleMimeType", String.class);
        method.setAccessible(true);
        String mime = (String) method.invoke(server, "file.unknown");
        assertEquals("application/octet-stream", mime);
    }

    @Test
    void testGetStatusMessageKnownCodes() throws Exception {
        HttpServer server = new HttpServer();
        Method method = HttpServer.class.getDeclaredMethod("getStatusMessage", int.class);
        method.setAccessible(true);
        assertEquals("OK", method.invoke(server, 200));
        assertEquals("Bad Request", method.invoke(server, 400));
        assertEquals("Not Found", method.invoke(server, 404));
        assertEquals("Internal Server Error", method.invoke(server, 500));
    }

    @Test
    void testGetStatusMessageUnknownCode() throws Exception {
        HttpServer server = new HttpServer();
        Method method = HttpServer.class.getDeclaredMethod("getStatusMessage", int.class);
        method.setAccessible(true);
        assertEquals("Unknown", method.invoke(server, 123));
    }
}