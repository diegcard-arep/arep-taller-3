package com.escuelaing.arep;

import com.escuelaing.arep.annotations.RestController;
import com.escuelaing.arep.annotations.GetMapping;
import java.lang.reflect.Method;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MicroSpringBoot {
    private static final int PORT = 8080;
    private static Map<String, RouteInfo> routes = new ConcurrentHashMap<>();
    
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            // Modo específico: cargar una clase específica
            String controllerClassName = args[0];
            loadSpecificController(controllerClassName);
        } else {
            // Modo automático: escanear el classpath
            loadAllControllers();
        }
        
        startServer();
    }
    
    private static void loadSpecificController(String className) throws Exception {
        Class<?> controllerClass = Class.forName(className);
        if (!controllerClass.isAnnotationPresent(RestController.class)) {
            System.out.println("La clase " + className + " no tiene la anotación @RestController");
            return;
        }
        registerController(controllerClass);
    }
    
    private static void loadAllControllers() {
        List<Class<?>> controllers = ClassScanner.findRestControllers("com.escuelaing.arep");
        for (Class<?> controllerClass : controllers) {
            try {
                registerController(controllerClass);
            } catch (Exception e) {
                System.err.println("Error registrando controlador " + controllerClass.getName() + ": " + e.getMessage());
            }
        }
    }
    
    private static void registerController(Class<?> controllerClass) throws Exception {
        Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
        
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                String path = mapping.value();
                routes.put(path, new RouteInfo(path, method, controllerInstance));
                System.out.println("Servicio GET registrado en URI: " + path);
            }
        }
    }
    
    private static void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado en puerto " + PORT);
        System.out.println("Rutas registradas:");
        for (String path : routes.keySet()) {
            System.out.println("  GET " + path);
        }
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleRequest(clientSocket);
        }
    }
    
    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            String requestLine = in.readLine();
            if (requestLine == null) return;
            
            System.out.println("Request: " + requestLine);
            
            String[] parts = requestLine.split(" ");
            if (parts.length >= 2 && "GET".equals(parts[0])) {
                String fullPath = parts[1];
                String path = fullPath.contains("?") ? fullPath.substring(0, fullPath.indexOf("?")) : fullPath;
                Map<String, String> queryParams = parseQueryParams(fullPath);
                
                if (routes.containsKey(path)) {
                    try {
                        RouteInfo route = routes.get(path);
                        String response = route.invoke(queryParams);
                        sendResponse(out, 200, "OK", response);
                    } catch (Exception e) {
                        sendResponse(out, 500, "Internal Server Error", "Error: " + e.getMessage());
                    }
                } else if (path.equals("/")) {
                    sendResponse(out, 200, "OK", getWelcomePage());
                } else {
                    sendResponse(out, 404, "Not Found", "Ruta no encontrada: " + path);
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static Map<String, String> parseQueryParams(String fullPath) {
        Map<String, String> params = new HashMap<>();
        if (fullPath.contains("?")) {
            String queryString = fullPath.substring(fullPath.indexOf("?") + 1);
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    try {
                        params.put(URLDecoder.decode(keyValue[0], "UTF-8"), 
                                 URLDecoder.decode(keyValue[1], "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        params.put(keyValue[0], keyValue[1]);
                    }
                }
            }
        }
        return params;
    }
    
    private static void sendResponse(PrintWriter out, int statusCode, String statusText, String body) {
        out.println("HTTP/1.1 " + statusCode + " " + statusText);
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println("Content-Length: " + body.getBytes().length);
        out.println();
        out.println(body);
    }
    
    private static String getWelcomePage() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>MicroSpringBoot Server</title></head>");
        html.append("<body>");
        html.append("<h1>MicroSpringBoot Server</h1>");
        html.append("<h2>Rutas disponibles:</h2>");
        html.append("<ul>");
        for (String path : routes.keySet()) {
            html.append("<li><a href=\"").append(path).append("\">GET ").append(path).append("</a></li>");
        }
        html.append("</ul>");
        html.append("</body></html>");
        return html.toString();
    }
}
