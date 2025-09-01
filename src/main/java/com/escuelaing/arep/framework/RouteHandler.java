package com.escuelaing.arep.framework;

import com.escuelaing.arep.http.Request;
import com.escuelaing.arep.http.Response;

/**
 * @deprecated Reemplazado por rutas anotadas y {@link RouteInfo}.
 */
@Deprecated
@FunctionalInterface
public interface RouteHandler {
    String handle(Request req, Response resp);
}
