package com.escuelaing.arep.framework;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple router registry to centralize route storage
 */
public class Router {
    private static final Map<String, RouteHandler> ROUTES = new ConcurrentHashMap<>();

    public static void register(String path, RouteHandler handler) {
        ROUTES.put(path, handler);
    }

    public static RouteHandler get(String path) {
        return ROUTES.get(path);
    }

    public static Map<String, RouteHandler> all() {
        return Map.copyOf(ROUTES);
    }

    public static void clear() {
        ROUTES.clear();
    }
}
