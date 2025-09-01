package com.escuelaing.arep;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class HttpServerTest {

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