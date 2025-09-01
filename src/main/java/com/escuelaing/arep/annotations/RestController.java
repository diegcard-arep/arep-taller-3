package com.escuelaing.arep.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated class is a "Rest Controller".
 * <p>
 * Classes annotated with {@code @RestController} are recognized as controllers
 * that handle RESTful web requests in the application.
 * </p>
 *
 * <p>
 * This annotation is retained at runtime and can be used for reflection-based
 * processing.
 * </p>
 * 
 * @author Diego Cardenas
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RestController {
}
