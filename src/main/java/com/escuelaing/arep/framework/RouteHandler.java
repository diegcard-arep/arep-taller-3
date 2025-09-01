package com.escuelaing.arep.framework;

import com.escuelaing.arep.http.Request;
import com.escuelaing.arep.http.Response;

/**
 * Functional interface for handling HTTP route requests.
 * Allows the use of lambda expressions for defining REST service handlers.
 * 
 * @author Diego Cardenas
 * @version 1.0
 */
@FunctionalInterface
public interface RouteHandler {
    
    /**
     * Handles an HTTP request and generates a response.
     * 
     * @param req the HTTP request wrapper containing request data and query parameters
     * @param resp the HTTP response wrapper for response handling
     * @return the response content as a String
     */
    String handle(Request req, Response resp);
}
