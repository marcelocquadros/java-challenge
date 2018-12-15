package br.com.comexport.javachallenge.utils;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validator to use with dates represented by only digits
 * Eg. 20181201
 */

@Documented
@Constraint(validatedBy = DateIntegerConstraintValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateIntegerValidator {
    String message() default "Data com formato inválido";
    String pattern() default "yyyyMMdd";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}