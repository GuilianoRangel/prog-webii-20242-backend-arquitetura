package br.ueg.progweb2.arquitetura.model.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface Searchable {
    String label() default "";
    boolean listEntityValues() default false;
    boolean autoComplete() default false;
}
