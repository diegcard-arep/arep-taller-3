package com.escuelaing.arep;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.escuelaing.arep.framework.RouteHandler;
import com.escuelaing.arep.framework.Router;

/**
 * Main framework class that provides static methods for defining REST services
 * and configuring static file locations. This class serves as the main entry
 * point for developing web applications using the framework.
 * 
 * @author Diego Cardenas
 * @version 1.0
 */
public class WebApp {
    
    private static final Logger LOGGER = Logger.getLogger(WebApp.class.getName());
    
    // Mantener por compatibilidad, pero delegar al Router
    private static final Map<String, RouteHandler> routes = new HashMap<>();
    private static String staticFilesDirectory = "/webroot";
    private static HttpServer server;
    private static final String DEFAULT_ROUTE_PREFIX = "/App";
    
    /**
     * Registers a GET route with the specified path and handler.
     * The handler will be called when a GET request matches the path.
     * 
     * @param path the route path (e.g., "/hello", "/pi")
     * @param handler the lambda function to handle the request
     */
    public static void get(String path, RouteHandler handler) {
        String fullPath = DEFAULT_ROUTE_PREFIX + path;
        routes.put(fullPath, handler);
        Router.register(fullPath, handler);
        LOGGER.log(Level.INFO, "Registered GET route: {0}", fullPath);
    }
    
    /**
     * Sets the directory where static files are located.
     * The framework will serve static files from this directory.
     * 
     * @param directory the path to the static files directory (relative to classpath)
     */
    public static void staticfiles(String directory) {
        staticFilesDirectory = directory;
        HttpServer.setStaticFilesDirectory(directory);
        LOGGER.log(Level.INFO, "Static files directory set to: {0}", directory);
    }
    
    /**
     * Gets the registered route handler for the specified path.
     * 
     * @param path the request path
     * @return the RouteHandler for the path, or null if not found
     */
    public static RouteHandler getRoute(String path) {
        RouteHandler handler = Router.get(path);
        return handler != null ? handler : routes.get(path);
    }
    
    /**
     * Gets all registered routes.
     * 
     * @return a map of paths to route handlers
     */
    public static Map<String, RouteHandler> getAllRoutes() {
        return new HashMap<>(routes);
    }
    
    /**
     * Gets the current static files directory.
     * 
     * @return the static files directory path
     */
    public static String getStaticFilesDirectory() {
        return staticFilesDirectory;
    }
    
    /**
     * Starts the web application server.
     * This method initializes and starts the HTTP server with the configured
     * routes and static files directory.
     * 
     * @throws IOException if an error occurs starting the server
     */
    public static void start() throws IOException {
        if (server == null) {
            server = new HttpServer();
        }
        LOGGER.log(Level.INFO, "Starting WebApp framework with {0} registered routes", routes.size());
        server.start();
    }
    
    /**
     * Stops the web application server.
     */
    public static void stop() {
        if (server != null) {
            LOGGER.log(Level.INFO, "Stopping WebApp framework");
        }
    }
    
    /**
     * Clears all registered routes. Useful for testing.
     */
    public static void clearRoutes() {
        routes.clear();
        Router.clear();
        LOGGER.log(Level.INFO, "All routes cleared");
    }
}