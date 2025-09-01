package com.escuelaing.arep.config;

/**
 * The {@code ServerConfig} class provides configuration settings for the server,
 * including the port number and the directory for static files.
 * <p>
 * It allows getting and setting the server port, and defines the location of static resources.
 * </p>
 *
 * <ul>
 *   <li>{@code PORT}: The port number on which the server listens.</li>
 *   <li>{@code STATIC_FILES_DIR}: The directory containing static files to be served.</li>
 * </ul>
 *
 * <p>
 * Usage example:
 * <pre>
 *   int port = ServerConfig.getPort();
 *   ServerConfig.setPort(8080);
 * </pre>
 * </p>
 * 
 * @author Diego Cardenas
 * @since 2.0
 */
public class ServerConfig {
    private static int PORT = 35000;
    public static final String STATIC_FILES_DIR = "static";
    
    /**
     * Gets the current server port.
     * 
     * @return the port number
     */
    public static int getPort() {
        return PORT;
    }
    
    /**
     * Sets the server port.
     * 
     * @param port the port number to set
     */
    public static void setPort(int port) {
        PORT = port;
    }
}
