package com.escuelaing.arep.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to bind a method parameter to a web request parameter.
 * <p>
 * The {@code value} element specifies the name of the request parameter to bind to.
 * The {@code defaultValue} element can be used to provide a default value if the request parameter is not present.
 * </p>
 * 
 * @author Diego Cardenas
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    String value();
    String defaultValue() default "";
}
