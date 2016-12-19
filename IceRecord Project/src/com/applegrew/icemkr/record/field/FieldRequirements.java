package com.applegrew.icemkr.record.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.applegrew.icemkr.record.field.FieldType.ScalarType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface FieldRequirements {
    int minLength() default 0;

    ScalarType scalarType();
}
