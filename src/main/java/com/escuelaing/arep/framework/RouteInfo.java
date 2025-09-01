package com.escuelaing.arep.framework;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class RouteInfo {
    private final String path;
    private final Method method;
    private final Object controllerInstance;
    private final Parameter[] parameters;
    
    public RouteInfo(String path, Method method, Object controllerInstance) {
        this.path = path;
        this.method = method;
        this.controllerInstance = controllerInstance;
        this.parameters = method.getParameters();
    }
    
    public String getPath() {
        return path;
    }
    
    public Method getMethod() {
        return method;
    }
    
    public Object getControllerInstance() {
        return controllerInstance;
    }
    
    public Parameter[] getParameters() {
        return parameters;
    }
    
    public String invoke(Map<String, String> queryParams) throws Exception {
        Object[] args = new Object[parameters.length];
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (param.isAnnotationPresent(com.escuelaing.arep.annotations.RequestParam.class)) {
                com.escuelaing.arep.annotations.RequestParam requestParam = 
                    param.getAnnotation(com.escuelaing.arep.annotations.RequestParam.class);
                String paramName = requestParam.value();
                String defaultValue = requestParam.defaultValue();
                
                String value = queryParams.get(paramName);
                if (value == null && !defaultValue.isEmpty()) {
                    value = defaultValue;
                }
                args[i] = value != null ? value : "";
            }
        }
        
        Object result = method.invoke(controllerInstance, args);
        return result != null ? result.toString() : "";
    }
}
