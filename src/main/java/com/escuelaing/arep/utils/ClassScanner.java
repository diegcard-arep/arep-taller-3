package com.escuelaing.arep.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.escuelaing.arep.annotations.RestController;

/**
 * Utility class for scanning a given package to find classes annotated with {@code RestController}.
 * <p>
 * This class provides methods to search for REST controller classes within a specified package
 * by inspecting the file system. Note: Scanning inside JAR files is not implemented for simplicity.
 * </p>
 *
 * <p>
 * Usage example:
 * <pre>
 *     List<Class<?>> controllers = ClassScanner.findRestControllers("com.example.controllers");
 * </pre>
 * </p>
 *
 * <p>
 * Logging is performed using {@link java.util.logging.Logger} for warnings and fine-grained messages.
 * </p>
 *
 * @author Diego Cardenas
 * @version 1.0
 */
public class ClassScanner {
    
    private static final Logger LOGGER = Logger.getLogger(ClassScanner.class.getName());

    public static List<Class<?>> findRestControllers(String packageName) {
        List<Class<?>> controllers = new ArrayList<>();
        try {
            String path = packageName.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if ("file".equals(resource.getProtocol())) {
                    File directory = new File(decode(resource.getFile()));
                    if (directory.exists()) {
                        scanDirectory(directory, packageName, controllers);
                    }
                }
                // Nota: escaneo dentro de JARs no implementado por simplicidad
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error leyendo recursos para paquete {0}: {1}", new Object[]{packageName, e.getMessage()});
        }
        return controllers;
    }
    
    private static void scanDirectory(File directory, String packageName, List<Class<?>> controllers) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(file, packageName + "." + file.getName(), controllers);
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(RestController.class)) {
                            controllers.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        // Ignorar clases que no se pueden cargar
                        LOGGER.log(Level.FINE, "Clase no encontrada durante el escaneo: {0}", className);
                    }
                }
            }
        }
    }

    private static String decode(String path) throws UnsupportedEncodingException {
        return URLDecoder.decode(path, java.nio.charset.StandardCharsets.UTF_8);
    }
}
