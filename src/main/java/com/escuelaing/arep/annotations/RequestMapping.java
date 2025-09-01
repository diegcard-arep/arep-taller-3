package com.escuelaing.arep.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map HTTP requests to handler classes.
 * <p>
 * This annotation can be applied to a class to specify the base URI path
 * for request handling. The default value is "/", which maps to the root path.
 * </p>
 *
 * Example usage:
 * <pre>
 * &#64;RequestMapping("/api")
 * public class ApiController { ... }
 * </pre>
 *
 * @author Diego Cardenas
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestMapping {
    String value() default "/";
}