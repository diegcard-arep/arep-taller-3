package com.escuelaing.arep;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.escuelaing.arep.config.ServerConfig;

@Tag("integration")
@DisplayName("Pruebas de integración del HttpServer")
class HttpServerIntegrationTest {

    @Test
    @DisplayName("GET /hola y /greeting deben responder 200")
    void endpoints_should_respond200_when_called() throws Exception {
        // Arrange
        int port = pickFreePort();
        ServerConfig.setPort(port);
        setRunning(true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(HttpServerIntegrationTest::runServer);
        waitForServer(port);

        try {
            // Act
            String hola = httpGet("http://localhost:" + port + "/hola");
            String greet = httpGet("http://localhost:" + port + "/greeting?name=Ana");

            // Assert
            assertEquals("Greetings from MicroSpringBoot!", hola.trim());
            assertEquals("Hola Ana", greet.trim());
        } finally {
            stopServer(future, port);
            executor.shutdownNow();
        }
    }

    @Test
    @DisplayName("GET / debe servir index.html")
    void root_should_serve_indexHtml() throws Exception {
        // Arrange
        int port = pickFreePort();
        ServerConfig.setPort(port);
        setRunning(true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(HttpServerIntegrationTest::runServer);
        waitForServer(port);

        try {
            // Act
            String html = httpGet("http://localhost:" + port + "/");

            // Assert
            assertTrue(html.toLowerCase().contains("<!doctype html"), "Debe retornar HTML");
        } finally {
            stopServer(future, port);
            executor.shutdownNow();
        }
    }

    // Helpers
    private static void runServer() {
        try {
            new HttpServer().start();
        } catch (IOException e) {
            // ignore when cancelled
        }
    }

    private static String httpGet(String urlStr) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        HttpRequest req = HttpRequest.newBuilder(URI.create(urlStr))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }

    private static void waitForServer(int port) {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        long nextAttempt = 0L;
        while (System.nanoTime() < deadline) {
            long now = System.nanoTime();
            if (now >= nextAttempt) {
                try {
                    String s = httpGet("http://localhost:" + port + "/hola");
                    if (!s.isEmpty()) return;
                } catch (IOException | InterruptedException ignored) {
                }
                nextAttempt = now + TimeUnit.MILLISECONDS.toNanos(100);
            }
        }
        fail("El servidor no inició a tiempo en el puerto " + port);
    }

    private static int pickFreePort() throws IOException {
        try (java.net.ServerSocket socket = new java.net.ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static void stopServer(Future<?> future, int port) {
        try {
            // Colocar running=false mediante reflexión y provocar una conexión para desbloquear accept()
            Field running = HttpServer.class.getDeclaredField("running");
            running.setAccessible(true);
            running.setBoolean(null, false);
        } catch (ReflectiveOperationException ignored) { }

        try {
            // Golpe final para desbloquear el accept()
            httpGet("http://localhost:" + port + "/hola");
        } catch (IOException | InterruptedException ignored) { }
        
        future.cancel(true);
    }

    private static void setRunning(boolean value) throws ReflectiveOperationException {
        Field running = HttpServer.class.getDeclaredField("running");
        running.setAccessible(true);
        running.setBoolean(null, value);
    }
}
