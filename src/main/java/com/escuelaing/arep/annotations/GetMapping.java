package com.escuelaing.arep.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method as a handler for HTTP GET requests.
 * The {@code value} element specifies the path to be mapped.
 * 
 * Usage example:
 * <pre>
 * {@literal @}GetMapping("/example")
 * public String handleExample() {
 *     // handler code
 * }
 * </pre>
 * 
 * @author Diego Cardenas
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {
    String value() default "/";
}
