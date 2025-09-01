package com.escuelaing.arep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.escuelaing.arep.config.ServerConfig;
import com.escuelaing.arep.framework.RouteInfo;

/**
 * HTTP Server secuencial con soporte de archivos estáticos y rutas anotadas
 * vía un mini IoC (@RestController + @GetMapping + @RequestParam).
 */
public class HttpServer {

    private static boolean running = true;
    private static String WEB_ROOT = System.getProperty("user.dir") + "/target/classes/" + ServerConfig.STATIC_FILES_DIR;
    private static final Logger LOGGER = Logger.getLogger(HttpServer.class.getName());

    private static final Map<String, byte[]> fileCache = new HashMap<>();
    // Rutas descubiertas por reflexión
    private static final Map<String, RouteInfo> routes = new HashMap<>();

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            WEB_ROOT = System.getProperty("user.dir") + "/" + args[0];
        }
        HttpServer server = new HttpServer();
        server.start();
    }

    public void start() throws IOException {
        // Cargar controladores anotados
        loadControllers();

        try (ServerSocket serverSocket = new ServerSocket(ServerConfig.getPort())) {
            LOGGER.log(Level.INFO, "HTTP Server started on port {0}", ServerConfig.getPort());
            LOGGER.log(Level.INFO, "Serving files from: {0}", WEB_ROOT);
            if (!routes.isEmpty()) {
                LOGGER.info("Rutas registradas por anotación:");
                for (String p : routes.keySet()) {
                    LOGGER.info("  GET " + p);
                }
            }
            LOGGER.log(Level.INFO, "Open http://localhost:{0} en su navegador", ServerConfig.getPort());

            while (running) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleRequest(clientSocket);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error handling client request: {0}", e.getMessage());
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not start server on port: {0}", ServerConfig.getPort());
            LOGGER.log(Level.SEVERE, "Error: {0}", e.getMessage());
        } finally {
            LOGGER.log(Level.INFO, "Server stopped.");
            stop();
        }
    }

    private void stop() {
        running = false;
    }

    public static void setStaticFilesDirectory(String directory) {
        if (directory.startsWith("/")) {
            WEB_ROOT = System.getProperty("user.dir") + "/target/classes" + directory;
        } else {
            WEB_ROOT = System.getProperty("user.dir") + "/" + directory;
        }
        LOGGER.log(Level.INFO, "Static files directory updated to: {0}", WEB_ROOT);
    }

    private void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream();

        String requestLine = in.readLine();
        if (requestLine == null || requestLine.trim().isEmpty()) {
            return;
        }

        LOGGER.log(Level.INFO, "Request: {0}", requestLine);

        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
            String[] parts = headerLine.split(": ", 2);
            if (parts.length == 2) {
                headers.put(parts[0].toLowerCase(), parts[1]);
            }
        }

        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 3) {
            sendErrorResponse(out, 400, "Bad Request");
            return;
        }

        String method = requestParts[0];
        String fullPath = requestParts[1];
        String path = fullPath.contains("?") ? fullPath.substring(0, fullPath.indexOf("?")) : fullPath;

        // 1) Rutas anotadas (@GetMapping)
        if ("GET".equals(method) && routes.containsKey(path)) {
            try {
                Map<String, String> queryParams = parseQueryParams(fullPath);
                String body = routes.get(path).invoke(queryParams);
                sendResponse(out, 200, "text/plain; charset=UTF-8", body.getBytes(StandardCharsets.UTF_8));
                return;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error invocando ruta {0}: {1}", new Object[]{path, e.getMessage()});
                sendErrorResponse(out, 500, "Internal Server Error");
                return;
            }
        }

        // 2) Archivos estáticos
        if (path.equals("/") || path.isEmpty()) {
            serveFile(out, "/index.html");
        } else {
            serveFile(out, path);
        }
    }

    private void loadControllers() {
        List<Class<?>> controllers = ClassScanner.findRestControllers("com.escuelaing.arep.controllers");
        for (Class<?> controllerClass : controllers) {
            try {
                Object instance = controllerClass.getDeclaredConstructor().newInstance();
                for (var method : controllerClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(com.escuelaing.arep.annotations.GetMapping.class)) {
                        String routePath = method.getAnnotation(com.escuelaing.arep.annotations.GetMapping.class).value();
                        routes.put(routePath, new RouteInfo(routePath, method, instance));
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se pudo registrar controlador {0}: {1}", new Object[]{controllerClass.getName(), e.getMessage()});
            }
        }
    }

    private Map<String, String> parseQueryParams(String fullPath) {
        Map<String, String> params = new HashMap<>();
        if (fullPath.contains("?")) {
            String queryString = fullPath.substring(fullPath.indexOf("?") + 1);
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    try {
                        params.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                                   URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        params.put(keyValue[0], keyValue[1]);
                    }
                }
            }
        }
        return params;
    }

    private void serveFile(OutputStream out, String path) throws IOException {
        path = path.replace("..", "").replace("//", "/");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        Path filePath = Paths.get(WEB_ROOT + path);

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            sendErrorResponse(out, 404, "File Not Found");
            return;
        }

        try {
            byte[] fileContent = fileCache.get(path);
            if (fileContent == null) {
                fileContent = Files.readAllBytes(filePath);
                if (fileContent.length < 1024 * 1024) {
                    fileCache.put(path, fileContent);
                }
            }

            String mimeType = getSimpleMimeType(filePath.getFileName().toString());
            sendResponse(out, 200, mimeType, fileContent);

            LOGGER.log(Level.INFO, "Served file: {0} ({1} bytes)", new Object[] { path, fileContent.length });

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading file: {0}", path);
            sendErrorResponse(out, 500, "Internal Server Error");
        }
    }

    private String getSimpleMimeType(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "application/octet-stream";
        }
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".html") || lowerFileName.endsWith(".htm")) {
            return "text/html";
        } else if (lowerFileName.endsWith(".css")) {
            return "text/css";
        } else if (lowerFileName.endsWith(".js")) {
            return "application/javascript";
        } else if (lowerFileName.endsWith(".png")) {
            return "image/png";
        } else if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFileName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFileName.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (lowerFileName.endsWith(".ico")) {
            return "image/x-icon";
        } else if (lowerFileName.endsWith(".txt")) {
            return "text/plain";
        } else {
            return "application/octet-stream";
        }
    }

    private void sendResponse(OutputStream out, int statusCode, String contentType, byte[] content) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        writer.print("HTTP/1.1 " + statusCode + " " + getStatusMessage(statusCode) + "\r\n");
        writer.print("Content-Type: " + contentType + "\r\n");
        writer.print("Content-Length: " + content.length + "\r\n");
        writer.print("Connection: close\r\n");
        writer.print("Access-Control-Allow-Origin: *\r\n");
        writer.print("Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n");
        writer.print("Access-Control-Allow-Headers: Content-Type\r\n");
        writer.print("\r\n");
        writer.flush();
        out.write(content);
        out.flush();
    }

    private void sendErrorResponse(OutputStream out, int statusCode, String message) throws IOException {
        String errorHtml = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Error %d</title>
                    <meta charset=\"UTF-8\">
                    <style>
                        body { font-family: Arial, sans-serif; margin: 40px; }
                        .error { color: #d32f2f; }
                        .code { background: #f5f5f5; padding: 20px; border-radius: 5px; }
                    </style>
                </head>
                <body>
                    <h1 class=\"error\">Error %d</h1>
                    <div class=\"code\">
                        <p><strong>Message:</strong> %s</p>
                        <p><strong>Server:</strong> HttpServer/1.0</p>
                    </div>
                </body>
                </html>
                """, statusCode, statusCode, message);
        sendResponse(out, statusCode, "text/html", errorHtml.getBytes(StandardCharsets.UTF_8));
    }

    private String getStatusMessage(int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }
}
