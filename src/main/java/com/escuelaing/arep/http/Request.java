package com.escuelaing.arep.http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper class for HTTP request data.
 * Provides access to query parameters and request information.
 * 
 * @author Diego Cardenas
 * @version 1.0
 */
public class Request {
    
    private static final Logger LOGGER = Logger.getLogger(Request.class.getName());
    
    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    
    /**
     * Constructs a Request object from HTTP request components.
     * 
     * @param method the HTTP method (GET, POST, etc.)
     * @param path the request path including query string
     * @param headers the HTTP headers map
     */
    public Request(String method, String path, Map<String, String> headers) {
        this.method = method;
        this.headers = headers != null ? new HashMap<>(headers) : new HashMap<>();
        this.queryParams = new HashMap<>();
        
        if (path.contains("?")) {
            String[] pathParts = path.split("\\?", 2);
            this.path = pathParts[0];
            parseQueryParameters(pathParts[1]);
        } else {
            this.path = path;
        }
    }
    
    /**
     * Parses query parameters from the query string.
     * 
     * @param queryString the URL query string (after the '?' character)
     */
    private void parseQueryParameters(String queryString) {
        if (queryString != null && !queryString.isEmpty()) {
            String[] params = queryString.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2) {
                    try {
                        String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                        String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        queryParams.put(key, value);
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Error decoding query parameter: {0}", e.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * Gets the value of a query parameter by name.
     * 
     * @param name the parameter name
     * @return the parameter value, or empty string if not found
     */
    public String getValues(String name) {
        return queryParams.getOrDefault(name, "");
    }
    
    /**
     * Gets all query parameters as a map.
     * 
     * @return a map of parameter names to values
     */
    public Map<String, String> getQueryParams() {
        return new HashMap<>(queryParams);
    }
    
    /**
     * Gets the HTTP method of the request.
     * 
     * @return the HTTP method (GET, POST, etc.)
     */
    public String getMethod() {
        return method;
    }
    
    /**
     * Gets the request path (without query parameters).
     * 
     * @return the request path
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Gets a header value by name.
     * 
     * @param name the header name (case-insensitive)
     * @return the header value, or null if not found
     */
    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }
    
    /**
     * Gets all headers as a map.
     * 
     * @return a map of header names to values
     */
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }
}
