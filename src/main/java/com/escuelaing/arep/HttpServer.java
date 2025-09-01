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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.escuelaing.arep.config.ServerConfig;
import com.escuelaing.arep.framework.RouteHandler;
import com.escuelaing.arep.http.Request;
import com.escuelaing.arep.http.Response;

/**
 * HTTP Server that handles multiple sequential requests and serves static
 * files. Supports HTML, CSS, JavaScript, images, and simple REST API endpoints.
 *
 * @author Diego Cardenas
 * @version 1.0
 */
public class HttpServer {

    private static boolean running = true;
    private static String WEB_ROOT = System.getProperty("user.dir") + "/target/classes/" + ServerConfig.STATIC_FILES_DIR;
    private static final Logger LOGGER = Logger.getLogger(HttpServer.class.getName());

    private static final Map<String, byte[]> fileCache = new HashMap<>();

    /**
     * The entry point of the application. Initializes an instance of
     * {@link HttpServer} and starts the server.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException if an I/O error occurs when starting the server.
     */
    public static void main(String[] args) throws IOException {
        if(args.length > 0) {
            WEB_ROOT = System.getProperty("user.dir") + "/" + args[0];
        }
        HttpServer server = new HttpServer();
        server.start();
    }

    /**
     * Starts the HTTP server and listens for incoming client connections.
     * <p>
     * The server binds to the specified port and serves files from the
     * configured web root directory. It logs server status and connection
     * information. For each client connection, it delegates request handling to
     * the {@code handleRequest(Socket clientSocket)} method.
     * <p>
     * The server runs in a loop while {@code running} is {@code true}. If an
     * error occurs while accepting or handling a client connection, it logs the
     * error. When the server is stopped, it logs the shutdown and calls
     * {@code stop()} to perform cleanup.
     *
     * @throws IOException if an I/O error occurs when opening the server
     *                     socket.
     */
    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(ServerConfig.getPort())) {
            LOGGER.log(Level.INFO, "HTTP Server started on port {0}", ServerConfig.getPort());
            LOGGER.log(Level.INFO, "Serving files from: {0}", WEB_ROOT);
            LOGGER.log(Level.INFO, "Open http://localhost:{0} in your browser", ServerConfig.getPort());

            while (running) {
                LOGGER.log(Level.INFO, "Waiting for client connection...");

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

    /**
     * Stops the HTTP server by setting the running flag to false. This method
     * should be called to gracefully shut down the server loop.
     */
    private void stop() {
        running = false;
    }

    /**
     * Updates the static files directory path.
     * This method is called by the framework to set a custom static files location.
     * 
     * @param directory the new static files directory path
     */
    public static void setStaticFilesDirectory(String directory) {
        if (directory.startsWith("/")) {
            // For resources in classpath, use target/classes
            WEB_ROOT = System.getProperty("user.dir") + "/target/classes" + directory;
        } else {
            WEB_ROOT = System.getProperty("user.dir") + "/" + directory;
        }
        LOGGER.log(Level.INFO, "Static files directory updated to: {0}", WEB_ROOT);
    }

    /**
     * Handles an incoming HTTP request from a client socket.
     * <p>
     * Reads the request line and headers, logs the request, and determines how
     * to process it:
     * <ul>
     * <li>If the path starts with "/api/", delegates to
     * {@code handleApiRequest}.</li>
     * <li>If the path is "/" or empty, serves the default index file.</li>
     * <li>Otherwise, attempts to serve the requested file.</li>
     * </ul>
     * Sends a 400 Bad Request response if the request line is malformed.
     *
     * @param clientSocket the socket connected to the client
     * @throws IOException if an I/O error occurs while reading the request or
     *                     writing the response
     */
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
        String path = requestParts[1];

        // First check if this is a framework route
        RouteHandler handler = WebApp.getRoute(path);
        if (handler != null) {
            handleFrameworkRoute(out, method, path, headers, handler);
        } else if (path.startsWith("/api/")) {
            handleApiRequest(out, method, path);
        } else if (path.equals("/") || path.isEmpty()) {
            serveFile(out, "/index.html");
        } else {
            serveFile(out, path);
        }
    }

    /**
     * Handles framework routes using the registered RouteHandler.
     * Creates Request and Response objects and delegates to the route handler.
     * 
     * @param out the OutputStream to write the response to
     * @param method the HTTP method of the request
     * @param path the request path with query parameters
     * @param headers the request headers
     * @param handler the RouteHandler for this route
     * @throws IOException if an I/O error occurs while handling the request
     */
    private void handleFrameworkRoute(OutputStream out, String method, String path, 
                                    Map<String, String> headers, RouteHandler handler) throws IOException {
        try {
            Request request = new Request(method, path, headers);
            Response response = new Response();
            
            String result = handler.handle(request, response);
            
            // Send the response with the content type from the Response object
            sendResponse(out, response.getStatusCode(), response.getContentType(), 
                        result.getBytes(StandardCharsets.UTF_8));
            
            LOGGER.log(Level.INFO, "Framework route handled: {0}", path);
            
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error handling framework route: {0}", e.getMessage());
            sendErrorResponse(out, 500, "Internal Server Error");
        }
    }

    /**
     * Handles API requests by determining the endpoint from the path and
     * generating the appropriate response. Supports the following endpoints:
     * <ul>
     * <li><b>/api/weather</b>: Returns weather information in JSON format.</li>
     * <li><b>/api/quote</b>: Returns a random quote in JSON format.</li>
     * <li><b>/api/hello</b>: Returns a greeting message in JSON format.</li>
     * </ul>
     * If the endpoint is not found, sends a 404 error response. In case of
     * internal errors, sends a 500 error response.
     *
     * @param out    the OutputStream to write the response to
     * @param method the HTTP method of the request (e.g., GET, POST)
     * @param path   the request path indicating the API endpoint
     * @throws IOException if an I/O error occurs while handling the request
     */
    private void handleApiRequest(OutputStream out, String method, String path) throws IOException {
        try {
            String response;
            String contentType = "application/json";

            if (path.startsWith("/api/weather")) {
                response = getWeatherInfo();
            } else if (path.startsWith("/api/quote")) {
                response = getRandomQuote();
            } else if (path.startsWith("/api/hello")) {
                switch (method) {
                    case "GET" ->
                        response = handleHelloServiceGet(path);
                    case "POST" ->
                        response = handleHelloServicePost(path);
                    default -> {
                        sendErrorResponse(out, 405, "Method Not Allowed");
                        return;
                    }
                }
            } else {
                sendErrorResponse(out, 404, "API endpoint not found");
                return;
            }

            sendResponse(out, 200, contentType, response.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error handling API request: {0}", e.getMessage());
            sendErrorResponse(out, 500, "Internal Server Error");
        }
    }

    /**
     * Generates a simulated weather information JSON string for Bogotá.
     * <p>
     * The returned JSON includes the following fields:
     * <ul>
     * <li>city: Name of the city ("Bogotá")</li>
     * <li>temperature: Simulated temperature ("18°C")</li>
     * <li>description: Weather description ("Parcialmente nublado")</li>
     * <li>humidity: Simulated humidity ("65%")</li>
     * <li>timestamp: Current timestamp in ISO-8601 format</li>
     * <li>message: Informational message indicating simulated data</li>
     * </ul>
     *
     * @return A JSON string containing simulated weather data for Bogotá.
     */
    private String getWeatherInfo() {
        return String.format("""
                {
                  "city": "Bogotá",
                  "temperature": "18°C",
                  "description": "Parcialmente nublado",
                  "humidity": "65%%",
                  "timestamp": "%s",
                  "message": "Datos simulados del clima"
                }
                """, java.time.Instant.now().toString());
    }

    /**
     * Generates a random inspirational quote in JSON format.
     * <p>
     * The quote consists of a content, author, current timestamp, and a fixed
     * message. If the selected quote does not specify an author, "Anónimo" is
     * used as the default.
     *
     * @return a JSON string containing the quote, author, timestamp, and
     *         message.
     */
    private String getRandomQuote() {
        String[] quotes = {
                "La única forma de hacer un gran trabajo es amar lo que haces. - Steve Jobs",
                "La vida es lo que pasa mientras estás ocupado haciendo otros planes. - John Lennon",
                "El futuro pertenece a quienes creen en la belleza de sus sueños. - Eleanor Roosevelt",
                "No es lo que nos pasa, sino cómo reaccionamos lo que importa. - Epicteto"
        };

        int randomIndex = (int) (Math.random() * quotes.length);
        String selectedQuote = quotes[randomIndex];
        String[] parts = selectedQuote.split(" - ");

        return String.format("""
                {
                  "content": "%s",
                  "author": "%s",
                  "timestamp": "%s",
                  "message": "Cita inspiradora del día"
                }
                """, parts[0], parts.length > 1 ? parts[1] : "Anónimo", java.time.Instant.now().toString());
    }

    private String handleHelloServicePost(String body) {
        String name = "World";

        if (body != null && body.startsWith("name=")) {
            try {
                name = URLDecoder.decode(body.substring(5), StandardCharsets.UTF_8);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error decoding name parameter: {0}", e.getMessage());
            }
        }

        return String.format("""
                {
                  "message": "Hello, %s! (POST)",
                  "timestamp": "%s",
                  "service": "HttpServer REST API"
                }
                """, name, java.time.Instant.now().toString());
    }

    private String handleHelloServiceGet(String path) {
        String name = "World";

        if (path.contains("?")) {
            String query = path.split("\\?", 2)[1];
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2 && "name".equals(keyValue[0])) {
                    try {
                        name = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Error decoding name parameter: {0}", e.getMessage());
                    }
                }
            }
        }

        return String.format("""
                {
                  "message": "Hello, %s! (GET)",
                  "timestamp": "%s",
                  "service": "HttpServer REST API"
                }
                """, name, java.time.Instant.now().toString());
    }

    /**
     * Serves a static file to the client.
     * <p>
     * The method sanitizes the requested path to prevent directory traversal
     * attacks, constructs the full file path, and checks if the file exists. If
     * the file is found, it reads its content (using a cache for files smaller
     * than 1MB) and sends it to the client with the appropriate MIME type. If
     * the file is not found or an error occurs, it sends an appropriate HTTP
     * error response.
     *
     * @param out  the OutputStream to write the response to
     * @param path the requested file path
     * @throws IOException if an I/O error occurs while reading the file or
     *                     writing the response
     */
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

    /**
     * A simple method to determine the MIME type based on file extension. This
     * method covers common file types and defaults to
     * "application/octet-stream".
     *
     * @param fileName The name of the file
     * @return The MIME type as a String
     */
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

    /**
     * Sends an HTTP response to the client.
     *
     * @param out         the OutputStream to write the response to
     * @param statusCode  contains the HTTP status code to send (e.g., 200, 404)
     * @param contentType the MIME type of the response content (e.g.,
     *                    "text/html", "application/json")
     * @param content     the byte array of the response body
     * @throws IOException if an I/O error occurs while writing the response
     */
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

    /**
     * Sends an HTTP error response to the client with a formatted HTML error
     * page.
     *
     * @param out        the OutputStream to write the response to
     * @param statusCode the HTTP status code to send (e.g., 404, 500)
     * @param message    the error message to display in the response body
     * @throws IOException if an I/O error occurs while writing the response
     */
    private void sendErrorResponse(OutputStream out, int statusCode, String message) throws IOException {
        String errorHtml = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Error %d</title>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; margin: 40px; }
                        .error { color: #d32f2f; }
                        .code { background: #f5f5f5; padding: 20px; border-radius: 5px; }
                    </style>
                </head>
                <body>
                    <h1 class="error">Error %d</h1>
                    <div class="code">
                        <p><strong>Message:</strong> %s</p>
                        <p><strong>Server:</strong> HttpServer/1.0</p>
                    </div>
                </body>
                </html>
                """, statusCode, statusCode, message);

        sendResponse(out, statusCode, "text/html", errorHtml.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns the HTTP status message corresponding to the provided status
     * code.
     *
     * @param statusCode the HTTP status code (e.g., 200, 400, 404, 500)
     * @return the status message as a String ("OK", "Bad Request", "Not Found",
     *         "Internal Server Error", or "Unknown" for unrecognized codes)
     */
    private String getStatusMessage(int statusCode) {
        return switch (statusCode) {
            case 200 ->
                "OK";
            case 400 ->
                "Bad Request";
            case 404 ->
                "Not Found";
            case 500 ->
                "Internal Server Error";
            default ->
                "Unknown";
        };
    }
}
