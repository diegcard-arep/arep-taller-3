package com.escuelaing.arep.config;

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
