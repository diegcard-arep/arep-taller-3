package com.escuelaing.arep.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class for HTTP response data.
 * Provides methods to configure response headers and status.
 * 
 * @author Diego Cardenas
 * @version 1.0
 */
public class Response {
    
    private int statusCode = 200;
    private String contentType = "text/plain";
    private final Map<String, String> headers;
    
    /**
     * Constructs a new Response object with default values.
     */
    public Response() {
        this.headers = new HashMap<>();
    }
    
    /**
     * Sets the HTTP status code for the response.
     * 
     * @param statusCode the HTTP status code (e.g., 200, 404, 500)
     * @return this Response object for method chaining
     */
    public Response status(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }
    
    /**
     * Sets the content type for the response.
     * 
     * @param contentType the MIME type (e.g., "text/html", "application/json")
     * @return this Response object for method chaining
     */
    public Response type(String contentType) {
        this.contentType = contentType;
        return this;
    }
    
    /**
     * Sets a response header.
     * 
     * @param name the header name
     * @param value the header value
     * @return this Response object for method chaining
     */
    public Response header(String name, String value) {
        headers.put(name, value);
        return this;
    }
    
    /**
     * Gets the current status code.
     * 
     * @return the HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     * Gets the current content type.
     * 
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Gets all response headers.
     * 
     * @return a map of header names to values
     */
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }
}
