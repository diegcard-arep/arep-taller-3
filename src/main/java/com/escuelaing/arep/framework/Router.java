package com.escuelaing.arep.framework;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @deprecated Ya no se utiliza. El servidor carga rutas mediante anotaciones
 * (@RestController, @GetMapping). Mantener solo por compatibilidad binaria.
 */
@Deprecated
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
