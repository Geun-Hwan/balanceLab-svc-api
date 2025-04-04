package gugunan.balanceLab.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.annotations.IdGeneratorType;

import gugunan.balanceLab.support.IdGenerator;

@IdGeneratorType(IdGenerator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomGeneratedId {
    String method();

}
