package com.escuelaing.arep;

import com.escuelaing.arep.annotations.RestController;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {
    
    public static List<Class<?>> findRestControllers(String packageName) {
        List<Class<?>> controllers = new ArrayList<>();
        try {
            String path = packageName.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(path);
            
            if (resource != null) {
                File directory = new File(resource.getFile());
                if (directory.exists()) {
                    scanDirectory(directory, packageName, controllers);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    }
                }
            }
        }
    }
}
